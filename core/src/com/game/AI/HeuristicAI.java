package com.game.AI;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.AI.Astar.AStarNew;
import com.game.AI.Dijkstra.Dijkstra;
import com.game.Board.Agent;
import com.game.Board.Area;
import com.game.Board.Board;
import com.game.Board.Guard;
import com.game.Board.Intruder;
import com.game.Board.LowVisionArea;
import com.game.Board.MapDivider;
import com.game.Board.OuterWall;
import com.game.CopsAndRobbers;
import com.game.States.MapState;

import org.w3c.dom.css.Rect;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Random;
import java.util.Stack;

public class HeuristicAI extends AI {

   // private Agent agent;
    private Vector2 point = new Vector2();
    //private Agent agent;
    //private Vector2 point;
    private float guardAngle, prevAngle;
    private Random rand = new Random();
    private ArrayList<Area> structures;
    //    private float areaWidth,areaHeight;
    private final int FACTOR = 20, AVERYBIGNUMBER = 500, Y_FACTOR = 15, X_FACTOR = 49, DEGREE_RANGE = 90; //number of squares that we want
    public final static int BOARD_WIDTH = 400;
    public final static int BOARD_HEIGHT = 200;
    private ArrayList<Vector2> explorationPoints;
    private String pattern;
    public static final float X_REDUC = MapState.X_REDUC;
    public static final float Y_REDUC = MapState.Y_REDUC;
    public boolean startingPos, guardSeen;
    public ArrayList<Area> exploredStructures;
    private Vector2 currentExplorationPoint;
    private Vector2 previousExplorationPoint;
    private Direction currentDirection = Direction.NORTH;
    private ArrayList<Point2D.Float> cornerPoints;
    public ArrayList<Rectangle2D.Float> astarStructures;
    private AStarNew astar;
    private Vector2 prevPoint;
    private int randomFactor = 98;

    public HeuristicAI(Agent agent)
    {
        this.agent = agent;
        speed = new Stack<Float>();
        rotation = new Stack<Float>();
        instruction = new Instruction();
        seenStructures = new ArrayList<Area>();
        exploredStructures = new ArrayList<Area>();
        startingPos = false;
        structures = new ArrayList<Area>();
        cornerPoints = new ArrayList<Point2D.Float>();
        astarStructures = new ArrayList<Rectangle2D.Float>();
        explorationSetUp();
    }

    public HeuristicAI()
    {
        speed = new Stack<Float>();
        rotation = new Stack<Float>();
        instruction = new Instruction();
        seenStructures = new ArrayList<Area>();
        exploredStructures = new ArrayList<Area>();
        startingPos = false;
        structures = new ArrayList<Area>();
        cornerPoints = new ArrayList<Point2D.Float>();
        astarStructures = new ArrayList<Rectangle2D.Float>();
        explorationSetUp();

    }

/*
    public HeuristicAI(Agent agent, float areaWidth, float areaHeight)
    {
        this.agent = agent;
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;
        exploration();
    }
*/

    public void explorationSetUp() {

        explorationPoints = new ArrayList<Vector2>();
        float tempX = (BOARD_WIDTH/MapState.X_REDUC) / FACTOR;
        float tempY = (650/MapState.Y_REDUC) / FACTOR;
        for (int i = 1; i < X_FACTOR; i++) {
            for (int j = 1; j < Y_FACTOR; j++) {
                explorationPoints.add(new Vector2(i * tempX + 0.5f * tempX, j * tempY + 0.5f * tempY));
            }
        }

        //TODO uncomment for evenly spaced points
//        explorationPoints = new ArrayList<Vector2>();
//        float tempX = 10;
//        float tempY = 10;
//        for (int i = 1; i < BOARD_WIDTH/tempX - 1; i++) {
//            for (int j = 1; j < BOARD_HEIGHT/tempY - 1; j++) {
//                explorationPoints.add(new Vector2(i * tempX + 0.5f * tempX, j * tempY + 0.5f * tempY));
//            }
//        }


        for(int i = 0; i < explorationPoints.size(); i++){
            //System.out.print("x = " + explorationPoints.get(i).x + " " + " y = " + explorationPoints.get(i).y );
            //System.out.println(" ");
        }

        currentExplorationPoint = explorationPoints.get(0);
    }

    public void explorationSetUpGuards(){
        explorationPoints = new ArrayList<Vector2>();
        float tempX = 10;
        float tempY = 10;
//        float tempX = (BOARD_WIDTH/MapState.X_REDUC) / FACTOR;
//        float tempY = (650/MapState.Y_REDUC) / FACTOR;

        float areaWidth = cornerPoints.get(2).x - cornerPoints.get(0).x;
        int nrOfSquaresWidth = (int) (areaWidth / tempX);
        float areaHeight = cornerPoints.get(2).y - cornerPoints.get(0).y;
        int nrOfSquaresHeight = (int) (areaHeight / tempY);

        for (int i = 1; i < nrOfSquaresWidth - 1; i++) {
            for (int j = 1; j < nrOfSquaresHeight - 1; j++) {
                explorationPoints.add(new Vector2(cornerPoints.get(0).x + (i * tempX + 0.5f * tempX), cornerPoints.get(0).y +  (j * tempY + 0.5f * tempY)));
            }
        }

        /*for(int i = 0; i < explorationPoints.size(); i++){
            System.out.print("x = " + explorationPoints.get(i).x + " " + " y = " + explorationPoints.get(i).y );
            System.out.println(" ");
        }*/
    }

    public void exploration() {
        if (pattern.equals("closest")) {
//            System.out.println("running heuristic closestUnknown");
            point = closestUnknown();
        }
        else if (pattern.equals("random")) {

//            System.out.println("running heuristic random");
            point = allOptions();
        }
//        else if (pattern.equals("all")) {
//            point = allOptions();
//        }
        else if (pattern.equals("heatmap")){

         //   System.out.println("running heuristic heatmap");
            point = heatMapMovement();
        }

        if (prevPoint == point)
        {
          //  System.out.println("adjusting the point");
//            point.x = point.x + 1;
//            point.y = point.y + 1;
            explorationPoints.remove(point);
        }
        prevPoint = point;

//        System.out.println("Agent is: " + agent);
//        System.out.println("calling a star with location: " + agent.xCenter + "," + agent.yCenter);
//        System.out.println("target loc for astar: " + point.x + "," + point.y);
        AStarNew astar = new AStarNew(astarStructures, agent.xCenter, agent.yCenter, point.x, point.y,agent);
        rotation = astar.getRotationStack();
        speed = astar.getSpeedStack();
       /* Dijkstra dijkstra = new Dijkstra(seenStructures,agent, point.x, point.y);
        rotation = dijkstra.rotations;
        speed = dijkstra.speeds; */
    }

    public void moveGuardToCenter(Vector2 centerLocation) {
//        System.out.println("Agent is: " + agent);
//        System.out.println("calling a star with location: " + agent.xCenter + "," + agent.yCenter);
//        System.out.println("target centerloc for astar: " + centerLocation.x + "," + centerLocation.y);

        AStarNew astar = new AStarNew(astarStructures,agent.xCenter, agent.yCenter, centerLocation.x, centerLocation.y,agent);

        rotation = astar.getRotationStack();
        speed = astar.getSpeedStack();
    }
    private Vector2 closestUnknown() {
        //Checks if there are structures that need to be explored and moves to them
        if (exploredStructures.size() > 0){

            for(int i = 0; i < explorationPoints.size(); i++){
                for (int j = 0; j<exploredStructures.size();j++){
                    if(exploredStructures.get(j).area.contains(explorationPoints.get(i))){
                        explorationPoints.remove(i);
                    }
                }
            }

            if(exploredStructures.get(0).xPos > 12 && exploredStructures.get(0).xPos < 388 && exploredStructures.get(0).yPos < 195 && exploredStructures.get(0).yPos > 15){
                float distance = 100000;
                int index = 0;
                for (int i = 0; i < explorationPoints.size(); i++) {
                    float temp_distance = explorationPoints.get(i).dst2(exploredStructures.get(0).xPos, exploredStructures.get(0).yPos);
                    if (temp_distance < distance) {
                        distance = temp_distance;
                        index = i;
                    }
                }

                point = explorationPoints.get(index);
                exploredStructures.remove(0);
                return point;
            }
            exploredStructures.remove(0);

        }

        Random rand = new Random();
        int percent = rand.nextInt(100);
       // point = explorationPoints.get(n);

        //Checks to see if the agent is in a starting corner
        if(startingPos == false) {
            float startingX = agent.getX();
            float startingY = agent.getY();
            point = new Vector2(startingX, startingY);
            if (point.x > 200) {
                if (point.y > 100) {
                    startingPos = true;
                    return new Vector2(380, 180);
                } else {

                    startingPos = true;
                    return new Vector2(380, 30);
                }
            } else {
                if (point.y > 100) {
                    startingPos = true;
                    return new Vector2(30, 180);
                } else {

                    startingPos = true;
                    return new Vector2(30, 30);
                }
            }
        }//Moves to the closest exploration point if there is nothing interesting to search
        else if(percent < randomFactor) {
            //System.out.println("percent = " + percent);
            float distance = 100000;
            int index = 0;
            for (int i = 0; i < explorationPoints.size(); i++) {
                float temp_distance = explorationPoints.get(i).dst2(agent.xPos,agent.yPos);
                if(temp_distance < distance ){
                    distance = temp_distance;
                    index = i;
                }
            }
            point = explorationPoints.get(index);


//            System.out.println("closest new exploration point: " + point);
            if (point == prevPoint && agent.speed == 0)
            {
                explorationPoints.remove(point);
//                System.out.println("point is the same as previously, so we run closestUnknown again");
               return allOptions();
            }
        }
        else{
            //System.out.println("made it to all options");
            //System.out.println("all options percent = " + percent);
            point = allOptions();
            //System.out.println("options x = " + point.x + " " + "options y = " + point.y);

            if (point == prevPoint && agent.speed == 0)
            {
                explorationPoints.remove(point);
//                System.out.println("point is the same as previously, so we run closestUnknown again");
                return closestUnknown();
            }
        }
        prevPoint = point;
        explorationPoints.remove(point);
        return point;
    }


    public Vector2 allOptions(){
        //Checks if there are structures that need to be explored and moves to them
        if (exploredStructures.size() > 0){

            for(int i = 0; i < explorationPoints.size(); i++){

                    if (exploredStructures.get(0).area.contains(explorationPoints.get(i))) {
                        explorationPoints.remove(i);
                    }

            }
            if(exploredStructures.get(0).xPos > 12 && exploredStructures.get(0).xPos < 388 && exploredStructures.get(0).yPos < 195 && exploredStructures.get(0).yPos > 15){
                point = new Vector2(exploredStructures.get(0).xPos,exploredStructures.get(0).yPos);
                exploredStructures.remove(0);
                return point;
            }
            exploredStructures.remove(0);
        }

        for (int i = 0; i < explorationPoints.size(); i++) {
            float temp_distance = explorationPoints.get(i).dst2(agent.xPos,agent.yPos);
            if(temp_distance < 30 && temp_distance > 20 ){
                return explorationPoints.get(i);
            }
        }

        Random rand = new Random();
        int n = rand.nextInt(explorationPoints.size());
        point = explorationPoints.get(n);
        return point;
    }

    public Vector2 heatMapMovement() {
        /**
         * Choose one of the exploration points to go to based on:
         * -has it already been explored
         * -distance
         * -are we moving in the same direction as in the previous iteration
         */

        //TODO start at one of the corners
      //  System.out.println("size of explored structures: " + exploredStructures.size());
        if (exploredStructures.size() > 0) {

            for (int i = 0; i < explorationPoints.size(); i++) {
                for (int j = 0; j<exploredStructures.size();j++) {
                    if (exploredStructures.get(j).area.contains(explorationPoints.get(i))) {
                        explorationPoints.remove(i);
                    }
                }
            }
       //     System.out.println("exploredstructures size is: " + exploredStructures.size());
            if (exploredStructures.get(0).xPos > 12 && exploredStructures.get(0).xPos < 388 && exploredStructures.get(0).yPos < 195 && exploredStructures.get(0).yPos > 15) {
                float distance = 100000;
                int index = 0;
                for (int i = 0; i < explorationPoints.size(); i++) {
                    float temp_distance = explorationPoints.get(i).dst2(exploredStructures.get(0).xPos, exploredStructures.get(0).yPos);
                    if (temp_distance < distance) {
                        distance = temp_distance;
                        index = i;
                    }
                }
                if(explorationPoints.size() != 0) {
                    point = explorationPoints.get(index);
                }
                exploredStructures.remove(0);
                return point;
            }
            exploredStructures.remove(0);
        }

            float minPointDistance = Float.MAX_VALUE;
            ArrayList<Vector2> closestPoints = new ArrayList<Vector2>();

            //find closests points
            for (int i = 0; i < explorationPoints.size(); i++) {
                float pointDistance = currentExplorationPoint.dst(explorationPoints.get(i));
                if (currentExplorationPoint != explorationPoints.get(i) && pointDistance < minPointDistance) {
                    minPointDistance = pointDistance;
                    closestPoints.clear();
                    closestPoints.add(explorationPoints.get(i));
                } else if (currentExplorationPoint != explorationPoints.get(i) && pointDistance == minPointDistance) {
                    closestPoints.add(explorationPoints.get(i));
                }
            }

        /**
         * When the guard runs out of exploration points, refill the list
         */
        if(closestPoints.size() == 0){
                explorationSetUpGuards();
                return currentExplorationPoint;
            }


            //check where each of the closest points are with respect to the the currentExplorationPoint
            //if any have the same relativeDirection as the currentDirection, return that point
            //otherwise return closestPoints.get(0), and save the direction we are now going in
            Direction relativeDirection = currentDirection;
            for (int j = 0; j < closestPoints.size(); j++) {
                float pointX = closestPoints.get(j).x;
                float pointY = closestPoints.get(j).y;

                if (pointX == currentExplorationPoint.x && pointY > currentExplorationPoint.y)
                    relativeDirection = Direction.NORTH;
                if (pointX > currentExplorationPoint.x && pointY > currentExplorationPoint.y)
                    relativeDirection = Direction.NORTH_EAST;
                if (pointX > currentExplorationPoint.x && pointY == currentExplorationPoint.y)
                    relativeDirection = Direction.EAST;
                if (pointX > currentExplorationPoint.x && pointY < currentExplorationPoint.y)
                    relativeDirection = Direction.SOUTH_EAST;
                if (pointX == currentExplorationPoint.x && pointY < currentExplorationPoint.y)
                    relativeDirection = Direction.SOUTH;
                if (pointX < currentExplorationPoint.x && pointY < currentExplorationPoint.y)
                    relativeDirection = Direction.SOUTH_WEST;
                if (pointX < currentExplorationPoint.x && pointY == currentExplorationPoint.y)
                    relativeDirection = Direction.WEST;
                if (pointX < currentExplorationPoint.x && pointY > currentExplorationPoint.y)
                    relativeDirection = Direction.NORTH_WEST;

                if (relativeDirection == currentDirection) {
                    currentExplorationPoint = closestPoints.get(j);
                    explorationPoints.remove(currentExplorationPoint);
//                    System.out.println("to go to point: " + currentExplorationPoint);
                    return currentExplorationPoint;
                }
            }

            if (closestPoints.get(0).x == currentExplorationPoint.x && closestPoints.get(0).y > currentExplorationPoint.y)
                relativeDirection = Direction.NORTH;
            if (closestPoints.get(0).x > currentExplorationPoint.x && closestPoints.get(0).y > currentExplorationPoint.y)
                relativeDirection = Direction.NORTH_EAST;
            if (closestPoints.get(0).x > currentExplorationPoint.x && closestPoints.get(0).y == currentExplorationPoint.y)
                relativeDirection = Direction.EAST;
            if (closestPoints.get(0).x > currentExplorationPoint.x && closestPoints.get(0).y < currentExplorationPoint.y)
                relativeDirection = Direction.SOUTH_EAST;
            if (closestPoints.get(0).x == currentExplorationPoint.x && closestPoints.get(0).y < currentExplorationPoint.y)
                relativeDirection = Direction.SOUTH;
            if (closestPoints.get(0).x < currentExplorationPoint.x && closestPoints.get(0).y < currentExplorationPoint.y)
                relativeDirection = Direction.SOUTH_WEST;
            if (closestPoints.get(0).x < currentExplorationPoint.x && closestPoints.get(0).y == currentExplorationPoint.y)
                relativeDirection = Direction.WEST;
            if (closestPoints.get(0).x < currentExplorationPoint.x && closestPoints.get(0).y > currentExplorationPoint.y)
                relativeDirection = Direction.NORTH_WEST;
            currentDirection = relativeDirection;
            previousExplorationPoint = currentExplorationPoint;
            currentExplorationPoint = closestPoints.get(0);

            explorationPoints.remove(currentExplorationPoint);
//            System.out.println("to go to point: " + currentExplorationPoint);
            return currentExplorationPoint;
    }

    public boolean checkCollision(){

        return false;
    }

    @Override
    public float getRotation() {
        if (rotation.empty()) {
            exploration();
        } else {
            //System.out.print("  and rotation: "+rotation.peek());
            return rotation.pop();
        }
        return 0f;
    }

    @Override
    public float getSpeed() {
        if (speed.empty()) {
            exploration();
        } else {
            //System.out.println("  getting instruction to move with speed: "+speed.peek());
            return speed.pop();
        }
        return 0f;
    }

    @Override
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void setStructures(ArrayList<Area> structures) {
        this.structures = structures;
    }

    @Override
    public void setArea(float areaWidth, float areaHeight) {

    }

    @Override
    public void reset() {
        for(int i = 0; i < rotation.size(); i++){
            rotation.pop();
        }

        for(int i = 0; i < speed.size(); i++){
            speed.pop();
        }
    }

    @Override
    public void seeArea(Area area) {
//        System.out.println("printing some area ");

      //  if(!(area instanceof OuterWall)) {
            boolean check = false;
            //checking to see if area is in seen structures, if not it is added to the array
            if (seenStructures.size() > 0) {
                for (int i = 0; i < seenStructures.size(); i++) {
                    if (area == seenStructures.get(i)) {
                        check = true;
                    }
                }
                if (!check) {
                 //   System.out.println("point x = " + point.x + " " + "point y = " + point.y);
                 //   System.out.println("area start x = " + area.xPos + " " + "area start y " + area.yPos);
                    for(int i = 0; i < explorationPoints.size(); i++){
                            if(area.area.contains(explorationPoints.get(i))){
                              //  System.out.println("exploration x = " + explorationPoints.get(i).x + " " + "exploration y = " + explorationPoints.get(i).y);
                                explorationPoints.remove(i);
                            }

                    }

                    if(!(area instanceof LowVisionArea)){
                        seenStructures.add(area);
                        Area rectangle = new Area(area.xPos - agent.width / 2, area.yPos - agent.height / 2, area.area.width + agent.width, area.area.height + agent.height);
                        Rectangle2D.Float rect = new Rectangle2D.Float(area.xPos - agent.width / 2, area.yPos - agent.height / 2, area.area.width + agent.width, area.area.height + agent.height);
                        exploredStructures.add(rectangle);
                        astarStructures.add(rect);
                    }

                   // reset();
                }
                reset();
            } else {

               // System.out.println("point x = " + point.x + " " + "point y = " + point.y);
             //   System.out.println("area start x = " + area.xPos + " " + "area start y " + area.yPos);

                for(int i = 0; i < explorationPoints.size(); i++) {
                    if (area.area.contains(explorationPoints.get(i))) {
                     //   System.out.println("exploration x = " + explorationPoints.get(i).x + " " + "exploration y = " + explorationPoints.get(i).y);

                        explorationPoints.remove(i);
                    }
                }

                if(!(area instanceof LowVisionArea)) {
                    seenStructures.add(area);
                    Area rectangle = new Area(area.xPos - agent.width / 2, area.yPos - agent.height / 2, area.area.width + agent.width, area.area.height + agent.height);
                    Rectangle2D.Float rect = new Rectangle2D.Float(area.xPos - agent.width / 2, area.yPos - agent.height / 2, area.area.width + agent.width, area.area.height + agent.height);
                    exploredStructures.add(rectangle);
                    // seenStructures.add(rectangle);
                    astarStructures.add(rect);
                    // reset();
                }
            }
      //  }

    }

    @Override
    public void seeAgent(Agent agent) {
            guardSeen = true;
    }

    @Override
    public void updatedSeenLocations(){

      //  System.out.println("before update = " + explorationPoints.size());

        for(int i = 0; i < explorationPoints.size(); i++){
            if(agent.area.contains(explorationPoints.get(i))){
                explorationPoints.remove(i);
            }
        }

       // System.out.println("after update = " + explorationPoints.size());
      //  System.out.println("Agent locaton x = " + agent.area.x + " " + "y loocation = " + agent.area.y);

    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setCurrentExplorationPoint(Vector2 currentExplorationPoint) {
        this.currentExplorationPoint = currentExplorationPoint;
    }

    @Override
    public void setCornerPoints(ArrayList<Point2D.Float> cornerPoints) {
        this.cornerPoints = cornerPoints;
        if(agent instanceof Guard){
            explorationSetUpGuards();
        }
    }

}