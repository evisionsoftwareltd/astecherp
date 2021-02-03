package acc.appform.LcModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class LtrInformationFind extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSupplierId;
	private Table table=new Table();

	public String receiptSupplierId = "";

	private ComboBox cmbLcNo;
	
	
	private ArrayList<Label> lblLcNo = new ArrayList<Label>();
	private ArrayList<Label> lblLtrNo = new ArrayList<Label>();
	private ArrayList<Label> lblLtrId = new ArrayList<Label>();

	private SessionBean sessionBean;
	public LtrInformationFind(SessionBean sessionBean,TextField txtReceiptSupplierId)
	{
		this.txtReceiptSupplierId = txtReceiptSupplierId;
		this.sessionBean=sessionBean;
		this.setCaption("LTR INFO FIND :: "+this.sessionBean.getCompany());
		this.center();
		this.setWidth("470px");
		this.setHeight("320px");
		this.setCloseShortcut(KeyCode.ESCAPE);
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName("cwindow");
		compInit();
		compAdd();
		tableInitialise();
		setEventAction();
		tableclear();
		tableDataAdding();
	}

	public void tableInitialise()
	{
		for(int i=0;i<5;i++)
		{
			tableRowAdd(i);
		}
	}

	public void tableRowAdd(final int ar)
	{
		lblLcNo.add(ar, new Label(""));
		lblLcNo.get(ar).setWidth("100%");
		lblLcNo.get(ar).setImmediate(true);
		lblLcNo.get(ar).setHeight("23px");
		lblLtrNo.add(ar, new Label(""));
		lblLtrNo.get(ar).setWidth("100%");
		lblLtrNo.get(ar).setImmediate(true);
		lblLtrNo.get(ar).setHeight("23px");
		
		lblLtrId.add(ar, new Label(""));
		lblLtrId.get(ar).setWidth("100%");
		lblLtrId.get(ar).setImmediate(true);
		lblLtrId.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblLcNo.get(ar),lblLtrNo.get(ar),lblLtrId.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSupplierId = lblLtrId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
			        txtReceiptSupplierId.setValue(receiptSupplierId);
					windowClose();				
					
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblLcNo.size(); i++)
		{
			lblLcNo.get(i).setValue("");
			lblLtrNo.get(i).setValue("");
		}
	}

	private void tableDataAdding()
	{
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			session.beginTransaction();
			String query ="select lcNo,ltrNo,ltrId from tbLtrInformation";
			System.out.println("Increment : "+query);
			List<?> list = session.createSQLQuery(query).list();
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator<?> iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblLcNo.get(i).setValue(element[0]);
					lblLtrNo.get(i).setValue(element[1]);
					lblLtrId.get(i).setValue(element[2]);

					if((i)==lblLcNo.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else {
				tableclear();
				this.getParent().showNotification("Data not Found !!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex) {
			this.getParent().showNotification("Error", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void windowClose()
	{
		this.close();
	}

	private void compInit()
	{
		mainLayout.setSpacing(true);
		
		cmbLcNo = new ComboBox("L/C  No:");
		cmbLcNo.setImmediate(true);
		cmbLcNo.setWidth("210px");
		cmbLcNo.setHeight("-1px");
		cmbLcNo.setNullSelectionAllowed(true);
		cmbLcNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
				
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("175px");

		table.setImmediate(true);
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("L/C No", Label.class, new Label());
		table.setColumnWidth("L/C No",185);

		table.addContainerProperty("LTR No", Label.class, new Label());
		table.setColumnWidth("LTR No",185);
		
		table.addContainerProperty("LTR Id", Label.class, new Label());
		table.setColumnWidth("LTR Id",85);
		table.setColumnCollapsed("LTR Id", true);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		btnLayout.addComponent(cmbLcNo);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}