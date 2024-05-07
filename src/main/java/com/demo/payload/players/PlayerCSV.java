package com.demo.payload.players;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerCSV {
    private Long id;
    private String nickname;
}
