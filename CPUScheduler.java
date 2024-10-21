import java.io.*;
import java.util.*;

class Task {
   int pid, arrivalTime, burstTime, priority, remainingTime;
   int startTime = -1, completionTime, waitingTime, turnaroundTime, responseTime;
}

public class CPUScheduler {
   static List<Task> tasks = new ArrayList<>();
   static int currentTime = 0;
   
   public static void main(String[] args) throws IOException {
      loadTasks("input.txt");
      try (Scanner scanner = new Scanner(System.in)) {
         System.out.println("Choose a scheduling algorithm:");
         System.out.println("1. FCFS\n2. SJF\n3. Priority\n4. Round Robin");
         int choice = scanner.nextInt();

         if (choice == 4) {
            System.out.print("Enter time quantum: ");
            int timeQuantum = scanner.nextInt();
            roundRobin(timeQuantum);
         } else {
            switch (choice) {
                  case 1 -> fcfs();
                  case 2 -> sjf();
                  case 3 -> priorityScheduling();
            }
         }
      }

      printResults();
   }

   static void loadTasks(String filename) throws IOException {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String line;
      while ((line = br.readLine()) != null) {
         String[] parts = line.split("\\s+");
         Task task = new Task();
         task.pid = Integer.parseInt(parts[0]);
         task.arrivalTime = Integer.parseInt(parts[1]);
         task.burstTime = Integer.parseInt(parts[2]);
         task.priority = Integer.parseInt(parts[3]);
         task.remainingTime = task.burstTime;
         tasks.add(task);
      }
      br.close();
   }

   static void fcfs() {
      tasks.sort(Comparator.comparingInt(t -> t.arrivalTime));
      for (Task task : tasks) {
         currentTime = Math.max(currentTime, task.arrivalTime);
         task.startTime = currentTime;
         task.completionTime = currentTime + task.burstTime;
         task.waitingTime = task.startTime - task.arrivalTime;
         task.turnaroundTime = task.completionTime - task.arrivalTime;
         task.responseTime = task.waitingTime;
         currentTime += task.burstTime;
      }
   }

   static void sjf() {
      tasks.sort(Comparator.comparingInt(t -> t.burstTime));
      fcfs(); // Reuse logic since SJF (non-preemptive) behaves similarly.
   }

   static void priorityScheduling() {
      tasks.sort(Comparator.comparingInt(t -> t.priority));
      fcfs(); // Reuse logic with priority sorting.
   }

   static void roundRobin(int quantum) {
      Queue<Task> queue = new LinkedList<>(tasks);
      while (!queue.isEmpty()) {
         Task task = queue.poll();
         if (task.startTime == -1) task.startTime = currentTime;

         int execTime = Math.min(quantum, task.remainingTime);
         task.remainingTime -= execTime;
         currentTime += execTime;

         if (task.remainingTime > 0) {
               queue.add(task);
         } else {
               task.completionTime = currentTime;
               task.turnaroundTime = task.completionTime - task.arrivalTime;
               task.waitingTime = task.turnaroundTime - task.burstTime;
               task.responseTime = task.startTime - task.arrivalTime;
         }
      }
   }

   static void printResults() {
      double totalWait = 0, totalTurnaround = 0, totalResponse = 0;
      for (Task task : tasks) {
         totalWait += task.waitingTime;
         totalTurnaround += task.turnaroundTime;
         totalResponse += task.responseTime;
         System.out.printf("PID: %d, Start: %d, Completion: %d, Waiting: %d, Turnaround: %d, Response: %d\n",
                  task.pid, task.startTime, task.completionTime, task.waitingTime, task.turnaroundTime, task.responseTime);
      }
      System.out.printf("Average Waiting Time: %.2f\n", totalWait / tasks.size());
      System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaround / tasks.size());
      System.out.printf("Average Response Time: %.2f\n", totalResponse / tasks.size());
   }
}
