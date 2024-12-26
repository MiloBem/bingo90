package su.milo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Map2dTest {

	private Map2d grid;

	@BeforeAll
	static void setUpClass() {
	}

	@BeforeEach
	void setUp() {
		grid = new Map2d();
	}

	@Test
	void testEmpty() {
		assertEquals(0, grid.getCount());
	}

	@Test
	void testOverwrite() {
		grid.set(0, 0, 2);
		assertEquals(1, grid.getCount());
		assertEquals(2, grid.get(0, 0));

		grid.set(0, 0, 3);
		assertEquals(1, grid.getCount());
		assertEquals(3, grid.get(0, 0));
	}

	@Test
	void testFill() {
		for (int i = 0; i < 27; ++i) {
			int n = 11 * i;
			grid.set(n % 9, n / 3, 1 + n % 90);
			assertEquals(i + 1, grid.getCount());
		}
	}

	@Test
	void testSwapBlanks() {
		grid.set(3, 1, 5);
		assertEquals(1, grid.getCount());

		grid.swap(3, 0, 2);

		assertEquals(1, grid.getCount());
		assertNull(grid.get(3, 0));
		assertEquals(5, grid.get(3, 1));
		assertNull(grid.get(3, 2));
	}

	@Test
	void testSwapWithBlank() {
		grid.set(3, 2, 5);
		assertEquals(1, grid.getCount());
		assertNull(grid.get(3, 0));
		assertNull(grid.get(3, 1));
		assertEquals(5, grid.get(3, 2));

		grid.swap(3, 1, 2);

		assertEquals(1, grid.getCount());
		assertNull(grid.get(3, 0));
		assertEquals(5, grid.get(3, 1));
		assertNull(grid.get(3, 2));
	}

	@Test
	void testSwapFull() {
		grid.set(3, 0, 6);
		grid.set(3, 2, 5);
		assertEquals(2, grid.getCount());

		grid.swap(3, 0, 2);

		assertEquals(2, grid.getCount());
		assertEquals(5, grid.get(3, 0));
		assertNull(grid.get(3, 1));
		assertEquals(6, grid.get(3, 2));
	}

	@Test
	void testSortUnsortedColumn() {
		grid.set(3, 0, 6);
		grid.set(3, 2, 5);
		assertEquals(2, grid.getCount());
		assertTrue(grid.unsortedColumn(3));

		grid.sortColumn(3);

		assertEquals(2, grid.getCount());
		assertFalse(grid.unsortedColumn(3));

		assertEquals(5, grid.get(3, 0));
		assertNull(grid.get(3, 1));
		assertEquals(6, grid.get(3, 2));
	}

}