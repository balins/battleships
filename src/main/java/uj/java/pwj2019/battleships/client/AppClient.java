package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AppClient {
    final String HOST;
    final int PORT;
    BattleshipsMap myMap;
    BattleshipsMap enemyMap;
    int myShipSegments;

    protected AppClient(){
        HOST = "";
        PORT = 0;
    };

    protected AppClient(String host, int port, List<String> mapLines) throws IllegalArgumentException {
        this.HOST = host;
        this.PORT = port;
        this.myMap = new BattleshipsMap(mapLines);
        this.enemyMap = new BattleshipsMap();
        this.myShipSegments = 20;
    }

    private enum Status {
        MISS("miss"), HIT("hit"), SUNK("sunk"), LAST_SUNK("last sunk");

        private String repr;

        Status(String status) {
            this.repr = status;
        }

        @Override
        public String toString() {
            return this.repr;
        }
    }

    public abstract void start() throws IOException, InterruptedException;

    protected boolean startPlayLoop(Coordinate lastGuess, BufferedReader in, OutputStream out) throws IOException, InterruptedException {
        String[] received;
        String response;

        while(true) {
            Thread animation = getAnimationPrinter();
            animation.start();
            received = receive(in, out).split(";"); //blocking operation
            animation.interrupt();
            animation.join();

            switch (received[0]) {
                case "start":
                    System.out.println("Let's begin!");
                    break;
                case "miss":
                    markMyMiss(lastGuess);
                    System.out.println("You've missed!");
                    break;
                case "hit":
                    markMyHit(lastGuess);
                    System.out.println("I've been hit.");
                    break;
                case "sunk":
                    markMySunk(lastGuess);
                    System.out.println("Hit and sunk...");
                    break;
                case "last sunk":
                    markMySunk(lastGuess);
                    System.out.println("Last sunk... You won...");
                    in.close();
                    out.close();
                    return true;
                default:
                    continue;
            }

            if (received.length > 1) {
                Coordinate c = new Coordinate(received[1]);
                System.out.println("Enemy: " + c.toString());
                response = proceedEnemyGuess(c);

                switch(response) {
                    case "miss":
                        System.out.println("*Enemy has missed!*");
                        break;
                    case "hit":
                        System.out.println("*Enemy has hit your ship :/*");
                        break;
                    case "sunk":
                        System.out.println("*Enemy has sunk your ship :(*");
                        break;
                    case "last sunk":
                        System.out.println("*Enemy has sunk your last ship :'(*");
                        in.close();
                        out.close();
                        return false;
                    default:
                        continue;
                }

                lastGuess = getMyGuess();
                response += ";" + lastGuess.toString();

                send(response, in, out);
            }
        }
    }

    protected Coordinate getMyGuess() {
        String guess;
        Coordinate c = null;
        boolean invalidInput = true;

        while(invalidInput) {
            System.out.print("Your guess: ");
            invalidInput = false;
            guess = new Scanner(System.in).nextLine();
            if(guess == null || guess.equals("")) {
                invalidInput = true;
                System.out.println("You have to enter row and column of chosen field. Retry.");
                continue;
            }
            try {
                c = new Coordinate(guess);
            } catch (IllegalArgumentException e) {
                invalidInput = true;
                System.out.println(e.getMessage() + ". Retry.");
            }
        }
        
        return c;
    }

    protected void win() {
        System.out.println("Win");
        enemyMap.print();
        System.out.println();
        myMap.print();
    }

    protected void lose() {
        System.out.println("Win");
        enemyMap.print();
        System.out.println();
        myMap.print();
    }

    protected void markEnemyMiss(Coordinate c) {
        myMap.mark(c, Field.MISS);
    }

    protected void markMyMiss(Coordinate c) {
        enemyMap.mark(c, Field.MISS);
    }

    protected void markEnemyHit(Coordinate c) {
        myMap.mark(c, Field.HIT);
    }

    protected void markMyHit(Coordinate c) {
        enemyMap.mark(c, Field.HIT);
    }

    protected void markEnemySunk(Coordinate c) {
        uncoverSurroundingToSunk(c, enemyMap);
    }

    protected void markMySunk(Coordinate c) {
        myMap.mark(c, Field.HIT);
    }

    protected String proceedEnemyGuess(Coordinate c) {
        Status status;

        if(myMap.getField(c).equals(Field.WATER) || myMap.getField(c).equals(Field.MISS)) {
            markEnemyMiss(c);
            status = Status.MISS;
        } else if(myMap.getField(c).equals(Field.HIT)) {
            status = Status.HIT;
        } else { //Field.SHIP
            myMap.mark(c, Field.HIT);
            myShipSegments--;

            if (myShipSegments == 0) {
                status = Status.LAST_SUNK;
            } else {
                status = isSunk(c, myMap) ? Status.SUNK : Status.HIT;
            }
        }

        return status.toString();
    }

    protected void printMyMap() {
        myMap.print();
    }

    protected void printEnemyMap() {
        enemyMap.print();
    }

    private boolean isSunk(Coordinate c, BattleshipsMap map) {
        FieldExplorer explorer = new SunkChecker(c, map);

        return explorer.traverse();
    }

    private void uncoverSurroundingToSunk(Coordinate c, BattleshipsMap map) {
        FieldExplorer explorer = new SurroundingChecker(c, map);

        explorer.traverse();
    }

    protected String receive(BufferedReader in, OutputStream out) throws IOException, InterruptedException {
        String received;

        do {
            received = in.readLine(); //blocking operation (waits timeout ms)
        } while(received.equals("ACK"));

        send("ACK", in, out); //send acknowledgement

        return received.trim();
    }

    protected void send(String message, BufferedReader in, OutputStream out) throws IOException, InterruptedException {
        out.write((message + "\n").getBytes());
        out.flush();

//        if(!message.equals("ACK")) { //wait for acknowledgement
//            int timeoutExpired = 0;
//
//            while(timeoutExpired < 3) {
//                Thread th = new Thread(() -> {
//                    try {
//                        receive(in, out);
//                    } catch (IOException | InterruptedException ignored) {}
//                });
//                th.start();
//                Thread.sleep(10000);
//                if(th.isAlive()) {
//                    th.interrupt();
//                    timeoutExpired++;
//                    out.write(message.getBytes()); //retry sending message
//                    out.flush();
//                } else {
//                    return;
//                }
//            }
//
//            throw new SocketTimeoutException("Communication error");
//        }
    }

    protected Thread getAnimationPrinter() {
        return new Thread(() -> {
            int chars = 0;
            String text = "waiting for your opponent's move...";
            boolean lower = true;
            int randomNum;
            try {
                while(true) {
                    text = lower ? text.toLowerCase() : text.toUpperCase();

                    for(int i = 0; i < text.length(); i++) {
                        randomNum = ThreadLocalRandom.current().nextInt(44, 777);
                        Thread.sleep(randomNum);

                        System.out.write(text.substring(i,i+1).getBytes());
                        chars++;
                    }

                    randomNum = ThreadLocalRandom.current().nextInt(666, 888);
                    Thread.sleep(randomNum);

                    for(int i = 0; i < text.length(); i++) {
                        System.out.write("\b".getBytes());
                        chars--;
                    }

                    lower = !lower;
                }
            } catch (IOException | InterruptedException ignored) {
                try {
                    for(int i = 0; i < chars; i++) {
                        System.out.write("\b".getBytes());
                    }
                } catch (IOException ignored2){}
            }
        });
    }
}
