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


public class RptShiftWiseTotalManPowerTwoShift extends Window {
		private ComboBox cmbDepartment;
		private ComboBox cmbSection;
		private PopupDateField dDate;
		private TimeField tohour;
		private TimeField tominute;
		private TimeField tosecond;
		private TextField totxtAMPM;
		private TimeField fromhour;
		private TimeField fromminute;
		private TimeField fromsecond;
		private TextField fromtxtAMPM;
		
		private TimeField tohourAnother;
		private TimeField tominuteAnother;
		private TimeField tosecondAnother;
		private TextField totxtAMPMAnother;
		private TimeField fromhourAnother;
		private TimeField fromminuteAnother;
		private TimeField fromsecondAnother;
		private TextField fromtxtAMPMAnother;

		private AbsoluteLayout mainLayout;
		SessionBean sessionbean;
		private CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
		private OptionGroup reportType;
		private static final List<String> lstType=Arrays.asList(new String[]{"PDF","Others"});
		private CheckBox chkAll;
		private CheckBox chkAllDpt;
		private ReportDate reportdate=new ReportDate();

		private ArrayList<Component> allComp=new ArrayList<Component>();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		private static final String CHO="'DEPT10'";
		public RptShiftWiseTotalManPowerTwoShift(SessionBean sessionbean)
		{
			this.sessionbean=sessionbean;
			this.setCaption("SHIFT WISE TOTAL MAN POWER REPORT :: "+sessionbean.getCompany());
			this.setWidth("575px");
			this.setHeight("250px");
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
			allComp.add(fromhour);
			allComp.add(fromminute);
			allComp.add(fromsecond);
			allComp.add(fromtxtAMPM);
			allComp.add(tohour);
			allComp.add(tominute);
			allComp.add(tosecond);
			allComp.add(totxtAMPM);
			
			allComp.add(fromhourAnother);
			allComp.add(fromminuteAnother);
			allComp.add(fromsecondAnother);
			allComp.add(fromtxtAMPMAnother);
			allComp.add(tohourAnother);
			allComp.add(tominuteAnother);
			allComp.add(tosecondAnother);
			allComp.add(totxtAMPMAnother);

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
			
			chkAllDpt.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(chkAllDpt.booleanValue())
					{
						cmbDepartment.setValue(null);
						cmbDepartment.setEnabled(false);
					}
					else
					{
						cmbDepartment.setEnabled(true);
					}
				}
			});

			cButton.btnPreview.addListener(new ClickListener()
			{
				public void buttonClick(ClickEvent event)
				{
					if(cmbDepartment.getValue()!=null || chkAllDpt.booleanValue())
					{
						if(cmbSection.getValue()!=null || chkAll.booleanValue())
						{
							String section = "%";
							
							if(cmbSection.getValue()!=null)
							{section = (cmbSection.getValue().toString());}

							reportShow(section);

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

		private void reportShow(Object sectionId)
		{
			ReportOption RadioBtn= new ReportOption(reportType.getValue().toString());
			String query=null;
			String shiftTimeStart="";
			String shiftTimeEnd="";
			
			String shiftTimeStartAnother="";
			String shiftTimeEndAnother="";
			
			int inHour = Integer.parseInt(fromhour.getValue().toString());
			int outHour = Integer.parseInt(tohour.getValue().toString());
			int inMin = Integer.parseInt(fromminute.getValue().toString());
			int outMin = Integer.parseInt(tominute.getValue().toString());
			int inSec = Integer.parseInt(fromsecond.getValue().toString());
			int outSec = Integer.parseInt(tosecond.getValue().toString());
			
			int inHourAnother = Integer.parseInt(fromhourAnother.getValue().toString());
			int outHourAnother = Integer.parseInt(tohourAnother.getValue().toString());
			int inMinAnother = Integer.parseInt(fromminuteAnother.getValue().toString());
			int outMinAnother = Integer.parseInt(tominuteAnother.getValue().toString());
			int inSecAnother = Integer.parseInt(fromsecondAnother.getValue().toString());
			int outSecAnother = Integer.parseInt(tosecondAnother.getValue().toString());
			
			System.out.println(inHour+" "+outHour+" "+inMin+" "+outMin);

			Session session=SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();

			try
			{
				String Section="";
				String dpt="";
				if(cmbSection.getValue()!=null)
				{
					Section=cmbSection.getValue().toString();
				}
				else
				{
					Section="%";
				}
				
				if(cmbSection.getValue()!=null)
				{
					dpt=cmbDepartment.getValue().toString();
				}
				else
				{
					dpt="%";
				}

				if(fromtxtAMPM.getValue().toString().equals("PM"))
				{
					inHour += 12;
				}
				if(totxtAMPM.getValue().toString().equals("PM"))
				{
					outHour += 12;
				}
				if(fromtxtAMPMAnother.getValue().toString().equals("PM"))
				{
					inHourAnother += 12;
				}
				if(totxtAMPMAnother.getValue().toString().equals("PM"))
				{
					outHourAnother += 12;
				}

				if(inHour <= 9)
				{
					shiftTimeStart = "0"+Integer.toString(inHour);
				}
				else
				{
					shiftTimeStart=Integer.toString(inHour);
				}
				if(outHour <= 9)
				{
					shiftTimeEnd = "0"+Integer.toString(outHour);
				}
				else
				{
					shiftTimeEnd=Integer.toString(outHour);
				}
				
				if(inMin <= 9)
				{
					shiftTimeStart += ":0"+Integer.toString(inMin);
				}
				else
				{
					shiftTimeStart += ":"+Integer.toString(inMin);
				}
				
				if(outMin <= 9)
				{
					shiftTimeEnd += ":0"+Integer.toString(outMin);
				}
				else
				{
					shiftTimeEnd += ":"+Integer.toString(outMin);
				}
				
				if(inSec <= 9)
				{
					shiftTimeStart += ":0"+Integer.toString(inSec);
				}
				else
				{
					shiftTimeStart += ":"+Integer.toString(inSec);
				}
				
				if(outSec <= 9)
				{
					shiftTimeEnd += ":0"+Integer.toString(outSec);
				}
				else
				{
					shiftTimeEnd += ":"+Integer.toString(outSec);
				}
				
				
				
				if(inHourAnother <= 9)
				{
					shiftTimeStartAnother = "0"+Integer.toString(inHourAnother);
				}
				else
				{
					shiftTimeStartAnother=Integer.toString(inHourAnother);
				}
				if(outHourAnother <= 9)
				{
					shiftTimeEndAnother = "0"+Integer.toString(outHourAnother);
				}
				else
				{
					shiftTimeEndAnother=Integer.toString(outHourAnother);
				}
				
				if(inMinAnother <= 9)
				{
					shiftTimeStartAnother += ":0"+Integer.toString(inMinAnother);
				}
				else
				{
					shiftTimeStartAnother += ":"+Integer.toString(inMinAnother);
				}
				
				if(outMinAnother <= 9)
				{
					shiftTimeEndAnother += ":0"+Integer.toString(outMinAnother);
				}
				else
				{
					shiftTimeEndAnother += ":"+Integer.toString(outMinAnother);
				}
				
				if(inSecAnother <= 9)
				{
					shiftTimeStartAnother += ":0"+Integer.toString(inSecAnother);
				}
				else
				{
					shiftTimeStartAnother += ":"+Integer.toString(inSecAnother);
				}
				
				if(outSecAnother <= 9)
				{
					shiftTimeEndAnother += ":0"+Integer.toString(outSecAnother);
				}
				else
				{
					shiftTimeEndAnother += ":"+Integer.toString(outSecAnother);
				}

				System.out.println(shiftTimeStartAnother+" "+shiftTimeEndAnother);
				System.out.println(shiftTimeStart+" "+shiftTimeEnd);

				/*query="select *,convert(time,convert(varchar(20),DATEDIFF(SS,Convert(time,dOutTimeOne),'"+shiftTimeEnd+"')/3600)+':'+"convert(varchar(20),DATEDIFF(SS,Convert(time,dOutTimeOne),'"+shiftTimeEnd+"')%3600/60)+':'+convert(varchar(20),DATEDIFF(SS,Convert(time,dOutTimeOne),'"+shiftTimeEnd+"')%60)) Early  from funDailyEmployeeAttendance"+
						"('"+dateFormat.format(dDate.getValue())+"','"+dateFormat.format(dDate.getValue())+"','%','"+cmbDepartment.getValue().toString()+"','"+sectionId+"')"
						+ " where (ISNULL(dInTimeOne,'')!='' and ISNULL(dOutTimeOne,'')!='') and convert(time,dOutTimeOne)<'"+shiftTimeEnd+"'";*/

				query="select * from [funShiftWiseTotalManPowerTwoShift]('"+dateFormat.format(dDate.getValue())+"','"+shiftTimeStart+"','"+shiftTimeEnd+"','"+shiftTimeStartAnother+"','"+shiftTimeEndAnother+"', '"+dpt+"','"+Section+"') where vDepartmentId!="+CHO+" order by vSectionName";
				
				System.out.println("Imran "+query);

				if(queryValueCheck(query))
				{
					HashMap <String,Object> hm = new HashMap <String,Object> ();
					hm.put("company", sessionbean.getCompany());
					hm.put("address", sessionbean.getCompanyAddress());
					hm.put("phone", sessionbean.getCompanyContact());
					hm.put("date", new SimpleDateFormat().format(dDate.getValue()));
					hm.put("username", sessionbean.getUserName()+" "+sessionbean.getUserIp());
					hm.put("SysDate",reportdate.getTime);
					hm.put("logo", sessionbean.getCompanyLogo());
					hm.put("date", new SimpleDateFormat("dd-MM-yyyy").format(dDate.getValue()));
					hm.put("shiftTimeStart", shiftTimeStart);
					hm.put("shiftTimeEnd", shiftTimeEnd);
					hm.put("sql", query);

					Window win = new ReportViewer(hm,"report/account/hrmModule/rptShiftWiseManPowerTowShift.jasper",
							this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
							this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
							this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

					win.setCaption("Project Report");
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
		}

		private boolean queryValueCheck(String sql)
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();

			try 
			{
				Iterator <?> iter = session.createSQLQuery(sql).list().iterator();
				if (iter.hasNext()) 
				{
					return true;
				}
			} 
			catch (Exception ex) 
			{
				System.out.print(ex);
			}
			finally{session.close();}
			return false;
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
			mainLayout.addComponent(dDate, "top:08.0px;left:145.0px;");

			cmbDepartment=new ComboBox();
			cmbDepartment.setImmediate(true);
			cmbDepartment.setWidth("260px");
			mainLayout.addComponent(new Label("Department Name : "), "top:40.0px;left:30.0px;");
			mainLayout.addComponent(cmbDepartment, "top:38.0px;left:145.0px;");
			
			chkAllDpt=new CheckBox("All");
			chkAllDpt.setImmediate(true);
			mainLayout.addComponent(chkAllDpt, "top:40.0px;left:405.0px;");

			cmbSection=new ComboBox();
			cmbSection.setImmediate(true);
			cmbSection.setWidth("260px");
			mainLayout.addComponent(new Label("Section Name : "), "top:70.0px;left:30.0px;");
			mainLayout.addComponent(cmbSection, "top:68.0px;left:145.0px;");

			chkAll=new CheckBox("All");
			chkAll.setImmediate(true);
			mainLayout.addComponent(chkAll, "top:70.0px;left:405.0px;");

			fromhour=new TimeField();
			fromhour.setWidth("30px");
			fromhour.setHeight("18px");
			fromhour.setInputPrompt("hh");
			fromhour.setImmediate(true);
			fromhour.setStyleName("Intime");
			fromhour.setMaxLength(2);
			fromhour.setValue("07");
			fromhour.setEnabled(false);
		fromhour.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!fromhour.getValue().toString().isEmpty())	
					{
						if(Integer.parseInt(fromhour.getValue().toString())>12)
						{
							fromhour.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label("Monring Shift Time : "), "top:100.0px;left:30.0px;");
			mainLayout.addComponent(fromhour, "top:98.0px;left:145.0px;");

			fromminute=new TimeField();
			fromminute.setWidth("30px");
			fromminute.setHeight("18px");
			fromminute.setInputPrompt("mm");
			fromminute.setImmediate(true);
			fromminute.setStyleName("Intime");
			fromminute.setMaxLength(2);
			fromminute.setValue("00");
			fromminute.setEnabled(false);
		fromminute.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!fromminute.getValue().toString().isEmpty())
					{
						if(Integer.parseInt(fromminute.getValue().toString())>59)
						{
							fromminute.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label(":"), "top:98.0px;left:173.0px;");
			mainLayout.addComponent(fromminute, "top:98.0px;left:181.0px;");

			fromsecond=new TimeField();
			fromsecond.setWidth("30px");
			fromsecond.setHeight("18px");
			fromsecond.setInputPrompt("ss");
			fromsecond.setImmediate(true);
			fromsecond.setStyleName("Intime");
			fromsecond.setMaxLength(2);
			fromsecond.setValue("00");
			fromsecond.setEnabled(false);
		fromsecond.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!fromsecond.getValue().toString().isEmpty())
					{
						if(Integer.parseInt(fromsecond.getValue().toString())>59)
						{
							fromsecond.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label(":"), "top:98.0px;left:209.0px;");
			mainLayout.addComponent(fromsecond, "top:98.0px;left:217.0px;");

			fromtxtAMPM=new TextField("");
			fromtxtAMPM.setImmediate(true);
			fromtxtAMPM.setWidth("30px");
			fromtxtAMPM.setInputPrompt("AM");
			fromtxtAMPM.setMaxLength(2);
			fromtxtAMPM.setTextChangeEventMode(TextChangeEventMode.EAGER);
			fromtxtAMPM.setValue("AM");
			fromtxtAMPM.setEnabled(false);
		fromtxtAMPM.addListener(new TextChangeListener()
			{
				public void textChange(TextChangeEvent event)
				{
					if(event.getText().equalsIgnoreCase("a"))
					{
						fromtxtAMPM.setValue("AM");
					}

					if(event.getText().equalsIgnoreCase("p"))
					{
						fromtxtAMPM.setValue("PM");
					}
				}
			});
			mainLayout.addComponent(fromtxtAMPM, "top:98.0px;left:248.0px;");

			mainLayout.addComponent(new Label("To"), "top:98.0px;left:290.0px;");


			tohour=new TimeField();
			tohour.setWidth("30px");
			tohour.setHeight("18px");
			tohour.setInputPrompt("hh");
			tohour.setImmediate(true);
			tohour.setStyleName("Intime");
			tohour.setMaxLength(2);
			tohour.setValue("05");
			tohour.setEnabled(false);
	         tohour.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!tohour.getValue().toString().isEmpty())	
					{
						if(Integer.parseInt(tohour.getValue().toString())>12)
						{
							tohour.setValue("");
						}
					}
				}
			});

			mainLayout.addComponent(tohour, "top:98.0px;left:315.0px;");

			tominute=new TimeField();
			tominute.setWidth("30px");
			tominute.setHeight("18px");
			tominute.setInputPrompt("mm");
			tominute.setImmediate(true);
			tominute.setStyleName("Intime");
			tominute.setMaxLength(2);
			tominute.setValue("00");
			tominute.setEnabled(false);
		tominute.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!tominute.getValue().toString().isEmpty())
					{
						if(Integer.parseInt(tominute.getValue().toString())>59)
						{
							tominute.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label(":"), "top:98.0px;left:348.0px;");
			mainLayout.addComponent(tominute, "top:98.0px;left:351.0px;");

			tosecond=new TimeField();
			tosecond.setWidth("30px");
			tosecond.setHeight("18px");
			tosecond.setInputPrompt("ss");
			tosecond.setImmediate(true);
			tosecond.setStyleName("Intime");
			tosecond.setMaxLength(2);
			tosecond.setValue("00");
			tosecond.setEnabled(false);
		tosecond.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!tosecond.getValue().toString().isEmpty())
					{
						if(Integer.parseInt(tosecond.getValue().toString())>59)
						{
							tosecond.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label(":"), "top:98.0px;left:381.0px;");
			mainLayout.addComponent(tosecond, "top:98.0px;left:384.0px;");

			totxtAMPM=new TextField("");
			totxtAMPM.setImmediate(true);
			totxtAMPM.setWidth("30px");
			totxtAMPM.setInputPrompt("PM");
			totxtAMPM.setMaxLength(2);
			totxtAMPM.setTextChangeEventMode(TextChangeEventMode.EAGER);
			totxtAMPM.setValue("PM");
			totxtAMPM.setEnabled(false);
		totxtAMPM.addListener(new TextChangeListener()
			{
				public void textChange(TextChangeEvent event)
				{
					if(event.getText().equalsIgnoreCase("a"))
					{
						totxtAMPM.setValue("AM");
					}

					if(event.getText().equalsIgnoreCase("p"))
					{
						totxtAMPM.setValue("PM");
					}
				}
			});
			mainLayout.addComponent(totxtAMPM, "top:98.0px;left:420.0px;");
			
			fromhourAnother=new TimeField();
			fromhourAnother.setWidth("30px");
			fromhourAnother.setHeight("18px");
			fromhourAnother.setInputPrompt("hh");
			fromhourAnother.setImmediate(true);
			fromhourAnother.setStyleName("Intime");
			fromhourAnother.setMaxLength(2);
			fromhourAnother.setValue("05");
			fromhourAnother.setEnabled(false);
		fromhour.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!fromhour.getValue().toString().isEmpty())	
					{
						if(Integer.parseInt(fromhour.getValue().toString())>12)
						{
							fromhour.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label("Night Shift Time : "), "top:120.0px;left:30.0px;");
			mainLayout.addComponent(fromhourAnother, "top:118.0px;left:145.0px;");

			fromminuteAnother=new TimeField();
			fromminuteAnother.setWidth("30px");
			fromminuteAnother.setHeight("18px");
			fromminuteAnother.setInputPrompt("mm");
			fromminuteAnother.setImmediate(true);
			fromminuteAnother.setStyleName("Intime");
			fromminuteAnother.setMaxLength(2);
			fromminuteAnother.setValue("01");
			fromminuteAnother.setEnabled(false);
		fromminute.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!fromminute.getValue().toString().isEmpty())
					{
						if(Integer.parseInt(fromminute.getValue().toString())>59)
						{
							fromminute.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label(":"), "top:118.0px;left:173.0px;");
			mainLayout.addComponent(fromminuteAnother, "top:118.0px;left:181.0px;");

			fromsecondAnother=new TimeField();
			fromsecondAnother.setWidth("30px");
			fromsecondAnother.setHeight("18px");
			fromsecondAnother.setInputPrompt("ss");
			fromsecondAnother.setImmediate(true);
			fromsecondAnother.setStyleName("Intime");
			fromsecondAnother.setMaxLength(2);
			fromsecondAnother.setValue("00");
			fromsecondAnother.setEnabled(false);
		fromsecond.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!fromsecond.getValue().toString().isEmpty())
					{
						if(Integer.parseInt(fromsecond.getValue().toString())>59)
						{
							fromsecond.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label(":"), "top:118.0px;left:209.0px;");
			mainLayout.addComponent(fromsecondAnother, "top:118.0px;left:217.0px;");

			fromtxtAMPMAnother=new TextField("");
			fromtxtAMPMAnother.setImmediate(true);
			fromtxtAMPMAnother.setWidth("30px");
			fromtxtAMPMAnother.setInputPrompt("AM");
			fromtxtAMPMAnother.setMaxLength(2);
			fromtxtAMPMAnother.setTextChangeEventMode(TextChangeEventMode.EAGER);
			fromtxtAMPMAnother.setValue("PM");
			fromtxtAMPMAnother.setEnabled(false);
		fromtxtAMPM.addListener(new TextChangeListener()
			{
				public void textChange(TextChangeEvent event)
				{
					if(event.getText().equalsIgnoreCase("a"))
					{
						fromtxtAMPM.setValue("AM");
					}

					if(event.getText().equalsIgnoreCase("p"))
					{
						fromtxtAMPM.setValue("PM");
					}
				}
			});
			mainLayout.addComponent(fromtxtAMPMAnother, "top:118.0px;left:248.0px;");

			mainLayout.addComponent(new Label("To"), "top:118.0px;left:290.0px;");


			tohourAnother=new TimeField();
			tohourAnother.setWidth("30px");
			tohourAnother.setHeight("18px");
			tohourAnother.setInputPrompt("hh");
			tohourAnother.setImmediate(true);
			tohourAnother.setStyleName("Intime");
			tohourAnother.setMaxLength(2);
			tohourAnother.setValue("08");
			tohourAnother.setEnabled(false);
	tohour.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!tohour.getValue().toString().isEmpty())	
					{
						if(Integer.parseInt(tohour.getValue().toString())>12)
						{
							tohour.setValue("");
						}
					}
				}
			});

			mainLayout.addComponent(tohourAnother, "top:118.0px;left:315.0px;");

			tominuteAnother=new TimeField();
			tominuteAnother.setWidth("30px");
			tominuteAnother.setHeight("18px");
			tominuteAnother.setInputPrompt("mm");
			tominuteAnother.setImmediate(true);
			tominuteAnother.setStyleName("Intime");
			tominuteAnother.setMaxLength(2);
			tominuteAnother.setValue("00");
			tominuteAnother.setEnabled(false);
		tominute.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!tominute.getValue().toString().isEmpty())
					{
						if(Integer.parseInt(tominute.getValue().toString())>59)
						{
							tominute.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label(":"), "top:118.0px;left:348.0px;");
			mainLayout.addComponent(tominuteAnother, "top:118.0px;left:351.0px;");

			tosecondAnother=new TimeField();
			tosecondAnother.setWidth("30px");
			tosecondAnother.setHeight("18px");
			tosecondAnother.setInputPrompt("ss");
			tosecondAnother.setImmediate(true);
			tosecondAnother.setStyleName("Intime");
			tosecondAnother.setMaxLength(2);
			tosecondAnother.setValue("00");
			tosecondAnother.setEnabled(false);
		tosecond.addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!tosecond.getValue().toString().isEmpty())
					{
						if(Integer.parseInt(tosecond.getValue().toString())>59)
						{
							tosecond.setValue("");
						}
					}
				}
			});
			mainLayout.addComponent(new Label(":"), "top:98.0px;left:381.0px;");
			mainLayout.addComponent(tosecondAnother, "top:118.0px;left:384.0px;");

			totxtAMPMAnother=new TextField("");
			totxtAMPMAnother.setImmediate(true);
			totxtAMPMAnother.setWidth("30px");
			totxtAMPMAnother.setInputPrompt("PM");
			totxtAMPMAnother.setMaxLength(2);
			totxtAMPMAnother.setTextChangeEventMode(TextChangeEventMode.EAGER);
			totxtAMPMAnother.setValue("AM");
			totxtAMPMAnother.setEnabled(false);
		totxtAMPM.addListener(new TextChangeListener()
			{
				public void textChange(TextChangeEvent event)
				{
					if(event.getText().equalsIgnoreCase("a"))
					{
						totxtAMPM.setValue("AM");
					}

					if(event.getText().equalsIgnoreCase("p"))
					{
						totxtAMPM.setValue("PM");
					}
				}
			});
			mainLayout.addComponent(totxtAMPMAnother, "top:118.0px;left:420.0px;");



			reportType=new OptionGroup("",lstType);
			reportType.setStyleName("horizontal");
			reportType.setImmediate(true);
			reportType.select("PDF");
			mainLayout.addComponent(reportType, "top:140.0px;left:200.0px;");

			mainLayout.addComponent(new Label("____________________________________________________________________________________________"), "top:150.0px;right:20.0px;left:20.0px;");
			mainLayout.addComponent(cButton, "top:180.0px;left:200.0px;");
			return mainLayout;
		}


}
