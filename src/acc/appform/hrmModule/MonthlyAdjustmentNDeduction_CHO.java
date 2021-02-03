package acc.appform.hrmModule;

	import java.text.DecimalFormat;
	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Iterator;
	import java.util.List;
	import org.hibernate.Session;
	import org.hibernate.Transaction;
	import com.common.share.AmountCommaSeperator;
	import com.common.share.CommonButton;
	import com.common.share.MessageBox;
	import com.common.share.SessionBean;
	import com.common.share.SessionFactoryUtil;
	import com.common.share.TextRead;
	import com.common.share.MessageBox.ButtonType;
	import com.common.share.MessageBox.EventListener;
	import com.vaadin.data.Property.ValueChangeListener;
	import com.vaadin.data.Property.ValueChangeEvent;
	import com.vaadin.terminal.ThemeResource;
	import com.vaadin.ui.AbsoluteLayout;
	import com.vaadin.ui.Button;
	import com.vaadin.ui.AbstractSelect.Filtering;
	import com.vaadin.ui.Button.ClickEvent;
	import com.vaadin.ui.OptionGroup;
	import com.vaadin.ui.CheckBox;
	import com.vaadin.ui.ComboBox;
	import com.vaadin.ui.Component;
	import com.vaadin.ui.Label;
	import com.vaadin.ui.NativeButton;
	import com.vaadin.ui.PopupDateField;
	import com.vaadin.ui.Table;
	import com.vaadin.ui.TextField;
	import com.vaadin.ui.Window;

public class MonthlyAdjustmentNDeduction_CHO extends Window {
	
		private SessionBean sessionBean;
		private AbsoluteLayout mainLayout;

		private Table table= new Table();
		private PopupDateField dWorkingDate ;

		private Label lblDate ;
		private Label lblSection;
		private ComboBox cmbSection;
		private ComboBox cmbDepartment;

		private Label lblEmployee;
		private ComboBox cmbEmployee;
		private CheckBox chkEmployeeAll;

		private OptionGroup opgEmployee;
		private List<String> lstEmployee = Arrays.asList(new String[]{"Employee ID","Proximity ID","Employee Name"});

		private ArrayList<NativeButton> btnDel=new ArrayList<NativeButton>();
		private ArrayList<Label> lblsl = new ArrayList<Label>();
		private ArrayList<Label> lblAutoEmployeeID=new ArrayList<Label>();
		private ArrayList<Label> lblEmployeeCode = new ArrayList<Label>();
		private ArrayList<Label> lblProximityID = new ArrayList<Label>();
		private ArrayList<Label> lblEmployeeName = new ArrayList<Label>();
		private ArrayList<Label> lblDesignation = new ArrayList<Label>();
		private ArrayList<Label> lblGross = new ArrayList<Label>();
		private ArrayList<AmountCommaSeperator> txtAdjust = new ArrayList<AmountCommaSeperator>();
		private ArrayList<AmountCommaSeperator> txtDeduction = new ArrayList<AmountCommaSeperator>();
		private ArrayList<TextField> txtReason = new ArrayList<TextField>();
		private ArrayList<TextField> txtPermittedBy = new ArrayList<TextField>();

		ArrayList<Component> allComp = new ArrayList<Component>();	

		private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		private SimpleDateFormat FMonthYear= new SimpleDateFormat("MMMMM-yyyy");

		TextRead txtMonth=new TextRead("");
		TextRead txtSectionID=new TextRead("");
		TextRead txtDepartmentID=new TextRead("");

		CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");

		int index=0;
		String Notify="";
		boolean isUpdate=false;

		public MonthlyAdjustmentNDeduction_CHO(SessionBean sessionBean) 
		{
			this.sessionBean=sessionBean;
			this.setResizable(false);
			this.setCaption("MONTHLY ADJUSTMENT AND DEDUCTION CHO :: " + sessionBean.getCompany());

			buildMainLayout();
			setContent(mainLayout);
			tableinitialise();
			componentIni(true);
			btnIni(true);
			cmbDepartmentAdd();
			SetEventAction();
			button.btnNew.focus();
		}

		private void cmbDepartmentAdd() 
		{
			cmbDepartment.removeAllItems();
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try 
			{
				String query="Select ein.vDepartmentID,dept.vDepartmentName from tbEmployeeInfo ein inner join " +
						"tbDepartmentInfo dept on ein.vDepartmentID=dept.vDepartmentID and dept.vDepartmentID='DEPT10' order by dept.vDepartmentName";
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
				String query="Select ein.vSectionID,sein.SectionName from tbEmployeeInfo ein inner join " +
						"tbSectionInfo sein on ein.vSectionID=sein.vSectionID where " +
						"sein.vDepartmentID='"+cmbDepartment.getValue()+"' order by sein.SectionName";
				List <?> list = session.createSQLQuery(query).list();
				for (Iterator <?> iter = list.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					cmbSection.addItem(element[0].toString());
					cmbSection.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch (Exception ex) 
			{
				showNotification("cmbSectionAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void addEmployeeData() 
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try 
			{
				String query="Select vEmployeeID,employeeCode from tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' " +
						"and vSectionID='"+cmbSection.getValue()+"' and iStatus=1 and ISNULL(vProximityID,'')!=''  order by employeeCode";
				if(opgEmployee.getValue()=="Employee Name")
					query="Select vEmployeeID,vEmployeeName from tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' " +
							"and vSectionID='"+cmbSection.getValue()+"' and iStatus=1 and ISNULL(vProximityID,'')!=''  order by employeeCode";
				else if(opgEmployee.getValue()=="Proximity ID")
					query="Select vEmployeeID,vProximityID from tbEmployeeInfo where vDepartmentID='"+cmbDepartment.getValue()+"' " +
							"and vSectionID='"+cmbSection.getValue()+"' and iStatus=1 and ISNULL(vProximityID,'')!=''  order by employeeCode";

				List <?> list = session.createSQLQuery(query).list();
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

		private boolean tableDataCheck()
		{
			boolean tbRet=false;
			for(int i=0;i<lblAutoEmployeeID.size();i++)
			{
				if((lblAutoEmployeeID.get(i).getValue().toString().equalsIgnoreCase((cmbEmployee.getValue()!=null?cmbEmployee.getValue().toString():"%"))))
				{
					tbRet=false;
					Notify="Employee Already Exist in the table!!!";
					break;
				}
				else
				{
					tbRet=true;
				}
			}
			return tbRet;
		}

		private void tableDataAdd()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName,din.DesignationName," +
						" ein.mOthersAllowance from tbEmployeeInfo ein inner join tbDesignationInfo din on" +
						" ein.vDesignationId=din.designationId where ein.vDepartmentID='"+cmbDepartment.getValue().toString()+"'" +
						" and ein.vSectionId='"+cmbSection.getValue().toString()+"' and vEmployeeId" +
						" like '"+(cmbEmployee.getValue()!=null?cmbEmployee.getValue().toString():"%")+"'" +
						" and iStatus=1 and ISNULL(vProximityID,'')!='' order by ein.employeeCode";

				List <?> lst=session.createSQLQuery(query).list();
				if(!lst.isEmpty())
				{
					boolean checkData=false;
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element=(Object[])itr.next();
						boolean check=false;
						for(int chkindex=0;chkindex<lblAutoEmployeeID.size();chkindex++)
						{
							if(lblAutoEmployeeID.get(chkindex).getValue().toString().equalsIgnoreCase(element[0].toString()))
							{
								check=true;
								index=chkindex;
								break;
							}
							else if(lblAutoEmployeeID.get(chkindex).getValue().toString().isEmpty())
							{
								check=false;
								index=chkindex;
								break;
							}
							else
							{
								check=true;
							}
						}
						if(!check)
						{
							lblAutoEmployeeID.get(index).setValue(element[0].toString());
							lblEmployeeCode.get(index).setValue(element[1].toString());
							lblProximityID.get(index).setValue(element[2].toString());
							lblEmployeeName.get(index).setValue(element[3].toString());
							lblDesignation.get(index).setValue(element[4].toString());
							lblGross.get(index).setValue(decimalFormat.format(Double.parseDouble(element[5].toString())));
							if(index==lblAutoEmployeeID.size()-1)
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
				{
					showNotification("Warning", "No Data Found!!!", Notification.TYPE_WARNING_MESSAGE);

				}
			}
			catch (Exception exp)
			{
				showNotification("tableDataAdd", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void SetEventAction()
		{	
			cmbDepartment.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					cmbSection.removeAllItems();
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
					index=0;
					cmbEmployee.removeAllItems();
					chkEmployeeAll.setValue(false);
					tableClear();
					if(cmbSection.getValue()!=null)
					{
						addEmployeeData();
					}
				}
			});

			opgEmployee.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					addEmployeeData();
				}
			});

			cmbEmployee.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(cmbSection.getValue()!=null)
					{
						if(tableDataCheck())
							tableDataAdd();
						else
							showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
					}
				}
			});

			chkEmployeeAll.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					tableClear();
					index=0;
					if(chkEmployeeAll.booleanValue())
					{
						cmbEmployee.setValue(null);
						cmbEmployee.setEnabled(false);
						if(tableDataCheck())
							tableDataAdd();
						else
							showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
					}
					else
					{
						cmbEmployee.setEnabled(true);
					}
				}
			});

			button.btnNew.addListener( new Button.ClickListener() 
			{
				public void buttonClick(ClickEvent event) 
				{
					componentIni(false);
					btnIni(false);
					txtClear();
					dWorkingDate.focus();
				}
			});

			button.btnSave.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					saveButtonEvent();
				}
			});

			button.btnEdit.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					updateButtonEvent();
				}
			});

			button.btnRefresh.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event) 
				{
					txtClear();
					componentIni(true);
					btnIni(true);
				}
			});

			button.btnFind.addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					findButtonEvent();
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

		private boolean formValidation()
		{
			boolean ret=false;
			int count=0;
			for(int i=0;i<lblAutoEmployeeID.size();i++)
			{
				if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
				{
					if(!txtAdjust.get(i).getValue().toString().isEmpty())
					{
						if(!txtDeduction.get(i).getValue().toString().isEmpty())
						{
							if(!txtReason.get(i).getValue().toString().isEmpty())
							{
								if(!txtPermittedBy.get(i).getValue().toString().isEmpty())
								{
									ret=true;
								}
								else
								{
									txtPermittedBy.get(i).focus();
									Notify="Provide Permitted By!!!";
									ret=false;
								}
							}
							else
							{
								txtReason.get(i).focus();
								Notify="Provide Reason By!!!";
								ret=false;
							}
						}
						else
						{
							txtDeduction.get(i).focus();
							Notify="Provide Deduction By!!!";
							ret=false;
						}
					}
					else
					{
						txtAdjust.get(i).focus();
						Notify="Provide Adjust By!!!";
						ret=false;
					}
					count++;
				}
			}
			if(count==0)
				Notify="No Data Found!!!";
			return ret;
		}

		private boolean chkSaveButtonEvent(String query)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				List <?> lst=session.createSQLQuery(query).list();
				if(!lst.isEmpty())
					return true;
			}
			catch (Exception exp)
			{
				showNotification("saveButtonEvent", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
			return false;
		}

		private void saveButtonEvent()
		{
			if(formValidation())
			{
				if(isUpdate && sessionBean.isUpdateable())
				{
					String query="select * from tbSalary where MONTH(dDate)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"') " +
							" and YEAR(dDate)=YEAR('"+dFormat.format(dWorkingDate.getValue())+"') and SectionID='"+cmbSection.getValue().toString()+"'";
					if(!chkSaveButtonEvent(query))
					{	
						MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
						mb.show(new EventListener()
						{
							public void buttonClicked(ButtonType buttonType)
							{
								if(buttonType == ButtonType.YES)
								{
									if(deleteData())
										insertData();
									txtClear();
									componentIni(true);
									btnIni(true);
									showNotification("All Information Updated Successfully");
								}
							}
						});
					}
					else
					{
						showNotification("Warning", "Salary Already Generated for the Month of"+FMonthYear.format(dWorkingDate.getValue()), Notification.TYPE_WARNING_MESSAGE);
					}
					isUpdate=false;
				}

				else if (sessionBean.isUpdateable())
				{
					String query="select * from tbSalary where MONTH(dDate)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"') " +
							" and YEAR(dDate)=YEAR('"+dFormat.format(dWorkingDate.getValue())+"') and SectionID='"+cmbSection.getValue().toString()+"'";
					if(!chkSaveButtonEvent(query))
					{
						String query1 = "select * from tbAdjustment_Deduction where vSectionID='"+cmbSection.getValue()+"' " +
								"and MONTH(dDate)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"') " +
								"and YEAR(dDate)=YEAR('"+dFormat.format(dWorkingDate.getValue())+"')";
						if(!chkSaveButtonEvent(query1))
						{
							MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
							mb.show(new EventListener()
							{
								public void buttonClicked(ButtonType buttonType)
								{
									if(buttonType == ButtonType.YES)
									{
										insertData();
										txtClear();
										componentIni(true);
										btnIni(true);
										showNotification("All Information Saved Successfully");
									}
								}
							});
						}
						else
						{
							showNotification("Warning", "Data Already Exist!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning", "Salary Already Generated for the Month of"+FMonthYear.format(dWorkingDate.getValue()), Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
			else
			{
				showNotification("Warning", Notify, Notification.TYPE_WARNING_MESSAGE);
			}
		}

		private void updateButtonEvent()
		{
			if(!lblAutoEmployeeID.get(0).toString().equals(""))
			{
				isUpdate=true;
				componentIni(false);
				btnIni(false);
			}
			else
			{
				showNotification("Warning!","There are nothing to edit", Notification.TYPE_WARNING_MESSAGE);
			}
		}

		private void insertData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{
				for(int i=0; i<lblAutoEmployeeID.size();i++)
				{
					if(!lblAutoEmployeeID.get(i).getValue().toString().isEmpty())
					{
						String query="insert into tbAdjustment_Deduction (dDate,vEmployeeID,employeeCode,vProximityID,vEmployeeName," +
								" vDesignationName,vDepartmentID,vDepartmentName,vSectionID,vSectionName,mGross,mAdjustment,mDeduction,vReason,vPermittedBy," +
								" vUserName,vUserIP,dEntryTime) values ('"+dFormat.format(dWorkingDate.getValue())+"'," +
								" '"+lblAutoEmployeeID.get(i).getValue().toString()+"'," +
								" '"+lblEmployeeCode.get(i).getValue().toString()+"'," +
								" '"+lblProximityID.get(i).getValue().toString()+"'," +
								" '"+lblEmployeeName.get(i).getValue().toString()+"'," +
								" '"+lblDesignation.get(i).getValue().toString()+"'," +
								" '"+cmbDepartment.getValue().toString()+"'," +
								" '"+cmbDepartment.getItemCaption(cmbDepartment.getValue())+"'," +
								" '"+cmbSection.getValue().toString()+"'," +
								" '"+cmbSection.getItemCaption(cmbSection.getValue())+"'," +
								" '"+lblGross.get(i).getValue().toString()+"'," +
								" '"+txtAdjust.get(i).getValue().toString().replaceAll(",", "")+"'," +
								" '"+txtDeduction.get(i).getValue().toString().replaceAll(",", "")+"'," +
								" '"+txtReason.get(i).getValue()+"'," +
								"'"+txtPermittedBy.get(i).getValue()+"'," +
								"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',getDate())";
						session.createSQLQuery(query).executeUpdate();
					}
				}
				tx.commit();
			}
			catch (Exception exp)
			{
				tx.rollback();
				showNotification("Warning", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
			}
			finally{session.close();}
		}

		private boolean deleteData()
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			Transaction tx = session.beginTransaction();
			try
			{
				String Updatequery = "insert into tbUDAdjustment_Deduction (dDate,vEmployeeID,employeeCode,vProximityID," +
						" vEmployeeName,vDesignationName,vDepartmentID,vDepartmentName,vSectionID,vSectionName,mGross,mAdjustment,mDeduction,vReason," +
						" vPermittedBy,vUDFlag,vUserName,vUserIP,dEntryTime) select dDate,vEmployeeID,employeeCode,vProximityID," +
						" vEmployeeName,vDesignationName,vDepartmentID,vDepartmentName,vSectionID,vSectionName,mGross,mAdjustment,mDeduction,vReason," +
						" vPermittedBy,'UPDATED',vUserName,vUserIP,dEntryTime from tbAdjustment_Deduction where " +
						" MONTH(dDate)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"') and YEAR(dDate)=YEAR('"+dFormat.format(dWorkingDate.getValue())+"') " +
						" and vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionID='"+cmbSection.getValue()+"'";
				session.createSQLQuery(Updatequery).executeUpdate();

				session.createSQLQuery("delete from tbAdjustment_Deduction where Month(dDate)=MONTH('"+dFormat.format(dWorkingDate.getValue())+"')" +
						" and Year(dDate)=YEAR('"+dFormat.format(dWorkingDate.getValue())+"') and vDepartmentID='"+cmbDepartment.getValue()+"' and vSectionID='"+cmbSection.getValue()+"'").executeUpdate();

				tx.commit();
			}
			catch (Exception exp)
			{
				tx.rollback();
				showNotification("Warning", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
			}
			finally{session.close();}
			return true;
		}

		private void findButtonEvent()
		{
			Window win = new MonthlyAdjustmentNDeductionFind_CHO(sessionBean, txtMonth, txtDepartmentID, txtSectionID);
			win.addListener(new Window.CloseListener() 
			{
				public void windowClose(CloseEvent e) 
				{
					if (txtMonth.getValue().toString().length() > 0)
					{
						txtClear();
						findInitialise(txtMonth.getValue().toString(), txtDepartmentID.getValue().toString(), txtSectionID.getValue().toString());
					}
				}
			});

			this.getParent().addWindow(win);
		}

		private void findInitialise(String strMonth, String strDepartmentID, String strSectionID)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				String query="select dDate,vEmployeeID,employeeCode,vProximityID,vEmployeeName,vDesignationName," +
						"vDepartmentID,vDepartmentName,vSectionID,vSectionName,mGross,mAdjustment,mDeduction,vReason," +
						"vPermittedBy from tbAdjustment_Deduction where MONTH(dDate)=MONTH('"+strMonth+"') and " +
						"YEAR(dDate)=YEAR('"+strMonth+"') and vDepartmentID='"+strDepartmentID+"' and " +
						"vSectionID='"+strSectionID+"' order by employeeCode";
				System.out.println(query);
				List <?> lst=session.createSQLQuery(query).list();
				if(!lst.isEmpty())
				{
					int i=0;
					for(Iterator <?> itr=lst.iterator();itr.hasNext();)
					{
						Object [] element = (Object[])itr.next();
						if(i==0)
						{
							dWorkingDate.setValue(element[0]);
							cmbDepartment.setValue(element[6]);
							cmbSection.setValue(element[8]);
						}
						lblAutoEmployeeID.get(i).setValue(element[1].toString());
						lblEmployeeCode.get(i).setValue(element[2].toString());
						lblProximityID.get(i).setValue(element[3].toString());
						lblEmployeeName.get(i).setValue(element[4].toString());
						lblDesignation.get(i).setValue(element[5].toString());
						lblGross.get(i).setValue(decimalFormat.format(Double.parseDouble(element[10].toString())));
						txtAdjust.get(i).setValue(decimalFormat.format(Double.parseDouble(element[11].toString())));
						txtDeduction.get(i).setValue(decimalFormat.format(Double.parseDouble(element[12].toString())));
						txtReason.get(i).setValue(element[13]);
						txtPermittedBy.get(i).setValue(element[14]);
						if(i==lblAutoEmployeeID.size()-1)
							tableRowAdd(i+1);
						i++;
					}
				}
			}
			catch (Exception exp)
			{
				showNotification("findInitialise",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}

		private void componentIni(boolean b) 
		{
			dWorkingDate.setEnabled(!b);
			cmbDepartment.setEnabled(!b);
			cmbSection.setEnabled(!b);
			cmbEmployee.setEnabled(!b);
			chkEmployeeAll.setEnabled(!b);
			table.setEnabled(!b);
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
				lblGross.get(i).setValue("");
				txtAdjust.get(i).setValue("");
				txtDeduction.get(i).setValue("");
				txtReason.get(i).setValue("");
				txtPermittedBy.get(i).setValue("");
			}
		}

		private AbsoluteLayout buildMainLayout() 
		{
			mainLayout = new AbsoluteLayout();
			mainLayout.setImmediate(true);
			mainLayout.setMargin(false);
			mainLayout.setWidth("1043px");
			mainLayout.setHeight("480px");

			lblDate = new Label("Month :");
			lblDate.setImmediate(false);
			lblDate.setWidth("-1px");
			lblDate.setHeight("-1px");
			mainLayout.addComponent(lblDate, "top:20.0px; left:20.0px;");

			dWorkingDate = new PopupDateField();
			dWorkingDate.setImmediate(true);
			dWorkingDate.setWidth("140px");
			dWorkingDate.setDateFormat("MMMMM-yyyy");
			dWorkingDate.setValue(new java.util.Date());
			dWorkingDate.setResolution(PopupDateField.RESOLUTION_MONTH);
			mainLayout.addComponent(dWorkingDate, "top:18.0px; left:140.0px;");

			cmbDepartment = new ComboBox();
			cmbDepartment.setImmediate(true);
			cmbDepartment.setWidth("280px");
			cmbDepartment.setHeight("24px");
			cmbDepartment.setNullSelectionAllowed(true);
			cmbDepartment.setNewItemsAllowed(false);
			cmbDepartment.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(new Label("Department Name : "), "top:45.0px; left:20.0px;");
			mainLayout.addComponent(cmbDepartment, "top:43.0px; left:140.0px;");

			lblSection = new Label("Section Name :");
			lblSection.setImmediate(false); 
			lblSection.setWidth("-1px");
			lblSection.setHeight("-1px");
			mainLayout.addComponent(lblSection, "top:20.0px; left:480.0px;");

			cmbSection = new ComboBox();
			cmbSection.setImmediate(true);
			cmbSection.setWidth("280px");
			cmbSection.setHeight("24px");
			cmbSection.setNullSelectionAllowed(true);
			cmbSection.setNewItemsAllowed(false);
			cmbSection.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbSection, "top:18.0px; left:600.0px;");

			opgEmployee = new OptionGroup("",lstEmployee);
			opgEmployee.setImmediate(true);
			opgEmployee.select("Employee ID");
			opgEmployee.setStyleName("horizontal");
			mainLayout.addComponent(opgEmployee, "top:45.0px; left:600.0px;");

			lblEmployee = new Label("Employee ID :");
			lblEmployee.setImmediate(false); 
			lblEmployee.setWidth("-1px");
			lblEmployee.setHeight("-1px");
			mainLayout.addComponent(lblEmployee, "top:70.0px; left:480.0px;");

			cmbEmployee = new ComboBox();
			cmbEmployee.setImmediate(true);
			cmbEmployee.setWidth("220px");
			cmbEmployee.setHeight("24px");
			cmbEmployee.setNullSelectionAllowed(true);
			cmbEmployee.setNewItemsAllowed(false);
			cmbEmployee.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
			mainLayout.addComponent(cmbEmployee, "top:68.0px; left:600.0px;");

			chkEmployeeAll = new CheckBox("All");
			chkEmployeeAll.setImmediate(true);
			chkEmployeeAll.setHeight("-1px");
			chkEmployeeAll.setWidth("-1px");
			mainLayout.addComponent(chkEmployeeAll, "top:70.0px; left:825.0px;");

			table.setWidth("1003px");
			table.setHeight("328px");
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
			table.setColumnWidth("Employee Name",  120);

			table.addContainerProperty("Designation", Label.class, new Label());
			table.setColumnWidth("Designation", 100);

			table.addContainerProperty("Gross", Label.class, new Label());
			table.setColumnWidth("Gross", 60);

			table.addContainerProperty("Adjust", AmountCommaSeperator.class, new AmountCommaSeperator());
			table.setColumnWidth("Adjust", 60);

			table.addContainerProperty("Deduction", AmountCommaSeperator.class, new AmountCommaSeperator());
			table.setColumnWidth("Deduction", 60);

			table.addContainerProperty("Reason", TextField.class, new TextField());
			table.setColumnWidth("Reason", 120);

			table.addContainerProperty("Permitted By", TextField.class, new TextField());
			table.setColumnWidth("Permitted By", 120);

			table.setColumnCollapsed("EMP ID", true);
			table.setColumnAlignments(new String[] {Table.ALIGN_RIGHT,Table.ALIGN_LEFT, Table.ALIGN_LEFT,
					Table.ALIGN_LEFT,Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_RIGHT, Table.ALIGN_CENTER, 
					Table.ALIGN_CENTER,Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER});

			mainLayout.addComponent(table,"top:110.0px; left:20.0px;");
			mainLayout.addComponent(button,"top:440.0px; left:270.0px");
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
			btnDel.get(ar).addListener(new Button.ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					lblAutoEmployeeID.get(ar).setValue("");
					lblEmployeeCode.get(ar).setValue("");
					lblProximityID.get(ar).setValue("");
					lblEmployeeName.get(ar).setValue("");
					lblDesignation.get(ar).setValue("");
					lblGross.get(ar).setValue("");
					txtAdjust.get(ar).setValue("");
					txtDeduction.get(ar).setValue("");
					txtReason.get(ar).setValue("");
					txtPermittedBy.get(ar).setValue("");

					for(int tbIndex=ar;tbIndex<lblAutoEmployeeID.size()-1;tbIndex++)
					{
						lblAutoEmployeeID.get(tbIndex).setValue(lblAutoEmployeeID.get(tbIndex+1).getValue().toString());
						lblEmployeeCode.get(tbIndex).setValue(lblEmployeeCode.get(tbIndex+1).getValue().toString());
						lblProximityID.get(tbIndex).setValue(lblProximityID.get(tbIndex+1).getValue().toString());
						lblEmployeeName.get(tbIndex).setValue(lblEmployeeName.get(tbIndex+1).getValue().toString());
						lblDesignation.get(tbIndex).setValue(lblDesignation.get(tbIndex+1).getValue().toString());
						lblGross.get(tbIndex).setValue(lblGross.get(tbIndex+1).getValue().toString());
						txtAdjust.get(tbIndex).setValue(txtAdjust.get(tbIndex+1).getValue().toString());
						txtDeduction.get(tbIndex).setValue(txtDeduction.get(tbIndex+1).getValue().toString());
						txtReason.get(tbIndex).setValue(txtReason.get(tbIndex+1).getValue().toString());
						txtPermittedBy.get(tbIndex).setValue(txtPermittedBy.get(tbIndex+1).getValue().toString());

						lblAutoEmployeeID.get(tbIndex+1).setValue("");
						lblEmployeeCode.get(tbIndex+1).setValue("");
						lblProximityID.get(tbIndex+1).setValue("");
						lblEmployeeName.get(tbIndex+1).setValue("");
						lblDesignation.get(tbIndex+1).setValue("");
						lblGross.get(tbIndex+1).setValue("");
						txtAdjust.get(tbIndex+1).setValue("");
						txtDeduction.get(tbIndex+1).setValue("");
						txtReason.get(tbIndex+1).setValue("");
						txtPermittedBy.get(tbIndex+1).setValue("");
						index--;
					}
				}
			});

			lblsl.add(ar,new Label());
			lblsl.get(ar).setWidth("100%");
			lblsl.get(ar).setHeight("16px");
			lblsl.get(ar).setValue(ar+1);

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

			lblGross.add(ar, new Label());
			lblGross.get(ar).setWidth("100%");

			txtAdjust.add(ar, new AmountCommaSeperator());
			txtAdjust.get(ar).setWidth("100%");

			txtDeduction.add(ar, new AmountCommaSeperator());
			txtDeduction.get(ar).setWidth("100%");

			txtReason.add(ar, new TextField());
			txtReason.get(ar).setWidth("100%");

			txtPermittedBy.add(ar, new TextField());
			txtPermittedBy.get(ar).setWidth("100%");

			table.addItem(new Object[]{btnDel.get(ar),lblsl.get(ar),lblAutoEmployeeID.get(ar),lblEmployeeCode.get(ar),
					lblProximityID.get(ar),lblEmployeeName.get(ar),lblDesignation.get(ar),lblGross.get(ar),
					txtAdjust.get(ar),txtDeduction.get(ar),txtReason.get(ar),txtPermittedBy.get(ar)},ar);
		}
}
