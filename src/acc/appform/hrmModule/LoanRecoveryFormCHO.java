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
	import com.vaadin.ui.Window;
	import com.vaadin.ui.Button.ClickEvent;


public class LoanRecoveryFormCHO extends Window {
	
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

		private Label lblRecoveryDate;
		private PopupDateField dRecoveryDate;

		private Label lblEmployeeID;
		private ComboBox cmbEmployeeID;

		private Label lblEmployeeName;
		private TextRead txtEmployeeName;

		private Label lblDepartment;
		private TextRead txtDepartment;

		private Label lblSection;
		private TextRead txtSection;

		private Label lblDesignation;
		private TextRead txtDesignation;

		private Label lblLoanInfo;
		private Label lblLoanNo;
		private ComboBox cmbLoanNo;

		private Label lblLoanBalance;
		private TextRead txtLoanBalance;

		private Label lblRecoveryAmount;
		private AmountCommaSeperator txtRecoveryAmount;

		private Label lblAdjustType;
		private ComboBox cmbAdjustType;

		private TextRead txtFindTranId = new TextRead();
		private TextRead txtFindDate = new TextRead();

		private String updateID = "";

		private ArrayList<Component> allComp = new ArrayList<Component>(); 

		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		private DecimalFormat df = new DecimalFormat("#0.00");

		private boolean isUpdate=false;

		String empId = "";
		String departmentID = "";
		String sectionId = "";
		String designationId = "";
		String loanType = "";
		String findDate = "";

		String findApplicationDate = "";	

		double previousRecovery;

		public LoanRecoveryFormCHO(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("LOAN RECOVERY CHO :: "+sessionBean.getCompany());
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
					txtClear();
					componentIni(true);
					btnIni(false);
					cmbEmployeeID.focus();
				}
			});

			cButton.btnSave.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					formValidation();
				}
			});

			cmbEmployeeID.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbEmployeeID.getValue()!=null)
					{
						employeeSetData(cmbEmployeeID.getValue().toString());
						employeeLoanData(cmbEmployeeID.getValue().toString());
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
					isUpdate=false;
					txtClear();
					componentIni(false);
					btnIni(true);
				}
			});

			cButton.btnEdit.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					if(cmbLoanNo.getValue()!=null && cmbEmployeeID.getValue()!=null)
					{
						isUpdate=true;
						componentIni(true);
						btnIni(false);
					}
					else
					{
						getParent().showNotification("There are nothing to edit", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			});

			cmbLoanNo.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbLoanNo.getValue()!=null)
					{
						setLoanData();
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
		}

		private void cmbAddEmployeeData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = " Select distinct a.vEmployeeId,a.employeeCode FROM tbEmployeeInfo as a inner join " +
						"tbLoanApplication as b on a.vEmployeeId=b.vAutoEmployeeId where b.iLoanStatus=1 and b.vDepartmentId='DEPT10'";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbEmployeeID.addItem(element[0].toString());
					cmbEmployeeID.setItemCaption(element[0].toString(), element[1].toString());	
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
			Window win = new LoanRecoveryFind(sessionBean, txtFindTranId, txtFindDate);
			win.addListener(new Window.CloseListener() 
			{
				public void windowClose(CloseEvent e) 
				{
					if (txtFindTranId.getValue().toString().length() > 0)
					{
						txtClear();
						System.out.println("Find");
						findInitialise(txtFindTranId.getValue().toString(),txtFindDate.getValue());

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
				String query = "Select cast(isnull(max(cast(replace(vTransactionId, '', '')as int))+1, 1)as varchar) as TransactionId from tbLoanRecoveryInfo ";
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

		/*private void findInitialise(String TranId, Object findDate) 
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{

				System.out.println("Hello "+TranId+" Find Date: "+findDate);
					String sql = " SELECT dRecoveryDate,(select vEmployeeId from tbEmployeeInfo ein where ein.vEmployeeId=LRin.vAutoEmployeeId) employeeId,"
							+ " vLoanNo,(select balance from funIndividualLoanStatementNew('"+cmbEmployeeID.getValue()+"','"+dateFormat.format(findDate)+"','"+dateFormat.format(findDate)+"','"+loanType+"')Ln where LRin.EmployeeId = Ln.EmployeeId ) Balance,mRecoveryAmount,vAdjustType from " +
							" tbLoanRecoveryInfo LRin where vTransactionId = '"+TranId+"' and dRecoveryDate = '"+dateFormat.format(findDate)+"' ";

					System.out.println("Find Initialize :" + sql);

					List <?> list = session.createSQLQuery(sql).list();

					if(list.iterator().hasNext())
					{
						Object[] element = (Object[]) list.iterator().next();
						dRecoveryDate.setValue(dateFormat.format(element[0].toString()));
						cmbEmployeeID.setValue(element[1].toString());
						cmbLoanNo.setValue(element[2].toString());

						txtLoanBalance.setValue(new CommaSeparator().setComma(Double.parseDouble(element[3].toString())));
						txtRecoveryAmount.setValue(df.format(Double.parseDouble(element[4].toString())));

						cmbAdjustType.setValue(element[5].toString());

						previousRecovery = Double.parseDouble(element[4].toString());
						updateID = TranId;
						findApplicationDate = findDate.toString();
					}

				String sql=" select dRecoveryDate,vAutoEmployeeId,vLoanNo,mRecoveryAmount,vAdjustType from tbLoanRecoveryInfo where vTransactionId='"+TranId+"'";
				List <?> list = session.createSQLQuery(sql).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					dRecoveryDate.setValue(element[0]);
					cmbEmployeeID.setValue(element[1].toString());
					cmbLoanNo.setValue(element[2]);
					cmbAdjustType.setValue(element[4]);
					txtRecoveryAmount.setValue(df.format(Double.parseDouble(element[3].toString())));
				}
				String sqlLoan="  select (mSanctionAmount-( select isnull(sum(mRecoveryAmount),0) from tbLoanRecoveryInfo where"
						+ " vLoanNo='"+cmbLoanNo.getValue()+"' and vTransactionId!='"+TranId+"' and dRecoveryDate> "
						+ "'"+dateFormat.format(dRecoveryDate.getValue())+"'))balance "
						+ "from tbLoanApplication where vLoanNo=3";
				Iterator<?> iter=session.createSQLQuery(sqlLoan).list().iterator();
				if(iter.hasNext()){
					txtLoanBalance.setValue(df.format(Double.parseDouble(iter.next().toString())));
				}

			}
			catch (Exception exp)
			{
				showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}
		 */	
		private void findInitialise(String TranId, Object findDate) 
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			double recovery=0.00;

			try
			{
				String sql = " SELECT dRecoveryDate,(select vEmployeeId from tbEmployeeInfo ein where ein.vEmployeeId=LRin.vAutoEmployeeId) " +
						"employeeId,vLoanNo,(select mLoanBalance from tbLoanApplication where vLoanNo=LRin.vLoanNo and " +
						"vAutoEmployeeId=LRin.vAutoEmployeeId)+mRecoveryAmount,mRecoveryAmount,vAdjustType from " +
						"tbLoanRecoveryInfo LRin where vTransactionId = '"+TranId+"'";

				List <?> list = session.createSQLQuery(sql).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					dRecoveryDate.setValue(element[0]);
					cmbEmployeeID.setValue(element[1].toString());
					cmbLoanNo.setValue(element[2].toString());		
					//txtLoanBalance.setValue(new CommaSeparator().setComma(Double.parseDouble(element[3].toString())));
					txtRecoveryAmount.setValue(df.format(Double.parseDouble(element[4].toString())));
					recovery=Double.parseDouble(element[4].toString());

					cmbAdjustType.setValue(element[5].toString());

					previousRecovery = Double.parseDouble(element[4].toString());
					updateID = TranId;
					findApplicationDate = findDate.toString();
				}	
				/*String sqlbalance = "select balance from  funIndividualLoanStatementNew('"+cmbEmployeeID.getValue()+"','"+findDate+"','"+findDate+"','"+loanType+"') ";
				Iterator<?> iter=session.createSQLQuery(sqlbalance).list().iterator();
				if(iter.hasNext()){
					txtLoanBalance.setValue(df.format(Double.parseDouble(iter.next().toString())));
				}*/
				
				txtLoanBalance.setValue(Double.parseDouble(txtLoanBalance.getValue().toString().replaceAll(",", ""))+recovery);

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
				String query = " SELECT E.vEmployeeID,E.employeeCode,E.vEmployeeName,E.vGender,E.vDepartmentID,dept.vDepartmentName," +
						"S.vSectionId,S.SectionName,D.designationId,D.designationName,E.dJoiningDate " +
						"FROM tbEmployeeInfo AS E INNER JOIN tbSectionInfo AS S ON E.vSectionId = S.vSectionId INNER JOIN " +
						"tbDesignationInfo AS D ON E.vDesignationId = D.designationId inner join tbDepartmentInfo dept on " +
						"dept.vDepartmentID=E.vDepartmentID WHERE E.vEmployeeId = '"+employeeId+"' ORDER BY E.vEmployeeType ";

				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();

					empId = (element[0].toString());
					txtEmployeeName.setValue(element[2].toString());
					txtDepartment.setValue(element[5].toString());
					txtSection.setValue(element[7].toString());
					txtDesignation.setValue(element[9].toString());

					departmentID = element[4].toString();
					sectionId = element[6].toString();
					designationId = element[8].toString();
				}
			}
			catch(Exception ex)
			{
				showNotification("employeeSetData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void employeeLoanData(String empId)
		{
			cmbLoanNo.removeAllItems();
			txtLoanBalance.setValue("");
			txtRecoveryAmount.setValue("");
			cmbAdjustType.setValue(null);

			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = " select dApplicationDate,vLoanNo from tbLoanApplication where vAutoEmployeeId='"+empId+"' and iLoanStatus='1' ";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbLoanNo.addItem(element[1]);
					cmbLoanNo.setItemCaption(element[1], element[1].toString());	
				}
			}
			catch(Exception ex)
			{
				showNotification("employeeLoanData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void setLoanData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				double recoveryamount=0.00f;
				String query = " SELECT mLoanBalance,mAmountPerInstall,iSanctionStatus,vLoanType,mLoanAmount from tbLoanApplication where vLoanNo = '"+cmbLoanNo.getValue().toString()+"' ";
				List <?> list = session.createSQLQuery(query).list();
				
				String sql=	"select 0, isnull(sum(mRecoveryAmount),0) taka  from tbLoanRecoveryInfo where vLoanNo='"+cmbLoanNo.getValue().toString()+"' and dRecoveryDate<='"+dateFormat.format(dRecoveryDate.getValue())+"' ";
				List <?> list1 = session.createSQLQuery(sql).list();
				Iterator<?>iter=list1.iterator();
				if(iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();
					recoveryamount=  Double.parseDouble(element[1].toString()) ;
				}
				
				

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					if(element[2].toString().equals("1"))
					{
						txtLoanBalance.setValue(new CommaSeparator().setComma(Double.parseDouble(element[4].toString())-recoveryamount));
						txtRecoveryAmount.setValue(df.format(Double.parseDouble(element[1].toString())));
						loanType = element[3].toString();
					}
					else
					{
						cmbLoanNo.setValue(null);
						txtLoanBalance.setValue("");
						txtRecoveryAmount.setValue("");
						showNotification("Warning!", "Loan is not sanctioned yet", Notification.TYPE_WARNING_MESSAGE);
					}
				}		
			}
			catch(Exception ex)
			{
				showNotification("setLoanData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void formValidation()
		{
			if(cmbEmployeeID.getValue()!=null)
			{
				if(cmbLoanNo.getValue()!=null)
				{
					if(!txtRecoveryAmount.getValue().toString().isEmpty())
					{
						if(cmbAdjustType.getValue()!=null)
						{
							saveBtnAction();
						}
						else
						{
							showNotification("Warning!","Select Adjust Type", Notification.TYPE_WARNING_MESSAGE);
							cmbAdjustType.focus();
						}
					}
					else
					{
						showNotification("Warning!","Provide Recovery Amount", Notification.TYPE_WARNING_MESSAGE);
						txtRecoveryAmount.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Loan No", Notification.TYPE_WARNING_MESSAGE);
					cmbLoanNo.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Employee Name", Notification.TYPE_WARNING_MESSAGE);
				cmbEmployeeID.focus();
			}
		}

		private void saveBtnAction()
		{
			double balance = Double.parseDouble(txtLoanBalance.getValue().toString().replace(",", ""))-Double.parseDouble(txtRecoveryAmount.getValue().toString().replace(",", ""));

			if(isUpdate)
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
				if(balance>=0)
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
					showNotification("Warning!","Recovery amount is greater than balance amount",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}

		private void insertData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();

			String insertQuery = ""; 
			String updateQuery = "";
			String loanStatus = "1";

			double balance = Double.parseDouble(txtLoanBalance.getValue().toString().replaceAll(",", ""))-Double.parseDouble(txtRecoveryAmount.getValue().toString().replaceAll(",", ""));
			System.out.println("Balance: "+balance);

			if(balance==0)
			{loanStatus= "0";}

			try
			{
				insertQuery = " Insert into tbLoanRecoveryInfo (vTransactionId,dRecoveryDate,vAutoEmployeeId,vEmployeeId,vProximityId," +
						"vSectionId,vDesignationId,vLoanNo,mLoanBalance,mRecoveryAmount,vLoanType,vAdjustType,vRecoverFrom,vUserId," +
						"vUserIp,dEntryTime,vDepartmentID) values (" +
						" '"+autoId()+"', '"+dateFormat.format(dRecoveryDate.getValue())+"',"+
						" '"+cmbEmployeeID.getValue()+"','"+cmbEmployeeID.getItemCaption(cmbEmployeeID.getValue())+"',(select vProximityID from tbEmployeeInfo where vEmployeeId='"+cmbEmployeeID.getValue().toString().trim()+"')," +
						" '"+sectionId+"', '"+designationId+"'," +
						" '"+cmbLoanNo.getValue().toString()+"','0.0', " +
						" '"+txtRecoveryAmount.getValue().toString().replace(",", "")+"', " +
						" '"+loanType+"', '"+cmbAdjustType.getValue().toString()+"', 'From Loan Recovery', " +
						" '"+sessionBean.getUserName()+"', '"+sessionBean.getUserIp()+"', CURRENT_TIMESTAMP,'"+departmentID+"') ";

				session.createSQLQuery(insertQuery).executeUpdate();

				updateQuery = "update tbLoanApplication set mLoanBalance='"+balance+"',iLoanStatus='"+loanStatus+"' where" +
						" vLoanNo='"+cmbLoanNo.getValue().toString()+"' and" +
						" vAutoEmployeeId='"+cmbEmployeeID.getValue()+"' ";

				session.createSQLQuery(updateQuery).executeUpdate();

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
			String updateQueryBalance = "";

			double updateBalance = previousRecovery-Double.parseDouble(txtRecoveryAmount.getValue().toString().replace(",", ""));

			try
			{
				updateQuery = " UPDATE tbLoanRecoveryInfo set " +
						" mRecoveryAmount = '"+txtRecoveryAmount.getValue().toString()+"' ," +
						" vAdjustType = '"+cmbAdjustType.getValue().toString()+"' ," +
						" dEntryTime=CURRENT_TIMESTAMP, vUserIp='"+sessionBean.getUserIp()+"'," +
						" vUserId='"+sessionBean.getUserName()+"'" +
						" where vTransactionId='"+updateID+"' and dRecoveryDate = '"+findApplicationDate+"' ";

				session.createSQLQuery(updateQuery).executeUpdate();

				updateQueryBalance = " UPDATE tbLoanApplication set mLoanBalance=mLoanBalance+'"+updateBalance+"' where" +
						" vLoanNo='"+cmbLoanNo.getValue().toString()+"' and vAutoEmployeeId='"+empId+"' ";

				session.createSQLQuery(updateQueryBalance).executeUpdate();

				tx.commit();
				showNotification("All Information Update Successfully");
			}
			catch(Exception ex)
			{
				tx.rollback();
				showNotification("updateData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			showNotification("All Information Update Successfully");
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
			dRecoveryDate.setEnabled(t);
			if(isUpdate==true)
			{
				cmbEmployeeID.setEnabled(!t);
				cmbLoanNo.setEnabled(!t);
			}
			else
			{
				cmbEmployeeID.setEnabled(t);
				cmbLoanNo.setEnabled(t);
			}

			txtEmployeeName.setEnabled(t);
			txtDepartment.setEnabled(t);
			txtSection.setEnabled(t);
			txtDesignation.setEnabled(t);

			txtLoanBalance.setEnabled(t);
			txtRecoveryAmount.setEnabled(t);
			cmbAdjustType.setEnabled(t);
		}

		private void txtClear()
		{
			dRecoveryDate.setValue(new java.util.Date());
			cmbEmployeeID.setValue(null);
			txtEmployeeName.setValue("");
			txtDepartment.setValue("");
			txtSection.setValue("");
			txtDesignation.setValue("");

			cmbLoanNo.setValue(null);
			txtLoanBalance.setValue("");
			txtRecoveryAmount.setValue("");
			cmbAdjustType.setValue(null);
		}

		private void focusEnter()
		{
			allComp.add(dRecoveryDate);
			allComp.add(cmbEmployeeID);
			allComp.add(cmbLoanNo);
			allComp.add(txtRecoveryAmount);
			allComp.add(cmbAdjustType);
			allComp.add(cButton.btnSave);
			new FocusMoveByEnter(this,allComp);
		}

		public AbsoluteLayout buildMainLayout()
		{
			// common part: create layout
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setWidth("530px");
			mainLayout.setHeight("340px");

			// lblRecoveryDate
			lblRecoveryDate = new Label("Recovery Date : ");
			lblRecoveryDate.setImmediate(false);
			lblRecoveryDate.setWidth("-1px");
			lblRecoveryDate.setHeight("-1px");
			mainLayout.addComponent(lblRecoveryDate, "top:20.0px; left:70.0px;");

			// dRecoveryDate
			dRecoveryDate = new PopupDateField();
			dRecoveryDate.setImmediate(false);
			dRecoveryDate.setWidth("110px");
			dRecoveryDate.setHeight("-1px");
			dRecoveryDate.setValue(new java.util.Date());
			dRecoveryDate.setDateFormat("dd-MM-yyyy");
			dRecoveryDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dRecoveryDate, "top:19.0px; left:200.0px;");

			// lblEmployeeID
			lblEmployeeID = new Label("Employee ID :");
			lblEmployeeID.setImmediate(false);
			lblEmployeeID.setWidth("-1px");
			lblEmployeeID.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeID, "top:45.0px; left:70.0px;");

			// cmbEmployeeID
			cmbEmployeeID = new ComboBox();
			cmbEmployeeID.setImmediate(true);
			cmbEmployeeID.setWidth("230px");
			cmbEmployeeID.setHeight("-1px");
			cmbEmployeeID.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbEmployeeID, "top:43.0px; left:200.0px;");

			// lblEmployeeName
			lblEmployeeName = new Label("Employee Name :");
			lblEmployeeName.setImmediate(false);
			lblEmployeeName.setWidth("-1px");
			lblEmployeeName.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeName, "top:70.0px; left:70.0px;");

			// txtEmployeeName
			txtEmployeeName = new TextRead();
			txtEmployeeName.setImmediate(true);
			txtEmployeeName.setWidth("220px");
			txtEmployeeName.setHeight("22px");
			mainLayout.addComponent(txtEmployeeName, "top:68.0px; left:201.0px;");

			// lblDesignation
			lblDesignation = new Label("Designation :");
			lblDesignation.setImmediate(false);
			lblDesignation.setWidth("-1px");
			lblDesignation.setHeight("-1px");
			mainLayout.addComponent(lblDesignation, "top:95.0px; left:70.0px;");

			// txtDesignation
			txtDesignation = new TextRead();
			txtDesignation.setImmediate(true);
			txtDesignation.setWidth("220px");
			txtDesignation.setHeight("22px");
			mainLayout.addComponent(txtDesignation, "top:93.0px; left:201.0px;");

			// lblSection
			lblDepartment = new Label("Department Name :");
			lblDepartment.setImmediate(false);
			lblDepartment.setWidth("-1px");
			lblDepartment.setHeight("-1px");
			mainLayout.addComponent(lblDepartment, "top:120.0px; left:70.0px;");

			// txtSection
			txtDepartment = new TextRead();
			txtDepartment.setImmediate(true);
			txtDepartment.setWidth("220px");
			txtDepartment.setHeight("22px");
			mainLayout.addComponent(txtDepartment, "top:118.0px; left:201.0px;");

			// lblSection
			lblSection = new Label("Section Name :");
			lblSection.setImmediate(false);
			lblSection.setWidth("-1px");
			lblSection.setHeight("-1px");
			mainLayout.addComponent(lblSection, "top:145.0px; left:70.0px;");

			// txtSection
			txtSection = new TextRead();
			txtSection.setImmediate(true);
			txtSection.setWidth("220px");
			txtSection.setHeight("22px");
			mainLayout.addComponent(txtSection, "top:143.0px; left:201.0px;");

			// lblLoanInfo
			lblLoanInfo = new Label("<html><font color='#74078B'><b><u>Loan Details</u></b></font></html>",Label.CONTENT_XHTML);
			lblLoanInfo.setImmediate(false);
			lblLoanInfo.setWidth("-1px");
			lblLoanInfo.setHeight("-1px");
			mainLayout.addComponent(lblLoanInfo, "top:170.0px; left:150.0px;");

			// lblLoanNo
			lblLoanNo = new Label("Loan No :");
			lblLoanNo.setImmediate(false);
			lblLoanNo.setWidth("-1px");
			lblLoanNo.setHeight("-1px");
			mainLayout.addComponent(lblLoanNo, "top:195.0px; left:70.0px;");

			// cmbLoanNo
			cmbLoanNo = new ComboBox();
			cmbLoanNo.setImmediate(true);
			cmbLoanNo.setWidth("80px");
			cmbLoanNo.setHeight("-1px");
			mainLayout.addComponent(cmbLoanNo, "top:193.0px; left:200.0px;");

			// lblLoanBalance
			lblLoanBalance = new Label("Loan Balance :");
			lblLoanBalance.setImmediate(false);
			lblLoanBalance.setWidth("-1px");
			lblLoanBalance.setHeight("-1px");
			mainLayout.addComponent(lblLoanBalance, "top:220.0px; left:70.0px;");

			// txtLoanBalance
			txtLoanBalance = new TextRead(1);
			txtLoanBalance.setImmediate(true);
			txtLoanBalance.setWidth("100px");
			txtLoanBalance.setHeight("-1px");
			mainLayout.addComponent(txtLoanBalance, "top:218.0px; left:200.0px;");

			// lblRecoveryAmount
			lblRecoveryAmount = new Label("Recovery Amount :");
			lblRecoveryAmount.setImmediate(false);
			lblRecoveryAmount.setWidth("-1px");
			lblRecoveryAmount.setHeight("-1px");
			mainLayout.addComponent(lblRecoveryAmount, "top:245.0px; left:70.0px;");

			// txtRecoveryAmount
			txtRecoveryAmount = new AmountCommaSeperator();
			txtRecoveryAmount.setImmediate(true);
			txtRecoveryAmount.setWidth("100px");
			txtRecoveryAmount.setHeight("-1px");
			mainLayout.addComponent(txtRecoveryAmount, "top:243.0px; left:200.0px;");

			// lblAdjustType
			lblAdjustType = new Label("Adjust Type :");
			lblAdjustType.setImmediate(false);
			lblAdjustType.setWidth("-1px");
			lblAdjustType.setHeight("-1px");
			mainLayout.addComponent(lblAdjustType, "top:270.0px; left:70.0px;");

			// cmbAdjustType
			cmbAdjustType = new ComboBox();
			cmbAdjustType.setImmediate(true);
			cmbAdjustType.setWidth("130px");
			cmbAdjustType.setHeight("-1px");
			cmbAdjustType.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbAdjustType, "top:268.0px; left:200.0px;");
			for(int i=0;i<adjustType.length;i++)
			{cmbAdjustType.addItem(adjustType[i]);}

			mainLayout.addComponent(cButton, "top:298.0px; left:10.0px;");
			return mainLayout;
		}


}
