import java.io.*;
import java.util.*;

public class MemoryManager{

    private static int MEMORY_MAX = 0;
    private static int memoryAllocated = 0;

    //Checks if the SYS_LOG File Writer has been initialized
    private static Boolean writerNotInitialized = true;
    private static FileWriter writer;
    private static File SYS_LOG = new File("SYS_LOG.txt");
    private static int jobsDelivered = 0;

    //method that physically allocates the memory
    public void allocate(int size){
        memoryAllocated += size;
    }

    //Method that checks if there is room in memory
    public Boolean canAllocate(int size){
        int total = memoryAllocated + size;
        if(total <= MEMORY_MAX)
            return true;
        else
            return false;
    }

    //Setup is called when SYSTEM first executes, for Phase I setup is hardcoded to 512 by the SYSTEM
    public void setup(int max){
        MEMORY_MAX =  max;
    }


    //release will release the memory units that are allocated to a certain job
    public void release(PCB currentJob){
        int size = currentJob.getJobSize();
        memoryAllocated = memoryAllocated - size;
    }

    //Stats is called every 200 time units by the CPU, I used a File Writer to write to SYS_LOG
    public void stats(SYSTEM system){
        //Check if the File Writer is initialized or not
        if(writerNotInitialized){
            try{
                //If the File Writer is not initialized, then do so and output the SYS_LOG column headers
                writer = new FileWriter(SYS_LOG);
                writer.write(String.format("%-30s %-30s %-30s %-30s %-30s %-30s \n", "ALLOCATED MEMORY UNITS","FREE MEMORY UNITS","JOBS IN JOB QUEUE", "JOBS IN BLOCKED QUEUE", "JOBS IN READY QUEUE", "JOBS DELIVERED"));
                writer.flush();
                writerNotInitialized = false;
            }catch(Exception ex){System.out.println("The SYS_LOG File Writer could not be instantiated.");}
        }

        //Output the relative information in regards to SYS_LOG
        try{
            int numberOFJobsInReadyQueue = system.scheduler.subqueue1.size() + system.scheduler.subqueue2.size() + system.scheduler.subqueue3.size() + system.scheduler.subqueue4.size();
            String firstString  = "" + memoryAllocated;
            String secondString = "" + (MEMORY_MAX - memoryAllocated);
            String thirdString  = "" + system.loader.jobQueue.size();
            String fourthString = "" + system.scheduler.blockedQueue.size();
            String fifthString  = "" + numberOFJobsInReadyQueue;
            String sixthString  = "" + jobsDelivered;

            writer.write(String.format("%-30s %-30s %-30s %-30s %-30s %-30s \n",  firstString, secondString, thirdString, fourthString, fifthString, sixthString));
            writer.flush();
        }catch(Exception ex){}

    }

    //Increment the total number of jobs delivered (For SYS_LOG statistics)
    public void incrementJobsDelivered(){ jobsDelivered++; }
}
