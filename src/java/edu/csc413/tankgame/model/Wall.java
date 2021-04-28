package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class Wall extends Entity {
    String imagefile = "";
    public Wall(String id, double x, double y, String imagefile) {
        super(id, x, y, 0);
        this.imagefile = imagefile;
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
}
