package com.game.AI;

import com.badlogic.gdx.math.Vector2;
import com.game.AI.Astar.AStarNew;
import com.game.Board.Agent;
import com.game.Board.Area;
import com.game.Board.Guard;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

public class TrackingLongDistance extends AI{

    private Guard guard;
    private float angle;
    public Vector2 showvect;
    private float enemyx;
    private float enemyy;
    private AI previousAI;
    private int trackcounter;
    private ArrayList previousPos;
    private boolean predictive = false;
    private Instruction instruction;
    private Vector2 opponentLocation;
    private ArrayList<Area> seenStructures = new ArrayList<Area>();
    private ArrayList<Rectangle2D.Float> rectangles;
    private AStarNew astar;

    //TODO once the alerted guards have reached the intruder, make them approach from different directions

    public TrackingLongDistance(Guard guard, Vector2 opponentLocation, AI storeAI, ArrayList<Area> seenStructures)
    {
        this.guard = guard;
        this.opponentLocation = opponentLocation;
        speed = new Stack<Float>();
        rotation = new Stack<Float>();
        previousAI = storeAI;
        this.seenStructures = seenStructures;
        //previousPos = new ArrayList<Vector2>();
        //trackcounter = 0;
//        instruction = new Instruction();
        rectangles = new ArrayList<Rectangle2D.Float>();
        trackIntruder();
    }

    public void trackIntruder(){
        if(previousAI instanceof HeuristicAI) {
            //System.out.println("seen structures: " + seenStructures.size());
            for (Area a : seenStructures) {
                //System.out.println("area: " + a);
                rectangles.add(new Rectangle2D.Float(a.xPos, a.yPos, a.getMaxX() - a.xPos, a.getMaxY() - a.yPos));
            }
//            System.out.println("size of rectangles: " + rectangles.size());
//            System.out.println("opponentlocation is: " + opponentLocation.x + "," + opponentLocation.y);
//            System.out.println("guard centers are: " + guard.xCenter + "," + guard.yCenter);
            astar = new AStarNew(rectangles, guard.xCenter, guard.yCenter, opponentLocation.x, opponentLocation.y, guard);
            //instruction.translate(opponentLocation, guard, false);
            rotation = astar.getRotationStack();
            speed = astar.getSpeedStack();
        }

    }

    @Override
    public float getRotation() {
        if (rotation.empty()){
            previousAI.reset();
            guard.setAI(previousAI);
            return guard.ai.getSpeed();
        }
        else
        {
            return rotation.pop();
        }
        //return rotation.pop();
    }

    @Override
    public float getSpeed() {
        if (speed.empty()){
            previousAI.reset();
            guard.setAI(previousAI);
            return guard.ai.getSpeed();
        }
        else
        {
            return speed.pop();
        }
        //return speed.pop();
    }
    @Override
    public void setAgent(Agent agent) {

    }

    @Override
    public void setStructures(ArrayList<Area> structures) {
        seenStructures = structures;
    }

    @Override
    public void setArea(float areaWidth, float areaHeight) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void seeArea(Area area) {

    }

    @Override
    public void seeAgent(Agent agent) {

    }

    @Override
    public void updatedSeenLocations() {

    }

}
