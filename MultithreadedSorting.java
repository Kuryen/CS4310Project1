import java.util.Arrays;

public class MultithreadedSorting {
   static int[] arr = {7, 12, 19, 3, 18, 4, 2, 6, 15, 8};
   static int[] sortedArr = new int[arr.length];

   public static void main(String[] args) throws InterruptedException {
      int mid = arr.length / 2;

      // Creating sorting threads
      Thread sortingThread1 = new Thread(() -> sort(0, mid));
      Thread sortingThread2 = new Thread(() -> sort(mid, arr.length));

      // Start both sorting threads
      sortingThread1.start();
      sortingThread2.start();

      // Wait for both threads to finish
      sortingThread1.join();
      sortingThread2.join();

      // Create a merge thread to merge the sorted halves
      Thread mergeThread = new Thread(() -> merge(0, mid, arr.length));
      mergeThread.start();
      mergeThread.join();

      // Print the sorted array
      System.out.println("Sorted Array: " + Arrays.toString(sortedArr));
   }

   // Sort function using Arrays.sort()
   static void sort(int start, int end) {
      Arrays.sort(arr, start, end);
   }

   // Merge function to merge two sorted halves
   static void merge(int start, int mid, int end) {
      int i = start, j = mid, k = 0;

      while (i < mid && j < end) {
         if (arr[i] <= arr[j]) {
               sortedArr[k++] = arr[i++];
         } else {
               sortedArr[k++] = arr[j++];
         }
      }

      while (i < mid) {
         sortedArr[k++] = arr[i++];
      }

      while (j < end) {
         sortedArr[k++] = arr[j++];
      }
   }
}
