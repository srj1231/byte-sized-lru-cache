package com.saumya.lld.byte_sized_lru;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ByteSizedLruApplication {

	public static void main(String[] args) throws InterruptedException {
		// Test 1: Basic byte-sized LRU operations
		System.out.println("=== Test 1: Basic Operations ===");
		LRUCache<Integer, byte[]> cache = new LRUCache<>(20); // 20 bytes max

		cache.put(1, new byte[]{1});       // 1 byte
		cache.put(2, new byte[]{2, 2});    // 2 bytes
		cache.put(3, new byte[]{3, 3, 3}); // 3 bytes
		System.out.println(cache);          // Should show 6 bytes used

		System.out.println("Get key 1: " + java.util.Arrays.toString(cache.get(1)));
		System.out.println(cache);          // Key 1 moved to front

		cache.put(4, new byte[10]);         // 10 bytes - should evict to make space
		System.out.println(cache);          // Check eviction

		// Test 2: Reject item larger than maxBytes
		System.out.println("\n=== Test 2: Reject Oversized Item ===");
		LRUCache<Integer, byte[]> cache2 = new LRUCache<>(10);
		boolean result = cache2.put(1, new byte[20]); // 20 bytes > 10 max
		System.out.println("Put 20-byte item in 10-byte cache: " + result);
		System.out.println(cache2);

		// Test 3: Update existing key
		System.out.println("\n=== Test 3: Update Existing Key ===");
		LRUCache<Integer, byte[]> cache3 = new LRUCache<>(15);
		cache3.put(1, new byte[]{1});       // 1 byte
		cache3.put(2, new byte[]{2, 2});    // 2 bytes
		System.out.println(cache3);
		cache3.put(1, new byte[5]);          // Update key 1 to 5 bytes (delta +4)
		System.out.println(cache3);          // Should show 7 bytes total

		// Test 4: Concurrent access
		System.out.println("\n=== Test 4: Concurrent Access ===");
		ExecutorService pool = Executors.newFixedThreadPool(8);
		LRUCache<Integer, byte[]> c = new LRUCache<>(10_000);
		for (int t = 0; t < 8; t++) {
			pool.submit(() -> {
				for (int i = 0; i < 100_000; i++) {
					byte[] data = new byte[(i % 50) + 1]; // 1-50 bytes
					c.put(i % 500, data);
					c.get(i % 500);
				}
			});
		}
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.MINUTES);
		System.out.println("Concurrent test done, no exception");
		System.out.println("Final cache state: " + c);
	}

}
