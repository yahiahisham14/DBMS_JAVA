import java.util.ArrayList;
import java.util.StringTokenizer;

public class Parser {

	DBMS obj = new DBMS_Class();

	public boolean splitAll(String query) {
		boolean check = false;
		String w1 = "", w2 = "";
		StringTokenizer st = new StringTokenizer(query);
		String[] type = new String[st.countTokens()];
		int c = 0;
		while (st.hasMoreTokens()) {
			type[c] = st.nextToken();
			c++;
		}
		w1 = type[0];
		w2 = type[1];
		if (w1.equalsIgnoreCase("SELECT")) {
			check = checkSelect(type);
		} else if (w1.equalsIgnoreCase("INSERT")) {
			check = checkInsert(query);
		} else if (w1.equalsIgnoreCase("UPDATE")) {
			check = checkUpdate(query);
		} else if (w1.equalsIgnoreCase("DELETE")) {
			check = checkDelete(query);
		} else if ((w1.equalsIgnoreCase("CREATE"))
				&& w2.equalsIgnoreCase("TABLE")) {
			check = checkCreateTB(query);
		} else if ((w1.equalsIgnoreCase("CREATE"))
				&& w2.equalsIgnoreCase("DATABASE")) {
			check = checkCreateDB(query);
		} else if (w1.equalsIgnoreCase("USE")) {
			check = checkUse(query);
		}
		return check;

	}

	public boolean checkCreateDB(String query) {
		StringTokenizer st = new StringTokenizer(query);
		String[] split = new String[st.countTokens()];
		int c = 0;
		while (st.hasMoreTokens()) {
			split[c] = st.nextToken();
			c++;
		}

		// checking if the length of the sentence;
		if (split.length > 3 ) {
			return false;
		}
		String name =split[2];

		// checking if the name is valid
		if (!validName(name)) {
			return false;
		}

		// finished check.
		// send to method create DB (createDB(name))
		if (!obj.createDatabase(name))
			return false;
		return true;
	}// end method.

	public boolean checkDelete(String query) {
		// checking the semiColon.
		// checking the basic words.
		String tableName = "";
		String condition = "";
		String arrayCondition[];
		try {
			StringTokenizer st = new StringTokenizer(query);
			String[] split = new String[st.countTokens()];
			int c = 0;
			while (st.hasMoreTokens()) {
				split[c] = st.nextToken();
				c++;
			}
			if(split.length<4){
				return false;
			}
			if (split[1].equals("*")) {
				if(!split[2].equalsIgnoreCase("FROM")){
					return false;
				}
				tableName = split[3];
				if(!validName(tableName)){
					return false;
				}
				arrayCondition= new String[3];
				arrayCondition[0]="*";
				arrayCondition[1]="*";
				arrayCondition[2]="*";
			} else {
				if (!split[0].equalsIgnoreCase("DELETE")
						|| !split[1].equalsIgnoreCase("FROM")
						|| !split[3].equalsIgnoreCase("WHERE")) {
					return false;
				}
				tableName = pureString(split[2]);
				if (!validName(tableName)) {
					return false;
				}

				for (int i = 4; i < split.length; i++) {
					condition += split[i];

				}

				// checking the number of sings.
				if (!isValidCondition(condition)) {
					return false;
				}
				arrayCondition = divideCondition(condition);
				if (!validName(arrayCondition[0])
						|| !validName(arrayCondition[2])) {
					return false;
				}
			}
		} catch (Exception e) {
			return false;
		}

		if (!obj.deleteFromTable(tableName, arrayCondition))
			return false;
		return true;
	}// end method.

	public boolean checkUse(String query) {
		StringTokenizer st = new StringTokenizer(query);
		String[] split = new String[st.countTokens()];
		int c = 0;
		while (st.hasMoreTokens()) {
			split[c] = st.nextToken();
			c++;
		}

		// checking if the length of the sentence;
		if (split.length > 3 || split.length < 2) {
			System.out.println("Incorrect enrty, Enter query:");
			return false;
		}

		String name = split[2].replace(";", "");

		// checking if the name is valid
		if (!validName(name)) {
			// System.out.println("Incorrect name, Enter query:");
			return false;
		}

		// finished check.
		// send to method use DB (useDB(name))

		if (!obj.useDatabase(name))
			return false;

		System.out.println("Database " + name + " used");

		return true;
	}// end method.

	public boolean checkSelect(String[] query) {
		String tableName = "";
		String[] conditionArray;
		String[] columnsArray;
		try {
			int whereIndex = 0, fromIndex = 0;
			boolean from = false, where = false;
			for (int i = 1; i < query.length; i++) {
				if (from && where)
					break;
				if (query[i].equalsIgnoreCase("FROM")) {
					from = true;
					fromIndex = i;
				} else if (query[i].equalsIgnoreCase("WHERE")) {
					where = true;
					whereIndex = i;
				}
			}

			if (!from || !where)
				return false;
			tableName = query[(fromIndex + whereIndex) / 2];
			if (query[1].equalsIgnoreCase(pureString("*"))) {
				columnsArray = new String[1];
				columnsArray[0] = "*";

			} else {
				int i = 1;
				String columns = "";
				while (i < fromIndex) {
					columns += query[i];
					i++;
				}
				columnsArray = columns.split(",");
			}
			int j = whereIndex + 1;
			String condition = "";
			while (j < query.length) {
				condition += query[j];
				j++;
			}
			conditionArray = new String[3];
			String sign = "";
			int signIndex;
			try {
				signIndex = condition.indexOf('=');
				conditionArray[1] = "=";
			} catch (Exception e1) {
				try {
					signIndex = condition.indexOf('>');
					conditionArray[1] = ">";
				} catch (Exception e2) {
					try {
						signIndex = condition.indexOf('<');
						conditionArray[1] = "<";
					} catch (Exception e3) {
						return false;
					}
				}
			}
			conditionArray[0] = pureString(condition.substring(0, signIndex));
			conditionArray[2] = pureString(condition.substring(signIndex + 1));
			for (int k = 0; k < columnsArray.length; k++) {
				columnsArray[k] = pureString(columnsArray[k]);
			}
		} catch (Exception e) {
			return false;
		}
		// System.out.println(tableName + " " + columnsArray[1] + " " +
		// conditionArray[0]+conditionArray[1] + conditionArray[2]);

		ArrayList<String>[] adjList = obj.selectFromTable(tableName,
				columnsArray, conditionArray);
		if (adjList == null)
			return false;

		for (int i = 0; i < adjList[0].size(); i++) {
			for (int j = 0; j < adjList.length; j++) {
				System.out.print(adjList[j].get(i) + " ");
			}// end for j.
			System.out.println();
		}// end for i.
		return true;
	}

	public boolean checkInsert(String query) {
		String tableName = "";
		String[] columns = null;
		String[] values = null;
		try {

			if (!query.split(" ")[1].equalsIgnoreCase("INTO"))
				return false;
			int i = 11;
			String temp = "";
			while (i < query.length()) {
				if (query.charAt(i) != '(') {
					temp += query.charAt(i);
					i++;
				} else {
					tableName = pureString(temp);
					i++;
					break;
				}
			}
			temp = "";
			while (i < query.length()) {
				if (query.charAt(i) != ')') {
					temp += query.charAt(i);
					i++;
				} else {
					columns = temp.split(",");
					i++;
					break;
				}
			}
			temp = "";
			while (i < query.length()) {
				if (query.charAt(i) != '(') {
					temp += query.charAt(i);
					i++;
				} else {
					if (!pureString(temp).equalsIgnoreCase("VALUES")) {
						return false;
					}
					i++;
					break;
				}
			}
			temp = "";
			while (i < query.length()) {
				if (query.charAt(i) != ')') {
					temp += query.charAt(i);
					i++;
				} else {
					values = temp.split(",");
					i++;
					break;
				}
			}
			if (values.length != columns.length)
				return false;
			for (int j = 0; j < values.length; j++) {
				values[j] = pureString(values[j]);
				columns[j] = pureString(columns[j]);
			}
			for (int j = 0; j < columns.length; j++) {
				for (int j2 = j + 1; j2 < columns.length; j2++) {
					if (columns[j].equalsIgnoreCase(columns[j2]))
						return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		// System.out.println(tableName + " " + columns[0] + " " + values[0]);

		if (!obj.insertIntoTable(tableName, columns, values))
			return false;

		return true;

	}

	public boolean checkUpdate(String query) {
		String columns[];
		String values[];
		StringTokenizer st = new StringTokenizer(query);
		String[] split = new String[st.countTokens()];
		int c = 0;
		while (st.hasMoreTokens()) {
			split[c] = st.nextToken();
			c++;
		}
		// checking the basic words
		String tableName = "";
		tableName = split[1];
		if (!validName(tableName)) {
			return false;
		}
		if (!split[2].equalsIgnoreCase("SET")) {
			return false;
		}
		if (!validName(split[1])) {
			return false;
		}
		// getting condition_1.
		ArrayList<String[]> list = new ArrayList<String[]>();
		String setLine = "";
		int whereIndex = 0;
		for (int i = 3; i < split.length; i++) {
			if (split[i].equalsIgnoreCase("WHERE")) {
				whereIndex = i;
				break;
			}

			setLine += split[i];
		}// end for(i).

		String[] tempSet = setLine.split(",");
		for (int i = 0; i < tempSet.length; i++) {
			if (!isValidCondition(tempSet[i])) {
				return false;
			}
			String[] arraySet = divideCondition(tempSet[i]);
			if (!arraySet[1].equals("=")) {
				return false;
			}
			// System.out.println(arraySet[0] + "--" + arraySet[1] + "--"
			// + arraySet[2]);

			list.add(arraySet);
			setLine = "";
		}// for(i).
		columns = new String[list.size()];
		values = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			columns[i] = list.get(i)[0];
			values[i] = list.get(i)[2];
		}// end for(i).

		// getting condition.
		String conditionLine = "";
		for (int i = whereIndex + 1; i < split.length; i++) {
			conditionLine += split[i];
		}

		// checking conditionline_1 and conditionLine_2.
		if (!isValidCondition(conditionLine)) {
			return false;
		}

		String[] arrayCondition = divideCondition(conditionLine);

		for (int i = 0; i < values.length; i++) {
			if (!validName(values[i])) {
				return false;
			}
		}
		if (!validName(arrayCondition[0]) || !validName(arrayCondition[2])) {
			return false;
		}
		if (!obj.updateTable(tableName, columns, values, arrayCondition))
			return false;

		return true;

	}// end method.

	public boolean checkCreateTB(String query) {

		if (!pureString(query.split(" ")[1]).equalsIgnoreCase("TABLE"))
			return false;
		int i = query.indexOf('(');
		int j = query.indexOf(')');
		String temp = pureString(query.split(" ")[2]);
		String tableName = "";
		if (temp.contains("(")) {
			int k = temp.indexOf('(');
			tableName = temp.substring(0, k);
		} else
			tableName = temp;
		String content = query.substring(i + 1, j);
		String[] attributes = content.split(",");
		String[] types = new String[attributes.length];
		try {
			for (int k = 0; k < attributes.length; k++) {
				StringTokenizer st = new StringTokenizer(attributes[k]);
				attributes[k] = pureString(st.nextToken());
				types[k] = pureString(st.nextToken());
			}
			for (int k = 0; k < types.length; k++) {
				if (!types[k].equalsIgnoreCase("INT")
						&& !types[k].equalsIgnoreCase("BIGINT")
						&& !types[k].equalsIgnoreCase("VARCHAR"))
					return false;
				else if (types[k].equalsIgnoreCase("BIGINT"))
					types[k] = "double";
				else if (types[k].equalsIgnoreCase("VARCHAR"))
					types[k] = "String";
				else if (types[k].equalsIgnoreCase("INT"))
					types[k] = "int";
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		ArrayList<String> a = new ArrayList<String>();
		ArrayList<String> b = new ArrayList<String>();

		for (int m = 0; m < types.length; m++) {
			a.add(types[m]);
			b.add(attributes[m]);

		}// end for m.

		if (!obj.createTable(tableName, b, a))
			return false;

		return true;

	}

	public boolean isValidCondition(String condition) {
		int numOfSigns = 0;
		int signIndex = 0;
		for (int i = 0; i < condition.length(); i++) {
			if (condition.charAt(i) == '<' || condition.charAt(i) == '>'
					|| condition.charAt(i) == '=') {
				numOfSigns++;
				signIndex = i;
			}
		}
		if (numOfSigns != 1) {
			return false;
		}

		if (signIndex == 0 || signIndex == condition.length()) {
			return false;
		}

		return true;
	}

	public String[] divideCondition(String conditionLine) {
		String sign = "";
		for (int i = 0; i < conditionLine.length(); i++) {
			if (conditionLine.charAt(i) == '<'
					|| conditionLine.charAt(i) == '>'
					|| conditionLine.charAt(i) == '=') {
				sign += conditionLine.charAt(i);
			}
		}
		conditionLine = pureString(conditionLine);

		String arrayCondition[] = new String[3];
		String s[] = conditionLine.split(sign);
		arrayCondition[0] = s[0];
		arrayCondition[1] = sign;
		arrayCondition[2] = s[1];

		return arrayCondition;
	}

	public boolean validName(String name) {
		String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-";
		for (int i = 0; i < name.length(); i++) {
			boolean check = false;
			for (int j = 0; j < valid.length(); j++) {
				if (name.charAt(i) == valid.charAt(j)) {
					check = true;
				}// end if check.
			}// end if (j).
			if (!check) {
				return false;
			}
		}// end for(i).
		return true;
	}// end method.

	public String pureString(String s) {
		String temp = "";
		temp = s.replace(";", "");
		temp = temp.replace(",", "");
		temp = temp.replace(" ", "");
		temp = temp.replace(".", "");
		temp = temp.replace("`", "");
		temp = temp.replace("\'", "");
		temp = temp.replace("\"", "");
		temp = temp.replace("[", "");
		temp = temp.replace("]", "");
		temp = temp.replace("\n", "");

		return temp;
	}
}