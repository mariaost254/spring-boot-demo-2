package com.demo.utils;

import com.demo.payload.players.PlayerAPI;
import com.demo.payload.players.PlayerCSV;
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


    @Around("@annotation(redisCacheable)")
    public Object cacheData(ProceedingJoinPoint joinPoint, RedisCacheable redisCacheable) throws Throwable {
        List<PlayerCSV> playerscsv= csvParser.convertCSVToObject("players.csv");
        List<PlayerCSV> playerscsvMissing = playerscsv.stream()
                .filter(p -> redisTemplate.opsForValue().get(String.valueOf(p.getId())) == null)
                .collect(Collectors.toList());

        if(!playerscsvMissing.isEmpty()) {
            List<PlayerAPI.Player> getPlayerFromAPI = getPlayerFromAPI(playerscsvMissing);
            for (PlayerAPI.Player player : getPlayerFromAPI) {
                redisTemplate.opsForValue().set(String.valueOf(player.getId()), player);
            }
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(redisScheduled)")
    public Object scheduledData(ProceedingJoinPoint joinPoint, RedisScheduled redisScheduled) throws Throwable {
        List<PlayerCSV> playerscsv= csvParser.convertCSVToObject("players.csv");
        List<PlayerAPI.Player> getPlayerFromAPI = getPlayerFromAPI(playerscsv);
        List<PlayerAPI.Player> playerscsvExsiting = getPlayerFromAPI.stream()
                .filter(p -> redisTemplate.opsForValue().get(String.valueOf(p.getId())) != null)
                .collect(Collectors.toList());

        List<PlayerAPI.Player> playerscsvMissing = getPlayerFromAPI.stream()
                .filter(p -> redisTemplate.opsForValue().get(String.valueOf(p.getId())) == null)
                .collect(Collectors.toList());

        if(!playerscsvExsiting.isEmpty()) {
            for (PlayerAPI.Player player : getPlayerFromAPI) {
                if(!Objects.equals(redisTemplate.opsForValue().get(String.valueOf(player.getId())), player)){
                    //TODO websocket here - there was a change
                    redisTemplate.opsForValue().set(String.valueOf(player.getId()), player);
                }
            }
        }

        if(!playerscsvMissing.isEmpty()) {
            for (PlayerAPI.Player player : getPlayerFromAPI) {
                //TODO websocket here - there was a change
                redisTemplate.opsForValue().set(String.valueOf(player.getId()), player);
            }
        }
        return joinPoint.proceed();
    }

    private List<PlayerAPI.Player> getPlayerFromAPI(List<PlayerCSV> playerscsv){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "e6559a88-aebb-456e-8d2e-d759f5a94ed7");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlWithIds = url+paramsBuilder(playerscsv);
        ResponseEntity<PlayerAPI> res = restTemplate.exchange(urlWithIds, HttpMethod.GET, entity, PlayerAPI.class);
        if (res.getStatusCode() == HttpStatus.OK) {
            playerscsv.forEach(csvPlayer -> {
                Objects.requireNonNull(res.getBody()).getData().stream()
                        .filter(apiPlayer -> apiPlayer.getId().equals(csvPlayer.getId()))
                        .findFirst()
                        .ifPresent(apiPlayer -> {
                            apiPlayer.setNickname(csvPlayer.getNickname());
                        });
            });
            return Objects.requireNonNull(res.getBody()).getData();
            }
        return new ArrayList<>();
    }

    private String paramsBuilder(List<PlayerCSV> players){
        List<Long> ids = players.stream().map(PlayerCSV::getId).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for(int i= 0 ; i< ids.size()-1; i++){
            stringBuilder.append("player_ids[]=").append(ids.get(i).toString()).append("&");
        }
        stringBuilder.append("player_ids[]=").append(ids.get(ids.size()-1).toString());
        return stringBuilder.toString();
    }
}
