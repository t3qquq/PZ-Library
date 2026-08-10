// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

/**
 * ILuaVariableSource   Provides the functions expected by LUA when dealing with objects of this type.
 */
public interface ILuaVariableSource {
    String GetVariable(String key);

    void SetVariable(String key, String value);

    void ClearVariable(String key);
}
