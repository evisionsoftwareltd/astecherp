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
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SectionWiseDeleteProximityID extends Window
{
	SessionBean sessionbean;
	private AbsoluteLayout mainlayout;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	
	private OptionGroup opgEmployee;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
	private ComboBox cmbEmployee;
	private CheckBox chkAllEmp;
	private CheckBox chkDeleteAll;
	private PopupDateField date;
	
	private Table table=new Table();;
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
	private ArrayList<CheckBox> chkDelete=new ArrayList<CheckBox>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private CommonButton cButton=new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");

	boolean isSave=false;
	boolean isFind=false;
	boolean isRefresh=false;
	boolean isUpdate=false;
	int index=0;

	public SectionWiseDeleteProximityID(SessionBean sessionBean)
	{

		this.sessionbean=sessionBean;
		this.setCaption("DELETE PROXIMITY ID/CARD NO. :: "+sessionbean.getCompany());
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
					"tbEmployeeInfo ein on sein.vSectionID=ein.vSectionId where " +
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
		chkDeleteAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkDeleteAll.booleanValue())
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							chkDelete.get(i).setValue(true);
						}
					}
				}
				else
				{
					for(int i=0;i<lblEmployeeID.size();i++)
					{
						if(!lblProximityID.get(i).getValue().toString().isEmpty())
						{
							chkDelete.get(i).setValue(false);
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
				cmbEmployee.setValue(null);
				opgEmployee.setEnabled(true);
				opgEmployee.select(null);
				chkAllEmp.setValue(false);
				cmbEmployee.setEnabled(true);
				tableclear();
				cmbEmployee.removeAllItems();
				index=0;
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
						sql="select vEmployeeId,employeeCode from tbEmployeeInfo where " +
							"vDepartmentID='"+cmbDepartment.getValue()+"'" +
							" and vSectionId='"+cmbSection.getValue().toString()+"' and " +
							"iStatus=1 and ISNULL(vProximityID,'')!='' order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Proximity ID")
					{
						sql="select vEmployeeId,vProximityId from tbEmployeeInfo where " +
							"vDepartmentID='"+cmbDepartment.getValue()+"'" +
							" and vSectionId='"+cmbSection.getValue().toString()+"' and " +
							"iStatus=1 and ISNULL(vProximityID,'')!='' order by employeeCode";
					}
					else if(opgEmployee.getValue()=="Employee Name")
					{
						sql="select vEmployeeId,vEmployeeName from tbEmployeeInfo where " +
							"vDepartmentID='"+cmbDepartment.getValue()+"'" +
							" and vSectionId='"+cmbSection.getValue().toString()+"' and " +
							"iStatus=1 and ISNULL(vProximityID,'')!='' order by employeeCode";
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
		MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Saved All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
		String delProxSql="";
		String deleteDate=dateFormat.format(date.getValue());
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			for(int i=0;i<lblProximityID.size();i++) 
			{
				if(!lblProximityID.get(i).getValue().toString().isEmpty())
				{
					if(chkDelete.get(i).booleanValue())
					{
						delProxSql="insert into tbDeleteProximityID values('"+deleteDate+"','"+lblAutoEmpID.get(i).getValue().toString()+"'," +
								"'"+lblProximityID.get(i).getValue().toString()+"','"+lblDesignationID.get(i).getValue().toString()+"'," +
								"'"+lblSectionID.get(i).getValue().toString()+"','"+sessionbean.getUserName()+"'," +
								"'"+sessionbean.getUserIp()+"',getdate(),'"+lblDepartmentID.get(i).getValue().toString()+"')";
						session.createSQLQuery(delProxSql).executeUpdate();

						sql="update tbEmployeeInfo set vProximityID='',DeleteDate='"+dateFormat.format(date.getValue())+"', vStatus='Discontinue', iStatus='0', dStatusDate='"+dateFormat.format(date.getValue())+"'"+
								" where vEmployeeId='"+lblAutoEmpID.get(i).getValue().toString()+"' and vSectionId='"+lblSectionID.get(i).getValue().toString()+
								"' and vDesignationId='"+lblDesignationID.get(i).getValue().toString()+"'";
						session.createSQLQuery(sql).executeUpdate();
					}
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
			String sql="select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vdesignationId," +
					"din.designationName,ein.vDepartmentID,dept.vDepartmentName,ein.vSectionID,sein.sectionName " +
					"from tbEmployeeInfo ein inner join tbDesignationInfo din on ein.vdesignationId=din.designationID " +
					"inner join tbDepartmentInfo dept on dept.vDepartmentID=ein.vDepartmentId inner join tbsectionInfo " +
					"sein on sein.vSectionId=ein.vSectionId where ein.vDepartmentID='"+cmbDepartment.getValue()+"' and " +
					"ein.vSectionId='"+cmbSection.getValue().toString()+"' and vEmployeeId like '"+emp+"' and " +
					"ISNULL(vProximityID,'')!='' and iStatus=1 order by ein.employeeCode";

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
						if(lblAutoEmpID.get(chkindex).getValue().equals(element[0].toString()))
						{
							check=true;
							break;
						}
					}
					if(!check)
					{
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
		cmbEmployee.setValue(null);
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
			lblDepartmentID.get(i).setValue("");
			lblDepartmentName.get(i).setValue("");
			lblSectionID.get(i).setValue("");
			lblSectionName.get(i).setValue("");
			chkDelete.get(i).setValue(false);
		}
	}

	private void componentEnable(boolean b)
	{
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		chkDeleteAll.setEnabled(!b);
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
		for(int i=0;i<11;i++)
			tableRowAdd(i);
	}

	public void tableRowAdd(final int ar)
	{
		lblSl.add(ar, new Label(""));
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setHeight("24.0px");
		lblSl.get(ar).setValue(ar+1);

		lblAutoEmpID.add(ar, new Label());
		lblAutoEmpID.get(ar).setWidth("100%");

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

		chkDelete.add(ar, new CheckBox());
		chkDelete.get(ar).setWidth("100%");

		table.addItem(new Object[]{lblSl.get(ar),lblAutoEmpID.get(ar),lblEmployeeID.get(ar),lblProximityID.get(ar),
				lblEmployeeName.get(ar),lblDesignationID.get(ar),lblDesignation.get(ar),lblDepartmentID.get(ar),
				lblDepartmentName.get(ar),lblSectionID.get(ar),lblSectionName.get(ar),chkDelete.get(ar)}, ar);
	}

	public AbsoluteLayout buildMainLayout()
	{
		mainlayout=new AbsoluteLayout();
		mainlayout.setWidth("975.0px");
		mainlayout.setHeight("505.0px");

		date=new PopupDateField();
		date.setImmediate(true);
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yyyy");
		date.setValue(new Date());
		mainlayout.addComponent(new Label("Date : "), "top:10.0px;left:30.0px;");
		mainlayout.addComponent(date, "top:08.0px;left:140.0px;");

		cmbDepartment=new ComboBox();
		cmbDepartment.setWidth("250.0px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Department Name : "), "top:35.0px;left:30.0px;");
		mainlayout.addComponent(cmbDepartment, "top:33.0px;left:140.0px");

		cmbSection=new ComboBox();
		cmbSection.setWidth("250.0px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Section Name : "), "top:60.0px;left:30.0px;");
		mainlayout.addComponent(cmbSection, "top:58.0px;left:140.0px");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		mainlayout.addComponent(opgEmployee, "top:35.0px;left:560.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setWidth("250.0px");
		cmbEmployee.setHeight("-1px");
		cmbEmployee.setImmediate(true);
		cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainlayout.addComponent(new Label("Employee Name : "), "top:60.0px;left:445.0px;");
		mainlayout.addComponent(cmbEmployee, "top:58.0px;left:560.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainlayout.addComponent(chkAllEmp, "top:60.0px;left:810.0px");

		chkDeleteAll=new CheckBox("Delete All");
		chkDeleteAll.setImmediate(true);
		mainlayout.addComponent(chkDeleteAll, "top:60.0px;left:865.0px");

		table.setWidth("928.0px");
		table.setHeight("360.0px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 25);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 100);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID",100);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity ID", 100);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name", 150);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 30);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 140);

		table.addContainerProperty("Department ID", Label.class, new Label());
		table.setColumnWidth("Department ID", 30);

		table.addContainerProperty("Department Name", Label.class, new Label());
		table.setColumnWidth("Department Name", 140);

		table.addContainerProperty("Section ID", Label.class, new Label());
		table.setColumnWidth("Section ID", 30);

		table.addContainerProperty("Section Name", Label.class, new Label());
		table.setColumnWidth("Section Name", 130);

		table.addContainerProperty("Del", CheckBox.class, new CheckBox());
		table.setColumnWidth("Del", 20);

		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Department ID", true);
		table.setColumnCollapsed("Section ID", true);
		table.setColumnCollapsed("EMP ID", true);
		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_CENTER});
		mainlayout.addComponent(table, "top:90.0px;left:30.0px");
		tableinitialize();
		mainlayout.addComponent(cButton, "top:460.0px;left:315.0px;");
		return mainlayout;
	}
}
