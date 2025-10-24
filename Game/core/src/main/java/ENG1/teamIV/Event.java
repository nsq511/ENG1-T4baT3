package ENG1.teamIV;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Event extends Entity{
    private Array<Event> blockedBy;     // List of prerequisite events that must be completed first to trigger this event
    private boolean complete;           // Whether this event has been completed

    /**
     * Create an Event as a square {@link Entity Entity} with matching Rectangle
     * 
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param spriteTexture The filepath to the texture for the Sprite
     * @param size The width and height of the Sprite and Rectangle
     * @param pos The world position of the entity
     */
    public Event(Array<Event> prerequisites, String spriteTexture, float size, Vector2 pos){
        this(prerequisites, new Texture(spriteTexture), size, size, size, size, pos);
    }
    /**
     * Create an Event as a square {@link Entity Entity} with matching Rectangle
     * 
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param spriteTexture The Texture for the Sprite
     * @param size The width and height of the Sprite and Rectangle
     * @param pos The world position of the entity
     */
    public Event(Array<Event> prerequisites, Texture spriteTexture, float size, Vector2 pos){
        this(prerequisites, spriteTexture, size, size, size, size, pos);
    }
    /**
     * Create an Event as a square {@link Entity Entity} with differing Rectangle size
     * 
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param spriteTexture The filepath to the texture for the Sprite
     * @param spriteWidth The width of the Sprite
     * @param spriteHeight The heigh of the Sprite
     * @param rectWidth The width of the Rectangle
     * @param rectHeight The height of the Rectangle
     * @param pos The world position of the entity
     */
    public Event(Array<Event> prerequisites, String spriteTexture, float spriteWidth, float spriteHeight, float rectWidth, float rectHeight, Vector2 pos){
        this(prerequisites, new Texture(spriteTexture), spriteWidth, spriteHeight, rectWidth, rectHeight, pos);
    }
    /**
     * Create an Event as a square {@link Entity Entity} with differing Rectangle size
     * 
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param spriteTexture The filepath to the texture for the Sprite
     * @param spriteWidth The width of the Sprite
     * @param spriteHeight The heigh of the Sprite
     * @param rectWidth The width of the Rectangle
     * @param rectHeight The height of the Rectangle
     * @param pos The world position of the entity
     */
    public Event(Array<Event> prerequisites, Texture tex, float spriteWidth, float spriteHeight, float rectWidth, float rectHeight, Vector2 pos){
        super(tex, spriteWidth, spriteHeight, rectWidth, rectHeight, pos);
        blockedBy = new Array<>(prerequisites);     // Shallow copy important
    }

    /**
     * Whether the event has been completed
     * @return Whether the event has been completed
     */
    public boolean isComplete(){
        return complete;
    }

    void execute(){
        /*
         * Execute should be Overridden by any instantiation of an Event using anonymous classes. 
         * 
         * E.g.
         * 
         * Event e = new Event(){
         *      @Override
         *      public void execute(){
         *          // Code to run on event execution
         *      }
         * };
         */
    }    

    /**
     * Runs execute() if the even is executable.
     * I.e. It has not already been completed and has no prerequisites blocking it
     */
    public void tryEvent(){
        if(isExecutable()){
            execute();
            complete = true;
        }
    }

    /**
     * Checks whether the event is executable
     * I.e. It has not already been completed and has no prerequisites blocking it
     */
    public boolean isExecutable(){
        if(complete) return false;

        // If any of the prerequisite quests are incomplete then it is not executable
        for(Event e: blockedBy){
            if(!e.isComplete()) return false;
        }
        return true;
    }
}
