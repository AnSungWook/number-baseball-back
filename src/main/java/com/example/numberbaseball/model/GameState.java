package com.example.numberbaseball.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameState {
    private String answer;
    private int attemptCount;
    private List<GameResult> attempts;
    private boolean gameOver;
    private boolean won;
    private int hintCount;
    private List<String> usedHints;

    /**
     * 편의 생성자: 정답만 전달하면 나머지 필드는 기본값으로 초기화됩니다.
     */
    public GameState(String answer) {
        this.answer = answer;
        this.attemptCount = 0;
        this.attempts = new ArrayList<>();
        this.gameOver = false;
        this.won = false;
        this.hintCount = 0;
        this.usedHints = new ArrayList<>();
    }

    /**
     * 복사 생성자: 내부 상태를 외부로 반환할 때 원본 보호용으로 사용합니다.
     * null-safe하게 리스트를 복사합니다.
     */
    public GameState(GameState other) {
        if (other == null) {
            this.answer = null;
            this.attemptCount = 0;
            this.attempts = new ArrayList<>();
            this.gameOver = false;
            this.won = false;
            this.hintCount = 0;
            this.usedHints = new ArrayList<>();
            return;
        }
        this.answer = other.answer;
        this.attemptCount = other.attemptCount;
        this.attempts = other.attempts == null ? new ArrayList<>() : new ArrayList<>(other.attempts);
        this.gameOver = other.gameOver;
        this.won = other.won;
        this.hintCount = other.hintCount;
        this.usedHints = other.usedHints == null ? new ArrayList<>() : new ArrayList<>(other.usedHints);
    }
}
