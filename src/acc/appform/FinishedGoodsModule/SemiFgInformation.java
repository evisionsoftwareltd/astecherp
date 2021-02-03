package acc.appform.FinishedGoodsModule;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;

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

public class SemiFgInformation extends Window {

	private TextField txtItemID = new TextField();
	private AbsoluteLayout mainLayout;
	SessionBean sessionBean;
	
	CommonButton button = new CommonButton("New", "Save", "Edit", "Delete", "Refresh", "Find", "", "","","Exit");

	Label lblSemiFgCode,lblSemiFgName,lblUnit,lblStdWeight,lblDate,lblProductionType,lblcolor,lblGm,lblpartyName;
	TextRead txtSemiFgCode;
	ComboBox cmbProductionType,cmbPartyName;
	//cmbPartyName;
	TextField txtSemiFgName,txtUnit,txtcolor;
	PopupDateField dDeclareDate;
	AmountField txtStdWeight;

	ArrayList<Component> allComp = new ArrayList<Component>();
	private boolean isUpdate=false;
	
	private OptionGroup optGroup;
	private List <String>optStatus=Arrays.asList(new String[]{"Active","Inactive"});
	
	private OptionGroup optowner;
	private List <String>ownerList=Arrays.asList(new String[]{"Astech","3rdParty"});
	
	private FileWriter log;

	public SemiFgInformation(SessionBean sessionBean)
	{
		this.sessionBean=sessionBean;
		this.setResizable(false);
		this.setCaption("SEMI FINISH GOODS INFORMATION :: "+sessionBean.getCompany());
		buildMainLayout();
		setContent(mainLayout);
		btnIni(true);
		componentIni(true);
		focusEnter();
		setEventAction();
		productionTypeLoad();
		partyNameLoad();
	}
	private void partyNameLoad() 
	{
		String sql="select partyCode,partyName from tbPartyInfo";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbPartyName.addItem(element[0]);
			cmbPartyName.setItemCaption(element[0], element[1].toString());
		}
	}
	private void productionTypeLoad(){
		String sql="select productTypeId,productTypeName from tbProductionType";
		Iterator iter=dbService(sql);
		while(iter.hasNext()){
			Object element[]=(Object[]) iter.next();
			cmbProductionType.addItem(element[0]);
			cmbProductionType.setItemCaption(element[0], element[1].toString());
		}
	}
	
	
	private boolean checkValidation(){
		if(cmbProductionType.getValue()!=null)
		{
				if(!txtSemiFgName.getValue().toString().isEmpty()){
					if(!txtUnit.getValue().toString().isEmpty()){
						if(!txtStdWeight.getValue().toString().isEmpty()){
							return true;
						}
						else{
							showNotification("Please Provide Standard Weight",Notification.TYPE_WARNING_MESSAGE);
						}
					}
					else{
						showNotification("Please Provide Unit",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Provide Semi Fg Name",Notification.TYPE_WARNING_MESSAGE);
				}
			
		}
		else{
			showNotification("Please Provide Production Type",Notification.TYPE_WARNING_MESSAGE);
		}
		return false;
	}
	private boolean deleteData(){
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			
			String udquery="insert into tbudSemiFgInfo(productionTypeId,productionTypeName,partyCode,partyName,semiFgCode,semiFgName,unit,stdWeight,date,userName,userIp,entryTime,color,status,vFlag,ownerstatus,vPartyCode) " 
			               +"select productionTypeId,productionTypeName,partyCode,partyName, "
			               +"semiFgCode,semiFgName,unit,stdWeight,date,userName,userIp,entryTime, "
			               +"color,status,'UPDATE'vFlag,ownerstatus,vPartyCode from tbSemiFgInfo "
			               +"where semiFgCode='"+txtSemiFgCode.getValue()+"' ";
			
			session.createSQLQuery(udquery).executeUpdate();
			session.createSQLQuery("delete from tbSemiFgInfo where semiFgCode like '"+txtSemiFgCode.getValue()+"'").executeUpdate();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("Delete Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				//showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);
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
	private String getSemiFgCode(){
		String query="select isnull(max(cast(SUBSTRING(semiFgCode,CHARINDEX('-',semiFgCode)+1," +
				"len(semiFgCode)-CHARINDEX('-',semiFgCode))as int)),0)+1 from tbSemiFgInfo";
		Iterator iter=dbService(query);
		if(iter.hasNext()){
			Object element=iter.next();
			return "SemiFg-"+element.toString();
		}
		return null;
	}
	private void insertData(){
		String semiFgId="";
		if(isUpdate){
			semiFgId=txtSemiFgCode.getValue().toString().trim();
		}
		else{
			semiFgId=getSemiFgCode();
		}
		
		String partycode="0";
		if(cmbPartyName.getValue()!=null)
		{
			partycode= cmbPartyName.getValue().toString();
		}
		else
		{
			partycode="0";	
		}
		
		
		String sql="insert into tbSemiFgInfo(productionTypeId,productionTypeName,partyCode,partyName,semiFgCode,semiFgName,unit,stdWeight,date,userName,userIp,entryTime,color,status,ownerstatus,vPartyCode) "+
				" values('"+cmbProductionType.getValue()+"','"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"',''," +
				"'','"+semiFgId+"','"+txtSemiFgName.getValue().toString().trim().replaceAll("'", "")+"'," +
				"'"+txtUnit.getValue().toString().trim().replaceAll("'", "")+"','"+txtStdWeight.getValue()+"','"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDeclareDate.getValue())+"'," +
				"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+txtcolor.getValue().toString().trim().replaceAll("'", "")+"','"+optGroup.getValue()+"','"+optowner.getValue().toString()+"',"
				+ " '"+partycode+"' )";
		System.out.println(sql);
		
		
		String udquery="insert into tbudSemiFgInfo(productionTypeId,productionTypeName,partyCode,partyName,semiFgCode,semiFgName,unit,stdWeight,date,userName,userIp,entryTime,color,status,vFlag,ownerstatus,vPartyCode) "+
				" values('"+cmbProductionType.getValue()+"','"+cmbProductionType.getItemCaption(cmbProductionType.getValue())+"',''," +
				"'','"+semiFgId+"','"+txtSemiFgName.getValue().toString().trim().replaceAll("'", "")+"'," +
				"'"+txtUnit.getValue().toString().trim().replaceAll("'", "")+"','"+txtStdWeight.getValue()+"','"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dDeclareDate.getValue())+"'," +
				"'"+sessionBean.getUserName()+"','"+sessionBean.getUserIp()+"',CURRENT_TIMESTAMP,'"+txtcolor.getValue().toString().trim().replaceAll("'", "")+"','"+optGroup.getValue()+"','NEW','"+optowner.getValue()+"','"+partycode+"')";
		System.out.println(udquery);
		
		
		
		Session session=null;
		Transaction tx=null;
		try{
			session=SessionFactoryUtil.getInstance().openSession();
			tx=session.beginTransaction();
			session.createSQLQuery(sql).executeUpdate();
			session.createSQLQuery(udquery).executeUpdate();
		}
		catch(Exception exp){
			tx.rollback();
			showNotification("Insert Data: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
		finally{
			if(session!=null||tx!=null){
				tx.commit();
				showNotification("All information saved successfully",Notification.TYPE_HUMANIZED_MESSAGE);
			}
		}
	}
	private void saveButtonEvent(){
		if(isUpdate)
		{
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Update Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
					    mb.buttonLayout.getComponent(0).setEnabled(false);
						if(deleteData()){
							insertData();
							//emailSend() ;
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
			final MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Save Item info?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
			mb.show(new EventListener()
			{
				public void buttonClicked(ButtonType buttonType)
				{
					if(buttonType == ButtonType.YES)
					{
						mb.buttonLayout.getComponent(0).setEnabled(false);
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
		button.btnDelete.addListener(new ClickListener() {

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
			File f = new File(sessionBean.emailPathsemifg);
			f.mkdirs();
			System.out.printf("3");
			System.out.printf("f"+f);
			String MasterId="";
			log = new FileWriter("D:/Tomcat 7.0/webapps/report/astecherp/Semifg/log.txt");
			System.out.printf("log"+log);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			
			String host = "smtp.gmail.com";
			String from = "";
			String pass = "";
			
			
			from="evisionsoftwareltd@gmail.com";
			pass="786@esl10";
			

			String EmailTo="support@eslctg.com";
			String EmailSubject="Semi Fg Information Edit";
			String EmailTxt="Semifg Name "+txtSemiFgName.getValue().toString()+" has been Edited \n"+
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
			MasterId=txtSemiFgCode.getValue().toString();
			
			System.out.printf("4");
			System.out.printf("\n4.1"+MasterId);
			reportGenerate(MasterId,sessionBean.emailPathsemifg+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			
			
			
			MimeMessage message = new MimeMessage(esession);
			
			
			message.setFrom(new InternetAddress(from));
			
			//message.addRecipient(Message.RecipientType.TO, new InternetAddress("astchfty@astechbd.com"));
			//message.addRecipient(Message.RecipientType.TO, new InternetAddress("costing@astechbd.com"));
			//message.addRecipient(Message.RecipientType.TO, new InternetAddress("ahmedtalba@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("support@eslctg.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("rabiulbgcctg2016@gmail.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("emdidar@gmail.com"));
			//message.addRecipient(Message.RecipientType.TO, new InternetAddress("rabiulbgcctg2016@gmail.com"));
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
			
			DataSource source = (DataSource) new FileDataSource(sessionBean.emailPathsemifg+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			System.out.println("Hello Banglaedsh3");
			messageBodyPart.setDataHandler( new DataHandler((javax.activation.DataSource) source));
			messageBodyPart.setFileName(MasterId+"_"+"_"+EmailSubject+".pdf");
			multipart.addBodyPart(messageBodyPart);
			System.out.printf("9");
			// Put parts in message
			message.setContent(multipart);
			System.out.printf("10");
			Transport transport = esession.getTransport("smtp");
			System.out.println(sessionBean.emailPathsemifg+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
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

		query=     "select userName,userIp,entryTime,vFlag,productionTypeId,productionTypeName,partyCode,partyName,semiFgCode,semiFgName,unit,stdWeight,isnull(color,'')color,status from tbudSemiFgInfo "
				  +"where productionTypeId like '%' and partyCode like '%' and semiFgCode like '"+txtSemiFgCode.getValue()+"' order by autoId desc  ";
				   
		
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
			hm.put("parentType", "Semi Fg Information");
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("sql", query);
			System.out.println("Done!!");
			FileOutputStream of = new FileOutputStream(fpath);
				
            try
            {
            	JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/production/rptUDSemiFgInfo.jasper"), of, hm,session.connection());
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

	
	
	private void deleteButtonEvent(){
		if(!txtSemiFgCode.getValue().toString().isEmpty()){
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
		String sql="select semiFgId from tbProductionSemiFgDetails where semiFgId='"+txtSemiFgCode.getValue()+"' "+
				" union select semiFgId from tbSemiFgSubInformation where semiFgId='"+txtSemiFgCode.getValue()+"' "+
				" union select semiFgId from tbMixtureIssueEntryDetails where semiFgId='"+txtSemiFgCode.getValue()+"' "+
				" union select FinishedProduct from tbMouldProductionDetails where FinishedProduct='"+txtSemiFgCode.getValue()+"'";
		Iterator<?> iter=dbService(sql);
		if(iter.hasNext()){
			return false;
			
		}
		return true;
	}
	private void findInitialise(String id){
		String sql="select productionTypeId,partyCode,semiFgCode,semiFgName,unit,stdWeight,date,isnull(color,'')color,isnull(status,'')status,ownerstatus,vPartyCode  from tbSemiFgInfo where semiFgCode like '"+id+"'";
		Iterator iter=dbService(sql);
		if(iter.hasNext()){
			Object element[]=(Object[])iter.next();
			cmbProductionType.setValue(element[0]);
			//cmbPartyName.setValue(element[1]);
			txtSemiFgCode.setValue(element[2]);
			txtSemiFgName.setValue(element[3]);
			txtUnit.setValue(element[4]);
			txtStdWeight.setValue(element[5]);
			dDeclareDate.setValue(element[6]);
			txtcolor.setValue(element[7].toString());
			optGroup.setValue(element[8]);
			optowner.setValue(element[9]);
			cmbPartyName.setValue(element[10] );
		}
	}
	private void findButtonEvent(){
		Window win = new SemiFgFindWindow(sessionBean, txtItemID);
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
		if(!txtSemiFgCode.getValue().toString().isEmpty())
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
		cmbProductionType.focus();
	}
	private void txtClear() {
		txtSemiFgCode.setValue("");
		cmbProductionType.setValue(null);
		//cmbPartyName.setValue(null);
		txtSemiFgName.setValue("");
		txtUnit.setValue("");
		txtcolor.setValue("");
		txtStdWeight.setValue("");
		optGroup.setValue("Active");
		optowner.setValue("Astech");
		cmbPartyName.setValue(null);
		dDeclareDate.setValue(new java.util.Date());
	}
	private void focusEnter() {
		allComp.add(cmbProductionType);
		//allComp.add(cmbPartyName);
		allComp.add(txtSemiFgName);
		allComp.add(txtUnit);
		allComp.add(txtcolor);
		allComp.add(txtStdWeight);
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
		lblSemiFgCode.setEnabled(!b);
		lblSemiFgName.setEnabled(!b);
		lblUnit.setEnabled(!b);
		lblcolor.setEnabled(!b);
		lblStdWeight.setEnabled(!b);
		lblDate.setEnabled(!b);
		lblProductionType.setEnabled(!b);
		lblGm.setEnabled(!b);
		optGroup.setEnabled(!b);

		txtSemiFgCode.setEnabled(!b);
		cmbProductionType.setEnabled(!b);
		//cmbPartyName.setEnabled(!b);
		txtSemiFgName.setEnabled(!b);
		txtUnit.setEnabled(!b);
		txtStdWeight.setEnabled(!b);
		dDeclareDate.setEnabled(!b);
		txtcolor.setEnabled(!b);
		optowner.setEnabled(!b);
		cmbPartyName.setEnabled(!b);
		
	}
	private AbsoluteLayout buildMainLayout(){
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("600px");
		setHeight("430px");

		// lblProudctionType
		lblProductionType = new Label("Production Type: ");
		lblProductionType.setImmediate(true);
		lblProductionType.setWidth("100.0%");
		lblProductionType.setHeight("18px");

		// cmbProductionType
		cmbProductionType = new ComboBox();
		cmbProductionType.setImmediate(true);
		cmbProductionType.setWidth("200px");
		cmbProductionType.setHeight("24px");
		cmbProductionType.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbProductionType.setNullSelectionAllowed(true);

		// lblPartyName
	/*	lblPartyName = new Label("Party Name: ");
		lblPartyName.setImmediate(true);
		lblPartyName.setWidth("100.0%");
		lblPartyName.setHeight("18px");*/

		// cmbPartyName
		//cmbPartyName = new ComboBox();
		//cmbPartyName.setImmediate(true);
		///cmbPartyName.setWidth("318px");
		//cmbPartyName.setHeight("24px");
		//cmbPartyName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		//cmbPartyName.setNullSelectionAllowed(true);

		// lblSemiFgCode
		lblSemiFgCode = new Label("Semi Fg Code: ");
		lblSemiFgCode.setImmediate(true);
		lblSemiFgCode.setWidth("100.0%");
		lblSemiFgCode.setHeight("18px");

		// txtSemiFgCode
		txtSemiFgCode = new TextRead();
		txtSemiFgCode.setImmediate(false);
		txtSemiFgCode.setWidth("100px");
		txtSemiFgCode.setHeight("23px");

		// lblSemiFgName
		lblSemiFgName = new Label("Semi Fg Name: ");
		lblSemiFgName.setImmediate(true);
		lblSemiFgName.setWidth("100.0%");
		lblSemiFgName.setHeight("18px");

		// txtSemiFgName
		txtSemiFgName = new TextField();
		txtSemiFgName.setImmediate(false);
		txtSemiFgName.setWidth("350px");
		txtSemiFgName.setHeight("-1px");
		txtSemiFgName.setSecret(false);

		// lblUnit
		lblUnit = new Label("Unit: ");
		lblUnit.setImmediate(true);
		lblUnit.setWidth("100.0%");
		lblUnit.setHeight("18px");

		// txtUnit
		txtUnit = new TextField();
		txtUnit.setImmediate(false);
		txtUnit.setWidth("100px");
		txtUnit.setHeight("-1px");
		txtUnit.setSecret(false);


		// lblcolor
		lblcolor = new Label("Color: ");
		lblcolor.setImmediate(true);
		lblcolor.setWidth("100.0%");
		lblcolor.setHeight("18px");

		// txtUnit
		txtcolor = new TextField();
		txtcolor.setImmediate(false);
		txtcolor.setWidth("100px");
		txtcolor.setHeight("-1px");
		txtcolor.setSecret(false);

		// lblStdWeight
		lblStdWeight = new Label("Std. Weight: ");
		lblStdWeight.setImmediate(true);
		lblStdWeight.setWidth("100.0%");
		lblStdWeight.setHeight("18px");
		
		lblGm = new Label("gm");
		lblGm.setImmediate(true);
		lblGm.setWidth("100.0%");
		lblGm.setHeight("18px");

		// txtStdWeight
		txtStdWeight =new AmountField();
		txtStdWeight.setImmediate(false);
		txtStdWeight.setWidth("100px");
		txtStdWeight.setHeight("-1px");
		txtStdWeight.setSecret(false);

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
		
		
		
		mainLayout.addComponent(lblProductionType,"top:30.0px;left:70.0px;");
		mainLayout.addComponent(cmbProductionType, "top:27.0px;left:170.0px;");

		//mainLayout.addComponent(lblPartyName,"top:60.0px;left:70.0px;");
		//mainLayout.addComponent(cmbPartyName, "top:57.0px;left:170.0px;");

		mainLayout.addComponent(lblSemiFgCode,"top:60.0px;left:70.0px;");
		mainLayout.addComponent(txtSemiFgCode, "top:57.0px;left:170.0px;");

		mainLayout.addComponent(lblSemiFgName,"top:90.0px;left:70.0px;");
		mainLayout.addComponent(txtSemiFgName, "top:87.0px;left:170.0px;");

		mainLayout.addComponent(lblUnit,"top:120.0px;left:70.0px;");
		mainLayout.addComponent(txtUnit, "top:117.0px;left:170.0px;");
		
		mainLayout.addComponent(lblcolor,"top:150.0px;left:70.0px;");
		mainLayout.addComponent(txtcolor, "top:147.0px;left:170.0px;");

		mainLayout.addComponent(lblStdWeight,"top:180.0px;left:70.0px;");
		mainLayout.addComponent(txtStdWeight, "top:177.0px;left:170.0px;");
		mainLayout.addComponent(lblGm, "top:179.0px;left:280.0px;");

		mainLayout.addComponent(lblDate,"top:210.0px;left:70.0px;");
		mainLayout.addComponent(dDeclareDate, "top:207.0px;left:170.0px;");
		
		mainLayout.addComponent(new Label("Status"),"top:240.0px;left:70.0px;");
		mainLayout.addComponent(optGroup, "top:237.0px;left:170.0px;");
		
		mainLayout.addComponent(new Label("Owner"),"top:270.0px;left:70.0px;");
		mainLayout.addComponent(optowner, "top:267.0px;left:170.0px;");
		optowner.setValue("Astech");
		
		mainLayout.addComponent(lblpartyName,"top:300.0px;left:70.0px;");
		mainLayout.addComponent(cmbPartyName, "top:297.0px;left:170.0px;");
		lblpartyName.setVisible(false);
		cmbPartyName.setVisible(false);

		Label lblLine = new Label("<b><font color='#e65100'>===================================================================================================================================================================================</font></b>", Label.CONTENT_XHTML);
		mainLayout.addComponent(lblLine, "top:330.0px;left:0.0px;");

		mainLayout.addComponent(button, "top:360.0px;left:5.0px;");

		return mainLayout;
	}
}
