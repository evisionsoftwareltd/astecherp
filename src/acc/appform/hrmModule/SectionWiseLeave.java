package acc.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.TextRead;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SectionWiseLeave extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private ComboBox cmbEmployee;
	private TextRead txtDesignation;

	private PopupDateField dMonthYear;

	private Table table=new Table();;
	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> tblblSl=new ArrayList<Label>();
	private ArrayList<Label> tblblAbsentDate=new ArrayList<Label>();
	private ArrayList<PopupDateField> tbdAbsentDate=new ArrayList<PopupDateField>();
	private ArrayList<Label> tbLblDayName=new ArrayList<Label>();
	private ArrayList<ComboBox> tbcmbLeaveType=new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbRemLeave=new ArrayList<TextRead>();
	private ArrayList<TextField> tbTxtPurpose=new ArrayList<TextField>();
	private ArrayList<CheckBox> tbchkApproved=new ArrayList<CheckBox>();

	private CommonButton cButton=new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");

	private SimpleDateFormat dfDateFormate=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat yearFormat=new SimpleDateFormat("yyyy");
	private SimpleDateFormat bdDateFormat=new SimpleDateFormat("dd-MM-yyyy");

	boolean isSave=false;
	boolean isRefresh=false;
	int index=0;
	String Noti="";

	private ArrayList<Component> allComp=new ArrayList<Component>();

	public SectionWiseLeave(SessionBean sessionBean)
	{

		this.sessionbean=sessionBean;
		this.setCaption("SECTION WISE LEAVE :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbDepartmentDataLoad();
		FocusEnter();
		cmbDepartment.focus();
	}

	private void FocusEnter()
	{
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(dMonthYear);
		allComp.add(opgEmployee);
		allComp.add(cmbEmployee);
		for(int tabInd=0;tabInd<tbdAbsentDate.size();tabInd++)
		{
			allComp.add(tbcmbLeaveType.get(tabInd));
			allComp.add(tbTxtPurpose.get(tabInd));
		}
		new FocusMoveByEnter(this, allComp);
	}

	private void cmbDepartmentDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where (vEmployeeType='Permanent' or " +
					"vEmployeeType='Provisionary') order by dept.vDepartmentName";
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void cmbSectionDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct ein.vSectionId,sein.SectionName from tbSectionInfo sein inner join " +
					"tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where (vEmployeeType='Permanent' or " +
					"vEmployeeType='Provisionary') and ein.vDepartmentID='"+cmbDepartment.getValue()+"' " +
					"order by sein.SectionName";
			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("CmbSectionDataLoad", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventAction()
	{
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionDataLoad();
				}
			}
		});
		
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployee.select(null);
				cmbEmployee.removeAllItems();
				txtDesignation.setValue("");
			}
		});

		dMonthYear.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployee.select(null);
				cmbEmployee.removeAllItems();
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					String sql="";
					if(opgEmployee.getValue()=="Employee ID")
					{
						sql="select autoEmpID,empId from funCalcMonthlyAttendance('"+dfDateFormate.format(dMonthYear.getValue())+"','%','"+cmbDepartment.getValue().toString()+"','"+cmbSection.getValue().toString()+"') " +
								"where txtDate=convert(date,dateadd(DD,-1,Dateadd(mm,1,convert(date,CONVERT(varchar(20),YEAR('"+dfDateFormate.format(dMonthYear.getValue())+"'))+'-'+" +
								"convert(varchar(20),Month('"+dfDateFormate.format(dMonthYear.getValue())+"'))+'-01',121)))) and (empType='Permanent' or empType='Provisionary') and absentCount>0";
					}
					else if(opgEmployee.getValue()=="Proximity ID")
					{
						sql="select autoEmpID,empCode from funCalcMonthlyAttendance('"+dfDateFormate.format(dMonthYear.getValue())+"','%','"+cmbDepartment.getValue().toString()+"','"+cmbSection.getValue().toString()+"') " +
								"where txtDate=convert(date,dateadd(DD,-1,Dateadd(mm,1,convert(date,CONVERT(varchar(20),YEAR('"+dfDateFormate.format(dMonthYear.getValue())+"'))+'-'+" +
								"convert(varchar(20),Month('"+dfDateFormate.format(dMonthYear.getValue())+"'))+'-01',121)))) and (empType='Permanent' or empType='Provisionary') and absentCount>0";
					}
					else if(opgEmployee.getValue()=="Employee Name")
					{
						sql="select autoEmpID,empName from funCalcMonthlyAttendance('"+dfDateFormate.format(dMonthYear.getValue())+"','%','"+cmbDepartment.getValue().toString()+"','"+cmbSection.getValue().toString()+"') " +
								"where txtDate=convert(date,dateadd(DD,-1,Dateadd(mm,1,convert(date,CONVERT(varchar(20),YEAR('"+dfDateFormate.format(dMonthYear.getValue())+"'))+'-'+" +
								"convert(varchar(20),Month('"+dfDateFormate.format(dMonthYear.getValue())+"'))+'-01',121)))) and (empType='Permanent' or empType='Provisionary') and absentCount>0";
					}
					if(!sql.equals(""))
					{
						Session session=SessionFactoryUtil.getInstance().openSession();
						session.beginTransaction();
						try
						{
							List <?> lst=session.createSQLQuery(sql).list();
							if(!lst.isEmpty())
							{
								Iterator <?> itr=lst.iterator();
								while(itr.hasNext())
								{
									Object[] element=(Object[])itr.next();
									cmbEmployee.addItem(element[0]);
									cmbEmployee.setItemCaption(element[0], element[1].toString());
								}
							}
							else
								showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
						}
						catch(Exception exp)
						{
							showNotification("OPGEmployee", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
						finally{session.close();}
					}
				}

				else
				{
					if(!isSave && !isRefresh)
					{
						opgEmployee.select(null);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				txtDesignation.setValue("");
				tableclear();
				if(cmbEmployee.getValue()!=null)
				{
					Session session=SessionFactoryUtil.getInstance().openSession();
					session.beginTransaction();
					try
					{
						String chkMonthlySalarySQL="select * from tbSalary where autoEmployeeID='"+cmbEmployee.getValue().toString().trim()+"' " +
												   "and vMonthName=DateName(MM,'"+dfDateFormate.format(dMonthYear.getValue())+"') and " +
												   "year='"+yearFormat.format(dMonthYear.getValue())+"'";
						List <?> lst=session.createSQLQuery(chkMonthlySalarySQL).list();
						if(lst.isEmpty())
						{
							tableValueAdd(cmbEmployee.getValue().toString());
						}
						else
						{
							showNotification("Warning", "Salary already generated for the Employee!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					catch(Exception exp)
					{
						showNotification("cmbEmployee.addListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
					finally{session.close();}
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isSave=false;
				isRefresh=false;
				componentEnable(false);
				btnEnable(false);
				index=0;
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbSection.getValue()!=null)
				{
					if(dMonthYear.getValue()!=null)
					{
						if(cmbEmployee.getValue()!=null)
						{
							if(chkTableValue())
							{
								isSave=true;
								saveButtonEvent();
							}
							else
								showNotification("Warning", Noti, Notification.TYPE_WARNING_MESSAGE);
						}
						else
							showNotification("Warning", "Please Select Employee Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
					else
						showNotification("Warning", "Please Select Date!!!", Notification.TYPE_WARNING_MESSAGE);
				}

				else
					showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);

			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isRefresh=true;
				txtclear();
				componentEnable(true);
				btnEnable(true);
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

	private boolean chkTableValue()
	{
		boolean ret=false;
		for(int tbindex=0;tbindex<tbdAbsentDate.size();tbindex++)
		{
			if(tbchkApproved.get(tbindex).booleanValue())
			{
				if(tbdAbsentDate.get(tbindex).getValue()!=null)
				{
					if(tbcmbLeaveType.get(tbindex).getValue()!=null)
					{
						if(!tbTxtPurpose.get(tbindex).getValue().toString().trim().isEmpty())
						{
							ret=true;
						}
						else
						{
							ret=false;
							Noti="Please Provide Leave Purpose!!!";
							tbTxtPurpose.get(tbindex).focus();
							break;
						}
					}
					else
					{
						ret=false;
						Noti="Please Select Leave Type!!!";
						tbcmbLeaveType.get(tbindex).focus();
						break;
					}
				}
				else
				{
					ret=false;
					Noti="No Date Found!!!";
					tbchkApproved.get(tbindex).setValue(false);
					break;
				}
			}
		}
		return ret;
	}

	private void saveButtonEvent()
	{
		MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Update All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		msgbox.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType==ButtonType.YES)
				{
					insertdata();
					componentEnable(true);
					btnEnable(true);
					txtclear();
				}
			}
		});
	}

	private void insertdata()
	{
		String sql="";
		String sqlEnjoyedLeave="";
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			for(int i=0;i<tbdAbsentDate.size();i++) 
			{
				if(tbdAbsentDate.get(i).getValue()!=null)
				{
					if(tbchkApproved.get(i).booleanValue())
					{
						sql="insert into tbEmployeeLeave (dApplicationDate,vAutoEmployeeId,vEmployeeId,vProximityID,vSectionID,vDesignationId,dJoiningDate," +
								"vLeaveType,dApplyFrom,dApplyTo,vPurposeOfLeave,vLeaveAddress,vMobileNo,dSenctionFrom,dSenctionTo," +
								"iNoOfDays,iNoOfFridays,vRemarks,iApprove,vUserId,dEntryTime,vUserIp,vDepartmentID) values " +
								"('"+dfDateFormate.format(tbdAbsentDate.get(i).getValue())+"'," +
								"'"+cmbEmployee.getValue().toString()+"',(select employeeCode from tbEmployeeInfo where vEmployeeId='"+cmbEmployee.getValue().toString()+"')," +
								"(select vProximityId from tbEmployeeInfo where vEmployeeId='"+cmbEmployee.getValue().toString()+"'),'"+cmbSection.getValue().toString()+"'," +
								"(select ISNULL(designationId,'') from tbDesignationInfo din where din.designationName='"+txtDesignation.getValue().toString()+"')," +
								"ISNULL((select dJoiningDate from tbEmployeeInfo ein where ein.vProximityId='"+cmbEmployee.getValue().toString()+"'),'1900-01-01')," +
								"'"+tbcmbLeaveType.get(i).getValue().toString()+"','"+dfDateFormate.format(tbdAbsentDate.get(i).getValue())+"'," +
								"'"+dfDateFormate.format(tbdAbsentDate.get(i).getValue())+"','"+tbTxtPurpose.get(i).getValue()+"'," +
								"'Astech Group, HRD.',ISNULL((select vContact from tbEmployeeInfo ein where ein.vProximityId='"+cmbEmployee.getValue().toString()+"'),'')," +
								"'"+dfDateFormate.format(tbdAbsentDate.get(i).getValue())+"','"+dfDateFormate.format(tbdAbsentDate.get(i).getValue())+"',1,0,'Remarks',1," +
								"'"+sessionbean.getUserName()+"',getdate(),'"+sessionbean.getUserIp()+"','"+cmbDepartment.getValue()+"')";
						session.createSQLQuery(sql).executeUpdate();

						if(tbcmbLeaveType.get(i).getValue().toString().equals("1"))
						{
							if(Integer.parseInt(session.createSQLQuery("select iClyBalance from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"'").list().iterator().next().toString().trim())>0)
							{
								sqlEnjoyedLeave="update tbLeaveBalanceNew set iClEnjoyed=iClEnjoyed+1, iClyBalance=iClyBalance, iClBalance=iClyBalance-(iClEnjoyed+1) where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"' and iClyBalance>0";
							}
							else if(Integer.parseInt(session.createSQLQuery("select iClOpening from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"'").list().iterator().next().toString().trim())>0)
							{
								sqlEnjoyedLeave="update tbLeaveBalanceNew set iClEnjoyed=iClEnjoyed+1, iClOpening=iClOpening, iClBalance=iClOpening-(iClEnjoyed+1) where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"' and iClOpening>0";
							}
						}
						if(tbcmbLeaveType.get(i).getValue().toString().equals("2"))
						{
							if(Integer.parseInt(session.createSQLQuery("select iSlyBalance from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"'").list().iterator().next().toString().trim())>0)
							{
								sqlEnjoyedLeave="update tbLeaveBalanceNew set iSlEnjoyed=iSlEnjoyed+1, iSlyBalance=iSlyBalance, iSlBalance=iSlyBalance-(iSlEnjoyed+1) where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"' and iSlyBalance>0";
							}
							else if(Integer.parseInt(session.createSQLQuery("select iSlOpening from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"'").list().iterator().next().toString().trim())>0)
							{
								sqlEnjoyedLeave="update tbLeaveBalanceNew set iSlEnjoyed=iSlEnjoyed+1, iSlOpening=iSlOpening, iSlBalance=iSlOpening-(iSlEnjoyed+1) where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"' and iSlOpening>0";
							}
						}
						if(tbcmbLeaveType.get(i).getValue().toString().equals("3"))
						{
							if(Integer.parseInt(session.createSQLQuery("select iAlyBalance from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"'").list().iterator().next().toString().trim())>0)
							{
								sqlEnjoyedLeave="update tbLeaveBalanceNew set iAlEnjoyed=iAlEnjoyed+1, iAlyBalance=iAlyBalance, iAlBalance=iAlyBalance-(iAlEnjoyed+1) where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"' and iAlyBalance>0";
							}
							else if(Integer.parseInt(session.createSQLQuery("select iAlOpening from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"'").list().iterator().next().toString().trim())>0)
							{
								sqlEnjoyedLeave="update tbLeaveBalanceNew set iAlEnjoyed=iAlEnjoyed+1, iAlOpening=iAlOpening, iAlBalance=iAlOpening-(iAlEnjoyed+1) where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"' and iAlOpening>0";
							}
						}
						if(tbcmbLeaveType.get(i).getValue().toString().equals("4"))
						{
							if(Integer.parseInt(session.createSQLQuery("select iMlyBalance from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"'").list().iterator().next().toString().trim())>0)
							{
								sqlEnjoyedLeave="update tbLeaveBalanceNew set iMlEnjoyed=iMlEnjoyed+1, iMlyBalance=iMlyBalance, iMlBalance=iMlyBalance-(iMlEnjoyed+1) where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"' and iMlyBalance>0";
							}
							else if(Integer.parseInt(session.createSQLQuery("select iMlOpening from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"'").list().iterator().next().toString().trim())>0)
							{
								sqlEnjoyedLeave="update tbLeaveBalanceNew set iMlEnjoyed=iMlEnjoyed+1, iMlOpening=iMlOpening, iMlBalance=iMlOpening-(iMlEnjoyed+1) where vAutoEmployeeId='"+cmbEmployee.getValue().toString()+"' and iMlOpening>0";
							}
						}

						session.createSQLQuery(sqlEnjoyedLeave).executeUpdate();
					}
				}
			}
			showNotification("All Information Saved Successfully");
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("InsertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void tableValueAdd(String emp)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select designation,txtDate,txtDay from funCalcMonthlyAttendance('"+dfDateFormate.format(dMonthYear.getValue())+"','"+emp+"','"+cmbDepartment.getValue().toString()+"','"+cmbSection.getValue().toString()+"') where " +
					"(((ISNULL(inTime,'')='' or ISNULL(outTime,'')='') and ISNULL(inTimeMax,'')='' and " +
					"ISNULL(outTimeMax,'')='') or DATEPART(hh,totalHour)<7) and txtDate<=convert(date,getDate())  and txtStatus not like '%Holiday%' and txtStatus not like '%Leave%'";

			List <?> lst=session.createSQLQuery(sql).list();
			session.clear();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				int i=0;
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();

					if(i==0)
						txtDesignation.setValue(element[0].toString());

					tbdAbsentDate.get(i).setReadOnly(false);
					tbdAbsentDate.get(i).setValue(element[1]);
					tbdAbsentDate.get(i).setReadOnly(true);
					
					tblblAbsentDate.get(i).setValue(bdDateFormat.format(element[1]));
					
					tbLblDayName.get(i).setValue(element[2].toString());

					if(element[2].toString().equalsIgnoreCase("Friday"))
					{
						tblblAbsentDate.get(i).setStyleName("dateColorStyle");
						tbLblDayName.get(i).setStyleName("dateColorStyle");
					}
					
					String leaveSql="select iLeaveTypeID,vLeaveTypeName from tbLeaveType";
					List <?> lst1=session.createSQLQuery(leaveSql).list();

					for(Iterator <?> itr1=lst1.iterator();itr1.hasNext();)
					{
						Object [] element1=(Object [])itr1.next();

						tbcmbLeaveType.get(i).addItem(element1[0]);
						tbcmbLeaveType.get(i).setItemCaption(element1[0], element1[1].toString());
					}

					if(i==tbTxtPurpose.size()-1)
						tableRowAdd(i+1);
					i++;
				}
			}
			else
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("TableValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void txtclear()
	{
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		opgEmployee.select(null);
		cmbEmployee.setValue(null);
		txtDesignation.setValue("");
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0;i<tbTxtPurpose.size();i++)
		{
			tbdAbsentDate.get(i).setReadOnly(false);
			tbdAbsentDate.get(i).setValue(null);
			tbdAbsentDate.get(i).setReadOnly(true);
			tblblAbsentDate.get(i).setValue("");
			tbLblDayName.get(i).setValue("");
			tbcmbLeaveType.get(i).setValue(null);
			tbRemLeave.get(i).setValue("");
			tbTxtPurpose.get(i).setValue("");
			tbchkApproved.get(i).setValue(false);
			tbcmbLeaveType.get(i).removeAllItems();
		}
	}

	private void componentEnable(boolean b)
	{
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		txtDesignation.setEnabled(!b);
		dMonthYear.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnEnable(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnRefresh.setEnabled(!b);
	}

	public void tableinitialize()
	{
		for(int i=0;i<12;i++)
			tableRowAdd(i);
	}

	public void tableRowAdd(final int ar)
	{
		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("30px");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tbdAbsentDate.get(ar).setReadOnly(false);
				tbdAbsentDate.get(ar).setValue(null);
				tbdAbsentDate.get(ar).setReadOnly(true);
				tblblAbsentDate.get(ar).setValue("");
				tbcmbLeaveType.get(ar).setValue(null);
				tbRemLeave.get(ar).setValue("");
				tbTxtPurpose.get(ar).setValue("");
				tbchkApproved.get(ar).setValue(false);

				for(int tbIndex=ar;tbIndex<tbTxtPurpose.size();tbIndex++)
				{
					if(tbIndex+1<tbdAbsentDate.size())
					{
						if(tbdAbsentDate.get(tbIndex+1).getValue()!=null)
						{
							tbdAbsentDate.get(tbIndex).setReadOnly(false);
							tbdAbsentDate.get(tbIndex).setValue(tbdAbsentDate.get(tbIndex+1).getValue());
							tbdAbsentDate.get(tbIndex).setReadOnly(true);
							tblblAbsentDate.get(tbIndex).setValue(tblblAbsentDate.get(tbIndex+1).getValue().toString().trim());
							tbcmbLeaveType.get(tbIndex).setValue(tbcmbLeaveType.get(tbIndex+1).getValue());
							tbRemLeave.get(tbIndex).setValue(tbRemLeave.get(tbIndex+1).getValue());
							tbTxtPurpose.get(tbIndex).setValue(tbTxtPurpose.get(tbIndex+1).getValue().toString().trim());
							tbchkApproved.get(tbIndex).setValue(tbchkApproved.get(tbIndex+1).getValue());

							tbdAbsentDate.get(tbIndex+1).setReadOnly(false);
							tbdAbsentDate.get(tbIndex+1).setValue(null);
							tbdAbsentDate.get(tbIndex+1).setReadOnly(true);
							tblblAbsentDate.get(tbIndex+1).setValue("");
							tbcmbLeaveType.get(tbIndex+1).setValue(null);
							tbRemLeave.get(tbIndex+1).setValue("");
							tbTxtPurpose.get(tbIndex+1).setValue("");
							tbchkApproved.get(tbIndex+1).setValue(false);
						}
					}
				}
			}
		});

		tblblSl.add(ar, new Label(""));
		tblblSl.get(ar).setWidth("100%");
		tblblSl.get(ar).setHeight("20px");
		tblblSl.get(ar).setValue(ar+1);

		tbdAbsentDate.add(ar, new PopupDateField());
		tbdAbsentDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
		tbdAbsentDate.get(ar).setImmediate(true);
		tbdAbsentDate.get(ar).setWidth("100%");
		tbdAbsentDate.get(ar).setReadOnly(true);
		tbdAbsentDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbdAbsentDate.get(ar).setValue(null);

		tblblAbsentDate.add(ar, new Label());
		tblblAbsentDate.get(ar).setImmediate(true);
		tblblAbsentDate.get(ar).setWidth("100%");
		
		tbLblDayName.add(ar, new Label());
		tbLblDayName.get(ar).setWidth("100%");
		
		tbcmbLeaveType.add(ar, new ComboBox());
		tbcmbLeaveType.get(ar).setImmediate(true);
		tbcmbLeaveType.get(ar).setWidth("100%");
		tbcmbLeaveType.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tbRemLeave.get(ar).setValue("");
				if(tbcmbLeaveType.get(ar).getValue()!=null)
				{
					String query="";
					String presentYearQuery="";

					Session session=SessionFactoryUtil.getInstance().openSession();
					session.beginTransaction();
					try
					{
						presentYearQuery=session.createSQLQuery("select Max(year(currentYear)) from tbLeaveBalanceNew").list().iterator().next().toString().trim();

						if(presentYearQuery.equalsIgnoreCase(session.createSQLQuery("select Year('"+dfDateFormate.format(dMonthYear.getValue())+"')").list().iterator().next().toString().trim()))
						{
							if(tbcmbLeaveType.get(ar).getValue().toString().equals("1"))
							{
								query="select ISNULL(iClOpening+iClyBalance-iClEnjoyed,0) from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString().trim()+"' and year(currentYear)=Year('"+dfDateFormate.format(dMonthYear.getValue())+"') and iflag=1";
							}

							if(tbcmbLeaveType.get(ar).getValue().toString().equals("2"))
							{
								query="select ISNULL(iSlOpening+iSlyBalance-iSlEnjoyed,0) from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString().trim()+"' and year(currentYear)=Year('"+dfDateFormate.format(dMonthYear.getValue())+"') and iflag=1";
							}

							if(tbcmbLeaveType.get(ar).getValue().toString().equals("3"))
							{
								query="select ISNULL(iAlOpening+iAlyBalance-iAlEnjoyed,0) from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString().trim()+"' and year(currentYear)=Year('"+dfDateFormate.format(dMonthYear.getValue())+"') and iflag=1";
							}

							if(tbcmbLeaveType.get(ar).getValue().toString().equals("4"))
							{
								query="select ISNULL(iMlOpening+iMlyBalance-iMlEnjoyed,0) from tbLeaveBalanceNew where vAutoEmployeeId='"+cmbEmployee.getValue().toString().trim()+"' and year(currentYear)=Year('"+dfDateFormate.format(dMonthYear.getValue())+"') and iflag=1";
							}

							leaveBalanceCheck(session,query,ar);
							addRemainingLeave(session,query,ar);
						}
						else
						{
							showNotification("Warning", "Already Carry Forward the Previous Year Leave!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					catch(Exception exp)
					{
						showNotification("tbcmbLeaveType.ValueChangeListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
					finally{session.close();}
				}
			}
		});

		tbRemLeave.add(ar, new TextRead(""));
		tbRemLeave.get(ar).setImmediate(true);
		tbRemLeave.get(ar).setWidth("100%");
		tbRemLeave.get(ar).setHeight("20px");

		tbTxtPurpose.add(ar, new TextField());
		tbTxtPurpose.get(ar).setImmediate(true);
		tbTxtPurpose.get(ar).setWidth("100%");

		tbchkApproved.add(ar, new CheckBox());
		tbchkApproved.get(ar).setImmediate(true);
		tbchkApproved.get(ar).setWidth("100%");

		table.addItem(new Object[]{tblblSl.get(ar),tbdAbsentDate.get(ar),tblblAbsentDate.get(ar),tbLblDayName.get(ar),tbcmbLeaveType.get(ar),tbRemLeave.get(ar),tbTxtPurpose.get(ar),tbchkApproved.get(ar),btnDel.get(ar)}, ar);
	}

	private void addRemainingLeave(Session session,String query,int i)
	{
		String remLeave="";
		List <?> lst=session.createSQLQuery(query).list();
		if(!lst.isEmpty())
			remLeave=lst.iterator().next().toString().trim();
		else
			remLeave="0";
		tbRemLeave.get(i).setValue(remLeave);
	}

	private void leaveBalanceCheck(Session session,String query, int ind)
	{
		try
		{
			String strLeave="";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				strLeave=lst.iterator().next().toString().trim();
			else
				strLeave="0";

			if(Integer.parseInt(strLeave)<=0)
			{
				tbcmbLeaveType.get(ind).setValue(null);
				tbRemLeave.get(ind).setValue("");
				showNotification("Warning", "Remaining "+tbcmbLeaveType.get(ind).getItemCaption(tbcmbLeaveType.get(ind).getValue())+" is 0!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("LeaveBalanceCheck",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("950.0px");
		mainlayout.setHeight("530.0px");
		
		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("290.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department Name : "), "top:10.0px;left:30.0px;");
		mainlayout.addComponent(cmbDepartment, "top:08.0px;left:140.0px");
		
		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section Name : "), "top:40.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:38.0px;left:140.0px");

		dMonthYear=new PopupDateField();
		dMonthYear.setImmediate(true);
		dMonthYear.setWidth("135px");
		dMonthYear.setHeight("-1px");
		dMonthYear.setDateFormat("MMMMM-yyyy");
		dMonthYear.setResolution(PopupDateField.RESOLUTION_MONTH);
		dMonthYear.setValue(new Date());
		mainlayout.addComponent(new Label("Month : "), "top:70.0px;left:30.0px;");
		mainlayout.addComponent(dMonthYear, "top:68.0px;left:140.0px;");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainlayout.addComponent(opgEmployee, "top:10.0px;left:495.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee : "), "top:40.0px;left:495.0px;");
		mainlayout.addComponent(cmbEmployee, "top:38.0px;left:580.0px;");

		txtDesignation=new TextRead();
		txtDesignation.setWidth("220.0px");
		txtDesignation.setHeight("-1px");
		txtDesignation.setImmediate(true);
		mainlayout.addComponent(new Label("Designation : "), "top:70.0px;left:495.0px;");
		mainlayout.addComponent(txtDesignation, "top:68.0px;left:580.0px;");

		table.setWidth("99%");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);
		
		table.addContainerProperty("Abs. Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Abs. Date", 100);

		table.addContainerProperty("Absent Date", Label.class, new Label());
		table.setColumnWidth("Absent Date", 100);
		
		table.addContainerProperty("Day Name", Label.class, new Label());
		table.setColumnWidth("Day Name", 100);
		
		table.addContainerProperty("Leave Type", ComboBox.class, new ComboBox());
		table.setColumnWidth("Leave Type", 110);

		table.addContainerProperty("Rem. Leave", TextRead.class, new TextRead());
		table.setColumnWidth("Rem. Leave", 80);

		table.addContainerProperty("Purpose", TextField.class, new TextField());
		table.setColumnWidth("Purpose", 255);

		table.addContainerProperty("Approved", CheckBox.class, new CheckBox());
		table.setColumnWidth("Approved", 55);

		table.addContainerProperty("Delete", NativeButton.class, new NativeButton());
		table.setColumnWidth("Delete", 55);

		table.setColumnCollapsed("Abs. Date", true);
		
		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:100.0px;left:30.0px");
		tableinitialize();

		mainlayout.addComponent(cButton, "top:490.0px;left:290.0px;");
		return mainlayout;
	}
}
