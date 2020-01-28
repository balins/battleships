package uj.java.pwj2019.battleships.map.explorer;

import uj.java.pwj2019.battleships.map.GameMap;
import uj.java.pwj2019.battleships.map.Coordinate;

import java.util.Arrays;
import java.util.Stack;

public abstract class FieldExplorer {
    final GameMap map;
    final boolean[][] visited;
    final Stack<Coordinate> stack;

    protected FieldExplorer(Coordinate start, GameMap map) {
        this.map = map;
        this.visited = new boolean[10][10];
        for(var row : visited)
            Arrays.fill(row, false);
        this.stack = new Stack<>();
        stack.push(start);
    }

    abstract boolean proceed(Coordinate c);

    public boolean traverse() { //returns boolean value for SunkChecker, return value for SurroundingChecker is ignored
        boolean[][] visited = new boolean[10][10];
        for(var row : visited)
            Arrays.fill(row, false);


        Coordinate c;
        int row, col;
        boolean shouldContinue;

        while(!stack.empty()) {
            c = stack.pop();
            row = c.getRow();
            col = c.getCol();
            visited[row][col] = true;
            Coordinate neighbor;

            if(row > 0) {
                if(!visited[row-1][col]) {
                    neighbor = new Coordinate(row-1, col);
                    shouldContinue = proceed(neighbor);
                    if(!shouldContinue)
                        return false;
                }
            }
            if(row < 9) {
                if(!visited[row+1][col]) {
                    neighbor = new Coordinate(row+1, col);
                    shouldContinue = proceed(neighbor);
                    if(!shouldContinue)
                        return false;
                }
            }
            if(col > 0) {
                if(!visited[row][col-1]) {
                    neighbor = new Coordinate(row, col-1);
                    shouldContinue = proceed(neighbor);
                    if(!shouldContinue)
                        return false;
                }
            }
            if(col < 9) {
                if(!visited[row][col+1]) {
                    neighbor = new Coordinate(row, col+1);
                    shouldContinue = proceed(neighbor);
                    if(!shouldContinue)
                        return false;
                }
            }
        }

        return true;
    }
}
