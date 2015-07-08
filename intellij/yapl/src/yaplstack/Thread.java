package yaplstack;

public class Thread {
    public CallFrame callFrame;
    public OperandFrame operandFrame;
    public Environment environment;
    private boolean finished;

    public Thread(CallFrame callFrame) {
        this.callFrame = callFrame;
        operandFrame = new OperandFrame();
        environment = new Environment();
    }

    public Thread evalAll() {
        while(!finished)
            callFrame.instructions[callFrame.ip].eval(this);

        return this;
    }

    public void setFinished() {
        finished = true;
    }
}
