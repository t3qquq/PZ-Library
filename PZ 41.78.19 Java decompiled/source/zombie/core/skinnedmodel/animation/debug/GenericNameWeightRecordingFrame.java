// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation.debug;

import zombie.util.list.PZArrayUtil;

public class GenericNameWeightRecordingFrame extends GenericNameValueRecordingFrame {
    private float[] m_weights = new float[0];

    public GenericNameWeightRecordingFrame(String string) {
        super(string, "_weights");
    }

    @Override
    protected void onColumnAdded() {
        this.m_weights = PZArrayUtil.add(this.m_weights, 0.0F);
    }

    public void logWeight(String string, int int1, float float0) {
        int int0 = this.getOrCreateColumn(string, int1);
        this.m_weights[int0] = this.m_weights[int0] + float0;
    }

    public int getOrCreateColumn(String string2, int int0) {
        String string0 = int0 != 0 ? int0 + ":" : "";
        String string1 = String.format("%s%s", string0, string2);
        int int1 = super.getOrCreateColumn(string1);
        if (this.m_weights[int1] == 0.0F) {
            return int1;
        } else {
            int int2 = 1;

            while (true) {
                String string3 = String.format("%s%s-%d", string0, string2, int2);
                int1 = super.getOrCreateColumn(string3);
                if (this.m_weights[int1] == 0.0F) {
                    return int1;
                }

                int2++;
            }
        }
    }

    public float getWeightAt(int int0) {
        return this.m_weights[int0];
    }

    @Override
    public String getValueAt(int int0) {
        return String.valueOf(this.getWeightAt(int0));
    }

    @Override
    public void reset() {
        int int0 = 0;

        for (int int1 = this.m_weights.length; int0 < int1; int0++) {
            this.m_weights[int0] = 0.0F;
        }
    }
}
