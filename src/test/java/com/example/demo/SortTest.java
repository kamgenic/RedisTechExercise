package com.example.demo;

import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SortTest {

	private static Jedis write_node;
	private static Jedis read_node;

	@BeforeClass
	public static void setup() {
		write_node = new Jedis("52.91.75.7", 10001);
		write_node.auth("mypass");

		read_node = new Jedis("54.81.188.194", 10001);
		read_node.auth("mypass");

		write_node.flushAll();
	}


	private List<String> sort(boolean reverse) {

		Set<String> keySet = read_node.keys("*");
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

		return keySorted;
	}

	private void insertRandom(int num) {
		Random rand = new Random();
		write_node.flushAll();
		IntStream.rangeClosed(1, num).forEach(i -> {
			int nextInt = rand.nextInt(1000);
			write_node.set(String.valueOf(nextInt), String.valueOf(nextInt));
		});
	}

	private void insertList(List<Integer> nums) {
		write_node.flushAll();
		nums.forEach(i -> {
			write_node.set(String.valueOf(i), String.valueOf(i));
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
