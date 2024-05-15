package com.demo.controller;
import com.demo.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/player")
public class PlayersController {

    @Autowired
    private PlayerService playerService;


    @GetMapping
    public ResponseEntity<?> getAllPlayers(){
        byte[] fileContent = playerService.getCSVPlayer();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "players.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }
}
