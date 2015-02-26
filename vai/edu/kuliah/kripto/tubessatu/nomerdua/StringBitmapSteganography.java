package edu.kuliah.kripto.tubessatu.nomerdua;

public interface StringBitmapSteganography extends Steganography{
	public boolean encode(String imageFilepath, String outputFilepath, String message);
	public String decode(String imageFilepath);
}
