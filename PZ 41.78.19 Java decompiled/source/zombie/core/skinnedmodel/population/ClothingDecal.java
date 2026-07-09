// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.util.StringUtils;

@XmlRootElement
public class ClothingDecal {
    @XmlTransient
    public String name;
    public String texture;
    public int x;
    public int y;
    public int width;
    public int height;

    public boolean isValid() {
        return !StringUtils.isNullOrWhitespace(this.texture) && this.x >= 0 && this.y >= 0 && this.width > 0 && this.height > 0;
    }
}
