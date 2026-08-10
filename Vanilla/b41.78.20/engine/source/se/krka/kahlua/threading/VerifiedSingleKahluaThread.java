/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.threading;

import java.io.PrintStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.Platform;

public class VerifiedSingleKahluaThread
extends KahluaThread {
    private final Lock lock = new ReentrantLock();

    public VerifiedSingleKahluaThread(Platform platform, KahluaTable kahluaTable) {
        super(platform, kahluaTable);
    }

    public VerifiedSingleKahluaThread(PrintStream printStream, Platform platform, KahluaTable kahluaTable) {
        super(printStream, platform, kahluaTable);
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
    public int call(int n) {
        this.lock();
        try {
            int n2 = super.call(n);
            return n2;
        }
        finally {
            this.unlock();
        }
    }

    @Override
    public int pcall(int n) {
        this.lock();
        try {
            int n2 = super.pcall(n);
            return n2;
        }
        finally {
            this.unlock();
        }
    }

    @Override
    public Object[] pcall(Object object) {
        this.lock();
        try {
            Object[] objectArray = super.pcall(object);
            return objectArray;
        }
        finally {
            this.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Object[] pcall(Object object, Object[] objectArray) {
        this.lock();
        try {
            Object[] objectArray2 = super.pcall(object, objectArray);
            return objectArray2;
        }
        finally {
            this.unlock();
        }
    }

    @Override
    public void setmetatable(Object object, KahluaTable kahluaTable) {
        this.lock();
        try {
            super.setmetatable(object, kahluaTable);
        }
        finally {
            this.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object call(Object object, Object object2, Object object3, Object object4) {
        this.lock();
        try {
            Object object5 = super.call(object, object2, object3, object4);
            return object5;
        }
        finally {
            this.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object call(Object object, Object[] objectArray) {
        this.lock();
        try {
            Object object2 = super.call(object, objectArray);
            return object2;
        }
        finally {
            this.unlock();
        }
    }

    @Override
    public KahluaTable getEnvironment() {
        this.lock();
        try {
            KahluaTable kahluaTable = super.getEnvironment();
            return kahluaTable;
        }
        finally {
            this.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getMetaOp(Object object, String string) {
        this.lock();
        try {
            Object object2 = super.getMetaOp(object, string);
            return object2;
        }
        finally {
            this.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getmetatable(Object object, boolean bl) {
        this.lock();
        try {
            Object object2 = super.getmetatable(object, bl);
            return object2;
        }
        finally {
            this.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object tableget(Object object, Object object2) {
        this.lock();
        try {
            Object object3 = super.tableget(object, object2);
            return object3;
        }
        finally {
            this.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void tableSet(Object object, Object object2, Object object3) {
        this.lock();
        try {
            super.tableSet(object, object2, object3);
        }
        finally {
            this.unlock();
        }
    }
}

