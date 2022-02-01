package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class PaymentInvoice {
	
	private String docId;
	private String invoiceno;
	private String billedtoname;
	private String paymentStatus;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dateofinvoice;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dueDate;
	
	private List<Payments> payments;
	

}
