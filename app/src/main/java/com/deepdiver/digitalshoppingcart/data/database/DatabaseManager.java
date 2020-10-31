package com.deepdiver.digitalshoppingcart.data.database;

public class DatabaseManager {

    public static final String DATABASE_NAME = "digitalDB";
    public static final String _ID = "_id";

    //customer table structure

    public static final String CUSTOMER_TABLE = "customer_table";
    public static final String  first_name = "first_name";
    public static final String second_name = "second_name";
    public static final String email = "email";
    public static final String user_name = "user_name";
    public  static final String address = "address";
    public static final String city = "city";
    public static final String password = "password";


    //shopping list structure

    public static final String SHOPPING_LIST_TABLE = "shopping_list_table";

    public  static final String foreign_product_id = "foreign_product_id";

    public static  final  String foreign_customer_id = "foreign_customer_id";
    public static final String isChecked = "isChecked";

    //cart structure

    public static final String CART_TABLE = "cart_table";
    public static final String product_id_foreign = "product_foreign_id";
    public static final String customer_id_foreign ="customer_foreign_id";

    //product table structure

    public static final String PRODUCT_TABLE = "product_table";
    public static final String product_name = "product_name";
    public  static final String serial_no = "serial_no";
    public static final String price = "price";
    public static final String desc = "product_desc";
    public static final String product_image = "product_image";





   
}
