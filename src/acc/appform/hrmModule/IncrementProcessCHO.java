
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
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;


public class IncrementProcessCHO extends Window {

	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	private Label lblEmployee;
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private ComboBox cmbIncrement;
	private ComboBox cmbEmployee;
	private OptionGroup opgEmployee;
	private CheckBox chkAllEmp;

	private Label lblDepartment;
	private Label lblSection;
	public Label lblStar;
	private Label lblIncrement;


	private static final List<String> Optiontype=Arrays.asList(new String[]{"Employee Name","Employee ID","Proximity ID"});

	private Label LblOr;
	private PopupDateField dDate;
	private AmountField txtIncrPercentage;
	private AmountField txtIncrementAmount;
	private OptionGroup btnGroupIncrementType;
	private static final List<String> type = Arrays.asList(new String[] {"Basic","Gross"});

	private Table table;
	private ArrayList<Label> tbLblSl = new ArrayList<Label>();
	private ArrayList<Label> tbLblAutoEmpID = new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> tbLblProximityID = new ArrayList<Label>();
	private ArrayList<Label> tbLblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> tbLblDesignationID = new ArrayList<Label>();
	private ArrayList<Label> tbLblDesignation = new ArrayList<Label>();
	private ArrayList<Label> tblblEmployeeType = new ArrayList<Label>();
	private ArrayList<Label> tblblInterviewDate = new ArrayList<Label>();
	private ArrayList<Label> tblblServiceOfLength = new ArrayList<Label>();
	private ArrayList<Label> tbtLblBasic = new ArrayList<Label>();
	private ArrayList<Label> tbLblHouseRent = new ArrayList<Label>();
	private ArrayList<Label> tbLblConveyance = new ArrayList<Label>();
	private ArrayList<Label> tbLblMedicalAllowance = new ArrayList<Label>();
	private ArrayList<Label> tbLblGross= new ArrayList<Label>();
	private ArrayList<AmountField> tbtxtIncrPercentage = new ArrayList<AmountField>();
	private ArrayList<Label> tbLblIncPer= new ArrayList<Label>();
	private ArrayList<AmountField> tbtxtIncrementAmount = new ArrayList<AmountField>();
	private ArrayList<Label> tbLblIncAmt= new ArrayList<Label>();
	private ArrayList<Label> tbLblNewBasic = new ArrayList<Label>();
	private ArrayList<Label> tbLblNewHouseRent = new ArrayList<Label>();
	private ArrayList<Label> tbLblNewConveyance = new ArrayList<Label>();
	private ArrayList<Label> tbLblNewMedicalAllowance = new ArrayList<Label>();
	private ArrayList<Label> tbLblNewGross = new ArrayList<Label>();


	private ArrayList<NativeButton>tbBtndel = new ArrayList<NativeButton>();

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private DecimalFormat twoDigit = new DecimalFormat("#0.00");
	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "Exit");
	private NativeButton nbIncrementType;

	private CommaSeparator commaSeparator=new CommaSeparator();

	int index=0;
	String Notify="";
	private boolean isUpdate=false;
	private Label IncrementDate = new Label();
	private Label SectionID = new Label();
	private Label IncrementType = new Label();
	private Label EmployeeName = new Label();
	boolean isFind=false;


	private ArrayList<Component> allComp = new ArrayList<Component>();

	public IncrementProcessCHO(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("INCREMENT PROCESS CHO:: "+sessionBean.getCompany());
		this.setResizable(false);
		buildlMainLayout();
		setContent(mainLayout);
		tableinitialize();
		componentIni(true);
		btnIni(true);
		setEventAction();
		addDepartmentData();
		cmbIncrementData();
		addSectionData();	
		focuMove();
	}

	private void componentIni(boolean b)
	{
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		opgEmployee.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkAllEmp.setEnabled(!b);
		txtIncrPercentage.setEnabled(!b);
		btnGroupIncrementType.setVisible(false);
		dDate.setEnabled(!b);
		txtIncrementAmount.setVisible(true);
		txtIncrementAmount.setEnabled(!b);
		nbIncrementType.setEnabled(!b);
		cmbIncrement.setEnabled(!b);
		table.setEnabled(!b);
	}

	private void btnIni(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnEdit.setEnabled(b);
		cButton.btnRefresh.setEnabled(!b);
		cButton.btnFind.setEnabled(b);
	}

	private void setEventAction()
	{
		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue() != null)
				{
					addSectionData();

				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				tableClear();
				tableComponentIni();
				txtIncrPercentage.setValue("");
				txtIncrementAmount.setValue("");
				if(cmbSection.getValue() != null)
				{					
					addEmployeeName(cmbSection.getValue().toString());
					//tableValueAdd();


				}
			}
		});


		opgEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();

				if(cmbSection.getValue()!=null)
				{	
					addEmployeeName(cmbSection.getValue().toString());
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


				String empId="";
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null)
					{
						if(cmbEmployee.getValue()!=null)
						{
							tableClear();
							empId = cmbEmployee.getValue().toString();							
							tableValueAdd(empId);
						}
					}
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
						tableClear();

						cmbEmployee.setEnabled(false);												
						addEmployeeName("%");
						tableValueAdd("%");

					}
					else
					{

						opgEmployee.setEnabled(true);
						cmbEmployee.setEnabled(true);						
						isUpdate=true;
						tableClear();
					}
				}
				else
				{
					tableClear();
					chkAllEmp.setValue(false);
					showNotification("Warning", "Please Select Section Name!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});



		cmbIncrement.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(cmbIncrement.getValue() != null)
				{
					cmbIncrementData();
				}
			}
		});

		nbIncrementType.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.println("Group Form");
				incrementTypeLink();				
			}
		});

		btnGroupIncrementType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtIncrementAmount.getValue().toString().trim().isEmpty() || !txtIncrPercentage.getValue().toString().trim().isEmpty())
					PercentAmountCalculation();	
			}
		});

		txtIncrPercentage.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{

				if(!txtIncrPercentage.getValue().toString().trim().isEmpty())
				{					
					txtIncrementAmount.setVisible(false);					
					btnGroupIncrementType.setVisible(true);
					cButton.btnSave.focus();
					txtIncrementAmount.setValue("");
					if(Double.parseDouble(txtIncrPercentage.getValue().toString().trim())>0)
					{
						for(int ind=0;ind<tbLblAutoEmpID.size();ind++)
						{
							if(!tbLblAutoEmpID.get(ind).getValue().toString().trim().isEmpty())
							{
								tbtxtIncrPercentage.get(ind).setValue(txtIncrPercentage.getValue().toString().trim());

							}
						}
					}
				}
				else
				{
					for(int ind=0;ind<tbtxtIncrPercentage.size();ind++)
					{
						if(!tbtxtIncrPercentage.get(ind).getValue().toString().trim().isEmpty())
						{
							tbtxtIncrPercentage.get(ind).setValue("");
						}
					}
				}
			}
		});

		txtIncrementAmount.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtIncrementAmount.getValue().toString().trim().isEmpty())
				{
					cButton.btnSave.focus();
					txtIncrPercentage.setValue("");
					btnGroupIncrementType.setVisible(false);
					if(Double.parseDouble(txtIncrementAmount.getValue().toString().trim())>0)
					{
						for(int ind=0;ind<tbLblAutoEmpID.size();ind++)
						{
							if(!tbLblAutoEmpID.get(ind).getValue().toString().trim().isEmpty())
							{
								tbtxtIncrementAmount.get(ind).setValue(txtIncrementAmount.getValue().toString().trim());
							}
						}
					}
				}
				else
				{
					for(int ind=0;ind<tbtxtIncrementAmount.size();ind++)
					{
						if(!tbtxtIncrementAmount.get(ind).getValue().toString().trim().isEmpty())
						{
							tbtxtIncrementAmount.get(ind).setValue("");
						}
					}
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				newBtnEvent();
			}
		});

		cButton.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{				
				formValidation();
			}
		});


		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{

				updateBntEvent();
			}
		});
		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				refreshBtnEvent();
			}
		});
		cButton.btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=true;
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

		cmbIncrement.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbIncrementData();
			}
		});

	}


	private void formValidation()
	{
		if(cmbDepartment.getValue()!=null )
		{
			if(cmbSection.getValue()!=null )
			{
				if (!isFind)
				{

					if(cmbEmployee.getValue()!=null || chkAllEmp.booleanValue())
					{	

						if(cmbIncrement.getValue()!=null )
						{								

							/*if(!txtIncrPercentage.getValue().toString().isEmpty() )
								{	*/							

							saveButtonEvent();
							/*		}
								else 
								{
									showNotification("Warning","Give Increment Persentage",Notification.TYPE_WARNING_MESSAGE);
											txtIncrPercentage.focus();						

								}*/
						}

						else
						{
							showNotification("Warning","Select Increment Type",Notification.TYPE_WARNING_MESSAGE);
							txtIncrementAmount.focus();
						}

					} 
					else
					{
						showNotification("Warning","Select Employee",Notification.TYPE_WARNING_MESSAGE);

					}	
				}

				else
				{

					if(cmbIncrement.getValue()!=null )
					{								
						saveButtonEvent();

					}


					else
					{
						showNotification("Warning","Select Increment Type",Notification.TYPE_WARNING_MESSAGE);
						txtIncrementAmount.focus();


					}						
				}			

			}
			else
			{
				showNotification("Warning","Select Section",Notification.TYPE_WARNING_MESSAGE);
				cmbDepartment.focus();
			}
		}
		else
		{
			showNotification("Warning","Select Department",Notification.TYPE_WARNING_MESSAGE);
			cmbSection.focus();
		}			
	}


	public void incrementTypeLink() 
	{
		Window win = new IncrementType(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{

				System.out.println("Group Form");
			}
		});

		this.getParent().addWindow(win);

	}


	private void addDepartmentData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			cmbDepartment.removeAllItems();
			/*String query="select distinct ein.vDepartmentId,dein.vDepartmentName from tbEmployeeInfo ein inner join tbDepartmentInfo "
						+ " dein on ein.vDepartmentId=dein.vDepartmentId where ISNULL(ein.vProximityId,'')!='' and ein.iStatus=1 and "
						+ " isnull(mOthersAllowance,'')!='' and vDepartmentName != 'CHO' order by dein.vDepartmentName";
			 */			


			String query="SELECT distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join tbEmployeeInfo ein on ein.vDepartmentId=dept.vDepartmentId where dept.vDepartmentName='CHO' order by dept.vDepartmentName";
			List <?> lst=session.createSQLQuery(query).list();

			System.out.println("depttt"+query);

			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbDepartment.addItem(element[0]);
					cmbDepartment.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("addDepartmentData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbIncrementData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vIncrementId,vIncrementType from tbIncrementName order by vIncrementType ";

			System.out.println("Query :"+query);

			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbIncrement.addItem(element[0]);
					cmbIncrement.setItemCaption(element[0], element[1].toString());
				}
			}
		}
		catch (Exception exp)
		{
			showNotification("addIncrementData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addSectionData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{ 
			cmbSection.removeAllItems();
			/*

				String query="select distinct ein.vSectionId,sein.SectionName from tbEmployeeInfo ein inner join tbSectionInfo "
						+ " sein on ein.vSectionId=sein.vSectionId where ein.vDepartmentID = '"+cmbDepartment.getValue().toString()+"' and "
						+ " ISNULL(ein.vProximityId,'')!='' and ein.iStatus=1 and isnull(mOthersAllowance,'')!='' and SectionName != 'CHO' order by ein.vSectionId";
			 */			

			String query=" SELECT distinct ein.vSectionId,sein.SectionName "
					+ "from tbSectionInfo sein "
					+ "inner join tbEmployeeInfo ein on ein.vSectionID=sein.vSectionID "
					+ "where ein.vDepartmentID='"+cmbDepartment+"' order by sein.SectionName";


			List <?> lst=session.createSQLQuery(query).list();

			System.out.println("Section"+query);

			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object[] element=(Object[])itr.next();
					cmbSection.addItem(element[0]);
					cmbSection.setItemCaption(element[0], element[1].toString());

				}
			}
		}
		catch (Exception exp)
		{
			showNotification("addSectionData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}


	private void addEmployeeName(String Section)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		cmbEmployee.removeAllItems();

		try
		{
			String query = "SELECT distinct vEmployeeId,employeeCode from tbEmployeeInfo " +
					"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
					"vSectionId='"+cmbSection.getValue().toString().trim()+"'order by employeeCode";
			lblEmployee.setValue("Employee ID :");

			if(opgEmployee.getValue()=="Employee Name")
			{
				query = "SELECT distinct vEmployeeId,vEmployeeName,employeeCode from tbEmployeeInfo " +
						"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
						"vSectionId='"+cmbSection.getValue().toString().trim()+"'order by employeeCode";
				lblEmployee.setValue("Employee Name :");
			}

			else if(opgEmployee.getValue()=="Proximity ID")
			{
				query = "SELECT distinct vEmployeeId,vProximityID,employeeCode from tbEmployeeInfo " +
						"where vDepartmentID='"+cmbDepartment.getValue().toString()+"' and " +
						"vSectionId='"+cmbSection.getValue().toString().trim()+"'order by employeeCode";
				lblEmployee.setValue("Proximity ID :");
			}
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator <?> itr=lst.iterator();
				while(itr.hasNext())
				{
					Object [] element=(Object[])itr.next();
					cmbEmployee.addItem(element[0]);
					cmbEmployee.setItemCaption(element[0], element[1].toString());
				}
			}
			else
				showNotification("Warning","No Employee Found!!!",Notification.TYPE_WARNING_MESSAGE);
		}
		catch (Exception exp)
		{
			showNotification("EmployeeDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}





	private void tableComponentIni()
	{
		for(int ind=0;ind<tbLblAutoEmpID.size();ind++)
		{
			if(!tbLblAutoEmpID.get(ind).getValue().toString().trim().isEmpty())
			{
				tbtxtIncrPercentage.get(ind).setEnabled(true);
				tbtxtIncrementAmount.get(ind).setEnabled(true);
			}
			else
			{
				tbtxtIncrPercentage.get(ind).setEnabled(false);
				tbtxtIncrementAmount.get(ind).setEnabled(false);

			}
		}
	}

	private void tableValueAdd(String emp)
	{

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vDesignationId,"
					+ " din.designationName,ein.vEmployeeType,ein.dInterviewDate,DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')/365 jdYear,"
					+ " DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')%365/30 jdMonth,"
					+ " DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')%365%30 jdDay,"
					+ " ein.mMonthlySalary,ein.mHouseRent,ein.mConAllowance,ein.mMedicalAllowance,mOthersAllowance from tbEmployeeInfo ein"
					+ " inner join tbDesignationInfo din on ein.vDesignationId=din.designationId where ein.vSectionId='"+cmbSection.getValue()+"' and "
					+ " ISNULL(ein.vProximityId,'')!='' and ein.iStatus=1 and ein.vEmployeeId like '"+emp+"'  and"
					+ " vEmployeeId like '"+emp+"' and vSectionId='"+cmbSection.getValue()+"'";





			/*"select distinct ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vDesignationId,"
						+ " din.designationName,ein.vEmployeeType,ein.dInterviewDate,DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')/365 jdYear,"
						+ " DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')%365/30 jdMonth,"
						+ " ein.mMonthlySalary,ein.mHouseRent,ein.mConAllowance,ein.mMedicalAllowance,mOthersAllowance from tbEmployeeInfo ein"
						+ " inner join tbDesignationInfo din on ein.vDesignationId=din.designationId where ein.vSectionId='"+cmbSection.getValue()+"' and "
						+ " ISNULL(ein.vProximityId,'')!='' and ein.iStatus=1 and ein.vEmployeeId not in (select vEmployeeID from tbSalaryIncrement) and"
						+ " vEmployeeId like '"+emp+"' and vSectionId='"+cmbSection.getValue()+"' ";*/
			/*
				String query="select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vDesignationId,"
						+ " din.designationName,ein.vEmployeeType,ein.dInterviewDate,DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')/365 jdYear,"
						+ " DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')%365/30 jdMonth,"
						+ " ein.mMonthlySalary,ein.mHouseRent,ein.mConAllowance,ein.mMedicalAllowance,mOthersAllowance from tbEmployeeInfo ein"
						+ " inner join tbDesignationInfo din on ein.vDesignationId=din.designationId where ein.vSectionId='"+cmbSection.getValue()+"' and "
						+ " ISNULL(ein.vProximityId,'')!='' and ein.iStatus=1 and ein.vEmployeeId not in (select vEmployeeID from tbSalaryIncrement where"
						+ " vEmployeeId like '"+emp+"' and vSectionId='"+cmbSection.getValue()+"' and "
						+ " MONTH(dDate) = MONTH('"+dFormat.format(dDate.getValue())+"') and YEAR(dDate) = YEAR('"+dFormat.format(dDate.getValue())+"'))";
			 */
			/*			
				String query="select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,ein.vDesignationId,"
						+ " din.designationName,ein.vEmployeeType,ein.dInterviewDate,DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')/365 jdYear,"
						+ " DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')%365/30 jdMonth,"
						+ " ein.mMonthlySalary,ein.mHouseRent,ein.mConAllowance,ein.mMedicalAllowance,mOthersAllowance from tbEmployeeInfo ein"
						+ " inner join tbDesignationInfo din on ein.vDesignationId=din.designationId where ein.vSectionId='"+cmbSection.getValue()+"' and "
						+ " ISNULL(ein.vProximityId,'')!='' and ein.iStatus=1 and ein.vEmployeeId not in (select vEmployeeID from tbSalaryIncrement where"
						+ " vEmployeeId like '"+emp+"' and vSectionId='"+cmbSection.getValue()+"' and "
						+ " MONTH(dDate) = MONTH('"+dFormat.format(dDate.getValue())+"') and YEAR(dDate) = YEAR('"+dFormat.format(dDate.getValue())+"'))";*/


			tableClear();
			System.out.println("tableValueAdd"+query);

			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				int ind=0;
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[])itr.next();
					tbLblAutoEmpID.get(ind).setValue(element[0]);
					tbLblEmployeeID.get(ind).setValue(element[1]);
					tbLblProximityID.get(ind).setValue(element[2]);
					tbLblEmployeeName.get(ind).setValue(element[3]);
					tbLblDesignationID.get(ind).setValue(element[4]);
					tbLblDesignation.get(ind).setValue(element[5]);
					tblblEmployeeType.get(ind).setValue(element[6]);
					tblblInterviewDate.get(ind).setValue(dateFormat.format(element[7]));
					tblblServiceOfLength.get(ind).setValue(element[8].toString()+"y "+element[9].toString()+"m "+element[10].toString()+"d");
					tbtLblBasic.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[11].toString())));
					tbLblHouseRent.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[12].toString())));
					tbLblConveyance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[13].toString())));
					tbLblMedicalAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[14].toString())));
					tbLblGross.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[15].toString())));

					if(ind==tbLblAutoEmpID.size()-1)
					{
						tableRowAdd(ind+1);
					}

					ind++;
				}
			}
			else
			{
				showNotification("Warning", "No Employee Found or Data already exists!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception exp)
		{
			showNotification("tableValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		tableComponentIni();
	}

	private void focuMove()
	{
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(cmbEmployee);
		allComp.add(cmbIncrement);
		allComp.add(txtIncrPercentage);
		allComp.add(txtIncrementAmount);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this, allComp);
	}

	private void txtClear()
	{
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbEmployee.setValue(null);
		chkAllEmp.setValue(false);
		txtIncrPercentage.setValue("");
		txtIncrementAmount.setValue("");
		cmbIncrement.setValue(null);
		tableClear();
	}

	private void tableClear()
	{
		for(int ind=0;ind<tbLblAutoEmpID.size();ind++)
		{
			if(!tbLblAutoEmpID.get(ind).getValue().toString().trim().isEmpty())
			{
				tbLblAutoEmpID.get(ind).setValue("");
				tbLblEmployeeID.get(ind).setValue("");
				tbLblProximityID.get(ind).setValue("");
				tbLblEmployeeName.get(ind).setValue("");
				tbLblDesignation.get(ind).setValue("");
				tblblEmployeeType.get(ind).setValue("");
				tblblInterviewDate.get(ind).setValue("");
				tblblServiceOfLength.get(ind).setValue("");
				tbtLblBasic.get(ind).setValue("");
				tbLblHouseRent.get(ind).setValue("");
				tbLblConveyance.get(ind).setValue("");
				tbLblMedicalAllowance.get(ind).setValue("");
				tbLblGross.get(ind).setValue("");
				tbLblIncPer.get(ind).setValue("");
				tbtxtIncrPercentage.get(ind).setValue("");
				tbLblIncAmt.get(ind).setValue("");
				tbtxtIncrementAmount.get(ind).setValue("");
				tbLblNewBasic.get(ind).setValue("");
				tbLblNewGross.get(ind).setValue("");
			}
		}
	}

	private void PercentAmountCalculation()
	{
		for(int q=0;q<tbLblAutoEmpID.size();q++)
		{
			if(!tbLblAutoEmpID.get(q).getValue().toString().trim().isEmpty())
			{
				tbLblIncPer.get(q).setValue("");
				tbLblIncAmt.get(q).setValue("");
				double basic = Double.parseDouble(tbtLblBasic.get(q).toString().isEmpty()?"0":tbtLblBasic.get(q).toString().replaceAll(",", "").trim());
				double Gross = Double.parseDouble(tbLblGross.get(q).toString().isEmpty()?"0":tbLblGross.get(q).toString().replaceAll(",", "").trim());
				if(!tbtxtIncrPercentage.get(q).getValue().toString().trim().isEmpty())
				{
					table.setColumnCollapsed("Incr_(%)", true);
					table.setColumnCollapsed("Incr(%)", false);
					table.setColumnCollapsed("Incr Amt", true);
					table.setColumnCollapsed("Incr_Amt", false);
					double incrementPercentage = Double.parseDouble(tbtxtIncrPercentage.get(q).toString().trim().isEmpty()?"0":tbtxtIncrPercentage.get(q).toString().replaceAll(",", "").trim());

					if(btnGroupIncrementType.getValue()=="Basic")
					{
						double incrementAmount = (basic*incrementPercentage)/100;
						tbLblIncPer.get(q).setValue(twoDigit.format(incrementPercentage));
						tbLblIncAmt.get(q).setValue(twoDigit.format(incrementAmount));
					}
					else
					{
						double incrementAmount = (Gross*incrementPercentage)/100;
						tbLblIncPer.get(q).setValue(twoDigit.format(incrementPercentage));
						tbLblIncAmt.get(q).setValue(twoDigit.format(incrementAmount));
					}
				}
				else if(!tbtxtIncrementAmount.get(q).getValue().toString().trim().isEmpty())
				{
					table.setColumnCollapsed("Incr_(%)", false);
					table.setColumnCollapsed("Incr(%)", true);
					table.setColumnCollapsed("Incr Amt", false);
					table.setColumnCollapsed("Incr_Amt", true);
					double incrementAmount = Double.parseDouble(tbtxtIncrementAmount.get(q).toString().trim().isEmpty()?"0":tbtxtIncrementAmount.get(q).toString().replaceAll(",", "").trim());
					double incrementPercentage = (incrementAmount/Gross)*100;
					tbLblIncPer.get(q).setValue(twoDigit.format(incrementPercentage));
					tbLblIncAmt.get(q).setValue(twoDigit.format(incrementAmount));
				}
			}
		}
	}

	private void tbPercentAmountCalculation(int ind)
	{
		if(!tbLblAutoEmpID.get(ind).getValue().toString().trim().isEmpty())
		{
			tbLblIncPer.get(ind).setValue("");
			tbLblIncAmt.get(ind).setValue("");
			double basic = Double.parseDouble(tbtLblBasic.get(ind).toString().isEmpty()?"0":tbtLblBasic.get(ind).toString().replaceAll(",", "").trim());
			double Gross = Double.parseDouble(tbLblGross.get(ind).toString().isEmpty()?"0":tbLblGross.get(ind).toString().replaceAll(",", "").trim());
			if(!tbtxtIncrPercentage.get(ind).getValue().toString().trim().isEmpty())
			{
				table.setColumnCollapsed("Incr(%)", false);
				table.setColumnCollapsed("Incr_(%)", true);
				table.setColumnCollapsed("Incr Amt", true);
				table.setColumnCollapsed("Incr_Amt", false);
				double incrementPercentage = Double.parseDouble(tbtxtIncrPercentage.get(ind).toString().trim().isEmpty()?"0":tbtxtIncrPercentage.get(ind).toString().replaceAll(",", "").trim());

				if(btnGroupIncrementType.getValue()=="Basic")
				{
					double incrementAmount = (basic*incrementPercentage)/100;
					tbLblIncPer.get(ind).setValue(twoDigit.format(incrementPercentage));
					tbLblIncAmt.get(ind).setValue(twoDigit.format(incrementAmount));
				}
				else
				{
					double incrementAmount = (Gross*incrementPercentage)/100;
					tbLblIncPer.get(ind).setValue(twoDigit.format(incrementPercentage));
					tbLblIncAmt.get(ind).setValue(twoDigit.format(incrementAmount));
				}
			}
			else if(!tbtxtIncrementAmount.get(ind).getValue().toString().trim().isEmpty())
			{
				table.setColumnCollapsed("Incr(%)", true);
				table.setColumnCollapsed("Incr_(%)", false);
				table.setColumnCollapsed("Incr Amt", false);
				table.setColumnCollapsed("Incr_Amt", true);
				double incrementAmount = Double.parseDouble(tbtxtIncrementAmount.get(ind).toString().trim().isEmpty()?"0":tbtxtIncrementAmount.get(ind).toString().replaceAll(",", "").trim());
				double incrementPercentage = (incrementAmount/Gross)*100;
				tbLblIncPer.get(ind).setValue(twoDigit.format(incrementPercentage));
				tbLblIncAmt.get(ind).setValue(twoDigit.format(incrementAmount));
			}
		}
	}
	private void columnAdjustment(int ind)
	{
		if(tbtxtIncrementAmount.get(ind).getValue().toString().trim().isEmpty() && tbtxtIncrPercentage.get(ind).getValue().toString().trim().isEmpty())
		{
			table.setColumnCollapsed("Incr(%)", false);
			table.setColumnCollapsed("Incr_(%)", true);
			table.setColumnCollapsed("Incr Amt", false);
			table.setColumnCollapsed("Incr_Amt", true);
		}
	}

	private void newBtnEvent()
	{
		isUpdate = false;
		isFind = false;	
		txtClear();
		componentIni(false);
		btnIni(false);
		tableComponentIni();
	}

	private void updateBntEvent()
	{
		isUpdate = true;
		isFind=true;
		componentIni(true);
		btnIni(false);
		txtIncrPercentage.setEnabled(true);
		cmbIncrement.setEnabled(true);
		cmbEmployee.setEnabled(true);
		table.setEnabled(true);
		cButton.btnSave.setEnabled(false);
	}

	private void refreshBtnEvent()
	{
		isUpdate = false;
		txtClear();
		componentIni(true);
		btnIni(true);
	}

	private void tableinitialize()
	{
		for(int i=0;i<13;i++)
			tableRowAdd(i);
	}

	private boolean existDataCheck(String query)
	{
		boolean ret = false;
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				ret = true;
		}
		catch (Exception exp)
		{
			showNotification("saveButtonEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}

	private void saveButtonEvent()
	{
		if(isUpdate && sessionBean.isUpdateable())
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
							String showNotify = "All Information Updated Successfully";
							deleteData(session);
							insertData(session,showNotify);
							isUpdate = false;
							txtClear();
							componentIni(true);
							btnIni(true);
							tx.commit();

						}
						catch(Exception exp)
						{

							tx.rollback();
							showNotification("Warning", exp.toString(),Notification.TYPE_WARNING_MESSAGE);
						}
						finally{session.close();}
					}
				}
			});

		}

		else if (sessionBean.isUpdateable())
		{
			String employeeid="";
			String firstemployeeid=tbLblAutoEmpID.get(0).getValue().toString();
			employeeid= "'"+firstemployeeid+"'";

			for(int i=1;i<tbLblAutoEmpID.size();i++)
			{
				if(!tbLblAutoEmpID.get(i).getValue().toString().isEmpty())
				{
					String prefix=tbLblAutoEmpID.get(i).getValue().toString();
					//prefix="'+prefix+'";

					employeeid=employeeid+","+"'"+prefix+"'";
				}
			}

			//String query1 ="select * from tbSalaryIncrement where vSectionID like '"+cmbSection.getValue()+"' and CONVERT(Date,dDate,105)='"+dFormat.format(dDate.getValue())+"'";


			String query1="select * from tbSalaryIncrement where vSectionId like '"+cmbSection.getValue().toString()+"' and CONVERT(Date,dDate,105)='"+dFormat.format(dDate.getValue())+"' "
					+"and vIncrementId like '"+cmbIncrement.getValue().toString()+"' and vEmployeeId in ("+employeeid+") ";

			System.out.println("Desire query is :"+query1);


			if(!existDataCheck(query1))
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
								String showNotify = "All Information Saved Successfully";
								insertData(session,showNotify);
								txtClear();
								componentIni(true);
								btnIni(true);
								tx.commit();
							}
							catch(Exception exp)
							{
								tx.rollback();
								showNotification("Warning", exp.toString(),Notification.TYPE_WARNING_MESSAGE);
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
	}

	private void deleteData(Session session)
	{
		String Insertquery = "insert into tbUDSalaryIncrement (dDate,vEmployeeId,employeeCode,vProximityId,vEmployeeName,"
				+ " vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationId,vDesignationName,vEmployeeType,"
				+ " mBasic,mHouseRent,mConveyance,mMedicalAllowance,mGross,vIncrementPercentage,mIncrementAmount,mNewBasic,"
				+ " mNewHouseRent,mNewConveyance,mNewMedicalAllowance,mNewGross,iApproved_FLag,vUDFlag,vUserName,vUserIP,"
				+ " dEntryTime,vIncrementType,vIncrementId,vBasedOnIncrement,vIncrementStatus)"
				+ " select dDate,vEmployeeId,employeeCode,vProximityId,vEmployeeName,vDepartmentID,vDepartmentName,"
				+ " vSectionId,vSectionName,vDesignationId,vDesignationName,vEmployeeType,mBasic,mHouseRent,mConveyance,"
				+ " mMedicalAllowance,mGross,vIncrementPercentage,mIncrementAmount,mNewBasic,mNewHouseRent,mNewConveyance,"
				+ " mNewMedicalAllowance,mNewGross,iApproved_FLag,'OLD',vUserName,vUserIP,dEntryTime,vIncrementType, "
				+ "vIncrementId,vBasedOnIncrement,vIncrementStatus from tbSalaryIncrement "
				+ " where MONTH(dDate)=MONTH('"+dFormat.format(dDate.getValue())+"') and "
				+ " YEAR(dDate)=YEAR('"+dFormat.format(dDate.getValue())+"') and vSectionID='"+cmbSection.getValue()+"'";
		session.createSQLQuery(Insertquery).executeUpdate();
		System.out.println(Insertquery);

		// Main table update
		String updateUnit = "UPDATE tbUDSalaryIncrement set "
				+ "vIncrementId ='"+cmbIncrement.getValue().toString()+"', " +
				" vIncrementType = '"+cmbIncrement.getItemCaption(cmbIncrement.getValue().toString())+"',  "+
				" vUserName = '"+sessionBean.getUserName()+"'," +
				" vUserIp = '"+sessionBean.getUserIp()+"'," +
				" dEntryTime = GETDATE() " +
				" where vIncrementId = '"+IncrementDate.getValue().toString()+"' ";

		System.out.println(updateUnit);

		System.out.println("Delete data"+Insertquery);

		System.out.println("Delete2222"+Insertquery);

		session.createSQLQuery("delete from tbSalaryIncrement where Day(dDate)=Day('"+dFormat.format(dDate.getValue())+"') and Month(dDate)=MONTH('"+dFormat.format(dDate.getValue())+"')"
				+ " and YEAR(dDate)=YEAR('"+dFormat.format(dDate.getValue())+"') and vSectionID='"+cmbSection.getValue()+"'").executeUpdate();

	}

	private void insertData(Session session,String showNotify)
	{
		String incrementStatus="";
		for(int i=0; i<tbLblAutoEmpID.size();i++)
		{
			if(!tbLblAutoEmpID.get(i).getValue().toString().isEmpty())
			{
				if(txtIncrementAmount.getValue()!="")
				{
					incrementStatus="By Amount";
				}
				else
				{
					incrementStatus="By Percentage";
				}
				String empInfoQuery = "insert into tbUDEmployeeInfo (vEmployeeId,employeeCode,iFingerID,vProximityId,"
						+ " vEmployeeName,vEmployeeNameBan,vReligion,vContact,vEmail,vGender,dDateOfBirth,DateOfBirthLocation,"
						+ " vNationality,nId,nIdLocation,vEmployeeType,dApplicationDate,applicationDateLocation,dInterviewDate,"
						+ " dJoiningDate,joiningDateLocation,dConfirmationDate,confirmationDateLocation,vStatus,iStatus,dStatusDate,"
						+ " vDepartmentId,vSectionId,vDesignationId,vFloor,vLine,vGrade,imageLocation,vFatherName,vMotherName,"
						+ " vPermanentAddress,vMailingAddress,vBloodGroup,vMeritalStatus,dMarriageDate,vSpouseName,vSpouseOccupation,"
						+ " iNoOfChild,vN1Name,vN1Relation,vN2Name,vN2Relation,vOtherQualification,vComputerSkill,mMonthlySalary,"
						+ " vUserName,dDateTime,vPcIp,birthImage,nidImage,applicationImage,joiningImage,confirmImage,subUnitId,"
						+ " mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mNonPractice,mOthersAllowance,"
						+ " mProvidentFund,mRoomCharge,mBreej,mKhichuri,mKFund,bankId,bankName,bankBranchId,branchName,accountNo,"
						+ " userId,userIp,entryTime,FridayStatus,FridayLunchFee,isDelete,DeleteDate,mDearnessAllowance,mFireAllowance,"
						+ " vUDFlag,vMobileBankFlag,vTinNo,vTinImageLocation,vCircle,vCircleImageLocation,vZone,vZoneImageLocation) "
						+ " select vEmployeeId,employeeCode,iFingerID,vProximityId,vEmployeeName,vEmployeeNameBan,vReligion,vContact,"
						+ " vEmail,vGender,dDateOfBirth,DateOfBirthLocation,vNationality,nId,nIdLocation,vEmployeeType,dApplicationDate,"
						+ " applicationDateLocation,dInterviewDate,dJoiningDate,joiningDateLocation,dConfirmationDate,confirmationDateLocation,"
						+ " vStatus,iStatus,dStatusDate,vDepartmentId,vSectionId,vDesignationId,vFloor,vLine,vGrade,imageLocation,vFatherName,"
						+ " vMotherName,vPermanentAddress,vMailingAddress,vBloodGroup,vMeritalStatus,dMarriageDate,vSpouseName,vSpouseOccupation,"
						+ " iNoOfChild,vN1Name,vN1Relation,vN2Name,vN2Relation,vOtherQualification,vComputerSkill,mMonthlySalary,"
						+ " vUserName,dDateTime,vPcIp,birthImage,nidImage,applicationImage,joiningImage,confirmImage,subUnitId,"
						+ " mHouseRent,OtStatus,mMedicalAllowance,mConAllowance,mClinical,mSpecial,mNonPractice,mOthersAllowance,"
						+ " mProvidentFund,mRoomCharge,mBreej,mKhichuri,mKFund,bankId,bankName,bankBranchId,branchName,accountNo,"
						+ " userId,userIp,entryTime,FridayStatus,FridayLunchFee,isDelete,DeleteDate,mDearnessAllowance,mFireAllowance,"
						+ " 'UPDATE',vMobileBankFlag,vTinNo,vTinImageLocation,vCircle,vCircleImageLocation,vZone,vZoneImageLocation from "
						+ " tbEmployeeInfo where vEmployeeId = '"+tbLblAutoEmpID.get(i).getValue().toString()+"'";

				session.createSQLQuery(empInfoQuery).executeUpdate();
				System.out.print("empInfoQuery"+empInfoQuery);


				String empInfoUpdate = "update tbEmployeeInfo set mMonthlySalary = '"+tbLblNewBasic.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " mHouseRent = '"+tbLblNewHouseRent.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " mConAllowance = '"+tbLblNewConveyance.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " mMedicalAllowance = '"+tbLblNewMedicalAllowance.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " mOthersAllowance = '"+tbLblNewGross.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " vUserName = '"+sessionBean.getUserName()+"', dDateTime = GETDATE(), vPcIp = '"+sessionBean.getUserIp()+"',"
						+ " userId = '"+sessionBean.getUserName()+"', userIp = '"+sessionBean.getUserIp()+"', entryTime = GETDATE() where "
						+ " vEmployeeId = '"+tbLblAutoEmpID.get(i).getValue().toString()+"'";

				System.out.print("Emp"+empInfoUpdate);


				session.createSQLQuery(empInfoUpdate).executeUpdate();

				String query="insert into tbSalaryIncrement (dDate,vEmployeeId,employeeCode,vProximityId,vEmployeeName,"
						+ " vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationId,vDesignationName,"
						+ " vEmployeeType,mBasic,mHouseRent,mConveyance,mMedicalAllowance,mGross,vIncrementPercentage,"
						+ " mIncrementAmount,vIncrementId,vIncrementType,mNewBasic,mNewHouseRent,mNewConveyance,mNewMedicalAllowance,mNewGross,"
						+ " iApproved_FLag,vUserName,vUserIP,dEntryTime,vBasedOnIncrement,vIncrementStatus) values "		
						+ " ('"+dFormat.format(dDate.getValue())+"',"
						+ " '"+tbLblAutoEmpID.get(i).getValue().toString()+"',"
						+ " '"+tbLblEmployeeID.get(i).getValue().toString()+"',"
						+ " '"+tbLblProximityID.get(i).getValue().toString()+"',"
						+ " '"+tbLblEmployeeName.get(i).getValue().toString()+"',"
						+ " '"+cmbDepartment.getValue().toString()+"',"
						+ " '"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"',"
						+ " '"+cmbSection.getValue().toString()+"',"
						+ " '"+cmbSection.getItemCaption(cmbSection.getValue())+"',"					
						+ " '"+tbLblDesignationID.get(i).getValue().toString()+"',"
						+ " '"+tbLblDesignation.get(i).getValue().toString()+"',"
						+ " '"+tblblEmployeeType.get(i).getValue().toString()+"',"
						+ " '"+tbtLblBasic.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblHouseRent.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblConveyance.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblMedicalAllowance.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblGross.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblIncPer.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblIncAmt.get(i).getValue().toString().replaceAll(",", "")+"',"						
						+ " '"+cmbIncrement.getValue().toString()+"',"
						+ " '"+cmbIncrement.getItemCaption(cmbIncrement.getValue())+"',"					
						+ " '"+tbLblNewBasic.get(i).getValue().toString().replaceAll(",", "")+"', "
						+ " '"+tbLblNewHouseRent.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblNewConveyance.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblNewMedicalAllowance.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '"+tbLblNewGross.get(i).getValue().toString().replaceAll(",", "")+"',"
						+ " '0','"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getdate(),'"+btnGroupIncrementType.getValue().toString()+"','"+incrementStatus+"')";

				session.createSQLQuery(query).executeUpdate();
				showNotification(showNotify);

				System.out.println("queerry"+query);
			}
		}

		if (isUpdate)
		{
			String Insertquery="";

			Insertquery = "insert into tbUDSalaryIncrement (dDate,vEmployeeId,employeeCode,vProximityId,vEmployeeName,"
					+ " vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationId,vDesignationName,vEmployeeType,"
					+ " mBasic,mHouseRent,mConveyance,mMedicalAllowance,mGross,vIncrementPercentage,mIncrementAmount,mNewBasic,"
					+ " mNewHouseRent,mNewConveyance,mNewMedicalAllowance,mNewGross,iApproved_FLag,vUDFlag,vUserName,vUserIP,"
					+ " dEntryTime,vIncrementType,vIncrementId,vBasedOnIncrement,vIncrementStatus )"
					+ " select dDate,vEmployeeId,employeeCode,vProximityId,vEmployeeName,vDepartmentID,vDepartmentName,"
					+ " vSectionId,vSectionName,vDesignationId,vDesignationName,vEmployeeType,mBasic,mHouseRent,mConveyance,"
					+ " mMedicalAllowance,mGross,vIncrementPercentage,mIncrementAmount,mNewBasic,mNewHouseRent,mNewConveyance,"
					+ " mNewMedicalAllowance,mNewGross,iApproved_FLag,'UPDATE',vUserName,vUserIP,dEntryTime,vIncrementType= '"+cmbIncrement.getItemCaption(cmbIncrement.getValue().toString())+"',"
					+ "vIncrementId ='"+cmbIncrement.getValue().toString()+"','"+btnGroupIncrementType.getValue().toString()+"','"+incrementStatus+"' from tbSalaryIncrement "
					+ " where MONTH(dDate)=MONTH('"+dFormat.format(dDate.getValue())+"') and "
					+ " YEAR(dDate)=YEAR('"+dFormat.format(dDate.getValue())+"') and vSectionID='"+cmbSection.getValue()+"'";
			session.createSQLQuery(Insertquery).executeUpdate(); 	
		}

	}

	private void findbuttonEvent()
	{
		Window win = new IncrementProcess_CHOFind(sessionBean,IncrementDate,SectionID,IncrementType,EmployeeName);
		win.addListener(new CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(!IncrementDate.getValue().toString().trim().isEmpty() && !SectionID.getValue().toString().trim().isEmpty() && !IncrementType.getValue().toString().trim().isEmpty() && !EmployeeName.getValue().toString().trim().isEmpty() )
				{
					txtClear();
					findInitialize(IncrementDate.getValue().toString().trim(), SectionID.getValue().toString().trim(),IncrementType.getValue().toString().trim(),EmployeeName.getValue().toString().trim());


				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialize(String incDate, String Section,String incType,String empName)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select sinf.dDate,sinf.vEmployeeId,sinf.employeeCode,sinf.vProximityId,sinf.vEmployeeName,sinf.vDepartmentId,"
					+ " sinf.vSectionId,sinf.vIncrementId,sinf.vDesignationId,sinf.vDesignationName,sinf.vEmployeeType,sinf.mBasic,sinf.mHouseRent,"
					+ " sinf.mConveyance,sinf.mMedicalAllowance,sinf.mGross,sinf.vIncrementPercentage,ein.dInterviewDate,"
					+ " DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')/365 jdYear,"
					+ "DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(dDate.getValue())+"')%365/30 jdMonth "
					+ " from tbSalaryIncrement sinf inner join tbEmployeeInfo ein on "
					+ " sinf.vEmployeeId = ein.vEmployeeId where sinf.vSectionId='"+Section+"' and Day(dDate) = Day('"+incDate+"') and MONTH(dDate)= MONTH('"+incDate+"')  and YEAR(dDate) = YEAR('"+incDate+"') and vIncrementType='"+incType+"' and ein.vEmployeeName='"+empName+"'";



			System.out.println("findInitialize"+query);



			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				int ind=0;
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{

					Object [] element=(Object[])itr.next();
					if(ind == 0)
					{
						cmbDepartment.setValue(element[5]);
						cmbSection.setValue(element[6]);
						cmbIncrement.setValue(element[7]);
						dDate.setValue(element[0]);
						tableClear();
					}
					tbLblAutoEmpID.get(ind).setValue(element[1]);
					tbLblEmployeeID.get(ind).setValue(element[2]);
					tbLblProximityID.get(ind).setValue(element[3]);
					tbLblEmployeeName.get(ind).setValue(element[4]);
					tbLblDesignationID.get(ind).setValue(element[8]);
					tbLblDesignation.get(ind).setValue(element[9]);
					tblblEmployeeType.get(ind).setValue(element[10]);
					tbtLblBasic.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[11].toString())));
					tbLblHouseRent.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[12].toString())));
					tbLblConveyance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[13].toString())));
					tbLblMedicalAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[14].toString())));
					tbLblGross.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[15].toString())));
					tbtxtIncrPercentage.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[16].toString())));
					tblblInterviewDate.get(ind).setValue(dateFormat.format(element[17]));
					tblblServiceOfLength.get(ind).setValue(element[18].toString()+"y "+element[19].toString()+"m");

					if(ind==tbLblAutoEmpID.size()-1)
					{
						tableRowAdd(ind+1);
					}

					ind++;
				}
			}
			else
			{
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception exp)
		{
			showNotification("findInitialize", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		tableComponentIni();
	}

	private void calculateNewAmount(int ind)
	{
		if(!tbLblIncAmt.get(ind).getValue().toString().isEmpty())
		{
			double oldGross = Double.parseDouble(tbLblGross.get(ind).getValue().toString().replaceAll(",", ""));
			double incAmount = Double.parseDouble(tbLblIncAmt.get(ind).getValue().toString().replaceAll(",", ""));
			double newGross = oldGross + incAmount;
			tbLblNewGross.get(ind).setValue(twoDigit.format(newGross));
			if(!tblblEmployeeType.get(ind).getValue().toString().equalsIgnoreCase("Casual"))
			{
				tbLblNewBasic.get(ind).setValue(twoDigit.format(newGross*0.60));
				tbLblNewHouseRent.get(ind).setValue(twoDigit.format(newGross*0.25));
				tbLblNewConveyance.get(ind).setValue(twoDigit.format(newGross*0.075));
				tbLblNewMedicalAllowance.get(ind).setValue(twoDigit.format(newGross*0.075));
			}
			else
			{
				tbLblNewBasic.get(ind).setValue("0.00");
				tbLblNewHouseRent.get(ind).setValue("0.00");
				tbLblNewConveyance.get(ind).setValue("0.00");
				tbLblNewMedicalAllowance.get(ind).setValue("0.00");
			}
		}
		else
		{
			tbLblNewGross.get(ind).setValue("");
			tbLblNewBasic.get(ind).setValue("");
			tbLblNewHouseRent.get(ind).setValue("");
			tbLblNewConveyance.get(ind).setValue("");
			tbLblNewMedicalAllowance.get(ind).setValue("");
		}
	}

	private void tableRowAdd(final int ar)
	{
		tbLblSl.add(ar, new Label());
		tbLblSl.get(ar).setWidth("100%");
		tbLblSl.get(ar).setHeight("20px");
		tbLblSl.get(ar).setValue(ar+1);

		tbLblAutoEmpID.add(ar, new Label());
		tbLblAutoEmpID.get(ar).setWidth("100%");
		tbLblAutoEmpID.get(ar).setHeight("20px");

		tbLblEmployeeID.add(ar, new Label());
		tbLblEmployeeID.get(ar).setWidth("100%");

		tbLblProximityID.add(ar, new Label());
		tbLblProximityID.get(ar).setWidth("100%");

		tbLblEmployeeName.add(ar, new Label());
		tbLblEmployeeName.get(ar).setWidth("100%");

		tbLblDesignationID.add(ar, new Label());
		tbLblDesignationID.get(ar).setWidth("100%");

		tbLblDesignation.add(ar, new Label());
		tbLblDesignation.get(ar).setWidth("100%");

		tblblEmployeeType.add(ar, new Label());
		tblblEmployeeType.get(ar).setWidth("100%");

		tblblInterviewDate.add(ar, new Label());
		tblblInterviewDate.get(ar).setWidth("100%");

		tblblServiceOfLength.add(ar, new Label());
		tblblServiceOfLength.get(ar).setWidth("100%");

		tbtLblBasic.add(ar, new Label());
		tbtLblBasic.get(ar).setWidth("100%");
		tbtLblBasic.get(ar).setImmediate(true);

		tbLblHouseRent.add(ar, new Label());
		tbLblHouseRent.get(ar).setWidth("100%");
		tbLblHouseRent.get(ar).setImmediate(true);

		tbLblConveyance.add(ar, new Label());
		tbLblConveyance.get(ar).setWidth("100%");
		tbLblConveyance.get(ar).setImmediate(true);

		tbLblMedicalAllowance.add(ar, new Label());
		tbLblMedicalAllowance.get(ar).setWidth("100%");
		tbLblMedicalAllowance.get(ar).setImmediate(true);

		tbLblGross.add(ar, new Label());
		tbLblGross.get(ar).setWidth("100%");

		tbLblIncPer.add(ar, new Label());
		tbLblIncPer.get(ar).setWidth("100%");

		tbtxtIncrPercentage.add(ar, new AmountField(""));
		tbtxtIncrPercentage.get(ar).setWidth("100%");
		tbtxtIncrPercentage.get(ar).setImmediate(true);
		tbtxtIncrPercentage.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbtxtIncrPercentage.get(ar).getValue().toString().trim().isEmpty() 
						&& Double.parseDouble(tbtxtIncrPercentage.get(ar).getValue().toString().trim())>0)
				{
					tbPercentAmountCalculation(ar);
				}
				else
				{
					tbtxtIncrPercentage.get(ar).setValue("");
					tbLblIncPer.get(ar).setValue("");
					tbLblIncAmt.get(ar).setValue("");
					columnAdjustment(ar);
				}
			}
		});

		tbLblIncAmt.add(ar, new Label());
		tbLblIncAmt.get(ar).setWidth("100%");
		tbLblIncAmt.get(ar).setImmediate(true);
		tbLblIncAmt.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				calculateNewAmount(ar);
			}
		});

		tbtxtIncrementAmount.add(ar, new AmountField(""));
		tbtxtIncrementAmount.get(ar).setWidth("100%");
		tbtxtIncrementAmount.get(ar).setImmediate(true);
		tbtxtIncrementAmount.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbtxtIncrementAmount.get(ar).getValue().toString().trim().isEmpty() 
						&& Double.parseDouble(tbtxtIncrementAmount.get(ar).getValue().toString().trim())>0)
				{
					tbPercentAmountCalculation(ar);
				}
				else
				{
					tbtxtIncrementAmount.get(ar).setValue("");
					tbLblIncPer.get(ar).setValue("");
					tbLblIncAmt.get(ar).setValue("");
					columnAdjustment(ar);
				}
			}
		});

		tbLblNewBasic.add(ar, new Label());
		tbLblNewBasic.get(ar).setWidth("100%");
		tbLblNewBasic.get(ar).setImmediate(true);

		tbLblNewHouseRent.add(ar, new Label());
		tbLblNewHouseRent.get(ar).setWidth("100%");
		tbLblNewHouseRent.get(ar).setImmediate(true);

		tbLblNewConveyance.add(ar, new Label());
		tbLblNewConveyance.get(ar).setWidth("100%");
		tbLblNewConveyance.get(ar).setImmediate(true);

		tbLblNewMedicalAllowance.add(ar, new Label());
		tbLblNewMedicalAllowance.get(ar).setWidth("100%");
		tbLblNewMedicalAllowance.get(ar).setImmediate(true);

		tbLblNewGross.add(ar, new Label());
		tbLblNewGross.get(ar).setWidth("100%");

		tbBtndel.add(ar, new NativeButton());
		tbBtndel.get(ar).setWidth("100%");
		tbBtndel.get(ar).setImmediate(true);
		tbBtndel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		tbBtndel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tbLblAutoEmpID.get(ar).setValue("");
				tbLblEmployeeID.get(ar).setValue("");
				tbLblProximityID.get(ar).setValue("");
				tbLblEmployeeName.get(ar).setValue("");
				tbLblDesignationID.get(ar).setValue("");
				tbLblDesignation.get(ar).setValue("");
				tblblEmployeeType.get(ar).setValue("");
				tblblInterviewDate.get(ar).setValue("");
				tblblServiceOfLength.get(ar).setValue("");
				tbtLblBasic.get(ar).setValue("");
				tbLblHouseRent.get(ar).setValue("");
				tbLblConveyance.get(ar).setValue("");
				tbLblMedicalAllowance.get(ar).setValue("");
				tbLblGross.get(ar).setValue("");
				tbtxtIncrPercentage.get(ar).setValue("");
				tbLblIncPer.get(ar).setValue("");
				tbtxtIncrementAmount.get(ar).setValue("");
				tbLblIncAmt.get(ar).setValue("");
				tbLblNewBasic.get(ar).setValue("");
				tbLblNewHouseRent.get(ar).setValue("");
				tbLblNewConveyance.get(ar).setValue("");
				tbLblNewMedicalAllowance.get(ar).setValue("");
				tbLblNewGross.get(ar).setValue("");

				for(int ind = ar; ind < tbLblAutoEmpID.size()-1; ind++)
				{
					tbLblAutoEmpID.get(ind).setValue(tbLblAutoEmpID.get(ind+1).getValue());
					tbLblEmployeeID.get(ind).setValue(tbLblEmployeeID.get(ind+1).getValue());
					tbLblProximityID.get(ind).setValue(tbLblProximityID.get(ind+1).getValue());
					tbLblEmployeeName.get(ind).setValue(tbLblEmployeeName.get(ind+1).getValue());
					tbLblDesignationID.get(ind).setValue(tbLblDesignationID.get(ind+1).getValue());
					tbLblDesignation.get(ind).setValue(tbLblDesignation.get(ind+1).getValue());
					tblblEmployeeType.get(ind).setValue(tblblEmployeeType.get(ind+1).getValue());
					tblblInterviewDate.get(ind).setValue(tblblInterviewDate.get(ind+1).getValue());
					tblblServiceOfLength.get(ind).setValue(tblblServiceOfLength.get(ind+1).getValue());
					tbtLblBasic.get(ind).setValue(tbtLblBasic.get(ind+1).getValue());
					tbLblHouseRent.get(ind).setValue(tbLblHouseRent.get(ind+1).getValue());
					tbLblConveyance.get(ind).setValue(tbLblConveyance.get(ind+1).getValue());
					tbLblMedicalAllowance.get(ind).setValue(tbLblMedicalAllowance.get(ind+1).getValue());
					tbLblGross.get(ind).setValue(tbLblGross.get(ind+1).getValue());
					tbtxtIncrPercentage.get(ind).setValue(tbtxtIncrPercentage.get(ind+1).getValue());
					tbLblIncPer.get(ind).setValue(tbLblIncPer.get(ind+1).getValue());
					tbtxtIncrementAmount.get(ind).setValue(tbtxtIncrementAmount.get(ind+1).getValue());
					tbLblIncAmt.get(ind).setValue(tbLblIncAmt.get(ind+1).getValue());
					tbLblNewBasic.get(ind).setValue(tbLblNewBasic.get(ind+1).getValue());
					tbLblNewHouseRent.get(ind).setValue(tbLblNewHouseRent.get(ind+1).getValue());
					tbLblNewConveyance.get(ind).setValue(tbLblNewConveyance.get(ind+1).getValue());
					tbLblNewMedicalAllowance.get(ind).setValue(tbLblNewMedicalAllowance.get(ind+1).getValue());
					tbLblNewGross.get(ind).setValue(tbLblNewGross.get(ind+1).getValue());

				}
			}
		});

		table.addItem(new Object[]{tbLblSl.get(ar),tbLblAutoEmpID.get(ar),tbLblEmployeeID.get(ar),tbLblProximityID.get(ar),
				tbLblEmployeeName.get(ar),tbLblDesignationID.get(ar),tbLblDesignation.get(ar),tblblEmployeeType.get(ar),
				tblblInterviewDate.get(ar),tblblServiceOfLength.get(ar),tbtLblBasic.get(ar),tbLblHouseRent.get(ar),
				tbLblConveyance.get(ar),tbLblMedicalAllowance.get(ar),tbLblGross.get(ar),tbtxtIncrPercentage.get(ar),
				tbLblIncPer.get(ar),tbtxtIncrementAmount.get(ar),tbLblIncAmt.get(ar),tbLblNewBasic.get(ar),
				tbLblNewHouseRent.get(ar),tbLblNewConveyance.get(ar),tbLblNewMedicalAllowance.get(ar),tbLblNewGross.get(ar),
				tbBtndel.get(ar)}, ar);
	}

	private AbsoluteLayout buildlMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("1220.0px");
		mainLayout.setHeight("650.0px");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:10.0px;left:19.0px;");

		// lblDept
		lblDepartment = new Label("Department :");
		lblDepartment.setImmediate(true);
		lblDepartment.setWidth("-1px");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment, "top:10.0px;left:25.0px;");


		cmbDepartment=new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("350.0px");
		mainLayout.addComponent(cmbDepartment,"top:08.0px; left:117.0px;");

		// lblStar
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:40.0px;left:18.0px;");

		// lblsection
		lblSection = new Label("Section :");
		lblSection.setImmediate(true);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:40.0px;left:25.0px;");

		cmbSection=new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("350.0px");
		mainLayout.addComponent(cmbSection,"top:38.0px; left:117.0px;");

		//lblEmpType
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:85.0px;left:18.0px;");

		lblEmployee = new Label("Employee Name :");
		lblEmployee.setImmediate(true);
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee, "top:85.0px;left:22.0px;");

		cmbEmployee=new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("350.0px");
		cmbEmployee.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbEmployee,"top:83.0px; left:117.0px;");

		chkAllEmp=new CheckBox("All");
		chkAllEmp.setImmediate(true);
		mainLayout.addComponent(chkAllEmp, "top:85.0px;left:469.0px");

		opgEmployee=new OptionGroup("",Optiontype);
		opgEmployee.setImmediate(true);
		opgEmployee.setStyleName("horizontal");
		opgEmployee.select("Employee Name");
		mainLayout.addComponent(opgEmployee, "top:60.0px;left:95.0px;");

		//lblIncrementType
		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:10.0px;left:492.0px;");

		lblIncrement = new Label("Increment Type :");
		lblIncrement.setImmediate(true);
		lblIncrement.setWidth("-1px");
		lblIncrement.setHeight("-1px");
		mainLayout.addComponent(lblIncrement, "top:10.0px;left:500.0px;");

		cmbIncrement=new ComboBox();
		cmbIncrement.setImmediate(true);
		cmbIncrement.setWidth("300.0px");
		cmbIncrement.setNewItemsAllowed(true);
		mainLayout.addComponent(cmbIncrement,"top:08.0px; left:598.0px;");

		nbIncrementType = new NativeButton();
		nbIncrementType.setIcon(new ThemeResource("../icons/add.png"));
		nbIncrementType.setImmediate(true);
		nbIncrementType.setWidth("32px");
		nbIncrementType.setHeight("21px");
		mainLayout.addComponent(nbIncrementType,"top:08.0px;left:900.0px;");

		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:10.0px;left:940.0px;");

		dDate=new PopupDateField();
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		mainLayout.addComponent(new Label("Date : "), "top:10.0px; left:945.0px;");
		mainLayout.addComponent(dDate, "top:10.0px; left:990.0px;");

		lblStar = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b> ");
		lblStar.setImmediate(true);
		lblStar.setContentMode(Label.CONTENT_XHTML);
		lblStar.setWidth("-1px");
		lblStar.setHeight("-1px");
		mainLayout.addComponent(lblStar, "top:40.0px;left:505.0px;");

		//////amount field
		txtIncrPercentage = new AmountField("");
		txtIncrPercentage.setImmediate(true);
		txtIncrPercentage.setWidth("110px");
		mainLayout.addComponent(new Label("Increment % : "), "top:40.0px; left:510.0px;");
		mainLayout.addComponent(txtIncrPercentage, "top:40.0px; left:600.0px;");	

		LblOr = new Label("<b><Font Color='#000000' size='3px'>OR</Font></b> ");
		LblOr.setImmediate(true);
		LblOr.setContentMode(Label.CONTENT_XHTML);
		LblOr.setWidth("-1px");
		LblOr.setHeight("-1px");
		mainLayout.addComponent(LblOr, "top:40.0px;left:730.0px;");

		txtIncrementAmount = new AmountField("");
		txtIncrementAmount.setImmediate(true);
		txtIncrementAmount.setWidth("110px");
		mainLayout.addComponent(new Label("Increment Amount : "), "top:40.0px; left:775.0px;");
		mainLayout.addComponent(txtIncrementAmount, "top:38.0px; left:898.0px;");


		btnGroupIncrementType = new OptionGroup("",type);
		btnGroupIncrementType.setImmediate(true);
		btnGroupIncrementType.setValue("Gross");
		btnGroupIncrementType.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Based On   :"),"top:65.0px; left:510.0px;");
		mainLayout.addComponent(btnGroupIncrementType, "top:65.0px; left:592.0px;");

		table=new Table();
		table.setWidth("99%");
		table.setHeight("420.0px");
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("SL#", Label.class , new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 100);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 95);

		table.addContainerProperty("Proximity ID", Label.class , new Label());
		table.setColumnWidth("Proximity ID",80);

		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",155);

		table.addContainerProperty("Designation ID", Label.class, new Label());
		table.setColumnWidth("Designation ID", 50);

		table.addContainerProperty("Designation", Label.class , new Label());
		table.setColumnWidth("Designation",85);

		table.addContainerProperty("Employee Type", Label.class , new Label());
		table.setColumnWidth("Employee Type",85);

		table.addContainerProperty("Joining Date", Label.class , new Label());
		table.setColumnWidth("Joining Date",85);

		table.addContainerProperty("Service Length", Label.class , new Label());
		table.setColumnWidth("Service Length",85);

		table.addContainerProperty("Basic", Label.class , new Label());
		table.setColumnWidth("Basic",50);

		table.addContainerProperty("H.Rent", Label.class , new Label());
		table.setColumnWidth("H.Rent",50);

		table.addContainerProperty("Conv.", Label.class , new Label());
		table.setColumnWidth("Conv.",50);

		table.addContainerProperty("M.Allow.", Label.class , new Label());
		table.setColumnWidth("M.Allow.",50);

		table.addContainerProperty("Gross", Label.class , new Label());
		table.setColumnWidth("Gross",60);

		table.addContainerProperty("Incr(%)", AmountField.class , new AmountField(""));
		table.setColumnWidth("Incr(%)",40);

		table.addContainerProperty("Incr_(%)", Label.class , new Label(""));
		table.setColumnWidth("Incr_(%)",40);

		table.addContainerProperty("Incr Amt", AmountField.class , new AmountField(""));
		table.setColumnWidth("Incr Amt",50);

		table.addContainerProperty("Incr_Amt", Label.class , new Label(""));
		table.setColumnWidth("Incr_Amt",50);

		table.addContainerProperty("New Basic", Label.class , new Label());
		table.setColumnWidth("New Basic",50);

		table.addContainerProperty("New H.Rent", Label.class , new Label());
		table.setColumnWidth("New H.Rent",50);

		table.addContainerProperty("New Conv.", Label.class , new Label());
		table.setColumnWidth("New Conv.",50);

		table.addContainerProperty("New M.Allow.", Label.class , new Label());
		table.setColumnWidth("New M.Allow.",50);

		table.addContainerProperty("New Gross", Label.class , new Label());
		table.setColumnWidth("New Gross",65);

		table.addContainerProperty("Remove", NativeButton.class , new NativeButton());
		table.setColumnWidth("Remove",40);

		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("Designation ID", true);
		table.setColumnCollapsed("Employee Type", true);
		table.setColumnCollapsed("Incr_(%)", true);
		table.setColumnCollapsed("Incr_Amt", true);



		table.setColumnAlignments(new String[]{Table.ALIGN_RIGHT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT,Table.ALIGN_RIGHT,Table.ALIGN_CENTER,});

		mainLayout.addComponent(table, "top:110.0px; left:25.0px;");
		mainLayout.addComponent(cButton, "top:550.0px; left:365.0px;");

		Label lblComment = new Label("Fields marked with <Font Color='#CD0606' size='3px'>(<b>*</b>)</Font> are mandatory",Label.CONTENT_XHTML);
		lblComment.setImmediate(true);
		lblComment.setWidth("-1px");
		lblComment.setHeight("-1px");
		mainLayout.addComponent(lblComment, "top:550.0px; left:30.0px;");


		return mainLayout;
	}
}
