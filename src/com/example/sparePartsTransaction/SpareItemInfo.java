package com.example.sparePartsTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.rawMaterialSetup.RawItemCategory;
import com.example.rawMaterialSetup.RawItemSubCategory;
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

public class SpareItemInfo extends Window 
{
	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;

	private Label lblRawItemCode;
	private TextRead txtRawItemCode;

	private Label lblRawItemName;
	private TextField txtRawItemName;

	private Label lblGroup;
	private ComboBox cmbGroup;	

	private Label lblSubGroup;
	private ComboBox cmbSubGroup;

	private Label lblUnit;
	private TextField txtUnit;

	private Label lblRawItemType;
	private ComboBox cmbRawItemType;

	private Label lblMaxLavel;
	private AmountCommaSeperator txtMaxLabel;

	private Label lblMinLavel;
	private AmountCommaSeperator txtMinLabel;


	private Label lblReLavel;
	private AmountCommaSeperator txtReLabel;

	private NativeButton nbGroup;
	private NativeButton nbSubGroup;
	
	private Label lblmodelno;
	private TextField txtmodelno;
	
	private Label lblbrandname;
	private TextField txtbrandname;

	String RawItemId="";
	private String findUpdateRawItemId="";

	String LedgerId="";

	private TextRead ledgerCode = new TextRead();

	private static final String[] itemType = new String[]{"Store Item","Non-Store Item"};

	boolean isUpdate=false;
	int index;
	SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();

	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");

	public SpareItemInfo(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("SPARE PARTS ITEM INFORMATION :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);

		btnIni(true);
		componentIni(true);

		cmbGroupAddData();

		setEventAction();
		focusEnter();
		authenticationCheck();
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

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sessionBean.isUpdateable()){
					isUpdate = true;
					updateButtonEvent();
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				formValidation();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});

		nbGroup.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				groupLink();				
			}
		});

		nbSubGroup.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				subGroupLink();				
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});

		cmbGroup.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbGroup.getValue()!=null)
				{
					cmbSubGroupAddData();
				}
				else
				{
					cmbSubGroup.removeAllItems();
				}
			}
		});

		cmbSubGroup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbSubGroup.getValue()!=null)
				{
					//selectGroupName();
				}
			}
		});
	}

	private String selectRawItemCode()
	{
		String RawItemCode = "";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query = " Select isnull(max(cast(SUBSTRING(vSpareItemCode,4,LEN(vSpareItemCode)) as int)),0)+1 from tbSpareItemInfo";

			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext())
			{
				RawItemCode = iter.next().toString();
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return RawItemCode;
	}

	public void cmbGroupAddData()
	{
		cmbGroup.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT Group_Id,vCategoryName from tbRawItemCategory where vCategoryType like 'Spare Parts' order by iCategoryCode ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbGroup.addItem(element[0].toString());
				cmbGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbSubGroupAddData()
	{
		cmbSubGroup.removeAllItems();
		int i=0;
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery(" SELECT SubGroup_Id,vSubCategoryName FROM tbRawItemSubCategory where Group_Id='"+cmbGroup.getValue().toString()+"' order by iSubCategoryID ").list();

			i=1;
			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSubGroup.addItem(element[0].toString());
				cmbSubGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		if(i==0)
		{
			showNotification("Warning","There are no Sub-Group in this Group");
		}
	}

	private void updateButtonEvent()
	{
		if(!txtRawItemName.getValue().toString().isEmpty())
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void formValidation()
	{
		if(sessionBean.isSubmitable())
		{
			if(!txtRawItemCode.getValue().toString().isEmpty())
			{
				if(!txtRawItemName.getValue().toString().isEmpty())
				{
					if(cmbGroup.getValue()!=null)
					{
						if(!txtUnit.getValue().toString().isEmpty())
						{
							if(cmbRawItemType.getValue()!=null)
							{
								saveButtonEvent();
							}
							else
							{
								getParent().showNotification("Warning","Select Item Type",Notification.TYPE_WARNING_MESSAGE);
								cmbRawItemType.focus();
							}
						}
						else
						{
							getParent().showNotification("Warning","Please provide unit",Notification.TYPE_WARNING_MESSAGE);
							txtUnit.focus();
						}
					}
					else
					{
						getParent().showNotification("Warning","Please select category",Notification.TYPE_WARNING_MESSAGE);
						cmbGroup.focus();
					}
				}
				else
				{
					getParent().showNotification("Warning","Please provide Item name",Notification.TYPE_WARNING_MESSAGE);
					txtRawItemName.focus();
				}
			}
			else
			{
				getParent().showNotification("Warning","Please provide Item code",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			getParent().showNotification("Warning","Please provide Item code",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findButtonEvent() 
	{
		Window win = new SpareItemInfoFind(sessionBean, txtItemID,"ItemId");
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}

	private void findInitialise(String txtItemId) 
	{
		Transaction tx = null;
		String sql = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			sql = "select * from tbSpareItemInfo where vSpareItemCode = '"+txtItemId+"' ";
			List led = session.createSQLQuery(sql).list();
			if (led.iterator().hasNext()) 
			{
				Object[] element = (Object[]) led.iterator().next();

				txtRawItemCode.setValue(element[1]);
				txtRawItemName.setValue(element[2]);

				cmbGroup.setValue(element[3].toString());
				cmbSubGroup.setValue(element[5].toString());
				
				txtmodelno.setValue(element[7].toString());
				txtbrandname.setValue(element[8].toString());

				txtUnit.setValue(element[9]);

				ledgerCode.setValue(element[10].toString());

				cmbRawItemType.setValue(element[11].toString());
				txtMaxLabel.setValue(element[15].toString());
				txtMinLabel.setValue(element[16].toString());
				txtReLabel.setValue(element[17].toString());
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	public void subGroupLink()
	{
		Window win = new RawItemSubCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				showNotification("Warning","Select Group first");
			}

		});
		this.getParent().addWindow(win);
	}

	public void groupLink()
	{
		Window win = new RawItemCategory(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				cmbGroupAddData();
			}

		});
		this.getParent().addWindow(win);
	}

	private void saveButtonEvent()
	{
		if(!isUpdate)
		{
			RawItemId = selectRawItemCode();
			LedgerId="";
			System.out.println("AutoID: "+RawItemId+" LedgerId: "+LedgerId);
		}
		else
		{
			RawItemId = findUpdateRawItemId;
			LedgerId = "";
		}

		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
					}
				}
			});																	
		}
		else
		{									
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();
					}
				}
			});
		}
	}

	public String ledgerId()
	{
		String ledgerId = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			/*String query = " Select cast(isnull(max(cast(replace(Ledger_Id, 'EL', '')as int))+1, 1)as varchar) from tbLedger" +
					" where Ledger_Id like 'EL%' ";*/
			
			String query= "select ISNULL(MAX(CAST(SUBSTRING(Ledger_Id,3,LEN(Ledger_Id)) as int) ),0)+1   from tbLedger where Ledger_Id like 'EL%'";
			
			Iterator iter = session.createSQLQuery(query).list().iterator();

			if (iter.hasNext()) 
			{
				ledgerId = "EL"+iter.next().toString();
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}

		return ledgerId;
	}

	private void insertData()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		System.out.println("AutoID: "+RawItemId+" LedgerId: "+LedgerId);

		String groupId = "";
		String subGroupId = "";

		String createForm = "";
		String subGroup = "";
		String subGroupCaption = "";

		/*List lstGroup = session.createSQLQuery("select 0 zero,('S'+ISNULL((SELECT cast((max(substring(Sub_Group_Id,2,len(Group_Id)-1))+1)" +
				" as VARCHAR) FROM tbSub_Group),101)) id").list();

		List lstSubGroup = session.createSQLQuery("select 0 zero,('S'+ISNULL((SELECT cast((max(substring(Sub_Group_Id,2,len(Group_Id)-1))+1)" +
				" as VARCHAR) FROM tbSub_Group),101)) id").list();

		if (lstGroup.iterator().hasNext()) 
		{
			Object[] element = (Object[]) lstGroup.iterator().next();

			groupId = element[1].toString();
		}

		if (lstSubGroup.iterator().hasNext()) 
		{
			Object[] element = (Object[]) lstSubGroup.iterator().next();

			subGroupId = element[1].toString();
		}*/

		String parentId = "";

		LedgerId=ledgerId();

		createForm = "E1-"+cmbGroup.getValue().toString()+""+(cmbSubGroup.getValue()==null?"":"-"+cmbSubGroup.getValue().toString());
		subGroup = cmbSubGroup.getValue()==null?"":"-"+cmbSubGroup.getValue().toString();
		subGroupCaption = cmbSubGroup.getItemCaption(cmbSubGroup.getValue()==null?"":"-"+cmbSubGroup.getValue().toString());

		try
		{
			String InsertRawItem = " INSERT into tbSpareItemInfo values(" +
					" '"+"RI-"+selectRawItemCode()+"'," +
					" '"+txtRawItemName.getValue().toString().trim()+"'," +
					" '"+cmbGroup.getValue().toString()+"'," +
					" '"+cmbGroup.getItemCaption(cmbGroup.getValue().toString())+"'," +
					" '"+(cmbSubGroup.getValue()==null?"":cmbSubGroup.getValue().toString())+"'," +
					" '"+(cmbSubGroup.getItemCaption(cmbSubGroup.getValue()==null?"":cmbSubGroup.getValue().toString()))+"'," +
					" '"+txtmodelno.getValue().toString().trim()+"','"+txtbrandname.getValue().toString().trim()+"',"+
					" '"+txtUnit.getValue().toString().trim()+"'," +
					" '"+LedgerId+"', " +
					" '"+cmbRawItemType.getItemCaption(cmbRawItemType.getValue())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+txtMaxLabel.getValue().toLowerCase().trim()+"',"+
					" '"+txtMinLabel.getValue().toString().trim()+"','"+txtReLabel.getValue().toString().trim()+"') ";

			System.out.println("Insertquery : "+InsertRawItem);
			session.createSQLQuery(InsertRawItem).executeUpdate();

			if(cmbSubGroup.getValue()!=null)
			{
				parentId = cmbSubGroup.getValue().toString();
			}
			else
			{
				parentId = cmbGroup.getValue().toString();
			}

			String InsertLedger="INSERT into tbLedger values(" +
					" '"+LedgerId+"', " +
					" '"+txtRawItemName.getValue().toString().trim()+"', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+parentId+"', " +
					" '"+createForm+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			System.out.println("InsertLedger : "+InsertLedger);
			session.createSQLQuery(InsertLedger).executeUpdate();

			String LedgerOpen="INSERT into tbLedger_Op_Balance values(" +
					" '"+LedgerId+"', " +
					" '0.00', " +
					" '0.00', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			System.out.println("LedgerOpen : "+LedgerOpen);
			session.createSQLQuery(LedgerOpen).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private boolean updateData()
	{
		String createForm = "";
		String subGroup = "";
		String subGroupCaption = "";

		String parentId="";

		if(cmbSubGroup.getValue()!=null)
		{
			createForm = "E1-"+cmbGroup.getValue().toString()+"-"+cmbSubGroup.getValue().toString();
			subGroup = cmbSubGroup.getValue().toString();
			subGroupCaption = cmbSubGroup.getItemCaption(cmbSubGroup.getValue());
		}
		else
		{
			createForm = "E1-"+cmbGroup.getValue().toString();
			subGroup = "";
			subGroupCaption = "";
		}

		System.out.println("AutoID: "+createForm);

		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String updateRawItem ="UPDATE tbSpareItemInfo set" +
					" vSpareItemName = '"+txtRawItemName.getValue().toString().trim()+"' ," +
					" vGroupId = '"+cmbGroup.getValue().toString()+"' ," +
					" vGroupName = '"+cmbGroup.getItemCaption(cmbGroup.getValue())+"' ," +
					" vSubGroupId = '"+subGroup+"' ," +
					" vSubGroupName = '"+subGroupCaption+"' , vModelNo = '"+txtmodelno+"', vBrandName = '"+txtbrandname+"',"+
					" vUnitName = '"+(txtUnit.getValue().toString().isEmpty()?"":txtUnit.getValue().toString())+"' ," +
					" vProductType = '"+cmbRawItemType.getItemCaption(cmbRawItemType.getValue())+"' ," +
					" vUserName = '"+sessionBean.getUserId()+"', " +
					" vUserIp = '"+sessionBean.getUserIp()+"', " +
					" dEntryTime = CURRENT_TIMESTAMP , mMaxLabel = '"+txtMaxLabel.getValue().toString().trim()+"',"+
					" mMinLabel = '"+txtMinLabel.getValue().toString().trim()+"', mReLabel = '"+txtReLabel.getValue().toString().trim()+"' "+
					" where vSpareItemCode='"+txtRawItemCode.getValue().toString()+"'";

			//System.out.println("UpdateRawItem: "+updateRawItem);
			session.createSQLQuery(updateRawItem).executeUpdate();

			if(cmbSubGroup.getValue()!=null)
			{
				parentId = cmbSubGroup.getValue().toString();
			}
			else
			{
				parentId = cmbGroup.getValue().toString();
			}

			String UpdateLedger = "UPDATE tbLedger set" +
					" Ledger_Name = '"+txtRawItemName.getValue().toString().trim()+"', " +
					" Creation_Year = '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" Parent_Id = '"+parentId+"', " +
					" Create_From = '"+createForm+"', " +
					" userId = '"+sessionBean.getUserId()+"', " +
					" userIp = '"+sessionBean.getUserIp()+"', " +
					" entryTime = CURRENT_TIMESTAMP " +
					" where Ledger_Id='"+ledgerCode.getValue()+"'";

			//System.out.println("UpdateLedger: "+UpdateLedger);
			session.createSQLQuery(UpdateLedger).executeUpdate();

			this.getParent().showNotification("All information update successfully.");

			tx.commit();

			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void newButtonEvent() 
	{
		componentIni(false);
		btnIni(false);
		txtClear();
		txtRawItemCode.setValue("RI-"+selectRawItemCode());
		txtRawItemName.focus();
	}

	private void focusEnter()
	{
		allComp.add(txtRawItemCode);
		allComp.add(txtRawItemName);
		allComp.add(cmbGroup);
		allComp.add(cmbSubGroup);
		allComp.add(txtmodelno);
		allComp.add(txtbrandname);
		allComp.add(txtUnit);
		allComp.add(txtMaxLabel);
		allComp.add(txtMinLabel);
		allComp.add(txtReLabel);
		allComp.add(cmbRawItemType);

		allComp.add(button.btnSave);

		new FocusMoveByEnter(this,allComp);
	}

	public void txtClear()
	{
		txtRawItemCode.setValue("");
		txtRawItemName.setValue("");
		cmbGroup.setValue(null);
		cmbSubGroup.setValue(null);
		txtmodelno.setValue("");
		txtbrandname.setValue("");
		txtUnit.setValue("");
		cmbRawItemType.setValue(null);
		txtMaxLabel.setValue("");
		txtMinLabel.setValue("");
		txtReLabel.setValue("");
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnFind.setEnabled(t);		
	}

	private void componentIni(boolean b) 
	{
		txtRawItemCode.setEnabled(!b);
		txtRawItemName.setEnabled(!b);
		cmbGroup.setEnabled(!b);
		cmbSubGroup.setEnabled(!b);
		nbGroup.setEnabled(!b);
		nbSubGroup.setEnabled(!b);
		txtmodelno.setEnabled(!b);
		txtbrandname.setEnabled(!b);
		txtUnit.setEnabled(!b);
		cmbRawItemType.setEnabled(!b);
		txtMaxLabel.setEnabled(!b);
		txtMinLabel.setEnabled(!b);
		txtReLabel.setEnabled(!b);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("610px");
		setHeight("430px");

		// lblRawItemCode
		lblRawItemCode = new Label("Item Code: ");
		lblRawItemCode.setImmediate(true);
		lblRawItemCode.setWidth("100.0%");
		lblRawItemCode.setHeight("18px");
		mainLayout.addComponent(lblRawItemCode,"top:30.0px;left:50.0px;");

		// txtRawItemCode
		txtRawItemCode = new TextRead();
		txtRawItemCode.setImmediate(false);
		txtRawItemCode.setWidth("100px");
		txtRawItemCode.setHeight("23px");
		mainLayout.addComponent(txtRawItemCode, "top:27.0px;left:190.0px;");

		// lblRawItemName
		lblRawItemName = new Label("Item Name: ");
		lblRawItemName.setImmediate(false);
		lblRawItemName.setWidth("-1px");
		lblRawItemName.setHeight("-1px");
		mainLayout.addComponent(lblRawItemName, " top:55.0px;left:50.0px;");

		// txtRawItemName
		txtRawItemName = new TextField();
		txtRawItemName.setImmediate(false);
		txtRawItemName.setWidth("318px");
		txtRawItemName.setHeight("-1px");
		txtRawItemName.setSecret(false);
		mainLayout.addComponent(txtRawItemName, "top:53.0px;left:190.0px;");

		// lblGroup
		lblGroup = new Label("Category: ");
		lblGroup.setImmediate(false);
		lblGroup.setWidth("-1px");
		lblGroup.setHeight("-1px");
		mainLayout.addComponent(lblGroup, "top:80.0px;left:50.0px;");

		// cmbCategory
		cmbGroup = new ComboBox();
		cmbGroup.setImmediate(true);
		cmbGroup.setWidth("318px");
		cmbGroup.setHeight("24px");
		cmbGroup.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbGroup, "top:78.0px;left:190.0px;");

		// nbGroup
		nbGroup = new NativeButton();
		nbGroup.setIcon(new ThemeResource("../icons/add.png"));
		nbGroup.setImmediate(true);
		nbGroup.setWidth("28px");
		nbGroup.setHeight("24px");
		mainLayout.addComponent(nbGroup, " top:78.0px;left:515.0px;");

		// lblSubGroup
		lblSubGroup = new Label("Sub-Category: ");
		lblSubGroup.setImmediate(false);
		lblSubGroup.setWidth("-1px");
		lblSubGroup.setHeight("-1px");
		mainLayout.addComponent(lblSubGroup, "top:105.0px;left:50.0px;");

		// cmbSubGroup
		cmbSubGroup = new ComboBox();
		cmbSubGroup.setImmediate(false);
		cmbSubGroup.setWidth("318px");
		cmbSubGroup.setHeight("-1px");
		cmbSubGroup.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbSubGroup, "top:103.0px;left:190.0px;");

		// nbSubGroup
		nbSubGroup = new NativeButton();
		nbSubGroup.setIcon(new ThemeResource("../icons/add.png"));
		nbSubGroup.setImmediate(true);
		nbSubGroup.setWidth("28px");
		nbSubGroup.setHeight("24px");
		mainLayout.addComponent(nbSubGroup, "top:103.0px;left:515.0px;");

		//lblmodelno
		lblmodelno=new Label();
		lblmodelno.setCaption("Model No: ");
		lblmodelno.setImmediate(false);
		lblmodelno.setWidth("-1px");
		lblmodelno.setHeight("-1px");
		mainLayout.addComponent(lblmodelno, "top:148.0px; left:50.0px;");
		
		//txtmodelno
		txtmodelno=new TextField();
		txtmodelno.setImmediate(false);
		txtmodelno.setWidth("150px");
		txtmodelno.setHeight("-1px");
		mainLayout.addComponent(txtmodelno, "top:128.0px; left:190.0px;");
		
		//lblbrandname
		lblbrandname=new Label();
		lblbrandname.setImmediate(false);
		lblbrandname.setWidth("-1px");
		lblbrandname.setHeight("-1px");
		lblbrandname.setCaption("Brand Name: ");
		mainLayout.addComponent(lblbrandname, "top:175.0px; left:50.0px;");
		
		//txtbrandname
		txtbrandname=new TextField();
		txtbrandname.setImmediate(false);
		txtbrandname.setWidth("150px");
		txtbrandname.setHeight("-1px");
		mainLayout.addComponent(txtbrandname, "top:155.0px; left:190.0px;");
		
		// lblUnit
		lblUnit = new Label("Unit :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("-1px");
		lblUnit.setHeight("-1px");
		mainLayout.addComponent(lblUnit, "top:184.0px;left:50.0px;");

		// txtUnit
		txtUnit = new TextField();
		txtUnit.setImmediate(false);
		txtUnit.setWidth("100px");
		txtUnit.setHeight("-1px");
		txtUnit.setSecret(false);
		mainLayout.addComponent(txtUnit, "top:182.0px;left:190.0px;");

		// lblUnit
		lblMaxLavel = new Label("Max Level :");
		lblMaxLavel.setImmediate(false);
		lblMaxLavel.setWidth("-1px");
		lblMaxLavel.setHeight("-1px");
		mainLayout.addComponent(lblMaxLavel, "top:209.0px;left:50.0px;");

		// txtUnit
		txtMaxLabel = new AmountCommaSeperator();
		txtMaxLabel.setImmediate(false);
		txtMaxLabel.setWidth("100px");
		txtMaxLabel.setHeight("-1px");
		txtMaxLabel.setSecret(false);
		mainLayout.addComponent(txtMaxLabel, "top:207.0px;left:190.0px;");

		// lblUnit
		lblMinLavel = new Label("Min Level :");
		lblMinLavel.setImmediate(false);
		lblMinLavel.setWidth("-1px");
		lblMinLavel.setHeight("-1px");
		mainLayout.addComponent(lblMinLavel, "top:236.0px;left:50.0px;");

		// txtUnit
		txtMinLabel = new AmountCommaSeperator();
		txtMinLabel.setImmediate(false);
		txtMinLabel.setWidth("100px");
		txtMinLabel.setHeight("-1px");
		txtMinLabel.setSecret(false);
		mainLayout.addComponent(txtMinLabel, "top:234.0px;left:190.0px;");

		lblReLavel = new Label("Re Level :");
		lblReLavel.setImmediate(false);
		lblReLavel.setWidth("-1px");
		lblReLavel.setHeight("-1px");
		mainLayout.addComponent(lblReLavel, "top:263.0px;left:50.0px;");

		// txtUnit
		txtReLabel = new AmountCommaSeperator();
		txtReLabel.setImmediate(false);
		txtReLabel.setWidth("100px");
		txtReLabel.setHeight("-1px");
		txtReLabel.setSecret(false);
		mainLayout.addComponent(txtReLabel, "top:261.0px;left:190.0px;");

		// lblRawItemType
		lblRawItemType = new Label("Item Type :");
		lblRawItemType.setImmediate(false);
		lblRawItemType.setWidth("-1px");
		lblRawItemType.setHeight("-1px");
		mainLayout.addComponent(lblRawItemType, "top:290.0px;left:50.0px;");

		// cmbRawItemType
		cmbRawItemType = new ComboBox();
		cmbRawItemType.setImmediate(true);
		cmbRawItemType.setWidth("150px");
		cmbRawItemType.setHeight("-1px");
		cmbRawItemType.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbRawItemType, "top:288.0px;left:190.0px;");
		for(int i=0; i<itemType.length; i++)
		{cmbRawItemType.addItem(itemType[i]);}

		mainLayout.addComponent(button, "top:318.0px;left:55.0px;");

		return mainLayout;
	}
}
