// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.ai.states.ZombieIdleState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public final class ReanimatedPlayers {
    public static ReanimatedPlayers instance = new ReanimatedPlayers();
    private final ArrayList<IsoZombie> Zombies = new ArrayList<>();

    private static void noise(String string) {
        DebugLog.log("reanimate: " + string);
    }

    public void addReanimatedPlayersToChunk(IsoChunk chunk) {
        int int0 = chunk.wx * 10;
        int int1 = chunk.wy * 10;
        int int2 = int0 + 10;
        int int3 = int1 + 10;

        for (int int4 = 0; int4 < this.Zombies.size(); int4++) {
            IsoZombie zombie0 = this.Zombies.get(int4);
            if (zombie0.getX() >= int0 && zombie0.getX() < int2 && zombie0.getY() >= int1 && zombie0.getY() < int3) {
                IsoGridSquare square = chunk.getGridSquare((int)zombie0.getX() - int0, (int)zombie0.getY() - int1, (int)zombie0.getZ());
                if (square != null) {
                    if (GameServer.bServer) {
                        if (zombie0.OnlineID != -1) {
                            noise("ERROR? OnlineID != -1 for reanimated player zombie");
                        }

                        zombie0.OnlineID = ServerMap.instance.getUniqueZombieId();
                        if (zombie0.OnlineID == -1) {
                            continue;
                        }

                        ServerMap.instance.ZombieMap.put(zombie0.OnlineID, zombie0);
                    }

                    zombie0.setCurrent(square);

                    assert !IsoWorld.instance.CurrentCell.getObjectList().contains(zombie0);

                    assert !IsoWorld.instance.CurrentCell.getZombieList().contains(zombie0);

                    IsoWorld.instance.CurrentCell.getObjectList().add(zombie0);
                    IsoWorld.instance.CurrentCell.getZombieList().add(zombie0);
                    this.Zombies.remove(int4);
                    int4--;
                    SharedDescriptors.createPlayerZombieDescriptor(zombie0);
                    noise("added to world " + zombie0);
                }
            }
        }
    }

    public void removeReanimatedPlayerFromWorld(IsoZombie zombie0) {
        if (zombie0.isReanimatedPlayer()) {
            if (!GameServer.bServer) {
                zombie0.setSceneCulled(true);
            }

            if (zombie0.isOnFire()) {
                IsoFireManager.RemoveBurningCharacter(zombie0);
                zombie0.setOnFire(false);
            }

            if (zombie0.AttachedAnimSprite != null) {
                ArrayList arrayList = zombie0.AttachedAnimSprite;

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    IsoSpriteInstance spriteInstance = (IsoSpriteInstance)arrayList.get(int0);
                    IsoSpriteInstance.add(spriteInstance);
                }

                zombie0.AttachedAnimSprite.clear();
            }

            if (!GameServer.bServer) {
                for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                    IsoPlayer player = IsoPlayer.players[int1];
                    if (player != null && player.ReanimatedCorpse == zombie0) {
                        player.ReanimatedCorpse = null;
                        player.ReanimatedCorpseID = -1;
                    }
                }
            }

            if (GameServer.bServer && zombie0.OnlineID != -1) {
                ServerMap.instance.ZombieMap.remove(zombie0.OnlineID);
                zombie0.OnlineID = -1;
            }

            SharedDescriptors.releasePlayerZombieDescriptor(zombie0);

            assert !VirtualZombieManager.instance.isReused(zombie0);

            if (!zombie0.isDead()) {
                if (!GameClient.bClient) {
                    if (!this.Zombies.contains(zombie0)) {
                        this.Zombies.add(zombie0);
                        noise("added to Zombies " + zombie0);
                        zombie0.setStateMachineLocked(false);
                        zombie0.changeState(ZombieIdleState.instance());
                    }
                }
            }
        }
    }

    public void saveReanimatedPlayers() {
        if (!GameClient.bClient) {
            ArrayList arrayList = new ArrayList();

            try {
                ByteBuffer byteBuffer = SliceY.SliceBuffer;
                byteBuffer.clear();
                byteBuffer.putInt(195);
                arrayList.addAll(this.Zombies);

                for (IsoZombie zombie0 : IsoWorld.instance.CurrentCell.getZombieList()) {
                    if (zombie0.isReanimatedPlayer() && !zombie0.isDead() && !arrayList.contains(zombie0)) {
                        arrayList.add(zombie0);
                    }
                }

                byteBuffer.putInt(arrayList.size());

                for (IsoZombie zombie1 : arrayList) {
                    zombie1.save(byteBuffer);
                }

                File file = ZomboidFileSystem.instance.getFileInCurrentSave("reanimated.bin");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return;
            }

            noise("saved " + arrayList.size() + " zombies");
        }
    }

    public void loadReanimatedPlayers() {
        if (!GameClient.bClient) {
            this.Zombies.clear();
            File file = ZomboidFileSystem.instance.getFileInCurrentSave("reanimated.bin");

            try (
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            ) {
                synchronized (SliceY.SliceBufferLock) {
                    ByteBuffer byteBuffer = SliceY.SliceBuffer;
                    byteBuffer.clear();
                    int int0 = bufferedInputStream.read(byteBuffer.array());
                    byteBuffer.limit(int0);
                    this.loadReanimatedPlayers(byteBuffer);
                }
            } catch (FileNotFoundException fileNotFoundException) {
                return;
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return;
            }

            noise("loaded " + this.Zombies.size() + " zombies");
        }
    }

    private void loadReanimatedPlayers(ByteBuffer byteBuffer) throws IOException, RuntimeException {
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();

        for (int int2 = 0; int2 < int1; int2++) {
            if (!(IsoObject.factoryFromFileInput(IsoWorld.instance.CurrentCell, byteBuffer) instanceof IsoZombie zombie0)) {
                throw new RuntimeException("expected IsoZombie here");
            }

            zombie0.load(byteBuffer, int0);
            zombie0.getDescriptor().setID(0);
            zombie0.setReanimatedPlayer(true);
            IsoWorld.instance.CurrentCell.getAddList().remove(zombie0);
            IsoWorld.instance.CurrentCell.getObjectList().remove(zombie0);
            IsoWorld.instance.CurrentCell.getZombieList().remove(zombie0);
            this.Zombies.add(zombie0);
        }
    }
}
