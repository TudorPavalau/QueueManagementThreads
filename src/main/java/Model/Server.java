package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server(){
        tasks = new LinkedBlockingDeque<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task task) {
        tasks.add(task);
        waitingPeriod.addAndGet(task.getServiceTime());
    }

    @Override
    public synchronized void run() {
        while (true) {
            try {
                Task task = tasks.peek();
                if (task != null) {
                    task.decrementServiceTime();
                    if(task.getServiceTime() <= 0){
                        tasks.remove();
                    }
                }
                Thread.sleep(1000L);
                waitingPeriod.addAndGet(-1);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    public Task getTask(){
        return tasks.poll();
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public int getWaitingPeriod() {
        int nr=0;
        for(Task task : tasks){
            nr+=task.getServiceTime();
        }
        return nr;
    }

    public int getQueueSize() {
        return tasks.size();
    }
}
