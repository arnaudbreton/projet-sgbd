package swt;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Screen{
	public Screen(){
		
	}
	
	public void createContent(){
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(2, false));
		
		Label lbl;
		
		//Groupe Configuration
		Group confGroup = new Group(shell,SWT.NONE);
		confGroup.setText("Configuration");
		confGroup.setLayout(new GridLayout(2, false));
		
		lbl = new Label(confGroup,SWT.NONE);
		lbl.setText("Min conf: ");
		
		Text minConfTxt = new Text(confGroup,SWT.SINGLE);
		minConfTxt.setText("");
		
		lbl = new Label(confGroup,SWT.NONE);
		lbl.setText("Min sup: ");
		
		Text minSupTxt = new Text(confGroup,SWT.SINGLE);
		minSupTxt.setText("");
		
		//Groupe de sélection de la table
		Group tableGroup = new Group(shell,SWT.NONE);
		tableGroup.setText("Selection table");
		tableGroup.setLayout(new GridLayout(2, false));
		
		lbl = new Label(tableGroup,SWT.NONE);
		lbl.setText("Nom Table: ");
		
		Combo comboTable = new Combo(tableGroup, SWT.NONE);
		
		TableViewer dataBaseTable = new TableViewer(tableGroup);
		dataBaseTable.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				viewer.refresh();
			}

			@Override
			public void dispose() {
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return null;
			}
		});
		
		dataBaseTable.setLabelProvider(new ITableLabelProvider() {
			
			@Override
			public void removeListener(ILabelProviderListener listener) {
			}
			
			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			
			@Override
			public void dispose() {
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
			}
			
			@Override
			public String getColumnText(Object element, int columnIndex) {
				return null;
			}
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
		});
		
		
		
		shell.pack();
		shell.open();
		shell.setSize(640, 480);
		while (!shell.isDisposed())
		{
		if (!display.readAndDispatch())
		display.sleep();
		}
		display.dispose();
	}
}
