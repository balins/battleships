package uj.java.pwj2019.battleships.map;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class MapInitHelper {
    static Field[][] convertMap(List<String> mapLines) throws IllegalArgumentException {
        Field[][] map = new Field[10][10];

        for(int i = 0; i < mapLines.size(); i++)
            for(int j = 0; j < mapLines.get(i).length(); j++)
                map[i][j] = Field.fromChar(mapLines.get(i).charAt(j));

        return map;
    }

    static boolean isValid(Field[][] map) {
        boolean[][] visited = new boolean[10][10];
        for(var row : visited)
            Arrays.fill(row, false);

        int[] ships = new int[4]; //counts: ships[0] -> ships with length 1, ships[1] -> ships with length 2...
        Arrays.fill(ships, 0);

        int row, col, currentLen;
        Stack<Coordinate> stack = new Stack<>();

        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(map[i][j] == Field.WATER || visited[i][j])
                    continue;

                if(map[i][j] != Field.SHIP)
                    return false;

                // if found a new field with ship, find all the other parts of it
                currentLen = 0;
                Coordinate c = new Coordinate(i, j);
                stack.push(c);

                while(!stack.empty()) {
                    c = stack.pop();
                    row = c.getRow();
                    col = c.getCol();
                    visited[row][col] = true;
                    currentLen++;

                    if(row > 0) {
                        if(map[row-1][col] == Field.SHIP && !visited[row-1][col]) {
                            stack.push(new Coordinate(row-1, col));
                        }
                    }
                    if(row < 9) {
                        if(map[row+1][col] == Field.SHIP && !visited[row+1][col]) {
                            stack.push(new Coordinate(row+1, col));
                        }
                    }
                    if(col > 0) {
                        if(map[row][col-1] == Field.SHIP && !visited[row][col-1]) {
                            stack.push(new Coordinate(row, col-1));
                        }
                    }
                    if(col < 9) {
                        if(map[row][col+1] == Field.SHIP && !visited[row][col+1]) {
                            stack.push(new Coordinate(row, col+1));
                        }
                    }
                }

                if(currentLen > 4) //there are no longer ships than 4
                    return false;

                ships[currentLen-1]++;

                if(ships[0] > 4 || ships[1] > 3 || ships[2] > 2 || ships[3] > 1)
                    return false;
            }
        }

        return ships[0] == 4 && ships[1] == 3 && ships[2] == 2;
    }
}
