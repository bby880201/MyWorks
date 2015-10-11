package view;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

public class MusicPlayerView<T> extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8610126877687530775L;
	private JPanel pnlRoot;
	private JTextField tfFileName;
	private JPanel pnlControl;
	private JLabel lblFile;
	private JButton btnLoad;
	private JButton btnParse;
	private JComboBox<T> lstInstrument;
	private JButton btnPlay;
	private JButton btnStop;
	private JSplitPane pnlSplit;
	private JScrollPane spnlContent;
	private JScrollPane spnlParsed;
	private JTextArea taContent;
	private JTextArea taParsed;

	/**
	 * Create the frame.
	 */
	public MusicPlayerView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		pnlRoot = new JPanel();
		pnlRoot.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlRoot.setLayout(new BorderLayout(0, 0));
		setContentPane(pnlRoot);
		
		pnlControl = new JPanel();
		pnlControl.setToolTipText("control panel");
		pnlControl.setBackground(Color.GREEN);
		pnlRoot.add(pnlControl, BorderLayout.NORTH);
		
		lblFile = new JLabel("File:");
		lblFile.setToolTipText("file label");
		pnlControl.add(lblFile);
		
		tfFileName = new JTextField();
		tfFileName.setText("scale");
		tfFileName.setToolTipText("enter a abc file name here");
		pnlControl.add(tfFileName);
		tfFileName.setColumns(10);
		
		btnLoad = new JButton("Load");
		btnLoad.setToolTipText("load abc file");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnParse.setEnabled(true);
			}
		});
		pnlControl.add(btnLoad);
		
		btnParse = new JButton("Parse");
		btnParse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnParse.setEnabled(false);
			}
		});
		btnParse.setEnabled(false);
		btnParse.setToolTipText("parse abc file");
		pnlControl.add(btnParse);
		
		lstInstrument = new JComboBox<T>();
		lstInstrument.setToolTipText("select an instrument to play");
		pnlControl.add(lstInstrument);
		
		btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStop.setEnabled(true);
			}
		});
		btnPlay.setToolTipText("play music");
		pnlControl.add(btnPlay);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStop.setEnabled(false);
			}
		});
		btnStop.setEnabled(false);
		btnStop.setToolTipText("stop music");
		pnlControl.add(btnStop);
		pnlControl.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{tfFileName, btnLoad, btnParse, lstInstrument, btnPlay, btnStop}));
		
		pnlSplit = new JSplitPane();
		pnlSplit.setBackground(Color.WHITE);
		pnlSplit.setToolTipText("display file contents before and after parsing");
		pnlSplit.setResizeWeight(0.5);
		pnlSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pnlRoot.add(pnlSplit, BorderLayout.CENTER);
		
		spnlContent = new JScrollPane();
		spnlContent.setViewportBorder(new TitledBorder(null, "File Contents", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		spnlContent.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		spnlContent.setToolTipText("panel contains a text area for file content");
		pnlSplit.setLeftComponent(spnlContent);
		
		taContent = new JTextArea();
		taContent.setWrapStyleWord(true);
		taContent.setLineWrap(true);
		taContent.setToolTipText("file content displays here");
		spnlContent.setViewportView(taContent);
		
		spnlParsed = new JScrollPane();
		spnlParsed.setViewportBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Parsed IPhrase Structure", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		spnlParsed.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		spnlParsed.setToolTipText("panel contains a text area for parsed structure");
		pnlSplit.setRightComponent(spnlParsed);
		
		taParsed = new JTextArea();
		taParsed.setWrapStyleWord(true);
		taParsed.setLineWrap(true);
		spnlParsed.setViewportView(taParsed);
		pnlRoot.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{tfFileName, btnLoad, btnParse, lstInstrument, btnPlay, btnStop}));
	}

	public void start() {
		setVisible(true);
	}

}
