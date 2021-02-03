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
public class CashReceiptVoucher extends Window
{
	private SessionBean sessionBean;

	private CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "", "Exit");

	private ComboBox description = new ComboBox();

	private Table table = new Table();

	private ArrayList<ComboBox> acHead = new ArrayList<ComboBox>();
	private ArrayList<ComboBox> costCentreMulti = new ArrayList<ComboBox>();
	private ArrayList<TextRead> budget = new ArrayList<TextRead>();
	private ArrayList<TextRead> balance = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> credit = new ArrayList<AmountCommaSeperator>();

	private HorizontalLayout btnLayout = new HorizontalLayout();

	private NumberFormat frmt = new DecimalFormat("#0.00");
	private CommaSeparator cms = new CommaSeparator();
	private TextField vfDate = new TextField(); 
	private TextField vflag = new TextField();

	private boolean isUpdate = false;

	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");

	private AbsoluteLayout mainLayout;

	private TextField voucherNo;
	private TextRead bankBal;
	private TextRead bankBudget;
	private TextField cheqNo;
	private TextField receivedFrom;

	private DateField chqDate;
	private DateField date;

	private ComboBox bankHead;
	private ComboBox costCentre;
	private ComboBox cmbSku;

	private Label lblSku;
	private Label lblDescription;
	private Label lblBalance;
	private Label lblBudget;
	private Label lblVoucherNo;
	private Label lblPaymentBank;
	private Label lblCostCentre;
	private Label lblreceivedFrom;
	private Label lblDate;

	private NativeButton btnbankHead;
	private NativeButton btnCostCentre;

	private ImmediateUploadExample bpvUpload = new ImmediateUploadExample("");
	private ImmediateUploadExample billUpload = new ImmediateUploadExample("");

	private String filePathTmp = "";
	private Button btnPreview;
	private String imageLoc = "0";
	private String crvPdf = "";
	private String findFiscalYear = "";
	private String strCash = "";

	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

	public CashReceiptVoucher(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("950px");
		this.setCaption("CASH RECEIVED VOUCHER :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		button.btnNew.focus();

		buildMainLayout();
		setContent(mainLayout);

		buttonActionAdd();

		bankHeadIni();

		btnIni(true);
		txtEnable(false);

		txtDisable();

		Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
		List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();

		costCentreIni(costCentre, costCenter);

		addCmbParticularData();

		Component allComp[] = {date,receivedFrom,costCentre,bankHead,cheqNo,chqDate,acHead.get(0),button.btnNew,button.btnEdit,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
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
			description.removeAllItems();
			String sql = "select NarrationId,Narration from tbNarrationList where NarrationId like '%CR%'";
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

	private void bankHeadIni()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A7') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
			for(Iterator<?> iter = bh.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				bankHead.addItem(element[0].toString());
				bankHead.setItemCaption(element[0].toString(), element[1].toString());
				strCash = element[0].toString();
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
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
					budget.get(t).setValue(0);
					balance.get(t).setValue(0);
				}
				else
				{
					bankBudget.setValue(0);
					bankBal.setValue(0);
				}
			}
			else
			{
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormatter.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				String msg = "";
				Iterator<?> iter = session.createQuery("SELECT substring(id.r,1,1),id.h+isnull('\\'+id.g,'')+isnull('\\'+id.s,'')+'\\'+id.l FROM VwLedgerList WHERE id.ledgerId = '"+head+"'").list().iterator();

				Object[] element = (Object[]) iter.next();
				if(element[0].toString().equalsIgnoreCase("A"))
					msg = "Assets\\"+element[1];
				else if(element[0].toString().equalsIgnoreCase("I"))
					msg = "Income\\"+element[1];
				else if(element[0].toString().equalsIgnoreCase("E"))
					msg = "Expenses\\"+element[1];
				else 
					msg = "Liabilities\\"+element[1];

				this.showNotification("",msg,Notification.TYPE_TRAY_NOTIFICATION);

				iter = session.createQuery("SELECT budgetAmount FROM TbBudget WHERE id.ledgerId = '"+head+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' AND id.opYear = (SELECT year(opDate) FROM TbFiscalYear WHERE slNo = "+fsl+")").list().iterator();

				double budAmt = 0;

				if(iter.hasNext())
					budAmt = Double.valueOf(iter.next().toString());

				double bal = Double.valueOf(session.createSQLQuery("SELECT ISNULL(SUM(drAmount)- SUM(crAmount),0) FROM "+voucher+" WHERE Ledger_Id = '"+head+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator().next().toString());

				double opBal = Double.valueOf(session.createSQLQuery("SELECT ISNULL(SUM(drAmount)- SUM(crAmount),0) FROM TbLedger_Op_Balance WHERE ledger_Id = '"+head+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' AND op_Year = (SELECT year(op_Date) FROM TbFiscal_Year WHERE slNo = "+fsl+")").list().iterator().next().toString());

				if(t > -1)
				{
					budget.get(t).setValue(cms.setComma(Double.valueOf(frmt.format(budAmt))));
					balance.get(t).setValue(cms.setComma(Double.valueOf(frmt.format((bal+opBal)))));
				}
				else
				{
					bankBudget.setValue(cms.setComma(Double.valueOf(frmt.format(budAmt))));
					bankBal.setValue(cms.setComma(Double.valueOf(frmt.format((bal+opBal)))));
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean sameHeadAllowed()
	{
		boolean ret = true;
		for(int i=0; i<acHead.size(); i++)
		{
			if(acHead.get(i).getValue()!=null)
			{
				if(new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken().equals(bankHead.getValue().toString()))
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
		bankHead.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{				
				headSelect(bankHead.getValue()== null?"x":bankHead.getValue().toString(),-1);
			}
		});

		button.btnNew.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				isUpdate = false;
				newBtnAction(event);
				receivedFrom.focus();
			}
		});

		button.btnEdit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{

				if (!new AstechacApplication().isClosedFiscal(dateFormatter.format(date.getValue())))
					updateBtnAction(event);
				else
					showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

			}
		});

		button.btnSave.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{	if(sameHeadAllowed())
			{			
				if(nullCheck())
					if (!new AstechacApplication().isClosedFiscal(dateFormatter.format(date.getValue())))
						saveBtnAction();
					else
						showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	
			}
			else{
				showNotification("Warning!","Same head is not allowed",Notification.TYPE_WARNING_MESSAGE);
				bankHead.focus();
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
				bankHeadIni();
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
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
				if(!new AstechacApplication().isClosedFiscal(dateFormatter.format(date.getValue())))
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
				exitButtonEvent();
			}
		});

		button.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBtnAction();
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
					if(!bpvUpload.actionCheck)
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
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
						getWindow().open(new ExternalResource(link),"_blank", 500, 200, Window.BORDER_NONE);
					}
				}
			}
		});

		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"","");
				System.out.println("Done");
			}
		});

		btnbankHead.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				bankHeadLink();
			}
		});

		btnCostCentre.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				costCentreLink();	
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

			String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", "report")+filePathTmp;
			hm.put("urlLink", link);

			String sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+voucherNo.getValue().toString()+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by CrAmount desc";

			hm.put("sql",sql);
			System.out.println(sql);
			Window win = new ReportViewer(hm,"report/account/voucher/CashReceipttVoucher.jasper",
					getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			win.setStyleName("cwindow");
			getParent().getWindow().addWindow(win);
			win.setCaption("CASH RECEIVED VOUCHER :: "+sessionBean.getCompany());
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
	}

	public void bankHeadLink()
	{
		Window win = new LedgerCreate(sessionBean);
		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{	
				bankHeadIni();
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
			System.out.println("if flag 0");
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId()+"CRV";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						crvPdf = SessionBean.imagePathTmp+path+".jpg";
						filePathTmp = path+".jpg";
					}else{
						String path = sessionBean.getUserId()+"CRV";
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						crvPdf = SessionBean.imagePathTmp+path+".pdf";
						filePathTmp = path+".pdf";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			return crvPdf;
		}

		if(flag==1)
		{
			System.out.println("into file move");
			System.out.println("fILE nAME: "+billUpload.fileName.toString());
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						System.out.println("into file move 1");
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/cashBillPayment"+fiscalYearNo+"/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/cashBillPayment"+fiscalYearNo+"/"+path+".jpg";
					}
					else
					{
						System.out.println("into file move 2");
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/cashBillPayment"+fiscalYearNo+"/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/cashBillPayment"+fiscalYearNo+"/"+path+".pdf";
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

	private void exitButtonEvent() 
	{
		this.close();
	}

	private boolean nullCheck()
	{
		boolean a = false;
		if(!receivedFrom.getValue().equals(""))
		{
			if(bankHead.getValue()!=null)
			{
				if(costCentre.getValue()!=null)
				{
					for(int i=0;i<acHead.size();i++)
					{
						if(acHead.get(i).getValue()!= null)
						{
							if(Double.valueOf("0"+credit.get(i).getValue()) == 0)
							{
								showNotification("","Please insert amount for "+acHead.get(i).getItemCaption(acHead.get(i).getValue()),Notification.TYPE_WARNING_MESSAGE);
								credit.get(i).focus();
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
						showNotification("Warning ","Please select an account head.",Notification.TYPE_WARNING_MESSAGE);
						acHead.get(0).focus();
					}
				}
				else
				{				
					showNotification("Warning ","Please select a cost centre head.",Notification.TYPE_WARNING_MESSAGE);
					costCentre.focus();
				}
			}
			else
			{
				showNotification("Warning ","Please select a Cash head.",Notification.TYPE_WARNING_MESSAGE);
				bankHead.focus();
			}
		}
		else
		{
			showNotification("Warning ","Please select received from.",Notification.TYPE_WARNING_MESSAGE);
			receivedFrom.focus();
		}
		return a;
	}

	private void newBtnAction(ClickEvent e)
	{
		isUpdate = false;
		btnIni(false);
		txtEnable(true);
		txtClear();
	}

	private void updateBtnAction(ClickEvent e)
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
				}
				catch(Exception ex)
				{
					showNotification("Error",""+ex,Notification.TYPE_ERROR_MESSAGE);
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
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormatter.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
						"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'Update' TType,costId, '"+sessionBean.getUserIp()+"' userIp, companyId from "+
						""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				double tb = 0;
				NumberFormat f = new DecimalFormat("#0.00");

				String imagePath = imagePath(1,voucherNo.getValue().toString(),fsl)==null?imageLoc:imagePath(1,voucherNo.getValue().toString(),fsl);

				//CMbo Check
				String skuId="",skuName="";
				if(cmbSku.getValue()!=null)
				{
					skuId=cmbSku.getValue().toString();
					skuName=cmbSku.getItemCaption(cmbSku.getValue());
				}

				//credit insert
				for(int i=0;i<acHead.size();i++)
				{
					if(acHead.get(i).getValue() != null)
					{
						tb = tb+Double.valueOf("0"+credit.get(i).getValue().toString());
						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
								" VALUES('"+voucherNo.getValue()+"','"+dateFormatter.format(date.getValue())+"','"+
								new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken()+"','"+
								description.getValue()+"','0"+credit.get(i).getValue()+"','0','cca','"+receivedFrom.getValue()+"','"+costCentreMulti.get(i).getValue()+"','"+ sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
						session.createSQLQuery(sql).executeUpdate();
					}
				}

				//debit insert
				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
						" VALUES('"+voucherNo.getValue()+"','"+dateFormatter.format(date.getValue())+"','"+bankHead.getValue()+"','"+description.getValue()+"','0','"+
						f.format(tb)+"','cca','"+receivedFrom.getValue()+"','"+costCentre.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"','"+imagePath+"','"+skuId+"','"+skuName+"')";
				session.createSQLQuery(sql).executeUpdate();

				sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
						"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'New' TType,costId, '"+sessionBean.getUserIp()+"' userIp, companyId from "+
						""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				showNotification("Desired voucher no updated successfully.");
				txtEnable(false);
				btnIni(true);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
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
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormatter.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				int sl = 1;

				Iterator<?> iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE vouchertype in ('cca','cbi') and CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
				if(iter.hasNext())
					sl = Integer.valueOf(iter.next().toString());

				voucherNo.setValue("CR-CH-"+sl);
				double tb = 0;
				NumberFormat f = new DecimalFormat("#0.00");
				String sql = "";

				String imagePath = imagePath(1,"CR-CH-"+sl,fsl)==null?imageLoc:imagePath(1,"CR-CH-"+sl,fsl);

				//CMbo Check
				String skuId="",skuName="";
				if(cmbSku.getValue()!=null)
				{
					skuId=cmbSku.getValue().toString();
					skuName=cmbSku.getItemCaption(cmbSku.getValue());
				}

				//credit insert
				for(int i=0;i<acHead.size();i++)
				{
					if(acHead.get(i).getValue()!= null && costCentreMulti.get(i).getValue() != null)
					{
						tb = tb+Double.valueOf("0"+credit.get(i).getValue().toString());

						sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,costId,chqClear,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
								" VALUES('"+voucherNo.getValue().toString() +"','"+dateFormatter.format(date.getValue())+"','"+
								new StringTokenizer(acHead.get(i).getValue().toString()+"","#").nextToken()+"','"+
								description.getValue()+"','0"+credit.get(i).getValue()+"','0','cca','"+receivedFrom.getValue()+"','"+costCentreMulti.get(i).getValue()+"','0','"+
								sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, " +
								"'"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
						session.createSQLQuery(sql).executeUpdate();
					}
				}

				//Debit insert
				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,costId,chqClear,userId,userIp,entryTime, auditapproveflag, companyId, attachBill,skuId,skuName) "+
						" VALUES('"+voucherNo.getValue()+"','"+dateFormatter.format(date.getValue())+"','"+bankHead.getValue()+"','"+description.getValue()+"','0','"+
						f.format(tb)+"','cca','"+receivedFrom.getValue()+"','"+costCentre.getValue()+"','0','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"','"+skuId+"','"+skuName+"')";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				showNotification("All information saved successfully.");
				txtEnable(false);
				btnIni(true);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		else
		{
			showNotification("Authentication Failed","You have not proper authentication for save.",Notification.TYPE_ERROR_MESSAGE);
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
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormatter.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
					"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'Delete' TType,costId, '"+sessionBean.getUserIp()+"' userIp, companyId from "+
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
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void txtClear()
	{
		try
		{
			voucherNo.setValue("");
			receivedFrom.setValue("");
			bankHead.setValue(null);
			bankBudget.setValue("");
			bankBal.setValue("");
			costCentre.setValue(null);
			description.setValue(null);	 
			cmbSku.setValue(null);
			for(int i=0;i<acHead.size();i++)
			{
				acHead.get(i).setValue(null);
				costCentreMulti.get(i).setValue(null);
				budget.get(i).setValue(0);
				balance.get(i).setValue(0);
				credit.get(i).setValue("");
			}

			bpvUpload.fileName = "";
			bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
			filePathTmp = "";
			bpvUpload.actionCheck = false;
			imageLoc = "0";
			bankHead.setValue(strCash);
		}
		catch(Exception ex)
		{
			//	this.showNotification(ex.toString());
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
		receivedFrom.setEnabled(t);
		bankHead.setEnabled(t);
		date.setEnabled(t);
		description.setEnabled(t);
		costCentre.setEnabled(t);
		table.setEnabled(t);
		bankBudget.setEnabled(t);
		bankBal.setEnabled(t);
		bpvUpload.setEnabled(t);
		btnPreview.setEnabled(t);
		btnCostCentre.setEnabled(t);
		btnbankHead.setEnabled(t);
		cmbSku.setEnabled(t);
	}

	private void txtDisable()
	{
		bankBudget.setEnabled(false);
		bankBal.setEnabled(false);
		voucherNo.setEnabled(false);
	}

	private void findBtnAction()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dateFormatter.format(date.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;

		String sqlF = "SELECT tbLedger.Ledger_Id, tbLedger.Ledger_Name, vwVoucher.Date, vwVoucher.Voucher_No,vwVoucher.Narration,vwVoucher.DrAmount,vwVoucher.CrAmount,vwVoucher.vouchertype FROM tbLedger INNER JOIN "+voucher+" as vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id WHERE vouchertype = 'cca' AND vwVoucher.DrAmount !=0 AND vwVoucher.companyId = '"+ sessionBean.getCompanyId() +"' AND ";
		String sqlE = " order by Date,CAST(SUBSTRING(Voucher_No,7,50) AS INT)";

		vflag.setValue("cashReceive");
		Window win = new VoucherFind(sessionBean,sqlF,sqlE,voucherNo,vfDate,vflag);
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

	@SuppressWarnings("deprecation")
	private void findInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+vfDate.getValue()+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			List<?> list = session.createSQLQuery("SELECT date, transactionWith, ledger_Id, narration, drAmount, costId,attachBill,skuId  FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND drAmount != 0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list();

			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				date.setValue(new Date(element[0].toString().replace("-", "/").substring(0,10).trim()));
				receivedFrom.setValue(element[1]);
				bankHead.setValue(element[2]);
				description.addItem(element[3]);
				description.setItemCaption(element[3],element[3].toString());
				description.setValue(element[3]);
				System.out.println((element[3]));
				if(element[5]!=null)
					costCentre.setValue(element[5]);

				imageLoc = element[6].toString();
				cmbSku.setValue(element[7]);
			}

			String tSql= "SELECT ledger_Id, costId, crAmount FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND crAmount != 0 AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			list = session.createSQLQuery(tSql).list();

			for(int i=0;i<acHead.size();i++)
			{
				acHead.get(i).setValue(null);
				budget.get(i).setValue(0);
				balance.get(i).setValue(0);
				credit.get(i).setValue("");
			}

			int i = 0;

			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				acHead.get(i).setValue(element[0].toString()+"#"+i);
				if(element[1]!= null)
					costCentreMulti.get(i).setValue(element[1].toString());
				if(element[1]== null)
					costCentreMulti.get(i).setValue("U-3");
				credit.get(i).setValue(frmt.format(element[2]));
			}
			this.bringToFront();
			button.btnEdit.focus();
		}
		catch(Exception exp)
		{
			//	showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void tableInitialise()
	{	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		List<?> ledger = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1,ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2))!='A1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
		List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' order by costCentreName").list();
		for(int i=0;i<1;i++)
		{
			tableRowAdd(i, ledger, costCenter);	
		}
	}

	private void tableRowAdd(final int ar, final List<?> ledger, final List<?> costCenter)
	{
		try
		{
			acHead.add(ar, new ComboBox());
			acHead.get(ar).setWidth("100%");
			acHead.get(ar).setImmediate(true);
			acHead.get(ar).setNullSelectionAllowed(true);
			acHead.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			acHead.get(ar).removeAllItems();

			costCentreMulti.add(ar, new ComboBox());
			costCentreMulti.get(ar).setWidth("100%");
			costCentreMulti.get(ar).setImmediate(true);
			costCentreMulti.get(ar).setNullSelectionAllowed(true);
			costCentreMulti.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

			for(Iterator<?> iter = ledger.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				acHead.get(ar).addItem(element[0].toString()+"#"+ar);
				acHead.get(ar).setItemCaption(element[0].toString()+"#"+ar, element[1].toString());
			}

			costCentreIni(costCentreMulti.get(ar), costCenter);

			acHead.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
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
						costCentreMulti.get(ar).focus();
					}
				}
			});

			costCentreMulti.get(ar).addListener(new ValueChangeListener() 
			{				
				@Override
				public void valueChange(ValueChangeEvent event) 
				{
					credit.get(ar).focus();
				}
			});

			budget.add(ar, new TextRead());
			budget.get(ar).setWidth("100%");
			budget.get(ar).setStyleName("fright");

			balance.add(ar, new TextRead());
			balance.get(ar).setWidth("100%");
			balance.get(ar).setStyleName("fright");

			credit.add(ar, new AmountCommaSeperator());
			credit.get(ar).setStyleName("fright");
			credit.get(ar).setWidth("100%");
			table.setColumnAlignment("Credit", Table.ALIGN_RIGHT);
			table.addItem(new Object[]{acHead.get(ar), costCentreMulti.get(ar),budget.get(ar),balance.get(ar),credit.get(ar)},ar); 
		}
		catch(Exception exp)
		{
			//exp.printStackTrace();
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void costCentreIni(ComboBox cmb, List<?> costCenter)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			for(Iterator<?> iter = costCenter.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmb.addItem(element[0].toString());
				cmb.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout() 
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("950px");
		setHeight("370px");

		// lblreceivedFrom
		lblreceivedFrom = new Label();
		lblreceivedFrom.setImmediate(true);
		lblreceivedFrom.setWidth("-1px");
		lblreceivedFrom.setHeight("-1px");
		lblreceivedFrom.setValue("Received From :");
		mainLayout.addComponent(lblreceivedFrom, "top:22.0px;left:20.0px;");

		// receivedFrom
		receivedFrom = new TextField();
		receivedFrom.setImmediate(true);
		receivedFrom.setWidth("-1px");
		receivedFrom.setHeight("-1px");
		mainLayout.addComponent(receivedFrom, "top:22.0px;left:130.0px;");

		// lblCostCentre
		lblCostCentre = new Label();
		lblCostCentre.setImmediate(true);
		lblCostCentre.setWidth("-1px");
		lblCostCentre.setHeight("-1px");
		lblCostCentre.setValue("Cost Centre :");
		mainLayout.addComponent(lblCostCentre, "top:47.0px;left:20.0px;");

		// costCentre
		costCentre = new ComboBox();
		costCentre.setImmediate(true);
		//costCentre.setWidth("-1px");
		costCentre.setHeight("-1px");
		costCentre.setWidth("210px");
		costCentre.setNullSelectionAllowed(true);
		costCentre.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(costCentre, "top:47.0px;left:130.0px;");

		// btnCostCentre
		btnCostCentre = new NativeButton();
		btnCostCentre.setCaption("");
		btnCostCentre.setImmediate(true);
		btnCostCentre.setWidth("28px");
		btnCostCentre.setHeight("24px");
		btnCostCentre.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnCostCentre,"top:47.0px;left:340.0px;");

		// lblPaymentBank
		lblPaymentBank = new Label();
		lblPaymentBank.setImmediate(true);
		lblPaymentBank.setWidth("-1px");
		lblPaymentBank.setHeight("-1px");
		lblPaymentBank.setValue("Cash A/C :");
		mainLayout.addComponent(lblPaymentBank, "top:72.0px;left:20.0px;");

		// bankHead
		bankHead = new ComboBox();
		//bankHead.setImmediate(true);
		//bankHead.setWidth("-1px");
		bankHead.setHeight("-1px");
		bankHead.setWidth("270px");
		bankHead.setImmediate(true);
		bankHead.setNullSelectionAllowed(true);
		bankHead.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(bankHead, "top:72.0px;left:130.0px;");

		// btnCostCentre
		btnbankHead = new NativeButton();
		btnbankHead.setCaption("");
		btnbankHead.setImmediate(true);
		btnbankHead.setWidth("28px");
		btnbankHead.setHeight("24px");
		btnbankHead.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnbankHead,"top:72.0px;left:405.0px;");

		// lblBudget
		lblBudget = new Label();
		lblBudget.setImmediate(true);
		lblBudget.setWidth("-1px");
		lblBudget.setHeight("-1px");
		lblBudget.setValue("Budget :");
		mainLayout.addComponent(lblBudget, "top:95.0px;left:20.0px;");

		// bankBudget
		bankBudget = new TextRead("",1);
		bankBudget.setImmediate(true);
		bankBudget.setWidth("120px");
		bankBudget.setHeight("-1px");
		mainLayout.addComponent(bankBudget, "top:97.0px;left:131.0px;");

		// lblBalance
		lblBalance = new Label();
		lblBalance.setImmediate(true);
		lblBalance.setWidth("-1px");
		lblBalance.setHeight("-1px");
		lblBalance.setValue("Balance :");
		mainLayout.addComponent(lblBalance, "top:95.0px;left:255.0px;");

		// bankBal
		bankBal = new TextRead("",1);
		bankBal.setImmediate(true);
		bankBal.setWidth("120px");
		bankBal.setHeight("-1px");
		mainLayout.addComponent(bankBal, "top:97.0px;left:310.0px;");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:22.0px;left:450.0px;");

		// date
		date = new DateField();
		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		mainLayout.addComponent(date,"top:22.0px;left:530.0px;");

		// lblVoucherNo
		lblVoucherNo = new Label();
		lblVoucherNo.setImmediate(true);
		lblVoucherNo.setWidth("-1px");
		lblVoucherNo.setHeight("-1px");
		lblVoucherNo.setValue("Voucher No :");
		mainLayout.addComponent(lblVoucherNo, "top:47.0px;left:450.0px;");

		// voucherNo
		voucherNo = new TextField();
		voucherNo.setImmediate(true);
		voucherNo.setWidth("120px");
		voucherNo.setHeight("-1px");
		mainLayout.addComponent(voucherNo, "top:47.0px;left:530.0px;");

		// lblSKU
		lblSku = new Label();
		lblSku.setImmediate(true);
		lblSku.setWidth("-1px");
		lblSku.setHeight("-1px");
		lblSku.setValue("SKU :");
		mainLayout.addComponent(lblSku, "top:74.0px;left:450.0px;");

		// cmbSKU
		cmbSku = new ComboBox();
		cmbSku.setImmediate(true);
		cmbSku.setHeight("-1px");
		cmbSku.setWidth("320px");
		cmbSku.setNullSelectionAllowed(true);
		cmbSku.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbSku, "top:74.0px;left:530.0px;");

		/*	// lblCheque
		lblCheque = new Label();
		lblCheque.setImmediate(true);
		lblCheque.setWidth("-1px");
		lblCheque.setHeight("-1px");
		lblCheque.setValue("Cheque No :");
		mainLayout.addComponent(lblCheque, "top:72.0px;left:450.0px;");

		// cheqNo
		cheqNo = new TextField();
		cheqNo.setImmediate(true);
		cheqNo.setWidth("120px");
		cheqNo.setHeight("-1px");
		cheqNo.setSecret(false);
		mainLayout.addComponent(cheqNo, "top:72.0px;left:530.0px;");

		// lblChequeDate
		lblChequeDate = new Label();
		lblChequeDate.setImmediate(true);
		lblChequeDate.setWidth("-1px");
		lblChequeDate.setHeight("-1px");
		lblChequeDate.setValue("Cheque Date :");
		mainLayout.addComponent(lblChequeDate, "top:97.0px;left:450.0px;");

		// chqDate
		chqDate = new DateField();
		chqDate.setValue(new java.util.Date());
		chqDate.setResolution(PopupDateField.RESOLUTION_DAY);
		chqDate.setDateFormat("dd-MM-yy");
		chqDate.setInvalidAllowed(false);
		chqDate.setImmediate(true);
		mainLayout.addComponent(chqDate, "top:97.0px;left:530.0px;");
		 */
		// bpvUpload
		mainLayout.addComponent(bpvUpload, "top:22.0px;left:660.0px;");

		// btnPreview
		btnPreview = new Button("Bill Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:47.0px;left:780.0px;");

		/*		// bpvChequeUpload
		bpvChequeUpload.upload.setButtonCaption("Attach Cheque");
		bpvChequeUpload.upload.setWidth("100px");
		mainLayout.addComponent(bpvChequeUpload, "top:72.0px;left:660.0px;");

		// btnPreview
		btnChequePreview = new Button("Cheque Preview");
		btnChequePreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnChequePreview.addStyleName("icon-after-caption");
		btnChequePreview.setImmediate(true);
		btnChequePreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnChequePreview, "top:97.0px;left:780.0px;");*/

		table.setFooterVisible(true);
		table.setWidth("100%");
		table.setHeight("80px");
		table.setImmediate(true);

		table.setFooterVisible(false);
		table.addContainerProperty("LEDGER NAME", ComboBox.class, new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("LEDGER NAME",310);	
		table.setColumnFooter("LEDGER NAME", "Total :");
		table.addContainerProperty("Cost Centre", ComboBox.class, new ComboBox(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Cost Centre", 210);
		table.addContainerProperty("Budget", TextRead.class, new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Budget",90);
		table.addContainerProperty("Balance", TextRead.class, new TextRead(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Balance", 90);
		table.addContainerProperty("Credit AMOUNT", AmountCommaSeperator.class, new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Credit AMOUNT",90);
		table.setFooterVisible(true);

		tableInitialise();

		mainLayout.addComponent(table, "top:130.0px;left:20.0px;");

		// lblDescription
		lblDescription = new Label();
		lblDescription.setImmediate(true);
		lblDescription.setWidth("-1px");
		lblDescription.setHeight("-1px");
		lblDescription.setValue("Description :");
		mainLayout.addComponent(lblDescription, "top:220.0px;left:20.0px;");

		// cmbDescription
		description.setWidth("650px");
		description.setNullSelectionAllowed(false);
		description.setImmediate(true);
		description.setNewItemsAllowed(true);

		mainLayout.addComponent(description, "top:220.0px;left:100.0px;");

		//common button
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:260.0px;left:90.0px;");

		return mainLayout;
	}
}
