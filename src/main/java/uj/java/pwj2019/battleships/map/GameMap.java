package uj.java.pwj2019.battleships.map;

import java.util.Arrays;
import java.util.List;

public class GameMap {
    final Field[][] map;

    public GameMap() {
        this.map = new Field[10][10];

        for(var row : map)
            Arrays.fill(row, Field.UNKNOWN);
    }

    public GameMap(List<String> mapLines) throws IllegalArgumentException {
        this.map = MapInitHelper.convertMap(mapLines);
        if(!MapInitHelper.isValid(map))
            throw new IllegalArgumentException("Format of the provided map is invalid. " +
                    "Please, read the rules of ships placement provided with the app and then correct your map.");
    }

    public Field getField(Coordinate c) {
        return map[c.getRow()][c.getCol()];
    }

    public void mark(Coordinate c, Field status) {
        map[c.getRow()][c.getCol()] = status;
    }

    public Field[] getRow(int i) {
        return map[i];
    }
}
