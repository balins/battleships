package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AppClient {
    final String HOST;
    final int PORT;
    BufferedReader input;
    OutputStream output;
    Socket socket;
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

    protected boolean startPlayLoop(Coordinate lastGuess) throws IOException, InterruptedException {
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.output = socket.getOutputStream();
        if(lastGuess != null) {
            String msg = "start;" + lastGuess.toString() + "\n";
            send(msg);
        }
        String[] received;
        String response;

        while(true) {
            Thread animation = getAnimationPrinter();
            animation.start();
            received = receive().split(";"); //blocking operation
            animation.interrupt();
            animation.join();

            switch (received[0]) {
                case "start":
                    System.out.println("Enemy: Let's begin!");
                    break;
                case "miss":
                    markMyMiss(lastGuess);
                    System.out.println("Enemy: You've missed!");
                    break;
                case "hit":
                    markMyHit(lastGuess);
                    System.out.println("Enemy: I've been hit.");
                    break;
                case "sunk":
                    markMySunk(lastGuess);
                    System.out.println("Enemy: Hit and sunk...");
                    break;
                case "last sunk":
                    markMyHit(lastGuess);
                    System.out.println("Enemy: Last sunk... You won...");
                    input.close();
                    output.close();
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
                        input.close();
                        output.close();
                        return false;
                    default:
                        continue;
                }

                lastGuess = getMyGuess();
                response += ";" + lastGuess.toString();

                send(response);
            }
        }
    }

    protected Coordinate getMyGuess() {
        String guess;
        Coordinate c = null;
        boolean invalidInput = true;

        printMaps();

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
        System.out.println();
        System.out.println("--------------------------- Win ---------------------------");
        printMaps();
    }

    protected void lose() {
        System.out.println();
        System.out.println("--------------------------- Lose ---------------------------");
        printMaps();
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

    protected void markMySunk(Coordinate c) {
        markMyHit(c);
        uncoverSurroundingToSunk(c);
    }

    protected String proceedEnemyGuess(Coordinate c) {
        Status status;

        if(myMap.getField(c).equals(Field.WATER) || myMap.getField(c).equals(Field.MISS)) {
            markEnemyMiss(c);
            status = Status.MISS;
        } else if(myMap.getField(c).equals(Field.HIT)) {
            status = Status.HIT;
        } else { //Field.SHIP
            markEnemyHit(c);
            myShipSegments--;

            if (myShipSegments == 0) {
                status = Status.LAST_SUNK;
            } else {
                status = isSunk(c) ? Status.SUNK : Status.HIT;
            }
        }

        return status.toString();
    }

    protected void printMaps() {
        System.out.println();
        Field[] row;

        System.out.println("\t     Opponent's map\t\t\t\t       Your map\n");
        System.out.println("\t  1 2 3 4 5 6 7 8 9 10\t\t\t\t  1 2 3 4 5 6 7 8 9 10");

        for (int i = 0; i < 10; i++) {
            System.out.print("\t");

            System.out.print((char)(i+65));
            row = enemyMap.getRow(i);
            for (var field : row)
                System.out.print(" " + field.toString());

            System.out.print("\t\t\t\t");

            System.out.print((char)(i+65));
            row = myMap.getRow(i);
            for (var field : row)
                System.out.print(" " + field.toString());

            System.out.println();
        }

        System.out.println();
    }

    private boolean isSunk(Coordinate c) {
        FieldExplorer explorer = new SunkChecker(c, myMap);

        return explorer.traverse();
    }

    private void uncoverSurroundingToSunk(Coordinate c) {
        FieldExplorer explorer = new SurroundingChecker(c, enemyMap);

        explorer.traverse();
    }

    protected String receive() throws IOException, InterruptedException {
        String received;

        received = input.readLine(); //blocking operation

        send("ACK"); //send acknowledgement

        return received;
    }

    protected void send(String message) throws IOException, InterruptedException {
        output.write((message + "\n").getBytes());
        output.flush();

        if(!message.equals("ACK")) { //wait for acknowledgement
            int timeoutExpired = 0;
            socket.setSoTimeout(1000);

            while(true) {
                try {
                    receive();
                    break;
                } catch (IOException e) {
                    timeoutExpired++;
                    if(timeoutExpired == 3) {
                        socket.setSoTimeout(0);
                        throw new SocketTimeoutException("Communication error");
                    } else {
                        output.write((message + "\n").getBytes()); //retry sending message
                        output.flush();
                    }
                }
            }

            socket.setSoTimeout(0);
        }
    }

    protected Thread getAnimationPrinter() {
        return new Thread(() -> {
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
                    }

                    randomNum = ThreadLocalRandom.current().nextInt(666, 888);
                    Thread.sleep(randomNum);

                    for(int i = 0; i < text.length(); i++) {
                        System.out.write("\b".getBytes());
                    }

                    lower = !lower;
                }
            } catch (IOException | InterruptedException ignored) {
                try {
                    System.out.write("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b".getBytes());
                    System.out.write("                                   ".getBytes());
                    System.out.write("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b".getBytes());
                } catch (IOException ignored2){}
            }
        });
    }
}
