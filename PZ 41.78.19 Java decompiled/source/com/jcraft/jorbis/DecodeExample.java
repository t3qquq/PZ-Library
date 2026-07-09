// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jorbis;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import java.io.FileInputStream;

class DecodeExample {
    static int convsize = 8192;
    static byte[] convbuffer = new byte[convsize];

    public static void main(String[] strings) {
        Object object = System.in;
        if (strings.length > 0) {
            try {
                object = new FileInputStream(strings[0]);
            } catch (Exception exception0) {
                System.err.println(exception0);
            }
        }

        SyncState syncState = new SyncState();
        StreamState streamState = new StreamState();
        Page page = new Page();
        Packet packet = new Packet();
        Info info = new Info();
        Comment comment = new Comment();
        DspState dspState = new DspState();
        Block block = new Block(dspState);
        int int0 = 0;
        syncState.init();

        while (true) {
            boolean boolean0 = false;
            int int1 = syncState.buffer(4096);
            byte[] bytes0 = syncState.data;

            try {
                int0 = object.read(bytes0, int1, 4096);
            } catch (Exception exception1) {
                System.err.println(exception1);
                System.exit(-1);
            }

            syncState.wrote(int0);
            if (syncState.pageout(page) != 1) {
                if (int0 < 4096) {
                    syncState.clear();
                    System.err.println("Done.");
                    return;
                }

                System.err.println("Input does not appear to be an Ogg bitstream.");
                System.exit(1);
            }

            streamState.init(page.serialno());
            info.init();
            comment.init();
            if (streamState.pagein(page) < 0) {
                System.err.println("Error reading first page of Ogg bitstream data.");
                System.exit(1);
            }

            if (streamState.packetout(packet) != 1) {
                System.err.println("Error reading initial header packet.");
                System.exit(1);
            }

            if (info.synthesis_headerin(comment, packet) < 0) {
                System.err.println("This Ogg bitstream does not contain Vorbis audio data.");
                System.exit(1);
            }

            for (int int2 = 0; int2 < 2; syncState.wrote(int0)) {
                while (int2 < 2) {
                    int int3 = syncState.pageout(page);
                    if (int3 == 0) {
                        break;
                    }

                    if (int3 == 1) {
                        streamState.pagein(page);

                        while (int2 < 2) {
                            int3 = streamState.packetout(packet);
                            if (int3 == 0) {
                                break;
                            }

                            if (int3 == -1) {
                                System.err.println("Corrupt secondary header.  Exiting.");
                                System.exit(1);
                            }

                            info.synthesis_headerin(comment, packet);
                            int2++;
                        }
                    }
                }

                int1 = syncState.buffer(4096);
                bytes0 = syncState.data;

                try {
                    int0 = object.read(bytes0, int1, 4096);
                } catch (Exception exception2) {
                    System.err.println(exception2);
                    System.exit(1);
                }

                if (int0 == 0 && int2 < 2) {
                    System.err.println("End of file before finding all Vorbis headers!");
                    System.exit(1);
                }
            }

            byte[][] bytes1 = comment.user_comments;

            for (int int4 = 0; int4 < bytes1.length && bytes1[int4] != null; int4++) {
                System.err.println(new String(bytes1[int4], 0, bytes1[int4].length - 1));
            }

            System.err.println("\nBitstream is " + info.channels + " channel, " + info.rate + "Hz");
            System.err.println("Encoded by: " + new String(comment.vendor, 0, comment.vendor.length - 1) + "\n");
            convsize = 4096 / info.channels;
            dspState.synthesis_init(info);
            block.init(dspState);
            float[][][] floats0 = new float[1][][];
            int[] ints = new int[info.channels];

            while (!boolean0) {
                while (!boolean0) {
                    int int5 = syncState.pageout(page);
                    if (int5 == 0) {
                        break;
                    }

                    if (int5 == -1) {
                        System.err.println("Corrupt or missing data in bitstream; continuing...");
                    } else {
                        streamState.pagein(page);

                        while (true) {
                            int5 = streamState.packetout(packet);
                            if (int5 == 0) {
                                if (page.eos() != 0) {
                                    boolean0 = true;
                                }
                                break;
                            }

                            if (int5 != -1) {
                                if (block.synthesis(packet) == 0) {
                                    dspState.synthesis_blockin(block);
                                }

                                int int6;
                                while ((int6 = dspState.synthesis_pcmout(floats0, ints)) > 0) {
                                    float[][] floats1 = floats0[0];
                                    int int7 = int6 < convsize ? int6 : convsize;

                                    for (int int8 = 0; int8 < info.channels; int8++) {
                                        int int9 = int8 * 2;
                                        int int10 = ints[int8];

                                        for (int int11 = 0; int11 < int7; int11++) {
                                            int int12 = (int)(floats1[int8][int10 + int11] * 32767.0);
                                            if (int12 > 32767) {
                                                int12 = 32767;
                                            }

                                            if (int12 < -32768) {
                                                int12 = -32768;
                                            }

                                            if (int12 < 0) {
                                                int12 |= 32768;
                                            }

                                            convbuffer[int9] = (byte)int12;
                                            convbuffer[int9 + 1] = (byte)(int12 >>> 8);
                                            int9 += 2 * info.channels;
                                        }
                                    }

                                    System.out.write(convbuffer, 0, 2 * info.channels * int7);
                                    dspState.synthesis_read(int7);
                                }
                            }
                        }
                    }
                }

                if (!boolean0) {
                    int1 = syncState.buffer(4096);
                    bytes0 = syncState.data;

                    try {
                        int0 = object.read(bytes0, int1, 4096);
                    } catch (Exception exception3) {
                        System.err.println(exception3);
                        System.exit(1);
                    }

                    syncState.wrote(int0);
                    if (int0 == 0) {
                        boolean0 = true;
                    }
                }
            }

            streamState.clear();
            block.clear();
            dspState.clear();
            info.clear();
        }
    }
}
