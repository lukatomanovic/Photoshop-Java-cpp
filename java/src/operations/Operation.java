package operations;

import javax.swing.JPanel;

abstract public class Operation implements Cloneable{
	
	protected String name_;
	protected String description_;
	protected int number_of_arguments_;
	
	
	public String getName() {
		return name_;
	}
	public void setName(String name_) {
		this.name_ = name_;
	}
	public String getDescription() {
		return description_;
	}
	public void setDescription(String description_) {
		this.description_ = description_;
	}
	public int getNumber_of_arguments() {
		return number_of_arguments_;
	}
	public void setNumber_of_arguments(int number_of_arguments_) {
		this.number_of_arguments_ = number_of_arguments_;
	}
	public Operation(String name_, String description_, int number_of_arguments_) {
		super();
		this.name_ = name_;
		this.description_ = description_;
		this.number_of_arguments_ = number_of_arguments_;
	}
	
	abstract public JPanel generateOperationJPanel();
	
	@Override
	public Operation clone() {
		// TODO Auto-generated method stub
		try {
			return (Operation)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
