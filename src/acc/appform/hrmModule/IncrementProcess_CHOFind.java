package acc.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

	@SuppressWarnings("serial")
	public class IncrementProcess_CHOFind extends Window
	{
	private AbsoluteLayout mainLayout=new AbsoluteLayout();
	private Label IncrementDate;
	private Label SectionID;
	private Label lblSectionName;
	private Label IncrementType;
	private Label EmployeeName;
	private ComboBox cmbSectionName;
	private Table table;
	private ArrayList<Label> tblblSl = new ArrayList<Label>();
	private ArrayList<Label> tblblSectionID = new ArrayList<Label>();
	private ArrayList<Label> tblblSection = new ArrayList<Label>();
	private ArrayList<Label> tblblemployeeCode= new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeType = new ArrayList<Label>();
	private ArrayList<Label> tblblIncrementType = new ArrayList<Label>();
	private ArrayList<Label> tblblIncrementDate = new ArrayList<Label>();
	private ArrayList<Label> tblblMonth = new ArrayList<Label>();
	private ArrayList<Label> tblblYear = new ArrayList<Label>();
	
	SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings("unused")
	private SessionBean sessionBean;

	public IncrementProcess_CHOFind(SessionBean sessionBean,Label IncrementDate,Label SectionID,Label IncrementType,Label EmployeeName)
	{
		this.IncrementDate = IncrementDate;
		this.SectionID = SectionID;
		this.IncrementType = IncrementType;
		this.EmployeeName = EmployeeName;
		this.sessionBean=sessionBean;
		this.setCaption("INCREMENT PROCESS FIND CHO:: "+sessionBean.getCompany());
		this.center();
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		buildMainLayout();
		setContent(mainLayout);
		tableInitialise();
		setEventAction();
		SectionValueAdd();
		tableDataAdd();
	}

	public void setEventAction()
	{	
		cmbSectionName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				tableclear();
				if(cmbSectionName.getValue()!=null)
				{
					tableDataAdd();
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					IncrementDate.setValue(tblblIncrementDate.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					SectionID.setValue(tblblSectionID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					IncrementType.setValue(tblblIncrementType.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					EmployeeName.setValue(tblblEmployeeName.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					System.out.println(EmployeeName.getValue().toString());
					System.out.println(IncrementType.getValue().toString());
					windowClose();
				}
			}
		});
	}

	private void SectionValueAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct vSectionID, vSectionName from tbSalaryIncrement where vDepartmentId ='DEPT10'";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr = lst.iterator(); itr.hasNext();)
				{
					Object [] element = (Object [])itr.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("SectionValueAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String SectionID = "";
			if(cmbSectionName.getValue()!=null)
			SectionID=cmbSectionName.getValue().toString();
			
			String query ="select distinct vSectionID, vSectionName,employeeCode,"
					+ "vEmployeeName,vEmployeeType,vIncrementType,dDate, CONVERT(varchar,DATENAME(MM,dDate)) vMonthName,"
					+ " YEAR(dDate) iYear from tbSalaryIncrement where vSectionID like '"+SectionID+"'";
					
					
					
					
					/*"select distinct vSectionID, vSectionName, dDate, CONVERT(varchar,DATENAME(MM,dDate)) vMonthName,"
					+ " YEAR(dDate) iYear from tbSalaryIncrement where vSectionID like '"+SectionID+"'";*/
		      System.out.println("TableValueAdd");
		      
		      
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				int i=0;
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element = (Object[]) itr.next();
					tblblSectionID.get(i).setValue(element[0].toString());
					tblblSection.get(i).setValue(element[1].toString());
					tblblemployeeCode.get(i).setValue(element[2].toString());
					tblblEmployeeName.get(i).setValue(element[3].toString());
					tblblEmployeeType.get(i).setValue(element[4].toString());
					tblblIncrementType.get(i).setValue(element[5].toString());
					tblblIncrementDate.get(i).setValue(element[6].toString());
					tblblMonth.get(i).setValue(element[7].toString());
					tblblYear.get(i).setValue(element[8].toString());
						
					if(tblblIncrementDate.size()-1==i)
						tableRowAdd(i+1);
					i++;
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("tableDataAdd", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
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
		tblblSl.add(ar, new Label(""));
		tblblSl.get(ar).setWidth("100%");
		tblblSl.get(ar).setImmediate(true);
		tblblSl.get(ar).setValue(ar+1);
		tblblSl.get(ar).setHeight("20px");
		
		tblblSectionID.add(ar, new Label(""));
		tblblSectionID.get(ar).setWidth("100%");
		tblblSectionID.get(ar).setImmediate(true);
		
		tblblSection.add(ar, new Label(""));
		tblblSection.get(ar).setWidth("100%");
		tblblSection.get(ar).setImmediate(true);
		
		tblblemployeeCode.add(ar, new Label(""));
		tblblemployeeCode.get(ar).setWidth("100%");
		tblblemployeeCode.get(ar).setImmediate(true);
		
		tblblEmployeeName.add(ar, new Label(""));
		tblblEmployeeName.get(ar).setWidth("100%");
		tblblEmployeeName.get(ar).setImmediate(true);

		tblblEmployeeType.add(ar, new Label(""));
		tblblEmployeeType.get(ar).setWidth("100%");
		tblblEmployeeType.get(ar).setImmediate(true);

		tblblIncrementType.add(ar, new Label(""));
		tblblIncrementType.get(ar).setWidth("100%");
		tblblIncrementType.get(ar).setImmediate(true);
		
		tblblIncrementDate.add(ar, new Label(""));
		tblblIncrementDate.get(ar).setWidth("100%");
		tblblIncrementDate.get(ar).setImmediate(true);
		
		tblblMonth.add(ar, new Label(""));
		tblblMonth.get(ar).setWidth("100%");
		tblblMonth.get(ar).setImmediate(true);
		
		tblblYear.add(ar, new Label(""));
		tblblYear.get(ar).setWidth("100%");
		tblblYear.get(ar).setImmediate(true);
		
		table.addItem(new Object[]{tblblSl.get(ar),tblblSectionID.get(ar),tblblSection.get(ar),tblblemployeeCode.get(ar),
				tblblEmployeeName.get(ar),tblblEmployeeType.get(ar),tblblIncrementType.get(ar),
				tblblIncrementDate.get(ar),tblblMonth.get(ar),tblblYear.get(ar)},ar);
	}

	private void tableclear()
	{
		for(int i=0; i<tblblIncrementDate.size(); i++)
		{
			tblblSectionID.get(i).setValue("");
			tblblSection.get(i).setValue("");
			tblblIncrementDate.get(i).setValue("");
			tblblemployeeCode.get(i).setValue("");
			tblblEmployeeName.get(i).setValue("");
			tblblEmployeeType.get(i).setValue("");
			tblblIncrementType.get(i).setValue("");
			tblblMonth.get(i).setValue("");
			tblblYear.get(i).setValue("");
				
		}
	}
	
	private void windowClose()
	{
		this.close();
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("800.0px");
		mainLayout.setHeight("450.0px");
		
		lblSectionName = new Label("Section Name : ");
		mainLayout.addComponent(lblSectionName, "top:30.0px; left:160.0px");
		
		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("260.0px");
		mainLayout.addComponent(cmbSectionName, "top:28.0px; left:265.0px;");
		
		table = new Table();
		table.setWidth("98%");
		table.setHeight("300.0px");
		
		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#", 20);
		
		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 60);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 100);

		table.addContainerProperty("Employee Id", Label.class, new Label());
		table.setColumnWidth("Employee Id", 100);
		
		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 130);

		table.addContainerProperty("Employee Type", Label.class, new Label());
		table.setColumnWidth("Employee Type", 80);
		
		table.addContainerProperty("Increment Type", Label.class, new Label());
		table.setColumnWidth("Increment Type", 140);

		table.addContainerProperty("Increment Date", Label.class, new Label());
		table.setColumnWidth("Increment Date", 90);

		table.addContainerProperty("Month", Label.class, new Label());
		table.setColumnWidth("Month", 50);

		table.addContainerProperty("Year", Label.class, new Label());
		table.setColumnWidth("Year", 50);

		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER});
		
		//table.setColumnCollapsed("Increment Date", true);
		table.setColumnCollapsed("Section ID", true);
		mainLayout.addComponent(table, "top:100.0px; left:20.0px;");

		return mainLayout;
	}
}