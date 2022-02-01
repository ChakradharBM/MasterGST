
			<script type="text/javascript">
			var paymentsArray=new Object();
			var hiddenCols = new Array();
			$('input.btaginput').tagsinput({tagClass : 'big',});
			$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
				$('.btaginput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
			});
			hiddenCols.push(9);
			$.ajax({
				url: "${contextPath}/allrecordpayments/${id}/${fullname}/${usertype}/${clientid}/${returntype}/${month}/${year}",
				async: true,
				cache: false,
				success : function(response){
			if(response.length > 0){
				var i=1;var custnames = [];var gstins = [];var counts=0;var totalamt = 0; var transcounts=0;
				$('#paymentstable tbody').html('');
					$.each(response, function(index, itemData) {
						$('#paymentstable tbody').append('<tr><td class="text-center">'+i+'</td><td class="text-center">'+itemData.paymentDate+'</td><td class="text-center">'+itemData.voucherNumber+'</td><td class="text-center" class="text-center">'+itemData.invoiceNumber+'</td><td class="text-center">'+itemData.customerName+'</td><td class="text-center">'+itemData.gstNumber+'</td><td class="text-center">'+itemData.amount+'</td><td class="text-center">'+itemData.modeOfPayment+'</td><td class="text-center">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td></tr>');
					   
						if(itemData.customerName) {
							if(counts == 0){
								custnames.push(itemData.customerName);
								$("#multiselectGSTR12").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));
							}
							if(jQuery.inArray(itemData.customerName, custnames) == -1){
								custnames.push(itemData.customerName);
								$("#multiselectGSTR12").append($("<option></option>").attr("value",itemData.customerName).text(itemData.customerName));
							}
						}
						if(itemData.gstNumber) {
							if(counts == 0){
								gstins.push(itemData.gstNumber);
								$("#multiselectGSTR13").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));
							}
							if(jQuery.inArray(itemData.gstNumber, gstins) == -1){
								gstins.push(itemData.gstNumber);
								$("#multiselectGSTR13").append($("<option></option>").attr("value",itemData.gstNumber).text(itemData.gstNumber));
							}
						}
						totalamt += parseFloat(itemData.amount);
						counts++;
						i++;
						transcounts++;
					});
				
					$('#idCountGSTR1').html(transcounts);
					$('#idTotalValGSTR1').html(formatNumber(parseFloat(totalamt).toFixed(2)));
					$("#multiselectGSTR12").multiselect('rebuild');
					$("#multiselectGSTR13").multiselect('rebuild');
					paymentstable = $("#paymentstable").DataTable({
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
					paymentsArray['GSTR1'] = response;
			}else{if ( $.fn.DataTable.isDataTable('#paymentstable') ) {$('#paymentstable').DataTable().destroy();}
			$('#paymentstable tbody').empty();
				paymentstable = $("#paymentstable").DataTable({
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
				$("#multiselectGSTR12").multiselect('rebuild');
				$("#multiselectGSTR13").multiselect('rebuild');
			}
				},error:function(err){
				}
			});
			$('#multiselectGSTR11').multiselect({
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
			$('#multiselectGSTR12').multiselect({
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
			$('#multiselectGSTR13').multiselect({
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
			
			$('#multiselectGSTR14').multiselect({
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
			$('#divFiltersGSTR1').on('click', '.deltag', function(e) {
				var val = $(this).data('val');
				$('#multiselectGSTR11').multiselect('deselect', [val]);
				$('#multiselectGSTR12').multiselect('deselect', [val]);
				$('#multiselectGSTR13').multiselect('deselect', [val]);
				$('#multiselectGSTR14').multiselect('deselect', [val]);
				applyFilters('${varRetType}');
			});
			function applyFilters(retType) {
				paymentstable.clear();
				var financialOptions = new Array();
				var paymentOptions = new Array();
				financialOptions = $('#multiselectGSTR11 option:selected');
				paymentOptions = $('#multiselectGSTR14 option:selected');
				var customerOptions = $('#multiselectGSTR12 option:selected');
				var gstinOptions = $('#multiselectGSTR13 option:selected');
			
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
				
				$('#divFiltersGSTR1').html(filterContent);
				commonInvoiceFilter(retType, financialArr, customerArr, gstinArr,paymentArr);
				paymentstable.draw();
			}
			
			function commonInvoiceFilter(retType, arrFinancial, arrCustomer, arrGstin, arrPayment) {
				if(paymentsArray['GSTR1'].length > 0) {
					var rows = new Array();
					var taxArray = new Array();
					var rowNode;
					var i=1;
					paymentsArray['GSTR1'].forEach(function(itemData) {
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
							rowNode = paymentstable.row.add(row);
							tpayments++;
							tamt+=parseFloat(taxArray[index][0]);
					  }
					  index++;
					});
					 $('#idCountGSTR1').html(tpayments);
					$('#idTotalValGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt).toFixed(2))); 
				}
			}
		function clearFilters(retType) {
			$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
			$('#divFilters').html('');
			$('.normaltable .filter').css("display","none");
			paymentstable.clear();
			commonInvoiceFilter(retType, new Array(), new Array(),new Array(),new Array());
			paymentstable.draw();
		}
		function formatNumber(nStr) {
			var negativenumber = false;
			if(nStr && nStr.includes("-")){
				negativenumber = true;
				nStr = nStr.replace("-","");
			}
			nStr=nStr.toString();var afterPoint = '';
			if(nStr.indexOf('.') > 0)
			   afterPoint = nStr.substring(nStr.indexOf('.'),nStr.length);
			nStr = Math.floor(nStr);
			nStr=nStr.toString();
			var lastThree = nStr.substring(nStr.length-3);
			var otherNumbers = nStr.substring(0,nStr.length-3);
			if(otherNumbers != '')
			    lastThree = ',' + lastThree;
			var res = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",") + lastThree + afterPoint;
			if(negativenumber){
				res = "-"+res;
			}
			return res;
    	}
	</script>