package chrysostom.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class Inspector
{
	/**
	 * Определяет возможно ли создание директории.
	 *
	 * @param dir Проверяемая директория.
	 * @return {@code true}, если директорию можно создать, иначе {@code false}.
	 */
	private static boolean isDirectoryPossible(File dir) {
		return (dir != null) && (dir.exists() || isDirectoryPossible(dir.getParentFile()));
	}

	/**
	 * Определяет, корректно ли имя файла. Имя файла не должно содержать специальных сивмволов,
	 * включая: "/" и "\".
	 *
	 * @param file Проверяемый файл.
	 * @return {@code true}, если имя файла корректно, иначе {@code false}.
	 */
	private static boolean isNameCorrect(File file) {
		try {
			return file.getCanonicalPath().equals(file.getAbsolutePath());
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * Определяет пустая ли строка.
	 *
	 * @param string Проверяемая строка.
	 * @return {@code true}, если строка пустая или состоит только из пробелов, иначе {@code false}.
	 */
	private static boolean isEmpty(String string) {
		if (string.equals("")) return true;
		for (char c : string.toCharArray()) {
			if (c != ' ') return false;
		}
		return true;
	}

	/**
	 * Проверяет возможность сохранения файла.
	 *
	 * @param fileName Имя файла.
	 * @param dirName  Полное имя папки.
	 * @throws FileNameException           <ul>
	 *                                     <li>Имя файла не введено;</li>
	 *                                     <li>Имя фала состоит из пробелов;</li>
	 *                                     <li>Имя файла некорректно (содержит спецсимволы);</li>
	 *                                     <li>Имя папки не введено;</li>
	 *                                     <li>Имя папки состоит из пробелов;</li>
	 *                                     <li>Указанная папка или не может быть создана.</li>
	 *                                     </ul>
	 * @throws DirectoryNotExistsException Указанная папка не существует.
	 * @throws FileAlreadyExistsException  Указанный файл уже существует.
	 */
	public void check(String fileName, String dirName, String ext) throws FileNameException,
			DirectoryNotExistsException,
			FileAlreadyExistsException {
		File dir = new File(dirName);
		File file = new File(fileName);
		File fullFile = new File(dirName + File.separator + fileName + "." + ext);
		if (isEmpty(fileName)) {
			throw new FileNameException("Введите имя файла.");
		}
		if (!isNameCorrect(file) || !file.getName().equals(fileName)) {
			throw new FileNameException("Некорректное имя файла.");
		}
		if (isEmpty(dirName)) {
			throw new FileNameException("Введите имя папки.");
		}
		if (!dir.isAbsolute()) {
			throw new FileNameException("Путь к папке для сохранения должен быть абсолютным.");
		}
		if (!isDirectoryPossible(dir)) {
			String message = "Папка \"" + dir.getAbsolutePath() + "\"не существует и не может быть создана" + ".";
			throw new FileNameException(message);
		}
		if (!isNameCorrect(dir)) {
			throw new FileNameException("Некорректное имя папки.");
		}
		if (!dir.exists()) {
			String message = "Папка \"" + dir.getAbsolutePath() + "\" не существует.";
			throw new DirectoryNotExistsException(message);
		}
		if (fullFile.exists()) {
			String message = "Файл \"" + fullFile.getAbsolutePath() + "\" уже существует.";
			throw new FileAlreadyExistsException(message);
		}
	}
}
