package com.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.game.AI.TestAI;
import com.game.AI.TestAI2;
import com.game.Board.Agent;
import com.game.Board.Area;
import com.game.Board.Guard;
import com.game.Board.Intruder;
import com.game.Board.SentryTower;
import com.game.Board.Structure;
import com.game.Board.TargetArea;
import com.game.Board.LowVisionArea;
import com.game.Board.OuterWall;
import com.game.CopsAndRobbers;
import com.game.GameLogic.Collider;
import com.game.Objects.Candle;
import com.game.Objects.Cop;
import com.game.Objects.Door;
import com.game.Objects.GameObject;
import com.game.Objects.Ground;
import com.game.Objects.LookOut;
import com.game.Objects.Play;
import com.game.Objects.Robber;
import com.game.Objects.Steps;
import com.game.Objects.Target;
import com.game.Objects.VDoor;
import com.game.Objects.Web;
import com.game.Objects.hWall;
import com.game.Objects.vWall;
import com.game.Readers.FileHandler;
import com.game.Readers.SpriteReader;

import org.w3c.dom.css.Rect;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MapState extends State {

    public ArrayList<GameObject> menuObjects;
    public ArrayList<GameObject> activeObjects;
    public ArrayList<GameObject> copObjects;
    public ArrayList<GameObject> robberObjects;
    public ArrayList<Agent> agents;
    public ArrayList<Area> structures;
    public ArrayList<Structure> walls;
    public String name;
    public boolean vertical;
    public boolean delete;
    public BitmapFont font;
    public SpriteReader reader;
    public Play play;
    public vWall vWall;
    public hWall hWall;
    public Ground ground;;
    public Collider collider;
    public static final int X_REDUC = 1;
    public static final int Y_REDUC = 1;
    
    
    public MapState(GameStateManager gsm){
        super(gsm);
        menuObjects = new ArrayList<GameObject>();
        activeObjects = new ArrayList<GameObject>();
        copObjects = new ArrayList<GameObject>();
        robberObjects = new ArrayList<GameObject>();
        agents = new ArrayList<Agent>();
        structures = new ArrayList<Area>();
        walls = new ArrayList<Structure>();
        reader = new SpriteReader();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        collider = new Collider();

         play = new Play(865,545);
         vWall = new vWall(820,520);
         hWall = new hWall(0,500);
         ground = new Ground(0,0);



        menuObjects.add(new Robber(5,575));
        menuObjects.add(new Cop(85,575));
        menuObjects.add(new Steps(165,575));
        menuObjects.add(new Candle(245,575));
        menuObjects.add(new hWall(325,575));
        menuObjects.add(new VDoor(405,575));
        menuObjects.add(new Door(485,575));
        menuObjects.add(new Target(565,575));
        menuObjects.add(new LookOut(645,575));
        menuObjects.add(new Web(725,575));

        
        for(int i=0; i<50; i++) {
        	structures.add(new OuterWall(i*20/Y_REDUC,0,20/X_REDUC,20/Y_REDUC));
        	structures.add(new OuterWall(i*20/Y_REDUC,500-20/X_REDUC,20/X_REDUC,20/Y_REDUC));

        }
        
        for(int i=1; i<25; i++) {
        	structures.add(new OuterWall(0,i*20/X_REDUC,20/X_REDUC,20/Y_REDUC));
        	structures.add(new OuterWall(1000-20/Y_REDUC,i*20/X_REDUC,20/X_REDUC,20/Y_REDUC));
        }
        

    }
    @Override
    public void handleInput() {

        if(Gdx.input.isKeyPressed(Input.Keys.V)) {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (Exception e) {
                System.out.println("Error");
            }
            if(vertical == true){
                vertical = false;
            }
            else{
                vertical  = true;
            }

        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (Exception e) {
                System.out.println("Error");
            }
            if(delete == true){
                delete = false;
            }
            else{
                delete  = true;
            }

        }

        if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            this.name = "robber";
         ////   System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            this.name = "cop";
           // System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            this.name = "steps";
           // System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            this.name = "candle";
           // System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
            this.name = "wall";
         //   System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
            this.name = "Vdoor";
          //  System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_7)) {
            this.name = "door";
          //  System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_8)) {
            this.name = "target";
            System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_9)) {
            this.name = "lookout";
          //  System.out.println("Made it");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
            this.name = "web";
            //  System.out.println("Made it");
        }



        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (Exception e) {
                System.out.println("Error");
            }

            if(Gdx.input.getX() > 860 && Gdx.input.getY() < 100){
                dispose();
                FileHandler fileHandler = new FileHandler();
                try {
                    fileHandler.fileWriter(agents,structures,walls);
                    fileHandler.fileReader(1);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                gsm.push(new MainState(gsm,structures,agents,walls));
            }

            if (Gdx.input.getY() >= 150) {
            float x = (float) Math.floor(Gdx.input.getX()/X_REDUC);
            float y = (float) Math.floor((CopsAndRobbers.HEIGHT - Gdx.input.getY())/Y_REDUC);
            Rectangle area = new Rectangle(x,y,10,10);
            Boolean overlaps = false;

                if(delete == true) {
                    for (int i = 0; i < structures.size(); i++) {
                        if (structures.get(i).area.overlaps(area)) {
                            structures.remove(i);
                            overlaps = true;

                        }
                    }
                    for (int i = 0; i < agents.size(); i++) {
                        if (agents.get(i).area.overlaps(area)) {
                            agents.remove(i);
                            overlaps = true;
                        }
                    }

                    for (int i = 0; i < walls.size(); i++) {
                        if (walls.get(i).area.overlaps(area)) {
                            walls.remove(i);
                            overlaps = true;
                        }
                    }
                }

                if(!overlaps) {

                    if (this.name == "target") {
                        structures.add(new TargetArea(x, y, 20 / X_REDUC, 20 / Y_REDUC));
                    }

                    if (name == "steps") {
                        activeObjects.add(new Steps((int) x, (int) y));
                    }
                    if (name == "wall") {
                        if (vertical == true) {
                            walls.add(new Structure(x, y, 20 / X_REDUC, 100 / Y_REDUC, false));
                         //   structures.add(walls.get(walls.size() - 1));

                        } else {
                            walls.add(new Structure(x, y, 100 / X_REDUC, 20 / Y_REDUC, true));
                         //   structures.add(walls.get(walls.size() - 1));
                        }
                    }
                    if (name == "robber") {
                        agents.add(new Intruder(x, y, 20 / X_REDUC, 20 / Y_REDUC));
                    }
                    if (name == "candle") {
                        structures.add(new LowVisionArea(x, y, 20 / X_REDUC, 40 / Y_REDUC));
                    }
                    if (name == "lookout") {
                        structures.add(new SentryTower(x, y, 50 / X_REDUC, 50 / Y_REDUC));
                    }
                    if (name == "Vdoor") {
                        for (int i = 0; i < walls.size(); i++) {
                            if (walls.get(i).contains(x, y)) {
                                walls.get(i).placeDoor(x, y);
                            }
                        }
                    }
                    if (name == "door") {
                        for (int i = 0; i < walls.size(); i++) {
                            if (walls.get(i).contains(x, y)) {
                                walls.get(i).placeDoor(x, y);
                            }
                        }
                    }
                    if (name == "cop") {
                        agents.add(new Guard(x, y, 20 / X_REDUC, 20 / Y_REDUC));
                    }
                    if (name == "web") {
                        structures.add(new OuterWall(x, y, 20 / X_REDUC, 20 / Y_REDUC));
                    }
                }


            }
        }


        robberObjects = collider.copVsRobber(robberObjects, copObjects);
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        int count = 1;
        sb.begin();
        sb.draw(ground.texture,ground.xPos,ground.yPos, CopsAndRobbers.WIDTH,CopsAndRobbers.HEIGHT);
        sb.draw(play.texture,play.xPos,play.yPos, 100,100);
        sb.draw(hWall.texture,hWall.xPos,hWall.yPos, 1000,20);
        sb.draw(vWall.texture,vWall.xPos,vWall.yPos, 20,180);

        for(int i =0; i < menuObjects.size(); i++ ){
            sb.draw(menuObjects.get(i).texture, menuObjects.get(i).xPos,menuObjects.get(i).yPos,75,75);
            font.draw(sb,String.valueOf(count), menuObjects.get(i).xPos+30,menuObjects.get(i).yPos-10);
            count++;

        }

        for(int i =0; i < activeObjects.size(); i++ ){
            sb.draw(activeObjects.get(i).texture, activeObjects.get(i).xPos*X_REDUC,activeObjects.get(i).yPos*Y_REDUC,activeObjects.get(i).width,activeObjects.get(i).height);
        }
        
        for(int i =0; i < agents.size(); i++ ){
            agents.get(i).drawTexture(sb,X_REDUC,Y_REDUC);
        }
        
        for(int i =0; i < structures.size(); i++ ){
            structures.get(i).drawTexture(sb,X_REDUC,Y_REDUC);
        }
        for(int i =0; i < walls.size(); i++ ){
            walls.get(i).drawTexture(sb,X_REDUC,Y_REDUC);
        }


        sb.end();


    }

    public void dispose(){
        font.dispose();

        for(int i = 0; i < menuObjects.size(); i++){
           menuObjects.get(i).dispose();
        }
        for(int i = 0; i < activeObjects.size(); i++){
            activeObjects.get(i).dispose();
        }
        for(int i = 0; i < copObjects.size(); i++){
            copObjects.get(i).dispose();
        }
        for(int i = 0; i < robberObjects.size(); i++){
            robberObjects.get(i).dispose();
        }
        /*
        for(int i = 0; i < structures.size(); i++){
            structures.get(i).dispose();
        }
        for(int i = 0; i < agents.size(); i++){
            agents.get(i).dispose();
        }*/
    }
}
