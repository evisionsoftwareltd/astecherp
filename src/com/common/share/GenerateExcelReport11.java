package com.common.share;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.hibernate.Session;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Window;

public class GenerateExcelReport11
{

	SessionBean sessionBean;
	LinkedHashMap<String, Object> rowMap=new LinkedHashMap<String, Object>();
	int i=0;
	int rownum=0;
	private SimpleDateFormat dFormat=new SimpleDateFormat("dd-MM-yy");
	private String loc,url,fname,sheetName;

	public GenerateExcelReport11(SessionBean sessionbean, String loc, String url, String fname, String sheetName,
			String ReportName, String Header, String [] TableHeader, int GroupHeaderRowHeight,String [] GroupHeader, 
			Object [] GroupColName, Object [][] Group_Element, int DetailRowHeight,String [] DetailQuery, 
			int totalStart, int totalEnd,String StrPaperSize,String StrPrintSetUp, String [] SignatureOption)
	{
		this.sessionBean=sessionbean;
		this.loc=loc;
		this.url=url;
		this.fname=fname;
		this.sheetName=sheetName;
		generateExcelFile(ReportName, Header, TableHeader, GroupHeaderRowHeight,GroupHeader, GroupColName, Group_Element, 
				DetailRowHeight,DetailQuery,totalStart,totalEnd,StrPaperSize,StrPrintSetUp,SignatureOption);
	}
	public GenerateExcelReport11(SessionBean sessionbean, String loc, String url, String fname, String sheetName,
			String ReportName, String Header, String [] TableHeader, int GroupHeaderRowHeight,String [] GroupHeader, 
			Object [] GroupColName, Object [][] Group_Element, int DetailRowHeight,List <?> DetailQuery, 
			int totalStart, int totalEnd,String StrPaperSize,String StrPrintSetUp, String [] SignatureOption)
	{
		this.sessionBean=sessionbean;
		this.loc=loc;
		this.url=url;
		this.fname=fname;
		this.sheetName=sheetName;
		generateExcelFileForTrailBalance(ReportName, Header, TableHeader, GroupHeaderRowHeight,GroupHeader, GroupColName, Group_Element, 
				DetailRowHeight,DetailQuery,totalStart,totalEnd,StrPaperSize,StrPrintSetUp,SignatureOption);
	}
	public void generateExcelFile(String ReportName, String Header, String [] TableHeader, int GroupHeaderRowHeight,String [] GroupHeader,
			Object [] GroupColName, Object [][] Group_Element, int DetailRowHeight, String [] DetailQuery, int totalStart,
			int totalEnd,String StrPaperSize,String StrPrintSetUp, String [] SignatureOption)
	{
		System.out.println("GenerateExcelReport first Step");

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet spreadsheet = workbook.createSheet(sheetName);
		HSSFDataFormat dataFormat=workbook.createDataFormat();
		PrintSetup printSetup=spreadsheet.getPrintSetup();
		spreadsheet.setAutobreaks(false);
		spreadsheet.setFitToPage(true);
		spreadsheet.setZoom(100);
		if(StrPaperSize.equalsIgnoreCase("A4"))
			printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		else if(StrPaperSize.equalsIgnoreCase("Legal"))
			printSetup.setPaperSize(HSSFPrintSetup.LEGAL_PAPERSIZE);
		
		if(StrPrintSetUp.equalsIgnoreCase("LandScape"))
			printSetup.setLandscape(true);
		else if(StrPrintSetUp.equalsIgnoreCase("Portrait"))
			printSetup.setLandscape(false);
		

		System.out.println("GenerateExcelReport Second Step");

		HSSFFont font=workbook.createFont();
		int ColLength=TableHeader.length;
		font.setFontHeightInPoints((short)13);
		font.setFontName("Arial");
		font.setBold(true);

		Row row=spreadsheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		Cell cellCompany=row.createCell(0);
		cellCompany.setCellValue(sessionBean.getCompany());
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellCompany.setCellStyle(style);
		spreadsheet.addMergedRegion(new CellRangeAddress(0,1,0,TableHeader.length-1));

		System.out.println("GenerateExcelReport Third Step");

		row=spreadsheet.createRow(2);
		CellStyle style1 = workbook.createCellStyle();
		font=workbook.createFont();
		font.setFontHeightInPoints((short)10.0);
		font.setFontName("Arial");
		font.setBold(true);
		Cell cellAddress=row.createCell(0);
		cellAddress.setCellValue(sessionBean.getCompanyAddress());
		style1.setFont(font);
		style1.setAlignment(CellStyle.ALIGN_CENTER);
		style1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellAddress.setCellStyle(style1);
		spreadsheet.addMergedRegion(new CellRangeAddress(2,2,0,TableHeader.length-1));

		System.out.println("GenerateExcelReport Fourth Step");

		row=spreadsheet.createRow(3);
		CellStyle style2 = workbook.createCellStyle();
		font=workbook.createFont();
		font.setFontHeightInPoints((short)10.0);
		font.setFontName("Arial");
		font.setBold(true);
		Cell cellContact=row.createCell(0);
		cellContact.setCellValue(sessionBean.getCompanyContact());
		style2.setFont(font);
		style2.setAlignment(CellStyle.ALIGN_CENTER);
		style2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellContact.setCellStyle(style2);
		spreadsheet.addMergedRegion(new CellRangeAddress(3,3,0,TableHeader.length-1));

		System.out.println("GenerateExcelReport Fifth Step");

		row=spreadsheet.createRow(5);
		CellStyle style8 = workbook.createCellStyle();
		font=workbook.createFont();
		font.setFontHeightInPoints((short)11.0);
		font.setFontName("Arial");
		font.setBold(true);
		Cell cellReportName=row.createCell(0);
		cellReportName.setCellValue(ReportName);
		style8.setFont(font);
		style8.setAlignment(CellStyle.ALIGN_CENTER);
		style8.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellReportName.setCellStyle(style8);
		spreadsheet.addMergedRegion(new CellRangeAddress(5,5,0,TableHeader.length-1));
		
		row=spreadsheet.createRow(6);
		if(Header.length()>0)
		{
			CellStyle style6 = workbook.createCellStyle();
			font=workbook.createFont();
			font.setFontHeightInPoints((short)10.0);
			font.setFontName("Arial");
			font.setBold(true);
			Cell cellHeader=row.createCell(0);
			cellHeader.setCellValue(Header);
			style6.setFont(font);
			style6.setAlignment(CellStyle.ALIGN_CENTER);
			style6.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			style6.setFont(font);
			cellHeader.setCellStyle(style6);
			spreadsheet.addMergedRegion(new CellRangeAddress(6,6,0,TableHeader.length-1));
		}

		System.out.println("GenerateExcelReport Sixth Step");
		
		System.out.println("GenerateExcelReport 6.1 ");
		
		CellStyle style3 = workbook.createCellStyle();
		if(TableHeader.length>0)
		{
			row=spreadsheet.createRow(8);
			for(int colInd=0;colInd<ColLength;colInd++)
			{
				if(TableHeader[colInd].length()>0)
				{
					System.out.println("GenerateExcelReport 6.2 ");
					Cell cell=row.createCell(colInd);
					cell.setCellValue(TableHeader[colInd]);
					style3.setBorderBottom(CellStyle.BORDER_THIN);
					style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					style3.setBorderLeft(CellStyle.BORDER_THIN);
					style3.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					style3.setBorderRight(CellStyle.BORDER_THIN);
					style3.setRightBorderColor(IndexedColors.BLACK.getIndex());
					style3.setBorderTop(CellStyle.BORDER_THIN);
					style3.setTopBorderColor(IndexedColors.BLACK.getIndex());
					style3.setAlignment(CellStyle.ALIGN_CENTER);
					style3.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
					style3.setFont(font);
					cell.setCellStyle(style3);
					spreadsheet.autoSizeColumn(colInd);
				}
			}
		}
		System.out.println("GenerateExcelReport 6.3 ");

		System.out.println("GenerateExcelReport Seventh Step");

		//row=spreadsheet.createRow(8);
		CellStyle style7 = workbook.createCellStyle();
		font=workbook.createFont();
		font.setFontHeightInPoints((short)10.0);
		font.setFontName("Arial");
		font.setBold(true);

		System.out.println("GenerateExcelReport 7.1 ");
		
		i=1;
		rownum = 9;
		CellStyle style4=workbook.createCellStyle();
		style4.setWrapText(true);
		style4.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style4.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

		if(GroupHeader.length>0)
		{
			for(int ind=0;ind<GroupHeader.length;ind++)
			{
				System.out.println("GenerateExcelReport 7.2 ");
				rowMap.put("#"+Integer.toString(i),Group_Element);
				row=spreadsheet.createRow(rownum);
				font=workbook.createFont();
				font.setFontHeightInPoints((short)10.0);
				font.setFontName("Arial");
				font.setBold(true);
				Cell cellEmpty1=row.createCell(0);
				cellEmpty1.setCellValue("");
				style7.setFont(font);
				style7.setAlignment(CellStyle.ALIGN_CENTER);
				style7.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				cellEmpty1.setCellStyle(style7);
				spreadsheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,TableHeader.length-1));
				rownum++;
				i++;

				System.out.println("GenerateExcelReport 7.3 ");
				
				rowMap.put("#"+Integer.toString(i),Group_Element);//"Group Header"+Integer.toString(i),Group_Element);
				row=spreadsheet.createRow(rownum);
				font=workbook.createFont();
				font.setFontHeightInPoints((short)10.0);
				font.setFontName("Arial");
				font.setBold(true);
				Cell cellGroup=row.createCell(0);
				cellGroup.setCellValue(GroupHeader[ind]);
				style4.setBorderBottom(CellStyle.BORDER_THIN);
				style4.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style4.setBorderLeft(CellStyle.BORDER_THIN);
				style4.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				style4.setBorderRight(CellStyle.BORDER_THIN);
				style4.setRightBorderColor(IndexedColors.BLACK.getIndex());
				style4.setBorderTop(CellStyle.BORDER_THIN);
				style4.setTopBorderColor(IndexedColors.BLACK.getIndex());
				style4.setAlignment(CellStyle.ALIGN_LEFT);
				style4.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				style4.setFont(font);
				cellGroup.setCellStyle(style4);
				spreadsheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,TableHeader.length-1));
				row.setHeightInPoints((GroupHeaderRowHeight*spreadsheet.getDefaultRowHeightInPoints()));
				rownum++;
				i++;
				
				System.out.println("GenerateExcelReport 7.4 ");

				if(GroupColName.length>0)
				{
					row=spreadsheet.createRow(rownum);
					rowMap.put("#"+Integer.toString(i),GroupColName);
					for(int GroupInd=0;GroupInd<GroupColName.length;GroupInd++)
					{
						System.out.println("GenerateExcelReport 7.5 ");
						
						Cell cell=row.createCell(GroupInd);
						cell.setCellValue(GroupColName[GroupInd].toString());
						style3.setBorderBottom(CellStyle.BORDER_THIN);
						style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());
						style3.setBorderLeft(CellStyle.BORDER_THIN);
						style3.setLeftBorderColor(IndexedColors.BLACK.getIndex());
						style3.setBorderRight(CellStyle.BORDER_THIN);
						style3.setRightBorderColor(IndexedColors.BLACK.getIndex());
						style3.setBorderTop(CellStyle.BORDER_THIN);
						style3.setTopBorderColor(IndexedColors.BLACK.getIndex());
						style3.setAlignment(CellStyle.ALIGN_CENTER);
						style3.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
						style3.setFont(font);
						cell.setCellStyle(style3);
						spreadsheet.autoSizeColumn(GroupInd);
					}
					rownum++;
					i++;
					
					System.out.println("GenerateExcelReport 7.6 ");
					
				}
				excelReport(DetailQuery[ind]);
			}
			
			System.out.println("GenerateExcelReport 7.7 ");
		}
		else
		{
			excelReport(DetailQuery[0]);
		}

		System.out.println("GenerateExcelReport Eighth Step");

		rownum = 9;
		Set<String> keySet=rowMap.keySet();
		CellStyle style5=workbook.createCellStyle();
		style5.setWrapText(true);
		int cellIndex = 0;
		for (String key : keySet) 
		{
			if(!key.contains("#"))
			{
				System.out.println("GenerateExcelReport 8.1 ");
				
				row = spreadsheet.createRow(rownum);
				//System.out.println("Hello Shoumen : "+rowMap.get(key));
				Object [] objArr = (Object[])rowMap.get(key);
				int cellnum = 0;
				for (Object obj : objArr) 
				{
					System.out.println("GenerateExcelReport 8.2 ");
					
					Cell cell = row.createCell(cellnum);
					if(obj instanceof Date)
						cell.setCellValue(dFormat.format(obj));
					else if(obj instanceof Boolean)
						cell.setCellValue((Boolean)obj);
					else if(obj instanceof String)
						cell.setCellValue((String)obj);
					else if(obj instanceof Double)
					{
						cell.setCellValue((Double)obj);
						style5.setDataFormat(dataFormat.getFormat("#,##0"));
					}
					else if(obj instanceof Integer)
						cell.setCellValue((Integer)obj);

					System.out.println("GenerateExcelReport 8.3 ");
					
					style5.setBorderBottom(CellStyle.BORDER_THIN);
					style5.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					style5.setBorderLeft(CellStyle.BORDER_THIN);
					style5.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					style5.setBorderRight(CellStyle.BORDER_THIN);
					style5.setRightBorderColor(IndexedColors.BLACK.getIndex());
					style5.setBorderTop(CellStyle.BORDER_THIN);
					style5.setTopBorderColor(IndexedColors.BLACK.getIndex());
					style5.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
					if((totalStart!=0 && totalEnd!=0) && (cellnum>=totalStart && cellnum<=totalEnd))
					{
						style5.setDataFormat(dataFormat.getFormat("#,##0"));
					}
					cell.setCellStyle(style5);
					spreadsheet.autoSizeColumn(cellnum);
					cellnum++;
					System.out.println("GenerateExcelReport 8.4 ");
				}
				row.setHeightInPoints((DetailRowHeight*spreadsheet.getDefaultRowHeightInPoints()));
				cellIndex = cellnum;
			}
			rownum++;
		}
		rowMap.clear();
		System.out.println("GenerateExcelReport Ninth Step");
		if(totalStart!=0 && totalEnd!=0)
		{
			System.out.println("GenerateExcelReport 9.1 ");
			
			CellStyle style9=workbook.createCellStyle();
			row=spreadsheet.createRow(rownum);
			Cell cell=row.createCell(totalStart-1);
			cell.setCellValue("Total : ");
			style9.setAlignment(CellStyle.ALIGN_RIGHT);
			style9.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			style9.setFont(font);
			cell.setCellStyle(style9);
			spreadsheet.autoSizeColumn(totalStart-1);
			CellStyle style10=workbook.createCellStyle();
			for(int colInd=totalStart;colInd<=totalEnd;colInd++)
			{
				System.out.println("GenerateExcelReport 9.2 ");
				
				cell=row.createCell(colInd);
				cell.setCellFormula("SUM("+Character.toString((char)(65+colInd))+"10:"+Character.toString((char)(65+colInd))+Integer.toString(rownum)+")");
				style10.setDataFormat(dataFormat.getFormat("#,##0"));
				style10.setBorderBottom(CellStyle.BORDER_DOUBLE);
				style10.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style10.setAlignment(CellStyle.ALIGN_RIGHT);
				style10.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				style10.setFont(font);
				cell.setCellStyle(style10);
				spreadsheet.autoSizeColumn(colInd);
			}
		}

		System.out.println("GenerateExcelReport 9.3 ");
		
		if(SignatureOption.length>0)
		{
			CellStyle style11=workbook.createCellStyle();
			int startCol = 2;
			row=spreadsheet.createRow(rownum+6);
			Cell cell=row.createCell(startCol);
			cell.setCellValue("Total : ");
			style11.setAlignment(CellStyle.ALIGN_RIGHT);
			style11.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			style11.setFont(font);
			cell.setCellStyle(style11);
			CellStyle style12=workbook.createCellStyle();
			for(int colInd=0;colInd<SignatureOption.length;colInd++)
			{
				System.out.println("GenerateExcelReport 9.4 ");
				
				cell=row.createCell(startCol);
				cell.setCellValue(SignatureOption[colInd]);
				style12.setBorderTop(CellStyle.BORDER_THIN);
				style12.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style12.setAlignment(CellStyle.ALIGN_RIGHT);
				style12.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				style12.setFont(font);
				cell.setCellStyle(style12);
				startCol+=Math.ceil(cellIndex/SignatureOption.length);
			}
		}
		
		System.out.println("GenerateExcelReport Tenth Step");
		try
		{
			
			File file=new File(loc+"/"+fname);
			FileOutputStream out=new FileOutputStream(file);
			workbook.write(out);
			out.close();
			workbook.close();
			
			System.out.println("GenerateExcelReport 10.1 ");
		}
		catch (Exception exp)
		{
			System.out.println(exp.toString());
		}
		//return fname;
	}
	
	public void excelReport(String detailedQuery)
	{
		System.out.println("GenerateExcelReport 7.0.1 ");
		
		Session session=SessionFactoryUtil.getInstance().openSession();
		List lst=session.createSQLQuery(detailedQuery).list();

		int countSL=1;

		for(Iterator itr=lst.iterator();itr.hasNext();)
		{
			System.out.println("GenerateExcelReport 7.0.2 ");
			
			Object [] element=(Object[])itr.next();
			Object[] excelElement=new Object[element.length+1];
			excelElement[0]=(Object)countSL++;
			for(int elInd=1;elInd<=element.length;elInd++)
			{
				excelElement[elInd]=element[elInd-1];
			}
			rowMap.put(Integer.toString(i),excelElement);
			//System.out.println("excelReport : "+rowMap.keySet());
			rownum++;
			i++;
		}
		session.close();
	}
	
	public void generateExcelFileForTrailBalance(String ReportName, String Header, String [] TableHeader, int GroupHeaderRowHeight,String [] GroupHeader,
			Object [] GroupColName, Object [][] Group_Element, int DetailRowHeight, List<?> DetailQuery, int totalStart,
			int totalEnd,String StrPaperSize,String StrPrintSetUp, String [] SignatureOption)
	{
		//System.out.println("GenerateExcelReport first Step");

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet spreadsheet = workbook.createSheet(sheetName);
		HSSFDataFormat dataFormat=workbook.createDataFormat();
		PrintSetup printSetup=spreadsheet.getPrintSetup();
		spreadsheet.setAutobreaks(false);
		spreadsheet.setFitToPage(true);
		spreadsheet.setZoom(100);
		if(StrPaperSize.equalsIgnoreCase("A4"))
			printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
		else if(StrPaperSize.equalsIgnoreCase("Legal"))
			printSetup.setPaperSize(HSSFPrintSetup.LEGAL_PAPERSIZE);
		
		if(StrPrintSetUp.equalsIgnoreCase("LandScape"))
			printSetup.setLandscape(true);
		else if(StrPrintSetUp.equalsIgnoreCase("Portrait"))
			printSetup.setLandscape(false);
		

		//System.out.println("GenerateExcelReport Second Step");

		HSSFFont font=workbook.createFont();
		int ColLength=TableHeader.length;
		font.setFontHeightInPoints((short)13);
		font.setFontName("Arial");
		font.setBold(true);

		Row row=spreadsheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		Cell cellCompany=row.createCell(0);
		cellCompany.setCellValue(sessionBean.getCompany());
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellCompany.setCellStyle(style);
		spreadsheet.addMergedRegion(new CellRangeAddress(0,1,0,TableHeader.length-1));

		//System.out.println("GenerateExcelReport Third Step");

		row=spreadsheet.createRow(2);
		CellStyle style1 = workbook.createCellStyle();
		font=workbook.createFont();
		font.setFontHeightInPoints((short)10.0);
		font.setFontName("Arial");
		font.setBold(true);
		Cell cellAddress=row.createCell(0);
		cellAddress.setCellValue(sessionBean.getCompanyAddress());
		style1.setFont(font);
		style1.setAlignment(CellStyle.ALIGN_CENTER);
		style1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellAddress.setCellStyle(style1);
		spreadsheet.addMergedRegion(new CellRangeAddress(2,2,0,TableHeader.length-1));

		//System.out.println("GenerateExcelReport Fourth Step");

		row=spreadsheet.createRow(3);
		CellStyle style2 = workbook.createCellStyle();
		font=workbook.createFont();
		font.setFontHeightInPoints((short)10.0);
		font.setFontName("Arial");
		font.setBold(true);
		Cell cellContact=row.createCell(0);
		cellContact.setCellValue(sessionBean.getCompanyContact());
		style2.setFont(font);
		style2.setAlignment(CellStyle.ALIGN_CENTER);
		style2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellContact.setCellStyle(style2);
		spreadsheet.addMergedRegion(new CellRangeAddress(3,3,0,TableHeader.length-1));

		//System.out.println("GenerateExcelReport Fifth Step");

		row=spreadsheet.createRow(5);
		CellStyle style8 = workbook.createCellStyle();
		font=workbook.createFont();
		font.setFontHeightInPoints((short)11.0);
		font.setFontName("Arial");
		font.setBold(true);
		Cell cellReportName=row.createCell(0);
		cellReportName.setCellValue(ReportName);
		style8.setFont(font);
		style8.setAlignment(CellStyle.ALIGN_CENTER);
		style8.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellReportName.setCellStyle(style8);
		spreadsheet.addMergedRegion(new CellRangeAddress(5,5,0,TableHeader.length-1));
		
		row=spreadsheet.createRow(6);
		if(Header.length()>0)
		{
			CellStyle style6 = workbook.createCellStyle();
			font=workbook.createFont();
			font.setFontHeightInPoints((short)10.0);
			font.setFontName("Arial");
			font.setBold(true);
			Cell cellHeader=row.createCell(0);
			cellHeader.setCellValue(Header);
			style6.setFont(font);
			style6.setAlignment(CellStyle.ALIGN_CENTER);
			style6.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			style6.setFont(font);
			cellHeader.setCellStyle(style6);
			spreadsheet.addMergedRegion(new CellRangeAddress(6,6,0,TableHeader.length-1));
		}

		//System.out.println("GenerateExcelReport Sixth Step");
		
		//System.out.println("GenerateExcelReport 6.1 ");
		
		CellStyle style3 = workbook.createCellStyle();
		if(TableHeader.length>0)
		{
			row=spreadsheet.createRow(8);
			for(int colInd=0;colInd<ColLength;colInd++)
			{
				if(TableHeader[colInd].length()>0)
				{
					//System.out.println("GenerateExcelReport 6.2 ");
					Cell cell=row.createCell(colInd);
					cell.setCellValue(TableHeader[colInd]);
					style3.setBorderBottom(CellStyle.BORDER_THIN);
					style3.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					style3.setBorderLeft(CellStyle.BORDER_THIN);
					style3.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					style3.setBorderRight(CellStyle.BORDER_THIN);
					style3.setRightBorderColor(IndexedColors.BLACK.getIndex());
					style3.setBorderTop(CellStyle.BORDER_THIN);
					style3.setTopBorderColor(IndexedColors.BLACK.getIndex());
					style3.setAlignment(CellStyle.ALIGN_CENTER);
					style3.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
					style3.setFont(font);
					cell.setCellStyle(style3);
					spreadsheet.autoSizeColumn(colInd);
				}
			}
		}
		//System.out.println("GenerateExcelReport 6.3 ");

		//System.out.println("GenerateExcelReport Seventh Step");

		//row=spreadsheet.createRow(8);
		CellStyle style7 = workbook.createCellStyle();
		font=workbook.createFont();
		font.setFontHeightInPoints((short)10.0);
		font.setFontName("Arial");
		font.setBold(true);

		//System.out.println("GenerateExcelReport 7.1 ");
		
		i=1;
		rownum = 9;
		CellStyle style4=workbook.createCellStyle();
		style4.setWrapText(true);
		style4.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style4.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

		String GroupName = "";
		int cellIndex = 0;
		int countSL=1;
		CellStyle style5=workbook.createCellStyle();
		style5.setWrapText(true);
		style5.setDataFormat(dataFormat.getFormat("#,##0_);(#,##0)"));
		for(Iterator<?> iter=DetailQuery.iterator(); iter.hasNext();)
		{
				Object [] element=(Object[])iter.next();
				if(!((String)element[0]).equals(GroupName)){
					GroupName = (String) element[0];
					countSL=1;
					System.out.println("GenerateExcelReport 7.2 ");
					
					row=spreadsheet.createRow(rownum);
					Cell cellEmpty1=row.createCell(0);
					cellEmpty1.setCellValue("");
					cellEmpty1.setCellStyle(style7);
					spreadsheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,TableHeader.length-1));
					rownum++;
					i++;
	
					//System.out.println("GenerateExcelReport 7.3 ");
					
					row=spreadsheet.createRow(rownum);
					Cell cellGroup=row.createCell(0);
					cellGroup.setCellValue(GroupName);
					cellGroup.setCellStyle(style4);
					spreadsheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,TableHeader.length-1));
					
					rownum++;
					i++;
				}
				
				//System.out.println("GenerateExcelReport 7.4 ");

				//System.out.println("Hello Asad : "+rowMap.get(key));
				
				row = spreadsheet.createRow(rownum);
				element[0] = (Object)countSL++;
				Cell cell0 = row.createCell(0);
				cell0.setCellValue((int)element[0]);
				Cell cell1 = row.createCell(1);
				cell1.setCellValue((String)element[1]);
				Cell cell2 = row.createCell(2);
				cell2.setCellValue((Double)element[2]);
				cell2.setCellStyle(style5);
				Cell cell3 = row.createCell(3);
				cell3.setCellValue((Double)element[3]);
				cell3.setCellStyle(style5);
				rownum++;
				
		}
			
		//System.out.println("GenerateExcelReport Ninth Step");
		if(totalStart!=0 && totalEnd!=0)
		{
			//System.out.println("GenerateExcelReport 9.1 ");
			
			CellStyle style9=workbook.createCellStyle();
			row=spreadsheet.createRow(rownum);
			Cell cell=row.createCell(totalStart-1);
			cell.setCellValue("Total : ");
			style9.setAlignment(CellStyle.ALIGN_RIGHT);
			style9.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			style9.setFont(font);
			cell.setCellStyle(style9);
			spreadsheet.autoSizeColumn(totalStart-1);
			CellStyle style10=workbook.createCellStyle();
			for(int colInd=totalStart;colInd<=totalEnd;colInd++)
			{
				//System.out.println("GenerateExcelReport 9.2 ");
				
				cell=row.createCell(colInd);
				cell.setCellFormula("SUM("+Character.toString((char)(65+colInd))+"10:"+Character.toString((char)(65+colInd))+Integer.toString(rownum)+")");
				style10.setDataFormat(dataFormat.getFormat("#,##0"));
				style10.setBorderBottom(CellStyle.BORDER_DOUBLE);
				style10.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style10.setAlignment(CellStyle.ALIGN_RIGHT);
				style10.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				style10.setFont(font);
				cell.setCellStyle(style10);
				spreadsheet.autoSizeColumn(colInd);
			}
		}

		System.out.println("GenerateExcelReport 9.3 ");
		
		if(SignatureOption.length>0)
		{
			CellStyle style11=workbook.createCellStyle();
			int startCol = 2;
			row=spreadsheet.createRow(rownum+6);
			Cell cell=row.createCell(startCol);
			cell.setCellValue("Total : ");
			style11.setAlignment(CellStyle.ALIGN_RIGHT);
			style11.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			style11.setFont(font);
			cell.setCellStyle(style11);
			CellStyle style12=workbook.createCellStyle();
			for(int colInd=0;colInd<SignatureOption.length;colInd++)
			{
				//System.out.println("GenerateExcelReport 9.4 ");
				
				cell=row.createCell(startCol);
				cell.setCellValue(SignatureOption[colInd]);
				style12.setBorderTop(CellStyle.BORDER_THIN);
				style12.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style12.setAlignment(CellStyle.ALIGN_RIGHT);
				style12.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				style12.setFont(font);
				cell.setCellStyle(style12);
				startCol+=Math.ceil(cellIndex/SignatureOption.length);
			}
		}
		
		//System.out.println("GenerateExcelReport Tenth Step");
		try
		{
			
			File file=new File(loc+"/"+fname);
			FileOutputStream out=new FileOutputStream(file);
			workbook.write(out);
			out.close();
			workbook.close();
			
			//System.out.println("GenerateExcelReport 10.1 ");
		}
		catch (Exception exp)
		{
			System.out.println(exp.toString());
		}
		//return fname;
	}
}
