package uj.java.pwj2019.battleships.map;

import java.util.Arrays;
import java.util.List;

public class BattleshipsMap {
    final Field[][] map;

    public BattleshipsMap() {
        this.map = new Field[10][10];

        for(var row : map)
            Arrays.fill(row, Field.UNKNOWN);
    }

    public BattleshipsMap(List<String> mapLines) throws IllegalArgumentException {
        this.map = MapHelper.convertMap(mapLines);

        if(!MapHelper.isValid(map)) {
            throw new IllegalArgumentException("Format of the provided map is invalid. " +
                    "Please, read the rules of ships placement provided with the app and then correct your map.");
        }
    }

    public Field getField(Coordinate c) {
        return map[c.getRow()][c.getCol()];
    }

    public void mark(Coordinate c, Field status) {
        map[c.getRow()][c.getCol()] = status;
    }

    public void print() {
        for(var row : map){
            for(var field : row) {
                System.out.print(field.toString());
            }

            System.out.println();
        }
    }


}
