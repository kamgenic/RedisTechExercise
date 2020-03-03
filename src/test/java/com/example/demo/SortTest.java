package com.example.demo;

import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SortTest {

	private static Jedis oss_jedis;
	private static Jedis ent_jedis;

	@BeforeClass
	public static void setup() {
		oss_jedis = new Jedis("52.91.75.7", 10001);
		oss_jedis.auth("mypass");

		ent_jedis = new Jedis("54.81.188.194", 10001);
		ent_jedis.auth("mypass");

		oss_jedis.flushAll();
	}


	private List<String> sort(boolean reverse) {

		Set<String> keySet = ent_jedis.keys("*");
		List<String> keySorted = new ArrayList<String>(keySet) ;        //set -> list

		//Sort the list
		Collections.sort(keySorted, new Comparator<String>() {
			public int compare(String k1, String k2) {
				return Integer.valueOf(k1) > Integer.valueOf(k2) ? 1 : Integer.valueOf(k1) < Integer.valueOf(k2) ? -1 : 0;
			}
		});

		if (reverse) {
			Collections.reverse(keySorted);
		}

		//keySorted.forEach((key) -> System.out.println("KEY: " + key + " VALUE: " + jedis.get(key)));
		return keySorted;
	}

	private void insertRandom(int num) {
		Random rand = new Random();
		oss_jedis.flushAll();
		IntStream.rangeClosed(1, num).forEach(i -> {
			int nextInt = rand.nextInt(1000);
			oss_jedis.set(String.valueOf(nextInt), String.valueOf(nextInt));
		});
	}

	private void insertList(List<Integer> nums) {
		Random rand = new Random();
		oss_jedis.flushAll();
		nums.forEach(i -> {
			oss_jedis.set(String.valueOf(i), String.valueOf(i));
		});
	}



	@Test
	public void no_keys() {
		insertRandom(0);
		assertTrue(sort(false).isEmpty());
	}

	@Test
	public void one_key() {
		insertRandom(1);
		assertEquals(1, sort(false).size());
	}

	@Test
	public void three_random_keys() {
		insertRandom(3);
		assertEquals(3, sort(false).size());
	}

	@Test
	public void sort_five_keys() {
		insertList(Arrays.asList(10,1,9,5,3));

		List<String> sorted = sort(false);
		assertEquals(5, sorted.size());

		List<String> expected = Arrays.asList("1", "3", "5", "9", "10");
		assertEquals(expected, sorted);
	}

	@Test
	public void sort_five_keys_reverse() {
		insertList(Arrays.asList(10,1,9,5,3));

		List<String> sorted = sort(true);
		assertEquals(5, sorted.size());

		List<String> expected = Arrays.asList("10", "9", "5", "3", "1");
		assertEquals(expected, sorted);
	}

	@Test
	public void sort_lotta_keys() {
		int num = 10;
		insertRandom(num);

		List<String> sorted = sort(false);
//INDETERMINISTIC!!		assertEquals(num, sorted.size());

		IntStream.rangeClosed(1, 3).forEach(i -> {
			int rand = new Random().nextInt(sorted.size());
			assertTrue(Integer.valueOf(sorted.get(rand)) < Integer.valueOf(sorted.get(rand+1)));
		});
	}
}
