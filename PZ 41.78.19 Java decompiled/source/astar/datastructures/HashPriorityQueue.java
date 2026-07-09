// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package astar.datastructures;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public class HashPriorityQueue<K, V> {
    HashMap<K, V> hashMap = new HashMap<>();
    TreeMap<V, K> treeMap;

    public HashPriorityQueue(Comparator<V> comparator) {
        this.treeMap = new TreeMap<>(comparator);
    }

    public int size() {
        return this.treeMap.size();
    }

    public boolean isEmpty() {
        return this.treeMap.isEmpty();
    }

    public boolean contains(K object) {
        return this.hashMap.containsKey(object);
    }

    public V get(K object) {
        return this.hashMap.get(object);
    }

    public boolean add(K object0, V object1) {
        this.hashMap.put((K)object0, (V)object1);
        this.treeMap.put((V)object1, (K)object0);
        return true;
    }

    public boolean remove(K object1, V object0) {
        if (object0 == null) {
            object0 = this.hashMap.get(object1);
        }

        this.hashMap.remove(object1);
        this.treeMap.remove(object0);
        return true;
    }

    public V poll() {
        Entry entry = this.treeMap.pollFirstEntry();
        return (V)entry.getKey();
    }

    public void clear() {
        this.hashMap.clear();
        this.treeMap.clear();
    }

    public HashMap<K, V> getHashMap() {
        return this.hashMap;
    }

    public TreeMap<V, K> getTreeMap() {
        return this.treeMap;
    }
}
