package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;
import edu.csc413.tankgame.KeyboardReader;

import java.util.List;

public class PlayerTank extends Tank {
    float scale_fire = 1.0f;
    float scale_move = 1.0f;
    public PlayerTank(String id, double x, double y, double angle) {
        super(id, x, y, angle);
        cooldown = 0;
        health = 6;
        scale_fire = 1.0f;
        scale_move = 1.0f;
    }

    public void move(GameWorld gameWorld) {
        if(KeyboardReader.instance().upPressed() )
            moveForward(Constants.TANK_MOVEMENT_SPEED * scale_move);
        if(KeyboardReader.instance().downPressed() )
            moveBackward(Constants.TANK_MOVEMENT_SPEED * scale_move);
        if(KeyboardReader.instance().leftPressed() )
            turnLeft(Constants.TANK_TURN_SPEED);
        if(KeyboardReader.instance().rightPressed() )
            turnRight(Constants.TANK_TURN_SPEED);

        if( cooldown > 0 ) {
            cooldown--;
            return;
        }

        if(KeyboardReader.instance().spacePressed() )
        {
            if( cooldown > 0 )
                return;

            // get shell which fire
            // check if there is a fired shell
            boolean exist = false;
            List<Entity> entityList = gameWorld.getEntities();
            for(Entity entity : entityList) {
                if( entity instanceof Shell )
                {
                    Shell shell = (Shell) entity;
                    if( shell.getTankID().equals(getId())) {
                        exist = true;
                    }
                }
            }

            if( exist )
            {
                // cannot fire
                return;
            }

            cooldown = (int)(200 / scale_fire);
            Shell shell = new Shell("player_shell_" + System.currentTimeMillis(), getShellX(), getShellY(), getShellAngle(), getId());
            System.out.println("Shell is created");
            gameWorld.addNewEntity(shell);
        }
    }

    public void powerUp() {
        scale_fire *= 1.5f;
        scale_move *= 1.5f;
    }
}
