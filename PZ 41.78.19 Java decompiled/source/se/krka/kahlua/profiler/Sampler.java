// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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

    public Sampler(KahluaThread kahluaThread, long long0, Profiler profilerx) {
        this.thread = kahluaThread;
        this.period = long0;
        this.profiler = profilerx;
        this.timer = new Timer("Kahlua Sampler-" + NEXT_ID.incrementAndGet(), true);
    }

    public void start() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ArrayList arrayList = new ArrayList();
                Sampler.this.appendList(arrayList, Sampler.this.thread.currentCoroutine);
                Sampler.this.profiler.getSample(new Sample(arrayList, Sampler.this.period));
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 0L, this.period);
    }

    private void appendList(List<StacktraceElement> list, Coroutine coroutine) {
        while (coroutine != null) {
            LuaCallFrame[] luaCallFrames = coroutine.getCallframeStack();
            int int0 = Math.min(luaCallFrames.length, coroutine.getCallframeTop());

            for (int int1 = int0 - 1; int1 >= 0; int1--) {
                LuaCallFrame luaCallFrame = luaCallFrames[int1];
                int int2 = luaCallFrame.pc - 1;
                LuaClosure luaClosure = luaCallFrame.closure;
                JavaFunction javaFunction = luaCallFrame.javaFunction;
                if (luaClosure != null) {
                    list.add(new LuaStacktraceElement(int2, luaClosure.prototype));
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
