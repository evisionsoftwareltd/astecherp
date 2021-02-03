package acc.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class IDoubleShiftDeclare extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;
	private ComboBox cmbSection;
	private TextRead txtRamadanShiftTime;
	private CheckBox chkSectionAll;
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private CheckBox chkApplyAll;
	private CheckBox chkCancelAll;
	private PopupDateField date;
	private PopupDateField dFromDate;
	private PopupDateField dToDate;
	private OptionGroup opgShift;
	private static final List<String> Shifttype=Arrays.asList(new String[]{"I-Double Shift","Ramadan Shift"});
	//private CheckBox chkFriAll;

	private TextRead txtDepartmentID=new TextRead();
	private TextRead txtSectionId=new TextRead();
	private TextRead txtFindDate=new TextRead();

	private Table table=new Table();
	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblSl=new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblProximityID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName=new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID=new ArrayList<Label>();
	private ArrayList<Label> lblDesignation=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentID=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName=new ArrayList<Label>();
	private ArrayList<Label> lblSectionID=new ArrayList<Label>();
	private ArrayList<Label> lblSectionName=new ArrayList<Label>();
	private ArrayList<CheckBox> chkApply=new ArrayList<CheckBox>();
	private ArrayList<CheckBox> chkCancel=new ArrayList<CheckBox>();

	private TextRead totalField = new TextRead(1);

	private DecimalFormat decimal = new DecimalFormat("#0");

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "", "Exit");

	boolean isSave=false;
	boolean isUpdate=false;
	boolean isRefresh=false;
	boolean isFind=false;
	int index=0;
	String Noti="";

	public IDoubleShiftDeclare(SessionBean sessionBean)
	{
		this.sessionbean=sessionBean;
		this.setCaption("I DOUBLE SHIFT/RAMADAN SHIFT ASSIGN :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbDepartmentDataLoad();
	}

	private void cmbDepartmentDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId order by dept.vDepartmentName";
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
		finally
		{
			session.close();
		}
	}

	private void cmbSectionDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct ein.vSectionId,sein.SectionName from tbSectionInfo sein inner join " +
					"tbEmployeeInfo ein on sein.vSectionID=ein.vSectionId where ein.vDepartmentID='"+cmbDepartment.getValue()+"' " +
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
		finally
		{
			session.close();
		}
	}

	public String RamadanShiftTime()
	{
		String ret = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select Convert(varchar,tShiftStart)+ '-' + Convert(varchar,tShiftEnd) from tbshiftInformation where vShiftId = 'Shift-2'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			if(iter.hasNext())
			{
				ret = iter.next().toString();
			}
		}
		catch(Exception ex)
		{
			showNotification("Error",ex+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}

	private void setEventAction()
	{
		date.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkAllEmp.setValue(false);
				chkApplyAll.setValue(false);
			}
		});

		chkApplyAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(chkApplyAll.booleanValue())
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(!chkCancel.get(i).booleanValue())
								chkApply.get(i).setValue(true);
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(chkCancel.get(i).booleanValue())
								chkApply.get(i).setValue(false);
						}
					}
				}
			}
		});

		chkCancelAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkCancelAll.booleanValue())
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(!chkApply.get(i).booleanValue())
								chkCancel.get(i).setValue(true);
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(chkApply.get(i).booleanValue())
								chkCancel.get(i).setValue(false);
						}
					}
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(!isFind)
				{
					cmbSection.setEnabled(true);
					chkSectionAll.setValue(false);
				}
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionDataLoad();
				} 
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.setValue(null);
				cmbSection.removeAllItems();
				if(chkDepartmentAll.booleanValue())
					cmbDepartment.setEnabled(false);
				else
					cmbDepartment.setEnabled(true);
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployee.select(null);
				if(!isFind)
				{
					opgEmployee.setEnabled(true);
					cmbEmployee.setEnabled(true);
					chkAllEmp.setValue(false);
					chkApplyAll.setValue(false);

				}
				cmbEmployee.removeAllItems();
				tableclear();
				index=0;
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.setValue(null);
				opgEmployee.select(null);
				if(!isFind)
				{
					opgEmployee.setEnabled(true);
					cmbEmployee.setEnabled(true);
					chkAllEmp.setValue(false);
					chkApplyAll.setValue(false);
				}
				cmbEmployee.removeAllItems();
				if(chkSectionAll.booleanValue())
					cmbSection.setEnabled(false);
				else
					cmbSection.setEnabled(true);
				tableclear();
				index=0;

			}
		});

		opgShift.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(opgShift.getValue()=="Ramadan Shift")
				{
					dFromDate.setEnabled(true);
					dToDate.setEnabled(true);
					txtRamadanShiftTime.setEnabled(true);
					txtRamadanShiftTime.setValue(RamadanShiftTime());
				}
				else
				{
					txtRamadanShiftTime.setValue("");
				}
			}
		});

		opgShift.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(opgShift.getValue()=="I-Double Shift")
				{
					dFromDate.setEnabled(false);
					dToDate.setEnabled(false);
					txtRamadanShiftTime.setEnabled(false);
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					String sql="";
					String DepartmentID="%";
					String SectionID="%";

					if(cmbDepartment.getValue()!=null)
						DepartmentID=cmbDepartment.getValue().toString();
					if(cmbSection.getValue()!=null)
						SectionID=cmbSection.getValue().toString();

					if(opgEmployee.getValue()=="Employee ID")
					{
						sql="select vEmployeeId,employeeCode from tbEmployeeInfo where vDepartmentID like '"+DepartmentID+"' " +
								"and vSectionId like'"+SectionID+"' and ISNULL(vProximityId,'')!='' " +
								"and iStatus=1 order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Proximity ID")
					{
						sql="select vEmployeeId,vProximityID from tbEmployeeInfo where vDepartmentID like '"+DepartmentID+"' " +
								"and vSectionId like '"+SectionID+"' and ISNULL(vProximityId,'')!='' " +
								"and iStatus=1 order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Employee Name")
					{
						sql="select vEmployeeId,vEmployeeName from tbEmployeeInfo where vDepartmentID like '"+DepartmentID+"' " +
								"and vSectionId like '"+SectionID+"' and ISNULL(vProximityId,'')!='' " +
								"and iStatus=1 order by employeeCode";
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
						finally
						{
							session.close();
						}
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
				if(cmbEmployee.getValue()!=null)
				{
					tableValueAdd(cmbEmployee.getValue().toString());
					sumEmployee();
				}		
			}
		});

		chkAllEmp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(chkAllEmp.booleanValue())
					{
						opgEmployee.select(null);
						opgEmployee.setEnabled(false);
						cmbEmployee.removeAllItems();
						cmbEmployee.setEnabled(false);
						tableValueAdd("%");
						/*lblTotalEmployee.setValue(TotalEmloyee());*/
					}
					else
					{
						opgEmployee.setEnabled(true);
						cmbEmployee.setEnabled(true);
					}
				}
				else
				{
					if(!isSave && !isRefresh)
					{
						chkAllEmp.setValue(false);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				sumEmployee();
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isSave=false;
				isRefresh=false;
				txtclear();
				componentEnable(false);
				chkApplyAll.setEnabled(true);
				chkCancelAll.setEnabled(false);
				for(int tbind=0;tbind<lblEmployeeID.size();tbind++)
				{
					chkApply.get(tbind).setEnabled(true);
					chkCancel.get(tbind).setEnabled(false);
				}
				btnEnable(false);
				index=0;
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkTableData())
				{
					isSave=true;
					saveButtonEvent();
				}

				else
					showNotification("Warning", Noti, Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkTableData())
				{
					isUpdate=true;
					updateButtonEvent();
				}
				else
					showNotification("Warning", "Edit Failed!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnDelete.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkTableData())
				{
					deleteButtonEvent();
				}
				else
					showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
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

		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=true;
				findButtonEvent();
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

	private void deleteButtonEvent()
	{
		MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Delete All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		msgbox.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType==ButtonType.YES)
				{

					Session session=SessionFactoryUtil.getInstance().openSession();
					Transaction tx=session.beginTransaction();
					try
					{
						String SectionID="%";
						if(cmbSection.getValue()!=null)
							SectionID=cmbSection.getValue().toString();
						String chkQuery="select * from tbIdoubleShift where dDate='"+dateFormat.format(date.getValue())+"' " +
								"and vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionID='"+SectionID+"'";
						List <?> lst=session.createSQLQuery(chkQuery).list();
						if(!lst.isEmpty())
						{
							deleteData(session);
						}
						tx.commit();
						txtclear();
						componentEnable(true);
						btnEnable(true);
					}
					catch (Exception exp)
					{
						tx.rollback();
						showNotification("btnDelete.addListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
					finally
					{
						session.close();
					}
				}
			}
		});
	}

	private void deleteData(Session session)
	{
		String sql="delete from tbIdoubleShift where dDate='"+dateFormat.format(date.getValue())+"' and vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionId='"+cmbSection.getValue()+"'";
		session.createSQLQuery(sql).executeUpdate();
	}

	private void updateButtonEvent()
	{
		componentEnable(false);
		chkApplyAll.setEnabled(false);
		chkCancelAll.setEnabled(true);
		for(int tbind=0;tbind<lblEmployeeID.size();tbind++)
		{
			chkApply.get(tbind).setEnabled(false);
			chkCancel.get(tbind).setEnabled(true);
		}
		btnEnable(false);
	}

	private void findButtonEvent() 
	{
		Window win = new IDoubleShiftFind(sessionbean,txtDepartmentID,txtSectionId,txtFindDate);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtSectionId.getValue().toString().length() > 0)
				{
					txtclear();
					findInitialise(txtDepartmentID.getValue().toString(), txtSectionId.getValue().toString(), txtFindDate.getValue());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String DepartmentID,String SectionId, Object findDate) 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String sql = " select distinct IDS.dDate,IDS.vEmployeeID,ein.employeeCode,IDS.vProximityId,IDS.vEmployeeName,IDS.vDesignationId," +
					"IDS.vDesignationName,IDS.vDepartmentID,IDS.vDepartmentName,IDS.vSectionId,IDS.vSectionName from " +
					"tbIdoubleShift IDS inner join tbEmployeeInfo ein on IDS.vEmployeeID=ein.vEmployeeID where " +
					"IDS.dDate ='"+findDate+"' and IDS.vDepartmentID='"+DepartmentID+"' and IDS.vSectionID='"+SectionId+"'";
			List <?> list = session.createSQLQuery(sql).list();
			isFind=true;

			int i=0;
			for(Iterator <?> itr=list.iterator();itr.hasNext();)
			{
				Object[] element = (Object[]) itr.next();

				if(i==0)
				{
					date.setValue(element[0]);
					cmbDepartment.setValue(element[7]);
					cmbSection.setValue(element[9]);
				}

				lblAutoEmployeeID.get(i).setValue(element[1]);
				lblEmployeeID.get(i).setValue(element[2]);
				lblProximityID.get(i).setValue(element[3]);
				lblEmployeeName.get(i).setValue(element[4]);

				lblDesignationID.get(i).setValue(element[5]);
				lblDesignation.get(i).setValue(element[6]);
				lblDepartmentID.get(i).setValue(element[7]);
				lblDepartmentName.get(i).setValue(element[8]);
				lblSectionID.get(i).setValue(element[9]);
				lblSectionName.get(i).setValue(element[10]);
				chkApply.get(i).setValue(true);
				if(i==lblEmployeeID.size()-1)
					tableRowAdd(i+1);
				i++;
			}
		}
		catch (Exception exp)
		{
			showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
		isFind=false;
	}

	private boolean chkTableData()
	{
		boolean ret=false;
		for(int tbin=0;tbin<lblAutoEmployeeID.size();tbin++)
		{
			if(!lblAutoEmployeeID.get(tbin).getValue().toString().trim().isEmpty())
			{
				if(chkApply.get(tbin).booleanValue() || chkCancel.get(tbin).booleanValue())
				{
					ret=true;
					break;
				}
				else
				{
					Noti="Check Apply!!!";
				}
			}
			else
			{
				Noti="No Data Found!!!";
			}
		}
		return ret;
	}

	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Update All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			msgbox.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						try
						{
							String chkQuery="select * from tbIdoubleShift where dDate='"+dateFormat.format(date.getValue())+"' and vSectionID='"+cmbSection.getValue()+"'";
							List <?> lst=session.createSQLQuery(chkQuery).list();
							if(!lst.isEmpty())
							{
								deleteData(session);
								session.clear();
							}
							insertdata(session);
							tx.commit();
						}
						catch (Exception exp)
						{
							tx.rollback();
							showNotification("SaveButtonEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
						finally
						{
							session.close();
						}
					}
				}
			});
			isUpdate=false;
		}
		else
		{
			MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Save All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			msgbox.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						Session session=SessionFactoryUtil.getInstance().openSession();
						Transaction tx=session.beginTransaction();
						try
						{
							insertdata(session);
							tx.commit();
							txtclear();
							componentEnable(true);
							btnEnable(true);
						}
						catch (Exception exp)
						{
							tx.rollback();
							showNotification("SaveButtonEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
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
	
	private void insertdata(Session session)
	{
		String delIdoubleSql="";
		String ramadanShiftEntry = "";
		String deleteDate=dateFormat.format(date.getValue());
		String ramadanFromDate=dateFormat.format(dFromDate.getValue());
		String ramadanToDate=dateFormat.format(dToDate.getValue());
		for(int i=0;i<lblProximityID.size();i++) 
		{
			if(!lblProximityID.get(i).getValue().toString().isEmpty())
			{
				if(chkApply.get(i).booleanValue())
				{
					if(opgShift.getValue().toString().equals("I-Double Shift"))
					{
						delIdoubleSql="insert into tbIdoubleShift (dDate,vEmployeeID,vProximityId,vEmployeeName,vSectionId,vSectionName,"+
								"vDesignationId,vDesignationName,vUserName,vUserIp,dEntryTime,vDepartmentID,vDepartmentName) values " +
								"('"+deleteDate+"','"+lblAutoEmployeeID.get(i).getValue().toString()+"'," +
								"'"+lblProximityID.get(i).getValue().toString()+"'," +
								"'"+lblEmployeeName.get(i).getValue().toString()+"'," +
								"'"+lblSectionID.get(i).getValue().toString()+"'," +
								"'"+lblSectionName.get(i).getValue().toString()+"'," +
								"'"+lblDesignationID.get(i).getValue().toString()+"'," +
								"'"+lblDesignation.get(i).getValue().toString()+"'," +
								"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',getdate()," +
								"'"+lblDepartmentID.get(i).getValue()+"'," +
								"'"+lblDepartmentName.get(i).getValue()+"')";
						session.createSQLQuery(delIdoubleSql).executeUpdate();
					}
					if(opgShift.getValue().toString().equals("Ramadan Shift"))
					{
						ramadanShiftEntry="insert into tbRamadanShift (vShiftID,dEntryDate,dFromDate,dToDate,vEmployeeID,vProximityId,vEmployeeName,vSectionId,vSectionName,"+
								"vDesignationId,vDesignationName,vUserName,vUserIp,dEntryTime,vDepartmentID,vDepartmentName) values " +
								"('Shift-2','"+dateFormat.format(date.getValue())+"','"+ramadanFromDate+"','"+ramadanToDate+"','"+lblAutoEmployeeID.get(i).getValue().toString()+"'," +
								"'"+lblProximityID.get(i).getValue().toString()+"'," +
								"'"+lblEmployeeName.get(i).getValue().toString()+"'," +
								"'"+lblSectionID.get(i).getValue().toString()+"'," +
								"'"+lblSectionName.get(i).getValue().toString()+"'," +
								"'"+lblDesignationID.get(i).getValue().toString()+"'," +
								"'"+lblDesignation.get(i).getValue().toString()+"'," +
								"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',getdate()," +
								"'"+lblDepartmentID.get(i).getValue()+"'," +
								"'"+lblDepartmentName.get(i).getValue()+"')";
						session.createSQLQuery(ramadanShiftEntry).executeUpdate();
					}
				}
				else
				{
					delIdoubleSql="delete from tbIdoubleShift where dDate='"+dateFormat.format(date.getValue())+"' and vSectionId='"+cmbSection.getValue()+"' and vEmployeeID='"+lblAutoEmployeeID.get(i).getValue().toString()+"'";
					session.createSQLQuery(delIdoubleSql).executeUpdate();
				}
			}
		}

		txtclear();
		componentEnable(true);
		btnEnable(true);
		showNotification("All Information Saved Successfully");
	}

	private void tableValueAdd(String emp)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String DepartmentID="%";
			String SectionID="%";

			if(cmbDepartment.getValue()!=null)
				DepartmentID=cmbDepartment.getValue().toString();
			if(cmbSection.getValue()!=null)
				SectionID=cmbSection.getValue().toString();

			String sql="select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vdesignationId," +
					"din.designationName,ein.vDepartmentID,dept.vDepartmentName,ein.vSectionID,sein.sectionName " +
					"from tbEmployeeInfo ein inner join tbDepartmentInfo dept on ein.vDepartmentID=dept.vDepartmentID " +
					"inner join tbDesignationInfo din on din.designationID=ein.vDesignationID inner join tbSectionInfo " +
					"sein on sein.vSectionID=ein.vSectionID where ein.vDepartmentID like '"+DepartmentID+"' and " +
					"ein.vSectionId like '"+SectionID+"' and vEmployeeId like '"+emp+"' and " +
					"ISNULL(vProximityId,'')!='' and iStatus=1 order by ein.vDepartmentId,ein.employeeCode";

			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				boolean checkData=false;
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<lblEmployeeID.size();chkindex++)
					{

						if(lblAutoEmployeeID.get(chkindex).getValue().equals(element[0].toString()))
						{
							check=true;
							break;
						}
						if(lblProximityID.get(chkindex).getValue().toString().isEmpty())
						{
							index=chkindex;
							break;
						}
					}

					if(!check)
					{
						lblAutoEmployeeID.get(index).setValue(element[0].toString());
						lblEmployeeID.get(index).setValue(element[1].toString());
						lblProximityID.get(index).setValue(element[2].toString());
						lblEmployeeName.get(index).setValue(element[3].toString());
						lblDesignationID.get(index).setValue(element[4].toString());
						lblDesignation.get(index).setValue(element[5].toString());
						lblDepartmentID.get(index).setValue(element[6].toString());
						lblDepartmentName.get(index).setValue(element[7].toString());
						lblSectionID.get(index).setValue(element[8].toString());
						lblSectionName.get(index).setValue(element[9].toString());

						if(index==lblEmployeeID.size()-1)
							tableRowAdd(index+1);

						index++;
					}
					checkData=check;
				}
				if(checkData)
				{
					showNotification("Warning", "Employee is already Found in the list!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("TableValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void txtclear()
	{
		cmbDepartment.setValue(null);
		chkDepartmentAll.setValue(false);
		cmbSection.setValue(null);
		txtRamadanShiftTime.setValue("");
		chkSectionAll.setValue(false);
		cmbEmployee.setValue(null);
		opgEmployee.select(null);
		date.setValue(new java.util.Date());
		dFromDate.setValue(new java.util.Date());
		dToDate.setValue(new java.util.Date());
		totalField.setValue("");
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0;i<lblEmployeeID.size();i++)
		{
			lblAutoEmployeeID.get(i).setValue("");
			lblEmployeeID.get(i).setValue("");
			lblProximityID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblDepartmentID.get(i).setValue("");
			lblDepartmentName.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			chkApply.get(i).setValue(false);
			chkCancel.get(i).setValue(false);
		}
	}

	private void componentEnable(boolean b)
	{
		cmbDepartment.setEnabled(!b);
		chkDepartmentAll.setEnabled(!b);
		cmbSection.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		txtRamadanShiftTime.setEnabled(!b);
		opgShift.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		chkApplyAll.setEnabled(false);
		chkCancelAll.setEnabled(false);
		date.setEnabled(!b);
		dFromDate.setEnabled(!b);
		dToDate.setEnabled(!b);
	}

	private void btnEnable(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnEdit.setEnabled(b);
		cButton.btnDelete.setEnabled(b);
		cButton.btnRefresh.setEnabled(!b);
		cButton.btnFind.setEnabled(b);
	}

	public void tableinitialize()
	{
		for(int i=0;i<12;i++)
			tableRowAdd(i);
	}
	
	public void tableRowAdd(final int ar)
	{
		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/remove.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
public void buttonClick(ClickEvent event) 
		    
              {
				lblAutoEmployeeID.get(ar).setValue("");
				lblEmployeeID.get(ar).setValue("");
				lblProximityID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignationID.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblDepartmentID.get(ar).setValue("");
				lblDepartmentName.get(ar).setValue("");
				lblSectionID.get(ar).setValue("");
				lblSectionName.get(ar).setValue("");
				chkApply.get(ar).setValue(false);

				for(int tbIndex=ar;tbIndex<lblProximityID.size();tbIndex++)
				{
					if(tbIndex+1<lblProximityID.size())
					{
						if(!lblProximityID.get(tbIndex+1).getValue().toString().trim().equals(""))
						{
							lblAutoEmployeeID.get(tbIndex).setValue(lblAutoEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeID.get(tbIndex).setValue(lblEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblProximityID.get(tbIndex).setValue(lblProximityID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeName.get(tbIndex).setValue(lblEmployeeName.get(tbIndex+1).getValue().toString().trim());
							lblDesignationID.get(tbIndex).setValue(lblDesignationID.get(tbIndex+1).getValue().toString().trim());
							lblDesignation.get(tbIndex).setValue(lblDesignation.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentID.get(tbIndex).setValue(lblDepartmentID.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentName.get(tbIndex).setValue(lblDepartmentName.get(tbIndex+1).getValue().toString().trim());
							lblSectionID.get(tbIndex).setValue(lblSectionID.get(tbIndex+1).getValue().toString().trim());
							lblSectionName.get(tbIndex).setValue(lblSectionName.get(tbIndex+1).getValue().toString().trim());
							chkApply.get(tbIndex).setValue(chkApply.get(tbIndex+1).getValue());
							chkCancel.get(tbIndex).setValue(chkCancel.get(tbIndex+1).getValue());
							lblAutoEmployeeID.get(tbIndex+1).setValue("");
							lblEmployeeID.get(tbIndex+1).setValue("");
							lblProximityID.get(tbIndex+1).setValue("");
							lblEmployeeName.get(tbIndex+1).setValue("");
							lblDesignationID.get(tbIndex+1).setValue("");
							lblDesignation.get(tbIndex+1).setValue("");
							lblDepartmentID.get(tbIndex+1).setValue("");
							lblDepartmentName.get(tbIndex+1).setValue("");
							lblSectionID.get(tbIndex+1).setValue("");
							lblSectionName.get(tbIndex+1).setValue("");
							chkApply.get(tbIndex+1).setValue(false);
							chkCancel.get(tbIndex+1).setValue(false);
						}
					}
				}
			}
		});

		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("22.0px");
		lblSl.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");
		lblAutoEmployeeID.get(ar).setImmediate(true);

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");


		lblProximityID.add(ar, new Label());
		lblProximityID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar, new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignationID.add(ar, new Label());
		lblDesignationID.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

		lblDepartmentID.add(ar, new Label());
		lblDepartmentID.get(ar).setWidth("100%");

		lblDepartmentName.add(ar, new Label());
		lblDepartmentName.get(ar).setWidth("100%");

		lblSectionID.add(ar, new Label());
		lblSectionID.get(ar).setWidth("100%");

		lblSectionName.add(ar, new Label());
		lblSectionName.get(ar).setWidth("100%");

		chkApply.add(ar, new CheckBox());
		chkApply.get(ar).setImmediate(true);
		chkApply.get(ar).setWidth("100%");
		chkApply.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkApply.get(ar).booleanValue())
					chkCancel.get(ar).setValue(false);
				else
					chkCancel.get(ar).setValue(true);
			}
		});

		chkCancel.add(ar, new CheckBox());
		chkCancel.get(ar).setImmediate(true);
		chkCancel.get(ar).setWidth("100%");
		chkCancel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkCancel.get(ar).booleanValue())
					chkApply.get(ar).setValue(false);
				else
					chkApply.get(ar).setValue(true);
			}
		});

		table.addItem(new Object[]{lblSl.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),lblProximityID.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),lblDepartmentID.get(ar),lblDepartmentName.get(ar),
				lblSectionID.get(ar),lblSectionName.get(ar),chkApply.get(ar),chkCancel.get(ar),btnDel.get(ar)}, ar);
	}

	public void sumEmployee()
	{
		double totalEmployee = 0; 

		for(int i = 0; i<cmbEmployee.size(); i++)
		{
			if(cmbEmployee.getValue()!=null || chkAllEmp.booleanValue()==true)
			{
				totalEmployee++;
			}
		}

		table.setColumnFooter("Total Employee", decimal.format(totalEmployee)+"");
		totalField.setValue(totalEmployee);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1085.0px");
		mainlayout.setHeight("550.0px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("290.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department Name : "), "top:10.0px;left:30.0px;");
		mainlayout.addComponent(cmbDepartment, "top:08.0px;left:140.0px");

		chkDepartmentAll=new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		mainlayout.addComponent(chkDepartmentAll, "top:10.0px; left:435.0px;");

		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section Name : "), "top:35.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:33.0px;left:140.0px");

		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainlayout.addComponent(chkSectionAll, "top:35.0px; left:435.0px;");

		date=new PopupDateField();
		date.setImmediate(true);
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yyyy");
		date.setValue(new Date());
		mainlayout.addComponent(new Label("Date : "), "top:60.0px;left:30.0px;");
		mainlayout.addComponent(date, "top:58.0px;left:140.0px;");

		txtRamadanShiftTime=new TextRead();
		txtRamadanShiftTime.setWidth("150.0px");
		txtRamadanShiftTime.setHeight("20px");
		txtRamadanShiftTime.setImmediate(true);
		mainlayout.addComponent(new Label("Ramadan Shift Time : "), "top:42.0px;left:790.0px;");
		mainlayout.addComponent(txtRamadanShiftTime, "top:40.0px;left:920.0px");

		dFromDate=new PopupDateField();
		dFromDate.setImmediate(true);
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new Date());
		mainlayout.addComponent(new Label("Effective From : "), "top:10.0px;left:490.0px;");
		mainlayout.addComponent(dFromDate, "top:08.0px;left:600.0px;");

		dToDate=new PopupDateField();
		dToDate.setImmediate(true);
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new Date());
		mainlayout.addComponent(new Label("Effective To  : "), "top:35.0px;left:490.0px;");
		mainlayout.addComponent(dToDate, "top:32.0px;left:600.0px;");

		opgShift=new OptionGroup("",Shifttype);
		opgShift.setImmediate(true);
		opgShift.setValue("I-Double Shift");
		opgShift.setStyleName("horizontal");
		mainlayout.addComponent(opgShift, "top:15.0px;left:790.0px;");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		opgEmployee.setValue("Employee ID");
		mainlayout.addComponent(opgEmployee, "top:60.0px;left:435.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee Name : "), "top:90.0px;left:435.0px;");
		mainlayout.addComponent(cmbEmployee, "top:88.0px;left:540.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:90.0px;left:835.0px");

		chkCancelAll=new CheckBox("Cancel All");
		chkCancelAll.setImmediate(true);
		mainlayout.addComponent(chkCancelAll, "top:110.0px;left:990.0px");

		table.setWidth("97%");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);


		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Emp ID", Label.class, new Label());
		table.setColumnWidth("Emp ID", 70);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",100);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity ID", 90);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 150);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 120);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 30);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 130);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 130);

		table.addContainerProperty("Apply", CheckBox.class, new CheckBox());
		table.setColumnWidth("Apply", 35);

		table.addContainerProperty("Cancel", CheckBox.class, new CheckBox());
		table.setColumnWidth("Cancel", 35);

		table.addContainerProperty("Remove", NativeButton.class, new NativeButton());
		table.setColumnWidth("Remove", 50);

		
		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
		table.setFooterVisible(true);

		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER});

		mainlayout.addComponent(table, "top:130.0px;left:30.0px");
		tableinitialize();

		//table.setColumnFooter("Employee ID", "Total: ");

		chkApplyAll=new CheckBox("Apply All");
		chkApplyAll.setImmediate(true);
		mainlayout.addComponent(chkApplyAll, "top:500.0px;left:910.0px");

		mainlayout.addComponent(cButton, "top:510.0px;left:270.0px;");
		return mainlayout;
	}
}
