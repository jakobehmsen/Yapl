package yaplstack.ast;

import java.util.Collections;
import java.util.List;

public interface Node {
    String getName();
    List<Node> getChildren();

    class Atom implements Node {
        public final Object value;

        public Atom(Object value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return "atom";
        }

        @Override
        public List<Node> getChildren() {
            return Collections.emptyList();
        }
    }
}
