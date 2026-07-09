// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.commands.serverCommands.AddAllToWhiteListCommand;
import zombie.commands.serverCommands.AddItemCommand;
import zombie.commands.serverCommands.AddUserCommand;
import zombie.commands.serverCommands.AddUserToWhiteListCommand;
import zombie.commands.serverCommands.AddVehicleCommand;
import zombie.commands.serverCommands.AddXPCommand;
import zombie.commands.serverCommands.AlarmCommand;
import zombie.commands.serverCommands.BanSteamIDCommand;
import zombie.commands.serverCommands.BanUserCommand;
import zombie.commands.serverCommands.ChangeOptionCommand;
import zombie.commands.serverCommands.CheckModsNeedUpdate;
import zombie.commands.serverCommands.ChopperCommand;
import zombie.commands.serverCommands.ClearCommand;
import zombie.commands.serverCommands.ConnectionsCommand;
import zombie.commands.serverCommands.CreateHorde2Command;
import zombie.commands.serverCommands.CreateHordeCommand;
import zombie.commands.serverCommands.DebugPlayerCommand;
import zombie.commands.serverCommands.GodModeCommand;
import zombie.commands.serverCommands.GrantAdminCommand;
import zombie.commands.serverCommands.GunShotCommand;
import zombie.commands.serverCommands.HelpCommand;
import zombie.commands.serverCommands.InvisibleCommand;
import zombie.commands.serverCommands.KickUserCommand;
import zombie.commands.serverCommands.LightningCommand;
import zombie.commands.serverCommands.LogCommand;
import zombie.commands.serverCommands.NoClipCommand;
import zombie.commands.serverCommands.PlayersCommand;
import zombie.commands.serverCommands.QuitCommand;
import zombie.commands.serverCommands.ReleaseSafehouseCommand;
import zombie.commands.serverCommands.ReloadLuaCommand;
import zombie.commands.serverCommands.ReloadOptionsCommand;
import zombie.commands.serverCommands.RemoveAdminCommand;
import zombie.commands.serverCommands.RemoveUserFromWhiteList;
import zombie.commands.serverCommands.RemoveZombiesCommand;
import zombie.commands.serverCommands.ReplayCommands;
import zombie.commands.serverCommands.SaveCommand;
import zombie.commands.serverCommands.ServerMessageCommand;
import zombie.commands.serverCommands.SetAccessLevelCommand;
import zombie.commands.serverCommands.ShowOptionsCommand;
import zombie.commands.serverCommands.StartRainCommand;
import zombie.commands.serverCommands.StartStormCommand;
import zombie.commands.serverCommands.StatisticsCommand;
import zombie.commands.serverCommands.StopRainCommand;
import zombie.commands.serverCommands.StopWeatherCommand;
import zombie.commands.serverCommands.TeleportCommand;
import zombie.commands.serverCommands.TeleportToCommand;
import zombie.commands.serverCommands.ThunderCommand;
import zombie.commands.serverCommands.UnbanSteamIDCommand;
import zombie.commands.serverCommands.UnbanUserCommand;
import zombie.commands.serverCommands.VoiceBanCommand;
import zombie.core.Translator;
import zombie.core.raknet.UdpConnection;

public abstract class CommandBase {
    private final int playerType;
    private final String username;
    private final String command;
    private String[] commandArgs;
    private boolean parsingSuccessful = false;
    private boolean parsed = false;
    private String message = "";
    protected final UdpConnection connection;
    protected String argsName = "default args name. Nothing match";
    protected static final String defaultArgsName = "default args name. Nothing match";
    protected final String description;
    private static Class[] childrenClasses = new Class[]{
        SaveCommand.class,
        ServerMessageCommand.class,
        ConnectionsCommand.class,
        AddUserCommand.class,
        GrantAdminCommand.class,
        RemoveAdminCommand.class,
        DebugPlayerCommand.class,
        QuitCommand.class,
        AlarmCommand.class,
        ChopperCommand.class,
        AddAllToWhiteListCommand.class,
        KickUserCommand.class,
        TeleportCommand.class,
        TeleportToCommand.class,
        ReleaseSafehouseCommand.class,
        StartRainCommand.class,
        StopRainCommand.class,
        ThunderCommand.class,
        GunShotCommand.class,
        ReloadOptionsCommand.class,
        BanUserCommand.class,
        BanSteamIDCommand.class,
        UnbanUserCommand.class,
        UnbanSteamIDCommand.class,
        AddUserToWhiteListCommand.class,
        RemoveUserFromWhiteList.class,
        ChangeOptionCommand.class,
        ShowOptionsCommand.class,
        GodModeCommand.class,
        VoiceBanCommand.class,
        NoClipCommand.class,
        InvisibleCommand.class,
        HelpCommand.class,
        ClearCommand.class,
        PlayersCommand.class,
        AddItemCommand.class,
        AddXPCommand.class,
        AddVehicleCommand.class,
        CreateHordeCommand.class,
        CreateHorde2Command.class,
        ReloadLuaCommand.class,
        RemoveZombiesCommand.class,
        SetAccessLevelCommand.class,
        LogCommand.class,
        StatisticsCommand.class,
        LightningCommand.class,
        StopWeatherCommand.class,
        StartStormCommand.class,
        ReplayCommands.class,
        CheckModsNeedUpdate.class
    };

    public static Class[] getSubClasses() {
        return childrenClasses;
    }

    public static Class findCommandCls(String string) {
        for (Class clazz : childrenClasses) {
            if (!isDisabled(clazz)) {
                CommandName[] commandNames = clazz.getAnnotationsByType(CommandName.class);

                for (CommandName commandName : commandNames) {
                    Pattern pattern = Pattern.compile("^" + commandName.name() + "\\b", 2);
                    if (pattern.matcher(string).find()) {
                        return clazz;
                    }
                }
            }
        }

        return null;
    }

    public static String getHelp(Class clazz) {
        CommandHelp commandHelp = getAnnotation(CommandHelp.class, clazz);
        if (commandHelp == null) {
            return null;
        } else if (commandHelp.shouldTranslated()) {
            String string = commandHelp.helpText();
            return Translator.getText(string);
        } else {
            return commandHelp.helpText();
        }
    }

    public static String getCommandName(Class clazz) {
        Annotation[] annotations = clazz.getAnnotationsByType(CommandName.class);
        return ((CommandName)annotations[0]).name();
    }

    public static boolean isDisabled(Class clazz) {
        DisabledCommand disabledCommand = getAnnotation(DisabledCommand.class, clazz);
        return disabledCommand != null;
    }

    public static int accessLevelToInt(String string) {
        switch (string) {
            case "admin":
                return 32;
            case "observer":
                return 2;
            case "moderator":
                return 16;
            case "overseer":
                return 8;
            case "gm":
                return 4;
            default:
                return 1;
        }
    }

    protected CommandBase(String string0, String string2, String string1, UdpConnection udpConnection) {
        this.username = string0;
        this.command = string1;
        this.connection = udpConnection;
        this.playerType = accessLevelToInt(string2);
        ArrayList arrayList = new ArrayList();
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".*?\")\\s*").matcher(string1);

        while (matcher.find()) {
            arrayList.add(matcher.group(1).replace("\"", ""));
        }

        this.commandArgs = new String[arrayList.size() - 1];

        for (int int0 = 1; int0 < arrayList.size(); int0++) {
            this.commandArgs[int0 - 1] = (String)arrayList.get(int0);
        }

        this.description = "cmd=\""
            + string1
            + "\" user=\""
            + string0
            + "\" role=\""
            + this.playerType
            + "\" "
            + (udpConnection != null ? "guid=\"" + udpConnection.getConnectedGUID() + "\" id=\"" + udpConnection.idStr : "unknown connection")
            + "\"";
    }

    public String Execute() throws SQLException {
        return this.canBeExecuted() ? this.Command() : this.message;
    }

    public boolean canBeExecuted() {
        if (this.parsed) {
            return this.parsingSuccessful;
        } else if (!this.PlayerSatisfyRequiredRights()) {
            this.message = this.playerHasNoRightError();
            return false;
        } else {
            this.parsingSuccessful = this.parseCommand();
            return this.parsingSuccessful;
        }
    }

    public boolean isCommandComeFromServerConsole() {
        return this.connection == null;
    }

    protected RequiredRight getRequiredRights() {
        return this.getClass().getAnnotation(RequiredRight.class);
    }

    protected CommandArgs[] getCommandArgVariants() {
        Class clazz = this.getClass();
        return clazz.getAnnotationsByType(CommandArgs.class);
    }

    public boolean hasHelp() {
        Class clazz = this.getClass();
        CommandHelp commandHelp = clazz.getAnnotation(CommandHelp.class);
        return commandHelp != null;
    }

    protected String getHelp() {
        Class clazz = this.getClass();
        return getHelp(clazz);
    }

    public String getCommandArg(Integer integer) {
        return this.commandArgs != null && integer >= 0 && integer < this.commandArgs.length ? this.commandArgs[integer] : null;
    }

    public boolean hasOptionalArg(Integer integer) {
        return this.commandArgs != null && integer >= 0 && integer < this.commandArgs.length;
    }

    public int getCommandArgsCount() {
        return this.commandArgs.length;
    }

    protected abstract String Command() throws SQLException;

    public boolean parseCommand() {
        CommandArgs[] commandArgss = this.getCommandArgVariants();
        if (commandArgss.length == 1 && commandArgss[0].varArgs()) {
            this.parsed = true;
            return true;
        } else {
            boolean boolean0 = commandArgss.length != 0 && this.commandArgs.length != 0 || commandArgss.length == 0 && this.commandArgs.length == 0;
            ArrayList arrayList = new ArrayList();

            for (CommandArgs commandArgsx : commandArgss) {
                arrayList.clear();
                this.message = "";
                int int0 = 0;
                boolean0 = true;

                for (int int1 = 0; int1 < commandArgsx.required().length; int1++) {
                    String string = commandArgsx.required()[int1];
                    if (int0 == this.commandArgs.length) {
                        boolean0 = false;
                        break;
                    }

                    Matcher matcher0 = Pattern.compile(string).matcher(this.commandArgs[int0]);
                    if (!matcher0.matches()) {
                        boolean0 = false;
                        break;
                    }

                    for (int int2 = 0; int2 < matcher0.groupCount(); int2++) {
                        arrayList.add(matcher0.group(int2 + 1));
                    }

                    int0++;
                }

                if (boolean0) {
                    if (int0 == this.commandArgs.length) {
                        this.argsName = commandArgsx.argName();
                        break;
                    }

                    if (!commandArgsx.optional().equals("no value")) {
                        Matcher matcher1 = Pattern.compile(commandArgsx.optional()).matcher(this.commandArgs[int0]);
                        if (matcher1.matches()) {
                            for (int int3 = 0; int3 < matcher1.groupCount(); int3++) {
                                arrayList.add(matcher1.group(int3 + 1));
                            }
                        } else {
                            boolean0 = false;
                        }
                    } else if (int0 < this.commandArgs.length) {
                        boolean0 = false;
                    }

                    if (boolean0) {
                        this.argsName = commandArgsx.argName();
                        break;
                    }
                }
            }

            if (boolean0) {
                this.commandArgs = new String[arrayList.size()];
                this.commandArgs = arrayList.toArray(this.commandArgs);
            } else {
                this.message = this.invalidCommand();
                this.commandArgs = new String[0];
            }

            this.parsed = true;
            return boolean0;
        }
    }

    protected int getAccessLevel() {
        return this.playerType;
    }

    protected String getExecutorUsername() {
        return this.username;
    }

    protected String getCommand() {
        return this.command;
    }

    protected static <T> T getAnnotation(Class<T> clazz1, Class clazz0) {
        return clazz0.getAnnotation(clazz1);
    }

    public boolean isParsingSuccessful() {
        if (!this.parsed) {
            this.parsingSuccessful = this.parseCommand();
        }

        return this.parsingSuccessful;
    }

    private boolean PlayerSatisfyRequiredRights() {
        RequiredRight requiredRight = this.getRequiredRights();
        return (this.playerType & requiredRight.requiredRights()) != 0;
    }

    private String invalidCommand() {
        return this.hasHelp() ? this.getHelp() : Translator.getText("UI_command_arg_parse_failed", this.command);
    }

    private String playerHasNoRightError() {
        return Translator.getText("UI_has_no_right_to_execute_command", this.username, this.command);
    }
}
