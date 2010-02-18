package controlP5;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class ControlClassLoader extends ClassLoader {

	public ControlClassLoader() {
		super(ControlClassLoader.class.getClassLoader());
		System.out.println(getPackages().length);
		for(int i=0;i<getPackages().length;i++) {
			System.out.println(getPackages()[i]);
		}
	}

	public Class load(String className) throws ClassNotFoundException {
		return findClass(className);
	}

	public Class findClass(String className) {
		byte classByte[];
		Class result = null;
		result = (Class) classes.get(className);
		if (result != null) {
			return result;
		}

		try {
			return findSystemClass(className);
		}
		catch (Exception e) {
			System.out.println(e);
		}
		try {
			String classPath = ((String) ClassLoader.getSystemResource(
				className.replace('.', File.separatorChar) + ".class").getFile()).substring(1);
			classByte = loadClassData(classPath);
			result = defineClass(className, classByte, 0, classByte.length, null);
			classes.put(className, result);
			return result;
		}
		catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	private byte[] loadClassData(String className) throws IOException {

		File f;
		f = new File(className);
		int size = (int) f.length();
		byte buff[] = new byte[size];
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		dis.readFully(buff);
		dis.close();
		return buff;
	}

	private Hashtable classes = new Hashtable();
}
