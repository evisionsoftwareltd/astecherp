package com.example.productionSetup;

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


import java.awt.TextField;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;
import com.common.share.*;
import com.common.share.MessageBox.ButtonType;
import com.common.share.MessageBox.EventListener;
public class FinishedStandard extends Window
{

	SessionBean sessionBean;
	AbsoluteLayout mainLayout;

	boolean isUpdate = false;
	boolean isFind = false;

	Label lblPartyName,lblFGName,lblDia,lblWeight,lblPerSqm,lblPerSqm1,lblPerPcs,lblPerPcs1,
	lblCycleTime,lblCavity,lblEstimatedQty,lblActual,lblDeclarationDate,lblPercent,lblFgUnit,lbl,
	lblWidthActual,lblWidthConsumption,lblHeightActual,lblHeightConsumption;

	ComboBox cmbParty,cmbFGName;
	TextRead txtDia,txtWeight,txtPerSqm,txtPerSqm1,txtPerPcs,txtPerPcs1,txtCycleTime,
	txtCavity,txtEstimated,txtActual,txtFgUnit,txtStdNo,txtWidthActual,txtWidthConsumption,txtLengthActual,txtLengthConsumption;
	AmountField txtPercent;
	PopupDateField dDeclaration;
	Panel panelSearch;
	private CommonButton cButton = new CommonButton("New", "Save", "Edit","", "Refresh", "Find", "", "", "", "Exit");

	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateformat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private DecimalFormat df = new DecimalFormat("#0.000000");
	private DecimalFormat df2 = new DecimalFormat("#0.00");

	private ArrayList<Label>lblSl=new ArrayList<Label>();
	private ArrayList<ComboBox>cmbRm=new ArrayList<ComboBox>();
	private ArrayList<TextRead>txtUnit=new ArrayList<TextRead>();
	private ArrayList<AmountField>amtPercent=new ArrayList<AmountField>();
	private ArrayList<AmountField>amtQty=new ArrayList<AmountField>();
	private ArrayList<AmountField>txtRate=new ArrayList<AmountField>();
	private ArrayList<AmountCommaSeperator>amtAmount=new ArrayList<AmountCommaSeperator>();
	private Table tableRm=new Table();

	private ArrayList<Label>lblSl1=new ArrayList<Label>();
	private ArrayList<ComboBox>cmbPack=new ArrayList<ComboBox>();
	private ArrayList<TextRead>txtUnit1=new ArrayList<TextRead>();
	private ArrayList<AmountField>amtQty1=new ArrayList<AmountField>();
	private ArrayList<AmountField>txtRate1=new ArrayList<AmountField>();
	private ArrayList<AmountCommaSeperator>amtAmount1=new ArrayList<AmountCommaSeperator>();
	private Table tablePack=new Table();

	private ArrayList<Label>lblSl2=new ArrayList<Label>();
	private ArrayList<ComboBox>cmbInk=new ArrayList<ComboBox>();
	private ArrayList<TextRead>txtUnit2=new ArrayList<TextRead>();
	private ArrayList<AmountField>amtQty2=new ArrayList<AmountField>();
	private ArrayList<AmountField>txtRate2=new ArrayList<AmountField>();
	private ArrayList<AmountField>amtkgpcs=new ArrayList<AmountField>();
	private ArrayList<AmountCommaSeperator>amtAmount2=new ArrayList<AmountCommaSeperator>();
	private Table tableInk=new Table();

	Label lblRaw=new Label();
	Label lblInk=new Label();
	Label lblPack=new Label();

	HashMap hmUnit=new HashMap();
	HashMap hmRate=new HashMap();
	int x=0;

	ComboBox cmbFindMouldName=new ComboBox("Mould Name: ");
	ComboBox cmbFindFG=new ComboBox("Finished Goods: ");
	ComboBox cmbFindDate=new ComboBox("Declaration Date: ");
	private FileWriter log;

	Label lblMouldName;
	ComboBox cmbMouldName;

	private OptionGroup RadioBtnGroup;
	private static final List<String> option = Arrays.asList(new String[]{"YES","NO"});

	OptionGroup optGroup;
	private static final List<String>optStatus  = Arrays.asList(new String[] {"Active" ,"Inactive" });
	int sl=0;


	public FinishedStandard(SessionBean sessionBean){

		this.sessionBean=sessionBean;
		this.setCaption("PRODUCT FORMULATION :: "+sessionBean.getCompany());
		this.setResizable(false);
		buildMainLayout();
		setContent(mainLayout);
		cmbFinishedGoodsData();
		cmbMouldData();
		setEventAction();
		unitRateLoad();
		btnIni(true);
		focusMove();
		componentIni(true);
	}
	private void componentIni(boolean b) 
	{
		optGroup.setEnabled(!b);
		RadioBtnGroup.setEnabled(!b);
		lblPartyName.setEnabled(!b);
		cmbParty.setEnabled(!b);

		lblMouldName.setEnabled(!b);
		cmbMouldName.setEnabled(!b);

		lblFGName.setEnabled(!b);
		cmbFGName.setEnabled(!b);

		lblDia.setEnabled(!b);
		txtDia.setEnabled(!b);

		lblFgUnit.setEnabled(!b);
		txtFgUnit.setEnabled(!b);


		lblWeight.setEnabled(!b);
		txtWeight.setEnabled(!b);

		lblPercent.setEnabled(!b);
		txtStdNo.setEnabled(!b);


		lblWidthActual.setEnabled(!b);
		lblWidthConsumption.setEnabled(!b);

		txtWidthActual.setEnabled(!b);
		txtWidthConsumption.setEnabled(!b);

		lblHeightActual.setEnabled(!b);
		lblHeightConsumption.setEnabled(!b);

		txtLengthActual.setEnabled(!b);
		txtLengthConsumption.setEnabled(!b);

		lblPerSqm.setEnabled(!b);
		lblPerSqm1.setEnabled(!b);

		txtPerSqm.setEnabled(!b);
		txtPerSqm1.setEnabled(!b);

		lblPerPcs.setEnabled(!b);
		lblPerPcs1.setEnabled(!b);

		txtPerPcs.setEnabled(!b);
		txtPerPcs1.setEnabled(!b);

		lblCycleTime.setEnabled(!b);
		txtCycleTime.setEnabled(!b);

		lblCavity.setEnabled(!b);
		txtCavity.setEnabled(!b);

		lblEstimatedQty.setEnabled(!b);
		txtEstimated.setEnabled(!b);

		lblActual.setEnabled(!b);
		txtActual.setEnabled(!b);

		lblDeclarationDate.setEnabled(!b);
		dDeclaration.setEnabled(!b);

		tableRm.setEnabled(!b);
		tablePack.setEnabled(!b);
		tableInk.setEnabled(!b);
		panelSearch.setEnabled(!b);


	}
	private void focusMove() {
		ArrayList<Component> allComp = new ArrayList<Component>();
		allComp.add(cmbParty);
		allComp.add(cmbFGName);
		allComp.add(txtPercent);
		for(int a=0;a<lblSl.size();a++){
			allComp.add(cmbRm.get(a));
			allComp.add(amtPercent.get(a));
		}
		for(int a=0;a<lblSl1.size();a++){
			allComp.add(cmbPack.get(a));
			allComp.add(amtQty1.get(a));
		}
		for(int a=0;a<lblSl2.size();a++){
			allComp.add(cmbInk.get(a));
			allComp.add(amtQty2.get(a));
		}
		new FocusMoveByEnter(this,allComp);
	}
	private void tableClearRm(){

		for(int a=0;a<lblSl.size();a++){
			cmbRm.get(a).setValue(null);
			txtUnit.get(a).setValue("");
			amtPercent.get(a).setValue("");
			amtQty.get(a).setValue("");
			txtRate.get(a).setValue("");
			amtAmount.get(a).setValue("");
			tableRm.setColumnFooter("Amount", "Total: "+0.0);
		}
		tableRm.setColumnFooter("Amount", "Total: "+0.0);
	}
	private void tableClearPack(){

		for(int a=0;a<lblSl1.size();a++){
			cmbPack.get(a).setValue(null);
			txtUnit1.get(a).setValue("");
			amtQty1.get(a).setValue("");
			txtRate1.get(a).setValue("");
			amtAmount1.get(a).setValue("");
		}
		tablePack.setColumnFooter("Amount", "Total: "+0.0);
	}
	private void tableClearInk(){

		for(int a=0;a<lblSl2.size();a++){
			cmbInk.get(a).setValue(null);
			txtUnit2.get(a).setValue("");
			amtkgpcs.get(a).setValue("");
			amtQty2.get(a).setValue("");
			txtRate2.get(a).setValue("");
			amtAmount2.get(a).setValue("");

		}
		tableInk.setColumnFooter("Amount", "Total: "+0.0);
	}
	private void unitRateLoad() 
	{
		hmRate.clear();
		hmUnit.clear();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select productId,unit,closingRate from dbo.[funRawMaterialsStock]('"+dateformat.format(dDeclaration.getValue())+" 23:59:59','%') "
				+"union all "
				+"select vCode,vUnitName,0 rate from tbThirdPartyItemInfo "; 

			//String sql="select productId,unit,closingRate from dbo.[funRawMaterialsStock]('"+dateformat.format(dDeclaration.getValue())+" 23:59:59','%')";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();)
			{

				Object[] element=(Object[]) iter.next();
				hmUnit.put(element[0], element[1]);
				hmRate.put(element[0], element[2]);

			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
	}

	private void cmbFinishedGoodsData() 
	{
		cmbFGName.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();

			/*String sql = "select distinct semiFgCode,semiFgName,color from tbSemiFgInfo where semiFgCode " 
					     +"not in (select fGCode from tbFinishedGoodsStandardInfo) order by semiFgCode ";*/
			String sql="select distinct semiFgCode,semiFgName,color from tbSemiFgInfo order by semiFgCode ";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFGName.addItem(element[0]);
				String caption=element[1].toString()+" # "+element[2].toString();
				cmbFGName.setItemCaption(element[0], caption);
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
	}

	private void newButtonEvent() 
	{
		btnIni(false);
		componentIni(false);
		panelSearch.setEnabled(false);
		autoJobNo();
		isUpdate=false;
		isFind=false;
	}


	private void autoJobNo() 
	{
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select 0,isnull(max(CAST(jobNo as int)),0)+1 as jobNo from tbFinishedGoodsStandardInfo";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				txtStdNo.setValue(element[1]);
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
	}



	private void refreshEvent(){
		isFind=false;
		isUpdate=false;
		btnIni(true);
		txtClear();
		componentIni(true);
		searchtableclear();
	}
	private void txtClear() {
		RadioBtnGroup.setValue("NO");
		optGroup.setValue("Active");
		cmbParty.setValue(null);
		cmbFGName.setValue(null);
		cmbMouldName.setValue(null);
		dDeclaration.setValue(new java.util.Date());
		txtFgUnit.setValue("");
		//ltxtPercent.setValue("");
		txtDia.setValue("");
		txtWeight.setValue("");
		txtCycleTime.setValue("");
		txtPerPcs.setValue("");
		txtPerPcs1.setValue("");
		txtPerSqm.setValue("");
		txtPerSqm1.setValue("");
		txtCavity.setValue("");
		txtEstimated.setValue("");
		txtActual.setValue("");
		txtWidthActual.setValue("");
		txtWidthConsumption.setValue("");
		txtLengthActual.setValue("");
		txtLengthConsumption.setValue("");
		tableClearRm();
		tableClearPack();
		tableClearInk();

	}

	private void findButtonEvent() {
		componentIni(true);
		panelSearch.setEnabled(true);
		isFind=true;
	}

	private void searchtableclear()
	{
		cmbFindFG.setValue(null);
		cmbFindMouldName.setValue(null);
		cmbFindDate.setValue(null);
	}


	private void dataclear()
	{

		txtFgUnit.setValue("");
		//ltxtPercent.setValue("");
		txtDia.setValue("");
		txtWeight.setValue("");
		txtCycleTime.setValue("");
		txtPerPcs.setValue("");
		txtPerPcs1.setValue("");
		txtPerSqm.setValue("");
		txtPerSqm1.setValue("");
		txtCavity.setValue("");
		txtEstimated.setValue("");
		txtActual.setValue("");
		txtWidthActual.setValue("");
		txtWidthConsumption.setValue("");
		txtLengthActual.setValue("");
		txtLengthConsumption.setValue("");	
	}

	private void cmbFinishedDetails(){


		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			/*String sql="select vProductId,vProductName,vCategoryId,vSubCategoryId, vUnitName,isnull(mDealerPrice,0)as price, "+
					" vLedgerId,isnull(vSizeName,0) asd ,isnull(Dia,0) as dia,isnull(Length,0) as length,isnull(weight,0)as weight,"+
					" isnull(cycleTime,0)as cycleTime,isnull(perSqmQty,0)as perSqmQty,perSqmUnit, isnull(perPcsQty,0) as perPcsQty,perPcsUnit,"+
					" isnull(cavityNo,0) as cavityNo,isnull(EstimatedDailyProduction,0)as estimate,isnull(ActualDailyProduction,0)as actualProd,"+
					" ISNULL(Length,0)as length,isnull(LengthConsumption,0)as LengthC,isnull(width,0) as width,isnull(widthConsumption,0)as widthC"+
					" from tbFinishedProductInfo where vProductId = '"+cmbFGName.getValue()+"' ";*/

			/*String sql="select a.vProductId,a.vProductName,a.vCategoryId,a.vSubCategoryId, a.vUnitName, "+
					"isnull(a.mDealerPrice,0)as price,  a.vLedgerId,isnull(vSizeName,0) asd ,isnull(a.Dia,0) as dia, "+
					"isnull(a.Length,0) as length,isnull(a.weight,0)as weight, isnull(a.cycleTime,0)as cycleTime, "+
					"isnull(a.perSqmQty,0)as perSqmQty,a.perSqmUnit, isnull(a.perPcsQty,0) as perPcsQty,a.perPcsUnit, "+ 
					"isnull(a.cavityNo,0) as cavityNo,isnull(a.EstimatedDailyProduction,0)as estimate, "+
					"isnull(a.ActualDailyProduction,0)as actualProd, ISNULL(a.Length,0)as length,isnull(a.LengthConsumption,0)as LengthC, "+
					"isnull(a.width,0) as width,isnull(a.widthConsumption,0)as widthC,b.rawItemCode,isnull(b.perPcs,0)as perpcs "+
					"from tbFinishedProductInfo a inner join tbFinishedProductDetails b on a.vProductId=b.fgId "+
					"where a.vProductId = '"+cmbFGName.getValue()+"'";*/

			/*String sql="select a.vProductId,a.vProductName,a.vUnitName, isnull(a.mDealerPrice,0)unitPrice ,isnull(vSizeName,0)packetQty,isnull(a.weight,0) stdWeight, "+
					"isnull(a.cycleTime,0) cycleTime, isnull(a.cavityNo,0)cavity,isnull(a.EstimatedDailyProduction,0)estimate, "+
					"isnull(a.ActualDailyProduction,0)actualProduction,b.rawItemCode,sum(isnull(b.perPcs,0))qty from tbFinishedProductInfo a "+
					"inner join tbFinishedProductDetails b on a.vProductId=b.fgId where a.vProductId = '"+cmbFGName.getValue()+"' group by "+
					"a.vProductId,a.vProductName,a.vUnitName,b.rawItemCode,a.mDealerPrice,vSizeName,a.weight,a.cycleTime, "+
					"a.cavityNo,a.EstimatedDailyProduction,a.ActualDailyProduction";

			List list=session.createSQLQuery(sql).list();
			int a=0;
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				txtFgUnit.setValue(element[2]);
				//txtDia.setValue(df.format(element[8]));
				txtWeight.setValue(df.format(element[5]));
				txtCycleTime.setValue(df.format(element[6]));
				//txtPerSqm1.setValue(df.format(element[12]));
				//txtPerSqm.setValue(element[13]);
				//txtPerPcs.setValue(element[14]);
				//txtPerPcs1.setValue(element[15]);
				txtCavity.setValue(element[7]);
				txtEstimated.setValue(df.format(element[8]));
				txtActual.setValue(df.format(element[9]));
				//txtWidthActual.setValue(df.format(element[21]));
				//txtWidthConsumption.setValue(df.format(element[22]));
				//txtLengthActual.setValue(df.format(element[19]));
			 * 
			 * 
				//txtLengthConsumption.setValue(df.format(element[20]));

				cmbRm.get(a).setValue(element[10]);
				amtQty.get(a).setValue(df.format(element[11]));
				a++;
			}*/
			String sql="select unit,stdWeight from tbSemiFgInfo where semiFgCode like '"+cmbFGName.getValue()+"'";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object element[]=(Object[])iter.next();
				txtFgUnit.setValue(element[0]);
				txtWeight.setValue(df2.format(element[1]));
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}
	}
	private void standardSl()
	{
		Transaction tx=null;
		Session ses=SessionFactoryUtil.getInstance().getCurrentSession();
		tx=ses.beginTransaction();
		Iterator iter=ses.createSQLQuery("select ISNULL(MAX(slFlag),0)+1 from tbUdFinishedGoodsStandardInfo where " +
				"fGCode='"+cmbFGName.getValue()+"' and mouldName='"+cmbMouldName.getValue()+"'").list().iterator();
		if(iter.hasNext()){
			sl=Integer.valueOf(iter.next().toString());
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
			File f = new File(sessionBean.emailpathproducformulation);
			f.mkdirs();
			System.out.printf("3");
			System.out.printf("f"+f);
			String MasterId="";
			log = new FileWriter("D:/Tomcat 7.0/webapps/report/astecherp/Formulation/log.txt");
			System.out.printf("log"+log);
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			Transaction tx = session.beginTransaction();
			
			
			String host = "smtp.gmail.com";
			String from = "";
			String pass = "";
			
			
			from="evisionsoftwareltd@gmail.com";
			pass="786@esl10";
			

			String EmailTo="support@eslctg.com";
			String EmailSubject="Product Formulation Edit";
			String EmailTxt="Product Name "+cmbFGName.getItemCaption(cmbFGName.getValue())+" has been Edited \n"+
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
			MasterId=txtStdNo.getValue().toString();
			
			System.out.printf("4");
			System.out.printf("\n4.1"+MasterId);
			reportGenerate(MasterId,sessionBean.emailpathproducformulation+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			
			
			
			MimeMessage message = new MimeMessage(esession);
			
			
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("astechfty@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("costing@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("ahmedtalba@astechbd.com"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("support@eslctg.com"));
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
			
			DataSource source = (DataSource) new FileDataSource(sessionBean.emailpathproducformulation+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			System.out.println("Hello Banglaedsh3");
			messageBodyPart.setDataHandler( new DataHandler((javax.activation.DataSource) source));
			//messageBodyPart.setFileName(sessionBean.emailPathmachine+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
			
			messageBodyPart.setFileName(MasterId+"_"+"_"+EmailSubject+".pdf");
			multipart.addBodyPart(messageBodyPart);
			System.out.printf("9");
			// Put parts in message
			message.setContent(multipart);
			System.out.printf("10");
			Transport transport = esession.getTransport("smtp");
			System.out.println(sessionBean.emailpathproducformulation+"Email/"+MasterId+"_"+"_"+EmailSubject+".pdf");
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

		/*query=     "select a.userIp,(select name from tbLogin where userId=a.userId)userName,"
				+"a.EntryTime ,c.status,a.partyCode,(select partyName from tbPartyInfo where vGroupId=a.partyCode)partyName,a.fGCode,  c.semiFgName+' #  '+c.color as semiFgName,"
				+"c.stdWeight,b.RawItemCode,(select vRawItemName from tbRawItemInfo where vRawItemCode=b.RawItemCode)rawItemName,"
				+"b.unit,b.percentage,b.Qty,a.mouldName as mouldId,(select mouldName from tbmouldInfo where mouldid=a.mouldName)mouldName,isnull(isFg,'')isFg, case "
				+"when b.udFlag='New'then 'Updated' else  'Updated' end as udFlag from tbudFinishedGoodsStandardInfo a "
				+"inner join tbudFinishedGoodsStandardDetails b on a.JobNo=b.JobNo "
				+"inner join tbSemiFgInfo c on a.fGCode=c.semiFgCode where "
				+"a.mouldName like '"+cmbMouldName.getValue().toString()+"' and a.fGCode like '"+cmbFGName.getValue().toString()+"'  order by b.AutoId desc ";
			*/
		
		query=  "select a.userIp,(select name from tbLogin where userId=a.userId)userName, "
				+"a.EntryTime ,c.status,a.partyCode,(select partyName from tbPartyInfo where vGroupId=a.partyCode)partyName,a.fGCode,  c.semiFgName+' #  '+c.color as semiFgName, "
				+"c.stdWeight,b.RawItemCode, case when b.RawItemCode like '%RI%' then  (select vRawItemName from tbRawItemInfo where vRawItemCode=b.RawItemCode) else (select vItemName from tbThirdPartyItemInfo where vCode=b.RawItemCode) end rawItemName, "
				+"b.unit,b.percentage,b.Qty,a.mouldName as mouldId,(select mouldName from tbmouldInfo where mouldid=a.mouldName)mouldName,isnull(isFg,'')isFg, case " 
				+"when b.udFlag='New'then 'Updated' else  'Updated' end as udFlag from tbudFinishedGoodsStandardInfo a " 
				+"inner join tbudFinishedGoodsStandardDetails b on a.JobNo=b.JobNo " 
				+"inner join tbSemiFgInfo c on a.fGCode=c.semiFgCode where " 
				+"a.mouldName like '"+cmbMouldName.getValue().toString()+"' and a.fGCode like '"+cmbFGName.getValue().toString()+"'  order by b.AutoId desc ";
					
		
		System.out.println("query is:"+query);	   
		
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
			hm.put("parentType", "Prduct Formulation");
			hm.put("Phone", sessionBean.getCompanyContact());
			hm.put("sql", query);
			System.out.println("Done!!");
			FileOutputStream of = new FileOutputStream(fpath);
				
            try
            {
            	JasperRunManager.runReportToPdfStream(getClass().getClassLoader().getResourceAsStream("report/production/rawUDProductFormulation.jasper"), of, hm,session.connection());
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
	
	
	
	private void insertData(Session session,Transaction tx){

		try{
			String type="New";
			if(isUpdate){
				
				type="Update";
			}
			autoJobNo();

			double rawTotal=Double.parseDouble(lblRaw.getValue().toString().isEmpty()?"0.0":lblRaw.getValue().toString());
			double inkTotal=Double.parseDouble(lblInk.getValue().toString().isEmpty()?"0.0":lblInk.getValue().toString());
			double packTotal=Double.parseDouble(lblPack.getValue().toString().isEmpty()?"0.0":lblPack.getValue().toString());

			System.out.println("Raw Total: "+rawTotal);
			System.out.println("Ink Total: "+inkTotal);
			System.out.println("Packing Total: "+packTotal);

			String sql="insert into tbFinishedGoodsStandardInfo (jobNO,partyCode,fGCode,declarationDate," +
					"rawMaterialAmount,packingAmount,InkAmount,userIp,userId,EntryTime,mouldName,isFg,status,slFlag)"+
					" values('"+txtStdNo.getValue()+"','"+cmbParty.getValue()+"','"+cmbFGName.getValue()+"'," +
					"'"+dateformat1.format(dDeclaration.getValue())+"'," +
					"'"+rawTotal+"','"+packTotal+"','"+inkTotal+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"'," +
					"CURRENT_TIMESTAMP,'"+cmbMouldName.getValue()+"','"+RadioBtnGroup.getValue()+"'," +
					"'"+optGroup.getValue()+"','"+sl+"')";
			session.createSQLQuery(sql).executeUpdate();
			
			String sqlUd="insert into tbUdFinishedGoodsStandardInfo (jobNO,partyCode,fGCode,declarationDate," +
					"rawMaterialAmount,packingAmount,InkAmount,userIp,userId,EntryTime,mouldName,isFg,status,slFlag,udFlag)"+
					" values('"+txtStdNo.getValue()+"','"+cmbParty.getValue()+"','"+cmbFGName.getValue()+"'," +
					"'"+dateformat1.format(dDeclaration.getValue())+"'," +
					"'"+rawTotal+"','"+packTotal+"','"+inkTotal+"'," +
					"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"'," +
					"CURRENT_TIMESTAMP,'"+cmbMouldName.getValue()+"','"+RadioBtnGroup.getValue()+"'," +
					"'"+optGroup.getValue()+"','"+sl+"','"+type+"')";
			session.createSQLQuery(sqlUd).executeUpdate();
			
			for(int a=0;a<cmbRm.size();a++){
				if(cmbRm.get(a).getValue()!=null&&!amtAmount.get(a).toString().isEmpty())
				{
					String sql1="insert into tbFInishedGoodsStandardDetails " +
							"(jobNo,RawItemCode,unit,Qty,rate,Amount,userIp,userId,EntryTime,itemType,pcskg,percentage)"
							+" values('"+txtStdNo.getValue()+"','"+cmbRm.get(a).getValue()+"','"+txtUnit.get(a).getValue()+"'," +
							"'"+amtQty.get(a).getValue()+"','"+txtRate.get(a).getValue()+"','"+amtAmount.get(a).getValue()+"'," +
							"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'Raw Material',0.00,'"+amtPercent.get(a).getValue()+"')";
					session.createSQLQuery(sql1).executeUpdate();
					
					String sql1Ud="insert into tbUdFInishedGoodsStandardDetails " +
							"(jobNo,RawItemCode,unit,Qty,rate,Amount,userIp,userId,EntryTime,itemType,pcskg,percentage,UdFlag)"
							+" values('"+txtStdNo.getValue()+"','"+cmbRm.get(a).getValue()+"','"+txtUnit.get(a).getValue()+"'," +
							"'"+amtQty.get(a).getValue()+"','"+txtRate.get(a).getValue()+"','"+amtAmount.get(a).getValue()+"'," +
							"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'Raw Material',0.00,'"+amtPercent.get(a).getValue()+"','"+type+"')";
					session.createSQLQuery(sql1Ud).executeUpdate();
				}
			}
			for(int a=0;a<cmbPack.size();a++)
			{
				if(cmbPack.get(a).getValue()!=null&&!amtAmount1.get(a).toString().isEmpty()){

					String sql1="insert into tbFInishedGoodsStandardDetails " +
							"(jobNo,RawItemCode,unit,Qty,rate,Amount,userIp,userId,EntryTime,itemType,pcskg)"
							+" values('"+txtStdNo.getValue()+"','"+cmbPack.get(a).getValue()+"','"+txtUnit1.get(a).getValue()+"'," +
							"'"+amtQty1.get(a).getValue()+"','"+txtRate1.get(a).getValue()+"','"+amtAmount1.get(a).getValue()+"'," +
							"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'Packing Material',0.00)";
					session.createSQLQuery(sql1).executeUpdate();
					
					String sql1ud="insert into tbUdFInishedGoodsStandardDetails " +
							"(jobNo,RawItemCode,unit,Qty,rate,Amount,userIp,userId,EntryTime,itemType,pcskg,udflag)"
							+" values('"+txtStdNo.getValue()+"','"+cmbPack.get(a).getValue()+"','"+txtUnit1.get(a).getValue()+"'," +
							"'"+amtQty1.get(a).getValue()+"','"+txtRate1.get(a).getValue()+"','"+amtAmount1.get(a).getValue()+"'," +
							"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'Packing Material',0.00,'"+type+"')";
					session.createSQLQuery(sql1ud).executeUpdate();
				}
			}
			for(int a=0;a<cmbInk.size();a++){
				if(cmbInk.get(a).getValue()!=null&&!amtAmount2.get(a).toString().isEmpty()){

					String sql1="insert into tbFInishedGoodsStandardDetails " +
							"(jobNo,RawItemCode,unit,Qty,rate,Amount,userIp,userId,EntryTime,itemType,pcskg)"
							+" values('"+txtStdNo.getValue()+"','"+cmbInk.get(a).getValue()+"','"+txtUnit2.get(a).getValue()+"'," +
							"'"+amtQty2.get(a).getValue()+"','"+txtRate2.get(a).getValue()+"','"+amtAmount2.get(a).getValue()+"'," +
							"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'Ink','"+amtkgpcs.get(a).getValue()+"')";
					session.createSQLQuery(sql1).executeUpdate();
					
					String sql1Ud="insert into tbUdFInishedGoodsStandardDetails " +
							"(jobNo,RawItemCode,unit,Qty,rate,Amount,userIp,userId,EntryTime,itemType,pcskg,udFlag)"
							+" values('"+txtStdNo.getValue()+"','"+cmbInk.get(a).getValue()+"','"+txtUnit2.get(a).getValue()+"'," +
							"'"+amtQty2.get(a).getValue()+"','"+txtRate2.get(a).getValue()+"','"+amtAmount2.get(a).getValue()+"'," +
							"'"+sessionBean.getUserIp()+"','"+sessionBean.getUserId()+"',CURRENT_TIMESTAMP,'Ink','"+amtkgpcs.get(a).getValue()+"','"+type+"')";
					session.createSQLQuery(sql1Ud).executeUpdate();
				}
			}
			tx.commit();
			showNotification("All Information Save Successfully",Notification.TYPE_HUMANIZED_MESSAGE);

		}
		catch(Exception exp){
			tx.rollback();
			showNotification("InsertData: "+exp,Notification.TYPE_ERROR_MESSAGE);
		}
	}
	private boolean deleteData(Session session,Transaction tx){
		try{
			session.createSQLQuery("delete from tbFinishedGoodsStandardInfo where JobNo='"+txtStdNo.getValue()+"'").executeUpdate();
			session.createSQLQuery("delete from tbFinishedGoodsStandardDetails where JobNo='"+txtStdNo.getValue()+"'").executeUpdate();
			return true;
		}
		catch(Exception exp){
			this.getParent().showNotification("Error",exp+"",Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
	}
	private boolean qtyZeroCheck(){



		for(int a=0;a<lblSl.size();a++){
			if(cmbRm.get(a).getValue()!=null){
				if(amtQty.get(a).getValue().toString().isEmpty()||amtQty.get(a).getValue().toString().equalsIgnoreCase("0")){
					return false;
				}
			}
		}
		for(int a=0;a<lblSl1.size();a++){
			if(cmbPack.get(a).getValue()!=null){
				if(amtQty1.get(a).getValue().toString().isEmpty()||amtQty1.get(a).getValue().toString().equalsIgnoreCase("0")){
					return false;
				}
			}
		}
		for(int a=0;a<lblSl2.size();a++){
			if(cmbInk.get(a).getValue()!=null){
				if(amtQty2.get(a).getValue().toString().isEmpty()||amtQty2.get(a).getValue().toString().equalsIgnoreCase("0")){
					return false;
				}
			}
		}
		return true;
	}
	private void saveButtonEvent() {

		if(cmbMouldName.getValue()!=null){
			if(cmbFGName.getValue()!=null){
				if(cmbRm.get(0).getValue()!=null&&!amtQty.get(0).getValue().toString().isEmpty()&&!amtAmount.get(0).getValue().toString().isEmpty()){

					if(qtyZeroCheck()){
						if(isUpdate){
							MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to update ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
							mb.show(new EventListener() 
							{
								public void buttonClicked(ButtonType buttonType) 
								{
									if (buttonType == ButtonType.YES) 
									{
										standardSl();
										Transaction tx = null;
										Session session = SessionFactoryUtil.getInstance().getCurrentSession();
										tx = session.beginTransaction();
										//if(deleteData(session,tx)){
											insertData(session,tx);
											//emailSend();
											txtClear();
											componentIni(true);
										//}
										isFind = false;
										isUpdate = false;
									}
								}
							});
						}
						else{
							MessageBox mb = new MessageBox(getParent(), "Are you sure?",MessageBox.Icon.QUESTION, "Do you want to Save ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
							mb.show(new EventListener() 
							{
								public void buttonClicked(ButtonType buttonType) 
								{
									if (buttonType == ButtonType.YES) 
									{
										standardSl();
										Transaction tx = null;
										Session session = SessionFactoryUtil.getInstance().getCurrentSession();
										tx = session.beginTransaction();
										
										insertData(session,tx);
										txtClear();
										componentIni(true);
										btnIni(true);
										isFind = false;
										isUpdate = false;
									}
								}
							});
						}
					}
					else{
						showNotification("Quantity Can't be Zero.",Notification.TYPE_WARNING_MESSAGE);
					}
				}
				else{
					showNotification("Please Provide all fields.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
			else{
				showNotification("Select Finished Goods Please.",Notification.TYPE_WARNING_MESSAGE);
			}
		}
		else{
			showNotification("Select Mould Name Please.",Notification.TYPE_WARNING_MESSAGE);
		}
	}

	private void cmbFinishedPartyInfo() {
		cmbFindMouldName.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select vGroupId,partyName from tbPartyInfo where vGroupId in (select partyCode from tbFinishedGoodsStandardInfo)";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindMouldName.addItem(element[0]);
				cmbFindMouldName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("Company Data: "+exp);
		}

	}
	private void setEventAction() 
	{

		cButton.btnNew.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				newButtonEvent();
			}
		});
		cButton.btnRefresh.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {

				refreshEvent();
			}
		});
		cButton.btnFind.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				findButtonEvent();
				//cmbFinishedPartyInfo();
				cmbFindFgDataLoad();
			}

		});
		cButton.btnEdit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				if(sessionBean.isUpdateable())
				{
					updateButtonEvent();
					isFind = false;
					cmbFGName.focus();
				}
				else
				{
					showNotification("Warning,","You are not permitted to edit data.",Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});
		cButton.btnExit.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				close();
			}
		});
		cButton.btnSave.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				saveButtonEvent();
				cButton.btnNew.focus();

			}
		});
		/*cmbParty.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbParty.getValue()!=null){
					cmbFinishedGoodsData();
				}
			}

		});*/
		cmbFGName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbFGName.getValue()!=null)
				{
					if(cmbMouldName.getValue()!=null){
						dataclear();
						Transaction tx=null;
						List lst=null;
						Session session= SessionFactoryUtil.getInstance().getCurrentSession();
						tx=session.beginTransaction();
						String query="select * from tbFinishedGoodsStandardInfo where fGCode like '"+cmbFGName.getValue().toString()+"' and mouldName like '"+cmbMouldName.getValue().toString()+"' ";
						lst=session.createSQLQuery(query).list();

						if(lst.isEmpty())
						{
							cmbFinishedDetails();	
						}
						else
						{
							//if(!lst.isEmpty()){
							System.out.println("Before");
							if(!isFind)
							{
								System.out.println("After");
								MessageBox mb = new MessageBox(getParent(), "Standard Of This Product All Ready Given ",MessageBox.Icon.QUESTION, "Do you want to set Standard Again ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
								mb.show(new EventListener() 
								{
									public void buttonClicked(ButtonType buttonType) 
									{
										if (buttonType == ButtonType.YES) 
										{
											cmbFinishedDetails();

										}
										else
										{
											showNotification("You Have Cancelled",Notification.TYPE_WARNING_MESSAGE);	
										}
									}
								});	
							}
							else
							{
								cmbFinishedDetails();	
							}

							//}	
						}
					}
					else{
						showNotification("Provid Mould Name Please",Notification.TYPE_WARNING_MESSAGE);
					}

				}
				else
				{
					dataclear();
				}

			}
		});

		cmbMouldName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbMouldName.getValue()!=null)
				{
					if(cmbMouldName.getValue()!=null && cmbFGName.getValue()!=null)
					{
						dataclear();
						Transaction tx=null;
						List lst=null;
						Session session= SessionFactoryUtil.getInstance().getCurrentSession();
						tx=session.beginTransaction();
						String query="select * from tbFinishedGoodsStandardInfo where fGCode like '"+cmbFGName.getValue().toString()+"' and mouldName like '"+cmbMouldName.getValue().toString()+"' ";
						lst=session.createSQLQuery(query).list();

						if(lst.isEmpty())
						{
							cmbFinishedDetails();	
						}
						else
						{
							//if(!lst.isEmpty()){
							System.out.println("Before");
							if(!isFind)
							{
								System.out.println("After");
								MessageBox mb = new MessageBox(getParent(), "Standard Of This Product All Ready Given ",MessageBox.Icon.QUESTION, "Do you want to set Standard Again ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
								mb.show(new EventListener() 
								{
									public void buttonClicked(ButtonType buttonType) 
									{
										if (buttonType == ButtonType.YES) 
										{
											cmbFinishedDetails();

										}
										else
										{
											showNotification("You Have Cancelled",Notification.TYPE_WARNING_MESSAGE);	
										}
									}
								});	
							}
							else
							{
								cmbFinishedDetails();	
							}

							//}	
						}
					}
					else{
						showNotification("Provid Select Fg Name",Notification.TYPE_WARNING_MESSAGE);
					}

				}
				else
				{
					dataclear();
				}

			}
		});


		dDeclaration.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(dDeclaration.getValue() instanceof Date)
				{
					if(cmbFGName.getValue()!=null)
					{
						unitRateLoad();
						Transaction tx=null;
						List lst=null;
						Session session= SessionFactoryUtil.getInstance().getCurrentSession();
						tx=session.beginTransaction();
						String query="select * from tbFinishedGoodsStandardInfo where fGCode like '"+cmbFGName.getValue().toString()+"' and CONVERT(date,declarationDate,105) like '"+new SimpleDateFormat("yyyy-MM-dd").format(dDeclaration.getValue())+"' ";
						lst=session.createSQLQuery(query).list();

						if(!lst.isEmpty()){

							if(!isFind)
							{
								MessageBox mb = new MessageBox(getParent(), "Standard Of This Product All Ready Given dfdf!!",MessageBox.Icon.QUESTION, "Do you want to set Standard Again ?",new MessageBox.ButtonConfig(MessageBox.ButtonType.YES,"Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
								mb.show(new EventListener() 
								{
									public void buttonClicked(ButtonType buttonType) 
									{
										if (buttonType == ButtonType.YES) 
										{
											System.out.println("All IS Well");
											cmbFinishedDetails();

										}
										else
										{
											dataclear();
											showNotification("You Have Cancelled",Notification.TYPE_WARNING_MESSAGE);	
										}
									}
								});	
							}
							else
							{
								cmbFinishedDetails();	
							}

						}	
					}


				}
			}
		});
		cmbFindFG.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindFG.getValue()!=null){
					cmbFindMouldDataLoad();
				}
				else{
					cmbFindFG.removeAllItems();
				}
			}
		});
		cmbFindMouldName.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindMouldName.getValue()!=null){
					cmbDelareDateLoad();
				}
				else{
					cmbFindMouldName.removeAllItems();
				}
			}
		});
		cmbFindDate.addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				if(cmbFindDate.getValue()!=null){
					txtClear();
					itmeLoadToCmb();
				}
				else{
					txtClear();
				}
			}
		});
		cButton.btnDelete.addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				deleteButtonEvent();
			}
		});
	}
	private boolean checkReference(){
		String sql="";
		Session session=null;
		Transaction tx=session.beginTransaction();
		Iterator<?> iter=session.createSQLQuery(sql).list().iterator();
		if(iter.hasNext()){
			return false;

		}
		return true;
	}
	private void deleteButtonEvent(){
		if(cmbFGName.getValue()!=null&&cmbMouldName.getValue()!=null){
			if(checkReference()){

				MessageBox mb = new MessageBox(getParent(), "", MessageBox.Icon.QUESTION, "Do you want to Delete ?", new MessageBox.ButtonConfig(MessageBox.ButtonType.YES, "Yes"), new MessageBox.ButtonConfig(MessageBox.ButtonType.NO, "No"));
				mb.show(new EventListener()
				{
					public void buttonClicked(ButtonType buttonType)
					{
						if(buttonType == ButtonType.YES)
						{
							Transaction tx=null;
							Session session = SessionFactoryUtil.getInstance().getCurrentSession();
							tx = session.beginTransaction();
							if(deleteData(session,tx)){
								btnIni(true);
								componentIni(true);
								txtClear();
								cButton.btnNew.focus();
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
	private void updateButtonEvent() {
		if (cmbFGName.getValue() != null&&!amtAmount.get(0).getValue().isEmpty()) 
		{
			isUpdate = true;
			btnIni(false);
			componentIni(false);
			panelSearch.setEnabled(false);
			System.out.println(isUpdate);
		} 
		else
		{
			this.getParent().showNotification("Update Failed","There are no data for update.",Notification.TYPE_WARNING_MESSAGE);
		}
	}
	private void itmeLoadToCmb() 
	{
		int raw=0,pack=0,ink=0,i=0;
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
		/*	String sql= "select '',a.fGCode,b.RawItemCode,c.vRawItemName,c.vCategoryType,b.unit, "
					+"b.Qty,b.rate,b.Amount,a.JobNo, a.declarationDate, isnull(pcskg,0)pcskg,b.percentage,a.mouldName,a.isFg,a.status,a.slFlag  from tbFinishedGoodsStandardInfo a "  
					+"inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo inner join tbRawItemInfo "
					+"c on b.RawItemCode=c.vRawItemCode where a.mouldName='"+cmbFindMouldName.getValue()+"' and a.fGCode='"+cmbFindFG.getValue()+"'  "
					+"and  declarationDate like  (select MAX(declarationDate) from tbFinishedGoodsStandardInfo where mouldName like " +
					" '"+cmbFindMouldName.getValue()+"' and fGCode like '"+cmbFindFG.getValue()+"' and  CONVERT(varchar(10),declarationDate,103) like '"+cmbFindDate.getValue()+"')";
*/
		/*	String sql="select '',a.fGCode,b.RawItemCode, case when b.RawItemCode like '%RI%' then ( select vRawItemName from tbRawItemInfo where tbRawItemInfo.vRawItemCode=b.RawItemCode) else  "
					+ " ( select vItemName from tbThirdPartyItemInfo where vCode=vRawItemCode ) end vRawItemName,   case when b.RawItemCode like '%RI%' then ( select vCategoryType from tbRawItemInfo where tbRawItemInfo.vRawItemCode=b.RawItemCode) "
					+ " else ( select vCategoryType from tbThirdPartyItemInfo where vCode=vRawItemCode ) end   vCategoryType,b.unit, " 
					+ "b.Qty,b.rate,b.Amount,a.JobNo, a.declarationDate, isnull(pcskg,0)pcskg,b.percentage,a.mouldName,a.isFg,a.status,a.slFlag  from tbFinishedGoodsStandardInfo a "   
					+ "inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo inner join tbRawItemInfo " 
					+ "c on b.RawItemCode=c.vRawItemCode where a.mouldName='"+cmbFindMouldName.getValue()+"' and a.fGCode='"+cmbFindFG.getValue()+"' "  
					+ "and  declarationDate like  (select MAX(declarationDate) from tbFinishedGoodsStandardInfo where mouldName like " 
					+ " '"+cmbFindMouldName.getValue()+"' and fGCode like '"+cmbFindFG.getValue()+"' and  CONVERT(varchar(10),declarationDate,103) like '"+cmbFindDate.getValue()+"') ";
			*/
			
			
			

               String sql= "select '',a.fGCode,b.RawItemCode, case when b.RawItemCode like '%RI%' then ( select vRawItemName from tbRawItemInfo where tbRawItemInfo.vRawItemCode=b.RawItemCode) else  " 
					       +"( select vItemName from tbThirdPartyItemInfo where vCode=b.RawItemCode ) end vRawItemName,   case when b.RawItemCode like '%RI%' then ( select vCategoryType from tbRawItemInfo where tbRawItemInfo.vRawItemCode=b.RawItemCode) " 
					       +"else ( select vCategoryType from tbThirdPartyItemInfo where vCode=b.RawItemCode ) end   vCategoryType,b.unit, " 
					       +"b.Qty,b.rate,b.Amount,a.JobNo, a.declarationDate, isnull(pcskg,0)pcskg,b.percentage,a.mouldName,a.isFg,a.status,a.slFlag  from tbFinishedGoodsStandardInfo a  "   
					       +"inner join tbFinishedGoodsStandardDetails b on a.JobNo=b.JobNo " 
					       +"where a.mouldName='"+cmbFindMouldName.getValue()+"' and a.fGCode='"+cmbFindFG.getValue()+"' "   
					       +"and  declarationDate  =  (select max(declarationDate)  from tbFinishedGoodsStandardInfo where mouldName like "  
					       +"'"+cmbFindMouldName.getValue()+"' and fGCode like '"+cmbFindFG.getValue()+"' and  CONVERT(varchar(10),declarationDate,103) like '"+cmbFindDate.getValue()+"') ";
			
			System.out.println("Sql Is"+sql);

			List list=session.createSQLQuery(sql).list();

			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				if(i==0)
				{
					//cmbParty.setValue(element[0]);
					cmbMouldName.setValue(element[13]);
					cmbFGName.setValue(element[1]);	
					RadioBtnGroup.setValue(element[14]);
					optGroup.setValue(element[15]);
					sl=Integer.valueOf(element[16].toString());
					//dDeclaration.setValue(element[10]);

					System.out.println("FGName: "+element[1]);
					System.out.println("MouldName: "+element[13]);
					System.out.println("Declaration: "+element[10]);
				}

				txtStdNo.setValue(element[9]);
				if(element[4].toString().equalsIgnoreCase("Raw Material"))
				{
					cmbRm.get(raw).setValue(element[2]);
					amtPercent.get(raw).setValue(df2.format(element[12]));
					amtQty.get(raw).setValue(df.format(element[6]));
					txtRate.get(raw).setValue(df.format(element[7]));
					amtAmount.get(raw).setValue(df.format(element[8]));
					raw++;
				}
				else if(element[4].toString().equalsIgnoreCase("Packing Material"))
				{
					cmbPack.get(pack).setValue(element[2]);
					amtQty1.get(raw).setValue(df.format(element[6]));
					txtRate1.get(raw).setValue(df.format(element[7]));
					amtAmount1.get(raw).setValue(df.format(element[8]));
					pack++;
				}

				else if(element[4].toString().equalsIgnoreCase("Ink"))
				{
					cmbInk.get(ink).setValue(element[2]);
					amtkgpcs.get(ink).setValue(element[11]);
					amtQty2.get(raw).setValue(df.format(element[6]));
					txtRate2.get(raw).setValue(df.format(element[7]));
					amtAmount2.get(raw).setValue(df.format(element[8]));
					ink++;
				}
				i++;
			}


			System.out.println("OK");

		}
		catch(Exception exp){
			showNotification("cmbDelareDateLoad: "+exp);
		}
	}
	private void cmbDelareDateLoad() {
		cmbFindDate.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select 0,CONVERT(varchar(10),declarationDate,103) as date from tbFinishedGoodsStandardInfo where mouldName='"+cmbFindMouldName.getValue()+"' and fGCode='"+cmbFindFG.getValue()+"'";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindDate.addItem(element[1]);
				//cmbFindDate.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbDelareDateLoad: "+exp);
		}
	}
	private void cmbFindFgDataLoad() {
		cmbFindFG.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select semiFgCode,semiFgName,color from tbSemiFgInfo where semiFgCode in" +
					"(select fGCode from tbFinishedGoodsStandardInfo)";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindFG.addItem(element[0]);
				cmbFindFG.setItemCaption(element[0], element[1].toString()+" # "+element[2].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbFindFgDataLoad: "+exp);
		}
	}
	private void cmbFindMouldDataLoad() {
		cmbFindMouldName.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select a.mouldName,(select mouldName from tbmouldInfo b where b.mouldid=a.mouldName) " +
					"from tbFinishedGoodsStandardInfo a where fGCode like '"+cmbFindFG.getValue()+"'";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbFindMouldName.addItem(element[0]);
				cmbFindMouldName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbFindFgDataLoad: "+exp);
		}
	}
	private void cmbMouldData(){

		cmbMouldName.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct mouldid,mouldName from tbmouldInfo order by mouldid";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbMouldName.addItem(element[0]);
				cmbMouldName.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbMouldName: "+exp);
		}
	}
	/*private void cmbPartyData(){

		cmbParty.removeAllItems();
		Transaction tx=null;
		try{
			Session session=SessionFactoryUtil.getInstance().getCurrentSession();
			tx=session.beginTransaction();
			String sql="select distinct partyCode,partyName from tbSemiFgInfo order by partyCode";
			List list=session.createSQLQuery(sql).list();
			for(Iterator iter=list.iterator();iter.hasNext();){

				Object[] element=(Object[]) iter.next();
				cmbParty.addItem(element[0]);
				cmbParty.setItemCaption(element[0], element[1].toString());
			}
		}
		catch(Exception exp){
			showNotification("cmbPartyData: "+exp);
		}
	}*/
	private void btnIni(boolean t)
	{
		cButton.btnNew.setEnabled(t);
		cButton.btnEdit.setEnabled(t);
		cButton.btnSave.setEnabled(!t);
		cButton.btnRefresh.setEnabled(!t);
		cButton.btnDelete.setEnabled(t);
		cButton.btnFind.setEnabled(t);
	}
	private AbsoluteLayout buildMainLayout(){
		// mainLayout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(false);
		setWidth("1160px");
		setHeight("700px");


		lblPartyName=new Label();
		lblPartyName.setValue("Party Name: ");
		lblPartyName.setVisible(false);
		mainLayout.addComponent(lblPartyName,"top:20px;left:10px;");

		cmbParty=new ComboBox();
		cmbParty.setImmediate(true);
		cmbParty.setWidth("300px");
		cmbParty.setHeight("24px");
		cmbParty.setNullSelectionAllowed(true);
		cmbParty.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbParty.setVisible(false);
		mainLayout.addComponent(cmbParty,"top:18px;left:140px;");

		lblFGName=new Label();
		lblFGName.setValue("Semi Finished Goods: ");
		mainLayout.addComponent(lblFGName,"top:45px;left:10px;");

		cmbFGName=new ComboBox();
		cmbFGName.setImmediate(true);
		cmbFGName.setWidth("300px");
		cmbFGName.setHeight("24px");
		cmbFGName.setNullSelectionAllowed(true);
		cmbFGName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbFGName,"top:43px;left:140px;");

		lblMouldName=new Label();
		lblMouldName.setValue("Mould Name: ");
		mainLayout.addComponent(lblMouldName,"top:20px;left:10px;");

		cmbMouldName=new ComboBox();
		cmbMouldName.setImmediate(true);
		cmbMouldName.setWidth("300px");
		cmbMouldName.setHeight("24px");
		cmbMouldName.setNullSelectionAllowed(true);
		cmbMouldName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		mainLayout.addComponent(cmbMouldName,"top:18px;left:140px;");

		mainLayout.addComponent(new Label("IS FG? :"),"top:20px;left:460px;");
		RadioBtnGroup = new OptionGroup("",option);
		RadioBtnGroup.setImmediate(true);
		RadioBtnGroup.setValue("NO");
		RadioBtnGroup.setStyleName("horizontal");
		mainLayout.addComponent(RadioBtnGroup, "top:18.0px; left:510.0px;");

		optGroup=new OptionGroup("", optStatus);
		optGroup.setImmediate(true);
		optGroup.setStyleName("horizontal");
		optGroup.setValue("Active");

		mainLayout.addComponent(new Label("Status: "), "top:45px;left:460.0px;");
		mainLayout.addComponent(optGroup, "top:43.0px;left:510.0px;");

		lblDeclarationDate=new Label();
		lblDeclarationDate.setValue("Declaration Date: ");
		mainLayout.addComponent(lblDeclarationDate,"top:70px;left:10px;");

		dDeclaration=new PopupDateField();
		dDeclaration.setResolution(PopupDateField.RESOLUTION_DAY);
		dDeclaration.setValue(new java.util.Date());
		dDeclaration.setWidth("110px");
		dDeclaration.setDateFormat("dd-MM-yyyy");
		mainLayout.addComponent(dDeclaration,"top:68px;left:140px;");

		lblFgUnit=new Label();
		lblFgUnit.setValue("Unit : ");
		mainLayout.addComponent(lblFgUnit,"top:70px;left:260px;");

		txtFgUnit=new TextRead(1);
		txtFgUnit.setWidth("70px");
		txtFgUnit.setImmediate(true);
		mainLayout.addComponent(txtFgUnit,"top:68px;left:290px;");

		lblPercent=new Label();
		lblPercent.setValue("Job No : ");
		mainLayout.addComponent(lblPercent,"top:95px;left:10px;");

		txtStdNo=new TextRead();
		txtStdNo.setWidth("60px");
		txtStdNo.setImmediate(true);
		mainLayout.addComponent(txtStdNo,"top:93px;left:140px;");

		// lblLength
		lblWidthActual = new Label("Width  <font color='#0000FF'><b>[Actual]</b></Font>  :",Label.CONTENT_XHTML);
		lblWidthActual.setImmediate(false);
		lblWidthActual.setWidth("-1px");
		lblWidthActual.setHeight("-1px");
		lblWidthActual.setVisible(false);
		mainLayout.addComponent(lblWidthActual,"top:95px;left:180px;");

		// afLength
		txtWidthActual = new TextRead(1);
		txtWidthActual.setImmediate(true);
		txtWidthActual.setWidth("80px");
		txtWidthActual.setHeight("-1px");
		txtWidthActual.setVisible(false);
		mainLayout.addComponent(txtWidthActual,"top:93px;left:270px;");

		lblWidthConsumption = new Label("Width  <font color='#0000FF'><b>[Consumption]</b></Font>  :",Label.CONTENT_XHTML);
		lblWidthConsumption.setImmediate(false);
		lblWidthConsumption.setWidth("-1px");
		lblWidthConsumption.setHeight("-1px");
		lblWidthConsumption.setVisible(false);
		mainLayout.addComponent(lblWidthConsumption,"top:95px;left:365px;");

		txtWidthConsumption = new TextRead(1);
		txtWidthConsumption.setImmediate(true);
		txtWidthConsumption.setWidth("80px");
		txtWidthConsumption.setHeight("-1px");
		txtWidthConsumption.setVisible(false);
		mainLayout.addComponent(txtWidthConsumption,"top:93px;left:490px;");

		// lblLength
		lblHeightActual = new Label("Height  <font color='#0000FF'><b>[Actual]</b></Font>  :",Label.CONTENT_XHTML);
		lblHeightActual.setImmediate(false);
		lblHeightActual.setWidth("-1px");
		lblHeightActual.setHeight("-1px");
		lblHeightActual.setVisible(false);
		mainLayout.addComponent(lblHeightActual,"top:95px;left:590px;");

		txtLengthActual = new TextRead(1);
		txtLengthActual.setImmediate(true);
		txtLengthActual.setWidth("80px");
		txtLengthActual.setHeight("-1px");
		txtLengthActual.setVisible(false);
		mainLayout.addComponent(txtLengthActual,"top:93px;left:690px;");

		lblHeightConsumption = new Label("Height  <font color='#0000FF'><b>[Consumption]</b></Font>  :",Label.CONTENT_XHTML);
		lblHeightConsumption.setImmediate(false);
		lblHeightConsumption.setWidth("-1px");
		lblHeightConsumption.setHeight("-1px");
		lblHeightConsumption.setVisible(false);
		mainLayout.addComponent(lblHeightConsumption,"top:95px;left:790px;");

		txtLengthConsumption = new TextRead(1);
		txtLengthConsumption.setImmediate(true);
		txtLengthConsumption.setWidth("80px");
		txtLengthConsumption.setHeight("-1px");
		txtLengthConsumption.setVisible(false);
		mainLayout.addComponent(txtLengthConsumption,"top:93px;left:935px;");

		/*lbl=new Label();
		lbl.setValue("%");
		mainLayout.addComponent(lbl,"top:97px;left:220px;");*/

		lblDia=new Label();
		lblDia.setValue("Dia : ");
		lblDia.setVisible(false);
		mainLayout.addComponent(lblDia,"top:70px;left:610px;");

		txtDia=new TextRead(1);
		txtDia.setWidth("100px");
		txtDia.setImmediate(true);
		txtDia.setVisible(false);
		mainLayout.addComponent(txtDia,"top:68px;left:610px;");

		lblWeight=new Label();
		lblWeight.setValue("Std. Weight : ");
		//lblWeight.setVisible(false);
		mainLayout.addComponent(lblWeight,"top:95px;left:210px;");

		txtWeight=new TextRead(1);
		txtWeight.setImmediate(true);
		txtWeight.setWidth("100px");
		//txtWeight.setVisible(false);
		mainLayout.addComponent(txtWeight,"top:93px;left:290px;");

		lblCycleTime=new Label();
		lblCycleTime.setValue("Cycle Time : ");
		lblCycleTime.setVisible(false);
		mainLayout.addComponent(lblCycleTime,"top:70px;left:420px;");

		txtCycleTime=new TextRead(1);
		txtCycleTime.setImmediate(true);
		txtCycleTime.setWidth("100px");
		txtCycleTime.setVisible(false);
		mainLayout.addComponent(txtCycleTime,"top:68px;left:490px;");

		lblPerSqm=new Label();
		lblPerSqm.setValue("Per ");
		lblPerSqm.setVisible(false);
		mainLayout.addComponent(lblPerSqm,"top:20px;left:610px;");

		txtPerSqm=new TextRead(1);
		txtPerSqm.setImmediate(true);
		txtPerSqm.setWidth("60px");
		txtPerSqm.setVisible(false);
		mainLayout.addComponent(txtPerSqm,"top:18px;left:670px");

		lblPerSqm1=new Label();
		lblPerSqm1.setValue("Pcs ");
		lblPerSqm1.setVisible(false);
		mainLayout.addComponent(lblPerSqm1,"top:20px;left:800px;");

		txtPerSqm1=new TextRead(1);
		txtPerSqm1.setImmediate(true);
		txtPerSqm1.setWidth("60px");
		txtPerSqm1.setVisible(false);
		mainLayout.addComponent(txtPerSqm1,"top:18px;left:735px");


		lblPerPcs=new Label();
		lblPerPcs.setValue("Per ");
		lblPerPcs.setVisible(false);
		mainLayout.addComponent(lblPerPcs,"top:45px;left:610px;");

		txtPerPcs=new TextRead(1);
		txtPerPcs.setImmediate(true);
		txtPerPcs.setWidth("60px");
		txtPerPcs.setVisible(false);
		mainLayout.addComponent(txtPerPcs,"top:43px;left:670px");

		lblPerPcs1=new Label();
		lblPerPcs1.setValue("Pcs ");
		lblPerPcs1.setVisible(false);
		mainLayout.addComponent(lblPerPcs1,"top:45px;left:640px;");

		txtPerPcs1=new TextRead(1);
		txtPerPcs1.setImmediate(true);
		txtPerPcs1.setWidth("60px");
		txtPerPcs1.setVisible(false);
		mainLayout.addComponent(txtPerPcs1,"top:43px;left:735px");

		lblCavity=new Label();
		lblCavity.setValue("Cavity No:  ");
		lblCavity.setVisible(false);
		mainLayout.addComponent(lblCavity,"top:20px;left:420px;");

		txtCavity=new TextRead(1);
		txtCavity.setImmediate(true);
		txtCavity.setWidth("100px");
		txtCavity.setVisible(false);
		mainLayout.addComponent(txtCavity,"top:18px;left:490px");

		lblEstimatedQty=new Label();
		lblEstimatedQty.setValue("Estimated Daily Prduction: ");
		lblEstimatedQty.setVisible(false);
		mainLayout.addComponent(lblEstimatedQty,"top:20px;left:600px;");

		txtEstimated=new TextRead(1);
		txtEstimated.setImmediate(true);
		txtEstimated.setWidth("100px");
		txtEstimated.setVisible(false);
		mainLayout.addComponent(txtEstimated,"top:18px;left:750px");

		lblActual=new Label();
		lblActual.setValue("Actual Daily Prduction: ");
		lblActual.setVisible(false);
		mainLayout.addComponent(lblActual,"top:45px;left:600px;");

		txtActual=new TextRead(1);
		txtActual.setImmediate(true);
		txtActual.setWidth("100px");
		txtActual.setVisible(false);
		mainLayout.addComponent(txtActual,"top:43px;left:750px");



		tableRm=new Table();
		tableRm.setWidth("590px");
		tableRm.setHeight("195px");
		tableRm.setColumnCollapsingAllowed(true);
		tableRm.setFooterVisible(true);

		tableRm.addContainerProperty("SL", Label.class,new Label());
		tableRm.setColumnWidth("SL", 10);

		tableRm.addContainerProperty("Raw Material", ComboBox.class,new ComboBox());
		tableRm.setColumnWidth("Raw Material", 220);

		tableRm.addContainerProperty("Unit", TextRead.class,new TextRead(1));
		tableRm.setColumnWidth("Unit", 50);

		tableRm.addContainerProperty("Percent", AmountField.class,new AmountField());
		tableRm.setColumnWidth("Percent", 50);

		tableRm.addContainerProperty("Qty", AmountField.class,new AmountField());
		tableRm.setColumnWidth("Qty", 50);

		tableRm.addContainerProperty("Rate", AmountField.class,new AmountField());
		tableRm.setColumnWidth("Rate", 50);

		tableRm.addContainerProperty("Amount", AmountCommaSeperator.class,new AmountCommaSeperator());
		tableRm.setColumnWidth("Amount", 60);
		tableInitRaw();
		mainLayout.addComponent(tableRm,"top:145px;left:10px;");
		tableRm.setColumnFooter("Amount", "Total:"+0.0);

		Label lblHeading1= new Label("<font color='#FF0000'><b><Strong>A.<Strong></b></font> <font color='##0000FF'><b><Strong>Raw Material :<Strong></b></font>");
		lblHeading1.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHeading1, "top:120px;left:10px;");

		tablePack=new Table();
		tablePack.setWidth("530px");
		tablePack.setHeight("195px");
		tablePack.setColumnCollapsingAllowed(true);
		tablePack.setFooterVisible(true);

		tablePack.addContainerProperty("SL", Label.class,new Label());
		tablePack.setColumnWidth("SL", 10);

		tablePack.addContainerProperty("Packing Material", ComboBox.class,new ComboBox());
		tablePack.setColumnWidth("Packing Material", 220);

		tablePack.addContainerProperty("Unit", TextRead.class,new TextRead(1));
		tablePack.setColumnWidth("Unit", 50);

		tablePack.addContainerProperty("Qty", AmountField.class,new AmountField());
		tablePack.setColumnWidth("Qty", 50);

		tablePack.addContainerProperty("Rate", AmountField.class,new AmountField());
		tablePack.setColumnWidth("Rate", 50);

		tablePack.addContainerProperty("Amount", AmountCommaSeperator.class,new AmountCommaSeperator());
		tablePack.setColumnWidth("Amount", 60);
		tableInitPack();

		tablePack.setColumnFooter("Amount", "Total:"+0.0);

		Label lblHeading2= new Label("<font color='#FF0000'><b><Strong>B.<Strong></b></font> <font color='##0000FF'><b><Strong>Ink :<Strong></b></font>");
		lblHeading2.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHeading2, "top:120px;left:620px;");

		tableInk=new Table();
		tableInk.setWidth("530px");
		tableInk.setHeight("195px");
		tableInk.setColumnCollapsingAllowed(true);
		tableInk.setFooterVisible(true);

		tableInk.addContainerProperty("SL", Label.class,new Label());
		tableInk.setColumnWidth("SL", 10);

		tableInk.addContainerProperty("Ink", ComboBox.class,new ComboBox());
		tableInk.setColumnWidth("Ink", 170);

		tableInk.addContainerProperty("Unit", TextRead.class,new TextRead(1));
		tableInk.setColumnWidth("Unit", 40);

		tableInk.addContainerProperty("pcs/kg", AmountField.class,new AmountField());
		tableInk.setColumnWidth("pcs/kg", 50);

		tableInk.addContainerProperty("Qty", AmountField.class,new AmountField());
		tableInk.setColumnWidth("Qty", 50);

		tableInk.addContainerProperty("Rate", AmountField.class,new AmountField());
		tableInk.setColumnWidth("Rate", 50);

		tableInk.addContainerProperty("Amount", AmountCommaSeperator.class,new AmountCommaSeperator());
		tableInk.setColumnWidth("Amount", 60);
		tableInitInk();
		mainLayout.addComponent(tablePack,"top:375px;left:10px;");
		mainLayout.addComponent(tableInk,"top:145px;left:620px;");
		tableInk.setColumnFooter("Amount", "Total:"+0.0);

		Label lblPanel= new Label(" <font color='##0000FF' size='4px'><b><Strong>Search :<Strong></b></font>");
		lblPanel.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblPanel, "top:350px;left:770px;");

		panelSearch=new Panel();
		panelSearch.setWidth("450px");
		panelSearch.setHeight("180px");
		mainLayout.addComponent(panelSearch,"top:375px;left:600px;");
		panelSearch.setEnabled(false);
		panelSearch.setStyleName("panelSearch");
		//panelSearch.setStyleName(Reindeer.PANEL_LIGHT);
		//panelSearch.setStyle(Pane)

		FormLayout frmLayout=new FormLayout();
		frmLayout.setSpacing(true);
		frmLayout.setMargin(true);


		cmbFindMouldName.setImmediate(true);
		cmbFindMouldName.setWidth("250px");
		cmbFindMouldName.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		//cmbFindMouldName.setVisible(false);

		//frmLayout.addComponent(cmbFindFG);

		cmbFindFG.setImmediate(true);
		cmbFindFG.setWidth("250px");
		cmbFindFG.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);

		frmLayout.addComponent(cmbFindFG);
		frmLayout.addComponent(cmbFindMouldName);
		frmLayout.addComponent(cmbFindDate);
		cmbFindDate.setImmediate(true);
		cmbFindDate.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		panelSearch.addComponent(frmLayout);

		Label lblHeading3= new Label("<font color='#FF0000'><b><Strong>C.<Strong></b></font> <font color='##0000FF'><b><Strong>Packing Material :<Strong></b></font>");
		lblHeading3.setContentMode(Label.CONTENT_XHTML);
		mainLayout.addComponent(lblHeading3, "top:350px;left:10px;");

		Label lblLine = new Label("<b><font color='#e65100'>=========================================================================================================================================================</font></b>", Label.CONTENT_XHTML);


		mainLayout.addComponent(lblLine, "top:570px;left:10px;");
		mainLayout.addComponent(cButton, "top:590px;left:250px;");

		return mainLayout;
	}
	private void cmbRmLoadData(int ar){
		cmbRm.get(ar).removeAllItems();
		String sql = "";
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();
			
			
			sql= "select vRawItemCode,vRawItemName,category,vUnitName from ( "
                 +"select vRawItemCode,vRawItemName,SUBSTRING(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName) )" 
	             +"as category,vUnitName from tbRawItemInfo where vCategoryType like 'Raw Material' "
	
	             +"union all " 
	             +"select vCode,vItemName,SUBSTRING(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName) ) " 
	             +"as category,vUnitName from tbThirdPartyItemInfo where vCategoryType like 'Raw Material' "
	
                 +") as temp "
                 +"order by  "
                 +"cast(SUBSTRING(vRawItemCode,CHARINDEX('-',vRawItemCode)+1,len(vRawItemCode)-CHARINDEX('-',vRawItemCode))as int) ";
	

		/*	sql = "select vRawItemCode,vRawItemName,SUBSTRING(vSubGroupName,CHARINDEX('-',vSubGroupName)+1,LEN(vSubGroupName) ) "+
					"as category,vUnitName from tbRawItemInfo where vCategoryType like 'Raw Material' order by "+
					" cast(SUBSTRING(vRawItemCode,CHARINDEX('-',vRawItemCode)+1,len(vRawItemCode)-CHARINDEX('-',vRawItemCode))as int)";
			System.out.print("cmbRm: "+sql);*/

			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbRm.get(ar).addItem(element[0].toString());
				String name=element[1].toString()+"( "+element[2].toString()+" )";
				cmbRm.get(ar).setItemCaption(element[0].toString(), name);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	private void cmbPackLoadData(int ar){
		cmbPack.get(ar).removeAllItems();
		String sql = "";
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			/*sql = "select vRawItemCode,vRawItemName,vUnitName from tbRawItemInfo where vCategoryType like 'Packing Material'";
			*/
			sql=    "select vRawItemCode,vRawItemName,vUnitName from tbRawItemInfo where vCategoryType like 'Packing Material' "
					+"union " 
					+"select vCode,vItemName,vUnitName from tbThirdPartyItemInfo where vCategoryType like 'Packing Material' ";

			
			
			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbPack.get(ar).addItem(element[0].toString());
				cmbPack.get(ar).setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	private void cmbInkLoadData(int ar){
		cmbInk.get(ar).removeAllItems();
		String sql = "";
		Transaction tx = null;
		try
		{
			Session session = SessionFactoryUtil.getInstance().getCurrentSession();
			tx = session.beginTransaction();

			sql="select vRawItemCode,vRawItemName,vUnitName from tbRawItemInfo where vCategoryType like 'Ink' "
				+"union " 
				+"select vCode,vItemName,vUnitName from tbThirdPartyItemInfo where vCategoryType like 'Ink' ";
			
			List list = session.createSQLQuery(sql).list();

			for(Iterator iter = list.iterator(); iter.hasNext();)
			{
				Object[] element = (Object[]) iter.next();

				cmbInk.get(ar).addItem(element[0].toString());
				cmbInk.get(ar).setItemCaption(element[0].toString(), element[1].toString());
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
	}
	private boolean doubleEntryCheck(String id,int ar,String head){

		if(head.equalsIgnoreCase("raw")){
			for(int a=0;a<cmbRm.size();a++)
			{
				if(cmbRm.get(a).getValue()!=null){
					if(a!=ar&&id.equalsIgnoreCase(cmbRm.get(a).getValue().toString())){

						return true;
					}
				}
			}
		}
		if(head.equalsIgnoreCase("packing"))
		{
			for(int a=0;a<cmbPack.size();a++)
			{
				if(cmbPack.get(a).getValue()!=null){
					if(a!=ar&&id.equalsIgnoreCase(cmbPack.get(a).getValue().toString())){

						return true;
					}
				}
			}
		}
		if(head.equalsIgnoreCase("ink"))
		{
			for(int a=0;a<cmbInk.size();a++)
			{
				if(cmbInk.get(a).getValue()!=null){
					if(a!=ar&&id.equalsIgnoreCase(cmbInk.get(a).getValue().toString())){

						return true;
					}
				}
			}
		}
		return false;
	}
	private void productCmbChange(String id, int ar, String head) {

		if(head.equalsIgnoreCase("Raw"))
		{
			txtUnit.get(ar).setValue(hmUnit.get(id));
			txtRate.get(ar).setValue(df.format(hmRate.get(id)));
			amtPercent.get(ar).focus();

		}
		else if(head.equalsIgnoreCase("Packing"))
		{
			txtUnit1.get(ar).setValue(hmUnit.get(id));
			txtRate1.get(ar).setValue(df.format(hmRate.get(id)));
			amtQty1.get(ar).focus();
			for(int a=0;a<amtQty.size();a++){

			}
		}
		else if(head.equalsIgnoreCase("Ink"))
		{
			txtUnit2.get(ar).setValue(hmUnit.get(id));
			txtRate2.get(ar).setValue(df.format(hmRate.get(id)));
			amtQty2.get(ar).focus();
			for(int a=0;a<amtQty.size();a++){

			}
		}
	}

	private void amountCalc(int ar, String head) {

		double qty,rate,amount,totalAmount=0.0;
		if(head.equalsIgnoreCase("raw"))
		{
			qty=Double.parseDouble(amtQty.get(ar).getValue().toString().isEmpty()?"0.0":amtQty.get(ar).getValue().toString());
			rate=Double.parseDouble(txtRate.get(ar).getValue().toString().isEmpty()?"0.0":txtRate.get(ar).getValue().toString());
			amtAmount.get(ar).setValue(df.format(qty*rate));

			for(int a=0;a<amtQty.size();a++)
			{
				if(!amtAmount.get(a).getValue().toString().isEmpty())
				{
					totalAmount=totalAmount+Double.parseDouble(amtAmount.get(a).getValue().toString());
					tableRm.setColumnFooter("Amount","Total: "+totalAmount);
					//txtRm.setValue(totalAmount);
					lblRaw.setValue(totalAmount);
				}

			}




		}
		else if(head.equalsIgnoreCase("packing"))
		{
			qty=Double.parseDouble(amtQty1.get(ar).getValue().toString().isEmpty()?"0.0":amtQty1.get(ar).getValue().toString());
			rate=Double.parseDouble(txtRate1.get(ar).getValue().toString().isEmpty()?"0.0":txtRate1.get(ar).getValue().toString());
			amtAmount1.get(ar).setValue(df.format(qty*rate));
			for(int a=0;a<amtQty1.size();a++)
			{
				if(!amtAmount1.get(a).getValue().toString().isEmpty())
				{
					totalAmount=totalAmount+Double.parseDouble(amtAmount1.get(a).getValue().toString());
					tablePack.setColumnFooter("Amount","Total: "+totalAmount);
					lblPack.setValue(totalAmount);
				}
			}

		}
		else if(head.equalsIgnoreCase("ink"))
		{
			qty=Double.parseDouble(amtQty2.get(ar).getValue().toString().isEmpty()?"0.0":amtQty2.get(ar).getValue().toString());
			rate=Double.parseDouble(txtRate2.get(ar).getValue().toString().isEmpty()?"0.0":txtRate2.get(ar).getValue().toString());
			amtAmount2.get(ar).setValue(df.format(qty*rate));
			for(int a=0;a<amtQty2.size();a++)
			{
				if(!amtAmount2.get(a).getValue().toString().isEmpty())
				{
					totalAmount=totalAmount+Double.parseDouble(amtAmount2.get(a).getValue().toString());
					tableInk.setColumnFooter("Amount","Total: "+totalAmount);
					lblInk.setValue(totalAmount);
				}
			}

		}
	}
	private void amountQtyCalc(int ar){
		double stdWeight=0.0,percent,qty;
		if(!txtWeight.getValue().toString().isEmpty()){
			stdWeight=Double.parseDouble(txtWeight.getValue().toString())/1000;
		}
		percent=Double.parseDouble(amtPercent.get(ar).getValue().toString().isEmpty()?"0.0":amtPercent.get(ar).getValue().toString());
		qty=(stdWeight*percent)/100;
		amtQty.get(ar).setValue(df.format(qty));
	}
	private void tableRowAddRaw(final int ar){

		lblSl.add(ar, new Label());
		lblSl.get(ar).setValue(ar+1);
		lblSl.get(ar).setWidth("100%");

		cmbRm.add(ar, new ComboBox());
		cmbRm.get(ar).setNullSelectionAllowed(true);
		cmbRm.get(ar).setImmediate(true);
		cmbRm.get(ar).setWidth("100%");
		cmbRm.get(ar).setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
		cmbRmLoadData(ar);

		cmbRm.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbRm.get(ar).getValue()!=null){
					if(!doubleEntryCheck(cmbRm.get(ar).getValue().toString(),ar,"Raw")){
						productCmbChange(cmbRm.get(ar).getValue().toString(),ar,"Raw");
					}
					else{

						showNotification("Double Entry");
						cmbRm.get(ar).setValue(null);
						cmbRm.get(ar).focus();
					}
				}
				else{
					txtUnit.get(ar).setValue("");
					amtQty.get(ar).setValue("");
					txtRate.get(ar).setValue("");
					amtAmount.get(ar).setValue("");
				}
			}


		});


		txtUnit.add(ar, new TextRead(1));
		txtUnit.get(ar).setImmediate(true);
		txtUnit.get(ar).setWidth("100%");

		amtPercent.add(ar,new AmountField());
		amtPercent.get(ar).setImmediate(true);
		amtPercent.get(ar).setWidth("100%");

		amtPercent.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				double percent=0;
				for(int ar=0;ar<amtPercent.size();ar++){
					if(!amtPercent.get(ar).getValue().toString().isEmpty()){
						percent=percent+Double.parseDouble(amtPercent.get(ar).getValue().toString());
					}
				}
				if(percent<=100){
					amountQtyCalc(ar);
				}
				else{

					showNotification("Sorry!!","Percentage Can't Exceed 100",Notification.TYPE_WARNING_MESSAGE);
					amtPercent.get(ar).setValue("");
					amtQty.get(ar).setValue("");
					amtAmount.get(ar).setValue("");
					amtPercent.get(ar).focus();
				}

			}
		});

		amtQty.add(ar,new AmountField());
		amtQty.get(ar).setImmediate(true);
		amtQty.get(ar).setWidth("100%");

		amtQty.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				amountCalc(ar,"Raw");
				double totalAmount=0.0;

			}
		});


		txtRate.add(ar, new AmountField());
		txtRate.get(ar).setImmediate(true);
		txtRate.get(ar).setWidth("100%");

		amtAmount.add(ar, new AmountCommaSeperator());
		amtAmount.get(ar).setImmediate(true);
		amtAmount.get(ar).setWidth("100%");

		tableRm.addItem(new Object[]{lblSl.get(ar),cmbRm.get(ar),txtUnit.get(ar),amtPercent.get(ar),amtQty.get(ar),txtRate.get(ar),amtAmount.get(ar)},ar);
	}
	private void tableRowAddPack(final int ar){

		lblSl1.add(ar, new Label());
		lblSl1.get(ar).setValue(ar+1);
		lblSl1.get(ar).setWidth("100%");

		cmbPack.add(ar, new ComboBox());
		cmbPack.get(ar).setNullSelectionAllowed(true);
		cmbPack.get(ar).setImmediate(true);
		cmbPack.get(ar).setWidth("100%");
		cmbPackLoadData(ar);

		cmbPack.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbPack.get(ar).getValue()!=null){
					if(!doubleEntryCheck(cmbPack.get(ar).getValue().toString(),ar,"packing")){
						productCmbChange(cmbPack.get(ar).getValue().toString(),ar,"Packing");
					}
					else{

						showNotification("Double Entry");
						cmbPack.get(ar).setValue(null);
						cmbPack.get(ar).focus();
					}
				}
				else{
					txtUnit1.get(ar).setValue("");
					amtQty1.get(ar).setValue("");
					txtRate1.get(ar).setValue("");
					amtAmount1.get(ar).setValue("");
				}
			}
		});

		txtUnit1.add(ar, new TextRead(1));
		txtUnit1.get(ar).setImmediate(true);
		txtUnit1.get(ar).setWidth("100%");

		amtQty1.add(ar,new AmountField());
		amtQty1.get(ar).setImmediate(true);
		amtQty1.get(ar).setWidth("100%");

		amtQty1.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				amountCalc(ar,"packing");
			}
		});

		txtRate1.add(ar,  new AmountField());
		txtRate1.get(ar).setImmediate(true);
		txtRate1.get(ar).setWidth("100%");

		amtAmount1.add(ar, new AmountCommaSeperator());
		amtAmount1.get(ar).setImmediate(true);
		amtAmount1.get(ar).setWidth("100%");

		tablePack.addItem(new Object[]{lblSl1.get(ar),cmbPack.get(ar),txtUnit1.get(ar),amtQty1.get(ar),txtRate1.get(ar),amtAmount1.get(ar)},ar);
	}
	private void tableRowAddInk(final int ar){

		lblSl2.add(ar, new Label());
		lblSl2.get(ar).setValue(ar+1);
		lblSl2.get(ar).setWidth("100%");

		cmbInk.add(ar, new ComboBox());
		cmbInk.get(ar).setNullSelectionAllowed(true);
		cmbInk.get(ar).setImmediate(true);
		cmbInk.get(ar).setWidth("100%");
		cmbInkLoadData(ar);

		cmbInk.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {

				if(cmbInk.get(ar).getValue()!=null){
					if(!doubleEntryCheck(cmbInk.get(ar).getValue().toString(),ar,"ink")){
						productCmbChange(cmbInk.get(ar).getValue().toString(),ar,"ink");
					}
					else{

						showNotification("Double Entry");
						cmbInk.get(ar).setValue(null);
						cmbInk.get(ar).focus();
					}
				}
				else{
					txtUnit2.get(ar).setValue("");
					amtQty2.get(ar).setValue("");
					txtRate2.get(ar).setValue("");
					amtAmount2.get(ar).setValue("");
				}
			}
		});

		txtUnit2.add(ar, new TextRead(1));
		txtUnit2.get(ar).setImmediate(true);
		txtUnit2.get(ar).setWidth("100%");

		amtkgpcs.add(ar,new AmountField());
		amtkgpcs.get(ar).setImmediate(true);
		amtkgpcs.get(ar).setWidth("100%");
		amtkgpcs.get(ar).addListener(new ValueChangeListener() 
		{

			public void valueChange(ValueChangeEvent event) 
			{
				if(cmbInk.get(ar).getValue()!=null && !amtkgpcs.get(ar).getValue().toString().isEmpty())
				{
					double pcskg=0.00;
					double qty=0.00;
					pcskg=Double.parseDouble(amtkgpcs.get(ar).getValue().toString()) ;
					qty= 1/pcskg ;
					amtQty2.get(ar).setValue(df.format(qty));
				}	
			}
		});

		amtQty2.add(ar,new AmountField());
		amtQty2.get(ar).setImmediate(true);
		amtQty2.get(ar).setWidth("100%");

		amtQty2.get(ar).addListener(new ValueChangeListener() {

			public void valueChange(ValueChangeEvent event) {
				amountCalc(ar,"ink");
			}
		});

		txtRate2.add(ar,  new AmountField());
		txtRate2.get(ar).setImmediate(true);
		txtRate2.get(ar).setWidth("100%");

		amtAmount2.add(ar, new AmountCommaSeperator());
		amtAmount2.get(ar).setImmediate(true);
		amtAmount2.get(ar).setWidth("100%");

		tableInk.addItem(new Object[]{lblSl2.get(ar),cmbInk.get(ar),txtUnit2.get(ar),amtkgpcs.get(ar) ,amtQty2.get(ar),txtRate2.get(ar),amtAmount2.get(ar)},ar);
	}
	private void tableInitRaw(){
		for(int a=0;a<5;a++){
			tableRowAddRaw(a);
		}
	}
	private void tableInitPack(){
		for(int a=0;a<5;a++){
			tableRowAddPack(a);
		}
	}
	private void tableInitInk(){
		for(int a=0;a<5;a++){
			tableRowAddInk(a);
		}
	}
}
