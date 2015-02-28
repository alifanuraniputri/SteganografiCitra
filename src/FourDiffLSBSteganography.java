
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class FourDiffLSBSteganography {

	int imageWidth, imageHeight;
	
	//algorithm parameters, default values
	int threshold = 5;
	int klow = 2;
	int khigh = 3;
	
	//Set parameters for edge-detection, predefined by users, also verify whether given parameter violated
	//any restrictions or not
	public boolean setParameter(int t, int klow, int khigh){
		if (!checkRestriction(t, klow, khigh)) return false;
		this.threshold = t;
		this.klow = klow;
		this.khigh = khigh;
		
		return true;
	}
	
	
	public byte[] embed(byte[] messageFilename, byte[] message, byte[] image, int imageType, int offset) {
		//variable to hold information about image block used in last iteration
		//lastBlock[0] = {r,g,b}, lastBlock[1] = row, lastBlock[2] = col
		int[] lastBlock = new int[]{0,0,0};
		
		byte[][] grey = new byte[][]{};
		byte[][] alpha = new byte[][]{};
		byte[][] blue = new byte[][]{};
		byte[][] green = new byte[][]{};
		byte[][] red = new byte[][]{};
		
		switch (imageType) {
		case BufferedImage.TYPE_4BYTE_ABGR:
			alpha = getARGBMatrix(image, 0);
			blue = getARGBMatrix(image, 1);
			green = getARGBMatrix(image, 2);
			red = getARGBMatrix(image, 3);
			break;
		case BufferedImage.TYPE_3BYTE_BGR:
			blue = getRGBMatrix(image, 0);
			green = getRGBMatrix(image, 1);
			red = getRGBMatrix(image, 2);
			break;
		default://anything else is assumed to be an 8-bit greyscale image
			grey = getGreyMatrix(image);
			break;
		}
		
		
		byte[] data = concat(concat(bitConversion(messageFilename.length), messageFilename), concat(bitConversion(message.length), message)); //message length need to be saved for extraction purpose
		
		//byte of data to be embedded
		int[] byteOffset = new int[]{0,7}; //begin from first byte and and leftmost bit
		
		byte[][] currentBlock = new byte[][]{};
		
		long capacity = 0;
		//iterate over all block to determine how many byte can be embedded with these settings
		while(lastBlock[2] != -1){
			//determine which block is the current block
			switch (imageType) {
			case BufferedImage.TYPE_4BYTE_ABGR:
				currentBlock = lastBlock[0] == 0 ? blue : lastBlock[0] == 1 ? green : lastBlock[0] == 2 ? red : alpha;
				break;
			case BufferedImage.TYPE_3BYTE_BGR:
				currentBlock = lastBlock[0] == 0 ? blue : lastBlock[0] == 1 ? green : red;
				break;
			default://anything else is assumed to be an 8-bit greyscale image
				currentBlock = grey;
				break;
			}
			
			byte[] y = new byte[4];
			y[0] = currentBlock[lastBlock[2]][lastBlock[1]];
			y[1] = currentBlock[lastBlock[2]+1][lastBlock[1]];
			y[2] = currentBlock[lastBlock[2]][lastBlock[1]+1];
			y[3] = currentBlock[lastBlock[2]+1][lastBlock[1]+1];
			
			//step 1
			double d = avgDiff(y);
			//step 2
			int k = checkLevel(d);
			//step 3
			if (verifyErrorBlock(y, d)){
				System.out.println("Current block is an error block, continue to next block");
			} else { //Process this block
				capacity += (k*4);
			}
			
			lastBlock = next(lastBlock, (int)Math.sqrt(4), imageWidth, imageHeight, imageType);
		}
		System.out.println("CAPACITY: " + capacity/8);
		
		if(8 + messageFilename.length + message.length + offset > capacity)
		{
			System.out.println(image.length + " " + offset);
			throw new IllegalArgumentException("File not long enough!");
		}
		
		lastBlock = new int[]{0,0,0};
		
		while (byteOffset[0] < data.length){
			//determine which block is the current block
			switch (imageType) {
			case BufferedImage.TYPE_4BYTE_ABGR:
				currentBlock = lastBlock[0] == 0 ? blue : lastBlock[0] == 1 ? green : lastBlock[0] == 2 ? red : alpha;
				break;
			case BufferedImage.TYPE_3BYTE_BGR:
				currentBlock = lastBlock[0] == 0 ? blue : lastBlock[0] == 1 ? green : red;
				break;
			default://anything else is assumed to be an 8-bit greyscale image
				currentBlock = grey;
				break;
			}
			
			byte[] y = new byte[4];
			y[0] = currentBlock[lastBlock[2]][lastBlock[1]];
			y[1] = currentBlock[lastBlock[2]+1][lastBlock[1]];
			y[2] = currentBlock[lastBlock[2]][lastBlock[1]+1];
			y[3] = currentBlock[lastBlock[2]+1][lastBlock[1]+1];
			
			//step 1
			double d = avgDiff(y);
			//step 2
			int k = checkLevel(d);
			//step 3
			if (verifyErrorBlock(y, d)){
				System.out.println("Current block is an error block, continue to next block");
			} else { //Process this block
				//step 4
				byte[] y1 = standardLSB(y, data, k, byteOffset);
				//step 5
				byte[] y2 = modifiedLSB(y, y1, k);
				//step 6
				byte[] yFinal = readjust(y, y2, k, d);

				//y doesnt hold a reference to original rgb array, manually copy final value to the original arrays
				currentBlock[lastBlock[2]][lastBlock[1]] = yFinal[0];
				currentBlock[lastBlock[2]+1][lastBlock[1]] = yFinal[1];
				currentBlock[lastBlock[2]][lastBlock[1]+1] = yFinal[2];
				currentBlock[lastBlock[2]+1][lastBlock[1]+1] = yFinal[3];
				
			}
			
			//TODO
			if (byteOffset[0] < data.length) lastBlock = next(lastBlock, (int)Math.sqrt(4), imageWidth, imageHeight, imageType);
		}
		
		//construct image from modified components
		byte[] image2 = new byte[]{};
		switch (imageType) {
		case BufferedImage.TYPE_4BYTE_ABGR:
			image2 = constructByteImageFromARGB(image.length, alpha, blue, green, red);
			break;
		case BufferedImage.TYPE_3BYTE_BGR:
			image2 = constructByteImageFromRGB(image.length, blue, green, red);
			break;
		default://anything else is assumed to be an 8-bit greyscale image
			image2 = constructByteImageFromGreyscale(image.length, grey);
			break;
		}
		
		return image2;
	}

	public byte[][] extract(byte[] image, int imageType) {
		int length = 0;
		
		int[] lastBlock = new int[]{0,0,0};
		
		byte[][] grey = new byte[][]{};
		byte[][] alpha = new byte[][]{};
		byte[][] blue = new byte[][]{};
		byte[][] green = new byte[][]{};
		byte[][] red = new byte[][]{};
		
		switch (imageType) {
		case BufferedImage.TYPE_4BYTE_ABGR:
			alpha = getARGBMatrix(image, 0);
			blue = getARGBMatrix(image, 1);
			green = getARGBMatrix(image, 2);
			red = getARGBMatrix(image, 3);
			break;
		case BufferedImage.TYPE_3BYTE_BGR:
			blue = getRGBMatrix(image, 0);
			green = getRGBMatrix(image, 1);
			red = getRGBMatrix(image, 2);
			break;
		default://anything else is assumed to be an 8-bit greyscale image
			grey = getGreyMatrix(image);
			break;
		}		
		
		int[] byteOffset = new int[]{0,0,0,0};
		
		byte[] yExtract = FourDiffLSB(4, byteOffset, lastBlock, grey, alpha, blue, green, red, imageType);	
		length = reverseBitConversion(yExtract);
		System.out.println("FILENAME LENGTH: " + length);
		
		//preparation to extract message
		byteOffset[0] = 0;
		byte[] fileNameresult = FourDiffLSB(length, byteOffset, lastBlock, grey, alpha, blue, green, red, imageType);//FourDiffLSB(length, byteOffset, lastBlock, grey);//
		System.out.println("FILENAME: " + new String(fileNameresult));
		
		byteOffset[0] = 0;
		byte[] messageLengthResult = FourDiffLSB(4, byteOffset, lastBlock, grey, alpha, blue, green, red, imageType);//FourDiffLSB(4, byteOffset, lastBlock, grey);//
		length = reverseBitConversion(messageLengthResult);
		System.out.println("MESSAGE LENGTH: " + length);
		
		//preparation to extract message
		byteOffset[0] = 0;
		byte[] result = FourDiffLSB(length, byteOffset, lastBlock, grey, alpha, blue, green, red, imageType);//FourDiffLSB(length, byteOffset, lastBlock, grey);//
		System.out.println("MESSAGE: " + new String(result));
		
		byte[][] ret = new byte[2][];
		ret[0] = result;
		ret[1] = fileNameresult;
		
		return ret;
		//return result;
	}
	
	private byte[] FourDiffLSB(int numByteToExtract, int[] byteOffset, int[] lastBlock, byte[][] grey, byte[][] alpha, byte[][] blue, byte[][] green, byte[][] red, int imageType){
		byte[] result = new byte[numByteToExtract];
		
		byte[][] currentBlock = new byte[][]{};
		while (byteOffset[0] < numByteToExtract){
			switch (imageType) {
			case BufferedImage.TYPE_4BYTE_ABGR:
				currentBlock = lastBlock[0] == 0 ? blue : lastBlock[0] == 1 ? green : lastBlock[0] == 2 ? red : alpha;
				break;
			case BufferedImage.TYPE_3BYTE_BGR:
				currentBlock = lastBlock[0] == 0 ? blue : lastBlock[0] == 1 ? green : red;
				break;
			default://anything else is assumed to be an 8-bit greyscale image
				currentBlock = grey;
				break;
			}
			
			byte[] y = new byte[4];
			y[0] = currentBlock[lastBlock[2]][lastBlock[1]];
			y[1] = currentBlock[lastBlock[2]+1][lastBlock[1]];
			y[2] = currentBlock[lastBlock[2]][lastBlock[1]+1];
			y[3] = currentBlock[lastBlock[2]+1][lastBlock[1]+1];
			
			//step 1
			double d = avgDiff(y);
			//step 2
			int k = checkLevel(d);
			//step 3
			if (verifyErrorBlock(y, d)){
				System.out.println("Current block is an error block, continue to next block");
			} else { //Process this block
				//buffer extracted bits from image
				result = extract(y, k, result, numByteToExtract, byteOffset);
			}
			
			if (byteOffset[0] < numByteToExtract) lastBlock = next(lastBlock, (int)Math.sqrt(4), imageWidth, imageHeight, imageType);
		}
		
		return result;
	}
	
	
	public BufferedImage encode(String imageFilepath, String messageFilename, byte[] message) {
		String			file_name 	= imageFilepath;
		
		BufferedImage image = getImage(file_name);
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();
	
		System.out.println("message " + message);
		System.out.println("message length " + message.length);
		System.out.println("message filename " + messageFilename);
		image = addText(image,messageFilename, message);
		
		return image;
	}
	
	public boolean encodeAndSave(String imageFilepath, String outputFilepath, String messageFilename, byte[] message) {
		String			file_name 	= imageFilepath;
		
		BufferedImage image = getImage(file_name);
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();
	
		System.out.println("message " + message);
		System.out.println("message length " + message.length);
		image = addText(image,messageFilename, message);
		
		return(setImage(image,new File(outputFilepath),"png"));
	}

	public byte[][] decode(String imageFilepath) {
		byte[][] decode;
		try
		{
			BufferedImage image  = getImage(imageFilepath);
			imageWidth = image.getWidth();
			imageHeight = image.getHeight();
			
			decode = extract(getByteData(image), image.getType());
			//decode[0] = message, decode[1] = fileName
			return(new byte[][]{decode[0], decode[1]});
		}
        catch(Exception e)
        {
        	e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
				"There is no hidden message in this image!","Error",
				JOptionPane.ERROR_MESSAGE);
			return new byte[][]{};
        }
	}
	
	
	//Image and bit manipulation methods
	private BufferedImage getImage(String f)
	{
		BufferedImage 	image	= null;
		File 		file 	= new File(f);
		
		try
		{
			image = ImageIO.read(file);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, 
				"Image could not be read!","Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	private boolean setImage(BufferedImage image, File file, String ext)
	{
		try
		{
			file.delete();
			ImageIO.write(image,ext,file);
			return true;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
				"File could not be saved!","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	
	private BufferedImage addText(BufferedImage image, String messageFilename, byte[] text)
	{
		byte img[]  = getByteData(image);
		byte msg[] = text;//text.getBytes();
		byte msgFilename[] = messageFilename.getBytes();
		
		try
		{
			byte[] temp =  embed(msgFilename, msg, img, image.getType(), 0);
			//temps doesnt hold reference to image, copy them manually to img
			for(int i = 0; i < img.length; i++){
				img[i] = temp[i];
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
"Target File cannot hold message!", "Error",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}

	private byte[] getByteData(BufferedImage image)
	{
		WritableRaster raster   = image.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	
	//convert an integer to its binary representation
	private byte[] bitConversion(int i)
	{
		byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
		byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
		byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
		byte byte0 = (byte)((i & 0x000000FF)       );
		return(new byte[]{byte3,byte2,byte1,byte0});
	}
	

	//reverse of above method
	private int reverseBitConversion(byte[] i){
		int result = 0;
		result = (result | (i[0] & 0xFF)) << 8;
		result = (result | (i[1] & 0xFF)) << 8;
		result = (result | (i[2] & 0xFF)) << 8;
		result = result | (i[3] & 0xFF);
		return result;
	}
	
	//concat a and b
	private byte[] concat(byte[] a, byte[] b) {
	   int aLen = a.length;
	   int bLen = b.length;
	   byte[] c= new byte[aLen+bLen];
	   System.arraycopy(a, 0, c, 0, aLen);
	   System.arraycopy(b, 0, c, aLen, bLen);
	   return c;
	}
	
	
	
	
	//Four-Difference LSB Algorithm Methods
	private boolean checkRestriction(int t, int klow, int khigh){
		return Math.pow(2, klow) <= t && t <= Math.pow(2, khigh) && 1 <= klow && khigh <= 5;
	}
	
	private int min(byte[] a){
		int min = a[0];
		for (int i = 0; i < a.length; i++){
			if (a[i] < min) min = a[i];
		}
		return min;
	}
	
	private int max(byte[] a){
		int max = a[0];
		for (int i = 0; i < a.length; i++){
			if (a[i] > max) max = a[i];
		}
		return max;
	}
	
	private double avgDiff(byte[] y){
		int ymin = (int) min(y);
		int sum = 0;
		for(int i = 0; i < y.length; i++){
			sum += (int) (y[i] - ymin);
		}
		return (double)sum/(double)(y.length-1);
	}
	
	private int checkLevel(double d){
		return d <= threshold ? klow : khigh;
	}
	
	private boolean verifyErrorBlock(byte[] y, double d){
		int ymin = (int) min(y);
		int ymax = (int) max(y);
		return d <= threshold && (ymax - ymin) > (2*threshold+2);
	}
	
	//embed bitNum bits of message into the image
	//e.g image byte 11100001, 3 bits of message, 011, will be embedded as 11100110
	//input/output offset
	//return index of next bit in data to be embedded, offset[0] = offset of datum, offset[1] = offset of bit
	private byte[] standardLSB(byte[] original, byte[] data, int bitNum, int[] offset){
			byte[] y = original.clone();
			boolean firstPass = false; //check for the first byte pass only
		
			int c = 0;
			int yCounter = 0;
			for(int i = offset[0]; i < data.length; i++){
				byte currentByte = data[i]; 
				
				for (int j = firstPass ? 7 : offset[1]; j >= 0; j--){
					if (c >= y.length){
						offset[0] = i;
						offset[1] = j;
						return y;
					}
					
					int b = (currentByte >>> j) & 1;
					b = b << yCounter;
					
					int mask = 0xFE << yCounter;
					if (yCounter >= 1) mask = mask | (int) Math.pow(2, yCounter)-1;
					
					y[c] = (byte) ((y[c] & mask) | b );
					
					
					yCounter++;
					if (yCounter >= bitNum){
						c++;
						yCounter = 0;
					}
					
					
				}
				if (!firstPass) firstPass = true;
				
			}
			offset[0] = data.length;
			offset[1] = 7;
			return y;
		}
		
		//check whether by substracting or adding in position bitnum+1 LSB could make the block
		//more similar in value with the original block
		private byte[] modifiedLSB(byte[] y, byte[] original, int bitnum){
			byte[] y2 = original.clone();
			for (int i = 0; i < y2.length;i++){
				int modifier = (int) Math.pow(2, bitnum);
				int inc = y2[i] + modifier;
				int dec = y2[i] - modifier;
				
				byte[] diff = new byte[]{(byte) Math.abs(y[i]-y2[i]), (byte) Math.abs(y[i]-inc), (byte) Math.abs(y[i]-dec)};
				
				if (diff[0] < diff[1] && diff[0] < diff[2]){
					
				} else if (diff[1] < diff[2]){
					y2[i] = (byte) inc;
				} else {
					y2[i] = (byte) dec;
				}
			}
			return y2;
		}
		
		//find mean square (error) between proximated block and original block
		private int meanSquare(byte[] y, byte[] yProx){
			int sum = 0;
			for(int i = 0; i < y.length; i++){
				sum += (yProx[i] - y[i])*(yProx[i] - y[i]);
			}
			return sum;
		}
		
		//readjust byte value, based on the paper
		private byte[] readjust(byte[] original, byte[] y2, int k, double d){
			byte[][] yProx = new byte[y2.length][3];
			int modifier = 0;
			for (int i = 0; i < y2.length; i++){
				modifier = (int) Math.pow(2, k);
				yProx[i] = new byte[]{y2[i],(byte) (y2[i] + modifier), (byte) (y2[i] - modifier)}; 
			}
			
			int index1 = -1,index2 = -1,index3 = -1,index4 = -1;
			int min = Integer.MAX_VALUE;
			
			for (int w = 0; w < 3; w++){
				for (int x = 0; x < 3; x++){
					for (int y = 0; y < 3; y++){
						for (int z = 0; z < 3; z++){
								byte[] yTemp = new byte[]{yProx[0][w],yProx[1][x],yProx[2][y],yProx[3][z]};
								double dTemp = avgDiff(yTemp);
								
								int kTemp = checkLevel(dTemp);
								if (k == kTemp && !verifyErrorBlock(yTemp, dTemp)){
									int minTemp = meanSquare(original, yTemp);
									if (minTemp < min){
										min = minTemp;
										index1 = w;
										index2 = x;
										index3 = y;
										index4 = z;
									}
								}
						}
					}
				}
			}
			
			byte[] yFinal = new byte[]{yProx[0][index1],yProx[1][index2],yProx[2][index3],yProx[3][index4]};	
			return yFinal;
		}
		
		
		//extract all bits from a block, or until number of bits needed is reached
		private byte[] extract(byte[] y, int k, byte[] lastResult, int numByteToExtract, int[] byteOffset){
			byte[] result = lastResult;
			int offset = byteOffset[2];
			int counter = byteOffset[3];
			int bitExracted = byteOffset[1];
			
			boolean firstPass = false;
			
			int b;
			for(b=byteOffset[0]; b<numByteToExtract; ++b )
			{
				for (int i = firstPass ? 0 : bitExracted%8; i < 8; i++){
					if (offset >= y.length){
						byteOffset[0] = b;
						byteOffset[1] = bitExracted;
						byteOffset[2] = 0;
						byteOffset[3] = 0;
						return result;
					}
					
					result[b] = (byte) ((result[b] << 1) | ((y[offset] >> counter) & 1));
					
					counter++;
					if (counter >= k){
						counter = 0;
						offset++;
					}
					
					bitExracted++;
					if (bitExracted >= numByteToExtract*8){
						byteOffset[0] = numByteToExtract;
						byteOffset[1] = 0;
						byteOffset[2] = offset;
						byteOffset[3] = counter;
						return result;
					}
				}
				if (!firstPass){
					firstPass = true;
				}
			}
			byteOffset[0] = numByteToExtract;
			byteOffset[1] = 0;
			byteOffset[2] = offset;
			byteOffset[3] = counter;
			return result;
		}
		
		//determine next block
		//imageType maxima greyscale = 1, rgb = 3, argb = 4
		private int[] next(int[] last,int step, int width, int height, int imageType){
			int maxima;
			switch (imageType) {
			case BufferedImage.TYPE_4BYTE_ABGR:
				maxima = 4;
				break;
			case BufferedImage.TYPE_3BYTE_BGR:
				maxima = 3;
				break;
			default://anything else is assumed to be an 8-bit greyscale image
				maxima = 1;
			}
			
			last[0]++;//blue, green, red order, plus alpha of ARGB
			if (last[0] >= maxima){
				last[0] = 0;
				
				if (last[1]+step < width){
					last[1] = last[1]+step;
					if (last[1] + step-1 >= width){
						last[1] = 0;
						last[2] += step;
					}
				} else {
					last[1] = 0;
					last[2] += step;
				}
				
				if (last[2]+step-1 >= height) last[2] = -1;
			}
			return last;
		}
	
		//get rgb component from an image
		//rgb = {0,1,2}, 0 = blue, 1 = green, 2 = red
		private byte[][] getRGBMatrix(byte[] imagePixels, int rgb){
			byte[][] result = new byte[imageHeight][imageWidth];
			int pixelLength = 3;//3 pixel components
	        
			for (int pixel = 0, row = 0, col = 0; pixel < imagePixels.length; pixel += pixelLength) {
	           byte rgbByte = (byte) (imagePixels[pixel + rgb] & 0x000000FF); 
	           result[row][col] = rgbByte;
	           col++;
	           if (col == imageWidth) {
	              col = 0;
	              row++;
	           }
	        }
	        return result;
		}
		
		private byte[][] getARGBMatrix(byte[] imagePixels, int argb){
			byte[][] result = new byte[imageHeight][imageWidth];
			int pixelLength = 4;//4 pixel components
	        
			for (int pixel = 0, row = 0, col = 0; pixel < imagePixels.length; pixel += pixelLength) {
	           byte rgbByte = (byte) (imagePixels[pixel + argb] & 0x000000FF); 
	           result[row][col] = rgbByte;
	           col++;
	           if (col == imageWidth) {
	              col = 0;
	              row++;
	           }
	        }
	        return result;
		}
		
		private byte[][] getGreyMatrix(byte[] imagePixels){
			byte[][] result = new byte[imageHeight][imageWidth];
	        
			for (int pixel = 0, row = 0, col = 0; pixel < imagePixels.length; pixel++) {
	           byte rgbByte = (byte) (imagePixels[pixel] & 0x000000FF); 
	           result[row][col] = rgbByte;
	           col++;
	           if (col == imageWidth) {
	              col = 0;
	              row++;
	           }
	        }
	        return result;
		}
	
		//construct image byte array from rgn component extracted from above method
		private byte[] constructByteImageFromRGB(int imageLength, byte[][] b, byte[][] g, byte[][] r){
			byte[] result = new byte[imageLength];
			int pixelLength = 3;//3 pixel components
			
			for(int pixel = 0, row = 0, col = 0; pixel < imageLength ; pixel += pixelLength){
				result[pixel] = b[row][col];
				result[pixel + 1] = g[row][col];
				result[pixel + 2] = r[row][col];
				col++;
				if (col == imageWidth){
					col = 0;
					row++;
				}
			}
			return result;
		}
		
		private byte[] constructByteImageFromARGB(int imageLength, byte[][] a, byte[][] b, byte[][] g, byte[][] r){
			byte[] result = new byte[imageLength];
			int pixelLength = 4;//4 pixel components
			
			for(int pixel = 0, row = 0, col = 0; pixel < imageLength ; pixel += pixelLength){
				result[pixel] = a[row][col];
				result[pixel + 1] = b[row][col];
				result[pixel + 2] = g[row][col];
				result[pixel + 3] = r[row][col];
				col++;
				if (col == imageWidth){
					col = 0;
					row++;
				}
			}
			return result;
		}
		
		private byte[] constructByteImageFromGreyscale(int imageLength, byte[][] g){
			byte[] result = new byte[imageLength];
			
			for(int pixel = 0, row = 0, col = 0; pixel < imageLength ; pixel++){
				result[pixel] = g[row][col];
				col++;
				if (col == imageWidth){
					col = 0;
					row++;
				}
			}
			return result;
		}
		
		public static void print2DBinary(byte[][] m){
			for(int i = 0; i < m.length; i++){
				for (int j = 0; j < m[i].length; j++){
					System.out.print(String.format("%8s",Integer.toBinaryString((int) m[i][j])).replace(' ', '0') + ",");
					//System.out.print((int) m[i][j] + ",");
				}
				System.out.println();
			}
		}
		
		public void printBinary(byte[] m){
			for(int i = 0; i < m.length; i++){
				//System.out.println((int) m[i]);
				System.out.println(String.format("%8s",Integer.toBinaryString((int) m[i])).replace(' ', '0'));
			}
		}
}
