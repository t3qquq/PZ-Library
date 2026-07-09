// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.nio.ByteBuffer;
import zombie.core.math.PZMath;
import zombie.iso.areas.NonPvpZone;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;

public class Safety {
    protected boolean enabled;
    protected boolean last;
    protected float cooldown;
    protected float toggle;
    protected IsoGameCharacter character;

    public Safety() {
    }

    public Safety(IsoGameCharacter _character) {
        this.character = _character;
        this.enabled = true;
        this.last = true;
        this.cooldown = 0.0F;
        this.toggle = 0.0F;
    }

    public void copyFrom(Safety other) {
        this.enabled = other.enabled;
        this.last = other.last;
        this.cooldown = other.cooldown;
        this.toggle = other.toggle;
    }

    public Object getCharacter() {
        return this.character;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean _enabled) {
        this.enabled = _enabled;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setLast(boolean _last) {
        this.last = _last;
    }

    public float getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(float _cooldown) {
        this.cooldown = _cooldown;
    }

    public float getToggle() {
        return this.toggle;
    }

    public void setToggle(float _toggle) {
        this.toggle = _toggle;
    }

    public boolean isToggleAllowed() {
        return ServerOptions.getInstance().PVP.getValue()
            && NonPvpZone.getNonPvpZone(PZMath.fastfloor(this.character.getX()), PZMath.fastfloor(this.character.getY())) == null
            && (!ServerOptions.getInstance().SafetySystem.getValue() || this.getCooldown() == 0.0F && this.getToggle() == 0.0F);
    }

    public void toggleSafety() {
        if (this.isToggleAllowed()) {
            if (GameClient.bClient) {
                GameClient.sendChangeSafety(this);
            } else {
                this.setToggle(ServerOptions.getInstance().SafetyToggleTimer.getValue());
                this.setLast(this.isEnabled());
                if (this.isEnabled()) {
                    this.setEnabled(!this.isEnabled());
                }

                if (GameServer.bServer) {
                    GameServer.sendChangeSafety(this);
                }
            }
        }
    }

    public void load(ByteBuffer input, int WorldVersion) {
        this.enabled = input.get() != 0;
        this.last = input.get() != 0;
        this.cooldown = input.getFloat();
        this.toggle = input.getFloat();
    }

    public void save(ByteBuffer output) {
        output.put((byte)(this.enabled ? 1 : 0));
        output.put((byte)(this.last ? 1 : 0));
        output.putFloat(this.cooldown);
        output.putFloat(this.toggle);
    }

    public String getDescription() {
        return "enabled=" + this.enabled + " last=" + this.last + " cooldown=" + this.cooldown + " toggle=" + this.toggle;
    }
}
