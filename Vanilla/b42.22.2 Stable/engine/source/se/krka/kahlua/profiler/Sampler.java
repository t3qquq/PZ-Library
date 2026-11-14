// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import se.krka.kahlua.vm.Coroutine;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;

public class Sampler {
    private static final AtomicInteger NEXT_ID = new AtomicInteger();
    private final KahluaThread thread;
    private final Timer timer;
    private final long period;
    private final Profiler profiler;

    public Sampler(KahluaThread thread, long period, Profiler profiler) {
        this.thread = thread;
        this.period = period;
        this.profiler = profiler;
        this.timer = new Timer("Kahlua Sampler-" + NEXT_ID.incrementAndGet(), true);
    }

    public void start() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                List<StacktraceElement> list = new ArrayList<>();
                Sampler.this.appendList(list, Sampler.this.thread.currentCoroutine);
                Sampler.this.profiler.getSample(new Sample(list, Sampler.this.period));
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 0L, this.period);
    }

    private void appendList(List<StacktraceElement> list, Coroutine coroutine) {
        while (coroutine != null) {
            LuaCallFrame[] stack = coroutine.getCallframeStack();
            int top = Math.min(stack.length, coroutine.getCallframeTop());

            for (int i = top - 1; i >= 0; i--) {
                LuaCallFrame frame = stack[i];
                int pc = frame.pc - 1;
                LuaClosure closure = frame.closure;
                JavaFunction javaFunction = frame.javaFunction;
                if (closure != null) {
                    list.add(new LuaStacktraceElement(pc, closure.prototype));
                } else if (javaFunction != null) {
                    list.add(new JavaStacktraceElement(javaFunction));
                }
            }

            coroutine = coroutine.getParent();
        }
    }

    public void stop() {
        this.timer.cancel();
    }
}
