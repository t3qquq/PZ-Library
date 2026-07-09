// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import java.util.ArrayList;
import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;

public final class ParameterZone extends FMODGlobalParameter {
    private final String m_zoneName;
    private final ArrayList<IsoMetaGrid.Zone> m_zones = new ArrayList<>();

    public ParameterZone(String name, String zoneName) {
        super(name);
        this.m_zoneName = zoneName;
    }

    @Override
    public float calculateCurrentValue() {
        IsoGameCharacter character = this.getCharacter();
        if (character == null) {
            return 40.0F;
        } else {
            byte byte0 = 0;
            this.m_zones.clear();
            IsoWorld.instance.MetaGrid.getZonesIntersecting((int)character.x - 40, (int)character.y - 40, byte0, 80, 80, this.m_zones);
            float float0 = Float.MAX_VALUE;

            for (int int0 = 0; int0 < this.m_zones.size(); int0++) {
                IsoMetaGrid.Zone zone = this.m_zones.get(int0);
                if (this.m_zoneName.equalsIgnoreCase(zone.getType())) {
                    if (zone.contains((int)character.x, (int)character.y, byte0)) {
                        return 0.0F;
                    }

                    float float1 = zone.x + zone.w / 2.0F;
                    float float2 = zone.y + zone.h / 2.0F;
                    float float3 = PZMath.max(PZMath.abs(character.x - float1) - zone.w / 2.0F, 0.0F);
                    float float4 = PZMath.max(PZMath.abs(character.y - float2) - zone.h / 2.0F, 0.0F);
                    float0 = PZMath.min(float0, float3 * float3 + float4 * float4);
                }
            }

            return (int)PZMath.clamp(PZMath.sqrt(float0), 0.0F, 40.0F);
        }
    }

    private IsoGameCharacter getCharacter() {
        IsoPlayer player0 = null;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player1 = IsoPlayer.players[int0];
            if (player1 != null && (player0 == null || player0.isDead() && player1.isAlive() || player0.Traits.Deaf.isSet() && !player1.Traits.Deaf.isSet())) {
                player0 = player1;
            }
        }

        return player0;
    }
}
