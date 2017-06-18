package controller;

import controller.events.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.*;

import java.net.URL;
import java.util.*;


/**
 * Created by Gandalf on 30/12/2015.
 */
public class ChannelController extends MediableController implements Initializable, Observer, ChannelEventVisitor {
    /**
     * Elementos del panel de chat.
     */


    @FXML TreeView channelsTree;

    private ChannelMediator mediator;

    private ChatClient client = new ChatClient("127.0.0.1", 5656);

    /**
     * Model
     */

    public ChannelController(User user) {
        this.client.addObserver(this);
    }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = (ChannelMediator) mediator;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Inicializo el árbol de canales.
        this.setChannelsRoot();
        this.client.addObserver(this);

        this.client.react();

        this.client.connect();
    }

    private void setChannelsRoot() {
        TreeItem<String> root = new TreeItem<>("Canales");
        this.channelsTree.setRoot(root);
    }

    private boolean channelExists(String channelName) {
        ObservableList<TreeItem<String>> channels = this.channelsTree.getRoot().getChildren();
        for (TreeItem<String> channel : channels) {
            if(channelName.equals(channel.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visit(CreateChannel event) {
        if(this.channelExists(event.getChannelName()))
            return;

        TreeItem<String> channel = new TreeItem<>(event.getChannelName());
        this.channelsTree.getRoot().getChildren().add(channel);
    }

    @Override
    public void visit(RemoveChannel event) {
        ObservableList<TreeItem<String>> channels = this.channelsTree.getRoot().getChildren();
        for (TreeItem<String> channel : channels) {
            if(event.getChannelName().equals(channel.getValue())) {
                channels.remove(channel);
                break;
            }
        }
    }

    @Override
    public void visit(SubscribeChannel event) {

    }

    @Override
    public void visit(UnsubscribeChannel event) {

    }

    @Override
    public void update(Observable object, Object src) {
        ((ChannelEventAcceptor) src).accept(this);
    }

}
