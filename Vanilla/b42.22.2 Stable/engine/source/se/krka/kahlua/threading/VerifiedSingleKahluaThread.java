// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.threading;

import java.io.PrintStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.Platform;

public class VerifiedSingleKahluaThread extends KahluaThread {
    private final Lock lock = new ReentrantLock();

    public VerifiedSingleKahluaThread(Platform platform, KahluaTable environment) {
        super(platform, environment);
    }

    public VerifiedSingleKahluaThread(PrintStream stream, Platform platform, KahluaTable environment) {
        super(stream, platform, environment);
    }

    private void lock() {
        if (!this.lock.tryLock()) {
            throw new IllegalStateException("Multiple threads may not access the same lua thread");
        }
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
    public void setmetatable(Object o, KahluaTable metatable) {
        this.lock();

        try {
            super.setmetatable(o, metatable);
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
}
