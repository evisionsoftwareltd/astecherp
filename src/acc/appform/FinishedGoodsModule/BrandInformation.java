package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class BrandInformation extends Window 
{
	private AbsoluteLayout mainLayout;

	private Label lblBrandNo;
	private TextRead txtBrandNo;
	private Label lblBrand;
	private TextField txtBrandName;
	private Label lblExistBrandName;

	private CommonButton button = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");
	private ArrayList<Component> allComp = new ArrayList<Component>(); 
	private TextField BrandId=new TextField();
	boolean isUpdate=false;
	boolean isFind=false;
	SessionBean sessionBean;
	public BrandInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setCaption("BRAND INFORMATION :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		compInit(true);
		btnint(true);

		setEventAction();
		authenticationCheck();
		button.btnNew.focus();
	}

	private void authenticationCheck(){
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

	private void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				newBtnAction();
				System.out.println("New Button");
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				compInit(true);
				btnint(true);
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


		button.btnSave.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtBrandName.getValue().toString().isEmpty())
				{
					saveButtonEvent();
				}
				else
				{
					showNotification("Warning!","Provide Size",Notification.TYPE_WARNING_MESSAGE);
					txtBrandName.focus();
				}
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				txtBrandName.focus();
				if(sessionBean.isUpdateable())
				{
					isFind = true;
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
				isFind = true;
				findButtonEvent();
				isFind = false;
			}
		});

		txtBrandName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(isFind)
				{
					if(!txtBrandName.getValue().toString().isEmpty())
					{
						if(duplicateName())
						{
							lblExistBrandName.setVisible(true);
							lblExistBrandName.setValue("<b><Font Color='#CD0606'>! Already Exist</Font></b>");
							txtBrandName.setValue("");
							txtBrandName.focus();
						}
						else
						{	
							lblExistBrandName.setVisible(false);
						}
					}
				}
			}
		});
	}



	private boolean duplicateName()
	{
		String BrandName="";
		if(!isUpdate)
		{
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " select vBrandName from tbBrandInformation where vBrandName='"+txtBrandName.getValue().toString().trim()+"' ";
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
		Window win = new BrandFindWindow (sessionBean, BrandId,"SizeId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(BrandId.getValue().toString().length() > 0)
				{
					System.out.println(BrandId.getValue().toString());
					txtClear();
					findInitialise(BrandId.getValue().toString());
					button.btnEdit.focus();
				}
			}
		});

		this.getParent().addWindow(win);
	}


	private void findInitialise(String SizeId)
	{
		Transaction tx = null;
		String sql = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select vBrandId,vBrandName from tbBrandInformation where iAutoId='"+SizeId+"'";
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtBrandNo.setValue(element[0]);
				txtBrandName.setValue(element[1]);
				//System.out.println("Value is Show :"+element[0]);
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
		if(!txtBrandName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnint(false);
			compInit(false);//Enable(true);
			//focusEnter();*/
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void saveButtonEvent()
	{

		if(!isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", 
					MessageBox.Icon.QUESTION, "Do you want to Save BrandInformation?",
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
						btnint(true);
						btnint(true);
						txtClear();
					}
				}
			});
		}
		else
		{
			MessageBox mb = new MessageBox(getParent(), "", 
					MessageBox.Icon.QUESTION, "Do you want to Update BrandInformation?",
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
						btnint(true);
						compInit(true);
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

			String insertData="INSERT INTO tbBrandInformation values(" +
					" '"+autoIdGenerate()+"', " +
					" '"+txtBrandName.getValue().toString().trim()+"', " +
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

			String updateProduct ="UPDATE tbBrandInformation set" +
					" vBrandName ='"+txtBrandName.getValue().toString().trim()+"' " +
					" where vBrandId ='"+BrandId.getValue()+"'";

			System.out.println("UpdateProduct: "+updateProduct);
			session.createSQLQuery(updateProduct).executeUpdate();

			this.getParent().showNotification("All information update successfully.");

			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void newBtnAction()
	{
		txtClear();
		compInit(false);
		btnint(false);
		focusEnter();
		txtBrandNo.setValue(autoIdGenerate());
		txtBrandName.focus();
	}

	private String autoIdGenerate()
	{
		String autoCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = "select ISNULL(MAX(vBrandId),0)+1 from tbBrandInformation";

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

	private void compInit(boolean t)
	{
		txtBrandNo.setEnabled(!t);
		txtBrandName.setEnabled(!t);
	}

	private void txtClear()
	{
		txtBrandNo.setValue("");
		txtBrandName.setValue("");
		lblExistBrandName.setValue("");
	}

	private void focusEnter()
	{
		allComp.add(txtBrandNo);
		allComp.add(txtBrandName);

		allComp.add(button.btnNew);
		allComp.add(button.btnEdit);
		allComp.add(button.btnSave);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnDelete);
		allComp.add(button.btnFind);

		new FocusMoveByEnter(this,allComp);
	}

	private void btnint(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);	
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("540px");
		mainLayout.setHeight("140px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("545px");
		setHeight("180px");
		this.setResizable(false);

		//lblSizeId
		lblBrandNo = new Label("Brand ID :");
		lblBrandNo.setImmediate(false);
		lblBrandNo.setWidth("100.0%");
		lblBrandNo.setHeight("-1px");
		mainLayout.addComponent(lblBrandNo,"top:20.0px; left:50.0px;");

		//txtSecId
		txtBrandNo = new TextRead();
		txtBrandNo.setImmediate(false);
		txtBrandNo.setWidth("80px");
		txtBrandNo.setHeight("24px");
		mainLayout.addComponent(txtBrandNo,"top:18.0px; left:130.0px;");

		//lblEmployeeType
		lblBrand = new Label("Brand Name :");
		lblBrand.setImmediate(false);
		lblBrand.setWidth("100%");
		lblBrand.setHeight("-1px");
		mainLayout.addComponent(lblBrand,"top:50px;left:50px;");

		//txtsize
		txtBrandName = new TextField();
		txtBrandName.setImmediate(false);
		txtBrandName.setWidth("200px");
		txtBrandName.setHeight("-1px");
		txtBrandName.setSecret(false);
		mainLayout.addComponent(txtBrandName,"top:48.0px; left:130.0px;");

		lblExistBrandName = new Label();
		lblExistBrandName.setWidth("-1px");
		lblExistBrandName.setHeight("-1px");
		lblExistBrandName.setImmediate(true);
		lblExistBrandName.setContentMode(Label.CONTENT_XHTML);
		lblExistBrandName.setVisible(false);
		lblExistBrandName.setValue("");
		mainLayout.addComponent(lblExistBrandName, " top:48.0px;left:350.0px;");


		mainLayout.addComponent(button, "top:100.0px;left:25.0px;");
		return mainLayout;
	}

}
