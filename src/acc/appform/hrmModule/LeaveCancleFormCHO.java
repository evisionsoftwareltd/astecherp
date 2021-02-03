	package acc.appform.hrmModule;

	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Iterator;
	import java.util.List;

	import org.hibernate.Session;
	import org.hibernate.Transaction;

	import com.common.share.CommonButton;
	import com.common.share.FocusMoveByEnter;
	import com.common.share.MessageBox;
	import com.common.share.MessageBox.ButtonType;
	import com.common.share.MessageBox.EventListener;
	import com.common.share.SessionBean;
	import com.common.share.SessionFactoryUtil;
	import com.common.share.TextRead;
	import com.vaadin.data.Property.ValueChangeEvent;
	import com.vaadin.data.Property.ValueChangeListener;
	import com.vaadin.ui.AbsoluteLayout;
	import com.vaadin.ui.Button.ClickEvent;
	import com.vaadin.ui.Button.ClickListener;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.Component;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.PopupDateField;
	import com.vaadin.ui.TextField;
	import com.vaadin.ui.Window;

public class LeaveCancleFormCHO extends Window {
	
		private CommonButton cButton = new CommonButton( "New",  "Save",  "",  "",  "Refresh",  "", "", "","","Exit");
		private SessionBean sessionBean;

		private AbsoluteLayout mainLayout;

		HashMap <String,Object> hDateDayList = new HashMap <String,Object> ();

		private Label lblLeaveCancelDate;
		private PopupDateField dLeaveCancelDate;

		private Label lblRefNo;
		private TextField txtRefNo;

		private Label lblEmployeeId;
		private ComboBox cmbEmployeeId;

		private Label lblEmployeeName;
		private TextRead txtEmployeeName;

		private Label lblId;
		private TextRead txtId;

		private Label lblApplicationDate;
		private ComboBox cmbApplicationDate;

		private Label lblLeaveType;
		private TextRead txtLeaveType;

		private Label lblLeaveFrom;
		private PopupDateField dLeaveFrom;

		private Label lblLeaveTo;
		private PopupDateField dLeaveTo;

		private Label lblSenctionFrom;
		private PopupDateField dSenctionFrom;

		private Label lblSenctionTo;
		private PopupDateField dSenctionTo;

		private Label lblDuration;
		private TextRead txtDuration;
		private Label lblDays;

		private Label lblPurposeOfLeave;
		private TextRead txtPurposeOfLeave;

		private Label lblReasonOfCancellation;
		private TextField txtReasonOfCancellation;

		private Label lblCancelledBy;
		private TextRead txtCancelledBy;

		private Label lblPermittedBy;
		private TextField txtPermittedBy;

		private ArrayList<Component> allComp = new ArrayList<Component>(); 
		
		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		private SimpleDateFormat dateFormatForm = new SimpleDateFormat("dd-MM-yyyy");
		private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

		private boolean isUpdate=false;

		String sectionId = "";
		String designationId = "";
		String leaveType = "0";
		String totalDays = "";
		String CBalance = "";
		String SBalance = "";
		String ABalance = "";
		String MBalance = "";
		String CEnjoy = "";
		String SEnjoy = "";
		String AEnjoy = "";
		String MEnjoy = "";
		String findEmployeeId = "";	

		public LeaveCancleFormCHO(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("LEAVE CANCEL FORM CHO ::"+sessionBean.getCompany());
			this.setResizable(false);
			buildMainLayout();
			setContent(mainLayout);
			btnIni(true);
			componentIni(false);
			setBtnAction();
			cButton.btnNew.focus();
			focusEnter();
			authenticationCheck();
		}

		private void authenticationCheck()
		{
			if(!sessionBean.isSubmitable()){
				cButton.btnSave.setVisible(false);
			}

			if(!sessionBean.isUpdateable()){
				cButton.btnEdit.setVisible(false);
			}

			if(!sessionBean.isDeleteable()){
				cButton.btnDelete.setVisible(false);
			}
		}

		private void setBtnAction()
		{
			dLeaveCancelDate.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbEmployeeId.removeAllItems();
					if(dLeaveCancelDate.getValue()!=null)
					{
						cmbAddEmployeeData();
					}
				}
			});

			cmbEmployeeId.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbEmployeeId.getValue()!=null)
					{
						employeeSetData(cmbEmployeeId.getValue().toString());
						dApplicationDateDataAdd(cmbEmployeeId.getValue().toString());
					}
				}
			});

			cmbApplicationDate.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					LeaveAllDataRemove();
					if(cmbApplicationDate.getValue()!=null)
					{
						LeaveAllDataAddd(dateFormat.format(cmbApplicationDate.getValue()));
					}
				}
			});
			
			txtReasonOfCancellation.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					
					txtCancelledBy.setValue("");
					
					if(!txtReasonOfCancellation.getValue().toString().trim().isEmpty())
					{
						txtCancelledBy.setValue(sessionBean.getUserName());
					}
					
				}
			});

			cButton.btnNew.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					txtClear();
					componentIni(true);
					btnIni(false);
					cmbEmployeeId.focus();
				}
			});

			cButton.btnSave.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					formValidation();
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

			cButton.btnExit.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					close();
				}
			});
		}

		private void LeaveAllDataRemove()
		{
			txtLeaveType.setValue("");
			dLeaveFrom.setReadOnly(false);
			dLeaveFrom.setValue(null);
			dLeaveFrom.setReadOnly(true);
			dLeaveTo.setReadOnly(false);
			dLeaveTo.setValue(null);
			dLeaveTo.setReadOnly(true);
			dSenctionFrom.setReadOnly(false);
			dSenctionFrom.setValue(null);
			dSenctionFrom.setReadOnly(true);
			dSenctionTo.setReadOnly(false);
			dSenctionTo.setValue(null);
			dSenctionTo.setReadOnly(true);
			txtDuration.setValue("");
			lblDays.setValue("");		
			txtPurposeOfLeave.setValue("");
		}

		private void LeaveAllDataAddd(String applyDate)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select (select vLeaveTypeName from tbLeaveType lt where lt.iLeaveTypeID=vLeaveType) LeaveName," +
						"dApplyFrom,dApplyTo,dSenctionFrom,dSenctionTo,DATEDIFF(DD,dSenctionFrom,dSenctionTo)+1 as Duration," +
						"vPurposeOfLeave from tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeId.getValue().toString()+"' " +
						"and dApplicationDate='"+cmbApplicationDate.getValue().toString()+"'";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					txtLeaveType.setValue(element[0]);
					dLeaveFrom.setReadOnly(false);
					dLeaveFrom.setValue(element[1]);
					dLeaveFrom.setReadOnly(true);

					dLeaveTo.setReadOnly(false);
					dLeaveTo.setValue(element[2]);
					dLeaveTo.setReadOnly(true);

					dSenctionFrom.setReadOnly(false);
					dSenctionFrom.setValue(element[3]);
					dSenctionFrom.setReadOnly(true);

					dSenctionTo.setReadOnly(false);
					dSenctionTo.setValue(element[4]);
					dSenctionTo.setReadOnly(true);

					txtDuration.setValue(element[5]);
					if(Integer.parseInt(element[5].toString())>1)
					{
						lblDays.setValue("Days");
					}
					else
					{
						lblDays.setValue("Day");
					}
					txtPurposeOfLeave.setValue(element[6]);
				}
			}
			catch(Exception ex)
			{
				showNotification("LeaveAllDataAddd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void dApplicationDateDataAdd(String autoEmpID)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select dApplicationDate,dApplicationDate from tbEmployeeLeave where " +
						"vAutoEmployeeId='"+cmbEmployeeId.getValue().toString()+"'";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbApplicationDate.addItem(element[0]);
					cmbApplicationDate.setItemCaption(element[0], dateFormatForm.format(element[1]));	
				}
			}
			catch(Exception ex)
			{
				showNotification("dApplicationDateDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void cmbAddEmployeeData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select distinct vAutoEmployeeId,vEmployeeID from tbemployeeleave " +
						"where dSenctionFrom>'"+dateFormat.format(dLeaveCancelDate.getValue())+"' ";
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

		private void employeeSetData(String employeeId)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select ein.vEmployeeName,el.vProximityID from tbEmployeeLeave el inner join tbEmployeeInfo ein " +
						"on el.vAutoEmployeeID=ein.vEmployeeID where el.vAutoEmployeeId='"+cmbEmployeeId.getValue().toString()+"'";
				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();

					txtEmployeeName.setValue(element[0]);
					txtId.setValue(element[1].toString());
				}

			}
			catch(Exception ex)
			{
				showNotification("employeeSetData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void formValidation()
		{
			if(dLeaveCancelDate.getValue()!=null)
			{
				if(cmbEmployeeId.getValue()!=null)
				{
					if(cmbApplicationDate.getValue()!=null)
					{
						if(!txtReasonOfCancellation.getValue().toString().equals(""))
						{
							if(!txtPermittedBy.getValue().toString().equals(""))
							{
								saveBtnAction();
							}
							else
							{
								showNotification("Warning!","Provide Permitted By", Notification.TYPE_WARNING_MESSAGE);
								txtPermittedBy.focus();
							}
						}
						else
						{
							showNotification("Warning!","Provide Reason of Cancellation", Notification.TYPE_WARNING_MESSAGE);
							txtReasonOfCancellation.focus();
						}
					}
					else
					{
						showNotification("Warning!","Select Application Date", Notification.TYPE_WARNING_MESSAGE);
						cmbApplicationDate.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Employee Name", Notification.TYPE_WARNING_MESSAGE);
					cmbEmployeeId.focus();
				}
			}
			else
			{
				showNotification("Warning!","Provide Cancel Date", Notification.TYPE_WARNING_MESSAGE);
				dLeaveCancelDate.focus();
			}
		}

		private void saveBtnAction()
		{
			if(isUpdate)
			{
				
			}
			else
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
							System.out.println(dLeaveFrom.getValue()+" "+dLeaveTo.getValue()+" "+dSenctionFrom.getValue()+" "+dSenctionTo.getValue());
							cButton.btnNew.focus();
						}
					}
				});
			}
		}

		private void deleteFromEmployeeLeave(Session session)
		{
			String query="delete from tbEmployeeLeave where dApplicationDate='"+cmbApplicationDate.getValue()+"' and vAutoEmployeeId='"+cmbEmployeeId.getValue().toString()+"'";
			session.createSQLQuery(query).executeUpdate();
		}
		
		private void UpdateLeaveBalanceNew(Session session)
		{
			int leaveType=0;
			if(txtLeaveType.getValue().toString().equals("CL"))
				leaveType=1;
			else if(txtLeaveType.getValue().toString().equals("SL"))
				leaveType=2;
			else if(txtLeaveType.getValue().toString().equals("EL"))
				leaveType=3;
			else if(txtLeaveType.getValue().toString().equals("ML"))
				leaveType=4;
			
			String query="update tbLeaveBalanceNew set i"+txtLeaveType.getValue().toString()+"" +
					"Balance+=(select iNoOfDays from tbEmployeeLeave  where vLeaveType='"+leaveType+"' and " +
					"dApplicationDate='"+cmbApplicationDate.getValue()+"' and " +
					"vAutoEmployeeId='"+cmbEmployeeId.getValue().toString()+"'),  i"+txtLeaveType.getValue().toString()+"" +
					"Enjoyed=i"+txtLeaveType.getValue().toString()+"Enjoyed-'"+txtDuration.getValue().toString().trim()+"' " +
					"where vAutoEmployeeId='"+cmbEmployeeId.getValue().toString()+"' and " +
					"Year(currentYear)='"+yearFormat.format(dLeaveCancelDate.getValue())+"'";
			session.createSQLQuery(query).executeUpdate();
		}
		
		private void insertData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{

				System.out.println("Deg :"+designationId);
				System.out.println("Sec :"+sectionId);
				String insertquery = "";

				insertquery = " insert into tbLeaveCancel(dCancelDate,vRefNo,vAutoEmployeeId,vEmployeeID,vProximityID,dApplicationDate,vLeaveTypeID," +
							  " vLeaveTypeName,dLeaveFrom,dLeaveTo,dSenctionFrom,dSenctionTo,Duration_NoOfDays,vPurposeOFLeave,vReasonOfCancellation," +
							  " vCancelledBy,vPermittedBy,vUserName,vUserIP,dEntryTime) values('"+dateFormat.format(dLeaveCancelDate.getValue())+"'," +
							  " '"+(txtRefNo.getValue().toString().trim().length()==0?"":txtRefNo.getValue().toString().trim())+"'," +
							  " '"+cmbEmployeeId.getValue().toString()+"'," +
							  " '"+cmbEmployeeId.getItemCaption(cmbEmployeeId.getValue().toString())+"'," +
							  " '"+txtId.getValue().toString()+"'," +
							  " '"+cmbApplicationDate.getValue()+"'," +
							  " (select iLeaveTypeID from tbLeaveType lt where lt.vLeaveTypeName='"+txtLeaveType.getValue().toString()+"')," +
							  " '"+txtLeaveType.getValue().toString()+"'," +
							  " '"+dateFormat.format(dLeaveFrom.getValue())+"'," +
							  " '"+dateFormat.format(dLeaveTo.getValue())+"'," +
							  " '"+dateFormat.format(dSenctionFrom.getValue())+"'," +
							  " '"+dateFormat.format(dSenctionTo.getValue())+"'," +
							  " '"+txtDuration.getValue().toString()+"'," +
							  " '"+txtPurposeOfLeave.getValue().toString().trim()+"'," +
							  " '"+txtReasonOfCancellation.getValue().toString().trim()+"'," +
							  " '"+txtCancelledBy.getValue().toString().trim()+"'," +
							  " '"+txtPermittedBy.getValue().toString().trim()+"'," +
							  " '"+sessionBean.getUserName()+"'," +
							  " '"+sessionBean.getUserIp()+"',getdate())";

				session.createSQLQuery(insertquery).executeUpdate();
				session.clear();
				
				UpdateLeaveBalanceNew(session);
				deleteFromEmployeeLeave(session);
				
				txtClear();
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
			{dLeaveCancelDate.setEnabled(!t);}
			else
			{dLeaveCancelDate.setEnabled(t);}
			txtRefNo.setEnabled(t);
			cmbEmployeeId.setEnabled(t);
			txtEmployeeName.setEnabled(t);
			txtId.setEnabled(t);
			cmbApplicationDate.setEnabled(t);
			txtLeaveType.setEnabled(t);
			dLeaveFrom.setEnabled(t);
			dLeaveTo.setEnabled(t);
			dSenctionFrom.setEnabled(t);
			dSenctionTo.setEnabled(t);
			txtDuration.setEnabled(t);
			txtPurposeOfLeave.setEnabled(t);
			txtReasonOfCancellation.setEnabled(t);
			txtCancelledBy.setEnabled(t);
			txtPermittedBy.setEnabled(t);
		}

		private void txtClear()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				dLeaveCancelDate.setValue(session.createSQLQuery("select convert(date,GETDATE())").list().iterator().next());
			}
			catch(Exception exp)
			{
				showNotification("txtClear", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			
			txtRefNo.setValue("");
			cmbEmployeeId.setValue(null);
			txtEmployeeName.setValue("");
			txtId.setValue("");
			txtLeaveType.setValue("");
			dLeaveFrom.setReadOnly(false);
			dLeaveFrom.setValue(null);
			dLeaveFrom.setReadOnly(true);
			dLeaveTo.setReadOnly(false);
			dLeaveTo.setValue(null);
			dLeaveTo.setReadOnly(true);
			dSenctionFrom.setReadOnly(false);
			dSenctionFrom.setValue(null);
			dSenctionFrom.setReadOnly(true);
			dSenctionTo.setReadOnly(false);
			dSenctionTo.setValue(null);
			dSenctionTo.setReadOnly(true);
			txtDuration.setValue("");
			txtPurposeOfLeave.setValue("");
			txtReasonOfCancellation.setValue("");
			txtCancelledBy.setValue("");
			txtPermittedBy.setValue("");
		}

		private void focusEnter()
		{
			allComp.add(dLeaveCancelDate);
			allComp.add(txtRefNo);
			allComp.add(cmbEmployeeId);
			allComp.add(cmbApplicationDate);
			allComp.add(txtReasonOfCancellation);
			allComp.add(txtCancelledBy);
			allComp.add(txtPermittedBy);
			allComp.add(cButton.btnSave);
			new FocusMoveByEnter(this,allComp);
		}

		public AbsoluteLayout buildMainLayout()
		{
			// common part: create layout
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setWidth("800px");
			mainLayout.setHeight("330px");
			mainLayout.setMargin(false);

			// lblApplicationDate
			lblLeaveCancelDate = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Cancel Date : ",Label.CONTENT_XHTML);
			lblLeaveCancelDate.setImmediate(false);
			lblLeaveCancelDate.setWidth("-1px");
			lblLeaveCancelDate.setHeight("-1px");
			mainLayout.addComponent(lblLeaveCancelDate, "top:20.0px; left:25.0px;");

			// dApplicationDate
			dLeaveCancelDate = new PopupDateField();
			dLeaveCancelDate.setImmediate(true);
			dLeaveCancelDate.setEnabled(false);
			dLeaveCancelDate.setWidth("110px");
			dLeaveCancelDate.setHeight("-1px");
			dLeaveCancelDate.setDateFormat("dd-MM-yyyy");
			dLeaveCancelDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dLeaveCancelDate, "top:18.0px; left:140.0px;");

			lblRefNo=new Label("Ref. NO.: ");
			lblRefNo.setImmediate(false);
			lblRefNo.setWidth("-1px");
			lblRefNo.setHeight("-1px");
			mainLayout.addComponent(lblRefNo, "top:50.0px;left:30.0px;");

			txtRefNo=new TextField();
			txtRefNo.setImmediate(true);
			txtRefNo.setWidth("110px");
			txtRefNo.setHeight("-1px");
			mainLayout.addComponent(txtRefNo, "top:48.0px;left:140.0px;");

			// lblEmployeeId
			lblEmployeeId = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Employee ID :",Label.CONTENT_XHTML);
			lblEmployeeId.setImmediate(false);
			lblEmployeeId.setWidth("-1px");
			lblEmployeeId.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeId, "top:80.0px; left:25.0px;");

			// cmbEmployeeId
			cmbEmployeeId = new ComboBox();
			cmbEmployeeId.setImmediate(true);
			cmbEmployeeId.setWidth("250px");
			cmbEmployeeId.setHeight("-1px");
			cmbEmployeeId.setNullSelectionAllowed(true);
			mainLayout.addComponent(cmbEmployeeId, "top:78.0px; left:140.0px;");

			lblEmployeeName=new Label("Employee Name :");
			lblEmployeeName.setImmediate(false);
			lblEmployeeName.setWidth("-1px");
			lblEmployeeName.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeName, "top:110.0px;left:30.0px;");

			txtEmployeeName=new TextRead();
			txtEmployeeName.setImmediate(false);
			txtEmployeeName.setWidth("140.0px");
			txtEmployeeName.setHeight("-1px");
			mainLayout.addComponent(txtEmployeeName, "top:108.0px;left:140.0px;");

			// lblId
			lblId = new Label("Proximity ID :");
			lblId.setImmediate(false);
			lblId.setWidth("-1px");
			lblId.setHeight("-1px");
			mainLayout.addComponent(lblId, "top:140.0px; left:30.0px;");

			// txtId
			txtId = new TextRead();
			txtId.setImmediate(true);
			txtId.setWidth("70px");
			txtId.setHeight("22px");
			mainLayout.addComponent(txtId, "top:138.0px; left:140.0px;");

			// lblApplicationDate
			lblApplicationDate = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Application Date : ",Label.CONTENT_XHTML);
			lblApplicationDate.setImmediate(false);
			lblApplicationDate.setWidth("-1px");
			lblApplicationDate.setHeight("-1px");
			mainLayout.addComponent(lblApplicationDate, "top:170.0px; left:25.0px;");

			// cmbApplicationDate
			cmbApplicationDate = new ComboBox();
			cmbApplicationDate.setImmediate(true);
			cmbApplicationDate.setWidth("110px");
			cmbApplicationDate.setHeight("22px");
			mainLayout.addComponent(cmbApplicationDate, "top:168.0px; left:141.0px;");

			// lblLeaveType
			lblLeaveType = new Label("Leave Type :");
			lblLeaveType.setImmediate(false);
			lblLeaveType.setWidth("-1px");
			lblLeaveType.setHeight("-1px");
			mainLayout.addComponent(lblLeaveType, "top:20.0px; left:410.0px;");

			// txtLeaveType
			txtLeaveType = new TextRead();
			txtLeaveType.setImmediate(true);
			txtLeaveType.setWidth("100px");
			txtLeaveType.setHeight("22px");
			mainLayout.addComponent(txtLeaveType, "top:18.0px; left:500.0px;");

			// lblLeaveFrom
			lblLeaveFrom = new Label("Leave From : ");
			lblLeaveFrom.setImmediate(false);
			lblLeaveFrom.setWidth("-1px");
			lblLeaveFrom.setHeight("-1px");
			mainLayout.addComponent(lblLeaveFrom, "top:50.0px; left:410.0px;");

			// dLeaveFrom
			dLeaveFrom = new PopupDateField();
			dLeaveFrom.setImmediate(true);
			dLeaveFrom.setWidth("110px");
			dLeaveFrom.setHeight("-1px");
			//dLeaveFrom.setValue(new java.util.Date());
			dLeaveFrom.setDateFormat("dd-MM-yyyy");
			dLeaveFrom.setEnabled(false);
			dLeaveFrom.setReadOnly(true);
			dLeaveFrom.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dLeaveFrom, "top:50.0px; left:500.0px;");


			// lblLeaveTo
			lblLeaveTo = new Label("Leave To : ");
			lblLeaveTo.setImmediate(false);
			lblLeaveTo.setWidth("-1px");
			lblLeaveTo.setHeight("-1px");
			mainLayout.addComponent(lblLeaveTo, "top:50.0px; left:620.0px;");

			// dLeaveTo
			dLeaveTo= new PopupDateField();
			dLeaveTo.setImmediate(true);
			dLeaveTo.setWidth("110px");
			dLeaveTo.setHeight("-1px");
			//dLeaveTo.setValue(new java.util.Date());
			dLeaveTo.setDateFormat("dd-MM-yyyy");
			dLeaveTo.setEnabled(false);
			dLeaveTo.setReadOnly(true);
			dLeaveTo.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dLeaveTo, "top:50.0px; left:710.0px;");


			// lblSenctionFrom
			lblSenctionFrom = new Label("Senction From :");
			lblSenctionFrom.setImmediate(false);
			lblSenctionFrom.setWidth("-1px");
			lblSenctionFrom.setHeight("-1px");
			mainLayout.addComponent(lblSenctionFrom, "top:70.0px; left:410.0px;");

			// dSenctionFrom
			dSenctionFrom = new PopupDateField();
			dSenctionFrom.setImmediate(true);
			dSenctionFrom.setWidth("110px");
			dSenctionFrom.setHeight("-1px");
			//dSenctionFrom.setValue(new java.util.Date());
			dSenctionFrom.setReadOnly(true);
			dSenctionFrom.setDateFormat("dd-MM-yyyy");
			dSenctionFrom.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dSenctionFrom, "top:70.0px; left:500.0px;");

			// lblSenctionTo
			lblSenctionTo = new Label("Senction From :");
			lblSenctionTo.setImmediate(false);
			lblSenctionTo.setWidth("-1px");
			lblSenctionTo.setHeight("-1px");
			mainLayout.addComponent(lblSenctionTo, "top:70.0px; left:620.0px;");

			// dSenctionTo
			dSenctionTo = new PopupDateField();
			dSenctionTo.setImmediate(true);
			dSenctionTo.setWidth("110px");
			dSenctionTo.setHeight("-1px");
			//dSenctionTo.setValue(new java.util.Date());
			dSenctionTo.setReadOnly(true);
			dSenctionTo.setDateFormat("dd-MM-yyyy");
			dSenctionTo.setEnabled(false);
			dSenctionTo.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dSenctionTo, "top:70.0px; left:710.0px;");

			// lblDuration
			lblDuration = new Label("Duration :");
			lblDuration.setImmediate(false);
			lblDuration.setWidth("-1px");
			lblDuration.setHeight("-1px");
			mainLayout.addComponent(lblDuration, "top:100.0px; left:410.0px;");

			// txtDuration
			txtDuration = new TextRead();
			txtDuration.setImmediate(true);
			txtDuration.setWidth("70px");
			txtDuration.setHeight("22px");
			mainLayout.addComponent(txtDuration, "top:98.0px; left:500.0px;");

			lblDays = new Label("");
			lblDays.setImmediate(true);
			lblDays.setWidth("-1px");
			lblDays.setHeight("-1px");
			mainLayout.addComponent(lblDays, "top:100.0px;left:580.0px;");

			// lblPurposeOfLeave
			lblPurposeOfLeave = new Label("Purpose :");
			lblPurposeOfLeave.setImmediate(false);
			lblPurposeOfLeave.setWidth("-1px");
			lblPurposeOfLeave.setHeight("-1px");
			mainLayout.addComponent(lblPurposeOfLeave, "top:130.0px; left:410.0px;");

			// txtPurposeOfLeave
			txtPurposeOfLeave = new TextRead();
			txtPurposeOfLeave.setImmediate(true);
			txtPurposeOfLeave.setWidth("280px");
			txtPurposeOfLeave.setHeight("-1px");
			mainLayout.addComponent(txtPurposeOfLeave, "top:128.0px; left:500.0px;");

			// lblReasonOfCancellation
			lblReasonOfCancellation = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Reason Of Cancellation :",Label.CONTENT_XHTML);
			lblReasonOfCancellation.setImmediate(false);
			lblReasonOfCancellation.setWidth("-1px");
			lblReasonOfCancellation.setHeight("-1px");
			mainLayout.addComponent(lblReasonOfCancellation, "top:210.0px; left:25.0px;");

			// txtReasonOfCancellation
			txtReasonOfCancellation = new TextField();
			txtReasonOfCancellation.setImmediate(true);
			txtReasonOfCancellation.setWidth("350px");
			txtReasonOfCancellation.setHeight("-1px");
			mainLayout.addComponent(txtReasonOfCancellation, "top:208.0px; left:170.0px;");

			// lblCancelledBy
			lblCancelledBy = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Cancelled By :",Label.CONTENT_XHTML);
			lblCancelledBy.setImmediate(false);
			lblCancelledBy.setWidth("-1px");
			lblCancelledBy.setHeight("-1px");
			mainLayout.addComponent(lblCancelledBy, "top:240.0px; left:25.0px;");

			// txtCancelledBy
			txtCancelledBy = new TextRead();
			txtCancelledBy.setImmediate(true);
			txtCancelledBy.setWidth("110px");
			txtCancelledBy.setHeight("-1px");
			mainLayout.addComponent(txtCancelledBy, "top:238.0px; left:170.0px;");

			// lblPermittedBy
			lblPermittedBy = new Label("<b><Font Color='#CD0606' size='3px'>*</Font></b>Permitted By :",Label.CONTENT_XHTML);
			lblPermittedBy.setImmediate(true);
			lblPermittedBy.setWidth("-1px");
			lblPermittedBy.setHeight("-1px");
			mainLayout.addComponent(lblPermittedBy, "top:240.0px; left:310.0px;");

			// txtPermittedBy
			txtPermittedBy = new TextField();
			txtPermittedBy.setImmediate(true);
			txtPermittedBy.setWidth("220px");
			txtPermittedBy.setHeight("-1px");
			mainLayout.addComponent(txtPermittedBy, "top:238.0px; left:410.0px;");

			mainLayout.addComponent(cButton, "top:270.0px; left:235.0px;");

			Label lblComment = new Label("Fields marked with <Font Color='#CD0606' size='3px'>(<b>*</b>)</Font> are mandatory",Label.CONTENT_XHTML);
			lblComment.setImmediate(true);
			lblComment.setWidth("-1px");
			lblComment.setHeight("-1px");
			mainLayout.addComponent(lblComment, "top:300.0px; left:30.0px;");
			return mainLayout;
		}


}
