package regle_association.test;

import jdbc.MysqlJDBC;
import regle_association.RechercheRegleAssociation;

/** 
 * Classe de test du moteur de calcul de règle d'associations
 * @author Arnaud
 *
 */
public class TestRegleAssociation {

	/**
	 * Lancement du moteur de calcul de règle d'associations
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		RechercheRegleAssociation rra = new RechercheRegleAssociation();

		MysqlJDBC.getInstance().connect();
		rra.getAttributsFrequents(args[0], Double.parseDouble(args[1]));
		rra.getReglesAssociations(args[0], Double.parseDouble(args[2]));
		MysqlJDBC.getInstance().deconnect();
	}


}