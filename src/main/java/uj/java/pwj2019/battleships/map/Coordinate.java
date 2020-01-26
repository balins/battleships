package uj.java.pwj2019.battleships.map;

public class Coordinate {
    private final int row; //0..9
    private final int col; //0..9

    public Coordinate(String rc) throws IllegalArgumentException {
        this(rc.charAt(0), Integer.parseInt(rc.substring(1)));
    }

    public Coordinate(char row, int col) throws IllegalArgumentException {
        if(row < 65 || (row > 74 && row < 97) || row > 106)
            throw new IllegalArgumentException("Row has to be a letter [Aa-Jj]. Got " + row);
        if(col < 0 || col > 9)
            throw new IllegalArgumentException("Column has to be an integer value in the range between 0 and 9. Got " + col);

        this.row = row < 97 ? row - 65 : row - 97;
        this.col = col-1;
    }

    public Coordinate(int row, int col) throws IllegalArgumentException {
        if(row < 0 || row > 9)
            throw new IllegalArgumentException("Row has to be an integer value in the range between 0 and 9. Got " + row);
        if(col < 0 || col > 9)
            throw new IllegalArgumentException("Column has to be an integer value in the range between 0 and 9. Got " + col);

        this.row = row;
        this.col = col-1;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return String.valueOf((char) (row + 65)) + (col + 1);
    }
}
