package uj.java.pwj2019.battleships.map;

public class Coordinate {
    private final int row; //0..9
    private final int col; //0..9

    public Coordinate(String rc) throws IllegalArgumentException {
        if(rc == null || rc.equals(""))
            throw new IllegalArgumentException("You have to provide both row and column of chosen field");

        int row = rc.charAt(0);
        int col = Integer.parseInt(rc.substring(1))-1; //user provides number from 1..10 that needs to be internally
                                                        //converted to a number from range 0 to 9

        if(row < 65 || (row > 74 && row < 97) || row > 106)
            throw new IllegalArgumentException("Row has to be a letter [Aa-Jj]. Got " + row);
        if(col < 0 || col > 9)
            throw new IllegalArgumentException("Column has to be an integer value in the range between 1 and 10. " +
                    "Got " + (col+1));

        this.row = row < 97 ? row - 65 : row - 97;
        this.col = col;
    }

    public Coordinate(int row, int col) throws IllegalArgumentException {
        if(row < 0 || row > 9)
            throw new IllegalArgumentException("Row has to be an integer value in the range between 0 and 9. " +
                    "Got " + row);
        if(col < 0 || col > 9)
            throw new IllegalArgumentException("Column has to be an integer value in the range between 0 and 9. " +
                    "Got " + col);

        this.row = row;
        this.col = col;
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
