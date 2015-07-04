package yaploo;

import java.util.Hashtable;

public class YaplEnvironment implements YaplObject {
    private YaplEnvironment outer;
    private Hashtable<String, YaplObject> locals = new Hashtable<>();

    public YaplEnvironment() {

    }

    public YaplEnvironment(YaplEnvironment outer) {
        this.outer = outer;
    }


    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public YaplObject resolve(String selector) {
        YaplObject obj = locals.get(selector);
        if(obj != null)
            return obj;

        return outer != null ? outer.resolve(selector) : null /* Should nulls be supported? */;
    }

    @Override
    public YaplObject extend() {
        return new YaplEnvironment(this);
    }

    @Override
    public void define(String name, YaplObject obj) {
        locals.put(name, obj);
    }
}
