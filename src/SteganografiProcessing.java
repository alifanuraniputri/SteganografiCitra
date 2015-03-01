import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class SteganografiProcessing {

	private BufferedImage citra;
	private BufferedImage steganoCitra;
	private String kunci;
	private String pesan;
	private String namaFile;
	private int seed;

	public SteganografiProcessing(BufferedImage steganoCitra, String kunci) {
		super();
		this.steganoCitra = steganoCitra;
		this.kunci = kunci;
		this.seed = 0;
		for (int i = 0; i < kunci.length(); i++) {
			this.seed += (int) kunci.charAt(i);
		}
	}

	public SteganografiProcessing(BufferedImage chosen, String kunci,
			String pesan, String namaFile) {
		super();
		this.citra = chosen;
		this.kunci = kunci;
		this.pesan = VigenereExtended.Enkrip(kunci,(namaFile+'@'+pesan));
		//this.pesan = pesan;
		System.out.println(this.pesan);
		this.seed = 0;
		for (int i = 0; i < kunci.length(); i++) {
			this.seed += (int) kunci.charAt(i);
		}
		
	}

	public BufferedImage sisipkanLSBstandard() {
		//
		try {
			steganoCitra = new BufferedImage(citra.getWidth(), citra.getHeight(),
					citra.getType());
			byte[] bytePesan = pesan.getBytes("UTF-8");
			sisipkanPanjangPesan(bytePesan.length);
			sisipkanPesanLSBStandard(bytePesan);
		} catch (Exception e) {}
		return steganoCitra;
	}
	
	public BufferedImage sisipkanLSBstandard8bit() {
		System.out.println("8bit");
		try {
			steganoCitra = new BufferedImage(citra.getWidth(), citra.getHeight(),
					BufferedImage.TYPE_BYTE_GRAY);
			byte[] bytePesan = pesan.getBytes("UTF-8");
			sisipkanPanjangPesan8bit(bytePesan.length);
			sisipkanPesanLSBStandard8bit(bytePesan);
		} catch (Exception e) {}
		return steganoCitra;
	}
	
	public String getPlainTextLSBstandard() {
		int panjang = getPanjangPesan();
		getPlainLSB(panjang);
		return pesan;
	}

	public String getPlainTextLSBstandard8bit() {
		System.out.println("8bit");
		int panjang = getPanjangPesan8bit();
		System.out.println(panjang);
		getPlainLSB8bit(panjang);
		return pesan;
	}
	
	public void getPlainLSB8bit(int panjang) {
		String bit = "";
		int nBit = panjang * 8;
		Random rand = new Random(this.seed);
		int max = steganoCitra.getHeight() * steganoCitra.getWidth();
		int min = 32;
		Set<Integer> acak = new LinkedHashSet<Integer>();
		int[] lokasi = new int[nBit];

		// generate random w/ seed
		while (acak.size() < ( nBit)) {
			Integer next = rand.nextInt((max - min) + 1) + min;
			acak.add(next);
		}
		int count = 0;
		for (int number : acak) {
			lokasi[count] = number;
			count++;
		}
		System.out.println(Arrays.toString(lokasi));
		// ekstrak LSB
		System.out.println("panjang: "+nBit);
		for (int i = 0; i < lokasi.length; i++) {
			int x = lokasi[i] % steganoCitra.getWidth();
			int y = lokasi[i] / steganoCitra.getWidth();
			int pixel = steganoCitra.getRGB(x, y);
			
			int red = (pixel >> 0) & 0x000000FF;
			//System.out.print(Integer.toBinaryString(red));
			// red
			int bitPesan = getBitValue(red, 0);
			//System.out.println(" "+bitPesan);
			bit = bit + bitPesan;
			
		}
		System.out.println(bit);
		byte[] bytes = new BigInteger(bit, 2).toByteArray();
		try {
			pesan = new String(bytes, "UTF-8");
			System.out.println( Arrays.toString(bytes));
			System.out.println(pesan);
			pesan = VigenereExtended.Dekrip(kunci, pesan);
		} catch (Exception e) {

		}
		namaFile = "";
		boolean found = false;
		for (int i=0; i<pesan.length() && found==false; i++) {
			if (pesan.charAt(i)=='.' || pesan.charAt(i)=='@') {
				found =true;
			}
			if (found==false) {
				namaFile = namaFile + pesan.charAt(i);
			}
			if (found==true) {
				if (pesan.charAt(i)=='.') {
					namaFile=namaFile+'.';
					boolean foundpesan = false;
					for (int j=(i+1) ; j<pesan.length() && foundpesan==false; j++) {
						if (pesan.charAt(j)=='@') {
							foundpesan =true;
						}
						if (foundpesan==false) {
							namaFile = namaFile + pesan.charAt(j);
						}
						if (foundpesan==true) {
							pesan = pesan.substring(j+1);
						}
					}
				} else {
						pesan = pesan.substring(i+1);
				}
			}
		}
		System.out.println(this.pesan);
		System.out.println("nama : "+namaFile);
	}

	public void getPlainLSB(int panjang) {
		String bit = "";
		int nBit = panjang * 8;
		Random rand = new Random(this.seed);
		int max = steganoCitra.getHeight() * steganoCitra.getWidth();
		int min = 11;
		Set<Integer> acak = new LinkedHashSet<Integer>();
		int[] lokasi = new int[(int) Math.ceil((double) nBit / 3)];

		// generate random w/ seed
		while (acak.size() < ((int) Math.ceil((double) nBit / 3))) {
			Integer next = rand.nextInt((max - min) + 1) + min;
			acak.add(next);
		}
		int count = 0;
		for (int number : acak) {
			lokasi[count] = number;
			count++;
		}

		// ekstrak LSB
		System.out.println("panjang: "+nBit);
		int numBit = 0;
		for (int i = 0; i < lokasi.length && numBit < nBit; i++) {
			int x = lokasi[i] % steganoCitra.getWidth();
			int y = lokasi[i] / steganoCitra.getWidth();
			int pixel = steganoCitra.getRGB(x, y);
			int red = (pixel >> 16) & 0x000000FF, green = (pixel >> 8) & 0x000000FF, blue = (pixel) & 0x000000FF;
			// red
			int bitPesan = getBitValue(red, 0);
			bit = bit + bitPesan;
			numBit++;
			// green
			if (numBit < nBit) {
				bitPesan = getBitValue(green, 0);
				bit = bit + bitPesan;
				numBit++;
			}
			// blue
			if (numBit < nBit) {
				bitPesan = getBitValue(blue, 0);
				bit = bit + bitPesan;
				numBit++;
			}
		}
		System.out.println(bit);
		byte[] bytes = new BigInteger(bit, 2).toByteArray();
		try {
			pesan = new String(bytes, 1, panjang, "UTF-8");
			System.out.println( Arrays.toString(bytes));
			System.out.println(pesan);
			pesan = VigenereExtended.Dekrip(kunci, pesan);
		} catch (Exception e) {

		}

		namaFile = "";
		boolean found = false;
		for (int i=0; i<pesan.length() && found==false; i++) {
			if (pesan.charAt(i)=='.' || pesan.charAt(i)=='@') {
				found =true;
			}
			if (found==false) {
				namaFile = namaFile + pesan.charAt(i);
			}
			if (found==true) {
				if (pesan.charAt(i)=='.') {
					namaFile=namaFile+'.';
					boolean foundpesan = false;
					for (int j=(i+1) ; j<pesan.length() && foundpesan==false; j++) {
						if (pesan.charAt(j)=='@') {
							foundpesan =true;
						}
						if (foundpesan==false) {
							namaFile = namaFile + pesan.charAt(j);
						}
						if (foundpesan==true) {
							pesan = pesan.substring(j+1);
						}
					}
				} else {
						pesan = pesan.substring(i+1);
				}
			}
		}
		System.out.println(this.pesan);
		System.out.println("nama : "+namaFile);
	}
	
	public String getNamaFile() {
		return namaFile;
	}

	public int getPanjangPesan() {
		int panjang = 0;
		int count = 0;
		for (int i = 0; i < 11 && count < 32; i++) {
			int x = i % steganoCitra.getWidth();
			int y = i / steganoCitra.getWidth();
			int pixel = steganoCitra.getRGB(x, y);
			int red = (pixel >> 16) & 0x000000FF, green = (pixel >> 8) & 0x000000FF, blue = (pixel) & 0x000000FF;

			// red
			int bitPanjangPesan = getBitValue(red, 0);
			panjang = setBitValue(panjang, count, bitPanjangPesan);
			count++;

			// green
			if (count < 32) {
				bitPanjangPesan = getBitValue(green, 0);
				panjang = setBitValue(panjang, count, bitPanjangPesan);
				count++;
			}
			// blue
			if (count < 32) {
				bitPanjangPesan = getBitValue(blue, 0);
				panjang = setBitValue(panjang, count, bitPanjangPesan);
				count++;
			}

		}
		return panjang;
	}
	
	public int getPanjangPesan8bit() {
		int panjang = 0;
		int count = 0;
		for (int i = 0; i < 32 && count < 32; i++) {
			int x = i % steganoCitra.getWidth();
			int y = i / steganoCitra.getWidth();
			int pixel = steganoCitra.getRGB(x, y);
			int red = (pixel >> 16) & 0x000000FF;

			// red
			int bitPanjangPesan = getBitValue(red, 0);
			panjang = setBitValue(panjang, count, bitPanjangPesan);
			count++;
			
			
		}
		System.out.println(Integer.toBinaryString(panjang));
		return panjang;
	}
	
	private int CharToASCII(final char character){
		return (int)character;
	}
	
	public void sisipkanPesanLSBStandard(byte[] bytePesan) {
		int nBit = bytePesan.length * 8;
		String bit = toBinary(bytePesan);
		System.out.println(bit);
		Random rand = new Random(this.seed);
		int max = citra.getHeight() * citra.getWidth();
		int min = 11;
		Set<Integer> acak = new LinkedHashSet<Integer>();
		int[] lokasi = new int[(int) Math.ceil((double) nBit / 3)];

		// generate random w/ seed
		while (acak.size() < ((int) Math.ceil((double) nBit / 3))) {
			Integer next = rand.nextInt((max - min) + 1) + min;
			acak.add(next);
		}
		int count = 0;
		for (int number : acak) {
			lokasi[count] = number;
			count++;
		}
		
		// edit LSB
		int numBit = 0;
		for (int i = 0; i < lokasi.length && numBit < nBit; i++) {
			int x = lokasi[i] % citra.getWidth();
			int y = lokasi[i] / citra.getWidth();
			int pixel = citra.getRGB(x, y);
			int bitPanjangPesan = Character.getNumericValue(bit.charAt(numBit));
			int alpha = (pixel >> 24) & 0x000000FF;
			int red = (pixel >> 16) & 0x000000FF, green = (pixel >> 8) & 0x000000FF, blue = (pixel) & 0x000000FF;
			// red
			red = setBitValue(red, 0, bitPanjangPesan);
			numBit++;
			// green
			if (numBit < nBit) {
				bitPanjangPesan = Character.getNumericValue(bit.charAt(numBit));
				green = setBitValue(green, 0, bitPanjangPesan);
				numBit++;
			}
			// blue
			if (numBit < nBit) {
				bitPanjangPesan = Character.getNumericValue(bit.charAt(numBit));
				blue = setBitValue(blue, 0, bitPanjangPesan);
				numBit++;
			}
			// new pixel
			int newPixel = (((int) alpha & 0xFF) << 24) | // alpha
					(((int) red & 0xFF) << 16) | // red
					(((int) green & 0xFF) << 8) | // green
					(((int) blue & 0xFF) << 0); // blue

			steganoCitra.setRGB(x, y, newPixel);
		}

		for (int i = 11; i < citra.getHeight() * citra.getWidth(); i++) {
			if (!acak.contains(i)) {
				int x = i % citra.getWidth();
				int y = i / citra.getWidth();
				steganoCitra.setRGB(x, y, citra.getRGB(x, y));
			}
		}
		System.out.println("done");
	}
	
	 private static int colorToRGB(int alpha, int red, int green, int blue) {
		 
	        int newPixel = 0;
	        newPixel += alpha;
	        newPixel = newPixel << 8;
	        newPixel += red; newPixel = newPixel << 8;
	        newPixel += green; newPixel = newPixel << 8;
	        newPixel += blue;
	 
	        return newPixel;
	 
	    }
	 
	public void sisipkanPesanLSBStandard8bit(byte[] bytePesan) {
		int nBit = bytePesan.length * 8;
		String bit = toBinary(bytePesan);
		System.out.println(bit);
		System.out.println(bit.length());
		Random rand = new Random(this.seed);
		int max = citra.getHeight() * citra.getWidth();
		int min = 32;
		Set<Integer> acak = new LinkedHashSet<Integer>();
		int[] lokasi = new int[ nBit];

		// generate random w/ seed
		while (acak.size() < (nBit)) {
			Integer next = rand.nextInt((max - min) + 1) + min;
			acak.add(next);
		}
		int count = 0;
		for (int number : acak) {
			lokasi[count] = number;
			count++;
		}
		System.out.println(Arrays.toString(lokasi));
		// edit LSB
		int numBit = 0;
		for (int i = 0; i < lokasi.length; i++) {
			int x = lokasi[i] % citra.getWidth();
			int y = lokasi[i] / citra.getWidth();
			int pixel = citra.getRGB(x, y);
			int bitPanjangPesan = Character.getNumericValue(bit.charAt(i));
			int red = (pixel >> 0) & 0x000000FF;
			// red
			System.out.print(Integer.toBinaryString(red) + " " + bitPanjangPesan + " ");
			red = setBitValue(red, 0, bitPanjangPesan);
			System.out.print(Integer.toBinaryString(red));
			
			numBit++;
		
			//int rgb = ((red&0x0ff)<<24) | ((red&0x0ff)<<16)|((red&0x0ff)<<8)|(red&0x0ff);
			int rgb = colorToRGB(255,red,red,red);
			
			System.out.print(" "+ Integer.toBinaryString(rgb) +" ");

			steganoCitra.setRGB(x, y, rgb);
			
			pixel = steganoCitra.getRGB(x, y);
			System.out.print(" "+ Integer.toBinaryString(pixel) +" ");
			//System.out.println(" tahu hasil "+ Integer.toBinaryString(red));
			red = (pixel >> 0) & 0xFF;
			System.out.println(" hasil "+ Integer.toBinaryString(red));
		}
/*
		for (int i = 32; i < citra.getHeight() * citra.getWidth(); i++) {
			if (!acak.contains(i)) {
				int x = i % citra.getWidth();
				int y = i / citra.getWidth();
				steganoCitra.setRGB(x, y, citra.getRGB(x, y));
			}
		} */
		System.out.println("done");
	}

	public void sisipkanPanjangPesan(int panjang) {
		// panjang |= (1 << 31);
		int widthX = citra.getWidth(), heightY = citra.getHeight();
		int startX = 0, startY = 0;
		int count = 0;
		for (int i = startY; i < heightY && count < 32; i++) {
			for (int j = startX; j < widthX && count < 32; j++) {
				int pixel = citra.getRGB(j, i);
				int bitPanjangPesan = getBitValue(panjang, count);
				int alpha = (pixel >> 24) & 0x000000FF;
				int red = (pixel >> 16) & 0x000000FF, green = (pixel >> 8) & 0x000000FF, blue = (pixel) & 0x000000FF;
				// red
				red = setBitValue(red, 0, bitPanjangPesan);
				count++;

				// green
				if (count < 32) {
					bitPanjangPesan = getBitValue(panjang, count);
					green = setBitValue(green, 0, bitPanjangPesan);
					count++;
				}
				// blue
				if (count < 32) {
					bitPanjangPesan = getBitValue(panjang, count);
					blue = setBitValue(blue, 0, bitPanjangPesan);
					count++;
				}
				// new pixel
				int newPixel = (((int) alpha & 0xFF) << 24) | // alpha
						(((int) red & 0xFF) << 16) | // red
						(((int) green & 0xFF) << 8) | // green
						(((int) blue & 0xFF) << 0); // blue

				steganoCitra.setRGB(j, i, newPixel);
			}
		}

	}
	
	public void sisipkanPanjangPesan8bit(int panjang) {
		// panjang |= (1 << 31);
		System.out.println(panjang);
		System.out.println(Integer.toBinaryString(panjang));
		int widthX = citra.getWidth(), heightY = citra.getHeight();
		int startX = 0, startY = 0;
		int count = 0;
		for (int i = startY; i < heightY && count < 32; i++) {
			for (int j = startX; j < widthX && count < 32; j++) {
				int pixel = citra.getRGB(j, i);
				int bitPanjangPesan = getBitValue(panjang, count);
				int red = (pixel >> 0) & 0x000000FF;
				// red
				red = setBitValue(red, 0, bitPanjangPesan);
				count++;

				
				// new pixel
				int rgb = (((int) red & 0xFF) << 24) | (((int) red & 0xFF) << 16) | // red
						(((int) red & 0xFF) << 8) | // green
						(((int) red & 0xFF) << 0);

				steganoCitra.setRGB(j, i, rgb);
			}
		}

	}

	private int getBitValue(int n, int location) {
		int v = n & (int) Math.round(Math.pow(2, location));
		return v == 0 ? 0 : 1;
	}

	private int setBitValue(int n, int location, int bit) {
		int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
		if (bv == bit)
			return n;
		if (bv == 0 && bit == 1)
			n |= toggle;
		else if (bv == 1 && bit == 0)
			n ^= toggle;
		return n;
	}

	String toBinary(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for (int i = 0; i < Byte.SIZE * bytes.length; i++)
			sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
					: '1');
		return sb.toString();
	}
}