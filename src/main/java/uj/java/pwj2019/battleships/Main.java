package uj.java.pwj2019.battleships;

import uj.java.pwj2019.battleships.client.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

public class Main {
    static String host;

    public static void main(String[] args) {
        String sMode = null, sPort = null, sMap = null;

        for(int i = 0; i < args.length-1; i+=2) {
            var key = args[i].toLowerCase();
            switch(key) {
                case "-mode":
                    sMode = args[i+1];
                    break;
                case "-host":
                    host = args[i+1];
                    break;
                case "-port":
                    sPort = args[i+1];
                    break;
                case "-map":
                    sMap = args[i+1];
                    break;
                default:
                    System.err.println("Unsupported argument: \"" + args[i+1] + "\"");
            }
        }

        if(sMode == null || sPort == null || sMap == null) {
            System.err.println("You have to provide all three required arguments:\n" +
                    "\t-mode [server|client]\n" +
                    "\t-port port-number\n" +
                    "\t-map path-to-map");
            System.err.println("Shutting down the application...");
            System.exit(-1);
        }

        Mode mode = null;
        int port = -1;
        List<String> mapLines = null;
        AppClient appClient = null;

        try {
            mode = InitHelper.parseMode(sMode);
            port = InitHelper.parsePort(sPort);
            mapLines = InitHelper.loadMap(sMap);
        } catch (IllegalArgumentException e) {
            System.err.println("Application can not be started due to following error:");
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        try {
            if(mode == Mode.SERVER) {
                if(host == null)
                    host = InitHelper.getPrivateIp();
                appClient = new Server(host, port, mapLines);
            } else {
                if(host == null)
                    host = "127.0.0.1";
                appClient = new Client(host, port, mapLines);
            }
        } catch (IOException ioe) {
            System.err.println("\nCould not connect to host " + host + " at port " + port);
            System.err.println("Shutting down the application...");
            System.exit(-1);
        } catch (IllegalArgumentException e) {
            System.err.println("\nCould not start the game");
            System.err.println(e.getMessage() + " (" + e.getCause() + ")");
            System.err.println("Shutting down the application...");
            System.exit(-1);
        }

        try {
            appClient.start();
        } catch (NullPointerException | SocketException e) {
            System.err.println("\nYour opponent has disconnected");
            System.err.println("Shutting down the application...");
            System.exit(-1);
        } catch (IOException | InterruptedException e) {
            System.err.println("\nAn error occurred:");
            System.err.println(e.getMessage());
            System.err.println("Shutting down the application...");
            System.exit(-1);
        }

    }
}
