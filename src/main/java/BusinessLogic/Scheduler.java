package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private int maxNOServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNOServers, int maxTasksPerServer) {
        this.maxNOServers = maxNOServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();

        for (int i = 0; i < maxNOServers; i++) {
            Server server = new Server();
            Thread thread = new Thread(server);
            thread.start();
            servers.add(server);
        }
        changeStrategy(SelectionPolicy.SHORTEST_QUEUE);
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ShortestQueueStrategy();
        } else if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ShortestTimeStrategy();
        }
    }

    public int dispatchTask(Task task) {
        return strategy.addTask(this.servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }
}
