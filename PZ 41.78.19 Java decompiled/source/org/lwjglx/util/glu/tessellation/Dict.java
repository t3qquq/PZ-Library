// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class Dict {
    DictNode head;
    Object frame;
    Dict.DictLeq leq;

    private Dict() {
    }

    static Dict dictNewDict(Object object, Dict.DictLeq dictLeq) {
        Dict dict = new Dict();
        dict.head = new DictNode();
        dict.head.key = null;
        dict.head.next = dict.head;
        dict.head.prev = dict.head;
        dict.frame = object;
        dict.leq = dictLeq;
        return dict;
    }

    static void dictDeleteDict(Dict dict) {
        dict.head = null;
        dict.frame = null;
        dict.leq = null;
    }

    static DictNode dictInsert(Dict dict, Object object) {
        return dictInsertBefore(dict, dict.head, object);
    }

    static DictNode dictInsertBefore(Dict dict, DictNode dictNode0, Object object) {
        do {
            dictNode0 = dictNode0.prev;
        } while (dictNode0.key != null && !dict.leq.leq(dict.frame, dictNode0.key, object));

        DictNode dictNode1 = new DictNode();
        dictNode1.key = object;
        dictNode1.next = dictNode0.next;
        dictNode0.next.prev = dictNode1;
        dictNode1.prev = dictNode0;
        dictNode0.next = dictNode1;
        return dictNode1;
    }

    static Object dictKey(DictNode dictNode) {
        return dictNode.key;
    }

    static DictNode dictSucc(DictNode dictNode) {
        return dictNode.next;
    }

    static DictNode dictPred(DictNode dictNode) {
        return dictNode.prev;
    }

    static DictNode dictMin(Dict dict) {
        return dict.head.next;
    }

    static DictNode dictMax(Dict dict) {
        return dict.head.prev;
    }

    static void dictDelete(Dict var0, DictNode dictNode) {
        dictNode.next.prev = dictNode.prev;
        dictNode.prev.next = dictNode.next;
    }

    static DictNode dictSearch(Dict dict, Object object) {
        DictNode dictNode = dict.head;

        do {
            dictNode = dictNode.next;
        } while (dictNode.key != null && !dict.leq.leq(dict.frame, object, dictNode.key));

        return dictNode;
    }

    public interface DictLeq {
        boolean leq(Object var1, Object var2, Object var3);
    }
}
