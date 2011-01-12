package regle_association;

import java.util.Random;

import jdbc.MysqlJDBC;

public class GenerateurTable {

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		generateTable(args[0],Integer.parseInt(args[1]), Integer.parseInt(args[2]));
	}

	public static boolean generateTable(String tableName, int columnsCount,
			int rowsCount) throws InstantiationException, IllegalAccessException {
		StringBuffer sbCreate,sbInsert;
		
		sbCreate = new StringBuffer();

		sbCreate.append("CREATE TABLE " + tableName + "(");
		sbCreate.append("Tid INTEGER PRIMARY KEY AUTO_INCREMENT,");

		
		for (int cptColumn = 0; cptColumn < columnsCount; cptColumn++) {
			sbCreate.append("A" + cptColumn + " integer");
			
			if (cptColumn+1 < columnsCount) {
				sbCreate.append(",");
			}
		}
		sbCreate.append(");");		

		try {
			System.out.println("Destruction de la table " + tableName + "...");
			MysqlJDBC.getInstance().executeUpdate("DROP TABLE " + tableName + ";");
			System.out.println("Destruction de la table " + tableName + " réussie.");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	
		System.out.println("Connexion à la base...");
		MysqlJDBC.getInstance().connect();
		System.out.println("Connexion à la base réussie");
		
		System.out.println("Création de la table "+ tableName + " : " + sbCreate.toString());
		MysqlJDBC.getInstance().executeUpdate(sbCreate.toString());
		System.out.println("Création de la table "+ tableName + " réussie.");
		
		Random rnd = new Random();
		
		System.out.println("Peuplement de la table...");
		sbInsert = new StringBuffer();
		for (int cptRow = 1; cptRow < rowsCount; cptRow++) {			
			sbInsert.delete(0, sbInsert.length());
			sbInsert.append("INSERT INTO " + tableName + " VALUES(");
			sbInsert.append(cptRow + ",");
			for (int cptColumn = 0; cptColumn < columnsCount; cptColumn++) {
				sbInsert.append(rnd.nextInt(10000) % 2);
				
				if (cptColumn+1 < columnsCount) {
					sbInsert.append(",");
				}
				
			}
			
			sbInsert.append(");");
			System.out.println("Insertion d'une ligne dans la table "+ tableName + " : " + sbInsert.toString());
			MysqlJDBC.getInstance().executeUpdate(sbInsert.toString());
			System.out.println("Insertion d'une ligne dans la table "+ tableName + " réussie.");
		}	
		
		System.out.println("Déconnexion de la base...");		
		MysqlJDBC.getInstance().deconnect();

		return true;
	}

}
