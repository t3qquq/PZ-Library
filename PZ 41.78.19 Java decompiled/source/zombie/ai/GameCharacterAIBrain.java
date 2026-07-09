// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stance;
import zombie.characters.Stats;
import zombie.characters.SurvivorGroup;
import zombie.iso.IsoMovingObject;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.Vector3;

public final class GameCharacterAIBrain {
    private final IsoGameCharacter character;
    public final ArrayList<IsoGameCharacter> spottedCharacters = new ArrayList<>();
    public boolean StepBehaviors;
    public Stance stance;
    public boolean controlledByAdvancedPathfinder;
    public boolean isInMeta;
    public final HashMap<Vector3, ArrayList<Vector3>> BlockedMemories = new HashMap<>();
    public final Vector2 AIFocusPoint = new Vector2();
    public final Vector3 nextPathTarget = new Vector3();
    public IsoMovingObject aiTarget;
    public boolean NextPathNodeInvalidated;
    public final AIBrainPlayerControlVars HumanControlVars = new AIBrainPlayerControlVars();
    String order;
    public ArrayList<IsoZombie> teammateChasingZombies = new ArrayList<>();
    public ArrayList<IsoZombie> chasingZombies = new ArrayList<>();
    public boolean allowLongTermTick = true;
    public boolean isAI = false;
    static ArrayList<IsoZombie> tempZombies = new ArrayList<>();
    static IsoGameCharacter compare;
    private static final Stack<Vector3> Vectors = new Stack<>();

    public IsoGameCharacter getCharacter() {
        return this.character;
    }

    public GameCharacterAIBrain(IsoGameCharacter _character) {
        this.character = _character;
    }

    public void update() {
    }

    public void postUpdateHuman(IsoPlayer isoPlayer) {
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String _order) {
        this.order = _order;
    }

    public SurvivorGroup getGroup() {
        return this.character.getDescriptor().getGroup();
    }

    public int getCloseZombieCount() {
        this.character.getStats();
        return Stats.NumCloseZombies;
    }

    public IsoZombie getClosestChasingZombie(boolean recurse) {
        IsoZombie zombie0 = null;
        float float0 = 1.0E7F;

        for (int int0 = 0; int0 < this.chasingZombies.size(); int0++) {
            IsoZombie zombie1 = this.chasingZombies.get(int0);
            float float1 = zombie1.DistTo(this.character);
            if (zombie1.isOnFloor()) {
                float1 += 2.0F;
            }

            if (!LosUtil.lineClearCollide(
                    (int)zombie1.x, (int)zombie1.y, (int)zombie1.z, (int)this.character.x, (int)this.character.y, (int)this.character.z, false
                )
                && zombie1.getStateMachine().getCurrent() != ThumpState.instance()
                && float1 < float0
                && zombie1.target == this.character) {
                float0 = float1;
                zombie0 = this.chasingZombies.get(int0);
            }
        }

        if (zombie0 == null && recurse) {
            for (int int1 = 0; int1 < this.getGroup().Members.size(); int1++) {
                IsoGameCharacter character0 = this.getGroup().Members.get(int1).getInstance();
                IsoZombie zombie2 = character0.getGameCharacterAIBrain().getClosestChasingZombie(false);
                if (zombie2 != null) {
                    float float2 = zombie2.DistTo(this.character);
                    if (float2 < float0) {
                        float0 = float2;
                        zombie0 = zombie2;
                    }
                }
            }
        }

        if (zombie0 == null && recurse) {
            for (int int2 = 0; int2 < this.spottedCharacters.size(); int2++) {
                IsoGameCharacter character1 = this.spottedCharacters.get(int2);
                IsoZombie zombie3 = character1.getGameCharacterAIBrain().getClosestChasingZombie(false);
                if (zombie3 != null) {
                    float float3 = zombie3.DistTo(this.character);
                    if (float3 < float0) {
                        float0 = float3;
                        zombie0 = zombie3;
                    }
                }
            }
        }

        return zombie0 != null && zombie0.DistTo(this.character) > 30.0F ? null : zombie0;
    }

    public IsoZombie getClosestChasingZombie() {
        return this.getClosestChasingZombie(true);
    }

    public ArrayList<IsoZombie> getClosestChasingZombies(int num) {
        tempZombies.clear();
        Object object = null;
        float float0 = 1.0E7F;

        for (int int0 = 0; int0 < this.chasingZombies.size(); int0++) {
            IsoZombie zombie0 = this.chasingZombies.get(int0);
            float float1 = zombie0.DistTo(this.character);
            if (!LosUtil.lineClearCollide(
                (int)zombie0.x, (int)zombie0.y, (int)zombie0.z, (int)this.character.x, (int)this.character.y, (int)this.character.z, false
            )) {
                tempZombies.add(zombie0);
            }
        }

        compare = this.character;
        tempZombies.sort((zombie0x, zombie1) -> {
            float float0x = compare.DistTo(zombie0x);
            float float1x = compare.DistTo(zombie1);
            if (float0x > float1x) {
                return 1;
            } else {
                return float0x < float1x ? -1 : 0;
            }
        });
        int int1 = num - tempZombies.size();
        if (int1 > tempZombies.size() - 2) {
            int1 = tempZombies.size() - 2;
        }

        for (int int2 = 0; int2 < int1; int2++) {
            tempZombies.remove(tempZombies.size() - 1);
        }

        return tempZombies;
    }

    public void AddBlockedMemory(int ttx, int tty, int ttz) {
        synchronized (this.BlockedMemories) {
            Vector3 vector0 = new Vector3((int)this.character.x, (int)this.character.y, (int)this.character.z);
            if (!this.BlockedMemories.containsKey(vector0)) {
                this.BlockedMemories.put(vector0, new ArrayList<>());
            }

            ArrayList arrayList = this.BlockedMemories.get(vector0);
            Vector3 vector1 = new Vector3(ttx, tty, ttz);
            if (!arrayList.contains(vector1)) {
                arrayList.add(vector1);
            }
        }
    }

    public boolean HasBlockedMemory(int lx, int ly, int lz, int x, int y, int z) {
        synchronized (this.BlockedMemories) {
            synchronized (Vectors) {
                Vector3 vector0;
                if (Vectors.isEmpty()) {
                    vector0 = new Vector3();
                } else {
                    vector0 = Vectors.pop();
                }

                Vector3 vector1;
                if (Vectors.isEmpty()) {
                    vector1 = new Vector3();
                } else {
                    vector1 = Vectors.pop();
                }

                vector0.x = lx;
                vector0.y = ly;
                vector0.z = lz;
                vector1.x = x;
                vector1.y = y;
                vector1.z = z;
                if (!this.BlockedMemories.containsKey(vector0)) {
                    Vectors.push(vector0);
                    Vectors.push(vector1);
                    return false;
                }

                if (this.BlockedMemories.get(vector0).contains(vector1)) {
                    Vectors.push(vector0);
                    Vectors.push(vector1);
                    return true;
                }

                Vectors.push(vector0);
                Vectors.push(vector1);
            }

            return false;
        }
    }

    public void renderlast() {
    }
}
