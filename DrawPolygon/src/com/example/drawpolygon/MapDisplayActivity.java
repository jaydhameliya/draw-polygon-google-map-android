package com.example.drawpolygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.drawpolygon.services.WebDataService;
import com.example.drawpolygon.services.WebService;
import com.example.drawpolygon.utils.Constants;
import com.example.drawpolygon.utils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapDisplayActivity extends ActionBarActivity implements
		OnMapClickListener, WebDataService {

	private GoogleMap myMap;

	Location myLocation;

	PolygonOptions polygonOptions;
	Polygon polygon;

	HashMap<String, Object> polygonList = null;
	ArrayList<Polygon> polygons = null;
	String jsonString = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapdisplay);

		polygonList = new HashMap<String, Object>();

		polygons = new ArrayList<Polygon>();
		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (myMap == null) {
			myMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();

			myMap.setMyLocationEnabled(true);
			myMap.getUiSettings().setZoomControlsEnabled(true);

			myMap.setOnMapClickListener(this);

			// call web services
			System.out.println("URL = " + Constants.AREA_GET_URL);
			if (Utils.isNetworkAvailable(MapDisplayActivity.this)) {
				new WebService(MapDisplayActivity.this, "")
						.execute(Constants.AREA_GET_URL);
			} else {
				Utils.showToast(MapDisplayActivity.this,
						"Check Internet Availability");
			}

			// check if map is created successfully or not
			if (myMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_mapdisplay, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_mapdraw:
			mapDraw();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void drawPolygon(String jsonString) throws JSONException {
		JSONObject jo = new JSONObject(jsonString);

		Iterator<String> keys = jo.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			System.out.println(key);
			JSONArray a = jo.getJSONArray(key);
			ArrayList<LatLng> p = new ArrayList<LatLng>();

			if (a.length() > 0) {
				PolygonOptions pOption = new PolygonOptions();
				pOption.strokeColor(Color.RED);
				pOption.strokeWidth(2);
				pOption.fillColor(Color.BLUE);

				for (int j = 0; j < a.length(); j++) {
					JSONArray b = a.getJSONArray(j);
					LatLng latlng = new LatLng(b.getDouble(0), b.getDouble(1));
					pOption.add(latlng);
					p.add(latlng);
					// if(j==0){
					// myMap.addMarker(new
					// MarkerOptions().position(latlng));
					// }
				}
				polygons.add(myMap.addPolygon(pOption));
			}
			polygonList.put(key, p);
		}

	}

	private boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
		int intersectCount = 0;
		for (int j = 0; j < vertices.size() - 1; j++) {
			if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
				intersectCount++;
			}
		}

		return ((intersectCount % 2) == 1); // odd = inside, even = outside;
	}

	private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

		double aY = vertA.latitude;
		double bY = vertB.latitude;
		double aX = vertA.longitude;
		double bX = vertB.longitude;
		double pY = tap.latitude;
		double pX = tap.longitude;

		if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
				|| (aX < pX && bX < pX)) {
			return false; // a and b can't both be above or below pt.y, and a or
							// b must be east of pt.x
		}

		double m = (aY - bY) / (aX - bX); // Rise over run
		double bee = (-aX) * m + aY; // y = mx + b
		double x = (pY - bee) / m; // algebra is neat!

		return x > pX;
	}

	@Override
	public void onMapClick(LatLng point) {
		Set<String> keys = polygonList.keySet();
		for (String k : keys) {
			ArrayList<LatLng> vertices = (ArrayList<LatLng>) polygonList.get(k);
			if (isPointInPolygon(point, vertices)) {
				System.out.println("Click inside of polygon key =  " + k);
				break;
			} else {
				System.out.println("Click outside of polygon = "
						+ point.toString());

			}
		}

	}

	@Override
	public void loadData(JSONObject jsonResponse) throws JSONException {
		// TODO Auto-generated method stub
		System.out.println(jsonResponse.toString());
		jsonString = jsonResponse.getString("data");
		try {
			drawPolygon(jsonString);
		} catch (JSONException e) {
			System.err.println("Error = " + e.toString());
		}
	}

	private void mapDraw(){
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}
}
