package acc.appform.hrmModule;

	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Iterator;
	import java.util.List;


	import org.hibernate.Session;


	import org.hibernate.Transaction;

	import com.common.share.CommaSeparator;
	import com.common.share.CommonButton;
	import com.common.share.FocusMoveByEnter;
	import com.common.share.MessageBox;
	import com.common.share.SessionBean;
	import com.common.share.SessionFactoryUtil;
	import com.common.share.MessageBox.ButtonType;
	import com.common.share.MessageBox.EventListener;
	import com.common.share.TextRead;
	import com.vaadin.data.Property.ValueChangeEvent;
	import com.vaadin.data.Property.ValueChangeListener;
	import com.vaadin.terminal.ThemeResource;
	import com.vaadin.ui.AbsoluteLayout;
	import com.vaadin.ui.AbstractSelect.Filtering;
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

public class LeaveEncashmentCHO extends Window {

		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private PopupDateField dDate;
		private TextRead txtRefference;
		private ComboBox cmbDepartment;
		private ComboBox cmbSection;
		private CheckBox chkSectionAll;
		private Label lblEmployeeID;
		private ComboBox cmbEmployeeID;
		private CheckBox chkEmployeeAll;
		/*private ComboBox cmbEmployeeName;*/
		private ComboBox cmbLeaveYear;

		private Label lblFullText;

		private Table table;
		private ArrayList<Label> tbLblSl = new ArrayList<Label>();
		private ArrayList<Label> tbLblAutoEmpID = new ArrayList<Label>();
		private ArrayList<Label> tbLblEmployeeID = new ArrayList<Label>();
		private ArrayList<Label> tbLblProximityID = new ArrayList<Label>();
		private ArrayList<Label> tbLblEmployeeName = new ArrayList<Label>();
		private ArrayList<Label> tbLblDesignationID = new ArrayList<Label>();
		private ArrayList<Label> tbLblDesignation = new ArrayList<Label>();
		private ArrayList<Label> tbLblSectionID = new ArrayList<Label>();
		private ArrayList<Label> tbLblSection = new ArrayList<Label>();
		private ArrayList<Label> tbtLblBasic = new ArrayList<Label>();
		private ArrayList<Label> tblblClOpeningTotal = new ArrayList<Label>();
		private ArrayList<Label> tblblClEnjoyed = new ArrayList<Label>();
		private ArrayList<Label> tbLblClBalance = new ArrayList<Label>();
		private ArrayList<Label> tbtxClAllowance = new ArrayList<Label>();
		private ArrayList<Label> tbLblSlOpeningTotal = new ArrayList<Label>();
		private ArrayList<Label> tbLblSlEnjoyed= new ArrayList<Label>();
		private ArrayList<Label> tblblSlBalance = new ArrayList<Label>();
		private ArrayList<Label> tbtxtSlAllowance= new ArrayList<Label>();
		private ArrayList<Label> tbLblElOpeningTotal = new ArrayList<Label>();
		private ArrayList<Label> tbLblElEnjoyed= new ArrayList<Label>();
		private ArrayList<Label> tbLblElBalance = new ArrayList<Label>();
		private ArrayList<Label> tbLblElAllowance = new ArrayList<Label>();
		private ArrayList<Label> tbLblTotalLeave = new ArrayList<Label>();
		private ArrayList<Label> tbLblTotalEnjoyed = new ArrayList<Label>();
		private ArrayList<Label> tbLblTotalBalance = new ArrayList<Label>();
		private ArrayList<Label> tbtxTotalAmount = new ArrayList<Label>();

		private ArrayList<NativeButton>tbBtndel = new ArrayList<NativeButton>();

		private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

		//private SimpleDateFormat dYearFormat = new SimpleDateFormat("yyyy");

		private OptionGroup opgTypeOfSearch;
		private List<String> lst=Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});
		private CommonButton cButton=new CommonButton("New", "Save", "", "", "Refresh", "", "", "", "", "Exit");

		private CommaSeparator commaSeparator=new CommaSeparator();

		int index=0;
		String Notify="";
		private boolean isUpdate=false;
		private ArrayList<Component> allComp = new ArrayList<Component>();

		public LeaveEncashmentCHO(SessionBean sessionBean) 
		{
			this.sessionBean=sessionBean;
			this.setCaption("LEAVE ENCASHMENT CHO:: "+sessionBean.getCompany());
			this.setResizable(false);
			buildlMainLayout();
			setContent(mainLayout);
			tableinitialize();
			componentIni(true);
			btnIni(true);
			addLeaveYear();
			setEventAction();
			focuMove();
		}

		private void componentIni(boolean b)
		{
			dDate.setEnabled(!b);
			txtRefference.setEnabled(!b);
			cmbDepartment.setEnabled(!b);
			cmbSection.setEnabled(!b);
			cmbEmployeeID.setEnabled(!b);
			cmbLeaveYear.setEnabled(!b);
			cmbLeaveYear.setValue(new java.util.Date());
			chkEmployeeAll.setEnabled(!b);
			chkSectionAll.setEnabled(!b);
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
			cmbLeaveYear.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbLeaveYear.getValue() != null) 
					{
						addDepartmentData();
					}
				}
			});

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
					if(cmbSection.getValue() != null)
					{
						cmbAddEmployeeData();
					}
				}
			});

			chkSectionAll.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(chkSectionAll.booleanValue())
					{
						cmbSection.setEnabled(false);
						cmbSection.setValue(null);
						cmbAddEmployeeData();
						
					}
					else
					{
						cmbSection.setEnabled(true);
					}
				}
			});

			cmbEmployeeID.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{

					if(cmbEmployeeID.getValue() != null)
					{
						tableValueAdd();
						
					}
				}
			});

			opgTypeOfSearch.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(opgTypeOfSearch.getValue().toString().trim().equals("Employee Name"))
					{
						lblEmployeeID.setValue("Employee Name : ");
						cmbEmployeeID.setWidth("250.0px");
						cmbAddEmployeeData();
					}

					else if(opgTypeOfSearch.getValue().toString().trim().equals("Employee ID"))
					{
						lblEmployeeID.setValue("Employee ID : ");
						cmbEmployeeID.setWidth("200.0px");
						cmbAddEmployeeData();
					}
				}
			});

			chkEmployeeAll.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(chkEmployeeAll.booleanValue() && cmbLeaveYear.getValue()!=null && cmbDepartment.getValue()!=null && (cmbSection.getValue()!=null || chkSectionAll.booleanValue()))
					{
						cmbEmployeeID.setEnabled(false);
						cmbEmployeeID.setValue(null);
						tableClear();
						tableValueAdd();
					}
					else
					{
						cmbEmployeeID.setEnabled(true);
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
					if(cmbEmployeeID.getValue()!=null||chkEmployeeAll.booleanValue())
					{
						if(cmbSection.getValue()!=null||chkSectionAll.booleanValue())
						{
							if(cmbDepartment.getValue()!=null)
							{
								if(cmbLeaveYear.getValue()!=null)
								{
									saveButtonEvent();
								}

								else
								{
									showNotification("Warning !","Please select  Leave Year",Notification.TYPE_WARNING_MESSAGE);	
								}
							}
							else
							{
								showNotification("Warning !","Please select Deaprtment",Notification.TYPE_WARNING_MESSAGE);	
							}
						}
						else
						{
							showNotification("Warning !","Please select Section",Notification.TYPE_WARNING_MESSAGE);	
						}

					}
					else
					{
						showNotification("Warning !","Please select Employee",Notification.TYPE_WARNING_MESSAGE);	
					}

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
			/*cButton.btnFind.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					findbuttonEvent();
				}
			});*/
			cButton.btnExit.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					close();
				}
			});
		}

		public String RefferenceID()
		{
			String ret = "";
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String sql = "SELECT ISNULL((MAX(CAST(SUBSTRING(vTransactionID,3,50) AS INT))+1),1)  FROM tbLeaveEncashment";
				Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
				if(iter.hasNext())
				{
					ret = "E-"+iter.next().toString();
				}
			}
			catch(Exception ex)
			{
				showNotification("RefferenceID",ex+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			return ret;
		}

		private void addLeaveYear()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct Year(DATEADD(YEAR,-1,GETDATE())),Year(DATEADD(YEAR,-1,GETDATE())) from tbLeaveBalanceNew";
				List <?> lst=session.createSQLQuery(query).list();
				if(!lst.isEmpty())
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object[] element=(Object[])itr.next();
						cmbLeaveYear.addItem(element[0]);
						cmbLeaveYear.setItemCaption(element[0], element[1].toString());
					}
				}
			}
			catch (Exception exp)
			{
				showNotification("addLeaveYear", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void addDepartmentData()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct vDepartmentID,(select vDepartmentName from tbDepartmentInfo di where "
						+ "di.vDepartmentId = ln.vDepartmentID )Departmentname from tbLeaveBalanceNew ln where YEAR(ln.currentYear) = '"+cmbLeaveYear.getValue() +"' and vDepartmentID='DEPT10' order by vDepartmentID";

				System.out.println("Department query :" + query);
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
			catch (Exception exp)
			{
				showNotification("addDepartmentData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void addSectionData()
		{
			cmbSection.removeAllItems();
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct vSectionId,(select SectionName from tbSectionInfo si where si.vSectionID=ln.vSectionId)SectionName"
						+ " from tbLeaveBalanceNew ln where ln.vDepartmentID = '"+cmbDepartment.getValue()+"'  order by vSectionId";
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
			catch (Exception exp)
			{
				showNotification("addSectionData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void cmbAddEmployeeData()
		{
			cmbEmployeeID.removeAllItems();
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "";
				if(opgTypeOfSearch.getValue().toString().equals("Employee Name"))
				{
					query = "select vEmployeeId,vEmployeeName from tbEmployeeInfo where"
							+ " vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and vEmployeeType = 'Permanent' and iStatus = '1' and vDepartmentID ='"+cmbDepartment.getValue()+"'";
				}

				if(opgTypeOfSearch.getValue().toString().equals("Employee ID"))
				{
					query = "select vEmployeeId,employeeCode from tbEmployeeInfo where"
							+ " vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and vEmployeeType = 'Permanent' and iStatus = '1' and vDepartmentID ='"+cmbDepartment.getValue()+"'";
				}

				if(opgTypeOfSearch.getValue().toString().equals("Proximity ID"))
				{
					query = "select vEmployeeId,vProximityId from tbEmployeeInfo where"
							+ " vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and vEmployeeType = 'Permanent' and iStatus = '1' and vDepartmentID ='"+cmbDepartment.getValue()+"'";
				}

				List <?> lst=session.createSQLQuery(query).list();

				System.out.println("Employee add data " + query);
				if(!lst.isEmpty())
				{
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object[] element=(Object[])itr.next();
						cmbEmployeeID.addItem(element[0]);
						cmbEmployeeID.setItemCaption(element[0], element[1].toString());
					}
				}
			}
			catch (Exception exp)
			{
				showNotification("addEmployeeData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		/*private void tableValueAdd()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select distinct ln.vAutoEmployeeId,ln.vEmployeeId,(select vEmployeeName from tbEmployeeInfo ei where ei.vEmployeeId = ln.vAutoEmployeeId)EmployeeName,"
						+ "s.designation,ln.vSectionId,(select SectionName from tbSectionInfo si where si.vSectionID = ln.vSectionID)SectionName,(select basicSalary from tbSalary where autoEmployeeID='"+cmbEmployeeID.getValue()+"' and year ='"+cmbLeaveYear.getValue()+"' and vMonthName='December')Basic,ln.iClOpening,ln.iClEnjoyed,"
						+ "(ln.iClOpening-iClEnjoyed)RemainingCLbalane,((select basicSalary from tbSalary where autoEmployeeID='"+cmbEmployeeID.getValue()+"' and year ='"+cmbLeaveYear.getValue()+"' and vMonthName='December')/26 * (ln.iClOpening-iClEnjoyed))CLAllowance,"
						+ "ln.iSlOpening,ln.iSlEnjoyed,(ln.iSlOpening-iSlEnjoyed)RemainingSLbalane,((select basicSalary from tbSalary where autoEmployeeID='"+cmbEmployeeID.getValue()+"' and year ='"+cmbLeaveYear.getValue()+"' and vMonthName='December')/26 * (ln.iSlOpening-iSlEnjoyed))SLAllowance,ln.iAlOpening,ln.iAlEnjoyed,(ln.iAlOpening-iAlEnjoyed)RemainingALbalane,"
						+ "((select basicSalary from tbSalary where autoEmployeeID='"+cmbEmployeeID.getValue()+"' and year ='"+cmbLeaveYear.getValue()+"' and vMonthName='December')/26 * (ln.iAlOpening-iAlEnjoyed))ALAllowance,(ln.iClOpening+ln.iSlOpening+ln.iAlOpening)as TotalLeave,"
						+ "(ln.iClEnjoyed+ln.iSlEnjoyed+ln.iAlEnjoyed)as TotalEnjoyed,((ln.iClOpening+ln.iSlOpening+ln.iAlOpening)-(ln.iClEnjoyed+ln.iSlEnjoyed+ln.iAlEnjoyed))Balance,"
						+ " round(((select basicSalary from tbSalary where autoEmployeeID='"+cmbEmployeeID.getValue()+"' and year ='"+cmbLeaveYear.getValue()+"' and vMonthName='December')/26 * (ln.iClOpening-iClEnjoyed)+"
						+ "(select basicSalary from tbSalary where autoEmployeeID='"+cmbEmployeeID.getValue()+"' and year ='"+cmbLeaveYear.getValue()+"' and vMonthName='December')/26 * (ln.iSlOpening-iSlEnjoyed)+"
						+ "(select basicSalary from tbSalary where autoEmployeeID='"+cmbEmployeeID.getValue()+"' and year ='"+cmbLeaveYear.getValue()+"' and vMonthName='December')/26 * (ln.iAlOpening-iAlEnjoyed)),0)TotalAllowance,ln.vProximityId"
						+ " from tbLeaveBalanceNew ln inner join tbSalary s on s.autoEmployeeID=ln.vAutoEmployeeId where Year(ln.currentYear) = '"+cmbLeaveYear.getValue()+"' and ln.vDepartmentID = '"+cmbDepartment.getValue()+"' "
						+ "and ln.vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' and ln.vAutoEmployeeId like '"+(cmbEmployeeID.getValue()!=null?cmbEmployeeID.getValue().toString():"%")+"' order by ln.vSectionId,ln.vAutoEmployeeId";

				System.out.println("Table value add " + query);
				List <?> lst=session.createSQLQuery(query).list();
				if(!lst.isEmpty())
				{
					int ind=0;
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						tbLblAutoEmpID.get(ind).setValue(element[0]);
						tbLblEmployeeID.get(ind).setValue(element[1]);
						tbLblEmployeeName.get(ind).setValue(element[2]);
						tbLblDesignation.get(ind).setValue(element[3]);
						tbLblSectionID.get(ind).setValue(element[4]);
						tbLblSection.get(ind).setValue(element[5]);
						tbtLblBasic.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[6].toString())));
						tblblClOpeningTotal.get(ind).setValue(element[7]);
						tblblClEnjoyed.get(ind).setValue(element[8]);
						tbLblClBalance.get(ind).setValue(element[9]);
						tbtxClAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[10].toString())));
						tbLblSlOpeningTotal.get(ind).setValue(element[11]);
						tbLblSlEnjoyed.get(ind).setValue(element[12]);
						tblblSlBalance.get(ind).setValue(element[13]);
						tbtxtSlAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[14].toString())));
						tbLblElOpeningTotal.get(ind).setValue(element[15]);
						tbLblElEnjoyed.get(ind).setValue(element[16]);
						tbLblElBalance.get(ind).setValue(element[17]);
						tbLblElAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[18].toString())));
						tbLblTotalLeave.get(ind).setValue(element[19]);
						tbLblTotalEnjoyed.get(ind).setValue(element[20]);
						tbLblTotalBalance.get(ind).setValue(element[21]);
						tbtxTotalAmount.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[22].toString())));
						tbLblProximityID.get(ind).setValue(element[23]);

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
		}*/

		private void tableValueAdd()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String check = "SELECT * from tbLeaveEncashment where YEAR(dLeaveYear)=('"+cmbLeaveYear.getValue()+"') " +
						"and vDepartmentID like  '"+cmbDepartment.getValue()+"' " +
						"and vSectionID like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"' " +
						"and vEmployeeID like '"+(cmbEmployeeID.getValue()!=null?cmbEmployeeID.getValue().toString():"%" )+"'";
								
				List <?> checkList = session.createSQLQuery(check).list();

				if(checkList.isEmpty())
				{

					String query="select distinct ln.vAutoEmployeeId,ln.vEmployeeId,(select vEmployeeName from tbEmployeeInfo ei where ei.vEmployeeId = ln.vAutoEmployeeId)EmployeeName," +
							"(select designationName from tbDesignationInfo where designationId=(select distinct vDesignationId from tbEmployeeInfo where vEmployeeId=ln.vAutoEmployeeId))designation,"
							+ "ln.vSectionId,(select SectionName from tbSectionInfo si where" +
							" si.vSectionID = ln.vSectionID)SectionName,(select basicSalary from tbSalary  " +
							"where autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"' and dDate=(select MAX(dDate) from tbSalary" +
							"  where autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"'))basic, " +
							"Round (((select basicSalary from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"' " +
							"and dDate= (select MAX(dDate) from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId " +
							" and year='"+cmbLeaveYear.getValue()+"')))/26 *((ln.iClOpening-iClEnjoyed)),0) CLAllowance,ln.iClOpening," +
							"ln.iClEnjoyed,(ln.iClOpening-iClEnjoyed)RemainingCLbalane," +
							"Round(((select basicSalary from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"' " +
							"and dDate= (select MAX(dDate) from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId" +
							" and year='"+cmbLeaveYear.getValue()+"')))/26 *((ln.iSlOpening-iSlEnjoyed)),0) SLAllowance,ln.iSlOpening," +
							"ln.iSlEnjoyed,(ln.iSlOpening-iSlEnjoyed)RemainingSLbalane," +
							"Round(((select basicSalary from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"' " +
							" and dDate= (select MAX(dDate) from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId and" +
							" year='"+cmbLeaveYear.getValue()+"')))/26 *((ln.iAlOpening-iAlEnjoyed)),0) ALAllowance,ln.iAlOpening,ln.iAlEnjoyed," +
							"(ln.iAlOpening-iAlEnjoyed)RemainingALbalane," +
							"ROUND((((select basicSalary from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId " +
							" and year='"+cmbLeaveYear.getValue()+"' and dDate= (select MAX(dDate) from tbSalary  where" +
							" autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"')))/26 *((ln.iClOpening-iClEnjoyed))" +
							"+((select basicSalary from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"'" +
							" and dDate= (select MAX(dDate) from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId and" +
							" year='"+cmbLeaveYear.getValue()+"')))/26 *((ln.iSlOpening-iSlEnjoyed))+((select basicSalary from tbSalary " +
							" where autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"' and dDate= (select MAX(dDate) from" +
							" tbSalary  where autoEmployeeID=ln.vAutoEmployeeId and year='"+cmbLeaveYear.getValue()+"')))/26" +
							" *((ln.iAlOpening-iAlEnjoyed))),0)TotalAllowance,(ln.iClOpening+ln.iSlOpening+ln.iAlOpening)as TotalLeave," +
							"(ln.iClEnjoyed+ln.iSlEnjoyed+ln.iAlEnjoyed)as TotalEnjoyed," +
							"((ln.iClOpening+ln.iSlOpening+ln.iAlOpening)-(ln.iClEnjoyed+ln.iSlEnjoyed+ln.iAlEnjoyed))" +
							"Balance,ln.vProximityId from tbLeaveBalanceNew ln " +
							"inner join tbSalary s on s.autoEmployeeID=ln.vAutoEmployeeId " +
							"inner join tbEmployeeinfo em on em.vEmployeeId=ln.vAutoEmployeeId where Year(ln.currentYear) = '"+cmbLeaveYear.getValue()+"' " +
							" and ln.vDepartmentID = '"+cmbDepartment.getValue()+"' and em.vSectionId like '"+(cmbSection.getValue()!=null?cmbSection.getValue().toString():"%")+"'and ln.vAutoEmployeeId " +
							"like '"+(cmbEmployeeID.getValue()!=null?cmbEmployeeID.getValue().toString():"%")+"' and em.iStatus=1 order by ln.vSectionId,ln.vAutoEmployeeId";

					
					//select basicSalary from tbSalary  where autoEmployeeID=ln.vAutoEmployeeId and year='2016' and dDate= (select MAX(dDate) from tbSalary  where autoEmployeeID=autoEmployeeID=ln.vAutoEmployeeId and year='2016')
					List <?> lst=session.createSQLQuery(query).list();
					if(!lst.isEmpty())
					{

						if(cmbEmployeeID.getValue()!=null)
						{  int ind=index();
						if(doubleentrycheck())
						{
							Iterator <?> itr=lst.iterator();
							if(itr.hasNext())
							{
								Object [] element=(Object[])itr.next();
								tbLblAutoEmpID.get(ind).setValue(element[0]);
								tbLblEmployeeID.get(ind).setValue(element[1]);
								tbLblEmployeeName.get(ind).setValue(element[2]);
								tbLblDesignation.get(ind).setValue(element[3]);
								tbLblSectionID.get(ind).setValue(element[4]);
								tbLblSection.get(ind).setValue(element[5]);
								tbtLblBasic.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[6].toString())));
								tblblClOpeningTotal.get(ind).setValue(element[8]);
								tblblClEnjoyed.get(ind).setValue(element[9]);
								tbLblClBalance.get(ind).setValue(element[10]);
								tbtxClAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[7].toString())));
								tbLblSlOpeningTotal.get(ind).setValue(element[12]);
								tbLblSlEnjoyed.get(ind).setValue(element[13]);
								tblblSlBalance.get(ind).setValue(element[14]);
								tbtxtSlAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[11].toString())));
								tbLblElOpeningTotal.get(ind).setValue(element[16]);
								tbLblElEnjoyed.get(ind).setValue(element[17]);
								tbLblElBalance.get(ind).setValue(element[18]);
								tbLblElAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[15].toString())));
								tbLblTotalLeave.get(ind).setValue(element[20]);
								tbLblTotalEnjoyed.get(ind).setValue(element[21]);
								tbLblTotalBalance.get(ind).setValue(element[22]);
								tbtxTotalAmount.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[19].toString())));
								tbLblProximityID.get(ind).setValue(element[23]);

								if(ind==tbLblAutoEmpID.size()-1)
								{
									tableRowAdd(ind+1);
								}

								ind++;
							}	 
						}
						else
						{
							showNotification("Warning", "Double Entry Is Not Allowed!!!", Notification.TYPE_WARNING_MESSAGE);	 
						}

						}

						else if(chkEmployeeAll.booleanValue())
						{
							int ind=0;
							for(Iterator <?> itr=lst.iterator();itr.hasNext();)
							{
								Object [] element=(Object[])itr.next();
								tbLblAutoEmpID.get(ind).setValue(element[0]);
								tbLblEmployeeID.get(ind).setValue(element[1]);
								tbLblEmployeeName.get(ind).setValue(element[2]);
								tbLblDesignation.get(ind).setValue(element[3]);
								tbLblSectionID.get(ind).setValue(element[4]);
								tbLblSection.get(ind).setValue(element[5]);
								tbtLblBasic.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[6].toString())));
								tblblClOpeningTotal.get(ind).setValue(element[8]);
								tblblClEnjoyed.get(ind).setValue(element[9]);
								tbLblClBalance.get(ind).setValue(element[10]);
								tbtxClAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[7].toString())));
								tbLblSlOpeningTotal.get(ind).setValue(element[12]);
								tbLblSlEnjoyed.get(ind).setValue(element[13]);
								tblblSlBalance.get(ind).setValue(element[14]);
								tbtxtSlAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[11].toString())));
								tbLblElOpeningTotal.get(ind).setValue(element[16]);
								tbLblElEnjoyed.get(ind).setValue(element[17]);
								tbLblElBalance.get(ind).setValue(element[18]);
								tbLblElAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[15].toString())));
								tbLblTotalLeave.get(ind).setValue(element[20]);
								tbLblTotalEnjoyed.get(ind).setValue(element[21]);
								tbLblTotalBalance.get(ind).setValue(element[22]);
								tbtxTotalAmount.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[19].toString())));
								tbLblProximityID.get(ind).setValue(element[23]);



								if(ind==tbLblAutoEmpID.size()-1)
								{
									tableRowAdd(ind+1);
								}

								ind++;
							}	
						}

					}

					else
					{
						showNotification("Warning", "No Employee Found or Data already exists!!!", Notification.TYPE_WARNING_MESSAGE);
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
			//tableComponentIni();
		}

		private int index()
		{
			for(int i=0;i<tbLblAutoEmpID.size();i++)
			{
				if (tbLblAutoEmpID.get(i).getValue().toString().isEmpty())
				{
					return i;	
				}
			}
			return 0;
		}

		private boolean doubleentrycheck()
		{
			for(int i=0;i<tbLblAutoEmpID.size();i++)
			{
				if (tbLblAutoEmpID.get(i).getValue().toString().equalsIgnoreCase(cmbEmployeeID.getValue().toString()))
				{
					return false;	
				}
			}
			return true;
		}


		private void focuMove()
		{
			allComp.add(cmbDepartment);
			allComp.add(cmbSection);
			allComp.add(cButton.btnSave);
			new FocusMoveByEnter(this, allComp);
		}

		private void txtClear()
		{
			dDate.setValue(new java.util.Date());
			txtRefference.setValue("");
			cmbLeaveYear.setValue(null);
			cmbDepartment.setValue(null);
			cmbSection.setValue(null);
			chkSectionAll.setValue(false);
			cmbEmployeeID.setValue(null);
			chkEmployeeAll.setValue(false);
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
					tbLblSection.get(ind).setValue("");
					tbLblDesignation.get(ind).setValue("");
					tbtLblBasic.get(ind).setValue("");
					tblblClOpeningTotal.get(ind).setValue("");
					tblblClEnjoyed.get(ind).setValue("");
					tbLblClBalance.get(ind).setValue("");
					tbtxClAllowance.get(ind).setValue("");
					tbLblSlOpeningTotal.get(ind).setValue("");
					tbLblSlEnjoyed.get(ind).setValue("");
					tblblSlBalance.get(ind).setValue("");
					tbtxtSlAllowance.get(ind).setValue("");
					tbLblElOpeningTotal.get(ind).setValue("");
					tbLblElEnjoyed.get(ind).setValue("");
					tbLblElBalance.get(ind).setValue("");
					tbLblElAllowance.get(ind).setValue("");
					tbLblTotalLeave.get(ind).setValue("");
					tbLblTotalEnjoyed.get(ind).setValue("");
					tbLblTotalBalance.get(ind).setValue("");
					tbtxTotalAmount.get(ind).setValue("");
				}
			}
		}

		private void newBtnEvent()
		{
			isUpdate = false;
			txtClear();
			componentIni(false);
			btnIni(false);
			txtRefference.setValue(RefferenceID());
		}

		private void updateBntEvent()
		{
			isUpdate = true;
			componentIni(true);
			btnIni(false);
			table.setEnabled(true);
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
			for(int i=0;i<50;i++)
				tableRowAdd(i);
		}

		/*private boolean existDataCheck(String query)
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
		}*/

		private void saveButtonEvent()
		{
			if(isUpdate)
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
								/*deleteData(session);*/
								insertData();
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
				isUpdate=false;
			}

			else
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
								insertData();
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
				/*}
				else
				{
					showNotification("Warning", "Data Already Exist!!!", Notification.TYPE_WARNING_MESSAGE);
				}*/
			}
		}

		/*private void deleteData(Session session)
		{
			String Updatequery = "insert into tbUDSalaryIncrement (cmbLeaveYear,vEmployeeId,employeeCode,vProximityId,vEmployeeName,"
					+ " vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationId,vDesignationName,vEmployeeType,"
					+ " mBasic,mHouseRent,mConveyance,mMedicalAllowance,mGross,vIncrementPercentage,mIncrementAmount,mNewBasic,"
					+ " mNewHouseRent,mNewConveyance,mNewMedicalAllowance,mNewGross,iApproved_FLag,vUDFlag,vUserName,vUserIP,"
					+ " dEntryTime) select cmbLeaveYear,vEmployeeId,employeeCode,vProximityId,vEmployeeName,vDepartmentID,vDepartmentName,"
					+ " vSectionId,vSectionName,vDesignationId,vDesignationName,vEmployeeType,mBasic,mHouseRent,mConveyance,"
					+ " mMedicalAllowance,mGross,vIncrementPercentage,mIncrementAmount,mNewBasic,mNewHouseRent,mNewConveyance,"
					+ " mNewMedicalAllowance,mNewGross,iApproved_FLag,'OLD',vUserName,vUserIP,dEntryTime from tbSalaryIncrement "
					+ " where MONTH(cmbLeaveYear)=MONTH('"+dFormat.format(cmbLeaveYear.getValue())+"') and "
					+ " YEAR(cmbLeaveYear)=YEAR('"+dFormat.format(cmbLeaveYear.getValue())+"') and vSectionID='"+cmbSection.getValue()+"'" ;
			session.createSQLQuery(Updatequery).executeUpdate();

			session.createSQLQuery("delete from tbSalaryIncrement where Month(cmbLeaveYear)=MONTH('"+dFormat.format(cmbLeaveYear.getValue())+"')"
					+ " and YEAR(cmbLeaveYear)=YEAR('"+dFormat.format(cmbLeaveYear.getValue())+"') and vSectionID='"+cmbSection.getValue()+"'").executeUpdate();
		}*/

		private void insertData()
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			Transaction tx=session.beginTransaction();
			try
			{
				System.out.println("Try query");
				for(int i=0; i<tbLblEmployeeID.size();i++)
				{
					if(!tbLblEmployeeID.get(i).getValue().toString().isEmpty())
					{
						String InsertQuery = "insert into tbLeaveEncashment (dDate,vTransactionID,dLeaveYear,vDepartmentID,vDepartmentName,"
								+ "vSectionID,vSectionName,vEmployeeID,vProximityID,vEmployeeCode,vEmployeeName,vDesignation,mBasic,"
								+ "iClTotalDays,iClEnjoyedDays,iClBalance,mClAllowance,iSlTotalDays,iSlEnjoyedDays,iSlBalance,mSlAllowance,"
								+ "iElTotalDays,iElEnjoyedDays,iElBalance,mElAllowance,iTotalLeave,iTotalEnjoyed,iTotalBalance,mTotalAmount,"
								+ "vUserName,vUserIP,dEntryTime)"
								+ "values('"+dFormat.format(dDate.getValue())+"',"
								+ "'"+txtRefference.getValue().toString()+"',"
								+ "'"+cmbLeaveYear.getValue()+"',"
								+ "'"+cmbDepartment.getValue().toString()+"',"
								+ "'"+cmbDepartment.getItemCaption(cmbDepartment.getValue().toString())+"',"
								+ "'"+tbLblSectionID.get(i).getValue()+"',"
								+ "'"+tbLblSection.get(i).getValue()+"',"
								+ "'"+tbLblAutoEmpID.get(i).getValue()+"',"
								+ "'"+tbLblProximityID.get(i).getValue()+"',"
								+ "'"+tbLblEmployeeID.get(i).getValue()+"',"
								+ "'"+tbLblEmployeeName.get(i).getValue()+"',"
								+ "'"+tbLblDesignation.get(i).getValue()+"',"
								+ "'"+tbtLblBasic.get(i).getValue()+"',"
								+ "'"+tblblClOpeningTotal.get(i).getValue()+"',"
								+ "'"+tblblClEnjoyed.get(i).getValue()+"',"
								+ "'"+tbLblClBalance.get(i).getValue()+"',"
								+ "'"+tbtxClAllowance.get(i).getValue()+"',"
								+ "'"+tbLblSlOpeningTotal.get(i).getValue()+"',"
								+ "'"+tbLblSlEnjoyed.get(i).getValue()+"',"
								+ "'"+tblblSlBalance.get(i).getValue()+"',"
								+ "'"+tbtxtSlAllowance.get(i).getValue()+"',"
								+ "'"+tbLblElOpeningTotal.get(i).getValue()+"',"
								+ "'"+tbLblElEnjoyed.get(i).getValue()+"',"
								+ "'"+tbLblElBalance.get(i).getValue()+"',"
								+ "'"+tbLblElAllowance.get(i).getValue()+"',"
								+ "'"+tbLblTotalLeave.get(i).getValue()+"',"
								+ "'"+tbLblTotalEnjoyed.get(i).getValue()+"',"
								+ "'"+tbLblTotalBalance.get(i).getValue()+"',"
								+ "'"+tbtxTotalAmount.get(i).getValue()+"',"
								+ "'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";

						session.createSQLQuery(InsertQuery).executeUpdate();
						System.out.println("Insert query :" +InsertQuery);

						String tbLeaveBalanceNew = "update tbLeaveBalanceNew set vEncashment = 'Encashed' where Year(currentYear)= '"+cmbLeaveYear.getValue()+"' and vAutoEmployeeId = '"+tbLblAutoEmpID.get(i).getValue().toString()+"'";

						session.createSQLQuery(tbLeaveBalanceNew).executeUpdate();
					}
				}
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

		/*private void findbuttonEvent()
		{
			Window win = new IncrementProcessFind(sessionBean,IncrementDate,SectionID);
			win.addListener(new CloseListener()
			{
				public void windowClose(CloseEvent e)
				{
					if(!IncrementDate.getValue().toString().trim().isEmpty() && !SectionID.getValue().toString().trim().isEmpty())
					{
						txtClear();
						findInitialize(IncrementDate.getValue().toString().trim(), SectionID.getValue().toString().trim());
					}
				}
			});
			this.getParent().addWindow(win);
		}

		private void findInitialize(String incDate, String Section)
		{
			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select sinf.cmbLeaveYear,sinf.vEmployeeId,sinf.employeeCode,sinf.vProximityId,sinf.vEmployeeName,sinf.vDepartmentId,"
						+ " sinf.vSectionId,sinf.vDesignationId,sinf.vDesignationName,sinf.vEmployeeType,sinf.mBasic,sinf.mHouseRent,"
						+ " sinf.mConveyance,sinf.mMedicalAllowance,sinf.mGross,sinf.vIncrementPercentage,ein.dInterviewDate,"
						+ " DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(cmbLeaveYear.getValue())+"')/365 jdYear,DATEDIFF(dd,ein.dInterviewDate,'"+dFormat.format(cmbLeaveYear.getValue())+"')%365/30 jdMonth"
						+ " from tbSalaryIncrement sinf inner join tbEmployeeInfo ein on "
						+ " sinf.vEmployeeId = ein.vEmployeeId where sinf.vSectionId='"+Section+"' and MONTH(cmbLeaveYear) = MONTH('"+incDate+"') and YEAR(cmbLeaveYear) = YEAR('"+incDate+"')";

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
							cmbLeaveYear.setValue(element[0]);
							tableClear();
						}
						tbLblAutoEmpID.get(ind).setValue(element[1]);
						tbLblEmployeeID.get(ind).setValue(element[2]);
						tbLblEmployeeName.get(ind).setValue(element[4]);
						tbLblDesignationID.get(ind).setValue(element[7]);
						tbLblDesignation.get(ind).setValue(element[8]);
						tbtLblBasic.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[10].toString())));
						tbLblClBalance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[11].toString())));
						tbtxClAllowance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[12].toString())));
						tbLblSlOpeningTotal.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[13].toString())));
						tbLblSlEnjoyed.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[14].toString())));
						tblblSlBalance.get(ind).setValue(commaSeparator.setComma(Double.parseDouble(element[15].toString())));
						tblblClOpeningTotal.get(ind).setValue(dateFormat.format(element[16]));
						tblblClEnjoyed.get(ind).setValue(element[17].toString()+"y "+element[18].toString()+"m");

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
		}
		 */
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

			tbLblSectionID.add(ar, new Label());
			tbLblSectionID.get(ar).setWidth("100%");

			tbLblSection.add(ar, new Label());
			tbLblSection.get(ar).setWidth("100%");

			tbtLblBasic.add(ar, new Label());
			tbtLblBasic.get(ar).setWidth("100%");
			tbtLblBasic.get(ar).setImmediate(true);

			tblblClOpeningTotal.add(ar, new Label());
			tblblClOpeningTotal.get(ar).setWidth("100%");

			tblblClEnjoyed.add(ar, new Label());
			tblblClEnjoyed.get(ar).setWidth("100%");

			tbLblClBalance.add(ar, new Label());
			tbLblClBalance.get(ar).setWidth("100%");
			tbLblClBalance.get(ar).setImmediate(true);

			tbtxClAllowance.add(ar, new Label());
			tbtxClAllowance.get(ar).setWidth("100%");
			tbtxClAllowance.get(ar).setImmediate(true);

			tbLblSlOpeningTotal.add(ar, new Label());
			tbLblSlOpeningTotal.get(ar).setWidth("100%");
			tbLblSlOpeningTotal.get(ar).setImmediate(true);

			tbLblSlEnjoyed.add(ar, new Label());
			tbLblSlEnjoyed.get(ar).setWidth("100%");

			tbtxtSlAllowance.add(ar, new Label());
			tbtxtSlAllowance.get(ar).setWidth("100%");
			tbtxtSlAllowance.get(ar).setImmediate(true);

			tblblSlBalance.add(ar, new Label());
			tblblSlBalance.get(ar).setWidth("100%");
			tblblSlBalance.get(ar).setImmediate(true);

			tbLblElEnjoyed.add(ar, new Label());
			tbLblElEnjoyed.get(ar).setWidth("100%");
			tbLblElEnjoyed.get(ar).setImmediate(true);

			tbLblElOpeningTotal.add(ar, new Label(""));
			tbLblElOpeningTotal.get(ar).setWidth("100%");
			tbLblElOpeningTotal.get(ar).setImmediate(true);

			tbLblElBalance.add(ar, new Label());
			tbLblElBalance.get(ar).setWidth("100%");
			tbLblElBalance.get(ar).setImmediate(true);

			tbLblElAllowance.add(ar, new Label());
			tbLblElAllowance.get(ar).setWidth("100%");
			tbLblElAllowance.get(ar).setImmediate(true);

			tbLblTotalLeave.add(ar, new Label());
			tbLblTotalLeave.get(ar).setWidth("100%");
			tbLblTotalLeave.get(ar).setImmediate(true);

			tbLblTotalEnjoyed.add(ar, new Label());
			tbLblTotalEnjoyed.get(ar).setWidth("100%");
			tbLblTotalEnjoyed.get(ar).setImmediate(true);

			tbLblTotalBalance.add(ar, new Label());
			tbLblTotalBalance.get(ar).setWidth("100%");
			tbLblTotalBalance.get(ar).setImmediate(true);

			tbtxTotalAmount.add(ar, new Label());
			tbtxTotalAmount.get(ar).setWidth("100%");
			tbtxTotalAmount.get(ar).setImmediate(true);

			tbBtndel.add(ar, new NativeButton());
			tbBtndel.get(ar).setWidth("100%");
			tbBtndel.get(ar).setImmediate(true);
			tbBtndel.get(ar).setIcon(new ThemeResource("../icons/cancel.png"));
			tbBtndel.get(ar).addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					tbLblAutoEmpID.get(ar).setValue("");
					tbLblProximityID.get(ar).setValue("");
					tbLblEmployeeID.get(ar).setValue("");
					tbLblEmployeeName.get(ar).setValue("");
					tbLblDesignationID.get(ar).setValue("");
					tbLblDesignation.get(ar).setValue("");
					tbLblSectionID.get(ar).setValue("");
					tbLblSection.get(ar).setValue("");
					tbtLblBasic.get(ar).setValue("");
					tblblClOpeningTotal.get(ar).setValue("");
					tblblClEnjoyed.get(ar).setValue("");
					tbLblClBalance.get(ar).setValue("");
					tbtxClAllowance.get(ar).setValue("");
					tbLblSlOpeningTotal.get(ar).setValue("");
					tbLblSlEnjoyed.get(ar).setValue("");
					tblblSlBalance.get(ar).setValue("");
					tbtxtSlAllowance.get(ar).setValue("");
					tbLblElOpeningTotal.get(ar).setValue("");
					tbLblElEnjoyed.get(ar).setValue("");
					tbLblElBalance.get(ar).setValue("");
					tbLblElAllowance.get(ar).setValue("");
					tbLblTotalLeave.get(ar).setValue("");
					tbLblTotalEnjoyed.get(ar).setValue("");
					tbLblTotalBalance.get(ar).setValue("");
					tbtxTotalAmount.get(ar).setValue("");

					for(int ind = ar; ind < tbLblAutoEmpID.size()-1; ind++)
					{
						tbLblAutoEmpID.get(ind).setValue(tbLblAutoEmpID.get(ind+1).getValue());
						tbLblProximityID.get(ind).setValue(tbLblAutoEmpID.get(ind+1).getValue());
						tbLblEmployeeID.get(ind).setValue(tbLblEmployeeID.get(ind+1).getValue());
						tbLblEmployeeName.get(ind).setValue(tbLblEmployeeName.get(ind+1).getValue());
						tbLblDesignationID.get(ind).setValue(tbLblDesignationID.get(ind+1).getValue());
						tbLblDesignation.get(ind).setValue(tbLblDesignation.get(ind+1).getValue());
						tbLblSectionID.get(ind).setValue(tbLblDesignationID.get(ind+1).getValue());
						tbLblSection.get(ind).setValue(tbLblDesignation.get(ind+1).getValue());
						tbtLblBasic.get(ind).setValue(tbtLblBasic.get(ind+1).getValue());
						tblblClOpeningTotal.get(ind).setValue(tblblClOpeningTotal.get(ind+1).getValue());
						tblblClEnjoyed.get(ind).setValue(tblblClEnjoyed.get(ind+1).getValue());
						tbLblClBalance.get(ind).setValue(tbLblClBalance.get(ind+1).getValue());
						tbtxClAllowance.get(ind).setValue(tbtxClAllowance.get(ind+1).getValue());
						tbLblSlOpeningTotal.get(ind).setValue(tbLblSlOpeningTotal.get(ind+1).getValue());
						tbLblSlEnjoyed.get(ind).setValue(tbLblSlEnjoyed.get(ind+1).getValue());
						tblblSlBalance.get(ind).setValue(tblblSlBalance.get(ind+1).getValue());
						tbtxtSlAllowance.get(ind).setValue(tbtxtSlAllowance.get(ind+1).getValue());
						tbLblElOpeningTotal.get(ind).setValue(tbLblElOpeningTotal.get(ind+1).getValue());
						tbLblElEnjoyed.get(ind).setValue(tbLblElEnjoyed.get(ind+1).getValue());
						tbLblElBalance.get(ind).setValue(tbLblElBalance.get(ind+1).getValue());
						tbLblElAllowance.get(ind).setValue(tbLblElAllowance.get(ind+1).getValue());
						tbLblTotalLeave.get(ind).setValue(tbLblTotalLeave.get(ind+1).getValue());
						tbLblTotalEnjoyed.get(ind).setValue(tbLblTotalEnjoyed.get(ind+1).getValue());
						tbLblTotalBalance.get(ind).setValue(tbLblTotalBalance.get(ind+1).getValue());
						tbtxTotalAmount.get(ind).setValue(tbtxTotalAmount.get(ind+1).getValue());
					}
				}
			});

			table.addItem(new Object[]{tbLblSl.get(ar),tbLblAutoEmpID.get(ar),tbLblEmployeeID.get(ar),tbLblProximityID.get(ar),
					tbLblEmployeeName.get(ar),tbLblDesignationID.get(ar),tbLblDesignation.get(ar),tbLblSectionID.get(ar),tbLblSection.get(ar),tbtLblBasic.get(ar),
					tblblClOpeningTotal.get(ar),tblblClEnjoyed.get(ar),tbLblClBalance.get(ar),
					tbtxClAllowance.get(ar),tbLblSlOpeningTotal.get(ar),tbLblSlEnjoyed.get(ar),tblblSlBalance.get(ar),
					tbtxtSlAllowance.get(ar),tbLblElOpeningTotal.get(ar),tbLblElEnjoyed.get(ar),tbLblElBalance.get(ar),
					tbLblElAllowance.get(ar),tbLblTotalLeave.get(ar),tbLblTotalEnjoyed.get(ar),tbLblTotalBalance.get(ar),tbtxTotalAmount.get(ar),
					tbBtndel.get(ar)}, ar);
		}

		private AbsoluteLayout buildlMainLayout()
		{
			mainLayout=new AbsoluteLayout();
			mainLayout.setImmediate(false);
			mainLayout.setWidth("1220.0px");
			mainLayout.setHeight("620.0px");

			dDate = new PopupDateField();
			dDate.setImmediate(true);
			dDate.setWidth("110px");
			dDate.setHeight("-1px");
			dDate.setDateFormat("dd-MM-yyyy");
			dDate.setValue(new java.util.Date());
			dDate.setResolution(PopupDateField.RESOLUTION_DAY);
			mainLayout.addComponent(new Label("Date : "), "top:10.0px; left:25.0px;");
			mainLayout.addComponent(dDate, "top:08.0px; left:115.0px;");

			txtRefference = new TextRead();
			txtRefference.setImmediate(false);
			txtRefference.setWidth("150px");
			txtRefference.setHeight("-1px");
			mainLayout.addComponent(new Label("Refference : "), "top:40.0px; left:25.0px;");
			mainLayout.addComponent(txtRefference, "top:38.0px; left:115.0px;");

			cmbLeaveYear=new ComboBox();
			cmbLeaveYear.setImmediate(true);
			cmbLeaveYear.setWidth("130.0px");
			mainLayout.addComponent(new Label("Year : "), "top:70.0px; left:25.0px;");
			mainLayout.addComponent(cmbLeaveYear, "top:68.0px; left:115.0px;");

			cmbDepartment=new ComboBox();
			cmbDepartment.setImmediate(true);
			cmbDepartment.setWidth("200.0px");
			mainLayout.addComponent(new Label("Department: "), "top:100.0px; left:25.0px;");
			mainLayout.addComponent(cmbDepartment,"top:98.0px; left:115.0px;");

			cmbSection=new ComboBox();
			cmbSection.setImmediate(true);
			cmbSection.setWidth("200.0px");
			mainLayout.addComponent(new Label("Section: "), "top:100.0px; left:340.0px;");
			mainLayout.addComponent(cmbSection,"top:98.0px; left:400.0px;");

			// chkSectionAll
			chkSectionAll = new CheckBox("All");
			chkSectionAll.setImmediate(true);
			chkSectionAll.setWidth("-1px");
			chkSectionAll.setHeight("-1px");
			mainLayout.addComponent(chkSectionAll, "top:100.0px;left:600.0px;");

			opgTypeOfSearch=new OptionGroup("",lst);
			opgTypeOfSearch.setImmediate(true);
			opgTypeOfSearch.setStyleName("horizontal");
			opgTypeOfSearch.select("Employee ID");
			mainLayout.addComponent(opgTypeOfSearch, "top:70.0px;left:680.0px;");

			// lblEmployeeID
			lblEmployeeID = new Label("Employee ID :");
			lblEmployeeID.setImmediate(false);
			lblEmployeeID.setWidth("-1px");
			lblEmployeeID.setHeight("-1px");
			mainLayout.addComponent(lblEmployeeID, "top:100.0px; left:680.0px;");

			// cmbEmployeeID
			cmbEmployeeID = new ComboBox();
			cmbEmployeeID.setImmediate(true);
			cmbEmployeeID.setWidth("250px");
			cmbEmployeeID.setHeight("-1px");
			cmbEmployeeID.setNullSelectionAllowed(true);
			cmbEmployeeID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbEmployeeID, "top:98.0px; left:780.0px;");

			// chkSectionAll
			chkEmployeeAll = new CheckBox("All");
			chkEmployeeAll.setImmediate(true);
			chkEmployeeAll.setWidth("-1px");
			chkEmployeeAll.setHeight("-1px");
			mainLayout.addComponent(chkEmployeeAll, "top:100.0px;right:150.0px;");

			/*// cmbEmployeeName
			cmbEmployeeI = new ComboBox();
			cmbEmployeeID.setImmediate(true);
			cmbEmployeeID.setWidth("250px");
			cmbEmployeeID.setHeight("-1px");
			cmbEmployeeID.setNullSelectionAllowed(true);
			cmbEmployeeID.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbEmployeeID, "top:68.0px; left:30.0px;");*/



			lblFullText = new Label("*Note: CL=Casual Leave,SL=Sick Leave,El=Earn Leave ");
			mainLayout.addComponent(lblFullText,"top:8.0px; left: 700px;");

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
			table.setColumnWidth("Employee ID", 85);

			table.addContainerProperty("Proximity ID", Label.class, new Label());
			table.setColumnWidth("Proximity ID", 85);

			table.addContainerProperty("Employee Name", Label.class , new Label());
			table.setColumnWidth("Employee Name",100);

			table.addContainerProperty("Designation ID", Label.class, new Label());
			table.setColumnWidth("Designation ID", 50);

			table.addContainerProperty("Designation", Label.class , new Label());
			table.setColumnWidth("Designation",85);

			table.addContainerProperty("Section ID", Label.class, new Label());
			table.setColumnWidth("Section ID", 50);

			table.addContainerProperty("Section", Label.class , new Label());
			table.setColumnWidth("Section",85);

			table.addContainerProperty("Basic", Label.class , new Label());
			table.setColumnWidth("Basic",55);

			table.addContainerProperty("CLTTL", Label.class , new Label());
			table.setColumnWidth("CLTTL",30);

			table.addContainerProperty("CLENJ", Label.class , new Label());
			table.setColumnWidth("CLENJ",30);

			table.addContainerProperty("CLBAL", Label.class , new Label());
			table.setColumnWidth("CLBAL",30);

			table.addContainerProperty("CLTK", Label.class , new Label());
			table.setColumnWidth("CLTK",50);

			table.addContainerProperty("SLTTL", Label.class , new Label());
			table.setColumnWidth("SLTTL",30);

			table.addContainerProperty("SLENJ", Label.class , new Label());
			table.setColumnWidth("SLENJ",30);

			table.addContainerProperty("SLBAL", Label.class , new Label(""));
			table.setColumnWidth("SLBAL",30);

			table.addContainerProperty("SLTK", Label.class , new Label(""));
			table.setColumnWidth("SLTK",50);

			table.addContainerProperty("ELTTL", Label.class , new Label(""));
			table.setColumnWidth("ELTTL",35);

			table.addContainerProperty("ELENJ", Label.class , new Label(""));
			table.setColumnWidth("ELENJ",30);

			table.addContainerProperty("ELBAL", Label.class , new Label());
			table.setColumnWidth("ELBAL",35);

			table.addContainerProperty("ELTK", Label.class , new Label());
			table.setColumnWidth("ELTK",50);

			table.addContainerProperty("T.Leave", Label.class , new Label());
			table.setColumnWidth("T.Leave",40);

			table.addContainerProperty("T.Enjoyed", Label.class , new Label());
			table.setColumnWidth("T.Enjoyed",55);

			table.addContainerProperty("T.Balance", Label.class , new Label());
			table.setColumnWidth("T.Balance",45);

			table.addContainerProperty("T.Amount", Label.class , new Label());
			table.setColumnWidth("T.Amount",55);

			table.addContainerProperty("Remove", NativeButton.class , new NativeButton());
			table.setColumnWidth("Remove",40);

			table.setColumnCollapsed("EMP ID", true);
			table.setColumnCollapsed("Proximity ID", true);
			table.setColumnCollapsed("Designation ID", true);
			table.setColumnCollapsed("Employee Type", true);
			table.setColumnCollapsed("Section ID", true);
			table.setColumnCollapsed("Section", true);



			table.setColumnAlignments(new String[]{Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
					Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
					Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
					Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,
					Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT,Table.ALIGN_LEFT});

			mainLayout.addComponent(table, "top:140.0px; left:25.0px;");
			mainLayout.addComponent(cButton, "top:580.0px; left:365.0px;");
			return mainLayout;
		}
	


}
