package regle_association;

import java.io.FileWriter;

public class AnalyseurPerformances {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		double minSup = 0.3;
		double pasSup = 0.1;

		double minConf = 0.5;
		double pasConf = 0.1;

		String nomTable = "testFrequent";

		int nbLignesBase = 50000;
		int nbLignes = nbLignesBase;
		double coeffLignes = 0;

		int nbColonnesBase = 5;
		int nbColonnes = nbColonnesBase;
		double coeffColonnes = 1.1;

		int nbEssais = 10;
		int cptEssais = 0;

		RechercheRegleAssociation rra = new RechercheRegleAssociation();

		FileWriter writer = new FileWriter("resultPerfs.txt");

		while (minSup < 1 && minConf < 1) {
			nbColonnes = nbColonnesBase;
			nbLignes = nbLignesBase;
			
			cptEssais = 0;
			while (cptEssais < nbEssais) {
				GenerateurTable.generateTable(nomTable, nbColonnes, nbLignes);

				long start = System.currentTimeMillis();
				rra.getAttributsFrequents(nomTable, minSup);
				rra.getReglesAssociations(nomTable, minConf);
				long end = System.currentTimeMillis();
				
				writer.write(String
						.format("Temps d'exécution essai n° %d (nbColonnes : %d, nbLignes : %d, minSup : %f, minConf : %f) : %dms %n",
								cptEssais, nbColonnes, nbLignes, minSup, minConf,
								end - start));
				//writer.write(end-start+";");

				nbColonnes += (int) (nbColonnesBase * coeffColonnes);
				nbLignes += (int) (nbLignesBase * coeffLignes);
				
				cptEssais++;
			}
			
			writer.write(String.format("%n"));

			if (minConf + pasConf < 1) {
				minConf += pasConf;
			}
			minSup += pasSup;
			
		}

		writer.close();
	}
}
