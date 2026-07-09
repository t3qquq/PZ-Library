// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.backup;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.network.CoopSlave;
import zombie.network.GameServer;
import zombie.network.ServerOptions;

public class ZipBackup {
    private static final int compressionMethod = 0;
    static ParallelScatterZipCreator scatterZipCreator = null;
    private static long lastBackupTime = 0L;

    public static void onStartup() {
        lastBackupTime = System.currentTimeMillis();
        if (ServerOptions.getInstance().BackupsOnStart.getValue()) {
            makeBackupFile(GameServer.ServerName, ZipBackup.BackupTypes.startup);
        }
    }

    public static void onVersion() {
        if (ServerOptions.getInstance().BackupsOnVersionChange.getValue()) {
            String string0 = ZomboidFileSystem.instance.getCacheDir() + File.separator + "backups" + File.separator + "last_server_version.txt";
            String string1 = getStringFromZip(string0);
            String string2 = Core.getInstance().getGameVersion().toString();
            if (!string2.equals(string1)) {
                putTextFile(string0, string2);
                makeBackupFile(GameServer.ServerName, ZipBackup.BackupTypes.version);
            }
        }
    }

    public static void onPeriod() {
        int int0 = ServerOptions.getInstance().BackupsPeriod.getValue();
        if (int0 > 0) {
            if (System.currentTimeMillis() - lastBackupTime > int0 * 60000) {
                lastBackupTime = System.currentTimeMillis();
                makeBackupFile(GameServer.ServerName, ZipBackup.BackupTypes.period);
            }
        }
    }

    public static void makeBackupFile(String string2, ZipBackup.BackupTypes backupTypes) {
        String string0 = ZomboidFileSystem.instance.getCacheDir() + File.separator + "backups" + File.separator + backupTypes.name();
        long long0 = System.currentTimeMillis();
        DebugLog.log("Start making backup to: " + string0);
        scatterZipCreator = new ParallelScatterZipCreator();
        CoopSlave.status("UI_ServerStatus_CreateBackup");
        FileOutputStream fileOutputStream = null;
        ZipArchiveOutputStream zipArchiveOutputStream = null;

        try {
            File file0 = new File(string0);
            if (!file0.exists()) {
                file0.mkdirs();
            }

            rotateBackupFile(backupTypes);
            String string1 = string0 + File.separator + "backup_1.zip";

            try {
                Files.deleteIfExists(Paths.get(string1));
            } catch (IOException iOException0) {
                iOException0.printStackTrace();
            }

            File file1 = new File(string1);
            file1.delete();
            fileOutputStream = new FileOutputStream(file1);
            zipArchiveOutputStream = new ZipArchiveOutputStream(fileOutputStream);
            zipArchiveOutputStream.setUseZip64(Zip64Mode.AsNeeded);
            zipArchiveOutputStream.setMethod(0);
            zipArchiveOutputStream.setLevel(0);
            zipTextFile("readme.txt", getBackupReadme(string2));
            zipArchiveOutputStream.setComment(getBackupReadme(string2));
            zipFile("options.ini", "options.ini");
            zipFile("popman-options.ini", "popman-options.ini");
            zipFile("latestSave.ini", "latestSave.ini");
            zipFile("debug-options.ini", "debug-options.ini");
            zipFile("sounds.ini", "sounds.ini");
            zipFile("gamepadBinding.config", "gamepadBinding.config");
            zipDir("mods", "mods");
            zipDir("Lua", "Lua");
            zipDir("db", "db");
            zipDir("Server", "Server");
            synchronized (IsoChunk.WriteLock) {
                zipDir("Saves" + File.separator + "Multiplayer" + File.separator + string2, "Saves" + File.separator + "Multiplayer" + File.separator + string2);

                try {
                    scatterZipCreator.writeTo(zipArchiveOutputStream);
                    DebugLog.log(scatterZipCreator.getStatisticsMessage().toString());
                    zipArchiveOutputStream.close();
                    fileOutputStream.close();
                } catch (IOException iOException1) {
                    iOException1.printStackTrace();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException iOException2) {
                    iOException2.printStackTrace();
                }
            }
        }

        DebugLog.log("Backup made in " + (System.currentTimeMillis() - long0) + " ms");
    }

    private static void rotateBackupFile(ZipBackup.BackupTypes backupTypes) {
        int int0 = ServerOptions.getInstance().BackupsCount.getValue() - 1;
        if (int0 > 0) {
            Path path0 = Paths.get(
                ZomboidFileSystem.instance.getCacheDir()
                    + File.separator
                    + "backups"
                    + File.separator
                    + backupTypes
                    + File.separator
                    + "backup_"
                    + (int0 + 1)
                    + ".zip"
            );

            try {
                Files.deleteIfExists(path0);
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

            for (int int1 = int0; int1 > 0; int1--) {
                Path path1 = Paths.get(
                    ZomboidFileSystem.instance.getCacheDir()
                        + File.separator
                        + "backups"
                        + File.separator
                        + backupTypes
                        + File.separator
                        + "backup_"
                        + int1
                        + ".zip"
                );
                Path path2 = Paths.get(
                    ZomboidFileSystem.instance.getCacheDir()
                        + File.separator
                        + "backups"
                        + File.separator
                        + backupTypes
                        + File.separator
                        + "backup_"
                        + (int1 + 1)
                        + ".zip"
                );

                try {
                    Files.move(path1, path2);
                } catch (Exception exception) {
                }
            }
        }
    }

    private static String getBackupReadme(String string0) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        simpleDateFormat.format(date);
        int int0 = getWorldVersion(string0);
        String string1 = "";
        if (int0 == -2) {
            string1 = "World isn't exist";
        } else if (int0 == -1) {
            string1 = "World version cannot be determined";
        } else {
            string1 = String.valueOf(int0);
        }

        return "Backup time: "
            + simpleDateFormat.format(date)
            + "\nServerName: "
            + string0
            + "\nCurrent server version:"
            + Core.getInstance().getGameVersion()
            + "\nCurrent world version:195\nWorld version in this backup is:"
            + string1;
    }

    private static int getWorldVersion(String string) {
        File file = new File(ZomboidFileSystem.instance.getSaveDir() + File.separator + "Multiplayer" + File.separator + string + File.separator + "map_t.bin");
        if (file.exists()) {
            try {
                byte byte0;
                try (
                    FileInputStream fileInputStream = new FileInputStream(file);
                    DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                ) {
                    byte byte1 = dataInputStream.readByte();
                    byte byte2 = dataInputStream.readByte();
                    byte byte3 = dataInputStream.readByte();
                    byte byte4 = dataInputStream.readByte();
                    if (byte1 == 71 && byte2 == 77 && byte3 == 84 && byte4 == 77) {
                        return dataInputStream.readInt();
                    }

                    byte0 = -1;
                }

                return byte0;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return -2;
    }

    private static void putTextFile(String string0, String string1) {
        try {
            Path path = Paths.get(string0);
            Files.createDirectories(path.getParent());

            try {
                Files.delete(path);
            } catch (Exception exception0) {
            }

            Files.write(path, string1.getBytes());
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
    }

    private static String getStringFromZip(String string1) {
        String string0 = null;

        try {
            Path path = Paths.get(string1);
            if (Files.exists(path)) {
                List list = Files.readAllLines(path);
                string0 = (String)list.get(0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return string0;
    }

    private static void zipTextFile(String string1, String string0) {
        InputStreamSupplier inputStreamSupplier = () -> new ByteArrayInputStream(string0.getBytes(StandardCharsets.UTF_8));
        ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(string1);
        zipArchiveEntry.setMethod(0);
        scatterZipCreator.addArchiveEntry(zipArchiveEntry, inputStreamSupplier);
    }

    private static void zipFile(String string1, String string0) {
        Path path = Paths.get(ZomboidFileSystem.instance.getCacheDir() + File.separator + string0);
        if (Files.exists(path)) {
            InputStreamSupplier inputStreamSupplier = () -> {
                InputStream inputStream = null;

                try {
                    inputStream = Files.newInputStream(path);
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }

                return inputStream;
            };
            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(string1);
            zipArchiveEntry.setMethod(0);
            scatterZipCreator.addArchiveEntry(zipArchiveEntry, inputStreamSupplier);
        }
    }

    private static void zipDir(String string2, String string0) {
        Path path = Paths.get(ZomboidFileSystem.instance.getCacheDir() + File.separator + string0);
        if (Files.exists(path)) {
            try {
                File file0 = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + string0);
                if (file0.isDirectory()) {
                    Iterator iterator = Arrays.asList(file0.listFiles()).iterator();
                    int int0 = file0.getAbsolutePath().length() + 1;

                    while (iterator.hasNext()) {
                        File file1 = (File)iterator.next();
                        if (!file1.isDirectory()) {
                            String string1 = file1.getAbsolutePath().substring(int0);
                            InputStreamSupplier inputStreamSupplier = () -> {
                                InputStream inputStream = null;

                                try {
                                    inputStream = Files.newInputStream(file1.toPath());
                                } catch (IOException iOException) {
                                    iOException.printStackTrace();
                                }

                                return inputStream;
                            };
                            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(string2 + File.separator + string1);
                            zipArchiveEntry.setMethod(0);
                            scatterZipCreator.addArchiveEntry(zipArchiveEntry, inputStreamSupplier);
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private static enum BackupTypes {
        period,
        startup,
        version;
    }
}
