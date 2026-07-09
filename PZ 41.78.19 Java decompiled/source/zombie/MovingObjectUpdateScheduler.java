// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;

public final class MovingObjectUpdateScheduler {
    public static final MovingObjectUpdateScheduler instance = new MovingObjectUpdateScheduler();
    final MovingObjectUpdateSchedulerUpdateBucket fullSimulation = new MovingObjectUpdateSchedulerUpdateBucket(1);
    final MovingObjectUpdateSchedulerUpdateBucket halfSimulation = new MovingObjectUpdateSchedulerUpdateBucket(2);
    final MovingObjectUpdateSchedulerUpdateBucket quarterSimulation = new MovingObjectUpdateSchedulerUpdateBucket(4);
    final MovingObjectUpdateSchedulerUpdateBucket eighthSimulation = new MovingObjectUpdateSchedulerUpdateBucket(8);
    final MovingObjectUpdateSchedulerUpdateBucket sixteenthSimulation = new MovingObjectUpdateSchedulerUpdateBucket(16);
    long frameCounter;
    private boolean isEnabled = true;

    public long getFrameCounter() {
        return this.frameCounter;
    }

    public void startFrame() {
        this.frameCounter++;
        this.fullSimulation.clear();
        this.halfSimulation.clear();
        this.quarterSimulation.clear();
        this.eighthSimulation.clear();
        this.sixteenthSimulation.clear();
        ArrayList arrayList = IsoWorld.instance.getCell().getObjectList();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int0);
            if (!GameServer.bServer || !(movingObject instanceof IsoZombie)) {
                boolean boolean0 = false;
                boolean boolean1 = false;
                float float0 = 1.0E8F;
                boolean boolean2 = false;

                for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                    IsoPlayer player = IsoPlayer.players[int1];
                    if (player != null) {
                        if (movingObject.getCurrentSquare() == null) {
                            movingObject.setCurrent(
                                IsoWorld.instance.getCell().getGridSquare((double)movingObject.x, (double)movingObject.y, (double)movingObject.z)
                            );
                        }

                        if (player == movingObject) {
                            boolean2 = true;
                        }

                        if (movingObject.getCurrentSquare() != null) {
                            if (movingObject.getCurrentSquare().isCouldSee(int1)) {
                                boolean0 = true;
                            }

                            if (movingObject.getCurrentSquare().isCanSee(int1)) {
                                boolean1 = true;
                            }

                            float float1 = movingObject.DistTo(player);
                            if (float1 < float0) {
                                float0 = float1;
                            }
                        }
                    }
                }

                int int2 = 3;
                if (!boolean1) {
                    int2--;
                }

                if (!boolean0 && float0 > 10.0F) {
                    int2--;
                }

                if (float0 > 30.0F) {
                    int2--;
                }

                if (float0 > 60.0F) {
                    int2--;
                }

                if (float0 > 80.0F) {
                    int2--;
                }

                if (movingObject instanceof IsoPlayer) {
                    int2 = 3;
                }

                if (movingObject instanceof BaseVehicle) {
                    int2 = 3;
                }

                if (GameServer.bServer) {
                    int2 = 3;
                }

                if (boolean2) {
                    int2 = 3;
                }

                if (!this.isEnabled) {
                    int2 = 3;
                }

                if (int2 == 3) {
                    this.fullSimulation.add(movingObject);
                }

                if (int2 == 2) {
                    this.halfSimulation.add(movingObject);
                }

                if (int2 == 1) {
                    this.quarterSimulation.add(movingObject);
                }

                if (int2 == 0) {
                    this.eighthSimulation.add(movingObject);
                }

                if (int2 < 0) {
                    this.sixteenthSimulation.add(movingObject);
                }
            }
        }
    }

    public void update() {
        GameTime.getInstance().PerObjectMultiplier = 1.0F;
        this.fullSimulation.update((int)this.frameCounter);
        this.halfSimulation.update((int)this.frameCounter);
        this.quarterSimulation.update((int)this.frameCounter);
        this.eighthSimulation.update((int)this.frameCounter);
        this.sixteenthSimulation.update((int)this.frameCounter);
    }

    public void postupdate() {
        GameTime.getInstance().PerObjectMultiplier = 1.0F;
        this.fullSimulation.postupdate((int)this.frameCounter);
        this.halfSimulation.postupdate((int)this.frameCounter);
        this.quarterSimulation.postupdate((int)this.frameCounter);
        this.eighthSimulation.postupdate((int)this.frameCounter);
        this.sixteenthSimulation.postupdate((int)this.frameCounter);
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(boolean boolean0) {
        this.isEnabled = boolean0;
    }

    public void removeObject(IsoMovingObject movingObject) {
        this.fullSimulation.removeObject(movingObject);
        this.halfSimulation.removeObject(movingObject);
        this.quarterSimulation.removeObject(movingObject);
        this.eighthSimulation.removeObject(movingObject);
        this.sixteenthSimulation.removeObject(movingObject);
    }

    public ArrayList<IsoMovingObject> getBucket() {
        return this.fullSimulation.getBucket((int)this.frameCounter);
    }
}
