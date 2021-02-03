package acc.appform.asset;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.astechac.AstechacApplication;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickShortcut;

@SuppressWarnings("serial")
public class AssetSales extends Window 
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "","Exit");
	private SessionBean sessionBean;
	private GridLayout titleGrid = new GridLayout(1,1);
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout rightVerLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private HorizontalLayout topHorLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private FormLayout formLayout = new FormLayout();

	private CheckBox cash = new CheckBox("Cash");
	private CheckBox credit = new CheckBox("Credit");
	private TextField voucherNo = new TextField("Voucher No[Sales]:");
	private TextField voucherNoDep = new TextField("Voucher No[Dep.]:");
	
	private ComboBox assetName = new ComboBox("Asset Name:");
	private ComboBox salesTo = new ComboBox("Sales To:");
	//private ComboBox costCenter = new ComboBox("Cost Center:");

	private DateField date = new DateField("Date:");
	private Table table = new Table();
	private TextField particular = new TextField();
	private AmountCommaSeperator salesAmt = new AmountCommaSeperator();
	private AmountCommaSeperator costPrice = new AmountCommaSeperator();
	private AmountCommaSeperator writenValue = new AmountCommaSeperator();

	private boolean isUpdate = false;
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	//private Formatter fmt = new Formatter();
	private NumberFormat fmt = new DecimalFormat("#0.00");
	private TextField findVoucherNo = new TextField();
	private TextField findVoucherNoDep = new TextField();
	public TextField vfDate=new TextField(); 
	public AssetSales(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("ASSET SALES :: "+sessionBean.getCompany());
		this.setWidth("670px");
		this.setResizable(false);

		titleGrid.addComponent(new Label("<h3><u>ASSET SALES</u></h3>",Label.CONTENT_XHTML));
		//Button.//
		mainLayout.addComponent(titleGrid);
		mainLayout.setComponentAlignment(titleGrid, Alignment.TOP_CENTER);

		mainLayout.addComponent(topHorLayout);
		Label sp2 = new Label();
		topHorLayout.addComponent(sp2);
		sp2.setWidth("20px");
		topHorLayout.addComponent(new Label("Voucher Type:"));
		topHorLayout.addComponent(cash);
		cash.setImmediate(true);

		topHorLayout.addComponent(credit);
		credit.setImmediate(true);
		
		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yyyy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		date.setWidth("110px");

		formLayout.addComponent(date);
		formLayout.addComponent(assetName);
		assetName.setWidth("280px");
		formLayout.addComponent(salesTo);
		salesTo.setWidth("280px");
		/*formLayout.addComponent(costCenter);
		costCenter.setNullSelectionAllowed(false);
		costCenter.setWidth("280px");
		costCenter.setImmediate(true);*/
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		horLayout.addComponent(formLayout);

		

		FormLayout fl = new FormLayout();
		fl.addComponent(voucherNo);
		fl.addComponent(voucherNoDep);
		rightVerLayout.addComponent(fl);
		rightVerLayout.setSpacing(true);
		//rightVerLayout.setMargin(true);


		horLayout.addComponent(rightVerLayout);
		mainLayout.addComponent(horLayout);
		HorizontalLayout tabLayout = new HorizontalLayout();

		table.setFooterVisible(true);
		table.setWidth("600px");
		table.setHeight("110px");

		table.addContainerProperty("Particular", TextField.class, particular,null,null,Table.ALIGN_CENTER);
		particular.setWidth("220px");
		particular.setHeight("50px");
		table.setColumnWidth("Particular", 220);

		table.addContainerProperty("Sales Amount", TextField.class, salesAmt,null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Sales Amount", 100);
		salesAmt.setWidth("100px");
		salesAmt.setHeight("50px"); 
		salesAmt.setStyleName("fright");

		table.addContainerProperty("Cost Price", TextField.class, costPrice,null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Cost Price", 100);
		costPrice.setWidth("100px");
		costPrice.setHeight("50px"); 
		costPrice.setStyleName("fright");

		table.addContainerProperty("Writen down value", TextField.class, writenValue,null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Writen down value", 120);
		writenValue.setWidth("120px");
		writenValue.setHeight("50px"); 
		writenValue.setStyleName("fright");
		table.addItem(new Object[]{particular,salesAmt,costPrice,writenValue},0);
		tabLayout.addComponent(table);
		tabLayout.setMargin(true);
		mainLayout.addComponent(tabLayout);
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout);
		btnLayout.setSpacing(true);
		btnLayout.setMargin(true);
		this.addComponent(mainLayout);
		ledgerIni();
		assetIni();
		btnIni(true);
		txtEnable(false);
		setButtonAction();
		costCenterInitialize();
		voucherNo.setEnabled(false);
		voucherNoDep.setEnabled(false);
		credit.setValue(true);

		button.btnNew.focus();
		Component ob[] = {cash,credit,assetName,salesTo, /*costCenter,*/ date,particular,salesAmt,costPrice,writenValue,button.btnNew,button.btnEdit,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
		new FocusMoveByEnter(this,ob);
		setButtonShortCut();
		
		
	}

	private void setButtonShortCut()
	{
		this.addAction(new ClickShortcut(button.btnNew, KeyCode.N, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnEdit, KeyCode.U, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnSave, KeyCode.S, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnRefresh, KeyCode.C, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnDelete, KeyCode.D, ModifierKey.ALT,ModifierKey.SHIFT));
		this.addAction(new ClickShortcut(button.btnFind, KeyCode.F, ModifierKey.ALT,ModifierKey.SHIFT));
	}

	private void voucherType(int i)
	{
		if(i!=0)
		{
			cash.setValue(false);
			salesTo.setVisible(true);
		}
		else
		{
			credit.setValue(false);
			salesTo.setVisible(false);
		}
	}

	private void setButtonAction()
	{
		credit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				voucherType(1);
			}
		});

		cash.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				voucherType(0);
			}
		});

		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newBtnAction();
				assetName.focus();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.println(cash.getValue());
				if (cash.getValue().equals(false))
				{
					if(nullCheck())
						//saveBtnAction();
						if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
							saveBtnAction();
						else
							showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
				}
				else
				{
					System.out.println("1");
					if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
						saveBtnAction();
					else
						showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
				}

			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//updateBtnAction();
				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
					updateBtnAction();
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
			}
		});

		button.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				//deleteBtnAction(event);

				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
					deleteBtnAction(event);
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				btnIni(true);
				txtEnable(false);
				txtClear();
				button.btnNew.focus();
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findBtnAction(event);
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private boolean nullCheck()
	{
		if(assetName.getValue()!=null)
		{
			if(salesTo.getValue()!=null)
			{
				if(Double.parseDouble("0"+salesAmt.getValue().toString())>0)
				{
					if(Double.parseDouble("0"+costPrice.getValue().toString())>0)
					{
						if(Double.parseDouble("0"+writenValue.getValue().toString())>0)
						{
							return true;
						}
						else
						{
							this.getParent().showNotification("Warning!","Please provide written down value.",Notification.TYPE_WARNING_MESSAGE);
							writenValue.focus();
							return false;
						}
					}
					else
					{
						this.getParent().showNotification("Warning!","Please provide coset price.",Notification.TYPE_WARNING_MESSAGE);
						costPrice.focus();
						return false;
					}
				}
				else
				{
					this.getParent().showNotification("Warning!","Please provide sales amount.",Notification.TYPE_WARNING_MESSAGE);
					salesAmt.focus();
					return false;
				}
			}
			else
			{
				this.getParent().showNotification("Warning!","Please select a account head.",Notification.TYPE_WARNING_MESSAGE);
				salesTo.focus();
				return false;
			}
		}
		else
		{
			this.getParent().showNotification("Warning!","Please select a asset name.",Notification.TYPE_WARNING_MESSAGE);
			assetName.focus();
			return false;
		}
	}

	private void deleteBtnAction(ClickEvent e)
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
				this.getParent().showNotification("Delete Failed","There are no data for delete.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for delete.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void deleteData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			session.createSQLQuery("DELETE FROM "+voucher+" WHERE Voucher_No = '"+findVoucherNo.getValue()+"' and date = '"+dtfYMD.format(date.getValue())+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			session.createSQLQuery("DELETE FROM "+voucher+" WHERE Voucher_No = '"+findVoucherNoDep.getValue()+"' and date = '"+dtfYMD.format(date.getValue())+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			session.createSQLQuery("DELETE FROM tbAssetSales WHERE Voucher_No = '"+findVoucherNo.getValue()+"' and date = '"+dtfYMD.format(date.getValue())+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();

			tx.commit();
			this.getParent().showNotification("Desired Information delete successfully.");
			isUpdate = false;
			findVoucherNo.setValue("");
			txtClear();
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void newBtnAction()
	{
		findVoucherNo.setValue("");
		btnIni(false);
		txtEnable(true);
		txtClear();
	}

	private void txtClear()
	{
		assetName.setValue(null);
		salesTo.setValue(null);
		voucherNo.setValue("");
		voucherNoDep.setValue("");
		particular.setValue("");
		salesAmt.setValue("");
		findVoucherNo.setValue("");
		costPrice.setValue("");
		writenValue.setValue("");
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
			if(findVoucherNo.getValue().toString().trim().length()>0)
			{
				btnIni(false);
				txtEnable(true);
				isUpdate = true;
			}
			else
			{
				this.getParent().showNotification("Edit Failed","There are no data for Edit.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void insertData()
	{
		if(sessionBean.isSubmitable())
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				int sl = 1,depSl=1;

				double assetVal = (Double.valueOf("0"+salesAmt.getValue())-Double.valueOf("0"+writenValue.getValue()));
				double depriciation = (Double.valueOf("0"+costPrice.getValue())-Double.valueOf("0"+writenValue.getValue()));
				double profitLoss=(Double.valueOf("0"+salesAmt.getValue())-Double.valueOf("0"+costPrice.getValue()));

				if(Boolean.parseBoolean(cash.getValue().toString()))
				{
					Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM tbAssetSales WHERE vouchertype = 'sac' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					if(iter.hasNext())
						sl = Integer.valueOf(iter.next().toString());
					
					Iterator<?> iterDep = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1)  FROM "+voucher+" WHERE vouchertype = 'daj' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					if(iterDep.hasNext())
						depSl = Integer.valueOf(iterDep.next().toString());
					
					// Here AL153 is a Ledger id for (Cash in hand) for asset sales in cash
					String sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id, Narration, CrAmount, DrAmount, vouchertype, costId, userId, userIp, entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('AS-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','AL153','"+particular.getValue()+"','0','0"+
							salesAmt.getValue()+"','sac',  'U-1', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					System.out.println("sql1 is :"+sql);
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId, userId, userIp, entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('AS-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"','0','0"+
							costPrice.getValue()+"','sac', 'U-1', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					
					System.out.println("sql1 is :"+sql);
					session.createSQLQuery(sql).executeUpdate();

					if(depriciation!=0)
					{
						//here EL300 is fixed asset depreciation code
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, " +
								"costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid,approve_by,chqClear) "+
								" VALUES('ASDEP-"+depSl+"','"+dtfYMD.format(date.getValue())+"','EL300','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'daj', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"','"+sessionBean.getUserId()+"',1)";
						session.createSQLQuery(sql).executeUpdate();
						
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, " +
								"costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid,approve_by,chqClear) "+
								" VALUES('ASDEP-"+depSl+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'daj', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"','"+sessionBean.getUserId()+"',1)";
						session.createSQLQuery(sql).executeUpdate();
					}
					if(assetVal!=0)
					{
						// Here IL112 is a Ledger id for (Sales of Assets (Profit / (Loss))) for asset sales in cash
						/*sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId, userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
								" VALUES('AS-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"',"+
								(assetVal>0?assetVal:"0")+","+(assetVal<0?(-1)*assetVal:"0")+",'sac', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();*/
						if(profitLoss>0){
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
									" VALUES('AS-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"',"+profitLoss+",'0','sac', 'U-1','"+sessionBean.getUserId()+"','"+
									sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
							System.out.println("sql3 is :"+sql);
							session.createSQLQuery(sql).executeUpdate();
						}
						else if(profitLoss<0){
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
									" VALUES('AS-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"','0','"+Math.abs(profitLoss)+"','sac', 'U-1','"+sessionBean.getUserId()+"','"+
									sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
							System.out.println("sql4 is :"+sql);
							session.createSQLQuery(sql).executeUpdate();
						}
					}

					sql = "INSERT INTO tbAssetSales (Voucher_No,Date,AssetId,Narration,LedgerId,SalesAmount,Costprice,"+
							"WrittenValue,VoucherType, companyId,voucherNoDep) VALUES('AS-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+
							particular.getValue()+"','AL153','0"+salesAmt+"','0"+costPrice.getValue()+"','0"+writenValue.getValue()+"','sac', '"+ sessionBean.getCompanyId() +"','ASDEP-"+depSl+"')";
					session.createSQLQuery(sql).executeUpdate();
					voucherNo.setValue("AS-CH-"+sl);
				}
				else
				{
					Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM tbAssetSales WHERE vouchertype = 'sao' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					if(iter.hasNext())
						sl = Integer.valueOf(iter.next().toString());
					
					Iterator<?> iterDep = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1)  FROM "+voucher+" WHERE vouchertype = 'daj' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					if(iterDep.hasNext())
						depSl = Integer.valueOf(iterDep.next().toString());

					String sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount," +
							"vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('AS-CR-"+sl+"','"+dtfYMD.format(date.getValue())+"','"+salesTo.getValue()+"','"+
							particular.getValue()+"','0','0"+salesAmt.getValue()+"','sao', 'U-1','"+sessionBean.getUserId()+"'," +
							"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId," +
							"userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('AS-CR-"+sl+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"'," +
							"'"+particular.getValue()+"','0','0"+
							costPrice.getValue()+"','sao', 'U-1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"'," +
							"CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					
					session.createSQLQuery(sql).executeUpdate();

					if(assetVal!=0)
					{
						if(profitLoss>0){
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
									" VALUES('AS-CR-"+sl+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"',"+profitLoss+",'0','sao', 'U-1','"+sessionBean.getUserId()+"','"+
									sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
							session.createSQLQuery(sql).executeUpdate();
						}
						else if(profitLoss<0){
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
									" VALUES('AS-CR-"+sl+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"','0','"+Math.abs(profitLoss)+"','sao', 'U-1','"+sessionBean.getUserId()+"','"+
									sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
							session.createSQLQuery(sql).executeUpdate();
							
						}
						
					}
					if(depriciation!=0)
					{
						//here EL300 is fixed asset depreciation code
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, " +
								"costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid,approve_by,chqClear) "+
								" VALUES('ASDEP-"+depSl+"','"+dtfYMD.format(date.getValue())+"','EL300','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'daj', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 2, 0, '"+ sessionBean.getCompanyId() +"','"+sessionBean.getUserId()+"',1)";
						session.createSQLQuery(sql).executeUpdate();
						
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, " +
								"costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid,approve_by,chqClear) "+
								" VALUES('ASDEP-"+depSl+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'daj', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 2, 0, '"+ sessionBean.getCompanyId() +"','"+sessionBean.getUserId()+"',1)";
						session.createSQLQuery(sql).executeUpdate();
					}

					sql = "INSERT INTO tbAssetSales (Voucher_No,Date,AssetId,Narration,LedgerId,SalesAmount,Costprice,"+
							"WrittenValue,VoucherType, companyId,voucherNoDep) VALUES('AS-CR-"+sl+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+
							particular.getValue()+"','"+salesTo.getValue()+"','0"+salesAmt+"','0"+costPrice.getValue()+"'," +
							"'0"+writenValue.getValue()+"','sao', '"+ sessionBean.getCompanyId() +"','ASDEP-"+depSl+"')";
					session.createSQLQuery(sql).executeUpdate();

					voucherNo.setValue("AS-CR-"+sl);
					voucherNoDep.setValue("ASDEP-"+depSl);
				}

				tx.commit();
				this.getParent().showNotification("All information saved successfully.");
				txtEnable(false);
				btnIni(true);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				voucherNo.setValue("");
				showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			Transaction tx = null;
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				String vNo = findVoucherNo.getValue().toString();
				String vNoDep=findVoucherNoDep.getValue().toString();

				double assetVal = (Double.valueOf("0"+salesAmt.getValue())-Double.valueOf("0"+writenValue.getValue()));
				double depriciation = (Double.valueOf("0"+costPrice.getValue())-Double.valueOf("0"+writenValue.getValue()));
				double profitLoss=(Double.valueOf("0"+salesAmt.getValue())-Double.valueOf("0"+costPrice.getValue()));

				session.createSQLQuery("DELETE FROM "+voucher+" WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
				session.createSQLQuery("DELETE FROM "+voucher+" WHERE Voucher_No = '"+findVoucherNoDep.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
				session.createSQLQuery("DELETE FROM tbAssetSales WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
				/*if(Boolean.parseBoolean(cash.getValue().toString()))
				{
					// Here AL153 is a Ledger id for (Cash in hand) for asset sales in cash
					String sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id, Narration, CrAmount, DrAmount, vouchertype, costId, userId, userIp, entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','AL153','"+particular.getValue()+"','0','0"+
							salesAmt.getValue()+"','sac',  'U-1', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId, userId, userIp, entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"','0','0"+
							costPrice.getValue()+"','sac', 'U-1', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					if(depriciation!=0)
					{
						//here EL300 is fixed asset depreciation code
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
								" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','EL300','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'sao', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}
					if(assetVal!=0)
					{
						// Here IL112 is a Ledger id for (Sales of Assets (Profit / (Loss))) for asset sales in cash
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId, userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
								" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"',"+
								(assetVal>0?assetVal:"0")+","+(assetVal<0?(-1)*assetVal:"0")+",'sac', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}

					sql = "INSERT INTO tbAssetSales (Voucher_No,Date,AssetId,Narration,LedgerId,SalesAmount,Costprice,"+
							"WrittenValue,VoucherType, companyId) VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+
							particular.getValue()+"','AL153','0"+salesAmt+"','0"+costPrice.getValue()+"','0"+writenValue.getValue()+"','sac', '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();
					voucherNo.setValue(vNo);
				}

				else
				{
					String sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+salesTo.getValue()+"','"+particular.getValue()+"','0','0"+
							salesAmt.getValue()+"','sao', 'U-1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"','0','0"+
							costPrice.getValue()+"','sao', 'U-1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					if(assetVal!=0)
					{
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
								" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"',"+
								(assetVal>0?assetVal:"0")+","+(assetVal<0?(-1)*assetVal:"0")+",'sao', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}
					if(depriciation!=0)
					{
						//here EL300 is fixed asset depreciation code
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
								" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','EL300','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'sao', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}

					sql = "INSERT INTO tbAssetSales (Voucher_No,Date,AssetId,Narration,LedgerId,SalesAmount,Costprice,"+
							"WrittenValue,VoucherType, companyId) VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+
							particular.getValue()+"','"+salesTo.getValue()+"','0"+salesAmt+"','0"+costPrice.getValue()+"','0"+writenValue.getValue()+"','sao', '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					voucherNo.setValue(vNo);
				}*/

				if(Boolean.parseBoolean(cash.getValue().toString()))
				{
					/*Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM tbAssetSales WHERE vouchertype = 'sac' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					if(iter.hasNext())
						sl = Integer.valueOf(iter.next().toString());*/
					// Here AL153 is a Ledger id for (Cash in hand) for asset sales in cash
					String sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id, Narration, CrAmount, DrAmount, vouchertype, costId, userId, userIp, entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','AL153','"+particular.getValue()+"','0','0"+
							salesAmt.getValue()+"','sac',  'U-1', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId, userId, userIp, entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"','0','0"+
							costPrice.getValue()+"','sac', 'U-1', '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					/*if(depriciation!=0)
					{
						//here EL300 is fixed asset depreciation code
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
								" VALUES('AS-CR-"+sl+"','"+dtfYMD.format(date.getValue())+"','EL300','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'sao', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}*/
					if(depriciation!=0)
					{
						//here EL300 is fixed asset depreciation code
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, " +
								"costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid,approve_by,chqClear) "+
								" VALUES('"+vNoDep+"','"+dtfYMD.format(date.getValue())+"','EL300','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'daj', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 2, 0, '"+ sessionBean.getCompanyId() +"','"+sessionBean.getUserId()+"',1)";
						session.createSQLQuery(sql).executeUpdate();
						
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, " +
								"costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid,approve_by,chqClear) "+
								" VALUES('"+vNoDep+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'daj', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 2, 0, '"+ sessionBean.getCompanyId() +"','"+sessionBean.getUserId()+"',1)";
						session.createSQLQuery(sql).executeUpdate();
					}
					if(assetVal!=0)
					{
						// Here IL112 is a Ledger id for (Sales of Assets (Profit / (Loss))) for asset sales in cash
						/*sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId, userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
								" VALUES('AS-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"',"+
								(assetVal>0?assetVal:"0")+","+(assetVal<0?(-1)*assetVal:"0")+",'sac', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();*/
						if(profitLoss>0){
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
									" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"',"+profitLoss+",'0','sac', 'U-1','"+sessionBean.getUserId()+"','"+
									sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
							session.createSQLQuery(sql).executeUpdate();
						}
						else if(profitLoss<0){
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
									" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"','0','"+Math.abs(profitLoss)+"','sac', 'U-1','"+sessionBean.getUserId()+"','"+
									sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
							session.createSQLQuery(sql).executeUpdate();
						}
					}

					sql = "INSERT INTO tbAssetSales (Voucher_No,Date,AssetId,Narration,LedgerId,SalesAmount,Costprice,"+
							"WrittenValue,VoucherType, companyId) VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+
							particular.getValue()+"','AL153','0"+salesAmt+"','0"+costPrice.getValue()+"','0"+writenValue.getValue()+"','sac', '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();
					voucherNo.setValue(vNo);
				}
				else
				{
					/*Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM tbAssetSales WHERE vouchertype = 'sao' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
					if(iter.hasNext())
						sl = Integer.valueOf(iter.next().toString());*/

					String sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+salesTo.getValue()+"','"+particular.getValue()+"','0','0"+
							salesAmt.getValue()+"','sao', 'U-1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyId) "+
							" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"','0','0"+
							costPrice.getValue()+"','sao', 'U-1','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					if(assetVal!=0)
					{
						if(profitLoss>0){
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
									" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"',"+profitLoss+",'0','sao', 'U-1','"+sessionBean.getUserId()+"','"+
									sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
							session.createSQLQuery(sql).executeUpdate();
						}
						else if(profitLoss<0){
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
									" VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','IL112','"+particular.getValue()+"','0','"+Math.abs(profitLoss)+"','sao', 'U-1','"+sessionBean.getUserId()+"','"+
									sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
							session.createSQLQuery(sql).executeUpdate();
						}
						
					}
					/*if(depriciation!=0)
					{
						//here EL300 is fixed asset depreciation code
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid) "+
								" VALUES('AS-CR-"+sl+"','"+dtfYMD.format(date.getValue())+"','EL300','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'sao', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 1, 0, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
					}*/
					if(depriciation!=0)
					{
						//here EL300 is fixed asset depreciation code
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype, " +
								"costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid,approve_by,chqClear) "+
								" VALUES('"+vNoDep+"','"+dtfYMD.format(date.getValue())+"','EL300','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'daj', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 2, 0, '"+ sessionBean.getCompanyId() +"','"+sessionBean.getUserId()+"',1)";
						session.createSQLQuery(sql).executeUpdate();
						
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype, " +
								"costId,userId,userIp,entryTime, auditapproveflag, attachBill, companyid,approve_by,chqClear) "+
								" VALUES('"+vNoDep+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+particular.getValue()+"',"+
								(0)+","+(depriciation)+",'daj', 'U-1','"+sessionBean.getUserId()+"','"+
								sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 2, 0, '"+ sessionBean.getCompanyId() +"','"+sessionBean.getUserId()+"',1)";
						session.createSQLQuery(sql).executeUpdate();
					}
					
					sql = "INSERT INTO tbAssetSales (Voucher_No,Date,AssetId,Narration,LedgerId,SalesAmount,Costprice,"+
							"WrittenValue,VoucherType, companyId) VALUES('"+vNo+"','"+dtfYMD.format(date.getValue())+"','"+assetName.getValue()+"','"+
							particular.getValue()+"','"+salesTo.getValue()+"','0"+salesAmt+"','0"+costPrice.getValue()+"','0"+writenValue.getValue()+"','sao', '"+ sessionBean.getCompanyId() +"')";
					session.createSQLQuery(sql).executeUpdate();

					voucherNo.setValue(vNo);
				}
				
				tx.commit();
				this.getParent().showNotification("Desired information updated successfully.");
				txtEnable(false);
				btnIni(true);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			this.getParent().showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void findBtnAction(ClickEvent e)
	{
		Window win = new SearchAssetSales(sessionBean,findVoucherNo,findVoucherNoDep,vfDate);
		win.center();
		this.getParent().addWindow(win);
		win.setModal(true);
		win.setCloseShortcut(KeyCode.ESCAPE);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				findInitialise();
			}
		});
		win.bringToFront();
	}

	@SuppressWarnings("deprecation")
	private void findInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			Iterator<?> iter = session.createSQLQuery("SELECT Voucher_No,Date,AssetId,Narration,LedgerId,SalesAmount,Costprice,"+
					"WrittenValue,VoucherType,voucherNoDep FROM tbAssetSales WHERE Voucher_No = '"+findVoucherNo+"' AND date = '"+vfDate.getValue()+"' and CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
			if(iter.hasNext())
			{
				Object[] element = (Object[]) iter.next();

				voucherNo.setValue(element[0].toString());
				voucherNoDep.setValue(element[9].toString());
				assetName.setValue(element[2].toString());

				if(element[8].toString().equals("sao"))
				{
					salesTo.setValue(element[4].toString());
					credit.setValue(true);
					voucherType(1);
				}
				else
				{
					cash.setValue(true);
					voucherType(0);
				}
				particular.setValue(element[3].toString());

				salesAmt.setValue(fmt.format(Double.valueOf(element[5].toString())));
				costPrice.setValue(fmt.format(Double.valueOf(element[6].toString())));				
				writenValue.setValue(fmt.format(Double.valueOf(element[7].toString())));
				date.setValue(new Date(element[1].toString().replace("-", "/").substring(0,10).trim()));
			}
			this.bringToFront();
			button.btnEdit.focus();
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		//button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void txtEnable(boolean t)
	{		
		assetName.setEnabled(t);
		salesTo.setEnabled(t);
		//costCenter.setEnabled(t);
		date.setEnabled(t);
		particular.setEnabled(t);
		salesAmt.setEnabled(t);
		topHorLayout.setEnabled(t);
		costPrice.setEnabled(t);
		writenValue.setEnabled(t);
	}

	private void ledgerIni()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			List<?> group = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger where companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
			for(Iterator<?> iter = group.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				salesTo.addItem(element[0].toString());
				salesTo.setItemCaption(element[0].toString(), element[1].toString());
			}
			salesTo.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void assetIni()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			try
			{
				assetName.removeAllItems();
			}
			catch(Exception sxp){}
			List<?> group = session.createSQLQuery("SELECT AssetID,vLedgerName FROM tbFixedAsset WHERE [Type] = 'O'").list();
			for(Iterator<?> iter = group.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				assetName.addItem(element[0].toString());
				assetName.setItemCaption(element[0].toString(), element[1].toString());
			}
			assetName.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void costCenterInitialize()
	{/*
		try
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List g = session.createSQLQuery("SELECT id,costCentreName FROM tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' order by costCentreName").list();
			costCenter.removeAllItems();

			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				costCenter.addItem(element[0].toString());
				costCenter.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//System.out.println(exp);
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	 */}


}
