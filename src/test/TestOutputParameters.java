package test;

import junit.framework.Assert;

import org.junit.Test;

import vash.OutputParameters;

public class TestOutputParameters {
	private OutputParameters op;
	
    
	@Test
	public void testGetFilename() {
		op = new vash.OutputParameters("foo.png", 64, 64);
		Assert.assertTrue(op.getFilename().equals("foo.png"));
		op = new vash.OutputParameters("-", 64, 64);
		Assert.assertTrue(op.getFilename().equals("-"));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetFilenameFail() {
		op = new vash.OutputParameters("foo.bar", 64, 64);
	}
	
	@Test
	public void testGetImageType() {
		op = new vash.OutputParameters("foo.png", 64, 64);
		Assert.assertTrue(op.getImageType().equals("png"));
		op = new vash.OutputParameters("foo.bmp", 64, 64);
		Assert.assertTrue(op.getImageType().equals("bmp"));
		op = new vash.OutputParameters("foo.jpg", 64, 64);
		Assert.assertTrue(op.getImageType().equals("jpeg"));
		op = new vash.OutputParameters("-", 64, 64);
		Assert.assertTrue(op.getImageType().equals("png"));
	}

	@Test
	public void testGetWidth() {
		op = new vash.OutputParameters("foo.png", 42, 64);
		Assert.assertTrue(op.getWidth() == 42);
	}

	@Test
	public void testGetHeight() {
		op = new vash.OutputParameters("foo.png", 64, 42);
		Assert.assertTrue(op.getHeight() == 42);
	}
}
