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
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class RptFinishedGoodsInformation extends Window
{
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;

	private Label lblMould;
	private ComboBox cmbMould;
	private Label lblProduction;
	private ComboBox cmbProductionType;
	private Label lblFGName;
	private ComboBox cmbFGName;

	private CheckBox chkAllType = new CheckBox();
	private CheckBox chkAllParty = new CheckBox();
	private CheckBox chkAllFG = new CheckBox();

	private CheckBox chkpdf = new CheckBox("PDF");
	private CheckBox chkother = new CheckBox("Others");
	private HorizontalLayout chklayout = new HorizontalLayout();

	private Label lblLine;

	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");

	/*private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");*/

	public RptFinishedGoodsInformation(SessionBean sessionBean,String s)
	{
		this.sessionBean = sessionBean;
		this.setCaption("MASTER PRODUCT INFORMATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		this.setWidth("560px");
		this.setHeight("260px");
		buildMainLayout();		
		setContent(mainLayout);
		setEventAction();
		TypeData();
	}

	private void setEventAction()
	{
		cmbProductionType.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbProductionType.getValue()!=null)
				{
					String Type=cmbProductionType.getValue().toString();
					partyDataLoad(Type);
				}
			}
		});

		cmbMould.addListener(new ValueChangeListener()
		{			
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbMould.getValue()!=null)
				{
					String partyCode=cmbMould.getValue().toString();
					productNameData(partyCode);
				}
			}
		});

		previewButton.addListener(new ClickListener() 
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbProductionType.getValue()!=null || chkAllType.booleanValue())
				{
					if(cmbMould.getValue()!=null || chkAllParty.booleanValue())
					{
						if(cmbFGName.getValue()!=null || chkAllFG.booleanValue())
						{
							reportView(); 	
						}
						else
						{
							showNotification("Please Select Finished Goods Name",Notification.TYPE_WARNING_MESSAGE);	  	
						}
							
					}
					else
					{
						showNotification("Please Select Party Name",Notification.TYPE_WARNING_MESSAGE);	
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
					String Type="%";
					partyDataLoad(Type);
				}
				else
				{
					cmbProductionType.setEnabled(true);
					cmbProductionType.focus();
					cmbMould.removeAllItems();
				}
			}
		});

		chkAllParty.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event)
			{
				if(chkAllParty.booleanValue() && (cmbProductionType.getValue()!=null|| chkAllType.booleanValue()) )
				{
					cmbMould.setEnabled(false);
					cmbMould.setValue(null);
					String partycode="%";
					productNameData(partycode);
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

	private void productNameData(String partyCode) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String ptype="";
			if(cmbProductionType.getValue()!=null)
			{
				ptype=cmbProductionType.getValue().toString();
			}
			else
			{
				ptype="%";	
			}
			String sql = "select distinct vProductId,vProductName from tbFinishedProductInfo where vCategoryId like '"+partyCode+"' and  vProductionTypeId like '"+ptype+"' order by vProductName ";
			
			List<?> list = session.createSQLQuery(sql).list();
			cmbFGName.removeAllItems();
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
	
	private void partyDataLoad(String Type) 
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct  vCategoryId,vCategoryName from tbFinishedProductInfo where vProductionTypeId like '"+Type+"' "
					     +"order by vCategoryName ";
			List<?> list = session.createSQLQuery(sql).list();
			cmbMould.removeAllItems();
			for(Iterator<?> iter = list.iterator(); iter.hasNext();)
			{
				Object element[]=(Object[]) iter.next();
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
	

	private void cmbMouldData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = " select distinct a.vGroupId, a.partyName from tbPartyInfo a inner join" +
					" (select partyId from tbJobOrderInfo) b on b.partyId = a.vGroupId order by a.partyName";
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
	
	private void TypeData()
	{
		Session session = SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try
		{
			String sql = "select distinct  vProductionTypeId,vProductionTypeName from  tbFinishedProductInfo order by vProductionTypeName ";
					
			List<?> list = session.createSQLQuery(sql).list();
			cmbProductionType.removeAllItems();
			for(Iterator<?> iter = list.iterator();iter.hasNext();)
			{
				Object element[] = (Object[]) iter.next();
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
	
	
	private void reportView()
	{
		String productName = "";
		String cmbMould1 = "";
		String productionType="";
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();

		if(chkAllParty.booleanValue())
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
		
		if(chkAllType.booleanValue())
		{
			productionType="%";	
		}
		else
		{
			productionType=cmbProductionType.getValue().toString();	
		}
		try
		{
			HashMap<Object, Object> hm = new HashMap<Object, Object>();
			hm.put("parentType", "MASTER PRODUCT INFORMATION");
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("logo", sessionBean.getCompanyLogo());

			String query = "select a.vProductionTypeId,a.vProductionTypeName,a.vCategoryId,a.vCategoryName,a.vProductId,a.vProductName,vUnitName,a.mDealerPrice,a.weight,semiFgId,semiFgName, "
					       +"semiFgSubId,semiFgSubName,vSizeName as packetqty,b.stdWeight,b.qty,unitPrice,b.consumptionStage,a.vFgcode  from tbFinishedProductInfo a inner join tbFinishedProductDetailsNew b  "
					       +"on a.vProductId=b.fgId where a.vProductionTypeId like '"+productionType+"' and a.vCategoryId like '"+cmbMould1+"' and a.vProductId like '"+productName+"' "
					       		+ " order by a.vCategoryName,a.vProductionTypeName" ;

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
																	
				Window win = new ReportViewerNew(hm,"report/production/rptFgInformation.jasper",
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
		chkAllType.setWidth("-1px");
		chkAllType.setHeight("24px");
		mainLayout.addComponent(chkAllType , "top:30.0px;left:440.0px;");

		lblMould = new Label("Party Name :");
		lblMould.setImmediate(false);
		lblMould.setWidth("-1px");
		lblMould.setHeight("-1px");	
		mainLayout.addComponent(lblMould, "top:60.0px;left:40.0px;");

		cmbMould= new ComboBox();
		cmbMould.setImmediate(true);
		cmbMould.setWidth("260px");
		cmbMould.setHeight("24px");
		cmbMould.setNullSelectionAllowed(true);
		cmbMould.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbMould, "top:58.0px;left:170.0px;");

		chkAllParty = new CheckBox("All");
		chkAllParty.setImmediate(true);
		chkAllParty.setValue(false);
		chkAllParty.setWidth("-1px");
		chkAllParty.setHeight("24px");
		mainLayout.addComponent(chkAllParty, "top:60.0px;left:440.0px;");

		lblFGName = new Label("Master Product Name :");
		lblFGName.setImmediate(false);
		lblFGName.setWidth("-1px");
		lblFGName.setHeight("-1px");
		mainLayout.addComponent(lblFGName,"top:90.0px;left:40.0px;");

		cmbFGName = new ComboBox();
		cmbFGName.setImmediate(true);
		cmbFGName.setWidth("260px");
		cmbFGName.setHeight("24px");
		cmbFGName.setNullSelectionAllowed(true);
		cmbFGName.setNewItemsAllowed(false);
		cmbFGName.setEnabled(true);
		mainLayout.addComponent( cmbFGName, "top:88.0px;left:170.0px;");

		chkAllFG = new CheckBox("All");
		chkAllFG.setImmediate(true);
		chkAllFG.setValue(false);
		chkAllFG.setWidth("-1px");
		chkAllFG.setHeight("24px");
		mainLayout.addComponent(chkAllFG, "top:90.0px;left:440.0px;");

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
		mainLayout.addComponent(lblLine, "top:140.0px;left:0.0px;");

		previewButton.setWidth("80px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));

		mainLayout.addComponent(chklayout, "top:130.0px; left:220.0px");
		mainLayout.addComponent(previewButton,"top:160.opx; left:186.0px");
		mainLayout.addComponent(exitButton,"top:160.opx; left:280.0px");

		return mainLayout;
	}
}