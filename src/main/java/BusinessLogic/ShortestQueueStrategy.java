package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy {
    @Override
    public int addTask(List<Server> servers, Task task) {
        Server bestServer = null;
        int minQueueSize = Integer.MAX_VALUE;

        for (Server server : servers) {
            int queueSize = server.getQueueSize();
            if (queueSize < minQueueSize) {
                minQueueSize = queueSize;
                bestServer = server;
            }
        }

        if (bestServer != null) {
            bestServer.addTask(task);
        }
        return bestServer.getWaitingPeriod();
    }

}
