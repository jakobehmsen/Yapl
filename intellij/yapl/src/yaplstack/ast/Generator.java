package yaplstack.ast;

import yaplstack.Instruction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Generator implements AST.Visitor<Void> {
    private boolean asExpression;
    private List<Instruction> instructions;

    public Generator(boolean asExpression, List<Instruction> instructions) {
        this.instructions = instructions;
        this.asExpression = asExpression;
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
    public Void visitFunction(List<String> params, AST code) {
        if(asExpression) {
            ArrayList<Instruction> instructions = new ArrayList<>();
            Generator generator = new Generator(true, instructions);

            generator.emit(Instruction.Factory.loadEnvironment);
            generator.emit(Instruction.Factory.extend);
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
            generator.emit(Instruction.Factory.outer);
            generator.emit(Instruction.Factory.storeEnvironment);
            generator.emit(Instruction.Factory.ret);

            emit(Instruction.Factory.loadConst(instructions.toArray(new Instruction[instructions.size()])));
        }

        return null;
    }

    @Override
    public Void visitCall(AST target, List<AST> asts) {
        asts.forEach(x -> visitAsExpression(x));
        visitAsExpression(target);
        emit(Instruction.Factory.call);

        if(!asExpression)
            emit(Instruction.Factory.pop);

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
    public Void visitMuli(AST lhs, AST rhs) {
        if(asExpression) {
            visitAsExpression(lhs);
            visitAsExpression(rhs);
            emit(Instruction.Factory.muli);
        }

        return null;
    }

    @Override
    public Void visitInvoke(AST target, Method method, List<AST> args) {
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
    public Void visitStore(String name, AST value) {
        emit(Instruction.Factory.loadEnvironment);
        visitAsExpression(value);

        if(asExpression)
            emit(Instruction.Factory.dupx1);

        emit(Instruction.Factory.store(name));

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
        Generator generator = new Generator(true, instructions);
        expression.accept(generator);
    }

    private void visitAsStatement(AST expression) {
        Generator generator = new Generator(false, instructions);
        expression.accept(generator);
    }

    private void emit(Instruction instruction) {
        if(instructions.size() == 4)
            instruction.toString();

        instructions.add(instruction);
    }

    public static Instruction[] toInstructions(AST code) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        code.accept(new Generator(true, instructions));
        return instructions.toArray(new Instruction[instructions.size()]);
    }
}
