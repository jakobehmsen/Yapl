package yaplstack;

public class CallFrame {
    public final CallFrame outer;
    public Environment environment;
    public final Instruction[] instructions;
    public int ip;

    public CallFrame(Instruction[] instructions) {
        this(new Environment(), null, instructions);
    }

    public CallFrame(Environment environment, CallFrame outer, Instruction[] instructions) {
        this.environment = environment;
        this.outer = outer;
        this.instructions = instructions;
    }

    public void incrementIP() {
        ip++;
    }

    public void setIP(int index) {
        ip = index;
    }
}
