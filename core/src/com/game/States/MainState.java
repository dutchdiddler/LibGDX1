package com.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.game.AI.AI;
import com.game.AI.Astar.Astar;
import com.game.AI.Controller;
import com.game.AI.GuardPatrolling;
import com.game.Board.Guard;
import com.game.Board.Intruder;
import com.game.CopsAndRobbers;
import com.game.Board.Agent;
import com.game.Board.Area;
import com.game.Board.Board;
import com.game.Board.Structure;
import com.game.Objects.Ground;
import com.game.Objects.Play;
import com.game.Readers.FileHandler;
import com.game.Readers.SpriteReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainState extends State {

    public ArrayList<Area> structures;
    public ArrayList<Agent> agents;
    public ArrayList<Structure> walls;
    public Texture wall;
    public Play play;
    public String name;
    public BitmapFont font;
    public TextureRegion tR;
    public SpriteReader reader;
    public Board board;
    public Ground ground;
    public GuardPatrolling guardPatrol;
    public static final float X_REDUC = MapState.X_REDUC;
    public static final float Y_REDUC = MapState.Y_REDUC;
    public AI guardAI;
    public AI intruderAI;


    public MainState(GameStateManager gsm, ArrayList<Area> structures, ArrayList<Agent> agents, ArrayList<Structure> walls, AI guardAI, AI intruderAI) {
        super(gsm);
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        wall = new Texture("wall.png");
        play = new Play(865,545);
        ground = new Ground(0,0);
        this.guardAI = guardAI;
        this.intruderAI = intruderAI;
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

        for(int i = 0; i < this.agents.size(); i++){
            if(this.agents.get(i) instanceof Guard){
                AI agentAI = new GuardPatrolling();
               this.agents.get(i).setAI(agentAI);
               agentAI.setAgent(this.agents.get(i));
                System.out.println("Cops's ai is:" + this.agents.get(i).ai);
                this.agents.get(i).ai.setArea(20,20);
               this.agents.get(i).ai.setStructures(structures);
            }
            else{
                AI agentAi = new Astar();
                this.agents.get(i).setAI(agentAi);
                System.out.println("Intruders's ai is:" + agentAi);
                //agentAi.setAgent(agents.get(i));
                System.out.println("Intruders's ai is:" + this.agents.get(i).ai);
                this.agents.get(i).ai.setArea(20,20);
                this.agents.get(i).ai.setStructures(structures);
            }
        }
        board = new Board();
        if(!this.structures.isEmpty()) {board.setUp(this.structures);}
        if(!this.agents.isEmpty()) {board.putInAgents(this.agents);}
    }


    @Override
    public void handleInput() {

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (Exception e) {
                System.out.println("Error");
            }
            if (Gdx.input.getX() > 850 && Gdx.input.getY() < 835) {
                gsm.pop();
            }
            int x = (int) Math.floor(Gdx.input.getX());
            int y = (int) Math.floor((CopsAndRobbers.HEIGHT - Gdx.input.getY()));

        }
        board.updateAgents();
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