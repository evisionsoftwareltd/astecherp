package acc.appform.transportModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.SessionBean;
import com.common.share.TextRead;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;

public class VehicleMaintenFind extends Window 
{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private SessionBean sessionBean;
	private String frmName;
	private TextRead findMaintenID = new TextRead();
	private TextRead mainType = new TextRead();
	String MaintenType = "";
	private String maintenID="";
	
	private Label lblUnitCode;
	private Label lblSubUnitCode;
	private Label lblVehicleNo;
	private Label lblDate;
	private Label lblTo;
	private Label lblMaintainence;
	
	private ComboBox cmbUnitCode;
	private ComboBox cmbSubUnitCode;
	private ComboBox cmbVehicleNo;
	
	private CheckBox selectRegu = new CheckBox("Regular");
	private CheckBox selectMain = new CheckBox("Maintenance");
	private CheckBox selectService = new CheckBox("Servicing");
	
	private PopupDateField dFromDate;
	private PopupDateField dToDate;
	
	private CommonButton cButton = new CommonButton("", "", "","", "","Find", "","","","");
	
	private Table table = new Table();
	
	private ArrayList<Label> lblSL = new ArrayList<Label>();
	private ArrayList<Label> lblExpHead = new ArrayList<Label>();
	private ArrayList<Label> lblRegNo = new ArrayList<Label>();
	private ArrayList<Label> lblMaintenId = new ArrayList<Label>();
	private ArrayList<Label> lblVehicleType = new ArrayList<Label>();
	private ArrayList<Label> lblSupplier = new ArrayList<Label>();
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public VehicleMaintenFind(SessionBean sessionBean,TextRead findMaintenID,String frmName,TextRead mainType)
	{
		this.findMaintenID=findMaintenID;
		this.sessionBean=sessionBean;
		this.mainType=mainType;
		this.setCaption("VEHICLE MAINTENANCE FIND WINDOW :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("800px");
		this.setHeight("450px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");
		
		buildMainlayout();
		setContent(mainLayout);
		
		tableInitialise();
		setBtnAction();
		
		loadVehicleRegistration();
	}
	
	public void tableInitialise()
	{
		for(int i=0; i<11; i++)
		{
			tableRowAdd(i);
		}
	}
	
	public void tableRowAdd(final int ar)
	{
		lblSL.add(ar,new Label());
		lblSL.get(ar).setWidth("100%");
		lblSL.get(ar).setHeight("16px");
		
		lblMaintenId.add(ar,new Label());
		lblMaintenId.get(ar).setWidth("100%");

		lblExpHead.add(ar,new Label());
		lblExpHead.get(ar).setWidth("100%");
		
		lblRegNo.add(ar,new Label());
		lblRegNo.get(ar).setWidth("100%");
		
		lblVehicleType.add(ar,new Label());
		lblVehicleType.get(ar).setWidth("100%");
		
		lblSupplier.add(ar,new Label());
		lblSupplier.get(ar).setWidth("100%");

		table.addItem(new Object[]{lblSL.get(ar),lblMaintenId.get(ar),lblExpHead.get(ar),lblRegNo.get(ar),lblVehicleType.get(ar),lblSupplier.get(ar)},ar);
	}
	
	public void setBtnAction()
	{
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonValidation();
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					maintenID = lblMaintenId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					findMaintenID.setValue(maintenID);					
					mainType.setValue(MaintenType);
					System.out.println("Got maintenId: "+maintenID+" and Type "+mainType);
					windowClose();
				}
			}
		});
		
		selectRegu.addListener(new Listener()
		{	
			public void componentEvent(Event event)
			{
				if(selectRegu.getValue().toString().equalsIgnoreCase("true"))
				{
					MaintenType="Regular";
					selectMain.setValue(false);
					selectService.setValue(false);
				}
			}
		});
		
		selectMain.addListener(new Listener()
		{	
			public void componentEvent(Event event)
			{
				if(selectMain.getValue().toString().equalsIgnoreCase("true"))
				{
					MaintenType="Maintenance";
					selectRegu.setValue(false);
					selectService.setValue(false);
				}
			}
		});
		
		selectService.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(selectService.getValue().toString().equalsIgnoreCase("true"))
				{
					MaintenType="Servicing";
					selectRegu.setValue(false);
					selectMain.setValue(false);
				}
			}
		});
	}
	
	private void windowClose()
	{
		this.close();
	}
	
	private void loadVehicleRegistration()
	{
		cmbVehicleNo.removeAllItems();
		
		try
		{
			Transaction tx;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			String query=" SELECT vehicleId,regNumber from tbVehicleInfo order by autoId ";
			
			List list = session.createSQLQuery(query).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbVehicleNo.addItem(element[0]+"#");
				cmbVehicleNo.setItemCaption(element[0]+"#", element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print("Hi"+ex);
		}
	}
	
	private void findButtonValidation()
	{
		if(cmbVehicleNo.getValue()!=null)
		{
			if(selectRegu.getValue().toString().equalsIgnoreCase("true") || selectMain.getValue().toString().equalsIgnoreCase("true") || selectService.getValue().toString().equalsIgnoreCase("true"))
			{
				findButtonEvent();
			}
			else
			{
				getParent().showNotification("Warning: ","Select Maintenance Type.");
			}
		}
		else
		{
			getParent().showNotification("Warning: ","Select Registration No.");
		}
	}
	
	private void findButtonEvent()
	{
		tableclear();
		
		System.out.println("Find"+MaintenType);
		
		String fromD = dateFormat.format(dFromDate.getValue())+" 00:00:00";
		String toD = dateFormat.format(dToDate.getValue())+" 23:59:59";
		String regVehicleNo = cmbVehicleNo.getValue().toString().replace("#", "");
			
		Transaction tx;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List lst = null;
			String sql = null;

//			if(frmName.equals("VehicleMainTen"))
			{
				sql = " Select a.maintenId,b.expenseHead,c.regNumber,a.maintenType,b.supplier" +
					  " from tbVehicleMainten as a inner join tbVehicleMaintenTable as b " +
					  " on a.maintenId=b.maintenId inner join tbVehicleInfo as c on " +
					  " a.vehicleRegistrationNo=c.vehicleId " +
					  " where a.vehicleRegistrationNo='"+regVehicleNo+"' and a.entryDate " +
					  " between '"+fromD+"' and '"+toD+"' and a.maintenType='"+MaintenType+"' " +
					  " Order by maintenId ASC";
				lst= session.createSQLQuery(sql).list();

				int i=0;
				if(!lst.isEmpty())
				{
					for (Iterator iter = lst.iterator(); iter.hasNext();)
					{
						Object[] element = (Object[]) iter.next();
														
						lblSL.get(i).setValue(i+1);
						lblMaintenId.get(i).setValue(element[0].toString());
						lblExpHead.get(i).setValue(element[1].toString());
						lblRegNo.get(i).setValue(element[2].toString());
						lblVehicleType.get(i).setValue(element[3].toString());
						lblSupplier.get(i).setValue(element[4].toString());
						
						if((i)==lblSL.size()-1) 
						{
							tableRowAdd(i+1);
						}
						i++;
					}
				}
				else
				{
					getParent().showNotification("Warning: ","There are no Data.");
				}
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}
	
	private void tableclear()
	{
		for(int i=0; i<lblSL.size(); i++)
		{
			lblSL.get(i).setValue("");
			lblMaintenId.get(i).setValue("");
			lblExpHead.get(i).setValue("");
			lblRegNo.get(i).setValue("");
			lblVehicleType.get(i).setValue("");
			lblSupplier.get(i).setValue("");
		}
	}
	
	private AbsoluteLayout buildMainlayout()
	{	
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		
		table.setSelectable(true);
		table.setWidth("780px");
		table.setHeight("265px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",20);
		
		table.addContainerProperty("Mainten ID", Label.class, null);
		table.setColumnWidth("Mainten ID",55);
		
		table.addContainerProperty("Expanse Head", Label.class, null);
		table.setColumnWidth("Expanse Head",180);
		
		table.addContainerProperty("Reg. Number", Label.class, new Label());
		table.setColumnWidth("Reg. Number",150);
		
		table.addContainerProperty("Maintenance Type", Label.class, new Label());
		table.setColumnWidth("Maintenance Type",100);
		
		table.addContainerProperty("Supplier", Label.class, new Label());
		table.setColumnWidth("Supplier",175);
		
		mainLayout.addComponent(table,"top: 130.0px; left: 10.0px; ");
		table.setImmediate(true);
		
		lblVehicleNo = new Label("Registration No :");
		lblVehicleNo.setImmediate(false);
		lblVehicleNo.setWidth("100px");
		lblVehicleNo.setHeight("-1px");
		mainLayout.addComponent(lblVehicleNo, "top:27px; left:30px;");
		
		cmbVehicleNo = new ComboBox();
		cmbVehicleNo.setImmediate(false);
		cmbVehicleNo.setWidth("220px");
		cmbVehicleNo.setHeight("-1px");
		mainLayout.addComponent(cmbVehicleNo, "top:25px; left:130px;");
		
		lblDate = new Label("Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("100px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:61px; left:30px;");
		
		dFromDate = new PopupDateField();
		dFromDate.setImmediate(false);
		dFromDate.setWidth("120px");
		dFromDate.setHeight("-1px");
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromDate, "top:58px; left:130px;");
		
		lblTo = new Label("To");
		lblTo.setImmediate(false);
		lblTo.setWidth("20px");
		lblTo.setHeight("-1px");
		mainLayout.addComponent(lblTo, "top:61px; left:255px;");
		
		dToDate = new PopupDateField();
		dToDate.setImmediate(false);
		dToDate.setWidth("120px");
		dToDate.setHeight("-1px");
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new java.util.Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToDate, "top:58px; left:280px;");
		
		lblMaintainence = new Label("Maintainence Type :");
		lblMaintainence.setImmediate(false);
		lblMaintainence.setWidth("120px");
		lblMaintainence.setHeight("-1px");
		mainLayout.addComponent(lblMaintainence, "top:95px; left:30px;");
		
		selectRegu.setImmediate(true);
		selectRegu.setStyleName("horizontal");
		mainLayout.addComponent(selectRegu, "top:93px; left:140px;");
		
		selectMain.setImmediate(true);
		selectMain.setStyleName("horizontal");
		mainLayout.addComponent(selectMain, "top:93px; left:220px;");

		selectService.setImmediate(true);
		selectService.setStyleName("horizontal");
		mainLayout.addComponent(selectService, "top:93px; left:325px;");
		
		mainLayout.addComponent(cButton, "top:90px; left:410px;");
		
		return mainLayout;
	}
	
}
