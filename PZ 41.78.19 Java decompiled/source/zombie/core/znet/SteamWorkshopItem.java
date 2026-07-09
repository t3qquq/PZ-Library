// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderThread;
import zombie.core.textures.PNGDecoder;

public class SteamWorkshopItem {
    private String workshopFolder;
    private String PublishedFileId;
    private String title = "";
    private String description = "";
    private String visibility = "public";
    private final ArrayList<String> tags = new ArrayList<>();
    private String changeNote = "";
    private boolean bHasMod;
    private boolean bHasMap;
    private final ArrayList<String> modIDs = new ArrayList<>();
    private final ArrayList<String> mapFolders = new ArrayList<>();
    private static final int VERSION1 = 1;
    private static final int LATEST_VERSION = 1;

    public SteamWorkshopItem(String _workshopFolder) {
        ZomboidFileSystem.instance.validatePrefix(_workshopFolder);
        this.workshopFolder = _workshopFolder;
    }

    public String getContentFolder() {
        return this.workshopFolder + File.separator + "Contents";
    }

    public String getFolderName() {
        return new File(this.workshopFolder).getName();
    }

    public void setID(String ID) {
        if (ID != null && !SteamUtils.isValidSteamID(ID)) {
            ID = null;
        }

        this.PublishedFileId = ID;
    }

    public String getID() {
        return this.PublishedFileId;
    }

    public void setTitle(String _title) {
        if (_title == null) {
            _title = "";
        }

        this.title = _title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDescription(String _description) {
        if (_description == null) {
            _description = "";
        }

        this.description = _description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setVisibility(String _visibility) {
        this.visibility = _visibility;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public void setVisibilityInteger(int v) {
        switch (v) {
            case 0:
                this.visibility = "public";
                break;
            case 1:
                this.visibility = "friendsOnly";
                break;
            case 2:
                this.visibility = "private";
                break;
            case 3:
                this.visibility = "unlisted";
                break;
            default:
                this.visibility = "public";
        }
    }

    public int getVisibilityInteger() {
        if ("public".equals(this.visibility)) {
            return 0;
        } else if ("friendsOnly".equals(this.visibility)) {
            return 1;
        } else if ("private".equals(this.visibility)) {
            return 2;
        } else {
            return "unlisted".equals(this.visibility) ? 3 : 0;
        }
    }

    public void setTags(ArrayList<String> _tags) {
        this.tags.clear();
        this.tags.addAll(_tags);
    }

    public static ArrayList<String> getAllowedTags() {
        ArrayList arrayList = new ArrayList();
        File file = ZomboidFileSystem.instance.getMediaFile("WorkshopTags.txt");

        String string;
        try (
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            while ((string = bufferedReader.readLine()) != null) {
                string = string.trim();
                if (!string.isEmpty()) {
                    arrayList.add(string);
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }

        return arrayList;
    }

    public ArrayList<String> getTags() {
        return this.tags;
    }

    public String getSubmitDescription() {
        String string = this.getDescription();
        if (!string.isEmpty()) {
            string = string + "\n\n";
        }

        string = string + "Workshop ID: " + this.getID();

        for (int int0 = 0; int0 < this.modIDs.size(); int0++) {
            string = string + "\nMod ID: " + this.modIDs.get(int0);
        }

        for (int int1 = 0; int1 < this.mapFolders.size(); int1++) {
            string = string + "\nMap Folder: " + this.mapFolders.get(int1);
        }

        return string;
    }

    public String[] getSubmitTags() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.tags);
        return arrayList.toArray(new String[arrayList.size()]);
    }

    public String getPreviewImage() {
        return this.workshopFolder + File.separator + "preview.png";
    }

    public void setChangeNote(String _changeNote) {
        if (_changeNote == null) {
            _changeNote = "";
        }

        this.changeNote = _changeNote;
    }

    public String getChangeNote() {
        return this.changeNote;
    }

    public boolean create() {
        return SteamWorkshop.instance.CreateWorkshopItem(this);
    }

    public boolean submitUpdate() {
        boolean[] booleans = new boolean[]{false};
        RenderThread.invokeOnRenderContext(
            () -> booleans[0] = TinyFileDialogs.tinyfd_messageBox(
                "WARNING: Steam Workshop upload requested!",
                "Please confirm you want to upload this mod to the Steam Workshop:\n\n%s%s\n\nIf you did not request this, click Cancel."
                    .formatted(
                        this.title.replace("\"", "\u201d").replace("'", "\u2019"), this.PublishedFileId == null ? "" : " (%s)".formatted(this.PublishedFileId)
                    ),
                "okcancel",
                "warning",
                false
            )
        );
        return booleans[0] && SteamWorkshop.instance.SubmitWorkshopItem(this);
    }

    public boolean getUpdateProgress(KahluaTable table) {
        if (table == null) {
            throw new NullPointerException("table is null");
        } else {
            long[] longs = new long[2];
            if (SteamWorkshop.instance.GetItemUpdateProgress(longs)) {
                System.out.println(longs[0] + "/" + longs[1]);
                table.rawset("processed", (double)longs[0]);
                table.rawset("total", (double)Math.max(longs[1], 1L));
                return true;
            } else {
                return false;
            }
        }
    }

    public int getUpdateProgressTotal() {
        return 1;
    }

    private String validateFileTypes(Path path0) {
        try {
            try (DirectoryStream directoryStream = Files.newDirectoryStream(path0)) {
                for (Path path1 : directoryStream) {
                    if (Files.isDirectory(path1)) {
                        String string0 = this.validateFileTypes(path1);
                        if (string0 != null) {
                            return string0;
                        }
                    } else {
                        String string1 = path1.getFileName().toString();
                        if (!string1.equalsIgnoreCase("pyramid.zip")
                            && (
                                string1.endsWith(".exe")
                                    || string1.endsWith(".dll")
                                    || string1.endsWith(".bat")
                                    || string1.endsWith(".app")
                                    || string1.endsWith(".dylib")
                                    || string1.endsWith(".sh")
                                    || string1.endsWith(".so")
                                    || string1.endsWith(".zip")
                            )) {
                            return "FileTypeNotAllowed";
                        }
                    }
                }
            }

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            return "IOError";
        }
    }

    private String validateModDotInfo(Path path) {
        String string0 = null;

        String string1;
        try (
            FileReader fileReader = new FileReader(path.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            while ((string1 = bufferedReader.readLine()) != null) {
                if (string1.startsWith("id=")) {
                    string0 = string1.replace("id=", "").trim();
                    break;
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            return "MissingModDotInfo";
        } catch (IOException iOException) {
            iOException.printStackTrace();
            return "IOError";
        }

        if (string0 != null && !string0.isEmpty()) {
            this.modIDs.add(string0);
            return null;
        } else {
            return "InvalidModDotInfo";
        }
    }

    private String validateMapDotInfo(Path var1) {
        return null;
    }

    private String validateMapFolder(Path path0) {
        boolean boolean0 = false;

        try (DirectoryStream directoryStream = Files.newDirectoryStream(path0)) {
            for (Path path1 : directoryStream) {
                if (!Files.isDirectory(path1) && "map.info".equals(path1.getFileName().toString())) {
                    String string = this.validateMapDotInfo(path1);
                    if (string != null) {
                        return string;
                    }

                    boolean0 = true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return "IOError";
        }

        if (!boolean0) {
            return "MissingMapDotInfo";
        } else {
            this.mapFolders.add(path0.getFileName().toString());
            return null;
        }
    }

    private String validateMapsFolder(Path path0) {
        boolean boolean0 = false;

        try (DirectoryStream directoryStream = Files.newDirectoryStream(path0)) {
            for (Path path1 : directoryStream) {
                if (Files.isDirectory(path1)) {
                    String string = this.validateMapFolder(path1);
                    if (string != null) {
                        return string;
                    }

                    boolean0 = true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return "IOError";
        }

        if (!boolean0) {
            return null;
        } else {
            this.bHasMap = true;
            return null;
        }
    }

    private String validateMediaFolder(Path path0) {
        try {
            try (DirectoryStream directoryStream = Files.newDirectoryStream(path0)) {
                for (Path path1 : directoryStream) {
                    if (Files.isDirectory(path1) && "maps".equals(path1.getFileName().toString())) {
                        String string = this.validateMapsFolder(path1);
                        if (string != null) {
                            return string;
                        }
                    }
                }
            }

            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            return "IOError";
        }
    }

    private String validateModFolder(Path path0) {
        boolean boolean0 = false;

        try (DirectoryStream directoryStream = Files.newDirectoryStream(path0)) {
            for (Path path1 : directoryStream) {
                if (Files.isDirectory(path1)) {
                    if ("media".equals(path1.getFileName().toString())) {
                        String string0 = this.validateMediaFolder(path1);
                        if (string0 != null) {
                            return string0;
                        }
                    }
                } else if ("mod.info".equals(path1.getFileName().toString())) {
                    String string1 = this.validateModDotInfo(path1);
                    if (string1 != null) {
                        return string1;
                    }

                    boolean0 = true;
                }
            }

            return !boolean0 ? "MissingModDotInfo" : null;
        } catch (Exception exception) {
            exception.printStackTrace();
            return "IOError";
        }
    }

    private String validateModsFolder(Path path0) {
        boolean boolean0 = false;

        try (DirectoryStream directoryStream = Files.newDirectoryStream(path0)) {
            for (Path path1 : directoryStream) {
                if (!Files.isDirectory(path1)) {
                    return "FileNotAllowedInMods";
                }

                String string = this.validateModFolder(path1);
                if (string != null) {
                    return string;
                }

                boolean0 = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return "IOError";
        }

        if (!boolean0) {
            return "EmptyModsFolder";
        } else {
            this.bHasMod = true;
            return null;
        }
    }

    private String validateBuildingsFolder(Path var1) {
        return null;
    }

    private String validateCreativeFolder(Path var1) {
        return null;
    }

    public String validatePreviewImage(Path path) throws IOException {
        if (Files.exists(path) && Files.isReadable(path) && !Files.isDirectory(path)) {
            if (Files.size(path) > 1024000L) {
                return "PreviewFileSize";
            } else {
                try {
                    try (
                        FileInputStream fileInputStream = new FileInputStream(path.toFile());
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    ) {
                        PNGDecoder pNGDecoder = new PNGDecoder(bufferedInputStream, false);
                        if (pNGDecoder.getWidth() != 256 || pNGDecoder.getHeight() != 256) {
                            return "PreviewDimensions";
                        }
                    }

                    return null;
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                    return "PreviewFormat";
                }
            }
        } else {
            return "PreviewNotFound";
        }
    }

    public String validateContents() {
        this.bHasMod = false;
        this.bHasMap = false;
        this.modIDs.clear();
        this.mapFolders.clear();

        try {
            Path path0 = FileSystems.getDefault().getPath(this.getContentFolder());
            if (!Files.isDirectory(path0)) {
                return "MissingContents";
            } else {
                Path path1 = FileSystems.getDefault().getPath(this.getPreviewImage());
                String string = this.validatePreviewImage(path1);
                if (string != null) {
                    return string;
                } else {
                    boolean boolean0 = false;

                    try (DirectoryStream directoryStream = Files.newDirectoryStream(path0)) {
                        for (Path path2 : directoryStream) {
                            if (!Files.isDirectory(path2)) {
                                return "FileNotAllowedInContents";
                            }

                            if ("buildings".equals(path2.getFileName().toString())) {
                                string = this.validateBuildingsFolder(path2);
                                if (string != null) {
                                    return string;
                                }
                            } else if ("creative".equals(path2.getFileName().toString())) {
                                string = this.validateCreativeFolder(path2);
                                if (string != null) {
                                    return string;
                                }
                            } else {
                                if (!"mods".equals(path2.getFileName().toString())) {
                                    return "FolderNotAllowedInContents";
                                }

                                string = this.validateModsFolder(path2);
                                if (string != null) {
                                    return string;
                                }
                            }

                            boolean0 = true;
                        }

                        return !boolean0 ? "EmptyContentsFolder" : this.validateFileTypes(path0);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        return "IOError";
                    }
                }
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
            return "IOError";
        }
    }

    public String getExtendedErrorInfo(String error) {
        if ("FolderNotAllowedInContents".equals(error)) {
            return "buildings/ creative/ mods/";
        } else {
            return "FileTypeNotAllowed".equals(error) ? "*.exe *.dll *.bat *.app *.dylib *.sh *.so *.zip" : "";
        }
    }

    public boolean readWorkshopTxt() {
        String string0 = this.workshopFolder + File.separator + "workshop.txt";
        if (!new File(string0).exists()) {
            return true;
        } else {
            try {
                boolean boolean0;
                try (
                    FileReader fileReader = new FileReader(string0);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                ) {
                    String string1;
                    while ((string1 = bufferedReader.readLine()) != null) {
                        string1 = string1.trim();
                        if (!string1.isEmpty() && !string1.startsWith("#") && !string1.startsWith("//")) {
                            if (string1.startsWith("id=")) {
                                String string2 = string1.replace("id=", "");
                                this.setID(string2);
                            } else if (string1.startsWith("description=")) {
                                if (!this.description.isEmpty()) {
                                    this.description = this.description + "\n";
                                }

                                this.description = this.description + string1.replace("description=", "");
                            } else if (string1.startsWith("tags=")) {
                                this.tags.addAll(Arrays.asList(string1.replace("tags=", "").split(";")));
                            } else if (string1.startsWith("title=")) {
                                this.title = string1.replace("title=", "");
                            } else if (!string1.startsWith("version=") && string1.startsWith("visibility=")) {
                                this.visibility = string1.replace("visibility=", "");
                            }
                        }
                    }

                    boolean0 = true;
                }

                return boolean0;
            } catch (IOException iOException) {
                iOException.printStackTrace();
                return false;
            }
        }
    }

    public boolean writeWorkshopTxt() {
        String string0 = this.workshopFolder + File.separator + "workshop.txt";
        File file = new File(string0);

        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("version=1");
            bufferedWriter.newLine();
            bufferedWriter.write("id=" + (this.PublishedFileId == null ? "" : this.PublishedFileId));
            bufferedWriter.newLine();
            bufferedWriter.write("title=" + this.title);
            bufferedWriter.newLine();

            for (String string1 : this.description.split("\n")) {
                bufferedWriter.write("description=" + string1);
                bufferedWriter.newLine();
            }

            String string2 = "";

            for (int int0 = 0; int0 < this.tags.size(); int0++) {
                string2 = string2 + this.tags.get(int0);
                if (int0 < this.tags.size() - 1) {
                    string2 = string2 + ";";
                }
            }

            bufferedWriter.write("tags=" + string2);
            bufferedWriter.newLine();
            bufferedWriter.write("visibility=" + this.visibility);
            bufferedWriter.newLine();
            bufferedWriter.close();
            return true;
        } catch (IOException iOException) {
            iOException.printStackTrace();
            return false;
        }
    }

    public static enum ItemState {
        None(0),
        Subscribed(1),
        LegacyItem(2),
        Installed(4),
        NeedsUpdate(8),
        Downloading(16),
        DownloadPending(32);

        private final int value;

        private ItemState(int int1) {
            this.value = int1;
        }

        public int getValue() {
            return this.value;
        }

        public boolean and(SteamWorkshopItem.ItemState itemState0) {
            return (this.value & itemState0.value) != 0;
        }

        public boolean and(long long0) {
            return (this.value & long0) != 0L;
        }

        public boolean not(long long0) {
            return (this.value & long0) == 0L;
        }

        public static String toString(long long0) {
            if (long0 == None.getValue()) {
                return "None";
            } else {
                StringBuilder stringBuilder = new StringBuilder();

                for (SteamWorkshopItem.ItemState itemState : values()) {
                    if (itemState != None && itemState.and(long0)) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append("|");
                        }

                        stringBuilder.append(itemState.name());
                    }
                }

                return stringBuilder.toString();
            }
        }
    }
}
