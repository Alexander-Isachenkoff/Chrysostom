package chrysostom.io;

import chrysostom.interfaces.Command;
import chrysostom.model.entities.Project;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ProjectLoader
{
    private final JFileChooser fileChooser = new JFileChooser(new File("").getAbsolutePath());
    private final Frame owner;
    private Project loadedProject;
    
    public ProjectLoader(Frame owner) {
        this.owner = owner;
        fileChooser.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f) {
                String[] formats = { ".chr", ".txt" };
                for (String format : formats) {
                    if (f.isDirectory() || f.getName().endsWith(format)) {
                        return true;
                    }
                }
                return false;
            }
            
            @Override
            public String getDescription() {
                return "Златоуст (.chr), Текстовые фалйы (.txt)";
            }
        });
    }
    
    private static Project readProject(File file) throws IOException, ClassNotFoundException {
        if (file.getName().endsWith(".chr")) {
            return (Project) IO.readProject(file);
        } else if (file.getName().endsWith(".txt")) {
            String text = IO.readText(file);
            return new Project(file.getName(), text);
        } else {
            throw new IOException("Неверный формат файла");
        }
    }
    
    public void load(Command onLoad, Command onFail) {
        loadedProject = null;
        fileChooser.showOpenDialog(owner);
        File file = fileChooser.getSelectedFile();
        if (file != null) {
            try {
                loadedProject = readProject(file);
                onLoad.execute();
            } catch (IOException | ClassNotFoundException e) {
                onFail.execute();
                e.printStackTrace();
            }
        }
    }
    
    public Project getLoadedProject() {
        return loadedProject;
    }
}
