/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import se.krka.kahlua.profiler.JavaStacktraceElement;
import se.krka.kahlua.profiler.LuaStacktraceElement;
import se.krka.kahlua.profiler.Profiler;
import se.krka.kahlua.profiler.Sample;
import se.krka.kahlua.profiler.StacktraceElement;
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

    public Sampler(KahluaThread kahluaThread, long l, Profiler profiler) {
        this.thread = kahluaThread;
        this.period = l;
        this.profiler = profiler;
        this.timer = new Timer("Kahlua Sampler-" + NEXT_ID.incrementAndGet(), true);
    }

    public void start() {
        TimerTask timerTask = new TimerTask(){

            @Override
            public void run() {
                ArrayList<StacktraceElement> arrayList = new ArrayList<StacktraceElement>();
                Sampler.this.appendList(arrayList, Sampler.this.thread.currentCoroutine);
                Sampler.this.profiler.getSample(new Sample(arrayList, Sampler.this.period));
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 0L, this.period);
    }

    private void appendList(List<StacktraceElement> list, Coroutine coroutine) {
        while (coroutine != null) {
            LuaCallFrame[] luaCallFrameArray = coroutine.getCallframeStack();
            int n = Math.min(luaCallFrameArray.length, coroutine.getCallframeTop());
            for (int i = n - 1; i >= 0; --i) {
                LuaCallFrame luaCallFrame = luaCallFrameArray[i];
                int n2 = luaCallFrame.pc - 1;
                LuaClosure luaClosure = luaCallFrame.closure;
                JavaFunction javaFunction = luaCallFrame.javaFunction;
                if (luaClosure != null) {
                    list.add(new LuaStacktraceElement(n2, luaClosure.prototype));
                    continue;
                }
                if (javaFunction == null) continue;
                list.add(new JavaStacktraceElement(javaFunction));
            }
            coroutine = coroutine.getParent();
        }
    }

    public void stop() {
        this.timer.cancel();
    }
}

