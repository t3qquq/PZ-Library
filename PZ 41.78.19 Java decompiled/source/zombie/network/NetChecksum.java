// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.codec.binary.Hex;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.scripting.ScriptManager;

public final class NetChecksum {
    public static final NetChecksum.Checksummer checksummer = new NetChecksum.Checksummer();
    public static final NetChecksum.Comparer comparer = new NetChecksum.Comparer();

    private static void noise(String string) {
        if (!Core.bDebug) {
        }

        DebugLog.log("NetChecksum: " + string);
    }

    public static final class Checksummer {
        private MessageDigest md;
        private final byte[] fileBytes = new byte[1024];
        private final byte[] convertBytes = new byte[1024];
        private boolean convertLineEndings;

        public void reset(boolean boolean0) throws NoSuchAlgorithmException {
            if (this.md == null) {
                this.md = MessageDigest.getInstance("MD5");
            }

            this.convertLineEndings = boolean0;
            this.md.reset();
        }

        public void addFile(String string1, String string0) throws NoSuchAlgorithmException {
            if (this.md == null) {
                this.md = MessageDigest.getInstance("MD5");
            }

            try (FileInputStream fileInputStream = new FileInputStream(string0)) {
                NetChecksum.GroupOfFiles.addFile(string1, string0);

                int int0;
                while ((int0 = fileInputStream.read(this.fileBytes)) != -1) {
                    if (this.convertLineEndings) {
                        boolean boolean0 = false;
                        int int1 = 0;

                        for (int int2 = 0; int2 < int0 - 1; int2++) {
                            if (this.fileBytes[int2] == 13 && this.fileBytes[int2 + 1] == 10) {
                                this.convertBytes[int1++] = 10;
                                boolean0 = true;
                            } else {
                                boolean0 = false;
                                this.convertBytes[int1++] = this.fileBytes[int2];
                            }
                        }

                        if (!boolean0) {
                            this.convertBytes[int1++] = this.fileBytes[int0 - 1];
                        }

                        this.md.update(this.convertBytes, 0, int1);
                        NetChecksum.GroupOfFiles.updateFile(this.convertBytes, int1);
                    } else {
                        this.md.update(this.fileBytes, 0, int0);
                        NetChecksum.GroupOfFiles.updateFile(this.fileBytes, int0);
                    }
                }

                NetChecksum.GroupOfFiles.endFile();
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }

        public String checksumToString() {
            byte[] bytes = this.md.digest();
            StringBuilder stringBuilder = new StringBuilder();

            for (int int0 = 0; int0 < bytes.length; int0++) {
                stringBuilder.append(Integer.toString((bytes[int0] & 255) + 256, 16).substring(1));
            }

            return stringBuilder.toString();
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            for (NetChecksum.GroupOfFiles groupOfFiles : NetChecksum.GroupOfFiles.groups) {
                String string = groupOfFiles.toString();
                stringBuilder.append("\n").append(string);
                if (GameClient.bClient) {
                    NetChecksum.comparer.sendError(GameClient.connection, string);
                }
            }

            return stringBuilder.toString();
        }
    }

    public static final class Comparer {
        private static final short PacketTotalChecksum = 1;
        private static final short PacketGroupChecksum = 2;
        private static final short PacketFileChecksums = 3;
        private static final short PacketError = 4;
        private static final byte FileDifferent = 1;
        private static final byte FileNotOnServer = 2;
        private static final byte FileNotOnClient = 3;
        private static final short NUM_GROUPS_TO_SEND = 10;
        private NetChecksum.Comparer.State state = NetChecksum.Comparer.State.Init;
        private short currentIndex;
        private String error;
        private final byte[] checksum = new byte[64];

        public void beginCompare() {
            this.error = null;
            this.sendTotalChecksum();
        }

        private void sendTotalChecksum() {
            if (GameClient.bClient) {
                NetChecksum.noise("send total checksum");
                ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
                PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
                byteBufferWriter.putShort((short)1);
                byteBufferWriter.putUTF(GameClient.checksum);
                byteBufferWriter.putUTF(ScriptManager.instance.getChecksum());
                PacketTypes.PacketType.Checksum.send(GameClient.connection);
                this.state = NetChecksum.Comparer.State.SentTotalChecksum;
            }
        }

        private void sendGroupChecksum() {
            if (GameClient.bClient) {
                if (this.currentIndex >= NetChecksum.GroupOfFiles.groups.size()) {
                    this.state = NetChecksum.Comparer.State.Success;
                } else {
                    short short0 = (short)Math.min(this.currentIndex + 10 - 1, NetChecksum.GroupOfFiles.groups.size() - 1);
                    NetChecksum.noise("send group checksums " + this.currentIndex + "-" + short0);
                    ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
                    PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
                    byteBufferWriter.putShort((short)2);
                    byteBufferWriter.putShort(this.currentIndex);
                    byteBufferWriter.putShort(short0);

                    for (short short1 = this.currentIndex; short1 <= short0; short1++) {
                        NetChecksum.GroupOfFiles groupOfFiles = NetChecksum.GroupOfFiles.groups.get(short1);
                        byteBufferWriter.putShort((short)groupOfFiles.totalChecksum.length);
                        byteBufferWriter.bb.put(groupOfFiles.totalChecksum);
                    }

                    PacketTypes.PacketType.Checksum.send(GameClient.connection);
                    this.state = NetChecksum.Comparer.State.SentGroupChecksum;
                }
            }
        }

        private void sendFileChecksums() {
            if (GameClient.bClient) {
                NetChecksum.noise("send file checksums " + this.currentIndex);
                NetChecksum.GroupOfFiles groupOfFiles = NetChecksum.GroupOfFiles.groups.get(this.currentIndex);
                ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
                PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
                byteBufferWriter.putShort((short)3);
                byteBufferWriter.bb.putShort(this.currentIndex);
                byteBufferWriter.putShort(groupOfFiles.fileCount);

                for (int int0 = 0; int0 < groupOfFiles.fileCount; int0++) {
                    byteBufferWriter.putUTF(groupOfFiles.relPaths[int0]);
                    byteBufferWriter.putByte((byte)groupOfFiles.checksums[int0].length);
                    byteBufferWriter.bb.put(groupOfFiles.checksums[int0]);
                }

                PacketTypes.PacketType.Checksum.send(GameClient.connection);
                this.state = NetChecksum.Comparer.State.SentFileChecksums;
            }
        }

        public String getReason(byte byte0) {
            return switch (byte0) {
                case 1 -> "File doesn't match the one on the server";
                case 2 -> "File doesn't exist on the server";
                case 3 -> "File doesn't exist on the client";
                default -> "File status unknown";
            };
        }

        public void clientPacket(ByteBuffer byteBuffer) {
            if (GameClient.bClient) {
                short short0 = byteBuffer.getShort();
                switch (short0) {
                    case 1:
                        if (this.state != NetChecksum.Comparer.State.SentTotalChecksum) {
                            this.error = "NetChecksum: received PacketTotalChecksum in state " + this.state;
                            this.state = NetChecksum.Comparer.State.Failed;
                        } else {
                            boolean boolean1 = byteBuffer.get() == 1;
                            boolean boolean2 = byteBuffer.get() == 1;
                            NetChecksum.noise("total checksum lua=" + boolean1 + " script=" + boolean2);
                            if (boolean1 && boolean2) {
                                this.state = NetChecksum.Comparer.State.Success;
                            } else {
                                this.currentIndex = 0;
                                this.sendGroupChecksum();
                            }
                        }
                        break;
                    case 2:
                        if (this.state != NetChecksum.Comparer.State.SentGroupChecksum) {
                            this.error = "NetChecksum: received PacketGroupChecksum in state " + this.state;
                            this.state = NetChecksum.Comparer.State.Failed;
                        } else {
                            short short2 = byteBuffer.getShort();
                            boolean boolean0 = byteBuffer.get() == 1;
                            if (short2 >= this.currentIndex && short2 < this.currentIndex + 10) {
                                NetChecksum.noise("group checksum " + short2 + " match=" + boolean0);
                                if (boolean0) {
                                    this.currentIndex = (short)(this.currentIndex + 10);
                                    this.sendGroupChecksum();
                                } else {
                                    this.currentIndex = short2;
                                    this.sendFileChecksums();
                                }
                            } else {
                                this.error = "NetChecksum: expected PacketGroupChecksum " + this.currentIndex + " but got " + short2;
                                this.state = NetChecksum.Comparer.State.Failed;
                            }
                        }
                        break;
                    case 3:
                        if (this.state != NetChecksum.Comparer.State.SentFileChecksums) {
                            this.error = "NetChecksum: received PacketFileChecksums in state " + this.state;
                            this.state = NetChecksum.Comparer.State.Failed;
                        } else {
                            short short1 = byteBuffer.getShort();
                            String string0 = GameWindow.ReadStringUTF(byteBuffer);
                            String string1 = GameWindow.ReadStringUTF(byteBuffer);
                            byte byte0 = byteBuffer.get();
                            if (short1 != this.currentIndex) {
                                this.error = "NetChecksum: expected PacketFileChecksums " + this.currentIndex + " but got " + short1;
                                this.state = NetChecksum.Comparer.State.Failed;
                            } else {
                                this.error = this.getReason(byte0);
                                if (DebugLog.isLogEnabled(LogSeverity.Debug, DebugType.Checksum)) {
                                    LoggerManager.getLogger("checksum").write(String.format("%s%s", this.error, NetChecksum.checksummer));
                                }

                                this.error = this.error + ":\n" + string0;
                                String string2 = ZomboidFileSystem.instance.getString(string0);
                                if (!string2.equals(string0)) {
                                    this.error = this.error + "\nclient: " + string2;
                                }

                                if (!string1.equals(string0)) {
                                    this.error = this.error + "\nserver: " + string1;
                                }

                                this.state = NetChecksum.Comparer.State.Failed;
                            }
                        }
                        break;
                    case 4:
                        this.error = GameWindow.ReadStringUTF(byteBuffer);
                        this.state = NetChecksum.Comparer.State.Failed;
                        break;
                    default:
                        this.error = "NetChecksum: unhandled packet " + short0;
                        this.state = NetChecksum.Comparer.State.Failed;
                }
            }
        }

        private boolean checksumEquals(byte[] bytes) {
            if (bytes == null) {
                return false;
            } else if (this.checksum.length < bytes.length) {
                return false;
            } else {
                for (int int0 = 0; int0 < bytes.length; int0++) {
                    if (this.checksum[int0] != bytes[int0]) {
                        return false;
                    }
                }

                return true;
            }
        }

        private void sendFileMismatch(UdpConnection udpConnection, short short0, String string, byte byte0) {
            if (GameServer.bServer) {
                ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
                PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
                byteBufferWriter.putShort((short)3);
                byteBufferWriter.putShort(short0);
                byteBufferWriter.putUTF(string);
                byteBufferWriter.putUTF(ZomboidFileSystem.instance.getString(string));
                byteBufferWriter.putByte(byte0);
                PacketTypes.PacketType.Checksum.send(udpConnection);
                if (DebugLog.isLogEnabled(LogSeverity.Debug, DebugType.Checksum)) {
                    LoggerManager.getLogger("checksum").write(String.format("%s%s", this.getReason(byte0), NetChecksum.checksummer));
                    LoggerManager.getLogger("checksum-" + udpConnection.idStr).write(this.getReason(byte0));
                }
            }
        }

        private void sendError(UdpConnection udpConnection, String string) {
            NetChecksum.noise(string);
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter);
            byteBufferWriter.putShort((short)4);
            byteBufferWriter.putUTF(string);
            PacketTypes.PacketType.Checksum.send(udpConnection);
        }

        public void serverPacket(ByteBuffer byteBuffer, UdpConnection udpConnection) {
            if (GameServer.bServer) {
                short short0 = byteBuffer.getShort();
                switch (short0) {
                    case 1:
                        String string1 = GameWindow.ReadString(byteBuffer);
                        String string2 = GameWindow.ReadString(byteBuffer);
                        boolean boolean0 = string1.equals(GameServer.checksum);
                        boolean boolean1 = string2.equals(ScriptManager.instance.getChecksum());
                        NetChecksum.noise("PacketTotalChecksum lua=" + boolean0 + " script=" + boolean1);
                        if (udpConnection.accessLevel == 32) {
                            boolean1 = true;
                            boolean0 = true;
                        }

                        udpConnection.checksumState = boolean0 && boolean1 ? UdpConnection.ChecksumState.Done : UdpConnection.ChecksumState.Different;
                        udpConnection.checksumTime = System.currentTimeMillis();
                        if (!boolean0 || !boolean1) {
                            DebugLog.log("user " + udpConnection.username + " will be kicked because Lua/script checksums do not match");
                            String string3 = "";
                            if (!boolean0) {
                                string3 = string3 + "Lua";
                            }

                            if (!boolean1) {
                                string3 = string3 + "Script";
                            }

                            ServerWorldDatabase.instance
                                .addUserlog(udpConnection.username, Userlog.UserlogType.LuaChecksum, string3, this.getClass().getSimpleName(), 1);
                        }

                        ByteBufferWriter byteBufferWriter0 = udpConnection.startPacket();
                        PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter0);
                        byteBufferWriter0.putShort((short)1);
                        byteBufferWriter0.putBoolean(boolean0);
                        byteBufferWriter0.putBoolean(boolean1);
                        PacketTypes.PacketType.Checksum.send(udpConnection);
                        break;
                    case 2:
                        short short1 = byteBuffer.getShort();
                        short short2 = byteBuffer.getShort();
                        if (short1 >= 0 && short2 >= short1 && short2 < short1 + 10) {
                            short short3 = short1;

                            while (short3 <= short2) {
                                short short4 = byteBuffer.getShort();
                                if (short4 < 0 || short4 > this.checksum.length) {
                                    this.sendError(udpConnection, "PacketGroupChecksum: numBytes is invalid");
                                    return;
                                }

                                byteBuffer.get(this.checksum, 0, short4);
                                if (short3 < NetChecksum.GroupOfFiles.groups.size()) {
                                    NetChecksum.GroupOfFiles groupOfFiles0 = NetChecksum.GroupOfFiles.groups.get(short3);
                                    if (this.checksumEquals(groupOfFiles0.totalChecksum)) {
                                        short3++;
                                        continue;
                                    }
                                }

                                ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                                PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter1);
                                byteBufferWriter1.putShort((short)2);
                                byteBufferWriter1.putShort(short3);
                                byteBufferWriter1.putBoolean(false);
                                PacketTypes.PacketType.Checksum.send(udpConnection);
                                return;
                            }

                            ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
                            PacketTypes.PacketType.Checksum.doPacket(byteBufferWriter2);
                            byteBufferWriter2.putShort((short)2);
                            byteBufferWriter2.putShort(short1);
                            byteBufferWriter2.putBoolean(true);
                            PacketTypes.PacketType.Checksum.send(udpConnection);
                        } else {
                            this.sendError(udpConnection, "PacketGroupChecksum: firstIndex and/or lastIndex are invalid");
                        }
                        break;
                    case 3:
                        short short5 = byteBuffer.getShort();
                        short short6 = byteBuffer.getShort();
                        if (short5 < 0 || short6 <= 0 || short6 > 20) {
                            this.sendError(udpConnection, "PacketFileChecksums: groupIndex and/or fileCount are invalid");
                            return;
                        }

                        if (short5 >= NetChecksum.GroupOfFiles.groups.size()) {
                            String string4 = GameWindow.ReadStringUTF(byteBuffer);
                            this.sendFileMismatch(udpConnection, short5, string4, (byte)2);
                            return;
                        }

                        NetChecksum.GroupOfFiles groupOfFiles1 = NetChecksum.GroupOfFiles.groups.get(short5);

                        for (short short7 = 0; short7 < short6; short7++) {
                            String string5 = GameWindow.ReadStringUTF(byteBuffer);
                            byte byte0 = byteBuffer.get();
                            if (byte0 < 0 || byte0 > this.checksum.length) {
                                this.sendError(udpConnection, "PacketFileChecksums: numBytes is invalid");
                                return;
                            }

                            if (short7 >= groupOfFiles1.fileCount) {
                                this.sendFileMismatch(udpConnection, short5, string5, (byte)2);
                                return;
                            }

                            if (!string5.equals(groupOfFiles1.relPaths[short7])) {
                                String string6 = ZomboidFileSystem.instance.getString(string5);
                                if (string6.equals(string5)) {
                                    this.sendFileMismatch(udpConnection, short5, string5, (byte)2);
                                    return;
                                }

                                this.sendFileMismatch(udpConnection, short5, groupOfFiles1.relPaths[short7], (byte)3);
                                return;
                            }

                            if (byte0 > groupOfFiles1.checksums[short7].length) {
                                this.sendFileMismatch(udpConnection, short5, groupOfFiles1.relPaths[short7], (byte)1);
                                return;
                            }

                            byteBuffer.get(this.checksum, 0, byte0);
                            if (!this.checksumEquals(groupOfFiles1.checksums[short7])) {
                                this.sendFileMismatch(udpConnection, short5, groupOfFiles1.relPaths[short7], (byte)1);
                                return;
                            }
                        }

                        if (groupOfFiles1.fileCount > short6) {
                            this.sendFileMismatch(udpConnection, short5, groupOfFiles1.relPaths[short6], (byte)3);
                            return;
                        }

                        this.sendError(udpConnection, "PacketFileChecksums: all checks passed when they shouldn't");
                        break;
                    case 4:
                        String string0 = GameWindow.ReadStringUTF(byteBuffer);
                        if (DebugLog.isLogEnabled(LogSeverity.Debug, DebugType.Checksum)) {
                            LoggerManager.getLogger("checksum-" + udpConnection.idStr).write(string0, null, true);
                        }
                        break;
                    default:
                        this.sendError(udpConnection, "Unknown packet " + short0);
                }
            }
        }

        private void gc() {
            NetChecksum.GroupOfFiles.gc();
        }

        public void update() {
            switch (this.state) {
                case Init:
                case SentTotalChecksum:
                case SentGroupChecksum:
                case SentFileChecksums:
                default:
                    break;
                case Success:
                    this.gc();
                    GameClient.checksumValid = true;
                    break;
                case Failed:
                    this.gc();
                    GameClient.connection.forceDisconnect("checksum-" + this.error);
                    GameWindow.bServerDisconnected = true;
                    GameWindow.kickReason = this.error;
            }
        }

        private static enum State {
            Init,
            SentTotalChecksum,
            SentGroupChecksum,
            SentFileChecksums,
            Success,
            Failed;
        }
    }

    public static final class GroupOfFiles {
        static final int MAX_FILES = 20;
        static MessageDigest mdTotal;
        static MessageDigest mdCurrentFile;
        static final ArrayList<NetChecksum.GroupOfFiles> groups = new ArrayList<>();
        static NetChecksum.GroupOfFiles currentGroup;
        byte[] totalChecksum;
        short fileCount;
        final String[] relPaths = new String[20];
        final String[] absPaths = new String[20];
        final byte[][] checksums = new byte[20][];

        private GroupOfFiles() throws NoSuchAlgorithmException {
            if (mdTotal == null) {
                mdTotal = MessageDigest.getInstance("MD5");
                mdCurrentFile = MessageDigest.getInstance("MD5");
            }

            mdTotal.reset();
            groups.add(this);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder()
                .append(this.fileCount)
                .append(" files, ")
                .append(this.absPaths.length)
                .append("/")
                .append(this.relPaths.length)
                .append("/")
                .append(this.checksums.length)
                .append(" \"")
                .append(Hex.encodeHexString(this.totalChecksum))
                .append("\"");

            for (int int0 = 0; int0 < 20; int0++) {
                stringBuilder.append("\n");
                if (int0 < this.relPaths.length) {
                    stringBuilder.append(" \"").append(this.relPaths[int0]).append("\"");
                }

                if (int0 < this.checksums.length) {
                    if (this.checksums[int0] == null) {
                        stringBuilder.append(" \"\"");
                    } else {
                        stringBuilder.append(" \"").append(Hex.encodeHexString(this.checksums[int0])).append("\"");
                    }
                }

                if (int0 < this.absPaths.length) {
                    stringBuilder.append(" \"").append(this.absPaths[int0]).append("\"");
                }
            }

            return stringBuilder.toString();
        }

        private void gc_() {
            Arrays.fill(this.relPaths, null);
            Arrays.fill(this.absPaths, null);
            Arrays.fill(this.checksums, null);
        }

        public static void initChecksum() {
            groups.clear();
            currentGroup = null;
        }

        public static void finishChecksum() {
            if (currentGroup != null) {
                currentGroup.totalChecksum = mdTotal.digest();
                currentGroup = null;
            }
        }

        private static void addFile(String string0, String string1) throws NoSuchAlgorithmException {
            if (currentGroup == null) {
                currentGroup = new NetChecksum.GroupOfFiles();
            }

            currentGroup.relPaths[currentGroup.fileCount] = string0;
            currentGroup.absPaths[currentGroup.fileCount] = string1;
            mdCurrentFile.reset();
        }

        private static void updateFile(byte[] bytes, int int0) {
            mdCurrentFile.update(bytes, 0, int0);
            mdTotal.update(bytes, 0, int0);
        }

        private static void endFile() {
            currentGroup.checksums[currentGroup.fileCount] = mdCurrentFile.digest();
            currentGroup.fileCount++;
            if (currentGroup.fileCount >= 20) {
                currentGroup.totalChecksum = mdTotal.digest();
                currentGroup = null;
            }
        }

        public static void gc() {
            for (NetChecksum.GroupOfFiles groupOfFiles : groups) {
                groupOfFiles.gc_();
            }

            groups.clear();
        }
    }
}
