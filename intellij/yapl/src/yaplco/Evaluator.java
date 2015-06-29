package yaplco;

import java.util.function.Consumer;

public class Evaluator {
    private Environment environment;

    public Evaluator(Environment environment) {
        this.environment = environment;
    }

    public CoRoutine eval(Scheduler scheduler, Object item) {
        // Somehow, co-routines shouldn't be called directly but instead requested for execution paired with a signal
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
                        //co.resume(requester, args);
                        scheduler.resume(requester, co, args);
                } else {
                    return (CoRoutineImpl) (requester, signal) ->
                        evaluateList(scheduler, list, null, requester);
                }
            }
        }

        return (CoRoutineImpl) (requester, signal) ->
            scheduler.respond(requester, item);

        /*return (requester, signal) ->
            scheduler.respond(requester, item);
            //requester.respond(item);
            */
    }

    public void eval(Scheduler scheduler, Object item, Consumer<Object> responseHandler) {
        CoRoutine evaluation = eval(scheduler, item);
        scheduler.resume(new CoCaller() {
            @Override
            public void resumeResponse(CoRoutine requester, Object signal) {
                responseHandler.accept(signal);
            }
        }, evaluation, null);
        /*evaluation.resume(new CoCaller() {
            @Override
            public void resumeResponse(CoRoutine requester, Object signal) {
                responseHandler.accept(signal);
            }
        }, null);*/
    }

    public void evaluateList(Scheduler scheduler, Pair list, Object lastResult, CoRoutine requester) {
        if(list == null)
            //requester.respond(lastResult);
            scheduler.respond(requester, lastResult);
        else {
            eval(scheduler, list.current, result ->
                evaluateList(scheduler, list.next, result, requester));
        }
    }
}
