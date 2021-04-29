package edu.csc413.tankgame.collision;

import edu.csc413.tankgame.model.Entity;

import java.util.List;

public class ShellShellCollisionHandler implements CollisionHandler  {
    @Override
    public void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList) {
        System.out.println("Shell-Shell: (" + entity1.getId() + ", " + entity2.getId() + ") are collission");
        removeList.add(entity1);
        removeList.add(entity2);
    }
}
