package uj.java.pwj2019.battleships;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String sMode = null, sPort = null, sMap = null;

        for(var arg : args) {
            var kv = arg.toLowerCase().split("\\s+");
            switch(kv[0]) {
                case "-mode":
                    sMode = kv[1];
                    break;
                case "-port":
                    sPort = kv[1];
                    break;
                case "-map":
                    sMap = kv[1];
                    break;
                default:
                    System.err.println("Unsupported argument: \"" + kv[0] + "\"");
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
            mode = parseMode(sMode);
            port = parsePort(sPort);
            mapLines = loadMap(sMap);
        } catch (IllegalArgumentException e) {
            System.err.println("Application can not be started due to following error:");
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        try {
            if(mode.equals(Mode.SERVER)) {
                appClient = new Server(port, mapLines);
            } else {
                appClient = new Client(port, mapLines);
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println("Shutting down the application...");
            System.exit(-1);
        }

        try {
            appClient.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println("Shutting down the application...");
            System.exit(-1);
        }
    }

    private static Mode parseMode(String sMode) throws IllegalArgumentException {
        Mode mode;
        if(sMode.equalsIgnoreCase("server")) {
            mode = Mode.SERVER;
        } else if(sMode.equalsIgnoreCase("client")) {
            mode = Mode.CLIENT;
        } else {
            throw new IllegalArgumentException("Given mode is unsupported: \"" + sMode + "\"");
        }

        return mode;
    }

    private static int parsePort(String sPort) throws IllegalArgumentException {
        int port;
        try {
            port = Integer.parseInt(sPort);
            if(port < 1024 || port > 49151) {
                throw new IllegalArgumentException("Port number is invalid. " +
                        "You have to use a port in the range between 1024 and 49151. Got " + sPort);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port number is invalid. " +
                    "You have to provide an integer value in the range between 1024 and 49151. Got " + sPort);
        }

        return port;
    }

    private static List<String> loadMap(String sMap) throws IllegalArgumentException {
        List<String> mapLines;
        Path pathToMap = Paths.get(sMap);
        try {
            mapLines = Files.readAllLines(pathToMap);

            if (mapLines.size() != 10) {
                throw new IllegalArgumentException("Dimensions of the provided map are invalid. " +
                        "Map should consist of 10 rows. Got " + mapLines.size());
            }

            for(var line : mapLines) {
                if (line.length() != 10) {
                    throw new IllegalArgumentException("Dimensions of the provided map are invalid. " +
                            "Each row should consist of 10 columns. Got " + line.length());
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("The map location: " + pathToMap.toAbsolutePath()
                    + " is not available for the app.\n" +
                    "Please make sure that the path is correct and the file is accessible.");
        }

        return mapLines;
    }
}
