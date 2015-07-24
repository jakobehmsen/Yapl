package yaplstack;

import java.util.Hashtable;
import java.util.stream.Collectors;

public class Environment {
    private static final Object NULL = new Object();
    private Hashtable<Integer, Object> locals = new Hashtable<>();

    public void store(int code, Object value) {
        if(value == null)
            value = NULL;

        locals.put(code, value);
    }

    public Object load(int code) {
        Object value = locals.get(code);
        return value != NULL ? value : null;
    }

    @Override
    public String toString() {
        return locals.entrySet().stream().collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue() instanceof Environment ? "{...}" : x.getValue())).toString();
    }
}
