package uj.java.pwj2019.battleships.map;

public class SurroundingChecker extends FieldExplorer {
    public SurroundingChecker(Coordinate start, BattleshipsMap map) {
        super(start, map);
    }

    @Override
    boolean proceed(Coordinate c) {
        if (map.getField(c).equals(Field.HIT)) {
            stack.push(c);
        } else if(map.getField(c).equals(Field.UNKNOWN)) {
            map.mark(c, Field.WATER);
        }

        return true; //should always continue until there is nothing more on the stack
    }
}
