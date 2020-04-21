import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Controller {

	public static void main(String[] args) {

		KeyboardInputClass kb = new KeyboardInputClass();

		while (true) {

			int limit = kb.getInteger("Find primes up to ? (press ENTER alone to exit): ", 2, Integer.MAX_VALUE, 1);

			if (limit < 2) {
				System.exit(0);
			}

			int defaultThreads = Runtime.getRuntime().availableProcessors();

			int threads = kb.getInteger("Specify a number of threads to use (default=" + defaultThreads + "): ", 1, 1024, defaultThreads);

			long start = System.currentTimeMillis();

			int totalPrimes = start(threads, limit);

			double elapsed = (System.currentTimeMillis() - start) / 1000.0;

			System.out.printf("Found %s prime(s) up to %s in %.3f second(s).\n\n",
					NumberFormat.getNumberInstance(Locale.US).format(totalPrimes),
					NumberFormat.getNumberInstance(Locale.US).format(limit), elapsed);
		}
	}

	private static int start(int threads, int limit) {

		// 2 is the first prime number, so any max less than 2 should have no primes.
		if (limit < 2) {
			return 0;
		}

		// The workers will start checking numbers from 3 upwards,
		// so account for 2 as a prime number
		// by starting the total at 1 instead of 0.
		int totalPrimes = 1;

		// Create the workers that will asynchronously compute the result
		List<PrimeWorker> tasks = new ArrayList<>(threads);

		for (int i = 0; i < threads; i++) {
			tasks.add(new PrimeWorker(i, threads, limit));
		}

		ExecutorService pool = null;

		try {

			// Create an ExecutorService to manage these workers
			pool = Executors.newFixedThreadPool(threads);

			// Start the workers
			List<Future<Integer>> results = pool.invokeAll(tasks);

			// Get the partial result from each worker
			for (Future<Integer> result : results) {
				totalPrimes += result.get();
			}

		} catch (InterruptedException | ExecutionException e) {

			e.printStackTrace();
			System.exit(1);

		} finally {
			if (pool != null) {
				pool.shutdown();
			}
		}

		return totalPrimes;
	}
}
