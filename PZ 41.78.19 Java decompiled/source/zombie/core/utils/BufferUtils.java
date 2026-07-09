// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BufferUtils {
    private static boolean trackDirectMemory = false;
    private static final ReferenceQueue<Buffer> removeCollected = new ReferenceQueue<>();
    private static final ConcurrentHashMap<BufferUtils.BufferInfo, BufferUtils.BufferInfo> trackedBuffers = new ConcurrentHashMap<>();
    static BufferUtils.ClearReferences cleanupThread;
    private static final AtomicBoolean loadedMethods = new AtomicBoolean(false);
    private static Method cleanerMethod = null;
    private static Method cleanMethod = null;
    private static Method viewedBufferMethod = null;
    private static Method freeMethod = null;

    public static void setTrackDirectMemoryEnabled(boolean boolean0) {
        trackDirectMemory = boolean0;
    }

    private static void onBufferAllocated(Buffer buffer) {
        if (trackDirectMemory) {
            if (cleanupThread == null) {
                cleanupThread = new BufferUtils.ClearReferences();
                cleanupThread.start();
            }

            if (buffer instanceof ByteBuffer) {
                BufferUtils.BufferInfo bufferInfo0 = new BufferUtils.BufferInfo(ByteBuffer.class, buffer.capacity(), buffer, removeCollected);
                trackedBuffers.put(bufferInfo0, bufferInfo0);
            } else if (buffer instanceof FloatBuffer) {
                BufferUtils.BufferInfo bufferInfo1 = new BufferUtils.BufferInfo(FloatBuffer.class, buffer.capacity() * 4, buffer, removeCollected);
                trackedBuffers.put(bufferInfo1, bufferInfo1);
            } else if (buffer instanceof IntBuffer) {
                BufferUtils.BufferInfo bufferInfo2 = new BufferUtils.BufferInfo(IntBuffer.class, buffer.capacity() * 4, buffer, removeCollected);
                trackedBuffers.put(bufferInfo2, bufferInfo2);
            } else if (buffer instanceof ShortBuffer) {
                BufferUtils.BufferInfo bufferInfo3 = new BufferUtils.BufferInfo(ShortBuffer.class, buffer.capacity() * 2, buffer, removeCollected);
                trackedBuffers.put(bufferInfo3, bufferInfo3);
            } else if (buffer instanceof DoubleBuffer) {
                BufferUtils.BufferInfo bufferInfo4 = new BufferUtils.BufferInfo(DoubleBuffer.class, buffer.capacity() * 8, buffer, removeCollected);
                trackedBuffers.put(bufferInfo4, bufferInfo4);
            }
        }
    }

    public static void printCurrentDirectMemory(StringBuilder stringBuilder) {
        long long0 = 0L;
        long long1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        boolean boolean0 = stringBuilder == null;
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
        }

        if (trackDirectMemory) {
            int int0 = 0;
            int int1 = 0;
            int int2 = 0;
            int int3 = 0;
            int int4 = 0;
            int int5 = 0;
            int int6 = 0;
            int int7 = 0;
            int int8 = 0;
            int int9 = 0;

            for (BufferUtils.BufferInfo bufferInfo : trackedBuffers.values()) {
                if (bufferInfo.type == ByteBuffer.class) {
                    long0 += bufferInfo.size;
                    int6 += bufferInfo.size;
                    int1++;
                } else if (bufferInfo.type == FloatBuffer.class) {
                    long0 += bufferInfo.size;
                    int5 += bufferInfo.size;
                    int0++;
                } else if (bufferInfo.type == IntBuffer.class) {
                    long0 += bufferInfo.size;
                    int7 += bufferInfo.size;
                    int2++;
                } else if (bufferInfo.type == ShortBuffer.class) {
                    long0 += bufferInfo.size;
                    int8 += bufferInfo.size;
                    int3++;
                } else if (bufferInfo.type == DoubleBuffer.class) {
                    long0 += bufferInfo.size;
                    int9 += bufferInfo.size;
                    int4++;
                }
            }

            stringBuilder.append("Existing buffers: ").append(trackedBuffers.size()).append("\n");
            stringBuilder.append("(b: ")
                .append(int1)
                .append("  f: ")
                .append(int0)
                .append("  i: ")
                .append(int2)
                .append("  s: ")
                .append(int3)
                .append("  d: ")
                .append(int4)
                .append(")")
                .append("\n");
            stringBuilder.append("Total   heap memory held: ").append(long1 / 1024L).append("kb\n");
            stringBuilder.append("Total direct memory held: ").append(long0 / 1024L).append("kb\n");
            stringBuilder.append("(b: ")
                .append(int6 / 1024)
                .append("kb  f: ")
                .append(int5 / 1024)
                .append("kb  i: ")
                .append(int7 / 1024)
                .append("kb  s: ")
                .append(int8 / 1024)
                .append("kb  d: ")
                .append(int9 / 1024)
                .append("kb)")
                .append("\n");
        } else {
            stringBuilder.append("Total   heap memory held: ").append(long1 / 1024L).append("kb\n");
            stringBuilder.append(
                    "Only heap memory available, if you want to monitor direct memory use BufferUtils.setTrackDirectMemoryEnabled(true) during initialization."
                )
                .append("\n");
        }

        if (boolean0) {
            System.out.println(stringBuilder.toString());
        }
    }

    private static Method loadMethod(String string1, String string0) {
        try {
            Method method = Class.forName(string1).getMethod(string0);
            method.setAccessible(true);
            return method;
        } catch (SecurityException | ClassNotFoundException | NoSuchMethodException noSuchMethodException) {
            return null;
        }
    }

    public static ByteBuffer createByteBuffer(int int0) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(int0).order(ByteOrder.nativeOrder());
        byteBuffer.clear();
        onBufferAllocated(byteBuffer);
        return byteBuffer;
    }

    private static void loadCleanerMethods() {
        if (!loadedMethods.getAndSet(true)) {
            synchronized (loadedMethods) {
                cleanerMethod = loadMethod("sun.nio.ch.DirectBuffer", "cleaner");
                viewedBufferMethod = loadMethod("sun.nio.ch.DirectBuffer", "viewedBuffer");
                if (viewedBufferMethod == null) {
                    viewedBufferMethod = loadMethod("sun.nio.ch.DirectBuffer", "attachment");
                }

                ByteBuffer byteBuffer = createByteBuffer(1);
                Class clazz = byteBuffer.getClass();

                try {
                    freeMethod = clazz.getMethod("free");
                } catch (SecurityException | NoSuchMethodException noSuchMethodException) {
                }
            }
        }
    }

    public static void destroyDirectBuffer(Buffer buffer) {
        if (buffer.isDirect()) {
            loadCleanerMethods();

            try {
                if (freeMethod != null) {
                    freeMethod.invoke(buffer);
                } else {
                    Object object0 = cleanerMethod.invoke(buffer);
                    if (object0 == null) {
                        Object object1 = viewedBufferMethod.invoke(buffer);
                        if (object1 != null) {
                            destroyDirectBuffer((Buffer)object1);
                        } else {
                            Logger.getLogger(BufferUtils.class.getName()).log(Level.SEVERE, "Buffer cannot be destroyed: {0}", buffer);
                        }
                    }
                }
            } catch (IllegalArgumentException | InvocationTargetException | SecurityException | IllegalAccessException illegalAccessException) {
                Logger.getLogger(BufferUtils.class.getName()).log(Level.SEVERE, "{0}", (Throwable)illegalAccessException);
            }
        }
    }

    private static class BufferInfo extends PhantomReference<Buffer> {
        private final Class type;
        private final int size;

        public BufferInfo(Class clazz, int int0, Buffer buffer, ReferenceQueue<? super Buffer> referenceQueue) {
            super(buffer, referenceQueue);
            this.type = clazz;
            this.size = int0;
        }
    }

    private static class ClearReferences extends Thread {
        ClearReferences() {
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Reference reference = BufferUtils.removeCollected.remove();
                    BufferUtils.trackedBuffers.remove(reference);
                }
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }
}
