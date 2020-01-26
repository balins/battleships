package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.BattleshipsMap;
import uj.java.pwj2019.battleships.map.Coordinate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AppClient {
    final int PORT;
    BattleshipsMap myMap;
    BattleshipsMap enemyMap;

    protected AppClient(int port, List<String> mapLines) throws IllegalArgumentException {
        this.PORT = port;
        this.myMap = new BattleshipsMap(mapLines);
        this.enemyMap = new BattleshipsMap();
    }

    public abstract void start() throws IOException;

    protected boolean startPlayLoop(Socket socket, Coordinate lastGuess) throws IOException {
        String[] received;
        String response;
        Thread animation = getAnimationPrinter();

        while(true) {
            animation.start();
            received = receive(0, socket).split(";", 2); //blocking operation
            animation.interrupt();

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
                    System.out.println("I've been hit!");
                    break;
                case "sunk":
                    markMySunk(lastGuess);
                    System.out.println("Hit and sunk!");
                    break;
                case "last sunk":
                    markMySunk(lastGuess);
                    System.out.println("Last sunk! You won.");
                    return true;
                default:
                    continue;
            }

            if (received.length > 1) {
                Coordinate c = new Coordinate(received[1]);
                System.out.println("Enemy: " + c.toString());
                response = proceedEnemyGuess(c);

                if(!response.equals("last sunk")) {
                    lastGuess = getMyGuess();
                    response += ";" + lastGuess.toString();
                } else {
                    return false;
                }

                response += "\n";

                send(response, socket);
            }
        }
    }

    protected Coordinate getMyGuess() {
        String guess;
        Coordinate c = null;
        boolean invalidInput = true;

        while(invalidInput) {
            System.out.println("Your guess: ");
            invalidInput = false;
            guess = new Scanner(System.in).nextLine();
            try {
                c = new Coordinate(guess);
            } catch (IllegalArgumentException e) {
                invalidInput = true;
                System.out.println(e.getMessage() + " Retry.");
            }
        }
        
        return c;
    }

    protected void win() {
        //todo implement
    }

    protected void lose() {
        //todo implement
    }

    protected void markEnemyMiss(Coordinate c) {
        //todo implement
    }

    protected void markMyMiss(Coordinate c) {
        //todo implement
    }

    protected void markEnemyHit(Coordinate c) {
        //todo implement
    }

    protected void markMyHit(Coordinate c) {
        //todo implement
    }

    protected void markEnemySunk(Coordinate c) {
        //todo implement
    }

    protected void markMySunk(Coordinate c) {
        //todo implement
    }

    protected String proceedEnemyGuess(Coordinate c) {
        //todo implement
        return null;
    }

    protected void printMyMap() {
        //todo implement
    }

    protected String receive(int timeout, Socket socket) throws IOException {
        String received;
        socket.setSoTimeout(timeout);

        send("ACK", socket); //send acknowledgement

        try(BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        )) {
            received = in.readLine(); //blocking operation (waits timeout ms)
        }

        return received.trim();
    }

    protected void send(String message, Socket socket) throws IOException {
        try(OutputStream out = socket.getOutputStream()) {
            out.write(message.getBytes());
            if(message.equals("ACK")) out.flush(); //send asap if the message was acknowledgement
        }

        if(!message.equals("ACK")) { //wait for acknowledgement
            int timeoutExpired = 0;

            while(timeoutExpired < 3) {
                try {
                    receive(1000, socket);
                    return;
                } catch (IOException e) {
                    timeoutExpired++;
                    try(OutputStream out = socket.getOutputStream()) {
                        out.write(message.getBytes()); //retry sending message
                    }
                }
            }

            throw new SocketTimeoutException("Communication error");
        }
    }

    protected Thread getAnimationPrinter() {
        Runnable animation = () -> {
            int chars = 0;

            try {
                System.out.write("Enemy: ".getBytes());

                while(true) {
                    for(int i = 0; i < 3; i++) {
                        Thread.sleep(1000);
                        System.out.write(".".getBytes());
                        chars++;
                    }
                    Thread.sleep(1000);
                    for(int i = 0; i < 3; i++) {
                        System.out.print("\b");
                        chars--;
                    }
                }
            } catch (IOException | InterruptedException ignored) {
                for(int i = 0; i < chars; i++)
                    System.out.print("\b");
            }
        };

        return new Thread(animation);
    }
}