package acc.appform.setupTransaction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.hrmModule.EmployeeInformation;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class DivisionInformation extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");
	private AbsoluteLayout mainLayout=new AbsoluteLayout();

	private Label lblDivisionNo;
	private TextRead txtDivisionNo;

	private Label lblDivisionName;
	private TextField txtDivisionName;

	private Label lblDivIncharge;
	private ComboBox cmbDivIncharge;

	private Label lblDesignation;
	private TextRead txtDesignation;
	
	private Label GroupId=new Label();

	boolean isUpdate=false;

	private NativeButton nbEmployee;

	private SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();
	private TextField DivisionId=new TextField();

	public DivisionInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("DIVISION INFORMATION :: "+sessionBean.getCompany());
		this.setWidth("580px");
		this.setResizable(false);
		
		buildMainLayout();
		setContent(mainLayout);
		
		componentIni(true);
		txtInit(true);
		btnInit(true);
		
		cmbAddEmployeeData();
		
		btnAction();
		focusEnter();
		
		authenticationCheck();
		button.btnNew.focus();
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

	public void btnAction()
	{
		button.btnNew.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newButtonEvent();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				componentIni(true);
				btnInit(true);
				txtClear();
			}
		});

		button.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sessionBean.isUpdateable())
				{
					isUpdate = true;
					updateButtonEvent();
				}
				else
				{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();	
			}
		});

		button.btnSave.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtDivisionName.getValue().toString().isEmpty())
				{
					if(!cmbDivIncharge.getValue().toString().isEmpty())
					{
						saveButtonEvent();
					}
					else
					{
						showNotification("Warning!","Provide Division Incharge",Notification.TYPE_WARNING_MESSAGE);
						cmbDivIncharge.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide Division Name",Notification.TYPE_WARNING_MESSAGE);
					txtDivisionName.focus();
				}
			}
		});

		nbEmployee.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				employeeLink();				
			}
		});

		cmbDivIncharge.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDivIncharge.getValue()!=null)
					setDesignation();
			}
		});
	}

	public void employeeLink()
	{
		Window win = new EmployeeInformation(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbAddEmployeeData();
			}

		});
		this.getParent().addWindow(win);
	}

	public void setDesignation()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query=" select di.designationName from tbEmployeeInfo ei inner join tbDesignationInfo di on ei.vDesignationId=di.designationId where ei.vEmployeeId = '"+cmbDivIncharge.getValue().toString()+"' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if(iter.hasNext())
			{
				txtDesignation.setValue(iter.next().toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnInit(false);
		txtClear();
		txtDivisionNo.setValue(selectDivisionCode());
		txtDivisionName.focus();
	}

	private String selectDivisionCode()
	{
		String DivisionId = "";
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query=" Select isnull(max(cast(vDivisionId as int)),0)+1 from tbDivisionInfo ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if(iter.hasNext())
			{
				DivisionId=iter.next().toString();
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}

		return DivisionId;
	}

	private void saveButtonEvent()
	{
		if(!isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", 
					MessageBox.Icon.QUESTION, "Do you want to Save Division info?",
					new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
					new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						System.out.print("OK");
						insertData();
						btnInit(true);
						txtInit(true);
						txtClear();
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "", 
					MessageBox.Icon.QUESTION, "Do you want to Update Division info?",
					new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"),
					new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new MessageBox.EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						System.out.print("OK");
						updateData();
						isUpdate=false;
						btnInit(true);
						txtInit(true);
						txtClear();
					}
				}
			});
		}
	}

	private void insertData()
	{
		Transaction tx= null;
		try
		{
			Session session= SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			String insertData="INSERT INTO tbDivisionInfo values(" +
					" '"+selectDivisionCode()+"', " +
					" '"+txtDivisionName.getValue().toString().trim()+"', " +
					" '"+cmbDivIncharge.getValue().toString().trim()+"', " +
					" '"+cmbDivIncharge.getItemCaption(cmbDivIncharge.getValue().toString().trim())+"', " +
					" '"+txtDesignation.getValue().toString().trim()+"', " +
					"  ('G'+ISNULL((SELECT cast((max(substring(Group_Id,2,len(Group_Id)-1))+1) as VARCHAR) FROM TbMain_Group),101)), " +
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP )";

			System.out.println("insertData: "+insertData);
			session.createSQLQuery(insertData).executeUpdate();

			String sql = "INSERT INTO tbMain_Group(Group_Id,Group_Name,Head_Id,userId,userIp,entryTime) VALUES('G'+ISNULL((SELECT cast((max(substring(Group_Id,2,len(Group_Id)-1))+1) as VARCHAR) FROM TbMain_Group),101) ,'"+
					txtDivisionName.getValue()+"','A6','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
					session.createSQLQuery(sql).executeUpdate();
					
			showNotification("All information saved successfully");
			tx.commit();
		}
		catch (Exception ex) 
		{
			this.getParent().showNotification("Error",ex+"", Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void updateData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String updateData ="UPDATE tbDivisionInfo set" +
					" vDivisionName ='"+txtDivisionName.getValue().toString().trim()+"', " +
					" vEmployeeId ='"+cmbDivIncharge.getValue().toString().trim()+"', " +
					" vEmployeeName ='"+cmbDivIncharge.getItemCaption(cmbDivIncharge.getValue().toString().trim())+"' ," +
					" vDesignation ='"+txtDesignation.getValue().toString().trim()+"' " +
					" where vDivisionId='"+DivisionId.getValue()+"' ";

			System.out.println("UpdateProduct: "+updateData);
			session.createSQLQuery(updateData).executeUpdate();
			
			String UpdateLedger="UPDATE tbMain_Group set" +
					" Group_Name = '"+txtDivisionName.getValue()+"', " +
					" userId = '"+sessionBean.getUserId()+"', " +
					" userIp = '"+sessionBean.getUserIp()+"', " +
					" entryTime = CURRENT_TIMESTAMP " +
					" where Group_Id='"+GroupId.getValue()+"'";

			System.out.println("UpdateLedger: "+UpdateLedger);
			session.createSQLQuery(UpdateLedger).executeUpdate();


			this.getParent().showNotification("All information update successfully.");

			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void updateButtonEvent()
	{
		if(!txtDivisionName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnInit(false);
			txtInit(false);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findButtonEvent()
	{
		Window win = new DivisionFindWindow(sessionBean, DivisionId,"DivisionId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(DivisionId.getValue().toString().length() > 0)
				{
					System.out.println(DivisionId.getValue().toString());
					txtClear();
					findInitialise(DivisionId.getValue().toString());
					button.btnEdit.focus();
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String DivisionCode)
	{
		Transaction tx = null;
		String sql = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select vDivisionId,vDivisionName,vEmployeeId,Group_Id from tbDivisionInfo where vDivisionId = '"+DivisionCode+"' ";
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtDivisionNo.setValue(element[0]);
				txtDivisionName.setValue(element[1]);
				cmbDivIncharge.setValue(element[2]);
				GroupId.setValue(element[3]);
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	public void cmbAddEmployeeData()
	{
		cmbDivIncharge.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vEmployeeId,vEmployeeName from tbEmployeeInfo").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDivIncharge.addItem(element[0].toString());
				cmbDivIncharge.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void txtClear() 
	{
		button.btnNew.focus();
		txtDivisionName.setValue("");
		txtDivisionNo.setValue("");
		cmbDivIncharge.setValue(null);
		txtDesignation.setValue("");
	}

	private void focusEnter()
	{
		allComp.add(txtDivisionName);
		allComp.add(cmbDivIncharge);
		allComp.add(button.btnSave);
		allComp.add(button.btnNew);
		new FocusMoveByEnter(this,allComp);
	}

	private void btnInit(boolean t) 
	{
		button.btnNew.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnEdit.setEnabled(t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void txtInit(boolean t)
	{
		txtDivisionNo.setEnabled(!t);
		txtDivisionName.setEnabled(!t);
		cmbDivIncharge.setEnabled(!t);
		txtDesignation.setEnabled(!t);
		nbEmployee.setEnabled(!t);
	}

	private void componentIni(boolean t) 
	{
		txtDivisionNo.setEnabled(!t);
		txtDivisionName.setEnabled(!t);
		cmbDivIncharge.setEnabled(!t);
		txtDesignation.setEnabled(!t);
		nbEmployee.setEnabled(!t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("535px");
		mainLayout.setHeight("190px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("560px");
		setHeight("230px");

		// lblStoreLocation
		lblDivisionNo= new Label("Division ID:");
		lblDivisionNo.setImmediate(false);
		lblDivisionNo.setWidth("-1px");
		lblDivisionNo.setHeight("-1px");
		mainLayout.addComponent(lblDivisionNo, "top:20.0px;left:100.0px;");

		// cmbSLocation
		txtDivisionNo = new TextRead();
		txtDivisionNo.setImmediate(false);
		txtDivisionNo.setWidth("200px");
		txtDivisionNo.setHeight("23px");
		mainLayout.addComponent(txtDivisionNo, "top:20.0px;left:240.0px;");

		// lblStoreLocationAddress
		lblDivisionName= new Label("Division :");
		lblDivisionName.setImmediate(false);
		lblDivisionName.setWidth("-1px");
		lblDivisionName.setHeight("-1px");
		mainLayout.addComponent(lblDivisionName,"top:50.0px;left:100.0px;");

		// txtAddress
		txtDivisionName = new TextField();
		txtDivisionName.setImmediate(false);
		txtDivisionName.setWidth("200px");
		txtDivisionName.setHeight("-1px");
		mainLayout.addComponent(txtDivisionName, "top:50.0px;left:240.0px;");

		// lblStoreLocationAddress
		lblDivIncharge= new Label("Division Incharge :");
		lblDivIncharge.setImmediate(false);
		lblDivIncharge.setWidth("-1px");
		lblDivIncharge.setHeight("-1px");
		mainLayout.addComponent(lblDivIncharge,"top:80.0px;left:100.0px;");

		// txtAddress
		cmbDivIncharge = new ComboBox();
		cmbDivIncharge.setImmediate(true);
		cmbDivIncharge.setWidth("200px");
		cmbDivIncharge.setHeight("23px");
		mainLayout.addComponent(cmbDivIncharge, "top:80.0px;left:240.0px;");

		//nbEmployee
		nbEmployee = new NativeButton();
		nbEmployee.setIcon(new ThemeResource("../icons/add.png"));
		nbEmployee.setImmediate(true);
		nbEmployee.setWidth("30px");
		nbEmployee.setHeight("25px");
		mainLayout.addComponent(nbEmployee, "top:80.0px; left:450.0px;");

		// lblStoreLocationAddress
		lblDesignation= new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("-1px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation,"top:110.0px;left:100.0px;");

		// txtAddress
		txtDesignation = new TextRead();
		txtDesignation.setImmediate(false);
		txtDesignation.setWidth("200px");
		txtDesignation.setHeight("24px");
		mainLayout.addComponent(txtDesignation, "top:110.0px;left:240.0px;");

		mainLayout.addComponent(button, "top:145.0px; left:25.0px ");		
		return mainLayout;
	}
}
