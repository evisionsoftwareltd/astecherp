package acc.reportmodule.voucher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportPdf;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;
import database.hibernate.TbFiscalYear;

public class Voucher extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "Find", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private VerticalLayout vL = new VerticalLayout();
	private VerticalLayout vR = new VerticalLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	private Panel vList = new Panel("Voucher List");
	private ArrayList<CheckBox>vNo = new ArrayList<CheckBox>();
	private NativeSelect voucherType = new NativeSelect("Voucher Type:");
	private ComboBox costCentre = new ComboBox("Cost Centre:");
	private DateField fromDate = new DateField("From Date:");
	private DateField toDate = new DateField("To Date:");
	private NativeButton allBtn = new NativeButton("Select All");
	private String lcw = "200px";
	private String lw = "100px";
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private boolean isAllSelect = false;

	public Voucher(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("460px");
		this.setCaption("VOUCHER :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		voucherType.setWidth(lcw);
		voucherType.setImmediate(true);
		voucherType.addItem("dca");
		voucherType.setItemCaption("dca", "Cash Payment Voucher");
		voucherType.addItem("cca','cci");
		voucherType.setItemCaption("cca','cci", "Cash Receipt Voucher");
		voucherType.addItem("dba");
		voucherType.setItemCaption("dba", "Bank Payment Voucher");
		voucherType.addItem("cba','cbi");
		voucherType.setItemCaption("cba','cbi", "Bank Receipt Voucher");
		voucherType.addItem("jau','jcv");
		voucherType.setItemCaption("jau','jcv", "Journal Voucher");
		voucherType.addItem("pao");
		voucherType.setItemCaption("pao", "FixedAsset Voucher");
		voucherType.addItem("daj");
		voucherType.setItemCaption("daj", "Depriciation Voucher");

		voucherType.setValue("dca");
		voucherType.setNullSelectionAllowed(false);
		/*
		voucherType.addItem("2");
		voucherType.setItemCaption("2", "FixedAsset Voucher");
		voucherType.addItem("3");
		voucherType.setItemCaption("3", "Deprication Voucher");
		 */


		fromDate.setWidth(lw);
		fromDate.setValue(sessionBean.getFiscalOpenDate());
		fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
		fromDate.setDateFormat("dd-MM-yy");
		fromDate.setInvalidAllowed(false);
		fromDate.setImmediate(true);

		toDate.setWidth(lw);
		toDate.setValue(new java.util.Date());
		toDate.setResolution(PopupDateField.RESOLUTION_DAY);
		toDate.setDateFormat("dd-MM-yy");
		toDate.setInvalidAllowed(false);
		toDate.setImmediate(true);

		btnL.addComponent(button.btnPreview);
		btnL.addComponent(button.btnExit);

		vL.addComponent(voucherType);
		vL.addComponent(costCentre);
		costCentre.setWidth(lcw);
		vL.addComponent(fromDate);
		vL.addComponent(toDate);
		vL.addComponent(button.btnFind);
		vL.setSpacing(true);

		vR.addComponent(vList);
		vList.setWidth("150px");
		vList.setHeight("220px");
		allBtn.setStyleName(Button.STYLE_LINK);
		vR.addComponent(allBtn);
		vR.addComponent(btnL);
		btnL.setSpacing(true);
		vR.setSpacing(true);

		HorizontalLayout sp = new HorizontalLayout();
		sp.setWidth("45px");
		horLayout.addComponent(vL);
		horLayout.addComponent(sp);
		horLayout.addComponent(vR);
		this.addComponent(horLayout);
		buttonActionAdd();
		costCenterInitialise();
		Component comp[] = {voucherType, costCentre, fromDate, toDate, button.btnFind, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		voucherType.focus();
	}

	private void buttonActionAdd()
	{
		button.btnFind.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				allBtn.setCaption("Select All");
				isAllSelect = false;
				if (chkDate())
				findBtnAction();
			}
		});

		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				preBtnAction();
			}
		});
		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		allBtn.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				allBtnAction();
			}
		});
	}

	private void findBtnAction()
	{

//		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
//				&&
//				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
//		{
			if(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue()))<=	Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue())))
			{
				findInitialise();
			}
			else
			{
				showNotification("","From date can not be greater than to date.",Notification.TYPE_WARNING_MESSAGE);
			}
//		}
//		else
//		{
//			showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
//		}
	}

	private void findInitialise()
	{
		String sql = "";
		try
		{
			vList.removeAllComponents();
			vNo.clear();
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			String cheque =  "chequeDetails"+fsl;
			
			if(voucherType.getValue().toString().equalsIgnoreCase("cba"))
				sql = "SELECT distinct(voucher_No),cast(substring(voucher_No,7,50) as int) FROM VwChequeRegister WHERE vouchertype = 'cba' "+
				" AND date >= '"+dtfYMD.format(fromDate.getValue())+"' AND date <= '"+dtfYMD.format(toDate.getValue())+
				"' AND mrNo = 0 AND clearanceDate is null and AuditApproveFlag = 2 AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') order by cast(substring(voucher_No,7,50) as int)";
//			sql = "SELECT distinct(voucher_No),cast(substring(voucher_No,7,50) as int) FROM VwChequeRegister WHERE vouchertype = 'cba' "+
//			" AND date >= '"+dtfYMD.format(fromDate.getValue())+"' AND date <= '"+dtfYMD.format(toDate.getValue())+
//			"' AND mrNo = 0 AND isnull(clearanceDate,'1900-01-01') != '1900-01-01' and AuditApproveFlag = 2 AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') order by cast(substring(voucher_No,7,50) as int)";
			else
				sql = "SELECT distinct(voucher_No),CAST(substring(voucher_No,7,50) AS int) FROM "+voucher+" "+
				"WHERE date >= '"+dtfYMD.format(fromDate.getValue())+"' AND date <= '"+dtfYMD.format(toDate.getValue())+
				"'AND vouchertype in ('"+voucherType.getValue()+"') and AuditApproveFlag = 2 AND companyId in ('0', '"+ sessionBean.getCompanyId() +"') ORDER BY CAST(substring(voucher_No,7,50) AS int)";

			//System.out.println(sql);
			Iterator iter = session.createSQLQuery(sql).list().iterator();
			int i = 0;
			for(i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				vNo.add(i,new CheckBox());
				vNo.get(i).setCaption(element[0].toString());
				vList.addComponent(vNo.get(i));
			}
			if(i==0)
				showNotification("In given criteria there is no voucher.", Notification.TYPE_WARNING_MESSAGE);
		}
		catch(Exception exp)
		{
			getParent().showNotification("Error ",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void preBtnAction()
	{
		String con = "";
		boolean isVSelect = false;
		int tsl = 0;
		for(int i=0;i<vNo.size();i++)
		{
			if(Boolean.valueOf(vNo.get(i).getValue().toString()))
			{	
				if(tsl==0)
				{
					isVSelect = true;
					con = vNo.get(i).getCaption();
				}
				else
				{
					con = con+"','"+vNo.get(i).getCaption();
				}
				tsl++;
			}
		}
		if(isVSelect)
		{
			showReport(con);
		}
		else
		{
			getParent().showNotification("You have to select at least one voucher to view or print the report.",Notification.TYPE_WARNING_MESSAGE);
		}			
	}

	private void showReport(String in)
	{
		String sql = "";
		HashMap hm = new HashMap();
		try
		{
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("userName",sessionBean.getUserName());
			hm.put("userIp",sessionBean.getUserIp());
			
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
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select  [dbo].[VoucherSelect]('"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			String voucher =  "voucher"+fsl;
			
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
				tx.commit();

			if(voucherType.getValue().toString().equalsIgnoreCase("dca"))
			{
				
/*				sql = "SELECT vwVoucher.Voucher_No AS vwVoucher_Voucher_No,vwVoucher.Date AS vwVoucher_Date,"+
				"tbLedger.Ledger_Name AS tbLedger_Ledger_Name,vwVoucher.Narration AS vwVoucher_Narration,"+
				"vwVoucher.DrAmount AS vwVoucher_DrAmount,dbo.number(vwVoucher.DrAmount)+' Only' amtWord,"+
				"vwVoucher.TransactionWith AS vwVoucher_TransactionWith,replace(vwVoucher.attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill  FROM "+
				"dbo.tbLedger tbLedger INNER JOIN dbo.vwVoucher vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id and tbLedger.companyId = vwVoucher.companyId "+
				"WHERE Voucher_No IN ('"+in+"') AND vwVoucher.Ledger_Id != 'AL1' AND auditapproveflag = 2 AND vwVoucher.companyId = '"+ sessionBean.getCompanyId() +"' AND (tbLedger.companyId = '"+ sessionBean.getCompanyId() +"') AND (vwVoucher.DrAmount > 0)";
				*/
				
				// closed before audit & approve name 
					/*sql = "SELECT tbLedger.Ledger_Id, tbLedger.Ledger_Name, vwVoucher.Date, vwVoucher.Voucher_No, vwVoucher.Narration,"+
						"vwVoucher.CrAmount,vwVoucher.DrAmount,vwVoucher.vouchertype,vwVoucher.TransactionWith,dbo.number(vwVoucher.CrAmount)+' Only' amtWord,"+
						"vwVoucher.DrAmount AS vwVoucher_DrAmount,dbo.number(vwVoucher.DrAmount)+' Only' amtWord,"+
						"replace(vwVoucher.attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill,dbo.tbLogin.name AS prepared_by "+
						"FROM dbo.tbLedger INNER JOIN dbo.vwVoucher AS vwVoucher ON dbo.tbLedger.Ledger_Id = vwVoucher.Ledger_Id INNER JOIN dbo.tbLogin ON vwVoucher.userId = dbo.tbLogin.userId "+
						"WHERE Voucher_No IN ('"+in+"')  AND vwVoucher.companyId = '"+ sessionBean.getCompanyId() +"' AND (tbLedger.companyId = '"+ sessionBean.getCompanyId() +"')";
				*/
				
				sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+in+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by DrAmount desc";
						
				System.out.println(sql);
				
				
				
				
				//System.out.println(sql);
				hm.put("sql",sql);
				
				Window win = new ReportViewer(hm,"report/account/voucher/CashPaymentVoucher.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				
				getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
				win.setCaption("CASH PAYMENT VOUCHER :: "+sessionBean.getCompany());
			}
			else if(voucherType.getValue().toString().equalsIgnoreCase("cca','cci"))
			{

/*				sql = "SELECT vwVoucher.Voucher_No AS vwVoucher_Voucher_No,vwVoucher.Date AS vwVoucher_Date,"+
				"tbLedger.Ledger_Name AS tbLedger_Ledger_Name,vwVoucher.Narration AS vwVoucher_Narration,"+
				"vwVoucher.CrAmount AS vwVoucher_CrAmount,dbo.number(vwVoucher.CrAmount)+' Only' amtWord,"+
				"vwVoucher.TransactionWith AS vwVoucher_TransactionWith,replace(vwVoucher.attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill FROM "+
				"dbo.tbLedger tbLedger INNER JOIN dbo.vwVoucher vwVoucher ON tbLedger.Ledger_Id = vwVoucher.Ledger_Id and tbLedger.companyId = vwVoucher.companyId "+
				"WHERE Voucher_No IN ('"+in+"') AND vwVoucher.Ledger_Id != 'AL1' AND auditapproveflag = 2 AND vwVoucher.companyId = '"+ sessionBean.getCompanyId() +"' AND (tbLedger.companyId = '"+ sessionBean.getCompanyId() +"') AND (vwVoucher.CrAmount > 0)";
				*/
				
/*				sql = "SELECT  v.Voucher_No, l.Ledger_Name, v.Date, v.TransactionWith, v.Narration, v.DrAmount, v.CrAmount, dbo.number(v.DrAmount) "+
						"  + ' Only' AS amtWord, REPLACE(v.attachBill, 'D:/Tomcat 7.0/webapps/', '') AS attachBill, dbo.tbLogin.name AS prepared_by "+
						"FROM         dbo.vwVoucher AS v INNER JOIN dbo.tbLogin ON v.userId = dbo.tbLogin.userId LEFT OUTER JOIN dbo.tbLedger AS l ON v.Ledger_Id = l.Ledger_Id "+
						"WHERE  Voucher_No IN ('"+in+"')  AND v.companyId = '"+ sessionBean.getCompanyId() +"' AND (l.companyId = '"+ sessionBean.getCompanyId() +"') " ;
				*/
				
			 sql = "SELECT * FROM vwCashVoucher WHERE Voucher_No IN ('"+in+"')  AND companyId = '"+ sessionBean.getCompanyId() +"' AND (company_Id = '"+ sessionBean.getCompanyId() +"') order by CrAmount desc";
			 
				System.out.println(sql);
				hm.put("sql",sql);
				Window win = new ReportViewer(hm,"report/account/voucher/CashReceipttVoucher.jasper",getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
				win.setCaption("CASH RECEIPT VOUCHER :: "+sessionBean.getCompany());
			}
			else if(voucherType.getValue().toString().equalsIgnoreCase("dba"))
			{
	/*			sql = "SELECT Voucher_No,Date,TransactionWith,Narration,DrAmount,CrAmount,dbo.number(CrAmount)+' Only' AS amtWord,"+
				"Cheque_No,Cheque_Date,Ledger_Name,replace(attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill FROM dbo.vwChequeRegister WHERE Voucher_No in('"+in+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount DESC";
*/
/*				sql = "SELECT Voucher_No,Date,TransactionWith,Narration,DrAmount,CrAmount,dbo.number(CrAmount)+' Only' AS amtWord,"+
						"Cheque_No,Cheque_Date,Ledger_Name,replace(attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill,prepared_by FROM dbo.vwChequeRegister WHERE Voucher_No in('"+in+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount DESC";
*/
				
				 sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+in+"') AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount DESC";
				//System.out.println("Hello1");
				System.out.println(sql);
				hm.put("sql",sql);
				
				Window win = new ReportViewer(hm,"report/account/voucher/BankPaymentVoucher.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				
				getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
				win.setCaption("BANK PAYMENT VOUCHER :: "+sessionBean.getCompany());
			}
			else if(voucherType.getValue().toString().equalsIgnoreCase("cba','cbi"))
			{

/*				sql = "SELECT Voucher_No,Date,TransactionWith,Narration,DrAmount,CrAmount,dbo.number(DrAmount)+' Only' AS amtWord,"+
				"Cheque_No,Cheque_Date,Ledger_Name,replace(attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill FROM dbo.vwChequeRegister WHERE Voucher_No in('"+in+"') AND auditapproveflag = 2 AND companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";
*/
/*				sql = "SELECT Voucher_No,Date,TransactionWith,Narration,DrAmount,CrAmount,dbo.number(DrAmount)+' Only' AS amtWord,"+
						"Cheque_No,Cheque_Date,Ledger_Name,replace(attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill,prepared_by FROM dbo.vwChequeRegister WHERE Voucher_No in('"+in+"') and companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";
*/
			  sql = "SELECT * from vwBankVoucher WHERE Voucher_No in('"+in+"') and companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),CrAmount Desc";
				
				 System.out.println(sql);
				hm.put("sql",sql);
				Window win = new ReportViewer(hm,"report/account/voucher/BankReceiveVoucher.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				
				getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
				win.setCaption("BANK RECEIPT VOUCHER :: "+sessionBean.getCompany());

			}
			else if(voucherType.getValue().toString().equalsIgnoreCase("jau','jcv"))
			{
/*				sql = "SELECT Voucher_No,Date,TransactionWith,Narration,DrAmount,CrAmount,"+
				"Ledger_Name,replace(v.attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill FROM dbo.vwVoucher as v inner join tbLedger as l on v.Ledger_Id = l.Ledger_Id  and v.companyId = l.companyId WHERE Voucher_No in('"+in+"') AND auditapproveflag = 2 And l.companyId = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";
				*/
				
				sql = "SELECT * FROM vwJournalVoucher WHERE Voucher_No in('"+in+"') AND auditapproveflag = 2 And companyId = '"+ sessionBean.getCompanyId() +"' and company_Id = '"+ sessionBean.getCompanyId() +"' ORDER BY CAST(substring(VOucher_No,7,50) as int),DrAmount DESC";
				
				System.out.println(sql);
				
				hm.put("sql",sql);
				
				Window win = new ReportViewer(hm,"report/account/voucher/JournalVoucher.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				
				getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
				win.setCaption("JOURNAL VOUCHER :: "+sessionBean.getCompany());
			}
			else if(voucherType.getValue().toString().equalsIgnoreCase("pao"))
			{

/*				sql = "SELECT    TOP (100) PERCENT dbo.tbFixedAsset.VoucherNo, tbLedger_2.Ledger_Name AS crdAc, dbo.tbFixedAsset.dAcquisition, dbo.tbFixedAsset.vDescription,(SELECT     Ledger_Name "+
				"FROM          dbo.tbLedger AS tbLedger_1  WHERE      (Ledger_Id = dbo.tbFixedAsset.vLedgerID)) AS debAc, dbo.tbFixedAsset.mTotal, dbo.number(dbo.tbFixedAsset.mTotal) + ' Only' AS amtWord "+
				"	FROM         dbo.tbFixedAsset INNER JOIN dbo.tbLedger AS tbLedger_2 ON dbo.tbFixedAsset.AssetID = tbLedger_2.Ledger_Id "+
				"	WHERE     (dbo.tbFixedAsset.VoucherNo IN ('"+in+"')) AND (tbLedger_2.companyId ='"+ sessionBean.getCompanyId() +"') ORDER BY CAST(SUBSTRING(dbo.tbFixedAsset.VoucherNo, 7, 50) AS int)";
*/
		
				//before audit & approve Name
/*				 
				sql = "SELECT DISTINCT dbo.tbFixedAsset.VoucherNo, tbLedger_2.Ledger_Name AS crdAc, dbo.tbFixedAsset.dAcquisition, dbo.tbFixedAsset.vDescription,  "+
				"(SELECT     Ledger_Name FROM dbo.tbLedger AS tbLedger_1 WHERE      (Ledger_Id = dbo.tbFixedAsset.vLedgerID)) AS debAc, dbo.tbFixedAsset.mTotal,  "+
				"dbo.number(dbo.tbFixedAsset.mTotal) + ' Only' AS amtWord, replace(v.attachBill,'D:/Tomcat 7.0/webapps/','') as attachBill FROM         dbo.tbFixedAsset INNER JOIN dbo.tbLedger "+ 
				"AS tbLedger_2 ON dbo.tbFixedAsset.AssetID = tbLedger_2.Ledger_Id LEFT OUTER JOIN dbo.vwVoucher AS v ON dbo.tbFixedAsset.VoucherNo = v.Voucher_No "+ 
				" WHERE     (dbo.tbFixedAsset.VoucherNo IN ('"+in+"')) AND (tbLedger_2.companyId ='"+ sessionBean.getCompanyId() +"')";
				*/
//				sql = "SELECT VoucherNo,(SELECT Ledger_Name FROM tbLedger WHERE Ledger_Id = AssetID) as crdAc,"+
//				"dAcquisition,vDescription,(SELECT Ledger_Name FROM tbLedger WHERE Ledger_Id = vLedgerID) "+
//				"as debAc,mTotal,dbo.number(mTotal)+' Only' AS amtWord  FROM tbFixedAsset WHERE VoucherNo in ('"+
//				in+"') AND companyId = '"+ sessionBean.getCompanyId() +"' order by CAST(substring(VOucherNo,7,50) as int)";
//			
				sql = "Select * from vwAssetPurchaseVoucher WHERE (VoucherNo IN ('"+in+"')) AND (companyId ='"+ sessionBean.getCompanyId() +"') AND (company_Id ='"+ sessionBean.getCompanyId() +"')";
				
				System.out.println(sql);
				
				hm.put("sql",sql);
				Window win = new ReportViewer(hm,"report/account/voucher/FixedAssetVoucher.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				
				getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
				win.setCaption("FIXED ASSET VOUCHER :: "+sessionBean.getCompany());

			}
			else if(voucherType.getValue().toString().equalsIgnoreCase("daj"))
			{
/*
				sql = "SELECT Voucher_No,Date,AssetId,l.Ledger_Name,Narration,Depreciation,"+
				"dbo.number(Depreciation)+' Only' AS amtWord FROM [tbDepreciationDetails] as d LEFT JOIN "+
				"tbLedger as l ON d.AssetId = l.Ledger_Id WHERE Voucher_No in ('"+in+"') AND companyId = '"+ sessionBean.getCompanyId() +"' order by CAST(substring(VOucherNo,7,50) as int)";
*/
/*				sql = "SELECT     v.Voucher_No, v.Date, l.Ledger_Id AS AssetId, l.Ledger_Name, v.Narration, v.CrAmount as Depreciation, dbo.number(v.CrAmount) + ' Only' AS amtWord, "+
						"REPLACE(v.attachBill, 'D:/Tomcat 7.0/webapps/', '') AS attachBill, lo.name AS prepared_by FROM dbo.tbLedger AS l INNER JOIN dbo.vwVoucher AS v ON l.Ledger_Id = v.Ledger_Id INNER JOIN  dbo.tbLogin AS lo "+
						"ON v.userId = lo.userId WHERE     (v.Voucher_No IN ('"+in+"')) AND (v.companyId =  '"+ sessionBean.getCompanyId() +"') AND (l.companyId =  '"+ sessionBean.getCompanyId() +"') AND (v.CrAmount > 0) ";*/
				
				
				sql = "Select * from vwDepreciationVoucher WHERE (Voucher_No IN ('"+in+"')) AND (companyId ='"+ sessionBean.getCompanyId() +"') AND (company_Id ='"+ sessionBean.getCompanyId() +"')";
				
				System.out.println(sql);
				hm.put("sql",sql);
				Window win = new ReportViewer(hm,"report/account/voucher/DepriciationVoucher.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				
				getParent().getWindow().addWindow(win);
				win.setStyleName("cwindow");
				win.setCaption("DEPRICIATION VOUCHER :: "+sessionBean.getCompany());

			}
			else
			{
				showNotification("Error ","Internal error. Please contact your software vendor.",Notification.TYPE_ERROR_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void allBtnAction()
	{
		if(isAllSelect)
		{
			allBtn.setCaption("Select All");
			for(int i=0;i<vNo.size();i++)
				vNo.get(i).setValue(false);
		}
		else
		{
			allBtn.setCaption("Deselect All"); 
			for(int i=0;i<vNo.size();i++)
				vNo.get(i).setValue(true);
		}
		isAllSelect = !isAllSelect;
	}

	private void costCenterInitialise()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery("SELECT id,costCentreName FROM tbCostCentre where companyId = '"+sessionBean.getCompanyId()+"' ORDER BY costCentreName").list().iterator();

//			costCentre.addItem("-1");
//			costCentre.setItemCaption("-1", "All");

			for(int i=0;iter.hasNext();i++)
			{
				Object[] element = (Object[]) iter.next();
				costCentre.addItem(element[0]);
				costCentre.setItemCaption(element[0], element[1].toString());
			}
			costCentre.setNullSelectionAllowed(false);
//			costCentre.setValue("-1");
		}
		catch(Exception exp)
		{
			showNotification("Error ",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	
	private boolean chkDate()
	{

		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dtfYMD.format(fromDate.getValue())+"','"+dtfYMD.format(toDate.getValue())+"')").list().iterator().next().toString();
			System.out.println(f);
			if (f.equals("1"))	

				//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue()))>= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
				//					&&
				//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue()))<= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			{
				return true;
			}
			else
			{
				this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			this.getParent().showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}

}
