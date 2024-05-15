package com.demo.service;

import com.demo.configuration.ServerWebSocketHandler;
import com.demo.payload.players.PlayerAPI;
import com.demo.payload.players.PlayerCSV;
import com.demo.utils.CSVparser;
import com.demo.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
@EnableAsync
@RequiredArgsConstructor
public class ScheduledTask {
    @Autowired
    private RedisTemplate<String, PlayerAPI.Player> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CSVparser csvParser;

    @Autowired
    private Utils utils;

    @Autowired
    private ServerWebSocketHandler serverWebSocketHandler;

    @Async
    @Scheduled(fixedDelay = 90000)
    public void cacheCheckScheduler(){
        CompletableFuture<List<PlayerAPI.Player>> futureItems = fetchPlayersAsync();
        futureItems.thenAccept(players -> {
            for (PlayerAPI.Player player : players) {
                System.out.println("scheduled");
                updatePlayer(player);
                if (!Objects.equals(redisTemplate.opsForValue().get(player.getId()), player)) {
                   // updatePlayer(player);
                }
            }
        });
    }

    @Async
    public CompletableFuture<List<PlayerAPI.Player>> fetchPlayersAsync() {
        List<PlayerCSV> playerscsv= csvParser.convertCSVToObject("players.csv");
        List<PlayerAPI.Player> players = utils.getPlayerFromAPI(playerscsv);
        return CompletableFuture.completedFuture(players);
    }

    @SneakyThrows
    private void updatePlayer(PlayerAPI.Player player) {
        redisTemplate.opsForValue().set(player.getId().toString() , player);
        serverWebSocketHandler.sendMessageToAll("Scheduled message every 15 minutes");
    }

}
