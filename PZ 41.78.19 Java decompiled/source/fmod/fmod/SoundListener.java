// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

import fmod.javafmod;
import zombie.SoundManager;
import zombie.characters.IsoPlayer;
import zombie.iso.Vector3;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class SoundListener extends BaseSoundListener {
    public float lx;
    public float ly;
    public float lz;
    private static final Vector3 vec = new Vector3();

    public SoundListener(int int0) {
        super(int0);
    }

    @Override
    public void tick() {
        if (!GameServer.bServer) {
            int int0 = 0;

            for (int int1 = 0; int1 < IsoPlayer.numPlayers && int1 != this.index; int1++) {
                if (IsoPlayer.players[int1] != null) {
                    int0++;
                }
            }

            vec.x = -1.0F;
            vec.y = -1.0F;
            vec.normalize();
            if (IsoPlayer.players[this.index] != null && IsoPlayer.players[this.index].Traits.Deaf.isSet()) {
                this.x = -1000.0F;
                this.y = -1000.0F;
                this.z = 0.0F;
            }

            this.lx = this.x;
            this.ly = this.y;
            this.lz = this.z;
            if (!GameClient.bClient || SoundManager.instance.getSoundVolume() > 0.0F) {
                javafmod.FMOD_Studio_Listener3D(
                    int0, this.x, this.y, this.z * 3.0F, this.x - this.lx, this.y - this.ly, this.z - this.lz, vec.x, vec.y, vec.z, 0.0F, 0.0F, 1.0F
                );
            }

            this.lx = this.x;
            this.ly = this.y;
            this.lz = this.z;
        }
    }
}
