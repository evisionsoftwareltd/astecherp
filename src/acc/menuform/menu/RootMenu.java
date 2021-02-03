package acc.menuform.menu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.common.share.MessageBox.EventListener;
import com.common.share.ChangePass;
import com.common.share.CommonButton;
import com.common.share.LogIn;
import com.common.share.MessageBox;
import com.common.share.MessageBox.ButtonType;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

import org.hibernate.Session;

@SuppressWarnings("serial")
public class RootMenu extends HorizontalLayout 
{
	private Panel mainWindow = new Panel();

	private Panel panelTop = new Panel();
	private Panel panelUp = new Panel();
	private Panel panelMiddle = new Panel();

	private Panel panelNote = new Panel();

	private Tree txtTree;

	private FormLayout fmLayout = new FormLayout();

	private AbsoluteLayout absNote;

	private Label lblUserName = new Label();
	private Label lblUserDay = new Label();
	private Label lblResolution = new Label();
	public Button btnImage = new Button("");
	public Button btnLogout = new Button("LogOut");
	public Button btnChangePass = new Button("Change Password");

	private InlineDateField date = new InlineDateField();

	public HorizontalLayout hLayout = new HorizontalLayout();
	public HorizontalLayout image = new HorizontalLayout();

	private static final Object CAPTION_PROPERTY = "caption";

	private SessionBean sessionBean;

	private Table table = new Table();

	private Label lblTodoList;
	private CommonButton cButton = new CommonButton("", "", "", "","Refresh","","","","","");

	private ArrayList<Label> tbAutoId = new ArrayList<Label>();
	private ArrayList<CheckBox> tbChkSeen = new ArrayList<CheckBox>();
	private ArrayList<Label> tblblNotiofication = new ArrayList<Label>();
	private ArrayList<Label> tblblFrom = new ArrayList<Label>();

	public RootMenu(SessionBean sessionBean) 
	{	
		this.sessionBean = sessionBean;
		this.setWidth("100%");
		this.setHeight("770px");

		absNote = new AbsoluteLayout();
		absNote.setImmediate(true);
		absNote.setMargin(false);
		absNote.setWidth("100%");
		absNote.setHeight("460px");

		fmLayout.setHeight("1000px");

		mainWindow.setWidth("480px");
		mainWindow.setHeight("100%");
		mainWindow.setImmediate(true);
		mainWindow.setScrollable(true);

		panelNote.setWidth("440px");
		panelNote.setHeight("500px");
		panelNote.setImmediate(true);
		panelNote.setScrollable(true);

		moduleAccessCheck();

		// txtTree
		txtTree = new Tree();
		txtTree.setCaption("LIST OF MODULES");
		txtTree.setImmediate(true);
		txtTree.setWidth("100%");
		txtTree.setHeight("100%");
		txtTree.setStyleName("menuBack");

		btnImage.setStyleName(BaseTheme.BUTTON_LINK);
		btnLogout.setStyleName(BaseTheme.BUTTON_LINK);
		btnLogout.setIcon(new ThemeResource("../icons/Logout.png"));
		btnChangePass.setStyleName(BaseTheme.BUTTON_LINK);
//		btnLogout.setDescription("Logout");
		
		// tree Item Add
		addTree();

		//add component to notificationLayout
		addComponent();

		getInfo();
		componentAdd();
		btnAction();

		mainWindow.addComponent(panelTop);
		mainWindow.addComponent(panelUp);

//		fmLayout.addComponent(btnImage);
		fmLayout.addComponent(btnLogout);
		fmLayout.addComponent(btnChangePass);
		fmLayout.addComponent(panelMiddle);
		date.setResolution(InlineDateField.RESOLUTION_DAY);
		fmLayout.addComponent(date);

//		panelNote.addComponent(absNote);
//		fmLayout.addComponent(panelNote);

		hLayout.addComponent(fmLayout);

		addComponent(mainWindow);
		addComponent(hLayout);
		setComponentAlignment(hLayout, Alignment.TOP_RIGHT);

		noteTableIni(5);
		treeClickAction();

		addNotification();
	}

	public void addComponent()
	{
		lblTodoList = new Label("To-do List",Label.CONTENT_XHTML);
		lblTodoList.setImmediate(true);
		lblTodoList.setWidth("-1px");
		lblTodoList.setHeight("-1px");
		lblTodoList.setStyleName("txtFont");
//		absNote.addComponent(lblTodoList, "top:0; left:110.0px;");

		cButton.btnRefresh.setWidth("33px");
		cButton.btnRefresh.setHeight("22px");
//		absNote.addComponent(cButton.btnRefresh,"top:0.0px;left:300.0px;");

		table.setWidth("100%");
		table.setHeight("225px");

		table.addContainerProperty("Id", CheckBox.class, new CheckBox());
		table.setColumnWidth("Id", 25);

		table.addContainerProperty("Seen", CheckBox.class, new CheckBox());
		table.setColumnWidth("Seen", 25);

		table.addContainerProperty("Notifications", Label.class, new Label());
		table.setColumnWidth("Notifications", 195);

		table.addContainerProperty("From", Label.class, new Label());
		table.setColumnWidth("From", 53);

		table.setColumnCollapsingAllowed(true);
		table.setColumnCollapsed("Id", true);

//		absNote.addComponent(table,"top:21.0px; left:0.0px;");
	}

	private void noteTableIni(int ar)
	{
		for(int i=0; i<ar; i++)
		{
			tableRowAdd(i);
		}
	}

	private void tableRowAdd(final int i)
	{
		tbAutoId.add(i, new Label());
		tbAutoId.get(i).setWidth("100%");
		tbAutoId.get(i).setImmediate(true);

		tbChkSeen.add(i,new CheckBox());
		tbChkSeen.get(i).setImmediate(true);
		tbChkSeen.get(i).addListener(new ValueChangeListener()
		{
			public void valueChange(ValueChangeEvent event)
			{
				try
				{
					if(tbChkSeen.get(i).booleanValue()==true)
					{
						System.out.println("Seen");
					}
				}
				catch(Exception ex)
				{

				}
			}
		});

		tblblNotiofication.add(i, new Label());
		tblblNotiofication.get(i).setWidth("100%");
		tblblNotiofication.get(i).setImmediate(true);

		tblblFrom.add(i, new Label());
		tblblFrom.get(i).setWidth("100%");
		tblblFrom.get(i).setImmediate(true);

		table.addItem(new Object[]{tbAutoId.get(i),tbChkSeen.get(i),tblblNotiofication.get(i),tblblFrom.get(i)},i);
	}

	private void addNotification()
	{
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String  sql = " select iAutoId,vNotification,vFromName,dDateTime from tbToDoList where vToId='"+sessionBean.getUserId()+"' ";
			List<?> lst = session.createSQLQuery(sql).list();
			int i = 0;
			if(!lst.isEmpty())
			{
				for(Iterator<?> iter = lst.iterator(); iter.hasNext();)
				{
					Object[] element = (Object[]) iter.next();
					if(i==tbChkSeen.size())
					{
						tableRowAdd(i);
					}
					tbAutoId.get(i).setValue(element[0].toString());
					tblblNotiofication.get(i).setValue(element[1].toString());
					tblblFrom.get(i).setValue(element[2].toString());
					i++;
				}
			}
			else
			{
				System.out.println("No data found!");
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void moduleAccessCheck()
	{
		sessionBean.setupModule = false;
		sessionBean.rawMeterialModule = false;
		sessionBean.productionModule = false;
		sessionBean.finishGoodsModule = false;
		sessionBean.PoSalesModule = false;
		sessionBean.accountsModule = false;
		sessionBean.fixedAssetModule = false;
		sessionBean.hrmModule = false;
		sessionBean.transportModule = false;
		sessionBean.lcModule = false;
		sessionBean.costingModule = false;
		sessionBean.sparePartsModule = false;
		sessionBean.crashingModule = false;
		sessionBean.thirdpartyrmModule = false;
		
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		session.beginTransaction();
		try
		{
			String sql = " Select moduleId,moduleName from tbLoginDetails where userId = '"+sessionBean.getUserId()+"' ";
			List<?> lst = session.createSQLQuery(sql).list();

			for(Iterator<?> iter = lst.iterator();iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();
				int moduleId = Integer.parseInt(element[0].toString());

				if(moduleId==0)
				{sessionBean.setupModule = true;}
				if(moduleId==1)
				{sessionBean.rawMeterialModule = true;}
				if(moduleId==2)
				{sessionBean.productionModule = true;}
				if(moduleId==3)
				{sessionBean.finishGoodsModule = true;}
				if(moduleId==4)
				{sessionBean.PoSalesModule = true;}
				if(moduleId==5)
				{sessionBean.accountsModule = true;}
				if(moduleId==6)
				{sessionBean.fixedAssetModule = true;}
				if(moduleId==7)
				{sessionBean.hrmModule = true;}
				if(moduleId==8)
				{sessionBean.transportModule = true;}
				if(moduleId==9)
				{sessionBean.lcModule = true;}
				if(moduleId==10)
				{sessionBean.costingModule = true;}
				if(moduleId==11)
				{sessionBean.sparePartsModule = true;}
				
				/*if(moduleId==12)
				{sessionBean.thirdpartyrmModule = true;}*/
				if(moduleId==12)
				{sessionBean.crashingModule = true;}
			}
		}
		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}

	private void btnAction()
	{
		btnLogout.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				userBackupMethod();
			}
		});
		btnChangePass.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				ChangePass cp=new ChangePass(sessionBean);
				cp.setStyleName("cwindow");
				cp.center();
				getWindow().addWindow(cp);
				
			}
		});
	}

	private void userBackupMethod()
	{
		MessageBox mb = new MessageBox(getWindow(), "Are you sure?", MessageBox.Icon.QUESTION, "Do you want to continue logout?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
		mb.show(new EventListener()
		{
			public void buttonClicked(ButtonType buttonType)
			{
				if(buttonType == ButtonType.YES)
				{
					Window mainWindow = getWindow();
					Iterator<?> iter = mainWindow.getChildWindows().iterator();

					while(iter.hasNext())
					{
						mainWindow.removeWindow((Window)iter.next());
						iter = mainWindow.getChildWindows().iterator();
					}

					mainWindow.removeAllComponents();
					LogIn login = new LogIn(sessionBean);
					mainWindow.getWindow().addWindow(login);
				}
				else
				{
					/*Window mainWindow = getWindow();
					Iterator<?> iter = mainWindow.getChildWindows().iterator();

					while(iter.hasNext())
					{
						mainWindow.removeWindow((Window)iter.next());
						iter = mainWindow.getChildWindows().iterator();
					}

					mainWindow.removeAllComponents();
					LogIn login = new LogIn(sessionBean);
					mainWindow.getWindow().addWindow(login);*/
				}
			}
		});
	}

	private void getInfo()
	{
		lblUserName.setValue("<b><font size=3px><i>User Name : "+sessionBean.getUserName()+"</i></font></b>");
		lblUserName.setContentMode(Label.CONTENT_XHTML);

		String day = new SimpleDateFormat("EEEEE, dd MMMMM yyyy").format(new Date());
		lblUserDay.setValue("<b><i>Day :</i></b> "+day);
		lblUserDay.setContentMode(Label.CONTENT_XHTML);

		lblResolution.setValue("<b><i>Best Resolution : 1280*1024 </i></b> ");
		lblResolution.setContentMode(Label.CONTENT_XHTML);
		
	}

	private void componentAdd()
	{
		Embedded emb = new Embedded("",new ThemeResource("../icons/astechLogo.png"));
		emb.requestRepaint();
		emb.setWidth("270px");
		emb.setHeight("100%");
		image.removeAllComponents();
		image.addComponent(emb);

		panelTop.addComponent(image);
		panelTop.setWidth("100%");
		panelTop.setHeight("100%");

		panelUp.setWidth("100%");
		panelUp.setHeight("100%");
		panelUp.addComponent(txtTree);
		panelUp.setScrollable(true);

		panelMiddle.setWidth("240px");
		panelMiddle.setHeight("100%");
		panelMiddle.addComponent(lblUserName);
		panelMiddle.addComponent(lblUserDay);
		panelMiddle.addComponent(lblResolution);
		panelMiddle.setStyleName("a");
	}

	private void treeClickAction()
	{
		txtTree.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) 
			{	
				if(event.getProperty().getValue()!=null)
				{
					System.out.println(txtTree.getValue());
				}
			}
		});

		cButton.btnRefresh.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				addNotification();
			}
		});
	}

	void addTree()
	{		
		txtTree.setDebugId("tree");
		txtTree.setImmediate(true);
		txtTree.addContainerProperty(CAPTION_PROPERTY, String.class, "");
		txtTree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		txtTree.setItemCaptionPropertyId(CAPTION_PROPERTY);

		addParents();
	}

	void addParents()
	{
		Object setupModule = null,rawMeterialModule = null;
		Object productionModule = null,finishGoodsModule = null,demandOrderSalesModule = null;
		Object accountingMenu = null,fixedAssetMenu = null;
		Object hrmModule = null,transportModule = null,lcModule = null,costingModule=null,sparePartsModule=null, crashingModule=null, thirdpartRmModule=null;

		if(sessionBean.setupModule)
		{setupModule = addCaptionedItem("SETUP MODULE", null);}

		if(sessionBean.rawMeterialModule)
		{rawMeterialModule = addCaptionedItem("INVENTORY MODULE", null);}
		
		if(sessionBean.sparePartsModule)
		{sparePartsModule = addCaptionedItem("SPARE PARTS MODULE", null);}

		if(sessionBean.productionModule)
		{productionModule = addCaptionedItem("PRODUCTION MODULE", null);}

		if(sessionBean.finishGoodsModule)
		{finishGoodsModule = addCaptionedItem("FINISH GOODS MODULE", null);}

		if(sessionBean.PoSalesModule)
		{demandOrderSalesModule = addCaptionedItem("PO & SALES MODULE", null);}

		if(sessionBean.accountsModule)
		{accountingMenu = addCaptionedItem("ACCOUNTS MODULE", null);}

		if(sessionBean.fixedAssetModule)
		{fixedAssetMenu = addCaptionedItem("FIXED ASSET MODULE", null);}

		if(sessionBean.costingModule)
		{costingModule = addCaptionedItem("COSTING MODULE", null);}
		
		if(sessionBean.lcModule)
		{lcModule = addCaptionedItem("L/C MODULE", null);}

		if(sessionBean.hrmModule)
		{hrmModule = addCaptionedItem("HRM MODULE", null);}

		if(sessionBean.transportModule)
		{transportModule = addCaptionedItem("TRANSPORT MODULE", null);}
		
		/*if(sessionBean.thirdpartyrmModule)
		{thirdpartRmModule = addCaptionedItem("THIRD PARTY RM MODULE", null);}
*/
		if(sessionBean.crashingModule)
		{crashingModule = addCaptionedItem("CRASHING MODULE", null);} 
		
		

		addChild(setupModule,rawMeterialModule,sparePartsModule,productionModule,finishGoodsModule,demandOrderSalesModule,
				accountingMenu,fixedAssetMenu,hrmModule,transportModule,lcModule,costingModule,thirdpartRmModule,crashingModule);	
	}

	void addChild(Object setupModule,Object rawMeterialModule,Object sparePartsModule,Object productionModule, Object finishGoodsModule,
			Object demandOrderSalesModule,Object accountingMenu,Object fixedAssetMenu, Object hrmModule,
			Object transportModule, Object lcModule,Object CostingModule,Object thirdpartRmModule,Object crashingModule)
	{
		if(sessionBean.setupModule)
		{new AdminMenu(setupModule,txtTree,sessionBean,mainWindow);}

		if(sessionBean.rawMeterialModule)
		{new RawMaterialMenu(rawMeterialModule,txtTree,sessionBean,mainWindow);}
		
		if(sessionBean.sparePartsModule)
		{new SparePartsMenu(sparePartsModule,txtTree,sessionBean,mainWindow);}

		if(sessionBean.productionModule)
		{new ProductionMenu(productionModule,txtTree,sessionBean,mainWindow);}

		if(sessionBean.finishGoodsModule)
		{new FinishedGoodsMenu(finishGoodsModule,txtTree,sessionBean,mainWindow);}

		if(sessionBean.PoSalesModule)
		{new DOSalesMenu(demandOrderSalesModule,txtTree,sessionBean,mainWindow);}

		if(sessionBean.accountsModule)
		{new AccountingMenu(accountingMenu,txtTree,sessionBean,mainWindow);}

		if(sessionBean.fixedAssetModule)
		{new FixedAssetMenu(fixedAssetMenu,txtTree,sessionBean,mainWindow);}

		if(sessionBean.lcModule)
		{new LcMenu(lcModule,txtTree,sessionBean,mainWindow);}

		if(sessionBean.hrmModule)
		{new HrmMenu(hrmModule,txtTree,sessionBean,mainWindow);	}

		if(sessionBean.transportModule)
		{new TransportMenu(transportModule,txtTree,sessionBean,mainWindow);	}
		
		if(sessionBean.costingModule)
		{new CostingMenu(CostingModule,txtTree,sessionBean,mainWindow);	}
		
		/*if(sessionBean.thirdpartyrmModule)
		{new ThirdPartMenu(thirdpartRmModule,txtTree,sessionBean,mainWindow);	}
		*/
		if(sessionBean.crashingModule)
		{new CrashingMenu(crashingModule,txtTree,sessionBean,mainWindow);	}
	}

	private Object addCaptionedItem(String caption, Object parent) 
	{
		final Object id = txtTree.addItem();
		final Item item = txtTree.getItem(id);
		final Property p = item.getItemProperty(CAPTION_PROPERTY);

		p.setValue(caption);

		if (parent != null) 
		{
			txtTree.setChildrenAllowed(parent, true);
			txtTree.setParent(id, parent);
			txtTree.setChildrenAllowed(id, false);
		}
		return id;
	}
	
}

