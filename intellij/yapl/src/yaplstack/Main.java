package yaplstack;

import static yaplstack.Instruction.Factory.*;

public class Main {
    public static void main(String[] args) {
        /*
        What primivites are necessary to simulate tradition function calls?

        // Three registers:
        // Operand frame
        // Call frame
        // Current environment

        // Store all registers:
        // [..., operand frame, call frame, environment]
        // storeEnvironment
        // [..., operand frame, call frame]
        // storeCallFrame
        // [..., operand frame]
        // storeOperandFrame
        // [...']

        */

        Thread thread = new Thread(new CallFrame(new Instruction[] {
            loadEnvironment,
            dup,
            load(new Instruction[]{
                loadEnvironment,
                extend,
                storeEnvironment,
                pushOperandFrame(0),

                loadEnvironment,
                load(5),
                local("x"),
                load(7),
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
            call,
            finish
        }));
        Object result = thread.evalAll().operandFrame.pop();

        System.out.println(result);
    }
}
