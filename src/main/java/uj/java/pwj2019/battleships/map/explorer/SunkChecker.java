package uj.java.pwj2019.battleships.map.explorer;

import uj.java.pwj2019.battleships.map.GameMap;
import uj.java.pwj2019.battleships.map.Coordinate;
import uj.java.pwj2019.battleships.map.Field;

public class SunkChecker extends FieldExplorer {
    public SunkChecker(Coordinate start, GameMap map) {
        super(start, map);
    }

    @Override
    boolean proceed(Coordinate c) {
        Field field = map.getField(c);

        if (field == Field.SHIP)
            return false; //should not continue, cause there is some segment of the ship that has not been hit yet

        if (field == Field.HIT)
            stack.push(c);

        return true;
    }
}
