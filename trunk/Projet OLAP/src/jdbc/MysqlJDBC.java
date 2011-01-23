package jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlJDBC {
	// private Statement _st;
	private Connection _con;
	private static MysqlJDBC _instance;

	private MysqlJDBC() throws InstantiationException, IllegalAccessException {
		connect();
	}

	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.err.println("Erreur: Association base Oracle -> "
					+ e.getMessage());
		}
		try {
			if (_con == null || _con.isClosed()) {
				_con = DriverManager.getConnection(
						"jdbc:mysql://localhost/projetBD", "root", "");
			}
		} catch (SQLException e) {
			System.err.println("Erreur: requete SQL -> " + e.getMessage());
		}
	}

	public static MysqlJDBC getInstance() {
		if (_instance != null)
			return _instance;
		else {
			try {
				_instance = new MysqlJDBC();
			} catch (InstantiationException e) {
				_instance = null;
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				_instance = null;
				e.printStackTrace();
			}
			
			return _instance;
		}
	}

	public void executeQuery(String sqlRequest) {
		Statement st;
		try {
			st = _con.createStatement();
			// connect();
			st.executeQuery(sqlRequest);
			// deconnect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executeUpdate(String sqlRequest) {
		Statement st;
		try {
			st = _con.createStatement();
			// connect();
			st.executeUpdate(sqlRequest);
			// deconnect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultSet get(String sqlRequest) {
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

	/**
	 * Liste l'ensemble des noms des colonnes d'une table
	 * 
	 * @param tableName
	 *            Le nom de la table
	 * @return Retourne l'ensemble des noms des colonnes d'une table, null si la
	 *         table n'existe pas.
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public List<String> getColumnsName(String tableName) {
		DatabaseMetaData dmd;

		try {
			dmd = _con.getMetaData();

			ResultSet resultat = dmd.getColumns(_con.getCatalog(), null,
					tableName, "%");

			ArrayList<String> columnsName = new ArrayList<String>();

			while (resultat.next()) {
				columnsName.add(resultat.getString("COLUMN_NAME"));
			}

			resultat.close();

			return columnsName;

		} catch (Exception e) {
			e.printStackTrace();
			return  new ArrayList<String>();
		}
	}

	/**
	 * Liste l'ensemble des noms des tables de la base de données courante
	 * 
	 * @return Un tableau contenant l'ensemble des noms des tables de la base de
	 *         données courante, un tableau vide sinon.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public String[] getTablesNames() {
		try {
			List<String> tablesNames = new ArrayList<String>();

			DatabaseMetaData dmd = _con.getMetaData();

			ResultSet tables = dmd
					.getTables(_con.getCatalog(), null, "%", null);

			while (tables.next()) {
				tablesNames.add(tables.getString("TABLE_NAME"));
			}

			tables.close();

			return tablesNames.toArray(new String[tablesNames.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return new String[0];
		}
	}

	public void deconnect() {
		try {
			_con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
