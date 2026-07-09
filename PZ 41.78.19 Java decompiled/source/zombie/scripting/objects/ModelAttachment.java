// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import java.util.ArrayList;
import org.joml.Vector3f;
import zombie.util.StringUtils;

public final class ModelAttachment {
    private String id;
    private final Vector3f offset = new Vector3f();
    private final Vector3f rotate = new Vector3f();
    private String bone;
    private ArrayList<String> canAttach;
    private float zoffset;
    private boolean updateConstraint = true;

    public ModelAttachment(String _id) {
        this.setId(_id);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String _id) {
        if (StringUtils.isNullOrWhitespace(_id)) {
            throw new IllegalArgumentException("ModelAttachment id is null or empty");
        } else {
            this.id = _id;
        }
    }

    public Vector3f getOffset() {
        return this.offset;
    }

    public Vector3f getRotate() {
        return this.rotate;
    }

    public String getBone() {
        return this.bone;
    }

    public void setBone(String _bone) {
        _bone = _bone.trim();
        this.bone = _bone.isEmpty() ? null : _bone;
    }

    public ArrayList<String> getCanAttach() {
        return this.canAttach;
    }

    public void setCanAttach(ArrayList<String> _canAttach) {
        this.canAttach = _canAttach;
    }

    public float getZOffset() {
        return this.zoffset;
    }

    public void setZOffset(float _zoffset) {
        this.zoffset = _zoffset;
    }

    public boolean isUpdateConstraint() {
        return this.updateConstraint;
    }

    public void setUpdateConstraint(boolean _updateConstraint) {
        this.updateConstraint = _updateConstraint;
    }
}
