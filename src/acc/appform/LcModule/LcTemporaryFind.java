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
import com.common.share.TextRead;
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
public class LcTemporaryFind extends Window
{
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout hLayout = new HorizontalLayout();
	private PopupDateField fromDate = new PopupDateField();
	private PopupDateField toDate = new PopupDateField();
	private Label lblFrom = new Label("Form:");
	private Label lblTo = new Label("To:");
	private TextField txtLcNoForFind;

	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");

	private Table table = new Table();

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	private ComboBox cmbLcNo;

	public String lcId = "";
	public String lc = "";

	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<Label> tbLblDate = new ArrayList<Label>();
	private ArrayList<Label> tblblVoucherNo = new ArrayList<Label>();
	private ArrayList<Label> tbLblMrrNo = new ArrayList<Label>();
	private ArrayList<Label> tbLblLedgerId = new ArrayList<Label>();
	private ArrayList<Label> tbLblLcNo = new ArrayList<Label>();
	private ArrayList<TextRead> tbLblLcValueUSD = new ArrayList<TextRead>();
	private ArrayList<TextRead> tbLblLcValueBDT = new ArrayList<TextRead>();

	private ArrayList<Component> allComp = new ArrayList<Component>();
	
	private CommaSeparator cm = new CommaSeparator();

	public LcTemporaryFind(SessionBean sessionBean,TextField txtLcNo,String frmName)
	{
		this.txtLcNoForFind = txtLcNo;
		this.setCaption("L/C CLOSE FIND :: "+sessionBean.getCompany());
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

		focusEnter();
		lcNoLoad();
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

		tbLblDate.add(ar, new Label(""));
		tbLblDate.get(ar).setWidth("100%");
		tbLblDate.get(ar).setImmediate(true);
		tbLblDate.get(ar).setHeight("23px");

		tblblVoucherNo.add(ar, new Label(""));
		tblblVoucherNo.get(ar).setWidth("100%");
		tblblVoucherNo.get(ar).setImmediate(true);
		tblblVoucherNo.get(ar).setHeight("23px");

		tbLblMrrNo.add(ar, new Label(""));
		tbLblMrrNo.get(ar).setWidth("100%");
		tbLblMrrNo.get(ar).setImmediate(true);
		tbLblMrrNo.get(ar).setHeight("23px");

		tbLblLcNo.add(ar, new Label(""));
		tbLblLcNo.get(ar).setWidth("100%");
		tbLblLcNo.get(ar).setImmediate(true);
		tbLblLcNo.get(ar).setHeight("23px");
		
		tbLblLedgerId.add(ar, new Label(""));
		tbLblLedgerId.get(ar).setWidth("100%");
		tbLblLedgerId.get(ar).setImmediate(true);
		tbLblLedgerId.get(ar).setHeight("23px");

		tbLblLcValueUSD.add(ar, new TextRead(1));
		tbLblLcValueUSD.get(ar).setWidth("100%");
		tbLblLcValueUSD.get(ar).setImmediate(true);
		tbLblLcValueUSD.get(ar).setHeight("23px");
		
		tbLblLcValueBDT.add(ar, new TextRead(1));
		tbLblLcValueBDT.get(ar).setWidth("100%");
		tbLblLcValueBDT.get(ar).setImmediate(true);
		tbLblLcValueBDT.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblSl.get(ar),tbLblLedgerId.get(ar),tbLblLcNo.get(ar),tbLblMrrNo.get(ar),
				tblblVoucherNo.get(ar),tbLblDate.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					txtLcNoForFind.setValue(tbLblMrrNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					//txtLcNoForFind.setValue(tbLblLcNo.get().getValue().toString());
					windowClose();
				}
			}
		});

		/*cButton.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				
			}
		});*/
		cmbLcNo.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(cmbLcNo.getValue()!=null){
					tableDataAdding();
				}
				else{
					tableclear();
				}
			}
		});
	}

	private void windowClose()
	{
		this.close();
	}

	private void tableclear()
	{
		for(int i=0; i<lblSl.size(); i++)
		{
			tbLblLcNo.get(i).setValue("");
			tbLblLedgerId.get(i).setValue("");
			tbLblMrrNo.get(i).setValue("");
			tblblVoucherNo.get(i).setValue("");
			tbLblDate.get(i).setValue("");
			tbLblLcValueUSD.get(i).setValue("");
			tbLblLcValueBDT.get(i).setValue("");
		}
	}
	private void lcNoLoad(){
		String query = "select SupplierId,(select Ledger_Name from tbLedger where Ledger_Id=SupplierId)ledgername from tbRawPurchaseInfo where  transactionType like 'L / C'";
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			Iterator iter= session.createSQLQuery(query).list().iterator();
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				cmbLcNo.addItem(element[0]);
				cmbLcNo.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Error", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void tableDataAdding()
	{
		String query = "";

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			query ="select CONVERT(varchar(12),date,105) date,SupplierId,(select Ledger_Name from tbLedger where Ledger_Id=SupplierId)ledgername,VoucherNo,MrrNo from tbRawPurchaseInfo where SupplierId like '"+cmbLcNo.getValue()+"' ";

			System.out.println("Increment : "+query);
			List<?> list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					//cmbLcNo.setValue(element[3]);
					tbLblDate.get(i).setValue(element[0]);
					tblblVoucherNo.get(i).setValue(element[3]);
					tbLblMrrNo.get(i).setValue(element[4]);
					tbLblLedgerId.get(i).setValue(element[1]);
					tbLblLcNo.get(i).setValue(element[2]);
				
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
		cmbLcNo = new ComboBox();
		cmbLcNo.setImmediate(true);
		cmbLcNo.setWidth("270px");
		cmbLcNo.setHeight("-1px");
		cmbLcNo.setNullSelectionAllowed(true);
		cmbLcNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("265px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",20);
		
		table.addContainerProperty("Ledger Id", Label.class, new Label());
		table.setColumnWidth("Ledger Id",50);
		table.setColumnCollapsed("Ledger Id", true);
		
		table.addContainerProperty("L/C No", Label.class, new Label());
		table.setColumnWidth("L/C No",200);
		//table.setColumnCollapsed("L/C No", true);
		
		table.addContainerProperty("MRR No", Label.class, new Label());
		table.setColumnWidth("MRR No",120);
		
		table.addContainerProperty("Voucher No", Label.class, new Label());
		table.setColumnWidth("Voucher No",80);
		
		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",100);

		
		

		table.setColumnAlignments(new String[] { Table.ALIGN_CENTER, Table.ALIGN_CENTER,Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_CENTER,Table.ALIGN_CENTER});	
	}

	private void compAdd()
	{
		hLayout.setSpacing(true);
		//hLayout.addComponent(lblFrom);
		//hLayout.addComponent(fromDate);
		//hLayout.addComponent(lblTo);
		hLayout.addComponent(new Label("L/C No: "));
		hLayout.addComponent(cmbLcNo);
	//	hLayout.addComponent(cButton.btnFind);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}

	/*private void tableClear()
	{
		table.removeAllItems();
	}*/
}