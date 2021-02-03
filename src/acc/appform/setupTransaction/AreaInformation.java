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
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class AreaInformation extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");
	private AbsoluteLayout mainLayout=new AbsoluteLayout();

	private Label lblAreaNo;
	private TextRead txtAreaNo;

	private Label lblDivision;
	private ComboBox cmbDivision;

	private Label lblDividionIncharge;
	private TextRead txtDivisionIncharge;

	private Label lblAreaName;
	private TextField txtAreaName;

	private Label lblMr;
	private ComboBox cmbMr;

	private Label lblDesignation;
	private TextRead txtDesignation;

	private NativeButton nbEmployee;

	private TextRead SubGroupId = new TextRead();
	private TextRead GroupId = new TextRead();

	boolean isUpdate=false;

	private SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();
	private TextField AreaId=new TextField();

	public AreaInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("ZONE INFORMATION :: "+sessionBean.getCompany());
		this.setWidth("580px");
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		componentIni(true);
		txtInit(true);
		btnInit(true);

		cmbAddDivisionData();
		cmbAddEmployeeData();

		btnAction();
		focusEnter();

		authenticationCheck();
		button.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{
			button.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable())
		{
			button.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable())
		{
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
				if(!txtAreaName.getValue().toString().isEmpty())
				{
					if(!cmbMr.getValue().toString().isEmpty())
					{
						saveButtonEvent();
					}
					else
					{
						showNotification("Warning!","Provide Division Incharge",Notification.TYPE_WARNING_MESSAGE);
						cmbDivision.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide Division Name",Notification.TYPE_WARNING_MESSAGE);
					cmbDivision.focus();
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

		cmbMr.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbMr.getValue()!=null)
					setDesignation();
			}
		});

		cmbDivision.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDivision.getValue()!=null)
					setDivDesignation();
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

	private void newButtonEvent() 
	{
		componentIni(false);
		btnInit(false);
		txtClear();
		txtAreaNo.setValue(selectAreaCode());
		cmbDivision.focus();
	}

	private String selectAreaCode()
	{
		String DivisionId = "";
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query=" Select isnull(max(cast(vAreaId as int)),0)+1 from tbAreaInfo ";
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

	public void setDesignation()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query=" select di.designationName from tbEmployeeInfo ei inner join tbDesignationInfo di on ei.vDesignationId=di.designationId where ei.vEmployeeId = '"+cmbMr.getValue().toString()+"' ";
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

	public void setDivDesignation()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query="  select vEmployeeName from tbDivisionInfo where vDivisionId = '"+cmbDivision.getValue().toString()+"' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if(iter.hasNext())
			{
				txtDivisionIncharge.setValue(iter.next().toString());
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
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

			String insertData="INSERT INTO tbAreaInfo values(" +
					" '"+cmbDivision.getValue().toString().trim()+"', " +
					" '"+cmbDivision.getItemCaption(cmbDivision.getValue().toString().trim())+"', " +
					" '"+txtDivisionIncharge.getValue().toString().trim()+"', " +
					" '"+selectAreaCode()+"', " +
					" '"+txtAreaName.getValue().toString().trim()+"', " +
					" '"+cmbMr.getValue().toString().trim()+"', " +
					" '"+cmbMr.getItemCaption(cmbMr.getValue().toString().trim())+"', " +
					" '"+txtDesignation.getValue().toString().trim()+"', " +
					"(Select Group_Id From tbDivisionInfo where" +
					" vDivisionId='"+cmbDivision.getValue()+"'),"+
					" ('S'+ISNULL((SELECT cast((max(substring(Sub_Group_Id,2,len(Sub_Group_Id)-1))+1) as VARCHAR) FROM tbSub_Group),101)) ,"+
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP )";

			System.out.println("insertData: "+insertData);
			session.createSQLQuery(insertData).executeUpdate();

			String sql = "INSERT INTO tbSub_Group(Sub_Group_Id,Sub_Group_Name,Group_Id,userId,userIp,entryTime) VALUES('S'+ISNULL((SELECT cast((max(substring(Sub_Group_Id,2,len(Sub_Group_Id)-1))+1) as VARCHAR) FROM tbSub_Group),101) ,'"+
					txtAreaName.getValue()+"',(Select Group_Id From tbDivisionInfo where vDivisionId='"+cmbDivision.getValue()+"'),'"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)";
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

			String updateData ="UPDATE tbAreaInfo set" +
					" vDivisionId ='"+cmbDivision.getValue().toString().trim()+"' ," +
					" vDivisionName ='"+cmbDivision.getItemCaption(cmbDivision.getValue().toString().trim())+"' ," +
					" vDivisionIncharge ='"+txtDivisionIncharge.getValue().toString().trim()+"' ," +
					" vAreaName ='"+txtAreaName.getValue().toString().trim()+"' ," +
					" vEmployeeId ='"+cmbMr.getValue().toString().trim()+"' ," +
					" vEmployeeName ='"+cmbMr.getItemCaption(cmbMr.getValue().toString().trim())+"' ," +
					" vDesignation ='"+txtDesignation.getValue().toString().trim()+"' " +
					" where vAreaId='"+AreaId.getValue()+"'";

			System.out.println("UpdateProduct: "+updateData);
			session.createSQLQuery(updateData).executeUpdate();

			String UpdateLedger="UPDATE tbSub_Group set" +
					" Sub_Group_Name = '"+txtAreaName.getValue()+"', " +
					" userId = '"+sessionBean.getUserId()+"', " +
					" userIp = '"+sessionBean.getUserIp()+"', " +
					" entryTime = CURRENT_TIMESTAMP " +
					" where Group_Id='"+GroupId.getValue()+"'"+
					" and Sub_Group_Id='"+SubGroupId.getValue()+"'";

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
		if(!txtAreaName.getValue().toString().isEmpty())
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
		Window win = new AreaFindWindow(sessionBean, AreaId,"AreaId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(AreaId.getValue().toString().length() > 0)
				{
					System.out.println(AreaId.getValue().toString());
					txtClear();
					findInitialise(AreaId.getValue().toString());
					button.btnEdit.focus();
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String AreaCode)
	{
		Transaction tx = null;
		String sql = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select vDivisionId,vDivisionIncharge,vAreaId,vAreaName,vEmployeeId,vDesignation,Group_Id,SubGroup_Id from tbAreaInfo where vAreaId = '"+AreaCode+"' ";
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				cmbDivision.setValue(element[0].toString());
				txtDivisionIncharge.setValue(element[1].toString());
				txtAreaNo.setValue(element[2].toString());
				txtAreaName.setValue(element[3].toString());
				cmbMr.setValue(element[4].toString());
				txtDesignation.setValue(element[5].toString());
				GroupId.setValue(element[6].toString());
				SubGroupId.setValue(element[7].toString());

				System.out.println("SubGroupId: "+SubGroupId.getValue());
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
		cmbMr.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vEmployeeId,vEmployeeName from tbEmployeeInfo").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbMr.addItem(element[0].toString());
				cmbMr.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbAddDivisionData()
	{
		cmbDivision.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vDivisionId,vDivisionName from tbDivisionInfo order by iAutoId").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbDivision.addItem(element[0].toString());
				cmbDivision.setItemCaption(element[0].toString(), element[1].toString());
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
		txtAreaName.setValue("");
		txtAreaNo.setValue("");
		cmbMr.setValue(null);
		txtDesignation.setValue("");
		txtDivisionIncharge.setValue("");
		cmbDivision.setValue(null);

	}

	private void focusEnter()
	{
		allComp.add(cmbDivision);
		allComp.add(txtAreaName);
		allComp.add(cmbMr);
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
		txtAreaNo.setEnabled(!t);
		cmbDivision.setEnabled(!t);
		txtAreaName.setEnabled(!t);
		cmbMr.setEnabled(!t);
		txtDesignation.setEnabled(!t);
		txtDivisionIncharge.setEnabled(!t);
	}

	private void componentIni(boolean t) 
	{
		txtAreaNo.setEnabled(!t);
		cmbDivision.setEnabled(!t);
		txtAreaName.setEnabled(!t);
		cmbMr.setEnabled(!t);
		txtDesignation.setEnabled(!t);
		nbEmployee.setEnabled(!t);
		txtDivisionIncharge.setEnabled(!t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("535px");
		mainLayout.setHeight("240px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("560px");
		setHeight("290px");

		// lblStoreLocation
		lblAreaNo= new Label("Zone ID:");
		lblAreaNo.setImmediate(false);
		lblAreaNo.setWidth("-1px");
		lblAreaNo.setHeight("-1px");
		mainLayout.addComponent(lblAreaNo, "top:20.0px;left:100.0px;");

		// cmbSLocation
		txtAreaNo = new TextRead();
		txtAreaNo.setImmediate(false);
		txtAreaNo.setWidth("200px");
		txtAreaNo.setHeight("23px");
		mainLayout.addComponent(txtAreaNo, "top:20.0px;left:240.0px;");

		// lblStoreLocationAddress
		lblDivision= new Label(" Division Name :");
		lblDivision.setImmediate(false);
		lblDivision.setWidth("-1px");
		lblDivision.setHeight("-1px");
		mainLayout.addComponent(lblDivision,"top:50.0px;left:100.0px;");

		// txtAddress
		cmbDivision = new ComboBox();
		cmbDivision.setImmediate(true);
		cmbDivision.setWidth("200px");
		cmbDivision.setHeight("-1px");
		mainLayout.addComponent(cmbDivision, "top:50.0px;left:240.0px;");

		// lblStoreLocationAddress
		lblDividionIncharge= new Label(" Division Incharge :");
		lblDividionIncharge.setImmediate(false);
		lblDividionIncharge.setWidth("-1px");
		lblDividionIncharge.setHeight("-1px");
		mainLayout.addComponent(lblDividionIncharge,"top:80.0px;left:100.0px;");

		// txtAddress
		txtDivisionIncharge = new TextRead();
		txtDivisionIncharge.setImmediate(false);
		txtDivisionIncharge.setWidth("200px");
		txtDivisionIncharge.setHeight("23px");
		mainLayout.addComponent(txtDivisionIncharge, "top:80.0px;left:240.0px;");


		// lblStoreLocationAddress
		lblAreaName= new Label("Zone Name :");
		lblAreaName.setImmediate(false);
		lblAreaName.setWidth("-1px");
		lblAreaName.setHeight("-1px");
		mainLayout.addComponent(lblAreaName,"top:110.0px;left:100.0px;");

		// txtAddress
		txtAreaName = new TextField();
		txtAreaName.setImmediate(false);
		txtAreaName.setWidth("200px");
		txtAreaName.setHeight("-1px");
		mainLayout.addComponent(txtAreaName, "top:110.0px;left:240.0px;");

		// lblStoreLocationAddress
		lblMr= new Label("Name of TSO :");
		lblMr.setImmediate(false);
		lblMr.setWidth("-1px");
		lblMr.setHeight("-1px");
		mainLayout.addComponent(lblMr,"top:140.0px;left:100.0px;");

		// txtAddress
		cmbMr = new ComboBox();
		cmbMr.setImmediate(true);
		cmbMr.setWidth("200px");
		cmbMr.setHeight("-1px");
		mainLayout.addComponent(cmbMr, "top:140.0px;left:240.0px;");

		//nbEmployee
		nbEmployee = new NativeButton();
		nbEmployee.setIcon(new ThemeResource("../icons/add.png"));
		nbEmployee.setImmediate(true);
		nbEmployee.setWidth("30px");
		nbEmployee.setHeight("25px");
		mainLayout.addComponent(nbEmployee, "top:140.0px; left:450.0px;");

		// lblStoreLocationAddress
		lblDesignation= new Label(" Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("-1px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation,"top:170.0px;left:100.0px;");

		// txtAddress
		txtDesignation = new TextRead();
		txtDesignation.setImmediate(false);
		txtDesignation.setWidth("200px");
		txtDesignation.setHeight("24px");
		mainLayout.addComponent(txtDesignation, "top:170.0px;left:240.0px;");

		mainLayout.addComponent(button, "top:210.0px; left:25.0px ");		
		return mainLayout;
	}
}
