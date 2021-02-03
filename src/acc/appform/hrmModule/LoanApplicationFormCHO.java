package acc.appform.hrmModule;

	import java.text.DecimalFormat;
	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Iterator;
	import java.util.List;
	import org.hibernate.Session;
	import org.hibernate.Transaction;

	import com.common.share.AmountCommaSeperator;
	import com.common.share.AmountField;
	import com.common.share.CommaSeparator;
	import com.common.share.CommonButton;
	import com.common.share.FocusMoveByEnter;
	import com.common.share.MessageBox;
	import com.common.share.SessionBean;
	import com.common.share.SessionFactoryUtil;
	import com.common.share.TextRead;
	import com.common.share.MessageBox.ButtonType;
	import com.common.share.MessageBox.EventListener;
	import com.vaadin.data.Property.ValueChangeEvent;
	import com.vaadin.data.Property.ValueChangeListener;
	import com.vaadin.ui.AbsoluteLayout;
	import com.vaadin.ui.Button.ClickListener;
	import com.vaadin.ui.CheckBox;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.Component;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.PopupDateField;
	import com.vaadin.ui.Table;
	import com.vaadin.ui.TextField;
	import com.vaadin.ui.Window;
	import com.vaadin.ui.Button.ClickEvent;

public class LoanApplicationFormCHO extends Window {
		private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");
		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;
		public Table table = new Table();
		public ArrayList<Label> tbllblSerial = new ArrayList<Label>();
		public ArrayList<Label> tbllblDate = new ArrayList<Label>();
		public ArrayList<Label> tbllblWeekDay = new ArrayList<Label>();
		public ArrayList<ComboBox> tblCmbNatureOfLeave = new ArrayList<ComboBox>();
		public ArrayList<CheckBox> tblChkEnjoyed = new ArrayList<CheckBox>();

		HashMap <String,Object> hDateDayList = new HashMap <String,Object> ();
		private static final String[] adjustType = new String[] {"Monthly Salary","Others"};

		private Label lblApplicationDate;
		private PopupDateField dApplicationDate;

		private Label lblLoanNo;
		private TextRead txtLoanNo;

		private Label lblEmployeeId;
		private ComboBox cmbEmployeeId;

		private Label lblEmployeeName;
		private TextRead txtEmployeeName;

		private Label lblDepartment;
		private TextRead txtDepartment;

		private Label lblSection;
		private TextRead txtSection;

		private Label lblDesignation;
		private TextRead txtDesignation;

		private Label lblJoiningDate;
		private PopupDateField dJoiningDate;

		private Label lblGrossSalary;
		private TextRead txtGrossSalary;

		private Label lblLoanType;
		private ComboBox cmbLoanType;
		private static final String[] loanType = new String[] {"General Loan","Salary Loan","Others"};

		private Label lblLoanAmount;
		private AmountCommaSeperator txtLoanAmount;

		private Label lblRateOfInterest;
		private AmountField txtRateOfInterest;
		private Label lblRateOfInterestP;

		private Label lblInterestAmount;
		private TextRead txtInterestAmount;

		private Label lblGrossLoanAmount;
		private TextRead txtGrossLoanAmount;

		private Label lblLoanPurpose;
		private TextField txtLoanPurpose;

		private Label lblNoOfInstallment;
		private AmountField txtNoOfInstallment;

		private Label lblAmountOfInstallment;
		private TextRead txtAmountOfInstallment;

		private Label lblPaymentStart;
		private PopupDateField dPaymentStart;

		private Label lblAdjustType;
		private ComboBox cmbAdjustType;

		private Label lblLoanSanction;
		private Label lblSanctionDate;
		private PopupDateField dSanctionDate;

		private Label lblSanctionAmount;
		private AmountCommaSeperator txtSanctionAmount;

		private TextRead txtFindLoanNo = new TextRead();
		private TextRead txtFindDate = new TextRead();

		private String updateID="";

		private ArrayList<Component> allComp = new ArrayList<Component>(); 

		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

		private DecimalFormat df = new DecimalFormat("#0.00");

		private boolean isUpdate=false;
		private boolean pendingLoan = false;
		private boolean activeLoan = false;

		private String isSanction = "";

		String empId = "";
		String departmentId = "";
		String sectionId = "";
		String designationId = "";

		String findApplicationDate = "";	

		public LoanApplicationFormCHO(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("LOAN APPLICATION CHO:: "+sessionBean.getCompany());
			this.setResizable(false);

			buildMainLayout();
			setContent(mainLayout);
			btnIni(true);
			componentIni(false);
			setBtnAction();
			cButton.btnNew.focus();
			focusEnter();
			cmbAddEmployeeData();
		}

		private void setBtnAction()
		{
			cButton.btnNew.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					isSanction = "";
					pendingLoan = false;
					activeLoan = false;
					txtClear();
					componentIni(true);
					btnIni(false);
					cmbEmployeeId.focus();
					txtLoanNo.setValue(autoId());
				}
			});

			cButton.btnSave.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					formValidation();
				}
			});

			cmbEmployeeId.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbEmployeeId.getValue()!=null)
					{
						employeeSetData(cmbEmployeeId.getValue().toString());
					}
				}
			});

			cButton.btnFind.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					findButtonEvent();
				}
			});

			cButton.btnRefresh.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					isUpdate = false;
					isSanction = "";
					pendingLoan = false;
					activeLoan = false;
					txtClear();
					componentIni(false);
					btnIni(true);
				}
			});

			cButton.btnEdit.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					if(!txtLoanNo.getValue().toString().equals("") && cmbEmployeeId.getValue()!=null)
					{
						isUpdate=true;
						componentIni(true);
						btnIni(false);
						if(isSanction.equals("1"))
						{
							txtSanctionAmount.setEnabled(false);
							dSanctionDate.setEnabled(false);
						}
						else
						{
							txtSanctionAmount.setEnabled(true);
							dSanctionDate.setEnabled(true);
						}
					}
					else
					{
						showNotification("There are nothing to edit", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			});

			cButton.btnExit.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					close();
				}
			});

			txtLoanAmount.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!txtRateOfInterest.getValue().toString().isEmpty() && !txtLoanAmount.getValue().toString().isEmpty())
					{
						double Loan = Double.parseDouble(txtLoanAmount.getValue().toString().replace(",", ""));
						double interest = Double.parseDouble(txtRateOfInterest.getValue().toString().replace(",", ""));
						double totalInterest = (Loan*interest)/100;

						txtInterestAmount.setValue(new CommaSeparator().setComma(totalInterest));
						txtGrossLoanAmount.setValue(new CommaSeparator().setComma(totalInterest+Loan));
					}
				}
			});

			txtRateOfInterest.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!txtLoanAmount.getValue().toString().isEmpty())
					{
						double Loan = Double.parseDouble(txtLoanAmount.getValue().toString().replace(",", ""));
						double interest = Double.parseDouble(txtRateOfInterest.getValue().toString().replace(",", ""));
						double totalInterest = (Loan*interest)/100;

						txtInterestAmount.setValue(new CommaSeparator().setComma(totalInterest));
						txtGrossLoanAmount.setValue(new CommaSeparator().setComma(totalInterest+Loan));
					}
				}
			});

			txtNoOfInstallment.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!txtNoOfInstallment.getValue().toString().trim().isEmpty())
					{
						if(Double.parseDouble(txtNoOfInstallment.getValue().toString().trim())<=0)
						{
							txtNoOfInstallment.setValue("");
							txtNoOfInstallment.focus();
						}
						else if(!txtGrossLoanAmount.getValue().toString().isEmpty() && Double.parseDouble(txtNoOfInstallment.getValue().toString().trim())>0)
						{
							double grossAmount = Double.parseDouble(txtGrossLoanAmount.getValue().toString().replace(",", ""));
							double noOfInstall = Double.parseDouble(txtNoOfInstallment.getValue().toString().replace(",", ""));

							txtAmountOfInstallment.setValue(new CommaSeparator().setComma(grossAmount/noOfInstall));
						}
					}
				}
			});

			txtSanctionAmount.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!txtSanctionAmount.getValue().toString().isEmpty())
					{
						if(!txtRateOfInterest.getValue().toString().isEmpty())
						{
							double Loan = Double.parseDouble(txtSanctionAmount.getValue().toString().replace(",", ""));
							double interest = Double.parseDouble(txtRateOfInterest.getValue().toString().replace(",", ""));
							double totalInterest = (Loan*interest)/100;

							txtInterestAmount.setValue(new CommaSeparator().setComma(totalInterest));
							txtGrossLoanAmount.setValue(new CommaSeparator().setComma(totalInterest+Loan));
						}

						if(!txtGrossLoanAmount.getValue().toString().isEmpty())
						{
							double grossAmount = Double.parseDouble(txtGrossLoanAmount.getValue().toString().replace(",", ""));
							double noOfInstall = Double.parseDouble(txtNoOfInstallment.getValue().toString().replace(",", ""));

							txtAmountOfInstallment.setValue(new CommaSeparator().setComma(grossAmount/noOfInstall));
						}
					}
				}
			});
		}

		private void cmbAddEmployeeData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "Select vEmployeeID,employeeCode FROM tbEmployeeInfo where ISNULL(vProximityId,'')!='' and iStatus=1 and vDepartmentId='DEPT10'";

				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbEmployeeId.addItem(element[0]);
					cmbEmployeeId.setItemCaption(element[0], element[1].toString());	
				}
			}
			catch(Exception ex)
			{
				showNotification("cmbAddEmployeeData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void findButtonEvent() 
		{
			Window win = new LoanAppFind(sessionBean, txtFindLoanNo, txtFindDate);
			win.addListener(new Window.CloseListener() 
			{
				public void windowClose(CloseEvent e) 
				{
					if (txtFindLoanNo.getValue().toString().length() > 0)
					{
						txtClear();
						findInitialise(txtFindLoanNo.getValue().toString() , txtFindDate.getValue());
					}
				}
			});

			this.getParent().addWindow(win);
		}

		private Object autoId()
		{
			Object autoDate = null;
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try 
			{
				String query = " Select cast(isnull(max(cast(replace(vLoanNo, '', '')as int))+1, 1)as varchar) as loanId from tbLoanApplication ";

				Iterator <?> iter = session.createSQLQuery(query).list().iterator();

				if (iter.hasNext()) 
				{
					autoDate = iter.next();
				}
			} 
			catch (Exception ex) 
			{
				showNotification("Warning", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			return autoDate;
		}

		private void findInitialise(String LoanId, Object findDate) 
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();

			try 
			{
				String sql="select iAutoId,vLoanNo,dApplicationDate,vAutoEmployeeId,vLoanType,mLoanAmount,mInterestRate," +
						"mInterestAmount,mGrossAmount,vLoanPurpose,mInstallmentNo,mAmountPerInstall,dPaymentStart," +
						"vAdjustType,dSanctionDate,mSanctionAmount,iSanctionStatus,iLoanStatus,mLoanBalance,vUserId," +
						"vUserIp,dEntryTime from tbLoanApplication where vLoanNo = '"+LoanId+"' and dApplicationDate = '"+findDate+"' ";
				List <?> list = session.createSQLQuery(sql).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();

					txtLoanNo.setValue(element[1].toString());
					dApplicationDate.setValue((element[2]));
					cmbEmployeeId.setValue(element[3]);
					cmbLoanType.setValue(element[4]);
					txtLoanAmount.setValue(df.format(Double.parseDouble(element[5].toString())));
					txtRateOfInterest.setValue(df.format(Double.parseDouble(element[6].toString())));
					txtInterestAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[7].toString())));
					txtGrossLoanAmount.setValue(new CommaSeparator().setComma(Double.parseDouble(element[8].toString())));
					txtLoanPurpose.setValue(element[9].toString());
					txtNoOfInstallment.setValue(df.format(Double.parseDouble(element[10].toString())));
					txtAmountOfInstallment.setValue(new CommaSeparator().setComma(Double.parseDouble(element[11].toString())));
					dPaymentStart.setValue(element[12]);
					cmbAdjustType.setValue(element[13]);

					if(element[14].toString().equals("1900-01-01 00:00:00.0"))
					{
						dSanctionDate.setValue(new java.util.Date());
					}
					else
					{
						dSanctionDate.setValue(element[14]);
					}
					if(Double.parseDouble(element[15].toString())!=0)
					{
						txtSanctionAmount.setValue(df.format(Double.parseDouble(element[15].toString())));
					}

					updateID = element[0].toString();
					findApplicationDate = element[2].toString();
					isSanction = element[16].toString();
				}
			}
			catch (Exception exp)
			{
				showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void employeeSetData(String employeeId)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = " SELECT E.vEmployeeName, E.employeeCode,E.vGender, E.vDepartmentID," +
						"Dept.vDepartmentName, S.vSectionId, S.SectionName, D.designationId, D.designationName," +
						"E.dJoiningDate, mOthersAllowance as mGrossSalary,E.vProximityID FROM tbEmployeeInfo AS " +
						"E inner join tbDepartmentInfo Dept on Dept.vDepartmentID=E.vDepartmentID  INNER JOIN " +
						"tbSectionInfo AS S ON E.vSectionId = S.vSectionId INNER JOIN tbDesignationInfo AS D ON " +
						"E.vDesignationId = D.designationId WHERE E.vEmployeeId = '"+employeeId+"' ORDER BY E.vEmployeeType ";

				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();

					txtEmployeeName.setValue(element[0].toString());
					txtDepartment.setValue(element[4].toString());
					txtSection.setValue(element[6].toString());
					txtDesignation.setValue(element[8].toString());
					dJoiningDate.setValue(element[9]);
					txtGrossSalary.setValue(new CommaSeparator().setComma(Double.parseDouble(element[10].toString())));

					empId = element[11].toString();
					departmentId = element[3].toString();
					sectionId = element[5].toString();
					designationId = element[7].toString();
				}

				String pending = " select * from tbLoanApplication where vAutoEmployeeId = '"+employeeId+"' and iSanctionStatus = '0' " ;

				List <?> pendingList = session.createSQLQuery(pending).list();

				if(pendingList.iterator().hasNext())
				{
					pendingLoan = true;
				}
				else
				{
					pendingLoan = false;
				}

				String active = " select * from tbLoanApplication where vAutoEmployeeId = '"+employeeId+"' and iLoanStatus = '1' and iSanctionStatus='1' " ;

				List <?> activeLoanList = session.createSQLQuery(active).list();

				if(activeLoanList.iterator().hasNext())
				{
					activeLoan = true;
				}
				else
				{
					activeLoan = false;
				}
			}
			catch(Exception ex)
			{
				showNotification("employeeSetData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private boolean chkLoanApplication(String query)
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				List <?> lst=session.createSQLQuery(query).list();
				if(!lst.isEmpty())
					return true;
			}
			catch(Exception exp)
			{
				showNotification("formValidation", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			return false;
		}

		private void formValidation()
		{
			if(cmbEmployeeId.getValue()!=null)
			{
				if(cmbLoanType.getValue()!=null)
				{
					if(!txtLoanAmount.getValue().toString().isEmpty())
					{
						if(!txtRateOfInterest.getValue().toString().equals(""))
						{
							if(!txtLoanPurpose.getValue().toString().equals(""))
							{
								if(!txtNoOfInstallment.getValue().toString().equals(""))
								{
									if(cmbAdjustType.getValue()!=null)
									{
										if(isUpdate)
										{
											String query="select * from tbLoanApplication where mLoanBalance=mSanctionAmount and vLoanNo = '"+txtFindLoanNo.getValue().toString()+"' and dApplicationDate = '"+txtFindDate.getValue()+"' ";
											if(chkLoanApplication(query))
											{
												saveBtnAction();
											}
											else
											{
												showNotification("Warning", "Please don't try to Update!!!", Notification.TYPE_WARNING_MESSAGE);
											}
										}
										else
										{
											saveBtnAction();
										}
									}
									else
									{
										showNotification("Warning!","Select Adjust Type", Notification.TYPE_WARNING_MESSAGE);
										cmbAdjustType.focus();
									}
								}
								else
								{
									showNotification("Warning!","Provide No of Loan Installment", Notification.TYPE_WARNING_MESSAGE);
									txtNoOfInstallment.focus();
								}
							}
							else
							{
								showNotification("Warning!","Provide Loan Purpose", Notification.TYPE_WARNING_MESSAGE);
								txtLoanPurpose.focus();
							}
						}
						else
						{
							showNotification("Warning!","Provide Rate of Interest", Notification.TYPE_WARNING_MESSAGE);
							txtRateOfInterest.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide Loan Amount", Notification.TYPE_WARNING_MESSAGE);
						txtLoanAmount.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Loan Type", Notification.TYPE_WARNING_MESSAGE);
					cmbLoanType.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Employee Name", Notification.TYPE_WARNING_MESSAGE);
				cmbEmployeeId.focus();
			}
		}

		private void saveBtnAction()
		{
			if(isUpdate)
			{
				if(!txtSanctionAmount.getValue().toString().equals("") && Double.parseDouble(txtSanctionAmount.getValue().toString())>0)
				{
					if(!activeLoan)
					{
						MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
						mb.show(new EventListener()
						{
							public void buttonClicked(ButtonType buttonType)
							{
								if(buttonType == ButtonType.YES)
								{
									updateData();
									isUpdate = false;
									componentIni(false);
									btnIni(true);
									txtClear();
									cButton.btnNew.focus();
								}
							}
						});
					}
					else
					{
						showNotification("Warning!","This employee has a unbalanced active loan",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning!","Provide sanction amount",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				if(!pendingLoan)
				{

					MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
					mb.show(new EventListener()
					{
						public void buttonClicked(ButtonType buttonType)
						{
							if(buttonType == ButtonType.YES)
							{
								insertData();
								isUpdate = false;
								componentIni(false);
								btnIni(true);
								txtClear();
								cButton.btnNew.focus();
							}
						}
					});

				}
				else
				{
					showNotification("Warning!","This employee has a pending loan request",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}

		private void insertData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			String insertQuery = ""; 

			try
			{
				insertQuery = "Insert into tbLoanApplication (vLoanNo,dApplicationDate,vAutoEmployeeId,vEmployeeId,vProximityID,vSectionId,vDesignationId,dJoiningDate,mGrossSalary,vLoanType,mLoanAmount," +
						"mInterestRate,mInterestAmount,mGrossAmount,vLoanPurpose,mInstallmentNo,mAmountPerInstall,dPaymentStart,vAdjustType,dSanctionDate,mSanctionAmount,iSanctionStatus,iLoanStatus," +
						"mLoanBalance,vUserId,vUserIp,dEntryTime,vDepartmentId) values (" +
						" '"+autoId()+"', '"+dateFormat.format(dApplicationDate.getValue())+"','" +cmbEmployeeId.getValue().toString().trim()+ "',"+
						" '"+cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue())+"',(select vProximityID from tbEmployeeInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"'),'"+sectionId+"', '"+designationId+"'," +
						" '"+dateFormat.format(dJoiningDate.getValue())+"','"+txtGrossSalary.getValue().toString().replace(",", "")+"', " +
						" '"+cmbLoanType.getValue().toString()+"', '"+txtLoanAmount.getValue().toString().replace(",", "")+"', '"+txtRateOfInterest.getValue().toString()+"'," +
						" '"+txtInterestAmount.getValue().toString().replace(",", "")+"', '"+txtGrossLoanAmount.getValue().toString().replace(",", "")+"', '"+txtLoanPurpose.getValue().toString()+"'," +
						" '"+txtNoOfInstallment.getValue().toString()+"', '"+txtAmountOfInstallment.getValue().toString().replace(",", "")+"', '"+dateFormat.format(dPaymentStart.getValue())+"'," +
						" '"+cmbAdjustType.getValue().toString()+"', '', '0', '0', '1', '0'," +
						" '"+sessionBean.getUserId()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP,'"+departmentId+"') ";
				session.createSQLQuery(insertQuery).executeUpdate();

				tx.commit();
				showNotification("All Information Save Successfully");
			}
			catch(Exception ex)
			{
				tx.rollback();
				showNotification("insertData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void updateData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			String updateQuery = "";

			try
			{
				updateQuery = "UPDATE tbLoanApplication set " +
						" vLoanNo = '"+txtLoanNo.getValue().toString()+"' ," +
						" vAutoEmployeeId = '"+cmbEmployeeId.getValue().toString().trim()+"' ,"+
						" vEmployeeId = '"+cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue())+"' ," +
						" vProximityID = (select vProximityID from tbEmployeeInfo where vEmployeeId='"+cmbEmployeeId.getValue().toString().trim()+"'),"+
						" vSectionId = '"+sectionId+"' ," +
						" vDesignationId = '"+designationId+"' ," +
						" dJoiningDate = '"+dateFormat.format(dJoiningDate.getValue())+"' ," +
						" mGrossSalary = '"+txtGrossSalary.getValue().toString()+"' ," +
						" vLoanType = '"+cmbLoanType.getValue()+"' ," +
						" mLoanAmount = '"+txtLoanAmount.getValue()+"' ," +
						" mInterestRate = '"+txtRateOfInterest.getValue()+"' ," +
						" mInterestAmount = '"+txtInterestAmount.getValue()+"' ," +
						" mGrossAmount = '"+txtGrossLoanAmount.getValue()+"' ," +
						" vLoanPurpose = '"+txtLoanPurpose.getValue()+"' ," +
						" mInstallmentNo = '"+txtNoOfInstallment.getValue()+"' ," +
						" mAmountPerInstall = '"+txtAmountOfInstallment.getValue()+"' , " +
						" dPaymentStart = '"+dateFormat.format(dPaymentStart.getValue())+"' ," +
						" vAdjustType = '"+cmbAdjustType.getValue()+"' ," +
						" dSanctionDate = '"+dateFormat.format(dSanctionDate.getValue())+"' ," +
						" mSanctionAmount = '"+txtSanctionAmount.getValue().toString()+"' ," +
						" iSanctionStatus = '1' ," +
						" iLoanStatus = '1' ," +
						" mLoanBalance = '"+txtGrossLoanAmount.getValue()+"' ," +
						" dEntryTime=CURRENT_TIMESTAMP, vUserIp='"+sessionBean.getUserIp()+"', " +
						" vUserId='"+sessionBean.getUserId()+"'," +
						" vDepartmentID='"+departmentId+"'" +
						" where iAutoId='"+updateID+"' ";
				session.createSQLQuery(updateQuery).executeUpdate();

				tx.commit();
				showNotification("All Information Updated Successfully");
			}
			catch(Exception ex)
			{
				tx.rollback();
				showNotification("updateData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void btnIni(boolean t)
		{
			cButton.btnNew.setEnabled(t);
			cButton.btnEdit.setEnabled(t);
			cButton.btnSave.setEnabled(!t);
			cButton.btnRefresh.setEnabled(!t);
			cButton.btnDelete.setEnabled(t);
			cButton.btnFind.setEnabled(t);
		}

		private void componentIni(boolean t)
		{
			if(isUpdate==true)
			{dApplicationDate.setEnabled(!t);}
			else
			{dApplicationDate.setEnabled(t);}
			txtLoanNo.setEnabled(t);
			cmbEmployeeId.setEnabled(t);
			txtEmployeeName.setEnabled(t);
			txtDepartment.setEnabled(t);
			txtSection.setEnabled(t);
			txtDesignation.setEnabled(t);
			txtGrossSalary.setEnabled(t);
			cmbLoanType.setEnabled(t);
			txtLoanAmount.setEnabled(t);
			txtRateOfInterest.setEnabled(t);
			txtInterestAmount.setEnabled(t);
			txtGrossLoanAmount.setEnabled(t);
			txtLoanPurpose.setEnabled(t);
			txtNoOfInstallment.setEnabled(t);
			txtAmountOfInstallment.setEnabled(t);
			dPaymentStart.setEnabled(t);
			cmbAdjustType.setEnabled(t);

			txtSanctionAmount.setEnabled(false);
			dSanctionDate.setEnabled(false);

			if(isSanction.equals("1"))
			{
				cmbEmployeeId.setEnabled(false);
				txtLoanAmount.setEnabled(false);
				txtRateOfInterest.setEnabled(false);
				txtNoOfInstallment.setEnabled(false);
				cmbAdjustType.setEnabled(false);
			}
		}

		private void txtClear()
		{
			dApplicationDate.setValue(new java.util.Date());
			txtLoanNo.setValue("");
			cmbEmployeeId.setValue(null);
			txtEmployeeName.setValue("");
			txtDepartment.setValue("");
			txtSection.setValue("");
			txtDesignation.setValue("");
			dJoiningDate.setValue(new java.util.Date());
			txtGrossSalary.setValue("");
			cmbLoanType.setValue(null);
			txtLoanAmount.setValue("");
			txtRateOfInterest.setValue("");
			txtInterestAmount.setValue("");
			txtGrossLoanAmount.setValue("");
			txtLoanPurpose.setValue("");
			txtNoOfInstallment.setValue("");
			txtAmountOfInstallment.setValue("");
			dPaymentStart.setValue(new java.util.Date());
			cmbAdjustType.setValue(null);

			txtSanctionAmount.setValue("");
			dSanctionDate.setValue(new java.util.Date());
		}

		private void focusEnter()
		{
			allComp.add(dApplicationDate);
			allComp.add(cmbEmployeeId);
			allComp.add(cmbLoanType);
			allComp.add(txtLoanAmount);
			allComp.add(txtRateOfInterest);
			allComp.add(txtLoanPurpose);
			allComp.add(txtNoOfInstallment);
			allComp.add(dPaymentStart);
			allComp.add(cmbAdjustType);

			allComp.add(cButton.btnSave);

			new FocusMoveByEnter(this,allComp);
		}

		public AbsoluteLayout buildMainLayout()
		{
			// common part: create layout
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setWidth("780px");
			mainLayout.setHeight("360px");

			// lblApplicationDate
			lblApplicationDate = new Label("Application Date : ");
			lblApplicationDate.setImmediate(false);
			lblApplicationDate.setWidth("-1px");
			lblApplicationDate.setHeight("-1px");
			mainLayout.addComponent(lblApplicationDate, "top:20.0px; left:30.0px;");

			// dApplicationDate
			dApplicationDate = new PopupDateField();
			dApplicationDate.setImmediate(false);
			dApplicationDate.setWidth("110px");
			dApplicationDate.setHeight("-1px");
			dApplicationDate.setValue(new java.util.Date());
			dApplicationDate.setDateFormat("dd-MM-yyyy");
			dApplicationDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dApplicationDate, "top:19.0px; left:140.0px;");

			// lblLoanNo
			lblLoanNo = new Label("Loan No :");
			lblLoanNo.setImmediate(false);
			lblLoanNo.setWidth("-1px");
			lblLoanNo.setHeight("-1px");
			mainLayout.addComponent(lblLoanNo, "top:45.0px; left:30.0px;");

			// txtLoanNo
			txtLoanNo = new TextRead();
			txtLoanNo.setImmediate(true);
			txtLoanNo.setWidth("70px");
			txtLoanNo.setHeight("22px");
			mainLayout.addComponent(txtLoanNo, "top:44.0px; left:141.0px;");

			// lblEmployeeId
			lblEmployeeId = new Label("Employee ID :");
			lblEmployeeId.setImmediate(false);
			lblEmployeeId.setWidth("-1px");
			lblEmployeeId.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeId, "top:70.0px; left:30.0px;");

			// cmbEmployeeId
			cmbEmployeeId = new ComboBox();
			cmbEmployeeId.setImmediate(true);
			cmbEmployeeId.setWidth("230px");
			cmbEmployeeId.setHeight("-1px");
			cmbEmployeeId.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbEmployeeId, "top:68.0px; left:140.0px;");

			// lblEmployeeName
			lblEmployeeName = new Label("Employee Name :");
			lblEmployeeName.setImmediate(false);
			lblEmployeeName.setWidth("-1px");
			lblEmployeeName.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeName, "top:95.0px; left:30.0px;");

			// txtEmployeeName
			txtEmployeeName = new TextRead();
			txtEmployeeName.setImmediate(true);
			txtEmployeeName.setWidth("110px");
			txtEmployeeName.setHeight("22px");
			mainLayout.addComponent(txtEmployeeName, "top:93.0px; left:141.0px;");

			// lblDepartment
			lblDepartment = new Label("Department Name :");
			lblDepartment.setImmediate(false);
			lblDepartment.setWidth("-1px");
			lblDepartment.setHeight("-1px");
			mainLayout.addComponent(lblDepartment, "top:120.0px; left:30.0px;");

			// txtSection
			txtDepartment = new TextRead();
			txtDepartment.setImmediate(true);
			txtDepartment.setWidth("220px");
			txtDepartment.setHeight("22px");
			mainLayout.addComponent(txtDepartment, "top:118.0px; left:141.0px;");

			// lblSection
			lblSection = new Label("Section Name :");
			lblSection.setImmediate(false);
			lblSection.setWidth("-1px");
			lblSection.setHeight("-1px");
			mainLayout.addComponent(lblSection, "top:145.0px; left:30.0px;");

			// txtSection
			txtSection = new TextRead();
			txtSection.setImmediate(true);
			txtSection.setWidth("220px");
			txtSection.setHeight("22px");
			mainLayout.addComponent(txtSection, "top:143.0px; left:141.0px;");

			// lblDesignation
			lblDesignation = new Label("Designation :");
			lblDesignation.setImmediate(false);
			lblDesignation.setWidth("-1px");
			lblDesignation.setHeight("-1px");
			mainLayout.addComponent(lblDesignation, "top:170.0px; left:30.0px;");

			// txtDesignation
			txtDesignation = new TextRead();
			txtDesignation.setImmediate(true);
			txtDesignation.setWidth("220px");
			txtDesignation.setHeight("22px");
			mainLayout.addComponent(txtDesignation, "top:168.0px; left:141.0px;");

			// lblJoiningDate
			lblJoiningDate = new Label("Joining Date : ");
			lblJoiningDate.setImmediate(false);
			lblJoiningDate.setWidth("-1px");
			lblJoiningDate.setHeight("-1px");
			mainLayout.addComponent(lblJoiningDate, "top:195.0px; left:30.0px;");

			// dJoiningDate
			dJoiningDate = new PopupDateField();
			dJoiningDate.setImmediate(true);
			dJoiningDate.setWidth("110px");
			dJoiningDate.setHeight("-1px");
			dJoiningDate.setValue(new java.util.Date());
			dJoiningDate.setDateFormat("dd-MM-yyyy");
			dJoiningDate.setEnabled(false);
			dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dJoiningDate, "top:193.0px; left:140.0px;");

			// lblGrossSalary
			lblGrossSalary = new Label("Gross Salary :");
			lblGrossSalary.setImmediate(false);
			lblGrossSalary.setWidth("-1px");
			lblGrossSalary.setHeight("-1px");
			mainLayout.addComponent(lblGrossSalary, "top:220.0px; left:30.0px;");

			// txtGrossSalary
			txtGrossSalary = new TextRead(1);
			txtGrossSalary.setImmediate(true);
			txtGrossSalary.setWidth("100px");
			txtGrossSalary.setHeight("24px");
			mainLayout.addComponent(txtGrossSalary, "top:218.0px; left:140.0px;");

			// lblLoanSanction
			lblLoanSanction = new Label("<html><font color='#74078B'> <b>Sanction Details</b></font></html>",Label.CONTENT_XHTML);
			lblLoanSanction.setImmediate(false);
			lblLoanSanction.setWidth("-1px");
			lblLoanSanction.setHeight("-1px");
			mainLayout.addComponent(lblLoanSanction, "top:247.0px; left:85.0px;");

			// lblSanctionDate
			lblSanctionDate = new Label("Sanction Date :");
			lblSanctionDate.setImmediate(false);
			lblSanctionDate.setWidth("-1px");
			lblSanctionDate.setHeight("-1px");
			mainLayout.addComponent(lblSanctionDate, "top:272.0px; left:30.0px;");

			// dSanctionDate
			dSanctionDate = new PopupDateField();
			dSanctionDate.setImmediate(true);
			dSanctionDate.setWidth("110px");
			dSanctionDate.setHeight("-1px");
			dSanctionDate.setValue(new java.util.Date());
			dSanctionDate.setDateFormat("dd-MM-yyyy");
			dSanctionDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dSanctionDate, "top:270.0px; left:140.0px;");

			// lblSanctionAmount
			lblSanctionAmount = new Label("Sanction Amount :");
			lblSanctionAmount.setImmediate(false);
			lblSanctionAmount.setWidth("-1px");
			lblSanctionAmount.setHeight("-1px");
			mainLayout.addComponent(lblSanctionAmount, "top:295.0px; left:30.0px;");

			// txtSanctionAmount
			txtSanctionAmount = new AmountCommaSeperator();
			txtSanctionAmount.setImmediate(true);
			txtSanctionAmount.setWidth("100px");
			txtSanctionAmount.setHeight("-1px");
			mainLayout.addComponent(txtSanctionAmount, "top:293.0px; left:140.0px;");

			// lblLoanType
			lblLoanType = new Label("Loan Type :");
			lblLoanType.setImmediate(false);
			lblLoanType.setWidth("-1px");
			lblLoanType.setHeight("-1px");
			mainLayout.addComponent(lblLoanType, "top:20.0px; left:400.0px;");

			// cmbLoanType
			cmbLoanType = new ComboBox();
			cmbLoanType.setImmediate(true);
			cmbLoanType.setWidth("130px");
			cmbLoanType.setHeight("-1px");
			mainLayout.addComponent(cmbLoanType, "top:18.0px; left:545.0px;");
			for(int i=0; i<loanType.length; i++)
			{cmbLoanType.addItem(loanType[i]);}

			// lblLoanAmount
			lblLoanAmount = new Label("Loan Amount :");
			lblLoanAmount.setImmediate(false);
			lblLoanAmount.setWidth("-1px");
			lblLoanAmount.setHeight("-1px");
			mainLayout.addComponent(lblLoanAmount, "top:45.0px; left:400.0px;");

			// txtLoanAmount
			txtLoanAmount = new AmountCommaSeperator();
			txtLoanAmount.setImmediate(true);
			txtLoanAmount.setWidth("110px");
			txtLoanAmount.setHeight("-1px");
			mainLayout.addComponent(txtLoanAmount, "top:43.0px; left:545.0px;");

			// lblRateOfInterest
			lblRateOfInterest = new Label("Rate of Interest :");
			lblRateOfInterest.setImmediate(false);
			lblRateOfInterest.setWidth("-1px");
			lblRateOfInterest.setHeight("-1px");
			mainLayout.addComponent(lblRateOfInterest, "top:70.0px; left:400.0px;");

			// txtRateOfInterest
			txtRateOfInterest = new AmountField();
			txtRateOfInterest.setImmediate(true);
			txtRateOfInterest.setWidth("50px");
			txtRateOfInterest.setHeight("-1px");
			mainLayout.addComponent(txtRateOfInterest, "top:68.0px; left:545.0px;");

			// lblRateOfInterestP
			lblRateOfInterestP = new Label("<html> <b>%</b> </html>",Label.CONTENT_XHTML);
			lblRateOfInterestP.setImmediate(false);
			lblRateOfInterestP.setWidth("-1px");
			lblRateOfInterestP.setHeight("-1px");
			mainLayout.addComponent(lblRateOfInterestP, "top:70.0px; left:600.0px;");

			// lblInterestAmount
			lblInterestAmount = new Label("Interest Amount :");
			lblInterestAmount.setImmediate(false);
			lblInterestAmount.setWidth("-1px");
			lblInterestAmount.setHeight("-1px");
			mainLayout.addComponent(lblInterestAmount, "top:95.0px; left:400.0px;");

			// txtInterestAmount
			txtInterestAmount = new TextRead(1);
			txtInterestAmount.setImmediate(true);
			txtInterestAmount.setWidth("110px");
			txtInterestAmount.setHeight("24px");
			mainLayout.addComponent(txtInterestAmount, "top:93.0px; left:545.0px;");

			// lblGrossLoanAmount
			lblGrossLoanAmount = new Label("Gross Amount :");
			lblGrossLoanAmount.setImmediate(true);
			lblGrossLoanAmount.setWidth("-1px");
			lblGrossLoanAmount.setHeight("-1px");
			mainLayout.addComponent(lblGrossLoanAmount, "top:120.0px; left:400.0px;");

			// txtPurposeOfLeave
			txtGrossLoanAmount = new TextRead(1);
			txtGrossLoanAmount.setImmediate(true);
			txtGrossLoanAmount.setWidth("110px");
			txtGrossLoanAmount.setHeight("24px");
			mainLayout.addComponent(txtGrossLoanAmount, "top:118.0px; left:545.0px;");

			// lblLoanPurpose
			lblLoanPurpose = new Label("Purpose :");
			lblLoanPurpose.setImmediate(false);
			lblLoanPurpose.setWidth("-1px");
			lblLoanPurpose.setHeight("-1px");
			mainLayout.addComponent(lblLoanPurpose, "top:145.0px; left:400.0px;");

			// txtLoanPurpose
			txtLoanPurpose = new TextField();
			txtLoanPurpose.setImmediate(true);
			txtLoanPurpose.setWidth("200px");
			txtLoanPurpose.setHeight("48px");
			mainLayout.addComponent(txtLoanPurpose, "top:143.0px; left:545.0px;");

			// lblNoOfInstallment
			lblNoOfInstallment = new Label("No of Installment :");
			lblNoOfInstallment.setImmediate(false);
			lblNoOfInstallment.setWidth("-1px");
			lblNoOfInstallment.setHeight("-1px");
			mainLayout.addComponent(lblNoOfInstallment, "top:195.0px; left:400.0px;");

			// txtNoOfInstallment
			txtNoOfInstallment = new AmountField();
			txtNoOfInstallment.setImmediate(true);
			txtNoOfInstallment.setWidth("50px");
			txtNoOfInstallment.setHeight("-1px");
			mainLayout.addComponent(txtNoOfInstallment, "top:193.0px; left:545.0px;");

			// lblAmountOfInstallment
			lblAmountOfInstallment = new Label("Amount per Installment :");
			lblAmountOfInstallment.setImmediate(false);
			lblAmountOfInstallment.setWidth("-1px");
			lblAmountOfInstallment.setHeight("-1px");
			mainLayout.addComponent(lblAmountOfInstallment, "top:220.0px; left:400.0px;");

			// txtAmountOfInstallment
			txtAmountOfInstallment = new TextRead(1);
			txtAmountOfInstallment.setImmediate(true);
			txtAmountOfInstallment.setWidth("110px");
			txtAmountOfInstallment.setHeight("-1px");
			mainLayout.addComponent(txtAmountOfInstallment, "top:218.0px; left:545.0px;");

			// lblPaymentStart
			lblPaymentStart = new Label("Payment Start :");
			lblPaymentStart.setImmediate(false);
			lblPaymentStart.setWidth("-1px");
			lblPaymentStart.setHeight("-1px");
			mainLayout.addComponent(lblPaymentStart, "top:245.0px; left:400.0px;");

			// dPaymentStart
			dPaymentStart = new PopupDateField();
			dPaymentStart.setImmediate(true);
			dPaymentStart.setWidth("110px");
			dPaymentStart.setHeight("-1px");
			dPaymentStart.setValue(new java.util.Date());
			dPaymentStart.setDateFormat("dd-MM-yyyy");
			dPaymentStart.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dPaymentStart, "top:243.0px; left:545.0px;");

			// lblAdjustType
			lblAdjustType = new Label("Adjust Type :");
			lblAdjustType.setImmediate(false);
			lblAdjustType.setWidth("-1px");
			lblAdjustType.setHeight("-1px");
			mainLayout.addComponent(lblAdjustType, "top:270.0px; left:400.0px;");

			// cmbAdjustType
			cmbAdjustType = new ComboBox();
			cmbAdjustType.setImmediate(true);
			cmbAdjustType.setWidth("130px");
			cmbAdjustType.setHeight("-1px");
			cmbAdjustType.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbAdjustType, "top:268.0px; left:545.0px;");
			for(int i=0;i<adjustType.length;i++)
			{cmbAdjustType.addItem(adjustType[i]);}

			mainLayout.addComponent(cButton, "top:320.0px; left:135.0px;");
			return mainLayout;
		}
	}
