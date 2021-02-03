package acc.appform.transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.astechac.AstechacApplication;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

public class ContraVoucher extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "", "Exit");
	private SessionBean sessionBean;
	private GridLayout grid = new GridLayout(1,1);
	private GridLayout titleGrid = new GridLayout(1,1);
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout rightFormLayout = new FormLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();	
	private FormLayout leftFormLayout = new FormLayout();

	private TextField voucherNo = new TextField("Voucher No:");
	private DateField date = new DateField("Date:");

	private ComboBox transferFrom = new ComboBox("Transfer From:");
	private ComboBox transferTo = new ComboBox("Transfer To:");

	private TextField cheqNo = new TextField("Cheque No:");
	private DateField cheqDate = new DateField("CQ. Date:");

	private Table table = new Table();
	private ComboBox particular = new ComboBox();
	private ComboBox narration = new ComboBox();
	private AmountCommaSeperator amount = new AmountCommaSeperator();
	private CommaSeparator cms = new CommaSeparator();
	private boolean isUpdate = false;
	private SimpleDateFormat dftYMD = new SimpleDateFormat("yyyy-MM-dd");
	public TextField vDate = new TextField();
	public TextField vflag = new TextField();

	public ContraVoucher(SessionBean sessionBean)	
	{
		this.sessionBean = sessionBean;
		this.setCaption("CONTRA ENTRY :: "+sessionBean.getCompany());
		this.setWidth("660px");
		this.setResizable(false);

		titleGrid.addComponent(new Label("<h3><u>CONTRA VOUCHER</u></h3>",Label.CONTENT_XHTML));
		mainLayout.addComponent(titleGrid);
		mainLayout.setComponentAlignment(titleGrid, Alignment.TOP_CENTER);

		leftFormLayout.addComponent(voucherNo);
		leftFormLayout.addComponent(transferFrom);
		transferFrom.setWidth("250px");
		transferFrom.setNullSelectionAllowed(false);
		transferFrom.setImmediate(true);
		leftFormLayout.addComponent(transferTo);
		transferTo.setWidth("250px");
		transferTo.setNullSelectionAllowed(false);
		transferTo.setImmediate(true);

		leftFormLayout.setMargin(true);
		leftFormLayout.setSpacing(true);


		rightFormLayout.addComponent(date);
		rightFormLayout.addComponent(cheqNo);
		cheqNo.setWidth("145px");
		rightFormLayout.addComponent(cheqDate);

		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		date.setWidth("85px");

		cheqDate.setValue(new java.util.Date());
		cheqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		cheqDate.setDateFormat("dd-MM-yy");
		cheqDate.setInvalidAllowed(false);
		cheqDate.setImmediate(true);
		cheqDate.setWidth("85px");

		horLayout.addComponent(leftFormLayout);
		horLayout.addComponent(rightFormLayout);

		mainLayout.addComponent(horLayout);

		HorizontalLayout tabLayout = new HorizontalLayout();
		HorizontalLayout spaceLayout=new HorizontalLayout();
		
/*
		table.setFooterVisible(true);
		table.setWidth("510px");
		table.setHeight("110px");

		table.addContainerProperty("Particular", TextField.class, particular,null,null,Table.ALIGN_CENTER);
		particular.setWidth("360px");
		particular.setHeight("55px");

		table.setColumnWidth("Particular", 370);
		table.addContainerProperty("Amount", TextField.class, amount,null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Amount", 110);
		amount.setWidth("110px");
		amount.setHeight("55px");
		amount.setStyleName("fright");
		table.addItem(new Object[]{particular,amount},0); 


		tabLayout.addComponent(table);
		tabLayout.setMargin(true);
		mainLayout.addComponent(tabLayout);*/
		particular.setImmediate(true);
		particular.setNullSelectionAllowed(false);
		particular.setNewItemsAllowed(true);
		tabLayout.setSpacing(true);
		particular.setWidth("300px");
		//particular.setHeight("200px");
		amount.setWidth("90px");
		amount.setStyleName("fright");
		
		addCmbParticularData();
		tabLayout.setMargin(true);
		spaceLayout.setWidth("35px");
		tabLayout.addComponent(spaceLayout);
		tabLayout.addComponent(new Label("Particular "));
		tabLayout.addComponent(particular);
		tabLayout.addComponent(new Label("Amount "));
		tabLayout.addComponent(amount);
		mainLayout.addComponent(tabLayout);
		
		
		
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout);
		btnLayout.setSpacing(true);
		btnLayout.setMargin(true);
		this.addComponent(mainLayout);
		ledgerIni();
		btnIni(true);
		txtEnable(false);
		setButtonAction();
		voucherNo.setEnabled(false);
		addCmbParticularData();		
		Component ob[] = {transferFrom,transferTo,date,cheqNo,cheqDate,particular,amount,button.btnNew,button.btnEdit,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
		new FocusMoveByEnter(this,ob);
		buttonShortCut();
		button.btnNew.focus();
	}
	public void addCmbParticularData(){
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String sql="select NarrationId,Narration from tbNarrationList where NarrationId like '%CE%'";
			Iterator iter = session.createSQLQuery(sql).list().iterator();
			for(;iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				particular.addItem(element[0]);
				particular.setItemCaption(element[0],element[1].toString());
				
			}
			
			
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			
		}
	}
	
	private void buttonShortCut()
	{
		button.btnNew.setClickShortcut(KeyCode.N, ModifierKey.ALT);
		button.btnEdit.setClickShortcut(KeyCode.U, ModifierKey.ALT);
		button.btnSave.setClickShortcut(KeyCode.S, ModifierKey.ALT);
		button.btnRefresh.setClickShortcut(KeyCode.C, ModifierKey.ALT);
		button.btnDelete.setClickShortcut(KeyCode.D, ModifierKey.ALT);
		button.btnFind.setClickShortcut(KeyCode.F, ModifierKey.ALT);
	}
	
	private void setButtonAction()
	{
		button.btnNew.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newBtnAction(event);
				transferFrom.focus();
			}
		});
		
		button.btnSave.addListener( new ClickListener() 
		{			
			public void buttonClick(ClickEvent event) 
			{
				cheqNo.getValue();
				if(nullCheck())
					
				if (!new AstechacApplication().isClosedFiscal(dftYMD.format(date.getValue())))
					saveBtnAction();
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});
		
		button.btnEdit.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
//				updateBtnAction();
				if (!new AstechacApplication().isClosedFiscal(dftYMD.format(date.getValue())))
					updateBtnAction();
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});
		
		button.btnDelete.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//deleteBtnAction();
				if (!new AstechacApplication().isClosedFiscal(dftYMD.format(date.getValue())))
					deleteBtnAction();
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});
		
		button.btnRefresh.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				btnIni(true);
				txtEnable(false);
				transferFrom.setValue(null);
				transferTo.setValue(null);
				voucherNo.setValue("");
				particular.setValue("");
				amount.setValue("");
				cheqNo.setValue("");
				ledgerIni();
				addCmbParticularData();
				button.btnNew.focus();
			}
		});
		
		button.btnFind.addListener( new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findBtnAction(event);
			}
		});
		
		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				exitButtonEvent();
			}
		});
	}
	
	private void exitButtonEvent() 
	{
		this.close();
	}
	
	private boolean nullCheck()
	{
		if(transferFrom.getValue()!=null)
		{
			if(transferTo.getValue()!=null)
			{
				if (cheqNo.getValue() != null)
				{
					if(Double.valueOf("0"+amount.getValue())>0)
					{
						return true;
					}
					else
					{
						amount.focus();
						showNotification("","Please insert amount.",Notification.TYPE_WARNING_MESSAGE);
						return false;
					}

				}	
				else
				{
					cheqNo.focus();
					showNotification("","Please insert Cheque Number.",Notification.TYPE_WARNING_MESSAGE);
					return false;
				}
			}
			else
			{
				showNotification("","Please select TransferTo head.",Notification.TYPE_WARNING_MESSAGE);
				transferTo.focus();
				return false;
			}
		}
		else
		{
			showNotification("","Please select TransferFrom head.",Notification.TYPE_WARNING_MESSAGE);
			transferFrom.focus();
			return false;
		}

	}
	
	private void deleteBtnAction()
	{
		if(sessionBean.isDeleteable())
		{
			if(voucherNo.getValue().toString().trim().length()>0)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete voucher no. "+voucherNo.getValue()+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							deleteData();
						}
					}
				});
			}
			else
			{
				showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for delete.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void deleteData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dftYMD.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;

			String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
			"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' as UserName,CURRENT_TIMESTAMP as DUDtime,'Update' as TType, 'Null' as costId,'"+sessionBean.getUserIp()+"' userIp, companyId from "+
			""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"'";
			session.createSQLQuery(sql).executeUpdate();

			sql = "INSERT INTO tbDeleteUpdateCheque SELECT Cheque_No,Cheque_Date,PartyAccountDetails,Voucher_No,"+
			"Bank_Id,ClearanceDate,BankName,BranchName,MrNo,PayNo,pbankId,pbranchId,'Admin' AS UserName,"+
			"CURRENT_TIMESTAMP AS DUDtime,'Update' AS TType, companyId FROM "+cheque+" WHERE Voucher_No = '"+voucherNo.getValue()+"'";
			session.createSQLQuery(sql).executeUpdate();

			sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();
			sql = "DELETE FROM "+cheque+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			showNotification("Desired Information delete successfully.");
			isUpdate = false;
			transferFrom.setValue(null);
			transferTo.setValue(null);
			voucherNo.setValue("");
			particular.setValue("");
			amount.setValue("");
			cheqNo.setValue("");
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}
	
	private void newBtnAction(ClickEvent e)
	{
		isUpdate = false;		
		btnIni(false);
		txtEnable(true);
		transferFrom.setValue(null);
		transferTo.setValue(null);
		voucherNo.setValue("");
		particular.setValue("");
		amount.setValue("");
		cheqNo.setValue("");
		cheqDate.setValue(new java.util.Date());
		//date.setValue(new java.util.Date());
	}
	
	private void saveBtnAction()
	{
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						updateData();
					}
				}
			});
		}
		else
		{
			final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						insertData();
					}
				}
			});
		}
	}
	
	private void updateBtnAction()
	{
		if(sessionBean.isUpdateable())
		{
			if(voucherNo.getValue().toString().trim().length()>0)
			{
				btnIni(false);
				txtEnable(true);
				isUpdate = true;
				button.btnSave.focus();
			}
			else
			{
				showNotification("Edit Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void insertData()
	{
		if(sessionBean.isSubmitable())
		{
			Transaction tx = null;

//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
//					&&
//				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
//			{
				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();
					
					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dftYMD.format(date.getValue())+"')").list().iterator().next().toString();
					String voucher =  "voucher"+fsl;
					String cheque =  "chequeDetails"+fsl;

					int sl = 1;

					Iterator iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE vouchertype = 'jau' or vouchertype = 'jcv' and CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					try
					{
						if(iter.hasNext())
							sl = Integer.valueOf(iter.next().toString());
					}
					catch(Exception exp)
					{

					}
					voucherNo.setValue("JV-NO-"+sl);

					String sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,userId,userIp,entryTime, auditapproveflag, companyId) "+
					" VALUES('"+voucherNo.getValue()+"','"+dftYMD.format(date.getValue())+"','"+transferFrom.getValue()+"','"+particular.getValue()+"','0"+
					amount.getValue()+"','0','jcv','"+cheqNo.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,userId,userIp,entryTime, auditapproveflag, companyId) "+
					" VALUES('"+voucherNo.getValue()+"','"+dftYMD.format(date.getValue())+"','"+transferTo.getValue()+"','"+particular.getValue()+"','0"+
					amount.getValue()+"','0','jcv','"+cheqNo.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					//chequeDetails insert 
					if(!transferTo.getValue().toString().equalsIgnoreCase("AL1"))
					{
						sql = "INSERT INTO "+cheque+"(Cheque_No,Cheque_Date,Voucher_No,Bank_Id, companyId) VALUES('"+cheqNo.getValue()+"','"+
						dftYMD.format(cheqDate.getValue())+"','"+voucherNo.getValue()+"','"+transferTo.getValue()+"', '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}

					if(!transferFrom.getValue().toString().equalsIgnoreCase("AL1"))
					{
						sql = "INSERT INTO "+cheque+"(Cheque_No,Cheque_Date,Voucher_No,Bank_Id, companyId) VALUES('"+cheqNo.getValue()+"','"+
						dftYMD.format(cheqDate.getValue())+"','"+voucherNo.getValue()+"','"+transferFrom.getValue()+"', '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}

					tx.commit();
					showNotification("All information save successfully.");
					txtEnable(false);
					btnIni(true);
					button.btnNew.focus();
				}catch(Exception exp){
					showNotification(
							"Error",
							exp+"",
							Notification.TYPE_ERROR_MESSAGE);
					tx.rollback();
				}

//			}else{
//				showNotification(
//						"",
//						"Transaction date is not valid. Transaction date must be within the working fiscal year.",
//						Notification.TYPE_WARNING_MESSAGE);
//				date.focus();
//			}

		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			Transaction tx = null;

//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue()))>=Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
//					&&
//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue()))<=Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
//			{
				try
				{
					Session session = SessionFactoryUtil.getInstance().getCurrentSession();
					tx = session.beginTransaction();
					String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dftYMD.format(date.getValue())+"')").list().iterator().next().toString();
					String voucher =  "voucher"+fsl;
					String cheque =  "chequeDetails"+fsl;

					String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
					"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' as UserName,CURRENT_TIMESTAMP as DUDtime,'Update' as TType, 'Null' as costId,'"+sessionBean.getUserIp()+"' userIp, companyId from "+
					""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"'";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO tbDeleteUpdateCheque SELECT Cheque_No,Cheque_Date,PartyAccountDetails,Voucher_No,"+
					"Bank_Id,ClearanceDate,BankName,BranchName,MrNo,PayNo,pbankId,pbranchId,'Admin' AS UserName,"+
					"CURRENT_TIMESTAMP AS DUDtime,'Update' AS TType, companyId FROM "+cheque+" WHERE Voucher_No = '"+voucherNo.getValue()+"'";
					session.createSQLQuery(sql).executeUpdate();

					sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
					session.createSQLQuery(sql).executeUpdate();

					sql = "DELETE FROM "+cheque+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,userId,userIp,entryTime, auditapproveflag, companyId) "+
					" VALUES('"+voucherNo.getValue()+"','"+dftYMD.format(date.getValue())+"','"+transferFrom.getValue()+"','"+particular.getValue()+"','0"+
					amount.getValue()+"','0','jcv','"+cheqNo.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,userId,userIp,entryTime, auditapproveflag, companyId) "+
					" VALUES('"+voucherNo.getValue()+"','"+dftYMD.format(date.getValue())+"','"+transferTo.getValue()+"','"+particular.getValue()+"','0"+
					amount.getValue()+"','0','jcv','"+cheqNo.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					//chequeDetails insert 
					if(!transferTo.getValue().toString().equalsIgnoreCase("AL1"))
					{
						sql = "INSERT INTO "+cheque+"(Cheque_No,Cheque_Date,Voucher_No,Bank_Id, companyId) VALUES('"+cheqNo.getValue()+"','"+
						dftYMD.format(cheqDate.getValue())+"','"+voucherNo.getValue()+"','"+transferTo.getValue()+"', '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}

					if(!transferFrom.getValue().toString().equalsIgnoreCase("AL1"))
					{
						sql = "INSERT INTO "+cheque+"(Cheque_No,Cheque_Date,Voucher_No,Bank_Id, companyId) VALUES('"+cheqNo.getValue()+"','"+
						dftYMD.format(cheqDate.getValue())+"','"+voucherNo.getValue()+"','"+transferFrom.getValue()+"', '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}

					sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
					"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' as UserName,CURRENT_TIMESTAMP as DUDtime,'New' as TType, 'Null' as costId,'"+sessionBean.getUserIp()+"' userIp, companyId from "+
					""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"'";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO tbDeleteUpdateCheque SELECT Cheque_No,Cheque_Date,PartyAccountDetails,Voucher_No,"+
					"Bank_Id,ClearanceDate,BankName,BranchName,MrNo,PayNo,pbankId,pbranchId,'Admin' AS UserName,"+
					"CURRENT_TIMESTAMP AS DUDtime,'New' AS TType, companyId FROM "+cheque+" WHERE Voucher_No = '"+voucherNo.getValue()+"'";
					session.createSQLQuery(sql).executeUpdate();

					tx.commit();
					showNotification("Desired voucher no update successfully.");
					txtEnable(false);
					btnIni(true);
					button.btnNew.focus();
				}
				catch(Exception exp)
				{
					showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
					tx.rollback();
				}

//			}
//			else
//			{
//				showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
//				date.focus();
//			}

		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void findBtnAction(ClickEvent e)
	{
		String sqlF = "SELECT DISTINCT(Voucher_No),Date,Narration FROM vwVoucher WHERE vouchertype = 'jcv' AND companyId = '"+ sessionBean.getCompanyId() +"' AND ";
		String sqlE = "  order by Date";
		vflag.setValue("jcv");
		Window win = new JVFind(sessionBean,sqlF,sqlE,voucherNo,vDate,vflag);	
		win.center();
		this.getParent().addWindow(win);
		win.setModal(true);
		win.setCloseShortcut(KeyCode.ESCAPE);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(voucherNo.getValue().toString().length()>0)
					findInitialise();
			}
		});
		win.bringToFront();
	}

	private void findInitialise()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+vDate.getValue()+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;
			
			Iterator iter = session.createSQLQuery("SELECT date,ledger_Id,drAmount,crAmount,narration FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();

			Object[] element = (Object[]) iter.next();
			date.setValue(new Date(element[0].toString().replace("-", "/").substring(0,10).trim()));
			particular.setValue(element[4]);

			if(Double.valueOf(element[2].toString())>0)
			{
				transferTo.setValue(element[1]);
				amount.setValue(Double.valueOf(element[2].toString()));
			}
			else
			{
				transferFrom.setValue(element[1]);
				amount.setValue(Double.valueOf(element[3].toString()));
			}

			element = (Object[]) iter.next();
			if(Double.valueOf(element[3].toString())>0)
			{
				transferFrom.setValue(element[1]);
			}
			else
			{
				transferTo.setValue(element[1]);
			}

			Iterator chk = session.createSQLQuery("SELECT cheque_No,cheque_Date FROM "+cheque+" WHERE voucher_No = '"+ voucherNo.getValue() +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();

			if(chk.hasNext())
			{
				element = (Object[]) chk.next();
				cheqNo.setValue(element[0]);
				cheqDate.setValue(new Date(element[1].toString().replace("-", "/").substring(0,10).trim()));
			}
			this.bringToFront();
			button.btnEdit.focus();

		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void txtEnable(boolean t)
	{
		cheqNo.setEnabled(t);
		transferFrom.setEnabled(t);
		transferTo.setEnabled(t);
		date.setEnabled(t);
		cheqDate.setEnabled(t);
		particular.setEnabled(t);
		narration.setEnabled(t);
		amount.setEnabled(t);
	}

	private void ledgerIni()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			//List group = session.createQuery("SELECT ledgerId,ledgerName FROM TbLedger WHERE substring(createFrom,1,2) = 'A9' OR ledgerId = 'AL1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledgerName").list();
			List group = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A9', 'L7') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
			 
			transferFrom.addListener(new ValueChangeListener()
			{
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					headSelect(transferFrom.getValue()== null?"x":event.getProperty().toString());
				}
			});

			transferTo.addListener(new ValueChangeListener()
			{
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					headSelect(transferTo.getValue()== null?"x":event.getProperty().toString());
				}
			});

			transferFrom.removeAllItems();
			transferTo.removeAllItems();
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				transferFrom.addItem(element[0].toString());
				transferFrom.setItemCaption(element[0].toString(), element[1].toString());

				transferTo.addItem(element[0].toString());
				transferTo.setItemCaption(element[0].toString(), element[1].toString());
			}			
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private void headSelect(String head)
	{
		Transaction tx = null;
		try
		{
			if(head != "x")
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				String msg = "";
				Iterator iter = session.createSQLQuery("SELECT substring(r,1,1) a,h+isnull('\\'+g,'')+isnull('\\'+s,'')+'\\'+l b FROM VwLedgerList WHERE ledger_Id = '"+head+"' AND CompanyId in ('0', '"+ sessionBean.getCompanyId() +"')").list().iterator();
				Object[] element = (Object[]) iter.next();
				if(element[0].toString().equalsIgnoreCase("A"))
					msg = "Assets\\"+element[1].toString();
				else if(element[0].toString().equalsIgnoreCase("I"))
					msg = "Income\\"+element[1].toString();
				else if(element[0].toString().equalsIgnoreCase("E"))
					msg = "Expenses\\"+element[1].toString();
				else 
					msg = "Liabilities\\"+element[1].toString();

				showNotification("",msg,Notification.TYPE_TRAY_NOTIFICATION);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
