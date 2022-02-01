var gstr2bReconsileArray = new Array();
var gstr2bReconsileTable;
var gstr2bReconsileTableUrl = new Array();
var gstr2bInvoiceReconsileArray = new Object();

function reconsileG2bInvoice(g2bInv){
	var cContent ='';
	ReconsilationNOT_IN_PR = ReconsilationNOT_IN_PR + 1;
	if(g2bInv.b2b == null || g2bInv.b2b.length == 0) {
		g2bInv.b2b = new Array();
		var tObj = new Object();
		tObj.ctin = '';
		g2bInv.b2b.push(tObj);
	}
	if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == g2bInv.invtype && g2bInv.cdn && g2bInv.cdn.length > 0 && g2bInv.cdn[0].nt && g2bInv.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == g2bInv.invtype && g2bInv.cdn && g2bInv.cdn.length > 0 && g2bInv.cdn[0].nt && g2bInv.cdn[0].nt.length > 0)) {
		if(g2bInv.cdn[0].nt[0].ntty == 'C'){
			g2bInv.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			g2bInv.invtype = 'Debit Note';
		}

	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == g2bInv.invtype && g2bInv.cdnr && g2bInv.cdnr.length > 0 && g2bInv.cdnr[0].nt && g2bInv.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == g2bInv.invtype && g2bInv.cdnr && g2bInv.cdnr.length > 0 && g2bInv.cdnr[0].nt && g2bInv.cdnr[0].nt.length > 0)) {
		if(g2bInv.cdnr[0].nt[0].ntty == 'C'){
			g2bInv.invtype = 'Credit Note';
		}else if(g2bInv.cdnr[0].nt[0].ntty == 'D'){
			g2bInv.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == g2bInv.invtype && g2bInv.cdnur && g2bInv.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == g2bInv.invtype && g2bInv.cdnur && g2bInv.cdnur.length > 0)) {
		if(g2bInv.cdnur[0].ntty == 'C'){
			g2bInv.invtype = 'Credit Note(UR)';
		}else if(g2bInv.cdnur[0].ntty == 'D'){
			g2bInv.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == g2bInv.invtype)){
		g2bInv.b2b[0].ctin = g2bInv.impGoods[0].stin ? g2bInv.impGoods[0].stin : "";
	}
	
	g2bInv.mstatus='Not In Purchases';
	g2bInv.gfp='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.fp+'</div></div>';
	g2bInv.ginvno='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.invoiceno+'</div></div>';
	g2bInv.ginvdate='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(g2bInv.dateofinvoice)+'</div></div>';
	g2bInv.ggstno='<span class="" index="gstinno'+g2bInv.b2b[0].ctin+'"><div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.b2b[0].ctin+'</div></div></span>';
	g2bInv.ginvvalue='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(g2bInv.totalamount).toFixed(2))+'</div></div>';
	g2bInv.gtaxablevalue='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(g2bInv.totaltaxableamount).toFixed(2))+'</div></div>';
	g2bInv.gtotaltax='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(g2bInv.totaltax).toFixed(2))+'</div></div>';
	g2bInv.gbranch='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.branch+'</div></div>';
	g2bInv.gvertical='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.vertical+'</div></div>';
	g2bInv.gfullname='<div onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.fullname+'</div></div>';
	g2bInv.gcomments='<div><a href="#" onclick="supComments(\''+g2bInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
	//notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
	if((g2bInv.billedtoname == '' && g2bInv.invoiceCustomerId == '') || (g2bInv.billedtoname == null && g2bInv.invoiceCustomerId == null)) {
		g2bInv.invoiceCustomerIdAndBilledToName = '';
	}else if((g2bInv.billedtoname != null && g2bInv.invoiceCustomerId == null) || (g2bInv.billedtoname != '' && g2bInv.invoiceCustomerId == '')) {
		g2bInv.invoiceCustomerIdAndBilledToName =g2bInv.billedtoname;
	}else if((g2bInv.billedtoname != null || g2bInv.billedtoname != '') && (g2bInv.invoiceCustomerId != null || g2bInv.invoiceCustomerId != '')) {
		g2bInv.invoiceCustomerIdAndBilledToName = g2bInv.billedtoname+"("+g2bInv.invoiceCustomerId+")";
	}
	//<td class="text-left" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')">'+g2bInv.branch+'</td>
	Gstr2bReconsilationArray.push(g2bInv);
	
	cContent += '<tr><td><div class="checkbox" index="'+g2bInv.docId+'"><label><input type="checkbox" name="invMFilter'+g2bInv.docId+'" onClick="updateG2bMisMatchSelection(null, \''+g2bInv.docId+'\', \''+g2bInv.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2B</span></div></td><td class="text-left" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')">'+g2bInv.invtype+'</td><td class="text-left" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')">'+g2bInv.billedtoname+'</td><td class="text-left" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.fp+'</div></td>';
	cContent += '<td class="text-left" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.invoiceno+'</div></td><td class="text-left" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(g2bInv.dateofinvoice)+'</div></td><td class="text-left" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><span class="" index="gstinno'+g2bInv.b2b[0].ctin+'"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+g2bInv.b2b[0].ctin+'</div></span></td>';
	cContent += '<td class="text-right" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(g2bInv.totalamount).toFixed(2))+'</div></td><td class="text-right" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(g2bInv.totaltaxableamount).toFixed(2))+'</div></td><td class="text-right" onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(g2bInv.totaltax).toFixed(2))+'</div></td>';
	cContent += '<td onClick="showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+g2bInv.mstatus+'">'+g2bInv.mstatus+'</span></td>';
	cContent += '<td class="text-left" onClick="showMismatchInv(\''+g2bInv.docId+'\',\''+g2bInv.mstatus+'\')">'+g2bInv.branch+'</td><td class="text-left" onClick="showMismatchInv(\''+g2bInv.gstrid+'\',\''+g2bInv.mstatus+'\')">'+g2bInv.vertical+'</td><td class="text-left" onClick="(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')">'+g2bInv.fullname+'</td>';
	cContent += '<td><a href="#" onclick="supComments(\''+g2bInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></td></tr>';
	return cContent;
}

function reconsilePrInvoice(prInv){
	var cContent = '';
	ReconsilationNOT_IN_GSTR2B = ReconsilationNOT_IN_GSTR2B+1;
	if(prInv.b2b == null || prInv.b2b.length == 0) {
		prInv.b2b = new Array();
		var tObj = new Object();
		tObj.ctin = '';
		prInv.b2b.push(tObj);
	}
	if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == prInv.invtype && prInv.cdn && prInv.cdn.length > 0 && prInv.cdn[0].nt && prInv.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == prInv.invtype && prInv.cdn && prInv.cdn.length > 0 && prInv.cdn[0].nt && prInv.cdn[0].nt.length > 0)) {
		if(prInv.cdn[0].nt[0].ntty == 'C'){
			prInv.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			prInv.invtype = 'Debit Note';
		}

	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == prInv.invtype && prInv.cdnr && prInv.cdnr.length > 0 && prInv.cdnr[0].nt && prInv.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == prInv.invtype && prInv.cdnr && prInv.cdnr.length > 0 && prInv.cdnr[0].nt && prInv.cdnr[0].nt.length > 0)) {
		if(prInv.cdnr[0].nt[0].ntty == 'C'){
			prInv.invtype = 'Credit Note';
		}else if(prInv.cdnr[0].nt[0].ntty == 'D'){
			prInv.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == prInv.invtype && prInv.cdnur && prInv.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == prInv.invtype && prInv.cdnur && prInv.cdnur.length > 0)) {
		if(prInv.cdnur[0].ntty == 'C'){
			prInv.invtype = 'Credit Note(UR)';
		}else if(prInv.cdnur[0].ntty == 'D'){
			prInv.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == prInv.invtype)){
		prInv.b2b[0].ctin = prInv.impGoods[0].stin ? prInv.impGoods[0].stin : "";
	}
	//showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')
	prInv.mstatus='Not In GSTR2B';
	prInv.gfp='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+prInv.fp+'</div><div class="tdline_2 color-red">-</div></div>';
	prInv.ginvno='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+prInv.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
	prInv.ginvdate='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(prInv.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
	prInv.ggstno='<span class="" index="gstinno'+prInv.b2b[0].ctin+'"><div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+prInv.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
	prInv.ginvvalue='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(prInv.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red misindformat">-</div></div>';
	prInv.gtaxablevalue='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(prInv.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red misindformat">-</div></div>';
	prInv.gtotaltax='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(prInv.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red misindformat">-</div></div>';
	prInv.gbranch='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+prInv.branch+'</div><div class="tdline_2 color-red">-</div></div>';
	prInv.gvertical='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+prInv.vertical+'</div><div class="tdline_2 color-red">-</div></div>';
	prInv.gfullname='<div onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+prInv.fullname+'</div><div class="tdline_2 color-red">-</div></div>';
	prInv.gcomments='<div><a href="#" onclick="supComments(\''+prInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
	//notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
	if((prInv.billedtoname == '' && prInv.invoiceCustomerId == '') || (prInv.billedtoname == null && prInv.invoiceCustomerId == null)) {
		prInv.invoiceCustomerIdAndBilledToName = '';
	}else if((prInv.billedtoname != null && prInv.invoiceCustomerId == null) || (prInv.billedtoname != '' && prInv.invoiceCustomerId == '')) {
		prInv.invoiceCustomerIdAndBilledToName =prInv.billedtoname;
	}else if((prInv.billedtoname != null || prInv.billedtoname != '') && (prInv.invoiceCustomerId != null || prInv.invoiceCustomerId != '')) {
		prInv.invoiceCustomerIdAndBilledToName = prInv.billedtoname+"("+prInv.invoiceCustomerId+")";
	}
	
	Gstr2bReconsilationArray.push(prInv);
	cContent += '<tr><td><div class="checkbox" index="'+prInv.docId+'"><label><input type="checkbox" name="invMFilter'+prInv.docId+'" onClick="updateG2bMisMatchSelection(\''+prInv.docId+'\',null, \''+prInv.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2B</span></div></td><td class="text-left" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')">'+prInv.invtype+'</td><td class="text-left" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')">'+prInv.billedtoname+'</td><td class="text-left" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+prInv.fp+'</div><div class="tdline_2 color-red">-</div></td>';
	cContent += '<td class="text-left" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+prInv.invoiceno+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(prInv.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><span class="" index="gstinno'+prInv.b2b[0].ctin+'"><div class="tdline_1 color-red">'+prInv.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></span></td>';
	cContent += '<td class="text-right" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(prInv.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(prInv.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(prInv.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td onClick="showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+prInv.mstatus+'">'+prInv.mstatus+'</span></td>';
	cContent += '<td class="text-left" onClick="showMismatchInv(\''+prInv.docId+'\',\''+prInv.mstatus+'\')">'+prInv.branch+'</td><td class="text-left" onClick="showMismatchInv(\''+prInv.docId+'\',\''+prInv.mstatus+'\')">'+prInv.vertical+'</td><td class="text-left" onClick="showMismatchInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')">'+prInv.fullname+'</td>';
	cContent += '<td><a href="#" onclick="supComments(\''+prInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></td></tr>';
	return cContent;
}

function reconsileG2bPrMatchInvs(g2bMtchInv, prMtchInv){
	if(prMtchInv.gstr2bMatchingStatus != 'Matched' && prMtchInv.gstr2bMatchingStatus != 'Round Off Matched' && prMtchInv.gstr2bMatchingStatus != 'Matched In Other Months' && prMtchInv.gstr2bMatchingStatus != 'Probable Matched') {
		var tStatus = 'Mismatched';
		if((g2bMtchInv.invoiceno == prMtchInv.invoiceno)
			&& (g2bMtchInv.dateofinvoice == prMtchInv.dateofinvoice)
			&& (g2bMtchInv.b2b[0].ctin == prMtchInv.b2b[0].ctin)
			&& (g2bMtchInv.totaltaxableamount == prMtchInv.totaltaxableamount)
			&& (g2bMtchInv.totaltax == prMtchInv.totaltax)) {
			tStatus = 'Matched';
			ReconsilationMATCHED = ReconsilationMATCHED + 1;
		} else if((g2bMtchInv.invoiceno == prMtchInv.invoiceno)
			&& (g2bMtchInv.dateofinvoice == prMtchInv.dateofinvoice)
			&& (g2bMtchInv.b2b[0].ctin != prMtchInv.b2b[0].ctin)
			&& (g2bMtchInv.totaltaxableamount == prMtchInv.totaltaxableamount)
			&& (g2bMtchInv.totaltax == prMtchInv.totaltax)) {
			tStatus = 'GST No Mismatched';
			ReconsilationGST_NO_MISMATCHED = ReconsilationGST_NO_MISMATCHED + 1;
		} else if((g2bMtchInv.invoiceno == prMtchInv.invoiceno)
			&& (g2bMtchInv.dateofinvoice == prMtchInv.dateofinvoice)
			&& (g2bMtchInv.b2b[0].ctin == prMtchInv.b2b[0].ctin)
			&& (g2bMtchInv.totaltaxableamount != prMtchInv.totaltaxableamount)
			&& (g2bMtchInv.totaltax == prMtchInv.totaltax)) {
			tStatus = 'Invoice Value Mismatched';
			ReconsilationINVOICE_VALUE_MISMATCHED  = ReconsilationINVOICE_VALUE_MISMATCHED + 1;
		} else if((g2bMtchInv.invoiceno == prMtchInv.invoiceno)
			&& (g2bMtchInv.dateofinvoice == prMtchInv.dateofinvoice)
			&& (g2bMtchInv.b2b[0].ctin == prMtchInv.b2b[0].ctin)
			&& (g2bMtchInv.totaltaxableamount == prMtchInv.totaltaxableamount)
			&& (g2bMtchInv.totaltax != prMtchInv.totaltax)) {
			tStatus = 'Tax Mismatched';
			ReconsilationTAX_MISMATCHED = ReconsilationTAX_MISMATCHED + 1;
		} else if((g2bMtchInv.invoiceno != prMtchInv.invoiceno)
			&& (g2bMtchInv.dateofinvoice == prMtchInv.dateofinvoice)
			&& (g2bMtchInv.b2b[0].ctin == prMtchInv.b2b[0].ctin)
			&& (g2bMtchInv.totaltaxableamount == prMtchInv.totaltaxableamount)
			&& (g2bMtchInv.totaltax == prMtchInv.totaltax)) {
			tStatus = 'Invoice No Mismatched';
			ReconsilationINVOICENO_MISMATCHED = ReconsilationINVOICENO_MISMATCHED + 1;
		} else if((g2bMtchInv.invoiceno == prMtchInv.invoiceno)
				&& (g2bMtchInv.dateofinvoice != prMtchInv.dateofinvoice)
				&& (g2bMtchInv.b2b[0].ctin == prMtchInv.b2b[0].ctin)
				&& (g2bMtchInv.totaltaxableamount == prMtchInv.totaltaxableamount)
				&& (g2bMtchInv.totaltax == prMtchInv.totaltax)) {
				tStatus = 'Invoice Date Mismatched';
				ReconsilationINVOICE_DATE_MISMATCHED = ReconsilationINVOICE_DATE_MISMATCHED + 1;
		}else {
			ReconsilationMISMATCHED = ReconsilationMISMATCHED+1;
		}
		g2bMtchInv.mstatus=tStatus;
	}else{
		if(g2bMtchInv.gstr2bMatchingStatus == 'Matched' && prMtchInv.gstr2bMatchingStatus == 'Matched'){
			ReconsilationMATCHED = ReconsilationMATCHED+1;
			tStatus = 'Matched';
		}else if(g2bMtchInv.gstr2bMatchingStatus == 'Round Off Matched' && prMtchInv.gstr2bMatchingStatus == 'Round Off Matched'){
			ReconsilationROUNDOFF_MATCHED = ReconsilationROUNDOFF_MATCHED+1;
			tStatus = 'Round Off Matched';
		}else if(g2bMtchInv.gstr2bMatchingStatus == 'Probable Matched' && prMtchInv.gstr2bMatchingStatus == 'Probable Matched'){
			ReconsilationPROBABLE_MATCHED = ReconsilationPROBABLE_MATCHED+1;
			tStatus = 'Probable Matched';
		}else if(g2bMtchInv.gstr2bMatchingStatus == 'Matched In Other Months' && prMtchInv.gstr2bMatchingStatus == 'Matched In Other Months'){
			ReconsilationMATCHED_IN_OTHER_MONTHS = ReconsilationMATCHED_IN_OTHER_MONTHS+1;
			tStatus = 'Matched In Other Months';
		}
	}
	g2bMtchInv.mstatus=tStatus;
	
	var cContent ='';
	if(g2bMtchInv.b2b == null || g2bMtchInv.b2b.length == 0) {
		g2bMtchInv.b2b = new Array();
		var tObj = new Object();
		tObj.ctin = '';
		g2bMtchInv.b2b.push(tObj);
	}
	if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == g2bMtchInv.invtype && g2bMtchInv.cdn && g2bMtchInv.cdn.length > 0 && g2bMtchInv.cdn[0].nt && g2bMtchInv.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == g2bMtchInv.invtype && g2bMtchInv.cdn && g2bMtchInv.cdn.length > 0 && g2bMtchInv.cdn[0].nt && g2bMtchInv.cdn[0].nt.length > 0)) {
		if(g2bMtchInv.cdn[0].nt[0].ntty == 'C'){
			g2bMtchInv.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			g2bMtchInv.invtype = 'Debit Note';
		}

	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == g2bMtchInv.invtype && g2bMtchInv.cdnr && g2bMtchInv.cdnr.length > 0 && g2bMtchInv.cdnr[0].nt && g2bMtchInv.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == g2bMtchInv.invtype && g2bMtchInv.cdnr && g2bMtchInv.cdnr.length > 0 && g2bMtchInv.cdnr[0].nt && g2bMtchInv.cdnr[0].nt.length > 0)) {
		if(g2bMtchInv.cdnr[0].nt[0].ntty == 'C'){
			g2bMtchInv.invtype = 'Credit Note';
		}else if(g2bMtchInv.cdnr[0].nt[0].ntty == 'D'){
			g2bMtchInv.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == g2bMtchInv.invtype && g2bMtchInv.cdnur && g2bMtchInv.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == g2bMtchInv.invtype && g2bMtchInv.cdnur && g2bMtchInv.cdnur.length > 0)) {
		if(g2bMtchInv.cdnur[0].ntty == 'C'){
			g2bMtchInv.invtype = 'Credit Note(UR)';
		}else if(g2bMtchInv.cdnur[0].ntty == 'D'){
			g2bMtchInv.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == g2bMtchInv.invtype)){
		g2bMtchInv.b2b[0].ctin = g2bMtchInv.impGoods[0].stin ? g2bMtchInv.impGoods[0].stin : "";
	}
	//showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')
	//showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')
	g2bMtchInv.mstatus=tStatus;
	g2bMtchInv.gfp='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1">'+prMtchInv.fp+'</div><div class="tdline_2">'+g2bMtchInv.fp+'</div></div>';
	g2bMtchInv.ginvno='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1">'+prMtchInv.invoiceno+'</div><div class="tdline_2">'+g2bMtchInv.invoiceno+'</div></div>';
	g2bMtchInv.ginvdate='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1">'+formatDate(prMtchInv.dateofinvoice)+'</div><div class="tdline_2">'+formatDate(g2bMtchInv.dateofinvoice)+'</div></div>';
	g2bMtchInv.ggstno='<span class="" index="gstinno'+g2bMtchInv.b2b[0].ctin+'"><div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1">'+g2bMtchInv.b2b[0].ctin+'</div><div class="tdline_2">'+g2bMtchInv.b2b[0].ctin+'</div></div></span>';
	g2bMtchInv.ginvvalue='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(prMtchInv.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">'+formatNumber(parseFloat(g2bMtchInv.totalamount).toFixed(2))+'</div></div>';
	g2bMtchInv.gtaxablevalue='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(prMtchInv.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">'+formatNumber(parseFloat(g2bMtchInv.totaltaxableamount).toFixed(2))+'</div></div>';
	g2bMtchInv.gtotaltax='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(prMtchInv.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">'+formatNumber(parseFloat(g2bMtchInv.totaltax).toFixed(2))+'</div></div>';
	g2bMtchInv.gbranch='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1">'+prMtchInv.branch+'</div><div class="tdline_2">'+g2bMtchInv.branch+'</div></div>';
	g2bMtchInv.gvertical='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1">'+prMtchInv.vertical+'</div><div class="tdline_2">'+g2bMtchInv.vertical+'</div></div>';
	g2bMtchInv.gfullname='<div onClick="showReconsilationInv(\''+g2bMtchInv.docId+'\',\''+g2bMtchInv.clientid+'\',\''+g2bMtchInv.fp+'\',\''+g2bMtchInv.mstatus+'\')"><div class="tdline_1">'+prMtchInv.fullname+'</div><div class="tdline_2">'+g2bMtchInv.fullname+'</div></div>';
	g2bMtchInv.gcomments='<div><a href="#" onclick="supComments(\''+g2bMtchInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
	
	if((g2bMtchInv.billedtoname == '' && g2bMtchInv.invoiceCustomerId == '') || (g2bMtchInv.billedtoname == null && g2bMtchInv.invoiceCustomerId == null)) {
		g2bMtchInv.invoiceCustomerIdAndBilledToName = '';
	}else if((g2bMtchInv.billedtoname != null && g2bMtchInv.invoiceCustomerId == null) || (g2bMtchInv.billedtoname != '' && g2bMtchInv.invoiceCustomerId == '')) {
		g2bMtchInv.invoiceCustomerIdAndBilledToName =g2bMtchInv.billedtoname;
	}else if((g2bMtchInv.billedtoname != null || g2bMtchInv.billedtoname != '') && (g2bMtchInv.invoiceCustomerId != null || g2bMtchInv.invoiceCustomerId != '')) {
		g2bMtchInv.invoiceCustomerIdAndBilledToName = g2bMtchInv.billedtoname+"("+g2bMtchInv.invoiceCustomerId+")";
	}
	
	Gstr2bReconsilationArray.push(g2bMtchInv);
	Gstr2bReconsilationSummaryArray.push(g2bMtchInv);
	if(prMtchInv.b2b == null || prMtchInv.b2b.length == 0) {
		prMtchInv.b2b = new Array();
		var tObj = new Object();
		tObj.ctin = '';
		prMtchInv.b2b.push(tObj);
	}
	if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == prMtchInv.invtype && prMtchInv.cdn && prMtchInv.cdn.length > 0 && prMtchInv.cdn[0].nt && prMtchInv.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == prMtchInv.invtype && prMtchInv.cdn && prMtchInv.cdn.length > 0 && prMtchInv.cdn[0].nt && prMtchInv.cdn[0].nt.length > 0)) {
		if(prMtchInv.cdn[0].nt[0].ntty == 'C'){
			prMtchInv.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			prMtchInv.invtype = 'Debit Note';
		}

	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == prMtchInv.invtype && prMtchInv.cdnr && prMtchInv.cdnr.length > 0 && prMtchInv.cdnr[0].nt && prMtchInv.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == prMtchInv.invtype && prMtchInv.cdnr && prMtchInv.cdnr.length > 0 && prMtchInv.cdnr[0].nt && prMtchInv.cdnr[0].nt.length > 0)) {
		if(prMtchInv.cdnr[0].nt[0].ntty == 'C'){
			prMtchInv.invtype = 'Credit Note';
		}else if(prMtchInv.cdnr[0].nt[0].ntty == 'D'){
			prMtchInv.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == prMtchInv.invtype && prMtchInv.cdnur && prMtchInv.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == prMtchInv.invtype && prMtchInv.cdnur && prMtchInv.cdnur.length > 0)) {
		if(prMtchInv.cdnur[0].ntty == 'C'){
			prMtchInv.invtype = 'Credit Note(UR)';
		}else if(prMtchInv.cdnur[0].ntty == 'D'){
			prMtchInv.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == prMtchInv.invtype)){
		prMtchInv.b2b[0].ctin = prMtchInv.impGoods[0].stin ? prMtchInv.impGoods[0].stin : "";
	}
	prMtchInv.mstatus=tStatus;
	prMtchInv.gfp='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+prMtchInv.fp+'</div></div>';
	prMtchInv.ginvno='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+prMtchInv.invoiceno+'</div></div>';
	prMtchInv.ginvdate='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(prMtchInv.dateofinvoice)+'</div></div>';
	prMtchInv.ggstno='<span class="" index="gstinno'+prMtchInv.b2b[0].ctin+'"><div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+prMtchInv.b2b[0].ctin+'</div></div></span>';
	prMtchInv.ginvvalue='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(prMtchInv.totalamount).toFixed(2))+'</div></div>';
	prMtchInv.gtaxablevalue='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(prMtchInv.totaltaxableamount).toFixed(2))+'</div></div>';
	prMtchInv.gtotaltax='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(prMtchInv.totaltax).toFixed(2))+'</div></div>';
	prMtchInv.gbranch='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+prMtchInv.branch+'</div></div>';
	prMtchInv.gvertical='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+prMtchInv.vertical+'</div></div>';
	prMtchInv.gfullname='<div onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+prMtchInv.fullname+'</div></div>';
	prMtchInv.gcomments='<div><a href="#" onclick="supComments(\''+prMtchInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
	//notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
	if((prMtchInv.billedtoname == '' && prMtchInv.invoiceCustomerId == '') || (prMtchInv.billedtoname == null && prMtchInv.invoiceCustomerId == null)) {
		prMtchInv.invoiceCustomerIdAndBilledToName = '';
	}else if((prMtchInv.billedtoname != null && prMtchInv.invoiceCustomerId == null) || (prMtchInv.billedtoname != '' && prMtchInv.invoiceCustomerId == '')) {
		prMtchInv.invoiceCustomerIdAndBilledToName =prMtchInv.billedtoname;
	}else if((prMtchInv.billedtoname != null || prMtchInv.billedtoname != '') && (prMtchInv.invoiceCustomerId != null || prMtchInv.invoiceCustomerId != '')) {
		prMtchInv.invoiceCustomerIdAndBilledToName = prMtchInv.billedtoname+"("+prMtchInv.invoiceCustomerId+")";
	}
	cContent += '<tr><td><div class="checkbox" index="'+prMtchInv.docId+'"><label><input type="checkbox" name="invMFilter'+prMtchInv.docId+'" onClick="updateG2bMisMatchSelection(\''+prMtchInv.docId+'\',null, \''+prMtchInv.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2B</span></div></td><td class="text-left" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')">'+prMtchInv.invtype+'</td><td class="text-left" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')">'+prMtchInv.billedtoname+'</td><td class="text-left" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1">'+prMtchInv.fp+'</div><div class="tdline_2">'+g2bMtchInv.fp+'</div></td>';
	cContent += '<td class="text-left" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1">'+prMtchInv.invoiceno+'</div><div class="tdline_2">'+g2bMtchInv.invoiceno+'</div></td><td class="text-left" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1">'+formatDate(prMtchInv.dateofinvoice)+'</div><div class="tdline_2">'+formatDate(g2bMtchInv.dateofinvoice)+'</div></td><td class="text-left" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><span class="" index="gstinno'+prMtchInv.b2b[0].ctin+'"><div class="tdline_1">'+prMtchInv.b2b[0].ctin+'</div><div class="tdline_2">'+g2bMtchInv.b2b[0].ctin+'</div></span></td>';
	cContent += '<td class="text-right" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1">'+formatNumber(parseFloat(prMtchInv.totalamount).toFixed(2))+'</div><div class="tdline_2">'+formatNumber(parseFloat(g2bMtchInv.totalamount).toFixed(2))+'</div></td><td class="text-right" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1">'+formatNumber(parseFloat(prMtchInv.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2">'+formatNumber(parseFloat(g2bMtchInv.totaltaxableamount).toFixed(2))+'</div></td><td class="text-right" onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><div class="tdline_1">'+formatNumber(parseFloat(prMtchInv.totaltax).toFixed(2))+'</div><div class="tdline_2">'+formatNumber(parseFloat(g2bMtchInv.totaltax).toFixed(2))+'</div></td><td onClick="showReconsilationInv(\''+prMtchInv.docId+'\',\''+prMtchInv.clientid+'\',\''+prMtchInv.fp+'\',\''+prMtchInv.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+prMtchInv.mstatus+'">'+prMtchInv.mstatus+'</span></td>';
	cContent += '<td class="text-left" onClick="showMismatchInv(\''+prMtchInv.docId+'\',\''+prMtchInv.mstatus+'\')">'+prMtchInv.branch+'</td><td class="text-left" onClick="showMismatchInv(\''+prMtchInv.docId+'\',\''+prMtchInv.mstatus+'\')">'+prMtchInv.vertical+'</td><td class="text-left" onClick="(\''+prMtchInv.docId+'\',\''+prMtchInv.mstatus+'\')">'+prMtchInv.fullname+'</td>';
	cContent += '<td><a href="#" onclick="supComments(\''+prMtchInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></td></tr>';
	return cContent;
}

function reconsilePrManulMatchInvoice(prInv){
	var cContent = '';
	ReconsilationMANUAL_MATCHED = ReconsilationMANUAL_MATCHED+1;
	if(prInv.b2b == null || prInv.b2b.length == 0) {
		prInv.b2b = new Array();
		var tObj = new Object();
		tObj.ctin = '';
		prInv.b2b.push(tObj);
	}
	if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == prInv.invtype && prInv.cdn && prInv.cdn.length > 0 && prInv.cdn[0].nt && prInv.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == prInv.invtype && prInv.cdn && prInv.cdn.length > 0 && prInv.cdn[0].nt && prInv.cdn[0].nt.length > 0)) {
		if(prInv.cdn[0].nt[0].ntty == 'C'){
			prInv.invtype = 'Credit Note';
		}else if(prInv.cdn[0].nt[0].ntty == 'D'){
			prInv.invtype = 'Debit Note';
		}

	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == prInv.invtype && prInv.cdnr && prInv.cdnr.length > 0 && prInv.cdnr[0].nt && prInv.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == prInv.invtype && prInv.cdnr && prInv.cdnr.length > 0 && prInv.cdnr[0].nt && prInv.cdnr[0].nt.length > 0)) {
		if(prInv.cdnr[0].nt[0].ntty == 'C'){
			prInv.invtype = 'Credit Note';
		}else if(prInv.cdnr[0].nt[0].ntty == 'D'){
			prInv.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == prInv.invtype && prInv.cdnur && prInv.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == prInv.invtype && prInv.cdnur && prInv.cdnur.length > 0)) {
		if(prInv.cdnur[0].ntty == 'C'){
			prInv.invtype = 'Credit Note(UR)';
		}else if(prInv.cdnur[0].ntty == 'D'){
			prInv.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == prInv.invtype)){
		prInv.b2b[0].ctin = prInv.impGoods[0].stin ? prInv.impGoods[0].stin : "";
	}
	//showReconsilationInv(\''+prInv.docId+'\',\''+prInv.clientid+'\',\''+prInv.fp+'\',\''+prInv.mstatus+'\')
	
	if(prInv.b2b == null || prInv.b2b.length == 0) {
		prInv.b2b = new Array();
		var tObj = new Object();
		tObj.ctin = '';
		prInv.b2b.push(tObj);
	}
		
	prInv.mstatus='Manual Matched';
	prInv.gfp='<div class="tdline_1">'+prInv.fp+'</div><div class="tdline_2">-</div>';
	prInv.ginvno='<div class="tdline_1">'+prInv.invoiceno+'</div><div class="tdline_2">-</div>';
	prInv.ginvdate='<div class="tdline_1">'+formatDate(prInv.dateofinvoice)+'</div><div class="tdline_2">-</div>';
	prInv.ggstno='<span class="" index="gstinno'+prInv.b2b[0].ctin+'"><div class="tdline_1">'+prInv.b2b[0].ctin+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal" onClick="viewG2bMannualMatchedInvoices(\''+prInv.docId+'\',\'Purchase Register\')">Mannualy Matched with Multiple Invoices</a></div></span>';
	prInv.ginvvalue='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(prInv.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
	prInv.gtaxablevalue='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(prInv.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
	prInv.gtotaltax='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(prInv.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
	prInv.gbranch='<div class="tdline_1">'+prInv.branch+'</div><div class="tdline_2">-</div>';
	prInv.gvertical='<div class="tdline_1">'+prInv.vertical+'</div><div class="tdline_2">-</div></div>';
	prInv.gfullname='<div class="tdline_1">'+prInv.fullname+'</div><div class="tdline_2">-</div></div>';
	prInv.gcomments='<div><a href="#" onclick="supComments(\''+prInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
	prInv.gstrid = prInv.matchingId;
	Gstr2bReconsilationArray.push(prInv);
	if((prInv.billedtoname == '' && prInv.invoiceCustomerId == '') || (prInv.billedtoname == null && prInv.invoiceCustomerId == null)) {
		prInv.invoiceCustomerIdAndBilledToName = '';
	}else if((prInv.billedtoname != null && prInv.invoiceCustomerId == null) || (prInv.billedtoname != '' && prInv.invoiceCustomerId == '')) {
		prInv.invoiceCustomerIdAndBilledToName =prInv.billedtoname;
	}else if((prInv.billedtoname != null || prInv.billedtoname != '') && (prInv.invoiceCustomerId != null || prInv.invoiceCustomerId != '')) {
		prInv.invoiceCustomerIdAndBilledToName = prInv.billedtoname+"("+prInv.invoiceCustomerId+")";
	}
	cContent += '<tr><td><div class="checkbox" index="'+prInv.docId+'"><label><input type="checkbox" name="invMFilter'+prInv.docId+'" onClick="updateMisMatchSelection(\''+prInv.docId+'\', \''+prInv.matchingId+'\', \''+prInv.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td align="center"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left">'+prInv.invtype+'</td><td class="text-left">'+prInv.billedtoname+'</td><td class="text-left"><div class="tdline_1 hsadj">'+prInv.fp+'</div>';
	cContent += '<td class="text-left"><div class="tdline_1">'+prInv.invoiceno+'</div><div class="tdline_2">-</div></td>';
	cContent += '<td class="text-left"><div class="tdline_1">'+formatDate(prInv.dateofinvoice)+'</div><div class="tdline_2">-</div></td>';
	cContent += '<td class="text-left"><span class="" index="gstinno'+prInv.b2b[0].ctin+'"><div class="tdline_1">'+prInv.b2b[0].ctin+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal" onClick="viewG2bMannualMatchedInvoices(\''+prInv.docId+'\',\'Purchase Register\')">Mannualy Matched with Multiple Invoices</a></div></span></td>';
	cContent += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(prInv.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
	cContent += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(prInv.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
	cContent += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(prInv.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
	cContent += '<td><span class="bluetxt f-13" index="mismatchStatus'+prInv.mstatus+'">'+prInv.mstatus+'</span></td><td class="text-left">'+prInv.branch+'</td><td class="text-left">'+prInv.vertical+'</td><td class="text-left">'+prInv.fullname+'</td>';
	cContent += '<td><a href="#" onclick="supComments(\''+prInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';

	return cContent;
}

function reconsileG2bManulMatchInvoice(g2bInv){
	var cContent = '';
	ReconsilationNOT_IN_GSTR2B = ReconsilationNOT_IN_GSTR2B+1;
	if(g2bInv.b2b == null || g2bInv.b2b.length == 0) {
		g2bInv.b2b = new Array();
		var tObj = new Object();
		tObj.ctin = '';
		g2bInv.b2b.push(tObj);
	}
	if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == g2bInv.invtype && g2bInv.cdn && g2bInv.cdn.length > 0 && g2bInv.cdn[0].nt && g2bInv.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == g2bInv.invtype && g2bInv.cdn && g2bInv.cdn.length > 0 && g2bInv.cdn[0].nt && g2bInv.cdn[0].nt.length > 0)) {
		if(g2bInv.cdn[0].nt[0].ntty == 'C'){
			g2bInv.invtype = 'Credit Note';
		}else if(g2bInv.cdn[0].nt[0].ntty == 'D'){
			g2bInv.invtype = 'Debit Note';
		}

	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == g2bInv.invtype && g2bInv.cdnr && g2bInv.cdnr.length > 0 && g2bInv.cdnr[0].nt && g2bInv.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == g2bInv.invtype && g2bInv.cdnr && g2bInv.cdnr.length > 0 && g2bInv.cdnr[0].nt && g2bInv.cdnr[0].nt.length > 0)) {
		if(g2bInv.cdnr[0].nt[0].ntty == 'C'){
			g2bInv.invtype = 'Credit Note';
		}else if(g2bInv.cdnr[0].nt[0].ntty == 'D'){
			g2bInv.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == g2bInv.invtype && g2bInv.cdnur && g2bInv.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == g2bInv.invtype && g2bInv.cdnur && g2bInv.cdnur.length > 0)) {
		if(g2bInv.cdnur[0].ntty == 'C'){
			g2bInv.invtype = 'Credit Note(UR)';
		}else if(g2bInv.cdnur[0].ntty == 'D'){
			g2bInv.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == g2bInv.invtype)){
		g2bInv.b2b[0].ctin = g2bInv.impGoods[0].stin ? g2bInv.impGoods[0].stin : "";
	}
	//showReconsilationInv(\''+g2bInv.docId+'\',\''+g2bInv.clientid+'\',\''+g2bInv.fp+'\',\''+g2bInv.mstatus+'\')
	
	if(g2bInv.b2b == null || g2bInv.b2b.length == 0) {
		g2bInv.b2b = new Array();
		var tObj = new Object();
		tObj.ctin = '';
		g2bInv.b2b.push(tObj);
	}

	g2bInv.mstatus='Manual Matched';
		
	if((g2bInv.billedtoname == '' && g2bInv.invoiceCustomerId == '') || (g2bInv.billedtoname == null && g2bInv.invoiceCustomerId == null)) {
		g2bInv.invoiceCustomerIdAndBilledToName = '';
	}else if((g2bInv.billedtoname != null && g2bInv.invoiceCustomerId == null) || (g2bInv.billedtoname != '' && g2bInv.invoiceCustomerId == '')) {
		g2bInv.invoiceCustomerIdAndBilledToName =g2bInv.billedtoname;
	}else if((g2bInv.billedtoname != null || g2bInv.billedtoname != '') && (g2bInv.invoiceCustomerId != null || g2bInv.invoiceCustomerId != '')) {
		g2bInv.invoiceCustomerIdAndBilledToName = g2bInv.billedtoname+"("+g2bInv.invoiceCustomerId+")";
	}
	
	g2bInv.gfp='<div class="tdline_1">'+g2bInv.fp+'</div><div class="tdline_2">-</div>';
	g2bInv.ginvno='<div class="tdline_1">'+g2bInv.invoiceno+'</div><div class="tdline_2">-</div>';
	g2bInv.ginvdate='<div class="tdline_1">'+formatDate(g2bInv.dateofinvoice)+'</div><div class="tdline_2">-</div>';
	g2bInv.ggstno='<span class="" index="gstinno'+g2bInv.b2b[0].ctin+'"><div class="tdline_1">'+g2bInv.b2b[0].ctin+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal" onClick="viewG2bMannualMatchedInvoices(\''+g2bInv.docId+'\',\'GSTR2B\')">Mannualy Matched with Multiple Invoices</a></div></span>';
	g2bInv.ginvvalue='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(g2bInv.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
	g2bInv.gtaxablevalue='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(g2bInv.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
	g2bInv.gtotaltax='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(g2bInv.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
	g2bInv.gbranch='<div class="tdline_1">'+g2bInv.branch+'</div><div class="tdline_2">-</div>';
	g2bInv.gvertical='<div class="tdline_1">'+g2bInv.vertical+'</div><div class="tdline_2">-</div></div>';
	g2bInv.gfullname='<div class="tdline_1">'+g2bInv.fullname+'</div><div class="tdline_2">-</div></div>';
	g2bInv.gcomments='<div><a href="#" onclick="supComments(\''+g2bInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
	g2bInv.gstrid = g2bInv.docId;
	
	cContent += '<tr><td><div class="checkbox" index="'+g2bInv.docId+'"><label><input type="checkbox" name="invMFilter'+g2bInv.docId+'" onClick="updateMisMatchSelection(\''+g2bInv.docId+'\', \''+g2bInv.matchingId+'\', \''+g2bInv.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td align="center"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left">'+g2bInv.invtype+'</td><td class="text-left">'+g2bInv.billedtoname+'</td><td class="text-left"><div class="tdline_1 hsadj">'+g2bInv.fp+'</div>';
	cContent += '<td class="text-left"><div class="tdline_1">'+g2bInv.invoiceno+'</div><div class="tdline_2">-</div></td>';
	cContent += '<td class="text-left"><div class="tdline_1">'+formatDate(g2bInv.dateofinvoice)+'</div><div class="tdline_2">-</div></td>';
	cContent += '<td class="text-left"><span class="" index="gstinno'+g2bInv.b2b[0].ctin+'"><div class="tdline_1">'+g2bInv.b2b[0].ctin+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal" onClick="viewG2bMannualMatchedInvoices(\''+g2bInv.docId+'\',\'GSTR2B\')">Mannualy Matched with Multiple Invoices</a></div></span></td>';
	cContent += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(g2bInv.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
	cContent += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(g2bInv.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
	cContent += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(g2bInv.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
	cContent += '<td><span class="bluetxt f-13" index="mismatchStatus'+g2bInv.mstatus+'">'+g2bInv.mstatus+'</span></td>';
	cContent += '<td class="text-left">'+g2bInv.branch+'</td><td class="text-left">'+g2bInv.vertical+'</td><td class="text-left">'+g2bInv.fullname+'</td>';
	cContent += '<td><a href="#" onclick="supComments(\''+g2bInv.docId+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
	
	
	Gstr2bReconsilationArray.push(g2bInv);
	
	return cContent;
}


function updateGstr2bReconsilationSummary(){
	if(Gstr2bReconsilationSummaryArray.length > 0) {
		var g2btaxArray = new Array();
		var g2bcounts =0;
		var g2bcustnames = [];
		Gstr2bReconsilationSummaryArray.forEach(function(invoice) {
			if(invoice.invoiceCustomerId){
				if(invoice.billedtoname) {
					if(g2bcounts == 0){
						$('#Gstr2bReconsilationMultiselect4').children('option').remove();
						g2bcustnames.push(invoice.billedtoname+"("+invoice.invoiceCustomerId+")");
						$("#Gstr2bReconsilationMultiselect4").append($("<option></option>").attr("value",invoice.billedtoname+"("+invoice.invoiceCustomerId+")").text(invoice.billedtoname+"("+invoice.invoiceCustomerId+")"));
					}
					if(jQuery.inArray(invoice.billedtoname+"("+invoice.invoiceCustomerId+")", g2bcustnames ) == -1){
						g2bcustnames.push(invoice.billedtoname+"("+invoice.invoiceCustomerId+")");
						$("#Gstr2bReconsilationMultiselect4").append($("<option></option>").attr("value",invoice.billedtoname+"("+invoice.invoiceCustomerId+")").text(invoice.billedtoname+"("+invoice.invoiceCustomerId+")"));
					}
				}
			}else{
				if(invoice.billedtoname) {
					if(g2bcounts == 0){
						$('#Gstr2bReconsilationMultiselect4').children('option').remove();
						g2bcustnames.push(invoice.billedtoname);
						$("#Gstr2bReconsilationMultiselect4").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
					}
					if(jQuery.inArray(invoice.billedtoname, g2bcustnames ) == -1){
						g2bcustnames.push(invoice.billedtoname);
						$("#Gstr2bReconsilationMultiselect4").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
					}
				}			
			}
			var creditDebit = 'credit';
			var invtype = invoice.invtype;
			if(invtype == 'Debit Note' || invtype == 'Credit Note'){
				if(invoice.cdn[0].nt[0].ntty == 'D'){
					creditDebit = 'debit';
				}else{
					creditDebit = 'credit';
				}
			}else if(invtype == 'Credit Note(UR)' || invtype == 'Debit Note(UR)'){
				if(invoice.cdnur[0].ntty == 'D'){
					creditDebit = 'debit';
				}else{
					creditDebit = 'credit';
				}
			}else if(invtype == 'CDNA'){
				if(invoice.cdna[0].nt[0].ntty == 'D'){
					creditDebit = 'debit';
				}else{
					creditDebit = 'credit';
				}
			}else{
				creditDebit = 'debit';
			}
			g2btaxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,creditDebit]);
			g2bcounts++;
		});
		$("#Gstr2bReconsilationMultiselect4").multiselect('rebuild');
		var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0;
		g2btaxArray.forEach(function(row) {
			transCount++;
			if(g2btaxArray[index][6] != 'credit'){
				tIGST+=g2btaxArray[index][0];
				tCGST+=g2btaxArray[index][1];
				tSGST+=g2btaxArray[index][2];
				tCESS+=g2btaxArray[index][3];
				tTaxableAmount+=g2btaxArray[index][4];
				tTotalTax+=g2btaxArray[index][5];
			}else{
				tIGST+=g2btaxArray[index][0];
				tCGST+=g2btaxArray[index][1];
				tSGST+=g2btaxArray[index][2];
				tCESS+=g2btaxArray[index][3];
				tTaxableAmount-=g2btaxArray[index][4];
				tTotalTax+=g2btaxArray[index][0];
				tTotalTax+=g2btaxArray[index][1];
				tTotalTax+=g2btaxArray[index][2];
				tTotalTax+=g2btaxArray[index][3];
			}
		  	index++;
		});
		$('#idGstr2bReconsilationCount').html(transCount);
		$('#idGstr2bReconsilationIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tIGST).toFixed(2)));
		$('#idGstr2bReconsilationCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCGST).toFixed(2)));
		$('#idGstr2bReconsilationSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tSGST).toFixed(2)));
		$('#idGstr2bReconsilationCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCESS).toFixed(2)));
		$('#idGstr2bReconsilationTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTaxableAmount).toFixed(2)));
		$('#idGstr2bReconsilationTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalTax).toFixed(2)));
	}else{
		$('#idGstr2bReconsilationCount').html(0);
		$('#idGstr2bReconsilationIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idGstr2bReconsilationCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idGstr2bReconsilationSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idGstr2bReconsilationCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idGstr2bReconsilationTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idGstr2bReconsilationTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
}

function showReconsilationInv(docId, clientid, fp, mstatus){
	var content = '';var content1 = '';var branch;var billedtoname;var pbilledtoname;
	var mthYr = $('#datetimepicker').val().split('-');
	$('#recgstr2a').html('Record in GSTR2B');
	$('#showMismatchInvModal').modal("show");
	$.ajax({
		type: "GET",
		url: contextPath+"/getg2breconsilationInvDetails/"+docId+"/"+clientid+"/"+mstatus+"?fp="+fp,
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response){
				if(mstatus == 'Not In GSTR2B'){
					if(response.PurchaseRegisterinvoice != null){
						if(response.PurchaseRegisterinvoice.branch == '' || response.PurchaseRegisterinvoice.branch == null){branch = "-";}else{branch=response.PurchaseRegisterinvoice.branch;}
						if(response.PurchaseRegisterinvoice.billedtoname == '' || response.PurchaseRegisterinvoice.billedtoname == null){pbilledtoname = "";}else{pbilledtoname=response.PurchaseRegisterinvoice.billedtoname;}
						content += '<tr><td class="text-left">'+response.PurchaseRegisterinvoice.invtype+'</td><td class="text-left">'+pbilledtoname+'</td><td class="text-left">'+response.PurchaseRegisterinvoice.invoiceno+'</td><td>'+formatDate(response.PurchaseRegisterinvoice.dateofinvoice)+'</td><td class="text-left">'+response.PurchaseRegisterinvoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.PurchaseRegisterinvoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.PurchaseRegisterinvoice.totaltax).toFixed(2))+'</td><td class="text-left">'+branch+'</td><td class="text-left">'+mstatus+'</td></tr>';
					}
					if(showInvTable) {
						showInvTable.clear().destroy();
					}
					if(showInvPRTable) {
						showInvPRTable.clear().destroy();
					}
					
					$('#showInvDetailTable_Body').html(content);
				}else if(mstatus == 'Not In Purchases'){
						if(response.gstr2binvoice != null){
							if(response.gstr2binvoice.billedtoname == '' || response.gstr2binvoice.billedtoname == null){billedtoname = "";}else{billedtoname=response.gstr2binvoice.billedtoname;}
							content += '<tr><td class="text-left">'+response.gstr2binvoice.invtype+'</td><td class="text-left">'+billedtoname+'</td><td class="text-left">'+response.gstr2binvoice.invoiceno+'</td><td>'+formatDate(response.gstr2binvoice.dateofinvoice)+'</td><td class="text-left">'+response.gstr2binvoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2binvoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2binvoice.totaltax).toFixed(2))+'</td><td class="text-left">'+mstatus+'</td></tr>';
						}
					if(showInvTable) {
						showInvTable.clear().destroy();
					}
					if(showInvPRTable) {
						showInvPRTable.clear().destroy();
					}
					$('#showInvDetailTable_Body1').html(content);
				}else{
					if(response.gstr2binvoice != null){
						if(response.gstr2binvoice.billedtoname == '' || response.gstr2binvoice.billedtoname == null){billedtoname = "";}else{billedtoname=response.gstr2binvoice.billedtoname;}
						content += '<tr><td class="text-left">'+response.gstr2binvoice.invtype+'</td><td class="text-left">'+billedtoname+'</td><td class="text-left">'+response.gstr2binvoice.invoiceno+'</td><td>'+formatDate(response.gstr2binvoice.dateofinvoice)+'</td><td class="text-left">'+response.gstr2binvoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2binvoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2binvoice.totaltax).toFixed(2))+'</td><td class="text-left">'+mstatus+'</td></tr>';
					}
					if(response.PurchaseRegisterinvoice != null){
						if(response.PurchaseRegisterinvoice.billedtoname == '' || response.PurchaseRegisterinvoice.billedtoname == null){pbilledtoname = "";}else{pbilledtoname=response.PurchaseRegisterinvoice.billedtoname;}
						if(response.PurchaseRegisterinvoice.branch == '' || response.PurchaseRegisterinvoice.branch == null){branch = "-";}else{branch=response.PurchaseRegisterinvoice.branch;}
						content1 +='<tr><td class="text-left">'+response.PurchaseRegisterinvoice.invtype+'</td><td class="text-left">'+pbilledtoname+'</td><td class="text-left">'+response.PurchaseRegisterinvoice.invoiceno+'</td><td>'+formatDate(response.PurchaseRegisterinvoice.dateofinvoice)+'</td><td class="text-left">'+response.PurchaseRegisterinvoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.PurchaseRegisterinvoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.PurchaseRegisterinvoice.totaltax).toFixed(2))+'</td><td class="text-left"><span>'+branch+'</span></td><td class="text-left">'+mstatus+'</td></tr>';
					}
					
					if(showInvTable) {
						showInvTable.clear().destroy();
					}
					
					if(showInvPRTable) {
						showInvPRTable.clear().destroy();
					}
					$('#showInvDetailTable_Body1').html(content);
					$('#showInvDetailTable_Body').html(content1);
					
				}
				showInvTable = $('#showInvDetailTable1').DataTable({
					"dom": '<"toolbar">frtip',
					"paging": false,
					"searching": false,
					"responsive": true
					
				});
				showInvPRTable = $('#showInvDetailTable').DataTable({
					"dom": '<"toolbar">frtip',
					"paging": false,
					"searching": false,
					"responsive": true
					
				});
		},error:function(err){
			
		}
	});
}
function resetGstr2bReconsilationCounts(){
	ReconsilationMATCHED = 0;
	ReconsilationMATCHED_IN_OTHER_MONTHS = 0;
	ReconsilationROUNDOFF_MATCHED = 0;
	ReconsilationPROBABLE_MATCHED = 0;
	ReconsilationNOT_IN_PR = 0;
	ReconsilationNOT_IN_GSTR2B = 0;
	ReconsilationMISMATCHED = 0;
	ReconsilationINVOICENO_MISMATCHED = 0;
	ReconsilationTAX_MISMATCHED = 0;
	ReconsilationINVOICE_VALUE_MISMATCHED = 0;
	ReconsilationGST_NO_MISMATCHED = 0;
	ReconsilationINVOICE_DATE_MISMATCHED = 0;
	ReconsilationMANUAL_MATCHED = 0;
	
	gstr2bReconsileArray = new Object();
	Reconsilation_G2B_Macthed_Docid = new Array();
	Reconsilation_PR_Macthed_Docid = new Array();
	
	g2bManualMatchArr = new Array();
	Gstr2bReconsilationSummaryArray = new Array();
}

function updateGst2bReconsilationCounts(){
	
	$('#RecMismatchedInvoices').html(ReconsilationMISMATCHED);
	$('#RecMatchedInvoices').html(ReconsilationMATCHED);
	$('#RecMatchedInvoicesInOtherMonths').html(ReconsilationMATCHED_IN_OTHER_MONTHS);
	
	$('#RecNotInGstr2AInvoices').html(ReconsilationNOT_IN_GSTR2B);
	$('#RecNotInPurchasesInvoices').html(ReconsilationNOT_IN_PR);
	$('#RecGSTnoMismatchedInvoices').html(ReconsilationGST_NO_MISMATCHED);
	$('#RecInvoiceValueMismatchedInvoices').html(ReconsilationINVOICE_VALUE_MISMATCHED);
	$('#RecTaxMismatchedInvoices').html(ReconsilationTAX_MISMATCHED);
	$('#RecInvoiceNoMismatchInvoices').html(ReconsilationINVOICENO_MISMATCHED);
	$('#RecRoundoffMismatchedInvoices').html(ReconsilationROUNDOFF_MATCHED);
	$('#RecInvoiceDateMismatchedInvoices').html(ReconsilationINVOICE_DATE_MISMATCHED);
	$('#RecProbableMatchedInvoices').html(ReconsilationPROBABLE_MATCHED);
	$('#RecMannualMatchedInvoices').html(ReconsilationMANUAL_MATCHED);
}

function updateG2bMisMatchSelection(prid, g2id, gstin, chkBox) {
	var mObj = new Object();
	mObj.purchaseId = prid;
	mObj.gstrId = g2id;
	var id = prid;
	if(id == null){
		id = g2id;
	}
	if(chkBox.checked) {
		selG2bMatchArray.push(mObj);
		sendMsgArray.push(id);
		sendMsgCount++;
		if(gstMatchArray.length == 0){
			gstinnomatch = gstin;
			gstMatchArray.push(gstin);
			$('.sendmessage').removeClass('disable');
			$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2B\')');
		}else{
			if(jQuery.inArray(gstin, gstMatchArray ) == -1){
				gstnNotSelArray.push(gstin);
				$('.sendmessage').addClass('disable');
			}
		}
	} else {
		sendMsgCount--;
		var tArray=new Array();
		selG2bMatchArray.forEach(function(inv) {
			if(inv.purchaseId == id && inv.gstrId == gstrId) {
			} else {
				tArray.push(inv);
			}
		});
		selG2bMatchArray = tArray;
		var mArray=new Array();
		sendMsgArray.forEach(function(inv) {
			if(inv != id) {
				mArray.push(inv);
			}
		});
		sendMsgArray = mArray;
	
		if(jQuery.inArray(gstin, gstMatchArray ) == -1){
			gstnNotSelArray.splice(gstnNotSelArray.indexOf(gstin), 1);
			if(gstnNotSelArray.length > 0){
				$('.sendmessage').addClass('disable');
			}else{
				$('.sendmessage').removeClass('disable');
				$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2\')');
			}
		}else{
			if(sendMsgCount == 1){
				if(gstnNotSelArray.length > 0){
					gstMatchArray = gstnNotSelArray;
				}
					$('.sendmessage').removeClass('disable');
					$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2\')');
			}else{
				if(gstnNotSelArray.length > 0){
					$('.sendmessage').addClass('disable');
				}else{
					$('.sendmessage').removeClass('disable');
					$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2\')');
				}
			}
		}
		if(sendMsgCount == 0){
			$('.sendmessage').addClass('disable');
			gstMatchArray=new Array();
			gstnNotSelArray=new Array();
		}
	}
	if(selG2bMatchArray.length > 0) {
		$('#btnMisMatchAccept').removeClass('disable');
		$('#btnMisMatchReject').removeClass('disable');
		if(selG2bMatchArray.length == 1) {
			$('.g2bMannualMatching').removeClass('disable');
		}else{
			$('.g2bMannualMatching').addClass('disable');
		}
		$('.select_msg').text('You have Selected '+selG2bMatchArray.length+' Invoice(s)');
	} else {
		$('#btnMisMatchAccept').addClass('disable');
		$('#btnMisMatchReject').addClass('disable');
		$('.g2bMannualMatching').addClass('disable');
		$('.select_msg').text('');
		$('#checkMismatch').prop("checked",false);
	}
}

function g2bMannualMatchingInv(clientid, monthlyOryearly){
	var returnType;
	var invoiceid;
	if(selG2bMatchArray[0].purchaseId){
		returnType = 'Purchase Register';
		invoiceid = selG2bMatchArray[0].purchaseId;
		$('#invhdr').html('Invoice In Purchases');
		$('#dinvhdr').html('Invoice In GSTR2B');
	}else{
		returnType = 'GSTR2B';
		invoiceid = selG2bMatchArray[0].gstrId;
		$('#invhdr').html('Invoice In GSTR2B');
		$('#dinvhdr').html('Invoice In Purchases');
	}
	if(mannualMatchtable){
		mannualMatchtable.clear();
		mannualMatchtable.destroy();
	}
	g2bManualMatchArr = new Array();
	$.ajax({
		url: contextPath+"/g2bMannualMatchingInvoice/"+invoiceid+"/"+returnType,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			JSON.stringify(response);
			var invoice = updateInvoiceDetails(response);
			var maincontent = '<tr><td class="text-left">'+invoice.invoicetype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left" id="manInvoiceNo">'+invoice.invoiceno+'</td><td class="text-left" id="manInvoiceDate">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left" id="manGSTIN">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
				
				$('#mainMatchInvoices').html(maincontent);
		},error:function(err){
			
		}
	});
	var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
	var yer = year;
	if(monthlyOryearly == 'yearly'){
		yer = yrs;
	}else{
		yer = year;
	}
	/*if(returnType == 'GSTR2B'){
		returnType = 'Purchase Register';
	}else{
		returnType = 'GSTR2B';
	}*/
	var pUrl = contextPath+"/g2bInvoicesForMannualMatch/"+clientid+"/"+invoiceid+"/"+returnType+"/"+month+"/"+yer+"/"+monthlyOryearly;
	mannualMatchTableUrl["Mannualmatch"] = pUrl;
	mannualMatchtable = $('#mannualMatch_table5').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json; charset=utf-8',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.invoices.totalElements;
	        	 resp.recordsFiltered = resp.invoices.totalElements;
	        	 return resp.invoices.content;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[4,'desc']],
		'columns': getG2bInvManualMatchColumns(returnType),
		'columnDefs' : getManualMatchInvColumnDefs()
	});
	$('#mannualHiddenInvoiceid').val(invoiceid);
	$('#mannualHiddenReturnType').val(returnType);
	$('#mannualMatch_table5_wrapper').css('width','100%');
	$('#message_send_btn').attr("onclick","updateG2bMannualMatchData(this,'monthly')");
}

function getG2bInvManualMatchColumns(retTyp){
	var chkBx = {data: function ( data, type, row ) {
		if(retTyp == 'GSTR2B'){
			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invManFilter'+data.docId+'" onClick="updateG2bMannulaMatchSelection(null,\''+data.docId+'\',this)"/><i class="helper"></i></label></div>';			
		}else{
			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invManFilter'+data.docId+'" onClick="updateG2bMannulaMatchSelection(\''+data.docId+'\', null,this)"/><i class="helper"></i></label></div>';
		}
    }};
	
	var itype = {data: function ( data, type, row ) {
		return '<span class="text-left invoiceclk ">'+data.invtype+'</span>';
	 }};
	var invsNo = {data:  function ( data, type, row ) {
					var invoiceno = '';
					if(data.invoiceno != undefined){
						invoiceno = data.invoiceno;
					}
			      	 return '<span class="text-left invoiceclk ">'+invoiceno+'</span>';
			    }};
	var billtoname = {data: function ( data, type, row ) {
						var billedtoname = '';
						if(data.billedtoname != undefined){
							billedtoname = data.billedtoname;
						}
				      	 return '<span class="text-left invoiceclk ">'+billedtoname+'</span>';
				    }};
	var billtogtnn = {data: function ( data, type, row ) {
					
					var invtype = data.invtype;
					var gstnnum= '';
					var cfs = '';
					if(data.b2b && data.b2b.length > 0){
						if(data.b2b[0].ctin != undefined){
							gstnnum = data.b2b[0].ctin;
						}
					}else if(data.cdn && data.cdn.length > 0){
						if(data.cdn[0].ctin != undefined){
							gstnnum = data.cdn[0].ctin;
						}
					}
					 return '<span class="text-left invoiceclk ">'+gstnnum+'</span>';
			    }};
	
	var invDate = {data: function ( data, type, row ) {
					var dateofinvoice = "";
						if(data.dateofinvoice != null){
							var invDate = new Date(data.dateofinvoice);
							var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
							day = checkZero(day);month = checkZero(month);year = checkZero(year);
							dateofinvoice = day + "/" + month + "/" + year;
						}else{dateofinvoice = "";}
				      	 return '<span class="text-left invoiceclk ">'+(new Date(data.dateofinvoice)).toLocaleDateString("en-GB")+'</span>';
				    }};
	var taxblamt = {data: function ( data, type, row ) {
					var totalTaxableAmt = 0.0;
					if(data.totaltaxableamount){
						totalTaxableAmt = data.totaltaxableamount;
					}
				   	 return '<span class="ind_formats text-right invoiceclk ">'+formatNumber(totalTaxableAmt.toFixed(2))+'</span>';
				    }};
	var totlTax = {data: function ( data, type, row ) {
					var totalTax = 0.0;
					if(data.totaltax){
						totalTax = data.totaltax;
					}
				   	 return '<span id="tot_tax" class="ind_formats text-right invoiceclk ">'+formatNumber(totalTax.toFixed(2))+'</span>';
				    }};
	return [chkBx , itype, billtoname,invsNo, invDate, billtogtnn,taxblamt,  totlTax];
}

function getManualMatchInvColumnDefs(){
	return  [
		{
			"targets": 0,
			"orderable": false
		}
		
	];
}

function mannualMatchInvoiceFiltera(arrGSTNo, arrInvoiceNumber, arrInvoiceDate) {
	var pUrl = mannualMatchTableUrl["Mannualmatch"];
	var appd = '';
	if(arrGSTNo.length > 0){
		appd+='&gstno='+arrGSTNo.join(',');
	}
	if(arrInvoiceNumber.length > 0){
		appd+='&invoiceno='+arrInvoiceNumber.join(',');
	}
	if(arrInvoiceDate.length > 0){
		appd+='&dateofInvoice='+arrInvoiceDate.join(',');
	}
	pUrl += '?'+appd;
	mannualMatchtable.ajax.url(pUrl).load();
}

function updateG2bMannualMatchData(btn,monthlyOrYearly) {
	var invoiceid = $('#mannualHiddenInvoiceid').val();
	var returnType = $('#mannualHiddenReturnType').val();
	$(btn).addClass('btn-loader');
	$.ajax({
		type: "POST",
		url: contextPath+"/g2bmannualMatchArray/"+returnType+"/"+invoiceid,
		async: false,
		cache: false,
		data: JSON.stringify(g2bManualMatchArr),
		contentType: 'application/json',
		success : function(response) {
			if(monthlyOrYearly == 'monthly'){
				window.location.href = contextPath+'/alliview'+commonturnOverSuffix+'/'+varRetType+'/'+Paymenturlprefix+'?type=mmtch';
			}else{
				window.location.href = contextPath+'/reports'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=yearlyRecocileReport';
			}
			
		},
		error : function(e, status, error) {if(e.responseText) {errorNotification(e.responseText);}}
	});
}

function updateG2bMannulaMatchSelection(prid, g2Id, chkBox) {
	$('#message_send_btn').removeAttr('onclick');	
	var mObj=new Object();
	mObj.purchaseId = prid;
	mObj.gstrId = g2Id;
	if(chkBox.checked) {
		g2bManualMatchArr.push(mObj);
	}
	$('#message_send_btn').attr('onclick',"updateG2bMannualMatchData(this,'monthly')");
}
function viewG2bMannualMatchedInvoices(invoiceid, returntype){
	if(returntype == 'Purchase Register'){
		$('#vinvhdr').html('Invoice In Purchases');
		$('#vdinvhdr').html('Invoice In GSTR2B');
	}else{
		$('#vinvhdr').html('Invoice In GSTR2B');
		$('#vdinvhdr').html('Invoice In Purchases');
		
	}
	var fUrl = contextPath+"/g2bMannualMatchingInvoice/"+invoiceid+"/"+returntype;
	$.ajax({
		url: fUrl,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			JSON.stringify(response);
			var invoice = updateInvoiceDetails(response);
			var maincontent = '<tr><td class="text-left">'+invoice.invoicetype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left">'+invoice.invoiceno+'</td><td class="text-left">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
				$('#mainvMatchInvoices').html(maincontent);
		},error:function(err){
			
		}
	});
	$.ajax({
		url: contextPath+"/viewMannualG2bMatchingInvoices/"+invoiceid+"/"+returntype,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			var content = '';
			var vmannulaMatching = new Array();
			var fyList = null;
			if(response.content) {
				fyList = response.content;
			} else {
				fyList = response;
			}
			if(fyList instanceof Array) {
				if(fyList.length > 0) {
					fyList.forEach(function(fyInv){
						JSON.stringify(fyInv);
						var fyInvoice = updateInvoiceDetails(fyInv);
						vmannulaMatching.push(fyInvoice);
					});
				}
			}
			if(mannualViewMatchTable) {
				mannualViewMatchTable.clear().destroy();
			}
			
			if(vmannulaMatching == null || vmannulaMatching == undefined || vmannulaMatching.length == 0) {
			} else {
				vmannulaMatching.forEach(function(invoice) {
					content += '<tr><td class="text-left">'+invoice.invtype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left">'+invoice.invoiceno+'</td><td class="text-left">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
				});
				$('#viewMannulaMatchInvoices').html(content);
			}
			mannualViewMatchTable = $('#viewmannualMatch_table5').DataTable({
				"paging": true,
				"searching": true,
				"lengthMenu": [ [7,10, 25, 50, 100, 500], [7,10, 25, 50, 100, 500] ],
				"responsive": true,
				"ordering": true,
				"pageLength": 7,
				"language": {
					"search": "_INPUT_",
					"searchPlaceholder": "Search..."
				}
			});
			$('#viewmannualMatch_table5_wrapper').css('width','100%');
		},error:function(err){}
	});
}

function reconcileGstr2b(clientid, userid){
	$('.2breconbtn').addClass("btn-loader");
	$('.2breconbtn').html("Reconciling...");
	var cUrl = contextPath+'/subscriptionCheck/'+clientid+'/'+userid;
	$.ajax({
		url : cUrl,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response.status_cd == '0') {
				$('.2breconbtn').removeClass("btn-loader");
				 $('.2breconbtn').html("Reconcile");
				if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
					if(varUserType == 'suvidha' || varUserType == 'enterprise'){
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
					}else{
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+contextPath+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
					}
				}else {
					errorNotification(response.status_desc);
				}
			}else{
				window.location.href = contextPath+'/update2breconcileinv'+commonSuffix+'/'+clientid+'/GSTR2B/'+month+'/'+year;
			}
		}
	});	
}

