package regle_association.test;

import jdbc.MysqlJDBC;
import regle_association.GenerateurTable;

/**
 * Classe de test du générateur de table
 * @author Arnaud
 *
 */
public class TestGenerateurTable {

	/**
	 * @param args Le nom de la table, le nombre de colonne, le nombre de ligne désirés
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) throws InstantiationException,
			IllegalAccessException {
		MysqlJDBC.getInstance().connect();
		GenerateurTable.generateTable(args[0], Integer.parseInt(args[1]),
				Integer.parseInt(args[2]));
		MysqlJDBC.getInstance().deconnect();
	}
}
