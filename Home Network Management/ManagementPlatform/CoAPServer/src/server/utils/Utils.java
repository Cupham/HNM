package server.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Cu Pham
 *
 */
public class Utils {
	
	/*
	 * Check leap year
	 * 
	 * @param year YEAR
	 * 
	 * @return true if year is leap year, otherwise return false
	 */
	public static boolean isLeapYear(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
			return true;
		return false;
	}

	/**
	 * Check date valid
	 * 
	 * @param year
	 *            YEAR
	 * @param month
	 *            MONTH
	 * @param day
	 *            DAY
	 * @return true if date is valid, otherwise return false
	 */
	public static boolean isValidDate(int year, int month, int day) {
		int dayOfMonth[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if (year < 0)
			return false;
		if (isLeapYear(year))
			dayOfMonth[1] = 29;
		if (month < 1 || month > 12)
			return false;
		if (day < 1 || day > dayOfMonth[month - 1])
			return false;

		return true;
	}

	/**
	 * Write content to file
	 * 
	 * @param file
	 * @param content
	 */
	public static void writeFile(File file, String content) {
		try {
			// create connect
			FileOutputStream fos = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			DataOutputStream dos = new DataOutputStream(bos);
			// write content
			dos.writeBytes(content);
			// close connect
			dos.close();
			bos.close();
			fos.close();
		} catch (IOException ex) {
		}
	}

	/**
	 * Print time
	 * 
	 * @param milisec
	 *            time in mili seconds
	 * @return time in words
	 */
	public static String printTime(long milisec) {
		String message = "";
		if (milisec < 1000) {
			message = milisec + " ms(s).";
		} else if (milisec >= 1000 && milisec <= 60000) {
			message = milisec / 1000 + " second(s).";
		} else {
			message = milisec / 60000 + " minute(s) and " + (milisec % 60000) / 1000 + " second(s)";
		}
		return message;
	}

	/**
	 * Read content of file
	 * 
	 * @param filePath
	 *            file path
	 * @return content of file
	 */
	public static String readFile(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists())
				return null;
			// create input stream
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			
			byte[] data = new byte[(int) file.length()];
			dis.read(data);
			String content = new String(data, "UTF-8");
			
			// release input stream
			dis.close();
			bis.close();
			fis.close();
			
			return content;
		} catch(Exception ex){
			return null;
		}
	}
}
