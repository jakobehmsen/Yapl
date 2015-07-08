package yaplstack;

import java.util.Hashtable;

public class Environment {
    public final Environment outer;
    private Hashtable<String, Object> locals = new Hashtable<>();

    public Environment() {
        this(null);
    }

    public Environment(Environment outer) {
        this.outer = outer;
    }

    public void local(String name, Object value) {
        locals.put(name, value);
    }

    public void store(String name, Object value) {
        if(locals.containsKey(name))
            locals.put(name, value);
        else
            outer.store(name, value);
    }

    public Object load(String name) {
        Object value = locals.get(name);
        if(value != null)
            return value;
        return outer.load(name);
    }
}
