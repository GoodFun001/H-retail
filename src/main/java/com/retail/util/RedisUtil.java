package com.retail.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis设置失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 设置缓存并指定过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis设置失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 设置字符串缓存
     */
    public void setString(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis设置失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 设置字符串缓存并指定过期时间
     */
    public void setString(String key, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis设置失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis获取失败: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 获取字符串缓存
     */
    public String getString(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis获取失败: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 删除缓存
     */
    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis删除失败: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 判断缓存是否存在
     */
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis判断key存在失败: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("Redis设置过期时间失败: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis获取过期时间失败: key={}, error={}", key, e.getMessage());
            return -1L;
        }
    }

    /**
     * 递增
     */
    public Long increment(String key) {
        try {
            return stringRedisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Redis递增失败: key={}, error={}", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 递增指定步长
     */
    public Long increment(String key, long delta) {
        try {
            return stringRedisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis递增失败: key={}, delta={}, error={}", key, delta, e.getMessage());
            return 0L;
        }
    }

    /**
     * 递减
     */
    public Long decrement(String key) {
        try {
            return stringRedisTemplate.opsForValue().decrement(key);
        } catch (Exception e) {
            log.error("Redis递减失败: key={}, error={}", key, e.getMessage());
            return 0L;
        }
    }

    /**
     * 递减指定步长
     */
    public Long decrement(String key, long delta) {
        try {
            return stringRedisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("Redis递减失败: key={}, delta={}, error={}", key, delta, e.getMessage());
            return 0L;
        }
    }

    /**
     * 原子性扣减库存
     * @param key 库存key
     * @param num 扣减数量
     * @return 剩余库存数量
     */
    public Long deductStock(String key, Integer num) {
        try {
            Long remainStock = stringRedisTemplate.opsForValue().decrement(key, num);
            return remainStock;
        } catch (Exception e) {
            log.error("Redis扣减库存失败: key={}, num={}, error={}", key, num, e.getMessage());
            return null;
        }
    }

    /**
     * 原子性增加库存
     * @param key 库存key
     * @param num 增加数量
     * @return 剩余库存数量
     */
    public Long addStock(String key, Integer num) {
        try {
            Long remainStock = stringRedisTemplate.opsForValue().increment(key, num);
            return remainStock;
        } catch (Exception e) {
            log.error("Redis增加库存失败: key={}, num={}, error={}", key, num, e.getMessage());
            return null;
        }
    }
}
