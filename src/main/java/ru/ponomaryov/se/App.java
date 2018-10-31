package ru.ponomaryov.se;

import java.util.Random;
import java.util.Scanner;

/**
 * @author Oleg Ponomaryov
 * @version 1.0.0
 */

public class App {
    private static final char DOT_EMPTY = '.';
    private static final char DOT_X = 'X';
    private static final char DOT_0 = '0';
    private static char[][] map;
    private static int size;
    private static int block;
    private static final Scanner sc = new Scanner(System.in);
    private static final Random rand = new Random();
    private static int lastHumanX, lastHumanY;
    public static void main(String[] args) {
        App.startGame();
    }

    private static boolean isMapFull() {
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                if (map[col][row] == DOT_EMPTY) return false;
            }
        }
        return true;
    }

    private static void aiTurn() {
        int x, y;
        if (!blockHumanTurn(lastHumanX, lastHumanY)) {
            do {
                x = rand.nextInt(size);
                y = rand.nextInt(size);
            } while (isCellValid(x, y));
            System.out.println("AI made turn" + (x + 1) + " " + (y + 1));
            map[x][y] = DOT_0;
        }

    }

    private static boolean blockHumanTurn(int lastHumanTurnX, int lastHumanTurnY) {
        int maxHumanDots = 0;
        int humanDots= 0;
        int x,y;
        for(int col=0; col <size; col++) {
            if(map[col][lastHumanTurnY]==DOT_X) {
                humanDots++;
            } else {
                if(maxHumanDots < humanDots) {
                    maxHumanDots = humanDots;
                    humanDots = 0;
                } else {
                    humanDots = 0;
                }
            }
        }
        if((float)maxHumanDots/(float)block > 0.4F) {
            do {
                x = rand.nextInt(size);
                y = lastHumanTurnY;
            } while (isCellValid(x, y));
            map[x][y] = DOT_0;
            System.out.println("Blocked: " + (x+1) + " " + (y+1));
            return true;
        }
        maxHumanDots = 0;
        humanDots= 0;
        for(int row=0; row <size; row++) {
            if(map[lastHumanTurnX][row]==DOT_X) {
                humanDots++;
            } else {
                if(maxHumanDots < humanDots) {
                    maxHumanDots = humanDots;
                    humanDots = 0;
                } else {
                    humanDots = 0;
                }
            }
        }
        if((float)maxHumanDots/(float)block > 0.4F) {
            do {
                x = lastHumanTurnX;
                y = rand.nextInt(size);
            } while (isCellValid(x, y));
            map[x][y] = DOT_0;
            System.out.println("Blocked: " + (x+1) + " " + (y+1));
            return true;
        }
        return false;
    }

    private static boolean isCellValid(int col, int row) {
        if (col < 0 || col >= size || row < 0 || row >= size) return false;
        return map[col][row] != DOT_EMPTY;
    }


    private static void humanTurn() {
        int col, row;
        do {
            System.out.println("Input coordinate using X Y");
            col = sc.nextInt() - 1;
            row = sc.nextInt() - 1;
        } while (isCellValid(col, row));
        lastHumanX = col;
        lastHumanY = row;
        map[col][row] = DOT_X;
    }

    private static void startGame() {
        initMap();
        prepareMap();
        printMap();
        while (true) {
            humanTurn();
            printMap();
            if (checkWin(DOT_X)) {
                System.out.println("Human won!");
                break;
            }
            if (isMapFull()) {
                System.out.println("It's draw!");
                break;
            }
            aiTurn();
            printMap();
            if (checkWin(DOT_0)) {
                System.out.println("AI won!");
                break;
            }
            if (isMapFull()) {
                System.out.println("It's draw");
                break;
            }
        }
        System.out.println("Game over!");
    }

    private static void initMap() {
        do {
            System.out.println("\nInput map size [3-10]: ");
            size = sc.nextInt();
        } while (size < 3 || size > 10);
        do {
            System.out.println("How many times to repeat blocks to win? [3-" + size + "]: ");
            block = sc.nextInt();
        } while (block < 3 || block > size);
    }

    private static void prepareMap() {
        map = new char[size][size];
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                map[col][row] = DOT_EMPTY;
            }
        }
    }

    private static void printMap() {
        for (int i = 0; i <= size; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int col = 0; col < size; col++) {
            System.out.print((col + 1) + " ");
            for (int row = 0; row < size; row++) {
                System.out.print(map[col][row] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static boolean checkWin(char symbol) {
        int maxXDots;
        int xDots;
        int maxYDots;
        int yDots;
        //check lines
        for (int col = 0; col < size; col++) {
            maxXDots = 0;
            xDots = 0;
            maxYDots = 0;
            yDots = 0;
            for (int row = 0; row < size; row++) {
                if (map[col][row] == symbol) {
                    yDots++;
                } else {
                    if(maxYDots < yDots) {
                        maxYDots = yDots;
                        yDots = 0;
                    } else {
                        yDots = 0;
                    }
                }
                if (map[row][col] == symbol) {
                    xDots++;
                } else {
                    if(maxXDots < xDots) {
                        maxXDots = xDots;
                        xDots = 0;
                    } else {
                        xDots = 0;
                    }
                }
                if (maxXDots >= block || maxYDots >= block) {
                    return true;
                }
            }
        }
        //check diagonal
        int symbolXCount = 0;
        int symbolYCount = 0;
        for (int index = 0; index < size; index++) {
            if (map[index][index] == symbol) {
                symbolXCount++;
            }
            if (map[size - index - 1][index] == symbol) {
                symbolYCount++;
            }
            if (symbolXCount >= block || symbolYCount >= block) {
                return true;
            }
        }
        return false;
    }
}
