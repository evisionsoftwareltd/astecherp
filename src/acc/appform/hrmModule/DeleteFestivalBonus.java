package acc.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class DeleteFestivalBonus extends Window
{
	private AbsoluteLayout mainLayout;
	private SessionBean sessionBean;

	private Label lblDepartment;
	private ComboBox cmbDepartment;
	private CheckBox chkDepartmentAll;

	private Label lblSection;
	private ComboBox cmbSection;
	private CheckBox chkSectionAll;

	private Label lblemployeeName;
	private ComboBox cmbEmployeeName;
	private CheckBox chkEmployeeAll;
	private Label lblSalaryMonth;
	private ComboBox cmbSalaryMonth;
	private ProgressIndicator PI;
	private Worker1 worker1;
	private ArrayList<Component> allComp = new ArrayList<Component>();	
	private CommonButton cButton = new CommonButton("", "", "", "Delete", "", "", "", "", "", "");
	private SimpleDateFormat dMonth = new SimpleDateFormat("MMMMM");

	String username = "";
	String section = "";
	String department = "";
	String employee = "";
	String vMonthName="";
	String yearName="";
	int clickCount=0;

	public DeleteFestivalBonus(SessionBean sessionBean) 
	{
		this.sessionBean= sessionBean;
		this.setCaption("DELETE FESTIVAL BONUS :: " +sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		componentIni(true);
		cmbSalaryMonthData();
		setEventAction();
		authenticationCheck();
		focusEnter();
		cmbSalaryMonth.focus();
	}


	private void authenticationCheck()
	{
		if(!sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(false);
		}
	}

	private void setEventAction()
	{
		cButton.btnDelete.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		cmbSection.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(cmbSection.getValue()!=null)
				{
					cmbEmployeeNameDataAdd(cmbSection.getValue().toString());
				}
			}
		});

		chkSectionAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				cmbEmployeeName.removeAllItems();
				if(chkSectionAll.booleanValue())
				{
					cmbSection.setEnabled(false);
					cmbSection.setValue(null);
					cmbEmployeeNameDataAdd("%");
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		chkEmployeeAll.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(chkEmployeeAll.booleanValue())
				{
					cmbEmployeeName.setEnabled(false);
					cmbEmployeeName.setValue(null);
				}
				else
				{
					cmbEmployeeName.setEnabled(true);
				}
			}
		});

		cmbSalaryMonth.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(cmbSalaryMonth.getValue()!=null)
				{
					StringTokenizer strToken=new StringTokenizer(cmbSalaryMonth.getItemCaption(cmbSalaryMonth.getValue()), "- ");
					vMonthName=strToken.nextToken();
					yearName=strToken.nextToken();
					cmbDepartmentData();
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
					cmbSectionData(cmbDepartment.getValue().toString());
			}
		});

		chkDepartmentAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(chkDepartmentAll.booleanValue())
				{
					cmbDepartment.setValue(null);
					cmbDepartment.setEnabled(false);
					cmbSectionData("%");
				}
				else
				{
					cmbDepartment.setEnabled(true);
				}
			}
		});
	}

	private void cmbSalaryMonthData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct  CONVERT( varchar (50),DateName(MM,dBonusDate)) gg,YEAR(dBonusDate) year,dBonusDate from tbEmployeeBonus where  vDepartmentName !='CHO' order by dBonusDate desc";
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSalaryMonth.addItem( element[2]);
				cmbSalaryMonth.setItemCaption( element[2], element[0].toString()+"-"+element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSalaryMonthData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbEmployeeNameDataAdd(String SectionID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			System.out.println("Month Name : "+vMonthName+"Year Name"+yearName);
			String query="select vEmployeeID, vEmployeeCode from tbEmployeeBonus where CONVERT( varchar (50),DateName(MM,dBonusDate))='"+vMonthName+"' and year(dBonusDate)='"+yearName+"' and vSectionID like '"+SectionID+"' ";
			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				for(Iterator <?> itr=lst.iterator();itr.hasNext();)
				{
					Object [] element=(Object[]) itr.next();
					cmbEmployeeName.addItem(element[0]);
					cmbEmployeeName.setItemCaption(element[0], element[1].toString());
				}
			}
			else
			{
				showNotification("Warning", "No Employee Found!!!", Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbEmployeeNameDataAdd",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidation()
	{
		if(cmbSalaryMonth.getValue()!=null)
		{
			if(cmbDepartment.getValue()!=null || chkDepartmentAll.booleanValue())
			{
				if(cmbSection.getValue()!=null || chkSectionAll.booleanValue())
				{
					if(cmbEmployeeName.getValue()!=null || chkEmployeeAll.booleanValue())
					{
						deleteBonus();
					}
					else
					{
						showNotification("Warning!","Please Select Employee Name",Notification.TYPE_WARNING_MESSAGE);
						cmbEmployeeName.focus();
					}
				}
				else
				{
					showNotification("Warning!","Please Select Section",Notification.TYPE_WARNING_MESSAGE);
					cmbSection.focus();
				}
			}
			else
			{
				showNotification("Warning!","Please Select Department",Notification.TYPE_WARNING_MESSAGE);
				cmbSection.focus();
			}
		}
		else
		{
			showNotification("Warning!","Please Select Bonus Month",Notification.TYPE_WARNING_MESSAGE);
			cmbSalaryMonth.focus();
		}
	}

	private boolean chkSalary(String query)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> empList = session.createSQLQuery(query).list();
			if(!empList.isEmpty())
				return true;
		}
		catch (Exception exp)
		{

		}
		finally{session.close();}
		return false;
	}

	private void deleteBonus()
	{
		String query=null;
		String empQuery=null;
		employee="%";
		if(chkDepartmentAll.booleanValue())
		{
			department="%";
		}
		else
		{
			department = cmbDepartment.getValue().toString();
		}
		if(chkSectionAll.booleanValue())
		{
			section="%";
		}
		else
		{
			section = cmbSection.getValue().toString();
		}
		if(!chkEmployeeAll.booleanValue())
		{
			employee=cmbEmployeeName.getValue().toString();
		}

		empQuery = " select * from tbEmployeeInfo where vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' and vSectionId like '"+section+"' and iStatus=1 and ISNULL(vProximityID,'')!=''";

		if(chkSalary(empQuery))
		{
			query = "select * from tbEmployeeBonus where DateName(MM,dBonusDate)= '"+vMonthName+"' AND year(dBonusDate)= '"+yearName+"' AND vDepartmentID like '"+(cmbDepartment.getValue()!=null?cmbDepartment.getValue():"%")+"' and vSectionId like '"+section+"' and vEmployeeID like '"+employee+"'";
			if(chkSalary(query))
			{
				clickCount=0;
				System.out.println("Click Count 1: "+clickCount);
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete salary?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							if(clickCount==0)
							{
								clickCount++;
								Session session=SessionFactoryUtil.getInstance().openSession();
								Transaction tx=session.beginTransaction();
								try
								{
									String prcDeleteBonus = "exec prcDeleteBonus '"+cmbSalaryMonth.getValue()+"','"+department+"','"+section+"','"+employee+"'"; 

									System.out.println("Procedure :"+prcDeleteBonus);
									session.createSQLQuery(prcDeleteBonus).executeUpdate();
									
									

									String deleteTracking="insert into tbUDEmployeeBonus(dGenerateDate,dBonusDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName," +
											"vDesignationID,vDesignationName,vDepartmentId,vDepartmentName,vSectionID,vSectionName,mGrossSalary,dJoiningDate," +
											"lengthOfService,mBonusAmt,UDFlag,vOccasion,vUserName,vUserIP,dEntryTime) select dGenerateDate,dBonusDate,vEmployeeID,vEmployeeCode,vProximityID,vEmployeeName," +
											"vDesignationID,vDesignationName,vDepartmentId,vDepartmentName,vSectionID,vSectionName,mGrossSalary,dJoiningDate," +
											"lengthOfService,mBonusAmt,'DELETE',vOccasion,vUserName,vUserIP,dEntryTime from tbDeleteEmployeeBonus where DateName(MM,dBonusDate)= '"+vMonthName+"' " +
											"AND year(dBonusDate)= '"+yearName+"' and vDepartmentID like '"+department+"' AND vSectionId like '"+section+"' and " +
											"vEmployeeID like '"+employee+"'";
									
									System.out.println("DeleteQuery"+deleteTracking);
									session.createSQLQuery(deleteTracking).executeUpdate();

									String deleteQuery="delete from tbEmployeeBonus where DateName(MM,dBonusDate)= '"+vMonthName+"' " +
											"AND year(dBonusDate)= '"+yearName+"' and vDepartmentID like '"+department+"' AND vSectionId like '"+section+"' and vEmployeeID like '"+employee+"'";
									session.createSQLQuery(deleteQuery).executeUpdate();
									tx.commit();
								}
								catch (Exception exp)
								{
									tx.rollback();
									showNotification("deleteBonus",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
								}
								finally{session.close();}

								worker1 = new Worker1();
								worker1.start();
								PI.setEnabled(true);
								PI.setValue(0f);
								cButton.btnDelete.setEnabled(false);
								componentIni(false);
							}
							else
							{
								showNotification("Warning", "Please Wait!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
					}
				});
			}
			else
			{
				showNotification("Warning!","Bonus not found for month "+dMonth.format(cmbSalaryMonth.getValue())+" ", Notification.TYPE_WARNING_MESSAGE);
				txtClear();
				componentIni(true);
			}
		}
		else
		{
			showNotification("There are no Employee in this Section",Notification.TYPE_WARNING_MESSAGE);
			txtClear();
			componentIni(true);
		}
	}

	private void cmbDepartmentData() 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vDepartmentID,vDepartmentName from tbEmployeebonus where DateName(MM,dBonusDate)='"+vMonthName+"' and year(dBonusDate)='"+yearName+"' and vDepartmentName!='CHO' order by vDepartmentName";
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], (String) element[1]);
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectionData(String DepartmentID) 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select distinct vsectionID,vSectionName,vDepartmentName from tbEmployeeBonus where vDepartmentID like '"+DepartmentID+"' and  DateName(MM,dBonusDate)='"+vMonthName+"' and year(dBonusDate)='"+yearName+"' and vSectionName !='CHO' order by vSectionName";
			List <?> list=session.createSQLQuery(query).list();	

			for(Iterator <?> iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[2].toString()+"("+element[1].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectionData",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(cmbSalaryMonth);
		allComp.add(cmbSection);
		allComp.add(cmbEmployeeName);
		allComp.add(cButton.btnDelete);
		new FocusMoveByEnter(this,allComp);
	}

	private void txtClear()
	{
		cmbSalaryMonth.setValue(null);
		cmbDepartment.setValue(null);
		cmbSection.setValue(null);
		cmbEmployeeName.setValue(null);
		chkEmployeeAll.setValue(false);
		chkSectionAll.setValue(false);
		chkDepartmentAll.setValue(false);
	}

	private void componentIni(boolean b) 
	{
		cmbSalaryMonth.setEnabled(b);
		cmbDepartment.setEnabled(b);
		cmbSection.setEnabled(b);
		cmbEmployeeName.setEnabled(b);
		chkEmployeeAll.setEnabled(b);
		chkSectionAll.setEnabled(b);
		chkDepartmentAll.setEnabled(b);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("530px");
		setHeight("280px");

		// lblSalaryMonth
		lblSalaryMonth = new Label("Month :");
		lblSalaryMonth.setImmediate(false);
		lblSalaryMonth.setWidth("-1px");
		lblSalaryMonth.setHeight("-1px");
		mainLayout.addComponent(lblSalaryMonth, "top:40.0px;left:30.0px;");

		// cmbSalaryMonth
		cmbSalaryMonth = new ComboBox();
		cmbSalaryMonth.setImmediate(true);
		cmbSalaryMonth.setWidth("150px");
		cmbSalaryMonth.setHeight("-1px");
		//		cmbSalaryMonth.setValue(new java.util.Date());
		mainLayout.addComponent(cmbSalaryMonth, "top:38.0px;left:150.0px;");

		// lblDepartment
		lblDepartment = new Label("Department :");
		lblDepartment.setImmediate(false);
		lblDepartment.setWidth("-1px");
		lblDepartment.setHeight("-1px");
		mainLayout.addComponent(lblDepartment, "top:70.0px;left:30.0px;");

		// cmbDepartment
		cmbDepartment= new ComboBox();
		cmbDepartment.setWidth("320px");
		cmbDepartment.setHeight("-1px");
		cmbDepartment.setImmediate(true);
		mainLayout.addComponent(cmbDepartment, "top:68.0px;left:150.0px;");

		// chkDepartmentAll
		chkDepartmentAll = new CheckBox("All");
		chkDepartmentAll.setImmediate(true);
		chkDepartmentAll.setWidth("-1px");
		chkDepartmentAll.setHeight("-1px");
		//mainLayout.addComponent(chkDepartmentAll, "top:70.0px;left:473.0px;");

		// lblSection
		lblSection = new Label("Section :");
		lblSection.setImmediate(false);
		lblSection.setWidth("-1px");
		lblSection.setHeight("-1px");
		mainLayout.addComponent(lblSection, "top:100.0px;left:30.0px;");

		// cmbSection
		cmbSection= new ComboBox();
		cmbSection.setWidth("320px");
		cmbSection.setHeight("-1px");
		cmbSection.setImmediate(true);
		mainLayout.addComponent(cmbSection, "top:98.0px;left:150.0px;");

		// chkSectionAll
		chkSectionAll = new CheckBox("All");
		chkSectionAll.setImmediate(true);
		chkSectionAll.setWidth("-1px");
		chkSectionAll.setHeight("-1px");
		mainLayout.addComponent(chkSectionAll, "top:100.0px;left:473.0px;");

		lblemployeeName = new Label("Employee ID :");
		mainLayout.addComponent(lblemployeeName, "top:130.0px; left:30.0px;");

		cmbEmployeeName= new ComboBox();
		cmbEmployeeName.setWidth("320px");
		cmbEmployeeName.setHeight("-1px");
		cmbEmployeeName.setImmediate(true);
		mainLayout.addComponent(cmbEmployeeName, "top:128.0px;left:150.0px;");

		// chkSectionAll
		chkEmployeeAll = new CheckBox("All");
		chkEmployeeAll.setImmediate(true);
		chkEmployeeAll.setWidth("-1px");
		chkEmployeeAll.setHeight("-1px");
		mainLayout.addComponent(chkEmployeeAll, "top:130.0px;left:473.0px;");

		// CommonButton
		mainLayout.addComponent(cButton,"top:180.0px;left:110.0px;");

		// PI
		PI=new ProgressIndicator();
		PI.setWidth("130px");
		PI.setImmediate(true);
		PI.setEnabled(false);
		mainLayout.addComponent(PI,"top:188.0px;left:330.0px;");

		return mainLayout;
	}

	public class Worker1 extends Thread 
	{
		int current = 1;
		public final static int MAX = 10;
		public void run() 
		{
			for (; current <= MAX; current++) 
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				synchronized (getApplication()) 
				{
					prosessed();
				}
			}
			showNotification("Bonus Deleted Successfully");
			txtClear();
			componentIni(true);
		}
		public int getCurrent() 
		{
			return current;
		}
	}
	public void prosessed() 
	{
		int i = worker1.getCurrent();
		if (i == Worker1.MAX)
		{
			PI.setEnabled(false);
			cButton.btnDelete.setEnabled(true);
			PI.setValue(1f);
		}
		else
		{
			PI.setValue((float) i / Worker1.MAX);
		}
	}
}
