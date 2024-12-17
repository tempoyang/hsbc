package com.trafficAccount.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Service
public class TrafficControlService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private DefaultRedisScript<Long> incrementAndExpireScript;

    @PostConstruct
    public void init() {
        incrementAndExpireScript = new DefaultRedisScript<>();
        incrementAndExpireScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("current_count.lua")));
        incrementAndExpireScript.setResultType(Long.class);
    }

    public boolean isRequestAllowed(String userId, String apiName, int limit) {
        /*String key = String.format("traffic:%s:%s", userId, apiName);
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }*/
        String key = String.format("traffic:%s:%s", userId,apiName);
        Long currentCount = redisTemplate.execute(incrementAndExpireScript, Collections.singletonList(key), String.valueOf(60));
        return currentCount <= limit;
    }

}
