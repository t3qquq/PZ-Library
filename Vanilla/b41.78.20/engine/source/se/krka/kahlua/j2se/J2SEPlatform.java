/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.j2se;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.j2se.MathLib;
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

public class J2SEPlatform
implements Platform {
    private static J2SEPlatform INSTANCE = new J2SEPlatform();

    public static J2SEPlatform getInstance() {
        return INSTANCE;
    }

    @Override
    public double pow(double d, double d2) {
        return Math.pow(d, d2);
    }

    @Override
    public KahluaTable newTable() {
        return new KahluaTableImpl(new LinkedHashMap<Object, Object>());
    }

    @Override
    public KahluaTable newEnvironment() {
        KahluaTable kahluaTable = this.newTable();
        this.setupEnvironment(kahluaTable);
        return kahluaTable;
    }

    @Override
    public void setupEnvironment(KahluaTable kahluaTable) {
        kahluaTable.wipe();
        kahluaTable.rawset("_G", (Object)kahluaTable);
        kahluaTable.rawset("_VERSION", (Object)"Kahlua kahlua.major.kahlua.minor.kahlua.fix for Lua lua.version (J2SE)");
        MathLib.register(this, kahluaTable);
        BaseLib.register(kahluaTable);
        RandomLib.register(this, kahluaTable);
        UserdataArray.register(this, kahluaTable);
        StringLib.register(this, kahluaTable);
        CoroutineLib.register(this, kahluaTable);
        OsLib.register(this, kahluaTable);
        TableLib.register(this, kahluaTable);
        LuaCompiler.register(kahluaTable);
        KahluaThread kahluaThread = this.setupWorkerThread(kahluaTable);
        KahluaUtil.setupLibrary(kahluaTable, kahluaThread, "/stdlib");
        File file = new File("serialize.lua").getAbsoluteFile();
        try (FileInputStream fileInputStream = new FileInputStream(file);){
            LuaClosure luaClosure = LuaCompiler.loadis(fileInputStream, "serialize.lua", kahluaTable);
            kahluaThread.call(luaClosure, null, null, null);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    private KahluaThread setupWorkerThread(KahluaTable kahluaTable) {
        BlockingKahluaThread blockingKahluaThread = new BlockingKahluaThread(this, kahluaTable);
        KahluaUtil.setWorkerThread(kahluaTable, blockingKahluaThread);
        return blockingKahluaThread;
    }
}

