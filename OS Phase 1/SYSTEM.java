import java.util.*;
import java.io.*;

public class SYSTEM{
    /*
        PATRICK GODDARD
        CS 4323
        Operating Systems Program Phase I
        March 22nd, 2017

        Global Variables:
            Within Class SYSTEM:
                system: The current specific instance of SYSTEM in which all class instances communicate through
                loader: The current instance of loader, which loads file from the input file and stores them on disk and calls the scheduler
                mem_manager: The current instance of memory manager, which handles memory allocation and deallocation and whether jobs can be loaded into memory
                scheduler: The current instance of Scheduler, which handles the MLFQ, Blocked Queue, and handling process movement through the four subqueues
                processor: The current instance of CPU which handles running the current process, and returning status codes which tell the System what method of the Scheduler to call
                clock: The current instance of Clock, which handles the global system time and is incremented only by CPU run time or idle time
            Within Class Scheduler:
                currentJob: Holds the current PCB of the job that is running
                quantum: Hols the current allowed time slice for the job that is currently running
                subqueue1: SubQueue1 of the MLFQ
                subqueue2: SubQueue2 of the MLFQ
                subqueue3: SubQueue3 of the MLFQ
                subqueue4: SubQueue4 of the MLFQ
                blockedQueue: The blockedQueue which holds jobs that are waiting in I/O
            Within Class Loader:
                jobQueue: Serves as 'Disk' for jobs that are unable to be currently loaded into memory
    */

    private int quantum;
    private boolean finalBurst;
    private boolean demotionAfterQuantum;

    Loader loader;
    MemoryManager mem_manager;
    Scheduler scheduler;
    CPU processor;
    SystemClock clock;

    //Constructor for a new instance of SYSTEM, which has all of the necessary components
    public SYSTEM(Loader loader, MemoryManager mem_manager, Scheduler scheduler, CPU processor, SystemClock clock){
        this.loader = loader;
        this.mem_manager = mem_manager;
        this.scheduler = scheduler;
        this.processor = processor;
        this.clock = clock;
    }

    public static void main(String args[]){
        //Create instances of each of the System parts
        SystemClock clock = new SystemClock(0);
        Loader loader = new Loader(args[0]);
        MemoryManager mem_manager = new MemoryManager();
        //The memory is fixed for Phase I, so call setup memory with 512 units
        mem_manager.setup(512);
        Scheduler scheduler = new Scheduler();
        CPU processor = new CPU();

        //Put all of the instances into a constructor to build an abstract 'System'
        SYSTEM system = new SYSTEM(loader, mem_manager, scheduler, processor, clock);

        //Instantiate the FileWrite for the JOB_LOG file
        scheduler.startFileWrite();

        //Initially load as many jobs as possible into memory
        loader.load(system);

        /*
            Use an inifite loop that will only terminate when all jobs are complete
            i.e: No jobs left in the file, no jobs in the subqueues, no jobs in the JobQueue
        */
        while(true){
            //Have scheduler check the Blocked Queue to see if any jobs have completed their respective I/O
            scheduler.checkBlockedQueue(system);
            //Call the scheduler to decide which job will be ran next
            scheduler.dispatch(system);

            int returnStatus = 0;
            //Load the job onto the processor if there is a job ready, expecting a return status value for when control returns to the system
            //If there is not a job ready (i.e all jobs in blockedQueue), then have the processor remain idle
            if(scheduler.currentJob != null)
                returnStatus = processor.run(system, scheduler.currentJob, scheduler.quantum, scheduler.currentJob.getFinalBurst(), scheduler.currentJob.getDemotionAfterQuantum());
            else
                processor.idle(system);

            //If the status of the job upon completion is a request for I/O then have the scheduler add the process to the Blocked Queue
            if(returnStatus == 1){
                scheduler.blocked(system, false);
                scheduler.currentJob = null;
            }
            //If the status of the job upon completion is to remain in the same subqueue and not require I/O, then add it back to the same subqueue
            else if(returnStatus == 2){
                scheduler.readyUp();
            }
            //If the status of the job upon completion is to not require I/O, but has used all of its turns for the subqueue it was in, then demote the process
            else if(returnStatus == 3){
                scheduler.demoteAndReadyUp();
            }
            //If the status of the job upon completion is a request for I/O DURING the time slice, then add to Blocked Queue and reset subqueue turns
            else if(returnStatus == 4){
                scheduler.blocked(system, true);
                scheduler.currentJob = null;
            }
            //If the status of the job upon completion is termination then print statistics and release memory. Call the loader to load the next job to use the now free memory. Also decrement the active PCB number, increase number of jobs delivered, and call scheduler.stats()
            else if(returnStatus == 5){
                long exitTime = system.clock.getTime();
                scheduler.stats(system, exitTime);
                scheduler.decrementPCBNum();
                mem_manager.release(scheduler.currentJob);
                scheduler.currentJob = null;
                mem_manager.incrementJobsDelivered();
                loader.load(system);
            }

            //Call the scheduler to see if all subqueues, the job queue, and the blocked queue are empty.
            scheduler.checkForSystemCompletion(system);
        }
    }
}
