// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.AttachedItems.AttachedModelName;
import zombie.characters.AttachedItems.AttachedModelNames;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoMannequin;
import zombie.popman.ObjectPool;
import zombie.util.StringUtils;
import zombie.vehicles.UI3DScene;

public final class DeadBodyAtlas {
    public static final int ATLAS_SIZE = 1024;
    private TextureFBO fbo;
    public static final DeadBodyAtlas instance = new DeadBodyAtlas();
    private static final Vector2 tempVector2 = new Vector2();
    private final HashMap<String, DeadBodyAtlas.BodyTexture> EntryMap = new HashMap<>();
    private final ArrayList<DeadBodyAtlas.Atlas> AtlasList = new ArrayList<>();
    private final DeadBodyAtlas.BodyParams bodyParams = new DeadBodyAtlas.BodyParams();
    private int updateCounter = -1;
    private final DeadBodyAtlas.Checksummer checksummer = new DeadBodyAtlas.Checksummer();
    private static final Stack<DeadBodyAtlas.RenderJob> JobPool = new Stack<>();
    private final DeadBodyAtlas.DebugDrawInWorld[] debugDrawInWorld = new DeadBodyAtlas.DebugDrawInWorld[3];
    private long debugDrawTime;
    private final ArrayList<DeadBodyAtlas.RenderJob> RenderJobs = new ArrayList<>();
    private final DeadBodyAtlas.CharacterTextureVisual characterTextureVisualFemale = new DeadBodyAtlas.CharacterTextureVisual(true);
    private final DeadBodyAtlas.CharacterTextureVisual characterTextureVisualMale = new DeadBodyAtlas.CharacterTextureVisual(false);
    private final CharacterTextures characterTexturesFemale = new CharacterTextures();
    private final CharacterTextures characterTexturesMale = new CharacterTextures();
    private final ObjectPool<DeadBodyAtlas.BodyTextureDrawer> bodyTextureDrawerPool = new ObjectPool<>(DeadBodyAtlas.BodyTextureDrawer::new);

    public void lightingUpdate(int _updateCounter, boolean lightsChanged) {
        if (_updateCounter != this.updateCounter && lightsChanged) {
            this.updateCounter = _updateCounter;
        }
    }

    public DeadBodyAtlas.BodyTexture getBodyTexture(IsoDeadBody body) {
        this.bodyParams.init(body);
        return this.getBodyTexture(this.bodyParams);
    }

    public DeadBodyAtlas.BodyTexture getBodyTexture(IsoZombie body) {
        this.bodyParams.init(body);
        return this.getBodyTexture(this.bodyParams);
    }

    public DeadBodyAtlas.BodyTexture getBodyTexture(IsoMannequin body) {
        this.bodyParams.init(body);
        return this.getBodyTexture(this.bodyParams);
    }

    public DeadBodyAtlas.BodyTexture getBodyTexture(boolean bFemale, String animSet, String stateName, IsoDirections dir, int frame, float trackTime) {
        CharacterTextures characterTextures = bFemale ? this.characterTexturesFemale : this.characterTexturesMale;
        DeadBodyAtlas.BodyTexture bodyTexture0 = characterTextures.getTexture(animSet, stateName, dir, frame);
        if (bodyTexture0 != null) {
            return bodyTexture0;
        } else {
            this.bodyParams.init(bFemale ? this.characterTextureVisualFemale : this.characterTextureVisualMale, dir, animSet, stateName, trackTime);
            this.bodyParams.variables.put("zombieWalkType", "1");
            DeadBodyAtlas.BodyTexture bodyTexture1 = this.getBodyTexture(this.bodyParams);
            characterTextures.addTexture(animSet, stateName, dir, frame, bodyTexture1);
            return bodyTexture1;
        }
    }

    public DeadBodyAtlas.BodyTexture getBodyTexture(DeadBodyAtlas.BodyParams body) {
        String string = this.getBodyKey(body);
        DeadBodyAtlas.BodyTexture bodyTexture = this.EntryMap.get(string);
        if (bodyTexture != null) {
            return bodyTexture;
        } else {
            DeadBodyAtlas.AtlasEntry atlasEntry = new DeadBodyAtlas.AtlasEntry();
            atlasEntry.key = string;
            atlasEntry.lightKey = this.getLightKey(body);
            atlasEntry.updateCounter = this.updateCounter;
            bodyTexture = new DeadBodyAtlas.BodyTexture();
            bodyTexture.entry = atlasEntry;
            this.EntryMap.put(string, bodyTexture);
            this.RenderJobs.add(DeadBodyAtlas.RenderJob.getNew().init(body, atlasEntry));
            return bodyTexture;
        }
    }

    public void checkLights(Texture entryTex, IsoDeadBody body) {
        if (entryTex != null) {
            DeadBodyAtlas.BodyTexture bodyTexture = this.EntryMap.get(entryTex.getName());
            if (bodyTexture != null) {
                DeadBodyAtlas.AtlasEntry atlasEntry = bodyTexture.entry;
                if (atlasEntry != null && atlasEntry.tex == entryTex) {
                    if (atlasEntry.updateCounter != this.updateCounter) {
                        atlasEntry.updateCounter = this.updateCounter;
                        this.bodyParams.init(body);
                        String string = this.getLightKey(this.bodyParams);
                        if (!atlasEntry.lightKey.equals(string)) {
                            this.EntryMap.remove(atlasEntry.key);
                            atlasEntry.key = this.getBodyKey(this.bodyParams);
                            atlasEntry.lightKey = string;
                            entryTex.setNameOnly(atlasEntry.key);
                            this.EntryMap.put(atlasEntry.key, bodyTexture);
                            DeadBodyAtlas.RenderJob renderJob = DeadBodyAtlas.RenderJob.getNew().init(this.bodyParams, atlasEntry);
                            renderJob.bClearThisSlotOnly = true;
                            this.RenderJobs.add(renderJob);
                            this.render();
                        }
                    }
                }
            }
        }
    }

    public void checkLights(Texture entryTex, IsoZombie body) {
        if (entryTex != null) {
            DeadBodyAtlas.BodyTexture bodyTexture = this.EntryMap.get(entryTex.getName());
            if (bodyTexture != null) {
                DeadBodyAtlas.AtlasEntry atlasEntry = bodyTexture.entry;
                if (atlasEntry != null && atlasEntry.tex == entryTex) {
                    if (atlasEntry.updateCounter != this.updateCounter) {
                        atlasEntry.updateCounter = this.updateCounter;
                        this.bodyParams.init(body);
                        String string = this.getLightKey(this.bodyParams);
                        if (!atlasEntry.lightKey.equals(string)) {
                            this.EntryMap.remove(atlasEntry.key);
                            atlasEntry.key = this.getBodyKey(this.bodyParams);
                            atlasEntry.lightKey = string;
                            entryTex.setNameOnly(atlasEntry.key);
                            this.EntryMap.put(atlasEntry.key, bodyTexture);
                            DeadBodyAtlas.RenderJob renderJob = DeadBodyAtlas.RenderJob.getNew().init(this.bodyParams, atlasEntry);
                            renderJob.bClearThisSlotOnly = true;
                            this.RenderJobs.add(renderJob);
                            this.render();
                        }
                    }
                }
            }
        }
    }

    private void assignEntryToAtlas(DeadBodyAtlas.AtlasEntry atlasEntry, int int2, int int1) {
        if (atlasEntry.atlas == null) {
            for (int int0 = 0; int0 < this.AtlasList.size(); int0++) {
                DeadBodyAtlas.Atlas atlas0 = this.AtlasList.get(int0);
                if (!atlas0.isFull() && atlas0.ENTRY_WID == int2 && atlas0.ENTRY_HGT == int1) {
                    atlas0.addEntry(atlasEntry);
                    return;
                }
            }

            DeadBodyAtlas.Atlas atlas1 = new DeadBodyAtlas.Atlas(1024, 1024, int2, int1);
            atlas1.addEntry(atlasEntry);
            this.AtlasList.add(atlas1);
        }
    }

    private String getBodyKey(DeadBodyAtlas.BodyParams bodyParamsx) {
        if (bodyParamsx.humanVisual == this.characterTextureVisualFemale.humanVisual) {
            return "SZF_" + bodyParamsx.animSetName + "_" + bodyParamsx.stateName + "_" + bodyParamsx.dir + "_" + bodyParamsx.trackTime;
        } else if (bodyParamsx.humanVisual == this.characterTextureVisualMale.humanVisual) {
            return "SZM_" + bodyParamsx.animSetName + "_" + bodyParamsx.stateName + "_" + bodyParamsx.dir + "_" + bodyParamsx.trackTime;
        } else {
            try {
                this.checksummer.reset();
                HumanVisual humanVisual = bodyParamsx.humanVisual;
                this.checksummer.update((byte)bodyParamsx.dir.index());
                this.checksummer.update((int)(PZMath.wrap(bodyParamsx.angle, 0.0F, (float) (Math.PI * 2)) * (180.0F / (float)Math.PI)));
                this.checksummer.update(humanVisual.getHairModel());
                this.checksummer.update(humanVisual.getBeardModel());
                this.checksummer.update(humanVisual.getSkinColor());
                this.checksummer.update(humanVisual.getSkinTexture());
                this.checksummer.update((int)(humanVisual.getTotalBlood() * 100.0F));
                this.checksummer.update(bodyParamsx.primaryHandItem);
                this.checksummer.update(bodyParamsx.secondaryHandItem);

                for (int int0 = 0; int0 < bodyParamsx.attachedModelNames.size(); int0++) {
                    AttachedModelName attachedModelName = bodyParamsx.attachedModelNames.get(int0);
                    this.checksummer.update(attachedModelName.attachmentNameSelf);
                    this.checksummer.update(attachedModelName.attachmentNameParent);
                    this.checksummer.update(attachedModelName.modelName);
                    this.checksummer.update((int)(attachedModelName.bloodLevel * 100.0F));
                }

                this.checksummer.update(bodyParamsx.bFemale);
                this.checksummer.update(bodyParamsx.bZombie);
                this.checksummer.update(bodyParamsx.bSkeleton);
                this.checksummer.update(bodyParamsx.animSetName);
                this.checksummer.update(bodyParamsx.stateName);
                ItemVisuals itemVisuals = bodyParamsx.itemVisuals;

                for (int int1 = 0; int1 < itemVisuals.size(); int1++) {
                    ItemVisual itemVisual = itemVisuals.get(int1);
                    ClothingItem clothingItem = itemVisual.getClothingItem();
                    if (clothingItem != null) {
                        this.checksummer.update(itemVisual.getBaseTexture(clothingItem));
                        this.checksummer.update(itemVisual.getTextureChoice(clothingItem));
                        this.checksummer.update(itemVisual.getTint(clothingItem));
                        this.checksummer.update(clothingItem.getModel(humanVisual.isFemale()));
                        this.checksummer.update((int)(itemVisual.getTotalBlood() * 100.0F));
                    }
                }

                this.checksummer.update(bodyParamsx.fallOnFront);
                this.checksummer.update(bodyParamsx.bStanding);
                this.checksummer.update(bodyParamsx.bOutside);
                this.checksummer.update(bodyParamsx.bRoom);
                float float0 = (int)(bodyParamsx.ambient.r * 10.0F) / 10.0F;
                this.checksummer.update((byte)(float0 * 255.0F));
                float float1 = (int)(bodyParamsx.ambient.g * 10.0F) / 10.0F;
                this.checksummer.update((byte)(float1 * 255.0F));
                float float2 = (int)(bodyParamsx.ambient.b * 10.0F) / 10.0F;
                this.checksummer.update((byte)(float2 * 255.0F));
                this.checksummer.update((int)bodyParamsx.trackTime);

                for (int int2 = 0; int2 < bodyParamsx.lights.length; int2++) {
                    this.checksummer.update(bodyParamsx.lights[int2], bodyParamsx.x, bodyParamsx.y, bodyParamsx.z);
                }

                return this.checksummer.checksumToString();
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
                return "bogus";
            }
        }
    }

    private String getLightKey(DeadBodyAtlas.BodyParams bodyParamsx) {
        try {
            this.checksummer.reset();
            this.checksummer.update(bodyParamsx.bOutside);
            this.checksummer.update(bodyParamsx.bRoom);
            float float0 = (int)(bodyParamsx.ambient.r * 10.0F) / 10.0F;
            this.checksummer.update((byte)(float0 * 255.0F));
            float float1 = (int)(bodyParamsx.ambient.g * 10.0F) / 10.0F;
            this.checksummer.update((byte)(float1 * 255.0F));
            float float2 = (int)(bodyParamsx.ambient.b * 10.0F) / 10.0F;
            this.checksummer.update((byte)(float2 * 255.0F));

            for (int int0 = 0; int0 < bodyParamsx.lights.length; int0++) {
                this.checksummer.update(bodyParamsx.lights[int0], bodyParamsx.x, bodyParamsx.y, bodyParamsx.z);
            }

            return this.checksummer.checksumToString();
        } catch (Throwable throwable) {
            ExceptionLogger.logException(throwable);
            return "bogus";
        }
    }

    public void render() {
        for (int int0 = 0; int0 < this.AtlasList.size(); int0++) {
            DeadBodyAtlas.Atlas atlas = this.AtlasList.get(int0);
            if (atlas.clear) {
                SpriteRenderer.instance.drawGeneric(new DeadBodyAtlas.ClearAtlasTexture(atlas));
            }
        }

        if (!this.RenderJobs.isEmpty()) {
            for (int int1 = 0; int1 < this.RenderJobs.size(); int1++) {
                DeadBodyAtlas.RenderJob renderJob = this.RenderJobs.get(int1);
                if (renderJob.done != 1 || renderJob.renderRefCount <= 0) {
                    if (renderJob.done == 1 && renderJob.renderRefCount == 0) {
                        this.RenderJobs.remove(int1--);

                        assert !JobPool.contains(renderJob);

                        JobPool.push(renderJob);
                    } else if (renderJob.renderMain()) {
                        renderJob.renderRefCount++;
                        SpriteRenderer.instance.drawGeneric(renderJob);
                    }
                }
            }
        }
    }

    public void renderDebug() {
        if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
            if (JobPool.isEmpty()) {
                return;
            }

            if (JobPool.get(JobPool.size() - 1).entry.atlas == null) {
                return;
            }

            if (this.debugDrawInWorld[0] == null) {
                for (int int0 = 0; int0 < this.debugDrawInWorld.length; int0++) {
                    this.debugDrawInWorld[int0] = new DeadBodyAtlas.DebugDrawInWorld();
                }
            }

            int int1 = SpriteRenderer.instance.getMainStateIndex();
            long long0 = System.currentTimeMillis();
            if (long0 - this.debugDrawTime < 500L) {
                DeadBodyAtlas.RenderJob renderJob0 = JobPool.pop();
                renderJob0.done = 0;
                renderJob0.bClearThisSlotOnly = true;
                this.RenderJobs.add(renderJob0);
            } else if (long0 - this.debugDrawTime < 1000L) {
                DeadBodyAtlas.RenderJob renderJob1 = JobPool.pop();
                renderJob1.done = 0;
                renderJob1.renderMain();
                this.debugDrawInWorld[int1].init(renderJob1);
                SpriteRenderer.instance.drawGeneric(this.debugDrawInWorld[int1]);
            } else {
                this.debugDrawTime = long0;
            }
        }
    }

    public void renderUI() {
        if (Core.bDebug && DebugOptions.instance.DeadBodyAtlasRender.getValue()) {
            int int0 = 512 / Core.TileScale;
            int int1 = 0;
            int int2 = 0;

            for (int int3 = 0; int3 < this.AtlasList.size(); int3++) {
                DeadBodyAtlas.Atlas atlas = this.AtlasList.get(int3);
                SpriteRenderer.instance.renderi(null, int1, int2, int0, int0, 1.0F, 1.0F, 1.0F, 0.75F, null);
                SpriteRenderer.instance.renderi(atlas.tex, int1, int2, int0, int0, 1.0F, 1.0F, 1.0F, 1.0F, null);
                float float0 = (float)int0 / atlas.tex.getWidth();

                for (int int4 = 0; int4 <= atlas.tex.getWidth() / atlas.ENTRY_WID; int4++) {
                    SpriteRenderer.instance
                        .renderline(
                            null,
                            (int)(int1 + int4 * atlas.ENTRY_WID * float0),
                            int2,
                            (int)(int1 + int4 * atlas.ENTRY_WID * float0),
                            int2 + int0,
                            0.5F,
                            0.5F,
                            0.5F,
                            1.0F
                        );
                }

                for (int int5 = 0; int5 <= atlas.tex.getHeight() / atlas.ENTRY_HGT; int5++) {
                    SpriteRenderer.instance
                        .renderline(
                            null,
                            int1,
                            (int)(int2 + int0 - int5 * atlas.ENTRY_HGT * float0),
                            int1 + int0,
                            (int)(int2 + int0 - int5 * atlas.ENTRY_HGT * float0),
                            0.5F,
                            0.5F,
                            0.5F,
                            1.0F
                        );
                }

                int2 += int0;
                if (int2 + int0 > Core.getInstance().getScreenHeight()) {
                    int2 = 0;
                    int1 += int0;
                }
            }

            SpriteRenderer.instance.renderi(null, int1, int2, int0, int0, 1.0F, 1.0F, 1.0F, 0.5F, null);
            SpriteRenderer.instance.renderi((Texture)ModelManager.instance.bitmap.getTexture(), int1, int2, int0, int0, 1.0F, 1.0F, 1.0F, 1.0F, null);
        }
    }

    public void Reset() {
        if (this.fbo != null) {
            this.fbo.destroyLeaveTexture();
            this.fbo = null;
        }

        this.AtlasList.forEach(DeadBodyAtlas.Atlas::Reset);
        this.AtlasList.clear();
        this.EntryMap.clear();
        this.characterTexturesFemale.clear();
        this.characterTexturesMale.clear();
        JobPool.forEach(DeadBodyAtlas.RenderJob::Reset);
        JobPool.clear();
        this.RenderJobs.clear();
    }

    private void toBodyAtlas(DeadBodyAtlas.RenderJob renderJob) {
        GL11.glPushAttrib(2048);
        if (this.fbo.getTexture() != renderJob.entry.atlas.tex) {
            this.fbo.setTexture(renderJob.entry.atlas.tex);
        }

        this.fbo.startDrawing();
        GL11.glViewport(0, 0, this.fbo.getWidth(), this.fbo.getHeight());
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        int int0 = renderJob.entry.atlas.tex.getWidth();
        int int1 = renderJob.entry.atlas.tex.getHeight();
        GLU.gluOrtho2D(0.0F, int0, int1, 0.0F);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glEnable(3553);
        GL11.glDisable(3089);
        if (renderJob.entry.atlas.clear) {
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16640);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
            renderJob.entry.atlas.clear = false;
        }

        if (renderJob.bClearThisSlotOnly) {
            GL11.glEnable(3089);
            GL11.glScissor(renderJob.entry.x, 1024 - renderJob.entry.y - renderJob.entry.h, renderJob.entry.w, renderJob.entry.h);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16640);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
            int int2 = SpriteRenderer.instance.getRenderingPlayerIndex();
            int int3 = int2 != 0 && int2 != 2 ? Core.getInstance().getOffscreenTrueWidth() / 2 : 0;
            int int4 = int2 != 0 && int2 != 1 ? Core.getInstance().getOffscreenTrueHeight() / 2 : 0;
            int int5 = Core.getInstance().getOffscreenTrueWidth();
            int int6 = Core.getInstance().getOffscreenTrueHeight();
            if (IsoPlayer.numPlayers > 1) {
                int5 /= 2;
            }

            if (IsoPlayer.numPlayers > 2) {
                int6 /= 2;
            }

            GL11.glScissor(int3, int4, int5, int6);
            GL11.glDisable(3089);
        }

        int int7 = ModelManager.instance.bitmap.getTexture().getWidth() / 8 * Core.TileScale;
        int int8 = ModelManager.instance.bitmap.getTexture().getHeight() / 8 * Core.TileScale;
        int int9 = renderJob.entry.x - (int7 - renderJob.entry.atlas.ENTRY_WID) / 2;
        int int10 = renderJob.entry.y - (int8 - renderJob.entry.atlas.ENTRY_HGT) / 2;
        ModelManager.instance.bitmap.getTexture().bind();
        GL11.glBegin(7);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2i(int9, int10);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2i(int9 + int7, int10);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2i(int9 + int7, int10 + int8);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2i(int9, int10 + int8);
        GL11.glEnd();
        Texture.lastTextureID = 0;
        GL11.glBindTexture(3553, 0);
        this.fbo.endDrawing();
        GL11.glEnable(3089);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        renderJob.entry.ready = true;
        renderJob.done = 1;
    }

    private final class Atlas {
        public final int ENTRY_WID;
        public final int ENTRY_HGT;
        public Texture tex;
        public final ArrayList<DeadBodyAtlas.AtlasEntry> EntryList = new ArrayList<>();
        public boolean clear = true;

        public Atlas(int arg1, int arg2, int arg3, int arg4) {
            this.ENTRY_WID = arg3;
            this.ENTRY_HGT = arg4;
            this.tex = new Texture(arg1, arg2, 16);
            if (DeadBodyAtlas.this.fbo == null) {
                DeadBodyAtlas.this.fbo = new TextureFBO(this.tex, false);
            }
        }

        public boolean isFull() {
            int int0 = this.tex.getWidth() / this.ENTRY_WID;
            int int1 = this.tex.getHeight() / this.ENTRY_HGT;
            return this.EntryList.size() >= int0 * int1;
        }

        public DeadBodyAtlas.AtlasEntry addBody(String arg0) {
            int int0 = this.tex.getWidth() / this.ENTRY_WID;
            int int1 = this.EntryList.size();
            int int2 = int1 % int0;
            int int3 = int1 / int0;
            DeadBodyAtlas.AtlasEntry atlasEntry = new DeadBodyAtlas.AtlasEntry();
            atlasEntry.atlas = this;
            atlasEntry.key = arg0;
            atlasEntry.x = int2 * this.ENTRY_WID;
            atlasEntry.y = int3 * this.ENTRY_HGT;
            atlasEntry.w = this.ENTRY_WID;
            atlasEntry.h = this.ENTRY_HGT;
            atlasEntry.tex = this.tex.split(arg0, atlasEntry.x, this.tex.getHeight() - (atlasEntry.y + this.ENTRY_HGT), atlasEntry.w, atlasEntry.h);
            atlasEntry.tex.setName(arg0);
            this.EntryList.add(atlasEntry);
            return atlasEntry;
        }

        public void addEntry(DeadBodyAtlas.AtlasEntry arg0) {
            int int0 = this.tex.getWidth() / this.ENTRY_WID;
            int int1 = this.EntryList.size();
            int int2 = int1 % int0;
            int int3 = int1 / int0;
            arg0.atlas = this;
            arg0.x = int2 * this.ENTRY_WID;
            arg0.y = int3 * this.ENTRY_HGT;
            arg0.w = this.ENTRY_WID;
            arg0.h = this.ENTRY_HGT;
            arg0.tex = this.tex.split(arg0.key, arg0.x, this.tex.getHeight() - (arg0.y + this.ENTRY_HGT), arg0.w, arg0.h);
            arg0.tex.setName(arg0.key);
            this.EntryList.add(arg0);
        }

        public void Reset() {
            this.EntryList.forEach(DeadBodyAtlas.AtlasEntry::Reset);
            this.EntryList.clear();
            if (!this.tex.isDestroyed()) {
                RenderThread.invokeOnRenderContext(() -> GL11.glDeleteTextures(this.tex.getID()));
            }

            this.tex = null;
        }
    }

    private static final class AtlasEntry {
        public DeadBodyAtlas.Atlas atlas;
        public String key;
        public String lightKey;
        public int updateCounter;
        public int x;
        public int y;
        public int w;
        public int h;
        public float offsetX;
        public float offsetY;
        public Texture tex;
        public boolean ready = false;

        public void Reset() {
            this.atlas = null;
            this.tex.destroy();
            this.tex = null;
            this.ready = false;
        }
    }

    private static final class BodyParams {
        HumanVisual humanVisual;
        final ItemVisuals itemVisuals = new ItemVisuals();
        IsoDirections dir;
        float angle;
        boolean bFemale;
        boolean bZombie;
        boolean bSkeleton;
        String animSetName;
        String stateName;
        final HashMap<String, String> variables = new HashMap<>();
        boolean bStanding;
        String primaryHandItem;
        String secondaryHandItem;
        final AttachedModelNames attachedModelNames = new AttachedModelNames();
        float x;
        float y;
        float z;
        float trackTime;
        boolean bOutside;
        boolean bRoom;
        final ColorInfo ambient = new ColorInfo();
        boolean fallOnFront = false;
        final IsoGridSquare.ResultLight[] lights = new IsoGridSquare.ResultLight[5];

        BodyParams() {
            for (int int0 = 0; int0 < this.lights.length; int0++) {
                this.lights[int0] = new IsoGridSquare.ResultLight();
            }
        }

        void init(DeadBodyAtlas.BodyParams arg0) {
            this.humanVisual = arg0.humanVisual;
            this.itemVisuals.clear();
            this.itemVisuals.addAll(arg0.itemVisuals);
            this.dir = arg0.dir;
            this.angle = arg0.angle;
            this.bFemale = arg0.bFemale;
            this.bZombie = arg0.bZombie;
            this.bSkeleton = arg0.bSkeleton;
            this.animSetName = arg0.animSetName;
            this.stateName = arg0.stateName;
            this.variables.clear();
            this.variables.putAll(arg0.variables);
            this.bStanding = arg0.bStanding;
            this.primaryHandItem = arg0.primaryHandItem;
            this.secondaryHandItem = arg0.secondaryHandItem;
            this.attachedModelNames.copyFrom(arg0.attachedModelNames);
            this.x = arg0.x;
            this.y = arg0.y;
            this.z = arg0.z;
            this.trackTime = arg0.trackTime;
            this.fallOnFront = arg0.fallOnFront;
            this.bOutside = arg0.bOutside;
            this.bRoom = arg0.bRoom;
            this.ambient.set(arg0.ambient.r, arg0.ambient.g, arg0.ambient.b, 1.0F);

            for (int int0 = 0; int0 < this.lights.length; int0++) {
                this.lights[int0].copyFrom(arg0.lights[int0]);
            }
        }

        void init(IsoDeadBody arg0) {
            this.humanVisual = arg0.getHumanVisual();
            arg0.getItemVisuals(this.itemVisuals);
            this.dir = arg0.dir;
            this.angle = arg0.getAngle();
            this.bFemale = arg0.isFemale();
            this.bZombie = arg0.isZombie();
            this.bSkeleton = arg0.isSkeleton();
            this.primaryHandItem = null;
            this.secondaryHandItem = null;
            this.attachedModelNames.initFrom(arg0.getAttachedItems());
            this.animSetName = "zombie";
            this.stateName = "onground";
            this.variables.clear();
            this.bStanding = false;
            if (arg0.getPrimaryHandItem() != null || arg0.getSecondaryHandItem() != null) {
                if (arg0.getPrimaryHandItem() != null && !StringUtils.isNullOrEmpty(arg0.getPrimaryHandItem().getStaticModel())) {
                    this.primaryHandItem = arg0.getPrimaryHandItem().getStaticModel();
                }

                if (arg0.getSecondaryHandItem() != null && !StringUtils.isNullOrEmpty(arg0.getSecondaryHandItem().getStaticModel())) {
                    this.secondaryHandItem = arg0.getSecondaryHandItem().getStaticModel();
                }

                this.animSetName = "player";
                this.stateName = "deadbody";
            }

            this.x = arg0.x;
            this.y = arg0.y;
            this.z = arg0.z;
            this.trackTime = 0.0F;
            this.fallOnFront = arg0.isFallOnFront();
            this.bOutside = arg0.square != null && arg0.square.isOutside();
            this.bRoom = arg0.square != null && arg0.square.getRoom() != null;
            this.initAmbient(arg0.square);
            this.initLights(arg0.square);
        }

        void init(IsoZombie arg0) {
            this.humanVisual = arg0.getHumanVisual();
            arg0.getItemVisuals(this.itemVisuals);
            this.dir = arg0.dir;
            this.angle = arg0.getAnimAngleRadians();
            this.bFemale = arg0.isFemale();
            this.bZombie = true;
            this.bSkeleton = arg0.isSkeleton();
            this.primaryHandItem = null;
            this.secondaryHandItem = null;
            this.attachedModelNames.initFrom(arg0.getAttachedItems());
            this.animSetName = "zombie";
            this.stateName = "onground";
            this.variables.clear();
            this.bStanding = false;
            this.x = arg0.x;
            this.y = arg0.y;
            this.z = arg0.z;
            this.trackTime = 0.0F;
            this.fallOnFront = arg0.isFallOnFront();
            this.bOutside = arg0.getCurrentSquare() != null && arg0.getCurrentSquare().isOutside();
            this.bRoom = arg0.getCurrentSquare() != null && arg0.getCurrentSquare().getRoom() != null;
            this.initAmbient(arg0.getCurrentSquare());
            this.initLights(arg0.getCurrentSquare());
        }

        void init(IsoMannequin arg0) {
            this.humanVisual = arg0.getHumanVisual();
            arg0.getItemVisuals(this.itemVisuals);
            this.dir = arg0.dir;
            this.angle = this.dir.ToVector().getDirection();
            this.bFemale = arg0.isFemale();
            this.bZombie = arg0.isZombie();
            this.bSkeleton = arg0.isSkeleton();
            this.primaryHandItem = null;
            this.secondaryHandItem = null;
            this.attachedModelNames.clear();
            this.animSetName = arg0.getAnimSetName();
            this.stateName = arg0.getAnimStateName();
            this.variables.clear();
            arg0.getVariables(this.variables);
            this.bStanding = true;
            this.x = arg0.getX();
            this.y = arg0.getY();
            this.z = arg0.getZ();
            this.trackTime = 0.0F;
            this.fallOnFront = false;
            this.bOutside = arg0.square != null && arg0.square.isOutside();
            this.bRoom = arg0.square != null && arg0.square.getRoom() != null;
            this.initAmbient(arg0.square);
            this.initLights(null);
        }

        void init(IHumanVisual arg0, IsoDirections arg1, String arg2, String arg3, float arg4) {
            this.humanVisual = arg0.getHumanVisual();
            arg0.getItemVisuals(this.itemVisuals);
            this.dir = arg1;
            this.angle = arg1.ToVector().getDirection();
            this.bFemale = arg0.isFemale();
            this.bZombie = arg0.isZombie();
            this.bSkeleton = arg0.isSkeleton();
            this.primaryHandItem = null;
            this.secondaryHandItem = null;
            this.attachedModelNames.clear();
            this.animSetName = arg2;
            this.stateName = arg3;
            this.variables.clear();
            this.bStanding = true;
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
            this.trackTime = arg4;
            this.fallOnFront = false;
            this.bOutside = true;
            this.bRoom = false;
            this.ambient.set(1.0F, 1.0F, 1.0F, 1.0F);
            this.initLights(null);
        }

        void initAmbient(IsoGridSquare var1) {
            this.ambient.set(1.0F, 1.0F, 1.0F, 1.0F);
        }

        void initLights(IsoGridSquare square) {
            for (int int0 = 0; int0 < this.lights.length; int0++) {
                this.lights[int0].radius = 0;
            }

            if (square != null) {
                IsoGridSquare.ILighting iLighting = square.lighting[0];
                int int1 = iLighting.resultLightCount();

                for (int int2 = 0; int2 < int1; int2++) {
                    this.lights[int2].copyFrom(iLighting.getResultLight(int2));
                }
            }
        }

        void Reset() {
            this.humanVisual = null;
            this.itemVisuals.clear();
            Arrays.fill(this.lights, null);
        }
    }

    public static final class BodyTexture {
        DeadBodyAtlas.AtlasEntry entry;

        public void render(float x, float y, float r, float g, float b, float a) {
            if (this.entry.ready && this.entry.tex.isReady()) {
                this.entry
                    .tex
                    .render(
                        x - this.entry.w / 2.0F - this.entry.offsetX,
                        y - this.entry.h / 2.0F - this.entry.offsetY,
                        this.entry.w,
                        this.entry.h,
                        r,
                        g,
                        b,
                        a,
                        null
                    );
            } else {
                SpriteRenderer.instance.drawGeneric(DeadBodyAtlas.instance.bodyTextureDrawerPool.alloc().init(this, x, y, r, g, b, a));
            }
        }

        public void renderObjectPicker(float sx, float sy, ColorInfo lightInfo, IsoGridSquare square, IsoObject object) {
            if (this.entry.ready) {
                IsoObjectPicker.Instance
                    .Add((int)(sx - this.entry.w / 2), (int)(sy - this.entry.h / 2), this.entry.w, this.entry.h, square, object, false, 1.0F, 1.0F);
            }
        }
    }

    private static final class BodyTextureDrawer extends TextureDraw.GenericDrawer {
        DeadBodyAtlas.BodyTexture bodyTexture;
        float x;
        float y;
        float r;
        float g;
        float b;
        float a;

        DeadBodyAtlas.BodyTextureDrawer init(
            DeadBodyAtlas.BodyTexture bodyTexturex, float float0, float float1, float float2, float float3, float float4, float float5
        ) {
            this.bodyTexture = bodyTexturex;
            this.x = float0;
            this.y = float1;
            this.r = float2;
            this.g = float3;
            this.b = float4;
            this.a = float5;
            return this;
        }

        @Override
        public void render() {
            DeadBodyAtlas.AtlasEntry atlasEntry = this.bodyTexture.entry;
            if (atlasEntry.ready && atlasEntry.tex.isReady()) {
                int int0 = (int)(this.x - atlasEntry.w / 2.0F - atlasEntry.offsetX);
                int int1 = (int)(this.y - atlasEntry.h / 2.0F - atlasEntry.offsetY);
                int int2 = atlasEntry.w;
                int int3 = atlasEntry.h;
                atlasEntry.tex.bind();
                GL11.glBegin(7);
                GL11.glColor4f(this.r, this.g, this.b, this.a);
                GL11.glTexCoord2f(atlasEntry.tex.xStart, atlasEntry.tex.yStart);
                GL11.glVertex2i(int0, int1);
                GL11.glTexCoord2f(atlasEntry.tex.xEnd, atlasEntry.tex.yStart);
                GL11.glVertex2i(int0 + int2, int1);
                GL11.glTexCoord2f(atlasEntry.tex.xEnd, atlasEntry.tex.yEnd);
                GL11.glVertex2i(int0 + int2, int1 + int3);
                GL11.glTexCoord2f(atlasEntry.tex.xStart, atlasEntry.tex.yEnd);
                GL11.glVertex2i(int0, int1 + int3);
                GL11.glEnd();
                SpriteRenderer.ringBuffer.restoreBoundTextures = true;
            }
        }

        @Override
        public void postRender() {
            this.bodyTexture = null;
            DeadBodyAtlas.instance.bodyTextureDrawerPool.release(this);
        }
    }

    private static final class CharacterTextureVisual implements IHumanVisual {
        final HumanVisual humanVisual = new HumanVisual(this);
        boolean bFemale;

        CharacterTextureVisual(boolean boolean0) {
            this.bFemale = boolean0;
            this.humanVisual.setHairModel("");
            this.humanVisual.setBeardModel("");
        }

        @Override
        public HumanVisual getHumanVisual() {
            return this.humanVisual;
        }

        @Override
        public void getItemVisuals(ItemVisuals arg0) {
            arg0.clear();
        }

        @Override
        public boolean isFemale() {
            return this.bFemale;
        }

        @Override
        public boolean isZombie() {
            return true;
        }

        @Override
        public boolean isSkeleton() {
            return false;
        }
    }

    private static final class Checksummer {
        private MessageDigest md;
        private final StringBuilder sb = new StringBuilder();

        public void reset() throws NoSuchAlgorithmException {
            if (this.md == null) {
                this.md = MessageDigest.getInstance("MD5");
            }

            this.md.reset();
        }

        public void update(byte arg0) {
            this.md.update(arg0);
        }

        public void update(boolean arg0) {
            this.md.update((byte)(arg0 ? 1 : 0));
        }

        public void update(int arg0) {
            this.md.update((byte)(arg0 & 0xFF));
            this.md.update((byte)(arg0 >> 8 & 0xFF));
            this.md.update((byte)(arg0 >> 16 & 0xFF));
            this.md.update((byte)(arg0 >> 24 & 0xFF));
        }

        public void update(String arg0) {
            if (arg0 != null && !arg0.isEmpty()) {
                this.md.update(arg0.getBytes());
            }
        }

        public void update(ImmutableColor arg0) {
            this.update((byte)(arg0.r * 255.0F));
            this.update((byte)(arg0.g * 255.0F));
            this.update((byte)(arg0.b * 255.0F));
        }

        public void update(IsoGridSquare.ResultLight arg0, float arg1, float arg2, float arg3) {
            if (arg0 != null && arg0.radius > 0) {
                this.update((int)(arg0.x - arg1));
                this.update((int)(arg0.y - arg2));
                this.update((int)(arg0.z - arg3));
                this.update((byte)(arg0.r * 255.0F));
                this.update((byte)(arg0.g * 255.0F));
                this.update((byte)(arg0.b * 255.0F));
                this.update((byte)arg0.radius);
            }
        }

        public String checksumToString() {
            byte[] bytes = this.md.digest();
            this.sb.setLength(0);

            for (int int0 = 0; int0 < bytes.length; int0++) {
                this.sb.append(bytes[int0] & 255);
            }

            return this.sb.toString();
        }
    }

    private static final class ClearAtlasTexture extends TextureDraw.GenericDrawer {
        DeadBodyAtlas.Atlas m_atlas;

        ClearAtlasTexture(DeadBodyAtlas.Atlas atlas) {
            this.m_atlas = atlas;
        }

        @Override
        public void render() {
            TextureFBO textureFBO = DeadBodyAtlas.instance.fbo;
            if (textureFBO != null && this.m_atlas.tex != null) {
                if (this.m_atlas.clear) {
                    if (textureFBO.getTexture() != this.m_atlas.tex) {
                        textureFBO.setTexture(this.m_atlas.tex);
                    }

                    textureFBO.startDrawing(false, false);
                    GL11.glPushAttrib(2048);
                    GL11.glViewport(0, 0, textureFBO.getWidth(), textureFBO.getHeight());
                    GL11.glMatrixMode(5889);
                    GL11.glPushMatrix();
                    GL11.glLoadIdentity();
                    int int0 = this.m_atlas.tex.getWidth();
                    int int1 = this.m_atlas.tex.getHeight();
                    GLU.gluOrtho2D(0.0F, int0, int1, 0.0F);
                    GL11.glMatrixMode(5888);
                    GL11.glPushMatrix();
                    GL11.glLoadIdentity();
                    GL11.glDisable(3089);
                    GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                    GL11.glClear(16640);
                    GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
                    textureFBO.endDrawing();
                    GL11.glEnable(3089);
                    GL11.glMatrixMode(5889);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(5888);
                    GL11.glPopMatrix();
                    GL11.glPopAttrib();
                    this.m_atlas.clear = false;
                }
            }
        }
    }

    private static final class DebugDrawInWorld extends TextureDraw.GenericDrawer {
        DeadBodyAtlas.RenderJob job;
        boolean bRendered;

        public void init(DeadBodyAtlas.RenderJob arg0) {
            this.job = arg0;
            this.bRendered = false;
        }

        @Override
        public void render() {
            this.job.animatedModel.DoRenderToWorld(this.job.body.x, this.job.body.y, this.job.body.z, this.job.m_animPlayerAngle);
            this.bRendered = true;
        }

        @Override
        public void postRender() {
            if (this.job.animatedModel != null) {
                if (this.bRendered) {
                    assert !DeadBodyAtlas.JobPool.contains(this.job);

                    DeadBodyAtlas.JobPool.push(this.job);
                } else {
                    assert !DeadBodyAtlas.JobPool.contains(this.job);

                    DeadBodyAtlas.JobPool.push(this.job);
                }

                this.job.animatedModel.postRender(this.bRendered);
            }
        }
    }

    private static final class RenderJob extends TextureDraw.GenericDrawer {
        static float SIZEV = 42.75F;
        public final DeadBodyAtlas.BodyParams body = new DeadBodyAtlas.BodyParams();
        public DeadBodyAtlas.AtlasEntry entry;
        public AnimatedModel animatedModel;
        public float m_animPlayerAngle;
        public int done = 0;
        public int renderRefCount;
        public boolean bClearThisSlotOnly;
        int entryW;
        int entryH;
        final int[] m_viewport = new int[4];
        final Matrix4f m_matri4f = new Matrix4f();
        final Matrix4f m_projection = new Matrix4f();
        final Matrix4f m_modelView = new Matrix4f();
        final Vector3f m_scenePos = new Vector3f();
        final float[] m_bounds = new float[4];
        final float[] m_offset = new float[2];

        public static DeadBodyAtlas.RenderJob getNew() {
            return DeadBodyAtlas.JobPool.isEmpty() ? new DeadBodyAtlas.RenderJob() : DeadBodyAtlas.JobPool.pop();
        }

        public DeadBodyAtlas.RenderJob init(DeadBodyAtlas.BodyParams arg0, DeadBodyAtlas.AtlasEntry arg1) {
            this.body.init(arg0);
            this.entry = arg1;
            if (this.animatedModel == null) {
                this.animatedModel = new AnimatedModel();
                this.animatedModel.setAnimate(false);
            }

            this.animatedModel.setAnimSetName(arg0.animSetName);
            this.animatedModel.setState(arg0.stateName);
            this.animatedModel.setPrimaryHandModelName(arg0.primaryHandItem);
            this.animatedModel.setSecondaryHandModelName(arg0.secondaryHandItem);
            this.animatedModel.setAttachedModelNames(arg0.attachedModelNames);
            this.animatedModel.setAmbient(arg0.ambient, arg0.bOutside, arg0.bRoom);
            this.animatedModel.setLights(arg0.lights, arg0.x, arg0.y, arg0.z);
            this.animatedModel.setModelData(arg0.humanVisual, arg0.itemVisuals);
            this.animatedModel.setAngle(DeadBodyAtlas.tempVector2.setLengthAndDirection(arg0.angle, 1.0F));
            this.animatedModel.setVariable("FallOnFront", arg0.fallOnFront);
            arg0.variables.forEach((string0, string1) -> this.animatedModel.setVariable(string0, string1));
            this.animatedModel.setTrackTime(arg0.trackTime);
            this.animatedModel.update();
            this.bClearThisSlotOnly = false;
            this.done = 0;
            this.renderRefCount = 0;
            return this;
        }

        public boolean renderMain() {
            if (this.animatedModel.isReadyToRender()) {
                this.animatedModel.renderMain();
                this.m_animPlayerAngle = this.animatedModel.getAnimationPlayer().getRenderedAngle();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void render() {
            if (this.done != 1) {
                GL11.glDepthMask(true);
                GL11.glColorMask(true, true, true, true);
                GL11.glDisable(3089);
                GL11.glPushAttrib(2048);
                ModelManager.instance.bitmap.startDrawing(true, true);
                GL11.glViewport(0, 0, ModelManager.instance.bitmap.getWidth(), ModelManager.instance.bitmap.getHeight());
                this.calcModelOffset(this.m_offset);
                this.animatedModel.setOffset(this.m_offset[0], 0.0F, this.m_offset[1]);
                this.animatedModel
                    .DoRender(
                        0,
                        0,
                        ModelManager.instance.bitmap.getTexture().getWidth(),
                        ModelManager.instance.bitmap.getTexture().getHeight(),
                        SIZEV,
                        this.m_animPlayerAngle
                    );
                if (this.animatedModel.isRendered()) {
                    this.renderAABB();
                }

                ModelManager.instance.bitmap.endDrawing();
                GL11.glPopAttrib();
                if (this.animatedModel.isRendered()) {
                    DeadBodyAtlas.instance.assignEntryToAtlas(this.entry, this.entryW, this.entryH);
                    DeadBodyAtlas.instance.toBodyAtlas(this);
                }
            }
        }

        @Override
        public void postRender() {
            if (this.animatedModel != null) {
                this.animatedModel.postRender(this.done == 1);

                assert this.renderRefCount > 0;

                this.renderRefCount--;
            }
        }

        void calcMatrices(Matrix4f matrix4f0, Matrix4f matrix4f1, float float3, float float4) {
            int int0 = ModelManager.instance.bitmap.getWidth();
            int int1 = ModelManager.instance.bitmap.getHeight();
            float float0 = SIZEV;
            float float1 = (float)int0 / int1;
            boolean boolean0 = true;
            matrix4f0.identity();
            if (boolean0) {
                matrix4f0.ortho(-float0 * float1, float0 * float1, float0, -float0, -100.0F, 100.0F);
            } else {
                matrix4f0.ortho(-float0 * float1, float0 * float1, -float0, float0, -100.0F, 100.0F);
            }

            float float2 = (float)Math.sqrt(2048.0);
            matrix4f0.scale(-float2, float2, float2);
            matrix4f1.identity();
            boolean boolean1 = true;
            if (boolean1) {
                matrix4f1.rotate((float) (Math.PI / 6), 1.0F, 0.0F, 0.0F);
                matrix4f1.rotate(this.m_animPlayerAngle + (float) (Math.PI / 4), 0.0F, 1.0F, 0.0F);
            } else {
                matrix4f1.rotate(this.m_animPlayerAngle, 0.0F, 1.0F, 0.0F);
            }

            matrix4f1.translate(float3, 0.0F, float4);
        }

        void calcModelBounds(float[] floats) {
            float float0 = Float.MAX_VALUE;
            float float1 = Float.MAX_VALUE;
            float float2 = -Float.MAX_VALUE;
            float float3 = -Float.MAX_VALUE;

            for (int int0 = 0; int0 < this.animatedModel.getAnimationPlayer().modelTransforms.length; int0++) {
                if (int0 != 44) {
                    org.lwjgl.util.vector.Matrix4f matrix4f = this.animatedModel.getAnimationPlayer().modelTransforms[int0];
                    this.sceneToUI(matrix4f.m03, matrix4f.m13, matrix4f.m23, this.m_projection, this.m_modelView, this.m_scenePos);
                    float0 = PZMath.min(float0, this.m_scenePos.x);
                    float2 = PZMath.max(float2, this.m_scenePos.x);
                    float1 = PZMath.min(float1, this.m_scenePos.y);
                    float3 = PZMath.max(float3, this.m_scenePos.y);
                }
            }

            floats[0] = float0;
            floats[1] = float1;
            floats[2] = float2;
            floats[3] = float3;
        }

        void calcModelOffset(float[] floats) {
            int int0 = ModelManager.instance.bitmap.getWidth();
            int int1 = ModelManager.instance.bitmap.getHeight();
            float float0 = 0.0F;
            float float1 = this.body.bStanding ? -0.0F : 0.0F;
            this.calcMatrices(this.m_projection, this.m_modelView, float0, float1);
            this.calcModelBounds(this.m_bounds);
            float float2 = this.m_bounds[0];
            float float3 = this.m_bounds[1];
            float float4 = this.m_bounds[2];
            float float5 = this.m_bounds[3];
            this.uiToScene(this.m_projection, this.m_modelView, float2, float3, this.m_scenePos);
            float float6 = this.m_scenePos.x;
            float float7 = this.m_scenePos.z;
            float float8 = (int0 - (float4 - float2)) / 2.0F;
            float float9 = (int1 - (float5 - float3)) / 2.0F;
            this.uiToScene(this.m_projection, this.m_modelView, float8, float9, this.m_scenePos);
            float0 += this.m_scenePos.x - float6;
            float1 += this.m_scenePos.z - float7;
            floats[0] = 1.0F * float0 + 0.0F;
            floats[1] = 1.0F * float1 + 0.0F;
            this.entry.offsetX = (float8 - float2) / (8.0F / Core.TileScale);
            this.entry.offsetY = (float9 - float3) / (8.0F / Core.TileScale);
        }

        void renderAABB() {
            this.calcMatrices(this.m_projection, this.m_modelView, this.m_offset[0], this.m_offset[1]);
            this.calcModelBounds(this.m_bounds);
            float float0 = this.m_bounds[0];
            float float1 = this.m_bounds[1];
            float float2 = this.m_bounds[2];
            float float3 = this.m_bounds[3];
            int int0 = ModelManager.instance.bitmap.getWidth();
            int int1 = ModelManager.instance.bitmap.getHeight();
            float float4 = 128.0F;
            float0 -= float4;
            float1 -= float4;
            float2 += float4;
            float3 += float4;
            float0 = (float)Math.floor(float0 / 128.0F) * 128.0F;
            float2 = (float)Math.ceil(float2 / 128.0F) * 128.0F;
            float1 = (float)Math.floor(float1 / 128.0F) * 128.0F;
            float3 = (float)Math.ceil(float3 / 128.0F) * 128.0F;
            this.entryW = (int)(float2 - float0) / (8 / Core.TileScale);
            this.entryH = (int)(float3 - float1) / (8 / Core.TileScale);
        }

        UI3DScene.Ray getCameraRay(float float0, float float1, Matrix4f matrix4f1, Matrix4f matrix4f2, UI3DScene.Ray ray) {
            Matrix4f matrix4f0 = DeadBodyAtlas.RenderJob.L_getCameraRay.matrix4f;
            matrix4f0.set(matrix4f1);
            matrix4f0.mul(matrix4f2);
            matrix4f0.invert();
            this.m_viewport[0] = 0;
            this.m_viewport[1] = 0;
            this.m_viewport[2] = ModelManager.instance.bitmap.getWidth();
            this.m_viewport[3] = ModelManager.instance.bitmap.getHeight();
            Vector3f vector3f0 = matrix4f0.unprojectInv(float0, float1, 0.0F, this.m_viewport, DeadBodyAtlas.RenderJob.L_getCameraRay.ray_start);
            Vector3f vector3f1 = matrix4f0.unprojectInv(float0, float1, 1.0F, this.m_viewport, DeadBodyAtlas.RenderJob.L_getCameraRay.ray_end);
            ray.origin.set(vector3f0);
            ray.direction.set(vector3f1.sub(vector3f0).normalize());
            return ray;
        }

        Vector3f sceneToUI(float float0, float float1, float float2, Matrix4f matrix4f1, Matrix4f matrix4f2, Vector3f vector3f) {
            Matrix4f matrix4f0 = this.m_matri4f;
            matrix4f0.set(matrix4f1);
            matrix4f0.mul(matrix4f2);
            this.m_viewport[0] = 0;
            this.m_viewport[1] = 0;
            this.m_viewport[2] = ModelManager.instance.bitmap.getWidth();
            this.m_viewport[3] = ModelManager.instance.bitmap.getHeight();
            matrix4f0.project(float0, float1, float2, this.m_viewport, vector3f);
            return vector3f;
        }

        Vector3f uiToScene(Matrix4f matrix4f0, Matrix4f matrix4f1, float float0, float float1, Vector3f vector3f) {
            UI3DScene.Plane plane = DeadBodyAtlas.RenderJob.L_uiToScene.plane;
            plane.point.set(0.0F);
            plane.normal.set(0.0F, 1.0F, 0.0F);
            UI3DScene.Ray ray = this.getCameraRay(float0, float1, matrix4f0, matrix4f1, DeadBodyAtlas.RenderJob.L_uiToScene.ray);
            if (UI3DScene.intersect_ray_plane(plane, ray, vector3f) != 1) {
                vector3f.set(0.0F);
            }

            return vector3f;
        }

        public void Reset() {
            this.body.Reset();
            this.entry = null;
            if (this.animatedModel != null) {
                this.animatedModel.releaseAnimationPlayer();
                this.animatedModel = null;
            }
        }

        static final class L_getCameraRay {
            static final Matrix4f matrix4f = new Matrix4f();
            static final Vector3f ray_start = new Vector3f();
            static final Vector3f ray_end = new Vector3f();
        }

        static final class L_uiToScene {
            static final UI3DScene.Plane plane = new UI3DScene.Plane();
            static final UI3DScene.Ray ray = new UI3DScene.Ray();
        }
    }
}
