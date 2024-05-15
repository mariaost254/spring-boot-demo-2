package com.demo.utils;

import com.demo.payload.players.PlayerAPI;
import com.demo.payload.players.PlayerCSV;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Aspect
@Order(1)
@Component
public class PlayerCacheAspect {

    private final String url = "https://api.balldontlie.io/v1/players?";

    @Autowired
    private RedisTemplate<String, PlayerAPI.Player> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CSVparser csvParser;

    @Autowired
    private Utils utils;

    @Around("@annotation(redisCacheable)")
    @SneakyThrows
    public Object cacheData(ProceedingJoinPoint joinPoint, RedisCacheable redisCacheable){
        List<PlayerCSV> playerscsv= csvParser.convertCSVToObject("players.csv");
        List<PlayerCSV> playerscsvMissing = playerscsv.stream()
                .filter(p -> redisTemplate.opsForValue().get(String.valueOf(p.getId())) == null)
                .collect(Collectors.toList());

        if(!playerscsvMissing.isEmpty()) {
            List<PlayerAPI.Player> getPlayerFromAPI = utils.getPlayerFromAPI(playerscsvMissing);
            for (PlayerAPI.Player player : getPlayerFromAPI) {
                redisTemplate.opsForValue().set(String.valueOf(player.getId()), player);
            }
        }
        return joinPoint.proceed();
    }

}
