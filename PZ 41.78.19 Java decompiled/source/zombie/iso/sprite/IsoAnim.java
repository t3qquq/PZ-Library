// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.textures.Texture;
import zombie.network.GameServer;
import zombie.network.ServerGUI;

public final class IsoAnim {
    public static final HashMap<String, IsoAnim> GlobalAnimMap = new HashMap<>();
    public short FinishUnloopedOnFrame = 0;
    public short FrameDelay = 0;
    public short LastFrame = 0;
    public final ArrayList<IsoDirectionFrame> Frames = new ArrayList<>(8);
    public String name;
    boolean looped = true;
    public int ID = 0;
    private static final ThreadLocal<StringBuilder> tlsStrBuf = new ThreadLocal<StringBuilder>() {
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };
    public IsoDirectionFrame[] FramesArray = new IsoDirectionFrame[0];

    public static void DisposeAll() {
        GlobalAnimMap.clear();
    }

    void LoadExtraFrame(String string2, String string0, int int0) {
        this.name = string0;
        String string1 = string2 + "_";
        String string3 = "_" + string0 + "_";
        Integer integer = new Integer(int0);
        IsoDirectionFrame directionFrame = new IsoDirectionFrame(
            Texture.getSharedTexture(string1 + "8" + string3 + integer.toString() + ".png"),
            Texture.getSharedTexture(string1 + "9" + string3 + integer.toString() + ".png"),
            Texture.getSharedTexture(string1 + "6" + string3 + integer.toString() + ".png"),
            Texture.getSharedTexture(string1 + "3" + string3 + integer.toString() + ".png"),
            Texture.getSharedTexture(string1 + "2" + string3 + integer.toString() + ".png")
        );
        this.Frames.add(directionFrame);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    public void LoadFramesReverseAltName(String ObjectName, String AnimName, String AltName, int nFrames) {
        this.name = AltName;
        StringBuilder stringBuilder = tlsStrBuf.get();
        stringBuilder.setLength(0);
        stringBuilder.append(ObjectName);
        stringBuilder.append("_%_");
        stringBuilder.append(AnimName);
        stringBuilder.append("_^");
        int int0 = stringBuilder.lastIndexOf("^");
        int int1 = stringBuilder.indexOf("_%_") + 1;
        stringBuilder.setCharAt(int1, '9');
        stringBuilder.setCharAt(int0, '0');
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            for (int int2 = 0; int2 < nFrames; int2++) {
                this.Frames.add(new IsoDirectionFrame(null));
            }

            this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
            this.FramesArray = this.Frames.toArray(this.FramesArray);
        }

        Texture texture = Texture.getSharedTexture(stringBuilder.toString());
        if (texture != null) {
            for (int int3 = 0; int3 < nFrames; int3++) {
                if (int3 == 10) {
                    stringBuilder.setLength(0);
                    stringBuilder.append(ObjectName);
                    stringBuilder.append("_1_");
                    stringBuilder.append(AnimName);
                    stringBuilder.append("_10");
                }

                Integer integer = int3;
                Object object = null;
                String string0 = integer.toString();
                if (texture == null) {
                    stringBuilder.setCharAt(int1, '8');

                    try {
                        stringBuilder.setCharAt(int0, integer.toString().charAt(0));
                    } catch (Exception exception) {
                        this.LoadFramesReverseAltName(ObjectName, AnimName, AltName, nFrames);
                    }

                    String string1 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '9');
                    String string2 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '6');
                    String string3 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '3');
                    String string4 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '2');
                    String string5 = stringBuilder.toString();
                    object = new IsoDirectionFrame(
                        Texture.getSharedTexture(string1),
                        Texture.getSharedTexture(string2),
                        Texture.getSharedTexture(string3),
                        Texture.getSharedTexture(string4),
                        Texture.getSharedTexture(string5)
                    );
                } else {
                    stringBuilder.setCharAt(int1, '9');

                    for (int int4 = 0; int4 < string0.length(); int4++) {
                        stringBuilder.setCharAt(int0 + int4, string0.charAt(int4));
                    }

                    String string6 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '6');
                    String string7 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '3');
                    String string8 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '2');
                    String string9 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '1');
                    String string10 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '4');
                    String string11 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '7');
                    String string12 = stringBuilder.toString();
                    stringBuilder.setCharAt(int1, '8');
                    String string13 = stringBuilder.toString();
                    object = new IsoDirectionFrame(
                        Texture.getSharedTexture(string6),
                        Texture.getSharedTexture(string7),
                        Texture.getSharedTexture(string8),
                        Texture.getSharedTexture(string9),
                        Texture.getSharedTexture(string10),
                        Texture.getSharedTexture(string11),
                        Texture.getSharedTexture(string12),
                        Texture.getSharedTexture(string13)
                    );
                }

                this.Frames.add(0, (IsoDirectionFrame)object);
            }

            this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
            this.FramesArray = this.Frames.toArray(this.FramesArray);
        }
    }

    public void LoadFrames(String ObjectName, String AnimName, int nFrames) {
        this.name = AnimName;
        StringBuilder stringBuilder = tlsStrBuf.get();
        stringBuilder.setLength(0);
        stringBuilder.append(ObjectName);
        stringBuilder.append("_%_");
        stringBuilder.append(AnimName);
        stringBuilder.append("_^");
        int int0 = stringBuilder.indexOf("_%_") + 1;
        int int1 = stringBuilder.lastIndexOf("^");
        stringBuilder.setCharAt(int0, '9');
        stringBuilder.setCharAt(int1, '0');
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            for (int int2 = 0; int2 < nFrames; int2++) {
                this.Frames.add(new IsoDirectionFrame(null));
            }

            this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        }

        Texture texture = Texture.getSharedTexture(stringBuilder.toString());
        if (texture != null) {
            for (int int3 = 0; int3 < nFrames; int3++) {
                if (int3 % 10 == 0 && int3 > 0) {
                    stringBuilder.setLength(0);
                    stringBuilder.append(ObjectName);
                    stringBuilder.append("_%_");
                    stringBuilder.append(AnimName);
                    stringBuilder.append("_^_");
                    int0 = stringBuilder.indexOf("_%_") + 1;
                    int1 = stringBuilder.lastIndexOf("^");
                }

                Integer integer = int3;
                Object object = null;
                String string0 = integer.toString();
                if (texture != null) {
                    stringBuilder.setCharAt(int0, '9');

                    for (int int4 = 0; int4 < string0.length(); int4++) {
                        stringBuilder.setCharAt(int1 + int4, string0.charAt(int4));
                    }

                    String string1 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '6');
                    String string2 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '3');
                    String string3 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '2');
                    String string4 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '1');
                    String string5 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '4');
                    String string6 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '7');
                    String string7 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '8');
                    String string8 = stringBuilder.toString();
                    object = new IsoDirectionFrame(
                        Texture.getSharedTexture(string1),
                        Texture.getSharedTexture(string2),
                        Texture.getSharedTexture(string3),
                        Texture.getSharedTexture(string4),
                        Texture.getSharedTexture(string5),
                        Texture.getSharedTexture(string6),
                        Texture.getSharedTexture(string7),
                        Texture.getSharedTexture(string8)
                    );
                } else {
                    try {
                        stringBuilder.setCharAt(int0, '8');
                    } catch (Exception exception0) {
                        this.LoadFrames(ObjectName, AnimName, nFrames);
                    }

                    for (int int5 = 0; int5 < string0.length(); int5++) {
                        try {
                            stringBuilder.setCharAt(int1 + int5, integer.toString().charAt(int5));
                        } catch (Exception exception1) {
                            this.LoadFrames(ObjectName, AnimName, nFrames);
                        }
                    }

                    String string9 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '9');
                    String string10 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '6');
                    String string11 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '3');
                    String string12 = stringBuilder.toString();
                    stringBuilder.setCharAt(int0, '2');
                    String string13 = stringBuilder.toString();
                    object = new IsoDirectionFrame(
                        Texture.getSharedTexture(string9),
                        Texture.getSharedTexture(string10),
                        Texture.getSharedTexture(string11),
                        Texture.getSharedTexture(string12),
                        Texture.getSharedTexture(string13)
                    );
                }

                this.Frames.add((IsoDirectionFrame)object);
            }

            this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
            this.FramesArray = this.Frames.toArray(this.FramesArray);
        }
    }

    public void LoadFramesUseOtherFrame(String ObjectName, String Variant, String AnimName, String OtherAnimName, int nOtherFrameFrame, String pal) {
        this.name = AnimName;
        String string0 = OtherAnimName + "_" + Variant + "_";
        String string1 = "_";
        String string2 = "";
        if (pal != null) {
            string2 = "_" + pal;
        }

        for (int int0 = 0; int0 < 1; int0++) {
            Integer integer = new Integer(nOtherFrameFrame);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(
                Texture.getSharedTexture(string0 + "8" + string1 + integer.toString() + string2 + ".png"),
                Texture.getSharedTexture(string0 + "9" + string1 + integer.toString() + string2 + ".png"),
                Texture.getSharedTexture(string0 + "6" + string1 + integer.toString() + string2 + ".png"),
                Texture.getSharedTexture(string0 + "3" + string1 + integer.toString() + string2 + ".png"),
                Texture.getSharedTexture(string0 + "2" + string1 + integer.toString() + string2 + ".png")
            );
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    public void LoadFramesBits(String ObjectName, String Variant, String AnimName, int nFrames) {
        this.name = AnimName;
        String string0 = AnimName + "_" + Variant + "_";
        String string1 = "_";

        for (int int0 = 0; int0 < nFrames; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(
                Texture.getSharedTexture(string0 + "8" + string1 + integer.toString() + ".png"),
                Texture.getSharedTexture(string0 + "9" + string1 + integer.toString() + ".png"),
                Texture.getSharedTexture(string0 + "6" + string1 + integer.toString() + ".png"),
                Texture.getSharedTexture(string0 + "3" + string1 + integer.toString() + ".png"),
                Texture.getSharedTexture(string0 + "2" + string1 + integer.toString() + ".png")
            );
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    public void LoadFramesBits(String ObjectName, String AnimName, int nFrames) {
        this.name = AnimName;
        String string0 = ObjectName + "_" + AnimName + "_";
        String string1 = "_";

        for (int int0 = 0; int0 < nFrames; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(
                Texture.getSharedTexture(string0 + "8" + string1 + integer.toString() + ".png"),
                Texture.getSharedTexture(string0 + "9" + string1 + integer.toString() + ".png"),
                Texture.getSharedTexture(string0 + "6" + string1 + integer.toString() + ".png"),
                Texture.getSharedTexture(string0 + "3" + string1 + integer.toString() + ".png"),
                Texture.getSharedTexture(string0 + "2" + string1 + integer.toString() + ".png")
            );
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    public void LoadFramesBitRepeatFrame(String ObjectName, String AnimName, int RepeatFrame) {
        this.name = AnimName;
        String string0 = "_";
        String string1 = "";
        Integer integer = new Integer(RepeatFrame);
        IsoDirectionFrame directionFrame = new IsoDirectionFrame(
            Texture.getSharedTexture(AnimName + "8" + string0 + integer.toString() + string1 + ".png"),
            Texture.getSharedTexture(AnimName + "9" + string0 + integer.toString() + string1 + ".png"),
            Texture.getSharedTexture(AnimName + "6" + string0 + integer.toString() + string1 + ".png"),
            Texture.getSharedTexture(AnimName + "3" + string0 + integer.toString() + string1 + ".png"),
            Texture.getSharedTexture(AnimName + "2" + string0 + integer.toString() + string1 + ".png")
        );
        this.Frames.add(directionFrame);
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    public void LoadFramesBitRepeatFrame(String ObjectName, String Variant, String AnimName, int RepeatFrame, String pal) {
        this.name = AnimName;
        String string0 = AnimName + "_" + Variant + "_";
        String string1 = "_";
        String string2 = "";
        if (pal != null) {
            string2 = "_" + pal;
        }

        Integer integer = new Integer(RepeatFrame);
        IsoDirectionFrame directionFrame = new IsoDirectionFrame(
            Texture.getSharedTexture(string0 + "8" + string1 + integer.toString() + string2 + ".png"),
            Texture.getSharedTexture(string0 + "9" + string1 + integer.toString() + string2 + ".png"),
            Texture.getSharedTexture(string0 + "6" + string1 + integer.toString() + string2 + ".png"),
            Texture.getSharedTexture(string0 + "3" + string1 + integer.toString() + string2 + ".png"),
            Texture.getSharedTexture(string0 + "2" + string1 + integer.toString() + string2 + ".png")
        );
        this.Frames.add(directionFrame);
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    public void LoadFramesBits(String ObjectName, String Variant, String AnimName, int nFrames, String pal) {
        this.name = AnimName;
        String string0 = AnimName + "_" + Variant + "_";
        String string1 = "_";
        String string2 = "";
        if (pal != null) {
            string2 = "_" + pal;
        }

        for (int int0 = 0; int0 < nFrames; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(
                Texture.getSharedTexture(string0 + "8" + string1 + integer.toString() + string2 + ".png"),
                Texture.getSharedTexture(string0 + "9" + string1 + integer.toString() + string2 + ".png"),
                Texture.getSharedTexture(string0 + "6" + string1 + integer.toString() + string2 + ".png"),
                Texture.getSharedTexture(string0 + "3" + string1 + integer.toString() + string2 + ".png"),
                Texture.getSharedTexture(string0 + "2" + string1 + integer.toString() + string2 + ".png")
            );
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    public void LoadFramesPcx(String ObjectName, String AnimName, int nFrames) {
        this.name = AnimName;
        String string0 = ObjectName + "_";
        String string1 = "_" + AnimName + "_";

        for (int int0 = 0; int0 < nFrames; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(
                Texture.getSharedTexture(string0 + "8" + string1 + integer.toString() + ".pcx"),
                Texture.getSharedTexture(string0 + "9" + string1 + integer.toString() + ".pcx"),
                Texture.getSharedTexture(string0 + "6" + string1 + integer.toString() + ".pcx"),
                Texture.getSharedTexture(string0 + "3" + string1 + integer.toString() + ".pcx"),
                Texture.getSharedTexture(string0 + "2" + string1 + integer.toString() + ".pcx")
            );
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    void Dispose() {
        for (int int0 = 0; int0 < this.Frames.size(); int0++) {
            IsoDirectionFrame directionFrame = this.Frames.get(int0);
            directionFrame.SetAllDirections(null);
        }
    }

    Texture LoadFrameExplicit(String string) {
        Texture texture = Texture.getSharedTexture(string);
        IsoDirectionFrame directionFrame = new IsoDirectionFrame(texture);
        this.Frames.add(directionFrame);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
        return texture;
    }

    void LoadFramesNoDir(String string2, String string0, int int1) {
        this.name = string0;
        String string1 = "media/" + string2;
        String string3 = "_" + string0 + "_";

        for (int int0 = 0; int0 < int1; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string1 + string3 + integer.toString() + ".png"));
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    void LoadFramesNoDirPage(String string2, String string0, int int1) {
        this.name = string0;
        String string1 = string2;
        String string3 = "_" + string0 + "_";

        for (int int0 = 0; int0 < int1; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string1 + string3 + integer.toString()));
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    void LoadFramesNoDirPageDirect(String string2, String string0, int int1) {
        this.name = string0;
        String string1 = string2;
        String string3 = "_" + string0 + "_";

        for (int int0 = 0; int0 < int1; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string1 + string3 + integer.toString() + ".png"));
            this.Frames.add(directionFrame);
        }

        this.FramesArray = this.Frames.toArray(this.FramesArray);
        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
    }

    void LoadFramesNoDirPage(String string1) {
        this.name = "default";
        String string0 = string1;

        for (int int0 = 0; int0 < 1; int0++) {
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string0));
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    public void LoadFramesPageSimple(String NObjectName, String SObjectName, String EObjectName, String WObjectName) {
        this.name = "default";

        for (int int0 = 0; int0 < 1; int0++) {
            new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(
                Texture.getSharedTexture(NObjectName),
                Texture.getSharedTexture(SObjectName),
                Texture.getSharedTexture(EObjectName),
                Texture.getSharedTexture(WObjectName)
            );
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    void LoadFramesNoDirPalette(String string2, String string0, int int1, String string4) {
        this.name = string0;
        String string1 = "media/characters/" + string2;
        String string3 = "_" + string0 + "_";

        for (int int0 = 0; int0 < int1; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(Texture.getSharedTexture(string1 + string3 + integer.toString() + ".pcx", string4));
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    void LoadFramesPalette(String string2, String string0, int int1, String string4) {
        this.name = string0;
        String string1 = string2 + "_";
        String string3 = "_" + string0 + "_";

        for (int int0 = 0; int0 < int1; int0++) {
            Integer integer = new Integer(int0);
            IsoDirectionFrame directionFrame = new IsoDirectionFrame(
                Texture.getSharedTexture(string1 + "8" + string3 + integer.toString() + "_" + string4),
                Texture.getSharedTexture(string1 + "9" + string3 + integer.toString() + "_" + string4),
                Texture.getSharedTexture(string1 + "6" + string3 + integer.toString() + "_" + string4),
                Texture.getSharedTexture(string1 + "3" + string3 + integer.toString() + "_" + string4),
                Texture.getSharedTexture(string1 + "2" + string3 + integer.toString() + "_" + string4)
            );
            this.Frames.add(directionFrame);
        }

        this.FinishUnloopedOnFrame = (short)(this.Frames.size() - 1);
        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }

    void DupeFrame() {
        for (int int0 = 0; int0 < 8; int0++) {
            IsoDirectionFrame directionFrame = new IsoDirectionFrame();
            directionFrame.directions[int0] = this.Frames.get(0).directions[int0];
            directionFrame.bDoFlip = this.Frames.get(0).bDoFlip;
            this.Frames.add(directionFrame);
        }

        this.FramesArray = this.Frames.toArray(this.FramesArray);
    }
}
