package Model;

import java.util.concurrent.atomic.AtomicInteger;

public class Task {
    private int id;
    private int arrivalTime;
    private AtomicInteger serviceTime;
    private int initialServiceTime;

    public Task(int id, int arrivalTime, int processingTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = new AtomicInteger(processingTime);
        this.initialServiceTime = processingTime;
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public int getServiceTime() {
        return serviceTime.get();
    }
    public void decrementServiceTime() {
        serviceTime.decrementAndGet();
    }
    public int getId() {
        return id;
    }
    public int getInitialServiceTime() {
        return initialServiceTime;
    }
    @Override
    public String toString() {
        return "(" + id + ", " + arrivalTime + ", " + serviceTime + "); ";
    }
}
