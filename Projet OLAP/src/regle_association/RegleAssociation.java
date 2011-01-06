package regle_association;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import jdbc.OracleJDBC;

public class RegleAssociation {
	/**
	 * Constructeur
	 */
	public RegleAssociation() {

	}

	public Object getAttributFrequent(String nomTable, double minSup,
			double minConf) throws Exception {
		if (nomTable == null || nomTable == "") {
			throw new Exception(
					"Un nom de table est nécessaire pour déterminer les fréquences");
		}

		if (minSup <= 0) {
			throw new Exception("Le minimum de support doit être supérieur à 0");
		}

		if (minConf <= 0) {
			throw new Exception(
					"Le minimum de confiance doit être supérieur à 0");
		}

		// Connexion à la BDD
		OracleJDBC.getInstance().connect();

		// On récupère l'ensemble des attributs de la table
		List<Attribut> attsCandidats = getNomAttributs(nomTable);

		// On analyse les attributs
		int cardinalite = 0;

		do {
			cardinalite++;
			genererCandidats(cardinalite, attsCandidats);

			ArrayList<String> attsFrequents = new ArrayList<String>();
			for (Attribut attCandidat : attsCandidats) {
				attCandidat.setSupport(getSupport(attCandidat));
				attCandidat.setConfiance(getConfiance(attCandidat));
				if(attCandidat.getSupport() > minSup && attCandidat.getConfiance() > minConf)) {
					attsFrequents.add(attCandidat);
				}
			}

			attsCandidats = attsFrequents;
		} while (attsCandidats.size() > 0 && cardinalite < attsCandidats.size());
		// Déconnexion de la BDD
		OracleJDBC.getInstance().deconnect();

		return null;
	}

	private List<Attribut> getNomAttributs(String nomTable) throws SQLException {
		ResultSet resultReqNomAtt = OracleJDBC.getInstance().get(
				"SELECT COLUMN_NAME from USER_TAB_COLUMNS where TABLE_NAME ='"
						+ nomTable + "'");

		ArrayList<Attribut> attributs = new ArrayList<Attribut>();
		while(resultReqNomAtt.next()) {
			attributs.add(new Attribut(resultReqNomAtt.getString(0)));
			resultReqNomAtt.next();
		}
		
		return attributs;
	}

	private void genererCandidats(int n, List<Attribut> candidats) {
		List<String> tempCandidates = new ArrayList<String>(); // temporary
																// candidate
																// string List
		String str1, str2; // strings that will be used for comparisons
		StringTokenizer st1, st2; // string tokenizers for the two itemsets
									// being compared

		// if its the first set, candidates are just the numbers
		if (n == 1) {
			for (int i = 1; i <= candidats.size(); i++) {
				tempCandidates.add(candidats.get(i).getNom());
			}
		} else if (n == 2) // second itemset is just all combinations of itemset
							// 1
		{
			// add each itemset from the previous frequent itemsets together
			for (int i = 0; i < candidats.size(); i++) {
				st1 = new StringTokenizer(candidats.get(i).getNom());
				str1 = st1.nextToken();
				for (int j = i + 1; j < candidats.size(); j++) {
					st2 = new StringTokenizer(candidats.get(j).getNom());
					str2 = st2.nextToken();
					tempCandidates.add(str1 + " " + str2);
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
					st1 = new StringTokenizer(candidats.get(i).getNom());
					st2 = new StringTokenizer(candidats.get(j).getNom());

					// make a string of the first n-2 tokens of the strings
					for (int s = 0; s < n - 2; s++) {
						str1 = str1 + " " + st1.nextToken();
						str2 = str2 + " " + st2.nextToken();
					}

					// if they have the same n-2 tokens, add them together
					if (str2.compareToIgnoreCase(str1) == 0)
						tempCandidates
								.add((str1 + " " + st1.nextToken() + " " + st2
										.nextToken()).trim());
				}
			}
		}
		// clear the old candidates
		candidats.clear();
		// set the new ones
		candidats = new ArrayList<Attribut>(tempCandidates);
		tempCandidates.clear();
	}

	private double getSupport(String nomTable, String... attributs) throws SQLException {
		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM " + nomTable
				+ " WHERE ");

		int nbAtts = 0;
		while (nbAtts < attributs.length) {
			if (nbAtts > 0) {
				sb.append(" AND ");
			}
			sb.append(attributs[nbAtts] + "=1");
			
			nbAtts++;
		}

		ResultSet resultRq = OracleJDBC.getInstance().get(sb.toString());
		
		return resultRq.getDouble(0);
	}
	
	private double getConfiance(String nomTable, String attGauche, String... attributs) throws SQLException {
		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM " + nomTable
				+ " WHERE ");

		

		ResultSet resultRq = OracleJDBC.getInstance().get(sb.toString());
		
		return resultRq.getDouble(0);
	}
}
