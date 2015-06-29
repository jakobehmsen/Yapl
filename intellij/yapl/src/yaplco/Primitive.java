package yaplco;

public interface Primitive {
    CoRoutine newCo(Scheduler scheduler, Evaluator evaluator);
}
