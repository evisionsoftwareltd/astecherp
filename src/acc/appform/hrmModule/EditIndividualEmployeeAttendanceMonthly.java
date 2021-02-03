package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import java.text.SimpleDateFormat;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class EditIndividualEmployeeAttendanceMonthly extends Window 
{
	private AbsoluteLayout mainLayout;

	private SessionBean sessionBean;
	private Label lblMonth = new Label();
	private PopupDateField dMonth = new PopupDateField();

	private Label lblDepartment = new Label();
	private ComboBox cmbDepartment = new ComboBox();

	private Label lblSection = new Label();
	private ComboBox cmbSection = new ComboBox();

	private Label lblShiftName = new Label("Shift Name : ");
	private List<?> shiftList = Arrays.asList(new Object[]{"General Shift","I Double Shift"});
	private OptionGroup opgShiftName;

	private List<?> employeeList = Arrays.asList(new Object[]{"Employee ID","Proximity ID","Employee Name"});
	private OptionGroup opgEmployee;
	private Label lblEmployee = new Label();
	private ComboBox cmbEmployee = new ComboBox();

	private Table table = new Table();
	private ArrayList<PopupDateField> tbDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbLblDayName = new ArrayList<Label>();
	private ArrayList<Label> tbLblStatus = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbDIndate = new ArrayList<PopupDateField>();
	private ArrayList<NumberField> tbInHour = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbInMin = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbInSec = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbInHour1 = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbInMin1 = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbInSec1 = new ArrayList<NumberField>();
	private ArrayList<PopupDateField> tbDOutDate = new ArrayList<PopupDateField>();
	private ArrayList<NumberField> tbOutHour = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbOutMin = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbOutSec = new ArrayList<NumberField>();
	private ArrayList<PopupDateField> tbDOutDate1 = new ArrayList<PopupDateField>();
	private ArrayList<NumberField> tbOutHour1 = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbOutMin1 = new ArrayList<NumberField>();
	private ArrayList<NumberField> tbOutSec1 = new ArrayList<NumberField>();
	private ArrayList<TextField> tbTxtReason = new ArrayList<TextField>();
	private ArrayList<TextField> tbTxtPermittedBy = new ArrayList<TextField>();

	private CommonButton cButton = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dbYearFormat = new SimpleDateFormat("yyyy");
	private SimpleDateFormat dbMonthFormat = new SimpleDateFormat("MM");

	private String notify="";

	LinkedHashMap<String, String> hmEmployeeInfo=new LinkedHashMap<String, String>();

	public EditIndividualEmployeeAttendanceMonthly(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("EDIT MONTHLY EMPLOYEE ATTENDANCE (INDIVIDUAL) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();

		btnIni(true);
		componentIni(true);
		cButtonAction();
		focusEnter();
		authenticationCheck();
		cmbDepartmentValueAdd();

		cButton.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			cButton.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			cButton.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			cButton.btnDelete.setVisible(false);
		}
	}

	private void cButtonAction()
	{
		dMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dMonth.getValue()!=null)
				{
					cmbDepartmentValueAdd();
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionValueAdd();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployee.setValue("Employee ID");
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeAddData(opgEmployee.getValue().toString());
				}
			}
		});

		opgShiftName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableColumnChange();
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				String type=opgEmployee.getValue().toString();
				cmbEmployee.removeAllItems();
				cmbEmployeeAddData(type);
			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				addTableData();
				addValueToHashMap();
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				newButtonEvent();
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(formValidation())
					saveButtonAction();
				else
					showNotification("Warning",notify,Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				refreshButtonEvent();
			}
		});

		cButton.btnExit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}

	private void cmbDepartmentValueAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentID,vDepartmentName from tbEmployeeMainAttendance where " +
					"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
					"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' and vDepartmentID!='DEPT10' ";
			List<?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator<?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentValueAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionValueAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vSectionID,vSectionName from tbEmployeeMainAttendance where " +
					"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
					"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' " +
					"and vDepartmentID = '"+cmbDepartment.getValue()+"'";
			List<?> lst=session.createSQLQuery(query).list();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object[] element=(Object[])itr.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionValueAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableColumnChange()
	{
		if(opgShiftName.getValue().toString()=="General Shift")
		{
			table.setColumnCollapsed("In Max (HH)", true);
			table.setColumnCollapsed("In Max (MM)", true);
			table.setColumnCollapsed("Out date (Max)", true);
			table.setColumnCollapsed("Out Max (HH)", true);
			table.setColumnCollapsed("Out Max (MM)", true);
		}
		else
		{
			table.setColumnCollapsed("In Max (HH)", false);
			table.setColumnCollapsed("In Max (MM)", false);
			table.setColumnCollapsed("Out date (Max)", false);
			table.setColumnCollapsed("Out Max (HH)", false);
			table.setColumnCollapsed("Out Max (MM)", false);
		}
	}

	private void cmbEmployeeAddData(String type)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="";
			if(type=="Employee ID")
			{
				query="select distinct vEmployeeID,employeeCode,eatt.employeeCode from tbEmployeeMainAttendance as eatt where " +
						"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
						"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' " +
						"and vDepartmentID = '"+cmbDepartment.getValue()+"' " +
						"and vSectionID = '"+cmbSection.getValue()+"' order by eatt.employeeCode";
				lblEmployee.setValue("Employee ID : ");
			}
			else if(type=="Proximity ID")
			{
				query="select distinct vEmployeeID,vProximityID,eatt.employeeCode from tbEmployeeMainAttendance as eatt where " +
						"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
						"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' " +
						"and vDepartmentID = '"+cmbDepartment.getValue()+"' " +
						"and vSectionID = '"+cmbSection.getValue()+"' order by eatt.employeeCode";
				lblEmployee.setValue("Proximity ID : ");
			}
			else if(type=="Employee Name")
			{
				query="select distinct vEmployeeID,vEmployeeName,eatt.employeeCode from tbEmployeeMainAttendance as eatt where " +
						"MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' " +
						"and Year(dDate)='"+dbYearFormat.format(dMonth.getValue())+"' " +
						"and vDepartmentID = '"+cmbDepartment.getValue()+"' " +
						"and vSectionID = '"+cmbSection.getValue()+"' order by eatt.employeeCode";
				lblEmployee.setValue("Employee Name : ");
			}

			List<?> lst=session.createSQLQuery(query).list();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object[] element=(Object[])itr.next();
				cmbEmployee.addItem(element[0]);
				cmbEmployee.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeAddData",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addTableData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select dTxtDate,vTxtDay,Convert(date,dInTime) InDate,DATEPART(HH,dInTime) InHour," +
					"DATEPART(MI,dInTime) InMin,'00' as InSec,DATEPART(HH,dInTimeMax) InHour1," +
					"DATEPART(MI,dInTimeMax) InMin1,'00' InSec1,Convert(date,dOutTime) OutDate,DATEPART(HH,dOutTime) " +
					"outHour,DATEPART(MI,dOutTime) outMin, '00' outSec,Convert(date,dOutTimeMax) OutDate1," +
					"DATEPART(HH,dOutTimeMax) outHour1,DATEPART(MI,dOutTimeMax) outMin1, '00' outSec1," +
					"(case when vTxtStatus like '%Holiday%'  Then 'H' when vTxtStatus like 'Present' then 'P' " +
					"when vTxtStatus like '%Leave%' then 'L' when vTxtStatus like '%Absent%' then 'A' end) " +
					"TxtStatus from funMonthlyEmployeeAttendance('"+dbDateFormat.format(dMonth.getValue())+"'," +
					"'"+cmbEmployee.getValue()+"','"+cmbDepartment.getValue()+"','"+cmbSection.getValue()+"')";
			List<?> lst = session.createSQLQuery(query).list();
			int i=0;
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object [] element = (Object [])itr.next();
				tbDate.get(i).setReadOnly(false);
				tbDate.get(i).setValue(element[0]);
				tbDate.get(i).setReadOnly(true);
				tbLblDayName.get(i).setValue(element[1]);
				tbLblStatus.get(i).setValue(element[17]);

				tbDIndate.get(i).setValue(element[2]!=null?element[2]:element[0]);
				tbInHour.get(i).setValue(element[3]!=null?element[3]:"");
				tbInMin.get(i).setValue(element[4]!=null?element[4]:"");
				tbInSec.get(i).setValue(element[2]!=null?element[5]:"");

				tbInHour1.get(i).setValue(element[6]!=null?element[6]:"");
				tbInMin1.get(i).setValue(element[7]!=null?element[7]:"");
				tbInSec1.get(i).setValue(element[2]!=null?element[8]:"");

				tbDOutDate.get(i).setValue(element[9]!=null?element[9]:element[0]);
				tbOutHour.get(i).setValue(element[10]!=null?element[10]:"");
				tbOutMin.get(i).setValue(element[11]!=null?element[11]:"");
				tbOutSec.get(i).setValue(element[9]!=null?element[12]:"");

				tbDOutDate1.get(i).setValue(element[13]!=null?element[13]:element[0]);
				tbOutHour1.get(i).setValue(element[14]!=null?element[14]:"");
				tbOutMin1.get(i).setValue(element[15]!=null?element[15]:"");
				tbOutSec1.get(i).setValue(element[13]!=null?element[16]:"");

				if(element[17].toString().equalsIgnoreCase("H"))
				{
					tbLblStatus.get(i).setStyleName("lbStyle");
				}

				if(i==tbDate.size()-1)
					tableRowAdd(i+1);
				i++;
			}
		}
		catch (Exception exp)
		{
			showNotification("addTableData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addValueToHashMap()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vDesignationId," +
					"din.designationName,din.designationSerial,otStatus from tbEmployeeInfo ein inner join " +
					"tbDesignationInfo din on ein.vDesignationId=din.designationId where " +
					"ein.vEmployeeId = '"+cmbEmployee.getValue()+"'";
			List<?> lstInfo=session.createSQLQuery(query).list();
			if(!lstInfo.isEmpty())
			{
				Object [] element = (Object[])lstInfo.iterator().next(); 
				hmEmployeeInfo.put("1", element[0].toString());
				hmEmployeeInfo.put("2", element[1].toString());
				hmEmployeeInfo.put("3", element[2].toString());
				hmEmployeeInfo.put("4", element[3].toString());
				hmEmployeeInfo.put("5", element[4].toString());
				hmEmployeeInfo.put("6", element[5].toString());
				hmEmployeeInfo.put("7", element[6].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addValueToHashMap", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void newButtonEvent() 
	{
		dMonth.focus();
		componentIni(false);
		btnIni(false);
	}

	private boolean formValidation()
	{
		boolean ret=false;
		int count=0;
		for(int i=0;i<tbDate.size();i++)
		{
			if(tbDate.get(i).getValue()!=null)
			{
				if(tbTxtReason.get(i).getValue().toString().trim().length()>0)
				{
					if(tbTxtPermittedBy.get(i).getValue().toString().trim().length()>0)
					{
						if(tbInHour.get(i).getValue().toString().trim().length()>0 && 
								tbOutHour.get(i).getValue().toString().trim().length()>0 && 
								tbInMin.get(i).getValue().toString().trim().length()>0 && 
								tbOutMin.get(i).getValue().toString().trim().length()>0)
						{
							ret=true;
							break;
						}
						else
						{
							notify="Please Provide Attendance Time!!!";
						}
					}
					else
					{
						notify="Please Provide Reason!!!";
						tbTxtPermittedBy.get(i).focus();
					}
				}
				else
				{
					notify="Please Provide Permitted By!!!";
					tbTxtReason.get(i).focus();
				}
				count++;
			}
		}
		if(count==0)
			notify="No Data Found!!!";
		return ret;
	}

	private void saveButtonAction()
	{
		try
		{
			MessageBox mb = new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Save Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "NO"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						insertData();
					}
				}
			});
		}
		catch(Exception ex)
		{
			showNotification("saveButtonAction", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void refreshButtonEvent() 
	{
		txtClear();
		componentIni(true);
		btnIni(true);
	}

	private void insertData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			/*--------------------------------------------------------Attendance---------------------------------------------------------*/			
			String query="Insert into tbEmployeeMainAttendance (dDate,vEmployeeID,employeeCode,vProximityID," +
					"vEmployeeName,vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationID," +
					"vDesignationName,iDesignationSerial,vShiftID,vShiftName,dInTimeOne,dOutTimeOne,dInTimeTwo," +
					"dOutTimeTwo,vEditFlag,vAttendanceFlag,bOtStatus,ishiftStatus) " +
					"values ";

			String InTimeOne="";
			String OutTimeOne="";
			String InTimeTwo="NULL";
			String OutTimeTwo="NULL";
			String holiday="";
			int workingTime=0;
			String AttendanceFlag="";
			int count = 0;
			String deleteDate="";

			for(int i=0;i<tbDate.size();i++)
			{
				if(tbDate.get(i).getValue()!=null)
				{
					if(tbTxtReason.get(i).getValue().toString().trim().length()>0 && tbTxtPermittedBy.get(i).getValue().toString().trim().length()>0)
					{
						InTimeOne=dbDateFormat.format(tbDIndate.get(i).getValue())+" "+tbInHour.get(i).getValue().toString()+":"+tbInMin.get(i).getValue().toString()+":00";
						OutTimeOne=dbDateFormat.format(tbDOutDate.get(i).getValue())+" "+tbOutHour.get(i).getValue().toString()+":"+tbOutMin.get(i).getValue().toString()+":00";
						if(tbInHour1.get(i).getValue().toString().trim().length()>0 && tbInMin1.get(i).getValue().toString().trim().length()>0)
							InTimeTwo="'"+dbDateFormat.format(tbDIndate.get(i).getValue())+" "+tbInHour1.get(i).getValue().toString()+":"+tbInMin1.get(i).getValue().toString()+":00'";
						if(tbOutHour1.get(i).getValue().toString().trim().length()>0 && tbOutMin1.get(i).getValue().toString().trim().length()>0)
							OutTimeTwo="'"+dbDateFormat.format(tbDOutDate1.get(i).getValue())+" "+tbOutHour1.get(i).getValue().toString()+":"+tbOutMin1.get(i).getValue().toString()+":00'";

						if(count>0)
						{
							query+=",";
							deleteDate+=",";
						}
						String holidayCheck = "select dDate from tbHoliday where dDate='"+tbDate.get(i).getValue()+"'";
						List<?> lstHoliday=session.createSQLQuery(holidayCheck).list();
						System.out.println("Holiday : "+holiday);
						if(!lstHoliday.isEmpty())
							AttendanceFlag="HP";
						else
						{
							String workingHourCheck = "select DATEDIFF(SS,'"+InTimeOne+"','"+OutTimeOne+"')";
							workingTime=Integer.parseInt(session.createSQLQuery(workingHourCheck).list().iterator().next().toString());
							System.out.println("Working Time : "+workingTime);
							if(workingTime>=25200)
								AttendanceFlag="PR";
							else
								AttendanceFlag="SA";
						}

						deleteDate+="'"+dbDateFormat.format(tbDate.get(i).getValue())+"'";
						query+="('"+dbDateFormat.format(tbDate.get(i).getValue())+"'," +
								"'"+cmbEmployee.getValue()+"'," +
								"'"+hmEmployeeInfo.get("1")+"'," +
								"'"+hmEmployeeInfo.get("2")+"'," +
								"'"+hmEmployeeInfo.get("3")+"'," +
								"'"+cmbDepartment.getValue()+"'," +
								"'"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"'," +
								"'"+cmbSection.getValue()+"'," +
								"'"+cmbSection.getItemCaption(cmbSection.getValue())+"'," +
								"'"+hmEmployeeInfo.get("4")+"'," +
								"'"+hmEmployeeInfo.get("5")+"'," +
								"'"+hmEmployeeInfo.get("6")+"'," +
								"'0','General'," +
								"'"+InTimeOne+"','"+OutTimeOne+"',"+InTimeTwo+","+OutTimeTwo+"," +
								"'Edited','"+AttendanceFlag+"','"+hmEmployeeInfo.get("7")+"','')";
						count++;
					}
				}
			}

			String checkQuery="select * from tbEmployeeMainAttendance where dDate in ("+deleteDate+")";
			List<?> chkList = session.createSQLQuery(checkQuery).list();
			/*--------------------------------------------------------Attendance EDIT & DELETE---------------------------------------------------------*/
			if(!chkList.isEmpty())
			{
				String updateQuery = "Insert into tbUDEmployeeMainAttendance(dDate,vEmployeeID,employeeCode," +
						"vProximityID,vEmployeeName,vDepartmentID,vDepartmentName,vSectionId,vSectionName," +
						"vDesignationID,vDesignationName,iDesignationSerial,vShiftID,vShiftName,dInTimeOne," +
						"dOutTimeOne,dInTimeTwo,dOutTimeTwo,vEditFlag,vAttendanceFlag,bOtStatus,ishiftStatus," +
						"vUDFlag,vUserName,vUserIP,dEntryTime) select dDate,vEmployeeID,employeeCode,vProximityID," +
						"vEmployeeName,vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationID," +
						"vDesignationName,iDesignationSerial,vShiftID,vShiftName,dInTimeOne,dOutTimeOne,dInTimeTwo," +
						"dOutTimeTwo,vEditFlag,vAttendanceFlag,bOtStatus,ishiftStatus,'NEW','','',GETDATE() " +
						"from tbEmployeeMainAttendance where dDate in ("+deleteDate+") and " +
						"vEmployeeID = '"+cmbEmployee.getValue()+"'";

				String deleteQuery = "delete from tbEmployeeMainAttendance where dDate in ("+deleteDate+") " +
						"and vEmployeeID = '"+cmbEmployee.getValue()+"'";

				session.createSQLQuery(updateQuery).executeUpdate();
				session.createSQLQuery(deleteQuery).executeUpdate();
			}
			session.createSQLQuery(query).executeUpdate();
			/*--------------------------------------------------------Attendance EDIT & DELETE---------------------------------------------------------*/			

			/*--------------------------------------------------------Attendance EDIT---------------------------------------------------------*/
			String updateQuery1 = "Insert into tbUDEmployeeMainAttendance(dDate,vEmployeeID,employeeCode," +
					"vProximityID,vEmployeeName,vDepartmentID,vDepartmentName,vSectionId,vSectionName," +
					"vDesignationID,vDesignationName,iDesignationSerial,vShiftID,vShiftName,dInTimeOne," +
					"dOutTimeOne,dInTimeTwo,dOutTimeTwo,vEditFlag,vAttendanceFlag,bOtStatus,ishiftStatus," +
					"vUDFlag,vUserName,vUserIP,dEntryTime) select dDate,vEmployeeID,employeeCode,vProximityID," +
					"vEmployeeName,vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationID," +
					"vDesignationName,iDesignationSerial,vShiftID,vShiftName,dInTimeOne,dOutTimeOne,dInTimeTwo," +
					"dOutTimeTwo,vEditFlag,vAttendanceFlag,bOtStatus,ishiftStatus,'UPDATE','','',GETDATE() " +
					"from tbEmployeeMainAttendance where dDate in ("+deleteDate+") and " +
					"vEmployeeID = '"+cmbEmployee.getValue()+"'";
			session.createSQLQuery(updateQuery1).executeUpdate();
			/*--------------------------------------------------------Attendance EDIT---------------------------------------------------------*/

			/*--------------------------------------------------------------------------Attendance--------------------------------------------------------------------------------------*/

			/*--------------------------------------------------------Salary Calculation---------------------------------------------------------*/
			String SalaryCheck="select * from tbSalary where autoEmployeeID='"+cmbEmployee.getValue()+"' " +
					"and MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and " +
					"YEAR(dDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
			List<?> lstSalary = session.createSQLQuery(SalaryCheck).list();
			if(!lstSalary.isEmpty())
			{
				String revenueStampQuery="select convert(varchar,convert(date,GETDATE())) Date1";
				List<?> lstRevenue=session.createSQLQuery(revenueStampQuery).list();
				String Date = lstRevenue.iterator().next().toString();

				String updateSalaryQuery="Insert into tbUdSalary (year,vMonthName,vAutoEmployeeID,empId," +
						"empCode,empName,shiftId,shiftName,empType,designation,SectionID,Section,joinDate," +
						"totalDaysofMonth,totalWorkingDay,Friday,present,absentDay,leaveDay,holiday,casualLeave," +
						"sickLeave,EarnedLeave,officialTour,Gross,basicSalary,houseRent,Conveyance,Medical," +
						"perDay,AttBonus,FridayAllowance,Subtotal,salaryCutAbsent,advanceSalary,incomeTax," +
						"Insurance,ProvidentFund,totalDeduction,Adjust,Less,payableAmount,otHour,otRate," +
						"totalOtTaka,userId,userIP,entryTime,vLoanNo,vLoanTransactionID,UDFlag,mDearnessAllowance," +
						"mFireAllowance,mRevenueStamp,vDesignationID,iDesignationSerial,vBankAccountNo,dDate," +
						"vDepartmentID,vDepartmentName,iTotalOTMin,iExtraOT,iLessOT,iTotalNormalOTMin," +
						"iTotalFridayOTMin,fridayAmount) select year,vMonthName,autoEmployeeID,empId,empCode," +
						"empName,shiftId,shiftName,empType,designation,SectionID,Section,joinDate,totalDaysofMonth," +
						"totalWorkingDay,Friday,present,absentDay,leaveDay,holiday,casualLeave,sickLeave," +
						"EarnedLeave,officialTour,Gross,basicSalary,houseRent,Conveyance,Medical,perDay," +
						"AttBonus,FridayAllowance,Subtotal,salaryCutAbsent,advanceSalary,incomeTax,Insurance," +
						"ProvidentFund,totalDeduction,Adjust,Less,payableAmount,itotalOTHour,otRate,totalOtTaka,userId," +
						"userIP,entryTime,vLoanNo,vLoanTransactionID,'OLD',mDearnessAllowance,mFireAllowance," +
						"mRevenueStamp,vDesignationID,iDesignationSerial,vBankAccountNo,dDate,vDepartmentID," +
						"vDepartmentName,iTotalOTMin,iExtraOT,iLessOT,iTotalNormalOTMin,iTotalFridayOTMin," +
						"fridayAmount from tbSalary where autoEmployeeID='"+cmbEmployee.getValue()+"' " +
						"and MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and " +
						"YEAR(dDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
				session.createSQLQuery(updateSalaryQuery).executeUpdate();

				String deleteSalaryQuery = "delete from tbSalary where autoEmployeeID='"+cmbEmployee.getValue()+"' " +
						"and MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and " +
						"YEAR(dDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
				session.createSQLQuery(deleteSalaryQuery).executeUpdate();

				String salaryGenerateQuery = "exec prcCalcMonthlySalary '"+Date+"'," +
						"'"+dbDateFormat.format(dMonth.getValue())+"'," +
						"'"+cmbEmployee.getValue()+"'," +
						"'"+cmbDepartment.getValue()+"'," +
						"'"+cmbSection.getValue()+"'," +
						"'0'," +
						"'"+sessionBean.getUserName()+"'," +
						"'"+sessionBean.getUserIp()+"'";
				session.createSQLQuery(salaryGenerateQuery).executeUpdate();

				String updateSalaryQuery1="Insert into tbUdSalary (year,vMonthName,vAutoEmployeeID,empId," +
						"empCode,empName,shiftId,shiftName,empType,designation,SectionID,Section,joinDate," +
						"totalDaysofMonth,totalWorkingDay,Friday,present,absentDay,leaveDay,holiday,casualLeave," +
						"sickLeave,EarnedLeave,officialTour,Gross,basicSalary,houseRent,Conveyance,Medical," +
						"perDay,AttBonus,FridayAllowance,Subtotal,salaryCutAbsent,advanceSalary,incomeTax," +
						"Insurance,ProvidentFund,totalDeduction,Adjust,Less,payableAmount,otHour,otRate," +
						"totalOtTaka,userId,userIP,entryTime,vLoanNo,vLoanTransactionID,UDFlag,mDearnessAllowance," +
						"mFireAllowance,mRevenueStamp,vDesignationID,iDesignationSerial,vBankAccountNo,dDate," +
						"vDepartmentID,vDepartmentName,iTotalOTMin,iExtraOT,iLessOT,iTotalNormalOTMin," +
						"iTotalFridayOTMin,fridayAmount) select year,vMonthName,autoEmployeeID,empId,empCode," +
						"empName,shiftId,shiftName,empType,designation,SectionID,Section,joinDate,totalDaysofMonth," +
						"totalWorkingDay,Friday,present,absentDay,leaveDay,holiday,casualLeave,sickLeave," +
						"EarnedLeave,officialTour,Gross,basicSalary,houseRent,Conveyance,Medical,perDay," +
						"AttBonus,FridayAllowance,Subtotal,salaryCutAbsent,advanceSalary,incomeTax,Insurance," +
						"ProvidentFund,totalDeduction,Adjust,Less,payableAmount,itotalOTHour,otRate,totalOtTaka,userId," +
						"userIP,entryTime,vLoanNo,vLoanTransactionID,'OLD',mDearnessAllowance,mFireAllowance," +
						"mRevenueStamp,vDesignationID,iDesignationSerial,vBankAccountNo,dDate,vDepartmentID," +
						"vDepartmentName,iTotalOTMin,iExtraOT,iLessOT,iTotalNormalOTMin,iTotalFridayOTMin," +
						"fridayAmount from tbSalary where autoEmployeeID='"+cmbEmployee.getValue()+"' " +
						"and MONTH(dDate)='"+dbMonthFormat.format(dMonth.getValue())+"' and " +
						"YEAR(dDate)='"+dbYearFormat.format(dMonth.getValue())+"'";
				session.createSQLQuery(updateSalaryQuery1).executeUpdate();
			}
			/*--------------------------------------------------------Salary Calculation---------------------------------------------------------*/

			txtClear();
			componentIni(true);
			btnIni(true);
			tx.commit();
			showNotification("All Information Saved Successfully.");
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("insertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("320px");
		table.setImmediate(true);

		table.addContainerProperty("date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("date",80);

		table.addContainerProperty("Day Name", Label.class , new Label());
		table.setColumnWidth("Day Name",80);

		table.addContainerProperty("Status", Label.class, new Label());
		table.setColumnWidth("Status", 50);

		table.addContainerProperty("In date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("In date",110);

		table.addContainerProperty("In (HH)", NumberField.class , new NumberField());
		table.setColumnWidth("In (HH)",35);

		table.addContainerProperty("In (MM)", NumberField.class , new NumberField());
		table.setColumnWidth("In (MM)",35);

		table.addContainerProperty("In (SS)", NumberField.class , new NumberField());
		table.setColumnWidth("In (SS)",35);

		table.addContainerProperty("In Max (HH)", NumberField.class , new NumberField());
		table.setColumnWidth("In Max (HH)",35);

		table.addContainerProperty("In Max (MM)", NumberField.class , new NumberField());
		table.setColumnWidth("In Max (MM)",35);

		table.addContainerProperty("In Max (SS)", NumberField.class , new NumberField());
		table.setColumnWidth("In Max (SS)",35);

		table.addContainerProperty("Out date", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("Out date",110);

		table.addContainerProperty("Out (HH)", NumberField.class , new NumberField());
		table.setColumnWidth("Out (HH)",35);

		table.addContainerProperty("Out (MM)", NumberField.class , new NumberField());
		table.setColumnWidth("Out (MM)",35);

		table.addContainerProperty("Out (SS)", NumberField.class , new NumberField());
		table.setColumnWidth("Out (SS)",35);

		table.addContainerProperty("Out date (Max)", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("Out date (Max)",110);

		table.addContainerProperty("Out Max (HH)", NumberField.class , new NumberField());
		table.setColumnWidth("Out Max (HH)",35);

		table.addContainerProperty("Out Max (MM)", NumberField.class , new NumberField());
		table.setColumnWidth("Out Max (MM)",35);

		table.addContainerProperty("Out Max (SS)", NumberField.class , new NumberField());
		table.setColumnWidth("Out Max (SS)",35);

		table.addContainerProperty("Reason", TextField.class , new TextField());
		table.setColumnWidth("Reason",120);

		table.addContainerProperty("Permitted By", TextField.class , new TextField());
		table.setColumnWidth("Permitted By",110);

		table.setColumnCollapsed("In (SS)", true);
		table.setColumnCollapsed("In Max (HH)", true);
		table.setColumnCollapsed("In Max (MM)", true);
		table.setColumnCollapsed("In Max (SS)", true);
		table.setColumnCollapsed("Out (SS)", true);
		table.setColumnCollapsed("Out date (Max)", true);
		table.setColumnCollapsed("Out Max (HH)", true);
		table.setColumnCollapsed("Out Max (MM)", true);
		table.setColumnCollapsed("Out Max (SS)", true);

		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT});

		rowAddInTable();
	}

	public void rowAddInTable()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tbDate.add(ar, new PopupDateField());
		tbDate.get(ar).setWidth("100%");
		tbDate.get(ar).setHeight("20.0px");
		tbDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDate.get(ar).setImmediate(true);
		tbDate.get(ar).setReadOnly(true);

		tbLblDayName.add(ar, new Label());
		tbLblDayName.get(ar).setWidth("100%");
		tbLblDayName.get(ar).setImmediate(true);

		tbLblStatus.add(ar, new Label());
		tbLblStatus.get(ar).setWidth("100%");
		tbLblStatus.get(ar).setImmediate(true);

		tbDIndate.add(ar, new PopupDateField());
		tbDIndate.get(ar).setWidth("100%");
		tbDIndate.get(ar).setImmediate(true);
		tbDIndate.get(ar).setDateFormat("dd-MM-yyyy");
		tbDIndate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);

		tbInHour.add(ar, new NumberField());
		tbInHour.get(ar).setImmediate(true);
		tbInHour.get(ar).setWidth("100%");
		tbInHour.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbInHour.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbInHour.get(ar).getValue().toString())>23)
					{
						tbInHour.get(ar).setValue("");
					}
				}
			}
		});

		tbInMin.add(ar, new NumberField());
		tbInMin.get(ar).setImmediate(true);
		tbInMin.get(ar).setWidth("100%");
		tbInMin.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbInMin.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbInMin.get(ar).getValue().toString())>59)
					{
						tbInMin.get(ar).setValue("");
					}
				}
			}
		});

		tbInSec.add(ar, new NumberField());
		tbInSec.get(ar).setImmediate(true);
		tbInSec.get(ar).setWidth("100%");
		tbInSec.get(ar).setEnabled(false);

		tbInHour1.add(ar, new NumberField());
		tbInHour1.get(ar).setImmediate(true);
		tbInHour1.get(ar).setWidth("100%");
		tbInHour1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbInHour1.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbInHour1.get(ar).getValue().toString())>23)
					{
						tbInHour1.get(ar).setValue("");
					}
				}
			}
		});

		tbInMin1.add(ar, new NumberField());
		tbInMin1.get(ar).setImmediate(true);
		tbInMin1.get(ar).setWidth("100%");
		tbInMin1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbInMin1.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbInMin1.get(ar).getValue().toString())>59)
					{
						tbInMin1.get(ar).setValue("");
					}
				}
			}
		});

		tbInSec1.add(ar, new NumberField());
		tbInSec1.get(ar).setImmediate(true);
		tbInSec1.get(ar).setWidth("100%");
		tbInSec1.get(ar).setEnabled(false);

		tbDOutDate.add(ar, new PopupDateField());
		tbDOutDate.get(ar).setWidth("100%");
		tbDOutDate.get(ar).setImmediate(true);
		tbDOutDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbDOutDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);

		tbOutHour.add(ar, new NumberField());
		tbOutHour.get(ar).setImmediate(true);
		tbOutHour.get(ar).setWidth("100%");
		tbOutHour.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbOutHour.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbOutHour.get(ar).getValue().toString())>23)
					{
						tbOutHour.get(ar).setValue("");
					}
				}
			}
		});

		tbOutMin.add(ar, new NumberField());
		tbOutMin.get(ar).setImmediate(true);
		tbOutMin.get(ar).setWidth("100%");
		tbOutMin.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbOutMin.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbOutMin.get(ar).getValue().toString())>59)
					{
						tbOutMin.get(ar).setValue("");
					}
				}
			}
		});

		tbOutSec.add(ar, new NumberField());
		tbOutSec.get(ar).setImmediate(true);
		tbOutSec.get(ar).setWidth("100%");
		tbOutSec.get(ar).setEnabled(false);

		tbDOutDate1.add(ar, new PopupDateField());
		tbDOutDate1.get(ar).setWidth("100%");
		tbDOutDate1.get(ar).setImmediate(true);
		tbDOutDate1.get(ar).setDateFormat("dd-MM-yyyy");
		tbDOutDate1.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);

		tbOutHour1.add(ar, new NumberField());
		tbOutHour1.get(ar).setImmediate(true);
		tbOutHour1.get(ar).setWidth("100%");
		tbOutHour1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbOutHour1.get(ar).getValue().toString().trim().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbOutHour1.get(ar).getValue().toString())>23)
					{
						tbOutHour1.get(ar).setValue("");
					}
				}
			}
		});

		tbOutMin1.add(ar, new NumberField());
		tbOutMin1.get(ar).setImmediate(true);
		tbOutMin1.get(ar).setWidth("100%");
		tbOutMin1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(tbOutMin1.get(ar).getValue().toString().length()>0)
				{
					checkTime(ar);
					if(Integer.parseInt(tbOutMin1.get(ar).getValue().toString())>59)
					{
						tbOutMin1.get(ar).setValue("");
					}
				}
			}
		});

		tbOutSec1.add(ar, new NumberField());
		tbOutSec1.get(ar).setImmediate(true);
		tbOutSec1.get(ar).setWidth("100%");
		tbOutSec1.get(ar).setEnabled(false);

		tbTxtReason.add(ar, new TextField());
		tbTxtReason.get(ar).setImmediate(true);
		tbTxtReason.get(ar).setWidth("100%");

		tbTxtPermittedBy.add(ar, new TextField());
		tbTxtPermittedBy.get(ar).setImmediate(true);
		tbTxtPermittedBy.get(ar).setWidth("100%");

		table.addItem(new Object[]{tbDate.get(ar),tbLblDayName.get(ar),tbLblStatus.get(ar),tbDIndate.get(ar),
				tbInHour.get(ar),tbInMin.get(ar),tbInSec.get(ar),tbInHour1.get(ar),tbInMin1.get(ar),
				tbInSec1.get(ar),tbDOutDate.get(ar),tbOutHour.get(ar),tbOutMin.get(ar),tbOutSec.get(ar),
				tbDOutDate1.get(ar),tbOutHour1.get(ar),tbOutMin1.get(ar),tbOutSec1.get(ar),tbTxtReason.get(ar),
				tbTxtPermittedBy.get(ar)},ar);
	}

	private void checkTime(int ind)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(opgShiftName.getValue()=="General Shift")
			{
				if(tbInHour.get(ind).getValue().toString().trim().length()>0 && tbInMin.get(ind).toString().trim().length()>0 &&  
						tbOutHour.get(ind).getValue().toString().trim().length()>0 && tbOutMin.get(ind).getValue().toString().trim().length()>0)
				{
					String InTimeOne = dbDateFormat.format(tbDIndate.get(ind).getValue())+" "+tbInHour.get(ind).getValue().toString()+":"+tbInMin.get(ind).getValue().toString()+":00";
					String OutTimeOne = dbDateFormat.format(tbDOutDate.get(ind).getValue())+" "+tbOutHour.get(ind).getValue().toString()+":"+tbOutMin.get(ind).getValue().toString()+":00";

					String workingHourCheck = "select DATEDIFF(SS,'"+InTimeOne+"','"+OutTimeOne+"')";
					int workingTime=Integer.parseInt(session.createSQLQuery(workingHourCheck).list().iterator().next().toString());

					if(workingTime>84600)
					{
						tbOutHour.get(ind).focus();
						tbOutHour.get(ind).setValue("");
						tbOutMin.get(ind).setValue("");
						if(opgShiftName.getValue()=="I Double Shift")
						{
							tbOutHour1.get(ind).setValue("");
							tbOutMin1.get(ind).setValue("");
						}
						showNotification("Warning", "Total duration can't exceed 23:30 hours!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}

			if(opgShiftName.getValue()=="I Double Shift")
			{
				String InTimeOne ="";
				String OutTimeOne = "";
				int workingTime = 0;
				if(tbInHour.get(ind).getValue().toString().trim().length()>0 && tbInMin.get(ind).toString().trim().length()>0)
					InTimeOne = dbDateFormat.format(tbDIndate.get(ind).getValue())+" "+tbInHour.get(ind).getValue().toString()+":"+tbInMin.get(ind).getValue().toString()+":00";
				if(tbOutHour.get(ind).getValue().toString().trim().length()>0 && tbOutMin.get(ind).getValue().toString().trim().length()>0)
					OutTimeOne= dbDateFormat.format(tbDOutDate.get(ind).getValue())+" "+tbOutHour.get(ind).getValue().toString()+":"+tbOutMin.get(ind).getValue().toString()+":00";
				if(InTimeOne.length()>0 && OutTimeOne.length()>0)
				{
					String workingHourCheck = "select DATEDIFF(SS,'"+InTimeOne+"','"+OutTimeOne+"')";
					workingTime=Integer.parseInt(session.createSQLQuery(workingHourCheck).list().iterator().next().toString());
				}

				String InTimeTwo = "";
				String OutTimeTwo = "";
				if(tbInHour1.get(ind).getValue().toString().trim().length()>0 && tbInMin1.get(ind).getValue().toString().trim().length()>0)
					InTimeTwo=dbDateFormat.format(tbDIndate.get(ind).getValue())+" "+tbInHour1.get(ind).getValue().toString()+":"+tbInMin1.get(ind).getValue().toString()+":00";
				if(tbOutHour1.get(ind).getValue().toString().trim().length()>0 && tbOutMin1.get(ind).getValue().toString().trim().length()>0)
					OutTimeTwo=dbDateFormat.format(tbDOutDate1.get(ind).getValue())+" "+tbOutHour1.get(ind).getValue().toString()+":"+tbOutMin1.get(ind).getValue().toString()+":00";
				if(InTimeTwo.length()>0 && OutTimeTwo.length()>0)
				{
					String workingHourCheck1 = "select DATEDIFF(SS,'"+InTimeTwo+"','"+OutTimeTwo+"')";
					workingTime+=Integer.parseInt(session.createSQLQuery(workingHourCheck1).list().iterator().next().toString());
				}
				
				if(workingTime>84600)
				{
					tbOutHour.get(ind).focus();
					tbOutHour.get(ind).setValue("");
					tbOutMin.get(ind).setValue("");
					if(opgShiftName.getValue()=="I Double Shift")
					{
						tbOutHour1.get(ind).setValue("");
						tbOutMin1.get(ind).setValue("");
					}
					showNotification("Warning", "Total duration can't exceed 23:30 hours!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("checkTime", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
	}

	private void componentIni(boolean b) 
	{
		dMonth.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		opgShiftName.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void txtClear()
	{
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0; i<tbDate.size(); i++)
		{
			tbDate.get(i).setReadOnly(false);
			tbDate.get(i).setValue(null);
			tbDate.get(i).setReadOnly(true);

			tbLblDayName.get(i).setValue("");
			tbLblStatus.get(i).setValue("");
			tbDIndate.get(i).setValue(null);
			tbInHour.get(i).setValue("");
			tbInMin.get(i).setValue("");
			tbInSec.get(i).setValue("");
			tbInHour1.get(i).setValue("");
			tbInMin1.get(i).setValue("");
			tbInSec1.get(i).setValue("");

			tbDOutDate.get(i).setValue(null);
			tbOutHour.get(i).setValue("");
			tbOutMin.get(i).setValue("");
			tbOutSec.get(i).setValue("");
			tbDOutDate1.get(i).setValue(null);
			tbOutHour1.get(i).setValue("");
			tbOutMin1.get(i).setValue("");
			tbOutSec1.get(i).setValue("");

			tbTxtReason.get(i).setValue("");
			tbTxtPermittedBy.get(i).setValue("");
		}
	}

	private void focusEnter()
	{
		allComp.add(dMonth);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);

		for(int i=0; i<tbDate.size();i++)
		{
			allComp.add(tbDIndate.get(i));
			allComp.add(tbInHour.get(i));
			allComp.add(tbInMin.get(i));
			allComp.add(tbDOutDate.get(i));
			allComp.add(tbOutHour.get(i));
			allComp.add(tbOutMin.get(i));
			allComp.add(tbTxtReason.get(i));
			allComp.add(tbTxtPermittedBy.get(i));
		}

		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		mainLayout.setWidth("1005px");
		mainLayout.setHeight("480px");

		lblMonth = new Label("Month :");
		lblMonth.setWidth("-1px");
		lblMonth.setHeight("-1px");
		mainLayout.addComponent(lblMonth, "top:10.0px;left:20.0px;");

		dMonth = new PopupDateField();
		dMonth.setValue(new Date());
		dMonth.setWidth("130px");
		dMonth.setHeight("24px");
		dMonth.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonth.setDateFormat("MMMMM-yyyy");
		dMonth.setInvalidAllowed(false);
		dMonth.setInputPrompt("Month");
		dMonth.setImmediate(true);
		mainLayout.addComponent(dMonth, "top:08.0px;left:140.0px;");

		lblDepartment = new Label("Department Name : ");
		lblDepartment.setWidth("-1px");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment, "top:35.0px;left:20.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("210px");
		cmbDepartment.setHeight("24px");
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbDepartment, "top:33.0px; left:140.0px;");

		lblSection = new Label("Section Name : ");
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:60.0px;left:20.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("210px");
		cmbSection.setHeight("24px");
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSection, "top:58.0px; left:140.0px;");

		lblShiftName = new Label("Shift Name");
		lblShiftName.setWidth("-1px");
		lblShiftName.setHeight("-1px");
		mainLayout.addComponent(lblShiftName, "top:10.0px; left:450.0px;");

		opgShiftName = new OptionGroup("",shiftList);
		opgShiftName.setImmediate(true);
		opgShiftName.setValue("General Shift");
		opgShiftName.setStyleName("horizontal");
		mainLayout.addComponent(opgShiftName, "top:08.0px; left:570.0px;");

		opgEmployee = new OptionGroup("", employeeList);
		opgEmployee.setImmediate(true);
		opgEmployee.setValue("Employee ID");
		opgEmployee.setStyleName("horizontal");
		mainLayout.addComponent(opgEmployee, "top:35.0px; left:570.0px;");

		lblEmployee = new Label("Employee ID : ");
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee, "top:60.0px;left:450.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("320px");
		cmbEmployee.setHeight("24px");
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee, "top:58.0px; left:570.0px;");

		mainLayout.addComponent(table, "top:100.0px; left:20.0px;");
		mainLayout.addComponent(cButton, "top:440.0px; left:330.0px;");
		return mainLayout;
	}
}
