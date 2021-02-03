package acc.appform.transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import acc.appform.accountsSetup.CostInformation;
import acc.appform.accountsSetup.LedgerCreate;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExample;
import com.common.share.MessageBox;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.example.astechac.AstechacApplication;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class JournalVoucher extends Window
{
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "","Exit");

	private SessionBean sessionBean;

	private HorizontalLayout btnLayout = new HorizontalLayout();

	private Table table = new Table();

	private ArrayList<ComboBox> acHead = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> costCentreMulti = new ArrayList<ComboBox>();
	private ArrayList<TextRead> balance = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> debit = new ArrayList<AmountCommaSeperator>();

	private boolean isUpdate = false;

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private NumberFormat frmt = new DecimalFormat("#0.00");
	private CommaSeparator cms = new CommaSeparator();

	private int tr = 7;

	public TextField vflag = new TextField();
	public TextField vDate = new TextField();

	private AbsoluteLayout mainLayout;

	private ComboBox debCrdHead;
	private ComboBox costCentre;
	private ComboBox description = new ComboBox();

	private Label lblBalance;
	private Label lblDebitHead;
	private Label lblCostCentre;
	private Label lblVoucherNo;
	private Label lblDate;
	private Label lblVoucherType;
	private Label lblDescription;
	private Label lblSku;

	private TextField voucherNo;
	private TextRead debCrdBal;

	private NativeButton btnCostCentre;
	private NativeButton btndebCrdHead;

	private DateField date;


	private ComboBox cmbSku;
	private CheckBox crdVoucher;
	private CheckBox debVoucher;

	private ImmediateUploadExample jvUpload = new ImmediateUploadExample("");

	String jvPdf = null;
	String filePathTmp = "";

	String imageLoc = "0";

	Button btnPreview;
	String findFiscalYear = "";

	public JournalVoucher(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("810px");
		this.setCaption("JOURNAL VOUCHER :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		buttonActionAdd();

		debCrdHeadIni();

		btnIni(true);
		txtEnable(false);

		txtDisable();

		voucherType(0);

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
		List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();

		costCentreIni(costCentre, costCenter);
		addCmbParticularData();

		Component allComp[] = {date,debVoucher,crdVoucher,costCentre,debCrdHead,acHead.get(0),button.btnNew,button.btnEdit,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
		new FocusMoveByEnter(this,allComp);

		setButtonShortCut();

		button.btnNew.focus();	
		addCmbSkuData();
	}

	public void addCmbSkuData()
	{
		cmbSku.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = "select vProductId,vProductName from tbFinishedProductInfo order by vProductName";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			for(;iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbSku.addItem(element[0]);
				cmbSku.setItemCaption(element[0],element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	public void addCmbParticularData()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = "select NarrationId,Narration from tbNarrationList where NarrationId like '%JV%'";
			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
			for(;iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				description.addItem(element[1]);
				description.setItemCaption(element[1],element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);

		}
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
		debVoucher.setValue(true);
		crdVoucher.setValue(true);
		if(i!=0)
		{
			debVoucher.setValue(false);
			table.setColumnHeader("Debit","Debit Amount");
			//	debCrdHead.setCaption("Credit Head:");
			lblDebitHead.setValue("Credit Head:");
		}
		else
		{
			crdVoucher.setValue(false);
			table.setColumnHeader("Debit","Credit Amount");
			//	debCrdHead.setCaption("Debit Head  :");
			lblDebitHead.setValue("Debit Head  :");
		}
	}
	private boolean sameHeadAllowed()
	{
		boolean ret = true;
		for(int i=0; i<acHead.size(); i++)
		{
			if(acHead.get(i).getValue()!=null)
			{
				if(new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken().equals(debCrdHead.getValue().toString()))
				{
					ret = false;
					break;
				}
			}
		}

		return ret;
	}
	private void buttonActionAdd()
	{
		button.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				newBtnAction();
				costCentre.focus();
			}
		});

		button.btnEdit.addListener( new ClickListener()
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

		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{if(sameHeadAllowed())
			{
				if(nullCheck())
					//saveBtnAction();
					if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
						saveBtnAction();
					else
						showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	
			}
			else{
				showNotification("Warning!","Same head is not allowed",Notification.TYPE_WARNING_MESSAGE);
				debCrdHead.focus();
			}
			}
		});

		button.btnRefresh.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				btnIni(true);
				txtEnable(false);
				txtClear();
				debCrdHeadIni();
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				session.beginTransaction();
				List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();
				costCentreIni(costCentre, costCenter);
				table.removeAllItems();
				tableInitialise();
				addCmbParticularData();
				button.btnNew.focus();
			}
		});

		button.btnDelete.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				//deleteBtnAction();
				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
					deleteBtnAction();
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});

		button.btnFind.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				findBtnAction();
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		button.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBtnAction();
			}
		});

		debVoucher.addListener(  new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				voucherType(0);
			}
		});

		crdVoucher.addListener(  new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				voucherType(1);
			}
		});

		debCrdHead.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{

				headSelect(debCrdHead.getValue()== null?"x":debCrdHead.getValue().toString(),-1);				
			}
		});

		btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				// Hyperlink to a given URL
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
					getWindow().open(new ExternalResource(link),"_blank", 500, 200, Window.BORDER_NONE);
				}
				if(isUpdate)
				{
					if(!jvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", "report")+filePathTmp;
							getWindow().open(new ExternalResource(link),"_blank", 500, 200, Window.BORDER_NONE);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(jvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", "report")+filePathTmp;
						getWindow().open(new ExternalResource(link),"_blank", 500, 200, Window.BORDER_NONE);
					}
				}
			}
		});

		jvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"","");
				System.out.println("Done");
			}
		});

		btnCostCentre.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				costCentreLink();	
			}
		});

		btndebCrdHead.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				headLink();	
			}
		});
	}

	private void previewBtnAction()
	{
		if(!voucherNo.getValue().toString().isEmpty())
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			tx.commit();

			showReport();
		}
		else
		{
			showNotification("Warning","There are no data for Preview",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void showReport()
	{
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", "");
			hm.put("urlLink", link);

			String sql = "SELECT * FROM vwJournalVoucher WHERE Voucher_No in('"+voucherNo.getValue().toString()+"') And companyId = '"+ sessionBean.getCompanyId() +"' and company_Id = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";

			hm.put("sql",sql);
			Window win = new ReportViewer(hm,"report/account/voucher/JournalVoucher.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			win.setStyleName("cwindow");
			getParent().getWindow().addWindow(win);
			win.setCaption("JOURNAL VOUCHER :: "+sessionBean.getCompany());
		}
		catch(Exception ex)
		{
			showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public void headLink()
	{
		Window win = new LedgerCreate(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				debCrdHeadIni();
			}
		});
		this.getParent().addWindow(win);
	}

	public void costCentreLink()
	{
		Window win = new CostInformation(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
				List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();
				costCentreIni(costCentre, costCenter);
			}
		});
		this.getParent().addWindow(win);
	}

	private String imagePath(int flag,String str,String fiscalYearNo)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			// image move
			if(jvUpload.fileName.trim().length()>0)
				try {
					if(jvUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId()+"JV";
						fileMove(basePath+jvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						jvPdf = SessionBean.imagePathTmp+path+".jpg";
						filePathTmp = path+".jpg";
					}else{
						String path = sessionBean.getUserId()+"JV";
						fileMove(basePath+jvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						jvPdf = SessionBean.imagePathTmp+path+".pdf";
						filePathTmp = path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return jvPdf;
		}

		if(flag==1)
		{
			// image move
			if(jvUpload.fileName.trim().length()>0)
				try {
					if(jvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+jvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/journalBillPayment"+fiscalYearNo+"/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/journalBillPayment"+fiscalYearNo+"/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+jvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/journalBillPayment"+fiscalYearNo+"/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/journalBillPayment"+fiscalYearNo+"/"+path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return stuImage;
		}

		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

	private boolean nullCheck()
	{
		boolean a = false;
		if(debCrdHead.getValue()!=null)
		{
			if(costCentre.getValue()!=null)
			{
				for(int i=0;i<acHead.size();i++)
				{
					if(acHead.get(i).getValue()!=null)
					{
						if(Double.valueOf("0"+debit.get(i).getValue())==0)
						{
							showNotification("","Please insert amount for "+acHead.get(i).getItemCaption(acHead.get(i).getValue()),Notification.TYPE_WARNING_MESSAGE);
							debit.get(i).focus();
							return false;
						}
						else
						{
							a = true;
						}
					}
				}
				if(!a)
				{
					showNotification("","Please select a account head.",Notification.TYPE_WARNING_MESSAGE);
					acHead.get(0).focus();
				}
			}
			else
			{
				showNotification("","Please select a cost centre head.",Notification.TYPE_WARNING_MESSAGE);
				costCentre.focus();
			}
		}
		else
		{
			showNotification("","Please select a Debit/Credit head.",Notification.TYPE_WARNING_MESSAGE);
			debCrdHead.focus();
		}
		return a;
	}

	private void newBtnAction()
	{
		btnIni(false);
		txtEnable(true);
		txtClear();
		isUpdate = false;

	}

	private void updateBtnAction()
	{
		if(sessionBean.isUpdateable())
		{
			if(voucherNo.getValue().toString().trim().length()>0)
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				session.beginTransaction();
				try
				{	
					String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
					findFiscalYear = fsl;
					System.out.println("findFiscalYear: "+findFiscalYear);
				}
				catch(Exception e)
				{
					showNotification("Error",""+e,Notification.TYPE_ERROR_MESSAGE);
				}

				btnIni(false);
				txtEnable(true);
				isUpdate = true;
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

	private void saveBtnAction()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
		if(isUpdate)
		{
			if(findFiscalYear.equals(fsl))
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
				showNotification("Warning!","Data should be in same fiscal year",Notification.TYPE_WARNING_MESSAGE);
			}
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

	private void updateData()
	{
		if(sessionBean.isUpdateable())
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			try
			{
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
						"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'Update' TType,costId,'"+sessionBean.getUserIp()+"' userIp, companyId from "+
						""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				//CMbo Check
				String skuId="",skuName="";
				if(cmbSku.getValue()!=null)
				{
					skuId=cmbSku.getValue().toString();
					skuName=cmbSku.getItemCaption(cmbSku.getValue());
				}

				//voucher delete
				sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				double tb = 0;
				NumberFormat f = new DecimalFormat("#0.00");

				String imagePath = imagePath(1,voucherNo.getValue().toString(),fsl)==null?imageLoc:imagePath(1,voucherNo.getValue().toString(),fsl);

				//debit insert
				for(int i=0;i<acHead.size();i++)
				{
					if(acHead.get(i).getValue()!=null && (Double.valueOf("0"+debit.get(i).getValue().toString())>0))
					{
						tb = tb + Double.valueOf("0"+debit.get(i).getValue().toString());

						if(!Boolean.valueOf(debVoucher.getValue().toString()))
						{
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
									" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+
									new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken()+"','"+
									description.getValue()+"','0"+debit.get(i).getValue()+"','0','jau','"+ costCentreMulti.get(i).getValue() +"','"+
									sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId()+"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
							session.createSQLQuery(sql).executeUpdate();
						}
						else
						{
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
									" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+
									new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken()+"','"+
									description.getValue()+"','0"+debit.get(i).getValue()+"','0','jau','"+ costCentreMulti.get(i).getValue() +"','"+
									sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
							session.createSQLQuery(sql).executeUpdate();
						}
					}
				}

				//DebCrd Insert
				if(!Boolean.valueOf(debVoucher.getValue().toString()))
				{
					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
							" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+debCrdHead.getValue().toString()+"','"+
							description.getValue()+"','"+f.format(tb)+"','0','jau','"+costCentre.getValue()+"','"+
							sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
					session.createSQLQuery(sql).executeUpdate();
				}
				else
				{
					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
							" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+debCrdHead.getValue().toString()+"','"+
							description.getValue()+"','"+f.format(tb)+"','0','jau','"+costCentre.getValue()+"','"+
							sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId()+"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
					session.createSQLQuery(sql).executeUpdate();
				}

				sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
						"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'New' TType,costId,'"+sessionBean.getUserIp()+"' userIp, companyId from "+
						""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
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
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			try
			{
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				int sl = 1;
				NumberFormat frmt = new DecimalFormat("#0.00");
				Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE  CompanyId = '"+ sessionBean.getCompanyId() +"' and (vouchertype = 'jau' or vouchertype = 'jcv' or vouchertype = 'jai')").list().iterator();
				try
				{
					if(iter.hasNext())
						sl = Integer.valueOf(iter.next().toString());
				}
				catch(Exception exp)
				{

				}
				voucherNo.setValue("JV-NO-"+sl);
				String sql = "";
				double tb = 0;

				String imagePath = imagePath(1,"JV-NO-"+sl,fsl)==null?imageLoc:imagePath(1,"JV-NO-"+sl,fsl);

				//CMbo Check
				String skuId="",skuName="";
				if(cmbSku.getValue()!=null)
				{
					skuId=cmbSku.getValue().toString();
					skuName=cmbSku.getItemCaption(cmbSku.getValue());
				}
				

				//debit insert
				for(int i=0;i<acHead.size();i++)
				{
					if(acHead.get(i)!=null && (Double.valueOf("0"+debit.get(i).getValue().toString())>0))
					{
						tb = tb+Double.valueOf("0"+debit.get(i).getValue().toString());

						if(!Boolean.valueOf(debVoucher.getValue().toString()))
						{
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,userId,userIp,entryTime, auditapproveflag,  companyId, attachBill,skuId,skuName) "+
									" VALUES('"+voucherNo.getValue().toString()+"','"+dtfYMD.format(date.getValue())+"','"+
									new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken()+"','"+
									description.getValue()+"','0"+debit.get(i).getValue()+"','0','jau','"+costCentreMulti.get(i).getValue()+"','"+
									sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
							session.createSQLQuery(sql).executeUpdate();
						}
						else
						{
							sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
									" VALUES('"+voucherNo.getValue().toString()+"','"+dtfYMD.format(date.getValue())+"','"+
									new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken()+"','"+
									description.getValue()+"','0"+debit.get(i).getValue()+"','0','jau','"+costCentreMulti.get(i).getValue()+"','"+
									sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
							session.createSQLQuery(sql).executeUpdate();
						}
					}
				}

				//DebCrd Insert
				if(!Boolean.valueOf(debVoucher.getValue().toString()))
				{
					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
							" VALUES('"+voucherNo.getValue().toString()+"','"+dtfYMD.format(date.getValue())+"','"+debCrdHead.getValue().toString()+"','"+
							description.getValue()+"','"+frmt.format(tb)+"','0','jau','"+costCentre.getValue()+"','"+
							sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
					session.createSQLQuery(sql).executeUpdate();
				}
				else
				{
					sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
							" VALUES('"+voucherNo.getValue().toString()+"','"+dtfYMD.format(date.getValue())+"','"+debCrdHead.getValue().toString()+"','"+
							description.getValue()+"','"+frmt.format(tb)+"','0','jau','"+costCentre.getValue()+"','"+
							sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
					session.createSQLQuery(sql).executeUpdate();
				}
				tx.commit();
				showNotification("All information saved successfully.");
				txtEnable(false);
				btnIni(true);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void deleteBtnAction()
	{
		if(sessionBean.isDeleteable())
		{
			if(voucherNo.getValue().toString().trim().length()>0)
			{
				final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to delete voucher no. "+voucherNo.getValue()+"?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
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
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
					"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'Delete' TType,costId,'"+sessionBean.getUserIp()+"' userIp, companyId from "+
					""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();

			sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			showNotification("Desired Information delete successfully.");
			isUpdate = false;
			txtClear();
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void findBtnAction()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;

		String sqlF = "SELECT DISTINCT(Voucher_No),Date,Narration FROM "+voucher+" WHERE vouchertype = 'jau' AND ";
		String sqlE = "  order by Date";
		vflag.setValue("jau");
		Window win = new JVFind(sessionBean,sqlF,sqlE,voucherNo,vDate,vflag);	
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
		jvUpload.fileName = "";
		jvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		jvUpload.actionCheck = false;
		imageLoc = "0";

		if(!voucherNo.getValue().toString().isEmpty())
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			try
			{
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+vDate.getValue()+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				for(int i=0;i<acHead.size();i++)
				{
					acHead.get(i).setValue(null);
					balance.get(i).setValue(0);
					debit.get(i).setValue("");
					debit.get(i).setValue("");
				}

				List<?> list = session.createSQLQuery("SELECT date,narration,Ledger_Id,drAmount,crAmount,costId,attachBill,skuId FROM "+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' order by autoId").list();
				List<?> ledger = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From, 1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2))!='A1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
				List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' order by costCentreName").list();

				int i = 0;
				NumberFormat f = new DecimalFormat("#0.00");

				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					if(iter.hasNext())
					{
						System.out.println(i);
						System.out.println(element[5]);

						acHead.get(i).setValue(element[2].toString()+"#"+i);
						costCentreMulti.get(i).setValue(element[5].toString());
						cmbSku.setValue(element[7]);
						if(Double.valueOf(element[3].toString())>0)
							debit.get(i).setValue(f.format(element[3]));
						else
							debit.get(i).setValue(f.format(element[4]));
					}
					else
					{
						date.setValue(new Date(element[0].toString().replace("-", "/").substring(0,10).trim()));

						description.addItem(element[1]);
						description.setItemCaption(element[1],element[1].toString());
						description.setValue(element[1]);

						//description.setValue(element[1].toString());
						costCentre.setValue(element[5].toString());

						imageLoc = element[6].toString();
						cmbSku.setValue(element[7]);
						debCrdHead.setValue(element[2].toString());
						if(Double.valueOf(element[3].toString())>0)
						{
							voucherType(0);
						}
						else
						{
							voucherType(1);
						}
					}
					i++;
					if(i==acHead.size())
					{
						tableRowAdd(i, ledger, costCenter);
					}
				}
				this.bringToFront();
				button.btnEdit.focus();
			}
			catch(Exception exp)
			{
				showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnFind.setEnabled(t);
		button.btnPreview.setEnabled(t);
	}

	private void txtEnable(boolean t)
	{
		date.setEnabled(t);
		description.setEnabled(t);
		costCentre.setEnabled(t);
		debCrdBal.setEnabled(t);
		btnCostCentre.setEnabled(t);
		btndebCrdHead.setEnabled(t);

		table.setEnabled(t);

		debCrdHead.setEnabled(t);
		debVoucher.setEnabled(t);
		crdVoucher.setEnabled(t);
		jvUpload.setEnabled(t);
		btnPreview.setEnabled(t);
		cmbSku.setEnabled(t);
	}

	private void txtClear()
	{
		debCrdHead.setValue(null);
		costCentre.setValue(null);
		voucherNo.setValue("");
		description.setValue("");

		jvUpload.fileName = "";
		jvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		jvUpload.actionCheck = false;
		imageLoc = "0";
		cmbSku.setValue(null);

		for(int i=0;i<acHead.size();i++)
		{
			acHead.get(i).setValue(null);
			costCentreMulti.get(i).setValue(null);
			balance.get(i).setValue(0);
			debit.get(i).setValue("");
		}

		debCrdBal.setValue("");
	}

	private void txtDisable()
	{
		voucherNo.setEnabled(false);
		for(int i=0;i<acHead.size();i++)
		{
			balance.get(i).setEnabled(false);
		}
	}

	private void debCrdHeadIni()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			debCrdHead.removeAllItems();
			List<?> bh = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE replace(substring(create_From,1,3),'-','')!='A1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
			for(Iterator<?> iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				debCrdHead.addItem(element[0].toString());
				debCrdHead.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void headSelect(String head,int t)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			if(head.equalsIgnoreCase("x"))
			{
				if(t > -1)
				{
					balance.get(t).setValue(0);
				}
				else
				{
					debCrdBal.setValue(0);
				}
			}
			else
			{
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				String msg = "";
				Iterator<?> iter = session.createSQLQuery("SELECT substring(r,1,1) a,h+isnull('\\'+g,'')+isnull('\\'+s,'')+'\\'+l b FROM VwLedgerList WHERE ledger_Id = '"+head+"' AND CompanyId in('0', '"+ sessionBean.getCompanyId() +"')").list().iterator();
				Object[] element = (Object[]) iter.next();

				if(element[0].toString().equalsIgnoreCase("A"))
					msg = "Assets\\"+element[1].toString();
				else if(element[0].toString().equalsIgnoreCase("I"))
					msg = "Income\\"+element[1].toString();
				else if(element[0].toString().equalsIgnoreCase("E"))
					msg = "Expenses\\"+element[1].toString();
				else 
					msg = "Liabilities\\"+element[1].toString();

				this.showNotification("Ledger Path :",msg,Notification.TYPE_TRAY_NOTIFICATION);

				double bal = Double.valueOf(session.createSQLQuery("SELECT ISNULL(SUM(drAmount)- SUM(crAmount),0) FROM "+voucher+" WHERE Ledger_Id = '"+ head +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator().next().toString());

				double opBal = Double.valueOf(session.createSQLQuery("SELECT ISNULL(SUM(drAmount)- SUM(crAmount),0) FROM TbLedger_Op_Balance WHERE ledger_Id = '"+ head +"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' AND op_Year = (SELECT year(op_Date) FROM TbFiscal_Year WHERE slNO = "+fsl+")").list().iterator().next().toString());

				if(t > -1)
				{
					balance.get(t).setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
				}
				else
				{
					debCrdBal.setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
				}

			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		List<?> list = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE replace(substring(create_From,1,3),'-','')!='A1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
		List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();
		for(int i=0;i<tr;i++)
		{
			tableRowAdd(i, list, costCenter);	
		}
	}

	private void tableRowAdd(final int ar, final List<?> list, final List<?> costCenter)
	{	
		try
		{
			acHead.add(ar,new ComboBox());
			acHead.get(ar).setWidth("100%");
			acHead.get(ar).setImmediate(true);
			acHead.get(ar).removeAllItems();
			acHead.get(ar).setNullSelectionAllowed(true);
			acHead.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			costCentreMulti.add(ar, new ComboBox());
			costCentreMulti.get(ar).setWidth("100%");
			costCentreMulti.get(ar).setImmediate(true);
			costCentreMulti.get(ar).setNullSelectionAllowed(true);
			costCentreMulti.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			costCentreIni(costCentreMulti.get(ar), costCenter);

			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				acHead.get(ar).addItem(element[0].toString()+"#"+ar);
				acHead.get(ar).setItemCaption(element[0].toString()+"#"+ar, element[1].toString());
			}

			acHead.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{
					try
					{
						if(acHead.get(ar).getValue()!=null)
						{
							for(int i=0;i<ar;i++)
							{
								if(i!=ar)
								{	
									String a_head = acHead.get(ar).getItemCaption(acHead.get(ar).getValue()).toString();
									String b_head = acHead.get(i).getItemCaption(acHead.get(i).getValue()).toString();

									if(a_head.equalsIgnoreCase(b_head))
									{
										showNotification("Warning","Duplicate Ledger Name",Notification.TYPE_WARNING_MESSAGE);
										acHead.get(ar).setValue(null);
										break;
									}
								}
							}

							StringTokenizer st = new StringTokenizer(acHead.get(ar).getValue() == null?"x#"+ar:event.getProperty().toString(),"#");
							String str = st.nextToken();
							int r = Integer.valueOf(st.nextToken());
							headSelect(str,r);

							int temp = debit.size();

							debitActionInit(r);
							if((ar+1)==debit.size())
							{
								tableRowAdd(temp, list, costCenter);
							}
							costCentreMulti.get(r).focus();
						}
					}
					catch(Exception exp){}
				}
			});

			costCentreMulti.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{
					debit.get(ar).focus();
				}
			});

			balance.add(ar, new TextRead());
			balance.get(ar).setWidth("100%");
			balance.get(ar).setStyleName("fright");
			balance.get(ar).setValue(0);
			balance.get(ar).setEnabled(false);
			debit.add(ar, new AmountCommaSeperator());
			debit.get(ar).setWidth("100%");
			debit.get(ar).setStyleName("fright");
			debit.get(ar).setImmediate(true);			
			table.setColumnAlignment("Debit", Table.ALIGN_RIGHT);
			table.addItem(new Object[]{acHead.get(ar), costCentreMulti.get(ar),balance.get(ar),debit.get(ar)},ar);
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}


	private void debitActionInit(final int ar)
	{
		try
		{
			debit.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					double totalAmt=0.0;
					for(int i=0;i<debit.size();i++)
					{
						if(!debit.get(i).getValue().toString().trim().isEmpty())
						{
							totalAmt += i == ar ? event.getProperty().toString().trim().isEmpty()? 0:Double.parseDouble(event.getProperty().toString().trim().replace(",", "")): debit.get(i).getValue().toString().trim().isEmpty()?0:Double.parseDouble(debit.get(i).getValue().toString().replace(",", ""));
						}
					}
					table.setColumnFooter("Debit", cms.setComma(Double.parseDouble(frmt.format(totalAmt))));
					acHead.get(ar + 1).focus();
				}
			});
		}
		catch(Exception ex)
		{
			showNotification("Warning ", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void costCentreIni(ComboBox cmb, List<?> costCenter)
	{
		try
		{
			cmb.removeAllItems();
			for(Iterator<?> iter = costCenter.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmb.addItem(element[0].toString());
				cmb.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("795px");
		setHeight("580px");

		// lblVoucherType
		lblVoucherType = new Label();
		lblVoucherType.setImmediate(true);
		lblVoucherType.setWidth("-1px");
		lblVoucherType.setHeight("-1px");
		lblVoucherType.setValue("Voucher Type :");
		mainLayout.addComponent(lblVoucherType, "top:30.0px;left:20.0px;");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:55.0px;left:20.0px;");

		// lblVoucherNo
		lblVoucherNo = new Label();
		lblVoucherNo.setImmediate(true);
		lblVoucherNo.setWidth("-1px");
		lblVoucherNo.setHeight("-1px");
		lblVoucherNo.setValue("Voucher No :");
		mainLayout.addComponent(lblVoucherNo, "top:80.0px;left:20.0px;");

		// lblCostCentre
		lblCostCentre = new Label();
		lblCostCentre.setImmediate(true);
		lblCostCentre.setWidth("-1px");
		lblCostCentre.setHeight("-1px");
		lblCostCentre.setValue("Cost Centre :");
		mainLayout.addComponent(lblCostCentre, "top:105.0px;left:20.0px;");

		// debit
		debVoucher = new CheckBox();
		debVoucher.setCaption("Debit");
		debVoucher.setImmediate(true);
		debVoucher.setWidth("-1px");
		debVoucher.setHeight("-1px");
		mainLayout.addComponent(debVoucher, "top:30.0px;left:105.0px;");

		// credit
		crdVoucher = new CheckBox();
		crdVoucher.setCaption("Credit");
		crdVoucher.setImmediate(true);
		crdVoucher.setWidth("-1px");
		crdVoucher.setHeight("-1px");
		mainLayout.addComponent(crdVoucher, "top:30.0px;left:165.0px;");

		// dDate
		date = new DateField();
		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		mainLayout.addComponent(date, "top:55.0px;left:110.0px;");

		// txtVoucherNo
		voucherNo = new TextField();
		voucherNo.setImmediate(true);
		voucherNo.setWidth("-1px");
		voucherNo.setHeight("-1px");
		mainLayout.addComponent(voucherNo, "top:80.0px;left:110.0px;");

		// cmbCostCentre
		costCentre = new ComboBox();
		//costCentre.setImmediate(true);
		//costCentre.setWidth("-1px");
		costCentre.setHeight("-1px");
		costCentre.setNullSelectionAllowed(true);
		costCentre.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(costCentre, "top:105.0px;left:110.0px;");

		// btnCostCentre
		btnCostCentre = new NativeButton();
		btnCostCentre.setCaption("");
		btnCostCentre.setImmediate(true);
		btnCostCentre.setWidth("28px");
		btnCostCentre.setHeight("24px");
		btnCostCentre.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnCostCentre,"top:105.0px;left:300.0px;");

		// lblDebitHead
		lblDebitHead = new Label();
		lblDebitHead.setImmediate(true);
		lblDebitHead.setWidth("-1px");
		lblDebitHead.setHeight("-1px");
		//	lblDebitHead.setValue("Debit Head :");
		mainLayout.addComponent(lblDebitHead, "top:30.0px;left:350.0px;");

		// lblBalance
		lblBalance = new Label();
		lblBalance.setImmediate(true);
		lblBalance.setWidth("-1px");
		lblBalance.setHeight("-1px");
		lblBalance.setValue("Balance :");
		mainLayout.addComponent(lblBalance, "top:55.0px;left:350.0px;");

		// cmbDebitHead
		debCrdHead = new ComboBox();
		//debCrdHead.setImmediate(true);
		debCrdHead.setWidth("210px");
		debCrdHead.setHeight("-1px");
		debCrdHead.setImmediate(true);
		debCrdHead.setNullSelectionAllowed(true);
		debCrdHead.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(debCrdHead, "top:30.0px;left:430.0px;");

		// btnDebitHead
		btndebCrdHead = new NativeButton();
		btndebCrdHead.setCaption("");
		btndebCrdHead.setImmediate(true);
		btndebCrdHead.setWidth("28px");
		btndebCrdHead.setHeight("24px");
		btndebCrdHead.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btndebCrdHead,"top:30.0px;left:650.0px;");

		// txtBalance
		debCrdBal = new TextRead("",1);
		debCrdBal.setImmediate(true);
		debCrdBal.setWidth("120px");
		debCrdBal.setHeight("-1px");
		mainLayout.addComponent(debCrdBal, "top:55.0px;left:430.0px;");

		// lblSKU
		lblSku = new Label();
		lblSku.setImmediate(true);
		lblSku.setWidth("-1px");
		lblSku.setHeight("-1px");
		lblSku.setValue("SKU :");
		mainLayout.addComponent(lblSku, "top:87.0px;left:350.0px;");

		// cmbSKU
		cmbSku = new ComboBox();
		cmbSku.setImmediate(true);
		cmbSku.setHeight("-1px");
		cmbSku.setWidth("320px");
		cmbSku.setNullSelectionAllowed(true);
		cmbSku.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSku, "top:87.0px;left:430.0px;");

		// jvUpload
		mainLayout.addComponent(jvUpload, "top:115.0px;left:350.0px;");

		// btnPreview
		btnPreview = new Button("Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:143.0px;left:460.0px;");

		table.setFooterVisible(true);
		table.setWidth("750");
		table.setHeight("255px");
		table.setFooterVisible(false);
		table.addContainerProperty("Ledger Name", ComboBox.class, new ComboBox());
		table.setColumnWidth("Ledger Name", 290);
		table.setColumnFooter("Ac. Head", "Total :");
		table.addContainerProperty("Cost Centre", ComboBox.class, new ComboBox());
		table.setColumnWidth("Cost Centre", 180);
		table.addContainerProperty("Balance", TextRead.class, new TextRead());
		table.setColumnWidth("Balance", 110);
		table.addContainerProperty("Debit", TextField.class, new TextField(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Debit", 110);
		table.setFooterVisible(true);

		tableInitialise();

		mainLayout.addComponent(table, "top:165.0px;left:20.0px;");

		// lblDescription
		lblDescription = new Label();
		lblDescription.setImmediate(true);
		lblDescription.setWidth("-1px");
		lblDescription.setHeight("-1px");
		lblDescription.setValue("Description :");
		mainLayout.addComponent(lblDescription, "top:430.0px;left:20.0px;");

		// cmbDescription
		description.setWidth("650px");
		description.setNullSelectionAllowed(false);
		description.setNewItemsAllowed(true);

		mainLayout.addComponent(description, "top:430.0px;left:100.0px;");

		//common button
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:475.0px;left:50.0px;");

		return mainLayout;
	}
}