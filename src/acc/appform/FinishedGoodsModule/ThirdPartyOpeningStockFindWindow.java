package acc.appform.FinishedGoodsModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class ThirdPartyOpeningStockFindWindow extends Window
{
	private AbsoluteLayout mainLayout;
	private TextField txtTransectionId;
	private Table table=new Table();
	public String transectionId = "";
	
	private Label lblYear;
	private InlineDateField dYear;
	
	private Label lblOwnerNameParty=new Label("Owner Name (party): ");
	private ComboBox cmbOwnerNameParty=new ComboBox() ;
	private CheckBox chkOwnerNameParty=new CheckBox("ALL");
	
	private Label lblSupplierNameSource=new Label("Supplier Name (Source): ");
	private ComboBox cmbSupplierNameSource=new ComboBox() ;
	private CheckBox chkSuppliernameSource=new CheckBox("ALL");
	
	
	
	//private ArrayList<Label> tblblDate = new ArrayList<Label>();
	private ArrayList<Label> tblblTransectionId = new ArrayList<Label>();
	private ArrayList<Label> tblbItemCode = new ArrayList<Label>();
	private ArrayList<Label> tblblItemNameDeliveryStage = new ArrayList<Label>();
	private ArrayList<Label> tblblItemNameRecevingStage = new ArrayList<Label>();
	private ArrayList<Label> tblblUnit= new ArrayList<Label>();
	private ArrayList<Label> tblblRate = new ArrayList<Label>();
	private ArrayList<Label> tblblQty= new ArrayList<Label>(); 
	private ArrayList<Label> tblblAmount= new ArrayList<Label>(); 
	
	
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private Label lblCountProduct=new Label();

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;

	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yyyy");
	
	private SimpleDateFormat dYearFormat=new SimpleDateFormat("yyyy");
	
	private DecimalFormat df = new DecimalFormat("#0.00");

	private DecimalFormat dacimalf = new DecimalFormat("#0");

	
	public ThirdPartyOpeningStockFindWindow(SessionBean sessionBean,TextField txtTransectionId)
	{
		this.txtTransectionId = txtTransectionId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND THIRD PARTY INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("910px");
		this.setHeight("650px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		setContent(buildMainLayout());
		setEventAction();
		cmbPartyNameLoad();
		//cmbAddCategoryData();
	}
	
	public void setEventAction()
	{
		cmbOwnerNameParty.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbOwnerNameParty.getValue()!=null)
				{
					SourceNameLoad(cmbOwnerNameParty.getValue().toString());
				}
				else{
					cmbSupplierNameSource.removeAllItems();
				}
			}
		});

		chkOwnerNameParty.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(chkOwnerNameParty.booleanValue()==true){
					   cmbOwnerNameParty.setValue(null);			
					   cmbOwnerNameParty.setEnabled(false);
					   SourceNameLoad("%");
				}
				else{
					cmbOwnerNameParty.setEnabled(true);
				}
			}
		});
		
		cmbSupplierNameSource.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbSupplierNameSource.getValue()!=null)
				{
					tableclear();
					tableDataAdding(cmbSupplierNameSource.getValue().toString());
				}
				else{
					tableclear();
				}
			}
		});

		chkSuppliernameSource.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(chkSuppliernameSource.booleanValue()==true){
					cmbSupplierNameSource.setValue(null);			
					cmbSupplierNameSource.setEnabled(false);
					   tableDataAdding("%");
				}
				else{
					cmbSupplierNameSource.setEnabled(true);
				}
			}
		});
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					transectionId = tblblTransectionId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtTransectionId.setValue(transectionId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<tblbItemCode.size(); i++)
		{
			tblblTransectionId.get(i).setValue("");

			tblbItemCode.get(i).setValue("");
			tblblItemNameDeliveryStage.get(i).setValue("");
			tblblItemNameRecevingStage.get(i).setValue("");
	
			tblblUnit.get(i).setValue("");
			tblblQty.get(i).setValue("");
			tblblAmount.get(i).setValue("");

			tblblRate.get(i).setValue("");
		}
	}

	private void tableDataAdding(String Sourceid)
	{
		Transaction tx = null;
		String query = "",partyid="";
		try
		{
			if(cmbOwnerNameParty.getValue()!=null){
				partyid=cmbOwnerNameParty.getValue().toString();
			}
			if(chkOwnerNameParty.booleanValue()){
				partyid="%";
			}
			Session session = SessionFactoryUtil.getInstance().openSession();
			query= "select iTransectionId,vItemId, vItemNameDeliveryStage, vItemNameReceivingStage, vUnit, "
					+ "mqty, mRate, mAmount from tb3rdPartyItemOpening where "
					+ "vOwnerNamePartyId like '"+partyid+"' and vSupplierNameSourceId like '"+Sourceid+"'"; 
			System.out.println("query"+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tblblTransectionId.get(i).setValue(element[0].toString());
					
					tblbItemCode.get(i).setValue(element[1].toString());
					tblblItemNameDeliveryStage.get(i).setValue(element[2].toString());
					tblblItemNameRecevingStage.get(i).setValue(element[3].toString());

					tblblUnit.get(i).setValue(element[4].toString());
					tblblQty.get(i).setValue(dacimalf.format(element[5]));
					tblblRate.get(i).setValue(df.format(element[6]));
					tblblAmount.get(i).setValue(df.format(element[7]));
					
					if(i==tblbItemCode.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data Found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) {
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void windowClose()
	{
		this.close();
	}

	public void SourceNameLoad(String partyid)
	{
		cmbSupplierNameSource.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select distinct vSupplierNameSourceId,vSupplierNameSource from tb3rdPartyItemOpening where vOwnerNamePartyId like '"+partyid+"'").list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSupplierNameSource.addItem(element[0].toString());
				cmbSupplierNameSource.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public void cmbPartyNameLoad()
	{
		cmbOwnerNameParty.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select distinct vOwnerNamePartyId,vOwnerNameParty from tb3rdPartyItemOpening").list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbOwnerNameParty.addItem(element[0].toString());
				cmbOwnerNameParty.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private AbsoluteLayout buildMainLayout()
	{		
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		

		lblYear = new Label("Opening Year: ");
		lblYear.setImmediate(true);
		lblYear.setWidth("100.0%");
		lblYear.setHeight("18px");
		mainLayout.addComponent(lblYear,"top:20.0px;left:50.0px;");
		
		dYear = new InlineDateField();
		dYear.setImmediate(true);
		dYear.setDateFormat("yyyy");
		dYear.setWidth("-1px");
		dYear.setHeight("-1px");
		dYear.setInvalidAllowed(false);
		dYear.setResolution(6);
		mainLayout.addComponent(dYear, "top:18.0px;left:200.0px;");
	
		lblOwnerNameParty.setImmediate(true);
		lblOwnerNameParty.setWidth("100.0%");
		lblOwnerNameParty.setHeight("18px");
		mainLayout.addComponent(lblOwnerNameParty,"top:50.0px;left:50.0px;");
		
		cmbOwnerNameParty = new ComboBox();
		cmbOwnerNameParty.setImmediate(true);
		cmbOwnerNameParty.setWidth("318px");
		cmbOwnerNameParty.setHeight("24px");
		cmbOwnerNameParty.setNullSelectionAllowed(true);
		cmbOwnerNameParty.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbOwnerNameParty, "top:48.0px;left:200.0px;");
		
		chkOwnerNameParty=new CheckBox("All");
		chkOwnerNameParty.setImmediate(true);
		chkOwnerNameParty.setValue(false);
		mainLayout.addComponent(chkOwnerNameParty, "top:50.0px;left:520.0px;");
		
		lblSupplierNameSource.setImmediate(true);
		lblSupplierNameSource.setWidth("100.0%");
		lblSupplierNameSource.setHeight("18px");
		mainLayout.addComponent(lblSupplierNameSource,"top:80.0px;left:50.0px;");
		
		cmbSupplierNameSource = new ComboBox();
		cmbSupplierNameSource.setImmediate(true);
		cmbSupplierNameSource.setWidth("318px");
		cmbSupplierNameSource.setHeight("24px");
		cmbSupplierNameSource.setNullSelectionAllowed(true);
		cmbSupplierNameSource.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSupplierNameSource, "top:78.0px;left:200.0px;");
		
		chkSuppliernameSource=new CheckBox("All");
		chkSuppliernameSource.setImmediate(true);
		chkSuppliernameSource.setValue(false);
		mainLayout.addComponent(chkSuppliernameSource, "top:80.0px;left:520.0px;");

		//table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("90%");

		table.setImmediate(true); // react at once when something is selected
		//table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Transection Id", Label.class, new Label());
		table.setColumnWidth("Transection Id",60);
		table.setColumnCollapsed("Transection Id", true);

		
		table.addContainerProperty("Item Code", Label.class, new Label());
		table.setColumnWidth("Item Code",60);
		table.setColumnAlignment("Item Code", table.ALIGN_CENTER);
		table.setColumnCollapsed("Item Code", true);

		table.addContainerProperty("Item Name(Delivery stage)", Label.class, new Label());
		table.setColumnWidth("Item Name(Delivery stage)",280);

		
		table.addContainerProperty("Item Name(Receiving Stage)", Label.class, new Label());
		table.setColumnWidth("Item Name(Receiving Stage)",280);
		
		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",45);
		table.setColumnAlignment("Unit", table.ALIGN_CENTER);
		
		table.addContainerProperty("Qty", Label.class, new Label());
		table.setColumnWidth("Qty",55);
		table.setColumnAlignment("Qty", table.ALIGN_CENTER);
		
		table.addContainerProperty("Rate", Label.class, new Label());
		table.setColumnWidth("Rate",50);
		table.setColumnAlignment("Rate", table.ALIGN_RIGHT);
		
		table.addContainerProperty("Amount", Label.class, new Label());
		table.setColumnWidth("Amount",55);
		table.setColumnAlignment("Amount", table.ALIGN_CENTER);
		
		tableInitialise();
		
		//mainLayout.addComponent(cButton, "top:80px; left:120px;");
		mainLayout.addComponent(table, "top:150px; left:5px;");
		return mainLayout;
	}
	
	public void tableInitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		String size="-1px";
		
		tblblTransectionId.add(ar, new Label(""));
		tblblTransectionId.get(ar).setImmediate(true);



		
		tblbItemCode.add(ar, new Label(""));

		tblbItemCode.get(ar).setImmediate(true);


		tblblItemNameDeliveryStage.add(ar, new Label(""));
	
		tblblItemNameDeliveryStage.get(ar).setImmediate(true);

		tblblItemNameRecevingStage.add(ar, new Label(""));

		tblblItemNameRecevingStage.get(ar).setImmediate(true);


	
		
		tblblUnit .add(ar, new Label(""));

		tblblUnit.get(ar).setImmediate(true);

		
		tblblRate .add(ar, new Label(""));

		tblblRate.get(ar).setImmediate(true);

		
		tblblQty .add(ar, new Label(""));

		tblblQty.get(ar).setImmediate(true);

		
		tblblAmount .add(ar, new Label(""));

		tblblAmount.get(ar).setImmediate(true);
		
		

		
		table.addItem(new Object[]{tblblTransectionId.get(ar),tblbItemCode.get(ar),
				tblblItemNameDeliveryStage.get(ar),tblblItemNameRecevingStage.get(ar),tblblUnit.get(ar),
				tblblQty.get(ar), tblblRate.get(ar),tblblAmount.get(ar)},ar);
	}
	
}