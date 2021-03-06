package com.game.Board;

import java.util.ArrayList;

public class SoundOccurence {

    /**
     * @author Yvar Hulshof
     *
     */

    int soundRange;
    private long time;
    float xpos;
    float ypos;
    public String name = "soundoccurence";

    public SoundOccurence(long time, float xPos, float yPos, int range) {
        this.time = time;
        this.xpos = xPos;
        this.ypos = yPos;
        this.soundRange = range;
    }

    public SoundOccurence(long time, float xpos, float ypos) {
        this.time = time;
        this.xpos = xpos;
        this.ypos = ypos;
        soundRange = 10;
    }

    public long getTime() {
        return time;
    }
}

