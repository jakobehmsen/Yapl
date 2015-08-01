package yaplstack.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface AST extends Node {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visitProgram(AST code);
        T visitBlock(List<AST> code);
        T visitLiteral(Object obj);
        T visitAddi(AST lhs, AST rhs);
        T visitSubi(AST lhs, AST rhs);
        T visitMuli(AST lhs, AST rhs);
        T visitDivi(AST lhs, AST rhs);
        T visitLti(AST lhs, AST rhs);
        T visitGti(AST lhs, AST rhs);
        T visitEqi(AST lhs, AST rhs);
        T visitNewInstance(Constructor constructor, List<AST> args);
        T visitInvoke(Method method, List<AST> args);
        T visitInvoke(AST target, Method method, List<AST> args);
        T visitFieldGet(Field field);
        T visitFieldGet(AST target, Field field);
        T visitFieldSet(Field field, AST value);
        T visitFieldSet(AST target, Field field, AST value);
        T visitLocal(AST target, String name, AST value);
        T visitLocal(String name, AST value);
        T visitStore(AST target, String name, AST value);
        T visitStore(String name, AST value);
        T visitLoad(AST target, String name);
        T visitLoad(String name);
        T visitTest(AST condition, AST ifTrue, AST ifFalse);
        T visitLoop(AST condition, AST body);
        T visitEnv();
        T visitItoc(AST i);
        T visitApplyCC(AST target);
        T visitResume(AST target, AST value);
        T visitFrame();
        T visitRet(AST expression);
        T visitNot(AST expression);
        T visitBP();
        T visitSend(AST target, String name, List<AST> arguments);
        T visitObject(List<Slot> slots);
        T visitEqc(AST lhs, AST rhs);
        T visitLoadD(String name);
        T visitAnd(AST lhs, AST rhs);
        T visitOr(AST lhs, AST rhs);
        T visitEval(AST target);

        class Default<T> implements Visitor<T> {
            @Override
            public T visitProgram(AST code) {
                return null;
            }

            @Override
            public T visitBlock(List<AST> code) {
                return null;
            }

            @Override
            public T visitLiteral(Object obj) {
                return null;
            }

            @Override
            public T visitAddi(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitSubi(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitMuli(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitDivi(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitLti(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitGti(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitEqi(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitNewInstance(Constructor constructor, List<AST> args) {
                return null;
            }

            @Override
            public T visitInvoke(Method method, List<AST> args) {
                return null;
            }

            @Override
            public T visitInvoke(AST target, Method method, List<AST> args) {
                return null;
            }

            @Override
            public T visitFieldGet(Field field) {
                return null;
            }

            @Override
            public T visitFieldGet(AST target, Field field) {
                return null;
            }

            @Override
            public T visitFieldSet(Field field, AST value) {
                return null;
            }

            @Override
            public T visitFieldSet(AST target, Field field, AST value) {
                return null;
            }

            @Override
            public T visitLocal(AST target, String name, AST value) {
                return null;
            }

            @Override
            public T visitLocal(String name, AST value) {
                return null;
            }

            @Override
            public T visitStore(AST target, String name, AST value) {
                return null;
            }

            @Override
            public T visitStore(String name, AST value) {
                return null;
            }

            @Override
            public T visitLoad(AST target, String name) {
                return null;
            }

            @Override
            public T visitLoad(String name) {
                return null;
            }

            @Override
            public T visitTest(AST condition, AST ifTrue, AST ifFalse) {
                return null;
            }

            @Override
            public T visitLoop(AST condition, AST body) {
                return null;
            }

            @Override
            public T visitEnv() {
                return null;
            }

            @Override
            public T visitItoc(AST i) {
                return null;
            }

            @Override
            public T visitApplyCC(AST target) {
                return null;
            }

            @Override
            public T visitResume(AST target, AST value) {
                return null;
            }

            @Override
            public T visitFrame() {
                return null;
            }

            @Override
            public T visitRet(AST expression) {
                return null;
            }

            @Override
            public T visitNot(AST expression) {
                return null;
            }

            @Override
            public T visitBP() {
                return null;
            }

            @Override
            public T visitSend(AST target, String name, List<AST> arguments) {
                return null;
            }

            @Override
            public T visitObject(List<Slot> slots) {
                return null;
            }

            @Override
            public T visitEqc(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitLoadD(String name) {
                return null;
            }

            @Override
            public T visitAnd(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitOr(AST lhs, AST rhs) {
                return null;
            }

            @Override
            public T visitEval(AST target) {
                return null;
            }
        }
    }

    class Parse {
        private static Map<String, BiFunction<String, List<AST>, AST>> factories;

        static {
            factories = Arrays.asList(Factory.class.getMethods()).stream().collect(Collectors.toMap(
                m -> m.getName() + "/" + m.getParameterTypes().length,
                m -> {
                    List<Function<AST, Object>> argumentDerivers = Arrays.asList(m.getParameterTypes()).stream().map(pt -> {
                        if (pt.equals(String.class)) {
                            return (Function<AST, Object>) arg -> arg.accept(new Visitor.Default<Object>() {
                                @Override
                                public Object visitLoad(String name) {
                                    return name;
                                }
                            });
                        } else {
                            return (Function<AST, Object>) arg -> arg;
                        }
                    }).collect(Collectors.toList());

                    return (operator, operands) -> {
                        Object[] arguments = new Object[operands.size()];
                        for(int i = 0; i < argumentDerivers.size(); i++)
                            arguments[i] = argumentDerivers.get(i).apply(operands.get(i));

                        try {
                            return (AST)m.invoke(null, arguments);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        return null;
                    };
                }
            ));
        }

        public static AST parseOperation(String operator, List<AST> operands) {
            BiFunction<String, List<AST>, AST> factory = factories.get(operator + "/" + operands.size());

            if(factory != null) {
                return factory.apply(operator, operands);
            } else {
                // Call method
            }

            return null;

            /*Object[] arguments = operands.stream().toArray(s -> new Object[s]);
            Class<?>[] parameterTypes = operands.stream().map(x -> AST.class).toArray(s -> new Class<?>[s]);
            try {
                Method method = Factory.class.getMethod(operator, parameterTypes);
                try {
                    return (AST)method.invoke(null, arguments);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                // Call method
            }
            return null;*/
        }
    }

    class Factory {
        public static AST program(AST code) {
            return new AST() {
                @Override
                public String getName() {
                    return "program";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(code);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitProgram(code);
                }
            };
        }

        public static AST block(AST... code) {
            return new AST() {
                @Override
                public String getName() {
                    return "block";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(code);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitBlock(Arrays.asList(code));
                }
            };
        }

        public static AST defun(String name, AST code) {
            return defun(name, new String[0], code);
        }

        public static AST defun(String name, String[] params, AST code) {
            return local(Selector.get(name, params.length), fn(params, code));
        }

        public static Slot field(String name, AST value) {
            return new Slot() {
                @Override
                public String getName() {
                    return "method";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(name), value);
                }

                @Override
                public void accept(Visitor visitor) {
                    visitor.visitField(name, value);
                }
            };
        }

        public static Slot method(String name, AST body) {
            return method(name, new String[0], body);
        }

        public static Slot method(String name, String[] parameters, AST body) {
            return new Slot() {
                @Override
                public String getName() {
                    return "method";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(name), new Node.Atom(parameters), body);
                }

                @Override
                public void accept(Visitor visitor) {
                    visitor.visitMethod(name, Arrays.asList(parameters), body);
                }
            };
        }

        public static AST object(Slot... slots) {
            return new AST() {
                @Override
                public String getName() {
                    return "object";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(slots);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitObject(Arrays.asList(slots));
                }
            };
        }

        public static AST send(AST target, String name, AST... arguments) {
            return new AST() {
                @Override
                public String getName() {
                    return "send";
                }

                @Override
                public List<Node> getChildren() {
                    return Stream.concat(
                        Arrays.asList(target, new Node.Atom(name)).stream(),
                        Arrays.asList(arguments).stream()
                    ).collect(Collectors.toList());
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitSend(target, name, Arrays.asList(arguments));
                }
            };
        }

        public static AST env() {
            return new AST() {
                @Override
                public String getName() {
                    return "env";
                }

                @Override
                public List<Node> getChildren() {
                    return Collections.emptyList();
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitEnv();
                }
            };
        }

        public static AST fn(AST code) {
            return fn(new String[0], code);
        }

        public static AST fn(String[] params, AST code) {
            return object(
                method("call", params, code)
            );
        }

        public static AST local(AST target, String name, AST value) {
            return new AST() {
                @Override
                public String getName() {
                    return "local";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(target, new Node.Atom(name), value);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLocal(target, name, value);
                }
            };
        }

        public static AST local(String name, AST value) {
            return new AST() {
                @Override
                public String getName() {
                    return "local";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(name), value);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLocal(name, value);
                }
            };
        }

        public static AST store(String name, AST value) {
            return new AST() {
                @Override
                public String getName() {
                    return "store";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(name), value);
                }
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitStore(name, value);
                }
            };
        }

        public static AST store(AST target, String name, AST value) {
            return new AST() {
                @Override
                public String getName() {
                    return "store";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(target, new Node.Atom(name), value);
                }
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitStore(target, name, value);
                }
            };
        }

        public static AST loadd(String name) {
            return new AST() {
                @Override
                public String getName() {
                    return "loadd";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(name));
                }
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLoadD(name);
                }
            };
        }

        public static AST load(String name) {
            return new AST() {
                @Override
                public String getName() {
                    return "name";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(name));
                }
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLoad(name);
                }
            };
        }

        public static AST load(AST target, String name) {
            return new AST() {
                @Override
                public String getName() {
                    return "local";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(target, new Node.Atom(name));
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLoad(target, name);
                }
            };
        }

        public static AST apply(AST target, AST... arguments) {
            return send(target, "call", arguments);
        }

        public static AST eval(AST target) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitEval(target);
                }

                @Override
                public String getName() {
                    return "eval";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(target);
                }
            };
        }

        public static AST call(String name, AST... arguments) {
            return apply(load(Selector.get(name, arguments.length)), arguments);
        }

        public static AST calld(String name, AST... arguments) {
            return apply(loadd(Selector.get(name, arguments.length)), arguments);
        }

        public static AST bp = new AST() {
            @Override
            public String getName() {
                return "bp";
            }

            @Override
            public List<Node> getChildren() {
                return Collections.emptyList();
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.visitBP();
            }
        };

        public static AST resume(AST target, AST value) {
            return new AST() {
                @Override
                public String getName() {
                    return "resume";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(target, value);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitResume(target, value);
                }
            };
        }

        public static AST frame = new AST() {
            @Override
            public String getName() {
                return "frame";
            }

            @Override
            public List<Node> getChildren() {
                return Collections.emptyList();
            }

            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.visitFrame();
            }
        };

        public static AST ret(AST expression) {
            return new AST() {
                @Override
                public String getName() {
                    return "ret";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(expression);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitRet(expression);
                }
            };
        }


        public static AST applycc(AST target) {
            return new AST() {
                @Override
                public String getName() {
                    return "applycc";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(target);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitApplyCC(target);
                }
            };
        }

        public static AST test(AST condition, AST ifTrue) {
            return test(condition, ifTrue, literal(false));
        }

        public static AST test(AST condition, AST ifTrue, AST ifFalse) {
            return new AST() {
                @Override
                public String getName() {
                    return "test";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(condition, ifTrue, ifFalse);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitTest(condition, ifTrue, ifFalse);
                }
            };
        }

        public static AST loop(AST condition, AST body) {
            return new AST() {
                @Override
                public String getName() {
                    return "loop";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(condition, body);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLoop(condition, body);
                }
            };
        }

        public static AST literal(Object obj) {
            return new AST() {
                @Override
                public String getName() {
                    return "literal";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(obj));
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLiteral(obj);
                }
            };
        }

        public static AST newInstance(Constructor constructor, AST... args) {
            return new AST() {
                @Override
                public String getName() {
                    return "newInstance";
                }

                @Override
                public List<Node> getChildren() {
                    return Stream.concat(
                        Arrays.asList(new Node.Atom(constructor)).stream(),
                        Arrays.asList(args).stream()).collect(Collectors.toList());
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitNewInstance(constructor, Arrays.asList(args));
                }
            };
        }

        public static AST invoke(Method method, AST... args) {
            return new AST() {
                @Override
                public String getName() {
                    return "invoke";
                }

                @Override
                public List<Node> getChildren() {
                    return Stream.concat(
                        Arrays.asList(new Node.Atom(method)).stream(),
                        Arrays.asList(args).stream()).collect(Collectors.toList());
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitInvoke(method, Arrays.asList(args));
                }
            };
        }

        public static AST invoke(AST target, Method method, AST... args) {
            return new AST() {
                @Override
                public String getName() {
                    return "invoke";
                }

                @Override
                public List<Node> getChildren() {
                    return Stream.concat(
                        Arrays.asList(target, new Node.Atom(method)).stream(),
                        Arrays.asList(args).stream()).collect(Collectors.toList());
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitInvoke(target, method, Arrays.asList(args));
                }
            };
        }

        public static AST fieldGet(Field field) {
            return new AST() {
                @Override
                public String getName() {
                    return "fieldGet";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(field));
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldGet(field);
                }
            };
        }

        public static AST fieldGet(AST target, Field field) {
            return new AST() {
                @Override
                public String getName() {
                    return "fieldGet";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(target, new Node.Atom(field));
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldGet(target, field);
                }
            };
        }

        public static AST fieldSet(Field field, AST value) {
            return new AST() {
                @Override
                public String getName() {
                    return "fieldGet";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(new Node.Atom(field), value);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldSet(field, value);
                }
            };
        }

        public static AST fieldSet(AST target, Field field, AST value) {
            return new AST() {
                @Override
                public String getName() {
                    return "fieldSet";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(target, new Node.Atom(field), value);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldSet(target, field, value);
                }
            };
        }

        public static AST addi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "addi";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitAddi(lhs, rhs);
                }
            };
        }

        public static AST subi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "subi";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitSubi(lhs, rhs);
                }
            };
        }

        public static AST muli(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "muli";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitMuli(lhs, rhs);
                }
            };
        }

        public static AST divi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "divi";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitDivi(lhs, rhs);
                }
            };
        }

        public static AST lti(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "lti";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLti(lhs, rhs);
                }
            };
        }

        public static AST gti(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "gti";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitGti(lhs, rhs);
                }
            };
        }

        public static AST eqi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "eqi";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitEqi(lhs, rhs);
                }
            };
        }

        /*public static AST gtei(AST lhs, AST rhs) {
            return keep(
                lhs, rhs,
                or(
                    gtei(top0, top1),
                    eqc(top0, top1)
                )
            );
        }*/

        public static AST eqc(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "eqc";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitEqc(lhs, rhs);
                }
            };
        }

        public static AST itoc(AST i) {
            return new AST() {
                @Override
                public String getName() {
                    return "itoc";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(i);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitItoc(i);
                }
            };
        }

        public static AST not(AST expression) {
            return new AST() {
                @Override
                public String getName() {
                    return "not";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(expression);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitNot(expression);
                }
            };
        }

        public static AST and(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "and";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitAnd(lhs, rhs);
                }
            };
        }

        public static AST or(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public String getName() {
                    return "or";
                }

                @Override
                public List<Node> getChildren() {
                    return Arrays.asList(lhs, rhs);
                }

                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitOr(lhs, rhs);
                }
            };
        }
    }
}
