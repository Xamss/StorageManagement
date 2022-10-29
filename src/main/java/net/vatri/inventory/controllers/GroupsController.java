package net.vatri.inventory.controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.TableColumn;

import javafx.scene.control.Button;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

import net.vatri.inventory.App;
import net.vatri.inventory.libs.BaseController;
import net.vatri.inventory.models.ProductGroup;

public class GroupsController extends BaseController implements Initializable {

    @FXML
    private TableView<ProductGroup> tblGroups;
    @FXML
    private TableColumn<ProductGroup, String> idCol;
    @FXML
    private TableColumn<ProductGroup, String> groupCol;
    @FXML
    private TableColumn<ProductGroup, String> priceCol;

    public void initialize(URL url, ResourceBundle rb) {

        ObservableList<ProductGroup> tblData = FXCollections.observableArrayList(
                inventoryService.getGroups()
        );

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        groupCol.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        tblGroups.setItems(tblData);
    }

    @FXML
    protected void openGroup() {
        ProductGroup group = tblGroups.getSelectionModel().getSelectedItem();
        if (group != null) {
            App.getInstance().repository.put("selectedGroupId", group.getId().toString());
            App.showPage("addEditGroup");
        }
    }

    @FXML
    protected void handleAddGroup() {
        App.getInstance().repository.put("selectedGroupId", null);
        App.showPage("addEditGroup");
    }

}
