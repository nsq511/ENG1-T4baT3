package ENG1.teamIV;

import com.badlogic.gdx.math.Vector2;

public class Timer {
    private float time;
    private float timeStep;

    /**
     * Create a Timer object which counts down in set time increments
     * 
     * @param timeLimit The amount of time to count down from
     * @param timeStep The amount to increment the timer down by on each tick
     */
    public Timer(int timeLimit, int timeStep){
        time = timeLimit;
        this.timeStep = timeStep;
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
     * @param timeChange The amount of time to adnavce the timer by
     */
    public void tick(float timeChange){
        time -= timeChange;

        if(time <= 0){
            time = 0;
            timesUp();
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

    private void timesUp(){

    }

    public int getTime(){
        return (int)time;
    }
}
