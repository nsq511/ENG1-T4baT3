package ENG1.teamIV;

import com.badlogic.gdx.math.Vector2;

public class Timer{
    private float timeLimit;
    private float time;
    private float timeStep;
    private boolean finished;

    /**
     * Create a Timer object which counts down in set time increments
     * 
     * @param timeLimit The amount of time to count down from in seconds
     * @param timeStep The amount to increment the timer down by on each tick in seconds
     */
    public Timer(float timeLimit, float timeStep){
        this.timeLimit = timeLimit;
        time = timeLimit;
        this.timeStep = timeStep;
        finished = false;
    }

    /**
     * Advance the time by the set time step
     */
    public void tick(){
        tick(timeStep);
    }
    /**
     * Advance the time with a specified time
     * 
     * @param timeChange The amount of time to advance the timer by in seconds
     */
    public void tick(float timeChange){
        if(finished) return;

        time -= timeChange;

        if(time <= 0){
            time = 0;
            finished = true;
        }
    }
    /**
     * Advance the timer based on distance moved
     * The timeStep is considered to be the time advanced after moving a distance of 1
     * 
     * @param movement A vector representing the movement from the last render loop
     */
    public void tick(Vector2 movement){
        float distance = movement.len();
        tick(timeStep * distance);
    }

    public boolean isFinished(){
        return finished;
    }

    /**
     * Converts the time to a score
     * 
     * @return The score
     */
    public int toScore(){
        // For a timer of 5 minutes, dividing by 3 will give a score out of 100
        return (int)time / 3;
    }

    public int getTime(){
        return (int)time;
    }

    /**
     * Restarts the timer
     */
    public void reset(){
        time = timeLimit;
        finished = false;
    }

    public String toString(){
        int timeNum = getTime();
        int minutes = timeNum / 60;
        int seconds = timeNum % 60;
        return Utilities.doubleDigit(minutes) + ":" + Utilities.doubleDigit(seconds);
    }
}
