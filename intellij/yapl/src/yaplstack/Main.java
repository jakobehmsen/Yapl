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

        Load operand frame
        Store operand frame
        Push operand frame
        Pop operand frame

        Load current environment
        Store current environment
        Extend environment
        Store in environment
        Load from environment

        call/push call frame
        ret/pop call frame

        */

        Object result = new Thread(new CallFrame(new Instruction[] {
            push(new Instruction[] {
                push(5),
                store("x"),
                push(7),
                load("x"),
                addi,
                ret
            }),
            store("myFunc"),
            load("myFunc"),
            call(),
            finish
        })).evalAll().operandFrame.pop();

        System.out.println(result);
    }
}
