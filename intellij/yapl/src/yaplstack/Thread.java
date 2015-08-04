package yaplstack;

import java.util.function.BiConsumer;

public class Thread {
    public SymbolTable symbolTable;
    public CallFrame callFrame;
    private boolean run;

    private BiConsumer<Thread, Throwable> exceptionHandler;

    public Thread(SymbolTable symbolTable, BiConsumer<Thread, Throwable> exceptionHandler, CallFrame callFrame) {
        this.symbolTable = symbolTable;
        this.exceptionHandler = exceptionHandler;
        this.callFrame = callFrame;
    }

    public Thread evalAll() {
        run = true;

        // Outermost loop is used for exception handling in case halt is invoked when exceptionHandler is called
        while(run) {
            try {
                while (run)
                    callFrame.codeSegment.instructions[callFrame.ip].eval(this);
            } catch (Throwable e) {
                exceptionHandler.accept(this, e);
            }
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
