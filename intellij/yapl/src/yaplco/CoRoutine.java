package yaplco;

public interface CoRoutine {
    default void respond(Object signal) {
        resume(END, Pair.list("response", signal));
    }

    void resume(CoRoutine requester, Object signal);

    /*default void resume(CoRoutine requester, Object signal) {
        // Add default error handling
        if(signal instanceof Pair) {
            Pair signalAsPair = (Pair)signal;
            if(signalAsPair.current.equals("error")) {
                resumeError(requester, signal);
                return;
            } else if(signalAsPair.current.equals("response")) {
                resumeResponse(requester, signalAsPair.next.current);
                return;
            }
        }

        resumeError(requester, Pair.list("error", "invalid_signal", signal));
    }

    void resumeResponse(CoRoutine requester, Object signal);

    default void resumeError(CoRoutine requester, Object signal) {
        requester.resume(this, signal);
    }*/

    CoRoutine END = new CoRoutine() {
        @Override
        public void resume(CoRoutine requester, Object signal) {
            requester.resume(this, Pair.list("end"));
        }
    };
}
