package components;


public abstract class Formatter {

	public abstract void exportImage(Image image, String path);
	public abstract void importImage(Image image, String path);
	public abstract Layer ReadImage(String path);
	public abstract void SaveImage(Layer l, String path);
}
