package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter logger = new PrintWriter(new OutputStreamWriter(new FileOutputStream("game.log",
                true), StandardCharsets.UTF_8))) {

            logger.println("НОВАЯ ИГРА");

            WordleDictionaryLoader loader = new WordleDictionaryLoader("words_ru.txt");
            List<String> wordList = loader.load();
            logger.println("Загружено слов: " + wordList.size());

            if (wordList.isEmpty()) {
                System.out.println("Ошибка: словарь пуст. Игра не может быть запущена.");
                logger.println("ОШИБКА: словарь пуст!");
                return;
            }

            WordleDictionary dictionary = new WordleDictionary(wordList);
            WordleGame game = new WordleGame(dictionary, logger);
            Scanner scanner = new Scanner(System.in);

            boolean playing = true;

            while (playing) {
                logger.println("Игра создана. Загадано слово: " + game.getAnswer());

                System.out.println("     ДОБРО ПОЖАЛОВАТЬ В WORDLE");
                System.out.println("Правила игры просты, загадано русское слово из 5 букв.");
                System.out.println("У вас 6 попыток, чтобы его отгадать.");
                System.out.println();
                System.out.println("После ввода слова вы увидите подсказку:");
                System.out.println("  + — буква на своём месте");
                System.out.println("  ^ — буква есть, но не на этом месте");
                System.out.println("  - — буквы нет в слове");
                System.out.println();
                System.out.println("Для подсказки нажмите Enter без ввода слова.");
                System.out.println("=================================");
                System.out.println();

                while (!game.isGameOver()) {
                    System.out.print("Введите слово (или Enter для подсказки): ");
                    String input = scanner.nextLine().trim();

                    if (input.isEmpty()) {
                        try {
                            String hint = game.getHint();
                            System.out.println("Подсказка: " + hint);
                            System.out.println("Осталось попыток: " + game.getSteps());
                            System.out.println();
                        } catch (GameFinishedException e) {
                            System.out.println(e.getMessage());
                            break;
                        }
                        continue;
                    }

                    String normalized = WordleDictionary.normalize(input);

                    try {
                        String result = game.makeGuess(normalized);
                        System.out.println("Результат: " + result);
                        System.out.println("Осталось попыток: " + game.getSteps());
                        System.out.println();

                    } catch (WordNotFoundException e) {
                        System.out.println("Неверно! " + e.getMessage());
                        System.out.println("Попробуйте другое слово.");
                        System.out.println();

                    } catch (GameFinishedException e) {
                        System.out.println(e.getMessage());
                        break;

                    } catch (IllegalArgumentException e) {
                        System.out.println("Неверно! " + e.getMessage());
                        System.out.println("Слово должно состоять из 5 русских букв.");
                        System.out.println();
                    }
                }

                System.out.println();
                System.out.println("=================================");
                if (game.isWon()) {
                    System.out.println("ПОЗДРАВЛЯЕМ! ВЫ ОТГАДАЛИ СЛОВО!");
                } else {
                    System.out.println("Мне очень жаль, но вы проиграли...");
                }
                System.out.println("Загаданное слово: " + game.getAnswer());
                System.out.println("=================================");

                logger.println("Игра завершена. Победа: " + game.isWon());

                System.out.print("Хотите сыграть ещё? (да/нет): ");
                String answer = scanner.nextLine().trim().toLowerCase();
                if (answer.equals("да")) {
                    game.reset();
                    System.out.println();
                    System.out.println("НОВАЯ ИГРА!");
                    System.out.println();
                } else {
                    playing = false;
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Ошибка: файл словаря не найден!");
            System.err.println("Убедитесь, что файл 'words_ru.txt' находится в корне проекта.");
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}