package regle_association;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import jdbc.MysqlJDBC;

public class RechercheRegleAssociation {

	public List<String> getReglesAssociations(
			String nomTable, double minSup, double minConf) throws Exception {
		// Connexion à la BDD
		MysqlJDBC.getInstance().connect();

		List<String> itemsFrequents = getAttributsFrequents(nomTable, minSup);

		List<String> reglesInteressantes = new ArrayList<String>();
		for (String itemFrequent : itemsFrequents) {
			System.out.println("Début de l'étude de l'ensemble fréquent : " + itemFrequent);
			List<String> candidats = Arrays.asList(itemFrequent.split(" "));

			int cardinalite = candidats.size();
			for (int cptCardinalite = 0; cptCardinalite < cardinalite; cptCardinalite++) {
				List<String> partiesGauches = null;
				String partieDroite;

				if (cptCardinalite == 0) {
					partiesGauches = new ArrayList<String>();
				} else {
					partiesGauches = genererCandidats(cptCardinalite, candidats);
				}

				if (partiesGauches.size() == 0) {
					//getConfiance(nomTable, "", itemFrequent);
				}

				else {
					for (String partieGauche : partiesGauches) {
						partieDroite = itemFrequent;
						for(String itemPartieGauche : partieGauche.split(" ")) {
							partieDroite = partieDroite.replace(itemPartieGauche, "");
						}
						
						partieDroite = partieDroite.replaceAll(" +"," ");
						partieDroite = partieDroite.replaceAll("^ +","");
						partieDroite = partieDroite.replaceAll(" +$","");
						
						String regleEnCours = partieGauche+"=>"+partieDroite;
						
						System.out.println("Etude de la règle : " + regleEnCours);
						double conf = getConfiance(nomTable, partieGauche, partieDroite);
						
						System.out.println("Calcul de la confiance de la règle " + regleEnCours + " : " + conf);
						if(conf > minConf) {
							reglesInteressantes.add(partieGauche+"=>"+partieDroite);
							System.out.println(partieGauche+"=>"+partieDroite + " : est intéressante");
						}
					}
				}
			}
			System.out.println("Fin de l'étude de l'ensemble fréquent : " + itemFrequent);
		}

		// Déconnexion de la BDD
		MysqlJDBC.getInstance().deconnect();

		return reglesInteressantes;
	}

	private List<String> getAttributsFrequents(String nomTable, double minSup)
			throws Exception {

		if (nomTable == null || nomTable == "") {
			throw new Exception(
					"Un nom de table est nécessaire pour déterminer les fréquences");
		}

		if (minSup <= 0) {
			throw new Exception("Le minimum de support doit être supérieur à 0");
		}

		// On récupère l'ensemble des Strings de la table
		List<String> attsCandidats = MysqlJDBC.getInstance().getColumnsName(
				nomTable);
		attsCandidats.remove(0);

		List<String> attsFrequents = new ArrayList<String>();
		ArrayList<String> attsFrequentsN = new ArrayList<String>();

		// On analyse les Strings
		int cardinalite = 1;

		do {
			// On calcule les Strings fréquents
			// (A savoir, ceux ayant un support & une confiance supérieure
			// à minSup & minConf)

			attsFrequentsN.clear();
			for (String attCandidat : attsCandidats) {
				double support = getSupport(nomTable, attCandidat);
				if (support >= minSup) {
					attsFrequentsN.add(attCandidat);
				}
			}

			attsFrequents.addAll(attsFrequentsN);

			// Calcul des candidats d'une certaine cardinalité
			cardinalite++;
			attsCandidats = genererCandidats(cardinalite, attsFrequentsN);
		} while (attsCandidats.size() > 1);

		return attsFrequents;
	}

	private List<String> genererCandidats(int n, List<String> candidats) {
		List<String> tempCandidates = new ArrayList<String>(); // temporary
																// candidate
																// string
																// List
		String str1, str2; // strings that will be used for comparisons
		StringTokenizer st1, st2; // string tokenizers for the two itemsets
									// being compared

		// if its the first set, candidates are just the numbers
		if (n == 1) {
			for (int i = 0; i < candidats.size(); i++) {
				tempCandidates.add(candidats.get(i));
			}
		} else if (n == 2) // second itemset is just all combinations of itemset
							// 1
		{
			// add each itemset from the previous frequent itemsets together
			for (int i = 0; i < candidats.size(); i++) {
				st1 = new StringTokenizer(candidats.get(i));
				str1 = st1.nextToken();
				for (int j = i + 1; j < candidats.size(); j++) {
					st2 = new StringTokenizer(candidats.get(j));
					str2 = st2.nextToken();
					tempCandidates.add(new String(str1 + " " + str2));
				}
			}
		} else {
			// for each itemset
			for (int i = 0; i < candidats.size(); i++) {
				// compare to the next itemset
				for (int j = i + 1; j < candidats.size(); j++) {
					// create the strigns
					str1 = new String();
					str2 = new String();
					// create the tokenizers
					st1 = new StringTokenizer(candidats.get(i));
					st2 = new StringTokenizer(candidats.get(j));

					// make a string of the first n-2 tokens of the strings
					for (int s = 0; s < n - 2; s++) {
						str1 = str1 + " " + st1.nextToken();
						str2 = str2 + " " + st2.nextToken();
					}

					// if they have the same n-2 tokens, add them together
					if (str2.compareToIgnoreCase(str1) == 0)
						tempCandidates.add(new String((str1 + " "
								+ st1.nextToken() + " " + st2.nextToken())
								.trim()));
				}
			}
		}

		return tempCandidates;
	}

	private double getSupport(String nomTable, String String)
			throws SQLException, InstantiationException, IllegalAccessException {
		StringBuilder sbCountWhere1 = new StringBuilder("SELECT COUNT(*) FROM "
				+ nomTable + " WHERE ");

		String[] Strings = String.split(" ");
		for (int nbAtts = 0; nbAtts < Strings.length; nbAtts++) {
			sbCountWhere1.append(Strings[nbAtts] + "=1");

			if (nbAtts + 1 < Strings.length) {
				sbCountWhere1.append(" AND ");
			}
		}

		ResultSet resultRqCountWhere1 = MysqlJDBC.getInstance().get(
				sbCountWhere1.toString());

		resultRqCountWhere1.first();

		return resultRqCountWhere1.getDouble(1);
	}

	private double getConfiance(String nomTable, String partieGauche,
			String partieDroite) throws SQLException, InstantiationException, IllegalAccessException {
		return getSupport(nomTable, partieGauche.concat(" " + partieDroite)) / getSupport(nomTable, partieGauche);
	}
}
