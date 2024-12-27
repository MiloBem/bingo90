package su.milo;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class StripBuilderTest {

	@Test
	void smokeTest() {
		try {
			for (int i = 0; i < 10; ++i) {
				List<String> validate = new StripBuilder().build().validate();
				assertTrue(validate.isEmpty(), validate.toString());
			}
		} catch (ValidationError ve) {
			fail(ve.getMessage());
		}
	}

}