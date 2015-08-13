package yaplstack.ast;

import java.util.List;

public interface Slot extends Node {
    interface Visitor<T> {
        T visitField(String name, AST value);
        T visitMethod(String name, List<String> parameters, AST body);
    }

    <T> T accept(Visitor<T> visitor);
}
