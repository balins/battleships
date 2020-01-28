package uj.java.pwj2019.battleships;

import uj.java.pwj2019.battleships.client.Mode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class InitHelper {
    static Mode parseMode(String sMode) throws IllegalArgumentException {
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

    static int parsePort(String sPort) throws IllegalArgumentException {
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

    static List<String> loadMap(String sMap) throws IllegalArgumentException {
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
            throw new IllegalArgumentException("The map location: " + pathToMap.toAbsolutePath().normalize().toString()
                    + " is not available for the app.\n" +
                    "Please make sure that the path is correct and the file is accessible.");
        }

        return mapLines;
    }

    static String getPrivateIp() {
        StringBuilder sb = new StringBuilder();
        String command = "hostname -I | awk '{print $1}'";

        try {
            Process process = Runtime.getRuntime().exec(command);

            try (InputStream in = process.getInputStream()) {
                int c;
                while ((c = in.read()) != -1) {
                    sb.append((char)c);
                }
            }

            return sb.toString();
        } catch (IOException e) {
            return "127.0.0.1";
        }
    }
}
