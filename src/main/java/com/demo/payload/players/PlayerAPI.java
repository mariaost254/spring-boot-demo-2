package com.demo.payload.players;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerAPI {
    private List<Player> data;
    private Meta meta;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Player implements Serializable {
        private Long id;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String nickname;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("last_name")
        private String lastName;
        private String position;
        private String height;
        private int weight;
        @JsonProperty("jersey_number")
        private String jerseyNumber;
        private String college;
        private String country;
        @JsonProperty("draft_year")
        private int draftYear;
        @JsonProperty("draft_round")
        private int draftRound;
        @JsonProperty("draft_number")
        private int draftNumber;
        private Team team;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Team{
        private Long id;
        private String conference;
        private String division;
        private String city;
        private String name;
        @JsonProperty("full_name")
        private String fullName;
        private String abbreviation;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Meta{
        @JsonProperty("next_cursor")
        private String nextCursor;
        @JsonProperty("per_page")
        private String perPage;
    }
}
