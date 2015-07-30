package yaplstack;

public class Thread {
    public SymbolTable symbolTable = new SymbolTable();
    public CallFrame callFrame;
    private boolean run;

    public Thread(CallFrame callFrame) {
        this.callFrame = callFrame;
    }

    public Thread evalAll() {
        try {
            run = true;
            while (run)
                callFrame.codeSegment.instructions[callFrame.ip].eval(this);
        } catch (Throwable e) {
            e.toString();
            e.printStackTrace();
            callFrame.ip--;
            callFrame.codeSegment.instructions[callFrame.ip].eval(this);
            callFrame.codeSegment.instructions[callFrame.ip].eval(this);
        }

        return this;
    }

    public void halt() {
        run = false;
    }

    @Override
    public String toString() {
        return callFrame.toString(this);
    }
}
