import java.util.concurrent.atomic.AtomicInteger;
public class Score {
	private AtomicInteger missedWords;
	private AtomicInteger caughtWords;
	private AtomicInteger gameScore;
	Score() {
		missedWords=new AtomicInteger();
		caughtWords=new AtomicInteger();
		gameScore=new AtomicInteger();
	}
		
	// all getters and setters must be synchronized
	
	public synchronized int getMissed() {
		return missedWords.get();
	}

	public synchronized int getCaught() {
		return caughtWords.get();
	}
	
	public synchronized int getTotal() {
		return (missedWords.get()+caughtWords.get());
	}

	public synchronized int getScore() {
		return gameScore.get();
	}
	public synchronized void missedWord() {
		missedWords.getAndIncrement();
 	}

	public synchronized void caughtWord(int length) {
		caughtWords.getAndIncrement();
		gameScore.getAndAdd(length);
	}

	public void resetScore() {
		caughtWords.set(0);
		missedWords.set(0);
		gameScore.set(0);
	}
}
