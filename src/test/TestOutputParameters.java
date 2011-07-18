package test;

import junit.framework.Assert;

import org.junit.Test;

import vash.OutputParameters;

public class TestOutputParameters {
	private OutputParameters op;
	
    
	@Test
	public void testGetFilename() {
		op = new vash.OutputParameters("foo.png", 64, 64);
		Assert.assertEquals("foo.png", op.getFilename());
		op = new vash.OutputParameters("-", 64, 64);
		Assert.assertEquals("-", op.getFilename());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetFilenameFail() {
		op = new vash.OutputParameters("foo.bar", 64, 64);
	}
	
	@Test
	public void testGetImageType() {
		op = new vash.OutputParameters("foo.png", 64, 64);
		Assert.assertEquals("png", op.getImageType());
		op = new vash.OutputParameters("foo.bmp", 64, 64);
		Assert.assertEquals("bmp", op.getImageType());
		op = new vash.OutputParameters("foo.jpg", 64, 64);
		Assert.assertEquals("jpeg", op.getImageType());
		op = new vash.OutputParameters("-", 64, 64);
		Assert.assertEquals("png", op.getImageType());
	}

	@Test
	public void testGetWidth() {
		op = new vash.OutputParameters("foo.png", 42, 64);
		Assert.assertEquals(42, op.getWidth());
	}

	@Test
	public void testGetHeight() {
		op = new vash.OutputParameters("foo.png", 64, 42);
		Assert.assertEquals(42, op.getHeight());
	}
}
