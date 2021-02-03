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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class DepoInformation extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "","Refresh","Find","","","","Exit");
	private AbsoluteLayout mainLayout=new AbsoluteLayout();

	private Label lblDepoNo;
	private TextRead txtDepoNo;

	private Label lblDepoName;
	private TextField txtDepoName;

	private Label lblDepoAddress;
	private TextField txtDepoAddress;

	private Label lblTelephone;
	private TextField txtTelephone;

	private Label lblMobile;
	private TextField txtMobile;

	private Label lblEmail;
	private TextField txtEmail;

	private Label lblDepoIncharge;
	private ComboBox cmbDepoIncharge;

	private Label lblDesignation;
	private TextRead txtDesignation;

	boolean isUpdate=false;

	private NativeButton nbEmployee;

	private SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();
	private TextField DepoId=new TextField();

	public DepoInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("STORE INFORMATION :: "+sessionBean.getCompany());
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
				if(!txtDepoName.getValue().toString().isEmpty())
				{
					if(!txtDepoAddress.getValue().toString().isEmpty())
					{
						/*if(!txtDepoIncharge.getValue().toString().isEmpty())
						{*/
							saveButtonEvent();
						/*}
						else
						{
							showNotification("Warning!","Provide Store Incharge",Notification.TYPE_WARNING_MESSAGE);
							txtDepoIncharge.focus();
						}*/
					}
					else
					{
						showNotification("Warning!","Provide DepoAddress",Notification.TYPE_WARNING_MESSAGE);
						txtDepoAddress.focus();
					}
				}
				else
				{
					showNotification("Warning!","Provide Store Name",Notification.TYPE_WARNING_MESSAGE);
					txtDepoName.focus();
				}
			}
		});
		
		cmbDepoIncharge.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepoIncharge.getValue()!=null)
					setDesignation();
			}
		});
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnInit(false);
		txtClear();
		txtDepoNo.setValue(selectDepoCode());
		txtDepoName.focus();
	}
	
	public void setDesignation()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query=" select di.designationName from tbEmployeeInfo ei inner join tbDesignationInfo di on ei.vDesignationId=di.designationId where ei.vEmployeeId = '"+cmbDepoIncharge.getValue().toString()+"' ";
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

	private String selectDepoCode()
	{
		String DepoId = "";
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String query=" Select isnull(max(cast(vDepoId as int)),0)+1 from tbDepoInformation ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if(iter.hasNext())
			{
				DepoId=iter.next().toString();
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}

		return DepoId;
	}

	private void saveButtonEvent()
	{
		if(!isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", 
					MessageBox.Icon.QUESTION, "Do you want to Save Store info?",
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
					MessageBox.Icon.QUESTION, "Do you want to Update Store info?",
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

			String insertData="INSERT INTO tbDepoInformation values(" +
					" '"+selectDepoCode()+"', " +
					" '"+txtDepoName.getValue().toString().trim()+"', " +
					" '"+txtDepoAddress.getValue().toString().trim()+"', " +
					" '"+txtTelephone.getValue().toString().trim()+"', " +
					" '"+txtMobile.getValue().toString().trim()+"', " +
					" '"+txtEmail.getValue().toString().trim()+"', " +
					" '"+cmbDepoIncharge.getValue().toString().trim()+"', " +
					" '"+cmbDepoIncharge.getItemCaption(cmbDepoIncharge.getValue().toString().trim())+"', " +
					" '"+txtDesignation.getValue().toString().trim()+"', " +
					" '"+sessionBean.getUserId()+"', " +
					" '"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP )";

			System.out.println("insertData: "+insertData);
			session.createSQLQuery(insertData).executeUpdate();

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

			String updateData ="UPDATE tbDepoInformation set" +
					" vDepoName ='"+txtDepoName.getValue().toString().trim()+"', " +
					" vDepoAddress ='"+txtDepoAddress.getValue().toString().trim()+"', " +
					" vTelephone ='"+txtTelephone.getValue().toString().trim()+"' ," +
					" vMobile ='"+txtMobile.getValue().toString().trim()+"', " +
					" vEmail ='"+txtEmail.getValue().toString().trim()+"', " +
					" vDepoInchargeId ='"+cmbDepoIncharge.getValue().toString().trim()+"', " +
					" vDepoIncharge ='"+cmbDepoIncharge.getItemCaption(cmbDepoIncharge.getValue().toString().trim())+"', " +
					" vDesignation ='"+txtDesignation.getValue().toString().trim()+"' " +
					" where vDepoId='"+txtDepoNo.getValue()+"' ";

			System.out.println("UpdateProduct: "+updateData);
			session.createSQLQuery(updateData).executeUpdate();

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
		if(!txtDepoName.getValue().toString().isEmpty())
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
	
	public void cmbAddEmployeeData()
	{
		cmbDepoIncharge.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vEmployeeId,vEmployeeName from tbEmployeeInfo").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepoIncharge.addItem(element[0].toString());
				cmbDepoIncharge.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void findButtonEvent()
	{
		Window win = new DepoFindWindow(sessionBean, DepoId,"DepoId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(DepoId.getValue().toString().length() > 0)
				{
					System.out.println(DepoId.getValue().toString());
					txtClear();
					findInitialise(DepoId.getValue().toString());
					button.btnEdit.focus();
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String DepoCode)
	{
		Transaction tx = null;
		String sql = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select vDepoId,vDepoName,vDepoAddress,vTelephone,vMobile,vEmail,vDepoInchargeId,vDesignation from tbDepoInformation Where vDepoId = '"+DepoCode+"' ";
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtDepoNo.setValue(element[0]);
				txtDepoName.setValue(element[1]);
				txtDepoAddress.setValue(element[2]);
				txtTelephone.setValue(element[3]);
				txtMobile.setValue(element[4]);
				txtEmail.setValue(element[5]);
				cmbDepoIncharge.setValue(element[6].toString());
				txtDesignation.setValue(element[7]);
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void txtClear() 
	{
		button.btnNew.focus();
		txtDepoName.setValue("");
		txtDepoAddress.setValue("");
		txtTelephone.setValue("");
		txtMobile.setValue("");
		txtEmail.setValue("");
		cmbDepoIncharge.setValue(null);
		txtDesignation.setValue("");
	}

	private void focusEnter()
	{
		allComp.add(txtDepoName);
		allComp.add(txtDepoAddress);
		allComp.add(txtTelephone);
		allComp.add(txtMobile);
		allComp.add(txtEmail);
		allComp.add(cmbDepoIncharge);
		allComp.add(txtDesignation);
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
		txtDepoNo.setEnabled(!t);
		txtDepoName.setEnabled(!t);
		txtDepoAddress.setEnabled(!t);
		txtTelephone.setEnabled(!t);
		txtMobile.setEnabled(!t);
		txtEmail.setEnabled(!t);
		cmbDepoIncharge.setEnabled(!t);
		txtDesignation.setEnabled(!t);
	}

	private void componentIni(boolean t) 
	{
		txtDepoNo.setEnabled(!t);
		txtDepoName.setEnabled(!t);
		txtDepoAddress.setEnabled(!t);
		txtTelephone.setEnabled(!t);
		txtMobile.setEnabled(!t);
		txtEmail.setEnabled(!t);
		cmbDepoIncharge.setEnabled(!t);
		txtDesignation.setEnabled(!t);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("535px");
		mainLayout.setHeight("340px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("560px");
		setHeight("380px");

		// lblStoreLocation
		lblDepoNo= new Label("Store ID:");
		lblDepoNo.setImmediate(false);
		lblDepoNo.setWidth("-1px");
		lblDepoNo.setHeight("-1px");
		mainLayout.addComponent(lblDepoNo, "top:20.0px;left:50.0px;");

		// cmbSLocation
		txtDepoNo = new TextRead();
		txtDepoNo.setImmediate(true);
		txtDepoNo.setWidth("80px");
		txtDepoNo.setHeight("23px");
		mainLayout.addComponent(txtDepoNo, "top:20.0px;left:190.0px;");

		// lblStoreLocationAddress
		lblDepoName= new Label("Store Name :");
		lblDepoName.setImmediate(false);
		lblDepoName.setWidth("-1px");
		lblDepoName.setHeight("-1px");
		mainLayout.addComponent(lblDepoName,"top:50.0px;left:50.0px;");

		// txtAddress
		txtDepoName = new TextField();
		txtDepoName.setImmediate(true);
		txtDepoName.setWidth("200px");
		txtDepoName.setHeight("-1px");
		mainLayout.addComponent(txtDepoName, "top:50.0px;left:190.0px;");

		lblDepoAddress= new Label("Store Address :");
		lblDepoAddress.setImmediate(false);
		lblDepoAddress.setWidth("-1px");
		lblDepoAddress.setHeight("-1px");
		mainLayout.addComponent(lblDepoAddress,"top:80.0px;left:50.0px;");

		// txtAddress
		txtDepoAddress = new TextField();
		txtDepoAddress.setImmediate(true);
		txtDepoAddress.setWidth("320px");
		txtDepoAddress.setHeight("46px");
		mainLayout.addComponent(txtDepoAddress, "top:80.0px;left:190.0px;");

		// lblStoreLocationAddress
		lblTelephone= new Label("Telephone :");
		lblTelephone.setImmediate(false);
		lblTelephone.setWidth("-1px");
		lblTelephone.setHeight("-1px");
		mainLayout.addComponent(lblTelephone,"top:130.0px;left:50.0px;");

		// txtAddress
		txtTelephone = new TextField();
		txtTelephone.setImmediate(true);
		txtTelephone.setWidth("200px");
		txtTelephone.setHeight("-1px");
		mainLayout.addComponent(txtTelephone, "top:130.0px;left:190.0px;");

		// lblStoreLocationAddress
		lblMobile= new Label("Mobile :");
		lblMobile.setImmediate(false);
		lblMobile.setWidth("-1px");
		lblMobile.setHeight("-1px");
		mainLayout.addComponent(lblMobile,"top:160.0px;left:50.0px;");

		// txtMobile
		txtMobile = new TextField();
		txtMobile.setImmediate(true);
		txtMobile.setWidth("200px");
		txtMobile.setHeight("-1px");
		mainLayout.addComponent(txtMobile, "top:160.0px;left:190.0px;");

		// lblEmail
		lblEmail= new Label("Email :");
		lblEmail.setImmediate(false);
		lblEmail.setWidth("-1px");
		lblEmail.setHeight("-1px");
		mainLayout.addComponent(lblEmail,"top:190.0px;left:50.0px;");

		// txtEmail
		txtEmail = new TextField();
		txtEmail.setImmediate(true);
		txtEmail.setWidth("200px");
		txtEmail.setHeight("-1px");
		mainLayout.addComponent(txtEmail, "top:190.0px;left:190.0px;");

		// lblEmail
		lblDepoIncharge= new Label("Store Incharge :");
		lblDepoIncharge.setImmediate(true);
		lblDepoIncharge.setWidth("-1px");
		lblDepoIncharge.setHeight("-1px");
		mainLayout.addComponent(lblDepoIncharge,"top:220.0px;left:50.0px;");

		// txtEmail
		cmbDepoIncharge = new ComboBox();
		cmbDepoIncharge.setImmediate(true);
		cmbDepoIncharge.setWidth("200px");
		cmbDepoIncharge.setHeight("24px");
		mainLayout.addComponent(cmbDepoIncharge, "top:220.0px;left:190.0px;");

		// lblStoreLocationAddress
		lblDesignation= new Label("Designation :");
		lblDesignation.setImmediate(false);
		lblDesignation.setWidth("-1px");
		lblDesignation.setHeight("-1px");
		mainLayout.addComponent(lblDesignation,"top:250.0px;left:50.0px;");

		// txtAddress
		txtDesignation = new TextRead();
		txtDesignation.setImmediate(true);
		txtDesignation.setWidth("200px");
		txtDesignation.setHeight("24px");
		mainLayout.addComponent(txtDesignation, "top:250.0px;left:190.0px;");

		mainLayout.addComponent(button, "top:295.0px; left:25.0px ");		
		return mainLayout;
	}
}
