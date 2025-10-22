package com.example.numberbaseball.controller;

import com.example.numberbaseball.model.GameResult;
import com.example.numberbaseball.model.GameState;
import com.example.numberbaseball.service.NumberBaseballService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NumberBaseballController {
    
    private final NumberBaseballService gameService;
    
    @PostMapping("/start")
    public ResponseEntity<String> startGame() {
        String gameId = gameService.startNewGame();
        return ResponseEntity.ok(gameId);
    }
    
    @PostMapping("/{gameId}/guess")
    public ResponseEntity<GameResult> makeGuess(@PathVariable String gameId, @RequestBody Map<String, String> request) {
        String guess = request.get("guess");
        GameResult result = gameService.makeGuess(gameId, guess);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{gameId}/state")
    public ResponseEntity<GameState> getGameState(@PathVariable String gameId) {
        GameState state = gameService.getGameState(gameId);
        if (state == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(state);
    }
    
    @PostMapping("/{gameId}/hint")
    public ResponseEntity<String> getHint(@PathVariable String gameId, @RequestBody Map<String, Integer> request) {
        Integer position = request.get("position");
        if (position == null) {
            return ResponseEntity.badRequest().body("자리 번호를 입력해주세요.");
        }
        
        String hint = gameService.getHint(gameId, position);
        return ResponseEntity.ok(hint);
    }
}
