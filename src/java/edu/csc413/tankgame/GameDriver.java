package edu.csc413.tankgame;

import edu.csc413.tankgame.collision.GameContext;
import edu.csc413.tankgame.model.*;
import edu.csc413.tankgame.view.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GameDriver {
    private final MainView mainView;
    private final RunGameView runGameView;
    private final GameWorld gameWorld;
    private int delayNumber = 0;
    private boolean gamePaused = false;


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
            case PauseMenuView.RESUME_BUTTON_ACTION_COMMAND -> resumeGame();
            case PauseMenuView.RESTART_BUTTON_ACTION_COMMAND -> restartGame();
            case StartMenuView.EXIT_BUTTON_ACTION_COMMAND -> mainView.closeGame();
            default -> throw new RuntimeException("Unexpected action command: " + actionEvent.getActionCommand());
        }
    }

    private void resumeGame() {
        mainView.setScreen(MainView.Screen.RUN_GAME_SCREEN);
        gamePaused = false;
    }

    private void restartGame() {
        resetGame();
        runGame();
    }


    private void runGame() {
        mainView.setScreen(MainView.Screen.RUN_GAME_SCREEN);
        Runnable gameRunner = () -> {
            setUpGame();
            while (updateGame()) {
                if( gamePaused == false )
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
        delayNumber = 0;
        gamePaused = false;
        addWallToView();

        // Create Player Tank and AI tank
        Entity playerTank = new PlayerTank(Constants.PLAYER_TANK_ID, Constants.PLAYER_TANK_INITIAL_X, Constants.PLAYER_TANK_INITIAL_Y, Constants.PLAYER_TANK_INITIAL_ANGLE){};
        Entity aiTank1 = new AITank(Constants.AI_TANK_1_ID, Constants.AI_TANK_1_INITIAL_X, Constants.AI_TANK_1_INITIAL_Y, Constants.AI_TANK_1_INITIAL_ANGLE){};
        Entity aiTank2 = new AITank(Constants.AI_TANK_2_ID, Constants.AI_TANK_2_INITIAL_X, Constants.AI_TANK_2_INITIAL_Y, Constants.AI_TANK_2_INITIAL_ANGLE){};


        // Add Objects into Game World
        gameWorld.addEntity(playerTank);
        gameWorld.addEntity(aiTank1);
        gameWorld.addEntity(aiTank2);

        // Add Power up icon
        for(int i = 0; i < Constants.POWERUP_COUNT; i++) {
            double x = Math.random() * (Constants.TANK_X_UPPER_BOUND - Constants.TANK_X_LOWER_BOUND) + Constants.TANK_X_LOWER_BOUND;
            double y = Math.random() * (Constants.TANK_Y_UPPER_BOUND - Constants.TANK_Y_LOWER_BOUND) + Constants.TANK_Y_LOWER_BOUND;
            Entity powerup = new PowerUp("powerup_" + i, x, y);
            gameWorld.addEntity(powerup);
        }

        // Add Spirits to View
        List<Entity> entityList = gameWorld.getEntities();
        String filename = "player-tank.png";
        for(Entity entity : entityList) {
            if( entity instanceof PlayerTank )
                filename = "player-tank.png";
            if( entity instanceof AITank )
                filename = "ai-tank.png";
            if( entity instanceof Wall )
                filename = ((Wall)entity).getImagefile();
            if( entity instanceof PowerUp )
                filename = "powerup.png";

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

        if(KeyboardReader.instance().escapePressed() ) {
            if( gamePaused == false )
                mainView.setScreen(MainView.Screen.PAUSE_MENU_SCREEN);

            gamePaused = true;
            return true;
        }

        if( gamePaused == true )
            return true;

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
                runGameView.addAnimation(RunGameView.SHELL_EXPLOSION_ANIMATION, RunGameView.SHELL_EXPLOSION_FRAME_DELAY, entity.getX(), entity.getY());
            if( entity instanceof Tank )
                runGameView.addAnimation(RunGameView.BIG_EXPLOSION_ANIMATION, RunGameView.BIG_EXPLOSION_FRAME_DELAY, entity.getX(), entity.getY());
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
        {
            return shouldWaited();
        }

        if( gameWorld.getEntity(Constants.AI_TANK_1_ID) == null && gameWorld.getEntity(Constants.AI_TANK_2_ID) == null )
        {
            return shouldWaited();
        }


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

    private void handleCollision(Entity entity1, Entity entity2, List<Entity> removeList) {
        GameContext context = new GameContext();
        context.executeCollision(entity1, entity2, removeList);
    }

    private boolean shouldWaited() {
        if( delayNumber <= 0 )
            delayNumber = 100;
        else
            delayNumber--;

        if( delayNumber == 1 )
            return false;

        return true;
    }
}
