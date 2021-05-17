package chrysostom.view.chart;

import java.io.File;

public class SaveParameters
{
    private final int width;
    private final int height;
    private final String format;
    private final boolean transparency;
    private final File file;
    
    public SaveParameters(int width, int height, String format, boolean transparency, File file) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.transparency = transparency;
        this.file = file;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public String getFormat() {
        return format;
    }
    
    public boolean getTransparency() {
        return transparency;
    }
    
    public File getFile() {
        return file;
    }
}
