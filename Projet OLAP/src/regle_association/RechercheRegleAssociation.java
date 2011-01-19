package regle_association;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.StringTokenizer;

import regle_association.objets_metiers.ItemSet;
import regle_association.objets_metiers.RegleAssociation;

import jdbc.MysqlJDBC;

/**
 * Classe permettant d'obtenir pour une table SQL donnée, l'ensemble des règles
 * d'associations intéressantes.
 * 
 * @author Arnaud
 * 
 */
public class RechercheRegleAssociation extends Observable {

	@Override
	protected synchronized void setChanged() {
		// TODO Auto-generated method stub
		super.setChanged();
	}

	private List<ItemSet> itemsSetsFrequents;
	
	/**
	 * Calcule les règles d'associations à partir d'une table
	 * 
	 * @param nomTable
	 *            Le nom de la table
	 * @param minConf
	 *            Le minimum de confiance
	 * @return Les règles d'associations intéressantes
	 * @throws Exception
	 */
	public List<RegleAssociation> getReglesAssociations(String nomTable, double minConf) throws Exception {
		
		if(minConf < 0 || minConf > 1) {
			throw new Exception("Le seuil de confiance doit être compris entre 0 et 1");
		}

		try {
			// Connexion à la BDD
			MysqlJDBC.getInstance().connect();
			
			if(this.itemsSetsFrequents == null) {
				throw new Exception("Aucun ensemble de fréquents à exploiter");
			}

			List<RegleAssociation> reglesInteressantes = new ArrayList<RegleAssociation>();
			for (ItemSet itemFrequent : this.itemsSetsFrequents) {
				addLog("Début de l'étude de l'ensemble fréquent : "
						+ itemFrequent);
				List<ItemSet> candidats = new ArrayList<ItemSet>();
				
				for(String item : Arrays.asList(itemFrequent.getNom().split(" "))) {
					candidats.add(new ItemSet(item));
				}

				int cardinalite = candidats.size();
				for (int cptCardinalite = 0; cptCardinalite < cardinalite; cptCardinalite++) {
					List<ItemSet> partiesGauches = null;
					String partieDroite;

					if (cptCardinalite == 0) {
						partiesGauches = new ArrayList<ItemSet>();
					} else {
						partiesGauches = genererCandidats(cptCardinalite,
								candidats);
					}

					if (partiesGauches.size() == 0) {
						// getConfiance(nomTable, "", itemFrequent);
					}

					else {
						for (ItemSet partieGauche : partiesGauches) {
							partieDroite = itemFrequent.getNom();
							for (String itemPartieGauche : partieGauche
									.getNom().split(" ")) {
								partieDroite =partieDroite.replace(
										itemPartieGauche, "");
							}

							partieDroite = partieDroite.replaceAll(" +", " ");
							partieDroite = partieDroite.replaceAll("^ +", "");
							partieDroite = partieDroite.replaceAll(" +$", "");

							RegleAssociation rg = new RegleAssociation(partieGauche, new ItemSet(partieDroite));
							rg.setConfiance(getConfiance(nomTable, rg));
							
							addLog("Etude de la règle : "
									+ rg.toString());

							addLog("Calcul de la confiance de la règle "
											+ rg.toString() + " : " + rg.getConfiance());
							if (rg.getConfiance() > minConf) {
								reglesInteressantes.add(rg);
								addLog(rg.toString() + " est intéressante.");
							}
						}
					}
				}
				addLog("Fin de l'étude de l'ensemble fréquent : "
						+ itemFrequent);
			}

			return reglesInteressantes;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; 
		}
		finally {
			// Déconnexion de la BDD
			MysqlJDBC.getInstance().deconnect();
			this.itemsSetsFrequents = null;
		}
	}

	/**
	 * Calcule les fréquents dépassant un certain seuil
	 * 
	 * @param nomTable
	 *            Nom de la trable sur lequel calculer les fréquents
	 * @param minSup
	 *            Le minimum de support
	 * @return Les ensembles fréquents
	 * @throws Exception
	 */
	public List<ItemSet> getAttributsFrequents(String nomTable, double minSup)
			throws Exception {

		if (nomTable == null || nomTable == "") {
			throw new Exception(
					"Un nom de table est nécessaire pour déterminer les fréquences");
		}

		if (minSup <= 0) {
			throw new Exception("Le minimum de support doit être supérieur à 0");
		}

		MysqlJDBC.getInstance().connect();
		
		// On récupère l'ensemble des Strings de la table
		List<ItemSet> itemsSetsCandidats = getNomColonnes(nomTable); 
	
		List<ItemSet> itemsSetsFrequents = new ArrayList<ItemSet>();
		ArrayList<ItemSet> itemsSetsFrequentsN = new ArrayList<ItemSet>();

		// On analyse les Strings
		int cardinalite = 1;

		do {
			// On calcule les Strings fréquents
			// (A savoir, ceux ayant un support & une confiance supérieure
			// à minSup & minConf)

			itemsSetsFrequentsN.clear();
			for (ItemSet attCandidat : itemsSetsCandidats) {
				attCandidat.setSupport(getSupport(nomTable, attCandidat));
				if (attCandidat.getSupport() >= minSup) {
					itemsSetsFrequentsN.add(attCandidat);
				}
			}

			itemsSetsFrequents.addAll(itemsSetsFrequentsN);

			// Calcul des candidats d'une certaine cardinalité
			cardinalite++;
			itemsSetsCandidats = genererCandidats(cardinalite, itemsSetsFrequentsN);
		} while (itemsSetsCandidats.size() > 1);

		this.itemsSetsFrequents = itemsSetsFrequents;
		
		MysqlJDBC.getInstance().deconnect();
		return itemsSetsFrequents;
	}

	private static List<ItemSet> getNomColonnes(String nomTable) {
		List<ItemSet> nomsColonnes;
		
		nomsColonnes = new ArrayList<ItemSet>();
		for(String nomColonne : MysqlJDBC.getInstance().getColumnsName(nomTable)) {
			nomsColonnes.add(new ItemSet(nomColonne));
		}
		
		if(nomsColonnes.size() > 0) {
			nomsColonnes.remove(0);
		}
		
		return nomsColonnes;
	}

	/**
	 * Génère un ensemble d'attributs d'une certaine cardinalité
	 * 
	 * @param n
	 *            La cardinalité de l'ensemble souhaité
	 * @param candidats
	 *            L'ensemble de base
	 * @return Un ensemble de cardinalité n.
	 */
	private List<ItemSet> genererCandidats(int n, List<ItemSet> candidats) {
		List<ItemSet> tempCandidates = new ArrayList<ItemSet>(); // temporary
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
					tempCandidates.add(new ItemSet(new String(str1 + " " + str2)));
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
						tempCandidates.add(new ItemSet(new String((str1 + " "
								+ st1.nextToken() + " " + st2.nextToken())
								.trim())));
				}
			}
		}

		return tempCandidates;
	}

	/**
	 * Calcul du support
	 * 
	 * @param nomTable
	 *            Nom de la table
	 * @param String
	 *            Liste d'attributs
	 * @return Le support des attributs envoyés
	 * @throws SQLException
	 *             Erreur SQL
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private double getSupport(String nomTable, ItemSet itemSet)
			throws SQLException, InstantiationException, IllegalAccessException {
		StringBuilder sbCountWhere1 = new StringBuilder("SELECT COUNT(*) FROM "
				+ nomTable + " WHERE ");

		String[] Strings = itemSet.getNom().split(" ");
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

	/**
	 * Calcule la confiance d'une règle
	 * @param nomTable Nom de la table dans lesquels on recherche les données
	 * @param rg Règles d'associations dont on veut la confiance
	 * @return La confiance de la règle
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private double getConfiance(String nomTable, RegleAssociation rg) throws SQLException, InstantiationException,
			IllegalAccessException {
		return getSupport(nomTable, new ItemSet(rg.getPartieGauche().getNom().concat(" " + rg.getPartieDroite().getNom())))
				/ getSupport(nomTable, rg.getPartieGauche());
	}
	
	private void addLog(String message) {
		setChanged();
		notifyObservers(message);
	}
}
