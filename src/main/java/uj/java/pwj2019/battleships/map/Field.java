package uj.java.pwj2019.battleships.map;

public enum Field {
    WATER("."), SHIP("#"), MISS("~"), HIT("@"), UNKNOWN("?");

    private String repr;

    Field(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return this.repr;
    }

    public static Field fromChar(char c) {
        switch(c) {
            case '.':
                return WATER;
            case '#':
                return SHIP;
            case '~':
                return MISS;
            case '@':
                return HIT;
            case '?':
                return UNKNOWN;
            default:
                throw new IllegalArgumentException("Unknown cast from " + c + "to enum Field.");
        }
    }
}
