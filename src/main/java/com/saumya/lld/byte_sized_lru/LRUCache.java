package com.saumya.lld.byte_sized_lru;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class LRUCache<K, V> {

    private class Node {
        K key;
        byte[] value;
        Node prev, next;

        Node(K key, byte[] value) {
            this.key = key;
            this.value = value;
        }
    }

    private final long maxBytes;
    private long currentBytes = 0;
    private final Map<K, Node> map = new HashMap<>();
    private final Node head = new Node(null, null); // most recent side
    private final Node tail = new Node(null, null); // least recent side

    public LRUCache(long maxBytes) {
        if (maxBytes <= 0) throw new IllegalArgumentException("maxBytes must be > 0");
        this.maxBytes = maxBytes;
        head.next = tail;
        tail.prev = head;
    }

    public synchronized byte[] get(K key) {
        Node node = map.get(key);
        if (node == null) return null;
        remove(node);
        addToFront(node);
        return node.value;
    }

    public synchronized boolean put(K key, byte[] value) {
        long size = value.length;
        if (size > maxBytes) return false;       // reject items that can never fit

        Node existing = map.get(key);
        if (existing != null) {   // update adjusts by delta
            currentBytes -= existing.value.length;
            remove(existing);
            map.remove(key);
        }

        while (currentBytes + size > maxBytes) { // evict until it fits
            Node lru = tail.prev;
            remove(lru);
            map.remove(lru.key);
            currentBytes -= lru.value.length;
        }

        Node node = new Node(key, value);
        map.put(key, node);
        addToFront(node);
        currentBytes += size;
        return true;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void addToFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public synchronized long usedBytes() {
        return currentBytes;
    }

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node current = head.next;
        boolean first = true;
        while (current != tail) {
            if (!first) sb.append(", ");
            sb.append(current.key).append(" = ").append(Arrays.toString(current.value));

            first = false;
            current = current.next;
        }
        sb.append("]");
        return sb + " (" + usedBytes() + " bytes)";
    }
}
