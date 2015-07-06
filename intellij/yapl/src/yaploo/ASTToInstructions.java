package yaploo;

import java.util.ArrayList;
import java.util.List;

public class ASTToInstructions implements AST.Visitor<ASTToInstructions> {
    private boolean atRoot;
    private List<YaplObject> instructions;

    public ASTToInstructions(List<YaplObject> instructions) {
        this(true, instructions);
    }

    public ASTToInstructions(boolean atRoot, List<YaplObject> instructions) {
        this.atRoot = atRoot;
        this.instructions = instructions;
    }

    @Override
    public ASTToInstructions visitLiteral(int i) {
        instructions.add(YaplPrimitive.Factory.push(new YaplInteger(i)));
        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return this;
    }

    @Override
    public ASTToInstructions visitLiteral(String str) {
        instructions.add(YaplPrimitive.Factory.push(new YaplString(str)));
        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return this;
    }

    @Override
    public ASTToInstructions visitSelf() {
        instructions.add(YaplPrimitive.Factory.pushSelf);
        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return this;
    }

    @Override
    public ASTToInstructions visitMessageSend(AST receiver, String selector, List<AST> args) {
        visitAsExpression(receiver);
        args.forEach(x -> visitAsExpression(x));
        instructions.add(YaplPrimitive.Factory.send(selector, args.size()));

        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return this;
    }

    @Override
    public ASTToInstructions visitSequence(List<AST> expressions) {
        if(atRoot) {
            for (AST expression : expressions)
                visitAsStatement(expression);
        } else {
            for(int i = 0; i < expressions.size() - 1; i++)
                visitAsStatement(expressions.get(i));
            visitAsExpression(expressions.get(expressions.size() - 1));
        }

        return this;
    }

    @Override
    public ASTToInstructions visitDefine(String name, AST value) {
        visitAsExpression(value);

        if(!atRoot)
            instructions.add(YaplPrimitive.Factory.dup);

        instructions.add(YaplPrimitive.Factory.define(name));

        return this;
    }

    @Override
    public ASTToInstructions visitSet(String name, AST value) {
        visitAsExpression(value);

        if(!atRoot)
            instructions.add(YaplPrimitive.Factory.dup);

        instructions.add(YaplPrimitive.Factory.store(name));

        return this;
    }

    @Override
    public ASTToInstructions visitGet(String name) {
        instructions.add(YaplPrimitive.Factory.load(name));
        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return this;
    }

    @Override
    public ASTToInstructions visitConst(YaplObject obj) {
        instructions.add(YaplPrimitive.Factory.push(obj));
        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return this;
    }

    public ASTToInstructions append(YaplObject instruction) {
        instructions.add(instruction);
        return this;
    }

    private void visitAsStatement(AST statement) {
        statement.accept(new ASTToInstructions(true, instructions));
    }

    private void visitAsExpression(AST expression) {
        expression.accept(new ASTToInstructions(false, instructions));
    }

    public static YaplObject toProgram(AST ast) {
        ArrayList<YaplObject> instructions = new ArrayList<>();

        ast.accept(new ASTToInstructions(instructions));
        instructions.add(YaplPrimitive.Factory.finish);

        return new YaplArray(instructions.toArray(new YaplObject[instructions.size()]));
    }

    public YaplObject toYaplObject() {
        return new YaplArray(instructions.toArray(new YaplObject[instructions.size()]));
    }
}
