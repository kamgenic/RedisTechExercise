package com.example.demo;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.codec.Utf8StringCodec;
import io.lettuce.core.masterslave.MasterSlave;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SortTest {

	private static RedisCommands<String, String> sync;
	private static StatefulRedisClusterConnection<String, String> connection;
	private static RedisClusterClient client;

	@BeforeClass
	public static void setup() {
		RedisClient redisClient = RedisClient.create();

		StatefulRedisMasterSlaveConnection<String, String> connection
				= MasterSlave.connect(redisClient,
				new Utf8StringCodec(), RedisURI.create("redis://172.31.21.73:14335"));

//		connection.setReadFrom(ReadFrom.SLAVE);

		sync = connection.sync();
	}

	@AfterClass
	public static void tearDown() {
		connection.close();
		client.shutdown();
	}

	private static RedisStringCommands getSync(String uri) {
		RedisClient ent_client = RedisClient.create(uri);
		StatefulRedisConnection<String, String> connection = ent_client.connect();
		return connection.sync();
	}


	private List<String> sort(boolean reverse) {

		Set<String> keySet = Collections.singleton(sync.get("*"));
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
		IntStream.rangeClosed(1, num).forEach(i -> {
			int nextInt = rand.nextInt(1000);
			sync.set(String.valueOf(nextInt), String.valueOf(nextInt));
		});
	}

	private void insertList(List<Integer> nums) {
		Random rand = new Random();
		nums.forEach(i -> {
			sync.set(String.valueOf(i), String.valueOf(i));
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

		IntStream.rangeClosed(1, 5).forEach(i -> {
			int rand = new Random().nextInt(sorted.size());
			assertTrue(Integer.valueOf(sorted.get(rand)) < Integer.valueOf(sorted.get(rand+1)));
		});
	}
}
