package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import common.Config;

import common.Log;
import data.StpTable;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements ActionListener, WindowListener, MouseListener {

	public static final String MAINWINDOW_X = "mainwindow_x";
	public static final String MAINWINDOW_Y = "mainwindow_y";
	public static final String MAINWINDOW_WIDTH = "mainwindow_width";
	public static final String MAINWINDOW_HEIGHT = "mainwindow_height";
	public static final String WINDOW_FC_WIDTH = "mainwindow_width";
	public static final String WINDOW_FC_HEIGHT = "mainwindow_height";
	public static final String WINDOW_CURRENT_DIR = "mainwindow_current_dir";
	
	public static final String WINDOW_SPLIT_PANE_HEIGHT = "window_split_pane_height";
	
	public static final int DEFAULT_DIVIDER_LOCATION = 450;
	
	private StpTable stpTable;
	
	public static JFrame frame;
	// Swing File Chooser
	JFileChooser fc = null;
	//AWT file chooser for the Mac
	FileDialog fd = null;
	JLabel lblVersion;
	static JLabel lblLogFileDir;
	
	// Menu items
	static JMenuItem mntmDownload;
	static JMenuItem mntmLoad;
	static JMenuItem mntmExit;
	static JMenuItem mntmSettings;
	static JMenuItem mntmManual;
	static JMenuItem mntmAbout;

	EarthPlotPanel earthPlot;
	
	public MainWindow() {
		frame = this; // a handle for error dialogues
		initialize();
		/*
		if (Config.isMacOs()) {
			macApplication = com.apple.eawt.Application.getApplication();
			macApplication.setAboutHandler(new MacAboutHandler());
			macApplication.setPreferencesHandler(new MacPreferencesHandler());
			macApplication.setQuitHandler(new MacQuitHandler(this));
		}
		*/
	}
	
	private void initialize() {
		if (Config.getInt(MAINWINDOW_X) == 0) {
			Config.set(MAINWINDOW_X, 100);
			Config.set(MAINWINDOW_Y, 100);
			Config.set(MAINWINDOW_WIDTH, 680);
			Config.set(MAINWINDOW_HEIGHT, 800);
		}
		setBounds(Config.getInt(MAINWINDOW_X), Config.getInt(MAINWINDOW_Y), 
				Config.getInt(MAINWINDOW_WIDTH), Config.getInt(MAINWINDOW_HEIGHT));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setTitle("AMSAT Fox STP Analysis Tool");
		initMenu();
		makeTopPanel();
		makeBottomPanel();
		makeCenterPanel();
		
		fd = new FileDialog(MainWindow.frame, "Select STP file",FileDialog.LOAD);
		fd.setFile("*.stp");
		fc = new JFileChooser();
		// initialize the file chooser
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Stp", "stp", "Csv", "csv");
		fc.setFileFilter(filter);
		Action details = fc.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);
		
		addWindowListener(this);
		
	}
	
	
	private void makeTopPanel() {
		JPanel topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new FlowLayout (FlowLayout.LEFT));
		
		
	}
	
	private void makeCenterPanel() {
		
		JPanel centerPanel = new JPanel();
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout ());

		earthPlot = new EarthPlotPanel();
		centerPanel.add(earthPlot);
		
	}
	
	
	
	private void makeBottomPanel() {
		JPanel bottomPanel = new JPanel();
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BorderLayout ());
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
//		mntmDelete = new JMenuItem("Delete Payload Files");
//		mnFile.add(mntmDelete);
//		mntmDelete.addActionListener(this);
		mntmDownload = new JMenuItem("Fetch Server STP");
		mntmLoad = new JMenuItem("Load STP File");
		mnFile.add(mntmDownload);
		mntmDownload.addActionListener(this);
		mnFile.add(mntmLoad);
		mntmLoad.addActionListener(this);

		mnFile.addSeparator();
		
		if (!Config.isMacOs()) {
			mntmSettings = new JMenuItem("Settings");
			mnFile.add(mntmSettings);
			mntmSettings.addActionListener(this);
			mnFile.addSeparator();
		}
		mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		mntmExit.addActionListener(this);

			
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		mntmManual = new JMenuItem("Open Manual");
		mnHelp.add(mntmManual);
		mntmManual.addActionListener(this);

		if (!Config.isMacOs()) {
			mntmAbout = new JMenuItem("About");
			mnHelp.add(mntmAbout);
			mntmAbout.addActionListener(this);
		}
	}

	public void shutdownWindow() {
		Log.println("Window Closed");
		Log.close();
		this.dispose();
		saveProperties();
		System.exit(0);	
	}
	
	public void saveProperties() {
		Config.set(MAINWINDOW_HEIGHT, this.getHeight());
		Config.set(MAINWINDOW_WIDTH, this.getWidth());
		Config.set(MAINWINDOW_X, this.getX());
		Config.set(MAINWINDOW_Y, this.getY());
		
		Config.save();
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		shutdownWindow();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		shutdownWindow();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == mntmDownload) {
			////////////////////////////////////////////
		}
		if (e.getSource() == mntmLoad) {
			loadFile();
			earthPlot.setData(stpTable);
		}
		if (e.getSource() == mntmExit) {
			windowClosed(null);
		}
		if (e.getSource() == mntmSettings) {
//			SettingsFrame f = new SettingsFrame(this, true);
//			f.setVisible(true);
		}
		

	}
	
	public boolean loadFile() {
		File file = null;
		File dir = null;
		String d = Config.get(WINDOW_CURRENT_DIR);
		if (d == null)
			dir = new File(".");
		else
			if (d != "") {
				dir = new File(Config.get(WINDOW_CURRENT_DIR));
			}

		if(Config.getBoolean(Config.USE_NATIVE_FILE_CHOOSER)) {
			// use the native file dialog on the mac

			if (dir != null) {
				fd.setDirectory(dir.getAbsolutePath());
			}
			fd.setVisible(true);
			String filename = fd.getFile();
			String dirname = fd.getDirectory();
			if (filename == null)
				Log.println("You cancelled the choice");
			else {
				Log.println("File: " + filename);
				Log.println("DIR: " + dirname);
				file = new File(dirname + filename);
			}
		} else {
			if (Config.getInt(WINDOW_FC_WIDTH) == 0) {
				Config.set(WINDOW_FC_WIDTH, 600);
				Config.set(WINDOW_FC_HEIGHT, 600);
			}
			fc.setPreferredSize(new Dimension(Config.getInt(WINDOW_FC_WIDTH), Config.getInt(WINDOW_FC_HEIGHT)));
			if (dir != null)
				fc.setCurrentDirectory(dir);

			int returnVal = fc.showOpenDialog(this);
			Config.set(WINDOW_FC_HEIGHT, fc.getHeight());
			Config.set(WINDOW_FC_WIDTH,fc.getWidth());		
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
			}
		}

		if (file != null) {
			Config.set(WINDOW_CURRENT_DIR, file.getParent());					
//			lblFileName.setText(file.getName());
//			filePanel.setVisible(true);
			
			load(file.getAbsolutePath());
			
			return true;
		}
		return false;
	}

	private void load(String filename) {
		try {
			stpTable = new StpTable(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.errorDialog("ERROR", "Cant load the STP File\n" + e.getMessage());
		}
	}

	public void mouseClicked(MouseEvent e) {
//		int row = directoryTable.rowAtPoint(e.getPoint());
//		int col = directoryTable.columnAtPoint(e.getPoint());
//		if (row >= 0 && col >= 0) {
//			//Log.println("CLICKED ROW: "+row+ " and COL: " + col + " COUNT: " + e.getClickCount());
//
//			String id = (String) directoryTable.getValueAt(row, 0);
//			txtFileId.setText(id);
//			Long lid = Long.decode("0x"+id);
//			PacSatFile pf = new PacSatFile(Config.spacecraft.directory.dirFolder, lid);
//			//Log.println(pf.getHoleListString());
//			if (e.getClickCount() == 2)
//				displayRow(directoryTable, row);
//			directoryTable.setRowSelectionInterval(row, row);
//		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
