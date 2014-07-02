package edu.cs307.railbarons;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;

public class Renderer {
	/* This section is incomplete
	 * What is done is only temporary to test the functionality
	 * What needs to be accomplished is to split the drawables into
	 *   layer sections, then to pass the sections to each of the layers.
	 * THIS IS A BROKEN CLASS AT THE MOMENT
	 */
	// Color Constants
	private final static int TRAINBORDERCOLOR = Color.rgb(21, 58, 249); 
	private final static int TRAINCOLORMAIN = Color.BLACK;
	private final static int RAILCOLORACTIVE = Color.rgb(195, 255, 155);			//Yellow Green
	private final static int RAILCOLORINACTIVE = Color.argb(0, 0, 0, 0);			//Transparent
	private final static int RAILCOLORSELECTED = Color.rgb(0, 255, 255);			//Baby Blue
	private final static int RAILCOLORAVAILABLE = Color.rgb(160, 160, 205);			//Purple
	private final static int OBJECTHIGHLIGHTCOLOR = Color.argb(150, 255, 255, 255);
	
	// Numerical Constants
	private final static float TRAINRADIUS = 15f;
	private final static float TRAINBORDERTHICKNESS = 2f;
	private final static float CITYSCALEFACTOR = 0.65f;
	
	// Static Variables
	public static boolean debug = false;
	
	// Global Variables
	private static Context context;
	private static Resources resources;
	private static ArrayList<BitmapCacheObject> bitmapCache;
	private static Typeface typeface;
	private static Matrix mat;
	private static ArrayList<Train> trains;
	private static ArrayList<Rail> rails;
	private static ArrayList<City> cities;
	private static ArrayList<City> route;
	private static Train routeTrainSelected;
//	private static Point tap;
	private static Paint resourcePaint;
	private static City buildRailCity1, buildRailCity2;
	private static Rail buildRailRail;
	
	private static EventListenerState eventState;
	
	//TODO: Convert to static initizlization function
	public static void initialize(Context con) {
		context = con;
		resources = context.getResources();
		typeface = Typeface.create("Helvetica", Typeface.NORMAL);
		bitmapCache = new ArrayList<BitmapCacheObject>();
		mat = new Matrix();
		resourcePaint = new Paint();
		resourcePaint.setTextSize(30);
		resourcePaint.setColor(Color.YELLOW);
		
		trains = InfoCenter.getTrains();
		rails = InfoCenter.getRails();
		cities = InfoCenter.getCities();
		
		eventState = EventListenerState.EVENTSTATEVIEWCITY;
		route = null;
		buildRailCity1 = null;
		buildRailCity2 = null;
		buildRailRail = null;

		loadImages();
	}
	
	private static void loadImages() {
		getBitmapById(R.drawable.map);
		getBitmapById(R.drawable.city1);
		getBitmapById(R.drawable.city2);
		getBitmapById(R.drawable.city3);
		getBitmapById(R.drawable.city4);
		getBitmapById(R.drawable.city5);
	}
	
	public static void drawLayers(Canvas c, Paint p, Board b) {
		if( trains==null ) { trains=InfoCenter.getTrains(); }
		if( rails==null ) { rails=InfoCenter.getRails(); }
		if( cities==null ) { cities=InfoCenter.getCities(); }
//		c = b.scaleWithOffset(c);
		drawMapLayer(c);
		drawRailLayer(c, p, rails);
		drawTrainLayer(c, p, trains);
		drawCityLayer(c, p, cities);
		c.restore();
		drawResourceLayer(c, b);
		if(debug) {
			drawTouchBounds(c, p);
		}
	}
	
	private static void drawCityLayer(Canvas c, Paint p, ArrayList<City> cities) {
		for(City city:cities) {
			// Get image
			BitmapCacheObject temp = getBitmapById(city.getImageId());
			
			// Determine the scale factor so the city will appear the correct size
			float scaleFactor = CITYSCALEFACTOR;
			switch(city.getImageId()) {
			case R.drawable.city1: scaleFactor -= 0.35f; break;
			case R.drawable.city2: scaleFactor -= 0.2f; break;
			case R.drawable.city3: scaleFactor -= 0.15f; break;
			case R.drawable.city4: scaleFactor -= 0.1; break;
			case R.drawable.city5: scaleFactor -= 0.0f; break;
			}
			
			// Scale and Translate city image to it's appropriate size and location
			p.setColor(Color.WHITE);
			PointF loc = convertCenterToCorner(temp.getImage(), city.getPosition(), scaleFactor);
			mat.set(new Matrix());
			mat.postScale(scaleFactor, scaleFactor);
			mat.postTranslate(loc.x, loc.y);
			
			// Set bounds for city object
			RectF tempRect = new RectF(0, 0, temp.getImage().getWidth(), temp.getImage().getHeight());
			mat.mapRect(tempRect);
			city.setBounds(tempRect);
			c.drawBitmap(temp.getImage(), mat, p);
			
			// Draw text of name
			p.setTypeface(typeface);
			p.setColor(Color.WHITE);
			p.setTextSize(30);
			c.drawText(city.getCityName(), loc.x, 10f + loc.y + scaleFactor*(float)temp.image.getHeight(), p);
			
			// Highlighting the city
			if( city.getHighlight() ) {
				p.setColor(OBJECTHIGHLIGHTCOLOR);
				c.drawRect(city.getBounds(), p);
			}
		}
	}
	
	private static void drawRailLayer(Canvas c, Paint p, ArrayList<Rail> rails) {
		for(Rail rail:rails) {
			Point start = rail.getCity1().getPosition();
			Point end = rail.getCity2().getPosition();
			
			// Draw the rail itself
			p.setStrokeWidth(12);
			switch(rail.getDrawStyle()) {
			case RAILAVAILABLE:
				p.setColor(RAILCOLORAVAILABLE);
				break;
			case RAILACTIVE:
				p.setColor(RAILCOLORACTIVE);
				break;
			case RAILSELECTED:
				p.setColor(RAILCOLORSELECTED);
				break;
			case RAILINACTIVE:
			default:
				p.setColor(RAILCOLORINACTIVE);
				break;
			}
			c.drawLine(start.x, start.y, end.x, end.y, p);
		}
	}
	
	public static void drawMapLayer(Canvas c) {
		BitmapCacheObject bit = getBitmapById(R.drawable.map);
		bit.getDrawable().setBounds(0, 0, 3195, 2048);
		bit.getDrawable().draw(c);
	}
	
	private static void drawTrainLayer(Canvas c, Paint p, ArrayList<Train> trains) {
		for(Train train:trains) {	
			if (train.getDestinationCityId() != 0) {
				train.calculatePosition();
				train.setBounds(new RectF(train.getPosition().x - TRAINRADIUS,
					train.getPosition().y - TRAINRADIUS,
					train.getPosition().x + TRAINRADIUS,
					train.getPosition().y + TRAINRADIUS));
				drawTrain(c, p, new PointF(train.getPosition()));
			}
		}
	}
	
	public static void drawResourceLayer(Canvas c, Board b) {
		String text = "$"+String.valueOf(InfoCenter.getUser().getMoney());
		float height = b.bHeight()-20;
		float width = b.bWidth()-150;
		c.drawText(text, width, height, resourcePaint);
		
	}
	private static void drawTrain(Canvas c, Paint p, PointF loc) {
		// Draw Train Circle Border
		p.setColor(TRAINBORDERCOLOR);
		c.drawCircle(loc.x, loc.y, TRAINRADIUS+(2*TRAINBORDERTHICKNESS), p);
		
		// Draw Train Circle
		p.setColor(TRAINCOLORMAIN);
		c.drawCircle(loc.x, loc.y, TRAINRADIUS, p);
	}
	
	private static void drawTouchBounds(Canvas c, Paint p) {
		p.setColor(Color.BLACK);
		for(City cit:cities) {
			c.drawRect(cit.getBounds(), p);
		}
		p.setColor(Color.GRAY);
		for(Train tra:trains) {
			c.drawRect(tra.getBounds(), p);
		}
	}
	
	private static PointF convertCenterToCorner(Bitmap b, Point loc, float scale) {
		return new PointF((float)loc.x - (scale*((float)b.getWidth())/2),
						  (float)loc.y - (scale*((float)b.getHeight())/2));
	}
	
	private static BitmapCacheObject getBitmapById(int id) {
		for(BitmapCacheObject b : bitmapCache) {
			if( b.imageId == id ) return b;
		}
		// Else (we didn't find the image)
		BitmapCacheObject b = new BitmapCacheObject(resources, id);
		bitmapCache.add(b);
		return b;
	}
	
	// Used to cache bitmaps
	private static class BitmapCacheObject {
		public int imageId;
		private Bitmap image;
		private BitmapDrawable imageDrawable;
		private Resources res;
		public BitmapCacheObject(Resources res, int id) {
			this.res = res;
			imageId = id;
			image = BitmapFactory.decodeResource(this.res, imageId);
			imageDrawable = null;
		}
		public Bitmap getImage() {
			return image;
		}
		
		public BitmapDrawable getDrawable() {
			if( imageDrawable==null ) {
				imageDrawable = new BitmapDrawable(res, image);
			}
			return imageDrawable;
		}
	}
	
	public enum EventListenerState {
		EVENTSTATEVIEWCITY, EVENTSTATEBUILDRAIL, EVENTSTATESELECTDESTINATION;
	}
	
	/* This function will be used to handle events on the canvas */
	public static void eventHandler(Point touchEvent) {
		for(City c : cities) {
			if( c.getBounds().contains(touchEvent.x, touchEvent.y) ) {
				// Execute something for the event
				eventHandlerCity(c);
				return;
			}
		}
		for(Train t : trains) {
			if( t.getBounds().contains(touchEvent.x, touchEvent.y) ) {
				return;
			}
		}
		return;
	}
	
	private static void eventHandlerCity(City city) {
		switch(eventState) {
		case EVENTSTATEVIEWCITY:
			// Code to start the station view activity
			Intent myIntent = new Intent(context, Activity_Station.class);
			myIntent.putExtra("cityId", city.getCityId());
			context.startActivity(myIntent);
			break;
		case EVENTSTATEBUILDRAIL:
			// Code to perform build rail events
			if( buildRailCity1==null ) {
				for(Rail r:city.getRails()) {
					if(r.isVisible()) {
						buildRailCity1 = city;
						buildRailCity1.setHighlight(true);
						highlightRails(city, Rail.DrawStyle.RAILAVAILABLE);
						break;
					}
				}
			} else if( buildRailCity1.equals(city) ) {
				return;
			} else if( buildRailCity2==null ) {
				for(Rail r:buildRailCity1.getRails()) {
					if(r.isVisible()) r.setDrawStyle(Rail.DrawStyle.RAILACTIVE);
					else r.setDrawStyle(Rail.DrawStyle.RAILINACTIVE);
					
					if( r.getCity1().equals(city) || r.getCity2().equals(city) && !r.isVisible() ) {
						city.setHighlight(true);
						r.setDrawStyle(Rail.DrawStyle.RAILSELECTED);
						buildRailRail = r;
						buildRailCity2 = city;
					}
				}
				if( buildRailRail==null ) {
					buildRailCity2 = null;
					highlightRails(buildRailCity1, Rail.DrawStyle.RAILAVAILABLE);
				}
			}
			break;
		case EVENTSTATESELECTDESTINATION:
			// Code to perform selecting a route
			for(Rail ra:rails ) {
				// Check if the rail is available.  If not, move on to the next one
				if( !ra.isVisible() ) {
					continue;
				// If it is available, check that the cities match
				} else if( (ra.getCity1().equals(city) && ra.getCity2().equals(route.get(route.size()-1))) ||
						   (ra.getCity2().equals(city) && ra.getCity1().equals(route.get(route.size()-1))) ) {
					// If they match, add it and select the rail
					route.add(city);
					city.setHighlight(true);
					ra.setDrawStyle(Rail.DrawStyle.RAILSELECTED);
					break;
				}
			}
			break;
		}
	}
	
	private static void highlightRails(City city, Rail.DrawStyle d) {
		for(Rail rail:rails) {
			if ((rail.getCity1().equals(city) || rail.getCity2().equals(city)) 
				&& !(rail.getDrawStyle()).equals(Rail.DrawStyle.RAILACTIVE))
				rail.setDrawStyle(d);
		}
	}
	
	public static EventListenerState getEventState() {
		return eventState;
	}
	
	public static void setEventStateSelectDestination(int trainId) {
		eventState = EventListenerState.EVENTSTATESELECTDESTINATION;
		routeTrainSelected = InfoCenter.findTrainById(trainId);
		City temp = InfoCenter.findCityById(routeTrainSelected.getCurrentCityId());
		temp.setHighlight(true);
		route = new ArrayList<City>();
		route.add(temp);
	}
	
	public static void cancelEventStateSelectDestination() {
		if(route!=null) {
			for(City c:route) {
				c.setHighlight(false);
			}
			route.clear();
		}
		for(Rail r:rails) {
			if(r.isVisible()) {
				r.setDrawStyle(Rail.DrawStyle.RAILACTIVE);
			} else {
				r.setDrawStyle(Rail.DrawStyle.RAILINACTIVE);
			}
		}
		routeTrainSelected = null;
		eventState = EventListenerState.EVENTSTATEVIEWCITY;
	}
	
	public static void undoEventStateSelectDestination() {
		// YOU SHALL NOT PASS
		// (when removing the first city)
		if( route!=null ) {
			if( route.size() > 1 ) {
				City temp = route.remove(route.size()-1);
				temp.setHighlight(route.contains(temp));
				for(Rail r:temp.getRails()) {
					if(r.getDrawStyle() == Rail.DrawStyle.RAILSELECTED) {
						r.setDrawStyle(Rail.DrawStyle.RAILACTIVE);
					}
				}
				for(City c:route) {
					for(Rail r:c.getRails()){
						if(r.getCity1().equals(c) && r.getCity2().getHighlight()){
							r.setDrawStyle(Rail.DrawStyle.RAILSELECTED);
						}
					}
				}
			}
		}
	}
	
	public static void submitEventStateSelectDestination() {
		eventState = EventListenerState.EVENTSTATEVIEWCITY;
		for(City c:route) {
			c.setHighlight(false);
			for(Rail r:c.getRails()) {
				if( r.isVisible() ) r.setDrawStyle(Rail.DrawStyle.RAILACTIVE);
				else r.setDrawStyle(Rail.DrawStyle.RAILINACTIVE);
			}
		}
		ArrayList<City> temp = route;
		route = null;
		int tempTrain = routeTrainSelected.getTrainId();
		routeTrainSelected = null;
		InfoCenter.addPath(temp, tempTrain);
	}
	
	public static void setEventStatePurchaseRail() {
		eventState = EventListenerState.EVENTSTATEBUILDRAIL;
	}
	
	public static void setEventStateViewCity() {
		eventState = EventListenerState.EVENTSTATEVIEWCITY;
	}

	// BuildRailRail, so contradictory, yet so perfectly rounded to describe its purpose
	public static void cancelEventStateBuildRail() {
		if( buildRailCity1!=null ) {
			buildRailCity1.setHighlight(false);
			for(Rail r:buildRailCity1.getRails()) {
				if(!r.isVisible())r.setDrawStyle(Rail.DrawStyle.RAILINACTIVE);
			}
		}
		if( buildRailCity2!=null ) {
			buildRailCity2.setHighlight(false);
		}
		if( buildRailRail!=null ) {
			buildRailRail.setDrawStyle(Rail.DrawStyle.RAILINACTIVE);
		}
		buildRailCity1 = buildRailCity2 = null;
		buildRailRail = null;
		eventState = EventListenerState.EVENTSTATEVIEWCITY;
	}

	public static boolean submitEventStateBuildRail() {
		//eventState = EventListenerState.EVENTSTATEVIEWCITY;
		if( buildRailCity1 != null && buildRailCity2 != null && buildRailRail != null ) {
			InfoCenter.update(buildRailRail, BoardLayers.RAIL);
			if( buildRailRail.isVisible() ) {
				int railId = buildRailRail.getRailId();
				Rail tmp = InfoCenter.findRailById(railId);
				tmp.setDrawStyle(Rail.DrawStyle.RAILACTIVE);
				tmp.setVisible(1);
				buildRailRail.setDrawStyle(Rail.DrawStyle.RAILACTIVE);
				buildRailCity1.setHighlight(false);
				buildRailCity2.setHighlight(false);
				buildRailCity1 = null;
				buildRailCity2 = null;
				buildRailRail = null;
				eventState = EventListenerState.EVENTSTATEVIEWCITY;
				return true;
			}
		}
		return false;
	}
}
