package yaploo;

public class YaplThread implements YaplObject {
    private YaplObject frame;

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
    public void pushFrame(YaplObject receiver, YaplObject environment) {
        frame = new YaplFrame(frame, receiver, environment);
    }

    @Override
    public void popFrame() {
        frame = frame.getOuter();
    }
}
