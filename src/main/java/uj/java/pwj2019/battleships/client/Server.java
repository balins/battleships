package uj.java.pwj2019.battleships.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

public class Server extends AppClient {
    private Server(){};

    public Server(String host, int port, List<String> mapLines) {
        super(host, port, mapLines);
    }

    @Override
    public void start() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("\nWaiting for connection from other player on "
                + HOST + ", port " + PORT + "...\n");

        this.communicator = new Communicator(serverSocket.accept()); //blocking operation

        boolean win = startPlayLoop(null);

        if(win) {
            win();
        } else {
            lose();
        }

        communicator.close();
    }
}
