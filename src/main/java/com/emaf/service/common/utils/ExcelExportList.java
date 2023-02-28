package com.emaf.service.common.utils;

import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.enumeration.EEventType;
import com.emaf.service.model.common.CommonData;
import com.emaf.service.model.event.EventListData;
import com.emaf.service.repository.EventRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ExcelExportList
 *
 * @author: VuongVT2
 * @since: 2023/02/01
 */
public class ExcelExportList {
    private String fileName;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private XSSFSheet sheet2;
    private CreationHelper creationHelper;
    private List<EventListData> eventListData;
    private String startTime;
    private String endTime;

    private final EventRepository repo;

    public ExcelExportList(final String fileName, final List<EventListData> eventListData, final String startTime, final String endTime, final EventRepository repo) {
        this.eventListData = eventListData;
        this.fileName = System.getProperty("user.dir") + "/" + fileName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repo = repo;
        workbook = new XSSFWorkbook();
        creationHelper = workbook.getCreationHelper();
    }

    private void writeHeaderLineListEvent() {
        sheet = workbook.createSheet("List-Event-Report");
        sheet2 = workbook.createSheet("Statistic");
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 8000);
        sheet.setColumnWidth(6, 8000);
        sheet.setColumnWidth(7, 8000);
        sheet.setColumnWidth(8, 4000);
        sheet.setColumnWidth(9, 4000);
        sheet.setColumnWidth(10, 4000);
        sheet2.setColumnWidth(0, 8000);
        sheet2.setColumnWidth(1, 4600);
        sheet2.setColumnWidth(2, 5000);
        Row row = sheet.createRow(0);
        Row row2 = sheet2.createRow(1);
        Row row00 = sheet2.createRow(0);

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
        createCell(row, 7, "ActualNumOfParticipant", style);
        createCell(row, 8, "Status", style);
        createCell(row, 9, "Type", style);

        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setBold(true);
        font2.setFontHeight(14);
        style2.setFont(font2);

        CellStyle style6 = workbook.createCellStyle();
        XSSFFont font6 = workbook.createFont();
        font6.setBold(true);
        font6.setFontHeight(14);
        style6.setFont(font6);

        CellStyle style5 = workbook.createCellStyle();
        XSSFFont font5 = workbook.createFont();
        font5.setFontHeight(14);
        style5.setAlignment(HorizontalAlignment.CENTER);
        style5.setFont(font5);
        createCell(row2, 0, "Total event by categoty:", style6);
        createCell(row2, 2, repo.countByRangeTimeAndType(startTime, endTime, EEventType.ACADEMIC), style5);
        createCell(row00, 2, "Number of events", style6);
        CellStyle style3 = workbook.createCellStyle();
        XSSFFont font3 = workbook.createFont();
        font.setFontHeight(14);
        style2.setFont(font3);
        createCell(row2, 1, "ACADEMIC", style3);
        Row row3 = sheet2.createRow(2);
        createCell(row3, 1, "SPORT", style3);
        createCell(row3, 2, repo.countByRangeTimeAndType(startTime, endTime, EEventType.SPORTS), style5);
        Row row4 = sheet2.createRow(3);
        createCell(row4, 1, "ARTS", style3);
        createCell(row4, 2, repo.countByRangeTimeAndType(startTime, endTime, EEventType.ARTS), style5);
        Row row5 = sheet2.createRow(4);
        createCell(row5, 1, "GAME", style3);
        createCell(row5, 2, repo.countByRangeTimeAndType(startTime, endTime, EEventType.GAMES), style5);
        Row row6 = sheet2.createRow(5);
        createCell(row6, 1, "CULTURE", style3);
        createCell(row6, 2, repo.countByRangeTimeAndType(startTime, endTime, EEventType.CULTURE), style5);

        Row row7 = sheet2.createRow(7);
        createCell(row7, 0, "Total event by status:", style6);
        createCell(row7, 1, "FINISHED", style3);
        createCell(row7, 2, repo.countByRangeTimeAndStatus(startTime, endTime, EEventStatus.FINISHED), style5);
        Row row8 = sheet2.createRow(8);
        createCell(row8, 1, "STOPPED", style3);
        createCell(row8, 2, repo.countByRangeTimeAndStatus(startTime, endTime, EEventStatus.STOPPED), style5);
        Row row9 = sheet2.createRow(10);
        createCell(row9, 0, "Total event:", style6);
        createCell(row9, 2, repo.countByRangeTime(startTime, endTime), style5);
    }


    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
//        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }else if (value instanceof Boolean) {
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

    private void writeDataListLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        CellStyle styleCenter = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        styleCenter.setFont(font);
        styleCenter.setAlignment(HorizontalAlignment.CENTER);
        font.setFontHeight(14);
        style.setFont(font);
        int index = 0;
        for (EventListData eventData : eventListData) {
            List<String> roomNameList = new ArrayList<>();
                    List<CommonData> roomList = eventData.getRoom();
            if (!CollectionUtils.isEmpty(roomList)) {
                for (CommonData room : roomList) {
                    String name = room.getName();
                    roomNameList.add(name);
                }
            }
            String roomName = String.join(", ", roomNameList);
            Row row = sheet.createRow(rowCount++);
            index = index + 1;
            int columnCount = 0;
            createCell(row, 0, index, styleCenter);
            createCell(row, 1, eventData.getEventName(), style);
            createCell(row, 2, eventData.getOrganizerName(), style);
            createCell(row, 3, eventData.getLocation(), style);
            createCell(row, 4, roomName, style);
            createCell(row, 5, eventData.getMinNumOfParticipant(), styleCenter);
            createCell(row, 6, eventData.getMaxNumOfParticipant(), styleCenter);
            createCell(row, 7, eventData.getActualNumOfParticipant(), styleCenter);
            createCell(row, 8, eventData.getStatus().name(), style);
            createCell(row, 9, eventData.getType().name(), style);
        }
    }

    public void export() {
        writeHeaderLineListEvent();
        writeDataListLines();

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
