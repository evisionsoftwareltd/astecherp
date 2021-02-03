package acc.appform.LcModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;

import com.vaadin.data.Property.*;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LcFindWindowNew extends Window
{
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout hLayout = new HorizontalLayout();
	private PopupDateField fromDate = new PopupDateField();
	private PopupDateField toDate = new PopupDateField();
	private Label lblFrom = new Label("Form:");
	private Label lblTo = new Label("To:");
	private Label lblLCNo = new Label("LC NO:");
	private TextField txtLcNoForFind;

	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");

	private Table table = new Table();

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private ComboBox cmbPartyName,cmbLcNo;

	public String lcId = "";
	public String lc = "";

	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<Label> lblTransactionID = new ArrayList<Label>();
	private ArrayList<Label> lblLcNo = new ArrayList<Label>();
	private ArrayList<Label> lblLcBank = new ArrayList<Label>();
	private ArrayList<Label> lblLcAmount = new ArrayList<Label>();
	private ArrayList<Label> lblLcRefferenceNo = new ArrayList<Label>();

	private ArrayList<Component> allComp = new ArrayList<Component>();
	
	private CommaSeparator cm = new CommaSeparator();

	public LcFindWindowNew(SessionBean sessionBean,TextField txtLcNo,String frmName)
	{
		this.txtLcNoForFind = txtLcNo;
		this.setCaption("L/C FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("700px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");

		compInit();
		compAdd();
		tableInitialise();

		setEventAction();
		cmbLcNoLoad();
		focusEnter();
	}
	private void cmbLcNoLoad() {
		String query = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			query ="select distinct vTransactionID,vLcNo from tbLcOpeningInfo";
			List<?> list = session.createSQLQuery(query).list();
			Iterator iter=list.iterator();
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				cmbLcNo.addItem(element[0]);
				cmbLcNo.setItemCaption(element[0], element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}


	public void tableInitialise()
	{
		for(int i=0;i<8;i++)
		{
			tableRowAdd(i);
		}
	}

	private void focusEnter()
	{
		allComp.add(fromDate);
		allComp.add(toDate);

		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	public void tableRowAdd(final int ar)
	{
		lblSl.add(ar, new Label(""));
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setImmediate(true);
		lblSl.get(ar).setHeight("23px");
		lblSl.get(ar).setValue(ar+1);

		lblTransactionID.add(ar, new Label(""));
		lblTransactionID.get(ar).setWidth("100%");
		lblTransactionID.get(ar).setImmediate(true);
		lblTransactionID.get(ar).setHeight("23px");

		lblLcNo.add(ar, new Label(""));
		lblLcNo.get(ar).setWidth("100%");
		lblLcNo.get(ar).setImmediate(true);
		lblLcNo.get(ar).setHeight("23px");

		lblLcBank.add(ar, new Label(""));
		lblLcBank.get(ar).setWidth("100%");
		lblLcBank.get(ar).setImmediate(true);
		lblLcBank.get(ar).setHeight("23px");

		lblLcAmount.add(ar, new Label(""));
		lblLcAmount.get(ar).setWidth("100%");
		lblLcAmount.get(ar).setImmediate(true);
		lblLcAmount.get(ar).setHeight("23px");

		lblLcRefferenceNo.add(ar, new Label(""));
		lblLcRefferenceNo.get(ar).setWidth("100%");
		lblLcRefferenceNo.get(ar).setImmediate(true);
		lblLcRefferenceNo.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblSl.get(ar),lblTransactionID.get(ar),lblLcNo.get(ar),lblLcBank.get(ar),lblLcAmount.get(ar),lblLcRefferenceNo.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					txtLcNoForFind.setValue(lblTransactionID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					windowClose();
				}
			}
		});

		cButton.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				tableDataAdding();
			}
		});
		cmbLcNo.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				tableclear();
				if(cmbLcNo.getValue()!=null){
					tableDataLoadFromCombo();
				}
			}
		});
	}
	private void tableDataLoadFromCombo(){
		String query = "";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			query ="select vTransactionID,vLcNo,vLcOpeningBank,mLCAmountBDT,vBbRefferenceNo from tbLcOpeningInfo where vTransactionID='"+cmbLcNo.getValue()+"'";

			System.out.println("Increment : "+query);
			List<?> list = session.createSQLQuery(query).list();
			Iterator iter=list.iterator();
			if(!list.isEmpty())
			{						  
					Object[] element = (Object[]) iter.next();
					lblTransactionID.get(0).setValue(element[0]);
					lblLcNo.get(0).setValue(element[1]);
					lblLcBank.get(0).setValue(element[2]);
					lblLcAmount.get(0).setValue(cm.setComma(Double.parseDouble(element[3].toString())));
					lblLcRefferenceNo.get(0).setValue(element[4]);
			}
			else {
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) 
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void windowClose()
	{
		this.close();
	}

	private void tableclear()
	{
		for(int i=0; i<lblSl.size(); i++)
		{
			lblLcNo.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		String query = "";

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			query ="select vTransactionID,vLcNo,vLcOpeningBank,mLCAmountBDT,vBbRefferenceNo from tbLcOpeningInfo where Convert(date,dLcOpeningDate)" +
					" between '"+dateformat.format(fromDate.getValue())+"' and '"+dateformat.format(toDate.getValue())+"'";

			System.out.println("Increment : "+query);
			List<?> list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					lblTransactionID.get(i).setValue(element[0]);
					lblLcNo.get(i).setValue(element[1]);
					lblLcBank.get(i).setValue(element[2]);
					lblLcAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[3].toString())));
					lblLcRefferenceNo.get(i).setValue(element[4]);

					if((i)==lblSl.size()-1) {
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else {
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) 
		{
			showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void compInit()
	{
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("270px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setNullSelectionAllowed(false);
		
		cmbLcNo = new ComboBox();
		cmbLcNo.setImmediate(true);
		cmbLcNo.setWidth("200px");
		cmbLcNo.setHeight("-1px");
		cmbLcNo.setNullSelectionAllowed(true);
		cmbLcNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setWidth("110px");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);

		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setWidth("110px");
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);

		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("265px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",20);

		table.addContainerProperty("Transaction NO", Label.class, new Label());
		table.setColumnWidth("Transaction NO",100);

		table.addContainerProperty("L/C NO", Label.class, new Label());
		table.setColumnWidth("L/C NO",100);

		table.addContainerProperty("L/C OPENING BANK", Label.class, new Label());
		table.setColumnWidth("L/C OPENING BANK",200);

		table.addContainerProperty("L/C AMOUNT", Label.class, new Label());
		table.setColumnWidth("L/C AMOUNT",120);

		table.addContainerProperty("REFFERENCE NO", Label.class, new Label());
		table.setColumnWidth("REFFERENCE NO",140);
		
		table.setColumnCollapsed("Transaction NO", true);

		table.setColumnAlignments(new String[] { Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_CENTER, 
				Table.ALIGN_LEFT,Table.ALIGN_CENTER});	
	}

	private void compAdd()
	{
		hLayout.setSpacing(true);
		hLayout.addComponent(lblFrom);
		hLayout.addComponent(fromDate);
		hLayout.addComponent(lblTo);
		hLayout.addComponent(toDate);
		hLayout.addComponent(cButton.btnFind);
		hLayout.addComponent(lblLCNo);
		hLayout.addComponent(cmbLcNo);
		
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}

	/*private void tableClear()
	{
		table.removeAllItems();
	}*/
}