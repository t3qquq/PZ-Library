// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
final class DebugOptionsXml {
    public boolean setDebugMode = false;
    public boolean debugMode = true;
    public final ArrayList<DebugOptionsXml.OptionNode> options = new ArrayList<>();

    public static final class OptionNode {
        public String name;
        public boolean value;

        public OptionNode() {
        }

        public OptionNode(String string, boolean boolean0) {
            this.name = string;
            this.value = boolean0;
        }
    }
}
