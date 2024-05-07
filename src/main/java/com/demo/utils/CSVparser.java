package com.demo.utils;

import com.demo.payload.players.PlayerCSV;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
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
}
