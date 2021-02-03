package acc.appform.asset;

import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

public class SearchAssetPurchase extends Window
{	
	private TextField voucherNo;
	private TextField assetId;
	private VerticalLayout mainLayout = new VerticalLayout();
	private Table table = new Table();
	
	private ArrayList<Label> vNo = new ArrayList<Label>();
	private ArrayList<Label> aId = new ArrayList<Label>();
	private ArrayList<Label> aName = new ArrayList<Label>();
	private ArrayList<Label> lName = new ArrayList<Label>();
	
	private SessionBean sessionBean = new SessionBean();
	public SearchAssetPurchase(SessionBean sessionBean,TextField voucherNo,TextField assetId)
	{
		this.sessionBean = sessionBean;
		this.voucherNo = voucherNo;
		this.assetId = assetId;
		this.setCaption("FIXED ASSET PURCHASE SEARCH :: "+sessionBean.getCompany());
		this.setWidth("600px");
		this.setResizable(false);		
		table.setWidth("550px");
        table.setHeight("255px");
        
        table.addContainerProperty("Voucher No", Label.class, new Label());
        table.setColumnWidth("Voucher No", 100);
        
        table.addContainerProperty("Asset Id", Label.class, new Label());
        table.setColumnWidth("Asset Id", 65);
        
        table.addContainerProperty("Asset Name", Label.class, new Label());
        table.setColumnWidth("Asset Name", 230);
        
        table.addContainerProperty("Ledger Name", Label.class, new Label());
        table.setColumnWidth("Ledger Name", 230);
        
        table.addListener(new ItemClickListener() 
        {
            public void itemClick(ItemClickEvent event) 
            {
                tabRowSelect(event);
            }
        });
        
        mainLayout.addComponent(table);
        this.addComponent(mainLayout);
        setStyleName("cwindow");
        tableInitialise();
	}
	private void tabRowSelect(ItemClickEvent event){
		if (event.isDoubleClick()){
			voucherNo.setValue(vNo.get(Integer.valueOf(event.getItemId()+"")));
			assetId.setValue(aId.get(Integer.valueOf(event.getItemId()+"")));
			this.close();
		}
	}
	private void tableInitialise(){
		table.removeAllItems();
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List led = session.createSQLQuery("SELECT VoucherNo,AssetID,vDescription,vLedgerName FROM tbFixedAsset WHERE [Type] = 'P' AND CompanyId = '"+ sessionBean.getCompanyId() +"' order by Convert(Numeric,Substring(VoucherNo,7,11))").list();
			int i = 0;
			for(Iterator iter = led.iterator(); iter.hasNext();){
				Object[] element = (Object[]) iter.next();
				
				vNo.add(i, new Label());
				vNo.get(i).setWidth("100px");
				vNo.get(i).setValue(element[0]);
				
				aId.add(i, new Label());
				aId.get(i).setWidth("65px");
				aId.get(i).setValue(element[1]);
				
				aName.add(i, new Label());
				aName.get(i).setWidth("230px");
				aName.get(i).setValue(element[2]);
				
				lName.add(i, new Label());
				lName.get(i).setWidth("230px");
				lName.get(i).setValue(element[3]);
				
				table.addItem(new Object[]{vNo.get(i),aId.get(i),aName.get(i),lName.get(i)},i); 
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
