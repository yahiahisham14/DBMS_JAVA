
import static org.junit.Assert.*;

import org.junit.Test;


public class ParserTest {

	@Test
	public void test() {
		Parser object = new Parser();
		Boolean test1= object.splitAll("CREATE DATABASE menagerie");
	//	Boolean test2= object.splitAll("    USE menagerie");
		Boolean test2 =object.splitAll("create table Persons (FirstName VARCHAR, LastName Varchar, Age varchar)");
		Boolean test3= object.splitAll("INSERT INTO Persons (FirstName, LastName, Age) VALUES ('Peter', 'Griffin',35)");
		Boolean test4= object.splitAll("SELECT FirstName,column_name2 FROM Persons WHERE FirstName = omar");
		Boolean test5= object.splitAll("DELETE FROM Persons WHERE LastName='Griffin'");
		Boolean test6= object.splitAll("UPDATE Persons SET  Age=36,userName = omar WHERE FirstName = 'Peter'");
		assertEquals(test1,true);
		assertEquals(test2,true);
		assertEquals(test3,true);
		assertEquals(test4,false);
		assertEquals(test5,true);
		assertEquals(test6,true);
		
		
		Boolean t1= object.splitAll("CREATE DATABASE menagerie WHERE");
		Boolean t2= object.splitAll("USE menagerie ,");
		Boolean t3= object.splitAll("INSERT INTO Persons FirstName, LastName, Age) VALUES ('Peter', 'Griffin',35)");
		Boolean t4= object.splitAll("SELECTs column_name1,column_name2 FROM table_name WHERE firstname = omar");
		Boolean t5= object.splitAll("DELETE FROM Persons WHERE LastName>'Griffin';");
		Boolean t6= object.splitAll("UPDATE Persons  eSET Age=36 WHERE FirstName = 'Peter'");
		assertEquals(t1,false);
		assertEquals(t2,false);
		assertEquals(t3,false);
		assertEquals(t4,false);
		assertEquals(t5,false);
		assertEquals(t6,false);
		
		
//		Boolean a1= object.splitAll("CREATE habala");
//		Boolean a2= object.splitAll("USE menageri??");
//		Boolean a3= object.splitAll("INSERT INTO Persons FirstName, LastName, Age) VALUES ('Peter', 'Griffin',35,45)");
//		Boolean a4= object.splitAll("SELECT column_name1,column_name2 FROM table_name WHERE firstname => omar");
//		Boolean a5= object.splitAll("DELETE FROM Persons WHERE LastName='Griffin");
//		Boolean a6= object.splitAll("UPDATE Persons SET Age>36 WHERE FirstName = 'Peter'");
//		assertEquals(a1,false);
//		assertEquals(a2,false);
//		assertEquals(a3,false);
//		assertEquals(a4,false);
//		assertEquals(a5,false);
//		assertEquals(a6,false);
//		
//		String b1 = object.pureString("'string';");
//		assertEquals(b1 ,"string");
//		
//		boolean b2 =object.isValidCondition("username => 'omar'");
//		assertEquals(b2,false);
//		
//		boolean b3 =object.isValidCondition("username = 'omar'");
//		assertEquals(b3,true);
//		
//		boolean b4 =object.validName("omar??");
//		assertEquals(b4,false);
//		
//		boolean b5 =object.validName("omar_mahmoud");
//		assertEquals(b5,true);
		
	}

}
