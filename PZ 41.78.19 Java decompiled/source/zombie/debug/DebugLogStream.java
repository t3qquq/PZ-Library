// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

import java.io.PrintStream;
import zombie.core.Core;
import zombie.util.StringUtils;

public final class DebugLogStream extends PrintStream {
    private final PrintStream m_wrappedStream;
    private final PrintStream m_wrappedWarnStream;
    private final PrintStream m_wrappedErrStream;
    private final IDebugLogFormatter m_formatter;
    public static final String s_prefixErr = "ERROR: ";
    public static final String s_prefixWarn = "WARN : ";
    public static final String s_prefixOut = "LOG  : ";
    public static final String s_prefixDebug = "DEBUG: ";
    public static final String s_prefixTrace = "TRACE: ";

    public DebugLogStream(PrintStream out, PrintStream warn, PrintStream err, IDebugLogFormatter formatter) {
        super(out);
        this.m_wrappedStream = out;
        this.m_wrappedWarnStream = warn;
        this.m_wrappedErrStream = err;
        this.m_formatter = formatter;
    }

    private void write(PrintStream printStream, String string1) {
        String string0 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string1);
        if (string0 != null) {
            printStream.print(string0);
        }
    }

    private void writeln(PrintStream printStream, String string) {
        this.writeln(printStream, LogSeverity.General, "LOG  : ", string);
    }

    private void writeln(PrintStream printStream, String string, Object object) {
        this.writeln(printStream, LogSeverity.General, "LOG  : ", string, object);
    }

    private void writeln(PrintStream printStream, LogSeverity logSeverity, String string1, String string2) {
        String string0 = this.m_formatter.format(logSeverity, string1, "", string2);
        if (string0 != null) {
            printStream.println(string0);
        }
    }

    private void writeln(PrintStream printStream, LogSeverity logSeverity, String string1, String string2, Object object) {
        String string0 = this.m_formatter.format(logSeverity, string1, "", string2, object);
        if (string0 != null) {
            printStream.println(string0);
        }
    }

    /**
     * Returns the class name and method name prefix of the calling code.
     */
    public static String generateCallerPrefix() {
        StackTraceElement stackTraceElement = tryGetCallerTraceElement(4);
        return stackTraceElement == null ? "(UnknownStack)" : getStackTraceElementString(stackTraceElement, false);
    }

    public static StackTraceElement tryGetCallerTraceElement(int depthIdx) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        return stackTraceElements.length <= depthIdx ? null : stackTraceElements[depthIdx];
    }

    public static String getStackTraceElementString(StackTraceElement stackTraceElement, boolean includeLineNo) {
        if (stackTraceElement == null) {
            return "(UnknownStack)";
        } else {
            String string0 = getUnqualifiedClassName(stackTraceElement.getClassName());
            String string1 = stackTraceElement.getMethodName();
            int int0 = stackTraceElement.getLineNumber();
            String string2;
            if (stackTraceElement.isNativeMethod()) {
                string2 = " (Native Method)";
            } else if (includeLineNo && int0 > -1) {
                string2 = " line:" + int0;
            } else {
                string2 = "";
            }

            return string0 + "." + string1 + string2;
        }
    }

    public static String getTopStackTraceString(Throwable ex) {
        if (ex == null) {
            return "Null Exception";
        } else {
            StackTraceElement[] stackTraceElements = ex.getStackTrace();
            if (stackTraceElements != null && stackTraceElements.length != 0) {
                StackTraceElement stackTraceElement = stackTraceElements[0];
                return getStackTraceElementString(stackTraceElement, true);
            } else {
                return "No Stack Trace Available";
            }
        }
    }

    public void printStackTrace() {
        this.printStackTrace(0, null);
    }

    public void printStackTrace(String message) {
        this.printStackTrace(0, message);
    }

    public void printStackTrace(int depth, String message) {
        if (message != null) {
            this.println(message);
        }

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int int0 = depth == 0 ? stackTraceElements.length : Math.min(depth, stackTraceElements.length);

        for (int int1 = 0; int1 < int0; int1++) {
            StackTraceElement stackTraceElement = stackTraceElements[int1];
            this.println("\t" + stackTraceElement.toString());
        }
    }

    private static String getUnqualifiedClassName(String string1) {
        String string0 = string1;
        int int0 = string1.lastIndexOf(46);
        if (int0 > -1 && int0 < string1.length() - 1) {
            string0 = string1.substring(int0 + 1);
        }

        return string0;
    }

    /**
     * Similar to println. Prepends the calling method and class name to the output string.
     */
    public void debugln(String str) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", "%s", str);
            this.m_wrappedStream.println(string1);
        }
    }

    /**
     * Similar to println. Prepends the calling method and class name to the output string.  Uses
     * 
     * @param format The string format to be printed
     * @param param0
     */
    public void debugln(String format, Object param0) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0);
            this.m_wrappedStream.println(string1);
        }
    }

    /**
     * Similar to println. Prepends the calling method and class name to the output string.  Uses
     * 
     * @param format The string format to be printed
     * @param param0
     * @param param1
     */
    public void debugln(String format, Object param0, Object param1) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1);
            this.m_wrappedStream.println(string1);
        }
    }

    /**
     * Similar to println. Prepends the calling method and class name to the output string.  Uses
     * 
     * @param format The string format to be printed
     * @param param0
     * @param param1
     * @param param2
     */
    public void debugln(String format, Object param0, Object param1, Object param2) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2);
            this.m_wrappedStream.println(string1);
        }
    }

    /**
     * Similar to println. Prepends the calling method and class name to the output string.  Uses
     * 
     * @param format The string format to be printed
     * @param param0
     * @param param1
     * @param param2
     * @param param3
     */
    public void debugln(String format, Object param0, Object param1, Object param2, Object param3) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3);
            this.m_wrappedStream.println(string1);
        }
    }

    /**
     * Similar to println. Prepends the calling method and class name to the output string.  Uses
     * 
     * @param format The string format to be printed
     * @param param0
     * @param param1
     * @param param2
     * @param param3
     * @param param4
     */
    public void debugln(String format, Object param0, Object param1, Object param2, Object param3, Object param4) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3, param4);
            this.m_wrappedStream.println(string1);
        }
    }

    /**
     * Similar to println. Prepends the calling method and class name to the output string.  Uses
     * 
     * @param format The string format to be printed
     * @param param0
     * @param param1
     * @param param2
     * @param param3
     * @param param4
     * @param param5
     */
    public void debugln(String format, Object param0, Object param1, Object param2, Object param3, Object param4, Object param5) {
        if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3, param4, param5);
            this.m_wrappedStream.println(string1);
        }
    }

    /**
     * Prints a boolean value.  The string produced by
     * 
     * @param b The boolean to be printed
     */
    @Override
    public void print(boolean b) {
        this.write(this.m_wrappedStream, b ? "true" : "false");
    }

    /**
     * Prints a character.  The character is translated into one or more bytes  according to the platform's default character encoding, and these bytes  are written in exactly the manner of the
     * 
     * @param c The char to be printed
     */
    @Override
    public void print(char c) {
        this.write(this.m_wrappedStream, String.valueOf(c));
    }

    /**
     * Prints an integer.  The string produced by
     * 
     * @param i The int to be printed
     */
    @Override
    public void print(int i) {
        this.write(this.m_wrappedStream, String.valueOf(i));
    }

    /**
     * Prints a long integer.  The string produced by
     * 
     * @param l The long to be printed
     */
    @Override
    public void print(long l) {
        this.write(this.m_wrappedStream, String.valueOf(l));
    }

    /**
     * Prints a floating-point number.  The string produced by
     * 
     * @param f The float to be printed
     */
    @Override
    public void print(float f) {
        this.write(this.m_wrappedStream, String.valueOf(f));
    }

    /**
     * Prints a double-precision floating-point number.  The string produced by
     * 
     * @param d The double to be printed
     */
    @Override
    public void print(double d) {
        this.write(this.m_wrappedStream, String.valueOf(d));
    }

    /**
     * Prints a string.  If the argument is
     * 
     * @param s The String to be printed
     */
    @Override
    public void print(String s) {
        this.write(this.m_wrappedStream, String.valueOf(s));
    }

    /**
     * Prints an object.  The string produced by the
     * 
     * @param obj The Object to be printed
     */
    @Override
    public void print(Object obj) {
        this.write(this.m_wrappedStream, String.valueOf(obj));
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        this.write(this.m_wrappedStream, String.format(format, args));
        return this;
    }

    /**
     * Terminates the current line by writing the line separator string.  The  line separator string is defined by the system property
     */
    @Override
    public void println() {
        this.writeln(this.m_wrappedStream, "");
    }

    /**
     * Prints a boolean and then terminate the line.  This method behaves as  though it invokes
     * 
     * @param x The boolean to be printed
     */
    @Override
    public void println(boolean x) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(x));
    }

    /**
     * Prints a character and then terminate the line.  This method behaves as  though it invokes
     * 
     * @param x The char to be printed.
     */
    @Override
    public void println(char x) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(x));
    }

    /**
     * Prints an integer and then terminate the line.  This method behaves as  though it invokes
     * 
     * @param x The int to be printed.
     */
    @Override
    public void println(int x) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(x));
    }

    /**
     * Prints a long and then terminate the line.  This method behaves as  though it invokes
     * 
     * @param x a The long to be printed.
     */
    @Override
    public void println(long x) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(x));
    }

    /**
     * Prints a float and then terminate the line.  This method behaves as  though it invokes
     * 
     * @param x The float to be printed.
     */
    @Override
    public void println(float x) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(x));
    }

    /**
     * Prints a double and then terminate the line.  This method behaves as  though it invokes
     * 
     * @param x The double to be printed.
     */
    @Override
    public void println(double x) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(x));
    }

    /**
     * Prints a character and then terminate the line.  This method behaves as  though it invokes
     * 
     * @param x The char to be printed.
     */
    @Override
    public void println(char[] x) {
        this.writeln(this.m_wrappedStream, "%s", String.valueOf(x));
    }

    /**
     * Prints a String and then terminate the line.  This method behaves as  though it invokes
     * 
     * @param x The String to be printed.
     */
    @Override
    public void println(String x) {
        this.writeln(this.m_wrappedStream, "%s", x);
    }

    /**
     * Prints an Object and then terminate the line.  This method calls  at first String.valueOf(x) to get the printed object's string value,  then behaves as  though it invokes
     * 
     * @param x The Object to be printed.
     */
    @Override
    public void println(Object x) {
        this.writeln(this.m_wrappedStream, "%s", x);
    }

    public void println(String format, Object param0) {
        String string = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", format, param0);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    public void println(String format, Object param0, Object param1) {
        String string = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", format, param0, param1);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    public void println(String format, Object param0, Object param1, Object param2) {
        String string = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", format, param0, param1, param2);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    public void println(String format, Object param0, Object param1, Object param2, Object param3) {
        String string = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", format, param0, param1, param2, param3);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    public void println(String format, Object param0, Object param1, Object param2, Object param3, Object param4) {
        String string = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", format, param0, param1, param2, param3, param4);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    public void println(String format, Object param0, Object param1, Object param2, Object param3, Object param4, Object param5) {
        String string = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", format, param0, param1, param2, param3, param4, param5);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    public void println(String format, Object param0, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6) {
        String string = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", format, param0, param1, param2, param3, param4, param5, param6);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    public void println(String format, Object param0, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6, Object param7) {
        String string = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", format, param0, param1, param2, param3, param4, param5, param6, param7);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    public void println(
        String format, Object param0, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6, Object param7, Object param8
    ) {
        String string = this.m_formatter
            .format(LogSeverity.General, "LOG  : ", "", format, param0, param1, param2, param3, param4, param5, param6, param7, param8);
        if (string != null) {
            this.m_wrappedStream.println(string);
        }
    }

    /**
     * Prints an object to the Error stream.  The string produced by the
     * 
     * @param obj The Object to be printed
     */
    public void error(Object obj) {
        this.writeln(this.m_wrappedErrStream, LogSeverity.Error, "ERROR: ", generateCallerPrefix() + "> " + obj);
    }

    /**
     * Prints an object to the Error stream, using
     * 
     * @param format The string format to be printed
     * @param params The list of parameters to be inserted in the string.
     */
    public void error(String format, Object... params) {
        this.writeln(this.m_wrappedErrStream, LogSeverity.Error, "ERROR: ", generateCallerPrefix() + "> " + String.format(format, params));
    }

    /**
     * Prints an object to the Warning stream.  The string produced by the
     * 
     * @param obj The Object to be printed
     */
    public void warn(Object obj) {
        this.writeln(this.m_wrappedWarnStream, LogSeverity.Warning, "WARN : ", generateCallerPrefix() + "> " + obj);
    }

    /**
     * Prints an object to the Warning stream.  The string produced by the
     * 
     * @param format The string format to be printed
     * @param params The list of parameters to be inserted in the string.
     */
    public void warn(String format, Object... params) {
        this.writeln(this.m_wrappedWarnStream, LogSeverity.Warning, "WARN : ", generateCallerPrefix() + "> " + String.format(format, params));
    }

    public void printUnitTest(String format, boolean pass, Object... params) {
        if (!pass) {
            this.error(format + ", fail", params);
        } else {
            this.println(format + ", pass", params);
        }
    }

    public void printException(Throwable ex, String errorMessage, LogSeverity severity) {
        this.printException(ex, errorMessage, generateCallerPrefix(), severity);
    }

    public void printException(Throwable ex, String errorMessage, String callerPrefix, LogSeverity severity) {
        if (ex == null) {
            this.warn("Null exception passed.");
        } else {
            String string;
            PrintStream printStream;
            boolean boolean0;
            switch (severity) {
                case Trace:
                case General:
                    string = "LOG  : ";
                    printStream = this.m_wrappedStream;
                    boolean0 = false;
                    break;
                case Warning:
                    string = "WARN : ";
                    printStream = this.m_wrappedWarnStream;
                    boolean0 = false;
                    break;
                default:
                    this.error("Unhandled LogSeverity: %s. Defaulted to Error.", String.valueOf(severity));
                case Error:
                    string = "ERROR: ";
                    printStream = this.m_wrappedErrStream;
                    boolean0 = true;
            }

            if (errorMessage != null) {
                this.writeln(
                    printStream,
                    severity,
                    string,
                    String.format("%s> Exception thrown %s at %s. Message: %s", callerPrefix, ex.toString(), getTopStackTraceString(ex), errorMessage)
                );
            } else {
                this.writeln(
                    printStream, severity, string, String.format("%s> Exception thrown %s at %s.", callerPrefix, ex.toString(), getTopStackTraceString(ex))
                );
            }

            if (boolean0) {
                this.error("Stack trace:");
                ex.printStackTrace(printStream);
            }
        }
    }

    public void noise(String str) {
        if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", "%s", str);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void noise(String format, Object param0) {
        if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void noise(String format, Object param0, Object param1) {
        if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void noise(String format, Object param0, Object param1, Object param2) {
        if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void noise(String format, Object param0, Object param1, Object param2, Object param3) {
        if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void noise(String format, Object param0, Object param1, Object param2, Object param3, Object param4) {
        if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3, param4);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void noise(String format, Object param0, Object param1, Object param2, Object param3, Object param4, Object param5) {
        if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3, param4, param5);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void trace(String str) {
        if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string0, 36) + "> ", "%s", str);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void trace(String format, Object param0) {
        if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void trace(String format, Object param0, Object param1) {
        if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void trace(String format, Object param0, Object param1, Object param2) {
        if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void trace(String format, Object param0, Object param1, Object param2, Object param3) {
        if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void trace(String format, Object param0, Object param1, Object param2, Object param3, Object param4) {
        if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3, param4);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }

    public void trace(String format, Object param0, Object param1, Object param2, Object param3, Object param4, Object param5) {
        if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
            String string0 = generateCallerPrefix();
            String string1 = this.m_formatter
                .format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string0, 36) + "> ", format, param0, param1, param2, param3, param4, param5);
            if (string1 != null) {
                this.m_wrappedStream.println(string1);
            }
        }
    }
}
