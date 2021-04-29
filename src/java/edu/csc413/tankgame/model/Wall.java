package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class Wall extends Entity {
    String imagefile = "";
    int health = 0;
    public Wall(String id, double x, double y, String imagefile) {
        super(id, x, y, 0);
        this.imagefile = imagefile;
        health = 2;
    }

    public void move(GameWorld gameWorld) {

    }

    public String getImagefile() {
        return imagefile;
    }

    public double getXBound() {
        return x + Constants.WALL_WIDTH;
    }

    public double getYBound() {
        return y + Constants.WALL_HEIGHT;
    }

    public void decreaseHealth() {
        health--;
        if( health < 0 )
            health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
