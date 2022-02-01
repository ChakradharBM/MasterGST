package com.mastergst.web.usermanagement.runtime.controller;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.exp;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.sbt;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.mastergst.core.util.MoneyConverterUtil;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.builder.column.ColumnBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.subtotal.AggregationSubtotalBuilder;
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class InvoiceDesign {
    
    private static InvoiceParent getInvoice() {
    	InvoiceParent invoice = new InvoiceParent();
    	invoice.setInvoiceno("INV123");
    	invoice.setFullname("BVM IT Consulting Services");
    	invoice.setFromAddr1("# 832, B S Nagar, KPHB Colony Hyderabad 500072");
    	invoice.setGstin("36AAGCB1286Q1Z5");
        invoice.setTotalamount(10000.1d);
        invoice.setTotaltax(10.1d);
        //invoice.setTotalIgstAmount(1000d);
        
        invoice.setNotes("Online / Wire Transfer");
        
        invoice.setBilledtoname("Mary Patterson");
        invoice.setToAddr1("151 Pompton St.");
        invoice.setToAddr2("Hyderabad");

        List<Item> items = new ArrayList<Item>();
        items.add(createItem("Notebook", "998313", 1d, Double.valueOf(1000000000)));
        items.add(createItem("DVD", "998313", 5d, Double.valueOf(40)));
        items.add(createItem("Book", "998313", 2d, Double.valueOf(.2)));
        items.add(createItem("Phone", "998", 1d, Double.valueOf(200)));
        invoice.setItems(items);
        
    	return invoice;
    }
    
    private static Item createItem(String description, String hsn, Double quantity, Double unitprice) {
        Item item = new Item();
        item.setDesc(description);
        item.setHsn(hsn);
        item.setQuantity(quantity);
        item.setTaxablevalue(unitprice);
        item.setCgstamount(unitprice*quantity*.09);
        item.setCgstrate(9.0);
        item.setSgstamount(unitprice*quantity*.09);
        item.setSgstrate(9.0);
        item.setIgstamount(unitprice*quantity*.18);
        item.setIgstrate(18.0);
        item.setTotal(unitprice*quantity);
        return item;
    }

    public static void main(String[] args) {
    	//https://dynamicreports.lbayer.com/examples/examples-overview/
    	InvoiceDesign design = new InvoiceDesign();
        InvoiceParent data = getInvoice();
        try {
        	InputStream is = Templates.class.getResource("bvmcs_logo.png").openStream();
            JasperReportBuilder report = design.build(data, 9.0, is, is);
            report.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JasperReportBuilder build(InvoiceParent data, Double rate, InputStream logo, InputStream signature) throws DRException {
        JasperReportBuilder report = report();
        // init styles
        StyleBuilder columnStyle = stl.style(Templates.columnStyle).setBorder(stl.pen1Point());
        StyleBuilder subtotalStyle = stl.style(columnStyle).bold();

        // init columns
        TextColumnBuilder<String> nameColumn = col.column("Item / Service Description", "desc", type.stringType());
        TextColumnBuilder<String> hsnColumn = col.column("HSN / SAC", "hsn", type.stringType());
        TextColumnBuilder<Double> taxableValColumn = col.column("Taxable Value", "taxablevalue", type.doubleType()).setValueFormatter(new CurrencyFormatter());
        TextColumnBuilder<Double> igstColumn = col.column("IGST Amt", "igstamount", type.doubleType()).setValueFormatter(new GSTColumnFormatter(rate));
        TextColumnBuilder<Double> cgstColumn = col.column("CGST Amt", "cgstamount", type.doubleType()).setValueFormatter(new GSTColumnFormatter(rate));
        TextColumnBuilder<Double> sgstColumn = col.column("SGST Amt", "sgstamount", type.doubleType()).setValueFormatter(new GSTColumnFormatter(rate));
        TextColumnBuilder<Double> totalColumn = col.column("Total Amount", "total", type.doubleType()).setValueFormatter(new CurrencyFormatter());
        ColumnBuilder<?, ?>[] columns = {nameColumn, hsnColumn, taxableValColumn};
        AggregationSubtotalBuilder<?>[] subTotalColumns = {sbt.aggregate(exp.text(" "), nameColumn, Calculation.NOTHING).setShowInColumn(nameColumn), sbt.aggregate(exp.text("Totals"), hsnColumn, Calculation.NOTHING), sbt.sum(taxableValColumn)};
        if(data.getTotalIgstAmount() != null && data.getTotalIgstAmount() > 0) {
        	columns = ArrayUtils.add(columns, columns.length, igstColumn);
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.sum(igstColumn));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text(" "), igstColumn, Calculation.NOTHING).setShowInColumn(igstColumn).setStyle(stl.style().setBorder(stl.pen())));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text("Taxable Amt"), igstColumn, Calculation.NOTHING).setShowInColumn(igstColumn));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text("Total Tax"), igstColumn, Calculation.NOTHING).setShowInColumn(igstColumn));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text("Inv Total"), igstColumn, Calculation.NOTHING).setShowInColumn(igstColumn));
        } else {
        	columns = ArrayUtils.add(columns, columns.length, cgstColumn);
        	columns = ArrayUtils.add(columns, columns.length, sgstColumn);
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.sum(cgstColumn));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.sum(sgstColumn));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text(" "), sgstColumn, Calculation.NOTHING).setShowInColumn(sgstColumn).setStyle(stl.style().setBorder(stl.pen())));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text("Taxable Amt"), sgstColumn, Calculation.NOTHING).setShowInColumn(sgstColumn));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text("Total Tax"), sgstColumn, Calculation.NOTHING).setShowInColumn(sgstColumn));
        	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text("Inv Total"), sgstColumn, Calculation.NOTHING).setShowInColumn(sgstColumn));
        }
        columns = ArrayUtils.add(columns, columns.length, totalColumn);
        subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.sum(totalColumn));
        subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text(" "), totalColumn, Calculation.NOTHING).setShowInColumn(totalColumn).setStyle(stl.style().setBorder(stl.pen())));
    	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.sum(taxableValColumn).setShowInColumn(totalColumn));
    	subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.aggregate(exp.text(new DecimalFormat("##,##,##,##0.00").format(data.getTotaltax())), totalColumn, Calculation.NOTHING).setStyle(subtotalStyle.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)).setShowInColumn(totalColumn));
        subTotalColumns = ArrayUtils.add(subTotalColumns, subTotalColumns.length, sbt.sum(totalColumn).setShowInColumn(totalColumn));
        
        // configure report
        report.setTemplate(Templates.reportTemplate)
              .setColumnStyle(columnStyle)
              .setSubtotalStyle(subtotalStyle)
              // columns
              .columns(columns)
              // band components
			  .title(Templates.createTitleComponent(logo),
						cmp.horizontalList().setStyle(stl.style(10)).setGap(30).add(
								cmp.hListCell(createInvLeftComponent(data.getFullname(), data)).heightFixedOnTop(),
								cmp.hListCell(createInvRightComponent(data)).heightFixedOnTop()),
						cmp.verticalGap(10),
						cmp.horizontalList().setStyle(stl.style(10).setBorder(stl.pen1Point())).setGap(30).setFixedWidth(275).add(
								cmp.hListCell(createBilledToComponent(data)).heightFixedOnTop()),
						cmp.verticalGap(10))
			  .subtotalsAtSummary(subTotalColumns)
			  .summary(cmp.text("*** "+MoneyConverterUtil.getMoneyIntoWords(data.getTotalamount()).toUpperCase()).setStyle(Templates.groupRightStyle),
					   cmp.verticalGap(10),
					   cmp.text("Notes :").setStyle(Templates.boldCenteredStyle),
					   cmp.text("\t"+((null != data.getNotes()) ? data.getNotes() : "")),
					   cmp.verticalGap(10),
					   cmp.text("Terms & Conditions :").setStyle(Templates.boldCenteredStyle),
					   cmp.text("\t"+((null != data.getTerms()) ? data.getTerms() : "")),
					   cmp.verticalGap(10),
					   cmp.text("Custom Fields :").setStyle(Templates.boldCenteredStyle),
					   cmp.text("\t"+((null != data.getCustomField1()) ? data.getCustomField1() : "")),
					   cmp.horizontalList(cmp.text("\t"), cmp.image(signature).setFixedDimension(100, 50).setHorizontalImageAlignment(HorizontalImageAlignment.RIGHT)),
					   cmp.text("Authorised Signature").setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
					   cmp.text(((null != data.getAccountname()) ? data.getAccountname() : "")).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)
			  )
              .pageFooter(Templates.footerComponent)
              .setDataSource(new JRBeanCollectionDataSource(data.getItems()));

        return report;
    }

    private ComponentBuilder<?, ?> createInvLeftComponent(String label, InvoiceParent customer) {
        HorizontalListBuilder list = cmp.horizontalList().setBaseStyle(stl.style().setLeftPadding(10));
        addColumnAttribute(list, "GSTIN", customer.getGstin());
        addColumnAttribute(list, "PAN", customer.getGstin().substring(2, customer.getGstin().length()-3));
        addColumnAttribute(list, "CIN", "");
        addColumnAttribute(list, "Address", customer.getFromAddr1());
        return cmp.verticalList(cmp.text(label).setStyle(Templates.bold18CenteredStyle), list);
    }
    
    private ComponentBuilder<?, ?> createInvRightComponent(InvoiceParent customer) {
        HorizontalListBuilder list = cmp.horizontalList().setBaseStyle(stl.style().setLeftPadding(10));
        addColumnAttribute(list, "Invoice Number", customer.getInvoiceno());
        addColumnAttribute(list, "Invoice Date", customer.getDateofinvoice_str());
        addColumnAttribute(list, "State Code", String.valueOf(customer.getFromStateCode()));
        addColumnAttribute(list, "Place of Supply", customer.getFromPlace());
        return cmp.verticalList(cmp.text(" ").setStyle(Templates.bold18CenteredStyle), list);
    }
    
    private ComponentBuilder<?, ?> createBilledToComponent(InvoiceParent customer) {
        HorizontalListBuilder list = cmp.horizontalList().setBaseStyle(stl.style().setLeftPadding(10));
        list.add(cmp.text(customer.getBilledtoname()).setStyle(Templates.boldStyle)).newRow();
        list.add(cmp.verticalGap(5)).newRow();
        list.add(cmp.text("GSTIN: "+customer.getToGstin())).newRow();
        list.add(cmp.text(customer.getToAddr1())).newRow();
        list.add(cmp.text(customer.getToAddr2())).newRow();
        return cmp.verticalList(cmp.text("Details Of Receiver ( Billed To )").setStyle(Templates.bold12CenteredStyle.setBackgroundColor(Color.LIGHT_GRAY)), list);
    }

    private void addColumnAttribute(HorizontalListBuilder list, String label, String value) {
        if (value == null)
        	value = "";
        list.add(cmp.text(label + " :").setFixedColumns(10).setStyle(Templates.boldStyle), cmp.text(value)).newRow();
    }
    
    private class CurrencyFormatter extends AbstractValueFormatter<String, Double> {
		private static final long serialVersionUID = 1L;

		@Override
		public String format(Double value, ReportParameters reportParameters) {
			if(null != value) {
				return "₹ "+new DecimalFormat("##,##,##,##0.00").format(value);
			}
			return "";
		}
	}
    
    private class GSTColumnFormatter extends AbstractValueFormatter<String, Double> {
		private static final long serialVersionUID = 1L;
		Double amt=0d;
		
		private GSTColumnFormatter(Double amt) {
			this.amt=amt;
		}

		@Override
		public String format(Double value, ReportParameters reportParameters) {
			return "₹ "+new DecimalFormat("##,##,##,##0.00").format(value) + "\n@("+amt+"%)";
		}
	}
}