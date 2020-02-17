package com.company;

import java.util.Random;
import java.util.Scanner;

public class Main {

    private static Scanner input = new Scanner(System.in);
    private static int[][] board = new int[4][4];
    private static int[] dirLine = {1, 0, -1, 0};
    private static int[] dirColumn = {0, 1, 0, -1};

    public static void main(String[] args) {
        // write your code here
        boolean quit = false;
        char[] commandToDir = new char[128];
        commandToDir['s'] = 0;
        commandToDir['d'] = 1;
        commandToDir['w'] = 2;
        commandToDir['a'] = 3;
        newGame();

        while (!quit) {
            System.out.print("\033[H\033[2J");
            printUI();
            char command;
            command = input.next().charAt(0);
            if (command == ('n')) {
                newGame();
            } else if (command == ('q')) {
                quit = true;
            } else {
                int currentDirection = commandToDir[command];
                System.out.println(currentDirection);
                applyMove(currentDirection);
            }
        }
    }

    private static void newGame() {
        int i = 0, j = 0;
        for (i = 0; i <= 3; ++i) {
            for (j = 0; j <= 3; ++j) {
                board[i][j] = 0;
            }
        }
        addPiece();
    }

    private static void printUI() {

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (board[i][j] == 0) {
                    System.out.printf("%-3s", "   |");
                } else {
                    System.out.printf("%-3s",  " "+board[i][j]+" |");
                }
                // System.out.print(" ");
            }
            System.out.println("\n ---------------");
        }
        System.out.println("N: New Game, Q: Quit, W: UP, S: DOWN, D: RIGHT, A: LEFT\n");
    }

    private static void applyMove(int direction) {
        // int[][] boardCopy = Arrays.copyOf(board,4);
        // System.out.println(Arrays.deepToString(boardCopy));
        int startLine = 0;
        int startColumn = 0;
        int lineStep = 1;
        int columnStep = 1;
        if (direction == 0) {
            startLine = 3;
            lineStep = -1;
        }
        if (direction == 1) {
            startColumn = 3;
            columnStep = -1;
        }
        int movePossible = 0;
        int canAddPiece = 0;
        do {
            movePossible = 0;
            for (int i = startLine; i >= 0 && i < 4; i += lineStep) {
                for (int j = startColumn; j >= 0 && j < 4; j += columnStep) {
                    int nextI = i + dirLine[direction];
                    int nextJ = j + dirColumn[direction];
                    if (board[i][j] != 0 && canDoMove(i, j, nextI, nextJ)) {
                        board[nextI][nextJ] += board[i][j];
                        board[i][j] = 0;
                        movePossible = 1;
                        canAddPiece = 1;
                    }
                }
            }
            printUI();
        } while (movePossible == 1);
        if (canAddPiece == 1) {
            addPiece();
        }
    }

    private static int[] pair() {
        int[] intPair = new int[2];
        Random rand = new Random();

        int occupied = 1;
        int line = 0;
        int column = 0;
        while (occupied == 1) {
            line = rand.nextInt(4);
            column = rand.nextInt(4);
            if (board[line][column] == 0) {
                occupied = 0;
            }
        }
        intPair[0] = line;
        intPair[1] = column;
        return intPair;
    }

    private static boolean canDoMove(int line, int column, int nextLine, int nextColumn) {
        if ((nextLine < 0 || nextColumn < 0) || (nextLine >= 4 || nextColumn >= 4)) {
            return false;
        }
        if ((board[line][column] != board[nextLine][nextColumn]) && (board[nextLine][nextColumn] != 0)) {
            return false;
        }
        return true;
    }

    private static void addPiece() {
        int[] pos = pair();
        board[pos[0]][pos[1]] = 2;
    }
}
