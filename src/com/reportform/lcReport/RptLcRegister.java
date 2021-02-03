package com.reportform.lcReport;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;

import com.common.share.CommonButton;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptLcRegister extends Window 
{
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private DateField dFromDate = new DateField("From Date :");
	private DateField dToDate = new DateField("To Date :");

	private FormLayout formLayout = new FormLayout();
	private FormLayout left = new FormLayout();

	private HorizontalLayout middleLayout = new  HorizontalLayout();
	private HorizontalLayout btnLayout = new HorizontalLayout();
	private OptionGroup RadioBtnGroup;
	private static final List<String> reportType = Arrays.asList(new String[]{"PDF","Other"});

	private CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
	private ReportDate reportTime = new ReportDate();

	public RptLcRegister(SessionBean sessionBean) 
	{
		this.setCaption("L/C REGISTER REPORT:: "+sessionBean.getCompany());
		this.sessionBean = sessionBean;
		this.setWidth("400px");
		this.setHeight("230px");
		this.setResizable(false);

		dFromDate.setWidth("110px");
		dToDate.setWidth("110px");
		left.setWidth("50px");

		dFromDate.setDateFormat("dd-MM-yyyy");
		dFromDate.setValue(new Date());
		dFromDate.setImmediate(true);
		dFromDate.setResolution(PopupDateField.RESOLUTION_DAY);

		dToDate.setDateFormat("dd-MM-yyyy");
		dToDate.setValue(new Date());
		dToDate.setImmediate(true);
		dToDate.setResolution(PopupDateField.RESOLUTION_DAY);
		
		addAllComponent();
		allButtonAction();
		this.addComponent(mainLayout);
	}



	public void allButtonAction()
	{
		button.btnPreview.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(dFromDate.getValue()!=null)
				{
					previewBtnAction();
				}
				else
				{
					showNotification("Warning","Select L/C Register Date",Notification.TYPE_WARNING_MESSAGE);
				}

			}
		});

		button.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

	}
	
	private void previewBtnAction()
	{
		ReportOption rptOption = new ReportOption(RadioBtnGroup.getValue().toString());
		String queryAll = null;
		try
		{
			queryAll = "select loi.vLcNo,loi.dLcOpeningDate,loi.vLcOpeningBank,loi.vOrigin,loi.vDischargePort," +
					"loi.dShipmentDate,loi.vModeOfShipment,loi.dExpiryDate,loi.vIncoterm,loi.mLCValueUSD,loi.vProformaInvNo," +
					"loi.dProformaInvDate,loi.vMarineCoveredNo,loi.dMarineCoveredDate,loi.vInsCompanyName,loi.mTotalPremium," +
					"loi.mNetPremium,loi.mInsuranceRefund,loi.vBenificiaryBank,loi.vBenificiaryBranch,loi.vAmmendmentNo," +
					"loi.dAmmentmentDate,loi.dClearingDate,loi.vCnfAgentNAme,loi.vNameOfLC,lod.vProductId,lod.vProductName," +
					"lod.vProductUnit,lod.mQuantity,lod.mRate,lod.mAmount,lod.vHsCode from tbLcOpeningInfo loi inner join " +
					"tbLcOpeningDetails lod on loi.vLcNo = lod.vLcNo where loi.dLcOpeningDate between '"+dFormat.format(dFromDate.getValue())+"' and " +
					"'"+dFormat.format(dToDate.getValue())+"'";	

			System.out.println("Report Query: "+queryAll);

			if(queryCheckValue(queryAll))
			{
				HashMap <String,Object> hm = new HashMap <String,Object> ();
				hm.put("company", sessionBean.getCompany());
				hm.put("address", sessionBean.getCompanyAddress());
				hm.put("userIp", sessionBean.getUserIp());
				hm.put("user", sessionBean.getUserName());
				hm.put("SysDate",reportTime.getTime);
				hm.put("phone", sessionBean.getCompanyContact());
				hm.put("logo", sessionBean.getCompanyLogo());
				hm.put("sql", queryAll);

				Window win = new ReportViewer(hm,"report/account/rptLcRegister.jasper",
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
	
	private void addAllComponent()
	{
		formLayout.addComponent(dFromDate);
		formLayout.addComponent(dToDate);
		
		RadioBtnGroup = new OptionGroup("",reportType);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		formLayout.addComponent(RadioBtnGroup);
		
		formLayout.setComponentAlignment(dFromDate, Alignment.BOTTOM_LEFT);
		formLayout.setComponentAlignment(dToDate, Alignment.BOTTOM_LEFT);
		
		mainLayout.setSpacing(true);
		middleLayout.addComponent(left);
		middleLayout.addComponent(formLayout);

		mainLayout.addComponent(middleLayout);

		btnLayout.addComponent(button);

		btnLayout.setSpacing(true);
		mainLayout.addComponent(btnLayout);
		mainLayout.setComponentAlignment(btnLayout, Alignment.BOTTOM_CENTER);
		addComponent(mainLayout);
	}

}
