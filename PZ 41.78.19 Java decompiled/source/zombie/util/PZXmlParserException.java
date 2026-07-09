// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

public final class PZXmlParserException extends Exception {
    public PZXmlParserException() {
    }

    public PZXmlParserException(String string) {
        super(string);
    }

    public PZXmlParserException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public PZXmlParserException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String toString() {
        String string0 = super.toString();
        String string1 = string0;
        Throwable throwable = this.getCause();
        if (throwable != null) {
            string1 = string0 + System.lineSeparator() + "  Caused by:" + System.lineSeparator() + "    " + throwable.toString();
        }

        return string1;
    }
}
