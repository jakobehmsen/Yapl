package yaploo;

public class YaplInteger implements YaplObject {
    private final int value;

    public YaplInteger(int value) {
        this.value = value;
    }

    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public int toInt() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
