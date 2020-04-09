package cn.gjing.tools.excel.write.style;

import cn.gjing.tools.excel.ExcelField;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gjing
 **/
public final class DefaultExcelStyleWriteListener implements ExcelStyleWriteListener {
    private Workbook workbook;
    private CellStyle titleStyle;
    private Map<Integer, CellStyle> headStyle;
    private Map<Integer, CellStyle> bodyStyle;

    @Override
    public void init(Workbook workbook) {
        this.workbook = workbook;
        this.headStyle = new HashMap<>(16);
        this.bodyStyle = new HashMap<>(16);
    }

    @Override
    public void setTitleStyle(Cell cell) {
        if (this.titleStyle == null) {
            CellStyle cellStyle = this.workbook.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            this.titleStyle = cellStyle;
        }
        cell.setCellStyle(this.titleStyle);
    }

    @Override
    public void setHeadStyle(Row row, Cell cell, ExcelField excelField, Field field, String headName, int index, int colIndex) {
        CellStyle cellStyle = this.headStyle.get(colIndex);
        if (cellStyle == null) {
            cellStyle = this.workbook.createCellStyle();
            cellStyle.setFillForegroundColor(IndexedColors.LIME.index);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.index);
            cellStyle.setFont(font);
            this.headStyle.put(colIndex, cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void setBodyStyle(Row row, Cell cell, ExcelField excelField, Field field, String headName, int index, int colIndex) {
        CellStyle cellStyle = this.bodyStyle.get(colIndex);
        if (cellStyle == null) {
            cellStyle = this.workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setWrapText(true);
            cellStyle.setLocked(false);
            cellStyle.setDataFormat(this.workbook.createDataFormat().getFormat(excelField.format()));
            this.bodyStyle.put(colIndex, cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    @Override
    public void completeCell(Sheet sheet, Row row, Cell cell, ExcelField excelField, Field field, String headName, int index,
                             int colIndex, boolean isHead, Object value) {
        if (isHead) {
            if (index == 0) {
                CellStyle cellStyle = this.workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                cellStyle.setWrapText(true);
                cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(excelField.format()));
                sheet.setDefaultColumnStyle(colIndex, cellStyle);
                sheet.setColumnWidth(colIndex, excelField.width());
            }
            this.setHeadStyle(row, cell, excelField, field, headName, index, colIndex);
            return;
        }
        this.setBodyStyle(row, cell, excelField, field, headName, index, colIndex);
    }

    @Override
    public void completeRow(Sheet sheet, Row row, int index, boolean isHead) {
        if (isHead) {
            row.setHeight((short) 350);
        }
    }
}
