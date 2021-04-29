package edu.csc413.tankgame.collision;

import edu.csc413.tankgame.model.Entity;
import edu.csc413.tankgame.model.Shell;
import edu.csc413.tankgame.model.Tank;
import edu.csc413.tankgame.model.Wall;

import java.util.List;

public class TankWallCollisionHandler implements CollisionHandler  {
    @Override
    public void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList) {
        Tank tank = null;
        Wall wall = null;

        if( entity1 instanceof Tank )
            tank = (Tank) entity1;
        if( entity2 instanceof Tank )
            tank = (Tank) entity2;

        if( entity1 instanceof Wall )
            wall = (Wall) entity1;
        if( entity2 instanceof Wall )
            wall = (Wall) entity2;

        double distance[] = new double[4];
        distance[0] = wall.getXBound() - tank.getX();
        distance[1] = tank.getXBound() - wall.getX();
        distance[2] = wall.getYBound() - tank.getY();
        distance[3] = tank.getYBound() - wall.getY();

        double min_distance = 100000;
        int index = -1;
        for (int i = 0; i < 4; i++) {
            if (distance[i] < min_distance) {
                min_distance = distance[i];
                index = i;
            }
        }

        switch (index) {
            case 0:
                tank.setX(tank.getX() + min_distance);
                break;
            case 1:
                tank.setX(tank.getX() - min_distance);
                break;
            case 2:
                tank.setY(tank.getY() + min_distance);
                break;
            case 3:
                tank.setY(tank.getY() - min_distance);
                break;
        }
    }
}
