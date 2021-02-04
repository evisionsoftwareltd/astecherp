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





public class ThirdPartyItemReceiptFind extends Window
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
	
	private Label lblProductId=new Label("Product Id : ");
	private ComboBox cmbProductName=new ComboBox() ;
	private CheckBox chkAllProductName=new CheckBox("ALL");
	
	TextField txtReceipt=new TextField();
	private ArrayList<Label> tblblReceiptNo = new ArrayList<Label>();
	private ArrayList<Label> tblblReceiptDate = new ArrayList<Label>();
	private ArrayList<Label> tblblChallanNo = new ArrayList<Label>();
	private ArrayList<Label> tblblChallanDate= new ArrayList<Label>();
	private ArrayList<Label> tblblProductName = new ArrayList<Label>();
	private ArrayList<Label> tblblUnit = new ArrayList<Label>();
	private ArrayList<Label> tblblQty= new ArrayList<Label>(); 
	private ArrayList<Label> tblblRate= new ArrayList<Label>(); 
	private ArrayList<Label> tblblAmount= new ArrayList<Label>(); 
	
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private Label lblCountProduct=new Label();

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;

	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yyyy");
	
	private DecimalFormat df = new DecimalFormat("#0.00");
	private DecimalFormat dformate = new DecimalFormat("#0");
	
	public ThirdPartyItemReceiptFind(SessionBean sessionBean,TextField txtTransectionId)
	{
		this.txtTransectionId = txtTransectionId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND THIRD PARTY ITEM RECEIPT :: "+sessionBean.getCompany());
		this.center();
		table.setImmediate(true);
		this.setWidth("550px");
		this.setHeight("700px");
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
		cmbProductName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbProductName.getValue()!=null)
				{
					tableclear();
					System.out.println("I am From CmbProduct Name Action ");
					String party="%";
					if(chkAllPartyName.booleanValue()==false){
						party= cmbPartyName.getValue().toString();
					}
					System.out.println("I am From CmbProduct Name Action :  "+party);
					tableDataAdding(party);
				}
				else{
					tableclear();
				}
			}
		});

		chkAllProductName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(chkAllProductName.booleanValue()==true){
					cmbProductName.setValue(null);			
					cmbProductName.setEnabled(false);
					tableDataAdding("%");
				}
				else{
					cmbProductName.setEnabled(true);
				}
			}
		});
		
		cmbPartyName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbPartyName.getValue()!=null)
				{
					cmbProductNameLoad();
				}
				/*else{
					showNotification("Null", " Product Name",  Notification.TYPE_ERROR_MESSAGE );
				}*/
			}
		});

		chkAllPartyName.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(chkAllPartyName.booleanValue()==true){
					System.out.println("Party All Click : ");
					cmbPartyName.setValue(null);			
					cmbPartyName.setEnabled(false);
					cmbProductNameLoad();
					System.out.println("Party All Click : ");
				}
				else{
					cmbPartyName.setEnabled(true);
				}
			}
		});
		
		
		
		
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				
				if(event.isDoubleClick())
				{
					transectionId = tblblReceiptNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtTransectionId.setValue(transectionId);
					windowClose();
				}
			}
		});
		
		
	}

	private void tableclear()
	{
		for(int i=0; i<tblblReceiptNo.size(); i++)
		{
			tblblReceiptNo.get(i).setValue("");
			tblblReceiptDate.get(i).setValue("");
			tblblChallanNo.get(i).setValue("");
			tblblChallanDate.get(i).setValue("");
			tblblProductName.get(i).setValue("");
			tblblUnit.get(i).setValue("");
			tblblQty.get(i).setValue("");
			tblblRate.get(i).setValue("");
			tblblAmount.get(i).setValue("");
		}
	}
	private void tableDataAdding(String partyId)
	{
		Transaction tx = null;
		String query = "";
		try
		{
			System.out.println("Hi : Now start ");
			if(chkAllPartyName.booleanValue()){ 
				partyId="%";
			}
			
			System.out.println("Party Name : "+partyId);
			
			String product="";
			System.out.println("Hi : Now start ");
			if(chkAllProductName.booleanValue()){ 
				product="%";
			}
			else{
				product= cmbProductName.getValue().toString();
			}
			
			System.out.println("Party Name : "+product);
			
			
			Session session = SessionFactoryUtil.getInstance().openSession();
			/*query= "select a.vReceiptNo, a.dReceiptDate, a.vChallanNo, a.dChallanDate,b.vProductName,b.vUnit,"
					+ "b.mQty,b.mRate,b.mAmount  from tb3rdPartyReceiptInformation a "
					+ "inner join tb3rdPartyReceiptDetails b on a.vReceiptNo=b.vReceiptNo "
					+ "where a.vPartyId like '"+partyId+"' "; */
			
/*			query= "select vReceiptNo, dReceiptDate, vChallanNo,dChallanDate  "
					+ "from tb3rdPartyReceiptInformation  where vPartyId like '"+partyId+"'"; */
			
			query=" select a.vReceiptNo, a.dReceiptDate, a.vChallanNo,a.dChallanDate  "
					+ " from tb3rdPartyReceiptInformation a inner join tb3rdPartyReceiptDetails b  "
					+ "on a.iTransectionId=b.iTransectionId where a.vPartyId like '"+partyId+"' "
							+ "and b.vProductId like '"+product+"'";
			
			System.out.println("query"+query);
			List list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();
					tblblReceiptNo.get(i).setValue(element[0].toString());
					tblblReceiptDate.get(i).setValue(dFormat.format(element[1]));
					tblblChallanNo.get(i).setValue(element[2].toString());
					tblblChallanDate.get(i).setValue(dFormat.format(element[3]));
					/*tblblProductName.get(i).setValue(element[4].toString());
					tblblUnit.get(i).setValue(element[5].toString());
					tblblQty.get(i).setValue(dformate.format(element[6]));
					tblblRate.get(i).setValue(df.format(element[7]));
					tblblAmount.get(i).setValue(df.format(element[8]));*/
					if(i==tblblReceiptNo.size()-1)
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

	public void cmbPartyNameLoad()
	{
		cmbPartyName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select  distinct vPartyId,vPartyName from tb3rdPartyReceiptInformation").list();
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
	
	public void cmbProductNameLoad(){
		cmbProductName.removeAllItems();
		Transaction tx= null;
		try{
			String party="";
			System.out.println("Hi : Now start ");
			if(chkAllPartyName.booleanValue()){ 
				party="%";
				System.out.println("Chk All Party .");
			}
			else{
				party= cmbPartyName.getValue().toString();
			}
			System.out.println("Party Name : "+party);
				
			Session session= SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct vLabelCode,vLabelName from tb3rdPartylabelInformation a "
					+ " inner join tb3rdPartyReceiptDetails b on a.vLabelCode= b.vProductId "
					+ "where vPartyId like '"+party+"'";
			
			
			
			System.out.println("Label Code or Product Name : "+sql);
			List list = session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProductName.addItem(element[0].toString());
				cmbProductName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Error", exp+"  ", Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private AbsoluteLayout buildMainLayout()
	{		
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

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
		mainLayout.addComponent(lblPartyId,"top:20.0px;left:50.0px;");
		
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("318px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPartyName, "top:18.0px;left:150.0px;");
		
		chkAllPartyName=new CheckBox("All");
		chkAllPartyName.setImmediate(true);
		chkAllPartyName.setValue(false);
		mainLayout.addComponent(chkAllPartyName, "top:18.0px;left:480.0px;");
		
		lblProductId = new Label("Product Name: ");
		lblProductId.setImmediate(true);
		lblProductId.setWidth("100.0%");
		lblProductId.setHeight("18px");
		mainLayout.addComponent(lblProductId,"top:50.0px;left:50.0px;");
		
		cmbProductName = new ComboBox();
		cmbProductName.setImmediate(true);
		cmbProductName.setWidth("318px");
		cmbProductName.setHeight("24px");
		cmbProductName.setNullSelectionAllowed(true);
		cmbProductName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbProductName, "top:48.0px;left:150.0px;");
		
		chkAllProductName=new CheckBox("All");
		chkAllProductName.setImmediate(true);
		chkAllProductName.setValue(false);
		mainLayout.addComponent(chkAllProductName, "top:48.0px;left:480.0px;");

		table.setImmediate(true);
		table.setWidth("99%");
		table.setHeight("95%");
		 // react at once when something is selected
	

		table.addContainerProperty("Receipt No", Label.class, new Label());
		table.setColumnWidth("Receipt No",130);
		table.setColumnAlignment("Receipt No", table.ALIGN_CENTER);
	
		table.addContainerProperty("Receipt Date", Label.class, new Label());
		table.setColumnWidth("Receipt Date",100);
		table.setColumnAlignment("Receipt Date", table.ALIGN_CENTER);
		
		table.addContainerProperty("Challan No", Label.class, new Label());
		table.setColumnWidth("Challan No",100);
		table.setColumnAlignment("Challan No", table.ALIGN_CENTER);

		table.addContainerProperty("Challan Date", Label.class, new Label());
		table.setColumnWidth("Challan Date",130);
		table.setColumnAlignment("Challan Date", table.ALIGN_CENTER);
		
		
		tableInitialise();
		mainLayout.addComponent(table, "top:80px; left:5px;");
		return mainLayout;
	}
	
	public void tableInitialise()
	{
		for(int i=0;i<250;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tblblReceiptNo.add(ar, new Label(""));
		tblblReceiptNo.get(ar).setWidth("100%");
		tblblReceiptNo.get(ar).setImmediate(true);
		tblblReceiptNo.get(ar).setHeight("15px");
		
		tblblReceiptDate.add(ar, new Label(""));
		tblblReceiptDate.get(ar).setWidth("100%");
		tblblReceiptDate.get(ar).setImmediate(true);
		tblblReceiptDate.get(ar).setHeight("15px");
		
		tblblChallanNo.add(ar, new Label(""));
		tblblChallanNo.get(ar).setWidth("100%");
		tblblChallanNo.get(ar).setImmediate(true);
		tblblChallanNo.get(ar).setHeight("15px");

		tblblChallanDate.add(ar, new Label(""));
		tblblChallanDate.get(ar).setWidth("100%");
		tblblChallanDate.get(ar).setImmediate(true);
		tblblChallanDate.get(ar).setHeight("15px");

		tblblProductName .add(ar, new Label(""));
		tblblProductName.get(ar).setWidth("100%");
		tblblProductName.get(ar).setImmediate(true);
		tblblProductName.get(ar).setHeight("15px");
		
		tblblUnit .add(ar, new Label(""));
		tblblUnit.get(ar).setWidth("100%");
		tblblUnit.get(ar).setImmediate(true);
		tblblUnit.get(ar).setHeight("15px");
		
		tblblQty .add(ar, new Label(""));
		tblblQty.get(ar).setWidth("100%");
		tblblQty.get(ar).setImmediate(true);
		tblblQty.get(ar).setHeight("15px");

		tblblRate .add(ar, new Label(""));
		tblblRate.get(ar).setWidth("100%");
		tblblRate.get(ar).setImmediate(true);
		tblblRate.get(ar).setHeight("15px");
		
		tblblAmount .add(ar, new Label(""));
		tblblAmount.get(ar).setWidth("100%");
		tblblAmount.get(ar).setImmediate(true);
		tblblAmount.get(ar).setHeight("15px");
		
		table.addItem(new Object[]{tblblReceiptNo.get(ar),tblblReceiptDate.get(ar),tblblChallanNo.get(ar),
				tblblChallanDate.get(ar)},ar);
		
		/*table.addItem(new Object[]{tblblReceiptNo.get(ar),tblblReceiptDate.get(ar),tblblChallanNo.get(ar),
				tblblChallanDate.get(ar),tblblProductName.get(ar), tblblUnit.get(ar),tblblQty.get(ar),
				tblblRate.get(ar), tblblAmount.get(ar)},ar);*/
	}
	
}