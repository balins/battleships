package uj.java.pwj2019.battleships.map.explorer;

import uj.java.pwj2019.battleships.map.GameMap;
import uj.java.pwj2019.battleships.map.Coordinate;
import uj.java.pwj2019.battleships.map.Field;

public class SurroundingChecker extends FieldExplorer {
    public SurroundingChecker(Coordinate start, GameMap map) {
        super(start, map);
    }

    @Override
    boolean proceed(Coordinate c) {
        Field field = map.getField(c);

        if (field == Field.HIT)
            stack.push(c);
        else if(field == Field.UNKNOWN)
            map.mark(c, Field.WATER);

        return true; //should always continue until there is no more uncovered surrounding fields
    }
}