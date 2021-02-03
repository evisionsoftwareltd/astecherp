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
import com.common.share.TextRead;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SectionWiseActiveInactiveFind extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;

	private Label lblFormDate;
	private Label lblToDate;
	private PopupDateField dFromDate=new PopupDateField();
	private PopupDateField dtoDate=new PopupDateField();

	private Table table = new Table();
	private ArrayList<Label> lblSL = new ArrayList<Label>();
	private ArrayList<PopupDateField> dDate=new ArrayList<PopupDateField>();
	private ArrayList<Label> lblDepartmentId=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName=new ArrayList<Label>();
	private ArrayList<Label> lblSectionID=new ArrayList<Label>();
	private ArrayList<Label> lblSectionName=new ArrayList<Label>();

	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	String autoId = "";
	String empId = "";
	String ApplicationDate="";
	String findDate = "a";
	String Section="";

	private TextRead txtFindDate = new TextRead();
	private TextRead txtSectionId=new TextRead();
	private TextRead txtDepartmentId=new TextRead();
	
	public SectionWiseActiveInactiveFind(SessionBean sessionBean, TextRead txtDepartmentId, TextRead txtSectionID, TextRead txtFindDate)
	{
		this.txtDepartmentId=txtDepartmentId;
		this.txtSectionId=txtSectionID;
		this.txtFindDate = txtFindDate;
		this.sessionBean = sessionBean;
		this.setCaption("FIND ACTIVE/INACTIVE EMPLOYEE :: "+sessionBean.getCompany());
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

		table.addContainerProperty("Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Date", 110);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 60);
		
		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 120);
		
		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 60);
		
		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 120);
		
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
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

		dDate.add(ar, new PopupDateField());
		dDate.get(ar).setDateFormat("dd-MM-yyyy");
		dDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.get(ar).setImmediate(true);
		dDate.get(ar).setReadOnly(true);
		dDate.get(ar).setWidth("100%");
		dDate.get(ar).setHeight("20px");

		lblDepartmentId.add(ar, new Label());
		lblDepartmentId.get(ar).setWidth("100%");
		lblDepartmentId.get(ar).setHeight("20px");
		
		lblDepartmentName.add(ar, new Label());
		lblDepartmentName.get(ar).setWidth("100%");
		lblDepartmentName.get(ar).setHeight("20px");
		
		lblSectionID.add(ar, new Label());
		lblSectionID.get(ar).setWidth("100%");
		lblSectionID.get(ar).setHeight("20px");
		
		lblSectionName.add(ar, new Label());
		lblSectionName.get(ar).setWidth("100%");
		lblSectionName.get(ar).setHeight("20px");
		
		table.addItem(new Object[]{lblSL.get(ar),dDate.get(ar),lblDepartmentId.get(ar),lblDepartmentName.get(ar),
				lblSectionID.get(ar),lblSectionName.get(ar)},ar);
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
					txtDepartmentId.setValue(lblDepartmentId.get(Integer.valueOf(event.getItemId().toString())).getValue());
					txtFindDate.setValue(dFormat.format(dDate.get(Integer.valueOf(event.getItemId().toString())).getValue()));
					txtSectionId.setValue(lblSectionID.get(Integer.valueOf(event.getItemId().toString())).getValue());
					windowClose();
				}
			}
		});
	}

	private void findButtonEvent()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		String from = dFormat.format(dFromDate.getValue());
		String to = dFormat.format(dtoDate.getValue());
		try
		{
			String Findquery = " SELECT distinct convert(date,dInactiveDate) dDate,vDepartmentID,vDepartmentName,vSectionID," +
					"vSectionName from tbInactiveEmployee where convert(date,dInactiveDate) between '"+from+"' and '"+to+"' " +
					"and vActive_Inactive='Inactive' order by convert(date,dInactiveDate) desc";
			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					dDate.get(i).setReadOnly(false);
					dDate.get(i).setValue(element[0]);
					dDate.get(i).setReadOnly(true);
					lblDepartmentId.get(i).setValue(element[1]);
					lblDepartmentName.get(i).setValue(element[2]);
					lblSectionID.get(i).setValue(element[3]);
					lblSectionName.get(i).setValue(element[4]);

					if((i)==dDate.size()-1)
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
			dDate.get(i).setReadOnly(false);
			dDate.get(i).setValue(null);
			dDate.get(i).setReadOnly(true);
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

		setWidth("450px");
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
		mainLayout.addComponent(cButton.btnFind, "top:43.0px;left:360.0px;");

		mainLayout.addComponent(table, "top:75.0px;left:15.0px;");
		return mainLayout;
	}
}
