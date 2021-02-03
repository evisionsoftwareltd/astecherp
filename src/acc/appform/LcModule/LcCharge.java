package acc.appform.LcModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class LcCharge extends Window
{
	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"Debit","Credit"});
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","Delete", "Refresh", "Find", "", "Preview", "", "Exit");
	private SessionBean sessionBean;

	private Table table = new Table();
	private ArrayList<Label> sl = new ArrayList<Label>();
	public ArrayList<ComboBox> cmbExpencehead = new ArrayList<ComboBox>();
	private ArrayList<AmountCommaSeperator> txtAmount = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextField> txtRemarks = new ArrayList<TextField>();

	ArrayList<Component> allComp = new ArrayList<Component>();	

	private boolean isUpdate = false;
	private boolean isFind= false;
	private int tr = 7;

	public TextField vflag = new TextField();
	public TextField vDate = new TextField();
	/*private Label lblShip;
	private ComboBox cmbShip;*/
	private Label lbllc,lblDrBalance,lblCrBalance;
	private ComboBox cmbLc;
	private Label lblBankAccount;
	private ComboBox cmbBankAccNo;
	private Label lblCostCenter;
	private ComboBox cmbCostCenter;
	private Label lblDate;
	private DateField date;
	private Label lblVoucherNo;
	private TextRead txtvoucherno;
	private TextRead txtLCNoBalance;
	private TextRead txtCreditBalance;

	private Label lblRefferenceNo;
	private TextRead txtrefferenceno;
	
	private TextRead txtreferenceNo=new TextRead();


	private AbsoluteLayout mainLayout;
	private Label lblDescription= new Label();

	private ImmediateUploadExample jvUpload = new ImmediateUploadExample("");

	private ComboBox cmbDescription= new ComboBox();
	private TextRead totalField = new TextRead(1);


	private NativeButton btnBankHead;
	private NativeButton btnCostCenter;
	String transactionNoglb="";
	String referenceglb="";


	String jvPdf = null;
	String filePathTmp = "";
	String imageLoc = "0";
	Button btnPreview;
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private TextField txtFromFindWindow= new TextField();
	private NumberFormat frmt = new DecimalFormat("#0.00");
	private CommaSeparator cms = new CommaSeparator();

	public LcCharge(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("L/C Charge :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		buttonActionAdd();
		//AddShipData();
		bankHeadIni();
		cmbCostCenterLoadData();

		btnIni(true);
		componentIni(true);

		cButton.btnNew.focus();
		focusEnter();
		authencationCheck();
		lcNoLoad();
	}

	private void authencationCheck()
	{
		if(sessionBean.isSubmitable())
		{
			cButton.btnSave.setVisible(true);
		}
		else
		{
			cButton.btnSave.setVisible(false);
		}
		if(sessionBean.isUpdateable())
		{
			cButton.btnEdit.setVisible(true);
		}
		else
		{
			cButton.btnEdit.setVisible(false);
		}
		if(sessionBean.isDeleteable())
		{
			cButton.btnDelete.setVisible(true);
		}
		else
		{
			cButton.btnDelete.setVisible(false);
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
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
				List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();

				costCentreIni(cmbCostCenter, costCenter,session);
			}
		});
		this.getParent().addWindow(win);
	}
	private void cmbCostCenterLoadData(){
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();		
		List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();

		costCentreIni(cmbCostCenter, costCenter,session);
	}
	private void costCentreIni(ComboBox cmb, List<?> costCente,Session session)
	{
		Transaction tx = null;
		try
		{
			for (Iterator<?> iter = costCente.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmb.addItem(element[0].toString());
				cmb.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void headSelect(String head,int t,String caption)
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			if(head.equalsIgnoreCase("x"))
			{
				/*if(t > -1)
				{
					balance.get(t).setValue(0);
				}
				else
				{*/
				txtLCNoBalance.setValue(0);
				//}
				if(caption.equalsIgnoreCase("LC")){
					txtLCNoBalance.setValue(0);
				}
				else if(caption.equalsIgnoreCase("Credit")){
					txtCreditBalance.setValue(0);
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

				//if(t > -1)
				//{
				//txtLCNoBalance.setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
				//}
				//else
				//{
				//	debCrdBal.setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
				//}
				if(caption.equalsIgnoreCase("LC")){
					txtLCNoBalance.setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
				}
				else if(caption.equalsIgnoreCase("Credit")){
					txtCreditBalance.setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
				}

			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void deleteButtonEvent(){
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to Delete information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{

				if(buttonType == ButtonType.YES)
				{
					deleteData();
					txtClear();
					showNotification("All Information Delete Successfully",Notification.TYPE_WARNING_MESSAGE);
				}	
			}
		});
	}
	private void buttonActionAdd()
	{
		RadioBtnGroup.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Debit")){
					lbllc.setValue("LC No(Dr.):");
					lblBankAccount.setValue("Credit Account(Cr.) :");
					lblDrBalance.setValue("Debit Acc.[Balance] :");
					lblCrBalance.setValue("Credit Acc.[Balance] :");
				}

				else{
					lbllc.setValue("LC No(Cr.):");
					lblBankAccount.setValue("Debit Account(Dr.) :");
					lblDrBalance.setValue("Credit Acc.[Balance] :");
					lblCrBalance.setValue("Debit Acc.[Balance] :");
				}
			}
		});
		btnCostCenter.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				costCentreLink();
			}
		});
		cButton.btnDelete.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(!txtvoucherno.getValue().toString().isEmpty()){
					deleteButtonEvent();
				}
				else{
					showNotification("Nothing To Delete",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				System.out.print("ok");
				isFind = false;
				isUpdate = false;
				newBtnAction();
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshButtonEvent();
				tableClear();
				isUpdate = false;
			}
		});

		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				findButtonEvent();
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

					if(link.endsWith("RSRM/"))
					{
						link = link.replaceAll("RSRM", "report")+filePathTmp;
					}
					else if(link.endsWith("RSRIL/"))
					{
						link = link.replaceAll("RSRIL", "report")+filePathTmp;
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

					getWindow().open(new ExternalResource(link),"_blank", 500, 200, Window.BORDER_NONE);
				}
				if(isUpdate)
				{
					if(!jvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith("RSRM/"))
							{
								link = link.replaceAll("RSRM/", imageLoc.substring(22, imageLoc.length()));
							}
							else if(link.endsWith("RSRIL/"))
							{
								link = link.replaceAll("RSRIL/", imageLoc.substring(22, imageLoc.length()));
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
								link = link.replaceAll("UNIGLOBAL/", imageLoc.substring(22, imageLoc.length()));
							}

							System.out.println(link);
							System.out.println("bb : "+event.getSource());

							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(jvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith("RSRM/"))
						{
							link = link.replaceAll("RSRM", "report")+filePathTmp;
						}
						else if(link.endsWith("RSRIL/"))
						{
							link = link.replaceAll("RSRIL", "report")+filePathTmp;
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

						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});
		cButton.btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBtnAction();
			}
		});
		jvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				System.out.println("Done");
			}}
				);

		cButton.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				formValidation();
			}
		});

		cButton.btnEdit.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				if(isClosed()){
					isUpdate=true;
					componentIni(false);
					btnIni(false);
				}
				else{
					showNotification("Sorry!!","LC No: "+cmbLc.getItemCaption(cmbLc.getValue())+" is already Closed",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		btnBankHead.addListener(new Button.ClickListener() 
		{	
			public void buttonClick(ClickEvent event) 
			{	
				bankHeadLink();
			}
		});
		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});
		cmbLc.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				headSelect(cmbLc.getValue()== null?"x":cmbLc.getValue().toString(),-1,"LC");
			}
		});
		cmbBankAccNo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				headSelect(cmbBankAccNo.getValue()== null?"x":cmbBankAccNo.getValue().toString(),-1,"CREDIT");
			}
		});
	}
	private void previewBtnAction()
	{
		if(!txtvoucherno.getValue().toString().isEmpty())
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

			String sql = "SELECT * FROM vwJournalVoucher WHERE Voucher_No in('"+txtvoucherno.getValue().toString()+"') And companyId = '"+ sessionBean.getCompanyId() +"' and company_Id = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";

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
	private boolean isClosed(){

		Iterator<?>iter=dbService("select * from tbLcOpeningInfo where vLedgerID='"+cmbLc.getValue()+"' " +
				"and vLcNo='"+cmbLc.getItemCaption(cmbLc.getValue())+"' and isActive!=0");
		if(iter.hasNext()){
			return true;
		}

		return false;
	}
	private void formValidation()
	{
		if(cmbLc.getValue()!=null)
		{
			if(cmbBankAccNo.getValue()!=null)
			{
				//if(!txtvoucherno.getValue().toString().isEmpty())
				//{
				if(cmbExpencehead.get(0).getValue()!=null)
				{
					if(!txtAmount.get(0).getValue().toString().isEmpty())
					{
						if(isClosed()){
							saveBtnAction();
						}
						else{
							showNotification("Sorry!!","LC No: "+cmbLc.getItemCaption(cmbLc.getValue())+" is already Closed",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please  enter amount", Notification.TYPE_WARNING_MESSAGE);
						txtAmount.get(0).focus();
					}
				}
				else
				{
					showNotification("Warning","Please  enter value for Expences head", Notification.TYPE_WARNING_MESSAGE);
					cmbExpencehead.get(0).focus();
				}
				/*}
				else
				{
					showNotification("Warning","Please enter Voucher No ", Notification.TYPE_WARNING_MESSAGE);
				}*/
			}
			else
			{
				showNotification("Warning","Please select Bank Account No ", Notification.TYPE_WARNING_MESSAGE);
				cmbBankAccNo.focus();
			}
		}
		else
		{
			showNotification("Warning","Please select L/C No ", Notification.TYPE_WARNING_MESSAGE);
			cmbLc.focus();
		}
	}

	private void saveBtnAction()
	{
		try
		{
			if(sessionBean.isUpdateable())
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
								Transaction tx;
								Session session = SessionFactoryUtil.getInstance().getCurrentSession();
								tx = session.beginTransaction();

								if(deleteData())
								{
									insertData();
									isUpdate=false;
								}
								else
								{
									tx.rollback();		
								}
							}	
						}
					});
				}
				else
				{
					final MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to save information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
					mb.show(new EventListener()
					{
						public void buttonClicked(ButtonType buttonType)
						{
							if(buttonType == ButtonType.YES)
							{	mb.buttonLayout.getComponent(0).setEnabled(false);						
								insertData();
								isUpdate=false;
							}
						}
					});
				}
			}
		}
		catch(Exception ex)
		{
			this.getParent().showNotification("Error.", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private String getTransactionNo(){
		String sql="select isnull(MAX(transactionNo),0)+1 from tbLCChargeInfo";
		Iterator<?>iter=dbService(sql);
		if(iter.hasNext()){
			return iter.next().toString();
		}
		return "";
	}

	private String getRefferenceNo()
	{
		//String sql="select CONVERT(varchar,YEAR(GETDATE()))+'-'+CONVERT(varchar,MONTH(GETDATE()))+'-'+cast(isnull(max(cast(SUBSTRING(REVERSE(referenceNo),0,len(referenceNo)-CHARINDEX('-',referenceNo)-1)as int)),0)+1 as varchar) from tbLCChargeInfo";
		String sql="select CONVERT(varchar,YEAR(GETDATE()))+'-'+CONVERT(varchar,MONTH(GETDATE()))+'-'+CAST(ISNULL(MAX(CAST(substring(referenceNo,CHARINDEX('-',referenceNo,6)+1,LEN(referenceNo)) as int) ),0)+1  as varchar) from tbLCChargeInfo";


		Iterator<?>iter=dbService(sql);
		if(iter.hasNext()){
			return iter.next().toString();
		}
		return "";
	}
	private void insertData()
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String query=null;
		String voucherNo="";
		if(!isUpdate)
		{
			voucherNo = "JV-NO-"+getVoucher();	
			txtvoucherno.setValue(voucherNo);
		}

		if(isUpdate)
		{
			voucherNo=txtvoucherno.getValue().toString();	
		}

		String voucher =  "voucher"+selectVoucher();
		String invoiceNo = "JV-NO-"+voucherNo;
		//txtvoucherno.setValue(invoiceNo);
		String transactionNo="";
		if(!isUpdate)
		{
			transactionNo=getTransactionNo();	
		}
		else
		{
			transactionNo=transactionNoglb;	
		}
		String RefferenceNo="";
		if(!isUpdate)
		{
			RefferenceNo=getRefferenceNo();	
		}
		else
		{
			RefferenceNo=referenceglb;
		}



		String imagePath = imagePath(1,voucher)==null?imageLoc:imagePath(1,voucher);

		try
		{
			//String Naration = "LC: "+cmbLc.getItemCaption(cmbLc.getValue())+", Date: "+dFormat.format(date.getValue())+", Bank: "+cmbBankAccNo.getItemCaption(cmbBankAccNo.getValue())+", "+totalField.getValue().toString().replace(",", "");
			String Naration=cmbDescription.getValue()==null?"":cmbDescription.getValue().toString();
			query="insert into tbLcChargeInfo(transactionNo,referenceNo,ledgerIdLc,lcNo,ledgerIdBank,"+
					"ledgerBankName,date,voucherNo,attachment,description,totalamount,userName,userIp,entryTime,costid)"+
					"values('"+transactionNo+"','"+RefferenceNo+"','"+cmbLc.getValue().toString()+"'," +
					"'"+cmbLc.getItemCaption(cmbLc.getValue())+"','"+cmbBankAccNo.getValue()+"','"+cmbBankAccNo.getItemCaption(cmbBankAccNo.getValue())+"'," +
					"'"+dateF.format(date.getValue())+"','"+voucherNo+"','"+imagePath+"','"+cmbDescription.getValue()+"'," +
					"'"+totalField.getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'"+cmbCostCenter.getValue()+"')";
			System.out.println("Insert : "+query);
			session.createSQLQuery(query).executeUpdate();

			for(int i=0; i<sl.size(); i++)
			{
				if((cmbExpencehead.get(i).getValue()!=null) && (!txtAmount.get(i).getValue().toString().isEmpty()))
				{
					String sql="insert into tbLcChargeDetails (transactionNo,referenceNo,voucherNo,headId,headName,amount,remarks)" +
							"values('"+transactionNo+"','"+RefferenceNo+"','"+voucherNo+"'," +
							"'"+cmbExpencehead.get(i).getValue()+"','"+cmbExpencehead.get(i).getItemCaption(cmbExpencehead.get(i).getValue())+"'," +
							"'"+txtAmount.get(i).getValue()+"','"+txtRemarks.get(i).getValue()+"')";

					session.createSQLQuery(sql).executeUpdate();				
				}
			}
			
			String debitHead="",creditHead="";
			if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Debit")){
				debitHead=cmbLc.getValue().toString();
				creditHead=cmbBankAccNo.getValue().toString();
			}
			else if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Credit")){
				creditHead=cmbLc.getValue().toString();
				debitHead=cmbBankAccNo.getValue().toString();
			}
			
			String lcLedger = " INSERT into "+voucher+" values(" +
					" '"+voucherNo+"'," +
					" '"+dFormat.format(date.getValue())+"'," +
					" '"+debitHead+"'," +//" 'Bank'," +//
					" '"+Naration+"'," +
					" '"+totalField.getValue().toString().replace(",", "")+"'," +
					" '0.00'," +
					" 'jau'," +
					" '"+cmbLc.getItemCaption(cmbLc.getValue().toString())+"'," +
					" ''," +
					" '1'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '2'," +
					" '"+sessionBean.getCompanyId()+"',"+
					" '"+imagePath+"', "+
					" '0'," +
					" ''," +
					" '','','','lcChargeEntry')";

			session.createSQLQuery(lcLedger).executeUpdate();

			String bankLedger = " INSERT into "+voucher+" values(" +
					" '"+voucherNo+"'," +
					" '"+dFormat.format(date.getValue())+"'," +
					" '"+creditHead+"'," +
					" '"+Naration+"'," +						
					" '0.00'," +
					" '"+totalField.getValue().toString().replace(",", "")+"'," +
					" 'jau'," +
					" '"+cmbLc.getItemCaption(cmbLc.getValue().toString())+"'," +
					" ' '," +
					" '1'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '2'," +
					" '"+sessionBean.getCompanyId()+"',"+
					" '"+imagePath+"', "+
					" '0'," +
					" ''," +
					" '','','','lcChargeEntry')";

			session.createSQLQuery(bankLedger).executeUpdate();

			tx.commit();
			this.getParent().showNotification("All information save successfully.");
			componentIni(true);
			btnIni(true);
			//txtClear();
		}

		catch(Exception ex)
		{
			this.getParent().showNotification("Error at insert data", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
	}

	private boolean deleteData()
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String voucher =  "voucher"+selectVoucher();

		try
		{
			String delFormSql = " delete from tbLcChargeInfo where voucherNo='"+txtvoucherno.getValue().toString()+"' and referenceNo  like '"+txtrefferenceno.getValue().toString()+"' ";
			String delTableSql = " delete from tbLcChargeDetails where voucherNo='"+txtvoucherno.getValue().toString()+"' and referenceNo like '"+txtrefferenceno.getValue().toString()+"' ";
			String delVoucherSql = " delete from "+voucher+" where Voucher_No='"+txtvoucherno.getValue().toString()+"' ";

			session.createSQLQuery(delFormSql).executeUpdate();
			session.createSQLQuery(delTableSql).executeUpdate();
			session.createSQLQuery(delVoucherSql).executeUpdate();

			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
			return false;
		}
	}

	private String getVoucher()
	{
		String voucher =  "voucher"+selectVoucher();	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String invoice = session.createSQLQuery(" Select cast(isnull(max(cast(replace(Voucher_No, 'JV-NO-', '')as int))+1, 1)as varchar) from "+voucher+" where Voucher_No like 'JV-NO-%' ").list().iterator().next().toString();

		return invoice;
	}

	private String getBankLedgerId()
	{
		String LedgerId="";
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		LedgerId = session.createSQLQuery(" SELECT Ledger_Id from tbLedger where Ledger_Name = '"+cmbBankAccNo.getItemCaption(cmbBankAccNo.getValue())+"' ").list().iterator().next().toString();
		System.out.println(LedgerId+1);
		return LedgerId;
	}

	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			// image move
			if(jvUpload.fileName.trim().length()>0)
				try
			{
					if(jvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"JV";
						fileMove(basePath+jvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						jvPdf = SessionBean.imagePathTmp+path+".jpg";
						filePathTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"JV";
						fileMove(basePath+jvUpload.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						jvPdf = SessionBean.imagePathTmp+path+".pdf";
						filePathTmp = path+".pdf";
					}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return jvPdf;
		}

		if(flag==1)
		{
			// image move
			if(jvUpload.fileName.trim().length()>0)
				try
			{
					if(jvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+jvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/journalBillPayment/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/journalBillPayment/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+jvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/journalBillPayment/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/journalBillPayment/"+path+".pdf";
					}
			}
			catch (IOException e)
			{

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
		catch(Exception exp)
		{}

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
	private Iterator<?> dbService(String sql){
		Transaction tx=null;
		Session session=null;
		Iterator<?> iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter=session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){

				session.close();
			}
		}
		return iter;
	}
	private void newBtnAction()
	{
		btnIni(false);
		componentIni(false);
		txtClear();
		isUpdate = false;
		txtrefferenceno.setValue(getRefferenceNo());
		//setVoucherNo();

	}
	private void lcNoLoad(){
		Iterator<?>iter=dbService(" SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A4','A8', 'L7','L8','L10','A6') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"' ) ORDER BY ledger_Name ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbLc.addItem(element[0]);
			cmbLc.setItemCaption(element[0],element[1].toString());
		}
	}
	private void findButtonEvent()
	{
		Window win=new LcChrageFind(sessionBean,txtFromFindWindow,"shift",txtreferenceNo);
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txtFromFindWindow.getValue().toString().length()>0)
				{
					txtClear();
					findInitialise(txtFromFindWindow,txtreferenceNo.getValue().toString());
				}
			}
		});
		this.getParent().addWindow(win);
	}

	private void setVoucherNo()
	{
		String invoice = getVoucher();
		txtvoucherno.setValue("JV-NO-"+invoice);
	}

	private void bankHeadIni()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery(" SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A4','A8', 'L7','L8','L10','A6') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"' ) ORDER BY ledger_Name ").list();

			for (Iterator<?> iter = bh.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbBankAccNo.addItem(element[0].toString());
				cmbBankAccNo.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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

	private void componentIni(boolean b) 
	{
		txtrefferenceno.setEnabled(!b);
		cmbLc.setEnabled(!b);
		cmbBankAccNo.setEnabled(!b);
		date.setEnabled(!b);
		txtvoucherno.setEnabled(!b);
		jvUpload.setEnabled(!b);
		btnPreview.setEnabled(!b);
		table.setEnabled(!b);
		cmbDescription.setEnabled(!b);
		btnBankHead.setEnabled(!b);
		cmbCostCenter.setEnabled(!b);
		btnCostCenter.setEnabled(!b);
		txtLCNoBalance.setEnabled(!b);
		txtCreditBalance.setEnabled(!b);
		//	btnShip.setEnabled(!b);
	}

	private void txtClear()
	{
		txtrefferenceno.setValue("");
		cmbLc.setValue(null);
		cmbBankAccNo.setValue(null);
		cmbCostCenter.setValue(null);
		//date.setValue(new java.util.Date());
		txtvoucherno.setValue("");
		cmbDescription.setValue(null);
		jvUpload.fileName = "";
		jvUpload.status.setValue(new Label("<font size=1px>(.pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		jvUpload.actionCheck = false;
		imageLoc = "0";

		for(int i=0;i<sl.size();i++)
		{
			cmbExpencehead.get(i).setValue(null);
			txtAmount.get(i).setValue("");
			txtRemarks.get(i).setValue("");
		}
		table.setColumnFooter("Amount","Total: 0.0");
		totalField.setValue("");
		isUpdate=false;
		txtLCNoBalance.setValue("");
		txtCreditBalance.setValue("");
	}

	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void focusEnter()
	{
		allComp.add(cmbLc);
		allComp.add(cmbBankAccNo);
		allComp.add(date);
		allComp.add(txtvoucherno);

		for(int i=0;i<cmbExpencehead.size();i++)
		{
			allComp.add(cmbExpencehead.get(i));
			allComp.add(txtAmount.get(i));	
		}
		allComp.add(cmbDescription);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
	}
	/*
	private void cmblcdataAdd()
	{
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			cmbLc.removeAllItems();
			List lst = session.createSQLQuery("select 0, lcNo from tbLcInformation where shipId='"+cmbShip.getValue().toString()+"'").list();

			for (Iterator iter = lst.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbLc.addItem(element[1].toString());
				cmbLc.setItemCaption(element[1].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}*/

	private String selectVoucher()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String voucher = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dFormat.format(date.getValue())+"') as voucher").list().iterator().next().toString();

		return voucher;
	}

	private void findInitialise(Object voucherId,String referenceNo) 
	{		
		String sql = null;
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try 
		{			
			sql = "select referenceNo,ledgerIdLc,ledgerIdBank,date,voucherNo,costid,transactionNo,description, isnull(voucherType,'')  from tbLcChargeInfo where voucherNo='"+voucherId+"' and referenceNo like '"+referenceNo+"'  ";
			System.out.println("sql is :"+sql);
			
			List<?> led = session.createSQLQuery(sql).list();
			if(led.iterator().hasNext()){
				Object element[]=(Object[])led.iterator().next();
				txtrefferenceno.setValue(element[0]);
				cmbLc.setValue(element[1].toString());
				cmbBankAccNo.setValue(element[2]);
				date.setValue(element[3]);
				txtvoucherno.setValue(element[4]);
				cmbCostCenter.setValue(element[5]);
				transactionNoglb=element[6].toString();
				referenceglb=element[0].toString();
				cmbDescription.setValue(element[7]);
				if(element[8].equals("Debit")){
					RadioBtnGroup.setValue("Debit");
				}
				else if(element[8].equals("Credit")){
					RadioBtnGroup.setValue("Credit");
				}

			}
			
			System.out.println("Nothing");

			int i = 0;
			String sqlDetails="  select headid,amount,remarks from tbLcChargeDetails where voucherNo='"+voucherId+"' and referenceNo like '"+referenceNo+"' ";
			List<?> led1 = session.createSQLQuery(sqlDetails).list();
			for (Iterator<?> iter = led1.iterator();iter.hasNext();)
			{
				Object[] element1 = (Object[]) iter.next();
				if(sl.size()-1==i)
				{
					tableRowAdd(i+1);
				}
				cmbExpencehead.get(i).setValue(element1[0].toString());
				txtAmount.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element1[1].toString())));
				txtRemarks.get(i).setValue(element1[2].toString());
				i++;
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}

	private void tableClear()
	{
		for(int i=0;i<sl.size();i++)
		{
			cmbExpencehead.get(i).setValue(null);
			txtAmount.get(i).setValue("");
			txtRemarks.get(i).setValue("");
		}
	}

	private void tableInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		for(int i=0;i<tr;i++)
		{
			tableRowAdd(i);	
		}
	}

	private void tableRowAdd(final int ar)
	{	
		try
		{
			sl.add(ar, new Label(""));
			sl.get(ar).setWidth("100%");
			sl.get(ar).setHeight("15px");
			sl.get(ar).setValue(ar+1);

			cmbExpencehead.add(ar, new ComboBox());
			cmbExpencehead.get(ar).setWidth("100%");
			cmbExpencehead.get(ar).setImmediate(true);
			cmbExpencehead.get(ar).removeAllItems();
			cmbExpencehead.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			cmbExpencehead.get(ar).setNullSelectionAllowed(true);


			try
			{	
				Session session = SessionFactoryUtil.getInstance().openSession();
				session.beginTransaction();
				List<?> lst = session.createSQLQuery("select headId,headName from tbLCHeadInfo where isActive=1 order by headName").list();
				for(Iterator<?> iter = lst.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					cmbExpencehead.get(ar).addItem(element[0].toString() );	
					cmbExpencehead.get(ar).setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception ex)
			{

			}

			cmbExpencehead.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if (cmbExpencehead.get(ar).getValue()!=null)
					{
						boolean fla = (doubleEntryCheck(cmbExpencehead.get(ar).getItemCaption(cmbExpencehead.get(ar).getValue()), ar));

						if ((Object) cmbExpencehead.get(ar).getValue() != null && fla)
						{
							try
							{

								if (ar==cmbExpencehead.size()-1)
								{
									tableRowAdd(cmbExpencehead.size());
									focusEnter();
								}
								txtAmount.get(ar).focus();
							}
							catch(Exception exp)
							{
								showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
							}
						}

						else 
						{
							Object checkNull = (Object) cmbExpencehead.get(ar).getItemCaption(cmbExpencehead.get(ar).getValue());

							if(!checkNull.equals("")) 
							{
								cmbExpencehead.get(ar).setValue("x#" + ar);

								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
								cmbExpencehead.get(ar).setValue(null);
							}
						}
					}
				}
			});

			txtAmount.add(ar, new AmountCommaSeperator());
			txtAmount.get(ar).setWidth("100%");
			txtAmount.get(ar).setStyleName("fright");
			txtAmount.get(ar).setImmediate(true);
			txtAmount.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!txtAmount.get(ar).getValue().toString().isEmpty())
					{
						double total = 0.0;
						for(int i = 0;i<txtAmount.size();i++)
						{
							if(!txtAmount.get(i).getValue().toString().isEmpty() && cmbExpencehead.get(i).getValue()!=null)
							{
								total+=Double.parseDouble(txtAmount.get(i).getValue().toString());
								table.setColumnFooter("Amount","Total: "+ new CommaSeparator().setComma(total));
								totalField.setValue(new CommaSeparator().setComma(total));
							}	
						}
					}
				}
			});

		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}

		txtRemarks.add(ar, new TextField());
		txtRemarks.get(ar).setWidth("100%");
		txtRemarks.get(ar).setImmediate(true);


		table.addItem(new Object[]{ sl.get(ar),cmbExpencehead.get(ar), txtAmount.get(ar),txtRemarks.get(ar)},ar);
	}

	private boolean doubleEntryCheck(String caption,int row)
	{
		for(int i=0;i<cmbExpencehead.size();i++)
		{
			if(i!=row && caption.equals(cmbExpencehead.get(i).getItemCaption(cmbExpencehead.get(i).getValue())))
			{
				return false;
			}
		}
		return true;
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("700px");
		setHeight("600px");

		// btnShip
		/*	btnShip = new NativeButton();
		btnShip.setCaption("");
		btnShip.setImmediate(true);
		btnShip.setWidth("28px");
		btnShip.setHeight("24px");
		btnShip.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnShip,"top:29.0px;left:340.0px;");   */

		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setValue("Debit");
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Voucher Type: "), "top:5.0px;left:10.0px;");
		mainLayout.addComponent(RadioBtnGroup, "top:3.0px; left:145.0px;");

		lblRefferenceNo = new Label();
		lblRefferenceNo.setImmediate(true);
		lblRefferenceNo.setWidth("-1px");
		lblRefferenceNo.setHeight("-1px");
		lblRefferenceNo.setValue("Refference No :");
		mainLayout.addComponent(lblRefferenceNo, "top:28.0px;left:10.0px;");

		txtrefferenceno= new TextRead();
		txtrefferenceno.setImmediate(true);
		txtrefferenceno.setWidth("210px");
		txtrefferenceno.setHeight("-1px");
		mainLayout.addComponent(txtrefferenceno, "top:26.0px;left:145.0px;");

		lbllc = new Label();
		lbllc.setImmediate(true);
		lbllc.setWidth("-1px");
		lbllc.setHeight("-1px");
		lbllc.setValue("LC No(Dr.):");
		mainLayout.addComponent(lbllc, "top:55.0px;left:10.0px;");

		cmbLc = new ComboBox();
		cmbLc.setImmediate(true);
		cmbLc.setWidth("280px");
		cmbLc.setHeight("24px");
		//cmbLc.setNullSelectionAllowed(true);
		cmbLc.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbLc, "top:53.0px;left:145.0px;");
		
		lblDrBalance = new Label();
		lblDrBalance.setImmediate(true);
		lblDrBalance.setWidth("-1px");
		lblDrBalance.setHeight("-1px");
		lblDrBalance.setValue("Debit Acc.[Balance] :");

		txtLCNoBalance = new TextRead(1);
		txtLCNoBalance.setImmediate(true);
		txtLCNoBalance.setWidth("130px");
		txtLCNoBalance.setHeight("24px");
		mainLayout.addComponent(lblDrBalance, "top:80.0px;left:10.0px;");
		mainLayout.addComponent(txtLCNoBalance, "top:78.0px;left:145.0px;");

		lblBankAccount = new Label();
		lblBankAccount.setImmediate(true);
		lblBankAccount.setWidth("-1px");
		lblBankAccount.setHeight("-1px");
		lblBankAccount.setValue("Credit Account(Cr.) :");
		mainLayout.addComponent(lblBankAccount, "top:105.0px;left:10.0px;");

		// txtVoucherNo
		cmbBankAccNo = new ComboBox();
		cmbBankAccNo.setImmediate(true);
		cmbBankAccNo.setWidth("280px");
		cmbBankAccNo.setHeight("-1px");
		//cmbBankAccNo.setNullSelectionAllowed(true);
		cmbBankAccNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbBankAccNo, "top:103.0px;left:145.0px;");

		// btnBankHead
		btnBankHead = new NativeButton();
		btnBankHead.setCaption("");
		btnBankHead.setImmediate(true);
		btnBankHead.setWidth("28px");
		btnBankHead.setHeight("24px");
		btnBankHead.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnBankHead,"top:103.0px;left:425.0px;");
		
		lblCrBalance = new Label();
		lblCrBalance.setImmediate(true);
		lblCrBalance.setWidth("-1px");
		lblCrBalance.setHeight("-1px");
		lblCrBalance.setValue("Credit Acc.[Balance] :");

		txtCreditBalance = new TextRead(1);
		txtCreditBalance.setImmediate(true);
		txtCreditBalance.setWidth("130px");
		txtCreditBalance.setHeight("24px");
		mainLayout.addComponent(lblCrBalance, "top:130.0px;left:10.0px;");
		mainLayout.addComponent(txtCreditBalance, "top:128.0px;left:145.0px;");

		lblCostCenter = new Label();
		lblCostCenter.setImmediate(true);
		lblCostCenter.setWidth("-1px");
		lblCostCenter.setHeight("-1px");
		lblCostCenter.setValue("Cost Center :");
		mainLayout.addComponent(lblCostCenter, "top:155.0px;left:10.0px;");

		// txtVoucherNo
		cmbCostCenter = new ComboBox();
		cmbCostCenter.setImmediate(true);
		cmbCostCenter.setWidth("280px");
		cmbCostCenter.setHeight("-1px");
		cmbCostCenter.setNullSelectionAllowed(true);
		cmbCostCenter.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbCostCenter, "top:153.0px;left:145.0px;");

		// btnBankHead
		btnCostCenter = new NativeButton();
		btnCostCenter.setCaption("");
		btnCostCenter.setImmediate(true);
		btnCostCenter.setWidth("28px");
		btnCostCenter.setHeight("24px");
		btnCostCenter.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnCostCenter,"top:153.0px;left:425.0px;");



		// lblBalance
		lblDate = new Label();
		lblDate.setImmediate(true);
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");
		lblDate.setValue("Date :");
		mainLayout.addComponent(lblDate, "top:30.0px;left:470.0px;");

		date = new DateField();
		date.setValue(new java.util.Date());
		date.setWidth("110px");
		date.setResolution(PopupDateField.RESOLUTION_DAY);
		date.setDateFormat("dd-MM-yy");
		date.setInvalidAllowed(false);
		date.setImmediate(true);
		mainLayout.addComponent(date, "top:28.0px;left:550.0px;");

		// lblVoucherNo
		lblVoucherNo = new Label();
		lblVoucherNo.setImmediate(true);
		lblVoucherNo.setWidth("-1px");
		lblVoucherNo.setHeight("-1px");
		lblVoucherNo.setValue("Voucher No :");
		mainLayout.addComponent(lblVoucherNo, "top:55.0px;left:470.0px;");

		// txtVoucherNo
		txtvoucherno = new TextRead();
		txtvoucherno.setImmediate(true);
		txtvoucherno.setWidth("110px");
		txtvoucherno.setHeight("21px");
		mainLayout.addComponent(txtvoucherno, "top:53.0px;left:550.0px;");

		lblDescription = new Label();
		lblDescription.setImmediate(true);
		lblDescription.setWidth("-1px");
		lblDescription.setHeight("-1px");
		lblDescription.setValue("Description :");
		mainLayout.addComponent(lblDescription, "top:460.0px;left:35.0px;");

		cmbDescription = new ComboBox();
		cmbDescription.setImmediate(true);
		cmbDescription.setNewItemsAllowed(true);
		cmbDescription.setWidth("540px");
		cmbDescription.setHeight("-1px");
		mainLayout.addComponent(cmbDescription, "top:455.0px;left:120.0px;");
		cmbDescription.addItem("Amount Paid To against ");

		mainLayout.addComponent(jvUpload, "top:80.0px;left:450.0px;");

		// btnPreview
		btnPreview = new Button("Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:105.0px;left:560.0px;");

		table.setFooterVisible(true);
		table.setWidth("675px");
		table.setHeight("255px");
		table.setColumnCollapsingAllowed(true);
		table.setColumnFooter("Amount: ", "");

		table.addContainerProperty("SL", Label.class, new Label());
		table.setColumnWidth("SL", 20);

		table.addContainerProperty("Expense Head", ComboBox.class, new ComboBox());
		table.setColumnWidth("Expense Head", 280);

		table.addContainerProperty("Amount", AmountCommaSeperator.class, new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		table.setColumnWidth("Amount", 130);

		table.addContainerProperty("Remarks", TextField.class, new TextField());
		table.setColumnWidth("Remarks", 180);

		table.setColumnFooter("Amount", "Total: 0.0");


		tableInitialise();

		mainLayout.addComponent(table, "top:190.0px;left:10.0px;");

		mainLayout.addComponent(cButton, "top:515.0px;left:10.0px;");

		return mainLayout;
	}
}