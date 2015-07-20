package yaplstack.ast;

public class Selector {
    public static String get(String name, int arity) {
        return name + "/" + arity;
    }
}
