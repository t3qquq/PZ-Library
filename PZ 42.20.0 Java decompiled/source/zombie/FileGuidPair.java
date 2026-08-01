// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "FileGuidPair")
public final class FileGuidPair {
    @XmlElement(name = "path")
    public String path;
    @XmlElement(name = "guid")
    public String guid;
}
