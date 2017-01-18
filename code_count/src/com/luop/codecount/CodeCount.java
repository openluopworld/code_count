package com.luop.codecount;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Count the lines of a project
 * @author LuoPeng
 * @time 2015.7.9
 *
 */
public class CodeCount extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9113566755801573057L;
	
	// the absolute paths of all the source files
	private List<String> names = new ArrayList<String>();
	// the language of the user's project
	private String language = null;
	
	// the code language
	private String[] languages = new String[]{"Java", "C++", "C"};
	// panel about the file info
	private JPanel panelFileInfo = null;
	// panel show the result
	private JPanel panelResult = null, panelResultLines = null, panelResultDetail = null;
	// pane about the soft info
	private JPanel panelSoftInfo = null;
	// choose the language
	private JComboBox<String> combox = null;
	// input the file path
	private JTextField filePath = null;
	// count button
	private JButton countBu = null;
	private JLabel lbResult = null;
	// show the result lines
	private JTextField tfResult = null;
	// show the detail lines of each file
	private JTextArea taComputeProcess = null;
	// software info
	private JLabel lbReadme1 = null, lbReadme2 = null, lbReadme3 = null;
	
	// font size 18
	private int fontSizeEighteen = 18;
	// font size 12
	private int fontSizeTwelve = 12;
	// font name
	private String fontName = "Consolas";
	
	/**
	 * create the layout, the component are as follows
	 * -------------------------------
	 * |  combox  filePath  countBu  |
	 * -------------------------------
	 * |    lbResult    tfResult     |
	 * |                             |
	 * |                             |
	 * |      taComputeProcess       |
	 * |                             |
	 * |                             |
	 * -------------------------------
	 * |         lbReadme1           |
	 * |         lbReadme2           |
	 * |         lbReadme3           |
	 * |         lbReadme4           |
	 * -------------------------------
	 */
	public CodeCount() {
		/*
		 * add the file info component
		 */
		panelFileInfo = new JPanel();
		panelFileInfo.setLayout(new GridLayout(1, 3, 20, 20));
		combox = new JComboBox<String>(languages);
		panelFileInfo.add(combox);
		filePath = new JTextField();
		filePath.setFont(new Font(fontName, Font.PLAIN, fontSizeEighteen));
		panelFileInfo.add(filePath);
		countBu = new JButton("Count");
		countBu.setFont(new Font(fontName, Font.PLAIN, fontSizeEighteen));
		countBu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String path = filePath.getText();
				language = combox.getSelectedItem().toString();
				int lines = codeLinesCounter(path);
				tfResult.setText("" + lines);
			}
		});
		panelFileInfo.add(countBu);
		this.add(panelFileInfo, BorderLayout.NORTH);
		
		/*
		 * add the result component 
		 */
		// total lines
		panelResult = new JPanel();
		panelResult.setLayout(new BorderLayout());
		panelResultLines = new JPanel();
		panelResultLines.setLayout(new GridLayout(1, 2, 20, 20));
		lbResult = new JLabel("Total Lines:");
		lbResult.setFont(new Font(fontName, Font.PLAIN, fontSizeEighteen));
		panelResultLines.add(lbResult);
		tfResult = new JTextField();
		tfResult.setFont(new Font(fontName, Font.BOLD, fontSizeEighteen));
		tfResult.setEditable(false);
		panelResultLines.add(tfResult);
		panelResult.add(panelResultLines, BorderLayout.NORTH);
		// detail lines of each file
		panelResultDetail = new JPanel();
		panelResultDetail.setLayout(new GridLayout(1, 1, 20, 20));
		taComputeProcess = new JTextArea();
		taComputeProcess.setEditable(false);
		taComputeProcess.setFont(new Font(fontName, Font.PLAIN, fontSizeTwelve));
		taComputeProcess.setLineWrap(true); // change line automaticlly
		panelResultDetail.add(taComputeProcess);
		panelResult.add(panelResultDetail, BorderLayout.CENTER);
		this.add(panelResult, BorderLayout.CENTER);
		
		/*
		 * add the software info component
		 */
		panelSoftInfo = new JPanel();
		panelSoftInfo.setLayout(new GridLayout(3, 1));
		lbReadme1 = new JLabel("README: Input project path, click to count.");
		lbReadme1.setFont(new Font(fontName, Font.PLAIN, fontSizeEighteen));
		panelSoftInfo.add(lbReadme1);
		lbReadme2 = new JLabel();
		panelSoftInfo.add(lbReadme2);
		lbReadme3 = new JLabel("[Author: luopengxq@gmail.com]");
		lbReadme3.setFont(new Font(fontName, Font.PLAIN, fontSizeEighteen));
		panelSoftInfo.add(lbReadme3);
		this.add(panelSoftInfo, BorderLayout.SOUTH);
		
		/*
		 * the Frame info
		 */
		this.setTitle("Count Lines");
		this.setLocation(0, 0);
		this.setSize(400, 400);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	}

	public static void main( String[] args ) {
		new CodeCount();
	}
	
	/**
	 * count the lines of a project
	 * 
	 * @param path the path of the project
	 * @return code lines, or -1 if the path is not a directory
	 */
	private int codeLinesCounter(String path) {
		
		if ( names.size() != 0) { // clear the possible file paths of last result
			names.clear();
		}
		taComputeProcess.setText(""); // clear the possible info of last result
		
		int number = 0;
		File file = new File(path);
		if ( !file.isDirectory()) {
			return -1;
		}
		
		// get all the file names
		if ( "Java".equals(language) ) {
			getFileNames(path, ".java");
		} else if ("C++".equals(language)) {
			getFileNames(path, ".cpp");
			getFileNames(path, ".h");
		} else if ("C".equals(language)) {
			getFileNames(path, ".c");
			getFileNames(path, ".h");
		}
		
		// count the lines of each file
		int size = names.size();
		int tempNumber = 0;
		try {
			for ( int i = 0; i < size; i++) {
				tempNumber = countLines(names.get(i));
				number += tempNumber;
				if ( null == taComputeProcess.getText() ||
						"".equals(taComputeProcess.getText())) {
					taComputeProcess.setText(names.get(i) + "----" + tempNumber + " lines.");
				} else {
					taComputeProcess.setText(taComputeProcess.getText() + "\n" +
							names.get(i) + "----" + tempNumber + " lines.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return number;
		
	}
	
	/**
	 * Get all the file names of the direct children of directory 'path'
	 * 
	 * Root
	 *   -Directory1
	 *     -nihao.java
	 *     -beijing.txt
	 *   -Hello.java
	 *   -123.txt
	 *   
	 * if the path is 'Root', the result will only contain 'Root//Hello.java' and 'Root//123.txt'
	 *  
	 * @param path the file path of a project
	 * @param language the coding language of the project
	 * @return
	 */
	private void getFileNames(String path, String language) {
		
		String[] files = new File(path).list();
		int length = files.length;
		for ( int i = 0; i < length; i++ ) {
			// files[i] is the name of a file or directory
			if ( files[i].endsWith(language)) {
				names.add( path + "//" + files[i]);
			} else if ( new File(path + "//" + files[i]).isDirectory()) {
				// recursion
				getFileNames(path + "//" + files[i], language);
			}
		}
		
	}
	
	/**
	 * count the lines of a source file
	 * 
	 * @param filePath filePath the path of the file
	 * @return lines of the file
	 * @throws IOException
	 */
	private int countLines ( String filePath) throws IOException {
		int lines = 0; 
		BufferedReader br = null;
		String line = null;
		
		br = new BufferedReader(new FileReader(new File(filePath)));
		line = br.readLine();
		while (line != null) {
			lines++;
			line = br.readLine();
		}
		
		br.close();
		return lines;
	}
	
}

