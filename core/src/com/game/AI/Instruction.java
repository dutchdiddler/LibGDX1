/**
 * Class used for translating simple decision making from AIs into sets of 
 * instructions used by logic handling classes to simulate a game
 */
package com.game.AI;

import com.badlogic.gdx.math.Vector2;
import com.game.Board.Agent;
import com.game.Board.Board;

import java.util.Stack;


/**
 * @author Lukas Padolevicius
 *
 */
public class Instruction {
		
	private Stack<Float> rotations;
	private Stack<Float> speeds;
	
	public Instruction() {
		rotations = new Stack();
		speeds = new Stack();
	}
	
	public void translate(Vector2 destination, Agent agent, boolean exploring) {
		
		Vector2 positions = new Vector2((destination.x-agent.xCenter),(destination.y-agent.yCenter));
		float pathLength = positions.len();
		float turnAngle = agent.viewAngle.angle(positions);
		
		//prepare to invert turning if the shortest angle is negative
		boolean positive = true;
		if (turnAngle < 0) {
			positive = false;
			turnAngle = -turnAngle;
		}
		
		//find the number of times to turn at maximum and the leftover small turn
		float maxTurn = agent.turningCircle/Board.fps;
		if(exploring) {
			maxTurn = (float) 45.0/Board.fps;
		}
		float ufTurn = turnAngle/maxTurn;
		int turncount = (int) ufTurn;
		float leftoverTurn = turnAngle - ((float) turncount * maxTurn);
		
		//invert back the values as needed
		if(!positive) {
			maxTurn = -maxTurn;
			leftoverTurn = -leftoverTurn;
		}
		for(int i=0; i < turncount; i++) {
			rotations.push(maxTurn);
			speeds.push(0f);
		}
		rotations.push(leftoverTurn);
		speeds.push(0f);
		
		//now repeat for only speed
		float maxWalk = agent.maxSpeed/Board.fps;
		float ufWalk = pathLength/maxWalk;
		int walkcount = (int) ufWalk;
		float leftoverSpeed = pathLength - ((float) walkcount * maxWalk);
		
		for(int i=0; i < walkcount; i++) {
			rotations.push(0f);
			speeds.push(maxWalk);
		}
		rotations.push(0f);
		speeds.push(leftoverSpeed);
		
		Stack<Float> reverseRotations = new Stack<Float>();
		Stack<Float> reverseSpeeds = new Stack<Float>();

		while(!speeds.isEmpty()) {
			reverseRotations.push(rotations.pop());
			reverseSpeeds.push(speeds.pop());
		}
		speeds = reverseSpeeds;
		rotations = reverseRotations;
		
	}
	
	public void translate(Vector2 destination, Vector2 start, float angle, float turningCircle, float maxSpeed) {
		

		//find the length to traverse and how much to turn
		Vector2 positions = new Vector2((destination.x-start.x),(destination.y-start.y));
		float pathLength = positions.len();
		float turnAngle = positions.angle()-angle;
		while(turnAngle < -180 || turnAngle > 180) {
			if(turnAngle < -180) {turnAngle = turnAngle+360;}
			if(turnAngle > 180) {turnAngle = turnAngle-360;}
			
		}
				
		//prepare to invert turning if the shortest angle is negative
		boolean positive = true;
		if (turnAngle < 0) {
			positive = false;
			turnAngle = -turnAngle;
		}
		
		//find the number of times to turn at maximum and the leftover small turn
		float maxTurn = turningCircle/Board.fps;
		float ufTurn = turnAngle/maxTurn;
		int turncount = (int) ufTurn;
		float leftoverTurn = turnAngle - ((float) turncount * maxTurn);
		
		//invert back the values as needed
		if(!positive) {
			maxTurn = -maxTurn;
			leftoverTurn = -leftoverTurn;
		}
		for(int i=0; i < turncount; i++) {
			rotations.push(maxTurn);
			speeds.push(0f);
		}
		rotations.push(leftoverTurn);
		speeds.push(0f);
		
		//now repeat for only speed
		float maxWalk = maxSpeed/Board.fps;
		float ufWalk = pathLength/maxWalk;
		int walkcount = (int) ufWalk;
		float leftoverSpeed = pathLength - ((float) walkcount * maxWalk);
		
		for(int i=0; i < walkcount; i++) {
			rotations.push(0f);
			speeds.push(maxWalk);
		}
		rotations.push(0f);
		speeds.push(leftoverSpeed);
		
		Stack<Float> reverseRotations = new Stack<Float>();
		Stack<Float> reverseSpeeds = new Stack<Float>();

		while(!speeds.isEmpty()) {
			reverseRotations.push(rotations.pop());
			reverseSpeeds.push(speeds.pop());
		}
		speeds = reverseSpeeds;
		rotations = reverseRotations;
		
	}
	
	
	public int translateWithSprinting(Vector2 destination, Agent agent, boolean exploring) {
		
		Vector2 positions = new Vector2((destination.x-agent.xCenter),(destination.y-agent.yCenter));
		float pathLength = positions.len();
		float turnAngle = agent.viewAngle.angle(positions);
		
		//prepare to invert turning if the shortest angle is negative
		boolean positive = true;
		if (turnAngle < 0) {
			positive = false;
			turnAngle = -turnAngle;
		}
		
		//find the number of times to turn at maximum and the leftover small turn
		float maxTurn = agent.turningCircle/Board.fps;
		if(exploring) {
			maxTurn = (float) 45.0/Board.fps;
		}
		float ufTurn = turnAngle/maxTurn;
		int turncount = (int) ufTurn;
		float leftoverTurn = turnAngle - ((float) turncount * maxTurn);
		
		//invert back the values as needed
		if(!positive) {
			maxTurn = -maxTurn;
			leftoverTurn = -leftoverTurn;
		}
		for(int i=0; i < turncount; i++) {
			rotations.push(maxTurn);
			speeds.push(0f);
		}
		rotations.push(leftoverTurn);
		speeds.push(0f);
		
		//solve normally the distance you cannot cover in one sprint
		float subPathLength = 0;
		if(pathLength > 15) {
			subPathLength = pathLength - 15;
			pathLength = 15;
		}
		
		//now repeat for only speed
				int sprintCount = 0;
				float maxWalk = 3f/Board.fps;
				float ufWalk = pathLength/maxWalk;
				int walkcount = (int) ufWalk;
				float leftoverSpeed = pathLength - ((float) walkcount * maxWalk);
				
				for(int i=0; i < walkcount; i++) {
					rotations.push(0f);
					speeds.push(maxWalk);
				}
				if(leftoverSpeed != 0) {
					rotations.push(0f);
					speeds.push(leftoverSpeed);
					sprintCount++;
				}
				sprintCount = sprintCount+walkcount;

		if(subPathLength != 0) {
			maxWalk = agent.maxSpeed/Board.fps;
			ufWalk = subPathLength/maxWalk;
			walkcount = (int) ufWalk;
			leftoverSpeed = subPathLength - ((float) walkcount * maxWalk);
			
			for(int i=0; i < walkcount; i++) {
				rotations.push(0f);
				speeds.push(maxWalk);
			}
			rotations.push(0f);
			speeds.push(leftoverSpeed);
		}
			
				
		Stack<Float> reverseRotations = new Stack<Float>();
		Stack<Float> reverseSpeeds = new Stack<Float>();

		while(!speeds.isEmpty()) {
			reverseRotations.push(rotations.pop());
			reverseSpeeds.push(speeds.pop());
		}
		speeds = reverseSpeeds;
		rotations = reverseRotations;
		return speeds.size()-sprintCount;
	}
	
	public Stack<Float> getRotations() {
		Stack<Float> rots = rotations;
		rotations = new Stack<Float>();
		return rots;
	}
	
	public Stack<Float> getSpeeds() {
		Stack<Float> sps = speeds;
		speeds = new Stack<Float>();
		return sps;
	}
}
