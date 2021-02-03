package acc.appform.transaction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommaSeparator;
import com.common.share.ImmediateUploadExample;
import com.common.share.MessageBox;
import com.common.share.SessionBean;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.BaseTheme;

@SuppressWarnings("serial")
public class RcecivedAgnstInvoice extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Table table = new Table();

	private TextField txtChallanNoFind = new TextField();

	private static final List<String> voucherType = Arrays.asList(new String[] {"BANK","CASH"});
	public OptionGroup ogVoucherType = new OptionGroup("",voucherType);

	private static final List<String> type = Arrays.asList(new String[] {"Cheque","Online Debit / Credit"});
	public OptionGroup ogType = new OptionGroup("",type);

	private Label lblCashCheque;
	private Label lblCommon;

	private TextRead txtVoucherNo;
	private PopupDateField dVoucherDate;

	private ComboBox cmbPartyName;
	private ComboBox cmbBankName;
	private ComboBox cmbBranchName;
	//private ComboBox cmbNarration;

	private Label lblChequeNo;
	private TextField txtChequeNo;
	private Label lblChequeDate;
	private PopupDateField dChequeDate;

	private ComboBox cmbDepositeAccNo;

	private TextRead txtGTotalInvoice;
	private TextRead txtGTotalPaid;
	private TextRead txtGTotalBalance;
	private TextRead txtGTotalAmount;
	private TextRead txtGTotalTaxAmount;
	private TextRead txtGNetAmount;

	private PopupDateField dFromInvoice;
	private PopupDateField dToInvoice;
	private CheckBox chkAllInvoice;

	private AmountCommaSeperator txtTotalRecTax;

	//Table Value
	private ArrayList<Label> tbLblSel = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChk = new ArrayList<CheckBox>();
	private ArrayList<Label> tbLblInvoiceNo = new ArrayList<Label>();
	private ArrayList<PopupDateField> tbInvoiceDate = new ArrayList<PopupDateField>();
	private ArrayList<TextRead> tbTxtInvoiceAmount = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbTxtPaidAmount = new ArrayList<TextRead>(1);
	private ArrayList<AmountCommaSeperator> tbTxtAmount = new ArrayList<AmountCommaSeperator>();
	private ArrayList<AmountCommaSeperator> tbTxtTaxAmount = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextRead> tbTxtBalanceAmount = new ArrayList<TextRead>(1);
	private ArrayList<TextRead> tbTxtNetAmount = new ArrayList<TextRead>(1);
	private ArrayList<Label> tblblStatus = new ArrayList<Label>();
	private ArrayList<Component> allComp = new ArrayList<Component>();

	private CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "", "", "Exit");
	private NativeButton btnFind = new NativeButton("Find");

	private boolean tick = false;
	private boolean isUpdate = false;
	private boolean isFind = false;
	private boolean isRefresh = false;

	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

	private ImmediateUploadExample bpvChequeUpload = new ImmediateUploadExample("");
	private String bpvChequePdf = null;
	private String filePathChequeTmp = "";

	private Button btnChequePreview;

	private String imageChequeLoc = "0" ;

	private CommaSeparator cm = new CommaSeparator();

	public RcecivedAgnstInvoice(SessionBean sessionBean,String formName)
	{
		this.sessionBean = sessionBean;
		this.setResizable(false);
		this.setWidth("1090px");
		this.setHeight("655px");

		this.setCaption("RECEIVED AGAINST INVOICE :: " + sessionBean.getCompany());

		buildMainLayout();		
		setContent(mainLayout);

		tableinitialise();

		componentIni(true);
		btnIni(true);

		setEventAction();
		focusEnter();

		authenticationCheck();

		cmbAccountHeadData();
		cmbAllData();

		button.btnNew.focus();
	}

	private void authenticationCheck()
	{
		if(!sessionBean.isSubmitable())
		{button.btnSave.setVisible(false);}
		if(!sessionBean.isUpdateable())
		{button.btnEdit.setVisible(false);}
		if(!sessionBean.isDeleteable())
		{button.btnDelete.setVisible(false);}
	}

	public void setEventAction()
	{
		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isFind = false;
				isRefresh = false;

				componentIni(false);
				btnIni(false);
				txtClear();

				//txtVoucherNo.setValue("CR-BK-"+selectVoucherNo());
				cmbPartyName.focus();
				txtChallanNoFind.setValue("");
			}
		});

		button.btnSave.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(ogVoucherType.getValue().toString().equals("BANK"))
				{
					formValidationBank();
				}
				else
				{
					formValidationCash();
				}
			}
		});

		button.btnEdit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbPartyName.getValue()!=null)
				{
					isFind = false;
					isUpdate = true;
					btnIni(false);
					componentIni(false);
					ogVoucherType.setEnabled(false);
				}
				else
				{
					showNotification("Warning!","Find data to edit.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnDelete.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbPartyName.getValue()!=null)
				{
					deleteButtonEvent();
				}
				else
				{
					showNotification("Warning!","Find data to delete.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				findButtonEvent();
			}
		});

		button.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				isRefresh = true;
				componentIni(true);
				btnIni(true);
				txtClear();
				txtChallanNoFind.setValue("");
				dFromInvoice.setEnabled(false);
				dToInvoice.setEnabled(false);
			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		cmbPartyName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
					tableClear();
					calculateTax();
					ledgerRootPath(1);
				}
			}
		});

		chkAllInvoice.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllInvoice.booleanValue())
				{
					dFromInvoice.setEnabled(false);
					dToInvoice.setEnabled(false);
				}
				else
				{
					dFromInvoice.setEnabled(true);
					dToInvoice.setEnabled(true);
				}
			}
		});

		cmbDepositeAccNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbDepositeAccNo.getValue()!=null)
				{
					ledgerRootPath(2);
				}
			}
		});

		btnChequePreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{	
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathChequeTmp;
					getWindow().open(new ExternalResource(link),"_blank", 500, 200, Window.BORDER_NONE );
				}
				if(isUpdate)
				{
					if(!bpvChequeUpload.actionCheck)
					{
						if(!imageChequeLoc.equalsIgnoreCase("0"))
						{

							String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathChequeTmp;
							getWindow().open(new ExternalResource(link),"_blank", 500, 200, Window.BORDER_NONE );
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvChequeUpload.actionCheck)
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathChequeTmp;
						getWindow().open(new ExternalResource(link),"_blank", 500, 200, Window.BORDER_NONE );
					}
				}
			}
		});

		bpvChequeUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePathCheque(0,"","");
			}
		});

		ogVoucherType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(ogVoucherType.getValue().toString().equals("BANK"))
				{
					if(!isRefresh)
					{
						ogType.setEnabled(true);
						cmbBankName.setEnabled(true);
						cmbBranchName.setEnabled(true);
						txtChequeNo.setEnabled(true);
						dChequeDate.setEnabled(true);
						btnChequePreview.setEnabled(true);
						bpvChequeUpload.setEnabled(true);
					}
					//lblCashCheque.setValue("Deposite A/C Name :");
					//txtVoucherNo.setValue("CR-BK-"+selectVoucherNo());

					cmbAccountHeadData();
				}
				else if(ogVoucherType.getValue().toString().equals("CASH"))
				{
					ogType.setEnabled(false);
					cmbBankName.setEnabled(false);
					cmbBankName.setValue(null);
					cmbBranchName.setEnabled(false);

					txtChequeNo.setEnabled(false);
					txtChequeNo.setValue("");
					dChequeDate.setEnabled(false);
					btnChequePreview.setEnabled(false);
					bpvChequeUpload.setEnabled(false);

					bpvChequeUpload.fileName = "";
					bpvChequeUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
					filePathChequeTmp = "";
					bpvChequeUpload.actionCheck = false;
					imageChequeLoc = "0";

					//lblCashCheque.setValue("Cash A/C Name :");
					//txtVoucherNo.setValue("CR-CH-"+selectVoucherNo());

					cmbAccountHeadData();
				}
			}
		});

		ogType.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(ogType.getValue().toString().equals("Cheque"))
				{
					lblChequeNo.setValue("Cheque No :");
					lblChequeDate.setValue("Date :");
				}
				else
				{
					lblChequeNo.setValue("Trans No :");
					lblChequeDate.setValue("Date :");
				}
			}
		});

		txtChequeNo.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!txtChequeNo.getValue().toString().equals(""))
				{
					if(!checkExistingCheque() && !isFind)
					{
						showNotification("Warning!","Cheque no already exist.",Notification.TYPE_WARNING_MESSAGE);
						txtChequeNo.setValue("");
					}
				}
			}
		});

		txtTotalRecTax.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(cmbPartyName.getValue()!=null)
				{
					if(Double.parseDouble("0"+txtGTotalInvoice.getValue().toString().replaceAll(",", ""))>0)
					{
						calculateTax();
					}
					else
					{
						txtTotalRecTax.setValue("");
						showNotification("Warning!","No invoice found.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					txtTotalRecTax.setValue("");
					showNotification("Warning!","Select party name.",Notification.TYPE_WARNING_MESSAGE);
					cmbPartyName.focus();
				}
			}
		});

		btnFind.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				tableClear();
				if(cmbPartyName.getValue()!=null)
				{
					selectAllInvoice();
				}
				else
				{
					showNotification("Warning!", "Select party name.", Notification.TYPE_WARNING_MESSAGE);
					cmbPartyName.focus();
				}
			}
		});

		table.addListener(new Table.HeaderClickListener() 
		{
			public void headerClick(HeaderClickEvent event) 
			{
				if(event.getPropertyId().toString().equalsIgnoreCase("SEL"))
				{
					if(tick)
					{tick = false;}
					else
					{tick = true;} 
					for(int i=0; i<tbChk.size(); i++)
					{
						if(!tbLblInvoiceNo.get(i).getValue().toString().isEmpty())
						{
							tbChk.get(i).setValue(tick);
						}
					}
				}
			}
		});
	}

	private void deleteButtonEvent()
	{
		MessageBox mb = new MessageBox(getParent(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to update information?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{						
					if(deleteData())
					{
						txtClear();
						showNotification("Delete all information successfully.");
					}
				}
			}
		});
	}

	public int selectVoucherNo()
	{
		String query = "";
		int sl = 1;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dFormat.format(dVoucherDate.getValue())+"')").list().iterator().next().toString();
			String voucher = "voucher"+fsl;
			if(ogVoucherType.getValue().equals("BANK"))
			{
				query = "SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1)  FROM "+voucher+" WHERE vouchertype in ('cba') and CompanyId = '"+ sessionBean.getCompanyId() +"'";
			}
			else
			{
				query = "SELECT ISNULL((MAX(CAST(SUBSTRING(Voucher_No,7,50) AS INT))+1),1) FROM "+voucher+" WHERE vouchertype in ('cca') and CompanyId = '"+ sessionBean.getCompanyId() +"'";
			}
			Iterator<?> iter = session.createSQLQuery(query).list().iterator();
			if(iter.hasNext())
			{
				sl = Integer.valueOf(iter.next().toString());
			}
		}
		catch(Exception ex)
		{
			showNotification("Can't find Voucher no","",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return sl;
	}

	private String imagePathCheque(int flag,String str,String fiscalYearNo)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String stuImage = null;

		if(flag==0)
		{
			if(bpvChequeUpload.fileName.trim().length()>0)
			{
				try
				{
					if(bpvChequeUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = sessionBean.getUserId()+"BPVC";
						fileMove(basePath+bpvChequeUpload.fileName.trim(),SessionBean.imagePathTmp+path+".jpg");
						bpvChequePdf = SessionBean.imagePathTmp+path+".jpg";
						filePathChequeTmp = path+".jpg";
					}
					else
					{
						String path = sessionBean.getUserId()+"BPVC";
						fileMove(basePath+bpvChequeUpload.fileName.trim(),SessionBean.imagePathTmp+path+".pdf");
						bpvChequePdf = SessionBean.imagePathTmp+path+".pdf";
						filePathChequeTmp = path+".pdf";
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return bpvChequePdf;
		}

		if(flag==1)
		{
			if(bpvChequeUpload.fileName.trim().length()>0)
			{
				try
				{
					if(bpvChequeUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvChequeUpload.fileName.trim(),SessionBean.imagePath+projectName+"/chequeBillPayment"+fiscalYearNo+"/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/chequeBillPayment"+fiscalYearNo+"/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvChequeUpload.fileName.trim(),SessionBean.imagePath+projectName+"/chequeBillPayment"+fiscalYearNo+"/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/chequeBillPayment"+fiscalYearNo+"/"+path+".pdf";
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
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
			{f1.delete();}
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

	private boolean checkExistingCheque()
	{
		boolean ret = true;
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dFormat.format(dVoucherDate.getValue())+"')").list().iterator().next().toString();
			String cheque =  "chequeDetails"+fsl;
			Iterator<?> iter = session.createSQLQuery("Select [Cheque_No] from "+cheque+" where [Cheque_No] = '"+txtChequeNo.getValue().toString().trim()+"' and companyId = '" + sessionBean.getCompanyId()+ "'").list().iterator();
			if(iter.hasNext())
			{
				ret = false;
			}
		}
		catch(Exception exp)
		{
			showNotification("Can't find cheque",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		return ret;
	}

	private void selectAllInvoice()
	{
		if(!isFind)
		{
			String sql = "";
			Session session = SessionFactoryUtil.getInstance().openSession();
			session.beginTransaction();
			try
			{
				if(!chkAllInvoice.booleanValue())
				{
					/*sql = "select vBillNo,CONVERT(date,sd.dBillDate)dDate,ISNULL((select SUM(rdi.mAmount) from tbSalesInvoiceDetails rdi"+
							" where rdi.vBillNo=sd.vBillNo),0)+(select mVatAmount from tbSalesInvoiceInfo si where si.vBillNo = sd.vBillNo)mInvAmount,ISNULL((select SUM(rdi.mTotalAmount) from tbReceivedAgainstInvoiceDetails"+
							" rdi where rdi.vInvoiceNo=sd.vBillNo),0)mPaidAmount from tbSalesInvoiceDetails sd left join tbReceivedAgainstInvoiceDetails rd on sd.vBillNo = rd.vInvoiceNo"+
							" where CONVERT(date,dBillDate) between '"+dFormat.format(dFromInvoice.getValue())+"' and '"+dFormat.format(dToInvoice.getValue())+"' and  sd.vBillNo in (select vBillNo"+
							" from tbSalesInvoiceInfo where vPartyId = '"+cmbPartyName.getValue().toString()+"') group by vBillNo,CONVERT(date,sd.dBillDate) having"+
							" ((ISNULL((select SUM(rdi.mAmount) from tbSalesInvoiceDetails rdi where rdi.vBillNo=sd.vBillNo),0))+(select mVatAmount from tbSalesInvoiceInfo si where si.vBillNo = sd.vBillNo)-(ISNULL((select SUM(rdi.mTotalAmount) from"+ 
							" tbReceivedAgainstInvoiceDetails rdi where rdi.vInvoiceNo=sd.vBillNo),0)))!=0 order by CONVERT(date,sd.dBillDate)";*/
					sql=" select vBillNo,dInvoiceDate,mNetTotal,(ReceivedAmount+ATax+Rejection+Discount)paidAmount from(  "+
							" select a.vBillNo,convert(date,a.dInvoiceDate,105)dInvoiceDate,a.mNetTotal,    "+
							" (select isnull(SUM(mTotalAmount),0) from tbReceivedAgainstInvoiceDetails where vInvoiceNo=a.vBillNo)ReceivedAmount,    "+
							" (select isnull(SUM(AdvanceTax),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)ATax,    "+
							" (select isnull(SUM(Rejection),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)Rejection,   "+ 
							" (select isnull(SUM(Discount),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)Discount     "+
							" from tbSalesInvoiceInfo a where a.vPartyId='"+cmbPartyName.getValue().toString()+"'  and  CONVERT(date,a.dInvoiceDate) " +
							" between '"+dFormat.format(dFromInvoice.getValue())+"' and '"+dFormat.format(dToInvoice.getValue())+"'  "+
							" ) a where (mNetTotal-(ReceivedAmount+ATax+Rejection+Discount))>0";
				}
				else
				{
					/*sql = "select vBillNo,CONVERT(date,sd.dBillDate)dDate,ISNULL((select SUM(rdi.mAmount) from tbSalesInvoiceDetails rdi"+
							" where rdi.vBillNo=sd.vBillNo),0)+(select mVatAmount from tbSalesInvoiceInfo si where si.vBillNo = sd.vBillNo)mInvAmount,ISNULL((select SUM(rdi.mTotalAmount) from tbReceivedAgainstInvoiceDetails"+
							" rdi where rdi.vInvoiceNo=sd.vBillNo),0)mPaidAmount from tbSalesInvoiceDetails sd left join tbReceivedAgainstInvoiceDetails rd on sd.vBillNo = rd.vInvoiceNo"+
							" where sd.vBillNo in (select vBillNo from tbSalesInvoiceInfo where vPartyId = '"+cmbPartyName.getValue().toString()+"') group by vBillNo,CONVERT(date,sd.dBillDate) having"+
							" ((ISNULL((select SUM(rdi.mAmount) from tbSalesInvoiceDetails rdi where rdi.vBillNo=sd.vBillNo),0))+(select mVatAmount from tbSalesInvoiceInfo si where si.vBillNo = sd.vBillNo)-(ISNULL((select SUM(rdi.mTotalAmount) from"+ 
							" tbReceivedAgainstInvoiceDetails rdi where rdi.vInvoiceNo=sd.vBillNo),0)))!=0 order by CONVERT(date,sd.dBillDate)";*/
					
					sql=" select vBillNo,dInvoiceDate,mNetTotal,(ReceivedAmount+ATax+Rejection+Discount)paidAmount from(  "+
							" select a.vBillNo,convert(date,a.dInvoiceDate,105)dInvoiceDate,a.mNetTotal,    "+
							" (select isnull(SUM(mTotalAmount),0) from tbReceivedAgainstInvoiceDetails where vInvoiceNo=a.vBillNo)ReceivedAmount,    "+
							" (select isnull(SUM(AdvanceTax),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)ATax,    "+
							" (select isnull(SUM(Rejection),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)Rejection,   "+ 
							" (select isnull(SUM(Discount),0) from tbJournalAgainstInvoice where vBillNo=a.vBillNo)Discount     "+
							" from tbSalesInvoiceInfo a where a.vPartyId='"+cmbPartyName.getValue().toString()+"' /* and  CONVERT(date,a.dInvoiceDate)*/ " +
							//" between '"+dFormat.format(dFromInvoice.getValue())+"' and '"+dFormat.format(dToInvoice.getValue())+"'  "+
							" ) a where (mNetTotal-(ReceivedAmount+ATax+Rejection+Discount))>0";
				}
				Iterator<?> iter = session.createSQLQuery(sql).list().iterator();
				int i = 0;
				for(;iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();

					tbLblInvoiceNo.get(i).setValue(element[0].toString());
					tbInvoiceDate.get(i).setReadOnly(false);
					tbInvoiceDate.get(i).setValue(element[1]);
					tbInvoiceDate.get(i).setReadOnly(true);

					tbTxtInvoiceAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[2].toString())));
					tbTxtPaidAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[3].toString())));
					tbTxtBalanceAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[2].toString())-Double.parseDouble(element[3].toString())));
					setStatus(i);
					if(tbLblInvoiceNo.size()-1==i)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
				if(i==0)
				{
					showNotification("Warning!","No invoice found.",Notification.TYPE_WARNING_MESSAGE);
				}
			}	
			catch(Exception exp)
			{
				showNotification("Cann't Find Invoice",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
			finally{session.close();}
		}
	}

	private void setStatus(int i)
	{
		if(Double.parseDouble("0"+tbTxtPaidAmount.get(i).getValue().toString().replaceAll(",", ""))>0)
		{
			tblblStatus.get(i).setValue("Partial");
		}
		else
		{
			tblblStatus.get(i).setValue("Unpaid");
		}
	}

	private void findButtonEvent()
	{
		Window win = new InvoiceVoucherFind(sessionBean, txtChallanNoFind,"RECEIVED AGAINST INVOICE FIND","bankCash");
		win.center();
		getParent().addWindow(win);
		win.setModal(true);
		win.setStyleName("cwindow");
		win.setCloseShortcut(KeyCode.ESCAPE);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if(txtChallanNoFind.getValue().toString().length() > 0)
				{
					txtClear();
					isFind = true;
					findInitialise(txtChallanNoFind.getValue().toString());
				}
			}
		});
	}

	private void findInitialise(String ReferenceNo)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vVoucherType,vPaymentType,ri.vVoucherNo,rd.dVoucherDate,vPartyId,vDepoLedgerId,"+
					" vBankId,vBranchId,vChequeTransNo,dChequeTransDate,vAttachFile,rd.vInvoiceNo,rd.dInvoiceDate,"+
					" ISNULL((select SUM(mAmount) from tbSalesInvoiceDetails where vBillNo = rd.vInvoiceNo),0)+(select mVatAmount from tbSalesInvoiceInfo si where si.vBillNo = rd.vInvoiceNo)mInvoiceAmount,"+
					" ISNULL((select SUM(mAmount) from tbReceivedAgainstInvoiceDetails ra where ra.vReferenceNo != '"+ReferenceNo+"' and ra.vInvoiceNo=rd.vInvoiceNo),0)mPaidAmount,"+
					" rd.mAmount,rd.mTaxAmount from tbReceivedAgainstInvoiceInfo ri inner join tbReceivedAgainstInvoiceDetails rd"+
					" on ri.vReferenceNo = rd.vReferenceNo where ri.vReferenceNo = '"+ReferenceNo+"'";
			List<?> led = session.createSQLQuery(sql).list();
			int i = 0;
			for(Iterator<?> iter = led.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				if(i==0)
				{
					ogVoucherType.setValue(element[0].toString());
					ogType.setValue(element[1].toString());
					txtVoucherNo.setValue(element[2].toString());
					dVoucherDate.setValue(element[3]);
					cmbPartyName.setValue(element[4].toString());
					cmbDepositeAccNo.setValue(element[5].toString());
					cmbBankName.setValue(element[6].toString());
					cmbBranchName.setValue(element[7].toString());
					txtChequeNo.setValue(element[8].toString());
					dChequeDate.setValue(element[9]);
					imageChequeLoc = element[10].toString();
				}
				tbLblInvoiceNo.get(i).setValue(element[11].toString());
				tbChk.get(i).setValue(true);
				tbInvoiceDate.get(i).setReadOnly(false);
				tbInvoiceDate.get(i).setValue(element[12]);
				tbInvoiceDate.get(i).setReadOnly(true);

				tbTxtInvoiceAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[13].toString())));
				tbTxtPaidAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[14].toString())));
				tbTxtBalanceAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[13].toString())-Double.parseDouble(element[14].toString())));
				tbTxtAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[15].toString())));
				tbTxtTaxAmount.get(i).setValue(cm.setComma(Double.parseDouble(element[16].toString())));
				sumAmount(i);
				i++;
				if(tbLblInvoiceNo.size()-1==i)
				{
					tableRowAdd(i+1);
				}
			}
			//calculateTax();
		}
		catch(Exception exp)
		{
			showNotification("Error1", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbAllData()
	{
		cmbPartyName.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{	
			List<?> listParty = session.createSQLQuery(" Select partyCode,partyName from tbPartyInfo where isActive='1' ORDER by partyName ").list();
			for(Iterator<?> iter = listParty.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyName.addItem(element[0].toString());
				cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());				
			}

			List<?> listBank = session.createSQLQuery("select id,bankName from tbBankName order by bankName").list();
			for(Iterator<?> iter = listBank.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBankName.addItem(element[0].toString());
				cmbBankName.setItemCaption(element[0].toString(), element[1].toString());
			}

			List<?> listBranch = session.createSQLQuery("select id,branchName from tbBankBranch order by branchName").list();
			for(Iterator<?> iter = listBranch.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbBranchName.addItem(element[0].toString());
				cmbBranchName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error2",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbAccountHeadData()
	{
		String DepositeLedger = "";
		cmbDepositeAccNo.removeAllItems();
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(ogVoucherType.getValue().toString().equals("BANK"))
			{
				DepositeLedger = "select Ledger_Id,Ledger_Name from tbLedger where substring(create_From,"
					+ " 1, ABS(CHARINDEX('G', Create_From) - 2)) in ('A8','L8') and companyId = '"+sessionBean.getCompanyId()+"' ";
			}
			else
			{
				DepositeLedger = "select Ledger_Id,Ledger_Name from tbLedger where substring(create_From,"
					+ " 1, ABS(CHARINDEX('G', Create_From) - 2)) in ('A8','L8') and companyId = '"+sessionBean.getCompanyId()+"' ";
			}

			List<?> listDeposite = session.createSQLQuery(DepositeLedger).list();
			for(Iterator<?> iter = listDeposite.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbDepositeAccNo.addItem(element[0].toString());
				cmbDepositeAccNo.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error3",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void formValidationBank()
	{
		if(cmbPartyName.getValue()!=null)
		{
			if(cmbDepositeAccNo.getValue()!=null)
			{
				if(ogType.getValue().toString().equals("Online Debit / Credit") || !txtChequeNo.getValue().toString().equals(""))
				{
					if(Double.parseDouble("0"+txtGNetAmount.getValue().toString().replaceAll(",", ""))>0)
					{
						saveButtonEvent();
					}
					else
					{
						showNotification("Warning!","There are no receive amount.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else
				{
					showNotification("Warning!","Provide "+lblChequeNo.getValue().toString().replaceAll(":", "")+"",Notification.TYPE_WARNING_MESSAGE);
					txtChequeNo.focus();
				}
			}
			else
			{
				showNotification("Warning!","Select deposite account no.",Notification.TYPE_WARNING_MESSAGE);
				cmbDepositeAccNo.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select party name.",Notification.TYPE_WARNING_MESSAGE);
			cmbPartyName.focus();
		}
	}

	private void formValidationCash()
	{
		if(cmbPartyName.getValue()!=null)
		{
			if(cmbDepositeAccNo.getValue()!=null)
			{
				if(Double.parseDouble("0"+txtGTotalAmount.getValue().toString().replaceAll(",", ""))>0)
				{
					saveButtonEvent();
				}
				else
				{
					showNotification("Warning!","There are no receive amount.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else
			{
				showNotification("Warning!","Select deposite account no.",Notification.TYPE_WARNING_MESSAGE);
				cmbDepositeAccNo.focus();
			}
		}
		else
		{
			showNotification("Warning!","Select party name.",Notification.TYPE_WARNING_MESSAGE);
			cmbPartyName.focus();
		}
	}

	private void saveButtonEvent()
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
						if(deleteData())
						{
							mb.buttonLayout.getComponent(0).setEnabled(false);
							insertData();
							btnIni(true);
							componentIni(true);
							isUpdate = false;
							isFind = false;
							dFromInvoice.setEnabled(false);
							dToInvoice.setEnabled(false);
							button.btnNew.focus();
							showNotification("All information update successfully.");
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
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
						insertData();
						btnIni(true);
						componentIni(true);
						//txtClear();
						button.btnNew.focus();
						showNotification("All information saved successfully.");
					}
				}
			});
		}
	}

	private void insertData()
	{
		String refNo = "";
		String query = "";
		String VoucherNo = "";
		String imagePathCheck = "";
		String VoucherType = "";

		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();

		String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dFormat.format(dVoucherDate.getValue())+"')").list().iterator().next().toString();
		String voucher =  "voucher"+fsl;
		String cheque =  "chequeDetails"+fsl;
		int sl = selectVoucherNo();

		query = "SELECT ISNULL((MAX(CAST(SUBSTRING(vReferenceNo,5,20) AS INT))+1),1) FROM tbReceivedAgainstInvoiceInfo";
		Iterator<?> iterRef = session.createSQLQuery(query).list().iterator();
		
		if(iterRef.hasNext())
		{
			refNo = "REF-"+iterRef.next().toString();
		}
		if(ogVoucherType.getValue().toString().equals("BANK"))
		{
			VoucherNo = "CR-BK-"+sl;
			imagePathCheck = imagePathCheque(1,VoucherNo,fsl)==null?"0":imagePathCheque(1,VoucherNo,fsl);
		}
		else
		{
			VoucherNo = "CR-CH-"+sl;
			imagePathCheck = imagePathCheque(1,VoucherNo,fsl)==null?"0":imagePathCheque(1,VoucherNo,fsl);
		}
		if(!isUpdate)
		{
			txtVoucherNo.setValue(VoucherNo);	
		}
		
		try
		{
			Iterator<?> iter = session.createSQLQuery("select vVoucherNo from tbReceivedAgainstInvoiceInfo where" +
					" vVoucherNo='"+VoucherNo+"' and dVoucherDate='"+dFormat.format(dVoucherDate.getValue())+"'").list().iterator();
			if(!iter.hasNext())
			{
				String InsertInfo = " INSERT into tbReceivedAgainstInvoiceInfo(vVoucherType,vPaymentType,vReferenceNo,vVoucherNo,"+
						" dVoucherDate,vPartyId,vPartyLedger,vPartyName,vDepoLedgerId,vDepoLedgerName,vBankId,vBankName,vBranchId,"+
						" vBranchName,vChequeTransNo,dChequeTransDate,vAttachFile,mTotalAmount,mTotalTaxAmount,mTotalAmountAfterTax,"+
						" vUserName,vUserIp,dEntryTime) values (" +
						" '"+ogVoucherType.getValue().toString()+"'," +
						" '"+ogType.getValue().toString()+"'," +
						" '"+refNo+"'," +
						" '"+VoucherNo+"'," +
						" '"+dFormat.format(dVoucherDate.getValue())+"'," +
						" '"+cmbPartyName.getValue().toString()+"'," +
						" (select ledgerCode from tbPartyInfo where partyCode = '"+cmbPartyName.getValue().toString()+"')," +
						" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"'," +
						" '"+(cmbDepositeAccNo.getValue().toString())+"'," +
						" '"+cmbDepositeAccNo.getItemCaption(cmbDepositeAccNo.getValue()).toString()+"'," +
						" '"+(cmbBankName.getValue()==null?"":(cmbBankName.getValue()).toString())+"'," +
						" '"+(cmbBankName.getValue()==null?"":cmbBankName.getItemCaption(cmbBankName.getValue()).toString())+"'," +
						" '"+(cmbBranchName.getValue()==null?"":(cmbBranchName.getValue().toString()))+"'," +
						" '"+(cmbBranchName.getValue()==null?"":cmbBranchName.getItemCaption(cmbBranchName.getValue()).toString())+"'," +
						" '"+(txtChequeNo.getValue().toString().isEmpty()?"":txtChequeNo.getValue().toString())+"'," +
						" '"+dFormat.format(dChequeDate.getValue())+"'," +
						" '"+imagePathCheck+"'," +
						" '"+(txtGTotalAmount.getValue().toString().replaceAll(",", ""))+"'," +
						" '"+(txtGTotalTaxAmount.getValue().toString().replaceAll(",", ""))+"'," +
						" '"+(txtGNetAmount.getValue().toString().replaceAll(",", ""))+"'," +
						" '"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP) ";
				session.createSQLQuery(InsertInfo).executeUpdate();

				for(int i = 0; i<tbLblInvoiceNo.size(); i++)
				{
					if(Double.parseDouble("0"+tbTxtNetAmount.get(i).getValue().toString().replaceAll(",", ""))>0)
					{
						String InsertDetails = "Insert into tbReceivedAgainstInvoiceDetails(vReferenceNo,vVoucherNo,dVoucherDate,vInvoiceNo,"+
								" dInvoiceDate, mInvoiceAmount, mAmount, mTaxAmount, mTotalAmount) values ( " +
								" '"+refNo+"'," +
								" '"+VoucherNo+"'," +
								" '"+dFormat.format(dVoucherDate.getValue())+"'," +
								" '"+tbLblInvoiceNo.get(i).getValue().toString()+"'," +
								" '"+dFormat.format(tbInvoiceDate.get(i).getValue())+"'," +
								" '"+tbTxtInvoiceAmount.get(i).getValue().toString().replaceAll(",", "")+"'," +
								" '"+tbTxtAmount.get(i).getValue().toString().replaceAll(",", "")+"'," +
								" '"+tbTxtTaxAmount.get(i).getValue().toString().replaceAll(",", "")+"'," +
								" '"+tbTxtNetAmount.get(i).getValue().toString().replaceAll(",", "")+"')";
						session.createSQLQuery(InsertDetails).executeUpdate();
					}
				}

				if(ogVoucherType.getValue().toString().equals("BANK"))
				{
					VoucherType = "cba";
					query = " insert into "+cheque+" (Cheque_No, Cheque_Date, Voucher_No, Bank_Id, BankName, BranchName, companyId, flag) " +
							" values('"+ txtChequeNo.getValue().toString() +"', '"+ dFormat.format(dChequeDate.getValue())+"'," +
							" '"+ txtVoucherNo.getValue().toString() +"', '"+ cmbDepositeAccNo.getValue().toString() +"'," +
							" '"+ (cmbBankName.getValue()==null?"":cmbBankName.getItemCaption(cmbBankName.getValue())) +"'," +
							" '"+ (cmbBranchName.getValue()==null?"":cmbBranchName.getItemCaption(cmbBranchName.getValue())) +"'," +
							" '"+ sessionBean.getCompanyId() +"','"+ogType.getValue().toString()+"')";
					session.createSQLQuery(query).executeUpdate();
				}
				else
				{
					VoucherType = "cca";
				}

				query = " insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,"+
						" costId,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,attachChequeBill,audit_by,approve_by,chqClear)VALUES(" +
						" '"+VoucherNo+"'," +
						" '"+dFormat.format(dVoucherDate.getValue())+"'," +
						" '"+cmbDepositeAccNo.getValue().toString()+"'," +
						" 'Received Against Invoice',"+
						" '"+txtGTotalAmount.getValue().toString().replaceAll(",", "")+"'," +
						" '0','"+VoucherType+"','"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"'," +
						" '','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
						" 2, '"+ sessionBean.getCompanyId() +"', '0', '"+imagePathCheck+"',"+
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserId()+"','1')";
				session.createSQLQuery(query).executeUpdate();

				//Tax Amount Add
				if(Double.parseDouble("0"+txtGTotalTaxAmount.getValue().toString().replaceAll(",", ""))>0)
				{
					query = " insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,"+
							" costId,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,attachChequeBill,audit_by,approve_by,chqClear)VALUES(" +
							" '"+VoucherNo+"'," +
							" '"+dFormat.format(dVoucherDate.getValue())+"'," +
							" 'AL429'," +
							" 'Received Against Invoice',"+
							" '"+txtGTotalTaxAmount.getValue().toString().replaceAll(",","")+"'," +
							" '0','"+VoucherType+"','"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"'," +
							" '','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" 2, '"+ sessionBean.getCompanyId() +"', '0', '"+imagePathCheck+"',"+
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserId()+"','1')";
					session.createSQLQuery(query).executeUpdate();
				}

				query = " insert into "+voucher+"(Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,vouchertype,TransactionWith,"+
						" costId,userId,userIp,entryTime,auditapproveflag,companyId,attachBill,attachChequeBill,audit_by,approve_by,chqClear)VALUES(" +
						" '"+VoucherNo+"'," +
						" '"+dFormat.format(dVoucherDate.getValue())+"'," +
						" (select ledgerCode from tbPartyInfo where partyCode = '"+cmbPartyName.getValue().toString()+"')," +
						" 'Received Against Invoice','0',"+
						" '"+txtGNetAmount.getValue().toString().replaceAll(",","")+"'," +
						" '"+VoucherType+"','"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"'," +
						" '','"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
						" 2, '"+ sessionBean.getCompanyId() +"', '0', '"+imagePathCheck+"'," +
						" '"+sessionBean.getUserId()+"','"+sessionBean.getUserId()+"','1')";
				session.createSQLQuery(query).executeUpdate();
			}
			else
			{
				showNotification("Warning!","Voucher no already exist.",Notification.TYPE_WARNING_MESSAGE);
			}
			tx.commit();
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error4",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private boolean deleteData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try
		{
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dFormat.format(dVoucherDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;

			String sql = " insert into tbDeleteUpdateVoucher select Voucher_No,Date,Ledger_Id,Narration,DrAmount,CrAmount,"+
					" vouchertype,TransactionWith,'"+sessionBean.getUserName()+"' UserName,CURRENT_TIMESTAMP DUDtime,'New' TType,costId, '"+sessionBean.getUserIp()+"' userIp, companyId from "+
					" "+voucher+" WHERE Voucher_No = '"+txtVoucherNo.getValue()+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"'";
			session.createSQLQuery(sql).executeUpdate();

			String deleteInfo = " delete from tbReceivedAgainstInvoiceInfo where vReferenceNo = '"+txtChallanNoFind.getValue().toString()+"' ";
			session.createSQLQuery(deleteInfo).executeUpdate();

			String deleteDetails = " delete from tbReceivedAgainstInvoiceDetails where vReferenceNo = '"+txtChallanNoFind.getValue().toString()+"' ";
			session.createSQLQuery(deleteDetails).executeUpdate();

			String deleteVoucher = " delete from "+voucher+" where Voucher_No = '"+txtVoucherNo.getValue().toString()+"'";
			session.createSQLQuery(deleteVoucher).executeUpdate();

			if(ogVoucherType.getValue().toString().equals("BANK"))
			{
				String deleteCheque = " delete from "+cheque+" where Voucher_No = '"+txtVoucherNo.getValue().toString()+"'";
				session.createSQLQuery(deleteCheque).executeUpdate();
			}
			tx.commit();
			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			showNotification("Error1",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{session.close();}
	}

	private void focusEnter()
	{
		allComp.add(dVoucherDate);
		allComp.add(cmbPartyName);
		allComp.add(cmbDepositeAccNo);
		allComp.add(cmbBankName);
		allComp.add(cmbBranchName);
		allComp.add(txtChequeNo);
		allComp.add(dChequeDate);
		allComp.add(txtTotalRecTax);

		allComp.add(tbTxtAmount.get(0));

		new FocusMoveByEnter(this,allComp);
	}

	private void componentIni(boolean b) 
	{	
		ogVoucherType.setEnabled(!b);
		ogType.setEnabled(!b);
		txtVoucherNo.setEnabled(!b);
		dVoucherDate.setEnabled(!b);

		dFromInvoice.setEnabled(!b);
		dToInvoice.setEnabled(!b);
		chkAllInvoice.setEnabled(!b);

		cmbPartyName.setEnabled(!b);
		cmbDepositeAccNo.setEnabled(!b);

		cmbBankName.setEnabled(!b);
		cmbBranchName.setEnabled(!b);
		txtChequeNo.setEnabled(!b);
		dChequeDate.setEnabled(!b);

		bpvChequeUpload.setEnabled(!b);
		btnChequePreview.setEnabled(!b);

		btnFind.setEnabled(!b);
		txtTotalRecTax.setEnabled(!b);

		table.setEnabled(!b);

		txtGTotalInvoice.setEnabled(!b);
		txtGTotalPaid.setEnabled(!b);
		txtGTotalTaxAmount.setEnabled(!b);
		txtGTotalAmount.setEnabled(!b);
		txtGTotalBalance.setEnabled(!b);
		txtGNetAmount.setEnabled(!b);

		if(ogVoucherType.getValue().toString().equals("CASH"))
		{
			ogType.setEnabled(false);
			cmbBankName.setEnabled(false);
			cmbBankName.setValue(null);
			cmbBranchName.setEnabled(false);

			txtChequeNo.setEnabled(false);
			txtChequeNo.setValue("");
			dChequeDate.setEnabled(false);
			btnChequePreview.setEnabled(false);
			bpvChequeUpload.setEnabled(false);

			bpvChequeUpload.fileName = "";
			bpvChequeUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
			filePathChequeTmp = "";
			bpvChequeUpload.actionCheck = false;
			imageChequeLoc = "0";

			lblCashCheque.setValue("Cash A/C Name :");
		}

		if(isUpdate)
		{
			for(int i=0; i<tbChk.size(); i++)
			{
				if(!tbLblInvoiceNo.get(i).getValue().toString().isEmpty())
				{
					tbTxtAmount.get(i).setEnabled(true);
					tbTxtTaxAmount.get(i).setEnabled(true);
				}
			}
		}
	}

	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnDelete.setEnabled(t);
		button.btnCancel.setEnabled(t);
		button.btnPreview.setEnabled(t);
		button.btnFind.setEnabled(t);
	}

	private void txtClear()
	{
		isFind = false;
		isUpdate = false;

		ogVoucherType.setValue("BANK");
		ogType.setValue("Cheque");

		cmbPartyName.setValue(null);
		cmbDepositeAccNo.setValue(null);

		cmbBankName.setValue(null);
		cmbBranchName.setValue("3");
		txtChequeNo.setValue("");
		txtTotalRecTax.setValue("");
		chkAllInvoice.setValue(false);

		bpvChequeUpload.fileName = "";
		bpvChequeUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathChequeTmp = "";
		bpvChequeUpload.actionCheck = false;
		imageChequeLoc = "0";

		tableClear();
	}

	private void tableClear()
	{
		for(int i=0;i<tbLblInvoiceNo.size();i++)
		{
			tbChk.get(i).setValue(false);
			tbLblInvoiceNo.get(i).setValue("");
			tbInvoiceDate.get(i).setReadOnly(false);
			tbInvoiceDate.get(i).setValue(null);
			tbInvoiceDate.get(i).setReadOnly(true);

			tbTxtInvoiceAmount.get(i).setValue("");
			tbTxtPaidAmount.get(i).setValue("");
			tbTxtAmount.get(i).setValue("");
			tbTxtTaxAmount.get(i).setValue("");
			tbTxtAmount.get(i).setEnabled(false);
			tbTxtTaxAmount.get(i).setEnabled(false);
			tbTxtBalanceAmount.get(i).setValue("");
			tbTxtNetAmount.get(i).setValue("");
			tblblStatus.get(i).setValue("");
		}

		txtGTotalInvoice.setValue("");
		txtGTotalPaid.setValue("");
		txtGTotalTaxAmount.setValue("");
		txtGTotalAmount.setValue("");
		txtGTotalBalance.setValue("");
		txtGNetAmount.setValue("");

		table.setColumnFooter("SELECT", "");
		table.setColumnFooter("Invoice No", "");
	}

	private AbsoluteLayout buildMainLayout() 
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		lblCommon = new Label("Voucher Type :");
		lblCommon.setImmediate(false);
		lblCommon.setWidth("-1px");
		lblCommon.setHeight("-1px");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:10.0px;");

		ogVoucherType.setImmediate(true);
		ogVoucherType.setStyleName("horizontal");
		ogVoucherType.setValue("BANK");
		mainLayout.addComponent(ogVoucherType, "top:18.0px;left:110.0px;");

		lblCommon = new Label("Payment Type :");
		mainLayout.addComponent(lblCommon, "top:45.0px; left:10.0px;");

		ogType.setImmediate(true);
		ogType.setStyleName("horizontal");
		ogType.setValue("Cheque");
		mainLayout.addComponent(ogType, "top:43.0px;left:110.0px;");

		lblCommon = new Label("Voucher No :");
		mainLayout.addComponent(lblCommon, "top:70.0px; left:10.0px;");

		txtVoucherNo = new TextRead();
		txtVoucherNo.setImmediate(true);
		txtVoucherNo.setWidth("110px");
		txtVoucherNo.setHeight("22px");
		mainLayout.addComponent(txtVoucherNo, "top:68.0px; left:110.0px;");

		lblCommon = new Label("Voucher Date :");
		mainLayout.addComponent(lblCommon, "top:95.0px; left:10.0px;");

		dVoucherDate = new PopupDateField();
		dVoucherDate.setWidth("110px");
		dVoucherDate.setDateFormat("dd-MM-yyyy");
		dVoucherDate.setValue(new java.util.Date());
		dVoucherDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dVoucherDate, "top:93.0px; left:110.0px;");

		/*lblCommon = new Label("Narration :");
		mainLayout.addComponent(lblCommon, "top:120.0px; left:10.0px;");

		cmbNarration = new ComboBox();
		cmbNarration.setWidth("300px");
		cmbNarration.setHeight("-1px");
		cmbNarration.setImmediate(true);
		cmbNarration.setNewItemsAllowed(true);
		mainLayout.addComponent(cmbNarration, "top:118.0px; left:110.0px;");*/

		lblCommon = new Label("Invoice From :");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:325.0px;");

		dFromInvoice = new PopupDateField();
		dFromInvoice.setWidth("110px");
		dFromInvoice.setImmediate(true);
		dFromInvoice.setDateFormat("dd-MM-yyyy");
		dFromInvoice.setValue(new java.util.Date());
		dFromInvoice.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dFromInvoice, "top:18.0px; left:415.0px;");

		lblCommon = new Label("To");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:525.0px;");

		dToInvoice = new PopupDateField();
		dToInvoice.setWidth("110px");
		dToInvoice.setImmediate(true);
		dToInvoice.setDateFormat("dd-MM-yyyy");
		dToInvoice.setValue(new java.util.Date());
		dToInvoice.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dToInvoice, "top:18.0px; left:541.0px;");

		chkAllInvoice = new CheckBox("All");
		chkAllInvoice.setImmediate(true);
		chkAllInvoice.setHeight("-1px");
		chkAllInvoice.setWidth("-1px");
		mainLayout.addComponent(chkAllInvoice, "top:20.0px; left:650.0px;");

		lblCommon = new Label("Party Name :");
		mainLayout.addComponent(lblCommon, "top:45.0px; left:325.0px;");

		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("270px");
		cmbPartyName.setHeight("-1px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbPartyName, "top:43.0px; left:415.0px;");

		btnFind.setIcon(new ThemeResource("../icons/Findicon.png"));
		mainLayout.addComponent(btnFind, "top:43.0px; left:688.0px;");
		btnFind.setHeight("24px");
		btnFind.setWidth("30px");
		btnFind.setDescription("Click here to find invoices.");

		lblCashCheque = new Label("Deposit A/C :");
		lblCashCheque.setImmediate(true);
		lblCashCheque.setWidth("-1px");
		lblCashCheque.setHeight("-1px");
		mainLayout.addComponent(lblCashCheque, "top:70.0px; left:325.0px;");

		cmbDepositeAccNo = new ComboBox();
		cmbDepositeAccNo.setImmediate(true);
		cmbDepositeAccNo.setWidth("270px");
		cmbDepositeAccNo.setHeight("-1px");
		cmbDepositeAccNo.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbDepositeAccNo.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbDepositeAccNo, "top:68.0px; left:415.0px;");

		lblCommon = new Label("Bank Name :");
		mainLayout.addComponent(lblCommon, "top:95.0px; left:325.0px;");

		cmbBankName = new ComboBox();
		cmbBankName.setImmediate(true);
		cmbBankName.setNewItemsAllowed(true);
		cmbBankName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbBankName.setWidth("270px");
		cmbBankName.setHeight("-1px");
		mainLayout.addComponent(cmbBankName, "top:93.0px; left:415.0px;");

		lblCommon = new Label("Branch Name :");
		mainLayout.addComponent(lblCommon, "top:20.0px; left:730.0px;");

		cmbBranchName = new ComboBox();
		cmbBranchName.setImmediate(true);
		cmbBranchName.setNewItemsAllowed(true);
		cmbBranchName.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		cmbBranchName.setWidth("260px");
		cmbBranchName.setHeight("-1px");
		mainLayout.addComponent(cmbBranchName, "top:18.0px; left:820.0px;");

		lblChequeNo = new Label("Cheque No :");
		lblChequeNo.setHeight("-1px");
		lblChequeNo.setWidth("-1px");
		mainLayout.addComponent(lblChequeNo, "top:45.0px; left:730.0px;");

		txtChequeNo = new TextField();
		txtChequeNo.setImmediate(true);
		txtChequeNo.setHeight("-1px");
		txtChequeNo.setWidth("100px");
		mainLayout.addComponent(txtChequeNo,"top:43.0px; left:820.0px;");

		lblChequeDate = new Label("Date :");
		lblChequeDate.setHeight("-1px");
		lblChequeDate.setWidth("-1px");
		mainLayout.addComponent(lblChequeDate, "top:45.0px; left:930.0px;");

		dChequeDate = new PopupDateField();
		dChequeDate.setWidth("110px");
		dChequeDate.setDateFormat("dd-MM-yyyy");
		dChequeDate.setValue(new java.util.Date());
		dChequeDate.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(dChequeDate, "top:43.0px; left:970.0px;");

		bpvChequeUpload.upload.setButtonCaption("Attach Cheque");
		bpvChequeUpload.upload.setWidth("100px");
		mainLayout.addComponent(bpvChequeUpload, "top:70.0px;left:820.0px;");

		btnChequePreview = new Button("Cheque Preview");
		btnChequePreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnChequePreview.addStyleName("icon-after-caption");
		btnChequePreview.setImmediate(true);
		btnChequePreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnChequePreview, "top:95.0px;left:930.0px;");

		lblCommon = new Label("Total Tax :");
		mainLayout.addComponent(lblCommon, "top:120.0px; left:730.0px;");

		txtTotalRecTax = new AmountCommaSeperator();
		txtTotalRecTax.setImmediate(true);
		txtTotalRecTax.setWidth("100px");
		txtTotalRecTax.setHeight("-1px");
		mainLayout.addComponent(txtTotalRecTax, "top:118.0px; left:820.0px;");

		table.setWidth("100%");
		table.setHeight("390px");
		table.setColumnCollapsingAllowed(true);
		table.setImmediate(true);

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#", 25);

		table.addContainerProperty("SEL", CheckBox.class, new CheckBox());
		table.setColumnWidth("SEL", 25);

		table.addContainerProperty("Invoice No", Label.class, new Label());
		table.setColumnWidth("Invoice No", 140);

		table.addContainerProperty("Date", PopupDateField.class, new PopupDateField());
		table.setColumnWidth("Date", 83);

		table.addContainerProperty("Invoice Amount", TextRead.class, new TextRead(1));
		table.setColumnWidth("Invoice Amount", 90);

		table.addContainerProperty("Rec. Amount", TextRead.class, new TextRead(1));
		table.setColumnWidth("Rec. Amount", 90);

		table.addContainerProperty("Balance Amount", TextRead.class, new TextRead(1));
		table.setColumnWidth("Balance Amount", 92);

		table.addContainerProperty("Paid Amount", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Paid Amount", 100);

		table.addContainerProperty("Tax Amount", AmountCommaSeperator.class, new AmountCommaSeperator());
		table.setColumnWidth("Tax Amount", 100);

		table.addContainerProperty("Net Amount", TextRead.class, new TextRead(1));
		table.setColumnWidth("Net Amount", 100);

		table.addContainerProperty("Status", Label.class, new Label());
		table.setColumnWidth("Status", 43);

		table.setColumnAlignments(new String[] {Table.ALIGN_LEFT,Table.ALIGN_CENTER,Table.ALIGN_LEFT,
				Table.ALIGN_LEFT ,Table.ALIGN_LEFT ,Table.ALIGN_LEFT,Table.ALIGN_LEFT, Table.ALIGN_LEFT,
				Table.ALIGN_CENTER, Table.ALIGN_CENTER, Table.ALIGN_CENTER});

		table.setFooterVisible(true);
		table.setLazyLoading(false);

		mainLayout.addComponent(table,"top:145.0px; left:0.0px; ");

		lblCommon = new Label("<b>Grand Total :</b>",Label.CONTENT_XHTML);
		mainLayout.addComponent(lblCommon, "top:544.0px; left:200.0px;");

		txtGTotalInvoice = new TextRead(1);
		txtGTotalInvoice.setImmediate(true);
		txtGTotalInvoice.setHeight("22px");
		txtGTotalInvoice.setWidth("100px");
		mainLayout.addComponent(txtGTotalInvoice,"top:545.0px; left:328.0px;");//299

		txtGTotalPaid = new TextRead(1);
		txtGTotalPaid.setImmediate(true);
		txtGTotalPaid.setHeight("22px");
		txtGTotalPaid.setWidth("100px");
		mainLayout.addComponent(txtGTotalPaid,"top:545.0px; left:431.0px;");

		txtGTotalBalance = new TextRead(1);
		txtGTotalBalance.setImmediate(true);
		txtGTotalBalance.setHeight("22px");
		txtGTotalBalance.setWidth("100px");
		mainLayout.addComponent(txtGTotalBalance,"top:545.0px; left:534.0px;");

		txtGTotalAmount = new TextRead(1);
		txtGTotalAmount.setImmediate(true);
		txtGTotalAmount.setHeight("22px");
		txtGTotalAmount.setWidth("110px");
		mainLayout.addComponent(txtGTotalAmount,"top:545.0px; left:638.0px;");

		txtGTotalTaxAmount = new TextRead(1);
		txtGTotalTaxAmount.setImmediate(true);
		txtGTotalTaxAmount.setHeight("22px");
		txtGTotalTaxAmount.setWidth("110px");
		mainLayout.addComponent(txtGTotalTaxAmount,"top:545.0px; left:752.0px;");

		txtGNetAmount = new TextRead(1);
		txtGNetAmount.setImmediate(true);
		txtGNetAmount.setHeight("22px");
		txtGNetAmount.setWidth("110px");
		mainLayout.addComponent(txtGNetAmount,"top:545.0px; left:865.0px;");

		mainLayout.addComponent(button,"top:580.0px; left:200.0px");

		return mainLayout;
	}

	private void tableinitialise()
	{
		for(int i=0; i<10; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd(final int ar)
	{
		tbLblSel.add(ar,new Label());
		tbLblSel.get(ar).setWidth("100%");
		tbLblSel.get(ar).setValue(ar+1);

		tbChk.add(ar,new CheckBox());
		tbChk.get(ar).setWidth("100%");
		tbChk.get(ar).setHeight("23px");
		tbChk.get(ar).setImmediate(true);
		tbChk.get(ar).setDescription("Select this row.");
		tbChk.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(!tbLblInvoiceNo.get(ar).getValue().toString().equals("") && tbChk.get(ar).booleanValue())
				{
					tbTxtAmount.get(ar).setValue(cm.setComma(Double.parseDouble
							("0"+tbTxtBalanceAmount.get(ar).getValue().toString().replaceAll(",", ""))));
					tbTxtAmount.get(ar).setEnabled(true);
					tbTxtTaxAmount.get(ar).setEnabled(true);
				}
				else
				{
					tbTxtAmount.get(ar).setEnabled(false);
					tbTxtTaxAmount.get(ar).setEnabled(false);
					tbTxtAmount.get(ar).setValue("");
					tbTxtTaxAmount.get(ar).setValue("");
					tbTxtNetAmount.get(ar).setValue("");
					tbChk.get(ar).setValue(false);
					setStatus(ar);
				}
			}
		});

		tbLblInvoiceNo.add(ar,new Label());
		tbLblInvoiceNo.get(ar).setWidth("100%");

		tbInvoiceDate.add(ar,new PopupDateField());
		tbInvoiceDate.get(ar).setWidth("100%");
		tbInvoiceDate.get(ar).setDateFormat("dd-MM-yyyy");
		tbInvoiceDate.get(ar).setReadOnly(true);

		tbTxtInvoiceAmount.add(ar,new TextRead(1));
		tbTxtInvoiceAmount.get(ar).setWidth("100%");
		tbTxtInvoiceAmount.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				sumAmount(ar);
				calculateTax();
			}
		});

		tbTxtPaidAmount.add(ar, new TextRead(1));
		tbTxtPaidAmount.get(ar).setWidth("100%");
		tbTxtPaidAmount.get(ar).setImmediate(true);

		tbTxtBalanceAmount.add(ar, new TextRead(1));
		tbTxtBalanceAmount.get(ar).setWidth("100%");
		tbTxtBalanceAmount.get(ar).setImmediate(true);

		tbTxtAmount.add(ar, new AmountCommaSeperator());
		tbTxtAmount.get(ar).setWidth("100%");
		tbTxtAmount.get(ar).setImmediate(true);
		tbTxtAmount.get(ar).setEnabled(false);
		tbTxtAmount.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				sumAmount(ar);
				calculateTax();
			}
		});

		tbTxtTaxAmount.add(ar, new AmountCommaSeperator());
		tbTxtTaxAmount.get(ar).setWidth("100%");
		tbTxtTaxAmount.get(ar).setImmediate(true);
		tbTxtTaxAmount.get(ar).setEnabled(false);
		tbTxtTaxAmount.get(ar).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				sumAmount(ar);
				tbChk.get(ar+1).focus();
			}
		});

		tbTxtNetAmount.add(ar, new TextRead(1));
		tbTxtNetAmount.get(ar).setWidth("100%");
		tbTxtNetAmount.get(ar).setImmediate(true);

		tblblStatus.add(ar, new Label());
		tblblStatus.get(ar).setWidth("100%");
		tblblStatus.get(ar).setImmediate(true);

		table.addItem(new Object[]{tbLblSel.get(ar),tbChk.get(ar),tbLblInvoiceNo.get(ar),tbInvoiceDate.get(ar),
				tbTxtInvoiceAmount.get(ar),tbTxtPaidAmount.get(ar),tbTxtBalanceAmount.get(ar),tbTxtAmount.get(ar),
				tbTxtTaxAmount.get(ar),tbTxtNetAmount.get(ar),tblblStatus.get(ar)},ar);
	}

	private void sumAmount(final int i)
	{
		int invoiceNumber = 0;
		double gInvoiceAmnt = 0;
		double gPaidAmnt = 0;
		double gPreAmnt = 0;
		double gTaxAmnt = 0;
		double gBalanceAmnt = 0;
		double gNetAmnt = 0;

		for(int j=0; j<tbLblInvoiceNo.size(); j++)
		{
			if(!tbLblInvoiceNo.get(j).getValue().toString().equals("") && tbChk.get(j).booleanValue())
			{
				gInvoiceAmnt = gInvoiceAmnt + Double.parseDouble("0"+tbTxtInvoiceAmount.get(j).getValue().toString().replaceAll(",", ""));
				gPaidAmnt = gPaidAmnt + Double.parseDouble("0"+tbTxtPaidAmount.get(j).getValue().toString().replaceAll(",", ""));
				gPreAmnt = gPreAmnt + Double.parseDouble("0"+tbTxtAmount.get(j).getValue().toString().replaceAll(",", ""));
				gTaxAmnt = gTaxAmnt + Double.parseDouble("0"+tbTxtTaxAmount.get(j).getValue().toString().replaceAll(",", ""));
				gBalanceAmnt = gBalanceAmnt + Double.parseDouble("0"+tbTxtBalanceAmount.get(j).getValue().toString().replaceAll(",", ""));
				tbTxtNetAmount.get(j).setValue(cm.setComma(Double.parseDouble("0"+tbTxtAmount.get(j).getValue().toString().replaceAll(",", ""))+
						Double.parseDouble("0"+tbTxtTaxAmount.get(j).getValue().toString().replaceAll(",", ""))));
				gNetAmnt = gNetAmnt + Double.parseDouble("0"+tbTxtNetAmount.get(j).getValue().toString().replaceAll(",", ""));
				invoiceNumber++;
			}
		}
		txtGTotalInvoice.setValue(cm.setComma(gInvoiceAmnt));
		txtGTotalPaid.setValue(cm.setComma(gPaidAmnt));
		txtGTotalAmount.setValue(cm.setComma(gPreAmnt));
		txtGTotalTaxAmount.setValue(cm.setComma(gTaxAmnt));
		txtGTotalBalance.setValue(cm.setComma(gBalanceAmnt));
		txtGNetAmount.setValue(cm.setComma(gNetAmnt));
		table.setColumnFooter("Invoice No", "Total Invoice= "+invoiceNumber);

		if(Double.parseDouble("0"+tbTxtNetAmount.get(i).getValue().toString().replaceAll(",", ""))==0)
		{
			tblblStatus.get(i).setValue("Unpaid");
		}
		else if(Double.parseDouble("0"+tbTxtBalanceAmount.get(i).getValue().toString().replaceAll(",", ""))==
				Double.parseDouble("0"+tbTxtNetAmount.get(i).getValue().toString().replaceAll(",", "")))
		{
			tblblStatus.get(i).setValue("Paid");
		}
		else if(Double.parseDouble("0"+tbTxtBalanceAmount.get(i).getValue().toString().replaceAll(",", ""))>
		Double.parseDouble("0"+tbTxtNetAmount.get(i).getValue().toString().replaceAll(",", "")))
		{
			tblblStatus.get(i).setValue("Partial");
		}

		if(Double.parseDouble("0"+tbTxtNetAmount.get(i).getValue().toString().replaceAll(",", ""))>0 && !isFind)
		{
			if(Double.parseDouble("0"+tbTxtNetAmount.get(i).getValue().toString().replaceAll(",", ""))>
			Double.parseDouble("0"+tbTxtBalanceAmount.get(i).getValue().toString().replaceAll(",", "")))
			{
				tbTxtNetAmount.get(i).setValue("");
				tbTxtAmount.get(i).setValue("");
				tbTxtTaxAmount.get(i).setValue("");
				showNotification("Warning!","Exceeded invoice amount.",Notification.TYPE_WARNING_MESSAGE);
				tbTxtAmount.get(i).focus();
			}
		}
	}

	private void calculateTax()
	{
		for(int i=0; i<tbLblInvoiceNo.size(); i++)
		{
			if(!tbLblInvoiceNo.get(i).getValue().toString().isEmpty() && 
					Double.parseDouble("0"+tbTxtAmount.get(i).getValue().toString().replaceFirst(",", ""))>0)
			{
				if(Double.parseDouble("0"+txtTotalRecTax.getValue().toString().replaceAll(",", ""))>0)
				{
					double InvidiualAmount = Double.parseDouble("0"+tbTxtAmount.get(i).getValue().toString().replaceAll(",", ""));
					double TotalTax = Double.parseDouble("0"+txtTotalRecTax.getValue().toString().replaceAll(",", ""));
					double TotalAmount = Double.parseDouble("0"+txtGTotalAmount.getValue().toString().replaceAll(",", ""));
					tbTxtTaxAmount.get(i).setValue(cm.setComma(((InvidiualAmount*TotalTax)/TotalAmount)<=0?0:(InvidiualAmount*TotalTax)/TotalAmount));
				}
			}
		}
	}

	public void ledgerRootPath(int action)
	{
		String LedgerHead = "";
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			if(action==1 && cmbPartyName.getValue()!=null)
			{
				String getLedgerId = " select ledgerCode from tbPartyInfo where partyCode = '"+cmbPartyName.getValue().toString()+"' ";				

				List <?> list = session.createSQLQuery(getLedgerId).list();
				if(!list.isEmpty())
				{
					Iterator<?> iter = list.iterator();
					if(iter.hasNext())
					{
						LedgerHead = iter.next().toString();
					}
				}
			}
			else if(action==2 && cmbDepositeAccNo.getValue()!=null)
			{
				LedgerHead = cmbDepositeAccNo.getValue().toString();
			}

			String query = "select ISNULL(dbo.funLedgerRootPath ('"+ LedgerHead +"', '"+ sessionBean.getCompanyId() +"'),'')";
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
			{
				Iterator<?> iter = lst.iterator();
				if(iter.hasNext())
				{
					showNotification("Root Path:", iter.next().toString(), Notification.TYPE_TRAY_NOTIFICATION);
				}
			}
		}
		catch(Exception ex)
		{
			showNotification("Warning :", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		finally{session.close();}
	}
}