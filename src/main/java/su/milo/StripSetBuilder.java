package su.milo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StripSetBuilder {

	private final List<StripBuilder> strips = Stream.generate(StripBuilder::new).limit(6).toList();
	private final List<List<Integer>> unassignedNumbersPerColumn = initializeUnassignedNumbers();

	StripSetBuilder build() throws ValidationError {
		assignNumbersToCards();
		for (StripBuilder strip : strips) {
			strip.pullDown();
			strip.sortColumns();
		}
		List<String> errors = validate();
		if (!errors.isEmpty()) {
			throw new ValidationError(errors);
		}
		return this;
	}

	private void assignNumbersToCards() {
		for (StripBuilder strip : strips) {
			strip.fillFirstRow(unassignedNumbersPerColumn);
		}
		Random random = new Random();
		for (int c = 8; c >= 0; --c) {

			for (StripBuilder strip : strips) {
				strip.forceFillRemains(c, unassignedNumbersPerColumn);
			}

			List<Integer> thisColumn = unassignedNumbersPerColumn.get(c);
			List<StripBuilder> unfilled = canTakeIntoColumn(c);
			while (!thisColumn.isEmpty()) {
				int nextCard = random.nextInt(unfilled.size());
				StripBuilder strip = unfilled.get(nextCard);
				strip.fillCell(c, strip.listBlankRowsInColumn(c).get(0), thisColumn.remove(0));
				if (!strip.canTakeIntoColumn(c)) {
					unfilled.remove(strip);
				}
			}
		}
	}

	private List<StripBuilder> canTakeIntoColumn(int c) {
		return strips.stream().filter(strip -> strip.canTakeIntoColumn(c)).collect(Collectors.toCollection(LinkedList::new));
	}

	private static List<List<Integer>> initializeUnassignedNumbers() {
		List<List<Integer>> unassigned = new ArrayList<>();
		unassigned.add(buildColumnRange(1, 9));
		for (int i = 1; i < 8; ++i) {
			unassigned.add(buildColumnRange(10 * i, 10 * i + 9));
		}
		unassigned.add(buildColumnRange(80, 90));
		return unassigned;
	}

	private static List<Integer> buildColumnRange(int a, int z) {
		List<Integer> list = IntStream.rangeClosed(a, z).boxed().collect(Collectors.toCollection(LinkedList::new));
		Collections.shuffle(list);
		return list;
	}

	List<String> validate() {
		List<String> validationErrors = strips.stream()
				.map(StripBuilder::validate)
				.filter(stripErrors -> !stripErrors.isEmpty())
				.collect(Collectors.toCollection(LinkedList::new));
		for (int c = 0; c < 9; ++c) {
			HashSet<Integer> numbersInColumn = new HashSet<>();
			for (StripBuilder strip : strips) {
				for (int r = 0; r < 3; ++r) {
					Integer value = strip.readCell(c, r);
					if (null != value) {
						if (numbersInColumn.contains(value)) {
							validationErrors.add(String.format("Number %d duplicated", value));
						} else {
							numbersInColumn.add(value);
						}
					}
				}
			}
			int size = numbersInColumn.size();
			switch (c) {
				case 0:
					if (9 != size) {
						validationErrors.add(String.format("Column %d has %d unique numbers instead of 9", c, size));
					}
					break;
				case 8:
					if (11 != size) {
						validationErrors.add(String.format("Column %d has %d unique numbers instead of 11", c, size));
					}
					break;
				default:
					if (10 != size) {
						validationErrors.add(String.format("Column %d has %d unique numbers instead of 10", c, size));
					}
					break;
			}
		}
		return validationErrors;
	}

	public Integer[][][] export() {
		return strips.stream().map(StripBuilder::export).toArray(Integer[][][]::new);
	}

	@Override
	public String toString() {
		boolean ok = true;
		StringBuilder stringBuilder = new StringBuilder("\nCard set");
		for (StripBuilder strip : strips) {
			stringBuilder.append("\n").append(strip);
			String errors = strip.validate();
			if (!errors.isEmpty()) {
				ok = false;
				stringBuilder.append(errors);
			}
		}
		if (!ok) {
			stringBuilder.append("\nValidation errors! Inspect strips for details");
		}
		return stringBuilder.toString();
	}
}
