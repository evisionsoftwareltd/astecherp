package acc.appform.FinishedGoodsModule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class BrandFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtReceiptSizeId =new TextField();
	private String 	receiptSizeId="";
	private Table table=new Table();

	private ArrayList<Label>lblBrandNo  = new ArrayList<Label>();
	private ArrayList<Label> lblBrand = new ArrayList<Label>();
	private SessionBean sessionBean;

	public BrandFindWindow(SessionBean sessionBean,TextField ReligionId,String frmName)
	{
		this.sessionBean=sessionBean;
		this.txtReceiptSizeId = ReligionId;
		this.setCaption("FIND BRAND INFORMATION :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("400px");
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

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					receiptSizeId = lblBrandNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();

					txtReceiptSizeId.setValue(receiptSizeId);
					System.out.println(""+receiptSizeId);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblBrandNo.size(); i++)
		{
			lblBrandNo.get(i).setValue("");
			lblBrand.get(i).setValue("");
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
		lblBrandNo.add(ar, new Label(""));
		lblBrandNo.get(ar).setWidth("100%");
		lblBrandNo.get(ar).setImmediate(true);
		lblBrandNo.get(ar).setHeight("12px");

		lblBrand.add(ar, new Label(""));
		lblBrand.get(ar).setWidth("100%");
		lblBrand.get(ar).setImmediate(true);

		table.addItem(new Object[]{lblBrandNo.get(ar),lblBrand.get(ar)},ar);
	}

	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			query ="select vBrandId,vBrandName from tbBrandInformation Order by convert(int,vBrandId)";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();

			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblBrandNo.get(i).setValue(element[0].toString());
					lblBrand.get(i).setValue(element[1].toString());

					if((i)==lblBrandNo.size()-1)
					{
						tableRowAdd(i+1);
					}
					i++;
				}
			}
			else
			{
				tableclear();
				this.getParent().showNotification("No data Found!", Notification.TYPE_WARNING_MESSAGE); 
			}
		}
		catch (Exception ex)
		{
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
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("200px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("BRAND ID", Label.class, new Label());
		table.setColumnWidth("BRAND ID",60);

		table.addContainerProperty("BRAND", Label.class, new Label());
	}

	private void compAdd()
	{
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}

}
