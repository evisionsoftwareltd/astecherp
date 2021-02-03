package acc.appform.hrmModule;

	import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class LeaveApplicationCHO extends Window {
	
		private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "Delete",  "Refresh",  "Find", "", "","","Exit");
		private SessionBean sessionBean;

		private AbsoluteLayout mainLayout;
		private AbsoluteLayout senctionLayout;

		private OptionGroup opgTypeOfSearch;
		private List<String> lst=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

		public Table table = new Table();
		public ArrayList<Label> tbllblSerial = new ArrayList<Label>();
		public ArrayList<Label> tbllblDate = new ArrayList<Label>();
		public ArrayList<Label> tbllblWeekDay = new ArrayList<Label>();
		public ArrayList<ComboBox> tblCmbNatureOfLeave = new ArrayList<ComboBox>();
		public ArrayList<CheckBox> tblChkEnjoyed = new ArrayList<CheckBox>();

		HashMap <String,Object> hDateDayList = new HashMap <String,Object> ();

		private Label lblApplicationDate;
		private PopupDateField dApplicationDate;
		private Label lblDepartment;
		private ComboBox cmbDepartment;
		private Label lblSection;
		private ComboBox cmbSection;
		private Label lblEmpType;
		private ComboBox cmbEmpType;
		private Label lblEmployeeName;
		private ComboBox cmbEmployeeName;
		private Label lblEmployeeId;
		private TextRead txtEmployeeId;
		private Label lblId;
		private TextRead txtId;
		private Label lblDesignation;
		private TextRead txtDesignation;
		private Label lblPurposeOfLeave;
		private TextField txtPurposeOfLeave;
		private Label lblLeaveAddress;
		private TextField txtLeaveAddress;
		private Label lblMobileNo;
		private TextField txtMobileNo;
		private Label lblLeaveType;
		private ComboBox cmbLeaveType;
		private Label lblLeaveFrom;
		private PopupDateField dLeaveFrom;
		private Label lblLeaveTo;
		private PopupDateField dLeaveTo;
		private Label lblJoiningDate;
		private PopupDateField dJoiningDate;
		private Label lblLeaveBalance;
		private Label lblCasualLeave;
		private TextRead txtCasualLeave;
		private Label lblSickLeave;
		private TextRead txtSickLeave;
		private Label lblAnualLeave;
		private TextRead txtAnualLeave;
		private Label lblMeternityLeave;
		private TextRead txtMeternityLeave;
		private Label lblSenctionFrom;
		private PopupDateField dSenctionFrom;
		private Label lblSenctionTo;
		private PopupDateField dSenctionTo;
		private CheckBox chkApprove;
		private Label lblDuration;
		private TextRead txtDuration;
		private Label lblDays;
		private Label lblFriday;
		private TextRead txtFriday;
		private TextRead txtAutoId = new TextRead();
		private TextRead txtFindDate = new TextRead();
		private NativeButton btnTransection;
		private String updateID="";
		private ArrayList<Component> allComp = new ArrayList<Component>(); 
		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		
		private boolean isUpdate=false;
		private boolean isFind=false;

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
		String typeOfSearch="";

		private String serviceType[]={"Permanent","Temporary","Provisionary","Casual"};

		public LeaveApplicationCHO(SessionBean sessionBean)
		{
			this.sessionBean=sessionBean;
			this.setCaption("LEAVE APPLICATION CHO::"+sessionBean.getCompany());
			this.setResizable(false);
			buildMainLayout();
			setContent(mainLayout);
			tableInitialise();
			btnIni(true);
			componentIni(false);
			setEventAction();
			cButton.btnNew.focus();
			focusEnter();
			cmbDepartmentValueAdd();
			cmbAddLeaveData();
			authenticationCheck();
		}

		private void cmbDepartmentValueAdd()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct ei.vDepartmentId,dept.vDepartmentName from tbEmployeeInfo ei " +
						"inner join tbDepartmentInfo dept on ei.vDepartmentId=dept.vDepartmentId where dept.vDepartmentName='CHO'  order by dept.vDepartmentName";
				List <?> lst=session.createSQLQuery(query).list();
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
			catch(Exception exp)
			{
				showNotification("cmbDepartmentValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void cmbSectionValueAdd()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct ei.vSectionId,si.SectionName from tbEmployeeInfo ei " +
						"inner join tbSectionInfo si on ei.vSectionId=si.vSectionId where " +
						"ei.vDepartmentID='"+cmbDepartment.getValue()+"' order by si.SectionName";
				List <?> lst=session.createSQLQuery(query).list();
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
			catch(Exception exp)
			{
				showNotification("cmbSectionValueAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
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

		private void employeeDataClear()
		{
			txtId.setValue("");
			txtEmployeeId.setValue("");
			txtDesignation.setValue("");
			dJoiningDate.setValue(null);
			txtCasualLeave.setValue("");
			txtSickLeave.setValue("");
			txtAnualLeave.setValue("");
			txtMeternityLeave.setValue("");
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
						cmbSectionValueAdd();
					}
				}
			});

			cmbSection.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbEmpType.removeAllItems();
					if(cmbSection.getValue()!=null)
					{
						for(int i=0;i<serviceType.length;i++)
						{
							cmbEmpType.addItem(serviceType[i]);
							cmbEmpType.setItemCaption(serviceType[i], serviceType[i]);
						}
					}
				}
			});

			cmbEmpType.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbEmployeeName.removeAllItems();
					if(cmbEmpType.getValue()!=null)
					{
						cmbAddEmployeeData();
					}
				}
			});

			opgTypeOfSearch.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(opgTypeOfSearch.getValue().toString().trim().equals("Employee Name"))
					{
						lblEmployeeName.setValue("Employee Name : ");
						cmbEmployeeName.setWidth("250.0px");
						lblEmployeeId.setValue("Employee ID : ");
						txtEmployeeId.setWidth("140.0px");
						lblId.setValue("Proximity ID : ");
						txtId.setWidth("140.0px");
						txtClearCombo();
						cmbAddEmployeeData();
					}

					else if(opgTypeOfSearch.getValue().toString().trim().equals("Employee ID"))
					{
						lblEmployeeName.setValue("Employee ID : ");
						cmbEmployeeName.setWidth("140.0px");
						lblEmployeeId.setValue("Employee Name : ");
						txtEmployeeId.setWidth("250.0px");
						lblId.setValue("Proximity ID : ");
						txtId.setWidth("140.0px");
						txtClearCombo();
						cmbAddEmployeeData();
					}

					else if(opgTypeOfSearch.getValue().toString().trim().equals("Proximity ID"))
					{
						lblEmployeeName.setValue("Proximity ID : ");
						cmbEmployeeName.setWidth("140.0px");
						lblEmployeeId.setValue("Employee ID : ");
						txtEmployeeId.setWidth("140.0px");
						lblId.setValue("Employee Name : ");
						txtId.setWidth("250.0px");
						txtClearCombo();
						cmbAddEmployeeData();
					}
				}
			});
			cButton.btnDelete.addListener(new Button.ClickListener() {
				
				public void buttonClick(ClickEvent event) {
					if(cmbEmployeeName.getValue()!=null)
					{
						if(dLeaveFrom.getValue()!=null && dLeaveTo.getValue()!=null && dSenctionFrom.getValue()!=null && dSenctionTo.getValue()!=null)
						{
							deleteWork();
							
						}
						else
						{
							showNotification("Provide all data");
						}
					}
					else
					{
						showNotification("Provide Employee Id");
					}
				}
			});
			cButton.btnNew.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					isFind=false;
					txtClear();
					componentIni(true);
					btnIni(false);
					cmbEmployeeName.focus();
				}
			});

			cButton.btnSave.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{

					isFind=false;
					formValidation();
				}
			});

			cmbEmployeeName.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					employeeDataClear();
					if(cmbEmployeeName.getValue()!=null)
					{
						employeeSetData(cmbEmployeeName.getValue().toString());
					}
				}
			});

			dLeaveFrom.addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{ 
					dLeaveFromWork();
				}
			});

			dLeaveTo.addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{ 
					dLeaveToWork();
				}
			});


			dSenctionFrom.addListener(new ValueChangeListener()
			{

				public void valueChange(ValueChangeEvent event)
				{

					dSenctionFromWork();
				}
			});

			dSenctionTo.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					dSenctionToWork();
				}
			});

			btnTransection.addListener(new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					if(dSenctionFrom.getValue()!=null && dSenctionTo.getValue()!=null)
					{
						findDateList();
						dateDayAddTable();
					}
					else
					{
						showNotification("Select Senction Start & End Date", Notification.TYPE_WARNING_MESSAGE);
					}
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

			cButton.btnRefresh.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					isUpdate=false;
					isFind=false;
					txtClear();
					componentIni(false);
					btnIni(true);
				}
			});

			cButton.btnEdit.addListener(new ClickListener()
			{	
				public void buttonClick(ClickEvent event)
				{
					if(!tbllblDate.get(0).toString().equals(""))
					{
						isUpdate=true;
						componentIni(true);
						btnIni(false);
						leaveType = cmbLeaveType.getValue().toString();
						totalDays = txtDuration.getValue().toString();
					}
					else
					{
						showNotification("Warning!","There are nothing to edit", Notification.TYPE_WARNING_MESSAGE);
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
		private void dSenctionFromWork()
		{
			if(!isFind)
			{
				if(dSenctionFrom.getValue()!=null && dSenctionTo.getValue()!=null)
				{

					Session session=SessionFactoryUtil.getInstance().openSession();
					session.beginTransaction();

					String dbsenctionDate="select convert(date,dSenctionTo) from tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue().toString().trim()+"' " +
							"and ((convert(date,dSenctionFrom)<='"+(dSenctionFrom.getValue()==null?"1900-01-01":dateFormat.format(dSenctionFrom.getValue()))+"' and convert(date,dSenctionTo)>='"+(dSenctionFrom.getValue()==null?"1900-01-01":dateFormat.format(dSenctionFrom.getValue()))+"') " +
							"or (convert(date,dSenctionFrom)<='"+(dSenctionTo.getValue()==null?"1900-01-01":dateFormat.format(dSenctionTo.getValue()))+"' and convert(date,dSenctionTo)>='"+(dSenctionTo.getValue()==null?"1900-01-01":dateFormat.format(dSenctionTo.getValue()))+"'))";
					List <?> lstchk=session.createSQLQuery(dbsenctionDate).list();
					session.clear();
					if(!lstchk.isEmpty() && !isFind)
					{
						dLeaveTo.setValue(null);
						dSenctionFrom.setValue(null);
						dSenctionTo.setValue(null);
						showNotification("Warning","Data Already Exists!!!",Notification.TYPE_WARNING_MESSAGE);
					}
					else
					{
						String senctionDateFrom="";
						String senctionDateTo="";
						try
						{
							senctionDateFrom = dateFormat.format(dSenctionFrom.getValue());
							senctionDateTo = dateFormat.format(dSenctionTo.getValue());
							String chkMonthlySalarySQL="select * from tbSalary where autoEmployeeID='"+cmbEmployeeName.getValue().toString().trim()+"' " +
									"and vMonthName=DateName(MM,'"+senctionDateFrom+"') and " +
									"year='"+yearFormat.format(dLeaveFrom.getValue())+"'";
							List <?> lst=session.createSQLQuery(chkMonthlySalarySQL).list();
							if(lst.isEmpty())
							{
								String query = "select 0 as c, DATEDIFF(DAY,'"+senctionDateFrom+"','"+senctionDateTo+"')+1 as day";
								List <?> list = session.createSQLQuery(query).list();

								if(list.iterator().hasNext())
								{
									Object[] element = (Object[]) list.iterator().next();

									int totalDays = Integer.parseInt(element[1].toString());

									if(totalDays <1 && totalDays>-1)
									{
										txtDuration.setValue("");
										txtFriday.setValue("");
										showNotification("Select Valid Date Range", Notification.TYPE_WARNING_MESSAGE);
									}
									else if(totalDays>0)
									{
										int nmFriday = fridayCount(senctionDateFrom, senctionDateTo);
										txtDuration.setValue(totalDays);
										txtFriday.setValue(nmFriday);
									}
									else
									{
										dSenctionFrom.setValue(null);
										dSenctionTo.setValue(null);
										txtDuration.setValue("");
										txtFriday.setValue("");
										if(dSenctionFrom.getValue()!=null && dSenctionTo.getValue()!=null)
											showNotification(" Warning","Senction From Date must be less than Senction Date",Notification.TYPE_WARNING_MESSAGE);
									}
								}
							}
							else
							{
								dSenctionFrom.setValue(null);
								showNotification("Warning", "Salary already generated for the Employee!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						catch(Exception exp)
						{
							showNotification("dSenctionTo", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
						finally{session.close();}
					}

					if(cmbEmployeeName.getValue()!=null)
					{
						if(cmbEmployeeName.getValue()!=null)
						{
							if(cmbLeaveType.getValue()!=null)
							{
								leaveValidity();
							}
							else
							{
								showNotification("Warning","Select Leave Type");
							}
						}
						else
						{
							showNotification("Warning","Select Employee Name");
						}
					}
				}
			}
		}
		private void dSenctionToWork()
		{
			if(!isFind)
			{
				if(dSenctionFrom.getValue()!=null && dSenctionTo.getValue()!=null)
				{
					Session session=SessionFactoryUtil.getInstance().openSession();
					session.beginTransaction();
					String senctionDateFrom="";
					String senctionDateTo="";
					String dbsenctionDate="select convert(date,dSenctionTo) from tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue().toString().trim()+"' " +
							"and ((convert(date,dSenctionFrom)<='"+(dSenctionFrom.getValue()==null?"1900-01-01":dateFormat.format(dSenctionFrom.getValue()))+"' and convert(date,dSenctionTo)>='"+(dSenctionFrom.getValue()==null?"1900-01-01":dateFormat.format(dSenctionFrom.getValue()))+"') " +
							"or (convert(date,dSenctionFrom)<='"+(dSenctionTo.getValue()==null?"1900-01-01":dateFormat.format(dSenctionTo.getValue()))+"' and convert(date,dSenctionTo)>='"+(dSenctionTo.getValue()==null?"1900-01-01":dateFormat.format(dSenctionTo.getValue()))+"'))";

					List <?> lstchk=session.createSQLQuery(dbsenctionDate).list();
					session.clear();
					if(!lstchk.isEmpty() && !isFind)
					{
						dLeaveTo.setValue(null);
						dSenctionFrom.setValue(null);
						dSenctionTo.setValue(null);
						showNotification("Warning","Data Already Exists!!!",Notification.TYPE_WARNING_MESSAGE);
					}
					else
					{
						try
						{
							senctionDateFrom = dateFormat.format(dSenctionFrom.getValue());
							senctionDateTo = dateFormat.format(dSenctionTo.getValue());
							String chkMonthlySalarySQL="select * from tbSalary where autoEmployeeID='"+cmbEmployeeName.getValue().toString().trim()+"' " +
									"and vMonthName=DateName(MM,'"+senctionDateTo+"') and " +
									"year='"+yearFormat.format(dLeaveFrom.getValue())+"'";
							List <?> lst=session.createSQLQuery(chkMonthlySalarySQL).list();
							if(lst.isEmpty())
							{
								String query = "select 0 as c, DATEDIFF(DAY,'"+senctionDateFrom+"','"+senctionDateTo+"')+1 as day";
								List <?> list = session.createSQLQuery(query).list();

								if(list.iterator().hasNext())
								{
									Object[] element = (Object[]) list.iterator().next();

									int totalDays = Integer.parseInt(element[1].toString());

									if(totalDays <1 && totalDays>-1)
									{
										txtDuration.setValue("");
										txtFriday.setValue("");
										getParent().showNotification("Select Valid Date Range", Notification.TYPE_WARNING_MESSAGE);
									}
									else if(totalDays>0)
									{
										int nmFriday = fridayCount(senctionDateFrom, senctionDateTo);
										txtDuration.setValue(totalDays);
										txtFriday.setValue(nmFriday);
									}
									else
									{
										dSenctionFrom.setValue(null);
										dSenctionTo.setValue(null);
										txtDuration.setValue("");
										txtFriday.setValue("");
										if(dSenctionFrom.getValue()!=null && dSenctionTo.getValue()!=null)
											getParent().getWindow().showNotification(" Warning","Senction From Date must be less than Senction Date",Notification.TYPE_WARNING_MESSAGE);
									}
								}
							}
							else
							{
								dSenctionTo.setValue(null);
								showNotification("Warning", "Salary already generated for the Employee!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						catch(Exception exp)
						{
							showNotification("dSenctionTo", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
						}
						finally{session.close();}
					}
					if(cmbEmployeeName.getValue()!=null)
					{
						if(cmbEmployeeName.getValue()!=null)
						{
							if(cmbLeaveType.getValue()!=null)
							{
								leaveValidity();
							}
							else
							{
								getParent().showNotification("Warning","Select Leave Type");
							}
						}
						else
						{
							getParent().showNotification("Warning","Select Employee Name");
						}
					}
				}
			}
		}
		private void dLeaveToWork()
		{
			if(!isFind)
			{
				if(dLeaveFrom.getValue()!=null)
				{
					if(dLeaveTo.getValue()!=null)
					{
						if(cmbEmployeeName.getValue()!=null)
						{
							if(cmbLeaveType.getValue()!=null)
							{
								if(!txtCasualLeave.getValue().toString().trim().isEmpty() && !txtSickLeave.getValue().toString().trim().isEmpty() 
										&& !txtAnualLeave.getValue().toString().trim().isEmpty() && !txtMeternityLeave.getValue().toString().trim().isEmpty())
								{
									Session session=SessionFactoryUtil.getInstance().openSession();
									session.beginTransaction();
									try
									{
										String chkMonthlySalarySQL="select * from tbSalary where autoEmployeeID='"+cmbEmployeeName.getValue().toString().trim()+"' " +
												"and vMonthName=DateName(MM,'"+dateFormat.format(dLeaveTo.getValue())+"') and " +
												"year='"+yearFormat.format(dLeaveFrom.getValue())+"'";
										List <?> lst=session.createSQLQuery(chkMonthlySalarySQL).list();
										if(lst.isEmpty())
										{
											findDayDiffernce();
											leaveValidity();
										}
										else
										{
											dLeaveTo.setValue(null);
											showNotification("Warning", "Salary already generated for the Employee!!!", Notification.TYPE_WARNING_MESSAGE);
										}
									}
									catch(Exception exp)
									{
										showNotification("dLeaveFrom.addListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
									}
									finally{session.close();}
								}
								else
								{
									dLeaveTo.setValue(null);
									showNotification("Warning", "Please Check Your Leave Balance or Provide Leave Opening Balance first",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else
							{
								showNotification("Warning","Select Leave Type");
							}
						}
						else
						{
							dLeaveTo.setValue(null);
							showNotification("Warning","Select Employee Name");
						}
					}
				}
			}
		}
		private void dLeaveFromWork()
		{
			if(!isFind)
			{
				if(dLeaveFrom.getValue()!=null)
				{
					if(dLeaveTo.getValue()!=null)//  && isFind==false)
					{
						if(cmbEmployeeName.getValue()!=null)
						{
							if(cmbEmployeeName.getValue()!=null)
							{
								if(cmbLeaveType.getValue()!=null)
								{
									if(!txtCasualLeave.getValue().toString().trim().isEmpty() && !txtSickLeave.getValue().toString().trim().isEmpty() 
											&& !txtAnualLeave.getValue().toString().trim().isEmpty() && !txtMeternityLeave.getValue().toString().trim().isEmpty())
									{
										Session session=SessionFactoryUtil.getInstance().openSession();
										session.beginTransaction();
										try
										{
											String chkMonthlySalarySQL="select * from tbSalary where autoEmployeeID='"+cmbEmployeeName.getValue().toString().trim()+"' " +
													"and vMonthName=DateName(MM,'"+dateFormat.format(dLeaveFrom.getValue())+"') and " +
													"year='"+yearFormat.format(dLeaveFrom.getValue())+"'";
											List <?> lst=session.createSQLQuery(chkMonthlySalarySQL).list();
											if(lst.isEmpty())
											{
												findDayDiffernce();
												leaveValidity();
											}
											else
											{
												dLeaveFrom.setValue(new Date());
												showNotification("Warning", "Salary already generated for the Employee!!!", Notification.TYPE_WARNING_MESSAGE);
											}
										}
										catch(Exception exp)
										{
											showNotification("dLeaveFrom.addListener", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
										}
										finally{session.close();}
									}
									else
									{
										dLeaveTo.setValue(null);
										showNotification("Warning", "Please Check Your Leave Balance or Provide Leave Opening Balance first",Notification.TYPE_WARNING_MESSAGE);
									}
								}
								else
								{
									showNotification("Warning","Select Leave Type");
								}
							}
							else
							{
								dLeaveTo.setValue(null);
								showNotification("Warning","Select Employee Name");
							}
						}
					}
				}
			}
		}
		
		private void deleteWork()
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx=session.beginTransaction();
			String sqlCheck=" select * from tbSalary where "
					+ " vMonthName=DateName(MONTH,'"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"') and year=YEAR('"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"') "
					+ " and vMonthName=DateName(MONTH,'"+sessionBean.dfDb.format(dLeaveTo.getValue())+"') and year=YEAR('"+sessionBean.dfDb.format(dLeaveTo.getValue())+"') "
					+ " and autoEmployeeID='"+cmbEmployeeName.getValue()+"'";
			List<?> list=session.createSQLQuery(sqlCheck).list();
			if(list.isEmpty())
			{
				MessageBox mb=new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Delete?", new MessageBox.ButtonConfig(ButtonType.YES, "Yes"),new MessageBox.ButtonConfig(ButtonType.NO, "No"));
				mb.show(new EventListener() {
					
					public void buttonClicked(ButtonType buttonType) {
						if(buttonType==ButtonType.YES)
						{
							deleteLeave();
							txtClear();
							
						}
					}
				});
			}
			else
			{
				showNotification("Salary Already Generated Within Leave Apply Duration",Notification.TYPE_WARNING_MESSAGE);
			}
			
		}
		public void deleteLeave()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			Transaction tx=session.beginTransaction();
			try{


				String sql="insert into tbUdEmployeeLeave "+
				" select dApplicationDate,vAutoEmployeeId,vEmployeeId,vProximityID,vSectionID,vDesignationId,dJoiningDate,vLeaveType,dApplyFrom,"+
				" dApplyTo,vPurposeOfLeave,vLeaveAddress,vMobileNo,dSenctionFrom,dSenctionTo,iNoOfDays,iNoOfFridays,vRemarks,iApprove,"+
				" vUserId,dEntryTime,vUserIp,'Old',vDepartmentID from tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' "+
				" and dApplyFrom='"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"' and dApplyTo='"+sessionBean.dfDb.format(dLeaveTo.getValue())+"'";
				int x=session.createSQLQuery(sql).executeUpdate();
				if(x>0)
				{
					String sqlDel="delete tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' "
							+ " and dApplyFrom='"+sessionBean.dfDb.format(dLeaveFrom.getValue())+"' and dApplyTo='"+sessionBean.dfDb.format(dLeaveTo.getValue())+"'";
					session.createSQLQuery(sqlDel).executeUpdate();
					
					String leaveBalanceQuery="update tbLeaveBalanceNew set iClEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
							"tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' and vLeaveType='1' and " +
							"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
							"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
							"iSlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
							"tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' and vLeaveType='2' and " +
							"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
							"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
							"iAlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
							"tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' and vLeaveType='3' and " +
							"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
							"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
							"iMlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
							"tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue()+"' and vLeaveType='4' and " +
							"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
							"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"')) " +
							"where YEAR(currentYear)=YEAR(Getdate()) and vAutoEmployeeID='"+cmbEmployeeName.getValue()+"'";
					session.createSQLQuery(leaveBalanceQuery).executeUpdate();
				}
				tx.commit();
				showNotification("Leaves Deleted Successfully!");
			
			}
			catch(Exception exp)
			{
				tx.rollback();
				showNotification("Delete Leave:"+exp,Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}
		private void cmbAddEmployeeData()
		{
			cmbEmployeeName.removeAllItems();
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "";

				if(opgTypeOfSearch.getValue().toString().equals("Employee Name"))
				{
					query = "Select vEmployeeId,vEmployeeName FROM tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionID='"+cmbSection.getValue()+"' and vEmployeeType='"+cmbEmpType.getItemCaption(cmbEmpType.getValue())+"' and iStatus=1 order by employeeCode";
				}

				else if(opgTypeOfSearch.getValue().toString().equals("Employee ID"))
				{
					query = "Select vEmployeeId,employeecode FROM tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionID='"+cmbSection.getValue()+"' and vEmployeeType='"+cmbEmpType.getItemCaption(cmbEmpType.getValue())+"' and iStatus=1 order by employeeCode";
				}

				else if(opgTypeOfSearch.getValue().toString().equals("Proximity ID"))
				{
					query = "Select vEmployeeId,vProximityID FROM tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionID='"+cmbSection.getValue()+"' and vEmployeeType='"+cmbEmpType.getItemCaption(cmbEmpType.getValue())+"' and iStatus=1 order by employeeCode";
				}

				List <?> list = session.createSQLQuery(query).list();

				if(!list.isEmpty())
				{
					for (Iterator <?> iter = list.iterator(); iter.hasNext();)
					{
						Object[] element =  (Object[]) iter.next();	
						cmbEmployeeName.addItem(element[0]);
						cmbEmployeeName.setItemCaption(element[0], element[1].toString());	
					}
				}
				else
					showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
			catch(Exception ex)
			{
				showNotification("cmbAddEmployeeData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void cmbAddLeaveData()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "Select iLeaveTypeID, vLeaveTypeName FROM tbLeaveType";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbLeaveType.addItem(element[0].toString());
					cmbLeaveType.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception ex)
			{
				showNotification("cmbAddLeaveData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void findButtonEvent() 
		{
			Window win = new LeaveInfoFindCHO(sessionBean, txtAutoId, txtFindDate, typeOfSearch);
			win.addListener(new Window.CloseListener() 
			{
				public void windowClose(CloseEvent e) 
				{
					if (txtAutoId.getValue().toString().length() > 0)
					{
						txtClear();
						findInitialise(txtAutoId.getValue().toString() , txtFindDate.getValue(), typeOfSearch);
					}
				}
			});

			this.getParent().addWindow(win);
		}

		private void findInitialise(String autoID, Object findDate,String type) 
		{
			String sql = "";
			opgTypeOfSearch.setValue(type);
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try 
			{
				sql = " SELECT empl.iAutoId,empl.dApplicationDate,empl.vAutoEmployeeId,empl.vEmployeeId,empl.vProximityID,empl.vDepartmentID," +
						"empl.vSectionID,empl.vDesignationId,empl.dJoiningDate,empl.vLeaveType,convert(date,empl.dApplyFrom) dApplyFrom,convert(date,empl.dApplyTo) dApplyTo," +
						"empl.vPurposeOfLeave,empl.vLeaveAddress,empl.vMobileNo,convert(date,empl.dSenctionFrom) dSenctionFrom,convert(date,empl.dSenctionTo) dSenctionTo,empl.iNoOfDays," +
						"empl.iNoOfFridays,empl.vRemarks,empl.iApprove,empl.vUserId,empl.dEntryTime,empl.vUserIp,empi.vEmployeeType " +
						"from tbEmployeeLeave empl inner join tbemployeeinfo empi on empl.vAutoEmployeeId=empi.vEmployeeId where " +
						"empl.iAutoID = '"+autoID+"' and empl.dApplicationDate = '"+findDate+"' ";
				List <?> list = session.createSQLQuery(sql).list();
				isFind=true;

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();

					dApplicationDate.setValue(element[1]);
					cmbDepartment.setValue(element[5]);
					cmbSection.setValue(element[6]);
					cmbEmpType.setValue(element[24]);
					cmbEmployeeName.setValue(element[2]);
					cmbLeaveType.setValue(element[9].toString());
					dLeaveFrom.setValue(element[10]);
					dLeaveTo.setValue(element[11]);
					txtPurposeOfLeave.setValue(element[12].toString());
					txtLeaveAddress.setValue(element[13].toString());
					txtMobileNo.setValue(element[14].toString());
					dSenctionFrom.setValue(element[15]);
					dSenctionTo.setValue(element[16]);
					txtDuration.setValue(element[17].toString());
					txtFriday.setValue(element[18].toString());

					if(element[20].toString().equals("1"))
					{chkApprove.setValue(true);}
					else
					{chkApprove.setValue(false);}

					updateID = element[0].toString();
					findEmployeeId = element[2].toString();
				}
				findDateList();
				dateDayAddTable();
				setOldBalance();
			}
			catch (Exception exp)
			{
				showNotification("findInitialise", exp + "",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void setOldBalance()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = " SELECT iClBalance,iSlBalance,iAlBalance,iMlBalance,iClEnjoyed," +
						" iSlEnjoyed,iAlEnjoyed,iMlEnjoyed from tbLeaveBalanceNew where" +
						" vAutoEmployeeId='"+findEmployeeId+"' ";
				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();

					CBalance = element[0].toString();
					SBalance = element[1].toString();
					ABalance = element[2].toString();
					MBalance = element[3].toString();
					CEnjoy = element[4].toString();
					SEnjoy = element[5].toString();
					AEnjoy = element[6].toString();
					MEnjoy = element[7].toString();
				}
			}
			catch (Exception exp)
			{
				showNotification("setOldBalance", exp + "",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void employeeSetData(String employeeId)
		{
			txtClearCombo();
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = " SELECT E.employeeCode,E.vProximityId, E.vEmployeeName,E.vGender, S.AutoID, S.SectionName, D.designationId," +
						" D.designationName,E.dJoiningDate," +
						" (LB.iClyBalance+LB.iClOpening-LB.iClEnjoyed) as totalCL," +
						" (LB.iSlyBalance+LB.iSlOpening-LB.iSlEnjoyed) as totalSL," +
						" (LB.iAlyBalance+LB.iAlOpening-LB.iAlEnjoyed) as totalAL," +
						" (LB.iMlyBalance+LB.iMlOpening-LB.iMlEnjoyed) as totalML " +
						" FROM tbEmployeeInfo AS E" +
						" INNER JOIN tbSectionInfo AS S ON E.vSectionId = S.vSectionId " +
						" INNER JOIN tbDesignationInfo AS D ON E.vDesignationId = D.designationId " +
						" INNER JOIN tbLeaveBalanceNew as LB on E.vEmployeeId = LB.vAutoEmployeeId WHERE E.vEmployeeId = '"+employeeId+"' " +
						" and LB.iflag = '1' and YEAR(LB.currentYear)=YEAR((select MAX(currentYear) from tbLeaveBalanceNew)) " +
						" and isnull(vEncashment,'')!='Encashed' ORDER BY E.vEmployeeType ";

				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();

					if(opgTypeOfSearch.getValue().toString().equals("Employee Name"))
					{
						txtEmployeeId.setValue(element[0]);
						txtId.setValue(element[1].toString());
					}

					else if(opgTypeOfSearch.getValue().toString().equals("Employee ID"))
					{
						txtEmployeeId.setValue(element[2].toString());
						txtId.setValue(element[1].toString());
					}

					else if(opgTypeOfSearch.getValue().toString().equals("Proximity ID"))
					{
						txtEmployeeId.setValue(element[0]);
						txtId.setValue(element[2].toString());
					}

					txtDesignation.setValue(element[7].toString());
					dJoiningDate.setValue(element[8]);

					//Leave Balance
					txtCasualLeave.setValue(element[9].toString());
					txtSickLeave.setValue(element[10].toString());
					txtAnualLeave.setValue(element[11].toString());
					txtMeternityLeave.setValue(element[3].toString().equalsIgnoreCase("Male")?0:element[12].toString());
					sectionId = element[4].toString();
					designationId = element[6].toString();
				}
			}
			catch(Exception ex)
			{
				showNotification("employeeSetData", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void findDayDiffernce()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			String applyDateFrom="";
			String applyDateTo="";
			try
			{
				applyDateFrom = dateFormat.format(dLeaveFrom.getValue());
				applyDateTo = dateFormat.format(dLeaveTo.getValue());
				dSenctionFrom.setValue(dLeaveFrom.getValue());
				dSenctionTo.setValue(dLeaveTo.getValue());
				String query = "select 0 as c, DATEDIFF(DAY,'"+applyDateFrom+"','"+applyDateTo+"')+1 as day";
				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					int totalDays = Integer.parseInt(element[1].toString());

					if(totalDays <1 && totalDays>-1)
					{
						txtDuration.setValue("");
						txtFriday.setValue("");
						showNotification("Select Valid Date Range", Notification.TYPE_WARNING_MESSAGE);
					}
					else if(totalDays>0)
					{
						int nmFriday = fridayCount(applyDateFrom, applyDateTo);
						txtDuration.setValue(totalDays);
						txtFriday.setValue(nmFriday);
					}
					else
					{
						dLeaveTo.setValue(null);
						dSenctionFrom.setValue(null);
						dSenctionTo.setValue(null);
						txtDuration.setValue("");
						txtFriday.setValue("");
						if(dLeaveFrom.getValue()!=null && dLeaveTo.getValue()!=null)
							showNotification("Warning","Leave From Date must be less than Leave To Date!!!",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
			catch (Exception ex)
			{
				showNotification("findDayDiffernce", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private int fridayCount(String fromDate, String toDate)
		{
			int numFriday = 0;
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();

			try
			{
				String query = "Select 0 as nf, datediff(day, -3, '"+toDate+"')/7-datediff(day, -2, '"+fromDate+"')/7 as NF";
				List <?> list = session.createSQLQuery(query).list();

				if(list.iterator().hasNext())
				{
					Object[] element = (Object[]) list.iterator().next();
					numFriday = Integer.parseInt(element[1].toString());
				}
			}
			catch (Exception ex)
			{
				showNotification("fridayCount", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			return numFriday;  
		}

		private void leaveValidity()
		{
			if(cmbLeaveType.getValue().equals("1") && !isFind)
			{
				leaveCeheck(1);
			}
			else if(cmbLeaveType.getValue().equals("2") && !isFind)
			{
				leaveCeheck(2);
			}
			else if(cmbLeaveType.getValue().equals("3") && !isFind)
			{
				leaveCeheck(3);
			}
			else if(cmbLeaveType.getValue().equals("4") && !isFind)
			{
				leaveCeheck(4);
			}
		}

		private void leaveCeheck(int leaveID)
		{
			int leaveRemain=0,leaveApplydays=0;

			if(leaveID==1)
			{
				if(txtDuration.getValue()!="")
					leaveApplydays = Integer.parseInt(txtDuration.getValue().toString());
				leaveRemain = Integer.parseInt(txtCasualLeave.getValue().toString());
				if(leaveApplydays<=leaveRemain)
				{
					System.out.println("leaveApplydays<=leaveRemain : " +leaveApplydays +"<="+leaveRemain);
				}
				else
				{
					txtDuration.setValue("");
					txtFriday.setValue("");
					dLeaveTo.setValue(null);
					dSenctionFrom.setValue(null);
					dSenctionTo.setValue(null);
					this.getParent().showNotification("<font size= +2>Exceeded leave Balance!</font>",Notification.TYPE_WARNING_MESSAGE);
				}
			}

			else if(leaveID==2)
			{
				if(txtDuration.getValue()!="")
					leaveApplydays = Integer.parseInt(txtDuration.getValue().toString());
				leaveRemain = Integer.parseInt(txtSickLeave.getValue().toString());
				if(leaveApplydays<=leaveRemain)
				{
					System.out.println("leaveApplydays<=leaveRemain : " +leaveApplydays +"<="+leaveRemain);
				}
				else
				{
					txtDuration.setValue("");
					txtFriday.setValue("");
					dLeaveTo.setValue(null);
					dSenctionFrom.setValue(null);
					dSenctionTo.setValue(null);
					this.getParent().showNotification("<font size= +2>Exceeded leave Balance!</font>",Notification.TYPE_WARNING_MESSAGE);
				}
			}

			else if(leaveID==3)
			{
				if(txtDuration.getValue()!="")
					leaveApplydays = Integer.parseInt(txtDuration.getValue().toString());
				leaveRemain = Integer.parseInt(txtAnualLeave.getValue().toString());
				if(leaveApplydays<=leaveRemain)
				{
					System.out.println("leaveApplydays<=leaveRemain : " +leaveApplydays +"<="+leaveRemain);
				}
				else
				{
					txtDuration.setValue("");
					txtFriday.setValue("");
					dLeaveTo.setValue(null);
					dSenctionFrom.setValue(null);
					dSenctionTo.setValue(null);
					this.getParent().showNotification("<font size= +2>Exceeded leave Balance!</font>",Notification.TYPE_WARNING_MESSAGE);
				}
			}

			else
			{
				if(txtDuration.getValue()!="")
					leaveApplydays = Integer.parseInt(txtDuration.getValue().toString());
				leaveRemain = Integer.parseInt(txtMeternityLeave.getValue().toString());
				if(leaveApplydays<=leaveRemain)
				{
					System.out.println("leaveApplydays<=leaveRemain : " +leaveApplydays +"<="+leaveRemain);
				}
				else
				{
					txtDuration.setValue("");
					txtFriday.setValue("");
					dLeaveTo.setValue(null);
					dSenctionFrom.setValue(null);
					dSenctionTo.setValue(null);
					this.getParent().showNotification("<font size= +2>Exceeded leave Balance!</font>",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		}

		private void findDateList()
		{
			hDateDayList.clear();
			String finddate = "";
			String finddayName = "";
			try
			{
				List<Date> dates = new ArrayList<Date>();
				String str_date = new SimpleDateFormat("dd-MM-yyyy").format(dSenctionFrom.getValue());
				String end_date = new SimpleDateFormat("dd-MM-yyyy").format(dSenctionTo.getValue());
				DateFormat formatter ; 

				formatter = new SimpleDateFormat("dd-MM-yyyy");
				Date  startDate = (Date)formatter.parse(str_date); 
				Date  endDate = (Date)formatter.parse(end_date);
				long interval = 24*1000 * 60 * 60; // 1 hour in millis
				long endTime =endDate.getTime() ; // create your endtime here, possibly using Calendar or Date
				long curTime = startDate.getTime();
				while (curTime <= endTime)
				{
					dates.add(new Date(curTime));
					curTime += interval;
				}

				for(int i=0;i<dates.size();i++)
				{
					Date lDate =(Date)dates.get(i);
					finddate = formatter.format(lDate);
					finddayName = findDayName(finddate);
					hDateDayList.put(Integer.toString(i), finddate+"#"+finddayName);
				}
			}
			catch (Exception ex)
			{
				this.getParent().showNotification("findDateList",ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
		}

		private String findDayName(String sDate)
		{
			String finalDay = "";
			try
			{
				String inputDate= sDate;
				SimpleDateFormat format1=new SimpleDateFormat("dd-MM-yyyy");
				Date dt1=format1.parse(inputDate);
				DateFormat format2=new SimpleDateFormat("EEEE");
				finalDay=format2.format(dt1);
			}
			catch (Exception ex)
			{
				this.getParent().showNotification("findDayName", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			return finalDay;
		}

		private void dateDayAddTable()
		{
			removeDataTable();
			if(hDateDayList.size()!=0)
			{
				String abc = hDateDayList.toString().replaceAll("\\{", "");
				abc = abc.replaceAll("\\}", "");

				StringTokenizer st = new StringTokenizer(hDateDayList.toString());

				int len = st.countTokens();

				System.out.println(len);

				for(int i=0;i<len;i++)
				{
					if((i)==tbllblDate.size())
					{
						tableRowAdd(i);
					}

					String ss = st.nextToken();

					int equal = ss.indexOf("=");
					int hash = ss.indexOf("#");

					String datelis = ss.substring(equal+1,hash);
					String dna = ss.substring(hash+1,ss.length()-1);

					tbllblDate.get(i).setValue(datelis);
					tbllblWeekDay.get(i).setValue(dna);
					tblCmbNatureOfLeave.get(i).setValue(cmbLeaveType.getValue());
					tblChkEnjoyed.get(i).setValue(true);
				}
			}
		}

		public void tableInitialise()
		{
			for(int i=0;i<4;i++)
			{
				tableRowAdd(i);
			}
		}

		public void tableRowAdd(final int ar)
		{
			tbllblSerial.add(ar,new Label());
			tbllblSerial.get(ar).setWidth("100%");
			tbllblSerial.get(ar).setHeight("14px");
			tbllblSerial.get(ar).setValue(ar+1);

			tbllblDate.add(ar,new Label());
			tbllblDate.get(ar).setWidth("100%");

			tbllblWeekDay.add(ar,new Label());
			tbllblWeekDay.get(ar).setWidth("100%");

			tblCmbNatureOfLeave.add(ar,new ComboBox());
			tblCmbNatureOfLeave.get(ar).setWidth("100%");
			tblCmbNatureOfLeave.get(ar).setImmediate(true);
			tblCmbNatureOfLeave.get(ar).setNullSelectionAllowed(true);
			tblCmbNatureOfLeave.get(ar).setEnabled(false);
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "Select iLeaveTypeID, vLeaveTypeName FROM tbLeaveType";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					tblCmbNatureOfLeave.get(ar).addItem(element[0].toString());
					tblCmbNatureOfLeave.get(ar).setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception ex)
			{
				showNotification("tableRowAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}

			tblChkEnjoyed.add(ar,new CheckBox());
			tblChkEnjoyed.get(ar).setWidth("100%");		

			table.addItem(new Object[]{tbllblSerial.get(ar),tbllblDate.get(ar),tbllblWeekDay.get(ar),tblCmbNatureOfLeave.get(ar),tblChkEnjoyed.get(ar)},ar);
		}

		private void removeDataTable()
		{		
			for(int i=0; i<tbllblDate.size(); i++)
			{
				tbllblDate.get(i).setValue("");
				tbllblWeekDay.get(i).setValue("");
				tblChkEnjoyed.get(i).setValue(false);
				tblCmbNatureOfLeave.get(i).setValue(null);
			}
		}


		private void formValidation()
		{
			if(cmbEmployeeName.getValue()!=null)
			{
				if(cmbLeaveType.getValue()!=null)
				{
					if(dLeaveTo.getValue()!=null)
					{
						if(!txtPurposeOfLeave.getValue().toString().equals(""))
						{
							if(!txtLeaveAddress.getValue().toString().equals(""))
							{
								if(tblCmbNatureOfLeave.get(0).getValue()!=null && tblCmbNatureOfLeave.get(0).getValue().toString()==cmbLeaveType.getValue().toString())
								{
									saveBtnAction();
								}
								else
								{
									showNotification("Warning!","Generate Full Transection", Notification.TYPE_WARNING_MESSAGE);
									btnTransection.focus();
								}
							}
							else
							{
								showNotification("Warning!","Provide Leave Address", Notification.TYPE_WARNING_MESSAGE);
								txtLeaveAddress.focus();
							}
						}
						else
						{
							showNotification("Warning!","Provide Purpose of leave", Notification.TYPE_WARNING_MESSAGE);
							txtPurposeOfLeave.focus();
						}
					}
					else
					{
						showNotification("Warning!","Select Leave Date To", Notification.TYPE_WARNING_MESSAGE);
						dLeaveTo.focus();
					}
				}
				else
				{
					showNotification("Warning!","Select Leave Type", Notification.TYPE_WARNING_MESSAGE);
					cmbLeaveType.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select Employee Name", Notification.TYPE_WARNING_MESSAGE);
				cmbEmployeeName.focus();
			}
		}

		private void saveBtnAction()
		{
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
							isFind = false;
							componentIni(false);
							btnIni(true);
							System.out.println(dLeaveFrom.getValue()+" "+dLeaveTo.getValue()+" "+dSenctionFrom.getValue()+" "+dSenctionTo.getValue());
							cButton.btnNew.focus();
						}
					}
				});
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
							isFind = false;						
							componentIni(false);
							btnIni(true);
							System.out.println(dLeaveFrom.getValue()+" "+dLeaveTo.getValue()+" "+dSenctionFrom.getValue()+" "+dSenctionTo.getValue());
							cButton.btnNew.focus();
						}
					}
				});
			}
		}

		private void insertData()
		{
			int approveFlag = 0;
			Session session=SessionFactoryUtil.getInstance().openSession();
			Transaction tx=session.beginTransaction();
			try
			{
				System.out.println("Deg :"+designationId);
				System.out.println("Sec :"+sectionId);
				String insertquery = "";

				approveFlag = chkApprove.booleanValue()==true?1:0;

				String autoEmpID="";
				String empID="";
				String empProxID="";

				if(opgTypeOfSearch.getValue().toString().trim().equals("Employee Name"))
				{
					autoEmpID=cmbEmployeeName.getValue().toString().trim();
					empID=txtEmployeeId.getValue().toString().trim();
					empProxID=txtId.getValue().toString().trim();
				}

				else if(opgTypeOfSearch.getValue().toString().trim().equals("Employee ID"))
				{
					autoEmpID=cmbEmployeeName.getValue().toString().trim();
					empID=cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue()).toString().trim();
					empProxID=txtId.getValue().toString().trim();
				}

				else if(opgTypeOfSearch.getValue().toString().trim().equals("Proximity ID"))
				{
					autoEmpID=cmbEmployeeName.getValue().toString().trim();
					empProxID=cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue()).toString().trim();
					empID=txtEmployeeId.getValue().toString().trim();
				}

				insertquery = "Insert into tbEmployeeLeave " +
						" (dApplicationDate,vAutoEmployeeID,vEmployeeId,vProximityID,vSectionID, " +
						" vDesignationId,dJoiningDate, " +
						" vLeaveType,dApplyFrom, " +
						" dApplyTo,vPurposeOfLeave, " +
						" vLeaveAddress,vMobileNo, " +
						" dSenctionFrom,dSenctionTo, " +
						" iNoOfDays,iNoOfFridays,vRemarks,iApprove, " +
						" vUserId ,dEntryTime,vUserIp,vDepartmentID) "+
						" values('"+dateFormat.format(dApplicationDate.getValue())+"','"+autoEmpID+"','"+empID+"','"+empProxID+"','"+cmbSection.getValue()+"', " +
						" '"+designationId+"','"+dateFormat.format(dJoiningDate.getValue())+"','"+cmbLeaveType.getValue().toString()+"', " +
						" '"+dateFormat.format(dLeaveFrom.getValue())+"', '"+dateFormat.format(dLeaveTo.getValue())+"', " +
						" '"+txtPurposeOfLeave.getValue().toString()+"', '"+txtLeaveAddress.getValue().toString()+"', " +
						" '"+txtMobileNo.getValue().toString()+"','"+dateFormat.format(dSenctionFrom.getValue())+"', " +
						" '"+dateFormat.format(dSenctionTo.getValue())+"','"+txtDuration.getValue()+"','"+txtFriday.getValue()+"', " +
						" '"+"Remarks"+"', '"+approveFlag+"', " +
						" '"+sessionBean.getUserName()+"', CURRENT_TIMESTAMP , '"+sessionBean.getUserIp()+"','"+cmbDepartment.getValue()+"')";

				session.createSQLQuery(insertquery).executeUpdate();
				session.clear();
				
				String leaveBalanceQuery="update tbLeaveBalanceNew set iClEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
						"tbEmployeeLeave where vAutoEmployeeId='"+autoEmpID+"' and vLeaveType='1' and " +
						"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
						"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
						"iSlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
						"tbEmployeeLeave where vAutoEmployeeId='"+autoEmpID+"' and vLeaveType='2' and " +
						"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
						"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
						"iAlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
						"tbEmployeeLeave where vAutoEmployeeId='"+autoEmpID+"' and vLeaveType='3' and " +
						"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
						"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
						"iMlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
						"tbEmployeeLeave where vAutoEmployeeId='"+autoEmpID+"' and vLeaveType='4' and " +
						"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
						"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"')) " +
						"where YEAR(currentYear)=YEAR(Getdate()) and vAutoEmployeeID='"+autoEmpID+"'";
				session.createSQLQuery(leaveBalanceQuery).executeUpdate();
				
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

		private void updateData()
		{
			int approveFlag = 0;
			
			Session session=SessionFactoryUtil.getInstance().openSession();
			Transaction tx=session.beginTransaction();
			try
			{
				String insertquery = "";
				approveFlag = chkApprove.booleanValue()==true?1:0;
				
				String employeeID = cmbEmployeeName.getItemCaption(cmbEmployeeName.getValue());
				if(opgTypeOfSearch.getValue()!="Employee ID")
					employeeID = txtEmployeeId.getValue().toString().trim();
				
				insertquery = "UPDATE tbEmployeeLeave set " +
						" dApplicationDate='"+dateFormat.format(dApplicationDate.getValue())+"', vAutoEmployeeId='"+cmbEmployeeName.getValue().toString().trim()+"'," +
						" vEmployeeId='"+employeeID+"', vProximityId='"+txtId.getValue().toString().trim()+"',"+
						" vDesignationId='"+designationId+"'," +
						" dJoiningDate='"+dateFormat.format(dJoiningDate.getValue())+"', vLeaveType='"+cmbLeaveType.getValue().toString()+"'," +
						" dApplyFrom='"+dateFormat.format(dLeaveFrom.getValue())+"', dApplyTo='"+dateFormat.format(dLeaveTo.getValue())+"'," +
						" vPurposeOfLeave='"+txtPurposeOfLeave.getValue().toString()+"', vLeaveAddress='"+txtLeaveAddress.getValue().toString()+"'," +
						" vMobileNo='"+txtMobileNo.getValue().toString()+"', dSenctionFrom='"+dateFormat.format(dSenctionFrom.getValue())+"'," +
						" dSenctionTo='"+dateFormat.format(dSenctionTo.getValue())+"', iNoOfDays='"+txtDuration.getValue()+"'," +
						" iNoOfFridays='"+txtFriday.getValue()+"',vRemarks='Remarks'," +
						" iApprove='"+approveFlag+"', vUserId='"+sessionBean.getUserName()+"'," +
						" dEntryTime=CURRENT_TIMESTAMP,vUserIp='"+sessionBean.getUserIp()+"' where iAutoId='"+updateID+"' ";
				session.createSQLQuery(insertquery).executeUpdate();
				session.clear();
				
				String leaveBalanceQuery="update tbLeaveBalanceNew set iClEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
						"tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue().toString().trim()+"' and vLeaveType='1' and " +
						"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
						"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
						"iSlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
						"tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue().toString().trim()+"' and vLeaveType='2' and " +
						"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
						"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
						"iAlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
						"tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue().toString().trim()+"' and vLeaveType='3' and " +
						"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
						"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"'))," +
						"iMlEnjoyed=(select ISNULL(Sum(iNoOfDays),0) from " +
						"tbEmployeeLeave where vAutoEmployeeId='"+cmbEmployeeName.getValue().toString().trim()+"' and vLeaveType='4' and " +
						"(Year(dSenctionFrom)='"+yearFormat.format(dSenctionFrom.getValue())+"' and " +
						"Year(dSenctionTo)='"+yearFormat.format(dSenctionTo.getValue())+"')) " +
						"where YEAR(currentYear)=YEAR(Getdate()) and vAutoEmployeeID='"+cmbEmployeeName.getValue().toString().trim()+"'";
				session.createSQLQuery(leaveBalanceQuery).executeUpdate();
				tx.commit();
				txtClear();

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
			cmbEmpType.setEnabled(t);
			opgTypeOfSearch.setEnabled(t);
			cmbEmployeeName.setEnabled(t);
			txtEmployeeId.setEnabled(t);
			txtId.setEnabled(t);
			cmbDepartment.setEnabled(t);
			cmbSection.setEnabled(t);
			txtDesignation.setEnabled(t);
			txtCasualLeave.setEnabled(t);
			txtSickLeave.setEnabled(t);
			txtAnualLeave.setEnabled(t);
			txtMeternityLeave.setEnabled(t);
			cmbLeaveType.setEnabled(t);
			dLeaveFrom.setEnabled(t);
			dLeaveTo.setEnabled(t);
			txtPurposeOfLeave.setEnabled(t);
			txtLeaveAddress.setEnabled(t);
			txtMobileNo.setEnabled(t);
			dSenctionFrom.setEnabled(t);
			dSenctionTo.setEnabled(t);
			chkApprove.setEnabled(t);
			txtDuration.setEnabled(t);
			txtFriday.setEnabled(t);
			btnTransection.setEnabled(t);
			table.setEnabled(t);
		}

		private void txtClear()
		{
			dApplicationDate.setValue(new java.util.Date());
			cmbEmpType.setValue(null);
			cmbEmployeeName.setValue(null);
			txtEmployeeId.setValue("");
			txtId.setValue("");
			cmbDepartment.setValue(null);
			cmbSection.setValue(null);
			txtDesignation.setValue("");
			dJoiningDate.setValue(new java.util.Date());
			txtCasualLeave.setValue("");
			txtSickLeave.setValue("");
			txtAnualLeave.setValue("");
			txtMeternityLeave.setValue("");
			cmbLeaveType.setValue(null);
			dLeaveFrom.setValue(new java.util.Date());
			dLeaveTo.setValue(null);
			txtPurposeOfLeave.setValue("");
			txtLeaveAddress.setValue("");
			txtMobileNo.setValue("");
			dSenctionFrom.setValue(null);
			dSenctionTo.setValue(null);
			chkApprove.setValue(false);
			txtDuration.setValue("");
			txtFriday.setValue("");

			for(int i=0;i<tbllblSerial.size();i++)
			{
				tbllblDate.get(i).setValue("");
				tbllblWeekDay.get(i).setValue("");
				tblCmbNatureOfLeave.get(i).setValue(null);
				tblChkEnjoyed.get(i).setValue(false);
			}
		}

		private void txtClearCombo()
		{
			txtEmployeeId.setValue("");
			txtId.setValue("");
			txtDesignation.setValue("");
			dJoiningDate.setValue(new java.util.Date());
			txtCasualLeave.setValue("");
			txtSickLeave.setValue("");
			txtAnualLeave.setValue("");
			txtMeternityLeave.setValue("");
			cmbLeaveType.setValue(null);
			dLeaveFrom.setValue(new java.util.Date());
			dLeaveTo.setValue(null);
			txtPurposeOfLeave.setValue("");
			txtLeaveAddress.setValue("");
			txtMobileNo.setValue("");
			dSenctionFrom.setValue(null);
			dSenctionTo.setValue(null);
			chkApprove.setValue(false);
			txtDuration.setValue("");
			txtFriday.setValue("");

			for(int i=0;i<tbllblSerial.size();i++)
			{
				tbllblDate.get(i).setValue("");
				tbllblWeekDay.get(i).setValue("");
				tblCmbNatureOfLeave.get(i).setValue(null);
				tblChkEnjoyed.get(i).setValue(false);
			}
		}

		private void focusEnter()
		{
			allComp.add(dApplicationDate);
			allComp.add(cmbSection);
			allComp.add(cmbEmpType);
			allComp.add(cmbEmployeeName);
			allComp.add(cmbLeaveType);
			allComp.add(dLeaveTo);
			allComp.add(txtPurposeOfLeave);
			allComp.add(txtLeaveAddress);
			allComp.add(txtMobileNo);
			allComp.add(btnTransection);

			for(int i=0;i<tbllblSerial.size();i++)
			{
				allComp.add(tblCmbNatureOfLeave.get(i));
				allComp.add(tblChkEnjoyed.get(i));
			}
			allComp.add(cButton.btnSave);
			new FocusMoveByEnter(this,allComp);
		}

		public AbsoluteLayout buildMainLayout()
		{
			// common part: create layout
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setWidth("780px");
			mainLayout.setHeight("620px");
			mainLayout.setMargin(false);

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
			mainLayout.addComponent(dApplicationDate, "top:18.0px; left:140.0px;");

			// lblSection
			lblDepartment = new Label("Department Name :");
			lblDepartment.setImmediate(false);
			lblDepartment.setWidth("-1px");
			lblDepartment.setHeight("-1px");
			mainLayout.addComponent(lblDepartment, "top:45.0px; left:30.0px;");

			// cmbSection
			cmbDepartment = new ComboBox();
			cmbDepartment.setImmediate(true);
			cmbDepartment.setWidth("200px");
			cmbDepartment.setHeight("22px");
			cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbDepartment, "top:43.0px; left:140.0px;");

			// lblSection
			lblSection = new Label("Section Name :");
			lblSection.setImmediate(false);
			lblSection.setWidth("-1px");
			lblSection.setHeight("-1px");
			mainLayout.addComponent(lblSection, "top:70.0px; left:30.0px;");

			// cmbSection
			cmbSection = new ComboBox();
			cmbSection.setImmediate(true);
			cmbSection.setWidth("200px");
			cmbSection.setHeight("22px");
			cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbSection, "top:68.0px; left:140.0px;");

			lblEmpType=new Label("Service Type : ");
			mainLayout.addComponent(lblEmpType, "top:95.0px; left:30.0px;");

			cmbEmpType=new ComboBox();
			cmbEmpType.setImmediate(true);
			cmbEmpType.setWidth("180.0px");
			cmbEmpType.setHeight("22.0px");
			mainLayout.addComponent(cmbEmpType, "top:93.0px; left:140.0px;");

			opgTypeOfSearch=new OptionGroup("",lst);
			opgTypeOfSearch.setImmediate(true);
			opgTypeOfSearch.setStyleName("horizontal");
			opgTypeOfSearch.select("Employee Name");
			mainLayout.addComponent(opgTypeOfSearch, "top:120.0px;left:25.0px;");

			// lblEmployeeName
			lblEmployeeName = new Label("Employee Name :");
			lblEmployeeName.setImmediate(false);
			lblEmployeeName.setWidth("-1px");
			lblEmployeeName.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeName, "top:145.0px; left:30.0px;");

			// cmbEmployeeName
			cmbEmployeeName = new ComboBox();
			cmbEmployeeName.setImmediate(true);
			cmbEmployeeName.setWidth("250px");
			cmbEmployeeName.setHeight("-1px");
			cmbEmployeeName.setNullSelectionAllowed(true);
			cmbEmployeeName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbEmployeeName, "top:143.0px; left:140.0px;");

			lblEmployeeId=new Label("Employee ID :");
			lblEmployeeId.setImmediate(false);
			lblEmployeeId.setWidth("-1px");
			lblEmployeeId.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeId, "top:170.0px;left:30.0px;");

			txtEmployeeId=new TextRead();
			txtEmployeeId.setImmediate(false);
			txtEmployeeId.setWidth("140.0px");
			txtEmployeeId.setHeight("22px");
			mainLayout.addComponent(txtEmployeeId, "top:168.0px;left:140.0px;");

			// lblId
			lblId = new Label("Proximity ID :");
			lblId.setImmediate(false);
			lblId.setWidth("-1px");
			lblId.setHeight("-1px");
			mainLayout.addComponent(lblId, "top:195.0px; left:30.0px;");

			// txtId
			txtId = new TextRead();
			txtId.setImmediate(true);
			txtId.setWidth("140px");
			txtId.setHeight("22px");
			mainLayout.addComponent(txtId, "top:193.0px; left:140.0px;");

			// lblDesignation
			lblDesignation = new Label("Designation :");
			lblDesignation.setImmediate(false);
			lblDesignation.setWidth("-1px");
			lblDesignation.setHeight("-1px");
			mainLayout.addComponent(lblDesignation, "top:220.0px; left:30.0px;");

			// txtDesignation
			txtDesignation = new TextRead();
			txtDesignation.setImmediate(true);
			txtDesignation.setWidth("200px");
			txtDesignation.setHeight("22px");
			mainLayout.addComponent(txtDesignation, "top:218.0px; left:141.0px;");

			// lblJoiningDate
			lblJoiningDate = new Label("Joining Date : ");
			lblJoiningDate.setImmediate(false);
			lblJoiningDate.setWidth("-1px");
			lblJoiningDate.setHeight("-1px");
			mainLayout.addComponent(lblJoiningDate, "top:245.0px; left:30.0px;");

			// dJoiningDate
			dJoiningDate = new PopupDateField();
			dJoiningDate.setImmediate(true);
			dJoiningDate.setWidth("110px");
			dJoiningDate.setHeight("-1px");
			dJoiningDate.setValue(new java.util.Date());
			dJoiningDate.setDateFormat("dd-MM-yyyy");
			dJoiningDate.setEnabled(false);
			dJoiningDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dJoiningDate, "top:243.0px; left:140.0px;");


			// lblLeaveBalance
			lblLeaveBalance = new Label("<html> <b><u>Leave Balance</u></b></html>",Label.CONTENT_XHTML);
			lblLeaveBalance.setImmediate(false);
			lblLeaveBalance.setWidth("-1px");
			lblLeaveBalance.setHeight("-1px");
			mainLayout.addComponent(lblLeaveBalance, "top:270.0px; left:140.0px;");

			// lblCasualLeave
			lblCasualLeave = new Label("CL :");
			lblCasualLeave.setImmediate(false);
			lblCasualLeave.setWidth("-1px");
			lblCasualLeave.setHeight("-1px");
			mainLayout.addComponent(lblCasualLeave, "top:295.0px; left:79.0px;");

			// txtCasualLeave
			txtCasualLeave = new TextRead();
			txtCasualLeave.setImmediate(true);
			txtCasualLeave.setWidth("70px");
			txtCasualLeave.setHeight("22px");
			mainLayout.addComponent(txtCasualLeave, "top:293.0px; left:105.0px;");

			// lblSickLeave
			lblSickLeave = new Label("SL :");
			lblSickLeave.setImmediate(false);
			lblSickLeave.setWidth("-1px");
			lblSickLeave.setHeight("-1px");
			mainLayout.addComponent(lblSickLeave, "top:295.0px; left:190.0px;");

			// txtSickLeave
			txtSickLeave = new TextRead();
			txtSickLeave.setImmediate(true);
			txtSickLeave.setWidth("70px");
			txtSickLeave.setHeight("22px");
			mainLayout.addComponent(txtSickLeave, "top:293.0px; left:215.0px;");

			// lblAnualLeave
			lblAnualLeave = new Label("EL :");
			lblAnualLeave.setImmediate(false);
			lblAnualLeave.setWidth("-1px");
			lblAnualLeave.setHeight("-1px");
			mainLayout.addComponent(lblAnualLeave, "top:320.0px; left:80.0px;");

			// txtAnualLeave
			txtAnualLeave = new TextRead();
			txtAnualLeave.setImmediate(true);
			txtAnualLeave.setWidth("70px");
			txtAnualLeave.setHeight("22px");
			mainLayout.addComponent(txtAnualLeave, "top:318.0px; left:105.0px;");

			// lblMeternityLeave
			lblMeternityLeave = new Label("ML :");
			lblMeternityLeave.setImmediate(false);
			lblMeternityLeave.setWidth("-1px");
			lblMeternityLeave.setHeight("-1px");
			mainLayout.addComponent(lblMeternityLeave, "top:320.0px; left:189.0px;");

			// txtMeternityLeave
			txtMeternityLeave = new TextRead();
			txtMeternityLeave.setImmediate(true);
			txtMeternityLeave.setWidth("70px");
			txtMeternityLeave.setHeight("22px");
			mainLayout.addComponent(txtMeternityLeave, "top:318.0px; left:215.0px;");

			// lblLeaveType
			lblLeaveType = new Label("Leave Type :");
			lblLeaveType.setImmediate(false);
			lblLeaveType.setWidth("-1px");
			lblLeaveType.setHeight("-1px");
			mainLayout.addComponent(lblLeaveType, "top:20.0px; left:430.0px;");

			// cmbLeaveType
			cmbLeaveType = new ComboBox();
			cmbLeaveType.setImmediate(true);
			cmbLeaveType.setWidth("130px");
			cmbLeaveType.setHeight("-1px");
			mainLayout.addComponent(cmbLeaveType, "top:18.0px; left:530.0px;");

			// lblLeaveFrom
			lblLeaveFrom = new Label("Leave From :");
			lblLeaveFrom.setImmediate(false);
			lblLeaveFrom.setWidth("-1px");
			lblLeaveFrom.setHeight("-1px");
			mainLayout.addComponent(lblLeaveFrom, "top:45.0px; left:430.0px;");

			// dLeaveFrom
			dLeaveFrom = new PopupDateField();
			dLeaveFrom.setImmediate(true);
			dLeaveFrom.setWidth("110px");
			dLeaveFrom.setHeight("-1px");
			dLeaveFrom.setValue(new java.util.Date());
			dLeaveFrom.setDateFormat("dd-MM-yyyy");
			dLeaveFrom.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dLeaveFrom, "top:43.0px; left:530.0px;");

			// lblLeaveTo
			lblLeaveTo = new Label("Leave To :");
			lblLeaveTo.setImmediate(false);
			lblLeaveTo.setWidth("-1px");
			lblLeaveTo.setHeight("-1px");
			mainLayout.addComponent(lblLeaveTo, "top:70.0px; left:430.0px;");

			// dLeaveTo
			dLeaveTo = new PopupDateField();
			dLeaveTo.setImmediate(true);
			dLeaveTo.setWidth("110px");
			dLeaveTo.setHeight("-1px");
			//dLeaveTo.setValue(new java.util.Date());
			dLeaveTo.setDateFormat("dd-MM-yyyy");
			dLeaveTo.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(dLeaveTo, "top:68.0px; left:530.0px;");

			// lblPurposeOfLeave
			lblPurposeOfLeave = new Label("Purpose of leave :");
			lblPurposeOfLeave.setImmediate(true);
			lblPurposeOfLeave.setWidth("-1px");
			lblPurposeOfLeave.setHeight("-1px");
			mainLayout.addComponent(lblPurposeOfLeave, "top:95.0px; left:430.0px;");

			// txtPurposeOfLeave
			txtPurposeOfLeave = new TextField();
			txtPurposeOfLeave.setImmediate(true);
			txtPurposeOfLeave.setWidth("220px");
			txtPurposeOfLeave.setHeight("60px");
			mainLayout.addComponent(txtPurposeOfLeave, "top:93.0px; left:530.0px;");

			// lblLeaveAddress
			lblLeaveAddress = new Label("Leave address :");
			lblLeaveAddress.setImmediate(false);
			lblLeaveAddress.setWidth("-1px");
			lblLeaveAddress.setHeight("-1px");
			mainLayout.addComponent(lblLeaveAddress, "top:157.0px; left:430.0px;");

			// txtLeaveAddress
			txtLeaveAddress = new TextField();
			txtLeaveAddress.setImmediate(true);
			txtLeaveAddress.setWidth("220px");
			txtLeaveAddress.setHeight("60px");
			mainLayout.addComponent(txtLeaveAddress, "top:155.0px; left:530.0px;");

			// lblMobileNo
			lblMobileNo = new Label("Mobile No :");
			lblMobileNo.setImmediate(false);
			lblMobileNo.setWidth("-1px");
			lblMobileNo.setHeight("-1px");
			mainLayout.addComponent(lblMobileNo, "top:218.0px; left:430.0px;");

			// txtMobileNo
			txtMobileNo = new TextField();
			txtMobileNo.setImmediate(true);
			txtMobileNo.setWidth("150px");
			txtMobileNo.setHeight("-1px");
			mainLayout.addComponent(txtMobileNo, "top:216.0px; left:530.0px;");

			senctionLayout = builtSenctionLayout();
			senctionLayout.setStyleName("vSenctionLayout");
			mainLayout.addComponent(senctionLayout, "top:345; left:90;");

			// table design
			table.setWidth("598px");
			table.setHeight("150px");
			table.setColumnCollapsingAllowed(true);

			table.addContainerProperty("SL#", Label.class , new Label());
			table.setColumnWidth("SL#",35);

			table.addContainerProperty("Date", Label.class , new Label());
			table.setColumnWidth("Date",130);

			table.addContainerProperty("Date Day", Label.class , new Label());
			table.setColumnWidth("Date Day",110);

			table.addContainerProperty("Type Of Leave", ComboBox.class , new ComboBox());
			table.setColumnWidth("Type Of Leave",152);

			table.addContainerProperty("Enjoyed (Y/N)", CheckBox.class , new CheckBox() ,null,null,Table.ALIGN_CENTER);
			table.setColumnWidth("Enjoyed (Y/N)",85);

			mainLayout.addComponent(table,"top:410.0px;left:90.0px;");
			mainLayout.addComponent(cButton, "top:580.0px; left:135.0px;");
			return mainLayout;
		}

		private AbsoluteLayout builtSenctionLayout()
		{
			senctionLayout = new AbsoluteLayout();
			senctionLayout.setImmediate(true);
			senctionLayout.setWidth("598px");
			senctionLayout.setHeight("65px");
			senctionLayout.setMargin(false);

			// lblSenctionFrom
			lblSenctionFrom = new Label("Senction From :");
			lblSenctionFrom.setImmediate(false);
			lblSenctionFrom.setWidth("-1px");
			lblSenctionFrom.setHeight("-1px");
			senctionLayout.addComponent(lblSenctionFrom, "top:08.0px; left:10.0px;");

			// dSenctionFrom
			dSenctionFrom = new PopupDateField();
			dSenctionFrom.setImmediate(true);
			dSenctionFrom.setWidth("110px");
			dSenctionFrom.setHeight("-1px");
			//dSenctionFrom.setValue(new java.util.Date());
			dSenctionFrom.setDateFormat("dd-MM-yyyy");
			dSenctionFrom.setResolution(PopupDateField.RESOLUTION_DAY);
			senctionLayout.addComponent(dSenctionFrom, "top:06.0px; left:100.0px;");

			// lblSenctionTo
			lblSenctionTo = new Label("Senction To :");
			lblSenctionTo.setImmediate(false);
			lblSenctionTo.setWidth("-1px");
			lblSenctionTo.setHeight("-1px");
			senctionLayout.addComponent(lblSenctionTo, "top:08.0px; left:244.0px;");

			// dSenctionTo
			dSenctionTo = new PopupDateField();
			dSenctionTo.setImmediate(true);
			dSenctionTo.setWidth("110px");
			dSenctionTo.setHeight("-1px");
			//dSenctionTo.setValue(new java.util.Date());
			dSenctionTo.setDateFormat("dd-MM-yyyy");
			dSenctionTo.setResolution(PopupDateField.RESOLUTION_DAY);
			senctionLayout.addComponent(dSenctionTo, "top:06.0px; left:320.0px;");

			// chkApprove
			chkApprove = new CheckBox("Approve");
			chkApprove.setImmediate(true);
			senctionLayout.addComponent(chkApprove, "top:08px; left:440px;");

			// lblDuration
			lblDuration = new Label("Duration :");
			lblDuration.setImmediate(false);
			lblDuration.setWidth("-1px");
			lblDuration.setHeight("-1px");
			senctionLayout.addComponent(lblDuration, "top:35.0px; left:10.0px;");

			// txtDuration
			txtDuration = new TextRead();
			txtDuration.setImmediate(true);
			txtDuration.setWidth("60px");
			txtDuration.setHeight("23px");
			senctionLayout.addComponent(txtDuration, "top:33.0px; left:100.0px;");

			// lblDays
			lblDays = new Label("Days");
			lblDays.setImmediate(false);
			lblDays.setWidth("-1px");
			lblDays.setHeight("-1px");
			senctionLayout.addComponent(lblDays, "top:35.0px; left:165.0px;");

			// lblFriday
			lblFriday = new Label("No of Friday :");
			lblFriday.setImmediate(true);
			lblFriday.setWidth("-1px");
			lblFriday.setHeight("-1px");
			senctionLayout.addComponent(lblFriday, "top:35.0px; left:244.0px;");

			// txtFriday
			txtFriday = new TextRead();
			txtFriday.setImmediate(true);
			txtFriday.setWidth("60px");
			txtFriday.setHeight("23px");
			senctionLayout.addComponent(txtFriday, "top:33.0px; left:320.0px;");

			// btnTransection
			btnTransection = new NativeButton();
			btnTransection.setCaption("Generate Full Transaction");
			btnTransection.setImmediate(true);
			btnTransection.setWidth("-1px");
			btnTransection.setHeight("24px");
			senctionLayout.addComponent(btnTransection, "top:35.0px;left:415.0px;");
			return senctionLayout;
		}


}
