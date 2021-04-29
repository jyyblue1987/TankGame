package edu.csc413.tankgame.collision;

import edu.csc413.tankgame.model.Entity;
import edu.csc413.tankgame.model.Shell;
import edu.csc413.tankgame.model.Tank;
import edu.csc413.tankgame.model.Wall;

import java.util.List;

public class ShellWallCollisionHandler implements CollisionHandler  {
    @Override
    public void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList) {
        Shell shell = null;
        Wall wall = null;

        if( entity1 instanceof Shell )
            shell = (Shell) entity1;
        if( entity2 instanceof Shell )
            shell = (Shell) entity2;

        if( entity1 instanceof Wall )
            wall = (Wall) entity1;
        if( entity2 instanceof Wall )
            wall = (Wall) entity2;

        removeList.add(shell);

        wall.decreaseHealth();
        if( wall.isAlive() == false )
            removeList.add(wall);
    }
}
