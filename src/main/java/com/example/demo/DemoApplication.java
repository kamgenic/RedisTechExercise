package com.example.demo;

import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.IntStream;

public class DemoApplication {

	private static volatile RedisClient instance = null;

	public static void main(String[] args) {
		Jedis jedis = new Jedis("34.229.147.134", 10001);
//		RedisClient jedis = getInstance("34.229.147.134", 10001);

		Random rand = new Random();

		IntStream.rangeClosed(1, 10).forEach(i -> jedis.set("K" + rand.nextInt(1000), "TEST"));

		Set<String> keySet = jedis.keys("K*");

		List<String> keySorted = new ArrayList<String>(keySet) ;        //set -> list

		//Sort the list
		Collections.sort(keySorted);
		Collections.reverse(keySorted);

		keySorted.forEach((key) -> System.out.println("KEY: " + key + " VALUE: " + jedis.get(key)));

//		List<String> sorted = jedis.sort("K");
//		sorted.forEach((key) -> System.out.println("VALUE: " + key));
	}


	public static RedisClient getInstance(String ip, final int port) {
		if (instance == null) {
			synchronized (RedisClient.class) {
				if (instance == null) {
					instance = new RedisClient(ip, port);
				}
			}
		}
		return instance;
	}


}
