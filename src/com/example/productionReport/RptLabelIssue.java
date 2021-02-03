package com.example.productionReport;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.xerces.impl.dtd.models.CMBinOp;
import org.hibernate.Session;
import org.hibernate.Transaction;


import com.common.share.FocusMoveByEnter;
import com.common.share.ReportViewerNew;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
public class RptLabelIssue extends Window{

	SessionBean sessionBean;
	private Label lblFrom=new Label("From : ");
	private ComboBox cmbFrom=new ComboBox();
	private Label lblTo=new Label("To : ");
	private ComboBox cmbTo=new ComboBox();

	private CheckBox chkAll=new CheckBox();
	private Label lblAll=new Label();

	int type=0;
	private CheckBox chkpdf=new CheckBox("PDF");
	private CheckBox chkother=new CheckBox("Others");
	private HorizontalLayout chklayout=new HorizontalLayout();

	private Label lblFDate;
	private Label lblToDate;
	private Label lblLine;
	private PopupDateField formDate=new PopupDateField();
	private PopupDateField toDate=new PopupDateField();
	private NativeButton previewButton = new NativeButton("Preview");
	private NativeButton exitButton = new NativeButton("Exit");
	private Label lblline;
	private Label lblChallanNo=new Label();
	private ComboBox cmbchallan=new ComboBox();
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
	private SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd");


	private AbsoluteLayout mainLayout;

	public RptLabelIssue(SessionBean sessionBean,String s){

		this.sessionBean=sessionBean;
		this.setCaption("LABEL PRODUCTION ISSUE::"+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		cmbFromData();
		this.addComponent(mainLayout);
		setEventAction();
		focusMove();
		cmbFrom.focus();
	}

	private void focusMove()
	{
		ArrayList<Component> focusComp = new ArrayList<Component>();

		focusComp.add(cmbFrom);
		focusComp.add(cmbTo);
		focusComp.add(cmbchallan);

		focusComp.add(previewButton);
		focusComp.add(exitButton);

		new FocusMoveByEnter(this, focusComp);
	}	

	private void cmbFromData() 
	{
		cmbFrom.removeAllItems();

		Transaction tx=null;
		String query=null;

		try
		{

			query= "  select * from  "
					+"( "
					+"	select '1' as type, CAST(AutoID as varchar(120)) as id ,SectionName as section  from tbSectionInfo where SectionName like '%Tube%' "
					+"	union "
					+"	select distinct '2' as type,  StepId as id ,StepName section  from tbProductionStep a  "
					+"	inner join  "
					+"	tbProductionType b  "
					+"	on a.productionTypeId=b.productTypeId "
					+"	where b.productTypeName like '%Label%' "
					+"	) as a  order by a.type ";

			System.out.println(query);

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();

				cmbFrom.addItem(element[1].toString());
				cmbFrom.setItemCaption(element[1].toString(), (String) element[2]);
			}
		}


		catch(Exception exp)
		{
			System.out.println(exp);
		}
	}


	private void setEventAction() {

		previewButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event)
			{
				if(cmbFrom.getValue()!=null)
				{
					if(cmbTo.getValue()!=null)
					{
						if(cmbchallan.getValue()!=null)
						{
							reportView();	 
						}
						else
						{
							showNotification("Please Select Desired Challan No",Notification.TYPE_WARNING_MESSAGE);	
							cmbchallan.focus();
						}

					}
					else
					{
						showNotification("Please Select Issue TO",Notification.TYPE_WARNING_MESSAGE);
						cmbTo.focus();
					}

				}
				else
				{
					showNotification("Please Select Issue From",Notification.TYPE_WARNING_MESSAGE);
					cmbFrom.focus();
				}

			}
		});
		exitButton.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		chkAll.addListener(new ValueChangeListener() 
		{
			public void valueChange(ValueChangeEvent event) {
				boolean bv = chkAll.booleanValue();
				System.out.println("ST : "+bv);
				if(bv==true){
					cmbFrom.setEnabled(false);
					cmbFrom.setValue(null);

				}
				else{
					cmbFrom.setEnabled(true);
					cmbFrom.focus();
				}
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

		chkother.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event)
			{

				if(chkother.booleanValue()==true)
					chkpdf.setValue(false);
				else
					chkpdf.setValue(true);

			}
		});

		cmbFrom.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbFrom.getValue()!=null)
				{
					System.out.println("I am Ok");
					cmbToData();
				}
			}
		});


		cmbTo.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbFrom.getValue()!=null)
				{
					System.out.println("I am Ok");
					challandataload();


				}
			}
		});


	}

	private void cmbToData() {
		cmbTo.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			if(cmbFrom.getItemCaption(cmbFrom.getValue()).equalsIgnoreCase("Tube Section"))
			{

				query="select Distinct  StepId,StepName  from tbProductionStep a  inner join  tbProductionType b on  a.productionTypeId=b.productTypeId " 
						+"where   b.productTypeName like 'Label Production' and a.StepName like 'Printing' ";

			}
			if(cmbFrom.getItemCaption(cmbFrom.getValue()).equalsIgnoreCase("Printing"))
			{
				query="select distinct StepId,StepName  from  tbProductionStep where StepName not like 'Printing' and StepId like '%Level%' ";

			}


			System.out.println("To: "+query);


			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				System.out.print("Is This OK");

				Object[] element=(Object[]) iter.next();

				cmbTo.addItem(element[0]);
				cmbTo.setItemCaption(element[0], (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}

	private void challandataload() {
		cmbchallan.removeAllItems();
		cmbchallan.setValue(null);
		Transaction tx=null;
		String query=null;

		try{

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			query="select distinct  0,ChallanNo from tbLabelIssueInfo where IssueTo like '"+cmbTo.getValue()+"'  ";

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				System.out.print("Is This OK");

				Object[] element=(Object[]) iter.next();

				cmbchallan.addItem(element[1]);
				cmbchallan.setItemCaption(element[1], (String) element[1]);
			}
		}


		catch(Exception exp){
			System.out.println(exp);
		}
	}




	private void reportView()
	{	
		String query=null;
		Transaction tx=null;

		if(chkpdf.booleanValue()==true)
			type=1;
		else
			type=0;

		try{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			HashMap hm = new HashMap();
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			//			hm.put("fromdate",new SimpleDateFormat("dd-MM-yyyy").format(formDate.getValue()) );
			//			hm.put("todate", new SimpleDateFormat("dd-MM-yyyy").format(toDate.getValue()));
			hm.put("issueFrom",cmbFrom.getItemCaption(cmbFrom.getValue()) );
			hm.put("issueTo",cmbFrom.getItemCaption(cmbTo.getValue()) );

			query = " select  a.jobDate,a.jobNo,a.ChallanNo, b.fgName ,a.issueQty,a.itemCode ," +
					" case when  fromFlag='1'  then c.vRawItemName  else d.vProductName  end as  productName,a.unit, e.vProductName as finishProductName" +
					" from tbLabelIssueInfo a" +
					" inner join  tbLabelIssueDetails b" +
					" on a.jobNo=b.jobNo" +
					" left join tbRawItemInfo c on c.vRawItemCode=a.itemCode" +
					" left join tbFinishedProductInfo d on d.vProductId =a.itemCode" +
					" left join tbFinishedProductInfo e on e.vProductId=b.fgId" +
					" where a.IssueFrom like '"+cmbFrom.getValue()+"' and a.IssueTo like '"+cmbTo.getValue()+"' and a.ChallanNo like '"+cmbchallan+"'";
			System.out.println(query);
			hm.put("sql", query);

			List list=session.createSQLQuery(query).list();
			if(!list.isEmpty()){
				Window win = new ReportViewerNew(hm,"report/production/rptLabelIssue.jasper",
						this.getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/rpttmp",
						this.getWindow().getApplication().getURL()+"VAADIN/rpttmp",false,
						this.getWindow().getApplication().getURL()+"VAADIN/applet",type);
				win.setCaption("Project Report");
				this.getParent().getWindow().addWindow(win);

			}
			else{
				this.getParent().showNotification("There are no Data!!",Notification.TYPE_WARNING_MESSAGE);
			}

		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp.toString(),Notification.TYPE_ERROR_MESSAGE);
			System.out.println(exp);
		}
	}
	private AbsoluteLayout buildMainLayout() {

		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("460px");
		mainLayout.setHeight("200px");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("490px");
		setHeight("280px");

		lblFrom = new Label();
		lblFrom.setImmediate(false);
		lblFrom.setWidth("-1px");
		lblFrom.setHeight("-1px");
		lblFrom.setValue("From :");
		mainLayout.addComponent(lblFrom, "top:16.0px;left:62.0px;");

		cmbFrom = new ComboBox();
		cmbFrom.setImmediate(true);
		cmbFrom.setWidth("260px");
		cmbFrom.setHeight("24px");
		cmbFrom.setNullSelectionAllowed(true);
		cmbFrom.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbFrom, "top:15.0px;left:130.0px;");

		lblTo = new Label();
		lblTo.setImmediate(false);
		lblTo.setWidth("-1px");
		lblTo.setHeight("-1px");
		lblTo.setValue("To :");
		mainLayout.addComponent(lblTo, "top:39.0px;left:62.0px;");

		cmbTo= new ComboBox();
		cmbTo.setImmediate(true);
		cmbTo.setWidth("260px");
		cmbTo.setHeight("24px");
		cmbTo.setNullSelectionAllowed(true);
		cmbTo.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbTo, "top:41.0px;left:130.0px;");

		lblChallanNo = new Label();
		lblChallanNo.setImmediate(false);
		lblChallanNo.setWidth("-1px");
		lblChallanNo.setHeight("-1px");
		lblChallanNo.setValue("Challan No:");
		mainLayout.addComponent(lblChallanNo, "top:65.0px;left:62.0px;");

		cmbchallan = new ComboBox();
		cmbchallan.setImmediate(true);
		cmbchallan.setWidth("180px");
		cmbchallan.setHeight("24px");
		cmbchallan.setNullSelectionAllowed(true);
		cmbchallan.setNewItemsAllowed(false);
		mainLayout.addComponent( cmbchallan, "top:67.0px;left:130.0px;");

		chkpdf.setValue(true);
		chkpdf.setImmediate(true);
		chkother.setImmediate(true);
		chklayout.addComponent(chkpdf);
		chklayout.addComponent(chkother);
		mainLayout.addComponent(chklayout, "top:119.0px; left:130.0px");

		lblLine = new Label();
		lblLine.setImmediate(false);
		lblLine.setWidth("-1px");
		lblLine.setHeight("-1px");
		lblLine.setContentMode(Label.CONTENT_XHTML);
		lblLine.setValue("<b><font color='#e65100'>======================================================================================================================</font></b>");
		mainLayout.addComponent(lblLine, "top:145.0px;left:0.0px;");

		previewButton.setWidth("90px");
		previewButton.setHeight("28px");
		previewButton.setIcon(new ThemeResource("../icons/print.png"));
		mainLayout.addComponent(previewButton,"top:171.opx; left:125.0px");

		exitButton.setWidth("70px");
		exitButton.setHeight("28px");
		exitButton.setIcon(new ThemeResource("../icons/exit1.png"));
		mainLayout.addComponent(exitButton,"top:171.opx; left:220.0px");

		chkAll.setVisible(false);
		lblAll.setVisible(false);
		return mainLayout;


	}
}
