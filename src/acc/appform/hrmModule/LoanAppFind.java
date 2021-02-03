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
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LoanAppFind extends Window 
{
	@SuppressWarnings("unused")
	private SessionBean sessionBean;	
	private AbsoluteLayout mainLayout;
	private ComboBox cmbEmpName ;
	private Label lblEmployeeName;
	private Label lblDepartmentname;
	private Label lblSectionname;
	private Label lblFormDate;
	private Label lblToDate;
	private ComboBox cmbDepartmentName;
	private ComboBox cmbSectionName;
	private PopupDateField dFromDate=new PopupDateField();
	private PopupDateField dtoDate=new PopupDateField();
	private Table table = new Table();
	private ArrayList<Label> lbSL = new ArrayList<Label>();
	private ArrayList<Label> lbLoanNo = new ArrayList<Label>();
	private ArrayList<Label> lbAppDate = new ArrayList<Label>();
	private ArrayList<Label> lbDesignation = new ArrayList<Label>();
	private ArrayList<Label> lbEmpName = new ArrayList<Label>();

	private CheckBox ChkSection;
	private CheckBox ChkEmployee;
	ArrayList<Component> allComp = new ArrayList<Component>();
	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	String autoId = "";
	String empId = "";
	String findDate = "a";

	private TextRead txtAutoId = new TextRead();
	private TextRead txtFindDate = new TextRead();

	public LoanAppFind(SessionBean sessionBean, TextRead txtAuto, TextRead txtFindDate)
	{		
		this.txtAutoId = txtAuto;
		this.txtFindDate = txtFindDate;
		this.sessionBean = sessionBean;
		this.setCaption("FIND LOAN INFORMATION :: "+sessionBean.getCompany());
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
		table.setHeight("230px");
		table.setPageLength(0);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);
		
		table.addContainerProperty("Loan No", Label.class , new Label());
		table.setColumnWidth("Loan No",45);

		table.addContainerProperty("App. Date", Label.class , new Label());
		table.setColumnWidth("App. Date",65);

		table.addContainerProperty("Emp Name", Label.class , new Label());
		table.setColumnWidth("Emp Name",150);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",160);
		rowAddinTable();	
	}

	public void rowAddinTable()
	{
		for(int i=0; i<8; i++)
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

		lbLoanNo.add(ar,new Label());
		lbLoanNo.get(ar).setWidth("100%");
		lbLoanNo.get(ar).setHeight("14px");

		lbAppDate.add(ar, new Label(""));
		lbAppDate.get(ar).setWidth("100%");
		lbAppDate.get(ar).setHeight("14px");
		lbAppDate.get(ar).setStyleName("appDate");

		lbDesignation.add(ar, new Label(""));
		lbDesignation.get(ar).setWidth("100%");
		
		lbEmpName.add(ar, new Label(""));
		lbEmpName.get(ar).setWidth("100%");
		table.addItem(new Object[]{lbSL.get(ar),lbLoanNo.get(ar),lbAppDate.get(ar),lbDesignation.get(ar),lbEmpName.get(ar)},ar);
	}
	
	private void DepartmentDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
						   "tbLoanApplication ein on dept.vDepartmentID=ein.vDepartmentId order by dept.vDepartmentName";
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

	private void sectionDataAdd(String DepartmentID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct ein.vSectionId,sein.SectionName from tbSectionInfo sein inner join " +
						   "tbLoanApplication ein on sein.vSectionId=ein.vSectionId where " +
						   "ein.vDepartmentID='"+DepartmentID+"' order by sein.SectionName";
			List <?> list = session.createSQLQuery(query).list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element =  (Object[]) iter.next();	
				cmbSectionName.addItem(element[0]);
				cmbSectionName.setItemCaption(element[0], element[1].toString());	
			}
		}
		catch(Exception ex)
		{
			showNotification("sectionDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventActions()
	{
		cmbDepartmentName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				if(cmbDepartmentName.getValue()!=null)
				{
					sectionDataAdd(cmbDepartmentName.getValue().toString());
				}
			}
		});
		
		cmbSectionName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpName.removeAllItems();
				if(cmbSectionName.getValue()!=null)
				{
					addEmployeeData(cmbSectionName.getValue().toString());
				}
			}
		});

		ChkSection.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmpName.removeAllItems();
				if(ChkSection.booleanValue()==true)
				{
					String all="%";
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
					cmbEmpName.focus();
					addEmployeeData(all);
				}
				else
				{
					cmbSectionName.setEnabled(true);
					cmbEmpName.removeAllItems();
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
					autoId = lbLoanNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtAutoId.setValue(autoId);
					
					txtFindDate.setValue(lbAppDate.get(Integer.valueOf(event.getItemId().toString())).getValue());
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
			String Findquery = " SELECT b.vLoanNo,b.dApplicationDate,a.vEmployeeName,d.designationName from tbEmployeeInfo as a" +
					" inner join tbLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId inner join tbDesignationInfo as d " +
					" on a.vDesignationId=d.designationId where a.vDepartmentID='"+cmbDepartmentName.getValue()+"' and " +
					" a.vSectionId like '"+Section+"' and a.vEmployeeId like '"+Employee+"' and" +
					" b.dApplicationDate between '"+from+"' and '"+to+"' order by b.dApplicationDate ";
			List <?> list = session.createSQLQuery(Findquery).list();
			
			if(!list.isEmpty())
			{
				tableclear();
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					lbLoanNo.get(i).setValue(element[0]);
					lbAppDate.get(i).setValue((element[1]));
					lbEmpName.get(i).setValue(element[2].toString());
					lbDesignation.get(i).setValue(element[3].toString());
					
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
			lbLoanNo.get(i).setValue("");
			lbAppDate.get(i).setValue("");
			lbDesignation.get(i).setValue("");
		}
	}
	
	private void addEmployeeData(String section)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = " SELECT vEmployeeID, employeeCode from tbEmployeeInfo where " +
					"vEmployeeID in (select vAutoEmployeeID from tbLoanApplication where " +
					"vDepartmentID='"+cmbDepartmentName.getValue()+"' and vSectionId like '"+section+"')";
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

		setWidth("560px");
		setHeight("400px");

		lblDepartmentname = new Label();
		lblDepartmentname.setImmediate(true);
		lblDepartmentname.setWidth("-1px");
		lblDepartmentname.setHeight("-1px");
		lblDepartmentname.setValue("Department Name  :");
		mainLayout.addComponent(lblDepartmentname, "top:10.0px;left:15.0px;");

		cmbDepartmentName = new ComboBox();
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setWidth("210px");
		cmbDepartmentName.setHeight("24px");
		cmbDepartmentName.setImmediate(true);
		cmbDepartmentName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbDepartmentName, "top:08.0px;left:130.0px;");
		
		lblSectionname = new Label();
		lblSectionname.setImmediate(true);
		lblSectionname.setWidth("-1px");
		lblSectionname.setHeight("-1px");
		lblSectionname.setValue("Section Name  :");
		mainLayout.addComponent(lblSectionname, "top:35.0px;left:15.0px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("210px");
		cmbSectionName.setHeight("24px");
		cmbSectionName.setImmediate(true);
		cmbSectionName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbSectionName, "top:33.0px;left:130.0px;");

		ChkSection = new CheckBox("All");
		ChkSection.setWidth("-1px");
		ChkSection.setHeight("24px");
		ChkSection.setImmediate(true);
		mainLayout.addComponent(ChkSection, "top:35.0px;left:345.0px;");

		lblEmployeeName = new Label();
		lblEmployeeName.setImmediate(true);
		lblEmployeeName.setWidth("-1px");
		lblEmployeeName.setHeight("-1px");
		lblEmployeeName.setValue("Employee ID :");
		mainLayout.addComponent(lblEmployeeName, "top:60.0px;left:15.0px;");

		cmbEmpName = new ComboBox();
		cmbEmpName.setImmediate(true);
		cmbEmpName.setWidth("210px");
		cmbEmpName.setHeight("24px");
		cmbEmpName.setImmediate(true);
		cmbEmpName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbEmpName, "top:58.0px;left:130.0px;");

		ChkEmployee = new CheckBox("All");
		ChkEmployee.setImmediate(false);
		ChkEmployee.setWidth("-1px");
		ChkEmployee.setHeight("24px");
		ChkEmployee.setImmediate(true);
		mainLayout.addComponent(ChkEmployee, "top:60.0px;left:345.0px;");

		lblFormDate = new Label("Application Date :");
		lblFormDate.setImmediate(true);
		lblFormDate.setWidth("-1px");
		lblFormDate.setHeight("-1px");
		mainLayout.addComponent(lblFormDate, "top:85.0px;left:15.0px;");

		dFromDate.setValue(new java.util.Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setInvalidAllowed(false);
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110");
		mainLayout.addComponent(dFromDate, "top:83.0px;left:130.0px;");

		lblToDate = new Label("To");
		lblToDate.setImmediate(true);
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:85.0px;left:250.0px;");

		dtoDate.setValue(new java.util.Date());
		dtoDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dtoDate.setDateFormat("dd-MM-yyyy");
		dtoDate.setInvalidAllowed(false);
		dtoDate.setImmediate(true);
		dtoDate.setWidth("110");
		mainLayout.addComponent(dtoDate, "top:83.0px;left:275.0px;");

		cButton.btnFind.setWidth("80px");
		cButton.btnFind.setHeight("28px");
		mainLayout.addComponent(cButton.btnFind, "top:80.0px;left:430.0px;");

		mainLayout.addComponent(table, "top:120.0px;left:15.0px;");
		return mainLayout;
	}
}
