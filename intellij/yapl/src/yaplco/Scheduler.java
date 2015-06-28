package yaplco;

public class Scheduler {
    private Runnable pending;

    public void resume(CoRoutine requested, CoRoutine target, Object signal) {
        pending = new Runnable() {
            @Override
            public void run() {
                target.resume(requested, signal);
            }
        };
    }

    public void respond(CoRoutine requester, CoRoutine target, Object signal) {
        pending = new Runnable() {
            @Override
            public void run() {
                target.resumeResponse(requester, signal);
            }
        };
    }
}
