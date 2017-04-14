import java.util.*;
import java.io.*;

public class Loader{

    //File Reader to parse the data file
    private BufferedReader fileReader;
    LinkedList<String[]> jobQueue = new LinkedList<String[]>();

    //Create a loader around a datafile that is inputted into the Loader constructor
    public Loader(String fileName){
        try{
            this.fileReader = new BufferedReader(new FileReader(fileName));
        } catch(Exception ex) {System.out.println("Could not find data file.");}
    }

    /*
        This method is called when jobs need to be loaded from the inbound jobs (datafile)
        i.e:
            1. The system was just started and initial jobs need to be loaded
            2. The JobQueue is empty and more need to be loaded into memory or the job queue
    */
    public void load(SYSTEM system){
        boolean finished = false;

        while(!finished){
            try{
                String[] split = {};
                String newS = "";
                String s = "";
                //Only pull jobs from disk if job queue is empty
                if(jobQueue.peek() == null){
                        s = fileReader.readLine();
                        newS = s.trim();
                        split = newS.split("\\s+");
                        if(Integer.parseInt(split[0]) == 0)
                            break;
                }
                //Pull from the Job Queue since it is not empty
                else{
                    split = jobQueue.remove();
                    if(Integer.parseInt(split[0]) == 0)
                        break;
                }
                long arrivalTime = system.clock.getTime();
                int numberOfCPUBursts = 0;

                //Create an integer array of the required CPU bursts
                int[] cpuBursts = new int[split.length-2];

                //Populate the new int array with corresponding values
                for(int i = 2; i < split.length; i++){
                    numberOfCPUBursts++;
                    cpuBursts[i-2] = Integer.parseInt(split[i]);
                }

                //check if there are enough memory units to allocate the space for this job and also a free PCB slot
                int memoryUnitsNeeded = Integer.parseInt(split[1]);
                if((system.mem_manager.canAllocate(memoryUnitsNeeded) == true) && system.scheduler.canCreatePCB()){
                    system.mem_manager.allocate(memoryUnitsNeeded);
                    system.scheduler.setup(system, split, numberOfCPUBursts, arrivalTime, cpuBursts);
                }
                else{
                    jobQueue.addFirst(split);
                    finished = !finished;
                }
            }catch(Exception ex){break;}
        }
    }

    //Method to print the input file, used for debugging purposes
    public void printDataFile(){
        String s = "";
        try{
            while((s = fileReader.readLine()) != null){
                System.out.println(s);
            }
        }catch(Exception x){}
    }
}
