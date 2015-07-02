package yaplco;

public class Scheduler {
    private CoRoutineImpl END = new CoRoutineImpl() {
    @Override
        public void resume(CoRoutine requester, Object signal) {

        }
    };

    private boolean running;
    private Runnable pending;

    public void schedule(Runnable task) {
        if(pending != null)
            throw new RuntimeException("Task already scheduled in scheduler.");

        pending = task;

        if(!running) {
            running = true;

            while(pending != null) {
                Runnable nextTask = pending;
                pending = null;
                nextTask.run();
            }

            running = false;
        }
    }

    public Runnable resumeTask(CoRoutine requester, CoRoutine target, Object signal) {
        return () ->
            ((CoRoutineImpl) target).resume(requester, signal);
    }

    public void resume(CoRoutine requester, CoRoutine target, Object signal) {
        schedule(() ->
            ((CoRoutineImpl) target).resume(requester, signal));
    }

    public void respond(CoRoutine requester, CoRoutine target, Object signal) {
        schedule(() ->
            ((CoRoutineImpl) target).resumeResponse(requester, signal));
    }

    public void resumeResponse(CoRoutine requester, CoRoutine target, Object signal) {
        schedule(() ->
            ((CoRoutineImpl) target).resumeResponse(requester, signal));
    }

    public void resumeError(CoRoutine requester, CoRoutine target, Object signal) {
        schedule(() ->
            ((CoRoutineImpl) target).resumeError(requester, signal));
    }

    public void resumeOther(CoRoutine requester, CoRoutine target, Object signal) {
        schedule(() ->
            ((CoRoutineImpl) target).resumeOther(requester, signal));
    }

    public void resumeOther(CoRoutine target, Object signal) {
        resumeOther(END, target, signal);
    }

    public void respond(CoRoutine target, Object signal) {
        respond(END, target, signal);
    }
}
