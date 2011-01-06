package Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Utils.OracleJDBC;

public class Client {
	private final String _getCliReq = "SELECT * FROM Clients C, Adresse A WHERE C.idadresse=A.id AND login=";
	private final String _getCommReq = "SELECT id FROM Clients C, Commandes_clients CC WHERE C.login=CC.loginclient AND C.login=";
	
	private ArrayList<CommandeClients> _commandes = new ArrayList<CommandeClients>();
	
	private String _login;
	private String _mdp;
	private String _nom;
	private String _prenom;
	private int _numRue;
	private String _rue;
	private String _ville;
	private int _cp;
	private String _tel;
	private String _pays;
	
	public void setLogin(String login){_login = login;}
	public void setMdp(String mdp){_mdp = mdp;}
	public void setNom(String nom){	_nom = nom;}
	public void setPrenom(String prenom){_prenom = prenom;}
	public void setNumRue(int numRue){_numRue = numRue;}
	public void setRue(String rue){_rue = rue;}
	public void setVille(String ville){_ville = ville;}
	public void setCP(int cp){_cp = cp;}
	public void setTel(String tel){_tel = tel;}
	public void setPays(String pays){_pays = pays;}
	public String getLogin(){return _login;}
	public String getMdp(){return _mdp;}
	public String getNom(){return _nom;}
	public String getPrenom(){return _prenom;}
	public int getNumRue(){return _numRue;}
	public String getRue(){return _rue;}
	public String getVille(){return _ville;}
	public int getCP(){return _cp;}
	public String getTel(){return _tel;}
	public String getPays(){return _pays;}
	public CommandeClients getCommande(String id){
		return new CommandeClients(Integer.parseInt(id));
	}
	public ArrayList<CommandeClients> getCommandes(){
		ResultSet commSql = OracleJDBC.getInstance().get(_getCommReq+"'"+_login+"'");
		try {
			while(commSql.next()){
				_commandes.add(new CommandeClients(commSql.getInt("id")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return _commandes;
	}
	
	
	public Client(){
	}

	public Client(String id) {
		this();
		load(id);
	}
	
	public void load(String id){
		ResultSet result = OracleJDBC.getInstance().get(_getCliReq+"'"+id+"'");
		
		try{
			result.next();
			_login = result.getString("login");
			_mdp = result.getString("mot_de_passe");
			_nom = result.getString("nom");
			_prenom = result.getString("prenom");
			_numRue = result.getInt("numrue");
			_rue = result.getString("rue");
			_ville = result.getString("ville");
			_cp = result.getInt("code_postal");
			_pays = result.getString("pays");
			_tel = result.getString("telephone");
			
			
			if (result.next())
				System.err.println("Erreur: Id produit non unique!");
			result.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
