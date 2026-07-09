// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.FliesSound;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SharedDescriptors;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.characters.Talker;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedItems;
import zombie.characters.AttachedItems.AttachedLocationGroup;
import zombie.characters.AttachedItems.AttachedLocations;
import zombie.characters.WornItems.BodyLocationGroup;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItems;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.physics.Transform;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.visual.BaseVisual;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.debug.LogSeverity;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Food;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.IsoObjectID;
import zombie.network.ServerGUI;
import zombie.network.ServerLOS;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;

public final class IsoDeadBody extends IsoMovingObject implements Talker, IHumanVisual {
    public static final int MAX_ROT_STAGES = 3;
    private static final int VISUAL_TYPE_HUMAN = 0;
    private static final IsoObjectID<IsoDeadBody> Bodies = new IsoObjectID<>(IsoDeadBody.class);
    private static final ArrayList<IsoDeadBody> tempBodies = new ArrayList<>();
    private boolean bFemale = false;
    private boolean wasZombie = false;
    private boolean bFakeDead = false;
    private boolean bCrawling = false;
    private Color SpeakColor;
    private float SpeakTime = 0.0F;
    private int m_persistentOutfitID;
    private SurvivorDesc desc;
    private BaseVisual baseVisual = null;
    private WornItems wornItems;
    private AttachedItems attachedItems;
    private float deathTime = -1.0F;
    private float reanimateTime = -1.0F;
    private IsoPlayer player;
    private boolean fallOnFront = false;
    private boolean wasSkeleton = false;
    private InventoryItem primaryHandItem = null;
    private InventoryItem secondaryHandItem = null;
    private float m_angle;
    private int m_zombieRotStageAtDeath = 1;
    private short onlineID = -1;
    private short objectID = -1;
    private static final ThreadLocal<IsoZombie> tempZombie = new ThreadLocal<IsoZombie>() {
        public IsoZombie initialValue() {
            return new IsoZombie(null);
        }
    };
    private static ColorInfo inf = new ColorInfo();
    public DeadBodyAtlas.BodyTexture atlasTex;
    private static Texture DropShadow = null;
    private static final float HIT_TEST_WIDTH = 0.3F;
    private static final float HIT_TEST_HEIGHT = 0.9F;
    private static final Quaternionf _rotation = new Quaternionf();
    private static final Transform _transform = new Transform();
    private static final Vector3f _UNIT_Z = new Vector3f(0.0F, 0.0F, 1.0F);
    private static final Vector3f _tempVec3f_1 = new Vector3f();
    private static final Vector3f _tempVec3f_2 = new Vector3f();
    private float burnTimer = 0.0F;
    public boolean Speaking = false;
    public String sayLine = "";

    public static boolean isDead(short id) {
        float float0 = (float)GameTime.getInstance().getWorldAgeHours();

        for (IsoDeadBody deadBody : Bodies) {
            if (deadBody.onlineID == id && float0 - deadBody.deathTime < 0.1F) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getObjectName() {
        return "DeadBody";
    }

    public IsoDeadBody(IsoGameCharacter died) {
        this(died, false);
    }

    public IsoDeadBody(IsoGameCharacter died, boolean wasCorpseAlready) {
        super(died.getCell(), false);
        IsoZombie zombie0 = Type.tryCastTo(died, IsoZombie.class);
        this.setFallOnFront(died.isFallOnFront());
        if (!GameClient.bClient && !GameServer.bServer && zombie0 != null && zombie0.bCrawling) {
            if (!zombie0.isReanimate()) {
                this.setFallOnFront(true);
            }

            this.bCrawling = true;
        }

        IsoGridSquare square0 = died.getCurrentSquare();
        if (square0 != null) {
            if (died.getZ() < 0.0F) {
                DebugLog.General.error("invalid z-coordinate %d,%d,%d", died.x, died.y, died.z);
                died.setZ(0.0F);
            }

            this.square = square0;
            this.current = square0;
            if (died instanceof IsoPlayer) {
                ((IsoPlayer)died).removeSaveFile();
            }

            square0.getStaticMovingObjects().add(this);
            if (died instanceof IsoSurvivor) {
                IsoWorld.instance.TotalSurvivorNights = IsoWorld.instance.TotalSurvivorNights + ((IsoSurvivor)died).nightsSurvived;
                IsoWorld.instance.TotalSurvivorsDead++;
                if (IsoWorld.instance.SurvivorSurvivalRecord < ((IsoSurvivor)died).nightsSurvived) {
                    IsoWorld.instance.SurvivorSurvivalRecord = ((IsoSurvivor)died).nightsSurvived;
                }
            }

            this.bFemale = died.isFemale();
            this.wasZombie = zombie0 != null;
            if (this.wasZombie) {
                this.bFakeDead = zombie0.isFakeDead();
                this.wasSkeleton = zombie0.isSkeleton();
            }

            this.dir = died.dir;
            this.m_angle = died.getAnimAngleRadians();
            this.Collidable = false;
            this.x = died.getX();
            this.y = died.getY();
            this.z = died.getZ();
            this.nx = this.x;
            this.ny = this.y;
            this.offsetX = died.offsetX;
            this.offsetY = died.offsetY;
            this.solid = false;
            this.shootable = false;
            this.onlineID = died.getOnlineID();
            this.OutlineOnMouseover = true;
            this.setContainer(died.getInventory());
            this.setWornItems(died.getWornItems());
            this.setAttachedItems(died.getAttachedItems());
            if (died instanceof IHumanVisual) {
                this.baseVisual = new HumanVisual(this);
                this.baseVisual.copyFrom(((IHumanVisual)died).getHumanVisual());
                this.m_zombieRotStageAtDeath = this.getHumanVisual().zombieRotStage;
            }

            died.setInventory(new ItemContainer());
            died.clearWornItems();
            died.clearAttachedItems();
            if (!this.container.bExplored) {
                this.container.setExplored(died instanceof IsoPlayer || died instanceof IsoZombie && ((IsoZombie)died).isReanimatedPlayer());
            }

            boolean boolean0 = died.isOnFire();
            if (died instanceof IsoZombie) {
                this.m_persistentOutfitID = died.getPersistentOutfitID();
                if (!wasCorpseAlready && !GameServer.bServer) {
                    for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                        IsoPlayer playerx = IsoPlayer.players[int0];
                        if (playerx != null && playerx.ReanimatedCorpse == died) {
                            playerx.ReanimatedCorpse = null;
                            playerx.ReanimatedCorpseID = -1;
                        }
                    }

                    if (!GameClient.bClient && died.emitter != null) {
                        died.emitter.tick();
                    }
                }
            } else {
                if (died instanceof IsoSurvivor) {
                    this.getCell().getSurvivorList().remove(died);
                }

                this.desc = new SurvivorDesc(died.getDescriptor());
                if (died instanceof IsoPlayer) {
                    if (GameServer.bServer) {
                        this.player = (IsoPlayer)died;
                    } else if (!GameClient.bClient && ((IsoPlayer)died).isLocalPlayer()) {
                        this.player = (IsoPlayer)died;
                    }
                }
            }

            LuaManager.copyTable(this.getModData(), died.getModData());
            died.removeFromWorld();
            died.removeFromSquare();
            this.sayLine = died.getSayLine();
            this.SpeakColor = died.getSpeakColour();
            this.SpeakTime = died.getSpeakTime();
            this.Speaking = died.isSpeaking();
            if (boolean0) {
                if (!GameClient.bClient && SandboxOptions.instance.FireSpread.getValue()) {
                    IsoFireManager.StartFire(this.getCell(), this.getSquare(), true, 100, 500);
                }

                this.container.setExplored(true);
            }

            if (!wasCorpseAlready && !GameServer.bServer) {
                LuaEventManager.triggerEvent("OnContainerUpdate", this);
            }

            if (died instanceof IsoPlayer) {
                ((IsoPlayer)died).bDeathFinished = true;
            }

            this.deathTime = (float)GameTime.getInstance().getWorldAgeHours();
            this.setEatingZombies(died.getEatingZombies());
            if (!this.wasZombie) {
                ArrayList arrayList = new ArrayList();

                for (int int1 = -2; int1 < 2; int1++) {
                    for (int int2 = -2; int2 < 2; int2++) {
                        IsoGridSquare square1 = square0.getCell().getGridSquare(square0.x + int1, square0.y + int2, square0.z);
                        if (square1 != null) {
                            for (int int3 = 0; int3 < square1.getMovingObjects().size(); int3++) {
                                if (square1.getMovingObjects().get(int3) instanceof IsoZombie) {
                                    arrayList.add(square1.getMovingObjects().get(int3));
                                }
                            }
                        }
                    }
                }

                for (int int4 = 0; int4 < arrayList.size(); int4++) {
                    ((IsoZombie)arrayList.get(int4)).pathToLocationF(this.getX() + Rand.Next(-0.3F, 0.3F), this.getY() + Rand.Next(-0.3F, 0.3F), this.getZ());
                    ((IsoZombie)arrayList.get(int4)).bodyToEat = this;
                }
            }

            if (!GameClient.bClient) {
                this.objectID = Bodies.allocateID();
            }

            Bodies.put(this.objectID, this);
            if (!GameServer.bServer) {
                FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
            }

            DebugLog.Death.noise("Corpse created %s", this.getDescription());
        }
    }

    public IsoDeadBody(IsoCell cell) {
        super(cell, false);
        this.SpeakColor = Color.white;
        this.solid = false;
        this.shootable = false;
        BodyLocationGroup bodyLocationGroup = BodyLocations.getGroup("Human");
        this.wornItems = new WornItems(bodyLocationGroup);
        AttachedLocationGroup attachedLocationGroup = AttachedLocations.getGroup("Human");
        this.attachedItems = new AttachedItems(attachedLocationGroup);
        DebugLog.Death.noise("Corpse created on cell %s", this.getDescription());
    }

    public BaseVisual getVisual() {
        return this.baseVisual;
    }

    @Override
    public HumanVisual getHumanVisual() {
        return Type.tryCastTo(this.baseVisual, HumanVisual.class);
    }

    @Override
    public void getItemVisuals(ItemVisuals itemVisuals) {
        this.wornItems.getItemVisuals(itemVisuals);
    }

    @Override
    public boolean isFemale() {
        return this.bFemale;
    }

    @Override
    public boolean isZombie() {
        return this.wasZombie;
    }

    public boolean isCrawling() {
        return this.bCrawling;
    }

    public void setCrawling(boolean crawling) {
        this.bCrawling = crawling;
    }

    public boolean isFakeDead() {
        return this.bFakeDead;
    }

    public void setFakeDead(boolean fakeDead) {
        if (!fakeDead || SandboxOptions.instance.Lore.DisableFakeDead.getValue() != 3) {
            this.bFakeDead = fakeDead;
        }
    }

    @Override
    public boolean isSkeleton() {
        return this.wasSkeleton;
    }

    public void setWornItems(WornItems other) {
        this.wornItems = new WornItems(other);
    }

    public WornItems getWornItems() {
        return this.wornItems;
    }

    public void setAttachedItems(AttachedItems other) {
        if (other != null) {
            this.attachedItems = new AttachedItems(other);

            for (int int0 = 0; int0 < this.attachedItems.size(); int0++) {
                AttachedItem attachedItem = this.attachedItems.get(int0);
                InventoryItem item = attachedItem.getItem();
                if (!this.container.contains(item) && !GameClient.bClient && !GameServer.bServer) {
                    item.setContainer(this.container);
                    this.container.getItems().add(item);
                }
            }
        }
    }

    public AttachedItems getAttachedItems() {
        return this.attachedItems;
    }

    public InventoryItem getItem() {
        InventoryItem item = InventoryItemFactory.CreateItem("Base.CorpseMale");
        item.storeInByteData(this);
        return item;
    }

    private IsoSprite loadSprite(ByteBuffer byteBuffer) {
        String string = GameWindow.ReadString(byteBuffer);
        float float0 = byteBuffer.getFloat();
        float float1 = byteBuffer.getFloat();
        float float2 = byteBuffer.getFloat();
        float float3 = byteBuffer.getFloat();
        return null;
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.bFemale = input.get() == 1;
        this.wasZombie = input.get() == 1;
        if (WorldVersion >= 192) {
            this.objectID = input.getShort();
        } else {
            this.objectID = -1;
        }

        boolean boolean0 = input.get() == 1;
        if (WorldVersion >= 171) {
            this.m_persistentOutfitID = input.getInt();
        }

        if (boolean0 && WorldVersion < 171) {
            short short0 = input.getShort();
        }

        if (input.get() == 1) {
            this.desc = new SurvivorDesc(true);
            this.desc.load(input, WorldVersion, null);
        }

        if (WorldVersion >= 190) {
            byte byte0 = input.get();
            switch (byte0) {
                case 0:
                    this.baseVisual = new HumanVisual(this);
                    this.baseVisual.load(input, WorldVersion);
                    break;
                default:
                    throw new IOException("invalid visualType for corpse");
            }
        } else {
            this.baseVisual = new HumanVisual(this);
            this.baseVisual.load(input, WorldVersion);
        }

        if (input.get() == 1) {
            int int0 = input.getInt();

            try {
                this.setContainer(new ItemContainer());
                this.container.ID = int0;
                ArrayList arrayList = this.container.load(input, WorldVersion);
                byte byte1 = input.get();

                for (int int1 = 0; int1 < byte1; int1++) {
                    String string0 = GameWindow.ReadString(input);
                    short short1 = input.getShort();
                    if (short1 >= 0 && short1 < arrayList.size() && this.wornItems.getBodyLocationGroup().getLocation(string0) != null) {
                        this.wornItems.setItem(string0, (InventoryItem)arrayList.get(short1));
                    }
                }

                byte byte2 = input.get();

                for (int int2 = 0; int2 < byte2; int2++) {
                    String string1 = GameWindow.ReadString(input);
                    short short2 = input.getShort();
                    if (short2 >= 0 && short2 < arrayList.size() && this.attachedItems.getGroup().getLocation(string1) != null) {
                        this.attachedItems.setItem(string1, (InventoryItem)arrayList.get(short2));
                    }
                }
            } catch (Exception exception) {
                if (this.container != null) {
                    DebugLog.log("Failed to stream in container ID: " + this.container.ID);
                }
            }
        }

        this.deathTime = input.getFloat();
        this.reanimateTime = input.getFloat();
        this.fallOnFront = input.get() == 1;
        if (boolean0 && (GameClient.bClient || GameServer.bServer && ServerGUI.isCreated())) {
            this.checkClothing(null);
        }

        this.wasSkeleton = input.get() == 1;
        if (WorldVersion >= 159) {
            this.m_angle = input.getFloat();
        } else {
            this.m_angle = this.dir.toAngle();
        }

        if (WorldVersion >= 166) {
            this.m_zombieRotStageAtDeath = input.get() & 255;
        }

        if (WorldVersion >= 168) {
            this.bCrawling = input.get() == 1;
            this.bFakeDead = input.get() == 1;
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.put((byte)(this.bFemale ? 1 : 0));
        output.put((byte)(this.wasZombie ? 1 : 0));
        output.putShort(this.objectID);
        if (!GameServer.bServer && !GameClient.bClient) {
            output.put((byte)0);
        } else {
            output.put((byte)1);
        }

        output.putInt(this.m_persistentOutfitID);
        if (this.desc != null) {
            output.put((byte)1);
            this.desc.save(output);
        } else {
            output.put((byte)0);
        }

        if (this.baseVisual instanceof HumanVisual) {
            output.put((byte)0);
            this.baseVisual.save(output);
            if (this.container != null) {
                output.put((byte)1);
                output.putInt(this.container.ID);
                ArrayList arrayList = this.container.save(output);
                if (this.wornItems.size() > 127) {
                    throw new RuntimeException("too many worn items");
                }

                output.put((byte)this.wornItems.size());
                this.wornItems.forEach(wornItem -> {
                    GameWindow.WriteString(output, wornItem.getLocation());
                    output.putShort((short)arrayList.indexOf(wornItem.getItem()));
                });
                if (this.attachedItems == null) {
                    output.put((byte)0);
                } else {
                    if (this.attachedItems.size() > 127) {
                        throw new RuntimeException("too many attached items");
                    }

                    output.put((byte)this.attachedItems.size());
                    this.attachedItems.forEach(attachedItem -> {
                        GameWindow.WriteString(output, attachedItem.getLocation());
                        output.putShort((short)arrayList.indexOf(attachedItem.getItem()));
                    });
                }
            } else {
                output.put((byte)0);
            }

            output.putFloat(this.deathTime);
            output.putFloat(this.reanimateTime);
            output.put((byte)(this.fallOnFront ? 1 : 0));
            output.put((byte)(this.isSkeleton() ? 1 : 0));
            output.putFloat(this.m_angle);
            output.put((byte)this.m_zombieRotStageAtDeath);
            output.put((byte)(this.bCrawling ? 1 : 0));
            output.put((byte)(this.bFakeDead ? 1 : 0));
        } else {
            throw new IllegalStateException("unhandled baseVisual class");
        }
    }

    @Override
    public void softReset() {
        this.square.RemoveTileObject(this);
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        if ("becomeSkeleton".equals(change)) {
            bb.putInt(this.getHumanVisual().getSkinTextureIndex());
        } else if ("zombieRotStage".equals(change)) {
            bb.putInt(this.getHumanVisual().zombieRotStage);
        } else {
            super.saveChange(change, tbl, bb);
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("becomeSkeleton".equals(change)) {
            int int0 = bb.getInt();
            this.getHumanVisual().setBeardModel("");
            this.getHumanVisual().setHairModel("");
            this.getHumanVisual().setSkinTextureIndex(int0);
            this.wasSkeleton = true;
            this.getWornItems().clear();
            this.getAttachedItems().clear();
            this.getContainer().clear();
            this.atlasTex = null;
        } else if ("zombieRotStage".equals(change)) {
            this.getHumanVisual().zombieRotStage = bb.getInt();
            this.atlasTex = null;
        } else {
            super.loadChange(change, bb);
        }
    }

    @Override
    public void renderlast() {
        if (this.Speaking) {
            float float0 = this.sx;
            float float1 = this.sy;
            float0 -= IsoCamera.getOffX();
            float1 -= IsoCamera.getOffY();
            float0 += 8.0F;
            float1 += 32.0F;
            if (this.sayLine != null) {
                TextManager.instance
                    .DrawStringCentre(UIFont.Medium, float0, float1, this.sayLine, this.SpeakColor.r, this.SpeakColor.g, this.SpeakColor.b, this.SpeakColor.a);
            }
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        this.offsetX = 0.0F;
        this.offsetY = 0.0F;
        boolean boolean0 = this.isHighlighted();
        if (ModelManager.instance.bDebugEnableModels && ModelManager.instance.isCreated()) {
            if (this.atlasTex == null) {
                this.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
                DeadBodyAtlas.instance.render();
            }

            if (this.atlasTex != null) {
                if (IsoSprite.globalOffsetX == -1.0F) {
                    IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
                    IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
                }

                float float0 = IsoUtils.XToScreen(x, y, z, 0);
                float float1 = IsoUtils.YToScreen(x, y, z, 0);
                this.sx = float0;
                this.sy = float1;
                float0 = this.sx + IsoSprite.globalOffsetX;
                float1 = this.sy + IsoSprite.globalOffsetY;
                if (Core.TileScale == 1) {
                }

                if (boolean0) {
                    inf.r = this.getHighlightColor().r;
                    inf.g = this.getHighlightColor().g;
                    inf.b = this.getHighlightColor().b;
                    inf.a = this.getHighlightColor().a;
                } else {
                    inf.r = col.r;
                    inf.g = col.g;
                    inf.b = col.b;
                    inf.a = col.a;
                }

                col = inf;
                if (!boolean0 && PerformanceSettings.LightingFrameSkip < 3 && this.getCurrentSquare() != null) {
                    this.getCurrentSquare().interpolateLight(col, x - this.getCurrentSquare().getX(), y - this.getCurrentSquare().getY());
                }

                if (GameServer.bServer && ServerGUI.isCreated()) {
                    inf.set(1.0F, 1.0F, 1.0F, 1.0F);
                }

                this.atlasTex.render((int)float0, (int)float1, col.r, col.g, col.b, col.a);
                if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
                    LineDrawer.DrawIsoLine(x - 0.5F, y, z, x + 0.5F, y, z, 1.0F, 1.0F, 1.0F, 0.25F, 1);
                    LineDrawer.DrawIsoLine(x, y - 0.5F, z, x, y + 0.5F, z, 1.0F, 1.0F, 1.0F, 0.25F, 1);
                }

                this.sx = float0;
                this.sy = float1;
                if (IsoObjectPicker.Instance.wasDirty) {
                    this.renderObjectPicker(this.getX(), this.getY(), this.getZ(), col);
                }
            }
        }

        if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
            _rotation.setAngleAxis(this.m_angle + (Math.PI / 2), 0.0, 0.0, 1.0);
            _transform.setRotation(_rotation);
            _transform.origin.set(this.x, this.y, this.z);
            Vector3f vector3f0 = _tempVec3f_1;
            _transform.basis.getColumn(1, vector3f0);
            Vector3f vector3f1 = _tempVec3f_2;
            vector3f0.cross(_UNIT_Z, vector3f1);
            float float2 = 0.3F;
            float float3 = 0.9F;
            vector3f0.x *= float3;
            vector3f0.y *= float3;
            vector3f1.x *= float2;
            vector3f1.y *= float2;
            float float4 = x + vector3f0.x;
            float float5 = y + vector3f0.y;
            float float6 = x - vector3f0.x;
            float float7 = y - vector3f0.y;
            float float8 = float4 - vector3f1.x;
            float float9 = float4 + vector3f1.x;
            float float10 = float6 - vector3f1.x;
            float float11 = float6 + vector3f1.x;
            float float12 = float7 - vector3f1.y;
            float float13 = float7 + vector3f1.y;
            float float14 = float5 - vector3f1.y;
            float float15 = float5 + vector3f1.y;
            float float16 = 1.0F;
            float float17 = 1.0F;
            float float18 = 1.0F;
            if (this.isMouseOver(Mouse.getX(), Mouse.getY())) {
                float18 = 0.0F;
                float16 = 0.0F;
            }

            LineDrawer.addLine(float8, float14, 0.0F, float9, float15, 0.0F, float16, float17, float18, null, true);
            LineDrawer.addLine(float8, float14, 0.0F, float10, float12, 0.0F, float16, float17, float18, null, true);
            LineDrawer.addLine(float9, float15, 0.0F, float11, float13, 0.0F, float16, float17, float18, null, true);
            LineDrawer.addLine(float10, float12, 0.0F, float11, float13, 0.0F, float16, float17, float18, null, true);
        }

        if (this.isFakeDead() && DebugOptions.instance.ZombieRenderFakeDead.getValue()) {
            float float19 = IsoUtils.XToScreen(x, y, z, 0) + IsoSprite.globalOffsetX;
            float float20 = IsoUtils.YToScreen(x, y, z, 0) + IsoSprite.globalOffsetY - 16 * Core.TileScale;
            float float21 = this.getFakeDeadWakeupHours() - (float)GameTime.getInstance().getWorldAgeHours();
            float21 = Math.max(float21, 0.0F);
            TextManager.instance.DrawStringCentre(UIFont.Medium, float19, float20, String.format("FakeDead %.2f", float21), 1.0, 1.0, 1.0, 1.0);
        }

        if (Core.bDebug && DebugOptions.instance.MultiplayerShowZombieOwner.getValue()) {
            Color color = Colors.Yellow;
            float float22 = IsoUtils.XToScreenExact(x + 0.4F, y + 0.4F, z, 0);
            float float23 = IsoUtils.YToScreenExact(x + 0.4F, y - 1.4F, z, 0);
            TextManager.instance
                .DrawStringCentre(
                    UIFont.DebugConsole,
                    float22,
                    float23,
                    this.objectID + " / " + this.onlineID + " / " + (this.isFemale() ? "F" : "M"),
                    color.r,
                    color.g,
                    color.b,
                    color.a
                );
            TextManager.instance
                .DrawStringCentre(
                    UIFont.DebugConsole,
                    float22,
                    float23 + 10.0F,
                    String.format("x=%09.3f ", x) + String.format("y=%09.3f ", y) + String.format("z=%d", (byte)z),
                    color.r,
                    color.g,
                    color.b,
                    color.a
                );
        }
    }

    public void renderShadow() {
        _rotation.setAngleAxis(this.m_angle + (Math.PI / 2), 0.0, 0.0, 1.0);
        _transform.setRotation(_rotation);
        _transform.origin.set(this.x, this.y, this.z);
        Vector3f vector3f = _tempVec3f_1;
        _transform.basis.getColumn(1, vector3f);
        float float0 = 0.45F;
        float float1 = 1.4F;
        float float2 = 1.125F;
        int int0 = IsoCamera.frameState.playerIndex;
        ColorInfo colorInfo = this.square.lighting[int0].lightInfo();
        renderShadow(this.x, this.y, this.z, vector3f, float0, float1, float2, colorInfo, this.getAlpha(int0));
    }

    public static void renderShadow(float x, float y, float z, Vector3f forward, float w, float fm, float bm, ColorInfo lightInfo, float alpha) {
        float float0 = alpha * ((lightInfo.r + lightInfo.g + lightInfo.b) / 3.0F);
        float0 *= 0.66F;
        forward.normalize();
        Vector3f vector3f = _tempVec3f_2;
        forward.cross(_UNIT_Z, vector3f);
        w = Math.max(0.65F, w);
        fm = Math.max(fm, 0.65F);
        bm = Math.max(bm, 0.65F);
        vector3f.x *= w;
        vector3f.y *= w;
        float float1 = x + forward.x * fm;
        float float2 = y + forward.y * fm;
        float float3 = x - forward.x * bm;
        float float4 = y - forward.y * bm;
        float float5 = float1 - vector3f.x;
        float float6 = float1 + vector3f.x;
        float float7 = float3 - vector3f.x;
        float float8 = float3 + vector3f.x;
        float float9 = float4 - vector3f.y;
        float float10 = float4 + vector3f.y;
        float float11 = float2 - vector3f.y;
        float float12 = float2 + vector3f.y;
        float float13 = IsoUtils.XToScreenExact(float5, float11, z, 0);
        float float14 = IsoUtils.YToScreenExact(float5, float11, z, 0);
        float float15 = IsoUtils.XToScreenExact(float6, float12, z, 0);
        float float16 = IsoUtils.YToScreenExact(float6, float12, z, 0);
        float float17 = IsoUtils.XToScreenExact(float8, float10, z, 0);
        float float18 = IsoUtils.YToScreenExact(float8, float10, z, 0);
        float float19 = IsoUtils.XToScreenExact(float7, float9, z, 0);
        float float20 = IsoUtils.YToScreenExact(float7, float9, z, 0);
        if (DropShadow == null) {
            DropShadow = Texture.getSharedTexture("media/textures/NewShadow.png");
        }

        SpriteRenderer.instance.renderPoly(DropShadow, float13, float14, float15, float16, float17, float18, float19, float20, 0.0F, 0.0F, 0.0F, float0);
        if (DebugOptions.instance.IsoSprite.DropShadowEdges.getValue()) {
            LineDrawer.addLine(float5, float11, z, float6, float12, z, 1, 1, 1, null);
            LineDrawer.addLine(float6, float12, z, float8, float10, z, 1, 1, 1, null);
            LineDrawer.addLine(float8, float10, z, float7, float9, z, 1, 1, 1, null);
            LineDrawer.addLine(float7, float9, z, float5, float11, z, 1, 1, 1, null);
        }
    }

    @Override
    public void renderObjectPicker(float x, float y, float z, ColorInfo lightInfo) {
        if (this.atlasTex != null) {
            this.atlasTex.renderObjectPicker(this.sx, this.sy, lightInfo, this.square, this);
        }
    }

    public boolean isMouseOver(float screenX, float screenY) {
        _rotation.setAngleAxis(this.m_angle + (Math.PI / 2), 0.0, 0.0, 1.0);
        _transform.setRotation(_rotation);
        _transform.origin.set(this.x, this.y, this.z);
        _transform.inverse();
        Vector3f vector3f = _tempVec3f_1.set(IsoUtils.XToIso(screenX, screenY, this.z), IsoUtils.YToIso(screenX, screenY, this.z), this.z);
        _transform.transform(vector3f);
        return vector3f.x >= -0.3F && vector3f.y >= -0.9F && vector3f.x < 0.3F && vector3f.y < 0.9F;
    }

    public void Burn() {
        if (!GameClient.bClient) {
            if (this.getSquare() != null && this.getSquare().getProperties().Is(IsoFlagType.burning)) {
                this.burnTimer = this.burnTimer + GameTime.instance.getMultipliedSecondsSinceLastUpdate();
                if (this.burnTimer >= 10.0F) {
                    boolean boolean0 = true;

                    for (int int0 = 0; int0 < this.getSquare().getObjects().size(); int0++) {
                        IsoObject object0 = this.getSquare().getObjects().get(int0);
                        if (object0.getName() != null && "burnedCorpse".equals(object0.getName())) {
                            boolean0 = false;
                            break;
                        }
                    }

                    if (boolean0) {
                        IsoObject object1 = new IsoObject(this.getSquare(), "floors_burnt_01_" + Rand.Next(1, 3), "burnedCorpse");
                        this.getSquare().getObjects().add(object1);
                        object1.transmitCompleteItemToClients();
                    }

                    if (GameServer.bServer) {
                        GameServer.sendRemoveCorpseFromMap(this);
                    }

                    this.getSquare().removeCorpse(this, true);
                }
            }
        }
    }

    /**
     * 
     * @param container the container to set
     */
    @Override
    public void setContainer(ItemContainer container) {
        super.setContainer(container);
        container.type = this.bFemale ? "inventoryfemale" : "inventorymale";
        container.Capacity = 8;
        container.SourceGrid = this.square;
    }

    public void checkClothing(InventoryItem removedItem) {
        for (int int0 = 0; int0 < this.wornItems.size(); int0++) {
            InventoryItem item0 = this.wornItems.getItemByIndex(int0);
            if (this.container == null || this.container.getItems().indexOf(item0) == -1) {
                this.wornItems.remove(item0);
                this.atlasTex = null;
                int0--;
            }
        }

        if (removedItem == this.getPrimaryHandItem()) {
            this.setPrimaryHandItem(null);
            this.atlasTex = null;
        }

        if (removedItem == this.getSecondaryHandItem()) {
            this.setSecondaryHandItem(null);
            this.atlasTex = null;
        }

        for (int int1 = 0; int1 < this.attachedItems.size(); int1++) {
            InventoryItem item1 = this.attachedItems.getItemByIndex(int1);
            if (this.container == null || this.container.getItems().indexOf(item1) == -1) {
                this.attachedItems.remove(item1);
                this.atlasTex = null;
                int1--;
            }
        }
    }

    @Override
    public boolean IsSpeaking() {
        return this.Speaking;
    }

    @Override
    public void Say(String line) {
        this.SpeakTime = line.length() * 4;
        if (this.SpeakTime < 60.0F) {
            this.SpeakTime = 60.0F;
        }

        this.sayLine = line;
        this.Speaking = true;
    }

    @Override
    public String getSayLine() {
        return this.sayLine;
    }

    @Override
    public String getTalkerType() {
        return "Talker";
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        if (!GameServer.bServer) {
            FliesSound.instance.corpseAdded((int)this.getX(), (int)this.getY(), (int)this.getZ());
        }

        if (!GameClient.bClient && this.objectID == -1) {
            this.objectID = Bodies.allocateID();
        }

        Bodies.put(this.objectID, this);
        if (!GameClient.bClient) {
            if (this.reanimateTime > 0.0F) {
                this.getCell().addToStaticUpdaterObjectList(this);
                if (Core.bDebug) {
                    DebugLog.log("reanimate: addToWorld reanimateTime=" + this.reanimateTime + this);
                }
            }

            float float0 = (float)GameTime.getInstance().getWorldAgeHours();
            if (this.deathTime < 0.0F) {
                this.deathTime = float0;
            }

            if (this.deathTime > float0) {
                this.deathTime = float0;
            }
        }
    }

    @Override
    public void removeFromWorld() {
        if (!GameServer.bServer) {
            FliesSound.instance.corpseRemoved((int)this.getX(), (int)this.getY(), (int)this.getZ());
        }

        Bodies.remove(this.objectID);
        super.removeFromWorld();
    }

    public static void updateBodies() {
        if (!GameClient.bClient) {
            if (Core.bDebug) {
            }

            boolean boolean0 = false;
            float float0 = (float)SandboxOptions.instance.HoursForCorpseRemoval.getValue();
            if (!(float0 <= 0.0F)) {
                float float1 = float0 / 3.0F;
                float float2 = (float)GameTime.getInstance().getWorldAgeHours();
                tempBodies.clear();
                Bodies.getObjects(tempBodies);

                for (IsoDeadBody deadBody : tempBodies) {
                    if (deadBody.getHumanVisual() != null) {
                        if (deadBody.deathTime > float2) {
                            deadBody.deathTime = float2;
                            deadBody.getHumanVisual().zombieRotStage = deadBody.m_zombieRotStageAtDeath;
                        }

                        if (!deadBody.updateFakeDead() && (ServerOptions.instance.RemovePlayerCorpsesOnCorpseRemoval.getValue() || deadBody.wasZombie)) {
                            int int0 = deadBody.getHumanVisual().zombieRotStage;
                            deadBody.updateRotting(float2, float1, boolean0);
                            if (deadBody.isFakeDead()) {
                            }

                            int int1 = deadBody.getHumanVisual().zombieRotStage;
                            float float3 = float2 - deadBody.deathTime;
                            if (!(float3 < float0 + (deadBody.isSkeleton() ? float1 : 0.0F))) {
                                if (boolean0) {
                                    int int2 = (int)(float3 / float1);
                                    DebugLog.General.debugln("%s REMOVE %d -> %d age=%.2f stages=%d", deadBody, int0, int1, float3, int2);
                                }

                                if (GameServer.bServer) {
                                    GameServer.sendRemoveCorpseFromMap(deadBody);
                                }

                                deadBody.removeFromWorld();
                                deadBody.removeFromSquare();
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateRotting(float float1, float float2, boolean boolean0) {
        if (!this.isSkeleton()) {
            float float0 = float1 - this.deathTime;
            int int0 = (int)(float0 / float2);
            int int1 = this.m_zombieRotStageAtDeath + int0;
            if (int0 < 3) {
                int1 = PZMath.clamp(int1, 1, 3);
            }

            if (int1 <= 3 && int1 != this.getHumanVisual().zombieRotStage) {
                int int2 = int1 - this.getHumanVisual().zombieRotStage;
                if (boolean0) {
                    DebugLog.General.debugln("%s zombieRotStage %d -> %d age=%.2f stages=%d", this, this.getHumanVisual().zombieRotStage, int1, float0, int0);
                }

                this.getHumanVisual().zombieRotStage = int1;
                this.atlasTex = null;
                if (GameServer.bServer) {
                    this.sendObjectChange("zombieRotStage");
                }

                if (Rand.Next(100) == 0 && this.wasZombie && SandboxOptions.instance.Lore.DisableFakeDead.getValue() == 2) {
                    this.setFakeDead(true);
                    if (Rand.Next(5) == 0) {
                        this.setCrawling(true);
                    }
                }

                String string = ClimateManager.getInstance().getSeasonName();
                if (int2 >= 1 && string != "Winter") {
                    if (SandboxOptions.instance.MaggotSpawn.getValue() != 3) {
                        byte byte0 = 5;
                        if (string == "Summer") {
                            byte0 = 3;
                        }

                        for (int int3 = 0; int3 < int2; int3++) {
                            if (this.wasZombie) {
                                if (Rand.Next((int)byte0) == 0) {
                                    InventoryItem item0 = InventoryItemFactory.CreateItem("Maggots");
                                    if (item0 != null && this.getContainer() != null) {
                                        this.getContainer().addItem(item0);
                                        if (item0 instanceof Food) {
                                            ((Food)item0).setPoisonPower(5);
                                        }
                                    }
                                }

                                if (Rand.Next(byte0 * 2) == 0 && SandboxOptions.instance.MaggotSpawn.getValue() != 2) {
                                    InventoryItem item1 = InventoryItemFactory.CreateItem("Maggots");
                                    if (item1 != null && this.getSquare() != null) {
                                        this.getSquare().AddWorldInventoryItem(item1, (float)(Rand.Next(10) / 10), (float)(Rand.Next(10) / 10), 0.0F);
                                        if (item1 instanceof Food) {
                                            ((Food)item1).setPoisonPower(5);
                                        }
                                    }
                                }
                            } else {
                                if (Rand.Next((int)byte0) == 0) {
                                    InventoryItem item2 = InventoryItemFactory.CreateItem("Maggots");
                                    if (item2 != null && this.getContainer() != null) {
                                        this.getContainer().addItem(item2);
                                    }
                                }

                                if (Rand.Next(byte0 * 2) == 0 && SandboxOptions.instance.MaggotSpawn.getValue() != 2) {
                                    InventoryItem item3 = InventoryItemFactory.CreateItem("Maggots");
                                    if (item3 != null && this.getSquare() != null) {
                                        this.getSquare().AddWorldInventoryItem(item3, (float)(Rand.Next(10) / 10), (float)(Rand.Next(10) / 10), 0.0F);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (int0 == 3 && Rand.NextBool(7)) {
                    if (boolean0) {
                        DebugLog.General.debugln("%s zombieRotStage %d -> x age=%.2f stages=%d", this, this.getHumanVisual().zombieRotStage, float0, int0);
                    }

                    this.getHumanVisual().setBeardModel("");
                    this.getHumanVisual().setHairModel("");
                    this.getHumanVisual().setSkinTextureIndex(Rand.Next(1, 3));
                    this.wasSkeleton = true;
                    this.getWornItems().clear();
                    this.getAttachedItems().clear();
                    this.getContainer().clear();
                    this.atlasTex = null;
                    if (GameServer.bServer) {
                        this.sendObjectChange("becomeSkeleton");
                    }
                }
            }
        }
    }

    private boolean updateFakeDead() {
        if (!this.isFakeDead()) {
            return false;
        } else if (this.isSkeleton()) {
            return false;
        } else if (this.getFakeDeadWakeupHours() > GameTime.getInstance().getWorldAgeHours()) {
            return false;
        } else if (!this.isPlayerNearby()) {
            return false;
        } else if (SandboxOptions.instance.Lore.DisableFakeDead.getValue() == 3) {
            return false;
        } else {
            this.reanimateNow();
            return true;
        }
    }

    private float getFakeDeadWakeupHours() {
        return this.deathTime + 0.5F;
    }

    private boolean isPlayerNearby() {
        if (GameServer.bServer) {
            for (int int0 = 0; int0 < GameServer.Players.size(); int0++) {
                IsoPlayer player0 = GameServer.Players.get(int0);
                boolean boolean0 = this.square != null && ServerLOS.instance.isCouldSee(player0, this.square);
                if (this.isPlayerNearby(player0, boolean0)) {
                    return true;
                }
            }
        } else {
            IsoGridSquare square = this.getSquare();

            for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
                IsoPlayer player1 = IsoPlayer.players[int1];
                boolean boolean1 = square != null && square.isCanSee(int1);
                if (this.isPlayerNearby(player1, boolean1)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPlayerNearby(IsoPlayer playerx, boolean boolean0) {
        if (!boolean0) {
            return false;
        } else if (playerx == null || playerx.isDead()) {
            return false;
        } else if (playerx.isGhostMode() || playerx.isInvisible()) {
            return false;
        } else if (playerx.getVehicle() != null) {
            return false;
        } else {
            float float0 = playerx.DistToSquared(this);
            return !(float0 < 4.0F) && !(float0 > 16.0F);
        }
    }

    public float getReanimateTime() {
        return this.reanimateTime;
    }

    public void setReanimateTime(float hours) {
        this.reanimateTime = hours;
        if (!GameClient.bClient) {
            ArrayList arrayList = IsoWorld.instance.CurrentCell.getStaticUpdaterObjectList();
            if (this.reanimateTime > 0.0F && !arrayList.contains(this)) {
                arrayList.add(this);
            } else if (this.reanimateTime <= 0.0F && arrayList.contains(this)) {
                arrayList.remove(this);
            }
        }
    }

    private float getReanimateDelay() {
        float float0 = 0.0F;
        float float1 = 0.0F;

        float1 = switch (SandboxOptions.instance.Lore.Reanimate.getValue()) {
            case 2 -> 0.008333334F;
            case 3 -> 0.016666668F;
            case 4 -> 12.0F;
            case 5 -> {
                float0 = 48.0F;
                yield 72.0F;
            }
            case 6 -> {
                float0 = 168.0F;
                yield 336.0F;
            }
        };
        if (Core.bTutorial) {
            float1 = 0.25F;
        }

        return float0 == float1 ? float0 : Rand.Next(float0, float1);
    }

    public void reanimateLater() {
        this.setReanimateTime((float)GameTime.getInstance().getWorldAgeHours() + this.getReanimateDelay());
    }

    public void reanimateNow() {
        this.setReanimateTime((float)GameTime.getInstance().getWorldAgeHours());
    }

    @Override
    public void update() {
        if (this.current == null) {
            this.current = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
        }

        if (!GameClient.bClient) {
            if (this.reanimateTime > 0.0F) {
                float float0 = (float)GameTime.getInstance().getWorldAgeHours();
                if (this.reanimateTime <= float0) {
                    this.reanimate();
                }
            }
        }
    }

    public void reanimate() {
        short short0 = -1;
        if (GameServer.bServer) {
            short0 = ServerMap.instance.getUniqueZombieId();
            if (short0 == -1) {
                return;
            }
        }

        SurvivorDesc survivorDesc = new SurvivorDesc();
        survivorDesc.setFemale(this.isFemale());
        IsoZombie zombie0 = new IsoZombie(IsoWorld.instance.CurrentCell, survivorDesc, -1);
        zombie0.setPersistentOutfitID(this.m_persistentOutfitID);
        if (this.container == null) {
            this.container = new ItemContainer();
        }

        zombie0.setInventory(this.container);
        this.container = null;
        zombie0.getHumanVisual().copyFrom(this.getHumanVisual());
        zombie0.getWornItems().copyFrom(this.wornItems);
        this.wornItems.clear();
        zombie0.getAttachedItems().copyFrom(this.attachedItems);
        this.attachedItems.clear();
        zombie0.setX(this.getX());
        zombie0.setY(this.getY());
        zombie0.setZ(this.getZ());
        zombie0.setCurrent(this.getCurrentSquare());
        zombie0.setMovingSquareNow();
        zombie0.setDir(this.dir);
        LuaManager.copyTable(zombie0.getModData(), this.getModData());
        zombie0.getAnimationPlayer().setTargetAngle(this.m_angle);
        zombie0.getAnimationPlayer().setAngleToTarget();
        zombie0.setForwardDirection(Vector2.fromLengthDirection(1.0F, this.m_angle));
        zombie0.setAlphaAndTarget(1.0F);
        Arrays.fill(zombie0.IsVisibleToPlayer, true);
        zombie0.setOnFloor(true);
        zombie0.setCrawler(this.bCrawling);
        zombie0.setCanWalk(!this.bCrawling);
        zombie0.walkVariant = "ZombieWalk";
        zombie0.DoZombieStats();
        zombie0.setFallOnFront(this.isFallOnFront());
        if (SandboxOptions.instance.Lore.Toughness.getValue() == 1) {
            zombie0.setHealth(3.5F + Rand.Next(0.0F, 0.3F));
        }

        if (SandboxOptions.instance.Lore.Toughness.getValue() == 2) {
            zombie0.setHealth(1.8F + Rand.Next(0.0F, 0.3F));
        }

        if (SandboxOptions.instance.Lore.Toughness.getValue() == 3) {
            zombie0.setHealth(0.5F + Rand.Next(0.0F, 0.3F));
        }

        if (GameServer.bServer) {
            zombie0.OnlineID = short0;
            ServerMap.instance.ZombieMap.put(zombie0.OnlineID, zombie0);
        }

        if (this.isFakeDead()) {
            zombie0.setWasFakeDead(true);
        } else {
            zombie0.setReanimatedPlayer(true);
            zombie0.getDescriptor().setID(0);
            SharedDescriptors.createPlayerZombieDescriptor(zombie0);
        }

        zombie0.setReanimate(this.bCrawling);
        if (!IsoWorld.instance.CurrentCell.getZombieList().contains(zombie0)) {
            IsoWorld.instance.CurrentCell.getZombieList().add(zombie0);
        }

        if (!IsoWorld.instance.CurrentCell.getObjectList().contains(zombie0) && !IsoWorld.instance.CurrentCell.getAddList().contains(zombie0)) {
            IsoWorld.instance.CurrentCell.getAddList().add(zombie0);
        }

        if (GameServer.bServer) {
            if (this.player != null) {
                this.player.ReanimatedCorpse = zombie0;
                this.player.ReanimatedCorpseID = zombie0.OnlineID;
            }

            zombie0.networkAI.reanimatedBodyID = this.objectID;
        }

        if (GameServer.bServer) {
            GameServer.sendRemoveCorpseFromMap(this);
        }

        this.removeFromWorld();
        this.removeFromSquare();
        LuaEventManager.triggerEvent("OnContainerUpdate");
        zombie0.setReanimateTimer(0.0F);
        zombie0.onWornItemsChanged();
        if (this.player != null) {
            if (GameServer.bServer) {
                GameServer.sendReanimatedZombieID(this.player, zombie0);
            } else if (!GameClient.bClient && this.player.isLocalPlayer()) {
                this.player.ReanimatedCorpse = zombie0;
            }

            this.player.setLeaveBodyTimedown(3601.0F);
        }

        zombie0.actionContext.update();
        float float0 = GameTime.getInstance().FPSMultiplier;
        GameTime.getInstance().FPSMultiplier = 100.0F;

        try {
            zombie0.advancedAnimator.update();
        } finally {
            GameTime.getInstance().FPSMultiplier = float0;
        }

        if (this.isFakeDead() && SoundManager.instance.isListenerInRange(this.x, this.y, 20.0F) && !GameServer.bServer) {
            zombie0.parameterZombieState.setState(ParameterZombieState.State.Reanimate);
        }

        if (Core.bDebug) {
            DebugLog.Multiplayer
                .debugln(
                    "Reanimate: corpse=%d/%d zombie=%d delay=%f",
                    this.getObjectID(),
                    this.getOnlineID(),
                    zombie0.getOnlineID(),
                    GameTime.getInstance().getWorldAgeHours() - this.reanimateTime
                );
        }
    }

    public static void Reset() {
        Bodies.clear();
    }

    @Override
    public void Collision(Vector2 collision, IsoObject object) {
        if (object instanceof BaseVehicle vehicle) {
            float float0 = 15.0F;
            Vector3f vector3f0 = BaseVehicle.TL_vector3f_pool.get().alloc();
            Vector3f vector3f1 = BaseVehicle.TL_vector3f_pool.get().alloc();
            vehicle.getLinearVelocity(vector3f0);
            vector3f0.y = 0.0F;
            vector3f1.set(vehicle.x - this.x, 0.0F, vehicle.z - this.z);
            vector3f1.normalize();
            vector3f0.mul(vector3f1);
            BaseVehicle.TL_vector3f_pool.get().release(vector3f1);
            float float1 = vector3f0.length();
            BaseVehicle.TL_vector3f_pool.get().release(vector3f0);
            float1 = Math.min(float1, float0);
            if (float1 < 0.05F) {
                return;
            }

            if (Math.abs(vehicle.getCurrentSpeedKmHour()) > 20.0F) {
                vehicle.doChrHitImpulse(this);
            }
        }
    }

    public boolean isFallOnFront() {
        return this.fallOnFront;
    }

    public void setFallOnFront(boolean _fallOnFront) {
        this.fallOnFront = _fallOnFront;
    }

    public InventoryItem getPrimaryHandItem() {
        return this.primaryHandItem;
    }

    public void setPrimaryHandItem(InventoryItem item) {
        this.primaryHandItem = item;
        this.updateContainerWithHandItems();
    }

    private void updateContainerWithHandItems() {
        if (this.getContainer() != null) {
            if (this.getPrimaryHandItem() != null) {
                this.getContainer().AddItem(this.getPrimaryHandItem());
            }

            if (this.getSecondaryHandItem() != null) {
                this.getContainer().AddItem(this.getSecondaryHandItem());
            }
        }
    }

    public InventoryItem getSecondaryHandItem() {
        return this.secondaryHandItem;
    }

    public void setSecondaryHandItem(InventoryItem item) {
        this.secondaryHandItem = item;
        this.updateContainerWithHandItems();
    }

    public float getAngle() {
        return this.m_angle;
    }

    public String getOutfitName() {
        return this.getHumanVisual().getOutfit() != null ? this.getHumanVisual().getOutfit().m_Name : null;
    }

    private String getDescription() {
        return String.format(
            "object-id=%d online-id=%d bFakeDead=%b bCrawling=%b isFallOnFront=%b (x=%f,y=%f,z=%f;a=%f) outfit=%d",
            this.objectID,
            this.onlineID,
            this.bFakeDead,
            this.bCrawling,
            this.fallOnFront,
            this.x,
            this.y,
            this.z,
            this.m_angle,
            this.m_persistentOutfitID
        );
    }

    public String readInventory(ByteBuffer b) {
        String string0 = GameWindow.ReadString(b);
        if (this.getContainer() != null && this.getWornItems() != null && this.getAttachedItems() != null) {
            this.getContainer().clear();
            this.getWornItems().clear();
            this.getAttachedItems().clear();
            boolean boolean0 = b.get() == 1;
            if (boolean0) {
                try {
                    ArrayList arrayList = this.getContainer().load(b, IsoWorld.getWorldVersion());
                    this.getContainer().Capacity = 8;
                    byte byte0 = b.get();

                    for (int int0 = 0; int0 < byte0; int0++) {
                        String string1 = GameWindow.ReadStringUTF(b);
                        short short0 = b.getShort();
                        if (short0 >= 0 && short0 < arrayList.size() && this.getWornItems().getBodyLocationGroup().getLocation(string1) != null) {
                            this.getWornItems().setItem(string1, (InventoryItem)arrayList.get(short0));
                        }
                    }

                    byte byte1 = b.get();

                    for (int int1 = 0; int1 < byte1; int1++) {
                        String string2 = GameWindow.ReadStringUTF(b);
                        short short1 = b.getShort();
                        if (short1 >= 0 && short1 < arrayList.size() && this.getAttachedItems().getGroup().getLocation(string2) != null) {
                            this.getAttachedItems().setItem(string2, (InventoryItem)arrayList.get(short1));
                        }
                    }
                } catch (IOException iOException) {
                    DebugLog.Multiplayer.printException(iOException, "ReadDeadBodyInventory error for dead body " + this.getOnlineID(), LogSeverity.Error);
                }
            }

            return string0;
        } else {
            return string0;
        }
    }

    public short getObjectID() {
        return this.objectID;
    }

    public void setObjectID(short _objectID) {
        this.objectID = _objectID;
    }

    public short getOnlineID() {
        return this.onlineID;
    }

    public void setOnlineID(short _onlineID) {
        this.onlineID = _onlineID;
    }

    public boolean isPlayer() {
        return this.player != null;
    }

    public static IsoDeadBody getDeadBody(short id) {
        return Bodies.get(id);
    }

    public static void addDeadBodyID(short id, IsoDeadBody deadBody) {
        Bodies.put(id, deadBody);
    }

    public static void removeDeadBody(short id) {
        IsoDeadBody deadBody = Bodies.get(id);
        if (deadBody != null) {
            Bodies.remove(id);
            if (deadBody.getSquare() != null) {
                deadBody.getSquare().removeCorpse(deadBody, true);
            }
        }
    }
}
