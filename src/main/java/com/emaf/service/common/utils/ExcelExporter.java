package com.emaf.service.common.utils;

import com.emaf.service.model.ExcelColumnConfig;
import com.emaf.service.model.ParticipationData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExcelExporter {
    private String fileName;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private CreationHelper creationHelper;
    private List<ParticipationData> participationDataList;

    public ExcelExporter(final String fileName, final List<ParticipationData> participationDataList) {
        this.participationDataList = participationDataList;
        this.fileName = System.getProperty("user.dir") + "/" + fileName;
        workbook = new XSSFWorkbook();
        creationHelper = workbook.getCreationHelper();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Student-Attendance");
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 5000);
        sheet.setColumnWidth(8, 5000);

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "No.", style);
        createCell(row, 1, "Student Code", style);
        createCell(row, 2, "Full Name", style);
        createCell(row, 3, "Email", style);
        createCell(row, 4, "Event Name", style);
        createCell(row, 5, "Role", style);
        createCell(row, 6, "Checkin Time", style);
        createCell(row, 7, "Checkout Time", style);
        createCell(row, 8, "Status", style);
    }


    private void writeHeaderLineListEvent() {
        sheet = workbook.createSheet("List-Event-Report");
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 5000);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 5000);
        sheet.setColumnWidth(8, 5000);
        sheet.setColumnWidth(9, 5000);

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "No.", style);
        createCell(row, 1, "Event Name", style);
        createCell(row, 2, "Organizer name", style);
        createCell(row, 3, "Location", style);
        createCell(row, 4, "Room", style);
        createCell(row, 5, "MinNumOfParticipant", style);
        createCell(row, 6, "MaxNumOfParticipant", style);
        createCell(row, 7, "ActialNumOfParticipant", style);
        createCell(row, 8, "Status", style);
    }


    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
//        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        int index = 0;
        for (ParticipationData participationData : participationDataList) {
            Row row = sheet.createRow(rowCount++);
            index = index + 1;
            int columnCount = 0;
            createCell(row, 0, index, style);
            createCell(row, 1, participationData.getStudentCode(), style);
            createCell(row, 2, participationData.getFullName(), style);
            createCell(row, 3, participationData.getEmail(), style);
            createCell(row, 4, participationData.getEventName(), style);
            createCell(row, 5, participationData.getRole(), style);
            if (Objects.isNull(participationData.getCheckinTime())) {
                createCell(row, 6, "", style);
            } else {
                CellStyle styleDate = workbook.createCellStyle();
                styleDate.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
                createCell(row, 6, participationData.getCheckinTime(), styleDate);
            }
            if (Objects.isNull(participationData.getCheckoutTime())) {
                createCell(row, 7, "", style);
            } else {
                CellStyle styleDate = workbook.createCellStyle();
                styleDate.setDataFormat(creationHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
                createCell(row, 7, participationData.getCheckoutTime(), styleDate);
            }
            createCell(row, 8, participationData.getStatus(), style);
        }
    }

    public void export() {
        writeHeaderLine();
        writeDataLines();

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
