package yaploo;

public class YaplThread implements YaplObject {
    private YaplObject frame;

    public YaplThread(YaplObject frame) {
        this.frame = frame;
    }

    @Override
    public void send(YaplObject message) {

    }

    @Override
    public YaplObject getFrame() {
        return frame;
    }
}
