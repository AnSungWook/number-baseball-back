package com.example.numberbaseball.service;

import com.example.numberbaseball.model.GameResult;
import com.example.numberbaseball.model.GameState;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NumberBaseballService {

    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    public String startNewGame() {
        String gameId = UUID.randomUUID().toString();
        String answer = generateAnswer();
        games.put(gameId, new GameState(answer));
        return gameId;
    }

    public GameResult makeGuess(String gameId, String guess) {
        GameState game = games.get(gameId);
        if (game == null) {
            return new GameResult(0, 0, false, "게임을 찾을 수 없습니다.", guess);
        }

        if (game.isGameOver()) {
            return new GameResult(0, 0, false, "이미 종료된 게임입니다.", guess);
        }

        if (!isValidGuess(guess)) {
            if (guess.startsWith("0")) {
                return new GameResult(0, 0, false, "0으로 시작하는 숫자는 입력할 수 없습니다.", guess);
            } else if (guess.length() != 4) {
                return new GameResult(0, 0, false, "4자리 숫자를 입력해주세요.", guess);
            } else {
                return new GameResult(0, 0, false, "서로 다른 4자리 숫자를 입력해주세요.", guess);
            }
        }

        GameResult result = calculateResult(game.getAnswer(), guess);
        result.setGuess(guess);
        game.getAttempts().add(result);
        game.setAttemptCount(game.getAttemptCount() + 1);

        if (result.isCorrect()) {
            game.setGameOver(true);
            game.setWon(true);
            result.setMessage("축하합니다! 정답입니다!");
        } else if (game.getAttemptCount() >= 10) {
            game.setGameOver(true);
            game.setWon(false);
            result.setMessage("게임 오버! 정답은 " + game.getAnswer() + "였습니다.");
        } else {
            result.setMessage(String.format("%d스트라이크 %d볼", result.getStrikes(), result.getBalls()));
        }

        return result;
    }

    public GameState getGameState(String gameId) {
        GameState game = games.get(gameId);
        if (game == null) {
            return null;
        }
        // 반환할 때 외부로 전달할 복사본을 만들어서 전달 (원본은 내부에서 제거 가능)
        GameState copy = new GameState(game);
        if (!game.isGameOver()) {
            copy.setAnswer(null);
        }
        // 게임이 이미 종료된 상태라면, 상태를 반환한 이후 더 이상 서버에 보관할 필요가 없음 -> 제거
        if (game.isGameOver()) {
            games.remove(gameId);
        }
        return copy;
    }

    public String getHint(String gameId, int position) {
        GameState game = games.get(gameId);
        if (game == null) {
            return "게임을 찾을 수 없습니다.";
        }

        if (game.isGameOver()) {
            return "이미 종료된 게임입니다.";
        }

        if (game.getHintCount() >= 1) {
            return "힌트를 모두 사용했습니다.";
        }

        if (position < 1 || position > 4) {
            return "1~4번째 자리만 선택할 수 있습니다.";
        }

        String hintKey = position + "번째 자리";
        if (game.getUsedHints().contains(hintKey)) {
            return "이미 확인한 자리입니다.";
        }

        char digit = game.getAnswer().charAt(position - 1);
        game.setHintCount(game.getHintCount() + 1);
        game.getUsedHints().add(hintKey);

        return position + "번째 자리는 " + digit + "입니다.";
    }

    private String generateAnswer() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        if (numbers.get(0) == 0) {
            for (int i = 1; i < numbers.size(); i++) {
                if (numbers.get(i) != 0) {
                    Collections.swap(numbers, 0, i);
                    break;
                }
            }
        }

        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            answer.append(numbers.get(i));
        }
        return answer.toString();
    }

    private boolean isValidGuess(String guess) {
        if (guess == null || guess.length() != 4) {
            return false;
        }

        try {
            int number = Integer.parseInt(guess);
            if (number < 1000 || number > 9999) {
                return false;
            }

            // 중복 숫자 체크
            Set<Character> digits = new HashSet<>();
            for (char c : guess.toCharArray()) {
                if (!digits.add(c)) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private GameResult calculateResult(String answer, String guess) {
        int strikes = 0;
        int balls = 0;

        for (int i = 0; i < 4; i++) {
            char guessChar = guess.charAt(i);
            char answerChar = answer.charAt(i);

            if (guessChar == answerChar) {
                strikes++;
            } else if (answer.contains(String.valueOf(guessChar))) {
                balls++;
            }
        }

        boolean isCorrect = strikes == 4;
        return new GameResult(strikes, balls, isCorrect, "", guess);
    }
}
