package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class PowerUp extends Entity {
    public PowerUp(String id, double x, double y) {
        super(id, x, y, 0);
    }

    @Override
    public void move(GameWorld gameWorld) {

    }

    public double getXBound() {
        return x + Constants.POWERUP_WIDTH;
    }

    public double getYBound() {
        return y + Constants.POWERUP_HEIGHT;
    }

}
