package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class OTNFridayEnable extends Window
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
	private CheckBox chkOTAll;
	private CheckBox chkFriAll;

	private Table table=new Table();
	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblSl=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblProximityID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName=new ArrayList<Label>();
	private ArrayList<Label> lblDesignationID=new ArrayList<Label>();
	private ArrayList<Label> lblDesignation=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentID=new ArrayList<Label>();
	private ArrayList<Label> lblDepartmentName=new ArrayList<Label>();
	private ArrayList<Label> lblSectionID=new ArrayList<Label>();
	private ArrayList<Label> lblSectionName=new ArrayList<Label>();
	private ArrayList<CheckBox> chkOT=new ArrayList<CheckBox>();
	private ArrayList<CheckBox> chkFriday=new ArrayList<CheckBox>();

	private CommonButton cButton=new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");

	boolean isSave=false;
	boolean isRefresh=false;
	int index=0;
	
	public OTNFridayEnable(SessionBean sessionBean)
	{
		this.sessionbean=sessionBean;
		this.setCaption("OT & FRIDAY ENABLE :: "+sessionbean.getCompany());
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
					   "tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where  dept.vDepartmentId!='DEPT10' and ein.iStatus=1 order by dept.vDepartmentName";
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
					   "tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where ein.iStatus=1 and " +
					   "ein.vDepartmentID='"+cmbDepartment.getValue()+"' order by sein.SectionName";
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
		chkOTAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkOTAll.booleanValue())
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(!chkFriday.get(i).booleanValue())
								chkOT.get(i).setValue(true);
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(chkFriday.get(i).booleanValue())
								chkOT.get(i).setValue(false);
						}
					}
				}
			}
		});

		chkFriAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkFriAll.booleanValue())
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(!chkOT.get(i).booleanValue())
								chkFriday.get(i).setValue(true);
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							if(chkOT.get(i).booleanValue())
								chkFriday.get(i).setValue(false);
						}
					}
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				chkAllSection.setValue(false);
				cmbSection.setEnabled(true);
				cmbSection.removeAllItems();
				tableclear();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectionDataLoad();
				}
			}
		});
		
		chkAllSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.setValue(null);
				opgEmployee.select(null);
				opgEmployee.setEnabled(true);
				chkAllEmp.setValue(false);
				cmbEmployee.setEnabled(true);
				chkFriAll.setValue(false);
				chkOTAll.setValue(false);
				cmbEmployee.removeAllItems();
				if(chkAllSection.booleanValue())
					cmbSection.setEnabled(false);
				else
					cmbSection.setEnabled(true);
				tableclear();
				index=0;
			}
		});
		
		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				opgEmployee.select(null);
				opgEmployee.setEnabled(true);
				chkAllEmp.setValue(false);
				cmbEmployee.setEnabled(true);
				chkFriAll.setValue(false);
				chkOTAll.setValue(false);
				cmbEmployee.removeAllItems();
				tableclear();
				index=0;
			}
		});

		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbSection.getValue()!=null || chkAllSection.booleanValue())
				{
					String sectionID="%";
					if(cmbSection.getValue()!=null)
					{
						sectionID=cmbSection.getValue().toString();
					}
					
					String sql="";
					if(opgEmployee.getValue()=="Employee ID")
					{
						sql="select vEmployeeID,employeeCode from tbEmployeeInfo where " +
							"vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionId like " +
							"'"+sectionID+"' and iStatus=1 and ISNULL(vProximityID,'')!='' order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Proximity ID")
					{
						sql="select vEmployeeID,vProximityId from tbEmployeeInfo where " +
							"vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionId like " +
							"'"+sectionID+"' and iStatus=1 and ISNULL(vProximityID,'')!='' order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Employee Name")
					{
						sql="select vEmployeeID,vEmployeeName from tbEmployeeInfo where " +
							"vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionId like " +
							"'"+sectionID+"' and iStatus=1 and ISNULL(vProximityID,'')!='' order by employeeCode";
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
				if(cmbSection.getValue()!=null || chkAllSection.booleanValue())
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
				if(!lblProximityID.get(0).getValue().toString().isEmpty())
				{
					isSave=true;
					saveButtonEvent();
				}
				else
					showNotification("Warning", "No Data Found in the Table!!!", Notification.TYPE_WARNING_MESSAGE);
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
					txtclear();
					componentEnable(true);
					btnEnable(true);
				}
			}
		});
	}

	private void insertdata()
	{
		String sql="";
		String udsqlbefore="";
		String udsqlafter="";
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			for(int i=0;i<lblProximityID.size();i++) 
			{
				if(!lblProximityID.get(i).getValue().toString().isEmpty())
				{
					udsqlbefore="insert into tbUDOTNFriday values('"+lblProximityID.get(i).getValue().toString()+"'," +
							"'"+lblEmployeeName.get(i).getValue().toString()+"',(select OtStatus from tbEmployeeInfo " +
							"ein where ein.vProximityID='"+lblProximityID.get(i).getValue().toString()+"' " +
							"and vSectionID='"+lblSectionID.get(i).getValue().toString()+"'),(select FridayStatus " +
							"from tbEmployeeInfo ein where ein.vProximityID='"+lblProximityID.get(i).getValue().toString()+"' " +
							"and vSectionID='"+lblSectionID.get(i).getValue().toString()+"'),'New','"+sessionbean.getUserName()+"'," +
							"'"+sessionbean.getUserName()+"',getdate(),(select vEmployeeID from tbEmployeeInfo " +
							"ein where ein.employeeCode='"+lblEmployeeID.get(i).getValue().toString()+"')," +
							"'"+lblEmployeeID.get(i).getValue().toString()+"','"+lblSectionID.get(i).getValue().toString()+"'," +
							"'"+lblSectionName.get(i).getValue().toString()+"')";
					session.createSQLQuery(udsqlbefore).executeUpdate();
					session.clear();
					
					sql="update tbEmployeeInfo set OtStatus='"+(chkOT.get(i).booleanValue()?1:0)+"',FridayStatus='"+(chkFriday.get(i).booleanValue()?1:0)+"', " +
							"FridayLunchFee='"+(chkFriday.get(i).booleanValue()?75.00:0.00)+"' "+
							"where vProximityID='"+lblProximityID.get(i).getValue().toString()+"' and vDepartmentID='"+lblDepartmentID.get(i).getValue().toString()+"' " +
							"and vSectionId='"+lblSectionID.get(i).getValue().toString()+"' " +
							"and vDesignationId='"+lblDesignationID.get(i).getValue().toString()+"'";
					session.createSQLQuery(sql).executeUpdate();
					session.clear();

					udsqlafter="insert into tbUDOTNFriday values('"+lblProximityID.get(i).getValue().toString()+"'," +
							"'"+lblEmployeeName.get(i).getValue().toString()+"','"+(chkOT.get(i).booleanValue()?1:0)+"'," +
							"'"+(chkFriday.get(i).booleanValue()?1:0)+"','Update','"+sessionbean.getUserName()+"'," +
							"'"+sessionbean.getUserName()+"',getdate(),(select vEmployeeID from tbEmployeeInfo " +
							"ein where ein.employeeCode='"+lblEmployeeID.get(i).getValue().toString()+"')," +
							"'"+lblEmployeeID.get(i).getValue().toString()+"','"+lblSectionID.get(i).getValue().toString()+"'," +
							"'"+lblSectionName.get(i).getValue().toString()+"')";
					session.createSQLQuery(udsqlafter).executeUpdate();
					session.clear();
				}
			}
			showNotification("All Information Saved Successfully");
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("InsertDate", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
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
			String SectionID="%";
			if(cmbSection.getValue()!=null)
				SectionID=cmbSection.getValue().toString();
			String sql="select ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vdesignationId,din.designationName," +
					"ein.vDepartmentID,dept.vDepartmentName,ein.vSectionID,sein.sectionName,ein.OtStatus,ein.FridayStatus from tbEmployeeInfo ein inner join " +
					"tbDepartmentInfo dept on ein.vDepartmentID=dept.vDepartmentID inner join tbDesignationInfo din on " +
					"din.designationId=ein.vDesignationID inner join tbSectionInfo sein on sein.vSectionID=ein.vSectionID " +
					"where ein.vDepartmentID='"+cmbDepartment.getValue()+"' and ein.vSectionId like '"+SectionID+"' and " +
					"ein.vEmployeeID like '"+emp+"' and ISNULL(vProximityID,'')!='' and iStatus=1 order by ein.employeeCode";

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

						if(lblProximityID.get(chkindex).getValue().equals(element[1].toString()))
						{
							check=true;
							break;
						}
					}
					if(!check)
					{
						lblEmployeeID.get(index).setValue(element[0].toString());
						lblProximityID.get(index).setValue(element[1].toString());
						lblEmployeeName.get(index).setValue(element[2].toString());
						lblDesignationID.get(index).setValue(element[3].toString());
						lblDesignation.get(index).setValue(element[4].toString());
						lblDepartmentID.get(index).setValue(element[5].toString());
						lblDepartmentName.get(index).setValue(element[6].toString());
						lblSectionID.get(index).setValue(element[7].toString());
						lblSectionName.get(index).setValue(element[8].toString());

						if(element[9].toString().equals("1"))
							chkOT.get(index).setValue(true);
						if(element[10].toString().equals("1"))
							chkFriday.get(index).setValue(true);

						if(index==lblEmployeeID.size()-1)
							tableRowAdd(index+1);

						index++;
					}
					checkData=check;
				}
				if(checkData)
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
		cmbEmployee.setValue(null);
		opgEmployee.select(null);
		chkAllSection.setValue(false);
		chkAllEmp.setValue(false);
		tableclear();
	}

	private void tableclear()
	{
		for(int i=0;i<lblEmployeeID.size();i++)
		{
			lblEmployeeID.get(i).setValue("");
			lblProximityID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignationID.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblDepartmentID.get(i).setValue("");
			lblDepartmentName.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			chkOT.get(i).setValue(false);
			chkFriday.get(i).setValue(false);
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
		chkOTAll.setEnabled(!b);
		chkFriAll.setEnabled(!b);
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
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!lblProximityID.get(ar).getValue().toString().equals(""))
					index=index-1;
				lblEmployeeID.get(ar).setValue("");
				lblProximityID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignationID.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblDepartmentID.get(ar).setValue("");
				lblDepartmentName.get(ar).setValue("");
				lblSectionID.get(ar).setValue("");
				lblSectionName.get(ar).setValue("");
				chkOT.get(ar).setValue(false);
				chkFriday.get(ar).setValue(false);

				for(int tbIndex=ar;tbIndex<lblProximityID.size();tbIndex++)
				{
					if(tbIndex+1<lblProximityID.size())
					{
						if(!lblProximityID.get(tbIndex+1).getValue().toString().equals(""))
						{
							lblEmployeeID.get(tbIndex).setValue(lblEmployeeID.get(tbIndex+1).getValue().toString().trim());
							lblProximityID.get(tbIndex).setValue(lblProximityID.get(tbIndex+1).getValue().toString().trim());
							lblEmployeeName.get(tbIndex).setValue(lblEmployeeName.get(tbIndex+1).getValue().toString().trim());
							lblDesignationID.get(tbIndex).setValue(lblDesignationID.get(tbIndex+1).getValue().toString().trim());
							lblDesignation.get(tbIndex).setValue(lblDesignation.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentID.get(tbIndex).setValue(lblDepartmentID.get(tbIndex+1).getValue().toString().trim());
							lblDepartmentName.get(tbIndex).setValue(lblDepartmentName.get(tbIndex+1).getValue().toString().trim());
							lblSectionID.get(tbIndex).setValue(lblSectionID.get(tbIndex+1).getValue().toString().trim());
							lblSectionName.get(tbIndex).setValue(lblSectionName.get(tbIndex+1).getValue().toString().trim());
							chkOT.get(tbIndex).setValue(chkOT.get(tbIndex+1).getValue());
							chkFriday.get(tbIndex).setValue(chkFriday.get(tbIndex+1).getValue());
							
							lblEmployeeID.get(tbIndex+1).setValue("");
							lblProximityID.get(tbIndex+1).setValue("");
							lblEmployeeName.get(tbIndex+1).setValue("");
							lblDesignationID.get(tbIndex+1).setValue("");
							lblDesignation.get(tbIndex+1).setValue("");
							lblDepartmentID.get(tbIndex+1).setValue("");
							lblDepartmentName.get(tbIndex+1).setValue("");
							lblSectionID.get(tbIndex+1).setValue("");
							lblSectionName.get(tbIndex+1).setValue("");
							chkOT.get(tbIndex+1).setValue(false);
							chkFriday.get(tbIndex+1).setValue(false);
						}
					}
				}
			}
		});

		lblSl.add(ar, new Label());
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("20px");
		lblSl.get(ar).setValue(ar+1);

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

		chkOT.add(ar, new CheckBox());
		chkOT.get(ar).setWidth("100%");
		chkOT.get(ar).setImmediate(true);
		chkOT.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkOT.get(ar).booleanValue())
					chkFriday.get(ar).setValue(false);
				else
					chkFriday.get(ar).setValue(true);
			}
		});

		chkFriday.add(ar, new CheckBox());
		chkFriday.get(ar).setWidth("100%");
		chkFriday.get(ar).setImmediate(true);
		chkFriday.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkFriday.get(ar).booleanValue())
					chkOT.get(ar).setValue(false);
				else
					chkOT.get(ar).setValue(true);
			}
		});

		table.addItem(new Object[]{btnDel.get(ar),lblSl.get(ar),lblEmployeeID.get(ar),lblProximityID.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),lblDepartmentID.get(ar),
				lblDepartmentName.get(ar),lblSectionID.get(ar),lblSectionName.get(ar),chkOT.get(ar),chkFriday.get(ar)}, ar);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("1065.0px");
		mainlayout.setHeight("490.0px");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("250.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department Name : "), "top:10.0px;left:30.0px;");
		mainlayout.addComponent(cmbDepartment, "top:08.0px;left:140.0px");
		
		cmbSection=new ComboBox();
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section Name : "), "top:35.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:33.0px;left:140.0px");

		chkAllSection=new CheckBox("All");
		chkAllSection.setImmediate(true);
		mainlayout.addComponent(chkAllSection, "top:35.0px; left:395.0px;");
		
		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainlayout.addComponent(opgEmployee, "top:10.0px;left:590.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("290.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee Name : "), "top:35.0px;left:470.0px;");
		mainlayout.addComponent(cmbEmployee, "top:33.0px;left:580.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:35.0px;left:875.0px");

		chkOTAll=new CheckBox("OT All");
		chkOTAll.setImmediate(true);
		mainlayout.addComponent(chkOTAll, "top:10.0px;left:945.0px");

		chkFriAll=new CheckBox("Friday All");
		chkFriAll.setImmediate(true);
		mainlayout.addComponent(chkFriAll, "top:35.0px;left:945.0px");

		table.setWidth("1025.0px");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",90);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity ID", 90);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 150);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 150);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 30);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 150);
		
		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 150);

		table.addContainerProperty("OT", CheckBox.class, new CheckBox());
		table.setColumnWidth("OT", 20);

		table.addContainerProperty("FRI", CheckBox.class, new CheckBox());
		table.setColumnWidth("FRI", 20);

		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnAlignments(new String[]{Table.ALIGN_CENTER,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:80.0px;left:30.0px");
		tableinitialize();
		mainlayout.addComponent(cButton, "top:450.0px;left:335.0px;");
		return mainlayout;
	}
}
