// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import com.google.common.util.concurrent.FutureCallback;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public class DiscordBot {
    private DiscordAPI api;
    private Collection<Channel> channels;
    private Channel current;
    private String currentChannelName;
    private String currentChannelID;
    private String name;
    private DiscordSender sender;

    public DiscordBot(String string, DiscordSender discordSender) {
        this.name = string;
        this.sender = discordSender;
        this.current = null;
    }

    public void connect(boolean boolean0, String string0, String string1, String string2) {
        if (string0 == null || string0.isEmpty()) {
            DebugLog.log(DebugType.Network, "DISCORD: token not configured");
            boolean0 = false;
        }

        if (!boolean0) {
            DebugLog.log(DebugType.Network, "*** DISCORD DISABLED ****");
            this.current = null;
        } else {
            this.api = Javacord.getApi(string0, true);
            this.api.connect(new DiscordBot.Connector());
            DebugLog.log(DebugType.Network, "*** DISCORD ENABLED ****");
            this.currentChannelName = string1;
            this.currentChannelID = string2;
        }
    }

    private void setChannel(String string0, String string1) {
        Collection collection = this.getChannelNames();
        if ((string0 == null || string0.isEmpty()) && !collection.isEmpty()) {
            string0 = (String)collection.iterator().next();
            DebugLog.log(DebugType.Network, "DISCORD: set default channel name = \"" + string0 + "\"");
        }

        if (string1 != null && !string1.isEmpty()) {
            this.setChannelByID(string1);
        } else {
            if (string0 != null) {
                this.setChannelByName(string0);
            }
        }
    }

    public void sendMessage(String string1, String string0) {
        if (this.current != null) {
            this.current.sendMessage(string1 + ": " + string0);
            DebugLog.log(DebugType.Network, "DISCORD: User '" + string1 + "' send message: '" + string0 + "'");
        }
    }

    private Collection<String> getChannelNames() {
        ArrayList arrayList = new ArrayList();
        this.channels = this.api.getChannels();

        for (Channel channel : this.channels) {
            arrayList.add(channel.getName());
        }

        return arrayList;
    }

    private void setChannelByName(String string) {
        this.current = null;

        for (Channel channel : this.channels) {
            if (channel.getName().equals(string)) {
                if (this.current != null) {
                    DebugLog.log(DebugType.Network, "Discord server has few channels with name '" + string + "'. Please, use channel ID instead");
                    this.current = null;
                    return;
                }

                this.current = channel;
            }
        }

        if (this.current == null) {
            DebugLog.log(DebugType.Network, "DISCORD: channel \"" + string + "\" is not found. Try to use channel ID instead");
        } else {
            DebugLog.log(DebugType.Network, "Discord enabled on channel: " + string);
        }
    }

    private void setChannelByID(String string) {
        this.current = null;

        for (Channel channel : this.channels) {
            if (channel.getId().equals(string)) {
                DebugLog.log(DebugType.Network, "Discord enabled on channel with ID: " + string);
                this.current = channel;
                break;
            }
        }

        if (this.current == null) {
            DebugLog.log(DebugType.Network, "DISCORD: channel with ID \"" + string + "\" not found");
        }
    }

    class Connector implements FutureCallback<DiscordAPI> {
        public void onSuccess(DiscordAPI discordAPI) {
            DebugLog.log(DebugType.Network, "*** DISCORD API CONNECTED ****");
            DiscordBot.this.setChannel(DiscordBot.this.currentChannelName, DiscordBot.this.currentChannelID);
            discordAPI.registerListener(DiscordBot.this.new Listener());
            discordAPI.updateUsername(DiscordBot.this.name);
            if (DiscordBot.this.current != null) {
                DebugLog.log(DebugType.Network, "*** DISCORD INITIALIZATION SUCCEEDED ****");
            } else {
                DebugLog.log(DebugType.Network, "*** DISCORD INITIALIZATION FAILED ****");
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    class Listener implements MessageCreateListener {
        @Override
        public void onMessageCreate(DiscordAPI discordAPI, Message message) {
            if (DiscordBot.this.current != null) {
                if (!discordAPI.getYourself().getId().equals(message.getAuthor().getId())) {
                    if (message.getChannelReceiver().getId().equals(DiscordBot.this.current.getId())) {
                        DebugLog.log(DebugType.Network, "DISCORD: get message on current channel");
                        DebugLog.log(DebugType.Network, "DISCORD: send message = \"" + message.getContent() + "\" for " + message.getAuthor().getName() + ")");
                        String string = this.replaceChannelIDByItsName(discordAPI, message);
                        string = this.removeSmilesAndImages(string);
                        if (!string.isEmpty() && !string.matches("^\\s$")) {
                            DiscordBot.this.sender.sendMessageFromDiscord(message.getAuthor().getName(), string);
                        }
                    }
                }
            }
        }

        private String replaceChannelIDByItsName(DiscordAPI discordAPI, Message message) {
            String string = message.getContent();
            Pattern pattern = Pattern.compile("<#(\\d+)>");
            Matcher matcher = pattern.matcher(message.getContent());
            if (matcher.find()) {
                for (int int0 = 1; int0 <= matcher.groupCount(); int0++) {
                    Channel channel = discordAPI.getChannelById(matcher.group(int0));
                    if (channel != null) {
                        string = string.replaceAll("<#" + matcher.group(int0) + ">", "#" + channel.getName());
                    }
                }
            }

            return string;
        }

        private String removeSmilesAndImages(String string) {
            StringBuilder stringBuilder = new StringBuilder();
            char[] chars = string.toCharArray();
            int int0 = chars.length;

            for (int int1 = 0; int1 < int0; int1++) {
                Character character = chars[int1];
                if (!Character.isLowSurrogate(character) && !Character.isHighSurrogate(character)) {
                    stringBuilder.append(character);
                }
            }

            return stringBuilder.toString();
        }
    }
}
