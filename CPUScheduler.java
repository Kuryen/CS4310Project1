import java.io.*;
import java.util.*;

class Task {
   int pid, arrivalTime, burstTime, priority, remainingTime;
   int startTime = -1, completionTime, waitingTime, turnaroundTime, responseTime;
}

public class CPUScheduler {
   // List to store all the tasks loaded from the input file
   static List<Task> tasks = new ArrayList<>();
   static int currentTime = 0; // Keeps track of the current time in the simulation

   public static void main(String[] args) throws IOException {
      // Load tasks from input.txt file
      loadTasks("input.txt");
      try (Scanner scanner = new Scanner(System.in)) {
      // Display menu for user to select a scheduling algorithm
         System.out.println("Choose a scheduling algorithm:");
         System.out.println("1. FCFS\n2. SJF\n3. Priority\n4. Round Robin");
         int choice = scanner.nextInt(); // Read user choice

         // Handle the selected scheduling algorithm
         if (choice == 4) { // If Round Robin is chosen
            System.out.print("Enter time quantum: ");
            int timeQuantum = scanner.nextInt(); // Get time quantum input
            roundRobin(timeQuantum); // Call Round Robin scheduling
         } else {
            // Call the corresponding scheduling algorithm based on user choice
            switch (choice) {
                  case 1 -> fcfs(); // FCFS
                  case 2 -> sjf();  // SJF
                  case 3 -> priorityScheduling(); // Priority
            }
         }
   }

      // Print the results after scheduling completes
      printResults();
   }

   // Function to load tasks from input.txt
   static void loadTasks(String filename) throws IOException {
      BufferedReader br = new BufferedReader(new FileReader(filename)); // Open the file
      String line;
      // Read each line and create a Task object
      while ((line = br.readLine()) != null) {
         String[] parts = line.split("\\s+"); // Split line into parts
         Task task = new Task(); // Create a new Task
         task.pid = Integer.parseInt(parts[0]); // Set Process ID
         task.arrivalTime = Integer.parseInt(parts[1]); // Set Arrival Time
         task.burstTime = Integer.parseInt(parts[2]); // Set Burst Time
         task.priority = Integer.parseInt(parts[3]); // Set Priority
         task.remainingTime = task.burstTime; // Initialize Remaining Time
         tasks.add(task); // Add task to the list
      }
      br.close(); // Close the file
   }

   // FCFS scheduling algorithm
   static void fcfs() {
      // Sort tasks by arrival time
      tasks.sort(Comparator.comparingInt(t -> t.arrivalTime));
      // Iterate over each task and simulate execution
      for (Task task : tasks) {
         currentTime = Math.max(currentTime, task.arrivalTime); // Adjust current time
         task.startTime = currentTime; // Set start time
         task.completionTime = currentTime + task.burstTime; // Set completion time
         task.waitingTime = task.startTime - task.arrivalTime; // Calculate waiting time
         task.turnaroundTime = task.completionTime - task.arrivalTime; // Calculate turnaround time
         task.responseTime = task.waitingTime; // Response time is same as waiting time for FCFS
         currentTime += task.burstTime; // Move current time forward
      }
   }

   // SJF scheduling algorithm
   static void sjf() {
      List<Task> availableTasks = new ArrayList<>();
      int completed = 0;

      while (completed < tasks.size()) {
         // Add tasks that have arrived by the current time to the available list
         for (Task task : tasks) {
            if (task.arrivalTime <= currentTime && !availableTasks.contains(task) && task.startTime == -1) {
               availableTasks.add(task);
            }
         }

        if (availableTasks.isEmpty()) {
            currentTime++; // If no task is available, increment time
            continue;
         }

         // Find the task with the shortest burst time
         availableTasks.sort(Comparator.comparingInt(t -> t.burstTime));
         Task currentTask = availableTasks.remove(0); // Execute the shortest job

         // Simulate execution of the selected task
         currentTask.startTime = currentTime;
         currentTask.completionTime = currentTime + currentTask.burstTime;
         currentTask.waitingTime = currentTask.startTime - currentTask.arrivalTime;
         currentTask.turnaroundTime = currentTask.completionTime - currentTask.arrivalTime;
         currentTask.responseTime = currentTask.waitingTime;
         
         currentTime = currentTask.completionTime; // Update current time
         completed++; // Increment the completed task count
      }
   }

   // Non-preemptive Priority Scheduling algorithm
   static void priorityScheduling() {
      // Sort tasks first by arrival time, and then by priority (higher value = higher priority)
      tasks.sort(Comparator.comparingInt((Task t) -> t.arrivalTime).thenComparingInt(t -> -t.priority)); // Negative for descending priority

      // Simulate task execution in the sorted order
      for (Task task : tasks) {
         currentTime = Math.max(currentTime, task.arrivalTime); // Adjust current time
         task.startTime = currentTime; // Set the start time for the task
         task.completionTime = currentTime + task.burstTime; // Calculate completion time
         task.waitingTime = task.startTime - task.arrivalTime; // Calculate waiting time
         task.turnaroundTime = task.completionTime - task.arrivalTime; // Calculate turnaround time
         task.responseTime = task.waitingTime; // Response time is the same as waiting time
         currentTime += task.burstTime; // Move the current time forward
      }
   }

   // RR scheduling algorithm with a given time quantum
   static void roundRobin(int quantum) {
      Queue<Task> queue = new LinkedList<>(tasks); // Create a queue for tasks

      // RR scheduling
      while (!queue.isEmpty()) {
         Task task = queue.poll(); // Get the next task from the queue

         // Set the start time if the task is running for the first time
         if (task.startTime == -1) task.startTime = currentTime;

         // Execute the task for the minimum of time quantum or remaining time
         int execTime = Math.min(quantum, task.remainingTime);
         task.remainingTime -= execTime; // Decrease remaining time
         currentTime += execTime; // Move current time forward

         // If the task still has remaining time, re-add it to the queue
         if (task.remainingTime > 0) {
               queue.add(task);
         } else {
               // If the task is complete, calculate its metrics
               task.completionTime = currentTime;
               task.turnaroundTime = task.completionTime - task.arrivalTime;
               task.waitingTime = task.turnaroundTime - task.burstTime;
               task.responseTime = task.startTime - task.arrivalTime;
         }
      }
   }

   // Function to print the results after scheduling completes
   static void printResults() {
      double totalWait = 0, totalTurnaround = 0, totalResponse = 0;
      int totalBurstTime = 0; // To track the total busy time

      // Calculate the earliest start time and latest completion time
      int firstStartTime = tasks.stream()
                                .mapToInt(task -> task.startTime)
                                .min()
                                .orElse(0); // Earliest start time, or 0 if no tasks
      int lastCompletionTime = tasks.stream()
                                    .mapToInt(task -> task.completionTime)
                                    .max()
                                    .orElse(0); // Latest completion time, or 0 if no tasks

      // Print details for each task and accumulate totals
      for (Task task : tasks) {
         totalWait += task.waitingTime;
         totalTurnaround += task.turnaroundTime;
         totalResponse += task.responseTime;
         totalBurstTime += task.burstTime;

         System.out.printf(
            "PID: %d, Start: %d, Completion: %d, Waiting: %d, Turnaround: %d, Response: %d\n", 
            task.pid, task.startTime, task.completionTime, 
            task.waitingTime, task.turnaroundTime, task.responseTime);
      }

      // Print averages
      System.out.printf("Average Waiting Time: %.2f\n", totalWait / tasks.size());
      System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaround / tasks.size());
      System.out.printf("Average Response Time: %.2f\n", totalResponse / tasks.size());

      // Calculate and print CPU Utilization Rate
      int totalTime = lastCompletionTime - firstStartTime; // Total makespan
      double cpuUtilization = ((double) totalBurstTime / totalTime) * 100;
      System.out.printf("CPU Utilization Rate: %.2f%%\n", cpuUtilization);
   }
}
