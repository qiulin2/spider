package com.spider.robot;

import com.spider.main.AbstractTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author zql
 * @Date 2019/8/8 0008
 * @Description TODO
 **/
@Component
public class TestRedis extends AbstractTask {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ListOperations listOperations;
    @Override
    public int doJob() {
        List<Object> results = redisTemplate.executePipelined(
                new RedisCallback<Object>() {
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        for(int i=0; i< 100000000; i++) {
                            listOperations.rightPush("test","aaaaaaaaaaaaaaaaa"+i);
                        }
                        return null;
                    }
                });

        return 0;
    }
}
