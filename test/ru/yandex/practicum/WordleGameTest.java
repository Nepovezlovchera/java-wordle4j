package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private WordleDictionary dictionary;
    private PrintWriter testLogger;
    private StringWriter logOutput;

    @BeforeEach
    void setUp() {
        List<String> words = Arrays.asList("герой", "город", "гений", "кот", "слово");
        dictionary = new WordleDictionary(words);

        logOutput = new StringWriter();
        testLogger = new PrintWriter(logOutput);
    }


    @Test
    void testMakeGuess_CorrectWord_ShouldWin() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        String result = game.makeGuess("герой");

        assertEquals("+++++", result);
        assertTrue(game.isGameOver());
        assertTrue(game.isWon());
    }

    @Test
    void testMakeGuess_WrongWord_ShouldNotEndGame() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        String result = game.makeGuess("город");

        assertNotNull(result);
        assertFalse(game.isGameOver());
        assertFalse(game.isWon());
        assertEquals(5, game.getSteps());
    }

    @Test
    void testMakeGuess_WordNotFound_ShouldThrowException() {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        assertThrows(WordNotFoundException.class, () -> {
            game.makeGuess("абвгд");
        });
    }

    @Test
    void testMakeGuess_AfterGameOver_ShouldThrowException() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");
        game.makeGuess("герой");

        assertThrows(GameFinishedException.class, () -> {
            game.makeGuess("город");
        });
    }

    @Test
    void testMakeGuess_WrongLength_ShouldThrowException() {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        assertThrows(IllegalArgumentException.class, () -> {
            game.makeGuess("абв");
        });
    }

    @Test
    void testMakeGuess_NullInput_ShouldThrowException() {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        assertThrows(IllegalArgumentException.class, () -> {
            game.makeGuess(null);
        });
    }

    @Test
    void testMakeGuess_BlankInput_ShouldThrowException() {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        assertThrows(IllegalArgumentException.class, () -> {
            game.makeGuess("     ");
        });
    }

    @Test
    void testMakeGuess_LoseAfterSixAttempts() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        for (int i = 0; i < 5; i++) {
            game.makeGuess("город");
        }
        game.makeGuess("гений");

        assertTrue(game.isGameOver());
        assertFalse(game.isWon());
        assertEquals(0, game.getSteps());
    }


    @Test
    void testUpdateHintInfo_AfterGuess_CorrectLettersShouldUpdate() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        game.makeGuess("город");

        String hint = game.getHint();
        assertNotNull(hint);
    }


    @Test
    void testGetHint_BeforeAnyGuesses_ShouldReturnRandomWord() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        String hint = game.getHint();

        assertNotNull(hint);
        assertTrue(dictionary.contains(hint));
    }

    @Test
    void testGetHint_AfterGameOver_ShouldThrowException() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");
        game.makeGuess("герой");

        assertThrows(GameFinishedException.class, () -> {
            game.getHint();
        });
    }

    @Test
    void testGetHint_ShouldNotRepeatSameHint() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        String hint1 = game.getHint();
        String hint2 = game.getHint();

        assertNotNull(hint1);
        assertNotNull(hint2);
    }


    @Test
    void testRemainingSteps_DecreasesAfterGuess() throws Exception {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");

        assertEquals(6, game.getSteps());

        game.makeGuess("город");
        assertEquals(5, game.getSteps());

        game.makeGuess("гений");
        assertEquals(4, game.getSteps());
    }


    @Test
    void testGetAnswer_ReturnsCorrectWord() {
        WordleGame game = new WordleGame(dictionary, testLogger, "герой");
        assertEquals("герой", game.getAnswer());
    }
}