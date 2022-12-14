package net.vatri.inventory.controllers;

import net.vatri.inventory.App;

import net.vatri.inventory.models.GroupVariant;
import net.vatri.inventory.models.ProductGroup;

import net.vatri.inventory.libs.BaseController;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddEditGroupController extends BaseController implements Initializable {

    @FXML
    private TextField fldName;
    @FXML
    private Label errorLabel;
    @FXML
    private Label savedLabel;
    @FXML
    private TableView<GroupVariant> tblVariants;
    @FXML
    private TableColumn<GroupVariant, String> colVariantName;
    @FXML
    private TextField newVariantNameFld;


    private String _groupId = App.getInstance().repository.get("selectedGroupId");

    public void initialize(URL url, ResourceBundle rb) {

        if (_groupId != null) {
            _loadGroupData(_groupId);
        }
    }

    private void _loadGroupData(String groupId) {
        ProductGroup group = inventoryService.getGroup(groupId);
        fldName.setText(group.getGroupName());
        _loadGroupVariantsTable(group);
    }

    private void _loadGroupVariantsTable(ProductGroup group) {
        colVariantName.setCellValueFactory(new PropertyValueFactory<>("variantName"));
        tblVariants.getItems().addAll(group.getGroupVariants());
    }

    @FXML
    protected boolean saveGroup() {
        if (fldName.getText().length() < 2) {
            errorLabel.setVisible(true);
            return false;
        }
        else {
            ProductGroup group = new ProductGroup();
            if (_groupId != null && !_groupId.equals("")) {
                group = inventoryService.getGroup(_groupId);
            } else {
                group.setCreated(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
            }

            group.setGroupName(fldName.getText());
            group.setGroupVariants(tblVariants.getItems());

            for (GroupVariant gv : group.getGroupVariants()) {
                gv.setGroup(group);
            }

            if (!inventoryService.saveGroup(group)) {
                errorLabel.setText("ERROR: can't save your form. Please contact IT support team!");
                return false;
            } else {

                if (_groupId == null) {
                    fldName.setText("");
                }
                savedLabel.setVisible(true);
                errorLabel.setVisible(false);
                return true;
            }
        }
    }

    @FXML
    protected void hideLabels() {
        errorLabel.setVisible(false);
        savedLabel.setVisible(false);
    }

    @FXML
    protected void handleBack() {
        App.showPage("groups");
    }

    @FXML
    private void handleAddNewVariant() {
        if (newVariantNameFld.getText().length() > 0) {
            GroupVariant groupVariant = new GroupVariant();
            groupVariant.setVariantName(newVariantNameFld.getText());
            tblVariants.getItems().add(groupVariant);
            newVariantNameFld.setText("");
        }
    }
}

