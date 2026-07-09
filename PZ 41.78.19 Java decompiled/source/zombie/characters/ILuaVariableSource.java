// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

/**
 * ILuaVariableSource   Provides the functions expected by LUA when dealing with objects of this type.
 */
public interface ILuaVariableSource {
    String GetVariable(String key);

    void SetVariable(String key, String value);

    void ClearVariable(String key);
}
