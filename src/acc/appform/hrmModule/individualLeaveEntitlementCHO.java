package acc.appform.hrmModule;

	import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

	import org.hibernate.Session;
import org.hibernate.Transaction;

	import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

	import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class individualLeaveEntitlementCHO extends Window {


		private AbsoluteLayout mainLayout;
		private Label lblYear;
		private InlineDateField dYear = new InlineDateField();
		private Label lblDeptName;
		private ComboBox cmbDeptName = new ComboBox();
		private Label lblSecName;
		private ComboBox cmbSecName = new ComboBox();

		private CheckBox chkEmployeeAll;
		private AmountField txtCl,txtSl,txtEl;
		private boolean isRefresh = false;
		private DecimalFormat decimal = new DecimalFormat("#0");

		private List <?> groupEmployee = Arrays.asList(new String [] {"Employee ID","Proximity ID","Employee Name"});
		private OptionGroup opgEmployeeGroup;

		private Label lblEmployeeName;
		private ComboBox cmbEmployeeName = new ComboBox();

		private SessionBean sessionBean;

		String computerName = "";
		String userName = "";
		String year = "";
		String deptID = "";

		String strEmpDeptID ="";

		String strEmpID ="";
		String strDeptID ="";

		private boolean isUpdate=false;
		private boolean isFind= false;

		private TextRead trIDDeptID = new TextRead("");
		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		private Table table = new Table();
		private ArrayList<Label> lbSl = new ArrayList<Label>();
		private ArrayList<Label> lblEmpID=new ArrayList<Label>();
		private ArrayList<Label> lbEmployeeID = new ArrayList<Label>();
		private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
		private ArrayList<Label> lbDesignationID = new ArrayList<Label>();
		private ArrayList<Label> lbDesignation = new ArrayList<Label>();
		private ArrayList<PopupDateField> lbJoiningDate = new ArrayList<PopupDateField>();
		private ArrayList<AmountField> amtCL = new ArrayList<AmountField>();
		private ArrayList<AmountField> amtSL = new ArrayList<AmountField>();
		private ArrayList<AmountField> amtAL = new ArrayList<AmountField>();
		private CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");
		ArrayList<Component> allComp = new ArrayList<Component>();

		private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		int i=0;

		public individualLeaveEntitlementCHO(SessionBean sessionBean)
		{
			this.sessionBean = sessionBean;

			this.setCaption("INDIVIDUAL LEAVE ENTITLEMENT CHO:: "+sessionBean.getCompany());
			this.setResizable(false);

			buildMainLayout();
			setContent(mainLayout);
			tableInitialize();
			btnIni(true);
			componentIni(true); 
			departMentdataAdd();
			buttonAction();
			focusEnter();
			authenticationCheck();
		}

		private void authenticationCheck()
		{
			if(!sessionBean.isSubmitable()){
				button.btnSave.setVisible(false);
			}

			if(!sessionBean.isUpdateable()){
				button.btnEdit.setVisible(false);
			}

			if(!sessionBean.isDeleteable()){
				button.btnDelete.setVisible(false);
			}
		}

		private void tableInitialize()
		{
			table.setColumnCollapsingAllowed(true);

			table.setWidth("98%");
			table.setHeight("310px");
			table.setPageLength(0);

			table.addContainerProperty("SL #", Label.class , new Label());
			table.setColumnWidth("SL #",20);

			table.addContainerProperty("EMP ID", Label.class , new Label());
			table.setColumnWidth("EMP ID",50);

			table.addContainerProperty("Employee ID", Label.class, new Label());
			table.setColumnWidth("Employee ID", 90);

			table.addContainerProperty("Employee Name", Label.class , new Label());
			table.setColumnWidth("Employee Name",200);

			table.addContainerProperty("Designation ID", Label.class , new Label());
			table.setColumnWidth("Designation ID",50);

			table.addContainerProperty("Designation", Label.class, new Label());
			table.setColumnWidth("Designation", 100);

			table.addContainerProperty("Date of Joining", PopupDateField.class , new PopupDateField());
			table.setColumnWidth("Date of Joining",90);

			table.addContainerProperty("CL", AmountField.class , new AmountField());
			table.setColumnWidth("CL",33);	

			table.addContainerProperty("SL", AmountField.class , new AmountField());
			table.setColumnWidth("SL",33);

			table.addContainerProperty("EL", AmountField.class , new AmountField());
			table.setColumnWidth("EL",33);

			table.setColumnCollapsed("EMP ID", true);
			table.setColumnCollapsed("Designation ID", true);

			rowAddinTable();
		}

		public void rowAddinTable()
		{
			for(int i=0;i<10;i++)
			{
				tableRowAdd(i);
			}
		}

		public void tableRowAdd(final int ar)
		{
			lbSl.add(ar, new Label(""));
			lbSl.get(ar).setWidth("100%");
			lbSl.get(ar).setHeight("20px");
			lbSl.get(ar).setValue(ar+1);

			lblEmpID.add(ar, new Label());
			lblEmpID.get(ar).setWidth("100%");
			lblEmpID.get(ar).setHeight("20px");

			lbEmployeeID.add(ar, new Label(""));
			lbEmployeeID.get(ar).setWidth("100%");
			lbEmployeeID.get(ar).setHeight("-1px");

			lbEmployeeName.add(ar, new Label(""));
			lbEmployeeName.get(ar).setWidth("100%");
			lbEmployeeName.get(ar).setImmediate(true);

			lbDesignationID.add(ar, new Label());
			lbDesignationID.get(ar).setWidth("100%");

			lbDesignation.add(ar, new Label(""));
			lbDesignation.get(ar).setWidth("100%");
			lbDesignation.get(ar).setHeight("-1px");

			lbJoiningDate.add(ar, new PopupDateField(""));
			lbJoiningDate.get(ar).setDateFormat("dd-MM-yyyy");
			lbJoiningDate.get(ar).setResolution(PopupDateField.RESOLUTION_DAY);
			lbJoiningDate.get(ar).setReadOnly(false);
			lbJoiningDate.get(ar).setWidth("100%");
			lbJoiningDate.get(ar).setReadOnly(true);

			amtCL.add(ar, new AmountField());
			amtCL.get(ar).setWidth("100%");
			amtCL.get(ar).setImmediate(true);
			amtCL.get(ar).setHeight("20px");
			amtCL.get(ar).setStyleName("amount");
			amtCL.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					amtSL.get(ar).focus();
				}
			});

			amtSL.add(ar, new AmountField());
			amtSL.get(ar).setWidth("100%");
			amtSL.get(ar).setImmediate(true);
			amtSL.get(ar).setStyleName("amount");
			amtSL.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					amtAL.get(ar).focus();
				}
			});

			amtAL.add(ar, new AmountField());
			amtAL.get(ar).setWidth("100%");
			amtAL.get(ar).setImmediate(true);
			amtAL.get(ar).setStyleName("amount");
			amtAL.get(ar).addListener(new ValueChangeListener() {
				public void valueChange(ValueChangeEvent event) {
					amtCL.get(ar+1).focus();
				}
			});

			table.addItem(new Object[]{lbSl.get(ar),lblEmpID.get(ar),lbEmployeeID.get(ar),lbEmployeeName.get(ar),
					lbDesignationID.get(ar),lbDesignation.get(ar),lbJoiningDate.get(ar),amtCL.get(ar),amtSL.get(ar),
					amtAL.get(ar)},ar);
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

		private void componentIni(boolean b) 
		{
			dYear.setEnabled(!b);
			if(isFind==false)
			{
				chkEmployeeAll.setEnabled(!b);
				cmbDeptName.setEnabled(!b);
				cmbSecName.setEnabled(!b);
				cmbEmployeeName.setEnabled(!b);
				txtCl.setEnabled(!b);
				txtSl.setEnabled(!b);
				txtEl.setEnabled(!b);
			}
			else
			{
				chkEmployeeAll.setEnabled(b);
				cmbDeptName.setEnabled(b);
				cmbSecName.setEnabled(b);
				cmbEmployeeName.setEnabled(b);
				txtCl.setEnabled(b);
				txtSl.setEnabled(b);
				txtEl.setEnabled(b);
			}
			table.setEnabled(!b);
		}

		private void txtClear()
		{
			chkEmployeeAll.setValue(false);
			dYear.setValue(new java.util.Date());
			cmbDeptName.setValue(null);
			cmbSecName.setValue(null);
			cmbEmployeeName.setValue(null);
			txtCl.setValue("");
			txtSl.setValue("");
			txtEl.setValue("");
			for(int i =0; i<lbEmployeeID.size(); i++)
			{
				lbEmployeeID.get(i).setValue("");
				lbEmployeeName.get(i).setValue("");
				lbDesignationID.get(i).setValue("");
				lbDesignation.get(i).setValue("");
				lbJoiningDate.get(i).setReadOnly(false);
				lbJoiningDate.get(i).setValue(null);
				lbJoiningDate.get(i).setReadOnly(true);
				amtCL.get(i).setValue("");
				amtSL.get(i).setValue("");
				amtAL.get(i).setValue("");
			}
		}

		private void tableclear()
		{
			for(int i =0; i<lbEmployeeID.size(); i++)
			{
				lblEmpID.get(i).setValue("");
				lbEmployeeID.get(i).setValue("");
				lbEmployeeName.get(i).setValue("");
				lbDesignationID.get(i).setValue("");
				lbDesignation.get(i).setValue("");
				lbJoiningDate.get(i).setReadOnly(false);
				lbJoiningDate.get(i).setValue(null);
				lbJoiningDate.get(i).setReadOnly(true);
				amtCL.get(i).setValue("");
				amtSL.get(i).setValue("");
				amtAL.get(i).setValue("");
			}
		}

		private void focusEnter()
		{
			allComp.add(cmbDeptName);
			allComp.add(cmbSecName);
			allComp.add(txtCl);
			allComp.add(txtSl);
			allComp.add(txtEl);
			for(int i=0; i<lbEmployeeName.size();i++)
			{
				allComp.add(amtCL.get(i));
				allComp.add(amtSL.get(i));
				allComp.add(amtAL.get(i));
			}

			allComp.add(button.btnSave);

			new FocusMoveByEnter(this,allComp);
		}

		private void departMentdataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select distinct ein.vDepartmentId,dept.vDepartmentName from tbDepartmentInfo dept inner join " +
						"tbEmployeeInfo ein on dept.vDepartmentId=ein.vDepartmentId where (vEmployeeType='Permanent' " +
						"or vEmployeeType='Provisionary') and dept.vDepartmentName='CHO'  order by dept.vDepartmentName";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbDeptName.addItem(element[0]);
					cmbDeptName.setItemCaption(element[0], element[1].toString());	
				}
			}

			catch(Exception ex)
			{
				this.getParent().showNotification("departMentdataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void sectiondataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query = "select distinct ein.vSectionId,sein.SectionName from tbSectionInfo sein inner join "
						+ " tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where (vEmployeeType='Permanent' "
						+ " or vEmployeeType='Provisionary') and ein.vDepartmentID = '"+cmbDeptName.getValue()+"' order "
						+ " by sein.SectionName";
				List <?> list = session.createSQLQuery(query).list();

				for (Iterator <?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element =  (Object[]) iter.next();	
					cmbSecName.addItem(element[0]);
					cmbSecName.setItemCaption(element[0], element[1].toString());	
				}
			}

			catch(Exception ex)
			{
				this.getParent().showNotification("sectiondataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void buttonAction()
		{
			cmbDeptName.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbSecName.removeAllItems();
					if(cmbDeptName.getValue()!=null)
					{
						chkEmployeeAll.setValue(false);
						tableclear();
						sectiondataAdd();
					}
				}
			});

			cmbSecName.addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					cmbEmployeeName.removeAllItems();
					if(cmbSecName.getValue()!=null && !isFind)
					{
						chkEmployeeAll.setValue(false);
						tableclear();
						addEmployeeName();
					}
				}
			});

			cmbEmployeeName.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbEmployeeName.getValue()!=null)
					{
						if(!isFind)
						{
							txtCl.setValue("");
							txtSl.setValue("");
							txtEl.setValue("");
							tableDataAdd();
							amtCL.get(0).focus();
						}
					}
				}
			});
			
			chkEmployeeAll.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(chkEmployeeAll.booleanValue())
					{
						cmbEmployeeName.setEnabled(false);
						cmbEmployeeName.setValue(null);
						
						if(!isFind)
						{
							txtCl.setValue("");
							txtSl.setValue("");
							txtEl.setValue("");
							tableclear();
							tableDataAdd();
							amtCL.get(0).focus();
							i=0;
						}
					}
					else
					{
						txtCl.setValue("");
						txtSl.setValue("");
						txtEl.setValue("");
						tableclear();
						cmbEmployeeName.setEnabled(true);
					}
				}
			});
			
			txtCl.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					calculateLeave();
				}
			});

			txtSl.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					calculateLeave();
				}
			});

			txtEl.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					calculateLeave();
				}
			});

			button.btnNew.addListener(new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					isRefresh = false;
					isUpdate = false;
					isFind = false;
					newButtonEvent();
				}
			});

			button.btnFind.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					isFind = true;
					findButtonEvent();
				}
			});

			button.btnEdit.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					isUpdate = true;
					updateAction();
				}
			});


			button.btnSave.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					if(cmbSecName.getValue()!=null)
					{
						if(!amtCL.get(0).getValue().toString().equals(""))
						{
							saveButtonAction();
						}
						else
						{
							getParent().showNotification("Warning","Provide Leave", Notification.TYPE_WARNING_MESSAGE);
							amtCL.get(0).focus();
						}
					}
					else
					{
						getParent().showNotification("Warning","Select Department", Notification.TYPE_WARNING_MESSAGE);
						cmbSecName.focus();
					}
				}
			});

			button.btnRefresh.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event) 
				{
					isRefresh = true;
					isUpdate = false;
					isFind=false;
					refreshButtonEvent();
					cmbEmployeeName.setEnabled(false);
				}
			});

			button.btnExit.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					close();
				}
			});
		}
		private void calculateLeave()
		{
			double sum = 0;

			sum = Double.parseDouble("0"+txtCl.getValue().toString()) + Double.parseDouble("0"+txtSl.getValue().toString()) +
					Double.parseDouble("0"+txtEl.getValue().toString());

			if(!isRefresh)
			{
				if(sum>0)
				{
					setLeaveToAll();
					//tableEnable();
				}
				else
				{
					LeaveClearAll();
					//tableEnable();
				}
			}
		}
		private void LeaveClearAll()
		{
			for(int i = 0; i<lblEmpID.size(); i++)
			{
				if(!lblEmpID.get(i).getValue().toString().isEmpty())
				{
					if(txtCl.getValue().toString().trim().isEmpty() || Double.parseDouble(txtCl.getValue().toString().trim())<=0)
						amtCL.get(i).setValue("");
					if(txtSl.getValue().toString().trim().isEmpty() || Double.parseDouble(txtSl.getValue().toString().trim())<=0)
						amtSL.get(i).setValue("");
					if(txtEl.getValue().toString().trim().isEmpty() || Double.parseDouble(txtEl.getValue().toString().trim())<=0)
						amtAL.get(i).setValue("");
				}
			}
		}
		/*private void tableEnable()
		{
			for(int i = 0; i<lblEmpID.size(); i++)
			{
				if(!lblEmpID.get(i).getValue().toString().isEmpty())
				{
					if(Double.parseDouble(txtCl.getValue().toString().trim().isEmpty()?"0":txtCl.getValue().toString().trim())>0)
						amtCL.get(i).setEnabled(false);
					else
						amtCL.get(i).setEnabled(true);

					if(Double.parseDouble(txtSl.getValue().toString().trim().isEmpty()?"0":txtSl.getValue().toString().trim())>0)
						amtSL.get(i).setEnabled(false);
					else
						amtSL.get(i).setEnabled(true);

					if(Double.parseDouble(txtEl.getValue().toString().trim().isEmpty()?"0":txtEl.getValue().toString().trim())>0)
						amtAL.get(i).setEnabled(false);
					else
						amtAL.get(i).setEnabled(true);
				}
			}
		}*/
		
		private void setLeaveToAll()
		{
			for(int i = 0; i<lblEmpID.size(); i++)
			{
				if(!lblEmpID.get(i).getValue().toString().isEmpty())
				{
					amtCL.get(i).setValue(decimal.format(Double.parseDouble("0"+txtCl.getValue().toString())));
					amtSL.get(i).setValue(decimal.format(Double.parseDouble("0"+txtSl.getValue().toString())));
					amtAL.get(i).setValue(decimal.format(Double.parseDouble("0"+txtEl.getValue().toString())));
				}
			}
		}
		private void updateAction() 
		{
			System.out.println("Update");
			if (!lbEmployeeID.get(0).getValue().toString().isEmpty()) 
			{
				btnIni(false);
				componentIni(false);
				opgEmployeeGroup.setEnabled(false);
				cmbEmployeeName.setEnabled(false);
			} 
			else
			{
				this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
			}
		}

		private void addEmployeeName()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query =  "select vEmployeeId,employeeCode,vProximityId,vEmployeeName from tbEmployeeInfo where vEmployeeId not in "
						+ " (select vAutoEmployeeId from tbLeaveBalanceNew where YEAR(currentYear) = '"+yearFormat.format(dYear.getValue())+"') "
						+ "and vDepartmentID = '"+cmbDeptName.getValue()+"' and vSectionID = '"+cmbSecName.getValue()+"' and iStatus = 1 and "
						+ "(vEmployeeType='Permanent' or vEmployeeType='Provisionary') order by employeeCode";

				List <?> list = session.createSQLQuery(query).list();
				if(!list.isEmpty())
				{
					for(Iterator <?> iter = list.iterator(); iter.hasNext();)
					{				  
						Object[] element = (Object[]) iter.next();
						if(opgEmployeeGroup.getValue()=="Employee ID")
						{
							cmbEmployeeName.addItem(element[0]);
							cmbEmployeeName.setItemCaption(element[0], element[1].toString());
						}
						if(opgEmployeeGroup.getValue()=="Proximity ID")
						{
							cmbEmployeeName.addItem(element[0]);
							cmbEmployeeName.setItemCaption(element[0], element[2].toString());
						}
						if(opgEmployeeGroup.getValue()=="Employee Name")
						{
							cmbEmployeeName.addItem(element[0]);
							cmbEmployeeName.setItemCaption(element[0], element[3].toString());
						}
					}
				}
				else
				{
					this.getParent().showNotification("Warning!","Balance already exist for all employee or no employee found", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			catch (Exception ex)
			{
				this.getParent().showNotification("addEmployeeName", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void refreshButtonEvent() 
		{
			componentIni(true);
			btnIni(true);
			txtClear();	
			i = 0;
		}
		private int index()
		{
			for(int i=0;i<lblEmpID.size();i++)
			{
				if (lblEmpID.get(i).getValue().toString().isEmpty())
				{
					return i;	
				}
			}
			return 0;
		}
		private boolean doubleentrycheck()
		{
			for(int i=0;i<lblEmpID.size();i++)
			{
				if (lblEmpID.get(i).getValue().toString().equalsIgnoreCase(cmbEmployeeName.getValue().toString()))
				{
					return false;	
				}
			}
			return true;
		}
		private void tableDataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query ="SELECT ein.vEmployeeId, ein.employeeCode, ein.vEmployeeName, ein.vDesignationId, din.designationName , "
						+ " ein.dJoiningDate FROM tbEmployeeInfo ein inner join tbDesignationInfo din on ein.vDesignationId = din.designationId "
						+ " where vSectionId='"+cmbSecName.getValue()+"' and iStatus = '1' and vEmployeeId not IN (Select vAutoEmployeeId from "
						+ " tbLeaveBalanceNew where YEAR(currentYear) = '"+yearFormat.format(dYear.getValue())+"' and vSectionId='"+cmbSecName.getValue()+"') "
						+ " and (vEmployeeType='Permanent' or vEmployeeType='Provisionary') "
						+ "and ein.vEmployeeID like '"+(cmbEmployeeName.getValue()!=null?cmbEmployeeName.getValue().toString():"%")+"' "
						+ "order by employeeCode";

				List <?> list = session.createSQLQuery(query).list();

				if(!list.isEmpty())
				{
					if(cmbEmployeeName.getValue()!=null)
					{  
						int ind=index();
						
						if(doubleentrycheck())
						{
							Iterator <?> iter=list.iterator();
							if(iter.hasNext())
							{				  
								Object[] element = (Object[]) iter.next();
			
								lblEmpID.get(i).setValue(element[0]);
								lbEmployeeID.get(i).setValue(element[1]);
								lbEmployeeName.get(i).setValue(element[2]);
								lbDesignationID.get(i).setValue(element[3]);
								lbDesignation.get(i).setValue(element[4]);
								lbJoiningDate.get(i).setReadOnly(false);
								lbJoiningDate.get(i).setValue(element[5]);
								lbJoiningDate.get(i).setReadOnly(true);
			
								if((i)==lbEmployeeID.size()-1)
								{
									tableRowAdd(i+1);
								}
								i++;
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
						for(Iterator <?> iter=list.iterator();iter.hasNext();)
						{
							Object[] element = (Object[]) iter.next();
							
							lblEmpID.get(ind).setValue(element[0]);
							lbEmployeeID.get(ind).setValue(element[1]);
							lbEmployeeName.get(ind).setValue(element[2]);
							lbDesignationID.get(ind).setValue(element[3]);
							lbDesignation.get(ind).setValue(element[4]);
							lbJoiningDate.get(ind).setReadOnly(false);
							lbJoiningDate.get(ind).setValue(element[5]);
							lbJoiningDate.get(ind).setReadOnly(true);
		
							if(ind==lblEmpID.size()-1)
							{
								tableRowAdd(ind+1);
							}
							ind++;
						}
					}
				}
				else
				{
					tableclear();
					this.getParent().showNotification("Warning!","Balance already exist or no employee found", Notification.TYPE_WARNING_MESSAGE);
				}
			}
			catch (Exception ex)
			{
				this.getParent().showNotification("tableDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void newButtonEvent() 
		{
			cmbSecName.focus();
			componentIni(false);
			btnIni(false);
			txtClear();	
			i = 0;
		}

		private void findButtonEvent()
		{
			Window win = new individualLeaveEntitlementFind(sessionBean, trIDDeptID);
			win.setStyleName("cwindow");
			win.setModal(true);
			win.addListener(new Window.CloseListener() 
			{
				public void windowClose(CloseEvent e) 
				{
					if(trIDDeptID.getValue().toString().length()>0)
					{
						isFind = true;
						strEmpID = trIDDeptID.getValue().toString();
						findInitialize(strEmpID);
					}
				}
			});
			this.getParent().addWindow(win);
		}

		private void findInitialize(String EmpID)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();		
			try
			{	
				String findQuery = " Select E.vEmployeeId, E.employeeCode, E.vEmployeeName, P.vDepartmentID,"
						+ "P.vSectionID, E.vDesignationId, din.designationName,P.iClOpening, P.iSlOpening, "
						+ "P.iAlOpening,E.dJoiningDate FROM tbLeaveBalanceNew as P "
						+ "INNER JOIN tbEmployeeInfo as E ON E.vEmployeeId = P.vAutoEmployeeId "
						+ "inner join tbDesignationInfo din on din.designationId = E.vDesignationId  "
						+ "WHERE P.vAutoEmployeeId='"+EmpID+"' "
						+ "and YEAR(currentYear) = '"+yearFormat.format(dYear.getValue())+"' and iFlag = 1";

				List <?> list = session.createSQLQuery(findQuery).list();

				if(!list.isEmpty())
				{
					if(list.iterator().hasNext())
					{
						Object[] element = (Object[]) list.iterator().next();

						cmbDeptName.setValue(element[3]);
						cmbSecName.setValue(element[4]);
						lblEmpID.get(0).setValue(element[0]);
						lbEmployeeID.get(0).setValue(element[1]);
						lbEmployeeName.get(0).setValue(element[2]);
						lbDesignationID.get(0).setValue(element[5]);
						lbDesignation.get(0).setValue(element[6]);
						amtCL.get(0).setValue(element[7]);
						amtSL.get(0).setValue(element[8]);
						amtAL.get(0).setValue(element[9]);
						lbJoiningDate.get(0).setReadOnly(false);
						lbJoiningDate.get(0).setValue(element[10]);
						lbJoiningDate.get(0).setDateFormat("dd-MM-yyyy");
						lbJoiningDate.get(0).setReadOnly(true);
					}
				}
				else
				{
					tableclear();
					this.getParent().showNotification("Warning!","Balance Already Exist", Notification.TYPE_WARNING_MESSAGE); 
				}
			}
			catch (Exception ex)
			{
				this.getParent().showNotification("findInitialize", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			isFind = false;
		}

		private void saveButtonAction()
		{
			if (sessionBean.isUpdateable())
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
							}	
						}
					});
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
								insertData();
							}
						}
					});
				}
			}
		}

		private void insertData ()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{
				year = yearFormat.format(dYear.getValue());
				String insertQuery = "";

				for(int i=0; i<lbEmployeeID.size(); i++)
				{
					if(!lbEmployeeID.get(i).getValue().toString().isEmpty())
					{
						if(!amtCL.get(i).getValue().toString().isEmpty() || !amtSL.get(i).getValue().toString().isEmpty() ||
								!amtAL.get(i).getValue().toString().isEmpty())
						{
							insertQuery = "insert into tbLeaveBalanceNew (currentYear,vAutoEmployeeId,vEmployeeId,vProximityId,"
									+ " vSectionId,iClyBalance,iSlyBalance,iAlyBalance,iMlyBalance,iClOpening,iCarryCl,iSlOpening,"
									+ " iCarrySl,iAlOpening,iCarryAl,iMlOpening,iCarryMl,iClBalance,iSlBalance,iAlBalance,iMlBalance,"
									+ " iClEnjoyed,iSlEnjoyed,iAlEnjoyed,iMlEnjoyed,iflag,vEncashment,vPartial,userName,entryTime,userIp,vDepartmentID) values "
									+ " ('"+dateFormat.format(dYear.getValue())+"',"
									+ " '"+lblEmpID.get(i).getValue()+"',"
									+ " '"+lbEmployeeID.get(i).getValue()+"',"
									+ "	(select vProximityID from tbEmployeeInfo where vEmployeeID = '"+lblEmpID.get(i).getValue()+"'),"
									+ " '"+cmbSecName.getValue()+"',0,0,0,0,"
									+ " '"+amtCL.get(i).getValue().toString().trim().replaceAll(",", "")+"',0,"
									+ " '"+amtSL.get(i).getValue().toString().trim().replaceAll(",", "")+"',"
									+ " 0,'"+amtAL.get(i).getValue().toString().trim().replaceAll(",", "")+"',"
									+ " 0,0,0,0,0,0,0,0,0,0,0,1,'0','0','"+sessionBean.getUserName()+"',GETDATE(),'"+sessionBean.getUserIp()+"',"
									+ " '"+cmbDeptName.getValue()+"')";

							session.createSQLQuery(insertQuery).executeUpdate();
						}
					}
				}
				tx.commit();
				this.getParent().showNotification("All Information Saved Successfully");
				isUpdate=false;
				isFind = false;
				txtClear();
				componentIni(true);
				btnIni(true);
			}
			catch(Exception ex)
			{
				this.getParent().showNotification("insertData", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
				tx.rollback();
			}
			finally{session.close();}
		}

		private void updateData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{
				year = yearFormat.format(dYear.getValue());
				String updateQuery = "";

				for(int i=0; i<lbEmployeeID.size(); i++)
				{
					if(!lbEmployeeID.get(i).getValue().toString().isEmpty())
					{
						if(!amtCL.get(i).getValue().toString().isEmpty() || !amtSL.get(i).getValue().toString().isEmpty() ||
								!amtAL.get(i).getValue().toString().isEmpty())
						{
							updateQuery = "update tbLeaveBalanceNew set iClOpening = '"+amtCL.get(i).getValue().toString().trim().replaceAll(",", "")+"',"
									+ " iSlOpening = '"+amtSL.get(i).getValue().toString().trim().replaceAll(",", "")+"', "
									+ " iAlOpening = '"+amtAL.get(i).getValue().toString().trim().replaceAll(",", "")+"', "
									+ " userName = '"+sessionBean.getUserName()+"', entrytime = GETDATE(), userIP = '"+sessionBean.getUserIp()+"' "
									+ " where vAutoEmployeeId = '"+lblEmpID.get(i).getValue()+"'";

							session.createSQLQuery(updateQuery).executeUpdate();
						}
					}
				}
				tx.commit();
				this.getParent().showNotification("All Information Updated Successfully");
				isUpdate=false;
				isFind = false;
				txtClear();
				componentIni(true);
				btnIni(true);
			}
			catch(Exception ex)
			{
				this.getParent().showNotification("updateData", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
				tx.rollback();
			}
			finally{session.close();}
		}

		private AbsoluteLayout buildMainLayout()
		{
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(true);
			mainLayout.setMargin(false);

			setWidth("760px");
			setHeight("495px");

			lblYear = new Label();
			lblYear.setImmediate(true);
			lblYear.setWidth("-1px");
			lblYear.setHeight("-1px");
			lblYear.setValue("Opening Year :");
			mainLayout.addComponent(lblYear, "top:10.0px;left:20.0px;");

			dYear = new InlineDateField();
			dYear.setValue(new java.util.Date());
			dYear.setWidth("110px");
			dYear.setHeight("24px");
			dYear.setResolution(InlineDateField.RESOLUTION_YEAR);
			dYear.setDateFormat("yyyy");
			dYear.setInvalidAllowed(false);
			dYear.setImmediate(true);
			mainLayout.addComponent(dYear, "top:08.0px;left:130.0px;");

			lblDeptName = new Label();
			lblDeptName.setImmediate(true);
			lblDeptName.setWidth("-1px");
			lblDeptName.setHeight("-1px");
			lblDeptName.setValue("Department Name :");
			mainLayout.addComponent(lblDeptName, "top:40.0px;left:20.0px;");

			cmbDeptName = new ComboBox();
			cmbDeptName.setImmediate(true);
			cmbDeptName.setWidth("200px");
			cmbDeptName.setHeight("24px");
			cmbDeptName.setImmediate(true);
			cmbDeptName.setNullSelectionAllowed(false);
			mainLayout.addComponent(cmbDeptName, "top:38.0px;left:130.0px;");

			lblSecName = new Label();
			lblSecName.setImmediate(true);
			lblSecName.setWidth("-1px");
			lblSecName.setHeight("-1px");
			lblSecName.setValue("Section Name :");
			mainLayout.addComponent(lblSecName, "top:70.0px;left:20.0px;");

			cmbSecName = new ComboBox();
			cmbSecName.setImmediate(true);
			cmbSecName.setWidth("200px");
			cmbSecName.setHeight("24px");
			cmbSecName.setImmediate(true);
			cmbSecName.setNullSelectionAllowed(false);
			mainLayout.addComponent(cmbSecName, "top:68.0px;left:130.0px;");

			opgEmployeeGroup = new OptionGroup("",groupEmployee);
			opgEmployeeGroup.setImmediate(true);
			opgEmployeeGroup.setStyleName("horizontal");
			opgEmployeeGroup.setValue("Employee ID");
			mainLayout.addComponent(opgEmployeeGroup, "top:10.0px; left:430.0px;");

			lblEmployeeName = new Label();
			lblEmployeeName.setImmediate(true);
			lblEmployeeName.setWidth("-1px");
			lblEmployeeName.setHeight("-1px");
			lblEmployeeName.setValue("Employee Name :");
			mainLayout.addComponent(lblEmployeeName, "top:40.0px;left:360.0px;");



			cmbEmployeeName = new ComboBox();
			cmbEmployeeName.setImmediate(true);
			cmbEmployeeName.setWidth("260px");
			cmbEmployeeName.setHeight("24px");
			cmbEmployeeName.setImmediate(true);
			cmbEmployeeName.setNullSelectionAllowed(false);
			mainLayout.addComponent(cmbEmployeeName, "top:38.0px;left:450.0px;");

			chkEmployeeAll = new CheckBox("All");
			chkEmployeeAll.setImmediate(true);
			chkEmployeeAll.setWidth("-1px");
			chkEmployeeAll.setHeight("-1px");
			mainLayout.addComponent(chkEmployeeAll, "top:40.0px;left:715.0px;");
			
			txtCl = new AmountField();
			txtCl.setImmediate(true);
			txtCl.setWidth("40px");
			txtCl.setHeight("24px");
			txtCl.setImmediate(true);
			mainLayout.addComponent(txtCl, "top:68.0px;left:589.0px;");

			txtSl = new AmountField();
			txtSl.setImmediate(true);
			txtSl.setWidth("40px");
			txtSl.setHeight("24px");
			txtSl.setImmediate(true);
			mainLayout.addComponent(txtSl, "top:68.0px;left:634.0px;");
			
			txtEl = new AmountField();
			txtEl.setImmediate(true);
			txtEl.setWidth("40px");
			txtEl.setHeight("24px");
			txtEl.setImmediate(true);
			mainLayout.addComponent(txtEl, "top:68.0px;left:680.0px;");

			mainLayout.addComponent(table, "top:95.0px;left:20.0px;");
			mainLayout.addComponent(button, "top:415.0px;left:155.0px;");

			return mainLayout;
		}


}
