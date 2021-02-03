package acc.appform.FinishedGoodsModule;


import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.AmountField;
import com.common.share.CommonButton;
import com.common.share.FocusMoveByEnter;
import com.common.share.MessageBox;
import com.common.share.ReportDate;
import com.common.share.SessionBean;
import com.common.share.SessionFactoryUtil;
import com.common.share.TextRead;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.gwt.client.ui.VLabel;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

import database.hibernate.TbCompanyInfo;
import database.hibernate.TbSubGroup;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;



import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

@SuppressWarnings("serial")
public  class FinishedGoodsInformation extends Window 
{

	private AbsoluteLayout mainLayout;

	private Label lblDate;
	private PopupDateField dDate;

	private Label lblFgID;
	private TextRead txtFgID;

	private Label lblFinishItemName;
	private TextField txtFinishItemName;

	private ComboBox cmbFindFinishItemName;

	private Label lblProductionType;
	private ComboBox cmbProductionType;

	private ComboBox cmbFindProductionType;

	private Label lblPartyName;
	private ComboBox cmbPartyName; 

	private ComboBox cmbFindPartyName; 

	private Label lblSemiFgName;
	private ComboBox cmbSemiFgName; 

	private Label lblSemiFgSubName;
	private ComboBox cmbSemiFgSubName; 

	private ComboBox cmbFindSemiFgName; 

	private Label lblUnit;
	private ComboBox cmbUnit;

	private Label lblFinishItemRate;
	private AmountField txtFinishItemRate;

	private Label lblSetQty;
	private AmountField txtSetQty;

	private Panel searchPanel;
	private Label lblSearchPanel;
	private FormLayout frmLayout = new FormLayout();

	private Label lblWeight;
	private AmountField afWeight;

	String Groupname;

	String FinishItemId="";
	String LedgerId="";

	boolean isUpdate=false;
	int index;
	SessionBean sessionBean;
	ArrayList<Component> allComp = new ArrayList<Component>();
	DecimalFormat df=new DecimalFormat("0.000");

	CommonButton button = new CommonButton("New", "Save", "Edit", "", "Refresh", "Find", "", "","","Exit");
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");

	ArrayList<Label>tbFindSl=new ArrayList<Label>();
	ArrayList<Label>tbFindFgId=new ArrayList<Label>();
	ArrayList<Label>tbFindFgName=new ArrayList<Label>();
	ArrayList<Label>tbFindUnit=new ArrayList<Label>();
	ArrayList<Label>tbFindStdWeight=new ArrayList<Label>();
	Table tableFind = new Table();

	ArrayList<Label>tbFgSl=new ArrayList<Label>();
	ArrayList<ComboBox>tbFgCmbSemiFg=new ArrayList<ComboBox>();
	ArrayList<ComboBox>tbFgCmbSemiFgSub=new ArrayList<ComboBox>();
	ArrayList<AmountField>tbFgWeight=new ArrayList<AmountField>();
	ArrayList<AmountField>tbFgQty=new ArrayList<AmountField>();
	ArrayList<AmountField>tbFgUnitPrice=new ArrayList<AmountField>();
	ArrayList<ComboBox>tbConsumptionStage=new ArrayList<ComboBox>();
	Table tableFg=new Table();

	HashMap<String, String> hmUnit=new HashMap<String, String>();
	NativeButton btnFind=new NativeButton("FIND");
	private TextRead ledgerCode = new TextRead();

	private OptionGroup optowner;
	private List <String>ownerList=Arrays.asList(new String[]{"Astech","3rdParty"});

	private OptionGroup optMultiple;
	private List <String>MultipleList=Arrays.asList(new String[]{"No","Yes"});
	
	private Label lblfgCode;
	private TextField txtfgCode;
	private FileWriter log;

	public FinishedGoodsInformation(SessionBean sessionBean) 
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("MASTER PRODUCT INFORMATION :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		setEventAction();
		btnIni(true);
		componentIni(true);
		cmbProductionTypeData();
		cmbPartyNameAddData();
		focusEnter();
		//authenticationCheck();
		//tableInitialize();
		//tableHdpeMBInitialise();
		semiFgLoadData();
		cmbFindProductionTypeLoad();
		tableFindDataLoad("%","%","%");


	}
	private void semiFgSubLoadData() 
	{
		cmbSemiFgSubName.removeAllItems();
		String sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation where semiFgId like '"+cmbSemiFgName.getValue()+"' and status='Active'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbSemiFgSubName.addItem(element[0]);
			cmbSemiFgSubName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void tableClearFind()
	{

		for(int a=0;a<tbFindFgId.size();a++)
		{
			tbFindFgId.get(a).setValue("");
			tbFindFgName.get(a).setValue("");
			tbFindUnit.get(a).setValue("");
			tbFindStdWeight.get(a).setValue("");
		}


	}
	private void tableFindDataLoad(String ProductionType, String PartyName, String FgName) {
		tableClearFind();
		String sql="select  vProductId,vProductName,vUnitName,weight from tbFinishedProductInfo where "+
				" vProductionTypeId like '"+ProductionType+"' and vCategoryId like '"+PartyName+"' and vProductId like '"+FgName+"' order by  vProductName ";
		//" cast(SUBSTRING(vProductId,CHARINDEX('-',vProductId)+1,len(vProductId)-CHARINDEX('-',vProductId))as int)";
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			if(a==tbFindSl.size()){
				tablerRowAddFind(a);
			}
			Object[] element = (Object[]) iter.next();
			tbFindFgId.get(a).setValue(element[0]);
			tbFindFgName.get(a).setValue(element[1]);
			tbFindUnit.get(a).setValue(element[2]);
			tbFindStdWeight.get(a).setValue(element[3]);

			cmbFindFinishItemName.addItem(element[0]);
			cmbFindFinishItemName.setItemCaption(element[0], element[1].toString());
			a++;
		}
	}
	private void cmbFindProductionTypeLoad() {
		cmbFindProductionType.removeAllItems();
		String sql="select distinct vProductionTypeId,vProductionTypeName from tbFinishedProductInfo";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			cmbFindProductionType.addItem(element[0].toString());
			cmbFindProductionType.setItemCaption(element[0].toString(), element[1].toString());
		}
	}
	private void semiFgLoadData() {
		cmbSemiFgName.removeAllItems();
		String sql="select distinct semiFgCode,semiFgName,color from tbSemiFgInfo where status='Active'  and semiFgCode in (select fGCode from tbFinishedGoodsStandardInfo)";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			cmbSemiFgName.addItem(element[0].toString());
			String caption=element[1].toString()+" # "+element[2].toString();
			cmbSemiFgName.setItemCaption(element[0].toString(), caption);
		}
	}
	private void focusEnter(){
		allComp.add(cmbProductionType);
		allComp.add(cmbPartyName);
		allComp.add(txtfgCode);
		allComp.add(txtFinishItemName);
		allComp.add(cmbUnit);
		allComp.add(txtFinishItemRate);
		allComp.add(afWeight);
		allComp.add(cmbSemiFgName);
		allComp.add(txtSetQty);

		for(int a=0;a<tbFgCmbSemiFg.size();a++){
			allComp.add(tbFgCmbSemiFg.get(a));
			allComp.add(tbFgCmbSemiFgSub.get(a));
			allComp.add(tbFgWeight.get(a));
			allComp.add(tbFgQty.get(a));
			allComp.add(tbFgUnitPrice.get(a));
		}
		allComp.add(cmbFindProductionType);
		allComp.add(cmbFindPartyName);
		allComp.add(cmbFindFinishItemName);
		allComp.add(btnFind);

		new FocusMoveByEnter(this, allComp);
	}
	private boolean checkValidation(){
		if(cmbProductionType.getValue()!=null){
			if(cmbPartyName.getValue()!=null){
				if(!txtFinishItemName.getValue().toString().trim().isEmpty()){
					if(cmbUnit.getValue()!=null){
						if(!txtFinishItemRate.getValue().toString().isEmpty()){
							if(!afWeight.getValue().toString().isEmpty()){
								return true;
							}
							else{
								showNotification("Please Provide Standard Weight",Notification.TYPE_WARNING_MESSAGE);
							}
						}
						else{
							showNotification("Please Provide Unit Price",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						showNotification("Please Provide Unit Name",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Provide Master Product Name",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Please Provide Party Name",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private boolean checkUnit()
	{
		if(cmbUnit.getValue().toString().equalsIgnoreCase("Set"))
		{
			// checkSecondaryStageFG();
			return checkSecondaryStageFG();

		}
		else
		{
			if(cmbUnit.getValue().toString().equalsIgnoreCase("Pcs"))
			{
				return  checkPcsProduct();
			}
		}
		return false;

	}
	private boolean checkSecondaryStageFG()
	{
		for(int i=0;i<tbFgCmbSemiFgSub.size();i++)
			if(tbConsumptionStage.get(i).getValue()!=null)
			{	
					if(tbFgCmbSemiFg.get(i).getValue()!=null)	  
					{
						return true;  
					}
			}

		showNotification("Please select semi fg From Below table",Notification.TYPE_WARNING_MESSAGE);
		return false;
	}

	
	private boolean checkPcsProduct()
	{
		
		if(cmbSemiFgName.getValue()!=null)
		{
		   return true;	
		}
		showNotification("Please select semi fg",Notification.TYPE_WARNING_MESSAGE);
		return false;
	}

	
	

	/*private boolean checkSecondaryStageFG()
	{
		for (int i = 0; i < tbFgCmbSemiFgSub.size(); i++) {
				Object temp = tbFgCmbSemiFgSub.get(i).getItemCaption(
						tbFgCmbSemiFgSub.get(i).getValue());
				System.out.println(tbFgCmbSemiFgSub.get(i).getValue());
				if (temp != null) {

						return true;
				}
					else
					{
						this.getParent().showNotification("Warning !","Please select Secondary Stage FG Name.",Notification.TYPE_WARNING_MESSAGE);
					}

				}

		return false;
	}*/

	/*	private String checkSecondaryStageFGnew()
	{
		String str = "";
		for(int i=0;i<tbFgCmbSemiFg.size();i++)
		{
			if(tbFgCmbSemiFg.get(i).getValue().toString()!=null)
			{
				if(tbFgCmbSemiFgSub.get(i).getValue().toString()==null)
				{
					str = "yes";
					break;
				}
			}
			else{
				str = "no";
			}
		}

		return str;
	}*/
	public void setEventAction()
	{

		button.btnNew.addListener( new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) 
			{
				isUpdate = false;
				newButtonEvent();
			}
		});

		button.btnEdit.addListener(new Button.ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				if(sessionBean.isUpdateable()){
					isUpdate = true;
					updateButtonEvent();
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		button.btnSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				//formValidation();
				if(checkValidation())
				{
					if(checkUnit())
					{
						saveButtonEvent();
					}
					
				}
			}
		});

		button.btnExit.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		button.btnRefresh.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});

		button.btnFind.addListener(new Button.ClickListener() 
		{
			public void buttonClick(ClickEvent event) {
				txtClear();
				findButtonEvent();
				tableFindClear();
				tableFindDataLoad("%", "%", "%");
			}
		});
		cmbUnit.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {


				disable();
			}

		});
		cmbSemiFgName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event)
			{

				if(cmbSemiFgName.getValue()!=null)
				{
					semiFgSubLoadData();
				}
				else
				{
					cmbSemiFgSubName.removeAllItems();
				}
			}
		});
		cmbFindProductionType.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbFindProductionType.getValue()!=null)
				{
					//tableFindDataLoad(""+cmbFindProductionType.getValue()+"","%","%");
					cmbFindPartyLoad();
				}
				else
				{
					cmbFindPartyName.removeAllItems();
				}
			}
		});
		cmbFindPartyName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbFindPartyName.getValue()!=null&&cmbFindProductionType.getValue()!=null){
					//tableFindDataLoad(""+cmbFindProductionType.getValue()+"",""+cmbFindPartyName.getValue()+"","%");
					cmbFindFinishItemLoad();
				}
				else
				{
					cmbFindFinishItemName.removeAllItems();
				}
			}
		});
		tableFind.addListener(new ItemClickListener() 
		{
			public void itemClick(ItemClickEvent event) 
			{
				if(event.isDoubleClick())
				{
					findInitialize(tbFindFgId.get(Integer.valueOf(event.getItemId().toString())).getValue().toString());
					searchPanel.setEnabled(false);
				}
			}
		});
		btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				txtClear();
				tableFg.setVisible(false);
				searchFindEvent();
				btnIni(true);
			}
		});
	}

	public void disable() 
	{
		if(cmbUnit.getValue()!=null){
			if(cmbUnit.getValue().toString().equalsIgnoreCase("Set")){
				tableFg.setVisible(true);
				cmbSemiFgName.setEnabled(false);
				cmbSemiFgSubName.setEnabled(false);
				txtFinishItemRate.setEnabled(false);
				afWeight.setEnabled(false);
			}
			else if(cmbUnit.getValue().toString().equalsIgnoreCase("Pcs"))
			{
				tableFg.setVisible(false);
				//cmbSemiFgName.setEnabled(true);
				//cmbSemiFgSubName.setEnabled(false);
				txtFinishItemRate.setEnabled(true);
				afWeight.setEnabled(true);
			}
		}

		else{
			tableFg.setVisible(true);
			//cmbSemiFgName.setEnabled(true);
			//cmbSemiFgSubName.setEnabled(true);
			txtFinishItemRate.setEnabled(true);
			afWeight.setEnabled(true);
		}
	}
	private void emailSend() 
	{

		//public static String emailPath = "D:/Tomcat 7.0/webapps/report/astecherp/Email/";

		ReportDate reportTime = new ReportDate();

		System.out.printf("1");
		//HashMap hm = new HashMap();
		try
		{
			System.out.printf("2");
			File f = new File(sessionBean.emailPathmasterproduct);
			f.mkdirs();
			System.out.printf("3");
			System.out.printf("f"+f);
			String MasterId="";
			log = new FileWriter("D:/Tomcat 7.0/webapps/report/astecherp/MasterProduct/log.txt");
			System.out.printf("log"+log);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();


			String host = "smtp.gmail.com";
			String from = "";
			String pass = "";


			from="evisionsoftwareltd@gmail.com";
			pass="786@esl10";


			String EmailTo="support@eslctg.com";
			String EmailSubject="Master Product Information Edit";
			String EmailTxt="Master Product Name "+txtFinishItemName.getValue().toString()+" has been Edited \n"+
					"by User Name: "+sessionBean.getUserName()+", \nUser IP: "+sessionBean.getUserIp()+" \n and Date Time: "+reportTime.getTime+" , " +
					"and please check here with attached PDF report";

			System.out.printf("\nHost"+from);
			System.out.printf("\nPass"+pass);

			Properties props = System.getProperties();
			props.put("mail.smtp.starttls.enable", "true"); // added this line
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.user", from);
			props.put("mail.smtp.password", pass);
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");

			javax.mail.Session esession = javax.mail.Session.getDefaultInstance(props, null);
			MasterId=txtFgID.getValue().toString();

			System.out.printf("4");
			System.out.printf("\n4.1"+MasterId);
			reportGenerate(MasterId,sessionBean.emailPathmasterproduct+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");



			MimeMessage message = new MimeMessage(esession);


			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("costing@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("ahmedtalba@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("store@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("astechfty@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("jafarahmed@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("rahman@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("rabiulbgcctg2016@gmail.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("nazimesl@yahoo.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("emdidar@gmail.com"));


			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("akidahmed@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("sharif@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("ashim@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("hr.desk@astechbd.com"));
			//message.addRecipient(Message.RecipientType.CC, new InternetAddress("nazimesl@yahoo.com"));



			message.setSubject(EmailSubject);
			message.setText(EmailTxt);
			System.out.printf("7");
			// create the message part 
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			//fill message
			messageBodyPart.setText(EmailTxt);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();

			DataSource source = (DataSource) new FileDataSource(sessionBean.emailPathmasterproduct+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			System.out.println("Hello Banglaedsh3");
			messageBodyPart.setDataHandler( new DataHandler((javax.activation.DataSource) source));
			messageBodyPart.setFileName(MasterId+"_"+"_"+EmailSubject+".pdf");
			multipart.addBodyPart(messageBodyPart);
			System.out.printf("9");
			// Put parts in message
			message.setContent(multipart);
			System.out.printf("10");
			Transport transport = esession.getTransport("smtp");
			System.out.println(sessionBean.emailPathmasterproduct+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			System.out.printf("11");
			System.out.printf("host "+host+" from "+from+" pass "+pass);
			transport.connect(host, from, pass);
			System.out.printf("12");
			transport.sendMessage(message, message.getAllRecipients());
			System.out.printf("13");
			transport.close();
			System.out.printf("14");
			//log.write("Info:"+"E-mail Send for client id: "+MasterId+"\n");
			System.out.printf("15");
			this.getParent().showNotification("E-mail Send Successfully.");	
		}
		catch(Exception exp)
		{
			showNotification("mail send :"+exp,Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void reportGenerate(String iclientId, String fpath) throws HibernateException, JRException, IOException 
	{	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String query = "";
		ReportDate reportTime = new ReportDate();

		query=   "select a.UserId,a.UserIP,a.EntryTime,b.vFlag,a.vProductionTypeId,a.vProductionTypeName," +
				"a.vCategoryId,a.vCategoryName,a.vProductId,a.vProductName,vUnitName,a.mDealerPrice,a.weight," +
				"semiFgId,semiFgName,semiFgSubId,semiFgSubName,vSizeName as packetqty,b.stdWeight,b.qty," +
				"b.unitPrice,b.consumptionStage  from tbudmasterProductInfo a" +
				" inner join tbudFinishedProductDetailsNew b on a.vProductId=b.fgId and a.itransactionId=b.udtransactionId where  " +
				" a.vProductId like '"+txtFgID.getValue()+"'";

		System.out.println("sql is:"+query);

		if(queryValueCheck(query))
		{
			HashMap <String,Object> hm = new HashMap <String,Object> ();
			hm.put("company", sessionBean.getCompany());
			hm.put("address", sessionBean.getCompanyAddress());
			hm.put("phone", sessionBean.getCompanyContact());
			//hm.put("phone", "Phone : "+sessionBean.get+" Fax : "+sessionBean.getCompanyFax()+" E-Mail : "+sessionBean.getCompanyEmail());
			//System.out.println(sessionBean.getCompanyPhone());
			hm.put("UserName", sessionBean.getUserName()+"  "+sessionBean.getUserIp());
			hm.put("URL",getApplication().getURL().toString().replace("uptd/", ""));
			hm.put("logo", sessionBean.getCompanyLogo());
			hm.put("parentType", "Master Product Information");
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("sql", query);
			System.out.println("Done!!");
			FileOutputStream of = new FileOutputStream(fpath);

			try
			{
				JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/production/rptUDFgInformation.jasper"), of, hm,session.connection());
				System.out.println("Done!!");	
			}

			catch(Exception ex)
			{
				System.out.print("Exception is:"+ex)	;
			}

			tx.commit();
			of.close();
		}		
	}

	private boolean queryValueCheck(String sql)
	{
		Session session=SessionFactoryUtil.getInstance().openSession();
		session.beginTransaction();
		try 
		{
			List <?> lst = session.createSQLQuery(sql).list();
			if (!lst.isEmpty()) 
			{
				return true;
			}
		} 
		catch (Exception ex) 
		{
			System.out.print(ex);
		}
		finally{session.close();}
		return false;
	}



	private void findInitialize(String fgId) 
	{

		String sql="select vProductionTypeId,vCategoryId,vProductId,vProductName,vUnitName,mDealerPrice,weight, "+
				" b.semiFgId,b.semiFgSubId,b.stdWeight,b.qty,b.unitPrice,vSizeName,dDate,vLedgerId,isnull(vFgCode,''),b.consumptionStage,ownerstatus,isFlag  "+
				" from tbFinishedProductInfo a  left join tbFinishedProductDetailsNew b on a.vProductId=b.fgId "+
				" where a.vProductId like '"+fgId+"'";
		Iterator iter=dbService(sql);
		int a=0;
		while(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionType.setValue(element[0]);
			cmbPartyName.setValue(element[1]);
			txtFgID.setValue(element[2]);
			txtfgCode.setValue(element[15]);
			txtFinishItemName.setValue(element[3]);
			ledgerCode.setValue(element[14].toString());
			cmbUnit.setValue(element[4]);
			optowner.setValue(element[17]);
			optMultiple.setValue(element[18]);
			if(cmbUnit.getValue().toString().equalsIgnoreCase("Pcs")){
				txtFinishItemRate.setValue(element[5]);
				afWeight.setValue(element[6]);
				cmbSemiFgName.setValue(element[7]);
				cmbSemiFgSubName.setValue(element[8]);
				txtSetQty.setValue(element[12]);
				dDate.setValue(element[13]);
				
				
			}
			else{
				txtSetQty.setValue(element[12]);
				tbFgCmbSemiFg.get(a).setValue(element[7]);
				tbFgCmbSemiFgSub.get(a).setValue(element[8]);
				tbFgWeight.get(a).setValue(element[9]);
				tbFgQty.get(a).setValue(element[10]);
				tbFgUnitPrice.get(a).setValue(element[11]);
				tbConsumptionStage.get(a).setValue(element[16]);
			}
			a++;
		}
	}
	private boolean deleteData(String fgId,int udid){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();

			String udsql=	"insert into tbudFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,stdWeight,qty,unitPrice,consumptionStage,vFlag,udtransactionId) "
					+ "select fgId,fgName,semiFgId,semiFgName,stdWeight,qty,unitPrice, "
					+"consumptionStage,'UPDATE' as vFlag,'"+udid+"' from tbFinishedProductDetailsNew where fgid='"+fgId+"' ";
			session.createSQLQuery(udsql).executeUpdate();

			session.createSQLQuery("delete from tbFinishedProductDetailsNew where fgid = '"+fgId+"'").executeUpdate();
			System.out.println("Hello");
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("From Delete: "+exp,Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		finally{
			if(tx!=null&&session!=null){
				tx.commit();
				return true;
			}
		}
		return true;
	}
	private boolean updateData()
	{
		String createForm = "";
		String subGroup = "";

		String parentId="";

		createForm = "I1-"+cmbPartyName.getValue().toString();
		subGroup = "";

		System.out.println("AutoID: "+createForm);
		int udid=0;
		udid=udAutoId();


		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			String fgId=txtFgID.getValue().toString().trim();
			String udsql="insert into tbudmasterProductInfo (vProductionTypeId,vProductionTypeName,vCategoryId,vCategoryName,vProductId,  "
					+"vProductName,vUnitName,mDealerPrice,weight,vSizeName,dDate,imageLocation,isActive,UserId,userIp,EntryTime,vLedgerId,vFgCode,vFlag,itransactionId,ownerstatus,isFlag) "
					+"select vProductionTypeId,vProductionTypeName,vCategoryId,vCategoryName, "
					+"vProductId,vProductName,vUnitName,mDealerPrice,weight,vSizeName,dDate, "
					+"imageLocation,isActive,UserId,UserIP,EntryTime,vLedgerId,vFgcode,'UPDATE','"+udid+"',ownerstatus,isFlag from tbFinishedProductInfo where " 
					+"vFgCode='"+txtfgCode.getValue().toString()+"' and vProductId='"+txtFgID.getValue().toString()+"' ";
			session.createSQLQuery(udsql).executeUpdate();

			String updateFinishItem ="UPDATE tbFinishedProductInfo set" +
					" vProductName = '"+txtFinishItemName.getValue().toString().trim()+"' ," +
					" vProductionTypeId = '"+cmbProductionType.getValue()+"' ," +
					" vProductionTypeName = '"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"' ," +
					" vCategoryId = '"+cmbPartyName.getValue().toString()+"' ," +
					" vCategoryName = '"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"' ," +
					" vUnitName = '"+cmbUnit.getValue()+"' ," +
					" mDealerPrice = '"+txtFinishItemRate.getValue().toString()+"' ," +
					" UserId = '"+sessionBean.getUserId()+"', " +
					" UserIp = '"+sessionBean.getUserIp()+"', " +
					" vSizeName = '"+(txtSetQty.getValue().toString().isEmpty()?"":txtSetQty.getValue().toString())+"', " +
					" EntryTime = CURRENT_TIMESTAMP " +
					",weight='"+afWeight.getValue()+"'"+
					",dDate='"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue())+"',vFgCode='"+txtfgCode.getValue().toString()+"',ownerstatus='"+optowner.getValue().toString()+"',isFlag='"+optMultiple.getValue().toString()+"' "+
					" where vProductId='"+txtFgID.getValue().toString()+"' ";

			System.out.println("UpdateFinishItem: "+updateFinishItem);
			session.createSQLQuery(updateFinishItem).executeUpdate();

			udsql="insert into tbudmasterProductInfo (vProductionTypeId,vProductionTypeName,vCategoryId,vCategoryName,vProductId, "+
					" vProductName,vUnitName,mDealerPrice,weight,vSizeName,dDate,imageLocation,isActive,UserId,userIp,EntryTime,vLedgerId,vFgCode,vFlag,itransactionId,ownerstatus,isFlag)values" +
					"('"+cmbProductionType.getValue()+"','"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"','"+cmbPartyName.getValue()+"', "+
					" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"','"+fgId+"','"+txtFinishItemName.getValue()+"'," +
					"'"+cmbUnit.getValue()+"','"+txtFinishItemRate.getValue()+"','"+afWeight.getValue()+"','"+txtSetQty.getValue()+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue())+"',0,1,'"+sessionBean.getUserName()+"'," +
					"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+LedgerId+"','"+txtfgCode.getValue().toString()+"','NEW','"+udid+"','"+optowner.getValue().toString()+"','"+optMultiple.getValue().toString()+"')";
			session.createSQLQuery(udsql).executeUpdate();



			String id=txtFgID.getValue().toString();
			if(deleteData(id,udid))
			{
				if(cmbUnit.getValue().toString().equalsIgnoreCase("Pcs"))
				{
					String semiFgId="",semiFgName="",semiFgSubId="",semiFgSubName="";
					if(cmbSemiFgName.getValue()!=null){
						semiFgId=cmbSemiFgName.getValue().toString();
						semiFgName=cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue());
					}
					if(cmbSemiFgSubName.getValue()!=null){
						semiFgSubId=cmbSemiFgSubName.getValue().toString();
						semiFgSubName=cmbSemiFgSubName.getItemCaption(cmbSemiFgSubName.getValue());
					}
					String sql1="insert into tbFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,semiFgSubId,semiFgSubName,stdWeight,qty,unitPrice,consumptionStage)" +
							"values('"+id+"','"+txtFinishItemName.getValue()+"','"+semiFgId+"'," +
							"'"+semiFgName+"','"+semiFgSubId+"','"+semiFgSubName+"','"+afWeight.getValue()+"','1','"+txtFinishItemRate.getValue()+"','Delivery Challan')";
					session.createSQLQuery(sql1).executeUpdate();

					String uddetails="insert into tbudFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,stdWeight,qty,unitPrice,consumptionStage,vFlag,itransactionId ,udtransactionId)" +
							"values('"+fgId+"','"+txtFinishItemName.getValue()+"','"+semiFgId+"'," +
							"'"+semiFgName+"','"+afWeight.getValue()+"','1','"+txtFinishItemRate.getValue()+"','Delivery Challan','NEW','"+udid+"','"+udid+"')";
					session.createSQLQuery(uddetails).executeUpdate();




				}
				else{
					for(int a=0;a<tbFgCmbSemiFg.size();a++)
					{
						String semiFgId="",semiFgName="";
						if(tbFgCmbSemiFg.get(a).getValue()!=null)
						{
							semiFgId=tbFgCmbSemiFg.get(a).getValue().toString();
							semiFgName=tbFgCmbSemiFg.get(a).getItemCaption(tbFgCmbSemiFg.get(a).getValue());
						}
						String semiFgSubId="",semiFgSubName="";
						if(tbFgCmbSemiFgSub.get(a).getValue()!=null)
						{
							semiFgSubId=tbFgCmbSemiFgSub.get(a).getValue().toString();
							semiFgSubName=tbFgCmbSemiFgSub.get(a).getItemCaption(tbFgCmbSemiFgSub.get(a).getValue());
						}
						if(tbFgCmbSemiFg.get(a).getValue()!=null)
						{
							String sql1="insert into tbFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,semiFgSubId,semiFgSubName,"+
									" stdWeight,qty,unitPrice,consumptionStage)values('"+id+"','"+txtFinishItemName.getValue()+"','"+semiFgId+"'," +
									"'"+semiFgName+"','"+semiFgSubId+"'," +
									"'"+semiFgSubName+"'," +
									"'"+tbFgWeight.get(a).getValue()+"','"+tbFgQty.get(a).getValue()+"','"+tbFgUnitPrice.get(a).getValue()+"','"+tbConsumptionStage.get(a).getValue()+"')";
							session.createSQLQuery(sql1).executeUpdate();

							String uddetails="insert into tbudFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,semiFgSubId,semiFgSubName,"+
									" stdWeight,qty,unitPrice,consumptionStage,vFlag,udtransactionId)values('"+fgId+"','"+txtFinishItemName.getValue()+"','"+semiFgId+"'," +
									"'"+semiFgName+"','"+semiFgSubId+"'," +
									"'"+semiFgSubName+"'," +
									"'"+tbFgWeight.get(a).getValue()+"','"+tbFgQty.get(a).getValue()+"','"+tbFgUnitPrice.get(a).getValue()+"','"+tbConsumptionStage.get(a).getValue()+"','NEW','"+udid+"')";
							session.createSQLQuery(uddetails).executeUpdate();
						}
					}
				}

			}

			//String id=txtFinishItemCode.getValue().toString();
			parentId = cmbPartyName.getValue().toString();

			String UpdateLedger = "UPDATE tbLedger set" +
					" Ledger_Name = '"+txtFinishItemName.getValue().toString().trim()+"', " +
					" Parent_Id = '"+parentId+"', " +
					" Create_From = '"+createForm+"', " +
					" userId = '"+sessionBean.getUserId()+"', " +
					" userIp = '"+sessionBean.getUserIp()+"', " +
					" entryTime = CURRENT_TIMESTAMP " +
					" where Ledger_Id='"+ledgerCode.getValue()+"'";

			System.out.println("UpdateLedger: "+UpdateLedger);
			session.createSQLQuery(UpdateLedger).executeUpdate();


			this.getParent().showNotification("All information updated successfully.");

			tx.commit();

			return true;
		}
		catch(Exception exp)
		{
			tx.rollback();
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}

	private int udAutoId()
	{
		Transaction tx=null;
		Session session=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=session.beginTransaction();
		String sql="select ISNULL( MAX(itransactionId),0)+1 from tbudmasterProductInfo";
		List lst=session.createSQLQuery(sql).list();
		Iterator<?>itr=lst.iterator();
		if(itr.hasNext())
		{
			return  (Integer) itr.next();
		}
		return 0;
	}

	private void tableFindClear(){
		cmbFindFinishItemName.setValue(null);
		cmbFindPartyName.setValue(null);
		cmbFindProductionType.setValue(null);
		for(int a=0;a<tbFindFgId.size();a++){
			tbFindFgId.get(a).setValue("");
			tbFindFgName.get(a).setValue("");
			tbFindUnit.get(a).setValue("");
			tbFindStdWeight.get(a).setValue("");
		}
	}
	private void searchFindEvent(){
		String type="%",party="%",fgName="%";
		if(cmbFindProductionType.getValue()!=null){
			type=cmbFindProductionType.getValue().toString();
		}
		if(cmbFindPartyName.getValue()!=null){
			party=cmbFindPartyName.getValue().toString();
		}
		if(cmbFindFinishItemName.getValue()!=null){
			fgName=cmbFindFinishItemName.getValue().toString();
		}
		tableFindDataLoad(type, party, fgName);
	}
	private void cmbFindFinishItemLoad(){
		cmbFindFinishItemName.removeAllItems();
		String sql="select distinct vProductId,vProductName from tbFinishedProductInfo where vProductionTypeId like '"+cmbFindProductionType.getValue()+"' and vCategoryId like '"+cmbFindPartyName.getValue()+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			cmbFindFinishItemName.addItem(element[0].toString());
			cmbFindFinishItemName.setItemCaption(element[0].toString(), element[1].toString());
		}
	}
	private void cmbFindPartyLoad(){
		cmbFindPartyName.removeAllItems();
		String sql="select distinct vCategoryId,vCategoryName from tbFinishedProductInfo where vProductionTypeId like '"+cmbFindProductionType.getValue()+"'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			cmbFindPartyName.addItem(element[0].toString());
			cmbFindPartyName.setItemCaption(element[0].toString(), element[1].toString());
		}
	}


	protected void newButtonEvent() 
	{
		componentIni(false);
		searchPanel.setEnabled(false);
		btnIni(false);
		txtClear();
		cmbProductionType.focus();

	}


	protected void formValidation() 
	{

		if(!txtFinishItemName.getValue().toString().isEmpty())
		{
			if(cmbPartyName.getValue()!=null)
			{
				if(cmbProductionType.getValue()!=null){

					if(!cmbUnit.getValue().toString().isEmpty())
					{
						if(!txtFinishItemRate.getValue().toString().isEmpty())
						{
							saveButtonEvent();
						}
						else
						{
							getParent().showNotification("Warning","Provide Unit Price",Notification.TYPE_WARNING_MESSAGE);
							txtFinishItemRate.focus();
						}
					}
					else
					{
						getParent().showNotification("Warning","Please provide unit",Notification.TYPE_WARNING_MESSAGE);
						cmbUnit.focus();
					}
				}
				else{
					getParent().showNotification("Warning","Please select Production Type",Notification.TYPE_WARNING_MESSAGE);
					cmbProductionType.focus();
				}
			}
			else
			{
				getParent().showNotification("Warning","Please select Party Name",Notification.TYPE_WARNING_MESSAGE);
				cmbPartyName.focus();
			}
		}
		else
		{
			getParent().showNotification("Warning","Please provide Item name",Notification.TYPE_WARNING_MESSAGE);
			txtFinishItemName.focus();
		}
	}
	public String ledgerId()
	{
		/*String ledgerId = "";
		Transaction tx = null;

		try 
		{
			Session session = SessionFactoryUtil.getInstance().openSession();
			tx = session.beginTransaction();*/
		String ledgerId = "";
		String query = " Select cast(isnull(max(cast(replace(Ledger_Id, 'IL', '')as int))+1, 1)as varchar) from tbLedger" +
				" where Ledger_Id like 'IL%' ";
		//Iterator iter = session.createSQLQuery(query).list().iterator();
		Iterator iter=dbService(query);

		if (iter.hasNext()) 
		{
			ledgerId = "IL"+iter.next().toString();
		}
		/*}
		catch (Exception ex) 
		{
			System.out.print(ex);
		}*/

		return ledgerId;
	}
	private String getFgId(){
		String sql="select isnull(max(cast(SUBSTRING(vProductId,CHARINDEX('-',vProductId)+1,len(vProductId)- "+
				" CHARINDEX('-',vProductId))as int)),0)+1 from tbFinishedProductInfo";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			Object element=iter.next();
			return "FI-"+element.toString();
		}
		return null;
	}
	private void insertData(){
		String fgId="";
		if(isUpdate){
			fgId=txtFgID.getValue().toString().trim();
		}
		else{
			fgId=getFgId();
		}
		String createForm = "";
		String parentId = "";

		LedgerId=ledgerId();

		createForm = "I1-"+cmbPartyName.getValue().toString();
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			String sql="insert into tbFinishedProductInfo (vProductionTypeId,vProductionTypeName,vCategoryId,vCategoryName,vProductId, "+
					" vProductName,vUnitName,mDealerPrice,weight,vSizeName,dDate,imageLocation,isActive,UserId,userIp,EntryTime,vLedgerId,vFgCode,ownerstatus,isFlag)values" +
					"('"+cmbProductionType.getValue()+"','"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"','"+cmbPartyName.getValue()+"', "+
					" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"','"+fgId+"','"+txtFinishItemName.getValue()+"'," +
					"'"+cmbUnit.getValue()+"','"+txtFinishItemRate.getValue()+"','"+afWeight.getValue()+"','"+txtSetQty.getValue()+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue())+"',0,1,'"+sessionBean.getUserName()+"'," +
					"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+LedgerId+"','"+txtfgCode.getValue().toString()+"','"+optowner.getValue().toString()+"','"+optMultiple.getValue().toString()+"')";
			session.createSQLQuery(sql).executeUpdate();


			String udsql="insert into tbudmasterProductInfo (vProductionTypeId,vProductionTypeName,vCategoryId,vCategoryName,vProductId, "+
					" vProductName,vUnitName,mDealerPrice,weight,vSizeName,dDate,imageLocation,isActive,UserId,userIp,EntryTime,vLedgerId,vFgCode,vFlag,ownerstatus,isFlag)values" +
					"('"+cmbProductionType.getValue()+"','"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"','"+cmbPartyName.getValue()+"', "+
					" '"+cmbPartyName.getItemCaption(cmbPartyName.getValue())+"','"+fgId+"','"+txtFinishItemName.getValue()+"'," +
					"'"+cmbUnit.getValue()+"','"+txtFinishItemRate.getValue()+"','"+afWeight.getValue()+"','"+txtSetQty.getValue()+"'," +
					"'"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDate.getValue())+"',0,1,'"+sessionBean.getUserName()+"'," +
					"'"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+LedgerId+"','"+txtfgCode.getValue().toString()+"','NEW','"+optowner.getValue().toString()+"','"+optMultiple.getValue().toString()+"')";
			session.createSQLQuery(udsql).executeUpdate();

			if(cmbUnit.getValue().toString().equalsIgnoreCase("Pcs"))
			{
				String semiFgId="",semiFgName="";
				if(cmbSemiFgName.getValue()!=null){
					semiFgId=cmbSemiFgName.getValue().toString();
					semiFgName=cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue());
				}


				String sql1="insert into tbFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,stdWeight,qty,unitPrice,consumptionStage)" +
						"values('"+fgId+"','"+txtFinishItemName.getValue()+"','"+semiFgId+"'," +
						"'"+semiFgName+"','"+afWeight.getValue()+"','1','"+txtFinishItemRate.getValue()+"','Delivery Challan')";
				session.createSQLQuery(sql1).executeUpdate();


				String uddetails="insert into tbudFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,stdWeight,qty,unitPrice,consumptionStage,vFlag)" +
						"values('"+fgId+"','"+txtFinishItemName.getValue()+"','"+semiFgId+"'," +
						"'"+semiFgName+"','"+afWeight.getValue()+"','1','"+txtFinishItemRate.getValue()+"','Delivery Challan','NEW')";
				session.createSQLQuery(uddetails).executeUpdate();


			}
			else{
				for(int a=0;a<tbFgCmbSemiFg.size();a++)
				{
					String semiFgId="",semiFgName="";
					if(tbFgCmbSemiFg.get(a).getValue()!=null)
					{
						semiFgId=tbFgCmbSemiFg.get(a).getValue().toString();
						semiFgName=tbFgCmbSemiFg.get(a).getItemCaption(tbFgCmbSemiFg.get(a).getValue());
					}
					String semiFgSubId="",semiFgSubName="";
					if(tbFgCmbSemiFgSub.get(a).getValue()!=null)
					{
						semiFgSubId=tbFgCmbSemiFgSub.get(a).getValue().toString();
						semiFgSubName=tbFgCmbSemiFgSub.get(a).getItemCaption(tbFgCmbSemiFgSub.get(a).getValue());
					}
					if(tbFgCmbSemiFg.get(a).getValue()!=null){
						String sql1="insert into tbFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,semiFgSubId,semiFgSubName,"+
								" stdWeight,qty,unitPrice,consumptionStage)values('"+fgId+"','"+txtFinishItemName.getValue()+"','"+semiFgId+"'," +
								"'"+semiFgName+"','"+semiFgSubId+"'," +
								"'"+semiFgSubName+"'," +
								"'"+tbFgWeight.get(a).getValue()+"','"+tbFgQty.get(a).getValue()+"','"+tbFgUnitPrice.get(a).getValue()+"','"+tbConsumptionStage.get(a).getValue()+"')";
						session.createSQLQuery(sql1).executeUpdate();


						String uddetails="insert into tbudFinishedProductDetailsNew (fgId,fgName,semiFgId,semiFgName,semiFgSubId,semiFgSubName,"+
								" stdWeight,qty,unitPrice,consumptionStage,vFlag)values('"+fgId+"','"+txtFinishItemName.getValue()+"','"+semiFgId+"'," +
								"'"+semiFgName+"','"+semiFgSubId+"'," +
								"'"+semiFgSubName+"'," +
								"'"+tbFgWeight.get(a).getValue()+"','"+tbFgQty.get(a).getValue()+"','"+tbFgUnitPrice.get(a).getValue()+"','"+tbConsumptionStage.get(a).getValue()+"','NEW')";
						session.createSQLQuery(uddetails).executeUpdate();


					}
				}
			}

			parentId = cmbPartyName.getValue().toString();

			String InsertLedger="INSERT into tbLedger values(" +
					" '"+LedgerId+"', " +
					" '"+txtFinishItemName.getValue().toString().trim()+"', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+parentId+"', " +
					" '"+createForm+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			session.createSQLQuery(InsertLedger).executeUpdate();

			String LedgerOpen="INSERT into tbLedger_Op_Balance values(" +
					" '"+LedgerId+"', " +
					" '0.00', " +
					" '0.00', " +
					" '"+dateFormat.format(sessionBean.getFiscalOpenDate())+"', " +
					" '"+sessionBean.getUserId()+"','"+sessionBean.getUserIp()+"', " +
					" CURRENT_TIMESTAMP, '"+sessionBean.getCompanyId()+"') ";

			session.createSQLQuery(LedgerOpen).executeUpdate();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification(""+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				session.close();
			}
			showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
		}
	}
	private boolean deleteData(){

		return false;
	}


	private void saveButtonEvent() 
	{
		if(isUpdate)
		{
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						updateData();
						//emailSend();
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();
						button.btnNew.focus();
					}
				}
			});																	
		}
		else
		{									
			MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						insertData();
						isUpdate = false;
						btnIni(true);
						componentIni(true);
						txtClear();

					}
				}
			});
		}

	}



	protected void updateButtonEvent() 
	{
		if(!txtFgID.getValue().toString().isEmpty())
		{
			btnIni(false);
			componentIni(false);//Enable(true);
			disable();
			searchPanel.setEnabled(false);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}

	}
	protected void refreshButtonEvent() 
	{
		componentIni(true);
		//tableFind.setEnabled(true);
		btnIni(true);
		txtClear();
	}

	protected void findButtonEvent() 
	{
		componentIni(true);
		searchPanel.setEnabled(true);
	}
	private void componentIni(boolean b)
	{

		txtFinishItemName.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		cmbUnit.setEnabled(!b);
		txtSetQty.setEnabled(!b);
		dDate.setEnabled(!b);
		txtFinishItemRate.setEnabled(!b);
		afWeight.setEnabled(!b);
		tableFg.setEnabled(!b);
		searchPanel.setEnabled(!b);
		txtFgID.setEnabled(!b);
		txtfgCode.setEnabled(!b);
		cmbSemiFgName.setEnabled(!b);
		cmbSemiFgSubName.setEnabled(!b);
		optowner.setEnabled(!b);
		optMultiple.setEnabled(!b);
	}
	private void txtClear() 
	{
		//tableClear();
		txtFgID.setValue("");
		txtfgCode.setValue("");
		txtFinishItemName.setValue("");
		cmbPartyName.setValue(null);
		cmbProductionType.setValue(null);
		cmbSemiFgName.setValue(null);
		cmbSemiFgSubName.setValue(null);
		cmbUnit.setValue(null);
		dDate.setValue(new Date());
		cmbUnit.setValue("");
		txtSetQty.setValue("");
		txtFinishItemRate.setValue("");
		afWeight.setValue("");
		optowner.setValue("Astech");
		optMultiple.setValue("No");
		tableClear();

	}
	private void tableClear(){
		for(int a=0;a<tbFgCmbSemiFg.size();a++){
			//tbFgCmbSemiFg.get(a).setValue(null);
			tbFgCmbSemiFgSub.get(a).setValue(null);
			tbFgCmbSemiFg.get(a).setValue(null);
			tbFgWeight.get(a).setValue("");
			tbFgQty.get(a).setValue("");
			tbFgUnitPrice.get(a).setValue("");
			tbConsumptionStage.get(a).setValue(null);
		}
	}
	private void btnIni(boolean t) 
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnFind.setEnabled(t);

	}

	private void cmbProductionTypeData() {
		cmbProductionType.removeAllItems();
		String sql="select productTypeId,productTypeName from tbProductionType order by autoId";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			cmbProductionType.addItem(element[0].toString());
			cmbProductionType.setItemCaption(element[0].toString(), element[1].toString());
		}
	}
	public void cmbPartyNameAddData()
	{
		cmbPartyName.removeAllItems();
		String sql="select vGroupId,partyName from tbPartyInfo where isActive = '1' order by autoId";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			cmbPartyName.addItem(element[0].toString());
			cmbPartyName.setItemCaption(element[0].toString(), element[1].toString());
		}
	}

	private Iterator dbService(String sql){
		Transaction tx=null;
		Session session=null;
		Iterator iter=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			iter= session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				session.close();
			}
		}
		return iter;
	}

	private AbsoluteLayout buildMainLayout()
	{
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		setWidth("1000px");
		setHeight("710px");

		btnFind.setImmediate(true);
		btnFind.setWidth("100px");
		btnFind.setHeight("25px");

		lblProductionType = new Label("Production Type : ");
		lblProductionType.setImmediate(true);
		lblProductionType.setWidth("100.0%");
		lblProductionType.setHeight("18px");

		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setNullSelectionAllowed(true);
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblPartyName = new Label("Party Name : ");
		lblPartyName.setImmediate(false);
		lblPartyName.setWidth("100.0%");
		lblPartyName.setHeight("18px");

		cmbPartyName =new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("260px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setNullSelectionAllowed(true);
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		cmbFindPartyName =new ComboBox("Party Name: ");
		cmbFindPartyName.setImmediate(true);
		cmbFindPartyName.setWidth("260px");
		cmbFindPartyName.setHeight("24px");
		cmbFindPartyName.setNullSelectionAllowed(true);
		cmbFindPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblFgID = new Label("Master Product Id : ");
		lblFgID.setImmediate(true);
		lblFgID.setWidth("100.0%");
		lblFgID.setHeight("18px");

		txtFgID = new TextRead();
		txtFgID.setImmediate(false);
		txtFgID.setWidth("100.0px");
		txtFgID.setHeight("24px");

		lblfgCode = new Label("Maste Product Code : ");
		lblfgCode.setImmediate(true);
		lblfgCode.setWidth("100.0%");
		lblfgCode.setHeight("18px");

		txtfgCode = new TextField();
		txtfgCode.setImmediate(false);
		txtfgCode.setWidth("100.0px");
		txtfgCode.setHeight("24px");

		lblFinishItemName = new Label("Master Product Name : ");
		lblFinishItemName.setImmediate(false);
		lblFinishItemName.setWidth("100.0%");
		lblFinishItemName.setHeight("18px");

		txtFinishItemName = new TextField();
		txtFinishItemName.setImmediate(false);
		txtFinishItemName.setWidth("260px");
		txtFinishItemName.setHeight("24px");

		cmbFindFinishItemName = new ComboBox("Master Product Name: ");
		cmbFindFinishItemName.setImmediate(false);
		cmbFindFinishItemName.setWidth("260px");
		cmbFindFinishItemName.setHeight("24px");
		cmbFindFinishItemName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbFindFinishItemName.setNullSelectionAllowed(true);

		lblUnit = new Label("Unit :");
		lblUnit.setImmediate(false);
		lblUnit.setWidth("100px");
		lblUnit.setHeight("18px");

		cmbUnit = new ComboBox();
		cmbUnit.setImmediate(true);
		cmbUnit.setWidth("100.0px");
		cmbUnit.setHeight("24px");
		cmbUnit.addItem("Set");
		cmbUnit.addItem("Pcs");
		cmbUnit.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblFinishItemRate = new Label("Unit Price :");
		lblFinishItemRate.setImmediate(false);
		lblFinishItemRate.setWidth("100px");
		lblFinishItemRate.setHeight("18px");

		txtFinishItemRate = new AmountField();
		txtFinishItemRate.setImmediate(true);
		txtFinishItemRate.setWidth("100px");
		txtFinishItemRate.setHeight("24px");

		lblWeight = new Label("Std Weight :");
		lblWeight.setImmediate(false);
		lblWeight.setWidth("100px");
		lblWeight.setHeight("18px");

		afWeight = new AmountField();
		afWeight.setImmediate(true);
		afWeight.setWidth("100px");
		afWeight.setHeight("24px");

		lblSemiFgName = new Label("Semi FG Name : ");
		lblSemiFgName.setImmediate(false);
		lblSemiFgName.setWidth("100px");
		lblSemiFgName.setHeight("18px");

		cmbSemiFgName =new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("260px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setNullSelectionAllowed(true);
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblSemiFgSubName =  new Label("Secondary Stage FG Name : ");
		lblSemiFgSubName.setImmediate(false);
		lblSemiFgSubName.setWidth("-1px");
		lblSemiFgSubName.setHeight("18px");

		cmbSemiFgSubName =new ComboBox();
		cmbSemiFgSubName.setImmediate(true);
		cmbSemiFgSubName.setWidth("260px");
		cmbSemiFgSubName.setHeight("24px");
		cmbSemiFgSubName.setNullSelectionAllowed(true);
		cmbSemiFgSubName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		lblSetQty = new Label("Packing Qty(PCS) :");
		lblSetQty.setImmediate(false);
		lblSetQty.setWidth("100px");
		lblSetQty.setHeight("18px");

		txtSetQty = new AmountField();
		txtSetQty.setImmediate(true);
		txtSetQty.setWidth("100px");
		txtSetQty.setHeight("24px");





		lblDate = new Label("Date :");
		lblDate.setImmediate(false);
		lblDate.setWidth("100px");
		lblDate.setHeight("18px");

		dDate = new PopupDateField();
		dDate.setImmediate(true);
		dDate.setWidth("110px");
		dDate.setDateFormat("dd-MM-yyyy");
		dDate.setValue(new java.util.Date());
		dDate.setResolution(PopupDateField.RESOLUTION_DAY);


		optowner=new OptionGroup("", ownerList);
		optowner.setImmediate(true);
		optowner.setStyleName("horizontal");
		optowner.setValue("Astech");
		
		optMultiple=new OptionGroup("", MultipleList);
		optMultiple.setImmediate(true);
		optMultiple.setStyleName("horizontal");
		optMultiple.setValue("No");

		lblPartyName = new Label("Party: ");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("100.0%");
		lblPartyName.setHeight("18px");

		// cmbProductionType
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("200px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbPartyName.setNullSelectionAllowed(true);



		cmbFindProductionType = new ComboBox("Prodution Type: ");
		cmbFindProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbFindProductionType.setImmediate(true);
		cmbFindProductionType.setWidth("260px");
		cmbFindProductionType.setHeight("24px");
		cmbFindProductionType.setNullSelectionAllowed(true);

		lblSearchPanel = new Label("<font color='green' size='4px'><b>Search Panel</b></font>", Label.CONTENT_XHTML);
		lblSearchPanel.setWidth("-1px");
		lblSearchPanel.setHeight("-1px");
		lblSearchPanel.setImmediate(false);	

		searchPanel = new Panel();
		searchPanel.setImmediate(true);
		searchPanel.setWidth("490px");
		searchPanel.setHeight("330px");
		searchPanel.setStyleName("radius");

		tableFind.setSelectable(true);
		tableFind.setWidth("440px");
		tableFind.setHeight("150px");
		tableFind.setImmediate(true); 
		tableFind.setColumnCollapsingAllowed(true);	
		tableFind.setFooterVisible(true);

		tableFind.addContainerProperty("SL", Label.class, new Label());
		tableFind.setColumnWidth("SL", 15);

		tableFind.addContainerProperty("FG ID", Label.class, new Label());
		tableFind.setColumnWidth("FG ID", 40);
		tableFind.setColumnCollapsed("FG ID", true);

		tableFind.addContainerProperty("MASTER PRODUCT Name", Label.class, new Label());
		tableFind.setColumnWidth("MASTER PRODUCT Name", 260);

		tableFind.addContainerProperty("Unit", Label.class, new Label());
		tableFind.setColumnWidth("Unit", 40);

		tableFind.addContainerProperty("Std. Weight", Label.class, new Label());
		tableFind.setColumnWidth("Std. Weight", 50);

		frmLayout.addComponent(cmbFindProductionType);
		frmLayout.addComponent(cmbFindPartyName);
		frmLayout.addComponent(cmbFindFinishItemName);
		frmLayout.addComponent(tableFind);
		frmLayout.addComponent(btnFind);
		searchPanel.addComponent(frmLayout);
		searchPanel.addComponent(tableFind);



		tableFindInitialize();

		tableFg.setWidth("950px");
		tableFg.setHeight("190px");
		tableFg.setFooterVisible(true);
		tableFg.setColumnCollapsingAllowed(true);

		tableFg.addContainerProperty("SL", Label.class , new Label());
		tableFg.setColumnWidth("SL",20);

		tableFg.addContainerProperty("Semi FG Name", ComboBox.class , new ComboBox());
		tableFg.setColumnWidth("Semi FG Name",250);

		tableFg.addContainerProperty("Secondary Stage FG Name", ComboBox.class , new ComboBox());
		tableFg.setColumnWidth("Secondary Stage FG Name",250);

		tableFg.addContainerProperty("Weight(gm)", AmountField.class , new AmountField());
		tableFg.setColumnWidth("Weight(gm)",60);

		tableFg.addContainerProperty("Qty", AmountField.class , new AmountField());
		tableFg.setColumnWidth("Qty",40);

		tableFg.addContainerProperty("Unit Price" ,AmountField.class , new AmountField());
		tableFg.setColumnWidth("Unit Price",60);

		tableFg.addContainerProperty("Consumption Stage", ComboBox.class , new ComboBox());
		tableFg.setColumnWidth("Consumption Stage",150);

		tableFgInitialize();

		mainLayout.addComponent(searchPanel,"top:30.0px;left:30px;");
		mainLayout.addComponent(lblSearchPanel,"top:26.0px;left:190.0px;");

		mainLayout.addComponent(lblProductionType,"top:30.0px;left:540.0px;");
		mainLayout.addComponent(cmbProductionType, "top:27.0px;left:700.0px;");

		mainLayout.addComponent(lblPartyName,"top:60.0px;left:540.0px;");
		mainLayout.addComponent(cmbPartyName, "top:57.0px;left:700.0px;");

		mainLayout.addComponent(lblFgID,"top:90.0px;left:540.0px;");
		mainLayout.addComponent(txtFgID, "top:87.0px;left:700.0px;");

		mainLayout.addComponent(lblfgCode,"top:120.0px;left:540.0px;");
		mainLayout.addComponent(txtfgCode, "top:117.0px;left:700.0px;");

		mainLayout.addComponent(lblFinishItemName,"top:150.0px;left:540.0px;");
		mainLayout.addComponent(txtFinishItemName, "top:147.0px;left:700.0px;");

		mainLayout.addComponent(lblUnit, "top:180.0px;left:540.0px;");
		mainLayout.addComponent(cmbUnit, "top:177.0px;left:700.0px;");

		mainLayout.addComponent(lblFinishItemRate, "top:210.0px;left:540.0px;");
		mainLayout.addComponent(txtFinishItemRate, "top:207.0px;left:700.0px;");

		mainLayout.addComponent(lblWeight, "top:240.0px;left:540.0px;");
		mainLayout.addComponent(afWeight, "top:237.0px;left:700.0px;");

		mainLayout.addComponent(lblSemiFgName, "top:270.0px;left:540.0px;");
		mainLayout.addComponent(cmbSemiFgName, "top:267.0px;left:700.0px;");

		mainLayout.addComponent(lblSemiFgSubName, "top:300.0px;left:540.0px;");
		mainLayout.addComponent(cmbSemiFgSubName, "top:297.0px;left:700.0px;");

		mainLayout.addComponent(lblDate, "top:330.0px;left:805.0px;");
		mainLayout.addComponent(dDate, "top:327.0px;left:865.0px;");

		mainLayout.addComponent(lblSetQty, "top:330.0px;left:540.0px;");
		mainLayout.addComponent(txtSetQty, "top:327.0px;left:700.0px;");
		
		mainLayout.addComponent(new Label("Owner"),"top:360.0px;left:540.0px;");
		mainLayout.addComponent(optowner, "top:357.0px;left:700.0px;");
		optowner.setValue("Astech"); 

		mainLayout.addComponent(new Label("Multiple Secondary"),"top:390.0px;left:540.0px;");
		mainLayout.addComponent(optMultiple, "top:387.0px;left:700.0px;");
		optMultiple.setValue("No"); 

		
		mainLayout.addComponent(tableFg, "top:420.0px;left:30.0px;");

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:610.0px;left:0.0px;");

		mainLayout.addComponent(button, "top:625.0px;left:210.0px;");

		return mainLayout;
	}
	private void tableFgInitialize() {
		for(int a=0;a<7;a++){
			tableRowAddFg(a);
		}
	}

	private void tbSemiFgLoadData(int ar)
	{
		tbFgCmbSemiFg.get(ar).removeAllItems();
		String sql="select distinct semiFgCode,semiFgName,color from tbSemiFgInfo where semiFgCode in (select fGCode from tbFinishedGoodsStandardInfo) "
				+"union "
				+"select vRawItemCode,vRawItemName,''  from tbRawItemInfo ";

		//String sql="select distinct semiFgCode,semiFgName,color from tbSemiFgInfo";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			tbFgCmbSemiFg.get(ar).addItem(element[0].toString());
			String caption=element[1].toString()+" # "+element[2].toString();
			tbFgCmbSemiFg.get(ar).setItemCaption(element[0].toString(), caption);
		}
	}

	private void semiFgSubLoadData(int ar)
	{
		tbFgCmbSemiFgSub.get(ar).removeAllItems();
		//String sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation where semiFgId like '"+tbFgCmbSemiFg.get(ar).getValue()+"' or semiFgIdTwo like '"+tbFgCmbSemiFg.get(ar).getValue()+"' or semiFgIdThree like '"+tbFgCmbSemiFg.get(ar).getValue()+"'    ";

		String sql="select semiFgSubId,semiFgSubName from tbSemiFgSubInformation where semiFgId like '"+tbFgCmbSemiFg.get(ar).getValue()+"'";


		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object[] element = (Object[]) iter.next();
			tbFgCmbSemiFgSub.get(ar).addItem(element[0].toString());
			tbFgCmbSemiFgSub.get(ar).setItemCaption(element[0].toString(), element[1].toString());
		}
	}

	private boolean doubleEntryCheckSemiFg(String caption,int row)
	{
		for(int i=0;i<tbFgSl.size();i++)
		{

			if(tbFgCmbSemiFg.get(i).getValue()!=null){
				if(i!=row && caption.equals(tbFgCmbSemiFg.get(i).getItemCaption(tbFgCmbSemiFg.get(i).getValue())))
				{

					return true;
				}
			}
		}
		return false;
	}
	private boolean doubleEntryCheckSemiFgSub(String caption,int row)
	{
		for(int i=0;i<tbFgSl.size();i++)
		{

			if(tbFgCmbSemiFgSub.get(i).getValue()!=null)
			{
				if(i!=row && caption.equals(tbFgCmbSemiFgSub.get(i).getItemCaption(tbFgCmbSemiFgSub.get(i).getValue())))
				{

					return true;
				}
			}
		}
		return false;
	}
	private void semiFgWeightLoad(int ar){
		String sql="select stdWeight from tbSemiFgInfo where semiFgCode like '"+tbFgCmbSemiFg.get(ar).getValue()+"'";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			tbFgWeight.get(ar).setValue(df.format(iter.next()));
		}
	}
	private void tableRowAddFg(final int ar) {
		tbFgSl.add(ar,new Label());
		tbFgSl.get(ar).setValue(ar+1);
		tbFgSl.get(ar).setWidth("100%");
		tbFgSl.get(ar).setImmediate(true);

		tbFgCmbSemiFg.add(ar,new ComboBox());
		tbFgCmbSemiFg.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		tbFgCmbSemiFg.get(ar).setImmediate(true);
		tbFgCmbSemiFg.get(ar).setNullSelectionAllowed(true);
		tbFgCmbSemiFg.get(ar).setWidth("100%");
		tbSemiFgLoadData(ar); 
		tbFgCmbSemiFg.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(tbFgCmbSemiFg.get(ar).getValue()!=null){
					if(!doubleEntryCheckSemiFg(tbFgCmbSemiFg.get(ar).getItemCaption(tbFgCmbSemiFg.get(ar).getValue()), ar)){
						semiFgSubLoadData(ar);
						semiFgWeightLoad(ar);
					}
					else{
						tbFgCmbSemiFg.get(ar).setValue(null);
						showNotification("Double Entry",Notification.TYPE_WARNING_MESSAGE);
						tbFgCmbSemiFg.get(ar).focus();
					}
				}
				else{
					tbFgCmbSemiFgSub.get(ar).removeAllItems();
				}
			}
		});

		tbFgCmbSemiFgSub.add(ar,new ComboBox());
		tbFgCmbSemiFgSub.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		tbFgCmbSemiFgSub.get(ar).setImmediate(true);
		tbFgCmbSemiFgSub.get(ar).setNullSelectionAllowed(true);
		tbFgCmbSemiFgSub.get(ar).setWidth("100%");

		tbFgCmbSemiFgSub.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(doubleEntryCheckSemiFg(tbFgCmbSemiFg.get(ar).getItemCaption(tbFgCmbSemiFg.get(ar).getValue()), ar)){
					tbFgCmbSemiFgSub.get(ar).setValue(null);
					showNotification("Double Entry",Notification.TYPE_WARNING_MESSAGE);
					tbFgCmbSemiFgSub.get(ar).focus();
				}
			}
		});

		tbFgWeight.add(ar,new AmountField());
		tbFgWeight.get(ar).setWidth("100%");
		tbFgWeight.get(ar).setImmediate(true);

		tbFgWeight.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				ttlWeightCalc();
			}
		});

		tbFgQty.add(ar,new AmountField());
		tbFgQty.get(ar).setWidth("100%");
		tbFgQty.get(ar).setImmediate(true);

		tbFgUnitPrice.add(ar,new AmountField());
		tbFgUnitPrice.get(ar).setWidth("100%");
		tbFgUnitPrice.get(ar).setImmediate(true);

		tbFgUnitPrice.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				ttlRateCalc();
			}
		});

		tbConsumptionStage.add(ar,new ComboBox());
		tbConsumptionStage.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		tbConsumptionStage.get(ar).setImmediate(true);
		tbConsumptionStage.get(ar).setNullSelectionAllowed(true);
		tbConsumptionStage.get(ar).setWidth("100%");
		tbConsumptionStage.get(ar).addItem("Delivery Challan");
		tbConsumptionStage.get(ar).addItem("Assemble");

		tableFg.addItem(new Object[]{tbFgSl.get(ar),tbFgCmbSemiFg.get(ar),tbFgCmbSemiFgSub.get(ar),tbFgWeight.get(ar),tbFgQty.get(ar),tbFgUnitPrice.get(ar),tbConsumptionStage.get(ar)},ar);
	}
	private void ttlWeightCalc(){
		double totalWeight=0.0;
		for(int a=0;a<tbFgCmbSemiFg.size();a++){
			totalWeight=totalWeight+Double.parseDouble(tbFgWeight.get(a).getValue().toString().isEmpty()?"0.0":tbFgWeight.get(a).getValue().toString());
		}
		afWeight.setValue(df.format(totalWeight));
	}
	private void ttlRateCalc(){
		double totalRate=0.0;
		for(int a=0;a<tbFgCmbSemiFg.size();a++){
			totalRate=totalRate+Double.parseDouble(tbFgUnitPrice.get(a).getValue().toString().isEmpty()?"0.0":tbFgUnitPrice.get(a).getValue().toString());
		}
		txtFinishItemRate.setValue(df.format(totalRate));
	}
	private void tableFindInitialize() {
		for(int a=0;a<7;a++){
			tablerRowAddFind(a);
		}
	}
	private void tablerRowAddFind(int ar) 
	{
		tbFindSl.add(ar,new Label());
		tbFindSl.get(ar).setValue(ar+1);
		tbFindSl.get(ar).setWidth("100%");
		tbFindSl.get(ar).setImmediate(true);

		tbFindFgId.add(ar,new Label());
		tbFindFgId.get(ar).setWidth("100%");
		tbFindFgId.get(ar).setImmediate(true);

		tbFindFgName.add(ar,new Label());
		tbFindFgName.get(ar).setWidth("100%");
		tbFindFgName.get(ar).setImmediate(true);

		tbFindUnit.add(ar,new Label());
		tbFindUnit.get(ar).setWidth("100%");
		tbFindUnit.get(ar).setImmediate(true);

		tbFindStdWeight.add(ar,new Label());
		tbFindStdWeight.get(ar).setWidth("100%");
		tbFindStdWeight.get(ar).setImmediate(true);

		tableFind.addItem(new Object[]{tbFindSl.get(ar),tbFindFgId.get(ar),tbFindFgName.get(ar),tbFindUnit.get(ar),tbFindStdWeight.get(ar)},ar);
	}
}
