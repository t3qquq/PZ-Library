/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.profiler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import se.krka.kahlua.profiler.StacktraceCounter;
import se.krka.kahlua.profiler.StacktraceElement;

public class StacktraceNode {
    private final long time;
    private final StacktraceElement element;
    private final List<StacktraceNode> children;

    public StacktraceNode(StacktraceElement stacktraceElement, List<StacktraceNode> list, long l) {
        this.element = stacktraceElement;
        this.children = list;
        this.time = l;
    }

    public static StacktraceNode createFrom(StacktraceCounter stacktraceCounter, StacktraceElement stacktraceElement, int n, double d, int n2) {
        StacktraceNode stacktraceNode = new StacktraceNode(stacktraceElement, new ArrayList<StacktraceNode>(), stacktraceCounter.getTime());
        if (n > 0) {
            Map<StacktraceElement, StacktraceCounter> map = stacktraceCounter.getChildren();
            ArrayList<Map.Entry<StacktraceElement, StacktraceCounter>> arrayList = new ArrayList<Map.Entry<StacktraceElement, StacktraceCounter>>(map.entrySet());
            Collections.sort(arrayList, new Comparator<Map.Entry<StacktraceElement, StacktraceCounter>>(){

                @Override
                public int compare(Map.Entry<StacktraceElement, StacktraceCounter> entry, Map.Entry<StacktraceElement, StacktraceCounter> entry2) {
                    return Long.signum(entry2.getValue().getTime() - entry.getValue().getTime());
                }
            });
            for (int i = arrayList.size() - 1; i >= n2; --i) {
                arrayList.remove(i);
            }
            for (Map.Entry entry : arrayList) {
                StacktraceElement stacktraceElement2 = (StacktraceElement)entry.getKey();
                StacktraceCounter stacktraceCounter2 = (StacktraceCounter)entry.getValue();
                if (!((double)stacktraceCounter2.getTime() >= d * (double)stacktraceCounter.getTime())) continue;
                StacktraceNode stacktraceNode2 = StacktraceNode.createFrom(stacktraceCounter2, stacktraceElement2, n - 1, d, n2);
                stacktraceNode.children.add(stacktraceNode2);
            }
        }
        return stacktraceNode;
    }

    public void output(PrintWriter printWriter) {
        this.output(printWriter, "", this.time, this.time);
    }

    public void output(PrintWriter printWriter, String string, long l, long l2) {
        printWriter.println(String.format("%-40s   %4d ms   %5.1f%% of parent    %5.1f%% of total", string + this.element.name() + " (" + this.element.type() + ")", this.time, 100.0 * (double)this.time / (double)l, 100.0 * (double)this.time / (double)l2));
        String string2 = string + "  ";
        for (StacktraceNode stacktraceNode : this.children) {
            stacktraceNode.output(printWriter, string2, this.time, l2);
        }
    }
}

