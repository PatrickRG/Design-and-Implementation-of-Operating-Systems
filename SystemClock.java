public class SystemClock{

    private long currentTime;

    public SystemClock(long currentTime){
        this.currentTime = currentTime;
    }

    //Increment clock method that the CPU will wall during each iteration
    public void incrementClock(){ currentTime++; }

    //Return the current system time
    public long getTime(){ return currentTime; }
}
