package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataCacher {
	
	/**
	 * Saves the object into the file path that has been given 
	 * @param yourObject Object to be serialized 
	 * @param filePath File path to save object too
	 */
	public static void saveSerialized(Object yourObject, File file) {
		
		ObjectOutputStream outputStream = null;
		
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(file.getPath()));
			outputStream.writeObject(yourObject);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}		
	}
	
	/**
	 * Loads the object from file path that has been given 
	 * @param filePath File path to load object from
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static Object loadSerialized(File file) throws ClassNotFoundException, IOException {
		
		Object data1 = null;		
		FileInputStream fileIn = new FileInputStream(file.getPath());
		ObjectInputStream in = new ObjectInputStream(fileIn);
			
		data1 = in.readObject();
		in.close();
		
		return data1;
	}
	

}
