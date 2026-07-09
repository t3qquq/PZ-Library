// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipError;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.network.MD5Checksum;

public final class ZipLogs {
    static ArrayList<String> filePaths = new ArrayList<>();

    public static void addZipFile(boolean boolean0) {
        FileSystem fileSystem = null;

        try {
            String string0 = ZomboidFileSystem.instance.getCacheDir() + File.separator + "logs.zip";
            String string1 = new File(string0).toURI().toString();
            URI uri = URI.create("jar:" + string1);
            Path path = FileSystems.getDefault().getPath(string0).toAbsolutePath();
            HashMap hashMap = new HashMap();
            hashMap.put("create", String.valueOf(Files.notExists(path)));

            try {
                fileSystem = FileSystems.newFileSystem(uri, hashMap);
            } catch (IOException iOException0) {
                iOException0.printStackTrace();
                return;
            } catch (ZipError zipError) {
                zipError.printStackTrace();
                DebugLog.log("Deleting possibly-corrupt " + string0);

                try {
                    Files.deleteIfExists(path);
                } catch (IOException iOException1) {
                    iOException1.printStackTrace();
                }

                return;
            }

            long long0 = getMD5FromZip(fileSystem, "/meta/console.txt.md5");
            long long1 = getMD5FromZip(fileSystem, "/meta/coop-console.txt.md5");
            long long2 = getMD5FromZip(fileSystem, "/meta/server-console.txt.md5");
            long long3 = getMD5FromZip(fileSystem, "/meta/DebugLog.txt.md5");
            addLogToZip(fileSystem, "console", "console.txt", long0);
            addLogToZip(fileSystem, "coop-console", "coop-console.txt", long1);
            addLogToZip(fileSystem, "server-console", "server-console.txt", long2);
            addDebugLogToZip(fileSystem, "debug-log", "DebugLog.txt", long3);
            addToZip(fileSystem, "/configs/options.ini", "options.ini");
            addToZip(fileSystem, "/configs/popman-options.ini", "popman-options.ini");
            addToZip(fileSystem, "/configs/latestSave.ini", "latestSave.ini");
            addToZip(fileSystem, "/configs/debug-options.ini", "debug-options.ini");
            addToZip(fileSystem, "/configs/sounds.ini", "sounds.ini");
            addToZip(fileSystem, "/addition/translationProblems.txt", "translationProblems.txt");
            addToZip(fileSystem, "/addition/gamepadBinding.config", "gamepadBinding.config");
            addFilelistToZip(fileSystem, "/addition/mods.txt", "mods");
            addDirToZipLua(fileSystem, "/lua", "Lua");
            addDirToZip(fileSystem, "/db", "db");
            addDirToZip(fileSystem, "/server", "Server");
            addDirToZip(fileSystem, "/statistic", "Statistic");
            if (!boolean0) {
                addSaveOldToZip(fileSystem, "/save_old/map_t.bin", "map_t.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_ver.bin", "map_ver.bin");
                addSaveOldToZip(fileSystem, "/save_old/map.bin", "map.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_sand.bin", "map_sand.bin");
                addSaveOldToZip(fileSystem, "/save_old/reanimated.bin", "reanimated.bin");
                addSaveOldToZip(fileSystem, "/save_old/zombies.ini", "zombies.ini");
                addSaveOldToZip(fileSystem, "/save_old/z_outfits.bin", "z_outfits.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_p.bin", "map_p.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_meta.bin", "map_meta.bin");
                addSaveOldToZip(fileSystem, "/save_old/map_zone.bin", "map_zone.bin");
                addSaveOldToZip(fileSystem, "/save_old/serverid.dat", "serverid.dat");
                addSaveOldToZip(fileSystem, "/save_old/thumb.png", "thumb.png");
                addSaveOldToZip(fileSystem, "/save_old/players.db", "players.db");
                addSaveOldToZip(fileSystem, "/save_old/players.db-journal", "players.db-journal");
                addSaveOldToZip(fileSystem, "/save_old/vehicles.db", "vehicles.db");
                addSaveOldToZip(fileSystem, "/save_old/vehicles.db-journal", "vehicles.db-journal");
                putTextFile(fileSystem, "/save_old/description.txt", getLastSaveDescription());
            } else {
                addSaveToZip(fileSystem, "/save/map_t.bin", "map_t.bin");
                addSaveToZip(fileSystem, "/save/map_ver.bin", "map_ver.bin");
                addSaveToZip(fileSystem, "/save/map.bin", "map.bin");
                addSaveToZip(fileSystem, "/save/map_sand.bin", "map_sand.bin");
                addSaveToZip(fileSystem, "/save/reanimated.bin", "reanimated.bin");
                addSaveToZip(fileSystem, "/save/zombies.ini", "zombies.ini");
                addSaveToZip(fileSystem, "/save/z_outfits.bin", "z_outfits.bin");
                addSaveToZip(fileSystem, "/save/map_p.bin", "map_p.bin");
                addSaveToZip(fileSystem, "/save/map_meta.bin", "map_meta.bin");
                addSaveToZip(fileSystem, "/save/map_zone.bin", "map_zone.bin");
                addSaveToZip(fileSystem, "/save/serverid.dat", "serverid.dat");
                addSaveToZip(fileSystem, "/save/thumb.png", "thumb.png");
                addSaveToZip(fileSystem, "/save/players.db", "players.db");
                addSaveToZip(fileSystem, "/save/players.db-journal", "players.db-journal");
                addSaveToZip(fileSystem, "/save/vehicles.db", "vehicles.db");
                addSaveToZip(fileSystem, "/save/vehicles.db-journal", "vehicles.db-journal");
                putTextFile(fileSystem, "/save/description.txt", getCurrentSaveDescription());
            }

            try {
                fileSystem.close();
            } catch (IOException iOException2) {
                iOException2.printStackTrace();
            }
        } catch (Exception exception) {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (IOException iOException3) {
                    iOException3.printStackTrace();
                }
            }

            exception.printStackTrace();
        }
    }

    private static void copyToZip(Path path1, Path path2, Path path3) throws IOException {
        Path path0 = path1.resolve(path2.relativize(path3).toString());
        if (Files.isDirectory(path3)) {
            Files.createDirectories(path0);
        } else {
            Files.copy(path3, path0);
        }
    }

    public static void addToZip(FileSystem fileSystem, String string0, String string1) {
        try {
            Path path0 = fileSystem.getPath(string0);
            Files.createDirectories(path0.getParent());
            Path path1 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string1).toAbsolutePath();
            Files.deleteIfExists(path0);
            if (Files.exists(path1)) {
                Files.copy(path1, path0, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private static void addSaveToZip(FileSystem fileSystem, String string0, String string1) {
        try {
            Path path0 = fileSystem.getPath(string0);
            Files.createDirectories(path0.getParent());
            Path path1 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getFileNameInCurrentSave(string1)).toAbsolutePath();
            Files.deleteIfExists(path0);
            if (Files.exists(path1)) {
                Files.copy(path1, path0, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private static void addSaveOldToZip(FileSystem fileSystem, String string2, String string3) {
        try {
            BufferedReader bufferedReader = null;

            try {
                bufferedReader = new BufferedReader(new FileReader(new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "latestSave.ini")));
            } catch (FileNotFoundException fileNotFoundException) {
                return;
            }

            String string0 = bufferedReader.readLine();
            String string1 = bufferedReader.readLine();
            bufferedReader.close();
            Path path0 = fileSystem.getPath(string2);
            Files.createDirectories(path0.getParent());
            Path path1 = FileSystems.getDefault()
                .getPath(ZomboidFileSystem.instance.getSaveDir() + File.separator + string1 + File.separator + string0 + File.separator + string3)
                .toAbsolutePath();
            Files.deleteIfExists(path0);
            if (Files.exists(path1)) {
                Files.copy(path1, path0, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private static String getLastSaveDescription() {
        try {
            BufferedReader bufferedReader = null;

            try {
                bufferedReader = new BufferedReader(new FileReader(new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "latestSave.ini")));
            } catch (FileNotFoundException fileNotFoundException) {
                return "-";
            }

            String string0 = bufferedReader.readLine();
            String string1 = bufferedReader.readLine();
            bufferedReader.close();
            return "World: " + string0 + "\n\rGameMode:" + string1;
        } catch (IOException iOException) {
            iOException.printStackTrace();
            return "-";
        }
    }

    private static String getCurrentSaveDescription() {
        String string0 = "Sandbox";
        if (Core.GameMode != null) {
            string0 = Core.GameMode;
        }

        String string1 = "-";
        if (Core.GameSaveWorld != null) {
            string1 = Core.GameSaveWorld;
        }

        return "World: " + string1 + "\n\rGameMode:" + string0;
    }

    public static void addDirToZip(FileSystem fileSystem, String string0, String string1) {
        try {
            Path path0 = fileSystem.getPath(string0);
            deleteDirectory(fileSystem, path0);
            Files.createDirectories(path0);
            Path path1 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string1).toAbsolutePath();
            Stream stream = Files.walk(path1);
            stream.forEach(path2 -> {
                try {
                    copyToZip(path0, path1, path2);
                } catch (IOException iOExceptionx) {
                    throw new RuntimeException(iOExceptionx);
                }
            });
        } catch (IOException iOException) {
        }
    }

    private static void addDirToZipLua(FileSystem fileSystem, String string0, String string1) {
        try {
            Path path0 = fileSystem.getPath(string0);
            deleteDirectory(fileSystem, path0);
            Files.createDirectories(path0);
            Path path1 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string1).toAbsolutePath();
            Stream stream = Files.walk(path1);
            stream.forEach(path0x -> {
                try {
                    if (!path0x.endsWith("ServerList.txt") && !path0x.endsWith("ServerListSteam.txt")) {
                        copyToZip(path0, path1, path0x);
                    }
                } catch (IOException iOExceptionx) {
                    throw new RuntimeException(iOExceptionx);
                }
            });
        } catch (IOException iOException) {
        }
    }

    private static void addFilelistToZip(FileSystem fileSystem, String string0, String string1) {
        try {
            Path path0 = fileSystem.getPath(string0);
            Path path1 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string1).toAbsolutePath();
            Stream stream = Files.list(path1);
            String string2 = stream.map(Path::getFileName).map(Path::toString).collect(Collectors.joining("; "));
            Files.deleteIfExists(path0);
            Files.write(path0, string2.getBytes());
        } catch (IOException iOException) {
        }
    }

    static void deleteDirectory(FileSystem fileSystem, Path path) {
        filePaths.clear();
        getDirectoryFiles(path);

        for (String string : filePaths) {
            try {
                Files.delete(fileSystem.getPath(string));
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
    }

    static void getDirectoryFiles(Path path) {
        try {
            Stream stream = Files.walk(path);
            stream.forEach(path1 -> {
                if (!path1.toString().equals(path.toString())) {
                    if (Files.isDirectory(path1)) {
                        getDirectoryFiles(path1);
                    } else if (!filePaths.contains(path1.toString())) {
                        filePaths.add(path1.toString());
                    }
                }
            });
            filePaths.add(path.toString());
        } catch (IOException iOException) {
        }
    }

    private static void addLogToZip(FileSystem fileSystem, String string1, String string0, long long1) {
        long long0;
        try {
            long0 = MD5Checksum.createChecksum(ZomboidFileSystem.instance.getCacheDir() + File.separator + string0);
        } catch (Exception exception0) {
            long0 = 0L;
        }

        File file = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + string0);
        if (file.exists() && !file.isDirectory() && long0 != long1) {
            try {
                Path path0 = fileSystem.getPath("/" + string1 + "/log_5.txt");
                Files.delete(path0);
            } catch (Exception exception1) {
            }

            for (int int0 = 5; int0 > 0; int0--) {
                Path path1 = fileSystem.getPath("/" + string1 + "/log_" + int0 + ".txt");
                Path path2 = fileSystem.getPath("/" + string1 + "/log_" + (int0 + 1) + ".txt");

                try {
                    Files.move(path1, path2);
                } catch (Exception exception2) {
                }
            }

            try {
                Path path3 = fileSystem.getPath("/" + string1 + "/log_1.txt");
                Files.createDirectories(path3.getParent());
                Path path4 = FileSystems.getDefault().getPath(ZomboidFileSystem.instance.getCacheDir() + File.separator + string0).toAbsolutePath();
                Files.copy(path4, path3, StandardCopyOption.REPLACE_EXISTING);
                Path path5 = fileSystem.getPath("/meta/" + string0 + ".md5");
                Files.createDirectories(path5.getParent());

                try {
                    Files.delete(path5);
                } catch (Exception exception3) {
                }

                Files.write(path5, String.valueOf(long0).getBytes());
            } catch (Exception exception4) {
                exception4.printStackTrace();
            }
        }
    }

    private static void addDebugLogToZip(FileSystem fileSystem, String string2, String string3, long long1) {
        String string0 = null;
        File file0 = new File(LoggerManager.getLogsDir());
        String[] strings = file0.list();

        for (int int0 = 0; int0 < strings.length; int0++) {
            String string1 = strings[int0];
            if (string1.contains("DebugLog.txt")) {
                string0 = LoggerManager.getLogsDir() + File.separator + string1;
                break;
            }
        }

        if (string0 != null) {
            long long0;
            try {
                long0 = MD5Checksum.createChecksum(string0);
            } catch (Exception exception0) {
                long0 = 0L;
            }

            File file1 = new File(string0);
            if (file1.exists() && !file1.isDirectory() && long0 != long1) {
                try {
                    Path path0 = fileSystem.getPath("/" + string2 + "/log_5.txt");
                    Files.delete(path0);
                } catch (Exception exception1) {
                }

                for (int int1 = 5; int1 > 0; int1--) {
                    Path path1 = fileSystem.getPath("/" + string2 + "/log_" + int1 + ".txt");
                    Path path2 = fileSystem.getPath("/" + string2 + "/log_" + (int1 + 1) + ".txt");

                    try {
                        Files.move(path1, path2);
                    } catch (Exception exception2) {
                    }
                }

                try {
                    Path path3 = fileSystem.getPath("/" + string2 + "/log_1.txt");
                    Files.createDirectories(path3.getParent());
                    Path path4 = FileSystems.getDefault().getPath(string0).toAbsolutePath();
                    Files.copy(path4, path3, StandardCopyOption.REPLACE_EXISTING);
                    Path path5 = fileSystem.getPath("/meta/" + string3 + ".md5");
                    Files.createDirectories(path5.getParent());

                    try {
                        Files.delete(path5);
                    } catch (Exception exception3) {
                    }

                    Files.write(path5, String.valueOf(long0).getBytes());
                } catch (Exception exception4) {
                    exception4.printStackTrace();
                }
            }
        }
    }

    private static long getMD5FromZip(FileSystem fileSystem, String string) {
        long long0 = 0L;

        try {
            Path path = fileSystem.getPath(string);
            if (Files.exists(path)) {
                List list = Files.readAllLines(path);
                long0 = Long.parseLong((String)list.get(0));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return long0;
    }

    public static void putTextFile(FileSystem fileSystem, String string0, String string1) {
        try {
            Path path = fileSystem.getPath(string0);
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
}
