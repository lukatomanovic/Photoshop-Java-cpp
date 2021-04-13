package program;

import java.awt.BorderLayout;
import java.awt.Button;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;


import components.*;
import exceptions.OperationException;
import exceptions.SelectionException;
import operations.BasicOperation;
import operations.CompositeOperation;
import operations.Operation;



public class Program extends JFrame {
	
	private Image img;
	private boolean img_changed_=false;
	private BufferedImage img_shown_;
	private JMenuBar main_menu;
	private Panel cp;
	private JPanel layer_panel;
	private JPanel layer_panel_outside;
	private JPanel selection_panel;
	private JPanel selection_panel_outside;
	private JPanel selection_panel_wrapper;
	private JLabel jLabelImage;
	private JScrollPane jscpImagePane;
	private static int next_layer_id=0;
	private static int next_selection_id=0;
	private static final int rows=2;
	
	private ArrayList<Rectangle> curr_selection_=new ArrayList<>();
	private boolean mousePressed_=true;
	private int rectangle_x_start_;
	private int rectangle_y_start_;
	private int rectangle_x_end_;
	private int rectangle_y_end_;
	
	JTextField jtf_selection_name;
	JButton btnapply;
	JButton btndiscard;
	
	
	DefaultListModel<String> dlm;
	DefaultListModel<String> dlmexec;
	JList<String> allposibleoperations;
	JList<String> operations_exec_list;
	
	JMenu file_menu;
	
	public DefaultListModel<String> getDLMListOfPossibleOperatins() {
			return dlm;
	}
	
	
	public JLabel getjLabelImage() {
		return jLabelImage;
	}

	public JScrollPane getJscpImagePane() {
		return jscpImagePane;
	}

	public boolean isImageChanged() {
		return img_changed_;
	}

	public void setImageChanged(boolean img_changed_) {
		this.img_changed_ = img_changed_;
	}
	
	
	public BufferedImage getImageShown() {
		return img_shown_;
	}
	public void setImageShown(BufferedImage img_shown_) {
		this.img_shown_ = img_shown_;
	}
	
	

	public BufferedImage getImgShown() {
		return img_shown_;
	}


	Program() {
		img = new Image(this);
		configureGUI();
		setVisible(true);
		// addImage("konacnaslika.bmp");
		// this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		setSize(1000, 800);
		// this.setUndecorated(true);
		// this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!img.isSaved()) {
					int reply = JOptionPane.showConfirmDialog(Program.this,
							"You have not saved your project since the last change. All changes will be lost. Are you sure you want to exit?",
							"Project not saved", JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION) {
						Program.this.dispose();
						img.destroyExecutors();
						//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					} else {
						setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					}
				} else {
					img.destroyExecutors();
					setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}

			};
		});


		
	}
	
	void configureGUI() {
		
		dlm = new DefaultListModel<String>();
		dlmexec = new DefaultListModel<String>();
		allposibleoperations=new JList<String>(dlm);
		operations_exec_list=new JList<String>(dlmexec);
		dlm.addElement("add");
		dlm.addElement("sub");
		dlm.addElement("subinv");
		dlm.addElement("div");
		dlm.addElement("divinv");
		dlm.addElement("mul");
		dlm.addElement("fill");
		dlm.addElement("log");
		dlm.addElement("max");
		dlm.addElement("min");
		dlm.addElement("pow");
		dlm.addElement("grayscale");
		dlm.addElement("inversion");
		dlm.addElement("median");
		dlm.addElement("blackwhite");
		
		/**************MENI**************/
		main_menu=new JMenuBar();
		file_menu=new JMenu("File");
		file_menu.setFont(new Font("Arial", Font.BOLD, 15));
		
		JMenuItem jmiOpenProject=new JMenuItem("Open project");
		file_menu.add(jmiOpenProject);
		jmiOpenProject.addActionListener(l->{
			if (!img.isSaved()) {
				int reply = JOptionPane.showConfirmDialog(Program.this,
						"You have not saved your project since the last change. All changes will be lost. Are you sure you want to continue?",
						"Project not saved", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.NO_OPTION) {
					return;
				}
			}
			JFileChooser chose_xml_file = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			chose_xml_file.setDialogTitle("Choose a image you want to load");
			chose_xml_file.setFileFilter(new FileNameExtensionFilter(
				        "XML", "xml"));
			int choise=chose_xml_file.showOpenDialog(this);
			if (choise == JFileChooser.APPROVE_OPTION) {
				LoadNewProject(Paths.get(chose_xml_file.getSelectedFile().getAbsolutePath()));
				img.setSaved(false);
			}
		});
		
		
		JMenuItem jmiSaveProject=new JMenuItem("Save project");
		jmiSaveProject.addActionListener(l->{
			JFileChooser choose_where_to_save = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			choose_where_to_save.setDialogTitle("Save Project");
			choose_where_to_save.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ret=choose_where_to_save.showDialog(null,"Save");
			if(ret==choose_where_to_save.APPROVE_OPTION) {
				if(!img.SaveImage(Paths.get(choose_where_to_save.getSelectedFile().getAbsolutePath()))){
					 JOptionPane.showMessageDialog(this, "You must enter valid path!", "Unable to Save project",
						        JOptionPane.ERROR_MESSAGE);
				}
				else {
					 JOptionPane.showMessageDialog(this, "Project saved successfully!", "Project saved",
						        JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		});
		file_menu.add(jmiSaveProject);
		file_menu.addSeparator();
		JMenuItem export_menu=new JMenuItem("Export image");
		export_menu.addActionListener(l->{
			JFileChooser choose_where_to_save = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			choose_where_to_save.setDialogTitle("Save Image");
			choose_where_to_save.setFileFilter(new FileNameExtensionFilter(
			        "BMP & PAM Images", "bmp", "pam"));
			int ret=choose_where_to_save.showDialog(null,"Save");
			if(ret==choose_where_to_save.APPROVE_OPTION) {
				if(!img.exportImage(choose_where_to_save.getSelectedFile().getAbsolutePath())){
					 JOptionPane.showMessageDialog(this, "1.\tYou must enter file name with tile type!\n  \tExample: example.[bmp|pam]\n2.\tYour image must contain at least one layer!", "Unable to Save",
						        JOptionPane.ERROR_MESSAGE);
				}
				else {
					 JOptionPane.showMessageDialog(this, "Image exported successfully!", "Image exported",
						        JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		});
		JMenu operation_menu=new JMenu("Operation");
		operation_menu.setFont(new Font("Arial", Font.BOLD, 15));
		JMenuItem save_operation=new JMenuItem("Save composite operation");
		
		save_operation.addActionListener(l->{
			class OperationDialog extends JDialog{
				public OperationDialog() {
					super(Program.this,true);
					setTitle("Choose composite operation to save");
					setResizable(false);
				    setLocation(400, 400);
				    
				    JPanel dialogPanel = new JPanel(new BorderLayout());
				    
					JPanel radioPanel = new JPanel(new GridLayout(0,2));
					
					
					
					JLabel jlb_add_new_layer=new JLabel();
					jlb_add_new_layer.setText("Choose an operation: \n");
					jlb_add_new_layer.setHorizontalAlignment(SwingConstants.CENTER);
					dialogPanel.add(jlb_add_new_layer, BorderLayout.NORTH);
					
					ButtonGroup bg_operations = new ButtonGroup();
					for(int i=0;i<allposibleoperations.getModel().getSize();i++) {
						String opname=allposibleoperations.getModel().getElementAt(i);
						if(img.isComposite(opname)) {
							JCheckBox jcbop=new JCheckBox(allposibleoperations.getModel().getElementAt(i),false);
							jcbop.setActionCommand(opname);
							bg_operations.add(jcbop);
							radioPanel.add(jcbop);
						}
					}
					dialogPanel.add(radioPanel,BorderLayout.CENTER);
					
					JButton jbsave= new JButton("Save");
					jbsave.addActionListener(l->{
						JFileChooser choose_where_to_save = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
						choose_where_to_save.setDialogTitle("Save composite operation");
						int ret=choose_where_to_save.showDialog(null,"Save");
						if(ret==choose_where_to_save.APPROVE_OPTION) {
							if(!img.SaveCompositeOperation(bg_operations.getSelection().getActionCommand(), choose_where_to_save.getSelectedFile().getAbsolutePath())){
								 JOptionPane.showMessageDialog(new JFrame(), "Wrong path, try again! Path must contain finename with extension!","Unable to save composite operation",
									        JOptionPane.ERROR_MESSAGE);
							}
							else {
								this.dispose();
							}
						}
					});
					dialogPanel.add(jbsave, BorderLayout.SOUTH);
					add(dialogPanel);
					pack();
				}
				
			}
			OperationDialog od=new OperationDialog();
			od.setVisible(true);
		
		});
		
		JMenuItem load_operation=new JMenuItem("Load composite operation");
		load_operation.addActionListener(l->{
			JTextField jtfopname;
			class EnterNameDialog extends JDialog{
				public EnterNameDialog() {
					super(Program.this,true);
					setTitle("Load composite operation");
					setResizable(false);
				    setLocation(400, 400);
				    
				    JPanel dialogPanel = new JPanel(new BorderLayout());
				    
					JPanel radioPanel = new JPanel(new GridLayout(0,2));
					
					
					
					JLabel jlb_add_new_layer=new JLabel();
					jlb_add_new_layer.setText("Enter the name of composite operation you want to load");
					jlb_add_new_layer.setHorizontalAlignment(SwingConstants.CENTER);
					dialogPanel.add(jlb_add_new_layer, BorderLayout.NORTH);
					
					JTextField jtfopname=new JTextField(8);
					dialogPanel.add(jtfopname,BorderLayout.CENTER);
					
					JButton jbok= new JButton("Ok");
					jbok.addActionListener(l->{
						if(jtfopname.getText().length()==0) {
							 JOptionPane.showMessageDialog(new JFrame(), "You must enter composite operation name!","Unable to load composite operation",
								        JOptionPane.ERROR_MESSAGE);
						}
						JFileChooser chose_img_file = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
						chose_img_file.setDialogTitle("Choose a composite operation you want to load");
						int choise=chose_img_file.showOpenDialog(this);
						if (choise == JFileChooser.APPROVE_OPTION) {
							File selectedFile = chose_img_file.getSelectedFile();
							try {
								img.LoadCompositeOperation(jtfopname.getText(), selectedFile.getAbsolutePath().toString());
								EnterNameDialog.this.dispose();
							} catch (OperationException e) {
								JOptionPane.showMessageDialog(new JFrame(), e.getMessage(),"Unable to load composite operation",
								        JOptionPane.ERROR_MESSAGE);
							}
							
						}
					});
					dialogPanel.add(jbok, BorderLayout.SOUTH);
					add(dialogPanel);
					pack();
				}
				
			}
			EnterNameDialog end=new EnterNameDialog();
			end.setVisible(true);
			
		});
		operation_menu.add(load_operation);
		operation_menu.add(save_operation);

	
		file_menu.add(export_menu);
		main_menu.add(file_menu);
		main_menu.add(operation_menu);
		//setMenuBar(main_menu);
		setJMenuBar(main_menu);
		
		
		/*****************************************/
		
		/**********CP PANEL*********************/
		cp=new Panel(new GridLayout(2,1));
		layer_panel_outside=new JPanel(new BorderLayout()); // !!)
		layer_panel=generateLayerPanel();
		selection_panel_outside=new JPanel(new BorderLayout()); // !!)
		selection_panel=generateSelectionPanel();

		JPanel layer_panel_wrapper=new JPanel(new BorderLayout());
		layer_panel_wrapper.add(layer_panel, BorderLayout.PAGE_START);
		layer_panel_wrapper.setBackground(Color.WHITE);
		
		JTabbedPane jtbpLayers=new JTabbedPane();
		JTabbedPane jtbpSelections=new JTabbedPane();
		
		JPanel operation_panel=generateOperationPanel();
		
		//String title = "Selections";
		//Border border = BorderFactory.createTitledBorder(title);
		//selection_panel_outside.setBorder(border);
		

		ImageIcon addimgicon = new ImageIcon("plus.png");
		JButton btnadd=new JButton("Add New Layer",addimgicon);
		btnadd.addActionListener(l->{
			addNewLayer();
		});
		
		layer_panel_outside.add(btnadd, BorderLayout.NORTH);
		
		JScrollPane jscplayer=new JScrollPane(layer_panel_wrapper,
	            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jscplayer.setPreferredSize(new Dimension(cp.getWidth(),cp.getHeight()/2));
		layer_panel_outside.add(jscplayer, BorderLayout.CENTER);
		jtbpLayers.add("Layers", layer_panel_outside);
		
		selection_panel_wrapper=new JPanel(new BorderLayout());
		selection_panel_wrapper.add(selection_panel, BorderLayout.PAGE_START);
		selection_panel_wrapper.setBackground(Color.WHITE);
		
		JPanel spheader=new JPanel(new FlowLayout());
		JLabel jlb_selection_name=new JLabel("Current selection name:");
		spheader.add(jlb_selection_name);
		jtf_selection_name=new JTextField(8);
		jtf_selection_name.setEnabled(false);
		spheader.add(jtf_selection_name);
		ImageIcon imgicon = new ImageIcon("apply.png");
		btnapply=new JButton("Apply",imgicon);
		btnapply.addActionListener(l->{
			addNewSelection();
		});
		btnapply.setEnabled(false);
		spheader.add(btnapply);
		imgicon = new ImageIcon("discard.png");
		btndiscard=new JButton("Discard",imgicon);
		btndiscard.addActionListener(l->{
			curr_selection_.clear();
			jtf_selection_name.setText("");
			jtf_selection_name.setEnabled(false);
			btnapply.setEnabled(false);
			btndiscard.setEnabled(false);
			showCurrentSelection();
		});
		btndiscard.setEnabled(false);
		spheader.add(btndiscard);
		selection_panel_outside.add(spheader, BorderLayout.NORTH);
		
		JScrollPane jscpselection=new JScrollPane(selection_panel_wrapper,
	            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jscplayer.setPreferredSize(new Dimension(cp.getWidth(),cp.getHeight()/2));
		selection_panel_outside.add(jscpselection, BorderLayout.CENTER);
		jtbpSelections.add("Selections", selection_panel_outside);
		
		
		
		jtbpSelections.add("Operations", operation_panel);
		layer_panel_outside.setBackground(Color.WHITE);
		selection_panel_outside.setBackground(Color.WHITE);
		cp.add(jtbpLayers);
		cp.add(jtbpSelections);
		add(cp,BorderLayout.EAST);

		
		
		
		 
	     jLabelImage = new JLabel();
         JPanel slika=new JPanel(new FlowLayout());
         jLabelImage.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					//System.out.println("Mouse pressed x="+e.getX()+" y="+e.getY());
					mousePressed_=true;
					rectangle_x_start_=e.getX();
					rectangle_y_start_=e.getY();
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					//System.out.println("Mouse released x="+e.getX()+" y="+e.getY());
					int x=e.getX();
					int y=e.getY(); 
					rectangle_x_end_=(x<0)?0:x;
					rectangle_x_end_=(x>=img.getImgWidth())?img.getImgWidth()-1:rectangle_x_end_;
					rectangle_y_end_=(y<0)?0:y;
					rectangle_y_end_=(y>=img.getImgHeight())?img.getImgHeight()-1:rectangle_y_end_;
				
					int lx=(rectangle_x_start_<rectangle_x_end_)?rectangle_x_start_:rectangle_x_end_;
					int ly=(rectangle_y_start_<rectangle_y_end_)?rectangle_y_start_:rectangle_y_end_;
					curr_selection_.add(new Rectangle(lx, ly, Math.abs(rectangle_x_start_-rectangle_x_end_), Math.abs(rectangle_y_start_-rectangle_y_end_)));
					mousePressed_=false;
					btnapply.setEnabled(true);
					btndiscard.setEnabled(true);
					jtf_selection_name.setEnabled(true);
					showCurrentSelection();
					//add rectangle
				}
			});
	       // this.getContentPane().add(jLabel);
	        slika.add(jLabelImage);
	        jscpImagePane=new JScrollPane(slika);
	        this.add(jscpImagePane , BorderLayout.CENTER);
		

	}

	public JPanel generateLayerPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 5, 5));
		panel.setBackground(Color.WHITE);
		return panel;
	}
	public JPanel generateSelectionPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 5, 5));
		panel.setBackground(Color.WHITE);
		return panel;
	}
	
	public JPanel generateOperationPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JScrollPane jsc=new JScrollPane(allposibleoperations);
		jsc.setPreferredSize(new Dimension(100,cp.getHeight()/2));
		panel.add(jsc,BorderLayout.WEST);
		
		JPanel buttons=new JPanel(new GridLayout(4,1,0,0));
		JButton btnexecute=new JButton("Execute");
		btnexecute.setEnabled(false);
		String lbl = "<html>" + "Add selected" + "<br>" + "operation to list"+ "</html>";
		JButton btnadd=new JButton(lbl);
		lbl = "<html>" + "Create" + "<br>" + "composite"+ "</html>";
		JButton btncreatecomposite=new JButton(lbl);
		lbl = "<html>" + "Remove selected" + "<br>" + "operation from list"+ "</html>";
		btncreatecomposite.setEnabled(false);
		JButton btnremove=new JButton(lbl);
		btnremove.setEnabled(false);
		btnadd.addActionListener(l->{
			if(allposibleoperations.isSelectionEmpty())return;//check is anything selected
			List<String> selected=allposibleoperations.getSelectedValuesList();
			selected.forEach(operation->{
				dlmexec.addElement(operation);
				btnexecute.setEnabled(true);
				btncreatecomposite.setEnabled(true);
				btnremove.setEnabled(true);				
			});
		});
		
		btnremove.addActionListener(l->{
			if(operations_exec_list.isSelectionEmpty())return;
			dlmexec.remove(operations_exec_list.getSelectedIndex());
			if(dlmexec.size()==0) {
				btnexecute.setEnabled(false);
				btncreatecomposite.setEnabled(false);
				btnremove.setEnabled(false);		
			}
		});
		
		btnexecute.addActionListener(l->{
			//if(img.numberOfLayers()==0)return;
			ExecuteOperation();
		});



		JLabel compname=new JLabel("Enter the name: ");
		JTextField jtfcomositename=new JTextField(8);
		JPanel compPanel=new JPanel(new BorderLayout());
		JPanel jtfpanel = new JPanel(new GridLayout(2,1,5,5));
		jtfpanel.add(compname);
		jtfpanel.add(jtfcomositename);
		
		compPanel.add(jtfpanel, BorderLayout.CENTER);
		compPanel.add(btncreatecomposite, BorderLayout.EAST);
		
		btncreatecomposite.addActionListener(l->{
			ArrayList<String> selectedop=new ArrayList<String>();
			for(int i = 0; i< operations_exec_list.getModel().getSize();i++){
	           selectedop.add(operations_exec_list.getModel().getElementAt(i));
	        }
			if(jtfcomositename.getText().length()==0||img.operationExists(jtfcomositename.getText())==true) {
				JOptionPane.showMessageDialog(new JFrame(),
						"Ime operacije se mora zadati i mora biti jedinstveno!",
						"Operation can not be created!", JOptionPane.ERROR_MESSAGE);
				return;
			}
			img.createCompositeOperation(jtfcomositename.getText(), selectedop);
			dlm.addElement(jtfcomositename.getText());
			dlmexec.clear();
			btnexecute.setEnabled(false);
			btncreatecomposite.setEnabled(false);
			btnremove.setEnabled(false);	
			jtfcomositename.setText("");
		});

		buttons.add(btnadd);
		buttons.add(btnremove);
		buttons.add(btnexecute);
		buttons.add(compPanel);
		panel.add(buttons,BorderLayout.CENTER);
		jsc=new JScrollPane(operations_exec_list);
		jsc.setPreferredSize(new Dimension(100,cp.getHeight()/2));
		panel.add(jsc,BorderLayout.EAST);
		panel.setBackground(Color.WHITE);
		return panel;
	}
	
	
	public void addNewLayer() {
		class LayerDialog extends JDialog{
			JButton selectfile;
			JTextField jtfwidth;
			JTextField jtfheight;
			JButton jbcreateEmpty;
			public LayerDialog() {
				super(Program.this,true);
				setTitle("Add New Layer");
				setResizable(false);
			    setLocation(400, 400);
			    
				JPanel dialogPanel = new JPanel(new GridLayout(3,1));
				JLabel jlb_add_new_layer=new JLabel();
				jlb_add_new_layer.setText("Choose an option: \n");
				jlb_add_new_layer.setHorizontalAlignment(SwingConstants.CENTER);
				dialogPanel.add(jlb_add_new_layer);
				ButtonGroup bg_how_to_add = new ButtonGroup();
				JCheckBox jcb_path=new JCheckBox("1.Add layer with picture",false);
				JCheckBox jcb_empty=new JCheckBox("2.Add empty layer",false);
				
				bg_how_to_add.add(jcb_path);
				bg_how_to_add.add(jcb_empty);
				jcb_path.addActionListener(l->{
					DisableAddEmpty();
					EnableAddPicture();
				});
				jcb_empty.addActionListener(l->{
					DisableAddPicture();
					EnableAddEmpty();
				});
				
				
				//formiranje dela za dodavanje slike
				JPanel p1 = new JPanel();
				p1.add(jcb_path);
				selectfile=new JButton("Select file");
				p1.add(selectfile);
				selectfile.addActionListener(l->{
					JFileChooser chose_img_file = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
					chose_img_file.setDialogTitle("Choose a image you want to load");
					chose_img_file.setFileFilter(new FileNameExtensionFilter(
						        "BMP & PAM Images", "bmp", "pam"));
					int choise=chose_img_file.showOpenDialog(this);
					if (choise == JFileChooser.APPROVE_OPTION) {
						File selectedFile = chose_img_file.getSelectedFile();
						Layer added_layer=img.addLayer(selectedFile.getAbsolutePath());
						addLayerToLayerPanel(added_layer);
						img.setSaved(false);
						LayerDialog.this.dispose();
					}
				});
				//formiranje dela za dodavanje praznog sloja
				JPanel p2 = new JPanel();
				p2.add(jcb_empty);
				jtfwidth=new JTextField(4);
				jtfheight=new JTextField(4);
				JLabel jlbwidth=new JLabel("Width :");
				jtfwidth.setText(img.getImgWidth()+"");
				p2.add(jlbwidth);
				p2.add(jtfwidth);
				JLabel jlbheight=new JLabel("Height :");
				jtfheight.setText(img.getImgHeight()+"");
				p2.add(jlbheight);
				p2.add(jtfheight);
				jbcreateEmpty=new JButton("Create");
				p2.add(jbcreateEmpty);
				jbcreateEmpty.addActionListener(l->{
					int w,h;
					try {
						w=Integer.parseInt(jtfwidth.getText());
						h=Integer.parseInt(jtfheight.getText());
						if(w==0||h==0)throw new NumberFormatException();
						Layer added_layer=img.addEmptyLayer(w, h);
						addLayerToLayerPanel(added_layer);
					}
					catch(NumberFormatException nfe) {
						  JOptionPane.showMessageDialog(new JFrame(), "Width and height must be bigger then zero and must be entered!", "Params Error",
							        JOptionPane.ERROR_MESSAGE);
					}
					finally {
						LayerDialog.this.dispose();
					}
				});
				
				dialogPanel.add(p1);
				dialogPanel.add(p2);
				DisableAddEmpty();
				DisableAddPicture();
				add(dialogPanel);
				pack();
			}
			void EnableAddPicture() {
				selectfile.setEnabled(true);
			}
			void EnableAddEmpty() {
				if(img.getImgWidth()==0&&img.getImgHeight()==0) {
					jtfwidth.setEnabled(true);
					jtfheight.setEnabled(true);
				}
				jbcreateEmpty.setEnabled(true);
			}
			void DisableAddPicture() {
				selectfile.setEnabled(false);
			}
			void DisableAddEmpty() {
				jtfwidth.setEnabled(false);
				jtfheight.setEnabled(false);
				jbcreateEmpty.setEnabled(false);
			}
		}
		LayerDialog ld=new LayerDialog();
		ld.setVisible(true);
	}
	
	public void addNewSelection() {
		try {
			if (curr_selection_.size() == 0) {
				throw new SelectionException("Current selection does not exists. Please select an area to create selection and try again.");
			}
			if (jtf_selection_name.getText().length() == 0) {
				throw new SelectionException("Selection name must be entered. Please enter the name and try again.");
			}
			Selection selection_applied=img.addSelection(jtf_selection_name.getText(), curr_selection_);
			jtf_selection_name.setText("");
			curr_selection_.clear();
			btnapply.setEnabled(false);
			btndiscard.setEnabled(false);
			jtf_selection_name.setEnabled(false);
			addSelectionToSelectionPanel(selection_applied);
			showCurrentSelection();
			img.showActiveSelections(img_shown_);
		} catch (SelectionException se) {
			JOptionPane.showMessageDialog(new JFrame(),
					se.getMessage(),
					"Selection can not be applied!", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void addLayerToLayerPanel(Layer layer) {
		int id=next_layer_id++;
		Color background = new Color(230, 247, 255);
	
		layer_panel.setLayout(new GridLayout(img.numberOfLayers(), 1, 5, 5));
		JPanel row_outside=new JPanel(new BorderLayout());
		JPanel row=new JPanel(new FlowLayout());
		ImageIcon rmimgicon = new ImageIcon("kanta.png");
		JButton btnrm=new JButton(rmimgicon);
		btnrm.addActionListener(acl->{
			if(img.isOperationInProgres()) {
				JOptionPane.showMessageDialog(new JFrame(), "Operation in progress! Wait for operation to finish.", "Cannot remove layer",
				        JOptionPane.ERROR_MESSAGE);
				return;
			}
			img.removeLayer(layer);
			layer_panel.remove(row_outside);
			layer_panel_outside.revalidate();
			cp.revalidate();
			cp.repaint();
		});
		row_outside.add(btnrm,BorderLayout.WEST);
		//add action listener
		JLabel lblayer = new JLabel("Layer " + id);
		lblayer.setFont(new Font("Arial", Font.BOLD, 15));
		row.add(lblayer);
		JTextField tfopacity = new JTextField(2);
		tfopacity.setText(layer.getOpacity()+"");
		row.add(tfopacity);
		JLabel lbopacity = new JLabel("opacity(%)");
		lbopacity.setFont(new Font("Arial", Font.PLAIN, 15));
		row.add(lbopacity);
		JButton setOk=new JButton("set");
		setOk.addActionListener(l->{
			
				try {
					int opacity=Integer.parseInt(tfopacity.getText());
					
					if(opacity<0||opacity>100)throw new NumberFormatException();
					layer.setOpacity((short)opacity);
					img_changed_=true;
					img.setSaved(false);
					RevaildateImage();
					img.showActiveSelections(img_shown_);
					showCurrentSelection();
				}
				catch(NumberFormatException nfe) {
					  JOptionPane.showMessageDialog(new JFrame(), "Width and height must be bigger then zero and must be entered!", "Params Error",
						        JOptionPane.ERROR_MESSAGE);
					  tfopacity.setText(layer.getOpacity()+"");
				}
		});
		row.add(setOk);
		JCheckBox cbvisible = new JCheckBox("visible", layer.isVisible());
		JCheckBox cbactive = new JCheckBox("active", layer.isActive());
		cbvisible.setHorizontalAlignment(SwingConstants.CENTER);
		cbactive.setHorizontalAlignment(SwingConstants.CENTER);
		cbvisible.addActionListener(l->{
			layer.setVisible(cbvisible.isSelected());
			img_changed_=true;
			img.setSaved(false);
			RevaildateImage();
			img.showActiveSelections(img_shown_);
			showCurrentSelection();
		});
		cbactive.addActionListener(l->{
			layer.setActive(cbactive.isSelected());
		});
		row.add(cbvisible);
		row.add(cbactive);
		row.setBackground(background);
		row_outside.add(row);
		layer_panel.add(row_outside);	
		//layer_panel.revalidate();
		cp.revalidate();
	}
	private void addSelectionToSelectionPanel(Selection selection) {
		Color background = new Color(230, 247, 225);
		selection_panel.setLayout(new GridLayout(img.numberOfSelections(), 1, 5, 5));
		JPanel row=new JPanel(new BorderLayout());
	
		ImageIcon rmimgicon = new ImageIcon("kanta.png");
		JButton btnrm=new JButton(rmimgicon);
		btnrm.addActionListener(acl->{
			//remove selection
			try {
				img.deleteSelection(selection.getName());
				selection_panel.remove(row);
				selection_panel.setLayout(new GridLayout(0, 1, 5, 5));
				selection_panel.revalidate();
				selection_panel.repaint();
				RevaildateImage();
				img.showActiveSelections(img_shown_);
				showCurrentSelection();
				
			} catch (SelectionException e) {
				//nece se desiti konrolisano je
				System.out.println("Provera da se ne desi greska kad se uklanja selekcija!");
			}
		});
		row.add(btnrm, BorderLayout.WEST);
		JLabel lbselection = new JLabel(selection.getName());
		lbselection.setFont(new Font("Arial", Font.BOLD, 15));
		lbselection.setHorizontalAlignment(SwingConstants.CENTER);
		row.add(lbselection, BorderLayout.CENTER);
		JCheckBox cbactive = new JCheckBox("active", selection.isActive());
		cbactive.setHorizontalAlignment(SwingConstants.CENTER);
		cbactive.addActionListener(l->{
			boolean before=selection.isActive();
			selection.setActive(cbactive.isSelected());
			if(before) {
				img_changed_=true;
				RevaildateImage();
				showCurrentSelection();
			}
			img.showActiveSelections(img_shown_);
			showCurrentSelection();
		});
		row.add(cbactive, BorderLayout.EAST);
		row.setBackground(background);
		row.setBounds(5, 0, row.getWidth()-5,row.getHeight());
		selection_panel.add(row);	
		//selection_panel.revalidate();
		
		/*for (int i = 0; i < 5; i++) {
				row=new Panel(new BorderLayout());
				JLabel lblayer = new JLabel("Selection " + i);
				lblayer.setFont(new Font("Arial", Font.PLAIN, 15));
				row.add(lblayer,BorderLayout.WEST);
				JCheckBox cbactive = new JCheckBox("active", false);
				cbactive.setHorizontalAlignment(SwingConstants.CENTER);
				cbactive.setBackground(background);
				row.add(cbactive,BorderLayout.CENTER);
				row.setBackground(background);
				panel.add(row);
			
		}*/
		cp.revalidate();
	}
	
	public synchronized void RevaildateImage() {
		if(img_changed_==false)return;
		Layer layer=img.compressIntoOneLayer();
		if (layer == null) {
			img_shown_ = null;
			jLabelImage.setIcon(null);
			jscpImagePane.revalidate();
			// mozda pobrisati i sve selekcije
			if (img.numberOfLayers() == 0) {
				img.deleteAllSelections();
				curr_selection_.clear();
				btnapply.setEnabled(false);
				btndiscard.setEnabled(false);
				selection_panel.removeAll();
				selection_panel = generateSelectionPanel();		
				selection_panel_wrapper.add(selection_panel, BorderLayout.PAGE_START);
				
				//selection_panel_outside.add(new JScrollPane(selection_panel), BorderLayout.CENTER);
				cp.revalidate();
				cp.repaint();

			}
			//selekcije pobrisane
			return;
		}
		int height=layer.getHeight();
		int width=layer.getWidth();
		BufferedImage bufferedImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		
		/*int color=0;
		Pixel p=null;
		for (int y = 0; y < height; y++) 
			for (int x = 0; x < width; x++) {
				p=layer.layer_.get(y*width+x);
				color=p.getAlpha()<<8;
				color=(color|p.getRed())<<8;
				color=(color|p.getGreen())<<8;
				color=color|p.getBlue();
				bufferedImage.setRGB(x, y, color);
			}
		 */
		AtomicInteger counter =new AtomicInteger(0);
		layer.layer_.forEach(pixel->{
			int color=0;
			int px,py;
			int pos=counter.getAndIncrement();
			py=pos/width;
			px=pos%width;
			color=pixel.getAlpha()<<8;
			color=(color|pixel.getRed())<<8;
			color=(color|pixel.getGreen())<<8;
			color=color|pixel.getBlue();
			bufferedImage.setRGB(px, py, color);
		});

        jLabelImage.setIcon(new ImageIcon(bufferedImage));
        jscpImagePane.revalidate();
		setImageChanged(false);
		img_shown_=bufferedImage;
		//showCurrentSelection();
       // revalidate();
	}
	
	public synchronized void showCurrentSelection() {
		if(img_shown_==null) {
			return;
		}
		if(curr_selection_.isEmpty()) {
			jLabelImage.setIcon(new ImageIcon(img_shown_));
	        jscpImagePane.revalidate();
		}
		int width=img_shown_.getWidth();
		int height=img_shown_.getHeight();
		BufferedImage out = new BufferedImage(width,height,img_shown_.getType());
		Graphics2D g2d = out.createGraphics();
		g2d.drawImage(img_shown_, 0, 0, width,height, null);
		g2d.setColor(Color.YELLOW);
		curr_selection_.forEach(rectangle->{
			g2d.drawRect(rectangle.getStartX(), rectangle.getStartY(), rectangle.getWidth(), rectangle.getHeight());
		});
		g2d.dispose();
		jLabelImage.setIcon(new ImageIcon(out));
        jscpImagePane.revalidate();
	}
	private void getArguments(CompositeOperation co) {
		JPanel ars_panel=co.generateOperationJPanel();	

		class ArgsDialog extends JDialog{
			public ArgsDialog() {
				super(Program.this,true);
				setTitle("Set arguments for operations");
				setResizable(false);
			    setLocation(400, 400);
				List<String> args=new ArrayList<String>();
			    List<JTextField> jtfargs=new ArrayList<JTextField>();
			    JPanel dialogPanel = new JPanel(new BorderLayout());
			    JPanel args_panel=co.generateOperationJPanel();	
			    JPanel basic_panel=(JPanel) args_panel.getComponent(1);//dohvatamo gridlayout panel gde su basic operacije
			    
			
			  /*for (Component component : basic_panel.getComponents()) {
		            if (component instanceof JPanel) {
		            	//usli smo u panel operacije
		            	JPanel operation_panel=(JPanel)((JPanel)component).getComponent(1);
		            	for (Component elem : operation_panel.getComponents()) {
		            		if(elem instanceof JTextField) {
		            			jtfargs.add((JTextField)elem);
		            		}
		            	}
		            }
		        }*/
			    Stream.of(basic_panel.getComponents())
			    .filter(c->{ return c instanceof JPanel;}).forEach(op_panel->{
			    	JPanel operation_panel=(JPanel)((JPanel)op_panel).getComponent(1);
			    	Stream.of(operation_panel.getComponents()).filter(tf->{ return tf instanceof JTextField;}).forEach(tf->{
			    		jtfargs.add((JTextField)tf);
			    	});
			    });
			/*    Stream.of(args_panel.getComponents())
			    .filter(c->{return c instanceof JTextField;})
			    .map(c -> (JTextField) c).forEach(jtfargs::add);
				*/
				dialogPanel.add(args_panel,BorderLayout.CENTER);
				
				JButton jbExecute= new JButton("Execute");
				jbExecute.addActionListener(l->{
					
					jtfargs.forEach(tf->{
						args.add(tf.getText());
					});
					//args.forEach(System.out::println);
					img.executeOperation(co,args);
					this.dispose();
				});
				dialogPanel.add(jbExecute, BorderLayout.SOUTH);
				add(dialogPanel);
				pack();
			}
			
		}
		ArgsDialog argd=new ArgsDialog();
		argd.setVisible(true);
		
		return;
	}

	private void ExecuteOperation() {
		
		ArrayList<String> selectedop=new ArrayList<String>();
		for(int i = 0; i< operations_exec_list.getModel().getSize();i++){
           selectedop.add(operations_exec_list.getModel().getElementAt(i));
        }
	/*	int num_of_selected=selectedop.size();
		JPanel operation_panel=new JPanel(new GridLayout(5, num_of_selected/5,5,5));
		ButtonGroup bg_ops=new ButtonGroup();
		List<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
		selectedop.forEach(opname->{
			JCheckBox jcbop=new JCheckBox(opname,false)
			checkboxes.add(jcbop);
			operation_panel.add(jcbop);
		});
	*/
		
		CompositeOperation co=img.generateCompositeForExecution(selectedop);
		getArguments(co);

		//XMLFormatter xmlf=new XMLFormatter();
		//xmlf.SaveFun(Paths.get(funpath), co);
		
	}
	
	
	private boolean LoadNewProject(Path path) {
		XMLFormatter xmlf=new XMLFormatter();
		Image image_new=xmlf.ReadImage(path, this);
		if(image_new==null) {
			JOptionPane.showMessageDialog(this, "Project could not be loaded! Check all files before try again!", "Loading project error",
			        JOptionPane.ERROR_MESSAGE);
			return false;
		}
		img=image_new;
		this.getContentPane().removeAll();
		configureGUI();
		List<Selection> selections=img.getSelections();
		List<Layer> layers=img.getLayers();
		List<Operation> operations=img.getOperations();
		layers.forEach(this::addLayerToLayerPanel);
		selections.forEach(this::addSelectionToSelectionPanel);
		operations.stream().filter(o->{return o instanceof CompositeOperation;}).forEach(co->{
			dlm.addElement(co.getName());
		});
		img_changed_=true;
		RevaildateImage();
		img.showActiveSelections(img_shown_);
		showCurrentSelection();
		//treba poubacivati kompozitne, lejere!
		this.revalidate();
		this.repaint();
		return true;
	}
	public void disableSaving() {
		file_menu.setEnabled(false);
	}
	public void enableSaving() {
		file_menu.setEnabled(true);
	}
	public static void main(String[] args) {
		
		Program prog=new Program();
		/*BMPFormatter bp=new BMPFormatter();
		EmbededFormatters ef=new EmbededFormatters();
		Layer l=ef.ReadImage("pamslika.pam");
		bp.SaveImage(l, "konacnaslikabuff.bmp");*/
		//Image i=new Image();
		//i.addLayer(l);
		//bp.exportImage(i, "komprimovano.bmp");
	//	prog.addImage("konacnaslika.bmp");
		
		/*PAM TEST
		BMPFormatter bp=new BMPFormatter();
		PAMFormatter pam=new PAMFormatter();
		Layer l=pam.ReadImage("pamslika.pam");
		bp.SaveImage(l, "pamtest.bmp");
		//pam.SaveImage(l,"pamslika.pam");
		*/
	} 

}

