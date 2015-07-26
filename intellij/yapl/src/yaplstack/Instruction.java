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
        public static IncIP dupx1down = thread -> thread.callFrame.dupx1down();
        public static IncIP pop = thread -> thread.callFrame.pop();

        public static IncIP dupx(int delta) {
            return thread ->
                thread.callFrame.dupx(delta);
        }

        public static IncIP bp = thread ->
            new String();

        public static Instruction resume(int pushCount) {
            return thread -> {
                CallFrame target = (CallFrame)thread.callFrame.pop();
                thread.callFrame.pushTo(target, pushCount);
                target.incrementIP();
                thread.callFrame = target;
            };
        }

        public static Instruction finish = thread -> thread.setFinished();

        public static Instruction store(String name) {
            return thread -> {
                int code = thread.symbolTable.getCode(name);
                thread.callFrame.instructions[thread.callFrame.ip] = store(code);
            };
        }

        public static IncIP store(int code) {
            return thread -> {
                Object value = thread.callFrame.pop();
                Environment environment = (Environment)thread.callFrame.pop();
                environment.store(code, value);
            };
        }

        public static Instruction load(String name) {
            return thread -> {
                int code = thread.symbolTable.getCode(name);
                thread.callFrame.instructions[thread.callFrame.ip] = load(code);
            };
        }

        public static IncIP load(int code) {
            return thread -> {
                Environment environment = (Environment)thread.callFrame.pop();
                Object value = environment.load(code);
                if(value == null)
                    throw new RuntimeException("\"" + thread.symbolTable.getSymbol(code) + "\" is undefined.");
                thread.callFrame.push(value);
            };
        }

        public static Instruction loadd(String name) {
            return thread -> {
                int code = thread.symbolTable.getCode(name);
                thread.callFrame.instructions[thread.callFrame.ip] = loadd(code);
            };
        }

        public static IncIP loadd(int code) {
            return thread -> {
                // Load via "dynamic scope"
                CallFrame frame = thread.callFrame;

                while(true) {
                    Environment environment = (Environment)frame.stack.get(0);
                    Object value = environment.load(code);

                    if(value != null) {
                        thread.callFrame.push(value);
                        return;
                    }

                    frame = frame.outer;
                    if(frame == null)
                        break;
                }

                throw new RuntimeException("\"" + thread.symbolTable.getSymbol(code) + "\" is undefined.");
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

        public static IncIP frameStoreVar(int ordinal) {
            return thread -> {
                Object value = thread.callFrame.pop();
                CallFrame frame = (CallFrame)thread.callFrame.pop();
                frame.set(ordinal, value);
            };
        }

        public static IncIP frameLoadVar(int ordinal) {
            return thread -> {
                CallFrame frame = (CallFrame)thread.callFrame.pop();
                Object value = frame.get(ordinal);
                thread.callFrame.push(value);
            };
        }

        public static IncIP newEnvironment = thread ->
            thread.callFrame.push(new Environment());

        public static IncIP loadCallFrame = thread ->
            thread.callFrame.push(thread.callFrame);

        public static Instruction pushCallFrame(int pushCount) {
            return thread -> {
                Instruction[] instructions = (Instruction[])thread.callFrame.pop();
                if(instructions == null)
                    throw new NullPointerException();
                thread.callFrame = new CallFrame(thread.callFrame, instructions);
                thread.callFrame.outer.pushTo(thread.callFrame, pushCount);
            };
        }

        public static Instruction jumpIfTrue(int index) {
            return thread -> {
                boolean condition = (boolean)thread.callFrame.pop();
                if(condition)
                    thread.callFrame.setIP(index);
                else
                    thread.callFrame.incrementIP();
            };
        };

        public static Instruction popCallFrame(int pushCount) {
            return thread -> {
                thread.callFrame.pushTo(thread.callFrame.outer, pushCount);
                thread.callFrame = thread.callFrame.outer;
                thread.callFrame.incrementIP();
            };
        }

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
        public static IncIP eqi = binaryReducer((Integer lhs, Integer rhs) -> (int)lhs == (int)rhs);
        public static IncIP eqc = binaryReducer((Character lhs, Character rhs) -> (char)lhs == (char)rhs);

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
                if(method.getName().equals("isWhitespace"))
                    new String();

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
