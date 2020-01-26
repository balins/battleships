package uj.java.pwj2019.battleships.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends AppClient {
    public Server(int port, List<String> mapLines) {
        super(port, mapLines);
    }

    @Override
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);

        System.out.println("Waiting for connection from other player on "
                + serverSocket.getInetAddress().toString() + ", port " + PORT + "...");

        Socket socket = serverSocket.accept(); //blocking operation

        boolean win = startPlayLoop(socket, null);

        socket.close();

        if(win) {
            win();
        } else {
            lose();
        }
    }
}
