// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public final class FileGuidTable {
    public final ArrayList<FileGuidPair> files = new ArrayList<>();
    @XmlTransient
    private final Map<String, String> guidToPath = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    @XmlTransient
    private final Map<String, String> pathToGuid = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public void setModID(String string) {
        for (FileGuidPair fileGuidPair : this.files) {
            fileGuidPair.guid = string + "-" + fileGuidPair.guid;
        }
    }

    public void mergeFrom(FileGuidTable fileGuidTable0) {
        this.files.addAll(fileGuidTable0.files);
    }

    public void loaded() {
        for (FileGuidPair fileGuidPair : this.files) {
            this.guidToPath.put(fileGuidPair.guid, fileGuidPair.path);
            this.pathToGuid.put(fileGuidPair.path, fileGuidPair.guid);
        }
    }

    public void clear() {
        this.files.clear();
        this.guidToPath.clear();
        this.pathToGuid.clear();
    }

    public String getFilePathFromGuid(String string) {
        return this.guidToPath.get(string);
    }

    public String getGuidFromFilePath(String string) {
        return this.pathToGuid.get(string);
    }
}
