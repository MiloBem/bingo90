package su.milo;

import java.util.HashMap;
import java.util.Map;

public class Map2d {

	record Coordinate(int c, int r) {

	}

	private int count = 0;

	private final Map<Coordinate, Integer> cells = new HashMap<>();

	int getCount() {
		return count;
	}

	boolean isBlank(int c, int r) {
		return null == get(c, r);
	}

	Integer get(int c, int r) {
		return cells.get(new Coordinate(c, r));
	}

	void set(int c, int r, Integer value) {
		Coordinate key = new Coordinate(c, r);
		if (null != value && null == cells.get(key)) {
			++count;
		} else if (null == value && null != cells.get(key)) {
			--count;
		}
		cells.put(key, value);
	}

	void swap(int c, int r1, int r2) {
		Coordinate from = new Coordinate(c, r1);
		Coordinate to = new Coordinate(c, r2);
		Integer carry = cells.get(to);

		cells.put(to, cells.get(from));
		cells.put(from, carry);
	}

	boolean unsortedColumn(int c) {
		return unsortedColumn(c, 0, 1) || unsortedColumn(c, 1, 2) || unsortedColumn(c, 0, 2);
	}

	void sortColumn(int c) {
		if (!isBlank(c, 1)) {
			bubble(c, 0, 1);
			bubble(c, 1, 2);
			bubble(c, 0, 1);
		} else {
			bubble(c, 0, 2);
		}
	}

	private boolean unsortedColumn(int c, int r1, int r2) {
		return !isBlank(c, r1) && !isBlank(c, r2) && get(c, r1) > get(c, r2);
	}

	private void bubble(int c, int r1, int r2) {
		if (unsortedColumn(c, r1, r2)) {
			swap(c, r1, r2);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("----------------------------------------------");
		for (int r = 0; r < 3; ++r) {
			builder.append("\n|");
			for (int c = 0; c < 9; ++c) {
				Integer value = get(c, r);
				if (null == value) {
					builder.append("    |");
				} else {
					builder.append(String.format("%3d |", value));
				}
			}
			builder.append("\n----------------------------------------------");
		}
		return builder.toString();
	}

}
