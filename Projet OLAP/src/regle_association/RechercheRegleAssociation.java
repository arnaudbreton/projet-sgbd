package regle_association;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jdbc.MysqlJDBC;

public class RechercheRegleAssociation {

	public List<RechercheRegleAssociation> getReglesAssociations(String nomTable,
			double minSup, double minConf) throws Exception {
		// Connexion � la BDD
		MysqlJDBC.getInstance().connect();

		List<String> itemsFrequents = getStringsFrequents(nomTable, minSup);

		// D�connexion de la BDD
		MysqlJDBC.getInstance().deconnect();

		return null;
	}

	private List<String> getStringsFrequents(String nomTable, double minSup)
			throws Exception {
		if (nomTable == null || nomTable == "") {
			throw new Exception(
					"Un nom de table est n�cessaire pour d�terminer les fr�quences");
		}

		if (minSup <= 0) {
			throw new Exception("Le minimum de support doit �tre sup�rieur � 0");
		}

		// On r�cup�re l'ensemble des Strings de la table
		List<String> attsCandidats =  MysqlJDBC.getInstance().getColumnsName(nomTable);
		attsCandidats.remove(0);

		List<String> attsFrequents = new ArrayList<String>();
		ArrayList<String> attsFrequentsN = new ArrayList<String>();
		
		// On analyse les Strings
		int cardinalite = 1;

		do {
			// On calcule les Strings fr�quents
			// (A savoir, ceux ayant un support & une confiance sup�rieure
			// � minSup & minConf)

			attsFrequentsN.clear();
			for (String attCandidat : attsCandidats) {
				double support = getSupport(nomTable, attCandidat);
				if (support >= minSup) {
					attsFrequentsN.add(attCandidat);
				}
			}

			//Je parcours F'
			for(String attFrequentN : attsFrequentsN) {
				boolean found = false;
				int cpt = 0;
				
				//Je parcours F
				while (!found && cpt<attsFrequents.size()){
					found = attFrequentN.contains(attsFrequents.get(cpt));
				}
				
				if (found){
					attsFrequents.remove(cpt);
				}
			}
			
			attsFrequents.addAll(attsFrequentsN);

			// Calcul des candidats d'une certaine cardinalit�
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
		

	private double getConfiance(String nomTable, String... Strings)
			throws SQLException {

		return 0.0;
	}
}
