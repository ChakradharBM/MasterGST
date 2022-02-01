var partnerTableUrl = new Object();
var partnerSummaryTableUrl = new Object();
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
	//salesFileName = 'MGST_Sales_Monthly_'+gstnnumber+'_'+month+year;
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
function leadsTableData(tabType){
	clearPartnerFiltersReports();
	$('.monthely-sp').css("display","inline-block");
	$('.yearly-sp').css("display","none");
	$('#daySummaryTable').css("display","block");$('#weekSummaryTable,#YaerlySummaryTable').css("display","none");
	var year=new Date().getFullYear();
	var date = new Date();
	var month = date.getMonth()+1;
	var	year = date.getFullYear();
	var day = date.getDate();
	var mnt = date.getMonth()+1;
	var yr = date.getFullYear();
	//salesFileName = 'MGST_Sales_Monthly_'+gstnnumber+'_'+month+year;
	var dateValue = ((''+month).length<2 ? '0' : '') + month + '-' + year;
	$('.dpMonths').datepicker('update', dateValue);
	$('.typeDiv').css("display","inline-block");
	var abc = $('#filing_option').html('Monthly');
	var dow = $('#type_option').html('Day Wise');
	loadPartnerSupport(tabType,mnt,yr,abc,tabType,loadPartnersInDropdown);
	$('#multiselectPartnerType').find('option').remove();
	if(tabType == "salesTab"){
		$('#salesteam').addClass("active");
		$('#partners').removeClass("active");
		$('#all_leads').removeClass("active");
		$('.generate').attr("onClick","generateData('salesTab')");
		var usersMultiSelObj = $('#multiselectPartnerType');
		var partnertype = ["Sales Team"];
		if (partnertype.length > 0) {				
			partnertype.forEach(function(partner) {
				if(partner != ''){
					usersMultiSelObj.append($("<option></option>").attr("value",partner).text(partner));
				} 
			});
		}
		loadLeadsTable('salesTab',mth,yer,"Monthly");
		loadSummaryTable('dayTable',mth,yer,"Monthly",tabType);
	}else if(tabType == "partnersTab"){
		$('#partners').addClass("active");
		$('#salesteam').removeClass("active");
		$('#all_leads').removeClass("active");
		$('.generate').attr("onClick","generateData('partnersTab')");
		var usersMultiSelObj = $('#multiselectPartnerType');
		var partnertype = ["Silver Partner","Gold Partner","Platinum Partner"];
		if (partnertype.length > 0) {				
			partnertype.forEach(function(partner) {
				if(partner != ''){
					usersMultiSelObj.append($("<option></option>").attr("value",partner).text(partner));
				} 
			});
		}
		loadLeadsTable('partnersTab',mth,yer,"Monthly");
		loadSummaryTable('dayTable',mth,yer,"Monthly",tabType);
	}else{
		$('#all_leads').addClass("active");
		$('#salesteam').removeClass("active");
		$('#partners').removeClass("active");
		$('.generate').attr("onClick","generateData('allLeadsTab')");
		var usersMultiSelObj = $('#multiselectPartnerType');
		var partnertype = ["Sales Team","Silver Partner","Gold Partner","Platinum Partner"];
		if (partnertype.length > 0) {				
			partnertype.forEach(function(partner) {
				if(partner != ''){
					usersMultiSelObj.append($("<option></option>").attr("value",partner).text(partner));
				} 
			});
		}
		loadLeadsTable('allLeadsTab',mth,yer,"Monthly");
		loadSummaryTable('dayTable',mth,yer,"Monthly",tabType);
	}
	//$('#multiselectPartnerType').multiselect('destroy');
	$('#multiselectPartnerType').multiselect('rebuild');
	initiateCallBacksForMultiSelectReports();
	initializeRemoveAppliedFilters();
}
function loadLeadsTable(tabName,month,year,reporttype){
	if(leadsTable){
		leadsTable.clear();
		leadsTable.destroy();
	}
	var pUrl =_getContextPath()+"/leadclients/"+month+"/"+year+"?tabName="+tabName+"&reportType="+reporttype;
	partnerTableUrl=pUrl;
	leadsTable = $('#leadsTable').DataTable({
	"ajax": {
        url: pUrl,
        type: 'GET'
     },
     "processing": true,
     "serverSide": true,
     "paging": true,
	 "pageLength":10,
	 "responsive": true,
	 "orderClasses": false,
	 "searching": true,
     "order": [[9,'desc']],
      dom: '<"toolbar">lBfrtip',
     'columns': getLeadsColumns(tabName)
 });

$('#leadsTable tbody').on('click', 'tr', function (e) {
	if (!$(e.target).closest('.nottoedit').length) {
		var dat = leadsTable.row($(this)).data();
		showLeadsPopuUp(dat);
	}
	}); 
}
function showLeadsPopuUp(data){
	$('#busineesModal').modal('show');
	$('#customerName').val(data.partnername);
	$('#customerEmail').val(data.partneremail);
	$('#mobileId').val(data.partnermobileno);
	$('#clienttype').val(data.clienttype);
	var estimatedCost = data.estimatedCost ? data.estimatedCost : 0.0;
	$('#estimatedCost').val(formatNumber(estimatedCost.toFixed(2)));
	$('#state').val(data.state ? data.state : '');
	$('#city').val(data.city ? data.city : '');
	$('#industryType').val(data.industryType ? data.industryType : '');
	$('#description').val(data.content);
	var needtoFollow = data.needFollowup ? data.needFollowup : '';
	if(needtoFollow == true){
		$('#followup').prop("checked",true);
		$('#followup').val("true");
		$('#followupdateDiv').css("display","block");
		$('#needFollowupdate').val(data.needFollowupdate ? data.needFollowupdate : '');
	}else{
		$('#followup').prop("checked",false);
		$('#followup').val("false");
		$('#followupdateDiv').css("display","none");
	}
	var demostatus = data.demostatus ? data.demostatus : '';
	if(demostatus == true){
		$('#demostatus').prop("checked",true);
		$('#demostatus').val("true");
	}else{
		$('#demostatus').prop("checked",false);
		$('#demostatus').val("false");
	}
	$('#productType').select2().val(data.productType).trigger('change');
}
function getLeadsColumns(tabName){
	var ptnrname = {data:  function ( data, type, row ) {
		var partnername = data.partnername ? data.partnername : "";
		return '<span class="text-left invoiceclk">'+partnername+'</span>';
		}};
	var ptnrmobile = {data:  function ( data, type, row ) {
		var partnermobileno = data.partnermobileno ? data.partnermobileno : "";
		return '<span class="text-left invoiceclk">'+partnermobileno+'</span>';
		}};
	var ptnremail = {data:  function ( data, type, row ) {
		var partneremail = data.partneremail ? data.partneremail : "";
		return '<span class="text-left invoiceclk">'+partneremail+'</span>';
		}};
	var userName = {data:  function ( data, type, row ) {
		var name = data.name ? data.name : "";
		var uname = data.fullname ? data.fullname : "";
		if(tabName == "allLeadsTab"){
			return '<span class="text-left invoiceclk">'+name+'</span>';
		}else{
			return '<span class="text-left invoiceclk">'+uname+'</span>';
		}
		}};
	var email = {data:  function ( data, type, row ) {
		var email = data.email ? data.email : "";
		return '<span class="text-left invoiceclk">'+email+'</span>';
		}};
	var mobileno = {data:  function ( data, type, row ) {
		var mobilenumber = data.mobilenumber ? data.mobilenumber : "";
		return '<span class="text-left invoiceclk">'+mobilenumber+'</span>';
		}};
	var clientType = {data:  function ( data, type, row ) {
		var clienttype = data.clienttype ? data.clienttype : "";
		var type = data.type ? data.type : "";
		if(tabName == "allLeadsTab"){
			return '<span class="text-left invoiceclk">'+clienttype+'</span>';
		}else{
			return '<span class="text-left invoiceclk">'+type+'</span>';
		}
		}};
	var estimatedcost = {data:  function ( data, type, row ) {
		var estimatedCost = data.estimatedCost ? data.estimatedCost : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(estimatedCost.toFixed(2))+'</span>';
		}};
	var subscriptioncost = {data:  function ( data, type, row ) {
		var subscriptionCost = data.subscriptionAmount ? data.subscriptionAmount : 0.0;
		return '<span class="text-left invoiceclk">'+formatNumber(subscriptionCost.toFixed(2))+'</span>';
	}};
	var createddate = {data:  function ( data, type, row ) {
		var createdDate = data.createdDate ? data.createdDate : "";
		if(createdDate != ""){
			return '<span class="text-left invoiceclk">'+(new Date(createdDate)).toLocaleDateString('en-GB')+'</span>';
		}else{
			var updatedDate = data.updatedDate ? data.updatedDate : "";
			if(updatedDate != ""){
				return '<span class="text-left invoiceclk">'+(new Date(updatedDate)).toLocaleDateString('en-GB')+'</span>';
			}else{
				return '<span class="text-left invoiceclk"></span>';
			}
		}
		}};
	var joindate = {data:  function ( data, type, row ) {
		var joinDate = data.joinDate ? data.joinDate : "";
		if(joinDate != ""){
			return '<span class="text-left invoiceclk">'+(new Date(joinDate)).toLocaleDateString('en-GB')+'</span>';
		}else{
			return '<span class="text-left invoiceclk"></span>';
		}
		}};
	var Status = {data:  function ( data, type, row ) {
		var status = data.status ? data.status : "";
		if(status == 'Joined'){
			return '<span style="color:green"><b>'+status+'</b></span>';	
		}else{
			return '<span style="color:red"><b>'+status+'</b></span>';		
		}
		}};
	return [ptnrname,ptnrmobile,ptnremail,userName,email,mobileno,clientType,estimatedcost,subscriptioncost,createddate,joindate,Status,
		{data: function ( data, type, row ) {
        	return '<a href="#" class="nottoedit" id="lead_cmts" onclick="showComments(\''+data.createdBy+'\',\''+data.email+'\')"><img style="width:26px;" class="cmntsimg" src="'+_getContextPath()+'/static/mastergst/images/dashboard-ca/comments-black.png" /></a>';
        }},
		];
}
function showComments(id,email){
	$('#leadCommentsModal').modal('show');
	$('.leads_commentTab').html("");
	$('#nocomments_lead').text("");
	$.ajax({
		url: _getContextPath()+'/getLeadComments?id='+id,
		async: false,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response){
			if(response.length == 0){
				$('#nocomment_leads').text("No Comments Added Yet");
			}
			for(var i=0;i<response.length;i++){
				$('#nocomment_leads').text("");
				$('.leadscommentTab').append('<div class="leadscommentsTab mb-2" style="margin-right: 10px;"><strong><label class="label_txt">Added By : '+response[i].addedby+'</label></strong><strong><label style="float:right;">Date : '+formatDate(response[i].commentDate)+'</label></strong><br/>'+response[i].leadscomments+'</div>');
			}
		},error:function(err){
		}
	});
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
function getval(sel) {
	document.getElementById('filing_option').innerHTML = sel;
	if (sel == 'Custom') {
		$('.typeDiv').css("display", "none");$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "inline-block");$('.dropdown-menu.ret-type').css("left", "16%");
	} else if (sel == 'Yearly') {
		$('.typeDiv').css("display", "none");$('.monthely-sp').css("display", "none");$('.yearly-sp').css("display", "inline-block");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type').css("left", "19%");
	} else {
		$('.monthely-sp,.typeDiv').css("display", "inline-block");$('.yearly-sp').css("display", "none");$('.custom-sp').css("display", "none");$('.dropdown-menu.ret-type').css("left", "19%");
	}
}
function getDowVal(val){
	document.getElementById('type_option').innerHTML = val;
	/*if(val == "Day Wise"){
		$('#daySummaryTable').css("display","block");$('#weekSummaryTable,#YaerlySummaryTable').css("display","none");
		loadSummaryTable('dayTable');
		}else{
			$('#weekSummaryTable').css("display","block");$('#daySummaryTable,#YaerlySummaryTable').css("display","none");
			loadSummaryTable('weekTable');
		}*/
}
function loadSummaryTable(tableid,month,year,type,tabType){
	if($.fn.DataTable.isDataTable('#'+tableid)){$('#'+tableid).DataTable().destroy();}
	
	$('#'+tableid).DataTable({
		"paging": false,
		scrollX: true,
		scrollCollapse: true,
		ordering: false,
		info: false,
		filter: false,
		"dom": '<"toolbar">frtip',  
		lengthChange: false,
		fixedColumns:{
			leftColumns:1,
			rightColumns:1
		}
	});
	var pUrl = _getContextPath()+'/leadDaysTotalSummary/'+month+"/"+year+"/"+tableid+"?tabName="+tabType;
	partnerSummaryTableUrl = pUrl;
	var urlStr =pUrl;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			summaryResponse(response,tableid,type);
		}
	});
}
function loadPartnerSupport(tableid,month,year,type,tabType, callback){
	var urlStr =_getContextPath()+'/getAdditionalPartnerDetails/'+month+"/"+year+"/"+tableid+"?tabName="+tabType;
	$.ajax({
		url: urlStr,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			callback(response);
		}
	});
}

function loadPartnersInDropdown(response){
 $('#multiselectPartnerName').find('option').remove();
	var usersMultiSelObj = $('#multiselectPartnerName');
	if (response.partnerNames.length > 0) {				
		response.partnerNames.forEach(function(partner) {
			if(partner != ''){
				usersMultiSelObj.append($("<option></option>").attr("value",partner).text(partner));
			} 
		});
	}
	//$('#multiselectPartnerName').multiselect('destroy');
	multiselrefresh('#multiselectPartnerName', "Partner");
	$('#multiselectPartnerName').multiselect('rebuild');
}

function summaryResponse(response,tableid,type){
	if($.fn.DataTable.isDataTable('#'+tableid)){$('#'+tableid).DataTable().destroy();}
	var dayweek = '';
	if(tableid == 'weekTable'){
		dayweek = 'w';
	}else if(tableid == 'yearlyTable'){
		dayweek = 'y';
	}
	var totLeads = 0, totNew = 0, totPending = 0, totJoined = 0, totDemo = 0, totEstimatedAmt = 0.0, totSubscriptionAmt = 0.0; 
	$.each(response, function(key, value){
		$('#'+dayweek+'totalleads'+key).html(value.totalLeads);
		$('#'+dayweek+'totalNew'+key).html(value.totalNew);
		$('#'+dayweek+'totalPending'+key).html(value.totalPending);
		$('#'+dayweek+'totalJoined'+key).html(value.totalJoined);
		$('#'+dayweek+'totalDemo'+key).html(value.totalDemo);
		var estimatedAmt = 0.0;
		if(value.estimatedAmt){
			estimatedAmt = value.estimatedAmt;
		}
		$('#'+dayweek+'totalestrev'+key).html(parseFloat(estimatedAmt));
		var subscriptionAmt = 0.0;
		if(value.subscriptionAmt){
			subscriptionAmt = value.subscriptionAmt;
		}
		$('#'+dayweek+'totalsubscrrev'+key).html(parseFloat(subscriptionAmt));
		
		totLeads += parseInt(value.totalLeads);
		totNew += parseInt(value.totalNew);
		totPending += parseInt(value.totalPending);
		totJoined += parseInt(value.totalJoined);
		totDemo += parseInt(value.totalDemo);
		totEstimatedAmt += parseFloat(estimatedAmt);
		totSubscriptionAmt += parseFloat(subscriptionAmt);
		
		if(key == 'totals'){
			$('#idCountLeads').html(value.totalLeads);
			$('#idCountNew').html(value.totalNew);
			$('#idCountPending').html(value.totalPending);
			$('#idCountJoined').html(value.totalJoined);
			$('#idCountDemo').html(value.totalDemo);
			var estimatedAmt = 0.0;
			if(value.estimatedAmt){
				estimatedAmt = value.estimatedAmt;
			}
			$('#idCountRevenue').html(parseFloat(estimatedAmt));
			//$('#idCountRevenue').html(value.estimatedAmt);
			var subscriptionAmt = 0.0;
			if(value.subscriptionAmt){
				subscriptionAmt = value.subscriptionAmt;
			}
			$('#idCountSubRevenue').html(parseFloat(subscriptionAmt));
			
			$('.totLeads').html(value.totalLeads);
			$('.totNew').html(value.totalNew);
			$('.totPending').html(value.totalPending);
			$('.totJoined').html(value.totalJoined);
			$('.totDemo').html(value.totalDemo);
			$('.totEstimatedAmt').html(parseFloat(estimatedAmt));
			$('.totSubscriptionAmt').html(parseFloat(subscriptionAmt));
		}
	});
	OSREC.CurrencyFormatter.formatAll({selector: '.ind_formatss'});
	$('#'+tableid).DataTable({
		"paging": false,
		scrollX: true,
		scrollCollapse: true,
		ordering: false,
		info: false,
		filter: false,
		"dom": '<"toolbar">frtip',  
		lengthChange: false,
		fixedColumns:{
			leftColumns:1,
			rightColumns:1
		}
	});
}
function loadYearlySummaryTable(){
	if($.fn.DataTable.isDataTable('#yearlyTable')){$('#yearlyTable').DataTable().destroy();}
	$('#yearlyTable').DataTable({
		"paging": false,
		scrollX: true,
		scrollCollapse: true,
		ordering: false,
		info: false,
		filter: false,
		"dom": '<"toolbar">frtip',  
		lengthChange: false,
		fixedColumns:{
			leftColumns:1,
			rightColumns:1
		}
	});
}
function initiateCallBacksForMultiSelectReports(){
	var multiSelDefaultVals = ['','- Partner Type -'];	
	/*for(i=1;i<=5;i++){
		
			if(i == 2 || i == 3){
				$('#multiselect'+varRetTypeCode+i).hide();
				continue;
			}
		
		multiselrefresh('#multiselectPartnerType', multiSelDefaultVals[i]);
	}*/
	multiselrefresh('#multiselectPartnerType', "Partner Type");
}
function multiselrefresh(idval, descVal){
	$(idval).multiselect({
		nonSelectedText: descVal,
		includeSelectAllOption: true,
		onChange: function(){
			applyInvFilterss();
		},
		onSelectAll: function(){
			applyInvFilterss();
		},
		onDeselectAll: function(){
			applyInvFilterss();
		}
	});
	
}
function applyInvFilterss() {
	var typeOptions = $('#multiselectPartnerType option:selected');
	var partnerOptions = $('#multiselectPartnerName option:selected');
	if(typeOptions.length > 0 || partnerOptions.length > 0){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	var typeArr=new Array();
	var userArr=new Array();
	var filterContent='';
	if(typeOptions.length > 0) {
		for(var i=0;i<typeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+typeOptions[i].text+'<span data-val="'+typeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			typeArr.push(typeOptions[i].value);
		}
	} else {
		//typeArr.push('All');
	}
	if(partnerOptions.length > 0) {
		for(var i=0;i<partnerOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+partnerOptions[i].value+'<span data-val="'+partnerOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			var userValue = partnerOptions[i].value;
			if(userValue.includes("&")){
				userValue = userValue.replace("&","-mgst-");
			}
			userArr.push(userValue);
		}
	} else {
		//userArr.push('All');
	}
	$('#divFiltersPartner').html(filterContent);
	reloadReportsInvTable(typeArr, userArr);	
	//reloadInvSummaryTable(typeArr, userArr);	
}

function reloadReportsInvTable(typeArr, userArr){
	var pUrl = partnerTableUrl;
	var psUrl = partnerSummaryTableUrl;
	var appd = '';
	
	if(typeArr.length > 0){
		appd+='&partnerType='+typeArr.join(',');
	}
	if(userArr.length > 0){
		appd+='&partnerName='+userArr.join(',');
	}
	pUrl += ''+appd;
	psUrl += ''+appd;
	leadsTable.ajax.url(pUrl).load();
	loadSummaryTableWithFilter(psUrl);
}

function updateYearlyOption(value){
	document.getElementById('yearlyoption').innerHTML=value;
}
function loadSummaryTableWithFilter(psUrl){
	
	var fpsplit = psUrl.split('?');
	var type = $('#fillingoption span').html();
	var talble = fpsplit[0].split('/');
	var tableid = talble[5];
	
	if($.fn.DataTable.isDataTable('#'+tableid)){$('#'+tableid).DataTable().destroy();}
	
	$('#'+tableid).DataTable({
		"paging": false,
		scrollX: true,
		scrollCollapse: true,
		ordering: false,
		info: false,
		filter: false,
		"dom": '<"toolbar">frtip',  
		lengthChange: false,
		fixedColumns:{
			leftColumns:1,
			rightColumns:1
		}
	});
	$.ajax({
		url: psUrl,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			summaryResponse(response,tableid,type);
		}
	});
}


function clearPartnerFiltersReports() {
	$('#multiselectPartnerType.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	$('#multiselectPartnerName.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	$('#divFiltersPartner').html('');
	$('.normaltable .filter').css("display","none");
	
	reloadReportsInvTable(new Array(), new Array());
}

function initializeRemoveAppliedFilters(){
	$('#divFiltersPartner').on('click', '.deltag', function(e) {
		var val = $(this).data('val');
		$('#multiselectPartnerType').multiselect('deselect', [val]);
		$('#multiselectPartnerName').multiselect('deselect', [val]);
		applyInvFilterss();
	});
}
