package com.example.drawpolygon;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.drawpolygon.services.WebDataService;
import com.example.drawpolygon.services.WebService;
import com.example.drawpolygon.utils.Constants;
import com.example.drawpolygon.utils.Utils;


public class AreaDetailActivity extends ActionBarActivity implements
		WebDataService {

	String area_coordinate = null;
	static final String AREA_COORDINATE = "area_coordinate";

	EditText edt_area_name = null, edt_address = null, edt_postal_code = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_areadetail);
		// get a Farm coordinate from the MainActivity
		area_coordinate = getIntent().getExtras().getString(AREA_COORDINATE);

		edt_area_name = (EditText) findViewById(R.id.edt_area_name);
		edt_address = (EditText) findViewById(R.id.edt_address);
		edt_postal_code = (EditText) findViewById(R.id.edt_postal_code);

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_areadetail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_save:
			saveData();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void saveData() {
		System.out.println("Save button is click");
		System.out.println("Area Name = " + edt_area_name.getText());
		System.out.println("Address = " + edt_address.getText());
		System.out.println("Postal Code =" + edt_postal_code.getText());

		String area_name = edt_area_name.getText().toString();
		String address = edt_address.getText().toString();
		String postal_code = edt_postal_code.getText().toString();

		String dataToSend = "area_coordinate=" + area_coordinate
				+ "&area_name=" + area_name + "&address=" + address
				+ "&postalcode=" + postal_code;
		if (Utils.isNetworkAvailable(AreaDetailActivity.this)) {
			new WebService(AreaDetailActivity.this, dataToSend)
					.execute(Constants.AREA_ADD_URL);
		} else {
			Utils.showToast(AreaDetailActivity.this,
					"Check Internet Availability");
		}
	}

	@Override
	public void loadData(JSONObject jsonResponse) throws JSONException {
		// TODO Auto-generated method stub
		System.out.println("response = " + jsonResponse.toString());
		if (jsonResponse.getBoolean("status")) {
			Intent intent = new Intent(AreaDetailActivity.this,
					MainActivity.class);
			startActivity(intent);
		} else {
			Utils.showToast(getApplicationContext(),
					jsonResponse.getString("msg"));
			// check errors is existing or not
			if (jsonResponse.has("errors")) {
				JSONObject errors = jsonResponse.getJSONObject("errors");
				if (errors.has("area_coordinate")) {
					//
				}

				if (errors.has("area_name")) {
					// set error message on area name field
					edt_area_name.setError(errors.getString("area_name"));
				}
				if (errors.has("address")) {
					// set error message on address field
					edt_address.setError(errors.getString("address"));
				}
				if (errors.has("postalcode")) {
					// set error message on postal code field
					edt_postal_code.setError(errors.getString("postalcode"));
				}
			}
		}

	}

}
