package yaploo;

import java.util.List;

public class ASTToInstructions implements AST.Visitor<Void> {
    private boolean atRoot;
    private List<YaplObject> instructions;

    public ASTToInstructions(boolean atRoot, List<YaplObject> instructions) {
        this.atRoot = atRoot;
        this.instructions = instructions;
    }

    @Override
    public Void visitLiteral(int i) {
        instructions.add(YaplPrimitive.Factory.push(new YaplInteger(i)));
        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return null;
    }

    @Override
    public Void visitLiteral(String str) {
        instructions.add(YaplPrimitive.Factory.push(new YaplString(str)));
        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return null;
    }

    @Override
    public Void visitSelf() {
        instructions.add(YaplPrimitive.Factory.pushSelf);
        if(atRoot)
            instructions.add(YaplPrimitive.Factory.pop);

        return null;
    }

    @Override
    public Void visitMessageSend(AST receiver, String selector, List<AST> args) {
        return null;
    }

    @Override
    public Void visitSequence(List<AST> expressions) {
        for (AST expression : expressions)
            expression.accept(this);

        return null;
    }

    @Override
    public Void visitDefine(String name, AST value) {
        value.accept(new ASTToInstructions(false, instructions));
        instructions.add(YaplPrimitive.Factory.define(name));

        return null;
    }

    @Override
    public Void visitSet(String name, AST value) {
        return null;
    }

    @Override
    public Void visitGet(String name) {
        return null;
    }
}
