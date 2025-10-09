package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ShortestTimeStrategy implements Strategy {
    @Override
    public int  addTask(List<Server> servers, Task t) {
        Server bestServer = null;
        int minWaitingTime = Integer.MAX_VALUE;

        for (Server server : servers) {
            int waitingTime = server.getWaitingPeriod();
            if (waitingTime < minWaitingTime) {
                minWaitingTime = waitingTime;
                bestServer = server;
            }
        }
        if (bestServer != null) {
            bestServer.addTask(t);
        }
        return bestServer.getWaitingPeriod();
    }

}
