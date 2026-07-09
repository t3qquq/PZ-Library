// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.ArrayList;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.FileTask_ParseXML;
import zombie.fileSystem.FileSystem;
import zombie.util.list.PZArrayUtil;

public class ClothingItemAssetManager extends AssetManager {
    public static final ClothingItemAssetManager instance = new ClothingItemAssetManager();

    @Override
    protected void startLoading(Asset asset) {
        FileSystem fileSystem = asset.getAssetManager().getOwner().getFileSystem();
        FileTask_ParseXML fileTask_ParseXML = new FileTask_ParseXML(
            ClothingItemXML.class, asset.getPath().getPath(), object -> this.onFileTaskFinished((ClothingItem)asset, object), fileSystem
        );
        AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_ParseXML, asset);
        this.setTask(asset, assetTask_RunFileTask);
        assetTask_RunFileTask.execute();
    }

    private void onFileTaskFinished(ClothingItem clothingItem, Object object) {
        if (object instanceof ClothingItemXML clothingItemXML) {
            clothingItem.m_MaleModel = this.fixPath(clothingItemXML.m_MaleModel);
            clothingItem.m_FemaleModel = this.fixPath(clothingItemXML.m_FemaleModel);
            clothingItem.m_Static = clothingItemXML.m_Static;
            PZArrayUtil.arrayCopy(clothingItem.m_BaseTextures, this.fixPaths(clothingItemXML.m_BaseTextures));
            clothingItem.m_AttachBone = clothingItemXML.m_AttachBone;
            PZArrayUtil.arrayCopy(clothingItem.m_Masks, clothingItemXML.m_Masks);
            clothingItem.m_MasksFolder = this.fixPath(clothingItemXML.m_MasksFolder);
            clothingItem.m_UnderlayMasksFolder = this.fixPath(clothingItemXML.m_UnderlayMasksFolder);
            PZArrayUtil.arrayCopy(clothingItem.textureChoices, this.fixPaths(clothingItemXML.textureChoices));
            clothingItem.m_AllowRandomHue = clothingItemXML.m_AllowRandomHue;
            clothingItem.m_AllowRandomTint = clothingItemXML.m_AllowRandomTint;
            clothingItem.m_DecalGroup = clothingItemXML.m_DecalGroup;
            clothingItem.m_Shader = clothingItemXML.m_Shader;
            clothingItem.m_HatCategory = clothingItemXML.m_HatCategory;
            this.onLoadingSucceeded(clothingItem);
        } else {
            this.onLoadingFailed(clothingItem);
        }
    }

    private String fixPath(String string) {
        return string == null ? null : string.replaceAll("\\\\", "/");
    }

    private ArrayList<String> fixPaths(ArrayList<String> arrayList) {
        if (arrayList == null) {
            return null;
        } else {
            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                arrayList.set(int0, this.fixPath((String)arrayList.get(int0)));
            }

            return arrayList;
        }
    }

    @Override
    public void onStateChanged(Asset.State state0, Asset.State state1, Asset asset) {
        super.onStateChanged(state0, state1, asset);
        if (state1 == Asset.State.READY) {
            OutfitManager.instance.onClothingItemStateChanged((ClothingItem)asset);
        }
    }

    @Override
    protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams var2) {
        return new ClothingItem(assetPath, this);
    }

    @Override
    protected void destroyAsset(Asset var1) {
    }
}
