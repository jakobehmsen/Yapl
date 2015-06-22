package yapl;

import java.util.Hashtable;

public class Environment {
    private static Object UNSET = new Object();

    public final Environment outer;
    private Hashtable<String, Object> locals = new Hashtable<>();

    public Environment(Environment outer) {
        this.outer = outer;
    }

    public Environment() { this(null); }

    public void declare(String name) {
        locals.put(name, UNSET);
    }

    public void set(String name, Object value) {
        locals.put(name, value);
    }

    public Object get(String name) {
        Object value = locals.get(name);
        if(value != null && value != UNSET)
            return value;
        return outer != null ? outer.get(name) : null;
    }
}
