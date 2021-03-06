package com.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.game.Readers.SpriteReader;

import java.io.IOException;

public class Cop extends GameObject {
    public Cop(int xPos, int yPos) {
        super(xPos, yPos);
        SpriteReader reader = new SpriteReader();
        this.width = 30;
        this.height = 30;
        bounds = new Rectangle(xPos,yPos,width,height);

        try {
            this.texture = reader.getImage(65,255,30,33);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
