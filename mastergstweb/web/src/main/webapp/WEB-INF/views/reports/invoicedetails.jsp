<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<script>
function checkZero(data){
	  if(data.length == 1){
		data = "0" + data;
	  }
	  return data;
}
function formatDate(date) {
	if(date == null || typeof(date) === 'string' || date instanceof String) {
		return date;
	} else {
		var d = new Date(date),
			month = '' + (d.getMonth() + 1),
			day = '' + d.getDate(),
			year = d.getFullYear();

		if (month.length < 2) month = '0' + month;
		if (day.length < 2) day = '0' + day;
		return [day, month, year].join('-');
	}
}
function updateInvoiceDetails(invoice,rettype) {
	var totalIGST1 = 0, totalCGST1 = 0, totalSGST1 = 0, totalCESS1 = 0, totalExempted = 0, totalITCIGST1 = 0, totalITCCGST1 = 0, totalITCSGST1 = 0, totalITCCESS1 = 0,totalinvitc = 0;
	invoice.id = invoice.userid;
	invoice.invoicetype = invoice.invtype;
	if(invoice.invoiceno == null){
		invoice.invoiceno = '';
	}
	invoice.serialnoofinvoice = invoice.invoiceno;
	if(invoice.b2b && invoice.b2b.length > 0) {
		if(invoice.b2b[0].ctin == null){
			invoice.b2b[0].ctin = '';
		}
		invoice.billedtogstin = invoice.b2b[0].ctin;
		if(invoice.b2b[0].inv && invoice.b2b[0].inv.length > 0) {
			invoice.address = invoice.b2b[0].inv[0].address;
			invoice.invTyp = invoice.b2b[0].inv[0].invTyp;
		}
	} else {
		invoice.billedtogstin = '';
		invoice.address = '';
	}
	if(invoice.pi && invoice.pi.length > 0){
		var expDate = invoice.pi[0].expirydate;
		expDate = expDate.split('-'); 
		invoice.expirydateofinvoice = expDate[0] + "/" + expDate[1] + "/" + expDate[2];
	}else if(invoice.est && invoice.est.length > 0){
		var expDate = invoice.est[0].expirydate;
		expDate = expDate.split('-');
		invoice.expirydateofinvoice = expDate[0] + "/" + expDate[1] + "/" + expDate[2];
	}
	if(invoice.po && invoice.po.length > 0){
		var deliveryDate = invoice.po[0].deliverydate;
		deliveryDate = deliveryDate.split('-');
		invoice.deliverydate = deliveryDate[0] + "/" + deliveryDate[1] + "/" + deliveryDate[2];
	}
	
	if(invoice.dc && invoice.dc.length > 0){
		invoice.challantype = invoice.dc[0].challanType;
	}
	
	if(invoice.diffPercent == null){
		invoice.diffPercent = 'No';
	}
	if(invoice.dateofinvoice != null){
	var invDate = new Date(invoice.dateofinvoice);
	var day = invDate.getDate() + "";
	var month = (invDate.getMonth() + 1) + "";
	var year = invDate.getFullYear() + "";
	day = checkZero(day);
	month = checkZero(month);
	year = checkZero(year);
	invoice.dateofinvoice = day + "/" + month + "/" + year;
	}else{invoice.dateofinvoice = "";
	if(invoice.dueDate != null){
		var invDate = new Date(invoice.dueDate);
		var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dueDate = day + "/" + month + "/" + year;
	}else{invoice.dueDate = "";}
	var fp = invoice.fp;
	var month = fp.substring(0,2);
	}
	if(month == '01'){totalInv1++;}if(month == '02'){totalInv2++;}if(month == '03'){totalInv3++;}if(month == '04'){totalInv4++;}if(month == '05'){totalInv5++;}if(month == '06'){totalInv6++;}if(month == '07'){totalInv7++;}if(month == '08'){totalInv8++;}if(month == '09'){totalInv9++;}if(month == '10'){totalInv10++;}if(month == '11'){totalInv11++;}if(month == '12'){totalInv12++;}
	if(invoice.bankDetails) {
		if(invoice.bankDetails.bankname == '' && invoice.bankDetails.accountnumber == '' && invoice.bankDetails.branchname == '' && invoice.bankDetails.ifsccode == ''){
			invoice.bankdetails = 'false';
		}else{
			invoice.bankdetails = 'true';
		}	
		invoice.bankname = invoice.bankDetails.bankname;
		invoice.accountnumber = invoice.bankDetails.accountnumber;
		invoice.branchname = invoice.bankDetails.branchname;
		invoice.ifsccode = invoice.bankDetails.ifsccode;
		if(invoice.bankDetails.accountName == null){
			invoice.accountname = '';
		}else{
			invoice.accountname = invoice.bankDetails.accountName;
		}
		} else {
		invoice.bankdetails = 'false';
	}
	if(invoice.fullname == null) {
		invoice.fullname = '';
	}
	if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
		invoice.invoiceCustomerIdAndBilledToName = '';
	}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
		invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
	}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
		invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
	}
	if(invoice.invoiceCustomerId == null) {
		invoice.invoiceCustomerId = '';
	}
	if(invoice.billedtoname == null) {
		invoice.billedtoname = '';
	}
	if(invoice.branch == null) {
		invoice.branch = '';
	}
	if(invoice.vertical == null) {
		invoice.vertical = '';
	}
	if(invoice.tcstdsAmount) { 
		if(rettype == 'GSTR1' || rettype == 'SalesRegister' ){
			totalTcsValue += invoice.tcstdsAmount; 
			if(month == '01'){monthtotalTcsValue1+=invoice.tcstdsAmount;}if(month == '02'){monthtotalTcsValue2+=invoice.tcstdsAmount;}if(month == '03'){monthtotalTcsValue3+=invoice.tcstdsAmount;}if(month == '04'){monthtotalTcsValue4+=invoice.tcstdsAmount;}if(month == '05'){monthtotalTcsValue5+=invoice.tcstdsAmount;}if(month == '06'){monthtotalTcsValue6+=invoice.tcstdsAmount;}if(month == '07'){monthtotalTcsValue7+=invoice.tcstdsAmount;}if(month == '08'){monthtotalTcsValue8+=invoice.tcstdsAmount;}if(month == '09'){monthtotalTcsValue9+=invoice.tcstdsAmount;}if(month == '10'){monthtotalTcsValue10+=invoice.tcstdsAmount;}if(month == '11'){monthtotalTcsValue11+=invoice.tcstdsAmount;}if(month == '12'){monthtotalTcsValue12+=invoice.tcstdsAmount;}
		}else if(rettype == 'GSTR2' || rettype == 'PurchaseRegister' || rettype == 'Purchase Register'){
			totalTdsValue += invoice.tcstdsAmount;
			if(month == '01'){monthtotalTdsValue1+=invoice.tcstdsAmount;}if(month == '02'){monthtotalTdsValue2+=invoice.tcstdsAmount;}if(month == '03'){monthtotalTdsValue3+=invoice.tcstdsAmount;}if(month == '04'){monthtotalTdsValue4+=invoice.tcstdsAmount;}if(month == '05'){monthtotalTdsValue5+=invoice.tcstdsAmount;}if(month == '06'){monthtotalTdsValue6+=invoice.tcstdsAmount;}if(month == '07'){monthtotalTdsValue7+=invoice.tcstdsAmount;}if(month == '08'){monthtotalTdsValue8+=invoice.tcstdsAmount;}if(month == '09'){monthtotalTdsValue9+=invoice.tcstdsAmount;}if(month == '10'){monthtotalTdsValue10+=invoice.tcstdsAmount;}if(month == '11'){monthtotalTdsValue11+=invoice.tcstdsAmount;}if(month == '12'){monthtotalTdsValue12+=invoice.tcstdsAmount;}
		}
	}
	if(invoice.totaltaxableamount) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
					if(invoice.cdn[0].nt[0].ntty == 'C'){
						totalTaxableValue -= invoice.totaltaxableamount;
						if(month == '01'){monthtotalTaxableValue1-=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2-=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3-=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4-=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5-=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6-=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7-=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8-=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9-=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10-=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11-=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12-=invoice.totaltaxableamount;}
					}else{
						totalTaxableValue+=invoice.totaltaxableamount;
						if(month == '01'){monthtotalTaxableValue1+=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2+=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3+=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4+=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5+=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6+=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7+=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8+=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9+=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10+=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11+=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12+=invoice.totaltaxableamount;}
					}
			}else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalTaxableValue-=invoice.totaltaxableamount;
					if(month == '01'){monthtotalTaxableValue1-=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2-=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3-=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4-=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5-=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6-=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7-=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8-=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9-=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10-=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11-=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12-=invoice.totaltaxableamount;}
				}else{
					totalTaxableValue+=invoice.totaltaxableamount;
					if(month == '01'){monthtotalTaxableValue1+=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2+=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3+=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4+=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5+=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6+=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7+=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8+=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9+=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10+=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11+=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12+=invoice.totaltaxableamount;}
				}
			}else if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'C'){
						totalTaxableValue-=invoice.totaltaxableamount;
						if(month == '01'){monthtotalTaxableValue1-=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2-=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3-=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4-=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5-=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6-=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7-=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8-=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9-=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10-=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11-=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12-=invoice.totaltaxableamount;}
					}else{
						totalTaxableValue+=invoice.totaltaxableamount;
						if(month == '01'){monthtotalTaxableValue1+=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2+=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3+=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4+=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5+=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6+=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7+=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8+=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9+=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10+=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11+=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12+=invoice.totaltaxableamount;}
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalTaxableValue-=invoice.totaltaxableamount;
						if(month == '01'){monthtotalTaxableValue1-=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2-=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3-=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4-=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5-=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6-=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7-=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8-=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9-=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10-=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11-=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12-=invoice.totaltaxableamount;}
					}else{
						totalTaxableValue+=invoice.totaltaxableamount;
						if(month == '01'){monthtotalTaxableValue1+=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2+=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3+=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4+=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5+=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6+=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7+=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8+=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9+=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10+=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11+=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12+=invoice.totaltaxableamount;}
					}
				}
				
			}else{
				totalTaxableValue+=invoice.totaltaxableamount;
				if(month == '01'){monthtotalTaxableValue1+=invoice.totaltaxableamount;}if(month == '02'){monthtotalTaxableValue2+=invoice.totaltaxableamount;}if(month == '03'){monthtotalTaxableValue3+=invoice.totaltaxableamount;}if(month == '04'){monthtotalTaxableValue4+=invoice.totaltaxableamount;}if(month == '05'){monthtotalTaxableValue5+=invoice.totaltaxableamount;}if(month == '06'){monthtotalTaxableValue6+=invoice.totaltaxableamount;}if(month == '07'){monthtotalTaxableValue7+=invoice.totaltaxableamount;}if(month == '08'){monthtotalTaxableValue8+=invoice.totaltaxableamount;}if(month == '09'){monthtotalTaxableValue9+=invoice.totaltaxableamount;}if(month == '10'){monthtotalTaxableValue10+=invoice.totaltaxableamount;}if(month == '11'){monthtotalTaxableValue11+=invoice.totaltaxableamount;}if(month == '12'){monthtotalTaxableValue12+=invoice.totaltaxableamount;}
			}
		}
	} else {
		invoice.totaltaxableamount = 0.00;
	}
	if(invoice.totaltax) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'C'){
					totalTax-=invoice.totaltax;
					if(month == '01'){monthtotalTaxValue1-=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2-=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3-=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4-=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5-=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6-=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7-=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8-=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9-=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10-=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11-=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12-=invoice.totaltax;}
				}else{
					totalTax+=invoice.totaltax;
					if(month == '01'){monthtotalTaxValue1+=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2+=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3+=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4+=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5+=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6+=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7+=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8+=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9+=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10+=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11+=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12+=invoice.totaltax;}
				}
			}else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalTax-=invoice.totaltax;
					if(month == '01'){monthtotalTaxValue1-=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2-=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3-=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4-=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5-=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6-=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7-=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8-=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9-=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10-=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11-=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12-=invoice.totaltax;}
				}else{
					totalTax+=invoice.totaltax;
					if(month == '01'){monthtotalTaxValue1+=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2+=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3+=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4+=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5+=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6+=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7+=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8+=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9+=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10+=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11+=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12+=invoice.totaltax;}
				}
			}else if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'C'){
						totalTax-=invoice.totaltax;
						if(month == '01'){monthtotalTaxValue1-=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2-=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3-=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4-=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5-=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6-=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7-=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8-=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9-=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10-=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11-=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12-=invoice.totaltax;}
					}else{
						totalTax+=invoice.totaltax;
						if(month == '01'){monthtotalTaxValue1+=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2+=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3+=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4+=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5+=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6+=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7+=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8+=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9+=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10+=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11+=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12+=invoice.totaltax;}
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalTax-=invoice.totaltax;
						if(month == '01'){monthtotalTaxValue1-=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2-=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3-=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4-=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5-=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6-=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7-=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8-=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9-=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10-=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11-=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12-=invoice.totaltax;}
					}else{
						totalTax+=invoice.totaltax;
						if(month == '01'){monthtotalTaxValue1+=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2+=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3+=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4+=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5+=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6+=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7+=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8+=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9+=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10+=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11+=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12+=invoice.totaltax;}
					}
				}
			}else{
				totalTax+=invoice.totaltax;
				if(month == '01'){monthtotalTaxValue1+=invoice.totaltax;}if(month == '02'){monthtotalTaxValue2+=invoice.totaltax;}if(month == '03'){monthtotalTaxValue3+=invoice.totaltax;}if(month == '04'){monthtotalTaxValue4+=invoice.totaltax;}if(month == '05'){monthtotalTaxValue5+=invoice.totaltax;}if(month == '06'){monthtotalTaxValue6+=invoice.totaltax;}if(month == '07'){monthtotalTaxValue7+=invoice.totaltax;}if(month == '08'){monthtotalTaxValue8+=invoice.totaltax;}if(month == '09'){monthtotalTaxValue9+=invoice.totaltax;}if(month == '10'){monthtotalTaxValue10+=invoice.totaltax;}if(month == '11'){monthtotalTaxValue11+=invoice.totaltax;}if(month == '12'){monthtotalTaxValue12+=invoice.totaltax;}
			}
		}
	} else {
		invoice.totaltax = 0.00;
	}
	if(invoice.totalamount) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
						if(month == '01'){monthtotalValue1-=invoice.totalamount;}if(month == '02'){monthtotalValue2-=invoice.totalamount;}if(month == '03'){monthtotalValue3-=invoice.totalamount;}if(month == '04'){monthtotalValue4-=invoice.totalamount;}if(month == '05'){monthtotalValue5-=invoice.totalamount;}if(month == '06'){monthtotalValue6-=invoice.totalamount;}if(month == '07'){monthtotalValue7-=invoice.totalamount;}if(month == '08'){monthtotalValue8-=invoice.totalamount;}if(month == '09'){monthtotalValue9-=invoice.totalamount;}if(month == '10'){monthtotalValue10-=invoice.totalamount;}if(month == '11'){monthtotalValue11-=invoice.totalamount;}if(month == '12'){monthtotalValue12-=invoice.totalamount;}
					}else{
						totalValue+=invoice.totalamount;
						if(month == '01'){monthtotalValue1+=invoice.totalamount;}if(month == '02'){monthtotalValue2+=invoice.totalamount;}if(month == '03'){monthtotalValue3+=invoice.totalamount;}if(month == '04'){monthtotalValue4+=invoice.totalamount;}if(month == '05'){monthtotalValue5+=invoice.totalamount;}if(month == '06'){monthtotalValue6+=invoice.totalamount;}if(month == '07'){monthtotalValue7+=invoice.totalamount;}if(month == '08'){monthtotalValue8+=invoice.totalamount;}if(month == '09'){monthtotalValue9+=invoice.totalamount;}if(month == '10'){monthtotalValue10+=invoice.totalamount;}if(month == '11'){monthtotalValue11+=invoice.totalamount;}if(month == '12'){monthtotalValue12+=invoice.totalamount;}
					}
			}else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalValue-=invoice.totalamount;
					if(month == '01'){monthtotalValue1-=invoice.totalamount;}if(month == '02'){monthtotalValue2-=invoice.totalamount;}if(month == '03'){monthtotalValue3-=invoice.totalamount;}if(month == '04'){monthtotalValue4-=invoice.totalamount;}if(month == '05'){monthtotalValue5-=invoice.totalamount;}if(month == '06'){monthtotalValue6-=invoice.totalamount;}if(month == '07'){monthtotalValue7-=invoice.totalamount;}if(month == '08'){monthtotalValue8-=invoice.totalamount;}if(month == '09'){monthtotalValue9-=invoice.totalamount;}if(month == '10'){monthtotalValue10-=invoice.totalamount;}if(month == '11'){monthtotalValue11-=invoice.totalamount;}if(month == '12'){monthtotalValue12-=invoice.totalamount;}
				}else{
					totalValue+=invoice.totalamount;
					if(month == '01'){monthtotalValue1+=invoice.totalamount;}if(month == '02'){monthtotalValue2+=invoice.totalamount;}if(month == '03'){monthtotalValue3+=invoice.totalamount;}if(month == '04'){monthtotalValue4+=invoice.totalamount;}if(month == '05'){monthtotalValue5+=invoice.totalamount;}if(month == '06'){monthtotalValue6+=invoice.totalamount;}if(month == '07'){monthtotalValue7+=invoice.totalamount;}if(month == '08'){monthtotalValue8+=invoice.totalamount;}if(month == '09'){monthtotalValue9+=invoice.totalamount;}if(month == '10'){monthtotalValue10+=invoice.totalamount;}if(month == '11'){monthtotalValue11+=invoice.totalamount;}if(month == '12'){monthtotalValue12+=invoice.totalamount;}
				}
			}else if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
						if(month == '01'){monthtotalValue1-=invoice.totalamount;}if(month == '02'){monthtotalValue2-=invoice.totalamount;}if(month == '03'){monthtotalValue3-=invoice.totalamount;}if(month == '04'){monthtotalValue4-=invoice.totalamount;}if(month == '05'){monthtotalValue5-=invoice.totalamount;}if(month == '06'){monthtotalValue6-=invoice.totalamount;}if(month == '07'){monthtotalValue7-=invoice.totalamount;}if(month == '08'){monthtotalValue8-=invoice.totalamount;}if(month == '09'){monthtotalValue9-=invoice.totalamount;}if(month == '10'){monthtotalValue10-=invoice.totalamount;}if(month == '11'){monthtotalValue11-=invoice.totalamount;}if(month == '12'){monthtotalValue12-=invoice.totalamount;}
					}else{
						totalValue+=invoice.totalamount;
						if(month == '01'){monthtotalValue1+=invoice.totalamount;}if(month == '02'){monthtotalValue2+=invoice.totalamount;}if(month == '03'){monthtotalValue3+=invoice.totalamount;}if(month == '04'){monthtotalValue4+=invoice.totalamount;}if(month == '05'){monthtotalValue5+=invoice.totalamount;}if(month == '06'){monthtotalValue6+=invoice.totalamount;}if(month == '07'){monthtotalValue7+=invoice.totalamount;}if(month == '08'){monthtotalValue8+=invoice.totalamount;}if(month == '09'){monthtotalValue9+=invoice.totalamount;}if(month == '10'){monthtotalValue10+=invoice.totalamount;}if(month == '11'){monthtotalValue11+=invoice.totalamount;}if(month == '12'){monthtotalValue12+=invoice.totalamount;}
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
						if(month == '01'){monthtotalValue1-=invoice.totalamount;}if(month == '02'){monthtotalValue2-=invoice.totalamount;}if(month == '03'){monthtotalValue3-=invoice.totalamount;}if(month == '04'){monthtotalValue4-=invoice.totalamount;}if(month == '05'){monthtotalValue5-=invoice.totalamount;}if(month == '06'){monthtotalValue6-=invoice.totalamount;}if(month == '07'){monthtotalValue7-=invoice.totalamount;}if(month == '08'){monthtotalValue8-=invoice.totalamount;}if(month == '09'){monthtotalValue9-=invoice.totalamount;}if(month == '10'){monthtotalValue10-=invoice.totalamount;}if(month == '11'){monthtotalValue11-=invoice.totalamount;}if(month == '12'){monthtotalValue12-=invoice.totalamount;}
					}else{
						totalValue+=invoice.totalamount;
						if(month == '01'){monthtotalValue1+=invoice.totalamount;}if(month == '02'){monthtotalValue2+=invoice.totalamount;}if(month == '03'){monthtotalValue3+=invoice.totalamount;}if(month == '04'){monthtotalValue4+=invoice.totalamount;}if(month == '05'){monthtotalValue5+=invoice.totalamount;}if(month == '06'){monthtotalValue6+=invoice.totalamount;}if(month == '07'){monthtotalValue7+=invoice.totalamount;}if(month == '08'){monthtotalValue8+=invoice.totalamount;}if(month == '09'){monthtotalValue9+=invoice.totalamount;}if(month == '10'){monthtotalValue10+=invoice.totalamount;}if(month == '11'){monthtotalValue11+=invoice.totalamount;}if(month == '12'){monthtotalValue12+=invoice.totalamount;}
					}
				}
			}else{
				totalValue+=invoice.totalamount;
				if(month == '01'){monthtotalValue1+=invoice.totalamount;}if(month == '02'){monthtotalValue2+=invoice.totalamount;}if(month == '03'){monthtotalValue3+=invoice.totalamount;}if(month == '04'){monthtotalValue4+=invoice.totalamount;}if(month == '05'){monthtotalValue5+=invoice.totalamount;}if(month == '06'){monthtotalValue6+=invoice.totalamount;}if(month == '07'){monthtotalValue7+=invoice.totalamount;}if(month == '08'){monthtotalValue8+=invoice.totalamount;}if(month == '09'){monthtotalValue9+=invoice.totalamount;}if(month == '10'){monthtotalValue10+=invoice.totalamount;}if(month == '11'){monthtotalValue11+=invoice.totalamount;}if(month == '12'){monthtotalValue12+=invoice.totalamount;}
			}
		}
	} else {
		invoice.totalamount = 0.00;
	}
	if(('<%=MasterGSTConstants.B2C%>' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0) || ('<%=MasterGSTConstants.B2CSA%>' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0)) {
		invoice.splyTy = invoice.b2cs[0].splyTy;
		invoice.ecommercegstin = invoice.b2cs[0].etin;
	}
	if(('<%=MasterGSTConstants.ADVANCES%>' == invoice.invtype && invoice.at && invoice.at.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.at && invoice.at.length > 0)) {
		invoice.splyTy = invoice.at[0].splyTy;
	}else if(('<%=MasterGSTConstants.ADVANCES%>' == invoice.invtype && invoice.txi && invoice.txi.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.txi && invoice.txi.length > 0)){
		invoice.splyTy = invoice.txi[0].splyTy;
	}
	if(('<%=MasterGSTConstants.ATPAID%>' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0) || ('<%=MasterGSTConstants.ATA%>' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0)) {
		invoice.splyTy = invoice.txpd[0].splyTy;
		invoice.invtype = 'Adv. Adjustments';
	}
	if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
		invoice.voucherNumber = invoice.cdn[0].nt[0].inum;
		invoice.ntty = invoice.cdn[0].nt[0].ntty;
		invoice.rsn = invoice.cdn[0].nt[0].rsn;
		invoice.pGst = invoice.cdn[0].nt[0].pGst;
		if(invoice.cdn[0].nt[0].idt != null){
			invoice.voucherDate = invoice.cdn[0].nt[0].idt;
		}else{
			invoice.voucherDate = '';
		}
		if(invoice.cdn[0].nt[0].ntty == 'C'){
			invoice.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			invoice.invtype = 'Debit Note';
		}
		
	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
		invoice.voucherNumber = invoice.cdnr[0].nt[0].inum;
		invoice.ntty = invoice.cdnr[0].nt[0].ntty;
		invoice.rsn = invoice.cdnr[0].nt[0].rsn;
		invoice.pGst = invoice.cdnr[0].nt[0].pGst;
		if(invoice.cdnr[0].nt[0].idt != null){
			invoice.voucherDate = invoice.cdnr[0].nt[0].idt;
		}else{
			invoice.voucherDate = '';
		}
		if(invoice.cdnr[0].nt[0].ntty == 'C'){
			invoice.invtype = 'Credit Note';
		}else if(invoice.cdnr[0].nt[0].ntty == 'D'){
			invoice.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
		invoice.voucherNumber = invoice.cdnur[0].inum;
		if(invoice.cdnur[0].idt != null){
			invoice.voucherDate = invoice.cdnur[0].idt;
		}else{
			invoice.voucherDate = '';
		}
		invoice.ntty = invoice.cdnur[0].ntty;
		invoice.rsn = invoice.cdnur[0].rsn;
		invoice.pGst = invoice.cdnur[0].pGst;
		invoice.cdnurtyp = invoice.cdnur[0].typ;
		if(invoice.cdnur[0].ntty == 'C'){
			invoice.invtype = 'Credit Note(UR)';
		}else if(invoice.cdnur[0].ntty == 'D'){
			invoice.invtype = 'Debit Note(UR)';
		}
	}
	if(('<%=MasterGSTConstants.EXPORTS%>' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0) || ('<%=MasterGSTConstants.EXPA%>' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0)) {
		invoice.portcode = invoice.exp[0].inv[0].sbpcode;
		invoice.shippingBillNumber = invoice.exp[0].inv[0].sbnum;
		invoice.exportType = invoice.exp[0].expTyp;
		if(invoice.exp[0].inv[0].sbdt != null && invoice.exp[0].inv[0].sbdt != undefined){
			invoice.shippingBillDate = formatDate(invoice.exp[0].inv[0].sbdt);
		}else{
			invoice.shippingBillDate = '';
		}
	}
	if(('<%=MasterGSTConstants.ISD%>' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0) || ('<%=MasterGSTConstants.ISD%>' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0)) {
		invoice.isdDocty = invoice.isd[0].doclist[0].isdDocty;
		invoice.documentNumber = invoice.isd[0].doclist[0].docnum;
		if(invoice.isd[0].doclist[0].docdt != null && invoice.isd[0].doclist[0].docdt != undefined){
			invoice.documentDate = formatDate(invoice.isd[0].doclist[0].docdt);
		}else{
			invoice.documentDate = '';
		}
	}
	if('<%=MasterGSTConstants.IMP_GOODS%>' == invoice.invtype && invoice.impGoods && invoice.impGoods.length > 0) {
		invoice.isSez = invoice.impGoods[0].isSez;
		invoice.stin = invoice.impGoods[0].stin;
		invoice.impBillNumber = invoice.impGoods[0].boeNum;
		if(invoice.impGoods[0].boeDt != null){
			invoice.impBillDate = formatDate(invoice.impGoods[0].boeDt);
		}else{
			invoice.impBillDate = '';
		}
		invoice.boeVal = invoice.impGoods[0].boeVal;
		invoice.impPortcode = invoice.impGoods[0].portCode;
	}
	
	if(invoice.items) {
		invoice.items.forEach(function(item) {
			if(item.rate == null) {
				if(item.igstrate) {
					item.rate = item.igstrate;
				} else if(item.cgstrate) {
					item.rate = 2*item.cgstrate;
				}
			}
			if(item.hsn) {
				item.code = item.hsn;
				if(item.hsn.indexOf(':') > 0) {
					item.hsn=item.hsn.substring(0,item.hsn.indexOf(':'));
				}
			}
			
			if(item.exmepted != null && item.quantity != null){
				if(invoice.gstStatus != 'CANCELLED'){
					
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
						if(invoice.cdnr[0].nt[0].ntty == 'C'){
							totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							if(month == '01'){monthtotalExemValue1-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '02'){monthtotalExemValue2-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '03'){monthtotalExemValue3-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '04'){monthtotalExemValue4-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '05'){monthtotalExemValue5-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '06'){monthtotalExemValue6-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '07'){monthtotalExemValue7-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '08'){monthtotalExemValue8-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '09'){monthtotalExemValue9-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '10'){monthtotalExemValue10-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '11'){monthtotalExemValue11-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '12'){monthtotalTaxValue12-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}
						}else{
							totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							if(month == '01'){monthtotalExemValue1+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '02'){monthtotalExemValue2+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '03'){monthtotalExemValue3+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '04'){monthtotalExemValue4+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '05'){monthtotalExemValue5+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '06'){monthtotalExemValue6+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '07'){monthtotalExemValue7+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '08'){monthtotalExemValue8+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '09'){monthtotalExemValue9+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '10'){monthtotalExemValue10+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '11'){monthtotalExemValue11+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '12'){monthtotalTaxValue12+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(invoice.cdnur[0].ntty == 'C'){
							totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							if(month == '01'){monthtotalExemValue1-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '02'){monthtotalExemValue2-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '03'){monthtotalExemValue3-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '04'){monthtotalExemValue4-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '05'){monthtotalExemValue5-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '06'){monthtotalExemValue6-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '07'){monthtotalExemValue7-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '08'){monthtotalExemValue8-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '09'){monthtotalExemValue9-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '10'){monthtotalExemValue10-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '11'){monthtotalExemValue11-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '12'){monthtotalTaxValue12-=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}
						}else{
							totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							if(month == '01'){monthtotalExemValue1+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '02'){monthtotalExemValue2+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '03'){monthtotalExemValue3+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '04'){monthtotalExemValue4+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '05'){monthtotalExemValue5+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '06'){monthtotalExemValue6+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '07'){monthtotalExemValue7+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '08'){monthtotalExemValue8+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '09'){monthtotalExemValue9+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '10'){monthtotalExemValue10+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '11'){monthtotalExemValue11+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '12'){monthtotalTaxValue12+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}
						}
					}else{
						totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						if(month == '01'){monthtotalExemValue1+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '02'){monthtotalExemValue2+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '03'){monthtotalExemValue3+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '04'){monthtotalExemValue4+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '05'){monthtotalExemValue5+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '06'){monthtotalExemValue6+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '07'){monthtotalExemValue7+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '08'){monthtotalExemValue8+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '09'){monthtotalExemValue9+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '10'){monthtotalExemValue10+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '11'){monthtotalExemValue11+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}if(month == '12'){monthtotalTaxValue12+=(parseFloat(item.quantity))*(parseFloat(item.exmepted));}
					}
					
					
				}
			}
			
			if(item.igstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
						if(invoice.cdn[0].nt[0].ntty == 'C'){
							totalIGST -= item.igstamount;
							totalIGST1 -= item.igstamount;
							if(month == '01'){monthtotalIgstValue1-=item.igstamount;}if(month == '02'){monthtotalIgstValue2-=item.igstamount;}if(month == '03'){monthtotalIgstValue3-=item.igstamount;}if(month == '04'){monthtotalIgstValue4-=item.igstamount;}if(month == '05'){monthtotalIgstValue5-=item.igstamount;}if(month == '06'){monthtotalIgstValue6-=item.igstamount;}if(month == '07'){monthtotalIgstValue7-=item.igstamount;}if(month == '08'){monthtotalIgstValue8-=item.igstamount;}if(month == '09'){monthtotalIgstValue9-=item.igstamount;}if(month == '10'){monthtotalIgstValue10-=item.igstamount;}if(month == '11'){monthtotalIgstValue11-=item.igstamount;}if(month == '12'){monthtotalIgstValue12-=item.igstamount;}
						}else{
							totalIGST+=item.igstamount;
							totalIGST1 +=item.igstamount;
							if(month == '01'){monthtotalIgstValue1+=item.igstamount;}if(month == '02'){monthtotalIgstValue2+=item.igstamount;}if(month == '03'){monthtotalIgstValue3+=item.igstamount;}if(month == '04'){monthtotalIgstValue4+=item.igstamount;}if(month == '05'){monthtotalIgstValue5+=item.igstamount;}if(month == '06'){monthtotalIgstValue6+=item.igstamount;}if(month == '07'){monthtotalIgstValue7+=item.igstamount;}if(month == '08'){monthtotalIgstValue8+=item.igstamount;}if(month == '09'){monthtotalIgstValue9+=item.igstamount;}if(month == '10'){monthtotalIgstValue10+=item.igstamount;}if(month == '11'){monthtotalIgstValue11+=item.igstamount;}if(month == '12'){monthtotalIgstValue12+=item.igstamount;}
						}
					}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
						if(invoice.cdnr[0].nt[0].ntty == 'C'){
							totalIGST -= item.igstamount;
							totalIGST1 -= item.igstamount;
							if(month == '01'){monthtotalIgstValue1-=item.igstamount;}if(month == '02'){monthtotalIgstValue2-=item.igstamount;}if(month == '03'){monthtotalIgstValue3-=item.igstamount;}if(month == '04'){monthtotalIgstValue4-=item.igstamount;}if(month == '05'){monthtotalIgstValue5-=item.igstamount;}if(month == '06'){monthtotalIgstValue6-=item.igstamount;}if(month == '07'){monthtotalIgstValue7-=item.igstamount;}if(month == '08'){monthtotalIgstValue8-=item.igstamount;}if(month == '09'){monthtotalIgstValue9-=item.igstamount;}if(month == '10'){monthtotalIgstValue10-=item.igstamount;}if(month == '11'){monthtotalIgstValue11-=item.igstamount;}if(month == '12'){monthtotalIgstValue12-=item.igstamount;}
						}else{
							totalIGST+=item.igstamount;
							totalIGST1 +=item.igstamount;
							if(month == '01'){monthtotalIgstValue1+=item.igstamount;}if(month == '02'){monthtotalIgstValue2+=item.igstamount;}if(month == '03'){monthtotalIgstValue3+=item.igstamount;}if(month == '04'){monthtotalIgstValue4+=item.igstamount;}if(month == '05'){monthtotalIgstValue5+=item.igstamount;}if(month == '06'){monthtotalIgstValue6+=item.igstamount;}if(month == '07'){monthtotalIgstValue7+=item.igstamount;}if(month == '08'){monthtotalIgstValue8+=item.igstamount;}if(month == '09'){monthtotalIgstValue9+=item.igstamount;}if(month == '10'){monthtotalIgstValue10+=item.igstamount;}if(month == '11'){monthtotalIgstValue11+=item.igstamount;}if(month == '12'){monthtotalIgstValue12+=item.igstamount;}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'C'){
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
								if(month == '01'){monthtotalIgstValue1-=item.igstamount;}if(month == '02'){monthtotalIgstValue2-=item.igstamount;}if(month == '03'){monthtotalIgstValue3-=item.igstamount;}if(month == '04'){monthtotalIgstValue4-=item.igstamount;}if(month == '05'){monthtotalIgstValue5-=item.igstamount;}if(month == '06'){monthtotalIgstValue6-=item.igstamount;}if(month == '07'){monthtotalIgstValue7-=item.igstamount;}if(month == '08'){monthtotalIgstValue8-=item.igstamount;}if(month == '09'){monthtotalIgstValue9-=item.igstamount;}if(month == '10'){monthtotalIgstValue10-=item.igstamount;}if(month == '11'){monthtotalIgstValue11-=item.igstamount;}if(month == '12'){monthtotalIgstValue12-=item.igstamount;}
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
								if(month == '01'){monthtotalIgstValue1+=item.igstamount;}if(month == '02'){monthtotalIgstValue2+=item.igstamount;}if(month == '03'){monthtotalIgstValue3+=item.igstamount;}if(month == '04'){monthtotalIgstValue4+=item.igstamount;}if(month == '05'){monthtotalIgstValue5+=item.igstamount;}if(month == '06'){monthtotalIgstValue6+=item.igstamount;}if(month == '07'){monthtotalIgstValue7+=item.igstamount;}if(month == '08'){monthtotalIgstValue8+=item.igstamount;}if(month == '09'){monthtotalIgstValue9+=item.igstamount;}if(month == '10'){monthtotalIgstValue10+=item.igstamount;}if(month == '11'){monthtotalIgstValue11+=item.igstamount;}if(month == '12'){monthtotalIgstValue12+=item.igstamount;}
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
								if(month == '01'){monthtotalIgstValue1-=item.igstamount;}if(month == '02'){monthtotalIgstValue2-=item.igstamount;}if(month == '03'){monthtotalIgstValue3-=item.igstamount;}if(month == '04'){monthtotalIgstValue4-=item.igstamount;}if(month == '05'){monthtotalIgstValue5-=item.igstamount;}if(month == '06'){monthtotalIgstValue6-=item.igstamount;}if(month == '07'){monthtotalIgstValue7-=item.igstamount;}if(month == '08'){monthtotalIgstValue8-=item.igstamount;}if(month == '09'){monthtotalIgstValue9-=item.igstamount;}if(month == '10'){monthtotalIgstValue10-=item.igstamount;}if(month == '11'){monthtotalIgstValue11-=item.igstamount;}if(month == '12'){monthtotalIgstValue12-=item.igstamount;}
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
								if(month == '01'){monthtotalIgstValue1+=item.igstamount;}if(month == '02'){monthtotalIgstValue2+=item.igstamount;}if(month == '03'){monthtotalIgstValue3+=item.igstamount;}if(month == '04'){monthtotalIgstValue4+=item.igstamount;}if(month == '05'){monthtotalIgstValue5+=item.igstamount;}if(month == '06'){monthtotalIgstValue6+=item.igstamount;}if(month == '07'){monthtotalIgstValue7+=item.igstamount;}if(month == '08'){monthtotalIgstValue8+=item.igstamount;}if(month == '09'){monthtotalIgstValue9+=item.igstamount;}if(month == '10'){monthtotalIgstValue10+=item.igstamount;}if(month == '11'){monthtotalIgstValue11+=item.igstamount;}if(month == '12'){monthtotalIgstValue12+=item.igstamount;}
							}
						}
					}else{
						totalIGST+=item.igstamount;
						totalIGST1 +=item.igstamount;
						if(month == '01'){monthtotalIgstValue1+=item.igstamount;}if(month == '02'){monthtotalIgstValue2+=item.igstamount;}if(month == '03'){monthtotalIgstValue3+=item.igstamount;}if(month == '04'){monthtotalIgstValue4+=item.igstamount;}if(month == '05'){monthtotalIgstValue5+=item.igstamount;}if(month == '06'){monthtotalIgstValue6+=item.igstamount;}if(month == '07'){monthtotalIgstValue7+=item.igstamount;}if(month == '08'){monthtotalIgstValue8+=item.igstamount;}if(month == '09'){monthtotalIgstValue9+=item.igstamount;}if(month == '10'){monthtotalIgstValue10+=item.igstamount;}if(month == '11'){monthtotalIgstValue11+=item.igstamount;}if(month == '12'){monthtotalIgstValue12+=item.igstamount;}
					}
				}
			} else {
				item.igstamount = 0.00;
			}
			if(item.cgstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
						if(invoice.cdn[0].nt[0].ntty == 'C'){
							totalCGST-=item.cgstamount;
							totalCGST1 -=item.cgstamount;
							if(month == '01'){monthtotalCgstValue1-=item.cgstamount;}if(month == '02'){monthtotalCgstValue2-=item.cgstamount;}if(month == '03'){monthtotalCgstValue3-=item.cgstamount;}if(month == '04'){monthtotalCgstValue4-=item.cgstamount;}if(month == '05'){monthtotalCgstValue5-=item.cgstamount;}if(month == '06'){monthtotalCgstValue6-=item.cgstamount;}if(month == '07'){monthtotalCgstValue7-=item.cgstamount;}if(month == '08'){monthtotalCgstValue8-=item.cgstamount;}if(month == '09'){monthtotalCgstValue9-=item.cgstamount;}if(month == '10'){monthtotalCgstValue10-=item.cgstamount;}if(month == '11'){monthtotalCgstValue11-=item.cgstamount;}if(month == '12'){monthtotalCgstValue12-=item.cgstamount;}
						}else{
							totalCGST+=item.cgstamount;
							totalCGST1 +=item.cgstamount;
							if(month == '01'){monthtotalCgstValue1+=item.cgstamount;}if(month == '02'){monthtotalCgstValue2+=item.cgstamount;}if(month == '03'){monthtotalCgstValue3+=item.cgstamount;}if(month == '04'){monthtotalCgstValue4+=item.cgstamount;}if(month == '05'){monthtotalCgstValue5+=item.cgstamount;}if(month == '06'){monthtotalCgstValue6+=item.cgstamount;}if(month == '07'){monthtotalCgstValue7+=item.cgstamount;}if(month == '08'){monthtotalCgstValue8+=item.cgstamount;}if(month == '09'){monthtotalCgstValue9+=item.cgstamount;}if(month == '10'){monthtotalCgstValue10+=item.cgstamount;}if(month == '11'){monthtotalCgstValue11+=item.cgstamount;}if(month == '12'){monthtotalCgstValue12+=item.cgstamount;}
						}
					}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
						if(invoice.cdnr[0].nt[0].ntty == 'C'){
							totalCGST-=item.cgstamount;
							totalCGST1 -=item.cgstamount;
							if(month == '01'){monthtotalCgstValue1-=item.cgstamount;}if(month == '02'){monthtotalCgstValue2-=item.cgstamount;}if(month == '03'){monthtotalCgstValue3-=item.cgstamount;}if(month == '04'){monthtotalCgstValue4-=item.cgstamount;}if(month == '05'){monthtotalCgstValue5-=item.cgstamount;}if(month == '06'){monthtotalCgstValue6-=item.cgstamount;}if(month == '07'){monthtotalCgstValue7-=item.cgstamount;}if(month == '08'){monthtotalCgstValue8-=item.cgstamount;}if(month == '09'){monthtotalCgstValue9-=item.cgstamount;}if(month == '10'){monthtotalCgstValue10-=item.cgstamount;}if(month == '11'){monthtotalCgstValue11-=item.cgstamount;}if(month == '12'){monthtotalCgstValue12-=item.cgstamount;}
						}else{
							totalCGST+=item.cgstamount;
							totalCGST1 +=item.cgstamount;
							if(month == '01'){monthtotalCgstValue1+=item.cgstamount;}if(month == '02'){monthtotalCgstValue2+=item.cgstamount;}if(month == '03'){monthtotalCgstValue3+=item.cgstamount;}if(month == '04'){monthtotalCgstValue4+=item.cgstamount;}if(month == '05'){monthtotalCgstValue5+=item.cgstamount;}if(month == '06'){monthtotalCgstValue6+=item.cgstamount;}if(month == '07'){monthtotalCgstValue7+=item.cgstamount;}if(month == '08'){monthtotalCgstValue8+=item.cgstamount;}if(month == '09'){monthtotalCgstValue9+=item.cgstamount;}if(month == '10'){monthtotalCgstValue10+=item.cgstamount;}if(month == '11'){monthtotalCgstValue11+=item.cgstamount;}if(month == '12'){monthtotalCgstValue12+=item.cgstamount;}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
								if(month == '01'){monthtotalCgstValue1-=item.cgstamount;}if(month == '02'){monthtotalCgstValue2-=item.cgstamount;}if(month == '03'){monthtotalCgstValue3-=item.cgstamount;}if(month == '04'){monthtotalCgstValue4-=item.cgstamount;}if(month == '05'){monthtotalCgstValue5-=item.cgstamount;}if(month == '06'){monthtotalCgstValue6-=item.cgstamount;}if(month == '07'){monthtotalCgstValue7-=item.cgstamount;}if(month == '08'){monthtotalCgstValue8-=item.cgstamount;}if(month == '09'){monthtotalCgstValue9-=item.cgstamount;}if(month == '10'){monthtotalCgstValue10-=item.cgstamount;}if(month == '11'){monthtotalCgstValue11-=item.cgstamount;}if(month == '12'){monthtotalCgstValue12-=item.cgstamount;}
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
								if(month == '01'){monthtotalCgstValue1+=item.cgstamount;}if(month == '02'){monthtotalCgstValue2+=item.cgstamount;}if(month == '03'){monthtotalCgstValue3+=item.cgstamount;}if(month == '04'){monthtotalCgstValue4+=item.cgstamount;}if(month == '05'){monthtotalCgstValue5+=item.cgstamount;}if(month == '06'){monthtotalCgstValue6+=item.cgstamount;}if(month == '07'){monthtotalCgstValue7+=item.cgstamount;}if(month == '08'){monthtotalCgstValue8+=item.cgstamount;}if(month == '09'){monthtotalCgstValue9+=item.cgstamount;}if(month == '10'){monthtotalCgstValue10+=item.cgstamount;}if(month == '11'){monthtotalCgstValue11+=item.cgstamount;}if(month == '12'){monthtotalCgstValue12+=item.cgstamount;}
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
								if(month == '01'){monthtotalCgstValue1-=item.cgstamount;}if(month == '02'){monthtotalCgstValue2-=item.cgstamount;}if(month == '03'){monthtotalCgstValue3-=item.cgstamount;}if(month == '04'){monthtotalCgstValue4-=item.cgstamount;}if(month == '05'){monthtotalCgstValue5-=item.cgstamount;}if(month == '06'){monthtotalCgstValue6-=item.cgstamount;}if(month == '07'){monthtotalCgstValue7-=item.cgstamount;}if(month == '08'){monthtotalCgstValue8-=item.cgstamount;}if(month == '09'){monthtotalCgstValue9-=item.cgstamount;}if(month == '10'){monthtotalCgstValue10-=item.cgstamount;}if(month == '11'){monthtotalCgstValue11-=item.cgstamount;}if(month == '12'){monthtotalCgstValue12-=item.cgstamount;}
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
								if(month == '01'){monthtotalCgstValue1+=item.cgstamount;}if(month == '02'){monthtotalCgstValue2+=item.cgstamount;}if(month == '03'){monthtotalCgstValue3+=item.cgstamount;}if(month == '04'){monthtotalCgstValue4+=item.cgstamount;}if(month == '05'){monthtotalCgstValue5+=item.cgstamount;}if(month == '06'){monthtotalCgstValue6+=item.cgstamount;}if(month == '07'){monthtotalCgstValue7+=item.cgstamount;}if(month == '08'){monthtotalCgstValue8+=item.cgstamount;}if(month == '09'){monthtotalCgstValue9+=item.cgstamount;}if(month == '10'){monthtotalCgstValue10+=item.cgstamount;}if(month == '11'){monthtotalCgstValue11+=item.cgstamount;}if(month == '12'){monthtotalCgstValue12+=item.cgstamount;}
							}
						}
					}else{
						totalCGST+=item.cgstamount;
						totalCGST1 +=item.cgstamount;
						if(month == '01'){monthtotalCgstValue1+=item.cgstamount;}if(month == '02'){monthtotalCgstValue2+=item.cgstamount;}if(month == '03'){monthtotalCgstValue3+=item.cgstamount;}if(month == '04'){monthtotalCgstValue4+=item.cgstamount;}if(month == '05'){monthtotalCgstValue5+=item.cgstamount;}if(month == '06'){monthtotalCgstValue6+=item.cgstamount;}if(month == '07'){monthtotalCgstValue7+=item.cgstamount;}if(month == '08'){monthtotalCgstValue8+=item.cgstamount;}if(month == '09'){monthtotalCgstValue9+=item.cgstamount;}if(month == '10'){monthtotalCgstValue10+=item.cgstamount;}if(month == '11'){monthtotalCgstValue11+=item.cgstamount;}if(month == '12'){monthtotalCgstValue12+=item.cgstamount;}
					}
				}
			} else {
				item.cgstamount = 0.00;
			}
			if(item.sgstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
							if(invoice.cdn[0].nt[0].ntty == 'C'){
								totalSGST-=item.sgstamount;
								totalSGST1 -=item.sgstamount;
								if(month == '01'){monthtotalSgstValue1-=item.sgstamount;}if(month == '02'){monthtotalSgstValue2-=item.sgstamount;}if(month == '03'){monthtotalSgstValue3-=item.sgstamount;}if(month == '04'){monthtotalSgstValue4-=item.sgstamount;}if(month == '05'){monthtotalSgstValue5-=item.sgstamount;}if(month == '06'){monthtotalSgstValue6-=item.sgstamount;}if(month == '07'){monthtotalSgstValue7-=item.sgstamount;}if(month == '08'){monthtotalSgstValue8-=item.sgstamount;}if(month == '09'){monthtotalSgstValue9-=item.sgstamount;}if(month == '10'){monthtotalSgstValue10-=item.sgstamount;}if(month == '11'){monthtotalSgstValue11-=item.sgstamount;}if(month == '12'){monthtotalSgstValue12-=item.sgstamount;}
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
								if(month == '01'){monthtotalSgstValue1+=item.sgstamount;}if(month == '02'){monthtotalSgstValue2+=item.sgstamount;}if(month == '03'){monthtotalSgstValue3+=item.sgstamount;}if(month == '04'){monthtotalSgstValue4+=item.sgstamount;}if(month == '05'){monthtotalSgstValue5+=item.sgstamount;}if(month == '06'){monthtotalSgstValue6+=item.sgstamount;}if(month == '07'){monthtotalSgstValue7+=item.sgstamount;}if(month == '08'){monthtotalSgstValue8+=item.sgstamount;}if(month == '09'){monthtotalSgstValue9+=item.sgstamount;}if(month == '10'){monthtotalSgstValue10+=item.sgstamount;}if(month == '11'){monthtotalSgstValue11+=item.sgstamount;}if(month == '12'){monthtotalSgstValue12+=item.sgstamount;}
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalSGST -= item.sgstamount;
								totalSGST1 -=item.sgstamount;
								if(month == '01'){monthtotalSgstValue1-=item.sgstamount;}if(month == '02'){monthtotalSgstValue2-=item.sgstamount;}if(month == '03'){monthtotalSgstValue3-=item.sgstamount;}if(month == '04'){monthtotalSgstValue4-=item.sgstamount;}if(month == '05'){monthtotalSgstValue5-=item.sgstamount;}if(month == '06'){monthtotalSgstValue6-=item.sgstamount;}if(month == '07'){monthtotalSgstValue7-=item.sgstamount;}if(month == '08'){monthtotalSgstValue8-=item.sgstamount;}if(month == '09'){monthtotalSgstValue9-=item.sgstamount;}if(month == '10'){monthtotalSgstValue10-=item.sgstamount;}if(month == '11'){monthtotalSgstValue11-=item.sgstamount;}if(month == '12'){monthtotalSgstValue12-=item.sgstamount;}
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
								if(month == '01'){monthtotalSgstValue1+=item.sgstamount;}if(month == '02'){monthtotalSgstValue2+=item.sgstamount;}if(month == '03'){monthtotalSgstValue3+=item.sgstamount;}if(month == '04'){monthtotalSgstValue4+=item.sgstamount;}if(month == '05'){monthtotalSgstValue5+=item.sgstamount;}if(month == '06'){monthtotalSgstValue6+=item.sgstamount;}if(month == '07'){monthtotalSgstValue7+=item.sgstamount;}if(month == '08'){monthtotalSgstValue8+=item.sgstamount;}if(month == '09'){monthtotalSgstValue9+=item.sgstamount;}if(month == '10'){monthtotalSgstValue10+=item.sgstamount;}if(month == '11'){monthtotalSgstValue11+=item.sgstamount;}if(month == '12'){monthtotalSgstValue12+=item.sgstamount;}
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'C'){
								totalSGST-=item.sgstamount;
								totalSGST1 -=item.sgstamount;
								if(month == '01'){monthtotalSgstValue1-=item.sgstamount;}if(month == '02'){monthtotalSgstValue2-=item.sgstamount;}if(month == '03'){monthtotalSgstValue3-=item.sgstamount;}if(month == '04'){monthtotalSgstValue4-=item.sgstamount;}if(month == '05'){monthtotalSgstValue5-=item.sgstamount;}if(month == '06'){monthtotalSgstValue6-=item.sgstamount;}if(month == '07'){monthtotalSgstValue7-=item.sgstamount;}if(month == '08'){monthtotalSgstValue8-=item.sgstamount;}if(month == '09'){monthtotalSgstValue9-=item.sgstamount;}if(month == '10'){monthtotalSgstValue10-=item.sgstamount;}if(month == '11'){monthtotalSgstValue11-=item.sgstamount;}if(month == '12'){monthtotalSgstValue12-=item.sgstamount;}
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
								if(month == '01'){monthtotalSgstValue1+=item.sgstamount;}if(month == '02'){monthtotalSgstValue2+=item.sgstamount;}if(month == '03'){monthtotalSgstValue3+=item.sgstamount;}if(month == '04'){monthtotalSgstValue4+=item.sgstamount;}if(month == '05'){monthtotalSgstValue5+=item.sgstamount;}if(month == '06'){monthtotalSgstValue6+=item.sgstamount;}if(month == '07'){monthtotalSgstValue7+=item.sgstamount;}if(month == '08'){monthtotalSgstValue8+=item.sgstamount;}if(month == '09'){monthtotalSgstValue9+=item.sgstamount;}if(month == '10'){monthtotalSgstValue10+=item.sgstamount;}if(month == '11'){monthtotalSgstValue11+=item.sgstamount;}if(month == '12'){monthtotalSgstValue12+=item.sgstamount;}
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalSGST-=item.sgstamount;
								totalSGST1 -=item.sgstamount;
								if(month == '01'){monthtotalSgstValue1-=item.sgstamount;}if(month == '02'){monthtotalSgstValue2-=item.sgstamount;}if(month == '03'){monthtotalSgstValue3-=item.sgstamount;}if(month == '04'){monthtotalSgstValue4-=item.sgstamount;}if(month == '05'){monthtotalSgstValue5-=item.sgstamount;}if(month == '06'){monthtotalSgstValue6-=item.sgstamount;}if(month == '07'){monthtotalSgstValue7-=item.sgstamount;}if(month == '08'){monthtotalSgstValue8-=item.sgstamount;}if(month == '09'){monthtotalSgstValue9-=item.sgstamount;}if(month == '10'){monthtotalSgstValue10-=item.sgstamount;}if(month == '11'){monthtotalSgstValue11-=item.sgstamount;}if(month == '12'){monthtotalSgstValue12-=item.sgstamount;}
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
								if(month == '01'){monthtotalSgstValue1+=item.sgstamount;}if(month == '02'){monthtotalSgstValue2+=item.sgstamount;}if(month == '03'){monthtotalSgstValue3+=item.sgstamount;}if(month == '04'){monthtotalSgstValue4+=item.sgstamount;}if(month == '05'){monthtotalSgstValue5+=item.sgstamount;}if(month == '06'){monthtotalSgstValue6+=item.sgstamount;}if(month == '07'){monthtotalSgstValue7+=item.sgstamount;}if(month == '08'){monthtotalSgstValue8+=item.sgstamount;}if(month == '09'){monthtotalSgstValue9+=item.sgstamount;}if(month == '10'){monthtotalSgstValue10+=item.sgstamount;}if(month == '11'){monthtotalSgstValue11+=item.sgstamount;}if(month == '12'){monthtotalSgstValue12+=item.sgstamount;}
							}
						}
						}else{
							totalSGST+=item.sgstamount;
							totalSGST1 +=item.sgstamount;
							if(month == '01'){monthtotalSgstValue1+=item.sgstamount;}if(month == '02'){monthtotalSgstValue2+=item.sgstamount;}if(month == '03'){monthtotalSgstValue3+=item.sgstamount;}if(month == '04'){monthtotalSgstValue4+=item.sgstamount;}if(month == '05'){monthtotalSgstValue5+=item.sgstamount;}if(month == '06'){monthtotalSgstValue6+=item.sgstamount;}if(month == '07'){monthtotalSgstValue7+=item.sgstamount;}if(month == '08'){monthtotalSgstValue8+=item.sgstamount;}if(month == '09'){monthtotalSgstValue9+=item.sgstamount;}if(month == '10'){monthtotalSgstValue10+=item.sgstamount;}if(month == '11'){monthtotalSgstValue11+=item.sgstamount;}if(month == '12'){monthtotalSgstValue12+=item.sgstamount;}
						}
				}
			} else {
				item.sgstamount = 0.00;
			}
			if(item.cessamount) {
				if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
							if(invoice.cdn[0].nt[0].ntty == 'C'){
								totalCESS-=item.cessamount;
								totalCESS1 -=item.cessamount;
								if(month == '01'){monthtotalCessValue1-=item.cessamount;}if(month == '02'){monthtotalCessValue2-=item.cessamount;}if(month == '03'){monthtotalCessValue3-=item.cessamount;}if(month == '04'){monthtotalCessValue4-=item.cessamount;}if(month == '05'){monthtotalCessValue5-=item.cessamount;}if(month == '06'){monthtotalCessValue6-=item.cessamount;}if(month == '07'){monthtotalCessValue7-=item.cessamount;}if(month == '08'){monthtotalCessValue8-=item.cessamount;}if(month == '09'){monthtotalCessValue9-=item.cessamount;}if(month == '10'){monthtotalCessValue10-=item.cessamount;}if(month == '11'){monthtotalCessValue11-=item.cessamount;}if(month == '12'){monthtotalCessValue12-=item.cessamount;}
							}else{
								totalCESS+=item.cessamount;
								totalCESS1 +=item.cessamount;
								if(month == '01'){monthtotalCessValue1+=item.cessamount;}if(month == '02'){monthtotalCessValue2+=item.cessamount;}if(month == '03'){monthtotalCessValue3+=item.cessamount;}if(month == '04'){monthtotalCessValue4+=item.cessamount;}if(month == '05'){monthtotalCessValue5+=item.cessamount;}if(month == '06'){monthtotalCessValue6+=item.cessamount;}if(month == '07'){monthtotalCessValue7+=item.cessamount;}if(month == '08'){monthtotalCessValue8+=item.cessamount;}if(month == '09'){monthtotalCessValue9+=item.cessamount;}if(month == '10'){monthtotalCessValue10+=item.cessamount;}if(month == '11'){monthtotalCessValue11+=item.cessamount;}if(month == '12'){monthtotalCessValue12+=item.cessamount;}
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalCESS-=item.cessamount;
								totalCESS1 -=item.cessamount;
								if(month == '01'){monthtotalCessValue1-=item.cessamount;}if(month == '02'){monthtotalCessValue2-=item.cessamount;}if(month == '03'){monthtotalCessValue3-=item.cessamount;}if(month == '04'){monthtotalCessValue4-=item.cessamount;}if(month == '05'){monthtotalCessValue5-=item.cessamount;}if(month == '06'){monthtotalCessValue6-=item.cessamount;}if(month == '07'){monthtotalCessValue7-=item.cessamount;}if(month == '08'){monthtotalCessValue8-=item.cessamount;}if(month == '09'){monthtotalCessValue9-=item.cessamount;}if(month == '10'){monthtotalCessValue10-=item.cessamount;}if(month == '11'){monthtotalCessValue11-=item.cessamount;}if(month == '12'){monthtotalCessValue12-=item.cessamount;}
							}else{
								totalCESS+=item.cessamount;
								totalCESS1 +=item.cessamount;
								if(month == '01'){monthtotalCessValue1+=item.cessamount;}if(month == '02'){monthtotalCessValue2+=item.cessamount;}if(month == '03'){monthtotalCessValue3+=item.cessamount;}if(month == '04'){monthtotalCessValue4+=item.cessamount;}if(month == '05'){monthtotalCessValue5+=item.cessamount;}if(month == '06'){monthtotalCessValue6+=item.cessamount;}if(month == '07'){monthtotalCessValue7+=item.cessamount;}if(month == '08'){monthtotalCessValue8+=item.cessamount;}if(month == '09'){monthtotalCessValue9+=item.cessamount;}if(month == '10'){monthtotalCessValue10+=item.cessamount;}if(month == '11'){monthtotalCessValue11+=item.cessamount;}if(month == '12'){monthtotalCessValue12+=item.cessamount;}
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
									if(month == '01'){monthtotalCessValue1-=item.cessamount;}if(month == '02'){monthtotalCessValue2-=item.cessamount;}if(month == '03'){monthtotalCessValue3-=item.cessamount;}if(month == '04'){monthtotalCessValue4-=item.cessamount;}if(month == '05'){monthtotalCessValue5-=item.cessamount;}if(month == '06'){monthtotalCessValue6-=item.cessamount;}if(month == '07'){monthtotalCessValue7-=item.cessamount;}if(month == '08'){monthtotalCessValue8-=item.cessamount;}if(month == '09'){monthtotalCessValue9-=item.cessamount;}if(month == '10'){monthtotalCessValue10-=item.cessamount;}if(month == '11'){monthtotalCessValue11-=item.cessamount;}if(month == '12'){monthtotalCessValue12-=item.cessamount;}
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
									if(month == '01'){monthtotalCessValue1+=item.cessamount;}if(month == '02'){monthtotalCessValue2+=item.cessamount;}if(month == '03'){monthtotalCessValue3+=item.cessamount;}if(month == '04'){monthtotalCessValue4+=item.cessamount;}if(month == '05'){monthtotalCessValue5+=item.cessamount;}if(month == '06'){monthtotalCessValue6+=item.cessamount;}if(month == '07'){monthtotalCessValue7+=item.cessamount;}if(month == '08'){monthtotalCessValue8+=item.cessamount;}if(month == '09'){monthtotalCessValue9+=item.cessamount;}if(month == '10'){monthtotalCessValue10+=item.cessamount;}if(month == '11'){monthtotalCessValue11+=item.cessamount;}if(month == '12'){monthtotalCessValue12+=item.cessamount;}
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
									if(month == '01'){monthtotalCessValue1-=item.cessamount;}if(month == '02'){monthtotalCessValue2-=item.cessamount;}if(month == '03'){monthtotalCessValue3-=item.cessamount;}if(month == '04'){monthtotalCessValue4-=item.cessamount;}if(month == '05'){monthtotalCessValue5-=item.cessamount;}if(month == '06'){monthtotalCessValue6-=item.cessamount;}if(month == '07'){monthtotalCessValue7-=item.cessamount;}if(month == '08'){monthtotalCessValue8-=item.cessamount;}if(month == '09'){monthtotalCessValue9-=item.cessamount;}if(month == '10'){monthtotalCessValue10-=item.cessamount;}if(month == '11'){monthtotalCessValue11-=item.cessamount;}if(month == '12'){monthtotalCessValue12-=item.cessamount;}
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
									if(month == '01'){monthtotalCessValue1+=item.cessamount;}if(month == '02'){monthtotalCessValue2+=item.cessamount;}if(month == '03'){monthtotalCessValue3+=item.cessamount;}if(month == '04'){monthtotalCessValue4+=item.cessamount;}if(month == '05'){monthtotalCessValue5+=item.cessamount;}if(month == '06'){monthtotalCessValue6+=item.cessamount;}if(month == '07'){monthtotalCessValue7+=item.cessamount;}if(month == '08'){monthtotalCessValue8+=item.cessamount;}if(month == '09'){monthtotalCessValue9+=item.cessamount;}if(month == '10'){monthtotalCessValue10+=item.cessamount;}if(month == '11'){monthtotalCessValue11+=item.cessamount;}if(month == '12'){monthtotalCessValue12+=item.cessamount;}
								}
							}
						}else{
							totalCESS+=item.cessamount;
							totalCESS1 +=item.cessamount;
							if(month == '01'){monthtotalCessValue1+=item.cessamount;}if(month == '02'){monthtotalCessValue2+=item.cessamount;}if(month == '03'){monthtotalCessValue3+=item.cessamount;}if(month == '04'){monthtotalCessValue4+=item.cessamount;}if(month == '05'){monthtotalCessValue5+=item.cessamount;}if(month == '06'){monthtotalCessValue6+=item.cessamount;}if(month == '07'){monthtotalCessValue7+=item.cessamount;}if(month == '08'){monthtotalCessValue8+=item.cessamount;}if(month == '09'){monthtotalCessValue9+=item.cessamount;}if(month == '10'){monthtotalCessValue10+=item.cessamount;}if(month == '11'){monthtotalCessValue11+=item.cessamount;}if(month == '12'){monthtotalCessValue12+=item.cessamount;}
						}
				}
			} else {
				item.cessamount = 0.00;
			}
			if(item.discount == null) {
				item.discount = 0.00;
			}
			if(item.advreceived == null) {
				item.advreceived = 0.00;
			}
			
			if(item.igstavltax) {
				totalinvitc+= item.igstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				//totalITCIGST+=item.igstavltax;
				totalITCIGST1 +=item.igstavltax;
				}
			} else {
				item.igstavltax = 0.00;
			}
			if(item.cgstavltax) {
				totalinvitc+= item.cgstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				//totalITCCGST+=item.cgstavltax;
				totalITCCGST1 +=item.cgstavltax;
				}
			} else {
				item.cgstavltax = 0.00;
			}
			if(item.sgstavltax) {
				totalinvitc+= item.sgstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				//totalITCSGST+=item.sgstavltax;
				totalITCSGST1 +=item.sgstavltax;
				}
			} else {
				item.sgstavltax = 0.00;
			}
			if(item.cessavltax) {
				totalinvitc+= item.cessavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				//totalITCCESS+=item.cessavltax;
				totalITCCESS1 +=item.cessavltax;
				}
			} else {
				item.cessavltax = 0.00;
			}
			if(item.type) {
				
			} else {
				item.type = '';
			}
		});
	}
	invoice.totalitc = totalinvitc;
	if(invoice.totalitc) {
		if(invoice.gstStatus != 'CANCELLED'){
			totalITC+=invoice.totalitc;
			if(month == '01'){monthtotalItcValue1+=invoice.totalitc;}if(month == '02'){monthtotalItcValue2+=invoice.totalitc;}if(month == '03'){monthtotalItcValue3+=invoice.totalitc;}if(month == '04'){monthtotalItcValue4+=invoice.totalitc;}if(month == '05'){monthtotalItcValue5+=invoice.totalitc;}if(month == '06'){monthtotalItcValue6+=invoice.totalitc;}if(month == '07'){monthtotalItcValue7+=invoice.totalitc;}if(month == '08'){monthtotalItcValue8+=invoice.totalitc;}if(month == '09'){monthtotalItcValue9+=invoice.totalitc;}if(month == '10'){monthtotalItcValue10+=invoice.totalitc;}if(month == '11'){monthtotalItcValue11+=invoice.totalitc;}if(month == '12'){monthtotalItcValue12+=invoice.totalitc;}
		}
	} else {
		invoice.totalitc = 0.00;
	}
	invoice.igstamount = totalIGST1;
	invoice.cgstamount = totalCGST1;
	invoice.sgstamount = totalSGST1;
	invoice.cessamount = totalCESS1;
	invoice.igstavltax = totalITCIGST1;
	invoice.cgstavltax = totalITCCGST1;
	invoice.sgstavltax = totalITCSGST1;
	invoice.cessavltax = totalITCCESS1;
	invoice.totalExempted = totalExempted;
	return invoice;
	}
</script>