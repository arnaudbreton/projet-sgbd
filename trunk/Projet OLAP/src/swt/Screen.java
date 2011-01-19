package swt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import jdbc.MysqlJDBC;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import regle_association.RechercheRegleAssociation;

public class Screen implements Observer{
	private String _tableName = "";
	private String _minConf = "";
	private String _minSup = "";
	
	private Table _dataBaseTable;
	private List _traceLog;
	
	private MysqlJDBC _dataBaseConnection; 
	private RechercheRegleAssociation _ra;
	
	public Screen(){
			_dataBaseConnection = MysqlJDBC.getInstance();
			_ra = new RechercheRegleAssociation();
			_ra.addObserver(this);
	}
	
	public void createContent(){
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		
		Label lbl;
		
		//Groupe Configuration
		Group confGroup = new Group(shell,SWT.NONE);
		confGroup.setText("Configuration");
		confGroup.setLayout(new GridLayout(4, false));
		confGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		lbl = new Label(confGroup,SWT.NONE);
		lbl.setText("Min conf: ");
		
		final Text minConfTxt = new Text(confGroup,SWT.SINGLE);
		minConfTxt.setText("");
		minConfTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				if (!minConfTxt.getText().isEmpty()){
					_minConf = minConfTxt.getText();
				}
			}
		});
		minConfTxt.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				minConfTxt.selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
		});
		
		lbl = new Label(confGroup,SWT.NONE);
		lbl.setText("Min sup: ");
		
		final Text minSupTxt = new Text(confGroup,SWT.SINGLE);
		minSupTxt.setText("");
		minSupTxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				if (!minSupTxt.getText().isEmpty()){
					_minSup = minSupTxt.getText();
				}
			}
		});
		
		//Groupe de s�lection de la table
		Group tableGroup = new Group(shell,SWT.NONE);
		tableGroup.setText("Table selection");
		tableGroup.setLayout(new GridLayout(1, false));
		tableGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//Barre de s�lection/cr�ation de la table � utiliser
		Composite selectTableBar = new Composite(tableGroup,SWT.NONE);
		selectTableBar.setLayout(new GridLayout(3,false));
		selectTableBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		lbl = new Label(selectTableBar,SWT.NONE);
		lbl.setText("Table name: ");
		
		//Combo de choix de la table
		final Combo comboTable = new Combo(selectTableBar, SWT.NONE);
		comboTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboTable.setItems(_dataBaseConnection.getTablesNames());
		comboTable.setText("Select here a table");
		comboTable.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				_tableName = comboTable.getItem(comboTable.getSelectionIndex());
				if (!_tableName.isEmpty()){
					java.util.List<String> columns = _dataBaseConnection.getColumnsName(_tableName);
					
					for (String columnName:columns){
						TableColumn tableColumn = new TableColumn(_dataBaseTable,SWT.NONE);
						tableColumn.setText(columnName);
					}
					
					_dataBaseConnection.connect();
					ResultSet result = _dataBaseConnection.get("Select * FROM "+_tableName+";");
					_dataBaseConnection.deconnect();
					_dataBaseTable.removeAll();
					try {
						while(result.next()){
							TableItem tableItem= new TableItem(_dataBaseTable, SWT.NONE);
							for (String columnName:columns){
								tableItem.setText(columns.indexOf(columnName), result.getString(columnName));
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
					for (TableColumn tableColumn:_dataBaseTable.getColumns()){
						tableColumn.pack();
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		//Bouton de cr�ation de la table
		final Button createTableBtn = new Button(selectTableBar,SWT.PUSH);
		createTableBtn.setText("Create a new Table");
		
		//Tableau de la base de donn�e
		_dataBaseTable = new Table(tableGroup,SWT.BORDER | SWT.V_SCROLL);
		_dataBaseTable.setHeaderVisible(true);
		_dataBaseTable.setLinesVisible(true);
		_dataBaseTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//Bouton compute pour lancer le calcul
		Composite btnBarre = new Composite(shell, SWT.RIGHT_TO_LEFT);
		btnBarre.setLayout(new GridLayout());
		btnBarre.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button btnCompute = new Button(btnBarre,SWT.PUSH);
		btnCompute.setText("Compute");
		btnCompute.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!_tableName.isEmpty() && !_minConf.isEmpty() && !_minSup.isEmpty()){
					Double minConf = Double.parseDouble(_minConf);
					Double minSup = Double.parseDouble(_minSup);
					
					if (minConf>=0 && minConf<=1 && minSup >= 0){
						try {
							_ra.getReglesAssociations(_tableName, Double.parseDouble(_minSup), Double.parseDouble(_minConf));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} //else
				}//else
			}
		});
		
		Group traceGrp = new Group (shell, SWT.NONE);
		traceGrp.setLayout(new FillLayout());
		traceGrp.setLayoutData(new GridData(GridData.FILL_BOTH));
		traceGrp.setText("Trace Log");
		
		_traceLog = new List(traceGrp, SWT.V_SCROLL);
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
		if (!display.readAndDispatch())
		display.sleep();
		}
		display.dispose();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		//Affichage des logs de l'observable
		if (arg1 instanceof String){
			_traceLog.add(((String) arg1));
		}
	}
}