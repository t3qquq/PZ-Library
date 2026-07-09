// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import zombie.util.StringUtils;

public class RCONClient {
    private Socket socket;

    public boolean disconnect() {
        try {
            this.socket.close();
            return true;
        } catch (IOException iOException) {
            System.out.println("Disconnect failed: " + iOException.getMessage());
            return false;
        }
    }

    public boolean connect(String string0, String string1) {
        try {
            this.socket = new Socket();
            this.socket.setSoTimeout(5000);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(string0, Integer.parseInt(string1));
            this.socket.connect(inetSocketAddress, 1000);
            return true;
        } catch (IOException iOException) {
            System.out.println("Connect failed: " + iOException.getMessage());
            return false;
        }
    }

    public boolean auth(String string) {
        try {
            int int0 = (int)(65535L & System.currentTimeMillis());
            RCONClient.RCONMessage rCONMessage0 = new RCONClient.RCONMessage(int0, 3, string);
            rCONMessage0.writeObject(this.socket.getOutputStream());
            RCONClient.RCONMessage rCONMessage1 = new RCONClient.RCONMessage();
            rCONMessage1.readObject(this.socket.getInputStream(), 14);
            if (rCONMessage1.type == 0 && rCONMessage1.id == int0) {
                RCONClient.RCONMessage rCONMessage2 = new RCONClient.RCONMessage();
                rCONMessage2.readObject(this.socket.getInputStream(), 14);
                if (rCONMessage2.type == 2 && rCONMessage1.id == int0) {
                    return true;
                } else {
                    System.out.println("Authentication failed: auth response");
                    return false;
                }
            } else {
                System.out.println("Authentication failed: response value");
                return false;
            }
        } catch (IOException iOException) {
            System.out.println("Authentication failed: timeout");
            return false;
        }
    }

    public String exec(String string) {
        try {
            int int0 = (int)(65535L & System.currentTimeMillis());
            RCONClient.RCONMessage rCONMessage0 = new RCONClient.RCONMessage(int0, 2, string);
            rCONMessage0.writeObject(this.socket.getOutputStream());
            RCONClient.RCONMessage rCONMessage1 = new RCONClient.RCONMessage();
            rCONMessage1.readObject(this.socket.getInputStream(), 0);
            return new String(rCONMessage1.body);
        } catch (IOException iOException) {
            System.out.println("Command execution failed");
            return null;
        }
    }

    public boolean send(String string1, String string0) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .setHeader("Content-type", "application/json")
                .uri(URI.create(string1))
                .POST(BodyPublishers.ofString("{\"text\":\"" + string0 + "\"}"))
                .build();
            HttpResponse httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            if (httpResponse != null && httpResponse.statusCode() != 200) {
                System.out.println((String)httpResponse.body());
                return false;
            } else {
                return true;
            }
        } catch (Exception exception) {
            System.out.println("Result post failed");
            return false;
        }
    }

    private static void sleep(long long0) {
        try {
            Thread.sleep(long0);
        } catch (Exception exception) {
        }
    }

    public static void main(String[] strings) {
        String string0 = null;
        String string1 = null;
        String string2 = null;
        String string3 = null;
        String string4 = null;
        boolean boolean0 = false;

        for (int int0 = 0; int0 < strings.length; int0++) {
            if (!StringUtils.isNullOrEmpty(strings[int0])) {
                if (strings[int0].equals("-ip")) {
                    string0 = strings[++int0].trim();
                } else if (strings[int0].equals("-port")) {
                    string1 = strings[++int0].trim();
                } else if (strings[int0].equals("-password")) {
                    string2 = strings[++int0].trim();
                } else if (strings[int0].equals("-command")) {
                    string3 = strings[++int0].trim();
                } else if (strings[int0].equals("-webhook")) {
                    string4 = strings[++int0].trim();
                }
            }
        }

        if (!StringUtils.isNullOrEmpty(string0)
            && !StringUtils.isNullOrEmpty(string1)
            && !StringUtils.isNullOrEmpty(string2)
            && !StringUtils.isNullOrEmpty(string3)) {
            if (!StringUtils.isNullOrEmpty(string4)) {
                boolean0 = true;
            }

            RCONClient rCONClient = new RCONClient();

            do {
                if (rCONClient.connect(string0, string1)) {
                    if (rCONClient.auth(string2)) {
                        if (boolean0 && !rCONClient.send(string4, String.format("Connected to server %s:%s", string0, string1))) {
                            break;
                        }

                        String string5 = null;

                        do {
                            String string6 = rCONClient.exec(string3);
                            if (StringUtils.isNullOrEmpty(string6)) {
                                break;
                            }

                            if (!string6.equals(string5)) {
                                if (boolean0) {
                                    if (!rCONClient.send(string4, string6)) {
                                        break;
                                    }

                                    sleep(5000L);
                                } else {
                                    System.out.println(string6);
                                }
                            }

                            string5 = string6;
                        } while (boolean0);

                        if (boolean0 && !rCONClient.send(string4, "Connection to server lost")) {
                            break;
                        }
                    }

                    rCONClient.disconnect();
                }

                if (boolean0) {
                    sleep(60000L);
                }
            } while (boolean0);
        } else {
            System.out.println("Incorrect arguments");
        }
    }

    private static class RCONMessage {
        private static final byte[] input = new byte[4096];
        private static final ByteBuffer bbr = ByteBuffer.wrap(input);
        private static final byte[] output = new byte[4096];
        private static final ByteBuffer bbw = ByteBuffer.wrap(output);
        static final int baseSize = 10;
        int size;
        int id;
        int type;
        byte[] body;

        RCONMessage() {
        }

        RCONMessage(int int0, int int1, String string) throws UnsupportedEncodingException {
            this.id = int0;
            this.type = int1;
            this.body = string.getBytes();
            this.size = 10 + string.length();
        }

        private void writeObject(OutputStream outputStream) throws IOException {
            bbw.putInt(this.size);
            bbw.putInt(this.id);
            bbw.putInt(this.type);
            bbw.put(this.body);
            bbw.put((byte)0);
            bbw.put((byte)0);
            outputStream.write(output, 0, this.size + 4);
            bbw.clear();
        }

        private void readObject(InputStream inputStream, int int0) throws IOException {
            if (int0 == 0) {
                inputStream.read(input);
            } else {
                inputStream.read(input, 0, int0);
            }

            this.size = bbr.getInt();
            this.id = bbr.getInt();
            this.type = bbr.getInt();
            if (this.size > 10) {
                this.body = new byte[this.size - 10];
                bbr.get(this.body, 0, this.size - 10);
            }

            bbr.get();
            bbr.get();
            bbr.clear();
        }

        static {
            bbr.order(ByteOrder.LITTLE_ENDIAN);
            bbw.order(ByteOrder.LITTLE_ENDIAN);
        }
    }
}
