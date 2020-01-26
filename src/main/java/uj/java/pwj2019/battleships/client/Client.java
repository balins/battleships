package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.Coordinate;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client extends AppClient {
    final String HOST;

    public Client(String host, int port, List<String> mapLines) {
        super(port, mapLines);
        this.HOST = host;
    }

    @Override
    public void start() throws IOException {
        System.out.println("Connecting to other player on " + HOST + ", port " + PORT + "...");

        Socket socket = new Socket(HOST, PORT);

        Coordinate lastGuess = getMyGuess();
        send("start;" + lastGuess.toString(), socket);

        boolean win = startPlayLoop(socket, lastGuess);

        socket.close();

        if(win) {
            win();
        } else {
            lose();
        }
    }
}
