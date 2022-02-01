	<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<style>
	.missmultiselectuserlist ul {width:250px}
	</style>
	<div class="normaltable meterialform">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="Gstr2bReconsilationfilter">
					<span id="divcommonGstr2bReconsilationFilters"></span>
					<span class="btn-remove-tag" onclick="clearGstr2bReconsilationFilters('All')">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
			<div class="noramltable-row-desc">
				<select id="Gstr2bReconsilationMultiselect1" class="multiselect-ui form-control" multiple="multiple">
					<!-- <option value="Matched">Matched</option> -->
					<option value="Mismatched">Mismatched</option>
					<option value="Matched">Matched</option>
					<option value="Round Off Matched">Round Off Matched</option>
					<option value="Manual Matched">Manual Matched</option>
					<option value="Matched In Other Months">MATCHED IN OTHER MONTHS</option>
					<option value="Probable Matched">Probable Matched</option>
					<option value="Invoice No Mismatched">Invoice No Mismatched</option>
					<option value="Invoice Value Mismatched">Invoice Value Mismatched</option>
					<option value="Invoice Date Mismatched">Invoice Date Mismatched</option>
					<option value="GST No Mismatched">GST No Mismatched</option>
					<option value="Tax Mismatched">Tax Mismatched</option>
					<option value="Not In Purchases">Not In Purchases</option>
					<option value="Not In GSTR2B">Not In GSTR2B</option>
				</select>
				<select id="Gstr2bReconsilationMultiselect2" class="multiselect-ui form-control" multiple="multiple">
					<option value="B2B">B2B Invoices</option>
					<option value="B2BA">B2BA Invoices</option>
					<option value="Debit Note">Debit Note</option>
					<option value="Credit Note">Credit Note</option>
					<option value="CDNA">CDNA</option>
					<option value="<%=MasterGSTConstants.IMP_GOODS%>"><%=MasterGSTConstants.IMP_GOODS%></option>
					<option value="<%=MasterGSTConstants.IMP_SERVICES%>"><%=MasterGSTConstants.IMP_SERVICES%></option>
					<option value="<%=MasterGSTConstants.ISD%>">ISD</option>
				</select>
				<span class="multiselectuserlist">
					<select id="Gstr2bReconsilationMultiselect3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				<span class="missmultiselectuserlist">
				<select id="Gstr2bReconsilationMultiselect4" class="multiselect-ui form-control" multiple="multiple">
				</select>
				</span>
				<select id="Gstr2bReconsilationMultiselect5" class="multiselect-ui form-control" multiple="multiple">
				</select>
				<select id="Gstr2bReconsilationMultiselect6" class="multiselect-ui form-control" multiple="multiple">
				</select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idGstr2bReconsilationCount"> ${MismatchArray.length}</div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idGstr2bReconsilationTaxableVal"></div>
				</div>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idGstr2bReconsilationTaxVal"></div>
				</div>
				<div class="normaltable-col hdr">Total IGST
					<div class="normaltable-col-txt" id="idGstr2bReconsilationIGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CGST
					<div class="normaltable-col-txt" id="idGstr2bReconsilationCGST"></div>
				</div>
				<div class="normaltable-col hdr">Total SGST
					<div class="normaltable-col-txt" id="idGstr2bReconsilationSGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CESS
					<div class="normaltable-col-txt" id="idGstr2bReconsilationCESS"></div>
				</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
$(function() {
	<c:forEach items="${client.branches}" var="branch">
		$("#Gstr2bReconsilationMultiselect5").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
	</c:forEach>
	<c:forEach items="${client.verticals}" var="vertical">
		$("#Gstr2bReconsilationMultiselect6").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
	</c:forEach>
	$('#Gstr2bReconsilationMultiselect2').multiselect({
		nonSelectedText: '- Invoice Type -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGstr2bReconsilationFilter();
		},
		onSelectAll: function() {
			applyGstr2bReconsilationFilter();
		},
		onDeselectAll: function() {
			applyGstr2bReconsilationFilter();
		}
    });
	
	$('#Gstr2bReconsilationMultiselect1').multiselect({
		nonSelectedText: '- Status -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGstr2bReconsilationFilter();
		},
		onSelectAll: function() {
			applyGstr2bReconsilationFilter();
		},
		onDeselectAll: function() {
			applyGstr2bReconsilationFilter();
		}
    });

	$("#Gstr2bReconsilationMultiselect4").multiselect({
		nonSelectedText: '- Suppliers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGstr2bReconsilationFilter();
		},
		onSelectAll: function() {
			applyGstr2bReconsilationFilter();
		},
		onDeselectAll: function() {
			applyGstr2bReconsilationFilter();
		}
	});
	
	$('#Gstr2bReconsilationMultiselect5').multiselect({
		nonSelectedText: '- Branches -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGstr2bReconsilationFilter();
		},
		onSelectAll: function() {
			applyGstr2bReconsilationFilter();
		},
		onDeselectAll: function() {
			applyGstr2bReconsilationFilter();
		}
    });
	$('#Gstr2bReconsilationMultiselect6').multiselect({
		nonSelectedText: '- Verticals -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGstr2bReconsilationFilter();
		},
		onSelectAll: function() {
			applyGstr2bReconsilationFilter();
		},
		onDeselectAll: function() {
			applyGstr2bReconsilationFilter();
		}
    });
	$("#Gstr2bReconsilationMultiselect3").multiselect({
		nonSelectedText: '- User -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyGstr2bReconsilationFilter();
		},
		onSelectAll: function() {
			applyGstr2bReconsilationFilter();
		},
		onDeselectAll: function() {
			applyGstr2bReconsilationFilter();
		}
	});
});
function applyGstr2bReconsilationFilter() {
	selGstr2bReconsilationInvArray = new Array();
	$('.mannualMatching,.sendmessage').addClass('disable');
	$('#btnMisMatchAccept').addClass('disable');
	gstr2bReconsileTable.clear();
	var mtypeOptions = $('#Gstr2bReconsilationMultiselect1 option:selected');
	var typeOptions = $('#Gstr2bReconsilationMultiselect2 option:selected');
	var userOptions = $('#Gstr2bReconsilationMultiselect3 option:selected');
	var vendorOptions = $('#Gstr2bReconsilationMultiselect4 option:selected');
	var branchOptions =  $('#Gstr2bReconsilationMultiselect5 option:selected');
	var verticalOptions = $('#Gstr2bReconsilationMultiselect6 option:selected');
	if(mtypeOptions.length > 0 || typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0){
		$('.normaltable .filter').css("display","block");
		$('.Gstr2bReconsilationfilter .dataTables_length').css("margin-left","0px");
	}else{
		$('.normaltable .filter').css("display","none");
		$('.Gstr2bReconsilationfilter .dataTables_length').css("margin-left","0px");
	}
	var mtypeArr=new Array();
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var branchArr=new Array();
	var verticalArr=new Array();
	var filterContent='';
	if(mtypeOptions.length > 0) {
		for(var i=0;i<mtypeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+mtypeOptions[i].text+'<span data-val="'+mtypeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			mtypeArr.push(mtypeOptions[i].value);
		}
	} else {
		mtypeArr.push('All');
	}
	if(typeOptions.length > 0) {
		for(var i=0;i<typeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+typeOptions[i].text+'<span data-val="'+typeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			typeArr.push(typeOptions[i].value);
		}
	} else {
		typeArr.push('All');
	}
	if(userOptions.length > 0) {
		for(var i=0;i<userOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+userOptions[i].value+'<span data-val="'+userOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			userArr.push(userOptions[i].value);
		}
	} else {
		userArr.push('All');
	}
	if(vendorOptions.length > 0) {
		for(var i=0;i<vendorOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+vendorOptions[i].value+'<span data-val="'+vendorOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			vendorArr.push(vendorOptions[i].value);
		}
	} else {
		vendorArr.push('All');
	}
	
	if(branchOptions.length > 0) {
		for(var i=0;i<branchOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+branchOptions[i].value+'<span data-val="'+branchOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			branchArr.push(branchOptions[i].value);
		}
	} else {
		branchArr.push('All');
	}
	if(verticalOptions.length > 0) {
		for(var i=0;i<verticalOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+verticalOptions[i].value+'<span data-val="'+verticalOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			verticalArr.push(verticalOptions[i].value);
		}
	} else {
		verticalArr.push('All');
	}
	$('#divcommonGstr2bReconsilationFilters').html(filterContent);
	commonGstr2bReconsilationInvoiceFilter(mtypeArr, typeArr, userArr,vendorArr,branchArr,verticalArr);
	gstr2bReconsileTable.draw();
}
function clearGstr2bReconsilationFilters(type) {
	$('.mannualMatching,.sendmessage').addClass('disable');
	$('#btnMisMatchAccept').addClass('disable');
	selGstr2bReconsilationInvArray = new Array();
	$('.Gstr2bReconsilationfilter .dataTables_length').css("margin-left","0px");
	for(i=1;i<7;i++){
		//$('#selGstr2bReconsilationInvArrayMultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');		
		$('#Gstr2bReconsilationMultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');		
	}
	$('#divselGstr2bReconsilationInvArrayFilters').html('');
	$('.normaltable .filter').css("display","none");
	if(gstr2bReconsileTable){
		gstr2bReconsileTable.clear();	
	}
	
	selGstr2bReconsilationInvArray = new Array();
	commonGstr2bReconsilationInvoiceFilter(new Array(), new Array(), new Array(),new Array(),new Array(),new Array());
	if(gstr2bReconsileTable){
		gstr2bReconsileTable.draw();
	}
}
function clearselGstr2bReconsilationFilters() {
	$('.mannualMatching,.sendmessage').addClass('disable');
	$('#btnMisMatchAccept').addClass('disable');
	selMatchArray = new Array();
	$('.Gstr2bReconsilationfilter .dataTables_length').css("margin-left","0px");
	for(i=1;i<7;i++){
		if(i != 3){
			$('#Gstr2bReconsilationMultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');	
		}
	}
	$('#divcommonGstr2bReconsilationFilters').html('');
	$('.normaltable .filter').css("display","none");
	if(gstr2bReconsileTable){
		gstr2bReconsileTable.clear();	
	}
	
	selGstr2bReconsilationInvArray = new Array();
	commonGstr2bReconsilationInvoiceFilter(new Array(), new Array(), new Array(),new Array(),new Array(),new Array());
	if(gstr2bReconsileTable){
		gstr2bReconsileTable.draw();
	}
}
function commonGstr2bReconsilationInvoiceFilter(arrMType, arrInvType, arrUser, arrVendor, arrBranch, arrVertical) {
	if(Gstr2bReconsilationArray.length > 0) {
		var rows = new Array();
		var taxArray = new Array();
		Gstr2bReconsilationArray.forEach(function(invoice) {
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
			if(invoice.docId != undefined && invoice.docId != null){
				rows.push([ '<div class="checkbox" index="'+invoice.docId+'"><label><input type="checkbox" name="invMFilter'+invoice.docId+'" onclick="updateMisMatchSelection(\''+invoice.docId+'\', \''+invoice.docId+'\',\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>', '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2B</span></div>',invoice.invtype, invoice.billedtoname,invoice.gfp, invoice.ginvno, invoice.ginvdate,invoice.ggstno, invoice.ginvvalue,invoice.gtaxablevalue,invoice.gtotaltax, invoice.mstatus,invoice.gbranch,invoice.vertical,invoice.fullname,invoice.gcomments]);
			}else if(invoice.docId == undefined){
				rows.push([ '<div class="checkbox" index="'+invoice.docId+'"><label><input type="checkbox" name="invMFilter'+invoice.docId+'" onclick="updateMisMatchSelection(null,\''+invoice.docId+'\', \''+invoice.b2b[0].ctin+'\',this)"/><i class="helper"></i></label></div>', '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2B</span></div>',invoice.invtype, invoice.billedtoname,invoice.gfp, invoice.ginvno, invoice.ginvdate,invoice.ggstno, invoice.ginvvalue,invoice.gtaxablevalue,invoice.gtotaltax, invoice.mstatus,invoice.gbranch,invoice.vertical,invoice.fullname,invoice.gcomments]);
			}else if(invoice.docId == undefined){
				rows.push([ '<div class="checkbox" index="'+invoice.docId+'"><label><input type="checkbox" name="invMFilter'+invoice.docId+'" onclick="updateMisMatchSelection(\''+invoice.docId+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>', '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2B</span></div>',invoice.invtype, invoice.billedtoname, invoice.gfp,  invoice.ginvno, invoice.ginvdate,invoice.ggstno, invoice.ginvvalue,invoice.gtaxablevalue,invoice.gtotaltax, invoice.mstatus,invoice.gbranch,invoice.vertical,invoice.fullname,invoice.gcomments]);
			}
			taxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,creditDebit]);
		});
		var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0;
		rows.forEach(function(row) {
		  if((arrMType.length == 0 || $.inArray('All', arrMType) >= 0 || $.inArray(row[11], arrMType) >= 0)	
				&& (arrInvType.length == 0 || $.inArray('All', arrInvType) >= 0 || $.inArray(row[2], arrInvType) >= 0)
				&& (arrUser.length == 0 || $.inArray('All', arrUser) >= 0 || $.inArray(row[14], arrUser) >= 0)
				&& (arrVendor.length == 0 || $.inArray('All', arrVendor) >= 0 || $.inArray(row[3], arrVendor) >= 0)
				&& (arrBranch.length == 0 || $.inArray('All', arrBranch) >= 0 || $.inArray(row[12], arrBranch) >= 0)
				&& (arrVertical.length == 0 || $.inArray('All', arrVertical) >= 0 || $.inArray(row[13], arrVertical) >= 0)) {
			  var misstatus = row[11];
			  var mismatchStatus = '<span class="bluetxt f-13" index="mismatchStatus'+misstatus+'">'+misstatus+'</span>';
			  row[11] = mismatchStatus;
				gstr2bReconsileTable.row.add(row);
				transCount++;
				if(taxArray[index][6] != 'credit'){
					tIGST+=taxArray[index][0];
					tCGST+=taxArray[index][1];
					tSGST+=taxArray[index][2];
					tCESS+=taxArray[index][3];
					tTaxableAmount+=taxArray[index][4];
					tTotalTax+=taxArray[index][5];
				}else{
					tIGST+=taxArray[index][0];
					tCGST+=taxArray[index][1];
					tSGST+=taxArray[index][2];
					tCESS+=taxArray[index][3];
					tTaxableAmount-=taxArray[index][4];
					tTotalTax+=taxArray[index][0];
					tTotalTax+=taxArray[index][1];
					tTotalTax+=taxArray[index][2];
					tTotalTax+=taxArray[index][3];
				}
				
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
		$('.Gstr2bReconsilationfilter .dataTables_length').css("margin-left","0px");
	}
}
$('#divcommonGstr2bReconsilationFilters').on('click', '.deltag', function(e) {
	var val = $(this).data('val');
	$('#Gstr2bReconsilationMultiselect1').multiselect('deselect', [val]);
	$('#Gstr2bReconsilationMultiselect2').multiselect('deselect', [val]);
	$('#Gstr2bReconsilationMultiselect3').multiselect('deselect', [val]);
	$('#Gstr2bReconsilationMultiselect4').multiselect('deselect', [val]);
	$('#Gstr2bReconsilationMultiselect5').multiselect('deselect', [val]);
	$('#Gstr2bReconsilationMultiselect6').multiselect('deselect', [val]);
	applyGstr2bReconsilationFilter();
});
$(document).ready(function() {
	if('${totalTaxableValue}'){
		$('#idGstr2bReconsilationTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalTaxableValue}).toFixed(2)));
	}else{
		$('#idGstr2bReconsilationTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));	
	}
	if('${totalIGST}'){
		$('#idGstr2bReconsilationIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalIGST}).toFixed(2)));	
	}else{
		$('#idGstr2bReconsilationIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
	if('${totalCGST}'){
		$('#idGstr2bReconsilationCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalCGST}).toFixed(2)));
	}else{
		$('#idGstr2bReconsilationCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
	if('${totalSGST}'){
		$('#idGstr2bReconsilationSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalSGST}).toFixed(2)));
	}else{
		$('#idGstr2bReconsilationSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
	if('${totalCESS}'){
		$('#idGstr2bReconsilationCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalCESS}).toFixed(2)));
	}else{
		$('#idGstr2bReconsilationCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
	if('${totalTax}'){
		$('#idGstr2bReconsilationTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalTax}).toFixed(2)));
	}else{
		$('#idGstr2bReconsilationTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));	
	}
 });
 
function reconsilationUsers(cUrrl){
	$.ajax({
		url: contextPath+"/cp_users"+cUrrl,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if (response.length > 0) {
				$("#Gstr2bReconsilationMultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				response.forEach(function(cp_user) {
					$("#Gstr2bReconsilationMultiselect3").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
				});
			}else{
				$("#Gstr2bReconsilationMultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
			}
			$("#Gstr2bReconsilationMultiselect3").multiselect('rebuild');
		}
	});
}
</script>