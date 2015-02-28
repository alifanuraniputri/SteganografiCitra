import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI extends JFrame {

	private JPanel contentPane;
	private final JButton btnSelectPicture = new JButton("Select Picture");
	private final JLabel lblImage = new JLabel("image");
	private final JTextField textKey = new JTextField();
	private final JLabel lblKataKunci = new JLabel("Kata Kunci");
	private final JButton btnSisipkan = new JButton("Sisipkan Pesan ke Citra");
	private final JButton btnEkstrakPesan = new JButton(
			"Ekstrak Pesan dari Citra");
	private final JButton btnSimpanCitra = new JButton("Simpan Citra Berpesan");
	private final JLabel lblTubesIfKriptografi = new JLabel(
			"Tubes1 IF4020 Kriptografi ");
	private final JLabel lblVaiHabibie = new JLabel("Vai - Habibie - Alifa");
	private final JRadioButton radioMode1 = new JRadioButton("LSB");
	private final JRadioButton radioMode2 = new JRadioButton("LSB Xin Liao");
	private final JRadioButton radioMode3 = new JRadioButton("LSB Ghandarba S");
	private BufferedImage chosen;
	private BufferedImage result;
	private SteganografiProcessing stegano;
	private FourDiffLSBSteganography stegano2;
	private String pesan ="";
	private String namaFile ="";
	private String namaFileCitra ="";
	private int mode = 1;
	private final JButton btnSimpanPlain = new JButton("Simpan Plain Teks");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea txtTeks = new JTextArea();
	private final JButton btnNewButton = new JButton("Pilih File Pesan");
	private final JButton btnBandingkan = new JButton(
			"Bandingkan dengan Citra Asli");
	JFrame frameAsli = new JFrame("Citra Asli");
	JLabel lblImageAsli = new JLabel("image");
	File picture;
	String kunci = "";
	private final JButton btnAnalisisPnsr = new JButton("Analisis PSNR");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| javax.swing.UnsupportedLookAndFeelException ex) {
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
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
	public GUI() {
		setTitle("Steganografi Citra Digital");
		textKey.setForeground(Color.BLACK);
		textKey.setBounds(392, 97, 243, 30);
		textKey.setColumns(10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 688, 556);
		contentPane = new JPanel();
		contentPane.setForeground(Color.WHITE);
		contentPane.setBackground(new Color(255, 240, 245));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		btnSelectPicture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectImage();
				btnSimpanCitra.setEnabled(false);
				btnSimpanPlain.setEnabled(false);
			}
		});
		btnSelectPicture.setBackground(new Color(255, 105, 180));
		btnSelectPicture.setBounds(30, 29, 97, 30);

		contentPane.add(btnSelectPicture);
		lblImage.setOpaque(true);
		lblImage.setForeground(new Color(255, 105, 180));
		lblImage.setBackground(new Color(255, 105, 180));
		lblImage.setBounds(30, 76, 310, 310);

		contentPane.add(lblImage);

		contentPane.add(textKey);
		lblKataKunci.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblKataKunci.setBounds(392, 76, 105, 10);

		contentPane.add(lblKataKunci);
		btnSisipkan.setEnabled(false);
		btnSisipkan.setBackground(new Color(255, 105, 180));
		btnSisipkan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sisipkanPesan(mode);
				txtTeks.setEnabled(true);
			}
		});
		btnSisipkan.setBounds(392, 219, 243, 23);

		contentPane.add(btnSisipkan);
		btnEkstrakPesan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getPlainText(mode);
				btnSimpanPlain.setEnabled(true);
				btnSisipkan.setEnabled(false);
			}
		});
		btnEkstrakPesan.setEnabled(false);
		btnEkstrakPesan.setBackground(new Color(255, 105, 180));
		btnEkstrakPesan.setBounds(392, 287, 243, 23);

		contentPane.add(btnEkstrakPesan);
		btnSimpanCitra.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simpanCitra();
			}
		});
		btnSimpanCitra.setEnabled(false);
		btnSimpanCitra.setBackground(new Color(255, 105, 180));
		btnSimpanCitra.setBounds(392, 253, 243, 23);

		contentPane.add(btnSimpanCitra);
		lblTubesIfKriptografi.setForeground(new Color(255, 20, 147));
		lblTubesIfKriptografi.setBounds(448, 451, 135, 30);

		contentPane.add(lblTubesIfKriptografi);
		lblVaiHabibie.setForeground(new Color(255, 20, 147));
		lblVaiHabibie.setBounds(468, 480, 97, 14);

		contentPane.add(lblVaiHabibie);

		radioMode1.setSelected(true);
		radioMode1.setBackground(new Color(255, 240, 245));
		radioMode1.setBounds(392, 189, 43, 23);
		radioMode1.setMnemonic(KeyEvent.VK_C);

		radioMode1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				mode = 1;
			}
		});

		contentPane.add(radioMode1);
		radioMode2.setBackground(new Color(255, 240, 245));
		radioMode2.setBounds(437, 189, 81, 23);
		radioMode2.setMnemonic(KeyEvent.VK_M);
		radioMode2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				mode = 2;
			}
		});

		contentPane.add(radioMode2);
		radioMode3.setBackground(new Color(255, 240, 245));
		radioMode3.setBounds(520, 189, 137, 23);
		radioMode3.setMnemonic(KeyEvent.VK_P);
		radioMode3.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				mode = 3;
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(radioMode1);
		group.add(radioMode2);
		group.add(radioMode3);

		contentPane.add(radioMode3);
		btnSimpanPlain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simpanPlain();
			}
		});
		btnSimpanPlain.setEnabled(false);
		btnSimpanPlain.setBackground(new Color(255, 105, 180));
		btnSimpanPlain.setBounds(394, 321, 241, 23);

		contentPane.add(btnSimpanPlain);
		scrollPane.setBounds(30, 397, 310, 86);

		contentPane.add(scrollPane);
		txtTeks.setEnabled(false);

		scrollPane.setViewportView(txtTeks);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectFile();
			}
		});
		btnNewButton.setBackground(new Color(255, 105, 180));
		btnNewButton.setBounds(392, 138, 243, 30);

		contentPane.add(btnNewButton);

		btnBandingkan.setEnabled(false);
		btnBandingkan.setBounds(392, 391, 243, 23);

		contentPane.add(btnBandingkan);
		btnAnalisisPnsr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PSNR(chosen, result);
			}
		});
		btnAnalisisPnsr.setEnabled(false);
		btnAnalisisPnsr.setBounds(392, 425, 243, 23);
		
		contentPane.add(btnAnalisisPnsr);

		frameAsli.setSize(320, 320);
		frameAsli.setBounds(688, 100, 350, 375);
		JPanel panelAsli = new JPanel();
		panelAsli.setForeground(Color.WHITE);
		panelAsli.setBackground(new Color(255, 240, 245));
		panelAsli.setBorder(new EmptyBorder(5, 5, 5, 5));
		frameAsli.setContentPane(panelAsli);
		panelAsli.setLayout(null);

		btnBandingkan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				frameAsli.setVisible(true);

				lblImageAsli.setOpaque(true);
				lblImageAsli.setForeground(new Color(255, 105, 180));
				lblImageAsli.setBounds(10, 10, 310, 310);
				frameAsli.getContentPane().add(lblImageAsli);

				lblImageAsli.setIcon(new ImageIcon(chosen.getScaledInstance(
						lblImageAsli.getWidth(), lblImageAsli.getHeight(),
						BufferedImage.TRANSLUCENT)));
				frameAsli.getContentPane().add(lblImageAsli, BorderLayout.CENTER);
				lblImageAsli.setVisible(true);
			}
		});

	}

	public void selectFile() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setEnabled(false);
				fileChooser.setBounds(0, -41, 582, 397);
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				// Configure some more here
				final int userValue = fileChooser.showOpenDialog(fileChooser);
				if (userValue == JFileChooser.APPROVE_OPTION) {
					final File plainTextFile = fileChooser.getSelectedFile();
					namaFile = plainTextFile.getName();
					// if
					// (filename.substring(filename.lastIndexOf("."),filename.length()).equals("txt"))
					// {
					BufferedReader br;
					try {
						/*
						br = new BufferedReader(new FileReader(plainTextFile));
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();

						while (line != null) {
							sb.append(line);
							sb.append("\n");
							line = br.readLine();
						}
						*/
						try {
							FileInputStream fs = new FileInputStream(plainTextFile);
							int c;
					        while ((c = fs.read()) != -1) {
					        	pesan = pesan+ ((char)c);
					        }
						} catch (Exception e) {
							
						}
						txtTeks.setText(pesan);
						

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		});
	}

	public void selectImage() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setEnabled(false);
				FileFilter filter = new FileNameExtensionFilter(
						"JPG, GIF, BMP, & PNG Images", "jpg", "gif", "png",
						"bmp");
				fileChooser.setFileFilter(filter);
				fileChooser.setBounds(0, -41, 582, 397);
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				// Configure some more here
				final int userValue = fileChooser.showOpenDialog(fileChooser);
				if (userValue == JFileChooser.APPROVE_OPTION) {
					picture = fileChooser.getSelectedFile();
					namaFileCitra = picture.getName();
					try {
						chosen = ImageIO.read(picture);

					} catch (final IOException not_action) {
						not_action.printStackTrace();
					}

					lblImage.setIcon(new ImageIcon(chosen.getScaledInstance(
							lblImage.getWidth(), lblImage.getHeight(),
							BufferedImage.TRANSLUCENT)));
					btnBandingkan.setEnabled(false);
					getContentPane().add(lblImage, BorderLayout.CENTER);
					lblImage.setVisible(true);

				}
			}
		});
		btnSisipkan.setEnabled(true);
		btnEkstrakPesan.setEnabled(true);
		btnBandingkan.setEnabled(false);
		btnAnalisisPnsr.setEnabled(false);
	}

	public String bacaFilePesan(String fileName) {

		pesan = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileName));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			pesan = sb.toString();
			br.close();
			txtTeks.setText(pesan);
		} catch (Exception e) {
			System.out.println("Error while reading file line by line:"
					+ e.getMessage());
			JOptionPane.showMessageDialog(getContentPane(), "File Error");
		}
		return pesan;
	}

	public void getPlainText(int mode) {
		kunci = textKey.getText();
		if (!textKey.getText().equals("")) {
			stegano = new SteganografiProcessing(chosen, kunci);
			stegano2 = new FourDiffLSBSteganography();
			switch (mode) {
			case 1:
				pesan = stegano.getPlainTextLSBstandard();
				namaFile = stegano.getNamaFile();
				break;
			case 2:
				decode();
				break;
			case 3:
				try {
					MainLogic ML = new MainLogic();
					ML.readImage(chosen);
					
					pesan = dekripHabibie(ML.readStegoMessage("<ISI FILE>"),kunci); //encrypted
					namaFile = ML.readStegoMessage("-");
					System.out.println("Pesan panjang: "+pesan.length());
					System.out.println("Gak paham...");
				} catch (Exception e) {
					
				}
				break;
			}
			txtTeks.setText(pesan);
			txtTeks.setEnabled(false);
		} else {
			JOptionPane.showMessageDialog(getContentPane(), "Masukkan Kunci");
		}
	}
	
	private String dekripHabibie(String pesan, String key){
		String plain="";
		String token="";
		boolean isDone=false;
		int i=0;
		while (!isDone) {
			if (token.length() >= 8) {
				plain = plain + (char)Integer.parseInt(token,2);
				token = "";
			}
			else if (i >= pesan.length()) {
				isDone = true;
			} else {
				token = token + pesan.charAt(i);
				i++;
			}
		}
		System.out.println("Pesan mau didekrip: "+plain.length());
		System.out.println("Pesan mau didekrip: "+pesan.length());
		return VigenereExtended.Dekrip(key,plain);
	}

	public void sisipkanPesan(int mode) {
		kunci = textKey.getText();
		System.out.println(pesan);
		if (pesan.length() != 0 && !textKey.getText().equals("")) {
			

				stegano = new SteganografiProcessing(chosen, kunci, pesan,
						namaFile);
				stegano2 = new FourDiffLSBSteganography();
				switch (mode) {
				case 1:
					result = stegano.sisipkanLSBstandard();
					break;
				case 2:
					encode();
					break;
				case 3:
					try {
						MainLogic ML = new MainLogic();
						ML.readImage(chosen);

						String in = namaFile;
						ML.readFiletoBinary(pesan,kunci);

						ML.writeStegoMessage(in);
						ML.writeStegoMessage("<ISI FILE>");

						result = ML.writeImage();

					} catch (Exception e) {

					}
					break;
				}
				lblImage.setIcon(new ImageIcon(result.getScaledInstance(
						lblImage.getWidth(), lblImage.getHeight(),
						BufferedImage.TRANSLUCENT)));
				getContentPane().add(lblImage, BorderLayout.CENTER);
				lblImage.setVisible(true);
				JOptionPane.showMessageDialog(getContentPane(),
						"pesan telah disisipkan");
				btnBandingkan.setEnabled(true);
				btnAnalisisPnsr.setEnabled(true);
				btnEkstrakPesan.setEnabled(false);
				btnSimpanCitra.setEnabled(true);
			
		} else {
			JOptionPane.showMessageDialog(getContentPane(),
					"Pilih FIle, dan Kunci Tidak Kosong");
		}

	}

	private void encode() {
		System.out.println("Nomer 2, Encode Pesan: " + pesan);
		String path = picture.getPath();
		
		//pesan = SteganografiProcessing.enkripsiASCII(pesan, kunci);

		result = stegano2.encode(path, namaFile, pesan);
	}

	private void decode() {
		String[] res = stegano2.decode(picture.getPath());
		System.out.println("Nomer 2, Ekstrak Pesan: " + res[0]);
		System.out.println("Nomer 2, Nama File Pesan: " + res[1]);
		
		pesan = res[0];
		namaFile = res[1];
		//pesan = SteganografiProcessing.dekripsiASCII(message, kunci);
	}

	public void simpanPlain() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setApproveButtonText("Save");
				FileFilter filter = new FileNameExtensionFilter("file text",
						"txt");
				fileChooser.setDialogTitle("Specify a file to save");
				// fileChooser.setFileFilter(filter);
				File f = new File(namaFile);
				fileChooser.setSelectedFile(f);
				final int userValue = fileChooser.showOpenDialog(null);

				if (userValue == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fileChooser.getSelectedFile();

					
					try {
						FileOutputStream out = new FileOutputStream(fileChooser.getSelectedFile());
				         
			        	for (int j=0;j<pesan.length();j++){
			        		char c = pesan.charAt(j);
			        		out.write((int)c);
			        	}
					} catch (Exception e) {
					} finally {
						
							JOptionPane.showMessageDialog(getContentPane(),
									"file saved");
					
					}

				}
			}
		});
	}

	public void simpanCitra() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setApproveButtonText("Save");
				FileFilter filter = new FileNameExtensionFilter("BMP(.bmp)",
						"bmp");
				fileChooser.setDialogTitle("Save Stegano Image");
				fileChooser.setFileFilter(filter);

				final int userValue = fileChooser.showOpenDialog(fileChooser);

				if (userValue == JFileChooser.APPROVE_OPTION) {

					BufferedWriter writer = null;
					// write it new file
					try {
						if (fileChooser.getSelectedFile().getAbsolutePath()
								.contains(".bmp"))
							ImageIO.write(result, "BMP", new File(fileChooser
									.getSelectedFile().getAbsolutePath()));
						else
							ImageIO.write(result, "BMP", new File(fileChooser
									.getSelectedFile().getAbsolutePath()
									+ ".bmp"));
						JOptionPane.showMessageDialog(getContentPane(),
								"citra berpesan terseimpan");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
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

class PixelPosition {
	public int x;
	public int y;
	public int[] color = new int[4];

}

/*
String by = Integer.toBinaryString(in)
*/

class PixelMapping {
	public ArrayList<PixelPosition> pixels = new ArrayList<PixelPosition>();

	public int getPixel(int x, int y, char ch) { //r saja
		/* Algoritma search*/
		for (int i=0; i<pixels.size(); i++) {
			if ((pixels.get(i).x == x) && (pixels.get(i).y == y)) {
				if (ch == 'r') return pixels.get(i).color[0];
				else if (ch == 'g') return pixels.get(i).color[1];
				else if (ch == 'b') return pixels.get(i).color[2];
				else {
					return (pixels.get(i).color[0] + pixels.get(i).color[1] + pixels.get(i).color[2])/3;
				}
			}
		}
		return -1;
	}

	public int getRGBDecimal(int x, int y){
		return -1;
	}

	public void overridePixel(int x, int y, int r, int g, int b){
		System.out.println("Tulis size ("+x+","+y+") : "+r+"-"+g+"-"+b);
		for (int i=0; i<pixels.size(); i++) {
			if ((pixels.get(i).x == x) && (pixels.get(i).y == y)) {
				PixelPosition PP = pixels.get(i);
				PP.color[0] = r;
				PP.color[1] = g;
				PP.color[2] = b;
				pixels.set(i,PP);
				break;
			}
		}
	}

	public void insertPixel(int x, int y, int r, int g, int b, int a){
		PixelPosition PP = new PixelPosition();
		PP.x = x;
		PP.y = y;
		PP.color = new int[]{r,g,b,a};

		pixels.add(PP);
	}


	public void insertStegoMessage(int x, int y, char c, String partialBinaryString){
		//bakal make overridePixel, untuk red saja
		//System.out.println("P ("+x+","+y+") : "+partialBinaryString);	
		int n_bit = partialBinaryString.length();
		for (int i=0; i<pixels.size(); i++) {
			if ((pixels.get(i).x == x) && (pixels.get(i).y == y)) {
				PixelPosition PP = pixels.get(i);
				if (c == 'r'){
					int red = PP.color[0];
					PP.color[0] = modifyPixel(red,partialBinaryString); //red doang
					//System.out.println("P ("+x+","+y+") -> "+String.format("%8s", Integer.toBinaryString(PP.color[0])).replace(' ', '0')+" n: "+n_bit);	
				} else if (c == 'g'){
					int green = PP.color[1];
					PP.color[1] = modifyPixel(green,partialBinaryString); //green doang
					//System.out.println("I ("+x+","+y+") -> "+String.format("%8s", Integer.toBinaryString(PP.color[1])).replace(' ', '0')+" n: "+n_bit);	
				}

				pixels.set(i,PP);
				
				break;	
			}
		}	
	}

	public String readStegoMessageUnit(int x, int y, int n, char c) {
		for (int i=0; i<pixels.size(); i++){
			if ((pixels.get(i).x == x) && (pixels.get(i).y == y)) {
				String temPx;

				if (c == 'r')
					temPx = String.format("%8s", Integer.toBinaryString(pixels.get(i).color[0])).replace(' ', '0');
				else if (c == 'g')
					temPx = String.format("%8s", Integer.toBinaryString(pixels.get(i).color[1])).replace(' ', '0');
				else
					temPx = "";

				//System.out.println("P ("+x+","+y+") : "+temPx+" N : "+n);
				return temPx.substring(8-n,8);
			}	
		}
		System.out.println("Kalau masuk sini, berarti gk ketemu di ("+x+","+y+") : "+n);
		return "";
	}

	private static int modifyPixel(int pixels, String biner){
		int modifiedPx = 0;
		String temPx = String.format("%8s", Integer.toBinaryString(pixels)).replace(' ', '0');

		StringBuilder sb = new StringBuilder(temPx);

		//System.out.println("Awal: "+temPx);
		int pxLen = temPx.length();
		int binLen = biner.length();

		int count=0;
		for (int i=(pxLen-binLen);i<pxLen;i++){
			sb.setCharAt(i,biner.charAt(count));
			count++;
		}

		//System.out.println("Akhir: "+sb.toString());
		return Integer.parseInt(sb.toString(), 2);
	}
}

class MainLogic {
	PixelMapping P;
	int width;
	int height;
	
	//public byte[] fileData;
	
	public String str_file; //binary string

	public MainLogic(){
		P  = new PixelMapping();
		width = 0;
		height = 0;
	}

	/* Pembacaan File */


	public void readFiletoBinary(String pesan, String key){
		str_file="";
		try {
			String str;
			System.out.println("String mau dienkrip: "+pesan.length());
		    str = VigenereExtended.Enkrip(key,pesan);
		 	//System.out.println("Setelah enkrip-asli: "+str);

		 	for (int i=0;i<str.length();i++){
				str_file = str_file + String.format("%8s", Integer.toBinaryString( CharToASCII (str.charAt(i)) )).replace(' ', '0');
			}

			System.out.println("String setelah dienrkip: "+str_file.length());
		 	//str_file = str;
	 	} catch (Exception e){}
	}

	private static int CharToASCII(final char character){
		return (int)character;
	}

	/* Konversi Binary String ke Binary Array and Vice Versa */
	private String toBinary( byte[] bytes ) {
	    StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
	    for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
	        sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	    return sb.toString();
	}

	private byte[] fromBinary( String s ) {
	    int sLen = s.length();
	    byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
	    char c;
	    for( int i = 0; i < sLen; i++ )
	        if( (c = s.charAt(i)) == '1' )
	            toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
	        else if ( c != '0' )
	            throw new IllegalArgumentException();
	    return toReturn;
	}

	public int readStegoSize(int max_value){
		for (int i=0; i<P.pixels.size(); i++){
			//System.out.println("Search : "+P.pixels.get(i).x+"-"+P.pixels.get(i).y);
			if ((P.pixels.get(i).x == max_value) && (P.pixels.get(i).y == max_value)) {
				PixelPosition PP = P.pixels.get(i);
				String red = String.format("%8s", Integer.toBinaryString(PP.color[0])).replace(' ', '0');
				String green = String.format("%8s", Integer.toBinaryString(PP.color[1])).replace(' ', '0');
				String blue = String.format("%8s", Integer.toBinaryString(PP.color[2])).replace(' ', '0');

				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(red);
				stringBuilder.append(green);
				stringBuilder.append(blue);

				return Integer.parseInt(stringBuilder.toString(), 2);
			}
		}
		return -1;
	}

	public String readStegoMessage(String type){ //sementara jumlah biner yang harus diambil itu dihitung
		int local_count=0;
		try {
			if (type.equals("<ISI FILE>"))
				System.out.println("Membaca pesan stego (ISI FILE)...");
			else 
				System.out.println("Membaca pesan stego (JUDUL FILE)...");

			
			boolean isDone = false;

			int min_width=0;
			int max_width=2;
			int min_height=0;
			int max_height=2;

			int max_value;
			if (width > height) max_value = height;
			else max_value = width;

			String readBinaryMsg = "";
			int byte_count;

			if (type.equals("<ISI FILE>"))
				byte_count = readStegoSize(max_value-1);
			else
				byte_count = readStegoSize(max_value-2);

			System.out.println("Message Size Read Stego Message: "+byte_count);

			

			while (!isDone){
				if (max_width > max_value-2){
					max_width = 2;
					min_width = 0;
					min_height+=3;
					max_height+=3;
				} else if (max_height > max_value-2) isDone = true;
				else{
					int pxSignature = P.getPixel(max_width,max_height,'r'); //ambil bit ke-8
					String temPx = String.format("%8s", Integer.toBinaryString(pxSignature)).replace(' ', '0');
					String pixelBit = temPx.substring(6,8);

					//System.out.println("ganti blok: ("+max_width+","+max_height+") : "+pixelBit);

					if (pixelBit.matches("00")){
						for (int h = min_height; h <= max_height; h++) {
							for (int w = min_width; w <= max_width; w++) {
								if (local_count >= byte_count) {
										isDone = true;
										break;
								}else {
									if (!((h == max_height) && (w == max_width))) {
										if (type.equals("<ISI FILE>")){
											String am = P.readStegoMessageUnit(w,h,2,'r');
											readBinaryMsg += am;
											//System.out.println("X: "+w+" Y: "+h+" N: 2");
											//assert(am.length() == 2);
										}
										else
											readBinaryMsg += P.readStegoMessageUnit(w,h,2,'g');

										local_count+=2;
										//System.out.println("LocalCount: "+local_count);
									}
								}
							}
						}
					} else if (pixelBit.matches("01")){
						for (int h = min_height; h <= max_height; h++) {
							for (int w = min_width; w <= max_width; w++) {
								if (local_count >= byte_count) {
										isDone = true;
										break;
								}
								else {
									if (!((h == max_height) && (w == max_width))) {
										if (type.equals("<ISI FILE>")){
											String am = P.readStegoMessageUnit(w, h, 3, 'r');
											readBinaryMsg += am;
											//System.out.println("X: "+w+" Y: "+h+" N: 3");
											//assert(am.length() == 3);
										}
										else
											readBinaryMsg += P.readStegoMessageUnit(w,h,3,'g');

										local_count+=3;
										//System.out.println("LocalCount: "+local_count);
									}
								}
							}
						}
					} else if (pixelBit.matches("10")){
						for (int h = min_height; h <= max_height; h++) {
							for (int w = min_width; w <= max_width; w++) {
								if (local_count >= byte_count) {
										isDone = true;
										break;
								}
								else {
									if (!((h == max_height) && (w == max_width))) {
										if (type.equals("<ISI FILE>")){
											String am = P.readStegoMessageUnit(w,h,4,'r');
											readBinaryMsg += am;
											//System.out.println("X: "+w+" Y: "+h+" N: 4");
											//assert(am.length() == 4);
										}
										else
											readBinaryMsg += P.readStegoMessageUnit(w,h,4,'g');

										local_count+=4;	
										//System.out.println("LocalCount: "+local_count);
									}
								}
							}
						}
					} else {
						for (int h = min_height; h <= max_height; h++) {
							for (int w = min_width; w <= max_width; w++) {
								if (local_count >= byte_count) {
										isDone = true;
										break;
								}else {

									if (!((h == max_height) && (w == max_width))) {
										if (type.equals("<ISI FILE>")) {
											String am = P.readStegoMessageUnit(w,h,5,'r');
											readBinaryMsg += am;
											//System.out.println("X: "+w+" Y: "+h+" N: 5");
											//System.out.println("Kalo errror: "+am);
											//assert(am.length() == 5);
										}
										else
											readBinaryMsg += P.readStegoMessageUnit(w,h,5,'g');

										local_count+=5;
										//System.out.println("LocalCount: "+local_count);
									}
								}
							}
						}
					}
					/*
					for (int h = min_height; h <= max_height; h++) {
						for (int w = min_width; w <= max_width; w++) {

						}
					}
					*/
					
					min_width += 3;
					max_width += 3;
				}

			}
			//readBinaryMsg = readBinaryMsg.substring(0,byte_count);
			System.out.println("Biner terbaca panjang: "+readBinaryMsg.length());
			System.out.println("Local Count: "+local_count);

			if (type.equals("<ISI FILE>"))
				return readBinaryMsg;
			else
				return new String(fromBinary(readBinaryMsg),"UTF-8");
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}


	public String readStegoNameFile(){
		return "";
	}

	public void writeStegoSize(int size, int max_value, int type){
		//nulis di pixel terakhir
		String temPx = String.format("%24s", Integer.toBinaryString(size)).replace(' ', '0');
		//System.out.println("Size binary: "+temPx);

		String red = temPx.substring(0,8);
		String green = temPx.substring(8,16);
		String blue = temPx.substring(16,24);

		System.out.println("Size binary: "+red+green+blue);
		if (type == 0) //0 maka nulis size dari file
			P.overridePixel(max_value-1,max_value-1,Integer.parseInt(red, 2),Integer.parseInt(green, 2),Integer.parseInt(blue, 2));
		else
			P.overridePixel(max_value-2,max_value-2,Integer.parseInt(red, 2),Integer.parseInt(green, 2),Integer.parseInt(blue, 2));	
	}

	/* WriteStegoMessage */
	/*
	public void writeStegoNameFile(String filename){
		byte[] encoded = filename.getBytes(StandardCharsets.UTF_8);
		String binaryMsg = toBinary(encoded);
		System.out.println("Write Namefile: "+binaryMsg);

		int min_width=0;
		int max_width=2;
		int min_height=0;
		int max_height=2;

		int max_value;
		if (width > height) max_value = height;
		else max_value = width;

		writeStegoSize(binaryMsg.length(),max_value,1);

		int msg_offset = 0;


	} */

	public void writeStegoMessage(String file_name){
		//increment setiap 3 pixel X dan 3 pixel Y
		if (file_name.equals("<ISI FILE>"))
			System.out.println("MENULIS PESAN STEGO (ISI)...");
		else
			System.out.println("MENULIS PESAN STEGO (JUDUL)...");

		int min_width=0;
		int max_width=2;
		int min_height=0;
		int max_height=2;

		System.out.println("Padahal sudah dirubah");
		int max_value;
		if (width > height) max_value = height;
		else max_value = width;

		String msg;

		if (file_name.equals("<ISI FILE>"))
			msg = str_file;
		else{
			byte[] encoded = file_name.getBytes(StandardCharsets.UTF_8);
			msg = toBinary(encoded);
		}

		int msg_length = msg.length();
		
		if (file_name.equals("<ISI FILE>"))
			writeStegoSize(msg_length,max_value,0);
		else
			writeStegoSize(msg_length,max_value,1);

		System.out.println("Panjang Pesan Write: "+msg_length);
		//System.out.println("Biner terbaca: "+msg);
		//System.out.println(bs.getBytes());

		int msg_offset = 0;

		boolean isStegoDone = false;

		while (!isStegoDone){
			
			if (max_width > max_value-2){
				max_width = 2;
				min_width = 0;
				min_height+=3;
				max_height+=3;
			} else if (max_height > max_value-2) {
				isStegoDone = true;
				System.out.println("Pesan stego melebihi batas ukuran");
			}
			else{

				int[] blocks = new int[9];
				int min_blocks=99999;

				int count=0;
				for (int h = min_height; h <= max_height; h++) {
					for (int w = min_width; w <= max_width; w++) {
						blocks[count] = P.getPixel(h,w,'a');

						if (blocks[count] < min_blocks)
							min_blocks = blocks[count];

						count++;
					}
				}

				int jumlah_selisih = 0;
				for (int y=0; y < count; y++){
					jumlah_selisih = jumlah_selisih + (blocks[y] - min_blocks);
				}

				int d = jumlah_selisih / 8;
				int movePointer;
				/* Algoritma insertStego */

				//System.out.println("ganti blok: ("+max_width+","+max_height+") : "+d);
				boolean isLewat = false;
				for (int h = min_height; h <= max_height; h++) {
					for (int w = min_width; w <= max_width; w++) {
						if ((h == max_height) && (w == max_width)) {
							if (d <= 7){	
								if (file_name.equals("<ISI FILE>"))
									P.insertStegoMessage(w,h,'r',"00");	//2-lsb	
								else
									P.insertStegoMessage(w,h,'g',"00");	//2-lsb
							}
							else if ((d >= 8) && (d <= 15)) {
								if (file_name.equals("<ISI FILE>"))
									P.insertStegoMessage(w,h,'r',"01");	//3-lsb
								else
									P.insertStegoMessage(w,h,'g',"01");	//3-lsb
							}
							else if ((d >= 16) && (d <= 31)) {
								if (file_name.equals("<ISI FILE>"))
									P.insertStegoMessage(w,h,'r',"10");	//4-lsb
								else
									P.insertStegoMessage(w,h,'g',"10");	//4-lsb
							} else {
								if (file_name.equals("<ISI FILE>"))
									P.insertStegoMessage(w,h,'r',"11");	//5-lsb
								else
									P.insertStegoMessage(w,h,'g',"11");	//5-lsb
							}
						} else {
							if (!isLewat) {
								if (d <= 7){	
									movePointer = 2;		
								}
								else if ((d >= 8) && (d <= 15)) {
									movePointer = 3;
								}
								else if ((d >= 16) && (d <= 31)) {
									movePointer = 4;
								} else {
									movePointer = 5;
								}

								msg_offset+=movePointer;
								int selisih_error;

								if (msg_offset > msg_length){
									if (file_name.equals("<ISI FILE>"))
										P.insertStegoMessage(w,h,'r',msg.substring(msg_offset-movePointer,msg_length));
									else
										P.insertStegoMessage(w,h,'g',msg.substring(msg_offset-movePointer,msg_length));
								} else {
									if (file_name.equals("<ISI FILE>"))
										P.insertStegoMessage(w,h,'r',msg.substring(msg_offset-movePointer,msg_offset));
									else
										P.insertStegoMessage(w,h,'g',msg.substring(msg_offset-movePointer,msg_offset));
								}

								if (msg_offset >= msg_length) {
									isStegoDone = true;
									isLewat = true;
								}
							}
							
						}
					}
				}

				//System.out.println("d("+max_width+","+max_height+") : "+d);
				/* Naikkan widthnya */
				
				min_width += 3;
				max_width += 3;


			}
		}

	}

	// Convert R, G, B, Alpha to standard 8 bit
    private static int colorToRGB(int alpha, int red, int green, int blue) {
 
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red; newPixel = newPixel << 8;
        newPixel += green; newPixel = newPixel << 8;
        newPixel += blue;
 
        return newPixel;
 
    }
    
	/*Image Processing */
	public BufferedImage writeImage(){
		try {
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			WritableRaster raster = image.getRaster();
			System.out.println("Menulis Image");
			System.out.println("Height: "+height+" - Width: "+width);

			for (int i=0; i<P.pixels.size(); i++) {
				PixelPosition PPs = P.pixels.get(i);
				raster.setPixel(PPs.x,PPs.y,PPs.color);
			}

			//ImageIO.write(image,tipe,new File(outfile));
			return image;
		} catch (Exception e){e.printStackTrace(); return null;}
	}

	public void readImage(BufferedImage image){
		System.out.println("Sedang membaca image...");
	    
		try {
         width = image.getWidth();
         height = image.getHeight();
         
         for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
               Color c = new Color(image.getRGB(j, i));
               //System.out.println("Pos("+j+","+i+") ->   Red: " + c.getRed() +"  Green: " + c.getGreen() + " Blue: " + c.getBlue());
               P.insertPixel(j,i,c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()); //disimpan
            }
         }
         System.out.println("Selesai membaca image...");
      } catch (Exception e) {e.printStackTrace();}
	}


	/* File processing 
	public void writeFile(String lokasi, String str_out) {
		try {
			FileOutputStream out = null;
			String output_str = str_out;
			try {
	        	out = new FileOutputStream(lokasi);
	         
	        	for (int i=0;i<output_str.length();i++){
	        		char c = output_str.charAt(i);
	        		out.write((int)c);
	        	}
	      
	      	}finally {
	        	if (out != null) {
	        		out.close();
	        	}
	     	}
	     }catch (Exception e){} 
	} */

	public void writeFile(String lokasi, String inject_file, String key) throws Exception {
		String plain="";
		String token="";
		boolean isDone=false;
		int i=0;
		while (!isDone) {
			if (token.length() >= 8) {
				plain = plain + (char)Integer.parseInt(token,2);
				token = "";
			}
			else if (i >= inject_file.length()) {
				isDone = true;
			} else {
				token = token + inject_file.charAt(i);
				i++;
			}
		}

		//System.out.println("String setelah diambil: "+plain);

		FileOutputStream out = null;
		String output_str = VigenereExtended.Dekrip(key,plain);

		try {
        	out = new FileOutputStream(lokasi);
         
        	for (int j=0;j<output_str.length();j++){
        		char c = output_str.charAt(j);
        		out.write((int)c);
        	}
         	/*
        	int c;
        	while ((c = in.read()) != -1) {
        		str = str + ((char)c);
        		out.write(c);
        	}*/
      
      	}finally {
        	if (out != null) {
        		out.close();
        	}
     	}
	}

}
