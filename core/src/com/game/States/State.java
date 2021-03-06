package com.game.States;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.game.AI.AI;
import com.game.Board.Structure;
import com.game.Objects.GameObject;

import java.util.ArrayList;

public abstract class State {

    public OrthographicCamera cam;
    public Vector3 mouse;
    public GameStateManager gsm;

    public State(GameStateManager gsm){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector3();
    }

    public State(GameStateManager gsm, AI guardAI, AI intruderAI){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector3();
    }
    public State(GameStateManager gsm, ArrayList<GameObject> gameObjects, ArrayList<Structure> walls, AI guardAI, AI agentAI){
        this.gsm = gsm;
        cam = new OrthographicCamera();
        mouse = new Vector3();
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(SpriteBatch sb);
}
