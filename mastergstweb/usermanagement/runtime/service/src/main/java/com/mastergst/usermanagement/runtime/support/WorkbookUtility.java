package com.mastergst.usermanagement.runtime.support;

import java.awt.Color;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class WorkbookUtility {

	public static void copyRow(Sheet sourceSheet, Sheet targetSheet,
			int srcRowNo, int targetRowNo) {
		Row sourceRow = sourceSheet.getRow(srcRowNo);
		Row targetRow = targetSheet.createRow(targetRowNo);
		if(sourceRow != null) {
			/*if(sourceRow.getRowStyle() != null) {
				targetRow.setRowStyle(sourceRow.getRowStyle());
			}*/
			targetRow.setHeight(sourceRow.getHeight());
		}else {
			targetRow.setHeight((short)-1);
		}
		if(sourceRow != null) {
			for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
				Cell srcCell = sourceRow.getCell(i);
				Cell targetCell = targetRow.createCell(i);
				if (srcCell == null){ 
					targetCell = null; 
					continue; 
				}
				copyCells(srcCell, targetCell);
			}
		}
	}

	static Font copyFont(Font font1, Workbook wb2) {
		boolean isBold = font1.getBold();
		short color = font1.getColor();
		short fontHeight = font1.getFontHeight();
		String fontName = font1.getFontName();
		boolean isItalic = font1.getItalic();
		boolean isStrikeout = font1.getStrikeout();
		short typeOffset = font1.getTypeOffset();
		byte underline = font1.getUnderline();

		Font font2 = wb2.findFont(isBold, color, fontHeight, fontName, isItalic, isStrikeout, typeOffset, underline);
		if (font2 == null) {
			font2 = wb2.createFont();
			font2.setBold(isBold);
			font2.setColor(color);
			font2.setFontHeight(fontHeight);
			font2.setFontName(fontName);
			font2.setItalic(isItalic);
			font2.setStrikeout(isStrikeout);
			font2.setTypeOffset(typeOffset);
			font2.setUnderline(underline);
		}

		return font2;
	}

	static void copyStyles(Cell cell1, Cell cell2) {
		CellStyle style1 = cell1.getCellStyle();
		Map<String, Object> properties = new HashMap<String, Object>();

		// CellUtil.DATA_FORMAT
		short dataFormat1 = style1.getDataFormat();
		if (BuiltinFormats.getBuiltinFormat(dataFormat1) == null) {
			String formatString1 = style1.getDataFormatString();
			DataFormat format2 = cell2.getSheet().getWorkbook().createDataFormat();
			dataFormat1 = format2.getFormat(formatString1);
		}
		properties.put(CellUtil.DATA_FORMAT, dataFormat1);

		// CellUtil.FILL_PATTERN
		// CellUtil.FILL_FOREGROUND_COLOR
		FillPatternType fillPattern = style1.getFillPatternEnum();
		short fillForegroundColor = style1.getFillForegroundColor(); // gets
																		
		properties.put(CellUtil.FILL_PATTERN, fillPattern);
		properties.put(CellUtil.FILL_FOREGROUND_COLOR, fillForegroundColor);

		// CellUtil.FONT
		Font font1 = cell1.getSheet().getWorkbook().getFontAt(style1.getFontIndex());
		Font font2 = copyFont(font1, cell2.getSheet().getWorkbook());
		properties.put(CellUtil.FONT, font2.getIndex());

		// BORDERS
		BorderStyle borderStyle = null;
		short borderColor = -1;
		// CellUtil.BORDER_LEFT
		// CellUtil.LEFT_BORDER_COLOR
		borderStyle = style1.getBorderLeftEnum();
		properties.put(CellUtil.BORDER_LEFT, borderStyle);
		borderColor = style1.getLeftBorderColor();
		properties.put(CellUtil.LEFT_BORDER_COLOR, borderColor);
		// CellUtil.BORDER_RIGHT
		// CellUtil.RIGHT_BORDER_COLOR
		borderStyle = style1.getBorderRightEnum();
		properties.put(CellUtil.BORDER_RIGHT, borderStyle);
		borderColor = style1.getRightBorderColor();
		properties.put(CellUtil.RIGHT_BORDER_COLOR, borderColor);
		// CellUtil.BORDER_TOP
		// CellUtil.TOP_BORDER_COLOR
		borderStyle = style1.getBorderTopEnum();
		properties.put(CellUtil.BORDER_TOP, borderStyle);
		borderColor = style1.getTopBorderColor();
		properties.put(CellUtil.TOP_BORDER_COLOR, borderColor);
		// CellUtil.BORDER_BOTTOM
		// CellUtil.BOTTOM_BORDER_COLOR
		borderStyle = style1.getBorderBottomEnum();
		properties.put(CellUtil.BORDER_BOTTOM, borderStyle);
		borderColor = style1.getBottomBorderColor();
		properties.put(CellUtil.BOTTOM_BORDER_COLOR, borderColor);

		CellUtil.setCellStyleProperties(cell2, properties);
	}

	static void copyCells(Cell cell1, Cell cell2) {
		switch (cell1.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			RichTextString string1 = cell1.getRichStringCellValue();
			if(cell1.getRowIndex() == 0 || cell1.getRowIndex() == 1){
				String val = string1.getString();
				int ind = val.indexOf("*");
				if(ind != -1){
					Font redFont  = cell2.getRow().getSheet().getWorkbook().createFont();
					redFont.setColor(HSSFColor.RED.index);
					string1.applyFont(ind, ind+1, redFont);
				}
			}
			cell2.setCellValue(string1);
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell1)) {
				Date date1 = cell1.getDateCellValue();
				cell2.setCellValue(date1);
			} else {
				double cellValue1 = cell1.getNumericCellValue();
				cell2.setCellValue(cellValue1);
			}
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			String formula1 = cell1.getCellFormula();
			cell2.setCellFormula(formula1);
			break;

		}

		copyStyles(cell1, cell2);

	}
	
	
	public static void copyXssFRow(Sheet sourceSheet, Sheet targetSheet,
			int srcRowNo, int targetRowNo) {
		Row sourceRow = sourceSheet.getRow(srcRowNo);
		Row targetRow = targetSheet.createRow(targetRowNo);
		
		if(sourceRow != null) {
			/*if(sourceRow.getRowStyle() != null) {
				targetRow.setRowStyle(sourceRow.getRowStyle());
			}*/
			targetRow.setHeight(sourceRow.getHeight());
		}else {
			targetRow.setHeight((short)-1);
		}
		if(sourceRow != null) {
			for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
				Cell srcCell = sourceRow.getCell(i);
				Cell targetCell = targetRow.createCell(i);
				if (srcCell == null){ 
					targetCell = null; 
					continue; 
				}
				copyCells(srcCell, targetCell);
			}
		}

	}

	static Font copyXssFFont(Font font1, Workbook wb2) {
		boolean isBold = font1.getBold();
		short color = font1.getColor();
		short fontHeight = font1.getFontHeight();
		String fontName = font1.getFontName();
		boolean isItalic = font1.getItalic();
		boolean isStrikeout = font1.getStrikeout();
		short typeOffset = font1.getTypeOffset();
		byte underline = font1.getUnderline();

		Font font2 = wb2.findFont(isBold, color, fontHeight, fontName, isItalic, isStrikeout, typeOffset, underline);
		if (font2 == null) {
			font2 = wb2.createFont();
			font2.setBold(isBold);
			font2.setColor(color);
			font2.setFontHeight(fontHeight);
			font2.setFontName(fontName);
			font2.setItalic(isItalic);
			font2.setStrikeout(isStrikeout);
			font2.setTypeOffset(typeOffset);
			font2.setUnderline(underline);
		}

		return font2;
	}

	static void copyXssFStyles(Cell cell1, Cell cell2) {
		CellStyle style1 = cell1.getCellStyle();
		Map<String, Object> properties = new HashMap<String, Object>();

		// CellUtil.DATA_FORMAT
		short dataFormat1 = style1.getDataFormat();
		if (BuiltinFormats.getBuiltinFormat(dataFormat1) == null) {
			String formatString1 = style1.getDataFormatString();
			DataFormat format2 = cell2.getSheet().getWorkbook().createDataFormat();
			dataFormat1 = format2.getFormat(formatString1);
		}
		properties.put(CellUtil.DATA_FORMAT, dataFormat1);

		// CellUtil.FILL_PATTERN
		// CellUtil.FILL_FOREGROUND_COLOR
		FillPatternType fillPattern = style1.getFillPatternEnum();
		short fillForegroundColor = style1.getFillForegroundColor(); // gets
																		
		properties.put(CellUtil.FILL_PATTERN, fillPattern);
		properties.put(CellUtil.FILL_FOREGROUND_COLOR, fillForegroundColor);

		// CellUtil.FONT
		Font font1 = cell1.getSheet().getWorkbook().getFontAt(style1.getFontIndex());
		Font font2 = copyFont(font1, cell2.getSheet().getWorkbook());
		properties.put(CellUtil.FONT, font2.getIndex());

		// BORDERS
		BorderStyle borderStyle = null;
		short borderColor = -1;
		// CellUtil.BORDER_LEFT
		// CellUtil.LEFT_BORDER_COLOR
		borderStyle = style1.getBorderLeftEnum();
		properties.put(CellUtil.BORDER_LEFT, borderStyle);
		borderColor = style1.getLeftBorderColor();
		properties.put(CellUtil.LEFT_BORDER_COLOR, borderColor);
		// CellUtil.BORDER_RIGHT
		// CellUtil.RIGHT_BORDER_COLOR
		borderStyle = style1.getBorderRightEnum();
		properties.put(CellUtil.BORDER_RIGHT, borderStyle);
		borderColor = style1.getRightBorderColor();
		properties.put(CellUtil.RIGHT_BORDER_COLOR, borderColor);
		// CellUtil.BORDER_TOP
		// CellUtil.TOP_BORDER_COLOR
		borderStyle = style1.getBorderTopEnum();
		properties.put(CellUtil.BORDER_TOP, borderStyle);
		borderColor = style1.getTopBorderColor();
		properties.put(CellUtil.TOP_BORDER_COLOR, borderColor);
		// CellUtil.BORDER_BOTTOM
		// CellUtil.BOTTOM_BORDER_COLOR
		borderStyle = style1.getBorderBottomEnum();
		properties.put(CellUtil.BORDER_BOTTOM, borderStyle);
		borderColor = style1.getBottomBorderColor();
		properties.put(CellUtil.BOTTOM_BORDER_COLOR, borderColor);

		CellUtil.setCellStyleProperties(cell2, properties);
	}

	static void copyXssFCells(Cell cell1, Cell cell2) {
		switch (cell1.getCellType()) {
		case XSSFCell.CELL_TYPE_STRING:
			RichTextString string1 = cell1.getRichStringCellValue();
			if(cell1.getRowIndex() == 0 || cell1.getRowIndex() == 1){
				String val = string1.getString();
				int ind = val.indexOf("*");
				if(ind != -1){
					Font redFont  = cell2.getRow().getSheet().getWorkbook().createFont();
					redFont.setColor(HSSFColor.RED.index);
					string1.applyFont(ind, ind+1, redFont);
				}
			}
			cell2.setCellValue(string1);
			break;
		case XSSFCell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell1)) {
				Date date1 = cell1.getDateCellValue();
				cell2.setCellValue(date1);
			} else {
				double cellValue1 = cell1.getNumericCellValue();
				cell2.setCellValue(cellValue1);
			}
			break;
		case XSSFCell.CELL_TYPE_FORMULA:
			String formula1 = cell1.getCellFormula();
			cell2.setCellFormula(formula1);
			break;

		}

		copyStyles(cell1, cell2);

	}

}
