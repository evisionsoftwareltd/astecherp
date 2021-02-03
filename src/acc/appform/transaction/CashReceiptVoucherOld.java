package acc.appform.transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vaadin.autoreplacefield.NumberField;

import acc.appform.accountsSetup.CostInformation;
import acc.appform.accountsSetup.LedgerCreate;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.FocusMoveByEnter;
import com.common.share.ImmediateUploadExample;
import com.common.share.MessageBox;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.CommonButton;
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
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Button.ClickShortcut;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

public class CashReceiptVoucherOld extends Window 
{
	private AbsoluteLayout mainLayout;

	private HorizontalLayout btnLayout = new HorizontalLayout();

	//private AmountCommaSeperator amount;

	private TextRead balance;
	private TextRead budget;
	private TextField receivedFrom;
	private TextField voucherNo;
	public TextField vfDate=new TextField(); 
	public TextField vflag = new TextField();

	private Label lblAmount;
	private Label lblBalance;
	private Label lblBudget;
	private Label lblDate;
	private Label lblParticular;
	private Label lblCostCentre;
	private Label lblAccountsHead;
	private Label lblreceivedFrom;
	private Label lblVoucherNo;

	private DateField date = new DateField();

//	private ComboBox particular;
	private ComboBox costCentre;
	private ComboBox acHead;
	
	private NativeButton btnAcHead;
	private NativeButton btnCostCentre;

	Button btnPreview;

	String crvPdf = null;

	private ImmediateUploadExample billUpload = new ImmediateUploadExample("");

	private SessionBean sessionBean;

	private boolean isUpdate = false;
	
	String filePathTmp = "";
                                                                
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");

	private NumberFormat fmt = new DecimalFormat("#0.00");

	public double prebal;

	private CommaSeparator cms = new CommaSeparator();

	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "Preview", "","");
	
	String imageLoc = "0" ;
	
	private Table table = new Table();
	private ComboBox particular = new ComboBox();
	private AmountCommaSeperator amount = new AmountCommaSeperator();
	
	private ComboBox narration = new ComboBox();
	
/*	CommonButton button = new CommonButton("New", "Update", "Save", "Refresh", "Delete", "Find", "", "", "");
	private SessionBean sessionBean;
	private GridLayout grid = new GridLayout(1,1);
	private GridLayout titleGrid = new GridLayout(1,1);
	private VerticalLayout mainLayout = new VerticalLayout();
	private VerticalLayout rightVerLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private HorizontalLayout space = new HorizontalLayout();
	private FormLayout formLayout = new FormLayout();

	private TextField voucherNo = new TextField("Voucher No:");
	private TextField receivedFrom = new TextField("Received From:");
	private ComboBox acHead = new ComboBox("Accounts Head:");
	private ComboBox costCentre = new ComboBox("Cost Centre:");

	private DateField date = new DateField("Date:");
	private TextRead budget = new TextRead("Budget:",1);
	private TextRead balance = new TextRead("Balance:",1);

	private Table table = new Table();
	//private TextField particular = new TextField();
	private ComboBox narration = new ComboBox();
	//private NumberField amount = new NumberField();
	private ComboBox particular=new ComboBox();
	private AmountCommaSeperator amount=new AmountCommaSeperator();
	private CommaSeparator cms = new CommaSeparator();

	private boolean isUpdate = false;
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private NumberFormat fmt = new DecimalFormat("#0.00");
	public TextField vfDate=new TextField(); 
	public TextField vflag = new TextField();*/

	public CashReceiptVoucherOld(SessionBean sessionBean)
	{
		
		this.sessionBean = sessionBean;
		this.setCaption("CASH RECEIVED VOUCHER :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		addCmbParticularData();
		ledgerIni();

		btnIni(true);
		txtEnable(false);
		setButtonAction();
		voucherNo.setEnabled(false);
		costCentreIni();	
		Component ob[] = {receivedFrom,costCentre,acHead,date,particular,amount,button.btnNew,button.btnEdit,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
		new FocusMoveByEnter(this,ob);                                                                                                                                                                                                                                              
		setButtonShortCut();
		button.btnNew.focus();
		
/*		this.sessionBean = sessionBean;
		this.setCaption("CASH RECEIVED VOUCHER :: "+sessionBean.getCompany());
		this.setWidth("600px");
		this.setResizable(false);

		titleGrid.addComponent(new Label("<h3><u>Cash Received Voucher</u></h3>",Label.CONTENT_XHTML));
		mainLayout.addComponent(titleGrid);
		mainLayout.setComponentAlignment(titleGrid, Alignment.TOP_CENTER);

		formLayout.addComponent(voucherNo);
		formLayout.addComponent(receivedFrom);
		receivedFrom.setWidth("250px");
		formLayout.addComponent(acHead);
		acHead.setWidth("250px");
		acHead.setImmediate(true);

		formLayout.addComponent(costCentre);
		costCentre.setWidth("250px");

		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		horLayout.addComponent(formLayout);

		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		date.setWidth("85px");

		FormLayout fl = new FormLayout();
		fl.addComponent(date);
		rightVerLayout.addComponent(fl);
		rightVerLayout.setSpacing(true);

		fl.addComponent(budget);
		budget.setWidth("85px");
		fl.addComponent(balance);
		balance.setWidth("85px");

		horLayout.addComponent(rightVerLayout);

		mainLayout.addComponent(horLayout);

		HorizontalLayout tabLayout = new HorizontalLayout();
		HorizontalLayout spaceLayout=new HorizontalLayout();
		
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
		mainLayout.addComponent(tabLayout);
		particular.setImmediate(true);
		particular.setNullSelectionAllowed(false);
		particular.setNewItemsAllowed(true);
		tabLayout.setSpacing(true);
		particular.setWidth("300px");
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

		space.setWidth("60px");
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
		costCentreIni();
		setButtonShortCut();		
		Component ob[] = {receivedFrom,acHead,costCentre,date,particular,amount,button.btnNew,button.btnUpdate,button.btnSave,button.btnRefresh,button.btnDelete,button.btnFind};
		new FocusMoveByEnter(this,ob);
		button.btnNew.focus();*/
	}

	public void addCmbParticularData()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			particular.removeAllItems();
			String sql="select NarrationId,Narration from tbNarrationList where NarrationId like '%CR%'";
			Iterator iter = session.createSQLQuery(sql).list().iterator();
			for(;iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				particular.addItem(element[1]);
				particular.setItemCaption(element[1],element[1].toString());
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

	private void setButtonAction()
	{
		acHead.addListener(new ValueChangeListener()
		{
			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				try
				{
					acHeadSelect();
				}
				catch(Exception ex)
				{
					showNotification("Error " + ex.toString(), Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});

		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				newBtnAction();
				receivedFrom.focus();
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(nullCheck())
					//saveBtnAction();
					if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
						saveBtnAction();
					else
						showNotification("Transaction Failed:","Transaction is closed for this year.",Notification.TYPE_WARNING_MESSAGE);	

					
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
				//deleteBtnAction();
				if (!new AstechacApplication().isClosedFiscal(dtfYMD.format(date.getValue())))
					deleteBtnAction();
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
				acHead.setValue(null);
				costCentre.setValue(null);
				voucherNo.setValue("");
				particular.setValue("");
				amount.setValue("");
				receivedFrom.setValue("");
				ledgerIni();
				costCentreIni();
				addCmbParticularData();
				button.btnNew.focus();
				
				billUpload.fileName = "";
				billUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
				filePathTmp = "";
				billUpload.actionCheck = false;
				imageLoc = "0";
			}
		});

		button.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				findBtnAction();
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
					String link = getApplication().getURL().toString();

					if(link.endsWith("uptd/"))
					{
						link = link.replaceAll("uptd", "report")+filePathTmp;
					}
					else if(link.endsWith("MSML/"))
					{
						link = link.replaceAll("MSML", "report")+filePathTmp;
					}
					else if(link.endsWith("RJSL/"))
					{
						link = link.replaceAll("RJSL", "report")+filePathTmp;
					}
					else if(link.endsWith("UNIGLOBAL/"))
					{
						link = link.replaceAll("UNIGLOBAL", "report")+filePathTmp;
					}

					System.out.println(link);
					System.out.println("aa :"+event.getSource());
					
					getWindow().open(new ExternalResource(link),"_blank", // window name
							500, // width
							200, // weight
							Window.BORDER_NONE // decorations
							);
				}
				if(isUpdate)
				{
					if(!billUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("uptd/"))
							{
								link = link.replaceAll("uptd/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("MSML/"))
							{
								link = link.replaceAll("MSML/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("RJSL/"))
							{
								link = link.replaceAll("RJSL/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("UNIGLOBAL/"))
							{
								link = link.replaceAll("UNIGLOBAL/", imageLoc.substring(27, imageLoc.length()));
							}

							System.out.println(link);
							System.out.println("bb : "+event.getSource());

							getWindow().open(new ExternalResource(link),"_blank", // window name
									500, // width
									200, // weight
									Window.BORDER_NONE // decorations
									);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(billUpload.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("uptd/"))
						{
							link = link.replaceAll("uptd", "report")+filePathTmp;
						}
						else if(link.endsWith("MSML/"))
						{
							link = link.replaceAll("MSML", "report")+filePathTmp;
						}
						else if(link.endsWith("RJSL/"))
						{
							link = link.replaceAll("RJSL", "report")+filePathTmp;
						}
						else if(link.endsWith("UNIGLOBAL/"))
						{
							link = link.replaceAll("UNIGLOBAL", "report")+filePathTmp;
						}

						System.out.println(link);
						System.out.println("aa :"+event.getSource());
						
						getWindow().open(new ExternalResource(link),"_blank", // window name
								500, // width
								200, // weight
								Window.BORDER_NONE // decorations
								);
					}
				}
			}
		});

		billUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				System.out.println("Done");
			}
		});
		
		btnAcHead.addListener(new ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{
				acHeadLink();	
			}
		});
		
		btnCostCentre.addListener(new ClickListener() 
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
			HashMap hm = new HashMap();
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("userName", sessionBean.getUserName());
			hm.put("userIp", sessionBean.getUserIp());
			
			hm.put("logo", sessionBean.getCompanyLogo());

			String link = getApplication().getURL().toString();

			if(link.endsWith("uptd/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("uptd/", ""));
			}
			else if(link.endsWith("MSML/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("MSML/", ""));
			}
			else if(link.endsWith("astecherp/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("astecherp/", ""));
			}
			else if(link.endsWith("UNIGLOBAL/"))
			{
				hm.put("urlLink", this.getApplication().getURL().toString().replaceAll("UNIGLOBAL/", ""));
			}

			String 	sql = "SELECT vwVoucher.Voucher_No AS vwVoucher_Voucher_No,vwVoucher.Date AS vwVoucher_Date,"+
					"tbLedger.Ledger_Name AS tbLedger_Ledger_Name,vwVoucher.Narration AS vwVoucher_Narration,"+
					"vwVoucher.CrAmount AS vwVoucher_CrAmount,dbo.number(vwVoucher.CrAmount)+' Only' amtWord,"+
					"vwVoucher.TransactionWith AS vwVoucher_TransactionWith,replace(vwVoucher.attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill FROM "+
					"dbo.tbLedger tbLedger INNER JOIN dbo.vwVoucher vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id and tbLedger.companyId = vwVoucher.companyId "+
					"WHERE Voucher_No IN ('"+voucherNo.getValue().toString()+"') AND vwVoucher.Ledger_Id != 'AL1' AND vwVoucher.companyId = '"+ sessionBean.getCompanyId() +"' AND (tbLedger.companyId = '"+ sessionBean.getCompanyId() +"') AND (vwVoucher.CrAmount > 0)";

			hm.put("sql",sql);
			System.out.println(sql);
			Window win = new ReportViewer(hm,"report/account/voucher/CashReceipttVoucher.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
			win.setStyleName("cwindow");
			getParent().getWindow().addWindow(win);
			win.setCaption("CASH RECEIVED VOUCHER :: "+sessionBean.getCompany());
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
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
				costCentreIni();
			}
		});
		this.getParent().addWindow(win);
	}
	
	public void acHeadLink()
	{
		Window win = new LedgerCreate(sessionBean);

		win.setStyleName("cwindow");
		win.setModal(true);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				ledgerIni();
			}
		});
		this.getParent().addWindow(win);
	}
	
	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			// image move
			if(billUpload.fileName.trim().length()>0)
				try {
					if(billUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId()+"CRV";
						fileMove(basePath+billUpload.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						crvPdf = SessionBean.imagePathTmp+path+".jpg";
						filePathTmp = path+".jpg";
					}else{
						String path = sessionBean.getUserId()+"CRV";
						fileMove(basePath+billUpload.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
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
			// image move
			if(billUpload.fileName.trim().length()>0)
				try {
					if(billUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+billUpload.fileName.trim(),SessionBean.imagePath+projectName+"/cashBillPayment/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/cashBillPayment/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+billUpload.fileName.trim(),SessionBean.imagePath+projectName+"/cashBillPayment/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/cashBillPayment/"+path+".pdf";
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
		if(acHead.getValue()!= null)
		{
			if(costCentre.getValue()!= null)
			{
				if(Double.valueOf("0"+amount.getValue())>0)
				{
					return true;
				}
				else
				{
					showNotification("","Please insert amount.",Notification.TYPE_WARNING_MESSAGE);
					amount.focus();
					return false;
				}
			}
			else
			{
				showNotification("","Please select a cost centre head.",Notification.TYPE_WARNING_MESSAGE);
				costCentre.focus();
				return false;
			}
		}
		else
		{
			showNotification("","Please select a account head.",Notification.TYPE_WARNING_MESSAGE);
			acHead.focus();
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

			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;

			String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
					"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'Delete' TType,costId, companyId from "+
					""+voucher+" as vwVoucher WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();

			sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();
			tx.commit();
			this.getParent().showNotification("Desired Information delete successfully.");
			
			isUpdate = false;
			voucherNo.setValue("");
			particular.setValue("");
			amount.setValue("");
			receivedFrom.setValue("");
			
			billUpload.fileName = "";
			billUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
			filePathTmp = "";
			billUpload.actionCheck = false;
			imageLoc = "0";
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private void newBtnAction()
	{
		btnIni(false);
		txtEnable(true);
		acHead.setValue(null);
		costCentre.setValue(null);
		voucherNo.setValue("");
		particular.setValue("");
		amount.setValue("");
		receivedFrom.setValue("");
		
		billUpload.fileName = "";
		billUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		billUpload.actionCheck = false;
		imageLoc = "0";
	}

	private void saveBtnAction()
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save all information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
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

			//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue()))>=Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
			//					&&
			//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue()))<=Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			//			{
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();


				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				int sl = 1;

				Iterator iter = session.createSQLQuery("SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE vouchertype in ('cca','cci') and CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
				if(iter.hasNext())
					sl = Integer.valueOf(iter.next().toString());
				
				String imagePath = imagePath(1,"CR-CH-"+sl)==null?imageLoc:imagePath(1,"CR-CH-"+sl);

				String sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill) "+
						" VALUES('CR-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','"+acHead.getValue()+"','"+particular.getValue()+"','0"+
						amount.getValue()+"','0','cca','"+receivedFrom.getValue()+"','"+costCentre.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"')";
				session.createSQLQuery(sql).executeUpdate();

				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill) "+
						" VALUES('CR-CH-"+sl+"','"+dtfYMD.format(date.getValue())+"','AL183','"+particular.getValue()+"','0','0"+
						amount.getValue()+"','cca','"+receivedFrom.getValue()+"','"+costCentre.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"')";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				this.getParent().showNotification("All information saved successfully.");
				txtEnable(false);
				btnIni(true);
				voucherNo.setValue("CR-CH-"+sl);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}
		}
		//			else
		//			{
		//				showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		//			}
		//		}
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

			//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate()))) 
			//					&&
			//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(date.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			//			{
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;
				
				String imagePath = imagePath(1,voucherNo.getValue().toString())==null?imageLoc:imagePath(1,voucherNo.getValue().toString());

				String sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
						"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'Update' TType,costId, companyId from "+
						""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				sql = "DELETE FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill) "+
						" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','"+acHead.getValue()+"','"+particular.getValue()+"','0"+
						amount.getValue()+"','0','cca','"+receivedFrom.getValue()+"','"+costCentre.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"')";
				session.createSQLQuery(sql).executeUpdate();

				sql = "INSERT INTO "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,CrAmount,DrAmount,vouchertype,TransactionWith,costId,userId,userIp,entryTime, auditapproveflag, companyId, attachBill) "+
						" VALUES('"+voucherNo.getValue()+"','"+dtfYMD.format(date.getValue())+"','AL183','"+particular.getValue()+"','0','0"+
						amount.getValue()+"','cca','"+receivedFrom.getValue()+"','"+costCentre.getValue()+"','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP, 0, '"+ sessionBean.getCompanyId() +"', '"+imagePath+"')";
				session.createSQLQuery(sql).executeUpdate();

				sql = "insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
						"vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'New' TType,costId, companyId from "+
						""+voucher+" WHERE Voucher_No = '"+voucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
				session.createSQLQuery(sql).executeUpdate();

				tx.commit();
				this.getParent().showNotification("Desired voucher no update successfully.");
				txtEnable(false);
				btnIni(true);
				button.btnNew.focus();
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
				tx.rollback();
			}

		}
		//			else
		//			{
		//				this.getParent().showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
		//			}
		//		}
		else
		{
			this.getParent().showNotification("Authentication Failed","You have not proper authentication for update.",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void findBtnAction()
	{ 
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;

		String sqlF = "SELECT tbLedger.Ledger_Id, tbLedger.Ledger_Name, vwVoucher.Date, vwVoucher.Voucher_No,"+
				"vwVoucher.Narration,vwVoucher.CrAmount,vwVoucher.DrAmount,vwVoucher.vouchertype FROM tbLedger "+
				"INNER JOIN "+voucher+" as vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id WHERE vouchertype = 'cca' "+
				"AND tbLedger.Ledger_Id  != 'AL183' AND vwVoucher.companyId = '"+ sessionBean.getCompanyId() +"' AND ";
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
				//System.out.println("Hello");   
				findInitialise();
			}
		});
		win.bringToFront();
	}

	private void findInitialise()
	{
		Transaction tx = null;
		if (voucherNo.getValue() != "")
		{
			try
			{
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				tx = session.beginTransaction();
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+vfDate.getValue()+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				Iterator iter = session.createSQLQuery("SELECT voucher_No,date,transactionWith,ledger_Id,narration,crAmount,costId,attachBill FROM "+voucher+" WHERE voucher_No = '"+voucherNo.getValue()+"' AND ledger_Id != 'AL183' AND companyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
				if(iter.hasNext())
				{
					Object[] element = (Object[]) iter.next();

					//				System.out.println(element[2].toString());
					receivedFrom.setValue(element[2].toString());
					acHead.setValue(element[3].toString());
					particular.addItem(element[4]);
					particular.setItemCaption(element[4],element[4].toString());
					particular.setValue(element[4].toString());				
					amount.setValue(fmt.format(Double.valueOf(element[5].toString())));
					date.setValue(new Date(element[1].toString().replace("-", "/").substring(0,10).trim()));
					if(element[6]!=null)
						costCentre.setValue(element[6].toString());
					
					imageLoc = element[7].toString();
				}
				button.btnEdit.focus();
			}
			catch(Exception exp)
			{
				showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
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
		button.btnPreview.setEnabled(t);
	}

	private void txtEnable(boolean t)
	{	
		receivedFrom.setEnabled(t);
		acHead.setEnabled(t);
		date.setEnabled(t);
		particular.setEnabled(t);
		amount.setEnabled(t);
		costCentre.setEnabled(t);
		budget.setEnabled(t);
		balance.setEnabled(t);
		billUpload.setEnabled(t);
		btnPreview.setEnabled(t);
		btnAcHead.setEnabled(t);
		btnCostCentre.setEnabled(t);
	}

	private void acHeadSelect()
	{

		try
		{
			System.out.println(acHead.getValue());
			if (acHead.getValue() != null)
			{
				//System.out.println("Hello");
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				Transaction tx = session.beginTransaction();	
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(date.getValue())+"')").list().iterator().next().toString();
				String voucher =  "voucher"+fsl;

				String msg = "";
				Iterator iter = session.createSQLQuery("SELECT substring(r,1,1) a,h+isnull('\\'+g,'')+isnull('\\'+s,'')+'\\'+l b FROM VwLedgerList WHERE ledger_Id = '"+acHead.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator();
				Object[] element = (Object[]) iter.next();
				if(element[0].toString().equalsIgnoreCase("A"))
					msg = "Assets\\"+element[1].toString();
				else if(element[0].toString().equalsIgnoreCase("I"))
					msg = "Income\\"+element[1].toString();
				else if(element[0].toString().equalsIgnoreCase("E"))
					msg = "Expenses\\"+element[1].toString();
				else 
					msg = "Liabilities\\"+element[1].toString();

				this.showNotification("",msg,Notification.TYPE_TRAY_NOTIFICATION);

				iter = session.createSQLQuery("SELECT budgetAmount FROM TbBudget WHERE ledger_Id = '"+acHead.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' AND op_Year = (SELECT year(op_Date) FROM TbFiscal_Year WHERE slNo = "+fsl+")").list().iterator();

				double budAmt = 0;
				if(iter.hasNext())
					budAmt = Double.valueOf(iter.next().toString());

				double bal = Double.valueOf(session.createSQLQuery("SELECT ISNULL(SUM(drAmount)- SUM(crAmount),0) FROM "+voucher+" WHERE Ledger_Id = '"+ acHead.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'").list().iterator().next().toString());

				double opBal = Double.valueOf(session.createSQLQuery("SELECT ISNULL(SUM(drAmount)- SUM(crAmount),0) FROM TbLedger_Op_Balance WHERE ledger_Id = '"+ acHead.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' AND op_Year = (SELECT year(op_Date) FROM TbFiscal_Year WHERE slNo = "+fsl+")").list().iterator().next().toString());

				budget.setValue(cms.setComma(Double.valueOf(budAmt)));
				balance.setValue(cms.setComma(Double.valueOf((bal+opBal))));
			}
			else
			{
				budget.setValue(0);
				balance.setValue(0);
				System.out.println("Hello");
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}		
	}

	private void ledgerIni()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			acHead.removeAllItems();
			List group = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE ledger_Id!='AL183' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				acHead.addItem(element[0].toString());
				acHead.setItemCaption(element[0].toString(), element[1].toString());
			}
			acHead.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void costCentreIni()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			costCentre.removeAllItems();
			List group = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();
			for (Iterator iter = group.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				costCentre.addItem(element[0].toString());
				costCentre.setItemCaption(element[0].toString(), element[1].toString());
			}
			costCentre.setNullSelectionAllowed(false);
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
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
		setWidth("650px");
		setHeight("370px");
		
		// lblreceivedFrom
		lblreceivedFrom = new Label();
		lblreceivedFrom.setImmediate(true);
		lblreceivedFrom.setWidth("-1px");
		lblreceivedFrom.setHeight("-1px");
		lblreceivedFrom.setValue("Received From : ");
		mainLayout.addComponent(lblreceivedFrom, "top:20.0px;left:20.0px;");

		// receivedFrom
		receivedFrom = new TextField();
		receivedFrom.setImmediate(true);
		receivedFrom.setWidth("245px");
		receivedFrom.setHeight("-1px");
		receivedFrom.setSecret(false);
		mainLayout.addComponent(receivedFrom, "top:16.0px;left:110.0px;");
		
		// lblCostCentre
		lblCostCentre = new Label();
		lblCostCentre.setImmediate(true);
		lblCostCentre.setWidth("70px");
		lblCostCentre.setHeight("-1px");
		lblCostCentre.setValue("Cost Centre:");
		mainLayout.addComponent(lblCostCentre, "top:46.0px;left:36.0px;");

		// costCentre
		costCentre = new ComboBox();
		costCentre.setImmediate(true);
		costCentre.setNullSelectionAllowed(false);
		costCentre.setWidth("210px");
		costCentre.setHeight("-1px");
		mainLayout.addComponent(costCentre, "top:44.0px;left:110.0px;");
		
		// btnCostCentre
		btnCostCentre = new NativeButton();
		btnCostCentre.setCaption("");
		btnCostCentre.setImmediate(true);
		btnCostCentre.setWidth("28px");
		btnCostCentre.setHeight("24px");
		btnCostCentre.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnCostCentre,"top:44.0px;left:325.0px;");

		// lblAccountsHead
		lblAccountsHead = new Label();
		lblAccountsHead.setImmediate(true);
		lblAccountsHead.setWidth("98px");
		lblAccountsHead.setHeight("-1px");
		lblAccountsHead.setValue("Accounts Head:");
		mainLayout.addComponent(lblAccountsHead, "top:74.0px;left:20.0px;");

		// acHead
		acHead = new ComboBox();
		acHead.setImmediate(true);
		acHead.setWidth("250px");
		acHead.setHeight("-1px");
		acHead.setNullSelectionAllowed(false);
		mainLayout.addComponent(acHead, "top:72.0px;left:110.0px;");
		
		// btnAcHead
		btnAcHead = new NativeButton();
		btnAcHead.setCaption("");
		btnAcHead.setImmediate(true);
		btnAcHead.setWidth("28px");
		btnAcHead.setHeight("24px");
		btnAcHead.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnAcHead,"top:72.0px;left:365.0px;");

/*		// lblParticular
		lblParticular = new Label();
		lblParticular.setImmediate(true);
		lblParticular.setWidth("74px");
		lblParticular.setHeight("-1px");
		lblParticular.setValue("Particular:");
		mainLayout.addComponent(lblParticular, "top:128.0px;left:50.0px;");*/

/*		// particular
		particular = new ComboBox();
		particular.setImmediate(true);
		particular.setNullSelectionAllowed(false);
		particular.setNewItemsAllowed(true);
		particular.setWidth("300px");
		mainLayout.addComponent(particular, "top:124.0px;left:110.0px;");*/

		// billUpload
		mainLayout.addComponent(billUpload, "top:100.0px;left:110.0px;");

		// lblDate
		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("60px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date:");
		mainLayout.addComponent(lblDate, "top:20.0px;left:412.0px;");

		// date
		date.setValue(new java.util.Date());
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		mainLayout.addComponent(date, "top:18.0px;left:482.0px;");
		
		// lblVoucherNo
		lblVoucherNo = new Label();
		lblVoucherNo.setImmediate(true);
		lblVoucherNo.setWidth("80px");
		lblVoucherNo.setHeight("-1px");
		lblVoucherNo.setValue("Voucher No:");
		mainLayout.addComponent(lblVoucherNo, "top:44.0px;left:412.0px;");

		// voucherNo
		voucherNo = new TextField();
		voucherNo.setImmediate(true);
		voucherNo.setWidth("108px");
		voucherNo.setHeight("-1px");
		voucherNo.setSecret(false);
		mainLayout.addComponent(voucherNo, "top:44.0px;left:482.0px;");

		// lblBudget
		lblBudget = new Label();
		lblBudget.setImmediate(true);
		lblBudget.setWidth("-1px");
		lblBudget.setHeight("-1px");
		lblBudget.setValue("Budget Amt.:");
		mainLayout.addComponent(lblBudget, "top:74.0px;left:412.0px;");

		// budget
		budget = new TextRead();
		budget.setImmediate(true);
		budget.setWidth("108px");
		budget.setHeight("-1px");
		mainLayout.addComponent(budget, "top:72.0px;left:482.0px;");

		// lblBalance
		lblBalance = new Label();
		lblBalance.setImmediate(true);
		lblBalance.setWidth("52px");
		lblBalance.setHeight("-1px");
		lblBalance.setValue("Balance:");
		mainLayout.addComponent(lblBalance, "top:102.0px;left:412.0px;");

		// balance
		balance = new TextRead();
		balance.setImmediate(true);
		balance.setWidth("108px");
		balance.setHeight("-1px");
		mainLayout.addComponent(balance, "top:98.0px;left:482.0px;");

/*		// lblAmount
		lblAmount = new Label();
		lblAmount.setImmediate(true);
		lblAmount.setWidth("51px");
		lblAmount.setHeight("-1px");
		lblAmount.setValue("Amount:");
		mainLayout.addComponent(lblAmount, "top:128.0px;left:412.0px;");*/

	/*	// txtAmount
		amount = new AmountCommaSeperator();
		amount.setWidth("90px");
		amount.setStyleName("fright");
		mainLayout.addComponent(amount, "top:124.0px;left:462.0px;");*/

		// btnPreview
		btnPreview = new Button("Bill Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:125.0px;left:220.0px;");
		
		
		table.setFooterVisible(true);
		table.setWidth("530px");
		table.setHeight("80px");

		table.addContainerProperty("Particular", ComboBox.class, particular,null,null,Table.ALIGN_CENTER);
		particular.setWidth("360px");
		particular.setNullSelectionAllowed(true);
		particular.setNewItemsAllowed(true);
		particular.setImmediate(true);

		table.setColumnWidth("Particular", 360);
		table.addContainerProperty("Amount", AmountCommaSeperator.class, amount,null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Amount", 110);
		amount.setWidth("110px");
		amount.setStyleName("fright");
		table.addItem(new Object[]{particular,amount},0); 
		
		mainLayout.addComponent(table, "top:170.0px;left:50.0px;");

		//common button
		btnLayout.addComponent(button);
		mainLayout.addComponent(btnLayout, "top:280.0px;left:20.0px;");

		return mainLayout;
	}
}
