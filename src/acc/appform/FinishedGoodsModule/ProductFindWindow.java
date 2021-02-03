package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class ProductFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private HorizontalLayout hLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String receiptItemId = "";

	private Label lblCategoryName=new Label("Party Name :");
	private ComboBox cmbCategoryName=new ComboBox() ;
	
	private Label lblProductionType=new Label("Production Type:");
	private ComboBox cmbProductionType=new ComboBox() ;

	/*private Label lblSubCategoryName=new Label("Sub-Category");
	private ComboBox cmbSubCategoryName=new ComboBox() ;*/

	private ArrayList<Label> lbProductId = new ArrayList<Label>();
	private ArrayList<Label> lbProductName = new ArrayList<Label>();
	private ArrayList<Label> lbCategory = new ArrayList<Label>();
	private ArrayList<Label> lbRate = new ArrayList<Label>();
	private ArrayList<Label> lbUnit = new ArrayList<Label>();

	private CommonButton cButton = new CommonButton( "",  "",  "",  "",  "",  "Find", "", "","","");

	private Label lblCountProduct=new Label();

	private String frmName;
	private SessionBean sessionBean;

	boolean isFind=false;

	public ProductFindWindow(SessionBean sessionBean,TextField txtReceiptSupplierId,String frmName)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("FIND FINISHED PRODUCT INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("700px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.frmName=frmName;
		this.setStyleName("cwindow");

		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
		tableDataAdding();
		cmbAddCategoryData();
		cmbProductionTypeData();
	}

	private void cmbProductionTypeData() 
	{
		cmbProductionType.removeAllItems();
		Transaction tx=null;

		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery("  select productTypeId,productTypeName from tbProductionType").list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbProductionType.addItem(element[0].toString());
				cmbProductionType.setItemCaption(element[0].toString(), (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}

	public void tableInitialise()
	{
		for(int i=0;i<7;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lbProductId.add(ar, new Label(""));
		lbProductId.get(ar).setWidth("100%");
		lbProductId.get(ar).setImmediate(true);
		lbProductId.get(ar).setHeight("15px");

		lbProductName.add(ar, new Label(""));
		lbProductName.get(ar).setWidth("100%");
		lbProductName.get(ar).setImmediate(true);

		lbCategory.add(ar, new Label(""));
		lbCategory.get(ar).setWidth("100%");
		lbCategory.get(ar).setImmediate(true);

		lbUnit.add(ar, new Label(""));
		lbUnit.get(ar).setWidth("100%");
		lbUnit.get(ar).setImmediate(true);

		lbRate.add(ar, new Label(""));
		lbRate.get(ar).setWidth("100%");
		lbRate.get(ar).setImmediate(true);

		table.addItem(new Object[]{lbProductId.get(ar),lbProductName.get(ar),lbCategory.get(ar),lbUnit.get(ar),lbRate.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptItemId = lbProductId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					System.out.println(receiptItemId);
					txtReceiptSupplierId.setValue(receiptItemId);
					windowClose();
				}
			}
		});

		cmbCategoryName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				isFind=true;
				if(cmbCategoryName.getValue()==null)
				{
					showNotification("Please Select Party Name ", Notification.TYPE_WARNING_MESSAGE);
					cmbCategoryName.focus();
				}

				else 
				{
					tableclear();
					tableDataAdding();
				}
			}
		});

		/*cmbCategoryName.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{

			}
		});*/
		cmbProductionType.addListener(new ValueChangeListener() {
			
			public void valueChange(ValueChangeEvent event) {
			
				if(cmbProductionType.getValue()!=null&&cmbCategoryName.getValue()!=null){
					if(cmbCategoryName.getValue()!=null){
						tableclear();
						productionTypeWiseData();
					}
					else{
						showNotification("Select Party Name ",Notification.TYPE_WARNING_MESSAGE);
					}
				}
			}
		});
	}
	private void productionTypeWiseData(){
		Transaction tx = null;
		String query = null;
		String category="";
		String subCategory="";

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			query="select vProductId,vProductName,vCategoryName,vUnitName,mDealerPrice,vProductionTypeId from tbFinishedProductInfo " +
					"where vCategoryId='"+cmbCategoryName.getValue()+"' and vProductionTypeId like '"+cmbProductionType.getValue()+"' ORDER by iAutoId";

			List list = session.createSQLQuery(query).list();			

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbProductId.get(i).setValue(element[0].toString());
					lbProductName.get(i).setValue(element[1].toString());

					lbCategory.get(i).setValue(element[2].toString());

					lbUnit.get(i).setValue(element[3].toString());
					lbRate.get(i).setValue(element[4].toString());

					if((i)==lbProductId.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}

				table.setColumnFooter("Product Name","Total Product = "+i);
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data Found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			//this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void tableclear()
	{
		for(int i=0; i<lbProductId.size(); i++)
		{
			lbProductId.get(i).setValue("");
			lbProductName.get(i).setValue("");
			lbCategory.get(i).setValue("");
			lbRate.get(i).setValue("");
			lbUnit.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = null;
		String category="";
		String subCategory="";

		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			if(isFind)
			{
				category=cmbCategoryName.getValue().toString();
				//subCategory = (cmbSubCategoryName.getValue()==null?"%":cmbSubCategoryName.getValue().toString());

				query =" select vProductId,vProductName,vCategoryName,vUnitName,mDealerPrice from tbFinishedProductInfo where vCategoryId='"+category+"' ORDER by iAutoId ";
			}
			else
			{
				query =" select vProductId,vProductName,vCategoryName,vUnitName,mDealerPrice from tbFinishedProductInfo ORDER by iAutoId ";
			}

			List list = session.createSQLQuery(query).list();			

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lbProductId.get(i).setValue(element[0].toString());
					lbProductName.get(i).setValue(element[1].toString());

					lbCategory.get(i).setValue(element[2].toString());

					lbUnit.get(i).setValue(element[3].toString());
					lbRate.get(i).setValue(element[4].toString());

					if((i)==lbProductId.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}

				table.setColumnFooter("Product Name","Total Product = "+i);
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data Found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
			//this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void windowClose()
	{
		this.close();
	}

	public void cmbAddCategoryData()
	{
		cmbCategoryName.removeAllItems();
		Transaction tx=null;
		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			List list=session.createSQLQuery("select vGroupId,partyName from tbPartyInfo where isActive = '1' order by partyName ").list();

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbCategoryName.addItem(element[0].toString());
				cmbCategoryName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	/*public void cmbAddSubcategoryData()
	{
		cmbSubCategoryName.removeAllItems();
		if(cmbCategoryName.getValue()!=null)
		{
			Transaction tx=null;
			try
			{
				Session session=SessionFactoryUtil.getInstance().getCurrentSession();
				tx=session.beginTransaction();
				List list=session.createSQLQuery(" Select SubGroup_Id,vSubCategoryName from tbProductSubCategory where Group_Id='"+cmbCategoryName.getValue().toString()+"' ").list();

				for(Iterator iter=list.iterator();iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					cmbSubCategoryName.addItem(element[0].toString());
					cmbSubCategoryName.setItemCaption(element[0].toString(), element[1].toString());
				}
			}
			catch(Exception exp)
			{
				this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}*/

	private void compInit()
	{

		cmbCategoryName.setNullSelectionAllowed(true);
		cmbCategoryName.setImmediate(true);
		cmbCategoryName.setWidth("250px");

		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("180px");

		mainLayout.setSpacing(true);
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("500px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("Product Id", Label.class, new Label());
		table.setColumnWidth("Product Id",40);

		table.addContainerProperty("Product Name", Label.class, new Label());
		table.setColumnWidth("Product Name",200);

		table.addContainerProperty("Party Name", Label.class, new Label());
		table.setColumnWidth("Party Name",210);

		table.addContainerProperty("Unit", Label.class, new Label());
		table.setColumnWidth("Unit",40);

		table.addContainerProperty("Unit Price", Label.class, new Label());
		table.setColumnWidth("Unit Price",70);

		table.setFooterVisible(true);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		hLayout.setSpacing(true);
		btnLayout.setSpacing(true);
		hLayout.addComponent(lblCategoryName);
		hLayout.addComponent(cmbCategoryName);
		hLayout.addComponent(lblProductionType);
		hLayout.addComponent(cmbProductionType);
		//hLayout.addComponent(cButton);*/
		mainLayout.addComponent(lblCountProduct);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(hLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}