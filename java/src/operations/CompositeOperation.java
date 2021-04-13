package operations;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class CompositeOperation extends Operation implements Cloneable {

	private ArrayList<Operation> operation_list=new ArrayList<>();
	public CompositeOperation(String name_, String description_, int number_of_arguments_, ArrayList<Operation> operation_list) {
		super(name_, description_, number_of_arguments_);
		this.operation_list=operation_list;
		// TODO Auto-generated constructor stub
	}

	@Override
	public JPanel generateOperationJPanel() {
		JPanel panel_generated_=new JPanel(new BorderLayout());
		JPanel composite_about=new JPanel();
		JLabel op_name=new JLabel(name_);
		JLabel op_description=new JLabel("("+description_+") : ");
		composite_about.add(op_name);
		composite_about.add(op_description);
		panel_generated_.add(composite_about,BorderLayout.NORTH);
		JPanel op_generated=new JPanel(new GridLayout(operation_list.size(),1,5,5));
		operation_list.forEach(operation->{
			op_generated.add(operation.generateOperationJPanel());
		});
		panel_generated_.add(op_generated,BorderLayout.CENTER);
		return panel_generated_;
	}
	@Override
	public CompositeOperation clone() {
		// TODO Auto-generated method stub
		CompositeOperation co= (CompositeOperation)super.clone();
		co.operation_list=(ArrayList<Operation>)operation_list.clone();
		return co;
	}

	public ArrayList<Operation> getOperationList() {
		return operation_list;
	}
	
	

}
