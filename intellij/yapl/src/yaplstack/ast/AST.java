package yaplstack.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface AST {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visitProgram(AST code);
        T visitBlock(List<AST> code);
        T visitFN(List<String> params, AST code);
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
        T visitApply(AST target, List<AST> args);
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
        T visitFrameLoad(AST target, int ordinal);
        T visitFrameStore(AST target, int ordinal, AST value);
    }

    class Factory {
        public static AST program(AST code) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitProgram(code);
                }
            };
        }

        public static AST block(AST... code) {
            return new AST() {
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
            return visitor -> visitor.visitField(name, value);
        }

        public static Slot method(String name, AST body) {
            return method(name, new String[0], body);
        }

        public static Slot method(String name, String[] parameters, AST body) {
            return visitor -> visitor.visitMethod(name, Arrays.asList(parameters), body);
        }

        public static AST object(Slot... slots) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitObject(Arrays.asList(slots));
                }
            };
        }

        public static AST send(AST target, String name, AST... arguments) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitSend(target, name, Arrays.asList(arguments));
                }
            };
        }

        public static AST env() {
            return new AST() {
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
            /*return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFN(Arrays.asList(params), code);
                }
            };*/
            return object(
                method("call", params, code)
            );
        }

        public static AST local(AST target, String name, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLocal(target, name, value);
                }
            };
        }

        public static AST local(String name, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLocal(name, value);
                }
            };
        }

        public static AST store(String name, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitStore(name, value);
                }
            };
        }

        public static AST store(AST target, String name, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitStore(target, name, value);
                }
            };
        }

        public static AST frameStore(AST target, int ordinal, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFrameStore(target, ordinal, value);
                }
            };
        }

        public static AST load(String name) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLoad(name);
                }
            };
        }

        public static AST load(AST target, String name) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLoad(target, name);
                }
            };
        }

        public static AST frameLoad(AST target, int ordinal) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFrameLoad(target, ordinal);
                }
            };
        }

        public static AST apply(AST target, AST... arguments) {
            return send(target, "call", arguments);
            /*return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitApply(target, Arrays.asList(arguments));
                }
            };*/
        }

        public static AST call(String name, AST... arguments) {
            return apply(load(Selector.get(name, arguments.length)), arguments);
        }

        public static AST bp = new AST() {
            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.visitBP();
            }
        };

        public static AST resume(AST target, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitResume(target, value);
                }
            };
        }

        public static AST frame = new AST() {
            @Override
            public <T> T accept(Visitor<T> visitor) {
                return visitor.visitFrame();
            }
        };

        public static AST ret(AST expression) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitRet(expression);
                }
            };
        }


        public static AST applycc(AST target) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitApplyCC(target);
                }
            };
        }

        public static AST test(AST condition, AST ifTrue, AST ifFalse) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitTest(condition, ifTrue, ifFalse);
                }
            };
        }

        public static AST loop(AST condition, AST body) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLoop(condition, body);
                }
            };
        }

        public static AST literal(Object obj) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLiteral(obj);
                }
            };
        }

        public static AST newInstance(Constructor constructor, AST... args) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitNewInstance(constructor, Arrays.asList(args));
                }
            };
        }

        public static AST invoke(Method method, AST... args) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitInvoke(method, Arrays.asList(args));
                }
            };
        }

        public static AST invoke(AST target, Method method, AST... args) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitInvoke(target, method, Arrays.asList(args));
                }
            };
        }

        public static AST fieldGet(Field field) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldGet(field);
                }
            };
        }

        public static AST fieldGet(AST target, Field field) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldGet(target, field);
                }
            };
        }

        public static AST fieldSet(Field field, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldSet(field, value);
                }
            };
        }

        public static AST fieldSet(AST target, Field field, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldSet(target, field, value);
                }
            };
        }

        public static AST addi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitAddi(lhs, rhs);
                }
            };
        }

        public static AST subi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitSubi(lhs, rhs);
                }
            };
        }

        public static AST muli(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitMuli(lhs, rhs);
                }
            };
        }

        public static AST divi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitDivi(lhs, rhs);
                }
            };
        }

        public static AST lti(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLti(lhs, rhs);
                }
            };
        }

        public static AST gti(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitGti(lhs, rhs);
                }
            };
        }

        public static AST eqi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitEqi(lhs, rhs);
                }
            };
        }

        public static AST itoc(AST i) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitItoc(i);
                }
            };
        }

        public static AST not(AST expression) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitNot(expression);
                }
            };
        }
    }
}
