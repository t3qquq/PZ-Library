// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.characters.skills.PerkFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.packets.INetworkPacket;

public class Player extends Character implements INetworkPacket {
    protected IsoPlayer player;
    protected short playerIndex;
    protected short playerFlags;
    protected float charge;
    protected float perkAiming;
    protected float combatSpeed;
    protected String attackType;
    protected AttackVars attackVars = new AttackVars();
    ArrayList<HitInfo> hitList = new ArrayList<>();

    public void set(IsoPlayer player1, boolean boolean0) {
        super.set(player1);
        this.playerIndex = player1.isLocal() ? (short)player1.getPlayerNum() : -1;
        this.player = player1;
        this.playerFlags = 0;
        this.playerFlags = (short)(this.playerFlags | (short)(player1.isAimAtFloor() ? 1 : 0));
        this.playerFlags = (short)(this.playerFlags | (short)(player1.isDoShove() ? 2 : 0));
        this.playerFlags = (short)(this.playerFlags | (short)(player1.isAttackFromBehind() ? 4 : 0));
        this.playerFlags |= (short)(boolean0 ? 8 : 0);
        this.charge = player1.useChargeDelta;
        this.perkAiming = player1.getPerkLevel(PerkFactory.Perks.Aiming);
        this.combatSpeed = player1.getVariableFloat("CombatSpeed", 1.0F);
        this.attackType = player1.getAttackType();
        this.attackVars.copy(player1.attackVars);
        this.hitList.clear();
        this.hitList.addAll(player1.hitList);
    }

    public void parsePlayer(UdpConnection udpConnection) {
        if (GameServer.bServer) {
            if (udpConnection != null && this.playerIndex != -1) {
                this.player = GameServer.getPlayerFromConnection(udpConnection, this.playerIndex);
            } else {
                this.player = GameServer.IDToPlayerMap.get(this.ID);
            }

            this.character = this.player;
        } else if (GameClient.bClient) {
            this.player = GameClient.IDToPlayerMap.get(this.ID);
            if (this.player == null) {
                IsoPlayer player1 = IsoPlayer.getInstance();
                if (player1 != null) {
                    this.player = new IsoPlayer(player1.getCell(), new SurvivorDesc(), (int)player1.x, (int)player1.y, (int)player1.z);
                }
            }

            this.character = this.player;
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.playerIndex = byteBuffer.getShort();
        this.playerFlags = byteBuffer.getShort();
        this.charge = byteBuffer.getFloat();
        this.perkAiming = byteBuffer.getFloat();
        this.combatSpeed = byteBuffer.getFloat();
        this.attackType = GameWindow.ReadString(byteBuffer);
        this.attackVars.parse(byteBuffer, udpConnection);
        byte byte0 = byteBuffer.get();

        for (int int0 = 0; int0 < byte0; int0++) {
            HitInfo hitInfo = new HitInfo();
            hitInfo.parse(byteBuffer, udpConnection);
            this.hitList.add(hitInfo);
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putShort(this.playerIndex);
        byteBufferWriter.putShort(this.playerFlags);
        byteBufferWriter.putFloat(this.charge);
        byteBufferWriter.putFloat(this.perkAiming);
        byteBufferWriter.putFloat(this.combatSpeed);
        byteBufferWriter.putUTF(this.attackType);
        this.attackVars.write(byteBufferWriter);
        byte byte0 = (byte)this.hitList.size();
        byteBufferWriter.putByte(byte0);

        for (int int0 = 0; int0 < byte0; int0++) {
            this.hitList.get(int0).write(byteBufferWriter);
        }
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.player != null;
    }

    @Override
    public String getDescription() {
        String string = "";
        byte byte0 = (byte)Math.min(100, this.hitList.size());

        for (int int0 = 0; int0 < byte0; int0++) {
            HitInfo hitInfo = this.hitList.get(int0);
            string = string + hitInfo.getDescription();
        }

        return super.getDescription()
            + "\n\tPlayer [ player "
            + (this.player == null ? "?" : "\"" + this.player.getUsername() + "\"")
            + " | charge="
            + this.charge
            + " | perkAiming="
            + this.perkAiming
            + " | combatSpeed="
            + this.combatSpeed
            + " | attackType=\""
            + this.attackType
            + "\" | isAimAtFloor="
            + ((this.playerFlags & 1) != 0)
            + " | isDoShove="
            + ((this.playerFlags & 2) != 0)
            + " | isAttackFromBehind="
            + ((this.playerFlags & 4) != 0)
            + " | isCriticalHit="
            + ((this.playerFlags & 8) != 0)
            + " | _bodyDamage="
            + (this.player == null ? "?" : this.player.getBodyDamage().getHealth())
            + this.attackVars.getDescription()
            + "\n\t hitList=["
            + string
            + "](count="
            + this.hitList.size()
            + ") ]";
    }

    @Override
    void process() {
        super.process();
        this.player.useChargeDelta = this.charge;
        this.player.setVariable("recoilVarX", this.perkAiming / 10.0F);
        this.player.setAttackType(this.attackType);
        this.player.setVariable("CombatSpeed", this.combatSpeed);
        this.player.setVariable("AimFloorAnim", (this.playerFlags & 1) != 0);
        this.player.setAimAtFloor((this.playerFlags & 1) != 0);
        this.player.setDoShove((this.playerFlags & 2) != 0);
        this.player.setAttackFromBehind((this.playerFlags & 4) != 0);
        this.player.setCriticalHit((this.playerFlags & 8) != 0);
    }

    void attack(HandWeapon weapon, boolean boolean0) {
        if (GameClient.bClient) {
            this.player.attackStarted = false;
            this.player.attackVars.copy(this.attackVars);
            this.player.hitList.clear();
            this.player.hitList.addAll(this.hitList);
            this.player.pressedAttack(false);
            if (this.player.isAttackStarted() && weapon.isRanged() && !this.player.isDoShove()) {
                this.player.startMuzzleFlash();
            }

            if (weapon.getPhysicsObject() != null) {
                this.player.Throw(weapon);
            }
        } else if (GameServer.bServer && boolean0 && !this.player.getSafety().isEnabled()) {
            this.player.getSafety().setCooldown(this.player.getSafety().getCooldown() + ServerOptions.getInstance().SafetyCooldownTimer.getValue());
            GameServer.sendChangeSafety(this.player.getSafety());
        }
    }

    @Override
    public IsoGameCharacter getCharacter() {
        return this.player;
    }

    public IsoPlayer getPlayer() {
        return this.player;
    }

    boolean isRelevant(UdpConnection udpConnection) {
        return udpConnection.RelevantTo(this.positionX, this.positionY);
    }
}
