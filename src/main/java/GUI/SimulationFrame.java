package GUI;

import BusinessLogic.SelectionPolicy;
import BusinessLogic.ShortestQueueStrategy;
import Model.Server;
import Model.Task;
import BusinessLogic.SimulationManager;
import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.util.List;

public class SimulationFrame extends JFrame {
    private JTextArea logArea;
    private JLabel timeLabel;

    private JTextField timeLimitField;
    private JTextField numServersField;
    private JTextField numTasksField;
    private JTextField maxProcessingTimeField;
    private JTextField minProcessingTimeField;
    private JTextField minArrivalTimeField;
    private JTextField maxArrivalTimeField;

    private JButton startButton;
    private JButton changeStrategyButton;

    private SimulationManager simulationManager;
    private int nrOfClicks=0;

    public SimulationFrame() {
        setTitle("Task Scheduling Simulation");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Time label at the top
        timeLabel = new JLabel("Current Time: 0");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(timeLabel, BorderLayout.NORTH);

        // Log area in the center
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(9, 2, 10, 10));

        inputPanel.add(new JLabel("Time Limit:"));
        timeLimitField = new JTextField("60");
        inputPanel.add(timeLimitField);

        inputPanel.add(new JLabel("Number of Servers:"));
        numServersField = new JTextField("2");
        inputPanel.add(numServersField);

        inputPanel.add(new JLabel("Number of Tasks:"));
        numTasksField = new JTextField("4");
        inputPanel.add(numTasksField);

        inputPanel.add(new JLabel("Max Processing Time:"));
        maxProcessingTimeField = new JTextField("7");
        inputPanel.add(maxProcessingTimeField);

        inputPanel.add(new JLabel("Min Processing Time:"));
        minProcessingTimeField = new JTextField("2");
        inputPanel.add(minProcessingTimeField);

        inputPanel.add(new JLabel("Min Arrival Time:"));
        minArrivalTimeField = new JTextField("2");
        inputPanel.add(minArrivalTimeField);

        inputPanel.add(new JLabel("Max Arrival Time:"));
        maxArrivalTimeField = new JTextField("30");
        inputPanel.add(maxArrivalTimeField);

        startButton = new JButton("Start Simulation");
        startButton.addActionListener(e -> startSimulation());
        inputPanel.add(startButton);

        changeStrategyButton = new JButton("Change Strategy");
        changeStrategyButton.addActionListener(e -> changeStrategy());
        inputPanel.add(changeStrategyButton);

        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    private void changeStrategy() {
        this.nrOfClicks++;
        SelectionPolicy policy;
        if(this.nrOfClicks%2==1){
            policy = SelectionPolicy.SHORTEST_QUEUE;
        }
        else {
            policy= SelectionPolicy.SHORTEST_TIME;
        }
        this.simulationManager.getScheduler().changeStrategy(policy);
    }
    private void startSimulation() {
        try {
            int timeLimit = Integer.parseInt(timeLimitField.getText());
            int numServers = Integer.parseInt(numServersField.getText());
            int numTasks = Integer.parseInt(numTasksField.getText());
            int maxProcessingTime = Integer.parseInt(maxProcessingTimeField.getText());
            int minProcessingTime = Integer.parseInt(minProcessingTimeField.getText());
            int minArrivalTime = Integer.parseInt(minArrivalTimeField.getText());
            int maxArrivalTime = Integer.parseInt(maxArrivalTimeField.getText());

            if (!(timeLimit > 0 && numServers > 0 && numTasks > 0 && maxProcessingTime > 0 && minProcessingTime > 0 && maxArrivalTime > 0 && minArrivalTime > 0)) {
                JOptionPane.showMessageDialog(this, "Please enter valid positive values for all fields.");
                return;
            }

            this.simulationManager = new SimulationManager(this, timeLimit, numServers, numTasks, maxProcessingTime, minProcessingTime,minArrivalTime,maxArrivalTime);
            Thread simulationThread = new Thread(simulationManager);
            simulationThread.start();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integer values.");
        }
    }

    public void update(PrintStream file, int currentTime, List<Task> tasks, List<Server> servers) {
        timeLabel.setText("Current Time: " + currentTime);
        StringBuilder queueBuilder = new StringBuilder();
        for (Task task : tasks) {
            queueBuilder.append(task.toString());
        }

        StringBuilder logBuilder = new StringBuilder();
        for (int i = 0; i < servers.size(); i++) {
            Server server = servers.get(i);
            logBuilder.append("Server ").append(i + 1).append(": ");

            if (server.getQueueSize() == 0) {
                logBuilder.append("idle");
            } else {
                logBuilder.append("[ ");
                for (Task task : server.getTasks()) {
                    logBuilder.append(task.toString());
                }
                logBuilder.append("] ");
            }
            logBuilder.append("\n");
        }

        logArea.setText(queueBuilder.toString() + "\n" + logBuilder.toString());
        file.println("Time: " +  currentTime + "\nWaiting Clients: " + queueBuilder.toString() + "\n" + logBuilder.toString());
    }
}
