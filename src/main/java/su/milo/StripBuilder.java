package su.milo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StripBuilder {
	private final Map2d cells = new Map2d();

	void fillCell(int c, int r, Integer value) {
		cells.set(c, r, value);
	}

	Integer readCell(int c, int r) {
		return cells.get(c, r);
	}

	void fillFirstRow(List<List<Integer>> unassigned) {
		for (int c = 0; c < 9; ++c) {
			cells.set(c, 0, unassigned.get(c).remove(0));
		}
	}

	void forceFillRemains(int c, List<List<Integer>> unassigned) {
		List<Integer> blankRowsInColumn = listBlankRowsInColumn(c);
		while (2 * c + cells.getCount() < 15) {
			cells.set(c, blankRowsInColumn.remove(0), unassigned.get(c).remove(0));
		}
	}

	boolean canTakeIntoColumn(int c) {
		return 15 != cells.getCount() && !listBlankRowsInColumn(c).isEmpty();
	}

	void pullDown() {
		pullDown(2, 1, false);
		pullDown(2, 0, false);
		pullDown(1, 0, true);
	}

	private void pullDown(int toIndex, int fromIndex, boolean insist) {
		List<Integer> blanks = listBlankColumnsInRow(toIndex);
		if (4 != blanks.size()) {
			Collections.shuffle(blanks);
			while (4 < blanks.size()) {
				Integer column = blanks.remove(0);
				if (!cells.isBlank(column, fromIndex)) {
					cells.swap(column, fromIndex, toIndex);
				} else if (insist) {
					blanks.add(column);
				}
			}
		}
	}

	void sortColumns() {
		for (int c = 0; c < 9; ++c) {
			cells.sortColumn(c);
		}
	}

	protected List<Integer> listBlankRowsInColumn(int c) {
		List<Integer> blankRows = new LinkedList<>();
		for (int r = 0; r < 3; ++r) {
			if (cells.isBlank(c, r)) {
				blankRows.add(r);
			}
		}
		return blankRows;
	}

	protected List<Integer> listBlankColumnsInRow(int r) {
		List<Integer> blankColumns = new ArrayList<>();
		for (int c = 0; c < 9; ++c) {
			if (cells.isBlank(c, r)) {
				blankColumns.add(c);
			}
		}
		return blankColumns;
	}

	protected String validate() {
		StringBuilder errorMessages = new StringBuilder();
		if (15 != cells.getCount()) {
			errorMessages.append(String.format("\nThe strip has %d numbers instead of 15", cells.getCount()));
		}
		for (int c = 0; c < 9; ++c) {
			if (cells.isBlank(c, 0) && cells.isBlank(c, 1) && cells.isBlank(c, 2)) {
				errorMessages.append(String.format("\nColumn %d has three blanks", c));
			}
			if (cells.unsortedColumn(c)) {
				errorMessages.append(String.format("\nColumn %d is not sorted", c));
			}
			for (int r = 0; r < 3; ++r) {
				Integer value = cells.get(c, r);
				if (value == null || 90 == value && 8 == c) {
					continue;
				}
				if (value < 1 || value > 90) {
					errorMessages.append(String.format("\nInvalid number %d", value));
				} else if (value / 10 != c) {
					errorMessages.append(String.format("\nNumber %d in wrong column %d", value, c));
				}
			}
		}
		for (int r = 0; r < 3; ++r) {
			int blanks = listBlankColumnsInRow(r).size();
			if (4 != blanks) {
				errorMessages.append(String.format("\nRow %d has %d blanks", r, blanks));
			}
		}
		return errorMessages.toString();
	}

	public Integer[][] export() {
		Integer[][] array = new Integer[3][];
		for (int r = 0; r < 3; ++r) {
			Integer[] row = new Integer[9];
			for (int c = 0; c < 9; ++c) {
				row[c] = cells.get(c, r);
			}
			array[r] = row;
		}
		return array;
	}

	@Override
	public String toString() {
		return String.valueOf(cells);// + "\n" + validate();
	}

}
