/* Ada modul read & write pixel image */

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO; 
import java.awt.*;
import java.io.*;
import javax.swing.JFrame;
import java.util.*;
import java.nio.file.*;

class PixelPosition {
	public int x;
	public int y;
	public int[] color = new int[3];

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

	public void insertPixel(int x, int y, int r, int g, int b){
		PixelPosition PP = new PixelPosition();
		PP.x = x;
		PP.y = y;
		PP.color = new int[]{r,g,b};

		pixels.add(PP);
	}


	public void insertStegoMessage(int x, int y, String partialBinaryString){
		//bakal make overridePixel, untuk red saja
		//System.out.println("P ("+x+","+y+") : "+partialBinaryString);	
		for (int i=0; i<pixels.size(); i++) {
			if ((pixels.get(i).x == x) && (pixels.get(i).y == y)) {
				PixelPosition PP = pixels.get(i);
				int red = PP.color[0];
				PP.color[0] = modifyPixel(red,partialBinaryString); //red doang
				pixels.set(i,PP);
				//System.out.println("P ("+x+","+y+") -> "+String.format("%8s", Integer.toBinaryString(PP.color[0])).replace(' ', '0'));	
				break;			
			}
		}	
	}

	public String readStegoMessage(int x, int y, int n) {
		for (int i=0; i<pixels.size(); i++){
			if ((pixels.get(i).x == x) && (pixels.get(i).y == y)) {
				String temPx = String.format("%8s", Integer.toBinaryString(pixels.get(i).color[0])).replace(' ', '0');
				//System.out.println("P ("+x+","+y+") : "+temPx);
				return temPx.substring(8-n,8);
			}	
		}
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
	public byte[] fileData;

	public MainLogic(){
		P  = new PixelMapping();
		width = 0;
		height = 0;
	}

	/* Pembacaan File */

	public void readFiletoBinary(String infile){
		try {
			Path path = Paths.get(infile);
			fileData = Files.readAllBytes(path);
		} catch (Exception e){}
	}

	public void writeBinarytoFile(String outfile){
		try {
			Path path = Paths.get(outfile);
	    	Files.write(path, fileData); //creates, overwrites
	    } catch (Exception e){e.printStackTrace();}
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

	public String readStegoMessage(){ //sementara jumlah biner yang harus diambil itu dihitung
		System.out.println("Membaca pesan stego...");
		int local_count=0;
		boolean isDone = false;

		int min_width=0;
		int max_width=2;
		int min_height=0;
		int max_height=2;

		int max_value;
		if (width > height) max_value = height;
		else max_value = width;

		String readBinaryMsg = "";
		int byte_count = readStegoSize(max_value-1);

		System.out.println("Message Size: "+byte_count);

		

		while (!isDone){
			if (max_width > max_value){
				max_width = 2;
				min_width = 0;
				min_height+=3;
				max_height+=3;
			} else if (max_height > max_value) isDone = true;
			else{
				int pxSignature = P.getPixel(max_width,max_height,'r'); //ambil bit ke-8
				String temPx = String.format("%8s", Integer.toBinaryString(pxSignature)).replace(' ', '0');
				String pixelBit = temPx.substring(6,8);

				if (pixelBit.matches("00")){
					for (int h = min_height; h <= max_height; h++) {
						for (int w = min_width; w <= max_width; w++) {
							if (local_count >= byte_count) {
									isDone = true;
									break;
							}else {
								if (!((h == max_height) && (w == max_width))) {
									readBinaryMsg += P.readStegoMessage(w,h,2);
									local_count+=2;
									
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
									readBinaryMsg += P.readStegoMessage(w,h,3);
									local_count+=3;
									
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
									readBinaryMsg += P.readStegoMessage(w,h,4);
									local_count+=4;	
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
									readBinaryMsg += P.readStegoMessage(w,h,5);
									local_count+=5;
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

		System.out.println("Biner terbaca: "+readBinaryMsg);
		return readBinaryMsg;
	}

	public void writeStegoSize(int size, int max_value){
		//nulis di pixel terakhir
		String temPx = String.format("%24s", Integer.toBinaryString(size)).replace(' ', '0');
		System.out.println("Size binary: "+temPx);

		String red = temPx.substring(0,8);
		String green = temPx.substring(8,16);
		String blue = temPx.substring(16,24);

		System.out.println("Size binary: "+red+green+blue);
		P.overridePixel(max_value-1,max_value-1,Integer.parseInt(red, 2),Integer.parseInt(green, 2),Integer.parseInt(blue, 2));
	}

	/* WriteStegoMessage */
	public void writeStegoMessage(){
		//increment setiap 3 pixel X dan 3 pixel Y
		int min_width=0;
		int max_width=2;
		int min_height=0;
		int max_height=2;

		int max_value;
		if (width > height) max_value = height;
		else max_value = width;

		String msg = toBinary(fileData);
		int msg_length = msg.length();
		writeStegoSize(msg_length,max_value);
		System.out.println("Panjang Pesan: "+msg_length);
		System.out.println("Biner terbaca: "+toBinary(fileData));
		//System.out.println(bs.getBytes());

		int msg_offset = 0;

		boolean isStegoDone = false;

		while (!isStegoDone){
			
			if (max_width > max_value){
				max_width = 2;
				min_width = 0;
				min_height+=3;
				max_height+=3;
			} else if (max_height > max_value) isStegoDone = true;
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

				for (int h = min_height; h <= max_height; h++) {
					for (int w = min_width; w <= max_width; w++) {
						if ((h == max_height) && (w == max_width)) {
							if (d <= 7){	
								P.insertStegoMessage(w,h,"00");	//2-lsb	
							}
							else if ((d >= 8) && (d <= 15)) {
								P.insertStegoMessage(w,h,"01");	//3-lsb
							}
							else if ((d >= 16) && (d <= 31)) {
								P.insertStegoMessage(w,h,"10");	//4-lsb
							} else {
								P.insertStegoMessage(w,h,"11");	//5-lsb
							}
						} else {
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
							P.insertStegoMessage(w,h,msg.substring(msg_offset-movePointer,msg_offset));

							if (msg_offset >= msg_length) {
								isStegoDone = true;
								break;
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


	/*Image Processing */
	public void writeImage(String outfile){
		try {
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			WritableRaster raster = image.getRaster();
			System.out.println("Menulis Image");
			System.out.println("Height: "+height+" - Width: "+width);

			for (int i=0; i<P.pixels.size(); i++) {
				PixelPosition PPs = P.pixels.get(i);
				raster.setPixel(PPs.x,PPs.y,PPs.color);
			}

			ImageIO.write(image,"PNG",new File(outfile));
		} catch (Exception e){e.printStackTrace();}
	}

	public void readImage(String lokasifile){
		System.out.println("Sedang membaca image...");
		BufferedImage image;
	    
		try {
         File input = new File(lokasifile);
         image = ImageIO.read(input);
         width = image.getWidth();
         height = image.getHeight();
         
         for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
               Color c = new Color(image.getRGB(j, i));
               //System.out.println("Pos("+j+","+i+") ->   Red: " + c.getRed() +"  Green: " + c.getGreen() + " Blue: " + c.getBlue());
               P.insertPixel(j,i,c.getRed(),c.getGreen(),c.getBlue()); //disimpan
            }
         }
         System.out.println("Selesai membaca image...");
      } catch (Exception e) {e.printStackTrace();}
	}

	/* File processing */
	public void writeBinarytoFile(String outfile, String binstring){
		try {
			Path path = Paths.get(outfile);
	    	Files.write(path, fromBinary(binstring)); //creates, overwrites
	    } catch (Exception e){e.printStackTrace();}
	}

}

// bytes.toString()
//example.getBytes();
//new String(bytes)

public class MainApp {
	public static void main(String args[]){
		try {
			PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
			System.setOut(out);

			MainLogic ML = new MainLogic();

			Scanner input = new Scanner(System.in);
			System.out.print("File input (gambar): ");
			//String lokasifile = input.nextLine();
			String lokasifile = "cover1.png";
			ML.readImage(lokasifile);

			System.out.print("File stego: ");
			//String in = input.nextLine();
			String in = "test.txt";
			ML.readFiletoBinary(in);
			ML.writeStegoMessage();

			System.out.print("Nama file output: ");
			//String outfile = input.nextLine();
			String outfile = "cover2.png"; //output gambar stego

			ML.writeImage(outfile);

			String str = ML.readStegoMessage();
			ML.writeBinarytoFile("tulisan-out.txt",str);

		}catch (Exception e){e.printStackTrace();}
	}
}