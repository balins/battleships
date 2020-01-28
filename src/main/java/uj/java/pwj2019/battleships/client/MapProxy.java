package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.*;
import uj.java.pwj2019.battleships.map.explorer.FieldExplorer;
import uj.java.pwj2019.battleships.map.explorer.SunkChecker;
import uj.java.pwj2019.battleships.map.explorer.SurroundingChecker;

public class MapProxy {
    final GameMap myMap;
    final GameMap enemyMap;
    int myShipSegments;
    int enemyShipSegments;

    public MapProxy(GameMap myMap) {
        this.myMap = myMap;
        this.enemyMap = new GameMap();
        this.myShipSegments = this.enemyShipSegments = 20;
    }

    public void proceedMyGuess(Coordinate c, Command command) {
        switch(command) {
            case MISS:
                enemyMap.mark(c, Field.MISS);
                break;
            case HIT:
                if(enemyMap.getField(c) != Field.HIT) {
                    enemyMap.mark(c, Field.HIT);
                    enemyShipSegments--;
                }
                break;
            case SUNK:
                if(enemyMap.getField(c) != Field.HIT) {
                    enemyMap.mark(c, Field.HIT);
                    enemyShipSegments--;
                    uncoverSurroundingToSunk(c);
                }
                break;
            case LAST_SUNK:
                enemyMap.mark(c, Field.HIT);
                enemyShipSegments--;
                uncoverSurroundingToSunk(c);
                break;
        }
    }

    public Command proceedEnemyGuess(Coordinate c) {
        Command command;

        if(myMap.getField(c).equals(Field.WATER) || myMap.getField(c).equals(Field.MISS)) {
            myMap.mark(c, Field.MISS);
            command = Command.MISS;
        } else if(myMap.getField(c).equals(Field.HIT)) {
            command = Command.HIT;
        } else { //Field.SHIP
            myMap.mark(c, Field.HIT);
            myShipSegments--;

            if (myShipSegments == 0) {
                command = Command.LAST_SUNK;
            } else {
                command = isSunk(c) ? Command.SUNK : Command.HIT;
            }
        }

        return command;
    }

    private boolean isSunk(Coordinate c) {
        FieldExplorer explorer = new SunkChecker(c, myMap);

        return explorer.traverse();
    }

    private void uncoverSurroundingToSunk(Coordinate c) {
        FieldExplorer explorer = new SurroundingChecker(c, enemyMap);

        explorer.traverse();
    }

    public void uncoverUnknownEnemyFields() {
        Coordinate c;

        for(int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                c = new Coordinate(i, j);
                if (enemyMap.getField(c) == Field.UNKNOWN)
                    enemyMap.mark(c, Field.WATER);
            }
        }
    }

    public void print() {
        System.out.println();
        Field[] row;

        System.out.println("\t    Opponent's map\t\t\t\t    Your map\n");
        System.out.println("\t  1 2 3 4 5 6 7 8 9 10\t\t\t      1 2 3 4 5 6 7 8 9 10");

        for (int i = 0; i < 10; i++) {
            System.out.print("\t");

            System.out.print((char)(i+65));
            row = enemyMap.getRow(i);
            for (var field : row)
                System.out.print(" " + field.toString());

            System.out.print("\t\t\t    ");

            System.out.print((char)(i+65));
            row = myMap.getRow(i);
            for (var field : row)
                System.out.print(" " + field.toString());

            System.out.println();
        }

        System.out.println();

        System.out.println("\t Segments left: " + enemyShipSegments + "/20\t\t\t     Segments left: " + myShipSegments + "/20");

        System.out.println();
    }
}
