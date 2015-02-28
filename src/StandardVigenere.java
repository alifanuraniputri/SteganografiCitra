public class StandardVigenere {

	public static int ENCRYPT = 1;
	public static int DECRYPT = -1;
	
	public byte[] doCrypt(int mode, byte[] msg, byte[] key){
		if (mode != ENCRYPT && mode != DECRYPT) return null;
		byte[] res = new byte[msg.length];
		for(int i = 0; i < msg.length; i++){
			res[i] = (byte) ((msg[i] + (mode*key[i%key.length])) % 256);
		}
		return res;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StandardVigenere cipher = new StandardVigenere();
		byte[] pesan = "tes satu dua @# wrf".getBytes();
		System.out.println(new String(pesan));
		byte[] key = "ini key".getBytes();
		byte[] en = cipher.doCrypt(ENCRYPT, pesan, key);
		System.out.println(new String(en));
		byte[] de = cipher.doCrypt(DECRYPT, en, key);
		System.out.println(new String(de));
	}

}
