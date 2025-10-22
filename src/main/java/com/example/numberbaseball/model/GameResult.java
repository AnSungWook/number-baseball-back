package com.example.numberbaseball.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResult {
    private int strikes;
    private int balls;
    private boolean isCorrect;
    private String message;
    private String guess;
}
