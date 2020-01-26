package uj.java.pwj2019.battleships.map;

import java.util.Arrays;
import java.util.Stack;

public abstract class FieldExplorer {
    final BattleshipsMap map;
    final boolean[][] visited;
    final Stack<Coordinate> stack;

    protected FieldExplorer(Coordinate start, BattleshipsMap map) {
        this.map = map;
        this.visited = new boolean[10][10];
        for(var row : visited)
            Arrays.fill(row, false);
        this.stack = new Stack<>();
        stack.push(start);
    }

    abstract boolean proceed(Coordinate c);

    public boolean traverse() {
        boolean[][] visited = new boolean[10][10];
        for(var row : visited)
            Arrays.fill(row, false);

        int row, col;
        Stack<Coordinate> stack = new Stack<>();
        Coordinate c;
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
