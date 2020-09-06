package com.demo.sdk.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类，方法定义：http://doc.redisfans.com
 */
@Service
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 列表获取
     *
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1) {
        return redisTemplate.opsForList().range(k, l, l1);
    }

    /**
     * 保持链表只有N位
     *
     * @param k
     * @param N
     */
    public void lTrim(String k, int N) {
        redisTemplate.opsForList().trim(k, 0, N - 1);
    }

    /**
     * 设置list指定位置的值
     *
     * @param k
     * @param index
     * @param value
     */
    public void lSet(String k, Long index, String value) {
        redisTemplate.opsForList().set(k, index, value);
    }

    /**
     * 删除key
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除key
     */
    public void del(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 检查给定 key是否存在
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param timeout 单位秒
     */
    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit timeUtit) {
        return redisTemplate.expire(key, timeout, timeUtit);
    }

    /**
     * 设置过期时间
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 返回给定 key 的剩余生存时间,以秒为单位
     */
    public Long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /******************* String **********************/

    /**
     * 将 key所储存的值加上增量 delta,返回增加后的值
     */
    public Long incrBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将字符串值 value 关联到 key
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 将字符串值 value 关联到 key
     */
    public void setex(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 将 key的值设为 value ，当且仅当 key 不存在
     */
    public Boolean setnx(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 关联到 key
     */
    public void mset(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    /**
     * 返回 key所关联的字符串
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /******************* Hash **********************/

    /**
     * 删除哈希表 key中的一个或多个指定域，不存在的域将被忽略
     */
    public Long hdel(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 将哈希表 key中的域 field 的值设为 value
     */
    public void hset(String key, String hashKey, Object hashValue) {
        redisTemplate.opsForHash().put(key, hashKey, hashValue);
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中
     */
    public void hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在
     */
    public Boolean hsetnx(String key, String hashKey, Object hashValue) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, hashValue);
    }

    /**
     * 返回哈希表 key 中给定域 field 的值
     */
    public Object hget(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 返回哈希表 key 中，所有的域和值
     */
    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 返回哈希表 key 中的所有域
     * @date 2018年1月30日
     */
    public Set<Object> hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 返回哈希表 key 中所有域的值
     */
    public List<Object> hvals(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 为哈希表 key 中的域 field 的值加上增量 delta
     */
    public Long hincrBy(String key, String hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 查看哈希表 key 中，给定域 field 是否存在
     */
    public Boolean hexists(final String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    /******************* List **********************/

    /**
     * 删除并获取列表中的第一个元素
     */
    public Object lpop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 删除并获取列表中的第一个元素，或阻塞，直到有一个元素可用
     */
    public Object blpop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 获取index的元素值
     */
    public Object index(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 删除并获取列表中的最后一个元素
     */
    public Object rpop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 删除并获取列表中的最后一个元素，或阻塞，直到有一个元素可用
     */
    public Object brpop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 返回列表 key 的长度
     */
    public Long llen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 将value 插入到列表 key 的表头
     */
    public Long lpush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表
     *
     * @param
     * @author caiLinFeng
     * @date 2018年1月30日
     */
    public Long lpushx(String key, Object value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 将value 插入到列表 key 的表尾
     */
    public Long rpush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表
     */
    public Long rpushx(String key, Object value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /******************* Set **********************/

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略
     */
    public Long sadd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 返回集合 key 的基数(集合中元素的数量)
     */
    public Long scard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合之间的差集
     */
    public Set<Object> sdiff(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合之间的差集
     */
    public Set<Object> sdiff(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合的交集
     */
    public Set<Object> sinter(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合的交集
     */
    public Set<Object> sinter(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * 判断 member 元素是否集合 key 的成员
     */
    public Boolean sismember(String key, Object member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }

    /**
     * 返回集合 key 中的所有成员
     */
    public Set<Object> smembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略
     */
    public Long srem(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合的并集
     */
    public Set<Object> sunion(String key, String otherKey) {
        return redisTemplate.opsForSet().union(key, otherKey);
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合的并集
     */
    public Set<Object> sunion(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /******************* Zset **********************/

    /**
     * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中v
     */
    public Boolean zadd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 返回有序集 key 的基数
     */
    public Long zcard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max)的成员的数量
     */
    public Long zcount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 delta
     */
    public Double zincrby(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员,其中成员的位置按 score 值递增(从小到大)来排序
     */
    public Set<Object> zrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max)的成员。有序集成员按
     * score,值递增(从小到大)次序排列
     */
    public Set<Object> zrangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max)的成员。有序集成员按
     * score,值递增(从小到大)次序排列
     * @param offset 位置
     * @param count  个数
     */
    public Set<Object> zrangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    /**
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。排名以 0 为底
     */
    public Long zrank(String key, Object member) {
        return redisTemplate.opsForZSet().rank(key, member);
    }

    public Long zrem(String key, Object... member) {
        return redisTemplate.opsForZSet().remove(key, member);
    }

    /**
     * 移除有序集 key 中，指定排名(rank)区间内的所有成员
     */
    public Long zremrangeByRank(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
     */
    public Long zremrangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。其中成员的位置按 score 值递减(从大到小)来排列。
     */
    public Set<Object> zrevrange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min)的所有的成员。有序集成员按
     * score,值递减(从大到小)的次序排列
     */
    public Set<Object> zrevrangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。排名以 0 为底
     */
    public Long zrevrank(String key, Object member) {
        return redisTemplate.opsForZSet().reverseRank(key, member);
    }

    /**
     * 返回有序集 key 中，成员 member 的 score 值
     */
    public Double zscore(String key, Object member) {
        return redisTemplate.opsForZSet().score(key, member);
    }

    /******************* Pub/Sub **********************/

    /**
     * 将信息 message 发送到指定的频道 channel
     */
    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

    /******************* serial **********************/

    /**
     * 获取redisTemplate的序列化
     */
    public RedisSerializer<?> getDefaultSerializer() {
        return redisTemplate.getDefaultSerializer();
    }

    public RedisSerializer<?> getStringSerializer() {
        return redisTemplate.getStringSerializer();
    }

    public RedisSerializer<?> getValueSerializer() {
        return redisTemplate.getValueSerializer();
    }

}
