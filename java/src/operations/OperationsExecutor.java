package operations;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import components.BMPFormatter;
import components.Image;
import components.Layer;
import components.Selection;
import components.XMLFormatter;
import exceptions.ExecutionException;

public class OperationsExecutor extends Thread {
	private Image image_;
	private List<Selection> active_selections;
	private int layer_index;
	private String funpath_;
	private Layer layer_;
	private String args_;
	static private Runtime runtime = Runtime.getRuntime();
	private Process process_ = null;
	private boolean errorFlag=false;
	public boolean interruptFlag=false;

	public OperationsExecutor(Image image_, List<Selection> active_selections, int layer_index, String funpath_,
			String args_) {
		super();
		this.image_ = image_;
		this.active_selections = active_selections;
		this.layer_index = layer_index;
		this.funpath_ = funpath_;
		this.args_ = args_;
		layer_ = image_.getLayerAtIndex(layer_index);
		this.start();
	}

	@Override
	public void run() {
		image_.setOperationInProgres(true);
		BMPFormatter bmpf = new BMPFormatter();
		String layer_path = Paths.get("exec/layers/layer" + layer_index + ".bmp").normalize().toAbsolutePath()
				.toString();
		bmpf.SaveImage(layer_, layer_path);
		// JAVA -primer img/proba.bmp saved/photoshop/files/funs/nothing.fun 255
		String progpath = Paths.get("exec/Photoshop.exe").normalize().toAbsolutePath().toString();
		String cmd = progpath + " " + layer_path + " " + funpath_ + " " + args_;
		int ret;
		System.out.println("Program pokrenut sledecom komandom "+cmd);
		try {
			synchronized (runtime) {
				process_ = runtime.exec(cmd);
				ret = process_.waitFor();
			}
			if (ret < 0)
				throw new ExecutionException("Error in runtime!");
			System.out.println("Program vratio "+ret);
			Layer layer_new = bmpf.ReadImage(layer_path);
			if (!active_selections.isEmpty()) {
				AtomicInteger y = new AtomicInteger(0);
				AtomicInteger x = new AtomicInteger(0);
				for (; y.get() < layer_.getHeight(); y.getAndIncrement()) {
					for (x.set(0); x.get() < layer_.getWidth(); x.getAndIncrement()) {
						int lx = x.get();
						int ly = y.get();
						boolean pixel_match = active_selections.stream().anyMatch(selection -> {
							return selection.getSelection().stream().anyMatch(rectangle -> {

								if (lx >= rectangle.getStartX() && lx <= (rectangle.getStartX() + rectangle.getWidth())
										&& (ly >= rectangle.getStartY()
												&& ly <= (rectangle.getStartY() + rectangle.getHeight()))) {
									return true;
								}
								return false;
							});
						});
						if (pixel_match) {
							layer_.setPixelAt(ly, lx, layer_new.getPixelAt(ly, lx).clone());
						}

					}
				}
			}
			else {
				layer_.setLayer(layer_new.getLayer());				
			}
			image_.executorDone(layer_, layer_index);
		} catch (IOException | InterruptedException | ExecutionException e) {
			errorFlag=true;
			image_.executorDone(layer_, layer_index);
		}
		super.run();
	}
	
	
	
	public boolean nitRadi() {

		if(this.getState()!=Thread.State.TERMINATED
		&& this.getState()!=Thread.State.NEW)
			return true;
		return false;
	}
	public synchronized void prekiniNit() {
		if(nitRadi()) {
			this.interrupt();
		}

	}

	public boolean isErrorFlagTrue() {
		return errorFlag;
	}
	

}
