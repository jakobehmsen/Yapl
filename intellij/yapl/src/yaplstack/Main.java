package yaplstack;

import yaplstack.ast.AST;
import yaplstack.ast.Generator;
import yaplstack.ast.Selector;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static yaplstack.ast.AST.Factory.*;

public class Main {
    public static void main(String[] args) throws Exception {
        /*
        What primivites are necessary to simulate tradition fn calls?

        // Three registers:
        // Operand frame
        // Call frame
        // Current environment

        // Store all registers:
        // [..., operand frame, visitApply frame, environment]
        // storeEnvironment
        // [..., operand frame, visitApply frame]
        // storeCallFrame
        // [..., operand frame]
        // storeOperandFrame
        // [...']

        */

        /*
        Create reader:
        {
            inputStream = ...
            atEnd() => inputStream.available() <= 0;
            next() => inputStream.read();
        }
        */

        // push nth element in stack

        /*

        {
            var currentChar = null;
            peek = function() {
                return currentChar;
            }
            consume = function() {
                if(inputStream.hasMore())
                    currentChar = inputStream.nextChar();
                else
                    currentChar = '\0';
            }
            nextToken = function() {
                ignore();

                if(Character.isDigit(peek())) {
                    StringBuilder digits = new StringBuilder();
                    digits.append(peek());
                    consume();

                    while(Character.isDigit(peek())) {
                        StringBuilder digits = new StringBuilder();
                        digits.append(peek());
                        consume();

                        return {
                            type = "Integer";
                            value = Integer.parseInt(digits.toString());
                        };
                    }
                } else if(peek() == '(') {
                    return {
                        type = "OpenPar";
                    };
                } else if(peek() == ')') {
                    return {
                        type = "ClosePar";
                    };
                }

                return null;
            }
        }

        or:

        tokens = function(inputStream) {
            var currentChar = null;
            peek = function() {
                return currentChar;
            }
            consume = function() {
                if(inputStream.hasMore())
                    currentChar = inputStream.nextChar();
                else
                    currentChar = '\0';
            }
            ignore = ...

            ignore();

            while(inputStream.hasMore()) {
                if(Character.isDigit(peek())) {
                    StringBuilder digits = new StringBuilder();
                    digits.append(peek());
                    consume();

                    while(Character.isDigit(peek())) {
                        StringBuilder digits = new StringBuilder();
                        digits.append(peek());
                        consume();

                        return {
                            type = "Integer";
                            value = Integer.parseInt(digits.toString());
                        };
                    }
                } else if(peek() == '(') {
                    yield {
                        type = "OpenPar";
                    };
                } else if(peek() == ')') {
                    return {
                        type = "ClosePar";
                    };
                }
            }
        }

        defun threeNumbers(m) {
            m.yield(1);
            m.yield(2);
            m.yield(3);
        }

        defun generate(producer) {
            {
                var producer = producer;
                var hasNext = false;
                var atEnd = () -> {!hasNext};
                var next = () -> {
                    var res = current;
                    hasNext = false;
                    yielder.returnFrame = frame;
                    resume(yielder.yieldFrame, nil);
                    return res;
                };
                var yielder = producer({
                    var returnFrame = nil;
                    var yieldFrame = nil;
                    yield = value -> {
                        hasNext = true;
                        current = value;
                        yieldFrame = frame;
                        resume(returnFrame, nil);
                    }
                };
                yielder.returnFrame = frame;
                var current;
                () -> {
                   producer(yielder);
                   resume(yielder.returnFrame, nil);
                }()
            }
        }

        var generator1 = generate(threeNumbers);

        while(!generator1.atEnd()) {
            println(generator1.next());
        }

        */

        /*AST program = program(block(
            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),
            defun("myMsg", block(
                local("str", literal("Hello2")),
                send(env(), "println", load("str")),
                literal("Hurray")
            )),
            call("println", literal("Hello")),
            send(env(), "myMsg")
        ));*/

        /*AST program = program(block(
            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),

            defun("numbers", new String[]{"m"}, block(
                local("i", literal(0)),
                loop(lti(load("i"), literal(100)), block(
                    send(load("m"), "yield", load("i")),
                    store("i", addi(load("i"), literal(1)))
                ))
            )),

            defun("generate", new String[]{"producer"}, block(
                local("generator", object(block(
                    local("producer", load("producer")),
                    local("hasNext", literal(false)),
                    defun("atEnd", not(load("hasNext"))),
                    defun("next", block(
                        local("res", load("current")),
                        store("hasNext", literal(false)),
                        store("returnFrame", frame),
                        resume(load("yieldFrame"), literal(null)),
                        load("res")
                    )),
                    local("returnFrame", literal(false)),
                    local("yieldFrame", literal(false)),
                    defun("yield", new String[]{"value"}, block(
                        store("hasNext", literal(true)),
                        store("current", load("value")),
                        store("yieldFrame", frame),
                        resume(load("returnFrame"), literal(null))
                    )),
                    local("current", literal(false)),
                    local("returnFrame", frame)
                ))),
                apply(fn(new String[]{"producer", "generator"}, block(
                    call("producer", load("generator")),
                    resume(load(load("generator"), "returnFrame"), literal(null))
                )), load("producer"), load("generator")),
                load("generator")
            )),

            local("gen", call("generate", load("numbers"))),

            loop(not(send(load("gen"), "atEnd")), block(
                call("println", send(load("gen"), "next"))
            ))
        ));*/

        /*

        tokens(input) {
            return m -> {
                // input = <frame-load-var: 0>
                var current = null;
                while(input.hasNext()) {
                    current = input.next();
                    if(isDigit(current)) {

                    }
                }
            }
        }

        */


        /*AST program = program(block(
            local("point", object(
                field("x", literal(7)),
                field("y", literal(9)),
                method("area", muli(load("x"), load("y"))),
                method("area", new String[]{"z"}, muli(muli(load("x"), load("y")), load("z")))
            )),
            //send(load("point"), "area")
            //send(load("point"), "area", literal(11))
            load("point")
        ));*/

        /*
        defun newClosure(x) {
            var y = 5;

            return {
                f = frame,
                call:z => {
                    frameload(f, 0) * frameload(f, 1) * z;
                }
            };
        }

        (x, y) -> {x * y}
        =>
        {
            call:x, y => {x * y}
        }

        function = (x, y) -> {x * y}

        function(2, 3)
        =>
        function.call(2, 3)

        */

        // Add support for store for closures
        // Test 2+ levels of closure'ing
        /*AST program = program(block(
            defun("newClosure", new String[]{"x"}, block(
                local("y", literal(10)),
                fn(new String[]{"z"}, muli(muli(load("x"), load("y")), load("z")))
            )),

            local("closure", call("newClosure", literal(7))),
            apply(load("closure"), literal(8))

            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),

            defun("newClosure", new String[]{"x"}, block(
                local("y", literal(0)),
                fn(new String[]{}, block(
                    store("y", addi(load("y"), load("x")))
                ))
            )),

            local("closure", call("newClosure", literal(5))),

            call("println", apply(load("closure"))),
            call("println", apply(load("closure"))),
            call("println", apply(load("closure")))
        ));*/

        /*AST program = program(block(
            local("obj0", object(
                method("mth0", block(
                    local("x", literal(7)),
                    object(
                        method("mth1", fn(
                            //load("x")
                            store("x", addi(load("x"), literal(3)))
                        ))
                    )
                ))
            )),

            local("closure", send(send(load("obj0"), "mth0"), "mth1")),

            apply(load("closure")),
            apply(load("closure")),
            apply(load("closure"))
        ));*/

        /*AST program = program(block(
            defun("myFunction", literal("Hello World")),
            call("myFunction"),

            defun("newClosure", new String[]{"x"}, block(
                local("y", literal(5)),

                object(
                    field("f", frame),
                    method("call", new String[]{"z"}, muli(frameLoad(load("f"), 1), muli(frameLoad(load("f"), 2), load("z"))))
                )
            )),

            local("closure", call("newClosure", literal(7))),

            apply(load("closure"), literal(9)),

            apply(fn(new String[]{"x", "y"}, muli(load("x"), load("y"))), literal(7), literal(9))
        ));*/

        /*AST program = program(block(
            defun("myFunction", block(
                local("i", literal(2)),
                defun("innerFunc", new String[]{"x"}, addi(load("x"), load("i"))),
                call("innerFunc", literal(7))
            )),
            call("myFunction")
        ));*/

        /*AST program = program(block(
            local("obj", object(
                method("atEnd", literal(true))
            )),

            local("j", literal(false)),
            loop(
                and(
                    not(send(load("obj"), "atEnd")),
                    invoke(Character.class.getMethod("isWhitespace", char.class), literal('H'))
                ),
                block(
                    store("j", literal(true))
                )
            )
        ));*/

        /*AST program = program(block(
            local("i", literal(0)),
            loop(
                lti(load("i"), literal(10)),
                store("i", addi(load("i"), literal(1)))
            )
        ));*/



        /*String sourceCode =
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n";
        InputStream sourceCodeInputStream = new ByteArrayInputStream(sourceCode.getBytes());
        Reader sourceCodeInputStreamReader = new InputStreamReader(sourceCodeInputStream);

        AST program = program(block(
            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),

            defun("generate", new String[]{"producer"}, block(
                local("generator", object(
                    field("producer", load("producer")),
                    field("hasNext", literal(false)),
                    method("atEnd", not(load("hasNext"))),
                    method("next", block(
                        local("res", load("current")),
                        store("hasNext", literal(false)),
                        store("returnFrame", frame),
                        resume(load("yieldFrame"), literal(null)),
                        load("res")
                    )),
                    field("returnFrame", literal(false)),
                    field("yieldFrame", literal(false)),
                    method("yield", new String[]{"value"}, block(
                        store("hasNext", literal(true)),
                        store("current", load("value")),
                        store("yieldFrame", frame),
                        resume(load("returnFrame"), literal(null))
                    )),
                    field("current", literal(false)),
                    field("returnFrame", frame)
                )),
                apply(fn(new String[]{"producer", "generator"}, block(
                    apply(load("producer"), load("generator")),
                    resume(load(load("generator"), "returnFrame"), literal(null))
                )), load("producer"), load("generator")),
                load("generator")
            )),

            defun("buffer", new String[]{"stream"}, object(
                field("items", newInstance(ArrayList.class.getConstructor())),
                field("index", literal(0)),
                method("consume", store("index", addi(load("index"), literal(1)))),
                method("atEnd", block(
                    send(env(), "ensureBuffered"),
                    not(lti(load("index"), invoke(load("items"), ArrayList.class.getMethod("size"))))
                )),
                method("peek", send(env(), "la", literal(0))),
                method("peek1", send(env(), "la", literal(1))),
                method("la", new String[]{"i"}, block(
                    block(
                        send(env(), "ensureBuffered"),
                        test(lti(load("index"), invoke(load("items"), ArrayList.class.getMethod("size"))),
                            invoke(load("items"), ArrayList.class.getMethod("get", int.class), load("index")),
                            literal(false)
                        )
                    )
                )),
                method("ensureBuffered", block(
                    loop(and(not(send(load("stream"), "atEnd")), not(lti(load("index"), invoke(load("items"), ArrayList.class.getMethod("size"))))), block(
                        invoke(load("items"), ArrayList.class.getMethod("add", Object.class), send(load("stream"), "next"))
                    ))
                ))
            )),

            defun("chars", new String[]{"reader"}, fn(new String[]{"m"}, block(
                    local("b", literal(0)),
                    loop(
                        not(eqi(store("b", invoke(load("reader"), Reader.class.getMethod("read"))), literal(-1))),
                        block(
                            local("ch", load("b")),
                            send(load("m"), "yield", itoc(load("ch")))
                        )
                    )))
            ),

            defun("tokens", new String[]{"chars"}, fn(new String[]{"m"}, block(
                defun("ignore", block(
                    loop(
                        and(
                            not(send(load("chars"), "atEnd")),
                            invoke(Character.class.getMethod("isWhitespace", char.class), send(load("chars"), "peek"))
                        ),
                        block(
                            send(load("chars"), "consume")
                        )
                    )
                )),

                call("ignore"),

                loop(not(send(load("chars"), "atEnd")), block(
                    local("token", literal(false)),

                    test(
                        eqc(send(load("chars"), "peek"), literal('(')),
                        block(
                            store("token", object(field("type", literal("OPEN_PAR")))),
                            send(load("chars"), "consume")
                        ),
                        test(
                            eqc(send(load("chars"), "peek"), literal(')')),
                            block(
                                store("token", object(field("type", literal("CLOSE_PAR")))),
                                send(load("chars"), "consume")
                            ),
                            test(
                                eqc(send(load("chars"), "peek"), literal('\"')),
                                block(
                                    send(load("chars"), "consume"),

                                    local("stringBuilder", newInstance(StringBuilder.class.getConstructor())),
                                    invoke(load("stringBuilder"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                    send(load("chars"), "consume"),

                                    loop(
                                        and(
                                            not(send(load("chars"), "atEnd")),
                                            not(eqc(send(load("chars"), "peek"), literal('\"')))
                                        ),
                                        block(
                                            invoke(load("stringBuilder"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                            send(load("chars"), "consume")
                                        )
                                    ),

                                    send(load("chars"), "consume"),

                                    store("token", object(
                                        field("type", literal("STRING")),
                                        field("value", invoke(load("stringBuilder"), StringBuilder.class.getMethod("toString")))
                                    ))
                                ),
                                test(
                                    invoke(Character.class.getMethod("isDigit", char.class), send(load("chars"), "peek")),
                                    block(
                                        local("digits", newInstance(StringBuilder.class.getConstructor())),
                                        invoke(load("digits"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                        send(load("chars"), "consume"),

                                        loop(
                                            and(
                                                not(send(load("chars"), "atEnd")),
                                                invoke(Character.class.getMethod("isDigit", char.class), send(load("chars"), "peek"))
                                            ),
                                            block(
                                                invoke(load("digits"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                                send(load("chars"), "consume")
                                            )
                                        ),

                                        store("token", object(
                                            field("type", literal("INT")),
                                            field("value", invoke(Integer.class.getMethod("parseInt", String.class), invoke(load("digits"), StringBuilder.class.getMethod("toString"))))
                                        ))
                                    ),
                                    test(
                                        invoke(Character.class.getMethod("isJavaIdentifierStart", char.class), send(load("chars"), "peek")),
                                        block(
                                            local("stringBuilder", newInstance(StringBuilder.class.getConstructor())),
                                            invoke(load("stringBuilder"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                            send(load("chars"), "consume"),

                                            loop(
                                                and(
                                                    not(send(load("chars"), "atEnd")),
                                                    invoke(Character.class.getMethod("isJavaIdentifierPart", char.class), send(load("chars"), "peek"))
                                                ),
                                                block(
                                                    invoke(load("stringBuilder"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                                    send(load("chars"), "consume")
                                                )
                                            ),

                                            store("token", object(
                                                field("type", literal("WORD")),
                                                field("value", invoke(load("stringBuilder"), StringBuilder.class.getMethod("toString")))
                                            ))
                                        )
                                    )
                                )
                            )
                        )
                    ),

                    send(load("m"), "yield", load("token")),
                    call("ignore")
                ))
            ))),

            local("charsGen", call("buffer", call("generate", call("chars", literal(sourceCodeInputStreamReader))))),
            local("tokensGen", call("generate", call("tokens", load("charsGen")))),

            loop(not(send(load("tokensGen"), "atEnd")), block(
                call("println", send(load("tokensGen"), "next"))
            ))
        ));*/



        /*AST program = program(block(
            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),

            local("numbers", fn(new String[]{"m"}, block(
                local("i", literal(0)),
                loop(lti(load("i"), literal(100)), block(
                    send(load("m"), "yield", load("i")),
                    store("i", addi(load("i"), literal(1)))
                ))
            ))),

            defun("generate", new String[]{"producer"}, block(
                local("generator", object(
                    field("producer", load("producer")),
                    field("hasNext", literal(false)),
                    method("atEnd", not(load("hasNext"))),
                    method("next", block(
                        local("res", load("current")),
                        store("hasNext", literal(false)),
                        store("returnFrame", frame),
                        resume(load("yieldFrame"), literal(null)),
                        load("res")
                    )),
                    field("returnFrame", literal(false)),
                    field("yieldFrame", literal(false)),
                    method("yield", new String[]{"value"}, block(
                        store("hasNext", literal(true)),
                        store("current", load("value")),
                        store("yieldFrame", frame),
                        resume(load("returnFrame"), literal(null))
                    )),
                    field("current", literal(false)),
                    field("returnFrame", frame)
                )),
                apply(fn(new String[]{"producer", "generator"}, block(
                    apply(load("producer"), load("generator")),
                    resume(load(load("generator"), "returnFrame"), literal(null))
                )), load("producer"), load("generator")),
                load("generator")
            )),

            local("gen", call("generate", load("numbers"))),

            loop(not(send(load("gen"), "atEnd")), block(
                call("println", send(load("gen"), "next"))
            ))
        ));*/

        /*AST program = program(block(
            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),

            defun("tokens", new String[]{"m"}, block(
                local("i", literal(0)),
                loop(lti(load("i"), literal(100)), block(
                    // Load closed locals in side by side of arguments
                    send(load("m"), "yield", load("i")),
                    store("i", addi(load("i"), literal(1)))
                ))
            )),

            defun("generate", new String[]{"producer"}, block(
                local("generator", object(block(
                    local("producer", load("producer")),
                    local("hasNext", literal(false)),
                    defun("atEnd", not(load("hasNext"))),
                    defun("next", block(
                        local("res", load("current")),
                        store("hasNext", literal(false)),
                        store("returnFrame", frame),
                        resume(load("yieldFrame"), literal(null)),
                        load("res")
                    )),
                    local("returnFrame", literal(false)),
                    local("yieldFrame", literal(false)),
                    defun("yield", new String[]{"value"}, block(
                        store("hasNext", literal(true)),
                        store("current", load("value")),
                        store("yieldFrame", frame),
                        resume(load("returnFrame"), literal(null))
                    )),
                    local("current", literal(false)),
                    local("returnFrame", frame)
                ))),
                apply(fn(new String[]{"producer", "generator"}, block(
                    call("producer", load("generator")),
                    resume(load(load("generator"), "returnFrame"), literal(null))
                )), load("producer"), load("generator")),
                load("generator")
            )),

            local("gen", call("generate", load("numbers"))),

            loop(not(send(load("gen"), "atEnd")), block(
                call("println", send(load("gen"), "next"))
            ))
        ));*/




        /*String sourceCode = "( 1 ) 34";

        AST program = program(block(
            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),

            call("println", literal("Hello World")),

            local("inputStream", object(block(
                local("input", literal(new ByteArrayInputStream(sourceCode.getBytes()))),
                defun("next", invoke(load("input"), InputStream.class.getMethod("read"))),
                defun("nextChar", itoc(call("next"))),
                defun("hasMore", gti(invoke(load("input"), InputStream.class.getMethod("available")), literal(0)))
            ))),

            local("scanner", object(block(
                defun("nextToken", invoke(load("inputStream"), InputStream.class.getMethod("read"))),
                defun("hasMoreTokens", gti(invoke(load("input"), InputStream.class.getMethod("available")), literal(0)))
            ))),

            loop(send(load("inputStream"), "hasMore"),
                call("println", send(load("inputStream"), "nextChar"))
            )
        ));*/

        /*AST program = program(block(
            local("myPoint", object(block(
                local("x", literal(3)),
                local("y", literal(5)),
                defun("someFunc", new String[]{}, muli(load("x"), load("y"))),
                defun("setX", new String[]{"x"}, store(outerEnv(env()), "x", load("x"))),
                defun("setX2", new String[]{"x"}, block(
                    local("tmpX", addi(load("x"), literal(1))),
                    store(env(), "x", load("tmpX"))
                ))
            ))),

            defun("strconcat", new String[]{"x", "y"}, invoke(load("x"), String.class.getMethod("concat", String.class), load("y"))),
            defun("exclamator", new String[]{"x"}, call("strconcat", load("x"), literal("!!!"))),
            call("exclamator", literal("Hello world")),

            on(load("myPoint"), call("setX2", literal(77))),
            on(load("myPoint"), load("x"))
        ));*/



        /*AST program = program(block(
            local("myFunc", fn(new String[]{"x", "y"},
                addi(load("x"), load("y"))
            )),
            local("sq", fn(new String[]{"x"},
                muli(load("x"), literal(2))
            )),
            pushCallFrame(load("myFunc"), pushCallFrame(load("sq"), literal(5)), literal(6))
        ));*/
        /*AST program = program(block(
            local("x", literal(5)),
            local("y", literal(9)),
            test(lti(load("x"), load("y")),
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), literal("YES!!!")),
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), literal("No..."))
            )
        ));*/
        /*AST program = program(block(
            local("x", literal(0)),
            loop(lti(load("x"), literal(10)),
                store("x", addi(load("x"), literal(1)))
            ),
            load("x")
        ));*/

        /*Thread thread = new Thread(new CallFrame(new Instruction[] {
            loadEnvironment,
            newInstance(ArrayList.class.getConstructor()),
            local("list"),

            loadEnvironment,
            load("list"),
            loadConst(5),
            invoke(ArrayList.class.getMethod("add", Object.class)),

            loadEnvironment,
            load("list"),
            loadConst(7),
            invoke(ArrayList.class.getMethod("add", Object.class)),

            loadEnvironment,
            load("list"),
            finish
        }));*/

        /*Thread thread = new Thread(new CallFrame(new Instruction[] {
            loadEnvironment,
            loadConst(0),
            local("i"),
            loadEnvironment,
            load("i"),
            loadConst(10),
            lti,
            not,
            jumpIfTrue(17),
            loadEnvironment,
            loadEnvironment,
            load("i"),
            loadConst(1),
            addi,
            store("i"),
            loadConst(true),
            jumpIfTrue(3),
            loadEnvironment,
            load("i"),
            finish
        }));*/

        /*Thread thread = new Thread(new CallFrame(new Instruction[] {
            loadEnvironment,
            dup,
            loadConst(new Instruction[]{
                loadEnvironment,
                extendEnvironment,
                storeEnvironment,
                pushOperandFrame(0),

                loadEnvironment,
                loadConst(5),
                local("x"),
                loadConst(7),
                loadEnvironment,
                load("x"),
                addi,

                popOperandFrame(1),
                loadEnvironment,
                outerEnvironment,
                storeEnvironment,
                popCallFrame
            }),
            local("myFunc"),
            load("myFunc"),
            visitApply,
            finish
        }));*/

        ArrayList<Long> timings = new ArrayList<>();

        for(int i = 0; i < 1; i++) {
            System.out.println("************************Eval************************");
            AST program = ast();

            SymbolTable symbolTable = new SymbolTable();

            int onExceptionCode = symbolTable.getCode(Selector.get("onException", 2));
            int frameCode = symbolTable.getCode("__frame__");

            /*
            try {
                sdfsdf
            } {
                onException: frame, exception
            }
            */

            BiConsumer<Thread, Throwable> exceptionHandlerCode = (t, e) -> {
                Environment exceptionHandler = (Environment)t.callFrame.get(0); // Exception handler is located in var 0
                CallFrame exceptionHandlerFrame = (CallFrame)exceptionHandler.load(frameCode);
                Environment exceptionHandlerExceptionHandler = (Environment)exceptionHandlerFrame.get(0);
                Environment exceptionHandlerSelf = (Environment)exceptionHandlerFrame.get(1);

                CodeSegment exceptionHandlerBody = (CodeSegment)exceptionHandler.load(onExceptionCode);
                CallFrame exceptionFrame = new CallFrame(exceptionHandlerFrame.outer, exceptionHandlerBody);
                exceptionFrame.push(exceptionHandlerExceptionHandler);
                exceptionFrame.push(exceptionHandlerSelf);
                exceptionFrame.push(t.callFrame); // Frame is first argument
                exceptionFrame.push(e); // Exception is second argument

                t.callFrame = exceptionFrame;
            };

            CodeSegment codeSegment = Generator.toInstructions(program);
            CallFrame callFrame = new CallFrame(codeSegment);
            Environment exceptionHandler = new Environment();
            exceptionHandler.store(onExceptionCode, new CodeSegment(5, new Instruction[]{
                Instruction.Factory.loadVar(3), // Load exception
                Instruction.Factory.haltException
            }, null));
            // Create dummy call frame that conforms to the expected structure of an exception handler frame
            CallFrame exceptionHandlerFrame = new CallFrame(null, new CodeSegment(2, new Instruction[0], null));
            exceptionHandlerFrame.push(null); // Exception handler
            exceptionHandlerFrame.push(null); // Self
            exceptionHandler.store(frameCode, exceptionHandlerFrame);
            callFrame.push(exceptionHandler);
            callFrame.push(new Environment());
            Thread thread = new Thread(symbolTable, exceptionHandlerCode, callFrame);

            long start = System.currentTimeMillis();
            thread.evalAll();
            long end = System.currentTimeMillis();
            //System.out.println("Elapsed: " + (end - start) + "ms");
            timings.add(end - start);
            Object result = thread.callFrame.pop();

            //System.out.println(result);
        }

        double elapsed = timings.stream().mapToInt(x -> x.intValue()).average().getAsDouble();
        System.out.println("Elapsed: " + elapsed + "ms");
    }

    private static AST ast() throws Exception {
        if(1 != 2) {
            return program(block(
                // Provoke exception
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("print", String.class), literal(false))
            ));
        }

        String sourceCode =
            "(muli (muli 2 3) 4)\n" +
            "(addi 4 5)";
            /*" (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n" +
            " (word  34534 ) \"str\" \n";*/
        //InputStream sourceCodeInputStream = new ByteArrayInputStream(sourceCode.getBytes());
        InputStream sourceCodeInputStream = System.in;
        Reader sourceCodeInputStreamReader = new InputStreamReader(sourceCodeInputStream);

        AST program = program(block(
            defun("print", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("print", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),
            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),

            defun("generate", new String[]{"producer"}, block(
                local("generator", object(
                    field("producer", load("producer")),
                    field("hadNext", literal(false)),
                    field("started", literal(false)),
                    method("hadNext", load("hadNext")),
                    method("next", block(
                        store("hadNext", literal(false)),
                        store("returnFrame", frame),

                        test(load("started"),
                            resume(load("yieldFrame"), literal(null)),
                            block(
                                store("started", literal(true)),

                                apply(fn(block(
                                    apply(load("producer"), load("generator")),
                                    resume(load(load("generator"), "returnFrame"), literal(null))
                                )))
                            )
                        ),

                        load("current")
                    )),
                    field("returnFrame", literal(false)),
                    field("yieldFrame", literal(false)),
                    method("yield", new String[]{"value"}, block(
                        store("hadNext", literal(true)),
                        store("current", load("value")),
                        store("yieldFrame", frame),
                        resume(load("returnFrame"), literal(null))
                    )),
                    field("current", literal(false)),
                    field("returnFrame", frame)
                )),
                load("generator")
            )),

            defun("buffer", new String[]{"stream"}, object(
                field("items", newInstance(ArrayList.class.getConstructor())),
                field("index", literal(0)),
                field("atEndOfStream", literal(false)),
                method("consume", store("index", addi(load("index"), literal(1)))),
                method("atEnd", block(
                    send(env(), "ensureBuffered"),
                    not(lti(load("index"), invoke(load("items"), ArrayList.class.getMethod("size"))))
                )),
                method("peek", send(env(), "la", literal(0))),
                method("peek1", send(env(), "la", literal(1))),
                method("la", new String[]{"i"}, block(
                    block(
                        send(env(), "ensureBuffered"),
                        test(lti(load("index"), invoke(load("items"), ArrayList.class.getMethod("size"))),
                            invoke(load("items"), ArrayList.class.getMethod("get", int.class), load("index")),
                            literal(false)
                        )
                    )
                )),
                method("ensureBuffered", block(
                    loop(and(not(load("atEndOfStream")), not(lti(load("index"), invoke(load("items"), ArrayList.class.getMethod("size"))))), block(
                        local("next", send(load("stream"), "next")),
                        test(
                            send(load("stream"), "hadNext"),
                            invoke(load("items"), ArrayList.class.getMethod("add", Object.class), load("next")),
                            store("atEndOfStream", literal(true))
                        )
                    ))
                ))
            )),

            defun("chars", new String[]{"reader"}, fn(new String[]{"m"}, block(
                local("b", literal(0)),
                loop(
                    not(eqi(store("b", invoke(load("reader"), Reader.class.getMethod("read"))), literal(-1))),
                    block(
                        local("ch", load("b")),
                        send(load("m"), "yield", itoc(load("ch")))
                    )
                )))
            ),

            defun("tokens", new String[]{"chars"}, fn(new String[]{"m"}, block(
                defun("ignore", block(
                    loop(
                        and(
                            not(send(load("chars"), "atEnd")),
                            invoke(Character.class.getMethod("isWhitespace", char.class), send(load("chars"), "peek"))
                        ),
                        block(
                            send(load("chars"), "consume")
                        )
                    )
                )),

                call("ignore"),

                loop(not(send(load("chars"), "atEnd")), block(
                    local("token", literal(false)),

                    test(
                        eqc(send(load("chars"), "peek"), literal('(')),
                        block(
                            store("token", object(
                                field("type", literal("OPEN_PAR")),
                                field("typeCode", literal(0))
                            )),
                            send(load("chars"), "consume")
                        ),
                        test(
                            eqc(send(load("chars"), "peek"), literal(')')),
                            block(
                                store("token", object(
                                    field("type", literal("CLOSE_PAR")),
                                    field("typeCode", literal(1))
                                )),
                                send(load("chars"), "consume")
                            ),
                            test(
                                eqc(send(load("chars"), "peek"), literal('\"')),
                                block(
                                    send(load("chars"), "consume"),

                                    local("stringBuilder", newInstance(StringBuilder.class.getConstructor())),
                                    invoke(load("stringBuilder"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                    send(load("chars"), "consume"),

                                    loop(
                                        and(
                                            not(send(load("chars"), "atEnd")),
                                            not(eqc(send(load("chars"), "peek"), literal('\"')))
                                        ),
                                        block(
                                            invoke(load("stringBuilder"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                            send(load("chars"), "consume")
                                        )
                                    ),

                                    send(load("chars"), "consume"),

                                    store("token", object(
                                        field("type", literal("STRING")),
                                        field("typeCode", literal(2)),
                                        field("value", invoke(load("stringBuilder"), StringBuilder.class.getMethod("toString")))
                                    ))
                                ),
                                test(
                                    invoke(Character.class.getMethod("isDigit", char.class), send(load("chars"), "peek")),
                                    block(
                                        local("digits", newInstance(StringBuilder.class.getConstructor())),
                                        invoke(load("digits"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                        send(load("chars"), "consume"),

                                        loop(
                                            and(
                                                not(send(load("chars"), "atEnd")),
                                                invoke(Character.class.getMethod("isDigit", char.class), send(load("chars"), "peek"))
                                            ),
                                            block(
                                                invoke(load("digits"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                                send(load("chars"), "consume")
                                            )
                                        ),

                                        store("token", object(
                                            field("type", literal("INT")),
                                            field("typeCode", literal(3)),
                                            field("value", invoke(Integer.class.getMethod("parseInt", String.class), invoke(load("digits"), StringBuilder.class.getMethod("toString"))))
                                        ))
                                    ),
                                    test(
                                        invoke(Character.class.getMethod("isJavaIdentifierStart", char.class), send(load("chars"), "peek")),
                                        block(
                                            local("stringBuilder", newInstance(StringBuilder.class.getConstructor())),
                                            invoke(load("stringBuilder"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                            send(load("chars"), "consume"),

                                            loop(
                                                and(
                                                    not(send(load("chars"), "atEnd")),
                                                    invoke(Character.class.getMethod("isJavaIdentifierPart", char.class), send(load("chars"), "peek"))
                                                ),
                                                block(
                                                    invoke(load("stringBuilder"), StringBuilder.class.getMethod("append", char.class), send(load("chars"), "peek")),
                                                    send(load("chars"), "consume")
                                                )
                                            ),

                                            store("token", object(
                                                field("type", literal("WORD")),
                                                field("typeCode", literal(4)),
                                                field("value", invoke(load("stringBuilder"), StringBuilder.class.getMethod("toString")))
                                            ))
                                        )
                                    )
                                )
                            )
                        )
                    ),

                    send(load("m"), "yield", load("token")),
                    call("ignore")
                ))
            ))),

            defun("nodes", new String[]{"tokens"}, fn(new String[]{"m"}, block(
                defun("parseNode", block(
                    local("node", literal(false)),

                    test(
                        eqi(load(send(load("tokens"), "peek"), "typeCode"), literal(0)), // OPEN_PAR
                        block(
                            send(load("tokens"), "consume"),
                            test(
                                eqi(load(send(load("tokens"), "peek"), "typeCode"), literal(4)), // WORD
                                block(
                                    local("operator", load(send(load("tokens"), "peek"), "value")),
                                    send(load("tokens"), "consume"),
                                    local("operands", newInstance(ArrayList.class.getConstructor())),
                                    loop(
                                        and(
                                            not(send(load("tokens"), "atEnd")),
                                            not(eqi(load(send(load("tokens"), "peek"), "typeCode"), literal(1))) // not CLOSE_PAR
                                        ),
                                        block(
                                            local("operand", call("parseNode")),
                                            invoke(load("operands"), ArrayList.class.getMethod("add", Object.class), load("operand"))
                                        )
                                    ),
                                    send(load("tokens"), "consume"), // consume CLOSE_PAR
                                    store("node",
                                        invoke(AST.Parse.class.getMethod("parseOperation", String.class, List.class), load("operator"), load("operands"))
                                    )
                                )
                            )
                        ),
                        test(
                            eqi(load(send(load("tokens"), "peek"), "typeCode"), literal(2)), // STRING
                            block(
                                store("node",
                                    invoke(AST.Factory.class.getMethod("literal", Object.class), load(send(load("tokens"), "peek"), "value"))
                                ),
                                send(load("tokens"), "consume")
                            ),
                            test(
                                eqi(load(send(load("tokens"), "peek"), "typeCode"), literal(3)), // INTEGER
                                block(
                                    store("node",
                                        invoke(AST.Factory.class.getMethod("literal", Object.class), load(send(load("tokens"), "peek"), "value"))
                                    ),
                                    send(load("tokens"), "consume")
                                ),
                                test(
                                    eqi(load(send(load("tokens"), "peek"), "typeCode"), literal(4)), // WORD
                                    block(
                                        store("node",
                                            invoke(AST.Factory.class.getMethod("load", String.class), load(send(load("tokens"), "peek"), "value"))
                                        ),
                                        send(load("tokens"), "consume")
                                    )
                                )
                            )
                        )
                    ),

                    load("node")
                )),

                loop(not(send(load("tokens"), "atEnd")), block(
                    local("node", call("parseNode")),
                    send(load("m"), "yield", load("node"))
                ))
            ))),

            local("charsGen", call("buffer", call("generate", call("chars", literal(sourceCodeInputStreamReader))))),
            local("tokensGen", call("buffer", call("generate", call("tokens", load("charsGen"))))),
            local("nodesGen", call("generate", call("nodes", load("tokensGen")))),

            local("atEndOfStream", literal(false)),
            loop(not(load("atEndOfStream")), block(
                call("print", literal("> ")),
                local("node", send(load("nodesGen"), "next")),
                test(
                    send(load("nodesGen"), "hadNext"),
                    block(
                        local("nodeAsCode", invoke(Generator.class.getMethod("toEvalInstructions", AST.class), load("node"))),
                        local("evalResult", eval(load("nodeAsCode"))),
                        call("println", load("evalResult"))
                    ),
                    store("atEndOfStream", literal(true))
                )
            ))



//            while more nodes {
//
//                try {
//                    var node = nodes.next()
//                    var nodeAsCode = invoke(method("Generator", "toEvalInstructions", class(AST), node)
//                    var evalResult = eval(nodeAsCode)
//                } catch(InvalidCharException e) {
//                    println e
//                    chars.consume();
//                } catch(SyntaxErrorException e) {
//                    println e
//                    tokens.consume();
//                }
//
//            }


        ));

        return program;
    }
}
