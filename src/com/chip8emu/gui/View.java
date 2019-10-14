package com.chip8emu.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.chip8emu.core.CPU;


@SuppressWarnings("serial")
public class View extends JFrame {
	
	public Screen myScreen = new Screen();
	public Controller myController = new Controller(myScreen);
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuFile = new JMenu("Fichier");
	private JMenu menuDebug = new JMenu("Debug");
		
	private JMenuItem openMenuItem = new JMenuItem("Ouvrir");
	private JMenuItem playPauseMenuItem = new JMenuItem("Suspendre");
	private JMenuItem stepMenuItem = new JMenuItem("Step");
	private JMenuItem restartMenuItem = new JMenuItem("Recommencer");
	private JMenuItem resetMenuItem = new JMenuItem("RŽinitialiser");
	private JCheckBoxMenuItem autoStartMenuItem = new JCheckBoxMenuItem("AutoStart");
	
	private boolean autoStart = true;
	
	private ActionListener openAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			myController.loadROM();
			playPauseMenuItem.setEnabled(myController.currentFilename()!=null);
			playPauseMenuItem.setText("Commencer");
			stepMenuItem.setEnabled(true);
			if(autoStart) playPauseAction.actionPerformed(null);
		}
	};
	
	private ActionListener restartAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			myController.restart();
			stepMenuItem.setEnabled(false);
		}
	};	
	
	private ActionListener playPauseAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(myController.isPaused())
			{
				myController.play();
				playPauseMenuItem.setText("Suspendre");
				stepMenuItem.setEnabled(false);
			}
			else
			{
				myController.pause();
				playPauseMenuItem.setText("Reprendre");
				stepMenuItem.setEnabled(true);
			}
		}
	};	
	
	private ActionListener resetAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			myController.reset();
			stepMenuItem.setEnabled(false);
		}
	};
	
	private ActionListener stepAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			myController.step();
		}
	};	
	
	private ActionListener autoStartAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			autoStart=autoStartMenuItem.isSelected();
		}
	};
	
	public View(){	
		this.setTitle("Chip8Emu");
		this.setLocationRelativeTo(null);               
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(myScreen);
		buildMenu();
		this.pack();
		this.setVisible(true);
		this.setResizable(false);
		this.addKeyListener(myController);
	}
	
	private void buildMenu()
	{
		openMenuItem.addActionListener(openAction);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,KeyEvent.META_DOWN_MASK));
		menuFile.add(openMenuItem);
		playPauseMenuItem.addActionListener(playPauseAction);
		playPauseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,KeyEvent.META_DOWN_MASK));
		playPauseMenuItem.setEnabled(false);
		menuFile.add(playPauseMenuItem);
		resetMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.META_DOWN_MASK));
		resetMenuItem.addActionListener(resetAction);
		menuFile.add(resetMenuItem);
		restartMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,KeyEvent.META_DOWN_MASK));
		restartMenuItem.addActionListener(restartAction);
		menuFile.add(restartMenuItem);
		autoStartMenuItem.addActionListener(autoStartAction);
		autoStartMenuItem.setSelected(autoStart);
		menuFile.add(autoStartMenuItem);
		
		stepMenuItem.addActionListener(stepAction);
		stepMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,0));
		menuDebug.add(stepMenuItem);
		
		menuBar.add(menuFile);
		menuBar.add(menuDebug);
		setJMenuBar(menuBar);
	}
}


