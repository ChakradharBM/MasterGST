<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<div class="normaltable meterialform">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
                <div class="pfilter"> 
                <span  id="divPFilters"></span>
				<span class="btn-remove-tag" onclick="clearPFilters()">Clear All<span data-role="remove"></span></span>
               </div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
			<div class="noramltable-row-desc">
				<select id="pmultiselect2" class="multiselect-ui form-control" multiple="multiple">
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
					<select id="pmultiselect3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				<span class="multiselectsupplierlist">
					<select id="pmultiselect4" class="multiselect-ui form-control" multiple="multiple">
					<c:if test="${not empty vendorList}">
					<c:forEach items="${vendorList}" var="vendor">
						<option value="${vendor}">${vendor}</option>
					</c:forEach>
					</c:if>
					</select>
				</span>
				<select id="pmultiselect5" class="multiselect-ui form-control" multiple="multiple">
				</select>
				<select id="pmultiselect6" class="multiselect-ui form-control" multiple="multiple">
				</select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idPCount"> ${fn:length(purchaseList)}</div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
				<div class="normaltable-col-txt" id="idPTaxableVal"> 
					</div>
				</div>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idPTaxVal"> 
					</div>
				</div>
				<div class="normaltable-col hdr">Total Amount
					<div class="normaltable-col-txt" id="idPTotAmt"> 
					</div>
				</div>
				<div class="normaltable-col hdr filsummary">Total IGST
					<div class="normaltable-col-txt" id="idPIGST"> 
					</div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CGST
					<div class="normaltable-col-txt" id="idPCGST"> 
					</div>
				</div>
				<div class="normaltable-col hdr filsummary">Total SGST
					<div class="normaltable-col-txt" id="idPSGST"> 
					</div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CESS
					<div class="normaltable-col-txt" id="idPCESS"> 
					</div>
				</div>
				<div class="normaltable-col hdr">ITC Available
					<div class="normaltable-col-txt" id="idPITC"> 
					</div>
				</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
$(function() {
	purchaseTable=$('table#dbTable7').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		"paging": true,
		"searching": true,
		"responsive": true,
		"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
		"columnDefs": [
		{
			"targets": [ 6,11,12,13 ],
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
				$("#pmultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				response.forEach(function(cp_user) {
					$("#pmultiselect3").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
				});
			}else{
				$("#pmultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
			}
			$("#pmultiselect3").multiselect({
					nonSelectedText: '- User -',
					includeSelectAllOption: true,
					onChange: function(element, checked) {
						applyPFilters();
					},
					onSelectAll: function() {
						applyPFilters();
					},
					onDeselectAll: function() {
						applyPFilters();
					}
				});
			$("#pmultiselect3").multiselect('refresh');
		}
	});
	<c:forEach items="${client.branches}" var="branch">
		$("#pmultiselect5").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
	</c:forEach>
	<c:forEach items="${client.verticals}" var="vertical">
		$("#pmultiselect6").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
	</c:forEach>
	$('#pmultiselect2').multiselect({
		nonSelectedText: '- Invoice Type -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyPFilters();
		},
		onSelectAll: function() {
			applyPFilters();
		},
		onDeselectAll: function() {
			applyPFilters();
		}
    });
	$('#pmultiselect5').multiselect({
		nonSelectedText: '- Branches -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyPFilters();
		},
		onSelectAll: function() {
			applyPFilters();
		},
		onDeselectAll: function() {
			applyPFilters();
		}
    });
	$('#pmultiselect6').multiselect({
		nonSelectedText: '- Verticals -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyPFilters();
		},
		onSelectAll: function() {
			applyPFilters();
		},
		onDeselectAll: function() {
			applyPFilters();
		}
    });
	$('#pmultiselect4').multiselect({
		nonSelectedText: '- Suppliers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyPFilters();
		},
		onSelectAll: function() {
			applyPFilters();
		},
		onDeselectAll: function() {
			applyPFilters();
		}
    });
});

function applyPFilters() {
	purchaseTable.clear();
	var typeOptions = $('#pmultiselect2 option:selected');
	var userOptions = $('#pmultiselect3 option:selected');
	var vendorOptions = $('#pmultiselect4 option:selected');
	var branchOptions = $('#pmultiselect5 option:selected');
	var verticalOptions = $('#pmultiselect6 option:selected');
	if(typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0){
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
	if(vendorOptions.length > 0) {
		for(var i=0;i<vendorOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+vendorOptions[i].value+'<span data-val="'+vendorOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			vendorArr.push(vendorOptions[i].value);
		}
	} else {
		vendorArr.push('All');
	}
	$('#divPFilters').html(filterContent);
	commonPInvoiceFilter(typeArr, userArr,vendorArr,branchArr,verticalArr);
	purchaseTable.draw();
	OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formats'
	});
}
function clearPFilters() {
	$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	$('#divPFilters').html('');
	$('.normaltable .filter').css("display","none");
	purchaseTable.clear();
	selInvArray=new Array();
	commonPInvoiceFilter(new Array(), new Array(),new Array(),new Array(),new Array());
	purchaseTable.draw();
	OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formats'
	});
}
function commonPInvoiceFilter(arrInvType, arrUser, arrVendor, arrBranch, arrVertical) {
	if(prchArray.length > 0) {
		var rows = new Array();
		var taxArray = new Array();
		var rr;
		var invid;
		prchArray.forEach(function(invoice) {
			rows.push(['<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" onclick="updatePSelection(\''+invoice.id+'\', this)" id="invFilter'+invoice.id+'"/><i class="helper"></i></label></div>', invoice.invoicetype, invoice.serialnoofinvoice, invoice.billedtogstin, invoice.billedtoname, invoice.dateofinvoice, invoice.fullname, invoice.totaltaxableamount, invoice.totaltax, invoice.totalamount, invoice.totalitc,invoice.branch,invoice.vertical,invoice.id, '<a onclick="editInvPopup(null,\'${returntype}\',\''+invoice.id+'\')" > <img src="${contextPath}/static/mastergst/images/dashboard-ca/editdd.png" alt="Edit"></a>&nbsp;&nbsp;<a href="${contextPath}/invprint/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${client.id}/'+invoice.id+'/Purchase Register" target="_blank"> <img src="${contextPath}/static/mastergst/images/master/printicon.png" alt="Print"></a>&nbsp;&nbsp;<a href="#" onclick="showDeletePopup(\''+invoice.id+'\',\''+invoice.serialnoofinvoice+'\',\'${varPurchase}\')"><img src="${contextPath}/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete"></a>']);
			taxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,invoice.totalitc,invoice.totalamount]);
		});
		var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0, tTotalItc=0, tTotalAmount=0;
		rows.forEach(function(row) {
		  if((arrInvType.length == 0 || $.inArray('All', arrInvType) >= 0 || $.inArray(row[1], arrInvType) >= 0)
				&& (arrUser.length == 0 || $.inArray('All', arrUser) >= 0 || $.inArray(row[6], arrUser) >= 0)
				&& (arrVendor.length == 0 || $.inArray('All', arrVendor) >= 0 || $.inArray(row[4], arrVendor) >= 0)
				&& (arrBranch.length == 0 || $.inArray('All', arrBranch) >= 0 || $.inArray(row[11], arrBranch) >= 0)
				&& (arrVertical.length == 0 || $.inArray('All', arrVertical) >= 0 || $.inArray(row[12], arrVertical) >= 0)) {
				row[7] = '<span class="ind_formats" >'+row[7]+'</span>';
				row[8] = '<span class="ind_formats">'+row[8]+'</span>';
				row[9] = '<span class="ind_formats">'+row[9]+'</span>';
				row[10] = '<span class="ind_formats">'+row[10]+'</span><div class="dropdown pl-1" style="display: initial;"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding: 0px;border-radius: 2px;"><span class="caret"></span></i><ul class="dropdown-menu" style="padding: 15px; margin-left: -161px;"><li>Input Type<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/close-icon.png" alt="Close"  style="height: 24px;position: absolute; top: 5px; right: 4%; color: #5769bb;"/></span></li><li><form><select id="'+row[13]+'itc_droptype" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Input Good</option><option value="is">Ineligible</option><option value="no">Not Selected</option></select></form></li><li>ITC Amount</li><li><input id="'+row[13]+'itc_dropamt" type="text" class="form-control itc_amount" value="100" style="width: 45px;"><span style="margin-right: 13px;">%</span><button class="btn btn-blue" onclick="updateITCType(\''+row[13]+'\')" value="ok"> ok </button></li></ul></div>';
				rr = purchaseTable.row.add(row);
				prchArray.forEach(function(invoice) {
					if(invoice.serialnoofinvoice == row[2]) {
						$(rr.node()).children().addClass('invoiceclk').attr('onclick',"editInvPopup(null,'"+varRetType+"','"+invoice.id+"')");
					}
				});
				purchaseTable.row(rr).column(14).nodes().to$().addClass('text-right').removeAttr('onclick');
				purchaseTable.row(rr).column(10).nodes().to$().addClass('text-right').removeAttr('onclick');
				purchaseTable.row(rr).column(9).nodes().to$().addClass('text-right');
				purchaseTable.row(rr).column(8).nodes().to$().addClass('text-right');
				purchaseTable.row(rr).column(7).nodes().to$().addClass('text-right');
				transCount++;
				tIGST+=taxArray[index][0];
				tCGST+=taxArray[index][1];
				tSGST+=taxArray[index][2];
				tCESS+=taxArray[index][3];
				tTaxableAmount+=parseFloat(taxArray[index][4]);
				tTotalTax+=parseFloat(taxArray[index][5]);
				tTotalItc+=parseFloat(taxArray[index][6]);
				tTotalAmount+=parseFloat(taxArray[index][7]);
		  }
		  index++;
		});
		$('#idPCount').html(transCount);
		$('#idPIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tIGST).toFixed(2)));
		$('#idPCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCGST).toFixed(2)));
		$('#idPSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tSGST).toFixed(2)));
		$('#idPCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tCESS).toFixed(2)));
		$('#idPTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTaxableAmount).toFixed(2)));
		$('#idPTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalTax).toFixed(2)));
		$('#idPITC').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalItc).toFixed(2)));
		$('#idPTotAmt').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tTotalAmount).toFixed(2)));
	}
}
$('#divPFilters').on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		$('#pmultiselect2').multiselect('deselect', [val]);
		$('#pmultiselect3').multiselect('deselect', [val]);
		$('#pmultiselect4').multiselect('deselect', [val]);
		$('#pmultiselect5').multiselect('deselect', [val]);
		$('#pmultiselect6').multiselect('deselect', [val]);
		applyPFilters();
	});
 $(document).ready(function() {
	 $('#idPTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${prtotalTaxableValue}).toFixed(2)));
	$('#idPIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${prtotalIGST}).toFixed(2)));
	$('#idPCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${prtotalCGST}).toFixed(2)));
	$('#idPSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${prtotalSGST}).toFixed(2)));
	$('#idPCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${prtotalCESS}).toFixed(2)));
	$('#idPTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${prtotalTax}).toFixed(2)));
	$('#idPITC').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${prtotalITC}).toFixed(2)));
	$('#idPTotAmt').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${prtotalValue}).toFixed(2)));
 });
</script>