package yaplco;

import java.util.function.Consumer;

public class Evaluator {
    private Environment environment;

    public Evaluator(Environment environment) {
        this.environment = environment;
    }

    public CoRoutine eval(Scheduler scheduler, Object item) {
        if(item != null) {
            if(item instanceof Pair) {
                Pair list = (Pair) item;
                if (list.current instanceof String) {
                    String operator = (String) list.current;

                    Pair args = list.next;
                    Object function = environment.get(operator);

                    CoRoutine co = function instanceof Primitive
                        ? ((Primitive)function).newCo(scheduler, this)
                        : eval(scheduler, function);

                    return (CoRoutineImpl) (requester, signal) ->
                        scheduler.resume(requester, co, args);
                } else {
                    return (CoRoutineImpl) (requester, signal) ->
                        evaluateList(scheduler, list, null, requester);
                }
            }
        }

        return (CoRoutineImpl) (requester, signal) ->
            scheduler.respond(requester, item);
    }

    public void eval(Scheduler scheduler, Object item, CoRoutine requester, Consumer<Object> responseHandler) {
        CoRoutine evaluation = eval(scheduler, item);
        scheduler.resume(new CoCaller(scheduler, requester) {
            @Override
            public void resumeResponse(CoRoutine requester, Object signal) {
                responseHandler.accept(signal);
            }
        }, evaluation, null);
    }

    public void evaluateList(Scheduler scheduler, Pair list, Object lastResult, CoRoutine evalRequester) {
        if(list == null)
            scheduler.respond(evalRequester, lastResult);
        else {
            //eval(scheduler, list.current, result ->
            //    evaluateList(scheduler, list.next, result, evalRequester));

            CoRoutine evaluation = eval(scheduler, list.current);
            scheduler.resume(new CoCaller(scheduler, evalRequester) {
                @Override
                public void resumeResponse(CoRoutine requester, Object result) {
                    evaluateList(scheduler, list.next, result, requester);
                }
            }, evaluation, null);
        }
    }
}
