package yaplrawargs;

import java.util.function.BiConsumer;
import static yaplrawargs.Pair.list;

public class Main {
    public static void main(String[] args) {
        Environment env = new Environment();

        // Define primitives
        env.define("quote", (e, a) ->
            e.popFrame(a.current));

        env.define("locals", (e, a) ->
            e.popFrame(e.getLocals()));

        env.defun("first", Pair.class, (e, l) ->
            e.popFrame(l.current));

        env.define("rest", (e, a) ->
            e.popFrame(((Pair) a.current).next));

        env.defun("get", String.class, (e, name) -> {
            Object value = e.getEnvironment().get(name);
            e.popFrame(value);
        });

        env.defun("set", String.class, Object.class, (e, name, value) -> {
            e.getEnvironment().set(name, value);
            e.popFrame(value);
        });

        env.defun("define", String.class, Object.class, (e, name, value) -> {
            e.getEnvironment().define(name, value);
            e.popFrame(value);
        });

        env.defun("apply", Object.class, Object.class, (e, item, locals) ->
            e.apply(item, (Pair) locals));

        env.defun("fun", Object.class, (e, body) ->
            e.popFrame((BiConsumer<Evaluator, Pair>) (e2, l) -> {
                e2.pushFrame(result -> e2.popFrame(result), l);
                e2.evaluate(body);
            }));



        env.defun("+", int.class, int.class, (e, lhs, rhs) -> e.popFrame(lhs + rhs));
        env.defun("-", int.class, int.class, (e, lhs, rhs) -> e.popFrame(lhs - rhs));
        env.defun("/", int.class, int.class, (e, lhs, rhs) -> e.popFrame(lhs / rhs));
        env.defun("*", int.class, int.class, (e, lhs, rhs) -> e.popFrame(lhs * rhs));

        env.defun("print", String.class, (e, str) -> {
            System.out.print(str);
            e.popFrame(null);
        });

        env.define("test", (e, a) ->
            e.popFrame("Test"));

        /*Pair program = Pair.list(
            "print", "A string\n"
        );*/

        /*Pair program = Pair.list(
            Pair.list("print", "A string\n"),
            Pair.list("test"),
            Pair.list("add", 1, 3)
        );*/

        /*
        Pair program = Pair.list(
            Pair.list("define", "add2", Pair.list(Pair.list("lhs"), Pair.list("add", Pair.list("get", "lhs"), 2))),
            Pair.list("add2", 1)
        );
        */
        Pair program = list(
            /*
            Pair.list(
                "define", "add2",

                Pair.list("head", Pair.list("args"))
                Pair.list("head", Pair.list("rest", Pair.list("args")))

                Pair.list(Pair.list("lhs"), Pair.list("add", Pair.list("get", "lhs"), 2))
            ),
            */
            //Pair.list("first", Pair.list("quote", Pair.list("First", "Second")))

            list("define", "myFunc", list("fun", list("quote", list("*", 3, 7)))),
            list("myFunc")
        );

        Evaluator evaluator = new Evaluator(env, new Frame(null, result ->
            System.out.println("Result: " + result),
            null));

        evaluator.evaluate(program);
    }
}
