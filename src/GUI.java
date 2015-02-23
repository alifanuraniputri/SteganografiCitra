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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

public class GUI extends JFrame {

	private JPanel contentPane;
	private final JButton btnSelectPicture = new JButton("Select Picture");
	private final JLabel lblImage = new JLabel("image");
	private final JTextField textKey = new JTextField();
	private final JLabel lblKataKunci = new JLabel("Kata Kunci");
	private final JLabel lblNamaFile = new JLabel("Nama File");
	private final JTextField textFile = new JTextField();
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
	private String pesan;
	private int mode = 1;
	private final JButton btnSimpanPlain = new JButton("Simpan Plain Teks");
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea txtTeks = new JTextArea();

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
		textFile.setBounds(344, 138, 243, 30);
		textFile.setColumns(10);
		textKey.setForeground(Color.BLACK);
		textKey.setBounds(344, 71, 243, 30);
		textKey.setColumns(10);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 631, 499);
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
		lblImage.setBounds(30, 76, 249, 249);

		contentPane.add(lblImage);

		contentPane.add(textKey);
		lblKataKunci.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblKataKunci.setBounds(344, 50, 105, 10);

		contentPane.add(lblKataKunci);
		lblNamaFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNamaFile.setBounds(344, 113, 91, 14);

		contentPane.add(lblNamaFile);

		contentPane.add(textFile);
		btnSisipkan.setEnabled(false);
		btnSisipkan.setBackground(new Color(255, 105, 180));
		btnSisipkan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sisipkanPesan(mode);
				txtTeks.setEnabled(true);
			}
		});
		btnSisipkan.setBounds(344, 217, 243, 23);

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
		btnEkstrakPesan.setBounds(344, 285, 243, 23);

		contentPane.add(btnEkstrakPesan);
		btnSimpanCitra.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simpanCitra();
			}
		});
		btnSimpanCitra.setEnabled(false);
		btnSimpanCitra.setBackground(new Color(255, 105, 180));
		btnSimpanCitra.setBounds(344, 251, 243, 23);

		contentPane.add(btnSimpanCitra);
		lblTubesIfKriptografi.setForeground(new Color(255, 20, 147));
		lblTubesIfKriptografi.setBounds(401, 385, 135, 30);

		contentPane.add(lblTubesIfKriptografi);
		lblVaiHabibie.setForeground(new Color(255, 20, 147));
		lblVaiHabibie.setBounds(421, 414, 97, 14);

		contentPane.add(lblVaiHabibie);
		
		radioMode1.setSelected(true);
		radioMode1.setBackground(new Color(255, 240, 245));
		radioMode1.setBounds(344, 187, 43, 23);
		radioMode1.setMnemonic(KeyEvent.VK_C);
		
		radioMode1.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {         
	             mode=1;
	          }           
	       });

		contentPane.add(radioMode1);
		radioMode2.setBackground(new Color(255, 240, 245));
		radioMode2.setBounds(389, 187, 81, 23);
		radioMode2.setMnemonic(KeyEvent.VK_M);
		radioMode2.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {         
	             mode=2;
	          }           
	       });

		contentPane.add(radioMode2);
		radioMode3.setBackground(new Color(255, 240, 245));
		radioMode3.setBounds(472, 187, 137, 23);
		radioMode3.setMnemonic(KeyEvent.VK_P);
		radioMode3.addItemListener(new ItemListener() {
	         public void itemStateChanged(ItemEvent e) {         
	             mode=3;
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
		btnSimpanPlain.setBounds(346, 319, 241, 23);

		contentPane.add(btnSimpanPlain);
		scrollPane.setBounds(30, 347, 249, 81);

		contentPane.add(scrollPane);
		txtTeks.setEnabled(false);

		scrollPane.setViewportView(txtTeks);
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
					final File picture = fileChooser.getSelectedFile();

					try {
						chosen = ImageIO.read(picture);

					} catch (final IOException not_action) {
						not_action.printStackTrace();
					}

					lblImage.setIcon(new ImageIcon(chosen.getScaledInstance(
							lblImage.getWidth(), lblImage.getHeight(),
							BufferedImage.TRANSLUCENT)));
					getContentPane().add(lblImage, BorderLayout.CENTER);
					lblImage.setVisible(true);

				}
			}
		});
		btnSisipkan.setEnabled(true);
		btnEkstrakPesan.setEnabled(true);
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
		String kunci = textKey.getText();
		if (!textKey.getText().equals("")) {
			stegano = new SteganografiProcessing(chosen, kunci);
			switch (mode) {
			case 1:
				pesan = stegano.getPlainTextLSBstandard();
			case 2:
				stegano.sisipkanLSBXinLiao();
			case 3:
				stegano.sisipkanLSBGhandarba();
			}
			txtTeks.setText(pesan);
			txtTeks.setEnabled(false);
		} else {
			JOptionPane.showMessageDialog(getContentPane(), "Masukkan Kunci");
		}
	}

	public void sisipkanPesan(int mode) {
		String kunci = textKey.getText();
		String pesan = "";

		if (!textFile.getText().equals("") && !textKey.getText().equals("")) {
			if ((pesan.length() * 8 + 11) <= (chosen.getHeight()
					* chosen.getWidth() * 3)) {
				String fileName = textFile.getText();
				pesan = bacaFilePesan(fileName);
				stegano = new SteganografiProcessing(chosen, kunci, pesan);
				switch (mode) {
				case 1:
					result = stegano.sisipkanLSBstandard();
				case 2:
					stegano.sisipkanLSBXinLiao();
				case 3:
					stegano.sisipkanLSBGhandarba();
				}
				lblImage.setIcon(new ImageIcon(result.getScaledInstance(
						lblImage.getWidth(), lblImage.getHeight(),
						BufferedImage.TRANSLUCENT)));
				getContentPane().add(lblImage, BorderLayout.CENTER);
				lblImage.setVisible(true);
				JOptionPane.showMessageDialog(getContentPane(),
						"pesan telah disisipkan");
				btnEkstrakPesan.setEnabled(false);
				btnSimpanCitra.setEnabled(true);
			} else {
				JOptionPane.showMessageDialog(getContentPane(),
						"Pesan terlalu panjang untuk gambar yang dipilih");
			}

		} else {
			JOptionPane.showMessageDialog(getContentPane(),
					"isi Nama File Terlebih Dahulu");
		}

	}

	public void simpanPlain() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setApproveButtonText("Save");
				FileFilter filter = new FileNameExtensionFilter(
						"input file text (.txt)", "txt");
				fileChooser.setDialogTitle("Specify a file to save");
				fileChooser.setFileFilter(filter);

				final int userValue = fileChooser.showOpenDialog(fileChooser);

				if (userValue == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fileChooser.getSelectedFile();

					BufferedWriter writer = null;
					try {
						writer = new BufferedWriter(new FileWriter(fileChooser
								.getSelectedFile()));
						writer.write(pesan);

					} catch (IOException e) {
					} finally {
						try {
							if (writer != null)
								writer.close();
							JOptionPane.showMessageDialog(getContentPane(),
									"file saved");
						} catch (IOException e) {
						}
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
				FileFilter filter = new FileNameExtensionFilter("PNG (.png)",
						"png");
				fileChooser.setDialogTitle("Save Stegano Image");
				fileChooser.setFileFilter(filter);

				final int userValue = fileChooser.showOpenDialog(fileChooser);

				if (userValue == JFileChooser.APPROVE_OPTION) {

					BufferedWriter writer = null;
					// write it new file
					try {
						if (fileChooser.getSelectedFile().getAbsolutePath()
								.contains(".png"))
							ImageIO.write(result, "PNG", new File(fileChooser
									.getSelectedFile().getAbsolutePath()));
						else
							ImageIO.write(result, "PNG", new File(fileChooser
									.getSelectedFile().getAbsolutePath()
									+ ".png"));
						JOptionPane.showMessageDialog(getContentPane(),
								"citra berpesan terseimpan");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
