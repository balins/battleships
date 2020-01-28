package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.Coordinate;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client extends AppClient {
    private Client(){};

    public Client(String host, int port, List<String> mapLines) throws IOException {
        super(host, port, mapLines);
        this.communicator = new Communicator(new Socket(HOST, PORT));
    }

    @Override
    public void start() throws IOException, InterruptedException {
        System.out.println("\nConnected to other player!");

        Coordinate myGuess = getMyGuess();

        boolean win = startPlayLoop(myGuess);

        if(win) {
            win();
        } else {
            lose();
        }

        communicator.close();
    }
}
