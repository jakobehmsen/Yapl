package yaplstack.ast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public interface AST {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visitProgram(AST code);
        T visitBlock(List<AST> code);
        T visitFunction(List<String> params, AST code);
        T visitLiteral(Object obj);
        T visitAddi(AST lhs, AST rhs);
        T visitSubi(AST lhs, AST rhs);
        T visitMuli(AST lhs, AST rhs);
        T visitDivi(AST lhs, AST rhs);
        T visitLti(AST lhs, AST rhs);
        T visitGti(AST lhs, AST rhs);
        T visitEqi(AST lhs, AST rhs);
        T visitInvoke(AST target, Method method, List<AST> args);
        T visitFieldGet(AST target, Field field);
        T visitFieldSet(AST target, Field field, AST value);
        T visitLocal(String name, AST value);
        T visitStore(String name, AST value);
        T visitLoad(String name);
        T visitCall(AST target, List<AST> asts);
        T visitTest(AST condition, AST ifTrue, AST ifFalse);
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

        public static AST function(String[] params, AST code) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFunction(Arrays.asList(params), code);
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

        public static AST load(String name) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLoad(name);
                }
            };
        }

        public static AST call(AST target, AST... arguments) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitCall(target, Arrays.asList(arguments));
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

        public static AST literal(Object obj) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLiteral(obj);
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

        public static AST fieldGet(AST target, Field field) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitFieldGet(target, field);
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
    }
}
