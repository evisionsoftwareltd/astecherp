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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

public class LcChargeEntryMulti extends Window {
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","Delete", "Refresh", "Find", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;
	Label lblAccHead=new Label("Debit Head: ");
	TextRead txtReferenceNo,txtVoucherNo,txtBalance;
	PopupDateField dVoucherDate;
	ComboBox cmbAccHead,cmbCostCenter,cmbDescription;
	NativeButton btnAccHead,btnCostCenter;
	Button btnPreview;
	private ImmediateUploadExample jvUpload = new ImmediateUploadExample("");
	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"Debit","Credit"});
	private TextRead totalField = new TextRead(1);
	private TextRead totalFieldAcc = new TextRead(1);
	private NumberFormat frmt = new DecimalFormat("#0.00");
	private CommaSeparator cms = new CommaSeparator();
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	
	private Table tableExpenses = new Table();
	private ArrayList<Label> tbslExp = new ArrayList<Label>();
	public ArrayList<ComboBox> tbcmbExpenceheadExp = new ArrayList<ComboBox>();
	private ArrayList<AmountCommaSeperator> tbtxtAmountExp = new ArrayList<AmountCommaSeperator>();
	private ArrayList<TextField> tbtxtRemarksExp = new ArrayList<TextField>();
	
	
	private Table tableAcc = new Table();
	private ArrayList<Label> tbslAcc = new ArrayList<Label>();
	public ArrayList<ComboBox> tbcmbAccHead = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbTxtBalAcc = new ArrayList<TextRead>();
	private ArrayList<AmountCommaSeperator> tbtxtaAmountAcca = new ArrayList<AmountCommaSeperator>();
	
	private TextField txtFromFindWindow= new TextField();
	private boolean isUpdate = false;
	private boolean isFind= false;
	String jvPdf = null;
	String filePathTmp = "";
	String imageLoc = "0";
	String transactionNoglb="";
	String referenceglb="";
	private TextRead txtref=new TextRead();
	
	
	public LcChargeEntryMulti(SessionBean sessionBean){
		this.sessionBean = sessionBean;
		this.setCaption("L/C Charge :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		authencationCheck();
		componentIni(true);
		btnIni(true);
		btnAction();
		focusEnter();
		debitHeadIniDebit();
		creditHeadIniDebit();
		cmbCostCenterLoadData();
	}
	private void tablerClearAcc(){
		for(int a=0;a<tbcmbAccHead.size();a++){
			tbcmbAccHead.get(a).setValue(null);
			tbTxtBalAcc.get(a).setValue("0");
			tbtxtaAmountAcca.get(a).setValue("");
		}
	}
	private void tableClearExp()
	{
		for(int i=0;i<tbcmbExpenceheadExp.size();i++)
		{
			tbcmbExpenceheadExp.get(i).setValue(null);
			tbtxtaAmountAcca.get(i).setValue("");
			tbtxtRemarksExp.get(i).setValue("");
		}
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
				txtBalance.setValue(0);
			}
			else
			{
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(dVoucherDate.getValue())+"')").list().iterator().next().toString();
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
				txtBalance.setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void creditHeadIniDebit() {
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery(" SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A4','A8', 'L7','L8','L10') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"' ) ORDER BY ledger_Name ").list();

			for(int a=0;a<tbcmbAccHead.size();a++){
				tbcmbAccHead.get(a).removeAllItems();
				for (Iterator<?> iter = bh.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					tbcmbAccHead.get(a).addItem(element[0].toString());
					tbcmbAccHead.get(a).setItemCaption(element[0].toString(), element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	private void creditHeadIniCredit() {
		cmbAccHead.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery(" SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A4','A8', 'L7','L8','L10') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"' ) ORDER BY ledger_Name ").list();

			
				for (Iterator<?> iter = bh.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					cmbAccHead.addItem(element[0].toString());
					cmbAccHead.setItemCaption(element[0].toString(), element[1].toString());
				}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
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
	private void debitHeadIniDebit() {
		cmbAccHead.removeAllItems();
		Iterator<?>iter=dbService(" SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A4','A8', 'L7','L8') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"' ) ORDER BY ledger_Name ");
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbAccHead.addItem(element[0]);
			cmbAccHead.setItemCaption(element[0],element[1].toString());
		}
	}
	private void debitHeadIniCredit() {
		//cmbAccHead.removeAllItems();
		
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List<?> bh = session.createSQLQuery(" SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A4','A8', 'L7','L8') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"' ) ORDER BY ledger_Name ").list();

			for(int a=0;a<tbcmbAccHead.size();a++){
				tbcmbAccHead.get(a).removeAllItems();
				for (Iterator<?> iter = bh.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					tbcmbAccHead.get(a).addItem(element[0].toString());
					tbcmbAccHead.get(a).setItemCaption(element[0].toString(), element[1].toString());
				}
			}
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
		/*Iterator<?>iter=dbService(" SELECT ledger_Id,ledger_Name FROM TbLedger WHERE substring(create_From,1, ABS(CHARINDEX('G', dbo.tbLedger.Create_From) - 2)) in ('A4','A8', 'L7') AND companyId in ('0', '"+ sessionBean.getCompanyId() +"' ) ORDER BY ledger_Name ");
		for(int a=0;a<tbcmbAccHead.size();a++){
			tbcmbAccHead.get(a).removeAllItems();
			while(iter.hasNext()){
				Object element[]=(Object[])iter.next();
				tbcmbAccHead.get(a).addItem(element[0]);
				tbcmbAccHead.get(a).setItemCaption(element[0],element[1].toString());
			}
		}*/
	}
	private void btnAction() {
		RadioBtnGroup.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Debit")){
					//tablerClearAcc();
					tableAcc.setColumnHeader("Credit Amount", "Credit Amount");
					lblAccHead.setValue("Debit Head: ");
					//debitHeadIniDebit();
					//creditHeadIniDebit();
				}
				
				else{
					//tablerClearAcc();
					tableAcc.setColumnHeader("Credit Amount", "Debit Amount");
					lblAccHead.setValue("Credit Head: ");
					//debitHeadIniCredit();
					//creditHeadIniCredit();
				}
			}
		});
		cmbAccHead.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
				headSelect(cmbAccHead.getValue()== null?"x":cmbAccHead.getValue().toString(),-1);
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
				tableClearExp();
				tablerClearAcc();
				isUpdate = false;
			}
		});
		cButton.btnDelete.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(!txtVoucherNo.getValue().toString().isEmpty()){
					deleteButtonEvent();
				}
				else{
					showNotification("Nothing To Delete",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnFind.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isFind = true;
				//findButtonEvent();
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
					showNotification("Sorry!!","LC No: "+cmbAccHead.getItemCaption(cmbAccHead.getValue())+" is already Closed",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnSave.addListener( new Button.ClickListener() 
		{			
			public void buttonClick(ClickEvent event)
			{
				formValidation();
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
	}
	private void findButtonEvent()
	{
		Window win=new LcChrageFind(sessionBean,txtFromFindWindow,"shift",txtref);
		win.addListener(new Window.CloseListener()
		{
			public void windowClose(CloseEvent e)
			{
				if(txtFromFindWindow.getValue().toString().length()>0)
				{
					txtClear();
					findInitialise(txtFromFindWindow);
				}
			}
		});
		this.getParent().addWindow(win);
	}
	private void findInitialise(Object voucherId) 
	{		
		String sql = null;
		Session session = SessionFactoryUtil.getInstance().openSession();
		Transaction tx = session.beginTransaction();
		try 
		{			
			sql = "select referenceNo,ledgerIdLc,ledgerIdBank,date,voucherNo,costid,transactionNo,description," +
					"(select distinct voucherType from tbLcChargeDetailsVoucher where voucherNo='"+voucherId+"') " +
							"from tbLcChargeInfo where voucherNo='"+voucherId+"' ";
			List<?> led = session.createSQLQuery(sql).list();
			if(led.iterator().hasNext()){
				Object element[]=(Object[])led.iterator().next();
				if(element[8].toString().equalsIgnoreCase("Debit")){
					RadioBtnGroup.setValue("Debit");
				}
				else if(element[8].toString().equalsIgnoreCase("Credit")){
					RadioBtnGroup.setValue("Credit");
				}
				txtReferenceNo.setValue(element[0]);
				cmbAccHead.setValue(element[1].toString());
				//cmbBankAccNo.setValue(element[2]);
				dVoucherDate.setValue(element[3]);
				txtVoucherNo.setValue(element[4]);
				cmbCostCenter.setValue(element[5]);
				transactionNoglb=element[6].toString();
				referenceglb=element[0].toString();
				cmbDescription.setValue(element[7]);

				
			}

			int i = 0;
			String sqlDetails="  select headid,amount,remarks from tbLcChargeDetails where voucherNo='"+voucherId+"'";
			List<?> led1 = session.createSQLQuery(sqlDetails).list();
			for (Iterator<?> iter = led1.iterator();iter.hasNext();)
			{
				Object[] element1 = (Object[]) iter.next();
				if(tbcmbExpenceheadExp.size()-1==i)
				{
					tableRowAdd(i+1);
				}
				tbcmbExpenceheadExp.get(i).setValue(element1[0].toString());
				tbtxtAmountExp.get(i).setValue(new CommaSeparator().setComma(Double.parseDouble(element1[1].toString())));
				tbtxtRemarksExp.get(i).setValue(element1[2].toString());
				i++;
			}
			int a=0;
			String sqlDetailsVoucher="  select ledgerId,amount from tbLcChargeDetailsVoucher where voucherNo='"+voucherId+"'";
			List<?> ledVoucher = session.createSQLQuery(sqlDetailsVoucher).list();
			for (Iterator<?> iter = ledVoucher.iterator();iter.hasNext();)
			{
				Object[] element1 = (Object[]) iter.next();
				if(tbcmbAccHead.size()-1==a)
				{
					tableRowAddAcc(a+1);
				}
				tbcmbAccHead.get(a).setValue(element1[0].toString());
				tbtxtaAmountAcca.get(a).setValue(new CommaSeparator().setComma(Double.parseDouble(element1[1].toString())));
				
				a++;
			}
		}
		catch (Exception exp)
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			tx.rollback();
		}
		finally{session.close();}
	}
	private String getVoucher()
	{
		String voucher =  "voucher"+selectVoucher();	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		String invoice = session.createSQLQuery(" Select cast(isnull(max(cast(replace(Voucher_No, 'JV-NO-', '')as int))+1, 1)as varchar) from "+voucher+" where Voucher_No like 'JV-NO-%' ").list().iterator().next().toString();

		return invoice;
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
			txtVoucherNo.setValue(voucherNo);
		}

		if(isUpdate)
		{
			voucherNo=txtVoucherNo.getValue().toString();	
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
					"values('"+transactionNo+"','"+RefferenceNo+"','"+cmbAccHead.getValue().toString()+"'," +
					"'"+cmbAccHead.getItemCaption(cmbAccHead.getValue())+"','',''," +
					"'"+dtfYMD.format(dVoucherDate.getValue())+"','"+voucherNo+"','"+imagePath+"','"+cmbDescription.getValue()+"'," +
					"'"+totalFieldAcc.getValue()+"','"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'"+cmbCostCenter.getValue()+"')";
			System.out.println("Insert : "+query);
			session.createSQLQuery(query).executeUpdate();

			for(int i=0; i<tbcmbExpenceheadExp.size(); i++)
			{
				if((tbcmbExpenceheadExp.get(i).getValue()!=null) && (!tbtxtaAmountAcca.get(i).getValue().toString().isEmpty()))
				{
					String sql="insert into tbLcChargeDetails (transactionNo,referenceNo,voucherNo,headId,headName,amount,remarks)" +
							"values('"+transactionNo+"','"+RefferenceNo+"','"+voucherNo+"'," +
							"'"+tbcmbExpenceheadExp.get(i).getValue()+"','"+tbcmbExpenceheadExp.get(i).getItemCaption(tbcmbExpenceheadExp.get(i).getValue())+"'," +
							"'"+tbtxtAmountExp.get(i).getValue()+"','"+tbtxtRemarksExp.get(i).getValue()+"')";

					session.createSQLQuery(sql).executeUpdate();				
				}
			}
			String LCNo="",debit="0.0",credit="0.0";
			if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Debit")){
				LCNo=cmbAccHead.getItemCaption(cmbAccHead.getValue());
				debit=totalField.getValue().toString().replace(",", "");
			}
			else if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Credit")){
				//LCNo=tbcmbAccHead.get(0).getItemCaption(tbcmbAccHead.get(0).getValue());
				LCNo=cmbAccHead.getItemCaption(cmbAccHead.getValue());
				credit=totalField.getValue().toString().replace(",", "");
			}
			String lcLedger = " INSERT into "+voucher+" values(" +
					" '"+voucherNo+"'," +
					" '"+dtfYMD.format(dVoucherDate.getValue())+"'," +
					" '"+cmbAccHead.getValue()+"'," +//" 'Bank'," +//
					" '"+Naration+"'," +
					" '"+debit+"'," +
					" '"+credit+"'," +
					" 'jau'," +
					" '"+LCNo+"'," +
					" ''," +
					" '1'," +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
					" '2'," +
					" '"+sessionBean.getCompanyId()+"',"+
					" '"+imagePath+"', "+
					" '0'," +
					" ''," +
					" '','','')";

			session.createSQLQuery(lcLedger).executeUpdate();
			
			for(int a=0;a<tbcmbAccHead.size();a++){
				if(tbcmbAccHead.get(a).getValue()!=null&&Double.parseDouble(tbtxtaAmountAcca.get(a).getValue().toString())>0){
					
					String LC="",drAmount="0.0",crAmount="0.0",amount="0.0";
					amount=tbtxtaAmountAcca.get(a).getValue().toString();
					
					if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Debit")){
						LCNo=cmbAccHead.getItemCaption(cmbAccHead.getValue());
						//
						crAmount=tbtxtaAmountAcca.get(a).getValue().toString();
					}
					else if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Credit")){
						//LCNo=tbcmbAccHead.get(0).getItemCaption(tbcmbAccHead.get(0).getValue());
						LCNo=cmbAccHead.getItemCaption(cmbAccHead.getValue());
						drAmount=tbtxtaAmountAcca.get(a).getValue().toString();
					}
					
					String bankLedger = " INSERT into "+voucher+" values(" +
							" '"+voucherNo+"'," +
							" '"+dtfYMD.format(dVoucherDate.getValue())+"'," +
							" '"+tbcmbAccHead.get(a).getValue()+"'," +
							" '"+Naration+"'," +						
							" '"+drAmount+"'," +
							" '"+crAmount+"'," +
							" 'jau'," +
							" '"+LCNo+"'," +
							" ' '," +
							" '1'," +
							" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
							" '2'," +
							" '"+sessionBean.getCompanyId()+"',"+
							" '"+imagePath+"', "+
							" '0'," +
							" ''," +
							" '','','')";

					session.createSQLQuery(bankLedger).executeUpdate();
					
					String sqlVoucher="insert into tbLcChargeDetailsVoucher (transactionNo,referenceNo,voucherNo,ledgerId,ledgerName," +
							"voucherType,amount,remarks)" +
							"values('"+transactionNo+"','"+RefferenceNo+"','"+voucherNo+"'," +
							"'"+tbcmbAccHead.get(a).getValue()+"','"+tbcmbAccHead.get(a).getItemCaption(tbcmbAccHead.get(a).getValue())+"'," +
							"'"+RadioBtnGroup.getValue().toString()+"','"+amount+"','"+tbtxtRemarksExp.get(a).getValue()+"')";

					session.createSQLQuery(sqlVoucher).executeUpdate();
				}
			}

			

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
	private void formValidation()
	{
		if(cmbAccHead.getValue()!=null)
		{
			if(cmbCostCenter.getValue()!=null)
			{
				//if(!txtvoucherno.getValue().toString().isEmpty())
				//{
				if(tbcmbAccHead.get(0).getValue()!=null&&Double.parseDouble(tbtxtaAmountAcca.get(0).getValue().toString())>0)
				{
					if(tbcmbExpenceheadExp.get(0).getValue()!=null&&Double.parseDouble(tbtxtAmountExp.get(0).getValue().toString())>0)
					{
						System.out.println("Expense Total: "+totalField.getValue().toString().replace(",", ""));
						System.out.println("Ledger Total: "+totalFieldAcc.getValue().toString().replace(",", ""));
						if(Double.parseDouble("0"+totalField.getValue().toString().replace(",", ""))==Double.parseDouble("0"+totalFieldAcc.getValue().toString().replace(",", ""))){
							if(isClosed()){
								saveBtnAction();
							}
							else{
								showNotification("Sorry!!","LC No: "+cmbAccHead.getItemCaption(cmbAccHead.getValue())+" is already Closed",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else{
							showNotification("Warning","ledger Total and Expense Total Not matched", Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else
					{
						showNotification("Warning","Please  enter amount", Notification.TYPE_WARNING_MESSAGE);
						tbcmbExpenceheadExp.get(0).focus();
					}
				}
				else
				{
					showNotification("Warning","Please  Provide Ledger Data", Notification.TYPE_WARNING_MESSAGE);
					tbcmbAccHead.get(0).focus();
				}
				/*}
				else
				{
					showNotification("Warning","Please enter Voucher No ", Notification.TYPE_WARNING_MESSAGE);
				}*/
			}
			else
			{
				showNotification("Warning","Please select Cost Center ", Notification.TYPE_WARNING_MESSAGE);
				cmbCostCenter.focus();
			}
		}
		else
		{
			showNotification("Warning","Please select Acc. Head ", Notification.TYPE_WARNING_MESSAGE);
			cmbAccHead.focus();
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
	private boolean isClosed(){
		
		/*String LCNo="",ledgerId="";
		if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Debit")){
			LCNo=cmbAccHead.getItemCaption(cmbAccHead.getValue());
			ledgerId=cmbAccHead.getValue().toString();
		}
		else if(RadioBtnGroup.getValue().toString().equalsIgnoreCase("Credit")){
			//LCNo=tbcmbAccHead.get(0).getItemCaption(tbcmbAccHead.get(0).getValue());
			LCNo=cmbAccHead.getItemCaption(cmbAccHead.getValue());
			//ledgerId=tbcmbAccHead.get(0).getValue().toString();
		}*/

		Iterator<?>iter=dbService("select * from tbLcOpeningInfo where vLedgerID='"+cmbAccHead.getValue().toString()+"' " +
				"and vLcNo='"+cmbAccHead.getItemCaption(cmbAccHead.getValue())+"' and isActive!=0");
		if(iter.hasNext()){
			return true;
		}

		return false;
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
	private String selectVoucher()
	{
		Transaction tx = null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();
		String voucher = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(dVoucherDate.getValue())+"') as voucher").list().iterator().next().toString();

		return voucher;
	}
	private boolean deleteData()
	{
		Transaction tx=null;
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		tx = session.beginTransaction();

		String voucher =  "voucher"+selectVoucher();

		try
		{
			String delFormSql = " delete from tbLcChargeInfo where voucherNo='"+txtVoucherNo.getValue().toString()+"' ";
			String delTableSql = " delete from tbLcChargeDetails where voucherNo='"+txtVoucherNo.getValue().toString()+"' ";
			String delTableSqlVoucher = " delete from tbLcChargeDetailsVoucher where voucherNo='"+txtVoucherNo.getValue().toString()+"' ";
			String delVoucherSql = " delete from "+voucher+" where Voucher_No='"+txtVoucherNo.getValue().toString()+"' ";

			session.createSQLQuery(delFormSql).executeUpdate();
			session.createSQLQuery(delTableSql).executeUpdate();
			session.createSQLQuery(delTableSqlVoucher).executeUpdate();
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
	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}
	private void componentIni(boolean b) 
	{
		txtReferenceNo.setEnabled(!b);
		cmbAccHead.setEnabled(!b);
		txtBalance.setEnabled(!b);
		dVoucherDate.setEnabled(!b);
		txtVoucherNo.setEnabled(!b);
		jvUpload.setEnabled(!b);
		btnPreview.setEnabled(!b);
		tableAcc.setEnabled(!b);
		tableExpenses.setEnabled(!b);
		cmbDescription.setEnabled(!b);
		btnAccHead.setEnabled(!b);
		cmbCostCenter.setEnabled(!b);
		btnCostCenter.setEnabled(!b);
	}

	private void txtClear()
	{
		txtReferenceNo.setValue("");
		cmbAccHead.setValue(null);
		cmbCostCenter.setValue(null);
		dVoucherDate.setValue(new java.util.Date());
		txtVoucherNo.setValue("");
		cmbDescription.setValue(null);
		jvUpload.fileName = "";
		jvUpload.status.setValue(new Label("<font size=1px>(.pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		jvUpload.actionCheck = false;
		imageLoc = "0";

		for(int i=0;i<tbcmbExpenceheadExp.size();i++)
		{
			tbcmbExpenceheadExp.get(i).setValue(null);
			tbtxtAmountExp.get(i).setValue("");
			tbtxtRemarksExp.get(i).setValue("");
		}
		tablerClearAcc();
		tableExpenses.setColumnFooter("Amount","Total: 0.0");
		totalField.setValue("");
		tableAcc.setColumnFooter("Credit Amount","Total: 0.0");
		totalFieldAcc.setValue("");
		isUpdate=false;
		
	}
	private void focusEnter()
	{
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(cmbAccHead);
		allComp.add(cmbCostCenter);
		allComp.add(dVoucherDate);
		//allComp.add(txtvoucherno);

		for(int i=0;i<tbcmbAccHead.size();i++)
		{
			allComp.add(tbcmbAccHead.get(i));
			allComp.add(tbtxtaAmountAcca.get(i));	
		}
		for(int i=0;i<tbcmbExpenceheadExp.size();i++){
			allComp.add(tbcmbExpenceheadExp.get(i));
			allComp.add(tbtxtAmountExp.get(i));	
			allComp.add(tbtxtRemarksExp.get(i));
		}
		allComp.add(cmbDescription);
		allComp.add(cButton.btnSave);

		new FocusMoveByEnter(this,allComp);
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
		String sql="select CONVERT(varchar,YEAR(GETDATE()))+'-'+CONVERT(varchar,MONTH(GETDATE()))+'-'+CAST(ISNULL(MAX(CAST(substring(referenceNo,CHARINDEX('-',referenceNo,6)+1,LEN(referenceNo)) as int) ),0)+1  as varchar) from tbLCChargeInfo";
		Iterator<?>iter=dbService(sql);
		if(iter.hasNext()){
			return iter.next().toString();
		}
		return "";
	}
	private void newBtnAction()
	{
		btnIni(false);
		componentIni(false);
		txtClear();
		isUpdate = false;
		txtReferenceNo.setValue(getRefferenceNo());
	}
	private void previewBtnAction()
	{
		if(!txtVoucherNo.getValue().toString().isEmpty())
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(dVoucherDate.getValue())+"')").list().iterator().next().toString();
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

			String sql = "SELECT * FROM vwJournalVoucher WHERE Voucher_No in('"+txtVoucherNo.getValue().toString()+"') And companyId = '"+ sessionBean.getCompanyId() +"' and company_Id = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";

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
	

	private void btnIni(boolean t) 
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
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
	private void tableInitialiseAcc()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		//List<?> list = session.createSQLQuery("SELECT ledger_Id,ledger_Name FROM TbLedger WHERE replace(substring(create_From,1,3),'-','')!='A1' AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY ledger_Name").list();
		//List<?> costCenter = session.createSQLQuery("select id,costCentreName from tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"'  order by costCentreName").list();
		for(int i=0;i<10;i++)
		{
			tableRowAddAcc(i);	
		}
	}
	private void debitActionInit(final int ar)
	{
		try
		{
			tbtxtaAmountAcca.get(ar).addListener(new ValueChangeListener() 
			{
				public void valueChange(ValueChangeEvent event) 
				{
					double totalAmt=0.0;
					for(int i=0;i<tbtxtaAmountAcca.size();i++)
					{
						if(!tbtxtaAmountAcca.get(i).getValue().toString().trim().isEmpty())
						{
							totalAmt += i == ar ? event.getProperty().toString().trim().isEmpty()? 0:Double.parseDouble(event.getProperty().toString().trim().replace(",", "")): tbtxtaAmountAcca.get(i).getValue().toString().trim().isEmpty()?0:Double.parseDouble(tbtxtaAmountAcca.get(i).getValue().toString().replace(",", ""));
						}
					}
					tableAcc.setColumnFooter("Debit", cms.setComma(Double.parseDouble(frmt.format(totalAmt))));
					tbcmbAccHead.get(ar + 1).focus();
				}
			});
		}
		catch(Exception ex)
		{
			showNotification("Warning ", ex.toString(), Notification.TYPE_WARNING_MESSAGE);
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
					tbTxtBalAcc.get(t).setValue(0);
				}
				else
				{
					txtBalance.setValue(0);
				}
			}
			else
			{
				String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(dVoucherDate.getValue())+"')").list().iterator().next().toString();
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
					tbTxtBalAcc.get(t).setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
				}
				else
				{
					txtBalance.setValue(cms.setComma(Double.valueOf(frmt.format(bal + opBal))));
				}

			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	/*private void voucherType(int i)
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
	}*/
	private boolean sameHeadAllowed()
	{
		boolean ret = true;
		for(int i=0; i<tbcmbAccHead.size(); i++)
		{
			if(tbcmbAccHead.get(i).getValue()!=null)
			{
				if(tbcmbAccHead.get(i).getValue().toString().equals(cmbAccHead.getValue().toString()))
				{
					ret = false;
					break;
				}
			}
		}

		return ret;
	}
	private void tableRowAddAcc(final int ar)
	{	
		try
		{
			tbslAcc.add(ar, new Label(""));
			tbslAcc.get(ar).setWidth("100%");
			tbslAcc.get(ar).setHeight("15px");
			tbslAcc.get(ar).setValue(ar+1);
			
			tbcmbAccHead.add(ar,new ComboBox());
			tbcmbAccHead.get(ar).setWidth("100%");
			tbcmbAccHead.get(ar).setImmediate(true);
			tbcmbAccHead.get(ar).removeAllItems();
			tbcmbAccHead.get(ar).setNullSelectionAllowed(true);
			tbcmbAccHead.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


			/*for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				tbcmbAccHead.get(ar).addItem(element[0].toString());
				tbcmbAccHead.get(ar).setItemCaption(element[0].toString(), element[1].toString());
			}*/

			tbcmbAccHead.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event) 
				{
					try
					{
						if(tbcmbAccHead.get(ar).getValue()!=null)
						{
							for(int i=0;i<ar;i++)
							{
								if(i!=ar)
								{	
									String a_head = tbcmbAccHead.get(ar).getItemCaption(tbcmbAccHead.get(ar).getValue()).toString();
									String b_head = tbcmbAccHead.get(i).getItemCaption(tbcmbAccHead.get(i).getValue()).toString();

									if(a_head.equalsIgnoreCase(b_head))
									{
										showNotification("Warning","Duplicate Ledger Name",Notification.TYPE_WARNING_MESSAGE);
										tbcmbAccHead.get(ar).setValue(null);
										break;
									}
								}
							}

							/*StringTokenizer st = new StringTokenizer(tbcmbAccHead.get(ar).getValue() == null?"x#"+ar:event.getProperty().toString(),"#");
							String str = st.nextToken();
							int r = Integer.valueOf(st.nextToken());*/
							headSelect(tbcmbAccHead.get(ar).getValue().toString(),ar);

							int temp = tbTxtBalAcc.size();

							debitActionInit(ar);
							if((ar+1)==tbTxtBalAcc.size())
							{
								tableRowAddAcc(temp);
							}
							tbtxtaAmountAcca.get(ar).focus();
						}
					}
					catch(Exception exp){}
				}
			});


			tbTxtBalAcc.add(ar, new TextRead());
			tbTxtBalAcc.get(ar).setWidth("100%");
			tbTxtBalAcc.get(ar).setStyleName("fright");
			tbTxtBalAcc.get(ar).setValue(0);
			tbTxtBalAcc.get(ar).setEnabled(false);
			
			tbtxtaAmountAcca.add(ar, new AmountCommaSeperator());
			tbtxtaAmountAcca.get(ar).setWidth("100%");
			tbtxtaAmountAcca.get(ar).setStyleName("fright");
			tbtxtaAmountAcca.get(ar).setImmediate(true);	
			
			tbtxtaAmountAcca.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!tbtxtaAmountAcca.get(ar).getValue().toString().isEmpty())
					{
						double total = 0.0;
						for(int i = 0;i<tbtxtaAmountAcca.size();i++)
						{
							if(!tbtxtaAmountAcca.get(i).getValue().toString().isEmpty() && tbcmbAccHead.get(i).getValue()!=null)
							{
								total+=Double.parseDouble(tbtxtaAmountAcca.get(i).getValue().toString());
								tableAcc.setColumnFooter("Credit Amount","Total: "+ new CommaSeparator().setComma(total));
								totalFieldAcc.setValue(new CommaSeparator().setComma(total));
							}	
						}
					}
				}
			});
			
			tableAcc.setColumnAlignment("Debit", Table.ALIGN_RIGHT);
			tableAcc.addItem(new Object[]{tbslAcc.get(ar),tbcmbAccHead.get(ar), tbTxtBalAcc.get(ar),tbtxtaAmountAcca.get(ar)},ar);
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private AbsoluteLayout buildMainLayout() {
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(false);

		setWidth("730px");
		setHeight("700px");
		
		
		
		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setValue("Debit");
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(new Label("Voucher Type: "), "top:20.0px;left:25.0px;");
		mainLayout.addComponent(RadioBtnGroup, "top:18.0px; left:125.0px;");
		
		cmbAccHead = new ComboBox();
		cmbAccHead.setImmediate(true);
		cmbAccHead.setWidth("280px");
		cmbAccHead.setHeight("23px");
		cmbAccHead.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(lblAccHead, "top:45.0px;left:25.0px;");
		mainLayout.addComponent(cmbAccHead, "top:43.0px;left:125.0px;");
		
		btnAccHead = new NativeButton();
		btnAccHead.setCaption("");
		btnAccHead.setImmediate(true);
		btnAccHead.setWidth("28px");
		btnAccHead.setHeight("24px");
		btnAccHead.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnAccHead,"top:43.0px;left:410.0px;");
		
		txtBalance= new TextRead(1);
		txtBalance.setImmediate(true);
		txtBalance.setWidth("150px");
		txtBalance.setHeight("23px");
		mainLayout.addComponent(new Label("Balance: "), "top:70.0px;left:25.0px;");
		mainLayout.addComponent(txtBalance, "top:68.0px;left:125.0px;");
		
		cmbCostCenter = new ComboBox();
		cmbCostCenter.setImmediate(true);
		cmbCostCenter.setWidth("280px");
		cmbCostCenter.setHeight("23px");
		mainLayout.addComponent(new Label("Cost Center: "), "top:95.0px;left:25.0px;");
		mainLayout.addComponent(cmbCostCenter, "top:93.0px;left:125.0px;");
		
		btnCostCenter = new NativeButton();
		btnCostCenter.setCaption("");
		btnCostCenter.setImmediate(true);
		btnCostCenter.setWidth("28px");
		btnCostCenter.setHeight("24px");
		btnCostCenter.setIcon(new ThemeResource("../icons/add.png"));
		mainLayout.addComponent(btnCostCenter,"top:93.0px;left:410.0px;");
		
		dVoucherDate = new PopupDateField();
		dVoucherDate.setValue(new java.util.Date());
		dVoucherDate.setWidth("110px");
		dVoucherDate.setHeight("23px");
		dVoucherDate.setResolution(PopupDateField.RESOLUTION_DAY);
		dVoucherDate.setDateFormat("dd-MM-yy");
		dVoucherDate.setInvalidAllowed(false);
		dVoucherDate.setImmediate(true);
		mainLayout.addComponent(new Label("Date: "), "top:20.0px;left:450.0px;");
		mainLayout.addComponent(dVoucherDate, "top:18.0px; left:550.0px;");
		
		txtVoucherNo= new TextRead();
		txtVoucherNo.setImmediate(true);
		txtVoucherNo.setWidth("150px");
		txtVoucherNo.setHeight("23px");
		mainLayout.addComponent(new Label("Voucher No: "), "top:45.0px;left:450.0px;");
		mainLayout.addComponent(txtVoucherNo, "top:43.0px;left:550.0px;");
		
		txtReferenceNo= new TextRead();
		txtReferenceNo.setImmediate(true);
		txtReferenceNo.setWidth("150px");
		txtReferenceNo.setHeight("23px");
		mainLayout.addComponent(new Label("Reference No: "), "top:70.0px;left:450.0px;");
		mainLayout.addComponent(txtReferenceNo, "top:68.0px;left:550.0px;");

		mainLayout.addComponent(jvUpload, "top:95.0px;left:450.0px;");

		// btnPreview
		btnPreview = new Button("Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:95.0px;left:590.0px;");
		
		
		tableAcc.setFooterVisible(true);
		tableAcc.setWidth("620px");
		tableAcc.setHeight("200px");
		tableAcc.setFooterVisible(false);
		tableAcc.addContainerProperty("SL", Label.class, new Label());
		tableAcc.setColumnWidth("SL", 20);
		tableAcc.addContainerProperty("Ledger Name", ComboBox.class, new ComboBox());
		tableAcc.setColumnWidth("Ledger Name", 290);
		tableAcc.setColumnFooter("Ac. Head", "Total :");
		tableAcc.addContainerProperty("Balance", TextRead.class, new TextRead());
		tableAcc.setColumnWidth("Balance", 110);
		tableAcc.addContainerProperty("Credit Amount", TextField.class, new TextField(),null,null,Table.ALIGN_CENTER);
		tableAcc.setColumnWidth("Credit Amount", 110);
		tableAcc.setFooterVisible(true);
		tableAcc.setColumnFooter("Credit Amount", "Total: 0.0");

		tableInitialiseAcc();
		
		mainLayout.addComponent(tableAcc, "top:145.0px;left:20.0px;");
		
		tableExpenses.setFooterVisible(true);
		tableExpenses.setWidth("685px");
		tableExpenses.setHeight("200px");
		tableExpenses.setColumnCollapsingAllowed(true);
		tableExpenses.setColumnFooter("Amount: ", "");

		tableExpenses.addContainerProperty("SL", Label.class, new Label());
		tableExpenses.setColumnWidth("SL", 20);

		tableExpenses.addContainerProperty("Expense Head", ComboBox.class, new ComboBox());
		tableExpenses.setColumnWidth("Expense Head", 280);

		tableExpenses.addContainerProperty("Amount", AmountCommaSeperator.class, new AmountCommaSeperator(),null,null,Table.ALIGN_CENTER);
		tableExpenses.setColumnWidth("Amount", 130);

		tableExpenses.addContainerProperty("Remarks", TextField.class, new TextField());
		tableExpenses.setColumnWidth("Remarks", 180);

		tableExpenses.setColumnFooter("Amount", "Total: 0.0");


		tableInitialise();
		mainLayout.addComponent(tableExpenses, "top:360.0px;left:20.0px;");
		
		cmbDescription = new ComboBox();
		cmbDescription.setImmediate(true);
		cmbDescription.setNewItemsAllowed(true);
		cmbDescription.setWidth("540px");
		cmbDescription.setHeight("-1px");
		cmbDescription.addItem("Amount Paid To against ");
		mainLayout.addComponent(new Label("Description: "), "top:570.0px;left:25.0px;");
		mainLayout.addComponent(cmbDescription, "top:567.0px;left:125.0px;");
		
		mainLayout.addComponent(cButton, "top:600.0px;left:25.0px;");
		
		return mainLayout;
	}
	private void tableInitialise()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		for(int i=0;i<10;i++)
		{
			tableRowAdd(i);	
		}
	}
	private boolean doubleEntryCheck(String caption,int row)
	{
		for(int i=0;i<tbcmbExpenceheadExp.size();i++)
		{
			if(i!=row && caption.equals(tbcmbExpenceheadExp.get(i).getItemCaption(tbcmbExpenceheadExp.get(i).getValue())))
			{
				return false;
			}
		}
		return true;
	}
	private void tableRowAdd(final int ar)
	{	
		try
		{
			tbslExp.add(ar, new Label(""));
			tbslExp.get(ar).setWidth("100%");
			tbslExp.get(ar).setHeight("15px");
			tbslExp.get(ar).setValue(ar+1);

			tbcmbExpenceheadExp.add(ar, new ComboBox());
			tbcmbExpenceheadExp.get(ar).setWidth("100%");
			tbcmbExpenceheadExp.get(ar).setImmediate(true);
			tbcmbExpenceheadExp.get(ar).removeAllItems();
			tbcmbExpenceheadExp.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
			tbcmbExpenceheadExp.get(ar).setNullSelectionAllowed(true);


			try
			{	
				Session session = SessionFactoryUtil.getInstance().openSession();
				session.beginTransaction();
				List<?> lst = session.createSQLQuery("select headId,headName from tbLCHeadInfo where isActive=1 order by headName").list();
				for(Iterator<?> iter = lst.iterator(); iter.hasNext();) 
				{
					Object[] element = (Object[]) iter.next();
					tbcmbExpenceheadExp.get(ar).addItem(element[0].toString() );	
					tbcmbExpenceheadExp.get(ar).setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception ex)
			{

			}

			tbcmbExpenceheadExp.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if (tbcmbExpenceheadExp.get(ar).getValue()!=null)
					{
						boolean fla = (doubleEntryCheck(tbcmbExpenceheadExp.get(ar).getItemCaption(tbcmbExpenceheadExp.get(ar).getValue()), ar));

						if ((Object) tbcmbExpenceheadExp.get(ar).getValue() != null && fla)
						{
							try
							{

								if (ar==tbcmbExpenceheadExp.size()-1)
								{
									tableRowAdd(tbcmbExpenceheadExp.size());
									//focusEnter();
								}
								tbtxtAmountExp.get(ar).focus();
							}
							catch(Exception exp)
							{
								showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
							}
						}

						else 
						{
							Object checkNull = (Object) tbcmbExpenceheadExp.get(ar).getItemCaption(tbcmbExpenceheadExp.get(ar).getValue());

							if(!checkNull.equals("")) 
							{
								tbcmbExpenceheadExp.get(ar).setValue("x#" + ar);

								getParent().showNotification("Warning :","Same Product Name Is Not Applicable.",Notification.TYPE_WARNING_MESSAGE);
								tbcmbExpenceheadExp.get(ar).setValue(null);
							}
						}
					}
				}
			});

			tbtxtAmountExp.add(ar, new AmountCommaSeperator());
			tbtxtAmountExp.get(ar).setWidth("100%");
			tbtxtAmountExp.get(ar).setStyleName("fright");
			tbtxtAmountExp.get(ar).setImmediate(true);
			tbtxtAmountExp.get(ar).addListener(new ValueChangeListener()
			{
				public void valueChange(ValueChangeEvent event)
				{
					if(!tbtxtAmountExp.get(ar).getValue().toString().isEmpty())
					{
						double total = 0.0;
						for(int i = 0;i<tbtxtAmountExp.size();i++)
						{
							if(!tbtxtAmountExp.get(i).getValue().toString().isEmpty() && tbcmbExpenceheadExp.get(i).getValue()!=null)
							{
								total+=Double.parseDouble(tbtxtAmountExp.get(i).getValue().toString());
								tableExpenses.setColumnFooter("Amount","Total: "+ new CommaSeparator().setComma(total));
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

		tbtxtRemarksExp.add(ar, new TextField());
		tbtxtRemarksExp.get(ar).setWidth("100%");
		tbtxtRemarksExp.get(ar).setImmediate(true);


		tableExpenses.addItem(new Object[]{ tbslExp.get(ar),tbcmbExpenceheadExp.get(ar), tbtxtAmountExp.get(ar),tbtxtRemarksExp.get(ar)},ar);
	}
}
