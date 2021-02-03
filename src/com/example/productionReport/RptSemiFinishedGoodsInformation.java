package com.example.productionReport;

import java.util.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.ReportViewer;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class RptSemiFinishedGoodsInformation extends Window
{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;

	private Label lblParty;
	private ComboBox cmbParty;
	private Label lblProduction;
	private ComboBox cmbProductionType;
	private Label lblFGName;
	private ComboBox cmbFGName;
	private Label lblStatus;
	private CheckBox chkAllType = new CheckBox();
	private CheckBox chkAllParty = new CheckBox();
	private CheckBox chkAllFG = new CheckBox();

	private CheckBox chkpdf = new CheckBox("PDF");
	private CheckBox chkother = new CheckBox("Others");
	private HorizontalLayout chklayout = new HorizontalLayout();

	private Label lblLine;

	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private OptionGroup opgStatus;
	private static final List<String> Optiontype=Arrays.asList(new String[]{"Active","Inactive"});
	

	/*private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");*/

	public RptSemiFinishedGoodsInformation(SessionBean sessionBean)
	{
		this.sessionBean = sessionBean;
		this.setCaption("SEMI FINISHED GOODS INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		this.setWidth("560px");
		this.setHeight("260px");
		buildMainLayout();		
		setContent(mainLayout);
		productionTypeDataLoad();
		setEventAction();
		productNameData("%");
	}
	private void setEventAction()
	{
		cmbProductionType.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProductionType.getValue()!=null)
				{
					String Id= cmbProductionType.getValue().toString();
					partyDataLoad(Id);
				}
			}
		});

		cmbParty.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbParty.getValue()!=null && (cmbProductionType.getValue()!=null || chkAllType.booleanValue()))
				{
					String partyId=cmbParty.getValue().toString();
					productNameData(partyId);
				}
			}
		});

		previewButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbProductionType.getValue()!=null || chkAllType.booleanValue())
				{
					if(cmbParty.getValue()!=null || chkAllParty.booleanValue())
					{
						if(cmbFGName.getValue()!=null || chkAllFG.booleanValue())
						{
							reportView(); 	
						}
						else
						{
							showNotification("Please Select Product Name",Notification.TYPE_WARNING_MESSAGE);		
						}
							
					}
					else
					{
						showNotification("Please Select party Name",Notification.TYPE_WARNING_MESSAGE);	
					}
					
				}
				else
				{
					showNotification("Please Select Production Type",Notification.TYPE_WARNING_MESSAGE);
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

		chkAllType.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllType.booleanValue())
				{
					cmbProductionType.setEnabled(false);
					cmbProductionType.setValue(null);
					String Id ="%";
					partyDataLoad(Id);
				}
				else
				{
					cmbProductionType.setEnabled(true);
					cmbProductionType.focus();
					cmbParty.removeAllItems();
					
				}
			}
		});

		chkAllParty.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllParty.booleanValue())
				{
					cmbParty.setEnabled(false);
					cmbParty.setValue(null);
					String Id="%";
					productNameData(Id);
					
				}
				else
				{
					cmbParty.setEnabled(true);
					cmbParty.focus();
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
			String sql = "select distinct productionTypeId,productionTypeName from tbSemiFgInfo order by productionTypeName";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbProductionType.addItem(element[0].toString());
				cmbProductionType.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	private void productNameData() 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select vProductId,vProductName,vUnitName from tbFinishedProductInfo" +
					" where vCategoryId='"+cmbParty.getValue()+"'";
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbFGName.addItem(element[0].toString());
				cmbFGName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}
	
	private void productNameData(String partyId) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String type="%";
			
			if(cmbProductionType.getValue()!=null)
			{
				type=cmbProductionType.getValue().toString();  
			}
			if(chkAllType.booleanValue())
			{
				type="%";	
			}
			
			String sql= " select semiFgCode,semiFgName,color from  tbSemiFgInfo where productionTypeId like '"+type+"' and partyCode like '"+partyId+"' "
			            +"order by semiFgName ";
			cmbFGName.removeAllItems();
			List<?> list = session.createSQLQuery(sql).list();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
				cmbFGName.addItem(element[0].toString());
				String caption=element[1].toString()+" # "+element[2].toString();
				cmbFGName.setItemCaption(element[0].toString(), caption);
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}

	/*private void cmbPartyData(S)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select distinct a.vGroupId, a.partyName from tbPartyInfo a inner join" +
					" (select partyId from tbJobOrderInfo) b on b.partyId = a.vGroupId order by a.partyName";
			List<?> list = session.createSQLQuery(sql).list();
			cmbParty.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbParty.addItem(element[0].toString());
				cmbParty.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{session.close();}
	}*/
	
	private void partyDataLoad(String ID)
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql ="select partyCode,partyName from tbSemiFgInfo where productionTypeId like '"+ID+"' order by partyName ";
			List<?> list = session.createSQLQuery(sql).list();
			cmbParty.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
				cmbParty.addItem(element[0].toString());
				cmbParty.setItemCaption(element[0].toString(), element[1].toString());
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
		String cmbparty = "";
		String productionType="";
		String status="Active";
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();

		if(chkAllParty.booleanValue())
		{
			cmbparty = "%";
		}
		else
		{
			cmbparty = cmbParty.getValue().toString();
		}

		if(chkAllFG.booleanValue())
		{
			productName ="%";
		}
		else
		{
			productName = cmbFGName.getValue().toString();
		}
		
		if(chkAllType.booleanValue())
		{
			productionType="%";	
		}
		else
		{
			productionType=cmbProductionType.getValue().toString();	
		}
		if(opgStatus.getValue().toString().equalsIgnoreCase("Inactive")){
			status="Inactive";
		}
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("parentType", "SEMI FINISHED GOODS INFORMATION");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String query = "select productionTypeId,productionTypeName,partyCode,partyName,semiFgCode,semiFgName,unit,stdWeight, isnull(color,'')color,status  from tbSemiFgInfo "
					      +" where productionTypeId like '"+productionType+"' and partyCode like '"+cmbparty+"' and semiFgCode like '"+productName+"' and status ='"+status+"'  order by semiFgName";

			hm.put("sql", query);
			
			List lst = session.createSQLQuery(query).list();
			
			if(!lst.isEmpty())
			{	
						int type=0;
						
						if(chkpdf.booleanValue())
						{
							type=1;
						}
						
						else
						{
							type=0;	
						}
						
				
				
				
				Window win = new ReportViewerNew(hm,"report/production/rptSemiFgInfo.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				
				win.setStyleName("cwindow");
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);	
			}
			else
			{
				showNotification("There Is No Data",Notification.TYPE_WARNING_MESSAGE);	
			}
			
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
		mainLayout.addComponent(lblProduction, "top:30.0px;left:40.0px;");

		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("260px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setNullSelectionAllowed(false);
		cmbProductionType.setNewItemsAllowed(false);
		cmbProductionType.setValue(null);	
		mainLayout.addComponent( cmbProductionType, "top:28.0px;left:170.0px;");

		chkAllType = new CheckBox("All");
		chkAllType.setImmediate(true);
		//chkAllType.setValue(true);
		chkAllType.setWidth("-1px");
		chkAllType.setHeight("24px");
		mainLayout.addComponent(chkAllType , "top:30.0px;left:440.0px;");

		lblParty = new Label("Party Name :");
		lblParty.setImmediate(false);
		lblParty.setWidth("-1px");
		lblParty.setHeight("-1px");	
		lblParty.setVisible(false);
		mainLayout.addComponent(lblParty, "top:60.0px;left:40.0px;");

		cmbParty= new ComboBox();
		cmbParty.setImmediate(true);
		cmbParty.setWidth("260px");
		cmbParty.setHeight("24px");
		cmbParty.setNullSelectionAllowed(true);
		cmbParty.setNewItemsAllowed(false);
		cmbParty.setVisible(false);
		mainLayout.addComponent( cmbParty, "top:58.0px;left:170.0px;");

		chkAllParty = new CheckBox("All");
		chkAllParty.setImmediate(true);
		chkAllParty.setValue(false);
		chkAllParty.setWidth("-1px");
		chkAllParty.setHeight("24px");
		chkAllParty.setValue(true);
		chkAllParty.setVisible(false);
		mainLayout.addComponent(chkAllParty, "top:60.0px;left:440.0px;");

		lblFGName = new Label("Semi Finished Goods :");
		lblFGName.setImmediate(false);
		lblFGName.setWidth("-1px");
		lblFGName.setHeight("-1px");
		mainLayout.addComponent(lblFGName, "top:60.0px;left:40.0px;");

		cmbFGName = new ComboBox();
		cmbFGName.setImmediate(true);
		cmbFGName.setWidth("260px");
		cmbFGName.setHeight("24px");
		cmbFGName.setNullSelectionAllowed(true);
		cmbFGName.setNewItemsAllowed(false);
		cmbFGName.setEnabled(true);
		mainLayout.addComponent( cmbFGName, "top:58.0px;left:170.0px;");

		chkAllFG = new CheckBox("All");
		chkAllFG.setImmediate(true);
		chkAllFG.setValue(false);
		chkAllFG.setWidth("-1px");
		chkAllFG.setHeight("24px");
		mainLayout.addComponent(chkAllFG, "top:60.0px;left:440.0px;");
		
		lblStatus = new Label();
		lblStatus.setImmediate(false);
		lblStatus.setWidth("-1px");
		lblStatus.setHeight("-1px");
		lblStatus.setValue("Status: ");
		mainLayout.addComponent(lblStatus, "top:100.0px;left:40.0px;");
	
		opgStatus=new OptionGroup("",Optiontype);
		opgStatus.setImmediate(true);
		opgStatus.setValue("Active");
		opgStatus.setStyleName("horizontal");
		mainLayout.addComponent(opgStatus, "top:100.0px;left:190.0px;");
		
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
		mainLayout.addComponent(lblLine, "top:160.0px;left:0.0px;");

		previewButton.setWidth("100px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));

		mainLayout.addComponent(chklayout, "top:130.0px; left:200.0px");
		mainLayout.addComponent(previewButton,"top:180.opx; left:186.0px");
		mainLayout.addComponent(exitButton,"top:180.opx; left:280.0px");

		return mainLayout;
	}
}