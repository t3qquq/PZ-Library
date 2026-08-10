// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.io.PrintStream;

public final class ProxyPrintStream extends PrintStream {
    private PrintStream fileStream = null;
    private PrintStream systemStream = null;

    public ProxyPrintStream(PrintStream printStream0, PrintStream printStream1) {
        super(printStream0);
        this.systemStream = printStream0;
        this.fileStream = printStream1;
    }

    @Override
    public void print(String string) {
        this.systemStream.print(string);
        this.fileStream.print(string);
        this.fileStream.flush();
    }

    @Override
    public void println(String string) {
        this.systemStream.println(string);
        this.fileStream.println(string);
        this.fileStream.flush();
    }

    @Override
    public void println(Object object) {
        this.systemStream.println(object);
        this.fileStream.println(object);
        this.fileStream.flush();
    }

    @Override
    public void write(byte[] bytes, int int0, int int1) {
        this.systemStream.write(bytes, int0, int1);
        this.fileStream.write(bytes, int0, int1);
        this.fileStream.flush();
    }

    @Override
    public void flush() {
        this.systemStream.flush();
        this.fileStream.flush();
    }
}
