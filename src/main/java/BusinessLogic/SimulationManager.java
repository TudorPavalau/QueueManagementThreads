package BusinessLogic;

import GUI.SimulationFrame;
import Model.Task;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


public class SimulationManager implements Runnable {
    public int timelimit;
    public int numberOfServers;
    public int numberOfTasks;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int minArrivalTime;
    public int maxArrivalTime;

    public SelectionPolicy selectionPolicy;
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks;

    public SimulationManager(SimulationFrame frame, int timelimit, int numberOfServers, int numberOfTasks, int maxProcessingTime, int minProcessingTime,int minArrivalTime,int maxArrivalTime) {
        //initialize the scheduler
        // =>create and start numberOfServers threads
        // =>initialize selection strategy => create strategy
        //initialize frame to display simulation
        // generate numberOfTasks with generateRandomTasks()
        //and store them in generatedTasks

        this.frame = frame;
        this.timelimit = timelimit;
        this.numberOfServers = numberOfServers;
        this.numberOfTasks = numberOfTasks;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
//        System.out.println(this.toString());

        this.selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;
        this.scheduler = new Scheduler(numberOfServers, numberOfTasks);
        this.scheduler.changeStrategy(selectionPolicy);
        generateRandomTasks();
    }

    public void generateRandomTasks() {
        generatedTasks = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < numberOfTasks; i++) {
            int arrivalTime = rand.nextInt(maxArrivalTime-minArrivalTime)+minArrivalTime;
            int processingTime = rand.nextInt(maxProcessingTime-minProcessingTime) + minProcessingTime;
            Task task = new Task(i + 1, arrivalTime, processingTime);
            generatedTasks.add(task);
        }
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
    }


    public  void run() {
        int currentTime = 0;
        double totalWaitingTime = 0;
        int maxHour = 0;
        int maxWaitingTime = 0;
        try {
            int sumProcessingTime = 0;
            int nrProcessedTasks = 0;
            PrintStream out = new PrintStream(new FileOutputStream("Log.txt"));
            while (currentTime < timelimit) {
                List<Task> toDispatch = new ArrayList<>();
                for (Task task : new ArrayList<>(generatedTasks)) {
                    if (task.getArrivalTime() == currentTime) {
                        sumProcessingTime+=task.getInitialServiceTime();
                        int a=scheduler.dispatchTask(task);
                        totalWaitingTime+=a;
                        if(a>maxWaitingTime){
                            maxWaitingTime=a;
                            maxHour=currentTime;
                        }
                        toDispatch.add(task);
                        nrProcessedTasks++;
                    }
                }
                generatedTasks.removeAll(toDispatch);


                frame.update(out,currentTime, generatedTasks, scheduler.getServers());

                    Thread.sleep(1000);
                    currentTime++;
               }
            sumProcessingTime/=nrProcessedTasks;
            totalWaitingTime /= nrProcessedTasks;
            out.println("Average time is: " + totalWaitingTime + "\nPeak hour is " + maxHour +"\n Average Processing time is: " + sumProcessingTime);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public static void main(String[] args){
        SimulationFrame frame = new SimulationFrame();
        frame.setVisible(true);
    }


    @Override
    public String toString() {
        return "SimulationManager{" +
                "timelimit=" + timelimit +
                ", numberOfServers=" + numberOfServers +
                ", numberOfTasks=" + numberOfTasks +
                ", maxProcessingTime=" + maxProcessingTime +
                ", minProcessingTime=" + minProcessingTime +
                ", minArrivalTime=" + minArrivalTime +
                ", maxArrivalTime=" + maxArrivalTime +
                ", selectionPolicy=" + selectionPolicy +
                '}';
    }
}
