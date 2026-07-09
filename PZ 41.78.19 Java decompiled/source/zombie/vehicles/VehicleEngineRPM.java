// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.BaseScriptObject;

public class VehicleEngineRPM extends BaseScriptObject {
    public static final int MAX_GEARS = 8;
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    private String m_name;
    public final EngineRPMData[] m_rpmData = new EngineRPMData[8];

    public String getName() {
        return this.m_name;
    }

    public void Load(String name, String totalFile) throws RuntimeException {
        this.m_name = name;
        int int0 = -1;
        ScriptParser.Block block0 = ScriptParser.parse(totalFile);
        block0 = block0.children.get(0);

        for (ScriptParser.Value value : block0.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("VERSION".equals(string0)) {
                int0 = PZMath.tryParseInt(string1, -1);
                if (int0 < 0 || int0 > 1) {
                    throw new RuntimeException(String.format("unknown vehicleEngineRPM VERSION \"%s\"", string1));
                }
            }
        }

        if (int0 == -1) {
            throw new RuntimeException(String.format("unknown vehicleEngineRPM VERSION \"%s\"", block0.type));
        } else {
            int int1 = 0;

            for (ScriptParser.Block block1 : block0.children) {
                if (!"data".equals(block1.type)) {
                    throw new RuntimeException(String.format("unknown block vehicleEngineRPM.%s", block1.type));
                }

                if (int1 == 8) {
                    throw new RuntimeException(String.format("too many vehicleEngineRPM.data blocks, max is %d", 8));
                }

                this.m_rpmData[int1] = new EngineRPMData();
                this.LoadData(block1, this.m_rpmData[int1]);
                int1++;
            }
        }
    }

    private void LoadData(ScriptParser.Block block0, EngineRPMData engineRPMData) {
        for (ScriptParser.Value value : block0.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("afterGearChange".equals(string0)) {
                engineRPMData.afterGearChange = PZMath.tryParseFloat(string1, 0.0F);
            } else {
                if (!"gearChange".equals(string0)) {
                    throw new RuntimeException(String.format("unknown value vehicleEngineRPM.data.%s", value.string));
                }

                engineRPMData.gearChange = PZMath.tryParseFloat(string1, 0.0F);
            }
        }

        for (ScriptParser.Block block1 : block0.children) {
            if (!"xxx".equals(block1.type)) {
                throw new RuntimeException(String.format("unknown block vehicleEngineRPM.data.%s", block1.type));
            }
        }
    }

    public void reset() {
        for (int int0 = 0; int0 < this.m_rpmData.length; int0++) {
            if (this.m_rpmData[int0] != null) {
                this.m_rpmData[int0].reset();
            }
        }
    }
}
