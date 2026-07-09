// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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

public class BlockingKahluaThread extends KahluaThread {
    private final Lock lock = new ReentrantLock();

    public BlockingKahluaThread(Platform platform, KahluaTable table) {
        super(platform, table);
    }

    public BlockingKahluaThread(PrintStream printStream, Platform platform, KahluaTable table) {
        super(printStream, platform, table);
    }

    private void lock() {
        this.lock.lock();
    }

    private void unlock() {
        this.lock.unlock();
    }

    @Override
    public int call(int int1) {
        this.lock();

        int int0;
        try {
            int0 = super.call(int1);
        } finally {
            this.unlock();
        }

        return int0;
    }

    @Override
    public int pcall(int int1) {
        this.lock();

        int int0;
        try {
            int0 = super.pcall(int1);
        } finally {
            this.unlock();
        }

        return int0;
    }

    @Override
    public Object[] pcall(Object object) {
        this.lock();

        Object[] objects;
        try {
            objects = super.pcall(object);
        } finally {
            this.unlock();
        }

        return objects;
    }

    @Override
    public final Object[] pcall(Object object, Object[] objects1) {
        this.lock();

        Object[] objects0;
        try {
            objects0 = super.pcall(object, objects1);
        } finally {
            this.unlock();
        }

        return objects0;
    }

    @Override
    public Object call(Object object1, Object object2, Object object3, Object object4) {
        this.lock();

        Object object0;
        try {
            object0 = super.call(object1, object2, object3, object4);
        } finally {
            this.unlock();
        }

        return object0;
    }

    @Override
    public Object call(Object object1, Object[] objects) {
        this.lock();

        Object object0;
        try {
            object0 = super.call(object1, objects);
        } finally {
            this.unlock();
        }

        return object0;
    }

    @Override
    public KahluaTable getEnvironment() {
        this.lock();

        KahluaTable table;
        try {
            table = super.getEnvironment();
        } finally {
            this.unlock();
        }

        return table;
    }

    @Override
    public Object getMetaOp(Object object1, String string) {
        this.lock();

        Object object0;
        try {
            object0 = super.getMetaOp(object1, string);
        } finally {
            this.unlock();
        }

        return object0;
    }

    @Override
    public Object getmetatable(Object object1, boolean boolean0) {
        this.lock();

        Object object0;
        try {
            object0 = super.getmetatable(object1, boolean0);
        } finally {
            this.unlock();
        }

        return object0;
    }

    @Override
    public void setmetatable(Object object, KahluaTable table) {
        this.lock();

        try {
            super.setmetatable(object, table);
        } finally {
            this.unlock();
        }
    }

    @Override
    public Object tableget(Object object1, Object object2) {
        this.lock();

        Object object0;
        try {
            object0 = super.tableget(object1, object2);
        } finally {
            this.unlock();
        }

        return object0;
    }

    @Override
    public void tableSet(Object object0, Object object1, Object object2) {
        this.lock();

        try {
            super.tableSet(object0, object1, object2);
        } finally {
            this.unlock();
        }
    }

    public static void main(String[] var0) throws IOException, InterruptedException {
        J2SEPlatform j2SEPlatform = new J2SEPlatform();
        KahluaTable table = j2SEPlatform.newEnvironment();
        final BlockingKahluaThread blockingKahluaThread = new BlockingKahluaThread(j2SEPlatform, table);
        final LuaClosure luaClosure0 = LuaCompiler.loadstring("x = (x or 0) + 1", "", blockingKahluaThread.getEnvironment());
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        for (int int0 = 0; int0 < 100; int0++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int int0x = 0; int0x < 100; int0x++) {
                        try {
                            Thread.sleep((long)(Math.random() * 10.0));
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                        blockingKahluaThread.pcall(luaClosure0);
                    }

                    atomicInteger.incrementAndGet();
                }
            }).start();
        }

        while (atomicInteger.get() != 100) {
            Thread.sleep(100L);
        }

        LuaClosure luaClosure1 = LuaCompiler.loadstring("print('x='..x)", "", blockingKahluaThread.getEnvironment());
        blockingKahluaThread.pcall(luaClosure1);
    }
}
