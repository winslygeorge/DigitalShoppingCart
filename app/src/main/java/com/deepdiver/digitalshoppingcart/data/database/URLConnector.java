package com.deepdiver.digitalshoppingcart.data.database;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 */

/**
 * @author georgos7
 *
 */
public class URLConnector {
	
	protected String baseUrl = null;
	
	protected String tablename = null;
	
	protected String[] fields = null;
	
	protected String operation = null;
	
	public URLConnector(String baseUrl ) {
		
		this.baseUrl = baseUrl;
	
	}
	
	public JSONObject get(String tablename, String[] fields , String[] where, String[] wValues) {
		String fullUrl = "";
		int x = 1;
		String Fields = fields[0];
		while(x < fields.length) {
			
			Fields = Fields+ ","+fields[x];
			x++;
		}
		
		if(where != null && wValues != null) {
			
			 x = 1;
			String WHERE = where[0];
			while(x < where.length) {
				
				WHERE = WHERE+ ","+where[x];
				x++;
			}
			
			    int y = 1;
				String VWHERE = wValues[0];
				while(y < wValues.length) {
					
					VWHERE = VWHERE+ ","+wValues[y];
					
					y++;
				}
				
			
			 fullUrl= this.baseUrl+"?fields="+Fields+"&operation=select&tablename="+tablename+"&wfield="+WHERE+"&wvalue="+VWHERE;
			
		}else {
			
		 fullUrl= this.baseUrl+"?fields="+Fields+"&operation=select&tablename="+tablename;
		
		
		}


		Log.i("endurl", (fullUrl.replace(" ", "")));
		
		
		return handleGetRequest(fullUrl.replace(" ", ""));
		
	}

	public JSONObject handleGetRequest(String furl) {
		
		
		HttpURLConnection http = null;
		
	try {
		URL url = new URL(furl);
		http = (HttpURLConnection) url.openConnection();
		http.setRequestMethod("GET");
		http.setDoInput(true);
		http.setRequestProperty("Content-Type", "application/json");
		http.setConnectTimeout(3000);
		http.connect();
		
		String results = "";
		
		
			
			InputStream cin = http.getInputStream();
			int x = 0;
			while((x = cin.read()) != -1) {
				
				results += Character.toString((char)x);
			}


		Log.i( "handleGetRequest", results);
			
			JSONParser parser = new JSONParser();


			
			org.json.simple.JSONObject robjj = (org.json.simple.JSONObject)parser.parse(results);

		return robjj;
			
		
		
	} catch (IOException | ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	
	}
	
	public JSONObject delete( String tablename,   String[] where, String[] wValues) {
	
		if(where != null && wValues != null) {
			int x  = 1;
			String WHERE = where[0];
			while(x < where.length) {
				
				WHERE = WHERE+ ","+where[x];
				x++;
			}
			
			    int y = 1;
				String VWHERE = wValues[0];
				while(y < wValues.length) {
					
					VWHERE = VWHERE+ ","+wValues[y];
					
					y++;
				}
				
			
			String fullUrl= this.baseUrl+"?operation=delete&tablename="+tablename+"&wfield="+WHERE+"&wvalue="+VWHERE;
			
		return handleGetRequest(fullUrl);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject handlePostRequest(String furl, String [] fields, String [] values, String operation, String tablename, String where, String val) {
		

		HttpURLConnection http = null;
		
	try {
		URL url = new URL(furl);
		
		http = (HttpURLConnection) url.openConnection();
		
		http.setConnectTimeout(3000);
		http.setDoInput(true);
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		http.setRequestProperty("Content-Type", "application/json");
		http.connect();
		
		String results = "";
		
		JSONObject obj = new JSONObject();
		
	
		
		int x  = 0;
		

	
		while(x < fields.length && x < values.length) {
			
			obj.put(fields[x], values[x]);
			
			x++;
		}
		

	
		
		obj.put("operation", operation);
		
		obj.put("tablename", tablename);
		
if(where != null && val != null) {
			
			obj.put("where", where);
			obj.put("val", val);
		}
		
			
			
			PrintWriter out = new PrintWriter(new OutputStreamWriter(http.getOutputStream()), true);
			
			out.println(obj.toString());

			out.close();
			
			InputStream cin = http.getInputStream();
			 x = 0;
			while((x = cin.read()) != -1) {
				
				results += Character.toString((char)x);
			}
			
			cin.close();
		
		
		
	JSONParser parser = new JSONParser();
			
			JSONObject robj = (JSONObject)parser.parse(results);
			
			return robj;
		
		
	} catch (IOException | ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
	}
}
