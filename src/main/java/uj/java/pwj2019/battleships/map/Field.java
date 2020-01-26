package uj.java.pwj2019.battleships.map;

public enum Field {
    WATER("."), SHIP("#"), MISS("~"), HIT("@"), SUNK("!"), UNKNOWN("?");

    private String repr;

    Field(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return this.repr;
    }
}
