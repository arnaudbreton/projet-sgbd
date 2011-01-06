package regle_association;

public class Attribut {
	private String nom;
	
	private double support;
	
	private double confiance;
	
	public Attribut(String nom) {
		this.nom = nom;
		this.support = 0.0;
		this.confiance = 0.0;
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

	public double getConfiance() {
		return confiance;
	}

	public void setConfiance(double confiance) {
		this.confiance = confiance;
	}
}
