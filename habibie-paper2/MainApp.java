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
import java.nio.charset.*;

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
					System.out.println("P ("+x+","+y+") -> "+String.format("%8s", Integer.toBinaryString(PP.color[0])).replace(' ', '0')+" n: "+n_bit);	
				} else if (c == 'g'){
					int green = PP.color[1];
					PP.color[1] = modifyPixel(green,partialBinaryString); //green doang
					System.out.println("I ("+x+","+y+") -> "+String.format("%8s", Integer.toBinaryString(PP.color[1])).replace(' ', '0')+" n: "+n_bit);	
				}

				pixels.set(i,PP);
				
				break;	
			}
		}	
	}

	public String readStegoMessage(int x, int y, int n, char c) {
		for (int i=0; i<pixels.size(); i++){
			if ((pixels.get(i).x == x) && (pixels.get(i).y == y)) {
				String temPx;

				if (c == 'r')
					temPx = String.format("%8s", Integer.toBinaryString(pixels.get(i).color[0])).replace(' ', '0');
				else if (c == 'g')
					temPx = String.format("%8s", Integer.toBinaryString(pixels.get(i).color[1])).replace(' ', '0');
				else
					temPx = "";

				System.out.println("P ("+x+","+y+") : "+temPx+" N : "+n);
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
	
	//public byte[] fileData;
	
	public String str_file;

	public MainLogic(){
		P  = new PixelMapping();
		width = 0;
		height = 0;
	}

	/* Pembacaan File */


	public void readFiletoBinary(String infile, String key){
		str_file="";
		try {
			//ke str dlu

			FileInputStream in = null;
		    String str="";

		    try {
		        in = new FileInputStream(infile);
		         
		        int c;
		        while ((c = in.read()) != -1) {
		        	str = str + ((char)c);
		        }
		    }finally {
		        if (in != null) {
		            in.close();
		        }
		    }

		    str = VigenereExtended.Enkrip(key,str);
		 	//System.out.println("Setelah enkrip-asli: "+str);

		 	for (int i=0;i<str.length();i++){
				str_file = str_file + String.format("%8s", Integer.toBinaryString( CharToASCII (str.charAt(i)) )).replace(' ', '0');
			}

			
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
		try {
			if (type.equals("<ISI FILE>"))
				System.out.println("Membaca pesan stego (ISI FILE)...");
			else 
				System.out.println("Membaca pesan stego (JUDUL FILE)...");

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
			int byte_count;

			if (type.equals("<ISI FILE>"))
				byte_count = readStegoSize(max_value-1);
			else
				byte_count = readStegoSize(max_value-2);

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

					System.out.println("ganti blok: ("+max_width+","+max_height+") : "+pixelBit);

					if (pixelBit.matches("00")){
						for (int h = min_height; h <= max_height; h++) {
							for (int w = min_width; w <= max_width; w++) {
								if (local_count >= byte_count) {
										isDone = true;
										break;
								}else {
									if (!((h == max_height) && (w == max_width))) {
										if (type.equals("<ISI FILE>"))
											readBinaryMsg += P.readStegoMessage(w,h,2,'r');
										else
											readBinaryMsg += P.readStegoMessage(w,h,2,'g');

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
										if (type.equals("<ISI FILE>"))
											readBinaryMsg += P.readStegoMessage(w,h,3,'r');
										else
											readBinaryMsg += P.readStegoMessage(w,h,3,'g');

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
										if (type.equals("<ISI FILE>"))
											readBinaryMsg += P.readStegoMessage(w,h,4,'r');
										else
											readBinaryMsg += P.readStegoMessage(w,h,4,'g');

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
										if (type.equals("<ISI FILE>"))
											readBinaryMsg += P.readStegoMessage(w,h,5,'r');
										else
											readBinaryMsg += P.readStegoMessage(w,h,5,'g');

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
			readBinaryMsg = readBinaryMsg.substring(0,byte_count);
			System.out.println("Biner terbaca: "+readBinaryMsg);

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

		System.out.println("Panjang Pesan: "+msg_length);
		System.out.println("Biner terbaca: "+msg);
		//System.out.println(bs.getBytes());

		int msg_offset = 0;

		boolean isStegoDone = false;

		while (!isStegoDone){
			
			if (max_width > max_value){
				max_width = 2;
				min_width = 0;
				min_height+=3;
				max_height+=3;
			} else if (max_height > max_value-1) {
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

				System.out.println("ganti blok: ("+max_width+","+max_height+") : "+d);
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


	/*Image Processing */
	public void writeImage(String outfile, String tipe){
		try {
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			WritableRaster raster = image.getRaster();
			System.out.println("Menulis Image");
			System.out.println("Height: "+height+" - Width: "+width);

			for (int i=0; i<P.pixels.size(); i++) {
				PixelPosition PPs = P.pixels.get(i);
				raster.setPixel(PPs.x,PPs.y,PPs.color);
			}

			ImageIO.write(image,tipe,new File(outfile));
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

// bytes.toString()
//example.getBytes();
//new String(bytes)

public class MainApp {
	public static void main(String args[]){
		try {
			PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
			System.setOut(out);

			MainLogic ML = new MainLogic();
			String key = "Alifa - Habibie - Rivai";

			Scanner input = new Scanner(System.in);
			System.out.print("File input (gambar): ");
			//String lokasifile = input.nextLine();
			String lokasifile = "baboon_face.png";
			ML.readImage(lokasifile);

			System.out.print("File stego: ");
			//String in = input.nextLine();
			String in = "in.zip";
			

			ML.readFiletoBinary(in,key);

			ML.writeStegoMessage(in);
			ML.writeStegoMessage("<ISI FILE>");

			System.out.print("Nama file output: ");
			//String outfile = input.nextLine();
			String outfile = "baboon_face2.png"; //output gambar stego

			ML.writeImage(outfile,"png");

			String str = ML.readStegoMessage("<ISI FILE>");
			String str_filename = ML.readStegoMessage("-");

			System.out.println("Filename tersimpan: "+str_filename);
			ML.writeFile(str_filename+"-outfile",str,key);

			//ML.debugByteInteger();

		}catch (Exception e){e.printStackTrace();}
	}
}