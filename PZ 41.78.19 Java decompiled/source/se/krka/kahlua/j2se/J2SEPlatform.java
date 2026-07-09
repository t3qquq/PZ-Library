// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.j2se;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.stdlib.BaseLib;
import se.krka.kahlua.stdlib.CoroutineLib;
import se.krka.kahlua.stdlib.OsLib;
import se.krka.kahlua.stdlib.RandomLib;
import se.krka.kahlua.stdlib.StringLib;
import se.krka.kahlua.stdlib.TableLib;
import se.krka.kahlua.test.UserdataArray;
import se.krka.kahlua.threading.BlockingKahluaThread;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;

public class J2SEPlatform implements Platform {
    private static J2SEPlatform INSTANCE = new J2SEPlatform();

    public static J2SEPlatform getInstance() {
        return INSTANCE;
    }

    @Override
    public double pow(double arg0, double arg1) {
        return Math.pow(arg0, arg1);
    }

    @Override
    public KahluaTable newTable() {
        return new KahluaTableImpl(new LinkedHashMap<>());
    }

    @Override
    public KahluaTable newEnvironment() {
        KahluaTable table = this.newTable();
        this.setupEnvironment(table);
        return table;
    }

    @Override
    public void setupEnvironment(KahluaTable arg0) {
        arg0.wipe();
        arg0.rawset("_G", arg0);
        arg0.rawset("_VERSION", "Kahlua kahlua.major.kahlua.minor.kahlua.fix for Lua lua.version (J2SE)");
        MathLib.register(this, arg0);
        BaseLib.register(arg0);
        RandomLib.register(this, arg0);
        UserdataArray.register(this, arg0);
        StringLib.register(this, arg0);
        CoroutineLib.register(this, arg0);
        OsLib.register(this, arg0);
        TableLib.register(this, arg0);
        LuaCompiler.register(arg0);
        KahluaThread kahluaThread = this.setupWorkerThread(arg0);
        KahluaUtil.setupLibrary(arg0, kahluaThread, "/stdlib");
        File file = new File("serialize.lua").getAbsoluteFile();

        try {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                LuaClosure luaClosure = LuaCompiler.loadis(fileInputStream, "serialize.lua", arg0);
                kahluaThread.call(luaClosure, null, null, null);
            }
        } catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    private KahluaThread setupWorkerThread(KahluaTable table) {
        BlockingKahluaThread blockingKahluaThread = new BlockingKahluaThread(this, table);
        KahluaUtil.setWorkerThread(table, blockingKahluaThread);
        return blockingKahluaThread;
    }
}
