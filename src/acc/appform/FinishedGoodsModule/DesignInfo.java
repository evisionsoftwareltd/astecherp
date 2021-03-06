package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.setupTransaction.DeptFindWindow;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class DesignInfo extends Window 
{


	private AbsoluteLayout mainLayout;

	private Label lblDesignName;
	private TextField txtDesignName;

	private Label lblDesignId;
	private TextRead txtDesignId;

	private Label lblDesignDes;
	private TextArea txtDesignDes;
	private Label lblExistDesignName;

	private Label lblline;

	CommonButton cButton = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	String px="250px";
	boolean isUpdate=false;
	boolean isFind=false;
	String updateSID;
	private TextField txtDesignID = new TextField();

	SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();

	public DesignInfo(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		buildMainLayout();
		setContent(mainLayout);
		this.setResizable(false);
		this.setCaption("DESIGN INFORMATION ::" +" "+sessionBean.getCompany() );

		txtInit(true);
		btnIni(true);
		btnAction();
		focusEnter();

		authenticationCheck();
		cButton.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable()){
			cButton.btnSave.setVisible(false);
		}

		if(!sessionBean.isUpdateable()){
			cButton.btnEdit.setVisible(false);
		}

		if(!sessionBean.isDeleteable()){
			cButton.btnDelete.setVisible(false);
		}
	}

	private void focusEnter()
	{
		allComp.add(txtDesignName);
		allComp.add(txtDesignDes);
		allComp.add(cButton.btnNew);
		allComp.add(cButton.btnEdit);
		allComp.add(cButton.btnSave);
		allComp.add(cButton.btnRefresh);
		allComp.add(cButton.btnDelete);
		allComp.add(cButton.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	public void btnAction()
	{
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				txtDesignName.focus();
				newButtonEvent();
			}
		});

		cButton.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtDesignName.getValue().toString().isEmpty()){
					if(sessionBean.isSubmitable()){
						saveBtnAction();
					}else{
						showNotification("Warning,","You have not Proper Authentication to Save.", Notification.TYPE_WARNING_MESSAGE);
					}
				}else{
					showNotification("Provide Design Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});


		cButton.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				txtDesignName.focus();
				if(!txtDesignName.getValue().toString().isEmpty())
				{
					isFind = true;
					updateButtonEvent();
				}
				else
				{
					getParent().showNotification("Warning!,","There are nothing to update", Notification.TYPE_WARNING_MESSAGE);
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

		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				txtClear();
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind=true;
				txtDesignName.setValue("");
				findButtonEvent();
				isFind = false;

			}
		});
		
		txtDesignName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(isFind)
				{
					if(!txtDesignName.getValue().toString().isEmpty())
					{
						if(duplicateName())
						{
							lblExistDesignName.setVisible(true);
							lblExistDesignName.setValue("<b><Font Color='#CD0606'>! Already Exist</Font></b>");
							txtDesignName.setValue("");
							txtDesignName.focus();
						}
						else
						{	
							lblExistDesignName.setVisible(false);
						}
					}
				}
			}
		});
	}
	
	private boolean duplicateName()
	{
		String ColorName="";
		
		if(!isUpdate){
			
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " select vDesignName from tbDesignInfo where vDesignName='"+txtDesignName.getValue().toString().trim()+"' ";
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		}
		return false;
	}

	private void findButtonEvent() 
	{
		Window win = new DesignFindWindow(sessionBean, txtDesignID,"DesignId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtDesignID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtDesignID.getValue().toString());
					cButton.btnEdit.focus();
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String DesignID) 
	{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			updateSID  = DesignID;

			List led = session.createSQLQuery("Select vDesignId,vDesignName,vDesignDescription from tbDesignInfo Where vDesignId = '"+DesignID+"'").list();

			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtDesignId.setValue(element[0]);
				txtDesignName.setValue(element[1]);
				txtDesignDes.setValue(element[2]);
			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void updateButtonEvent()
	{
		if(!txtDesignName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			txtInit(false);
		}
		else{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveBtnAction()
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
						updateData();
						isUpdate = false;
						txtInit(true);
						btnIni(true);
						txtClear();
						cButton.btnNew.focus();
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						isUpdate = false;		
						txtInit(true);
						btnIni(true);
						txtClear();
						cButton.btnNew.focus();
					}
				}
			});
		}

	}

	public void updateData() 
	{
		Transaction tx = null;
		try
		{			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			session.createSQLQuery("Update tbDesignInfo set vDesignName= '"+txtDesignName.getValue().toString().trim()+"', vDesignDescription= '"+txtDesignDes.getValue().toString().trim()+"'," +
					"UserId = '"+sessionBean.getUserId()+"',userIp = '"+sessionBean.getUserIp()+"', EntryTime = CURRENT_TIMESTAMP where iAutoId = '"+updateSID+"'").executeUpdate();

			System.out.println("User IP : "+sessionBean.getUserIp());

			tx.commit();
			this.getParent().showNotification("Desire information update successfully.");

			isUpdate=false;
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void insertData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			session.createSQLQuery("Insert Into tbDesignInfo(vDesignId,vDesignName,vDesignDescription,UserId,UserIp,EntryTime) values('"+autoIdGenerate()+"','"+txtDesignName.getValue().toString().trim()+"','"+txtDesignDes.getValue().toString().trim()+"'," +
					"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP)").executeUpdate();
			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			btnIni(true);
		}
		catch(Exception exp){
			this.getParent().showNotification("Errora",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void refreshButtonEvent() 
	{
		txtInit(true);
		btnIni(true);
	}

	private void newButtonEvent()
	{
		txtInit(false);
		btnIni(false);
		txtDesignId.setValue(autoIdGenerate());
	}

	private void txtClear()
	{
		txtDesignName.setValue("");
		txtDesignId.setValue("");
		txtDesignDes.setValue("");
		lblExistDesignName.setValue("");
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}

	public void txtInit(boolean t)
	{
		txtDesignName.setEnabled(!t);
		txtDesignId.setEnabled(!t);
		txtDesignDes.setEnabled(!t);
	}

	private String autoIdGenerate(){
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "Select isnull(max(iAutoId),0)+1 from tbDesignInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				autoCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return autoCode;
	}


	@AutoGenerated
	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("530px");
		mainLayout.setHeight("220px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("555px");
		setHeight("260px");

		// lblStoreLocation
		lblDesignId= new Label();
		lblDesignId.setImmediate(false);
		lblDesignId.setWidth("-1px");
		lblDesignId.setHeight("-1px");
		lblDesignId.setValue("Design ID :");
		mainLayout.addComponent(lblDesignId, "top:40.0px;left:100.0px;");

		// cmbSLocation
		txtDesignId = new TextRead();
		txtDesignId.setImmediate(false);
		txtDesignId.setWidth("150px");
		txtDesignId.setHeight("23px");
		mainLayout.addComponent(txtDesignId, "top:38.0px;left:250.0px;");


		// lblStoreLocation
		lblDesignName= new Label();
		lblDesignName.setImmediate(false);
		lblDesignName.setWidth("-1px");
		lblDesignName.setHeight("-1px");
		lblDesignName.setValue("Design Name :");
		mainLayout.addComponent(lblDesignName, "top:70.0px;left:100.0px;");

		// cmbSLocation
		txtDesignName = new TextField();
		txtDesignName.setImmediate(true);
		txtDesignName.setWidth("-1px");
		txtDesignName.setHeight("-1px");
		mainLayout.addComponent(txtDesignName, "top:68.0px;left:250.0px;");
		

		lblExistDesignName = new Label();
		lblExistDesignName.setWidth("-1px");
		lblExistDesignName.setHeight("-1px");
		lblExistDesignName.setImmediate(true);
		lblExistDesignName.setContentMode(Label.CONTENT_XHTML);
		lblExistDesignName.setVisible(false);
		lblExistDesignName.setValue("");
		mainLayout.addComponent(lblExistDesignName, " top:68.0px;left:450.0px;");

		// lblStoreLocation
		lblDesignDes= new Label();
		lblDesignDes.setImmediate(false);
		lblDesignDes.setWidth("-1px");
		lblDesignDes.setHeight("-1px");
		lblDesignDes.setValue("Design Description :");
		mainLayout.addComponent(lblDesignDes, "top:100.0px;left:100.0px;");

		// cmbSLocation
		txtDesignDes = new TextArea();
		txtDesignDes.setImmediate(true);
		txtDesignDes.setWidth("250px");
		txtDesignDes.setHeight("40px");
		mainLayout.addComponent(txtDesignDes, "top:98.0px;left:250.0px;");


		lblline = new Label();
		lblline.setImmediate(false);
		lblline.setWidth("-1");
		lblline.setHeight("-1");
		lblline.setValue("________________________________________________________________________");
		mainLayout.addComponent(lblline,"top:140.0px; left:18.0px;");

		mainLayout.addComponent(cButton, "top:175.0px; left:20.0px ");		
		return mainLayout;
	}

}
