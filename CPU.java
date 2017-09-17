public class CPU{

    //The following variables are return statuses for the possible options of jobs after execution
    //ERROR CODE
    private final int ERROR = -1;
    //Burst completed, I/O requested
    private final int BURST_COMPLETED = 1;
    //Burst not completed, current turn was not the last turn for the job in its corresponding subqueue
    private final int BURST_NOT_COMPLETED = 2;
    //Burst not completed, current turn was last turn for the job in its corresponding subqueue
    private final int BURST_NOT_COMPLETED_DEMOTE = 3;
    //Burst completed, I/O requested in the middle of the time slice
    private final int BURST_COMPLETED_QUANTUM_NOT_EXPIRED = 4;
    //Burst completed, current burst was the job's final burst
    private final int TERMINATED = 5;

    public int run(SYSTEM system, PCB currentJob, int timeQuantum, boolean finalBurst, boolean demotionAfterQuantum){

        boolean finishedBurst = false;
        boolean ioRequestInQuantum = false;

        currentJob.incrementNumCPUShots();

        int counter = 0;
        //Iterate up to the current allowed time quantum
        while(counter < timeQuantum){
            //Increment the virtual clock

            system.clock.incrementClock();
            //Check if stats needs to be called, in order to output to SYS_LOG
            if(system.clock.getTime() % 200 == 0)
                system.mem_manager.stats(system);

            //Decrement the current burst length for the job
            if(currentJob.getCurrentBurstLength() > 0)
            {
                currentJob.setCurrentBurstLength(currentJob.getCurrentBurstLength() - 1);
                currentJob.incrementCPUTimeUsed();
            }
            //Handle the case in which I/O was requested in the middle of the time slice
            else if(currentJob.getCurrentBurstLength() == 0){
                //Boolean to store if an IO request was made during the time slice
                ioRequestInQuantum = true;
                //Store if the burst finished before we break
                finishedBurst = true;
                break;
            }

            //Check if the burst finished during the last virtual time unit for the time slice
            if(currentJob.getCurrentBurstLength() == 0){
                finishedBurst = true;
                ioRequestInQuantum = true;
            }
            counter++;
        }


        //Return a status for the job back to the system to take the appropriate scheduling action
        if(finishedBurst && finalBurst)
            return TERMINATED;
        else if(finishedBurst && !finalBurst && ioRequestInQuantum)
            return BURST_COMPLETED_QUANTUM_NOT_EXPIRED;
        else if(finishedBurst && !finalBurst)
            return BURST_COMPLETED;
        else if(!finishedBurst && !demotionAfterQuantum)
            return BURST_NOT_COMPLETED;
        else if(!finishedBurst && demotionAfterQuantum)
            return BURST_NOT_COMPLETED_DEMOTE;
        //THIS SHOULD NEVER BE RETURNED, INDICATED AN ERROR
        else
            return ERROR;
    }

    //This is called by SYSTEM if the CPU is idle
    public void idle(SYSTEM system){
        system.clock.incrementClock();
    }
}
