// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.Lua;

import java.util.ArrayList;
import se.krka.kahlua.integration.LuaCaller;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;

public final class Event {
    public static final int ADD = 0;
    public static final int NUM_FUNCTIONS = 1;
    private final Event.Add add;
    private final Event.Remove remove;
    public final ArrayList<LuaClosure> callbacks = new ArrayList<>();
    public String name;
    private int index = 0;

    public boolean trigger(KahluaTable var1, LuaCaller luaCaller, Object[] objects) {
        if (this.callbacks.isEmpty()) {
            return false;
        } else if (DebugOptions.instance.Checks.SlowLuaEvents.getValue()) {
            for (int int0 = 0; int0 < this.callbacks.size(); int0++) {
                try {
                    LuaClosure luaClosure = this.callbacks.get(int0);
                    long long0 = System.nanoTime();
                    luaCaller.protectedCallVoid(LuaManager.thread, luaClosure, objects);
                    double double0 = (System.nanoTime() - long0) / 1000000.0;
                    if (double0 > 250.0) {
                        DebugLog.Lua.warn("SLOW Lua event callback %s %s %dms", luaClosure.prototype.file, luaClosure, (int)double0);
                    }
                } catch (Exception exception0) {
                    ExceptionLogger.logException(exception0);
                }
            }

            return true;
        } else {
            for (int int1 = 0; int1 < this.callbacks.size(); int1++) {
                try {
                    luaCaller.protectedCallVoid(LuaManager.thread, this.callbacks.get(int1), objects);
                } catch (Exception exception1) {
                    ExceptionLogger.logException(exception1);
                }
            }

            return true;
        }
    }

    public Event(String _name, int _index) {
        this.index = _index;
        this.name = _name;
        this.add = new Event.Add(this);
        this.remove = new Event.Remove(this);
    }

    public void register(Platform platform, KahluaTable environment) {
        KahluaTable table = platform.newTable();
        table.rawset("Add", this.add);
        table.rawset("Remove", this.remove);
        environment.rawset(this.name, table);
    }

    public static final class Add implements JavaFunction {
        Event e;

        public Add(Event _e) {
            this.e = _e;
        }

        /**
         * Description copied from interface: se.krka.kahlua.vm.JavaFunction
         * @return N, number of return values. The top N objects on the stack are considered the return values.
         */
        @Override
        public int call(LuaCallFrame callFrame, int nArguments) {
            if (LuaCompiler.rewriteEvents) {
                return 0;
            } else {
                Object object = callFrame.get(0);
                if (this.e.name.contains("CreateUI")) {
                    boolean boolean0 = false;
                }

                if (object instanceof LuaClosure luaClosure) {
                    this.e.callbacks.add(luaClosure);
                }

                return 0;
            }
        }
    }

    public static final class Remove implements JavaFunction {
        Event e;

        public Remove(Event _e) {
            this.e = _e;
        }

        /**
         * Description copied from interface: se.krka.kahlua.vm.JavaFunction
         * @return N, number of return values. The top N objects on the stack are considered the return values.
         */
        @Override
        public int call(LuaCallFrame callFrame, int nArguments) {
            if (LuaCompiler.rewriteEvents) {
                return 0;
            } else {
                if (callFrame.get(0) instanceof LuaClosure luaClosure) {
                    this.e.callbacks.remove(luaClosure);
                }

                return 0;
            }
        }
    }
}
