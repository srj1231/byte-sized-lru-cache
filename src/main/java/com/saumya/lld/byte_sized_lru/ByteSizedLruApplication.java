package com.saumya.lld.byte_sized_lru;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ByteSizedLruApplication {

	public static void main(String[] args) throws InterruptedException {
		LRUCache<Integer, String> cache = new LRUCache<>(3);

		cache.put(1, "A");
		cache.put(2, "B");
		cache.put(3, "C");
		System.out.println(cache);               // [3=C, 2=B, 1=A]

		System.out.println(cache.get(1));        // A
		System.out.println(cache);               // [1=A, 3=C, 2=B]

		cache.put(4, "D");                       // evicts key 2
		System.out.println(cache);               // [4=D, 1=A, 3=C]
		System.out.println(cache.get(2));        // null

		cache.put(3, "C2");                      // update + move to front
		System.out.println(cache);               // [3=C2, 4=D, 1=A]

		cache.put(5, "E");                       // evicts key 1
		System.out.println(cache);               // [5=E, 3=C2, 4=D]

		// Concurrent test: 8 threads performing 100k operations each
		ExecutorService pool = Executors.newFixedThreadPool(8);
		LRUCache<Integer, Integer> c = new LRUCache<>(100);
		for (int t = 0; t < 8; t++) {
			pool.submit(() -> {
				for (int i = 0; i < 100_000; i++) {
					c.put(i % 500, i);  // put with 500 unique keys
					c.get(i % 500);     // get the same keys
				}
			});
		}
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.MINUTES);
		System.out.println("done, no exception");
	}

}
