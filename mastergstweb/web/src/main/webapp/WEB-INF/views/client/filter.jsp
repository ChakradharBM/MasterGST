<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
	<div class="normaltable meterialform">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="sfilter">
					<span id="divFilters"></span>
					<span class="btn-remove-tag" onclick="clearFilters()">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
			<div class="noramltable-row-desc">
				<select id="multiselect1" class="multiselect-ui form-control" multiple="multiple">
					<option value="SUCCESS">Uploaded</option>
					<option value="CANCELLED">Cancelled</option>
					<option value="Pending">Pending</option>
					<option value="Failed">Failed</option>
				</select>
				<select id="multiselect2" class="multiselect-ui form-control" multiple="multiple">
					<option value="<%=MasterGSTConstants.ADVANCES%>">Advance Tax</option>
					<option value="<%=MasterGSTConstants.ATPAID%>">Advance Adjusted</option>
					<option value="B2B">B2B Invoices</option>
					<c:if test="${returntype eq 'GSTR2' || returntype eq varPurchase}">
					<option value="<%=MasterGSTConstants.B2BUR%>"><%=MasterGSTConstants.B2BUR%></option>
					</c:if>
					<option value="B2C">B2CS (Small) Invoices</option>
					<option value="B2CL">B2CL (Large)</option>
					<option value="Debit Note">Debit Note</option>
					<option value="Credit Note">Credit Note</option>
					<option value="Debit Note">Debit Note(UR)</option>
					<option value="Credit Note">Credit Note(UR)</option>
					<option value="<%=MasterGSTConstants.EXPORTS%>"><%=MasterGSTConstants.EXPORTS%></option>
					<c:if test="${returntype eq 'GSTR2' || returntype eq varPurchase}">
					<option value="<%=MasterGSTConstants.IMP_GOODS%>"><%=MasterGSTConstants.IMP_GOODS%></option>
					<option value="<%=MasterGSTConstants.IMP_SERVICES%>"><%=MasterGSTConstants.IMP_SERVICES%></option>
					<option value="<%=MasterGSTConstants.ITC_REVERSAL%>"><%=MasterGSTConstants.ITC_REVERSAL%></option>
					</c:if>
					<option value="<%=MasterGSTConstants.NIL%>">Nil Rated / Exempted / Non-GST</option>
				</select>
				<span class="multiselectuserlist">
					<select id="multiselect3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				<select id="multiselect4" class="multiselect-ui form-control" multiple="multiple">
				<c:if test="${not empty vendorList}">
				<c:forEach items="${vendorList}" var="vendor">
					<option value="${vendor}">${vendor}</option>
				</c:forEach>
				</c:if>
				</select>
				<select id="multiselect5" class="multiselect-ui form-control" multiple="multiple">
				</select>
				<select id="multiselect6" class="multiselect-ui form-control" multiple="multiple">
				</select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idCount"> ${fn:length(invoices)}</div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idTaxableVal"></div>
				</div>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idTaxVal"></div>
				</div>
				<div class="normaltable-col hdr">Total Amount 
					<div class="normaltable-col-txt" id="idTotAmtVal"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total IGST
					<div class="normaltable-col-txt" id="idIGST"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CGST
					<div class="normaltable-col-txt" id="idCGST"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total SGST
					<div class="normaltable-col-txt" id="idSGST"></div>
				</div>
				<div class="normaltable-col hdr filsummary">Total CESS
					<div class="normaltable-col-txt" id="idCESS"></div>
				</div>
				<c:if test="${returntype eq 'GSTR2' || returntype eq varPurchase}">
				<div class="normaltable-col hdr">ITC Available
					<div class="normaltable-col-txt" id="idITC"></div>
				</div>
				</c:if>
				<div class="normaltable-col hdr filsummary">
					<c:if test="${varRetType eq 'GSTR1' || varRetType eq 'GSTR5' || varRetType eq 'GSTR4'}">
						Total TCS
					</c:if>
					<c:if test="${varRetType eq 'GSTR2' || varRetType eq 'GSTR2A' || varRetType eq varPurchase || varRetType eq 'Unclaimed' || varRetType eq varGSTR6}">
						Total TDS
					</c:if>
					<div class="normaltable-col-txt" id="idTCSTDS${varRetTypeCode}"></div>
				</div>
			</div>
		</div>
	</div>
<script type="text/javascript">
$(function() {
	$.ajax({
		url: "${contextPath}/cp_users/${id}/${client.id}",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if (response.length > 0) {
				$("#multiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				response.forEach(function(cp_user) {
					$("#multiselect3").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
				});
			}else{
				$("#multiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
			}
			$("#multiselect3").multiselect({
					nonSelectedText: '- User -',
					includeSelectAllOption: true,
					onChange: function(element, checked) {
						applyFilters();
					},
					onSelectAll: function() {
						applyFilters();
					},
					onDeselectAll: function() {
						applyFilters();
					}
				});
			$("#multiselect3").multiselect('refresh');
		}
	});
	<c:forEach items="${client.branches}" var="branch">
		$("#multiselect5").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
	</c:forEach>
	<c:forEach items="${client.verticals}" var="vertical">
		$("#multiselect6").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
	</c:forEach>
	$('#multiselect1').multiselect({
		nonSelectedText: '- Invoice Status -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyFilters();
		},
		onSelectAll: function() {
			applyFilters();
		},
		onDeselectAll: function() {
			applyFilters();
		}
    });
	$('#multiselect2').multiselect({
		nonSelectedText: '- Invoice Type -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyFilters();
		},
		onSelectAll: function() {
			applyFilters();
		},
		onDeselectAll: function() {
			applyFilters();
		}
    });
	$('#multiselect5').multiselect({
		nonSelectedText: '- Branches -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyFilters();
		},
		onSelectAll: function() {
			applyFilters();
		},
		onDeselectAll: function() {
			applyFilters();
		}
    });
	$('#multiselect6').multiselect({
		nonSelectedText: '- Verticals -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyFilters();
		},
		onSelectAll: function() {
			applyFilters();
		},
		onDeselectAll: function() {
			applyFilters();
		}
    });
	if('${returntype}' == 'GSTR1'){
	$('#multiselect4').multiselect({
		nonSelectedText: '- Customers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyFilters();
		},
		onSelectAll: function() {
			applyFilters();
		},
		onDeselectAll: function() {
			applyFilters();
		}
    });
	}else{
		$('#multiselect4').multiselect({
		nonSelectedText: '- Suppliers -',
		includeSelectAllOption: true,
		onChange: function(element, checked) {
			applyFilters();
		},
		onSelectAll: function() {
			applyFilters();
		},
		onDeselectAll: function() {
			applyFilters();
		}
    });	
	}
});
function applyFilters() {
	invoiceTable.clear();
	selInvArray=new Array();
	var statusOptions = $('#multiselect1 option:selected');
	var typeOptions = $('#multiselect2 option:selected');
	var userOptions = $('#multiselect3 option:selected');
	var vendorOptions = $('#multiselect4 option:selected');
	var branchOptions = $('#multiselect5 option:selected');
	var verticalOptions = $('#multiselect6 option:selected');
	if(statusOptions.length > 0 || typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions.length > 0 || verticalOptions.length > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	var statusArr=new Array();
	var typeArr=new Array();
	var userArr=new Array();
	var vendorArr=new Array();
	var branchArr=new Array();
	var verticalArr=new Array();
	var filterContent='';
	if(statusOptions.length > 0) {
		for(var i=0;i<statusOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+statusOptions[i].text+'<span data-val="'+statusOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			statusArr.push(statusOptions[i].value);
		}
	} else {
		statusArr.push('All');
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
	$('#divFilters').html(filterContent);
	commonInvoiceFilter(statusArr, typeArr, userArr,vendorArr,branchArr,verticalArr);
	invoiceTable.draw();
	OSREC.CurrencyFormatter.formatAll({
		selector: '.ind_formats'
	});
}
$('#divFilters').on('click', '.deltag', function(e) {
	var val = $(this).data('val');
	$('#multiselect1').multiselect('deselect', [val]);
	$('#multiselect2').multiselect('deselect', [val]);
	$('#multiselect3').multiselect('deselect', [val]);
	$('#multiselect4').multiselect('deselect', [val]);
	$('#multiselect5').multiselect('deselect', [val]);
	$('#multiselect6').multiselect('deselect', [val]);
	applyFilters();
});
$(document).ready(function() {
	 $('#idTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalTaxableValue}).toFixed(2)));
	$('#idIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalIGST}).toFixed(2)));
	$('#idCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalCGST}).toFixed(2)));
	$('#idSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalSGST}).toFixed(2)));
	$('#idCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalCESS}).toFixed(2)));
	$('#idTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalTax}).toFixed(2)));
	$('#idITC').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalITC}).toFixed(2)));
	$('#idTotAmtVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(${totalValue}).toFixed(2)));
 });
</script>