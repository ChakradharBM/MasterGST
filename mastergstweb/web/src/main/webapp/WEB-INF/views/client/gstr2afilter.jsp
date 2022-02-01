	<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<div class="normaltable meterialform">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="gstr2afilter">
					<span id="divG2Filters"></span>
					<span class="btn-remove-tag" onclick="clearG2Filters()">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
			<div class="noramltable-row-desc">
				<select id="G2multiselect2" class="multiselect-ui form-control" multiple="multiple">
					<option value="<%=MasterGSTConstants.ADVANCES%>">Advance Tax</option>
					<option value="<%=MasterGSTConstants.ATPAID%>">Advance Adjusted</option>
					<option value="B2B">B2B Invoices</option>
					<option value="<%=MasterGSTConstants.B2BUR%>"><%=MasterGSTConstants.B2BUR%></option>
					<option value="B2C">B2CS (Small) Invoices</option>
					<option value="B2CL">B2CL (Large)</option>
					<option value="<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>"><%=MasterGSTConstants.CREDIT_DEBIT_NOTES%></option>
					<option value="<%=MasterGSTConstants.CDNUR%>">Credit/Debit Note for Unregistered</option>
					<option value="<%=MasterGSTConstants.EXPORTS%>"><%=MasterGSTConstants.EXPORTS%></option>
					<option value="<%=MasterGSTConstants.IMP_GOODS%>"><%=MasterGSTConstants.IMP_GOODS%></option>
					<option value="<%=MasterGSTConstants.IMP_SERVICES%>"><%=MasterGSTConstants.IMP_SERVICES%></option>
					<option value="<%=MasterGSTConstants.ITC_REVERSAL%>"><%=MasterGSTConstants.ITC_REVERSAL%></option>
					<option value="<%=MasterGSTConstants.NIL%>">Nil Rated / Exempted / Non-GST</option>
				</select>
				<span class="multiselectuserlist">
					<select id="G2multiselect3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				<select id="G2multiselect4" class="multiselect-ui form-control" multiple="multiple">
				<c:if test="${not empty vendorList}">
				<c:forEach items="${vendorList}" var="vendor">
					<option value="${vendor}">${vendor}</option>
				</c:forEach>
				</c:if>
				</select>
				<select id="G2multiselect5" class="multiselect-ui form-control" multiple="multiple">
				</select>
				<select id="G2multiselect6" class="multiselect-ui form-control" multiple="multiple">
				</select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idG2Count"> ${fn:length(invoices)}</div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idG2TaxableVal"></div>
				</div>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idG2TaxVal"></div>
				</div>
				<div class="normaltable-col hdr">Total Amount
					<div class="normaltable-col-txt" id="idG2TotalVal"></div>
				</div>
				<div class="normaltable-col hdr">Total IGST
					<div class="normaltable-col-txt" id="idG2IGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CGST
					<div class="normaltable-col-txt" id="idG2CGST"></div>
				</div>
				<div class="normaltable-col hdr">Total SGST
					<div class="normaltable-col-txt" id="idG2SGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CESS
					<div class="normaltable-col-txt" id="idG2CESS"></div>
				</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
$(function() {
	gstr2aTable=$('table#dbTable6').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		"paging": true,
		"searching": true,
		"responsive": true,
		"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
		"columnDefs": [
		{
			"targets": [ 5,6,7 ],
			"visible": false,
			"searchable": true
		}
		]
	});
	$.ajax({
		url: "${contextPath}/cp_users/${id}/${client.id}",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if (response.length > 0) {
				$("#G2multiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				response.forEach(function(cp_user) {
					$("#G2multiselect3").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
				});
			}else{
				$("#G2multiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
			}
			$("#G2multiselect3").multiselect({
					nonSelectedText: '- User -',
					includeSelectAllOption: true,
					onChange: function(element, checked) {
						applyG2Filters();
					},
					onSelectAll: function() {
						applyG2Filters();
					},
					onDeselectAll: function() {
						applyG2Filters();
					}
				});
			$("#G2multiselect3").multiselect('refresh');
		}
	});
	<c:forEach items="${client.branches}" var="branch">
		$("#G2multiselect5").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
	</c:forEach>
	<c:forEach items="${client.verticals}" var="vertical">
		$("#G2multiselect6").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
	</c:forEach>
	$('#G2multiselect2').multiselect({
		nonSelectedText: '- Invoice Type -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyG2Filters();
		},
		onSelectAll: function() {
			applyG2Filters();
		},
		onDeselectAll: function() {
			applyG2Filters();
		}
    });
	$('#G2multiselect4').multiselect({
		nonSelectedText: '- Suppliers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyG2Filters();
		},
		onSelectAll: function() {
			applyG2Filters();
		},
		onDeselectAll: function() {
			applyG2Filters();
		}
    });
	$('#G2multiselect5').multiselect({
		nonSelectedText: '- Branches -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyG2Filters();
		},
		onSelectAll: function() {
			applyG2Filters();
		},
		onDeselectAll: function() {
			applyG2Filters();
		}
    });
	$('#G2multiselect6').multiselect({
		nonSelectedText: '- Verticals -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyG2Filters();
		},
		onSelectAll: function() {
			applyG2Filters();
		},
		onDeselectAll: function() {
			applyG2Filters();
		}
    });
});
function applyG2Filters() {
	gstr2aTable.clear();
	var typeOptions = $('#G2multiselect2 option:selected');
	var userOptions = $('#G2multiselect3 option:selected');
	var vendorOptions = $('#G2multiselect4 option:selected');
	var branchOptions = $('#G2multiselect5 option:selected');
	var verticalOptions = $('#G2multiselect6 option:selected');
	if(typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions > 0 || verticalOptions > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var branchArr=new Array();
	var verticalArr=new Array();
	var filterContent='';
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
			if((vendorOptions[i].value).includes("&")){
				vendorOptions[i].value = (vendorOptions[i].value).replace("&","-mgst-");
			}
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
	$('#divG2Filters').html(filterContent);
	commonG2InvoiceFilter(typeArr, userArr,vendorArr,branchArr,verticalArr);
	gstr2aTable.draw();
	OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formats'
	});
}
function clearG2Filters() {
	$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	$('#divG2Filters').html('');
	$('.normaltable .filter').css("display","none");
	gstr2aTable.clear();
	selInvArray=new Array();
	commonG2InvoiceFilter(new Array(), new Array(),new Array(),new Array(),new Array());
	gstr2aTable.draw();
	OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formats'
	});
}
function commonG2InvoiceFilter(arrInvType, arrUser, arrVendor, arrBranch, arrVertical) {
	if(Gstr2aArray.length > 0) {
		var rows = new Array();
		var taxArray = new Array();
		var rowNode;
		Gstr2aArray.forEach(function(invoice) {
			rows.push([ invoice.invoicetype, invoice.serialnoofinvoice, invoice.billedtogstin, invoice.billedtoname, invoice.dateofinvoice, invoice.fullname, invoice.branch,invoice.vertical,invoice.totaltaxableamount, invoice.totalamount, invoice.totaltax]);
			taxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,invoice.totalamount]);
		});
		var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0, tTotalAmount=0;
		rows.forEach(function(row) {
		  if((arrInvType.length == 0 || $.inArray('All', arrInvType) >= 0 || $.inArray(row[0], arrInvType) >= 0)
				&& (arrUser.length == 0 || $.inArray('All', arrUser) >= 0 || $.inArray(row[5], arrUser) >= 0)
				&& (arrVendor.length == 0 || $.inArray('All', arrVendor) >= 0 || $.inArray(row[3], arrVendor) >= 0)
				&& (arrBranch.length == 0 || $.inArray('All', arrBranch) >= 0 || $.inArray(row[6], arrBranch) >= 0)
				&& (arrVertical.length == 0 || $.inArray('All', arrVertical) >= 0 || $.inArray(row[7], arrVertical) >= 0)) {
				row[8] = "<span class='ind_formats'>"+row[8]+"</span>";
				row[9] = "<span class='ind_formats'>"+row[9]+"</span>";
				row[10] = "<span class='ind_formats'>"+row[10]+"</span>";
				rowNode = gstr2aTable.row.add(row);
				Gstr2aArray.forEach(function(invoice) {
					if(invoice.serialnoofinvoice == row[2]) {
						$(rowNode.node()).children().addClass('invoiceclk').attr('onclick',"editInvPopup(null,'"+varRetType+"','"+invoice.id+"')");
					}
				});
				gstr2aTable.row(rowNode).column(8).nodes().to$().addClass('text-right');
				gstr2aTable.row(rowNode).column(9).nodes().to$().addClass('text-right');
				gstr2aTable.row(rowNode).column(10).nodes().to$().addClass('text-right');
				transCount++;
				tIGST+=taxArray[index][0];
				tCGST+=taxArray[index][1];
				tSGST+=taxArray[index][2];
				tCESS+=taxArray[index][3];
				tTaxableAmount+=parseFloat(taxArray[index][4]);
				tTotalTax+=parseFloat(taxArray[index][5]);
				tTotalAmount+=parseFloat(taxArray[index][6]);
		  }
		  index++;
		});
		$('#idG2Count').html(transCount);
		$('#idG2IGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tIGST).toFixed(2)));
		$('#idG2CGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCGST).toFixed(2)));
		$('#idG2SGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tSGST).toFixed(2)));
		$('#idG2CESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCESS).toFixed(2)));
		$('#idG2TaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTaxableAmount).toFixed(2)));
		$('#idG2TaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalTax).toFixed(2)));
		$('#idG2TotalVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalAmount).toFixed(2)));		
	}
}
$('#divG2Filters').on('click', '.deltag', function(e) {
	var val = $(this).data('val');
	$('#G2multiselect2').multiselect('deselect', [val]);
	$('#G2multiselect3').multiselect('deselect', [val]);
	$('#G2multiselect4').multiselect('deselect', [val]);
	$('#G2multiselect5').multiselect('deselect', [val]);
	$('#G2multiselect6').multiselect('deselect', [val]);
	applyG2Filters();
});
$(document).ready(function() {
	 $('#idG2TaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalTaxableValue}).toFixed(2)));
	$('#idG2IGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalIGST}).toFixed(2)));
	$('#idG2CGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalCGST}).toFixed(2)));
	$('#idG2SGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalSGST}).toFixed(2)));
	$('#idG2CESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalCESS}).toFixed(2)));
	$('#idG2TaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalTax}).toFixed(2)));
	$('#idG2TotalVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalValue}).toFixed(2)));
 });
</script>