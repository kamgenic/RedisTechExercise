package com.example.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class RedisClient {

    private static JedisPool jedisPool;

    public RedisClient(String ip, int port) {
        try {
            if (jedisPool == null) {
                jedisPool = new JedisPool(new URI("http://" + ip + ":" + port));
            }
        } catch (URISyntaxException e) {
            System.out.println("Malformed server address: " + e);
        }
    }

    public List lrange(final String key, final long start, final long stop) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, stop);
        } catch (Exception ex) {
            System.out.println("Exception caught in lrange: " + ex);
        }
        return new LinkedList();
    }

    public String  set(final String key, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        } catch (Exception ex) {
            System.out.println("Exception caught in set: " + ex);
        }
        return "";
    }

    public String  get(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception ex) {
            System.out.println("Exception caught in get: " + ex);
        }
        return "";
    }

    public Set<String> keys(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(key + "*");
        } catch (Exception ex) {
            System.out.println("Exception caught in get: " + ex);
        }
        return new HashSet<>();
    }

    public List<String> sort(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sort(key + "*");
        } catch (Exception ex) {
            System.out.println("Exception caught in get: " + ex);
        }
        return new ArrayList<>();
    }
}
