package test;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSeed {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSeed2() {
		vash.Seed s = null;
		try {
			s = new vash.Seed("2", null, new ByteArrayInputStream("Foo".getBytes()));
		} catch(NoSuchAlgorithmException e) {
			System.err.println(e.toString());
			System.exit(1);
		} catch(vash.InvalidSaltException e) {
			System.err.println(e.toString());
			System.exit(3);
		} catch(IOException e) {
			System.err.println(e.toString());
			System.exit(3);
		}
		s.nextDouble();
	}
}
