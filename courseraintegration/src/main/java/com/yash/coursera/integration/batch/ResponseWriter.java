package com.yash.coursera.integration.batch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.yash.coursera.integration.model.SFLmsMapper;

@Component
public class ResponseWriter implements ItemWriter<List<SFLmsMapper>> {

	private String fileName;
	private static final String[] HEADERS = { "courseID", "providerID", "status", "title", "description",
			"thumbnailURI", "launchURL", "contentTitle", "contentID" };

	private String outputFilename;
	private Workbook workbook;
	private CellStyle dataCellStyle;
	private int currRow = 0;
	private JobExecution jobExecution;

	private void addHeaders(Sheet sheet) {

		Workbook wb = sheet.getWorkbook();

		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();

		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);

		Row row = sheet.createRow(2);
		int col = 0;

		for (String header : HEADERS) {
			Cell cell = row.createCell(col);
			cell.setCellValue(header);
			cell.setCellStyle(style);
			col++;
		}
		currRow++;
	}

	private void addTitleToSheet(Sheet sheet) {

		Workbook wb = sheet.getWorkbook();

		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();

		font.setFontHeightInPoints((short) 14);
		font.setFontName("Arial");
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFont(font);

		Row row = sheet.createRow(currRow);
		row.setHeightInPoints(16);

		String currDate = DateFormatUtils.format(Calendar.getInstance(),
				DateFormatUtils.ISO_DATETIME_FORMAT.getPattern());

		Cell cell = row.createCell(0, Cell.CELL_TYPE_STRING);
		cell.setCellValue("Stock Data as of " + currDate);
		cell.setCellStyle(style);

		CellRangeAddress range = new CellRangeAddress(0, 0, 0, 7);
		sheet.addMergedRegion(range);
		currRow++;

	}

	@AfterStep
	public void afterStep(StepExecution stepExecution) throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFilename);
		workbook.write(fos);
		fos.close();
	}

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Calling beforeStep");
		jobExecution = stepExecution.getJobExecution();
		fileName = jobExecution.getJobParameters().getString("fileName");
		String dateTime = DateFormatUtils.format(Calendar.getInstance(), "yyyyMMdd_HHmmss");
		outputFilename = fileName;

		workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet("Testing");
		sheet.createFreezePane(0, 3, 0, 3);
		sheet.setDefaultColumnWidth(20);

		addTitleToSheet(sheet);
		currRow++;
		addHeaders(sheet);
		initDataStyle();

	}

	private void initDataStyle() {
		dataCellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();

		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
		dataCellStyle.setFont(font);
	}

	@Override
	public void write(List<? extends List<SFLmsMapper>> mappers) throws Exception {

		Sheet sheet = workbook.getSheetAt(0);

		System.out.print("size>>>" + mappers.size());

		List<SFLmsMapper> items = mappers.get(0);

		for (SFLmsMapper data : items) {

			currRow++;
			Row row = sheet.createRow(currRow);

			// "courseID", "providerID", "status", "title", "description", "thumbnailURI",
			// "launchURL",
			// "contentTitle", "contentID"
			createStringCell(row, data.getCourseID(), 0);
			createStringCell(row, data.getProviderID(), 1);
			createStringCell(row, data.getStatus(), 2);
			createStringCell(row, data.getTitle().getValue(), 3);
			createStringCell(row, data.getDescription().getValue(), 4);
			createStringCell(row, data.getThumbnailURI(), 5);
			createStringCell(row, data.getLaunchURL(), 6);
			createStringCell(row, data.getContentTitle(), 7);
			createStringCell(row, data.getContentID(), 8);

		}
	}

	private void createStringCell(Row row, String val, int col) {
		Cell cell = row.createCell(col);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(val);
	}

	private void createNumericCell(Row row, Double val, int col) {
		Cell cell = row.createCell(col);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(val);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
