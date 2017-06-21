package controller;

import controller.events.ConfigurationEventVisitor;
import controller.events.CreateUser;
import controller.events.RemoveUser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.*;
import view.CustomAlert;
import view.Modal;

import java.net.URL;
import java.util.*;

/**
 * Created by Tomás on 30/12/2015.
 */
public class FirstController extends MediableController implements Initializable, Observer, ConfigurationEventVisitor {
    @FXML
    private TextField usernameInput;
    @FXML
    private Button usernameSubmit;

    private Mediator mediator;

    private NameValidator validator = new NameValidator();

    public FirstController() { }

    @Override
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.usernameSubmit.setOnAction((event) -> {
            String userName = this.usernameInput.getText();
            if(!userName.isEmpty())
                if(this.validator.validate(userName)) {
                    Modal subWindow = new Modal("layouts/chatView.fxml", new ChannelController(userName), new Stage(), this.context);
                    this.context.setChild(subWindow, new ChannelMediator());
                }
                else
                    new CustomAlert(Alert.AlertType.ERROR, ":(", "El nombre de usuario sólo puede contener letras, dígitos, _, - y debe contener entre 3 y 15 caractéres.");
            else
                new CustomAlert(Alert.AlertType.ERROR, ":(","El nombre de usuario no puede ser vacío.");
        });
    }

    @Override
    public void update(Observable object, Object src) {

    }

    @Override
    public void visit(CreateUser event) {

    }

    @Override
    public void visit(RemoveUser event) {

    }
}