package regle_association;

import java.util.Random;

import jdbc.OracleJDBC;

public class GenerateurTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		generateTable("TestFrequent",3,10);
	}

	public static boolean generateTable(String tableName, int columnsCount,
			int rowsCount) {
		StringBuffer sbCreate,sbInsert;
		
		sbCreate = new StringBuffer();

		sbCreate.append("CREATE TABLE " + tableName + "(");
		sbCreate.append("Tid INTEGER PRIMARY KEY,");

		
		for (int cptColumn = 0; cptColumn < columnsCount; cptColumn++) {
			sbCreate.append("A" + cptColumn + " integer");
			
			if (cptColumn+1 < columnsCount) {
				sbCreate.append(",");
			}
		}
		sbCreate.append(");");
		

	
		OracleJDBC.getInstance().connect();
		OracleJDBC.getInstance().execute(sbCreate.toString());
		
		Random rnd = new Random();
		
		sbInsert = new StringBuffer();
		sbInsert.append("INSERT INTO " + tableName + "(");
		for (int cptRow = 1; cptRow < rowsCount; cptRow++) {
			for (int cptColumn = 0; cptColumn < columnsCount; cptColumn++) {
				sbInsert = new StringBuffer();				
				sbInsert.append(rnd.nextInt(1));
				
				if (cptColumn+1 < columnsCount) {
					sbInsert.append(",");
				}
			}
		}
		
		sbInsert.append(");");
		
		OracleJDBC.getInstance().execute(sbInsert.toString());
		OracleJDBC.getInstance().deconnect();

		return true;
	}

}
