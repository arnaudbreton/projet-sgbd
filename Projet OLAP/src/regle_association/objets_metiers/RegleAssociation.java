package regle_association.objets_metiers;

/** 
 * Classe représentant une règle d'association
 * @author Arnaud
 *
 */
public class RegleAssociation {
	/**
	 * Valeur lorsque aucune confiance n'a été calculée
	 */
	public static final double CONFIANCE_INDEFINI = -1;
	
	/** 
	 * Partie gauche de la règle
	 */
	private ItemSet partieGauche;
	
	/**
	 * Partie droite de la règle
	 */
	private ItemSet partieDroite;
	
	/**
	 * Confiance de la règle
	 */
	private double confiance;
	
	/**
	 * Constructeur complet
	 * @param partieGauche Partie gauche de la règle
	 * @param partieDroite Partie droite de la règle
	 * @param confiance Confiance de la règle
	 */
	public RegleAssociation(ItemSet partieGauche, ItemSet partieDroite, double confiance) {
		this.partieGauche = partieGauche;
		this.partieDroite = partieDroite;		
		this.confiance = confiance;
	}
	
	/**
	 * Constructeur simple
	 * @param partieGauche Partie gauche de la règle
	 * @param partieDroite Partie droite de la règle
	 */
	public RegleAssociation(ItemSet partieGauche, ItemSet partieDroite) {
		this(partieGauche, partieDroite, CONFIANCE_INDEFINI);
	}

	public ItemSet getPartieGauche() {
		return partieGauche;
	}

	public ItemSet getPartieDroite() {
		return partieDroite;
	}

	public void setConfiance(double confiance) {
		this.confiance = confiance;
	}
	
	@Override
	public String toString() {
		return partieGauche.getNom() + "=>" + partieDroite.getNom();
	}

	public double getConfiance() {
		return confiance;
	}
}
