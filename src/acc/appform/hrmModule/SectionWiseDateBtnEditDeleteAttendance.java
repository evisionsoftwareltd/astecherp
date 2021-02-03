package acc.appform.hrmModule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SectionWiseDateBtnEditDeleteAttendance extends Window
{
	private CommonButton cButton = new CommonButton( "", "Save", "", "", "", "", "", "","","Exit");
	private AbsoluteLayout mainLayout;
	private Label lblcommon;
	private Label lblFormat;

	private Label lblEmployeeId;
	private Label lblEmployeeName;
	private Label lblDesignation;
	private NumberField txtInTime1;
	private NumberField txtInTime2;
	private NumberField txtOutTime1;
	private NumberField txtOutTime2;

	private String strEmployeeID;
	private String strDate;

	LinkedHashMap<String, String> hmEmployeeInfo = new LinkedHashMap<String, String>();
	private ArrayList<Component> allComponent  = new ArrayList<Component>();

	public SectionWiseDateBtnEditDeleteAttendance(SessionBean sessionBean,String Date,String EmployeeID) 
	{
		this.setCaption("EDIT EMPLOYEE ATTENDANCE (DATE BETWEEN) :: "+ sessionBean.getCompany());
		this.setResizable(false);
		this.strEmployeeID=EmployeeID;
		this.strDate=Date;
		buildMainLayout();
		setContent(mainLayout);
		SetEventAction();
		txtInTime1.focus();
		focusMoveByEnter();
		setPredefinedValue();
	}

	private void setPredefinedValue()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select ein.vEmployeeId,ein.employeeCode,ein.vProximityId,ein.vEmployeeName," +
					"ein.vDepartmentId,dinf.vDepartmentName,sinf.vSectionID,sinf.SectionName,ein.vDesignationId," +
					"din.designationName,din.designationSerial,ein.OtStatus from tbEmployeeInfo ein inner join " +
					"tbDepartmentInfo dinf on ein.vDepartmentId=dinf.vDepartmentId inner join tbSectionInfo sinf " +
					"on ein.vSectionId=sinf.vSectionID inner join tbDesignationInfo din on " +
					"ein.vDesignationId=din.designationId where ein.vEmployeeId='"+strEmployeeID+"'";
			List<?> lstInfo=session.createSQLQuery(query).list();
			if(!lstInfo.isEmpty())
			{
				Object [] element = (Object[])lstInfo.iterator().next(); 
				hmEmployeeInfo.put("1", element[0].toString());
				hmEmployeeInfo.put("2", element[1].toString());
				lblEmployeeId.setValue(element[1].toString());
				hmEmployeeInfo.put("3", element[2].toString());
				hmEmployeeInfo.put("4", element[3].toString());
				lblEmployeeName.setValue(element[3].toString());
				hmEmployeeInfo.put("5", element[4].toString());
				hmEmployeeInfo.put("6", element[5].toString());
				hmEmployeeInfo.put("7", element[6].toString());
				hmEmployeeInfo.put("8", element[7].toString());
				hmEmployeeInfo.put("9", element[8].toString());
				hmEmployeeInfo.put("10", element[9].toString());
				lblDesignation.setValue(element[9].toString());
				hmEmployeeInfo.put("11", element[10].toString());
				hmEmployeeInfo.put("12", element[11].toString());
			}
		}
		catch (Exception exp)
		{
			showNotification("addValueToHashMap", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void focusMoveByEnter()
	{
		allComponent.add(txtInTime1);
		allComponent.add(txtInTime2);
		allComponent.add(txtOutTime1);
		allComponent.add(txtOutTime2);
		allComponent.add(cButton.btnSave);

		new FocusMoveByEnter(this, allComponent);
	}

	private void SetEventAction() 
	{
		txtInTime1.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtInTime1.getValue().toString().trim().toString().isEmpty())
				{
					if(Integer.parseInt(txtInTime1.getValue().toString())>23)
					{
						txtInTime1.setValue("");
						showNotification("Warning", "Please Provide valid time!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		txtInTime2.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtInTime2.getValue().toString().trim().toString().isEmpty())
				{
					if(Integer.parseInt(txtInTime2.getValue().toString())>59)
					{
						txtInTime2.setValue("");
						showNotification("Warning", "Please Provide valid time!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		txtOutTime1.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtOutTime1.getValue().toString().trim().toString().isEmpty())
				{
					if(Integer.parseInt(txtOutTime1.getValue().toString())>23)
					{
						txtOutTime1.setValue("");
						showNotification("Warning", "Please Provide valid time!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		txtOutTime2.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtOutTime2.getValue().toString().trim().toString().isEmpty())
				{
					if(Integer.parseInt(txtOutTime2.getValue().toString())>59)
					{
						txtOutTime2.setValue("");
						showNotification("Warning", "Please Provide valid time!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtInTime1.getValue().toString().trim().isEmpty())
				{
					if(!txtInTime2.getValue().toString().trim().isEmpty())
					{
						if(!txtOutTime1.getValue().toString().trim().isEmpty())
						{
							if(!txtOutTime2.getValue().toString().trim().isEmpty())
							{
								if(!checkSalary())
									saveButtonEvent();
								else
									showNotification("Warning", "Salary already generated!!!", Notification.TYPE_WARNING_MESSAGE);
							}
							else
							{
								txtOutTime2.focus();
								showNotification("Warning", "Provide Out Time!!!", Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else
						{
							txtOutTime1.focus();
							showNotification("Warning", "Provide Out Time!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						txtInTime2.focus();
						showNotification("Warning", "Provide In Time!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					txtInTime1.focus();
					showNotification("Warning", "Provide In Time!!!", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnExit.addListener( new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
	}

	private boolean checkSalary()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query = "select * from tbSalary where autoEmployeeID = '"+strEmployeeID+"' and MONTH(dDate) = MONTH('"+strDate+"') and " +
					"YEAR(dDate) = YEAR('"+strDate+"')";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("checkSalary", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
		return false;
	}
	
	private void saveButtonEvent()
	{
		MessageBox msgbox=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Save All Information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		msgbox.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType==ButtonType.YES)
				{
					insertData();
					close();
				}
			}
		});
	}

	private void insertData()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{

			String inTime="";
			String outTime="";
			String query = "";

			int inHour=0;
			int outHour=0;
			int inMin=0;
			int outMin=0;

			inHour=Integer.parseInt(txtInTime1.getValue().toString());
			outHour=Integer.parseInt(txtOutTime1.getValue().toString());

			inMin=Integer.parseInt(txtInTime2.getValue().toString());
			outMin=Integer.parseInt(txtOutTime2.getValue().toString());

			if(inHour<outHour)
			{
				inTime="'"+strDate+" "+txtInTime1.getValue().toString()+":"+txtInTime2.getValue().toString()+":00'";
				outTime="'"+strDate+" "+txtOutTime1.getValue().toString()+":"+txtOutTime2.getValue().toString()+":00'";
			}

			else if(inHour==outHour)
			{
				if(inMin<outMin)
				{
					inTime="'"+strDate+" "+txtInTime1.getValue().toString()+":"+txtInTime2.getValue().toString()+":00'";
					outTime="'"+strDate+" "+txtOutTime1.getValue().toString()+":"+txtOutTime2.getValue().toString()+":00'";
				}
				else
				{
					inTime="'"+strDate+" "+txtInTime1.getValue().toString()+":"+txtInTime2.getValue().toString()+":00'";
					outTime="DateADD(dd,1,'"+strDate+" "+txtOutTime1.getValue().toString()+":"+txtOutTime2.getValue().toString()+":00')";
				}
			}

			else
			{
				inTime="'"+strDate+" "+txtInTime1.getValue().toString()+":"+txtInTime2.getValue().toString()+":00'";
				outTime="DateADD(dd,1,'"+strDate+"  "+txtOutTime1.getValue().toString()+":"+txtOutTime2.getValue().toString()+":00')";
			}

			String chkQuery="select vEmployeeID from tbEmployeeMainAttendance where " +
					"vEmployeeId='"+hmEmployeeInfo.get("1")+"' and dDate='"+strDate+"'";
			List<?> lstChk=session.createSQLQuery(chkQuery).list();

			if(lstChk.isEmpty())
			{
				query = "insert into tbEmployeeMainAttendance(dDate,vEmployeeID,employeeCode,vProximityID," +
						"vEmployeeName,vDepartmentID,vDepartmentName,vSectionId,vSectionName,vDesignationID," +
						"vDesignationName,iDesignationSerial,vShiftID,vShiftName,dInTimeOne,dOutTimeOne,vEditFlag," +
						"vAttendanceFlag,bOtStatus,ishiftStatus) " +
						"values ('"+strDate+"'," +
						"'"+hmEmployeeInfo.get("1")+"'," +
						"'"+hmEmployeeInfo.get("2")+"'," +
						"'"+hmEmployeeInfo.get("3")+"'," +
						"'"+hmEmployeeInfo.get("4")+"'," +
						"'"+hmEmployeeInfo.get("5")+"'," +
						"'"+hmEmployeeInfo.get("6")+"'," +
						"'"+hmEmployeeInfo.get("7")+"'," +
						"'"+hmEmployeeInfo.get("8")+"'," +
						"'"+hmEmployeeInfo.get("9")+"'," +
						"'"+hmEmployeeInfo.get("10")+"'," +
						"'"+hmEmployeeInfo.get("11")+"'," +
						"'0','General'," +
						""+inTime+"," +
						""+outTime+",'Edited'," +
						"(case when ISNULL((select dDate from tbHoliday where dDate='"+strDate+"'),'')!='' then 'HP' " +
						"when DATEDIFF(SS,"+inTime+","+outTime+")>=25200 then 'PR' " +
						"when DATEDIFF(SS,"+inTime+","+outTime+")<25200 then 'SA' end)," +
						"'"+hmEmployeeInfo.get("12")+"','')";
			}

			else
			{
				query="update tbEmployeeMainAttendance set dInTimeOne="+inTime+", dOutTimeOne="+outTime+", " +
						"vEditFlag='Edited',vAttendanceFlag=(case when ISNULL((select dDate from tbHoliday where " +
						"dDate='"+strDate+"'),'')!='' then 'HP' when DATEDIFF(SS,"+inTime+","+outTime+")>=25200 " +
						"then 'PR' when DATEDIFF(SS,"+inTime+","+outTime+")<25200 then 'SA' end) where " +
						"vEmployeeID='"+hmEmployeeInfo.get("1")+"' and dDate='"+strDate+"'";
			}

			session.createSQLQuery(query).executeUpdate();
			getParent().getWindow().showNotification("Information Saved Successfully.", Notification.TYPE_HUMANIZED_MESSAGE);
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("InsertData", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		setWidth("500px");
		setHeight("300px");

		lblFormat = new Label("<font color=#795548 font size='3'><b/>Use 24 Hours Format</font>",Label.CONTENT_XHTML);
		lblFormat.setWidth("-1px");
		mainLayout.addComponent(lblFormat, "top:10.0px; right:20px; ");

		lblcommon= new Label("Employee ID:");
		mainLayout.addComponent(lblcommon, "top:50.0px; left:20.0px; ");

		lblEmployeeId = new Label();
		lblEmployeeId.setImmediate(true);
		lblEmployeeId.setWidth("200px");
		lblEmployeeId.setHeight("24px");
		mainLayout.addComponent(lblEmployeeId, "top:48.0px; left:150.0px;");

		lblcommon= new Label("Employee Name:");
		mainLayout.addComponent(lblcommon, "top:80.0px; left:20.0px; ");

		lblEmployeeName = new Label();
		lblEmployeeName.setImmediate(true);
		lblEmployeeName.setWidth("220px");
		lblEmployeeName.setHeight("24px");
		mainLayout.addComponent(lblEmployeeName, "top:78.0px; left:150.0px;");

		lblcommon= new Label("Designation:");
		mainLayout.addComponent(lblcommon, "top:110.0px; left:20.0px; ");

		lblDesignation = new Label();
		lblDesignation.setImmediate(true);
		lblDesignation.setWidth("200px");
		lblDesignation.setHeight("24px");
		mainLayout.addComponent(lblDesignation, "top:108.0px; left:150.0px;");

		lblcommon= new Label("In Time:");
		mainLayout.addComponent(lblcommon, "top:140.0px; left:20.0px; ");

		txtInTime1 = new NumberField();
		txtInTime1.setImmediate(true);
		txtInTime1.setWidth("30px");
		txtInTime1.setHeight("20px");
		txtInTime1.setMaxLength(2);
		mainLayout.addComponent(txtInTime1, "top:138.0px; left:150.0px;");

		txtInTime2 = new NumberField();
		txtInTime2.setImmediate(true);
		txtInTime2.setWidth("30px");
		txtInTime2.setHeight("20px");
		txtInTime2.setMaxLength(2);
		mainLayout.addComponent(txtInTime2, "top:138.0px; left:185.0px;");

		lblcommon= new Label("Out Time:");
		mainLayout.addComponent(lblcommon, "top:170.0px; left:20.0px; ");

		txtOutTime1 = new NumberField();
		txtOutTime1.setImmediate(true);
		txtOutTime1.setWidth("30px");
		txtOutTime1.setHeight("20px");
		txtOutTime1.setMaxLength(2);
		mainLayout.addComponent(txtOutTime1, "top:168.0px; left:150.0px;");

		txtOutTime2 = new NumberField();
		txtOutTime2.setImmediate(true);
		txtOutTime2.setWidth("30px");
		txtOutTime2.setHeight("20px");
		txtOutTime2.setMaxLength(2);
		mainLayout.addComponent(txtOutTime2, "top:168.0px; left:185.0px;");

		mainLayout.addComponent(cButton, "bottom: 20.0px; left: 175px;");
		return mainLayout;
	}
}