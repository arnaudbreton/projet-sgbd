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

	/**
	 * Ensemble de fréquents
	 */
	private List<ItemSet> itemsSetsFrequents;

	/**
	 * Nombre de lignes de la table à analyser
	 */
	private int nbLignes;

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
	public List<RegleAssociation> getReglesAssociations(String nomTable,
			double minConf) throws Exception {

		if (minConf < 0 || minConf > 1) {
			throw new Exception(
					"Le seuil de confiance doit être compris entre 0 et 1");
		}

		List<RegleAssociation> reglesInteressantes = new ArrayList<RegleAssociation>();

		try {
			if (this.itemsSetsFrequents == null) {
				throw new Exception("Aucun ensemble de fréquents à exploiter");
			}

			// Parcourt de l'ensemble des fréquents
			for (ItemSet itemFrequent : this.itemsSetsFrequents) {
				addLog("Début de l'étude de l'ensemble fréquent : "
						+ itemFrequent);
				List<ItemSet> candidats = new ArrayList<ItemSet>();

				for (String item : Arrays.asList(itemFrequent.getNom().split(
						" "))) {
					candidats.add(new ItemSet(item));
				}

				int cardinalite = candidats.size();
				// Calcul des règles
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
					} else {
						for (ItemSet partieGauche : partiesGauches) {
							partieDroite = itemFrequent.getNom();
							for (String itemPartieGauche : partieGauche
									.getNom().split(" ")) {
								partieDroite = partieDroite.replace(
										itemPartieGauche, "");
							}

							partieDroite = partieDroite.replaceAll(" +", " ");
							partieDroite = partieDroite.replaceAll("^ +", "");
							partieDroite = partieDroite.replaceAll(" +$", "");

							RegleAssociation rg = new RegleAssociation(
									partieGauche, new ItemSet(partieDroite));
							rg.setConfiance(getConfiance(nomTable, rg));

							addLog("Etude de la règle : " + rg.toString());

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
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			this.itemsSetsFrequents = null;
		}

		return reglesInteressantes;
	}

	/**
	 * Calcule les fréquents dépassant un certain seuil de support
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

		// On récupère l'ensemble des Strings de la table
		List<ItemSet> itemsSetsCandidats = getNomColonnes(nomTable);

		List<ItemSet> itemsSetsFrequents = new ArrayList<ItemSet>();
		ArrayList<ItemSet> itemsSetsFrequentsN = new ArrayList<ItemSet>();

		// Calcul unique du nombre de lignes dans la table pour le futur calcul
		// du support
		ResultSet resultRqCount = MysqlJDBC.getInstance().get(
				"SELECT COUNT(*) FROM " + nomTable);
		resultRqCount.first();
		this.nbLignes = resultRqCount.getInt(1);
		resultRqCount.close();

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
			itemsSetsCandidats = genererCandidats(cardinalite,
					itemsSetsFrequentsN);
		} while (itemsSetsCandidats.size() > 1);

		this.itemsSetsFrequents = itemsSetsFrequents;

		return itemsSetsFrequents;
	}

	private static List<ItemSet> getNomColonnes(String nomTable) {
		List<ItemSet> nomsColonnes;

		nomsColonnes = new ArrayList<ItemSet>();
		for (String nomColonne : MysqlJDBC.getInstance().getColumnsName(
				nomTable)) {
			nomsColonnes.add(new ItemSet(nomColonne));
		}

		if (nomsColonnes.size() > 0) {
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
		// List temporaires de candidats
		List<ItemSet> candidatsTemp = new ArrayList<ItemSet>();
		// Strings utilisés pour comparaisons
		String str1, str2;
		// StringTokenizer utilisés pour comparer les deux ItemsSets
		StringTokenizer st1, st2;

		// Si c'est le premier ensemble, seuls les éléments en font partis
		if (n == 1) {
			for (int i = 0; i < candidats.size(); i++) {
				candidatsTemp.add(candidats.get(i));
			}
			// Un ItemSet de rang est une combinaison de ceux du rang 1
		} else if (n == 2) {
			// Ajout de chaque élément deux à deux
			for (int i = 0; i < candidats.size(); i++) {
				st1 = new StringTokenizer(candidats.get(i).getNom());
				str1 = st1.nextToken();
				for (int j = i + 1; j < candidats.size(); j++) {
					st2 = new StringTokenizer(candidats.get(j).getNom());
					str2 = st2.nextToken();
					candidatsTemp.add(new ItemSet(
							new String(str1 + " " + str2)));
				}
			}
		} else {
			for (int i = 0; i < candidats.size(); i++) {
				for (int j = i + 1; j < candidats.size(); j++) {
					str1 = new String();
					str2 = new String();

					st1 = new StringTokenizer(candidats.get(i).getNom());
					st2 = new StringTokenizer(candidats.get(j).getNom());

					for (int s = 0; s < n - 2; s++) {
						str1 = str1 + " " + st1.nextToken();
						str2 = str2 + " " + st2.nextToken();
					}

					if (str2.compareToIgnoreCase(str1) == 0)
						candidatsTemp.add(new ItemSet(new String((str1 + " "
								+ st1.nextToken() + " " + st2.nextToken())
								.trim())));
				}
			}
		}

		return candidatsTemp;
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

		return resultRqCountWhere1.getDouble(1) / this.nbLignes;
	}

	/**
	 * Calcule la confiance d'une règle
	 * 
	 * @param nomTable
	 *            Nom de la table dans lesquels on recherche les données
	 * @param rg
	 *            Règle d'association dont on veut la confiance
	 * @return La confiance de la règle
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private double getConfiance(String nomTable, RegleAssociation rg)
			throws SQLException, InstantiationException, IllegalAccessException {
		// Si l'ItemSet de gauche possède déjà un support, on l'exploite, sinon on calcul
		if (rg.getPartieGauche().getSupport() == Attribut.SUPPORT_INDEFINI) {
			rg.getPartieGauche().setSupport(
					getSupport(nomTable, rg.getPartieGauche()));
		}

		double supportPartieGauche = rg.getPartieGauche().getSupport();

		return getSupport(nomTable, new ItemSet(rg.getPartieGauche().getNom()
				.concat(" " + rg.getPartieDroite().getNom())))
				/ supportPartieGauche;
	}

	/**
	 * Ecriture d'un message, diffusé par les observateurs,
	 * 
	 * @param message
	 */
	private void addLog(String message) {
		setChanged();
		notifyObservers(message);
	}
}
