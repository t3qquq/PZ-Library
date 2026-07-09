// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting;

import java.util.Stack;

public final class ScriptParsingUtils {
    public static String[] SplitExceptInbetween(String string0, String string1, String string2) {
        Stack stack = new Stack();
        boolean boolean0 = false;

        while (string0.contains(string1)) {
            if (!boolean0) {
                int int0 = string0.indexOf(string1);
                int int1 = string0.indexOf(string2);
                if (int1 == -1) {
                    String[] strings0 = string0.split(string1);

                    for (int int2 = 0; int2 < strings0.length; int2++) {
                        stack.add(strings0[int2].trim());
                    }

                    strings0 = new String[stack.size()];

                    for (int int3 = 0; int3 < stack.size(); int3++) {
                        strings0[int3] = (String)stack.get(int3);
                    }

                    return strings0;
                }

                if (int0 == -1) {
                    String[] strings1 = new String[stack.size()];
                    if (!string0.trim().isEmpty()) {
                        stack.add(string0.trim());
                    }

                    for (int int4 = 0; int4 < stack.size(); int4++) {
                        strings1[int4] = (String)stack.get(int4);
                    }

                    return strings1;
                }

                if (int0 < int1) {
                    stack.add(string0.substring(0, int0));
                    string0 = string0.substring(int0 + 1);
                } else {
                    boolean0 = true;
                }
            } else {
                int int5 = string0.indexOf(string2);
                int int6 = string0.indexOf(string2);
                int int7 = string0.indexOf(string2, string0.indexOf(string2) + 1);
                int int8 = string0.indexOf(string1, int7 + 1);
                if (int8 == -1) {
                    break;
                }

                String string3 = string0.substring(0, int8).trim();
                if (!string3.isEmpty()) {
                    stack.add(string3);
                }

                string0 = string0.substring(int8 + 1);
                boolean0 = false;
            }
        }

        if (!string0.trim().isEmpty()) {
            stack.add(string0.trim());
        }

        String[] strings2 = new String[stack.size()];

        for (int int9 = 0; int9 < stack.size(); int9++) {
            strings2[int9] = (String)stack.get(int9);
        }

        return strings2;
    }

    public static String[] SplitExceptInbetween(String string0, String string2, String string1, String string3) {
        int int0 = 0;
        int int1 = 0;
        int int2 = 0;
        int int3 = 0;
        int3 = 0;
        int0 = 0;
        int1 = 0;
        int2 = 0;
        Stack stack = new Stack();
        if (string0.indexOf(string1, int0) == -1) {
            return string0.split(string2);
        } else {
            do {
                int0 = string0.indexOf(string1, int0 + 1);
                int1 = string0.indexOf(string3, int1 + 1);
                int2 = string0.indexOf(string2, int2 + 1);
                if (int2 == -1) {
                    stack.add(string0.trim());
                    string0 = "";
                } else if ((int2 < int0 || int0 == -1 && int2 != -1) && int3 == 0) {
                    stack.add(string0.substring(0, int2));
                    string0 = string0.substring(int2 + 1);
                    int0 = 0;
                    int1 = 0;
                    int2 = 0;
                } else if ((int1 >= int0 || int1 == -1) && int0 != -1) {
                    if (int0 != -1 && int1 == -1) {
                        int1 = int0;
                        int3++;
                    } else if (int0 != -1 && int1 != -1 && int0 < int1 && (int0 > int2 || int1 < int2)) {
                        stack.add(string0.substring(0, int2));
                        string0 = string0.substring(int2 + 1);
                        int0 = 0;
                        int1 = 0;
                        int2 = 0;
                    }
                } else {
                    int0 = int1;
                    if (--int3 == 0) {
                        stack.add(string0.substring(0, int1 + 1));
                        string0 = string0.substring(int1 + 1);
                        int0 = 0;
                        int1 = 0;
                        int2 = 0;
                    }
                }
            } while (string0.trim().length() > 0);

            if (!string0.trim().isEmpty()) {
                stack.add(string0.trim());
            }

            String[] strings = new String[stack.size()];

            for (int int4 = 0; int4 < stack.size(); int4++) {
                strings[int4] = ((String)stack.get(int4)).trim();
            }

            return strings;
        }
    }
}
