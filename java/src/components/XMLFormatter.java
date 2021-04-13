package components;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import exceptions.SelectionException;
import operations.CompositeOperation;
import operations.Operation;
import program.Program;

public class XMLFormatter {

	public XMLFormatter() {

	}

	public boolean SaveFun(Path path, CompositeOperation composite_operation) {
		try {
			String name = path.getFileName().toString();
			ArrayList<Operation> listop = composite_operation.getOperationList();
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			// document.setXmlStandalone(true);
			// root element
			Element root = document.createElement("CompositeOperation");
			root.setAttribute("name", name);
			document.appendChild(root);

			// operation element
			listop.forEach(operation -> {
				Element operation_node = document.createElement("Operation");
				operation_node.appendChild(document.createTextNode(operation.getName()));
				root.appendChild(operation_node);
			});

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // ova linija uklanja standalone=no
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(path.toString()));

			transformer.transform(domSource, streamResult);

		} catch (ParserConfigurationException | TransformerException e) {
			return false;
		}
		return true;
	}

	public ArrayList<String> LoadFun(Path path, String funname) {
		ArrayList<String> list_of_operations = new ArrayList<String>();
		try {
			File funfile = new File(path.toString());

			String filename = path.getFileName().toString();

			ArrayList<Operation> listop = new ArrayList<Operation>();
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document;

			document = documentBuilder.parse(funfile);

			document.getDocumentElement().normalize();
			Node root = document.getDocumentElement();
			Element co = null;
			if (root.getNodeType() == Node.ELEMENT_NODE)
				co = (Element) root;
			else
				return null;
			NodeList nodeList = co.getElementsByTagName("Operation");

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (root.getNodeType() == Node.ELEMENT_NODE) {
					Element operation = (Element) node;
					String op = operation.getTextContent();
					list_of_operations.add(op);
				} else
					return null;
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			return null;
		}

		return list_of_operations;
	}

	public boolean saveImage(Image image, Path path) {
		try {
			String projname = path.getFileName().toString();//vratice ime foldera
			System.out.println(path.toString()+"\\layers"); //C:\Users\LT\Desktop\photoshop
			File f = new File(path.toString()+"\\layers");
			f.mkdir();
			f = new File(path.toString()+"\\funs");
			f.mkdir();
			
			List<Operation> listop = image.getOperations();
			List<Selection> listsel = image.getSelections();
			List<Layer> listlay = image.getLayers();
			
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			// document.setXmlStandalone(true);
			
			// root element
			Element root = document.createElement("Image");
			root.setAttribute("width", image.getImgWidth()+"");
			root.setAttribute("height", image.getImgHeight()+"");
			document.appendChild(root);

			//layers
			Element layersElem = document.createElement("Layers");
			root.appendChild(layersElem);
			AtomicInteger ai=new AtomicInteger(0);
			BMPFormatter bmpf=new BMPFormatter();
			
			listlay.forEach(l->{

				
				Element layerElem = document.createElement("Layer");
				layerElem.setAttribute("opacity", l.getOpacity()+"");
				int act=(l.isActive())?1:0;
				layerElem.setAttribute("active", act+"");
				int vsb=(l.isVisible())?1:0;
				layerElem.setAttribute("visible", vsb+"");
				
				
				//sacuvati na putanju
				String pathToLayer=path.toString()+"\\layers\\layer"+ai.get()+".bmp";
				bmpf.SaveImage(l, pathToLayer);
				layerElem.setTextContent("layers\\layer"+ai.getAndIncrement()+".bmp");
				
				
				layersElem.appendChild(layerElem);	
			});
			
			
			//selections
			Element selectionsElem = document.createElement("Selections");
			root.appendChild(selectionsElem);
			listsel.forEach(s->{
				Element selectionElem = document.createElement("Selection");
				selectionElem.setAttribute("name", s.getName()+"");
				int act=(s.isActive())?1:0;
				selectionElem.setAttribute("active", act+"");
				s.selection_.forEach(rect->{
					Element rectElem = document.createElement("Rectangle");
					rectElem.setAttribute("startx", rect.getStartX()+"");
					rectElem.setAttribute("starty", rect.getStartY()+"");
					rectElem.setAttribute("width", rect.getWidth()+"");
					rectElem.setAttribute("height", rect.getHeight()+"");
					selectionElem.appendChild(rectElem);
					
				});	
				selectionsElem.appendChild(selectionElem);	
			});
			
			
			
			// operation element
			Element operationsElem = document.createElement("Operations");
			root.appendChild(operationsElem);
			
			listop.stream().filter(o->{return o instanceof CompositeOperation;})
					.map(oper->(CompositeOperation)oper)
					.forEach(operation->{
						Path opPath=Paths.get(path.toString()+"\\funs\\"+operation.getName()+".fun");					
						this.SaveFun(opPath,operation);
						Element operationElem = document.createElement("CompositeOperation");
						operationElem.setAttribute("name", operation.getName());
						operationElem.setTextContent("funs\\"+operation.getName()+".fun");
						operationsElem.appendChild(operationElem);
					});

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // ova linija uklanja standalone=no
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(path.toString()+"\\"+projname+".xml"));

			transformer.transform(domSource, streamResult);

		} catch (ParserConfigurationException | TransformerException e) {
			return false;
		}
		return true;
	}
	
	public Image ReadImage(Path path, Program program) {
		//path is absolute
		File projfile= new File(path.toString());
		String filename = path.getFileName().toString();
		String dirPath=path.getParent().toString();
		Image image=new Image(program);
		try {
			//sirina i visina slike ce se namesiti sami dok se budu lejeri dodavali!		
			ArrayList<Operation> listop = new ArrayList<Operation>();
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document;
			document = documentBuilder.parse(projfile);
			document.getDocumentElement().normalize();
			
			Node imageNode = document.getDocumentElement();//root
			Element imageElem = null;
			if (imageNode.getNodeType() == Node.ELEMENT_NODE)
				imageElem = (Element)imageNode ;
			else
				return null;
			
			
			/*******************LAYERS BEGIN***********************************/
			Node layersNode=imageElem.getElementsByTagName("Layers").item(0);
			Element layersElem = null;
			if (layersNode.getNodeType() == Node.ELEMENT_NODE)
				layersElem = (Element)layersNode ;
			else
				return null;
			NodeList layerList = document.getElementsByTagName("Layer");
			
			for(int counter=0;counter<layerList.getLength();counter++) {
				Node layerNode=layerList.item(counter);
				Element layerElem = null;
				if (layerNode.getNodeType() == Node.ELEMENT_NODE)
					layerElem = (Element)layerNode ;
				else
					return null;
				
				String layerPath=dirPath+"\\"+layerElem.getTextContent();
				int opacity, active, visible;
				opacity=Integer.parseInt(layerElem.getAttribute("opacity"));
				active=Integer.parseInt(layerElem.getAttribute("active"));
				visible=Integer.parseInt(layerElem.getAttribute("visible"));
				Layer l=image.addLayer(layerPath);
				l.setOpacity((short)opacity);
				l.setVisible(visible==1);
				l.setActive(active==1);	
			}
			/*******************LAYERS END***********************************/
			
			
			/*******************SELECTIONS BEGIN***********************************/
			Node selectionsNode=imageElem.getElementsByTagName("Selections").item(0);
			Element selectionsElem = null;
			if (selectionsNode.getNodeType() == Node.ELEMENT_NODE)
				selectionsElem = (Element)selectionsNode ;
			else
				return null;
			NodeList selectionList = document.getElementsByTagName("Selection");
			
			for(int counter=0;counter<selectionList.getLength();counter++) {
				Node selectionNode=selectionList.item(counter);
				Element selectionElem = null;
				if (selectionNode.getNodeType() == Node.ELEMENT_NODE)
					selectionElem = (Element)selectionNode ;
				else
					return null;
				
				//selekcija u okviru sebe ima listu pravougaonika
				String selectionName=selectionElem.getAttribute("name");
				boolean selectionStatus=Integer.parseInt(selectionElem.getAttribute("active"))==1;
				NodeList rectangleList = document.getElementsByTagName("Rectangle");
				ArrayList<Rectangle> rectangles=new ArrayList<Rectangle>();
				for(int i=0;i<rectangleList.getLength();i++) {
					Node rectangleNode=rectangleList.item(i);
					Element rectangleElem = null;
					if (rectangleNode.getNodeType() == Node.ELEMENT_NODE)
						rectangleElem = (Element)rectangleNode ;
					else
						return null;
					int startx,starty,width,height;
					startx=Integer.parseInt(rectangleElem.getAttribute("startx"));
					starty=Integer.parseInt(rectangleElem.getAttribute("starty"));
					width=Integer.parseInt(rectangleElem.getAttribute("width"));
					height=Integer.parseInt(rectangleElem.getAttribute("height"));
					rectangles.add(new Rectangle(startx, starty, width, height));					
				}
				Selection addedSelection=image.addSelection(selectionName, rectangles);
				addedSelection.setActive(selectionStatus);
			}
			/*******************SELECTIONS END***********************************/
			
			/*******************OPERATIONS BEGIN***********************************/
			Node operationsNode=imageElem.getElementsByTagName("Operations").item(0);
			Element operationsElem = null;
			if (operationsNode.getNodeType() == Node.ELEMENT_NODE)
				operationsElem = (Element)operationsNode ;
			else
				return null;
			NodeList operationsList = document.getElementsByTagName("CompositeOperation");
			
			
			
			for(int counter=0;counter<operationsList.getLength();counter++) {
				Node operationNode=operationsList.item(counter);
				Element operationElem = null;
				if (operationNode.getNodeType() == Node.ELEMENT_NODE)
					operationElem = (Element)operationNode ;
				else
					return null;
				String operationName=operationElem.getAttribute("name");
				
				String operationPath=dirPath+"\\"+operationElem.getTextContent();
				ArrayList<String> listOfBasic=this.LoadFun(Paths.get(operationPath), operationName);
				image.createCompositeOperation(operationName, listOfBasic);
			}
			/*******************OPERATIONS END***********************************/
			
			return image;
			
		} catch (SAXException | IOException | ParserConfigurationException | SelectionException e) {
			return null;
		}
	}

}