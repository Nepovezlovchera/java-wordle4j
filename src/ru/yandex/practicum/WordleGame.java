package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private String answer;
    private int steps = 6;
    private WordleDictionary dictionary;
    private final PrintWriter logger;
    private boolean gameOver;
    private boolean won;
    private List<String> guesses = new ArrayList<>();
    private Set<Character> wrongLetters = new HashSet<>();
    private Map<Integer, Character> correctLetters = new HashMap<>();
    private Map<Integer, Character> misplacedLetters = new HashMap<>();
    private Set<Character> requiredLetters = new HashSet<>();
    private Set<String> usedHints = new HashSet<String>();

    public String getAnswer() {
        return answer;
    }

    public int getSteps() {
        return steps;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return won;
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.dictionary = dictionary;
        this.logger = logger;
        this.answer = dictionary.getRandomWord();
        this.gameOver = false;
        this.won = false;
    }

    // Конструктор для тестов с ответом
    public WordleGame(WordleDictionary dictionary, PrintWriter logger, String predefinedAnswer) {
        this.dictionary = dictionary;
        this.logger = logger;
        this.answer = predefinedAnswer;
        this.steps = 6;
        this.gameOver = false;
        this.won = false;

        logger.println("Тестовая игра. Загадано слово: " + answer);
    }

    public String makeGuess(String guess) throws WordNotFoundException, GameFinishedException,
            IllegalArgumentException {
        if (gameOver) {
            throw new GameFinishedException("Игра завершилась!");
        }
        if (guess == null) {
            throw new IllegalArgumentException("Слово отсутствует");
        }
        if (guess.isBlank()) {
            throw new IllegalArgumentException("Введите слово, а не пустую строку");
        }
        if (guess.length() != 5) {
            throw new IllegalArgumentException("Слово должно состоять из 5 букв");
        }
        if (!dictionary.contains(guess)) {
            throw new WordNotFoundException("Такого слова нет в словаре");
        }
        steps--;
        guesses.add(guess);
        String result = WordleDictionary.compareWords(guess, answer);
        updateHintInfo(guess, result);
        if (guess.equals(answer)) {
            gameOver = true;
            won = true;
            logger.println("Игрок победил! Слово: " + answer);
        }
        if (steps == 0 && !won) {
            gameOver = true;
            logger.println("Игрок проиграл. Загаданное слово: " + answer);
        }

        return result;
    }

    private void updateHintInfo(String guess, String result) {
        char[] guessChars = guess.toCharArray();
        char[] resultChars = result.toCharArray();

        for (int i = 0; i < 5; i++) {
            char letter = guessChars[i];
            char status = resultChars[i];

            if (status == '+') {
                correctLetters.put(i, letter);
                requiredLetters.add(letter);
            } else if (status == '^') {
                misplacedLetters.put(i, letter);
                requiredLetters.add(letter);
            } else if (status == '-') {
                if (!requiredLetters.contains(letter)) {
                    wrongLetters.add(letter);
                }
            }
        }
    }

    public String getHint() throws GameFinishedException {
        if (gameOver) {
            throw new GameFinishedException("Игра завершена, подсказки не нужны");
        }

        List<String> possibleWords = dictionary.findPossibleWords(correctLetters, misplacedLetters, wrongLetters, requiredLetters);
        possibleWords.removeAll(usedHints);
        if (possibleWords.isEmpty()) {
            possibleWords = dictionary.findPossibleWords(correctLetters, misplacedLetters, wrongLetters, requiredLetters);
        } if (possibleWords.isEmpty()) {
            String hint = dictionary.getRandomWord();
            usedHints.add(hint);
            logger.println("Подсказка (случайная): " + hint);
            return hint;
        }
        Random random = new Random();
        String hint = possibleWords.get(random.nextInt(possibleWords.size()));
        usedHints.add(hint);
        logger.println("Подсказка: " + hint);
        return hint;
    }
}



    class WordNotFoundException extends Exception {
        public WordNotFoundException(String message) {
            super(message);
        }
    }

    class GameFinishedException extends Exception {
        public GameFinishedException(String message) {
            super(message);
        }
    }
