package yaplstack;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Instruction {
    void eval(Thread thread);

    interface IncIP extends Instruction {
        default void eval(Thread thread) {
            doEval(thread);
            thread.frame.incrementIP();
        }
        void doEval(Thread thread);
    }

    class Factory {
        public static IncIP push(Object obj) {
            return thread -> thread.frame.push(obj);
        }

        public static IncIP pop = thread -> thread.frame.pop();

        public static Instruction finish = thread -> thread.setFinished();

        private static <T, R, S> IncIP binaryReducer(BiFunction<R, T, S> reducer) {
            return thread -> {
                R operand2 = (R)thread.frame.pop();
                T operand1 = (T)thread.frame.pop();
                S res = reducer.apply(operand2, operand1);
                thread.frame.push(res);
            };
        }

        public static IncIP addi = binaryReducer((Integer lhs, Integer rhs) -> lhs + rhs);
        public static IncIP subi = binaryReducer((Integer lhs, Integer rhs) -> lhs - rhs);
        public static IncIP muli = binaryReducer((Integer lhs, Integer rhs) -> lhs * rhs);
        public static IncIP divi = binaryReducer((Integer lhs, Integer rhs) -> lhs / rhs);

        public static IncIP store(String name) {
            return thread -> {
                Object value = thread.frame.pop();
                thread.frame.environment.store(name, value);
            };
        }

        public static IncIP load(String name) {
            return thread -> {
                Object value = thread.frame.environment.load(name);
                thread.frame.push(value);
            };
        }

        public static IncIP call() {
            return thread -> {
                Instruction[] instructions = (Instruction[])thread.frame.pop();
                thread.frame = new Frame(thread.frame, thread.frame.environment, instructions);
            };
        }

        public static IncIP ret() {
            return thread -> {
                Frame frame = thread.frame;
                thread.frame = thread.frame.outer;
                thread.frame.push(frame.pop());
            };
        }
    }
}
