var invTableReportsUrl = new Object();
var invTableReports = new Object();
var invSummaryTableReportsUrl = new Object();
var invTableDwnld_invoiceUrl = new Object();
var invTableDwnld_itemUrl = new Object();
var invTableDwnld_fullUrl = new Object();
var invTableSummaryDownloadUrl = new Object();
var cpUsersResponse;
var isCpUsersResponseLoaded = false;
$(function() {
	var year=new Date().getFullYear();
	$( ".helpicon" ).hover(function() {
		$('.reportSales ').show();
	}, function() {
			$('.reportSales ').hide();
	});
	var date = new Date();
	var month = date.getMonth()+1;
	var	year = date.getFullYear();
	var day = date.getDate();
	var mnt = date.getMonth()+1;
	var yr = date.getFullYear();
	var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
	var customValue = day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr;
	var date = $('.dpMonths').datepicker({
		autoclose: true,
		viewMode: 1,
		minViewMode: 1,
		format: 'mm-yyyy'
	}).on('changeDate', function(ev) {
		month = ev.date.getMonth()+1;
		year = ev.date.getFullYear();
	});
	$('.dpCustom').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();
		mnt = ev.date.getMonth()+1;
		yr = ev.date.getFullYear();
		$('.fromtime').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
	});
	$('.dpCustom1').datepicker({
		format : "dd-mm-yyyy",
		viewMode : "days",
		minViewMode : "days"
	}).on('changeDate', function(ev) {
		day = ev.date.getDate();
		mnt = ev.date.getMonth()+1;
		yr = ev.date.getFullYear();
		$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
	});
	$('.dpMonths').datepicker('update', dateValue);
	$('.dpCustom').datepicker('update', customValue);
	$('.dpCustom1').datepicker('update', customValue);
});
function getval(sel) {
	if(sel){
		document.getElementById('filingoption').innerHTML = sel;
		if(sel == 'Today'){
			$('#group_and_client').css("right","53%");
			$('.monthely-sp,.weekly-sp,.yesterday-sp,.custom-sp,.yearly-sp').css("display", "none");
			$('.today-sp').css("display", "inline-block");
			$('.dropdown-menu.ret-type').css("left", "34%");
		}else if(sel == 'Yesterday'){
			$('#group_and_client').css("right","53%");
			$('.monthely-sp,.weekly-sp,.today-sp,.custom-sp,.yearly-sp').css("display", "none");
			$('.yesterday-sp').css("display", "inline-block");
			$('.dropdown-menu.ret-type').css("left", "34%");
		}else if(sel == 'Last Week'){
			$('#group_and_client').css("right","53%");
			$('.monthely-sp,.yesterday-sp,.today-sp,.custom-sp,.yearly-sp').css("display", "none");
			$('.weekly-sp').css("display", "inline-block");
			$('.dropdown-menu.ret-type').css("left", "34%");
		}else if (sel == 'Custom') {
			$('#group_and_client').css("right","53%");
			$('.monthely-sp,.weekly-sp,.yesterday-sp,.today-sp,.yearly-sp').css("display", "none");
			$('.custom-sp').css("display", "inline-block");
			$('.dropdown-menu.ret-type').css("left", "16%");
		} else if (sel == 'Yearly') {
			$('.monthely-sp,.weekly-sp,.yesterday-sp,.today-sp,.custom-sp').css("display", "none");
			$('.yearly-sp').css("display", "inline-block");
			$('.dropdown-menu.ret-type').css("left", "19%");
		} else {
			$('#group_and_client').css("right","47%");
			$('.monthely-sp').css("display", "inline-block");
			$('.yearly-sp,.weekly-sp,.yesterday-sp,.today-sp,.custom-sp').css("display", "none");
			$('.dropdown-menu.ret-type').css("left", "19%"); 
		}
	}
}
function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;document.getElementById('yearlyoption1').innerHTML=value;document.getElementById('yearlyoption2').innerHTML=value;
}

function auditlogDownloads(id, month, year, type,clientidsArray){
	var pUrl =_getContextPath()+'/getAuditlogsExcel/'+id+'/'+month+'/'+year+'?clientids='+clientidsArray;		
	if(type == 'Custom' || type == 'Today' || type == 'Yesterday' || type == 'Last Week'){
		//month eq fromtime and  year eq totime
		pUrl=_getContextPath()+'/getAuditlogsCustomExcel/'+id+'/'+month+'/'+year+'?clientids='+clientidsArray;
	}
	$('#auditlogexcel').attr("href",pUrl);
}

function loadauditInvTable(id, month, year, type,clientidsArray){
	var pUrl =_getContextPath()+'/getAuditlogs/'+id+'/'+month+'/'+year+'?clientids='+clientidsArray;		
	if(type == 'Custom' || type == 'Today' || type == 'Yesterday' || type == 'Last Week'){
		//month eq fromtime and  year eq totime
		pUrl=_getContextPath()+'/getAuditlogsCustom/'+id+'/'+month+'/'+year+'?clientids='+clientidsArray;
	}
	invTableReportsUrl = pUrl;
	if ($.fn.DataTable.isDataTable('#gloablReports_dataTable') ) {
		invTableReports.destroy();
	}
	invTableReports = $('#gloablReports_dataTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		 "processing": true,
		 "serverSide": true,
		 "lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
	     "ajax": {
	         url: pUrl,
	         type: 'GET',
	         contentType: 'application/json',
	         dataType: "json",
	         'dataSrc': function(resp){
	        	 resp.recordsTotal = resp.auditlogs.totalElements;
	        	 resp.recordsFiltered = resp.auditlogs.totalElements;
	        	 return resp.auditlogs.content ;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[4,'desc']],
		'columns': getInvColumns()
	});
	$('#invBodysalestable1').on('click', 'tr', function (e) {
		var data = invTableReports.row($(this)).data();
		populateElement(data.userid,'alldetails');
	} );
}
function formatDate(date) {
	if(date == null || typeof(date) === 'string' || date instanceof String) {
		return date;
	} else {
		var d = new Date(date),
			month = '' + (d.getMonth() + 1),
			day = '' + d.getDate(),
			year = d.getFullYear();
		if(month.length < 2) month = '0' + month;
		if(day.length < 2) day = '0' + day;
		
		var hours = d.getHours();
		var minutes = d.getMinutes();
		var ampm = hours >= 12 ? 'PM' : 'AM';
		hours = hours % 12;
		minutes = minutes < 10 ? '0'+minutes : minutes;
		var seconds = d.getSeconds();
		seconds = seconds < 10 ? '0'+seconds : seconds;
		var strTime = hours + ':' + minutes + ':' + seconds + ' ' + ampm;
		dt = day+'-'+month+'-'+year+' '+strTime;
		
		return dt;
	}
}
function getInvColumns(){
	var clientname = {data: function ( data, type, row ) {
						var clntname = data.clientname+'('+data.gstn+')';
						if(clntname.length > 30){
							var name = clntname.substring(0,30)+".....";
							return '<span class="text-left invoiceclk" data-toggle="tooltip" data-placement="top" title="'+clntname+'">'+name+'</span>';
						}else{
							return '<span class="text-left invoiceclk">'+clntname+'</span>';
						}
					}};
	var username = {data: function ( data, type, row ) {
	 					return '<span class="text-left invoiceclk">'+data.username+'('+data.useremail+')</span>';
	 				}};
	var desc = {data: function ( data, type, row ) {
							return '<span class="text-left invoiceclk">'+data.description+'</span>';
					 }};
	var action = {data: function ( data, type, row ) {
						return '<span class="text-center invoiceclk">'+data.action+'</span>';
		 			 }};
	var date = {data: function ( data, type, row ) {
	    			return '<span class="text-left invoiceclk">'+formatDate(data.createdDate)+'</span>';
			   }};
	return [clientname,action, desc, username,date];		
}

function applyAuditFilters(){
	var clientOptions = $('#multiselectAudit2 option:selected');
	var userOptions = $('#multiselectAudit1 option:selected');
	var actionOptions = $('#multiselectAudit3 option:selected');
	if(clientOptions.length > 0 || userOptions.length > 0 || actionOptions.length > 0){
		$('.normaltable .efilter').css("display","block");
	}else{
		$('.normaltable .efilter').css("display","none");
	}
	var filterContent="";
	var cArray = new Array();
	var uArray = new Array();
	var aArray = new Array();
	if(clientOptions.length > 0) {
		for(var i=0;i<clientOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput" >'+clientOptions[i].text+'<span data-val="'+clientOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			cArray.push(clientOptions[i].value);
		}
	}
	if(userOptions.length > 0) {
		for(var i=0;i<userOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+userOptions[i].text+'<span data-val="'+userOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			uArray.push(userOptions[i].value);
		}
	}
	
	if(actionOptions.length > 0) {
		for(var i=0;i<actionOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+actionOptions[i].value+'<span data-val="'+actionOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			aArray.push(actionOptions[i].value);
		}
	}
	$('#divAuditFilters').html(filterContent);
	reloadExpTable(cArray, uArray, aArray);
}
function reloadExpTable(cArray, uArray, aArray){
	var pUrl = invTableReportsUrl;
	var appd = '';
	if(cArray.length > 0){
		appd+='&clientname='+cArray.join(',');
	}
	if(uArray.length > 0){
		appd+='&username='+uArray.join(',');
	}
	if(aArray.length > 0){
		appd+='&action='+aArray.join(',');
	}
	pUrl += '&'+appd;
	invTableReports.ajax.url(pUrl).load();
}

function initiateAuditCallBacksForMultiSelect(){
	var multiSelDefaultVals = ['','','', '- Action -'];
	for(i=1;i<=3;i++){
		if(i == 1 || i == 2){
			continue;
		}
		multiselAuditRefresh('#multiselectAudit'+i, multiSelDefaultVals[i]);
	}
}

function multiselAuditRefresh(idval, descVal){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyAuditFilters();
		},
		onSelectAll: function(){
			applyAuditFilters();
		},
		onDeselectAll: function(){
			applyAuditFilters();
		}
	});
}

function initializeAuditRemoveAppliedFilters(){
	$('#divAuditFilters').on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		for(i=1;i<=3;i++){
			$('#multiselectAudit'+i).multiselect('deselect', [val]);
		}
		applyAuditFilters();
	});
}

function clearAuditFilter() {
	for(i=1;i<=3;i++){
		$('#multiselectAudit'+i+'.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	}
	$('#divAuditFilters').html('');
	$('.normaltable .efilter').css("display","none");
	reloadExpTable(new Array(), new Array(), new Array());
}
