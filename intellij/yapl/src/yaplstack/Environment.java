package yaplstack;

import java.util.Hashtable;

public class Environment {
    private Hashtable<String, Object> locals = new Hashtable<>();

    public void store(String name, Object value) {
        locals.put(name, value);
    }

    public Object load(String name) {
        return locals.get(name);
    }
}
