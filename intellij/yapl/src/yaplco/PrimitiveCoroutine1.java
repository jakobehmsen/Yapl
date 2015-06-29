package yaplco;

public interface PrimitiveCoroutine1<T> {
    void accept(Scheduler scheduler, Evaluator evaluator, CoRoutine requester, T arg0);
}
