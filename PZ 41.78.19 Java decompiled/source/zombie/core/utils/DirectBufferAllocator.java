// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.util.ArrayList;

public final class DirectBufferAllocator {
    private static final Object LOCK = "DirectBufferAllocator.LOCK";
    private static final ArrayList<WrappedBuffer> ALL = new ArrayList<>();

    public static WrappedBuffer allocate(int int0) {
        synchronized (LOCK) {
            destroyDisposed();
            WrappedBuffer wrappedBuffer = new WrappedBuffer(int0);
            ALL.add(wrappedBuffer);
            return wrappedBuffer;
        }
    }

    private static void destroyDisposed() {
        synchronized (LOCK) {
            for (int int0 = ALL.size() - 1; int0 >= 0; int0--) {
                WrappedBuffer wrappedBuffer = ALL.get(int0);
                if (wrappedBuffer.isDisposed()) {
                    ALL.remove(int0);
                }
            }
        }
    }

    public static long getBytesAllocated() {
        synchronized (LOCK) {
            destroyDisposed();
            long long0 = 0L;

            for (int int0 = 0; int0 < ALL.size(); int0++) {
                WrappedBuffer wrappedBuffer = ALL.get(int0);
                if (!wrappedBuffer.isDisposed()) {
                    long0 += wrappedBuffer.capacity();
                }
            }

            return long0;
        }
    }
}
