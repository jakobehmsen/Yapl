package yaplstack;

import java.util.Hashtable;
import java.util.stream.Collectors;

public class Environment {
    private Hashtable<Integer, Object> locals = new Hashtable<>();

    public void store(int code, Object value) {
        locals.put(code, value);
    }

    public Object load(int code) {
        return locals.get(code);
    }

    @Override
    public String toString() {
        return locals.entrySet().stream().collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue() instanceof Environment ? "{...}" : x.getValue())).toString();
    }
}
