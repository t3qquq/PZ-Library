// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.AlarmClock;
import zombie.inventory.types.AlarmClockClothing;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;

public final class Clock extends UIElement {
    boolean bLargeTextures = false;
    Texture background = null;
    Texture[] digitsLarge;
    Texture[] digitsSmall;
    Texture colon = null;
    Texture slash = null;
    Texture minus = null;
    Texture dot = null;
    Texture tempC = null;
    Texture tempF = null;
    Texture tempE = null;
    Texture texAM = null;
    Texture texPM = null;
    Texture alarmOn = null;
    Texture alarmRinging = null;
    Color displayColour = new Color(100, 200, 210, 255);
    Color ghostColour = new Color(40, 40, 40, 128);
    int uxOriginal;
    int uyOriginal;
    int largeDigitSpacing;
    int smallDigitSpacing;
    int colonSpacing;
    int ampmSpacing;
    int alarmBellSpacing;
    int decimalSpacing;
    int degreeSpacing;
    int slashSpacing;
    int tempDateSpacing;
    int dateOffset;
    int minusOffset;
    int amVerticalSpacing;
    int pmVerticalSpacing;
    int alarmBellVerticalSpacing;
    int displayVerticalSpacing;
    int decimalVerticalSpacing;
    public boolean digital = false;
    public boolean isAlarmSet = false;
    public boolean isAlarmRinging = false;
    private IsoPlayer clockPlayer = null;
    public static Clock instance = null;

    public Clock(int x, int y) {
        this.x = x;
        this.y = y;
        instance = this;
    }

    @Override
    public void render() {
        if (this.visible) {
            this.assignTextures(Core.getInstance().getOptionClockSize() == 2);
            this.DrawTexture(this.background, 0.0, 0.0, 0.75);
            this.renderDisplay(true, this.ghostColour);
            this.renderDisplay(false, this.displayColour);
            super.render();
        }
    }

    private void renderDisplay(boolean boolean0, Color color) {
        int int0 = this.uxOriginal;
        int int1 = this.uyOriginal;

        for (int int2 = 0; int2 < 4; int2++) {
            int[] ints0 = this.timeDigits();
            if (boolean0) {
                this.DrawTextureCol(this.digitsLarge[8], int0, int1, color);
            } else {
                this.DrawTextureCol(this.digitsLarge[ints0[int2]], int0, int1, color);
            }

            int0 += this.digitsLarge[0].getWidth();
            if (int2 == 1) {
                int0 += this.colonSpacing;
                this.DrawTextureCol(this.colon, int0, int1, color);
                int0 += this.colon.getWidth() + this.colonSpacing;
            } else if (int2 < 3) {
                int0 += this.largeDigitSpacing;
            }
        }

        int0 += this.ampmSpacing;
        if (!Core.getInstance().getOptionClock24Hour() || boolean0) {
            if (boolean0) {
                this.DrawTextureCol(this.texAM, int0, int1 + this.amVerticalSpacing, color);
                this.DrawTextureCol(this.texPM, int0, int1 + this.pmVerticalSpacing, color);
            } else if (GameTime.getInstance().getTimeOfDay() < 12.0F) {
                this.DrawTextureCol(this.texAM, int0, int1 + this.amVerticalSpacing, color);
            } else {
                this.DrawTextureCol(this.texPM, int0, int1 + this.pmVerticalSpacing, color);
            }
        }

        if (this.isAlarmRinging || boolean0) {
            this.DrawTextureCol(this.alarmRinging, int0 + this.texAM.getWidth() + this.alarmBellSpacing, int1 + this.alarmBellVerticalSpacing, color);
        } else if (this.isAlarmSet) {
            this.DrawTextureCol(this.alarmOn, int0 + this.texAM.getWidth() + this.alarmBellSpacing, int1 + this.alarmBellVerticalSpacing, color);
        }

        if (this.digital || boolean0) {
            int0 = this.uxOriginal;
            int1 += this.digitsLarge[0].getHeight() + this.displayVerticalSpacing;
            if (this.clockPlayer == null) {
                int0 += this.dateOffset;
            } else {
                int[] ints1 = this.tempDigits();
                if (ints1[0] == 1 || boolean0) {
                    this.DrawTextureCol(this.minus, int0, int1, color);
                }

                int0 += this.minusOffset;
                if (ints1[1] == 1 || boolean0) {
                    this.DrawTextureCol(this.digitsSmall[1], int0, int1, color);
                }

                int0 += this.digitsSmall[0].getWidth() + this.smallDigitSpacing;

                for (int int3 = 2; int3 < 5; int3++) {
                    if (boolean0) {
                        this.DrawTextureCol(this.digitsSmall[8], int0, int1, color);
                    } else {
                        this.DrawTextureCol(this.digitsSmall[ints1[int3]], int0, int1, color);
                    }

                    int0 += this.digitsSmall[0].getWidth();
                    if (int3 == 3) {
                        int0 += this.decimalSpacing;
                        this.DrawTextureCol(this.dot, int0, int1 + this.decimalVerticalSpacing, color);
                        int0 += this.dot.getWidth() + this.decimalSpacing;
                    } else if (int3 < 4) {
                        int0 += this.smallDigitSpacing;
                    }
                }

                int0 += this.degreeSpacing;
                this.DrawTextureCol(this.dot, int0, int1, color);
                int0 += this.dot.getWidth() + this.degreeSpacing;
                if (boolean0) {
                    this.DrawTextureCol(this.tempE, int0, int1, color);
                } else if (ints1[5] == 0) {
                    this.DrawTextureCol(this.tempC, int0, int1, color);
                } else {
                    this.DrawTextureCol(this.tempF, int0, int1, color);
                }

                int0 += this.digitsSmall[0].getWidth() + this.tempDateSpacing;
            }

            int[] ints2 = this.dateDigits();

            for (int int4 = 0; int4 < 4; int4++) {
                if (boolean0) {
                    this.DrawTextureCol(this.digitsSmall[8], int0, int1, color);
                } else {
                    this.DrawTextureCol(this.digitsSmall[ints2[int4]], int0, int1, color);
                }

                int0 += this.digitsSmall[0].getWidth();
                if (int4 == 1) {
                    int0 += this.slashSpacing;
                    this.DrawTextureCol(this.slash, int0, int1, color);
                    int0 += this.slash.getWidth() + this.slashSpacing;
                } else if (int4 < 3) {
                    int0 += this.smallDigitSpacing;
                }
            }
        }
    }

    private void assignTextures(boolean boolean0) {
        if (this.digitsLarge == null || this.bLargeTextures != boolean0) {
            this.bLargeTextures = boolean0;
            if (boolean0) {
                this.background = Texture.getSharedTexture("media/ui/ClockAssets/ClockLargeBackground.png");
            } else {
                this.background = Texture.getSharedTexture("media/ui/ClockAssets/ClockSmallBackground.png");
            }

            String string0 = "Medium";
            String string1 = "Small";
            if (boolean0) {
                string0 = "Large";
                string1 = "Medium";
                this.assignLargeOffsets();
            } else {
                this.assignSmallOffsets();
            }

            if (this.digitsLarge == null) {
                this.digitsLarge = new Texture[10];
                this.digitsSmall = new Texture[10];
            }

            for (int int0 = 0; int0 < 10; int0++) {
                this.digitsLarge[int0] = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string0 + int0 + ".png");
                this.digitsSmall[int0] = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string1 + int0 + ".png");
            }

            this.colon = Texture.getSharedTexture("media/ui/ClockAssets/ClockDivide" + string0 + ".png");
            this.slash = Texture.getSharedTexture("media/ui/ClockAssets/DateDivide" + string1 + ".png");
            this.minus = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string1 + "Minus.png");
            this.dot = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string1 + "Dot.png");
            this.tempC = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string1 + "C.png");
            this.tempF = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string1 + "F.png");
            this.tempE = Texture.getSharedTexture("media/ui/ClockAssets/ClockDigits" + string1 + "E.png");
            this.texAM = Texture.getSharedTexture("media/ui/ClockAssets/ClockAm" + string0 + ".png");
            this.texPM = Texture.getSharedTexture("media/ui/ClockAssets/ClockPm" + string0 + ".png");
            this.alarmOn = Texture.getSharedTexture("media/ui/ClockAssets/ClockAlarm" + string0 + "Set.png");
            this.alarmRinging = Texture.getSharedTexture("media/ui/ClockAssets/ClockAlarm" + string0 + "Sound.png");
        }
    }

    private void assignSmallOffsets() {
        this.uxOriginal = 3;
        this.uyOriginal = 3;
        this.largeDigitSpacing = 1;
        this.smallDigitSpacing = 1;
        this.colonSpacing = 1;
        this.ampmSpacing = 1;
        this.alarmBellSpacing = 1;
        this.decimalSpacing = 1;
        this.degreeSpacing = 1;
        this.slashSpacing = 1;
        this.tempDateSpacing = 5;
        this.dateOffset = 33;
        this.minusOffset = 0;
        this.amVerticalSpacing = 7;
        this.pmVerticalSpacing = 12;
        this.alarmBellVerticalSpacing = 1;
        this.displayVerticalSpacing = 2;
        this.decimalVerticalSpacing = 6;
    }

    private void assignLargeOffsets() {
        this.uxOriginal = 3;
        this.uyOriginal = 3;
        this.largeDigitSpacing = 2;
        this.smallDigitSpacing = 1;
        this.colonSpacing = 3;
        this.ampmSpacing = 3;
        this.alarmBellSpacing = 5;
        this.decimalSpacing = 2;
        this.degreeSpacing = 2;
        this.slashSpacing = 2;
        this.tempDateSpacing = 8;
        this.dateOffset = 65;
        this.minusOffset = -2;
        this.amVerticalSpacing = 15;
        this.pmVerticalSpacing = 25;
        this.alarmBellVerticalSpacing = 1;
        this.displayVerticalSpacing = 5;
        this.decimalVerticalSpacing = 15;
    }

    private int[] timeDigits() {
        float float0 = GameTime.getInstance().getTimeOfDay();
        if (GameClient.bClient && GameClient.bFastForward) {
            float0 = GameTime.getInstance().ServerTimeOfDay;
        }

        if (!Core.getInstance().getOptionClock24Hour()) {
            if (float0 >= 13.0F) {
                float0 -= 12.0F;
            }

            if (float0 < 1.0F) {
                float0 += 12.0F;
            }
        }

        int int0 = (int)float0;
        float float1 = (float0 - (int)float0) * 60.0F;
        int int1 = int0 / 10;
        int int2 = int0 % 10;
        int int3 = (int)(float1 / 10.0F);
        return new int[]{int1, int2, int3, 0};
    }

    private int[] dateDigits() {
        int int0 = 0;
        int int1 = 0;
        int int2 = 0;
        int int3 = 0;
        int0 = (GameTime.getInstance().getDay() + 1) / 10;
        int1 = (GameTime.getInstance().getDay() + 1) % 10;
        int2 = (GameTime.getInstance().getMonth() + 1) / 10;
        int3 = (GameTime.getInstance().getMonth() + 1) % 10;
        return Core.getInstance().getOptionClockFormat() == 1 ? new int[]{int2, int3, int0, int1} : new int[]{int0, int1, int2, int3};
    }

    private int[] tempDigits() {
        float float0 = ClimateManager.getInstance().getAirTemperatureForCharacter(this.clockPlayer, false);
        byte byte0 = 0;
        byte byte1 = 0;
        if (!Core.OptionTemperatureDisplayCelsius) {
            float0 = float0 * 1.8F + 32.0F;
            byte1 = 1;
        }

        if (float0 < 0.0F) {
            byte0 = 1;
            float0 *= -1.0F;
        }

        int int0 = (int)float0 / 100;
        int int1 = (int)(float0 % 100.0F) / 10;
        int int2 = (int)float0 % 10;
        int int3 = (int)(float0 * 10.0F) % 10;
        return new int[]{byte0, int0, int1, int2, int3, byte1};
    }

    public void resize() {
        this.visible = false;
        this.digital = false;
        this.clockPlayer = null;
        this.isAlarmSet = false;
        this.isAlarmRinging = false;
        if (IsoPlayer.getInstance() != null) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && !player.isDead()) {
                    for (int int1 = 0; int1 < player.getWornItems().size(); int1++) {
                        InventoryItem item0 = player.getWornItems().getItemByIndex(int1);
                        if (item0 instanceof AlarmClock || item0 instanceof AlarmClockClothing) {
                            this.visible = UIManager.VisibleAllUI;
                            this.digital = this.digital | item0.hasTag("Digital");
                            if (item0 instanceof AlarmClock) {
                                if (((AlarmClock)item0).isAlarmSet()) {
                                    this.isAlarmSet = true;
                                }

                                if (((AlarmClock)item0).isRinging()) {
                                    this.isAlarmRinging = true;
                                }
                            } else {
                                if (((AlarmClockClothing)item0).isAlarmSet()) {
                                    this.isAlarmSet = true;
                                }

                                if (((AlarmClockClothing)item0).isRinging()) {
                                    this.isAlarmRinging = true;
                                }
                            }

                            this.clockPlayer = player;
                        }
                    }

                    if (this.clockPlayer != null) {
                        break;
                    }

                    ArrayList arrayList = player.getInventory().getItems();

                    for (int int2 = 0; int2 < arrayList.size(); int2++) {
                        InventoryItem item1 = (InventoryItem)arrayList.get(int2);
                        if (item1 instanceof AlarmClock || item1 instanceof AlarmClockClothing) {
                            this.visible = UIManager.VisibleAllUI;
                            this.digital = this.digital | item1.hasTag("Digital");
                            if (item1 instanceof AlarmClock) {
                                if (((AlarmClock)item1).isAlarmSet()) {
                                    this.isAlarmSet = true;
                                }

                                if (((AlarmClock)item1).isRinging()) {
                                    this.isAlarmRinging = true;
                                }
                            } else {
                                if (((AlarmClockClothing)item1).isAlarmSet()) {
                                    this.isAlarmSet = true;
                                }

                                if (((AlarmClockClothing)item1).isRinging()) {
                                    this.isAlarmRinging = true;
                                }
                            }

                            this.clockPlayer = player;
                        }
                    }
                }
            }
        }

        if (DebugOptions.instance.CheatClockVisible.getValue()) {
            this.digital = true;
            this.visible = UIManager.VisibleAllUI;
        }

        if (this.background == null) {
            if (Core.getInstance().getOptionClockSize() == 2) {
                this.background = Texture.getSharedTexture("media/ui/ClockAssets/ClockLargeBackground.png");
            } else {
                this.background = Texture.getSharedTexture("media/ui/ClockAssets/ClockSmallBackground.png");
            }
        }

        this.setHeight(this.background.getHeight());
        this.setWidth(this.background.getWidth());
    }

    public boolean isDateVisible() {
        return this.visible && this.digital;
    }

    @Override
    public Boolean onMouseDown(double x, double y) {
        if (!this.isVisible()) {
            return false;
        } else {
            if (this.isAlarmRinging) {
                if (IsoPlayer.getInstance() != null) {
                    for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                        IsoPlayer player0 = IsoPlayer.players[int0];
                        if (player0 != null && !player0.isDead()) {
                            for (int int1 = 0; int1 < player0.getWornItems().size(); int1++) {
                                InventoryItem item0 = player0.getWornItems().getItemByIndex(int1);
                                if (item0 instanceof AlarmClock) {
                                    ((AlarmClock)item0).stopRinging();
                                } else if (item0 instanceof AlarmClockClothing) {
                                    ((AlarmClockClothing)item0).stopRinging();
                                }
                            }

                            for (int int2 = 0; int2 < player0.getInventory().getItems().size(); int2++) {
                                InventoryItem item1 = player0.getInventory().getItems().get(int2);
                                if (item1 instanceof AlarmClock) {
                                    ((AlarmClock)item1).stopRinging();
                                } else if (item1 instanceof AlarmClockClothing) {
                                    ((AlarmClockClothing)item1).stopRinging();
                                }
                            }
                        }
                    }
                }
            } else if (this.isAlarmSet) {
                if (IsoPlayer.getInstance() != null) {
                    for (int int3 = 0; int3 < IsoPlayer.numPlayers; int3++) {
                        IsoPlayer player1 = IsoPlayer.players[int3];
                        if (player1 != null && !player1.isDead()) {
                            for (int int4 = 0; int4 < player1.getWornItems().size(); int4++) {
                                InventoryItem item2 = player1.getWornItems().getItemByIndex(int4);
                                if (item2 instanceof AlarmClock && ((AlarmClock)item2).isAlarmSet()) {
                                    ((AlarmClock)item2).setAlarmSet(false);
                                } else if (item2 instanceof AlarmClockClothing && ((AlarmClockClothing)item2).isAlarmSet()) {
                                    ((AlarmClockClothing)item2).setAlarmSet(false);
                                }
                            }

                            for (int int5 = 0; int5 < player1.getInventory().getItems().size(); int5++) {
                                InventoryItem item3 = player1.getInventory().getItems().get(int5);
                                if (item3 instanceof AlarmClockClothing && ((AlarmClockClothing)item3).isAlarmSet()) {
                                    ((AlarmClockClothing)item3).setAlarmSet(false);
                                }

                                if (item3 instanceof AlarmClock && ((AlarmClock)item3).isAlarmSet()) {
                                    ((AlarmClock)item3).setAlarmSet(false);
                                }
                            }
                        }
                    }
                }
            } else if (IsoPlayer.getInstance() != null) {
                for (int int6 = 0; int6 < IsoPlayer.numPlayers; int6++) {
                    IsoPlayer player2 = IsoPlayer.players[int6];
                    if (player2 != null && !player2.isDead()) {
                        for (int int7 = 0; int7 < player2.getWornItems().size(); int7++) {
                            InventoryItem item4 = player2.getWornItems().getItemByIndex(int7);
                            if (item4 instanceof AlarmClock && ((AlarmClock)item4).isDigital() && !((AlarmClock)item4).isAlarmSet()) {
                                ((AlarmClock)item4).setAlarmSet(true);
                                if (this.isAlarmSet) {
                                    return true;
                                }
                            }

                            if (item4 instanceof AlarmClockClothing && ((AlarmClockClothing)item4).isDigital() && !((AlarmClockClothing)item4).isAlarmSet()) {
                                ((AlarmClockClothing)item4).setAlarmSet(true);
                                if (this.isAlarmSet) {
                                    return true;
                                }
                            }
                        }

                        for (int int8 = 0; int8 < player2.getInventory().getItems().size(); int8++) {
                            InventoryItem item5 = player2.getInventory().getItems().get(int8);
                            if (item5 instanceof AlarmClock && ((AlarmClock)item5).isDigital() && !((AlarmClock)item5).isAlarmSet()) {
                                ((AlarmClock)item5).setAlarmSet(true);
                                if (this.isAlarmSet) {
                                    return true;
                                }
                            }

                            if (item5 instanceof AlarmClockClothing && ((AlarmClockClothing)item5).isDigital() && !((AlarmClockClothing)item5).isAlarmSet()) {
                                ((AlarmClockClothing)item5).setAlarmSet(true);
                                if (this.isAlarmSet) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
