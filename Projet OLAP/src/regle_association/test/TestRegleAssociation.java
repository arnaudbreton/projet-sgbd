package regle_association.test;

import regle_association.RechercheRegleAssociation;

public class TestRegleAssociation {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		RechercheRegleAssociation ra = new RechercheRegleAssociation();
		
		ra.getReglesAssociations(args[0], Double.parseDouble(args[1]), Double.parseDouble(args[2]));
	}


}
