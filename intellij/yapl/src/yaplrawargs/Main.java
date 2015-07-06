package yaplrawargs;

import java.io.InputStream;
import java.util.PrimitiveIterator;
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

        env.defun("store", String.class, Object.class, (e, name, value) -> {
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


        // A scan function should probably rather be a closure which juggles the user and it as a supplier
        /*
        (define tokens (scan theInput))
        Somehow, scan should signal outwards its extracted tokens

        A response handler simply handles signal provided from a caller?

        //var coscan = (co (scan theInput))
        var coscan = (co scan)

        loop
            //var signal = (resume coscan)
            var signal = (resume coscan theInput) // Initiates coroutine
            // Here, an implicit provider of next (a continuation) is stored for additional next calls
            // This continuation should simply be a singleton arg?
            var numbers = []
            match signal
                case (next, :item):
                    if digit(item)
                        var digits = [item]
                        loop
                            match signal = (resume coscan)
                                case (next, :item):
                                    if digit(item)
                                        digits.append(item)
                                    else
                                        numbers.append(digits)
                                        break
                    if ws(item)
                        continue


            match signal, next, digit // next and digit can looked up and invoked


        Then, a regular function call look something like:

        (resume (co func) arg1, arg2, ..., argn)

        So, a function is basically a continuation factory




        handle
            (scan theInput)
        with // Should be given the code to run
            loop
                var signal = resume // Jumps to execution handle code
                // Here, an implicit provider of next (a continuation) is stored for additional next calls
                // This continuation should simply be a singleton arg?
                var numbers = []
                match signal
                    case (next, :item):
                        if digit(item)
                            var digits = [item]
                            loop
                                match signal = resume
                                    case (next, :item):
                                        if digit(item)
                                            digits.append(item)
                                        else
                                            numbers.append(digits)
                                            break
                        if ws(item)
                            continue


                match signal, next, digit // next and digit can looked up and invoked

        */

        /*
        env.defun("scan", InputStream.class, (e, input) ->
            e.popFrame((BiConsumer<Evaluator, Pair>) (e2, l) -> {
                e2.pushFrame(result -> e2.popFrame(result), l);
                e2.evaluate(body);
            }));
        */


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
