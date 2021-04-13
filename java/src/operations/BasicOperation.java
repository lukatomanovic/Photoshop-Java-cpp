package operations;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BasicOperation extends Operation implements Cloneable {

	public BasicOperation(String name_, String description_, int number_of_arguments_) {
		super(name_, description_, number_of_arguments_);
		// TODO Auto-generated constructor stub
	}
	
	public JPanel generateOperationJPanel() {
		JPanel panel_generated_=new JPanel(new BorderLayout());
		JPanel enter_text=new JPanel();
		JLabel op_name=new JLabel(name_);
		JLabel op_description=new JLabel("("+description_+") : ");
		enter_text.add(op_name);
		enter_text.add(op_description);
		panel_generated_.add(enter_text, BorderLayout.WEST);
		JLabel arg=null;
		JPanel enter_args=new JPanel();
		for(int i=0;i<number_of_arguments_;i++) {
			arg=new JLabel("arg. "+i);
			enter_args.add(new JTextField(4));
		}
		panel_generated_.add(enter_args, BorderLayout.EAST);
		return panel_generated_;
	}
	@Override
	public BasicOperation clone() {
		// TODO Auto-generated method stub
		return (BasicOperation)super.clone();
	}

}
