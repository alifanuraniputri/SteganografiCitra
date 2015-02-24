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
			String pesan) {
		super();
		this.citra = chosen;
		this.kunci = kunci;
		this.pesan = enkripsiASCII(pesan, kunci);
		//this.pesan = pesan;
		System.out.println(this.pesan);
		this.seed = 0;
		for (int i = 0; i < kunci.length(); i++) {
			this.seed += (int) kunci.charAt(i);
		}
		steganoCitra = new BufferedImage(citra.getWidth(), citra.getHeight(),
				BufferedImage.TYPE_INT_RGB);
	}

	public BufferedImage sisipkanLSBstandard() {
		try {
			byte[] bytePesan = pesan.getBytes("UTF-8");
			sisipkanPanjangPesan(bytePesan.length);
			sisipkanPesanLSBStandard(bytePesan);
		} catch (Exception e) {}
		return steganoCitra;
	}

	public String getPlainTextLSBstandard() {
		int panjang = getPanjangPesan();
		getPlainLSB(panjang);
		return pesan;
	}

	public void sisipkanLSBXinLiao() {

	}

	public void sisipkanLSBGhandarba() {

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
			this.pesan = new String(bytes, 1, panjang, "UTF-8");
			System.out.println( Arrays.toString(bytes));
			System.out.println(this.pesan);
			this.pesan = dekripsiASCII(this.pesan, kunci);
		} catch (Exception e) {

		}
		System.out.println(this.pesan);
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

	public String enkripsiASCII(String plain, String kunci) {

		int[] plainArr = new int[plain.length()];
		for (int i = 0; i < plain.length(); i++) {
			plainArr[i] = ((int) plain.charAt(i) + kunci.charAt(i
					% kunci.length())) % 256;
		}
		String cipher = "";
		for (int i = 0; i < plainArr.length; i++) {
			cipher = cipher + Character.toString((char) (plainArr[i]));
		}
		System.out.println(cipher);
		return cipher;
	}

	public String dekripsiASCII(String cipher, String kunci) {

		int[] cipherArr = new int[cipher.length()];
		for (int i = 0; i < cipher.length(); i++) {
			cipherArr[i] = Math.abs(((int) cipher.charAt(i) - kunci.charAt(i
					% kunci.length()))) % 256;
		}
		String plain = "";
		for (int i = 0; i < cipherArr.length; i++) {
			plain = plain + Character.toString((char) (cipherArr[i]));
		}
		return plain;
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
