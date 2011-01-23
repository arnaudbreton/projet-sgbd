package regle_association;

import java.io.FileWriter;

/**
 * Classe permettant d'analyser les performances de l'algorithme de recherche des règles d'associations
 * @author Arnaud
 *
 */
public class AnalyseurPerformances {

	/**
	 * Méthode principale d'analyse.
	 * @param args Arguments d'analyse
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String nomTable = args[0];
		
		double minSup = Double.parseDouble(args[1]);
		double pasSup = Double.parseDouble(args[2]);

		double minConf = Double.parseDouble(args[3]);
		double pasConf = Double.parseDouble(args[4]);
		

		int nbLignesBase = Integer.parseInt(args[5]);
		double coeffLignes = Double.parseDouble(args[6]);
		int nbLignes = nbLignesBase;		

		int nbColonnesBase = Integer.parseInt(args[6]);
		int nbColonnes = nbColonnesBase;
		double coeffColonnes = Double.parseDouble(args[7]);

		int nbEssais = Integer.parseInt(args[10]);
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
