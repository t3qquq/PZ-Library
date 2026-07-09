// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import zombie.core.logger.ExceptionLogger;

public final class DiskFileDevice implements IFileDevice {
    private final String m_name;

    public DiskFileDevice(String string) {
        this.m_name = string;
    }

    @Override
    public IFile createFile(IFile iFile) {
        return new DiskFileDevice.DiskFile(iFile, this);
    }

    @Override
    public void destroyFile(IFile var1) {
    }

    @Override
    public InputStream createStream(String string, InputStream var2) throws IOException {
        return new FileInputStream(string);
    }

    @Override
    public void destroyStream(InputStream var1) {
    }

    @Override
    public String name() {
        return this.m_name;
    }

    private static final class DiskFile implements IFile {
        final DiskFileDevice m_device;
        RandomAccessFile m_file;
        InputStream m_inputStream;
        final IFile m_fallthrough;
        boolean m_use_fallthrough;

        DiskFile(IFile iFile, DiskFileDevice diskFileDevice) {
            this.m_device = diskFileDevice;
            this.m_fallthrough = iFile;
            this.m_use_fallthrough = false;
        }

        @Override
        public boolean open(String string, int int0) {
            File file = new File(string);
            boolean boolean0 = (int0 & 1) != 0;
            if (boolean0 && !file.exists() && this.m_fallthrough != null) {
                this.m_use_fallthrough = true;
                return this.m_fallthrough.open(string, int0);
            } else {
                try {
                    if ((int0 & 16) == 0) {
                        this.m_file = new RandomAccessFile(string, FileOpenMode.toStringMode(int0));
                    } else {
                        this.m_inputStream = new FileInputStream(string);
                    }

                    return true;
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                    return false;
                }
            }
        }

        @Override
        public void close() {
            if (this.m_fallthrough != null) {
                this.m_fallthrough.close();
            }

            if (this.m_file != null || this.m_inputStream != null) {
                try {
                    if (this.m_file != null) {
                        this.m_file.close();
                    }

                    if (this.m_inputStream != null) {
                        this.m_inputStream.close();
                    }
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                }

                this.m_file = null;
                this.m_inputStream = null;
                this.m_use_fallthrough = false;
            }
        }

        @Override
        public boolean read(byte[] bytes, long long0) {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.read(bytes, long0);
            } else if (this.m_file == null) {
                return false;
            } else {
                try {
                    return this.m_file.read(bytes, 0, (int)long0) == long0;
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                    return false;
                }
            }
        }

        @Override
        public boolean write(byte[] bytes, long long0) {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.write(bytes, long0);
            } else if (this.m_file == null) {
                return false;
            } else {
                try {
                    this.m_file.write(bytes, 0, (int)long0);
                    return true;
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                    return false;
                }
            }
        }

        @Override
        public byte[] getBuffer() {
            return this.m_use_fallthrough ? this.m_fallthrough.getBuffer() : null;
        }

        @Override
        public long size() {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.size();
            } else if (this.m_file == null) {
                return 0L;
            } else {
                try {
                    return this.m_file.length();
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                    return 0L;
                }
            }
        }

        @Override
        public boolean seek(FileSeekMode fileSeekMode, long long0) {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.seek(fileSeekMode, long0);
            } else if (this.m_file == null) {
                return false;
            } else {
                try {
                    this.m_file.seek(switch (fileSeekMode) {
                        case CURRENT -> this.m_file.getFilePointer();
                        case END -> this.m_file.length();
                    });
                    return true;
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                    return false;
                }
            }
        }

        @Override
        public long pos() {
            if (this.m_use_fallthrough) {
                return this.m_fallthrough.pos();
            } else if (this.m_file == null) {
                return 0L;
            } else {
                try {
                    return this.m_file.getFilePointer();
                } catch (IOException iOException) {
                    ExceptionLogger.logException(iOException);
                    return 0L;
                }
            }
        }

        @Override
        public InputStream getInputStream() {
            return this.m_inputStream;
        }

        @Override
        public IFileDevice getDevice() {
            return this.m_device;
        }

        @Override
        public void release() {
            this.getDevice().destroyFile(this);
        }
    }
}
