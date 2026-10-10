package dev.moduleskit.modules.media.api.request;

public class ImageConversion {

    private final String name;
    private final int width;
    private final int height;
    private final String format;

    public ImageConversion(String name, int width, int height) {
        this(name, width, height, null);
    }

    public ImageConversion(String name, int width, int height, String format) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.format = format;
    }

    public String getName() {
        return name;
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
}
