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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SectionWiseActiveInactiveEmployee extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private CheckBox chkAllSection;
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private CheckBox chkInactiveAll;
	private CheckBox chkActiveAll;
	private PopupDateField date;

	private TextRead txtSectionId=new TextRead();
	private TextRead txtFindDate=new TextRead();
	private TextRead txtDepartmentId=new TextRead();

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
	private ArrayList<TextField> txtPermittedBy=new ArrayList<TextField>();
	private ArrayList<TextField> txtReason=new ArrayList<TextField>();
	private ArrayList<CheckBox> chkInactive=new ArrayList<CheckBox>();
	private ArrayList<CheckBox> chkActive=new ArrayList<CheckBox>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dbDateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");

	boolean isSave=false;
	boolean isUpdate=false;
	boolean isRefresh=false;
	boolean isFind=false;
	int index=0;
	String Noti="";

	public SectionWiseActiveInactiveEmployee(SessionBean sessionBean)
	{
		this.sessionbean=sessionBean;
		this.setCaption("ACTIVE/INACTIVE EMPLOYEE :: "+sessionbean.getCompany());
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
					"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where ein.iStatus=1 and ISNULL(ein.vProximityID,'')!='' " +
					"order by dept.vDepartmentName";
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
					"tbEmployeeInfo ein on sein.vSectionID=ein.vSectionId where ein.vDepartmentID='"+cmbDepartment.getValue()+"' and " +
					"ein.iStatus=1 and ISNULL(ein.vProximityID,'')!='' order by sein.SectionName";
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

	private void setEventAction()
	{

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				cmbSection.setEnabled(true);
				chkAllSection.setValue(false);
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionDataLoad();
				}
			}
		});

		date.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkAllEmp.setValue(false);
				chkInactiveAll.setValue(false);
			}
		});

		chkInactiveAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkInactiveAll.booleanValue())
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(!chkActive.get(i).booleanValue())
							{
								chkInactive.get(i).setValue(true);
								chkActive.get(i).setValue(false);
							}
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(chkActive.get(i).booleanValue())
							{
								chkInactive.get(i).setValue(false);
								chkActive.get(i).setValue(true);
							}
						}
					}
				}
			}
		});

		chkActiveAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkActiveAll.booleanValue())
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(!chkInactive.get(i).booleanValue())
							{
								chkActive.get(i).setValue(true);
								chkInactive.get(i).setValue(false);
							}
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(chkInactive.get(i).booleanValue())
							{
								chkInactive.get(i).setValue(true);
								chkActive.get(i).setValue(false);
							}
						}
					}
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployee.select(null);
				chkAllEmp.setValue(false);
				chkInactiveAll.setValue(false);
				cmbEmployee.removeAllItems();
				tableclear();
			}
		});

		chkAllSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableclear();
				if(chkAllSection.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					opgEmployee.select(null);
					chkAllEmp.setValue(false);
					cmbEmployee.removeAllItems();
				}
				else
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(true);
					opgEmployee.select(null);
					chkAllEmp.setValue(false);
					cmbEmployee.removeAllItems();
				}
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null || chkAllSection.booleanValue())
				{
					String sql="";
					String sectionID="%";
					if(cmbSection.getValue()!=null)
						sectionID=cmbSection.getValue().toString();

					if(opgEmployee.getValue()=="Employee ID")
					{
						sql="select vEmployeeId,employeeCode from tbEmployeeInfo where " +
								"vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionId like '"+sectionID+"' " +
								"and iStatus=1 and ISNULL(vProximityId,'')!='' order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Proximity ID")
					{    
						sql="select vEmployeeId,vProximityId from tbEmployeeInfo where " +
								"vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionId like '"+sectionID+"' " +
								"and iStatus=1 and ISNULL(vProximityId,'')!='' order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Employee Name")
					{
						sql="select vEmployeeId,vEmployeeName from tbEmployeeInfo where " +
								"vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionId like '"+sectionID+"' " +
								"and iStatus=1 and ISNULL(vProximityId,'')!='' order by employeeCode";
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
					tableValueAdd(cmbEmployee.getValue().toString());
			}
		});

		chkAllEmp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllEmp.booleanValue())
				{
					opgEmployee.select(null);
					opgEmployee.setEnabled(false);
					cmbEmployee.removeAllItems();
					cmbEmployee.setEnabled(false);
					tableValueAdd("%");
				}
				else
				{
					opgEmployee.setEnabled(true);
					cmbEmployee.setEnabled(true);
				}
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
				if(!lblAutoEmployeeID.get(0).getValue().toString().isEmpty())
				{
					isUpdate=true;
					updateButtonEvent();
				}
				else
					showNotification("Warning", "Edit Failed!!!", Notification.TYPE_WARNING_MESSAGE);
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

	private void updateButtonEvent()
	{
		componentEnable(false);
		btnEnable(false);
	}

	private void findButtonEvent() 
	{
		Window win = new SectionWiseActiveInactiveFind(sessionbean,txtDepartmentId,txtSectionId,txtFindDate);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtSectionId.getValue().toString().length() > 0)
				{
					txtclear();
					findInitialise(txtDepartmentId.getValue().toString(),txtSectionId.getValue().toString(), txtFindDate.getValue());
					componentEnable(true);
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
			String sql = " select distinct convert(date,dInactiveDate) dDate,vEmployeeID,(select employeeCode " +
					"from tbEmployeeInfo ein where ein.vEmployeeId=IE.vEmployeeID) employeeCode," +
					"vProximityId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentID," +
					"vDepartmentName,vSectionId,vSectionName,vPermittedBy,vReason,vActive_Inactive from " +
					"tbInactiveEmployee IE where convert(date,dInactiveDate) ='"+findDate+"' and vDepartmentID='"+DepartmentID+"' " +
					"and vSectionID='"+SectionId+"' and vActive_Inactive='Inactive' order by vDepartmentName,vSectionName,vEmployeeID";
			List <?> list = session.createSQLQuery(sql).list();
			isFind=true;

			int i=0;
			for(Iterator <?> itr=list.iterator();itr.hasNext();)
			{
				Object[] element = (Object[]) itr.next();

				if(i==lblEmployeeID.size()-1)
				{
					tableRowAdd(i+1);
				}
				
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
				txtPermittedBy.get(i).setValue(element[11]);
				txtReason.get(i).setValue(element[12]);
				if(element[13].toString().equalsIgnoreCase("Active"))
					chkActive.get(i).setEnabled(true);
				else
					chkInactive.get(i).setValue(true);
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
		if(!isUpdate)
		{
			for(int tbin=0;tbin<lblAutoEmployeeID.size();tbin++)
			{
				if(!lblAutoEmployeeID.get(tbin).getValue().toString().trim().isEmpty())
				{
					if(!txtPermittedBy.get(tbin).getValue().toString().trim().isEmpty())
					{
						ret=false;
						if(!txtReason.get(tbin).getValue().toString().trim().isEmpty())
						{
							if(chkInactive.get(tbin).booleanValue())
							{
								ret=true;
							}
							else
							{
								Noti="Check Inactive!!!";
								break;
							}
						}
						else
						{
							txtReason.get(tbin).focus();
							Noti="Provide Reason!!!";
							break;
						}
					}
					else
					{
						txtPermittedBy.get(tbin).focus();
						Noti="Provide Permitted By!!!";
						break;
					}
				}
				else
				{
					Noti="No Data Found!!!";
				}
			}
		}
		else
		{
			ret=false;
			for(int tbin=0;tbin<lblAutoEmployeeID.size();tbin++)
			{
				if(!lblAutoEmployeeID.get(tbin).getValue().toString().trim().isEmpty())
				{
					if(!txtPermittedBy.get(tbin).getValue().toString().trim().isEmpty())
					{
						if(!txtReason.get(tbin).getValue().toString().trim().isEmpty())
						{
							if(chkActive.get(tbin).booleanValue())
							{
								ret=true;
							}
							else
							{
								Noti="Check Active!!!";
								break;
							}
						}
						else
						{
							txtReason.get(tbin).focus();
							Noti="Provide Reason!!!";
							break;
						}
					}
					else
					{
						txtPermittedBy.get(tbin).focus();
						Noti="Provide Permitted By!!!";
						break;
					}
				}
				else
				{
					Noti="No Data Found!!!";
				}
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
							updateData(session);
							showNotification("All Information Updated Successfully");
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
							insertData(session);
							showNotification("All Information Saved Successfully");
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

	private void insertData(Session session)
	{
		String Sql="";
		for(int i=0;i<lblProximityID.size();i++) 
		{
			if(!lblProximityID.get(i).getValue().toString().isEmpty() && chkInactive.get(i).booleanValue())
			{

				Sql="update tbEmployeeInfo set vStatus='Discontinue', iStatus='0', dStatusDate='"+dateFormat.format(date.getValue())+"' "+
						"where vEmployeeId='"+lblAutoEmployeeID.get(i).getValue().toString()+"'";

				session.createSQLQuery(Sql).executeUpdate();
				session.clear();

				Sql="insert into tbInactiveEmployee (dInactiveDate,vEmployeeId,vEmployeeCode,vProximityId," +
						"vEmployeeName,vSectionID,vSectionName,vDesignationId,vDesignationName,vActive_Inactive,vPermittedBy," +
						"vReason,vUserName,vUserIP,dEntryTime,vDepartmentID,vDepartmentName,dActiveDate) values " +
						"('"+dateFormat.format(date.getValue())+"'," +
						"'"+lblAutoEmployeeID.get(i).getValue().toString()+"'," +
						"'"+lblEmployeeID.get(i).getValue().toString()+"'," +
						"'"+lblProximityID.get(i).getValue().toString()+"'," +
						"'"+lblEmployeeName.get(i).getValue().toString()+"'," +
						"'"+lblSectionID.get(i).getValue().toString()+"'," +
						"'"+lblSectionName.get(i).getValue().toString()+"'," +
						"'"+lblDesignationID.get(i).getValue().toString()+"'," +
						"'"+lblDesignation.get(i).getValue().toString()+"'," +
						"'Inactive','"+txtPermittedBy.get(i).getValue()+"'," +
						"'"+txtReason.get(i).getValue()+"'," +
						"'"+sessionbean.getUserName()+"'," +
						"'"+sessionbean.getUserIp()+"'," +
						"getdate()," +
						"'"+cmbDepartment.getValue()+"'," +
						"'"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"','1900-01-01')";
				session.createSQLQuery(Sql).executeUpdate();
				session.clear();
			}
		}
	}

	private void updateData(Session session)
	{
		String Sql="";
		for(int i=0;i<lblProximityID.size();i++) 
		{
			if(!lblProximityID.get(i).getValue().toString().isEmpty() && !chkInactive.get(i).booleanValue())
			{
				Sql="update tbEmployeeInfo set vStatus='Continue', iStatus='1', dStatusDate='"+dateFormat.format(date.getValue())+"' "+
						"where vEmployeeId='"+lblAutoEmployeeID.get(i).getValue().toString()+"'";

				session.createSQLQuery(Sql).executeUpdate();
				session.clear();

				Sql="update tbInactiveEmployee set vActive_Inactive ='Active', " +
						"dActiveDate=getDate() where vEmployeeId='"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
						"and convert(date,dInactiveDate)='"+dbDateFormat.format(date.getValue())+"'";

				session.createSQLQuery(Sql).executeUpdate();
				session.clear();
			}
		}
	}

	private void tableValueAdd(String emp)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String SectionID="%";
			if(cmbSection.getValue()!=null)
				SectionID=cmbSection.getValue().toString();

			String sql="select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vdesignationId,din.designationName," +
					"ein.vDepartmentID,dept.vDepartmentName,ein.vSectionID,sein.sectionName from tbEmployeeInfo ein " +
					"inner join tbDesignationInfo din on ein.vdesignationId=din.designationId inner join " +
					"tbDepartmentInfo dept on ein.vDepartmentID=dept.vDepartmentID inner join tbSectionInfo sein on " +
					"sein.vSectionID=ein.vSectionID where ein.vDepartmentID='"+cmbDepartment.getValue().toString()+"' " +
					"and ein.vSectionId like '"+SectionID+"' and ein.vEmployeeId like '"+emp+"' and ISNULL(vProximityId,'')!='' " +
					"and iStatus=1 order by ein.employeeCode";

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
						{
							tableRowAdd(index+1);
						}

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
		cmbSection.setValue(null);
		chkAllSection.setValue(false);
		cmbEmployee.setValue(null);
		opgEmployee.select(null);
		chkActiveAll.setValue(false);
		chkInactiveAll.setValue(false);
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
			txtPermittedBy.get(i).setValue("");
			txtReason.get(i).setValue("");
			chkInactive.get(i).setValue(false);
			chkActive.get(i).setValue(false);
		}
	}

	private void componentEnable(boolean b)
	{
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		chkAllSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		chkInactiveAll.setEnabled(!b);
		chkActiveAll.setEnabled(!b);
		date.setEnabled(!b);
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

		for(int i=0;i<11;i++)
			tableRowAdd(i);

	}

	public void tableRowAdd(final int ar)
	{
		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
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
				txtPermittedBy.get(ar).setValue("");
				txtReason.get(ar).setValue("");
				chkInactive.get(ar).setValue(false);
				chkActive.get(ar).setValue(false);

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
							txtPermittedBy.get(tbIndex).setValue(txtPermittedBy.get(tbIndex+1).getValue().toString().trim());
							txtReason.get(tbIndex).setValue(txtReason.get(tbIndex+1).getValue().toString().trim());
							chkInactive.get(tbIndex).setValue(chkInactive.get(tbIndex+1).getValue());
							chkActive.get(tbIndex).setValue(chkActive.get(tbIndex+1).getValue());

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
							txtPermittedBy.get(tbIndex+1).setValue("");
							txtReason.get(tbIndex+1).setValue("");
							chkInactive.get(tbIndex+1).setValue(false);
							chkActive.get(tbIndex+1).setValue(false);
						}
					}
				}
			}
		});

		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("100%");
		lblSl.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");

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

		txtPermittedBy.add(ar, new TextField());
		txtPermittedBy.get(ar).setWidth("100%");
		txtPermittedBy.get(ar).setImmediate(true);
		txtPermittedBy.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtPermittedBy.get(ar).getValue().toString().isEmpty())
					{
						txtReason.get(ar).setValue("");
						txtReason.get(ar).focus();
					}
				}
			}
		});

		txtReason.add(ar, new TextField());
		txtReason.get(ar).setWidth("100%");
		txtReason.get(ar).setImmediate(true);
		txtReason.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtReason.get(ar).getValue().toString().isEmpty())
					{
						if(!lblAutoEmployeeID.get(ar+1).getValue().toString().isEmpty())
						{
							txtPermittedBy.get(ar+1).setValue("");
							txtPermittedBy.get(ar+1).focus();
						}
					}
				}
			}
		});

		chkInactive.add(ar, new CheckBox());
		chkInactive.get(ar).setImmediate(true);
		chkInactive.get(ar).setWidth("100%");
		chkInactive.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkInactive.get(ar).booleanValue())
				{
					chkActive.get(ar).setValue(false);
					chkInactive.get(ar).setValue(true);
				}
				else
				{
					chkActive.get(ar).setValue(true);
					chkInactive.get(ar).setValue(false);
				}
			}
		});

		chkActive.add(ar, new CheckBox());
		chkActive.get(ar).setImmediate(true);
		chkActive.get(ar).setWidth("100%");
		chkActive.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkActive.get(ar).booleanValue())
				{
					chkInactive.get(ar).setValue(false);
					chkActive.get(ar).setValue(true);
				}
				else
				{
					chkInactive.get(ar).setValue(true);
					chkActive.get(ar).setValue(false);
				}
			}
		});

		table.addItem(new Object[]{btnDel.get(ar),lblSl.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),lblProximityID.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),lblDepartmentID.get(ar),
				lblDepartmentName.get(ar),lblSectionID.get(ar),lblSectionName.get(ar),txtPermittedBy.get(ar),txtReason.get(ar),
				chkInactive.get(ar),chkActive.get(ar)}, ar);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1085.0px");
		mainlayout.setHeight("530.0px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("290.0px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department Name : "), "top:05.0px; left:30.0px;");
		mainlayout.addComponent(cmbDepartment, "top:03.0px; left:145.0px;");

		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section Name : "), "top:35.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:33.0px;left:145.0px");

		chkAllSection=new CheckBox("All");
		chkAllSection.setImmediate(true);
		mainlayout.addComponent(chkAllSection, "top:35.0px; left:440.0px;");

		date=new PopupDateField();
		date.setImmediate(true);
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yyyy");
		date.setValue(new Date());
		mainlayout.addComponent(new Label("Date : "), "top:65.0px;left:30.0px;");
		mainlayout.addComponent(date, "top:63.0px;left:145.0px;");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainlayout.addComponent(opgEmployee, "top:35.0px;left:555.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee : "), "top:65.0px;left:465.0px;");
		mainlayout.addComponent(cmbEmployee, "top:63.0px;left:555.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:65.0px;left:850.0px");

		chkInactiveAll=new CheckBox("Inactive All");
		chkInactiveAll.setImmediate(true);
		mainlayout.addComponent(chkInactiveAll, "top:35.0px;left:990.0px");

		chkActiveAll=new CheckBox("Active All");
		chkActiveAll.setImmediate(true);
		mainlayout.addComponent(chkActiveAll, "top:65.0px;left:990.0px");

		table.setWidth("98%");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Emp ID", Label.class, new Label());
		table.setColumnWidth("Emp ID", 70);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",100);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity ID", 100);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 190);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 180);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 30);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 180);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 180);

		table.addContainerProperty("Permitted By", TextField.class, new TextField());
		table.setColumnWidth("Permitted By", 180);

		table.addContainerProperty("Reason", TextField.class, new TextField());
		table.setColumnWidth("Reason", 180);

		table.addContainerProperty("Inactive", CheckBox.class, new CheckBox());
		table.setColumnWidth("Inactive", 35);

		table.addContainerProperty("Active", CheckBox.class, new CheckBox());
		table.setColumnWidth("Active", 35);

		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Designation", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Department Name", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Section Name", true);
		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:100.0px;left:30.0px");
		tableinitialize();

		mainlayout.addComponent(cButton, "top:490.0px;left:260.0px;");
		return mainlayout;
	}
}
