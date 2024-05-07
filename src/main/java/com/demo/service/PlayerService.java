package com.demo.service;

import com.demo.payload.players.PlayerAPI;
import com.demo.payload.players.PlayerCSV;
import com.demo.payload.products.GenericResponse;
import com.demo.utils.CSVparser;
import com.demo.utils.RedisCacheable;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final String url = "https://api.balldontlie.io/v1/players?";

    @Autowired
    private CSVparser csvParser;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private final RedisTemplate<String, PlayerAPI.Player> redisTemplate;

    @SneakyThrows
    @RedisCacheable
    public List<PlayerAPI.Player> getCSVPlayer() {
        //TODO return here a mix of csv data and cache data
        List<PlayerCSV> playerscsv= csvParser.convertCSVToObject("players.csv");
        List<String> ids = playerscsv.stream().map(p -> p.getId().toString()).collect(Collectors.toList());
        return redisTemplate.opsForValue().multiGet(ids);
    }
}
