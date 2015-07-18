package yaplstack;

import java.util.Hashtable;

public class Environment {
    private static final Object NULL = new Object();
    public final Environment outer;
    private Hashtable<String, Object> locals = new Hashtable<>();

    public Environment() {
        this(null);
    }

    public Environment(Environment outer) {
        this.outer = outer;
    }

    public void local(String name, Object value) {
        put(name, value);
    }

    public void store(String name, Object value) {

        if(locals.containsKey(name)) {
            put(name, value);
        } else
            outer.store(name, value);
    }

    private void put(String name, Object value) {
        if(value == null)
            value = NULL;

        locals.put(name, value);
    }

    public Object load(String name) {
        Object value = locals.get(name);
        if(value != null)
            return value != NULL ? value : null;
        return outer.load(name);
    }
}
