package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {

    private final List<String> words;
    private final Random random;

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
        this.random = new Random();
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        return words.get(random.nextInt(words.size()));
    }

    public static String normalize(String word) {
        if (word == null) {
            return null;
        }
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    public static String compareWords(String guess, String secret) {
        char[] guessChars = guess.toCharArray();
        char[] secretChars = secret.toCharArray();

        boolean[] used = new boolean[5];

        char[] result = new char[5];

        for (int i = 0; i < 5; i++) {
            if (guessChars[i] == secretChars[i]) {
                result[i] = '+';
                used[i] = true;
                guessChars[i] = 0;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result[i] == '+') {
                continue;
            }

            if (guessChars[i] == 0) {
                continue;
            }

            boolean found = false;
            for (int j = 0; j < 5; j++) {
                if (!used[j] && secretChars[j] == guessChars[i]) {
                    found = true;
                    used[j] = true;
                    break;
                }
            }

            if (found) {
                result[i] = '^';
            } else {
                result[i] = '-';
            }

        }

        return new String(result);
    }

    public List<String> findPossibleWords(
            Map<Integer, Character> correctLetters,
            Map<Integer, Character> misplacedLetters,
            Set<Character> wrongLetters,
            Set<Character> requiredLetters
    ) {
        List<String> possible = new ArrayList<>();

        for (String word : words) {
            // Проверка 1: correctLetters
            boolean correctOk = true;
            for (Map.Entry<Integer, Character> entry : correctLetters.entrySet()) {
                if (word.charAt(entry.getKey()) != entry.getValue()) {
                    correctOk = false;
                    break;
                }
            }

            if (!correctOk) continue;

            boolean misplacedOk = true;
            for (Map.Entry<Integer, Character> entry : misplacedLetters.entrySet()) {
                int position = entry.getKey();
                char letter = entry.getValue();

                if (word.charAt(position) == letter) {
                    misplacedOk = false;
                    break;
                }

                if (word.indexOf(letter) == -1) {
                    misplacedOk = false;
                    break;
                }
            }

            if (!misplacedOk) continue;

            boolean wrongOk = true;
            for (char letter : wrongLetters) {
                if (word.indexOf(letter) != -1) {
                    wrongOk = false;
                    break;
                }
            }

            if (!wrongOk) continue;

            boolean requiredOk = true;
            for (char letter : requiredLetters) {
                if (word.indexOf(letter) == -1) {
                    requiredOk = false;
                    break;
                }
            }

            if (!requiredOk) continue;


            possible.add(word);
        }

        return possible;
    }

}


