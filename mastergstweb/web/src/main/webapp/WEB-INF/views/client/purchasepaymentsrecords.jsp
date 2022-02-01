<div class="db-ca-wrap">
		<div class="container">
				<div class=" "></div>
			<div class="tab-pane" id="gtab2" role="tabpanel">
					<div class="normaltable meterialform" id="monthlynormaltable">
		<div class="filter">
			<div class="noramltable-row"><div class="noramltable-row-hdr">Filter</div><div class="noramltable-row-desc"><div class="sfilter"><span id="divFiltersGSTR2"></span>
					<span class="btn-remove-tag" onClick="clearFilters('GSTR2')">Clear All<span data-role="remove"></span></span>
				</div></div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search Filter</div><div class="noramltable-row-desc">
				<select id="multiselectGSTR21" class="multiselect-ui form-control" multiple="multiple"><option value="2017-2018">2017-2018</option><option value="2018-2019">2018-2019</option><option value="2019-2020">2019-2020</option></select>
				<select id="multiselectGSTR22" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectGSTR23" class="multiselect-ui form-control" multiple="multiple"></select>
				<select id="multiselectGSTR24" class="multiselect-ui form-control" multiple="multiple"><option value="cash">Cash</option><option value="bank">Bank</option><option value="tds">TDS</option><option value="writeoff">Write-Off</option></select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Payments<div class="normaltable-col-txt" id="idCountGSTR2"></div></div>
				<div class="normaltable-col hdr">Total Amount Received<div class="normaltable-col-txt" id="idTotalValGSTR2"></div></div>
				
			</div>
		</div>
	</div>
			</div>
		
		<div class=" "><h4 class="f-18-b pull-left"><h4>Purchase Payments of <c:choose><c:when test='${fn:length(client.businessname) > 25}'>${fn:substring(client.businessname, 0, 25)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></h4></h4></div>
		<div class="customtable db-ca-view ">
				<table id='paymentstable1' class="row-border dataTable meterialform"
					cellspacing="0" width="100%">
					<thead>
						<tr><th class="text-center" width=5%>S.no</th><th class="text-center" width=12%>Date</th><th class="text-center">Payment Id</th><th class="text-center">Invoice Number</th><th class="text-center">Customer</th><th class="text-center">GSTIN</th><th class="text-center">Amount Received</th><th class="text-center">Payment Mode</th><th class="text-center">Reference No</th><th class="text-center" style="display:none;">Financial Year</th></tr>
					</thead>
					<tbody id='paymentsalestable'></tbody>
				</table>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	
			var paymentsArray=new Object();
			var hiddenCols = new Array();
			$('input.btaginput').tagsinput({tagClass : 'big',});
			$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
				$('.btaginput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
			});
			hiddenCols.push(9);
			$.ajax({
				url: "${contextPath}/allrecordpayments/${id}/${fullname}/${usertype}/${clientid}/GSTR2/${month}/${year}",
				async: true,
				cache: false,
				success : function(response){
					if(response.length > 0){
				var i=1;var custnames = [];var gstins = [];var counts=0;var totalamt = 0; var transcounts=0;
				$('#paymentstable1 tbody').html('');
					$.each(response, function(index, itemData) {
						$('#paymentstable1 tbody').append('<tr><td class="text-center">'+i+'</td><td class="text-center">'+itemData.paymentDate+'</td><td class="text-center">'+itemData.voucherNumber+'</td><td class="text-center" class="text-center">'+itemData.invoiceNumber+'</td><td class="text-center">'+itemData.customerName+'</td><td class="text-center">'+itemData.gstNumber+'</td><td class="text-center">'+itemData.amount+'</td><td class="text-center">'+itemData.modeOfPayment+'</td><td class="text-center">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td></tr>');
					    
						if(itemData.customerName) {
							if(counts == 0){
								custnames.push(itemData.customerName);
								$("#multiselectGSTR22").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));
							}
							if(jQuery.inArray(itemData.customerName, custnames) == -1){
								custnames.push(itemData.customerName);
								$("#multiselectGSTR22").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));
							}
						}
						if(itemData.gstNumber) {
							if(counts == 0){
								gstins.push(itemData.gstNumber);
								$("#multiselectGSTR23").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));
							}
							if(jQuery.inArray(itemData.gstNumber, gstins) == -1){
								gstins.push(itemData.gstNumber);
								$("#multiselectGSTR23").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));
							}
						}
						
						totalamt += parseFloat(itemData.amount);
						counts++;
						i++;
						transcounts++;
					});
					$('#idCountGSTR2').html(transcounts);
					$('#idTotalValGSTR2').html(formatNumber(parseFloat(totalamt).toFixed(2)));
					$("#multiselectGSTR22").multiselect('rebuild');
					$("#multiselectGSTR23").multiselect('rebuild');
					paymentstable1 = $("#paymentstable1").DataTable({
						"dom": '<"toolbar"f>lrtip<"clear">', 		
						"paging": true,
						"searching": true,
						"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
						"responsive": true,
						"ordering": true,
						"columnDefs": [
							{
								"targets": hiddenCols,
								"visible": false,
								"searchable": true
							}
						] ,
						"language": {
							"search": "_INPUT_",
							"searchPlaceholder": "Search...",
							"paginate": {
							   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
								"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
						   }
						 }
					});
					paymentsArray['GSTR2'] = response;
			}else{if ( $.fn.DataTable.isDataTable('#paymentstable1') ) {$('#paymentstable1').DataTable().destroy();}
			$('#paymentstable1 tbody').empty();
			paymentstable1 = $("#paymentstable1").DataTable({
				"dom": '<"toolbar"f>lrtip<"clear">', 		
				"paging": true,
				"searching": true,
				"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
				"responsive": true,
				"ordering": true,
				"language": {
					"search": "_INPUT_",
					"searchPlaceholder": "Search...",
					"paginate": {
					   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
						"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				   }
				 }
			});
			$("#multiselectGSTR22").multiselect('rebuild');
			$("#multiselectGSTR23").multiselect('rebuild');
		}
				},error:function(err){
				}
			});
			
			$('#multiselectGSTR21').multiselect({
				nonSelectedText: '- Financial Year-',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters('${returntype}');
				},
				onSelectAll: function() {
					applyFilters('${returntype}');
				},
				onDeselectAll: function() {
					applyFilters('${returntype}');
				}
			});
			$('#multiselectGSTR22').multiselect({
				nonSelectedText: '- Customer -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters('${returntype}');
				},
				onSelectAll: function() {
					applyFilters('${returntype}');
				},
				onDeselectAll: function() {
					applyFilters('${returntype}');
				}
			});
			$('#multiselectGSTR23').multiselect({
				nonSelectedText: '- GSTIN -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters('${returntype}');
				},
				onSelectAll: function() {
					applyFilters('${returntype}');
				},
				onDeselectAll: function() {
					applyFilters('${returntype}');
				}
			});
			
			$('#multiselectGSTR24').multiselect({
				nonSelectedText: '- Payment Mode -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters('${returntype}');
				},
				onSelectAll: function() {
					applyFilters('${returntype}');
				},
				onDeselectAll: function() {
					applyFilters('${returntype}');
				}
			});
			$('#divFiltersGSTR2').on('click', '.deltag', function(e) {
				var val = $(this).data('val');
				$('#multiselectGSTR21').multiselect('deselect', [val]);
				$('#multiselectGSTR22').multiselect('deselect', [val]);
				$('#multiselectGSTR23').multiselect('deselect', [val]);
				$('#multiselectGSTR24').multiselect('deselect', [val]);
				applyFilters('${varRetType}');
			});
			function applyFilters(retType) {
				paymentstable1.clear();
				var financialOptions = new Array();
				var paymentOptions = new Array();
				financialOptions = $('#multiselectGSTR21 option:selected');
				paymentOptions = $('#multiselectGSTR24 option:selected');
				var customerOptions = $('#multiselectGSTR22 option:selected');
				var gstinOptions = $('#multiselectGSTR23 option:selected');
			
				if(financialOptions.length > 0 || paymentOptions.length > 0 || customerOptions.length > 0 || gstinOptions.length > 0){
					$('.normaltable .filter').css("display","block");
				}else{
					$('.normaltable .filter').css("display","none");
				}
				var financialArr=new Array();
				var customerArr=new Array();
				var gstinArr=new Array();
				var paymentArr=new Array();
				
				var filterContent='';
				if(financialOptions.length > 0) {
					for(var i=0;i<financialOptions.length;i++) {
						filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+financialOptions[i].text+'<span data-val="'+financialOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
						financialArr.push(financialOptions[i].value);
					}
				} else {
					financialArr.push('All');
				}
				if(customerOptions.length > 0) {
					for(var i=0;i<customerOptions.length;i++) {
						filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+customerOptions[i].text+'<span data-val="'+customerOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
						customerArr.push(customerOptions[i].value);
					}
				} else {
					customerArr.push('All');
				}
				if(gstinOptions.length > 0) {
					for(var i=0;i<gstinOptions.length;i++) {
						filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+gstinOptions[i].value+'<span data-val="'+gstinOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
						gstinArr.push(gstinOptions[i].value);
					}
				} else {
					gstinArr.push('All');
				}
				if(paymentOptions.length > 0) {
					for(var i=0;i<paymentOptions.length;i++) {
						filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+paymentOptions[i].value+'<span data-val="'+paymentOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
						paymentArr.push(paymentOptions[i].value);
					}
				} else {
					paymentArr.push('All');
				}
				
				$('#divFiltersGSTR2').html(filterContent);
				commonInvoiceFilter(retType, financialArr, customerArr, gstinArr,paymentArr);
				paymentstable1.draw();
			}
			
			function commonInvoiceFilter(retType, arrFinancial, arrCustomer, arrGstin, arrPayment) {
				if(paymentsArray['GSTR2'].length > 0) {
					var rows = new Array();
					var taxArray = new Array();
					var rowNode;
					var i=1;
					paymentsArray['GSTR2'].forEach(function(itemData) {
						var rowData = [i,itemData.paymentDate,itemData.voucherNumber,itemData.invoiceNumber,itemData.customerName,itemData.gstNumber,itemData.amount,itemData.modeOfPayment,itemData.referenceNumber,itemData.financialYear];
						rows.push(rowData);
						i++;
						taxArray.push([itemData.amount]);
					});
					var index = 0, tpayments=0, tamt=0;
					rows.forEach(function(row) {
					if((arrFinancial.length == 0 || $.inArray('All', arrFinancial) >= 0 || $.inArray(row[9], arrFinancial) >= 0)
							&& (arrCustomer.length == 0 || $.inArray('All', arrCustomer) >= 0 || $.inArray(row[4], arrCustomer) >= 0)
							&& (arrGstin.length == 0 || $.inArray('All', arrGstin) >= 0 || $.inArray(row[5], arrGstin) >= 0)
							&& (arrPayment.length == 0 || $.inArray('All', arrPayment) >= 0 || $.inArray(row[7], arrPayment) >= 0)) {
							row[6] = "<span class='ind_formats'>"+row[6]+"</span>";
							rowNode = paymentstable1.row.add(row);
							tpayments++;
							tamt+=parseFloat(taxArray[index][0]);
					  }
					  index++;
					});
					 $('#idCountGSTR2').html(tpayments);
					$('#idTotalValGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt).toFixed(2))); 
				}
			}	
		function clearFilters(retType) {
			$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
			$('#divFilters').html('');
			$('.normaltable .filter').css("display","none");
			paymentstable1.clear();
			commonInvoiceFilter(retType, new Array(), new Array(),new Array(),new Array());
			paymentstable1.draw();
		}
	</script>