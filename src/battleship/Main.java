package battleship;

import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final int FIELD_SIZE = 10;
    private static final int SHIP_COUNT = 5;

    private static final char EMPTY = '~';
    private static final char SHIP = 'O';
    private static final char HIT = 'X';
    private static final char MISS = 'M';

    private static final String[] SHIP_NAMES = {
            "Aircraft Carrier",
            "Battleship",
            "Submarine",
            "Cruiser",
            "Destroyer"
    };

    private static final int[] SHIP_LENGTHS = {
            5,
            4,
            3,
            3,
            2
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Player player1 = new Player();
        Player player2 = new Player();

        // Player 1 places ships
        placeFleet(scanner, player1, 1);

        passMove(scanner);

        // Player 2 places ships
        placeFleet(scanner, player2, 2);

        passMove(scanner);

        // Start the battle
        playGame(scanner, player1, player2);
    }

    private static void placeFleet(
            Scanner scanner,
            Player player,
            int playerNumber
    ) {

        if (playerNumber == 1) {
            System.out.println(
                    "Player 1, place your ships on the game field"
            );
        } else {
            System.out.println(
                    "Player 2, place your ships to the game field"
            );
        }

        System.out.println();

        printField(player.field, false);

        for (int i = 0; i < SHIP_COUNT; i++) {

            System.out.println();
            System.out.println(
                    "Enter the coordinates of the "
                            + SHIP_NAMES[i]
                            + " ("
                            + SHIP_LENGTHS[i]
                            + " cells):"
            );
            System.out.println();

            placeShip(
                    scanner,
                    player,
                    SHIP_NAMES[i],
                    SHIP_LENGTHS[i],
                    i
            );

            System.out.println();
            printField(player.field, false);
        }

        System.out.println();
    }

    private static void placeShip(
            Scanner scanner,
            Player player,
            String shipName,
            int expectedLength,
            int shipId
    ) {

        while (true) {

            String start = scanner.next();
            String end = scanner.next();

            int[] startPosition = parseCoordinate(start);
            int[] endPosition = parseCoordinate(end);

            if (startPosition == null || endPosition == null) {
                System.out.println();
                System.out.println(
                        "Error! Wrong ship location! Try again:"
                );
                System.out.println();
                continue;
            }

            int startRow = startPosition[0];
            int startColumn = startPosition[1];

            int endRow = endPosition[0];
            int endColumn = endPosition[1];

            // The ship must be horizontal or vertical
            if (startRow != endRow
                    && startColumn != endColumn) {

                System.out.println();
                System.out.println(
                        "Error! Wrong ship location! Try again:"
                );
                System.out.println();
                continue;
            }

            int actualLength;

            if (startRow == endRow) {
                actualLength =
                        Math.abs(startColumn - endColumn) + 1;
            } else {
                actualLength =
                        Math.abs(startRow - endRow) + 1;
            }

            // Check the required ship length
            if (actualLength != expectedLength) {

                System.out.println();
                System.out.println(
                        "Error! Wrong length of the "
                                + shipName
                                + "! Try again:"
                );
                System.out.println();

                continue;
            }

            // Ships must not touch each other
            if (isTooClose(
                    player.field,
                    startRow,
                    startColumn,
                    endRow,
                    endColumn
            )) {

                System.out.println();
                System.out.println(
                        "Error! You placed it too close to another one. Try again:"
                );
                System.out.println();

                continue;
            }

            putShip(
                    player,
                    startRow,
                    startColumn,
                    endRow,
                    endColumn,
                    shipId
            );

            break;
        }
    }

    private static void playGame(
            Scanner scanner,
            Player player1,
            Player player2
    ) {

        int currentPlayer = 1;

        while (true) {

            Player attacker;
            Player defender;

            if (currentPlayer == 1) {
                attacker = player1;
                defender = player2;
            } else {
                attacker = player2;
                defender = player1;
            }

            // Opponent's field with fog of war
            printField(defender.field, true);

            System.out.println("---------------------");

            // Current player's own field
            printField(attacker.field, false);

            System.out.println();
            System.out.println(
                    "Player "
                            + currentPlayer
                            + ", it's your turn:"
            );
            System.out.println();

            boolean validShot = false;

            while (!validShot) {

                String coordinate = scanner.next();

                int[] position = parseCoordinate(coordinate);

                if (position == null) {

                    System.out.println();
                    System.out.println(
                            "Error! You entered wrong coordinates! Try again:"
                    );
                    System.out.println();

                    continue;
                }

                validShot = true;

                int row = position[0];
                int column = position[1];

                ShotResult result =
                        shoot(defender, row, column);

                System.out.println();

                if (result == ShotResult.WIN) {

                    System.out.println(
                            "You sank the last ship. You won. Congratulations!"
                    );

                    return;

                } else if (result == ShotResult.SUNK) {

                    System.out.println(
                            "You sank a ship!"
                    );

                } else if (result == ShotResult.HIT) {

                    System.out.println(
                            "You hit a ship!"
                    );

                } else {

                    System.out.println(
                            "You missed!"
                    );
                }
            }

            passMove(scanner);

            if (currentPlayer == 1) {
                currentPlayer = 2;
            } else {
                currentPlayer = 1;
            }
        }
    }

    private static ShotResult shoot(
            Player defender,
            int row,
            int column
    ) {

        char cell = defender.field[row][column];

        // Shooting a ship for the first time
        if (cell == SHIP) {

            defender.field[row][column] = HIT;

            int shipId =
                    defender.shipIds[row][column];

            if (isShipSunk(defender, shipId)) {

                if (!defender.sunkShips[shipId]) {
                    defender.sunkShips[shipId] = true;
                    defender.sunkCount++;
                }

                if (defender.sunkCount == SHIP_COUNT) {
                    return ShotResult.WIN;
                }

                return ShotResult.SUNK;
            }

            return ShotResult.HIT;
        }

        // Shooting an already hit ship again
        if (cell == HIT) {
            return ShotResult.HIT;
        }

        // Shooting empty water
        if (cell == EMPTY) {
            defender.field[row][column] = MISS;
        }

        // EMPTY and already missed cells are both misses
        return ShotResult.MISS;
    }

    private static boolean isShipSunk(
            Player player,
            int shipId
    ) {

        for (int row = 0;
             row < FIELD_SIZE;
             row++) {

            for (int column = 0;
                 column < FIELD_SIZE;
                 column++) {

                if (player.shipIds[row][column] == shipId
                        && player.field[row][column] == SHIP) {

                    return false;
                }
            }
        }

        return true;
    }

    private static int[] parseCoordinate(
            String coordinate
    ) {

        if (coordinate.length() < 2
                || coordinate.length() > 3) {

            return null;
        }

        char rowLetter = coordinate.charAt(0);

        if (rowLetter < 'A'
                || rowLetter > 'J') {

            return null;
        }

        int column;

        try {

            column =
                    Integer.parseInt(
                            coordinate.substring(1)
                    );

        } catch (NumberFormatException e) {

            return null;
        }

        if (column < 1 || column > 10) {
            return null;
        }

        int row =
                rowLetter - 'A';

        int columnIndex =
                column - 1;

        return new int[]{
                row,
                columnIndex
        };
    }

    private static boolean isTooClose(
            char[][] field,
            int startRow,
            int startColumn,
            int endRow,
            int endColumn
    ) {

        int minRow =
                Math.min(startRow, endRow);

        int maxRow =
                Math.max(startRow, endRow);

        int minColumn =
                Math.min(startColumn, endColumn);

        int maxColumn =
                Math.max(startColumn, endColumn);

        for (int row = minRow;
             row <= maxRow;
             row++) {

            for (int column = minColumn;
                 column <= maxColumn;
                 column++) {

                // Check the current cell and all surrounding cells
                for (int rowOffset = -1;
                     rowOffset <= 1;
                     rowOffset++) {

                    for (int columnOffset = -1;
                         columnOffset <= 1;
                         columnOffset++) {

                        int nearbyRow =
                                row + rowOffset;

                        int nearbyColumn =
                                column + columnOffset;

                        if (nearbyRow >= 0
                                && nearbyRow < FIELD_SIZE
                                && nearbyColumn >= 0
                                && nearbyColumn < FIELD_SIZE) {

                            if (field[nearbyRow][nearbyColumn]
                                    == SHIP) {

                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private static void putShip(
            Player player,
            int startRow,
            int startColumn,
            int endRow,
            int endColumn,
            int shipId
    ) {

        if (startRow == endRow) {

            // Horizontal ship
            int minColumn =
                    Math.min(startColumn, endColumn);

            int maxColumn =
                    Math.max(startColumn, endColumn);

            for (int column = minColumn;
                 column <= maxColumn;
                 column++) {

                player.field[startRow][column] = SHIP;

                player.shipIds[startRow][column] =
                        shipId;
            }

        } else {

            // Vertical ship
            int minRow =
                    Math.min(startRow, endRow);

            int maxRow =
                    Math.max(startRow, endRow);

            for (int row = minRow;
                 row <= maxRow;
                 row++) {

                player.field[row][startColumn] =
                        SHIP;

                player.shipIds[row][startColumn] =
                        shipId;
            }
        }
    }

    private static void printField(
            char[][] field,
            boolean fogOfWar
    ) {

        System.out.println(
                "  1 2 3 4 5 6 7 8 9 10"
        );

        for (int row = 0;
             row < FIELD_SIZE;
             row++) {

            System.out.print(
                    (char) ('A' + row)
            );

            for (int column = 0;
                 column < FIELD_SIZE;
                 column++) {

                char cell =
                        field[row][column];

                // Hide untouched ships from the opponent
                if (fogOfWar && cell == SHIP) {
                    cell = EMPTY;
                }

                System.out.print(
                        " " + cell
                );
            }

            System.out.println();
        }
    }

    private static void passMove(
            Scanner scanner
    ) {

        System.out.println();
        System.out.println(
                "Press Enter and pass the move to another player"
        );

        // Consume the rest of the current input line
        scanner.nextLine();

        // Wait until the player presses Enter
        scanner.nextLine();

        clearScreen();
    }

    private static void clearScreen() {

        // Hide the previous player's field
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private static class Player {

        private final char[][] field =
                new char[FIELD_SIZE][FIELD_SIZE];

        private final int[][] shipIds =
                new int[FIELD_SIZE][FIELD_SIZE];

        private final boolean[] sunkShips =
                new boolean[SHIP_COUNT];

        private int sunkCount = 0;

        private Player() {

            for (char[] row : field) {
                Arrays.fill(row, EMPTY);
            }

            for (int[] row : shipIds) {
                Arrays.fill(row, -1);
            }
        }
    }

    private enum ShotResult {
        MISS,
        HIT,
        SUNK,
        WIN
    }
}
