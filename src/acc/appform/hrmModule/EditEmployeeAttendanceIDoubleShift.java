package acc.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TimeField;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class EditEmployeeAttendanceIDoubleShift extends Window {
	private AbsoluteLayout mainLayout;
	private Label lblDate = new Label("Date");
	private ComboBox cmbDate = new ComboBox();

	private Label lblDeptName = new Label("");
	private ComboBox cmbDeptName = new ComboBox();
	private ComboBox cmbSectionName = new ComboBox();
	private CheckBox chkSectionAll = new CheckBox();

	private Table table = new Table();
	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmpID = new ArrayList<Label>();
	private ArrayList<Label> lblempID = new ArrayList<Label>();
	private ArrayList<Label> lbProxID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lbDesignationID = new ArrayList<Label>();
	private ArrayList<Label> lbDesignation = new ArrayList<Label>();
	private ArrayList<PopupDateField> lbAttendDate = new ArrayList<PopupDateField>();

	private ArrayList<TimeField> InTime1 = new ArrayList<TimeField>();
	private ArrayList<TimeField> InTime2 = new ArrayList<TimeField>();
	private ArrayList<TimeField> InTime3 = new ArrayList<TimeField>();
	private ArrayList<TimeField> InTime4 = new ArrayList<TimeField>();
	private ArrayList<TimeField> InTime5 = new ArrayList<TimeField>();
	private ArrayList<TimeField> InTime6 = new ArrayList<TimeField>();

	private ArrayList<TimeField> OutTime1 = new ArrayList<TimeField>();
	private ArrayList<TimeField> OutTime2 = new ArrayList<TimeField>();
	private ArrayList<TimeField> OutTime3 = new ArrayList<TimeField>();
	private ArrayList<TimeField> OutTime4 = new ArrayList<TimeField>();
	private ArrayList<TimeField> OutTime5 = new ArrayList<TimeField>();
	private ArrayList<TimeField> OutTime6 = new ArrayList<TimeField>();

	private ArrayList<NativeButton> Close = new ArrayList<NativeButton>();
	private ArrayList<TextField> txtPermitBy = new ArrayList<TextField>();
	private ArrayList<TextField> txtReason = new ArrayList<TextField>();
	private ArrayList<Label> lbDeptId = new ArrayList<Label>();
	private ArrayList<Label> lbDeptName = new ArrayList<Label>();
	private ArrayList<Label> lbSecId = new ArrayList<Label>();
	private ArrayList<Label> lbSecName = new ArrayList<Label>();

	private CommonButton cButton = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");
	ArrayList<Component> allComp = new ArrayList<Component>();
	private SimpleDateFormat DBdateformat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateformat=new SimpleDateFormat("dd-MM-yyyy");

	SessionBean sessionbean;

	public EditEmployeeAttendanceIDoubleShift(SessionBean sessionbean)
	{
		this.sessionbean=sessionbean;
		this.setCaption("EDIT EMPLOYEE ATTENDANCE (DOUBLE SHIFT) :: "+sessionbean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		componentIni(true);
		btnIni(true);
		tableInitialize();
		setEventAction();
		cmbDateDataAdd();
	}

	private void componentIni(boolean b)
	{
		cmbDate.setEnabled(!b);
		cmbDeptName.setEnabled(!b);
		cmbSectionName.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnIni(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnRefresh.setEnabled(!b);
	}

	private void setEventAction()
	{
		cmbDeptName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSectionName.removeAllItems();
				cmbSectionName.setEnabled(true);
				chkSectionAll.setValue(false);
				if(cmbDeptName.getValue()!=null)
				{
					cmbSectionDataAdd();
				}
			}
		});

		cmbSectionName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				if(cmbSectionName.getValue()!=null)
				{
					tableDataAdd(cmbSectionName.getValue().toString());
				}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				if(chkSectionAll.booleanValue())
				{
					cmbSectionName.setValue(null);
					cmbSectionName.setEnabled(false);
					tableDataAdd("%");
				}
				else
				{
					cmbSectionName.setEnabled(true);
				}
			}
		});

		cmbDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDeptName.removeAllItems();
				if(cmbDate.getValue()!=null)
				{
					cmbDepartmentNameAdd();
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				btnNewEvent();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				btnRefreshEvent();
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
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

	private void btnNewEvent()
	{
		componentIni(false);
		btnIni(false);
		cmbDeptName.setValue(null);
		txtClear();
	}

	private void btnRefreshEvent()
	{

		txtClear();
		componentIni(true);
		btnIni(true);
		cmbDeptName.setValue(null);
	}

	private boolean chkTableData()
	{
		boolean ret=false;
		for(int index=0;index<lblAutoEmpID.size();index++)
		{
			if(!lblAutoEmpID.get(index).getValue().toString().trim().isEmpty())
			{ 
				if(!InTime1.get(index).getValue().toString().trim().isEmpty())
				{
					if(!InTime2.get(index).getValue().toString().trim().isEmpty())
					{
						if(!InTime3.get(index).getValue().toString().trim().isEmpty())
						{
							if(!InTime4.get(index).getValue().toString().trim().isEmpty())
							{
								if(!InTime5.get(index).getValue().toString().trim().isEmpty())
								{
									if(!InTime6.get(index).getValue().toString().trim().isEmpty())
									{
										if(!OutTime1.get(index).getValue().toString().trim().isEmpty())
										{
											if(!OutTime2.get(index).getValue().toString().trim().isEmpty())
											{
												if(!OutTime3.get(index).getValue().toString().trim().isEmpty())
												{
													if(!OutTime4.get(index).getValue().toString().trim().isEmpty())
													{
														if(!OutTime5.get(index).getValue().toString().trim().isEmpty())
														{
															if(!OutTime6.get(index).getValue().toString().trim().isEmpty())
															{
																if(!txtPermitBy.get(index).getValue().toString().trim().isEmpty())
																{
																	if(!txtReason.get(index).getValue().toString().trim().isEmpty())
																	{
																		ret=true;
																		break;
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return ret;
	}

	private void formValidation()
	{
		if(chkTableData())
		{
			btnSaveEvent();
		}
		else
		{
			showNotification("Warning!","Provide All Data To Table ",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void btnSaveEvent()
	{
		if(sessionbean.isUpdateable())
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						Session session = SessionFactoryUtil.getInstance().openSession();
						Transaction tx = session.beginTransaction();
						try
						{
							insertData(session);
							tx.commit();
						}
						catch (Exception exp)
						{
							tx.rollback();
							showNotification("btnSaveEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
						finally
						{
							session.close();
						}
					}
				}
			});
		}
	}

	private void insertData(Session session)
	{
		String query = "";
		String query1 = "";
		String query2 = "";
		String query3 = "";
		for(int i=0; i<lblAutoEmpID.size(); i++)
		{
			if(!txtPermitBy.get(i).getValue().toString().trim().isEmpty() && !txtReason.get(i).getValue().toString().trim().isEmpty())
			{
				if(!InTime1.get(i).getValue().toString().isEmpty() && !InTime2.get(i).getValue().toString().isEmpty() && 
						!InTime4.get(i).getValue().toString().isEmpty() && !InTime5.get(i).getValue().toString().isEmpty() && 
						!OutTime1.get(i).getValue().toString().isEmpty() && !OutTime2.get(i).getValue().toString().isEmpty() && 
						!OutTime4.get(i).getValue().toString().isEmpty() && !OutTime5.get(i).getValue().toString().isEmpty())
				{

					int in_timeMax=Integer.parseInt(InTime4.get(i).getValue().toString().trim());
					int out_timeMax=Integer.parseInt(OutTime4.get(i).getValue().toString().trim());

					String inTime=InTime1.get(i).getValue().toString()+":"+InTime2.get(i).getValue().toString()+":00";
					String outTime=OutTime1.get(i).getValue().toString()+":"+OutTime2.get(i).getValue().toString()+":00";

					String intimeMax=InTime4.get(i).getValue().toString()+":"+InTime5.get(i).getValue().toString()+":00";
					String outtimeMax=OutTime4.get(i).getValue().toString()+":"+OutTime5.get(i).getValue().toString()+":00";

					List <?> lst=session.createSQLQuery("select * from tbEmployeeMainAttendance where dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"' " +
							"and vEmployeeId='"+lblAutoEmpID.get(i).getValue().toString()+"'").list();
					session.clear();

					String AttendanceFlag="SA";

					if(!lst.isEmpty())
					{
						query = "insert into tbUDEmployeeAttendanceDoubleShift select dDate,vEmployeeID,employeeCode,vProximityID,vEmployeeName," +
								"vDesignationName,vSectionId,vSectionName,dDate,dInTimeOne,dOutTimeOne,dInTimeTwo,dOutTimeTwo,'','',0,'New','Present'," +
								"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',GETDATE(),vDesignationID,vDepartmentID,vDepartmentName from " +
								"tbEmployeeMainAttendance where vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' and " +
								"dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'";
						session.createSQLQuery(query).executeUpdate();						
						session.clear();

						query1="update tbEmployeeMainAttendance set dInTimeOne='"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+intimeMax+"'," +
								"dOutTimeOne='"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+outtimeMax+"'," +
								"dInTimeTwo='"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+inTime+"'," +
								"dOutTimeTwo='"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+outTime+"',vEditFlag='Edited'," +
								"vAttendanceFlag='"+AttendanceFlag+"' where vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' and " +
								"dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'";
						if(in_timeMax>out_timeMax)
						{
							query1="update tbEmployeeMainAttendance set dInTimeOne='"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+intimeMax+"'," +
									"dOutTimeOne=DateAdd(dd,1,'"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+outtimeMax+"')," +
									"dInTimeTwo='"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+inTime+"'," +
									"dOutTimeTwo='"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+outTime+"'," +
									"vEditFlag='Edited',vAttendanceFlag='"+AttendanceFlag+"' where vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' " +
									"and dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'";
						}

						query2 = "insert into tbUDEmployeeAttendanceDoubleShift select dDate,vEmployeeID,employeeCode,vProximityID,vEmployeeName," +
								"vDesignationName,vSectionId,vSectionName,dDate,dInTimeOne,dOutTimeOne,dInTimeTwo,dOutTimeTwo,'','',0,'Update','Present'," +
								"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',GETDATE(),vDesignationID,vDepartmentID,vDepartmentName from " +
								"tbEmployeeMainAttendance where vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' and " +
								"dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'";
					}
					else
					{
						query1="insert into tbEmployeeMainAttendance (dDate,vEmployeeID,employeeCode,vProximityID,vEmployeeName,vDepartmentID," +
								"vDepartmentName,vSectionId,vSectionName,vDesignationID,vDesignationName,iDesignationSerial,vShiftID,vShiftName," +
								"dInTimeOne,dOutTimeOne,dInTimeTwo,dOutTimeTwo,vEditFlag,vAttendanceFlag,bOtStatus,ishiftStatus) values " +
								"('"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'," +
								"'"+lblAutoEmpID.get(i).getValue().toString()+"'," +
								"'"+lblempID.get(i).getValue().toString()+"'," +
								"'"+lbProxID.get(i).getValue().toString()+"'," +
								"'"+lbEmployeeName.get(i).getValue().toString()+"'," +
								"'"+lbDeptId.get(i).getValue().toString()+"'," +
								"'"+lbDeptName.get(i).getValue().toString()+"'," +
								"'"+lbSecId.get(i).getValue().toString()+"'," +
								"'"+lbSecName.get(i).getValue().toString()+"'," +
								"'"+lbDesignationID.get(i).getValue().toString()+"'," +
								"'"+lbDesignation.get(i).getValue().toString()+"'," +
								"(select designationSerial from tbDesignationInfo where designationID='"+lbDesignationID.get(i).getValue().toString()+"')," +
								"'0','General','"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+intimeMax+"'," +
								"'"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+outtimeMax+"'," +
								"'"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+inTime+"'," +
								"'"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+outTime+"'," +
								"'Edited','"+AttendanceFlag+"',(select OtStatus from tbEmployeeInfo where " +
								"vEmployeeId='"+lblAutoEmpID.get(i).getValue().toString()+"'),'')";
						if(in_timeMax>out_timeMax)
						{
							query1="insert into tbEmployeeMainAttendance (dDate,vEmployeeID,employeeCode,vProximityID,vEmployeeName,vDepartmentID," +
									"vDepartmentName,vSectionId,vSectionName,vDesignationID,vDesignationName,iDesignationSerial,vShiftID,vShiftName," +
									"dInTimeOne,dOutTimeOne,dInTimeTwo,dOutTimeTwo,vEditFlag,vAttendanceFlag,bOtStatus,ishiftStatus) values " +
									"('"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'," +
									"'"+lblAutoEmpID.get(i).getValue().toString()+"'," +
									"'"+lblempID.get(i).getValue().toString()+"'," +
									"'"+lbProxID.get(i).getValue().toString()+"'," +
									"'"+lbEmployeeName.get(i).getValue().toString()+"'," +
									"'"+lbDeptId.get(i).getValue().toString()+"'," +
									"'"+lbDeptName.get(i).getValue().toString()+"'," +
									"'"+lbSecId.get(i).getValue().toString()+"'," +
									"'"+lbSecName.get(i).getValue().toString()+"'," +
									"'"+lbDesignationID.get(i).getValue().toString()+"'," +
									"'"+lbDesignation.get(i).getValue().toString()+"'," +
									"(select designationSerial from tbDesignationInfo where designationID='"+lbDesignationID.get(i).getValue().toString()+"')," +
									"'0','General','"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+intimeMax+"'," +
									"DateAdd(dd,1,'"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+outtimeMax+"')," +
									"'"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+inTime+"'," +
									"'"+DBdateformat.format(lbAttendDate.get(i).getValue())+" "+outTime+"'," +
									"'Edited','"+AttendanceFlag+"',(select OtStatus from tbEmployeeInfo where " +
									"vEmployeeId='"+lblAutoEmpID.get(i).getValue().toString()+"'),'')";

							query2 = "insert into tbUDEmployeeAttendanceDoubleShift select dDate,vEmployeeID,employeeCode,vProximityID,vEmployeeName," +
									"vDesignationName,vSectionId,vSectionName,dDate,dInTimeOne,dOutTimeOne,dInTimeTwo,dOutTimeTwo,'','',0,'New','Absent'," +
									"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',GETDATE(),vDesignationID,vDepartmentID,vDepartmentName from " +
									"tbEmployeeMainAttendance where vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' and " +
									"dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'";
						}
					}
					session.createSQLQuery(query1).executeUpdate();
					session.createSQLQuery(query2).executeUpdate();	

					List <?> chkLst=session.createSQLQuery("select dDate from tbHoliday where " +
							"dDate = '"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'").list();
					if(!chkLst.isEmpty())
					{
						int chkHoliHour=Integer.parseInt(session.createSQLQuery("select convert(varchar,DATEDIFF(HH,dInTimeOne,dOutTimeOne)) as hourdiff " +
								"from tbEmployeeMainAttendance where " +
								"vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' " +
								"and dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'").list().iterator().next().toString());
						if(chkHoliHour>=5)
						{
							query3="update tbEmployeeMainAttendance set vAttendanceFlag='HP' where " +
									"vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' " +
									"and dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'";
							System.out.println("query3 = "+query3);
							session.createSQLQuery(query3).executeUpdate();
						}
					}
					else
					{
						int chkHour=Integer.parseInt(session.createSQLQuery("select convert(varchar,DATEDIFF(HH,dInTimeOne,dOutTimeOne)) as hourdiff " +
								"from tbEmployeeMainAttendance where " +
								"vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' " +
								"and dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'").list().iterator().next().toString());
						if(chkHour>=7)
						{
							query3="update tbEmployeeMainAttendance set vAttendanceFlag='PR' where " +
									"vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"' " +
									"and dDate='"+DBdateformat.format(lbAttendDate.get(i).getValue())+"'";
							System.out.println("query3 = "+query3);
							session.createSQLQuery(query3).executeUpdate();
						}
					}
				}
			}
		}

		txtClear();
		cmbDate.setValue(null);
		componentIni(true);
		btnIni(true);
	}

	private void tableclear()
	{
		for(int i =0; i<lblAutoEmpID.size(); i++)
		{
			lblAutoEmpID.get(i).setValue("");
			lblempID.get(i).setValue("");
			lbProxID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			lbDesignationID.get(i).setValue("");
			lbDesignation.get(i).setValue("");
			txtPermitBy.get(i).setValue("");
			txtReason.get(i).setValue("");
			txtPermitBy.get(i).setValue("");
			InTime1.get(i).setValue("");
			InTime2.get(i).setValue("");
			InTime3.get(i).setValue("");
			OutTime1.get(i).setValue("");
			OutTime2.get(i).setValue("");
			OutTime3.get(i).setValue("");
			InTime4.get(i).setValue("");
			InTime5.get(i).setValue("");
			InTime6.get(i).setValue("");
			OutTime4.get(i).setValue("");
			OutTime5.get(i).setValue("");
			OutTime6.get(i).setValue("");
			lbDeptId.get(i).setValue("");
			lbDeptName.get(i).setValue("");
			lbSecId.get(i).setValue("");
			lbSecName.get(i).setValue("");
			lbAttendDate.get(i).setReadOnly(false);
			lbAttendDate.get(i).setValue(null);
			lbAttendDate.get(i).setReadOnly(true);
		}
	}

	private void cmbSectionDataAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct IDS.vSectionID,Sein.SectionName from tbSectionInfo Sein inner join " +
					"tbIDoubleShift IDS on Sein.vSectionID=IDS.vSectionID where IDS.vDepartmentID='"+cmbDeptName.getValue()+"' " +
					"and IDS.dDate='"+cmbDate.getValue()+"' order by Sein.SectionName";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbSectionName.addItem(element[0]);
					cmbSectionName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbSectionDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void cmbDateDataAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct dDate as nDate,dDate from tbIDoubleShift order by dDate desc";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbDate.addItem(element[0]);
					cmbDate.setItemCaption(element[0], dateformat.format(element[1]));
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbDateDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void cmbDepartmentNameAdd()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct IDS.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbIDoubleShift IDS on dept.vDepartmentId=IDS.vDepartmentId where IDS.dDate='"+cmbDate.getValue()+"' " +
					"order by dept.vDepartmentName";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					cmbDeptName.addItem(element[0]);
					cmbDeptName.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("cmbDepartmentNameAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void txtClear()
	{
		for(int index=0;index<lblAutoEmpID.size();index++)
		{
			lblAutoEmpID.get(index).setValue("");
			lblempID.get(index).setValue("");
			lbProxID.get(index).setValue("");
			lbEmployeeName.get(index).setValue("");
			lbDesignationID.get(index).setValue("");
			lbDesignation.get(index).setValue("");
			lbAttendDate.get(index).setReadOnly(false);
			lbAttendDate.get(index).setValue(null);
			lbAttendDate.get(index).setReadOnly(true);

			InTime1.get(index).setValue("");
			InTime2.get(index).setValue("");
			InTime3.get(index).setValue("");
			OutTime1.get(index).setValue("");
			OutTime2.get(index).setValue("");
			OutTime3.get(index).setValue("");

			InTime4.get(index).setValue("");
			InTime5.get(index).setValue("");
			InTime6.get(index).setValue("");
			OutTime4.get(index).setValue("");
			OutTime5.get(index).setValue("");
			OutTime6.get(index).setValue("");

			lbDeptId.get(index).setValue("");
			lbDeptName.get(index).setValue("");
			lbSecId.get(index).setValue("");
			lbSecName.get(index).setValue("");
		}
	}

	private void tableDataAdd(String strSectionID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Query="select vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,vDesignationID,vDesignationName," +
					"dAttendanceDate,inHour1,inMinute1,inSecond1,outHour1,outMinute1,outSecond1,inHour,inMinute," +
					"inSecond,outHour,outMinute,outSecond,vDepartmentID,vDepartmentName,vSectionID,vSectionName from " +
					"funEditDailyEmployeeAttendanceDoubleShift" +
					"('"+DBdateformat.format(cmbDate.getValue())+"','"+cmbDeptName.getValue()+"','"+strSectionID+"') " +
					"where dAttendanceDate<convert(date,getdate()) order by vEmployeeCode";
			List <?> lst=session.createSQLQuery(Query).list();

			if(!lst.isEmpty())
			{
				int i=0;
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					lblAutoEmpID.get(i).setValue(element[0]);
					lblempID.get(i).setValue(element[1]);
					lbProxID.get(i).setValue(element[2]);
					lbEmployeeName.get(i).setValue(element[3]);
					lbDesignationID.get(i).setValue(element[4]);
					lbDesignation.get(i).setValue(element[5]);
					lbAttendDate.get(i).setReadOnly(false);
					lbAttendDate.get(i).setValue(element[6]);
					lbAttendDate.get(i).setReadOnly(true);

					InTime1.get(i).setValue(element[7]);
					InTime2.get(i).setValue(element[8]);
					InTime3.get(i).setValue(element[9]);
					OutTime1.get(i).setValue(element[10]);
					OutTime2.get(i).setValue(element[11]);
					OutTime3.get(i).setValue(element[12]);

					InTime4.get(i).setValue(element[13]);
					InTime5.get(i).setValue(element[14]);
					InTime6.get(i).setValue(element[15]);
					OutTime4.get(i).setValue(element[16]);
					OutTime5.get(i).setValue(element[17]);
					OutTime6.get(i).setValue(element[18]);

					lbDeptId.get(i).setValue(element[19]);
					lbDeptName.get(i).setValue(element[20]);
					lbSecId.get(i).setValue(element[21]);
					lbSecName.get(i).setValue(element[22]);

					i++;
					if(i==lblAutoEmpID.size()-1)
						tableRowAdd(i+1);
				}
			}
			else
			{
				showNotification("Warning!!!","No Data Found",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("tableDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	@SuppressWarnings("static-access")
	private void tableInitialize() 
	{
		table.setColumnCollapsingAllowed(true);
		table.setWidth("98%");
		table.setHeight("335px");
		table.setPageLength(0);

		table.addContainerProperty("Del", NativeButton.class , new NativeButton());
		table.setColumnWidth("Del",30);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 100);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 100);

		table.addContainerProperty("Proximity ID", Label.class , new Label());
		table.setColumnWidth("Proximity ID",80);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",170);

		table.addContainerProperty("Designation ID", Label.class , new Label());
		table.setColumnWidth("Designation ID",120);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",120);

		table.addContainerProperty("Attend Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Attend Date", 100);

		table.addContainerProperty("HH(IN1)", TimeField.class , new TimeField());
		table.setColumnWidth("HH(IN1)",28);

		table.addContainerProperty("Min(IN1)", TimeField.class , new TimeField());
		table.setColumnWidth("Min(IN1)",28);

		table.addContainerProperty("Sec(IN1)", TimeField.class , new TimeField());
		table.setColumnWidth("Sec(IN1)",28);

		table.addContainerProperty("HH(OUT1)", TimeField.class , new TimeField());
		table.setColumnWidth("HH(OUT1)",28);

		table.addContainerProperty("Min(OUT1)", TimeField.class , new TimeField());
		table.setColumnWidth("Min(OUT1)",28);

		table.addContainerProperty("Sec(OUT1)", TimeField.class , new TimeField());
		table.setColumnWidth("Sec(OUT1)",28);

		table.addContainerProperty("HH(IN2)", TimeField.class , new TimeField());
		table.setColumnWidth("HH(IN2)",28);

		table.addContainerProperty("Min(IN2)", TimeField.class , new TimeField());
		table.setColumnWidth("Min(IN2)",28);

		table.addContainerProperty("Sec(IN2)", TimeField.class , new TimeField());
		table.setColumnWidth("Sec(IN2)",28);

		table.addContainerProperty("HH(OUT2)", TimeField.class , new TimeField());
		table.setColumnWidth("HH(OUT2)",28);

		table.addContainerProperty("Min(OUT2)", TimeField.class , new TimeField());
		table.setColumnWidth("Min(OUT2)",28);

		table.addContainerProperty("Sec(OUT2)", TimeField.class , new TimeField());
		table.setColumnWidth("Sec(OUT 2)",28);

		table.addContainerProperty("Permitted By", TextField.class , new TextField());
		table.setColumnWidth("Permitted By",120);

		table.addContainerProperty("Reason", TextField.class , new TextField());
		table.setColumnWidth("Reason",120);	

		table.addContainerProperty("DId", Label.class , new Label());
		table.setColumnWidth("DId",50);

		table.addContainerProperty("DName", Label.class , new Label());
		table.setColumnWidth("DName",120);

		table.addContainerProperty("sId", Label.class , new Label());
		table.setColumnWidth("sId",50);

		table.addContainerProperty("sName", Label.class , new Label());
		table.setColumnWidth("sName",120);

		table.setColumnAlignments(new String[]{table.ALIGN_CENTER,table.ALIGN_RIGHT,table.ALIGN_LEFT,table.ALIGN_LEFT,
				table.ALIGN_LEFT,table.ALIGN_LEFT,table.ALIGN_LEFT,table.ALIGN_LEFT,table.ALIGN_CENTER,table.ALIGN_CENTER,
				table.ALIGN_CENTER,table.ALIGN_CENTER,table.ALIGN_CENTER,table.ALIGN_CENTER,table.ALIGN_CENTER,
				table.ALIGN_CENTER,table.ALIGN_CENTER,table.ALIGN_CENTER,table.ALIGN_CENTER,table.ALIGN_CENTER,
				table.ALIGN_CENTER,table.ALIGN_CENTER,table.ALIGN_CENTER,table.ALIGN_RIGHT,table.ALIGN_LEFT,
				table.ALIGN_RIGHT,table.ALIGN_LEFT});

		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Designation", true);
		table.setColumnCollapsed("Sec(IN1)", true);
		table.setColumnCollapsed("Sec(IN2)", true);
		table.setColumnCollapsed("Sec(OUT1)", true);
		table.setColumnCollapsed("Sec(OUT2)", true);
		table.setColumnCollapsed("DId", true);
		table.setColumnCollapsed("DName", true);
		table.setColumnCollapsed("sId", true);
		table.setColumnCollapsed("sName", true);

		rowAddinTable();
	}

	private void rowAddinTable()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		Close.add(ar, new NativeButton(""));
		Close.get(ar).setWidth("100%");
		Close.get(ar).setImmediate(true);
		Close.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		Close.get(ar).addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				lblAutoEmpID.get(ar).setValue("");
				lblempID.get(ar).setValue("");
				lbProxID.get(ar).setValue("");
				lbEmployeeName.get(ar).setValue("");
				lbDesignationID.get(ar).setValue("");
				lbDesignation.get(ar).setValue("");
				lbAttendDate.get(ar).setReadOnly(false);
				lbAttendDate.get(ar).setValue(null);
				lbAttendDate.get(ar).setReadOnly(true);
				txtPermitBy.get(ar).setValue("");
				txtReason.get(ar).setValue("");
				txtPermitBy.get(ar).setValue("");
				txtReason.get(ar).setValue("");
				InTime1.get(ar).setValue("");
				InTime2.get(ar).setValue("");
				InTime3.get(ar).setValue("");
				OutTime1.get(ar).setValue("");
				OutTime2.get(ar).setValue("");
				OutTime3.get(ar).setValue("");
				InTime4.get(ar).setValue("");
				InTime5.get(ar).setValue("");
				InTime6.get(ar).setValue("");
				OutTime4.get(ar).setValue("");
				OutTime5.get(ar).setValue("");
				OutTime6.get(ar).setValue("");
				lbDeptId.get(ar).setValue("");
				lbDeptName.get(ar).setValue("");
				lbSecId.get(ar).setValue("");
				lbSecName.get(ar).setValue("");

				for(int rowcount=ar;rowcount<=lbProxID.size()-1;rowcount++)
				{
					if(rowcount+1<=lbProxID.size()-1)
					{
						if(!lbProxID.get(rowcount+1).getValue().toString().equals(""))
						{
							lblAutoEmpID.get(rowcount).setValue(lblAutoEmpID.get(rowcount+1).getValue().toString());
							lblempID.get(rowcount).setValue(lblempID.get(rowcount+1).getValue().toString());
							lbProxID.get(rowcount).setValue(lbProxID.get(rowcount+1).getValue().toString());
							lbEmployeeName.get(rowcount).setValue(lbEmployeeName.get(rowcount+1).getValue().toString());
							lbAttendDate.get(rowcount).setReadOnly(false);
							lbAttendDate.get(rowcount).setValue(lbAttendDate.get(rowcount+1).getValue());
							lbAttendDate.get(rowcount).setReadOnly(true);
							lbDesignation.get(rowcount).setValue(lbDesignation.get(rowcount+1).getValue().toString());
							txtPermitBy.get(rowcount).setValue(txtPermitBy.get(rowcount+1).getValue().toString());
							txtReason.get(rowcount).setValue(txtReason.get(rowcount+1).getValue().toString());
							InTime1.get(rowcount).setValue(InTime1.get(rowcount+1).getValue().toString());
							InTime2.get(rowcount).setValue(InTime2.get(rowcount+1).getValue().toString());
							InTime3.get(rowcount).setValue(InTime3.get(rowcount+1).getValue().toString());
							OutTime1.get(rowcount).setValue(OutTime1.get(rowcount+1).getValue().toString());
							OutTime2.get(rowcount).setValue(OutTime2.get(rowcount+1).getValue().toString());
							OutTime3.get(rowcount).setValue(OutTime3.get(rowcount+1).getValue().toString());
							InTime4.get(rowcount).setValue(InTime4.get(rowcount+1).getValue().toString());
							InTime5.get(rowcount).setValue(InTime5.get(rowcount+1).getValue().toString());
							InTime6.get(rowcount).setValue(InTime6.get(rowcount+1).getValue().toString());
							OutTime4.get(rowcount).setValue(OutTime4.get(rowcount+1).getValue().toString());
							OutTime5.get(rowcount).setValue(OutTime5.get(rowcount+1).getValue().toString());
							OutTime6.get(rowcount).setValue(OutTime6.get(rowcount+1).getValue().toString());
							lbDeptId.get(rowcount).setValue(lbDeptId.get(rowcount+1).getValue().toString());
							lbDeptName.get(rowcount).setValue(lbDeptName.get(rowcount+1).getValue().toString());
							lbSecId.get(rowcount).setValue(lbSecId.get(rowcount+1).getValue().toString());
							lbSecName.get(rowcount).setValue(lbSecName.get(rowcount+1).getValue().toString());

							lblAutoEmpID.get(rowcount+1).setValue("");
							lblempID.get(rowcount+1).setValue("");
							lbProxID.get(rowcount+1).setValue("");
							lbEmployeeName.get(rowcount+1).setValue("");
							lbDesignation.get(rowcount+1).setValue("");
							lbAttendDate.get(rowcount+1).setReadOnly(false);
							lbAttendDate.get(rowcount+1).setValue(null);
							lbAttendDate.get(rowcount+1).setReadOnly(true);
							txtPermitBy.get(rowcount+1).setValue("");
							txtReason.get(rowcount+1).setValue("");
							InTime1.get(rowcount+1).setValue("");
							InTime2.get(rowcount+1).setValue("");
							InTime3.get(rowcount+1).setValue("");
							OutTime1.get(rowcount+1).setValue("");
							OutTime2.get(rowcount+1).setValue("");
							OutTime3.get(rowcount+1).setValue("");
							InTime4.get(rowcount+1).setValue("");
							InTime5.get(rowcount+1).setValue("");
							InTime6.get(rowcount+1).setValue("");
							OutTime4.get(rowcount+1).setValue("");
							OutTime5.get(rowcount+1).setValue("");
							OutTime6.get(rowcount+1).setValue("");
							lbDeptId.get(rowcount+1).setValue("");
							lbDeptName.get(rowcount+1).setValue("");
							lbSecId.get(rowcount+1).setValue("");
							lbSecName.get(rowcount+1).setValue("");
						}
					}
				}
			}
		});

		lbSl.add(ar, new Label(""));
		lbSl.get(ar).setWidth("100%");
		lbSl.get(ar).setHeight("20px");
		lbSl.get(ar).setValue(ar+1);

		lblAutoEmpID.add(ar, new Label());
		lblAutoEmpID.get(ar).setImmediate(true);
		lblAutoEmpID.get(ar).setWidth("100%");
		lblAutoEmpID.get(ar).setHeight("20px");

		lblempID.add(ar,new Label());
		lblempID.get(ar).setImmediate(true);
		lblempID.get(ar).setWidth("100%");
		lblempID.get(ar).setHeight("20px");

		lbProxID.add(ar, new Label());
		lbProxID.get(ar).setWidth("100%");
		lbProxID.get(ar).setImmediate(true);
		lbProxID.get(ar).setHeight("-1px");

		lbEmployeeName.add(ar, new Label());
		lbEmployeeName.get(ar).setWidth("100%");
		lbEmployeeName.get(ar).setImmediate(true);

		lbDesignationID.add(ar, new Label());
		lbDesignationID.get(ar).setWidth("100%");
		lbDesignationID.get(ar).setImmediate(true);

		lbDesignation.add(ar, new Label());
		lbDesignation.get(ar).setWidth("100%");
		lbDesignation.get(ar).setImmediate(true);

		lbAttendDate.add(ar, new PopupDateField());
		lbAttendDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		lbAttendDate.get(ar).setDateFormat("dd-MM-yyyy");
		lbAttendDate.get(ar).setReadOnly(true);
		lbAttendDate.get(ar).setWidth("100%");
		lbAttendDate.get(ar).setImmediate(true);

		InTime1.add(ar, new TimeField());
		InTime1.get(ar).setWidth("28px");
		InTime1.get(ar).setInputPrompt("hh");
		InTime1.get(ar).setImmediate(true);
		InTime1.get(ar).setStyleName("Intime");
		InTime1.get(ar).setMaxLength(2);
		InTime1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime1.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime1.get(ar).getValue().toString())>23)
					{
						InTime1.get(ar).setValue("");
					}
					else
					{
						InTime1.get(ar).setValue(InTime1.get(ar).getValue().toString());
						InTime2.get(ar).focus();
					}
				}
			}
		});

		InTime2.add(ar, new TimeField());
		InTime2.get(ar).setWidth("28px");
		InTime2.get(ar).setInputPrompt("mm");
		InTime2.get(ar).setImmediate(true);
		InTime2.get(ar).setStyleName("Intime");
		InTime2.get(ar).setMaxLength(2);
		InTime2.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime2.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime2.get(ar).getValue().toString())>=60)
					{
						InTime2.get(ar).setValue("");
					}
					else
					{
						InTime2.get(ar).setValue(InTime2.get(ar).getValue().toString());
						InTime4.get(ar).focus();
					}
				}
			}
		});

		InTime3.add(ar, new TimeField());
		InTime3.get(ar).setWidth("28px");
		InTime3.get(ar).setInputPrompt("ss");
		InTime3.get(ar).setImmediate(true);
		InTime3.get(ar).setStyleName("Intime");
		InTime3.get(ar).setMaxLength(2);
		InTime3.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime3.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime3.get(ar).getValue().toString())>=60)
					{
						InTime3.get(ar).setValue("");
					}
					else
					{
						InTime3.get(ar).setValue(InTime3.get(ar).getValue().toString());
					}
				}
			}
		});

		InTime3.get(ar).setEnabled(false);

		InTime4.add(ar, new TimeField());
		InTime4.get(ar).setWidth("28px");
		InTime4.get(ar).setInputPrompt("hh");
		InTime4.get(ar).setImmediate(true);
		InTime4.get(ar).setStyleName("Intime");
		InTime4.get(ar).setMaxLength(2);
		InTime4.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime4.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime4.get(ar).getValue().toString())>23)
					{
						InTime4.get(ar).setValue("");
					}
					else
					{
						InTime4.get(ar).setValue(InTime4.get(ar).getValue().toString());
						InTime5.get(ar).focus();
					}
				}
			}
		});

		InTime5.add(ar, new TimeField());
		InTime5.get(ar).setWidth("28px");
		InTime5.get(ar).setInputPrompt("mm");
		InTime5.get(ar).setImmediate(true);
		InTime5.get(ar).setStyleName("Intime");
		InTime5.get(ar).setMaxLength(2);
		InTime5.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime5.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime5.get(ar).getValue().toString())>=60)
					{
						InTime5.get(ar).setValue("");
					}
					else
					{
						InTime5.get(ar).setValue(InTime5.get(ar).getValue().toString());
						OutTime1.get(ar).focus();
					}
				}
			}
		});


		InTime6.add(ar, new TimeField());
		InTime6.get(ar).setWidth("28px");
		InTime6.get(ar).setInputPrompt("ss");
		InTime6.get(ar).setImmediate(true);
		InTime6.get(ar).setStyleName("Intime");
		InTime6.get(ar).setMaxLength(2);
		InTime6.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!InTime6.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(InTime6.get(ar).getValue().toString())>=60)
					{
						InTime6.get(ar).setValue("");
					}
					else{
						InTime6.get(ar).setValue(InTime6.get(ar).getValue().toString());
					}
				}
			}
		});
		InTime6.get(ar).setEnabled(false);


		OutTime1.add(ar, new TimeField());
		OutTime1.get(ar).setWidth("28px");
		OutTime1.get(ar).setInputPrompt("hh");
		OutTime1.get(ar).setImmediate(true);
		OutTime1.get(ar).addStyleName("Outtime");
		OutTime1.get(ar).setMaxLength(2);
		OutTime1.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime1.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime1.get(ar).getValue().toString())>=24)
					{
						OutTime1.get(ar).setValue("");
					}
					else
					{
						OutTime1.get(ar).setValue(OutTime1.get(ar).getValue().toString());
						OutTime2.get(ar).focus();
					}
				}
			}
		});

		OutTime2.add(ar, new TimeField());
		OutTime2.get(ar).setWidth("28px");
		OutTime2.get(ar).setInputPrompt("mm");
		OutTime2.get(ar).setImmediate(true);
		OutTime2.get(ar).setStyleName("Outtime");
		OutTime2.get(ar).setMaxLength(2);
		OutTime2.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime2.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime2.get(ar).getValue().toString())>=60)
					{
						OutTime2.get(ar).setValue("");
					}
					else
					{
						OutTime2.get(ar).setValue(OutTime2.get(ar).getValue().toString());
						OutTime4.get(ar).focus();
					}
				}
			}
		});

		OutTime3.add(ar, new TimeField());
		OutTime3.get(ar).setWidth("28px");
		OutTime3.get(ar).setInputPrompt("ss");
		OutTime3.get(ar).setImmediate(true);
		OutTime3.get(ar).setStyleName("Outtime");
		OutTime3.get(ar).setMaxLength(2);
		OutTime3.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime3.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime3.get(ar).getValue().toString())>=60)
					{
						OutTime3.get(ar).setValue("");
					}
					else{
						OutTime3.get(ar).setValue(OutTime3.get(ar).getValue().toString());
					}
				}
			}
		});

		OutTime3.get(ar).setEnabled(false);

		OutTime4.add(ar, new TimeField());
		OutTime4.get(ar).setWidth("28px");
		OutTime4.get(ar).setInputPrompt("hh");
		OutTime4.get(ar).setImmediate(true);
		OutTime4.get(ar).addStyleName("Outtime");
		OutTime4.get(ar).setMaxLength(2);
		OutTime4.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime4.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime4.get(ar).getValue().toString())>=24)
					{
						OutTime4.get(ar).setValue("");
					}
					else
					{
						OutTime4.get(ar).setValue(OutTime4.get(ar).getValue().toString());
						OutTime5.get(ar).focus();
					}
				}
			}
		});

		OutTime5.add(ar, new TimeField());
		OutTime5.get(ar).setWidth("28px");
		OutTime5.get(ar).setInputPrompt("mm");
		OutTime5.get(ar).setImmediate(true);
		OutTime5.get(ar).setStyleName("Outtime");
		OutTime5.get(ar).setMaxLength(2);
		OutTime5.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime5.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime5.get(ar).getValue().toString())>=60)
					{
						OutTime5.get(ar).setValue("");
					}
					else
					{
						OutTime5.get(ar).setValue(OutTime5.get(ar).getValue().toString());
						txtPermitBy.get(ar).focus();
					}
				}
			}
		});	

		OutTime6.add(ar, new TimeField());
		OutTime6.get(ar).setWidth("28px");
		OutTime6.get(ar).setInputPrompt("ss");
		OutTime6.get(ar).setImmediate(true);
		OutTime6.get(ar).setStyleName("Outtime");
		OutTime6.get(ar).setMaxLength(2);
		OutTime6.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!OutTime6.get(ar).getValue().toString().isEmpty())
				{			
					if(Double.parseDouble(OutTime6.get(ar).getValue().toString())>=60)
					{
						OutTime6.get(ar).setValue("");
					}
					else{
						OutTime6.get(ar).setValue(OutTime6.get(ar).getValue().toString());
					}
				}
			}
		});
		OutTime6.get(ar).setEnabled(false);

		txtPermitBy.add(ar, new TextField(""));
		txtPermitBy.get(ar).setWidth("100%");
		txtPermitBy.get(ar).setImmediate(true);
		txtPermitBy.get(ar).setHeight("-1px");
		txtPermitBy.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtPermitBy.get(ar).getValue().toString().trim().isEmpty())
				{
					txtReason.get(ar).focus();
				}
			}
		});

		txtReason.add(ar, new TextField(""));
		txtReason.get(ar).setWidth("100%");
		txtReason.get(ar).setImmediate(true);
		txtReason.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtReason.get(ar).getValue().toString().trim().isEmpty())
				{
					if(!lblAutoEmpID.get(ar+1).getValue().toString().trim().isEmpty())
					{
						InTime1.get(ar+1).focus();
					}
				}
			}
		});

		lbDeptId.add(ar, new Label(""));
		lbDeptId.get(ar).setWidth("100%");
		lbDeptId.get(ar).setImmediate(true);
		lbDeptId.get(ar).setHeight("-1px");

		lbDeptName.add(ar, new Label(""));
		lbDeptName.get(ar).setWidth("100%");
		lbDeptName.get(ar).setImmediate(true);
		lbDeptName.get(ar).setHeight("-1px");

		lbSecId.add(ar, new Label(""));
		lbSecId.get(ar).setWidth("100%");
		lbSecId.get(ar).setImmediate(true);
		lbSecId.get(ar).setHeight("-1px");

		lbSecName.add(ar, new Label(""));
		lbSecName.get(ar).setWidth("100%");
		lbSecName.get(ar).setImmediate(true);
		lbSecName.get(ar).setHeight("-1px");


		table.addItem(new Object[]{Close.get(ar),lbSl.get(ar),lblAutoEmpID.get(ar),lblempID.get(ar),lbProxID.get(ar),
				lbEmployeeName.get(ar),lbDesignationID.get(ar),lbDesignation.get(ar),lbAttendDate.get(ar),InTime1.get(ar),InTime2.get(ar),
				InTime3.get(ar),OutTime1.get(ar),OutTime2.get(ar),OutTime3.get(ar),InTime4.get(ar),InTime5.get(ar),
				InTime6.get(ar),OutTime4.get(ar),OutTime5.get(ar),OutTime6.get(ar),txtPermitBy.get(ar),txtReason.get(ar),
				lbDeptId.get(ar),lbDeptName.get(ar),lbSecId.get(ar),lbSecName.get(ar)},ar);
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		setWidth("1180px");
		setHeight("525px");


		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:20.0px;left:20.0px;");

		cmbDate = new ComboBox();
		cmbDate.setWidth("110px");
		cmbDate.setHeight("24px");
		cmbDate.setImmediate(true);
		mainLayout.addComponent(cmbDate, "top:18.0px;left:140.0px;");

		lblDeptName = new Label();
		lblDeptName.setImmediate(true);
		lblDeptName.setWidth("-1px");
		lblDeptName.setHeight("-1px");
		lblDeptName.setValue("Department Name : ");
		mainLayout.addComponent(lblDeptName, "top:45.0px;left:20.0px;");

		cmbDeptName = new ComboBox();
		cmbDeptName.setImmediate(true);
		cmbDeptName.setWidth("300px");
		cmbDeptName.setHeight("24px");
		cmbDeptName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbDeptName, "top:43.0px;left:140.0px;");

		cmbSectionName = new ComboBox();
		cmbSectionName.setImmediate(true);
		cmbSectionName.setWidth("300px");
		cmbSectionName.setHeight("24px");
		cmbSectionName.setNullSelectionAllowed(false);
		mainLayout.addComponent(new Label("Section Name : "), "top:45.0px; left:580.0px;");
		mainLayout.addComponent(cmbSectionName, "top:43.0px;left:680.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:45.0px; left:985.0px;");

		mainLayout.addComponent(table, "top:85.0px;left:20.0px;");
		mainLayout.addComponent(cButton, "top:445.0px;left:400.0px;");
		return mainLayout;
	}
}