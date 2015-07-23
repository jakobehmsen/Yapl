package yaplstack.ast;

import yaplstack.Instruction;

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

    public void emitLoad(Generator generator, String name) {
        emitLoad(generator, name, this);
    }

    private void emitLoad(Generator generator, String name, MetaFrame context) {
        if(context != null) {
            int localOrdinal = context.locals.indexOf(name);

            if(localOrdinal != -1) {
                int distance = distanceTo(context);
                if(distance == 0) {
                    generator.emit(Instruction.Factory.loadVar(name, localOrdinal));
                } else if(distance > 0) {
                    generator.emit(Instruction.Factory.loadVar("self", 0));
                    generator.emit(Instruction.Factory.load("__frame__"));

                    for(int i = 1; i < distance; i++) {
                        generator.emit(Instruction.Factory.loadOuterCallFrame);
                    }

                    generator.emit(Instruction.Factory.frameLoadVar(localOrdinal));

                    this.isDependent = true;
                }
            } else {
                emitLoad(generator, name, context.context);
            }
        } else {
            generator.emitLoadSelf();
            generator.emit(Instruction.Factory.load(name));
        }
    }

    private int distanceTo(MetaFrame context) {
        MetaFrame target = this;
        int i = 0;
        while(context != target) {
            target = target.context;
            if(target == null)
                return -1;
            i++;
        }
        return i;
    }
}
