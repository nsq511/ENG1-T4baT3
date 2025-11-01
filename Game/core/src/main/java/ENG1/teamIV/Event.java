package ENG1.teamIV;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Event extends Entity{
    private Array<Event> blockedBy;     // List of prerequisite events that must be completed first to trigger this event
    protected boolean complete;           // Whether this event has been completed
    protected boolean started;            // Whether the event has started

    private static int goodEventCounter = 0;    // Counts the number of good events completed
    private static int badEventCounter = 0;     // Counts the number of bad events completed
    private static int hiddenEventCounter = 0;  // Counts the number of hidden events completed

    private String name;                 // VERY useful for debugging

    /**
     * Create an Event as an invisible trigger square area
     * 
     * @param name The name of the event
     * @param note1Pos 
     * @param f 
     * @param notesTex 
     */
    public Event(String name, String notesTex, float f, Vector2 note1Pos){
        this(name, new Array<>(), 1, new Vector2());
    }
    /**
     * Create an Event as an invisible trigger square area
     * 
     * @param name The name of the event
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param size The width and height of the Rectangle
     * @param pos The world position of the entity
     */
    public Event(String name, Array<Event> prerequisites, float size, Vector2 pos){
        this(name, prerequisites, size, size, pos);
    }
    /**
     * Create an Event as an invisible trigger rectangle area
     * 
     * @param name The name of the event
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param rectWidth The width of the Rectangle
     * @param rectHeight The height of the Rectangle
     * @param pos The world position of the entity
     */
    public Event(String name, Array<Event> prerequisites, float rectWidth, float rectHeight, Vector2 pos){
        super(new Texture(AppConstants.TRANSPARENT_TEX), rectWidth, rectHeight, rectWidth, rectHeight, pos);
        blockedBy = new Array<>(prerequisites);     // Shallow copy important
        visible = false;    // Events is an invisible trigger area
        this.name = name;
    }
    /**
     * Create an Event as a visible trigger rectangle area
     * 
     * @param name The name of the event
     * @param tex The path to the texture of the event
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param rectWidth The width of the Rectangle
     * @param rectHeight The height of the Rectangle
     * @param pos The world position of the entity
     */
    public Event(String name, String tex, Array<Event> prerequisites, float rectWidth, float rectHeight, Vector2 pos){
        super(new Texture(tex), rectWidth, rectHeight, rectWidth, rectHeight, pos);
        blockedBy = new Array<>(prerequisites);     // Shallow copy important
        this.name = name;
    }
    /**
     * Create an Event as a visible trigger rectangle area
     * 
     * @param name The name of the event
     * @param tex The texture of the event
     * @param prerequisites The Events that must be completed before this Event can be started. Used for multi-stage events
     * @param rectWidth The width of the Rectangle
     * @param rectHeight The height of the Rectangle
     * @param pos The world position of the entity
     */
    public Event(String name, Texture tex, Array<Event> prerequisites, float rectWidth, float rectHeight, Vector2 pos){
        super(tex, rectWidth, rectHeight, rectWidth, rectHeight, pos);
        blockedBy = new Array<>(prerequisites);     // Shallow copy important
        this.name = name;
    }

    /**
     * Whether the event has been completed
     * @return Whether the event has been completed
     */
    public boolean isComplete(){
        return complete;
    }

    /**
     * What to do when the event is started
     * May be overridden by an anonymous class
     */
    void onStart(){
        /*
         * Execute should be Overridden by any instantiation of an Event using anonymous classes. 
         * 
         * E.g.
         * 
         * Event e = new Event(){
         *      @Override
         *      public void onStart(){
         *          // Code to run on event execution
         *      }
         * };
         */
    } 
    
    /**
     * What to do when the event is updated
     * This is called every frame after the event has started
     * May be overridden by an anonymous class
     * It should always contain a branch that will set complete to true, in order to finish the event
     */
    void onUpdate(){
        complete = true;
    }

    /**
     * What to do when the event is finished
     * May be overridden by an anonymous class
     */
    void onFinish(){

    }
    
    /**
     * What to do on game restart to make the event playable again
     * Like, execute() may be overridden by an anonymous class
     * At minimum this function should call super.reset() and set complete to false
     */
    public void reset(){
        super.reset();
        complete = false;
        started = false;
    }

    /**
     * Runs execute() if the even is executable.
     * I.e. It has not already been completed and has no prerequisites blocking it
     */
    public void tryEvent(){
        if(isExecutable()){
            if(!started){
                onStart();
                started = true;
            }
            else if(!complete){
                onUpdate();
            }   
            
            if(complete){
                onFinish();
            }
        }
    }

    /**
     * Resets the counters for each type of event
     */
    public static void resetEventCounters(){
        goodEventCounter = 0;
        badEventCounter = 0;
        hiddenEventCounter = 0;
    }

    public static int getGoodEventCounter(){
        return goodEventCounter;
    }

    public static int getBadEventCounter(){
        return badEventCounter;
    }

    public static int getHiddenEventCounter(){
        return hiddenEventCounter;
    }

    public static void incrementGoodEventCounter(){
        goodEventCounter++;
    }

    public static void incrementBadEventCounter(){
        badEventCounter++;
    }
    
    public static void incrementHiddenEventCounter(){
        hiddenEventCounter++;
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

    @Override
    public String toString(){
        return name;
    }
}
