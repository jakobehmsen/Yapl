package yaplco;

public interface PrimitiveCoroutine2<T, R> {
    void accept(Evaluator evaluator, CoRoutine requester, T arg0, R arg1);
}
