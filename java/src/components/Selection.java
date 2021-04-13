package components;

import java.util.ArrayList;
import java.util.List;

public class Selection {
	private String name_;
	private boolean active_;
	List<Rectangle> selection_=new ArrayList<>();
	
	public Selection(String name_, List<Rectangle> selection_) {
		super();
		this.name_ = name_;
		this.selection_ = selection_;
	}

	public boolean isActive() {
		return active_;
	}

	public void setActive(boolean activate) {
		active_ = activate;
	}

	public void add(Rectangle r) {
		selection_.add(r);
	}
	public String getName() {
		return name_;
	}
	
	public List<Rectangle> getSelection(){
		return selection_;
	}
	

}
