package edu.csc413.tankgame;

import edu.csc413.tankgame.model.*;
import edu.csc413.tankgame.view.*;

import java.awt.event.ActionEvent;
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

    /**
     * setUpGame is called once at the beginning when the game is started. Entities that are present from the start
     * should be initialized here, with their corresponding sprites added to the RunGameView.
     */
    private void setUpGame() {
        // TODO: Implement.

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

            runGameView.addSprite(entity.getId(), filename, entity.getX(), entity.getY(), entity.getAngle());
        }
    }

    /**
     * updateGame is repeatedly called in the gameplay loop. The code in this method should run a single frame of the
     * game. As long as it returns true, the game will continue running. If the game should stop for whatever reason
     * (e.g. the player tank being destroyed, escape being pressed), it should return false.
     */
    private boolean updateGame() {
        // TODO: Implement.
        // check if game is finished
        if( gameWorld.isGameFinished() )
            return false;

        if(KeyboardReader.instance().escapePressed() )
            return false;

        gameWorld.update();

        // check if game object is valid

        List<Entity> entityList = gameWorld.getEntities();
        for(Entity entity : entityList) {
            if( entity instanceof Tank )
            {
                if( entity.getX() < Constants.TANK_X_LOWER_BOUND )
                    entity.setX(Constants.TANK_X_LOWER_BOUND);

                if( entity.getX() > Constants.TANK_X_UPPER_BOUND )
                    entity.setX(Constants.TANK_X_UPPER_BOUND);

                if( entity.getY() < Constants.TANK_Y_LOWER_BOUND )
                    entity.setY(Constants.TANK_Y_LOWER_BOUND);

                if( entity.getY() > Constants.TANK_Y_UPPER_BOUND )
                    entity.setY(Constants.TANK_Y_UPPER_BOUND);
            }
        }

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
}
