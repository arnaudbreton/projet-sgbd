package regle_association;

import java.util.Observable;
import java.util.Random;

import jdbc.MysqlJDBC;

/**
 * Classe de g�n�ration de tables sous forme de tableau disjonctif complet (table binaire)
 * @author Arnaud
 *
 */
public class GenerateurTable extends Observable {

	/**
	 * G�n�re une table avec un nombre de colonnes et de lignes donn�es
	 * @param tableName Le nom de la table � g�n�rer
	 * @param columnsCount Le nombre de colonnes � g�n�rer (nomm� A*)
	 * @param rowsCount Le nombre de lignes � g�n�rer (peupl� al�atoirement de 0 et de 1)
	 * @return Vrai si la table est correctement cr�e, faux sinon.
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
						+ " r�ussie.");
			}
			catch(Exception ex) {
				addLog("Table " + tableName + " inexistante.");
			}
			
			addLog("Cr�ation de la table " + tableName + " : "
					+ sbCreate.toString());
			MysqlJDBC.getInstance().executeUpdate(sbCreate.toString());
			addLog("Cr�ation de la table " + tableName
					+ " r�ussie.");

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
						+ tableName + " r�ussie.");
			}

			addLog("D�connexion de la base...");
			
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	/**
	 * Ecriture d'un message, diffus� par les observateurs,
	 * 
	 * @param message
	 */
	private void addLog(String message) {
		setChanged();
		notifyObservers(message);
	}
}
