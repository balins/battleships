package uj.java.pwj2019.battleships.client;

import uj.java.pwj2019.battleships.map.Coordinate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Client extends AppClient {
    private Client(){};

    public Client(String host, int port, List<String> mapLines) {
        super(host, port, mapLines);
    }

    @Override
    public void start() throws IOException, InterruptedException {
        System.out.println("Connecting to other player on " + HOST + ", port " + PORT + "...");

        Socket socket = new Socket(HOST, PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        OutputStream out = socket.getOutputStream();

        Coordinate lastGuess = getMyGuess();
        String msg = "start;" + lastGuess.toString() + "\n";
        send(msg, in, out);

        boolean win = startPlayLoop(lastGuess, in, out);

        if(win) {
            win();
        } else {
            lose();
        }

        in.close();
        out.close();

        socket.close();
    }
}
