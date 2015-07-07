package yaplstack;

public class Thread {
    public Frame frame;
    private boolean finished;

    public Thread(Frame frame) {
        this.frame = frame;
    }

    public Thread evalAll() {
        while(!finished)
            frame.instructions[frame.ip].eval(this);

        return this;
    }

    public void setFinished() {
        finished = true;
    }
}
