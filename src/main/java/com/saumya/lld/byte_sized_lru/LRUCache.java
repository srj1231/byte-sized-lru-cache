package com.saumya.lld.byte_sized_lru;

import java.util.HashMap;
import java.util.Map;

class LRUCache<K, V> {

    private class Node {
        K key;
        V value;
        Node prev, next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node> map = new HashMap<>();
    private final Node head = new Node(null, null); // most recent side
    private final Node tail = new Node(null, null); // least recent side

    public LRUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public synchronized V get(K key) {
        Node node = map.get(key);
        if (node == null) return null;
        remove(node);
        addToFront(node);
        return node.value;
    }

    public synchronized void put(K key, V value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            remove(node);
            addToFront(node);
            return;
        }
        if (map.size() == capacity) {
            Node lru = tail.prev;
            remove(lru);
            map.remove(lru.key);
        }
        Node newNode = new Node(key, value);
        map.put(key, newNode);
        addToFront(newNode);
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

    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node current = head.next;
        boolean first = true;
        while (current != tail) {
            if (!first) sb.append(", ");
            sb.append(current.key).append(" = ").append(current.value);

            first = false;
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
