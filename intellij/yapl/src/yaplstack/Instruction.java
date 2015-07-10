package yaplstack;

import java.lang.reflect.*;
import java.util.function.BiFunction;
import java.util.function.Function;

// Instructions should be replaced by first class objects, that are designed to be ("more") interpretable
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


        public static IncIP loadConst(Object obj) {
            return thread -> thread.operandFrame.push(obj);
        }

        public static IncIP dup = thread -> thread.operandFrame.dup();
        public static IncIP dupx1 = thread -> thread.operandFrame.dupx1();
        public static IncIP pop = thread -> thread.operandFrame.pop();
        public static IncIP swap = thread -> thread.operandFrame.swap();

        public static Instruction finish = thread -> thread.setFinished();

        public static Instruction jumpIfTrue(int ip) {
            return thread -> {
                boolean condition = (boolean)thread.operandFrame.pop();
                if(condition)
                    thread.callFrame.setIP(ip);
                else
                    thread.callFrame.incrementIP();
            };
        }

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

        private static <T, R> IncIP unaryReducer(Function<T, R> reducer) {
            return thread -> {
                T operand = (T)thread.operandFrame.pop();
                R res = reducer.apply(operand);
                thread.operandFrame.push(res);
            };
        }

        private static <T, R, S> IncIP binaryReducer(BiFunction<T, R, S> reducer) {
            return thread -> {
                R operand2 = (R)thread.operandFrame.pop();
                T operand1 = (T)thread.operandFrame.pop();
                S res = reducer.apply(operand1, operand2);
                thread.operandFrame.push(res);
            };
        }

        public static IncIP not = unaryReducer((Boolean b) -> !b);
        public static IncIP and = binaryReducer((Boolean lhs, Boolean rhs) -> lhs && rhs);;
        public static IncIP or = binaryReducer((Boolean lhs, Boolean rhs) -> lhs || rhs);;

        public static IncIP addi = binaryReducer((Integer lhs, Integer rhs) -> lhs + rhs);
        public static IncIP subi = binaryReducer((Integer lhs, Integer rhs) -> lhs - rhs);
        public static IncIP muli = binaryReducer((Integer lhs, Integer rhs) -> lhs * rhs);
        public static IncIP divi = binaryReducer((Integer lhs, Integer rhs) -> lhs / rhs);
        public static IncIP lti = binaryReducer((Integer lhs, Integer rhs) -> lhs < rhs);
        public static IncIP gti = binaryReducer((Integer lhs, Integer rhs) -> lhs > rhs);
        public static IncIP eqi = binaryReducer((Integer lhs, Integer rhs) -> lhs == rhs);

        public static IncIP newInstance(Constructor<?> constructor) {
            return thread -> {
                int argCount = constructor.getParameterCount();
                Object[] args = new Object[argCount];
                for(int i = argCount - 1; i >= 0; i--)
                    args[i] = thread.operandFrame.pop();
                Object res = null;
                try {
                    res = constructor.newInstance(args);
                    thread.operandFrame.push(res);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                // How to handle exceptions?
            };
        }

        public static IncIP invoke(Method method) {
            boolean instance = !Modifier.isStatic(method.getModifiers());

            return thread -> {
                int argCount = method.getParameterCount();
                Object[] args = new Object[argCount];
                for(int i = argCount - 1; i >= 0; i--)
                    args[i] = thread.operandFrame.pop();
                Object obj = instance ? thread.operandFrame.pop() : null;
                Object res = null;
                try {
                    res = method.invoke(obj, args);
                    thread.operandFrame.push(res);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                // How to handle exceptions?
            };
        }

        public static IncIP fieldGet(Field field) {
            boolean instance = !Modifier.isStatic(field.getModifiers());

            return thread -> {
                Object obj = instance ? thread.operandFrame.pop() : null;
                Object res = null;
                try {
                    res = field.get(obj);
                    thread.operandFrame.push(res);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                // How to handle exceptions?
            };
        }

        public static IncIP fieldSet(Field field) {
            boolean instance = !Modifier.isStatic(field.getModifiers());

            return thread -> {
                Object value = thread.operandFrame.pop();
                Object obj = instance ? thread.operandFrame.pop() : null;
                try {
                    field.set(obj, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                // How to handle exceptions?
            };
        }
    }
}
