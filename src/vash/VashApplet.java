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
package vash;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;



class VashImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private String label = "Drawing...";
	
	public void setLabel(String l) {
		if(l == null) {
			label = "An unknown error occurred.";
		} else {
			label = l;
		}
	}
	
	public void setImage(BufferedImage b) {
		img = b;
	}
	
	@Override
	public void paint(Graphics g) {
		if(this.img == null) {
			g.setColor(new Color(0, 0, 0));
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.setColor(new Color(255, 255, 255));
			g.drawString(label, 10, 20);
		} else {
			g.drawImage(this.img, 
					0, 0, this.getWidth(), this.getHeight(),
					0, 0, this.getWidth(), this.getHeight(), null);
		}
	}
}

/**
 * Vash applet program entry point and driver.
 */
public class VashApplet extends JApplet implements ActionListener {
	static final long serialVersionUID = 0;

	private final String ALGORITHM = "1-fast";
	private final int SIZE = 200;

	private final String initial = "Vash";

	private VashImagePanel hashImagePanel;
	private JTextField hashInput;
	
	public VashApplet () {}
	
	@Override
	public void init() {
		buildLayout();
	}

	
	@Override
	public void start() {
		hash(this.initial);
	}
	
	
	public void buildLayout() {
		hashImagePanel = new VashImagePanel();
		hashImagePanel.setPreferredSize(new Dimension(SIZE, SIZE));
		
		hashInput = new JTextField();
		hashInput.setText(this.initial);
		hashInput.setActionCommand("input");
		hashInput.addActionListener(this);
		
		JButton hashButton = new JButton("Vash It");
		hashButton.setActionCommand("hash");
		hashButton.addActionListener(this);
		
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.add(hashInput);
        panel.add(hashButton);
        
        add(hashImagePanel, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);
	}
	
	
	public void hash(String inp) {
		// NOTE: need to keep this around for the background thread to access later
		final String _inp = inp;

		hashImagePanel.setLabel("Drawing...");
		hashImagePanel.setImage(null);
		repaint();
		
		//Background task for loading images.
		SwingWorker<BufferedImage, Object> worker = new SwingWorker<BufferedImage, Object>() {
			@Override
			public BufferedImage doInBackground() {
				try {
					return Vash.createImage(ALGORITHM, _inp, SIZE, SIZE);
				} catch(NoSuchAlgorithmException e) {
					hashImagePanel.setLabel(e.getLocalizedMessage());
					return null;
				}
			}
			@Override
			public void done() {
				try {
					hashImagePanel.setImage(get());
				} catch (InterruptedException e) {
					hashImagePanel.setLabel(e.getLocalizedMessage());
					e.printStackTrace();
				} catch (ExecutionException e) {
					hashImagePanel.setLabel(e.getLocalizedMessage());
					e.printStackTrace();
				}
				repaint();
			}
	    };
		worker.execute();
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	
	@Override
	public String getAppletInfo() {
		return "Vash: Making data memorable.  Visually pleasing and distinct abstract artwork, generated uniquely for any input data.";
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("hash")) {
			hash(hashInput.getText());
		} else if(e.getActionCommand().equals("input")) {
			hash(hashInput.getText());
		} else {
			System.out.format("unknown action: %s%n", e.getActionCommand());
		}
	}
}
