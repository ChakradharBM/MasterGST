package com.mastergst.core.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.DJValueFormatter;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class ReportHelper {
	
	private DJValueFormatter formatter = new DJValueFormatter() {
		
		@Override
		public String getClassName() {
			return String.class.getName();
		}
		
		@Override
		public Object evaluate(Object value, Map fields, Map variables, Map parameters) {
			return new DecimalFormat("#,##,##,##,##,##,##,##,###.00").format(new Double(value.toString()).doubleValue());
		}
	};

	public JasperPrint getReport(List items, List<AbstractColumn> columns) throws ColumnBuilderException, JRException, ClassNotFoundException {
		Style headerStyle = createHeaderStyle();
		Style detailTextStyle = createDetailTextStyle();
		Style detailNumberStyle = createDetailNumberStyle();
		DynamicReport dynaReport = getReport(headerStyle, detailTextStyle, detailNumberStyle, columns);
		JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dynaReport, new ClassicLayoutManager(),
				new JRBeanCollectionDataSource(items));
		return jp;
	}
	
	public static byte[] getReportPdf(final JasperPrint jp) throws JRException {
		return JasperExportManager.exportReportToPdf(jp);
	}
	
	public static byte[] getReportXlsx(final JasperPrint jp) throws JRException, IOException {
		JRXlsxExporter xlsxExporter = new JRXlsxExporter();
		final byte[] rawBytes;
		
		try(ByteArrayOutputStream xlsReport = new ByteArrayOutputStream()){
			xlsxExporter.setExporterInput(new SimpleExporterInput(jp));
			xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReport));
			xlsxExporter.exportReport();

			rawBytes = xlsReport.toByteArray();
		}
		return rawBytes;
	}

	private Style createHeaderStyle() {		
		return new StyleBuilder(true)
				.setFont(Font.VERDANA_MEDIUM_BOLD)
				.setBorder(Border.THIN())
				//.setBorderBottom(Border.PEN_2_POINT())
				.setBorderColor(Color.BLACK)
				.setBackgroundColor(Color.GRAY)
				.setTextColor(Color.BLACK)
				.setHorizontalAlign(HorizontalAlign.CENTER)
				.setVerticalAlign(VerticalAlign.MIDDLE)
				.setTransparency(Transparency.OPAQUE)
				.build();
	}

	private Style createDetailTextStyle() {
		return new StyleBuilder(true)
				.setFont(Font.VERDANA_MEDIUM)
				.setBorder(Border.THIN())
				.setBorderColor(Color.BLACK)
				.setTextColor(Color.BLACK)
				.setHorizontalAlign(HorizontalAlign.LEFT)
				.setVerticalAlign(VerticalAlign.MIDDLE)
				.setPaddingLeft(5)
				.build();
	}

	private Style createDetailNumberStyle() {
		return new StyleBuilder(true)
				.setFont(Font.VERDANA_MEDIUM)
				.setBorder(Border.THIN())
				.setBorderColor(Color.BLACK)
				.setTextColor(Color.BLACK)
				.setHorizontalAlign(HorizontalAlign.RIGHT)
				.setVerticalAlign(VerticalAlign.MIDDLE)
				.setPaddingRight(5)
				.setPattern("#,##,##,##,##,##,##,##,##0.00")
				.build();
	}

	public static AbstractColumn createColumn(String property, Class<?> type, String title, int width, Style headerStyle, Style detailStyle)
			throws ColumnBuilderException {
		return ColumnBuilder.getNew()
				.setColumnProperty(property, type.getName())
				.setTitle(title)
				.setWidth(Integer.valueOf(width))
				.setStyle(detailStyle)
				.setHeaderStyle(headerStyle)
				.build();
	}
	
	private Style getHeaderVariables() {
		Style headerVariables = new Style();
		headerVariables.setFont(Font.ARIAL_MEDIUM_BOLD);
		headerVariables.setHorizontalAlign(HorizontalAlign.RIGHT);
		headerVariables.setVerticalAlign(VerticalAlign.MIDDLE);
		return headerVariables;
	}
	
	private Style getGroupLabelStyle() {
		return new StyleBuilder(false).setFont(Font.ARIAL_SMALL)
				.setHorizontalAlign(HorizontalAlign.RIGHT).setBorderTop(Border.THIN())
				.setStretchWithOverflow(false)
				.build();
	}

	private DynamicReport getReport(Style headerStyle, Style detailTextStyle, Style detailNumStyle, List<AbstractColumn> columns)
			throws ColumnBuilderException, ClassNotFoundException {

		DynamicReportBuilder report = new DynamicReportBuilder();

		for(AbstractColumn column : columns) {
			report.addColumn(column);
		}

		StyleBuilder titleStyle = new StyleBuilder(true);
		titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
		titleStyle.setFont(new Font(20, null, true));
		
		Style headerVariables = getHeaderVariables();
		Style groupStyle = getGroupLabelStyle();
		
		DJGroupLabel glabel1 = new DJGroupLabel("Total amount:", groupStyle);

		report.setTitle("INVOICE");
		report.setTitleStyle(titleStyle.build());
		
		GroupBuilder gb1 = new GroupBuilder();
		DJGroup g1 = gb1 //.setCriteriaColumn((PropertyColumn) columnState)
				.addFooterVariable(columns.get(0), DJCalculation.SUM, headerVariables, formatter, glabel1)
				.setGroupLayout(GroupLayout.EMPTY) 
				.build();
		report.addGroup(g1);
		
		
		report.setUseFullPageWidth(true);
		return report.build();
	}
}