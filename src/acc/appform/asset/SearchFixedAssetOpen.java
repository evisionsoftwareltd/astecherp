package acc.appform.asset;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

public class SearchFixedAssetOpen extends Window
{
	private TextField assetId;
	private TextField ledgerId;
	private VerticalLayout mainLayout = new VerticalLayout();
	private Table table = new Table();
	private ArrayList<Label> aId = new ArrayList<Label>();
	private ArrayList<Label> aName = new ArrayList<Label>();
	private ArrayList<Label> dMethod = new ArrayList<Label>();
	private ArrayList<TextRead> dep = new ArrayList<TextRead>();
	private ArrayList<TextRead> depOpBal = new ArrayList<TextRead>();
	private ArrayList<TextRead> AssetOpBal = new ArrayList<TextRead>();
	private SessionBean sessionBean = new SessionBean();
	private ComboBox cmbGroup,cmbSubGroup;
	private NativeButton findButton = new NativeButton("Find");
	DecimalFormat df=new DecimalFormat("#0.00");
	
	public SearchFixedAssetOpen(SessionBean sessionBean,TextField assetId)
	{
		this.sessionBean = sessionBean;
		this.assetId = assetId;
		this.setCaption("FIXED ASSET OPENING SEARCH :: "+sessionBean.getCompany());
		this.setWidth("750px");
		this.setResizable(false);
		
		cmbGroup = new ComboBox("Group: ");
		cmbGroup.setWidth("230px");
		cmbGroup.setHeight("24px");
		cmbGroup.setImmediate(true);
		cmbGroup.setNullSelectionAllowed(true);
		cmbGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		cmbSubGroup = new ComboBox("SubGroup: ");
		cmbSubGroup.setWidth("230px");
		cmbSubGroup.setHeight("24px");
		cmbSubGroup.setImmediate(true);
		cmbSubGroup.setNullSelectionAllowed(true);
		cmbSubGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		
		table.setWidth("700px");
        table.setHeight("255px");
        
        table.addContainerProperty("Asset Id", Label.class, new Label());
        table.setColumnWidth("Asset Id", 65);
        
        table.addContainerProperty("Ledger Name", Label.class, new Label());
        table.setColumnWidth("Ledger Name", 220);
        
        table.addContainerProperty("Dep. Method", Label.class, new Label());
        table.setColumnWidth("Dep. Method", 120);
        
        table.addContainerProperty("Dep.%", TextRead.class, new TextRead(1));
        table.setColumnWidth("Dep.%", 55);
        
        table.addContainerProperty("Asset Op. Bal", TextRead.class, new TextRead(1));
        table.setColumnWidth("Asset Op. Bal", 75);
        
        table.addContainerProperty("Dep. Op Bal", TextRead.class, new TextRead(1));
        table.setColumnWidth("Dep. Op Bal", 75);
        
        table.addListener(new ItemClickListener() 
        {
            public void itemClick(ItemClickEvent event) 
            {
                tabRowSelect(event);
            }
        });
        HorizontalLayout hrLayout=new HorizontalLayout();
        hrLayout.setSpacing(true);
        hrLayout.addComponent(cmbGroup);
        hrLayout.addComponent(cmbSubGroup);
        hrLayout.addComponent(findButton);
        
        mainLayout.addComponent(hrLayout);
        mainLayout.addComponent(table);
        this.addComponent(mainLayout);
        setStyleName("cwindow");
        tableInitialise();
        groupInitialise();
        
        cmbGroup.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				subgroupInitialise();
			}
		});
        findButton.addListener(new ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				tableInitialise();
			}
		});
	}
	private void subgroupInitialise()
	{
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			try
			{
				cmbSubGroup.removeAllItems();
			}
			catch(Exception e)
			{
				
			}
			
			tx = session.beginTransaction();
			List g = session.createSQLQuery("SELECT sub_Group_Id,sub_Group_Name FROM TbSub_Group WHERE group_Id = '"+cmbGroup.getValue()+"'").list();

			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				cmbSubGroup.addItem(element[0].toString());
				cmbSubGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
			//subGroup.setNullSelectionAllowed(true);				
		}
		catch(Exception exp)
		{
			this.getParent().showNotification("Error ",exp.toString() ,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void groupInitialise()
	{
		try
		{
			Transaction tx = null;
			
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			List g = session.createSQLQuery("SELECT group_Id,group_Name FROM TbMain_Group WHERE head_Id = 'A1'").list();
			
			try
			{
				cmbGroup.removeAllItems();
			}
			catch(Exception e)
			{
				
			}
			for (Iterator iter = g.iterator(); iter.hasNext();) 
			{
				Object[] element = (Object[]) iter.next();
				
				cmbGroup.addItem(element[0].toString());
				cmbGroup.setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception exp)
		{
			//System.out.println(exp);
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private void tabRowSelect(ItemClickEvent event){
		if (event.isDoubleClick()){
			assetId.setValue(aId.get(Integer.valueOf(event.getItemId()+"")));
			this.close();
		}
	}
	private void tableInitialise(){
		String group="%",subGroup="%";
		if(cmbGroup.getValue()!=null){
			group=cmbGroup.getValue().toString();
		}
		if(cmbSubGroup.getValue()!=null){
			subGroup=cmbSubGroup.getValue().toString();
		}
		table.removeAllItems();
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List led = session.createSQLQuery("SELECT AssetID,vLedgerName,vDepreciationSystem,iDepreciationPer,AssetDrAmount,DepreciationCrAmount FROM tbFixedAsset WHERE vGroupID like '"+group+"' and vSubGroupID like '"+subGroup+"' and [Type] = 'O' AND CompanyId = '"+ sessionBean.getCompanyId() +"' order by vDescription").list();
			int i = 0;
			for(Iterator iter = led.iterator(); iter.hasNext();){
				Object[] element = (Object[]) iter.next();
				
				aId.add(i, new Label());
				aId.get(i).setWidth("65px");
				aId.get(i).setValue(element[0]);
				
				aName.add(i, new Label());
				aName.get(i).setWidth("220px");
				aName.get(i).setValue(element[1]);
				
				dMethod.add(i, new Label());
				dMethod.get(i).setWidth("120px");
				dMethod.get(i).setValue(element[2]);
				
				dep.add(i, new TextRead(1));
				dep.get(i).setWidth("50px");
				dep.get(i).setValue(element[3]);
				
				depOpBal.add(i, new TextRead(1));
				depOpBal.get(i).setWidth("75px");
				depOpBal.get(i).setValue(df.format(element[4]));
				
				AssetOpBal.add(i, new TextRead(1));
				AssetOpBal.get(i).setWidth("75px");
				AssetOpBal.get(i).setValue(df.format(element[5]));
				
				table.addItem(new Object[]{aId.get(i),aName.get(i),dMethod.get(i),dep.get(i),depOpBal.get(i),AssetOpBal.get(i)},i); 
				i++;
			}
			
		}catch(Exception exp){
			this.getParent().showNotification(
                    "Error",
                    exp+"",
                    Notification.TYPE_ERROR_MESSAGE);
		}
	}
}
