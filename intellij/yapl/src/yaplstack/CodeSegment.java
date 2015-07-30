package yaplstack;

import yaplstack.ast.Node;

public class CodeSegment {
    public final int maxStackSize;
    public final Instruction[] instructions;
    public final Node source;

    public CodeSegment(int maxStackSize, Instruction[] instructions, Node source) {
        this.maxStackSize = maxStackSize;
        this.instructions = instructions;
        this.source = source;
    }
}
