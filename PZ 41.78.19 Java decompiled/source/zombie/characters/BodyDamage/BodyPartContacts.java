// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.BodyDamage;

import java.util.ArrayList;
import zombie.debug.DebugLog;

public final class BodyPartContacts {
    private static final BodyPartContacts.ContactNode root;
    private static final BodyPartContacts.ContactNode[] nodes;

    public static BodyPartType[] getAllContacts(BodyPartType bodyPartType) {
        for (int int0 = 0; int0 < nodes.length; int0++) {
            BodyPartContacts.ContactNode contactNode = nodes[int0];
            if (contactNode.bodyPart == bodyPartType) {
                return contactNode.bodyPartAllContacts;
            }
        }

        return null;
    }

    public static BodyPartType[] getChildren(BodyPartType bodyPartType) {
        for (int int0 = 0; int0 < nodes.length; int0++) {
            BodyPartContacts.ContactNode contactNode = nodes[int0];
            if (contactNode.bodyPart == bodyPartType) {
                return contactNode.bodyPartChildren;
            }
        }

        return null;
    }

    public static BodyPartType getParent(BodyPartType bodyPartType) {
        for (int int0 = 0; int0 < nodes.length; int0++) {
            BodyPartContacts.ContactNode contactNode = nodes[int0];
            if (contactNode.bodyPart == bodyPartType) {
                if (contactNode.depth == 0) {
                    DebugLog.log("Warning, root node parent is always null.");
                }

                return contactNode.bodyPartParent;
            }
        }

        return null;
    }

    public static int getNodeDepth(BodyPartType bodyPartType) {
        for (int int0 = 0; int0 < nodes.length; int0++) {
            BodyPartContacts.ContactNode contactNode = nodes[int0];
            if (contactNode.bodyPart == bodyPartType) {
                if (!contactNode.initialised) {
                    DebugLog.log("Warning: attempting to get depth for non initialised node '" + contactNode.bodyPart.toString() + "'.");
                }

                return contactNode.depth;
            }
        }

        return -1;
    }

    private static BodyPartContacts.ContactNode getNodeForBodyPart(BodyPartType bodyPartType) {
        for (int int0 = 0; int0 < nodes.length; int0++) {
            if (nodes[int0].bodyPart == bodyPartType) {
                return nodes[int0];
            }
        }

        return null;
    }

    private static void initNodes(BodyPartContacts.ContactNode contactNode1, int int0, BodyPartContacts.ContactNode contactNode0) {
        contactNode1.parent = contactNode0;
        contactNode1.depth = int0;
        ArrayList arrayList = new ArrayList();
        if (contactNode1.parent != null) {
            arrayList.add(contactNode1.parent);
        }

        if (contactNode1.children != null) {
            for (BodyPartContacts.ContactNode contactNode2 : contactNode1.children) {
                arrayList.add(contactNode2);
                initNodes(contactNode2, int0 + 1, contactNode1);
            }
        }

        contactNode1.allContacts = new BodyPartContacts.ContactNode[arrayList.size()];
        arrayList.toArray(contactNode1.allContacts);
        contactNode1.initialised = true;
    }

    private static void postInit() {
        for (BodyPartContacts.ContactNode contactNode : nodes) {
            if (contactNode.parent != null) {
                contactNode.bodyPartParent = contactNode.parent.bodyPart;
            }

            if (contactNode.children != null && contactNode.children.length > 0) {
                contactNode.bodyPartChildren = new BodyPartType[contactNode.children.length];

                for (int int0 = 0; int0 < contactNode.children.length; int0++) {
                    contactNode.bodyPartChildren[int0] = contactNode.children[int0].bodyPart;
                }
            } else {
                contactNode.bodyPartChildren = new BodyPartType[0];
            }

            if (contactNode.allContacts != null && contactNode.allContacts.length > 0) {
                contactNode.bodyPartAllContacts = new BodyPartType[contactNode.allContacts.length];

                for (int int1 = 0; int1 < contactNode.allContacts.length; int1++) {
                    contactNode.bodyPartAllContacts[int1] = contactNode.allContacts[int1].bodyPart;
                }
            } else {
                contactNode.bodyPartAllContacts = new BodyPartType[0];
            }

            if (!contactNode.initialised) {
                DebugLog.log("Warning: node for '" + contactNode.bodyPart.toString() + "' is not initialised!");
            }
        }
    }

    static {
        int int0 = BodyPartType.ToIndex(BodyPartType.MAX);
        nodes = new BodyPartContacts.ContactNode[int0];

        for (int int1 = 0; int1 < int0; int1++) {
            nodes[int1] = new BodyPartContacts.ContactNode(BodyPartType.FromIndex(int1));
        }

        root = getNodeForBodyPart(BodyPartType.Torso_Upper);
        root.children = new BodyPartContacts.ContactNode[]{
            getNodeForBodyPart(BodyPartType.Neck),
            getNodeForBodyPart(BodyPartType.Torso_Lower),
            getNodeForBodyPart(BodyPartType.UpperArm_L),
            getNodeForBodyPart(BodyPartType.UpperArm_R)
        };
        BodyPartContacts.ContactNode contactNode = getNodeForBodyPart(BodyPartType.Neck);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Head)};
        contactNode = getNodeForBodyPart(BodyPartType.UpperArm_L);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.ForeArm_L)};
        contactNode = getNodeForBodyPart(BodyPartType.ForeArm_L);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Hand_L)};
        contactNode = getNodeForBodyPart(BodyPartType.UpperArm_R);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.ForeArm_R)};
        contactNode = getNodeForBodyPart(BodyPartType.ForeArm_R);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Hand_R)};
        contactNode = getNodeForBodyPart(BodyPartType.Torso_Lower);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Groin)};
        contactNode = getNodeForBodyPart(BodyPartType.Groin);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.UpperLeg_L), getNodeForBodyPart(BodyPartType.UpperLeg_R)};
        contactNode = getNodeForBodyPart(BodyPartType.UpperLeg_L);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.LowerLeg_L)};
        contactNode = getNodeForBodyPart(BodyPartType.LowerLeg_L);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Foot_L)};
        contactNode = getNodeForBodyPart(BodyPartType.UpperLeg_R);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.LowerLeg_R)};
        contactNode = getNodeForBodyPart(BodyPartType.LowerLeg_R);
        contactNode.children = new BodyPartContacts.ContactNode[]{getNodeForBodyPart(BodyPartType.Foot_R)};
        initNodes(root, 0, null);
        postInit();
    }

    private static class ContactNode {
        BodyPartType bodyPart;
        int depth = -1;
        BodyPartContacts.ContactNode parent;
        BodyPartContacts.ContactNode[] children;
        BodyPartContacts.ContactNode[] allContacts;
        BodyPartType bodyPartParent;
        BodyPartType[] bodyPartChildren;
        BodyPartType[] bodyPartAllContacts;
        boolean initialised = false;

        public ContactNode(BodyPartType bodyPartType) {
            this.bodyPart = bodyPartType;
        }
    }
}
