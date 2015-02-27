package edu.kuliah.kripto.tubessatu.nomerdua;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.JLabel;


import edu.kuliah.kripto.tubessatu.nomerdua.FourDiffLSBSteganography;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.JScrollPane;

public class Nomer2 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//GUI Components
	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;
	private JComboBox comboBox;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	
	//Program Properties
	private File selectedFile;
	private File inputFile;
	private FourDiffLSBSteganography stegano;
	File s;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				//Set look and feel, optional
				try {
				  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch(Exception e) {
				  System.out.println("Error setting native LAF: " + e);
				}
				//Actual run of the program
				try {
					Nomer2 frame = new Nomer2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Nomer2() {
		setTitle("Tubes Kripto Nomer Dua");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 262);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Components initialization
		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(10, 27, 279, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnOpenImage = new JButton("Open Image");
		btnOpenImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser("./");
				FileNameExtensionFilter filter=new FileNameExtensionFilter("Images", "bmp", "jpg","png");
		        chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(Nomer2.this);
				if (returnVal == JFileChooser.APPROVE_OPTION){
						selectedFile = chooser.getSelectedFile();
				}
			
				
			}
		});
		btnOpenImage.setBounds(10, 49, 108, 23);
		contentPane.add(btnOpenImage);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Encode", "Decode"}));
		comboBox.setBounds(316, 27, 108, 20);
		contentPane.add(comboBox);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 152, 279, 61);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JButton btnProcess = new JButton("Process");
		btnProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (selectedFile == null){
					JOptionPane.showMessageDialog(Nomer2.this,  
							"Please choose an image file first", "Error", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if(!stegano.setParameter(Integer.parseInt(textField_1.getText()), Integer.parseInt(textField_2.getText()), Integer.parseInt(textField_3.getText())))
				{
					JOptionPane.showMessageDialog(Nomer2.this,  
		"Parameter restriction violated!\nPlease ensure that 2^KLow<=T<=2^KHigh,  1<=KLow, KHigh<=5",  "Error",  JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
				
					if (comboBox.getSelectedIndex() == 0){ //Encode message into bitmap image
						encode();
					} if (comboBox.getSelectedIndex() == 1){ //Decode message from bitmap image
						decode();
					}
				}
			}
		});
		btnProcess.setBounds(316, 153, 108, 60);
		contentPane.add(btnProcess);
		
		JLabel lblImageFile = new JLabel("Image File");
		lblImageFile.setBounds(10, 11, 66, 14);
		contentPane.add(lblImageFile);
		
		JLabel lblMessage = new JLabel("Message");
		lblMessage.setBounds(10, 138, 46, 14);
		contentPane.add(lblMessage);
		
		JLabel lblParameter = new JLabel("Parameter");
		lblParameter.setBounds(10, 76, 83, 14);
		contentPane.add(lblParameter);
		
		textField_1 = new JTextField();
		textField_1.setText("5");
		textField_1.setBounds(53, 101, 46, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setText("2");
		textField_2.setColumns(10);
		textField_2.setBounds(146, 101, 46, 20);
		contentPane.add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setText("3");
		textField_3.setColumns(10);
		textField_3.setBounds(243, 101, 46, 20);
		contentPane.add(textField_3);
		
		JLabel lblT = new JLabel("T:");
		lblT.setBounds(37, 101, 19, 14);
		contentPane.add(lblT);
		
		JLabel lblKLow = new JLabel("K low:");
		lblKLow.setBounds(107, 101, 29, 14);
		contentPane.add(lblKLow);
		
		JLabel lblKHigh = new JLabel("K high:");
		lblKHigh.setBounds(202, 101, 40, 14);
		contentPane.add(lblKHigh);
		
		//Properties initialization
		selectedFile = null;
		s = null;
		stegano = new FourDiffLSBSteganography();
		
		
		JButton btnInputFile = new JButton("Input File");
		btnInputFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser("./");
				int returnVal = chooser.showOpenDialog(Nomer2.this);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					inputFile = chooser.getSelectedFile();
				}
			}
		});
		
		btnInputFile.setBounds(128, 49, 89, 23);
		contentPane.add(btnInputFile);
		
		JButton btnOutputFile = new JButton("Output File");
		btnOutputFile.setBounds(228, 49, 89, 23);
		contentPane.add(btnOutputFile);
		
		
		
	}
	
	
	private void encode(){
			
			JFileChooser chooser = new JFileChooser("./");
			FileNameExtensionFilter filter=new FileNameExtensionFilter("Images", "bmp");//,"jpg","png");
	        chooser.setFileFilter(filter);
			if (chooser.showSaveDialog(Nomer2.this) == JFileChooser.APPROVE_OPTION){
				
				FileInputStream is;
				try{
					String text = textArea.getText();
					String path = selectedFile.getPath();

					String key = textArea.getText();
					
					StandardVigenere cipher = new StandardVigenere();
					
					
					is = new FileInputStream(inputFile);
					byte[] input = new byte[(int) inputFile.length()];
					is.read(input);
					
					byte[] encryptedData = cipher.doCrypt(StandardVigenere.ENCRYPT, input, key.getBytes());
					
					if(stegano.encodeAndSave(path, chooser.getSelectedFile().getPath(), inputFile.getName(), encryptedData))
					{
						JOptionPane.showMessageDialog(Nomer2.this, "The Image was encoded Successfully!", 
							"Success", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						JOptionPane.showMessageDialog(Nomer2.this,  
		"The Image could not be encoded!", 
							"Error", JOptionPane.INFORMATION_MESSAGE);
					}
					
					is.close();
				}
				catch(Exception except) {
					except.printStackTrace();
					//msg if opening fails
					JOptionPane.showMessageDialog(Nomer2.this,  
		"The File cannot be opened!", 
						"Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
	}
	
	private void decode(){
		byte[][] message = stegano.decode(selectedFile.getPath());
		JFileChooser chooser = new JFileChooser("./");
		chooser.setSelectedFile(new File(new String(message[1])));
		if (chooser.showSaveDialog(Nomer2.this) == JFileChooser.APPROVE_OPTION){
			
			try{
				
				String key = textArea.getText();
				
				StandardVigenere cipher = new StandardVigenere();
				
				byte[] decryptedData = cipher.doCrypt(StandardVigenere.DECRYPT, message[0], key.getBytes());
				
				FileOutputStream os = new FileOutputStream(chooser.getSelectedFile());
				os.write(decryptedData);
				os.close();
			}
			catch(Exception except) {
				except.printStackTrace();
				//msg if opening fails
				JOptionPane.showMessageDialog(Nomer2.this,  
	"The File cannot be opened!", 
					"Error!", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	public static double PSNR(BufferedImage img1, BufferedImage img2) {
		if (img1.getType() != img2.getType() || img1.getHeight() != img2.getHeight() || img1.getWidth() != img2.getWidth()) return -1;

		double mse = 0;
		int width = img1.getWidth();
		int height = img1.getHeight();
	
		//converst images to byte array, this way itll work the same for all image type
		final byte[] pixels1 = ((DataBufferByte) img1.getRaster().getDataBuffer()).getData();
		final byte[] pixels2 = ((DataBufferByte) img2.getRaster().getDataBuffer()).getData();
		
		
		for(int i = 0; i < pixels1.length; i++){
			mse += Math.pow((pixels1[i]&0x000000FF) - (pixels2[i]&0x000000FF), 2);
		}
		
		mse /=  width * height;
		double rms = Math.sqrt(mse);
		
		int maxVal = 255;
		double x = maxVal / rms;
		double psnr = 20.0 * logBase10(x);
		System.out.println("PSNR: " + psnr);
		return psnr;
	}

	public static double logBase10(double x) {
		return Math.log(x) / Math.log(10);
	}
}
