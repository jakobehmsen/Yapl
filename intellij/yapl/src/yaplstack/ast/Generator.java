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
import java.util.function.Supplier;

public class Generator implements AST.Visitor<Void> {
    private boolean asExpression;
    private List<Supplier<Instruction>> instructions;
    private Map<Object, Integer> labelToIndex = new Hashtable<>();

    public Generator(boolean asExpression) {
        this(asExpression, new ArrayList<>(), new Hashtable<>());
    }

    public Generator(boolean asExpression, List<Supplier<Instruction>> instructions, Map<Object, Integer> labelToIndex) {
        this.instructions = instructions;
        this.asExpression = asExpression;
        this.labelToIndex = labelToIndex;
    }

    private void mark(Object label) {
        int index = instructions.size();
        labelToIndex.put(label, index);
    }

    private void emitJump(Object label, Function<Integer, Instruction> instructionFunction) {
        emit(() -> {
            int index = labelToIndex.get(label);
            return instructionFunction.apply(index);
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
            Generator generator = new Generator(true);

            generator.emit(Instruction.Factory.loadEnvironment);
            generator.emit(Instruction.Factory.extendEnvironment);
            generator.emit(Instruction.Factory.storeEnvironment);

            for(int i = params.size() - 1; i >= 0; i--) {
                generator.emit(Instruction.Factory.loadEnvironment);
                generator.emit(Instruction.Factory.swap);
                generator.emit(Instruction.Factory.local(params.get(i)));
            }

            generator.emit(Instruction.Factory.pushOperandFrame(0));

            code.accept(generator);

            generator.emit(Instruction.Factory.popOperandFrame(1));
            generator.emit(Instruction.Factory.loadEnvironment);
            generator.emit(Instruction.Factory.outerEnvironment);
            generator.emit(Instruction.Factory.storeEnvironment);
            generator.emit(Instruction.Factory.popCallFrame);

            Instruction[] instructionArray = generator.generate();

            emit(Instruction.Factory.loadConst(instructionArray));
        }

        return null;
    }

    private Instruction[] generate() {
        return instructions.stream().map(x -> x.get()).toArray(s -> new Instruction[s]);
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
        code.accept(this);
        if(asExpression)
            emit(Instruction.Factory.swap);
        emit(Instruction.Factory.storeEnvironment);

        return null;
    }

    @Override
    public Void visitLocal(String name, AST value) {
        emit(Instruction.Factory.loadEnvironment);
        visitAsExpression(value);

        if(asExpression)
            emit(Instruction.Factory.dupx1);

        emit(Instruction.Factory.local(name));

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
        emit(Instruction.Factory.loadEnvironment);
        visitAsExpression(value);

        if(asExpression)
            emit(Instruction.Factory.dupx1);

        emit(Instruction.Factory.store(name));

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
            emit(Instruction.Factory.loadEnvironment);
            emit(Instruction.Factory.load(name));
        }

        return null;
    }

    private void visitAsExpression(AST expression) {
        Generator generator = new Generator(true, instructions, labelToIndex);
        expression.accept(generator);
    }

    private void visitAsStatement(AST expression) {
        Generator generator = new Generator(false, instructions, labelToIndex);
        expression.accept(generator);
    }

    private void emit(Instruction instruction) {
        emit(() -> instruction);
    }

    private void emit(Supplier<Instruction> instruction) {
        instructions.add(instruction);
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
