package com.example.drawpolygon;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MainActivity extends ActionBarActivity implements
		OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener,
		OnMarkerDragListener {

	final int RQS_GooglePlayServices = 1;
	private GoogleMap myMap;

	Location myLocation;

	boolean markerClicked;
	PolygonOptions polygonOptions;
	Polygon polygon;

	ArrayList<Marker> markerList;

	LatLng latlng;
	Menu myMenu;

	static final LatLng PERTH = new LatLng(-31.90, 115.86);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		markerList = new ArrayList<Marker>();

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
			myMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			myMap.setMyLocationEnabled(true);
			myMap.getUiSettings().setZoomControlsEnabled(true);

			myMap.setOnMapClickListener(this);
			myMap.setOnMapLongClickListener(this);
			myMap.setOnMarkerClickListener(this);
			myMap.setOnMarkerDragListener(this);

			markerClicked = false;

			polygonOptions = new PolygonOptions();
			// check if map is created successfully or not
			if (myMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		initilizeMap();
	}

	@Override
	public void onMapClick(LatLng point) {
		myMap.animateCamera(CameraUpdateFactory.newLatLng(point));
	}

	@Override
	public void onMapLongClick(LatLng point) {
		Marker m = myMap.addMarker(new MarkerOptions().position(point)
				.title(point.toString()).draggable(true));
		markerList.add(m);
		// Draw polygon
		drawPolygon();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return true;
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// draw polygon;
		drawPolygon();
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		myMenu = menu;
		menu.findItem(R.id.action_ok).setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_ok:
			actionOK();
			return true;
		case R.id.action_mapdisplay:
			gotoMapdisplay();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @return Void
	 **/
	private void drawPolygon() {
		ArrayList<LatLng> points = new ArrayList<LatLng>();

		for (int i = 0; i < markerList.size(); i++) {
			points.add(markerList.get(i).getPosition());
		}
		if (polygon != null) {
			polygon.remove();
			polygon = null;
			if (!myMenu.findItem(R.id.action_ok).isVisible()) {
				myMenu.findItem(R.id.action_ok).setVisible(true);
			}
		}
		polygon = myMap.addPolygon(new PolygonOptions().addAll(points)
				.strokeColor(Color.RED).strokeWidth(2).fillColor(Color.BLUE));
	}

	private void actionOK() {
		JSONArray json = new JSONArray();
		for (LatLng tmp : polygon.getPoints()) {
			JSONArray jarray = new JSONArray();
			jarray.put(Double.valueOf(tmp.latitude));
			jarray.put(Double.valueOf(tmp.longitude));
			json.put(jarray);

		}
		System.out.println("points  = " + json.toString());
		Intent intent = new Intent(MainActivity.this, AreaDetailActivity.class);
		intent.putExtra("area_coordinate", json.toString());
		startActivity(intent);
	}

	private void gotoMapdisplay() {
		Intent intent = new Intent(MainActivity.this, MapDisplayActivity.class);
		startActivity(intent);
	}

}
