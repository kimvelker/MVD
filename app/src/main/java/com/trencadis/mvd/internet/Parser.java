package com.trencadis.mvd.internet;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Parser extends AsyncTask<Void, Void, Void> {
	
	/*
	 * Always check with ConnectionDetector first if an Internet connection is present
	 * 
	 * Set the URL in JSONParser class
	 * 
	 * Create a new string called "loading" for the ProgressDialog message
	 * 
	 * Override onPostExecute ->
	 * call super method to dismiss the loading dialog ->
	 * retrieve the result
	 * 
	 */
	
	public static final int OBJECT = 0;
	public static final int ARRAY = 1;
	
	public static final String POST = JSONParser.POST;
	public static final String GET = JSONParser.GET;

	private ArrayList<NameValuePair> parameters;
	private int returnType;
	
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	
	private String connectionMethod = POST;
	
	public Parser(Context context, int returnType){
		this.returnType = returnType;
		
		parameters = new ArrayList<NameValuePair>();
	}
	
	public Parser(Context context, int returnType, String connectionMethod){
		this.returnType = returnType;
		
		parameters = new ArrayList<NameValuePair>();
		
		this.connectionMethod = connectionMethod;
	}
	
	@Override
	protected void onPreExecute() {

	}

	@Override
	protected Void doInBackground(Void... x) {
		JSONParser jsonParser = new JSONParser(connectionMethod);

        if (returnType == OBJECT) {
            jsonObject = jsonParser.getJSONObject(parameters);
        } else {
            jsonArray = jsonParser.getJSONArray(parameters);
        }

		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {

	}
	
	public void addParam(String field, String value){
		parameters.add(new BasicNameValuePair(field, value));
	}
	
	public JSONObject getObject(){
		return jsonObject;
	}
	
	public JSONArray getArray(){
		return jsonArray;
	}
	
}
