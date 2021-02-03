package acc.appform.FinishedGoodsModule;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;






public class SemiFgSubInformation extends Window {

	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "","","Exit");

	Label lblSemiFgSubCode,lblSemiFgSubName,lblUnit,lblStdWeight,lblDate,lblProductionType,lblSemiFgName,lblSemiFgName2,lblSemiFgName3;
	TextRead txtSemiFgSubCode;
	ComboBox cmbProductionType,cmbSemiFgName,cmbSemiFgName2,cmbSemiFgName3,cmbProductionStep;
	TextField txtSemiFgSubName,txtUnit,txtStdWeight;
	PopupDateField dDeclareDate;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private boolean isUpdate=false;

	OptionGroup optGroup;
	List<String>optStatus=Arrays.asList(new String[]{"Active","Inactive"});
	private FileWriter log;

	private OptionGroup optowner;
	private List <String>ownerList=Arrays.asList(new String[]{"Astech","3rdParty"});

	private Label lblpartyName;
	private ComboBox cmbPartyName;


	public SemiFgSubInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("FINISH GOODS(Printing/Labeling) INFORMATION :: "+sessionBean.getCompany());

		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		focusEnter();
		setEventAction();
		SemiFgNameLoad();
		partyNameLoad() ;
	}
	
	private void partyNameLoad() 
	{
		String sql="select cast(partyCode as int) ,partyName from tbPartyInfo";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbPartyName.addItem(element[0]);
			cmbPartyName.setItemCaption(element[0], element[1].toString());
		}
	}
	
	
	private void SemiFgNameLoad() {
		cmbSemiFgName.removeAllItems();
		cmbSemiFgName2.removeAllItems();
		cmbSemiFgName3.removeAllItems();
		String sql="select distinct semiFgCode,semiFgName,color from tbSemiFgInfo where status='Active'";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbSemiFgName.addItem(element[0]);
			String caption=element[1].toString()+" # "+element[2].toString();
			cmbSemiFgName.setItemCaption(element[0], caption);

			cmbSemiFgName2.addItem(element[0]);
			//String caption=element[1].toString()+" # "+element[2].toString();
			cmbSemiFgName2.setItemCaption(element[0], caption);

			cmbSemiFgName3.addItem(element[0]);
			//String caption=element[1].toString()+" # "+element[2].toString();
			cmbSemiFgName3.setItemCaption(element[0], caption);
		}
	}
	private boolean checkValidation(){
		if(cmbProductionStep.getValue()!=null){
			if(!txtSemiFgSubName.getValue().toString().isEmpty()){
				if(cmbSemiFgName.getValue()!=null){
					return true;
				}
				else{
					showNotification("Please Provide Semi Fg Name",Notification.TYPE_WARNING_MESSAGE);
				}	
			}
			else{
				showNotification("Please Provide Semi Fg Sub Name",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Please Provide Production Step",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private boolean deleteData(){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			String udquery="insert into tbudSemiFgSubInformation (semiFgSubId,semiFgSubName,semiFgId,semiFgName,date,userName,userIp,entryTime, "
					+"status,semiFgIdTwo,semiFgNameTwo,SemiFgIdThree,semiFgNameThree,ProductionStepId,ProductionStepName,vFlag,ownerstatus,vPartyCode) "			
					+"select semiFgSubId,semiFgSubName,semiFgId,semiFgName,date,userName,userIp,entryTime,status,semiFgIdTwo,semiFgNameTwo,semiFgIdThree, "
					+"semiFgNameThree,productionStepId,productionStepName,'UPDATE' as flag,ownerstatus,vPartyCode from tbSemiFgSubInformation	where semiFgSubId='"+txtSemiFgSubCode.getValue()+"' ";			

			session.createSQLQuery(udquery).executeUpdate();
			session.createSQLQuery("delete from tbSemiFgSubInformation where semiFgSubId like '"+txtSemiFgSubCode.getValue()+"' ").executeUpdate();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("Delete Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				return true;
			}
		}
		return false;
	}
	private Iterator dbService(String sql){
		Transaction tx=null;
		Session session=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			return session.createSQLQuery(sql).list().iterator();
		}
		catch(Exception exp){
			showNotification(exp+"",Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(tx!=null||session!=null){
				session.close();
			}
		}
		return null;
	}
	private String getSemiFgSubCode(){
		String query="select isnull(max(cast(SUBSTRING(semiFgSubId,CHARINDEX('-',semiFgSubId)+1," +
				"len(semiFgSubId)-CHARINDEX('-',semiFgSubId))as int)),0)+1 from tbSemiFgSubInformation";
		Iterator iter=dbService(query);
		if(iter.hasNext()){
			Object element=iter.next();
			return "SemiFgSub-"+element.toString();
		}
		return null;
	}
	private void insertData(){
		String semiFgSubId="",semiFgIdTwo="",semiFgNameTwo="",semiFgIdThree="",
				semiFgNameThree="",productionStepId="",productionStepName="";
		if(isUpdate){
			semiFgSubId=txtSemiFgSubCode.getValue().toString().trim();
		}
		else{
			semiFgSubId=getSemiFgSubCode(); 
		}
		if(cmbSemiFgName2.getValue()!=null){
			semiFgIdTwo=cmbSemiFgName2.getValue().toString();
			semiFgNameTwo=cmbSemiFgName2.getItemCaption(cmbSemiFgName2.getValue());
		}
		if(cmbSemiFgName3.getValue()!=null){
			semiFgIdThree=cmbSemiFgName3.getValue().toString();
			semiFgNameThree=cmbSemiFgName3.getItemCaption(cmbSemiFgName3.getValue());
		}
		if(cmbProductionStep.getValue()!=null){
			productionStepId=cmbProductionStep.getValue().toString();
			productionStepName=cmbProductionStep.getItemCaption(cmbProductionStep.getValue());

		}
		
		String partyCode="0";
		
		if(cmbPartyName.getValue()!=null)
		{
			partyCode=cmbPartyName.getValue().toString();	
		}
		
		
		String sql="insert into tbSemiFgSubInformation (semiFgSubId,semiFgSubName,semiFgId,semiFgName,date,userName,userIp,entryTime," +
				"status,semiFgIdTwo,semiFgNameTwo,SemiFgIdThree,semiFgNameThree,ProductionStepId,ProductionStepName,ownerstatus,vPartyCode)values "+
				"('"+semiFgSubId+"','"+txtSemiFgSubName.getValue()+"','"+cmbSemiFgName.getValue()+"'," +
				"'"+cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDeclareDate.getValue())+"'," +
				"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
				"'"+optGroup.getValue()+"','"+semiFgIdTwo+"','"+semiFgNameTwo+"'," +
				"'"+semiFgIdThree+"','"+semiFgNameThree+"','"+productionStepId+"','"+productionStepName+"','"+optowner.getValue().toString()+"','"+partyCode+"')"; 
		System.out.println(sql);


		String udsql="insert into tbudSemiFgSubInformation (semiFgSubId,semiFgSubName,semiFgId,semiFgName,date,userName,userIp,entryTime," +
				"status,semiFgIdTwo,semiFgNameTwo,SemiFgIdThree,semiFgNameThree,ProductionStepId,ProductionStepName,vFlag,ownerstatus,vPartyCode)values "+
				"('"+semiFgSubId+"','"+txtSemiFgSubName.getValue()+"','"+cmbSemiFgName.getValue()+"'," +
				"'"+cmbSemiFgName.getItemCaption(cmbSemiFgName.getValue())+"','"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDeclareDate.getValue())+"'," +
				"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP," +
				"'"+optGroup.getValue()+"','"+semiFgIdTwo+"','"+semiFgNameTwo+"'," +
				"'"+semiFgIdThree+"','"+semiFgNameThree+"','"+productionStepId+"','"+productionStepName+"','NEW','"+optowner.getValue()+"','"+partyCode+"')"; 
		System.out.println(udsql);




		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			session.createSQLQuery(sql).executeUpdate();
			session.createSQLQuery(udsql).executeUpdate();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("Insert Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
		}
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
						if(deleteData()){
							insertData();
							//emailSend();
							isUpdate = false;
							btnIni(true);
							componentIni(true);
							txtClear();
							button.btnNew.focus();
						}
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
	private void setEventAction() {
		button.btnNew.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				newButtonEvent();
			}
		});
		button.btnSave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(checkValidation()){
					saveButtonEvent();
				}
			}
		});
		button.btnEdit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable()){
					isUpdate = true;
					updateButtonEvent();
				}
				else{
					getParent().showNotification("You are not Permitted to Edit",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		button.btnRefresh.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				refreshButtonEvent();
			}
		});
		button.btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				findButtonEvent();
			}
		});
		button.btnExit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		button.btnDelete.addListener(new ClickListener() 
		{

			public void buttonClick(ClickEvent event) {
				deleteButtonEvent();
			}
		});
		
		
         optowner.addListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event)
			{
				cmbPartyName.setValue(null);
				if(optowner.getValue().toString().equalsIgnoreCase("3rdParty"))
				{
					lblpartyName.setVisible(true);
					cmbPartyName.setVisible(true);
				}
				else
				{
					lblpartyName.setVisible(false);
					cmbPartyName.setVisible(false);	
				}
				
			}
		});
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
			File f = new File(sessionBean.emailPathfglabeling);
			f.mkdirs();
			System.out.printf("3");
			System.out.printf("f"+f);
			String MasterId="";
			log = new FileWriter("D:/Tomcat 7.0/webapps/report/astecherp/Fglabelingprinting/log.txt");
			System.out.printf("log"+log);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();


			String host = "smtp.gmail.com";
			String from = "";
			String pass = "";


			from="evisionsoftwareltd@gmail.com";
			pass="786@esl10";


			String EmailTo="support@eslctg.com";
			String EmailSubject="Finished Goods Information Edit";
			String EmailTxt="FG Name "+txtSemiFgSubName.getValue().toString()+" has been Edited \n"+
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
			MasterId=txtSemiFgSubCode.getValue().toString();

			System.out.printf("4");
			System.out.printf("\n4.1"+MasterId);
			reportGenerate(MasterId,sessionBean.emailPathfglabeling+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");


			//5656
			MimeMessage message = new MimeMessage(esession);


			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("astechfty@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("costing@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("ahmedtalba@astechbd.com"));
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

			DataSource source = (DataSource) new FileDataSource(sessionBean.emailPathfglabeling+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			System.out.println("Hello Banglaedsh3");
			messageBodyPart.setDataHandler( new DataHandler((javax.activation.DataSource) source));
			messageBodyPart.setFileName(MasterId+"_"+"_"+EmailSubject+".pdf");
			multipart.addBodyPart(messageBodyPart);
			System.out.printf("9");
			// Put parts in message
			message.setContent(multipart);
			System.out.printf("10");
			Transport transport = esession.getTransport("smtp");
			System.out.println(sessionBean.emailPathfglabeling+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
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
		catch(Exception exp){
			showNotification("mail send :"+exp,Notification.TYPE_ERROR_MESSAGE);
		}

	}

	private void reportGenerate(String iclientId, String fpath) throws HibernateException, JRException, IOException 
	{	
		Session session = SessionFactoryUtil.getInstance().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String query = "";
		ReportDate reportTime = new ReportDate();

		query=     "select userName,userIp,entryTime,vFlag,semiFgSubId,semiFgSubName,semiFgId,semiFgName , "
				+"(select stdWeight from tbSemiFgInfo where semiFgCode=semiFgId)stdWeight, "
				+"isnull(semiFgIdTwo,'')semiFgIdTwo,isnull(semiFgNameTwo,'')semiFgNameTwo , "
				+"isnull((select stdWeight from tbSemiFgInfo where semiFgCode=semiFgIdTwo),'')stdWeightTwo, "
				+"isnull(semiFgIdThree,'')semiFgIdThree,isnull(semiFgNameThree,'')semiFgNameThree, "
				+"isnull((select stdWeight from tbSemiFgInfo where semiFgCode=semiFgIdThree),'')stdWeightThree, "
				+"status   from tbudSemiFgSubInformation where semiFgId like '%' "
				+"and semiFgSubId like '"+txtSemiFgSubCode.getValue().toString()+"'  and status like '%'  order by autoId desc ";


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
			hm.put("parentType", "FINISHED GOODS(PRINTING/LABELING/CAP/SBM) INFORMATION");
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("sql", query);
			System.out.println("Done!!");
			FileOutputStream of = new FileOutputStream(fpath);

			try
			{
				JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/production/RptUDFGInfoLabellingOrPrinting.jasper"), of, hm,session.connection());
				System.out.println("Done!! again");	
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



	private void deleteButtonEvent(){
		if(!txtSemiFgSubCode.getValue().toString().isEmpty()){
			if(checkReference()){

				MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Delete ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							if(deleteData()){
								btnIni(true);
								componentIni(true);
								txtClear();
								button.btnNew.focus();
								showNotification("Delete Data Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
							}
						}
					}
				});	
			}
			else{
				showNotification("Referenced Data Can't be delete",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("There is no Data",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private boolean checkReference(){
		String sql="select semiFgSubId from tbFinishedProductDetailsNew where semiFgSubId='"+txtSemiFgSubCode.getValue()+"' "+
				" union select fgCode from tbLabelingPrintingDailyProductionDetails where fgCode='"+txtSemiFgSubCode.getValue()+"'";
		Iterator<?> iter=dbService(sql);
		if(iter.hasNext()){
			return false;

		}
		return true;
	}
	private void findInitialise(String id){
		String sql="select semiFgId,semiFgSubId,semiFgSubName,date,status,semiFgIdTwo," +
				"semiFgIdThree,ProductionStepId,ProductionStepName,ownerstatus,vPartyCode from tbSemiFgSubInformation where semiFgSubId like '"+id+"'";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbSemiFgName.setValue(element[0]);
			txtSemiFgSubCode.setValue(element[1]);
			txtSemiFgSubName.setValue(element[2]);
			dDeclareDate.setValue(element[3]);
			optGroup.setValue(element[4]);
			cmbSemiFgName2.setValue(element[5]);
			cmbSemiFgName3.setValue(element[6]);
			cmbProductionStep.setValue(element[7]);
			optowner.setValue(element[9]);
			partyNameLoad() ;
			cmbPartyName.setValue(element[10]);
		}
	}
	private void findButtonEvent(){
		Window win = new SemiFgSubFindWindow(sessionBean, txtItemID);
		win.addListener(new Window.CloseListener() 
		{
			public void windowClose(CloseEvent e) 
			{
				if (txtItemID.getValue().toString().length() > 0)
				{
					txtClear();
					findInitialise(txtItemID.getValue().toString());
				}
			}
		});

		this.getParent().addWindow(win);
	}
	private void updateButtonEvent() {
		if(!txtSemiFgSubCode.getValue().toString().isEmpty())
		{
			btnIni(false);
			componentIni(false);//Enable(true);
		}
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void refreshButtonEvent() 
	{
		componentIni(true);
		btnIni(true);
		txtClear();
	}

	private void newButtonEvent() 
	{
		isUpdate=false;
		componentIni(false);
		btnIni(false);
		txtClear();
		cmbSemiFgName.focus();
	}
	private void txtClear() {
		cmbProductionStep.setValue(null);
		txtSemiFgSubCode.setValue("");
		cmbSemiFgName.setValue(null);
		cmbSemiFgName2.setValue(null);
		cmbSemiFgName3.setValue(null);
		txtSemiFgSubName.setValue("");
		dDeclareDate.setValue(new java.util.Date());
		optGroup.setValue("Active");
		optowner.setValue("Astech");
		cmbPartyName.setValue(null);
	}
	private void focusEnter() {

		allComp.add(cmbSemiFgName);
		allComp.add(txtSemiFgSubName);
		allComp.add(dDeclareDate);

		allComp.add(button.btnNew);
		allComp.add(button.btnSave);
		allComp.add(button.btnEdit);
		allComp.add(button.btnRefresh);
		allComp.add(button.btnFind);
		allComp.add(button.btnExit);

		new FocusMoveByEnter(this, allComp);
	}
	private void btnIni(boolean t)
	{
		button.btnNew.setEnabled(t);
		button.btnEdit.setEnabled(t);
		button.btnSave.setEnabled(!t);
		button.btnSave.setEnabled(!t);
		button.btnRefresh.setEnabled(!t);
		button.btnFind.setEnabled(t);		
	}

	private void componentIni(boolean b) 
	{
		lblSemiFgSubCode.setEnabled(!b);
		lblSemiFgSubName.setEnabled(!b);
		lblDate.setEnabled(!b);
		lblSemiFgName.setEnabled(!b);
		lblSemiFgName2.setEnabled(!b);
		lblSemiFgName3.setEnabled(!b);


		txtSemiFgSubCode.setEnabled(!b);
		cmbSemiFgName.setEnabled(!b);
		txtSemiFgSubName.setEnabled(!b);
		dDeclareDate.setEnabled(!b);
		optGroup.setEnabled(!b);
		cmbSemiFgName2.setEnabled(!b);
		cmbSemiFgName3.setEnabled(!b);
		cmbProductionStep.setEnabled(!b);
		
		optowner.setEnabled(!b);
		lblpartyName.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		
	}
	private AbsoluteLayout buildMainLayout(){
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("600px");
		setHeight("420px");

		// lblSemiFgName
		lblSemiFgName = new Label("Semi Fg Name: ");
		lblSemiFgName.setImmediate(true);
		lblSemiFgName.setWidth("100.0%");
		lblSemiFgName.setHeight("18px");

		// cmbSemiFgName
		cmbSemiFgName = new ComboBox();
		cmbSemiFgName.setImmediate(true);
		cmbSemiFgName.setWidth("318px");
		cmbSemiFgName.setHeight("24px");
		cmbSemiFgName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSemiFgName.setNullSelectionAllowed(true);

		// cmbSemiFgName
		cmbProductionStep = new ComboBox();
		cmbProductionStep.setImmediate(true);
		cmbProductionStep.setWidth("318px");
		cmbProductionStep.setHeight("24px");
		cmbProductionStep.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbProductionStep.setNullSelectionAllowed(true);

		cmbProductionStep.addItem("Assemble");
		cmbProductionStep.addItem("Dry Offset Printing");
		cmbProductionStep.addItem("Screen Printing");
		cmbProductionStep.addItem("Heat Trasfer Label");
		cmbProductionStep.addItem("Manual Printing");
		cmbProductionStep.addItem("Labeling");
		cmbProductionStep.addItem("Cap Folding");
		cmbProductionStep.addItem("Stretch Blow Molding");
		cmbProductionStep.addItem("Shrink");

		// lblSemiFgName
		lblSemiFgName2 = new Label("Semi Fg Name: ");
		lblSemiFgName2.setImmediate(true);
		lblSemiFgName2.setWidth("100.0%");
		lblSemiFgName2.setHeight("18px");

		// cmbSemiFgName
		cmbSemiFgName2 = new ComboBox();
		cmbSemiFgName2.setImmediate(true);
		cmbSemiFgName2.setWidth("318px");
		cmbSemiFgName2.setHeight("24px");
		cmbSemiFgName2.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSemiFgName2.setNullSelectionAllowed(true);

		// lblSemiFgName
		lblSemiFgName3 = new Label("Semi Fg Name: ");
		lblSemiFgName3.setImmediate(true);
		lblSemiFgName3.setWidth("100.0%");
		lblSemiFgName3.setHeight("18px");

		// cmbSemiFgName
		cmbSemiFgName3 = new ComboBox();
		cmbSemiFgName3.setImmediate(true);
		cmbSemiFgName3.setWidth("318px");
		cmbSemiFgName3.setHeight("24px");
		cmbSemiFgName3.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbSemiFgName3.setNullSelectionAllowed(true);

		// lblSemiFgSubCode
		lblSemiFgSubCode = new Label("Fg Code(Printing/Labeling): ");
		lblSemiFgSubCode.setImmediate(true);
		lblSemiFgSubCode.setWidth("100.0%");
		lblSemiFgSubCode.setHeight("18px");

		// txtSemiFgSubCode
		txtSemiFgSubCode = new TextRead();
		txtSemiFgSubCode.setImmediate(false);
		txtSemiFgSubCode.setWidth("100px");
		txtSemiFgSubCode.setHeight("23px");

		// lblSemiFgName
		lblSemiFgSubName = new Label("Fg Name(Printing/Labeling):");
		lblSemiFgSubName.setImmediate(true);
		lblSemiFgSubName.setWidth("100.0%");
		lblSemiFgSubName.setHeight("18px");

		// txtSemiFgName
		txtSemiFgSubName = new TextField();
		txtSemiFgSubName.setImmediate(false);
		txtSemiFgSubName.setWidth("318px");
		txtSemiFgSubName.setHeight("-1px");
		txtSemiFgSubName.setSecret(false);

		// lblDate
		lblDate = new Label("Date: ");
		lblDate.setImmediate(true);
		lblDate.setWidth("100.0%");
		lblDate.setHeight("18px");

		//Declare Date
		dDeclareDate = new PopupDateField();
		dDeclareDate.setImmediate(true);
		dDeclareDate.setWidth("110px");
		dDeclareDate.setDateFormat("dd-MM-yyyy");
		dDeclareDate.setValue(new java.util.Date());
		dDeclareDate.setResolution(PopupDateField.RESOLUTION_DAY);

		optGroup=new OptionGroup("", optStatus);
		optGroup.setImmediate(true);
		optGroup.setStyleName("horizontal");
		optGroup.setValue("Active");


		optowner=new OptionGroup("", ownerList);
		optowner.setImmediate(true);
		optowner.setStyleName("horizontal");
		optowner.setValue("Astech");

		lblpartyName = new Label("Party: ");
		lblpartyName.setImmediate(true);
		lblpartyName.setWidth("100.0%");
		lblpartyName.setHeight("18px");

		// cmbProductionType
		cmbPartyName = new ComboBox();
		cmbPartyName.setImmediate(true);
		cmbPartyName.setWidth("200px");
		cmbPartyName.setHeight("24px");
		cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbPartyName.setNullSelectionAllowed(true);
        
		lblpartyName.setVisible(false);
		cmbPartyName.setVisible(false);


		mainLayout.addComponent(new Label("Production Step: "),"top:10.0px;left:30.0px;");
		mainLayout.addComponent(cmbProductionStep, "top:7.0px;left:190.0px;");

		mainLayout.addComponent(lblSemiFgSubCode,"top:40.0px;left:30.0px;");
		mainLayout.addComponent(txtSemiFgSubCode, "top:37.0px;left:190.0px;");

		mainLayout.addComponent(lblSemiFgSubName,"top:70.0px;left:30.0px;");
		mainLayout.addComponent(txtSemiFgSubName, "top:67.0px;left:190.0px;");


		mainLayout.addComponent(lblSemiFgName,"top:100.0px;left:30.0px;");
		mainLayout.addComponent(cmbSemiFgName, "top:97.0px;left:190.0px;");

		mainLayout.addComponent(lblSemiFgName2,"top:130.0px;left:30.0px;");
		mainLayout.addComponent(cmbSemiFgName2, "top:127.0px;left:190.0px;");

		mainLayout.addComponent(lblSemiFgName3,"top:160.0px;left:30.0px;");
		mainLayout.addComponent(cmbSemiFgName3, "top:157.0px;left:190.0px;");

		mainLayout.addComponent(lblDate,"top:190.0px;left:30.0px;");
		mainLayout.addComponent(dDeclareDate, "top:187.0px;left:190.0px;");

		mainLayout.addComponent(new Label("Status: "),"top:210.0px;left:30.0px;");
		mainLayout.addComponent(optGroup, "top:210.0px;left:190.0px;");
		
		mainLayout.addComponent(new Label("Owner: "),"top:240.0px;left:30.0px;");
		mainLayout.addComponent(optowner, "top:240.0px;left:190.0px;");
		
		mainLayout.addComponent(lblpartyName,"top:270.0px;left:30.0px;");
		mainLayout.addComponent(cmbPartyName, "top:270.0px;left:190.0px;");

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:300.0px;left:0.0px;");

		mainLayout.addComponent(button, "top:330.0px;left:20.0px;");

		return mainLayout;
	}
}
