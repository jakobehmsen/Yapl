package yaplstack;

public class CallFrame {
    public final CallFrame outer;
    public final Instruction[] instructions;
    public int ip;

    public CallFrame(Instruction[] instructions) {
        this(null, instructions);
    }

    public CallFrame(CallFrame outer, Instruction[] instructions) {
        this.outer = outer;
        this.instructions = instructions;
    }

    public void incrementIP() {
        ip++;

        if(ip == 6)
            new String();
    }
}
