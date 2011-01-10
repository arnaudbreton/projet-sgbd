package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import utils.OracleJDBC;

public class MysqlJDBC {
	//private Statement _st;
	private Connection _con; 
	private static MysqlJDBC _instance;
	
	private MysqlJDBC(){
		connect();
	}
	
	public void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Erreur: Association base Oracle -> "+e.getMessage());
		}
		try {
			_con = DriverManager.getConnection("jdbc:mysql://sql.free.fr/arnoo91?user=arnoo91&password=mon-passe");
			//_con = DriverManager.getConnection("jdbc:oracle:thin:gouin/gouin@miageb.isi.u-psud.fr:1521:dbmiage");
			//_st = _con.createStatement();
		} catch (SQLException e) {
			System.err.println("Erreur: requete SQL -> "+e.getMessage());
		}
	}
	
	public static MysqlJDBC getInstance(){
		if (_instance != null)
			return _instance;
		else {
			_instance = new MysqlJDBC(); 
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
