package swt.test;

import jdbc.MysqlJDBC;
import swt.Screen;

/**
 * Classe de lancement de l'IHM
 * @author Arnaud
 *
 */
public class TestIHM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MysqlJDBC.getInstance().connect();
		new Screen().createContent();
		MysqlJDBC.getInstance().deconnect();
	}

}
