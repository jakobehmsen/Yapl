package yaplco;

public interface CoRoutineImpl extends CoRoutine {
    /*default void respond(Object signal) {
        //resume(END, Pair.list("response", signal));
        resumeResponse(END, signal);
    }

    default void error(Object signal) {
        //resume(END, Pair.list("response", signal));
        resumeError(END, signal);
    }*/

    // Can be overridden to avoid reification of response
    default void resumeResponse(CoRoutine requester, Object signal) {
        resume(requester, Pair.list("response", signal));
    }

    // Can be overridden to avoid reification of error
    default void resumeError(CoRoutine requester, Object signal) {
        resume(requester, Pair.list("error", signal));
    }

    default void resumeOther(CoRoutine requester, Object signal) {
        resume(requester, signal);
    }

    void resume(CoRoutine requester, Object signal);

    /*CoRoutine END = new CoRoutine() {
        @Override
        public void resume(CoRoutine requester, Object signal) {
            requester.resume(this, Pair.list("end"));
        }
    };*/
}
