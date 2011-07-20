/*
 * Copyright 2011, Zettabyte Storage LLC
 * 
 * This file is part of Vash.
 * 
 * Vash is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vash is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with Vash.  If not, see <http://www.gnu.org/licenses/>.
 */
package test.operation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import vash.ImageParameters;
import vash.Options;
import vash.Output;
import vash.operation.Absolute;
import vash.operation.Add;
import vash.operation.ColorNode;
import vash.operation.Const;
import vash.operation.Divide;
import vash.operation.Exponentiate;
import vash.operation.Flower;
import vash.operation.Invert;
import vash.operation.LinearGradient;
import vash.operation.Modulus;
import vash.operation.Multiply;
import vash.operation.OperationNode;
import vash.operation.PolarTheta;
import vash.operation.RGB_Space;
import vash.operation.RadialGradient;
import vash.operation.Sinc;
import vash.operation.Sine;
import vash.operation.Spiral;
import vash.operation.Squircle;


public class TestOperationIntegration {
	private Options opt;
	private ImageParameters ip;
	
	@Before
	public void setUp() throws Exception {
		this.opt = new Options();
		this.opt.setWidth(128);
		this.opt.setHeight(128);
		this.ip = new ImageParameters(this.opt.getWidth(), this.opt.getHeight());
	}
	
	@After
	public void tearDown() throws Exception {
		
	}

	public static double compare(byte[] actual, int w, int h, String goal_in, String diff_out) {
		// read the expected result
		BufferedImage img = null;
		File fp = new File(goal_in);
		try {
		    img = ImageIO.read(fp);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unknown test");
		}
		Assert.assertEquals(img.getWidth(), w);
		Assert.assertEquals(img.getHeight(), h);
	
		// track total error in the image
		int error = 0;
		
		// compute the image difference in case we need it later
		byte[] diff = new byte[w * h * 3];
	
		// compare each pixel
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				int expect_clr = img.getRGB(x, y);
				
				int expect_b = (expect_clr >> 0) & 0xFF;
				int expect_g = (expect_clr >> 8) & 0xFF;
				int expect_r = (expect_clr >> 16) & 0xFF;
				int expect_a = (expect_clr >> 24) & 0xFF;
				Assert.assertEquals(expect_a, 255);
				
				int actual_b = actual[(y * w + x) * 3 + 0] & 0xFF;
				int actual_g = actual[(y * w + x) * 3 + 1] & 0xFF;
				int actual_r = actual[(y * w + x) * 3 + 2] & 0xFF;
				
				diff[(y * w + x) * 3 + 0] = (byte)Math.abs(expect_b - actual_b);
				diff[(y * w + x) * 3 + 1] = (byte)Math.abs(expect_g - actual_g);
				diff[(y * w + x) * 3 + 2] = (byte)Math.abs(expect_r - actual_r);
				
				error += Math.abs(expect_r - actual_r);
				error += Math.abs(expect_g - actual_g);
				error += Math.abs(expect_b - actual_b);
			}
		}
	
		// write out the diff before asserting, so we have it even if we fail
		if(diff_out != null) {
			BufferedImage bdiff = Output.dataToImage(diff, w, h);
			try {
				Output.writeImageFile(diff_out, "png", bdiff);
			} catch(IOException e) {
				System.err.println(e.toString());
				Assert.assertTrue(false);
			}
		}

		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				/*
				System.out.format("%dx%d: R: %x = %x, G: %x = %x, B: %x = %x%n", x, y,
						expect_r, actual_r, 
						expect_g, actual_g, 
						expect_b, actual_b);
				*/
				Assert.assertTrue(diff[(y * w + x) * 3 + 0] < 1);
				Assert.assertTrue(diff[(y * w + x) * 3 + 1] < 1);
				Assert.assertTrue(diff[(y * w + x) * 3 + 2] < 1);
			}
		}
		Assert.assertTrue(error < (w * h) / 2);

		return error;
	}

	private void runTest(String test, OperationNode a) {
		this.runTest(test, a, a.clone(), a.clone());
	}

	private void runTest(String test, OperationNode r, OperationNode g, OperationNode b) {
		ColorNode tree = new RGB_Space(r, g, b);
		byte[] actual = tree.compute(this.ip, true);
		int w = this.opt.getWidth();
		int h = this.opt.getHeight();
		
		// write the image to show what we got so we can compare on failures
		BufferedImage bactual = Output.dataToImage(actual, w, h);
		try {
			Output.writeImageFile("./test/result/" + test + ".png", "png", bactual);
		} catch(IOException e) {
			System.err.println(e.toString());
			Assert.assertTrue(false);
		}

		compare(actual, w, h, "./test/goal/" + test + ".png", "test/diff/" + test + ".png");
		
		// Total error should be limited 
		//System.out.format("%s-TotalError: %d, per-pix: %f%n", test, error, (double)error / (opt.getWidth() * opt.getHeight()));
	}

	private OperationNode c1P() {
		return new Const(1);
	}
	private OperationNode c1N() {
		return new Const(-1);
	}
	private OperationNode c0() {
		return new Const(0);
	}
	
	// const/plane layout tests
	@Test public void testConstRed() 	{ this.runTest("1101", c1P(), c1N(), c1N()); }
	@Test public void testConstGreen() 	{ this.runTest("1102", c1N(), c1P(), c1N()); }
	@Test public void testConstBlue() 	{ this.runTest("1103", c1N(), c1N(), c1P()); }
	@Test public void testConstMagenta(){ this.runTest("1104", c1P(), c1N(), c1P()); }
	
	// linear gradient / plane orientation tests
	@Test public void testLinGradDiagLeftFill()  { this.runTest("2101", new LinearGradient(0, 0, 1, 1)); }
	@Test public void testLinGradDiagRightFill() { this.runTest("2102", new LinearGradient(-1, -1, 0, 0)); }
	// Note: the rest of these codify existing, non-optimal behavior
	@Test public void testLinGradDiagNorthSouth(){ this.runTest("2103", new LinearGradient(0, -1, 0, 1)); }
	@Test public void testLinGradDiagSouthNorth(){ this.runTest("2104", new LinearGradient(0, 1, 0, -1)); }
	@Test public void testLinGradDiagNonSquareNorthSouth() {
		this.ip = new ImageParameters(256, 128);
		this.opt.setWidth(256);
		this.runTest("2105", new LinearGradient(0, -1, 0, 1));
	}
	@Test public void testLinGradDiagNonSquareSouthNorth() {
		this.ip = new ImageParameters(256, 128);
		this.opt.setWidth(256);
		this.runTest("2106", new LinearGradient(0, 1, 0, -1)); }
	@Test public void testLinGradDiagNorthSouthTiltedBig()  { 
		this.runTest("2107", new LinearGradient(0, -1, 1, 1)); }
	@Test public void testLinGradDiagNorthSouthTiltedSmall()  { 
		this.runTest("2108", new LinearGradient(0, -1, 0.09, 1)); }
	@Test public void testLinGradDiagNonSquareNorthSouthTiltedBig()  { 
		this.ip = new ImageParameters(256, 128);
		this.opt.setWidth(256);
		this.runTest("2109", new LinearGradient(0, -1, 1, 1)); }
	@Test public void testLinGradDiagNonSquareNorthSouthTiltedSmall()  { 
		this.ip = new ImageParameters(256, 128);
		this.opt.setWidth(256);
		this.runTest("2110", new LinearGradient(0, -1, 0.09, 1)); }
	
	// polar theta
	@Test public void testPolarTheta0() 	{ this.runTest("2201", new PolarTheta(0, 0, 0)); }
	@Test public void testPolarTheta05N() 	{ this.runTest("2202", new PolarTheta(0, 0, -0.5)); }
	@Test public void testPolarTheta05P() 	{ this.runTest("2203", new PolarTheta(0, 0, 0.5)); }
	@Test public void testPolarTheta1P() 	{ this.runTest("2204", new PolarTheta(0, 0, 1)); }
	@Test public void testPolarTheta1N() 	{ this.runTest("2205", new PolarTheta(0, 0, -1)); }
	@Test public void testPolarTheta75at1N(){ this.runTest("2206", new PolarTheta(-1, -1, 0.75)); }

	// radial gradients
	@Test public void testRadGradCenterFull()	{ this.runTest("2300", new RadialGradient(0, 0, 0.8, 0.8, 0)); }
	@Test public void testRadGradCenterEdge()	{ this.runTest("2301", new RadialGradient(0, 0, 0.707, 0.707, 0)); }
	@Test public void testRadGradUpperLeft()	{ this.runTest("2302", new RadialGradient(-1, -1, 0.8, 0.8, 0)); }
	@Test public void testRadGradUpperRight()	{ this.runTest("2303", new RadialGradient(1, -1, 0.8, 0.8, 0)); }
	@Test public void testRadGradBottomRight()	{ this.runTest("2304", new RadialGradient(1, 1, 0.8, 0.8, 0)); }
	@Test public void testRadGradBottomLeft()	{ this.runTest("2305", new RadialGradient(-1, 1, 0.8, 0.8, 0)); }
	@Test public void testRadGrad45()			{ this.runTest("2306", new RadialGradient(0, 0, 0.5, 0.8, 45)); }
	@Test public void testRadGrad315()			{ this.runTest("2307", new RadialGradient(0, 0, 0.5, 0.8, 315)); }

	// flower
	@Test public void testFlower5()		{this.runTest("2400", new Flower(0, 0, 0, 1, 0.5, 5));}
	@Test public void testFlowerAngle()	{this.runTest("2401", new Flower(0, 0, 45, 1, 0.5, 5));}
	@Test public void testFlowerSmallR(){this.runTest("2402", new Flower(0, 0, 0, 1, 0, 12));}
	@Test public void testFlowerLargeR(){this.runTest("2403", new Flower(0, 0, 0, 1, 1, 12));}
	@Test public void testFlowerCorner(){this.runTest("2404", new Flower(-1, -1, 45, 2.4, 0.1, 4));}

	private OperationNode XCoord() {
		return new LinearGradient(-1, 0, 1, 0);
	}
	private OperationNode YCoord() {
		return new LinearGradient(0, 1, 0, -1);
	}
	
	// xcoord
	@Test public void testXCoord()	{this.runTest("2500", XCoord());}
	@Test public void testYCoord()	{this.runTest("2600", YCoord());}
	
	// Unary Op
	@Test public void testAbs()		{this.runTest("3000", new Absolute(XCoord()));}
	@Test public void testInv()		{this.runTest("3100", new Invert(XCoord()));}

	// Binary Op
	@Test public void testAdd()		{this.runTest("3200", new Add(XCoord(), YCoord()));}
	@Test public void testDiv()		{this.runTest("3300", new Divide(XCoord(), YCoord()));}
	@Test public void testExp()		{this.runTest("3400", new Exponentiate(XCoord(), YCoord()));}
	@Test public void testMod()		{this.runTest("3500", new Modulus(XCoord(), YCoord()));}
	@Test public void testMul()		{this.runTest("3600", new Multiply(XCoord(), YCoord()));}

	// sin
	@Test public void testSin1x0()		{this.runTest("4000", new Sine(1, 0, XCoord()));}
	@Test public void testSin0x0()		{this.runTest("4001", new Sine(0, 0, XCoord()));}
	@Test public void testSinN1x0()		{this.runTest("4002", new Sine(-1, 0, XCoord()));}
	@Test public void testSinPIx0()		{this.runTest("4003", new Sine(3.14, 0, XCoord()));}
	@Test public void testSinPIxPI2()	{this.runTest("4004", new Sine(3.14, 1.57, XCoord()));}

	// sinc
	@Test public void testSinc1x0()		{this.runTest("4100", new Sinc(1, 0, XCoord()));}
	@Test public void testSinc0x0()		{this.runTest("4101", new Sinc(0, 0, XCoord()));}
	@Test public void testSincN1x0()	{this.runTest("4102", new Sinc(-1, 0, XCoord()));}
	//@Test public void testSinc10PIx0()	{this.runTest("4103", new Sinc(31.4, 0, XCoord()));}
	//@Test public void testSinc10PIxPI2(){this.runTest("4104", new Sinc(31.4, 15.7, XCoord()));}

	// squircle
	@Test public void testSquircleCirle()	{this.runTest("4200", new Squircle(0, 0, 1, 2, c0(), c0()));}
	@Test public void testSquircleStripe()	{this.runTest("4201", new Squircle(0, 0, 1, 4, XCoord(), XCoord()));}
	@Test public void testSquircleOffset()	{this.runTest("4202", new Squircle(1, -1, 2, 2, c0(), c0()));}
	@Test public void testSquircleCorner()	{this.runTest("4203", new Squircle(0, 0, 1.414, 2, c0(), c0()));}
	@Test public void testSquircleSquare()	{this.runTest("4204", new Squircle(0, 0, 1, 4, c0(), c0()));}

	// spiral
	@Test public void testSpiralBase()		{this.runTest("4300", new Spiral(0, 0, 1, 1, c1P()));}
	@Test public void testSpiralInvert()	{this.runTest("4301", new Spiral(0, 0, 1, -1, c1P()));}
	@Test public void testSpiralCircles()	{this.runTest("4302", new Spiral(0, 0, 0, -1, c1P()));}
	@Test public void testSpiralOffset()	{this.runTest("4303", new Spiral(1, -1, 1, 1, c1P()));}
	@Test public void testSpiralDistortX()	{this.runTest("4304", new Spiral(0, 0, 1, 1, XCoord()));}
	@Test public void testSpiralDistortY()	{this.runTest("4305", new Spiral(0, 0, 1, 1, YCoord()));}
	@Test public void testSpiralRationalN2(){this.runTest("4306", new Spiral(0, 0, 2.5, 1, c1P()));}
	@Test public void testSpiralRationalN3(){this.runTest("4307", new Spiral(0, 0, 3.5, 1, c1P()));}
	@Test public void testSpiralRationalN4(){this.runTest("4308", new Spiral(0, 0, 4.5, 1, c1P()));}
	@Test public void testSpiralRationalN5(){this.runTest("4309", new Spiral(0, 0, 5.5, 1, c1P()));}
}
