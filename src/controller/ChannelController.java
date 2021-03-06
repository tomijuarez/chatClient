package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTreeTableColumn;
import controller.events.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML TableView table;

    private TableColumn<String, String> messagesColumn;

    private ObservableList<String> messages = FXCollections.observableArrayList();

    private ChannelMediator mediator;

    private String userName = "";
    private String selectedUser = "";
    private String selectedChannel = "";
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
            if(!msg.isEmpty()) {
                if (!this.selectedChannel.equals("")) {
                    if(!this.selectedUser.equals("")) {
                        this.client.sendNewDirectMessage(this.selectedChannel, this.userName, this.selectedUser, msg);
                    }
                    else {
                        this.client.sendNewGlobalMessage(this.selectedChannel, this.userName, msg);
                    }
                }
            }
        });

        this.messagesColumn = new TableColumn<>("MENSAJES");
        this.messagesColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        this.messagesColumn.setPrefWidth(700);

        this.table.getColumns().add(this.messagesColumn);
        this.table.setItems(this.messages);

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
            if(this.selectedChannel.equals("") || !this.selectedChannel.equals(selectedItem.getValue())) {
                this.selectedChannel = selectedItem.getValue();
                this.client.toggleChannel(selectedItem.getValue(), this.userName);
                this.messages.clear();
                this.messagesColumn.setText("Mensajes globales en el canal " + selectedItem.getValue());
            }
        }
        else {
            System.out.println("CANAL ELEGIDO "+selectedItem.getParent().getValue()+" ACTUAL: "+this.selectedChannel + " | USUARIO ELEGIDO "+selectedItem.getValue()+ " ACTUAL: "+this.selectedUser);
            if((this.selectedChannel.equals("") && this.selectedUser.equals("")) || (!this.selectedUser.equals(selectedItem.getValue()) && !this.selectedChannel.equals(selectedItem.getParent().getValue()))) {
                this.selectedUser = selectedItem.getValue();
                this.selectedChannel = selectedItem.getParent().getValue();
                this.client.toggleUser(selectedItem.getParent().getValue(), selectedItem.getValue());
                this.messages.clear();
                this.messagesColumn.setText("Mensajes con " + this.selectedUser + " en el canal " + this.selectedChannel);
            }
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
        if(this.selectedChannel.equals(event.getChannel())){
            if(this.selectedUser.equals(event.getFrom()) || this.selectedUser.equals(event.getTo())) {
                this.messages.add(event.getFrom() + " dice: "+event.getMessage());
            }
        }
    }

    @Override
    public void visit(GlobalMessage event) {
        if(this.selectedChannel.equals(event.getChannel())) {
            this.messages.add(event.getFrom() + " dice: "+event.getMessage());
        }
    }

    @Override
    public void visit(Logout event) {
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
        this.messages.clear();
    }

    @Override
    public void update(Observable object, Object src) {
        ((ChannelEventAcceptor) src).accept(this);
    }

}
