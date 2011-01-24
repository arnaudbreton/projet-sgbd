package regle_association;

import java.util.Observable;
import java.util.Random;

import jdbc.MysqlJDBC;

/**
 * Classe de génération de tables sous forme de tableau disjonctif complet (table binaire)
 * @author Arnaud
 *
 */
public class GenerateurTable extends Observable {

	/**
	 * Génère une table avec un nombre de colonnes et de lignes données
	 * @param tableName Le nom de la table à générer
	 * @param columnsCount Le nombre de colonnes à générer (nommé A*)
	 * @param rowsCount Le nombre de lignes à générer (peuplé aléatoirement de 0 et de 1)
	 * @return Vrai si la table est correctement crée, faux sinon.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public boolean generateTable(String tableName, int columnsCount,
			int rowsCount) throws InstantiationException,
			IllegalAccessException {
		StringBuffer sbCreate, sbInsert;

		try {

			sbCreate = new StringBuffer();

			sbCreate.append("CREATE TABLE " + tableName + "(");
			sbCreate.append("Tid INTEGER PRIMARY KEY AUTO_INCREMENT,");

			for (int cptColumn = 0; cptColumn < columnsCount; cptColumn++) {
				sbCreate.append("A" + cptColumn + " integer");

				if (cptColumn + 1 < columnsCount) {
					sbCreate.append(",");
				}
			}
			sbCreate.append(");");		

			try {
				addLog("Destruction de la table " + tableName + "...");
				MysqlJDBC.getInstance().executeUpdate(
						"DROP TABLE " + tableName + ";");
				addLog("Destruction de la table " + tableName
						+ " réussie.");
			}
			catch(Exception ex) {
				addLog("Table " + tableName + " inexistante.");
			}
			
			addLog("Création de la table " + tableName + " : "
					+ sbCreate.toString());
			MysqlJDBC.getInstance().executeUpdate(sbCreate.toString());
			addLog("Création de la table " + tableName
					+ " réussie.");

			Random rnd = new Random();

			addLog("Peuplement de la table...");
			sbInsert = new StringBuffer();
			for (int cptRow = 0; cptRow < rowsCount; cptRow++) {
				sbInsert.delete(0, sbInsert.length());
				sbInsert.append("INSERT INTO " + tableName + " VALUES(");
				sbInsert.append(cptRow + 1 + ",");
				for (int cptColumn = 0; cptColumn < columnsCount; cptColumn++) {
					sbInsert.append(rnd.nextInt(10000) % 2);

					if (cptColumn + 1 < columnsCount) {
						sbInsert.append(",");
					}

				}

				sbInsert.append(");");
				addLog("Insertion d'une ligne dans la table "
						+ tableName + " : " + sbInsert.toString());
				MysqlJDBC.getInstance().executeUpdate(sbInsert.toString());
				addLog("Insertion d'une ligne dans la table "
						+ tableName + " réussie.");
			}

			addLog("Déconnexion de la base...");
			
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	/**
	 * Ecriture d'un message, diffusé par les observateurs,
	 * 
	 * @param message
	 */
	private void addLog(String message) {
		setChanged();
		notifyObservers(message);
	}
}
