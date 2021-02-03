package acc.reportmodule.financialStatement;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountCommaSeperator;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.GenerateExcelReport11;
import com.common.share.PreviewOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class AdjustedTrialRpt extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");
	private SessionBean sessionBean;	
	private VerticalLayout mainLayout = new VerticalLayout();
	private FormLayout formLayout = new FormLayout();
	private HorizontalLayout btnL = new HorizontalLayout();
	private DateField onDate = new DateField("AS on Date:");
	private SimpleDateFormat dateRpt = new SimpleDateFormat("dd-MM-yy");
	private String lcw = "110px";
	private String rpt = "";

	private PreviewOption po = new PreviewOption();

	private AmountCommaSeperator txtUsdRate = new AmountCommaSeperator("USD Rate :");
	private CheckBox chkValue;
	private CheckBox chkWithOutValue;
	
	private  List<String> type = Arrays.asList(new String[] {"Ledger With Value","All Ledger"});
	
	public OptionGroup txtType = new OptionGroup("",type);

	public AdjustedTrialRpt(SessionBean sessionBean,String rpt)
	{
		this.sessionBean = sessionBean;
		this.setWidth("350px");
		this.setResizable(false);
		this.rpt = rpt;

		formLayout.addComponent(onDate);
	/*	chkValue=new CheckBox("With Value");
		chkValue.setImmediate(true);
		chkValue.setValue(true);
		chkWithOutValue=new CheckBox("All");
		chkWithOutValue.setImmediate(true);
		formLayout.addComponent(chkValue);*/
		
		txtType.setStyleName("horizontal");
		txtType.setImmediate(true);
		txtType.setValue("Ledger With Value");
		formLayout.addComponent(txtType);
		

		if(rpt.equals("a"))
			this.setCaption("ADJUSTED TRIAL BALANCE :: "+this.sessionBean.getCompany());
		else if(rpt.equals("USD"))
		{
			this.setCaption("ADJUSTED TRIAL BALANCE USD($) :: "+this.sessionBean.getCompany());
			this.setWidth("350px");
		}
		else if(rpt.equals("USD($) GROUP"))
		{
			this.setCaption("ADJUSTED TRIAL BALANCE (GROUP WISE) USD($) :: "+this.sessionBean.getCompany());
			this.setWidth("350px");
		}
		else
			this.setCaption("ADJUSTED TRIAL BALANCE (GROUP WISE) :: "+this.sessionBean.getCompany());

		onDate.setWidth(lcw);
		onDate.setValue(new java.util.Date());
		onDate.setResolution(PopupDateField.RESOLUTION_DAY);
		onDate.setDateFormat("dd-MM-yyyy");
		onDate.setInvalidAllowed(false);
		onDate.setImmediate(true);

		btnL.setSpacing(true);
		btnL.addComponent(button);
		//		btnL.addComponent(exitBtn);
		Component comp[] = {onDate, button.btnPreview};
		new FocusMoveByEnter(this, comp);
		formLayout.addComponent(new Label("",Label.CONTENT_XHTML));

		if(rpt.equals("USD") || rpt.equals("USD GROUP"))
		{
			txtUsdRate.setImmediate(true);
			txtUsdRate.setWidth("60px");
			txtUsdRate.setHeight("-1px");
			formLayout.addComponent(txtUsdRate);
		}

		formLayout.addComponent(po);
		formLayout.addComponent(btnL);
		mainLayout.addComponent(formLayout);
		this.addComponent(mainLayout);
		mainLayout.setMargin(true);
		buttonActionAdd();
		onDate.focus();
	}

	private void buttonActionAdd()
	{
		button.btnPreview.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(rpt.equals("USD") || rpt.equals("USD GROUP"))
				{
					checkUsd();
				}
				else
				{
					preBtnAction();
				}
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

		button.btnExit.addListener( new ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
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
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String fsl = session.createSQLQuery("Select [dbo].[VoucherSelect]('"+sessionBean.dfDb.format(onDate.getValue())+"')").list().iterator().next().toString();

		session.createSQLQuery("exec prcAlterVoucher " + fsl +"").executeUpdate();
		tx.commit();

		if(rpt.equals("a") || rpt.equals("USD"))
			showAdjustedTrial();
		else
			showGroupTRial();
	}

	private void showAdjustedTrial()
	{
		try
		{
			
			
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("onDate",onDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());


			hm.put("userIp", sessionBean.getUserIp().toString());
			hm.put("userName", sessionBean.getUserName().toString());
			hm.put("logo", sessionBean.getCompanyLogo());

			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
			}

			hm.put("usdRate",Double.parseDouble((txtUsdRate.getValue().toString().isEmpty()?
					"1":txtUsdRate.getValue().toString().replaceAll(",", ""))));
			String sql = "";
			if(txtType.getValue().equals("Ledger With Value")){
				sql = "select Ledger_Id,Ledger_Name,"+
						" ISNULL(case when pg.Head_Id = 'A1' then dbo.openingBalFixedAsset(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')"+
						" when pg.Head_Id != 'A1' and (pg.Head_Id like 'A%' OR pg.Head_Id like 'E%') then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) bal,"+
						" ISNULL(case when pg.Head_Id like 'L%' OR pg.Head_Id like 'I%' then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) Crbal,"+
						" pg.Head_Id,Head_Name HeadName,"+
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
						" end lgroup from tbLedger l inner join tbPrimary_Group pg on REPLACE(SUBSTRING(Create_From,1,3),'-','') = pg.Head_Id"+
						" left join tbMain_Group mg on mg.Group_Id = REPLACE(SUBSTRING(Create_From,4,4),'-','')"+
						" left join tbSub_Group sg on sg.Sub_Group_Id = REPLACE(SUBSTRING(Create_From,9,4),'-','')"+
						" where companyId = '"+sessionBean.getCompanyId()+"' and"+
						" (dbo.openingBalFixedAsset(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')!=0 and dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')!=0)"+
						" order by sl,SlNO,Group_Name,Sub_Group_Name,Ledger_Name";
			}
			else if(txtType.getValue().equals("All Ledger")){
				sql = "select Ledger_Id,Ledger_Name,"+
						" ISNULL(case when pg.Head_Id = 'A1' then dbo.openingBalFixedAsset(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')"+
						" when pg.Head_Id != 'A1' and (pg.Head_Id like 'A%' OR pg.Head_Id like 'E%') then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) bal,"+
						" ISNULL(case when pg.Head_Id like 'L%' OR pg.Head_Id like 'I%' then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) Crbal,"+
						" pg.Head_Id,Head_Name HeadName,"+
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
						" end lgroup from tbLedger l inner join tbPrimary_Group pg on REPLACE(SUBSTRING(Create_From,1,3),'-','') = pg.Head_Id"+
						" left join tbMain_Group mg on mg.Group_Id = REPLACE(SUBSTRING(Create_From,4,4),'-','')"+
						" left join tbSub_Group sg on sg.Sub_Group_Id = REPLACE(SUBSTRING(Create_From,9,4),'-','')"+
						" where companyId = '"+sessionBean.getCompanyId()+"'"
						+" order by sl,SlNO,Group_Name,Sub_Group_Name,Ledger_Name";
			}
			System.out.println(sql);
			
			if(rpt.equals("a") && po.txtType.getValue()=="Excel"){
				
				String loc = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/temp/attendanceFolder";
				String fname = "AdjustedTrialRpt.xls";
				String url = getWindow().getApplication().getURL()+"VAADIN/themes/temp/attendanceFolder/"+fname;
				String strColName[]={"SL","Particular","DrAmount","CrAmount"};
				String Header="Ason date: "+dateRpt.format(onDate.getValue());
				String exelSql="";
				

				//exelSql = "select Head_Id, Head_Name from  tbPrimary_Group ";
				if(txtType.getValue().equals("Ledger With Value")){
					exelSql = "select Head_Name,Ledger_Name,"+
							" cast(ISNULL(case when pg.Head_Id = 'A1' then dbo.openingBalFixedAsset(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')"+
							" when pg.Head_Id != 'A1' and (pg.Head_Id like 'A%' OR pg.Head_Id like 'E%') then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) as float) bal,"+
							" cast(ISNULL(case when pg.Head_Id like 'L%' OR pg.Head_Id like 'I%' then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) as float) Crbal"+
							" from tbLedger l inner join tbPrimary_Group pg on REPLACE(SUBSTRING(Create_From,1,3),'-','') = pg.Head_Id"+
							" where companyId = '"+sessionBean.getCompanyId()+"'and Head_Id like '%'and"+
						" (dbo.openingBalFixedAsset(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')!=0 "
								+ "and dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')!=0) order by Head_Name,Ledger_Name";
					
				}
				else if(txtType.getValue().equals("All Ledger")){
				
				exelSql = "select Head_Name,Ledger_Name,"+
						" cast(ISNULL(case when pg.Head_Id = 'A1' then dbo.openingBalFixedAsset(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')"+
						" when pg.Head_Id != 'A1' and (pg.Head_Id like 'A%' OR pg.Head_Id like 'E%') then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) as float) bal,"+
						" cast(ISNULL(case when pg.Head_Id like 'L%' OR pg.Head_Id like 'I%' then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) as float) Crbal"+
						" from tbLedger l inner join tbPrimary_Group pg on REPLACE(SUBSTRING(Create_From,1,3),'-','') = pg.Head_Id"+
						" where companyId = '"+sessionBean.getCompanyId()+"'and Head_Id like '%'  order by Head_Name,Ledger_Name";
				
				}
				System.out.println("exelSql: "+exelSql);
				
				Session session = SessionFactoryUtil.getInstance().getCurrentSession();
				session.beginTransaction();
				List <?> lst1=session.createSQLQuery(exelSql).list();
				session.close();
				
				String detailQuery[]=new String[lst1.size()];
				String [] signatureOption = {""};
				//String [] signatureOption = new String [0];
				String [] groupItem=new String[lst1.size()];
				Object [][] GroupElement=new Object[lst1.size()][];
				String [] GroupColName=new String[0];

				
				/*for(Iterator<?> iter1=lst1.iterator(); iter1.hasNext();)
				{
					System.out.println("Execl query 2 :");
					
					 Object [] element1 = (Object[])iter1.next();
						groupItem[countInd]=" "+element1[1];
						GroupElement[countInd]=new Object [] {(Object)"",(Object)""};
					
						System.out.println("Execl query 3 :");
						
						detailQuery[countInd]="select Ledger_Name,"+
								" cast(ISNULL(case when pg.Head_Id = 'A1' then dbo.openingBalFixedAsset(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"')"+
								" when pg.Head_Id != 'A1' and (pg.Head_Id like 'A%' OR pg.Head_Id like 'E%') then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) as float) bal,"+
								" cast(ISNULL(case when pg.Head_Id like 'L%' OR pg.Head_Id like 'I%' then dbo.openingBal(Ledger_Id,'"+sessionBean.dfDb.format(onDate.getValue())+"','C','"+sessionBean.getCompanyId()+"') end,0) as float) Crbal"+
								"  from tbLedger l inner join tbPrimary_Group pg on REPLACE(SUBSTRING(Create_From,1,3),'-','') = pg.Head_Id"+
								" where companyId = '"+sessionBean.getCompanyId()+"'and Head_Id like '"+element1[0]+"' order by Ledger_Name";
							
						System.out.println("Execl query 4 :");
						
					System.out.println("Details query :"+detailQuery[countInd]);
					countInd++;
					
					System.out.println("Execl query 5 :");
				}*/
				
				

				/*new GenerateExcelReport(sessionBean, loc, url, fname, "Adjusted Trail Balance", "Adjusted Trail Balance",
						Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, detailQuery, 0, 0, "A4",
						"Landscape",signatureOption);*/
				
				new GenerateExcelReport11(sessionBean, loc, url, fname, "Adjusted Trail Balance", "Adjusted Trail Balance",
						Header, strColName, 2, groupItem, GroupColName, GroupElement, 1, lst1, 0, 0, "A4",
						"Landscape",signatureOption);
				
				
				
				Window window = new Window();
				getApplication().addWindow(window);
				getWindow().open(new ExternalResource(url),"_blank",500,200,Window.BORDER_NONE);

			}
			else{
				hm.put("sql", sql);
				Window win = new ReportViewer(hm,"report/account/trialbal/trialAdjusted.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

				win.setStyleName("cwindow");
				this.getParent().getWindow().addWindow(win);
				
				if(rpt.equals("USD"))
					win.setCaption("ADJUSTED TRIAL BALANCE USD($) :: "+sessionBean.getCompany());
				else
					win.setCaption("ADJUSTED TRIAL BALANCE :: "+sessionBean.getCompany());
			
			}
			
		}
		catch(Exception exp)
		{
			showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void showGroupTRial()
	{
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("onDate",onDate.getValue());
			hm.put("comName",sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phoneFax",sessionBean.getCompanyContact());
			hm.put("companyId",sessionBean.getCompanyId());

			hm.put("userIp", sessionBean.getUserIp().toString());
			hm.put("userName", sessionBean.getUserName().toString());
			hm.put("logo", sessionBean.getCompanyLogo());
			if(!po.actionCheck)
			{
				hm.put("image", "");
			}
			else
			{
				hm.put("image", "logo");
			}

			hm.put("usdRate",Double.parseDouble((txtUsdRate.getValue().toString().isEmpty()?
					"1":txtUsdRate.getValue().toString().replaceAll(",", ""))));
			Window win = new ReportViewer(hm,"report/account/trialbal/trialAdjustedGroup.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",po.actionCheck);

			win.setStyleName("cwindow");
			this.getParent().getWindow().addWindow(win);
			win.setCaption("GROUP WISE TRIAL BALANCE :: "+sessionBean.getCompany());
		} 
		catch(Exception exp)
		{
			showNotification("Error :",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
		}
	}
}