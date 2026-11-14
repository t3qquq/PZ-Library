// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package se.krka.kahlua.vm;

public interface Platform {
    double pow(double arg0, double arg1);

    KahluaTable newTable();

    KahluaTable newEnvironment();

    void setupEnvironment(KahluaTable arg0);
}
