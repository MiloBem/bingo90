package su.milo;

public class Main {
	public static void main(String[] args) throws ValidationError {
		if (args.length == 2) {
			switch (args[0]) {
				case "r":
					build(Integer.parseInt(args[1]));
					break;
				case "s":
					statistics(Integer.parseInt(args[1]));
					break;
				case "t":
					time(Integer.parseInt(args[1]));
					break;
				default:
					usage();
			}
		} else {
			build(1);
			usage();
		}
	}

	public static void usage() {
		System.out.println("USAGE: mrq_milo OPTION N");
		System.out.println("Options available:");
		System.out.println("r : generate and print N strips");
		System.out.println("s : silently generate N strips, print statistics per cell for debugging");
		System.out.println("t : silently generate N strips, print time statistics, for performance assessment");
	}

	public static void build(int n) throws ValidationError {
		for (int i = 0; i < n; ++i) {
			System.out.println(new StripBuilder().build());
		}
	}

	public static void statistics(int n) throws ValidationError {
		Map2d sum = new Map2d();
		Map2d cnt = new Map2d();

		for (int r = 0; r < 3; ++r) {
			for (int c = 0; c < 9; ++c) {
				cnt.set(c, r, 0);
				sum.set(c, r, 0);
			}
		}

		for (int i = 0; i < n; ++i) {
			Integer[][][] set = new StripBuilder().build().export();
			for (int s = 0; s < 6; ++s) {
				for (int r = 0; r < 3; ++r) {
					for (int c = 0; c < 9; ++c) {
						if (null != set[s][r][c]) {
							sum.set(c, r, set[s][r][c] + sum.get(c, r));
							cnt.set(c, r, 1 + cnt.get(c, r));
						}
					}
				}
			}
		}
		System.out.println("COUNT\n" + cnt);
		System.out.println("SUM\n" + sum);

		Map2d avg = new Map2d();
		for (int r = 0; r < 3; ++r) {
			for (int c = 0; c < 9; ++c) {
				avg.set(c, r, sum.get(c, r) / cnt.get(c, r));
			}
		}
		System.out.println("AVG\n" + avg);
	}

	public static void time(int n) throws ValidationError {
		long before = System.nanoTime();

		for (int i = 0; i < n; ++i) {
			new StripBuilder().build();
		}

		long after = System.nanoTime();
		long diff = (after - before);
		System.out.println("Took " + (diff / 1_000_000) + "ms");
		System.out.println("Avg " + (diff / n / 1000) + "us per strip");
	}

}