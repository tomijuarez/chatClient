package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import controller.events.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import model.*;
import sun.reflect.generics.tree.Tree;
import view.CustomAlert;

import java.net.URL;
import java.util.*;


/**
 * Created by Gandalf on 30/12/2015.
 */
public class ChannelController extends MediableController implements Initializable, Observer, ChannelEventVisitor {
    /**
     * Elementos del panel de chat.
     */


    @FXML TreeView<String> channelsTree;
    @FXML JFXButton createChannelButton;
    @FXML JFXButton logoutButton;
    @FXML JFXButton sendMessageButton;
    @FXML JFXTextArea messageInput;

    private ChannelMediator mediator;

    private String userName;
    private NameValidator validator = new NameValidator();


    private ChatClient client;

    /**
     * Model
     */

    public ChannelController(String userName) {
        this.userName = userName;
        this.client = new ChatClient("127.0.0.1", 5656, userName);
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
        this.client.connect();

        this.createChannelButton.setOnAction((event)->{
            this.createChannel();
        });

        this.logoutButton.setOnAction((event)->{
            this.client.logout(this.userName);
        });

        this.sendMessageButton.setOnAction((event)->{
            String msg = this.messageInput.getText();
            if(!msg.isEmpty())
                System.out.println("MENSAJE: "+msg);
        });
    }

    private void createChannel() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Crear un nuevo canal.");
        dialog.setHeaderText("Crear un nuevo canal.");
        dialog.setContentText("Por favor, ingrese un nombre para el canal:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String channelName = result.get();
            System.out.println(channelName);
            if(this.validator.validate(channelName)) {
                this.client.createChannel(channelName);
            }
            else
                new CustomAlert(Alert.AlertType.ERROR, ":(", "El nombre del canal sólo puede contener letras, dígitos, _, - y debe contener entre 3 y 15 caractéres.");
        }
        else {
            new CustomAlert(Alert.AlertType.ERROR, ":(", "El nombre de canal no puede ser vacío.");
        }
    }

    private void toggleChannel(TreeItem<String> selectedItem) {
        if(selectedItem.getParent() == this.channelsTree.getRoot()) {
            this.client.toggleChannel(selectedItem.getValue(), this.userName);
        }
        else {
            this.client.toggleUser(selectedItem.getParent().getValue(), selectedItem.getValue());
        }
    }

    private void setChannelsRoot() {
        TreeItem<String> root = new TreeItem<>("Canales");
        this.channelsTree.setRoot(root);
        root.setExpanded(true);

        this.channelsTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2) {
                    TreeItem<String> item = channelsTree.getSelectionModel().getSelectedItem();
                    toggleChannel(item);
                }
            }
        });
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

    private void removeUserFromUI(String userName) {
        List<TreeItem<String>> users;
        for(TreeItem<String> channel : this.channelsTree.getRoot().getChildren()) {
            users = channel.getChildren();
            for (TreeItem<String> userInChannel : users)
                if (userName.equals(userInChannel.getValue())) {
                    users.remove(userInChannel);
                    break;
                }
        }
    }

    private boolean isSubscribed(TreeItem<String> channel, String user) {
        for (TreeItem<String> userItem: channel.getChildren()) {
            if(user.equals(userItem.getValue()))
                return true;
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
        String channel = event.getChannelName();
        String user = event.getUserName();

        for (TreeItem<String> channelItem: this.channelsTree.getRoot().getChildren()) {
            if(channel.equals(channelItem.getValue()) && !this.isSubscribed(channelItem,user)) {
                channelItem.getChildren().add(new TreeItem<String>(user));
                channelItem.setExpanded(true);
                break;
            }
        }
    }

    @Override
    public void visit(UnsubscribeChannel event) {
        String channel = event.getChannelName();
        String user = event.getUserName();
        for (TreeItem<String> channelItem: this.channelsTree.getRoot().getChildren()) {
            if(channel.equals(channelItem.getValue())) {
                List<TreeItem<String>> usersInChannel = channelItem.getChildren();
                for(TreeItem<String> userInChannel : usersInChannel) {
                    if (user.equals(userInChannel.getValue())) {
                        usersInChannel.remove(userInChannel);
                        break;
                    }
                }
                channelItem.setExpanded(true);
                break;
            }
        }
    }


    @Override
    public void visit(DirectMessage event) {
        System.out.println("MENSAJE DIRECTO");
    }

    @Override
    public void visit(GlobalMessage event) {
        System.out.println("MENSAJE GLOBAL");
    }

    @Override
    public void visit(Logout event) {
        System.out.println("CERRÓ SESIÓN "+event.getUserName());
        if(event.getUserName().equals(this.userName))
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    getContext().close();
                }
            });
        else {
            System.out.println("SACO AL USUARIO "+event.getUserName() + " DE "+this.userName);
            this.removeUserFromUI(event.getUserName());
        }
    }

    @Override
    public void visit(Clear event) {
        System.out.println("LIMPIAR");
    }

    @Override
    public void update(Observable object, Object src) {
        ((ChannelEventAcceptor) src).accept(this);
    }

}
