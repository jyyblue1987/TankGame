package edu.csc413.tankgame.collision;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.model.Entity;
import edu.csc413.tankgame.model.Shell;
import edu.csc413.tankgame.model.Tank;

import java.util.List;

public class TankShellCollisionHandler implements CollisionHandler {
    @Override
    public void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList) {
        Tank tank = null;
        Shell shell = null;

        if( entity1 instanceof Tank )
            tank = (Tank) entity1;
        if( entity2 instanceof Tank )
            tank = (Tank) entity2;

        if( entity1 instanceof Shell )
            shell = (Shell) entity1;
        if( entity2 instanceof Shell )
            shell = (Shell) entity2;

        removeList.add(shell);
        if( shell.getTankID().equals(Constants.PLAYER_TANK_ID) && (tank.getId().equals(Constants.AI_TANK_1_ID) || tank.getId().equals(Constants.AI_TANK_2_ID)) ||
                (shell.getTankID().equals(Constants.AI_TANK_1_ID) || shell.getTankID().equals(Constants.AI_TANK_2_ID)) && tank.getId().equals(Constants.PLAYER_TANK_ID))
        {
            tank.decreaseHealth();
            if( tank.isAlive() == false )
                removeList.add(tank);
        }
    }
}
