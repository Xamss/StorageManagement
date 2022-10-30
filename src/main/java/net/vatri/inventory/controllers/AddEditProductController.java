package net.vatri.inventory.controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import javafx.scene.control.ComboBox;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import net.vatri.inventory.App;
import net.vatri.inventory.libs.BaseController;
import net.vatri.inventory.models.Product;
import net.vatri.inventory.models.ProductGroup;

public class AddEditProductController extends BaseController implements Initializable {

    @FXML
    private TextField fldName;
    @FXML
    private TextField fldPrice;
    @FXML
    private Label errorLabel;
    @FXML
    private Label savedLabel;
    @FXML
    private ComboBox<ProductGroup> groupCombo;

    private String _productId = App.getInstance().repository.get("selectedProductId");

    public void initialize(URL url, ResourceBundle rb) {
        if (_productId != null) {
            _loadProductData(_productId);
            _fillGroupComboData(inventoryService.getProduct(_productId).getGroup());
        } else {
            _fillGroupComboData(null);
        }
    }

    private void _loadProductData(String productId) {
        Product product = inventoryService.getProduct(productId);
        fldName.setText(product.getName());
        fldPrice.setText(product.getPrice());
    }

    private boolean _fillGroupComboData(ProductGroup selectedGroup) {

        ObservableList<ProductGroup> comboData = FXCollections.observableArrayList(
                inventoryService.getGroups()
        );
        groupCombo.getItems().addAll(comboData);

        if (selectedGroup != null && selectedGroup.getId() > 0) {
            groupCombo.getSelectionModel().select(selectedGroup);
        }

        return true;
    }

    @FXML
    protected void hideLabels() {
        errorLabel.setVisible(false);
        savedLabel.setVisible(false);
    }

    @FXML
    protected void handleBack() {
        App.showPage("products");
    }

    @FXML
    protected boolean handleSaveProduct(ActionEvent event) {

        if (!fldPrice.getText().matches("^[0-9].*") || fldName.getText().length() < 2) {
            savedLabel.setVisible(true);
            errorLabel.setVisible(true);
            return false;
        }

        Product product = new Product();
        product.setCreated(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
        if (_productId != null) {
            product = inventoryService.getProduct(_productId);
        }
        product.setName(fldName.getText());
        product.setPrice(fldPrice.getText());
        ProductGroup selectedGroup = groupCombo.getSelectionModel().getSelectedItem();

        if (selectedGroup != null) {
            product.setGroup(selectedGroup);
        }

        boolean isProductSaved = inventoryService.saveProduct(product);

        if (!isProductSaved) {
            errorLabel.setText("ERROR: can't save your form. Please contact IT support team!");
            return false;
        } else {
            if (_productId == null) {
                fldName.setText("");
                fldPrice.setText("");
            }
            savedLabel.setVisible(true);
            errorLabel.setVisible(false);
            return true;
        }

    }
}

