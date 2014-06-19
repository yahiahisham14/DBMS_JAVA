import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner in  = new Scanner(System.in);
		Parser Q = new Parser();	
		boolean database = false;
		System.out.println("please choose one of these two options: ");
		System.out.println("1- Create Database");
		System.out.println("2- Use Database");
		while(true)
		{	
			System.out.println("please enter the query: ");
			String query = in.nextLine();
			boolean catchy = false; String w1 = "",w2 = "",w3 = "";
			if(!database)
			{
				StringTokenizer st =  new StringTokenizer(query);
				try{
					w1 = st.nextToken(); w2 = st.nextToken(); w3 = st.nextToken();
				}
				catch(Exception e){catchy = true;}
					while(catchy || ((!w1.equalsIgnoreCase("CREATE") && !w2.equalsIgnoreCase("DATABASE")) 
						&& (!w1.equalsIgnoreCase("USE") && !w2.equalsIgnoreCase("DATABASE"))))
					{
						catchy = false;
						System.out.println("wrong entry, Please enter the query: ");
						query = in.nextLine();
						st =  new StringTokenizer(query);
						try{
							w1 = st.nextToken(); w2 = st.nextToken(); w3 = st.nextToken();
						}
						catch(Exception e){catchy = true;}
					}
					database = true;
			}
			while(!Q.splitAll(query))
			{
				System.out.println("please enter the query: ");
				query = in.nextLine();
			}
		}
	}
	
}
