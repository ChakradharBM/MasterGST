	<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<style>
	.missmultiselectuserlist ul {width:250px}
	</style>
	<div class="normaltable meterialform">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="mismatchfilter">
					<span id="divMMFilters"></span>
					<span class="btn-remove-tag" onclick="clearMMFilters()">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
			<div class="noramltable-row-desc">
				<select id="MMmultiselect1" class="multiselect-ui form-control" multiple="multiple">
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
					<option value="Not In GSTR 2A">Not In GSTR 2A</option>
				</select>
				<select id="MMmultiselect2" class="multiselect-ui form-control" multiple="multiple">
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
					<select id="MMmultiselect3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				<span class="missmultiselectuserlist">
				<select id="MMmultiselect4" class="multiselect-ui form-control" multiple="multiple">
				</select>
				</span>
				<select id="MMmultiselect5" class="multiselect-ui form-control" multiple="multiple">
				</select>
				<select id="MMmultiselect6" class="multiselect-ui form-control" multiple="multiple">
				</select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idMMCount"> ${MismatchArray.length}</div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idMMTaxableVal"></div>
				</div>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idMMTaxVal"></div>
				</div>
				<div class="normaltable-col hdr">Total IGST
					<div class="normaltable-col-txt" id="idMMIGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CGST
					<div class="normaltable-col-txt" id="idMMCGST"></div>
				</div>
				<div class="normaltable-col hdr">Total SGST
					<div class="normaltable-col-txt" id="idMMSGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CESS
					<div class="normaltable-col-txt" id="idMMCESS"></div>
				</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
$(function() {
	<c:forEach items="${client.branches}" var="branch">
		$("#MMmultiselect5").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
	</c:forEach>
	<c:forEach items="${client.verticals}" var="vertical">
		$("#MMmultiselect6").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
	</c:forEach>
	$('#MMmultiselect2').multiselect({
		nonSelectedText: '- Invoice Type -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyMMFilters();
		},
		onSelectAll: function() {
			applyMMFilters();
		},
		onDeselectAll: function() {
			applyMMFilters();
		}
    });
	
	$('#MMmultiselect1').multiselect({
		nonSelectedText: '- Status -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyMMFilters();
		},
		onSelectAll: function() {
			applyMMFilters();
		},
		onDeselectAll: function() {
			applyMMFilters();
		}
    });

	$("#MMmultiselect4").multiselect({
					nonSelectedText: '- Suppliers -',
					includeSelectAllOption: true,
					onChange: function(element, checked) {
						applyMMFilters();
					},
					onSelectAll: function() {
						applyMMFilters();
					},
					onDeselectAll: function() {
						applyMMFilters();
					}
				});
	
	$('#MMmultiselect5').multiselect({
		nonSelectedText: '- Branches -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyMMFilters();
		},
		onSelectAll: function() {
			applyMMFilters();
		},
		onDeselectAll: function() {
			applyMMFilters();
		}
    });
	$('#MMmultiselect6').multiselect({
		nonSelectedText: '- Verticals -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyMMFilters();
		},
		onSelectAll: function() {
			applyMMFilters();
		},
		onDeselectAll: function() {
			applyMMFilters();
		}
    });
});
function applyMMFilters() {
	selMatchArray = new Array();
	$('.mannualMatching,.sendmessage').addClass('disable');
	$('#btnMisMatchAccept').addClass('disable');
	mismatchTable.clear();
	var mtypeOptions = $('#MMmultiselect1 option:selected');
	var typeOptions = $('#MMmultiselect2 option:selected');
	var userOptions = $('#MMmultiselect3 option:selected');
	var vendorOptions = $('#MMmultiselect4 option:selected');
	var branchOptions = $('#MMmultiselect5 option:selected');
	var verticalOptions = $('#MMmultiselect6 option:selected');
	if(mtypeOptions.length > 0 || typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions > 0 || verticalOptions > 0){
		$('.normaltable .filter').css("display","block");
		$('.reconciletablee .dataTables_length').css("margin-left","0px");
	}else{
		$('.normaltable .filter').css("display","none");
		$('.reconciletablee .dataTables_length').css("margin-left","0px");
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
	$('#divMMFilters').html(filterContent);
	commonMMInvoiceFilter(mtypeArr, typeArr, userArr,vendorArr,branchArr,verticalArr);
	mismatchTable.draw();
}
function clearMMFilters() {
	$('.mannualMatching,.sendmessage').addClass('disable');
	$('#btnMisMatchAccept').addClass('disable');
	selMatchArray = new Array();
	$('.reconciletablee .dataTables_length').css("margin-left","0px");
	for(i=1;i<7;i++){
			$('#MMmultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');		
	}
	$('#divMMFilters').html('');
	$('.normaltable .filter').css("display","none");
	if(mismatchTable){
		mismatchTable.clear();	
	}
	
	selInvArray=new Array();
	commonMMInvoiceFilter(new Array(), new Array(), new Array(),new Array(),new Array(),new Array());
	if(mismatchTable){
		mismatchTable.draw();
	}
}
function clearMMMFilters() {
	$('.mannualMatching,.sendmessage').addClass('disable');
	$('#btnMisMatchAccept').addClass('disable');
	selMatchArray = new Array();
	$('.reconciletablee .dataTables_length').css("margin-left","0px");
	for(i=1;i<7;i++){
		
		if(i != 3){
			
			$('#MMmultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');	
		}
		
	}
	$('#divMMFilters').html('');
	$('.normaltable .filter').css("display","none");
	if(mismatchTable){
		mismatchTable.clear();	
	}
	
	selInvArray=new Array();
	commonMMInvoiceFilter(new Array(), new Array(), new Array(),new Array(),new Array(),new Array());
	if(mismatchTable){
		mismatchTable.draw();
	}
}
function commonMMInvoiceFilter(arrMType, arrInvType, arrUser, arrVendor, arrBranch, arrVertical) {
	if(MismatchArray.length > 0) {
		var rows = new Array();
		var taxArray = new Array();
		MismatchArray.forEach(function(invoice) {
			
			var creditDebit = 'credit';
				var invtype = invoice.invtype;
				if(invtype == 'Debit Note' || invtype == 'Credit Note' ){
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
				}else{
					creditDebit = 'debit';
				}
			if(invoice.id != undefined && invoice.gstrid != undefined){
				rows.push([ '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onclick="updateMisMatchSelection(\''+invoice.id+'\', \''+invoice.gstrid+'\',\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>', '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div>',invoice.invtype, invoice.billedtoname,invoice.gfp, invoice.fullname, invoice.vertical,invoice.invoiceCustomerIdAndBilledToName, invoice.ginvno, invoice.ginvdate,invoice.ggstno, invoice.ginvvalue,invoice.gtaxablevalue,invoice.gtotaltax, invoice.branch,invoice.mstatus,invoice.gcomments]);
			}else if(invoice.id == undefined){
				rows.push([ '<div class="checkbox" index="'+invoice.gstrid+'"><label><input type="checkbox" name="invMFilter'+invoice.gstrid+'" onclick="updateMisMatchSelection(null, \''+invoice.gstrid+'\', \''+invoice.b2b[0].ctin+'\',this)"/><i class="helper"></i></label></div>', '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div>',invoice.invtype, invoice.billedtoname,invoice.gfp, invoice.fullname, invoice.vertical,invoice.invoiceCustomerIdAndBilledToName, invoice.ginvno, invoice.ginvdate,invoice.ggstno, invoice.ginvvalue,invoice.gtaxablevalue,invoice.gtotaltax, invoice.branch,invoice.mstatus,invoice.gcomments]);
			}else if(invoice.gstrid == undefined){
				rows.push([ '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onclick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>', '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div>',invoice.invtype, invoice.billedtoname, invoice.gfp,invoice.fullname, invoice.vertical,invoice.invoiceCustomerIdAndBilledToName, invoice.ginvno, invoice.ginvdate,invoice.ggstno, invoice.ginvvalue,invoice.gtaxablevalue,invoice.gtotaltax, invoice.branch,invoice.mstatus,invoice.gcomments]);
			}
			taxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,creditDebit]);
		});
		var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0;
		rows.forEach(function(row) {
		  if((arrMType.length == 0 || $.inArray('All', arrMType) >= 0 || $.inArray(row[15], arrMType) >= 0)	&& (arrInvType.length == 0 || $.inArray('All', arrInvType) >= 0 || $.inArray(row[2], arrInvType) >= 0)
				&& (arrUser.length == 0 || $.inArray('All', arrUser) >= 0 || $.inArray(row[5], arrUser) >= 0)
				&& (arrVendor.length == 0 || $.inArray('All', arrVendor) >= 0 || $.inArray(row[7], arrVendor) >= 0)
				&& (arrBranch.length == 0 || $.inArray('All', arrBranch) >= 0 || $.inArray(row[14], arrBranch) >= 0)
				&& (arrVertical.length == 0 || $.inArray('All', arrVertical) >= 0 || $.inArray(row[6], arrVertical) >= 0)) {
			  var misstatus = row[15];
			  var mismatchStatus = '<span class="bluetxt f-13" index="mismatchStatus'+misstatus+'">'+misstatus+'</span>';
			  row[15] = mismatchStatus;
				mismatchTable.row.add(row);
				transCount++;
				if(taxArray[index][6] != 'credit'){
					tIGST+=taxArray[index][0];
					tCGST+=taxArray[index][1];
					tSGST+=taxArray[index][2];
					tCESS+=taxArray[index][3];
					tTaxableAmount+=parseFloat(taxArray[index][4]);
					tTotalTax+=parseFloat(taxArray[index][5]);
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
		$('#idMMCount').html(transCount);
		$('#idMMIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tIGST).toFixed(2)));
		$('#idMMCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCGST).toFixed(2)));
		$('#idMMSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tSGST).toFixed(2)));
		$('#idMMCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCESS).toFixed(2)));
		$('#idMMTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTaxableAmount).toFixed(2)));
		$('#idMMTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalTax).toFixed(2)));
	}else{
		$('.reconciletablee .dataTables_length').css("margin-left","0px");
	}
}
$('#divMMFilters').on('click', '.deltag', function(e) {
	var val = $(this).data('val');
	$('#MMmultiselect1').multiselect('deselect', [val]);
	$('#MMmultiselect2').multiselect('deselect', [val]);
	$('#MMmultiselect3').multiselect('deselect', [val]);
	$('#MMmultiselect4').multiselect('deselect', [val]);
	$('#MMmultiselect5').multiselect('deselect', [val]);
	$('#MMmultiselect6').multiselect('deselect', [val]);
	applyMMFilters();
});
$(document).ready(function() {
	if('${totalTaxableValue}'){
		$('#idMMTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalTaxableValue}).toFixed(2)));
	}else{
		$('#idMMTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));	
	}
	if('${totalIGST}'){
		$('#idMMIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalIGST}).toFixed(2)));	
	}else{
		$('#idMMIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
	if('${totalCGST}'){
		$('#idMMCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalCGST}).toFixed(2)));
	}else{
		$('#idMMCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
	if('${totalSGST}'){
		$('#idMMSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalSGST}).toFixed(2)));
	}else{
		$('#idMMSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
	if('${totalCESS}'){
		$('#idMMCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalCESS}).toFixed(2)));
	}else{
		$('#idMMCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
	if('${totalTax}'){
		$('#idMMTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalTax}).toFixed(2)));
	}else{
		$('#idMMTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));	
	}
 });
</script>