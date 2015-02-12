package com.example.drawpolygon.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.drawpolygon.utils.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class WebService extends AsyncTask<String, Void, Void> {

	private Context context;
	private String content;
	private String error = null;
	private String dataToSend = null;
	private ProgressDialog dialog = null;

	int sizeData = 0;

	private WebDataService webDataService = null;

	public WebService(Context context, String dataToSend) {
		this.context = context;
		this.dataToSend = dataToSend;
		this.dialog = new ProgressDialog(this.context);
		this.webDataService = (WebDataService) context;

	}

	protected void onPreExecute() {
		dialog.setMessage("Please wait..");
		dialog.show();
	}

	@Override
	protected Void doInBackground(String... params) {
		BufferedReader reader = null;
		try {
			URL url = new URL(params[0]);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(dataToSend);
			wr.flush();
			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			content = sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			error = ex.getMessage();
		} finally {
			try {
				reader.close();
			}

			catch (Exception ex) {
			}
		}
		return null;
	}

	protected void onPostExecute(Void unused) {
		dialog.dismiss();

		if (error != null) {
			Utils.showToast(context, "Error Ocuurred " + error);
		} else {
			JSONObject jsonResponse;

			try {

				jsonResponse = new JSONObject(content);
				webDataService.loadData(jsonResponse);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

}
