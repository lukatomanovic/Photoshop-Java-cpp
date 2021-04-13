package components;


import operations.*;
import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import exceptions.OperationException;
import exceptions.ParamsNotMachException;
import exceptions.SelectionException;
import program.Program;

public class Image {
	//friend class XMLFormatter;
	//std::vector<Layer> layers_;
	//std::map<std::string, Selection> selections_;
	//std::map<std::pair<unsigned, unsigned>, unsigned> slected_pixels_;
	//std::map<std::string, Operation*> operations_;
	//std::map<std::string, Formatter*> formatters_;
	//Formatter *format_=nullptr;
	//unsigned num_of_layers_=0;
	//unsigned img_width_=0 ,img_height_=0;
	//bool saved_;
	//void deleteImage();

	//void activateSelection(std::string name);//ne koristi korisnik
	//void deactivateSelection(std::string name);//ne koristi korisnik
	
	private int img_width_=0,img_height_=0;
	private int num_of_layers_=0;
	private List<Layer> layers_= new ArrayList<>();
	private Map<String, Formatter> formatters_=new HashMap<>();
	private Map<String, Selection> selections_=new HashMap<>();
	private Map<String, Operation> operations_=new HashMap<>();
	
	private boolean saved_=true;
	private Program program_;
	


	public Image(Program program_) {
		this.program_=program_;
		formatters_.put("bmp", new BMPFormatter());
		formatters_.put("pam", new PAMFormatter());
		generateOperations();
	}
	private void generateOperations() {
		operations_.put("add", new BasicOperation("add", "adds an argument to each pixel component", 1));
		operations_.put("sub", new BasicOperation("sub", "subtracts the given value from the value of each pixel component", 1));
		operations_.put("subinv", new BasicOperation("subinv", "subtracts the value of each pixel component from the given value from", 1));
		operations_.put("div", new BasicOperation("div", "divides the value of each pixel component by given value", 1));
		operations_.put("divinv", new BasicOperation("divinv", "divides the given value by the value of the pixel component", 1));
		operations_.put("mul", new BasicOperation("mul", "multiplies the value of each pixel component by the given value", 1));
		operations_.put("fill", new BasicOperation("fill", "colors each pixel with the specified color in RGB format", 3));
		operations_.put("abs", new BasicOperation("abs", "each pixel component takes its absolute value", 0));
		operations_.put("log", new BasicOperation("log", "each pixel component takes the value it has when logarithmized", 0));
		operations_.put("max", new BasicOperation("max", "all pixel components less than the specified constant are set to a given constant", 1));
		operations_.put("min", new BasicOperation("min", "all pixel components greater than the specified constant are set to a given constant", 1));
		operations_.put("pow", new BasicOperation("pow", "power operation with the given exponent", 1));
		operations_.put("grayscale", new BasicOperation("grayscale", "the components of the resulting color have the same value calculated as the arithmetic mean of the components of the current color", 0));
		operations_.put("inversion", new BasicOperation("inversion", "the resulting color of each pixel is obtained by subtracting the current value from maximum", 0));
		operations_.put("median", new BasicOperation("median", "the resulting color of the each is obtained as the median color of the given and adjacent pixels", 0));
		operations_.put("blackwhite", new BasicOperation("blackwhite", "the color of the each pixels will be black (0, 0, 0) if the arithmetic mean of the R, G and B components is lower than 127, while otherwise it will be white (255, 255, 255).", 0));
		
	}
	
	public void createCompositeOperation(String name,ArrayList<String> selectedop) {
		ArrayList<Operation> operations=new ArrayList<Operation>();
		AtomicInteger ai=new AtomicInteger(0);
		selectedop.forEach(opname->{
			Operation operation=operations_.get(opname);
			operation=operation.clone();
			if(operation instanceof CompositeOperation) {
				ArrayList<Operation> listop=((CompositeOperation) operation).getOperationList();
				listop.forEach(op->{operations.add(op);});
			}
			else operations.add(operation.clone());
			int numoperands=ai.get()+operation.getNumber_of_arguments();
			ai.getAndSet(numoperands);
		});
		CompositeOperation co=new CompositeOperation(name,"composite operation",ai.get(), operations);
		operations_.put(name, co);
	}
	

	public boolean operationExists(String name) {
		Operation operation=operations_.get(name);
		if(operation==null)return false;
		return true;
	}
	public boolean isComposite(String name) {
		Operation operation=operations_.get(name);
		if(operation==null)return false;
		return (operation instanceof CompositeOperation);
	}
	public boolean SaveCompositeOperation(String name, String path) {
		XMLFormatter xmlf=new XMLFormatter();
		Operation operation=operations_.get(name);
		if(operation==null||!(operation instanceof CompositeOperation))return false;
		return xmlf.SaveFun(Paths.get(path),(CompositeOperation)operation);
	}
	
	public void LoadCompositeOperation(String name, String path) throws OperationException {
		XMLFormatter xmlf=new XMLFormatter();
		Operation operation=operations_.get(name);
		if(operation!=null)throw new OperationException("Operacija sa zadatim imenom vec postoji!");
		ArrayList<String> list_of_op= xmlf.LoadFun(Paths.get(path),name);
		if(list_of_op==null)throw new OperationException("Greska pri citanju operacije!");
		List<String> checkList= list_of_op.stream().filter(op->{
			if(operations_.get(op)!=null)return true;
			return false;
		}).collect(Collectors.toList());
		if(checkList.size()!=list_of_op.size())throw new OperationException("Kompozitna operacija sadrzi nedozvoljene operacije!");
		ArrayList<Operation> op_for_composite=new ArrayList<Operation>();
		AtomicInteger ai=new AtomicInteger(0);
		list_of_op.forEach(opname->{
			Operation o=operations_.get(opname);
			op_for_composite.add(o);
			ai.getAndSet(ai.get()+o.getNumber_of_arguments());
		});
		CompositeOperation co=new CompositeOperation(name,"composite", ai.get(),op_for_composite);
		operations_.put(name,co);
		DefaultListModel<String>dlm=program_.getDLMListOfPossibleOperatins();
		dlm.addElement(name);
	}

	public Layer compressIntoOneLayer() {
		if(num_of_layers_==0)return null;
		Layer compressed_=new Layer(img_width_, img_height_);
		boolean first = true;
		for (Layer l : layers_) {
			synchronized (l) {
				if (l.isVisible()) {
					if (first) {
						compressed_ = l.clone();
						List<Pixel> layer_pixel = compressed_.layer_;
						layer_pixel.forEach(p -> {
							p.setAlpha((short) Math.round(p.getAlpha() * ((double) l.getOpacity() / 100.0)));
						});
						first = false;
					} else {
						int j = 0;
						int r, k;
						for (Pixel p : l.layer_) {
							r = j / img_width_;
							k = j % img_width_;
							compressed_.getPixelAt(r, k)
									.setRed((short) (((float) l.getOpacity() / 100) * (((float) p.getAlpha()) / 255)* p.getRed()
											+ (1 - ((float) l.getOpacity() / 100) * ((float) p.getAlpha()) / 255)
													* compressed_.getPixelAt(r, k).getRed()));
							compressed_.getPixelAt(r, k)
									.setGreen((short) (((float) l.getOpacity() / 100) * (((float) p.getAlpha()) / 255)* p.getGreen()
											+ (1 - ((float) l.getOpacity() / 100) * ((float) p.getAlpha()) / 255)
													* compressed_.getPixelAt(r, k).getGreen()));
							compressed_.getPixelAt(r, k)
									.setBlue((short) (((float) l.getOpacity() / 100) * (((float) p.getAlpha()) / 255)* p.getBlue()
											+ (1 - ((float) l.getOpacity() / 100) * ((float) p.getAlpha()) / 255)
													* compressed_.getPixelAt(r, k).getBlue()));
							double a = ((float) l.getOpacity()) / 100 * ((float) p.getAlpha())
									+ (1 - ((float) l.getOpacity() / 100) * ((float) p.getAlpha()) / 255)
											* compressed_.getPixelAt(r, k).getAlpha();
							if (compressed_.getPixelAt(r, k).getAlpha() < 255) {
								if (a > 255)
									a = 255;
								else
									a = Math.round(a);
								compressed_.getPixelAt(r, k).setAlpha((short) a);
							}
							j++;
						}
					}
				}
			}
		}
		if(first==true)return null;//nema vidljivih slojeva
		return compressed_;

	}
	
	public Layer addLayer(String path) {
		String str=path.toLowerCase();
    	Pattern p=Pattern.compile("^.*\\.(.{3})$");
    	Matcher m=p.matcher(str);
    	String format=null;
    	if(m.matches()){
    	   format=m.group(1);
    	}
    	Formatter f=formatters_.get(format);
    	Layer layer=f.ReadImage(path);
    	addLayer(layer);
		return layer;
	}

	
	public void addLayer(Layer layer) {

		
		if (num_of_layers_ == 0) {
			img_width_ = layer.getWidth();
			img_height_ = layer.getHeight();
		}
		else{
			if (layer.getWidth() > img_width_) {
				//resize width for all other layers
				img_width_ = layer.getWidth();
				layers_.forEach(l->{
					l.resizeW(img_width_);
				});
				/*for (Layer l : layers_) {
					l.resizeW(img_width_);
				}*/
			}
			else if (layer.getWidth() < img_width_) {
				//resize width for added layer l
				layer.resizeW(img_width_);
			}
			if (layer.getHeight() > img_height_) {
				//resize height for all other layers
				img_height_ = layer.getHeight();
				layers_.forEach(l->{
					l.resizeH(img_height_);
				});
				/*for (Layer l : layers_) {
					l.resizeH(img_height_);
				}*/

			}
			else if (layer.getHeight() < img_height_) {
				//resize height for added layer l
				layer.resizeH(img_height_);
			}
		}
		layers_.add(layer);
		num_of_layers_++;
		saved_ = false;
		program_.setImageChanged(true);
		program_.RevaildateImage();
		showActiveSelections(program_.getImageShown());
		program_.showCurrentSelection();
		
	}

	public Layer addLayer(int w , int h ) throws ParamsNotMachException {
		if (w != -1 && h != -1&&layers_.size()==0) {
			img_width_ = w;
			img_width_ = h;
		}
		else if(layers_.size()==0){
			throw new ParamsNotMachException("Sloju moraju biti zadati sirina i visina!");
		}
		Layer l=null;
		if (w != -1 && h != -1) {
			l=new Layer(w, h);
			l.generateTransparentPixels();
		}
		else {
			l=new Layer(img_width_, img_height_);
		}
		this.addLayer(l);
		return l;
		//broj lejera se povecava u funkciji addLayer(l);isto i saved_
	}
	public Layer addEmptyLayer(int w , int h ) {
		Layer l=null;
		l=new Layer(w, h);
		l.generateTransparentPixels();
		this.addLayer(l);
		return l;
	}
	
	public Layer getLayerAtIndex(int index) {
		if(index<0||index>=layers_.size())return null;
		return layers_.get(index);
	}
	
	public void removeLayer(Layer l) {
		if(layers_.remove(l)) {
			num_of_layers_--;
			if(num_of_layers_==0) {
				img_width_=0;
				img_height_=0;
			}
			program_.setImageChanged(true);
			program_.RevaildateImage();
			showActiveSelections(program_.getImageShown());
			program_.showCurrentSelection();
		}
	}
	public void setLayerStatus(int index, boolean status) throws ParamsNotMachException
	{
		if (index>=0 &&index < layers_.size())layers_.get(index).setActive(status);
		else throw new ParamsNotMachException("Lejer na zadatom indeksu ne postoji!");
		//saved_ = false;
	}

	public void setLayerVisible(int index, boolean visible) throws ParamsNotMachException
	{
		if (index>=0&&index < layers_.size()) {
			layers_.get(index).setVisible(visible);
			program_.setImageChanged(true);
		}
		else throw new ParamsNotMachException("Lejer na zadatom indeksu ne postoji!");
		saved_ = false;
	}

	public void setLeyerOpacity(int index, short opacity)throws ParamsNotMachException
	{
		if (opacity<0 || opacity > 100)throw new ParamsNotMachException("Opacity mora biti u granicama 0-100!");
		if (index < layers_.size()) {
			layers_.get(index).setOpacity(opacity);
			program_.setImageChanged(true);
		}
		else throw new ParamsNotMachException("Lejer na zadatom indeksu ne postoji!");
		saved_ = false;
	}

	public void deleteLayer(int index) throws ParamsNotMachException {
		if (index>=0 && index < layers_.size())layers_.remove(index);
		else throw new ParamsNotMachException("Lejer na zadatom indeksu ne postoji!");
		saved_ = false;
		program_.setImageChanged(true);
	}
	
	public Selection addSelection(String name, ArrayList<Rectangle> rectangles) throws SelectionException
	{
		Selection found=selections_.get(name);
		if (found!=null) {
			throw new SelectionException("Selection with the name entered already exists!");
		}
		for (Rectangle r : rectangles) {
			if ((r.getStartX() + r.getWidth()) > img_width_ || r.getStartX() < 0 || r.getWidth() < 0
				|| (r.getStartY() + r.getHeight()) > img_height_ || r.getStartY() < 0 || r.getHeight() < 0)
				throw new SelectionException("Invalid selection coordinates!");
		}
		ArrayList<Rectangle> rectangles_copy=(ArrayList<Rectangle>) rectangles.clone();
		Selection created_selection=new Selection(name, rectangles_copy);
		selections_.put(name, created_selection);
		return created_selection;
	}

	public void deleteSelection(String name) throws SelectionException{
		Selection found=selections_.get(name);
		if (found==null) {
			throw new SelectionException("Selection with the name entered does not exists!");
		}
	
		selections_.remove(name);
		program_.setImageChanged(true);
	}
	public void deleteAllSelections() {
		selections_.clear();
		program_.setImageChanged(true);
	}
	
	public int numberOfSelections() {
		return selections_.size();
	}
	public int numberOfLayers() {
		return layers_.size();
	}
	
	public boolean isLayerActive(int index) {
		if (index>=0 &&index < layers_.size()) return layers_.get(index).isActive();
		return false;
	}
	public boolean isLayerVisible(int index) {
		if (index>=0 &&index < layers_.size()) return layers_.get(index).isVisible();
		return false;
	}

	public int getImgWidth() {
		return img_width_;
	}

	public int getImgHeight() {
		return img_height_;
	}
	
	public boolean exportImage(String path) {

		String str=path.toLowerCase();
    	Pattern p=Pattern.compile("^.*\\.(.{3})$");
    	Matcher m=p.matcher(str);
    	String format=null;
    	if(m.matches()){
    	   format=m.group(1);
    	}
    	else {
    		return false;
    	}
    	Formatter f=formatters_.get(format);
    	Layer to_be_saved=compressIntoOneLayer();
    	if(to_be_saved==null)return false;
    	f.SaveImage(to_be_saved, path);
		saved_ = true;
		return true;
	}
	
	public synchronized void showActiveSelections(BufferedImage shownImage) {
		if(program_.getImageShown()==null)return;
		JLabel jLabelImage=program_.getjLabelImage();
		JScrollPane jscpImagePane=program_.getJscpImagePane();
		if(selections_.isEmpty()) {
			jLabelImage.setIcon(new ImageIcon(shownImage));
	        jscpImagePane.revalidate();
		}
		int width=shownImage.getWidth();
		int height=shownImage.getHeight();
		BufferedImage out = new BufferedImage(width,height,shownImage.getType());
		Graphics2D g2d = out.createGraphics();
		g2d.drawImage(shownImage, 0, 0, width,height, null);
		g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
		selections_.values().stream().filter(s->{return s.isActive();}).forEach(selection->{
			selection.selection_.forEach(rectangle->{
				g2d.drawRect(rectangle.getStartX(), rectangle.getStartY(), rectangle.getWidth(), rectangle.getHeight());
			});
		});
		g2d.dispose();
		jLabelImage.setIcon(new ImageIcon(out));
        jscpImagePane.revalidate();
        program_.setImageShown(out);
	}
	
	public CompositeOperation generateCompositeForExecution(ArrayList<String> selectedop) {
		ArrayList<Operation> operations=new ArrayList<Operation>();
		AtomicInteger ai=new AtomicInteger(0);
		selectedop.forEach(opname->{
			Operation operation=operations_.get(opname);
			operation=operation.clone();
			if(operation instanceof CompositeOperation) {
				ArrayList<Operation> listop=((CompositeOperation) operation).getOperationList();
				listop.forEach(op->{operations.add(op);});
			}
			else operations.add(operation.clone());
			int numoperands=ai.get()+operation.getNumber_of_arguments();
			ai.getAndSet(numoperands);
		});
		CompositeOperation co=new CompositeOperation("operation for execution","composite operation",ai.get(), operations);
		return co;
	}
	
	private AtomicInteger number_of_live_executors=new AtomicInteger(0);
	private boolean operation_in_progres=false;
	public List<OperationsExecutor> executors=new ArrayList<OperationsExecutor>();
	public synchronized boolean isOperationInProgres() {
		return operation_in_progres;
	}
	public synchronized void setOperationInProgres(boolean operation_in_progres) {
		this.operation_in_progres = operation_in_progres;
	}
	public void executeOperation(CompositeOperation co, List<String> args) {
		// TODO Auto-generated method stub
		String funpath = FileSystems.getDefault().getPath("exec/fun/exec.fun").normalize().toAbsolutePath().toString();
		XMLFormatter xmlf=new XMLFormatter();
		xmlf.SaveFun(Paths.get(funpath), co);
		List<Selection> active_selection=selections_.values().stream().filter(sel->{return sel.isActive();}).collect(Collectors.toList());
		String arguments="";
		for(String s:args) {
			if(arguments.length()!=0)arguments+=",";
			arguments+=s;
		}
		String cmd_arguments=arguments;
		program_.disableSaving();
		layers_.stream().filter(l->{return l.isActive();}).forEach(layer->{
			executors.add(new OperationsExecutor(this, active_selection,layers_.indexOf(layer), funpath, cmd_arguments.toString()));
			number_of_live_executors.incrementAndGet();
		});
	}
	private Map<Integer, Layer> layer_edited=new HashMap<Integer, Layer>();
	private Integer endprog=0;
	public synchronized void executorDone(Layer l, int index) {
	//	layers_.get(index).setLayer(l.getLayer());
		layer_edited.put(index,l);
		if(number_of_live_executors.decrementAndGet()==0) {
			
				if(endprog==1)return;
				System.out.println("Prosao!");
				boolean error = executors.stream().anyMatch(exec -> {
					return exec.isErrorFlagTrue();
				});
				if (error) {
					executors.clear();
					layer_edited.clear();
					synchronized (endprog) {
					JOptionPane.showMessageDialog(program_, "Operation finished unsuccessfully!",
							"Operation finished", JOptionPane.ERROR_MESSAGE);
					}
					return;
					
				}
				for (Map.Entry<Integer, Layer> entry : layer_edited.entrySet()) {
					layers_.get(entry.getKey()).setLayer(entry.getValue().getLayer());
				}

				program_.setImageChanged(true);
				saved_ = false;
				program_.RevaildateImage();
				showActiveSelections(program_.getImageShown());
				program_.showCurrentSelection();
				setOperationInProgres(false);
				executors.clear();
				layer_edited.clear();
				synchronized (endprog) {
					JOptionPane.showMessageDialog(program_, "Operation finished successfully!", "Operation finished",
							JOptionPane.INFORMATION_MESSAGE);
				}
				program_.enableSaving();
				// prikazi obavestenje da je gotovo
		
			}
		}
	
	
	public List<Operation> getOperations(){
		return operations_.values().stream().collect(Collectors.toList());
	}
	public List<Layer> getLayers(){
		return layers_;
	}
	public List<Selection> getSelections(){
		return selections_.values().stream().collect(Collectors.toList());
	}
	
	public boolean SaveImage(Path path) {
		XMLFormatter xmlf=new XMLFormatter();
		if(xmlf.saveImage(this, path)) {
			saved_=true;
			return true;
		}
		return false;
	}
	public void setSaved(boolean saved_) {
		this.saved_ = saved_;
	}
	public boolean isSaved() {
		return saved_;
	}
	public void destroyExecutors() {
		synchronized (endprog) {
			endprog=1;
			executors.forEach(executor->{
				executor.prekiniNit();
			});
			executors.forEach(status->{
				try {
					status.join();
					System.out.println(status.getState());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		
	}
	
}
