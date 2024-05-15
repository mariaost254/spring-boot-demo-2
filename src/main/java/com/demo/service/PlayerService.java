package com.demo.service;

import com.demo.payload.players.PlayerAPI;
import com.demo.payload.players.PlayerCSV;
import com.demo.utils.CSVparser;
import com.demo.utils.RedisCacheable;
import com.demo.utils.errors.PlayerServiceException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

    @Autowired
    private CSVparser csvParser;

    @Autowired
    private final RedisTemplate<String, PlayerAPI.Player> redisTemplate;

    @RedisCacheable
    public byte[] getCSVPlayer() {
        try {
            List<PlayerCSV> playerscsv = csvParser.convertCSVToObject("players.csv");
            List<String> ids = playerscsv.stream()
                    .map(p -> p.getId().toString())
                    .collect(Collectors.toList());
            List<PlayerAPI.Player> players = redisTemplate.opsForValue().multiGet(ids);

            if (players == null || players.isEmpty()) {
                throw new PlayerServiceException("No players found");
            }

            playerscsv.forEach(csvPlayer -> {
                players.stream()
                        .filter(apiPlayer -> apiPlayer.getId().equals(csvPlayer.getId()))
                        .findFirst()
                        .ifPresent(apiPlayer -> apiPlayer.setNickname(csvPlayer.getNickname()));
            });

            return csvParser.createCSVFileFromList(players);
        } catch (Exception e) {
            throw new PlayerServiceException("Error occurred while generating CSV", e);
        }
    }
}
