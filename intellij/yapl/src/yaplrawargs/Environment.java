package yaplrawargs;

import java.util.Hashtable;
import java.util.function.BiConsumer;

public class Environment {
    private static Object UNSET = new Object();

    public final Environment outer;
    private Hashtable<String, Object> locals = new Hashtable<>();

    public Environment(Environment outer) {
        this.outer = outer;
    }

    public Environment() { this(null); }

    public void define(String name, BiConsumer<Evaluator, Pair> function) {
        define(name, (Object)function);
    }

    public <T> void defun(String name, Class<T> arg0Type, BiConsumer<Evaluator, T> function) {
        define(name, (e, l) ->
            e.evaluate(l.current, arg0 ->
                function.accept(e, (T) arg0)));
    }

    public <T, R> void defun(String name, Class<T> arg0Type, Class<R> arg1Type, TriConsumer<Evaluator, T, R> function) {
        define(name, (e, l) ->
            e.evaluate(l.current, arg0 ->
                e.evaluate(l.next.current, arg1 ->
                    function.accept(e, (T)arg0, (R)arg1))));
    }

    public void define(String name, Object value) {
        if(value == null)
            value = UNSET;

        locals.put(name, value);
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
