package com.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.game.AI.AI;
import com.game.AI.Astar.AStarNew;
import com.game.AI.Astar.Astar;
import com.game.AI.HeuristicAI;
import com.game.AI.IntruderBasicMovement;
import com.game.AI.CopsCenters;
import com.game.AI.GuardCirclePatrolling;
import com.game.AI.GuardPatrolling;
import com.game.AI.IntruderBasicMovement;
import com.game.Board.Guard;
import com.game.Board.Intruder;
import com.game.Board.RandomMapGenerator;
import com.game.CopsAndRobbers;
import com.game.Board.Agent;
import com.game.Board.Area;
import com.game.Board.Board;
import com.game.Board.Structure;
import com.game.Objects.Ground;
import com.game.Objects.Play;
import com.game.Readers.SpriteReader;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainState extends State {

    public ArrayList<Area> structures;
    public ArrayList<Agent> agents;
    public ArrayList<Agent> guards;
    public ArrayList<Agent> intruders;
    public ArrayList<Structure> walls;

    private BitmapFont font;
    private float deltaTime = 0;
    CharSequence str;

    public Texture background;
    public Texture wall;
    public Play play;
    public String name;
    public TextureRegion tR;
    public SpriteReader reader;
    public Board board;
    public Ground ground;
    public GuardPatrolling guardPatrol;
    public static final float X_REDUC = MapState.X_REDUC;
    public static final float Y_REDUC = MapState.Y_REDUC;
    public double timeLimit = 60.00;
    String intruderAI;
    String guardAI;
    public long startTime;


    public MainState(GameStateManager gsm, ArrayList<Area> structures, ArrayList<Agent> agents, ArrayList<Structure> walls, String guardAI, String intruderAI) {
        super(gsm);
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        this.intruderAI = intruderAI;
        this.guardAI = guardAI;
        wall = new Texture("wall.png");
        play = new Play(865,545);
        ground = new Ground(0,0);
        reader = new SpriteReader();
        try {
            tR = reader.getImage(100,100,25,25);
        } catch (IOException e) {
            e.printStackTrace();
        }
      //separate the agents and structures
        this.structures = structures;
        this.agents = agents;
        this.walls = walls;


        for(int i = 0; i < this.walls.size(); i++){
            this.structures.add(this.walls.get(i));
        }

        guards = new ArrayList<Agent>();
        intruders = new ArrayList<Agent>();

        for(int i = 0; i < this.agents.size(); i++){
            if(this.agents.get(i) instanceof Guard){
            	if(guardAI == "Patrolling") {
	                AI agentAI = new GuardPatrolling();
	                this.agents.get(i).setAI(agentAI);
	                agentAI.setAgent(this.agents.get(i));
	                this.agents.get(i).ai.setArea(400,200);
	                this.agents.get(i).ai.setStructures(structures);
                    ((Guard) this.agents.get(i)).setSeenStructures(structures);
	                guards.add(this.agents.get(i));
            	} else if(guardAI == "Circle patrolling") {
	                AI agentAI = new GuardCirclePatrolling();
	                this.agents.get(i).setAI(agentAI);
	                agentAI.setAgent(this.agents.get(i));
	                this.agents.get(i).ai.setArea(400,200);
	                this.agents.get(i).ai.setStructures(structures);
//                    ((Guard) this.agents.get(i)).setSeenStructures(structures);
	                guards.add(this.agents.get(i));
            	} else if(guardAI == "Heatmap patrolling") {
                    AI agentAI = new HeuristicAI();
                    this.agents.get(i).setAI(agentAI);
                    ((HeuristicAI) agentAI).setPattern("heatmap");
                    this.agents.get(i).ai.setArea(400,200);
                    agentAI.setAgent(this.agents.get(i));
                    this.agents.get(i).ai.setStructures(structures);
//                    ((Guard) this.agents.get(i)).setSeenStructures(structures);
                    guards.add(agents.get(i));
                } else if(guardAI == "Random patrolling") {
            	    AI agentAI = new HeuristicAI();
            	    this.agents.get(i).setAI(agentAI);((HeuristicAI) agentAI).setPattern("random");
                    this.agents.get(i).ai.setArea(400,200);
//                    ((Guard) this.agents.get(i)).setSeenStructures(structures);
                    agentAI.setAgent(this.agents.get(i));
                    guards.add(agents.get(i));
            	} else {
            		System.out.println("Unrecognised AI name: "+guardAI);
            		System.exit(0);
            	}
            } else {
            	if(intruderAI == "Basic") {
	                AI agentAI = new IntruderBasicMovement();
	                this.agents.get(i).setAI(agentAI);
	                agentAI.setAgent(this.agents.get(i));
	                this.agents.get(i).ai.setArea(400,200);
	                this.agents.get(i).ai.setStructures(structures);
	                intruders.add(agents.get(i));
            	} else if(intruderAI == "Heuristic Closest AI") {
	                AI agentAI = new HeuristicAI();
                    this.agents.get(i).setAI(agentAI);
                    ((HeuristicAI) agentAI).setPattern("closest");
	                agentAI.setAgent(this.agents.get(i));
                    intruders.add(agents.get(i));
	                this.agents.get(i).ai.setArea(400,200);
	                this.agents.get(i).ai.setStructures(structures);
                } else if(intruderAI == "Heuristic Random AI") {
                    AI agentAI = new HeuristicAI();
                    this.agents.get(i).setAI(agentAI);
                    ((HeuristicAI) agentAI).setPattern("random");
                    agentAI.setAgent(this.agents.get(i));
                    intruders.add(agents.get(i));
                    this.agents.get(i).ai.setArea(400,200);
                    this.agents.get(i).ai.setStructures(structures);
            	} else {
            		System.out.println("Unrecognised AI name: "+intruderAI);
            		System.exit(0);
            	}
            }

            /**
             * Giving each guard the arraylist of guards so that
             * they are accesible for communication
             */
            for(int j = 0; j < guards.size(); j++){
                guards.get(j).setAgentList(guards);
            }
        }

        board = new Board();
        if(!this.structures.isEmpty()) {board.setUp(this.structures);}
        if(!this.agents.isEmpty()) {board.putInAgents(this.agents);}
        //guardPatrol = new GuardPatrolling(board)
        //Controller controller = new Controller(board);
        CopsCenters copsCenters = new CopsCenters(guards);

        Point2D.Float[] guardCenters = copsCenters.getCenters();
        ArrayList<ArrayList<Point2D.Float>> areas = copsCenters.getAreas(guardCenters);

        for(int i = 0; i < guards.size(); i++){
            guards.get(i).setCenterLocation(guardCenters[i]);
            if(guards.get(i).ai instanceof GuardCirclePatrolling){
                //TODO (add increment away from wall so that guards dont get stuck)
                guards.get(i).ai.setCornerPoints(areas.get(i));
            }
            if(guards.get(i).ai instanceof HeuristicAI){
                guards.get(i).ai.setCornerPoints(areas.get(i));
                ((HeuristicAI) guards.get(i).ai).moveGuardToCenter(new Vector2(guards.get(i).getCenterLocation().x,guards.get(i).getCenterLocation().y));
            }
            //TODO clean up AI-specific things like this from main state
        }
        

//        int guardCounter = 0;
//        for(int i = 0; i < this.agents.size(); i++){
//            if(agents.get(i) instanceof Guard){
//                guards.get(guardCounter).setCenterLocation(guardCenters[guardCounter]);
//                guardCounter++;
//            }
//        }
        //start the timer
        startTime = System.currentTimeMillis();
    }


    @Override
    public void handleInput() {

    	if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (Exception e) {
                System.out.println("Error");
            }
            if (Gdx.input.getX() > 860 && Gdx.input.getY() < 100) {
                gsm.pop();
            }
            int x = (int) Math.floor(Gdx.input.getX());
            int y = (int) Math.floor((CopsAndRobbers.HEIGHT - Gdx.input.getY()));

        }
    	if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (Exception e) {
                System.out.println("Error");
            }
            boolean place = false;
            float x = 0;
            float y = 0;
            float angle = 0;
            if (Gdx.input.getX() > 20 && Gdx.input.getX() < CopsAndRobbers.WIDTH-20 && Gdx.input.getY() < 150 && Gdx.input.getY() > 130) {
            	place = true;
	            x = (float) Math.floor(Gdx.input.getX());
	            y = (float) Math.floor((CopsAndRobbers.HEIGHT - 171));
	            angle = 270;
            } else if (Gdx.input.getX() > 20 && Gdx.input.getX() < CopsAndRobbers.WIDTH-20 && Gdx.input.getY() > CopsAndRobbers.HEIGHT-20 && Gdx.input.getY() < CopsAndRobbers.HEIGHT) {
            	place = true;
	            x = (float) Math.floor(Gdx.input.getX());
	            y = (float) Math.floor(21);
	            angle = 90;
            } else if (Gdx.input.getX() > 0 && Gdx.input.getX() < 20 && Gdx.input.getY() < (CopsAndRobbers.HEIGHT - 20) && Gdx.input.getY() > 150) {
            	place = true;
	            x = (float) Math.floor(21);
	            y = (float) Math.floor(CopsAndRobbers.HEIGHT-Gdx.input.getY());
	            angle = 0;
            } else if (Gdx.input.getX() > CopsAndRobbers.WIDTH-20 && Gdx.input.getX() < CopsAndRobbers.WIDTH && Gdx.input.getY() < (CopsAndRobbers.HEIGHT - 20) && Gdx.input.getY() > 150) {
            	place = true;
	            x = (float) Math.floor(CopsAndRobbers.WIDTH-36);
	            y = (float) Math.floor(CopsAndRobbers.HEIGHT-Gdx.input.getY());
	            angle = 180;
            }
            Intruder incoming = new Intruder(x / X_REDUC, y / Y_REDUC, 15 / X_REDUC, 15 / Y_REDUC);
            if(!checkOverlap(incoming.area)) {
                System.out.println("Putting agent at x: "+x+"  y: "+y);
                if(intruderAI == "Basic") {
	                AI agentAI = new IntruderBasicMovement();
	                incoming.setAI(agentAI);
	                agentAI.setAgent(incoming);
	                incoming.ai.setArea(400,200);
	                incoming.ai.setStructures(structures);
	                intruders.add(incoming);
            	} else if(intruderAI == "Heuristic Closest AI") {
	                AI agentAI = new HeuristicAI();
                    incoming.setAI(agentAI);
                    ((HeuristicAI) agentAI).setPattern("closest");
	                agentAI.setAgent(incoming);
                    intruders.add(incoming);
	                incoming.ai.setArea(400,200);
	                incoming.ai.setStructures(structures);
                } else if(intruderAI == "Heuristic Random AI") {
                    AI agentAI = new HeuristicAI();
                    incoming.setAI(agentAI);
                    ((HeuristicAI) agentAI).setPattern("random");
                    agentAI.setAgent(incoming);
                    intruders.add(incoming);
                    incoming.ai.setArea(400,200);
                    incoming.ai.setStructures(structures);
            	} else {
            		System.out.println("Unrecognised AI name: "+intruderAI);
            		System.exit(0);
            	}
            	incoming.angle = angle;
            	incoming.viewAngle.setAngle(angle);
            	incoming.soundRange = 5;
            	agents.add(incoming);
            	ArrayList<Agent> ar = new ArrayList<Agent>();
            	ar.add(incoming);
            	board.putInAgents(ar);
            }
        }
        if (board.gameOver) {
        	if(board.timeOfTracking != 0) {
        		try {
            		gsm.push(new GameOverState(gsm, deltaTime, board.timeOfTracking, board.predictiveTracking));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        	} else {
        		gsm.push(new GameOverState(gsm, deltaTime));
        	}
        }

        //if(deltaTime > timeLimit){gsm.push(new GameOverState(gsm,deltaTime));}
        board.updateAgents();

        for(int i = 0; i < intruders.size(); i++){
                intruders.get(i).ai.updatedSeenLocations();
        }

        for(int i = 0; i < guards.size(); i++){
                guards.get(i).ai.updatedSeenLocations();
            }

        
    }
    
    public boolean checkOverlap(Rectangle rec) {
    	boolean overlap = false;
    	for(int a=0; a<11 ;a++) {
        	for(int b=0; b<11 ;b++) {
        		float x = rec.x + rec.width/10*a;
        		float y = rec.y + rec.height/10*b;
        		for(int i = 0; i < structures.size(); i++) {
					if(structures.get(i).area.contains(x,y)) {
						return true;
					}
				}
				    	
        		for(int i = 0; i < walls.size(); i++) {
					if(walls.get(i).area.contains(x,y)) {
						return true;
					}
				}
				    	
				for(int j = 0; j < agents.size(); j++) {
					if(agents.get(j).area.contains(x,y)) {
						return true;
					}
				}
        	}
    	}
    	return overlap;
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
    	
        sb.begin();
        sb.draw(ground.texture,ground.xPos,ground.yPos, CopsAndRobbers.WIDTH,CopsAndRobbers.HEIGHT);
        sb.draw(play.texture, play.xPos, play.yPos, 100, 100);
        sb.draw(wall, 0, 500, 1000, 20);
        sb.draw(wall, 820, 520, 20, 180);

        //Draws the time onto the screen
        deltaTime = (float) (System.currentTimeMillis()-startTime)/1000;
        str = Float.toString(deltaTime);
        font.draw(sb, str, 100, 600);
        font.draw(sb, "TIME", 50, 600);

        //Draws all structures and agents
        for(int i =0; i < structures.size(); i++ ){
            structures.get(i).drawTexture(sb,MapState.X_REDUC,MapState.Y_REDUC);
        }
        
        for(int i =0; i < agents.size(); i++ ){
            agents.get(i).drawTexture(sb,MapState.X_REDUC,MapState.Y_REDUC);
        }

        sb.end();

    }


    public void dispose() {
        font.dispose();
    }
}