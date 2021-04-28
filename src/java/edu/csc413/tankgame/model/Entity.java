package edu.csc413.tankgame.model;

/**
 * A general concept for an entity in the Tank Game. This includes everything that can move or be interacted with, such
 * as tanks, shells, walls, power ups, etc.
 */
public abstract class Entity {
    protected String id;
    protected double x;
    protected double y;
    protected double angle;

    public Entity(String id, double x, double y, double angle) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    /** All entities can move, even if the details of their move logic may vary based on the specific type of Entity. */
    public abstract void move(GameWorld gameWorld);

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

}
