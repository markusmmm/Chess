package management;

public class ChessClock {
	private final long GIGA = 1000000000;

	private int playerI = 0;
	private int nPlayers;
	private long[] durations;
	private long bonusTime, bonusTimeLimit;
	private long startTime;
	private long moveStartTime;

	private ChessClock(ChessClock template) {
		playerI = template.playerI;
		nPlayers = template.nPlayers;
		if(durations != null) durations = template.durations.clone();
		bonusTime = template.bonusTime;
		bonusTimeLimit = template.bonusTimeLimit;
		startTime = template.startTime;
		moveStartTime = template.moveStartTime;
	}

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
		if(timeLeft() <= 0) return false;
		long delta = System.nanoTime() - moveStartTime;

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
	public double timeLeft() {
		return timeLeft(playerI);
	}

	private long toNanoTime(double time) {
		return Math.round(time) * GIGA;
	}
	private double fromNanoTime(long nanoTime) {
		return nanoTime / (double)GIGA;
	}

	@Override
	public ChessClock clone() {
		return new ChessClock(this);
	}
}