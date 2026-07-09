// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class LWJGLUtil {
    public static final int PLATFORM_LINUX = 1;
    public static final int PLATFORM_MACOSX = 2;
    public static final int PLATFORM_WINDOWS = 3;
    public static final String PLATFORM_LINUX_NAME = "linux";
    public static final String PLATFORM_MACOSX_NAME = "macosx";
    public static final String PLATFORM_WINDOWS_NAME = "windows";
    public static final boolean DEBUG = getPrivilegedBoolean("org.lwjgl.util.Debug");
    public static final boolean CHECKS = !getPrivilegedBoolean("org.lwjgl.util.NoChecks");
    private static final int PLATFORM;

    public static int getPlatform() {
        return PLATFORM;
    }

    public static String getPlatformName() {
        switch (getPlatform()) {
            case 1:
                return "linux";
            case 2:
                return "macosx";
            case 3:
                return "windows";
            default:
                return "unknown";
        }
    }

    private static String getPrivilegedProperty(final String string) {
        return AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                return System.getProperty(string);
            }
        });
    }

    public static boolean getPrivilegedBoolean(final String string) {
        return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return Boolean.getBoolean(string);
            }
        });
    }

    public static Integer getPrivilegedInteger(final String string) {
        return AccessController.doPrivileged(new PrivilegedAction<Integer>() {
            public Integer run() {
                return Integer.getInteger(string);
            }
        });
    }

    public static Integer getPrivilegedInteger(final String string, final int int0) {
        return AccessController.doPrivileged(new PrivilegedAction<Integer>() {
            public Integer run() {
                return Integer.getInteger(string, int0);
            }
        });
    }

    static {
        String string = getPrivilegedProperty("os.name");
        if (string.startsWith("Windows")) {
            PLATFORM = 3;
        } else if (!string.startsWith("Linux") && !string.startsWith("FreeBSD") && !string.startsWith("SunOS") && !string.startsWith("Unix")) {
            if (!string.startsWith("Mac OS X") && !string.startsWith("Darwin")) {
                throw new LinkageError("Unknown platform: " + string);
            }

            PLATFORM = 2;
        } else {
            PLATFORM = 1;
        }
    }
}
