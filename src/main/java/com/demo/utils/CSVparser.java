package com.demo.utils;

import com.demo.payload.players.PlayerAPI;
import com.demo.payload.players.PlayerCSV;
import com.opencsv.CSVReader;
import lombok.SneakyThrows;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CSVparser {

    @SneakyThrows
    public List<PlayerCSV> convertCSVToObject(String fileName)  {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
        List<PlayerCSV> objects = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(file.getPath()))) {
            String[] line;
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                PlayerCSV object = new PlayerCSV();
                object.setId(Long.parseLong(line[0]));
                object.setNickname(line[1]);
                objects.add(object);
            }
        }
        return objects;
    }

    @SneakyThrows
    public byte[] createCSVFileFromList(List<PlayerAPI.Player> playersList) {
        File file = File.createTempFile("players", ".csv");
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("ID,First Name,Last Name,Nickname,Position,Height,Weight,Jersey Number,College,");
        csvContent.append("Country,Draft Year,Draft Round,Draft Number,Team ID,Conference,Division,City,Team Name,");
        csvContent.append("Full Name,Abbreviation\n");

        for (PlayerAPI.Player player : playersList) {
            csvContent.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%d,%d,%s,%s,%s,%s,%s,%s\n",
                    player.getId(), player.getFirstName(), player.getLastName(), player.getNickname(), player.getPosition(),
                    player.getHeight(), player.getWeight(), player.getJerseyNumber(), player.getCollege(), player.getCountry(),
                    player.getDraftYear(), player.getDraftRound(), player.getDraftNumber(),
                    player.getTeam().getId(), player.getTeam().getConference(), player.getTeam().getDivision(),
                    player.getTeam().getCity(), player.getTeam().getName(), player.getTeam().getFullName(), player.getTeam().getAbbreviation()));
        }
        return csvContent.toString().getBytes();
    }
}
