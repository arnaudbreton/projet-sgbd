package swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.swt.widgets.Text;

public class Screen{
	public Screen(){
		
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
		
		Text minSupTxt = new Text(confGroup,SWT.SINGLE);
		minSupTxt.setText("");
		
		//Groupe de sélection de la table
		Group tableGroup = new Group(shell,SWT.NONE);
		tableGroup.setText("Selection table");
		tableGroup.setLayout(new GridLayout(2, false));
		tableGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		lbl = new Label(tableGroup,SWT.NONE);
		lbl.setText("Nom Table: ");
		
		Combo comboTable = new Combo(tableGroup, SWT.NONE);
		
		Table dataBaseTable = new Table(tableGroup,SWT.BORDER);
		dataBaseTable.setHeaderVisible(true);
		dataBaseTable.setLinesVisible(true);
		
		
		
		Composite btnBarre = new Composite(shell, SWT.None);
		btnBarre.setLayout(new GridLayout());
		btnBarre.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btnCompute = new Button(btnBarre,SWT.PUSH);
		btnCompute.setText("Compute");
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed())
		{
		if (!display.readAndDispatch())
		display.sleep();
		}
		display.dispose();
	}
}
