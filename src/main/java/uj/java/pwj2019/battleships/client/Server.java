package uj.java.pwj2019.battleships.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Server extends AppClient {
    private Server(){};

    public Server(String host, int port, List<String> mapLines) {
        super(host, port, mapLines);
    }

    @Override
    public void start() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Waiting for connection from other player on "
                + HOST + ", port " + PORT + "...");

        Socket socket = serverSocket.accept(); //blocking operation
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        OutputStream out = socket.getOutputStream();

        boolean win = startPlayLoop(null, in, out);

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
