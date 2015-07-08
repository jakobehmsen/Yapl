package yaplstack;

import java.util.function.BiFunction;

public interface Instruction {
    void eval(Thread thread);

    interface IncIP extends Instruction {
        default void eval(Thread thread) {
            doEval(thread);
            thread.callFrame.incrementIP();
        }
        void doEval(Thread thread);
    }

    class Factory {
        public static IncIP push(Object obj) {
            return thread -> thread.operandFrame.push(obj);
        }

        public static IncIP pop = thread -> thread.operandFrame.pop();

        public static Instruction finish = thread -> thread.setFinished();

        private static <T, R, S> IncIP binaryReducer(BiFunction<R, T, S> reducer) {
            return thread -> {
                R operand2 = (R)thread.operandFrame.pop();
                T operand1 = (T)thread.operandFrame.pop();
                S res = reducer.apply(operand2, operand1);
                thread.operandFrame.push(res);
            };
        }

        public static IncIP addi = binaryReducer((Integer lhs, Integer rhs) -> lhs + rhs);
        public static IncIP subi = binaryReducer((Integer lhs, Integer rhs) -> lhs - rhs);
        public static IncIP muli = binaryReducer((Integer lhs, Integer rhs) -> lhs * rhs);
        public static IncIP divi = binaryReducer((Integer lhs, Integer rhs) -> lhs / rhs);

        public static IncIP store(String name) {
            return thread -> {
                Object value = thread.operandFrame.pop();
                thread.environment.store(name, value);
            };
        }

        public static IncIP load(String name) {
            return thread -> {
                Object value = thread.environment.load(name);
                thread.operandFrame.push(value);
            };
        }

        public static Instruction call = thread -> {
            // Both an operand stack and an operation stack?
            Instruction[] instructions = (Instruction[])thread.operandFrame.pop();
            thread.callFrame = new CallFrame(thread.callFrame, instructions);
        };

        public static Instruction ret = thread -> {
            thread.callFrame = thread.callFrame.outer;
            thread.callFrame.incrementIP();
        };
    }
}
