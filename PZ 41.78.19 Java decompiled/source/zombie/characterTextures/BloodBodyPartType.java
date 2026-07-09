// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characterTextures;

import java.util.ArrayList;
import zombie.core.Translator;
import zombie.core.skinnedmodel.model.CharacterMask;

/**
 * Created by LEMMY on 7/1/2016.
 */
public enum BloodBodyPartType {
    Hand_L,
    Hand_R,
    ForeArm_L,
    ForeArm_R,
    UpperArm_L,
    UpperArm_R,
    Torso_Upper,
    Torso_Lower,
    Head,
    Neck,
    Groin,
    UpperLeg_L,
    UpperLeg_R,
    LowerLeg_L,
    LowerLeg_R,
    Foot_L,
    Foot_R,
    Back,
    MAX;

    private CharacterMask.Part[] m_characterMaskParts;

    public int index() {
        return ToIndex(this);
    }

    public static BloodBodyPartType FromIndex(int index) {
        switch (index) {
            case 0:
                return Hand_L;
            case 1:
                return Hand_R;
            case 2:
                return ForeArm_L;
            case 3:
                return ForeArm_R;
            case 4:
                return UpperArm_L;
            case 5:
                return UpperArm_R;
            case 6:
                return Torso_Upper;
            case 7:
                return Torso_Lower;
            case 8:
                return Head;
            case 9:
                return Neck;
            case 10:
                return Groin;
            case 11:
                return UpperLeg_L;
            case 12:
                return UpperLeg_R;
            case 13:
                return LowerLeg_L;
            case 14:
                return LowerLeg_R;
            case 15:
                return Foot_L;
            case 16:
                return Foot_R;
            case 17:
                return Back;
            default:
                return MAX;
        }
    }

    public static int ToIndex(BloodBodyPartType BPT) {
        if (BPT == null) {
            return 0;
        } else {
            switch (BPT) {
                case Hand_L:
                    return 0;
                case Hand_R:
                    return 1;
                case ForeArm_L:
                    return 2;
                case ForeArm_R:
                    return 3;
                case UpperArm_L:
                    return 4;
                case UpperArm_R:
                    return 5;
                case Torso_Upper:
                    return 6;
                case Torso_Lower:
                    return 7;
                case Head:
                    return 8;
                case Neck:
                    return 9;
                case Groin:
                    return 10;
                case UpperLeg_L:
                    return 11;
                case UpperLeg_R:
                    return 12;
                case LowerLeg_L:
                    return 13;
                case LowerLeg_R:
                    return 14;
                case Foot_L:
                    return 15;
                case Foot_R:
                    return 16;
                case Back:
                    return 17;
                case MAX:
                    return 18;
                default:
                    return 17;
            }
        }
    }

    public static BloodBodyPartType FromString(String str) {
        if (str.equals("Hand_L")) {
            return Hand_L;
        } else if (str.equals("Hand_R")) {
            return Hand_R;
        } else if (str.equals("ForeArm_L")) {
            return ForeArm_L;
        } else if (str.equals("ForeArm_R")) {
            return ForeArm_R;
        } else if (str.equals("UpperArm_L")) {
            return UpperArm_L;
        } else if (str.equals("UpperArm_R")) {
            return UpperArm_R;
        } else if (str.equals("Torso_Upper")) {
            return Torso_Upper;
        } else if (str.equals("Torso_Lower")) {
            return Torso_Lower;
        } else if (str.equals("Head")) {
            return Head;
        } else if (str.equals("Neck")) {
            return Neck;
        } else if (str.equals("Groin")) {
            return Groin;
        } else if (str.equals("UpperLeg_L")) {
            return UpperLeg_L;
        } else if (str.equals("UpperLeg_R")) {
            return UpperLeg_R;
        } else if (str.equals("LowerLeg_L")) {
            return LowerLeg_L;
        } else if (str.equals("LowerLeg_R")) {
            return LowerLeg_R;
        } else if (str.equals("Foot_L")) {
            return Foot_L;
        } else if (str.equals("Foot_R")) {
            return Foot_R;
        } else {
            return str.equals("Back") ? Back : MAX;
        }
    }

    public CharacterMask.Part[] getCharacterMaskParts() {
        if (this.m_characterMaskParts != null) {
            return this.m_characterMaskParts;
        } else {
            ArrayList arrayList = new ArrayList();
            switch (this) {
                case Hand_L:
                    arrayList.add(CharacterMask.Part.LeftHand);
                    break;
                case Hand_R:
                    arrayList.add(CharacterMask.Part.RightHand);
                    break;
                case ForeArm_L:
                    arrayList.add(CharacterMask.Part.LeftArm);
                    break;
                case ForeArm_R:
                    arrayList.add(CharacterMask.Part.RightArm);
                    break;
                case UpperArm_L:
                    arrayList.add(CharacterMask.Part.LeftArm);
                    break;
                case UpperArm_R:
                    arrayList.add(CharacterMask.Part.RightArm);
                    break;
                case Torso_Upper:
                    arrayList.add(CharacterMask.Part.Chest);
                    break;
                case Torso_Lower:
                    arrayList.add(CharacterMask.Part.Waist);
                    break;
                case Head:
                    arrayList.add(CharacterMask.Part.Head);
                    break;
                case Neck:
                    arrayList.add(CharacterMask.Part.Head);
                    break;
                case Groin:
                    arrayList.add(CharacterMask.Part.Crotch);
                    break;
                case UpperLeg_L:
                    arrayList.add(CharacterMask.Part.LeftLeg);
                    arrayList.add(CharacterMask.Part.Pelvis);
                    break;
                case UpperLeg_R:
                    arrayList.add(CharacterMask.Part.RightLeg);
                    arrayList.add(CharacterMask.Part.Pelvis);
                    break;
                case LowerLeg_L:
                    arrayList.add(CharacterMask.Part.LeftLeg);
                    break;
                case LowerLeg_R:
                    arrayList.add(CharacterMask.Part.RightLeg);
                    break;
                case Foot_L:
                    arrayList.add(CharacterMask.Part.LeftFoot);
                    break;
                case Foot_R:
                    arrayList.add(CharacterMask.Part.RightFoot);
                    break;
                case Back:
                    arrayList.add(CharacterMask.Part.Torso);
            }

            this.m_characterMaskParts = new CharacterMask.Part[arrayList.size()];

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                this.m_characterMaskParts[int0] = (CharacterMask.Part)arrayList.get(int0);
            }

            return this.m_characterMaskParts;
        }
    }

    public String getDisplayName() {
        return getDisplayName(this);
    }

    public static String getDisplayName(BloodBodyPartType BPT) {
        if (BPT == Hand_L) {
            return Translator.getText("IGUI_health_Left_Hand");
        } else if (BPT == Hand_R) {
            return Translator.getText("IGUI_health_Right_Hand");
        } else if (BPT == ForeArm_L) {
            return Translator.getText("IGUI_health_Left_Forearm");
        } else if (BPT == ForeArm_R) {
            return Translator.getText("IGUI_health_Right_Forearm");
        } else if (BPT == UpperArm_L) {
            return Translator.getText("IGUI_health_Left_Upper_Arm");
        } else if (BPT == UpperArm_R) {
            return Translator.getText("IGUI_health_Right_Upper_Arm");
        } else if (BPT == Torso_Upper) {
            return Translator.getText("IGUI_health_Upper_Torso");
        } else if (BPT == Torso_Lower) {
            return Translator.getText("IGUI_health_Lower_Torso");
        } else if (BPT == Head) {
            return Translator.getText("IGUI_health_Head");
        } else if (BPT == Neck) {
            return Translator.getText("IGUI_health_Neck");
        } else if (BPT == Groin) {
            return Translator.getText("IGUI_health_Groin");
        } else if (BPT == UpperLeg_L) {
            return Translator.getText("IGUI_health_Left_Thigh");
        } else if (BPT == UpperLeg_R) {
            return Translator.getText("IGUI_health_Right_Thigh");
        } else if (BPT == LowerLeg_L) {
            return Translator.getText("IGUI_health_Left_Shin");
        } else if (BPT == LowerLeg_R) {
            return Translator.getText("IGUI_health_Right_Shin");
        } else if (BPT == Foot_L) {
            return Translator.getText("IGUI_health_Left_Foot");
        } else if (BPT == Foot_R) {
            return Translator.getText("IGUI_health_Right_Foot");
        } else {
            return BPT == Back ? Translator.getText("IGUI_health_Back") : Translator.getText("IGUI_health_Unknown_Body_Part");
        }
    }
}
