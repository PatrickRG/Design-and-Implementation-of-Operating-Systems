public class PCB{

    private int jobID;
    private int jobSize;
    //Number of predicated CPUBursts
    private int numCPUBursts;
    //Remaining length of the current CPU burst
    private int currentBurstLength;
    private long entryTime;
    //Total CPU time used by the job
    private int cpuTimeUsed;
    //Variable that is set to currentTime+10 whenever I/O is requested
    private long completionTimeOfIO;
    //Total number of CPU shots by the job
    private int numCPUShots;
    //Holds the CPU Burst array
    private int[] cpuBursts;
    /*  Holds what subqueue the job is currently in
        0 = Blocked Queue
        1 = Sub Queue 1
        2 = Sub Queue 2
        3 = Sub Queue 3
        4 = Sub Queue 4*/
    private int currentSubqueue;
    //Holds the number of turns a job has had so far in the current subqueue
    private int timesInSubqueue;
    //Holds whether this is the job's final burst before termination
    private boolean finalBurst;
    //Holds whether the job needs to be demoted to a lower priority subqueue after the upcoming execution
    private boolean demotionAfterQuantum;

    //Contrustor for a PCB holding all of the variables listed above
    public PCB(int jobID, int jobSize, int numCPUBursts, int currentBurstLength, long entryTime, int cpuTimeUsed, long completionTimeOfIO, int[] cpuBursts, int currentSubqueue, int timesInSubqueue, boolean finalburst, boolean demotionAfterQuantum, int numCPUShots){
        this.jobID = jobID;
        this.jobSize = jobSize;
        this.numCPUBursts = numCPUBursts;
        this.currentBurstLength = currentBurstLength;
        this.entryTime = entryTime;
        this.cpuTimeUsed = cpuTimeUsed;
        this.completionTimeOfIO = completionTimeOfIO;
        this.cpuBursts = cpuBursts;
        this.currentSubqueue = currentSubqueue;
        this.timesInSubqueue = timesInSubqueue;
        this.finalBurst = finalBurst;
        this.demotionAfterQuantum = demotionAfterQuantum;
        this.numCPUShots = numCPUShots;
    }

    /*
        The following methods are accessors and setters/incrementors for the variables that are listed above.
        Since some variables are only set once (in the constructor), some variables only have a 'get' method. (i.e: the job ID never changes)
    */

    public int getID(){ return jobID;}

    public int getJobSize(){ return jobSize;}

    public int getCurrentSubqueue() {return currentSubqueue;}
    public void setCurrentSubqueue(int i){ currentSubqueue = i;}

    public int getTimesInSubqueue(){ return timesInSubqueue; }
    public void setTimesInSubqueue(int i){ timesInSubqueue = i; }


    public int getCurrentBurstLength(){ return currentBurstLength;}
    public void setCurrentBurstLength(int i) {currentBurstLength = i;}

    public int[] getCPUBurstArray() { return cpuBursts;}
    public void removeBurstArrayTime(int i){ cpuBursts[i] = 0; }

    public long getCompletionTimeOfIO(){ return completionTimeOfIO;}
    public void setCompletionTimeOfIO(long i){ completionTimeOfIO = i; }

    public boolean getFinalBurst(){ return finalBurst;}
    public void setFinalBurst(boolean b){ finalBurst = b;}

    public boolean getDemotionAfterQuantum(){ return demotionAfterQuantum;}
    public void setDemotionAfterQuantum(boolean b){ demotionAfterQuantum = b;}

    public int getCPUTimeUsed(){ return cpuTimeUsed;}
    public void incrementCPUTimeUsed(){ cpuTimeUsed++; }

    public void blockedCPUTime(){ cpuTimeUsed += 10; }

    public long getEntryTime(){ return entryTime;}

    public int getNumCPUShots(){ return numCPUShots; }
    public void incrementNumCPUShots(){ numCPUShots++; }

}
