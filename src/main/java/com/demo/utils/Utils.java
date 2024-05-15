package com.demo.utils;

import com.demo.payload.players.PlayerAPI;
import com.demo.payload.players.PlayerCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class Utils {

    private final String url = "https://api.balldontlie.io/v1/players?";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CSVparser csvParser;

    public List<PlayerAPI.Player> getPlayerFromAPI(List<PlayerCSV> playerscsv){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "e6559a88-aebb-456e-8d2e-d759f5a94ed7");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String urlWithIds = url+paramsBuilder(playerscsv);
        ResponseEntity<PlayerAPI> res = restTemplate.exchange(urlWithIds, HttpMethod.GET, entity, PlayerAPI.class);
        if (res.getStatusCode() == HttpStatus.OK) {
            return Objects.requireNonNull(res.getBody()).getData();
        }
        return new ArrayList<>();
    }

    public static String paramsBuilder(List<PlayerCSV> players){
        List<Long> ids = players.stream().map(PlayerCSV::id).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for(int i= 0 ; i< ids.size()-1; i++){
            stringBuilder.append("player_ids[]=").append(ids.get(i).toString()).append("&");
        }
        stringBuilder.append("player_ids[]=").append(ids.get(ids.size()-1).toString());
        return stringBuilder.toString();
    }
}
//