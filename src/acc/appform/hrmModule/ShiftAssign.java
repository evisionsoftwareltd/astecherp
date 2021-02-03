package acc.appform.hrmModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.TimeField;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class ShiftAssign extends Window
{

	private TextField txtShiftName;
	private TimeField hour;
	private TimeField minute;
	private TimeField second;
	private TextField txtAMPM;
	private TimeField txtDuration;

	private TextRead outhour;
	private TextRead outminute;
	private TextRead outsecond;
	private TextRead txtoutAMPM;

	private TextField txtDescription;

	private AbsoluteLayout mainLayout;
	SessionBean sessionbean;
	private CommonButton cButton=new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "", "", "");

	private ArrayList<Component> allComp=new ArrayList<Component>();

	SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	TextField txtShiftID=new TextField();

	String updateSID;
	boolean isUpdate=false;
	boolean isFind=false;

	public ShiftAssign(SessionBean sessionbean)
	{
		this.sessionbean=sessionbean;
		this.setCaption("SHIFT ASSIGN :: "+sessionbean.getCompany());
		this.setWidth("475px");
		this.setHeight("320px");
		this.setResizable(false);
		buildMainLayout();
		this.setContent(mainLayout);
		componentIni(true);
		btnIni(true);
		setEventAction();
		focusMove();
	}

	private void componentIni(boolean b)
	{
		txtShiftName.setEnabled(!b);
		hour.setEnabled(!b);
		minute.setEnabled(!b);
		second.setEnabled(!b);
		txtAMPM.setEnabled(!b);
		outhour.setEnabled(!b);
		txtDuration.setEnabled(!b);
		outminute.setEnabled(!b);
		outsecond.setEnabled(!b);
		txtoutAMPM.setEnabled(!b);
		txtDescription.setEnabled(!b);
	}

	private void btnIni(boolean b)
	{
		cButton.btnNew.setEnabled(b);
		cButton.btnSave.setEnabled(!b);
		cButton.btnEdit.setEnabled(b);
		cButton.btnRefresh.setEnabled(!b);
		cButton.btnFind.setEnabled(b);
	}

	private void focusMove()
	{
		allComp.add(txtShiftName);
		allComp.add(hour);
		allComp.add(minute);
		allComp.add(second);
		allComp.add(txtAMPM);
		allComp.add(txtDuration);
		allComp.add(txtDescription);
		allComp.add(cButton.btnSave);
		new FocusMoveByEnter(this, allComp);
	}

	/*private void cmbSectiondataload()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql="select AutoID,SectionName from tbSectionInfo";
			Iterator <?> itr=session.createSQLQuery(sql).list().iterator();
			while(itr.hasNext())
			{
				Object[] element=(Object[])itr.next();
			}
		}

		catch(Exception exp)
		{
			this.getParent().getWindow().showNotification("CMBSECTIONDATALOAD",exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}*/

	private void txtClear()
	{
		txtShiftName.setValue("");
		hour.setValue("");
		minute.setValue("");
		second.setValue("");
		txtAMPM.setValue("");
		txtAMPM.setInputPrompt("AM");
		outhour.setValue("");
		outminute.setValue("");
		outsecond.setValue("");
		txtoutAMPM.setValue("");
		txtDuration.setValue("");
		txtDescription.setValue("");
	}

	private void setEventAction()
	{
		txtShiftName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!isFind)
				{
					Session session=SessionFactoryUtil.getInstance().openSession();
					session.beginTransaction();
					try
					{
						String sql="select vShiftName from tbshiftInformation where vShiftName ='"+txtShiftName.getValue().toString().trim()+"'";
						List <?> lst=session.createSQLQuery(sql).list();
						if(!lst.isEmpty())
						{
							txtShiftName.setValue("");
							showNotification("Warning", "Shift Name Already Exists!!!", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					catch(Exception exp)
					{
						showNotification("txtShiftName", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
					}
					finally{session.close();}
				}
			}
		});

		txtDuration.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!hour.getValue().toString().isEmpty() && !minute.getValue().toString().isEmpty() && !second.getValue().toString().isEmpty() && !txtAMPM.getValue().toString().isEmpty())
				{
					if(!txtDuration.getValue().toString().isEmpty())
					{
						shiftcalc();
						txtDuration.focus();
					}
				}
			}
		});

		cButton.btnNew.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=false;
				componentIni(false);
				btnIni(false);
				txtClear();
				txtShiftName.focus();
			}
		});
		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!txtShiftName.getValue().toString().isEmpty())
				{
					if(!hour.getValue().toString().isEmpty() && !minute.getValue().toString().isEmpty() && !second.getValue().toString().isEmpty() && !txtAMPM.getValue().toString().isEmpty())
					{
						if(!txtDuration.getValue().toString().isEmpty())
						{
							saveBtnEvent();
						}
						else
							showNotification("Warning", "Please Enter Shift Duration!", Notification.TYPE_WARNING_MESSAGE);
					}
					else
						showNotification("Warning", "Please Enter Shift Start Time!", Notification.TYPE_WARNING_MESSAGE);
				}
				else
					showNotification("Warning", "Please Enter Shift Name!", Notification.TYPE_WARNING_MESSAGE);
			}
		});

		cButton.btnEdit.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(!txtShiftName.getValue().toString().isEmpty())
				{
					updateButtonEvent();
				}
				else
				{
					getParent().showNotification("Warning!,","There are nothing to update", Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnRefresh.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isFind=false;
				componentIni(true);
				btnIni(true);
				txtClear();
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
	}
	private void updateButtonEvent()
	{
		if(!txtShiftName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findButtonEvent() 
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String query="select vShiftId from tbShiftInformation";
			List <?> lst=session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{
				Window win = new ShiftFindWin(sessionbean, txtShiftID,"ShiftAssign");
				win.addListener(new Window.CloseListener() 
				{
					public void windowClose(CloseEvent e) 
					{
						if (txtShiftID.getValue().toString().length() > 0)
						{
							txtClear();
							findInitialise(txtShiftID.getValue().toString());
						}
					}
				});

				this.getParent().addWindow(win);
			}
			else
				showNotification("Warning", "No Shift Found!!!", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			showNotification("findButtonEvent", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}

	private void findInitialise(String txtShiftId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			updateSID  = txtShiftId;
			String query="select vShiftId,vShiftName,vDescription,DATEPART(HH,tShiftStart) hour,DATEPART(MI,tShiftStart) minute," +
					"DATEPART(SS,tShiftStart) second,shiftDuration from tbShiftInformation Where vShiftId ='"+txtShiftId+"'";
			List <?> led = session.createSQLQuery(query).list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtShiftName.setValue(element[1]);
				txtDescription.setValue(element[2]);
				if(Integer.parseInt(element[3].toString())<=12)
				{
					if(Integer.parseInt(element[3].toString())<10)
						hour.setValue("0"+element[3].toString());
					else
						hour.setValue(element[3].toString());

					if(Integer.parseInt(element[3].toString())==12)
						txtAMPM.setValue("PM");

					txtAMPM.setValue("AM");
				}

				else
				{
					if(Integer.parseInt(element[3].toString())-12<10)
						hour.setValue("0"+Integer.toString(Integer.parseInt(element[3].toString())-12));
					else
						hour.setValue(Integer.toString(Integer.parseInt(element[3].toString())-12));

					txtAMPM.setValue("PM");
				}

				if(Integer.parseInt(element[4].toString())<10)
					minute.setValue("0"+element[4].toString());
				else
					minute.setValue(Integer.parseInt(element[4].toString()));

				if(Integer.parseInt(element[5].toString())<10)
					second.setValue("0"+element[5].toString());
				else
					second.setValue(Integer.parseInt(element[5].toString()));

				txtDuration.setValue(Integer.parseInt(element[6].toString()));
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void saveBtnEvent()
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
						if(deletedate())
							insertdata();
						isUpdate = false;
						componentIni(true);
						btnIni(true);
						txtClear();
						cButton.btnNew.focus();
					}
				}
			});
		}
		else
		{
			MessageBox mb=new MessageBox(getParent(), "Are You Sure?", MessageBox.Icon.QUESTION, "Do You Want to Save All Information", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType==ButtonType.YES)
					{
						insertdata();
						isUpdate = false;
						componentIni(true);
						btnIni(true);
						txtClear();
					}
				}
			});
		}
	}

	private boolean deletedate()
	{
		String query="delete from tbshiftInformation where vShiftName='"+txtShiftName.getValue().toString().trim()+"'";
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			session.createSQLQuery(query).executeUpdate();
			tx.commit();
			return true;

		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("deletedata", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}

	private void insertdata()
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		Transaction tx=session.beginTransaction();
		try
		{
			String shiftId=session.createSQLQuery("select ISNULL(MAX(CAST(SUBSTRING(vShiftId,7,LEN(vShiftId)) as int)),0)+1 from tbshiftInformation").list().iterator().next().toString();
			int ihour=Integer.parseInt(hour.getValue().toString().trim());
			String strminute=minute.getValue().toString().trim();
			String strsecond=second.getValue().toString().trim();
			int iOutHr=Integer.parseInt(outhour.getValue().toString().trim());
			String strOutMin=outminute.getValue().toString().trim();
			String strOutSec=outsecond.getValue().toString().trim();
			String shiftStartTime="";
			String shiftEndTime="";

			if(txtAMPM.getValue().toString().trim().equalsIgnoreCase("PM") || txtAMPM.getValue().toString().trim().equalsIgnoreCase("P"))
				shiftStartTime=Integer.toString(ihour+12)+":"+strminute+":"+strsecond;

			else
				shiftStartTime=Integer.toString(ihour)+":"+strminute+":"+strsecond;

			if(txtoutAMPM.getValue().toString().trim().equalsIgnoreCase("PM"))
				shiftEndTime=Integer.toString(iOutHr+12)+":"+strOutMin+":"+strOutSec;

			else
				shiftEndTime=Integer.toString(iOutHr)+":"+strOutMin+":"+strOutSec;

			String sql="insert into tbshiftInformation (vShiftId,vShiftName,vDescription,tShiftStart,tShiftEnd,tLateIn,tEarlyOut," +
					"tOverTimeStart,tOverTimeEnd,UserId,UserIp,EntryTime,shiftDuration) values ('Shift-"+shiftId+"','"+txtShiftName.getValue().toString().trim()+"','"+txtDescription.getValue().toString().trim()+"'," +
					"'"+shiftStartTime+"','"+shiftEndTime+"','"+shiftStartTime+"','"+shiftEndTime+"','00:00:00','00:00:00',	'"+sessionbean.getUserName()+"','"+sessionbean.getUserIp()+"',getdate(),'"+Integer.parseInt(txtDuration.getValue().toString())+"')";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
		}
		catch (Exception exp)
		{
			tx.rollback();
			showNotification("InsertDate", exp.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		showNotification("All Information Saved Successfully.");

	}

	private void shiftcalc()
	{
		if(Integer.parseInt(txtDuration.getValue().toString())<=20)
		{
			int ihour=Integer.parseInt(hour.getValue().toString());
			if(txtAMPM.getValue().toString().equalsIgnoreCase("PM") || txtAMPM.getValue().toString().equalsIgnoreCase("p"))
				ihour+=12;

			if(((ihour+Integer.parseInt(txtDuration.getValue().toString()))%12)<10)
				outhour.setValue("0"+Integer.toString((ihour+Integer.parseInt(txtDuration.getValue().toString()))%12));
			else
				outhour.setValue(Integer.toString((ihour+Integer.parseInt(txtDuration.getValue().toString()))%12));

			if(Integer.parseInt(minute.getValue().toString())<10)
				outminute.setValue("0"+Integer.toString(Integer.parseInt(minute.getValue().toString())));
			else
				outminute.setValue(Integer.toString(Integer.parseInt(minute.getValue().toString())));

			if(Integer.parseInt(second.getValue().toString())<10)
				outsecond.setValue("0"+Integer.toString(Integer.parseInt(second.getValue().toString())));
			else
				outsecond.setValue(Integer.toString(Integer.parseInt(second.getValue().toString())));

			System.out.println("HOur : "+ihour+"TXTAMPM : "+txtAMPM.getValue().toString());

			if((ihour+Integer.parseInt(txtDuration.getValue().toString()))>=24)
			{
				int duration=ihour+Integer.parseInt(txtDuration.getValue().toString())-24;
				if(duration>12)
					txtoutAMPM.setValue("PM");
				else
					txtoutAMPM.setValue("AM");
			}
			else
			{
				int duration=ihour+Integer.parseInt(txtDuration.getValue().toString());
				if(duration>12)
					txtoutAMPM.setValue("PM");
				else
					txtoutAMPM.setValue("AM");
			}
		}
		else
		{
			txtDuration.setValue("");
			outhour.setValue("");
			outminute.setValue("");
			outsecond.setValue("");
			txtoutAMPM.setValue("");
		}
	}

	private AbsoluteLayout buildMainLayout()
	{

		mainLayout=new AbsoluteLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		txtShiftName=new TextField();
		txtShiftName.setImmediate(true);
		txtShiftName.setWidth("300.0px");
		mainLayout.addComponent(new Label("Shift Name : "), "top:30.0px; left:30.0px;");
		mainLayout.addComponent(txtShiftName, "top:28.0px; left:140.0px;");

		hour=new TimeField();
		hour.setWidth("30px");
		hour.setHeight("22px");
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
					else
						minute.focus();
				}
			}
		});
		mainLayout.addComponent(new Label("Shift Start Time : "), "top:60.0px;left:30.0px;");
		mainLayout.addComponent(hour, "top:60.0px;left:140.0px;");

		minute=new TimeField();
		minute.setWidth("30px");
		minute.setHeight("22px");
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
					else
						second.focus();
				}
			}
		});
		mainLayout.addComponent(new Label(":"), "top:60.0px;left:173.0px;");
		mainLayout.addComponent(minute, "top:60.0px;left:176.0px;");

		second=new TimeField();
		second.setWidth("30px");
		second.setHeight("22px");
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
					else
						txtAMPM.focus();
				}
			}
		});
		mainLayout.addComponent(new Label(":"), "top:60.0px;left:209.0px;");
		mainLayout.addComponent(second, "top:60.0px;left:212.0px;");

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
				txtDuration.focus();
			}
		});
		mainLayout.addComponent(txtAMPM, "top:58.0px;left:248.0px;");

		txtDuration=new TimeField();
		txtDuration.setImmediate(true);
		txtDuration.setWidth("40px");
		txtDuration.setMaxLength(2);
		mainLayout.addComponent(new Label("Duration : "), "top:90.0px; left:30.0px");
		mainLayout.addComponent(txtDuration, "top:88.0px;left:140.0px;");
		mainLayout.addComponent(new Label("Hours"), "top:90.0px;left:190.0px;");

		outhour=new TextRead("");
		outhour.setImmediate(true);
		outhour.setWidth("30px");
		outhour.setHeight("22px");
		mainLayout.addComponent(new Label("Shift End Time : "), "top:120.0px;left:30.0px;");
		mainLayout.addComponent(outhour, "top:118.0px;left:140.0px;");

		outminute=new TextRead("");
		outminute.setImmediate(true);
		outminute.setWidth("30px");
		outminute.setHeight("22px");
		mainLayout.addComponent(new Label(":"), "top:118.0px;left:173.0px;");
		mainLayout.addComponent(outminute, "top:118.0px;left:176.0px;");

		outsecond=new TextRead("");
		outsecond.setImmediate(true);
		outsecond.setWidth("30px");
		outsecond.setHeight("22px");
		mainLayout.addComponent(new Label(":"), "top:118.0px;left:209.0px;");
		mainLayout.addComponent(outsecond, "top:118.0px;left:212.0px;");

		txtoutAMPM=new TextRead("");
		txtoutAMPM.setImmediate(true);
		txtoutAMPM.setWidth("30px");
		txtoutAMPM.setHeight("22px");
		mainLayout.addComponent(txtoutAMPM, "top:118.0px;left:248.0px;");

		txtDescription=new TextField();
		txtDescription.setImmediate(true);
		txtDescription.setWidth("300px");
		txtDescription.setHeight("50px");
		mainLayout.addComponent(new Label("Description : "),"top:150.0px;left:30.0px;");
		mainLayout.addComponent(txtDescription, "top:148.0px;left:140.0px");

		mainLayout.addComponent(cButton, "top:230.0px;left:25.0px;");
		return mainLayout;
	}
}
