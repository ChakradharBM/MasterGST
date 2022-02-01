	$(function () {
		if(type == 'receive'){
			$('#salesTab').click();
		}else if(type == 'made'){
			$('#purchasesTab').click();
		}
			paymentsArray=new Object();
			var hiddenCols = new Array();
			salesAmt = invoicesTotalAmount;
			purchaseAmt=purchseinvoicesTotalAmount;
			$('input.btaginput').tagsinput({tagClass : 'big',});
			$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
				$('.btaginput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
			});
			hiddenCols.push(9);
			hiddenCols.push(10);
			$.ajax({
				url: contextPath+"/allrecordpayments"+paymenturlSuffix+"/GSTR1/"+Paymenturlprefix,
				async: true,
				cache: false,
				success : function(response){
			if(response.length > 0){
				var i=1;var custnames = [];var gstins = [];var counts=0;var totalamt = 0; var transcounts=0;var pendamt=0;var totFullAmt=0;
				$('#paymentstable tbody').html('');
					$.each(response, function(index, itemData) {
						var date = itemData.paymentDate;
						var dt = date.split("-");
						itemData.financialYear = dt[2];
						itemData.month = dt[1];
						var rettype = 'GSTR1';
						var jsptype = 'payments';
						if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
							var payments = new Object();
							payments.id = itemData.userid;
							payments.invtype = itemData.invtype;
							payments.returntype = itemData.returntype;
							payments.invoiceid = itemData.invoiceid;
							payments.gstNumber = itemData.gstNumber;
							payments.customerName = itemData.customerName;
							if(itemData.isadvadjust == '' || itemData.isadvadjust == null){
								payments.isadvadjust =false;
							}else{
								payments.isadvadjust = itemData.isadvadjust;
							}
							
							payments.voucherNumber = itemData.voucherNumber;
							payments.invoiceNumber = itemData.invoiceNumber;
							payments.paymentDate = itemData.paymentDate;
							if(itemData.pendingBalance == '' || itemData.pendingBalance == null){
								payments.pendingBalance = 0.00;
							}else{
							payments.pendingBalance = itemData.pendingBalance;
							}
							if(itemData.receivedAmount == '' || itemData.receivedAmount == null){
								payments.receivedAmount = 0.00;
							}else{
							payments.receivedAmount = itemData.receivedAmount;
							}
							if(itemData.previousPendingBalance == '' || itemData.previousPendingBalance == null){
								payments.previousPendingBalance = 0.00;
							}else{
								payments.previousPendingBalance = itemData.previousPendingBalance;
							}
							payments.bankname = itemData.bankname;
							payments.accountnumber = itemData.accountnumber;
							payments.accountName = itemData.accountName;
							payments.branchname =itemData.branchname ;
							payments.ifsccode = itemData.ifsccode;
							payments.advpmntrecno = itemData.advpmntrecno;
							payments.advpmntrecdate = itemData.advpmntrecdate;
							payments.advpmntpos = itemData.advpmntpos;
							if(itemData.advpmntrecamt == '' || itemData.advpmntrecamt == null){
								payments.advpmntrecamt =0.00;
							}else{
							payments.advpmntrecamt = itemData.advpmntrecamt;
							}
							if(itemData.advpmntavailadjust == '' || itemData.advpmntavailadjust == null){
								payments.advpmntavailadjust = "";
							}else{
							payments.advpmntavailadjust = itemData.advpmntavailadjust;
							}
							if(itemData.advpmntadjust == '' || itemData.advpmntadjust == null){
								payments.advpmntadjust = "";
							}else{
								payments.advpmntadjust = itemData.advpmntadjust;
							}
							
							payments.advpmnttaxrate = itemData.advpmnttaxrate;
							if(itemData.advpmntigstamt == '' || itemData.advpmntigstamt == null){
								payments.advpmntigstamt = "";
							}else{
							payments.advpmntigstamt = itemData.advpmntigstamt;
							}
							if(itemData.advpmntcgstamt == '' || itemData.advpmntcgstamt == null){
								payments.advpmntcgstamt ="";
							}else{
							payments.advpmntcgstamt = itemData.advpmntcgstamt;
							}
							if(itemData.advpmntsgstamt == '' || itemData.advpmntsgstamt == null){
								payments.advpmntsgstamt = "";
							}else{
							payments.advpmntsgstamt = itemData.advpmntsgstamt;
							}
							if(itemData.advpmntcessrate == '' || itemData.advpmntcessrate == null){
								payments.advpmntcessrate = "";
							}else{
								payments.advpmntcessrate = itemData.advpmntcessrate;
							}
							if(itemData.advpmntcessamt == '' || itemData.advpmntcessamt == null){
								payments.advpmntcessamt = "";
							}else{
								payments.advpmntcessamt = itemData.advpmntcessamt;
							}
							
							if(itemData.advpmntremainamt == '' || itemData.advpmntremainamt == null){
								payments.advpmntremainamt ="";
							}else{
								payments.advpmntremainamt = itemData.advpmntremainamt;
							}
							payments.paymentitems= itemData.paymentitems;
							paymentslist.push(payments);
							$.each(itemData.paymentitems, function(index, payemntItemData) {
								$('#paymentstable tbody').append('<tr class="row'+itemData.userid+'"><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right indformat">'+payemntItemData.amount+'</td><td class="text-left">'+payemntItemData.modeOfPayment+'</td><td class="text-left">'+payemntItemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td><td class="actionicons"><a href="#" style="margin-right:3px;" onClick="updatePaymentDetails(\''+itemData.userid+'\',\''+rettype+'\',\''+jsptype+'\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+itemData.userid+'\',\''+rettype+'\',\''+itemData.clientid+'\',\''+itemData.voucherNumber+'\',\''+itemData.invoiceNumber+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>');
								totalamt += parseFloat(payemntItemData.amount);
								totFullAmt += parseFloat(payemntItemData.previousPendingBalance);
								pendamt += parseFloat(payemntItemData.pendingBalance);
								counts++;
								i++;
								transcounts++;
							});
						}else{
							var payments = new Object();
							payments.id = itemData.userid;
							payments.returntype = itemData.returntype;
							payments.invoiceid = itemData.invoiceid;
							payments.gstNumber = itemData.gstNumber;
							payments.customerName = itemData.customerName;
							payments.voucherNumber = itemData.voucherNumber;
							payments.invoiceNumber = itemData.invoiceNumber;
							payments.paymentDate = itemData.paymentDate;
							if(itemData.pendingBalance == '' || itemData.pendingBalance == null){
								payments.pendingBalance = 0.00;
							}else{
							payments.pendingBalance = itemData.pendingBalance;
							}
							if(itemData.receivedAmount == '' || itemData.receivedAmount == null){
								payments.receivedAmount = 0.00;
							}else{
							payments.receivedAmount = itemData.receivedAmount;
							}
							if(itemData.previousPendingBalance == '' || itemData.previousPendingBalance == null){
								payments.previousPendingBalance = 0.00;
							}else{
								payments.previousPendingBalance = itemData.previousPendingBalance;
							}
							payments.bankname = itemData.bankname;
							payments.accountnumber = itemData.accountnumber;
							payments.accountName = itemData.accountName;
							payments.branchname =itemData.branchname ;
							payments.ifsccode = itemData.ifsccode;
							payments.advpmntrecno = itemData.advpmntrecno;
							payments.advpmntrecdate = itemData.advpmntrecdate;
							payments.advpmntpos = itemData.advpmntpos;
							if(itemData.advpmntrecamt == '' || itemData.advpmntrecamt == null){
								payments.advpmntrecamt =0.00;
							}else{
							payments.advpmntrecamt = itemData.advpmntrecamt;
							}
							if(itemData.advpmntavailadjust == '' || itemData.advpmntavailadjust == null){
								payments.advpmntavailadjust = 0.00;
							}else{
							payments.advpmntavailadjust = itemData.advpmntavailadjust;
							}
							if(itemData.advpmntadjust == '' || itemData.advpmntadjust == null){
								payments.advpmntadjust = 0.00;
							}else{
								payments.advpmntadjust = itemData.advpmntadjust;
							}
							
							payments.advpmnttaxrate = itemData.advpmnttaxrate;
							if(itemData.advpmntigstamt == '' || itemData.advpmntigstamt == null){
								payments.advpmntigstamt = 0.00;
							}else{
							payments.advpmntigstamt = itemData.advpmntigstamt;
							}
							if(itemData.advpmntcgstamt == '' || itemData.advpmntcgstamt == null){
								payments.advpmntcgstamt =0.00;
							}else{
							payments.advpmntcgstamt = itemData.advpmntcgstamt;
							}
							if(itemData.advpmntcgstamt == '' || itemData.advpmntcgstamt == null){
								payments.advpmntsgstamt = 0.00;
							}else{
							payments.advpmntsgstamt = itemData.advpmntsgstamt;
							}
							if(itemData.advpmntcessrate == '' || itemData.advpmntcessrate == null){
								payments.advpmntcessrate = 0.0;
							}else{
								payments.advpmntcessrate = itemData.advpmntcessrate;
							}
							if(itemData.advpmntcessrate == '' || itemData.advpmntcessrate == null){
								payments.advpmntcessamt = 0.00;
							}else{
								payments.advpmntcessamt = itemData.advpmntcessamt;
							}
							
							if(itemData.advpmntremainamt == '' || itemData.advpmntremainamt == null){
								payments.advpmntremainamt =0.00;
							}else{
							payments.advpmntremainamt = itemData.advpmntremainamt;
							}
							paymentslist.push(payments);
							$('#paymentstable tbody').append('<tr class="row'+itemData.userid+'"><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right indformat">'+itemData.amount+'</td><td class="text-left">'+itemData.modeOfPayment+'</td><td class="text-left">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td><td class="actionicons"><a href="#" data-toggle="modal" data-target="#paymentsModal" style="margin-right: 3px;" onClick="updatePaymentDetails(\''+itemData.userid+'\',\''+rettype+'\',\''+jsptype+'\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+itemData.userid+'\',\''+rettype+'\',\''+itemData.clientid+'\',\''+itemData.voucherNumber+'\',\''+itemData.invoiceNumber+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>');
							totalamt += parseFloat(itemData.amount);
							totFullAmt += parseFloat(itemData.previousPendingBalance);
							pendamt += parseFloat(itemData.pendingBalance);
							counts++;
							i++;
							transcounts++;
						}
					   
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
					});
					totFullAmt = totalamt + pendamt;
					$('#idCountGSTR1').html(transcounts);
					$('#idTotalValGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalamt).toFixed(2)));
					//$('#idTotalPendingGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pendamt).toFixed(2)));
					$('#idTotalPendingGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(salesAmt-totalamt).toFixed(2)));
					$('#idTotalAmtGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(salesAmt).toFixed(2)));
					$("#multiselectGSTR12").multiselect('rebuild');
					$("#multiselectGSTR13").multiselect('rebuild');
					paymentstable = $("#paymentstable").DataTable({
						"dom": '<"toolbar"f>lrtip<"clear">', 		
						"paging": true,
						"searching": true,
						"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
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
							   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
								"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
						   }
						 },
				drawCallback: function(){
				          $('.paginate_button', this.api().table().container())          
				             .on('click', function(){
				            	  $(".indformat").each(function(){
				       			    $(this).html($(this).html().replace(/,/g , ''));
				       			});
				       		  OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
				             });       
				       }
					});
					$(".indformat").each(function(){
	       			    $(this).html($(this).html().replace(/,/g , ''));
	       			});
					OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});	
          paymentsArray['GSTR1'] = response;
			}else{if ( $.fn.DataTable.isDataTable('#paymentstable') ) {$('#paymentstable').DataTable().destroy();}
			$('#paymentstable tbody').empty();
				paymentstable = $("#paymentstable").DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">', 		
					"paging": true,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
					"responsive": true,
					"ordering": true,
					"language": {
						"search": "_INPUT_",
						"searchPlaceholder": "Search...",
						"paginate": {
						   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
							"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
					   }
					 }
				});
				$("#multiselectGSTR12").multiselect('rebuild');
				$("#multiselectGSTR13").multiselect('rebuild');
				$('#idCountGSTR1').html(0);
				$('#idTotalValGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
				//$('#idTotalPendingGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
				$('#idTotalPendingGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
				$('#idTotalAmtGSTR1').html(formatNumber(parseFloat(0).toFixed(2)));
			}
				},error:function(err){
				}
			});
			$('#multiselectGSTR11').multiselect({
				nonSelectedText: '- Financial Year-',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters1('GSTR1');
				},
				onSelectAll: function() {
					applyFilters1('GSTR1');
				},
				onDeselectAll: function() {
					applyFilters1('GSTR1');
				}
			});
			$('#multiselectGSTR12').multiselect({
				nonSelectedText: '- Customer -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters1('GSTR1');
				},
				onSelectAll: function() {
					applyFilters1('GSTR1');
				},
				onDeselectAll: function() {
					applyFilters1('GSTR1');
				}
			});
			$('#multiselectGSTR13').multiselect({
				nonSelectedText: '- GSTIN -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters1('GSTR1');
				},
				onSelectAll: function() {
					applyFilters1('GSTR1');
				},
				onDeselectAll: function() {
					applyFilters1('GSTR1');
				}
			});
			
			$('#multiselectGSTR14').multiselect({
				nonSelectedText: '- Payment Mode -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters1('GSTR1');
				},
				onSelectAll: function() {
					applyFilters1('GSTR1');
				},
				onDeselectAll: function() {
					applyFilters1('GSTR1');
				}
			});
			$('#multiselectGSTR15').multiselect({
				nonSelectedText: '- Month -',
				includeSelectAllOption: true,
				onChange: function(element, checked) {
					applyFilters1('GSTR1');
				},
				onSelectAll: function() {
					applyFilters1('GSTR1');
				},
				onDeselectAll: function() {
					applyFilters1('GSTR1');
				}
			});
			$('#divFiltersGSTR1').on('click', '.deltag', function(e) {
				var val = $(this).data('val');
				$('#multiselectGSTR11').multiselect('deselect', [val]);
				$('#multiselectGSTR12').multiselect('deselect', [val]);
				$('#multiselectGSTR13').multiselect('deselect', [val]);
				$('#multiselectGSTR14').multiselect('deselect', [val]);
				$('#multiselectGSTR15').multiselect('deselect', [val]);
				applyFilters1('GSTR1');
			});
			function applyFilters1(retType) {
				paymentstable.clear();
				var financialOptions = new Array();
				var paymentOptions = new Array();
				var monthOptions = new Array();
				financialOptions = $('#multiselectGSTR11 option:selected');
				paymentOptions = $('#multiselectGSTR14 option:selected');
				var customerOptions = $('#multiselectGSTR12 option:selected');
				var gstinOptions = $('#multiselectGSTR13 option:selected');
				monthOptions = $('#multiselectGSTR15 option:selected');
			
				if(financialOptions.length > 0 || paymentOptions.length > 0 || customerOptions.length > 0 || gstinOptions.length > 0 || monthOptions.length > 0){
					$('.normaltable .filter').css("display","block");
				}else{
					$('.normaltable .filter').css("display","none");
				}
				var financialArr=new Array();
				var monthArr=new Array();
				var customerArr=new Array();
				var gstinArr=new Array();
				var paymentArr=new Array();
				var financialYearOptions=new Array();var mothlyTotalArr = new Array();
				var filterContent='';
				
				if(financialOptions.length > 0) {
					var fpVal = $('#multiselectGSTR11 option:selected').val();
					for(var i=0;i<financialOptions.length;i++) {
						filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+financialOptions[i].text+'<span data-val="'+financialOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
						financialArr.push(financialOptions[i].value);
						financialYearOptions.push(financialOptions[i].value);
						allInvoicesTotal(mothlyTotalArr,financialYearOptions);
					}
				} else {
					financialArr.push('All');
					financialYearOptions=new Array();
					allInvoicesTotal(mothlyTotalArr,financialYearOptions);
				}
					if(monthOptions.length > 0) {
						for(var i=0;i<monthOptions.length;i++) {
							filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+monthOptions[i].text+'<span data-val="'+monthOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
							monthArr.push(monthOptions[i].value);
							mothlyTotalArr.push(monthOptions[i].value);
							allInvoicesTotal(mothlyTotalArr,financialYearOptions);
						}
					} else {
						monthArr.push('All');
						mothlyTotalArr = new Array();
						allInvoicesTotal(mothlyTotalArr,financialYearOptions);
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
				commonInvoiceFilter1(retType, financialArr, monthArr,customerArr, gstinArr,paymentArr);
				paymentstable.draw();
				$(".indformat").each(function(){
				    $(this).html($(this).html().replace(/,/g , ''));
				});
				OSREC.CurrencyFormatter.formatAll({
					selector: '.indformat'
				});
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

		$.ajax({
			url: contextPath+"/allrecordpayments"+paymenturlSuffix+"/GSTR2/"+Paymenturlprefix,
			async: true,
			cache: false,
			success : function(response){
				if(response.length > 0){
			var i=1;var custnames = [];var gstins = [];var counts=0;var totalamt = 0; var transcounts=0;var pendamt=0;var totFullAmt=0;
			$('#paymentstable1 tbody').html('');
				$.each(response, function(index, itemData) {
					var date = itemData.paymentDate;
					var dt = date.split("-");
					itemData.financialYear = dt[2];
					itemData.month = dt[1];
					var rettype = 'GSTR2';
					var jsptype = 'payments';
					if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
						$.each(itemData.paymentitems, function(index, payemntItemData) {
							var payments = new Object();
							payments.id = itemData.userid;
							payments.invtype = itemData.invtype;
							payments.returntype = itemData.returntype;
							payments.invoiceid = itemData.invoiceid;
							payments.gstNumber = itemData.gstNumber;
							payments.customerName = itemData.customerName;
							if(itemData.isadvadjust == '' || itemData.isadvadjust == null){
								payments.isadvadjust =false;
							}else{
								payments.isadvadjust = itemData.isadvadjust;
							}
							
							payments.voucherNumber = itemData.voucherNumber;
							payments.invoiceNumber = itemData.invoiceNumber;
							payments.paymentDate = itemData.paymentDate;
							if(itemData.pendingBalance == '' || itemData.pendingBalance == null){
								payments.pendingBalance = 0.00;
							}else{
							payments.pendingBalance = itemData.pendingBalance;
							}
							if(itemData.receivedAmount == '' || itemData.receivedAmount == null){
								payments.receivedAmount = 0.00;
							}else{
							payments.receivedAmount = itemData.receivedAmount;
							}
							if(itemData.previousPendingBalance == '' || itemData.previousPendingBalance == null){
								payments.previousPendingBalance = 0.00;
							}else{
								payments.previousPendingBalance = itemData.previousPendingBalance;
							}
							payments.bankname = itemData.bankname;
							payments.accountnumber = itemData.accountnumber;
							payments.accountName = itemData.accountName;
							payments.branchname =itemData.branchname ;
							payments.ifsccode = itemData.ifsccode;
							payments.advpmntrecno = itemData.advpmntrecno;
							payments.advpmntrecdate = itemData.advpmntrecdate;
							payments.advpmntpos = itemData.advpmntpos;
							if(itemData.advpmntrecamt == '' || itemData.advpmntrecamt == null){
								payments.advpmntrecamt =0.00;
							}else{
							payments.advpmntrecamt = itemData.advpmntrecamt;
							}
							if(itemData.advpmntavailadjust == '' || itemData.advpmntavailadjust == null){
								payments.advpmntavailadjust = 0.00;
							}else{
							payments.advpmntavailadjust = itemData.advpmntavailadjust;
							}
							if(itemData.advpmntadjust == '' || itemData.advpmntadjust == null){
								payments.advpmntadjust = 0.00;
							}else{
								payments.advpmntadjust = itemData.advpmntadjust;
							}
							
							payments.advpmnttaxrate = itemData.advpmnttaxrate;
							if(itemData.advpmntigstamt == '' || itemData.advpmntigstamt == null){
								payments.advpmntigstamt = 0.00;
							}else{
							payments.advpmntigstamt = itemData.advpmntigstamt;
							}
							if(itemData.advpmntcgstamt == '' || itemData.advpmntcgstamt == null){
								payments.advpmntcgstamt =0.00;
							}else{
							payments.advpmntcgstamt = itemData.advpmntcgstamt;
							}
							if(itemData.advpmntcgstamt == '' || itemData.advpmntcgstamt == null){
								payments.advpmntsgstamt = 0.00;
							}else{
							payments.advpmntsgstamt = itemData.advpmntsgstamt;
							}
							if(itemData.advpmntcessrate == '' || itemData.advpmntcessrate == null){
								payments.advpmntcessrate = 0.0;
							}else{
								payments.advpmntcessrate = itemData.advpmntcessrate;
							}
							if(itemData.advpmntcessrate == '' || itemData.advpmntcessrate == null){
								payments.advpmntcessamt = 0.00;
							}else{
								payments.advpmntcessamt = itemData.advpmntcessamt;
							}
							
							if(itemData.advpmntremainamt == '' || itemData.advpmntremainamt == null){
								payments.advpmntremainamt =0.00;
							}else{
							payments.advpmntremainamt = itemData.advpmntremainamt;
							}
							payments.paymentitems= itemData.paymentitems;
							paymentslist.push(payments);
							
							$('#paymentstable1 tbody').append('<tr class="row'+itemData.userid+'"><td class="text-left">'+i+'</td><td class="text-left">'+itemData.paymentDate+'</td><td class="text-left">'+itemData.voucherNumber+'</td><td class="text-left">'+itemData.invoiceNumber+'</td><td class="text-left">'+itemData.customerName+'</td><td class="text-left">'+itemData.gstNumber+'</td><td class="text-right pindformat">'+payemntItemData.amount+'</td><td class="text-left">'+payemntItemData.modeOfPayment+'</td><td class="text-left">'+payemntItemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td><td class="actionicons"><a href="#" data-toggle="modal"  style="margin-right:3px;" data-target="#paymentsModal" onClick="updatePaymentDetails(\''+itemData.userid+'\',\''+rettype+'\',\''+jsptype+'\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+itemData.userid+'\',\''+rettype+'\',\''+itemData.clientid+'\',\''+itemData.voucherNumber+'\',\''+itemData.invoiceNumber+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>');
							totalamt += parseFloat(payemntItemData.amount);
							totFullAmt += parseFloat(payemntItemData.previousPendingBalance);
							pendamt += parseFloat(payemntItemData.pendingBalance);
							counts++;
							i++;
							transcounts++;
						});
					}else{
						var payments = new Object();
						payments.id = itemData.userid;
						payments.returntype = itemData.returntype;
						payments.invoiceid = itemData.invoiceid;
						payments.gstNumber = itemData.gstNumber;
						payments.customerName = itemData.customerName;
						payments.voucherNumber = itemData.voucherNumber;
						payments.invoiceNumber = itemData.invoiceNumber;
						payments.paymentDate = itemData.paymentDate;
						if(itemData.pendingBalance == '' || itemData.pendingBalance == null){
							payments.pendingBalance = 0.00;
						}else{
						payments.pendingBalance = itemData.pendingBalance;
						}
						if(itemData.receivedAmount == '' || itemData.receivedAmount == null){
							payments.receivedAmount = 0.00;
						}else{
						payments.receivedAmount = itemData.receivedAmount;
						}
						if(itemData.previousPendingBalance == '' || itemData.previousPendingBalance == null){
							payments.previousPendingBalance = 0.00;
						}else{
							payments.previousPendingBalance = itemData.previousPendingBalance;
						}
						payments.bankname = itemData.bankname;
						payments.accountnumber = itemData.accountnumber;
						payments.accountName = itemData.accountName;
						payments.branchname =itemData.branchname ;
						payments.ifsccode = itemData.ifsccode;
						payments.advpmntrecno = itemData.advpmntrecno;
						payments.advpmntrecdate = itemData.advpmntrecdate;
						payments.advpmntpos = itemData.advpmntpos;
						if(itemData.advpmntrecamt == '' || itemData.advpmntrecamt == null){
							payments.advpmntrecamt =0.00;
						}else{
						payments.advpmntrecamt = itemData.advpmntrecamt;
						}
						if(itemData.advpmntavailadjust == '' || itemData.advpmntavailadjust == null){
							payments.advpmntavailadjust = 0.00;
						}else{
						payments.advpmntavailadjust = itemData.advpmntavailadjust;
						}
						if(itemData.advpmntadjust == '' || itemData.advpmntadjust == null){
							payments.advpmntadjust = 0.00;
						}else{
							payments.advpmntadjust = itemData.advpmntadjust;
						}
						
						payments.advpmnttaxrate = itemData.advpmnttaxrate;
						if(itemData.advpmntigstamt == '' || itemData.advpmntigstamt == null){
							payments.advpmntigstamt = 0.00;
						}else{
						payments.advpmntigstamt = itemData.advpmntigstamt;
						}
						if(itemData.advpmntcgstamt == '' || itemData.advpmntcgstamt == null){
							payments.advpmntcgstamt =0.00;
						}else{
						payments.advpmntcgstamt = itemData.advpmntcgstamt;
						}
						if(itemData.advpmntcgstamt == '' || itemData.advpmntcgstamt == null){
							payments.advpmntsgstamt = 0.00;
						}else{
						payments.advpmntsgstamt = itemData.advpmntsgstamt;
						}
						if(itemData.advpmntcessrate == '' || itemData.advpmntcessrate == null){
							payments.advpmntcessrate = 0.0;
						}else{
							payments.advpmntcessrate = itemData.advpmntcessrate;
						}
						if(itemData.advpmntcessrate == '' || itemData.advpmntcessrate == null){
							payments.advpmntcessamt = 0.00;
						}else{
							payments.advpmntcessamt = itemData.advpmntcessamt;
						}
						
						if(itemData.advpmntremainamt == '' || itemData.advpmntremainamt == null){
							payments.advpmntremainamt =0.00;
						}else{
						payments.advpmntremainamt = itemData.advpmntremainamt;
						}
						paymentslist.push(payments);
						$('#paymentstable1 tbody').append('<tr class="row'+itemData.userid+'"><td class="text-center">'+i+'</td><td class="text-center">'+itemData.paymentDate+'</td><td class="text-center">'+itemData.voucherNumber+'</td><td class="text-center" class="text-center">'+itemData.invoiceNumber+'</td><td class="text-center">'+itemData.customerName+'</td><td class="text-center">'+itemData.gstNumber+'</td><td class="text-center pindformat">'+itemData.amount+'</td><td class="text-center">'+itemData.modeOfPayment+'</td><td class="text-center">'+itemData.referenceNumber+'</td><td class="text-center" style="display:none;">'+itemData.financialYear+'</td><td class="text-center" style="display:none;">'+itemData.month+'</td><td class="actionicons"><a href="#" data-toggle="modal" data-target="#paymentsModal" style="margin-right:3px;" onClick="updatePaymentDetails(\''+itemData.userid+'\',\''+rettype+'\',\''+jsptype+'\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+itemData.userid+'\',\''+rettype+'\',\''+itemData.clientid+'\',\''+itemData.voucherNumber+'\',\''+itemData.invoiceNumber+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td></tr>');
						totalamt += parseFloat(itemData.amount);
						totFullAmt += parseFloat(itemData.previousPendingBalance);
						pendamt += parseFloat(itemData.pendingBalance);
						counts++;
						i++;
						transcounts++;
					}
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
					
				});
				totFullAmt = totalamt + pendamt;
				$('#idCountGSTR2').html(transcounts);
				$('#idTotalValGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(totalamt).toFixed(2)));
				//$('#idTotalPendingGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pendamt).toFixed(2)));
				$('#idTotalPendingGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(purchseinvoicesTotalAmount-totalamt).toFixed(2)));
				$('#idTotalAmtGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(purchseinvoicesTotalAmount).toFixed(2)));
				$("#multiselectGSTR22").multiselect('rebuild');
				$("#multiselectGSTR23").multiselect('rebuild');
				paymentstable1 = $("#paymentstable1").DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">', 		
					"paging": true,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
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
						   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
							"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
					   }
					 },
					 drawCallback: function(){
				          $('.paginate_button', this.api().table().container())          
				             .on('click', function(){
				       		  $(".pindformat").each(function(){
				       			    $(this).html($(this).html().replace(/,/g , ''));
				       			});
				       		  OSREC.CurrencyFormatter.formatAll({selector : '.pindformat'});
				             });       
				       }
				});
				 $(".pindformat").each(function(){
	       			   
	       			    $(this).html($(this).html().replace(/,/g , ''));
	       			});
				OSREC.CurrencyFormatter.formatAll({
					selector: '.pindformat'
				});
				paymentsArray['GSTR2'] = response;
		}else{if ( $.fn.DataTable.isDataTable('#paymentstable1') ) {$('#paymentstable1').DataTable().destroy();}
		$('#paymentstable1 tbody').empty();
		paymentstable1 = $("#paymentstable1").DataTable({
			"dom": '<"toolbar"f>lrtip<"clear">', 		
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
			"responsive": true,
			"ordering": true,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
				   "previous": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-l.png />",
					"next": "<img src="+contextPath+"/static/mastergst/images/master/td-arw-r.png />"
			   }
			 }
		});
		$("#multiselectGSTR22").multiselect('rebuild');
		$("#multiselectGSTR23").multiselect('rebuild');
		$('#idCountGSTR2').html(0);
		$('#idTotalValGSTR2').html(formatNumber(parseFloat(0).toFixed(2)));
		//$('#idTotalPendingGSTR2').html(formatNumber(parseFloat(0).toFixed(2)));
		$('#idTotalPendingGSTR2').html(formatNumber(parseFloat(0).toFixed(2)));
		$('#idTotalAmtGSTR2').html(formatNumber(parseFloat(0).toFixed(2)));
	}
			},error:function(err){
			}
		});
		
		$('#multiselectGSTR21').multiselect({
			nonSelectedText: '- Financial Year-',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR2');
			},
			onSelectAll: function() {
				applyFilters('GSTR2');
			},
			onDeselectAll: function() {
				applyFilters('GSTR2');
			}
		});
		$('#multiselectGSTR22').multiselect({
			nonSelectedText: '- Supplier -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR2');
			},
			onSelectAll: function() {
				applyFilters('GSTR2');
			},
			onDeselectAll: function() {
				applyFilters('GSTR2');
			}
		});
		$('#multiselectGSTR23').multiselect({
			nonSelectedText: '- GSTIN -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR2');
			},
			onSelectAll: function() {
				applyFilters('GSTR2');
			},
			onDeselectAll: function() {
				applyFilters('GSTR2');
			}
		});
		
		$('#multiselectGSTR24').multiselect({
			nonSelectedText: '- Payment Mode -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR2');
			},
			onSelectAll: function() {
				applyFilters('GSTR2');
			},
			onDeselectAll: function() {
				applyFilters('GSTR2');
			}
		});
		$('#multiselectGSTR25').multiselect({
			nonSelectedText: '- Month -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyFilters('GSTR2');
			},
			onSelectAll: function() {
				applyFilters('GSTR2');
			},
			onDeselectAll: function() {
				applyFilters('GSTR2');
			}
		});
		$('#divFiltersGSTR2').on('click', '.deltag', function(e) {
			var val = $(this).data('val');
			$('#multiselectGSTR21').multiselect('deselect', [val]);
			$('#multiselectGSTR22').multiselect('deselect', [val]);
			$('#multiselectGSTR23').multiselect('deselect', [val]);
			$('#multiselectGSTR24').multiselect('deselect', [val]);
			$('#multiselectGSTR25').multiselect('deselect', [val]);
			applyFilters('GSTR2');
		});
		function applyFilters(retType) {
			paymentstable1.clear();
			var financialOptions = new Array();
			var paymentOptions = new Array();
			var monthOptions = new Array();
			financialOptions = $('#multiselectGSTR21 option:selected');
			paymentOptions = $('#multiselectGSTR24 option:selected');
			var customerOptions = $('#multiselectGSTR22 option:selected');
			var gstinOptions = $('#multiselectGSTR23 option:selected');
			monthOptions = $('#multiselectGSTR25 option:selected');
			if(financialOptions.length > 0 || paymentOptions.length > 0 || customerOptions.length > 0 || gstinOptions.length > 0 || monthOptions.length > 0){
				$('.normaltable .filter').css("display","block");
			}else{
				$('.normaltable .filter').css("display","none");
			}
			var financialArr=new Array();
			var monthArr=new Array();
			var customerArr=new Array();
			var gstinArr=new Array();
			var paymentArr=new Array();
			var financialYearOptions=new Array();var monthTotArr=new Array();
			var filterContent='';
			if(financialOptions.length > 0) {
				for(var i=0;i<financialOptions.length;i++) {
					filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+financialOptions[i].text+'<span data-val="'+financialOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
					financialArr.push(financialOptions[i].value);
					financialYearOptions.push(financialOptions[i].value);
					allInvoicesTotalGSTR2(monthTotArr,financialYearOptions);
				}
			} else {
				financialArr.push('All');
				financialYearOptions=new Array();
				allInvoicesTotalGSTR2(monthTotArr,financialYearOptions);
			}
			/*$.ajax({
				url: contextPath+"/allInvTotals"+paymenturlSuffix+"/GSTR2/"+month+"/"+financialYearOptions,
				async: false,
				cache: false,
				type: "GET",
				contentType: 'application/json',
				success : function(response){
					purchaseAmt = response.purchseTotalAmount;
					$('#idTotalAmtGSTR2').html(response.purchseTotalAmount);
				},error:function(err){
				}
			});*/
			if(monthOptions.length > 0) {
				for(var i=0;i<monthOptions.length;i++) {
					filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+monthOptions[i].text+'<span data-val="'+monthOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
					monthArr.push(monthOptions[i].value);
					monthTotArr.push(monthOptions[i].value);
					allInvoicesTotalGSTR2(monthTotArr,financialYearOptions);
				}
			} else {
				monthArr.push('All');
				monthTotArr=new Array();
				allInvoicesTotalGSTR2(monthTotArr,financialYearOptions);
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
			commonInvoiceFilter(retType, financialArr, monthArr,customerArr, gstinArr,paymentArr);
			paymentstable1.draw();
			$(".pindformat").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
			OSREC.CurrencyFormatter.formatAll({
				selector: '.pindformat'
			});
		}
	});
	function commonInvoiceFilter(retType, arrFinancial, arrMonth, arrCustomer, arrGstin, arrPayment) {
		if(paymentsArray['GSTR2'].length > 0) {
			var rows = new Array();
			var taxArray = new Array();
			var rowNode;
			var i=1;
			paymentsArray['GSTR2'].forEach(function(itemData) {
				var date = itemData.paymentDate;
				var dt = date.split("-");
				var month = parseInt(dt[1]);
				var year = parseInt(dt[2]);
				if(month < 4){
					year--;
				}
				itemData.financialYear = year.toString();
				itemData.month = dt[1];
				var rettype = 'GSTR2';
				var rowData  = new Array();
				if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
					$.each(itemData.paymentitems, function(index, payemntItemData) {
						rowData = [i,itemData.paymentDate,itemData.voucherNumber,itemData.invoiceNumber,itemData.customerName,itemData.gstNumber,payemntItemData.amount,payemntItemData.modeOfPayment,payemntItemData.referenceNumber,itemData.financialYear,itemData.month];
						rowData.push('<td class="actionicons"><a href="#" data-toggle="modal" style="margin-right:3px;" data-target="#paymentsModal" onClick="updatePayment(\''+rettype+'\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+itemData.userid+'\',\''+rettype+'\',\''+itemData.clientid+'\',\''+itemData.voucherNumber+'\',\''+itemData.invoiceNumber+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>');
						rows.push(rowData);
						taxArray.push([payemntItemData.amount,payemntItemData.previousPendingBalance,payemntItemData.pendingBalance]);
						i++;
					});
				}else{
					rowData = [i,itemData.paymentDate,itemData.voucherNumber,itemData.invoiceNumber,itemData.customerName,itemData.gstNumber,itemData.amount,itemData.modeOfPayment,itemData.referenceNumber,itemData.financialYear,itemData.month];
					rowData.push('<td class="actionicons"><a href="#" data-toggle="modal" style="margin-right:3px;" data-target="#paymentsModal" onClick="updatePayment(\''+rettype+'\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+itemData.userid+'\',\''+rettype+'\',\''+itemData.clientid+'\',\''+itemData.voucherNumber+'\',\''+itemData.invoiceNumber+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>');
					rows.push(rowData);
					taxArray.push([itemData.amount,itemData.previousPendingBalance,itemData.pendingBalance]);
					i++;
				}	
				
			});
			var index = 0, tpayments=0, tamt=0, pptamnt=0,pbal=0;
			rows.forEach(function(row) {
			if((arrFinancial.length == 0 || $.inArray('All', arrFinancial) >= 0 || $.inArray(row[9], arrFinancial) >= 0)
					&& (arrMonth.length == 0 || $.inArray('All', arrMonth) >= 0 || $.inArray(row[10], arrMonth) >= 0)
					&& (arrCustomer.length == 0 || $.inArray('All', arrCustomer) >= 0 || $.inArray(row[4], arrCustomer) >= 0)
					&& (arrGstin.length == 0 || $.inArray('All', arrGstin) >= 0 || $.inArray(row[5], arrGstin) >= 0)
					&& (arrPayment.length == 0 || $.inArray('All', arrPayment) >= 0 || $.inArray(row[7], arrPayment) >= 0)) {
					row[6] = "<span class='pindformat text-right' style='float:right'>"+row[6]+"</span>";
					rowNode = paymentstable1.row.add(row);
					tpayments++;
					tamt+=parseFloat(taxArray[index][0]);
					pptamnt+=parseFloat(taxArray[index][1]);
					pbal+=parseFloat(taxArray[index][2]);
			  }
			  index++;
			});
			$(".pindformat").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
			OSREC.CurrencyFormatter.formatAll({
				selector: '.pindformat'
			});
			 $('#idCountGSTR2').html(tpayments);
			$('#idTotalValGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt).toFixed(2))); 
			$('#idTotalAmtGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(purchaseAmt).toFixed(2))); 
			//$('#idTotalPendingGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pbal).toFixed(2)));
			$('#idTotalPendingGSTR2').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(purchaseAmt-tamt).toFixed(2)));
		}
	}	
		function commonInvoiceFilter1(retType, arrFinancial, arrMonth,arrCustomer, arrGstin, arrPayment) {
		if(paymentsArray['GSTR1'].length > 0) {
			var rows = new Array();
			var taxArray = new Array();
			var rowNode;
			var i=1;
			paymentsArray['GSTR1'].forEach(function(itemData) {
				var date = itemData.paymentDate;
				var dt = date.split("-");
				var month = parseInt(dt[1]);
				
				var year = parseInt(dt[2]);
				if(month < 4){
					year--;
				}
				itemData.financialYear = year.toString();
				itemData.month = dt[1];
				var rettype = 'GSTR1';
				var rowData  = new Array();
				if(itemData.paymentitems != null && itemData.paymentitems.length > 0){
					$.each(itemData.paymentitems, function(index, payemntItemData) {
						rowData = [i,itemData.paymentDate,itemData.voucherNumber,itemData.invoiceNumber,itemData.customerName,itemData.gstNumber,payemntItemData.amount,payemntItemData.modeOfPayment,payemntItemData.referenceNumber,itemData.financialYear,itemData.month];
						rowData.push('<td class="actionicons"><a href="#" data-toggle="modal" style="margin-right:3px;" data-target="#paymentsModal" onClick="updatePayment(\''+rettype+'\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+itemData.userid+'\',\''+rettype+'\',\''+itemData.clientid+'\',\''+itemData.voucherNumber+'\',\''+itemData.invoiceNumber+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>');
						rows.push(rowData);
						taxArray.push([payemntItemData.amount,payemntItemData.previousPendingBalance,payemntItemData.pendingBalance]);
						i++;
					});
				}else{
					rowData = [i,itemData.paymentDate,itemData.voucherNumber,itemData.invoiceNumber,itemData.customerName,itemData.gstNumber,itemData.amount,itemData.modeOfPayment,itemData.referenceNumber,itemData.financialYear,itemData.month];
					rowData.push('<td class="actionicons"><a href="#" data-toggle="modal" style="margin-right:3px;" data-target="#paymentsModal" onClick="updatePayment(\''+rettype+'\')"><i class="fa fa-edit"></i></a><a href="#" onClick="showDeletePopup(\''+itemData.userid+'\',\''+rettype+'\',\''+itemData.clientid+'\',\''+itemData.voucherNumber+'\',\''+itemData.invoiceNumber+'\')"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete" style="margin-top: -6px;"></a></td>');
					rows.push(rowData);
					taxArray.push([itemData.amount,itemData.previousPendingBalance,itemData.pendingBalance]);
					i++;
				}
				
			});
			var index = 0, tpayments=0, tamt=0, pptamnt=0,pbal=0;
			rows.forEach(function(row) {
			if((arrFinancial.length == 0 || $.inArray('All', arrFinancial) >= 0 || $.inArray(row[9], arrFinancial) >= 0)
					&& (arrMonth.length == 0 || $.inArray('All', arrMonth) >= 0 || $.inArray(row[10], arrMonth) >= 0)
					&& (arrCustomer.length == 0 || $.inArray('All', arrCustomer) >= 0 || $.inArray(row[4], arrCustomer) >= 0)
					&& (arrGstin.length == 0 || $.inArray('All', arrGstin) >= 0 || $.inArray(row[5], arrGstin) >= 0)
					&& (arrPayment.length == 0 || $.inArray('All', arrPayment) >= 0 || $.inArray(row[7], arrPayment) >= 0)) {
					row[6] = "<span class='indformat text-right' style='float:right'>"+row[6]+"</span>";
					rowNode = paymentstable.row.add(row);
					tpayments++;
					tamt+=parseFloat(taxArray[index][0]);
					pptamnt+=parseFloat(taxArray[index][1]);
					pbal+=parseFloat(taxArray[index][2]);
					
			  }
			  index++;
			});
			$(".indformat").each(function(){
   			    $(this).html($(this).html().replace(/,/g , ''));
   			});
			 OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
			 $('#idCountGSTR1').html(tpayments);
			$('#idTotalValGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(tamt).toFixed(2))); 
			$('#idTotalAmtGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(salesAmt).toFixed(2))); 
			//$('#idTotalPendingGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(pbal).toFixed(2))); 
			$('#idTotalPendingGSTR1').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(salesAmt-tamt).toFixed(2))); 
		}
	}
	function clearFilters(retType) {
		purchaseAmt=purchseinvoicesTotalAmount;
		$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		$('#divFilters').html('');
		$('.normaltable .filter').css("display","none");
		paymentstable1.clear();
		commonInvoiceFilter(retType, new Array(),new Array(), new Array(),new Array(),new Array());
		paymentstable1.draw();
		$(".pindformat").each(function(){
		    $(this).html($(this).html().replace(/,/g , ''));
		});
		OSREC.CurrencyFormatter.formatAll({selector : '.pindformat'});
	}
	function clearFilters1(retType) {
		salesAmt = invoicesTotalAmount;
		$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		$('#divFilters').html('');
		$('.normaltable .filter').css("display","none");
		paymentstable.clear();
		commonInvoiceFilter1(retType, new Array(),new Array(), new Array(),new Array(),new Array());
		paymentstable.draw();
		$(".indformat").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
		OSREC.CurrencyFormatter.formatAll({selector : '.indformat'});
	}
	
	function allInvoicesTotal(montharr,fparr){
		var paymentmethods=new Object();
		paymentmethods.month=montharr;
		paymentmethods.year=fparr;
		$.ajax({
			url:contextPath+"/allInvMothlyTotals"+paymenturlSuffix+"/GSTR1",
			async: false,
			cache: false,
			type: "POST",
			data: JSON.stringify(paymentmethods),
			contentType: 'application/json',
			success : function(response){
				salesAmt = response.mothlyInvoicesTotalAmount;
				$('#idTotalAmtGSTR1').html(response.salesTotalAmount);
			},error:function(err){
			}
		});
	}
	function allInvoicesTotalGSTR2(montharr,fparr){
		var paymentmethods2=new Object();
		paymentmethods2.month=montharr;
		paymentmethods2.year=fparr;
		$.ajax({
			url:contextPath+"/allInvMothlyTotals"+paymenturlSuffix+"/GSTR2",
			async: false,
			cache: false,
			type: "POST",
			data: JSON.stringify(paymentmethods2),
			contentType: 'application/json',
			success : function(response){
				purchaseAmt = response.pTotalAmt;
				$('#idTotalAmtGSTR2').html(response.pTotalAmt);
			},error:function(err){
			}
		});
	}
	
	function updatePaymentDetails(id,rettype,jsptype){
		$('#paymentsModal').modal("show");
		$('#paymentid').remove();
		$('#paymenthistory').css("display","none");
		$(".bank_details_add").removeAttr("checked").prop("checked",false);
		$("#payment_bank_details").hide();
		$('.pymntaddrow').attr("onclick","addeditrow()");
		//$("#bankdiv").css("display","none");
		if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
			$('#paymenthead').html('Receipts');
			$('#addpayment').html('Update Receipt');
			//$('#paymenthistory').html('Receipt History');
			$('#paymentbtn').html('Update Receipt');
		}else{
			$('#paymenthead').html('Payments');
			$('#addpayment').html('Update Payment');
			//$('#paymenthistory').html('Payment History');
			$('#paymentbtn').html('Update Payment');
		}
		$.ajax({
			url: contextPath+'/ledgerNames'+urlSuffixs+'/'+rettype,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(ledgerNames) {
				cashbankledgername = new Array();
				cashbankledgername = ledgerNames;
			}
		});
		if(rettype == 'GSTR1' || rettype == 'SalesRegister'){
			$.ajax({
				url: contextPath+'/bnkdtls'+urlSuffixs,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(bankDetails) {
					clientBankDetails = new Array();
					$('#bankselect').html('').hide();
					$("#bankselect").append($("<option></option>").attr("value","").text("-- Select Bank --")); 
					for (var i=0; i<bankDetails.length; i++) {
						$("#bankselect").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname));
						clientBankDetails.push(bankDetails[i]);
					}
					if(clientBankDetails && clientBankDetails.length == 1) {
						if(rettype != 'Purchase Register' && rettype != 'PurchaseOrder'){
							$("#payment_bank_details").show();
						}
						$(".bank_details_add").attr("checked","checked");
						$('#bankselect').val(clientBankDetails[0].accountnumber);
						selectBankName(clientBankDetails[0].accountnumber);
					}else if(clientBankDetails && clientBankDetails.length > 1){
						//$('#bankselect').attr("disabled","disabled");
					}
					$("#bankselect").show();
					
				}
			});
		}else{
			$('#pmnt_bank').css("display","none");
			$('#pmnt_details').css("display","none");
		}
		
		
		if(id) {
			$.ajax({
				url: _getContextPath()+'/getPaymentsInfo/'+id,
				async: false,
				cache: false,
				//dataType:"json",
				contentType: 'application/json',
				success : function(payments) {
					$('#paymentid').remove();
					$('.payment_table tbody').find("tr:gt(0)").remove();
					var totaladvtax = 0;
					$('#paymentbtn').removeClass('disabled');
					$("form[name='paymentform']").append('<input type="hidden" id="paymentid" name="id" value="'+payments.docId+'">');
					$('#paymentform').removeAttr("action");
					if(jsptype == 'journal'){
						$('#paymentform').attr("action",contextPath+"/record_payments"+urlSuffixs+"/"+rettype+"/"+pmntsSuffix+"?type="+jsptype);
					}else if(jsptype == 'ledgerreport'){
						$('#paymentform').attr("action",contextPath+"/record_payments"+urlSuffixs+"/"+rettype+"/"+pmntsSuffix+"?type="+jsptype);
					}else{
						$('#paymentform').attr("action",contextPath+"/record_payments"+urlSuffixs+"/"+rettype+"/"+pmntsSuffix+"?type="+rettype);
					}
					$('#voucherNumbers').val(payments.voucherNumber);
					$('#dateofpayment').val(payments.paymentDate);
					$('#invno').text(payments.invoiceNumber);
					$('#netreceive').text(formatNumber((payments.previousPendingBalance).toFixed(2)));
					$('#beforereceivedAmount').val(parseFloat((payments.receivedAmount).replace(/,/g, "")));
					$('#received').text(formatNumber((payments.paidAmount).toFixed(2)));
					$('#pendingamt').text(formatNumber((payments.previousPendingBalance - payments.paidAmount).toFixed(2)));
					$('#bankselect').val(payments.accountnumber);
					if(payments.accountnumber == ""){
						$('#details_bnk').css('display','none');
					}else{
						$('#details_bnk').css('display','block');
					}
					$('#bank_AcctNo').val(payments.accountnumber);
					$('#bank_AccountName').val(payments.accountName);
					$('#bank_Branch').val(payments.branchname);
					$('#bank_IFSC').val(payments.ifsccode);
					$('#advpmntdate_val').val(payments.advpmntrecdate);
					$('#advpmntdate').text(payments.advpmntrecdate);
					$('#advpmntpos_val').val(payments.advpmntpos);
					$('#advpmntpos').text(payments.advpmntpos);
					if(payments.advpmntrecamt != "" && payments.advpmntrecamt != undefined){
						
						$('#advpmntamnt_val').val((payments.advpmntrecamt).toFixed(2));
						$('#advpmntamnt').text(formatNumber((payments.advpmntrecamt).toFixed(2)));
					}
					if(payments.advpmntavailadjust != "" && payments.advpmntavailadjust != undefined){
						$('#advpmntadjust_val').val((payments.advpmntavailadjust).toFixed(2));
						$('#advpmntadjust').text(formatNumber((payments.advpmntavailadjust).toFixed(2)));
					}
					$('#advpmntno').val(payments.advpmntrecno);
					if(payments.advpmntadjust != "" && payments.advpmntadjust != undefined){
						$('#advpmnt_adjusted').val((payments.advpmntadjust).toFixed(2));
					}
					$('#advpmnttaxrate').val(payments.advpmnttaxrate);
					if(payments.advpmntigstamt != "" && payments.advpmntigstamt != undefined){
						totaladvtax = totaladvtax+payments.advpmntigstamt;
						$('#pmntigsttax').val((payments.advpmntigstamt).toFixed(2));
					}
					if(payments.advpmntcgstamt != "" && payments.advpmntcgstamt != undefined){
						totaladvtax = totaladvtax+payments.advpmntcgstamt;
						$('#pmntcgsttax').val((payments.advpmntcgstamt).toFixed(2));
					}
					if(payments.advpmntsgstamt != "" && payments.advpmntsgstamt != undefined){
						totaladvtax = totaladvtax+payments.advpmntsgstamt;
						$('#pmntsgsttax').val((payments.advpmntsgstamt).toFixed(2));
					}
					$('#advpmnttax').val((totaladvtax).toFixed(2));
					
					$('#advpmntcessrate').val(payments.advpmntcessrate);
					if(payments.advpmntcessamt != "" && payments.advpmntcessamt != undefined){
						$('#advpmntcessamnt').val((payments.advpmntcessamt).toFixed(2));
					}
					if(payments.advpmntremainamt != "" && payments.advpmntremainamt != undefined){
						$('#advpmntremaining').val((payments.advpmntremainamt).toFixed(2));
					}
					$('#invoice_id').val(payments.invoiceid);
					$('#previousPendingBalance').val(payments.previousPendingBalance);
					$('#invtype').val(payments.invtype);
					$('#recordinvno').val(payments.invoiceNumber);
	    			$('#custname_record').val(payments.customerName);
	    			$('#gstin').val(payments.gstNumber);
	    			$('#invoice_id').val(payments.invoiceid);
	    			$('#pendingBalance').val(payments.pendingBalance);
	    			$('#invoice_returntype').val(payments.returntype);
	    			$('#received_amount').val(payments.receivedAmount);
	    			$('#invtype').val(payments.invtype);
	    			if(payments.isadvadjust == true){
	    				$('.adv_adjusted_pmnt').prop("checked",true).val("true");
	    				$('#adv_pmnt_div,#sortable_table4').css("display","inline-block");
	    			}else{
	    				$('.adv_adjusted_pmnt').prop("checked",false).val("false");
	    				$('#adv_pmnt_div,#sortable_table4').css("display","none");
	    			}
	    			if(parseFloat(payments.previousPendingBalance) == parseFloat(payments.pendingBalance)){
	    				$('#paymentStatus').val("UnPaid");
	    			}else if(parseFloat(payments.pendingBalance) == 0){
	    				$('#paymentStatus').val("Paid");
	    			}else if(parseFloat((payments.receivedAmount).replace(/,/g, "")) > 0){
	    				$('#paymentStatus').val("Partially Paid");
	    			}
	    			var totalPending = 0.00;
	    			$('#amount1').attr("onkeyup","updateAmount1(1)");
	    			$('.mop_text1').children('option').remove();
	    			$(".mop_text1").append($("<option></option>").attr("value","").text("-- Select --"));
	    			for (var i=0; i<cashbankledgername.length; i++) {
	    				$(".mop_text1").append($("<option></option>").attr("value",cashbankledgername[i]).text(cashbankledgername[i]));
	    			}
					if(payments.paymentitems != null && payments.paymentitems.length > 0){
						for(var i=1;i<payments.paymentitems.length;i++){
							addeditrow();
						}
						$.each(payments.paymentitems, function(index, payemntItemData) {
							index++;
							$('.mop_text'+index).val(payemntItemData.modeOfPayment);
							if(payemntItemData.amount == '' || payemntItemData.amount == null){
								totalPaymentAmount = 0;
								$('.amount_text'+index).val(0.00);
							}else{
								totalPaymentAmount = totalPaymentAmount+payemntItemData.amount;
								$('.amount_text'+index).val(payemntItemData.amount);
							}
							$('.ref_text'+index).val(payemntItemData.referenceNumber);
							$('.ledger_text'+index).val(payemntItemData.ledger);
						});
					}
	    			$('#paymentsprogress-bar_'+referName).addClass('d-none');
				},error : function(err){
					$('#paymentsprogress-bar_'+referName).addClass('d-none');
				}
			});
		}
	}
	
	function addrow(){
		var rowCount = $('#paymentbody tr').length;
		var tablen = rowCount;
		rowCount = rowCount+1;
		var table3=document.getElementById("sortable_table3");
		//table3.insertRow(rowCount).outerHTML = '<tr><td id="sno_row2" align="center">'+rowCount+'</td><td id="" class="form-group" style="border: none;margin-bottom: 0px;"><select id="pament_mode'+rowCount+'" class="form-control mop_text'+rowCount+'" name="asd" required="required" data-error="Please select payment type"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS -GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select></td><td><input type="text" id="dateofpayment" name="paymentDate" class="form-control  date_text'+rowCount+'" aria-describedby="dateofpayment" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" placeholder="dd/mm/yyyy"  required="required" data-error="Please enter valid date"/></td><td id="" class="form-group"><input type="text" class="form-control amount_text'+rowCount+'" id="amount'+rowCount+'" name="amount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyUp="updateAmount('+rowCount+')" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td id="" class="form-group"><input type="text" class="form-control ref_text'+rowCount+'" playsholder="Reference No." required></td><td class="form-group"><input type="text" class="form-control ledger_text'+rowCount+'" required placeholder="Ledger"/></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_button'+rowCount+'" class="item_delete" onclick="delete_payrow('+rowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>';
		$('#paymentbody').append('<tr><td id="sno_row2" align="center">'+rowCount+'</td><td class="form-group" style="border: none;margin-bottom: 0px;"><select id="pament_mode'+rowCount+'" class="form-control mop_text'+rowCount+'" onchange="modeOfPaymentChange('+rowCount+')" name="paymentitems['+(rowCount-1)+'].modeOfPayment" required="required" data-error="Please select payment type"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS - GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select></td><td class="form-group"><input type="text" class="form-control amount_text'+rowCount+'" id="amount'+rowCount+'" name="paymentitems['+(rowCount-1)+'].amount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyUp="updateAmount('+rowCount+')" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td class="form-group"><input type="text" class="form-control ref_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].referenceNumber" placeholder="Reference No."></td><td class="form-group"><input type="text" class="form-control ledger_text'+rowCount+'" id="ledger_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].ledger" required placeholder="Ledger"/></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_button'+rowCount+'" class="item_delete" onclick="delete_payrow('+rowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
		//$('#sortable_table3').tableDnDUpdate();
		var ledgeroptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/ledgerlist/"+clientId+"?query="+ phrase + "&format=json";
			},
			getValue: "ledgerName",
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $("#ledger_text"+rowCount).getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($("#eac-container-ledger_text"+rowCount+" ul").children().length == 0) {
						$("#addlegername").show();
					} else {
						$("#addlegername").hide();
					}
				},
				maxNumberOfElements: 10
			}
		};
	$('#ledger_text'+rowCount).easyAutocomplete(ledgeroptions);
	$('.date_text'+rowCount).datetimepicker({
	  	timepicker: false,
	  	format: 'd-m-Y',
	  	scrollMonth: true
	});
		$('form[name="paymentform"]').validator('update');
	}
	
	function updateAmount1(no){
		var beforereceivedAmount=$('#beforereceivedAmount').val();
		var receive=$('#received').text().replace(/,/g, "");
		var netreceive=parseFloat($('#netreceive').text().replace(/,/g, ""));
		var amt=$('#amount'+no).val();
		if(amt == ''){
	    	amt = 0;	
	    }	
		//$('#received').html(0);
	  var totalamt = parseFloat(netreceive);
	  updatePaymentTotals(no);
	  var rec = parseFloat($('#received').text().replace(/,/g, ""));
	  var tamt = 0;
	  if(rec > parseFloat(beforereceivedAmount)){
		  tamt =  ((rec - parseFloat(beforereceivedAmount))+parseFloat(beforereceivedAmount)).toFixed(2);
	  }else{
		  tamt =  (parseFloat(beforereceivedAmount)-(parseFloat(totalPaymentAmount) - rec)).toFixed(2);  
	  }
	  var pendamt=netreceive - tamt;
	  
	  $('#pendingBalance').val(pendamt);
	 $('#pendingamt').text(formatNumber(parseFloat(pendamt).toFixed(2)));
	 $('#received').html(formatNumber(parseFloat(tamt).toFixed(2)));
	 $('.openingbal').html(formatNumber(parseFloat(pendamt).toFixed(2)));
	 $('#received_amount').val(formatNumber(parseFloat(tamt).toFixed(2)));

		var received_amount = $('#received').text().replace(/,/g, "");
		var pendingamt = $('#pendingamt').text().replace(/,/g, "");
		
		var ramt=parseFloat(received_amount);
		var pamt=parseFloat(pendingamt);
		
		 if(pamt == 0){
			 $('#paymentStatus').val("Paid");
		 }else if(pamt != 0){
			 $('#paymentStatus').val("Partially Paid");
		 }else {
			 $('#paymentStatus').val("UnPaid");
		 }
	}
	
	function addeditrow(){
		var rowCount = $('#paymentbody tr').length;
		var tablen = rowCount;
		rowCount = rowCount+1;
		var table3=document.getElementById("sortable_table3");
		//table3.insertRow(rowCount).outerHTML = '<tr><td id="sno_row2" align="center">'+rowCount+'</td><td id="" class="form-group" style="border: none;margin-bottom: 0px;"><select id="pament_mode'+rowCount+'" class="form-control mop_text'+rowCount+'" name="asd" required="required" data-error="Please select payment type"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS -GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select></td><td><input type="text" id="dateofpayment" name="paymentDate" class="form-control  date_text'+rowCount+'" aria-describedby="dateofpayment" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" placeholder="dd/mm/yyyy"  required="required" data-error="Please enter valid date"/></td><td id="" class="form-group"><input type="text" class="form-control amount_text'+rowCount+'" id="amount'+rowCount+'" name="amount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyUp="updateAmount('+rowCount+')" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td id="" class="form-group"><input type="text" class="form-control ref_text'+rowCount+'" playsholder="Reference No." required></td><td class="form-group"><input type="text" class="form-control ledger_text'+rowCount+'" required placeholder="Ledger"/></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_button'+rowCount+'" class="item_delete" onclick="delete_payrow('+rowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>';
		$('#paymentbody').append('<tr><td id="sno_row2" align="center">'+rowCount+'</td><td class="form-group" style="border: none;margin-bottom: 0px;"><select id="pament_mode'+rowCount+'" class="form-control mop_text'+rowCount+'" onchange="modeOfPaymentChange('+rowCount+')" name="paymentitems['+(rowCount-1)+'].modeOfPayment" required="required" data-error="Please select payment type"><option value="">-Select-</option><option value=Cash>Cash</option><option value=Bank>Bank</option><option value=TDS-IT>TDS - IT</option><option value=TDS-GST>TDS - GST</option><option value=Discount>Discount</option><option value=Others>Others</option></select></td><td class="form-group"><input type="text" class="form-control amount_text'+rowCount+'" id="amount'+rowCount+'" name="paymentitems['+(rowCount-1)+'].amount" required="required" pattern="[0-9]+(\.[0-9][0-9]?)?" data-error="Please enter amount" onKeyUp="updateAmount1('+rowCount+')" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 46) || (event.charCode == 8))" placeholder="Amount" /></td><td class="form-group"><input type="text" class="form-control ref_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].referenceNumber" placeholder="Reference No."></td><td class="form-group"><input type="text" class="form-control ledger_text'+rowCount+'" id="ledger_text'+rowCount+'" name="paymentitems['+(rowCount-1)+'].ledger" required placeholder="Ledger"/></td><td align="center" width="2%"><a href="javascript:void(0)" id="delete_button'+rowCount+'" class="item_delete" onclick="delete_payrow('+rowCount+')"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td></tr>');
		//$('#sortable_table3').tableDnDUpdate();
		$('.mop_text'+rowCount).children('option').remove();
		$(".mop_text"+rowCount).append($("<option></option>").attr("value","").text("-- Select --"));
		for (var i=0; i<cashbankledgername.length; i++) {
			$(".mop_text"+rowCount).append($("<option></option>").attr("value",cashbankledgername[i]).text(cashbankledgername[i]));
		}
		var ledgeroptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return contextPath+"/ledgerlist/"+clientId+"?query="+ phrase + "&format=json";
			},
			getValue: "ledgerName",
			list: {
				match: {
					enabled: true
				},
			onChooseEvent: function() {
				var groupdetails = $("#ledger_text"+rowCount).getSelectedItemData();
			}, 
				onLoadEvent: function() {
					if($("#eac-container-ledger_text"+rowCount+" ul").children().length == 0) {
						$("#addlegername").show();
					} else {
						$("#addlegername").hide();
					}
				},
				maxNumberOfElements: 10
			}
		};
	$('#ledger_text'+rowCount).easyAutocomplete(ledgeroptions);
	$('.date_text'+rowCount).datetimepicker({
	  	timepicker: false,
	  	format: 'd-m-Y',
	  	scrollMonth: true
	});
		$('form[name="paymentform"]').validator('update');
	}
	