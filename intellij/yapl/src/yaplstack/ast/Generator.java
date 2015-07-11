package yaplstack.ast;

import yaplstack.Instruction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Generator implements AST.Visitor<Void> {
    private boolean asExpression;
    private List<Instruction> instructions;

    public Generator(boolean asExpression) {
        this(asExpression, new ArrayList<>());
    }

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
        return instructions.stream().toArray(s -> new Instruction[s]);
    }

    @Override
    public Void visitCall(AST target, List<AST> asts) {
        asts.forEach(x -> visitAsExpression(x));
        visitAsExpression(target);
        emit(Instruction.Factory.pushCallFrame);

        if(!asExpression)
            emit(Instruction.Factory.pop);

        return null;
    }

    @Override
    public Void visitTest(AST condition, AST ifTrue, AST ifFalse) {
        emit(Instruction.Factory.loadConst(toInstructions(ifTrue, g -> { }, g -> g.emit(Instruction.Factory.popCallFrame))));
        emit(Instruction.Factory.loadConst(toInstructions(ifFalse, g -> { }, g -> g.emit(Instruction.Factory.popCallFrame))));
        visitAsExpression(condition);
        emit(Instruction.Factory.pushConditionalCallFrame);

        return null;
    }

    @Override
    public Void visitLoop(AST condition, AST body) {
        /*
        instructions = {
            test(
                condition,
                {
                    body
                    loadCallFrame
                    callFrameOuter
                    dup
                    callFrameInstructions
                    pushCallFrameFrom
                }
                popCallFrame
            )
            popCallFrame
        }
        loadConst(instructions)
        pushCallFrame
        */

        Generator ifTrueGenerator = new Generator(false);
        body.accept(ifTrueGenerator);
        ifTrueGenerator.emit(Instruction.Factory.loadCallFrame);            // [..., test call frame]
        ifTrueGenerator.emit(Instruction.Factory.outerCallFrame);           // [..., body call frame]
        ifTrueGenerator.emit(Instruction.Factory.dup);                      // [..., body call frame, body call frame]
        ifTrueGenerator.emit(Instruction.Factory.outerCallFrame);           // [..., body call frame, loop call frame]
        ifTrueGenerator.emit(Instruction.Factory.swap);                     // [..., loop call frame, body call frame]
        ifTrueGenerator.emit(Instruction.Factory.callFrameInstructions);    // [..., loop call frame, body call frame instructions]
        ifTrueGenerator.emit(Instruction.Factory.pushCallFrameFrom);        // [...]

        Generator ifFalseGenerator = new Generator(false);
        ifFalseGenerator.emit(Instruction.Factory.popCallFrame);

        Generator loopGenerator = new Generator(false);

        loopGenerator.emit(Instruction.Factory.loadConst(ifTrueGenerator.generate()));
        loopGenerator.emit(Instruction.Factory.loadConst(ifFalseGenerator.generate()));
        loopGenerator.visitAsExpression(condition);
        loopGenerator.emit(Instruction.Factory.pushConditionalCallFrame);
        loopGenerator.emit(Instruction.Factory.popCallFrame);

        emit(Instruction.Factory.loadConst(loopGenerator.generate()));
        emit(Instruction.Factory.pushCallFrame);

        if(asExpression)
            emit(Instruction.Factory.loadConst(null));

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
