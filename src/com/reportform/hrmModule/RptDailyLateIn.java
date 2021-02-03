package com.reportform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TimeField;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptDailyLateIn extends Window
{
	private ComboBox cmbDepartment;
	private ComboBox cmbSection;
	private PopupDateField dDate;
	private TimeField hour;
	private TimeField minute;
	private TimeField second;
	private TextField txtAMPM;
	private TextField txtLateInMinutes;
	private AbsoluteLayout mainLayout;
	SessionBean sessionbean;
	private CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private OptionGroup reportType;
	private static final List<String> lstType=Arrays.asList(new String[]{"PDF","Others"});
	private CheckBox chkAll;
	private ReportDate reportdate=new ReportDate();

	private ArrayList<Component> allComp=new ArrayList<Component>();
	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private static final String CHO="'DEPT10'";
	public RptDailyLateIn(SessionBean sessionbean)
	{
		this.sessionbean=sessionbean;
		this.setCaption("DAILY LATE IN REPORT :: "+sessionbean.getCompany());
		this.setWidth("475px");
		this.setHeight("280px");
		this.setResizable(false);
		buildMainLayout();
		this.setContent(mainLayout);
		setEventAction();
		cmbDepartmentdataload();
		focusMove();
	}

	private void focusMove()
	{
		allComp.add(cmbDepartment);
		allComp.add(cmbSection);
		allComp.add(dDate);
		allComp.add(hour);
		allComp.add(minute);
		allComp.add(second);
		allComp.add(txtAMPM);
		allComp.add(txtLateInMinutes);
		allComp.add(cButton.btnPreview);
		new FocusMoveByEnter(this, allComp);
	}

	private void cmbDepartmentdataload()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vDepartmentId,vDepartmentName from tbEmployeeMainAttendance " +
					"where dDate='"+dateFormat.format(dDate.getValue())+"' and vDepartmentId!="+CHO+" order by vDepartmentName";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();
			while(itr.hasNext())
			{

				Object[] element=(Object[])itr.next();
				cmbDepartment.addItem(element[0]);
				cmbDepartment.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbDepartmentdataload",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbSectiondataload(String DepartmentID)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select distinct vSectionId,vDepartmentName,vSectionName from tbEmployeeMainAttendance " +
					"where dDate='"+dateFormat.format(dDate.getValue())+"' and " +
					"vDepartmentID='"+DepartmentID+"' and vDepartmentId!="+CHO+" order by vDepartmentName,vSectionName";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();
			while(itr.hasNext())
			{
				Object[] element=(Object[])itr.next();
				cmbSection.addItem(element[0]);
				cmbSection.setItemCaption(element[0], element[1].toString()+"("+element[2].toString()+")");
			}
		}
		catch(Exception exp)
		{
			showNotification("cmbSectiondataload",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void setEventAction()
	{
		dDate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbDepartment.removeAllItems();
				if(dDate.getValue()!=null)
				{
					cmbDepartmentdataload();
				}
			}
		});

		cmbDepartment.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbSection.removeAllItems();
				if(cmbDepartment.getValue()!=null)
				{
					cmbSectiondataload(cmbDepartment.getValue().toString());
				}
			}
		});

		chkAll.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAll.booleanValue())
				{
					cmbSection.setValue(null);
					cmbSection.setEnabled(false);
				}
				else
				{
					cmbSection.setEnabled(true);
				}
			}
		});

		txtLateInMinutes.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtLateInMinutes.getValue().toString().trim().isEmpty())
				{
					if(Integer.parseInt(txtLateInMinutes.getValue().toString().trim())>59)
					{
						txtLateInMinutes.setValue("");
						txtLateInMinutes.focus();
					}
				}
			}
		});
		
		cButton.btnPreview.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbDepartment.getValue()!=null)
				{
					if(cmbSection.getValue()!=null || chkAll.booleanValue())
					{
						if(!txtLateInMinutes.getValue().toString().trim().isEmpty())
						{
							reportShow();
						}
						else
						{
							showNotification("Warning","Please Enter the Late In Time Limit!!!",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please Enter the Section Name or Select All Section!!!", Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning","Please Enter the Department Name!!!", Notification.TYPE_WARNING_MESSAGE);
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

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(reportType.getValue().toString());
		String query=null;
		String shiftTimestart="";
		String shiftStart="";
		int min=0;

		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String Section="%";
			shiftTimestart=hour.getValue().toString();
			shiftStart=hour.getValue().toString();

			if(txtAMPM.getValue().toString().equals("PM"))
				shiftTimestart=Integer.toString(12+Integer.parseInt(hour.getValue().toString()));

			min=Integer.parseInt(minute.getValue().toString())+Integer.parseInt(txtLateInMinutes.getValue().toString().trim());
			shiftTimestart+=":"+Integer.toString(min);
			if(min>9)
				shiftStart+=":"+Integer.toString(min);
			else
				shiftStart+=":0"+Integer.toString(min);
			shiftTimestart+=":"+second.getValue().toString();
			shiftStart+=":"+second.getValue().toString();

			if(txtAMPM.getValue().toString().equalsIgnoreCase("PM"))
				shiftStart+=" PM";
			else
				shiftStart+=" AM";

			if(cmbSection.getValue()!=null)
				Section=cmbSection.getValue().toString();
			
			int diffhour=Integer.parseInt(session.createSQLQuery("select datediff(hh,'"+shiftTimestart+"','14:00:00')").list().iterator().next().toString());

			if(diffhour>0)
			{
				query="select *,convert(time,convert(varchar(20),DATEDIFF(SS,'"+shiftTimestart+"',Convert(time,dInTimeOne))/3600)+':'+" +
						"convert(varchar(20),DATEDIFF(SS,'"+shiftTimestart+"',Convert(time,dInTimeOne))%3600/60)+':'+" +
						"convert(varchar(20),DATEDIFF(SS,'"+shiftTimestart+"',Convert(time,dInTimeOne))%60)) Late from funDailyEmployeeAttendance('"+dateFormat.format(dDate.getValue())+"','"+dateFormat.format(dDate.getValue())+"','%','"+cmbDepartment.getValue()+"','"+Section+"')" +
						" where ISNULL(dInTimeOne,'')!='' and ISNULL(dOutTimeOne,'')!='' and (convert(time,dInTimeOne)>'"+shiftTimestart+"' and convert(time,dInTimeOne)<='14:00:00') and vDepartmentId!="+CHO+" order by vDepartmentName,vSectionName,vEmployeeCode";
			}
			else
			{
				query="select *,convert(time,convert(varchar(20),DATEDIFF(SS,'"+shiftTimestart+"',Convert(time,dInTimeOne))/3600)+':'+" +
						"convert(varchar(20),DATEDIFF(SS,'"+shiftTimestart+"',Convert(time,dInTimeOne))%3600/60)+':'+" +
						"convert(varchar(20),DATEDIFF(SS,'"+shiftTimestart+"',Convert(time,dInTimeOne))%60)) Late from funDailyEmployeeAttendance('"+dateFormat.format(dDate.getValue())+"','"+dateFormat.format(dDate.getValue())+"','%','"+cmbDepartment.getValue()+"','"+Section+"')" +
						" where ISNULL(dInTimeOne,'')!='' and ISNULL(dOutTimeOne,'')!='' and (convert(time,dInTimeOne)>'"+shiftTimestart+"' and convert(time,dInTimeOne)<='23:59:59') and vDepartmentId!="+CHO+" order by vDepartmentName,vSectionName,vEmployeeCode";
			}

			List <?> lst=session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionbean.getCompany());
				hm.put("address", sessionbean.getCompanyAddress());
				hm.put("phone", sessionbean.getCompanyContact());
				hm.put("username", sessionbean.getUserName()+" "+sessionbean.getUserIp());
				hm.put("SysDate",reportdate.getTime);
				hm.put("logo", sessionbean.getCompanyLogo());
				hm.put("date", dDate.getValue());
				hm.put("shiftTime", shiftStart);
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/hrmModule/rptDailyLateList.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Section Wise Daily Late In Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("reportShow "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		dDate=new PopupDateField();
		dDate.setImmediate(true);
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new Date());
		mainLayout.addComponent(new Label("Date : "), "top:10.0px;left:30.0px;");
		mainLayout.addComponent(dDate, "top:08.0px;left:140.0px;");

		cmbDepartment=new ComboBox();
		cmbDepartment.setImmediate(true);
		cmbDepartment.setWidth("260px");
		mainLayout.addComponent(new Label("Department Name : "), "top:40.0px;left:30.0px;");
		mainLayout.addComponent(cmbDepartment, "top:38.0px;left:140.0px;");

		cmbSection=new ComboBox();
		cmbSection.setImmediate(true);
		cmbSection.setWidth("260px");
		mainLayout.addComponent(new Label("Section Name : "), "top:70.0px;left:30.0px;");
		mainLayout.addComponent(cmbSection, "top:68.0px;left:140.0px;");

		chkAll=new CheckBox("All");
		chkAll.setImmediate(true);
		mainLayout.addComponent(chkAll, "top:70.0px;left:405.0px;");

		hour=new TimeField();
		hour.setWidth("30px");
		hour.setHeight("18px");
		hour.setInputPrompt("hh");
		hour.setImmediate(true);
		hour.setStyleName("Intime");
		hour.setMaxLength(2);
		hour.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!hour.getValue().toString().isEmpty())	
				{
					if(Integer.parseInt(hour.getValue().toString())>12)
					{
						hour.setValue("");
					}
				}
			}
		});
		mainLayout.addComponent(new Label("Shift Start Time : "), "top:100.0px;left:30.0px;");
		mainLayout.addComponent(hour, "top:98.0px;left:140.0px;");

		minute=new TimeField();
		minute.setWidth("30px");
		minute.setHeight("18px");
		minute.setInputPrompt("mm");
		minute.setImmediate(true);
		minute.setStyleName("Intime");
		minute.setMaxLength(2);
		minute.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!minute.getValue().toString().isEmpty())
				{
					if(Integer.parseInt(minute.getValue().toString())>59)
					{
						minute.setValue("");
					}
				}
			}
		});
		mainLayout.addComponent(new Label(":"), "top:98.0px;left:173.0px;");
		mainLayout.addComponent(minute, "top:98.0px;left:176.0px;");

		second=new TimeField();
		second.setWidth("30px");
		second.setHeight("18px");
		second.setInputPrompt("ss");
		second.setImmediate(true);
		second.setStyleName("Intime");
		second.setMaxLength(2);
		second.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!second.getValue().toString().isEmpty())
				{
					if(Integer.parseInt(second.getValue().toString())>59)
					{
						second.setValue("");
					}
				}
			}
		});
		mainLayout.addComponent(new Label(":"), "top:98.0px;left:209.0px;");
		mainLayout.addComponent(second, "top:98.0px;left:212.0px;");

		txtAMPM=new TextField("");
		txtAMPM.setImmediate(true);
		txtAMPM.setWidth("30px");
		txtAMPM.setInputPrompt("AM");
		txtAMPM.setMaxLength(2);
		txtAMPM.setTextChangeEventMode(TextChangeEventMode.EAGER);
		txtAMPM.addListener(new TextChangeListener()
		{
			public void textChange(TextChangeEvent event)
			{
				if(event.getText().equalsIgnoreCase("a"))
				{
					txtAMPM.setValue("AM");
				}

				if(event.getText().equalsIgnoreCase("p"))
				{
					txtAMPM.setValue("PM");
				}
			}
		});
		mainLayout.addComponent(txtAMPM, "top:96.0px;left:248.0px;");

		txtLateInMinutes=new TextField();
		txtLateInMinutes.setImmediate(true);
		txtLateInMinutes.setWidth("40px");
		mainLayout.addComponent(new Label("Late In Time Limit : "), "top:130.0px; left:30.0px");
		mainLayout.addComponent(txtLateInMinutes, "top:130.0px;left:140.0px;");
		mainLayout.addComponent(new Label("mins."), "top:130.0px;left:190.0px;");

		reportType=new OptionGroup("",lstType);
		reportType.setStyleName("horizontal");
		reportType.setImmediate(true);
		reportType.select("PDF");
		mainLayout.addComponent(reportType, "top:160.0px;left:140.0px;");

		mainLayout.addComponent(new Label("____________________________________________________________________________________________"), "top:180.0px;right:20.0px;left:20.0px;");
		mainLayout.addComponent(cButton, "top:200.0px;left:160.0px;");
		return mainLayout;
	}
}
