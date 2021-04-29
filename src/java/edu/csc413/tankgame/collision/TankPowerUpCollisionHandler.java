package edu.csc413.tankgame.collision;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.model.*;

import java.util.List;

public class TankPowerUpCollisionHandler implements CollisionHandler {
    @Override
    public void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList) {
        PlayerTank tank = null;
        PowerUp powerUp = null;

        if( entity1 instanceof Tank )
            tank = (PlayerTank) entity1;
        if( entity2 instanceof Tank )
            tank = (PlayerTank) entity2;

        if( entity1 instanceof PowerUp )
            powerUp = (PowerUp) entity1;
        if( entity2 instanceof PowerUp )
            powerUp = (PowerUp) entity2;

        removeList.add(powerUp);
        tank.powerUp();
    }
}
