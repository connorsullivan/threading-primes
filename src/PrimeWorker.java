import java.util.concurrent.Callable;

public class PrimeWorker implements Callable<Integer> {

	private int threadIndex;
	private int totalThreads;
	private int max;

	public PrimeWorker(int threadIndex, int totalThreads, int max) {
		this.threadIndex = threadIndex;
		this.totalThreads = totalThreads;
		this.max = max;
	}

	@Override
	public Integer call() {

		int primes = 0;

		int start = 3 + (2 * threadIndex);
		int increment = totalThreads * 2;

		for (int i = start; i <= max; i += increment) {
			if (isPrime(i)) {
				primes++;
			}
		}

		return primes;
	}

	private static boolean isPrime(int n) {

		for (int i = 3; i * i <= n; i += 2) {
			if (n % i == 0) {
				return false;
			}
		}

		return true;
	}
}
