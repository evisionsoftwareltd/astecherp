package com.reportform.FinishedGoodsModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportDate;
import com.common.share.ReportOption;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Component.Listener;
import com.vaadin.ui.Window.Notification;

public class RptCategoryAndSubCategoryWiseProductList extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblProductCategory;
	private ComboBox cmbProductCategory;
	private CheckBox CategoryAll;

	private Label lblProductSubCategory;
	private ComboBox cmbProductSubCategory;
	private CheckBox SubCategoryAll;
	String Category;


	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	public RptCategoryAndSubCategoryWiseProductList(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Product List Category & Sub-Category Wise :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		addCategoryName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		cmbProductCategory.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbProductSubCategory.removeAllItems();
				cmbProductSubCategory.setValue('%');
				if(cmbProductCategory.getValue()!=null)
				{
					cmbAddSubCategory();
				}
			}
		});

		SubCategoryAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(SubCategoryAll.booleanValue()==true)
				{
					cmbProductSubCategory.setValue('%');
					cmbProductSubCategory.setEnabled(false);
				}
				else
				{
					cmbProductSubCategory.setEnabled(true);
				}
			}
		});

		CategoryAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(CategoryAll.booleanValue()==true)
				{
					SubCategoryAll.setEnabled(false);
					SubCategoryAll.setValue(true);
					cmbProductCategory.setValue(null);
					cmbProductCategory.setEnabled(false);
					cmbProductSubCategory.setValue(null);
					cmbProductSubCategory.setEnabled(false);
				}
				else
				{
					cmbProductSubCategory.setEnabled(true);
					cmbProductCategory.setEnabled(true);
					SubCategoryAll.setEnabled(true);
					SubCategoryAll.setValue(false);
					
				}
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable()){
					if(cmbProductCategory.getValue()!=null || CategoryAll.booleanValue()==true ){
						if(SubCategoryAll.booleanValue()==true || cmbProductSubCategory.getValue()!=null){
							
							reportShow();

						}else{
							getParent().showNotification("Warning","Please provide Product Sub-Category",Notification.TYPE_WARNING_MESSAGE);
							cmbProductSubCategory.focus();
						}
					}else{
						getParent().showNotification("Warning","Please Select Product Category",Notification.TYPE_WARNING_MESSAGE);
						cmbProductCategory.focus();
					}
				}
				else{
					getParent().showNotification("You are not Permitted to Save",Notification.TYPE_WARNING_MESSAGE);
				}
			}


		});

		cButton.btnExit.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});
	}

	public void addCategoryName()
	{
		cmbProductCategory.removeAllItems();
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery(" select Group_Id,vCategoryName from tbProductCategory order by iCategoryCode  ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProductCategory.addItem(element[0]);
				cmbProductCategory.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbAddSubCategory()
	{
		cmbProductSubCategory.removeAllItems();
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery(" select SubGroup_Id,vSubCategoryName from  tbProductSubCategory where Group_Id='"+cmbProductCategory.getValue()+"' ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbProductSubCategory.addItem(element[0]);
				cmbProductSubCategory.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void reportShow()
	{
		ReportOption RadioBtn= new ReportOption(RadioBtnGroup.getValue().toString());
		String query=null;
		String activeFlag = null;
		String SubCategory="";
		String Category="";
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();

			if(CategoryAll.booleanValue()==true)
			{
				Category="%";
				SubCategory="%";
			}
			else
			{
				Category=cmbProductCategory.getValue().toString();
				if(SubCategoryAll.booleanValue()==true)
				{
					SubCategory="%";
				}
				else
				{
					SubCategory=cmbProductSubCategory.getValue().toString();
				}
			}
			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			//			hm.put("phone", sessionBean.getCompanyPhone());
			//			hm.put("email", sessionBean.getCompanyEmail());
			//			hm.put("fax", sessionBean.getCompanyFax());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("Date",reportTime.getTime);
			//			hm.put("userIp", sessionBean.getUserIp());

			query = " select vProductId,vProductName,vCategoryName,vSubCategoryName,vUnitName,mDealerPrice," +
					" mTradePrice,imageLocation from tbFinishedProductInfo " +
					" where vCategoryId like '"+Category+"' and vSubCategoryId " +
					" like '"+SubCategory+"' order by vCategoryId,vSubCategoryId ";
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/account/finishedGoods/RptCat&SUbCatWiseProductList.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",RadioBtn.Radio);

				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);
			}
			else
			{
				showNotification("Warning","There are no Data",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private boolean queryValueCheck(String sql)
	{
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			Iterator iter = session.createSQLQuery(sql).list().iterator();

			if (iter.hasNext()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		return false;
	}

	private void focusMove()
	{
		allComp.add(cmbProductCategory);
		allComp.add(cButton.btnPreview);

		new FocusMoveByEnter(this,allComp);
	}

	private AbsoluteLayout buildMainLayout()
	{
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("440px");
		setHeight("200px");

		// lblCategory
		lblProductCategory = new Label();
		lblProductCategory.setImmediate(false);
		lblProductCategory.setWidth("100.0%");
		lblProductCategory.setHeight("-1px");
		lblProductCategory.setValue("Category Name :");
		mainLayout.addComponent(lblProductCategory,"top:30.0px; left:30.0px;");

		// cmbProductCategory
		cmbProductCategory = new ComboBox();
		cmbProductCategory.setImmediate(false);
		cmbProductCategory.setWidth("200px");
		cmbProductCategory.setHeight("-1px");
		cmbProductCategory.setNullSelectionAllowed(true);
		cmbProductCategory.setImmediate(true);
		mainLayout.addComponent(cmbProductCategory, "top:28.0px; left:160.0px;");

		//CategoryAll
		CategoryAll = new CheckBox("All");
		CategoryAll.setHeight("-1px");
		CategoryAll.setWidth("-1px");
		CategoryAll.setImmediate(true);
		mainLayout.addComponent(CategoryAll, "top:30.0px; left:366.0px;");

		lblProductSubCategory = new Label();
		lblProductSubCategory.setImmediate(false);
		lblProductSubCategory.setWidth("100.0%");
		lblProductSubCategory.setHeight("-1px");
		lblProductSubCategory.setValue("Sub-category Name :");
		mainLayout.addComponent(lblProductSubCategory,"top:55.0px; left:30.0px;");

		// cmbProductCategory
		cmbProductSubCategory = new ComboBox();
		cmbProductSubCategory.setImmediate(false);
		cmbProductSubCategory.setWidth("200px");
		cmbProductSubCategory.setHeight("-1px");
		cmbProductSubCategory.setNullSelectionAllowed(true);
		cmbProductSubCategory.setImmediate(true);
		mainLayout.addComponent(cmbProductSubCategory, "top:53.0px; left:160.0px;");

		//CategoryAll
		SubCategoryAll = new CheckBox("All");
		SubCategoryAll.setHeight("-1px");
		SubCategoryAll.setWidth("-1px");
		SubCategoryAll.setImmediate(true);
		mainLayout.addComponent(SubCategoryAll, "top:55.0px; left:366.0px;");

		// optionGroup
		RadioBtnGroup = new OptionGroup("",type1);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setStyleName("horizontal");
		RadioBtnGroup.setValue("PDF");
		mainLayout.addComponent(RadioBtnGroup, "top:80.0px;left:160.0px;");


		mainLayout.addComponent(cButton,"top:120.opx; left:120.0px");

		return mainLayout;
	}
}
