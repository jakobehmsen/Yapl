package yaploo;

public class YaplArray implements YaplObject {
    private final YaplObject[] items;

    public YaplArray() {
        this(new YaplObject[0]);
    }

    public YaplArray(YaplObject[] items) {
        this.items = items;
    }

    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public YaplObject get(int index) {
        return items[index];
    }

    @Override
    public void eval(YaplObject thread) {
        for(int i = 0; i < items.length; i++)
            items[i].eval(thread);
    }

    @Override
    public YaplObject length() {
        return new YaplInteger(items.length);
    }
}
