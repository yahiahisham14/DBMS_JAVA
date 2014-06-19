import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.stream.StreamResult;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class DBMS_Class implements DBMS {
	private static final Exception Exception = null;
	private static String path="C:\\Users\\technocity\\Desktop\\Server\\";
	private static String databaseName = "";
	
	public boolean useDatabase(String dbName){
		String dir = path + dbName + ".xml";
		
		try {
			FileReader fr = new FileReader(dir);
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			System.out.println("Database Not Found.");
			return false;
		}
		return true;
	}//end method.
	
	@Override
	public boolean createDatabase(String dbName) {
		
		try{
			 
			databaseName = dbName;
			String directory = path + databaseName + ".xml";
			 
			Element tables = new Element("tables");
			Document doc = new Document();
			doc.setRootElement(tables);
		
			XMLOutputter xmlOut=new XMLOutputter();
			 
			xmlOut.setFormat( Format.getPrettyFormat() );
			xmlOut.output(doc, new FileWriter( directory ));
			
			return true;
		}catch(IOException e){
			//System.out.println(e.getMessage());
			System.out.println("Error in file!!");
			return false;
		}
		
	}//end method createDB.

	@Override
	public boolean createTable( String tableName, ArrayList<String> values , ArrayList<String> types ) {
		// TODO Auto-generated method stub
		
		if( updateDatabaseFile( tableName ,values,types) ){
			 try {
				 	String directory = path + tableName + "_" + databaseName +".xml";
					
					Element table = new Element( tableName );
					Document doc = new Document();
					doc.setRootElement(table);
					
					XMLOutputter xmlOutput = new XMLOutputter();
					
					// display nice nice
					xmlOutput.setFormat(Format.getPrettyFormat());
					xmlOutput.output(doc, new FileWriter( directory ));
					
					return true;
				  } catch (IOException io) {
//					System.out.println(io.getMessage());
					  System.out.println("File ERROR!!");
					return false;
				  }

		}//end if .
		else{
			System.out.println("table exists!!");
			return false;
		}
				 	
		
	}//end method createTable.

	@Override
	public boolean insertIntoTable( String tableName , String[] corresValue ,String[] value ) {
		// TODO Auto-generated method stub
		// corresValue : firstName ,age ,.....
		// value : ahmed , 20 ,.....
		
		if( checkTableExist(tableName) ){
			
			try {
				
				HashSet<String> set = new HashSet<String>();
				for( int i = 0 ;i < corresValue.length ;i++){
					set.add( corresValue[i] );
					String t = getType( tableName ,corresValue[i] );
					if( t == null ){
						System.out.println("The attribute " + corresValue[i] + "doesnot exist.");
						return false;
						//throw(Exception);
					}else{
						if( t.equals("int") ){
								int x = Integer.parseInt( value[i] );
						}else if( t.equals( "double" ) ){
								double x = Double.parseDouble( value[i] );
						}//end else if.
					}//end else.
				
				}//end for i.
				
				// 2ND FILE:--------------------------
				 String dir = path + tableName +"_" + databaseName + ".xml";
					
				 SAXBuilder builder = new SAXBuilder();
				File xmlFile = new File( dir );
				
				Document doc = (Document) builder.build(xmlFile);
				Element rootNode = doc.getRootElement();
				
					Element row = new Element("row");
					for( int i = 0 ;i < corresValue.length ;i++){
						row.addContent(new Element( corresValue[i] ).setText( value[i] ));
					}//end for i.
					List list = getList(tableName);
					for( int i = 0 ;i < list.size() ;i++){
						Element node = (Element) list.get(i);
						if( !set.contains( node.getText() ) ){
							row.addContent(new Element( node.getText() +"" ).setText( "" ));							
						}
					}//end for i.
					
					rootNode.addContent(row);
					
					XMLOutputter xmlOutput = new XMLOutputter();
					 
					// display nice nice
					xmlOutput.setFormat(Format.getPrettyFormat());
					xmlOutput.output(doc, new FileWriter(dir) );
					return true;
			  } catch (IOException io) {
//				io.printStackTrace();
				  System.out.println("File Error.");
				return false;
			  } catch (JDOMException e) {
//				e.printStackTrace();
				System.out.println("JDOM Exception.");
				return false;
			  }catch(Exception e){
				  System.out.println("You have to stick to the type.");
				  return false;
			  }
		}//end if
		else{
			System.out.println("table doesnot exist ");
			return false;
		}
		
	}//end method insertIntoTable.

	@Override
	public ArrayList<String>[] selectFromTable( String tableName ,String[] corresValue ,String[] condition ) {
		// TODO Auto-generated method stub
		
		if( checkTableExist(tableName) ){
			
			try {
				
				String dir = path + tableName +"_" + databaseName + ".xml";
				SAXBuilder builder = new SAXBuilder();
				File xmlFile = new File( dir );
				
				Document doc = (Document) builder.build(xmlFile);
				Element rootNode = doc.getRootElement();
				
				if( corresValue[0].equals( "*" ) ){
					List test = getList(tableName);
					corresValue = new String[ test.size() ];
					
					for( int i = 0 ;i < test.size() ;i++){
						Element node = (Element) test.get(i);
						corresValue[i] = node.getText();
					}//end for i
					
				}//end if *.
				String t = getType( tableName ,condition[0] );
				if( t.equals("String") && !condition[1].equals("=") ){
					System.out.println("Not a valid Condition.");
//					throw( Exception );
				}else if( t.equals("int") ){
					int x = Integer.parseInt( condition[2] );
				}else if (t.equals("double")){
					double x = Double.parseDouble( condition[2] );
				}//end else if.
					
				List list = rootNode.getChildren( "row" );
				
				ArrayList<String>[] wanted;
				wanted = new ArrayList[ corresValue.length ];
				for (int i = 0; i < wanted.length; i++) {
					wanted[i] = new ArrayList<String>();
					wanted[i].add( corresValue[i] );
				}
				
				//CHECK CORRES VALUE:
				boolean check = true;
				for( int i = 0 ;i < corresValue.length ;i++){
					if( !list.contains( corresValue[i] ) ){
						check = false;
					}
				}//end for i.
				
				if( check == false ){
					return null;
				}
				
				for( int i = 0 ;i < list.size() ;i++){
					Element node = (Element) list.get(i);
					String current = node.getChildText( condition[0] );
					if( checkCondition( condition ,t,current ) ){
						for( int j = 0 ;j < corresValue.length ;j++){
							wanted[j].add( node.getChildText(corresValue[j]) );
							
						}//end for j.
								
					}//end if
					
				}//end for i.
				
				return wanted;
			 } catch (IOException io) {
//				io.printStackTrace();
				 System.out.println("File exception.");
				return null;
			  } catch (JDOMException e) {
//				e.printStackTrace();
				System.out.println("JDOM Exception.");
				return null;
			  }catch( Exception e){
				System.out.println( "You have to stick to the type." ); 
				return null;
			  }
				
			
		}//end if check exist.
		else{
			System.out.println("Table doesnot Exist.");
			return null;
		}
	}//end method select from table.

	@Override
	public boolean deleteFromTable( String tableName ,String[] condition ) {
		// TODO Auto-generated method stub
		// yenf3 delete table???
		
		if( checkTableExist(tableName) ){
			
			try {
				
				String dir = path + tableName +"_" + databaseName + ".xml";
				SAXBuilder builder = new SAXBuilder();
				File xmlFile = new File( dir );
				
				Document doc = (Document) builder.build(xmlFile);
				Element rootNode = doc.getRootElement();
				
				if( condition[0].equals("*") ){
					
					rootNode.removeContent();
					
				}else{
					String t = getType( tableName,condition[0] );
					
					if( t.equals("String") && !condition[1].equals("=") ){
						System.out.println("Not a valid condition.");
//						throw( Exception );
						return false;
					}else if( t.equals("int") ){
						int x = Integer.parseInt( condition[2] );
					}else if (t.equals("double")){
						double x = Double.parseDouble( condition[2] );
					}//end else if.
					
					List list = rootNode.getChildren();
					for( int i = 0 ;i < list.size() ;i++){
						Element node = (Element) list.get(i);
						String current = node.getChildText( condition[0] );
						if( checkCondition( condition ,t ,current ) ){
							rootNode.removeContent( node );
							// VERY IMPORTANT:
							i--;
						}
						
					}//end for i.
					
				}//end if.
				
				
				XMLOutputter xmlOutput = new XMLOutputter();
				// display nice nice
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, new FileWriter( dir ));
				
				return true;
			  } catch (IOException io) {
//				io.printStackTrace();
				  System.out.println("File Error.");
				return false;
			  } catch (JDOMException e) {
//				e.printStackTrace();
				  System.out.println("JDOM Exception.");
				return false;
			  }
				catch(Exception e){
					System.out.println("here");
				  return false;
			  }
		}//end if
		else{
			System.out.println("table doesnot exist");
			return false;
		}
		
		
	}//end method delete from table.

	@Override
	public boolean updateTable( String tableName , String[] corresValue ,String[] value ,String[] condition ) {
		// TODO Auto-generated method stub
		
		if( checkTableExist(tableName) ){
			
			try {
				 String dir = path + tableName +"_" + databaseName + ".xml";
				
				 SAXBuilder builder = new SAXBuilder();
				File xmlFile = new File( dir );
				
				Document doc = (Document) builder.build(xmlFile);
				Element rootNode = doc.getRootElement();
				
				String t = getType( tableName,condition[0] );
				
				if( t.equals("String") && !condition[1].equals("=") ){
					throw( Exception );
				}else if( t.equals("int") ){
					int x = Integer.parseInt( condition[2] );
				}else if (t.equals("double")){
					double x = Double.parseDouble( condition[2] );
				}//end else if.
								
				List list = rootNode.getChildren();
				for( int i = 0 ;i < list.size() ;i++){
					Element node = (Element) list.get(i);
					String current = node.getChildText( condition[0] );
					if( checkCondition( condition ,t ,current ) ){
						for( int j = 0 ; j < corresValue.length ;j++){
							node.getChild(corresValue[j]).setText( value[j] );
						}//end for j.
					}//end if.
				}//end for i.
				
				XMLOutputter xmlOutput = new XMLOutputter();
				// display nice nice
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, new FileWriter( dir ));
				
				return true;
			  } catch (IOException io) {
//				io.printStackTrace();
				  System.out.println("File Error.");
				return false;
			  } catch (JDOMException e) {
//				e.printStackTrace();
				  System.out.println("JDOM Exception.");
				return false;
			  }catch(Exception e){
				  System.out.println("here");
				  return false;
			  }
		}//end if
		else{
			System.out.println("Table doesnot exist");
			return false;
		}
		
		
	}//end method update table.
	
	public boolean checkTableExist( String tableName ){
		
		try {
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File( path + databaseName +".xml" );
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			
			List list =  rootNode.getChildren();
			for( int i = 0 ;i < list.size() ;i++){
				Element node = (Element) list.get(i);
				String typeShape = node.getAttributeValue("name");
				if( typeShape.equals(tableName) ){
					return true;
				}
			}//end for i.
			
			return false;
			} catch (IOException io) {
			io.printStackTrace();
			return true;
		  } catch (JDOMException e) {
			e.printStackTrace();
			return true;
		  }
		

		
	}//end method checkTableExist.
	
	public boolean updateDatabaseFile(String tableName , ArrayList<String> values, ArrayList<String> types){
		//saving the name of the table in db file.
				try {
					
					if( checkTableExist( tableName ) ){
						System.out.println("Table already Exists!!");
						return false;
					}
					
					// update staff id attribute
					SAXBuilder builder = new SAXBuilder();
					
					File xmlFile = new File( path + databaseName +".xml" );
					
					Document doc = (Document) builder.build(xmlFile);
					Element rootNode = doc.getRootElement();
					Element tableN =  new Element("table").setAttribute( "name",tableName );
					rootNode.addContent(tableN );
					
					
					Element row = new Element("rowSpecial").setAttribute("num",values.size() +"");
					for( int i = 0 ; i < types.size() ;i++){
						row.addContent(new Element( values.get(i) ).setText( values.get(i) ) .setAttribute("type" ,types.get(i)));					
					}//end for i.
					tableN.addContent(row);
					
					XMLOutputter xmlOutput = new XMLOutputter();
					
					// display nice nice
					xmlOutput.setFormat(Format.getPrettyFormat());
					xmlOutput.output(doc, new FileWriter( path + databaseName +".xml" ));
					
//					System.out.println("File updated!");
					return true;
					} catch (IOException io) {
//					io.printStackTrace();
					System.out.println("File not found!!");
					return false;
				  } catch (JDOMException e) {
//					e.printStackTrace();
					 System.out.println( "JDOM Exception!!");
					return false;
				  }
				
	}//end method update DB File.
	
	//el condition array gaylay metzabata.
	public boolean checkCondition( String[] condition ,String type ,String current){
		
		try{
			
			if( type.equals("int") ){
				int x = Integer.parseInt( current );
				int y = Integer.parseInt( condition[2] );
				
				if( condition[1].equals(">") && x > y ){
					return true;
				}
				else if( condition[1].equals("<") && x < y ){
					return true;
				}
				else if( condition[1].equals("=") && x == y ){
					return true;
				}
			}//end if.
			else if( type.equals("double") ){
				double x = Double.parseDouble( current );
				double y = Double.parseDouble( condition[2] );
				
				if( condition[1].equals(">") && x > y ){
					return true;
				}
				else if( condition[1].equals("<") && x < y ){
					return true;
				}
				else if( condition[1].equals("=") && x == y ){
					return true;
				}
			}//end else if
			else if( type.equals("String") ){
				if( current.equals(condition[2]) ){
					return true;
				}
			}//end else if.
			
			return false;
		}catch( Exception e ){
			System.out.println("You have to Stick to the type.");
			return false;
		}
		
	}//end method checkCondition.
	
	public String getType( String tableName ,String value ){
		
		try {
			
			String dir = path + databaseName + ".xml";
			
			 SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File( dir );
			
			Document doc = (Document) builder.build(xmlFile);
			
			Element rootNode = doc.getRootElement();
			Element tableN = null;
			
			List list =  rootNode.getChildren();
			for( int i = 0 ;i < list.size() ;i++){
				Element node = (Element) list.get(i);
				String typeShape = node.getAttributeValue("name");
				if( typeShape.equals(tableName) ){
					tableN = node.clone();
				}
			}//end for i.
			
			
			Element rowSpecial = tableN.getChild("rowSpecial");
			
			String t = rowSpecial.getChild( value ).getAttributeValue("type");
			
			return t;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return null;
		}catch(Exception e){
			System.out.println("here in get type!!");
			return null;
		}
		
	}//end method getType.
	
	public List getList(String tableName){
		try {
			String dir = path + databaseName + ".xml";
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File( dir );
			Document doc = (Document) builder.build(xmlFile);
		
			Element rootNode = doc.getRootElement();
			
			Element tableN = null;
			List list =  rootNode.getChildren();
			for( int i = 0 ;i < list.size() ;i++){
				Element node = (Element) list.get(i);
				String typeShape = node.getAttributeValue("name");
				if( typeShape.equals(tableName) ){
					tableN = node.clone();
				}
			}//end for i.
			
				
			Element rowSpecial = tableN.getChild("rowSpecial");
			
			List list2 = rowSpecial.getChildren();
			
			return list2;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}//end method getList.
	
}//end method.
