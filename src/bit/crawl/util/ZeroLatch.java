package bit.crawl.util;

public class ZeroLatch {
	private int count;

	public ZeroLatch(int count) {
		super();
		this.count = count;
	}

	public synchronized void await() throws InterruptedException {
		while (count > 0) {
			this.wait();
		}
	}

	public synchronized boolean await(long timeout) throws InterruptedException {
		if (count > 0) {
			this.wait(timeout);
		}
		if (count > 0) {
			return false;
		} else {
			return true;
		}
	}

	public synchronized void countUp() {
		count++;
	}

	public synchronized void countDown() {
		count--;
		if (count <= 0) {
			this.notifyAll();
		}
	}

	public synchronized void forceRelease() {
		this.notifyAll();
	}

	public int getCount() {
		return count;
	}

}
