package com.agrowmart.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisOtpStore {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveOtp(String phone, String code, int seconds) {
        redisTemplate.opsForValue().set("otp:" + phone, code, seconds, TimeUnit.SECONDS);
    }

    public String getOtp(String phone) {
        return redisTemplate.opsForValue().get("otp:" + phone);
    }

    public void deleteOtp(String phone) {
        redisTemplate.delete("otp:" + phone);
    }
}
