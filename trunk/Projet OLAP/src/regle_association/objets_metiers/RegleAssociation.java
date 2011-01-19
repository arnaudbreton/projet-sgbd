package regle_association.objets_metiers;

public class RegleAssociation {
	private ItemSet partieGauche;
	
	private ItemSet partieDroite;
	
	private double confiance;
	
	public RegleAssociation(ItemSet partieGauche, ItemSet partieDroite, double confiance) {
		this.partieGauche = partieGauche;
		this.partieDroite = partieDroite;		
		this.confiance = confiance;
	}
	
	public RegleAssociation(ItemSet partieGauche, ItemSet partieDroite) {
		this(partieGauche, partieDroite, -1);
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
