package com.example.drawpolygon.services;

import org.json.JSONException;
import org.json.JSONObject;

public interface WebDataService {

	public void loadData(JSONObject jsonResponse) throws JSONException;
}
