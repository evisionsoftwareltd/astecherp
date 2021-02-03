package acc.appform.asset;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

public class DepreciationChargeAgain extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private TextField particular;
	private Label date;
	private Label lblPart;
	private Label lblDep;
	private Label lblDate;
	private Table table;

	private ArrayList<Label> sl = new ArrayList<Label>();	
	private ArrayList<String> assetId = new ArrayList<String>();
	private ArrayList<Label> assetName = new ArrayList<Label>();
	private ArrayList<Label> acquisitionDate  = new ArrayList<Label>();
	private ArrayList<Label> valueWdv = new ArrayList<Label>();
	private ArrayList<TextField> dep = new ArrayList<TextField>();
	private ArrayList<Label> amount = new ArrayList<Label>();
	private ArrayList<Label> monthlyDep = new ArrayList<Label>();

	private boolean isUpdate = false;
	private DecimalFormat fmt = new DecimalFormat("#0.00");
	private SimpleDateFormat dftYMD = new SimpleDateFormat("yyyy-MM-dd");
	private TextField findVoucherNo = new TextField();

	BigDecimal b ;
	BigDecimal c = new BigDecimal(12) ;

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "","Exit");

	public DepreciationChargeAgain(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("DEPRECIATION CHARGES :: "+this.sessionBean.getCompany());
		this.setResizable(false);
		this.setWidth("800px");

		buildMainLayout();
		setContent(mainLayout);

		tableInitialise();
		buttonActionAdd();

		btnIni(true);
		txtEnable(false);

		button.btnNew.focus();
		Component ob[] = {button.btnNew,button.btnEdit,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
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

	private void buttonActionAdd()
	{
		button.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findVoucherNo.setValue("");
				isUpdate = false;
				newBtnAction(event);
				button.btnSave.focus();
			}
		});

		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				//updateBtnAction(event);
				System.out.println(dftYMD.format(date.getValue()));
				//	if (!new AstechacApplication().isClosedFiscal(dftYMD.format(date.getValue())))
				updateBtnAction(event);
				//	else
				//		showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
			}
		});

		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				//saveBtnAction(event);
				//if (!new AstechacApplication().isClosedFiscal(dftYMD.format(date.getValue())))
				saveBtnAction(event);
				//	else
				//		showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
			}
		});

		button.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				btnIni(true);
				tableInitialise();
				txtEnable(false);
				findVoucherNo.setValue("");
				isUpdate = false;
				button.btnNew.focus();
			}
		});

		button.btnDelete.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				//	deleteBtnAction(event);
				//	if (!new AstechacApplication().isClosedFiscal(dftYMD.format(date.getValue())))
				deleteBtnAction(event);
				//	else
				//		showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);
			}
		});

		button.btnFind.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findVoucherNo.setValue("");
				findBtnAction(event);
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void findBtnAction(ClickEvent evt)
	{
		Window win = new SearchDepCharge(sessionBean,findVoucherNo);
		win.center();
		this.getParent().addWindow(win);
		win.setModal(true);
		win.setCloseShortcut(KeyCode.ESCAPE);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(findVoucherNo.getValue().toString().length()>0)
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
			Iterator iter = session.createSQLQuery("SELECT AssetId,[Voucher_No],[Date],[AssetName],[Narration],[Depreciation],[TotalValue],"+
					"[AQDate],[Dpercent], CompanyId FROM tbDepreciationDetails WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();

			assetId.clear();
			for(int i=0;iter.hasNext();i++)
			{
				if(sl.size()<=i)
					tableRowAdd(i);
				Object[] element = (Object[]) iter.next();

				Date d = new Date(element[2].toString().replace("-", "/").substring(0,10).trim());
				particular.setReadOnly(false);
				particular.setValue(element[4]);
				particular.setReadOnly(true);
				date.setValue(new SimpleDateFormat("dd-MM-yyyy").format(d));

				assetId.add(i, element[0].toString());
				assetName.get(i).setValue(element[3].toString());
				acquisitionDate.get(i).setValue(dftYMD.format(new Date(element[7].toString().replace("-", "/").substring(0,10).trim())));
				//	double w = new Double(element[6].toString());
				//	double r = new Double(element[8].toString());

				//				valueWdv.get(i).setValue(w);
				//				dep.get(i).setValue(r);
				//
				//				amount.get(i).setValue(fmt.format((w*r)/100));				
				//				monthlyDep.get(i).setValue(fmt.format(((w*r)/100)/12));

				BigDecimal w = new BigDecimal(element[6].toString());
				BigDecimal r = new BigDecimal(element[8].toString());

				valueWdv.get(i).setValue(fmt.format(w));
				dep.get(i).setValue(r);

				b = (w.multiply(r)).divide(BigDecimal.valueOf(100));

				amount.get(i).setValue(fmt.format(b).toString());
				monthlyDep.get(i).setValue(fmt.format(Double.parseDouble(amount.get(i).getValue().toString())/12));
				b = BigDecimal.ZERO;
			}
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void updateBtnAction(ClickEvent e)
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
				this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void saveBtnAction(ClickEvent e)
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
						button.btnNew.focus();
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
						System.out.println("S");
						if(assetName.get(0).getValue().toString().equals(""))
						{
							insertData();
						}
						else
						{
							showNotification("Warning!","There are nothing to save.",Notification.TYPE_ERROR_MESSAGE);
						}
						button.btnNew.focus();
					}
				}
			});
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
				Date d = getDepDate();
				String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dftYMD.format(d)+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				//	session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
				Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();		

				int sl = 1;
				String vNo =  "";
				Iterator iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1)  FROM "+voucher+" WHERE vouchertype = 'daj' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
				if(iter.hasNext())
					sl = Integer.valueOf(iter.next().toString());

				String sql = "";
				System.out.println("A");
				for(int i=0;i<assetId.size();i++)
				{
					double r = new Double("0"+dep.get(i).getValue());
					if(r>0)
					{
						vNo = "ASDEP-"+sl;
						sql = "INSERT INTO tbDepreciationDetails(Voucher_No,Date,AssetId,AssetName,Narration,Depreciation,TotalValue,"+
								"AQDate,Dpercent,vType, CompanyId) VALUES('"+vNo+"','"+dftYMD.format(d)+"','"+assetId.get(i)+"','"+assetName.get(i).getValue()+"','"+
								particular.getValue()+"','0"+monthlyDep.get(i).getValue()+"','0"+valueWdv.get(i).getValue()+"','"+acquisitionDate.get(i).getValue()+
								"','"+r+"','daj', '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();

						//credit insert
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,userId,userIp,entryTime, CompanyId) "+
								" VALUES('"+vNo+"','"+dftYMD.format(d)+"','"+assetId.get(i)+"','"+particular.getValue()+"','0','0"+
								monthlyDep.get(i).getValue()+"','daj','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();

						//debit insert
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,userId,userIp,entryTime, CompanyId) "+
								" VALUES('"+vNo+"','"+dftYMD.format(d)+"','EL3','"+particular.getValue()+"','0','0"+
								monthlyDep.get(i).getValue()+"','daj','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, '"+ sessionBean.getCompanyId() +"')";
						session.createSQLQuery(sql).executeUpdate();
						sl++;
					}
				}
				tx.commit();
				this.getParent().showNotification("All information save successfully.");
				txtEnable(false);
				btnIni(true);
			}
			catch(Exception exp)
			{
				tx.rollback();
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
				Date d = getDepDate();
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();

				String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dftYMD.format(d)+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				//	session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
				Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();		


				String sql = "UPDATE tbDepreciationDetails SET Depreciation = '0"+monthlyDep.get(0).getValue()+"',Dpercent = '0"+dep.get(0).getValue()+"' WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				sql = "UPDATE "+voucher+" SET CrAmount = '0"+monthlyDep.get(0).getValue()+"',userId = '"+sessionBean.getUserId()+"',"+
						"userIp = '"+sessionBean.getUserIp()+"',entryTime = CURRENT_TIMESTAMP "+
						"WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND DrAmount = 0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				sql = "UPDATE "+voucher+" SET DrAmount = '0"+monthlyDep.get(0).getValue()+"',userId = '"+sessionBean.getUserId()+"',"+
						"userIp = '"+sessionBean.getUserIp()+"',entryTime = CURRENT_TIMESTAMP "+
						"WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CrAmount = 0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				this.getParent().showNotification("All information save successfully.");
				txtEnable(false);
				btnIni(true);

			}
			catch(Exception exp)
			{
				tx.rollback();
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void deleteBtnAction(ClickEvent e)
	{
		if(sessionBean.isDeleteable())
		{
			if(findVoucherNo.getValue().toString().trim().length()>0)
			{
				MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete voucher no. "+findVoucherNo.getValue()+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
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
			Date d = getDepDate();
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			session.createSQLQuery("DELETE FROM tbDepreciationDetails WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();
			session.createSQLQuery("DELETE FROM VwVoucher WHERE Voucher_No = '"+findVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").executeUpdate();

			tx.commit();
			this.getParent().showNotification("Voucher No. "+findVoucherNo.getValue()+" delete successfully.");
			txtEnable(false);
			btnIni(true);
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void newBtnAction(ClickEvent e)
	{
		isUpdate = false;
		btnIni(false);
		txtEnable(true);
		//System.out.println("A");
		Date d = getDepDate();
		//System.out.println("G");
		particular.setReadOnly(false);
		particular.setValue("Depreciation for the month of "+new SimpleDateFormat("MMMM yyyy").format(d));
		particular.setReadOnly(true);
		date.setValue(new SimpleDateFormat("dd-MM-yyyy").format(d));
		System.out.println("G");
		txtInitialise();
	}

	private Date getDepDate()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String m = session.createSQLQuery("SELECT dbo.getDepDate('"+sessionBean.getCompanyId()+"')").list().iterator().next().toString();
			return (new Date(m.replace("-", "/").substring(0,10).trim()));
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return (new Date());
		}
	}

	private void txtInitialise()
	{
		Transaction tx = null;
		int i = 0;

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dftYMD.format(getDepDate())+"')").list().iterator().next().toString();

			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();

			/*session.createSQLQuery("ALTER VIEW [dbo].[vwDepreciationCharge]"+
					" AS "+
					"SELECT    ab.AssetId, l.Ledger_Name,(SELECT     MAX(dAcquisition) AS Expr1 FROM dbo.tbFixedAsset AS tf "+ 
					"WHERE      (AssetID = ab.AssetId)) AS acqDate,(SELECT     MAX(iDepreciationPer) AS Expr1 "+
					"FROM dbo.tbFixedAsset AS tf WHERE      (AssetID = ab.AssetId)) AS depRate, ab.companyId "+
					"FROM dbo.tbAssetOpBalance AS ab INNER JOIN dbo.tbLedger AS l ON ab.AssetId = l.Ledger_Id"+
					" WHERE     (ab.companyId = "+sessionBean.getCompanyId()+") AND (l.companyId = "+sessionBean.getCompanyId()+")").executeUpdate();*/

			System.out.println("G1");
			Iterator iter = session.createSQLQuery("SELECT distinct AssetId,Ledger_Name,acqDate," +
					" dbo.getWDV(AssetId,'"+dt+"','"+dftYMD.format(getDepDate())+"'," +
					" '"+ sessionBean.getCompanyId() +"') as wdv,depRate FROM vwDepreciationCharge " +
					" WHERE CompanyId = '"+ sessionBean.getCompanyId() +"' AND " +
					" dbo.getWDV(AssetId,'"+dftYMD.format(dt)+"','"+dftYMD.format(getDepDate())+"'," +
					" '"+ sessionBean.getCompanyId() +"')>0 ").list().iterator();

			tx.commit();
			assetId.clear();
			for(i=0;iter.hasNext();i++)
			{
				if(sl.size()<=i)
					tableRowAdd(i);
				Object[] element = (Object[]) iter.next();
				assetId.add(i, element[0].toString());
				assetName.get(i).setValue(element[1].toString());
				acquisitionDate.get(i).setValue(dftYMD.format(new Date(element[2].toString().replace("-", "/").substring(0,10).trim())));

				BigDecimal w = new BigDecimal(element[3].toString());
				BigDecimal r = new BigDecimal(element[4].toString());

				valueWdv.get(i).setValue(fmt.format(w));
				dep.get(i).setValue(r);

				b = (w.multiply(r)).divide(BigDecimal.valueOf(100));

				amount.get(i).setValue(fmt.format(b).toString());
				monthlyDep.get(i).setValue(fmt.format(Double.parseDouble(amount.get(i).getValue().toString())/12));
				b = BigDecimal.ZERO;
			}

			table.setColumnFooter("Asset Name", "Total Asset = "+(i));
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void txtEnable(boolean t)
	{
		for(int i=0;i<dep.size();i++)
		{
			dep.get(i).setEnabled(t);
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

	private void tableInitialise()
	{	
		table.removeAllItems();
		assetId.clear();
		sl.clear();
		assetName.clear();
		acquisitionDate.clear();
		valueWdv.clear();
		dep.clear();
		amount.clear();
		monthlyDep.clear();
		table.setColumnFooter("Asset Name", "Total Asset = "+0);
		for(int i=0;i<17;i++)
		{
			tableRowAdd(i);	
		}
	}

	private void tableRowAdd(int ar)
	{
		sl.add(ar,new Label(""+(ar+1)));
		assetName.add(ar,new Label());
		assetName.get(ar).setWidth("230px");
		assetName.get(ar).setStyleName("fleft");

		acquisitionDate.add(ar,new Label());
		acquisitionDate.get(ar).setWidth("85px");

		valueWdv.add(ar,new Label());
		valueWdv.get(ar).setWidth("70px");

		dep.add(new AmountField());
		dep.get(ar).setWidth("40px");

		dep.get(ar).setDebugId(""+ar);
		dep.get(ar).setImmediate(true);
		dep.get(ar).setTextChangeEventMode(TextChangeEventMode.LAZY);
		dep.get(ar).setTextChangeTimeout(200);

		dep.get(ar).addListener(new TextChangeListener() 
		{
			@Override
			public void textChange(TextChangeEvent event) 
			{
				int id = Integer.valueOf(event.getComponent().getDebugId());
				double r = new Double("0"+event.getText());
				if(r>0)
				{
					double w = new Double("0"+valueWdv.get(id));
					if(w>0)
					{
						amount.get(id).setValue(fmt.format((w*r)/100));
						monthlyDep.get(id).setValue(fmt.format(((w*r)/100)/12));
					}
				}
				else
				{
					amount.get(id).setValue("");
					monthlyDep.get(id).setValue("");
				}
			}
		});

		amount.add(ar,new Label());
		amount.get(ar).setWidth("70px");

		monthlyDep.add(ar,new Label());
		monthlyDep.get(ar).setWidth("70px");
		table.addItem(new Object[]{sl.get(ar),assetName.get(ar),acquisitionDate.get(ar),valueWdv.get(ar),dep.get(ar),amount.get(ar),monthlyDep.get(ar)},ar);
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		setHeight("700px");

		// lblDep
		lblDep = new Label("<h3><u>Depreciation Charges</u></h3>",Label.CONTENT_XHTML);
		lblDep.setImmediate(true);
		lblDep.setWidth("-1px");
		lblDep.setHeight("-1px");
		mainLayout.addComponent(lblDep, "top:5.0px;left:320.0px;");

		// lblPart
		lblPart = new Label("Particular :");
		lblPart.setImmediate(true);
		lblPart.setHeight("-1px");
		lblPart.setWidth("100px");
		mainLayout.addComponent(lblPart, "top:51.0px;left:40.0px;");

		// particular
		particular = new TextField();
		particular.setImmediate(true);
		particular.setReadOnly(true);
		particular.setWidth("350px");
		particular.setHeight("-1px");
		mainLayout.addComponent(particular, "top:49.0px;left:105.0px;");

		// lblDate
		lblDate = new Label("Date :");
		lblDate.setImmediate(true);
		lblDate.setHeight("-1px");
		lblDate.setWidth("80px");
		mainLayout.addComponent(lblDate, "top:50.0px;left:650.0px;");

		// date
		date = new Label("a");
		date.setImmediate(true);
		date.setHeight("-1px");
		date.setWidth("100px");
		date.setValue(new SimpleDateFormat("MMM-yyyy").format(new java.util.Date()));
		mainLayout.addComponent(date, "top:50.0px;left:690.0px;");

		table = new Table();
		table.setFooterVisible(true);
		table.setWidth("720px");
		table.setHeight("500px");

		table.addContainerProperty("SL", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("SL",30);
		table.addContainerProperty("Asset Name", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Asset Name", 230);
		table.addContainerProperty("Acquisition Date", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Acquisition Date", 90);
		table.addContainerProperty("Value(WDV)", Label.class, new Label(),null,null,Table.ALIGN_RIGHT);
		table.setColumnWidth("Value(WDV)", 70);
		table.addContainerProperty("Dep.%", TextField.class, new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Dep.%", 50);
		table.addContainerProperty("Amount", Label.class, new Label(),null,null,Table.ALIGN_RIGHT);
		table.setColumnWidth("Amount", 70);
		table.addContainerProperty("Monthly Dep", Label.class, new Label(),null,null,Table.ALIGN_RIGHT);
		table.setColumnWidth("Monthly Dep", 70);
		mainLayout.addComponent(table, "top:90.0px;left:40.0px;");

		//CButton
		mainLayout.addComponent(button, "top:610.0px;left:100.0px;");

		return mainLayout;
	}
}
