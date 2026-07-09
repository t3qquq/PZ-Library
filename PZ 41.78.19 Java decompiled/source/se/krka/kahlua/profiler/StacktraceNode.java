// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.profiler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StacktraceNode {
    private final long time;
    private final StacktraceElement element;
    private final List<StacktraceNode> children;

    public StacktraceNode(StacktraceElement stacktraceElement, List<StacktraceNode> list, long long0) {
        this.element = stacktraceElement;
        this.children = list;
        this.time = long0;
    }

    public static StacktraceNode createFrom(StacktraceCounter stacktraceCounter0, StacktraceElement stacktraceElement0, int int0, double double0, int int2) {
        StacktraceNode stacktraceNode0 = new StacktraceNode(stacktraceElement0, new ArrayList<>(), stacktraceCounter0.getTime());
        if (int0 > 0) {
            Map map = stacktraceCounter0.getChildren();
            ArrayList arrayList = new ArrayList(map.entrySet());
            Collections.sort(arrayList, new Comparator<Entry<StacktraceElement, StacktraceCounter>>() {
                public int compare(Entry<StacktraceElement, StacktraceCounter> entry0, Entry<StacktraceElement, StacktraceCounter> entry1) {
                    return Long.signum(((StacktraceCounter)entry1.getValue()).getTime() - ((StacktraceCounter)entry0.getValue()).getTime());
                }
            });

            for (int int1 = arrayList.size() - 1; int1 >= int2; int1--) {
                arrayList.remove(int1);
            }

            for (Entry entry : arrayList) {
                StacktraceElement stacktraceElement1 = (StacktraceElement)entry.getKey();
                StacktraceCounter stacktraceCounter1 = (StacktraceCounter)entry.getValue();
                if (stacktraceCounter1.getTime() >= double0 * stacktraceCounter0.getTime()) {
                    StacktraceNode stacktraceNode1 = createFrom(stacktraceCounter1, stacktraceElement1, int0 - 1, double0, int2);
                    stacktraceNode0.children.add(stacktraceNode1);
                }
            }
        }

        return stacktraceNode0;
    }

    public void output(PrintWriter printWriter) {
        this.output(printWriter, "", this.time, this.time);
    }

    public void output(PrintWriter printWriter, String string0, long long1, long long0) {
        printWriter.println(
            String.format(
                "%-40s   %4d ms   %5.1f%% of parent    %5.1f%% of total",
                string0 + this.element.name() + " (" + this.element.type() + ")",
                this.time,
                100.0 * this.time / long1,
                100.0 * this.time / long0
            )
        );
        String string1 = string0 + "  ";

        for (StacktraceNode stacktraceNode1 : this.children) {
            stacktraceNode1.output(printWriter, string1, this.time, long0);
        }
    }
}
