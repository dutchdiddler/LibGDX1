/**
 * 
 */
package com.game.Board;

import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * @author Lukas Padolevicius
 *
 */

public class Agent extends AssetManager {

	public float xPos;
	public float yPos;
	private Vector2 viewAngle;
	private float speed;
	private float rotation;
	private float maxRotation;
	private float maxSpeed;
	private float soundRange;
	
	public String name;
    public Rectangle bounds;
    public TextureRegion texture;

	
	
	public Agent(float x, float y) {
		xPos = x;
		yPos = y;
		viewAngle = new Vector2(1,1);
		viewAngle.setToRandomDirection();
		speed = 0;
		soundRange = 0;
	}
	
	public float getX() {
		return xPos;
	}
	
	public float getY() {
		return yPos;
	}
	
	public void setPos(float x, float y) {
		xPos = x;
		yPos = y;
        bounds.setX((int) this.xPos);
        bounds.setY((int) this.yPos);
	}
	
	public float getAngle() {
		return viewAngle.angle();
	}

	public float getRotation() {
		return rotation;
	}

	public void rotate(float ang) {
		viewAngle.rotate(ang);
	}
	
	public void setAngle(float ang) {
		viewAngle.setAngle(ang);
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float sp) {
		speed = sp;
	}
	
	public void hearSound(float x, float y) {
		//heard a sound
	}
	
	public void see(Area object) {
		//saw object
	}
	
	public void triggerStep() {	}

    public void setX(int xPos){
        this.xPos += xPos;
        bounds.setX((int) this.xPos);
    }
    public void setY(int yPos){
        this.yPos += yPos;
        bounds.setY((int) this.yPos);
    }
    public void setName(String string){this.name  =string;}


}
