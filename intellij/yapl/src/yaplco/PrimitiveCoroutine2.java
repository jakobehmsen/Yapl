package yaplco;

public abstract class PrimitiveCoroutine2<T, R> implements PrimitiveCoroutine {
    @Override
    public void accept(Evaluator evaluator, CoRoutine requester, Pair args) {
        evaluator.eval(args.current, arg0 -> {
            evaluator.eval(args.next.current, arg1 -> {
                accept(evaluator, requester, (T) arg0, (R)arg1);
            });
        });
    }

    public abstract void accept(Evaluator evaluator, CoRoutine requester, T arg0, R arg1);
}
