// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class WinReqistry {
    public static String getSteamDirectory() {
        return readRegistry("HKEY_CURRENT_USER\\Software\\Valve\\Steam", "SteamPath");
    }

    public static final String readRegistry(String string1, String string0) {
        try {
            Process process = Runtime.getRuntime().exec("reg query \"" + string1 + "\" /v " + string0);
            WinReqistry.StreamReader streamReader = new WinReqistry.StreamReader(process.getInputStream());
            streamReader.start();
            process.waitFor();
            streamReader.join();
            String string2 = streamReader.getResult();
            if (string2 != null && !string2.equals("")) {
                string2 = string2.substring(string2.indexOf("REG_SZ") + 7).trim();
                String[] strings = string2.split("\t");
                return strings[strings.length - 1];
            } else {
                return null;
            }
        } catch (Exception exception) {
            return null;
        }
    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw = new StringWriter();

        public StreamReader(InputStream inputStream) {
            this.is = inputStream;
        }

        @Override
        public void run() {
            int int0;
            try {
                while ((int0 = this.is.read()) != -1) {
                    this.sw.write(int0);
                }
            } catch (IOException iOException) {
            }
        }

        public String getResult() {
            return this.sw.toString();
        }
    }
}
