package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.Coordinate;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client extends AppClient {
    private Client(){};

    public Client(String host, int port, List<String> mapLines) {
        super(host, port, mapLines);
    }

    @Override
    public void start() throws IOException, InterruptedException {
        System.out.println("Connecting to other player on " + HOST + ", port " + PORT + "...");

        this.socket = new Socket(HOST, PORT);

        Coordinate lastGuess = getMyGuess();

        boolean win = startPlayLoop(lastGuess);

        if(win) {
            win();
        } else {
            lose();
        }

        socket.close();
    }
}
