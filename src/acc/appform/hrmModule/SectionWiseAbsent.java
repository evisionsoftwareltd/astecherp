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
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SectionWiseAbsent extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private CheckBox chkAbsentAll;
	private CheckBox chkPresentAll;
	private PopupDateField date;
	private TextField FindDate=new TextField();
	private TextField txtFindSection=new TextField();
	private TextField txtFindDepartment=new TextField();

	private Table table=new Table();
	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblSl=new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmpID=new ArrayList<Label>();
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
	private ArrayList<CheckBox> chkAbsent=new ArrayList<CheckBox>();
	private ArrayList<CheckBox> chkPresent=new ArrayList<CheckBox>();
	//private ArrayList<CheckBox> chkFriday=new ArrayList<CheckBox>();

	private ArrayList<Component> comMove=new ArrayList<Component>();

	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");

	private SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");

	boolean isSave=false;
	boolean isFind=false;
	boolean isRefresh=false;
	boolean isUpdate=false;
	int index=0;

	public SectionWiseAbsent(SessionBean sessionBean)
	{
		this.sessionbean=sessionBean;
		this.setCaption("SECTION WISE ABSENT :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbDepartmentDataLoad();
		focusMoveByEnter();
	}

	private void focusMoveByEnter()
	{
		for(int i=0;i<lblProximityID.size();i++)
			comMove.add(txtReason.get(i));
		comMove.add(cButton.btnSave);
	}

	private void cmbDepartmentDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					   "tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId and dept.vDepartmentId!='DEPT10' order by dept.vDepartmentName";
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
					   "tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where ein.vDepartmentID='"+cmbDepartment.getValue()+"' " +
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

	private void setEventAction()
	{
		chkAbsentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAbsentAll.booleanValue())
				{
					chkPresentAll.setValue(false);
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblAutoEmpID.get(i).getValue().toString().isEmpty())
						{
							chkAbsent.get(i).setValue(true);
							chkPresent.get(i).setValue(false);
						}
					}
				}
				else
				{
					for(int i=0;i<lblAutoEmpID.size();i++)
					{
						if(!lblAutoEmpID.get(i).getValue().toString().isEmpty())
						{
							chkAbsent.get(i).setValue(false);
						}
					}
				}
			}
		});

		chkPresentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkPresentAll.booleanValue())
				{
					chkAbsentAll.setValue(false);
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							chkPresent.get(i).setValue(true);
							chkAbsent.get(i).setValue(false);
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							chkPresent.get(i).setValue(false);
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
				chkSectionAll.setValue(false);
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
				opgEmployee.setEnabled(true);
				opgEmployee.select(null);
				chkAllEmp.setValue(false);
				chkPresentAll.setValue(false);
				chkAbsentAll.setValue(false);
				cmbEmployee.removeAllItems();
				cmbEmployee.setEnabled(true);
				index=0;
				tableclear();
			}
		});
		
		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				index=0;
				opgEmployee.setEnabled(true);
				opgEmployee.select(null);
				chkAllEmp.setValue(false);
				chkPresentAll.setValue(false);
				chkAbsentAll.setValue(false);
				cmbEmployee.removeAllItems();
				cmbEmployee.setEnabled(true);
				tableclear();
				if(chkSectionAll.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		date.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployee.select(null);
				chkAllEmp.setValue(false);
				chkAbsentAll.setValue(false);
				cmbEmployee.removeAllItems();
				index=0;
				tableclear();
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					String sql="";
					String sectionID="%";
					if(cmbSection.getValue()!=null)
						sectionID=cmbSection.getValue().toString();
						
					if(opgEmployee.getValue()=="Employee ID")
					{
						sql="select vEmployeeID,vEmployeeCode from funDailyEmployeeAttendance('"+dateformat.format(date.getValue())+"','"+dateformat.format(date.getValue())+"','%','"+cmbDepartment.getValue()+"','"+sectionID+"') order by vEmployeeCode";
					}

					else if(opgEmployee.getValue()=="Proximity ID")
					{
						sql="select vEmployeeID,vProximityID from funDailyEmployeeAttendance('"+dateformat.format(date.getValue())+"','"+dateformat.format(date.getValue())+"','%','"+cmbDepartment.getValue()+"','"+sectionID+"') order by vEmployeeCode";
					}

					else if(opgEmployee.getValue()=="Employee Name")
					{
						sql="select vEmployeeID,vEmployeeName from funDailyEmployeeAttendance('"+dateformat.format(date.getValue())+"','"+dateformat.format(date.getValue())+"','%','"+cmbDepartment.getValue()+"','"+sectionID+"') order by vEmployeeCode";
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
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
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
				else
				{
					if(!isSave && !isRefresh)
					{
						chkAllEmp.setValue(false);
						showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isSave=false;
				isRefresh=false;
				isFind=false;
				isUpdate=false;
				newButtonEvent();
				index=0;
				cmbSection.focus();
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(checkSaveValidation())
				{
					isSave=true;
					isRefresh=false;
					isFind=false;
					saveButtonEvent();
				}
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isRefresh=false;
				isUpdate=true;
				updateAction();
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=false;
				isUpdate=false;
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

	private void updateAction() 
	{
		System.out.println("Update");
		if (!lblProximityID.get(0).getValue().toString().isEmpty()) 
		{
			componentEnable(false);
			btnEnable(false);
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void newButtonEvent()
	{
		componentEnable(false);
		btnEnable(false);
		txtclear();
	}

	private void findButtonEvent() 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vEmployeeCode from tbAbsentAsPunishment";
			List <?> lst=session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{
				Window win = new SectionWiseAbsentFind(sessionbean,txtFindDepartment,txtFindSection,FindDate,"SectionWiseAbsent");
				win.addListener(new Window.CloseListener() 
				{
					public void windowClose(CloseEvent e) 
					{
						if (txtFindSection.getValue().toString().length() > 0)
						{
							txtclear();
							findInitialise(txtFindDepartment.getValue().toString(),txtFindSection.getValue().toString().trim(),FindDate.getValue().toString());
						}
					}
				});

				this.getParent().addWindow(win);
			}
			else
				showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("findButtonEvent", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void findInitialise(String txtDeptID,String txtSecID,String Date) 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query="select distinct tap.dDate,tap.vEmployeeID,tap.vEmployeeCode,tap.vProximityID,tei.vEmployeeName," +
					"tap.vDesignationID,tdi.designationName,tap.vDepartmentID,dept.vDepartmentName,tap.vSectionID," +
					"tsi.SectionName,tap.vReason,tap.iAbsentFlag,tap.vPermittedBy from tbAbsentAsPunishment tap inner " +
					"join tbSectionInfo tsi on tap.vSectionID=tsi.vSectionID inner join tbDesignationInfo tdi on " +
					"tdi.designationId=tap.vDesignationID inner join tbEmployeeInfo tei on tei.vEmployeeId=tap.vEmployeeID " +
					"inner join tbDepartmentInfo dept on dept.vDepartmentID=tap.vDepartmentID where tap.vDepartmentID='"+txtDeptID+"' " +
					"and tap.vSectionID='"+txtSecID+"' and dDate='"+Date+"' order by tap.vEmployeeCode";
			List <?> led = session.createSQLQuery(query).list();
			int absIndex=0;

			for(Iterator <?> itr=led.iterator();itr.hasNext();) 
			{
				Object[] element = (Object[]) itr.next();
				if(absIndex==0)
				{
					cmbDepartment.setValue(element[7]);
					cmbSection.setValue(element[9]);
					date.setValue(element[0]);
				}
				lblAutoEmpID.get(absIndex).setValue(element[1].toString());
				lblEmployeeID.get(absIndex).setValue(element[2].toString());
				lblProximityID.get(absIndex).setValue(element[3].toString());
				lblEmployeeName.get(absIndex).setValue(element[4].toString());
				lblDesignationID.get(absIndex).setValue(element[5].toString());
				lblDesignation.get(absIndex).setValue(element[6].toString());
				lblDepartmentID.get(absIndex).setValue(element[7].toString());
				lblDepartmentName.get(absIndex).setValue(element[8].toString());
				lblSectionID.get(absIndex).setValue(element[9].toString());
				lblSectionName.get(absIndex).setValue(element[10].toString());
				txtReason.get(absIndex).setValue(element[11].toString());
				txtPermittedBy.get(absIndex).setValue(element[13].toString());
				if(element[12].toString().equals("1"))
					chkAbsent.get(absIndex).setValue(true);
				else
					chkPresent.get(absIndex).setValue(true);

				if(absIndex==lblProximityID.size()-1)
				{
					tableRowAdd(absIndex+1);
				}

				absIndex++;
			}
			componentEnable(true);
		}
		catch (Exception exp) 
		{
			showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private boolean checkSaveValidation()
	{
		boolean ret=false;
		for(int proIndex=0; proIndex<lblProximityID.size(); proIndex++)
		{
			if(!lblProximityID.get(proIndex).getValue().toString().trim().isEmpty())
			{
				if(!txtReason.get(proIndex).getValue().toString().trim().isEmpty())
				{
					if(!txtPermittedBy.get(proIndex).getValue().toString().trim().isEmpty())
					{
						if(!isFind)
						{
							if(chkAbsent.get(proIndex).booleanValue())
							{
								ret=true;
							}
							else
							{
								showNotification("Warning", "Please Check Absent!!!", Notification.TYPE_WARNING_MESSAGE);
								txtPermittedBy.get(proIndex).focus();
								ret=false;
								break;
							}
						}
						else
							ret=true;
					}
					else
					{
						showNotification("Warning", "Please Provide PermittedBy!!!", Notification.TYPE_WARNING_MESSAGE);
						txtPermittedBy.get(proIndex).focus();
						ret=false;
						break;
					}
				}
				else
				{
					showNotification("Warning", "Please Enter Reason!!!", Notification.TYPE_WARNING_MESSAGE);
					txtReason.get(proIndex).focus();
					ret=false;
					break;
				}
			}
			else
			{
				if(proIndex==0)
					showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);
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
						Updatedata();
						txtclear();
						componentEnable(true);
						btnEnable(true);
					}
				}
			});
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
						insertdata();
						txtclear();
						componentEnable(true);
						btnEnable(true);
					}
				}
			});
		}
	}

	private void Updatedata()
	{
		String sql="";
		String udSql="";
		String absDate=dateformat.format(date.getValue());
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			for(int i=0;i<lblAutoEmpID.size();i++) 
			{
				if(!lblAutoEmpID.get(i).getValue().toString().isEmpty())
				{
					udSql="insert into tbUDAbsentAsPunishment select dDate,vEmployeeID,vEmployeeCode,vProximityID,vDesignationID,vSectionID,vReason,iAbsentFlag," +
							"userName,userIP,dEntryTime,vPermittedBy,'New',vDepartmentID from tbAbsentAsPunishment where " +
							"dDate='"+absDate+"' and vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"'";
					session.createSQLQuery(udSql).executeUpdate();

					sql="update tbAbsentAsPunishment set iAbsentFlag='"+(chkAbsent.get(i).booleanValue()?1:0)+"'," +
							"userName='"+sessionbean.getUserName()+"',userIP='"+sessionbean.getUserIp()+"'," +
							"dEntryTime=getdate(),vPermittedBy='"+txtPermittedBy.get(i).getValue().toString().trim()+"' " +
							"where dDate='"+absDate+"' and vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"'";
					session.createSQLQuery(sql).executeUpdate();

					udSql="insert into tbUDAbsentAsPunishment select dDate,vEmployeeID,vEmployeeCode,vProximityID,vDesignationID,vSectionID,vReason,iAbsentFlag," +
							"userName,userIP,dEntryTime,vPermittedBy,'Update',vDepartmentID from tbAbsentAsPunishment where " +
							"dDate='"+absDate+"' and vEmployeeID='"+lblAutoEmpID.get(i).getValue().toString()+"'";
					session.createSQLQuery(udSql).executeUpdate();
				}
			}
			showNotification("All Information Updated Successfully");
			isUpdate=false;
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("UpdteData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void insertdata()
	{
		String absDate=dateformat.format(date.getValue());
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			for(int i=0;i<lblProximityID.size();i++) 
			{
				if(!lblProximityID.get(i).getValue().toString().isEmpty())
				{
					if(chkAbsent.get(i).booleanValue())
					{
						String sql="insert into tbAbsentAsPunishment (dDate,vEmployeeID,vEmployeeCode,vProximityID," +
							"vDesignationID,vSectionID,vReason,iAbsentFlag,userName,userIP,dEntryTime,vPermittedBy," +
							"vDepartmentID) " +
							"values('"+absDate+"','"+lblAutoEmpID.get(i).getValue().toString().trim()+"'," +
							"'"+lblEmployeeID.get(i).getValue().toString()+"'," +
							"'"+lblProximityID.get(i).getValue().toString()+"','"+lblDesignationID.get(i).getValue().toString()+"'," +
							"'"+lblSectionID.get(i).getValue().toString()+"','"+txtReason.get(i).getValue().toString().trim()+"'," +
							"'" +(chkAbsent.get(i).booleanValue()?1:0)+"','"+sessionbean.getUserName()+"'," +
							"'"+sessionbean.getUserIp()+"',getdate(),'"+txtPermittedBy.get(i).getValue().toString().trim()+"'," +
							"'"+cmbDepartment.getValue()+"')";
						session.createSQLQuery(sql).executeUpdate();
					}

				}
			}
			showNotification("All Information Saved Successfully");
			isSave=false;
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("InsertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally
		{
			session.close();
		}
	}

	private void tableValueAdd(String emp)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String SectionID="";
			if(chkSectionAll.booleanValue())
			{
				SectionID="%";
			}
			else if(cmbSection.getValue()!=null)
			{
				  SectionID=cmbSection.getValue().toString();
			}
			
			String sql="select distinct fun.vEmployeeID,fun.vEmployeeCode,fun.vProximityID,fun.vEmployeeName,ein.vdesignationId," +
					"fun.vDesignationName,ein.vDepartmentID,fun.vDepartmentName,ein.vSectionID,fun.vSectionName " +
					"from tbEmployeeInfo ein inner join " +
					"funDailyEmployeeAttendance('"+dateformat.format(date.getValue())+"'," +
					"'"+dateformat.format(date.getValue())+"','%','"+cmbDepartment.getValue()+"'," +
					"'"+SectionID+"') fun " +
					"on ein.vEmployeeId=fun.vEmployeeID where fun.vSectionId like '"+SectionID+"' " +
					"and fun.vEmployeeID like '"+emp+"' order by fun.vEmployeeCode";

			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				int chkExists=0;
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<lblEmployeeID.size();chkindex++)
					{
						if(lblProximityID.get(chkindex).getValue().equals(element[2].toString()))
						{
							check=true;
							break;
						}
					}

					if(!check)
					{
						chkExists=1;
						lblAutoEmpID.get(index).setValue(element[0].toString());
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
					else
						chkExists=0;
				}
				if(chkExists==0)
					showNotification("Warning", "Employee is already Found in the list!!!", Notification.TYPE_WARNING_MESSAGE);
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
		chkSectionAll.setValue(false);
		cmbEmployee.setValue(null);
		chkAllEmp.setValue(false);		
		opgEmployee.select(null);
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0;i<lblEmployeeID.size();i++)
		{
			lblAutoEmpID.get(i).setValue("");
			lblEmployeeID.get(i).setValue("");
			lblProximityID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			txtReason.get(i).setValue("");
			txtPermittedBy.get(i).setValue("");
			chkPresent.get(i).setValue(false);
			chkAbsent.get(i).setValue(false);
		}
	}

	private void componentEnable(boolean b)
	{
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		chkPresentAll.setEnabled(!b);
		chkAbsentAll.setEnabled(!b);
		date.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnEnable(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnEdit.setEnabled(b);
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
		btnDel.add(ar, new NativeButton(""));
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				lblAutoEmpID.get(ar).setValue("");
				lblEmployeeID.get(ar).setValue("");
				lblProximityID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignationID.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblDepartmentID.get(ar).setValue("");
				lblDepartmentName.get(ar).setValue("");
				lblSectionID.get(ar).setValue("");
				lblSectionName.get(ar).setValue("");
				txtReason.get(ar).setValue("");
				txtPermittedBy.get(ar).setValue("");
				chkAbsent.get(ar).setValue(false);
				chkPresent.get(ar).setValue(false);

				for(int rowcount=ar;rowcount<=lblProximityID.size()-1;rowcount++)
				{
					if(rowcount+1<=lblProximityID.size()-1)
					{
						if(!lblProximityID.get(rowcount+1).getValue().toString().equals(""))
						{
							lblAutoEmpID.get(rowcount).setValue(lblAutoEmpID.get(rowcount+1).getValue().toString());
							lblEmployeeID.get(rowcount).setValue(lblEmployeeID.get(rowcount+1).getValue().toString());
							lblProximityID.get(rowcount).setValue(lblProximityID.get(rowcount+1).getValue().toString());
							lblEmployeeName.get(rowcount).setValue(lblEmployeeName.get(rowcount+1).getValue().toString());
							lblDesignationID.get(rowcount).setValue(lblDesignationID.get(rowcount+1).getValue().toString());
							lblDesignation.get(rowcount).setValue(lblDesignation.get(rowcount+1).getValue().toString());
							lblDepartmentID.get(rowcount).setValue(lblDepartmentID.get(rowcount+1).getValue().toString());
							lblDepartmentName.get(rowcount).setValue(lblDepartmentName.get(rowcount+1).getValue().toString());
							lblSectionID.get(rowcount).setValue(lblSectionID.get(rowcount+1).getValue().toString());
							lblSectionName.get(rowcount).setValue(lblSectionName.get(rowcount+1).getValue().toString());
							txtReason.get(rowcount).setValue(txtReason.get(rowcount+1).getValue().toString().trim());
							txtPermittedBy.get(rowcount).setValue(txtPermittedBy.get(rowcount+1).getValue().toString().trim());
							chkAbsent.get(rowcount).setValue(chkAbsent.get(rowcount+1).getValue());
							chkPresent.get(rowcount).setValue(chkPresent.get(rowcount+1).getValue());

							lblAutoEmpID.get(rowcount+1).setValue("");
							lblEmployeeID.get(rowcount+1).setValue("");
							lblProximityID.get(rowcount+1).setValue("");
							lblEmployeeName.get(rowcount+1).setValue("");
							lblDesignationID.get(rowcount+1).setValue("");
							lblDesignation.get(rowcount+1).setValue("");
							lblDepartmentID.get(rowcount+1).setValue("");
							lblDepartmentName.get(rowcount+1).setValue("");
							lblSectionID.get(rowcount+1).setValue("");
							lblSectionName.get(rowcount+1).setValue("");
							txtReason.get(rowcount+1).setValue("");
							txtPermittedBy.get(rowcount+1).setValue("");
							chkAbsent.get(rowcount+1).setValue(false);
							chkPresent.get(rowcount+1).setValue(false);
						}
					}
				}
				index=index-1;
			}
		});

		lblSl.add(ar, new Label(""));
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("20px");
		lblSl.get(ar).setImmediate(true);
		lblSl.get(ar).setValue(ar+1);

		lblAutoEmpID.add(ar, new Label());
		lblAutoEmpID.get(ar).setWidth("100%");
		lblAutoEmpID.get(ar).setHeight("20px");
		
		lblEmployeeID.add(ar, new Label(""));
		lblEmployeeID.get(ar).setWidth("100%");
		lblEmployeeID.get(ar).setImmediate(true);

		lblProximityID.add(ar, new Label(""));
		lblProximityID.get(ar).setWidth("100%");
		lblProximityID.get(ar).setImmediate(true);

		lblEmployeeName.add(ar, new Label(""));
		lblEmployeeName.get(ar).setWidth("100%");
		lblEmployeeName.get(ar).setImmediate(true);

		lblDesignationID.add(ar, new Label(""));
		lblDesignationID.get(ar).setWidth("100%");
		lblDesignationID.get(ar).setImmediate(true);

		lblDesignation.add(ar, new Label(""));
		lblDesignation.get(ar).setWidth("100%");
		lblDesignation.get(ar).setImmediate(true);

		lblDepartmentID.add(ar, new Label(""));
		lblDepartmentID.get(ar).setWidth("100%");
		lblDepartmentID.get(ar).setImmediate(true);

		lblDepartmentName.add(ar, new Label(""));
		lblDepartmentName.get(ar).setWidth("100%");
		lblDepartmentName.get(ar).setImmediate(true);

		lblSectionID.add(ar, new Label(""));
		lblSectionID.get(ar).setWidth("100%");
		lblSectionID.get(ar).setImmediate(true);

		lblSectionName.add(ar, new Label(""));
		lblSectionName.get(ar).setWidth("100%");
		lblSectionName.get(ar).setImmediate(true);

		txtReason.add(ar, new TextField(""));
		txtReason.get(ar).setWidth("100%");
		txtReason.get(ar).setImmediate(true);

		txtPermittedBy.add(ar, new TextField(""));
		txtPermittedBy.get(ar).setWidth("100%");
		txtPermittedBy.get(ar).setImmediate(true);

		chkAbsent.add(ar, new CheckBox());
		chkAbsent.get(ar).setWidth("100%");
		chkAbsent.get(ar).setImmediate(true);
		chkAbsent.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(lblProximityID.get(ar).getValue().toString().isEmpty())
				{
					chkAbsent.get(ar).setValue(false);
				}
				else
				{
					if(chkAbsent.get(ar).booleanValue())
					{
						chkAbsent.get(ar).setValue(true);
						chkPresent.get(ar).setValue(false);
					}
					else
						chkAbsent.get(ar).setValue(false);
				}
			}
		});

		chkPresent.add(ar, new CheckBox());
		chkPresent.get(ar).setWidth("100%");
		chkPresent.get(ar).setImmediate(true);
		chkPresent.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(lblProximityID.get(ar).getValue().toString().isEmpty())
				{
					chkPresent.get(ar).setValue(false);
				}
				else
				{
					if(chkPresent.get(ar).booleanValue())
					{
						chkPresent.get(ar).setValue(true);
						chkAbsent.get(ar).setValue(false);
					}
					else
						chkPresent.get(ar).setValue(false);
				}
			}
		});

		table.addItem(new Object[]{btnDel.get(ar),lblSl.get(ar),lblAutoEmpID.get(ar),lblEmployeeID.get(ar),
				lblProximityID.get(ar),lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),
				lblDepartmentID.get(ar),lblDepartmentName.get(ar),lblSectionID.get(ar),lblSectionName.get(ar),
				txtReason.get(ar),txtPermittedBy.get(ar),chkPresent.get(ar),chkAbsent.get(ar)}, ar);
	}

	public AbsoluteLayout buildMainLayout()
	{

		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1150.0px");
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
		mainlayout.addComponent(new Label("Section Name : "), "top:35.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:33.0px;left:140.0px");

		chkSectionAll=new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainlayout.addComponent(chkSectionAll, "top:35.0px;left:435.0px;");
		
		date=new PopupDateField();
		date.setDateFormat("dd-MM-yyyy");
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setImmediate(true);
		date.setValue(new Date());
		mainlayout.addComponent(new Label("Date : "), "top:60.0px;left:30.0px;");
		mainlayout.addComponent(date, "top:58.0px;left:140.0px;");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainlayout.addComponent(opgEmployee, "top:35.0px;left:600.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee : "), "top:60.0px;left:520.0px;");
		mainlayout.addComponent(cmbEmployee, "top:58.0px;left:600.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:60.0px;left:890.0px");

		chkPresentAll=new CheckBox("Present All");
		chkPresentAll.setImmediate(true);
		mainlayout.addComponent(chkPresentAll, "top:35.0px;left:1023.0px;");

		chkAbsentAll=new CheckBox("Absent All");
		chkAbsentAll.setImmediate(true);
		mainlayout.addComponent(chkAbsentAll, "top:60.0px;left:1023.0px");

		table.setWidth("1090.0px");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);
		table.setPageLength(0);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 80);
		
		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",100);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity ID", 100);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 160);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 160);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 30);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 180);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 180);

		table.addContainerProperty("Reason", TextField.class, new TextField());
		table.setColumnWidth("Reason", 170);

		table.addContainerProperty("Permitted By", TextField.class, new TextField());
		table.setColumnWidth("Permitted By", 140);

		table.addContainerProperty("Pre", CheckBox.class, new CheckBox());
		table.setColumnWidth("Pre", 20);

		table.addContainerProperty("Abs", CheckBox.class, new CheckBox());
		table.setColumnWidth("Abs", 20);

		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Department Name", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Section Name", true);
		table.setColumnCollapsed("EMP ID", true);
		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:100.0px;left:30.0px");
		tableinitialize();

		mainlayout.addComponent(cButton, "top:480.0px;left:330.0px;");
		return mainlayout;
	}
}
