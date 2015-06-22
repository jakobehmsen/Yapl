package yapl;

import java.util.function.BiConsumer;

public class Main {
    public static void main(String[] args) {
        Environment env = new Environment();

        env.declare("apply");
        env.set("apply", (BiConsumer<Evaluator, Pair>) (e, a) -> {
            Object item = a.current;
            Object arguments = a.next.current;

            e.apply(item, (Pair) arguments);
        });

        env.declare("print");
        env.set("print", (BiConsumer<Evaluator, Pair>) (e, a) -> {
            System.out.print(a.current);
            e.popFrame(null);
        });

        env.declare("test");
        env.set("test", (BiConsumer<Evaluator, Pair>) (e, a) -> {
            e.popFrame("Test");
        });

        /*Pair program = Pair.list(
            "print", "A string\n"
        );*/

        Pair program = Pair.list(
            Pair.list("print", "A string\n"),
            Pair.list("test")
        );

        Evaluator evaluator = new Evaluator(env, new Frame(null, result -> {
            System.out.println("Result: " + result);
        }));

        evaluator.evaluate(program);
    }
}
