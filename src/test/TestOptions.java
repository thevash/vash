package test;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestOptions {
	vash.Options opt;
	
	@Before
	public void setUp() throws Exception {
		opt = new vash.Options();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetData() {
		opt.setData("FooBar".getBytes());
		//Assert.assertEquals("FooBar", opt.getData());
		//FIXME: testSetData
	}

	@Test
	public void testSetAlgorithm() {
		opt.setAlgorithm("FooBar");
		Assert.assertEquals("FooBar", opt.getAlgorithm());
	}

	@Test
	public void testSetOutput() {
		opt.setOutput("FooBar");
		Assert.assertEquals("FooBar", opt.getOutput());
	}

	@Test
	public void testSetWidth() {
		opt.setWidth(42);
		Assert.assertEquals(42, opt.getWidth());
	}

	@Test
	public void testSetHeight() {
		opt.setHeight(42);
		Assert.assertEquals(42, opt.getHeight());
	}

	@Test
	public void testSetAnimationMode() {
		//TODO: not yet implemented
	}

	@Test
	public void testSetDuration() {
		//TODO: not yet implemented
	}

	@Test
	public void testSetPeriod() {
		//TODO: not yet implemented
	}

	@Test
	public void testSetFrameRate() {
		//TODO: not yet implemented
	}

	@Test
	public void testShowKnownAlgorithms() {
		vash.Options.showKnownAlgorithms();
	}

	@Test
	public void testOptionsStringArray() {
		String[] toTest = {
				"--algorithm", "1",
				"--data", "data",
				"--output", "output",
				"--width", "42",
				"--height", "24",
		};
		opt = new vash.Options(toTest);
		Assert.assertEquals("1", opt.getAlgorithm());
		// FIXME: test file input
		Assert.assertEquals("output", opt.getOutput());
		Assert.assertEquals(42, opt.getWidth());
		Assert.assertEquals(24, opt.getHeight());

		String[] toTestShort = {
				"-a", "1",
				"-d", "data",
				"-o", "output",
				"-w", "42",
				"-h", "24",
		};
		opt = new vash.Options(toTestShort);
		Assert.assertEquals("1", opt.getAlgorithm());
		// FIXME: test file input
		Assert.assertEquals("output", opt.getOutput());
		Assert.assertEquals(42, opt.getWidth());
		Assert.assertEquals(24, opt.getHeight());
	}
	
}
