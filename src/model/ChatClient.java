package model;

import controller.events.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Observable;

/**
 * Created by tomi on 25/05/17.
 */
public class ChatClient extends Observable {

    private int port;
    private String server;
    private String userName;

    private Socket connection;
    private InputStream reader;
    private OutputStream writter;

    //Strings para el protocolo de comunicación con el servidor.
    private static final String MESSAGE = "MSG";
    private static final String NEW_CHANNEL = "NC";
    private static final String TOGGLE_CHANNEL = "TC";
    private static final String TOGGLE_USER = "TU";
    private static final String LOGIN = "LI";
    private static final String LOGOUT = "LO";
    private static final String LOAD_CHANNELS = "LC";
    private static final String LOAD_USERS = "LU";
    private static final String REGISTER_USER = "RU";
    private static final String SEPARATOR = "-";
    private static final String CLEAR = "CL";
    private static final String NEW_LINE = "\r\n";

    public ChatClient(String server, int port, String userName) {
        this.port = port;
        this.server = server;
        this.userName = userName;
    }

    private String buildRequest(String protocol, String message) {
        return protocol + ChatClient.SEPARATOR + message;
    }

    private String buildRequest(String protocol, String part1, String part2) {
        return protocol + ChatClient.SEPARATOR + part1 + ChatClient.SEPARATOR + part2;
    }

    private String buildRequest(String protocol, String part1, String part2, String part3) {
        return protocol + ChatClient.SEPARATOR + part1 + ChatClient.SEPARATOR + part2 + ChatClient.SEPARATOR + part3;
    }

    private String buildRequest(String protocol, String part1, String part2, String part3, String part4) {
        return protocol + ChatClient.SEPARATOR + part1 + ChatClient.SEPARATOR + part2 + ChatClient.SEPARATOR + part3 + ChatClient.SEPARATOR + part4;
    }

    private boolean makeRequest(String request) {
        try {
            this.writter.write((request+ChatClient.NEW_LINE).getBytes());
        }
        catch(IOException e) {
            e.printStackTrace();
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
        listenerThread.start();

        this.makeRequest(this.buildRequest(ChatClient.REGISTER_USER, userName));
    }

    public void createChannel(String channelName) {
        if (!this.makeRequest(this.buildRequest(ChatClient.NEW_CHANNEL, channelName)))
            System.out.println("Error en el request ");
    }

    public void toggleChannel(String channelName, String userName) {
        if (!this.makeRequest(this.buildRequest(ChatClient.TOGGLE_CHANNEL, channelName, userName)))
            System.out.println("Error en el request ");
    }

    public void toggleUser(String channelName, String userName) {
        if (!this.makeRequest(this.buildRequest(ChatClient.TOGGLE_USER, channelName, userName)))
            System.out.println("Error en el request ");
    }

    public void logout(String userName) {
        if(!this.makeRequest(this.buildRequest(ChatClient.LOGOUT, userName)))
            System.out.println("ERror en el request");
    }

    public void exit(String userName) {
        setChanged();
        this.notifyObservers(new Logout(userName));
    }

    public void sendNewGlobalMessage(String channel, String from, String msg) {
        System.out.println("NUEVO MENSAJE: canal "+channel + " de "+from+" -> "+msg);
        if (!this.makeRequest(this.buildRequest(ChatClient.MESSAGE, channel, from, msg)))
            System.out.println("Error en el request ");
    }

    public void sendNewDirectMessage(String channel, String from, String to, String msg) {
        System.out.println("NUEVO MENSAJE: canal "+channel + " de "+from+" a " +to+" -> "+msg);
        if (!this.makeRequest(this.buildRequest(ChatClient.MESSAGE, channel, from, to, msg)))
            System.out.println("Error en el request ");
    }

    public void triggerNewChannel(String channelName) {
        setChanged();
        this.notifyObservers(new CreateChannel(channelName));
    }

    public void triggerRemoveFromChannel(String channelName, String userName) {
        setChanged();
        this.notifyObservers(new UnsubscribeChannel(channelName, userName));
    }

    public void triggerAddToChannel(String channelName, String userName) {
        setChanged();
        this.notifyObservers(new SubscribeChannel(channelName, userName));
    }

    public void triggerLoadChannels(String[] channels) {
        for(String channel: channels) {
            this.triggerNewChannel(channel);
        }
    }

    public void triggerLoadUsers(String channel, String[] users) {
        for (String user: users) {
            this.triggerAddToChannel(channel, user);
        }
    }

    public void triggerClear() {
        setChanged();
        this.notifyObservers(new Clear());
    }

    public void triggerDirectMessage(String channel, String from, String to, String message) {
        setChanged();
        this.notifyObservers(new DirectMessage(channel, from, to, message));
    }

    public void triggerGlobalMessage(String channel, String from, String message) {
        setChanged();
        this.notifyObservers(new GlobalMessage(channel, from, message));
    }
}