package com.example.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisStringCommands;

import java.util.List;

public class DemoDriver {
    public static void main(String [] args) {
        RedisClient client = RedisClient.create("redis://mypass@52.91.75.7:10001");
        StatefulRedisConnection<String, String> connection = client.connect();
        RedisStringCommands sync = connection.sync();

        sync.set("key", "value");

        List<String> value = ((RedisCommands) sync).keys("*");

        value.forEach((k) -> System.out.println("KEY: " + k));
    }
}
