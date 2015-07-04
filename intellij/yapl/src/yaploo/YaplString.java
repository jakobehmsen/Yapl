package yaploo;

public class YaplString implements YaplObject {
    private final String value;

    public YaplString(String value) {
        this.value = value;
    }

    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public String toString() {
        return value;
    }
}
