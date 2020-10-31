package com.deepdiver.digitalshoppingcart.data;

public class Product {


    public String  productName;
    public String productImageUrl;
    public float productPrice;
    public String productDesc;
    public String productSerial;
    public int product_id;
    public int isChecked;

    public Product(String productName, String productImageUrl, float productPrice, String productDesc, String productSerial, int product_id) {
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productSerial = productSerial;
        this.product_id = product_id;
    }

    public Product(String productName, String productImageUrl, float productPrice, String productDesc, String productSerial, int product_id, int isChecked) {
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productSerial = productSerial;
        this.product_id = product_id;
        this.isChecked = isChecked;
    }

}
