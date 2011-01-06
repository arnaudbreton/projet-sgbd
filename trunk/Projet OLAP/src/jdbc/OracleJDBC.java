package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import utils.OracleJDBC;

public class OracleJDBC {
	//private Statement _st;
	private Connection _con; 
	private static OracleJDBC _instance;
	
	private OracleJDBC(){
		connect();
	}
	
	public void connect(){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("Erreur: Association base Oracle -> "+e.getMessage());
		}
		try {
			_con = DriverManager.getConnection("jdbc:oracle:thin:breton/breton@miageb.isi.u-psud.fr:1521:dbmiage");
			//_con = DriverManager.getConnection("jdbc:oracle:thin:gouin/gouin@miageb.isi.u-psud.fr:1521:dbmiage");
			//_st = _con.createStatement();
		} catch (SQLException e) {
			System.err.println("Erreur: requete SQL -> "+e.getMessage());
		}
	}
	
	public static OracleJDBC getInstance(){
		if (_instance != null)
			return _instance;
		else {
			_instance = new OracleJDBC(); 
			return _instance;
		}
	}
	
	public void execute(String sqlRequest){
		Statement st;
		try {
			st = _con.createStatement();
			//connect();
			st.executeQuery(sqlRequest);
			//deconnect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet get(String sqlRequest){
		Statement st;
		try {
			st = _con.createStatement();
			ResultSet toReturn = st.executeQuery(sqlRequest);
			return toReturn;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void deconnect(){
		try {
				//_st.close();
				_con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
