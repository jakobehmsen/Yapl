package yaplstack.ast;

import java.util.ArrayList;

public class MetaFrame {
    public MetaFrame context;
    public ArrayList<String> locals = new ArrayList<>();
    public boolean isDependent;

    public MetaFrame() {
        this(null);
    }

    public MetaFrame(MetaFrame context) {
        this.context = context;
    }

    public interface InstructionGenerator {
        void emitForFrame(int distance, MetaFrame target, int ordinal);
        void emitForEnv(String name);
    }

    public void emit(String name, InstructionGenerator instructionGenerator) {
        emit(name, this, 0, instructionGenerator);
    }

    private void emit(String name, MetaFrame context, int distance, InstructionGenerator instructionGenerator) {
        if(context != null) {
            int localOrdinal = context.locals.indexOf(name);

            if(localOrdinal != -1) {
                instructionGenerator.emitForFrame(distance, context, localOrdinal);
            } else {
                emit(name, context.context, distance + 1, instructionGenerator);
            }
        } else {
            instructionGenerator.emitForEnv(name);
        }
    }
}
