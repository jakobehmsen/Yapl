package yaplco;

public interface PrimitiveCoroutine {
    void accept(Evaluator evaluator, CoRoutine requester, Pair args);
}
