package yaplstack.ast;

import yaplstack.CodeSegment;
import yaplstack.Instruction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Generator implements AST.Visitor<Void> {
    interface TwoStageGenerator {
        Runnable generate(List<Instruction> instructions, Map<Object, Integer> labelToIndex);
    }

    private boolean asExpression;
    private boolean functionScope;
    private List<TwoStageGenerator> stagedInstructions = new ArrayList<>();

    private MetaFrame context;

    public Generator(boolean asExpression) {
        this(asExpression, false, new ArrayList<>(), createContext());
    }

    private static MetaFrame createContext() {
        MetaFrame context = new MetaFrame();
        // Exception handler at 0, and self at 1
        context.locals.add("exceptionHandler");
        context.locals.add("self");
        return context;
    }

    public void emitLoadExceptionHandler() {
        emit(Instruction.Factory.loadVar(0));
    }

    public void emitLoadSelf() {
        emit(Instruction.Factory.loadVar(1));
    }

    public Generator(boolean asExpression, boolean functionScope, List<TwoStageGenerator> stagedInstructions, MetaFrame context) {
        this.stagedInstructions = stagedInstructions;
        this.asExpression = asExpression;
        this.functionScope = functionScope;
        this.context = context;

    }

    private void mark(Object label) {
        emit((instructions, labelToIndex) -> {
            int index = instructions.size();
            labelToIndex.put(label, index);

            return () -> {
            };
        });
    }

    private void emitJump(Object label, Function<Integer, Instruction> instructionFunction) {
        emit((instructions, labelToIndex) -> {
            int instructionIndex = instructions.size();
            instructions.add(null);

            return () -> {
                int jumpIndex = labelToIndex.get(label);
                Instruction instruction = instructionFunction.apply(jumpIndex);
                instructions.set(instructionIndex, instruction);
            };
        });
    }

    private void emitJumpIfTrue(Object label) {
        emitJump(label, index -> Instruction.Factory.jumpIfTrue(index));
    }

    private void emitJump(Object label) {
        emit(Instruction.Factory.loadConst(true));
        emitJumpIfTrue(label);
    }

    @Override
    public Void visitProgram(AST code) {
        code.accept(this);
        emit(Instruction.Factory.halt);

        return null;
    }

    @Override
    public Void visitBlock(List<AST> code) {
        if(asExpression) {
            code.subList(0, code.size() - 1).forEach(x -> visitAsStatement(x));
            visitAsExpression(code.get(code.size() - 1));
        } else {
            code.forEach(x -> visitAsStatement(x));
        }

        return null;
    }

    private Instruction[] generate() {
        ArrayList<Instruction> instructions = new ArrayList<>();
        Hashtable<Object, Integer> labelToIndex = new Hashtable<>();

        stagedInstructions.stream()
            .map(x -> x.generate(instructions, labelToIndex))
            .collect(Collectors.toList())
            .forEach(x -> x.run());

        return instructions.stream().toArray(s -> new Instruction[s]);
    }

    @Override
    public Void visitSend(AST target, String name, List<AST> arguments) {
        boolean useSpecial = false;

        if(!useSpecial) {
            visitAsExpression(target);

            if (arguments.size() > 0) {
                arguments.forEach(x -> visitAsExpression(x));
                emit(Instruction.Factory.dupx(arguments.size()));
            } else {
                emit(Instruction.Factory.dup);
            }

            emit(Instruction.Factory.load(Selector.get(name, arguments.size())));

            emit(Instruction.Factory.pushCallFrame(1 + 1 + arguments.size()));

            if (!asExpression)
                emit(Instruction.Factory.pop);
        } else {
            visitAsExpression(target);
            arguments.forEach(x -> visitAsExpression(x));
            emit(Instruction.Factory.send(name, arguments.size()));

            // A pop could be avoided by having dual methods with similar selectors:
            // One of the selectors is used for methods as expression
            // The other selector is used for methods as statements
            if(!asExpression)
                emit(Instruction.Factory.pop);
        }

        return null;
    }

    @Override
    public Void visitObject(List<Slot> slots) {
        emit(Instruction.Factory.newEnvironment);

        HashSet<MetaFrame> dependentContexts = new HashSet<>();

        slots.forEach(x -> {
            x.accept(new Slot.Visitor() {
                @Override
                public void visitField(String name, AST value) {
                    emit(Instruction.Factory.dup);
                    visitAsExpression(value);
                    emit(Instruction.Factory.store(name));
                }

                @Override
                public void visitMethod(String name, List<String> parameters, AST body) {
                    emit(Instruction.Factory.dup);
                    visitMethodContent(parameters, body);
                    emit(Instruction.Factory.store(Selector.get(name, parameters.size())));
                }

                private void visitMethodContent(List<String> params, AST code) {
                    Generator bodyGenerator = new Generator(true);
                    bodyGenerator.functionScope = true;
                    bodyGenerator.context.locals.addAll(params);
                    bodyGenerator.context.context = context;
                    code.accept(bodyGenerator);
                    int variableCount = bodyGenerator.context.locals.size() - params.size();

                    Generator generator = new Generator(true);
                    generator.context.locals.addAll(params);

                    // Allocate variables
                    for (int i = 0; i < variableCount; i++)
                        generator.emit(Instruction.Factory.loadConst(null));

                    generator.emit(bodyGenerator);
                    generator.emit(Instruction.Factory.popCallFrame(1));

                    Instruction[] instructionArray = generator.generate();
                    int maxStackSize = maxStackSize(1 /*exception handler*/ + 1 /*self*/ + params.size(), instructionArray, 0);
                    CodeSegment codeSegment = new CodeSegment(maxStackSize, instructionArray, x);

                    emit(Instruction.Factory.loadConst(codeSegment));

                    if (bodyGenerator.context.isDependent)
                        dependentContexts.add(bodyGenerator.context);
                }
            });
        });

        if(dependentContexts.size() > 0) {
            emit(Instruction.Factory.dup);
            emit(Instruction.Factory.loadCallFrame);
            emit(Instruction.Factory.store("__frame__"));
        }

        return null;
    }

    @Override
    public Void visitTest(AST condition, AST ifTrue, AST ifFalse) {
        visitAsExpression(condition);

        Object labelIfTrue = new Object();
        Object labelEnd = new Object();

        emitJumpIfTrue(labelIfTrue);

        ifFalse.accept(this);
        emitJump(labelEnd);

        mark(labelIfTrue);
        ifTrue.accept(this);

        mark(labelEnd);

        return null;
    }

    @Override
    public Void visitLoop(AST condition, AST body) {
        Object labelStart = new Object();
        Object labelBody = new Object();
        Object labelEnd = new Object();

        mark(labelStart);
        visitAsExpression(condition);

        emitJumpIfTrue(labelBody);

        emitJump(labelEnd);

        mark(labelBody);
        visitAsStatement(body);
        emitJump(labelStart);

        mark(labelEnd);

        if(asExpression)
            emit(Instruction.Factory.loadConst(null));

        return null;
    }

    @Override
    public Void visitEnv() {
        if(asExpression)
            emitLoadSelf();

        return null;
    }

    @Override
    public Void visitItoc(AST i) {
        visitAsExpression(i);
        emit(Instruction.Factory.itoc);

        return null;
    }

    @Override
    public Void visitResume(AST target, AST value) {
        visitAsExpression(value);
        visitAsExpression(target);

        emit(Instruction.Factory.resume(1));

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitFrame() {
        if(asExpression)
            emit(Instruction.Factory.loadCallFrame);

        return null;
    }

    @Override
    public Void visitRet(AST expression) {
        visitAsExpression(expression);
        emit(Instruction.Factory.popCallFrame(1));

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitNot(AST expression) {
        visitAsExpression(expression);

        if(asExpression)
            emit(Instruction.Factory.not);
        else
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitBP() {
        emit(Instruction.Factory.bp);

        if(asExpression)
            emit(Instruction.Factory.loadConst(false));

        return null;
    }

    @Override
    public Void visitLiteral(Object obj) {
        if(asExpression)
            emit(Instruction.Factory.loadConst(obj));

        return null;
    }

    @Override
    public Void visitAddi(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.addi);
        }

        return null;
    }

    @Override
    public Void visitSubi(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.subi);
        }

        return null;
    }

    @Override
    public Void visitMuli(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.muli);
        }

        return null;
    }

    @Override
    public Void visitDivi(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.divi);
        }

        return null;
    }

    @Override
    public Void visitLti(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.lti);
        }

        return null;
    }

    @Override
    public Void visitGti(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.gti);
        }

        return null;
    }

    @Override
    public Void visitEqi(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.eqi);
        }

        return null;
    }

    @Override
    public Void visitEqc(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.eqc);
        }

        return null;
    }

    @Override
    public Void visitLoadD(String name) {
        if(asExpression)
            emit(Instruction.Factory.loadd(name));

        return null;
    }

    @Override
    public Void visitAnd(AST lhs, AST rhs) {
        Object labelTestRhs = new Object();
        Object labelEnd = new Object();

        visitAsExpression(lhs);
        emitJumpIfTrue(labelTestRhs);
        emit(Instruction.Factory.loadConst(false));
        emitJump(labelEnd);
        mark(labelTestRhs);
        visitAsExpression(rhs);
        mark(labelEnd);

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitOr(AST lhs, AST rhs) {
        Object labelLoadTrue = new Object();
        Object labelEnd = new Object();

        visitAsExpression(lhs);
        emitJumpIfTrue(labelLoadTrue);
        visitAsExpression(rhs);
        emitJump(labelEnd);

        mark(labelLoadTrue);
        emit(Instruction.Factory.loadConst(true));
        mark(labelEnd);

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitEval(AST target) {
        emitLoadExceptionHandler();
        emitLoadSelf();
        visitAsExpression(target);
        emit(Instruction.Factory.pushCallFrame(2));

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitTryCatch(AST body, AST handler) {
        // Push new call frame with body as and handler as exception handler

        return null;
    }

    @Override
    public Void visitNewInstance(Constructor constructor, List<AST> args) {
        args.forEach(x -> visitAsExpression(x));
        emit(Instruction.Factory.newInstance(constructor));

        return null;
    }

    @Override
    public Void visitInvoke(Method method, List<AST> args) {
        args.forEach(x -> visitAsExpression(x));
        emit(Instruction.Factory.invoke(method));

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitInvoke(AST target, Method method, List<AST> args) {
        visitAsExpression(target);
        args.forEach(x -> visitAsExpression(x));
        emit(Instruction.Factory.invoke(method));

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitFieldGet(Field field) {
        if(asExpression)
            emit(Instruction.Factory.fieldGet(field));

        return null;
    }

    @Override
    public Void visitFieldGet(AST target, Field field) {
        visitAsExpression(target);
        emit(Instruction.Factory.fieldGet(field));

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitFieldSet(Field field, AST value) {
        visitAsExpression(value);
        if(asExpression)
            emit(Instruction.Factory.dup);
        emit(Instruction.Factory.fieldSet(field));

        return null;
    }

    @Override
    public Void visitFieldSet(AST target, Field field, AST value) {
        visitAsExpression(target);
        visitAsExpression(value);

        if(asExpression)
            emit(Instruction.Factory.dupx1down);

        emit(Instruction.Factory.fieldSet(field));

        return null;
    }

    @Override
    public Void visitLocal(AST target, String name, AST value) {
        visitAsExpression(target);
        visitAsExpression(value);

        if(asExpression)
            emit(Instruction.Factory.dupx1down);

        emit(Instruction.Factory.store(name));

        return null;
    }

    @Override
    public Void visitLocal(String name, AST value) {
        if(functionScope) {
            int currentLocalOrdinal = context.locals.indexOf(name);
            int localOrdinal = currentLocalOrdinal == -1
                ? context.locals.size() // New local slot
                : currentLocalOrdinal; // Reuse local slot
            context.locals.add(name);
            visitAsExpression(value);
            if (asExpression)
                emit(Instruction.Factory.dup);
            emit(Instruction.Factory.storeVar(localOrdinal));
        } else {
            emitLoadSelf();
            visitAsExpression(value);

            if (asExpression)
                emit(Instruction.Factory.dupx1down);

            emit(Instruction.Factory.store(name));
        }

        return null;
    }

    @Override
    public Void visitStore(AST target, String name, AST value) {
        visitAsExpression(target);
        visitAsExpression(value);

        if(asExpression)
            emit(Instruction.Factory.dupx1down);

        emit(Instruction.Factory.store(name));

        return null;
    }

    @Override
    public Void visitStore(String name, AST value) {
        context.emit(name, new MetaFrame.InstructionGenerator() {
            @Override
            public void emitForFrame(int distance, MetaFrame target, int ordinal) {
                if (distance == 0) {
                    visitAsExpression(value);
                    if (asExpression)
                        emit(Instruction.Factory.dup);
                    emit(Instruction.Factory.storeVar(ordinal));
                } else if (distance > 0) {
                    emit(Instruction.Factory.loadVar(0));
                    emit(Instruction.Factory.load("__frame__"));

                    MetaFrame current = context;

                    for (int i = 1; i < distance; i++) {
                        current.isDependent = true;
                        current = current.context;
                        emit(Instruction.Factory.frameLoadVar(0));
                        emit(Instruction.Factory.load("__frame__"));
                    }

                    current.isDependent = true;

                    visitAsExpression(value);

                    if (asExpression)
                        emit(Instruction.Factory.dupx1down);

                    emit(Instruction.Factory.frameStoreVar(ordinal));
                }
            }

            @Override
            public void emitForEnv(String name) {
                emitLoadSelf();
                visitAsExpression(value);

                if (asExpression)
                    emit(Instruction.Factory.dupx1down);

                emit(Instruction.Factory.store(name));
            }
        });

        return null;
    }

    @Override
    public Void visitLoad(AST target, String name) {
        if(asExpression) {
            visitAsExpression(target);
            emit(Instruction.Factory.load(name));
        }

        return null;
    }

    @Override
    public Void visitLoad(String name) {
        if(asExpression) {
            context.emit(name, new MetaFrame.InstructionGenerator() {
                @Override
                public void emitForFrame(int distance, MetaFrame target, int ordinal) {
                    if (distance == 0) {
                        emit(Instruction.Factory.loadVar(ordinal));
                    } else if (distance > 0) {
                        emit(Instruction.Factory.loadVar(0));
                        emit(Instruction.Factory.load("__frame__"));

                        MetaFrame current = context;

                        for (int i = 1; i < distance; i++) {
                            current.isDependent = true;
                            current = current.context;
                            emit(Instruction.Factory.frameLoadVar(0));
                            emit(Instruction.Factory.load("__frame__"));
                        }

                        current.isDependent = true;
                        emit(Instruction.Factory.frameLoadVar(ordinal));
                    }
                }

                @Override
                public void emitForEnv(String name) {
                    emitLoadSelf();
                    emit(Instruction.Factory.load(name));
                }
            });
        }

        return null;
    }

    private void visitAsExpression(AST expression) {
        Generator generator = new Generator(true, functionScope, stagedInstructions, context);
        expression.accept(generator);
    }

    private void visitAsStatement(AST statement) {
        Generator generator = new Generator(false, functionScope, stagedInstructions, context);
        statement.accept(generator);
    }

    public void emit(Instruction instruction) {
        emit((instructions, indexToLabel) -> {
            instructions.add(instruction);
            return () -> {
            };
        });
    }

    private void emit(Generator generator) {
        emit(((instructions, labelToIndex) -> {
            List<Runnable> postProcessors = generator.stagedInstructions.stream()
                .map(x -> x.generate(instructions, labelToIndex))
                .collect(Collectors.toList());
            return () -> postProcessors.forEach(x -> x.run());
        }));
    }

    private void emit(TwoStageGenerator generator) {
        stagedInstructions.add(generator);
    }

    public static CodeSegment toInstructions(AST code) {
        return toInstructions(code, g -> { }, g -> { });
    }

    public static CodeSegment toEvalInstructions(AST code) {
        return toInstructions(
            code,
            g -> { },
            g -> g.emit(Instruction.Factory.popCallFrame(1)));
    }

    private static CodeSegment toInstructions(AST code, Consumer<Generator> pre, Consumer<Generator> post) {
        Generator generator = new Generator(true);
        pre.accept(generator);
        code.accept(generator);
        post.accept(generator);
        Instruction[] instructions = generator.generate();
        int maxStackSize = maxStackSize(generator.context.locals.size(), instructions, 0);
        return new CodeSegment(maxStackSize, instructions, code);
    }

    private static int maxStackSize(int size, Instruction[] instructions, int ip) {
        return maxStackSize(size, size, instructions, ip);
    }

    private static int maxStackSize(int size, int maxSize, Instruction[] instructions, int ip) {
        while(true) {
            Instruction instruction = instructions[ip];

            size += instruction.pushCount() - instruction.popCount();
            maxSize = Math.max(size, maxSize);

            int[] nextIPs = instruction.nextIPs(ip);

            if(nextIPs.length == 0) {
                // Halt/ret/pop frame
                break;
            } else if(nextIPs.length == 1) {
                int nextIP = nextIPs[0];
                if(nextIP < ip)
                    // Loop
                    break;

                ip = nextIP;
            } else if(nextIPs.length == 2) {
                // Branch
                int ipA = nextIPs[0];
                int ipB = nextIPs[1];

                if(ipA > ip && ipB > ip) {
                    return Math.max(
                        maxStackSize(size, maxSize, instructions, ipA),
                        maxStackSize(size, maxSize, instructions, ipB)
                    );
                } else {
                    int maxIP = Math.max(ipA, ipB);

                    if(maxIP > 0)
                        return maxStackSize(size, maxSize, instructions, maxIP);
                    else
                        break;
                }
            }
        }

        return maxSize;
    }
}
