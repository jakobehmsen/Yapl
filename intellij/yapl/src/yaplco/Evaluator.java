package yaplco;

import java.util.function.Consumer;

public class Evaluator {
    private Environment environment;

    public Evaluator(Environment environment) {
        this.environment = environment;
    }

    public CoRoutine eval(Object item) {
        if(item != null) {
            if(item instanceof Pair) {
                Pair list = (Pair) item;
                if (list.current instanceof String) {
                    String operator = (String) list.current;

                    Pair args = list.next;
                    Object function = environment.get(operator);

                    CoRoutine co = function instanceof Primitive
                        ? ((Primitive)function).newCo(this)
                        : eval(function);

                    return (requester, signal) ->
                        co.resume(requester, args);
                } else {
                    return new CoRoutine() {
                        Pair current;
                        //CoRoutine current;

                        @Override
                        public void resume(CoRoutine requester, Object signal) {

                        }
                    };
                }
            }
        }

        return (requester, signal) ->
            requester.respond(item);
    }

    public void eval(Object item, Consumer<Object> responseHandler) {
        CoRoutine evaluation = eval(item);
        evaluation.resume(new CoCaller() {
            @Override
            public void resumeResponse(CoRoutine requester, Object signal) {
                responseHandler.accept(signal);
            }
        }, null);
    }
}
