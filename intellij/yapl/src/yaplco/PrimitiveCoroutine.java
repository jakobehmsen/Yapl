package yaplco;

public interface PrimitiveCoroutine {
    void accept(Scheduler scheduler, Evaluator evaluator, CoRoutine requester, Pair args);
}
