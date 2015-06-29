package yaplco;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Stack;

public class Scheduler {
    private CoRoutineImpl END = new CoRoutineImpl() {
    @Override
        public void resume(CoRoutine requester, Object signal) {
            Scheduler.this.resume(this, requester, Pair.list("end"));
        }
    };

    private boolean running;
    private Runnable pending;

    public void schedule(Runnable task) {
        if(pending != null) {
            pending = sync(pending, task);
        } else
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

    private Runnable sync(Runnable first, Runnable second) {
        return () -> {
            first.run();
            this.schedule(second);
        };
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

    public void respond(CoRoutine target, Object signal) {
        respond(END, target, signal);
    }
}
