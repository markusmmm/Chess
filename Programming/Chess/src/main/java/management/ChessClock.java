package management;

public class ChessClock {
	private final long GIGA = 1000000000;

	private int playerI = 0;
	private int nPlayers;
	private long[] durations;
	private long bonusTime, bonusTimeLimit;
	private long startTime;
	private long moveStartTime;

	/**
	 *
	 * @param nPlayers
	 * @param bonus
	 * @param bonusLimit
	 */
	public ChessClock(int nPlayers, int duration, double bonus, double bonusLimit) {
		this.nPlayers = nPlayers;
		durations = new long[nPlayers];
		for(int i = 0; i < nPlayers; i++) {
			durations[i] = toNanoTime(duration);
		}

		bonusTime = toNanoTime(bonus);
		bonusTimeLimit = toNanoTime(bonusLimit);

		reset();
	}

	public void reset() {
		startTime = System.nanoTime();
		moveStartTime = startTime;
	}

	public boolean endTurn() {
		long delta = System.nanoTime() - moveStartTime;
		if(delta <= 0) return false;

		if(bonusTimeLimit < 0 || delta <= bonusTimeLimit) {
			durations[playerI] += bonusTime;
		}

		if(++playerI >= nPlayers)
			playerI = 0;

		moveStartTime = System.nanoTime();
		return true;
	}

	/**
	 * 
	 * @param playerIndex
	 */
	public double timeLeft(int playerIndex) {
		long nanoTimeLeft = startTime + durations[playerIndex] - System.nanoTime();
		return fromNanoTime(nanoTimeLeft);
	}

	private long toNanoTime(double time) {
		return Math.round(time) * GIGA;
	}
	private double fromNanoTime(long nanoTime) {
		return nanoTime / (double)GIGA;
	}
}