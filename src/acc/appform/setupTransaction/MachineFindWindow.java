package acc.appform.setupTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
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
import com.vaadin.ui.Window.Notification;

public class MachineFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private HorizontalLayout hLayout=new HorizontalLayout();
	//private PopupDateField fromDate=new PopupDateField();
	//private PopupDateField toDate=new PopupDateField();
	//private Label lblFrom=new Label("Form:");
	//private Label lblTo=new Label("To:");
	private TextField txtReceiptId;
	private Label lblProductionType;
	private ComboBox cmbProductionType;

	CommonButton cButton = new CommonButton("", "", "", "","","Find","","","","");
	
	private Table table=new Table();

	private com.common.share.SessionBean sessionBean;
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public String MachineId="";
	public String receiptDept="";
	private String px="150px";

	private ArrayList<Label>Sl = new ArrayList<Label>();
	private ArrayList<Label>lblSl = new ArrayList<Label>();
	private ArrayList<Label> lblMachineName = new ArrayList<Label>();
	private ArrayList<Label> lblMachineModel = new ArrayList<Label>();
	private ArrayList<Label> lblPurchaDate= new ArrayList<Label>();
	private ArrayList<Label> lblSupplierName= new ArrayList<Label>();
	
	private ArrayList<Component> allComp = new ArrayList<Component>();

	private String frmName="";

	private DecimalFormat df = new DecimalFormat("#0.00");

	public MachineFindWindow(SessionBean sessionBean,TextField txtReceiptId,String frmName)
	{
		this.txtReceiptId=txtReceiptId;
		this.sessionBean=sessionBean;
		this.setCaption("MACHINE FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("600px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		focusEnter();
		productionTypeData();
		findButtonEvent("%");
	}

	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}
	private void productionTypeData() {
		cmbProductionType.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery("  select productTypeId,productTypeName from tbProductionType").list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbProductionType.addItem(element[0].toString());
				cmbProductionType.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}
	private void focusEnter()
	{
		//allComp.add(fromDate);
		//allComp.add(toDate);
		allComp.add(cButton.btnFind);
		new FocusMoveByEnter(this,allComp);
	}


	public void tableRowAdd(final int ar)
	{
		Sl.add(ar, new Label(""));
		Sl.get(ar).setWidth("100%");
		Sl.get(ar).setImmediate(true);
		Sl.get(ar).setHeight("12px");
		Sl.get(ar).setValue(ar+1);
		
		lblSl.add(ar, new Label(""));
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setImmediate(true);
		lblSl.get(ar).setHeight("12px");

		lblPurchaDate.add(ar, new Label(""));
		lblPurchaDate.get(ar).setWidth("100%");
		lblPurchaDate.get(ar).setImmediate(true);
		
		lblMachineName.add(ar, new Label(""));
		lblMachineName.get(ar).setWidth("100%");
		lblMachineName.get(ar).setImmediate(true);
		
		lblMachineModel.add(ar, new Label(""));
		lblMachineModel.get(ar).setWidth("100%");
		lblMachineModel.get(ar).setImmediate(true);
		
		lblSupplierName.add(ar, new Label(""));
		lblSupplierName.get(ar).setWidth("100%");
		lblSupplierName.get(ar).setImmediate(true);

		table.addItem(new Object[]{Sl.get(ar),lblSl.get(ar),lblMachineName.get(ar),lblMachineModel.get(ar),lblPurchaDate.get(ar),lblSupplierName.get(ar)},ar);
	}


	public void setEventAction()
	{
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				tableClear();
				if(cmbProductionType.getValue()!=null){
					findButtonEvent(cmbProductionType.getValue().toString());
				}
				else{
					//showNotification("Provide Production Type Please",Notification.TYPE_WARNING_MESSAGE);
					findButtonEvent("%");
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					MachineId=lblSl.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtReceiptId.setValue(MachineId);
					System.out.println("Find dept:"+MachineId);
					windowClose();
				}
			}
		});
	}

	private void windowClose()
	{
		this.close();
	}

	private void findButtonEvent(String type)
	{
		Transaction tx = null;
		String query = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			query ="select iAutoId,vMachineName,machineModel,convert(varchar,dPurchaseDate,105),vSupplierName from tbMachineInfo where " +
					" productionTypeId like '"+type+"' " +
							"order by  vSupplierName,vMachineName";

			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblSl.get(i).setValue(element[0]);
					lblMachineName.get(i).setValue(element[1]);
					lblMachineModel.get(i).setValue(element[2]);
					lblPurchaDate.get(i).setValue(element[3]);
					lblSupplierName.get(i).setValue(element[4]);

					if((i)==lblSl.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableClear();
				this.getParent().showNotification("No data Found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void compInit()
	{
		/*fromDate.setValue(new java.util.Date());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yyyy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);
		fromDate.setWidth(px);
		
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yyyy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);
		toDate.setWidth(px);*/
		lblProductionType=new Label();
		lblProductionType.setValue("Production Type :");
		lblProductionType.setWidth("-1px");
		lblProductionType.setHeight("-1px");
		//mainLayout.addComponent(lblProductionType,"top:20.0px;left:50.0px;");


		cmbProductionType=new ComboBox();
		cmbProductionType.setWidth("200px");
		cmbProductionType.setImmediate(true);
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		//mainLayout.addComponent(cmbProductionType, "top:17.0px;left:200.0px;");

		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL",20);
		
		table.addContainerProperty("SL.", Label.class, new Label());
		table.setColumnWidth("SL.",20);
		table.setColumnCollapsed("SL.", true);

		table.addContainerProperty("Machine Name", Label.class, new Label());
		table.setColumnWidth("Machine Name",150);
		
		table.addContainerProperty("Model", Label.class, new Label());
		table.setColumnWidth("Model",100);
		
		table.addContainerProperty("Purchase Date", Label.class, new Label());
		table.setColumnWidth("Purchase Date",80);

		table.addContainerProperty("Supplier Name", Label.class, new Label());
		table.setColumnWidth("Supplier Name",100);
		
		table.setColumnAlignments(new String[] { Table.ALIGN_CENTER,  Table.ALIGN_CENTER,Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_LEFT});	
	}

	private void compAdd()
	{
		hLayout.setSpacing(true);
		/*hLayout.addComponent(lblFrom);
		hLayout.addComponent(fromDate);*/
		hLayout.addComponent(lblProductionType);
		hLayout.addComponent(cmbProductionType);
		hLayout.addComponent(cButton.btnFind);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
	
	private void tableClear()
	{
		for(int i=0; i<lblMachineName.size(); i++)
		{
			lblSl.get(i).setValue("");
			lblPurchaDate.get(i).setValue("");
			lblMachineName.get(i).setValue("");
			lblSupplierName.get(i).setValue("");
		}
	}

}
