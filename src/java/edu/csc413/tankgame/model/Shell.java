package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

public class Shell extends Entity {
    ShellState shellState = ShellState.INIT;  // 0: init 1: moving 2: destroyed
    String tank_id = "";
    public Shell(String id, double x, double y, double angle, String tank_id) {
        super(id, x, y, angle);
        this.tank_id = tank_id;
        shellState = ShellState.INIT;
    }

    @Override
    public void move(GameWorld gameWorld) {
        x += Constants.SHELL_MOVEMENT_SPEED * Math.cos(angle);
        y += Constants.SHELL_MOVEMENT_SPEED * Math.sin(angle);
    }

    public void setShellState(ShellState state) {
        shellState = state;
    }

    public ShellState getShellState() {
        return shellState;
    }

    public String getTankID() {
        return tank_id;
    }
}
