package acc.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LeaveInfoFindCHO extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;

	private ComboBox cmbEmpName ;
	private Label lblEmployeeName;
	private Label lblSectionname;
	private Label lblDepartmentName;
	private Label lblFormDate;
	private Label lblToDate;
	private ComboBox cmbDepartmentName;
	private ComboBox cmbSectionName;
	private PopupDateField dFromDate=new PopupDateField();
	private PopupDateField dtoDate=new PopupDateField();

	private Table table = new Table();
	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lblautoID=new ArrayList<Label>();
	private ArrayList<Label> lbEmpID = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeId = new ArrayList<Label>();
	private ArrayList<Label> lblEmpName=new ArrayList<Label>();
	private ArrayList<Label> lbAppDate = new ArrayList<Label>();
	private ArrayList<Label> lbDesignation = new ArrayList<Label>();

	private CheckBox ChkSection;
	private CheckBox ChkEmployee;
	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private OptionGroup opgTypeOfSearch;
	private List<String> lst=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

	String autoId = "";
	String empId = "";
	String ApplicationDate="";
	String findDate = "a";
	String Department="";
	String Section="";
	String type="";
	
	private TextRead txtAutoId = new TextRead();
	private TextRead txtFindDate = new TextRead();

	public LeaveInfoFindCHO(SessionBean sessionBean, TextRead txtAuto, TextRead txtFindDate, String typeOfSearch)
	{		
		this.type=typeOfSearch;
		this.txtAutoId = txtAuto;
		this.txtFindDate = txtFindDate;
		this.sessionBean = sessionBean;
		this.setCaption("FIND LEAVE INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("570px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		
		buildMainLayout();
		setContent(mainLayout);
		tableinitialization();
		DepartmentDataAdd();
		setEventActions();
		cmbDepartmentName.focus();
		focusEnter();
	}

	private void focusEnter()
	{
		allComp.add(cmbDepartmentName);
		allComp.add(cmbSectionName);
		allComp.add(cmbEmpName);
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
		table.setColumnWidth("SL #",40);

		table.addContainerProperty("Auto ID", Label.class, new Label());

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 70);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 70);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 150);

		table.addContainerProperty("Application Date", Label.class , new Label());
		table.setColumnWidth("Application Date",150);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",150);	

		table.setColumnCollapsed("Auto ID", true);
		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("Employee ID", true);

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

		lbSL.add(ar, new Label(""));
		lbSL.get(ar).setWidth("100%");
		lbSL.get(ar).setHeight("20px");
		lbSL.get(ar).setValue(ar+1);

		lblautoID.add(ar, new Label(""));
		lblautoID.get(ar).setWidth("100%");
		lblautoID.get(ar).setHeight("20px");

		lbEmpID.add(ar,new Label());
		lbEmpID.get(ar).setWidth("100%");

		lblEmployeeId.add(ar, new Label(""));
		lblEmployeeId.get(ar).setWidth("100%");
		lblEmployeeId.get(ar).setWidth("20px");

		lblEmpName.add(ar, new Label());
		lblEmpName.get(ar).setWidth("100%");

		lbAppDate.add(ar, new Label(""));
		lbAppDate.get(ar).setWidth("100%");
		lbAppDate.get(ar).setHeight("14px");
		lbAppDate.get(ar).setStyleName("appDate");

		lbDesignation.add(ar, new Label(""));
		lbDesignation.get(ar).setWidth("100%");

		table.addItem(new Object[]{lbSL.get(ar),lblautoID.get(ar),lbEmpID.get(ar),lblEmployeeId.get(ar),lblEmpName.get(ar),lbAppDate.get(ar),lbDesignation.get(ar)},ar);
	}

	private void DepartmentDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select el.vDepartmentID,dept.vDepartmentName from tbEmployeeLeave el inner join " +
					"tbDepartmentInfo dept on dept.vDepartmentId=el.vDepartmentID where el.dApplicationDate " +
					"between '"+dFormat.format(dFromDate.getValue())+"' "
					+ "and '"+dFormat.format(dtoDate.getValue())+"' and el.vDepartmentID='DEPT10' ";
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbDepartmentName.addItem(element[0]);
				cmbDepartmentName.setItemCaption(element[0], element[1].toString());	
			}
		}

		catch(Exception ex)
		{
			showNotification("DepartmentDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void sectionDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select el.vSectionID,sein.SectionName from tbEmployeeLeave el inner join " +
					"tbSectionInfo sein on sein.vSectionID=el.vSectionID where el.dApplicationDate " +
					"between '"+dFormat.format(dFromDate.getValue())+"' and '"+dFormat.format(dtoDate.getValue())+"' " +
					"and el.vDepartmentID='"+cmbDepartmentName.getValue()+"'";
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());	
			}
		}

		catch(Exception ex){
			showNotification("sectionDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventActions()
	{
		opgTypeOfSearch.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(opgTypeOfSearch.getValue().toString().trim().equals("Employee Name"))
				{
					lblEmployeeName.setValue("Employee Name : ");
					tableclear();
					addEmployeeData();
				}

				else if(opgTypeOfSearch.getValue().toString().trim().equals("Employee ID"))
				{
					lblEmployeeName.setValue("Employee ID : ");
					tableclear();
					addEmployeeData();
				}

				else if(opgTypeOfSearch.getValue().toString().trim().equals("Proximity ID"))
				{
					lblEmployeeName.setValue("Proximity ID : ");
					tableclear();
					addEmployeeData();
				}
			}
		});

		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartmentName.removeAllItems();
				if(dFromDate.getValue()!=null)
				{
					DepartmentDataAdd();
				}
			}
		});

		dtoDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartmentName.removeAllItems();
				if(dtoDate.getValue()!=null)
				{
					DepartmentDataAdd();
				}
			}
		});

		cmbDepartmentName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbDepartmentName.getValue()!=null)
				{
					sectionDataAdd();
				}
			}
		});
		
		cmbSectionName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSectionName.getValue()!=null)
				{
					Section=cmbSectionName.getValue().toString();
					opgTypeOfSearch.setValue("Employee Name");
					addEmployeeData();
				}
			}
		});

		ChkSection.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgTypeOfSearch.setValue("Employee Name");
				if(ChkSection.booleanValue()==true)
				{
					Section="%";
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
					cmbEmpName.focus();
					addEmployeeData();
				}
				else
				{
					cmbSectionName.setEnabled(true);
					cmbEmpName.removeAllItems();
					addEmployeeData();
				}
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbSectionName.getValue()==null && ChkSection.booleanValue()==false)
				{
					showNotification("Please Select Section Name ", Notification.TYPE_WARNING_MESSAGE);
					cmbSectionName.focus();
				}

				else if(cmbEmpName.getValue()==null &&ChkEmployee.booleanValue()==false)
				{
					showNotification("Please Select Employee Name ", Notification.TYPE_WARNING_MESSAGE);
					cmbEmpName.focus();
				}

				else 
				{
					findButtonEvent();
				}
			}
		});

		ChkEmployee.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				boolean bv = ChkEmployee.booleanValue();
				if(bv==true)
				{
					cmbEmpName.setValue(null);
					cmbEmpName.setEnabled(false);
					dFromDate.focus();
				}
				else
				{
					cmbEmpName.setEnabled(true);
				}
			}
		});

		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					autoId = lblautoID.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtAutoId.setValue(autoId);

					txtFindDate.setValue(lbAppDate.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					type=opgTypeOfSearch.getValue().toString().trim();
					windowClose();
				}
			}
		});
	}

	private void findButtonEvent()
	{
		String Section = "";
		String Employee = "";
		// Section
		if(ChkSection.booleanValue() == true)
		{
			Section = "%";
		}
		else
		{
			Section = cmbSectionName.getValue().toString();
		}
		// Designation
		if(ChkEmployee.booleanValue() == true)
		{
			Employee = "%";
		}
		else
		{
			Employee = cmbEmpName.getValue().toString();
		}

		String from = dFormat.format(dFromDate.getValue())+" 00:00:00";
		String to = dFormat.format(dtoDate.getValue())+" 23:59:59";
		
		

		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Findquery = " SELECT EL.iAutoId,EL.vAutoEmployeeId,EL.vEmployeeId,(select vEmployeeName from tbEmployeeInfo "
					+ "ein where ein.vEmployeeId=EL.vAutoEmployeeId) EmployeeName,EL.dApplicationDate,D.designationName from "
					+ "tbEmployeeLeave as EL inner join tbDesignationInfo as D on EL.vDesignationId=D.designationId "
					+ "where EL.vDepartmentID='"+cmbDepartmentName.getValue()+"' and EL.vSectionID like '"+Section+"' "
					+ "and EL.vAutoEmployeeId like '"+Employee+"' and EL.dApplicationDate "
					+ "between '"+from+"' and '"+to+"' ";

			List <?> list = session.createSQLQuery(Findquery).list();

			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					lblautoID.get(i).setValue(element[0]);
					lbEmpID.get(i).setValue(element[1]);
					lblEmployeeId.get(i).setValue(element[2]);
					lblEmpName.get(i).setValue(element[3].toString());
					lbAppDate.get(i).setValue((element[4]));
					lbDesignation.get(i).setValue(element[5].toString());
					
					if((i)==lbAppDate.size()-1)
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
		finally{session.close();}
	}

	private void tableclear()
	{
		for(int i=0; i<lbSL.size(); i++)
		{
			lblautoID.get(i).setValue("");
			lbEmpID.get(i).setValue("");
			lblEmployeeId.get(i).setValue("");
			lblEmpName.get(i).setValue("");
			lbAppDate.get(i).setValue("");
			lbDesignation.get(i).setValue("");
		}
	}

	private void addEmployeeData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="";
			cmbEmpName.removeAllItems();
			if(opgTypeOfSearch.getValue().toString().trim().equals("Employee Name"))
			{
				query = " select a.vEmployeeId, a.vEmployeeName from tbEmployeeInfo as a inner join tbEmployeeLeave as " +
						"b on a.vEmployeeId=b.vAutoEmployeeId where b.dApplicationDate between '"+dFormat.format(dFromDate.getValue())+"' " +
						"and '"+dFormat.format(dtoDate.getValue())+"' and b.vDepartmentID='"+cmbDepartmentName.getValue()+"' and b.vSectionId like '"+Section+"' ";
			}
			
			else if(opgTypeOfSearch.getValue().toString().trim().equals("Employee ID"))
			{
				query = " select a.vEmployeeId, a.employeeCode from tbEmployeeInfo as a inner join tbEmployeeLeave as " +
						"b on a.vEmployeeId=b.vAutoEmployeeId where b.dApplicationDate between '"+dFormat.format(dFromDate.getValue())+"' " +
						"and '"+dFormat.format(dtoDate.getValue())+"' and b.vDepartmentID='"+cmbDepartmentName.getValue()+"' and b.vSectionId like '"+Section+"' ";
			}
			
			else if(opgTypeOfSearch.getValue().toString().trim().equals("Proximity ID"))
			{
				query = " select a.vEmployeeId, a.vProximityID from tbEmployeeInfo as a inner join tbEmployeeLeave as " +
						"b on a.vEmployeeId=b.vAutoEmployeeId where b.dApplicationDate between '"+dFormat.format(dFromDate.getValue())+"' " +
						"and '"+dFormat.format(dtoDate.getValue())+"' and b.vDepartmentID='"+cmbDepartmentName.getValue()+"' and b.vSectionId like '"+Section+"' ";
			}
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbEmpName.addItem(element[0]);
				cmbEmpName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			showNotification("addEmployeeData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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

		setWidth("600px");
		setHeight("475px");

		lblFormDate = new Label("From Date :");
		lblFormDate.setImmediate(true);
		lblFormDate.setWidth("-1px");
		lblFormDate.setHeight("-1px");
		mainLayout.addComponent(lblFormDate, "top:10.0px;left:25.0px;");

		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setInvalidAllowed(false);
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110");
		mainLayout.addComponent(dFromDate, "top:08.0px;left:140.0px;");

		lblToDate = new Label("To Date :");
		lblToDate.setImmediate(true);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:10.0px;left:285.0px;");

		dtoDate.setValue(new java.util.Date());
		dtoDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dtoDate.setDateFormat("dd-MM-yyyy");
		dtoDate.setInvalidAllowed(false);
		dtoDate.setImmediate(true);
		dtoDate.setWidth("110");
		mainLayout.addComponent(dtoDate, "top:08.0px;left:340.0px;");

		lblDepartmentName = new Label();
		lblDepartmentName.setImmediate(true);
		lblDepartmentName.setWidth("-1px");
		lblDepartmentName.setHeight("-1px");
		lblDepartmentName.setValue("Department Name  :");
		mainLayout.addComponent(lblDepartmentName, "top:35.0px;left:25.0px;");

		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setWidth("250px");
		cmbDepartmentName.setHeight("24px");
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbDepartmentName, "top:33.0px;left:140.0px;");
		
		lblSectionname = new Label();
		lblSectionname.setImmediate(true);
		lblSectionname.setWidth("-1px");
		lblSectionname.setHeight("-1px");
		lblSectionname.setValue("Section Name  :");
		mainLayout.addComponent(lblSectionname, "top:60.0px;left:25.0px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("250px");
		cmbSectionName.setHeight("24px");
		cmbSectionName.setImmediate(true);
		cmbSectionName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbSectionName, "top:58.0px;left:140.0px;");

		ChkSection = new CheckBox("All");
		ChkSection.setWidth("-1px");
		ChkSection.setHeight("24px");
		ChkSection.setImmediate(true);
		mainLayout.addComponent(ChkSection, "top:60.0px;left:395.0px;");

		opgTypeOfSearch=new OptionGroup("",lst);
		opgTypeOfSearch.setImmediate(true);
		opgTypeOfSearch.setStyleName("horizontal");
		opgTypeOfSearch.select("Employee Name");
		mainLayout.addComponent(opgTypeOfSearch, "top:85.0px;left:20.0px;");

		lblEmployeeName = new Label();
		lblEmployeeName.setImmediate(true);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		lblEmployeeName.setValue("Employee Name :");
		mainLayout.addComponent(lblEmployeeName, "top:110.0px;left:25.0px;");

		cmbEmpName = new ComboBox();
		cmbEmpName.setImmediate(true);
		cmbEmpName.setWidth("250px");
		cmbEmpName.setHeight("24px");
		cmbEmpName.setImmediate(true);
		cmbEmpName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbEmpName, "top:108.0px;left:140.0px;");

		ChkEmployee = new CheckBox("All");
		ChkEmployee.setImmediate(false);
		ChkEmployee.setWidth("-1px");
		ChkEmployee.setHeight("24px");
		ChkEmployee.setImmediate(true);
		mainLayout.addComponent(ChkEmployee, "top:110.0px;left:395.0px;");

		cButton.btnFind.setWidth("80px");
		cButton.btnFind.setHeight("28px");
		mainLayout.addComponent(cButton.btnFind, "top:106.0px;left:440.0px;");

		mainLayout.addComponent(table, "top:135.0px;left:15.0px;");
		return mainLayout;
	}
}
