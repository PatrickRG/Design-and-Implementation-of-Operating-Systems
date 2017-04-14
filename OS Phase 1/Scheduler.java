import java.util.*;
import java.io.*;

public class Scheduler{

    private static int numberOfActivePCBs = 0;

    //The File JOB_LOG and File Writer that are used to output
    private File JOB_LOG = new File("JOB_LOG.txt");
    private FileWriter writer;

    //These variables hold the current job that is being processed by the CPU, and the time quantum for the subqueue that job is in
    PCB currentJob;
    int quantum;

    /*
        Blocked Queue for I/O waiting
        I used a Linked List to be able to iterate through it easier when checking for completion
    */
    LinkedList<PCB> blockedQueue = new LinkedList<PCB>();


    /*
        The subqueues for the MLFQ
        I used Linked List to be able to use the Peek() method
    */
    LinkedList<PCB> subqueue1 = new LinkedList<PCB>();
    LinkedList<PCB> subqueue2 = new LinkedList<PCB>();
    LinkedList<PCB> subqueue3 = new LinkedList<PCB>();
    LinkedList<PCB> subqueue4 = new LinkedList<PCB>();

    //This method checks if there are less than 15 PCBs, for setup and allocation purposes
    public boolean canCreatePCB(){
        if(numberOfActivePCBs >= 15)
            return false;
        else
            return true;
    }

    //Accessors for the number of active PCBs
    public void decrementPCBNum() { numberOfActivePCBs--;}
    public int numPCBs() { return numberOfActivePCBs;}

    /*
        This method is called by the Loader when a job is allocated by memory and ready to enter the readyQueue,
        a PCB is setup for the Job with the corresponding values, and is then placed in Subqueue1
    */
    public void setup(SYSTEM system, String[] jobInfo, int numberOfCPUBursts, long arrivalTime, int[] cpuBursts){
        //Job ID is always the first integer
        int id = Integer.parseInt(jobInfo[0]);
        //Memory units needed/size is always the second integer
        int size = Integer.parseInt(jobInfo[1]);
        long completionTime = 0;
        //Setup PCB with default starting values
        PCB newJob = new PCB(id, size, numberOfCPUBursts, 0, arrivalTime, 0, completionTime,cpuBursts, 1, 1, false, false, 0);
        //Increment number of active PCBs
        numberOfActivePCBs++;
        subqueue1.add(newJob);
    }

    //This method instantiates the File Writer for JOB_LOG and creates the headers at the top of the output file
    public void startFileWrite(){
        try{
            writer = new FileWriter(JOB_LOG);
            writer.write(String.format("%-15s %-15s %-15s %-15s %-15s \n", "JOB ID","ENTRY TIME","EXIT TIME", "EXECUTION TIME", "CPU SHOTS"));
            writer.flush();
        }catch(Exception ex){}
    }

    /*
        This method selects which job will run next out of the MLFQ
        The dispatcher will always pull in order from subqueue1 ---> subqueue4
    */
    public void dispatch(SYSTEM system){

        if(subqueue1.peek() != null){
            //Time slice for subqueue1 is 20
            quantum = 20;
            //Remove the selected job from the corresponding subqueue
            currentJob = subqueue1.remove();
            //Check if this will be the last turn in SubQueue 1 for the current job
            if(currentJob.getTimesInSubqueue() == 3)
                currentJob.setDemotionAfterQuantum(true);

            //If the current burst has remaining time of zero, we need to get the next burst in the array
            if(currentJob.getCurrentBurstLength() == 0){
                //Copy the remaining burst array
                int[] burstArray = currentJob.getCPUBurstArray();

                //Iterate through the copy to find the next non-zero burst
                for(int i = 0; i < burstArray.length; i++){
                    if(burstArray[i] > 0){
                        //Update current burst to the next non zero burst
                        currentJob.setCurrentBurstLength(burstArray[i]);
                        //Remove the burst we loaded from the overall array
                        currentJob.removeBurstArrayTime(i);
                        //If the burst we loaded is the last one, then let the PCB know this is the final burst
                        if(i == burstArray.length - 1){
                            currentJob.setFinalBurst(true);
                        }
                        return;
                    }
                }
            }
        }

        else if(subqueue2.peek() != null){
            //Time slice for subqueue2 is 30
            quantum = 30;
            //Remove the selected job from the corresponding subqueue
            currentJob = subqueue2.remove();
            //Check if this will be the last turn in SubQueue2 for the current job
            if(currentJob.getTimesInSubqueue() == 5)
                currentJob.setDemotionAfterQuantum(true);

            //If the current burst has remaining time of zero, we need to get the next burst in the array
            if(currentJob.getCurrentBurstLength() == 0){
                //Copy the remaining burst array
                int[] burstArray = currentJob.getCPUBurstArray();

                //Iterate through the copy to find the next non-zero burst
                for(int i = 0; i < burstArray.length; i++){
                    if(burstArray[i] > 0){
                        //Update current burst to the next non zero burst
                        currentJob.setCurrentBurstLength(burstArray[i]);
                        //Remove the burst we loaded from the overall array
                        currentJob.removeBurstArrayTime(i);
                        //If the burst we loaded is the last one, then let the PCB know this is the final burst
                        if(i == burstArray.length - 1){
                            currentJob.setFinalBurst(true);
                        }
                        return;
                    }
                }
            }
        }

        else if(subqueue3.peek() != null){
            //Time slice for subqueue3 is 50
            quantum = 50;
            //Remove the selected job from the corresponding subqueue
            currentJob = subqueue3.remove();
            //Check if this will be the last turn in SubQueue2 for the current job
            if(currentJob.getTimesInSubqueue() == 6)
                currentJob.setDemotionAfterQuantum(false);

            //If the current burst has remaining time of zero, we need to get the next burst in the array
            if(currentJob.getCurrentBurstLength() == 0){
                //Copy the remaining burst array
                int[] burstArray = currentJob.getCPUBurstArray();
                for(int i = 0; i < burstArray.length; i++){
                    if(burstArray[i] > 0){
                        //Update current burst to the next non zero burst
                        currentJob.setCurrentBurstLength(burstArray[i]);
                        //Remove the burst we loaded from the overall array
                        currentJob.removeBurstArrayTime(i);
                        //If the burst we loaded is the last one, then let the PCB know this is the final burst
                        if(i == burstArray.length - 1){
                            currentJob.setFinalBurst(true);
                        }
                        return;
                    }
                }
            }
        }

        else if(subqueue4.peek() != null){
            //Time slice for subqueue4 is 80
            quantum = 80;
            //Remove the selected job from the corresponding subqueue
            currentJob = subqueue4.remove();

            //If the current burst has remaining time of zero, we need to get the next burst in the array
            if(currentJob.getCurrentBurstLength() == 0){
                //Copy the remaining burst array
                int[] burstArray = currentJob.getCPUBurstArray();
                for(int i = 0; i < burstArray.length; i++){
                    if(burstArray[i] > 0){
                        //Update current burst to the next non zero burst
                        currentJob.setCurrentBurstLength(burstArray[i]);
                        //Remove the burst we loaded from the overall array
                        currentJob.removeBurstArrayTime(i);
                        //If the burst we loaded is the last one, then let the PCB know this is the final burst
                        if(i == burstArray.length - 1){
                            currentJob.setFinalBurst(true);
                        }
                        return;
                    }
                }
            }
        }

        //If all of the subqueues in the MLFQ are EMPTY, the call the loader
        else{
            system.loader.load(system);
            return;
        }
    }

    //This method is called when a job does not need a subqueue demotion and has not requested I/O
    public void readyUp(){
        //Increment number of turns in corresponding subqueue
        int currentSubQ = currentJob.getCurrentSubqueue();
        currentJob.setTimesInSubqueue(currentJob.getTimesInSubqueue() + 1);
        //Place the job back in the subqueue
        if(currentSubQ == 1)
            subqueue1.add(currentJob);
        else if(currentSubQ == 2)
            subqueue2.add(currentJob);
        else if(currentSubQ == 3)
            subqueue3.add(currentJob);
        else if(currentSubQ == 4)
            subqueue4.add(currentJob);
    }

    //This method is called when a job needs a subqueue demotion, and has not requested I/O
    public void demoteAndReadyUp(){
        //Reset the needs demotion variable
        currentJob.setDemotionAfterQuantum(false);
        //Reset number of times in subqueue
        int currentSubQ = currentJob.getCurrentSubqueue();
        currentJob.setTimesInSubqueue(1);

        //Demote the job to the next level subqueue
        if(currentSubQ == 1){
            currentJob.setCurrentSubqueue(2);
            subqueue2.add(currentJob);
        }
        else if(currentSubQ == 2){
            currentJob.setCurrentSubqueue(3);
            subqueue3.add(currentJob);
        }
        else if(currentSubQ == 3){
            currentJob.setCurrentSubqueue(4);
            subqueue4.add(currentJob);
        }
        else if(currentSubQ == 4){
            subqueue4.add(currentJob);
        }
    }

    /*
        This method handles I/O requests in the following fashion, in accordance with the assignment sheet:
        A job has an I/O request and will be added to the blocked queue
        If the request was within a time a time slice, the job remains in the same subqueue but the turns get reset (the boolean reset variable)
        If the request was made at the end of the time quantum, then the subqueue remains the same and the turns are maintained
        If the current subqueue was 4 when the I/O request was made, the job will now be promoted to sub queue 1
    */

    public void blocked(SYSTEM system, boolean reset){
        int currentSubQ = currentJob.getCurrentSubqueue();
        //If a job in subqueue4 requests I/O, promote the job to subqueue1 after the request
        if(currentSubQ == 4){
            currentJob.setCurrentSubqueue(1);
            currentJob.setTimesInSubqueue(1);
        }
        //I/O request at the end of a time slice
        else if(reset){
            currentJob.setTimesInSubqueue(1);
            currentJob.setDemotionAfterQuantum(false);
        }
        //The completion time of IO will be 10 time units from the current system time
        currentJob.blockedCPUTime();
        currentJob.setCompletionTimeOfIO(system.clock.getTime() + 10);
        //Add the job to the blockedQueue
        blockedQueue.add(currentJob);
    }

    //Stats is called whenever a job terminates
    public void stats(SYSTEM system, long exitTime){

        //remove the job from its current subqueue
        int currentSubQ = currentJob.getCurrentSubqueue();
        if(currentSubQ == 1){
            subqueue1.remove(currentJob);
        }
        else if(currentSubQ == 2){
            subqueue2.remove(currentJob);
        }
        else if(currentSubQ == 3){
            subqueue3.remove(currentJob);
        }
        else if(currentSubQ == 4){
            subqueue4.remove(currentJob);
        }
        //Write the necessary information upon termination to the JOB_LOG file
        try{
            String a = "" + system.scheduler.currentJob.getID();
            String b = "" + system.scheduler.currentJob.getEntryTime();
            String c = "" + system.clock.getTime();
            String d = "" + system.scheduler.currentJob.getCPUTimeUsed();
            String e = "" + system.scheduler.currentJob.getNumCPUShots();

            writer.write(String.format("%-15s %-15s %-15s %-15s %-15s \n",  a, b, c, d, e));
            writer.flush();
        }catch(Exception ex){}
    }

    //This method checks the blockedQueue, and removes all jobs that have waited 10 units of virtual time
    public void checkBlockedQueue(SYSTEM system){

        PCB temp;
        long currentTime = system.clock.getTime();

        for(int i = 0; i < blockedQueue.size(); i++){
            temp = blockedQueue.get(i);
            //Check if the job has qaited 10 time units
            if((currentTime - temp.getCompletionTimeOfIO()) >= 10){
                //If so, add the job back to the corresponding subqueue
                if(temp.getCurrentSubqueue() == 1)
                    subqueue1.add(blockedQueue.get(i));
                else if(temp.getCurrentSubqueue() == 2)
                    subqueue2.add(blockedQueue.get(i));
                else if(temp.getCurrentSubqueue() == 3)
                    subqueue3.add(blockedQueue.get(i));
                else if(temp.getCurrentSubqueue() == 4)
                    subqueue4.add(blockedQueue.get(i));
                //remove the job from the blockedQueue
                blockedQueue.remove(i);
            }
        }
    }

    //This method is the termination method, that checks if all subqueues are empty and the final job has completed
    public void checkForSystemCompletion(SYSTEM system){
        int a = subqueue1.size();
        int b = subqueue2.size();
        int c = subqueue3.size();
        int d = subqueue4.size();
        int e = system.loader.jobQueue.size();
        int g = blockedQueue.size();

        if(a == 0 && b == 0 && c == 0 && d == 0 && e == 0 && g == 0){
            try{
                writer.close();
                //Call mem_manager.stats() one last time to output the final memory statistics after job termination
                system.mem_manager.stats(system);
                //Exit the virtual machine
                System.exit(0);
            }catch(Exception ex){}
        }
    }
}
