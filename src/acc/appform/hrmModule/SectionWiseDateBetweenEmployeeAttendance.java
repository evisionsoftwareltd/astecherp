package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionFactoryUtil;
import com.common.share.SessionBean;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;

import java.text.SimpleDateFormat;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SectionWiseDateBetweenEmployeeAttendance extends Window 
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;

	private Label lblFromDate;
	private PopupDateField dFromDate;
	private Label lblToDate;
	private PopupDateField dToDate;

	private Label lblDepartment = new Label();
	private ComboBox cmbDepartment = new ComboBox();

	private Label lblSection = new Label();
	private ComboBox cmbSection = new ComboBox();

	private ComboBox cmbEmployeeId=new ComboBox();
	private CheckBox chkEmployeeAll=new CheckBox("All");
	
	private ComboBox cmbStatusType=new ComboBox();
	private CheckBox chkStatusTypeAll=new CheckBox("All");

	private Table table = new Table();
	private ArrayList<Label> tbLblAutoEmpID = new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> tbLblProximityID = new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tbLblDesignationID = new ArrayList<Label>();
	private ArrayList<Label> tbLblDesignation = new ArrayList<Label>();
	private ArrayList<Label> tblblDate = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbDate = new ArrayList<PopupDateField>();
	private ArrayList<Label> tbLblDayName = new ArrayList<Label>();
	private ArrayList<Label> tbLblStatus = new ArrayList<Label>();
	private ArrayList<Label> tbLblInTime = new ArrayList<Label>();
	private ArrayList<Label> tbLblOutTime = new ArrayList<Label>();
	private ArrayList<NativeButton> tbBtnEdit = new ArrayList<NativeButton>();
	private ArrayList<NativeButton> tbBtnDelete = new ArrayList<NativeButton>();

	ArrayList<Component> allComp = new ArrayList<Component>();
	private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat nDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	

	public SectionWiseDateBetweenEmployeeAttendance(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("SECTION WISE EDIT MONTHLY EMPLOYEE ATTENDANCE (DATE BETWEEN) :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		tableInitialize();
		setEventAction();
		focusEnter();
		cmbDepartmentValueAdd();
	}

	private void setEventAction()
	{
		dFromDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				tbButtonIni(true);
				if(dFromDate.getValue()!=null)
				{
					chkEmployeeAll.setValue(false);
					cmbEmployeeId.setValue(null);
					chkStatusTypeAll.setValue(false);
					cmbStatusType.setValue(null);
					cmbDepartmentValueAdd();
				}
			}
		});
		dToDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				tbButtonIni(true);
				if(dToDate.getValue()!=null)
				{
					chkEmployeeAll.setValue(false);
					cmbEmployeeId.setValue(null);
					chkStatusTypeAll.setValue(false);
					cmbStatusType.setValue(null);
					cmbDepartmentValueAdd();
				}
			}
		});
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				tbButtonIni(true);
				if(cmbDepartment.getValue()!=null)
				{					
					chkEmployeeAll.setValue(false);
					cmbEmployeeId.setValue(null);
					chkStatusTypeAll.setValue(false);
					cmbStatusType.setValue(null);
					cmbSectionValueAdd();
				}
			}
		});
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				tbButtonIni(true);
				chkStatusTypeAll.setValue(false);
				cmbStatusType.setValue(null);
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeValueAdd();
				}
			}
		});		
		cmbEmployeeId.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbEmployeeId.getValue()!=null)
				{
					tableclear();
					tbButtonIni(true);
					chkStatusTypeAll.setValue(false);
					cmbStatusType.setEnabled(true);
					cmbStatusType.setValue(null);
				}
			}
		});
		chkEmployeeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkEmployeeAll.booleanValue())
				{
					tableclear();
					tbButtonIni(true);
					cmbEmployeeId.setValue(null);
					cmbEmployeeId.setEnabled(false);
					
					chkStatusTypeAll.setValue(false);
					cmbStatusType.setEnabled(true);
					cmbStatusType.setValue(null);
				}
				else
				{
					cmbEmployeeId.setEnabled(true);
				}
			}
		});
		cmbStatusType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				tbButtonIni(true);
				if(dFromDate.getValue()!=null)
				{
					if(dToDate.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null)
						{
							if(cmbSection.getValue()!=null)
							{
								if(cmbEmployeeId.getValue()!=null | chkEmployeeAll.booleanValue())
								{
									if(cmbStatusType.getValue()!=null)
									{
										addTableData();
									}
								}
							}
						}
					}
				}
			}
		});
		chkStatusTypeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				tbButtonIni(true);
				if(dFromDate.getValue()!=null)
				{
					if(dToDate.getValue()!=null)
					{
						if(cmbDepartment.getValue()!=null)
						{
							if(cmbSection.getValue()!=null)
							{
								if(cmbEmployeeId.getValue()!=null | chkEmployeeAll.booleanValue())
								{
									if(chkStatusTypeAll.booleanValue())
									{
										cmbStatusType.setValue(null);
										cmbStatusType.setEnabled(false);
										addTableData();
									}
									else
									{
										cmbStatusType.setEnabled(true);
									}
								}
							}
						}
					}
				}
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
					"dDate between '"+dbDateFormat.format(dFromDate.getValue())+"' " +
					"and '"+dbDateFormat.format(dToDate.getValue())+"' and vDepartmentID!='DEPT10'  order by vDepartmentName";
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
					"dDate between '"+dbDateFormat.format(dFromDate.getValue())+"' " +
					"and '"+dbDateFormat.format(dToDate.getValue())+"' " +
					"and vDepartmentID = '"+cmbDepartment.getValue()+"'  order by vSectionName";
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
	
	private void cmbEmployeeValueAdd()
	{
		cmbEmployeeId.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vEmployeeID,employeeCode,vEmployeeName from tbEmployeeMainAttendance where " +
					"dDate between '"+dbDateFormat.format(dFromDate.getValue())+"' " +
					"and '"+dbDateFormat.format(dToDate.getValue())+"' " +
					"and vDepartmentID = '"+cmbDepartment.getValue()+"' and vSectionId = '"+cmbSection.getValue()+"' "
					+ "order by employeeCode";
			List<?> lst=session.createSQLQuery(query).list();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object[] element=(Object[])itr.next();
				cmbEmployeeId.addItem(element[0]);
				cmbEmployeeId.setItemCaption(element[0], element[1]+"-"+element[2]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeValueAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addTableData()
	{
		String statusType="%";
		String employeeId="%";
		if(cmbStatusType.getValue()!=null)
		{
			statusType=cmbStatusType.getValue().toString();
		}
		if(cmbEmployeeId.getValue()!=null)
		{
			employeeId=cmbEmployeeId.getValue().toString();
		}
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName," +
					"DesignationID,Designation,dAttendanceDate,vDayName,vStatus," +
					"ISNULL(substring(convert(varchar,convert(time,dInTimeMax)),1,8),'')+' '+" +
					"ISNULL(substring(convert(varchar,convert(time,dInTime)),1,8),'') InTime," +
					"ISNULL(substring(convert(varchar,convert(time,dOutTimeMax)),1,8),'')+' '+" +
					"ISNULL(substring(convert(varchar,convert(time,dOutTime)),1,8),'') OutTime," +
					"convert(date,dOutTime) outDate from funDeptWiseDateBetnAttendance" +
					"('"+dbDateFormat.format(dFromDate.getValue())+"'," +
					"'"+dbDateFormat.format(dToDate.getValue())+"','"+cmbDepartment.getValue()+"'," +
					"'"+cmbSection.getValue()+"') where vStatus like '"+statusType+"' and vEmployeeID like '"+employeeId+"' "
					+ "order by vEmployeeCode";
			List<?> lst = session.createSQLQuery(query).list();
			int i=0;
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object [] element = (Object [])itr.next();
				tbLblAutoEmpID.get(i).setValue(element[0]);
				tbLblEmployeeID.get(i).setValue(element[1]);
				tbLblProximityID.get(i).setValue(element[2]);
				tbLblEmployeeName.get(i).setValue(element[3]);
				tbLblDesignationID.get(i).setValue(element[4]);
				tbLblDesignation.get(i).setValue(element[5]);

				tblblDate.get(i).setValue(nDateFormat.format(element[6]));
				tbDate.get(i).setReadOnly(false);
				tbDate.get(i).setValue(element[6]);
				tbDate.get(i).setReadOnly(true);

				tbLblDayName.get(i).setValue(element[7]);
				if(element[8].toString().equals("Absent"))
				{
					tbLblStatus.get(i).setValue("<font color=#f44336>"+element[8]);
					tbLblStatus.get(i).setContentMode(Label.CONTENT_XHTML);
				}

				else if(element[8].toString().equals("Present"))
				{
					tbLblStatus.get(i).setValue("<font color=#000000>"+element[8]);
					tbLblStatus.get(i).setContentMode(Label.CONTENT_XHTML);
				}

				else if(element[8].toString().equals("Special Absent"))
				{
					tbLblStatus.get(i).setValue("<font color=#673ab7>"+element[8]);
					tbLblStatus.get(i).setContentMode(Label.CONTENT_XHTML);
				}
				else if(element[8].toString().equals("Holiday"))
				{
					tbLblStatus.get(i).setValue("<font color=#00AA00>"+element[8]);
					tbLblStatus.get(i).setContentMode(Label.CONTENT_XHTML);
				}

				tbLblInTime.get(i).setValue(element[9]);
				tbLblOutTime.get(i).setValue(element[10]+(element[11]!=null?"("+element[11]+")":""));
				tbBtnEdit.get(i).setEnabled(true);
				tbBtnDelete.get(i).setEnabled(true);

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

	private void tableInitialize()
	{
		table.setColumnCollapsingAllowed(true);
		table.setPageLength(0);
		table.setWidth("98%");
		table.setHeight("365px");
		table.setImmediate(true);

		table.addContainerProperty("EMP ID", Label.class , new Label());
		table.setColumnWidth("EMP ID",60);

		table.addContainerProperty("EMPLOYEE ID", Label.class , new Label());
		table.setColumnWidth("EMPLOYEE ID",100);

		table.addContainerProperty("PROXIMITY ID", Label.class , new Label());
		table.setColumnWidth("PROXIMITY ID",80);

		table.addContainerProperty("EMPLOYEE NAME", Label.class , new Label());
		table.setColumnWidth("EMPLOYEE NAME",120);

		table.addContainerProperty("DESIGNATION ID", Label.class , new Label());
		table.setColumnWidth("DESIGNATION ID",80);

		table.addContainerProperty("DESIGNATION", Label.class , new Label());
		table.setColumnWidth("DESIGNATION",75);

		table.addContainerProperty("DATE", Label.class , new Label());
		table.setColumnWidth("DATE",80);

		table.addContainerProperty("DDATE", PopupDateField.class , new PopupDateField());
		table.setColumnWidth("DDATE",80);

		table.addContainerProperty("DAY NAME", Label.class , new Label());
		table.setColumnWidth("DAY NAME",70);

		table.addContainerProperty("STATUS", Label.class , new Label());
		table.setColumnWidth("STATUS",45);

		table.addContainerProperty("IN TIME", Label.class , new Label());
		table.setColumnWidth("IN TIME",110);

		table.addContainerProperty("OUT TIME", Label.class , new Label());
		table.setColumnWidth("OUT TIME",110);

		table.addContainerProperty("EDIT", NativeButton.class , new NativeButton());
		table.setColumnWidth("EDIT",50);

		table.addContainerProperty("DELETE", NativeButton.class , new NativeButton());
		table.setColumnWidth("DELETE",60);

		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("PROXIMITY ID", true);
		table.setColumnCollapsed("DESIGNATION ID", true);
		table.setColumnCollapsed("DDATE", true);

		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER});

		rowAddInTable();
	}

	public void rowAddInTable()
	{
		for(int i=0;i<13;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		tbLblAutoEmpID.add(ar, new Label());
		tbLblAutoEmpID.get(ar).setHeight("13.0px");
		tbLblAutoEmpID.get(ar).setWidth("100%");
		tbLblAutoEmpID.get(ar).setStyleName("lblSmall");

		tbLblEmployeeID.add(ar, new Label());
		tbLblEmployeeID.get(ar).setWidth("100%");
		tbLblEmployeeID.get(ar).setStyleName("lblSmall");

		tbLblProximityID.add(ar, new Label());
		tbLblProximityID.get(ar).setWidth("100%");
		tbLblProximityID.get(ar).setStyleName("lblSmall");

		tbLblEmployeeName.add(ar, new Label());
		tbLblEmployeeName.get(ar).setWidth("100%");
		tbLblEmployeeName.get(ar).setStyleName("lblSmall");

		tbLblDesignationID.add(ar, new Label());
		tbLblDesignationID.get(ar).setWidth("100%");
		tbLblDesignationID.get(ar).setStyleName("lblSmall");

		tbLblDesignation.add(ar, new Label());
		tbLblDesignation.get(ar).setWidth("100%");
		tbLblDesignation.get(ar).setStyleName("lblSmall");

		tblblDate.add(ar, new Label());
		tblblDate.get(ar).setWidth("100%");
		tblblDate.get(ar).setStyleName("lblSmall");

		tbDate.add(ar, new PopupDateField());
		tbDate.get(ar).setStyleName("lblDate");
		tbDate.get(ar).setWidth("100%");
		tbDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbDate.get(ar).setImmediate(true);
		tbDate.get(ar).setReadOnly(true);

		tbLblDayName.add(ar, new Label());
		tbLblDayName.get(ar).setWidth("100%");
		tbLblDayName.get(ar).setStyleName("lblSmall");

		tbLblStatus.add(ar, new Label());
		tbLblStatus.get(ar).setImmediate(true);
		tbLblStatus.get(ar).setWidth("100%");
		tbLblStatus.get(ar).setStyleName("lblSmall");

		tbLblInTime.add(ar, new Label());
		tbLblInTime.get(ar).setWidth("100%");
		tbLblInTime.get(ar).setStyleName("lblSmall");

		tbLblOutTime.add(ar, new Label());
		tbLblOutTime.get(ar).setWidth("100%");
		tbLblOutTime.get(ar).setStyleName("lblSmall");

		tbBtnEdit.add(ar, new NativeButton("EDIT"));
		tbBtnEdit.get(ar).setStyleName("btnNative");
		tbBtnEdit.get(ar).setImmediate(true);
		tbBtnEdit.get(ar).setWidth("45px");
		tbBtnEdit.get(ar).setHeight("20px");
		tbBtnEdit.get(ar).setEnabled(false);
		tbBtnEdit.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				componentIni(true);
				Window EditWindow = new SectionWiseDateBtnEditDeleteAttendance(sessionBean,dbDateFormat.format(tbDate.get(ar).getValue()),tbLblAutoEmpID.get(ar).getValue().toString());
				EditWindow.center();
				EditWindow.setStyleName("cwindow");
				EditWindow.addListener(new CloseListener()
				{
					public void windowClose(CloseEvent e)
					{
						int Index=ar;
						tbLblInTime.get(ar).setValue("");
						tbLblOutTime.get(ar).setValue("");
						addRowData(tbLblAutoEmpID.get(ar).getValue().toString(),Index);
						componentIni(false);
					}
				});
				getParent().getWindow().addWindow(EditWindow);
			}
		});

		tbBtnDelete.add(ar, new NativeButton("DELETE"));
		tbBtnDelete.get(ar).setStyleName("btnNative");
		tbBtnDelete.get(ar).setImmediate(true);
		tbBtnDelete.get(ar).setWidth("55px");
		tbBtnDelete.get(ar).setHeight("20px");
		tbBtnDelete.get(ar).setEnabled(false);
		tbBtnDelete.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!tbLblInTime.get(ar).getValue().toString().trim().isEmpty() && !tbLblOutTime.get(ar).getValue().toString().trim().isEmpty())
				{
					int Index=ar;
					if(!checkSalary(Index))
						DeleteButtonEvent(dbDateFormat.format(tbDate.get(ar).getValue()),tbLblAutoEmpID.get(ar).getValue().toString(),Index);
					else
						showNotification("Warning", "Salary already generated!!!", Notification.TYPE_WARNING_MESSAGE);
				}
				else
				{
					showNotification("Warning", "There are No Data for Deleting!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		table.addItem(new Object[]{tbLblAutoEmpID.get(ar),tbLblEmployeeID.get(ar),tbLblProximityID.get(ar),
				tbLblEmployeeName.get(ar),tbLblDesignationID.get(ar),tbLblDesignation.get(ar),tblblDate.get(ar),
				tbDate.get(ar),tbLblDayName.get(ar),tbLblStatus.get(ar),tbLblInTime.get(ar),tbLblOutTime.get(ar),
				tbBtnEdit.get(ar),tbBtnDelete.get(ar)},ar);
	}

	private boolean checkSalary(int index)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select * from tbSalary where autoEmployeeID = '"+tbLblAutoEmpID.get(index).getValue().toString()+"' and MONTH(dDate) = MONTH('"+dbDateFormat.format(tbDate.get(index).getValue())+"') and " +
					"YEAR(dDate) = YEAR('"+dbDateFormat.format(tbDate.get(index).getValue())+"')";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("checkSalary", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void addRowData(String EmpID,int index)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select (case when vAttendanceFlag='PR' then 'Present' when vAttendanceFlag='SA' then 'Special Absent' when vAttendanceFlag='HP' then 'Holiday' end) vStatus," +
					"ISNULL(substring(convert(varchar,convert(time,dIntimeTwo)),1,8),'')+' '+ISNULL(substring(convert(varchar,convert(time,dIntimeOne)),1,8),'') dIntime," +
					"ISNULL(substring(convert(varchar,convert(time,dOutTimeTwo)),1,8),'')+' '+ISNULL(substring(convert(varchar,convert(time,dOutTimeOne)),1,8),'') dOutTime,convert(date,dOutTimeOne) from " +
					"tbEmployeeMainAttendance where dDate='"+dbDateFormat.format(tbDate.get(index).getValue())+"' and " +
					"vEmployeeID='"+tbLblAutoEmpID.get(index).getValue().toString()+"'";
			List<?> lst = session.createSQLQuery(query).list();
			for(Iterator<?> itr=lst.iterator();itr.hasNext();)
			{
				Object [] element = (Object [])itr.next();

				if(element[0].toString().equals("Present"))
				{
					tbLblStatus.get(index).setValue("<font color=#000000>"+element[0]);
					tbLblStatus.get(index).setContentMode(Label.CONTENT_XHTML);
				}

				else if(element[0].toString().equals("Special Absent"))
				{
					tbLblStatus.get(index).setValue("<font color=#673ab7>"+element[0]);
					tbLblStatus.get(index).setContentMode(Label.CONTENT_XHTML);
				}
				else if(element[0].toString().equals("Holiday"))
				{
					tbLblStatus.get(index).setValue("<font color=#00AA00>"+element[0]);
					tbLblStatus.get(index).setContentMode(Label.CONTENT_XHTML);
				}

				tbLblInTime.get(index).setValue(element[1]);
				tbLblOutTime.get(index).setValue(element[2]+(element[3]!=null?"("+element[3]+")":""));
			}
		}
		catch (Exception exp)
		{
			showNotification("addRowData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void DeleteButtonEvent(final String dDate,final String EmpID,final int ind)
	{
		MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Delete All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		msgbox.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType==ButtonType.YES)
				{
					deleteData(dDate,EmpID);
					if(!tbLblStatus.get(ind).getValue().toString().trim().contains("Holiday"))
					{
						tbLblStatus.get(ind).setValue("");
						tbLblStatus.get(ind).setValue("<font color=#f44336>Absent");
						tbLblStatus.get(ind).setContentMode(Label.CONTENT_XHTML);
					}
					tbLblInTime.get(ind).setValue("");
					tbLblOutTime.get(ind).setValue("");
				}
			}
		});
	}

	private void deleteData(String date,String EmployeeID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String insertDeleteData="insert into tbDeleteEmployeeAttendance(dDate,vEmployeeID,employeeCode,vProximityID," +
					"vEmployeeName,vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationID,vDesignationName," +
					"iDesignationSerial,vShiftID,vShiftName,dInTimeOne,dOutTimeOne,dInTimeTwo,dOutTimeTwo,dInTimeThree," +
					"dOutTimeThree,vEditFlag,vAttendanceFlag,bOtStatus,ishiftStatus,vUserName,vUserIP,dEntryTime)  select " +
					"dDate,vEmployeeID,employeeCode,vProximityID,vEmployeeName,vDepartmentID,vDepartmentName,vSectionId," +
					"vSectionName,vDesignationID,vDesignationName,iDesignationSerial,vShiftID,vShiftName,dInTimeOne," +
					"dOutTimeOne,dInTimeTwo,dOutTimeTwo,dInTimeThree,dOutTimeThree,vEditFlag,vAttendanceFlag,bOtStatus," +
					"ishiftStatus,'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getDate() from " +
					"tbEmployeeMainAttendance where vEmployeeId='"+EmployeeID+"' and dDate='"+date+"'";

			session.createSQLQuery(insertDeleteData).executeUpdate();

			String query = "delete from tbEmployeeMainAttendance where vEmployeeID='"+EmployeeID+"' and dDate='"+date+"'";
			session.createSQLQuery(query).executeUpdate();
			showNotification("Information Deleted Successfully.", Notification.TYPE_HUMANIZED_MESSAGE);
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("deleteData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tbButtonIni(boolean b)
	{
		for(int i=0;i<tbLblEmployeeID.size();i++)
		{
			tbBtnEdit.get(i).setEnabled(!b);
			tbBtnDelete.get(i).setEnabled(!b);
		}
	}

	private void componentIni(boolean b) 
	{
		dFromDate.setEnabled(!b);
		dToDate.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		cmbEmployeeId.setEnabled(!b);
		chkEmployeeAll.setEnabled(!b);
		cmbStatusType.setEnabled(!b);
		chkStatusTypeAll.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void focusEnter()
	{
		allComp.add(dFromDate);
		allComp.add(dToDate);
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeId);
		allComp.add(cmbStatusType);
		new FocusMoveByEnter(this,allComp);
	}

	private void tableclear()
	{
		for(int i=0; i<tbDate.size(); i++)
		{
			tbLblAutoEmpID.get(i).setValue("");
			tbLblEmployeeID.get(i).setValue("");
			tbLblProximityID.get(i).setValue("");
			tbLblEmployeeName.get(i).setValue("");
			tbLblDesignationID.get(i).setValue("");
			tbLblDesignation.get(i).setValue("");
			tblblDate.get(i).setValue("");

			tbDate.get(i).setReadOnly(false);
			tbDate.get(i).setValue(null);
			tbDate.get(i).setReadOnly(true);

			tbLblDayName.get(i).setValue("");
			tbLblStatus.get(i).setValue("");
			tbLblInTime.get(i).setValue("");
			tbLblOutTime.get(i).setValue("");
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		mainLayout.setWidth("1015px");
		mainLayout.setHeight("455px");

		lblFromDate = new Label("From Date : ");
		lblFromDate.setWidth("-1px");
		lblFromDate.setHeight("-1px");
		mainLayout.addComponent(lblFromDate, "top:10.0px; left:20.0px;");

		dFromDate = new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setWidth("110px");
		dFromDate.setValue(new Date());
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dFromDate, "top:08.0px; left:100.0px;");

		lblToDate = new Label("To Date : ");
		lblToDate.setWidth("-1px");
		lblToDate.setHeight("-1px");
		mainLayout.addComponent(lblToDate, "top:35.0px; left:20.0px;");

		dToDate = new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setWidth("110px");
		dToDate.setValue(new Date());
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dToDate, "top:33.0px; left:100.0px;");

		lblDepartment = new Label("Department Name : ");
		lblDepartment.setWidth("-1px");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment, "top:10.0px;left:280.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("210px");
		cmbDepartment.setHeight("24px");
		mainLayout.addComponent(cmbDepartment, "top:08.0px; left:400.0px;");

		lblSection = new Label("Section Name : ");
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:35.0px;left:280.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("210px");
		cmbSection.setHeight("24px");
		mainLayout.addComponent(cmbSection, "top:33.0px; left:400.0px;");
		
		cmbEmployeeId = new ComboBox();
		cmbEmployeeId.setImmediate(true);
		cmbEmployeeId.setWidth("210px");
		cmbEmployeeId.setHeight("24px");
		cmbEmployeeId.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(new Label("Employee: "), "top:10.0px;left:650.0px;");
		mainLayout.addComponent(cmbEmployeeId, "top:08.0px; left:720.0px;");
		mainLayout.addComponent(chkEmployeeAll, "top:10.0px; left:935.0px;");
		chkEmployeeAll.setImmediate(true);
		
		cmbStatusType = new ComboBox();
		cmbStatusType.setImmediate(true);
		cmbStatusType.setWidth("210px");
		cmbStatusType.setHeight("24px");		
		mainLayout.addComponent(new Label("Status: "), "top:35.0px;left:650.0px;");
		mainLayout.addComponent(cmbStatusType, "top:33.0px; left:720.0px;");
		mainLayout.addComponent(chkStatusTypeAll, "top:35.0px; left:935.0px;");
		chkStatusTypeAll.setImmediate(true);
		cmbStatusType.addItem("Absent");
		cmbStatusType.addItem("Holiday");
		cmbStatusType.addItem("Present");
		cmbStatusType.addItem("Special Absent");

		mainLayout.addComponent(table, "top:70.0px; left:20.0px;");
		return mainLayout;
	}
}
