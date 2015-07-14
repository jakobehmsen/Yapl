package yaplstack;

import yaplstack.ast.AST;
import yaplstack.ast.Generator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static yaplstack.ast.AST.Factory.*;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException {
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

        String sourceCode = "( 1 ) 34";

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

        if()

        */

        AST program = program(block(
            defun("println", new String[]{"str"},
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), invoke(load("str"), Object.class.getMethod("toString")))
            ),

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
        ));

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
        Instruction[] instructions = Generator.toInstructions(program);

        Thread thread = new Thread(new CallFrame(instructions));

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
        Object result = thread.evalAll().callFrame.pop();

        System.out.println(result);
    }
}
