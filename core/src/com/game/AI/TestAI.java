package com.game.AI;

import com.game.Objects.Cop;
import com.game.Objects.GameObject;

import java.util.ArrayList;

public class TestAI extends AI {

    public TestAI(){

    }
    @Override
    public void move(ArrayList<GameObject> object) {
        for(int i = 0; i < object.size(); i++) {
            if (object.get(i).xPos > 1000) {
                object.get(i).xPos = 0;
            }

            object.get(i).setX(1);
        }

    }
}
