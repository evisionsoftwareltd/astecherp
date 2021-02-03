package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import java.text.SimpleDateFormat;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class LeaveBalance extends Window 
{
	private AbsoluteLayout mainLayout;
	private Label lblYear = new Label("Year :");
	private InlineDateField dYear = new InlineDateField();
	private Label lblDeptName = new Label("Dept. Name :");
	private ComboBox cmbDeptName = new ComboBox();
	private Label lblSecName = new Label("Sec. Name :");
	private ComboBox cmbSecName = new ComboBox();
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
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy");
	private Table table = new Table();
	private ArrayList<Label> lbSl = new ArrayList<Label>();
	private ArrayList<Label> lblEmpID=new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeID = new ArrayList<Label>();
	private ArrayList<Label> lbEmployeeName = new ArrayList<Label>();
	private ArrayList<AmountField> amtCL = new ArrayList<AmountField>();
	private ArrayList<AmountField> amtSL = new ArrayList<AmountField>();
	private ArrayList<AmountField> amtAL = new ArrayList<AmountField>();
	private ArrayList<AmountField> amtML = new ArrayList<AmountField>();
	private CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");
	ArrayList<Component> allComp = new ArrayList<Component>();

	public LeaveBalance(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("LEAVE OPENING BALANCE :: "+sessionBean.getCompany());
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
		table.setHeight("308px");
		table.setPageLength(0);

		table.addContainerProperty("SL #", Label.class , new Label());
		table.setColumnWidth("SL #",20);

		table.addContainerProperty("EMP ID", Label.class , new Label());
		table.setColumnWidth("EMP ID",50);

		table.addContainerProperty("Employee ID", Label.class, new Label());
		table.setColumnWidth("Employee ID", 70);
		
		table.addContainerProperty("Employee Name", Label.class , new Label());
		table.setColumnWidth("Employee Name",225);

		table.addContainerProperty("CL", AmountField.class , new AmountField());
		table.setColumnWidth("CL",33);	

		table.addContainerProperty("SL", AmountField.class , new AmountField());
		table.setColumnWidth("SL",33);

		table.addContainerProperty("EL", AmountField.class , new AmountField());
		table.setColumnWidth("EL",33);	

		table.addContainerProperty("ML", AmountField.class , new AmountField());
		table.setColumnWidth("ML",33);	
		
		table.setColumnCollapsed("EMP ID", true);

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
		lbEmployeeID.get(ar).setImmediate(true);
		lbEmployeeID.get(ar).setHeight("-1px");

		lbEmployeeName.add(ar, new Label(""));
		lbEmployeeName.get(ar).setWidth("100%");
		lbEmployeeName.get(ar).setImmediate(true);

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
				amtML.get(ar).focus();
			}
		});

		amtML.add(ar, new AmountField());
		amtML.get(ar).setWidth("100%");
		amtML.get(ar).setImmediate(true);
		amtML.get(ar).setStyleName("amount");
		amtML.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				amtCL.get(ar+1).focus();
			}
		});
		table.addItem(new Object[]{lbSl.get(ar),lblEmpID.get(ar),lbEmployeeID.get(ar),lbEmployeeName.get(ar),amtCL.get(ar),amtSL.get(ar),amtAL.get(ar),amtML.get(ar)},ar);
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
			cmbDeptName.setEnabled(!b);
			cmbSecName.setEnabled(!b);
		}
		else
		{
			cmbDeptName.setEnabled(b);
			cmbSecName.setEnabled(b);
		}
		table.setEnabled(!b);
	}

	private void txtClear()
	{
		dYear.setValue(new java.util.Date());
		cmbDeptName.setValue(null);
		cmbSecName.setValue(null);

		for(int i =0; i<lbEmployeeID.size(); i++)
		{
			lbEmployeeID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			amtCL.get(i).setValue("");
			amtSL.get(i).setValue("");
			amtAL.get(i).setValue("");
			amtML.get(i).setValue("");
		}
	}

	private void tableclear()
	{
		for(int i =0; i<lbEmployeeID.size(); i++)
		{
			lbEmployeeID.get(i).setValue("");
			lbEmployeeName.get(i).setValue("");
			amtCL.get(i).setValue("");
			amtSL.get(i).setValue("");
			amtAL.get(i).setValue("");
			amtML.get(i).setValue("");
		}
	}

	private void focusEnter()
	{
		allComp.add(cmbDeptName);
		for(int i=0; i<lbEmployeeName.size();i++)
		{
			allComp.add(amtCL.get(i));
			allComp.add(amtSL.get(i));
			allComp.add(amtAL.get(i));
			allComp.add(amtML.get(i));
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
						   "tbEmployeeInfo ein on ein.vDepartmentId=dept.vDepartmentId where (vEmployeeType='Permanent' " +
						   "or vEmployeeType='Provisionary') order by dept.vDepartmentName";
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
			showNotification("departMentdataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void sectiondataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select distinct ein.vSectionId,sein.SectionName from tbSectionInfo sein inner join " +
						   "tbEmployeeInfo ein on sein.vSectionId=ein.vSectionId where (vEmployeeType='Permanent' " +
						   "or vEmployeeType='Provisionary') and ein.vDepartmentID='"+cmbDeptName.getValue()+"' " +
						   "order by sein.SectionName";
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
			showNotification("sectiondataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
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
				if(cmbDeptName.getValue()!=null )
				{
					sectiondataAdd();
				}
			}
		});
		
		cmbSecName.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbSecName.getValue()!=null )
				{
					if(isFind==false)
					{
						tableclear();
						tableDataAdd();
						amtCL.get(0).focus();
					}
				}
			}
		});

		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
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
				if(cmbDeptName.getValue()!=null)
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
					cmbDeptName.focus();
				}
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind=false;
				refreshButtonEvent();
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

	private void updateAction() 
	{
		if (!lbEmployeeID.get(0).getValue().toString().isEmpty()) 
		{
			btnIni(false);
			componentIni(false);
		} 
		else
		{
			showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();	
	}

	private void tableDataAdd()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query =  " SELECT vEmployeeId, employeeCode,vEmployeeName FROM tbEmployeeInfo ein where vDepartmentId='"+cmbDeptName.getValue().toString()+"' " +
					" and vSectionID = '"+cmbSecName.getValue()+"' and iStatus = '1' and vEmployeeId IN (Select vAutoEmployeeId from tbLeaveBalanceNew lb where iflag='0' " +
					" and lb.vDepartmentID=ein.vDepartmentID and lb.vSectionId=ein.vSectionId) and (vEmployeeType='Permanent' or vEmployeeType='Provisionary') order by " +
					" CONVERT(int, substring(vEmployeeId,4,len(vEmployeeId)))";
			List <?> list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator <?> iter = list.iterator(); iter.hasNext();)
				{				  
					Object[] element = (Object[]) iter.next();
					lblEmpID.get(i).setValue(element[0]);
					lbEmployeeID.get(i).setValue(element[1]);
					lbEmployeeName.get(i).setValue(element[2]);
					if((i)==lbEmployeeID.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				showNotification("Warning!","Balance already exist or no employee found", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			showNotification("tableDataAdd", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void newButtonEvent() 
	{
		cmbDeptName.focus();
		componentIni(false);
		btnIni(false);
		txtClear();
	}

	private void findButtonEvent()
	{
		Window win = new FindLeaveBalance(sessionBean, trIDDeptID);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(trIDDeptID.getValue().toString().length()>0)
				{
					strEmpDeptID = trIDDeptID.getValue().toString();
					strEmpID = strEmpDeptID.substring(0, strEmpDeptID.indexOf("^"));
					strDeptID = strEmpDeptID.substring(strEmpDeptID.indexOf("^")+1, strEmpDeptID.length());
					findInitialize(strEmpID, strDeptID);
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void findInitialize(String EmpID, String DeptID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{			
			String updatequery = " Select E.vEmployeeId, E.employeeCode, E.vEmployeeName, p.vDepartmentID,S.vSectionID, P.iClyBalance, P.iSlyBalance, P.iAlyBalance, P.iMlyBalance " +
					" FROM tbLeaveBalanceNew as P INNER JOIN tbEmployeeInfo as E ON E.vEmployeeId = P.vAutoEmployeeId INNER JOIN" +
					" tbSectionInfo as S ON E.vSectionID = S.vSectionID  WHERE P.vAutoEmployeeId='"+EmpID+"'";
			List <?> list = session.createSQLQuery(updatequery).list();

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
					amtCL.get(0).setValue(element[5]);
					amtSL.get(0).setValue(element[6]);
					amtAL.get(0).setValue(element[7]);
					amtML.get(0).setValue(element[8]);
				}
			}
			else
			{
				tableclear();
				showNotification("Warning!","Balance Already Exist", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			showNotification("findInitialize", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveButtonAction()
	{
		try
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
								Session session = SessionFactoryUtil.getInstance().openSession();
								Transaction tx = session.beginTransaction();
								insertData(tx,session);
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
								Session session = SessionFactoryUtil.getInstance().openSession();
								Transaction tx = session.beginTransaction();
								insertData(tx,session);
							}
						}
					});
				}
			}
		}
		catch(Exception ex)
		{
			showNotification("Error.", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void insertData (Transaction tx, Session session)
	{
		try
		{
			year = dateformat.format(dYear.getValue());
			String Updatequery = "";

			for(int i=0; i<lbEmployeeID.size(); i++)
			{
				if(!lbEmployeeID.get(i).getValue().toString().isEmpty())
				{
					if(!amtCL.get(i).getValue().toString().isEmpty() || !amtSL.get(i).getValue().toString().isEmpty() ||
							!amtAL.get(i).getValue().toString().isEmpty() || !amtML.get(i).getValue().toString().isEmpty())
					{
						Updatequery = " UPDATE tbLeaveBalanceNew SET" +
								" iClyBalance='"+amtCL.get(i).getValue()+"'," +
								" iSlyBalance ='"+amtSL.get(i).getValue()+"'," +
								" iAlyBalance = '"+amtAL.get(i).getValue()+"'," +
								" iMlyBalance = '"+amtML.get(i).getValue()+"'," +
								" iflag = '1'," +
								" userName = '"+sessionBean.getUserName()+"'," +
								" entryTime = CURRENT_TIMESTAMP," +
								" userIp = '"+sessionBean.getUserIp()+"'" +
								" WHERE vAutoEmployeeId = '"+lblEmpID.get(i).getValue()+"'" +
								" and YEAR(currentYear) = Year(CURRENT_TIMESTAMP)";

						session.createSQLQuery(Updatequery).executeUpdate();
					}
				}
			}
			tx.commit();
			this.getParent().showNotification("All Information Save Successfully");
			isUpdate=false;
			isFind = false;
			txtClear();
			componentIni(true);
			btnIni(true);
		}
		catch(Exception ex)
		{
			showNotification("insertData", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("600px");
		setHeight("470px");

		lblYear = new Label();
		lblYear.setImmediate(true);
		lblYear.setWidth("-1px");
		lblYear.setHeight("-1px");
		lblYear.setValue("Opening Year :");
		mainLayout.addComponent(lblYear, "top:15.0px;left:20.0px;");

		dYear = new InlineDateField();
		dYear.setValue(new java.util.Date());
		dYear.setWidth("110px");
		dYear.setHeight("24px");
		dYear.setResolution(InlineDateField.RESOLUTION_YEAR);
		dYear.setDateFormat("yyyy");
		dYear.setInvalidAllowed(false);
		dYear.setImmediate(true);
		mainLayout.addComponent(dYear, "top:13.0px;left:110.0px;");

		lblDeptName = new Label();
		lblDeptName.setImmediate(true);
		lblDeptName.setWidth("-1px");
		lblDeptName.setHeight("-1px");
		lblDeptName.setValue("Department Name :");
		mainLayout.addComponent(lblDeptName, "top:15.0px;left:240.0px;");

		cmbDeptName = new ComboBox();
		cmbDeptName.setImmediate(true);
		cmbDeptName.setWidth("210px");
		cmbDeptName.setHeight("24px");
		cmbDeptName.setImmediate(true);
		cmbDeptName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbDeptName, "top:13.0px;left:360.0px;");
		
		lblSecName = new Label();
		lblSecName.setImmediate(true);
		lblSecName.setWidth("-1px");
		lblSecName.setHeight("-1px");
		lblSecName.setValue("Section Name :");
		mainLayout.addComponent(lblSecName, "top:40.0px;left:240.0px;");

		cmbSecName = new ComboBox();
		cmbSecName.setImmediate(true);
		cmbSecName.setWidth("210px");
		cmbSecName.setHeight("24px");
		cmbSecName.setImmediate(true);
		cmbSecName.setNullSelectionAllowed(false);
		mainLayout.addComponent(cmbSecName, "top:38.0px;left:360.0px;");

		mainLayout.addComponent(table, "top:70.0px;left:20.0px;");
		mainLayout.addComponent(button, "top:390.0px;left:60.0px;");

		return mainLayout;
	}
}
