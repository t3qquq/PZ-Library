// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands.serverCommands;

public class ArgType {
    public static final String PlayerName = "(.+)";
    public static final String AnyText = "(.+)";
    public static final String Script = "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)";
    public static final String Coordinates = "(\\d+),(\\d+),(\\d+)";
    public static final String IP = "((?:\\d{1,3}\\.){3}\\d{1,3})";
    public static final String TrueFalse = "(-true|-false)";
    public static final String ItemName = "([a-zA-Z0-9.-]*[a-zA-Z][a-zA-Z0-9_.-]*)";
    public static final String Value = "(\\d+)";
}
