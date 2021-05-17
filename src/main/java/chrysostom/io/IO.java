package chrysostom.io;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.List;

public final class IO
{
	private IO() {}

	public static void writeProject(Object object, File file) throws IOException {
		FileOutputStream stream = new FileOutputStream(file);
		ObjectOutput objectWriter = new ObjectOutputStream(stream);
		objectWriter.writeObject(object);
	}

	public static Object readProject(File file) throws IOException, ClassNotFoundException {
		FileInputStream stream = new FileInputStream(file);
		ObjectInput objectReader = new ObjectInputStream(stream);
		return objectReader.readObject();
	}
	
	public static String readText(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		return String.join(System.lineSeparator(), lines);
	}

	/**
	 * Возвращает икноку типа {@code ImageIcon} из файла с изображением внутри jar-архива.
	 * Если иконку невозможно прочитать, то возвращает пустую иконку.
	 *
	 * @param path Путь к файлу с изображением.
	 * @return Иконку типа {@code ImageIcon} или, если чтение невозможно, пустую иконку.
	 */
	public static ImageIcon readIcon(String path) {
		try {
			return new ImageIcon(ImageIO.read(Object.class.getResource(path)));
		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
			return new ImageIcon();
		}
	}
}
