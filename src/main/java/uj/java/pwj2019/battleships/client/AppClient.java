package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.*;

import java.io.*;
import java.util.*;

public abstract class AppClient {
    final String HOST;
    final int PORT;
    final MapProxy maps;
    Communicator communicator;

    protected AppClient(){
        HOST = "";
        PORT = 0;
        this.maps = new MapProxy(new GameMap());
    };

    protected AppClient(String host, int port, List<String> mapLines) throws IllegalArgumentException {
        this.HOST = host;
        this.PORT = port;
        this.maps = new MapProxy(new GameMap(mapLines));
    }

    public abstract void start() throws IOException, InterruptedException;

    protected boolean startPlayLoop(Coordinate myGuess) throws IOException, InterruptedException {
        if(myGuess != null) {
            String msg = Command.START.toString() + ";" + myGuess.toString();
            communicator.send(msg);
        } else {
            System.out.write("A player has connected!\n\n".getBytes());
        }

        String[] received;
        Command command, outcome;
        Coordinate enemyGuess;
        String response;

        while(true) {
            Thread animation = getAnimationPrinter();
            animation.start();
            received = communicator.receive(true).split(";"); //blocking operation
            animation.interrupt();
            animation.join(); //wait for end of the animation

            command = Command.valueOf(received[0]); //got response
            maps.proceedMyGuess(myGuess, command);

            System.out.print("Enemy: ");
            switch (command) {
                case START:
                    System.out.println("Let's begin!");
                    break;
                case MISS:
                    System.out.println("You've missed!");
                    break;
                case HIT:
                    System.out.println("I've been hit.");
                    break;
                case SUNK:
                    System.out.println("Hit and sunk...");
                    break;
                case LAST_SUNK:
                    System.out.println("Last sunk... You won...");
                    return true;
            }

            enemyGuess = new Coordinate(received[1]);
            System.out.println("Enemy: " + enemyGuess.toString());
            outcome = maps.proceedEnemyGuess(enemyGuess);

            System.out.print("* Enemy has ");
            switch(outcome) {
                case MISS:
                    System.out.println("missed! *");
                    break;
                case HIT:
                    System.out.println("hit your ship :/ *");
                    break;
                case SUNK:
                    System.out.println("sunk your ship :( *");
                    break;
                case LAST_SUNK:
                    System.out.println("sunk your last ship :'( *");
                    response = outcome.toString() + ";";
                    communicator.send(response);
                    return false;
            }

            myGuess = getMyGuess();
            response = outcome.toString() + ";" + myGuess.toString();

            communicator.send(response);
        }
    }

    protected Coordinate getMyGuess() {
        String guess;
        Coordinate c = null;
        boolean invalidInput;

        maps.print();

        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Your guess: ");
            guess = sc.nextLine().replaceAll("\\s+", "");

            try {
                c = new Coordinate(guess);
                invalidInput = false;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + ". Retry.");
                invalidInput = true;
            }
        } while(invalidInput);
        
        return c;
    }

    protected void printEnemyContext(Command cmd) {

    }

    protected void win() {
        System.out.println();
        System.out.println("\t  --------------------------- YOU WIN ---------------------------");
        maps.print();
    }

    protected void lose() {
        System.out.println();
        System.out.println("\t  --------------------------- YOU LOSE ---------------------------");
        maps.print();
    }

    protected Thread getAnimationPrinter() {
        return new Thread(() -> {
            String text = "waiting for your opponent...";
            boolean lower = true;

            try {
                while(true) {
                    text = lower ? text.toLowerCase() : text.toUpperCase();

                    for(int i = 0; i < text.length(); i++) {
                        Thread.sleep(350);

                        System.out.write(text.charAt(i));
                        System.out.flush();
                    }

                    Thread.sleep(800);

                    for(int i = 0; i < text.length(); i++) {
                        Thread.sleep(150);

                        System.out.write('\b');
                        System.out.write(' ');
                        System.out.write('\b');
                        System.out.flush();
                    }

                    lower = !lower;
                }
            } catch (InterruptedException ignored) {
                for(int i = 0; i < 35; i++) {
                    System.out.write('\b');
                    System.out.write(' ');
                    System.out.write('\b');
                }
                System.out.flush();
            }
        });
    }
}
