package net.vatri.inventory.models;


public class EmailModel {

    public String product_name = "";
    public String type = "";
    public String quantity = "0";


    public EmailModel(String product_name, String variant_name, String stock) {
        this.setProductName(product_name);
        this.setVariantName(variant_name);
        this.setStock(stock);
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public String getVariantName() {
        return type;
    }

    public void setVariantName(String variant_name) {
        this.type = type;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setStock(String quantity) {
        this.quantity = quantity;
    }
}
