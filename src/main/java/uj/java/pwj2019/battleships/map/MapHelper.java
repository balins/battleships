package uj.java.pwj2019.battleships.map;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class MapHelper {
    static Field[][] convertMap(List<String> mapLines) throws IllegalArgumentException {
        Field[][] map = new Field[10][10];
        int shipFieldsCount = 0;

        for(int i = 0; i < mapLines.size(); i++) {
            for(int j = 0; j < mapLines.get(i). length(); j++) {
                String s = String.valueOf(mapLines.get(i).charAt(j));
                if(s.equals(Field.WATER.toString())) {
                    map[i][j] = Field.WATER;
                } else if(s.equals(Field.SHIP.toString())) {
                    map[i][j] = Field.SHIP;
                    shipFieldsCount++;
                } else {
                    throw new IllegalArgumentException("Format of the provided map is invalid. " +
                            "Use only '" + Field.WATER.toString() + "' for water " +
                            "and '" + Field.SHIP.toString() + "' for ship segment.");
                }
            }
        }

        if(shipFieldsCount != 20) {
            throw new IllegalArgumentException("The quantity of ship segments in the provided map is invalid. " +
                    "Having exactly 20 of them is required.");
        }

        return map;
    }

    static boolean isValid(Field[][] map) {
        boolean[][] visited = new boolean[10][10];
        for(var row : visited)
            Arrays.fill(row, false);

        int[] len = new int[4]; //counts: len[0] -> ships with length 1, len[1] -> ships with length 2...
        Arrays.fill(len, 0);
        int row, col, currentLen;
        Stack<Coordinate> stack = new Stack<>();

        for(int i = 0; i < map.length; i++) {   //find all of the ships on the map
            for(int j = 0; j < map[i].length; j++) {
                if(map[i][j].equals(Field.WATER) || visited[i][j])
                    continue;

                // if found a new field with ship, find all the other parts of it
                currentLen = -1;
                Coordinate c = new Coordinate(i, j);
                stack.push(c);

                while(!stack.empty()) {
                    c = stack.pop();
                    row = c.getRow();
                    col = c.getCol();
                    visited[row][col] = true;
                    currentLen++;

                    if(row > 0) {
                        if(map[row-1][col].equals(Field.SHIP) && !visited[row-1][col]) {
                            stack.push(new Coordinate(row-1, col));
                        }
                    }
                    if(row < map.length-1) {
                        if(map[row+1][col].equals(Field.SHIP) && !visited[row+1][col]) {
                            stack.push(new Coordinate(row+1, col));
                        }
                    }
                    if(col > 0) {
                        if(map[row][col-1].equals(Field.SHIP) && !visited[row][col-1]) {
                            stack.push(new Coordinate(row, col-1));
                        }
                    }
                    if(col < map[i].length-1) {
                        if(map[row][col+1].equals(Field.SHIP) && !visited[row][col+1]) {
                            stack.push(new Coordinate(row, col+1));
                        }
                    }
                }

                if(currentLen > 3)
                    return false;

                len[currentLen]++;

                if(len[0] > 4 || len[1] > 3 || len[2] > 2 || len[3] > 1)
                    return false;
            }
        }

        return len[0] == 4 && len[1] == 3 && len[3] == 2;
    }
}
