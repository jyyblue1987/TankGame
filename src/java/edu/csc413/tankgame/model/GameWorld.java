package edu.csc413.tankgame.model;

import edu.csc413.tankgame.Constants;

import java.util.*;

/**
 * GameWorld holds all of the model objects present in the game. GameWorld tracks all moving entities like tanks and
 * shells, and provides access to this information for any code that needs it (such as GameDriver or entity classes).
 */
public class GameWorld {
    // TODO: Implement. There's a lot of information the GameState will need to store to provide contextual information.
    //       Add whatever instance variables, constructors, and methods are needed.

    List<Entity> entities = new ArrayList<Entity>();

    public GameWorld() {
    }

    /** Returns a list of all entities in the game. */
    public List<Entity> getEntities() {
        // TODO: Implement.
        return entities;
    }

    /** Adds a new entity to the game. */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /** Returns the Entity with the specified ID. */
    public Entity getEntity(String id) {
        // TODO: Implement.
        for(Entity entity : entities)
        {
            if(entity.getId().equals(id))
                return entity;
        }
        return null;
    }

    /** Removes the entity with the specified ID from the game. */
    public void removeEntity(String id) {
        // TODO: Implement.
        for(Entity entity : entities)
        {
            Entity tank = (Entity) entity;
            if(tank.getId().equals(id))
                entities.remove(entity);
        }
    }

    public void clear() {
        entities.clear();
    }
}
