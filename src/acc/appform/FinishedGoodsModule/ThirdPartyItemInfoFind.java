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
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class ThirdPartyItemInfoFind extends Window
{
	private AbsoluteLayout mainLayout;
	private TextField txtTransectionId;
	private Table table=new Table();
	public String transectionId = "";
	
	/*private Label lblDate;
	private PopupDateField dDate;*/
	
	private Label lblPartyId=new Label("Party Id : ");
	private ComboBox cmbPartyName=new ComboBox() ;
	private CheckBox chkAllPartyName=new CheckBox("ALL");
	
	private Label lblProductName=new Label("Product Name : ");
	private ComboBox cmbProductName=new ComboBox() ;
	private CheckBox chkAllProductNme=new CheckBox("ALL");
	
	
	
	private ArrayList<Label> tblblDate = new ArrayList<Label>();
	private ArrayList<Label> tblblTransectionId = new ArrayList<Label>();
	private ArrayList<Label> tblblLabelCode = new ArrayList<Label>();
	private ArrayList<Label> tblblLabelName = new ArrayList<Label>();
	private ArrayList<Label> tblblLabelNamesource = new ArrayList<Label>();
	private ArrayList<Label> tblblSource = new ArrayList<Label>();
	private ArrayList<Label> tblblUnit= new ArrayList<Label>();
	private ArrayList<Label> tblblMax = new ArrayList<Label>();
	private ArrayList<Label> tblblRate = new ArrayList<Label>();
	private ArrayList<Label> tblblMin= new ArrayList<Label>(); 
	private ArrayList<Label> tblblRefill= new ArrayList<Label>(); 
	
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private Label lblCountProduct=new Label();

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;

	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yyyy");
	
	private DecimalFormat df = new DecimalFormat("#0.00");

	private DecimalFormat dacimalf = new DecimalFormat("#0");

	
	public ThirdPartyItemInfoFind(SessionBean sessionBean,TextField txtTransectionId)
	{
		this.txtTransectionId = txtTransectionId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND THIRD PARTY INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("910px");
		this.setHeight("750px");
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
		cmbPartyName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbPartyName.getValue()!=null)
				{
					tableclear();
					cmbProductnameLoad(cmbPartyName.getValue().toString());
				}
				else{
					tableclear();
				}
			}
		});
		
		chkAllPartyName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(chkAllPartyName.booleanValue()==true){
					   cmbPartyName.setValue(null);			
					   cmbPartyName.setEnabled(false);
					   tableclear();
					   cmbProductnameLoad("%");
				}
				else{
					cmbPartyName.setEnabled(true);
				}
			}
		});
		
		
		cmbProductName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbProductName.getValue()!=null)
				{
					tableclear();
					tableDataAdding(cmbProductName.getValue().toString());
				}
				else{
					tableclear();
				}
			}
		});
		
		chkAllProductNme.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(chkAllProductNme.booleanValue()==true){
					   cmbProductName.setValue(null);			
					   cmbProductName.setEnabled(false);
					   tableclear();
					   tableDataAdding("%");
				}
				else{
					cmbProductName.setEnabled(true);
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
		for(int i=0; i<tblblLabelCode.size(); i++)
		{
			tblblTransectionId.get(i).setValue("");
			tblblDate.get(i).setValue("");
			tblblLabelCode.get(i).setValue("");
			tblblLabelName.get(i).setValue("");
			tblblLabelNamesource.get(i).setValue("");
			tblblSource.get(i).setValue("");
			tblblUnit.get(i).setValue("");
			tblblMax.get(i).setValue("");
			tblblMin.get(i).setValue("");
			tblblRefill.get(i).setValue("");
			tblblRate.get(i).setValue("");
		}
	}

	private void tableDataAdding(String productId)
	{
		Transaction tx = null;
		String query = "";
		String partyid="%";
		try
		{
			if(cmbPartyName.getValue()!=null){
				partyid=cmbPartyName.getValue().toString();
			}
			Session session = SessionFactoryUtil.getInstance().openSession();
			query= "select iTransectionId, dDate, vLabelCode, vLabelName, vSourceName, vUnit, "
					+ "mMax, mMin, mRefill,mthirdPartyItemRate,isnull(vlabelNamesource,'')vlabelNamesource,vProductCode  from tb3rdPartylabelInformation where vPartyId like '"+partyid+"' "
					+ " and vLabelCode like '"+productId+"' and isActive like '1'"; 
			System.out.println("query"+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tblblTransectionId.get(i).setValue(element[0].toString());
					tblblDate.get(i).setValue(dFormat.format(element[1]));
					tblblLabelCode.get(i).setValue(element[11].toString());
					tblblLabelName.get(i).setValue(element[3].toString());
					tblblLabelNamesource.get(i).setValue(element[10].toString());
					tblblSource.get(i).setValue(element[4].toString());
					tblblUnit.get(i).setValue(element[5].toString());
					tblblMax.get(i).setValue(dacimalf.format(element[6]));
					tblblMin.get(i).setValue(dacimalf.format(element[7]));
					tblblRefill.get(i).setValue(dacimalf.format(element[8]));
					tblblRate.get(i).setValue(df.format(element[9]));
					
					if(i==tblblLabelCode.size()-1)
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

	public void cmbProductnameLoad(String partyid)
	{
		cmbProductName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vLabelCode,vlabelName from tb3rdPartylabelInformation "
					+ "where vPartyId like '"+partyid+"' and isActive like '1'").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProductName.addItem(element[0].toString());
				cmbProductName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	public void cmbPartyNameLoad()
	{
		cmbPartyName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vPartyId,vPartyName from tb3rdPartylabelInformation where isActive like '1'").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
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
		

		/*lblDate = new Label("Date: ");
		lblDate.setImmediate(true);
		lblDate.setWidth("100.0%");
		lblDate.setHeight("18px");
		mainLayout.addComponent(lblDate,"top:20.0px;left:50.0px;");

		dDate = new PopupDateField();
		dDate.setImmediate(true); 
		dDate.setWidth("110px");
		dDate.setHeight("24px");
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setValue(new java.util.Date());
		dDate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dDate, "top:18.0px;left:200.0px;");*/
	
		
		lblPartyId = new Label("Party Name: ");
		lblPartyId.setImmediate(true);
		lblPartyId.setWidth("100.0%");
		lblPartyId.setHeight("18px");
		mainLayout.addComponent(lblPartyId,"top:30.0px;left:50.0px;");
		
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("318px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPartyName, "top:28.0px;left:150.0px;");
		
		chkAllPartyName=new CheckBox("All");
		chkAllPartyName.setImmediate(true);
		chkAllPartyName.setValue(false);
		mainLayout.addComponent(chkAllPartyName, "top:28.0px;left:500.0px;");
		
		lblProductName = new Label("Product Name: ");
		lblProductName.setImmediate(true);
		lblProductName.setWidth("100.0%");
		lblProductName.setHeight("18px");
		mainLayout.addComponent(lblProductName,"top:60.0px;left:50.0px;");
		
		cmbProductName = new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setWidth("318px");
		cmbProductName.setHeight("24px");
		cmbProductName.setNullSelectionAllowed(true);
		cmbProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbProductName, "top:58.0px;left:150.0px;");
		
		chkAllProductNme=new CheckBox("All");
		chkAllProductNme.setImmediate(true);
		chkAllProductNme.setValue(false);
		mainLayout.addComponent(chkAllProductNme, "top:58.0px;left:500.0px;");
		
		//table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("90%");

		table.setImmediate(true); // react at once when something is selected
		//table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Transection Id", Label.class, new Label());
		table.setColumnWidth("Transection Id",60);
		table.setColumnCollapsed("Transection Id", true);

		table.addContainerProperty("Date", Label.class, new Label());
		table.setColumnWidth("Date",70);
		table.setColumnAlignment("Date", table.ALIGN_CENTER);
		
		table.addContainerProperty("Item Code", Label.class, new Label());
		table.setColumnWidth("Item Code",60);
		table.setColumnAlignment("Item Code", table.ALIGN_CENTER);

		table.addContainerProperty("Item Name(Delivery Stage)", Label.class, new Label());
		table.setColumnWidth("Item Name(Delivery Stage)",170);

		table.addContainerProperty("Item Name(Receiving Stage)", Label.class, new Label());
		table.setColumnWidth("Item Name(Receiving Stage)",170);

		table.addContainerProperty("Source", Label.class, new Label());
		table.setColumnWidth("Source",150);
		
		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",45);
		table.setColumnAlignment("Unit", table.ALIGN_CENTER);
		
		table.addContainerProperty("Rate", Label.class, new Label());
		table.setColumnWidth("Rate",50);
		table.setColumnAlignment("Rate", table.ALIGN_RIGHT);
		
		table.addContainerProperty("Max Label", Label.class, new Label());
		table.setColumnWidth("Max Label",55);
		table.setColumnAlignment("Max Label", table.ALIGN_CENTER);
		
		table.addContainerProperty("Min Label", Label.class, new Label());
		table.setColumnWidth("Min Label",55);
		table.setColumnAlignment("Min Label", table.ALIGN_CENTER);
		
		table.addContainerProperty("Refill", Label.class, new Label());
		table.setColumnWidth("Refill",55);
		table.setColumnAlignment("Refill", table.ALIGN_CENTER);
		
		tableInitialise();
		
		//mainLayout.addComponent(cButton, "top:80px; left:120px;");
		mainLayout.addComponent(table, "top:100px; left:5px;");
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
		//tblblTransectionId.get(ar).setWidth("100%");
		tblblTransectionId.get(ar).setImmediate(true);
		//tblblTransectionId.get(ar).setHeight("15px");

		tblblDate.add(ar, new Label(""));
		//tblblDate.get(ar).setWidth("100%");
		tblblDate.get(ar).setImmediate(true);
		//tblblDate.get(ar).setHeight(size);
		
		tblblLabelCode.add(ar, new Label(""));
		//tblblLabelCode.get(ar).setWidth("100%");
		tblblLabelCode.get(ar).setImmediate(true);
		//tblblLabelCode.get(ar).setHeight(size);

		tblblLabelName.add(ar, new Label(""));
		//tblblLabelName.get(ar).setWidth("100%");
		tblblLabelName.get(ar).setImmediate(true);
		//tblblLabelName.get(ar).setHeight(size);
		
		tblblLabelNamesource.add(ar, new Label(""));
		//tblblLabelNamesource.get(ar).setWidth("100%");
		tblblLabelNamesource.get(ar).setImmediate(true);
		//tblblLabelNamesource.get(ar).setHeight(size);

		tblblSource .add(ar, new Label(""));
		//tblblSource.get(ar).setWidth("100%");
		tblblSource.get(ar).setImmediate(true);
		//tblblSource.get(ar).setHeight(size);
		
		tblblUnit .add(ar, new Label(""));
		//tblblUnit.get(ar).setWidth("100%");
		tblblUnit.get(ar).setImmediate(true);
		//tblblUnit.get(ar).setHeight(size);
		
		tblblRate .add(ar, new Label(""));
		//tblblRate.get(ar).setWidth("100%");
		tblblRate.get(ar).setImmediate(true);
		//tblblRate.get(ar).setHeight(size);
		
		tblblMax .add(ar, new Label(""));
		//tblblMax.get(ar).setWidth("100%");
		tblblMax.get(ar).setImmediate(true);
		//tblblMax.get(ar).setHeight(size);
		
		tblblMin .add(ar, new Label(""));
		//tblblMin.get(ar).setWidth("100%");
		tblblMin.get(ar).setImmediate(true);
		//tblblMin.get(ar).setHeight(size);
		
		tblblRefill .add(ar, new Label(""));
		//tblblRefill.get(ar).setWidth("100%");
		tblblRefill.get(ar).setImmediate(true);
		//tblblRefill.get(ar).setHeight(size);
		
		table.addItem(new Object[]{tblblTransectionId.get(ar),tblblDate.get(ar),tblblLabelCode.get(ar),
				tblblLabelName.get(ar),tblblLabelNamesource.get(ar),tblblSource.get(ar), tblblUnit.get(ar),
				tblblRate.get(ar),tblblMax.get(ar), tblblMin.get(ar), tblblRefill.get(ar)},ar);
	}
	
}