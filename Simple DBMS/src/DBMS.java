import java.util.ArrayList;

public interface DBMS {
	
	public boolean createDatabase(String dbName);
	public boolean createTable( String tableName ,ArrayList<String> types , ArrayList<String> values );
	public boolean insertIntoTable( String tableName , String[] corresValue ,String[] value );
	public ArrayList<String>[] selectFromTable( String tableName ,String[] corresValue ,String[] condition );
	public boolean deleteFromTable( String tableName ,String[] condition );
	public boolean updateTable( String tableName , String[] corresValue ,String[] values ,String[] condition );
	public boolean useDatabase(String dbName);
	
}//end interface.
