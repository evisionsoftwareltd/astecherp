package com.example.productionTransaction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.naming.java.javaURLContextFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import com.common.share.*;

import database.hibernate.TbFiscalYear;
public class JobOrder extends  Window{

	AbsoluteLayout mainLayout;
	SessionBean sessionBean;

	Label lblOrderNo,lblOrderDate,lblDate,lblCompany,lblAddress,lblItem,lblOrderQty,
	lblFinishedFrom,lblDeliveryDate,lblRemarks,lblDocument,lblIssueDate,lblRevision,lblColor,lblorderdetails,lblstartdate,lblendDate,lblremarks,lblpoNo,lblpodate,lblproductionType,lblJobOrderNo;
	public Label lblImage;
	public FormLayout frmImage = new FormLayout();

	TextRead txtDocumentNo,txtRevision,txtColor;
	TextField txttransaction;
	TextField txtFinishForm,txtOrderNo;
	TextField  txtremarks;
	private TextField txtterscondition1;
	private TextField txtterscondition2;
	private TextField txtterscondition3;
	private TextField txtterscondition4;
	private TextRead txtcolorBox=new TextRead();
	private TextRead txtcolorBox1=new TextRead();
	private TextRead txtcolorBox2=new TextRead();
	private TextRead txtcolorBox3=new TextRead();
	private TextRead txtcolorBox4=new TextRead();

	AmountField txtOrderQty;
	TextArea arAddress;
	ComboBox cmbPartyName,cmbItem,cmbpoNo,cmbproductionType;
	PopupDateField dOrderDate,dIssueDate,dCurrentDate,dDeliveryDate,dStartDate,dEndate,dpodate;
	public ImmediateUploadExampleNew sampleAttached;
	public FileUpload image;
	boolean isUpdate = false,isFind=false;
	String filePathTmpReq= "";

	ArrayList<Component> allComp = new ArrayList<Component>();
	//String imageLoc="0";
	String imgLocation="",color="";
	String Productsequence="";
	TextField txtJobOrderNo;
	private static final List<String>areatype  = Arrays.asList(new String[] {"With PO","Without PO" });
	OptionGroup poType;
	Label lblPotype;
	//
	private Table tbproduct=new Table();

	private ArrayList<Label> lblsl = new ArrayList<Label>();
	private ArrayList<ComboBox>tbcmbproduct = new ArrayList<ComboBox>();
	private ArrayList<TextRead> tbtxtunit  = new ArrayList<TextRead>(1);
	private ArrayList<ComboBox>tbcmbproductionType = new ArrayList<ComboBox>();
	private ArrayList<TextRead>tbtxtpoqty = new ArrayList<TextRead>(1);
	private ArrayList<AmountCommaSeperator>tbtxtorderqty = new ArrayList<AmountCommaSeperator>();

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private TextField findJobNo=new TextField();
	private CommonButton cButton = new CommonButton( "New",  "Save",  "Edit",  "",  "Refresh",  "Find", "", "","","Exit");

	private ImmediateUploadExample2 bpvUpload = new ImmediateUploadExample2("");
	Button btnPreview;
	String imageLoc= "0";
	String filePathTmp = "";
	String bpvPdf = null;
	String tempimg="";
	private Label lblCommon;

	private static final String[] cities = new String[] { "Active", "Inactive" };
	TextArea txtInActioveRemarks;
	Label lblInactiveDate=new Label("Inactive Date :");
	Label lblInactioveRemarks=new Label("Remarks :");
	ComboBox cmbStatus;
	PopupDateField dInActive;
	SimpleDateFormat dateFYMD = new SimpleDateFormat("yyyy-MM-dd");

	public JobOrder(SessionBean sesionBean)
	{
		this.sessionBean=sesionBean;
		this.setCaption("JOB ORDER ::  "+sessionBean.getCompany());
		setHeight("650px");
		setWidth("1180px");
		setContent(buildMainLayout());
		setResizable(false);
		setEventAction();
		txtClear();
		focusMoveByEnter();
		btnIni(true);
		partydataLoad();
		componentIni(true);
	}
	private void tbSemiFgLoadData(int a){
		String sql="select semiFgCode,semiFgName,color from tbSemiFgInfo   order by semiFgName ";
		List list=dbService(sql);
		Iterator iter=list.iterator();
		//int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			tbcmbproduct.get(a).addItem(element[0]);
			String caption=element[1]+" # "+element[2];
			tbcmbproduct.get(a).setItemCaption(element[0], caption);
		}
	}
	public List dbService(String sql)
	{
		List list = null;
		Transaction tx = null;
		System.out.println(sql);
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			list=session.createSQLQuery(sql).list();
			return list;

		}
		catch(Exception exp){

		}
		return list;
	}

	private String autoReciptNo()
	{
		String autoId=null;

		try{
			List lst=dbService("select ISNULL(MAX(transactionNo),0)+1 as autoid from tbJobOrderInfo");
			Iterator<?>iter=lst.iterator();
			if(iter.hasNext())
			{
				autoId=iter.next().toString().trim();
			}
		}
		catch(Exception exp){
			System.out.println(exp);
		}
		return autoId;
	}

	private void partydataLoad()
	{

		List list=dbService(" select vGroupId,partyName from tbPartyInfo order by partyName ");
		Iterator iter=list.iterator();
		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			cmbPartyName.addItem(element[0].toString());
			cmbPartyName.setItemCaption(element[0].toString(), (String) element[1]);	
		}

	}

	private void FGDataLoad(int i)
	{

		String sql="select  vProductId,vProductName from tbFinishedProductInfo "
				+"where vCategoryId like '"+cmbPartyName.getValue().toString()+"' ";
		List list=dbService(sql);
		Iterator iter=list.iterator();
		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			tbcmbproduct.get(i).addItem(element[0].toString());
			tbcmbproduct.get(i).setItemCaption(element[0].toString(), (String) element[1]);	
		}

	}
	private void poDataLoad()
	{
		cmbpoNo.removeAllItems();
		String sql="";
		sql = "select distinct  0, doNo from tbDemandOrderInfo  "
				+"where partyId like (select partyCode from tbPartyInfo where vGroupId like '"+cmbPartyName.getValue().toString()+"') and vStatus not like 'Inactive'";
		List list=dbService(sql);
		Iterator iter=list.iterator();
		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			cmbpoNo.addItem(element[1].toString());	
		}
	}

	private void poDataLoadFilterMode()
	{
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		System.out.println("exec PrcPoDataLoad '"+cmbPartyName.getValue().toString().trim()+"' ");
		session.createSQLQuery(" exec PrcPoDataLoad '"+cmbPartyName.getValue().toString()+"'  ").executeUpdate();
		tx.commit();
		cmbpoNo.removeAllItems();
		String sql="";
		sql = "select poNo from tbprcpoNo";
		List list=dbService(sql);
		Iterator iter=list.iterator();

		while(iter.hasNext())
		{ 
			//Object[] element=(Object[]) iter.next();
			cmbpoNo.addItem(iter.next());	
		}
	}
	private void addressDataLoad()
	{
		List list=dbService("select  0,address from tbPartyInfo where vGroupId like '"+cmbPartyName.getValue()+"' ");
		System.out.println("select  0,address from tbPartyInfo where vGroupId like '"+cmbPartyName.getValue()+"' ");
		Iterator iter=list.iterator();
		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			arAddress.setValue(element[1].toString());
		}
	}


	/*private void productDataLoad()
	{
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();

		if (isFind)
		{
			for(int i=0;i<tbcmbproductionType.size();i++)
			{

				List lst=session.createSQLQuery("select  distinct  productId,productName from tbDemandOrderDetails where doNo like '"+cmbpoNo.getValue().toString()+"' ").list();


				for(Iterator iter=lst.iterator();iter.hasNext();)
				{
					Object[] element=(Object[]) iter.next();
					tbcmbproduct.get(i).addItem(element[0].toString());
					tbcmbproduct.get(i).setItemCaption(element[0].toString(), element[1].toString());

				}

			}
		}


		if (!isFind)
		{
			for(int i=0;i<tbcmbproductionType.size();i++)
			{

				List lst=session.createSQLQuery("select  fgId,fgName from tbprcpoNo where poNo like '"+cmbpoNo.getValue().toString()+"' ").list();


				for(Iterator iter=lst.iterator();iter.hasNext();)
				{
					Object[] element=(Object[]) iter.next();
					tbcmbproduct.get(i).addItem(element[0].toString());
					tbcmbproduct.get(i).setItemCaption(element[0].toString(), element[1].toString());
				}

			}
		}


		List lst1=session.createSQLQuery("select  fgId,fgName from tbprcpoNo where poNo like '"+cmbpoNo.getValue().toString()+"' ").list();

		if(!isFind)
		{
			int i=0;
			int a=lst1.size();
			Iterator iter=lst1.iterator();
			while(a>0)
			{
				Object[] element=(Object[]) iter.next();
				tbcmbproduct.get(i).setValue(element[0]);
				a--;
				i++;

			}	
		}


	}*/



	/*	

	private void partydataLoad() 
	{
		cmbPartyName.removeAllItems();
		Transaction tx=null;
		String query=null;

		try{

			query= "  select * from  "
					+"( "
					+"	select '1' as type, CAST(AutoID as varchar(120)) as id ,SectionName as section  from tbSectionInfo where SectionName like '%Tube Sec%' "
					+"	union "
					+"	select distinct '2' as type,  StepId as id ,StepName section  from tbProductionStep a  "
					+"	inner join  "
					+"	tbProductionType b  "
					+"	on a.productionTypeId=b.productTypeId "
					+"	where b.productTypeName like '%Tube%' "
					+"	) as a  order by a.type ";

			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery(query).list();

			for(Iterator iter=list.iterator(); iter.hasNext();){

				Object[] element=(Object[]) iter.next();

				cmbIssueFrom.addItem(element[1].toString());
				cmbIssueFrom.setItemCaption(element[1].toString(), (String) element[2]);
			}
		}

		catch(Exception exp){
			System.out.println(exp);
		}
	}*/





	private void focusMoveByEnter()
	{
		allComp.add(txtOrderNo);
		allComp.add(cmbPartyName);
		allComp.add(cmbpoNo);
		allComp.add(dDeliveryDate);
		allComp.add(dOrderDate);
		allComp.add(dStartDate);
		allComp.add(dEndate);

		for(int i=0;i<tbtxtunit.size();i++)
		{
			allComp.add(tbcmbproduct.get(i));
			//allComp.add(tbtxtcolor.get(i));
			allComp.add(tbcmbproductionType.get(i));
			//allComp.add(tbcmbFgFrom.get(i));
			allComp.add(tbtxtorderqty.get(i));	
		}


		allComp.add(txtremarks);
		allComp.add(txtterscondition1);
		allComp.add(txtterscondition2);
		allComp.add(txtterscondition3);
		allComp.add(txtterscondition4);

		new FocusMoveByEnter(this,allComp);
	}

	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
		cButton.btnPreview.setEnabled(t);

	}

	public void txtClear()
	{
		txtOrderNo.setValue("");
		dOrderDate.setValue(new java.util.Date());
		dDeliveryDate.setValue(new java.util.Date());
		dCurrentDate.setValue(new java.util.Date());
		cmbPartyName.setValue(null);
		arAddress.setValue("");
		dStartDate.setValue(new java.util.Date());
		dEndate.setValue(new java.util.Date());
		cmbpoNo.setValue(null);
		dpodate.setValue(new java.util.Date());
		tableClear();
		txtremarks.setValue("");
		txtterscondition1.setValue("");
		txtterscondition2.setValue("");
		txtterscondition3.setValue("");
		txtterscondition4.setValue("");
		txtJobOrderNo.setValue("");
		cmbStatus.setValue("Active");

		bpvUpload.fileName = "";
		bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
		filePathTmp = "";
		bpvUpload.actionCheck = false;
		imageLoc = "0";
		btnPreview.setEnabled(false);

	}
	private void tableClear(){
		for(int i=0;i<tbtxtunit.size();i++)
		{
			tbcmbproduct.get(i).setValue(null);
			tbtxtunit.get(i).setValue("");
			//tbtxtbtchNo.get(i).setValue("");
			//tbtxtcolor.get(i).setValue("");
			tbcmbproductionType.get(i).setValue(null);
			//tbcmbFgFrom.get(i).setValue(null);
			tbtxtpoqty.get(i).setValue("");
			tbtxtorderqty.get(i).setValue("");
			//tbtxtordersequence.get(i).setValue("");
		}
	}
	public void componentIni(boolean b) 
	{

		txtOrderNo.setEnabled(!b);
		dDeliveryDate.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		arAddress.setEnabled(!b);
		dOrderDate.setEnabled(!b);
		dCurrentDate.setEnabled(!b);
		dStartDate.setEnabled(!b);
		dEndate.setEnabled(!b);
		dStartDate.setEnabled(!b);

		txtremarks.setEnabled(!b);
		txtterscondition1.setEnabled(!b);
		txtterscondition2.setEnabled(!b);
		txtterscondition3.setEnabled(!b);
		txtterscondition4.setEnabled(!b);
		cmbpoNo.setEnabled(!b);
		dpodate.setEnabled(!b);
		txtJobOrderNo.setEnabled(!b);
		poType.setEnabled(!b);

		cmbStatus.setEnabled(!b);
		txtInActioveRemarks.setEnabled(!b);
		dInActive.setEnabled(!b);
		btnPreview.setEnabled(!b);
		bpvUpload.setEnabled(!b);

		//tbproduct.setEnabled(!b);
		for(int a=0;a<tbcmbproduct.size();a++){
			tbcmbproduct.get(a).setEnabled(!b);
			tbtxtunit.get(a).setEnabled(!b);
			tbcmbproductionType.get(a).setEnabled(!b);
			tbtxtpoqty.get(a).setEnabled(!b);
			tbtxtorderqty.get(a).setEnabled(!b);
		}

	}
	/*private String imagePatRequisition(int flag, String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		String ReqImage = null;

		System.out.println("Base Path: "+basePath);
		System.out.println("Base Path: o  "+getWindow().getApplication().getContext().getBaseDirectory());

		if(flag==0)
		{
			// image move		
			if(sampleAttached.fileName.trim().length()>0)
			{
				System.out.println("hello : "+sampleAttached.fileName.trim());
				if(sampleAttached.fileExtension.equalsIgnoreCase(".jpg") || sampleAttached.fileExtension.equalsIgnoreCase(".pdf"))
				{
					try
					{
						String path = str;
						fileMove(basePath+sampleAttached.fileName.trim(), SessionBean.imagePathTmp+path+sampleAttached.fileExtension);
						ReqImage = SessionBean.imagePathTmp+path+sampleAttached.fileExtension;
						filePathTmpReq = path+sampleAttached.fileExtension;
						System.out.println("Mezbah: "+basePath+sampleAttached.fileName.trim());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			return ReqImage;
		}

		if(flag==1)
		{
			// image move		
			if(sampleAttached.fileName.trim().length()>0)
			{
				System.out.println("sampleAttached.fileName : "+sampleAttached.fileName.trim());
				if(sampleAttached.fileExtension.equalsIgnoreCase(".jpg") || sampleAttached.fileExtension.equalsIgnoreCase(".pdf"))
				{
					try {
						String path =str;
						fileMove(basePath+sampleAttached.fileName.trim(),SessionBean.productSample+path+sampleAttached.fileExtension);
						ReqImage = SessionBean.productSample+path+sampleAttached.fileExtension;
						filePathTmpReq = path+sampleAttached.fileExtension;
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return ReqImage;
		}
		return null;
	}

	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp)
		{

		}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}*/
 
	public void cmbStatusAction(){
		dInActive.setValue(new Date());
		txtInActioveRemarks.setValue("");
		dInActive.setVisible(true);
		txtInActioveRemarks.setVisible(true);
		lblInactiveDate.setVisible(true);
		lblInactioveRemarks.setVisible(true);
	}
	public void statusWiseClear(){
		dInActive.setValue(new Date());
		txtInActioveRemarks.setValue("");
		dInActive.setVisible(false);
		txtInActioveRemarks.setVisible(false);
		lblInactiveDate.setVisible(false);
		lblInactioveRemarks.setVisible(false);

	}
	public boolean dateValidation(){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Date dateInactive=(Date) dInActive.getValue();//
		Date datePoDate=(Date) dOrderDate.getValue();
		cal1.setTime(datePoDate);
		cal2.setTime(dateInactive);
		if(cal1.after(cal2)){
			System.out.println("dateInactive is after CurrentDate");
			return false;
		}
		if(cal1.before(cal2)){
			System.out.println("dateInactive is before CurrentDate");
			return true;
		}
		if(cal1.equals(cal2)){
			System.out.println("dateInactive is equal CurrentDate");
			return true;
		}
		return false;
	}
	private String imagePath(int flag,String str)
	{
		String basePath = getWindow().getApplication().getContext().getBaseDirectory()+"".replace("\\","/")+"/VAADIN/themes/";
		System.out.println("basePath is:"+basePath+bpvUpload.fileName.trim());
		String stuImage = null;
		if(flag==0)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
				try {
					if(bpvUpload.fileName.toString().endsWith(".jpg")){
						String path = sessionBean.getUserId();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+path+".jpg");
						tempimg=basePath+bpvUpload.fileName.trim();
						bpvPdf = SessionBean.imagePath+path+".jpg";
						filePathTmp = path+".jpg";
					}
					else{
						String path = sessionBean.getUserId();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+path+".pdf");
						bpvPdf = SessionBean.imagePath+path+".pdf";
						filePathTmp = path+".pdf";
					}
				} 
			catch (IOException e){
				e.printStackTrace();
			}
			return bpvPdf;
		}

		if(flag==1)
		{
			// image move
			if(bpvUpload.fileName.trim().length()>0)
			{
				try
				{	
					if(bpvUpload.fileName.toString().endsWith(".jpg"))
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/jobOrder/"+path+".jpg");
						stuImage = SessionBean.imagePath+projectName+"/jobOrder/"+path+".jpg";
					}
					else
					{
						String path = str;
						String projectName = sessionBean.getContextName();
						fileMove(basePath+bpvUpload.fileName.trim(),SessionBean.imagePath+projectName+"/jobOrder/"+path+".pdf");
						stuImage = SessionBean.imagePath+projectName+"/jobOrder/"+path+".pdf";
					}
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			return stuImage;
		}
		return null;
	}

	//////////////////////////////////Tin upload end
	private void fileMove(String fStr,String tStr) throws IOException
	{
		try
		{
			File f1 = new File(tStr);
			if(f1.isFile())
				f1.delete();
		}
		catch(Exception exp){}
		FileInputStream ff= new FileInputStream(fStr);

		File  ft = new File(tStr);
		FileOutputStream fos = new FileOutputStream(ft);

		while(ff.available()!=0)
		{
			fos.write(ff.read());
		}
		fos.close();
		ff.close();
	}

	private void setEventAction() 
	{

		/*btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString();
					System.out.println("link is:"+link);

					if(link.endsWith(""+sessionBean.getContextName()+"/"))
					{
						link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+filePathTmp;
						link=imagePath(0,"");
						link=tempimg;
						link=link+"VAADIN/themes"+tempimg.substring(tempimg.lastIndexOf("/"));
						System.out.println(link);
					}
					getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
				}
				if(isUpdate)
				{
					if(!bpvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString();

							if(link.endsWith(""+sessionBean.getContextName()+"/"))
							{
								link = link.replaceAll(""+sessionBean.getContextName()+"/", imageLoc.substring(22, imageLoc.length()));
							}
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString();

						if(link.endsWith(""+sessionBean.getContextName()+"/"))
						{
							link = link.replaceAll(""+sessionBean.getContextName()+"", "report")+filePathTmp;
						}
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});
		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				btnPreview.setCaption("Preview");
				btnPreview.setEnabled(true);
				System.out.println("Done");
			}
		});*/
		
		
		btnPreview.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!isUpdate)
				{
					String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
					getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
				}
				if(isUpdate)
				{
					if(!bpvUpload.actionCheck)
					{
						if(!imageLoc.equalsIgnoreCase("0"))
						{
							String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName()+"/", imageLoc.substring(22, imageLoc.length()));
							getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
						}
						else
						{
							showNotification("There is no File",Notification.TYPE_HUMANIZED_MESSAGE);
						}
					}
					if(bpvUpload.actionCheck)
					{
						String link = getApplication().getURL().toString().replaceAll(sessionBean.getContextName(), "report")+filePathTmp;
						getWindow().open(new ExternalResource(link),"_blank",500,200,Window.BORDER_NONE);
					}
				}
			}
		});
		bpvUpload.upload.addListener(new Upload.SucceededListener() 
		{
			public void uploadSucceeded(SucceededEvent event) 
			{
				imagePath(0,"");
				btnPreview.setCaption("Preview");
				btnPreview.setEnabled(true);
				System.out.println("Done");
			}
		});
		

		cmbStatus.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(cmbStatus.getValue()!=null){
					if(cmbStatus.getValue().toString().equals("Inactive")){
						cmbStatusAction();
					}
					else{
						statusWiseClear();
					}
				}
				else{
					statusWiseClear();
				}
			}
		});

		dInActive.addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				if(!dateValidation()){
					dInActive.setValue(new Date());
					showNotification(null,"Inactive date should greater than job order date",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		cButton.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				componentIni(false);
				btnIni(false);
				txtClear();
				isFind=false;
				txtOrderNo.focus();
				cmbpoNo.setEnabled(false);
				dpodate.setEnabled(false);
				dDeliveryDate.setEnabled(false);
			}
		});


		cButton.btnRefresh.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				refreshevent();	
			}
		});

		cmbPartyName.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbPartyName.getValue()!=null)
				{
					addressDataLoad();
					if(!isFind)
					{
						poDataLoadFilterMode();
					}
					if (isFind)
					{
						poDataLoad();	
					}
					tableClear();
				}
				else
				{
					arAddress.setValue("");
					cmbpoNo.removeAllItems();

				}
			}
		});

		cButton.btnSave.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(cmbStatus.getValue().toString().trim().equals("Inactive")){
					if(!txtInActioveRemarks.getValue().toString().isEmpty()){
						saveButtonEvent();
					}
					else{
						showNotification("Warning!","Provide Inactive remarks.", Notification.TYPE_WARNING_MESSAGE);
						txtInActioveRemarks.focus();
					}
				}
				else{
					saveButtonEvent();
				}
			}
		});

		cmbpoNo.addListener(new ValueChangeListener()
		{

			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbpoNo.getValue()!=null)
				{
					List lst=dbService("select  doDate,deiveryDate from tbDemandOrderInfo where doNo like '"+cmbpoNo.getValue().toString()+"'");
					Iterator iter=lst.iterator();
					if(iter.hasNext())
					{
						Object[]element=(Object[]) iter.next();
						dpodate.setValue(element[0]);
						dDeliveryDate.setValue(element[1]);

					}
					tableClear();
					/*for(int i=0;i<tbcmbFgFrom.size();i++)
					{
						tbcmbproduct.get(i).setValue(null); 	
					}*/
					//productDataLoad();
				}
				else
				{
					/*for(int i=0;i<tbcmbFgFrom.size();i++)
					{
						tbcmbproduct.get(i).setValue(null); 	
					}*/
					tableClear();
					dpodate.setValue(new java.util.Date());	
					dDeliveryDate.setValue(new java.util.Date());
				}

			}
		});

		cButton.btnFind.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				findButtonEvent();
			}
		});

		cButton.btnExit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				close();
			}
		});

		poType.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) 
			{

				if(poType.getValue().equals("Without PO"))
				{
					pocomonent(true);
					cmbpoNo.setValue(null);
					/*for(int i=0;i<tbcmbFgFrom.size();i++)
					{
						tbcmbproduct.get(i).removeAllItems(); 	
					}

					if(cmbPartyName.getValue()!=null)
					{
						for(int i=0;i<tbcmbFgFrom.size();i++)
						{
							FGDataLoad(i);	
						}	
					}*/
					tableClear();
				}

				else
				{
					pocomonent(false);
					/*for(int i=0;i<tbcmbFgFrom.size();i++)
					{
						tbcmbproduct.get(i).removeAllItems(); 	
					}*/
					tableClear();

				}

			}
		});

		cButton.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event) 
			{
				if(!txtOrderNo.getValue().toString().isEmpty())
				{
					isUpdate=true;
					componentIni(false);
					btnIni(false);
				}
				else
				{
					showNotification("There Is Nothing To Update",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});


	}

	private void  pocomonent (boolean t)
	{
		cmbpoNo.setEnabled(!t);
		dpodate.setEnabled(!t);
		dDeliveryDate.setEnabled(!t);

	}

	private void findButtonEvent() 
	{
		Window win = new JobOrderFindWindow(sessionBean, txttransaction);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{

				txtClear();
				isFind = true;

				System.out.println("Transaction No: "+txttransaction.getValue().toString());
				findInitialise(txttransaction.getValue().toString());
				if(imageLoc.equals("0"))
				{btnPreview.setCaption("attach");
				//btnPreview.setEnabled(false);
				}
				else
				{btnPreview.setCaption("Preview");
				//btnPreview.setEnabled(true);
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void findInitialise(String findJobNo) 
	{
		String imgcap="";
		Transaction tx = null;
		try 
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String sql = "select Date,partyId,orderNo,poNo,orderDate,startDate,endDate,poType,remarks,termsCondition1,termsCondition2,termsCondition3,termsCondition4,vStatus,dInactivedate,vRemark,vAttachPath from tbJobOrderInfo "
					+"where transactionNo like '"+findJobNo+"'" ;

			//String sql1 = "select Date,partyId,orderNo,poNo,orderDate,startDate,endDate from tbJobOrderInfo where orderNo like '"+findJobNo+"'" ;
			System.out.println(sql);
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter = list.iterator(); iter.hasNext();) 
			{
				Object element[] = (Object[])iter.next();
				dCurrentDate.setValue(element[0]);
				cmbPartyName.setValue(element[1].toString());
				txtOrderNo.setValue(element[2].toString());
				poType.select(element[7].toString());

				if(element[7].toString().equals("With PO"))
				{
					cmbpoNo.addItem(element[3]);
					cmbpoNo.setValue(element[3].toString());	
				}
				dOrderDate.setValue(element[4]);
				dStartDate.setValue(element[5]);
				dEndate.setValue(element[6]);
				txtremarks.setValue(element[8].toString());
				txtterscondition1.setValue(element[9]);
				txtterscondition2.setValue(element[10]);
				txtterscondition3.setValue(element[11]);
				txtterscondition4.setValue(element[12]);
				System.out.println("Before cmbStatus");
				cmbStatus.setValue(element[13].toString());
				if(element[13].toString().equals("Inactive"))
				{
					dInActive.setValue(element[14]);
					txtInActioveRemarks.setValue(element[15].toString());
				}
				///////////////////////// Attach
				if(!element[16].toString().equals("0")){
					imageLoc=element[16].toString();
					imgcap=element[16].toString().substring(element[16].toString().lastIndexOf("/")+1,element[16].toString().length());
					bpvUpload.status.setValue(new Label("<font size=1px>("+imgcap+")</font>",Label.CONTENT_XHTML));
				}
				else{
					bpvUpload.fileName = "";
					bpvUpload.status.setValue(new Label("<font size=1px>(Select .pdf/.jpg Format)</font>",Label.CONTENT_XHTML));
					filePathTmp = "";
					bpvUpload.actionCheck = false;
					imageLoc = "0";
				}
				System.out.println("After Uplod");
			}

			String sql2 = "";
			sql2 = "select fgId,unit,batchNo,color,productionType,fgFrom,orderQty,orderSequence " 
					+"from tbJobOrderDetails where transactionNo = '"+findJobNo+"' ";
			System.out.println(sql2);
			List list1=session.createSQLQuery(sql2).list();

			int i = 0;

			for (Iterator iter2 = list1.iterator(); iter2.hasNext();)
			{
				Object element[] = (Object[])iter2.next();
				tbcmbproduct.get(i).setValue(element[0].toString());
				tbtxtunit.get(i).setValue(element[1].toString());
				//tbtxtbtchNo.get(i).setValue(element[2].toString());
				//tbtxtcolor.get(i).setValue(element[3].toString());
				tbcmbproductionType.get(i).setValue(element[4].toString());
				//tbcmbFgFrom.get(i).setValue(element[5].toString());
				tbtxtorderqty.get(i).setValue( new DecimalFormat("0.00").format(element[6]));
				//tbtxtordersequence.get(i).setValue(element[7].toString());

				i++;

			}
		}
		catch (Exception exp) 
		{
			this.getParent().showNotification("FindIni", exp + "",Notification.TYPE_ERROR_MESSAGE);
		}
	}

	private void refreshevent()
	{
		componentIni(true);
		btnIni(true);
		txtClear();
		isFind=false;
		isUpdate=false;
	}

	public void saveButtonEvent(){
		if(!txtOrderNo.getValue().toString().isEmpty())
		{
			if(cmbPartyName.getValue()!=null)
			{
				if(poType.getValue().equals("Without PO"))
				{
					if(productCheck())
					{
						//if(color()){
						if(productionType())
						{
							if(FGFrom())
							{
								if(!tbtxtorderqty.get(0).getValue().toString().isEmpty())
								{
									if(isUpdate)
									{
										this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
												new YesNoDialog.Callback() {
											public void onDialogResult(boolean yes) {
												if(yes){
													Transaction tx=null;
													Session session = SessionFactoryUtil.getInstance().getCurrentSession();
													tx = session.beginTransaction();
													if(deleteData(session,tx))
													{

														insertData(session,tx);	
													}

													else{
														tx.rollback();
													}
													isUpdate=false;
													refreshevent();
													componentIni(true);
													btnIni(true);
												}
											}
										}));
									}else{
										this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
												new YesNoDialog.Callback() {



											public void onDialogResult(boolean yes) 
											{

												if(yes)
												{
													Transaction tx=null;
													Session session = SessionFactoryUtil.getInstance().getCurrentSession();
													tx = session.beginTransaction();
													insertData(session,tx);
													isUpdate=false;
													refreshevent();
													componentIni(true);
													btnIni(true);
												}	
											}

										}));
									}	
								}

								else
								{
									showNotification("Please Select Order Qty",Notification.TYPE_WARNING_MESSAGE);	
								}

							}
							else{
								showNotification("Please Select FInish From",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else{
							showNotification("Please Select Production Type",Notification.TYPE_WARNING_MESSAGE);
						}
						/*}
						else{
							showNotification("Please Select Product Color",Notification.TYPE_WARNING_MESSAGE);
						}*/
					}
					else{
						showNotification("Please Select Desire Product Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}

				else 
				{
					System.out.println("Rabiul Hasan");
					if(cmbpoNo.getValue()!=null)
					{
						if(productCheck())
						{
							//					if(color()){
							if(productionType())
							{
								if(FGFrom())
								{
									if(!tbtxtorderqty.get(0).getValue().toString().isEmpty())
									{
										if(isUpdate)
										{
											this.getParent().addWindow(new YesNoDialog("","Do you want to update information?",
													new YesNoDialog.Callback() {
												public void onDialogResult(boolean yes) {
													if(yes){
														Transaction tx=null;
														Session session = SessionFactoryUtil.getInstance().getCurrentSession();
														tx = session.beginTransaction();
														if(deleteData(session,tx))
														{

															insertData(session,tx);	
														}

														else{
															tx.rollback();
														}
														isUpdate=false;
														refreshevent();
														componentIni(true);
														btnIni(true);
													}
												}
											}));
										}else{
											this.getParent().addWindow(new YesNoDialog("","Do you want to save all information?",
													new YesNoDialog.Callback() {



												public void onDialogResult(boolean yes) 
												{

													if(yes)
													{
														Transaction tx=null;
														Session session = SessionFactoryUtil.getInstance().getCurrentSession();
														tx = session.beginTransaction();
														insertData(session,tx);
														isUpdate=false;
														refreshevent();
														componentIni(true);
														btnIni(true);
													}	
												}

											}));
										}	
									}

									else
									{
										showNotification("Please Select Order Qty",Notification.TYPE_WARNING_MESSAGE);	
									}

								}
								else{
									showNotification("Please Select FInish From",Notification.TYPE_WARNING_MESSAGE);
								}
							}
							else{
								showNotification("Please Select Production Type",Notification.TYPE_WARNING_MESSAGE);
							}
							/*}
						else{
							showNotification("Please Select Product Color",Notification.TYPE_WARNING_MESSAGE);
						}*/
						}
						else{
							showNotification("Please Select Desire Product Name",Notification.TYPE_WARNING_MESSAGE);
						}	
					}

					else
					{
						showNotification("Please Select Desire PO No",Notification.TYPE_WARNING_MESSAGE);	
					}


				}


			}
			else{
				showNotification("Please Select Desire Company Name",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Job Order No",Notification.TYPE_WARNING_MESSAGE);
			txtOrderNo.focus();
		}
	}




	public boolean deleteData(Session session, Transaction tx) 
	{
		try 
		{
			System.out.println("Transaction No: "+txttransaction.getValue().toString());
			
			//////////// Ud Insert Start
			
			String tbjoborderInfo="insert into tbUdJobOrderInfo select orderNo, orderDate, DeliveryDate, Date, "
			+ "partyId, address, startDate, endDate, remarks, userIp, userId, entryTime, isActive, poNo, poDate,"
			+ " poType, transactionNo, termsCondition1, termsCondition2, termsCondition3, termsCondition4, vStatus,"
			+ " dInactivedate, vRemark, vAttachPath,'Update' from tbJobOrderInfo "
			+ "where transactionNo like '"+txttransaction.getValue().toString()+"' ";
			
			String tbJobOrderDetails="insert into tbUdJobOrderDetails select orderNo, fgId, unit, batchNo, "
			+ "color, productionType, fgFrom, orderQty, orderSequence, isActive, transactionNo,'Update' "
			+ "from tbJobOrderDetails where transactionNo like '"+txttransaction.getValue().toString()+"'";
			
			String tbSemiFgJobOrderDetails="insert into tbUdSemiFgJobOrderDetails select  JobOrderNo, FgId, "
			+ "SemiFgId, semiFgName, ratioQty, stdQty, unitPrice, jobOrderQty,'Update' "
			+ "from tbSemiFgJobOrderDetails where jobOrderNo like '"+txtOrderNo.getValue().toString()+"'";
			
			session.createSQLQuery(tbjoborderInfo).executeUpdate();
			session.createSQLQuery(tbJobOrderDetails).executeUpdate();
			session.createSQLQuery(tbSemiFgJobOrderDetails).executeUpdate();
			
			//////////// End
			
			
			session.createSQLQuery("delete from tbJobOrderInfo where transactionNo like '"+txttransaction.getValue().toString()+"' ").executeUpdate();
			session.createSQLQuery("delete from tbJobOrderDetails where transactionNo like '"+txttransaction.getValue().toString()+"' ").executeUpdate();
			session.createSQLQuery("delete from tbSemiFgJobOrderDetails where jobOrderNo like '"+txtOrderNo.getValue().toString()+"'").executeUpdate();
			return true;
		} 
		catch (Exception exp) 
		{
			this.getParent().showNotification("Error", exp + "",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	
	
	
	private void insertData(Session session,Transaction tx) {

		try{
			String Inactiveremarks="";
			if(cmbStatus.getValue().toString().equals("Inactive")){
				Inactiveremarks=txtInActioveRemarks.getValue().toString();
			}
			else{
				//Date FastDate=Date.parse("1900-01-01");
				//dInActive.setValue(dateFDMY.format(Date.parse(1900-01-01)));
			}
			String sql = "";
			System.out.println("Rabiul Bahar");
			String potype=poType.getValue().toString();
			String pono="";
			if(cmbpoNo.getValue()!=null)
			{
				pono=cmbpoNo.getValue().toString();
			}
			if(!isUpdate)
			{
				txttransaction.setValue(autoReciptNo());	
			}
			String attach = imagePath(1,txtOrderNo.getValue().toString())==null? imageLoc:imagePath(1,txtOrderNo.getValue().toString());

			sql = 	" insert into tbJobOrderInfo" +
					" (orderNo,orderDate,DeliveryDate,Date,partyId,address,startDate,endDate,remarks,userIp,userId,entryTime,isActive,poNo,poDate,poType,transactionNo,termsCondition1,termsCondition2,"
					+ "termsCondition3,termsCondition4,vStatus,dInactivedate,vRemark,vAttachPath)" +
					" values" +
					" ('"+txtOrderNo.getValue().toString().trim()+"'," +
					" '"+dateFormat.format(dOrderDate.getValue())+"'," +
					" '"+dateFormat.format(dDeliveryDate.getValue())+"'," +
					" '"+dateFormat.format(dCurrentDate.getValue())+"'," +
					" '"+cmbPartyName.getValue().toString()+"'," +
					" '"+arAddress.getValue().toString().trim()+"'," +
					" '"+dateFormat.format(dStartDate.getValue())+"'," +
					" '"+dateFormat.format(dEndate.getValue())+"'," +
					" '"+txtremarks.getValue().toString().trim()+"'," +
					" '"+sessionBean.getUserIp()+"'," +
					" '"+sessionBean.getUserId()+"'," +
					" CURRENT_TIMESTAMP, '1','"+pono+"','"+dateFormat.format(dpodate.getValue())+"'," +
					"'"+potype+"','"+txttransaction.getValue().toString()+"','"+txtterscondition1.getValue()+"','"+txtterscondition2.getValue()+"','"+txtterscondition3.getValue()+"'," +
					"'"+txtterscondition4.getValue()+"','"+cmbStatus.getValue().toString()+"','"+dateFYMD.format(dInActive.getValue() )+"','"+Inactiveremarks+"','"+attach+"')";
					System.out.println(sql);
					session.createSQLQuery(sql).executeUpdate();
			if(isUpdate){
				String Ud = " insert into tbUdJobOrderInfo" +
						" (orderNo,orderDate,DeliveryDate,Date,partyId,address,startDate,endDate,remarks,userIp,userId,entryTime,isActive,poNo,poDate,poType,transactionNo,termsCondition1,termsCondition2,"
						+ "termsCondition3,termsCondition4,vStatus,dInactivedate,vRemark,vAttachPath,vUdFlag)" +
						" values" +
						" ('"+txtOrderNo.getValue().toString().trim()+"'," +
						" '"+dateFormat.format(dOrderDate.getValue())+"'," +
						" '"+dateFormat.format(dDeliveryDate.getValue())+"'," +
						" '"+dateFormat.format(dCurrentDate.getValue())+"'," +
						" '"+cmbPartyName.getValue().toString()+"'," +
						" '"+arAddress.getValue().toString().trim()+"'," +
						" '"+dateFormat.format(dStartDate.getValue())+"'," +
						" '"+dateFormat.format(dEndate.getValue())+"'," +
						" '"+txtremarks.getValue().toString().trim()+"'," +
						" '"+sessionBean.getUserIp()+"'," +
						" '"+sessionBean.getUserId()+"'," +
						" CURRENT_TIMESTAMP, '1','"+pono+"','"+dateFormat.format(dpodate.getValue())+"'," +
						"'"+potype+"','"+txttransaction.getValue().toString()+"','"+txtterscondition1.getValue()+"','"+txtterscondition2.getValue()+"','"+txtterscondition3.getValue()+"'," +
						"'"+txtterscondition4.getValue()+"','"+cmbStatus.getValue().toString()+"','"+dateFYMD.format(dInActive.getValue() )+"','"+Inactiveremarks+"','"+attach+"','New')";
				System.out.println(Ud);
				session.createSQLQuery(Ud).executeUpdate();
			}
			
			System.out.println("insert info: "+sql);	

			for (int i = 0; i < tbcmbproduct.size(); i++)
			{
				if (tbcmbproduct.get(i).getValue()!=null && !tbtxtorderqty.get(i).getValue().toString().isEmpty() )
				{
					String query = 	" insert into tbJobOrderDetails" +
							" (orderNo,fgId,unit,batchNo,color,productionType,fgFrom,orderQty,orderSequence,isActive,transactionNo)" +
							" values" +
							" ('"+txtOrderNo.getValue().toString().trim()+"'," +
							" '"+tbcmbproduct.get(i).getValue().toString().trim()+"'," +
							" '"+tbtxtunit.get(i).getValue().toString().trim()+"','',''," +
							//" '"+tbtxtbtchNo.get(i).getValue().toString().trim()+"'," +
							//" '"+tbtxtcolor.get(i).getValue().toString().trim()+"'," +
							" '"+tbcmbproductionType.get(i).getValue().toString().trim()+"',''," +
							//" '"+tbcmbFgFrom.get(i).getValue().toString().trim()+"'," +
							" '"+tbtxtorderqty.get(i).getValue().toString().trim()+"'," +
							" '','1','"+txttransaction.getValue().toString()+"')";
					System.out.println(query);
					session.createSQLQuery(query).executeUpdate();
					
					if(isUpdate){
						String Udd = 	" insert into tbUdJobOrderDetails" +
								" (orderNo,fgId,unit,batchNo,color,productionType,fgFrom,orderQty,orderSequence,isActive,transactionNo,vUdFlag)" +
								" values" +
								" ('"+txtOrderNo.getValue().toString().trim()+"'," +
								" '"+tbcmbproduct.get(i).getValue().toString().trim()+"'," +
								" '"+tbtxtunit.get(i).getValue().toString().trim()+"','',''," +
								//" '"+tbtxtbtchNo.get(i).getValue().toString().trim()+"'," +
								//" '"+tbtxtcolor.get(i).getValue().toString().trim()+"'," +
								" '"+tbcmbproductionType.get(i).getValue().toString().trim()+"',''," +
								//" '"+tbcmbFgFrom.get(i).getValue().toString().trim()+"'," +
								" '"+tbtxtorderqty.get(i).getValue().toString().trim()+"'," +
								" '','1','"+txttransaction.getValue().toString()+"','New')";
						System.out.println(Udd);
						session.createSQLQuery(Udd).executeUpdate();
					}
				}
			}
			tx.commit();
			this.getParent().showNotification("All information saved successfully.");
			//			totalissue=0.00;
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("From Insert"+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}



	private boolean productCheck()
	{
		for(int i=0;i<tbcmbproduct.size();i++)
		{
			if(tbcmbproduct.get(i).getValue()!=null)
			{
				return true;  
			}
		}


		return false;

	}


	private boolean productionType()
	{
		for(int i=0;i<tbcmbproduct.size();i++)
		{
			if(tbcmbproductionType.get(i).getValue()!=null)
			{
				return true;  
			}
		}


		return false;

	}


	private boolean FGFrom()
	{
		for(int i=0;i<tbcmbproduct.size();i++)
		{
			if(tbcmbproduct.get(i).getValue()!=null)
			{
				return true;  
			}
		}
		return false;
	}


	private String  joborderset()
	{

		String monthYear=new SimpleDateFormat("MM-yy").format(new java.util.Date()).replace("-", "/");
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			String orderNo="";

			//String sql="select isnull(max(cast(substring(orderNo,0,CHARINDEX('/',orderNo))as int)),0)+1 from tbOrderCardSales";

			/*		String query = "select    ISNULL(MAX(CAST(SUBSTRING(orderNo,0,CHARINDEX('/',orderNo))as int)),0)+1 as  autoid    from tbJoborderInfo "
					+"where SUBSTRING(orderNo,CHARINDEX('/',orderNo)+1,LEN(orderNo)-CHARINDEX('/',orderNo)) like '"+monthYear+"' ";*/

			String query = "select    ISNULL(MAX(CAST(SUBSTRING(orderNo,CHARINDEX('-',orderNo)+1,CHARINDEX('/',orderNo)-CHARINDEX('-',orderNo)-1) as int)),0)+1  as  autoid    from tbJoborderInfo "
					+"where SUBSTRING(orderNo,CHARINDEX('/',orderNo)+1,LEN(orderNo)-CHARINDEX('/',orderNo)) like '"+monthYear+"' ";

			System.out.println("joborderset: "+query);

			Iterator iter = session.createSQLQuery(query).list().iterator();
			int num = 0;
			if (iter.hasNext()) 
			{
				num = Integer.parseInt(iter.next().toString());
				orderNo=(num+"/"+monthYear);
				return  orderNo;
			}
		}
		catch(Exception exp){
			this.getParent().showNotification("Errora",exp+"",Notification.TYPE_ERROR_MESSAGE);

		}
		return null;
	}


	private void loadData()
	{
		txtFinishForm.setValue("");

		Transaction tx=null;

		try
		{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			List list=session.createSQLQuery("select finishForm,imageLocation,color from tbFinishedProductInfo where vProductId like '"+cmbItem.getValue()+"'").list();

			for(Iterator iter=list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				txtFinishForm.setValue(element[0].toString());
				frmImage(element[1].toString());
				imgLocation=element[1].toString();
				color=element[2].toString();
				txtColor.setValue(color);
			}
		}
		catch(Exception ex)
		{
			System.out.print(ex);
		}
	}

	private void frmImage(String img)
	{

		File  fileStu_I = new File(img);

		Embedded eStu_I = new Embedded("",new FileResource(fileStu_I, getApplication()));
		eStu_I.requestRepaint();
		eStu_I.setWidth("130px");
		eStu_I.setHeight("160px");

		frmImage.removeAllComponents();
		frmImage.addComponent(eStu_I);

	}	

	private AbsoluteLayout buildMainLayout() {

		mainLayout=new AbsoluteLayout();
		mainLayout.setHeight("600px");

		lblOrderNo=new Label("Job Order No: ");
		lblOrderNo.setWidth("-1px");
		lblOrderNo.setHeight("-1px");

		txtOrderNo=new TextField();
		txtOrderNo.setImmediate(true);
		txtOrderNo.setWidth("250px");
		txtOrderNo.setHeight("-1px");

		txttransaction=new TextField();
		txttransaction.setImmediate(true);
		txttransaction.setWidth("250px");
		txttransaction.setHeight("-1px");

		lblOrderDate=new Label("Job Order Date: ");
		lblOrderDate.setWidth("-1px");
		lblOrderDate.setHeight("-1px");

		dOrderDate = new PopupDateField();
		dOrderDate.setImmediate(true);
		dOrderDate.setDateFormat("dd-MM-yyyy");
		dOrderDate.setWidth("-1px");
		dOrderDate.setHeight("-1px");
		dOrderDate.setInvalidAllowed(false);
		dOrderDate.setResolution(PopupDateField.RESOLUTION_DAY);

		lblDeliveryDate=new Label("Exp. Delivery Date: ");
		lblDeliveryDate.setWidth("-1px");
		lblDeliveryDate.setHeight("-1px");

		dDeliveryDate = new PopupDateField();
		dDeliveryDate.setImmediate(true);
		dDeliveryDate.setDateFormat("dd-MM-yyyy");
		dDeliveryDate.setWidth("-1px");
		dDeliveryDate.setHeight("-1px");
		dDeliveryDate.setInvalidAllowed(false);
		dDeliveryDate.setResolution(PopupDateField.RESOLUTION_DAY);


		lblDate=new Label("Date: ");
		lblDate.setWidth("-1px");
		lblDate.setHeight("-1px");

		dCurrentDate = new PopupDateField();
		dCurrentDate.setImmediate(true);
		dCurrentDate.setDateFormat("dd-MM-yyyy");
		dCurrentDate.setWidth("-1px");
		dCurrentDate.setHeight("-1px");
		dCurrentDate.setInvalidAllowed(false);
		dCurrentDate.setResolution(PopupDateField.RESOLUTION_DAY);

		lblCompany=new Label("Party Name: ");
		lblCompany.setWidth("-1px");
		lblCompany.setHeight("-1px");

		cmbPartyName= new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("280px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


		lblstartdate=new Label("Prod. Start Date : ");
		lblstartdate.setWidth("-1px");
		lblstartdate.setHeight("-1px");

		dStartDate = new PopupDateField();
		dStartDate.setImmediate(true);
		dStartDate.setDateFormat("dd-MM-yyyy");
		dStartDate.setWidth("-1px");
		dStartDate.setHeight("-1px");
		dStartDate.setInvalidAllowed(false);
		dStartDate.setResolution(PopupDateField.RESOLUTION_DAY);



		lblAddress=new Label("Address: ");
		lblAddress.setWidth("-1px");
		lblAddress.setHeight("-1px");

		arAddress = new TextArea();
		arAddress.setImmediate(true);
		arAddress.setWidth("280px");
		arAddress.setHeight("50px");


		lblendDate=new Label("Prod. End Date : ");
		lblendDate.setWidth("-1px");
		lblendDate.setHeight("-1px");

		dEndate = new PopupDateField();
		dEndate.setImmediate(true);
		dEndate.setDateFormat("dd-MM-yyyy");
		dEndate.setValue(new java.util.Date());
		dEndate.setWidth("-1px");
		dEndate.setHeight("-1px");
		dEndate.setInvalidAllowed(false);
		dEndate.setResolution(PopupDateField.RESOLUTION_DAY);

		lblPotype=new Label("P.O Type : ");
		lblPotype.setWidth("-1px");
		lblPotype.setHeight("-1px");

		poType= new OptionGroup("",areatype);
		poType.setImmediate(true);
		poType.setWidth("-1px");
		poType.setHeight("-1px");
		poType.setStyleName("horizontal");
		poType.select("Without PO");

		lblpoNo=new Label("P.O No : ");
		lblpoNo.setWidth("-1px");
		lblpoNo.setHeight("-1px");

		cmbpoNo= new ComboBox();
		cmbpoNo.setImmediate(true);
		cmbpoNo.setWidth("280px");
		cmbpoNo.setNullSelectionAllowed(true);
		cmbpoNo.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);


		lblpodate=new Label("P.O Date : ");
		lblpodate.setWidth("-1px");
		lblpodate.setHeight("-1px");

		dpodate = new PopupDateField();
		dpodate.setImmediate(true);
		dpodate.setDateFormat("dd-MM-yyyy");
		dpodate.setValue(new java.util.Date());
		dpodate.setWidth("-1px");
		dpodate.setHeight("-1px");
		dpodate.setInvalidAllowed(false);
		dpodate.setResolution(PopupDateField.RESOLUTION_DAY);

		lblJobOrderNo=new Label("Job Order No : ");
		lblJobOrderNo.setWidth("-1px");
		lblJobOrderNo.setHeight("-1px");
		lblJobOrderNo.setVisible(false);

		txtJobOrderNo=new TextField();
		txtJobOrderNo.setImmediate(true);
		txtJobOrderNo.setWidth("140px");
		txtJobOrderNo.setHeight("-1px");
		txtJobOrderNo.setVisible(false);


		lblorderdetails=new Label("<font color=green size=5px> <B>ORDER DETAILS :</b></font>",Label.CONTENT_XHTML);
		lblorderdetails.setImmediate(true);


		tbproduct.setWidth("98%");
		tbproduct.setHeight("200px");
		tbproduct.setColumnCollapsingAllowed(true);
		tbproduct.setFooterVisible(true);

		tbproduct.addContainerProperty("SL", Label.class , new Label());
		tbproduct.setColumnWidth("SL",20);

		tbproduct.addContainerProperty("Semi Fg Name", ComboBox.class , new ComboBox());
		tbproduct.setColumnWidth("Semi Fg Name",350);

		tbproduct.addContainerProperty("UNIT", TextRead.class , new TextRead());
		tbproduct.setColumnWidth("UNIT",60);
		tbproduct.setColumnAlignment("UNIT", Table.ALIGN_CENTER);

		//tbproduct.addContainerProperty("Batch No", TextRead.class , new TextRead());
		//tbproduct.setColumnWidth("Batch No",100);

		//tbproduct.addContainerProperty("Color", TextField.class , new TextField());
		//tbproduct.setColumnWidth("Color",120);

		tbproduct.addContainerProperty("Production Type", ComboBox.class , new ComboBox());
		tbproduct.setColumnWidth("Production Type",140);

		//tbproduct.addContainerProperty("Finish From", ComboBox.class , new ComboBox());
		//tbproduct.setColumnWidth("Finish From",140);

		tbproduct.addContainerProperty("Po Qty", TextRead.class , new TextRead(1));
		tbproduct.setColumnWidth("Po Qty",100);

		tbproduct.addContainerProperty("Order Qty", AmountCommaSeperator.class , new AmountCommaSeperator());
		tbproduct.setColumnWidth("Order Qty",100);

		/*tbproduct.addContainerProperty("Or.Sequence",TextRead.class , new TextRead());
		tbproduct.setColumnWidth("Or.Sequence",80);
		tbproduct.setColumnCollapsed("Or.Sequence",true);*/



		lblremarks=new Label("<font color=green size=5px> <B>Terms & Condition :</b></font>",Label.CONTENT_XHTML);
		lblremarks.setImmediate(true);

		txtcolorBox= new TextRead();
		txtcolorBox.setImmediate(true);
		txtcolorBox.setWidth("12px");
		txtcolorBox.setHeight("12px");
		txtcolorBox.setStyleName("bcolorbox");

		txtremarks=new TextField();
		txtremarks.setImmediate(true);
		txtremarks.setWidth("755px");
		txtremarks.setHeight("24px");

		txtcolorBox1 = new TextRead();
		txtcolorBox1.setImmediate(true);
		txtcolorBox1.setWidth("12px");
		txtcolorBox1.setHeight("12px");
		txtcolorBox1.setStyleName("bcolorbox");

		txtterscondition1 = new TextField();
		txtterscondition1.setImmediate(true);
		txtterscondition1.setWidth("755px");
		txtterscondition1.setHeight("24px");

		txtcolorBox2 = new TextRead();
		txtcolorBox2.setImmediate(true);
		txtcolorBox2.setWidth("12px");
		txtcolorBox2.setHeight("12px");
		txtcolorBox2.setStyleName("bcolorbox");

		txtterscondition2 = new TextField();
		txtterscondition2.setImmediate(true);
		txtterscondition2.setWidth("755px");
		txtterscondition2.setHeight("24px");

		txtcolorBox3 = new TextRead();
		txtcolorBox3.setImmediate(true);
		txtcolorBox3.setWidth("12px");
		txtcolorBox3.setHeight("12px");
		txtcolorBox3.setStyleName("bcolorbox");

		txtterscondition3 = new TextField();
		txtterscondition3.setImmediate(true);
		txtterscondition3.setWidth("755px");
		txtterscondition3.setHeight("24px");

		txtcolorBox4 = new TextRead();
		txtcolorBox4.setImmediate(true);
		txtcolorBox4.setWidth("12px");
		txtcolorBox4.setHeight("12px");
		txtcolorBox4.setStyleName("bcolorbox");

		txtterscondition4 = new TextField();
		txtterscondition4.setImmediate(true);
		txtterscondition4.setWidth("755px");
		txtterscondition4.setHeight("24px");

		cmbStatus = new ComboBox();
		cmbStatus.setImmediate(true);
		cmbStatus.setWidth("110px");
		cmbStatus.setHeight("-1px");
		cmbStatus.setFilteringMode(cmbStatus.FILTERINGMODE_CONTAINS);
		//mainLayout.addComponent(new Label("Status"),"top:423.0px; left:860.0px;");
		mainLayout.addComponent(cmbStatus, "top:420.0px; left:937.0px;");
		cmbStatus.setNullSelectionAllowed(false);
		for (int i = 0; i < cities.length; i++) {
			cmbStatus.addItem(cities[i]);
		}
		cmbStatus.setVisible(false);
		
		cmbStatus.setValue(cities[0]);
		dInActive = new PopupDateField("");
		dInActive.setImmediate(true);
		dInActive.setWidth("110px");
		dInActive.setHeight("-1px");
		dInActive.setDateFormat("dd-MM-yyyy");
		dInActive.setValue(new java.util.Date());
		dInActive.setResolution(PopupDateField.RESOLUTION_DAY);
		mainLayout.addComponent(lblInactiveDate,"top:453.0px; left:860.0px;");
		mainLayout.addComponent(dInActive, "top:450.0px; left:937.0px;");

		lblInactiveDate.setVisible(false);
		dInActive.setVisible(false);

		txtInActioveRemarks = new TextArea();
		txtInActioveRemarks.setImmediate(false);
		txtInActioveRemarks.setWidth("200px");
		txtInActioveRemarks.setHeight("48px");
		txtInActioveRemarks.setImmediate(true);
		mainLayout.addComponent(lblInactioveRemarks,"top:483.0px; left:860.0px;");
		mainLayout.addComponent(txtInActioveRemarks, "top:480.0px; left:937.5px;");

		lblInactioveRemarks.setVisible(false);
		txtInActioveRemarks.setVisible(false);

		////////////////////
		// bpvUpload
		lblCommon = new Label("Upload:");
		mainLayout.addComponent(lblCommon, "top:113.0px; left:840px;");
		mainLayout.addComponent(bpvUpload, "top:110.0px;left:940px;");
		// btnPreview
		btnPreview = new Button("Preview");
		btnPreview.setStyleName(BaseTheme.BUTTON_LINK);
		btnPreview.addStyleName("icon-after-caption");
		btnPreview.setImmediate(true);
		btnPreview.setIcon(new ThemeResource("../icons/document-pdf.png"));
		mainLayout.addComponent(btnPreview, "top:150.0px;left:940.0px;");
		btnPreview.setEnabled(false);
		////////////////////////////////
		mainLayout.addComponent(lblOrderNo,"top:20px;left:20px;");
		mainLayout.addComponent(txtOrderNo,"top:18px;left:120px;");

		mainLayout.addComponent(lblDate,"top:50px;left:20px;");
		mainLayout.addComponent(dCurrentDate,"top:48px;left:120px;");

		mainLayout.addComponent(lblCompany,"top:80px;left:20px;");
		mainLayout.addComponent(cmbPartyName,"top:78px;left:120px;");

		mainLayout.addComponent(lblAddress,"top:106px;left:20px;");
		mainLayout.addComponent(arAddress,"top:104px;left:120px;");

		mainLayout.addComponent(lblPotype,"top:20px;left:435px;");
		mainLayout.addComponent(poType,"top:18px;left:550px;");

		mainLayout.addComponent(lblpoNo,"top:50px;left:435px;");
		mainLayout.addComponent(cmbpoNo,"top:48px;left:550px;");

		mainLayout.addComponent(lblpodate,"top:80px;left:435px;");
		mainLayout.addComponent(dpodate,"top:78px;left:550px;");

		mainLayout.addComponent(lblDeliveryDate,"top:106px;left:435px;");
		mainLayout.addComponent(dDeliveryDate,"top:104px;left:550px;");

		/*mainLayout.addComponent(lblJobOrderNo,"top:120px;left:445px;");
		mainLayout.addComponent(txtJobOrderNo,"top:118px;left:550px;");*/


		mainLayout.addComponent(lblOrderDate,"top:20px;left:840px;");
		mainLayout.addComponent(dOrderDate,"top:18px;left:940px;");

		mainLayout.addComponent(lblstartdate,"top:50px;left:840px;");
		mainLayout.addComponent(dStartDate,"top:48px;left:940px;");

		mainLayout.addComponent(lblendDate,"top:80px;left:840px;");
		mainLayout.addComponent(dEndate,"top:78px;left:940px;");
		
		mainLayout.addComponent(lblorderdetails,"top:160px;left:20px;");

		mainLayout.addComponent(tbproduct,"top:186px;left:20px;");
		tableinitialise();
		mainLayout.addComponent(lblremarks,"top:395px;left:20px;");

		mainLayout.addComponent(txtcolorBox, "top:425px;left:50px;");
		mainLayout.addComponent(txtremarks,"top:420px;left:80px;");
		mainLayout.addComponent(txtcolorBox1, "top:451px;left:50px;");
		mainLayout.addComponent(txtterscondition1, "top:446.0px;left:80.0px;");
		mainLayout.addComponent(txtcolorBox2, "top:477px;left:50px;");
		mainLayout.addComponent(txtterscondition2, "top:472.0px;left:80.0px;");
		mainLayout.addComponent(txtcolorBox3, "top:503px;left:50px;");
		mainLayout.addComponent(txtterscondition3, "top:498.0px;left:80.0px;");
		mainLayout.addComponent(txtcolorBox4, "top:529px;left:50px;");
		mainLayout.addComponent(txtterscondition4, "top:524.0px;left:80.0px;");
		mainLayout.addComponent(cButton,"top:570px;left:270px;");

		return mainLayout;
	}

	public void tableinitialise()
	{
		for(int i=0;i<5;i++)
		{
			tablerowAdd(i);
		}
	}


	public boolean doubleCheck(int ar)
	{
		String value=tbcmbproduct.get(ar).getValue().toString();
		for(int x=0;x<tbcmbproduct.size();x++){
			if(tbcmbproduct.get(x).getValue()!=null)
			{
				if(x!=ar&&value.equalsIgnoreCase(tbcmbproduct.get(x).getValue().toString())){
					return false;
				}
			}
		}
		return true;
	}


	private void tablerowAdd(final int ar) 
	{
		lblsl.add(ar,new Label());
		lblsl.get(ar).setWidth("20px");
		lblsl.get(ar).setValue(ar + 1);

		tbcmbproduct.add(ar,new ComboBox());
		tbcmbproduct.get(ar).setWidth("100%");
		tbcmbproduct.get(ar).setImmediate(true);
		tbcmbproduct.get(ar).setNullSelectionAllowed(true);
		tbcmbproduct.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		tbSemiFgLoadData(ar);

		tbcmbproduct.get(ar).addListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) 
			{
				try
				{
					if(tbcmbproduct.get(ar).getValue()!=null)
					{
						if( doubleCheck(ar ))
						{
							List lst =dbService("select 0,unit from tbSemiFgInfo where semiFgCode like  '"+tbcmbproduct.get(ar).getValue().toString()+"' ");
							Iterator iter=lst.iterator();
							while(iter.hasNext())
							{
								Object[] element=(Object[]) iter.next();
								tbtxtunit.get(ar).setValue(element[1].toString());
							}

							/*List lis1=dbService("Select [dbo].[funcbtachNumber]('"+cmbPartyName.getValue()+"','"+tbcmbproduct.get(ar).getValue()+"','"+txtOrderNo.getValue()+"') as batchNo");
							Iterator iter1=lis1.iterator();

							while(iter1.hasNext())
							{
								tbtxtbtchNo.get(ar).setValue(iter1.next());
							}*/

							/*String query= "select distinct  isnull(MAX(orderSequence),0)+1 from tbJobOrderInfo a inner join tbJobOrderDetails b "
									+ "on a.orderNo=b.orderNo  where a.partyId like '"+cmbPartyName.getValue().toString()+"' and b.fgId like '"+tbcmbproduct.get(ar).getValue().toString()+"' ";

							List lis2=dbService(query);
							Iterator iter2=lis2.iterator();

							while(iter2.hasNext())
							{
								tbtxtordersequence.get(ar).setValue(iter2.next());
							}*/



							/*Transaction tx=null;
							Session session=SessionFactoryUtil.getInstance().getCurrentSession();
							tx=session.beginTransaction();

							if(cmbpoNo.getValue()!=null)
							{
								String sql=" select 0,  qty from tbDemandOrderDetails where doNo like '"+cmbpoNo.getValue().toString()+"' and productId like '"+tbcmbproduct.get(ar).getValue()+"' ";
								List lst1=session.createSQLQuery(sql).list();
								Iterator iternew=lst1.iterator();

								while(iternew.hasNext())
								{
									Object[] element=(Object[]) iternew.next();
									tbtxtpoqty.get(ar).setValue(new DecimalFormat("#0.00").format(element[1]) );
								}
							}*/

							if(ar==tbcmbproduct.size()-1)
							{
								tablerowAdd(ar+1);
								tbSemiFgLoadData(ar+1);

								/*if (poType.getValue().toString().equalsIgnoreCase("Without PO") && cmbpoNo.getValue()==null)
								{
									List lst2=session.createSQLQuery("select vProductId,vProductName from tbFinishedProductInfo where vCategoryId like '"+cmbPartyName.getValue().toString()+"' ").list();


									for(Iterator iter3=lst2.iterator();iter3.hasNext();)
									{
										Object[] element=(Object[]) iter3.next();
										tbcmbproduct.get(ar+1).addItem(element[0].toString());
										tbcmbproduct.get(ar+1).setItemCaption(element[0].toString(), element[1].toString());

									}

								}

								if (poType.getValue().toString().equalsIgnoreCase("With PO") && cmbpoNo.getValue()!=null)
								{

									List lstr=session.createSQLQuery("select  fgId,fgName from tbprcpoNo where poNo like '"+cmbpoNo.getValue().toString()+"' ").list();


									for(Iterator iterr=lstr.iterator();iterr.hasNext();)
									{
										Object[] element=(Object[]) iterr.next();
										tbcmbproduct.get(ar+1).addItem(element[0].toString());
										tbcmbproduct.get(ar+1).setItemCaption(element[0].toString(), element[1].toString());
									}
								}*/
							}	
						}

						else
						{
							showNotification("Same Item Name Is Not Applicable",Notification.TYPE_WARNING_MESSAGE);
							tbtxtunit.get(ar).setValue("");
							//tbtxtbtchNo.get(ar).setValue("");
							//tbtxtordersequence.get(ar).setValue("");
							tbcmbproduct.get(ar).setValue(null);
						}	
					}
					else
					{
						tbtxtunit.get(ar).setValue("");
						//tbtxtbtchNo.get(ar).setValue("");
						//tbtxtordersequence.get(ar).setValue("");
						tbtxtpoqty.get(ar).setValue("");
						//tbtxtcolor.get(ar).setValue("");
						tbcmbproductionType.get(ar).setValue(null);
						//tbcmbFgFrom.get(ar).setValue(null);
					}	
				}
				catch(Exception EX)
				{
					System.out.println("Exception Is"+EX)	;
				}



			}
		});


		tbtxtunit.add(ar,new TextRead(""));
		tbtxtunit.get(ar).setWidth("100%");

		//tbtxtordersequence.add(ar,new TextRead(""));
		//tbtxtordersequence.get(ar).setWidth("100%");

		//tbtxtbtchNo.add(ar,new TextRead(""));
		//tbtxtbtchNo.get(ar).setWidth("100%");

		//tbtxtcolor.add(ar,new TextField(""));
		//tbtxtcolor.get(ar).setWidth("100%");

		tbcmbproductionType.add(ar,new ComboBox());
		tbcmbproductionType.get(ar).setWidth("100%");
		tbcmbproductionType.get(ar).setImmediate(true);
		tbcmbproductionType.get(ar).setNullSelectionAllowed(true);

		List lst =dbService("select productTypeId,productTypeName  from tbProductionType order by productTypeId ");
		Iterator iter=lst.iterator();

		while(iter.hasNext())
		{
			Object[] element=(Object[]) iter.next();
			tbcmbproductionType.get(ar).addItem(element[0].toString());
			tbcmbproductionType.get(ar).setItemCaption(element[0].toString(),element[1].toString());
		}

		/*tbcmbproductionType.get(ar).addListener(new ValueChangeListener() {


			public void valueChange(ValueChangeEvent event) 
			{
				if(tbcmbproductionType.get(ar).getValue()!=null)
				{

					List lst =dbService("select StepId,StepName from tbProductionStep where productionTypeId like '"+tbcmbproductionType.get(ar).getValue()+"' ");
					Iterator iter=lst.iterator();

					tbcmbFgFrom.get(ar).removeAllItems();
					while(iter.hasNext())
					{
						Object[] element=(Object[]) iter.next();
						tbcmbFgFrom.get(ar).addItem(element[0].toString());
						tbcmbFgFrom.get(ar).setItemCaption(element[0].toString(),element[1].toString());
					}


				}

			}
		});*/


		/*tbcmbFgFrom.add(ar,new ComboBox());
		tbcmbFgFrom.get(ar).setWidth("100%");
		tbcmbFgFrom.get(ar).setImmediate(true);
		tbcmbFgFrom.get(ar).setNullSelectionAllowed(true);*/

		tbtxtpoqty.add(ar,new TextRead(1));
		tbtxtpoqty.get(ar).setWidth("100%");

		tbtxtorderqty.add(ar,new AmountCommaSeperator());
		tbtxtorderqty.get(ar).setWidth("100%");


		tbproduct.addItem(new Object[]{lblsl.get(ar),tbcmbproduct.get(ar),tbtxtunit.get(ar),
				tbcmbproductionType.get(ar),tbtxtpoqty.get(ar),tbtxtorderqty.get(ar)},ar);


	}
}
