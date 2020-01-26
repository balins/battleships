package uj.java.pwj2019.battleships;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends AppClient {
    public Server(int port, List<String> mapLines) {
        super(port, mapLines);
    }

    @Override
    protected void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        String[] received;
        String response = "";

        System.out.println("Waiting for connection from other player on "
                + serverSocket.getInetAddress().toString() + ", port " + PORT + "...");

        Socket socket = serverSocket.accept(); //blocking operation
        boolean playing = false;
        Coordinate myGuess = new Coordinate(0,0);

        do {
            Thread animation = new Thread(() -> {
                int chars = 7;
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
                        System.out.print("\r");
                    }
                } catch (IOException | InterruptedException ignored) {
                    for(int i = 0; i < chars; i++)
                        System.out.print("\b");

                    System.out.print("\r");
                }
            });
            animation.start();
            received = receive(0, socket).split(";", 2); //blocking operation
            animation.interrupt();

            System.out.print("Enemy: ");
            switch (received[0]) {
                case "start":
                    playing = true;
                    System.out.println("Let's begin!");
                    break;
                case "miss":
                    markMyMiss(myGuess);
                    System.out.println("You've missed!");
                    break;
                case "Hit!":
                    markMyHit(myGuess);
                    System.out.println("We've got shot!");
                    break;
                case "sunk":
                    markMySunk(myGuess);
                    System.out.println("Hit and sunk!");
                    break;
                case "last sunk":
                    markMySunk(myGuess);
                    System.out.println("Last sunk! You won.");
                    playing = false;
                    win();
                    break;
                default:
                    continue;
            }

            if (received.length > 1) {
                Coordinate c = new Coordinate(received[1]);
                System.out.println("Enemy: " + c.toString());
                response = proceedEnemyGuess(c) + ";" + getMyGuess() + "\n";

                send(response, socket);
            }

        } while(playing);

        socket.close();
    }
}
