package acc.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LayOffOptionFind extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;
	private TextField txtTransactionID;
	private Label lblFormDate;
	private Label lblToDate;
	private PopupDateField dFromDate=new PopupDateField();
	private PopupDateField dtoDate=new PopupDateField();
	private Table table = new Table();
	private ArrayList<Label> lblSL = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbdFromDate=new ArrayList<PopupDateField>();
	private ArrayList<PopupDateField> tbdToDate=new ArrayList<PopupDateField>();
	private ArrayList<Label> lblDepartmentID=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName=new ArrayList<Label>();
	private ArrayList<Label> lblSectionID=new ArrayList<Label>();
	private ArrayList<Label> lblTransactionID=new ArrayList<Label>();
	private ArrayList<Label> lblSectionName=new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	//private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");

	String autoId = "";
	String empId = "";
	String ApplicationDate="";
	String findDate = "a";
	String Section="";

	
	public LayOffOptionFind(SessionBean sessionBean, TextField txtTransactionID)
	{
		this.txtTransactionID = txtTransactionID;
		this.sessionBean = sessionBean;
		this.setCaption("FIND LAYOFF OPTION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("570px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		buildMainLayout();
		setContent(mainLayout);
		tableinitialization();
		setEventActions();
		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(dFromDate);
		allComp.add(dtoDate);
		allComp.add(cButton.btnFind);
		new FocusMoveByEnter(this,allComp);
	}

	private void tableinitialization()
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("285px");
		table.setPageLength(0);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",30);

		table.addContainerProperty("From Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("From Date", 110);

		table.addContainerProperty("To Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("To Date", 110);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 60);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 140);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 60);
		
		table.addContainerProperty("Transaction ID", Label.class, new Label());
		table.setColumnWidth("Transaction ID", 60);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 140);

		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Transaction ID", true);

		rowAddinTable();	
	}

	public void rowAddinTable()
	{
		for(int i=0; i<10; i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		table.setSelectable(true);
		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		lblSL.add(ar, new Label());
		lblSL.get(ar).setWidth("100%");
		lblSL.get(ar).setHeight("20px");
		lblSL.get(ar).setValue(ar+1);

		tbdFromDate.add(ar, new PopupDateField());
		tbdFromDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbdFromDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbdFromDate.get(ar).setImmediate(true);
		tbdFromDate.get(ar).setReadOnly(true);
		tbdFromDate.get(ar).setWidth("100%");
		tbdFromDate.get(ar).setHeight("20px");

		tbdToDate.add(ar, new PopupDateField());
		tbdToDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbdToDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbdToDate.get(ar).setImmediate(true);
		tbdToDate.get(ar).setReadOnly(true);
		tbdToDate.get(ar).setWidth("100%");
		tbdToDate.get(ar).setHeight("20px");

		lblDepartmentID.add(ar, new Label());
		lblDepartmentID.get(ar).setWidth("100%");
		lblDepartmentID.get(ar).setHeight("20px");

		lblDepartmentName.add(ar, new Label());
		lblDepartmentName.get(ar).setWidth("100%");
		lblDepartmentName.get(ar).setHeight("20px");

		lblSectionID.add(ar, new Label());
		lblSectionID.get(ar).setWidth("100%");
		lblSectionID.get(ar).setHeight("20px");
		
		lblTransactionID.add(ar, new Label());
		lblTransactionID.get(ar).setWidth("100%");
		lblTransactionID.get(ar).setHeight("20px");

		lblSectionName.add(ar, new Label());
		lblSectionName.get(ar).setWidth("100%");
		lblSectionName.get(ar).setHeight("20px");

		table.addItem(new Object[]{lblSL.get(ar),tbdFromDate.get(ar),tbdToDate.get(ar),lblDepartmentID.get(ar),lblDepartmentName.get(ar),lblSectionID.get(ar),lblTransactionID.get(ar),lblSectionName.get(ar)},ar);
	}

	private void setEventActions()
	{
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dFromDate.getValue()!=null && dtoDate.getValue()!=null) 
				{
					findButtonEvent();
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					txtTransactionID.setValue(lblTransactionID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					windowClose();
				}
			}
		});
	}

	private void findButtonEvent()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Findquery = " SELECT distinct dFromDate,dToDate,vDepartmentID,vDepartmentName," +
					"vSectionID,vSectionName,vTransactionID from tbLayOff where dFromDate>='"+dFormat.format(dFromDate.getValue())+"' " +
					"and dToDate<='"+dFormat.format(dtoDate.getValue())+"' order by vTransactionID desc";
			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					tbdFromDate.get(i).setReadOnly(false);
					tbdFromDate.get(i).setValue(element[0]);
					tbdFromDate.get(i).setReadOnly(true);
					tbdToDate.get(i).setReadOnly(false);
					tbdToDate.get(i).setValue(element[1]);
					tbdToDate.get(i).setReadOnly(true);
					lblDepartmentID.get(i).setValue(element[2]);
					lblDepartmentName.get(i).setValue(element[3]);
					lblSectionID.get(i).setValue(element[4]);
					lblSectionName.get(i).setValue(element[5]);
					lblTransactionID.get(i).setValue(element[6]);

					if((i)==tbdFromDate.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				showNotification("No data found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("findButtonEvent", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void tableclear()
	{
		for(int i=0; i<lblSL.size(); i++)
		{
			tbdFromDate.get(i).setReadOnly(false);
			tbdFromDate.get(i).setValue(null);
			tbdFromDate.get(i).setReadOnly(true);

			tbdToDate.get(i).setReadOnly(false);
			tbdToDate.get(i).setValue(null);
			tbdToDate.get(i).setReadOnly(true);
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("520px");
		setHeight("425px");

		lblFormDate = new Label("From Date :");
		lblFormDate.setImmediate(true);
		lblFormDate.setWidth("-1px");
		lblFormDate.setHeight("-1px");
		mainLayout.addComponent(lblFormDate, "top:20.0px;left:25.0px;");

		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setInvalidAllowed(false);
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		mainLayout.addComponent(dFromDate, "top:18.0px;left:100.0px;");

		lblToDate = new Label("To Date :");
		lblToDate.setImmediate(true);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:45.0px;left:25.0px;");

		dtoDate.setValue(new java.util.Date());
		dtoDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dtoDate.setDateFormat("dd-MM-yyyy");
		dtoDate.setInvalidAllowed(false);
		dtoDate.setImmediate(true);
		dtoDate.setWidth("110px");
		mainLayout.addComponent(dtoDate, "top:43.0px;left:100.0px;");

		cButton.btnFind.setWidth("80px");
		cButton.btnFind.setHeight("28px");
		mainLayout.addComponent(cButton.btnFind, "top:43.0px;left:430.0px;");

		mainLayout.addComponent(table, "top:75.0px;left:15.0px;");

		return mainLayout;
	}
}
