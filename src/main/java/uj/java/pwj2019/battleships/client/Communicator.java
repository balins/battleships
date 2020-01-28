package uj.java.pwj2019.battleships.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class Communicator {
    private final BufferedReader input;
    private final OutputStream output;
    private final Socket socket;

    public Communicator(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.output = socket.getOutputStream();
    }

    protected String receive(boolean sendAck) throws IOException, InterruptedException {
        String received;

        received = input.readLine(); //blocking operation

        if(sendAck)
            send("ACK"); //send acknowledgement

        return received;
    }

    protected void send(String message) throws IOException, InterruptedException {
        message += "\n";
        output.write(message.getBytes());
        output.flush();

        if(!message.equals("ACK\n")) { //wait for acknowledgement
            int timeoutExpired = 0;
            socket.setSoTimeout(1000);

            while(timeoutExpired < 3) {
                try {
                    receive(false);
                    socket.setSoTimeout(0);
                    return;
                } catch (IOException e) {
                    timeoutExpired++;
                    output.write(message.getBytes()); //retry sending message
                    output.flush();
                }
            }

            socket.setSoTimeout(0);
            throw new SocketTimeoutException("Communication error");
        }
    }

    protected void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}
