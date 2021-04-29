package edu.csc413.tankgame.collision;

import edu.csc413.tankgame.model.Entity;

import java.util.List;

public interface CollisionHandler {
    public void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList);
}
