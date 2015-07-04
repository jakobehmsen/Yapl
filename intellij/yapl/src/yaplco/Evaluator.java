package yaplco;

import java.util.function.Consumer;

public class Evaluator {
    public Environment environment;

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

                    /*
                    TODO:
                    Consider whether it is possible to pre-process request and resume such that thay work as expected?
                    Or is more evaluation context needed?
                    */

                    /*
                    Assumed the following protocol for functions:

                    a function is called with no initial signal and is assumed to request its arguments actively
                    - the signal is not accessible to custom functions:

                    // Server side (primitive or custom):
                    args = resume() // Request arguments
                    // do something based on args
                    resume(list('respond, ...)) // Pass response

                    // Client side (for instance evaluator):
                    function = ...
                    resume(function); // Resume function till it requests its arguments
                    response = resume(args) // Supply arguments and get response

                    */

                    /*return (CoRoutineImpl) (requester, signal) ->
                        scheduler.resume(requester, co, args);*/

                    return (CoRoutineImpl) (requester, signal) -> {
                        // Trigger argument request
                        scheduler.resume((CoRoutineImpl) (coRequestingArgs, s) -> {
                            // Function now requests arguments
                            // Pass arguments
                            scheduler.resume(requester, coRequestingArgs, args);
                        }, co, null);
                    };
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
        /*CoRoutine evaluation = eval(scheduler, item);
        scheduler.resume(new CoCaller(scheduler, requester) {
            @Override
            public void resumeResponse(CoRoutine requester, Object signal) {
                responseHandler.accept(signal);
            }

            @Override
            public String toString() {
                return "eval caller";
            }
        }, evaluation, null);*/

        eval(scheduler, item, new CoCaller(scheduler, requester) {
            @Override
            public void resumeResponse(CoRoutine requester, Object signal) {
                responseHandler.accept(signal);
            }

            @Override
            public String toString() {
                return "eval caller";
            }
        });
    }

    public void eval(Scheduler scheduler, Object item, CoRoutine requester) {
        CoRoutine evaluation = eval(scheduler, item);
        scheduler.resume(requester, evaluation, null);
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
                    evaluateList(scheduler, list.next, result, evalRequester);
                }

                @Override
                public String toString() {
                    return "eval list caller";
                }
            }, evaluation, null);
        }
    }
}
