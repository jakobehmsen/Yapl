package yaplstack;

import yaplstack.ast.AST;
import yaplstack.ast.Generator;

import java.io.PrintStream;

import static yaplstack.ast.AST.Factory.*;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException {
        /*
        What primivites are necessary to simulate tradition function calls?

        // Three registers:
        // Operand frame
        // Call frame
        // Current environment

        // Store all registers:
        // [..., operand frame, visitCall frame, environment]
        // storeEnvironment
        // [..., operand frame, visitCall frame]
        // storeCallFrame
        // [..., operand frame]
        // storeOperandFrame
        // [...']

        */

        /*AST program = program(block(
            local("myFunc", function(new String[]{"x", "y"},
                addi(load("x"), load("y"))
            )),
            local("sq", function(new String[]{"x"},
                muli(load("x"), literal(2))
            )),
            call(load("myFunc"), call(load("sq"), literal(5)), literal(6))
        ));*/
        AST program = program(block(
            local("x", literal(5)),
            local("y", literal(9)),
            test(lti(load("x"), load("y")),
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), literal("YES!!!")),
                invoke(fieldGet(System.class.getField("out")), PrintStream.class.getMethod("println", String.class), literal("No..."))
                //literal(true),
                //literal(false)
            )
        ));
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
                extend,
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
                outer,
                storeEnvironment,
                ret
            }),
            local("myFunc"),
            load("myFunc"),
            visitCall,
            finish
        }));*/
        Object result = thread.evalAll().operandFrame.pop();

        System.out.println(result);
    }
}
