// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

public class BlendInfo {
    public String name;
    public BlendType Type;
    public String mulDec;
    public String mulInc;
    public float dec;
    public float inc;

    public static class BlendInstance {
        public float current = -1.0F;
        public float target;
        BlendInfo info;

        public String GetDebug() {
            String string = "Blend: " + this.info.name;
            switch (this.info.Type) {
                case Linear:
                    string = string + ", Linear ";
                    break;
                case InverseExponential:
                    string = string + ", InverseExponential ";
            }

            return string + ", Current " + this.current;
        }

        public BlendInstance(BlendInfo blendInfo) {
            this.info = blendInfo;
        }

        public void set(float float0) {
            this.target = float0;
            if (this.current == -1.0F) {
                this.current = this.target;
            }
        }

        public void update() {
            float float0 = 0.0F;
            if (this.current < this.target) {
                float float1 = 1.0F;
                switch (this.info.Type) {
                    case InverseExponential:
                        float1 = this.current / 1.0F;
                        float1 = 1.0F - float1;
                        if (float1 < 0.1F) {
                            float1 = 0.1F;
                        }
                    case Linear:
                    default:
                        float0 = this.info.inc * float1;
                        this.current += float0;
                        if (this.current > this.target) {
                            this.current = this.target;
                        }
                }
            } else if (this.current > this.target) {
                float float2 = 1.0F;
                switch (this.info.Type) {
                    case InverseExponential:
                        float2 = this.current / 1.0F;
                        float2 = 1.0F - float2;
                        if (float2 < 0.1F) {
                            float2 = 0.1F;
                        }
                    case Linear:
                    default:
                        float0 = -this.info.dec * float2;
                        this.current += float0;
                        if (this.current < this.target) {
                            this.current = this.target;
                        }
                }
            }
        }
    }
}
