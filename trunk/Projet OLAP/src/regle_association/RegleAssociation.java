package regle_association;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import jdbc.MysqlJDBC;

public class RegleAssociation {
	/**
	 * Constructeur
	 */
	public RegleAssociation() {

	}

	public List<RegleAssociation> getReglesAssociations(String nomTable,
			double minSup, double minConf) throws Exception {
		// Connexion � la BDD
		MysqlJDBC.getInstance().connect();

		List<Attribut> itemsFrequents = getAttributsFrequents(nomTable, minSup);

		// D�connexion de la BDD
		MysqlJDBC.getInstance().deconnect();

		return null;
	}

	private List<Attribut> getAttributsFrequents(String nomTable, double minSup)
			throws Exception {
		if (nomTable == null || nomTable == "") {
			throw new Exception(
					"Un nom de table est n�cessaire pour d�terminer les fr�quences");
		}

		if (minSup <= 0) {
			throw new Exception("Le minimum de support doit �tre sup�rieur � 0");
		}

		// On r�cup�re l'ensemble des attributs de la table
		List<List<Attribut>> attsCandidats = new ArrayList<List<Attribut>>();
		attsCandidats.add(getNomAttributs(nomTable));

		// On analyse les attributs
		int cardinalite = -1;

		boolean candidats = true;
		do {
			// On calcule les attributs fr�quents
			// (A savoir, ceux ayant un support & une confiance sup�rieure
			// � minSup & minConf)
			
			cardinalite++;	
			
			ArrayList<Attribut> attsFrequents = new ArrayList<Attribut>();
			for (Attribut attCandidat : attsCandidats.get(cardinalite)) {
				attCandidat.setSupport(getSupport(nomTable, attCandidat));
				if (attCandidat.getSupport() > minSup) {
					 attsFrequents.add(attCandidat);
				}
			}
			
			attsCandidats.set(cardinalite, attsFrequents);
			
			// Calcul des candidats d'une certaine cardinalit�
			
			if(attsCandidats.get(cardinalite).size() > 1) {
				attsCandidats.add(genererCandidats(cardinalite+2, attsCandidats.get(cardinalite)));
			}			
			else {
				attsCandidats.remove(cardinalite);
				candidats = false;
			}

		} while (candidats);

		if(attsCandidats.size() > 0) {
			return attsCandidats.get(attsCandidats.size()-1);
		}
		else {
			return null;
		}
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
		StringBuilder sbCountWhere1 = new StringBuilder("SELECT COUNT(*) FROM " + nomTable
				+ " WHERE ");		

		String[] attributs = attribut.getNom().split(" ");
		for (int nbAtts = 0; nbAtts < attributs.length; nbAtts++) {
			sbCountWhere1.append(attributs[nbAtts] + "=1");

			if (nbAtts + 1 < attributs.length) {
				sbCountWhere1.append(" AND ");
			}
		}

		ResultSet resultRqCountWhere1 = MysqlJDBC.getInstance().get(sbCountWhere1.toString());		
		ResultSet resultRqCount = MysqlJDBC.getInstance().get("SELECT COUNT(*) FROM " + nomTable);
		
		resultRqCountWhere1.first();
		resultRqCount.first();
		
		double support = resultRqCountWhere1.getDouble(1) / resultRqCount.getDouble(1);
		
		resultRqCount.close();
		resultRqCountWhere1.close();
		
		return support;		
	}

	private double getConfiance(String nomTable, Attribut... attributs)
			throws SQLException {
		
		return 0.0;
	}
}
