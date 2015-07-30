package yaplstack;

import java.lang.reflect.*;
import java.util.function.BiFunction;
import java.util.function.Function;

// Instructions should be replaced by first class objects, that are designed to be ("more") interpretable
public interface Instruction {
    void eval(Thread thread);

    int popCount();
    int pushCount();

    default int[] nextIPs(int ip) {
        return new int[]{0};
    }

    interface IncIP extends Instruction {
        @Override
        default int[] nextIPs(int ip) {
            return new int[]{ip + 1};
        }

        default void eval(Thread thread) {
            doEval(thread);
            thread.callFrame.incrementIP();
        }
        void doEval(Thread thread);
    }

    class Factory {
        public static IncIP loadConst(Object obj) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    thread.callFrame.push(obj);
                }

                @Override
                public int popCount() {
                    return 0;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "loadConst(" + obj + ")";
                }
            };
        }

        public static IncIP dup = new IncIP() {
            @Override
            public void doEval(Thread thread) {
                thread.callFrame.dup();
            }

            @Override
            public int popCount() {
                return 0;
            }

            @Override
            public int pushCount() {
                return 1;
            }

            @Override
            public String toString() {
                return "dup";
            }
        };

        public static IncIP dupx1down = new IncIP() {
            @Override
            public void doEval(Thread thread) {
                thread.callFrame.dupx1down();
            }

            @Override
            public int popCount() {
                return 0;
            }

            @Override
            public int pushCount() {
                return 1;
            }

            @Override
            public String toString() {
                return "dupx1down";
            }
        };

        public static IncIP pop = new IncIP() {
            @Override
            public void doEval(Thread thread) {
                thread.callFrame.pop();
            }

            @Override
            public int popCount() {
                return 1;
            }

            @Override
            public int pushCount() {
                return 0;
            }

            @Override
            public String toString() {
                return "pop";
            }
        };

        public static IncIP dupx(int delta) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    thread.callFrame.dupx(delta);
                }

                @Override
                public int popCount() {
                    return 0;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "dupx(" + delta + ")";
                }
            };
        }

        public static IncIP bp = new IncIP() {
            @Override
            public void doEval(Thread thread) {
                new String();
            }

            @Override
            public int popCount() {
                return 0;
            }

            @Override
            public int pushCount() {
                return 0;
            }

            @Override
            public String toString() {
                return "bp";
            }
        };

        public static Instruction resume(int pushCount) {
            return new Instruction() {
                @Override
                public void eval(Thread thread) {
                    CallFrame target = (CallFrame)thread.callFrame.pop();
                    thread.callFrame.pushTo(target, pushCount);
                    target.incrementIP();
                    thread.callFrame = target;
                }

                @Override
                public int[] nextIPs(int ip) {
                    return new int[]{ip + 1};
                }

                @Override
                public int popCount() {
                    return pushCount;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "resume(" + pushCount + ")";
                }
            };
        }

        public static Instruction finish = new Instruction() {
            @Override
            public void eval(Thread thread) {
                thread.setFinished();
            }

            @Override
            public int popCount() {
                return 0;
            }

            @Override
            public int pushCount() {
                return 0;
            }

            @Override
            public int[] nextIPs(int ip) {
                return new int[0];
            }

            @Override
            public String toString() {
                return "finish";
            }
        };

        public static Instruction store(String name) {
            return new Instruction() {
                @Override
                public void eval(Thread thread) {
                    int code = thread.symbolTable.getCode(name);
                    thread.callFrame.codeSegment.instructions[thread.callFrame.ip] = store(code);
                }

                @Override
                public int[] nextIPs(int ip) {
                    return new int[]{ip + 1};
                }

                @Override
                public int popCount() {
                    return 2;
                }

                @Override
                public int pushCount() {
                    return 0;
                }

                @Override
                public String toString() {
                    return "store(" + name + ")";
                }
            };
        }

        public static IncIP store(int code) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    Object value = thread.callFrame.pop();
                    Environment environment = (Environment)thread.callFrame.pop();
                    environment.store(code, value);
                }

                @Override
                public int popCount() {
                    return 2;
                }

                @Override
                public int pushCount() {
                    return 0;
                }

                @Override
                public String toString() {
                    return "store(" + code + ")";
                }
            };
        }

        public static Instruction load(String name) {
            return new Instruction() {
                @Override
                public void eval(Thread thread) {
                    int code = thread.symbolTable.getCode(name);
                    thread.callFrame.codeSegment.instructions[thread.callFrame.ip] = load(code);
                }

                @Override
                public int[] nextIPs(int ip) {
                    return new int[]{ip + 1};
                }

                @Override
                public int popCount() {
                    return 1;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "load(" + name + ")";
                }
            };
        }

        public static IncIP load(int code) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    Environment environment = (Environment)thread.callFrame.pop();
                    Object value = environment.load(code);
                    if(value == null)
                        throw new RuntimeException("\"" + thread.symbolTable.getSymbol(code) + "\" is undefined.");
                    thread.callFrame.push(value);
                }

                @Override
                public int popCount() {
                    return 1;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "load(" + code + ")";
                }
            };
        }

        public static Instruction loadd(String name) {
            return new Instruction() {
                @Override
                public void eval(Thread thread) {
                    int code = thread.symbolTable.getCode(name);
                    thread.callFrame.codeSegment.instructions[thread.callFrame.ip] = loadd(code);
                }

                @Override
                public int[] nextIPs(int ip) {
                    return new int[]{ip + 1};
                }

                @Override
                public int popCount() {
                    return 0;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "loadd(" + name + ")";
                }
            };
        }

        public static IncIP loadd(int code) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    // Load via "dynamic scope"
                    CallFrame frame = thread.callFrame;

                    while(true) {
                        Environment environment = (Environment)frame.get(0);
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
                }

                @Override
                public int popCount() {
                    return 0;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "loadd(" + code + ")";
                }
            };
        }

        public static IncIP storeVar(int ordinal) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    Object value = thread.callFrame.pop();
                    thread.callFrame.set(ordinal, value);
                }

                @Override
                public int popCount() {
                    return 1;
                }

                @Override
                public int pushCount() {
                    return 0;
                }

                @Override
                public String toString() {
                    return "storeVar(" + ordinal + ")";
                }
            };
        }

        public static IncIP loadVar(int ordinal) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    Object value = thread.callFrame.get(ordinal);
                    thread.callFrame.push(value);
                }

                @Override
                public int popCount() {
                    return 0;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "loadVar(" + ordinal + ")";
                }
            };
        }

        public static IncIP frameStoreVar(int ordinal) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    Object value = thread.callFrame.pop();
                    CallFrame frame = (CallFrame)thread.callFrame.pop();
                    frame.set(ordinal, value);
                }

                @Override
                public int popCount() {
                    return 2;
                }

                @Override
                public int pushCount() {
                    return 0;
                }

                @Override
                public String toString() {
                    return "frameStoreVar(" + ordinal + ")";
                }
            };
        }

        public static IncIP frameLoadVar(int ordinal) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    CallFrame frame = (CallFrame)thread.callFrame.pop();
                    Object value = frame.get(ordinal);
                    thread.callFrame.push(value);
                }

                @Override
                public int popCount() {
                    return 1;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "frameLoadVar(" + ordinal + ")";
                }
            };
        }

        public static IncIP newEnvironment = new IncIP() {
            @Override
            public void doEval(Thread thread) {
                thread.callFrame.push(new Environment());
            }

            @Override
            public int popCount() {
                return 0;
            }

            @Override
            public int pushCount() {
                return 1;
            }

            @Override
            public String toString() {
                return "newEnvironment";
            }
        };

        public static IncIP loadCallFrame = new IncIP() {
            @Override
            public void doEval(Thread thread) {
                thread.callFrame.push(thread.callFrame);
            }

            @Override
            public int popCount() {
                return 0;
            }

            @Override
            public int pushCount() {
                return 1;
            }

            @Override
            public String toString() {
                return "loadCallFrame";
            }
        };

        // Special send instruction?
        public static Instruction pushCallFrame(int pushCount) {
            return new Instruction() {
                @Override
                public void eval(Thread thread) {
                    CodeSegment codeSegment = (CodeSegment)thread.callFrame.pop();
                    if(codeSegment == null)
                        throw new NullPointerException();
                    thread.callFrame = new CallFrame(thread.callFrame, codeSegment);
                    thread.callFrame.outer.pushTo(thread.callFrame, pushCount);
                }

                @Override
                public int[] nextIPs(int ip) {
                    return new int[]{ip + 1};
                }

                @Override
                public int popCount() {
                    return pushCount;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "pushCallFrame(" + pushCount + ")";
                }
            };
        }

        public static Instruction jumpIfTrue(int index) {
            return new Instruction() {
                @Override
                public void eval(Thread thread) {
                    boolean condition = (boolean)thread.callFrame.pop();
                    if(condition)
                        thread.callFrame.setIP(index);
                    else
                        thread.callFrame.incrementIP();
                }

                @Override
                public int popCount() {
                    return 1;
                }

                @Override
                public int pushCount() {
                    return 0;
                }

                @Override
                public int[] nextIPs(int ip) {
                    return new int[]{index, ip + 1};
                }

                @Override
                public String toString() {
                    return "jumpIfTrue(" + index + ")";
                }
            };
        };

        public static Instruction popCallFrame(int pushCount) {
            return new Instruction() {
                @Override
                public void eval(Thread thread) {
                    thread.callFrame.pushTo(thread.callFrame.outer, pushCount);
                    thread.callFrame = thread.callFrame.outer;
                    thread.callFrame.incrementIP();
                }

                @Override
                public int popCount() {
                    return pushCount;
                }

                @Override
                public int pushCount() {
                    return 0;
                }

                @Override
                public int[] nextIPs(int ip) {
                    return new int[0];
                }

                @Override
                public String toString() {
                    return "popCallFrame(" + pushCount + ")";
                }
            };
        }

        private static <T, R> IncIP unaryReducer(Function<T, R> reducer) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    T operand = (T)thread.callFrame.pop();
                    R res = reducer.apply(operand);
                    thread.callFrame.push(res);
                }

                @Override
                public int popCount() {
                    return 1;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "unaryReducer: " + reducer;
                }
            };
        }

        private static <T, R, S> IncIP binaryReducer(BiFunction<T, R, S> reducer) {
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    R operand2 = (R)thread.callFrame.pop();
                    T operand1 = (T)thread.callFrame.pop();
                    S res = reducer.apply(operand1, operand2);
                    thread.callFrame.push(res);
                }

                @Override
                public int popCount() {
                    return 2;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "binaryReducer: " + reducer;
                }
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
            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
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
                }

                @Override
                public int popCount() {
                    return constructor.getParameterCount();
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "newInstance(" + constructor + ")";
                }
            };
        }

        public static IncIP invoke(Method method) {
            boolean instance = !Modifier.isStatic(method.getModifiers());

            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
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
                    } catch(IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch(NullPointerException e) {
                        e.printStackTrace();
                    }

                    // How to handle exceptions?
                }

                @Override
                public int popCount() {
                    int popCount = method.getParameterCount();

                    if(instance)
                        popCount++;

                    return popCount;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "invoke(" + method + ")";
                }
            };
        }

        public static IncIP fieldGet(Field field) {
            boolean instance = !Modifier.isStatic(field.getModifiers());

            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    Object obj = instance ? thread.callFrame.pop() : null;
                    Object res = null;
                    try {
                        res = field.get(obj);
                        thread.callFrame.push(res);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    // How to handle exceptions?
                }

                @Override
                public int popCount() {
                    return instance ? 1 : 0;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "fieldGet(" + field + ")";
                }
            };
        }

        public static IncIP fieldSet(Field field) {
            boolean instance = !Modifier.isStatic(field.getModifiers());

            return new IncIP() {
                @Override
                public void doEval(Thread thread) {
                    Object value = thread.callFrame.pop();
                    Object obj = instance ? thread.callFrame.pop() : null;
                    try {
                        field.set(obj, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    // How to handle exceptions?
                }

                @Override
                public int popCount() {
                    return instance ? 2 : 1;
                }

                @Override
                public int pushCount() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "fieldSet(" + field + ")";
                }
            };
        }
    }
}
