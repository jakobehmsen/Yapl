package yaplco;

public abstract class CoCaller implements CoRoutineImpl {
    private Scheduler scheduler;
    private CoRoutine cause;

    protected CoCaller(Scheduler scheduler, CoRoutine cause) {
        this.scheduler = scheduler;
        this.cause = cause;
    }

    @Override
    public void resume(CoRoutine requester, Object signal) {
        // Add default error handling and signal reification
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

        //resumeError(requester, Pair.list("error", "invalid_signal", signal));
        resumeOther(requester, signal);
    }

    //public abstract void resumeResponse(CoRoutine requester, Object signal);

    public void resumeError(CoRoutine requester, Object signal) {
        //throw new RuntimeException("Unhandled error: " + signal);
        scheduler.resumeError(requester, cause, signal);
    }

    public void resumeOther(CoRoutine requester, Object signal) {
        scheduler.resumeOther(requester, cause, signal);
    }
}
