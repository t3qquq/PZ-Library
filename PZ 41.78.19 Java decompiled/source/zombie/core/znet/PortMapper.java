// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public class PortMapper {
    private static String externalAddress = null;

    public static void startup() {
    }

    public static void shutdown() {
        _cleanup();
    }

    public static boolean discover() {
        _discover();
        return _igd_found();
    }

    public static boolean igdFound() {
        return _igd_found();
    }

    public static boolean addMapping(int int0, int int1, String string0, String string1, int int2) {
        return addMapping(int0, int1, string0, string1, int2, false);
    }

    public static boolean addMapping(int int0, int int1, String string0, String string1, int int2, boolean boolean1) {
        boolean boolean0 = _add_mapping(int0, int1, string0, string1, int2, boolean1);
        if (!boolean0 && int2 != 0) {
            DebugLog.log(DebugType.Network, "Failed to add port mapping, retrying with zero lease time");
            boolean0 = _add_mapping(int0, int1, string0, string1, 0, boolean1);
        }

        return boolean0;
    }

    public static boolean removeMapping(int int0, String string) {
        return _remove_mapping(int0, string);
    }

    public static void fetchMappings() {
        _fetch_mappings();
    }

    public static int numMappings() {
        return _num_mappings();
    }

    public static PortMappingEntry getMapping(int int0) {
        return _get_mapping(int0);
    }

    public static String getGatewayInfo() {
        return _get_gateway_info();
    }

    public static synchronized String getExternalAddress(boolean boolean0) {
        if (boolean0 || externalAddress == null) {
            externalAddress = _get_external_address();
        }

        return externalAddress;
    }

    public static String getExternalAddress() {
        return getExternalAddress(false);
    }

    private static native void _discover();

    private static native void _cleanup();

    private static native boolean _igd_found();

    private static native boolean _add_mapping(int var0, int var1, String var2, String var3, int var4, boolean var5);

    private static native boolean _remove_mapping(int var0, String var1);

    private static native void _fetch_mappings();

    private static native int _num_mappings();

    private static native PortMappingEntry _get_mapping(int var0);

    private static native String _get_gateway_info();

    private static native String _get_external_address();
}
