package swt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import regle_association.GenerateurTable;
import regle_association.RechercheRegleAssociation;
import regle_association.objets_metiers.ItemSet;
import regle_association.objets_metiers.RegleAssociation;

public class Screen implements Observer{
	private String _tableName = "";
	private String _minConf = "";
	private String _minSup = "";
	
	private Table _dataBaseTable;
	private Text _traceLog;
	private Button _createTableBtn;
	
	private MysqlJDBC _dataBaseConnection; 
	private RechercheRegleAssociation _ra;
	private Table _itemSetsTable;
	private Table _reglesTable;
	protected String _newTableName;
	protected String _colQty;
	protected String _linesQty;
	private Button _btnCompute;
	
	public Screen(){
			_dataBaseConnection = MysqlJDBC.getInstance();
			_ra = new RechercheRegleAssociation();
			_ra.addObserver(this);
	}
	
	public void createContent(){
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));
		shell.setText("Algorithme de calcul de règles d'associations (C) Arnaud Breton & Florian Gouin");
		
		Composite leftPannel = new Composite(shell, SWT.NONE);
		leftPannel.setLayoutData(new GridData(GridData.FILL_BOTH));
		leftPannel.setLayout(new GridLayout());
		Composite rightPannel = new Composite(shell, SWT.NONE);
		rightPannel.setLayoutData(new GridData(GridData.FILL_BOTH));
		rightPannel.setLayout(new GridLayout());
		
		Label lbl;
		
		//Groupe Configuration
		Group confGroup = new Group(leftPannel,SWT.NONE);
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
				_btnCompute.setEnabled(_minSup!=null && _minConf!=null && !_minConf.isEmpty() && !_minSup.isEmpty());
			}
		});
		minConfTxt.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				minConfTxt.selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				minConfTxt.setText(minConfTxt.getText().replace(",", "."));
				if (!minConfTxt.getText().isEmpty()){
					float value = Float.parseFloat(minConfTxt.getText());
					if (value <0)
						minConfTxt.setText("0");
					else if (value > 1)
						minConfTxt.setText("1");
				}
			}
		});
		minConfTxt.addVerifyListener(new VerifyListener(){
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isISOControl(e.character)
				|| Character.isDigit(e.character)
				|| e.character == '\b'
				|| e.character == '.'
				|| e.character == ',';
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
				_btnCompute.setEnabled(_minSup!=null && _minConf!=null && !_minConf.isEmpty() && !_minSup.isEmpty());
			}
		});
		minSupTxt.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				minSupTxt.selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				minSupTxt.setText(minSupTxt.getText().replace(",", "."));
				if (!minSupTxt.getText().isEmpty()){
					float value = Float.parseFloat(minSupTxt.getText());
					if (value <0)
						minSupTxt.setText("0");
					else if (value > 1)
						minSupTxt.setText("1");
				}
			}
		});
		minSupTxt.addVerifyListener(new VerifyListener(){
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isISOControl(e.character)
				|| Character.isDigit(e.character)
				|| e.character == '\b'
				|| e.character == '.'
				|| e.character == ',';
			}
		});
		
		//Groupe de sélection de la table
		Group tableGroup = new Group(leftPannel,SWT.NONE);
		tableGroup.setText("Table selection");
		tableGroup.setLayout(new GridLayout(1, false));
		tableGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//Barre de sélection/création de la table à utiliser
		Composite selectTableBar = new Composite(tableGroup,SWT.NONE);
		selectTableBar.setLayout(new GridLayout(2,false));
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
					List<String> columns = _dataBaseConnection.getColumnsName(_tableName);
					
					for (String columnName:columns){
						TableColumn tableColumn = new TableColumn(_dataBaseTable,SWT.NONE);
						tableColumn.setText(columnName);
					}
					
					ResultSet result = _dataBaseConnection.get("Select * FROM "+_tableName+";");
					
					_dataBaseTable.clearAll();
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
		
		//Zone de création de table
		Composite createTableComposite = new Composite(selectTableBar,SWT.BORDER);
		createTableComposite.setLayout(new GridLayout(7,false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan=2;
		createTableComposite.setLayoutData(gridData);
		
		//Nom de la table à créer
		lbl = new Label(createTableComposite,SWT.NONE);
		lbl.setText("New Table Name: ");
		
		final Text tableName = new Text(createTableComposite,SWT.SINGLE);
		tableName.setText("");
		tableName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				if (!tableName.getText().isEmpty()){
					_newTableName = tableName.getText();
				}
				_createTableBtn.setEnabled(_linesQty!= null && _colQty!=null && _newTableName!=null && (!_linesQty.isEmpty() && !_colQty.isEmpty() && !_newTableName.isEmpty()));
			}
		});
		tableName.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				tableName.selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
		});
		tableName.addVerifyListener(new VerifyListener(){
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isISOControl(e.character) || Character.isLetter(e.character)
				|| Character.isDigit(e.character)
				|| Character.isSpaceChar(e.character) || e.character == '\b'
				|| e.character == '_';
			}
		});
		
		//Nombre de colonnes
		lbl = new Label(createTableComposite,SWT.NONE);
		lbl.setText("Columns qty: ");
		
		final Text colQty = new Text(createTableComposite,SWT.SINGLE);
		colQty.setText("");
		colQty.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				if (!colQty.getText().isEmpty()){
					_colQty = colQty.getText();
				}
				_createTableBtn.setEnabled(_linesQty!= null && _colQty!=null && _newTableName!=null && (!_linesQty.isEmpty() && !_colQty.isEmpty() && !_newTableName.isEmpty()));
			}
		});
		colQty.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				colQty.selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
		});
		colQty.addVerifyListener(new VerifyListener(){
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isDigit(e.character) || Character.isISOControl(e.character) || e.character == '\b';
			}
		});
		
		//Nombre de lignes
		lbl = new Label(createTableComposite,SWT.NONE);
		lbl.setText("Lines qty: ");
		
		final Text linesQty = new Text(createTableComposite,SWT.SINGLE);
		linesQty.setText("");
		linesQty.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				if (!linesQty.getText().isEmpty()){
					_linesQty = linesQty.getText();
				}
				_createTableBtn.setEnabled(_linesQty!= null && _colQty!=null && _newTableName!=null && (!_linesQty.isEmpty() && !_colQty.isEmpty() && !_newTableName.isEmpty()));
			}
		});
		linesQty.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent arg0) {
				linesQty.selectAll();
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
		});
		linesQty.addVerifyListener(new VerifyListener(){
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isDigit(e.character) || Character.isISOControl(e.character) || e.character == '\b';
			}
		});
		
		//Bouton de création de la table
		_createTableBtn = new Button(createTableComposite,SWT.PUSH);
		_createTableBtn.setText("Create a new Table");
		_createTableBtn.setEnabled(false);
		_createTableBtn.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				 try {
					GenerateurTable.generateTable(_newTableName, Integer.valueOf(_colQty), Integer.valueOf(_linesQty));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				comboTable.setItems(_dataBaseConnection.getTablesNames());
				comboTable.select(comboTable.indexOf(_newTableName));
			}
		});
		
		//Tableau de la base de donnée
		_dataBaseTable = new Table(tableGroup,SWT.BORDER | SWT.V_SCROLL);
		_dataBaseTable.setHeaderVisible(true);
		_dataBaseTable.setLinesVisible(true);
		_dataBaseTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		//Bouton compute pour lancer le calcul
		Composite btnBarre = new Composite(leftPannel, SWT.RIGHT_TO_LEFT);
		btnBarre.setLayout(new GridLayout());
		btnBarre.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_btnCompute = new Button(btnBarre,SWT.PUSH);
		_btnCompute.setText("Compute");
		_btnCompute.setEnabled(false);
		_btnCompute.addSelectionListener(new SelectionListener(){
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
							List<ItemSet> itemsSets = _ra.getAttributsFrequents(_tableName, Double.parseDouble(_minSup));
							_itemSetsTable.clearAll();
							_reglesTable.clearAll();
							
							for (ItemSet itemSet:itemsSets){
								TableItem tableItem = new TableItem(_itemSetsTable, SWT.NONE);
								tableItem.setText(0, itemSet.getNom());
								tableItem.setText(1, String.valueOf(itemSet.getSupport()));
							}
							for (TableColumn tableColumn:_itemSetsTable.getColumns()){
								tableColumn.pack();
							}
							
							List<RegleAssociation> reglesAssociations  = _ra.getReglesAssociations(_tableName, Double.parseDouble(_minConf));
							for (RegleAssociation regleAssociation:reglesAssociations){
								TableItem tableItem = new TableItem(_reglesTable, SWT.NONE);
								tableItem.setText(0, regleAssociation.toString());
								tableItem.setText(1, String.valueOf(regleAssociation.getConfiance()));
							}
							for (TableColumn tableColumn:_reglesTable.getColumns()){
								tableColumn.pack();
							}
							
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} //else
				}//else
			}
		});
		
		Group traceGrp = new Group (leftPannel, SWT.NONE);
		traceGrp.setLayout(new GridLayout());
		traceGrp.setLayoutData(new GridData(GridData.FILL_BOTH));
		traceGrp.setText("Trace Log");
		
		_traceLog = new Text(traceGrp, SWT.MULTI | SWT.V_SCROLL);
		_traceLog.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		//Panneau de droite
		Group resultGroup = new Group(rightPannel,SWT.NONE);
		resultGroup.setLayout(new GridLayout(2,false));
		resultGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		resultGroup.setText("Result");

		TableColumn column;
		
		//Création du tableau de résultat des itemSets
		_itemSetsTable = new Table(resultGroup,SWT.BORDER | SWT.V_SCROLL);
		_itemSetsTable.setLinesVisible(true);
		_itemSetsTable.setHeaderVisible(true);
		_itemSetsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		column = new TableColumn(_itemSetsTable,SWT.NONE);
		column.setText("ItemsSets");
		column = new TableColumn(_itemSetsTable,SWT.NONE);
		column.setText("Supports");
		
		for (TableColumn tableColumn:_itemSetsTable.getColumns()){
			tableColumn.pack();
		}
		
		//Création du tableau de résultat des règles
		_reglesTable = new Table(resultGroup,SWT.BORDER | SWT.V_SCROLL);
		_reglesTable.setLinesVisible(true);
		_reglesTable.setHeaderVisible(true);
		_reglesTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		column = new TableColumn(_reglesTable,SWT.NONE);
		column.setText("Association rules");
		column = new TableColumn(_reglesTable,SWT.NONE);
		column.setText("Confidences");
		
		for (TableColumn tableColumn:_reglesTable.getColumns()){
			tableColumn.pack();
		}
		
		shell.pack();
		shell.open();
		shell.setSize(1100, 700);
		while (!shell.isDisposed()){
			if (!display.readAndDispatch())
			display.sleep();
		}
		display.dispose();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		//Affichage des logs de l'observable
		if (arg1 instanceof String){
			_traceLog.setText(_traceLog.getText()+System.getProperty("line.separator")+((String) arg1));
		}
	}
}
