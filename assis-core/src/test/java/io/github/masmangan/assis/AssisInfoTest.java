package io.github.masmangan.assis;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AssisInfoTest {

	@Test
	void testVersionOrDev() {
		String actual = AssisInfo.versionOrDev();
		assertNotNull(actual);
	}

}
