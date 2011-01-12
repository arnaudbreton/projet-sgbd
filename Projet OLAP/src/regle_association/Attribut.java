package regle_association;

public class Attribut {
	private String nom;
	
	private double support;	

	public Attribut(String nom) {
		this.nom = nom;
		this.support = 0.0;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Double getSupport() {
		return support;
	}

	public void setSupport(double support) {
		this.support = support;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((Attribut)obj).getNom().equals(this.getNom());
	}
}
