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
        public static IncIP load(Object obj) {
            return thread -> thread.operandFrame.push(obj);
        }

        public static IncIP dup = thread -> thread.operandFrame.dup();

        public static IncIP pop = thread -> thread.operandFrame.pop();

        public static Instruction finish = thread -> thread.setFinished();

        public static IncIP local(String name) {
            return thread -> {
                Object value = thread.operandFrame.pop();
                Environment environment = (Environment)thread.operandFrame.pop();
                environment.local(name, value);
            };
        }

        public static IncIP store(String name) {
            return thread -> {
                Object value = thread.operandFrame.pop();
                Environment environment = (Environment)thread.operandFrame.pop();
                environment.store(name, value);
            };
        }

        public static IncIP load(String name) {
            return thread -> {
                Environment environment = (Environment)thread.operandFrame.pop();
                Object value = environment.load(name);
                thread.operandFrame.push(value);
            };
        }

        public static IncIP loadOperandFrame = thread ->
            thread.operandFrame.push(thread.operandFrame);

        public static IncIP storeOperandFrame = thread ->
            thread.operandFrame = (OperandFrame)thread.operandFrame.pop();

        public static IncIP loadEnvironment = thread ->
            thread.operandFrame.push(thread.environment);

        public static IncIP storeEnvironment = thread ->
            thread.environment = (Environment)thread.operandFrame.pop();

        public static IncIP loadCallFrame = thread ->
            thread.operandFrame.push(thread.callFrame);

        public static IncIP storeCallFrame = thread ->
            thread.callFrame = (CallFrame)thread.operandFrame.pop();

        public static Instruction call = thread -> {
            Instruction[] instructions = (Instruction[])thread.operandFrame.pop();
            thread.callFrame = new CallFrame(thread.callFrame, instructions);
        };

        public static Instruction ret = thread -> {
            thread.callFrame = thread.callFrame.outer;
            thread.callFrame.incrementIP();
        };

        public static IncIP pushOperandFrame(int pushCount) {
            return thread -> {
                OperandFrame operandFrame = new OperandFrame(thread.operandFrame);
                thread.operandFrame.pushTo(operandFrame, pushCount);
                thread.operandFrame = operandFrame;
            };
        }

        public static IncIP popOperandFrame(int popCount) {
            return thread -> {
                OperandFrame operandFrame = thread.operandFrame.outer;
                thread.operandFrame.pushTo(operandFrame, popCount);
                thread.operandFrame = operandFrame;
            };
        }

        public static IncIP extend = thread -> {
            Environment environment = (Environment)thread.operandFrame.pop();
            thread.operandFrame.push(new Environment(environment));
        };

        public static IncIP outer = thread -> {
            Environment environment = (Environment)thread.operandFrame.pop();
            thread.operandFrame.push(environment.outer);
        };

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
    }
}
