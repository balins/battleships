package uj.java.pwj2019.battleships;

import java.io.IOException;
import java.util.List;

public class Client extends AppClient {
    public Client(int port, List<String> mapLines) {
        super(port, mapLines);
    }

    @Override
    protected void start() throws IOException {

    }
}
