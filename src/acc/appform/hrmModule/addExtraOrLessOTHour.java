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

import com.common.share.AmountCommaSeperator;
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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
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
public class addExtraOrLessOTHour extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private PopupDateField date;

	private TextRead txtDepartmentId = new TextRead();
	private TextRead txtSectionId = new TextRead();
	private TextRead txtFindDate = new TextRead();

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
	private ArrayList<AmountCommaSeperator> txtExtraOT=new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> txtLessOT=new ArrayList<AmountCommaSeperator>();

	private DecimalFormat intFormat=new DecimalFormat("#,##0");
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat MonthFormat=new SimpleDateFormat("MMMMM-yyyy");
	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");

	boolean isSave=false;
	boolean isUpdate=false;
	boolean isRefresh=false;
	boolean isFind=false;
	int index=0;
	String Noti="";

	public addExtraOrLessOTHour(SessionBean sessionBean)
	{

		this.sessionbean=sessionBean;
		this.setCaption("ADD/LESS EXTRA OT HOUR :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbDepartmentDataLoad();
		//addDataFindData();
	}

	private void cmbDepartmentDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct ein.vDepartmentID,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
					"tbEmployeeInfo ein on dept.vDepartmentID=ein.vDepartmentID where ein.iStatus=1 and OtStatus=1 and ISNULL(ein.vProximityID,'')!='' and dept.vDepartmentID!='DEPT10' " +
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
		finally{session.close();}
	}

	private void cmbSectionDataLoad()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct ein.vSectionId,sein.SectionName from tbSectionInfo sein inner join " +
					"tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where " +
					"ein.vDepartmentID='"+cmbDepartment.getValue()+"' and ein.iStatus=1 and OtStatus=1 and " +
					"ISNULL(ein.vProximityID,'')!='' order by sein.SectionName";
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

		date.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkAllEmp.setValue(false);
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				cmbEmployee.setValue(null);
				opgEmployee.select(null);
				chkAllEmp.setValue(false);
				tableclear();
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
						sql="select vEmployeeId,employeeCode from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"' and iStatus=1 and OtStatus=1 and ISNULL(vProximityId,'')!='' order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Proximity ID")
					{
						sql="select vEmployeeId,vProximityId from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"' and iStatus=1 and OtStatus=1 and ISNULL(vProximityId,'')!='' order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Employee Name")
					{
						sql="select vEmployeeId,vEmployeeName from tbEmployeeInfo where vSectionId='"+cmbSection.getValue().toString()+"' and iStatus=1 and OtStatus=1 and ISNULL(vProximityId,'')!='' order by employeeCode";
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
				if(cmbEmployee.getValue()!=null)
					tableValueAdd(cmbEmployee.getValue().toString());
			}
		});

		chkAllEmp.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null)
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
				if(chkTableData())
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
		Window win = new addExtraOrLessOTHourFind(sessionbean,txtDepartmentId,txtSectionId,txtFindDate);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtSectionId.getValue().toString().length() > 0)
				{
					txtclear();
					findInitialise(txtDepartmentId.getValue().toString(),txtSectionId.getValue().toString(), txtFindDate.getValue());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String DepartmentID,String SectionId, Object findDate) 
	{
		String sql = "";
		System.out.println("ID: "+SectionId+" Date: "+findDate);
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			System.out.println("K000");
			sql = "select dDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,vSectionID,vSectionName,vDesignationID,vDesignationName," +
					"vPermittedBy,vReason,iExtraOTAdd,iLessOT,vDepartmentID,vDepartmentName from tbAddOrLessExtraOT where Month(dDate) = Month('"+findDate+"') and YEAR(dDate)=YEAR('"+findDate+"')" +
					" and vDepartmentID='"+DepartmentID+"' and vSectionID='"+SectionId+"'  order by vEmployeeCode";
			List <?> list = session.createSQLQuery(sql).list();

			isFind=true;

			int i=0;
			for(Iterator <?> itr=list.iterator();itr.hasNext();)
			{
				Object[] element = (Object[]) itr.next();

				if(i==0)
				{
					date.setValue(element[0]);
					cmbDepartment.setValue(element[13]);
					cmbSection.setValue(element[5]);
				}

				lblAutoEmployeeID.get(i).setValue(element[1]);
				lblEmployeeID.get(i).setValue(element[2]);
				lblProximityID.get(i).setValue(element[3]);
				lblEmployeeName.get(i).setValue(element[4]);

				lblDepartmentID.get(i).setValue(element[13]);
				lblDepartmentName.get(i).setValue(element[14]);
				lblSectionID.get(i).setValue(element[5]);
				lblSectionName.get(i).setValue(element[6]);
				lblDesignationID.get(i).setValue(element[7]);
				lblDesignation.get(i).setValue(element[8]);
				txtPermittedBy.get(i).setValue(element[9]);
				txtReason.get(i).setValue(element[10]);
				txtExtraOT.get(i).setValue(intFormat.format(element[11]));
				txtLessOT.get(i).setValue(intFormat.format(element[12]));
				
				if(i==lblEmployeeID.size()-1)
					tableRowAdd(i+1);
				i++;
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		isFind=false;
	}

	private boolean chkTableData()
	{
		boolean ret=false;
		for(int tbin=0;tbin<lblAutoEmployeeID.size();tbin++)
		{
			if(!lblAutoEmployeeID.get(tbin).getValue().toString().trim().isEmpty())
			{
				if(!txtPermittedBy.get(tbin).getValue().toString().trim().isEmpty())
				{
					ret=false;
					if(!txtReason.get(tbin).getValue().toString().trim().isEmpty())
					{
						if(!txtExtraOT.get(tbin).getValue().toString().trim().isEmpty() || !txtLessOT.get(tbin).getValue().toString().trim().isEmpty())
						{
							ret=true;
							break;
						}
						else
						{
							Noti="Provide Extra or Less OT!!!";
						}
					}
					else
					{
						txtReason.get(tbin).focus();
						Noti="Provide Reason!!!";
					}
				}
				else
				{
					txtPermittedBy.get(tbin).focus();
					Noti="Provide Permitted By!!!";
				}
			}
			else
			{
				Noti="No Data Found!!!";
			}
		}
		return ret;
	}

	private boolean chkSalary(String chkQuery)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst1=session.createSQLQuery(chkQuery).list();
			if(!lst1.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("btnSave.addListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return false;
	}

	private void saveButtonEvent()
	{
		if(isUpdate)
		{
			String query1 = "select * from tbSalary where vDepartmentID='"+cmbDepartment.getValue()+"' and SectionID='"+cmbSection.getValue()+"' " +
					"and MONTH(dDate)=MONTH('"+dateFormat.format(date.getValue())+"') " +
					"and YEAR(dDate)=YEAR('"+dateFormat.format(date.getValue())+"')";

			if(!chkSalary(query1))
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
								if(deleteData(session))
									insertdata(session);
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
							finally{session.close();}
						}
					}
				});
				isUpdate=false;
			}
			else
			{
				showNotification("Warning", "Salary Already Generated for the Month of "+MonthFormat.format(date.getValue())+"!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			String query1 = "select * from tbSalary where vDepartmentID='"+cmbDepartment.getValue()+"' and SectionID='"+cmbSection.getValue()+"' " +
					"and MONTH(dDate)=MONTH('"+dateFormat.format(date.getValue())+"') " +
					"and YEAR(dDate)=YEAR('"+dateFormat.format(date.getValue())+"')";

			if(!chkSalary(query1))
			{
				String query = "select * from tbAddOrLessExtraOT where vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionID='"+cmbSection.getValue()+"' " +
						"and MONTH(dDate)=MONTH('"+dateFormat.format(date.getValue())+"') " +
						"and YEAR(dDate)=YEAR('"+dateFormat.format(date.getValue())+"')";

				if(!chkSalary(query))
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
								finally{session.close();}
							}
						}
					});
				}
				else
				{
					showNotification("Warning", "Data Already Exist!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning", "Salary Already Generated for the Month of "+MonthFormat.format(date.getValue())+"!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
	}

	private boolean deleteData(Session session)
	{
		session.createSQLQuery("delete from tbAddOrLessExtraOT where Month(dDate)=MONTH('"+dateFormat.format(date.getValue())+"')" +
				" and Year(dDate)=YEAR('"+dateFormat.format(date.getValue())+"') and vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionID='"+cmbSection.getValue()+"'").executeUpdate();
		return true;
	}

	private void insertdata(Session session)
	{
		String Sql="";
		for(int i=0;i<lblProximityID.size();i++) 
		{
			if(!lblProximityID.get(i).getValue().toString().isEmpty())
			{
				if(!txtPermittedBy.get(i).getValue().toString().isEmpty() && !txtPermittedBy.get(i).getValue().toString().isEmpty()
						&& (!txtExtraOT.get(i).getValue().toString().isEmpty() || !txtLessOT.get(i).getValue().toString().isEmpty()))
				{
					Sql="insert into tbAddOrLessExtraOT (dDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName,vDepartmentID,vDepartmentName,vSectionID,vSectionName,vDesignationID,vDesignationName," +
							"vPermittedBy,vReason,iExtraOTAdd,iLessOT,vUserName,vUserIP,dEntryTime) values ('"+dateFormat.format(date.getValue())+"'," +
							"'"+lblAutoEmployeeID.get(i).getValue().toString()+"','"+lblEmployeeID.get(i).getValue().toString()+"','"+lblProximityID.get(i).getValue().toString()+"'," +
							"'"+lblEmployeeName.get(i).getValue().toString()+"','"+lblDepartmentID.get(i).getValue().toString()+"'," +
							"'"+lblDepartmentName.get(i).getValue().toString()+"','"+lblSectionID.get(i).getValue().toString()+"'," +
							"'"+lblSectionName.get(i).getValue().toString()+"','"+lblDesignationID.get(i).getValue().toString()+"'," +
							"'"+lblDesignation.get(i).getValue().toString()+"','"+txtPermittedBy.get(i).getValue()+"','"+txtReason.get(i).getValue()+"'," +
							"'"+(txtExtraOT.get(i).getValue().toString().trim().replaceAll(",", "").isEmpty()?"0":txtExtraOT.get(i).getValue().toString().trim().replaceAll(",", ""))+"'," +
							"'"+(txtLessOT.get(i).getValue().toString().trim().replaceAll(",", "").isEmpty()?"0":txtLessOT.get(i).getValue().toString().trim().replaceAll(",", ""))+"'," +
							"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',getdate())";
					session.createSQLQuery(Sql).executeUpdate();
				}
			}
		}
	}

	private void tableValueAdd(String emp)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select vEmployeeId,employeeCode,vProximityId,vEmployeeName,vdesignationId,(select designationName from " +
					"tbDesignationInfo where designationId=ein.vdesignationId) designationName,vSectionID," +
					"(select SectionName from tbSectionInfo where vSectionId=ein.vSectionId) sectionName,vDepartmentID," +
					"(select vDepartmentName from tbDepartmentInfo where vDepartmentId=ein.vDepartmentId) DepartmentName " +
					"from tbEmployeeInfo ein where ein.vDepartmentID='"+cmbDepartment.getValue()+"' and ein.vSectionId='"+cmbSection.getValue().toString()+"' " +
					"and vEmployeeId like '"+emp+"' and ISNULL(vProximityId,'')!='' and OtStatus=1 and iStatus=1 order by employeeCode";

			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				boolean checkData=false;
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					boolean check=false;
					for(int chkindex=0;chkindex<lblAutoEmployeeID.size();chkindex++)
					{
						if(lblAutoEmployeeID.get(chkindex).getValue().toString().equalsIgnoreCase(element[0].toString()))
						{
							check=true;
							index=chkindex;
							break;
						}
						else if(lblAutoEmployeeID.get(chkindex).getValue().toString().isEmpty())
						{
							check=false;
							index=chkindex;
							break;
						}
						else
						{
							check=true;
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
						lblDepartmentID.get(index).setValue(element[8].toString());
						lblDepartmentName.get(index).setValue(element[9].toString());
						lblSectionID.get(index).setValue(element[6].toString());
						lblSectionName.get(index).setValue(element[7].toString());

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
		finally{session.close();}
	}

	private void txtclear()
	{
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		opgEmployee.select(null);
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
			txtExtraOT.get(i).setValue("");
			txtLessOT.get(i).setValue("");
			txtPermittedBy.get(i).setValue("");
			txtReason.get(i).setValue("");
		}
	}

	private void componentEnable(boolean b)
	{
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		date.setEnabled(!b);
		//table.setEnabled(!b);
		for(int i=0;i<lblAutoEmployeeID.size();i++)
		{
			lblAutoEmployeeID.get(i).setEnabled(!b);
			lblEmployeeID.get(i).setEnabled(!b);
			lblProximityID.get(i).setEnabled(!b);
			lblEmployeeName.get(i).setEnabled(!b);
			lblDesignationID.get(i).setEnabled(!b);
			lblDesignation.get(i).setEnabled(!b);
			lblDepartmentID.get(i).setEnabled(!b);
			lblDepartmentName.get(i).setEnabled(!b);
			lblSectionID.get(i).setEnabled(!b);
			lblSectionName.get(i).setEnabled(!b);
			txtExtraOT.get(i).setEnabled(!b);
			txtLessOT.get(i).setEnabled(!b);
			txtPermittedBy.get(i).setEnabled(!b);
			txtReason.get(i).setEnabled(!b);
		}
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
		for(int i=0;i<20;i++)
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
				txtExtraOT.get(ar).setValue("");
				txtLessOT.get(ar).setValue("");
				txtPermittedBy.get(ar).setValue("");
				txtReason.get(ar).setValue("");

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
							txtExtraOT.get(tbIndex).setValue(txtExtraOT.get(tbIndex+1).getValue().toString().trim());
							txtLessOT.get(tbIndex).setValue(txtLessOT.get(tbIndex+1).getValue().toString().trim());
							txtPermittedBy.get(tbIndex).setValue(txtPermittedBy.get(tbIndex+1).getValue().toString().trim());
							txtReason.get(tbIndex).setValue(txtReason.get(tbIndex+1).getValue().toString().trim());

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
							txtExtraOT.get(tbIndex+1).setValue("");
							txtLessOT.get(tbIndex+1).setValue("");
							txtPermittedBy.get(tbIndex+1).setValue("");
							txtReason.get(tbIndex+1).setValue("");
							index--;
						}
					}
				}
			}
		});

		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");

		lblProximityID.add(ar, new Label());
		lblProximityID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar, new Label());
		lblEmployeeName.get(ar).setWidth("100%");
		
		lblEmployeeName.get(ar).addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
			  if(!lblEmployeeName.get(ar).getValue().toString().isEmpty())
			  {
				  if(ar==lblEmployeeID.size()-1)
				  {
					tableRowAdd(ar+1);  
				  }
			  }
				
			}
		});

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

		txtExtraOT.add(ar, new AmountCommaSeperator());
		txtExtraOT.get(ar).setWidth("100%");
		txtExtraOT.get(ar).setImmediate(true);
		txtExtraOT.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtExtraOT.get(ar).getValue().toString().isEmpty())
					{
						txtLessOT.get(ar).focus();
					}
				}
			}
		});

		txtLessOT.add(ar, new AmountCommaSeperator());
		txtLessOT.get(ar).setWidth("100%");
		txtLessOT.get(ar).setImmediate(true);
		txtLessOT.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!lblAutoEmployeeID.get(ar).getValue().toString().isEmpty())
				{
					if(!txtLessOT.get(ar).getValue().toString().isEmpty())
					{
						txtPermittedBy.get(ar).focus();
					}
				}
			}
		});

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
							txtExtraOT.get(ar+1).focus();
						}
					}
				}
			}
		});

		table.addItem(new Object[]{btnDel.get(ar),lblSl.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),lblProximityID.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),lblDepartmentID.get(ar),lblDepartmentName.get(ar),
				lblSectionID.get(ar),lblSectionName.get(ar),txtExtraOT.get(ar),txtLessOT.get(ar),txtPermittedBy.get(ar),txtReason.get(ar)}, ar);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1085.0px");
		mainlayout.setHeight("560.0px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("290.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department Name : "), "top:30.0px;left:30.0px;");
		mainlayout.addComponent(cmbDepartment, "top:28.0px;left:150.0px");

		cmbSection=new ComboBox();
		cmbSection.setWidth("290.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section Name : "), "top:60.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:58.0px;left:150.0px");

		date=new PopupDateField();
		date.setImmediate(true);
		date.setWidth("140px");
		date.setResolution(PopupDateField.RESOLUTION_MONTH);
		date.setDateFormat("MMMMM-yyyy");
		date.setValue(new Date());
		mainlayout.addComponent(new Label("Month : "), "top:90.0px;left:30.0px;");
		mainlayout.addComponent(date, "top:88.0px;left:150.0px;");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainlayout.addComponent(opgEmployee, "top:60.0px;left:470.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee : "), "top:90.0px;left:470.0px;");
		mainlayout.addComponent(cmbEmployee, "top:88.0px;left:560.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:90.0px;left:860.0px");

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

		table.addContainerProperty("Extra OT", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Less OT", 50);

		table.addContainerProperty("Less OT", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Less OT", 50);

		table.addContainerProperty("Permitted By", TextField.class, new TextField());
		table.setColumnWidth("Permitted By", 165);

		table.addContainerProperty("Reason", TextField.class, new TextField());
		table.setColumnWidth("Reason", 165);

		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Designation", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("Section Name", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Department Name", true);

		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:130.0px;left:30.0px");
		tableinitialize();

		mainlayout.addComponent(cButton, "top:520.0px;left:260.0px;");
		return mainlayout;
	}
}
