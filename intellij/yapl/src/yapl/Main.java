package yapl;

import java.util.function.BiConsumer;

public class Main {
    public static void main(String[] args) {
        Environment env = new Environment();

        env.define("get", (BiConsumer<Evaluator, Pair>) (e, a) -> {
            String name = (String) a.current;
            Object value = e.getEnvironment().get(name);

            e.popFrame(value);
        });

        env.define("define", (BiConsumer<Evaluator, Pair>) (e, a) -> {
            String name = (String) a.current;
            Object value = a.next.current;

            e.getEnvironment().define(name, value);

            e.popFrame(value);
        });

        env.define("add", (BiConsumer<Evaluator, Pair>) (e, a) ->
            e.evaluate(a, (lhs, rhs) -> e.popFrame((int) lhs + (int) rhs)));

        env.define("apply", (BiConsumer<Evaluator, Pair>) (e, a) -> {
            Object item = a.current;
            Object arguments = a.next.current;

            e.apply(item, (Pair) arguments);
        });

        env.define("print", (BiConsumer<Evaluator, Pair>) (e, a) -> {
            System.out.print(a.current);
            e.popFrame(null);
        });

        env.define("test", (BiConsumer<Evaluator, Pair>) (e, a) -> {
            e.popFrame("Test");
        });

        /*Pair program = Pair.list(
            "print", "A string\n"
        );*/

        /*Pair program = Pair.list(
            Pair.list("print", "A string\n"),
            Pair.list("test"),
            Pair.list("add", 1, 3)
        );*/

        /*
        (delimit (
                (define lhs 1)
                (define rhs 3)
                (apply add)))
        */

        Pair program = Pair.list(
            Pair.list("define", "add2", Pair.list(Pair.list("lhs"), Pair.list("add", Pair.list("get", "lhs"), 2))),
            Pair.list("add2", 1)
        );

        Evaluator evaluator = new Evaluator(env, new Frame(null, result ->
            System.out.println("Result: " + result)));

        evaluator.evaluate(program);
    }
}
