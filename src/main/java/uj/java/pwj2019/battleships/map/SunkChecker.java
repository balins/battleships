package uj.java.pwj2019.battleships.map;

public class SunkChecker extends FieldExplorer {
    public SunkChecker(Coordinate start, BattleshipsMap map) {
        super(start, map);
    }

    @Override
    boolean proceed(Coordinate c) {
        if (map.getField(c).equals(Field.SHIP))
            return false; //should not continue, cause there is some segment of the ship that has not been hit yet

        if (map.getField(c).equals(Field.HIT))
            stack.push(c);

        return true;
    }
}
