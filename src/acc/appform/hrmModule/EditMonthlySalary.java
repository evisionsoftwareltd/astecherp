package acc.appform.hrmModule;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
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
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.data.Property.ValueChangeListener;

@SuppressWarnings("serial")
public class EditMonthlySalary extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table= new Table();
	private PopupDateField dWorkingDate ;

	private Label lblDate ;
	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;

	private Label lblDepartment;
	private ComboBox cmbDepartment;

	private Label lblEmployee;
	private ComboBox cmbEmployee;
	private CheckBox chkEmployeeAll;

	private Label lblNoteDays=new Label("TD:Total Days in Month, WD:Working Days, PD:Present Days, AD:Absent Days, LD:Leave Days, HD:Holiday, FD:Friday");
	private Label lblNoteSalary=new Label("HR:House Rent, Conv.:Conveyance, MA:Medical Allowance, AT.Bonus:Attendance Bonus, FA: Friday Allowance");
	private Label lblNoteSalary2=new Label("Abs.Amt:Absent Amount, Avd. Sal:Advance Salary/Loan, Inc. Tax: Income Tax, INS.:Insurance");

	private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
	private ArrayList<Label> lblsa = new ArrayList<Label>();
	private ArrayList<Label> lblAutoEmployeeID=new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeCode = new ArrayList<Label>();
	private ArrayList<Label> lblProximityID = new ArrayList<Label>();
	private ArrayList<Label> lblEmployeeName = new ArrayList<Label>();
	private ArrayList<Label> lblDesignation = new ArrayList<Label>();

	private ArrayList<Label> lblTotalDays = new ArrayList<Label>();
	private ArrayList<Label> lblWorkDays = new ArrayList<Label>();
	private ArrayList<Label> lblPresentDays = new ArrayList<Label>();
	private ArrayList<Label> lblAbsentDays = new ArrayList<Label>();
	private ArrayList<Label> lblLeaveDays = new ArrayList<Label>();
	private ArrayList<Label> lblHoliDays = new ArrayList<Label>();
	private ArrayList<Label> lblFriDays = new ArrayList<Label>();

	private ArrayList<Label> lblGross = new ArrayList<Label>();
	private ArrayList<Label> lblBasic = new ArrayList<Label>();
	private ArrayList<Label> lblHouseRent = new ArrayList<Label>();
	private ArrayList<Label> lblConveyance = new ArrayList<Label>();
	private ArrayList<Label> lblMedicalAll = new ArrayList<Label>();
	private ArrayList<Label> lblAttBonus = new ArrayList<Label>();
	private ArrayList<Label> lblFridayAllow = new ArrayList<Label>();
	private ArrayList<Label> lblAbsentAmount = new ArrayList<Label>();
	private ArrayList<Label> lblLoanNo = new ArrayList<Label>();
	private ArrayList<Label> lblLoanTransactionNo = new ArrayList<Label>();
	private ArrayList<AmountField> txtAdvSalary = new ArrayList<AmountField>();   // Advance Salary or Loan
	private ArrayList<AmountField> txtIncomeTax = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtInsurance = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtAdjust = new ArrayList<AmountField>();
	private ArrayList<AmountField> txtLessAdjust=new ArrayList<AmountField>();

	ArrayList<Component> allComp = new ArrayList<Component>();	

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat FMonthName = new SimpleDateFormat("MMMMM");
	private SimpleDateFormat FYear = new SimpleDateFormat("yyyy");

	CommonButton button = new CommonButton("New", "Save", "", "","Refresh","","","","","Exit");

	private Boolean isUpdate= false;
	boolean t;
	int i = 0;

	private DecimalFormat df = new DecimalFormat("#0");

	String empId="";
	String empCode="";
	String empFingerId="";
	String empProxId="";
	String empDesId="";

	String Notify="";

	public EditMonthlySalary(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("MONTHLY SALARY EDIT :: " + sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		tableinitialise();
		componentIni(true);
		btnIni(true);
		SetEventAction();
		focusEnter();
		authenticationCheck();
		button.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable())
		{
			button.btnDelete.setVisible(false);
		}
	}

	private void SetEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(false);
				btnIni(false);
				txtClear();
				dWorkingDate.focus();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				componentIni(true);
				btnIni(true);
				txtClear();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(!lblEmployeeName.get(0).toString().equals(""))
						{
							if(tableValidationCheck())
							{
								saveBtnAction(event);
							}
							else
							{
								showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							showNotification("Warning","There are nothing to save!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please Select Section Name!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Please Select Department Name!!!",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		dWorkingDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(dWorkingDate.getValue()!=null)
				{
					cmbDepartment.removeAllItems();
					if(dWorkingDate.getValue()!=null)
					{
						cmbDepartmentAdd();
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
					cmbSectionAdd();
				}
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				cmbEmployee.removeAllItems();
				chkEmployeeAll.setValue(false);
				tableClear();
				if(cmbSection.getValue()!=null)
				{
					addEmployeeData(cmbSection.getValue().toString());
				}
			}
		});

		chkSectionAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployee.removeAllItems();
				chkEmployeeAll.setValue(false);
				tableClear();
				if(chkSectionAll.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
					addEmployeeData("%");
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		chkEmployeeAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkEmployeeAll.booleanValue()==true)
				{
					cmbEmployee.setEnabled(false);
					cmbEmployee.setValue(null);
					if(cmbDepartment.getValue()!=null)
					{
						if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
						{
							addTableData("%");
						}
					}
				}

				else
				{
					isUpdate=true;
					cmbEmployee.setEnabled(true);
				}
			}
		});

		cmbEmployee.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
					{
						if(cmbEmployee.getValue()!=null)
						{
							empId = cmbEmployee.getValue().toString();
							addTableData(empId);
						}
					}
				}
			}
		});
	}

	private boolean tableValidationCheck()
	{
		boolean ret=false;
		for(int i=0;i<lblAutoEmployeeID.size();i++)
		{
			if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
			{
				if(!txtAdvSalary.get(i).getValue().toString().isEmpty() ||
						!txtAdjust.get(i).getValue().toString().isEmpty() ||
						!txtLessAdjust.get(i).getValue().toString().isEmpty())
				{
					ret=true;
					break;
				}
				else
					Notify="Provide Salary Adjustment Amount!!!";
			}
			else
				Notify="No Employee Found!!!";
		}
		return ret;
	}

	private void addEmployeeData(String sectionID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> list = session.createSQLQuery("select autoEmployeeId,empID from tbSalary where " +
					"vDepartmentID='"+cmbDepartment.getValue()+"' and SectionID " +
					"like '"+sectionID+"' and " +
					"vMonthName=DateName(MM,'"+dFormat.format(dWorkingDate.getValue())+"') " +
					"and year=YEAR('"+dFormat.format(dWorkingDate.getValue())+"') order by empID").list();

			for (Iterator <?> iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbEmployee.addItem(element[0].toString());
				cmbEmployee.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			showNotification("addEmployeeData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void addTableData(String employeeId)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String check = " SELECT * from tbSalary where vMonthName='"+FMonthName.format(dWorkingDate.getValue())+"' and year = '"+FYear.format(dWorkingDate.getValue())+"'";
			List <?> checkList = session.createSQLQuery(check).list();

			String SectionID="%";
			if(cmbSection.getValue()!=null)
				SectionID=cmbSection.getValue().toString();

			if(!checkList.isEmpty())
			{
				String sql = "select autoEmployeeID,empId,empCode,empName,designation,totalDaysofMonth,totalWorkingDay,present,absentDay,leaveDay,"+
						"holiday,Friday,basicSalary,houseRent,Conveyance,Medical,AttBonus,FridayAllowance,perDay*absentDay absentamount,"+
						"advanceSalary,incomeTax,Insurance,Adjust,Less,vLoanNo,vloanTransactionID,Gross from tbSalary where vDepartmentID='"+cmbDepartment.getValue()+"' " +
						"and SectionID like '"+SectionID+"' and autoEmployeeID like '"+employeeId+"' and vMonthName='"+FMonthName.format(dWorkingDate.getValue())+"' " +
						"and year='"+FYear.format(dWorkingDate.getValue())+"' order by empID";
				List <?> lst = session.createSQLQuery(sql).list();

				for (Iterator <?> iter = lst.iterator(); iter.hasNext();) 
				{
					if(i==lblEmployeeName.size()-1)
					{
						tableRowAdd(i+1);
					}

					Object[] element = (Object[]) iter.next();

					for(int j=0;j<lblAutoEmployeeID.size();j++)
					{
						if(lblAutoEmployeeID.get(j).getValue().toString().trim().isEmpty())
						{
							i=j;
							t=true;
							break;
						}
						if(!lblAutoEmployeeID.get(j).getValue().toString().trim().isEmpty())
						{
							if(lblAutoEmployeeID.get(j).getValue().toString().equals(element[0].toString()))
							{
								t=false;
								break;
							}
						}
					}
					
					if(t){
						//not in table
						lblAutoEmployeeID.get(i).setValue(element[0].toString());
						lblEmployeeCode.get(i).setValue(element[1].toString());
						lblProximityID.get(i).setValue(element[2].toString());
						lblEmployeeName.get(i).setValue(element[3].toString());
						lblDesignation.get(i).setValue(element[4].toString());
						lblTotalDays.get(i).setValue(element[5].toString());

						lblWorkDays.get(i).setValue(element[6].toString());
						lblPresentDays.get(i).setValue(element[7].toString());
						lblAbsentDays.get(i).setValue(element[8].toString());
						lblLeaveDays.get(i).setValue(element[9].toString());
						lblHoliDays.get(i).setValue(element[10].toString());
						lblFriDays.get(i).setValue(element[11].toString());

						lblGross.get(i).setValue(df.format(Double.parseDouble(element[26].toString())));
						lblBasic.get(i).setValue(df.format(Double.parseDouble(element[12].toString())));
						lblHouseRent.get(i).setValue(df.format(Double.parseDouble(element[13].toString())));
						lblConveyance.get(i).setValue(df.format(Double.parseDouble(element[14].toString())));
						lblMedicalAll.get(i).setValue(df.format(Double.parseDouble(element[15].toString())));
						lblAttBonus.get(i).setValue(df.format(Double.parseDouble(element[16].toString())));

						lblFridayAllow.get(i).setValue(element[17].toString());
						lblLoanNo.get(i).setValue(element[24].toString());
						lblLoanTransactionNo.get(i).setValue(element[25].toString());
						lblAbsentAmount.get(i).setValue(df.format(Double.parseDouble(element[18].toString())));
						txtAdvSalary.get(i).setValue(df.format(Double.parseDouble(element[19].toString())));
						txtIncomeTax.get(i).setValue(df.format(Double.parseDouble(element[20].toString())));
						txtInsurance.get(i).setValue(df.format(Double.parseDouble(element[21].toString())));
						txtAdjust.get(i).setValue(df.format(Double.parseDouble(element[22].toString())));
						txtLessAdjust.get(i).setValue(df.format(Double.parseDouble(element[23].toString())));

						i++;
					}
				}
				if(i == 0)
				{
					showNotification("Warning!","No Data found",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				tableClear();
				showNotification("Warning!","No data found",Notification.TYPE_WARNING_MESSAGE);
			}
			if(!t)
			{
				showNotification("Warning","This Employee is already exists!!!",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception ex)
		{
			showNotification("addTableData",ex.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbDepartmentAdd() 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query="select distinct vDepartmentId,vDepartmentName from tbSalary where " +
					"Month(dDate)=Month('"+dateFormat.format(dWorkingDate.getValue())+"') " +
					"and year='"+FYear.format(dWorkingDate.getValue())+"' and vDepartmentName!='CHO' order by vDepartmentName";
			List <?> list = session.createSQLQuery(query).list();
			for (Iterator <?> iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0].toString());
				cmbDepartment.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			showNotification("cmbDepartmentAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionAdd() 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			String query="select distinct ts.SectionId,sein.vDepartmentName,sein.SectionName from tbSectionInfo sein " +
					"inner join tbSalary ts on sein.vSectionID=ts.SectionId where " +
					"ts.vDepartmentID='"+cmbDepartment.getValue()+"' and Month(dDate)=Month('"+dateFormat.format(dWorkingDate.getValue())+"') " +
					"and year='"+FYear.format(dWorkingDate.getValue())+"' and sein.SectionName!='CHO'  order by sein.vDepartmentName";
			List <?> list = session.createSQLQuery(query).list();
			for (Iterator <?> iter = list.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0].toString());
				cmbSection.setItemCaption(element[0].toString(), element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch (Exception ex) 
		{
			showNotification("cmbSectionAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveBtnAction(ClickEvent e)
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					updateData();
					txtClear();
					componentIni(true);
					btnIni(true);
				}
			}
		});
	}

	private void updateData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			for(int i = 0;i<lblAutoEmployeeID.size();i++)
			{
				if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
				{
					if(!txtAdvSalary.get(i).getValue().toString().isEmpty() || !txtAdjust.get(i).getValue().toString().isEmpty() || !txtLessAdjust.get(i).getValue().toString().isEmpty())
					{
						if(!lblLoanNo.get(i).getValue().toString().trim().isEmpty())
						{
							String updateLoanApplication = " UPDATE tbLoanApplication set mLoanBalance = mLoanBalance + (select advanceSalary from tbSalary ts where ts.autoEmployeeId = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' and "+  
									"ts.vMonthName='"+FMonthName.format(dWorkingDate.getValue())+"' and ts.year='"+FYear.format(dWorkingDate.getValue())+"' and vLoanNo = '"+lblLoanNo.get(i).getValue().toString()+"' and" +
									" vLoanTransactionID = '"+lblLoanTransactionNo.get(i).getValue().toString()+"') - '"+(txtAdvSalary.get(i).getValue().toString().trim().isEmpty()?0:txtAdvSalary.get(i).getValue().toString().trim())+"'"+
									" where vAutoEmployeeId = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' and" +
									" vLoanNo = '"+lblLoanNo.get(i).getValue().toString()+"' ";

							session.createSQLQuery(updateLoanApplication).executeUpdate();

							String updateLoanRecovery = " UPDATE tbLoanRecoveryInfo set mRecoveryAmount = '"+(txtAdvSalary.get(i).getValue().toString().trim().isEmpty()?0:txtAdvSalary.get(i).getValue().toString().trim())+"' "+
									"where vAutoEmployeeId = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
									"and vLoanNo = '"+lblLoanNo.get(i).getValue().toString()+"' and " +
									"vTransactionId = '"+lblLoanTransactionNo.get(i).getValue().toString()+"' ";

							session.createSQLQuery(updateLoanRecovery).executeUpdate();
							session.clear();
						}

						String udquery="insert into tbUDSalary(year,vMonthName,vAutoEmployeeID,empId,empCode,empName,shiftId,shiftName,empType,designation,SectionID,"+
								"Section,joinDate,totalDaysofMonth,totalWorkingDay,Friday,present,absentDay,leaveDay,holiday,casualLeave,"+
								"sickLeave,EarnedLeave,officialTour,Gross,basicSalary,houseRent,Conveyance,Medical,perDay,AttBonus,"+
								"FridayAllowance,Subtotal,salaryCutAbsent,advanceSalary,incomeTax,Insurance,ProvidentFund,totalDeduction,"+
								"Adjust,Less,payableAmount,otHour,otRate,totalOtTaka,userId,userIp,entryTime,vLoanNo,vLoanTransactionID,UDFlag,mDearnessAllowance,mFireAllowance," +
								"mRevenueStamp,vDesignationID,iDesignationSerial,vBankAccountNo,dDate,vDepartmentID,vDepartmentName)"+
								"select year,vMonthName,autoEmployeeId,empId,empCode,empName,shiftId,shiftName,empType,designation,SectionID,Section,joinDate,"+
								"totalDaysofMonth,totalWorkingDay,Friday,present,absentDay,leaveDay,holiday,casualLeave,sickLeave,EarnedLeave,"+
								"officialTour,Gross,basicSalary,houseRent,Conveyance,Medical,perDay,AttBonus,FridayAllowance,Subtotal,"+
								"salaryCutAbsent,advanceSalary,incomeTax,Insurance,ProvidentFund,totalDeduction,Adjust,Less,payableAmount,"+
								"itotalOTHour,otRate,totalOtTaka,userId,userIp,entryTime,vLoanNo,vLoanTransactionID,'OLD',mDearnessAllowance,mFireAllowance,mRevenueStamp," +
								"vDesignationID,iDesignationSerial,vBankAccountNo,dDate,vDepartmentID,vDepartmentName from tbSalary "+
								"where autoEmployeeID='"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
								"and vMonthName='"+FMonthName.format(dWorkingDate.getValue())+"' and year='"+FYear.format(dWorkingDate.getValue())+"'";
						session.createSQLQuery(udquery).executeUpdate();
						session.clear();

						String query = "update tbSalary set advanceSalary='"+(txtAdvSalary.get(i).getValue().toString().trim().isEmpty()?0:txtAdvSalary.get(i).getValue().toString().trim())+"'," +
								"incomeTax='"+(txtIncomeTax.get(i).getValue().toString().trim().isEmpty()?0:txtIncomeTax.get(i).getValue().toString().trim())+"'," +
								"Insurance='"+(txtInsurance.get(i).getValue().toString().trim().isEmpty()?0:txtInsurance.get(i).getValue().toString().trim())+"'," +
								"Adjust='"+(txtAdjust.get(i).getValue().toString().trim().isEmpty()?0:txtAdjust.get(i).getValue().toString().trim())+"', " +
								"Less='"+(txtLessAdjust.get(i).getValue().toString().trim().isEmpty()?0:txtLessAdjust.get(i).getValue().toString().trim())+"' where autoEmployeeId = '"+lblAutoEmployeeID.get(i).getValue().toString()+"' and "+  
								"vMonthName='"+FMonthName.format(dWorkingDate.getValue())+"' and year='"+FYear.format(dWorkingDate.getValue())+"' and vLoanNo = '"+lblLoanNo.get(i).getValue().toString()+"' " +
								"and vloanTransactionID = '"+lblLoanTransactionNo.get(i).getValue().toString()+"'";
						session.createSQLQuery(query).executeUpdate();
						session.clear();

						String udquery2="insert into tbUDSalary(year,vMonthName,vAutoEmployeeID,empId,empCode,empName,shiftId,shiftName,empType,designation,SectionID,"+
								"Section,joinDate,totalDaysofMonth,totalWorkingDay,Friday,present,absentDay,leaveDay,holiday,casualLeave,"+
								"sickLeave,EarnedLeave,officialTour,Gross,basicSalary,houseRent,Conveyance,Medical,perDay,AttBonus,"+
								"FridayAllowance,Subtotal,salaryCutAbsent,advanceSalary,incomeTax,Insurance,ProvidentFund,totalDeduction,"+
								"Adjust,Less,payableAmount,otHour,otRate,totalOtTaka,userId,userIp,entryTime,vLoanNo,vLoanTransactionID,UDFlag," +
								"mDearnessAllowance,mFireAllowance,mRevenueStamp,vDesignationID,iDesignationSerial,vBankAccountNo,dDate,vDepartmentID,vDepartmentName)"+
								"select year,vMonthName,autoEmployeeID,empId,empCode,empName,shiftId,shiftName,empType,designation,SectionID,Section,joinDate,"+
								"totalDaysofMonth,totalWorkingDay,Friday,present,absentDay,leaveDay,holiday,casualLeave,sickLeave,EarnedLeave,"+
								"officialTour,Gross,basicSalary,houseRent,Conveyance,Medical,perDay,AttBonus,FridayAllowance,Subtotal,"+
								"salaryCutAbsent,'"+txtAdvSalary.get(i).getValue().toString().trim()+"','"+txtIncomeTax.get(i).getValue().toString().trim()+"'" +
								",'"+txtInsurance.get(i).getValue()+"',ProvidentFund,totalDeduction,'"+txtAdjust.get(i).getValue().toString().trim()+"'," +
								"'"+txtLessAdjust.get(i).getValue().toString().trim()+"',payableAmount,"+
								"itotalOTHour,otRate,totalOtTaka,userId,userIp,entryTime,vLoanNo,vLoanTransactionID,'UPDATED',mDearnessAllowance,mFireAllowance,mRevenueStamp," +
								"vDesignationID,iDesignationSerial,vBankAccountNo,dDate,vDepartmentID,vDepartmentName from tbSalary "+
								"where autoEmployeeID='"+lblAutoEmployeeID.get(i).getValue().toString()+"' " +
								"and vMonthName='"+FMonthName.format(dWorkingDate.getValue())+"' and year='"+FYear.format(dWorkingDate.getValue())+"'";
						session.createSQLQuery(udquery2).executeUpdate();
						session.clear();
					}
				}
			}
			tx.commit();
			showNotification("All information update successfully.");
		}
		catch(Exception ex)
		{
			showNotification("updateData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(dWorkingDate);
		allComp.add(cmbSection);
		for(int i=0;i<lblEmployeeCode.size();i++)
		{
			allComp.add(txtAdvSalary.get(i));
			allComp.add(txtIncomeTax.get(i));
			allComp.add(txtInsurance.get(i));
			allComp.add(txtAdjust.get(i));
			allComp.add(txtLessAdjust.get(i));
		}
		allComp.add(button.btnSave);
		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{
		dWorkingDate.setEnabled(!b);
		lblNoteDays.setEnabled(!b);
		cmbDepartment.setEnabled(!b);
		cmbSection.setEnabled(!b);
		chkSectionAll.setEnabled(!b);
		lblNoteSalary.setEnabled(!b);
		cmbEmployee.setEnabled(!b);
		chkEmployeeAll.setEnabled(!b);
		lblNoteSalary2.setEnabled(!b);
		table.setEnabled(!b);
		chkEmployeeAll.setValue(false);
		if(isUpdate)
		{cmbEmployee.setEnabled(false);}
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);;
	}

	public void txtClear()
	{
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		chkSectionAll.setValue(false);
		cmbEmployee.setValue(null);
		tableClear();
	}

	private void tableClear()
	{
		for(int i=0; i<lblEmployeeName.size(); i++)
		{
			lblAutoEmployeeID.get(i).setValue("");
			lblEmployeeCode.get(i).setValue("");
			lblProximityID.get(i).setValue("");
			lblEmployeeName.get(i).setValue("");
			lblDesignation.get(i).setValue("");
			lblTotalDays.get(i).setValue("");
			lblWorkDays.get(i).setValue("");
			lblPresentDays.get(i).setValue("");
			lblAbsentDays.get(i).setValue("");
			lblLeaveDays.get(i).setValue("");
			lblHoliDays.get(i).setValue("");
			lblFriDays.get(i).setValue("");
			lblBasic.get(i).setValue("");
			lblHouseRent.get(i).setValue("");
			lblConveyance.get(i).setValue("");
			lblMedicalAll.get(i).setValue("");
			lblGross.get(i).setValue("");
			lblAttBonus.get(i).setValue("");
			lblFridayAllow.get(i).setValue("");
			lblAbsentAmount.get(i).setValue("");
			lblLoanNo.get(i).setValue("");
			lblLoanTransactionNo.get(i).setValue("");
			txtAdvSalary.get(i).setValue("");
			txtIncomeTax.get(i).setValue("");
			txtInsurance.get(i).setValue("");
			txtAdjust.get(i).setValue("");
			txtLessAdjust.get(i).setValue("");
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("1200px");
		mainLayout.setHeight("520px");

		lblDate = new Label("Salary Month :");
		lblDate.setImmediate(false);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		mainLayout.addComponent(lblDate, "top:20.0px; left:40.0px;");

		dWorkingDate = new PopupDateField();
		dWorkingDate.setImmediate(true);
		dWorkingDate.setWidth("140px");
		dWorkingDate.setDateFormat("MMMMM-yyyy");
		dWorkingDate.setValue(new java.util.Date());
		dWorkingDate.setResolution(PopupDateField.RESOLUTION_MONTH);
		mainLayout.addComponent(dWorkingDate, "top:18.0px; left:150.0px;");

		lblNoteDays.setStyleName("lbltxtColor");
		lblNoteDays.setImmediate(true);
		mainLayout.addComponent(lblNoteDays, "top:10.0px;left:550.0px;");

		lblDepartment = new Label("Department Name :");
		lblDepartment.setImmediate(false); 
		lblDepartment.setWidth("-1px");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment, "top:45.0px; left:40.0px;");

		cmbDepartment = new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("280px");
		cmbDepartment.setHeight("24px");
		cmbDepartment.setNullSelectionAllowed(true);
		cmbDepartment.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbDepartment, "top:43.0px; left:150.0px;");

		lblSection = new Label("Section Name :");
		lblSection.setImmediate(false); 
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:70.0px; left:40.0px;");

		cmbSection = new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("280px");
		cmbSection.setHeight("24px");
		cmbSection.setNullSelectionAllowed(true);
		cmbSection.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbSection, "top:68.0px; left:150.0px;");

		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		mainLayout.addComponent(chkSectionAll, "top:70.0px; left:435.0px;");

		lblNoteSalary.setStyleName("lbltxtColor");
		lblNoteSalary.setImmediate(true);
		mainLayout.addComponent(lblNoteSalary, "top:35.0px;left:550.0px;");

		lblEmployee = new Label("Employee ID :");
		lblEmployee.setImmediate(false); 
		lblEmployee.setWidth("-1px");
		lblEmployee.setHeight("-1px");
		mainLayout.addComponent(lblEmployee, "top:95.0px; left:40.0px;");

		cmbEmployee = new ComboBox();
		cmbEmployee.setImmediate(true);
		cmbEmployee.setWidth("280px");
		cmbEmployee.setHeight("24px");
		cmbEmployee.setNullSelectionAllowed(true);
		cmbEmployee.setNewItemsAllowed(false);
		mainLayout.addComponent(cmbEmployee, "top:93.0px; left:150.0px;");

		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setHeight("-1px");
		chkEmployeeAll.setWidth("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:95.0px; left:435.0px;");

		lblNoteSalary2.setStyleName("lbltxtColor");
		lblNoteSalary2.setImmediate(true);
		mainLayout.addComponent(lblNoteSalary2, "top:60.0px;left:550.0px;");

		table.setWidth("1160px");
		table.setHeight("325px");
		table.setColumnCollapsingAllowed(true);

		table.addContainerProperty("Del", NativeButton.class, new NativeButton());
		table.setColumnWidth("Del", 30);

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("EMP ID", Label.class, new Label());
		table.setColumnWidth("EMP ID", 80);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 75);

		table.addContainerProperty("Proximity ID", Label.class, new Label());
		table.setColumnWidth("Proximity ID", 75);

		table.addContainerProperty("Employee Name", Label.class, new Label());
		table.setColumnWidth("Employee Name",  150);

		table.addContainerProperty("Designation", Label.class, new Label());
		table.setColumnWidth("Designation", 100);

		table.addContainerProperty("TD", Label.class, new Label());
		table.setColumnWidth("TD", 17);

		table.addContainerProperty("WD", Label.class, new Label());
		table.setColumnWidth("WD", 17);

		table.addContainerProperty("PD", Label.class, new Label());
		table.setColumnWidth("PD", 17);

		table.addContainerProperty("AD", Label.class, new Label());
		table.setColumnWidth("AD", 17);

		table.addContainerProperty("LD", Label.class, new Label());
		table.setColumnWidth("LD", 10);

		table.addContainerProperty("HD", Label.class, new Label());
		table.setColumnWidth("HD", 17);

		table.addContainerProperty("FD", Label.class, new Label());
		table.setColumnWidth("FD", 17);

		table.addContainerProperty("Gross", Label.class, new Label());
		table.setColumnWidth("Gross", 40);

		table.addContainerProperty("Basic", Label.class, new Label());
		table.setColumnWidth("Basic", 40);

		table.addContainerProperty("HR", Label.class, new Label());
		table.setColumnWidth("HR", 40);

		table.addContainerProperty("Conv.", Label.class, new Label());
		table.setColumnWidth("Conv.", 40);

		table.addContainerProperty("MA", Label.class, new Label());
		table.setColumnWidth("MA", 40);

		table.addContainerProperty("At.Bonus", Label.class, new Label());
		table.setColumnWidth("At.Bonus", 40);

		table.addContainerProperty("FA", Label.class, new Label());
		table.setColumnWidth("FA", 30);

		table.addContainerProperty("Abs. Amt", Label.class, new Label());
		table.setColumnWidth("Abs. Amt", 40);

		table.addContainerProperty("Loan No.", Label.class, new Label());
		table.setColumnWidth("Loan No.", 20);

		table.addContainerProperty("Loan Transaction No.", Label.class, new Label());
		table.setColumnWidth("Loan Transaction No.", 20);

		table.addContainerProperty("Adv. Sal.", AmountField.class, new AmountField());
		table.setColumnWidth("Adv. Sal.", 40);

		table.addContainerProperty("Inc. Tax", AmountField.class, new AmountField());
		table.setColumnWidth("Inc. Tax", 40);

		table.addContainerProperty("Ins.", AmountField.class, new AmountField());
		table.setColumnWidth("Ins.", 40);

		table.addContainerProperty("Adjust", AmountField.class, new AmountField());
		table.setColumnWidth("Adjust", 40);

		table.addContainerProperty("Less", AmountField.class, new AmountField());
		table.setColumnWidth("Less", 40);

		table.setColumnCollapsed("Loan No.", true);
		table.setColumnCollapsed("Loan Transaction No.", true);
		table.setColumnCollapsed("EMP ID", true);
		table.setColumnCollapsed("TD", true);
		table.setColumnCollapsed("WD", true);
		table.setColumnCollapsed("PD", true);
		table.setColumnCollapsed("AD", true);
		table.setColumnCollapsed("LD", true);
		table.setColumnCollapsed("HD", true);
		table.setColumnCollapsed("FD", true);
		table.setColumnCollapsed("Basic", true);
		table.setColumnCollapsed("HR", true);
		table.setColumnCollapsed("Conv.", true);
		table.setColumnCollapsed("MA", true);
		table.setColumnCollapsed("At.Bonus", true);
		table.setColumnCollapsed("FA", true);
		table.setColumnCollapsed("Abs. Amt", true);

		table.setColumnAlignments(new String[] {Table.ALIGN_CENTER,Table.ALIGN_CENTER, Table.ALIGN_LEFT,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, 
				Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT,
				Table.ALIGN_RIGHT, Table.ALIGN_RIGHT, Table.ALIGN_RIGHT/*, Table.ALIGN_RIGHT*/
		});

		mainLayout.addComponent(table,"top:125.0px; left:20.0px;");
		mainLayout.addComponent(button,"top:465.0px; left:440.0px");
		return mainLayout;
	}

	private void tableinitialise()
	{
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd( final int ar)
	{
		btnDel.add(ar, new NativeButton());
		btnDel.get(ar).setWidth("100%");
		btnDel.get(ar).setImmediate(true);
		btnDel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
		btnDel.get(ar).addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				lblAutoEmployeeID.get(ar).setValue("");
				lblEmployeeCode.get(ar).setValue("");
				lblProximityID.get(ar).setValue("");
				lblEmployeeName.get(ar).setValue("");
				lblDesignation.get(ar).setValue("");
				lblTotalDays.get(ar).setValue("");
				lblWorkDays.get(ar).setValue("");
				lblPresentDays.get(ar).setValue("");
				lblAbsentDays.get(ar).setValue("");
				lblLeaveDays.get(ar).setValue("");
				lblHoliDays.get(ar).setValue("");
				lblFriDays.get(ar).setValue("");
				lblGross.get(ar).setValue("");
				lblBasic.get(ar).setValue("");
				lblHouseRent.get(ar).setValue("");
				lblConveyance.get(ar).setValue("");
				lblMedicalAll.get(ar).setValue("");
				lblAttBonus.get(ar).setValue("");
				lblFridayAllow.get(ar).setValue("");
				lblAbsentAmount.get(ar).setValue("");
				lblLoanNo.get(ar).setValue("");
				lblLoanTransactionNo.get(ar).setValue("");
				txtAdvSalary.get(ar).setValue("");
				txtIncomeTax.get(ar).setValue("");
				txtInsurance.get(ar).setValue("");
				txtAdjust.get(ar).setValue("");
				txtLessAdjust.get(ar).setValue("");

				for(int rowcount=ar;rowcount<=lblProximityID.size()-1;rowcount++)
				{
					if(rowcount+1<=lblProximityID.size()-1)
					{
						if(!lblProximityID.get(rowcount+1).getValue().toString().equals(""))
						{
							lblAutoEmployeeID.get(rowcount).setValue(lblAutoEmployeeID.get(rowcount+1).getValue().toString());
							lblEmployeeCode.get(rowcount).setValue(lblEmployeeCode.get(rowcount+1).getValue().toString());
							lblProximityID.get(rowcount).setValue(lblProximityID.get(rowcount+1).getValue().toString());
							lblEmployeeName.get(rowcount).setValue(lblEmployeeName.get(rowcount+1).getValue().toString());
							lblDesignation.get(rowcount).setValue(lblDesignation.get(rowcount+1).getValue().toString());
							lblTotalDays.get(rowcount).setValue(lblTotalDays.get(rowcount+1).getValue().toString());
							lblWorkDays.get(rowcount).setValue(lblWorkDays.get(rowcount+1).getValue().toString());
							lblPresentDays.get(rowcount).setValue(lblPresentDays.get(rowcount+1).getValue().toString());
							lblAbsentDays.get(rowcount).setValue(lblAbsentDays.get(rowcount+1).getValue().toString());
							lblLeaveDays.get(rowcount).setValue(lblLeaveDays.get(rowcount+1).getValue().toString());
							lblHoliDays.get(rowcount).setValue(lblHoliDays.get(rowcount+1).getValue().toString());
							lblFriDays.get(rowcount).setValue(lblFriDays.get(rowcount+1).getValue().toString());
							lblGross.get(rowcount).setValue(lblGross.get(rowcount+1).getValue().toString());
							lblBasic.get(rowcount).setValue(lblBasic.get(rowcount+1).getValue().toString());
							lblHouseRent.get(rowcount).setValue(lblHouseRent.get(rowcount+1).getValue().toString());
							lblConveyance.get(rowcount).setValue(lblConveyance.get(rowcount+1).getValue().toString());
							lblMedicalAll.get(rowcount).setValue(lblMedicalAll.get(rowcount+1).getValue().toString());
							lblAttBonus.get(rowcount).setValue(lblAttBonus.get(rowcount+1).getValue().toString());
							lblFridayAllow.get(rowcount).setValue(lblFridayAllow.get(rowcount+1).getValue().toString());
							lblAbsentAmount.get(rowcount).setValue(lblAbsentAmount.get(rowcount+1).getValue().toString());
							lblLoanNo.get(rowcount).setValue(lblLoanNo.get(rowcount+1).getValue().toString());
							lblLoanTransactionNo.get(rowcount).setValue(lblLoanTransactionNo.get(rowcount+1).getValue().toString());
							txtAdvSalary.get(rowcount).setValue(txtAdvSalary.get(rowcount+1).getValue().toString());
							txtIncomeTax.get(rowcount).setValue(txtIncomeTax.get(rowcount+1).getValue().toString());
							txtInsurance.get(rowcount).setValue(txtInsurance.get(rowcount+1).getValue().toString());
							txtAdjust.get(rowcount).setValue(txtAdjust.get(rowcount+1).getValue().toString());
							txtLessAdjust.get(rowcount).setValue(txtLessAdjust.get(rowcount+1).getValue().toString());

							lblAutoEmployeeID.get(rowcount+1).setValue("");
							lblEmployeeCode.get(rowcount+1).setValue("");
							lblProximityID.get(rowcount+1).setValue("");
							lblEmployeeName.get(rowcount+1).setValue("");
							lblDesignation.get(rowcount+1).setValue("");
							lblTotalDays.get(rowcount+1).setValue("");
							lblWorkDays.get(rowcount+1).setValue("");
							lblPresentDays.get(rowcount+1).setValue("");
							lblAbsentDays.get(rowcount+1).setValue("");
							lblLeaveDays.get(rowcount+1).setValue("");
							lblHoliDays.get(rowcount+1).setValue("");
							lblFriDays.get(rowcount+1).setValue("");
							lblGross.get(rowcount+1).setValue("");
							lblBasic.get(rowcount+1).setValue("");
							lblHouseRent.get(rowcount+1).setValue("");
							lblConveyance.get(rowcount+1).setValue("");
							lblMedicalAll.get(rowcount+1).setValue("");
							lblAttBonus.get(rowcount+1).setValue("");
							lblFridayAllow.get(rowcount+1).setValue("");
							lblAbsentAmount.get(rowcount+1).setValue("");
							lblLoanNo.get(rowcount+1).setValue("");
							lblLoanTransactionNo.get(rowcount+1).setValue("");
							txtAdvSalary.get(rowcount+1).setValue("");
							txtIncomeTax.get(rowcount+1).setValue("");
							txtInsurance.get(rowcount+1).setValue("");
							txtAdjust.get(rowcount+1).setValue("");
							txtLessAdjust.get(rowcount+1).setValue("");
						}
					}
				}

			}
		});

		lblsa.add(ar,new Label());
		lblsa.get(ar).setWidth("100%");
		lblsa.get(ar).setHeight("16px");
		lblsa.get(ar).setValue(ar+1);

		lblAutoEmployeeID.add(ar, new Label());
		lblAutoEmployeeID.get(ar).setWidth("100%");

		lblEmployeeCode.add(ar, new Label());
		lblEmployeeCode.get(ar).setWidth("100%");

		lblProximityID.add(ar, new Label());
		lblProximityID.get(ar).setWidth("100%");

		lblEmployeeName.add(ar,new Label());
		lblEmployeeName.get(ar).setWidth("100%");

		lblDesignation.add(ar, new Label());
		lblDesignation.get(ar).setWidth("100%");

		lblTotalDays.add(ar, new Label());
		lblTotalDays.get(ar).setWidth("100%");

		lblWorkDays.add(ar, new Label());
		lblWorkDays.get(ar).setWidth("100%");

		lblPresentDays.add(ar, new Label());
		lblPresentDays.get(ar).setWidth("100%");

		lblAbsentDays.add(ar, new Label());
		lblAbsentDays.get(ar).setWidth("100%");

		lblLeaveDays.add(ar, new Label());
		lblLeaveDays.get(ar).setWidth("100%");

		lblHoliDays.add(ar, new Label());
		lblHoliDays.get(ar).setWidth("100%");

		lblFriDays.add(ar, new Label());
		lblFriDays.get(ar).setWidth("100%");

		lblGross.add(ar, new Label());
		lblGross.get(ar).setWidth("100%");

		lblBasic.add(ar, new Label());
		lblBasic.get(ar).setWidth("100%");

		lblHouseRent.add(ar, new Label());
		lblHouseRent.get(ar).setWidth("100%");

		lblConveyance.add(ar, new Label());
		lblConveyance.get(ar).setWidth("100%");

		lblMedicalAll.add(ar, new Label());
		lblMedicalAll.get(ar).setWidth("100%");

		lblAttBonus.add(ar, new Label());
		lblAttBonus.get(ar).setWidth("100%");

		lblFridayAllow.add(ar, new Label());
		lblFridayAllow.get(ar).setWidth("100%");

		lblAbsentAmount.add(ar, new Label());
		lblAbsentAmount.get(ar).setWidth("100%");

		lblLoanNo.add(ar, new Label());
		lblLoanNo.get(ar).setWidth("100%");

		lblLoanTransactionNo.add(ar, new Label());
		lblLoanTransactionNo.get(ar).setWidth("100%");

		txtAdvSalary.add(ar, new AmountField());
		txtAdvSalary.get(ar).setWidth("100%");
		txtAdvSalary.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtAdvSalary.get(ar).getValue().toString().trim().equals(""))
					chkLoanPerInstallMent(ar);
			}
		});

		txtIncomeTax.add(ar, new AmountField());
		txtIncomeTax.get(ar).setWidth("100%");

		txtInsurance.add(ar, new AmountField());
		txtInsurance.get(ar).setWidth("100%");

		txtAdjust.add(ar, new AmountField());
		txtAdjust.get(ar).setWidth("100%");

		txtLessAdjust.add(ar, new AmountField());
		txtLessAdjust.get(ar).setWidth("100%");

		table.addItem(new Object[]{btnDel.get(ar),lblsa.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeCode.get(ar),lblProximityID.get(ar),lblEmployeeName.get(ar),
				lblDesignation.get(ar),lblTotalDays.get(ar),lblWorkDays.get(ar),lblPresentDays.get(ar),lblAbsentDays.get(ar),lblLeaveDays.get(ar),
				lblHoliDays.get(ar),lblFriDays.get(ar),lblGross.get(ar),lblBasic.get(ar),lblHouseRent.get(ar),lblConveyance.get(ar),lblMedicalAll.get(ar),
				lblAttBonus.get(ar),lblFridayAllow.get(ar),lblAbsentAmount.get(ar), lblLoanNo.get(ar), lblLoanTransactionNo.get(ar), txtAdvSalary.get(ar), txtIncomeTax.get(ar),
				txtInsurance.get(ar),txtAdjust.get(ar),txtLessAdjust.get(ar)},ar);
	}

	private void chkLoanPerInstallMent(int i)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select ISNULL(mAmountPerInstall,0) from tbLoanApplication where vLoanNo='"+lblLoanNo.get(i).getValue().toString().trim()+"' and vAutoEmployeeId='"+lblAutoEmployeeID.get(i).getValue().toString().trim()+"'";
			List <?> lst=session.createSQLQuery(query).list();
			double loanAmount=0.0;
			if(!lst.isEmpty())
			{
				loanAmount=Double.parseDouble(lst.iterator().next().toString().trim());
			}
			if(loanAmount<Double.parseDouble((txtAdvSalary.get(i).getValue().toString().trim().length()==0?"0":txtAdvSalary.get(i).getValue().toString().trim())))
			{
				txtAdvSalary.get(i).setValue(0);
				showNotification("Warning", "Amount is Greater than Loan Per Installment!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("chkLoanPerInstallMent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
}