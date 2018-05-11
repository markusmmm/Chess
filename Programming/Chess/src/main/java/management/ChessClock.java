package management;

public class ChessClock {
	private final long GIGA = 1000000000;

	private int nPlayers;
	private long[] durations = new long[] {};
	private long bonusTime;
	private long startTime;
	private long moveStartTime;

	private ChessClock(ChessClock template) {
		nPlayers = template.nPlayers;
		if(durations != null) durations = template.durations.clone();
		bonusTime = template.bonusTime;
		startTime = template.startTime;
		moveStartTime = template.moveStartTime;
	}

	/**
	 *
	 * @param nPlayers
	 * @param bonus
	 */
	public ChessClock(int nPlayers, int duration, double bonus) {
		this.nPlayers = nPlayers;
		durations = new long[nPlayers];
		for(int i = 0; i < nPlayers; i++) {
			durations[i] = toNanoTime(duration);
		}

		bonusTime = toNanoTime(bonus);

		reset();
	}

	public void reset() {
		startTime = System.nanoTime();
		moveStartTime = startTime;
	}

	public boolean endTurn(int playerI) {
		if(timeLeft(playerI) <= 0) return false;

		durations[playerI] += bonusTime;
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

	private String nanoToString(long nanoTime) {
		double time = fromNanoTime(nanoTime);
		int mins = (int)Math.floor(time / 60);
		int secs = (int)Math.round(time % 60);

		return mins + ":" + secs;
	}

	@Override
	public String toString() {
		String str = "|\t";
		for(long duration : durations)
			str += nanoToString(duration) + "\t|";

		return str;
	}

	@Override
	public ChessClock clone() {
		return new ChessClock(this);
	}
}