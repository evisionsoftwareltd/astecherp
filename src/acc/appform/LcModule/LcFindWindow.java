package acc.appform.LcModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
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
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class LcFindWindow extends Window
{
	private VerticalLayout mainLayout=new VerticalLayout();
	private FormLayout cmbLayout=new FormLayout();
	private HorizontalLayout btnLayout=new HorizontalLayout();
	private TextField txtShipNameForFind;
	private TextField txtLcForFind;
	private Table table=new Table();

	private String[] co=new String[]{"a","b"};
	public String shipId = "";
	public String lc = "";

	private ArrayList<Label> lblSl = new ArrayList<Label>();
	private ArrayList<Label> lblShipName = new ArrayList<Label>();
	private ArrayList<Label> lblLcNo = new ArrayList<Label>();
	private ArrayList<Label> lblApplicant = new ArrayList<Label>();
	private ArrayList<Label> lblApplicantBank = new ArrayList<Label>();

	private String frmName;
	private SessionBean sessionBean;
	public LcFindWindow(SessionBean sessionBean,TextField txtShipId,TextField txtLcForFind,String frmName)
	{
		this.txtShipNameForFind = txtShipId;
		this.txtLcForFind = txtLcForFind;
		this.sessionBean=sessionBean;
		this.setCaption("FIND PRODUCT INFO :: "+sessionBean.getCompany());
		this.center();
		this.setWidth("960px");
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
		lblSl.add(ar, new Label(""));
		lblSl.get(ar).setWidth("100%");
		lblSl.get(ar).setImmediate(true);
		lblSl.get(ar).setHeight("23px");

		lblShipName.add(ar, new Label(""));
		lblShipName.get(ar).setWidth("100%");
		lblShipName.get(ar).setImmediate(true);
		lblShipName.get(ar).setHeight("23px");

		lblLcNo.add(ar, new Label(""));
		lblLcNo.get(ar).setWidth("100%");
		lblLcNo.get(ar).setImmediate(true);
		lblLcNo.get(ar).setHeight("23px");
		
		lblApplicant.add(ar, new Label(""));
		lblApplicant.get(ar).setWidth("100%");
		lblApplicant.get(ar).setImmediate(true);
		lblApplicant.get(ar).setHeight("23px");
		
		lblApplicantBank.add(ar, new Label(""));
		lblApplicantBank.get(ar).setWidth("100%");
		lblApplicantBank.get(ar).setImmediate(true);
		lblApplicantBank.get(ar).setHeight("23px");

		table.addItem(new Object[]{lblSl.get(ar),lblShipName.get(ar),lblLcNo.get(ar),lblApplicant.get(ar),lblApplicantBank.get(ar)},ar);
	}

	public void setEventAction()
	{
		table.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					shipId = lblSl.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					lc = lblLcNo.get(Integer.valueOf(event.getItemId().toString())).getValue().toString();
					txtShipNameForFind.setValue("^"+shipId+"@"+lc);
					//txtLcForFind.setValue(lc);
					windowClose();
				}
			}
		});
	}

	private void tableclear()
	{
		for(int i=0; i<lblSl.size(); i++)
		{
			lblSl.get(i).setValue("");
			lblShipName.get(i).setValue("");
			lblLcNo.get(i).setValue("");
			lblApplicant.get(i).setValue("");
			lblApplicantBank.get(i).setValue("");
		}
	}


	private void tableDataAdding()
	{
		Transaction tx = null;
		String query = "";
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			query ="select shipId,shipName,lcNo,applicantName,applicantBankName from tbLcInformation";
			System.out.println("Increment : "+query);
			List list = session.createSQLQuery(query).list();
			
			if(!list.isEmpty())
			{
				int i=0;
				for(Iterator iter = list.iterator(); iter.hasNext();)
				{						  
					Object[] element = (Object[]) iter.next();

					lblSl.get(i).setValue(element[0]);
					lblShipName.get(i).setValue(element[1]);
					lblLcNo.get(i).setValue(element[2]);
					lblApplicant.get(i).setValue(element[3]);
					lblApplicantBank.get(i).setValue(element[4]);
					if((i)==lblSl.size()-1) {
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
		table.setSelectable(true);
		table.setWidth("100%");
		table.setHeight("250px");

		table.setImmediate(true); // react at once when something is selected
		table.setColumnReorderingAllowed(true);
		table.setColumnCollapsingAllowed(true);		

		table.addContainerProperty("SL#", Label.class, new Label());
		table.setColumnWidth("SL#",20);

		table.addContainerProperty("Ship Name", Label.class, new Label());
		table.setColumnWidth("Ship Name",220);

		table.addContainerProperty("L/C No", Label.class, new Label());
		table.setColumnWidth("L/C No",160);
		
		table.addContainerProperty("Applicant / Buyer", Label.class, new Label());
		table.setColumnWidth("Applicant / Buyer",270);

		table.addContainerProperty("Applicant / Buyer Bank", Label.class, new Label());
		table.setColumnWidth("Applicant / Buyer Bank",180);
	}

	private void compAdd()
	{
		cmbLayout.setSpacing(true);
		mainLayout.addComponent(cmbLayout);
		mainLayout.addComponent(btnLayout);
		mainLayout.addComponent(table);
		addComponent(mainLayout);
	}
}