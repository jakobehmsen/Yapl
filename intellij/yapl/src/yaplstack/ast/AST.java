package yaplstack.ast;

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
        T visitMuli(AST lhs, AST rhs);
        T visitInvoke(AST target, Method method, List<AST> args);
        T visitLocal(String name, AST value);
        T visitStore(String name, AST value);
        T visitLoad(String name);
        T visitCall(AST target, List<AST> asts);
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

        public static AST addi(AST lhs, AST rhs) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitAddi(lhs, rhs);
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
    }
}
