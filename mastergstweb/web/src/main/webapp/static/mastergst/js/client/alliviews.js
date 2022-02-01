var mannualMatchTableUrl = new Object();
$(function () {
	var invdate_2a = '01-'+month+'-'+year;
	$('.allitcclaimdate').datetimepicker({
	  	timepicker: false,
	  	format: 'd-m-Y',
	  	scrollMonth: true
	});
	$('#start-date').datetimepicker({
		value:invdate_2a,
		timepicker: false,
		format: 'd-m-Y',
		maxDate: new Date(year,month-1,new Date(year,month,0).getDate()),
		scrollMonth: true
	});
	$('#end-date').datetimepicker({
		value:invdate_2a,
		timepicker: false,
		format: 'd-m-Y',
		maxDate: new Date(year,month-1,new Date(year,month,0).getDate()),
		scrollMonth: true
	});
	$('.expense_pmntDate').datetimepicker({
		timepicker: false,
		format: 'd/m/Y',
		scrollMonth: true
	});
	$(".expense_pmntDate").on("change",function(){
        var selected = $(this).val();
        $('.expense_pmntDate').attr("value",selected);
        $('.expense_pmntDate').val(selected);
    });
	$('.expPmntd1').datetimepicker({
		timepicker: false,
		format: 'd-m-Y',
		scrollMonth: true
	});
	$('.expPmntd2').datetimepicker({
		timepicker: false,
		format: 'd-m-Y',
		scrollMonth: true
	});
    $(".expPmntd1").on("change",function(){
        var selected = $(this).val();
        $('.expPmntd1').attr("value",selected);
    });
    $(".expPmntd2").on("change",function(){
        var selected = $(this).val();
        $('.expPmntd2').attr("value",selected);
    });
	$('.enddatewrap').click(function(){
		$('#end-date').focus();
	});
	$('.startdatewrap').click(function(){
		$('#start-date').focus();
	});
	$('.startExpdatewrap').click(function(){
		$('.expstartdate').focus();
	});
	$('.endExpdatewrap').click(function(){
		$('.expenddate').focus();
	});
	var table;
	if(rType == 'GSTR1' && otherreturnType == 'DELIVERYCHALLANS'){
		$('#deliveryTab').click();
	}else if(rType == 'GSTR1' && otherreturnType == 'ESTIMATES'){
		$('#estimateTab').click();
	}else if(rType == 'GSTR1' && otherreturnType == 'PROFORMAINVOICES'){
		$('#proformaTab').click();
	}else if(rType == 'GSTR2' && otherreturnType == 'PurchaseOrder'){
		$('#purchaseOrderTab').click();
	}else if(rType == 'GSTR2' && otherreturnType == 'EXPENSES'){
		$('#expensesTab').click();
	}else if((rType == 'GSTR1' && invoiceStatus == 'dwnld') || (rType == 'GSTR1' && invoiceStatus == 'inv')){
		$('#invoiceTab').click();
	}else if(rType == 'GSTR2' && invoiceStatus == 'dwnld'){
		$('#purchaseTab').click();
	}else if(rType == 'GSTR1' && invoiceStatus == 'dwnldgstr1a'){
		$('#gstr1aTab').click();
	}else if(rType == 'GSTR2' && invoiceStatus == 'dwnldgstr2a'){
		$('#downloadTab').click();
	}else if(rType == 'GSTR2' && invoiceStatus == 'gstr2b'){
		$('#gstr2bTab').click();
	}else if(rType == 'GSTR2' && invoiceStatus == 'gstr2breconcile'){
		$('#gstr2bmisMatchTab').click();
	}else if(rType == 'ANX2'){
		$('#invoiceTab').click();
	}else if(rType == 'ANX2' && invoiceStatus == 'inv'){
		$('#purchaseTab').click();
	}else if(rType == 'ANX1'){
		$('#invoiceTab').click();
	}else if(invoiceStatus == 'mmtch'){
		$('#reconcileTab').click();
	}else if(invoiceStatus == 'mmtchinv'){
		$('#reconcileTab').click();
		$('#reconcilemodal').modal('show');
	}else if(invoiceStatus == 'gstr2breconcile'){
		$('#gstr2bmisMatchTab').click();
	}else if(invoiceStatus == 'EXPENSES'){
		$('#expensesTab').click();
	}else if((rType == 'GSTR2' && invoiceStatus == 'inv') || (rType == 'GSTR2')){
		if(invoiceStatus != 'Exp'){
			$('#purchaseTab').click();
		}else{
			$('#expensesTab').click();
		}
	}else if(rType == 'EWAYBILL'){
		$('#invoiceTab').click();
	}else if((rType == 'GSTR4' || rType == 'GSTR5' || rType == 'GSTR6') && invoiceStatus == 'inv'){
		$('#invoiceTab').click();
	}
	
	$(".dbTableUp .form-control").attr('readonly', true);
	$('.tpone-save, .tpone-cancel,.tptwo-save, .tptwo-cancel,.tpthree-save, .tpthree-cancel,.tpfour-save, .tpfour-cancel,.tpfive-save, .tpfive-cancel, .addmorewrap').hide();
	$(document).on('click', '.card-header', function(){
		$(this).addClass('active').siblings().removeClass('active');
	});
	$(document).on('keyup', ".dbTableUp .form-control",function () {
		var str1 = $(this).attr('id');
		if(str1.indexOf('-num') > 0) {
			var st = $(this).attr('name');
			var st12 = st.split(".");
			var totalNum=$("input[name='"+st12[0]+"."+st12[1]+".totnum']").val();
			var cancelNum=$("input[name='"+st12[0]+"."+st12[1]+".cancel']").val();
			
			$("input[name='"+st12[0]+"."+st12[1]+".netIssue']").val(totalNum - cancelNum);
			/*var strlen = str1.length;
			var res1 = str1.substr(10, strlen);
			var totalNum=  document.getElementById("totals-num"+res1).value
			var cancelNum =  document.getElementById("cancel-num"+res1).value
			var netNum = document.getElementById("net-issue"+res1).value = (totalNum - cancelNum);*/
		}
	});
	$(document).keydown(function(e) {
		if (e.keyCode == 27) {
			var sales = $('#idInvType').val();
			if('B2B' == sales || 'B2C' == sales || 'Advances' == sales || 'Advance Adjusted Detail' == sales || 'Credit/Debit Note for Unregistered Taxpayers' == sales || 'Credit/Debit Notes' == sales || 'Exports' == sales || 'B2B Unregistered' == sales || 'Import Goods' == sales || 'Import Services' == sales || 'ITC Reversal' == sales){
				closemodal('invoiceModal');
			}
		}
	});
	$('[rel=tooltip]').tooltip({ trigger: "hover" });
	$(".otp_form_input .invoice_otp").keyup(function () {
		if (this.value.length == this.maxLength) {
			$(this).next().next('.form-control').focus();
		}
	});
	$('#nav-client').addClass('active');
	$('.nav-link').click(function(){
		rolesPermissions();
		if($(this).attr('href') == '#gtab4') {
			invoiceStatus = '';
		} else if($(this).attr('href') == '#gtab1') {
			invoiceStatus = 'inv';
		} else if($(this).attr('href') == '#gtab2') {
			invoiceStatus = 'mmtch';
		} else if($(this).attr('href') == '#gtab6') {
			invoiceStatus = 'dwnld';
		} else if($(this).attr('href') == '#gtab5') {
			invoiceStatus = 'prchse';
		}
	});
	dbFilingTable = $('#dbFilingTable').DataTable({
		"dom": '<"toolbar">frtip',
		"paging": false,
		"searching": false,
		"responsive": true,
		"order":[[1,'asc']],
		"columnDefs": [
		  { className: "dt-right", "targets": [2,3,4,5] },
            {
                "targets": [ 1 ],
                "visible": false
            }
		]
	});
	dbHSNFilingTable = $('#dbHSNFilingTable').DataTable({
		"dom": '<"toolbar">frtip',
		"pageLength": 10,
		"paging": true,
		"searching": false,
		"responsive": true,
		"columnDefs": [
		  { className: "dt-right", "targets": [3,4,5,6,7,8,9] }
		]
	});
	dbDocIssueFilingTable = $('#dbDocIssueFilingTable').DataTable({
		"dom": '<"toolbar">frtip',
		"pageLength": 10,
		"paging": true,
		"searching": false,
		"responsive": true,
		"columnDefs": [
		  { className: "dt-right", "targets": [3,4,5] }
		]
	});
	offLiabTable = $('#offLiabTable').DataTable({
		"dom": '<"toolbar">frtip',
		"paging": true,
		"searching": false,
		"responsive": true,
		"columnDefs": [
		  { className: "dt-right", "targets": [3,4,5] }
		]
	});
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
	
	dbSendMsgTable = $('#msgDetailTable').DataTable({
		"dom": '<"toolbar">frtip',
		"pageLength": 10,
		"paging": true,
		"searching": false,
		"responsive": true,
		"order": [[2,'desc']]
	});
	$('#msgDetailTable_wrapper .toolbar').html('Invoices');
	$('#dbTable_wrapper .toolbar').addClass('pull-left');
	$('.tabtable1 #dbTable_wrapper .toolbar').removeClass('pull-left');
	$('table.display').DataTable({
		"dom": '<"toolbar">frtip',
		"paging": true,
		"searching": false,
		"responsive": true
	});
	/*---- dropdown menu hover ----*/
	$('ul.nav li.dropdown, .navbar-left ul li.dropdown, .addlist-dd.dropdown, .addsales.dropdown').hover(function() {
	  $(this).find('.dropdown-menu').stop(true, true).delay(200).fadeIn(500);
	}, function() {
	  $(this).find('.dropdown-menu').stop(true, true).delay(200).fadeOut(500);
	});

	$('.addsales.dropdown').hover(function(){
		$('.addsales.dropdown .showarrow').hover();										 
	});
	$('a.dropdown-toggle').click(function(){
		var $url = $(this).attr('href');
		window.location = $url;
	});
});
function clickEdit(a,b,c,d,e){
	//$(a).show();
	$(b).show();
	$(c).hide();
	$('#dbTableUp'+a+' .form-control').each(function() {
		if(($(this).attr('name').indexOf('docNum') == -1) 
			&& ($(this).attr('name').indexOf('netIssue') == -1)) {
			$(this).attr('readonly', false);
		}
	});
	$(d).addClass('tpone-input-edit');
	$(e).show();
}
function clickSave(a,b,c,d,e){
	$(a).hide();
	$(b).hide();
	$(c).show();
	$(d).attr('readonly', true);
	$(d).addClass('tpone-input-edit');
	$(e).hide();
	var formObj = document.getElementById('docForm');
	var formURL = formObj.action;
	var formData = new FormData(formObj);
	$.ajax({
		url: formURL,
		type: 'POST',
		data:  formData,
		mimeType:"multipart/form-data",
		contentType: false,
		cache: false,
		processData:false,
		success: function(data) {
			successNotification('Save data successful!');
		},
		error: function(e, status, error) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
	});
}
function clickCancel(a,b,c,d,e){
	$(a).hide();
	$(b).hide();
	$(c).show();
	$(d).attr('readonly', true);
	$(d).addClass('tpone-input-edit');
	$(e).hide();		
}
function invokeImport() {
	$('#invoiceTab').click();
	$('#importModal').modal('show');
}
function invokeUpload() {
	$('#invoiceTab').click();
	var nArray=new Array();
	uploadInvoiceSet(nArray, 0, '','hsn');
}
function invokeSubmit(usrid) {
	$('#filingTab').click();
	showSubmitPopup(usrid);
}
function viewFailed(retType) {
	$('#invoiceTab').click();
	applyFiltersForFailedInvoices(retType,'Failed');
}

function applyFiltersForFailedInvoices(retType, invstatus) {
	$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		invoiceTable[retType.replace(" ", "_")].clear();
		selInvArray=new Array();
		selArray[retType.replace(" ", "_")]=new Array();
		$('#multiselect'+retType+'1').multiselect().find(':checkbox[value="Failed"]').attr('checked','checked');
		
		$('#multiselect'+retType+'1 option[value="Failed"]').attr("selected", 1);
		$('#multiselect'+retType+'1').multiselect('select','Failed').multiselect('updateButtonText');
		$('#multiselect'+retType+'1').multiselect('refresh');
		var typeOptions = $('#multiselect'+retType.replace(" ", "_")+'2 option:selected');
		var userOptions = $('#multiselect'+retType.replace(" ", "_")+'3 option:selected');
		var vendorOptions = $('#multiselect'+retType.replace(" ", "_")+'4 option:selected');
		var branchOptions = $('#multiselect'+retType.replace(" ", "_")+'5 option:selected');
		var verticalOptions = $('#multiselect'+retType.replace(" ", "_")+'6 option:selected');
		$('.normaltable .filter').css("display","block");
		
		var statusArr=new Array();
		var typeArr=new Array();
		var userArr=new Array();
		var vendorArr=new Array();
		var branchArr=new Array();
		var verticalArr=new Array();
		var reverseChargeArr=new Array();
		var filterContent = '<span data-role="tagsinput" placeholder="" class="btaginput" >Failed<span data-val="Failed" class="deltag" data-role="remove"></span></span>';
		statusArr.push('Failed');
		$('#divFilters'+retType.replace(" ", "_")).html(filterContent);
		commonInvoiceFilter(retType, statusArr, typeArr, userArr,vendorArr,branchArr,verticalArr,reverseChargeArr);
		invoiceTable[retType.replace(" ", "_")].draw();
	}

function showDeletePopup(invId, invNo, retType) {
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html(" #"+invNo);
	$('#btnDelete').attr('onclick', "deleteInvoice('"+invId+"','"+retType+"', null)");
}
function showDeleteAllPopup(retType,returntype,booksOrReturns,allOrSelected,month,year) {
	$('#deleteModal').modal('show');
	$('#delPopupDetails').html("s of the Selected Period");
	if(retType == "EXPENSES"){
		$('.invOrExpenseTypeHead,.invOrExpenseType,.invOrExpenseTypeBtn').text("Expense");
		$('.invOrExpenseTypeNote').text("Expenses");
	}else{
		$('.invOrExpenseTypeHead,.invOrExpenseType,.invOrExpenseTypeBtn').text("Invoice");
		$('.invOrExpenseTypeNote').text("Invoices");
	}
	if(allOrSelected == 'selectedInvs'){
		$('#btnDelete').attr('onclick', "deleteSelectedInvoices('"+retType+"','"+returntype+"','"+booksOrReturns+"')");
	}else{
		var invviewoption = $('#invoiceview_option').text();
			if(invviewoption.indexOf('Yearly') != -1){
				year = $('#invoiceviewfinancialYear').val();
			}
		if(retType == "EXPENSES"){
			$('#btnDelete').attr('onclick', "deleteAllExpenses('"+retType+"','"+month+"','"+year+"')");
		}else{
			$('#btnDelete').attr('onclick', "deleteAllInvoices('"+retType+"','"+returntype+"','"+booksOrReturns+"','"+month+"','"+year+"')");
		}
	}
}
function deleteAllExpenses(retType,month,year){
	$('#deleteModal').modal('hide');
	$('.invview_Process').removeClass('d-none').css("top","1%");
	setTimeout(function(){
		$.ajax({
			url:contextPath+"/deleteAllExpenses"+urlSuffixs+"/"+retType+"/"+month+"/"+year,
			type:'POST',
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+retType+'/'+month+'/'+year+'?type=Exp';
				$('.invview_Process').addClass('d-none').css("width","37%");
			}, error: function(error){
				$('.invview_Process').addClass('d-none').css("width","37%");
			}
		});
	},1000);
}

function deleteSelectedInvoices(retType,returntype,booksOrReturns){
	if(retType == 'GSTR1' || retType == 'ANX1' || retType == 'GSTR4' || retType == 'GSTR5' || retType == 'DELIVERYCHALLANS' || retType == 'ESTIMATES'){
		if(selArray[retType.replace(" ", "_")].length > 0) {
			deleteSelectInvoicess(selArray[retType.replace(" ", "_")],retType,returntype,booksOrReturns);
		} else {
			deleteSelectInvoicess(new Array(),retType,returntype,booksOrReturns);
		}
	}else if(retType == 'EWAYBILL'){
		if(ewaybillArray.length > 0) {
			deleteSelectInvoicess(ewaybillArray,retType,returntype,booksOrReturns);
		} else {
			deleteSelectInvoicess(new Array(),retType,returntype,booksOrReturns);
		}
	}else if(retType == 'PROFORMAINVOICES'){
		if(PIArray.length > 0) {
			deleteSelectInvoicess(PIArray,retType,returntype,booksOrReturns);
		} else {
			deleteSelectInvoicess(new Array(),retType,returntype,booksOrReturns);
		}
	}else if(retType == 'EXPENSES'){
		if(expensesArray.length > 0) {
			deleteSelectExpensess(expensesArray,retType,returntype,booksOrReturns);
		} else {
			deleteSelectExpensess(new Array(),retType,returntype,booksOrReturns);
		}
	}else{
		if(prchInvArray.length > 0) {
			deleteSelectInvoicess(prchInvArray,retType,returntype,booksOrReturns);
		} else {
			deleteSelectInvoicess(new Array(),retType,returntype,booksOrReturns);
		}
	}
}
function deleteSelectExpensess(invArray,retType,returntype,booksOrReturns){
	$('#deleteModal').modal('hide');
	if(invArray.length <= 10){
		$('.invview_Process').removeClass('d-none').css("top","15%");
	}else if(invArray.length <= 25){
		$('.invview_Process').removeClass('d-none').css("top","10%");
	}else if(invArray.length <= 50){
		$('.invview_Process').removeClass('d-none').css("top","5%");
	}else if(invArray.length <= 100){
		$('.invview_Process').removeClass('d-none').css("top","3%");
	}else{
		$('.invview_Process').removeClass('d-none').css("top","1%");
	}
	setTimeout(function(){
		$.ajax({
			url:contextPath+"/delSelectedExps"+urlSuffixs+"/"+retType+"/"+month+"/"+year,
			type:'POST',
			async: false,
			cache: false,
			contentType: 'application/json',
			data: JSON.stringify(invArray),
			success : function(response) {
				window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+retType+'/'+month+'/'+year+'?type=Exp';
			
				$('.invview_Process').addClass('d-none').css("width","37%");
			}, error: function(error){
				$('.invview_Process').addClass('d-none').css("width","37%");
			}
		});
	},1000);
}
function deleteSelectInvoicess(invArray,retType,returntype,booksOrReturns){
	$('#deleteModal').modal('hide');
	if(invArray.length <= 10){
		$('.invview_Process').removeClass('d-none').css("top","15%");
	}else if(invArray.length <= 25){
		$('.invview_Process').removeClass('d-none').css("top","10%");
	}else if(invArray.length <= 50){
		$('.invview_Process').removeClass('d-none').css("top","5%");
	}else if(invArray.length <= 100){
		$('.invview_Process').removeClass('d-none').css("top","3%");
	}else{
		$('.invview_Process').removeClass('d-none').css("top","1%");
	}
	
	var invdat = new Object;
	invdat.invnos = invArray;
	
	setTimeout(function(){
		$.ajax({
			url:contextPath+"/delSelectedInvss"+urlSuffixs+"/"+retType+"/"+month+"/"+year+'?booksOrReturns='+booksOrReturns,
			type:'POST',
			async: false,
			cache: false,
			contentType: 'application/json',
			data: JSON.stringify(invdat),
			success : function(response) {
				if(returntype != ''){
					window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+returntype+'/'+month+'/'+year+'?type=';
				}else{
					if(retType == "Purchase Register"){
						var varRetTypes = "GSTR2";
						window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+varRetTypes+'/'+month+'/'+year+'?type=';
					}else if(retType == "EXPENSES"){
						window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+varRetTypes+'/'+month+'/'+year+'?type=Exp';
					}else{
						window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+retType+'/'+month+'/'+year+'?type=';
					}
				}
				$('.invview_Process').addClass('d-none').css("width","37%");
			}, error: function(error){
				$('.invview_Process').addClass('d-none').css("width","37%");
			}
		});
	},1000);
}
function deleteAllInvoices(retType,returntype,booksOrReturns,month,year){
	$('#deleteModal').modal('hide');
	$('.invview_Process').removeClass('d-none').css("top","1%");
	setTimeout(function(){
		$.ajax({
			url:contextPath+"/deleteAllInvoices"+urlSuffixs+"/"+retType+"/"+month+"/"+year+'?booksOrReturns='+booksOrReturns,
			type:'POST',
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response) {
				if(returntype != ''){
					window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+returntype+'/'+month+'/'+year+'?type=';
				}else if(retType == "EXPENSES"){
					window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+varRetTypes+'/'+month+'/'+year+'?type=Exp';
				}else{
					if(retType == "Purchase Register"){
						var varRetTypes = "GSTR2";
						window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+varRetTypes+'/'+month+'/'+year+'?type=';
					}else{
						window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+retType+'/'+month+'/'+year+'?type=';
					}
				}
				$('.invview_Process').addClass('d-none').css("width","37%");
			}, error: function(error){
				$('.invview_Process').addClass('d-none').css("width","37%");
			}
		});
	},1000);
}

function deleteSelectInvoices(invArray,retType,returntype,booksOrReturns){
		$.ajax({
			url: contextPath+"/delSelectedInvs"+urlSuffixs+"/"+retType+"/"+month+"/"+year+"/"+invArray+'?booksOrReturns='+booksOrReturns,
				success : function(response) {
					if(returntype != ''){
						window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+returntype+'/'+month+'/'+year+'?type='+invoiceStatus;
					}else{
						if(retType == "Purchase Register"){
							var varRetTypes = "GSTR2";
							window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+varRetTypes+'/'+month+'/'+year+'?type='+invoiceStatus;
						}else{
							window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+varRetType+'/'+month+'/'+year+'?type='+invoiceStatus;
						}
					}
				}
		});
}
function updatePurchaseMainSelection(chkBox) {
	prchInvArray = new Array();
	if(chkBox.checked) {
		//$('#idPermissionUpload_Invoice').removeClass('disable');
		$('#deletePurchaseInvoices').removeClass('disabled');
		purchaseTable.rows().every(function () {
			var row = this.data();
			var index = $(row[0]).attr('index');
			prchInvArray.push(index);
			row[0]='<div class="checkbox" index="'+index+'"><label><input type="checkbox" id="invPFilter'+index+'" checked onclick="updateSelection(\''+index+'\', this)"/><i class="helper"></i></label></div>';
			this.data(row);
			this.invalidate();
		});
		purchaseTable.draw();
	} else {
		//$('#idPermissionUpload_Invoice').addClass('disable');
		$('#deletePurchaseInvoices').addClass('disabled');
		purchaseTable.rows().every(function () {
			var row = this.data();
			var index = $(row[0]).attr('index');
			row[0]='<div class="checkbox" index="'+index+'"><label><input type="checkbox" id="invPFilter'+index+'" onclick="updateSelection(\''+index+'\', this)"/><i class="helper"></i></label></div>';
			this.data(row);
			this.invalidate();
		});
		purchaseTable.draw();
	}
}
function processResponse(statusResponse) {
	invTable[varRetType.replace(' ', '_')].draw();
	/*if(statusResponse.status_cd == '1') {
		if(statusResponse.data.status_cd == 'P') {
			invTable[varRetType.replace(' ', '_')].rows().every(function () {
				var row = this.data();
				var index = $(row[0]).attr('index');
				if(selArray[varRetType.replace(" ", "_")].length == 0 || $.inArray(index, selArray[varRetType.replace(" ", "_")]) >= 0) {
					row[1]="<span class='color-green'>Uploaded</span>";
					this.data(row);
					this.invalidate();
				}
			});
			invTable[varRetType.replace(' ', '_')].draw();
			
		} else if(statusResponse.data.status_cd == 'PE') {
			var errorMessage = 'ERROR';
			if(statusResponse.data.error_report) {
				if(statusResponse.data.error_report.error_msg) {
					errorMessage = statusResponse.data.error_report.error_msg;
				}
			}
			invTable[varRetType.replace(' ', '_')].rows().every(function () {
				var row = this.data();
				var index = $(row[0]).attr('index');
				if(selArray[varRetType.replace(" ", "_")].length == 0 || $.inArray(index, selArray[varRetType.replace(" ", "_")]) >= 0) {
					row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+errorMessage+"'></i>";
					this.data(row);
					this.invalidate();
				}
			});
			
			if(statusResponse.data.error_report) {
				
				if(statusResponse.data.error_report.b2b) {
					updateRowStatus(statusResponse.data.error_report.b2b, 'B2B');
				} else if(statusResponse.data.error_report.b2cl) {
					updateRowStatus(statusResponse.data.error_report.b2cl, 'B2CL');
				} else if(statusResponse.data.error_report.b2cs) {
					updateRowStatus(statusResponse.data.error_report.b2cs, 'B2C');
				} else if(statusResponse.data.error_report.exp) {
					updateRowStatus(statusResponse.data.error_report.exp, 'Exports');
				} else if(statusResponse.data.error_report.cdnr) {
					updateRowStatus(statusResponse.data.error_report.cdnr, 'Credit/Debit Notes');
				} else if(statusResponse.data.error_report.txpd) {
					updateRowStatus(statusResponse.data.error_report.txpd, 'Advance Adjusted Detail');
				} else if(statusResponse.data.error_report.at) {
					updateRowStatus(statusResponse.data.error_report.at, 'Advances');
				} else if(statusResponse.data.error_report.cdnur) {
					updateRowStatus(statusResponse.data.error_report.cdnur, 'Credit/Debit Note for Unregistered Taxpayers');
				} else if(statusResponse.data.error_report.nil) {
					updateRowStatus(statusResponse.data.error_report.nil, 'Nil Supplies');
				} 
			}
			invTable[varRetType.replace(' ', '_')].draw();
		} else {
			var errorMessage = 'ERROR';
			if(statusResponse.data.error_report) {
				if(statusResponse.data.error_report.error_msg) {
					errorMessage = statusResponse.data.error_report.error_msg;
				}
			}
			invTable[varRetType.replace(' ', '_')].rows().every(function () {
				var row = this.data();
				var index = $(row[0]).attr('index');
				if(selArray[varRetType.replace(" ", "_")].length == 0 || $.inArray(index, selArray[varRetType.replace(" ", "_")]) >= 0) {
					row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+errorMessage+"'></i>";
					this.data(row);
					this.invalidate();
				}
			});
			invTable[varRetType.replace(' ', '_')].draw();
		}
	} else if(statusResponse.error && statusResponse.error.message) {
		invTable[varRetType.replace(' ', '_')].rows().every(function () {
			var row = this.data();
			var index = $(row[0]).attr('index');
			if(selArray[varRetType.replace(" ", "_")].length == 0 || $.inArray(index, selArray[varRetType.replace(" ", "_")]) >= 0) {
				row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+statusResponse.error.message+"' ></i>";
				this.data(row);
				this.invalidate();
			}
		});
		invTable[varRetType.replace(' ', '_')].draw();
	} else {
		var errorMessage = null;
		if(statusResponse.data.error_report) {
			if(statusResponse.data.error_report.error_msg) {
				errorMessage = statusResponse.data.error_report.error_msg;
			}
		}
		if(errorMessage == null && statusResponse.status_cd == '0') {
			errorMessage = statusResponse.status_desc;
		} else if(errorMessage == null) {
			errorMessage = 'ERROR';
		}
		invTable[varRetType.replace(' ', '_')].rows().every(function () {
			var row = this.data();
			var index = $(row[0]).attr('index');
			if(selArray[varRetType.replace(" ", "_")].length == 0 || $.inArray(index, selArray[varRetType.replace(" ", "_")]) >= 0) {
				row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+errorMessage+"'></i>";
				this.data(row);
				this.invalidate();
			}
		});
		invTable[varRetType.replace(' ', '_')].draw();
	}*/
}
function updateRowStatus(record, type) {
	if(record instanceof Array) {
		record.forEach(function(inv) {
			if(inv.error_msg) {
				if(inv.inv) {
					inv.inv.forEach(function(eInv) {
						invTable[varRetType.replace(' ', '_')].rows().every(function () {
							var row = this.data();
							if(row[2].indexOf(type) !== -1
								&& (eInv.inum == undefined || row[3].indexOf(eInv.inum) !== -1)
								&& (inv.ctin == undefined || row[5].indexOf(inv.ctin) !== -1)) {
								row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+inv.error_msg+"'></i>";
								this.data(row);
								this.invalidate();
								
							}
						});
					});
				} else if(inv.nt) {
					inv.nt.forEach(function(eInv) {
						invTable[varRetType.replace(' ', '_')].rows().every(function () {
							var row = this.data();
							var ctype = type;
							if(eInv.ntty == 'C'){
								ctype = "Credit Note";
							}else{
								ctype = "Debit Note";
							}
							if(row[2].indexOf(ctype) !== -1 && row[3].indexOf(eInv.nt_num) !== -1
								&& (inv.ctin == undefined || row[5].indexOf(inv.ctin) !== -1)) {
								row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+inv.error_msg+"'></i>";
								this.data(row);
								this.invalidate();
								
							}
						});
					});
				} else {
					invTable[varRetType.replace(' ', '_')].rows().every(function () {
						var invoicetypes = type;
						if(type == 'Advance Adjusted Detail'){
							invoicetypes = "Adv.Adjustments";
						}else if(type == 'Credit/Debit Note for Unregistered Taxpayers'){
							if(inv.ntty == 'C'){
								invoicetypes = 'Credit Note(UR)';
							}else{
								invoicetypes = 'Debit Note(UR)';
							}
						}
						var row = this.data();
						if(row[2].indexOf(invoicetypes) !== -1) {
							row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+inv.error_msg+"'></i>";
							this.data(row);
							this.invalidate();
							
						}
					});
				}
			}
		});
	} else {
		if(record.error_msg) {
			if(record.inv) {
				record.inv.forEach(function(eInv) {
					invTable[varRetType.replace(' ', '_')].rows().every(function () {
						var row = this.data();
						var invoiceTypes = type;
						var invoiceNum = eInv.inum;
						if(type == 'Credit/Debit Notes'){
							invoiceNum = eInv.nt_num;
							if(eInv.ntty == 'C'){
								invoiceTypes = "Credit Note";
							}else{
								invoiceTypes = "Debit Note";
							}
						}else if(type == 'Credit/Debit Note for Unregistered Taxpayers'){
							invoiceNum = eInv.nt_num;
							if(inv.ntty == 'C'){
								invoiceTypes = 'Credit Note(UR)';
							}else{
								invoiceTypes = 'Debit Note(UR)';
							}
						}else if(type == 'Advance Adjusted Detail'){
							invoiceTypes = "Adv.Adjustments";
						}
						if(row[2].indexOf(invoiceTypes) !== -1 
							&& (invoiceNum == undefined || row[3].indexOf(invoiceNum) !== -1)
							&& (inv.ctin == undefined || row[5].indexOf(inv.ctin) !== -1)) {
							row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+inv.error_msg+"'></i>";
							this.data(row);
							this.invalidate();
							
						}
					});
				});
			}
		} else if($.isEmptyObject(record)) {
			invTable[varRetType.replace(' ', '_')].rows().every(function () {
				var row = this.data();
				var index = $(row[0]).attr('index');
				if(selArray[varRetType.replace(" ", "_")].length == 0 || $.inArray(index, selArray[varRetType.replace(" ", "_")]) >= 0) {
					row[1]="<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='Processed with Error'></i>";
					this.data(row);
					this.invalidate();
				}
			});
		}
	}
}
function evcFilingOTP() {
	$.ajax({
		url : contextPath+'/fotpevc'+urlSuffix+'/'+month+'/'+year,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response.status_cd == '0') {
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
				$('#evcOtpModal').modal('show');
			}
			
		}
	});
}
function fileEVC() {
	var otp = $('#evcotp1').val();
	$('#evcOtpModal').modal('hide');
	$.ajax({
		url : contextPath+'/fretevcfile'+urlSuffix+'/'+otp+'/'+month+'/'+year,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(retResponse) {
			if(retResponse.error && retResponse.error.message) {
				errorNotification(retResponse.error.message);
			} else if(retResponse.status_cd == '0') {
				errorNotification(retResponse.status_desc);
			} else if(retResponse.status_cd == '1') {
				successNotification('Return filing successful!');
			} else {
				errorNotification('Unable to file returns!');
			}
		}
	});
}
function trueCopyFiling() {
	$.ajax({
		url : contextPath+'/truecopy'+urlSuffix+'/'+month+'/'+year,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(data) {
			$("form[name='certlistForm']").append('<input type="hidden" name="authstr" value="'+data.authstr+'">');
			$("form[name='certlistForm']").append('<input type="hidden" name="cs" value="'+data.cs+'">');
			$("form[name='certlistForm']").append('<input type="hidden" name="dscpin" value="">');
			
			$('#certlistForm').ajaxSubmit( {
				url: 'https://localhost:8099/certlist',
				dataType: 'json',
				type: 'POST',
				cache: false,
				success: function(certResponse) {
					if(certResponse && certResponse.certlist && certResponse.certlist.length > 0) {
						successNotification('Certificate verification successful!');
						$("form[name='certsignForm']").append('<input type="hidden" name="authstr" value="'+data.authstr+'">');
						$("form[name='certsignForm']").append('<input type="hidden" name="cs" value="'+data.cs+'">');
						$("form[name='certsignForm']").append('<input type="hidden" name="certname" value="'+certResponse.certlist[certResponse.certlist.length-1]+'">');
						$("form[name='certsignForm']").append('<input type="hidden" name="dscpin" value="">');
						$("form[name='certsignForm']").append('<input type="hidden" name="hash" value="'+data.hash256+'">');

						$('#certsignForm').ajaxSubmit({
							dataType : 'json',
							url : 'https://localhost:8099/sign',
							cache : false,
							success: function(signResponse) {
								signResponse.content = data.content;
								successNotification('Signing successful!');
								$.ajax({
									type : "POST",
									url: contextPath+'/retfile'+urlSuffix+'/'+month+'/'+year,
									data : JSON.stringify(signResponse),
									async: false,
									cache: false,
									dataType:"json",
									contentType: 'application/json',
									success : function(retResponse) {
										if(retResponse.error && retResponse.error.message) {
											errorNotification(retResponse.error.message);
										} else if(retResponse.status_cd == '0') {
											errorNotification(retResponse.status_desc);
										} else {
											successNotification('Return filing successful!');
										}
									},
									error: function(e) {
										if(e.responseText) {
											errorNotification(e.responseText);
										}
									}
								});
							}
						});
					} else {
						errorNotification("Please check the status of your DSC software and ePass key");
					}
				},
				error: function(e) {
					errorNotification("Please check the status of your DSC software and ePass key");
				}
			});
		},
		error : function(e) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
	});
}
function showSubmitPopup(usrid) {
	//$('#submitInvModal').modal('show');
	//$('#btnSubmitInv').attr('onclick', "submitReturns()");
	
	$.ajax({
		url : contextPath+'/subscriptionCheck/'+usrid,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			
			if(response.status_cd == '0') {
				errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+contextPath+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');
			}else{
				$('#submitInvModal').modal('show');
				$('#btnSubmitInv').attr('onclick', "submitReturns()");
			}
		},error : function(response){
			
		}
	});
}
function showOffsetPopup() {
	$('#offLiabModal').modal('show');
	$('#btnOffLiabInv').attr('onclick', "invokeOffsetLiab()");
}
function invokeOffsetLiab() {
	$.ajax({
		url: contextPath+'/ihubsaveoffliab'+urlSuffix+'/1?month='+month+'&year='+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response.data && response.data.dbt_id) {
				successNotification(response.data.dbt_id);
			} else if(response.status_cd == '0') {
				errorNotification(response.status_desc);
			}
		},
		error : function(response) {
			if(response.status_cd == '0') {
				errorNotification(response.status_desc);
			} else if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
	});
}
function submitReturns() {
	$.ajax({
		url: contextPath+'/retsubmit'+urlSuffix+'/'+month+'/'+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response.status_cd == '1') {
				$('#idTrueCopyBtn').removeClass('disable');
				$('#idEVCBtn').removeClass('disable');
				successNotification('Submit returns successful!');
			} else if(response.error && response.error.message) {
				errorNotification(response.error.message);
				if(response.error.message == 'You already have Submitted/Filed For Current Return Period') {
					$('#idTrueCopyBtn').removeClass('disable');
					$('#idEVCBtn').removeClass('disable');
				}
			} else if(response.status_cd == '0') {
				if(response.status_desc == 'OTP verification is not yet completed!' || response.status_desc == 'Invalid Session' || response.status_desc == 'API Authorization Failed') {
					errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
				} else if(response.status_desc == 'You already have Submitted/Filed For Current Return Period') {
					$('#idTrueCopyBtn').removeClass('disable');
					$('#idEVCBtn').removeClass('disable');
					errorNotification(response.status_desc);
				} else if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
					if(varUserType == 'suvidha' || varUserType == 'enterprise'){
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
					}else{
						errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+contextPath+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
					}
				}else {
					errorNotification(response.status_desc);
				}
			}
		},
		error : function(response) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
	});
}
function proceedToFile() {
	$.ajax({
		url: contextPath+'/ihubprocfile'+urlSuffix+"?month="+month+"&year="+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			if(response.status_cd == '1') {
				window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+varRetType+'/'+month+'/'+year+'?type=inv';
			} else if(response.status_cd == '0') {
				if(response.status_desc == 'OTP verification is not yet completed!' || response.status_desc == 'Invalid Session' || response.status_desc == 'API Authorization Failed') {
					errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
				} else {
					errorNotification(response.status_desc);
				}
			}
		},
		error : function(response) {
			if(e.responseText) {
				errorNotification(e.responseText);
			}
		}
	});
}
function fetchRetSummary(acceptFlag,returnType) {
	otpExpiryCheck();
	if(otpExpirycheck == 'OTP_VERIFIED'){
		if(acceptFlag == false){
			$('#refreshSummary').addClass('fa-spin');
			window.setTimeout(function(){
				$('#refreshSummary').removeClass("fa-spin");
				}, 4000);
		}
		successNotification('Loading Summary information. Please wait..!');
		$.ajax({
			url: contextPath+"/ihubretsum"+urlSuffix+"?month="+month+"&year="+year,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				closeNotifications();
				if(acceptFlag == false){
					
				}
				if(response.error && response.error.message) {
					errorNotification(response.error.message);
				} else if(response.status_cd == '0') {
					if(response.status_desc == 'OTP verification is not yet completed!'	|| response.status_desc == 'Invalid Session' || response.status_desc == 'API Authorization Failed') {
						errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
					} else {
						errorNotification(response.status_desc);
					}
				} else if(response.data) {
					//$('#idFilingBtn').removeClass('disable');
					//$('#idFilingKYCBtn').removeClass('disable');
					//$('#idReturnSubmitBtn').removeClass('disable');
					$('#idOffLiabBtn').removeClass('disable');
					gstSummary = response;
					dbFilingTable.clear().draw();
					var totalInvoices = 0,totalTaxableAmount = 0,totalAmount = 0,totalTaxAmount = 0;
					if(gstSummary.data.secsum) {
						gstSummary.data.secsum.forEach(function(summary) {
							var taxAmt = 0;
								if(summary.ttligst) {
									taxAmt += parseFloat(summary.ttligst);
								}
								if(summary.ttlcgst) {
									taxAmt += parseFloat(summary.ttlcgst);
								}
								if(summary.ttlsgst) {
									taxAmt += parseFloat(summary.ttlsgst);
								}
								if(summary.ttlcess) {
									taxAmt += parseFloat(summary.ttlcess);
								}
							
							var totalTax = 0;
								
								if(summary.ttltax) {
									totalTax = parseFloat(summary.ttltax);
								}
							
							var totalVal = 0;
							
								if(summary.ttlval) {
									totalVal = parseFloat(summary.ttlval);
								}
								totalInvoices += summary.ttldoc;
								totalTaxableAmount += totalTax;
								totalAmount += totalVal;
								totalTaxAmount += taxAmt;
								var order = 1;
								dbFilingTable.row.add([summary.secnm, order, summary.ttldoc, '<span class="ind_formats">'+formatNumber(parseFloat(totalTax).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(totalVal).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(taxAmt).toFixed(2))+'</span>']);
						});
					}
					if(gstSummary.data.sec_sum) {
						gstSummary.data.sec_sum.forEach(function(summary) {var taxAmt = 0;
						if(summary.sec_nm == 'NIL') {
							taxAmt = 0;
						}else{
							if(summary.ttl_igst) {
								taxAmt += parseFloat(summary.ttl_igst);
							}
							if(summary.ttl_cgst) {
								taxAmt += parseFloat(summary.ttl_cgst);
							}
							if(summary.ttl_sgst) {
								taxAmt += parseFloat(summary.ttl_sgst);
							}
							if(summary.ttl_cess) {
								taxAmt += parseFloat(summary.ttl_cess);
							}
						}
							var totalTax = 0;
						if(summary.sec_nm == 'NIL') {
							if(summary.ttl_expt_amt) {
								totalTax += parseFloat(summary.ttl_expt_amt);
							}
							if(summary.ttl_ngsup_amt) {
								totalTax += parseFloat(summary.ttl_ngsup_amt);
							}
							if(summary.ttl_nilsup_amt) {
								totalTax += parseFloat(summary.ttl_nilsup_amt);
							}
						}else{	
							if(summary.ttl_tax) {
								totalTax = parseFloat(summary.ttl_tax);
							}
						}
							var totalVal = 0;
						if(summary.sec_nm == 'NIL') {
							if(summary.ttl_expt_amt) {
								totalVal += parseFloat(summary.ttl_expt_amt);
							}
							if(summary.ttl_ngsup_amt) {
								totalVal += parseFloat(summary.ttl_ngsup_amt);
							}
							if(summary.ttl_nilsup_amt) {
								totalVal += parseFloat(summary.ttl_nilsup_amt);
							}
						}else{	
							if(summary.ttl_val) {
								totalVal = parseFloat(summary.ttl_val);
							}
							}
							if(summary.sec_nm != null) {
								summary.sec_nm = summary.sec_nm.toUpperCase();
							}
							var order = 1;
							if(returnType == 'GSTR1'){
								if(summary.sec_nm == 'AT') {
									summary.sec_nm = '11A(1), 11A(2) - Tax Liability (Advances Received)';
									order = 8;
								} else if(summary.sec_nm == 'B2B') {
									summary.sec_nm = '4A, 4B, 4C, 6B, 6C - B2B Invoices';
									order = 1;
								} else if(summary.sec_nm == 'B2CL') {
									summary.sec_nm = '5A, 5B - B2C (Large) Invoices';
									order = 2;
								} else if(summary.sec_nm == 'B2CS') {
									summary.sec_nm = '7 - B2C (Others)';
									order = 6;
								} else if(summary.sec_nm == 'CDNR') {
									summary.sec_nm = '9B - Credit / Debit Notes (Registered)';
									order = 3;
								} else if(summary.sec_nm == 'TXPD') {
									summary.sec_nm = '11B(1), 11B(2) - Adjustment of Advances';
									order = 9;
								} else if(summary.sec_nm == 'NIL') {
									summary.sec_nm = '8 - Nil rated, exempted and non GST outward supplies';
									order = 7;
								} else if(summary.sec_nm == 'EXP') {
									summary.sec_nm = '6A - Exports Invoices';
									order = 5;
								} else if(summary.sec_nm == 'CDNUR') {
									summary.sec_nm = '9B - Credit / Debit Notes (Unregistered)';
									order = 4;
								} else if(summary.sec_nm == 'ATA') {
									summary.sec_nm = '11A - Amended Tax Liability (Advance Received)';
									order = 16;
								} else if(summary.sec_nm == 'B2BA') {
									summary.sec_nm = '9A - Amended B2B Invoices';
									order = 10;
								} else if(summary.sec_nm == 'B2CLA') {
									summary.sec_nm = '9A - Amended B2C (Large) Invoices';
									order = 11;
								} else if(summary.sec_nm == 'B2CSA') {
									summary.sec_nm = '10 - Amended B2C(Others)';
									order = 15;
								} else if(summary.sec_nm == 'CDNRA') {
									summary.sec_nm = '9C - Amended Credit/Debit Notes (Registered)';
									order = 12;
								} else if(summary.sec_nm == 'TXPDA') {
									summary.sec_nm = '11B - Amendment of Adjustment of Advances';
									order = 17;
								} else if(summary.sec_nm == 'CDNURA') {
									summary.sec_nm = '9C - Amended Credit/Debit Notes (Unregistered)';
									order = 13;
								} else if(summary.sec_nm == 'EXPA') {
									summary.sec_nm = '9A - Amended Exports Invoices';
									order = 14;
								}
							}else{
								if(summary.sec_nm == 'AT') {
									summary.sec_nm = 'Advance Tax'
								} else if(summary.sec_nm == 'B2B') {
									summary.sec_nm = 'B2B Invoices'
								} else if(summary.sec_nm == 'B2CL') {
									summary.sec_nm = 'B2C Large Invoices'
								} else if(summary.sec_nm == 'B2CS') {
									summary.sec_nm = 'B2C Small Invoices'
								} else if(summary.sec_nm == 'CDNR') {
									summary.sec_nm = 'Credit and Debit Note'
								} else if(summary.sec_nm == 'TXPD') {
									summary.sec_nm = 'Advance Adjusted Detail'
								} else if(summary.sec_nm == 'NIL') {
									summary.sec_nm = 'Nil Supplies'
								} else if(summary.sec_nm == 'EXP') {
									summary.sec_nm = 'Exports'
								} else if(summary.sec_nm == 'CDNUR') {
									summary.sec_nm = 'Credit and Debit Note for Unregistered Taxpayers'
								} else if(summary.sec_nm == 'ATA') {
									summary.sec_nm = 'Advance Tax Amendment'
								} else if(summary.sec_nm == 'B2BA') {
									summary.sec_nm = 'B2B Amendment'
								} else if(summary.sec_nm == 'B2CLA') {
									summary.sec_nm = 'B2C Large  Amendment'
								} else if(summary.sec_nm == 'B2CSA') {
									summary.sec_nm = 'B2C Small  Amendment'
								} else if(summary.sec_nm == 'CDNRA') {
									summary.sec_nm = 'Credit and Debit Note Amendment'
								} else if(summary.sec_nm == 'TXPDA') {
									summary.sec_nm = 'Advance Adjusted Detail Amendment'
								} else if(summary.sec_nm == 'CDNURA') {
									summary.sec_nm = 'Credit and Debit Note for Unregistered Amendment'
								} else if(summary.sec_nm == 'EXPA') {
									summary.sec_nm = 'Exports Amendment';
								}
							}
							
							if(summary.sec_nm != 'HSN' && summary.sec_nm != 'DOC_ISSUE') {
								totalInvoices += summary.ttl_rec;
								totalTaxableAmount += totalTax;
								totalAmount += totalVal;
								totalTaxAmount += taxAmt;
								dbFilingTable.row.add([summary.sec_nm, order, summary.ttl_rec, '<span class="ind_formats">'+formatNumber(parseFloat(totalTax).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(totalVal).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(taxAmt).toFixed(2))+'</span>']);
							}
						});
					}
					if(gstSummary.data.section_summary) {
						gstSummary.data.section_summary.forEach(function(summary) {
							var taxAmt = 0;
							if(summary.ttl_txpd_igst) {
								taxAmt += parseFloat(summary.ttl_txpd_igst);
							}
							if(summary.ttl_txpd_cgst) {
								taxAmt += parseFloat(summary.ttl_txpd_cgst);
							}
							if(summary.ttl_txpd_sgst) {
								taxAmt += parseFloat(summary.ttl_txpd_sgst);
							}
							if(summary.ttl_txpd_cess) {
								taxAmt += parseFloat(summary.ttl_txpd_cess);
							}
							var totalTax = 0;
							if(summary.ttl_tax) {
								totalTax = parseFloat(summary.ttl_tax);
							}
							var totalVal = 0;
							if(summary.ttl_val) {
								totalVal = parseFloat(summary.ttl_val);
							}
							var order = 1;
							if(returnType == 'GSTR1'){
								if(summary.sec_nm == 'AT') {
									summary.sec_nm = '11A(1), 11A(2) - Tax Liability (Advances Received)';
									order = 8;
								} else if(summary.sec_nm == 'B2B') {
									summary.sec_nm = '4A, 4B, 4C, 6B, 6C - B2B Invoices';
									order = 1;
								} else if(summary.sec_nm == 'B2CL') {
									summary.sec_nm = '5A, 5B - B2C (Large) Invoices';
									order = 2;
								} else if(summary.sec_nm == 'B2CS') {
									summary.sec_nm = '7 - B2C (Others)';
									order = 6;
								} else if(summary.sec_nm == 'CDNR') {
									summary.sec_nm = '9B - Credit / Debit Notes (Registered)';
									order = 3;
								} else if(summary.sec_nm == 'TXPD') {
									summary.sec_nm = '11B(1), 11B(2) - Adjustment of Advances';
									order = 9;
								} else if(summary.sec_nm == 'NIL') {
									summary.sec_nm = '8 - Nil rated, exempted and non GST outward supplies';
									order = 7;
								} else if(summary.sec_nm == 'EXP') {
									summary.sec_nm = '6A - Exports Invoices';
									order = 5;
								} else if(summary.sec_nm == 'CDNUR') {
									summary.sec_nm = '9B - Credit / Debit Notes (Unregistered)';
									order = 4;
								} else if(summary.sec_nm == 'ATA') {
									summary.sec_nm = '11A - Amended Tax Liability (Advance Received)';
									order = 16;
								} else if(summary.sec_nm == 'B2BA') {
									summary.sec_nm = '9A - Amended B2B Invoices';
									order = 10;
								} else if(summary.sec_nm == 'B2CLA') {
									summary.sec_nm = '9A - Amended B2C (Large) Invoices';
									order = 11;
								} else if(summary.sec_nm == 'B2CSA') {
									summary.sec_nm = '10 - Amended B2C(Others)';
									order = 15;
								} else if(summary.sec_nm == 'CDNRA') {
									summary.sec_nm = '9C - Amended Credit/Debit Notes (Registered)';
									order = 12;
								} else if(summary.sec_nm == 'TXPDA') {
									summary.sec_nm = '11B - Amendment of Adjustment of Advances';
									order = 17;
								} else if(summary.sec_nm == 'CDNURA') {
									summary.sec_nm = '9C - Amended Credit/Debit Notes (Unregistered)';
									order = 13;
								} else if(summary.sec_nm == 'EXPA') {
									summary.sec_nm = '9A - Amended Exports Invoices';
									order = 14;
								}
							}else{
								if(summary.sec_nm == 'AT') {
									summary.sec_nm = 'Advance Tax'
								} else if(summary.sec_nm == 'B2B') {
									summary.sec_nm = 'B2B Invoices'
								} else if(summary.sec_nm == 'B2CL') {
									summary.sec_nm = 'B2C Large Invoices'
								} else if(summary.sec_nm == 'B2CS') {
									summary.sec_nm = 'B2C Small Invoices'
								} else if(summary.sec_nm == 'CDNR') {
									summary.sec_nm = 'Credit and Debit Note'
								} else if(summary.sec_nm == 'TXPD') {
									summary.sec_nm = 'Advance Adjusted Detail'
								} else if(summary.sec_nm == 'NIL') {
									summary.sec_nm = 'Nil Supplies'
								} else if(summary.sec_nm == 'EXP') {
									summary.sec_nm = 'Exports'
								} else if(summary.sec_nm == 'CDNUR') {
									summary.sec_nm = 'Credit and Debit Note for Unregistered Taxpayers'
								} else if(summary.sec_nm == 'ATA') {
									summary.sec_nm = 'Advance Tax Amendment'
								} else if(summary.sec_nm == 'B2BA') {
									summary.sec_nm = 'B2B Amendment'
								} else if(summary.sec_nm == 'B2CLA') {
									summary.sec_nm = 'B2C Large  Amendment'
								} else if(summary.sec_nm == 'B2CSA') {
									summary.sec_nm = 'B2C Small  Amendment'
								} else if(summary.sec_nm == 'CDNRA') {
									summary.sec_nm = 'Credit and Debit Note Amendment'
								} else if(summary.sec_nm == 'TXPDA') {
									summary.sec_nm = 'Advance Adjusted Detail Amendment'
								} else if(summary.sec_nm == 'CDNURA') {
									summary.sec_nm = 'Credit and Debit Note for Unregistered Amendment'
								} else if(summary.sec_nm == 'EXPA') {
									summary.sec_nm = 'Exports Amendment';
								}
							}
							if(summary.sec_nm != 'HSN' && summary.sec_nm != 'DOC_ISSUE') {
								totalInvoices += summary.rc;
								totalTaxableAmount += totalTax;
								totalAmount += totalVal;
								totalTaxAmount += taxAmt;
								dbFilingTable.row.add([summary.sec_nm, order, summary.rc, '<span class="ind_formats">'+formatNumber(parseFloat(totalTax).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(totalVal).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(taxAmt).toFixed(2))+'</span>']);
							}
						});
					}
					if(gstSummary.data.itcDetails) {
						if(gstSummary.data.itcDetails.totalItc) {
							var taxAmt = 0;
							if(gstSummary.data.itcDetails.totalItc.iamt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.totalItc.iamt);
							}
							if(gstSummary.data.itcDetails.totalItc.camt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.totalItc.camt);
							}
							if(gstSummary.data.itcDetails.totalItc.samt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.totalItc.samt);
							}
							if(gstSummary.data.itcDetails.totalItc.csamt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.totalItc.csamt);
							}
							dbFilingTable.row.add(['ITC Total',1, '-', '-', '-', '<span class="ind_formats">'+formatNumber(parseFloat(taxAmt).toFixed(2))+'</span>']);
						}
						if(gstSummary.data.itcDetails.elgitc) {
							var taxAmt = 0;
							if(gstSummary.data.itcDetails.elgitc.iamt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.elgitc.iamt);
							}
							if(gstSummary.data.itcDetails.elgitc.camt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.elgitc.camt);
							}
							if(gstSummary.data.itcDetails.elgitc.samt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.elgitc.samt);
							}
							if(gstSummary.data.itcDetails.elgitc.csamt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.elgitc.csamt);
							}
							dbFilingTable.row.add(['ITC Eligible',1, '-', '-', '-', '<span class="ind_formats">'+formatNumber(parseFloat(taxAmt).toFixed(2))+'</span>']);
						}
						if(gstSummary.data.itcDetails.inelgitc) {
							var taxAmt = 0;
							if(gstSummary.data.itcDetails.inelgitc.iamt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.inelgitc.iamt);
							}
							if(gstSummary.data.itcDetails.inelgitc.camt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.inelgitc.camt);
							}
							if(gstSummary.data.itcDetails.inelgitc.samt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.inelgitc.samt);
							}
							if(gstSummary.data.itcDetails.inelgitc.csamt) {
								taxAmt += parseFloat(gstSummary.data.itcDetails.inelgitc.csamt);
							}
							dbFilingTable.row.add(['ITC Ineligible',1, '-', '-', '-', '<span class="ind_formats">'+formatNumber(parseFloat(taxAmt).toFixed(2))+'</span>']);
						}
					}
					
					$('#totalInvoices').html(totalInvoices);
					$('#totalTaxableAmt').html(formatNumber(totalTaxableAmount.toFixed(2)));
					$('#totalAmt').html(formatNumber(totalAmount.toFixed(2)));
					$('#totalTaxAmt').html(formatNumber(totalTaxAmount.toFixed(2)));
					dbFilingTable.draw();
					offLiabTable.clear().draw();
					if(gstSummary.data.tax_py_pd) {
						if(gstSummary.data.tax_py_pd.tax_pay) {
							gstSummary.data.tax_py_pd.tax_pay.forEach(function(summary) {
								offLiabTable.row.add([
									'Tax Payable',
									summary.liab_id, 
									summary.trandate, 
									'IGST', 
									summary.igst.tx,
									summary.igst.intr,
									summary.igst.pen,
									summary.igst.fee,
									summary.igst.oth,
									summary.igst.tot]);
								offLiabTable.row.add([
									'Tax Payable',
									summary.liab_id, 
									summary.trandate, 
									'CGST', 
									summary.cgst.tx,
									summary.cgst.intr,
									summary.cgst.pen,
									summary.cgst.fee,
									summary.cgst.oth,
									summary.cgst.tot]);
								offLiabTable.row.add([
									'Tax Payable',
									summary.liab_id, 
									summary.trandate, 
									'SGST', 
									summary.sgst.tx,
									summary.sgst.intr,
									summary.sgst.pen,
									summary.sgst.fee,
									summary.sgst.oth,
									summary.sgst.tot]);
								offLiabTable.row.add([
									'Tax Payable',
									summary.liab_id, 
									summary.trandate, 
									'CESS', 
									summary.cess.tx,
									summary.cess.intr,
									summary.cess.pen,
									summary.cess.fee,
									summary.cess.oth,
									summary.cess.tot]);
							});
						}
						if(gstSummary.data.tax_py_pd.pd_by_cash) {
							gstSummary.data.tax_py_pd.pd_by_cash.forEach(function(summary) {
								offLiabTable.row.add([
									'Paid By Cash',
									summary.liab_id, 
									summary.trandate, 
									'IGST', 
									summary.igst.tx,
									summary.igst.intr,
									summary.igst.pen,
									summary.igst.fee,
									summary.igst.oth,
									summary.igst.tot]);
								offLiabTable.row.add([
									'Paid By Cash',
									summary.liab_id, 
									summary.trandate, 
									'CGST', 
									summary.cgst.tx,
									summary.cgst.intr,
									summary.cgst.pen,
									summary.cgst.fee,
									summary.cgst.oth,
									summary.cgst.tot]);
								offLiabTable.row.add([
									'Paid By Cash',
									summary.liab_id, 
									summary.trandate, 
									'SGST', 
									summary.sgst.tx,
									summary.sgst.intr,
									summary.sgst.pen,
									summary.sgst.fee,
									summary.sgst.oth,
									summary.sgst.tot]);
								offLiabTable.row.add([
									'Paid By Cash',
									summary.liab_id, 
									summary.trandate, 
									'CESS', 
									summary.cess.tx,
									summary.cess.intr,
									summary.cess.pen,
									summary.cess.fee,
									summary.cess.oth,
									summary.cess.tot]);
							});
						}
					}
					if(gstSummary.data.utilizeCashReqVO) {
						if(gstSummary.data.utilizeCashReqVO.utilizeCashReqVO) {
							gstSummary.data.utilizeCashReqVO.utilizeCashReqVO.forEach(function(summary) {
								offLiabTable.row.add([
									'Op Liability',
									summary.liab_id, 
									'-', 
									'IGST', 
									summary.igst.tx,
									summary.igst.intr,
									summary.igst.pen,
									summary.igst.fee,
									summary.igst.oth,
									summary.igst.tot]);
								offLiabTable.row.add([
									'Op Liability',
									summary.liab_id, 
									'-', 
									'CGST', 
									summary.cgst.tx,
									summary.cgst.intr,
									summary.cgst.pen,
									summary.cgst.fee,
									summary.cgst.oth,
									summary.cgst.tot]);
								offLiabTable.row.add([
									'Op Liability',
									summary.liab_id, 
									'-', 
									'SGST', 
									summary.sgst.tx,
									summary.sgst.intr,
									summary.sgst.pen,
									summary.sgst.fee,
									summary.sgst.oth,
									summary.sgst.tot]);
								offLiabTable.row.add([
									'Op Liability',
									summary.liab_id, 
									'-', 
									'CESS', 
									summary.cess.tx,
									summary.cess.intr,
									summary.cess.pen,
									summary.cess.fee,
									summary.cess.oth,
									summary.cess.tot]);
							});
						}
					}
					if(gstSummary.data.liabDetl) {
						if(gstSummary.data.liabDetl.rev) {
							var summary = gstSummary.data.liabDetl.rev;
							offLiabTable.row.add([
								'Rev Liability',
								summary.tran_cd, 
								'-', 
								'IGST', 
								summary.igst.tx,
								summary.igst.intr,
								summary.igst.pen,
								summary.igst.fee,
								summary.igst.oth,
								summary.igst.tot]);
							offLiabTable.row.add([
								'Rev Liability',
								summary.tran_cd, 
								'-', 
								'CGST', 
								summary.cgst.tx,
								summary.cgst.intr,
								summary.cgst.pen,
								summary.cgst.fee,
								summary.cgst.oth,
								summary.cgst.tot]);
							offLiabTable.row.add([
								'Rev Liability',
								summary.tran_cd, 
								'-', 
								'SGST', 
								summary.sgst.tx,
								summary.sgst.intr,
								summary.sgst.pen,
								summary.sgst.fee,
								summary.sgst.oth,
								summary.sgst.tot]);
							offLiabTable.row.add([
								'Rev Liability',
								summary.tran_cd, 
								'-', 
								'CESS', 
								summary.cess.tx,
								summary.cess.intr,
								summary.cess.pen,
								summary.cess.fee,
								summary.cess.oth,
								summary.cess.tot]);
						}
						if(gstSummary.data.liabDetl.nonRev) {
							var summary = gstSummary.data.liabDetl.nonRev;
							offLiabTable.row.add([
								'Non-Rev Liability',
								summary.tran_cd, 
								'-', 
								'IGST', 
								summary.igst.tx,
								summary.igst.intr,
								summary.igst.pen,
								summary.igst.fee,
								summary.igst.oth,
								summary.igst.tot]);
							offLiabTable.row.add([
								'Non-Rev Liability',
								summary.tran_cd, 
								'-', 
								'CGST', 
								summary.cgst.tx,
								summary.cgst.intr,
								summary.cgst.pen,
								summary.cgst.fee,
								summary.cgst.oth,
								summary.cgst.tot]);
							offLiabTable.row.add([
								'Non-Rev Liability',
								summary.tran_cd, 
								'-', 
								'SGST', 
								summary.sgst.tx,
								summary.sgst.intr,
								summary.sgst.pen,
								summary.sgst.fee,
								summary.sgst.oth,
								summary.sgst.tot]);
							offLiabTable.row.add([
								'Non-Rev Liability',
								summary.tran_cd, 
								'-', 
								'CESS', 
								summary.cess.tx,
								summary.cess.intr,
								summary.cess.pen,
								summary.cess.fee,
								summary.cess.oth,
								summary.cess.tot]);
						}
					}
					offLiabTable.draw();
				}
				$('#refreshSummary').removeClass('fa-spin');
			},
			error : function(e, status, error) {
				$('#refreshSummary').removeClass('fa-spin');
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
		$.ajax({
			url: contextPath+"/ihubothsum"+urlSuffix+"?month="+month+"&year="+year,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if(response.data && response.data.hsn && response.data.hsn.data) {
					dbHSNFilingTable.clear();
					dbDocIssueFilingTable.clear();
					var hsnTotalTaxableAmount = 0,hsnTotalIgstAmount = 0,hsnTotalCgstAmount = 0,hsnTotalSgstAmount = 0,hsnTotalCessAmount = 0;
					response.data.hsn.data.forEach(function(hsn) {
						if(hsn.desc == undefined || hsn.desc == null) {
							hsn.desc = '';
						}
						var hsntaxrateval = 0.0;
						if((month > 4 && year > 2020) || (month < 4 && year > 2021)){
							hsntaxrateval =hsn.rt ? hsn.rt : 0.0;
						}else{
							hsntaxrateval =hsn.val ? hsn.val : 0.0;
						}
						
						dbHSNFilingTable.row.add([hsn.num, hsn.hsn_sc, hsn.desc, hsn.qty,
							'<span class="ind_formats">'+formatNumber(parseFloat(hsntaxrateval).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(hsn.txval).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(hsn.iamt).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(hsn.camt).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(hsn.samt).toFixed(2))+'</span>', '<span class="ind_formats">'+formatNumber(parseFloat(hsn.csamt).toFixed(2))+'</span>']);
						hsnTotalTaxableAmount += hsn.txval;
						hsnTotalIgstAmount += hsn.iamt;
						hsnTotalCgstAmount += hsn.camt;
						hsnTotalSgstAmount += hsn.samt;
						hsnTotalCessAmount += hsn.csamt;						
							
					});
					if(response.data.doc_issue) {
						response.data.doc_issue.doc_det.forEach(function(doc_det) {
							if(doc_det.docs) {
								doc_det.docs.forEach(function(doc) {
									dbDocIssueFilingTable.row.add([doc_det.doc_num, doc.from, doc.to, doc.totnum, doc.cancel, doc.net_issue]);
								});
							}
						});
					}
					$('#hsnTotalTaxableAmount').html(formatNumber(hsnTotalTaxableAmount.toFixed(2)));
					$('#hsnTotalIgstAmount').html(formatNumber(hsnTotalIgstAmount.toFixed(2)));
					$('#hsnTotalCgstAmount').html(formatNumber(hsnTotalCgstAmount.toFixed(2)));
					$('#hsnTotalSgstAmount').html(formatNumber(hsnTotalSgstAmount.toFixed(2)));
					$('#hsnTotalCessAmount').html(formatNumber(hsnTotalCessAmount.toFixed(2)));
					dbHSNFilingTable.draw();
					dbDocIssueFilingTable.draw();
				}
			},
			error : function(e, status, error) {
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}else{
		errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
	}
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
	function updateMisMatchSelection(id, gstrId, gstin, chkBox) {
		var mObj=new Object();
		mObj.purchaseId = id;
		mObj.gstrId = gstrId;
		if(chkBox.checked) {
			selMatchArray.push(mObj);
			sendMsgArray.push(id);
			sendMsgCount++;
			if(gstMatchArray.length == 0){
				gstinnomatch = gstin;
				gstMatchArray.push(gstin);
				$('.sendmessage').removeClass('disable');
				$('.sendmessage').attr('onClick','sendSuppliermessage(\'GSTR2\')');
			}else{
				if(jQuery.inArray(gstin, gstMatchArray ) == -1){
					gstnNotSelArray.push(gstin);
					$('.sendmessage').addClass('disable');
				}
			}
		} else {
			sendMsgCount--;
			var tArray=new Array();
			selMatchArray.forEach(function(inv) {
				if(inv.purchaseId == id && inv.gstrId == gstrId) {
				} else {
					tArray.push(inv);
				}
			});
			selMatchArray = tArray;
			
			var mArray=new Array();
			sendMsgArray.forEach(function(inv) {
				if(inv != id) {
					mArray.push(inv);
				}
			});
			sendMsgArray = mArray;
			if(jQuery.inArray(gstin, gstMatchArray ) == -1){
				gstnNotSelArray.splice(gstnNotSelArray.indexOf(gstin), 1);
				if(gstnNotSelArray.length > 1){
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
		if(selMatchArray.length > 0) {
			$('#btnMisMatchAccept').removeClass('disable');
			$('#btnMisMatchReject').removeClass('disable');
			if(selMatchArray.length == 1) {
				$('.mannualMatching').removeClass('disable');
			}else{
				$('.mannualMatching').addClass('disable');
			}
			$('.select_msg').text('You have Selected '+selMatchArray.length+' Invoice(s)');
		} else {
			$('#btnMisMatchAccept').addClass('disable');
			$('#btnMisMatchReject').addClass('disable');
			$('.mannualMatching').addClass('disable');
			$('.select_msg').text('');
			$('#checkMismatch').prop("checked",false);
		}
	}
	function verifyAccess(btn,hsn) {
		var turnOver = false;
		var yr;
		var currentyr;
		if(year == 2017){
			yr=year;
			if(month < 4){
				currentyr='APR2017';
			}else{
				currentyr='2017';
			}
		}else{
			yr = year-1;
			if(month < 4){
				if(year == 2017){
					currentyr='2017';
				}else{
					yr--;
					currentyr=yr;					
				}
			}else{
				currentyr=yr;
			}
		}
		if(turnoveroptionsArray.length == 0){
			turnOver = false;
		}else{
			for(var i=0;i < turnoveroptionsArray.length;i++){
				var turnoveroption = turnoveroptionsArray[i].split("-");
				if(currentyr == turnoveroption[0]){
					turnOver = true;
					break;
				}
			}
		}
		if(turnOver){
			otpExpiryCheck();
			if(otpExpirycheck == "OTP_VERIFIED"){
			$(btn).addClass('btn-loader');
			$(btn).html('Uploading...');
			window.setTimeout(function(){
				$(btn).removeClass("btn-loader");
				if(hsn == "hsn"){
					$(btn).html('Upload To GSTIN');
				}else{
					$(btn).html('Upload HSN Summary');
				}
				}, 2000);
			var myVar = null;
			function myTimer() {
				$(btn).removeClass('btn-loader');
				clearInterval(myVar);
			}
			if(selArray[varRetType.replace(" ", "_")] == undefined){
				uploadInvoiceSet(new Array(), 0, '',hsn);
			}else{
				if(selArray[varRetType.replace(" ", "_")].length > 0) {
					var iCount = 0;
					if(selArray[varRetType.replace(" ", "_")].length > 20) {
						iCount = parseInt(selArray[varRetType.replace(" ", "_")].length/10);
					}
					for(var i=0;i<selArray[varRetType.replace(" ", "_")].length;i++) {
						var tArray=new Array();
						var max = i+iCount;
						if(max > selArray[varRetType.replace(" ", "_")].length) {
							max = selArray[varRetType.replace(" ", "_")].length-1;
						}
						for(var j=i;j<=max;j++) {
							tArray.push(selArray[varRetType.replace(" ", "_")][j]);
							$('#sts'+selArray[varRetType.replace(" ", "_")][j]).html('<span class="color-yellow">In Progress</span>');
						}
						i = max;
						uploadInvoiceSet(tArray, i, selArray[varRetType.replace(" ", "_")].length,hsn);
					}
				} else {
					uploadInvoiceSet(new Array(), 0, '',hsn);
				}
			}
		}else{
			errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
		}
	}else{
		var nxtyr = Number(yr)+1;
		errorNotification(''+yr+' -'+ (nxtyr)+' Financial Year TurnOver is not avaliable! <a type="button" class="btn btn-sm btn-blue-dark" href="'+contextPath+'/about'+commonturnOverSuffix+'/'+month+'/'+year+'">Click Here</a> to add turnover.');
	}
		//myVar = setInterval(myTimer, 3000);
		selArray[varRetType.replace(" ", "_")] = new Array();
	}
	function deleteDocItem(elem) {
		$(elem).parent().parent().remove();
	}
	function uploadDocIssue(btn) {
		$(btn).addClass('btn-loader');
		$(btn).html('Uploading...');
		var pUrl = contextPath+'/ihubsdocissue'+urlSuffix+"?month=" + month + "&year=" + year;
		$.ajax({
			type: "GET",
			url: pUrl,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				$(btn).removeClass('btn-loader');
				$(btn).html('Upload All Docs to GSTN');
				if(response.status_cd == '1') {
					successNotification('Document Issue upload successful!');
				} else {
					if(response.error && response.error.message) {
						errorNotification(response.error.message);
					} else if(response.status_cd == '0') {
						if(response.status_desc == 'OTP verification is not yet completed!' || response.status_desc == 'Invalid Session' || response.status_desc == 'API Authorization Failed') {
							errorNotification('Your OTP Session Expired. Click <a href="#" class="btn btn-sm btn-blue-dark" onclick="invokeOTP(this)">Verify Now</a> to proceed further.');
						} else if(response.status_desc == 'Your subscription has expired. Kindly subscribe to proceed further!') {
							if(varUserType == 'suvidha' || varUserType == 'enterprise'){
								errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a type="button" class="btn btn-sm btn-blue-dark" data-toggle="modal" data-target="#subnowmodal"">Subscribe Now</a> to proceed further.');
							}else{
								errorNotification('Your subscription has expired. Kindly subscribe to proceed further! Click <a href="'+contextPath+'/dbllng'+commonSuffix+'/'+month+'/'+year+'" class="btn btn-sm btn-blue-dark">Subscribe Now</a> to proceed further.');	
							}
						} else {
							errorNotification(response.status_desc);
						}
					}
				}
			},
			error : function(e, status, error) {
				$(btn).removeClass('btn-loader');
				$(btn).html('Upload All Docs to GSTN');
				if(e.responseText) {
					errorNotification(e.responseText);
				}
			}
		});
	}
	function importInvoicesModal(type){
		$('#idSheet').css("display","none");
		if(type == 'Purchase Register'){
			$('#importsTitle').html('Import Purchase Invoices');
			$('.sales_template').hide();
			$('.purchase_template').show();
		}else if(type == 'GSTR1'){
			$('#importsTitle').html('Import Sales Invoices');
			$('.sales_template').show();
			$('.purchase_template').hide();
		}
	}
	function updateInvoiceFilter(value) {
		if(invoiceArray[varRetType.replace(' ', '_')].length > 0) {
			var rowNode;
			invoiceArray[varRetType.replace(' ', '_')].forEach(function(invoice) {
				if((value == 'All') || (invoice.invoicetype == value)) {
					var status = "<span class='color-yellow'>Pending</span>";
					if(invoice.gstStatus != undefined || invoice.gstStatus != null) {
						status = "<span class='color-yellow'>"+invoice.gstStatus+"</span>";
						if(invoice.gstStatus == 'SUCCESS') {
							status = "<span class='color-green'>Uploaded</span>";
						} else if(invoice.gstStatus == 'CANCELLED') {
							status = "<span class='color-red'>Cancelled</span>";
						} else {
							status = "<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+invoice.gstStatus+"'></i>";
						}
					}
					if(varRetType == 'GSTR1' || varRetType == 'GSTR4' || varRetType == 'GSTR5'){
					rowNode = invoiceTable[varRetType.replace(' ', '_')].row.add([ '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" id="invFilter'+invoice.id+'" onclick="updateSelection(\''+invoice.id+'\',\''+varRetType+'\', this)"/><i class="helper"></i></label></div>', status, invoice.invoicetype, invoice.serialnoofinvoice, invoice.billedtoname,
						invoice.billedtogstin, invoice.dateofinvoice, '<span class="ind_formats">'+invoice.totaltaxableamount+'</span>', '<span class="ind_formats">'+invoice.totaltax+'</span>', '<span class="ind_formats">'+invoice.totalamount+'</span>', invoice.fullname,invoice.branch,invoice.vertical,'<a onclick="editInvPopup(null,\''+varRetType+'\',\''+invoice.id+'\')"><i class="fa fa-edit"></i> </a><a href="'+contextPath+'/invprint'+commonSuffix+'/'+clientId+'/'+varRetType+'/'+invoice.id+'" target="_blank"> <img src="'+contextPath+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right"></a>'] );
					}else if(varRetType == 'GSTR2'){
					rowNode = invoiceTable[varRetType.replace(' ', '_')].row.add([ '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" id="invFilter'+invoice.id+'" onclick="updateSelection(\''+invoice.id+'\',\''+varRetType+'\', this)"/><i class="helper"></i></label></div>', status, invoice.invoicetype, invoice.serialnoofinvoice, invoice.billedtoname,
						invoice.billedtogstin, invoice.dateofinvoice, '<span class="ind_formats">'+invoice.totaltaxableamount+'</span>', '<span class="ind_formats">'+invoice.totaltax+'</span>', '<span class="ind_formats">'+invoice.totalamount+'</span>', invoice.fullname,invoice.branch,invoice.vertical,'<span>'+formatNumber(invoice.totalitc.toFixed(2))+'</span><div class="dropdown" style="display:initial; margin-left:5px"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><ul class="dropdown-menu" style="padding: 15px; margin-left: -161px;width: 300px;height: 180px;"><li>Input Type<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/close-icon.png" alt="Close"  style="height: 24px;position: absolute; top: 5px; right: 4%; color: #5769bb;"/></span></li><li><form><select id="'+invoice.id+varRetType.replace(" ", "_")+'itc_droptype" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Input Good</option><option value="is">Ineligible</option><option value="no">Not Selected</option> </select></form></li><li>ITC Amount</li><li><input id="'+invoice.id+varRetType.replace(" ", "_")+'itc_dropamt" type="text" class="form-control itc_amount" value="100" style=" width:  45px;"><span style=" margin-right: 13px;">%</span><button class="btn btn-blue" onclick="updateITCType(\''+varRetType.replace(" ", "_")+'\',\''+invoice.id+'\')" value="ok"> ok </button></li></ul><a class="" href="#"  style="" onclick="updateunclaimdetails(\''+varRetType.replace(" ", "_")+'\',\''+invoice.id+'\')">clear</a><span style="font-size:12px;margin-left:5px;">If we clear this all ITC values will become <b>0</b></span></div>'] );
					}else if(varRetType == 'GSTR6'){
					rowNode = invoiceTable[varRetType.replace(' ', '_')].row.add([ '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" id="invFilter'+invoice.id+'" onclick="updateSelection(\''+invoice.id+'\',\''+varRetType+'\', this)"/><i class="helper"></i></label></div>', status, invoice.invoicetype, invoice.serialnoofinvoice, invoice.billedtoname,
						invoice.billedtogstin, invoice.dateofinvoice, '<span class="ind_formats">'+invoice.totaltaxableamount+'</span>', '<span class="ind_formats">'+invoice.totaltax+'</span>', '<span class="ind_formats">'+invoice.totalamount+'</span>', invoice.fullname,invoice.branch,invoice.vertical,'<span>'+formatNumber(invoice.totalitc.toFixed(2))+'</span><div class="dropdown" style="display:initial; margin-left:5px"><i class="dropdown-toggle itc-avail-drop" type="button" data-toggle="dropdown" style="border: 1px solid #5769bb;padding:  0px;border-radius: 2px;"><span class="caret"></span></i><ul class="dropdown-menu" style="padding: 15px; margin-left: -161px;width: 300px;height: 180px;"><li>Input Type<span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/close-icon.png" alt="Close"  style="height: 24px;position: absolute; top: 5px; right: 4%; color: #5769bb;"/></span></li><li><form><select id="'+invoice.id+varRetType.replace(" ", "_")+'itc_droptype" class="form-control itc_drop"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Input Good</option><option value="is">Ineligible</option><option value="no">Not Selected</option> </select></form></li><li>ITC Amount</li><li><input id="'+invoice.id+varRetType.replace(" ", "_")+'itc_dropamt" type="text" class="form-control itc_amount" value="100" style=" width:  45px;"><span style=" margin-right: 13px;">%</span><button class="btn btn-blue" onclick="updateITCType(\''+varRetType.replace(" ", "_")+'\',\''+invoice.id+'\')" value="ok"> ok </button></li></ul><a class="" href="#"  style="" onclick="updateunclaimdetails(\''+varRetType.replace(" ", "_")+'\',\''+invoice.id+'\')">clear</a><span style="font-size:12px;margin-left:5px;">If we clear this all ITC values will become <b>0</b></span></div>','<a onclick="editInvPopup(null,\''+varRetType+'\',\''+invoice.id+'\')"><i class="fa fa-edit"></i> </a><a href="'+contextPath+'/invprint'+commonSuffix+'/'+clientId+'/'+varRetType+'/'+invoice.id+'" target="_blank"> <img src="'+contextPath+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right"></a>'] );	
					}
					$(rowNode.node()).children().addClass('invoiceclk').attr('onclick',"editInvPopup(null,'"+varRetType+"','"+invoice.id+"')");
					invoiceTable[varRetType.replace(" ", "_")].row(rowNode).column(0).nodes().to$().addClass('text-right').removeAttr('onclick');
					if(varRetType == 'GSTR1' || varRetType == 'GSTR4' || varRetType == 'GSTR5'){
						invoiceTable[varRetType.replace(" ", "_")].row(rowNode).column(13).nodes().to$().addClass('text-right').removeAttr('onclick').removeClass('invoiceclk');
					}else if(varRetType == 'GSTR6'){
						invoiceTable[varRetType.replace(" ", "_")].row(rowNode).column(14).nodes().to$().addClass('text-right').removeAttr('onclick').removeClass('invoiceclk');
					}
				}
			});
		}
	}
	function updateInvoiceSummary(retype,month,year) {
		if(retype == 'PROFORMAINVOICES' || retype == 'DELIVERYCHALLANS' || retype == 'ESTIMATES'){
			retype = 'GSTR1';
		}
		if(retype == 'PurchaseOrder'){
			retype = 'GSTR2';
		}
		var returnType = retype;
		var monthName = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
		if($('#filing_option').text() == " Quarterly "){
			if(month == 3) {
				monthName[month-1]='Jan-Mar';
			} else if(month == 6) {
				monthName[month-1]='Apr-Jun';
			} else if(month == 9) {
				monthName[month-1]='Jul-Sep';
			} else if(month == 12) {
				monthName[month-1]='Oct-Dec';
			}
		}
		if(retype == 'GSTR1' || retype == 'ANX1'){
			retype = 'Sales';
		}else if(retype == 'GSTR2' || retype == 'GSTR6' || retype == 'Purchase Register' || retype == 'Purchase_Register'){
			retype = 'Purchase';
		}
		if($('#invoiceSummary').html() == '') {
			var content = '';
		
			var loopArray=invoiceArray[returnType.replace(" ", "_")];
			if(retype == 'Purchase' || retype == 'Sales'){
			if(loopArray.length > 0) {
				var returnSummary = new Array();
				loopArray.forEach(function(invoice) {
					var present = false;
					returnSummary.forEach(function(rtn) {
						if(rtn.type == invoice.invoicetype) {
							rtn.count++;
							if(invoice.totaltaxableamount) {
								rtn.taxableamount += parseFloat(invoice.totaltaxableamount);
							}
							if(invoice.totaltax) {
								rtn.totaltax += parseFloat(invoice.totaltax);
							}
							if(invoice.totalamount) {
								rtn.totalamount += parseFloat(invoice.totalamount);
							}
							present = true;
						}
					});
					if(!present) {
						var rtn=new Object();
						rtn.type=invoice.invoicetype;
						rtn.count=1;
						if(invoice.totaltaxableamount) {
							rtn.taxableamount = parseFloat(invoice.totaltaxableamount);
						} else {
							rtn.taxableamount = 0;
						}
						if(invoice.totaltax) {
							rtn.totaltax = parseFloat(invoice.totaltax);
						} else {
							rtn.totaltax = 0;
						}
						if(invoice.totalamount) {
							rtn.totalamount = parseFloat(invoice.totalamount);
						} else {
							rtn.totalamount = 0;
						}
						returnSummary.push(rtn);
					}
				});
				if(returnSummary.length > 0) {
					returnSummary.forEach(function(rtn) {
						var invoiceType=rtn.type;
						var order = 1;
						    if(returnType == 'GSTR1'){
						    	if(invoiceType == 'B2B') {
									invoiceType="4A, 4B, 4C, 6B, 6C - B2B Invoices";
									order = 1;
								} else if(invoiceType == 'B2C') {
									invoiceType="7 - B2C (Others)";
									order = 6;
								} else if(invoiceType == 'B2CL') {
									invoiceType="5A, 5B - B2C (Large) Invoices";
									order = 2;
								} else if(invoiceType == 'Credit/Debit Notes'){
									invoiceType="9B - Credit / Debit Notes (Registered)";
									order = 3;
								} else if(invoiceType == 'Credit/Debit Note for Unregistered Taxpayers'){
									invoiceType="9B - Credit / Debit Notes (Unregistered)";
									order = 4;
								} else if(invoiceType == 'Exports'){
									invoiceType="6A - Exports Invoices";
									order = 5;
								} else if(invoiceType == 'Nil Supplies'){
									invoiceType = "8 - Nil rated, exempted and non GST outward supplies";
									order = 7;
								} else if(invoiceType == 'Advances'){
									invoiceType = "11A(1), 11A(2) - Tax Liability (Advances Received)";
									order = 8;
								} else if(invoiceType == 'Advance Adjusted Detail'){
									invoiceType = "11B(1), 11B(2) - Adjustment of Advances";
									order = 9;
								} else if(invoiceType == "B2BA"){
									invoiceType = "9A - Amended B2B Invoices";
									order = 10;
								} else if(invoiceType == 'B2CSA'){
									invoiceType = "10 - Amended B2C(Others)";
									order = 15;
								} else if(invoiceType == 'B2CLA'){
									invoiceType = "9A - Amended B2C (Large) Invoices";
									order = 11;
								} else if(invoiceType == 'CDNURA'){
									invoiceType = "9C - Amended Credit/Debit Notes (Unregistered)";
									order = 13;
								} else if(invoiceType == 'CDNA'){
									invoiceType = "9C - Amended Credit/Debit Notes (Registered)";
									order = 12;
								} else if(invoiceType == 'EXPA'){
									invoiceType = "9A - Amended Exports Invoices";
									order = 14;
								}else if(invoiceType == 'TXPA'){
									invoiceType = "11A - Amended Tax Liability (Advance Received)";
									order = 16;
								} else if(invoiceType == 'ATA'){
									invoiceType = "11B - Amendment of Adjustment of Advances";
									order = 17;
								}
						    }else{
								if(invoiceType == 'B2B') {
									invoiceType=invoiceType+" (Business to Business)";
								} else if(invoiceType == 'B2C') {
									invoiceType=invoiceType+" (Business to Consumers Small)";
								} else if(invoiceType == 'B2CL') {
									invoiceType=invoiceType+" (Business to Consumers Large)";
								}
						    }
						
						/*content+='<tr><td align="left">'+invoiceType+'</td><td align="right">'+order+'</td><td align="right">'+rtn.count+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.taxableamount.toFixed(2))+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.totaltax.toFixed(2))+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.totalamount.toFixed(2))+'</td><td align="left"><a href="#" onclick="updateDetails(\''+returnType+'\',\''+rtn.type+'\')" class="f-11 permissionInvoices-Sales-View">Details</a></td></tr>';*/
						
						if(returnType == 'GSTR1'){
							content+='<tr><td align="left">'+invoiceType+'</td><td align="right">'+order+'</td><td align="right">'+rtn.count+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.taxableamount.toFixed(2))+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.totaltax.toFixed(2))+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.totalamount.toFixed(2))+'</td><td align="left"><a href="#" onclick="updateDetails(\''+retype+'\',\''+rtn.type+'\')" class="f-11 permissionInvoices-Sales-View">Details</a></td></tr>';
							//$('.invoice_detailstab').addClass('permissionInvoices-Sales-View');
						}else{
							//$('.invoice_detailstab').addClass('permission'+returnType+'-'+returnType);																																																																																																										  
							content+='<tr><td align="left">'+invoiceType+'</td><td align="right">'+order+'</td><td align="right">'+rtn.count+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.taxableamount.toFixed(2))+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.totaltax.toFixed(2))+'</td><td align="right"><i class="fa fa-rupee"></i>'+formatNumber(rtn.totalamount.toFixed(2))+'</td><td align="left"><a href="#" onclick="updateDetails(\''+retype+'\',\''+rtn.type+'\')" class="f-11 permission'+returnType+'-'+returnType+'">Details</a></td></tr>';
						}
					});
					$('#invoiceSummary').html(content);
					table=$('table#dbTable1').DataTable({
						"dom": '<"toolbar">frtip',
						"pageLength": 20,
						"order": [[1,'asc']],
						"columnDefs": [
							{
								"targets": [1],
								"visible": false,
								"searchable": false
							}
						]
					});
					table.on('draw', rolesPermissions);
					$(".tabtable4  div.toolbar").html('<h4>'+ retype+' Summary Of '+monthName[month-1] +' '+year+' </h4>');
				}
				}else{
				table=$('table#dbTable1').DataTable({
					"dom": '<"toolbar">frtip',
					"pageLength": 20,
					"order": [[1,'asc']],
					"columnDefs": [
						{
							"targets": [1],
							"visible": false,
							"searchable": false
						}
					]
				});
				table.on('draw', rolesPermissions);
				$(".tabtable4  div.toolbar").html('<h4>'+ retype+' Summary Of '+monthName[month-1] +' '+year+' </h4>');
			}}else{
					table=$('table#dbTable1').DataTable({
						"dom": '<"toolbar">frtip',
						"pageLength": 20,
						"order": [[1,'asc']],
						"columnDefs": [
							{
								"targets": [1],
								"visible": false,
								"searchable": false
							}
						]
					});
					table.on('draw', rolesPermissions);
					$(".tabtable4  div.toolbar").html('<h4>'+ retype+' Summary Of '+monthName[month-1] +' '+year+' </h4>');
				}
		}
		
	}
	function updateDetails(retype,value) {
		if(retype == 'Purchase'){
			$('#purchaseTab').click();
		}else{
			$('#invoiceTab').click();
		}
		window.setTimeout(function(){
			applyFiltersForInvoiceType(retype,value);	
		}, 100);
		
	}
	function applyFiltersForInvoiceType(retType, invType) {
		if(retType == 'Sales'){
			retType = 'GSTR1';
		}else if(retType == 'Purchase'){
			retType = 'Purchase Register';
		}
		$('#multiselect'+retType.replace(" ", "_")+'2').multiselect().find(':checkbox[value="'+invType+'"]').attr('checked','checked');
		
		$('#multiselect'+retType.replace(" ", "_")+'2 option[value="'+invType+'"]').attr("selected", 1);
		$('#multiselect'+retType.replace(" ", "_")+'2').multiselect('select',''+invType+'').multiselect('updateButtonText');
		$('#multiselect'+retType.replace(" ", "_")+'2').multiselect('refresh');
		var typeOptions = $('#multiselect'+retType.replace(" ", "_")+'2 option:selected');
		var userOptions = $('#multiselect'+retType.replace(" ", "_")+'3 option:selected');
		var vendorOptions = $('#multiselect'+retType.replace(" ", "_")+'4 option:selected');
		var branchOptions = $('#multiselect'+retType.replace(" ", "_")+'5 option:selected');
		var verticalOptions = $('#multiselect'+retType.replace(" ", "_")+'6 option:selected');
		$('.normaltable .filter').css("display","block");
		applyInvFilters(retType);
		
	
	}
	
	function updateReconcileDetails(value) {
		//$('#misMatchTab').click();
		applyFiltersForMismatchType(value);
		$('#reconcilemodal').modal('hide');
	}
	
	function applyFiltersForMismatchType(mismatchType) {
		
		for(i=1;i<7;i++){
			$('#MMmultiselect'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
		}
	mismatchTable.clear();
		$('#MMmultiselect1').multiselect().find(':checkbox[value="'+mismatchType+'"]').attr('checked','checked');
		
		$('#MMmultiselect1 option[value="'+mismatchType+'"]').attr("selected", 1);
		$('#MMmultiselect1').multiselect('select',''+mismatchType+'').multiselect('updateButtonText');
		$('#MMmultiselect1').multiselect('refresh');
		var mtypeOptions = $('#MMmultiselect1 option:selected');
		var typeOptions = $('#MMmultiselect2 option:selected');
		var userOptions = $('#MMmultiselect3 option:selected');
		var vendorOptions = $('#MMmultiselect4 option:selected');
		var branchOptions = $('#MMmultiselect5 option:selected');
		var verticalOptions = $('#MMmultiselect6 option:selected');
		if(mtypeOptions.length > 0 || typeOptions.length > 0 || userOptions.length > 0 || vendorOptions.length > 0 || branchOptions > 0 || verticalOptions > 0){
			$('.normaltable .filter').css("display","block");
		}else{
			$('.normaltable .filter').css("display","none");
		}
		
		var mtypeArr=new Array();
		var typeArr=new Array();
		var userArr=new Array();
		var vendorArr=new Array();
		var branchArr=new Array();
		var verticalArr=new Array();
		
		var filterContent = '<span data-role="tagsinput" placeholder="" class="btaginput" >'+mismatchType+'<span data-val="'+mismatchType+'" class="deltag" data-role="remove"></span></span>';
		mtypeArr.push(mismatchType);
		$('#divMMFilters').html(filterContent);
		commonMMInvoiceFilter(mtypeArr, typeArr, userArr,vendorArr,branchArr,verticalArr);
		mismatchTable.draw();
	
	}
	
	
	function statusInvoiceFilter(value) {
		if(invoiceArray[varRetType.replace(' ', '_')].length > 0) {
			var rowNode;
			invoiceArray[varRetType.replace(' ', '_')].forEach(function(invoice) {
				if(value == 'Pending') {
					if(invoice.gstStatus == undefined 
						|| invoice.gstStatus == null || invoice.gstStatus == '') {
						invoiceTable[varRetType.replace(' ', '_')].row.add([ '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" id="invFilter'+invoice.id+'" onclick="updateSelection(\''+invoice.id+'\', this)"/><i class="helper"></i></label></div>', '<span class="color-yellow">Pending</span>', invoice.invoicetype, invoice.serialnoofinvoice, invoice.billedtoname,
						invoice.billedtogstin, invoice.dateofinvoice, invoice.totaltaxableamount, invoice.totaltax, invoice.totalamount,invoice.fullname,invoice.branch, invoice.vertical, '<a href="'+contextPath+'/editinv'+commonSuffix+'/'+invoice.id+'/'+varRetType+'/'+month+'/'+year+'"> <i class="fa fa-edit"></i> </a><a href="'+contextPath+'/invprint'+commonSuffix+'/'+clientId+'/'+varRetType+'/'+invoice.id+'" target="_blank"> <img src="'+contextPath+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right"></a><a href="#" onclick="showDeletePopup(\"'+invoice.id+'\",\"'+invoice.serialnoofinvoice+'\",\"'+varRetType+'\")"><img src="'+contextPath+'/static/mastergst/images/dashboard-ca/delicon.png" alt="Delete"></a>'] );
					}
				} else if((value == 'All') 
					|| (invoice.gstStatus == value)
					|| (value == 'Failed' && invoice.gstStatus != null && invoice.gstStatus != '' && invoice.gstStatus != 'SUCCESS' && invoice.gstStatus != 'Filed' && invoice.gstStatus != 'Submitted' && invoice.gstStatus != 'CANCELLED')) {
					var status = "<span class='color-yellow'>Pending</span>";
					if(value == 'All') {
						if(invoice.gstStatus != undefined || invoice.gstStatus != null) {
							status = "<span class='color-yellow'>"+invoice.gstStatus+"</span>";
							if(invoice.gstStatus == 'SUCCESS') {
								status = "<span class='color-green'>Uploaded</span>";
							} else if(invoice.gstStatus == 'CANCELLED') {
								status = "<span class='color-red'>Cancelled</span>";
							} else if(invoice.gstStatus == 'Filed') {
								status = "<span class='color-green'>Filed</span>";
							} else if(invoice.gstStatus == 'Submitted') {
								status = "<span class='color-green'>Submitted</span>";
							} else if(invoice.gstStatus == '') {
								status = "<span class='color-yellow'>Pending</span>";
							} else {
								status = "<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+invoice.gstStatus+"'></i>";
							}
						}
					} else if(value == 'SUCCESS') {
						status = "<span class='color-green'>Uploaded</span>";
					} else if(value == 'Filed') {
						status = "<span class='color-green'>Filed</span>";
					} else if(value == 'Submitted') {
						status = "<span class='color-green'>Submitted</span>";
					} else if(value == 'CANCELLED') {
						status = "<span class='color-red'>Cancelled</span>";
					} else if(value == 'Failed') {
						status = "<span class='color-red'>Failed </span><i class='fa fa-info-circle' style='font-size:17px;color:red' rel='tooltip' title='"+invoice.gstStatus+"'></i>";
					}
					rowNode = invoiceTable[varRetType.replace(' ', '_')].row.add([ '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" onclick="updateSelection(\''+invoice.id+'\', \''+varRetType+'\', \''+invoice.gstStatus+'\',this)" id="invFilter'+invoice.id+'"/><i class="helper"></i></label></div>', status, invoice.invoicetype, invoice.serialnoofinvoice, invoice.billedtoname, invoice.billedtogstin, invoice.dateofinvoice, formatNumber(invoice.totaltaxableamount.toFixed(2)), formatNumber(invoice.totaltax.toFixed(2)), formatNumber(invoice.totalamount.toFixed(2)), invoice.fullname,invoice.branch, invoice.vertical,'<a onclick="editInvPopup(null,\''+varRetType+'\',\''+invoice.id+'\')"><i class="fa fa-edit"></i> </a><a href="'+contextPath+'/invprint'+commonSuffix+'/'+clientId+'/'+varRetType+'/'+invoice.id+'" target="_blank"> <img src="'+contextPath+'/static/mastergst/images/master/printicon.png" alt="Print" style="vertical-align: initial;float:right"></a>'] );
					$(rowNode.node()).children().addClass('invoiceclk').attr('onclick',"editInvPopup(null,'"+varRetType+"','"+invoice.id+"')");
					invoiceTable[varRetType.replace(" ", "_")].row(rowNode).column(0).nodes().to$().addClass('text-right').removeAttr('onclick');
					invoiceTable[varRetType.replace(" ", "_")].row(rowNode).column(13).nodes().to$().addClass('text-right').removeAttr('onclick');
						
								
				}
			});
		}
	}
	function deleteInvoice(invId, retType, url) {
		if(url) { 
			window.location.href = url;
		} else {
			$.ajax({
				url: contextPath+"/delinv/"+invId+"/"+retType+"/"+clientId+"/"+month+"/"+year,
				success : function(response) {
					window.location.href = contextPath+'/alliview'+commonSuffix+'/'+clientId+'/'+varRetType+'/'+month+'/'+year+'?type='+invoiceStatus;
				}
			});
		}
	}
	function anx2tab(){
		$.ajax({
			url: contextPath+"/cp_users/"+userId+"/"+clientId,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				if (response.length > 0) {
					$("#MMmultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
					response.forEach(function(cp_user) {
						$("#MMmultiselect3").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
					});
				}else{
					$("#MMmultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				}
				$("#MMmultiselect3").multiselect({
						nonSelectedText: '- User -',
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
				$("#MMmultiselect3").multiselect('rebuild');
			}
		});
	}
	/*function invoiceviewByRTrDate(id, value, varRetType, varRetTypeCode, clientAddress) {
		var invviewtype = value;
		var retType = $('#retType').val();
		var fpinvDateInvcheck = "false";
		if(invviewtype == 'Invoice Date'){
			fpinvDateInvcheck = "false";
		}else{
			fpinvDateInvcheck = "true";
		}
		$.ajax({
			url: contextPath+"/mdfyclntBillDateInvoicesView?clientid="+clientId+"&billDateInv="+fpinvDateInvcheck,
			dataType: 'json',
			type: 'POST',
			cache: false,
			success : function(summary) {
				otherconfigdetails = summary;
				reconsiletab(clientAddress);
			}
		});
		
	}*/
	function reconsiletab(clientaddress){	
	if(otherconfigdetails.enableTransDate == true){
			$('.invoiceview').val('Transaction Date');
		}else{
			$('.invoiceview').val('Invoice Date');
		}
		clearMMMFilters();
		$.ajax({
			url: contextPath+"/cp_users/"+userId+"/"+clientId,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				$('#MMmultiselect3').children('option').remove();
				if (response.length > 0) {
					$("#MMmultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
					response.forEach(function(cp_user) {
						$("#MMmultiselect3").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name)); 
					});
				}else{
					$("#MMmultiselect3").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				}
				$("#MMmultiselect3").multiselect({
						nonSelectedText: '- User -',
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
				$("#MMmultiselect3").multiselect('rebuild');
			}
		});
		var fyUrl = contextPath+"/reconsileinvs"+urlSuffixs+"/Purchase Register/"+Paymenturlprefix;
		$.ajax({
			url: fyUrl,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(fyData) {
				Object.keys(fyData).forEach(function(rType) {
					invoiceArray[rType] = new Array();
					var fyList = null;
					var data = JSON.parse(fyData[rType]);
					if(data && data["content"]) {
						fyList = data["content"];
					} else {
						fyList = fyData[rType];
					}
					if(fyList instanceof Array) {
						if(fyList.length > 0) {
							fyList.forEach(function(fyInv){
								JSON.stringify(fyInv);
								var fyInvoice = updateInvoiceDetails(fyInv,clientaddress);
								invoiceArray[rType].push(fyInvoice);
							});
						}
					}
				});
				updateMismatchData();
			},
			error : function(data) {
				invoiceArray['prFY'] = invoiceArray['Purchase_Register'];
				updateMismatchData();
			}
			});
	}
	
	function updateMismatchData() {
		var content = '', redClass=' color-red', roundOffClass=' color-submit', matchedArray=new Array(), matchedidnotingstr2aArray=new Array(); var matchingarray = new Array(); var mannualMatchArray = new Array();
		var mismatchedtotalInvoices = 0,matchedtotalInvoices = 0,othermonthsmatchedtotalInvoices = 0,notinGstr2atotalInvoices = 0,notinPurchasetotalInvoices = 0,invoicenomismatchtotalInvoices = 0,taxmismatchtotalInvoices = 0,invoiceValuemismatchtotalInvoices = 0, rooundoffmismatchtotalInvoices = 0,gstnomismatchtotalInvoices = 0,roundoffmatechedtotalInvoices = 0,invoiceDateMismatchedInvoices = 0,probablematchedtotalInvoices = 0,mannualMatchedInvoices = 0;
		MismatchArray = new Array();
		if(invoiceArray['prFY'] == null || invoiceArray['prFY'] == undefined || invoiceArray['prFY'].length == 0) {
			if(invoiceArray['g2FYMatched'] == null || invoiceArray['g2FYMatched'] == undefined || invoiceArray['g2FYMatched'].length == 0) {
			} else {
				invoiceArray['g2FYMatched'].forEach(function(gstr2) {
					if(gstr2.b2b == null || gstr2.b2b.length == 0) {
						gstr2.b2b = new Array();
						var tObj = new Object();
						tObj.ctin = '';
						gstr2.b2b.push(tObj);
					}
					if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == gstr2.invtype && gstr2.cdn && gstr2.cdn.length > 0 && gstr2.cdn[0].nt && gstr2.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == gstr2.invtype && gstr2.cdn && gstr2.cdn.length > 0 && gstr2.cdn[0].nt && gstr2.cdn[0].nt.length > 0)) {
			
			if(gstr2.cdn[0].nt[0].ntty == 'C'){
				gstr2.invtype = 'Credit Note';
			}else if(invoice.cdn[0].nt[0].ntty == 'D'){
				gstr2.invtype = 'Debit Note';
			}
			
		} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == gstr2.invtype && gstr2.cdnr && gstr2.cdnr.length > 0 && gstr2.cdnr[0].nt && gstr2.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == gstr2.invtype && gstr2.cdnr && gstr2.cdnr.length > 0 && gstr2.cdnr[0].nt && gstr2.cdnr[0].nt.length > 0)) {
			
			if(gstr2.cdnr[0].nt[0].ntty == 'C'){
				gstr2.invtype = 'Credit Note';
			}else if(gstr2.cdnr[0].nt[0].ntty == 'D'){
				gstr2.invtype = 'Debit Note';
			}
		}
		if(('<%=MasterGSTConstants.CDNUR%>' == gstr2.invtype && gstr2.cdnur && gstr2.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == gstr2.invtype && gstr2.cdnur && gstr2.cdnur.length > 0)) {
			if(gstr2.cdnur[0].ntty == 'C'){
				gstr2.invtype = 'Credit Note(UR)';
			}else if(gstr2.cdnur[0].ntty == 'D'){
				gstr2.invtype = 'Debit Note(UR)';
			}
		}
		if(('Import Goods' == gstr2.invtype)){
			gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
		}
					gstr2.mstatus='Not In Purchases';
					gstr2.gfp='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
					gstr2.ginvno='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
					gstr2.ginvdate='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
					gstr2.ggstno='<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
					gstr2.ginvvalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
					gstr2.gtaxablevalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
					gstr2.gtotaltax='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
					gstr2.gbranch='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.branch+'</div></div>';
					gstr2.gcomments='<div><a href="#" onclick="supComments(\''+gstr2.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
					if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
						gstr2.invoiceCustomerIdAndBilledToName = '';
					}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
						gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
					}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
						gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
					}
					gstr2.gstrid = gstr2.id;
					gstr2.id = undefined;
					MismatchArray.push(gstr2);
					content += '<tr><td><div class="checkbox" index="'+gstr2.gstrid+'"><label><input type="checkbox" name="invMFilter'+gstr2.gstrid+'" onClick="updateMisMatchSelection(null, \''+gstr2.gstrid+'\', \''+gstr2.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.fullname+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.vertical+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></span></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.branch+'</td><td onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+gstr2.mstatus+'">'+gstr2.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+gstr2.gstrid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></td></tr>';
				});
			}
		} else {
			invoiceArray['prFY'].forEach(function(invoice) {
				if(invoice.matchingId) {
					matchedArray.push(invoice.matchingId);
				}
			});
			invoiceArray['prFY'].forEach(function(invoice) {
				if(invoice.b2b == null || invoice.b2b.length == 0) {
					invoice.b2b = new Array();
					var tObj = new Object();
					tObj.ctin = '';
					invoice.b2b.push(tObj);
				}
				if(invoiceArray['g2FYMatched'] == null || invoiceArray['g2FYMatched'] == undefined || invoiceArray['g2FYMatched'].length == 0) {
					matchedidnotingstr2aArray.push(invoice.id);
					if(('Import Goods' == invoice.invtype)){
						invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
					}
					invoice.mstatus='Not In GSTR 2A';
					invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
					invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gbranch='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
					
					if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
						invoice.invoiceCustomerIdAndBilledToName = '';
					}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
						invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
					}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
						invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
					}
					if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
			
			if(invoice.cdn[0].nt[0].ntty == 'C'){
				invoice.invtype = 'Credit Note';
			}else if(invoice.cdn[0].nt[0].ntty == 'D'){
				invoice.invtype = 'Debit Note';
			}
			
		} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
			
			if(invoice.cdnr[0].nt[0].ntty == 'C'){
				invoice.invtype = 'Credit Note';
			}else if(invoice.cdnr[0].nt[0].ntty == 'D'){
				invoice.invtype = 'Debit Note';
			}
		}
		if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
			if(invoice.cdnur[0].ntty == 'C'){
				invoice.invtype = 'Credit Note(UR)';
			}else if(invoice.cdnur[0].ntty == 'D'){
				invoice.invtype = 'Debit Note(UR)';
			}
		}
					
					MismatchArray.push(invoice);
					content += '<tr><td><div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left">'+invoice.fullname+'</td><td class="text-left">'+invoice.vertical+'</td><td class=""text-left">'+invoice.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></span></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</td><td onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
				} else {
					if(invoice.matchingStatus != null && invoice.matchingStatus != '' && invoice.matchingId != null && invoice.matchingId != '') {
						invoiceArray['g2FYMatched'].forEach(function(gstr2) {
							if(gstr2.b2b == null || gstr2.b2b.length == 0) {
								gstr2.b2b = new Array();
								var tObj = new Object();
								tObj.ctin = '';
								gstr2.b2b.push(tObj);
							}
							if(gstr2.id == invoice.matchingId) {
								if($.inArray(gstr2.id, matchingarray) == -1) {
									matchedidnotingstr2aArray.push(invoice.id);
								if(invoice.matchingStatus != 'Matched' && invoice.matchingStatus != 'Round Off Matched' && invoice.matchingStatus != 'Matched In Other Months' && invoice.matchingStatus != 'Probable Matched') {
									var tStatus = 'Mismatched';
									if((invoice.invoiceno == gstr2.invoiceno)
										&& (invoice.dateofinvoice == gstr2.dateofinvoice)
										&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
										&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
										&& (invoice.totaltax == gstr2.totaltax)) {
										matchedtotalInvoices = matchedtotalInvoices+1;
										tStatus = 'Matched';
									} else if((invoice.invoiceno == gstr2.invoiceno)
										&& (invoice.dateofinvoice == gstr2.dateofinvoice)
										&& (invoice.b2b[0].ctin != gstr2.b2b[0].ctin)
										&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
										&& (invoice.totaltax == gstr2.totaltax)) {
										tStatus = 'GST No Mismatched';
										gstnomismatchtotalInvoices = gstnomismatchtotalInvoices+1;
									} else if((invoice.invoiceno == gstr2.invoiceno)
										&& (invoice.dateofinvoice == gstr2.dateofinvoice)
										&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
										&& (invoice.totaltaxableamount != gstr2.totaltaxableamount)
										&& (invoice.totaltax == gstr2.totaltax)) {
										tStatus = 'Invoice Value Mismatched';
										invoiceValuemismatchtotalInvoices = invoiceValuemismatchtotalInvoices+1;
									} else if((invoice.invoiceno == gstr2.invoiceno)
										&& (invoice.dateofinvoice == gstr2.dateofinvoice)
										&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
										&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
										&& (invoice.totaltax != gstr2.totaltax)) {
										tStatus = 'Tax Mismatched';
										taxmismatchtotalInvoices = taxmismatchtotalInvoices+1;
									} else if((invoice.invoiceno != gstr2.invoiceno)
										&& (invoice.dateofinvoice == gstr2.dateofinvoice)
										&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
										&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
										&& (invoice.totaltax == gstr2.totaltax)) {
										tStatus = 'Invoice No Mismatched';
										invoicenomismatchtotalInvoices = invoicenomismatchtotalInvoices+1;
									} else if((invoice.invoiceno == gstr2.invoiceno)
											&& (invoice.dateofinvoice != gstr2.dateofinvoice)
											&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
											&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
											&& (invoice.totaltax == gstr2.totaltax)) {
											tStatus = 'Invoice Date Mismatched';
											invoiceDateMismatchedInvoices = invoiceDateMismatchedInvoices+1;
									}else {
										mismatchedtotalInvoices = mismatchedtotalInvoices+1;
									}
									invoice.mstatus=tStatus;
								} else {
									if(invoice.matchingStatus == 'Matched'){
										matchedtotalInvoices = matchedtotalInvoices+1;
									}else if(invoice.matchingStatus == 'Matched In Other Months'){
										othermonthsmatchedtotalInvoices = othermonthsmatchedtotalInvoices+1;
									}else if(invoice.matchingStatus == 'Round Off Matched'){
										roundoffmatechedtotalInvoices = roundoffmatechedtotalInvoices+1;
									}else{
										probablematchedtotalInvoices = probablematchedtotalInvoices+1;
									}
									invoice.mstatus=invoice.matchingStatus;
								}
								matchedArray.push(gstr2.id);
								matchingarray.push(gstr2.id);
								if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
									invoice.invoiceCustomerIdAndBilledToName = '';
								}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
									invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
								}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
									invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
								}
								if(('Import Goods' == invoice.invtype)){
									invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
									gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
								}
								content += '<tr><td><div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', \''+invoice.matchingId+'\', \''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td align="center" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1">'+invoice.fp+'</div><div class="tdline_2">'+gstr2.fp+'</div></td><td class="text-left">'+invoice.fullname+'</td><td class="text-left">'+invoice.vertical+'</td><td class="text-left">'+invoice.invoiceCustomerIdAndBilledToName+'</td>';
								
								invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1">'+invoice.fp+'</div><div class="tdline_2">'+gstr2.fp+'</div></div>';
								if(invoice.mstatus == 'Probable Matched'){
									content += '<td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.invoiceno == gstr2.invoiceno)?'':roundOffClass)+'">'+invoice.invoiceno+'</div><div class="tdline_2'+((invoice.invoiceno == gstr2.invoiceno)?'':roundOffClass)+'">'+gstr2.invoiceno+'</div></td>';
								}else{
									content += '<td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.invoiceno == gstr2.invoiceno)?'':redClass)+'">'+invoice.invoiceno+'</div><div class="tdline_2'+((invoice.invoiceno == gstr2.invoiceno)?'':redClass)+'">'+gstr2.invoiceno+'</div></td>';
								}
								if(invoice.mstatus == 'Round Off Matched'){
									content += '<td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':roundOffClass)+'">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':roundOffClass)+'">'+formatDate(gstr2.dateofinvoice)+'</div></td>';
								}else{
									content += '<td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':redClass)+'">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':redClass)+'">'+formatDate(gstr2.dateofinvoice)+'</div></td>';
								}
								content += '<td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1'+((invoice.b2b[0].ctin == gstr2.b2b[0].ctin)?'':redClass)+'">'+invoice.b2b[0].ctin+'</div><div class="tdline_2'+((invoice.b2b[0].ctin == gstr2.b2b[0].ctin)?'':redClass)+'">'+gstr2.b2b[0].ctin+'</div></span></td>';
								if(invoice.mstatus == 'Round Off Matched' || invoice.mstatus == 'Probable Matched'){
									content += '<td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totalamount == gstr2.totalamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totalamount == gstr2.totalamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></td>';
									content += '<td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></td>';
									content += '<td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltax == gstr2.totaltax)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltax == gstr2.totaltax)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></td>';
								}else{
									content += '<td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totalamount == gstr2.totalamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totalamount == gstr2.totalamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></td>';
									content += '<td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></td>';
									content += '<td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltax == gstr2.totaltax)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltax == gstr2.totaltax)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></td>';
								}
								content += '<td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</td><td onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
								if(invoice.mstatus == 'Probable Matched'){
									invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.invoiceno == gstr2.invoiceno)?'':roundOffClass)+'">'+invoice.invoiceno+'</div><div class="tdline_2'+((invoice.invoiceno == gstr2.invoiceno)?'':roundOffClass)+'">'+gstr2.invoiceno+'</div></div>';
								}else{
									invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.invoiceno == gstr2.invoiceno)?'':redClass)+'">'+invoice.invoiceno+'</div><div class="tdline_2'+((invoice.invoiceno == gstr2.invoiceno)?'':redClass)+'">'+gstr2.invoiceno+'</div></div>';
								}
								if(invoice.mstatus == 'Round Off Matched'){
									invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':roundOffClass)+'">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':roundOffClass)+'">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
								}else{
									invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':redClass)+'">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':redClass)+'">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
								}
								invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.b2b[0].ctin == gstr2.b2b[0].ctin)?'':redClass)+'">'+invoice.b2b[0].ctin+'</div><div class="tdline_2'+((invoice.b2b[0].ctin == gstr2.b2b[0].ctin)?'':redClass)+'">'+gstr2.b2b[0].ctin+'</div></div></span>';
								if(invoice.mstatus == 'Round Off Matched' || invoice.mstatus == 'Probable Matched'){
									invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totalamount == gstr2.totalamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totalamount == gstr2.totalamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
									invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
									invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltax == gstr2.totaltax)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltax == gstr2.totaltax)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
								}else{
									invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totalamount == gstr2.totalamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totalamount == gstr2.totalamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
									invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
									invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltax == gstr2.totaltax)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltax == gstr2.totaltax)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
								}
								invoice.gbranch='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.branch == gstr2.branch)?'':redClass)+'">'+invoice.branch+'</div><div class="tdline_2'+((invoice.branch == gstr2.branch)?'':redClass)+'">'+gstr2.branch+'</div></div>';
								invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
								invoice.gstrid = invoice.matchingId;
								MismatchArray.push(invoice);
							}
						}
						});
					} else if(invoice.matchingStatus == null || invoice.matchingStatus == '' || invoice.matchingId == null || invoice.matchingId == '') {
						matchedidnotingstr2aArray.push(invoice.id);
						if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
							invoice.invoiceCustomerIdAndBilledToName = '';
						}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
							invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
						}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
							invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
						}
						if(('Import Goods' == invoice.invtype)){
							invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
						}
						invoice.mstatus='Not In GSTR 2A';
						invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
						invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.gfullname='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
						notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
						MismatchArray.push(invoice);
						content += '<tr><td><div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left">'+invoice.fullname+'</td><td class="text-left">'+invoice.vertical+'</td><td class="text-left">'+invoice.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></span></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</td><td onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
					}
				}
			});
			invoiceArray['g2FYMatched'].forEach(function(gstr2) {
				if(gstr2.b2b == null || gstr2.b2b.length == 0) {
					gstr2.b2b = new Array();
					var tObj = new Object();
					tObj.ctin = '';
					gstr2.b2b.push(tObj);
				}
				if($.inArray(gstr2.id, matchedArray) == -1) {
					if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
						gstr2.invoiceCustomerIdAndBilledToName = '';
					}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
						gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
					}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
						gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
					}
					if(('Import Goods' == gstr2.invtype)){
						gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
					}
					gstr2.mstatus='Not In Purchases';
					gstr2.gfp='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
					gstr2.ginvno='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
					gstr2.ginvdate='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
					gstr2.ggstno='<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
					gstr2.ginvvalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
					gstr2.gtaxablevalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
					gstr2.gtotaltax='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
					gstr2.gbranch='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.branch+'</div></div>';
					gstr2.gcomments='<div><a href="#" onclick="supComments(\''+gstr2.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					gstr2.gstrid = gstr2.id;
					gstr2.id = undefined;
					notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
					MismatchArray.push(gstr2);
					content += '<tr><td><div class="checkbox" index="'+gstr2.gstrid+'"><label><input type="checkbox" name="invMFilter'+gstr2.gstrid+'" onClick="updateMisMatchSelection(null, \''+gstr2.gstrid+'\', \''+gstr2.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="color-green tdline_1"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></td><td class="text-left">'+gstr2.fullname+'</td><td class="text-left">'+gstr2.vertical+'</td><td class="text-left">'+gstr2.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></span></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.branch+'</td><td onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+gstr2.mstatus+'">'+gstr2.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+gstr2.gstrid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
				}
			});

			invoiceArray['prFY'].forEach(function(invoice) {
				
				if($.inArray(invoice.id, matchedidnotingstr2aArray) == -1) {
					if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
						invoice.invoiceCustomerIdAndBilledToName = '';
					}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
						invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
					}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
						invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
					}
					if(('Import Goods' == invoice.invtype)){
						invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
					}
					invoice.mstatus='Not In GSTR 2A';
					invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
					invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gfullname='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
					MismatchArray.push(invoice);
					content += '<tr><td><div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.fullname+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.vertical+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></span></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</td><td onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
				}
			});
			
			
		}
		if(invoiceArray['gPMannualFYMatched'] == null || invoiceArray['gPMannualFYMatched'] == undefined || invoiceArray['gPMannualFYMatched'].length == 0) {
			if(invoiceArray['g2MannualFYMatched'] == null || invoiceArray['g2MannualFYMatched'] == undefined || invoiceArray['g2MannualFYMatched'].length == 0) {
			} else {
				invoiceArray['g2MannualFYMatched'].forEach(function(gstr2) {
					if(gstr2.b2b == null || gstr2.b2b.length == 0) {
						gstr2.b2b = new Array();
						var tObj = new Object();
						tObj.ctin = '';
						gstr2.b2b.push(tObj);
					}
					if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == gstr2.invtype && gstr2.cdn && gstr2.cdn.length > 0 && gstr2.cdn[0].nt && gstr2.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == gstr2.invtype && gstr2.cdn && gstr2.cdn.length > 0 && gstr2.cdn[0].nt && gstr2.cdn[0].nt.length > 0)) {
			
			if(gstr2.cdn[0].nt[0].ntty == 'C'){
				gstr2.invtype = 'Credit Note';
			}else if(invoice.cdn[0].nt[0].ntty == 'D'){
				gstr2.invtype = 'Debit Note';
			}
			
		} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == gstr2.invtype && gstr2.cdnr && gstr2.cdnr.length > 0 && gstr2.cdnr[0].nt && gstr2.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == gstr2.invtype && gstr2.cdnr && gstr2.cdnr.length > 0 && gstr2.cdnr[0].nt && gstr2.cdnr[0].nt.length > 0)) {
			
			if(gstr2.cdnr[0].nt[0].ntty == 'C'){
				gstr2.invtype = 'Credit Note';
			}else if(gstr2.cdnr[0].nt[0].ntty == 'D'){
				gstr2.invtype = 'Debit Note';
			}
		}
		if(('<%=MasterGSTConstants.CDNUR%>' == gstr2.invtype && gstr2.cdnur && gstr2.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == gstr2.invtype && gstr2.cdnur && gstr2.cdnur.length > 0)) {
			if(gstr2.cdnur[0].ntty == 'C'){
				gstr2.invtype = 'Credit Note(UR)';
			}else if(gstr2.cdnur[0].ntty == 'D'){
				gstr2.invtype = 'Debit Note(UR)';
			}
		}			
		if(('Import Goods' == gstr2.invtype)){
			gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
		}
					gstr2.mstatus='Not In Purchases';
					gstr2.gfp='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
					gstr2.ginvno='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
					gstr2.ginvdate='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
					gstr2.ggstno='<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
					gstr2.ginvvalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
					gstr2.gtaxablevalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
					gstr2.gtotaltax='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
					gstr2.gbranch='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.branch+'</div></div>';
					gstr2.gcomments='<div><a href="#" onclick="supComments(\''+gstr2.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
					if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
						gstr2.invoiceCustomerIdAndBilledToName = '';
					}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
						gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
					}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
						gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
					}
					gstr2.gstrid = gstr2.id;
					gstr2.id = undefined;
					MismatchArray.push(gstr2);
					content += '<tr><td><div class="checkbox" index="'+gstr2.gstrid+'"><label><input type="checkbox" name="invMFilter'+gstr2.gstrid+'" onClick="updateMisMatchSelection(null, \''+gstr2.gstrid+'\', \''+gstr2.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></td><td class="text-left">'+gstr2.fullname+'</td><td class="text-left">'+gstr2.vertical+'</td><td class="text-left">'+gstr2.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></span></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.branch+'</td><td onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+gstr2.mstatus+'">'+gstr2.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+gstr2.gstrid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
				});
			}
		
		}else{
			invoiceArray['gPMannualFYMatched'].forEach(function(invoice) {
				if(invoice.id) {
					mannualMatchArray.push(invoice.id);
				}
			});
			
			invoiceArray['gPMannualFYMatched'].forEach(function(invoice) {
				if(invoice.b2b == null || invoice.b2b.length == 0) {
					invoice.b2b = new Array();
					var tObj = new Object();
					tObj.ctin = '';
					invoice.b2b.push(tObj);
				}
				if(('Import Goods' == invoice.invtype)){
					ivoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
				}
				if(invoiceArray['g2MannualFYMatched'] == null || invoiceArray['g2MannualFYMatched'] == undefined || invoiceArray['g2MannualFYMatched'].length == 0) {
					invoice.mstatus='Not In GSTR 2A';
					invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
					invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gbranch='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
					
					if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
						invoice.invoiceCustomerIdAndBilledToName = '';
					}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
						invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
					}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
						invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
					}
					if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
			
			if(invoice.cdn[0].nt[0].ntty == 'C'){
				invoice.invtype = 'Credit Note';
			}else if(invoice.cdn[0].nt[0].ntty == 'D'){
				invoice.invtype = 'Debit Note';
			}
			
		} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
			
			if(invoice.cdnr[0].nt[0].ntty == 'C'){
				invoice.invtype = 'Credit Note';
			}else if(invoice.cdnr[0].nt[0].ntty == 'D'){
				invoice.invtype = 'Debit Note';
			}
		}
		if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
			if(invoice.cdnur[0].ntty == 'C'){
				invoice.invtype = 'Credit Note(UR)';
			}else if(invoice.cdnur[0].ntty == 'D'){
				invoice.invtype = 'Debit Note(UR)';
			}
		}
					
					MismatchArray.push(invoice);
					content += '<tr><td><div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null, \''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.fp+'</td><td class="text-left">'+invoice.fullname+'</td><td class="text-left">'+invoice.vertical+'</td><td class=""text-left">'+invoice.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></span></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</td><td onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
				} else {
					if(invoice.matchingStatus != null && invoice.matchingStatus != '') {
						invoiceArray['g2MannualFYMatched'].forEach(function(gstr2) {
							if(gstr2.b2b == null || gstr2.b2b.length == 0) {
								gstr2.b2b = new Array();
								var tObj = new Object();
								tObj.ctin = '';
								gstr2.b2b.push(tObj);
							}
			
							if(gstr2.id == invoice.matchingId) {
								
								if($.inArray(gstr2.id, matchingarray) == -1) {
									invoice.mstatus=invoice.matchingStatus;
								mannualMatchArray.push(gstr2.id);
								matchingarray.push(gstr2.id);
								if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
									invoice.invoiceCustomerIdAndBilledToName = '';
								}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
									invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
								}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
									invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
								}
								if(('Import Goods' == invoice.invtype)){
									invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
								}
								content += '<tr><td><div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', \''+invoice.matchingId+'\', \''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td align="center"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left">'+invoice.invtype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left"><div class="tdline_1 hsadj">'+invoice.fp+'</div><div class="tdline_2">-</div></td><td class="text-left">'+invoice.branch+'</td><td class="text-left">'+invoice.vertical+'</td><td class="text-left">'+invoice.invoiceCustomerIdAndBilledToName+'</td>';
								//content += '<td class="text-left"><div class="tdline_1 hsadj">'+invoice.fp+'</div><div class="tdline_2">-</div></td>';
								content += '<td class="text-left"><div class="tdline_1">'+invoice.invoiceno+'</div><div class="tdline_2">-</div></td>';
								content += '<td class="text-left"><div class="tdline_1">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2">-</div></td>';
								content += '<td class="text-left"><span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1">'+invoice.b2b[0].ctin+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal" onClick="viewMannualMatchedInvoices(\''+invoice.id+'\',\'Purchase Register\')">Mannualy Matched with Multiple Invoices</a></div></span></td>';
								content += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
								content += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
								content += '<td class="text-right"><div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div></td>';
								content += '<td class="text-left">'+invoice.fullname+'</td><td><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></td>';
								content += '<td><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
								invoice.gfp='<div class="tdline_1">'+invoice.fp+'</div><div class="tdline_2">-</div>';
								invoice.ginvno='<div class="tdline_1">'+invoice.invoiceno+'</div><div class="tdline_2">-</div>';
								invoice.ginvdate='<div class="tdline_1">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2">-</div>';
								invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1">'+invoice.b2b[0].ctin+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal" onClick="viewMannualMatchedInvoices(\''+invoice.id+'\',\'Purchase Register\')">Mannualy Matched with Multiple Invoices</a></div></span>';
								invoice.ginvvalue='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
								invoice.gtaxablevalue='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
								invoice.gtotaltax='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
								invoice.gbranch='<div class="tdline_1">'+invoice.branch+'</div><div class="tdline_2">-</div>';
								invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
								invoice.gstrid = invoice.matchingId;
								MismatchArray.push(invoice);
								mannualMatchedInvoices = mannualMatchedInvoices+1;
							}
						}
						});
					} else if(invoice.matchingStatus == null || invoice.matchingStatus == '') {
						if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
							invoice.invoiceCustomerIdAndBilledToName = '';
						}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
							invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
						}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
							invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
						}
						if(('Import Goods' == invoice.invtype)){
							invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
						}
						invoice.mstatus='Not In GSTR 2A';
						invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
						invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.gbranch='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
						invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
						notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
						MismatchArray.push(invoice);
						content += '<tr><td><div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null, \''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left">'+invoice.fullname+'</td><td class="text-left">'+invoice.vertical+'</td><td class="text-left">'+invoice.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-right" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></td><td class="text-left" onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</td><td onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
					}
				}
			});
			invoiceArray['g2MannualFYMatched'].forEach(function(gstr2) {
				if(gstr2.b2b == null || gstr2.b2b.length == 0) {
					gstr2.b2b = new Array();
					var tObj = new Object();
					tObj.ctin = '';
					gstr2.b2b.push(tObj);
				}
				
				if($.inArray(gstr2.id, mannualMatchArray) == -1) {
					if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
						gstr2.invoiceCustomerIdAndBilledToName = '';
					}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
						gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
					}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
						gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
					}
					if(('Import Goods' == gstr2.invtype)){
						gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
					}
					gstr2.mstatus="Not In Purchases";
					gstr2.gfp='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 color-red">'+gstr2.fp+'</div></div>';
					gstr2.ginvno='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 color-red">'+gstr2.invoiceno+'</div></div>';
					gstr2.ginvdate='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
					gstr2.ggstno='<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
					gstr2.ginvvalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 misindformat color-red">-</div><div class="tdline_1 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
					gstr2.gtaxablevalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 misindformat color-red">-</div><div class="tdline_1 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
					gstr2.gtotaltax='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 misindformat color-red">-</div><div class="tdline_1 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
					gstr2.gbranch='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 colo-red">'+gstr2.branch+'</div></div>';
					gstr2.gcomments='<div><a href="#" onclick="supComments(\''+gstr2.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					gstr2.gstrid = gstr2.id;
					gstr2.id = undefined;
					notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
					if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
						gstr2.invoiceCustomerIdAndBilledToName = '';
					}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
						gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
					}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
						gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
					}
					MismatchArray.push(gstr2);
					content += '<tr><td><div class="checkbox" index="'+gstr2.gstrid+'"><label><input type="checkbox" name="invMFilter'+gstr2.gstrid+'" onClick="updateMisMatchSelection(null, \''+gstr2.gstrid+'\', \''+gstr2.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div></td><td class="center"><div class="color-green tdline_1"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invtype+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.billedtoname+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.fp+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.fullname+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.vertical+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invoiceCustomerIdAndBilledToName+'</td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invoiceno+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+formatDate(gstr2.dateofinvoice)+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div class="tdline_1 color-red" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></span></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></td><td class="text-right" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></td><td class="text-left" onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.branch+'</td><td onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><span class="bluetxt f-13" index="mismatchStatus'+gstr2.mstatus+'">'+gstr2.mstatus+'</span></td><td><a href="#" onclick="supComments(\''+gstr2.gstrid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a></td></tr>';
				}
			});
			
		}
		
		
		
		$('#totalMismatchedInvoices').html(mismatchedtotalInvoices);$('#totalMatchedInvoices').html(matchedtotalInvoices);$('#totalMatchedInvoicesInOtherMonths').html(othermonthsmatchedtotalInvoices);
		$('#totalNotInGstr2AInvoices').html(notinGstr2atotalInvoices);$('#totalNotInPurchasesInvoices').html(notinPurchasetotalInvoices);
		$('#totalGSTnoMismatchedInvoices').html(gstnomismatchtotalInvoices);$('#totalInvoiceValueMismatchedInvoices').html(invoiceValuemismatchtotalInvoices);
		$('#totalTaxMismatchedInvoices').html(taxmismatchtotalInvoices);$('#totalInvoiceNoMismatchInvoices').html(invoicenomismatchtotalInvoices);$('#totalRoundoffMismatchedInvoices').html(roundoffmatechedtotalInvoices);
		$('#totalInvoiceDateMismatchedInvoices').html(invoiceDateMismatchedInvoices);$('#totalProbableMatchedInvoices').html(probablematchedtotalInvoices);$('#totalMannualMatchedInvoices').html(mannualMatchedInvoices);
		if(MismatchArray.length > 0) {
		var taxArray = new Array();
		var counts =0;
		var custnames = [];
		MismatchArray.forEach(function(invoice) {
			if(invoice.invoiceCustomerId){
				if(invoice.billedtoname) {
					if(counts == 0){
						$('#MMmultiselect4').children('option').remove();
						custnames.push(invoice.billedtoname+"("+invoice.invoiceCustomerId+")");
						$("#MMmultiselect4").append($("<option></option>").attr("value",invoice.billedtoname+"("+invoice.invoiceCustomerId+")").text(invoice.billedtoname+"("+invoice.invoiceCustomerId+")"));
					}
					if(jQuery.inArray(invoice.billedtoname+"("+invoice.invoiceCustomerId+")", custnames ) == -1){
						custnames.push(invoice.billedtoname+"("+invoice.invoiceCustomerId+")");
						$("#MMmultiselect4").append($("<option></option>").attr("value",invoice.billedtoname+"("+invoice.invoiceCustomerId+")").text(invoice.billedtoname+"("+invoice.invoiceCustomerId+")"));
					}
				}
			}else{
				if(invoice.billedtoname) {
					if(counts == 0){
						$('#MMmultiselect4').children('option').remove();
						custnames.push(invoice.billedtoname);
						$("#MMmultiselect4").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
					}
					if(jQuery.inArray(invoice.billedtoname, custnames ) == -1){
						custnames.push(invoice.billedtoname);
						$("#MMmultiselect4").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
					}
				}				
			}
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
			taxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,creditDebit]);
			counts++;
		});
		$("#MMmultiselect4").multiselect('rebuild');
		var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0;
		taxArray.forEach(function(row) {
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
		$('#idMMCount').html(0);
		$('#idMMIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idMMCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idMMSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idMMCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idMMTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
		$('#idMMTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	}
		if(mismatchTable) {
			mismatchTable.destroy();
		}
		$('#idMisMatchBody').html(content);
		mismatchTable=$('#reconsileDbTable').DataTable({
			"dom": '<"toolbar"f>lrtip<"clear">',
			"paging": true,
			"searching": true,
			"responsive": true,
			"orderClasses": false,
			"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
			"order": [[9,'desc']],
			//"order": [[8,'desc']],
			"columnDefs": [
				{
					//"targets": [ 4, 5,6, 10 ],
					"targets": [ 5, 6,7, 11],
					"visible": false,
					"searchable": true
				},
				{
					"targets": [16],
					"class":"dt-body-right"
				},
				{
						"targets": 0,
						"orderable": false
					}
			]
		});
		$('#yearProcess').text('');
	}
	function supComments(id){
		$('#supCommentsModal').modal('show');
		$('.suppliercommentsTab').html("");
		$('#nocomments_sup').text("");
		$('#addComment').attr("onclick","addSupplierComments(\""+id+"\")");
		$.ajax({
			url: contextPath+"/suppliercomments/"+id,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response){
				if(response.length == 0){
					$('#nocomments_sup').text("No Comments Added Yet, Please add your Comments");
				}
				for(var i=0;i<response.length;i++){
					$('#nocomments_sup').text("");
					//$('.suppliercommentsTab').append('<div class="supcommentsTab mb-2 mr-2">'+response[i].supcomments+'<br/><strong>'+response[i].addedby+'</strong><strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+formatDate(response[i].commentDate)+'</strong></div>');
					$('.suppliercommentsTab').append('<div class="supcommentsTab mb-2 mr-2"><strong><label class="label_txt">Added By : '+response[i].addedby+'</label></strong><strong><label style="float:right;">Date : '+formatDate(response[i].commentDate)+'</label></strong><br/>'+response[i].supcomments+'</div>');
				}
			},error:function(err){
			}
		});
	}
	function addSupplierComments(invoiceid){
		var comments = $('#sup_comments').val();
		if(comments != ""){
			$.ajax({
				url: contextPath+"/saveSupplierComments/"+invoiceid+""+urlSuffixs,
				method:"POST",
				contentType: 'application/x-www-form-urlencoded',
				data: {
					'comments': comments
				},
				success : function(response){
					$('.suppliercommentsTab').html("");
					for(var i=0;i<response.length;i++){
						$('.suppliercommentsTab').append('<div class="supcommentsTab mb-2 mr-2"><strong><label class="label_txt">Added By : '+response[i].addedby+'</label></strong><strong><label style="float:right;">Date : '+formatDate(response[i].commentDate)+'</label></strong><br/>'+response[i].supcomments+'</div>');
					}
						$('#sup_comments').val("");
				},error:function(err){
				}
			});
	}
}
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
	function showMismatchInv(id,mstatus){
		var content = '';var content1 = '';var branch;var billedtoname;var pbilledtoname;
		$('#showMismatchInvModal').modal("show");
		$('#recgstr2a').html('Record in GSTR2A');
		$.ajax({
			type: "GET",
			url: contextPath+"/mismatchInvoice_details/"+mstatus+"/"+id,
			async: false,
			cache: false,
			contentType: 'application/json',
			success : function(response){
					if(mstatus == 'Not In GSTR 2A'){
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
							if(response.gstr2invoice != null){
								if(response.gstr2invoice.billedtoname == '' || response.gstr2invoice.billedtoname == null){billedtoname = "";}else{billedtoname=response.gstr2invoice.billedtoname;}
								content += '<tr><td class="text-left">'+response.gstr2invoice.invtype+'</td><td class="text-left">'+billedtoname+'</td><td class="text-left">'+response.gstr2invoice.invoiceno+'</td><td>'+formatDate(response.gstr2invoice.dateofinvoice)+'</td><td class="text-left">'+response.gstr2invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2invoice.totaltax).toFixed(2))+'</td><td class="text-left">'+mstatus+'</td></tr>';
							}
						if(showInvTable) {
							showInvTable.clear().destroy();
						}
						if(showInvPRTable) {
							showInvPRTable.clear().destroy();
						}
						$('#showInvDetailTable_Body1').html(content);
					}else{
						if(response.gstr2invoice != null){
							if(response.gstr2invoice.billedtoname == '' || response.gstr2invoice.billedtoname == null){billedtoname = "";}else{billedtoname=response.gstr2invoice.billedtoname;}
							content += '<tr><td class="text-left">'+response.gstr2invoice.invtype+'</td><td class="text-left">'+billedtoname+'</td><td class="text-left">'+response.gstr2invoice.invoiceno+'</td><td>'+formatDate(response.gstr2invoice.dateofinvoice)+'</td><td class="text-left">'+response.gstr2invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.gstr2invoice.totaltax).toFixed(2))+'</td><td class="text-left">'+mstatus+'</td></tr>';
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

	function sendSuppliermessage(retType){
		$('#emailerrormsg').addClass('d-none');
		$('#supplier_fullnameerrormsg').addClass('d-none');
		$('#meassagedetail').html('We thank you for your valuable contribution to our business .&#13;&#10;&#13;&#10;You must be aware that for availing GST credit on inward supplies, one of the conditions is that the invoices should appear in GSTR-2A. {Ref. Sec 16 & rule 36 of the CGST}.&#13;&#10; As a part of this compliance procedure, we have reconciled our books/ purchase register with Form GSTR-2A as made available to us by GSTN (generated based on GSTR-1 filed by you) for the period 2019. &#13;&#10;&#13;&#10;In this relation, we have observed non-reporting / mismatch of below invoices in GSTR-1 by you:');
		$('#subject').val("Mismatch In GSTR2A Invoices");
		$.ajax({
			type: "POST",
			url: contextPath+"/invoice_details/"+retType,
			async: false,
			cache: false,
			data: JSON.stringify(sendMsgArray),
			contentType: 'application/json',
			success : function(response){
				if(response.invoice.length >0){
					if(dbSendMsgTable){
						dbSendMsgTable.clear().destroy();
					}	
				}
				
				var count=1;
				for(var i=0;i<response.invoice.length;i++){
					if(count <= 10){
						$('#sendMsgsBody').append('<tr><td><div class="checkbox" index="'+response.invoice[i].revchargetype+'"><label><input type="checkbox" class="send_msgInv" id="invFilterGSTR2'+response.invoice[i].revchargetype+'" onClick="updateMsgSelection(\''+response.invoice[i].revchargetype+'\', \'GSTR2\',this)" checked/><i class="helper"></i></label></div></td><td class="text-left">'+response.invoice[i].referenceNumber+'</td><td class="text-left">'+response.invoice[i].invoiceno+'</td><td class="text-left">'+formatDate(response.invoice[i].dateofinvoice)+'</td><td class="text-left">'+response.invoice[i].b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.invoice[i].totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.invoice[i].totalamount).toFixed(2))+'</td></tr>');
						sendAllMsgsArray.push(response.invoice[i].revchargetype);
					}else{
						$('#sendMsgsBody').append('<tr><td><div class="checkbox" index="'+response.invoice[i].revchargetype+'"><label><input type="checkbox" class="send_msgInv" id="invFilterGSTR2'+response.invoice[i].revchargetype+'" onClick="updateMsgSelection(\''+response.invoice[i].revchargetype+'\', \'GSTR2\',this)"/><i class="helper"></i></label></div></td><td class="text-left">'+response.invoice[i].referenceNumber+'</td><td class="text-left">'+response.invoice[i].invoiceno+'</td><td class="text-left">'+formatDate(response.invoice[i].dateofinvoice)+'</td><td class="text-left">'+response.invoice[i].b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(response.invoice[i].totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(response.invoice[i].totalamount).toFixed(2))+'</td></tr>');
					}
				count++;
				if(response.invoice[i].toAddr2 == '' || response.invoice[i].toAddr2 == null ){
							$("#supplier_emailid").prop("readonly",false);
							$('#emailerrormsg').removeClass('d-none');
					}else{
							$("#supplier_emailid").val(response.invoice[i].toAddr2);
							//$("#supplier_emailid").prop("readonly",true);
					}
					if(response.invoice[i].toAddr1 == '' || response.invoice[i].toAddr1 == null ){
						$('#supplier_fullnameerrormsg').removeClass('d-none');
						$('#emailerrormsg').addClass('d-none');
					}else{
						$('#supplier_fullname').val(response.invoice[i].toAddr1);
					}
				}
				dbSendMsgTable = $('#msgDetailTable').DataTable({
					"dom": '<"toolbar">frtip',
					"pageLength": 10,
					"paging": true,
					"searching": false,
					"responsive": true,
					"order": [[2,'desc']]
				});
				$('#msgDetailTable_text').html('<h6>Attached Invoices</h6>');
			},error:function(err){
				
			}
			
		});
	}
	
	function updateMsgSelection(id,retType,chkBox){
		if(chkBox.checked) {
			sendAllMsgsArray.push(id);	
		}else {
			var mArray=new Array();
			sendAllMsgsArray.forEach(function(inv) {
				if(inv != id) {
					mArray.push(inv);
				}
			});
			sendAllMsgsArray = mArray;
		}
	}
	function updateMainMsgSection(chkBox){
		if(chkBox.checked) {
			$('.send_msgInv').prop("checked",false);
			sendAllMsgsArray=new Array();
		}else{
			
		}
	}
	function sendMessages(clientid,userid){
		var emailid=$('#supplier_emailid').val();
		var ccEmailids=$('.sup_ccemailids').val();
		var messagesubject=$('#subject').val();
		var meassagedetail=$('#meassagedetail').val();
		var suppliername = $('#supplier_fullname').val();
		//var clientid='<c:out value="${client.id}"/>';
		if(emailid.indexOf(',') != -1){
			emailid = emailid.split(',');
			for (var i = 0; i < emailid.length; i++) {
				supEmailids.push(emailid[i]);
			}
		}else{
			supEmailids.push(emailid);
		}
		if(ccEmailids.indexOf(',') != -1){
			ccEmailids = ccEmailids.split(',');
			for (var i = 0; i < ccEmailids.length; i++) {
				supCCEmailids.push(ccEmailids[i]);
			}
		}else{
			if(ccEmailids != ''){
				supCCEmailids.push(ccEmailids);
			}	
		}
		var messagesData=new Object();
		messagesData.subject=messagesubject;
		messagesData.messages=meassagedetail;
		messagesData.suplierName=suppliername;
		messagesData.emailid=supEmailids;
		messagesData.cc=supCCEmailids;
		if(meassagedetail =="" || messagesubject == "" || emailid=="" || suppliername=="" ){
			$('#errormsg').text('Please Fill All Fields');
		}else{
				$('#errormsg').text('');
				$.ajax({
					url : contextPath+'/sendmessages/'+clientid+'/'+userid+'?invoiceIds='+sendAllMsgsArray,
					data: JSON.stringify(messagesData),
					type:"POST",
					contentType: 'application/json',
				success : function(data) {
					$('#meassagedetail').val('');
					$('#subject').val('');
					$('.successmsg').removeClass('d-none');
					$('#message_send_btn').addClass('d-none');
					$('#successmsg').text('Message Send successfully...');
					sendMsgArray = new Array();
					sendAllMsgsArray=new Array();
					supCCEmailids=new Array();
					ccEmailids=new Array();
				
				},
				error:function(dat){}
			}); 
		} 
	}
	
	function mannualMatching(clientid,monthlyOryearly){
		var returnType;
		var invoiceid;
		if(selMatchArray[0].purchaseId){
			returnType = 'Purchase Register';
			invoiceid = selMatchArray[0].purchaseId;
			$('#invhdr').html('Invoice In Purchases');
			$('#dinvhdr').html('Invoice In GSTR2A');
		}else{
			returnType = 'GSTR2';
			invoiceid = selMatchArray[0].gstrId;
			$('#invhdr').html('Invoice In GSTR2A');
			$('#dinvhdr').html('Invoice In Purchases');
		}
		
		$.ajax({
			url: contextPath+"/mannualMatchingInvoice/"+invoiceid+"/"+returnType,
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
		
		
		$.ajax({
			url: contextPath+"/mannualMatchingInvoices/"+clientid+"/"+invoiceid+"/"+returnType+"/"+month+"/"+yer+"/"+monthlyOryearly,
			async: false,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response){
				var content = '';
				
				mannulaMatching = new Array();
				mnmatch = new Array();
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
							mannulaMatching.push(fyInvoice);
							mnmatch.push(fyInvoice);
						});
					}
				}
				
				if(mannualMatchtable) {
					mannualMatchtable.clear().destroy();
				}
				if(mannulaMatching == null || mannulaMatching == undefined || mannulaMatching.length == 0) {
				} else {
					
					mannulaMatching.forEach(function(invoice) {
						content += '<tr><td><div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invManFilter'+invoice.id+'" onClick="updateMannulaMatchSelection(\''+invoice.id+'\', null, this)"/><i class="helper"></i></label></div></td><td class="text-left">'+invoice.invtype+'</td><td class="text-left">'+invoice.billedtoname+'</td><td class="text-left">'+invoice.invoiceno+'</td><td class="text-left">'+formatDate(invoice.dateofinvoice)+'</td><td class="text-left">'+invoice.b2b[0].ctin+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</td><td class="text-right">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</td></tr>';
					});
					$('#MannulaMatchInvoices').html(content); 
					$('#mannualHiddenInvoiceid').val(invoiceid);
					$('#mannualHiddenReturnType').val(returnType);		
				}
				mannualMatchtable = $('#mannualMatch_table5').DataTable({
					
					"paging": true,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
					"responsive": true,
					"ordering": true,
					"pageLength": 7,
					"language": {
						"search": "_INPUT_",
						"searchPlaceholder": "Search..."
					}
				});
			 $('#mannualMatch_table5_wrapper').css('width','100%');
			},error:function(err){
				
			}
		});
		
		
	}
	
	function viewMannualMatchedInvoices(invoiceid, returntype){
		if(returntype == 'Purchase Register'){
			$('#vinvhdr').html('Invoice In Purchases');
			$('#vdinvhdr').html('Invoice In GSTR2A');
		}else{
			$('#vinvhdr').html('Invoice In GSTR2A');
			$('#vdinvhdr').html('Invoice In Purchases');
		}
		
		$.ajax({
			url: contextPath+"/mannualMatchingInvoice/"+invoiceid+"/"+returntype,
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
			url: contextPath+"/viewMannualMatchingInvoices/"+invoiceid+"/"+returntype,
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
			},error:function(err){
				
			}
		});
	}
	
	
	
function updateMannulaMatchSelection(id, gstrId, chkBox) {
		
		var mObj=new Object();
		mObj.purchaseId = id;
		mObj.gstrId = gstrId;
		if(chkBox.checked) {
			mannualMatchArray.push(mObj);
		} else {
			var tArray=new Array();
			mannualMatchArray.forEach(function(inv) {
				if(inv.purchaseId == id) {
				} else {
					tArray.push(inv);
				}
			});
			mannualMatchArray = tArray;
		}
	}

function updateMainMannualSelection(chkBox) {
	mannualMatchArray = new Array();
	if(mannulaMatching) {
		if(chkBox.checked) {
			mannualMatchtable.rows().every(function () {
				var row = this.data();
				var index = $(row[0]).attr('index');
				row[0]='<div class="checkbox" index="'+index+'"><label><input type="checkbox" checked id="invManFilter'+index+'" onclick="updateMannulaMatchSelection(null, \''+index+'\', this)"/><i class="helper"></i></label></div>';			
				this.data(row);
				this.invalidate();
			});
			}else{
				mannualMatchtable.rows().every(function () {
					var row = this.data();
					var index = $(row[0]).attr('index');
					row[0]='<div class="checkbox" index="'+index+'"><label><input type="checkbox" id="invManFilter'+index+'" onclick="updateMannulaMatchSelection(null, \''+index+'\', this)"/><i class="helper"></i></label></div>';
					this.data(row);
					this.invalidate();
				});
			}
		mannulaMatching.forEach(function(invoice) {
			if(chkBox.checked) {
				var mObj=new Object();
				mObj.purchaseId = invoice.id;mObj.gstrId = invoice.matchingId;
				mannualMatchArray.push(mObj);
			}
		});
	}
}



function mannualMatchInvoiceFilterValues(){
	var mgstnoArr=new Array();
	var invoicenoArr=new Array();
	var invoicedateArr=new Array();
	var e = 0;
	if($('#mannualGstno').is(':checked')) {
		var manGstno = $('#manGSTIN').html();
		mgstnoArr.push(manGstno);
	}
	
	if($('#mannualInvoiceNumber').is(':checked')) {
		var manInvoiceNo = $('#manInvoiceNo').html();
		invoicenoArr.push(manInvoiceNo);
	}
	
	if($('#mannualInvoiceDate').is(':checked')) {
		var manInvoiceDate = $('#manInvoiceDate').html();
		invoicedateArr.push(manInvoiceDate);
	}
	mannualMatchInvoiceFiltera(mgstnoArr,invoicenoArr,invoicedateArr);
	
	
}

function mannualMatchInvoiceFilter(arrGSTNo, arrInvoiceNumber, arrInvoiceDate) {
	mannualMatchtable.clear();
	if(mannulaMatching.length > 0) {
		var rows = new Array();
		mannuMatch = new Array();
		mannulaMatching.forEach(function(invoice) {
			
			var creditDebit = 'credit';
				var invtype = invoice.invtype;
				if(invtype == 'Debit Note' || invtype == 'Credit Note' ){
						if(invoice.cdn[0].nt[0].ntty == 'D'){
								creditDebit = 'credit';
						}else{
							creditDebit = 'debit';
						}
				}else if(invtype == 'Credit Note(UR)' || invtype == 'Debit Note(UR)'){
							if(invoice.cdnur[0].ntty == 'D'){
									creditDebit = 'credit';
							}else{
								creditDebit = 'debit';
							}
				}else{
					creditDebit = 'debit';
				}
				rows.push([ '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invManFilter'+invoice.id+'" onclick="updateMannulaMatchSelection(\''+invoice.id+'\', null, this)"/><i class="helper"></i></label></div>', invoice.invtype, invoice.billedtoname, invoice.invoiceno,invoice.dateofinvoice,invoice.b2b[0].ctin,formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2)),formatNumber(parseFloat(invoice.totaltax).toFixed(2))]);
		});
		var index = 0, transCount=0;
		rows.forEach(function(row) {
		  if((arrGSTNo.length == 0 || $.inArray('All', arrGSTNo) >= 0 || $.inArray(row[5], arrGSTNo) >= 0)	
				  && (arrInvoiceNumber.length == 0 || $.inArray('All', arrInvoiceNumber) >= 0 || $.inArray(row[3], arrInvoiceNumber) >= 0)
				  && (arrInvoiceDate.length == 0 || $.inArray('All', arrInvoiceDate) >= 0 || $.inArray(row[4], arrInvoiceDate) >= 0)) {
			  mannualMatchtable.row.add(row);
			  var index = $(row[0]).attr('index');
			  mannulaMatching.forEach(function(invoice) {
				  if(index == invoice.id){
					  mannuMatch.push(invoice);
				  }
				});
			}
		  index++;
		});
	}else{
		$('.reconciletablee .dataTables_length').css("margin-left","0px");
	}
	if(($('#mannualGstno').is(':checked') && $('#mannualInvoiceNumber').is(':checked') && $('#mannualInvoiceDate').is(':checked')) || ($('#mannualGstno').is(':not(:checked)') && $('#mannualInvoiceNumber').is(':not(:checked)') && $('#mannualInvoiceDate').is(':not(:checked)'))){
		
	}else{
		mannulaMatching = mannuMatch;
	}
	mannualMatchtable.draw();
}

function claimallinvoices(returnType,invId){
	var type = $('#inputtype').val();
	var datePart = $('.allitcclaimdate').val().split('-');;
	var clmddate = new Date(datePart[2], parseInt(datePart[1]) - 1, datePart[0]);
	var amt =	$('#itcamount').val();
	var itcClaimedArray=ITCclaimedArray;
	if(amt != null && amt>100){
		amt = '100';
	}	
	var retType = returnType;
	if(returnType == 'Unclaimed'){
		retType = 'Purchase Register';
	}	
	var itcclaimdata=new Object();
	itcclaimdata.invIds=ITCclaimedArray;
	itcclaimdata.itctype=type;
	itcclaimdata.claimeddate=clmddate;
	itcclaimdata.itcamt=amt;
	itcclaimdata.retType='Purchase Register';
	$.ajax({
		url : contextPath+'/allitcupdt',
		type: "POST",
		data: JSON.stringify(itcclaimdata),
		contentType: 'application/json',
		success : function(response) {
			location.reload(true);
		}, 	error:function(data){
    	}	
	});
}

function updateMannualMatchData(btn,monthlyOrYearly) {
	var invoiceid = $('#mannualHiddenInvoiceid').val();
	var returnType = $('#mannualHiddenReturnType').val();
	$(btn).addClass('btn-loader');
	$.ajax({
		type: "POST",
		url: contextPath+"/mannualMatchArray/"+returnType+"/"+invoiceid,
		async: false,
		cache: false,
		data: JSON.stringify(mannualMatchArray),
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

function moreReconcileConfig(){
	window.location.href = contextPath+'/cp_upload'+commonturnOverSuffix+'/'+Paymenturlprefix+'?type=reconcileconfig'; 
}

function updateExpenseData(expense){
	if(expense.paymentDate != null){
		var invDate = new Date(expense.paymentDate);
		var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		expense.paymentDate = day + "/" + month + "/" + year;
   }else{
	   expense.paymentDate = "";
   }
}

function updateInvoiceDetails(invoice,clientAddress) {
	var totalIGST1 = 0, totalCGST1 = 0, totalSGST1 = 0, totalCESS1 = 0,  totalITCIGST1 = 0, totalITCCGST1 = 0, totalITCSGST1 = 0, totalITCCESS1 = 0, totalinvitc = 0, totalExempted = 0;
	var rettype = 'GSTR2';
	invoice.id = invoice.userid;
	invoice.invoicetype = invoice.invtype;
	invoice.subSupplyType = invoice.subSupplyType;
	invoice.transDistance = invoice.transDistance;
	if(invoice.supplyType == null){
		invoice.supplyType = '';
	}
	if(invoice.eBillNo == null){
		invoice.eBillNo = '';
	}
	if(invoice.invoiceno == null){
		invoice.invoiceno = '';
	}
	if(invoice.paymentStatus == null){
		invoice.paymentStatus = '';
	}
	if(invoice.clientAddress == null){
		invoice.clientAddress = clientAddress;
	}
	if(invoice.invoiceEcomGSTIN == null){
		invoice.invoiceEcomGSTIN = '';
	}
	if(invoice.tdstcsenable == null){
		invoice.tdstcsenable = 'false';
	}
	if(invoice.section == null){
		invoice.section = '';
	}
	if(invoice.tcstdspercentage == null){
		invoice.tcstdspercentage = 0.00;
	}
	if(invoice.tcstdsAmount == null){
		invoice.tcstdsAmount = 0.00;
	}
	if(invoice.netAmount == null){
		invoice.netAmount = 0.00;
	}
	if(invoice.revchargetype == null){
		invoice.revchargetype = 'Regular';
	}
	if(invoice.ledgerName == null){
		invoice.ledgerName = '';
	}
	if(invoice.dealerType == null){
		invoice.dealerType = '';
	}
	if(invoice.vendorName == null){
		invoice.vendorName = '';
	}
	invoice.serialnoofinvoice = invoice.invoiceno;
	if(invoice.b2b && invoice.b2b.length > 0) {
		if(invoice.b2b[0].ctin == null){
			invoice.b2b[0].ctin = '';
		}
		if(invoice.b2b[0].cfs == null){
			invoice.b2b[0].cfs = '';
		}
		invoice.cfs = invoice.b2b[0].cfs; 
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
	if(invoice.billDate != null){
		var billDate = new Date(invoice.billDate);
		var day = billDate.getDate() + "";var month = (billDate.getMonth() + 1) + "";var year = billDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.billDate = day + "/" + month + "/" + year;
	}
	if(invoice.dueDate != null){
		var dDate = new Date(invoice.dueDate);
		var day = dDate.getDate() + "";var month = (dDate.getMonth() + 1) + "";	var year = dDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dueDate = day + "/" + month + "/" + year;
	}else{invoice.dueDate = "";}
	if(invoice.dateofinvoice != null){
		var invDate = new Date(invoice.dateofinvoice);
		var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dateofinvoice = day + "/" + month + "/" + year;
	}else{invoice.dateofinvoice = "";}
	if(invoice.dateofitcClaimed != null){
		var invDate = new Date(invoice.dateofitcClaimed);
		var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";var year = invDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dateofitcClaimed = day + "/" + month + "/" + year;
	}else{invoice.dateofitcClaimed = "";}
	if(invoice.eBillDate != null){
		var eDate = new Date(invoice.eBillDate);var day = eDate.getDate() + "";var month = (eDate.getMonth() + 1) + "";var year = eDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.eBillDate = day + "/" + month + "/" + year;
	}else{invoice.eBillDate = "";}
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
	if(invoice.totaltaxableamount) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
					if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalTaxableValue += invoice.totaltaxableamount;
					}else{
						totalTaxableValue-=invoice.totaltaxableamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalTaxableValue-=invoice.totaltaxableamount;
				}else{
					totalTaxableValue+=invoice.totaltaxableamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalTaxableValue+=invoice.totaltaxableamount;
					}else{
						totalTaxableValue-=invoice.totaltaxableamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalTaxableValue-=invoice.totaltaxableamount;
					}else{
						totalTaxableValue+=invoice.totaltaxableamount;
					}
				}
				
			}else{
				totalTaxableValue+=invoice.totaltaxableamount;
			}
		}
	} else {
		invoice.totaltaxableamount = 0.00;
	}
	if(invoice.totaltax) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
					totalTax+=invoice.totaltax;
				}else{
					totalTax-=invoice.totaltax;
				}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalTax-=invoice.totaltax;
				}else{
					totalTax+=invoice.totaltax;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalTax+=invoice.totaltax;
					}else{
						totalTax-=invoice.totaltax;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalTax-=invoice.totaltax;
					}else{
						totalTax+=invoice.totaltax;
					}
				}
			}else{
				totalTax+=invoice.totaltax;
			}
		}
	} else {
		invoice.totaltax = 0.00;
	}
	if(invoice.totalamount) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalValue-=invoice.totalamount;
				}else{
					totalValue+=invoice.totalamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
					}else{
						totalValue+=invoice.totalamount;
					}
				}
			}else{
				totalValue+=invoice.totalamount;
			}
		}
	} else {
		invoice.totalamount = invoice.totaltax + invoice.totaltaxableamount;
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalValue-=invoice.totalamount;
				}else{
					totalValue+=invoice.totalamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
					}else{
						totalValue+=invoice.totalamount;
					}
				}
			}else{
				totalValue+=invoice.totalamount;
			}
		}
		
	}
	if(('B2C' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0) || ('B2CSA' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0)) {
		invoice.splyTy = invoice.b2cs[0].splyTy;
		invoice.ecommercegstin = invoice.b2cs[0].etin;
	}
	if(('Advances' == invoice.invtype && invoice.at && invoice.at.length > 0) || ('ATA' == invoice.invtype && invoice.at && invoice.at.length > 0)) {
		invoice.splyTy = invoice.at[0].splyTy;
	}else if(('Advances' == invoice.invtype && invoice.txi && invoice.txi.length > 0) || ('ATA' == invoice.invtype && invoice.txi && invoice.txi.length > 0)){
		invoice.splyTy = invoice.txi[0].splyTy;
	}
	if(('Advance Adjusted Detail' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0) || ('TXPA' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0)) {
		invoice.splyTy = invoice.txpd[0].splyTy;
		invoice.invtype = 'Adv. Adjustments';
	}
	if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
		invoice.voucherNumber = invoice.cdn[0].nt[0].inum;
		invoice.ntty = invoice.cdn[0].nt[0].ntty;
		invoice.rsn = invoice.cdn[0].nt[0].rsn;
		invoice.pGst = invoice.cdn[0].nt[0].pGst;
		if(invoice.cdn[0].cfs == null){
			invoice.cdn[0].cfs = '';
		}
		invoice.cfs = invoice.cdn[0].cfs; 
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
		
	} else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
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
	if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
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
	if(('Exports' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0) || ('EXPA' == invoice.invtype && invoice.exp && invoice.exp.length > 0 && invoice.exp[0].inv && invoice.exp[0].inv.length > 0)) {
		invoice.portcode = invoice.exp[0].inv[0].sbpcode;
		invoice.shippingBillNumber = invoice.exp[0].inv[0].sbnum;
		invoice.exportType = invoice.exp[0].expTyp;
		if(invoice.exp[0].inv[0].sbdt != null && invoice.exp[0].inv[0].sbdt != undefined){
			invoice.shippingBillDate = formatDate(invoice.exp[0].inv[0].sbdt);
		}else{
			invoice.shippingBillDate = '';
		}
	}
	if(('ISD' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0) || ('ISDA' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0)) {
		invoice.isdDocty = invoice.isd[0].doclist[0].isdDocty;
		invoice.documentNumber = invoice.isd[0].doclist[0].docnum;
		if(invoice.isd[0].doclist[0].docdt != null && invoice.isd[0].doclist[0].docdt != undefined){
			invoice.documentDate = formatDate(invoice.isd[0].doclist[0].docdt);
		}else{
			invoice.documentDate = '';
		}
	}
	if('Import Goods' == invoice.invtype && invoice.impGoods && invoice.impGoods.length > 0) {
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
	if(invoice.advOriginalInvoiceNumber == null) {
		invoice.advOriginalInvoiceNumber = 0.00;
	}
	if(invoice.advPCustname == null) {
		invoice.advPCustname = "";
	}
	if(invoice.advPInvamt == null) {
		invoice.advPInvamt = 0.00;
	}
	if(invoice.advPIgstamt == null) {
		invoice.advPIgstamt = 0.00;
	}
	if(invoice.advPCgstamt == null) {
		invoice.advPCgstamt = 0.00;
	}
	if(invoice.advPSgstamt == null) {
		invoice.advPSgstamt = 0.00;
	}
	if(invoice.VehiclListDetails){
		if(VehiclListDetails.enteredDate != null){
			var eDate = new Date(VehiclListDetails.enteredDate);var day = eDate.getDate() + "";var month = (eDate.getMonth() + 1) + "";var year = eDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			VehiclListDetails.enteredDate = day + "/" + month + "/" + year;
		}else{invoice.enteredDate = "";}
		if(VehiclListDetails.transDocDate != null){
			var tdDate = new Date(VehiclListDetails.transDocDate);var day = tdDate.getDate() + "";var month = (tdDate.getMonth() + 1) + "";var year = tdDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			VehiclListDetails.transDocDate = day + "/" + month + "/" + year;
		}else{invoice.transDocDate = "";}
		invoice.VehiclListDetails.forEach(function(VehiclListDetails) {
			VehiclListDetails.enteredDate = VehiclListDetails.enteredDate;
			VehiclListDetails.transDocDate = VehiclListDetails.transDocDate;
			VehiclListDetails.vehicleNo = VehiclListDetails.vehicleNo;
			VehiclListDetails.fromPlace = VehiclListDetails.fromPlace;
			VehiclListDetails.fromState = VehiclListDetails.fromState;
			VehiclListDetails.tripshtNo = VehiclListDetails.tripshtNo;
			VehiclListDetails.userGSTINTransin = VehiclListDetails.userGSTINTransin;
			VehiclListDetails.transMode = VehiclListDetails.transMode;
		if(VehiclListDetails.transDocNo){VehiclListDetails.transDocNo = VehiclListDetails.transDocNo;}
		VehiclListDetails.groupNo = VehiclListDetails.groupNo;
		});
	}
	if(invoice.items) {
		invoice.items.forEach(function(item) {
			if(item.rate == null) {
				if(item.igstrate) {
					item.rate = item.igstrate;
				} else if(item.cgstrate) {
					item.rate = 2*item.cgstrate;
				}else{
					item.rate = 0;
				}
				
			}
			if(item.hsn) {
				item.code = item.hsn;
				if(item.hsn.indexOf(':') > 0) {
					item.hsn=item.hsn.substring(0,item.hsn.indexOf(' :'));
				}
			}
			if(item.igstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdna && invoice.cdna.length > 0 && invoice.cdna[0].nt && invoice.cdna[0].nt.length > 0)) {
						if('CDNA' == invoice.invtype){
							if(invoice.cdna[0].nt[0].ntty == 'D'){
								totalIGST += item.igstamount;
								totalIGST1 += item.igstamount;
							}else{
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}
						}else{
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalIGST += item.igstamount;
								totalIGST1 += item.igstamount;
							}else{
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}
						}
					}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
						if('CDNA' == invoice.invtype){
							if(invoice.cdnra[0].nt[0].ntty == 'C'){
								totalIGST -= item.igstamount;
								totalIGST1 -= item.igstamount;
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}
						}else{
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalIGST -= item.igstamount;
								totalIGST1 -= item.igstamount;
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}else{
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}
						}
					}else{
						totalIGST+=item.igstamount;
						totalIGST1 +=item.igstamount;
					}
				}
			} else {
				item.igstamount = 0.00;
			}
			if(item.cgstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdna && invoice.cdna.length > 0 && invoice.cdna[0].nt && invoice.cdna[0].nt.length > 0)) {
						if('CDNA' == invoice.invtype){
							if(invoice.cdna[0].nt[0].ntty == 'D'){
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}
						}else{
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}
						}
					}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
						if('CDNA' == invoice.invtype){
							if(invoice.cdnra[0].nt[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}
						}else{
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}
						}
					}else{
						totalCGST+=item.cgstamount;
						totalCGST1 +=item.cgstamount;
					}
				}
			} else {
				item.cgstamount = 0.00;
			}
			if(item.sgstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdna && invoice.cdna.length > 0 && invoice.cdna[0].nt && invoice.cdna[0].nt.length > 0)) {
							if('CDNA' == invoice.invtype){	
								if(invoice.cdna[0].nt[0].ntty == 'D'){
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}else{
									totalSGST-=item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}
							}else{
								if(invoice.cdn[0].nt[0].ntty == 'D'){
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}else{
									totalSGST-=item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}	
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
							if('CDNA' == invoice.invtype){
								if(invoice.cdnra[0].nt[0].ntty == 'C'){
									totalSGST -= item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}else{
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}
							}else{
								if(invoice.cdnr[0].nt[0].ntty == 'C'){
									totalSGST -= item.sgstamount;
									totalSGST1 -=item.sgstamount;
								}else{
									totalSGST+=item.sgstamount;
									totalSGST1 +=item.sgstamount;
								}
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalSGST-=item.sgstamount;
								totalSGST1 -=item.sgstamount;
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}
						}
						}else{
							totalSGST+=item.sgstamount;
							totalSGST1 +=item.sgstamount;
						}
				}
			} else {
				item.sgstamount = 0.00;
			}
			if(item.cessamount) {
				if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdna && invoice.cdna.length > 0 && invoice.cdna[0].nt && invoice.cdna[0].nt.length > 0)) {
							if('CDNA' == invoice.invtype){
								if(invoice.cdna[0].nt[0].ntty == 'D'){
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}
							}else{
								if(invoice.cdn[0].nt[0].ntty == 'D'){
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
							if('CDNA' == invoice.invtype){
								if(invoice.cdnra[0].nt[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}
							}else{
								if(invoice.cdnr[0].nt[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}
							}
						}else{
							totalCESS+=item.cessamount;
							totalCESS1 +=item.cessamount;
						}
				}
			} else {
				item.cessamount = 0.00;
			}
			if(item.exmepted != null && item.quantity != null){
				if(invoice.gstStatus != 'CANCELLED'){
					
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnra && invoice.cdnra.length > 0 && invoice.cdnra[0].nt && invoice.cdnra[0].nt.length > 0)){
						if('CDNA' == invoice.invtype){
							if(invoice.cdnra[0].nt[0].ntty == 'C'){
								totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
								totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							}else{
								totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
								totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							}
						}else{
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
								totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							}else{
								totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
								totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							}
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(invoice.cdnur[0].ntty == 'C'){
							totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
						}else{
							totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						}
					}else{
						totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
					}
					
					
				}
			}
			
			if(item.discount == null) {
				item.discount = 0.00;
			}
			if(item.advreceived == null) {
				item.advreceived = 0.00;
			}
			if(item.advReceiptNo == null){
				item.advReceiptNo = "";
			}
			if(item.advReceiptDate == null){
				item.advReceiptDate = "";
			}
			if(item.advStateName == null){
				item.advStateName = "";
			}
			if(item.advReceivedAmount == null){
				item.advReceivedAmount = 0.00;
			}
			if(item.advAdjustableAmount == null){
				item.advAdjustableAmount = 0.00;
			}
			if(item.advadjustedAmount == null){
				item.advadjustedAmount = 0.00;
			}
			if(item.igstavltax) {
				totalinvitc+= item.igstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCIGST+=item.igstavltax;
				totalITCIGST1 +=item.igstavltax;
				}
			} else {
				item.igstavltax = 0.00;
			}
			if(item.cgstavltax) {
				totalinvitc+= item.cgstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCCGST+=item.cgstavltax;
				totalITCCGST1 +=item.cgstavltax;
				}
			} else {
				item.cgstavltax = 0.00;
			}
			if(item.sgstavltax) {
				totalinvitc+= item.sgstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCSGST+=item.sgstavltax;
				totalITCSGST1 +=item.sgstavltax;
				}
			} else {
				item.sgstavltax = 0.00;
			}
			if(item.cessavltax) {
				totalinvitc+= item.cessavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCCESS+=item.cessavltax;
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
			if(invoice.invtype != 'Advances' && invoice.invtype != 'Adv. Adjustments' && invoice.invtype != 'ISD' && invoice.invtype != 'ITC Reversal' && invoice.invtype != 'Nil Supplies'){
				totalITC+=invoice.totalitc;
			}
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

function otpExpiryCheck(){
	otpExpirycheck = "";
	$.ajax({
		url : _getContextPath()+'/otpexpiry/'+clientId,
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response) {
			otpExpirycheck = response;
		}
	});
}

function mannualMatchingInv(clientid,monthlyOryearly){
	var returnType;
	var invoiceid;
	if(selMatchArray[0].purchaseId){
		returnType = 'Purchase Register';
		invoiceid = selMatchArray[0].purchaseId;
		$('#invhdr').html('Invoice In Purchases');
		$('#dinvhdr').html('Invoice In GSTR2A');
	}else{
		returnType = 'GSTR2';
		invoiceid = selMatchArray[0].gstrId;
		$('#invhdr').html('Invoice In GSTR2A');
		$('#dinvhdr').html('Invoice In Purchases');
	}
	if(mannualMatchtable){
		mannualMatchtable.clear();
		mannualMatchtable.destroy();
	}
	$.ajax({
		url: contextPath+"/mannualMatchingInvoice/"+invoiceid+"/"+returnType,
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
	
	
	var pUrl = contextPath+"/getInvoicesForMannualMatch/"+clientid+"/"+invoiceid+"/"+returnType+"/"+month+"/"+yer+"/"+monthlyOryearly;
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
		'columns': getInvManualMatchColumns(),
		'columnDefs' : getManualMatchInvColumnDefs()
	});
	$('#mannualHiddenInvoiceid').val(invoiceid);
	$('#mannualHiddenReturnType').val(returnType);
	 $('#mannualMatch_table5_wrapper').css('width','100%');
	 $('#message_send_btn').attr("onclick","updateMannualMatchData(this,'monthly')");
	
}

function getInvManualMatchColumns(){
	var chkBx = {data: function ( data, type, row ) {
      	 			return '<div class="checkbox nottoedit" index="'+data.userid+'"><label><input type="checkbox" id="invManFilter'+data.userid+'" onClick="updateMannulaMatchSelection(\''+data.userid+'\', null,this)"/><i class="helper"></i></label></div>';
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

function updateAnxInvoiceDetails(invoice){
	var totalIGST1 = 0, totalCGST1 = 0, totalSGST1 = 0, totalCESS1 = 0,  totalITCIGST1 = 0, totalITCCGST1 = 0, totalITCSGST1 = 0, totalITCCESS1 = 0, totalinvitc = 0, totalExempted = 0;
	var rettype = 'ANX1';var etype = '${eType}';
	invoice.id = invoice.userid;
	if(invoice.invtype == 'DE'){
		invoice.invoicetype = 'B2B';
		invoice.invTyp = 'DE';
	}else if(invoice.invtype == 'SEWP'){
		invoice.invoicetype = 'B2B';
		invoice.invTyp = 'SEWP';
	}else if(invoice.invtype == 'SEWOP'){
		invoice.invoicetype = 'B2B';
		invoice.invTyp = 'SEWOP';
	}else if(invoice.invtype == 'EXPWP' || invoice.invtype == 'EXPWOP'){
		invoice.invoicetype = 'Exports';
	}else{
		invoice.invoicetype = invoice.invtype;
	}
	
	invoice.subSupplyType = invoice.subSupplyType;
	if(invoice.actualDist){
		invoice.transDistance = invoice.actualDist;
	}
	invoice.transDistance = invoice.transDistance;
	if(invoice.supplyType == null){
		invoice.supplyType = '';
	}
	if(invoice.eBillNo == null){
		invoice.eBillNo = '';
	}
	if(invoice.invoiceno == null){
		invoice.invoiceno = '';
	}
	if(invoice.paymentStatus == null){
		invoice.paymentStatus = '';
	}
	if(invoice.clientAddress == null){
		invoice.clientAddress = '${client.address}';
	}
	if(invoice.invoiceEcomGSTIN == null){
		invoice.invoiceEcomGSTIN = '';
	}
	if(invoice.tdstcsenable == null){
		invoice.tdstcsenable = 'false';
	}
	if(invoice.section == null){
		invoice.section = '';
	}
	if(invoice.tcstdspercentage == null){
		invoice.tcstdspercentage = 0.00;
	}
	if(invoice.tcstdsAmount == null){
		invoice.tcstdsAmount = 0.00;
	}
	if(invoice.netAmount == null){
		invoice.netAmount = 0.00;
	}
	if(invoice.revchargetype == null){
		invoice.revchargetype = 'Regular';
	}
	if(invoice.ledgerName == null){
		invoice.ledgerName = '';
	}
	if(invoice.dealerType == null){
		invoice.dealerType = '';
	}
	if(invoice.vehicleType == null){
		invoice.vehicleType = 'R';
	}
	if(invoice.vendorName == null){
		invoice.vendorName = '';
	}
	invoice.serialnoofinvoice = invoice.invoiceno;
		if(invoice.anxb2b && invoice.anxb2b.length > 0){
			if(invoice.anxb2b[0].ctin != undefined){
				invoice.billedtogstin = invoice.anxb2b[0].ctin;
			}
		}else if(invoice.de && invoice.de.length > 0){
			if(invoice.de[0].ctin != undefined){
				invoice.billedtogstin = invoice.de[0].ctin;
			}
		}else if(invoice.sezwp && invoice.sezwp.length > 0){
			if(invoice.sezwp[0].ctin != undefined){
				invoice.billedtogstin = invoice.sezwp[0].ctin;
			}
		}else if(invoice.sezwop && invoice.sezwop.length > 0){
			if(invoice.sezwop[0].ctin != undefined){
				invoice.billedtogstin = invoice.sezwop[0].ctin;
			}
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
	if(invoice.billDate != null){
		var billDate = new Date(invoice.billDate);
		var day = billDate.getDate() + "";var month = (billDate.getMonth() + 1) + "";var year = billDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.billDate = day + "/" + month + "/" + year;
	}
	if(invoice.dateofinvoice != null){
		var invDate = new Date(invoice.dateofinvoice);
		var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dateofinvoice = day + "/" + month + "/" + year;
	}else{invoice.dateofinvoice = "";}
	if(invoice.dateofitcClaimed != null){
		var invDate = new Date(invoice.dateofitcClaimed);
		var day = invDate.getDate() + "";var month = (invDate.getMonth() + 1) + "";var year = invDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.dateofitcClaimed = day + "/" + month + "/" + year;
	}else{invoice.dateofitcClaimed = "";}
	if(invoice.eBillDate != null){
		var eDate = new Date(invoice.eBillDate);var day = eDate.getDate() + "";var month = (eDate.getMonth() + 1) + "";var year = eDate.getFullYear() + "";
		day = checkZero(day);month = checkZero(month);year = checkZero(year);
		invoice.eBillDate = day + "/" + month + "/" + year;
	}else{invoice.eBillDate = "";}
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
	if(invoice.totaltaxableamount) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
					if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalTaxableValue += invoice.totaltaxableamount;
					}else{
						totalTaxableValue-=invoice.totaltaxableamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalTaxableValue-=invoice.totaltaxableamount;
				}else{
					totalTaxableValue+=invoice.totaltaxableamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalTaxableValue+=invoice.totaltaxableamount;
					}else{
						totalTaxableValue-=invoice.totaltaxableamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalTaxableValue-=invoice.totaltaxableamount;
					}else{
						totalTaxableValue+=invoice.totaltaxableamount;
					}
				}
				
			}else{
				totalTaxableValue+=invoice.totaltaxableamount;
			}
		}
	} else {
		invoice.totaltaxableamount = 0.00;
	}
	if(invoice.totaltax) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
					totalTax+=invoice.totaltax;
				}else{
					totalTax-=invoice.totaltax;
				}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalTax-=invoice.totaltax;
				}else{
					totalTax+=invoice.totaltax;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalTax+=invoice.totaltax;
					}else{
						totalTax-=invoice.totaltax;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalTax-=invoice.totaltax;
					}else{
						totalTax+=invoice.totaltax;
					}
				}
			}else{
				totalTax+=invoice.totaltax;
			}
		}
	} else {
		invoice.totaltax = 0.00;
	}
	if(invoice.totalamount) {
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalValue-=invoice.totalamount;
				}else{
					totalValue+=invoice.totalamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
					}else{
						totalValue+=invoice.totalamount;
					}
				}
			}else{
				totalValue+=invoice.totalamount;
			}
		}
	} else {
		invoice.totalamount = invoice.totaltax + invoice.totaltaxableamount;
		if(invoice.gstStatus != 'CANCELLED'){
			if(('Credit/Debit Notes' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
				if(invoice.cdn[0].nt[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
			}else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
				if(invoice.cdnr[0].nt[0].ntty == 'C'){
					totalValue-=invoice.totalamount;
				}else{
					totalValue+=invoice.totalamount;
				}
			}else if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
				if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
					if(invoice.cdnur[0].ntty == 'D'){
						totalValue+=invoice.totalamount;
					}else{
						totalValue-=invoice.totalamount;
					}
				}else{
					if(invoice.cdnur[0].ntty == 'C'){
						totalValue-=invoice.totalamount;
					}else{
						totalValue+=invoice.totalamount;
					}
				}
			}else{
				totalValue+=invoice.totalamount;
			}
		}
		
	}
	if(('B2C' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0) || ('B2CSA' == invoice.invtype && invoice.b2cs && invoice.b2cs.length > 0)) {
		invoice.splyTy = invoice.b2cs[0].splyTy;
		invoice.ecommercegstin = invoice.b2cs[0].etin;
	}
	if(('Advances' == invoice.invtype && invoice.at && invoice.at.length > 0) || ('ATA' == invoice.invtype && invoice.at && invoice.at.length > 0)) {
		invoice.splyTy = invoice.at[0].splyTy;
	}else if(('Advances' == invoice.invtype && invoice.txi && invoice.txi.length > 0) || ('ATA' == invoice.invtype && invoice.txi && invoice.txi.length > 0)){
		invoice.splyTy = invoice.txi[0].splyTy;
	}
	if(('Advance Adjusted Detail' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0) || ('TXPA' == invoice.invtype && invoice.txpd && invoice.txpd.length > 0)) {
		invoice.splyTy = invoice.txpd[0].splyTy;
		invoice.invtype = 'Adv. Adjustments';
	}
	if('Credit/Debit Notes' == invoice.invtype && invoice.anxb2b && invoice.anxb2b.length > 0) {
		invoice.voucherNumber = invoice.anxb2b[0].docs[0].doc.num;
		invoice.ntty = invoice.anxb2b[0].docs[0].doctyp;
		if(invoice.anxb2b[0].docs[0].doc.dt != null){
			invoice.voucherDate = invoice.anxb2b[0].docs[0].doc.dt;
		}else{
			invoice.voucherDate = '';
		}
		
		
	} else if(('Credit/Debit Notes' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('CDNA' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
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
	if(('Credit/Debit Note for Unregistered Taxpayers' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('CDNURA' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
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
	if('EXPWP' == invoice.invtype && invoice.expwp && invoice.expwp.length > 0 ) {
		invoice.portcode = invoice.expwp[0].sb.pcode;
		invoice.shippingBillNumber = invoice.expwp[0].sb.num;
		invoice.exportType = 'WPAY';
		if(invoice.expwp[0].sb.dt != null && invoice.expwp[0].sb.dt != undefined){
			invoice.shippingBillDate = formatDate(invoice.expwp[0].sb.dt);
		}else{
			invoice.shippingBillDate = '';
		}
	}if('EXPWOP' == invoice.invtype && invoice.expwop && invoice.expwop.length > 0 ) {
		invoice.portcode = invoice.expwop[0].sb.pcode;
		invoice.shippingBillNumber = invoice.expwop[0].sb.num;
		invoice.exportType = 'WOPAY';
		if(invoice.expwop[0].sb.dt != null && invoice.expwop[0].sb.dt != undefined){
			invoice.shippingBillDate = formatDate(invoice.expwop[0].sb.dt);
		}else{
			invoice.shippingBillDate = '';
		}
	}
	if(('ISD' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0) || ('<%=MasterGSTConstants.ISD%>' == invoice.invtype && invoice.isd && invoice.isd.length > 0 && invoice.isd[0].doclist && invoice.isd[0].doclist.length > 0)) {
		invoice.isdDocty = invoice.isd[0].doclist[0].isdDocty;
		invoice.documentNumber = invoice.isd[0].doclist[0].docnum;
		if(invoice.isd[0].doclist[0].docdt != null && invoice.isd[0].doclist[0].docdt != undefined){
			invoice.documentDate = formatDate(invoice.isd[0].doclist[0].docdt);
		}else{
			invoice.documentDate = '';
		}
	}
	if('Import Goods' == invoice.invtype && invoice.impGoods && invoice.impGoods.length > 0) {
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
	if(invoice.advOriginalInvoiceNumber == null) {
		invoice.advOriginalInvoiceNumber = 0.00;
	}
	if(invoice.advPCustname == null) {
		invoice.advPCustname = "";
	}
	if(invoice.advPInvamt == null) {
		invoice.advPInvamt = 0.00;
	}
	if(invoice.advPIgstamt == null) {
		invoice.advPIgstamt = 0.00;
	}
	if(invoice.advPCgstamt == null) {
		invoice.advPCgstamt = 0.00;
	}
	if(invoice.advPSgstamt == null) {
		invoice.advPSgstamt = 0.00;
	}
	if(invoice.VehiclListDetails){
		if(VehiclListDetails.enteredDate != null){
			var eDate = new Date(VehiclListDetails.enteredDate);var day = eDate.getDate() + "";var month = (eDate.getMonth() + 1) + "";var year = eDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			VehiclListDetails.enteredDate = day + "/" + month + "/" + year;
		}else{invoice.enteredDate = "";}
		if(VehiclListDetails.transDocDate != null){
			var tdDate = new Date(VehiclListDetails.transDocDate);var day = tdDate.getDate() + "";var month = (tdDate.getMonth() + 1) + "";var year = tdDate.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			VehiclListDetails.transDocDate = day + "/" + month + "/" + year;
		}else{invoice.transDocDate = "";}
		invoice.VehiclListDetails.forEach(function(VehiclListDetails) {
			VehiclListDetails.enteredDate = VehiclListDetails.enteredDate;
			VehiclListDetails.transDocDate = VehiclListDetails.transDocDate;
			VehiclListDetails.vehicleNo = VehiclListDetails.vehicleNo;
			VehiclListDetails.fromPlace = VehiclListDetails.fromPlace;
			VehiclListDetails.fromState = VehiclListDetails.fromState;
			VehiclListDetails.tripshtNo = VehiclListDetails.tripshtNo;
			VehiclListDetails.userGSTINTransin = VehiclListDetails.userGSTINTransin;
			VehiclListDetails.transMode = VehiclListDetails.transMode;
		if(VehiclListDetails.transDocNo){VehiclListDetails.transDocNo = VehiclListDetails.transDocNo;}
		VehiclListDetails.groupNo = VehiclListDetails.groupNo;
		});
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
					item.hsn=item.hsn.substring(0,item.hsn.indexOf(' :'));
				}
			}
			if(item.igstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
						if(invoice.cdn[0].nt[0].ntty == 'D'){
							totalIGST += item.igstamount;
							totalIGST1 += item.igstamount;
						}else{
							totalIGST-=item.igstamount;
							totalIGST1 -=item.igstamount;
						}
					}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
						if(invoice.cdnr[0].nt[0].ntty == 'C'){
							totalIGST -= item.igstamount;
							totalIGST1 -= item.igstamount;
						}else{
							totalIGST+=item.igstamount;
							totalIGST1 +=item.igstamount;
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}else{
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalIGST-=item.igstamount;
								totalIGST1 -=item.igstamount;
							}else{
								totalIGST+=item.igstamount;
								totalIGST1 +=item.igstamount;
							}
						}
					}else{
						totalIGST+=item.igstamount;
						totalIGST1 +=item.igstamount;
					}
				}
			} else {
				item.igstamount = 0.00;
			}
			if(item.cgstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
						if(invoice.cdn[0].nt[0].ntty == 'D'){
							totalCGST+=item.cgstamount;
							totalCGST1 +=item.cgstamount;
						}else{
							totalCGST-=item.cgstamount;
							totalCGST1 -=item.cgstamount;
						}
					}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
						if(invoice.cdnr[0].nt[0].ntty == 'C'){
							totalCGST-=item.cgstamount;
							totalCGST1 -=item.cgstamount;
						}else{
							totalCGST+=item.cgstamount;
							totalCGST1 +=item.cgstamount;
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}else{
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalCGST-=item.cgstamount;
								totalCGST1 -=item.cgstamount;
							}else{
								totalCGST+=item.cgstamount;
								totalCGST1 +=item.cgstamount;
							}
						}
					}else{
						totalCGST+=item.cgstamount;
						totalCGST1 +=item.cgstamount;
					}
				}
			} else {
				item.cgstamount = 0.00;
			}
			if(item.sgstamount) {
				if(invoice.gstStatus != 'CANCELLED'){
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}else{
								totalSGST-=item.sgstamount;
								totalSGST1 -=item.sgstamount;
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalSGST -= item.sgstamount;
								totalSGST1 -=item.sgstamount;
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
							if(invoice.cdnur[0].ntty == 'D'){
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}
						}else{
							if(invoice.cdnur[0].ntty == 'C'){
								totalSGST-=item.sgstamount;
								totalSGST1 -=item.sgstamount;
							}else{
								totalSGST+=item.sgstamount;
								totalSGST1 +=item.sgstamount;
							}
						}
						}else{
							totalSGST+=item.sgstamount;
							totalSGST1 +=item.sgstamount;
						}
				}
			} else {
				item.sgstamount = 0.00;
			}
			if(item.cessamount) {
				if(invoice.gstStatus != 'CANCELLED'){
						if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
							if(invoice.cdn[0].nt[0].ntty == 'D'){
								totalCESS+=item.cessamount;
								totalCESS1 +=item.cessamount;
							}else{
								totalCESS-=item.cessamount;
								totalCESS1 -=item.cessamount;
							}
						}else if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
							if(invoice.cdnr[0].nt[0].ntty == 'C'){
								totalCESS-=item.cessamount;
								totalCESS1 -=item.cessamount;
							}else{
								totalCESS+=item.cessamount;
								totalCESS1 +=item.cessamount;
							}
						}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
							if(rettype == 'GSTR2' || rettype == 'Purchase Register'){
								if(invoice.cdnur[0].ntty == 'D'){
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}else{
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}
							}else{
								if(invoice.cdnur[0].ntty == 'C'){
									totalCESS-=item.cessamount;
									totalCESS1 -=item.cessamount;
								}else{
									totalCESS+=item.cessamount;
									totalCESS1 +=item.cessamount;
								}
							}
						}else{
							totalCESS+=item.cessamount;
							totalCESS1 +=item.cessamount;
						}
				}
			} else {
				item.cessamount = 0.00;
			}
			if(item.exmepted != null && item.quantity != null){
				if(invoice.gstStatus != 'CANCELLED'){
					
					if((('Credit Note' == invoice.invtype || 'Debit Note' == invoice.invtype) && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)){
						if(invoice.cdnr[0].nt[0].ntty == 'C'){
							totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
						}else{
							totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						}
					}else if((('Credit Note(UR)' == invoice.invtype || 'Debit Note(UR)' == invoice.invtype) && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)){
						if(invoice.cdnur[0].ntty == 'C'){
							totalExemptedValue = totalExemptedValue-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
							totalExempted == totalExempted-((parseFloat(item.quantity))*(parseFloat(item.exmepted)));
						}else{
							totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
							totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						}
					}else{
						totalExemptedValue += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
						totalExempted += (parseFloat(item.quantity))*(parseFloat(item.exmepted));
					}
					
					
				}
			}
			
			if(item.discount == null) {
				item.discount = 0.00;
			}
			if(item.advreceived == null) {
				item.advreceived = 0.00;
			}
			if(item.advReceiptNo == null){
				item.advReceiptNo = "";
			}
			if(item.advReceiptDate == null){
				item.advReceiptDate = "";
			}
			if(item.advStateName == null){
				item.advStateName = "";
			}
			if(item.advReceivedAmount == null){
				item.advReceivedAmount = 0.00;
			}
			if(item.advAdjustableAmount == null){
				item.advAdjustableAmount = 0.00;
			}
			if(item.advadjustedAmount == null){
				item.advadjustedAmount = 0.00;
			}
			if(item.igstavltax) {
				totalinvitc+= item.igstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCIGST+=item.igstavltax;
				totalITCIGST1 +=item.igstavltax;
				}
			} else {
				item.igstavltax = 0.00;
			}
			if(item.cgstavltax) {
				totalinvitc+= item.cgstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCCGST+=item.cgstavltax;
				totalITCCGST1 +=item.cgstavltax;
				}
			} else {
				item.cgstavltax = 0.00;
			}
			if(item.sgstavltax) {
				totalinvitc+= item.sgstavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCSGST+=item.sgstavltax;
				totalITCSGST1 +=item.sgstavltax;
				}
			} else {
				item.sgstavltax = 0.00;
			}
			if(item.cessavltax) {
				totalinvitc+= item.cessavltax;
				if(invoice.gstStatus != 'CANCELLED'){
				totalITCCESS+=item.cessavltax;
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
			if(invoice.invtype != 'Advances' && invoice.invtype != 'Adv. Adjustments' && invoice.invtype != 'ISD' && invoice.invtype != 'ITC Reversal' && invoice.invtype != 'Nil Supplies'){
				totalITC+=invoice.totalitc;
			}
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





function updateMismatchDataabcd() {
	 var data = [];
	var content = '', redClass=' color-red', roundOffClass=' color-submit', matchedArray=new Array(), matchedidnotingstr2aArray=new Array(); var matchingarray = new Array(); var mannualMatchArray = new Array();
	var mismatchedtotalInvoices = 0,matchedtotalInvoices = 0,othermonthsmatchedtotalInvoices = 0,notinGstr2atotalInvoices = 0,notinPurchasetotalInvoices = 0,invoicenomismatchtotalInvoices = 0,taxmismatchtotalInvoices = 0,invoiceValuemismatchtotalInvoices = 0, rooundoffmismatchtotalInvoices = 0,gstnomismatchtotalInvoices = 0,roundoffmatechedtotalInvoices = 0,invoiceDateMismatchedInvoices = 0,probablematchedtotalInvoices = 0,mannualMatchedInvoices = 0;
	MismatchArray = new Array();
	if(invoiceArray['prFY'] == null || invoiceArray['prFY'] == undefined || invoiceArray['prFY'].length == 0) {
		if(invoiceArray['g2FYMatched'] == null || invoiceArray['g2FYMatched'] == undefined || invoiceArray['g2FYMatched'].length == 0) {
		} else {
			invoiceArray['g2FYMatched'].forEach(function(gstr2) {
				if(gstr2.b2b == null || gstr2.b2b.length == 0) {
					gstr2.b2b = new Array();
					var tObj = new Object();
					tObj.ctin = '';
					gstr2.b2b.push(tObj);
				}
				if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == gstr2.invtype && gstr2.cdn && gstr2.cdn.length > 0 && gstr2.cdn[0].nt && gstr2.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == gstr2.invtype && gstr2.cdn && gstr2.cdn.length > 0 && gstr2.cdn[0].nt && gstr2.cdn[0].nt.length > 0)) {
		
		if(gstr2.cdn[0].nt[0].ntty == 'C'){
			gstr2.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			gstr2.invtype = 'Debit Note';
		}
		
	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == gstr2.invtype && gstr2.cdnr && gstr2.cdnr.length > 0 && gstr2.cdnr[0].nt && gstr2.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == gstr2.invtype && gstr2.cdnr && gstr2.cdnr.length > 0 && gstr2.cdnr[0].nt && gstr2.cdnr[0].nt.length > 0)) {
		
		if(gstr2.cdnr[0].nt[0].ntty == 'C'){
			gstr2.invtype = 'Credit Note';
		}else if(gstr2.cdnr[0].nt[0].ntty == 'D'){
			gstr2.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == gstr2.invtype && gstr2.cdnur && gstr2.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == gstr2.invtype && gstr2.cdnur && gstr2.cdnur.length > 0)) {
		if(gstr2.cdnur[0].ntty == 'C'){
			gstr2.invtype = 'Credit Note(UR)';
		}else if(gstr2.cdnur[0].ntty == 'D'){
			gstr2.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == gstr2.invtype)){
		gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
	}
				gstr2.mstatus='Not In Purchases';
				gstr2.gfp='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
				gstr2.ginvno='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
				gstr2.ginvdate='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
				gstr2.ggstno='<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
				gstr2.ginvvalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
				gstr2.gtaxablevalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
				gstr2.gtotaltax='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
				gstr2.gbranch='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.branch+'</div></div>';
				gstr2.gcomments='<div><a href="#" onclick="supComments(\''+gstr2.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
				notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
				if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
					gstr2.invoiceCustomerIdAndBilledToName = '';
				}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
					gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
				}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
					gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
				}
				gstr2.gstrid = gstr2.id;
				gstr2.id = undefined;
				MismatchArray.push(gstr2);
				
				
				var chkbx = '<div class="checkbox" index="'+gstr2.gstrid+'"><label><input type="checkbox" name="invMFilter'+gstr2.gstrid+'" onClick="updateMisMatchSelection(null, \''+gstr2.gstrid+'\', \''+gstr2.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
				var bk2a = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div>';
				var fp = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
				var invtype = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invtype+'</div>';
				var suppliername = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.billedtoname+'</div>';
				var fullname = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.fullname+'</div>';
				var vertical = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.vertical+'</div>';
				var customerid = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invoiceCustomerIdAndBilledToName+'</div>';
				var invno = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
				var invdt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
				var invgstin = '<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
				var invamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
				var invtaxableamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
				var invtaxamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
				var branch = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.branch+'</div>';
				var misstatus = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')" index="mismatchStatus'+gstr2.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+gstr2.mstatus+'">'+gstr2.mstatus+'</span></div>';
				var comments = '<a href="#" onclick="supComments(\''+gstr2.gstrid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a>';
				
				data.push( [ chkbx, bk2a, invtype,suppliername, fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
				
				
			});
		}
	} else {
		invoiceArray['prFY'].forEach(function(invoice) {
			if(invoice.matchingId) {
				matchedArray.push(invoice.matchingId);
			}
			if(invoice.b2b == null || invoice.b2b.length == 0) {
				invoice.b2b = new Array();
				var tObj = new Object();
				tObj.ctin = '';
				invoice.b2b.push(tObj);
			}
			if(invoiceArray['g2FYMatched'] == null || invoiceArray['g2FYMatched'] == undefined || invoiceArray['g2FYMatched'].length == 0) {
				matchedidnotingstr2aArray.push(invoice.id);
				if(('Import Goods' == invoice.invtype)){
					invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
				}
				invoice.mstatus='Not In GSTR 2A';
				invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
				invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gbranch='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
				notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
				
				if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
					invoice.invoiceCustomerIdAndBilledToName = '';
				}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
					invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
				}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
					invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
				}
				if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
		
		if(invoice.cdn[0].nt[0].ntty == 'C'){
			invoice.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			invoice.invtype = 'Debit Note';
		}
		
	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
		
		if(invoice.cdnr[0].nt[0].ntty == 'C'){
			invoice.invtype = 'Credit Note';
		}else if(invoice.cdnr[0].nt[0].ntty == 'D'){
			invoice.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
		if(invoice.cdnur[0].ntty == 'C'){
			invoice.invtype = 'Credit Note(UR)';
		}else if(invoice.cdnur[0].ntty == 'D'){
			invoice.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == invoice.invtype)){
		invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
	}
				MismatchArray.push(invoice);
				var chkbx = '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
				var bk2a = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
				var fp = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtype = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</div>';
				var suppliername = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</div>';
				var fullname = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.fullname+'</div>';
				var vertical = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.vertical+'</div>';
				var customerid = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invoiceCustomerIdAndBilledToName+'</div>';
				var invno = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
				var invdt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
				var invgstin = '<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></div></span>';
				var invamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtaxableamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtaxamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var branch = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</div>';
				var misstatus = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')" index="mismatchStatus'+invoice.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></div>';
				var comments = '<a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a>';
				
				data.push( [ chkbx, bk2a, invtype,suppliername, fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
			} else {
				if(invoice.matchingStatus != null && invoice.matchingStatus != '' && invoice.matchingId != null && invoice.matchingId != '') {
					invoiceArray['g2FYMatched'].forEach(function(gstr2) {
						if(gstr2.b2b == null || gstr2.b2b.length == 0) {
							gstr2.b2b = new Array();
							var tObj = new Object();
							tObj.ctin = '';
							gstr2.b2b.push(tObj);
						}
						if(gstr2.id == invoice.matchingId) {
							if($.inArray(gstr2.id, matchingarray) == -1) {
								matchedidnotingstr2aArray.push(invoice.id);
							if(invoice.matchingStatus != 'Matched' && invoice.matchingStatus != 'Round Off Matched' && invoice.matchingStatus != 'Matched In Other Months' && invoice.matchingStatus != 'Probable Matched') {
								var tStatus = 'Mismatched';
								if((invoice.invoiceno == gstr2.invoiceno)
									&& (invoice.dateofinvoice == gstr2.dateofinvoice)
									&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
									&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
									&& (invoice.totaltax == gstr2.totaltax)) {
									matchedtotalInvoices = matchedtotalInvoices+1;
									tStatus = 'Matched';
								} else if((invoice.invoiceno == gstr2.invoiceno)
									&& (invoice.dateofinvoice == gstr2.dateofinvoice)
									&& (invoice.b2b[0].ctin != gstr2.b2b[0].ctin)
									&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
									&& (invoice.totaltax == gstr2.totaltax)) {
									tStatus = 'GST No Mismatched';
									gstnomismatchtotalInvoices = gstnomismatchtotalInvoices+1;
								} else if((invoice.invoiceno == gstr2.invoiceno)
									&& (invoice.dateofinvoice == gstr2.dateofinvoice)
									&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
									&& (invoice.totaltaxableamount != gstr2.totaltaxableamount)
									&& (invoice.totaltax == gstr2.totaltax)) {
									tStatus = 'Invoice Value Mismatched';
									invoiceValuemismatchtotalInvoices = invoiceValuemismatchtotalInvoices+1;
								} else if((invoice.invoiceno == gstr2.invoiceno)
									&& (invoice.dateofinvoice == gstr2.dateofinvoice)
									&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
									&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
									&& (invoice.totaltax != gstr2.totaltax)) {
									tStatus = 'Tax Mismatched';
									taxmismatchtotalInvoices = taxmismatchtotalInvoices+1;
								} else if((invoice.invoiceno != gstr2.invoiceno)
									&& (invoice.dateofinvoice == gstr2.dateofinvoice)
									&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
									&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
									&& (invoice.totaltax == gstr2.totaltax)) {
									tStatus = 'Invoice No Mismatched';
									invoicenomismatchtotalInvoices = invoicenomismatchtotalInvoices+1;
								} else if((invoice.invoiceno == gstr2.invoiceno)
										&& (invoice.dateofinvoice != gstr2.dateofinvoice)
										&& (invoice.b2b[0].ctin == gstr2.b2b[0].ctin)
										&& (invoice.totaltaxableamount == gstr2.totaltaxableamount)
										&& (invoice.totaltax == gstr2.totaltax)) {
										tStatus = 'Invoice Date Mismatched';
										invoiceDateMismatchedInvoices = invoiceDateMismatchedInvoices+1;
								}else {
									mismatchedtotalInvoices = mismatchedtotalInvoices+1;
								}
								invoice.mstatus=tStatus;
							} else {
								if(invoice.matchingStatus == 'Matched'){
									matchedtotalInvoices = matchedtotalInvoices+1;
								}else if(invoice.matchingStatus == 'Matched In Other Months'){
									othermonthsmatchedtotalInvoices = othermonthsmatchedtotalInvoices+1;
								}else if(invoice.matchingStatus == 'Round Off Matched'){
									roundoffmatechedtotalInvoices = roundoffmatechedtotalInvoices+1;
								}else{
									probablematchedtotalInvoices = probablematchedtotalInvoices+1;
								}
								invoice.mstatus=invoice.matchingStatus;
							}
							matchedArray.push(gstr2.id);
							matchingarray.push(gstr2.id);
							if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
								invoice.invoiceCustomerIdAndBilledToName = '';
							}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
								invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
							}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
								invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
							}
							
							var chkbx = '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', \''+invoice.matchingId+'\', \''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
							var bk2a = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
							var fp = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1">'+invoice.fp+'</div><div class="tdline_2">'+gstr2.fp+'</div></div>';
							var invtype = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</div>';
							var suppliername = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</div>';
							var fullname = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.fullname+'</div>';
							var vertical = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.vertical+'</div>';
							var customerid = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invoiceCustomerIdAndBilledToName+'</div>';
							
							var invno = '';
							var invdt = '';
							var invgstin = '';
							var invamt = '';
							var invtaxableamt = '';
							var invtaxamt = '';
							var branch = '';
							var misstatus = '';
							var comments = '';
							if(('Import Goods' == invoice.invtype)){
								invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
								gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
							}
							if(invoice.mstatus == 'Probable Matched'){
								invno = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.invoiceno == gstr2.invoiceno)?'':roundOffClass)+'">'+invoice.invoiceno+'</div><div class="tdline_2'+((invoice.invoiceno == gstr2.invoiceno)?'':roundOffClass)+'">'+gstr2.invoiceno+'</div></div>';
							}else{
								invno = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.invoiceno == gstr2.invoiceno)?'':redClass)+'">'+invoice.invoiceno+'</div><div class="tdline_2'+((invoice.invoiceno == gstr2.invoiceno)?'':redClass)+'">'+gstr2.invoiceno+'</div></div>';
							}
							if(invoice.mstatus == 'Round Off Matched'){
								invdt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':roundOffClass)+'">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':roundOffClass)+'">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
							}else{
								invdt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':redClass)+'">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':redClass)+'">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
							}
							invgstin = '<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.b2b[0].ctin == gstr2.b2b[0].ctin)?'':redClass)+'">'+invoice.b2b[0].ctin+'</div><div class="tdline_2'+((invoice.b2b[0].ctin == gstr2.b2b[0].ctin)?'':redClass)+'">'+gstr2.b2b[0].ctin+'</div></div></span>';
							if(invoice.mstatus == 'Round Off Matched' || invoice.mstatus == 'Probable Matched'){
								invamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totalamount == gstr2.totalamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totalamount == gstr2.totalamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
								invtaxableamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
								invtaxamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltax == gstr2.totaltax)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltax == gstr2.totaltax)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
							}else{
								invamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totalamount == gstr2.totalamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totalamount == gstr2.totalamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
								invtaxableamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
								invtaxamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltax == gstr2.totaltax)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltax == gstr2.totaltax)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
							}
							branch = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</div>';
							misstatus = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')" index="mismatchStatus'+invoice.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></div>';
							comments = '<a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a>';
							data.push( [ chkbx, bk2a, invtype,suppliername, fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
							invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1">'+invoice.fp+'</div><div class="tdline_2">'+gstr2.fp+'</div></div>';
							if(invoice.mstatus == 'Probable Matched'){
								invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.invoiceno == gstr2.invoiceno)?'':roundOffClass)+'">'+invoice.invoiceno+'</div><div class="tdline_2'+((invoice.invoiceno == gstr2.invoiceno)?'':roundOffClass)+'">'+gstr2.invoiceno+'</div></div>';
							}else{
								invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.invoiceno == gstr2.invoiceno)?'':redClass)+'">'+invoice.invoiceno+'</div><div class="tdline_2'+((invoice.invoiceno == gstr2.invoiceno)?'':redClass)+'">'+gstr2.invoiceno+'</div></div>';
							}
							if(invoice.mstatus == 'Round Off Matched'){
								invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':roundOffClass)+'">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':roundOffClass)+'">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
							}else{
								invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':redClass)+'">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2'+((invoice.dateofinvoice == gstr2.dateofinvoice)?'':redClass)+'">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
							}
							invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.b2b[0].ctin == gstr2.b2b[0].ctin)?'':redClass)+'">'+invoice.b2b[0].ctin+'</div><div class="tdline_2'+((invoice.b2b[0].ctin == gstr2.b2b[0].ctin)?'':redClass)+'">'+gstr2.b2b[0].ctin+'</div></div></span>';
							if(invoice.mstatus == 'Round Off Matched' || invoice.mstatus == 'Probable Matched'){
								invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totalamount == gstr2.totalamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totalamount == gstr2.totalamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
								invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
								invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltax == gstr2.totaltax)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltax == gstr2.totaltax)?'':roundOffClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
							}else{
								invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totalamount == gstr2.totalamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totalamount == gstr2.totalamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
								invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltaxableamount == gstr2.totaltaxableamount)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
								invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.totaltax == gstr2.totaltax)?'':redClass)+' misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2'+((invoice.totaltax == gstr2.totaltax)?'':redClass)+' misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
							}
							invoice.gbranch='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1'+((invoice.branch == gstr2.branch)?'':redClass)+'">'+invoice.branch+'</div><div class="tdline_2'+((invoice.branch == gstr2.branch)?'':redClass)+'">'+gstr2.branch+'</div></div>';
							invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
							invoice.gstrid = invoice.matchingId;
							MismatchArray.push(invoice);
						}
					}
					});
				} else if(invoice.matchingStatus == null || invoice.matchingStatus == '' || invoice.matchingId == null || invoice.matchingId == '') {
					matchedidnotingstr2aArray.push(invoice.id);
					if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
						invoice.invoiceCustomerIdAndBilledToName = '';
					}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
						invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
					}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
						invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
					}
					if(('Import Goods' == invoice.invtype)){
						invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
					}
					invoice.mstatus='Not In GSTR 2A';
					invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
					invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gfullname='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
					MismatchArray.push(invoice);
					
					var chkbx = '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
					var bk2a = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
					var fp = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
					var invtype = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</div>';
					var suppliername = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</div>';
					var fullname = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.fullname+'</div>';
					var vertical = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.vertical+'</div>';
					var customerid = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invoiceCustomerIdAndBilledToName+'</div>';
					var invno = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
					var invdt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
					var invgstin = '<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></div></span>';
					var invamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					var invtaxableamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					var invtaxamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					var branch = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</div>';
					var misstatus = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')" index="mismatchStatus'+invoice.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></div>';
					var comments = '<a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a>';
					data.push( [ chkbx, bk2a, invtype,suppliername, fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
				}
			}
		});
		invoiceArray['g2FYMatched'].forEach(function(gstr2) {
			if(gstr2.b2b == null || gstr2.b2b.length == 0) {
				gstr2.b2b = new Array();
				var tObj = new Object();
				tObj.ctin = '';
				gstr2.b2b.push(tObj);
			}
			if($.inArray(gstr2.id, matchedArray) == -1) {
				if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
					gstr2.invoiceCustomerIdAndBilledToName = '';
				}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
					gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
				}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
					gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
				}
				if(('Import Goods' == gstr2.invtype)){
					gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
				}
				gstr2.mstatus='Not In Purchases';
				gstr2.gfp='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
				gstr2.ginvno='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
				gstr2.ginvdate='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
				gstr2.ggstno='<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
				gstr2.ginvvalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
				gstr2.gtaxablevalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
				gstr2.gtotaltax='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
				gstr2.gbranch='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.branch+'</div></div>';
				gstr2.gcomments='<div><a href="#" onclick="supComments(\''+gstr2.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
				gstr2.gstrid = gstr2.id;
				gstr2.id = undefined;
				notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
				MismatchArray.push(gstr2);
				
				var chkbx = '<div class="checkbox" index="'+gstr2.gstrid+'"><label><input type="checkbox" name="invMFilter'+gstr2.gstrid+'" onClick="updateMisMatchSelection(null, \''+gstr2.gstrid+'\', \''+gstr2.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
				var bk2a = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
				var fp = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
				var invtype = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invtype+'</div>';
				var suppliername = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.billedtoname+'</div>';
				var fullname = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.fullname+'</div>';
				var vertical = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.vertical+'</div>';
				var customerid = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invoiceCustomerIdAndBilledToName+'</div>';
				var invno = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
				var invdt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
				var invgstin = '<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
				var invamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
				var invtaxableamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
				var invtaxamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
				var branch = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.branch+'</div>';
				var misstatus = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')" index="mismatchStatus'+gstr2.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+gstr2.mstatus+'">'+gstr2.mstatus+'</span></div>';
				var comments = '<a href="#" onclick="supComments(\''+gstr2.gstrid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a>';
				data.push( [ chkbx, bk2a,invtype, suppliername, fp,fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
				
			}
		});

		invoiceArray['prFY'].forEach(function(invoice) {
			
			if($.inArray(invoice.id, matchedidnotingstr2aArray) == -1) {
				if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
					invoice.invoiceCustomerIdAndBilledToName = '';
				}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
					invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
				}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
					invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
				}
				if(('Import Goods' == invoice.invtype)){
					invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
				}
				invoice.mstatus='Not In GSTR 2A';
				invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
				invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gfullname='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
				notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
				MismatchArray.push(invoice);
				
				var chkbx = '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
				var bk2a = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
				var fp = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtype = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</div>';
				var suppliername = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</div>';
				var fullname = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.fullname+'</div>';
				var vertical = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.vertical+'</div>';
				var customerid = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invoiceCustomerIdAndBilledToName+'</div>';
				var invno = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
				var invdt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
				var invgstin = '<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></div></span>';
				var invamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtaxableamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtaxamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var branch = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</div>';
				var misstatus = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')" index="mismatchStatus'+invoice.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></div>';
				var comments = '<a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a>';
				data.push( [ chkbx, bk2a,invtype, suppliername,fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
			}
		});
		
		
	}
	if(invoiceArray['gPMannualFYMatched'] == null || invoiceArray['gPMannualFYMatched'] == undefined || invoiceArray['gPMannualFYMatched'].length == 0) {
		if(invoiceArray['g2MannualFYMatched'] == null || invoiceArray['g2MannualFYMatched'] == undefined || invoiceArray['g2MannualFYMatched'].length == 0) {
		} else {
			invoiceArray['g2MannualFYMatched'].forEach(function(gstr2) {
				if(gstr2.b2b == null || gstr2.b2b.length == 0) {
					gstr2.b2b = new Array();
					var tObj = new Object();
					tObj.ctin = '';
					gstr2.b2b.push(tObj);
				}
				if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == gstr2.invtype && gstr2.cdn && gstr2.cdn.length > 0 && gstr2.cdn[0].nt && gstr2.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == gstr2.invtype && gstr2.cdn && gstr2.cdn.length > 0 && gstr2.cdn[0].nt && gstr2.cdn[0].nt.length > 0)) {
		
		if(gstr2.cdn[0].nt[0].ntty == 'C'){
			gstr2.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			gstr2.invtype = 'Debit Note';
		}
		
	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == gstr2.invtype && gstr2.cdnr && gstr2.cdnr.length > 0 && gstr2.cdnr[0].nt && gstr2.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == gstr2.invtype && gstr2.cdnr && gstr2.cdnr.length > 0 && gstr2.cdnr[0].nt && gstr2.cdnr[0].nt.length > 0)) {
		
		if(gstr2.cdnr[0].nt[0].ntty == 'C'){
			gstr2.invtype = 'Credit Note';
		}else if(gstr2.cdnr[0].nt[0].ntty == 'D'){
			gstr2.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == gstr2.invtype && gstr2.cdnur && gstr2.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == gstr2.invtype && gstr2.cdnur && gstr2.cdnur.length > 0)) {
		if(gstr2.cdnur[0].ntty == 'C'){
			gstr2.invtype = 'Credit Note(UR)';
		}else if(gstr2.cdnur[0].ntty == 'D'){
			gstr2.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == gstr2.invtype)){
		gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
	}
				gstr2.mstatus='Not In Purchases';
				gstr2.gfp='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
				gstr2.ginvno='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
				gstr2.ginvdate='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
				gstr2.ggstno='<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
				gstr2.ginvvalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
				gstr2.gtaxablevalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
				gstr2.gtotaltax='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red misindformat">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
				gstr2.gbranch='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.branch+'</div></div>';
				gstr2.gcomments='<div><a href="#" onclick="supComments(\''+gstr2.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
				notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
				if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
					gstr2.invoiceCustomerIdAndBilledToName = '';
				}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
					gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
				}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
					gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
				}
				gstr2.gstrid = gstr2.id;
				gstr2.id = undefined;
				MismatchArray.push(gstr2);
				
				var chkbx = '<div class="checkbox" index="'+gstr2.gstrid+'"><label><input type="checkbox" name="invMFilter'+gstr2.gstrid+'" onClick="updateMisMatchSelection(null, \''+gstr2.gstrid+'\', \''+gstr2.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
				var bk2a = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
				var fp = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
				var invtype = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invtype+'</div>';
				var suppliername = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.billedtoname+'</div>';
				var fullname = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.fullname+'</div>';
				var vertical = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.vertical+'</div>';
				var customerid = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invoiceCustomerIdAndBilledToName+'</div>';
				var invno = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
				var invdt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
				var invgstin = '<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
				var invamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
				var invtaxableamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
				var invtaxamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
				var branch = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.branch+'</div>';
				var misstatus = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')" index="mismatchStatus'+gstr2.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+gstr2.mstatus+'">'+gstr2.mstatus+'</span></div>';
				var comments = '<a href="#" onclick="supComments(\''+gstr2.gstrid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a>';
				data.push( [ chkbx, bk2a,invtype, suppliername,fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
				
			});
		}
	
	}else{
		invoiceArray['gPMannualFYMatched'].forEach(function(invoice) {
			if(invoice.id) {
				mannualMatchArray.push(invoice.id);
			}
		});
		
		invoiceArray['gPMannualFYMatched'].forEach(function(invoice) {
			if(invoice.b2b == null || invoice.b2b.length == 0) {
				invoice.b2b = new Array();
				var tObj = new Object();
				tObj.ctin = '';
				invoice.b2b.push(tObj);
			}
			if(invoiceArray['g2MannualFYMatched'] == null || invoiceArray['g2MannualFYMatched'] == undefined || invoiceArray['g2MannualFYMatched'].length == 0) {
				if(('Import Goods' == invoice.invtype)){
					invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
				}
				invoice.mstatus='Not In GSTR 2A';
				invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
				invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gbranch='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
				invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
				notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
				
				if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
					invoice.invoiceCustomerIdAndBilledToName = '';
				}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
					invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
				}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
					invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
				}
				if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdn && invoice.cdn.length > 0 && invoice.cdn[0].nt && invoice.cdn[0].nt.length > 0)) {
		
		if(invoice.cdn[0].nt[0].ntty == 'C'){
			invoice.invtype = 'Credit Note';
		}else if(invoice.cdn[0].nt[0].ntty == 'D'){
			invoice.invtype = 'Debit Note';
		}
		
	} else if(('<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0) || ('<%=MasterGSTConstants.CDNA%>' == invoice.invtype && invoice.cdnr && invoice.cdnr.length > 0 && invoice.cdnr[0].nt && invoice.cdnr[0].nt.length > 0)) {
		
		if(invoice.cdnr[0].nt[0].ntty == 'C'){
			invoice.invtype = 'Credit Note';
		}else if(invoice.cdnr[0].nt[0].ntty == 'D'){
			invoice.invtype = 'Debit Note';
		}
	}
	if(('<%=MasterGSTConstants.CDNUR%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0) || ('<%=MasterGSTConstants.CDNURA%>' == invoice.invtype && invoice.cdnur && invoice.cdnur.length > 0)) {
		if(invoice.cdnur[0].ntty == 'C'){
			invoice.invtype = 'Credit Note(UR)';
		}else if(invoice.cdnur[0].ntty == 'D'){
			invoice.invtype = 'Debit Note(UR)';
		}
	}
	if(('Import Goods' == invoice.invtype)){
		invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
	}	
				MismatchArray.push(invoice);
				
				var chkbx = '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
				var bk2a = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
				var fp = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtype = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</div>';
				var suppliername = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</div>';
				var fullname = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.fullname+'</div>';
				var vertical = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.vertical+'</div>';
				var customerid = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invoiceCustomerIdAndBilledToName+'</div>';
				var invno = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
				var invdt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
				var invgstin = '<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></div></span>';
				var invamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtaxableamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var invtaxamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
				var branch = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</div>';
				var misstatus = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')" index="mismatchStatus'+invoice.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></div>';
				var comments = '<a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a>';
				data.push( [ chkbx, bk2a, invtype,suppliername,fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
			} else {
				if(invoice.matchingStatus != null && invoice.matchingStatus != '') {
					invoiceArray['g2MannualFYMatched'].forEach(function(gstr2) {
						if(gstr2.b2b == null || gstr2.b2b.length == 0) {
							gstr2.b2b = new Array();
							var tObj = new Object();
							tObj.ctin = '';
							gstr2.b2b.push(tObj);
						}
		
						if(gstr2.id == invoice.matchingId) {
							
							if($.inArray(gstr2.id, matchingarray) == -1) {
								invoice.mstatus=invoice.matchingStatus;
							mannualMatchArray.push(gstr2.id);
							matchingarray.push(gstr2.id);
							if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
								invoice.invoiceCustomerIdAndBilledToName = '';
							}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
								invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
							}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
								invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
							}
							if(('Import Goods' == invoice.invtype)){
								invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
							}
							var chkbx = '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', \''+invoice.matchingId+'\', \''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
							var bk2a = '<div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div>';
							var fp = '<div class="tdline_1">'+invoice.fp+'</div><div class="tdline_2">-</div>';
							var invtype = ''+invoice.invtype+'';
							var suppliername = ''+invoice.billedtoname+'';
							var fullname = ''+invoice.fullname+'';
							var vertical = ''+invoice.vertical+'';
							var customerid = ''+invoice.invoiceCustomerIdAndBilledToName+'';
							var invno = '<div class="tdline_1">'+invoice.invoiceno+'</div><div class="tdline_2">-</div>';
							var invdt = '<div class="tdline_1">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2">-</div>';
							var invgstin = '<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1">'+invoice.b2b[0].ctin+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal" onClick="viewMannualMatchedInvoices(\''+invoice.id+'\',\'Purchase Register\')">Mannualy Matched with Multiple Invoices</a></div></span>';
							var invamt = '<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
							var invtaxableamt = '<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
							var invtaxamt = '<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
							var branch = ''+invoice.branch+'';
							var misstatus = '<span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span>';
							var comments = '<a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a>';
							data.push( [ chkbx, bk2a,invtype, suppliername, fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
				
							
							
							invoice.gfp='<div class="tdline_1">'+invoice.fp+'</div><div class="tdline_2">-</div>';
							invoice.ginvno='<div class="tdline_1">'+invoice.invoiceno+'</div><div class="tdline_2">-</div>';
							invoice.ginvdate='<div class="tdline_1">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2">-</div>';
							invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div class="tdline_1">'+invoice.b2b[0].ctin+'</div><div class="tdline_2"><a href="#" data-toggle="modal" data-target="#viewMannualMatchModal" onClick="viewMannualMatchedInvoices(\''+invoice.id+'\',\'Purchase Register\')">Mannualy Matched with Multiple Invoices</a></div></span>';
							invoice.ginvvalue='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
							invoice.gtaxablevalue='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
							invoice.gtotaltax='<div class="tdline_1 misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 misindformat">-</div>';
							invoice.gbranch='<div class="tdline_1">'+invoice.branch+'</div><div class="tdline_2">-</div>';
							invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
							invoice.gstrid = invoice.matchingId;
							MismatchArray.push(invoice);
							mannualMatchedInvoices = mannualMatchedInvoices+1;
						}
					}
					});
				} else if(invoice.matchingStatus == null || invoice.matchingStatus == '') {
					if((invoice.billedtoname == '' && invoice.invoiceCustomerId == '') || (invoice.billedtoname == null && invoice.invoiceCustomerId == null)) {
						invoice.invoiceCustomerIdAndBilledToName = '';
					}else if((invoice.billedtoname != null && invoice.invoiceCustomerId == null) || (invoice.billedtoname != '' && invoice.invoiceCustomerId == '')) {
						invoice.invoiceCustomerIdAndBilledToName =invoice.billedtoname;
					}else if((invoice.billedtoname != null || invoice.billedtoname != '') && (invoice.invoiceCustomerId != null || invoice.invoiceCustomerId != '')) {
						invoice.invoiceCustomerIdAndBilledToName = invoice.billedtoname+"("+invoice.invoiceCustomerId+")";
					}
					if(('Import Goods' == invoice.invtype)){
						invoice.b2b[0].ctin = invoice.impGoods[0].stin ? invoice.impGoods[0].stin : "";
					}
					invoice.mstatus='Not In GSTR 2A';
					invoice.gfp='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvno='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ginvdate='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.ggstno='<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2 color-red">-</div></div></span>';
					invoice.ginvvalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtaxablevalue='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gtotaltax='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red misindformat">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gbranch='<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.branch+'</div><div class="tdline_2 color-red">-</div></div>';
					invoice.gcomments='<div><a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
					notinGstr2atotalInvoices = notinGstr2atotalInvoices+1;
					MismatchArray.push(invoice);
					
					var chkbx = '<div class="checkbox" index="'+invoice.id+'"><label><input type="checkbox" name="invMFilter'+invoice.id+'" onClick="updateMisMatchSelection(\''+invoice.id+'\', null,\''+invoice.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
					var bk2a = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
					var fp = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.fp+'</div><div class="tdline_2 color-red">-</div></div>';
					var invtype = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invtype+'</div>';
					var suppliername = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.billedtoname+'</div>';
					var fullname = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.fullname+'</div>';
					var vertical = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.vertical+'</div>';
					var customerid = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.invoiceCustomerIdAndBilledToName+'</div>';
					var invno = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.invoiceno+'</div><div class="tdline_2 color-red">-</div></div>';
					var invdt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatDate(invoice.dateofinvoice)+'</div><div class="tdline_2 color-red">-</div></div>';
					var invgstin = '<span class="" index="gstinno'+invoice.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+invoice.b2b[0].ctin+'</div><div class="tdline_2">-</div></div></span>';
					var invamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totalamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					var invtaxableamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					var invtaxamt = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')"><div class="tdline_1 color-red">'+formatNumber(parseFloat(invoice.totaltax).toFixed(2))+'</div><div class="tdline_2 color-red">-</div></div>';
					var branch = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')">'+invoice.branch+'</div>';
					var misstatus = '<div onClick="showMismatchInv(\''+invoice.id+'\',\''+invoice.mstatus+'\')" index="mismatchStatus'+invoice.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+invoice.mstatus+'">'+invoice.mstatus+'</span></div>';
					var comments = '<a href="#" onclick="supComments(\''+invoice.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png"/></a>';
					data.push( [ chkbx, bk2a,invtype,suppliername, fp,fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
				}
			}
		});
		invoiceArray['g2MannualFYMatched'].forEach(function(gstr2) {
			if(gstr2.b2b == null || gstr2.b2b.length == 0) {
				gstr2.b2b = new Array();
				var tObj = new Object();
				tObj.ctin = '';
				gstr2.b2b.push(tObj);
			}
			
			if($.inArray(gstr2.id, mannualMatchArray) == -1) {
				if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
					gstr2.invoiceCustomerIdAndBilledToName = '';
				}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
					gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
				}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
					gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
				}
				if(('Import Goods' == gstr2.invtype)){
					gstr2.b2b[0].ctin = gstr2.impGoods[0].stin ? gstr2.impGoods[0].stin : "";
				}
				gstr2.mstatus="Not In Purchases";
				gstr2.gfp='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 color-red">'+gstr2.fp+'</div></div>';
				gstr2.ginvno='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 color-red">'+gstr2.invoiceno+'</div></div>';
				gstr2.ginvdate='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
				gstr2.ggstno='<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
				gstr2.ginvvalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 misindformat color-red">-</div><div class="tdline_1 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
				gstr2.gtaxablevalue='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 misindformat color-red">-</div><div class="tdline_1 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
				gstr2.gtotaltax='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 misindformat color-red">-</div><div class="tdline_1 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
				gstr2.gbranch='<div onClick="showMismatchInv(\''+gstr2.id+'\',\''+gstr2.mstatus+'\')"><div class="tdline_2 color-red">-</div><div class="tdline_1 colo-red">'+gstr2.branch+'</div></div>';
				gstr2.gcomments='<div><a href="#" onclick="supComments(\''+gstr2.id+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a></div>';
				gstr2.gstrid = gstr2.id;
				gstr2.id = undefined;
				notinPurchasetotalInvoices = notinPurchasetotalInvoices+1;
				if((gstr2.billedtoname == '' && gstr2.invoiceCustomerId == '') || (gstr2.billedtoname == null && gstr2.invoiceCustomerId == null)) {
					gstr2.invoiceCustomerIdAndBilledToName = '';
				}else if((gstr2.billedtoname != null && gstr2.invoiceCustomerId == null) || (gstr2.billedtoname != '' && gstr2.invoiceCustomerId == '')) {
					gstr2.invoiceCustomerIdAndBilledToName =gstr2.billedtoname;
				}else if((gstr2.billedtoname != null || gstr2.billedtoname != '') && (gstr2.invoiceCustomerId != null || gstr2.invoiceCustomerId != '')) {
					gstr2.invoiceCustomerIdAndBilledToName = gstr2.billedtoname+"("+gstr2.invoiceCustomerId+")";
				}
				MismatchArray.push(gstr2);
				
				var chkbx = '<div class="checkbox" index="'+gstr2.gstrid+'"><label><input type="checkbox" name="invMFilter'+gstr2.gstrid+'" onClick="updateMisMatchSelection(null, \''+gstr2.gstrid+'\', \''+gstr2.b2b[0].ctin+'\', this)"/><i class="helper"></i></label></div>';
				var bk2a = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div style="color:#359045"><span class="f-11">BOOKS</span></div><div class="color-red tdline_2"><span class="f-11">GSTR2A</span></div></div>';
				var fp = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.fp+'</div></div>';
				var invtype = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invtype+'</div>';
				var suppliername = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.billedtoname+'</div>';
				var fullname = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.fullname+'</div>';
				var vertical = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.vertical+'</div>';
				var customerid = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.invoiceCustomerIdAndBilledToName+'</div>';
				var invno = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.invoiceno+'</div></div>';
				var invdt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatDate(gstr2.dateofinvoice)+'</div></div>';
				var invgstin = '<span class="" index="gstinno'+gstr2.b2b[0].ctin+'"><div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+gstr2.b2b[0].ctin+'</div></div></span>';
				var invamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totalamount).toFixed(2))+'</div></div>';
				var invtaxableamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltaxableamount).toFixed(2))+'</div></div>';
				var invtaxamt = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')"><div class="tdline_1 color-red">-</div><div class="tdline_2 color-red">'+formatNumber(parseFloat(gstr2.totaltax).toFixed(2))+'</div></div>';
				var branch = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')">'+gstr2.branch+'</div>';
				var misstatus = '<div onClick="showMismatchInv(\''+gstr2.gstrid+'\',\''+gstr2.mstatus+'\')" index="mismatchStatus'+gstr2.mstatus+'"><span class="bluetxt f-13" index="mismatchStatus'+gstr2.mstatus+'">'+gstr2.mstatus+'</span></div>';
				var comments = '<a href="#" onclick="supComments(\''+gstr2.gstrid+'\')"><img style="width:26px;" class="cmnts_img" src="'+contextPath+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a>';
				data.push( [ chkbx, bk2a,invtype,suppliername,fp, fullname, vertical, customerid, invno, invdt, invgstin, invamt, invtaxableamt, invtaxamt, branch, misstatus, comments] );
	
				
			}
		});
		
	}
	
	
	
	$('#totalMismatchedInvoices').html(mismatchedtotalInvoices);$('#totalMatchedInvoices').html(matchedtotalInvoices);$('#totalMatchedInvoicesInOtherMonths').html(othermonthsmatchedtotalInvoices);
	$('#totalNotInGstr2AInvoices').html(notinGstr2atotalInvoices);$('#totalNotInPurchasesInvoices').html(notinPurchasetotalInvoices);
	$('#totalGSTnoMismatchedInvoices').html(gstnomismatchtotalInvoices);$('#totalInvoiceValueMismatchedInvoices').html(invoiceValuemismatchtotalInvoices);
	$('#totalTaxMismatchedInvoices').html(taxmismatchtotalInvoices);$('#totalInvoiceNoMismatchInvoices').html(invoicenomismatchtotalInvoices);$('#totalRoundoffMismatchedInvoices').html(roundoffmatechedtotalInvoices);
	$('#totalInvoiceDateMismatchedInvoices').html(invoiceDateMismatchedInvoices);$('#totalProbableMatchedInvoices').html(probablematchedtotalInvoices);$('#totalMannualMatchedInvoices').html(mannualMatchedInvoices);
	if(MismatchArray.length > 0) {
	var taxArray = new Array();
	var counts =0;
	var custnames = [];
	MismatchArray.forEach(function(invoice) {
		if(invoice.invoiceCustomerId){
			if(invoice.billedtoname) {
				if(counts == 0){
					$('#MMmultiselect4').children('option').remove();
					custnames.push(invoice.billedtoname+"("+invoice.invoiceCustomerId+")");
					$("#MMmultiselect4").append($("<option></option>").attr("value",invoice.billedtoname+"("+invoice.invoiceCustomerId+")").text(invoice.billedtoname+"("+invoice.invoiceCustomerId+")"));
				}
				if(jQuery.inArray(invoice.billedtoname+"("+invoice.invoiceCustomerId+")", custnames ) == -1){
					custnames.push(invoice.billedtoname+"("+invoice.invoiceCustomerId+")");
					$("#MMmultiselect4").append($("<option></option>").attr("value",invoice.billedtoname+"("+invoice.invoiceCustomerId+")").text(invoice.billedtoname+"("+invoice.invoiceCustomerId+")"));
				}
			}
		}else{
			if(invoice.billedtoname) {
				if(counts == 0){
					$('#MMmultiselect4').children('option').remove();
					custnames.push(invoice.billedtoname);
					$("#MMmultiselect4").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
				}
				if(jQuery.inArray(invoice.billedtoname, custnames ) == -1){
					custnames.push(invoice.billedtoname);
					$("#MMmultiselect4").append($("<option></option>").attr("value",invoice.billedtoname).text(invoice.billedtoname));
				}
			}				
		}
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
		taxArray.push([invoice.igstamount,invoice.cgstamount,invoice.sgstamount,invoice.cessamount,invoice.totaltaxableamount,invoice.totaltax,creditDebit]);
		counts++;
	});
	$("#MMmultiselect4").multiselect('rebuild');
	var index = 0, transCount=0, tIGST=0, tCGST=0, tSGST=0, tCESS=0, tTaxableAmount=0, tTotalTax=0;
	taxArray.forEach(function(row) {
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
	$('#idMMCount').html(0);
	$('#idMMIGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	$('#idMMCGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	$('#idMMSGST').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	$('#idMMCESS').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	$('#idMMTaxableVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
	$('#idMMTaxVal').html('<i class="fa fa-rupee"></i>'+formatNumber(parseFloat(0).toFixed(2)));
}
	if(mismatchTable) {
		mismatchTable.destroy();
	}
	//$('#idMisMatchBody').html(content);
	mismatchTable=$('#reconsileDbTable').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		"data":           data,
       "deferRender":    true,
		"paging": true,
		"searching": true,
		"responsive": true,
		"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
		//"order": [[8,'desc']],
		"order": [[9,'desc']],
		"columnDefs": [
			{
				//"targets": [ 4, 5,6, 10 ],
				"targets": [ 5, 6,7, 11 ],
				"visible": false,
				"searchable": true
			},
			{
				"targets": [16],	
				"class":"dt-body-right"
			},
			{
						"targets": 0,
						"orderable": false
					}
		]
	});
	$('#yearProcess').text('');
}
var gstr2bReconsileArray = new Object();
var Reconsilation_G2B_Macthed_Docid = new Array();
var Reconsilation_PR_Macthed_Docid = new Array();
var g2bManualMatchArr = new Array();
var ReconsilationMATCHED = 0;
var ReconsilationMATCHED_IN_OTHER_MONTHS = 0;
var ReconsilationROUNDOFF_MATCHED = 0;
var ReconsilationPROBABLE_MATCHED = 0;
var ReconsilationNOT_IN_PR = 0;
var ReconsilationNOT_IN_GSTR2B = 0;
var ReconsilationMISMATCHED = 0;
var ReconsilationINVOICENO_MISMATCHED = 0;
var ReconsilationTAX_MISMATCHED = 0;
var ReconsilationINVOICE_VALUE_MISMATCHED = 0;
var ReconsilationGST_NO_MISMATCHED = 0;
var ReconsilationINVOICE_DATE_MISMATCHED = 0;
var ReconsilationMANUAL_MATCHED = 0;

function loadGstr2bReconsileInvoices(fUrls, retType, month, year, clientaddress){
	var contextPath=_getContextPath();
	resetGstr2bReconsilationCounts();
	var pUrl =contextPath+'/reconsileg2binvs/'+fUrls+'/'+retType+"/"+month+'/'+year;
	$.ajax({
		url: pUrl,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(fyData) {
			Object.keys(fyData).forEach(function(rType) {
				gstr2bReconsileArray[rType] = new Array();
				var fyList = null;
				var data = JSON.parse(fyData[rType]);
				if(data && data["content"]) {
					fyList = data["content"];
				} else {
					fyList = fyData[rType];
				}
				if(JSON.parse(fyList) instanceof Array) {
					if(fyList.length > 0) {
						JSON.parse(fyList).forEach(function(fyInv){
							JSON.stringify(fyInv);
							var fyInvoice = updateInvoiceDetails(fyInv,clientaddress);
							gstr2bReconsileArray[rType].push(fyInvoice);
						});
					}
				}
			});
			updateGstr2bMismatchData();
		},
		error : function(data) {
			updateGstr2bMismatchData();
		}
	});
}

function updateGstr2bMismatchData() {
	var g2bcontent = '', redClass=' color-red', roundOffClass=' color-submit', matchedidnotingstr2aArray=new Array();
	var g2bmatchingarray = new Array(), prmatchingarray = new Array();
	var mannualMatchArray = new Array();
	var mismatchedtotalInvoices = 0,matchedtotalInvoices = 0,othermonthsmatchedtotalInvoices = 0,notinGstr2atotalInvoices = 0,notinPurchasetotalInvoices = 0,invoicenomismatchtotalInvoices = 0,taxmismatchtotalInvoices = 0,invoiceValuemismatchtotalInvoices = 0, rooundoffmismatchtotalInvoices = 0,gstnomismatchtotalInvoices = 0,roundoffmatechedtotalInvoices = 0,invoiceDateMismatchedInvoices = 0,probablematchedtotalInvoices = 0,mannualMatchedInvoices = 0;
	Gstr2bReconsilationArray = new Array();
	Gstr2bReconsilationSummaryArray = new Array();
	gstr2bReconsileArray['prInvoices'].forEach(function(prInv) {
		g2bcontent += reconsilePrInvoice(prInv);
		Gstr2bReconsilationSummaryArray.push(prInv);
	});
	gstr2bReconsileArray['g2bInvoice'].forEach(function(g2bInv) {
		g2bcontent += reconsileG2bInvoice(g2bInv);
		Gstr2bReconsilationSummaryArray.push(g2bInv);
	});
	gstr2bReconsileArray['g2bMatch'].forEach(function(g2bMtchInv) {
		if(g2bMtchInv.gstr2bMatchingId != null && g2bMtchInv.gstr2bMatchingId.length > 0){
			for(var i = 0; i < g2bMtchInv.gstr2bMatchingId.length ;i++){
				g2bmatchingarray.push(g2bMtchInv.gstr2bMatchingId);
			}
		}
	});
	gstr2bReconsileArray['prMatched'].forEach(function(prMtchInv) {
		if(prMtchInv.gstr2bMatchingId != null && prMtchInv.gstr2bMatchingId.length > 0){
			for(var i = 0; i < prMtchInv.gstr2bMatchingId.length ;i++){
				prmatchingarray.push(prMtchInv.gstr2bMatchingId);
			}
		}
	});
	gstr2bReconsileArray['g2bMatch'].forEach(function(g2bMtchInv) {
		gstr2bReconsileArray['prMatched'].forEach(function(prMtchInv) {
			if(g2bMtchInv.gstr2bMatchingId != null && g2bMtchInv.gstr2bMatchingId.length > 0){
				for(var i = 0; i < g2bMtchInv.gstr2bMatchingId.length ;i++){
					if(g2bMtchInv.gstr2bMatchingId[i] == prMtchInv.docId){
						g2bcontent += reconsileG2bPrMatchInvs(g2bMtchInv, prMtchInv);
						Reconsilation_G2B_Macthed_Docid.push(g2bMtchInv.docId);
						Reconsilation_PR_Macthed_Docid.push(prMtchInv.docId);
					}
				}
			}
		});
	});
	
	gstr2bReconsileArray['g2bMatch'].forEach(function(g2bMtchInv) {
		if($.inArray(g2bMtchInv.docId, Reconsilation_G2B_Macthed_Docid) == -1) {
			g2bcontent += reconsileG2bInvoice(g2bMtchInv);
			Gstr2bReconsilationSummaryArray.push(g2bMtchInv);
		}
	});
	
	gstr2bReconsileArray['prMatched'].forEach(function(prMtchInv) {
		if($.inArray(prMtchInv.docId, Reconsilation_PR_Macthed_Docid) == -1) {
			g2bcontent += reconsilePrInvoice(prMtchInv);
			Gstr2bReconsilationSummaryArray.push(prMtchInv);
		}
	});
	gstr2bReconsileArray['prManualMatched'].forEach(function(prManualMtchInv) {
		if(prManualMtchInv.gstr2bMatchingRsn == "PR-Multiple" || prManualMtchInv.gstr2bMatchingRsn == "PR-Single"){
			g2bcontent += reconsilePrManulMatchInvoice(prManualMtchInv);
			Gstr2bReconsilationSummaryArray.push(prManualMtchInv);
		}
	});
	gstr2bReconsileArray['g2bManualMatch'].forEach(function(g2bManualMtchInv) {
		if(g2bManualMtchInv.gstr2bMatchingRsn == "G2B-Multiple" || g2bManualMtchInv.gstr2bMatchingRsn == "G2B-Single"){
			g2bcontent += reconsileG2bManulMatchInvoice(g2bManualMtchInv);
			Gstr2bReconsilationSummaryArray.push(g2bManualMtchInv);
		}
	});
	updateGstr2bReconsilationSummary();
	updateGst2bReconsilationCounts();
	if(gstr2bReconsileTable) {
		gstr2bReconsileTable.destroy();
		//gstr2bReconsileTable.destroy();
	}
	$('#gstr2bReconsileDbTableBody').html(g2bcontent);
	gstr2bReconsileTable = $('#gstr2bReconsileDbTable').DataTable({
		"dom": '<"toolbar"f>lrtip<"clear">',
		"paging": true,
		"searching": true,
		"responsive": true,
		"orderClasses": false,
		"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
		"order": [[3,'desc']],
		"columnDefs": [
			{
				"targets": [ 12, 13, 14 ],
				"visible": false,
				"searchable": true
			},
			{
				"targets": [8, 9, 10, 15],	
				"class":"dt-body-right"
			},
			{
				"targets": 0,
				"orderable": false
			}
		]
	});
}

function invoiceviewByR2BTrDate(id,value,varRetType, varRetTypeCode, clientAddress) {
	var invviewtype = value;var retType = $('#retType').val();
	var fpinvDateInvcheck = "false";
	if(invviewtype == 'Invoice Date'){
		fpinvDateInvcheck = "false";
	}else{
		fpinvDateInvcheck = "true";
	}
	$.ajax({
		url: contextPath+"/mdfyclntBillDateInvoicesView?clientid="+clientId+"&billDateInv="+fpinvDateInvcheck,
		dataType: 'json',
		type: 'POST',
		cache: false,
		success : function(summary) {
			otherconfigdetails = summary;
			gstr2bReconsiletab(clientAddress);
		}
	});
	
}

