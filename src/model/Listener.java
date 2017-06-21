package model;

import java.io.*;

/**
 * Created by tomi on 17/06/17.
 */
public class Listener extends Thread{

    private ChatClient client;
    private DataInputStream reader;

    //Strings para el protocolo de comunicación con el servidor.
    private static final String MESSAGE = "MSG";
    private static final String NEW_CHANNEL = "NC";
    private static final String TOGGLE_CHANNEL = "TC";
    private static final String TOGGLE_USER = "TU";
    private static final String SUBSCRIBE_CHANNEL = "SC";
    private static final String UNSUBSCRIBE_CHANNEL = "UC";
    private static final String LOGIN = "LI";
    private static final String LOGOUT = "LO";
    private static final String LOAD_CHANNELS = "LC";
    private static final String LOAD_USERS = "LU";
    private static final String REGISTER_USER = "RU";
    private static final String SEPARATOR = "-";
    private static final String SEP_ITEMS = ";";
    private static final String CLEAR = "CL";
    private static final String NEW_LINE = "\r\n";


    public Listener(ChatClient client, InputStream reader) {
        this.client = client;
        this.reader = new DataInputStream(reader);
    }

    private void manageResponse(String response) {
        String[] chunks = response.split(Listener.SEPARATOR);

        if (chunks.length == 2) {
            String command = chunks[0];
            String message = chunks[1];

            System.out.println(command);
            System.out.println(message);

            this.callProperFunction(command, message);
        }
        else if(chunks.length == 3) {
            String command = chunks[0];
            String part1 = chunks[1];
            String part2 = chunks[2];

            System.out.println(command);
            System.out.println(part1);
            System.out.println(part2);

            this.callProperFunction(command, part1, part2);
        }
    }

    //Función sucia. CASE is evil.
    private void callProperFunction(String command, String message) {
        switch (command) {
            case Listener.NEW_CHANNEL:
                this.client.triggerNewChannel(message);
                break;
            case Listener.LOAD_CHANNELS:
                this.client.triggerLoadChannels(message.split(Listener.SEP_ITEMS));
                break;
            case Listener.CLEAR:
                this.client.triggerClear();
                break;
            case Listener.LOGOUT:
                this.client.exit(message);
                break;
        }
    }

    private void callProperFunction(String command, String part1, String part2) {
        switch (command) {
            case Listener.SUBSCRIBE_CHANNEL:
                this.client.triggerAddToChannel(part1, part2);
                break;
            case Listener.UNSUBSCRIBE_CHANNEL:
                this.client.triggerRemoveFromChannel(part1, part2);
                break;
            case Listener.LOAD_USERS:
                this.client.triggerLoadUsers(part1, part2.split(Listener.SEP_ITEMS));
                break;
        }
    }

    @Override
    public void run() {
        try {
            String response;
            while ((response = this.reader.readLine()) != null) {
                this.manageResponse(response);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
