package edu.csc413.tankgame.collision;

import edu.csc413.tankgame.model.Entity;

import java.util.List;

public class TankTankCollisionHandler implements CollisionHandler  {
    @Override
    public void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList) {
        double distance[] = new double[4];
        distance[0] = entity1.getXBound() - entity2.getX();
        distance[1] = entity2.getXBound() - entity1.getX();
        distance[2] = entity1.getYBound() - entity2.getY();
        distance[3] = entity2.getYBound() - entity1.getY();

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
                entity1.setX(entity1.getX() - min_distance / 2);
                entity2.setX(entity2.getX() + min_distance / 2);
                break;
            case 1:
                entity1.setX(entity1.getX() + min_distance / 2);
                entity2.setX(entity2.getX() - min_distance / 2);
                break;
            case 2:
                entity1.setY(entity1.getY() - min_distance / 2);
                entity2.setY(entity2.getY() + min_distance / 2);
                break;
            case 3:
                entity1.setY(entity1.getY() + min_distance / 2);
                entity2.setY(entity2.getY() - min_distance / 2);
                break;
        }
    }
}
