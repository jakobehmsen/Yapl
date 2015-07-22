package yaplstack.ast;

import yaplstack.Instruction;

import java.util.ArrayList;
import java.util.HashSet;

public class MetaFrame {
    public MetaFrame context;
    public ArrayList<String> locals = new ArrayList<>();
    //public HashSet<MetaFrame> closedContexts = new HashSet<>();
    //public HashSet<MetaFrame> closedContexts = new HashSet<>();
    public HashSet<MetaFrame> dependents = new HashSet<>();
    public boolean isDepedent;

    public MetaFrame() {
        this(null);
    }

    public MetaFrame(MetaFrame context) {
        this.context = context;
    }

    public void emitLoad(Generator generator, String name) {
        emitLoad(generator, name, this);

        /*int localOrdinal = context.locals.indexOf(name);

        if(localOrdinal != -1) {
            generator.emit(Instruction.Factory.loadVar(name, localOrdinal));
        } else {
            generator.emitLoadSelf();
            generator.emit(Instruction.Factory.load(name));
        }*/
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

                    MetaFrame dependent = this;
                    MetaFrame depender = this.context;

                    for(int i = 1; i < distance; i++) {
                        dependent.isDepedent = true;
                        depender.dependents.add(dependent);

                        // Load outer self via frame
                        generator.emit(Instruction.Factory.load("__frame__"));
                        generator.emit(Instruction.Factory.frameLoadVar(0));

                        dependent = dependent.context;
                        depender = dependent.context;
                    }

                    dependent.isDepedent = true;
                    depender.dependents.add(dependent);

                    generator.emit(Instruction.Factory.load("__frame__"));
                    generator.emit(Instruction.Factory.frameLoadVar(localOrdinal));
                }
                //closedContexts.add(context);
                /*this.isDepedent = true;
                context.dependents.add(this);*/
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
