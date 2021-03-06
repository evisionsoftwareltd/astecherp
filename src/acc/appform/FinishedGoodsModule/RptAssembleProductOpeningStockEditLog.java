package acc.appform.FinishedGoodsModule;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.ReportViewer;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

public class RptAssembleProductOpeningStockEditLog extends Window {
	private AbsoluteLayout mainLayout;

	private Label lblline;

	private Label lblFgName;
	private ComboBox  cmbFgName;
	private CheckBox chkAllFgName;


	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");

	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");


	private SessionBean sessionBean;
	boolean type=false;
	public RptAssembleProductOpeningStockEditLog(SessionBean sessionBean,String str) {
		this.sessionBean = sessionBean;

		setContent(buildMainLayout());
		this.setResizable(false);
		this.setCaption("ASSEMBLE PRODUCT OPENING STOCK EDIT LOG :: "+ sessionBean.getCompany());
		Component ob[]={cmbFgName,previewButton};
		new FocusMoveByEnter(this, ob);
		allButtonAction();
		addFgNameData();
		
		
	}


	private void allButtonAction()
	{	

		previewButton.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(cmbFgName.getValue()!=null || chkAllFgName.booleanValue())
					{
						reportShow();			
					}
					else
					{
						getParent().showNotification("Please Select FG Name", Notification.TYPE_WARNING_MESSAGE);	
					}
			}
		});

		exitButton.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		chkpdf.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{

				if(chkpdf.booleanValue()==true)
					chkother.setValue(false);
				else
					chkother.setValue(true);

			}
		});


		chkAllFgName.addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event) 
			{
				if(chkAllFgName.booleanValue()==true)
				{

					cmbFgName.setEnabled(false);
					cmbFgName.setValue(null);
				}
				else
				{
					cmbFgName.setEnabled(true);

				}

			}
		});

	}


	private void reportShow()
	{
		String query=null;
		String query1=null;
		String activeFlag = null;
		String fgName="";
		String item="";

		if(chkAllFgName.booleanValue())
		{
			fgName ="%"; 

		}
		else
		{
			fgName = cmbFgName.getValue().toString();
		}


	
		try{

			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();


			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());			
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("username", sessionBean.getUserName()+sessionBean.getUserIp());

			query= " select * from tbUdMasterProductOpening where fgCode like '"+fgName+"' and udSl != '1' order by fgName,autoId";

			System.out.println(query);
			hm.put("sql", query);

			List lst=session.createSQLQuery(query).list();

			if(!lst.isEmpty())
			{
				System.out.println(" is Empty check    :");
				Window win = new ReportViewer(hm,"report/account/finishedGoods/RptAssembleOpeningEditLog.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",true);
				win.setCaption("FINISHED GOODS OPENING EDIT LOG :: "+sessionBean.getCompany());
				
				System.out.println(" is Empty check   1 :");
				
				this.getParent().getWindow().addWindow(win);
				
				System.out.println(" is Empty check   2 :");
			}

			else
			{

				getParent().showNotification("Date Not Found", Notification.TYPE_WARNING_MESSAGE);

			}

		}
		catch(Exception exp){

			this.getParent().showNotification("Error "+exp,Notification.TYPE_ERROR_MESSAGE);

		}
	}



	public void addFgNameData()
	{
		cmbFgName.removeAllItems();
		Transaction tx = null;
		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String query =" select Distinct fgCode,fgName from tbUdMasterProductOpening order by fgName ";

			List list = session.createSQLQuery(query).list();
			System.out.println("Section List:"+list.size());

			for(Iterator iter=list.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				cmbFgName.addItem(element[0].toString());
				cmbFgName.setItemCaption(element[0].toString(), element[1].toString());
			}
		}catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
	}



	private AbsoluteLayout buildMainLayout() {

		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("460px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		lblFgName = new Label();
		lblFgName.setImmediate(false);
		lblFgName.setWidth("-1px");
		lblFgName.setHeight("-1px");
		lblFgName.setValue("FG Name :");
		mainLayout.addComponent(lblFgName, "top:60.0px;left:20.0px;");


		cmbFgName = new ComboBox();
		cmbFgName.setImmediate(true);
		cmbFgName.setWidth("260px");
		cmbFgName.setHeight("24px");
		cmbFgName.setNullSelectionAllowed(false);
		cmbFgName.setNewItemsAllowed(false);
		cmbFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent( cmbFgName, "top:58.0px;left:140.0px;");


		chkAllFgName = new CheckBox("");
		chkAllFgName.setCaption("All");
		chkAllFgName.setWidth("-1px");
		chkAllFgName.setHeight("24px");
		chkAllFgName.setImmediate(true);
		mainLayout.addComponent(chkAllFgName, "top:59.0px;left:405.0px;");



		lblline = new Label();
		lblline.setImmediate(false);
		lblline.setWidth("-1px");
		lblline.setHeight("-1px");
		lblline.setValue("____________________________________________________________________________________________________");
		mainLayout.addComponent(lblline, "top:120.0px;left:0.0px;");

		previewButton.setWidth("95px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:150.0px; left:140.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit.png"));
		mainLayout.addComponent(exitButton,"top:150.0px; left:250.0px");

		return mainLayout;
	}

}
