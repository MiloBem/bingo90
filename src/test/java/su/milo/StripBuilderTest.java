package su.milo;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StripBuilderTest {

	private StripBuilder builder;

	@BeforeEach
	void setUp() {
		builder = new StripBuilder();
	}

	@Test
	void trackBlanks() {
		List<Integer> blanksInRowBefore = builder.listBlankColumnsInRow(1);
		assertEquals(9, blanksInRowBefore.size());
		assertTrue(blanksInRowBefore.contains(3));

		List<Integer> blanksInColumnBefore = builder.listBlankRowsInColumn(3);
		assertEquals(3, blanksInColumnBefore.size());
		assertTrue(blanksInColumnBefore.contains(1));

		assertNull(builder.readCell(3, 1));
		builder.fillCell(3, 1, 5);
		assertEquals(5, builder.readCell(3, 1));

		List<Integer> blanksInRowAfter = builder.listBlankColumnsInRow(1);
		assertEquals(8, blanksInRowAfter.size());
		assertFalse(blanksInRowAfter.contains(3));

		List<Integer> blanksInColumnAfter = builder.listBlankRowsInColumn(3);
		assertEquals(2, blanksInColumnAfter.size());
		assertFalse(blanksInColumnAfter.contains(1));
	}

	@Test
	void allowToFillColumnsUntilFull() {
		assertTrue(builder.validate().contains("Column 5 has three blanks"));
		assertTrue(builder.canTakeIntoColumn(5));

		builder.fillCell(5, 1, 6);
		assertFalse(builder.validate().contains("Column 5 has three blanks"));
		assertTrue(builder.canTakeIntoColumn(5));

		builder.fillCell(5, 0, 4);
		assertTrue(builder.canTakeIntoColumn(5));

		builder.fillCell(5, 2, 2);
		assertFalse(builder.canTakeIntoColumn(5));
	}

	@Test
	void allowToFillCellsUntil15() {
		for (int i = 0; i < 15; ++i) {
			assertTrue(builder.canTakeIntoColumn(5));
			assertTrue(builder.validate().contains(String.format("The strip has %d numbers instead of 15", i)));
			builder.fillCell(i % 9, i / 9, i);
		}
		assertFalse(builder.canTakeIntoColumn(5));
		assertFalse(builder.validate().contains("numbers instead of 15"));
	}

	@Test
	void sortColumnsWithoutAffectingBlanks() {
		for (int i = 0; i < 15; ++i) {
			builder.fillCell(i * 6 / 10, i % 3, 1 + 6 * i);
		}

		List<Integer> blanksInRowZeroBefore = builder.listBlankColumnsInRow(0);
		String validateBefore = builder.validate();
		assertEquals("\nColumn 1 is not sorted\nColumn 3 is not sorted", validateBefore);

		builder.sortColumns();

		List<Integer> blanksInRowZeroAfter = builder.listBlankColumnsInRow(0);
		String validateAfter = builder.validate();
		assertEquals("", validateAfter);
		assertEquals(blanksInRowZeroAfter, blanksInRowZeroBefore);
	}

	@Test
	void wrongNumbers() {
		String validateBefore = builder.validate();
		assertFalse(validateBefore.contains("Invalid number"));
		assertFalse(validateBefore.contains("wrong column"));

		for (int i = 0; i < 9; ++i) {
			builder.fillCell(i, 1, i);
		}
		builder.fillCell(5, 0, 99);

		String validateAfter = builder.validate();
		assertTrue(validateAfter.contains("Invalid number 0"));
		for (int i = 1; i < 9; ++i) {
			assertTrue(validateAfter.contains(String.format("Number %d in wrong column %d", i, i)));
		}
		assertTrue(validateAfter.contains("Invalid number 99"));
	}

}