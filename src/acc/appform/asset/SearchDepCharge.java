package acc.appform.asset;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;
import java.util.Formatter;

@SuppressWarnings("serial")
public class SearchDepCharge extends Window
{
	CommonButton button = new CommonButton("", "", "", "", "", "Find", "", "", "", "");
	private SessionBean sessionBean;
	private VerticalLayout mainLayout = new VerticalLayout();
	private HorizontalLayout horLayout = new HorizontalLayout();
	private GridLayout topGrid = new GridLayout(1,1);
	private DateField fromDate = new DateField();
	private DateField toDate = new DateField();
	private Table table = new Table();
	private ArrayList<Label> vNo = new ArrayList<Label>();
	private ArrayList<Label> vDate = new ArrayList<Label>();
	private ArrayList<Label> ledger = new ArrayList<Label>();
	private ArrayList<Label> narration = new ArrayList<Label>();
	private ArrayList<Label>  depAmt = new ArrayList<Label>();
	private ArrayList<Label>  ttlValue = new ArrayList<Label>();
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dF = new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
	private TextField voucherNo;
	private Formatter fmt = new Formatter();
	
	public SearchDepCharge(SessionBean sessionBean,TextField voucherNo)
	{
		this.voucherNo = voucherNo;
		this.sessionBean = sessionBean;
		this.setCaption("DEPRECIATION CHARGE SEARCH :: "+this.sessionBean.getCompany());
		this.setWidth("700px");
		this.setResizable(false);
		table.setWidth("650px");
        table.setHeight("255px");
        //table.addContainerProperty("Date", Label.class, new Label(),null,null,Table.ALIGN_CENTER);
        //table.setColumnWidth("Date",70);
        table.addContainerProperty("Voucher No", Label.class, new Label());
        table.setColumnWidth("Voucher No", 65);
        table.addContainerProperty("V. Date", Label.class, new Label());
        table.setColumnWidth("V. Date", 50);
        table.addContainerProperty("Asset Name", Label.class, new Label());
        table.setColumnWidth("Asset Name", 150);
        table.addContainerProperty("Narration", Label.class, new Label());
        table.setColumnWidth("Narration", 220);
        table.addContainerProperty("Dep Amount", Label.class, new Label());
        table.setColumnWidth("Dep Amount", 75);
        table.addContainerProperty("Total Value", Label.class, new Label());
        table.setColumnWidth("Total Value", 75);
        
        table.addListener(new ItemClickListener() 
        {
            public void itemClick(ItemClickEvent event) 
            {
                tabRowSelect(event);
            }
        });

        
        horLayout.addComponent(new Label("From Date :"));
        
        fromDate.setValue(new java.util.Date());
        fromDate.setResolution(PopupDateField.RESOLUTION_DAY);
        fromDate.setDateFormat("dd-MM-yy");
        fromDate.setInvalidAllowed(false);
        fromDate.setImmediate(true);
        horLayout.addComponent(fromDate);
        
        horLayout.addComponent(new Label("To Date :"));
        toDate.setValue(new java.util.Date());
        toDate.setResolution(PopupDateField.RESOLUTION_DAY);
        toDate.setDateFormat("dd-MM-yy");
        toDate.setInvalidAllowed(false);
        toDate.setImmediate(true);
        
        horLayout.addComponent(toDate);
        horLayout.addComponent(button.btnFind);
        button.btnFind.addListener( new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {
            	if(chkDate())
            	findBtnAction(event);
            }
        });
        horLayout.setSpacing(true);
        
        topGrid.addComponent(horLayout);
        
        HorizontalLayout tabLayout = new HorizontalLayout();
        tabLayout.addComponent(table);
        mainLayout.addComponent(topGrid);
		mainLayout.setComponentAlignment(topGrid, Alignment.TOP_CENTER);
		
		mainLayout.addComponent(tabLayout);
        this.addComponent(mainLayout);
        setStyleName("cwindow");
        
//        ComponentContainer ob[] = {horLayout};
//        new FocusMoveByEnter(this,ob);
        fromDate.focus();
	}
	
	@SuppressWarnings("deprecation")
	private boolean chkDate()
	{
		if(Date.parse(dF.format(fromDate.getValue())+"") <= Date.parse(dF.format(toDate.getValue())+""))
		{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String f = session.createSQLQuery("Select  [dbo].[dateSelect]('"+dateFormatter.format(fromDate.getValue())+"','"+dateFormatter.format(toDate.getValue())+"')").list().iterator().next().toString();
			System.out.println(f);
			if (f.equals("1"))	

				//			if((Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(fromDate.getValue()))>= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalOpenDate())))
				//					&&
				//					(Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(toDate.getValue()))<= Date.parse(new SimpleDateFormat("yyyy/MM/dd").format(sessionBean.getFiscalCloseDate()))))
			{
				return true;
			}
			else
			{
				this.getParent().showNotification("","From date or To Date are not valid. From/To date must be within same working fiscal year.",Notification.TYPE_WARNING_MESSAGE);
				return false;
			}
		}
		else
		{
			this.getParent().showNotification("","From date can not be greater then to date. Please verify the date range.",Notification.TYPE_WARNING_MESSAGE);
			return false;
		}
	}
	
	private void tabRowSelect(ItemClickEvent event)
	{
		if (event.isDoubleClick())
		{			
			//this.getParent().showNotification("Selected: " + vNo[Integer.valueOf(event.getItemId()+"")]);
			//voucherNo.setValue(vNo[Integer.valueOf(event.getItemId()+"")]);
			voucherNo.setValue(vNo.get(Integer.valueOf(event.getItemId()+"")));
			this.close();
		}
	}
	private void findBtnAction(ClickEvent e){
		String sql = "SELECT [Voucher_No],[Date],[AssetName],[Narration],[Depreciation],[TotalValue],[AQDate] FROM "+
		"tbDepreciationDetails WHERE Date >= '"+dateFormatter.format(fromDate.getValue())+
		"' AND Date <= '"+dateFormatter.format(toDate.getValue())+"' AND CompanyId = '"+ sessionBean.getCompanyId() +"' order by CAST(SUBSTRING(Voucher_No,7,50) as int)";
		
		table.removeAllItems();
		try{
			Transaction tx = null;
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			List led = session.createSQLQuery(sql).list();
			int i = 0;
			table.removeAllItems();
			vNo.clear();
			vDate.clear();
			ledger.clear();
			narration.clear();
			depAmt.clear();
			ttlValue.clear();
			for(Iterator iter = led.iterator(); iter.hasNext();){
				Object[] element = (Object[]) iter.next();
				vNo.add(i,new Label(element[0].toString()));
				vDate.add(i,new Label(sdf.format(Date.parse(element[1].toString().replace("-", "/").substring(0,10).trim()))));
				ledger.add(i,new Label(element[2].toString()));
				narration.add(i,new Label(element[3].toString()));
				fmt = new Formatter();
				fmt.format("%.2f",Double.valueOf(element[4].toString()));
				depAmt.add(i,new Label(fmt.toString()));
				depAmt.get(i).setStyleName("fright");
				
				fmt = new Formatter();
				fmt.format("%.2f",Double.valueOf(element[5].toString()));
				ttlValue.add(i,new Label(fmt.toString()));
				ttlValue.get(i).setStyleName("fright");
				
				table.addItem(new Object[]{vNo.get(i),vDate.get(i),ledger.get(i),narration.get(i),depAmt.get(i),ttlValue.get(i)},i); 
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
