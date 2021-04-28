package edu.csc413.tankgame.model;

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
}
