package com.reportform.lcReport;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class RptLcInfo extends Window
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	private Label lblLcNo;

	private ComboBox cmbLcNo;

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"PDF","Other"});
	CommonButton button = new CommonButton("", "", "", "","","","","Preview","","Exit");
	
	private ReportDate reportTime = new ReportDate();

	public RptLcInfo(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("L/C INFORMATION :: "+this.sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);
		setCombo();
		btnAction();
	}
	private void btnAction()
	{
		button.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				previewBtnAction();
			}
		});

		button.btnExit.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	private void setCombo()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> lst = session.createSQLQuery("select 0,vLcNo from tbLcOpeningInfo order by vLcNo ").list();
			for (Iterator<?> iter = lst.iterator();iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbLcNo.addItem(element[1]);
				cmbLcNo.setItemCaption(element[1], element[1].toString());
			}
		}
		catch (Exception ex) 
		{
			System.out.print(ex);
		} 
	}

	private void previewBtnAction()
	{
		ReportOption rptOption = new ReportOption(RadioBtnGroup.getValue().toString());
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		String queryAll = null;
		try
		{
			queryAll = "select loi.vPrimaryGroup,loi.vMainGroup,loi.vSubGroup,loi.vLcNo,loi.vLedgerID,loi.dLcOpeningDate," +
					"loi.vLcOpeningBank,loi.vLcOpeningBranch,loi.vOrigin,loi.vDischargePort,loi.vVessaileName,loi.dShipmentDate,loi.vModeOfShipment," +
					"loi.vArrivalPort,loi.dArrivalDate,loi.dExpiryDate,loi.vIncoterm,loi.vSupplierName,loi.vSupplierAddress," +
					"loi.mLCAmountBDT,loi.mExchangeRate,loi.mLCValueUSD,loi.mMarginPercentage,loi.mMarginAmount,loi.vBbRefferenceNo," +
					"loi.dRefferenceDate,loi.vProformaInvNo,loi.dProformaInvDate,loi.vMarineCoveredNo,loi.dMarineCoveredDate," +
					"loi.vInsCompanyName,loi.mTotalPremium,loi.mNetPremium,loi.mInsuranceRefund,loi.vBenificiaryBank,loi.vBenificiaryBranch,loi.vAmmendmentNo,loi.dAmmentmentDate,loi.vAmmendmentReason,loi.dClearingDate,loi.vCnfAgentNAme,loi.vNameOfLC,lod.vLcNo," +
					"lod.vProductId,lod.vProductName,lod.vProductUnit,lod.mQuantity,lod.mRate,lod.mRateBdt,lod.mAmountBdt,lod.mAmount,lod.vHsCode,(select SUM(amount) Amount  from tbLcChargeInfo li inner join tbLcChargeDetails ld on li.referenceNo = ld.referenceNo where li.lcNo = loi.vLcNo)Amount " +
					"from tbLcOpeningInfo loi inner join tbLcOpeningDetails lod on loi.vLcNo = lod.vLcNo where " +
					"loi.vLcNo = '"+cmbLcNo.getValue().toString()+"'";	

			System.out.println("Report Query: "+queryAll);

			if(queryCheckValue(queryAll))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("UserIp", sessionBean.getUserIp());
				hm.put("user", sessionBean.getUserName());
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("SysDate",reportTime.getTime);
				hm.put("sql", queryAll);
				
				session.createSQLQuery("exec prcLcCharge '"+cmbLcNo.getValue().toString()+"' ").executeUpdate();
				tx.commit();
				String subQuery="";
				
				
			/*	
				String  subQuery=
						  "select lo.lcNo,lo.date,lo.referenceNo,lo.voucherNo,ld.headId,ld.headName,lo.voucherType,  "
						 +"(select isnull(SUM(amount),0) from tbLcChargeDetails where voucherNo=lo.voucherNo and (select ISNULL(SUM(DrAmount),0)  from vwVoucher where lo.ledgerIdLc=vwVoucher.Ledger_Id and lo.voucherNo=vwVoucher.Voucher_No )!=0)DrAmount,  "
						 +"(select isnull(SUM(amount),0) from tbLcChargeDetails where voucherNo=lo.voucherNo and (select ISNULL(SUM(CrAmount),0)  from vwVoucher where lo.ledgerIdLc=vwVoucher.Ledger_Id and lo.voucherNo=vwVoucher.Voucher_No )!=0)CrAmount from  "
				         +"tbLcChargeInfo lo inner join tbLcChargeDetails ld on lo.referenceNo = ld.referenceNo where lo.lcNo "
				        + "like '"+cmbLcNo.getValue().toString()+"' order by headId  ";*/
				
				subQuery="select * from tbTempLcCharge";
				
				
/*
				String subQuery="select lo.lcNo,lo.date,lo.referenceNo,lo.voucherNo,ld.headId,ld.headName,lo.voucherType, "+
								" (select isnull(SUM(amount),0) from tbLcChargeDetails where voucherNo=lo.voucherNo and (lo.voucherType='Debit' or lo.voucherType is null ))DrAmount, "+
								" (select isnull(SUM(amount),0) from tbLcChargeDetails where voucherNo=lo.voucherNo and lo.voucherType='Credit')CrAmount from " +
						"tbLcChargeInfo lo inner join tbLcChargeDetails ld on lo.referenceNo = ld.referenceNo where lo.lcNo " +
						"like '"+cmbLcNo.getValue().toString()+"' order by headId ";*/
				
				
				
				
				
					
				
				
				
				if(queryCheckValue(subQuery))
				{
					hm.put("subSql", subQuery);
					hm.put("SUBREPORT_DIR", "./report/account/");
				}
				else
				{
					hm.put("subSql", "");
				}

				Window win = new ReportViewer(hm,"report/account/RptLcOpeningInformation.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",rptOption.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private boolean queryCheckValue(String query)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			List <?> lst = session.createSQLQuery(query).list();
			if(!lst.isEmpty())
				return true;
		}
		catch (Exception exp)
		{
			showNotification("queryCheckValue", exp.toString(), Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
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
		setWidth("420px");
		setHeight("200px");

		// lblLcNo
		lblLcNo = new Label();
		lblLcNo.setImmediate(true);
		lblLcNo.setWidth("-1px");
		lblLcNo.setHeight("-1px");
		lblLcNo.setValue("L/C Number : ");
		mainLayout.addComponent(lblLcNo, "top:45.0px;left:50.0px;");

		// cmbLcNo
		cmbLcNo = new ComboBox();
		cmbLcNo.setImmediate(true);
		cmbLcNo.setHeight("-1px");
		cmbLcNo.setWidth("200px");
		cmbLcNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbLcNo.setNullSelectionAllowed(true);
		mainLayout.addComponent(cmbLcNo, "top:42.0px;left:150.0px;");

		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setValue("PDF");
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnGroup, "top:67.0px; left:150.0px;");
		//CButton
		mainLayout.addComponent(button, "top:100.0px;left:130.0px;");

		return mainLayout;
	}
}
