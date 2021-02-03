package com.example.productionReport;

import java.util.*;

import org.hibernate.Session;

import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptFinishedGoodsFormulation extends Window
{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;

	private Label lblMould;
	private ComboBox cmbMould;
	private Label lblProduction;
	private ComboBox cmbProduction;
	private Label lblFGName;
	private ComboBox cmbFGName;

	private CheckBox chkAllType = new CheckBox();
	private CheckBox chkAllMould = new CheckBox();
	private CheckBox chkAllFG = new CheckBox();

	private CheckBox chkpdf = new CheckBox("PDF");
	private CheckBox chkother = new CheckBox("Others");
	private HorizontalLayout chklayout = new HorizontalLayout();
	private Label lblStatus;
	private Label lblLine;
	private OptionGroup opgStatus;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Active","Inactive"});
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");

	private OptionGroup opgFg;
	private static final List<String> OptiontypeFg=Arrays.asList(new String[]{"FG","SEMI FG","All","NOT ASSIGN"});

	/*private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");*/

	public RptFinishedGoodsFormulation(SessionBean sessionBean,String s)
	{
		this.sessionBean = sessionBean;
		this.setCaption("PRODUCT FORMULATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		this.setWidth("560px");
		this.setHeight("330px");

		buildMainLayout();		
		setContent(mainLayout);

		productionTypeDataLoad();
		cmbMouldData();
		setEventAction();
	}

	private void setEventAction()
	{

		cmbMould.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbMould.getValue()!=null)
				{
					productNameData(cmbMould.getValue().toString());
				}
			}
		});

		previewButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbMould.getValue()!=null || chkAllMould.booleanValue())
				{
					reportView(); 
				}
				else
				{
					showNotification("Please Select Party Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		exitButton.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				close();
			}
		});

		chkAllMould.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllMould.booleanValue())
				{
					cmbMould.setEnabled(false);
					cmbMould.setValue(null);	
					productNameData("%");
				}
				else
				{
					cmbMould.setEnabled(true);
					cmbMould.focus();
					cmbFGName.removeAllItems();
				}
			}
		});

		chkAllFG.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllFG.booleanValue())
				{
					cmbFGName.setEnabled(false);
					cmbFGName.setValue(null);					
				}
				else
				{
					cmbFGName.setEnabled(true);
					cmbFGName.focus();
				}
			}
		});

		chkpdf.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkpdf.booleanValue()==true)
				{
					chkother.setValue(false);
				}
				else
				{
					chkother.setValue(true);
				}
			}
		});

		chkother.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkother.booleanValue()==true)
				{
					chkpdf.setValue(false);
				}
				else
				{
					chkpdf.setValue(true);
				}
			}
		});
	}

	private void productionTypeDataLoad()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select productTypeId,productTypeName from tbProductionType";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbProduction.addItem(element[0].toString());
				cmbProduction.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void productNameData(String mouldName) 
	{
		cmbFGName.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{

			String sql = 	 "select fGCode,b.semiFgName,b.color from tbFinishedGoodsStandardInfo "
					+"a inner join tbSemiFgInfo b on  a.fGCode=b.semiFgCode "
					+ "where mouldName like '"+mouldName+"' ";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbFGName.addItem(element[0].toString());
				cmbFGName.setItemCaption(element[0].toString(), element[1].toString()+" # "+element[2].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+" ProductNameData",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void cmbMouldData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct mouldName,isnull((select mouldName from tbmouldInfo where mouldid=a.mouldName),0)from tbFinishedGoodsStandardInfo a where a.mouldName is not null ";
			List<?> list = session.createSQLQuery(sql).list();
			cmbMould.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbMould.addItem(element[0].toString());
				cmbMould.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void reportView()
	{
		String productName = "";
		String cmbMould1 = "";
		String status="Active";
		String fgFlag="";
		String query="";
		if(chkAllMould.booleanValue())
		{
			cmbMould1 = "%";
		}
		else
		{
			cmbMould1 = cmbMould.getValue().toString();
		}

		if(chkAllFG.booleanValue())
		{
			productName ="%";
		}
		else
		{
			productName = cmbFGName.getValue().toString();
		}

		if(opgStatus.getValue().toString().equalsIgnoreCase("Inactive")){
			status="Inactive";
		}
		if(opgFg.getValue().toString().equalsIgnoreCase("SEMI FG")){
			fgFlag="NO";
		}
		else if(opgFg.getValue().toString().equalsIgnoreCase("FG")){
			fgFlag="YES";
		}
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("parentType", "PRODUCT FORMULATION");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());
			
			if (opgFg.getValue().toString().equalsIgnoreCase("SEMI FG") || opgFg.getValue().toString().equalsIgnoreCase("FG") )
			{
				
				 query="select c.status,a.partyCode,(select partyName from tbPartyInfo where vGroupId=a.partyCode)partyName,a.fGCode,  c.semiFgName+' #  '+c.color as semiFgName, "+
							"c.stdWeight,b.RawItemCode,(select vRawItemName from tbRawItemInfo where vRawItemCode=b.RawItemCode)rawItemName,  "+
							"b.unit,b.percentage,b.Qty,a.mouldName as mouldId,(select mouldName from tbmouldInfo where mouldid=a.mouldName)mouldName,isnull(isFg,'')isFg from tbFinishedGoodsStandardInfo a   "+
							"inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo   "+
							"inner join tbSemiFgInfo c on a.fGCode=c.semiFgCode where a.declarationDate=   "+
							"(select MAX(declarationDate) from tbFinishedGoodsStandardInfo where fGCode=a.fGCode  and mouldName=a.mouldName)" +
							"and a.mouldName like '"+cmbMould1+"' and a.fGCode like '"+productName+"' and c. status ='"+status+"' and isFg like '"+fgFlag+"'   order by c.semiFgName,c.color,mouldName";
						
			}
			else if(opgFg.getValue().toString().equalsIgnoreCase("All"))
			{
				
				 query="select c.status,a.partyCode,(select partyName from tbPartyInfo where vGroupId=a.partyCode)partyName,a.fGCode,  c.semiFgName+' #  '+c.color as semiFgName, "+
							"c.stdWeight,b.RawItemCode,(select vRawItemName from tbRawItemInfo where vRawItemCode=b.RawItemCode)rawItemName,  "+
							"b.unit,b.percentage,b.Qty,a.mouldName as mouldId,(select mouldName from tbmouldInfo where mouldid=a.mouldName)mouldName,isnull(isFg,'')isFg from tbFinishedGoodsStandardInfo a   "+
							"inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo   "+
							"inner join tbSemiFgInfo c on a.fGCode=c.semiFgCode where a.declarationDate=   "+
							"(select MAX(declarationDate) from tbFinishedGoodsStandardInfo where fGCode=a.fGCode  and mouldName=a.mouldName)" +
							"and a.mouldName like '"+cmbMould1+"' and a.fGCode like '"+productName+"' and c. status ='"+status+"' and (isFg like 'YES' or isFg like 'NO' ) order by c.semiFgName,c.color,mouldName";
							
				
			}

			
			

			 else
			{
				query="select c.status,a.partyCode,(select partyName from tbPartyInfo where vGroupId=a.partyCode)partyName,a.fGCode,  "+
						" c.semiFgName+' #  '+c.color as semiFgName, c.stdWeight,b.RawItemCode,(select vRawItemName from tbRawItemInfo  "+
						"  where vRawItemCode=b.RawItemCode)rawItemName,  b.unit,b.percentage,b.Qty,a.mouldName as mouldId,  "+
						" (select mouldName from tbmouldInfo where mouldid=a.mouldName)mouldName,isnull(isFg,'')isFg   "+
						" from tbFinishedGoodsStandardInfo a   inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo  "+  
						" inner join tbSemiFgInfo c on a.fGCode=c.semiFgCode where a.declarationDate=   (select MAX(declarationDate)  "+
						"  from tbFinishedGoodsStandardInfo where fGCode=a.fGCode  and mouldName=a.mouldName)and a.mouldName like '"+cmbMould1+"'   "+
						"  and a.fGCode like '"+productName+"' and c. status ='"+status+"' and isFg not in('YES','NO') or isFg is null   order by c.semiFgName,c.color,mouldName";
			}
			
			 System.out.println(query);
			
			
			/* else(opgFg.getValue().toString().equalsIgnoreCase("NOT ASSIGN"))
				{
					query="select c.status,a.partyCode,(select partyName from tbPartyInfo where vGroupId=a.partyCode)partyName,a.fGCode,  "+
							" c.semiFgName+' #  '+c.color as semiFgName, c.stdWeight,b.RawItemCode,(select vRawItemName from tbRawItemInfo  "+
							"  where vRawItemCode=b.RawItemCode)rawItemName,  b.unit,b.percentage,b.Qty,a.mouldName as mouldId,  "+
							" (select mouldName from tbmouldInfo where mouldid=a.mouldName)mouldName,isnull(isFg,'')isFg   "+
							" from tbFinishedGoodsStandardInfo a   inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo  "+  
							" inner join tbSemiFgInfo c on a.fGCode=c.semiFgCode where a.declarationDate=   (select MAX(declarationDate)  "+
							"  from tbFinishedGoodsStandardInfo where fGCode=a.fGCode  and mouldName=a.mouldName)and a.mouldName like '"+cmbMould1+"'   "+
							"  and a.fGCode like '"+productName+"' and c. status ='"+status+"' and isFg not in('YES','NO') or isFg is null   order by c.semiFgName,c.color,mouldName";
				}
				*/
			
			

			hm.put("sql", query);
			Window win = new ReportViewer(hm,"report/production/rawProductFormulation.jasper",
					this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
					this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
					this.getWindow().getApplication().getURL()+"VAADIN/applet",true);

			win.setStyleName("cwindow");
			win.setCaption("Project Report");
			this.getParent().getWindow().addWindow(win);
		}
		catch(Exception exp)
		{
			showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		lblProduction = new Label("Production Type :");
		lblProduction.setImmediate(false);
		lblProduction.setWidth("-1px");
		lblProduction.setHeight("-1px");
		lblProduction.setVisible(false);
		mainLayout.addComponent(lblProduction, "top:45.0px;left:40.0px;");

		cmbProduction = new ComboBox();
		cmbProduction.setImmediate(true);
		cmbProduction.setEnabled(false);
		cmbProduction.setWidth("260px");
		cmbProduction.setHeight("24px");
		cmbProduction.setNullSelectionAllowed(false);
		cmbProduction.setNewItemsAllowed(false);
		cmbProduction.setValue(null);	
		cmbProduction.setVisible(false);
		mainLayout.addComponent( cmbProduction, "top:43.0px;left:170.0px;");

		chkAllType = new CheckBox("All");
		chkAllType.setImmediate(false);
		chkAllType.setValue(true);
		chkAllType.setWidth("-1px");
		chkAllType.setHeight("24px");
		chkAllType.setVisible(false);
		mainLayout.addComponent(chkAllType , "top:45.0px;left:440.0px;");

		lblMould = new Label("Mould Name :");
		lblMould.setImmediate(false);
		lblMould.setWidth("-1px");
		lblMould.setHeight("-1px");	
		mainLayout.addComponent(lblMould, "top:75.0px;left:40.0px;");

		cmbMould= new ComboBox();
		cmbMould.setImmediate(true);
		cmbMould.setWidth("260px");
		cmbMould.setHeight("24px");
		cmbMould.setNullSelectionAllowed(true);
		cmbMould.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbMould, "top:73.0px;left:170.0px;");

		chkAllMould = new CheckBox("All");
		chkAllMould.setImmediate(true);
		chkAllMould.setValue(false);
		chkAllMould.setWidth("-1px");
		chkAllMould.setHeight("24px");
		mainLayout.addComponent(chkAllMould, "top:75.0px;left:440.0px;");

		lblFGName = new Label("Semi FG Name :");
		lblFGName.setImmediate(false);
		lblFGName.setWidth("-1px");
		lblFGName.setHeight("-1px");
		mainLayout.addComponent(lblFGName, "top:105.0px;left:40.0px;");

		cmbFGName = new ComboBox();
		cmbFGName.setImmediate(true);
		cmbFGName.setWidth("260px");
		cmbFGName.setHeight("24px");
		cmbFGName.setNullSelectionAllowed(true);
		cmbFGName.setNewItemsAllowed(false);
		cmbFGName.setEnabled(true);
		mainLayout.addComponent( cmbFGName, "top:103.0px;left:170.0px;");

		chkAllFG = new CheckBox("All");
		chkAllFG.setImmediate(true);
		chkAllFG.setValue(false);
		chkAllFG.setWidth("-1px");
		chkAllFG.setHeight("24px");
		mainLayout.addComponent(chkAllFG, "top:105.0px;left:440.0px;");

		lblStatus = new Label();
		lblStatus.setImmediate(false);
		lblStatus.setWidth("-1px");
		lblStatus.setHeight("-1px");
		lblStatus.setValue("Status: ");
		mainLayout.addComponent(lblStatus, "top:135.0px;left:40.0px;");

		opgStatus=new OptionGroup("",Optiontype);
		opgStatus.setImmediate(true);
		opgStatus.setValue("Active");
		opgStatus.setStyleName("horizontal");
		mainLayout.addComponent(opgStatus, "top:135.0px;left:200.0px;");

		opgFg=new OptionGroup("",OptiontypeFg);
		opgFg.setImmediate(true);
		opgFg.setValue("FG");
		opgFg.setStyleName("horizontal");
		mainLayout.addComponent(opgFg, "top:165.0px;left:200.0px;");


		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:210.0px;left:0.0px;");

		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));

		mainLayout.addComponent(chklayout, "top:195.0px; left:220.0px");
		mainLayout.addComponent(previewButton,"top:230.opx; left:186.0px");
		mainLayout.addComponent(exitButton,"top:230.opx; left:285.0px");

		return mainLayout;
	}
}