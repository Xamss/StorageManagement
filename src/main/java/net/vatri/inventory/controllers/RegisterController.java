package net.vatri.inventory.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.vatri.inventory.App;
import net.vatri.inventory.libs.BaseController;
import net.vatri.inventory.models.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegisterController extends BaseController{

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label goodLabel;

    @FXML
    protected boolean btnRegisterPressed(ActionEvent event) {
        List<User> users = inventoryService.getUsers();
        if(!emailField.getText().matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")){
            goodLabel.setVisible(false);
            errorLabel.setVisible(true);
            return false;
        }
        for(User user: users){
            if(user.getEmail().equals(emailField.getText())){
                goodLabel.setVisible(false);
                errorLabel.setVisible(true);
                return false;
            }
        }
        errorLabel.setVisible(false);
        goodLabel.setVisible(true);

        User user = new User();

        user.setCreated(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());
        user.setPassword(passwordField.getText());


        if (inventoryService.saveUser(user)) {
            App.showPage("dashboard");
        } else {
            System.out.println("Can't save User!");
            errorLabel.setVisible(true);
        }
        return true;
    }

    @FXML
    private void handleReturn(){App.showPage("login");}
}
