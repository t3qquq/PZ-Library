// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.raknet.UdpConnection;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatType;

public final class ChatUtility {
    private static final boolean useEuclidean = true;
    private static final HashMap<String, String> allowedChatIcons = new HashMap<>();
    private static final HashMap<String, String> allowedChatIconsFull = new HashMap<>();
    private static final StringBuilder builder = new StringBuilder();
    private static final StringBuilder builderTest = new StringBuilder();

    private ChatUtility() {
    }

    public static float getScrambleValue(IsoObject object, IsoPlayer player, float float0) {
        return getScrambleValue(object.getX(), object.getY(), object.getZ(), object.getSquare(), player, float0);
    }

    public static float getScrambleValue(float float5, float float6, float float1, IsoGridSquare square, IsoPlayer player, float float3) {
        float float0 = 1.0F;
        boolean boolean0 = false;
        boolean boolean1 = false;
        if (square != null && player.getSquare() != null) {
            if (player.getBuilding() != null && square.getBuilding() != null && player.getBuilding() == square.getBuilding()) {
                if (player.getSquare().getRoom() == square.getRoom()) {
                    float0 = (float)(float0 * 2.0);
                    boolean1 = true;
                } else if (Math.abs(player.getZ() - float1) < 1.0F) {
                    float0 = (float)(float0 * 2.0);
                }
            } else if (player.getBuilding() != null || square.getBuilding() != null) {
                float0 = (float)(float0 * 0.5);
                boolean0 = true;
            }

            if (Math.abs(player.getZ() - float1) >= 1.0F) {
                float0 = (float)(float0 - float0 * (Math.abs(player.getZ() - float1) * 0.25));
                boolean0 = true;
            }
        }

        float float2 = float3 * float0;
        float float4 = 1.0F;
        if (float0 > 0.0F && playerWithinBounds(float5, float6, player, float2)) {
            float float7 = getDistance(float5, float6, player);
            if (float7 >= 0.0F && float7 < float2) {
                float float8 = float2 * 0.6F;
                if (!boolean1 && (boolean0 || !(float7 < float8))) {
                    if (float2 - float8 != 0.0F) {
                        float4 = (float7 - float8) / (float2 - float8);
                        if (float4 < 0.2F) {
                            float4 = 0.2F;
                        }
                    }
                } else {
                    float4 = 0.0F;
                }
            }
        }

        return float4;
    }

    public static boolean playerWithinBounds(IsoObject object1, IsoObject object0, float float0) {
        return playerWithinBounds(object1.getX(), object1.getY(), object0, float0);
    }

    public static boolean playerWithinBounds(float float2, float float0, IsoObject object, float float1) {
        return object == null
            ? false
            : object.getX() > float2 - float1 && object.getX() < float2 + float1 && object.getY() > float0 - float1 && object.getY() < float0 + float1;
    }

    public static float getDistance(IsoObject object, IsoPlayer player) {
        return player == null ? -1.0F : (float)Math.sqrt(Math.pow(object.getX() - player.x, 2.0) + Math.pow(object.getY() - player.y, 2.0));
    }

    public static float getDistance(float float1, float float0, IsoPlayer player) {
        return player == null ? -1.0F : (float)Math.sqrt(Math.pow(float1 - player.x, 2.0) + Math.pow(float0 - player.y, 2.0));
    }

    public static UdpConnection findConnection(short short0) {
        UdpConnection udpConnection0 = null;
        if (GameServer.udpEngine != null) {
            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection1 = GameServer.udpEngine.connections.get(int0);

                for (int int1 = 0; int1 < udpConnection1.playerIDs.length; int1++) {
                    if (udpConnection1.playerIDs[int1] == short0) {
                        udpConnection0 = udpConnection1;
                        break;
                    }
                }
            }
        }

        if (udpConnection0 == null) {
            DebugLog.log("Connection with PlayerID ='" + short0 + "' not found!");
        }

        return udpConnection0;
    }

    public static UdpConnection findConnection(String string) {
        UdpConnection udpConnection0 = null;
        if (GameServer.udpEngine != null) {
            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size() && udpConnection0 == null; int0++) {
                UdpConnection udpConnection1 = GameServer.udpEngine.connections.get(int0);

                for (int int1 = 0; int1 < udpConnection1.players.length; int1++) {
                    if (udpConnection1.players[int1] != null && udpConnection1.players[int1].username.equalsIgnoreCase(string)) {
                        udpConnection0 = udpConnection1;
                        break;
                    }
                }
            }
        }

        if (udpConnection0 == null) {
            DebugLog.log("Player with nickname = '" + string + "' not found!");
        }

        return udpConnection0;
    }

    public static IsoPlayer findPlayer(int int2) {
        IsoPlayer player = null;
        if (GameServer.udpEngine != null) {
            for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);

                for (int int1 = 0; int1 < udpConnection.playerIDs.length; int1++) {
                    if (udpConnection.playerIDs[int1] == int2) {
                        player = udpConnection.players[int1];
                        break;
                    }
                }
            }
        }

        if (player == null) {
            DebugLog.log("Player with PlayerID ='" + int2 + "' not found!");
        }

        return player;
    }

    public static String findPlayerName(int int0) {
        return findPlayer(int0).getUsername();
    }

    public static IsoPlayer findPlayer(String string) {
        IsoPlayer player = null;
        if (GameClient.bClient) {
            player = GameClient.instance.getPlayerFromUsername(string);
        } else if (GameServer.bServer) {
            player = GameServer.getPlayerByUserName(string);
        }

        if (player == null) {
            DebugLog.log("Player with nickname = '" + string + "' not found!");
        }

        return player;
    }

    public static ArrayList<ChatType> getAllowedChatStreams() {
        String string0 = ServerOptions.getInstance().ChatStreams.getValue();
        string0 = string0.replaceAll("\"", "");
        String[] strings = string0.split(",");
        ArrayList arrayList = new ArrayList();
        arrayList.add(ChatType.server);

        for (String string1 : strings) {
            switch (string1) {
                case "s":
                    arrayList.add(ChatType.say);
                    break;
                case "r":
                    arrayList.add(ChatType.radio);
                    break;
                case "a":
                    arrayList.add(ChatType.admin);
                    break;
                case "w":
                    arrayList.add(ChatType.whisper);
                    break;
                case "y":
                    arrayList.add(ChatType.shout);
                    break;
                case "sh":
                    arrayList.add(ChatType.safehouse);
                    break;
                case "f":
                    arrayList.add(ChatType.faction);
                    break;
                case "all":
                    arrayList.add(ChatType.general);
            }
        }

        return arrayList;
    }

    public static boolean chatStreamEnabled(ChatType chatType) {
        ArrayList arrayList = getAllowedChatStreams();
        return arrayList.contains(chatType);
    }

    public static void InitAllowedChatIcons() {
        allowedChatIcons.clear();
        Texture.collectAllIcons(allowedChatIcons, allowedChatIconsFull);
    }

    private static String getColorString(String string, boolean boolean0) {
        if (Colors.ColorExists(string)) {
            Color color = Colors.GetColorByName(string);
            return boolean0
                ? color.getRedFloat() + "," + color.getGreenFloat() + "," + color.getBlueFloat()
                : color.getRed() + "," + color.getGreen() + "," + color.getBlue();
        } else {
            if (string.length() <= 11 && string.contains(",")) {
                String[] strings = string.split(",");
                if (strings.length == 3) {
                    int int0 = parseColorInt(strings[0]);
                    int int1 = parseColorInt(strings[1]);
                    int int2 = parseColorInt(strings[2]);
                    if (int0 != -1 && int1 != -1 && int2 != -1) {
                        if (boolean0) {
                            return int0 / 255.0F + "," + int1 / 255.0F + "," + int2 / 255.0F;
                        }

                        return int0 + "," + int1 + "," + int2;
                    }
                }
            }

            return null;
        }
    }

    private static int parseColorInt(String string) {
        try {
            int int0 = Integer.parseInt(string);
            return int0 >= 0 && int0 <= 255 ? int0 : -1;
        } catch (Exception exception) {
            return -1;
        }
    }

    public static String parseStringForChatBubble(String string0) {
        try {
            builder.delete(0, builder.length());
            builderTest.delete(0, builderTest.length());
            string0 = string0.replaceAll("\\[br/]", "");
            string0 = string0.replaceAll("\\[cdt=", "");
            char[] chars = string0.toCharArray();
            boolean boolean0 = false;
            boolean boolean1 = false;
            int int0 = 0;

            for (int int1 = 0; int1 < chars.length; int1++) {
                char char0 = chars[int1];
                if (char0 == '*') {
                    if (!boolean0) {
                        boolean0 = true;
                    } else {
                        String string1 = builderTest.toString();
                        builderTest.delete(0, builderTest.length());
                        String string2 = getColorString(string1, false);
                        if (string2 != null) {
                            if (boolean1) {
                                builder.append("[/]");
                            }

                            builder.append("[col=");
                            builder.append(string2);
                            builder.append(']');
                            boolean0 = false;
                            boolean1 = true;
                        } else if (int0 < 10 && (string1.equalsIgnoreCase("music") || allowedChatIcons.containsKey(string1.toLowerCase()))) {
                            if (boolean1) {
                                builder.append("[/]");
                                boolean1 = false;
                            }

                            builder.append("[img=");
                            builder.append(string1.equalsIgnoreCase("music") ? "music" : allowedChatIcons.get(string1.toLowerCase()));
                            builder.append(']');
                            boolean0 = false;
                            int0++;
                        } else {
                            builder.append('*');
                            builder.append(string1);
                        }
                    }
                } else if (boolean0) {
                    builderTest.append(char0);
                } else {
                    builder.append(char0);
                }
            }

            if (boolean0) {
                builder.append('*');
                String string3 = builderTest.toString();
                if (string3.length() > 0) {
                    builder.append(string3);
                }

                if (boolean1) {
                    builder.append("[/]");
                }
            }

            return builder.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return string0;
        }
    }

    public static String parseStringForChatLog(String string0) {
        try {
            builder.delete(0, builder.length());
            builderTest.delete(0, builderTest.length());
            char[] chars = string0.toCharArray();
            boolean boolean0 = false;
            boolean boolean1 = false;
            int int0 = 0;

            for (int int1 = 0; int1 < chars.length; int1++) {
                char char0 = chars[int1];
                if (char0 == '*') {
                    if (!boolean0) {
                        boolean0 = true;
                    } else {
                        String string1 = builderTest.toString();
                        builderTest.delete(0, builderTest.length());
                        String string2 = getColorString(string1, true);
                        if (string2 != null) {
                            builder.append(" <RGB:");
                            builder.append(string2);
                            builder.append('>');
                            boolean0 = false;
                            boolean1 = true;
                        } else {
                            if (int0 < 10 && (string1.equalsIgnoreCase("music") || allowedChatIconsFull.containsKey(string1.toLowerCase()))) {
                                if (boolean1) {
                                    builder.append(" <RGB:");
                                    builder.append("1.0,1.0,1.0");
                                    builder.append('>');
                                    boolean1 = false;
                                }

                                String string3 = string1.equalsIgnoreCase("music") ? "Icon_music_notes" : allowedChatIconsFull.get(string1.toLowerCase());
                                Texture texture = Texture.getSharedTexture(string3);
                                if (Texture.getSharedTexture(string3) != null) {
                                    int int2 = (int)(texture.getWidth() * 0.5F);
                                    int int3 = (int)(texture.getHeight() * 0.5F);
                                    if (string1.equalsIgnoreCase("music")) {
                                        int2 = (int)(texture.getWidth() * 0.75F);
                                        int3 = (int)(texture.getHeight() * 0.75F);
                                    }

                                    builder.append("<IMAGE:");
                                    builder.append(string3);
                                    builder.append("," + int2 + "," + int3 + ">");
                                    boolean0 = false;
                                    int0++;
                                    continue;
                                }
                            }

                            builder.append('*');
                            builder.append(string1);
                        }
                    }
                } else if (boolean0) {
                    builderTest.append(char0);
                } else {
                    builder.append(char0);
                }
            }

            if (boolean0) {
                builder.append('*');
                String string4 = builderTest.toString();
                if (string4.length() > 0) {
                    builder.append(string4);
                }
            }

            return builder.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return string0;
        }
    }
}
