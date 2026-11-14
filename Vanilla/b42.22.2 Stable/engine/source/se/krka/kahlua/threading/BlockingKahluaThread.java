// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.threading;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;

public class BlockingKahluaThread extends KahluaThread {
    private final Lock lock = new ReentrantLock();

    public BlockingKahluaThread(Platform platform, KahluaTable environment) {
        super(platform, environment);
    }

    public BlockingKahluaThread(PrintStream stream, Platform platform, KahluaTable environment) {
        super(stream, platform, environment);
    }

    private void lock() {
        this.lock.lock();
    }

    private void unlock() {
        this.lock.unlock();
    }

    @Override
    public int call(int arguments) {
        this.lock();

        try {
            return super.call(arguments);
        } finally {
            this.unlock();
        }
    }

    @Override
    public int pcall(int arguments) {
        this.lock();

        try {
            return super.pcall(arguments);
        } finally {
            this.unlock();
        }
    }

    @Override
    public Object[] pcall(Object fun) {
        this.lock();

        try {
            return super.pcall(fun);
        } finally {
            this.unlock();
        }
    }

    @Override
    public final Object[] pcall(Object fun, Object[] args) {
        this.lock();

        try {
            return super.pcall(fun, args);
        } finally {
            this.unlock();
        }
    }

    @Override
    public Object call(Object fun, Object arg1, Object arg2, Object arg3) {
        this.lock();

        try {
            return super.call(fun, arg1, arg2, arg3);
        } finally {
            this.unlock();
        }
    }

    @Override
    public Object call(Object fun, Object[] args) {
        this.lock();

        try {
            return super.call(fun, args);
        } finally {
            this.unlock();
        }
    }

    @Override
    public KahluaTable getEnvironment() {
        this.lock();

        try {
            return super.getEnvironment();
        } finally {
            this.unlock();
        }
    }

    @Override
    public Object getMetaOp(Object o, String meta_op) {
        this.lock();

        try {
            return super.getMetaOp(o, meta_op);
        } finally {
            this.unlock();
        }
    }

    @Override
    public Object getmetatable(Object o, boolean raw) {
        this.lock();

        try {
            return super.getmetatable(o, raw);
        } finally {
            this.unlock();
        }
    }

    @Override
    public void setmetatable(Object o, KahluaTable metatable) {
        this.lock();

        try {
            super.setmetatable(o, metatable);
        } finally {
            this.unlock();
        }
    }

    @Override
    public Object tableget(Object table, Object key) {
        this.lock();

        try {
            return super.tableget(table, key);
        } finally {
            this.unlock();
        }
    }

    @Override
    public void tableSet(Object table, Object key, Object value) {
        this.lock();

        try {
            super.tableSet(table, key, value);
        } finally {
            this.unlock();
        }
    }

    static void main(String[] args) throws IOException, InterruptedException {
        Platform platform = new J2SEPlatform();
        KahluaTable env = platform.newEnvironment();
        final BlockingKahluaThread state = new BlockingKahluaThread(platform, env);
        final LuaClosure c = LuaCompiler.loadstring("x = (x or 0) + 1", "", state.getEnvironment());
        final AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int ix = 0; ix < 100; ix++) {
                        try {
                            Thread.sleep((long)(Math.random() * 10.0));
                        } catch (InterruptedException e) {
                            DebugType.General.printException(e, LogSeverity.Error);
                        }

                        state.pcall(c);
                    }

                    counter.incrementAndGet();
                }
            }).start();
        }

        while (counter.get() != 100) {
            Thread.sleep(100L);
        }

        LuaClosure c2 = LuaCompiler.loadstring("print('x='..x)", "", state.getEnvironment());
        state.pcall(c2);
    }
}
