package uj.java.pwj2019.battleships.client;

public enum Command {
    START("START"), MISS("MISS"), HIT("HIT"), SUNK("SUNK"), LAST_SUNK("LAST_SUNK");

    private String repr;

    Command(String status) {
        this.repr = status;
    }

    @Override
    public String toString() {
        return this.repr;
    }
}
