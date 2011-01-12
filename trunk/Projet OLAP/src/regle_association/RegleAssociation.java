package regle_association;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jdbc.MysqlJDBC;
import jdbc.OracleJDBC;

public class RegleAssociation {
	/**
	 * Constructeur
	 */
	public RegleAssociation() {

	}

	public List<RegleAssociation> getReglesAssociations(String nomTable,
			double minSup, double minConf) throws Exception {
		// Connexion à la BDD
		MysqlJDBC.getInstance().connect();

		List<Attribut> itemsFrequents = getAttributsFrequents(nomTable, minSup);

		// Déconnexion de la BDD
		MysqlJDBC.getInstance().deconnect();

		return null;
	}

	private List<Attribut> getAttributsFrequents(String nomTable, double minSup)
			throws Exception {
		if (nomTable == null || nomTable == "") {
			throw new Exception(
					"Un nom de table est nécessaire pour déterminer les fréquences");
		}

		if (minSup <= 0) {
			throw new Exception("Le minimum de support doit être supérieur à 0");
		}

		// On récupère l'ensemble des attributs de la table
		List<Attribut> attsCandidats = getNomAttributs(nomTable);

		// On analyse les attributs
		int cardinalite = 1;

		do {
			// Calcul des candidats d'une certaine cardinalité
			cardinalite++;
			attsCandidats.addAll(genererCandidats(cardinalite, attsCandidats));

			// On calcule les attributs fréquents
			// (A savoir, ceux ayant un support & une confiance supérieure
			// à minSup & minConf)
			ArrayList<Attribut> attsFrequents = new ArrayList<Attribut>();
			for (Attribut attCandidat : attsCandidats) {
				attCandidat.setSupport(getSupport(nomTable, attCandidat));
				if (attCandidat.getSupport() > minSup) {
					 attsFrequents.add(attCandidat);
				}
			}

			attsCandidats.clear();
			attsCandidats.addAll(attsFrequents);
			purgerSousEnsembles(attsCandidats);

		} while (attsCandidats.size() > 0 && cardinalite < attsCandidats.size());
		// Déconnexion de la BDD
		MysqlJDBC.getInstance().deconnect();

		return attsCandidats;
	}

	private List<Attribut> purgerSousEnsembles(List<Attribut> attCandidats) {
		ArrayList<Attribut> tempsAttsCandidats = new ArrayList<Attribut>();
		ArrayList<Attribut> attCandidatList = new ArrayList<Attribut>();
		List<Attribut> sousEnsemble = null;

		for (Attribut attCandidat : attCandidats) {
			String[] items = attCandidat.getNom().split(" ");
			int cardinalite = items.length - 1;
			while (cardinalite > 0) {
				attCandidatList.clear();
				for (String item : items) {
					attCandidatList.add(new Attribut(item));
				}
				sousEnsemble = genererCandidats(cardinalite, attCandidatList);

				for(Attribut att : sousEnsemble) {
					if (!attCandidats.contains(att)) {
						tempsAttsCandidats.add(att);
					}
				}

				cardinalite--;
			}
		}

		return tempsAttsCandidats;
	}

	private List<Attribut> getNomAttributs(String nomTable)
			throws SQLException, InstantiationException, IllegalAccessException {
		ArrayList<Attribut> itemsSet = new ArrayList<Attribut>();

		List<String> tableItems = MysqlJDBC.getInstance().getColumnsName(
				nomTable);
		tableItems.remove(0);
		for (String itemName : tableItems) {
			itemsSet.add(new Attribut(itemName));
		}

		return itemsSet;
	}

	private List<Attribut> genererCandidats(int n, List<Attribut> candidats) {
		List<Attribut> tempCandidates = new ArrayList<Attribut>(); // temporary
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
				st1 = new StringTokenizer(candidats.get(i).getNom());
				str1 = st1.nextToken();
				for (int j = i + 1; j < candidats.size(); j++) {
					st2 = new StringTokenizer(candidats.get(j).getNom());
					str2 = st2.nextToken();
					tempCandidates.add(new Attribut(str1 + " " + str2));
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
						tempCandidates.add(new Attribut((str1 + " "
								+ st1.nextToken() + " " + st2.nextToken())
								.trim()));
				}
			}
		}

		return tempCandidates;
	}

	private double getSupport(String nomTable, Attribut attribut)
			throws SQLException, InstantiationException, IllegalAccessException {
		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM " + nomTable
				+ " WHERE ");

		String[] attributs = attribut.getNom().split(" ");
		for (int nbAtts = 0; nbAtts < attributs.length; nbAtts++) {
			sb.append(attributs[nbAtts] + "=1");

			if (nbAtts + 1 < attributs.length) {
				sb.append(" AND ");
			}
		}

		ResultSet resultRq = MysqlJDBC.getInstance().get(sb.toString());
		resultRq.first();
		return resultRq.getDouble(1);
	}

	private double getConfiance(String nomTable, Attribut... attributs)
			throws SQLException {
		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM " + nomTable
				+ " WHERE ");

		ResultSet resultRq = OracleJDBC.getInstance().get(sb.toString());

		return resultRq.getDouble(0);
	}
}
