package yaplstack;

import yaplstack.ast.Node;

public class CodeSegment {
    public final Instruction[] instructions;
    public final Node source;

    public CodeSegment(Instruction[] instructions, Node source) {
        this.instructions = instructions;
        this.source = source;
    }
}
