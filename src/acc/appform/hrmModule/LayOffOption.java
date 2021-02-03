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

import com.common.share.AmountField;
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
public class LayOffOption extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private PopupDateField Fromdate;
	private PopupDateField Todate;

	private TextField txtTransactionID=new TextField();

	private Table table=new Table();
	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblSl=new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName=new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID=new ArrayList<Label>();
	private ArrayList<Label> lblDesignation=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentID=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName=new ArrayList<Label>();
	private ArrayList<Label> lblSectionID=new ArrayList<Label>();
	private ArrayList<Label> lblSectionName=new ArrayList<Label>();
	private ArrayList<AmountField> tbTxtAmount = new ArrayList<AmountField>();
	private ArrayList<CheckBox> chkApply=new ArrayList<CheckBox>();
	private ArrayList<CheckBox> chkCancel=new ArrayList<CheckBox>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat YearFormat=new SimpleDateFormat("yyyy");

	private DecimalFormat decimalFormat=new DecimalFormat("#0.00");
	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");

	boolean isUpdate=false;
	int index=0;
	String Noti="";
	protected List<String> TxtAmount;

	public LayOffOption(SessionBean sessionBean)
	{
		this.sessionbean=sessionBean;
		this.setCaption("LAYOFF OPTION :: "+sessionbean.getCompany());
		buildMainLayout();
		this.setContent(mainlayout);
		this.setResizable(false);
		componentEnable(true);
		btnEnable(true);
		setEventAction();
		cmbDepartmentDataLoad();
	}

	private void componentEnable(boolean b)
	{
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		Fromdate.setEnabled(!b);
		Todate.setEnabled(!b);
		table.setEnabled(!b);
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
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
					addEmployeeName();
				tableclear();
				index=0;
			}
		});

		Fromdate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkAllEmp.setValue(false);
				cmbEmployee.setValue(null);
				tableclear();
				chkDateValidity();
			}
		});

		Todate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkAllEmp.setValue(false);
				cmbEmployee.setValue(null);
				tableclear();
				chkDateValidity();
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					addEmployeeName();
				}
				else
				{
					showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
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
				}
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
						opgEmployee.setEnabled(false);
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
					chkAllEmp.setValue(false);
					showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
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
					String query = "select * from tbSalary where vDepartmentID = '"+cmbDepartment.getValue()+"' " +
							"and SectionID = '"+cmbSection.getValue()+"' and MONTH(dDate) between " +
							"MONTH('"+dateFormat.format(Fromdate.getValue())+"') and MONTH('"+dateFormat.format(Todate.getValue())+"') and YEAR='"+YearFormat.format(Todate.getValue())+"' and YEAR='"+YearFormat.format(Fromdate.getValue())+"' ";
					if(chkExistsData(query))
						saveButtonEvent();
					else
						showNotification("Warning", "Salary already generated!!!", Notification.TYPE_WARNING_MESSAGE);
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
					cmbDepartment.setEnabled(false);
					cmbSection.setEnabled(false);
					Fromdate.setEnabled(false);
					Todate.setEnabled(false);
				}
				else
					showNotification("Warning", "Edit Failed!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				txtclear();
				componentEnable(true);
				btnEnable(true);
			}
		});

		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				findbuttonEvent();
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

	private void addEmployeeName()
	{
		String sql="";
		String SectionID="%";
		if(cmbSection.getValue()!=null)
			SectionID=cmbSection.getValue().toString();

		if(opgEmployee.getValue()=="Employee ID")
		{
			sql="select vEmployeeId,employeeCode from tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' " +
					"and vSectionId='"+SectionID+"' and ISNULL(vProximityId,'')!='' " +
					"and iStatus=1 order by employeeCode";
		}
		else if(opgEmployee.getValue()=="Proximity ID")
		{
			sql="select vEmployeeId,vProximityID from tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' " +
					"and vSectionId='"+SectionID+"' and ISNULL(vProximityId,'')!='' " +
					"and iStatus=1 order by employeeCode";
		}
		else if(opgEmployee.getValue()=="Employee Name")
		{
			sql="select vEmployeeId,vEmployeeName from tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' " +
					"and vSectionId='"+SectionID+"' and ISNULL(vProximityId,'')!='' " +
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

	private void chkDateValidity()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String strDay=session.createSQLQuery("select DATEDIFF(dd,'"+dateFormat.format(Fromdate.getValue())+"','"+dateFormat.format(Todate.getValue())+"')").list().iterator().next().toString();
			if(Integer.parseInt(strDay)<0)
			{
				Todate.setValue(Fromdate.getValue());
			}
		}
		catch (Exception exp)
		{
			showNotification("chkDateValidity", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean chkExistsData(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			for(int i=0; i<lblAutoEmployeeID.size();i++)
			{
				List <?> lstChk = session.createSQLQuery(query).list();
				if(!lstChk.isEmpty())
				{
					return false;
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("chkExistsData", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return true;
	}

	private void updateButtonEvent()
	{
		componentEnable(false);
		btnEnable(false);
	}

	private void findbuttonEvent()
	{
		Window win = new LayOffOptionFind(sessionbean,txtTransactionID);
		win.addListener(new CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(!txtTransactionID.getValue().toString().trim().isEmpty())
				{
					txtclear();
					findInitialise(txtTransactionID.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialise(String TransactionID) 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try  
		{
			String sql = "select dFromDate,dToDate,vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName,vDesignationId,vDesignationName,vDepartmentID," +
					"vDepartmentName,vSectionId,vSectionName,mPerDayRate,vApproveFlag from tbLayOff where vTransactionID='"+TransactionID+"' order by vEmployeeCode";

			System.out.println("Shehab"+sql);
			List <?> list = session.createSQLQuery(sql).list();

			int i=0;
			for(Iterator <?> itr=list.iterator();itr.hasNext();)
			{
				Object[] element = (Object[]) itr.next();

				if(i==0)
				{
					Fromdate.setValue(element[0]);
					Todate.setValue(element[1]);
					cmbDepartment.setValue(element[8]);
					cmbSection.setValue(element[10]);
				}

				lblAutoEmployeeID.get(i).setValue(element[2]);
				lblEmployeeID.get(i).setValue(element[3]);
				lblEmployeeName.get(i).setValue(element[5]);
				lblDesignationID.get(i).setValue(element[6]);
				lblDesignation.get(i).setValue(element[7]);
				lblDepartmentID.get(i).setValue(element[8]);
				lblDepartmentName.get(i).setValue(element[9]);
				lblSectionID.get(i).setValue(element[10]);
				lblSectionName.get(i).setValue(element[11]);
				tbTxtAmount.get(i).setValue(decimalFormat.format(Double.parseDouble(element[12].toString())));

				if(Integer.parseInt(element[13].toString())==1)
				{
					chkApply.get(i).setValue(true);
				}
				else if(Integer.parseInt(element[13].toString())==0)
				{
					chkCancel.get(i).setValue(true);
				}
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
							deleteData(session);
							insertdata(session);
							isUpdate=false;
							tx.commit();

							txtclear();
							componentEnable(true);
							btnEnable(true);
							showNotification("All Information Updated Successfully");
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
							showNotification("All Information Saved Successfully");
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

	private String transactionIDGenerate()
	{
		String transactionID = "LOTNS-";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select ISNULL(MAX(CAST(SUBSTRING(vTransactionID,7,LEN(vTransactionID)) as int)),0)+1 from tbLayOff";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				transactionID += lst.iterator().next().toString();
			}
		}
		catch (Exception exp)
		{
			showNotification("transactionIDGenerate", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return transactionID;
	}

	private void deleteData(Session session)
	{
		String chkQuery="select * from tbLayOff where vTransactionID = '"+txtTransactionID.getValue().toString()+"'";
		List <?> lst=session.createSQLQuery(chkQuery).list();
		if(!lst.isEmpty())
		{
			String udSql="insert into tbUDLayOff (vTransactionID,vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName,vDepartmentID," +
					"vDepartmentName,vSectionId,vSectionName,vDesignationId,vDesignationName,dFromDate,dToDate,iTotalDays,mPerDayRate," +
					"vApproveFlag,UDFlag,vUserName,vUserIp,dEntryTime) select vTransactionID,vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName,vDepartmentID," +
					"vDepartmentName,vSectionId,vSectionName,vDesignationId,vDesignationName,dFromDate,dToDate,iTotalDays,mPerDayRate," +
					"vApproveFlag,'OLD',vUserName,vUserIp,dEntryTime from tbLayOff where vTransactionID = '"+txtTransactionID.getValue().toString()+"'";
			session.createSQLQuery(udSql).executeUpdate();
			String sql="delete from tbLayOff where vTransactionID = '"+txtTransactionID.getValue().toString()+"'";
			session.createSQLQuery(sql).executeUpdate();
			sql = "delete from tbTempLayOff where vTransactionID = '"+txtTransactionID.getValue().toString()+"'";
			session.createSQLQuery(sql).executeUpdate();
		}
	}

	private void insertdata(Session session)
	{
		String layoffSql="";
		String prcExecute="";
		String transactionID = "";
		if(!isUpdate)
			transactionID = transactionIDGenerate();
		else
			transactionID = txtTransactionID.getValue().toString();

		for(int i=0;i<lblAutoEmployeeID.size();i++) 
		{
			if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
			{
				layoffSql="insert into tbLayOff (vTransactionID,vEmployeeID,vEmployeeCode,vProximityId,vEmployeeName," +
						"vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationId,vDesignationName,dFromDate," +
						"dToDate,iTotalDays,mPerDayRate,vApproveFlag,vUserName,vUserIp,dEntryTime) values " +
						"('"+transactionID+"'," +
						"'"+lblAutoEmployeeID.get(i).getValue().toString()+"'," +
						"'"+lblEmployeeID.get(i).getValue().toString()+"'," +
						"(select vProximityId from tbEmployeeInfo where vEmployeeId='"+lblAutoEmployeeID.get(i).getValue().toString()+"')," +
						"'"+lblEmployeeName.get(i).getValue().toString()+"'," +
						"'"+lblDepartmentID.get(i).getValue()+"'," +
						"'"+lblDepartmentName.get(i).getValue()+"',"+
						"'"+lblSectionID.get(i).getValue().toString()+"'," +
						"'"+lblSectionName.get(i).getValue().toString()+"'," +
						"'"+lblDesignationID.get(i).getValue().toString()+"'," +
						"'"+lblDesignation.get(i).getValue().toString()+"',"+
						"'"+dateFormat.format(Fromdate.getValue())+"'," +
						"'"+dateFormat.format(Todate.getValue())+"',"+
						"DATEDIFF(dd,'"+dateFormat.format(Fromdate.getValue())+"','"+dateFormat.format(Todate.getValue())+"')+1,"+
						"'"+tbTxtAmount.get(i).getValue().toString()+"'," +
						"'"+(chkApply.get(i).booleanValue()?"1":"0")+"'," +
						"'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',getdate())";
				session.createSQLQuery(layoffSql).executeUpdate();
				if(chkApply.get(i).booleanValue())
				{
					prcExecute = "exec prcLayOff '"+lblAutoEmployeeID.get(i).getValue().toString()+"','"+transactionID+"','"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"'";
					session.createSQLQuery(prcExecute).executeUpdate();
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
			String sql="select ein.vEmployeeId,ein.employeeCode,ein.vEmployeeName,ein.vdesignationId," +
					"din.designationName,ein.vDepartmentID,dept.vDepartmentName,ein.vSectionID,sein.sectionName,50 as amount " +
					"from tbEmployeeInfo ein inner join tbDepartmentInfo dept on ein.vDepartmentID=dept.vDepartmentID " +
					"inner join tbDesignationInfo din on din.designationID=ein.vDesignationID inner join tbSectionInfo " +
					"sein on sein.vSectionID=ein.vSectionID where ein.vDepartmentID='"+cmbDepartment.getValue()+"' and " +
					"ein.vSectionId like '"+cmbSection.getValue().toString()+"' and vEmployeeId like '"+emp+"' and " +
					"ISNULL(vProximityId,'')!='' and iStatus=1 order by ein.employeeCode";

			List <?> lst=session.createSQLQuery(sql).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				boolean checkData=false;
				String empID="";
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
						if(tbTxtAmount.get(chkindex).getValue().toString().isEmpty())
						{
							index=chkindex;
							break;
						}
					}

					if(!check)
					{
						lblAutoEmployeeID.get(index).setValue(element[0].toString());
						lblEmployeeID.get(index).setValue(element[1].toString());
						lblEmployeeName.get(index).setValue(element[2].toString());
						lblDesignationID.get(index).setValue(element[3].toString());
						lblDesignation.get(index).setValue(element[4].toString());
						lblDepartmentID.get(index).setValue(element[5].toString());
						lblDepartmentName.get(index).setValue(element[6].toString());
						lblSectionID.get(index).setValue(element[7].toString());
						lblSectionName.get(index).setValue(element[8].toString());
						tbTxtAmount.get(index).setValue(element[9].toString());
						chkApply.get(index).setValue(true);

						if(index==lblEmployeeID.size()-1)
							tableRowAdd(index+1);
						

						String chksql = "select dLayOffDate from tbTempLayOff where dLayOffDate between " +
								"'"+dateFormat.format(Fromdate.getValue())+"' and '"+dateFormat.format(Todate.getValue())+"'" +
								" and vEmployeeID = '"+lblAutoEmployeeID.get(index).getValue()+"'";
						if(!chkExistsData(chksql) && !isUpdate)
						{
							empID+=lblEmployeeID.get(index).getValue().toString()+",";
							lblAutoEmployeeID.get(index).setValue("");
							lblEmployeeID.get(index).setValue("");
							lblEmployeeName.get(index).setValue("");
							lblDesignationID.get(index).setValue("");
							lblDesignation.get(index).setValue("");
							lblDepartmentID.get(index).setValue("");
							lblDepartmentName.get(index).setValue("");
							lblSectionID.get(index).setValue("");
							lblSectionName.get(index).setValue("");
							tbTxtAmount.get(index).setValue("");
							chkApply.get(index).setValue(false);
						}
						index++;
					}
					checkData=check;
				}
				if(empID.length()>0)
					showNotification("Warning", "Data already exists for "+empID.subSequence(0, empID.length()-1)+"!!!", Notification.TYPE_WARNING_MESSAGE);
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
		cmbEmployee.setValue(null);
		chkAllEmp.setValue(false);
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0;i<lblEmployeeID.size();i++)
		{
			lblAutoEmployeeID.get(i).setValue("");
			lblEmployeeID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblDepartmentID.get(i).setValue("");
			lblDepartmentName.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			tbTxtAmount.get(i).setValue("");
			chkApply.get(i).setValue(false);
			chkCancel.get(i).setValue(false);
		}
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
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				lblAutoEmployeeID.get(ar).setValue("");
				lblEmployeeID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignationID.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblDepartmentID.get(ar).setValue("");
				lblDepartmentName.get(ar).setValue("");
				lblSectionID.get(ar).setValue("");
				lblSectionName.get(ar).setValue("");
				tbTxtAmount.get(ar).setValue("");
				chkApply.get(ar).setValue(false);

				for(int tbIndex=ar;tbIndex<tbTxtAmount.size();tbIndex++)
				{
					if(tbIndex+1<tbTxtAmount.size())
					{
						if(!tbTxtAmount.get(tbIndex+1).getValue().toString().trim().equals(""))
						{
							lblAutoEmployeeID.get(tbIndex).setValue(lblAutoEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeID.get(tbIndex).setValue(lblEmployeeID.get(tbIndex+1).getValue().toString().trim());

							lblEmployeeName.get(tbIndex).setValue(lblEmployeeName.get(tbIndex+1).getValue().toString().trim());
							lblDesignationID.get(tbIndex).setValue(lblDesignationID.get(tbIndex+1).getValue().toString().trim());
							lblDesignation.get(tbIndex).setValue(lblDesignation.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentID.get(tbIndex).setValue(lblDepartmentID.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentName.get(tbIndex).setValue(lblDepartmentName.get(tbIndex+1).getValue().toString().trim());
							lblSectionID.get(tbIndex).setValue(lblSectionID.get(tbIndex+1).getValue().toString().trim());
							lblSectionName.get(tbIndex).setValue(lblSectionName.get(tbIndex+1).getValue().toString().trim());
							tbTxtAmount.get(tbIndex).setValue(tbTxtAmount.get(tbIndex+1).getValue().toString().trim());
							chkApply.get(tbIndex).setValue(chkApply.get(tbIndex+1).getValue());
							chkCancel.get(tbIndex).setValue(chkCancel.get(tbIndex+1).getValue());

							lblAutoEmployeeID.get(tbIndex+1).setValue("");
							lblEmployeeID.get(tbIndex+1).setValue("");
							lblEmployeeName.get(tbIndex+1).setValue("");
							lblDesignationID.get(tbIndex+1).setValue("");
							lblDesignation.get(tbIndex+1).setValue("");
							lblDepartmentID.get(tbIndex+1).setValue("");
							lblDepartmentName.get(tbIndex+1).setValue("");
							lblSectionID.get(tbIndex+1).setValue("");
							lblSectionName.get(tbIndex+1).setValue("");
							tbTxtAmount.get(tbIndex+1).setValue("");
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

		lblEmployeeID.add(ar, new Label());
		lblEmployeeID.get(ar).setWidth("100%");

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

		tbTxtAmount.add(ar, new AmountField());
		tbTxtAmount.get(ar).setWidth("100%");

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

		table.addItem(new Object[]{lblSl.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeID.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),
				lblDepartmentID.get(ar),lblDepartmentName.get(ar),lblSectionID.get(ar),
				lblSectionName.get(ar),tbTxtAmount.get(ar),chkApply.get(ar),chkCancel.get(ar),
				btnDel.get(ar)}, ar);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1085.0px");
		mainlayout.setHeight("500.0px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("200.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department Name : "), "top:10.0px;left:30.0px;");
		mainlayout.addComponent(cmbDepartment, "top:08.0px;left:140.0px");

		cmbSection=new ComboBox();
		cmbSection.setWidth("200.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section Name : "), "top:35.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:33.0px;left:140.0px");

		Fromdate=new PopupDateField();
		Fromdate.setImmediate(true);
		Fromdate.setResolution(PopupDateField.RESOLUTION_DAY);
		Fromdate.setDateFormat("dd-MM-yyyy");
		Fromdate.setValue(new Date());
		Fromdate.setWidth("110.0px");
		mainlayout.addComponent(new Label("From Date : "), "top:10.0px;left:360.0px;");
		mainlayout.addComponent(Fromdate, "top:08.0px;left:450.0px;");

		Todate=new PopupDateField();
		Todate.setImmediate(true);
		Todate.setResolution(PopupDateField.RESOLUTION_DAY);
		Todate.setDateFormat("dd-MM-yyyy");
		Todate.setValue(new Date());
		Todate.setWidth("110.0px");
		mainlayout.addComponent(new Label("To Date : "), "top:35.0px;left:360.0px;");
		mainlayout.addComponent(Todate, "top:33.0px;left:450.0px;");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		opgEmployee.select("Employee ID");
		mainlayout.addComponent(opgEmployee, "top:10.0px;left:700.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee Name : "), "top:35.0px;left:590.0px;");
		mainlayout.addComponent(cmbEmployee, "top:33.0px;left:700.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:35.0px;left:995.0px");

		table.setWidth("98%");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Emp ID", Label.class, new Label());
		table.setColumnWidth("Emp ID", 70);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",140);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 197);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 120);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 30);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 120);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 120);

		table.addContainerProperty("Per Day", AmountField.class, new AmountField());
		table.setColumnWidth("Per Day", 60);

		table.addContainerProperty("Apply", CheckBox.class, new CheckBox());
		table.setColumnWidth("Apply", 35);

		table.addContainerProperty("Cancel", CheckBox.class, new CheckBox());
		table.setColumnWidth("Cancel", 35);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.setColumnCollapsed("Emp ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,Table.ALIGN_CENTER,
				Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:80.0px;left:30.0px");
		tableinitialize();

		mainlayout.addComponent(cButton, "top:455.0px;left:260.0px;");
		return mainlayout;
	}
}
