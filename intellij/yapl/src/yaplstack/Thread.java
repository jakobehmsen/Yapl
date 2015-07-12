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
        try {
            while (!finished)
                callFrame.instructions[callFrame.ip].eval(this);
        } catch (Throwable e) {
            e.toString();
            e.printStackTrace();
            callFrame.ip--;
            callFrame.instructions[callFrame.ip].eval(this);
            callFrame.instructions[callFrame.ip].eval(this);
        }

        return this;
    }

    public void setFinished() {
        finished = true;
    }
}
