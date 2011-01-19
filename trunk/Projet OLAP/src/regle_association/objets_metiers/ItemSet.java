package regle_association.objets_metiers;

/**
 * Classe représentant un itemSet
 * @author Arnaud
 *
 */
public class ItemSet {
	/**
	 * Nom de l'itemSet
	 */
	private String nom;
	
	/** 
	 * Support de l'itemSet
	 */	
	private double support;
	
	/** 
	 * Constructeur complet
	 * @param nom Nom de l'itemSet
	 * @param support Support de l'itemSet
	 */
	public ItemSet(String nom, double support) {
		this.nom = nom;
		this.support = support;
	}
	
	/** 
	 * Constructeur simple
	 * @param nom Nom de l'itemSet
	 */
	public ItemSet(String nom) {
		this(nom, -1.0);
	}

	public double getSupport() {
		return support;
	}

	public void setSupport(double support) {
		this.support = support;
	}

	public String getNom() {
		return nom;
	}

	@Override
	public String toString() {
		return this.getNom();
	}
	
	
}
