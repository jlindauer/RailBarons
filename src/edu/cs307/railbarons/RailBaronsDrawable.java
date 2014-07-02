package edu.cs307.railbarons;

import android.graphics.Point;
import android.graphics.RectF;


public abstract class RailBaronsDrawable {
	private int imageId;
	private BoardLayers layer;
	private int pos_x, pos_y;
	private RectF bounds;
	private boolean highlight;
	
	public RailBaronsDrawable(BoardLayers layer, int imageId) {
		this.imageId = imageId;
		this.layer = layer;
		this.bounds = new RectF();
		this.highlight = false;
	}
	
	public void setPositionX(int x) { this.pos_x = x; }
	public void setPositionY(int y) { this.pos_y = y; }
	public void setPosition(int x, int y) { setPositionX(x); setPositionY(y); }
	public Point getPosition() { return new Point(pos_x, pos_y); }
	public int getPosX() { return pos_x; }
	public int getPosY() { return pos_y; }
	public int getImageId() { return imageId; }
	public void setBounds(RectF bound) {
		bounds.set(bound);
	}
	public void setHighlight(boolean h) {
		highlight = h;
	}
	public boolean getHighlight() { return highlight; }
	public RectF getBounds() { return bounds; }
	public BoardLayers getLayer() { return layer; }
	
	public abstract void calculatePosition();
}
