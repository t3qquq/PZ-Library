// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.world.WorldDictionary;

/**
 * TurboTuTone.
 */
public class RecordedMedia {
    public static boolean DISABLE_LINE_LEARNING = false;
    private static final int SPAWN_COMMON = 0;
    private static final int SPAWN_RARE = 1;
    private static final int SPAWN_EXCEPTIONAL = 2;
    public static final int VERSION1 = 1;
    public static final int VERSION2 = 2;
    public static final int VERSION = 2;
    public static final String SAVE_FILE = "recorded_media.bin";
    private final ArrayList<String> indexes = new ArrayList<>();
    private static final ArrayList<String> indexesFromServer = new ArrayList<>();
    private final Map<String, MediaData> mediaDataMap = new HashMap<>();
    private final Map<String, ArrayList<MediaData>> categorizedMap = new HashMap<>();
    private final ArrayList<String> categories = new ArrayList<>();
    private final ArrayList<String> legacyListenedLines = new ArrayList<>();
    private final HashSet<Short> homeVhsSpawned = new HashSet<>();
    private final Map<Integer, ArrayList<MediaData>> retailVhsSpawnTable = new HashMap<>();
    private final Map<Integer, ArrayList<MediaData>> retailCdSpawnTable = new HashMap<>();
    private boolean REQUIRES_SAVING = true;

    public void init() {
        try {
            this.load();
        } catch (Exception exception0) {
            exception0.printStackTrace();
        }

        LuaEventManager.triggerEvent("OnInitRecordedMedia", this);
        this.retailCdSpawnTable.put(0, new ArrayList<>());
        this.retailCdSpawnTable.put(1, new ArrayList<>());
        this.retailCdSpawnTable.put(2, new ArrayList<>());
        this.retailVhsSpawnTable.put(0, new ArrayList<>());
        this.retailVhsSpawnTable.put(1, new ArrayList<>());
        this.retailVhsSpawnTable.put(2, new ArrayList<>());
        ArrayList arrayList = this.categorizedMap.get("CDs");
        if (arrayList != null) {
            for (MediaData mediaData0 : arrayList) {
                if (mediaData0.getSpawning() == 1) {
                    this.retailCdSpawnTable.get(1).add(mediaData0);
                } else if (mediaData0.getSpawning() == 2) {
                    this.retailCdSpawnTable.get(2).add(mediaData0);
                } else {
                    this.retailCdSpawnTable.get(0).add(mediaData0);
                }
            }
        } else {
            DebugLog.General.error("categorizedMap with CDs is empty");
        }

        arrayList = this.categorizedMap.get("Retail-VHS");
        if (arrayList != null) {
            for (MediaData mediaData1 : arrayList) {
                if (mediaData1.getSpawning() == 1) {
                    this.retailVhsSpawnTable.get(1).add(mediaData1);
                } else if (mediaData1.getSpawning() == 2) {
                    this.retailVhsSpawnTable.get(2).add(mediaData1);
                } else {
                    this.retailVhsSpawnTable.get(0).add(mediaData1);
                }
            }
        } else {
            DebugLog.General.error("categorizedMap with Retail-VHS is empty");
        }

        try {
            this.save();
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
    }

    public static byte getMediaTypeForCategory(String category) {
        if (category == null) {
            return -1;
        } else {
            return (byte)(category.equalsIgnoreCase("cds") ? 0 : 1);
        }
    }

    public ArrayList<String> getCategories() {
        return this.categories;
    }

    public ArrayList<MediaData> getAllMediaForType(byte type) {
        ArrayList arrayList = new ArrayList();

        for (Entry entry : this.mediaDataMap.entrySet()) {
            if (((MediaData)entry.getValue()).getMediaType() == type) {
                arrayList.add((MediaData)entry.getValue());
            }
        }

        arrayList.sort(new RecordedMedia.MediaNameSorter());
        return arrayList;
    }

    public ArrayList<MediaData> getAllMediaForCategory(String category) {
        ArrayList arrayList = new ArrayList();

        for (Entry entry : this.mediaDataMap.entrySet()) {
            if (category.equalsIgnoreCase(((MediaData)entry.getValue()).getCategory())) {
                arrayList.add((MediaData)entry.getValue());
            }
        }

        arrayList.sort(new RecordedMedia.MediaNameSorter());
        return arrayList;
    }

    public MediaData register(String category, String id, String itemDisplayName, int spawning) {
        if (this.mediaDataMap.containsKey(id)) {
            DebugLog.log("RecordeMedia -> MediaData id already exists : " + id);
            return null;
        } else {
            if (spawning < 0) {
                spawning = 0;
            }

            MediaData mediaData = new MediaData(id, itemDisplayName, spawning);
            this.mediaDataMap.put(id, mediaData);
            mediaData.setCategory(category);
            if (!this.categorizedMap.containsKey(category)) {
                this.categorizedMap.put(category, new ArrayList<>());
                this.categories.add(category);
            }

            this.categorizedMap.get(category).add(mediaData);
            short short0;
            if (this.indexes.contains(id)) {
                short0 = (short)this.indexes.indexOf(id);
            } else {
                short0 = (short)this.indexes.size();
                this.indexes.add(id);
            }

            mediaData.setIndex(short0);
            this.REQUIRES_SAVING = true;
            return mediaData;
        }
    }

    public MediaData getMediaDataFromIndex(short index) {
        return index >= 0 && index < this.indexes.size() ? this.getMediaData(this.indexes.get(index)) : null;
    }

    public short getIndexForMediaData(MediaData data) {
        return (short)this.indexes.indexOf(data.getId());
    }

    public MediaData getMediaData(String id) {
        return this.mediaDataMap.get(id);
    }

    public MediaData getRandomFromCategory(String cat) {
        if (this.categorizedMap.containsKey(cat)) {
            MediaData mediaData = null;
            if (cat.equalsIgnoreCase("cds")) {
                int int0 = Rand.Next(0, 1000);
                if (int0 < 100) {
                    if (this.retailCdSpawnTable.get(2).size() > 0) {
                        mediaData = this.retailCdSpawnTable.get(2).get(Rand.Next(0, this.retailCdSpawnTable.get(2).size()));
                    }
                } else if (int0 < 400) {
                    if (this.retailCdSpawnTable.get(1).size() > 0) {
                        mediaData = this.retailCdSpawnTable.get(1).get(Rand.Next(0, this.retailCdSpawnTable.get(1).size()));
                    }
                } else {
                    mediaData = this.retailCdSpawnTable.get(0).get(Rand.Next(0, this.retailCdSpawnTable.get(0).size()));
                }

                if (mediaData != null) {
                    return mediaData;
                }

                return this.retailCdSpawnTable.get(0).get(Rand.Next(0, this.retailCdSpawnTable.get(0).size()));
            }

            if (cat.equalsIgnoreCase("retail-vhs")) {
                int int1 = Rand.Next(0, 1000);
                if (int1 < 100) {
                    if (this.retailVhsSpawnTable.get(2).size() > 0) {
                        mediaData = this.retailVhsSpawnTable.get(2).get(Rand.Next(0, this.retailVhsSpawnTable.get(2).size()));
                    }
                } else if (int1 < 400) {
                    if (this.retailVhsSpawnTable.get(1).size() > 0) {
                        mediaData = this.retailVhsSpawnTable.get(1).get(Rand.Next(0, this.retailVhsSpawnTable.get(1).size()));
                    }
                } else {
                    mediaData = this.retailVhsSpawnTable.get(0).get(Rand.Next(0, this.retailVhsSpawnTable.get(0).size()));
                }

                if (mediaData != null) {
                    return mediaData;
                }

                return this.retailVhsSpawnTable.get(0).get(Rand.Next(0, this.retailVhsSpawnTable.get(0).size()));
            }

            if (cat.equalsIgnoreCase("home-vhs")) {
                int int2 = Rand.Next(0, 1000);
                if (int2 < 200) {
                    ArrayList arrayList = this.categorizedMap.get("Home-VHS");
                    mediaData = (MediaData)arrayList.get(Rand.Next(0, arrayList.size()));
                    if (!this.homeVhsSpawned.contains(mediaData.getIndex())) {
                        this.homeVhsSpawned.add(mediaData.getIndex());
                        this.REQUIRES_SAVING = true;
                        return mediaData;
                    }
                }
            }
        }

        return null;
    }

    public void load() throws IOException {
        this.indexes.clear();
        if (GameClient.bClient) {
            this.indexes.addAll(indexesFromServer);
            indexesFromServer.clear();
        }

        if (!Core.getInstance().isNoSave()) {
            String string0 = ZomboidFileSystem.instance.getFileNameInCurrentSave("recorded_media.bin");
            File file = new File(string0);
            if (!file.exists()) {
                if (!WorldDictionary.isIsNewGame()) {
                    DebugLog.log("RecordedMedia data file is missing from world folder.");
                }
            } else {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    DebugLog.log("Loading Recorded Media:" + string0);
                    ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
                    byteBuffer.clear();
                    int int0 = fileInputStream.read(byteBuffer.array());
                    byteBuffer.limit(int0);
                    int int1 = byteBuffer.getInt();
                    int int2 = byteBuffer.getInt();

                    for (int int3 = 0; int3 < int2; int3++) {
                        String string1 = GameWindow.ReadString(byteBuffer);
                        if (!GameClient.bClient) {
                            this.indexes.add(string1);
                        }
                    }

                    if (int1 == 1) {
                        int2 = byteBuffer.getInt();

                        for (int int4 = 0; int4 < int2; int4++) {
                            String string2 = GameWindow.ReadString(byteBuffer);
                            this.legacyListenedLines.add(string2);
                        }
                    }

                    int2 = byteBuffer.getInt();

                    for (int int5 = 0; int5 < int2; int5++) {
                        this.homeVhsSpawned.add(byteBuffer.getShort());
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public void save() throws IOException {
        if (!Core.getInstance().isNoSave() && this.REQUIRES_SAVING) {
            try {
                int int0 = 0;
                int0 += this.indexes.size() * 40;
                int0 += this.homeVhsSpawned.size() * 2;
                int0 += 512;
                byte[] bytes = new byte[int0];
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                byteBuffer.putInt(2);
                byteBuffer.putInt(this.indexes.size());

                for (int int1 = 0; int1 < this.indexes.size(); int1++) {
                    GameWindow.WriteString(byteBuffer, this.indexes.get(int1));
                }

                byteBuffer.putInt(this.homeVhsSpawned.size());
                Short[] shorts = this.homeVhsSpawned.toArray(new Short[0]);

                for (int int2 = 0; int2 < shorts.length; int2++) {
                    byteBuffer.putShort(shorts[int2]);
                }

                byteBuffer.flip();
                String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("recorded_media.bin");
                File file = new File(string);
                DebugLog.log("Saving Recorded Media:" + string);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.getChannel().truncate(0L);
                fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            this.REQUIRES_SAVING = false;
        }
    }

    public static String toAscii(String string) {
        StringBuilder stringBuilder = new StringBuilder(string.length());
        string = Normalizer.normalize(string, Form.NFD);

        for (char char0 : string.toCharArray()) {
            if (char0 <= 127) {
                stringBuilder.append(char0);
            }
        }

        return stringBuilder.toString();
    }

    public boolean hasListenedToLine(IsoPlayer player, String guid) {
        return player.isKnownMediaLine(guid);
    }

    public boolean hasListenedToAll(IsoPlayer player, MediaData mediaData) {
        if (player == null) {
            player = IsoPlayer.players[0];
        }

        if (player != null && mediaData != null) {
            for (int int0 = 0; int0 < mediaData.getLineCount(); int0++) {
                MediaData.MediaLineData mediaLineData = mediaData.getLine(int0);
                if (!player.isKnownMediaLine(mediaLineData.getTextGuid())) {
                    return false;
                }
            }

            return mediaData.getLineCount() > 0;
        } else {
            return false;
        }
    }

    public void sendRequestData(ByteBuffer bb) {
        bb.putInt(this.indexes.size());

        for (int int0 = 0; int0 < this.indexes.size(); int0++) {
            GameWindow.WriteStringUTF(bb, this.indexes.get(int0));
        }
    }

    public static void receiveRequestData(ByteBuffer bb) {
        indexesFromServer.clear();
        int int0 = bb.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            indexesFromServer.add(GameWindow.ReadStringUTF(bb));
        }
    }

    public void handleLegacyListenedLines(IsoPlayer player) {
        if (!this.legacyListenedLines.isEmpty()) {
            if (player != null) {
                for (String string : this.legacyListenedLines) {
                    player.addKnownMediaLine(string);
                }
            }

            this.legacyListenedLines.clear();
        }
    }

    public static class MediaNameSorter implements Comparator<MediaData> {
        public int compare(MediaData mediaData1, MediaData mediaData0) {
            return mediaData1.getTranslatedItemDisplayName().compareToIgnoreCase(mediaData0.getTranslatedItemDisplayName());
        }
    }
}
