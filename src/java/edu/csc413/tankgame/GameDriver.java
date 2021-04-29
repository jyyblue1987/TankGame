package edu.csc413.tankgame;

import edu.csc413.tankgame.model.*;
import edu.csc413.tankgame.view.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GameDriver {
    private final MainView mainView;
    private final RunGameView runGameView;
    private final GameWorld gameWorld;


    public GameDriver() {
        mainView = new MainView(this::startMenuActionPerformed);
        runGameView = mainView.getRunGameView();
        gameWorld = new GameWorld();
    }

    public void start() {
        mainView.setScreen(MainView.Screen.START_GAME_SCREEN);
    }

    private void startMenuActionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case StartMenuView.START_BUTTON_ACTION_COMMAND -> runGame();
            case StartMenuView.EXIT_BUTTON_ACTION_COMMAND -> mainView.closeGame();
            default -> throw new RuntimeException("Unexpected action command: " + actionEvent.getActionCommand());
        }
    }

    private void runGame() {
        mainView.setScreen(MainView.Screen.RUN_GAME_SCREEN);
        Runnable gameRunner = () -> {
            setUpGame();
            while (updateGame()) {
                runGameView.repaint();
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            }
            mainView.setScreen(MainView.Screen.END_MENU_SCREEN);
            resetGame();
        };
        new Thread(gameRunner).start();
    }

    private void addWallToView() {
        List<WallInformation> wall_list = WallInformation.readWalls();

        int i = 1;
        for(WallInformation wall : wall_list) {
            gameWorld.addEntity(new Wall("wall_" + i, wall.getX(), wall.getY(), wall.getImageFile()));
            i++;
        }
    }

    /**
     * setUpGame is called once at the beginning when the game is started. Entities that are present from the start
     * should be initialized here, with their corresponding sprites added to the RunGameView.
     */
    private void setUpGame() {
        // TODO: Implement.

        // addWallToView
        addWallToView();

        // Create Player Tank and AI tank
        Entity playerTank = new PlayerTank(Constants.PLAYER_TANK_ID, Constants.PLAYER_TANK_INITIAL_X, Constants.PLAYER_TANK_INITIAL_Y, Constants.PLAYER_TANK_INITIAL_ANGLE){};
        Entity aiTank1 = new AITank(Constants.AI_TANK_1_ID, Constants.AI_TANK_1_INITIAL_X, Constants.AI_TANK_1_INITIAL_Y, Constants.AI_TANK_1_INITIAL_ANGLE){};
        Entity aiTank2 = new AITank(Constants.AI_TANK_2_ID, Constants.AI_TANK_2_INITIAL_X, Constants.AI_TANK_2_INITIAL_Y, Constants.AI_TANK_2_INITIAL_ANGLE){};

        // Add Objects into Game World
        gameWorld.addEntity(playerTank);
        gameWorld.addEntity(aiTank1);
        gameWorld.addEntity(aiTank2);

        // Add Spirits to View
        List<Entity> entityList = gameWorld.getEntities();
        String filename = "player-tank.png";
        for(Entity entity : entityList) {
            if( entity instanceof PlayerTank )
                filename = "player-tank.png";
            if( entity instanceof AITank )
                filename = "ai-tank.png";

            if( entity instanceof Wall ) {
                filename = ((Wall)entity).getImagefile();
            }

            runGameView.addSprite(entity.getId(), filename, entity.getX(), entity.getY(), entity.getAngle());
        }
    }

    /**
     * updateGame is repeatedly called in the gameplay loop. The code in this method should run a single frame of the
     * game. As long as it returns true, the game will continue running. If the game should stop for whatever reason
     * (e.g. the player tank being destroyed, escape being pressed), it should return false.
     */

    private void preventTankIntoOutside() {
        List<Entity> entityList = gameWorld.getEntities();

        for(Entity entity : entityList) {
            if (entity instanceof Tank) {
                if (entity.getX() < Constants.TANK_X_LOWER_BOUND)
                    entity.setX(Constants.TANK_X_LOWER_BOUND);

                if (entity.getX() > Constants.TANK_X_UPPER_BOUND)
                    entity.setX(Constants.TANK_X_UPPER_BOUND);

                if (entity.getY() < Constants.TANK_Y_LOWER_BOUND)
                    entity.setY(Constants.TANK_Y_LOWER_BOUND);

                if (entity.getY() > Constants.TANK_Y_UPPER_BOUND)
                    entity.setY(Constants.TANK_Y_UPPER_BOUND);
            }
        }

    }
    private boolean updateGame() {
        // TODO: Implement.
        // check if game is finished
        if( gameWorld.isGameFinished() )
            return false;

        if(KeyboardReader.instance().escapePressed() )
            return false;

        gameWorld.update();

        // check if tank is outside of region
        preventTankIntoOutside();

        List<Entity> entityList = gameWorld.getEntities();
        List<Entity> removeList = new ArrayList<>();

        for(Entity entity : entityList) {

            if( entity instanceof Shell )
            {
                // check outside of shell
                if( entity.getX() < Constants.SHELL_X_LOWER_BOUND || entity.getX() > Constants.SHELL_X_UPPER_BOUND ||
                        entity.getY() < Constants.SHELL_Y_LOWER_BOUND || entity.getY() > Constants.SHELL_Y_UPPER_BOUND)
                    removeList.add(entity);
            }
        }

        // Collision Detection
        int entity_count = entityList.size();
        for(int i = 0; i < entity_count - 1; i++)
        {
            Entity e1 = entityList.get(i);
            if( removeList.contains(e1) )
                continue;
            for(int j = i + 1; j < entity_count; j++)
            {
                Entity e2 = entityList.get(j);
                if( removeList.contains(e2) )
                    continue;

                if( entitiesOverlap(e1, e2) )
                {
                    handleCollision(e1, e2, removeList);
                }
            }
        }

        // remove sprits from view
        for(Entity entity: removeList) {
            runGameView.removeSprite(entity.getId());
            System.out.println(entity.getId() + " is removed from view");
            if( entity instanceof Shell )
                runGameView.addAnimation(RunGameView.SHELL_EXPLOSION_ANIMATION, 0, entity.getX(), entity.getY());
            if( entity instanceof Tank )
                runGameView.addAnimation(RunGameView.BIG_EXPLOSION_ANIMATION, 3, entity.getX(), entity.getY());
        }

        // remove entity from model
        entityList.removeAll(removeList);

        for(Entity entity : entityList) {
            if( entity instanceof Shell )
            {
                Shell shell = (Shell)entity;
                if( shell.getShellState() == ShellState.INIT )
                {
                    runGameView.addSprite(entity.getId(), "shell.png", entity.getX(), entity.getY(), entity.getAngle());
                    shell.setShellState(ShellState.MOVING);
                }
            }
            runGameView.setSpriteLocationAndAngle(entity.getId(), entity.getX(), entity.getY(), entity.getAngle());
        }

        if( gameWorld.getEntity(Constants.PLAYER_TANK_ID) == null )
            return false;

        if( gameWorld.getEntity(Constants.AI_TANK_1_ID) == null && gameWorld.getEntity(Constants.AI_TANK_2_ID) == null )
            return false;

        return true;
    }

    /**
     * resetGame is called at the end of the game once the gameplay loop exits. This should clear any existing data from
     * the game so that if the game is restarted, there aren't any things leftover from the previous run.
     */
    private void resetGame() {
        // TODO: Implement.
        runGameView.reset();
        gameWorld.clear();

    }

    public static void main(String[] args) {
        GameDriver gameDriver = new GameDriver();
        gameDriver.start();
    }

    private boolean entitiesOverlap(Entity e1, Entity e2) {
        if( e1.getX() < e2.getXBound() &&
            e1.getXBound() > e2.getX() &&
            e1.getY() < e2.getYBound() &&
            e1.getYBound() > e2.getY())
            return true;

        return false;
    }

    private void handleCollisionTank2Tank(Entity entity1, Entity entity2) {
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

    private void handleCollisionShell2Shell(Entity entity1, Entity entity2, List<Entity> removeList) {
        System.out.println("Shell-Shell: (" + entity1.getId() + ", " + entity2.getId() + ") are collission");
        removeList.add(entity1);
        removeList.add(entity2);
    }

    private void handleCollisionShell2Tank(Shell shell, Tank tank, List<Entity> removeList) {
        removeList.add(shell);
        if( shell.getTankID().equals(Constants.PLAYER_TANK_ID) && (tank.getId().equals(Constants.AI_TANK_1_ID) || tank.getId().equals(Constants.AI_TANK_2_ID)) ||
                (shell.getTankID().equals(Constants.AI_TANK_1_ID) || shell.getTankID().equals(Constants.AI_TANK_2_ID)) && tank.getId().equals(Constants.PLAYER_TANK_ID))
        {
            tank.decreaseHealth();
            if( tank.isAliveTank() == false )
                removeList.add(tank);
        }
    }

    private void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList) {
        if (entity1 instanceof Tank && entity2 instanceof Tank) {
            handleCollisionTank2Tank(entity1, entity2);
        } else if (entity1 instanceof Shell && entity2 instanceof Shell) {
            handleCollisionShell2Shell(entity1, entity2, removeList);
        } else if (entity1 instanceof Tank && entity2 instanceof Shell) {
            handleCollisionShell2Tank((Shell)entity2, (Tank)entity1, removeList);
        } else if (entity1 instanceof Shell && entity2 instanceof Tank) {
            handleCollisionShell2Tank((Shell)entity1, (Tank)entity2, removeList);
        }
    }
}
