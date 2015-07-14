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
            return thread -> thread.callFrame.push(obj);
        }

        public static IncIP dup = thread -> thread.callFrame.dup();
        public static IncIP dupx1 = thread -> thread.callFrame.dupx1();
        public static IncIP pop = thread -> thread.callFrame.pop();
        public static IncIP swap = thread -> thread.callFrame.swap();

        public static Instruction finish = thread -> thread.setFinished();

        public static IncIP local(String name) {
            return thread -> {
                Object value = thread.callFrame.pop();
                Environment environment = (Environment)thread.callFrame.pop();
                environment.local(name, value);
            };
        }

        public static IncIP store(String name) {
            return thread -> {
                Object value = thread.callFrame.pop();
                Environment environment = (Environment)thread.callFrame.pop();
                environment.store(name, value);
            };
        }

        public static IncIP load(String name) {
            return thread -> {
                Environment environment = (Environment)thread.callFrame.pop();
                Object value = environment.load(name);
                thread.callFrame.push(value);
            };
        }

        public static IncIP storeVar(int ordinal) {
            return thread -> {
                Object value = thread.callFrame.pop();
                thread.callFrame.set(ordinal, value);
            };
        }

        public static IncIP loadVar(int ordinal) {
            return thread -> {
                Object value = thread.callFrame.get(ordinal);
                thread.callFrame.push(value);
            };
        }

        /*public static IncIP loadOperandFrame = thread ->
            thread.callFrame.push(thread.callFrame);

        public static IncIP storeOperandFrame = thread ->
            thread.callFrame = (OperandFrame)thread.callFrame.pop();*/

        public static IncIP loadEnvironment = thread ->
            thread.callFrame.push(thread.callFrame.environment);

        public static IncIP storeEnvironment = thread ->
            thread.callFrame.environment = (Environment)thread.callFrame.pop();

        public static IncIP loadCallFrame = thread ->
            thread.callFrame.push(thread.callFrame);

        public static IncIP storeCallFrame = thread ->
            thread.callFrame = (CallFrame)thread.callFrame.pop();
        public static IncIP loadCurrentContinuation = thread ->
            thread.callFrame.push(thread.callFrame);


        public static Instruction resume(int pushCount) {
            return thread -> {
                CallFrame target = (CallFrame)thread.callFrame.pop();
                thread.callFrame.pushTo(target, pushCount);
                target.incrementIP();
                thread.callFrame = target;
            };
        }

        public static Instruction pushCallFrame = thread -> {
            Instruction[] instructions = (Instruction[])thread.callFrame.pop();
            thread.callFrame = new CallFrame(thread.callFrame.environment, thread.callFrame, instructions);
        };

        public static Instruction pushCallFrame(int pushCount) {
            return thread -> {
                Instruction[] instructions = (Instruction[])thread.callFrame.pop();
                thread.callFrame = new CallFrame(thread.callFrame.environment, thread.callFrame, instructions);
                thread.callFrame.outer.pushTo(thread.callFrame, pushCount);
            };
        }

        public static Instruction pushCallFrameFrom = thread -> {
            Instruction[] instructions = (Instruction[])thread.callFrame.pop();
            CallFrame callFrame = (CallFrame)thread.callFrame.pop();
            thread.callFrame = new CallFrame(thread.callFrame.environment, callFrame, instructions);
        };

        public static Instruction pushConditionalCallFrame = thread -> {
            boolean condition = (boolean)thread.callFrame.pop();;
            Instruction[] instructionsIfFalse = (Instruction[])thread.callFrame.pop();
            Instruction[] instructionsIfTrue = (Instruction[])thread.callFrame.pop();

            if(condition)
                thread.callFrame = new CallFrame(thread.callFrame.environment, thread.callFrame, instructionsIfTrue);
            else
                thread.callFrame = new CallFrame(thread.callFrame.environment, thread.callFrame, instructionsIfFalse);
        };

        public static Instruction jumpIfTrue(int index) {
            return thread -> {
                boolean condition = (boolean)thread.callFrame.pop();
                if(condition)
                    thread.callFrame.setIP(index);
                else
                    thread.callFrame.incrementIP();
            };
        };

        public static Instruction popCallFrame = thread -> {
            thread.callFrame = thread.callFrame.outer;
            thread.callFrame.incrementIP();
        };

        public static Instruction popCallFrame(int pushCount) {
            return thread -> {
                thread.callFrame.outer.pushTo(thread.callFrame, pushCount);
                thread.callFrame = thread.callFrame.outer;
                thread.callFrame.incrementIP();
            };
        }


        /*public static IncIP pushOperandFrame(int pushCount) {
            return thread -> {
                OperandFrame operandFrame = new OperandFrame(thread.callFrame);
                thread.callFrame.pushTo(operandFrame, pushCount);
                thread.callFrame = operandFrame;
            };
        }

        public static IncIP popOperandFrame(int popCount) {
            return thread -> {
                OperandFrame operandFrame = thread.callFrame.outer;
                thread.callFrame.pushTo(operandFrame, popCount);
                thread.callFrame = operandFrame;
            };
        }*/

        public static IncIP extendEnvironment = thread -> {
            Environment environment = (Environment)thread.callFrame.pop();
            thread.callFrame.push(new Environment(environment));
        };

        public static IncIP outerEnvironment = thread -> {
            Environment environment = (Environment)thread.callFrame.pop();
            thread.callFrame.push(environment.outer);
        };

        public static IncIP outerCallFrame = thread -> {
            CallFrame callFrame = (CallFrame)thread.callFrame.pop();
            thread.callFrame.push(callFrame.outer);
        };

        public static IncIP callFrameInstructions = thread -> {
            CallFrame callFrame = (CallFrame)thread.callFrame.pop();
            thread.callFrame.push(callFrame.instructions);
        };

        private static <T, R> IncIP unaryReducer(Function<T, R> reducer) {
            return thread -> {
                T operand = (T)thread.callFrame.pop();
                R res = reducer.apply(operand);
                thread.callFrame.push(res);
            };
        }

        private static <T, R, S> IncIP binaryReducer(BiFunction<T, R, S> reducer) {
            return thread -> {
                R operand2 = (R)thread.callFrame.pop();
                T operand1 = (T)thread.callFrame.pop();
                S res = reducer.apply(operand1, operand2);
                thread.callFrame.push(res);
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

        public static IncIP itoc = unaryReducer((Integer i) -> (char) i.intValue());

        public static IncIP newInstance(Constructor<?> constructor) {
            return thread -> {
                int argCount = constructor.getParameterCount();
                Object[] args = new Object[argCount];
                for(int i = argCount - 1; i >= 0; i--)
                    args[i] = thread.callFrame.pop();
                Object res = null;
                try {
                    res = constructor.newInstance(args);
                    thread.callFrame.push(res);
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
                    args[i] = thread.callFrame.pop();
                Object obj = instance ? thread.callFrame.pop() : null;
                Object res = null;
                try {
                    res = method.invoke(obj, args);
                    thread.callFrame.push(res);
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
                Object obj = instance ? thread.callFrame.pop() : null;
                Object res = null;
                try {
                    res = field.get(obj);
                    thread.callFrame.push(res);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                // How to handle exceptions?
            };
        }

        public static IncIP fieldSet(Field field) {
            boolean instance = !Modifier.isStatic(field.getModifiers());

            return thread -> {
                Object value = thread.callFrame.pop();
                Object obj = instance ? thread.callFrame.pop() : null;
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
