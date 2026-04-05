package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    private WordleDictionary dictionary;
    private List<String> testWords;

    @BeforeEach
    void setUp() {
        testWords = Arrays.asList("герой", "город", "гений", "кот", "слово", "мама");
        dictionary = new WordleDictionary(testWords);
    }

    @Test
    void testCompareWords_ShouldReturnFivePluses() {
        String result = WordleDictionary.compareWords("герой", "герой");
        assertEquals("+++++", result);
        assertEquals(5, result.length());
    }

    @Test
    void testCompareWords_ShouldReturnFiveMinuses() {
        String result = WordleDictionary.compareWords("абвгд", "ежзик");
        assertEquals("-----", result);
        assertEquals(5, result.length());
    }

    @Test
    void testCompareWords_ShouldReturnMisplacedLetters() {
        String result = WordleDictionary.compareWords("регой", "герой");
        assertEquals('^', result.charAt(0));
        assertEquals('+', result.charAt(1));
        assertEquals('^', result.charAt(2));
        assertEquals(5, result.length());
    }


    @Test
    void testNormalize_ShouldReturnLowerCase() {
        String result = WordleDictionary.normalize("ГЕРОЙ");
        assertEquals("герой", result);
    }

    @Test
    void testNormalize_ShouldReplaceWithE() {
        String result = WordleDictionary.normalize("ёжик");
        assertEquals("ежик", result);
    }

    @Test
    void testNormalize_ShouldTrim() {
        String result = WordleDictionary.normalize("  герой  ");
        assertEquals("герой", result);
    }

    @Test
    void testNormalize_Null_ShouldReturnNull() {
        assertNull(WordleDictionary.normalize(null));
    }

    @Test
    void testFindPossible_WordsWithCorrectLetters() {
        Map<Integer, Character> correctLetters = new HashMap<>();
        correctLetters.put(0, 'г');
        correctLetters.put(2, 'р');

        List<String> result = dictionary.findPossibleWords(
                correctLetters, new HashMap<>(), new HashSet<>(), new HashSet<>()
        );

        assertTrue(result.contains("герой"));
        assertTrue(result.contains("город"));
        assertFalse(result.contains("гений"));
    }

    @Test
    void testFindPossibleWords_WithMisplacedLetters() {
        Map<Integer, Character> misplacedLetters = new HashMap<>();
        misplacedLetters.put(0, 'г');

        List<String> result = dictionary.findPossibleWords(
                new HashMap<>(), misplacedLetters, new HashSet<>(), new HashSet<>()
        );

        for (String word : result) {
            assertNotEquals('г', word.charAt(0));
        }
    }

    @Test
    void testFindPossibleWords_WithWrongLetters() {
        Set<Character> wrongLetters = new HashSet<>(Arrays.asList('г', 'о'));

        List<String> result = dictionary.findPossibleWords(
                new HashMap<>(), new HashMap<>(), wrongLetters, new HashSet<>()
        );

        for (String word : result) {
            assertFalse(word.contains("г"));
            assertFalse(word.contains("о"));
        }
    }

    @Test
    void testFindPossibleWords_WithRequiredLetters() {
        Set<Character> requiredLetters = new HashSet<>(Arrays.asList('г', 'о'));

        List<String> result = dictionary.findPossibleWords(
                new HashMap<>(), new HashMap<>(), new HashSet<>(), requiredLetters
        );

        for (String word : result) {
            assertTrue(word.contains("г"));
            assertTrue(word.contains("о"));
        }
    }

    @Test
    void testFindPossibleWords_WithAllConditions() {
        Map<Integer, Character> correctLetters = new HashMap<>();
        correctLetters.put(0, 'г');

        Set<Character> requiredLetters = new HashSet<>(Arrays.asList('р'));
        Set<Character> wrongLetters = new HashSet<>(Arrays.asList('е'));

        List<String> result = dictionary.findPossibleWords(
                correctLetters, new HashMap<>(), wrongLetters, requiredLetters
        );

        for (String word : result) {
            assertEquals('г', word.charAt(0));
            assertTrue(word.contains("р"));
            assertFalse(word.contains("е"));
        }
    }
}