package model;

import controller.events.CreateChannel;
import controller.events.RemoveChannel;

import java.io.*;
import java.net.Socket;
import java.util.Observable;

/**
 * Created by tomi on 25/05/17.
 */
public class ChatClient extends Observable {

    private int port;
    private String server;

    private Socket connection;
    private InputStream reader;
    private OutputStream writter;

    //Strings para el protocolo de comunicación con el servidor.
    private static String MESSAGE = "MSG";
    private static String NEW_CHANNEL = "NC";
    private static String REMOVE_CHANNEL = "RC";
    private static String LOGIN = "LI";
    private static String LOGOUT = "LO";

    private static String SEPARATOR = "$";

    private static String NEW_LINE = "\r\n";

    public ChatClient(String server, int port) {
        this.port = port;
        this.server = server;
    }

    private String buildRequest(String protocol, String message) {
        return protocol + ChatClient.SEPARATOR + message;
    }

    private boolean makeRequest(String request) {
        try {
            this.writter.write((request).getBytes());
        }
        catch(IOException e) {
            return false;
        }

        return true;
    }

    public void connect() {
        try {
            this.connection = new Socket(server, port);
            this.reader = this.connection.getInputStream();
            this.writter = this.connection.getOutputStream();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        //Si estamos acá es porque se creo el socket correctamente.

        Listener listenerThread = new Listener(this, this.reader);
        listenerThread.run();
    }

    public void createChannel(String channelName) {
        if (this.makeRequest(this.buildRequest(ChatClient.NEW_CHANNEL, channelName)))
            this.triggerNewChannel();
        else
            System.out.println("Error en el request ");
    }

    public void removeChannel(String channelName) {
        if (this.makeRequest(this.buildRequest(ChatClient.REMOVE_CHANNEL, channelName)))
            this.triggerRemoveChannel();
        else
            System.out.println("Error en el request ");
    }

    private void triggerNewChannel() {
        setChanged();
        this.notifyObservers(new CreateChannel("piñata"));
    }

    private void triggerRemoveChannel() {
        setChanged();
        this.notifyObservers(new RemoveChannel("piñata"));
    }


    public void react() {
        this.triggerNewChannel();
        this.triggerNewChannel();
        this.triggerRemoveChannel();
    }

}
