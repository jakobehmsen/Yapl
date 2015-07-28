package yaplstack.ast;

import java.util.List;

public interface Slot extends Node {
    interface Visitor {
        void visitField(String name, AST value);
        void visitMethod(String name, List<String> parameters, AST body);
    }

    void accept(Visitor visitor);
}
