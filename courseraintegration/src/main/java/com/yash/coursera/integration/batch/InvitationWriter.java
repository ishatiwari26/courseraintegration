package com.yash.coursera.integration.batch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;

import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;

public class InvitationWriter implements ItemWriter<Elements> {

	private String fileName;

	private static final String[] HEADERS = { "id", "fullName", "externalId", "email", "programId" };
	private String outputFilename;
	private Workbook workbook;
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

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Calling beforeStep");
		System.out.println("Calling beforeStep");
		jobExecution = stepExecution.getJobExecution();
		fileName = jobExecution.getJobParameters().getString("fileName");
		outputFilename = fileName;

		workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet("Testing");
		sheet.createFreezePane(0, 3, 0, 3);
		sheet.setDefaultColumnWidth(20);

		currRow++;
		addHeaders(sheet);

	}

	@Override
	public void write(List<? extends Elements> invites) throws Exception {

		System.out.println(invites.get(0).getElement());
		System.out.println("dd");

		List<Element> elements = invites.get(0).getElement();

		Sheet sheet = workbook.getSheetAt(0);

		for (Element element : elements) {

			currRow++;
			Row row = sheet.createRow(currRow);

			createStringCell(row, element.getId(), 0);
			createStringCell(row, element.getFullName(), 1);
			createStringCell(row, element.getExternalId(), 2);
			createStringCell(row, element.getEmail(), 3);
			createStringCell(row, element.getProgramId(), 4);
		}
	}

	@AfterStep
	public void afterStep(StepExecution stepExecution) throws IOException {
		FileOutputStream fos = new FileOutputStream(outputFilename);
		workbook.write(fos);
		fos.close();
	}

	private void createStringCell(Row row, String val, int col) {
		Cell cell = row.createCell(col);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(val);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
