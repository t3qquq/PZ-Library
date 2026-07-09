// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import java.util.ArrayList;
import java.util.HashSet;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.characters.Talker;
import zombie.iso.objects.IsoRadio;
import zombie.iso.objects.IsoTelevision;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;
import zombie.ui.TextDrawObject;
import zombie.ui.UIFont;
import zombie.vehicles.VehiclePart;

/**
 * Turbo  shared display of chat lines functionallity for iso objects & players (characters)
 */
public class ChatElement implements Talker {
    protected ChatElement.PlayerLines[] playerLines = new ChatElement.PlayerLines[4];
    protected ChatElementOwner owner;
    protected float historyVal = 1.0F;
    protected boolean historyInRange = false;
    protected float historyRange = 15.0F;
    protected boolean useEuclidean = true;
    protected boolean hasChatToDisplay = false;
    protected int maxChatLines = -1;
    protected int maxCharsPerLine = -1;
    protected String sayLine = null;
    protected String sayLineTag = null;
    protected TextDrawObject sayLineObject = null;
    protected boolean Speaking = false;
    protected String talkerType = "unknown";
    public static boolean doBackDrop = true;
    public static NineGridTexture backdropTexture;
    private int bufferX = 0;
    private int bufferY = 0;
    private static ChatElement.PlayerLinesList[] renderBatch = new ChatElement.PlayerLinesList[4];
    private static HashSet<String> noLogText = new HashSet<>();

    public ChatElement(ChatElementOwner chatowner, int numberoflines, String talkertype) {
        this.owner = chatowner;
        this.setMaxChatLines(numberoflines);
        this.setMaxCharsPerLine(75);
        this.talkerType = talkertype != null ? talkertype : this.talkerType;
        if (backdropTexture == null) {
            backdropTexture = new NineGridTexture("NineGridBlack", 5);
        }
    }

    public void setMaxChatLines(int num) {
        num = num < 1 ? 1 : (num > 10 ? 10 : num);
        if (num != this.maxChatLines) {
            this.maxChatLines = num;

            for (int int0 = 0; int0 < this.playerLines.length; int0++) {
                this.playerLines[int0] = new ChatElement.PlayerLines(this.maxChatLines);
            }
        }
    }

    public int getMaxChatLines() {
        return this.maxChatLines;
    }

    public void setMaxCharsPerLine(int maxChars) {
        for (int int0 = 0; int0 < this.playerLines.length; int0++) {
            this.playerLines[int0].setMaxCharsPerLine(maxChars);
        }

        this.maxCharsPerLine = maxChars;
    }

    @Override
    public boolean IsSpeaking() {
        return this.Speaking;
    }

    @Override
    public String getTalkerType() {
        return this.talkerType;
    }

    public void setTalkerType(String type) {
        this.talkerType = type == null ? "" : type;
    }

    @Override
    public String getSayLine() {
        return this.sayLine;
    }

    public String getSayLineTag() {
        return this.Speaking && this.sayLineTag != null ? this.sayLineTag : "";
    }

    public void setHistoryRange(float range) {
        this.historyRange = range;
    }

    public void setUseEuclidean(boolean b) {
        this.useEuclidean = b;
    }

    public boolean getHasChatToDisplay() {
        return this.hasChatToDisplay;
    }

    protected float getDistance(IsoPlayer player) {
        if (player == null) {
            return -1.0F;
        } else {
            return this.useEuclidean
                ? (float)Math.sqrt(Math.pow(this.owner.getX() - player.x, 2.0) + Math.pow(this.owner.getY() - player.y, 2.0))
                : Math.abs(this.owner.getX() - player.x) + Math.abs(this.owner.getY() - player.y);
        }
    }

    protected boolean playerWithinBounds(IsoPlayer player, float float0) {
        return player == null
            ? false
            : player.getX() > this.owner.getX() - float0
                && player.getX() < this.owner.getX() + float0
                && player.getY() > this.owner.getY() - float0
                && player.getY() < this.owner.getY() + float0;
    }

    public void SayDebug(int n, String text) {
        if (!GameServer.bServer && n >= 0 && n < this.maxChatLines) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null) {
                    ChatElement.PlayerLines playerLinesx = this.playerLines[int0];
                    if (n < playerLinesx.chatLines.length) {
                        if (playerLinesx.chatLines[n].getOriginal() != null && playerLinesx.chatLines[n].getOriginal().equals(text)) {
                            playerLinesx.chatLines[n].setInternalTickClock(playerLinesx.lineDisplayTime);
                        } else {
                            playerLinesx.chatLines[n].setSettings(true, true, true, true, true, true);
                            playerLinesx.chatLines[n].setInternalTickClock(playerLinesx.lineDisplayTime);
                            playerLinesx.chatLines[n].setCustomTag("default");
                            playerLinesx.chatLines[n].setDefaultColors(1.0F, 1.0F, 1.0F, 1.0F);
                            playerLinesx.chatLines[n].ReadString(UIFont.Medium, text, this.maxCharsPerLine);
                        }
                    }
                }
            }

            this.sayLine = text;
            this.sayLineTag = "default";
            this.hasChatToDisplay = true;
        }
    }

    @Override
    public void Say(String line) {
        this.addChatLine(line, 1.0F, 1.0F, 1.0F, UIFont.Dialogue, 25.0F, "default", false, false, false, false, false, true);
    }

    public void addChatLine(String msg, float r, float g, float b, float baseRange) {
        this.addChatLine(msg, r, g, b, UIFont.Dialogue, baseRange, "default", false, false, false, false, false, true);
    }

    public void addChatLine(String msg, float r, float g, float b) {
        this.addChatLine(msg, r, g, b, UIFont.Dialogue, 25.0F, "default", false, false, false, false, false, true);
    }

    public void addChatLine(
        String msg,
        float r,
        float g,
        float b,
        UIFont font,
        float baseRange,
        String customTag,
        boolean bbcode,
        boolean img,
        boolean icons,
        boolean colors,
        boolean fonts,
        boolean equalizeHeights
    ) {
        if (!GameServer.bServer) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null
                    && (
                        !player.Traits.Deaf.isSet()
                            || (
                                this.owner instanceof IsoTelevision
                                    ? ((IsoTelevision)this.owner).isFacing(player)
                                    : !(this.owner instanceof IsoRadio) && !(this.owner instanceof VehiclePart)
                            )
                    )) {
                    float float0 = this.getScrambleValue(player, baseRange);
                    if (float0 < 1.0F) {
                        ChatElement.PlayerLines playerLinesx = this.playerLines[int0];
                        TextDrawObject textDrawObject = playerLinesx.getNewLineObject();
                        if (textDrawObject != null) {
                            textDrawObject.setSettings(bbcode, img, icons, colors, fonts, equalizeHeights);
                            textDrawObject.setInternalTickClock(playerLinesx.lineDisplayTime);
                            textDrawObject.setCustomTag(customTag);
                            String string;
                            if (float0 > 0.0F) {
                                string = ZomboidRadio.getInstance().scrambleString(msg, (int)(100.0F * float0), true, "...");
                                textDrawObject.setDefaultColors(0.5F, 0.5F, 0.5F, 1.0F);
                            } else {
                                string = msg;
                                textDrawObject.setDefaultColors(r, g, b, 1.0F);
                            }

                            textDrawObject.ReadString(font, string, this.maxCharsPerLine);
                            this.sayLine = msg;
                            this.sayLineTag = customTag;
                            this.hasChatToDisplay = true;
                        }
                    }
                }
            }
        }
    }

    protected float getScrambleValue(IsoPlayer player, float float2) {
        if (this.owner == player) {
            return 0.0F;
        } else {
            float float0 = 1.0F;
            boolean boolean0 = false;
            boolean boolean1 = false;
            if (this.owner.getSquare() != null && player.getSquare() != null) {
                if (player.getBuilding() != null
                    && this.owner.getSquare().getBuilding() != null
                    && player.getBuilding() == this.owner.getSquare().getBuilding()) {
                    if (player.getSquare().getRoom() == this.owner.getSquare().getRoom()) {
                        float0 = (float)(float0 * 2.0);
                        boolean1 = true;
                    } else if (Math.abs(player.getZ() - this.owner.getZ()) < 1.0F) {
                        float0 = (float)(float0 * 2.0);
                    }
                } else if (player.getBuilding() != null || this.owner.getSquare().getBuilding() != null) {
                    float0 = (float)(float0 * 0.5);
                    boolean0 = true;
                }

                if (Math.abs(player.getZ() - this.owner.getZ()) >= 1.0F) {
                    float0 = (float)(float0 - float0 * (Math.abs(player.getZ() - this.owner.getZ()) * 0.25));
                    boolean0 = true;
                }
            }

            float float1 = float2 * float0;
            float float3 = 1.0F;
            if (float0 > 0.0F && this.playerWithinBounds(player, float1)) {
                float float4 = this.getDistance(player);
                if (float4 >= 0.0F && float4 < float1) {
                    float float5 = float1 * 0.6F;
                    if (!boolean1 && (boolean0 || !(float4 < float5))) {
                        if (float1 - float5 != 0.0F) {
                            float3 = (float4 - float5) / (float1 - float5);
                            if (float3 < 0.2F) {
                                float3 = 0.2F;
                            }
                        }
                    } else {
                        float3 = 0.0F;
                    }
                }
            }

            return float3;
        }
    }

    protected void updateChatLines() {
        this.Speaking = false;
        boolean boolean0 = false;
        if (this.hasChatToDisplay) {
            this.hasChatToDisplay = false;

            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                float float0 = 1.25F * GameTime.getInstance().getMultiplier();
                int int1 = this.playerLines[int0].lineDisplayTime;

                for (TextDrawObject textDrawObject : this.playerLines[int0].chatLines) {
                    float float1 = textDrawObject.updateInternalTickClock(float0);
                    if (!(float1 <= 0.0F)) {
                        this.hasChatToDisplay = true;
                        if (!boolean0 && !textDrawObject.getCustomTag().equals("radio")) {
                            float float2 = float1 / (int1 / 2.0F);
                            if (float2 >= 1.0F) {
                                this.Speaking = true;
                            }

                            boolean0 = true;
                        }

                        float0 *= 1.2F;
                    }
                }
            }
        }

        if (!this.Speaking) {
            this.sayLine = null;
            this.sayLineTag = null;
        }
    }

    protected void updateHistory() {
        if (this.hasChatToDisplay) {
            this.historyInRange = false;
            IsoPlayer player = IsoPlayer.getInstance();
            if (player != null) {
                if (player == this.owner) {
                    this.historyVal = 1.0F;
                } else {
                    if (this.playerWithinBounds(player, this.historyRange)) {
                        this.historyInRange = true;
                    } else {
                        this.historyInRange = false;
                    }

                    if (this.historyInRange && this.historyVal != 1.0F) {
                        this.historyVal += 0.04F;
                        if (this.historyVal > 1.0F) {
                            this.historyVal = 1.0F;
                        }
                    }

                    if (!this.historyInRange && this.historyVal != 0.0F) {
                        this.historyVal -= 0.04F;
                        if (this.historyVal < 0.0F) {
                            this.historyVal = 0.0F;
                        }
                    }
                }
            }
        } else if (this.historyVal != 0.0F) {
            this.historyVal = 0.0F;
        }
    }

    public void update() {
        if (!GameServer.bServer) {
            this.updateChatLines();
            this.updateHistory();
        }
    }

    public void renderBatched(int playerNum, int x, int y) {
        this.renderBatched(playerNum, x, y, false);
    }

    public void renderBatched(int playerNum, int x, int y, boolean ignoreRadioLines) {
        if (playerNum < this.playerLines.length && this.hasChatToDisplay && !GameServer.bServer) {
            this.playerLines[playerNum].renderX = x;
            this.playerLines[playerNum].renderY = y;
            this.playerLines[playerNum].ignoreRadioLines = ignoreRadioLines;
            if (renderBatch[playerNum] == null) {
                renderBatch[playerNum] = new ChatElement.PlayerLinesList();
            }

            renderBatch[playerNum].add(this.playerLines[playerNum]);
        }
    }

    public void clear(int playerIndex) {
        this.playerLines[playerIndex].clear();
    }

    public static void RenderBatch(int playerNum) {
        if (renderBatch[playerNum] != null && renderBatch[playerNum].size() > 0) {
            for (int int0 = 0; int0 < renderBatch[playerNum].size(); int0++) {
                ChatElement.PlayerLines playerLinesx = renderBatch[playerNum].get(int0);
                playerLinesx.render();
            }

            renderBatch[playerNum].clear();
        }
    }

    public static void NoRender(int playerNum) {
        if (renderBatch[playerNum] != null) {
            renderBatch[playerNum].clear();
        }
    }

    public static void addNoLogText(String text) {
        if (text != null && !text.isEmpty()) {
            noLogText.add(text);
        }
    }

    class PlayerLines {
        protected int lineDisplayTime = 314;
        protected int renderX = 0;
        protected int renderY = 0;
        protected boolean ignoreRadioLines = false;
        protected TextDrawObject[] chatLines;

        public PlayerLines(int arg1) {
            this.chatLines = new TextDrawObject[arg1];

            for (int int0 = 0; int0 < this.chatLines.length; int0++) {
                this.chatLines[int0] = new TextDrawObject(0, 0, 0, true, true, true, true, true, true);
                this.chatLines[int0].setDefaultFont(UIFont.Medium);
            }
        }

        public void setMaxCharsPerLine(int arg0) {
            for (int int0 = 0; int0 < this.chatLines.length; int0++) {
                this.chatLines[int0].setMaxCharsPerLine(arg0);
            }
        }

        public TextDrawObject getNewLineObject() {
            if (this.chatLines != null && this.chatLines.length > 0) {
                TextDrawObject textDrawObject = this.chatLines[this.chatLines.length - 1];
                textDrawObject.Clear();

                for (int int0 = this.chatLines.length - 1; int0 > 0; int0--) {
                    this.chatLines[int0] = this.chatLines[int0 - 1];
                }

                this.chatLines[0] = textDrawObject;
                return this.chatLines[0];
            } else {
                return null;
            }
        }

        public void render() {
            if (!GameServer.bServer) {
                if (ChatElement.this.hasChatToDisplay) {
                    int int0 = 0;

                    for (TextDrawObject textDrawObject : this.chatLines) {
                        if (textDrawObject.getEnabled()) {
                            if (textDrawObject.getWidth() > 0 && textDrawObject.getHeight() > 0) {
                                float float0 = textDrawObject.getInternalClock();
                                if (!(float0 <= 0.0F) && (!textDrawObject.getCustomTag().equals("radio") || !this.ignoreRadioLines)) {
                                    float float1 = float0 / (this.lineDisplayTime / 4.0F);
                                    if (float1 > 1.0F) {
                                        float1 = 1.0F;
                                    }

                                    this.renderY = this.renderY - (textDrawObject.getHeight() + 1);
                                    boolean boolean0 = textDrawObject.getDefaultFontEnum() != UIFont.Dialogue;
                                    if (ChatElement.doBackDrop && ChatElement.backdropTexture != null) {
                                        ChatElement.backdropTexture
                                            .renderInnerBased(
                                                this.renderX - textDrawObject.getWidth() / 2,
                                                this.renderY,
                                                textDrawObject.getWidth(),
                                                textDrawObject.getHeight(),
                                                0.0F,
                                                0.0F,
                                                0.0F,
                                                0.4F
                                            );
                                    }

                                    if (int0 == 0) {
                                        textDrawObject.Draw(this.renderX, this.renderY, boolean0, float1);
                                    } else if (ChatElement.this.historyVal > 0.0F) {
                                        textDrawObject.Draw(this.renderX, this.renderY, boolean0, float1 * ChatElement.this.historyVal);
                                    }

                                    int0++;
                                }
                            } else {
                                int0++;
                            }
                        }
                    }
                }
            }
        }

        void clear() {
            if (ChatElement.this.hasChatToDisplay) {
                ChatElement.this.hasChatToDisplay = false;

                for (int int0 = 0; int0 < this.chatLines.length; int0++) {
                    if (!(this.chatLines[int0].getInternalClock() <= 0.0F)) {
                        this.chatLines[int0].Clear();
                        this.chatLines[int0].updateInternalTickClock(this.chatLines[int0].getInternalClock());
                    }
                }

                ChatElement.this.historyInRange = false;
                ChatElement.this.historyVal = 0.0F;
            }
        }
    }

    class PlayerLinesList extends ArrayList<ChatElement.PlayerLines> {
    }
}
