package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Arrays;
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


public class RptFgInfo extends Window 
{
	private SessionBean sessionBean;
	private AbsoluteLayout mainLayout;

	ArrayList<Component> allComp = new ArrayList<Component>();

	private Label lblPartyCategory;
	private ComboBox cmbPartyCategory;
	private CheckBox PartyAll;

	private Label lblFinihedGoods;
	private ComboBox cmbFinishGoods;
	private CheckBox FinihedGoodsAll;
	String Category;


	private ReportDate reportTime = new ReportDate();
	private OptionGroup RadioBtnGroup;
	private static final List<String> type1=Arrays.asList(new String[]{"PDF","Other"});

	CommonButton cButton=new CommonButton("", "", "", "", "", "", "", "Preview", "", "Exit");

	public RptFgInfo(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setCaption("Product List Category Wise :: "+sessionBean.getCompany());
		this.setResizable(false);

		buildMainLayout();
		setContent(mainLayout);

		addProductName();
		setEventAction();
		focusMove();
	}

	public void setEventAction()
	{
		cmbPartyCategory.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				cmbFinishGoods.removeAllItems();
				cmbFinishGoods.setValue('%');
				if(cmbPartyCategory.getValue()!=null)
				{
					cmbAddFinishGoods();
				}
			}
		});

		FinihedGoodsAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(FinihedGoodsAll.booleanValue()==true)
				{
					cmbFinishGoods.setValue('%');
					cmbFinishGoods.setEnabled(false);
				}
				else
				{
					cmbFinishGoods.setEnabled(true);
				}
			}
		});

		PartyAll.addListener(new Listener()
		{
			public void componentEvent(Event event)
			{
				if(PartyAll.booleanValue()==true)
				{
					FinihedGoodsAll.setEnabled(true);
					FinihedGoodsAll.setValue(false);
					cmbPartyCategory.setValue(null);
					cmbPartyCategory.setEnabled(false);
					cmbFinishGoods.setValue(null);
					cmbFinishGoods.setEnabled(true);
				}
				else
				{
					cmbFinishGoods.setEnabled(false);
					cmbPartyCategory.setEnabled(true);
					FinihedGoodsAll.setEnabled(false);
					FinihedGoodsAll.setValue(true);
					
				}
			}
		});

		cButton.btnPreview.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(sessionBean.isSubmitable()){
					if(cmbPartyCategory.getValue()!=null || PartyAll.booleanValue()==true ){
						if(FinihedGoodsAll.booleanValue()==true || cmbFinishGoods.getValue()!=null){
							
							reportShow();

						}else{
							getParent().showNotification("Warning","Please provide Product Sub-Category",Notification.TYPE_WARNING_MESSAGE);
							cmbFinishGoods.focus();
						}
					}else{
						getParent().showNotification("Warning","Please Select Product Category",Notification.TYPE_WARNING_MESSAGE);
						cmbPartyCategory.focus();
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

	public void addProductName()
	{
		cmbPartyCategory.removeAllItems();
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			List<?> list = session.createSQLQuery("select partyCode, partyName from tbPartyInfo order by partyName").list();

			for(Iterator<?>iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbPartyCategory.addItem(element[0]);
				cmbPartyCategory.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp)
		{
			showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cmbAddFinishGoods()
	{
		cmbFinishGoods.removeAllItems();
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List list = session.createSQLQuery(" select vProductId,vProductName from tbFinishedProductInfo ORDER by vProductName").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbFinishGoods.addItem(element[0]);
				cmbFinishGoods.setItemCaption(element[0], element[1].toString());
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

			if(PartyAll.booleanValue()==true)
			{
				Category="%";
				SubCategory="%";
			}
			else
			{
				Category=cmbPartyCategory.getValue().toString();
				if(FinihedGoodsAll.booleanValue()==true)
				{
					SubCategory="%";
				}
				else
				{
					SubCategory=cmbFinishGoods.getValue().toString();
				}
			}
			HashMap hm = new HashMap();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("logo", sessionBean.getCompanyLogo());
			//			hm.put("phone", sessionBean.getCompanyPhone());
			//			hm.put("email", sessionBean.getCompanyEmail());
			//			hm.put("fax", sessionBean.getCompanyFax());
			hm.put("username", sessionBean.getUserName()+" "+sessionBean.getUserIp());
			hm.put("Date",reportTime.getTime);
			hm.put("parentType","Finished Good");
			//			hm.put("userIp", sessionBean.getUserIp());

			query = " select distinct vCategoryId, vCategoryName, vProductId, vProductName, vUnitName, finishForm, filmSize, color, noOfUps, productionTypeName, " +
					" structure, flatWidth, dDate, mDealerPrice  " +
					" from tbFinishedProductInfo a  inner join" +
					" tbproductionTypeDetails b on b.productionTypeId = a.productionType " +
					" where vCategoryId like '%' order by vCategoryName, vProductName";
			if(queryValueCheck(query))
			{
				hm.put("sql", query);

				Window win = new ReportViewer(hm,"report/production/rawProductInfoRpt.jasper",
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

			Iterator<?> iter = session.createSQLQuery(sql).list().iterator();

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
		allComp.add(cmbPartyCategory);
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
		lblPartyCategory = new Label();
		lblPartyCategory.setImmediate(false);
		lblPartyCategory.setWidth("100.0%");
		lblPartyCategory.setHeight("-1px");
		lblPartyCategory.setValue("Party Name :");
		mainLayout.addComponent(lblPartyCategory,"top:30.0px; left:30.0px;");

		// cmbPartyCategory
		cmbPartyCategory = new ComboBox();
		cmbPartyCategory.setImmediate(false);
		cmbPartyCategory.setWidth("200px");
		cmbPartyCategory.setHeight("-1px");
		cmbPartyCategory.setNullSelectionAllowed(true);
		cmbPartyCategory.setImmediate(true);
		mainLayout.addComponent(cmbPartyCategory, "top:28.0px; left:160.0px;");

		//PartyAll
		PartyAll = new CheckBox("All");
		PartyAll.setHeight("-1px");
		PartyAll.setWidth("-1px");
		PartyAll.setImmediate(true);
		mainLayout.addComponent(PartyAll, "top:30.0px; left:366.0px;");

		lblFinihedGoods = new Label();
		lblFinihedGoods.setImmediate(false);
		lblFinihedGoods.setWidth("100.0%");
		lblFinihedGoods.setHeight("-1px");
		lblFinihedGoods.setValue("Finish Goods Name :");
		mainLayout.addComponent(lblFinihedGoods,"top:55.0px; left:30.0px;");

		// cmbPartyCategory
		cmbFinishGoods = new ComboBox();
		cmbFinishGoods.setImmediate(false);
		cmbFinishGoods.setWidth("200px");
		cmbFinishGoods.setHeight("-1px");
		cmbFinishGoods.setNullSelectionAllowed(true);
		cmbFinishGoods.setImmediate(true);
		mainLayout.addComponent(cmbFinishGoods, "top:53.0px; left:160.0px;");

		//PartyAll
		FinihedGoodsAll = new CheckBox("All");
		FinihedGoodsAll.setHeight("-1px");
		FinihedGoodsAll.setWidth("-1px");
		FinihedGoodsAll.setImmediate(true);
		mainLayout.addComponent(FinihedGoodsAll, "top:55.0px; left:366.0px;");

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
