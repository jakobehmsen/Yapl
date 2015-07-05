package yaploo;

import java.util.Arrays;
import java.util.List;

public interface AST {
    interface Visitor<T> {
        T visitLiteral(int i);
        T visitLiteral(String str);
        T visitSelf();
        T visitMessageSend(AST receiver, String selector, List<AST> args);
        T visitSequence(List<AST> expressions);
        T visitDefine(String name, AST value);
        T visitSet(String name, AST value);
        T visitGet(String name);
    }

    <T> T accept(Visitor<T> visitor);

    class Factory {
        public static AST seq(AST... expressions) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitSequence(Arrays.asList(expressions));
                }
            };
        }

        public static AST literal(int i) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLiteral(i);
                }
            };
        }

        public static AST literal(String str) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitLiteral(str);
                }
            };
        }

        public static AST self() {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitSelf();
                }
            };
        }

        public static AST messageSend(AST receiver, String selector, AST... args) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitMessageSend(receiver, selector, Arrays.asList(args));
                }
            };
        }

        public static AST define(String name, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitDefine(name, value);
                }
            };
        }

        public static AST set(String name, AST value) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitSet(name, value);
                }
            };
        }

        public static AST get(String name) {
            return new AST() {
                @Override
                public <T> T accept(Visitor<T> visitor) {
                    return visitor.visitGet(name);
                }
            };
        }
    }
}
