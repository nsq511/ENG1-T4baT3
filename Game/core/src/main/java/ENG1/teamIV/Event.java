package ENG1.teamIV;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Event extends Entity{
    private Array<Event> blockedBy;     // List of prerequisite events that must be completed first to trigger this event
    private boolean complete;           // Whether this event has been completed

    /**
     * Create an Event as a square {@link Entity Entity}
     */
    public Event(){
        this(new Array<>(), 1, new Vector2());
    }
    /**
     * Create an Event as a square {@link Entity Entity}
     * 
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param size The width and height of the Rectangle
     * @param pos The world position of the entity
     */
    public Event(Array<Event> prerequisites, float size, Vector2 pos){
        this(prerequisites, size, size, pos);
    }
    /**
     * Create an Event as an {@link Entity Entity} 
     * 
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param rectWidth The width of the Rectangle
     * @param rectHeight The height of the Rectangle
     * @param pos The world position of the entity
     */
    public Event(Array<Event> prerequisites, float rectWidth, float rectHeight, Vector2 pos){
        super(new Texture(AppConstants.TRANSPARENT_TEX), rectWidth, rectHeight, rectWidth, rectHeight, pos);
        blockedBy = new Array<>(prerequisites);     // Shallow copy important
        visible = false;
    }

    /**
     * Whether the event has been completed
     * @return Whether the event has been completed
     */
    public boolean isComplete(){
        return complete;
    }

    /**
     * What to do when the event is executed
     * May be overridden by an anonymous class
     */
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
     * What to do on game restart to make the event playable again
     * Like, execute() may be overridden by an anonymous class
     * At minimum this function should call super.reset() and set complete to false
     */
    public void reset(){
        super.reset();
        complete = false;
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
