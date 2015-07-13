package yaplstack.ast;

import yaplstack.Instruction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Generator implements AST.Visitor<Void> {
    interface TwoStageGenerator {
        Runnable generate(List<Instruction> instructions, Map<Object, Integer> labelToIndex);
    }

    private boolean asExpression;
    private boolean functionScope;
    private List<TwoStageGenerator> stagedInstructions = new ArrayList<>();

    private List<String> locals;

    public Generator(boolean asExpression) {
        this(asExpression, false, new ArrayList<>(), new ArrayList<>());
    }

    public Generator(boolean asExpression, boolean functionScope, List<TwoStageGenerator> stagedInstructions, List<String> locals) {
        this.stagedInstructions = stagedInstructions;
        this.asExpression = asExpression;
        this.functionScope = functionScope;
        this.locals = locals;
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
        emit(Instruction.Factory.finish);

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

    @Override
    public Void visitFN(List<String> params, AST code) {
        if(asExpression) {
            Generator bodyGenerator = new Generator(true);
            bodyGenerator.functionScope = true;
            bodyGenerator.locals.addAll(params);
            code.accept(bodyGenerator);
            int variableCount = bodyGenerator.locals.size() - params.size();

            Generator generator = new Generator(true);
            generator.locals.addAll(params);

            // Forward arguments
            generator.emit(Instruction.Factory.pushOperandFrame(params.size()));

            // Allocate variables
            for(int i = 0; i <= variableCount; i++)
                generator.emit(Instruction.Factory.loadConst(null));

            generator.emit(bodyGenerator);

            generator.emit(Instruction.Factory.popOperandFrame(1));
            generator.emit(Instruction.Factory.popCallFrame);

            Instruction[] instructionArray = generator.generate();

            emit(Instruction.Factory.loadConst(instructionArray));
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
    public Void visitApply(AST target, List<AST> asts) {
        asts.forEach(x -> visitAsExpression(x));
        visitAsExpression(target);
        emit(Instruction.Factory.pushCallFrame);

        if(!asExpression)
            emit(Instruction.Factory.pop);

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
        body.accept(this);
        emitJump(labelStart);

        mark(labelEnd);

        if(asExpression)
            emit(Instruction.Factory.loadConst(null));

        return null;
    }

    @Override
    public Void visitExtend(AST target) {
        visitAsExpression(target);
        emit(Instruction.Factory.extendEnvironment);

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitEnv() {
        if(asExpression)
            emit(Instruction.Factory.loadEnvironment);

        return null;
    }

    @Override
    public Void visitOuterEnv(AST target) {
        visitAsExpression(target);
        emit(Instruction.Factory.outerEnvironment);

        return null;
    }

    @Override
    public Void loadVar(int ordinal) {
        emit(Instruction.Factory.loadVar(ordinal));

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
    public Void visitNewInstance(Constructor constructor, List<AST> args) {
        args.forEach(x -> visitAsExpression(x));
        emit(Instruction.Factory.newInstance(constructor));

        return null;
    }

    @Override
    public Void visitInvoke(Method method, List<AST> args) {
        args.forEach(x -> visitAsExpression(x));
        emit(Instruction.Factory.invoke(method));

        return null;
    }

    @Override
    public Void visitInvoke(AST target, Method method, List<AST> args) {
        visitAsExpression(target);
        args.forEach(x -> visitAsExpression(x));
        emit(Instruction.Factory.invoke(method));

        return null;
    }

    @Override
    public Void visitFieldGet(Field field) {
        emit(Instruction.Factory.fieldGet(field));

        return null;
    }

    @Override
    public Void visitFieldGet(AST target, Field field) {
        visitAsExpression(target);
        emit(Instruction.Factory.fieldGet(field));

        return null;
    }

    @Override
    public Void visitFieldSet(Field field, AST value) {
        visitAsExpression(value);
        emit(Instruction.Factory.fieldSet(field));

        return null;
    }

    @Override
    public Void visitFieldSet(AST target, Field field, AST value) {
        visitAsExpression(target);
        visitAsExpression(value);
        emit(Instruction.Factory.fieldSet(field));

        return null;
    }

    @Override
    public Void visitOn(AST target, AST code) {
        emit(Instruction.Factory.loadEnvironment);
        visitAsExpression(target);
        emit(Instruction.Factory.storeEnvironment);

        visitAsNonFunction(code);

        //code.accept(this);
        if(asExpression)
            emit(Instruction.Factory.swap);
        emit(Instruction.Factory.storeEnvironment);

        return null;
    }

    @Override
    public Void visitLocal(String name, AST value) {
        if(functionScope) {
            int localOrdinal = locals.size();
            locals.add(name);
            visitAsExpression(value);
            if (asExpression)
                emit(Instruction.Factory.dup);
            emit(Instruction.Factory.storeVar(localOrdinal));
        } else {
            emit(Instruction.Factory.loadEnvironment);
            visitAsExpression(value);

            if (asExpression)
                emit(Instruction.Factory.dupx1);

            emit(Instruction.Factory.local(name));
        }

        return null;
    }

    @Override
    public Void visitStore(AST target, String name, AST value) {
        visitAsExpression(target);
        visitAsExpression(value);

        if(asExpression)
            emit(Instruction.Factory.dupx1);

        emit(Instruction.Factory.store(name));

        return null;
    }

    @Override
    public Void visitStore(String name, AST value) {
        int localOrdinal = locals.indexOf(name);

        if(localOrdinal != -1) {
            visitAsExpression(value);
            if (asExpression)
                emit(Instruction.Factory.dup);
            emit(Instruction.Factory.storeVar(localOrdinal));
        } else {
            emit(Instruction.Factory.loadEnvironment);
            visitAsExpression(value);

            if (asExpression)
                emit(Instruction.Factory.dupx1);

            emit(Instruction.Factory.store(name));
        }

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
            int localOrdinal = locals.indexOf(name);

            if(localOrdinal != -1) {
                emit(Instruction.Factory.loadVar(localOrdinal));
            } else {
                emit(Instruction.Factory.loadEnvironment);
                emit(Instruction.Factory.load(name));
            }
        }

        return null;
    }

    private void visitAsNonFunction(AST code) {
        Generator generator = new Generator(asExpression, false, stagedInstructions, locals);
        code.accept(generator);
    }

    private void visitAsExpression(AST expression) {
        Generator generator = new Generator(true, functionScope, stagedInstructions, locals);
        expression.accept(generator);
    }

    private void visitAsStatement(AST statement) {
        Generator generator = new Generator(false, functionScope, stagedInstructions, locals);
        statement.accept(generator);
    }

    private void emit(Instruction instruction) {
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

    public static Instruction[] toInstructions(AST code) {
        return toInstructions(code, g -> { }, g -> { });
    }

    private static Instruction[] toInstructions(AST code, Consumer<Generator> pre, Consumer<Generator> post) {
        Generator generator = new Generator(true);
        pre.accept(generator);
        code.accept(generator);
        post.accept(generator);
        return generator.generate();
    }
}
