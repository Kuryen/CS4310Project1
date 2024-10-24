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
      // Sort tasks by burst time 
      tasks.sort(Comparator.comparingInt(t -> t.burstTime));
      // Reuse the FCFS logic since SJF behaves similarly
      fcfs();
   }

   // Non-preemptive Priority Scheduling algorithm
   static void priorityScheduling() {
      // Sort tasks by arrival time to ensure they are considered in correct order
      tasks.sort(Comparator.comparingInt(t -> t.arrivalTime));
      
      // Create a PriorityQueue to store tasks based on their priority (lower value = higher priority)
      PriorityQueue<Task> readyQueue = new PriorityQueue<>(Comparator.comparingInt(t -> t.priority));
      
      int completedTasks = 0;  // Count of completed tasks
      
      while (completedTasks < tasks.size()) {
         // Add tasks to the ready queue that have arrived by the current time
         for (Task task : tasks) {
            if (task.arrivalTime <= currentTime && !readyQueue.contains(task) && task.startTime == -1) {
                  readyQueue.add(task);  // Add the task to the ready queue
            }
         }

         if (readyQueue.isEmpty()) {
            // If no tasks are ready, move the time forward
            currentTime++;
         } else {
            // Get the task with the highest priority (lowest priority number)
            Task currentTask = readyQueue.poll();
            
            // Set the start time for the task if it is running for the first time
            if (currentTask.startTime == -1) {
                  currentTask.startTime = currentTime;
            }

            // Execute the current task to completion
            currentTime += currentTask.burstTime;
            currentTask.completionTime = currentTime;

            // Calculate metrics for the current task
            currentTask.turnaroundTime = currentTask.completionTime - currentTask.arrivalTime;
            currentTask.waitingTime = currentTask.turnaroundTime - currentTask.burstTime;
            currentTask.responseTime = currentTask.startTime - currentTask.arrivalTime;

            // Increment the completed tasks counter
            completedTasks++;
         }
      }
   }

   // RR scheduling algorithm with a given time quantum
   static void roundRobin(int quantum) {
      Queue<Task> queue = new LinkedList<>(tasks); // Create a queue for tasks

      // Simulate the Round Robin scheduling
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

      // Print details for each task and calculate totals
      for (Task task : tasks) {
         totalWait += task.waitingTime;
         totalTurnaround += task.turnaroundTime;
         totalResponse += task.responseTime;
         System.out.printf(
                  "PID: %d, Start: %d, Completion: %d, Waiting: %d, Turnaround: %d, Response: %d\n",
                  task.pid, task.startTime, task.completionTime,
                  task.waitingTime, task.turnaroundTime, task.responseTime);
      }

      // Print averages of waiting, turnaround, and response times
      System.out.printf("Average Waiting Time: %.2f\n", totalWait / tasks.size());
      System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaround / tasks.size());
      System.out.printf("Average Response Time: %.2f\n", totalResponse / tasks.size());
   }
}
