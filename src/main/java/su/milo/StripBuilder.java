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

public class StripBuilder {

	private final List<TicketBuilder> tickets = Stream.generate(TicketBuilder::new).limit(6).toList();
	private final List<List<Integer>> unassignedNumbersPerColumn = initializeUnassignedNumbers();

	StripBuilder build() throws ValidationError {
		assignNumbersToTickets();
		for (TicketBuilder ticket : tickets) {
			ticket.pullDown();
			ticket.sortColumns();
		}
		List<String> errors = validate();
		if (!errors.isEmpty()) {
			throw new ValidationError(errors);
		}
		return this;
	}

	private void assignNumbersToTickets() {
		for (TicketBuilder ticket : tickets) {
			ticket.fillFirstRow(unassignedNumbersPerColumn);
		}
		Random random = new Random();
		for (int c = 8; c >= 0; --c) {

			for (TicketBuilder ticket : tickets) {
				ticket.forceFillRemains(c, unassignedNumbersPerColumn);
			}

			List<Integer> thisColumn = unassignedNumbersPerColumn.get(c);
			List<TicketBuilder> unfilled = canTakeIntoColumn(c);
			while (!thisColumn.isEmpty()) {
				int nextTicketIndex = random.nextInt(unfilled.size());
				TicketBuilder ticket = unfilled.get(nextTicketIndex);
				ticket.fillCell(c, ticket.listBlankRowsInColumn(c).get(0), thisColumn.remove(0));
				if (!ticket.canTakeIntoColumn(c)) {
					unfilled.remove(ticket);
				}
			}
		}
	}

	private List<TicketBuilder> canTakeIntoColumn(int c) {
		return tickets.stream().filter(ticket -> ticket.canTakeIntoColumn(c)).collect(Collectors.toCollection(LinkedList::new));
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
		List<String> validationErrors = tickets.stream()
				.map(TicketBuilder::validate)
				.filter(ticketErrors -> !ticketErrors.isEmpty())
				.collect(Collectors.toCollection(LinkedList::new));
		for (int c = 0; c < 9; ++c) {
			HashSet<Integer> numbersInColumn = new HashSet<>();
			for (TicketBuilder ticket : tickets) {
				for (int r = 0; r < 3; ++r) {
					Integer value = ticket.readCell(c, r);
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
		return tickets.stream().map(TicketBuilder::export).toArray(Integer[][][]::new);
	}

	@Override
	public String toString() {
		boolean ok = true;
		StringBuilder stringBuilder = new StringBuilder("\nTicket strip");
		for (TicketBuilder ticket : tickets) {
			stringBuilder.append("\n").append(ticket);
			String errors = ticket.validate();
			if (!errors.isEmpty()) {
				ok = false;
				stringBuilder.append(errors);
			}
		}
		if (!ok) {
			stringBuilder.append("\nValidation errors! Inspect tickets for details");
		}
		return stringBuilder.toString();
	}
}
