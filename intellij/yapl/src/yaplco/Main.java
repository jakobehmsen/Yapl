package yaplco;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;

import static yaplco.Pair.list;

public class Main {
    public static void main(String[] args) {
        Environment env = new Environment();

        // Define primitives

        /*
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
        */


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

        Scheduler scheduler = new Scheduler();

        env.define("parse", new Primitive() {
            @Override
            public CoRoutine newCo(Scheduler scheduler, Evaluator evaluator) {
                return new CoRoutineImpl() {
                    int index;
                    Reader reader;
                    StringBuffer buffer;
                    boolean inited;

                    @Override
                    public void resume(CoRoutine requester, Object signal) {
                        if(!inited) {
                            evaluator.eval(scheduler, ((Pair) signal).current, arg0 -> {
                                InputStream inputStream = (InputStream) arg0;
                                reader = new InputStreamReader(inputStream);
                                buffer = new StringBuffer();

                                inited = true;

                                ignore();
                                scheduler.resumeResponse(this, requester, null);
                                //resumeResponse(requester, null);
                            });
                        } else {
                            if(!atEnd()) {
                                Object next = next();
                                ignore();
                                scheduler.resumeResponse(this, requester, Pair.list("next", next));
                                //requester.resumeResponse(this, Pair.list("next", next));
                            } else
                                scheduler.resumeResponse(this, requester, Pair.list("atEnd"));
                                //requester.resumeResponse(this, Pair.list("atEnd"));
                        }
                    }

                    Object next() {
                        if(peek() == '(') {
                            consume();
                            ignore();

                            Object next = nextList();
                            if(peek() == ')') {
                                consume();
                                ignore();

                                return next;
                            } else {
                                // ERROR
                            }
                        } else if(Character.isDigit(peek())) {
                            StringBuffer digits = new StringBuffer();
                            digits.append(peek());
                            consume();

                            while(Character.isDigit(peek())) {
                                digits.append(peek());
                                consume();
                            }

                            return Integer.parseInt(digits.toString());
                        } else if(Character.isJavaIdentifierPart(peek())) {
                            StringBuffer parts = new StringBuffer();
                            parts.append(peek());
                            consume();

                            while(Character.isJavaIdentifierPart(peek())) {
                                parts.append(peek());
                                consume();
                            }

                            return parts.toString();
                        } else if(peek() == '"') {
                            consume();

                            StringBuffer chars = new StringBuffer();

                            while(true) {
                                if(peek() == '"') {
                                    consume();
                                    break;
                                }
                                // Check character escapes

                                chars.append(peek());
                                consume();
                            }

                            return chars.toString();
                        }

                        return null;
                    }

                    Pair nextList() {
                        ArrayList<Object> items = new ArrayList<>();

                        while(!atEnd()) {
                            Object next = next();
                            if(next != null) {
                                items.add(next);
                                ignore();
                            } else
                                break;
                        }

                        return Pair.list(items);
                    }

                    void consume() {
                        index++;
                    }

                    boolean atEnd() {
                        ensureBuffered();

                        return index >= buffer.length();
                    }

                    void ensureBuffered() {
                        if (index >= buffer.length()) {
                            try {
                                CharBuffer readChars = CharBuffer.allocate(1024);
                                int readCharCount = reader.read(readChars);
                                if(readCharCount > 0) {
                                    readChars.position(0);
                                    buffer.append(readChars, 0, readCharCount);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    char peek() {
                        ensureBuffered();

                        return !atEnd() ? buffer.charAt(index) : '\0';
                    }

                    void ignore() {
                        while (!atEnd() && Character.isWhitespace(peek()))
                            consume();
                    }
                };
            }
        });

        env.defun("+", int.class, int.class, (s, evaluator, requester, lhs, rhs) ->
            s.respond(requester, lhs + rhs));
            //requester.respond(lhs + rhs));
        /*env.defun("+", int.class, int.class, (e, r, lhs, rhs) -> r);
        env.defun("-", int.class, int.class, (co, lhs, rhs) -> e.popFrame(lhs - rhs));
        env.defun("/", int.class, int.class, (co, lhs, rhs) -> e.popFrame(lhs / rhs));
        env.defun("*", int.class, int.class, (co, lhs, rhs) -> e.popFrame(lhs * rhs));*/

        /*env.defun("print", String.class, (e, str) -> {
            System.out.print(str);
            e.popFrame(null);
        });

        env.define("test", (e, a) ->
            e.popFrame("Test"));*/

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

        String src = "( 11 (234 2)43 43)";
        CoRoutine coParse = new Evaluator(env).eval(scheduler, list("parse", new ByteArrayInputStream(src.getBytes())));

        scheduler.respond(
            new CoRoutineImpl() {
                @Override
                public void resume(CoRoutine requester, Object signal) {
                    // Parse next
                    scheduler.resume(this, coParse, null);
                    //coParse.resume(this, null);
                }

                @Override
                public void resumeResponse(CoRoutine requester, Object signal) {
                    if(signal != null && signal instanceof Pair) {
                        Pair signalAsPair = (Pair)signal;

                        if(signalAsPair.current.equals("next")) {
                            Object next = signalAsPair.next.current;
                            System.out.println(next);

                            // Parse next
                            scheduler.resume(this, coParse, null);
                            //requester.resume(this, null);
                        } else if(signalAsPair.current.equals("atEnd")) {

                        }
                    } else {
                        // Parse next
                        scheduler.resume(this, coParse, null);
                        //requester.resume(this, null);
                    }
                }
            }, null
        );

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

            /*list("define", "myFunc", list("fun", list("quote", list("*", 3, 7)))),
            list("myFunc")*/
            list("+", 1, 2),
            list("+", 1, 4)
        );

        Evaluator evaluator2 = new Evaluator(env);
        CoRoutine coProgram = evaluator2.eval(scheduler, program);
        scheduler.resume(new CoCaller() {
            @Override
            public void resumeResponse(CoRoutine requester, Object signal) {
                System.err.println("Response: " + signal);
            }

            @Override
            public void resumeError(CoRoutine requester, Object signal) {
                System.err.println("Error: " + signal);
            }
        }, coProgram, null);

    }
}
