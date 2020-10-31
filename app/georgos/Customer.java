package com.georgos;
/**
 * 
 */

/**
 * @author georgos7
 *
 */
public class Customer {
	
	public String first_name;
	
	public String second_name;
	
	public String user_name;
	
	public String email;
	
	public String image;
	
	public int address;
	
	public String city;
	
	public String password;
	
	public Customer(String first_name,String second_name, String user_name,String email, String image, int address,String city, String passwd) {
		
		this.address = address;
		this.city = city;
		this.email = email;
		this.first_name =first_name;
		this.image = image;
		this.second_name = second_name;
		this.user_name = user_name;
		this.password = passwd;
	}
}
