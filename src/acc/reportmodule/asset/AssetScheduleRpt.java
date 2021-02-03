package acc.reportmodule.asset;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportPdf;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

import database.hibernate.TbCompanyInfo;

public class AssetScheduleRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	private HorizontalLayout chklayout = new HorizontalLayout();

	private DateField asOnDate = new DateField("As on Date:");
	private ComboBox bankList = new ComboBox("Bank List:");

	private String lcw = "130px";
	private SimpleDateFormat dtfYMD = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
	private String rpt = "";
	
	private ComboBox ledgerList = new ComboBox("Ledger List:");
	
	private CheckBox chkwithvalue = new CheckBox("with value");
	private CheckBox chkall = new CheckBox("All");

	public AssetScheduleRpt(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setWidth("330px");
		this.setResizable(false);

		formLayout.addComponent(asOnDate);

		this.setCaption("ASSET SCHEDULE :: "+this.sessionBean.getCompany());

		asOnDate.setWidth(lcw);
		asOnDate.setValue(new java.util.Date());
		asOnDate.setResolution(PopupDateField.RESOLUTION_DAY);
		asOnDate.setDateFormat("dd-MM-yy");
		asOnDate.setInvalidAllowed(false);
		asOnDate.setImmediate(true);


		btnL.setSpacing(true);
		chklayout.setSpacing(true);
		
		chklayout.addComponent(chkwithvalue);
		chkwithvalue.setValue(true);
		chkwithvalue.setImmediate(true);
		
		chklayout.addComponent(chkall);
		chkall.setImmediate(true);
	
		
		btnL.addComponent(button);
		//		btnL.addComponent(exitBtn);

		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));
		formLayout.addComponent(chklayout);
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		Component comp[] = {asOnDate, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		buttonActionAdd();
		asOnDate.focus();
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				preBtnAction(event);
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
		
		chkwithvalue.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) 
			{
				
			   if	(chkwithvalue.booleanValue())
			   {
				   chkall.setValue(false);
			   }
			}
		});
		
		chkall.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) 
			{
				
			   if	(chkall.booleanValue())
			   {
				   chkwithvalue.setValue(false);
			   }
			}
		});
	}

	private void preBtnAction(ClickEvent event)
	{
//		if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(asOnDate.getValue())) >= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
//				&&
//				(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(asOnDate.getValue())) <= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
//		{
			showReport();
//		}
//		else
//		{
//			this.getParent().showNotification("","Transaction date is not valid. Transaction date must be within the working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
//		}	
	}
	private void showReport()
	{
		try
		{ 
			String report="";
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+dtfYMD.format(asOnDate.getValue())+"')").list().iterator().next().toString();
			//String voucher =  "voucher"+fsl;
			session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
			//session = SessionFactoryUtil.getInstance().getCurrentSession();
			//tx = session.beginTransaction();
			Date dt = (Date) session.createSQLQuery("Select op_date  from tbFiscal_Year where slNo = "+fsl+"").list().iterator().next();
			//hm.put("fromDate", dt);
				tx.commit();
				
			HashMap hm = new HashMap();
			hm.put("fromDate",dt);
		//	System.out.println(sessionBean.getFiscalOpenDate());
			//System.out.println(dft.format(sessionBean.getFiscalOpenDate()));
			hm.put("toDate", asOnDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());
			hm.put("path","report/account/asset/");
			
			if (chkall.booleanValue())
			{
				report="assetSchdule.jasper"	;
				
				Window win = new ReportPdf(hm,"report/account/asset/assetSchdule.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				this.getParent().getWindow().addWindow(win);
				win.setCaption("ASSET SCHEDULE :: "+sessionBean.getCompany());
			}
			
			if (chkwithvalue.booleanValue())
			{
				report="assetSchdulewithvalue.jasper"	;
				String sql=" select tb.AssetID, tb.vLedgerName, tb.vGroupID, tb.Group_Name, "+
						"  tb.vSubGroupID, tb.Sub_Group_Name,isnull(tb.CostBal,0)CostBal,isnull(tb.addition,0)addition, "+
						"  isnull(tb.CostDel,0)CostDel,isnull(tb.depRate,0)depRate,isnull(tb.DepBal,0)DepBal, "+
						"  isnull(tb.DepCharge,0)DepCharge,isnull(tb.DepDel,0)DepDel from( "+
						" SELECT     Distinct fa.AssetID, fa.vLedgerName, fa.vGroupID, mg.Group_Name, "+
						"  fa.vSubGroupID, sg.Sub_Group_Name, "+
						" ( "+
						" 	SELECT     AssetDrAmount - AssetCrAmount AS Expr1 FROM dbo.tbAssetOpBalance "+
						" 	WHERE      (AssetId = fa.AssetID) and Op_Year = Year('"+dt+"') and "+
						" 	companyId =  '"+sessionBean.getCompanyId()+"' "+
						" )/1 AS CostBal, "+
						" ( "+
						" 	SELECT ISNULL(SUM(mTotal), 0) AS Expr1 FROM dbo.tbFixedAsset "+
						" 	WHERE (AssetID = fa.AssetID) AND (Type = 'P') and (companyId = '"+sessionBean.getCompanyId()+"') "+
						" 	AND (dAcquisition BETWEEN '"+dt+"' AND '"+dft.format(asOnDate.getValue())+"') "+
						" )/1 AS addition, "+
						" ( "+
						" 	SELECT ISNULL(SUM(Costprice), 0) AS Expr1 FROM dbo.tbAssetSales "+
						" 	WHERE(AssetId = fa.AssetID) and (companyId = '"+sessionBean.getCompanyId()+"') AND (Date BETWEEN '"+dt+"' "+
						" 	AND '"+dft.format(asOnDate.getValue())+"') "+
						"  )/1 AS CostDel, "+
						" ( "+
						" 	SELECT MAX(iDepreciationPer) AS Expr1 "+
						" 	FROM dbo.tbFixedAsset AS tbFixedAsset_1 "+
						" 	WHERE(companyId ='"+sessionBean.getCompanyId()+"') and  (AssetID = fa.AssetID) "+
						" ) AS depRate, "+
						" ( "+
						" 	SELECT DepreciationCrAmount - DepreciationDrAmount AS Expr1 "+
						" 	FROM dbo.tbAssetOpBalance AS tbAssetOpBalance_1 "+
						" 	WHERE(AssetId = fa.AssetID) and tbAssetOpBalance_1.Op_Year =  Year('"+dt+"') and "+
						" 	companyId = '"+sessionBean.getCompanyId()+"' "+
						" )/1 AS DepBal, "+
						" ( "+
						" 	SELECT     ISNULL(SUM(Depreciation), 0) AS Expr1 "+
						" 	FROM dbo.tbDepreciationDetails "+
						" 	WHERE(AssetId = fa.AssetID) and (companyId = 1)  AND (Date BETWEEN '"+dt+"' "+
						" 	AND '"+dft.format(asOnDate.getValue())+"') "+
						" )/1 AS DepCharge, "+
						" ( "+
						" 	SELECT -ISNULL(SUM(WrittenValue - Costprice), 0) AS Expr1 "+
						" 	FROM dbo.tbAssetSales AS tbAssetSales_1 "+
						" 	WHERE(AssetId = fa.AssetID) and (companyId = '"+sessionBean.getCompanyId()+"') "+
						" 	AND (Date BETWEEN '"+dt+"' AND '"+dft.format(asOnDate.getValue())+"') "+
						" )/1 AS DepDel "+
						" FROM dbo.tbFixedAsset AS fa  "+
						" LEFT OUTER JOIN dbo.tbLedger ON fa.vLedgerID = dbo.tbLedger.Ledger_Id  "+
						" LEFT OUTER JOIN dbo.tbMain_Group AS mg ON fa.vGroupID = mg.Group_Id  "+
						" LEFT OUTER JOIN dbo.tbSub_Group AS sg ON sg.Sub_Group_Id = fa.vSubGroupID "+
						" WHERE(fa.companyId = '"+sessionBean.getCompanyId()+"') "+
						" )tb  "+
						" where tb.CostBal!=0 or tb.addition!=0 or tb.CostDel!=0 or tb.DepBal!=0 or tb.DepCharge!=0 or tb.DepDel!=0 "+
						" ORDER BY tb.vGroupID , tb.vSubGroupID ";
				hm.put("sql", sql);
				System.out.println(hm.get("sql"));
				
				Window win = new ReportPdf(hm,"report/account/asset/assetSchdulewithvalue.jasper",
						getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						getWindow().getApplication().getURL()+"VAADIN/rpttmp",false);
				this.getParent().getWindow().addWindow(win);
				win.setCaption("ASSET SCHEDULE :: "+sessionBean.getCompany());
			}
			
					
			
			

		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
