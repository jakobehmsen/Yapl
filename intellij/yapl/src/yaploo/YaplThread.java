package yaploo;

public class YaplThread implements YaplObject {
    private YaplObject frame;
    private boolean finished;

    public YaplThread(YaplObject frame) {
        this.frame = frame;
    }

    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public YaplObject getFrame() {
        return frame;
    }

    @Override
    public void pushFrame(YaplObject receiver, YaplObject environment, YaplObject instructions, YaplObject args) {
        frame = new YaplFrame(frame, receiver, environment, instructions, args);
    }

    @Override
    public void popFrame() {
        frame = frame.getOuter();
    }

    @Override
    public void run() {
        while(!finished)
            frame.eval(this);
    }

    @Override
    public void setFinished() {
        finished = true;
    }
}
