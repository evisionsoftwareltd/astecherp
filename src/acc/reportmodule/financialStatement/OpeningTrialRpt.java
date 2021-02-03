package acc.reportmodule.financialStatement;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.PreviewOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class OpeningTrialRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;	
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	private Label lblFiscal = new Label("Fiscal Year List:");
	private ListSelect fiscalYearList = new ListSelect("");

	private String rpt = "";

	private AmountCommaSeperator txtUsdRate = new AmountCommaSeperator("USD Rate :");
	private SimpleDateFormat dfBd = new SimpleDateFormat("dd-MM-yyyy");
	private PreviewOption po = new PreviewOption();
	private CheckBox chkValue;

	public OpeningTrialRpt(SessionBean sessionBean,String rpt)
	{
		this.sessionBean = sessionBean;

		this.setWidth("330px");
		fiscalYearList.setWidth("200px");
		fiscalYearList.setNullSelectionAllowed(false);
		this.setResizable(false);

		if(rpt.equals("a"))
		{
			this.setCaption("Opening Trial Balance :: "+this.sessionBean.getCompany());
		}
		else if(rpt.equals("USD($)"))
		{
			this.setCaption("Opening Trial Balance USD :: "+this.sessionBean.getCompany());
			this.setWidth("350px");
		}
		else if(rpt.equals("USD($) GROUP"))
		{
			this.setCaption("Opening Trial Balance(Group Wise) USD :: "+this.sessionBean.getCompany());
			this.setWidth("350px");
		}
		else
		{
			this.setCaption("Opening Trial Balance(Group Wise) :: "+this.sessionBean.getCompany());
		}

		this.rpt = rpt;
		formLayout.addComponent(lblFiscal);
		formLayout.addComponent(fiscalYearList);
		chkValue=new CheckBox("With Value");
		chkValue.setImmediate(true);
		formLayout.addComponent(chkValue);

		txtUsdRate.setImmediate(true);
		txtUsdRate.setWidth("60px");
		txtUsdRate.setHeight("-1px");

		btnL.setSpacing(true);
		btnL.addComponent(button);

		Component comp[] = {fiscalYearList, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));

		if(rpt.equals("USD($)") || rpt.equals("USD($) GROUP"))
		{
			formLayout.addComponent(txtUsdRate);
		}

		formLayout.addComponent(po);
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);

		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		buttonActionAdd();
		fiscalListInitialise();
		fiscalYearList.focus();
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(fiscalYearList.getValue()!=null)
				{
					if(rpt.equals("USD($)") || rpt.equals("USD($) GROUP"))
					{
						checkUsd();
					}
					else
					{
						preBtnAction();
					}
				}
				else
				{
					showNotification("Warning!","Select Fiscal Year First",Notification.TYPE_WARNING_MESSAGE);
					fiscalYearList.focus();
				}
			}
		});

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		txtUsdRate.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(!txtUsdRate.getValue().toString().equals(""))
				{
					button.btnPreview.focus();
				}
			}
		});
	}

	private void fiscalListInitialise()
	{
		String sql = "select Year(Op_date), Op_date, Cl_Date from tbfiscal_year order by op_date";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			List<?> group = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = group.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				fiscalYearList.addItem(element[0]);
				fiscalYearList.setItemCaption(element[0],dfBd.format(element[1]) +" to "+dfBd.format(element[2]));
			}
		}
		catch(Exception exp)
		{
			showNotification("Error to load fiscal year",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void checkUsd()
	{
		if(Double.parseDouble("0"+txtUsdRate.getValue().toString().replaceAll(",", ""))>0)
		{
			preBtnAction();
		}
		else
		{
			showNotification("Warning!","Provide USD Rate",Notification.TYPE_WARNING_MESSAGE);
			txtUsdRate.focus();
		}
	}

	private void preBtnAction()
	{
		showTrialBalance();
	}

	private void showTrialBalance()
	{
		double depriciation = 0;
		String dep = "";
		String sql = "";
		try
		{
			Session session1 = SessionFactoryUtil.getInstance().getCurrentSession();
			session1.beginTransaction();

			String queryDep = "SELECT dbo.FixedAssetAtCost('A1'," +
					" (CONVERT(varchar,year('"+fiscalYearList.getValue().toString().substring(0,4)+"')))+'-07-01' ," +
					" (CONVERT(varchar,year('"+fiscalYearList.getValue().toString().substring(0,4)+"')+1))+'-06-30'," +
					" 'D', '"+sessionBean.getCompanyId()+"')";
			Iterator<?> iter = session1.createSQLQuery(queryDep).list().iterator();
			if(iter.hasNext())
			{
				dep = iter.next().toString();
				depriciation = Double.parseDouble(dep);
			}

			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("onDate",fiscalYearList.getValue().toString().substring(0,4));
			hm.put("fiscalYear",fiscalYearList.getItemCaption(fiscalYearList.getValue()));
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("depriciation",depriciation);

			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
			}

			hm.put("userIp", sessionBean.getUserIp().toString());
			hm.put("userName", sessionBean.getUserName().toString());

			double usd = Double.parseDouble((txtUsdRate.getValue().toString().isEmpty()?"1":txtUsdRate.getValue().toString().replaceAll(",", "")));
			hm.put("usdRate",usd);

			sql = "select lo.Ledger_Id,Ledger_Name,((CrAmount-DrAmount)-ISNULL((select DepreciationDrAmount-DepreciationCrAmount from"+
					" tbAssetOpBalance where AssetId = lo.Ledger_Id and Op_Year = '"+fiscalYearList.getValue()+"'),0))/"+usd+" bal,pg.Head_Id,Head_Name HeadName,"+
					" ISNULL(mg.Group_Id,'')GroupId,ISNULL(mg.Group_Name,'')GroupName,ISNULL(sg.Sub_Group_Id,'')SubGroupId,"+
					" ISNULL(sg.Sub_Group_Name,'')SubGroupName, case"+
					" when SUBSTRING(pg.Head_Id,1,1) = 'A' then 1"+
					" when SUBSTRING(pg.Head_Id,1,1) = 'L' then 2"+
					" when SUBSTRING(pg.Head_Id,1,1) = 'I' then 3"+
					" when SUBSTRING(pg.Head_Id,1,1) = 'E' then 4"+
					" end sl,case when SUBSTRING(pg.Head_Id,1,1) = 'A' then 'Assests'"+
					" when SUBSTRING(pg.Head_Id,1,1) = 'L' then 'Liabilities'"+
					" when SUBSTRING(pg.Head_Id,1,1) = 'I' then 'Income'"+
					" when SUBSTRING(pg.Head_Id,1,1) = 'E' then 'Expenses'"+
					" end lgroup from tbLedger l inner join tbLedger_Op_Balance lo"+
					" on l.Ledger_Id = lo.Ledger_Id inner join tbPrimary_Group pg on REPLACE(SUBSTRING(Create_From,1,3),'-','') = pg.Head_Id"+
					" left join tbMain_Group mg on mg.Group_Id = REPLACE(SUBSTRING(Create_From,4,4),'-','')"+
					" left join tbSub_Group sg on sg.Sub_Group_Id = REPLACE(SUBSTRING(Create_From,9,4),'-','')"+
					" where Op_Year = '"+fiscalYearList.getValue()+"' and lo.companyId = '"+sessionBean.getCompanyId()+"' order by sl,SlNO,Group_Name,Sub_Group_Name,Ledger_Name";

			System.out.println(sql);
			
			if(chkValue.booleanValue()){
				sql=" select Ledger_Id,Ledger_Name,bal,Head_Id,HeadName,GroupId,GroupName,SubGroupId,SubGroupName,sl,lgroup from( "+
						" select lo.Ledger_Id,Ledger_Name,((CrAmount-DrAmount)-ISNULL((select DepreciationDrAmount-DepreciationCrAmount "+
						" from tbAssetOpBalance where AssetId = lo.Ledger_Id and Op_Year = '2016'),0))/1.0 bal,pg.Head_Id,Head_Name  "+
						" HeadName, ISNULL(mg.Group_Id,'')GroupId,ISNULL(mg.Group_Name,'')GroupName,ISNULL(sg.Sub_Group_Id,'')SubGroupId, "+
						" ISNULL(sg.Sub_Group_Name,'')SubGroupName, case when SUBSTRING(pg.Head_Id,1,1) = 'A' then 1 "+
						"  when SUBSTRING(pg.Head_Id,1,1) = 'L' then 2 when SUBSTRING(pg.Head_Id,1,1) = 'I' then 3  "+
						"  when SUBSTRING(pg.Head_Id,1,1) = 'E' then 4 end sl,case when SUBSTRING(pg.Head_Id,1,1) = 'A' "+ 
						" then 'Assests' when SUBSTRING(pg.Head_Id,1,1) = 'L' then 'Liabilities' when SUBSTRING(pg.Head_Id,1,1) = 'I' "+
						"  then 'Income' when SUBSTRING(pg.Head_Id,1,1) = 'E' then 'Expenses' end lgroup,SlNO from tbLedger l  "+
						"  inner join tbLedger_Op_Balance lo on l.Ledger_Id = lo.Ledger_Id  "+
						" inner join tbPrimary_Group pg on REPLACE(SUBSTRING(Create_From,1,3),'-','') = pg.Head_Id "+ 
						" left join tbMain_Group mg on mg.Group_Id = REPLACE(SUBSTRING(Create_From,4,4),'-','')  "+
						"  left join tbSub_Group sg on sg.Sub_Group_Id = REPLACE(SUBSTRING(Create_From,9,4),'-','')  "+
						"  where Op_Year = '"+fiscalYearList.getValue()+"' and lo.companyId = '"+sessionBean.getCompanyId()+"'  "+
						" ) a where bal!=0 order by sl,SlNO,GroupName,SubGroupName,Ledger_Name";
			}
			
			if(rpt.equals("a") || rpt.equals("USD($)"))
			{
				hm.put("sql",sql);
				Window win = new ReportViewer(hm,"report/account/trialbal/trialOpening.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);

				if(rpt.equals("USD($)"))
					win.setCaption("TRIAL BALANCE OPENING USD :: "+sessionBean.getCompany());
				else
					win.setCaption("TRIAL BALANCE OPENING :: "+sessionBean.getCompany());
			}
			else
			{
				sql = "select Head_Id, Head_Name groupName, SlNO, case when SUBSTRING(Head_Id,1,1) = 'A' then 1 when"
						+ " SUBSTRING(Head_Id,1,1) = 'L' then 2 when SUBSTRING(Head_Id,1,1) = 'I' then 3 when SUBSTRING(Head_Id,1,1) = 'E'"
						+ " then 4 end sl,case when SUBSTRING(Head_Id,1,1) = 'A' then 'Assests' when SUBSTRING(Head_Id,1,1) = 'L' then 'Liabilities'"
						+ " when SUBSTRING(Head_Id,1,1) = 'I' then 'Income' when SUBSTRING(Head_Id,1,1) = 'E' then 'Expenses' end"
						+ " mainHead, (ISNULL((select SUM(DrAmount) from tbLedger l inner join tbLedger_Op_Balance lo on l.Ledger_Id ="
						+ " lo.Ledger_Id where Op_Year = '"+fiscalYearList.getValue()+"' and lo.companyId = '"+sessionBean.getCompanyId()+"' and REPLACE(SUBSTRING(Create_From,1,3),'-','')"
						+ " like Head_Id),0)+ ISNULL((SELECT dbo.FixedAssetAtCost(Head_Id, '"+fiscalYearList.getValue()+"'+'-07-01', CONVERT(DATE,(DATEADD(YY,1,"
						+ "'"+fiscalYearList.getValue()+"'+'-06-30'))), 'D', '"+sessionBean.getCompanyId()+"')),0))/"+usd+" DrAmount, ISNULL((select SUM(CrAmount) from tbLedger l inner join"
						+ " tbLedger_Op_Balance lo on l.Ledger_Id = lo.Ledger_Id where Op_Year = '"+fiscalYearList.getValue()+"' and lo.companyId = '"+sessionBean.getCompanyId()+"' and"
						+ " REPLACE(SUBSTRING(Create_From,1,3),'-','') like Head_Id),0)/"+usd+" CrAmount from tbPrimary_Group order by sl,SlNO";
				
				if(chkValue.booleanValue()){
					sql = "select * from ( select Head_Id, Head_Name groupName, SlNO, case when SUBSTRING(Head_Id,1,1) = 'A' then 1 when"
							+ " SUBSTRING(Head_Id,1,1) = 'L' then 2 when SUBSTRING(Head_Id,1,1) = 'I' then 3 when SUBSTRING(Head_Id,1,1) = 'E'"
							+ " then 4 end sl,case when SUBSTRING(Head_Id,1,1) = 'A' then 'Assests' when SUBSTRING(Head_Id,1,1) = 'L' then 'Liabilities'"
							+ " when SUBSTRING(Head_Id,1,1) = 'I' then 'Income' when SUBSTRING(Head_Id,1,1) = 'E' then 'Expenses' end"
							+ " mainHead, (ISNULL((select SUM(DrAmount) from tbLedger l inner join tbLedger_Op_Balance lo on l.Ledger_Id ="
							+ " lo.Ledger_Id where Op_Year = '"+fiscalYearList.getValue()+"' and lo.companyId = '"+sessionBean.getCompanyId()+"' and REPLACE(SUBSTRING(Create_From,1,3),'-','')"
							+ " like Head_Id),0)+ ISNULL((SELECT dbo.FixedAssetAtCost(Head_Id, '"+fiscalYearList.getValue()+"'+'-07-01', CONVERT(DATE,(DATEADD(YY,1,"
							+ "'"+fiscalYearList.getValue()+"'+'-06-30'))), 'D', '"+sessionBean.getCompanyId()+"')),0))/"+usd+" DrAmount, ISNULL((select SUM(CrAmount) from tbLedger l inner join"
							+ " tbLedger_Op_Balance lo on l.Ledger_Id = lo.Ledger_Id where Op_Year = '"+fiscalYearList.getValue()+"' and lo.companyId = '"+sessionBean.getCompanyId()+"' and"
							+ " REPLACE(SUBSTRING(Create_From,1,3),'-','') like Head_Id),0)/"+usd+" CrAmount from tbPrimary_Group   ) a where DrAmount>0 and CrAmount>0 order by sl,SlNO";
				}
				
				System.out.println(sql);
				hm.put("sql",sql);
				Window win = new ReportViewer(hm,"report/account/trialbal/trialOpeningGroup.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);

				if(rpt.equals("USD($) GROUP"))
					win.setCaption("TRIAL BALANCE OPENING(GROUP WISE) USD :: "+sessionBean.getCompany());
				else
					win.setCaption("TRIAL BALANCE OPENING(GROUP WISE) :: "+sessionBean.getCompany());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error to preview :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
}