<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | User Client Reports</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<script type="text/javascript">var msgTab;</script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<style>
.db-ca-wrap table#msgTable.row-border tbody tr td:first-child{color:#000!important;min-width:250px}
.color-black{color:#000!important}
.drop-menu {display: none;position: absolute; min-width: 190px;box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;}
.drop_btn:hover .drop-menu {display: block;right:100%;background-color: white;top: 9px;padding: 8px;}
.drop-menu .arrow-right{border-top:7px solid transparent;border-bottom:7px solid transparent;border-left: 7px solid white;display: inline-flex;position: absolute;right: -7px;top: 5px;}
.drop_top .drop-menu{left: 0!important;margin-top: -100px;background-color: white;top: 9px;padding: 8px;}
.drop_top .drop-menu .arrow-right{left:10px;top: 100%;border-left: 7px solid transparent; border-right: 7px solid transparent; border-top: 7px solid white;width: 0px;}  
.drop_top .drop_left .drop-menu{margin-left: -220px;margin-top: -64px;}
.drop_top .drop_left .drop-menu .arrow-right{border-top: 7px solid transparent;border-bottom: 7px solid transparent;border-left: 7px solid white;
width: 0;top: 67%;left: 100%;}
.excel_btn{background-color: #8ee3fe;margin-right: 10px;color: #435a9e !important;font-size: 14px;padding: 5px 10px!important;border-radius: 4px;border: 1px;width: 73px;cursor: pointer;font-weight: bold;}
.excel_btn:hover{background-color: #364365!important;color: white!important;text-decoration:unset}
</style>
</head>
<body class="body-cls">
		<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><c:choose><c:when test="${usertype eq userCenter}"><a target="_blank"  href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:when><c:otherwise><a target="_blank"  href="#" class="urllink" link="${contextPath}/teamuser/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Admin</a></c:otherwise></c:choose></li>
						<li class="breadcrumb-item"><a target="_blank"  href="#" class="urllink" link="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/>Global Reports</a></li>
						<li class="breadcrumb-item active">All Clients Filing Status Report</li>
					</ol>
					<div class="retresp"></div>
				</div></div></div></div>
	<div class="db-ca-wrap">
            <div class="container">
			<div class="col-sm-12"><a href="${contextPath}/cp_ClientsReports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${month}/${year}" class="btn btn-blue-dark pull-right sales-back-btn" role="button" style="padding: 4px 25px;">Back</a></div>
			<div class="" style="margin-top: 38px;">
				<div class="col-sm-7 pull-right mt-2 ml-2 mr-2" style=" display: inline;z-index:2">
					<select class="pull-right" name="financialYear" id="financialYear" style="padding:3px"><option value="2021">2021-2022</option><option value="2020">2020-2021</option><option value="2019">2019-2020</option><option value="2018">2018-2019</option><option value="2017">2017-2018</option></select>
					<h4 class="hdrtitle1" style="display: inline-block; color: #374583;font-size: 20px;float:right;margin-top:4px">Financial Year : </h4> 
					<a id="exceldwnld" class="excel_btn pull-right" type="button" style="width:auto!important">Download To Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
					<a type="button" class="btn btn-primary ml-4 pull-right" style=" font-size:14px;padding: 5px 10px!important;height: 31px; margin-right: 10px;" onclick="refreshClientFilingStatusSummary()">Filing Status<i class="fa fa-refresh" id="flngClientstatusrefreshSummary" style="font-size: 14px; color: #fff; margin-left:5px;"></i></a>
				</div>	
			</div>
	<div class="bodywrap" style="min-height: 500px;">
		<div class="db-ca-wrap" style="padding-top:0px!important;margin-top:0px!important">
			<div class="container">
				<div class="">			
					<div class=""><div class=" "><h4 class="f-18-b pull-left mt-2 ml-2">All Clients Filing Status Report</h4>
					<div class=" "></div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
					<div class="normaltable meterialform" id="monthlynormaltable">
		<div class="filter">
			<div class="noramltable-row"><div class="noramltable-row-hdr">Filter</div><div class="noramltable-row-desc"><div class="sfilter"><span id="divFilters"></span>
					<span class="btn-remove-tag" onClick="clearFilters()">Clear All<span data-role="remove"></span></span>
				</div></div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search Filter</div><div class="noramltable-row-desc">
				<!-- <select id="multeselectuser" class="multiselect-ui form-control" multiple="multiple"></select> -->
				<select id="multeselectrtntype" class="multiselect-ui form-control" multiple="multiple">
				<option value="GSTR1">GSTR1</option>
				<option value="GSTR3B">GSTR3B</option>
				<option value="GSTR9">GSTR9</option>
				<option value="GSTR4">GSTR4</option>
				<option value="GSTR5">GSTR5</option>
				<option value="GSTR6">GSTR6</option>
				<option value="GSTR8">GSTR8</option>
				<option value="GSTR10">GSTR10</option>
				</select>
				<select id="multiselectclient" class="multiselect-ui form-control" multiple="multiple">
				
					<c:forEach var="client" items="${clientslst}">
						<option value="${client.businessname}"><c:out value="${client.businessname}"/></option>
					</c:forEach>
				</select>
			</div>
		</div>
		
	</div>
			</div>
					</div>
						<div id="filingStatusProcess" class="text-center"></div>
						<div class="customtable db-ca-view tabtable1" style="width:1080px;overflow-y:auto">
							<table id="msgTable"class="admin_datatable display row-border dataTable meterialform"	cellspacing="0" width="100%">
								<thead><tr><th>Clientid</th><th>ClientName</th><th>GSTNO</th><th>ReturnType</th><th>April</th><th>May</th><th>June</th><th>July</th><th>August</th><th>September</th><th>October</th><th>November</th><th>December</th><th>January</th><th>February</th><th>March</th></tr></thead>
								<tbody id="reportTable"></tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>
	</div>
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
</body>
<script type="text/javascript">
var invoiceArray=new Object();var returnTypesArray = new Array();var clientsArray = new Array();
var hiddenCols = new Array();
hiddenCols.push(0);
var currentDates = new Date();var currentMonths = currentDates.getMonth()+1;var currentYear = currentDates.getFullYear();
function formatUpdatedDate(dat, type, row){var createdDt = new Date(dat.updatedDate) ; var month = createdDt.getUTCMonth() + 1;var day = createdDt.getUTCDate();var year = createdDt.getUTCFullYear();
	return day+'-'+month+'-'+year;
}
var retTypes  = '${retTypes}';
var clientids = '${clientids}';
clientsArray.push(clientids);
returnTypesArray.push(retTypes);
var currDate = new Date();
$(document).ready(function(){$('#msgs_lnk').addClass('active');$('.nonAspReports').addClass('active');
	msgTab = $('#msgTable').DataTable({
			dom: 'lBfrtip', 
			"pageLength": 10,
			scrollX:        true,
        scrollCollapse: true,
			buttons : [ {
						extend : 'excel',
						filename : 'FilingStatusReport',
						title : '',
						text : 'Download to Excel <i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i>'
					}],
					"columnDefs": [
						{
							"targets": hiddenCols,
							"visible": false,
							"searchable": true
						}],
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
$('#multeselectrtntype').multiselect({
		nonSelectedText: '- Return Type -',
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
	$('#multiselectclient').multiselect({
		nonSelectedText: '- Client Name -',
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
	
	$('.tabtable1 div.toolbar').html('<h4 class="hdrtitle">Client Reports</h4>');
	var year = currDate.getFullYear();var month = currDate.getMonth()+1;
	var initialYear = year;
	var yrss = year+1;
	msgTab.clear();
	if(month <= 3){initialYear--;}
	$('#financialYear').val(initialYear);
	filingstatusreport(initialYear,month);
	});
$('#financialYear').change(function() {
	
	var changedYear = $('#financialYear').val();
	var yrs = changedYear; var nextYear =  parseInt(changedYear)+1;
	msgTab.clear();	
	var months = currDate.getMonth()+1;
	filingstatusreport(yrs,months);
});
function applyFilters() {
	clientsArray = new Array();returnTypesArray = new Array();
	var returntypeOptions = new Array();
	var clientOptions = new Array();
	//userOptions = $('#multeselectuser option:selected');
	returntypeOptions = $('#multeselectrtntype option:selected');
	clientOptions = $('#multiselectclient option:selected');
	if(returntypeOptions.length > 0 || clientOptions.length > 0 ){
		$('.normaltable .filter').css("display","block");
	}else{
		$('.normaltable .filter').css("display","none");
	}
	var userArr=new Array();
	var returntypeArr=new Array();
	var clientArr=new Array();
	
	var filterContent='';
	
	if(returntypeOptions.length > 0) {
		for(var i=0;i<returntypeOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+returntypeOptions[i].text+'<span data-val="'+returntypeOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			returntypeArr.push(returntypeOptions[i].value);
			returnTypesArray.push(returntypeOptions[i].value);
		}
	} else {
		returntypeArr.push('All');
	}
	if(clientOptions.length > 0) {
		for(var i=0;i<clientOptions.length;i++) {
			filterContent+='<span data-role="tagsinput" placeholder="" class="btaginput">'+clientOptions[i].value+'<span data-val="'+clientOptions[i].value+'" class="deltag" data-role="remove"></span></span>';
			clientArr.push(clientOptions[i].value);
			clientsArray.push(clientOptions[i].value);
		}
	} else {
		clientArr.push('All');
	}
	$('#divFilters').html(filterContent);
	commonInvoiceFilter(returntypeArr,clientArr);
	msgTab.draw();
	//invoiceTable['GSTR1'].draw();
}
function commonInvoiceFilter(rType,clntType) {
		var changedYear = $('#financialYear').val();
		var yrs = changedYear; var nextYear =  parseInt(changedYear)+1;
		var months = currDate.getMonth()+1;
		var rowNode;
	
	var months = currDate.getMonth()+1;
	$('#msgTable tbody').empty();
	msgTab=$('#msgTable').DataTable().draw();
	var i=0;
	if(invoiceArray['GSTR1'].length > 0) {
		var rows = new Array();
		var taxArray = new Array();
		var summary=invoiceArray['GSTR1'];
		msgTab.clear();	
		for(var i=0; i<summary.length;i++){
				var cchangedYear = nextYear.toString();
				Object.keys(summary[i].statusmap).forEach(function(rType) {
					if(rType != 'GSTR3' && rType != 'GSTR2'){
						if(yrs == '2017'){
							rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,'-','-','-','-',summary[i].statusmap[rType]["08"+yrs],summary[i].statusmap[rType]["09"+yrs],summary[i].statusmap[rType]["10"+yrs],summary[i].statusmap[rType]["11"+yrs],summary[i].statusmap[rType]["12"+yrs],summary[i].statusmap[rType]["01"+cchangedYear],summary[i].statusmap[rType]["02"+cchangedYear],summary[i].statusmap[rType]["03"+cchangedYear]];
						}else{
							if(rType == 'GSTR9'){
								rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,'-','-','-','-','-','-','-','-','-','-','-',summary[i].statusmap[rType]["03"+cchangedYear]];
							}else{
								rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,summary[i].statusmap[rType]["04"+yrs],summary[i].statusmap[rType]["05"+yrs],summary[i].statusmap[rType]["06"+yrs],summary[i].statusmap[rType]["07"+yrs],summary[i].statusmap[rType]["08"+yrs],summary[i].statusmap[rType]["09"+yrs],summary[i].statusmap[rType]["10"+yrs],summary[i].statusmap[rType]["11"+yrs],summary[i].statusmap[rType]["12"+yrs],summary[i].statusmap[rType]["01"+cchangedYear],summary[i].statusmap[rType]["02"+cchangedYear],summary[i].statusmap[rType]["03"+cchangedYear]];
							}
							rows.push(rowData);
						}
					}
			});
		}
		rows.forEach(function(row) {
			if((rType.length == 0 || $.inArray('All', rType) >= 0 || $.inArray(row[3], rType) >= 0)
					&& (clntType.length == 0 || $.inArray('All', clntType) >= 0 || $.inArray(row[1], clntType) >= 0)) {
			var clientid = row[0];
			var rtype = row[3];
			var rowNode;
			var cchangedYear = nextYear.toString();
				for(var j=4;j<=15;j++){
					var k = j;
					if(j == 13 || j == 14 || j == 15){
						if(j == 13){k = 1;}else if(j == 14){k=2;}else{k=3;}
						var duedate="";
						if(rtype == 'GSTR1'){
							duedate+='11'+'-'+k+'-'+yrs;
						}else if(rtype == 'GSTR2'){
							duedate+='15'+'-'+k+'-'+yrs;
						}else if(rtype == 'GSTR3B'){
							duedate+='20'+'-'+k+'-'+yrs;
						}else if(rtype == 'GSTR8'){
							duedate+='10'+'-'+k+'-'+yrs;
						}else if(rtype == 'GSTR9'){
							duedate+=31+'-12-'+(cchangedYear);
						}
						if(row[j][0] == 'Pending'){	
							row[j] ='<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>---</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red">Pending</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>---</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-red">Pending</span></a>'; 
								
								//'<a target="_blank"  href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-red">Pending</span></a>';
						}else if(row[j][0] == 'Submitted'){
							var arn='';
							if(row[j][2] == undefined || row[j][2] == 'undefined' || row[j][2] == null){
								arn='---';
							}else{
								arn=row[j][2];
							}
							row[j] ='<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+row[j][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue">Submitted</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+arn+'</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-blue">Submitted</span></a>'; 
								//'<a target="_blank"  href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-blue">Submitted</span></a>';
						}else if(row[j][0] == 'Filed'){
							var arn='';
							if(row[j][2] == undefined || row[j][2] == 'undefined' || row[j][2] == null){
								arn='---';
							}else{
								arn=row[j][2];
							}
							row[j] = '<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+row[j][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">Filed</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+arn+'</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-green">Filed</span></a>';
								//'<a target="_blank"  href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-green">Filed</span></a>';
						}
					}else{
						var duedate="";
						if(rtype == 'GSTR1'){
							duedate+='11'+'-'+k+'-'+yrs;
						}else if(rtype == 'GSTR2'){
							duedate+='15'+'-'+k+'-'+yrs;
						}else if(rtype == 'GSTR3B'){
							duedate+='20'+'-'+k+'-'+yrs;
						}else if(rtype == 'GSTR8'){
							duedate+='10'+'-'+k+'-'+yrs;
						}else if(rtype == 'GSTR9'){
							duedate+=31+'-12-'+(cchangedYear);
						}
						if(row[j][0] == 'Pending'){
							row[j] ='<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>---</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red">Pending</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>---</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+yrs+'?type="><span class="color-red">Pending</span></a>'; 
								
								//'<a target="_blank"  href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-red">Pending</span></a>';
						}else if(row[j][0] == 'Submitted'){
							var arn='';
							if(row[j][2] == undefined || row[j][2] == 'undefined' || row[j][2] == null){
								arn='---';
							}else{
								arn=row[j][2];
							}
							row[j] ='<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+row[j][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue">Submitted</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+arn+'</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+yrs+'?type="><span class="color-blue">Submitted</span></a>'; 
								//'<a target="_blank"  href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-blue">Submitted</span></a>';
						}else if(row[j][0] == 'Filed'){
							var arn='';
							if(row[j][2] == undefined || row[j][2] == 'undefined' || row[j][2] == null){
								arn='---';
							}else{
								arn=row[j][2];
							}
							row[j] = '<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+row[j][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">Filed</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+arn+'</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+k+'/'+yrs+'?type="><span class="color-green">Filed</span></a>';
								//'<a target="_blank"  href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+row[3]+'/'+k+'/'+nextYear+'?type="><span class="color-green">Filed</span></a>';
						}
					}	
				}
				//row[0] = '<a target="_blank"  href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+months+'/'+yrs+'?type=initial"><span class="color-black">'+row[0]+'</span></a>';
				row[1] = '<a target="_blank"  href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+months+'/'+yrs+'?type=initial"><span class="color-black">'+row[1]+'</span></a>';
				row[2] = '<a target="_blank"  href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+months+'/'+yrs+'?type=initial"><span class="color-black">'+row[2]+'</span></a>';
				row[3] = '<a target="_blank"  href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+clientid+'/'+row[3]+'/'+months+'/'+yrs+'?type="><span class="color-black">'+row[3]+'</span></a>';
			
					rowNode = msgTab.row.add(row);msgTab.row(rowNode).column(0).nodes().to$().css('white-space','nowrap');
					
					if(yrs != '2017'){
						if(currentMonths <= 3 && yrs == currentYear-1 && yrs <= currentYear){
							if(rType != 'GSTR9'){
								if(currentMonths >= 4) {
									for(var index = currentMonths+1; index <= 12; index++) {msgTab.row(rowNode).column(index).nodes().to$().text('-');}
									msgTab.row(rowNode).column(13).nodes().to$().text('-');	msgTab.row(rowNode).column(14).nodes().to$().text('-');	msgTab.row(rowNode).column(15).nodes().to$().text('-');
								} else if(currentMonths < 3) {
									if(yrs != '2017' && currentMonths == '1'){msgTab.row(rowNode).column(14).nodes().to$().text('-');msgTab.row(rowNode).column(15).nodes().to$().text('-');}
									if(yrs != '2017' && currentMonths == '2'){msgTab.row(rowNode).column(15).nodes().to$().text('-');}
								}
							}		
						}else if(yrs >= currentYear){
							if(rType != 'GSTR9'){
								if(currentMonths >= 4) {
									for(var index = currentMonths+1; index <= 12; index++) {msgTab.row(rowNode).column(index).nodes().to$().text('-');}
									msgTab.row(rowNode).column(13).nodes().to$().text('-');	msgTab.row(rowNode).column(14).nodes().to$().text('-');	msgTab.row(rowNode).column(15).nodes().to$().text('-');
								} else if(currentMonths < 3) {
									if(yrs != '2017' && currentMonths == '1'){msgTab.row(rowNode).column(14).nodes().to$().text('-');msgTab.row(rowNode).column(15).nodes().to$().text('-');}
									if(yrs != '2017' && currentMonths == '2'){msgTab.row(rowNode).column(15).nodes().to$().text('-');}
								}
							}
						}
					}
			  }
		});
		msgTab.draw();
		$( ".admin_datatable tbody tr:nth-last-child(1) td , .admin_datatable tbody tr:nth-last-child(2) td" ).addClass('drop_left');
		$( ".admin_datatable tbody tr:nth-last-child(1) , .admin_datatable tbody tr:nth-last-child(2)" ).addClass('drop_top');
		$('.admin_datatable tbody tr td').addClass('drop_btn dropdown');
		$('.admin_datatable').css('overflow','inherit');
		$('.admin_datatable tbody tr td').addClass('drop_btn dropdown');
	}
}
function clearFilters() {
	$('.multiselect-ui').multiselect('deselectAll',false).multiselect('updateButtonText');
	$('.normaltable .filter').css("display","none");
	$('#divFilters').html('');
	
	msgTab.clear();
	commonInvoiceFilter(new Array(), new Array(), new Array());
	msgTab.draw();
}
$('#divFilters').on('click', '.deltag', function(e) {
	var val = $(this).data('val');
	$('#multeselectuser').multiselect('deselect', [val]);
	$('#multiselectclient').multiselect('deselect', [val]);
	$('#multeselectrtntype').multiselect('deselect', [val]);
	applyFilters();
});
</script>
<script type="text/javascript">
$(document).ready(function(){
	$.ajax({
		url: "${contextPath}/adminsales_cp_users/${id}",
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		success : function(response) {
			$('#multeselectuser').empty().multiselect('rebuild');
			if (response.length > 0) {
				$("#multeselectuser").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
				response.forEach(function(cp_user) {
					$("#multeselectuser").append($("<option></option>").attr("value",cp_user.name).text(cp_user.name));
				});
			}else{
				$("#multeselectuser").append($("<option></option>").attr("value",globaluser).text(globaluser)); 
			}
			$('#multeselectuser').multiselect('rebuild');
		}
	});
}); 
function filingstatusreport(yrs,months){
	
	$('#exceldwnld').attr('href','${contextPath}/userclientfillingxls/${id}/${fullname}/${usertype}/${month}/'+yrs+'?exceldwnld=');
	var nextYear =  parseInt(yrs)+1;
	$.ajax({
		url: '${contextPath}/userclntflngstatus/${id}/'+yrs+'?exceldwnld=', 
		method: 'GET',
		contentType : 'application/json',
		cache : false,
		beforeSend: function () {
		    $('#filingStatusProcess').text('Please wait, Loading Data...');
		},
		success: function(summary){
			if(summary) {
				invoiceArray['GSTR1'] = summary;
				for(var i=0; i<summary.length;i++){
					Object.keys(summary[i].statusmap).forEach(function(rType) {
						var cchangedYear = nextYear.toString();
						if(rType != 'GSTR3' && rType != 'GSTR2'){
							var rowData;	var rowNode;
							if(yrs == '2017'){rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,'-','-','-','-',summary[i].statusmap[rType]["08"+yrs],summary[i].statusmap[rType]["09"+yrs],summary[i].statusmap[rType]["10"+yrs],summary[i].statusmap[rType]["11"+yrs],summary[i].statusmap[rType]["12"+yrs],summary[i].statusmap[rType]["01"+cchangedYear],summary[i].statusmap[rType]["02"+cchangedYear],summary[i].statusmap[rType]["03"+cchangedYear]];
							}else{
								if(rType == 'GSTR9'){
									rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,'-','-','-','-','-','-','-','-','-','-','-',summary[i].statusmap[rType]["03"+cchangedYear]];
								}else{
									rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,summary[i].statusmap[rType]["04"+yrs],summary[i].statusmap[rType]["05"+yrs],summary[i].statusmap[rType]["06"+yrs],summary[i].statusmap[rType]["07"+yrs],summary[i].statusmap[rType]["08"+yrs],summary[i].statusmap[rType]["09"+yrs],summary[i].statusmap[rType]["10"+yrs],summary[i].statusmap[rType]["11"+yrs],summary[i].statusmap[rType]["12"+yrs],summary[i].statusmap[rType]["01"+cchangedYear],summary[i].statusmap[rType]["02"+cchangedYear],summary[i].statusmap[rType]["03"+cchangedYear]];
								}
							}
								rowData[1] = '<a target="_blank"  href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+months+'/'+yrs+'?type=initial"><span class="color-black">'+summary[i].clientName+'</span></a>';
								rowData[2] = '<a target="_blank"  href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+months+'/'+yrs+'?type=initial"><span class="color-black">'+summary[i].gstno+'</span></a>';
								rowData[3] = '<a target="_blank"  href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+rType+'/'+months+'/'+yrs+'?type="><span class="color-black">'+rType+'</span></a>';
							for(var j=4;j<=15;j++){
								var k = j;
								if(j == 13 || j == 14 || j == 15){
									if(j == 13){k = 1;}else if(j == 14){k=2;}else{k=3;}
									var duedate="";
									if(rType == 'GSTR1'){
										duedate+=11+'-'+(k+1)+'-'+(yrs+1);
									}else if(rType == 'GSTR2'){
										duedate+=15+'-'+(k+1)+'-'+(yrs+1);
									}else if(rType == 'GSTR3B'){
										duedate+=20+'-'+(k+1)+'-'+(yrs+1);
									}else if(rType == 'GSTR8'){
										duedate+=10+'-'+(k+1)+'-'+(yrs+1);
									}else if(rType == 'GSTR9'){
										duedate+=31+'-12-'+(yrs+1);
									}
									if(rowData[j][0] == 'Pending'){
										rowData[j] = '<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>---</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red">Pending</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>---</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+rType+'/'+k+'/'+nextYear+'?type="><span class="color-red">Pending</span></a>';
									}else if(rowData[j][0] == 'Submitted'){
										var arn='';
										if(rowData[j][2] == undefined || rowData[j][2] == 'undefined' || rowData[j][2] == null){
											arn='---';
										}else{
											arn=rowData[j][2];
										}
										rowData[j] = '<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+rowData[j][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue">'+rowData[j][0]+'</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+arn+'</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+rType+'/'+k+'/'+nextYear+'?type="><span class="color-blue">Submitted</span></a>';
									}else if(rowData[j][0] == 'Filed'){
										var arn='';
										if(rowData[j][2] == undefined || rowData[j][2] == 'undefined' || rowData[j][2] == null){
											arn='---';
										}else{
											arn=rowData[j][2];
										}
										rowData[j] = '<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+rowData[j][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+rowData[j][0]+'</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+arn+'</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+rType+'/'+k+'/'+nextYear+'?type="><span class="color-green">Filed</span></a>';
									}
								}else{
									var duedate="";
									if(rType == 'GSTR1'){
										duedate+='11'+'-'+(k+1)+'-'+yrs;
									}else if(rType == 'GSTR2'){
										duedate+='15'+'-'+(k+1)+'-'+yrs;
									}else if(rType == 'GSTR3B'){
										duedate+='20'+'-'+(k+1)+'-'+yrs;
									}else if(rType == 'GSTR8'){
										duedate+='10'+'-'+(k+1)+'-'+yrs;
									}else if(rType == 'GSTR9'){
										duedate+=31+'-12-'+(yrs+1);
									}
									if(rowData[j][0] == 'Pending'){
										rowData[j] = '<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>---</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red">Pending</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>---</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+rType+'/'+k+'/'+yrs+'?type="><span class="color-red">Pending</span></a>';
									}else if(rowData[j][0] == 'Submitted'){
										var arn='';
										if(rowData[j][2] == undefined || rowData[j][2] == 'undefined' || rowData[j][2] == null){
											arn='---';
										}else{
											arn=rowData[j][2];
										}
										rowData[j] = '<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+rowData[j][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue">'+rowData[j][0]+'</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+arn+'</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+rType+'/'+k+'/'+yrs+'?type="><span class="color-blue">Submitted</span></a>';
									}else if(rowData[j][0] == 'Filed'){
										var arn='';
										if(rowData[j][2] == undefined || rowData[j][2] == 'undefined' || rowData[j][2] == null){
											arn='---';
										}else{
											arn=rowData[j][2];
										}
										rowData[j] = '<div class="dropdown-content drop-menu"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+rowData[j][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green">'+rowData[j][0]+'</span></strong></div><div>ARN<span class="colon" style="margin-left: 44px;margin-right: 5px;">:</span><span>'+arn+'</span></div></div><p><a target="_blank" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+rType+'/'+k+'/'+yrs+'?type="><span class="color-green">Filed</span></a>';
									}
								}
							}
							rowNode = msgTab.row.add(rowData);	msgTab.row(rowNode).column(0).nodes().to$().css('white-space','nowrap');
							
							if(yrs != '2017'){
								if(currentMonths <= 3 && yrs == currentYear-1 && yrs <= currentYear){
									if(rType != 'GSTR9'){
										if(currentMonths >= 4) {
											for(var index = currentMonths+1; index <= 12; index++) {msgTab.row(rowNode).column(index).nodes().to$().text('-');}
											msgTab.row(rowNode).column(13).nodes().to$().text('-');	msgTab.row(rowNode).column(14).nodes().to$().text('-');	msgTab.row(rowNode).column(15).nodes().to$().text('-');
										} else if(currentMonths < 3) {
											if(yrs != '2017' && currentMonths == '1'){msgTab.row(rowNode).column(14).nodes().to$().text('-');msgTab.row(rowNode).column(15).nodes().to$().text('-');}
											if(yrs != '2017' && currentMonths == '2'){msgTab.row(rowNode).column(15).nodes().to$().text('-');}
										}
									}		
								}else if(yrs >= currentYear){
									if(rType != 'GSTR9'){
										if(currentMonths >= 4) {
											
											for(var index = currentMonths+1; index <= 12; index++) {msgTab.row(rowNode).column(index).nodes().to$().text('-');}
											msgTab.row(rowNode).column(13).nodes().to$().text('-');	msgTab.row(rowNode).column(14).nodes().to$().text('-');	msgTab.row(rowNode).column(15).nodes().to$().text('-');
										} else if(currentMonths < 3) {
											if(yrs != '2017' && currentMonths == '1'){msgTab.row(rowNode).column(14).nodes().to$().text('-');msgTab.row(rowNode).column(15).nodes().to$().text('-');}
											if(yrs != '2017' && currentMonths == '2'){msgTab.row(rowNode).column(15).nodes().to$().text('-');}
										}
									}
								}
							
							}
						}else{
							if(rType != 'GSTR3' && rType != 'GSTR2'){
								var rowData;	var rowNode;
								if(yrs == '2017'){rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,'-','-','-','-',summary[i].statusmap[rType]["08"+yrs],summary[i].statusmap[rType]["09"+yrs],summary[i].statusmap[rType]["10"+yrs],summary[i].statusmap[rType]["11"+yrs],summary[i].statusmap[rType]["12"+yrs],summary[i].statusmap[rType]["01"+cchangedYear],summary[i].statusmap[rType]["02"+cchangedYear],summary[i].statusmap[rType]["03"+cchangedYear]];
								}else{
									if(rType == 'GSTR9'){
										rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,'-','-','-','-','-','-','-','-','-','-','-',summary[i].statusmap[rType]["03"+cchangedYear]];
									}else{
										rowData = [summary[i].clientid,summary[i].clientName,summary[i].gstno,rType,summary[i].statusmap[rType]["04"+yrs],summary[i].statusmap[rType]["05"+yrs],summary[i].statusmap[rType]["06"+yrs],summary[i].statusmap[rType]["07"+yrs],summary[i].statusmap[rType]["08"+yrs],summary[i].statusmap[rType]["09"+yrs],summary[i].statusmap[rType]["10"+yrs],summary[i].statusmap[rType]["11"+yrs],summary[i].statusmap[rType]["12"+yrs],summary[i].statusmap[rType]["01"+cchangedYear],summary[i].statusmap[rType]["02"+cchangedYear],summary[i].statusmap[rType]["03"+cchangedYear]];
									}
								}
								rowData[1] = '<a target="_blank" href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+months+'/'+yrs+'?type=initial"><span class="color-black">'+summary[i].clientName+'</span></a>';
								rowData[2] = '<a target="_blank" href="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/'+summary[i].clientid+'/'+months+'/'+yrs+'?type=initial"><span class="color-black">'+summary[i].gstno+'</span></a>';
								rowData[3] = '<span class="color-black" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">'+rType+'</span>';
								for(var j=4;j<=15;j++){
									//var k = j+1;
									var k = j;
									if(rowData[j][0] == 'Pending'){
										rowData[j] = '<span class="color-red" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">Pending</span>';
									}else if(rowData[j][0] == 'Submitted'){
										rowData[j] = '<span class="color-blue" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">Submitted</span>';
									}else if(rowData[j][0] == 'Filed'){
										rowData[j] = '<span class="color-green" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">Filed</span>';
									}
								}
								rowNode = msgTab.row.add(rowData);	msgTab.row(rowNode).column(0).nodes().to$().css('white-space','nowrap');
								if(yrs != '2017'){
									if(currentMonths <= 3 && yrs == currentYear-1 && yrs <= currentYear){
										if(rType != 'GSTR9'){
											if(currentMonths >= 4) {
												for(var index = currentMonths+1; index <= 12; index++) {msgTab.row(rowNode).column(index).nodes().to$().text('-');}
												msgTab.row(rowNode).column(13).nodes().to$().text('-');	msgTab.row(rowNode).column(14).nodes().to$().text('-');	msgTab.row(rowNode).column(15).nodes().to$().text('-');
											} else if(currentMonths < 3) {
												if(yrs != '2017' && currentMonths == '1'){msgTab.row(rowNode).column(14).nodes().to$().text('-');msgTab.row(rowNode).column(15).nodes().to$().text('-');}
												if(yrs != '2017' && currentMonths == '2'){msgTab.row(rowNode).column(15).nodes().to$().text('-');}
											}
										}		
									}else if(yrs >= currentYear){
										if(rType != 'GSTR9'){
											if(currentMonths >= 4) {
												for(var index = currentMonths+1; index <= 12; index++) {msgTab.row(rowNode).column(index).nodes().to$().text('-');}
												msgTab.row(rowNode).column(13).nodes().to$().text('-');	msgTab.row(rowNode).column(14).nodes().to$().text('-');	msgTab.row(rowNode).column(15).nodes().to$().text('-');
											} else if(currentMonths < 3) {
												if(yrs != '2017' && currentMonths == '1'){msgTab.row(rowNode).column(14).nodes().to$().text('-');msgTab.row(rowNode).column(15).nodes().to$().text('-');}
												if(yrs != '2017' && currentMonths == '2'){msgTab.row(rowNode).column(15).nodes().to$().text('-');}
											}
										}
									}
								}
							}
						}
					});
				}
			}
			msgTab.draw();
			$( ".admin_datatable tbody tr:nth-last-child(1) td , .admin_datatable tbody tr:nth-last-child(2) td" ).addClass('drop_left');
			$( ".admin_datatable tbody tr:nth-last-child(1) , .admin_datatable tbody tr:nth-last-child(2)" ).addClass('drop_top');
			
			$('.admin_datatable tbody tr td').addClass('drop_btn dropdown');
			$('#filingStatusProcess').text(' ');
		}
	});
}
function refreshClientFilingStatusSummary(){
	var financialyear=$('#financialYear').val();
	var pmonth = parseInt('${month}');
	$('#flngClientstatusrefreshSummary').addClass('fa-spin');
$.ajax({
	url: '${contextPath}/userclntflngstatus/${id}/'+financialyear+'?exceldwnld=exceldwnld', 
	method: 'GET',
	contentType : 'application/json',
	cache : false,
	beforeSend: function () {
	    $('#filingStatusProcess').text('Please wait, Loading Data...');
	},
	success : function(summary) {
		var currentDates = new Date();var currentYear = currentDates.getFullYear();
		if(financialyear <= currentYear){
			pmonth = 4;
		}
		filingstatusreport(financialyear , pmonth);
		window.setTimeout(function(){
			$('#flngClientstatusrefreshSummary').removeClass("fa-spin");
			successNotification('Client Filing Status Refresh Successfully...');
			$('#filingStatusProcess').text(' ');
			}, 4000);
	},error: function(data) {
		$('#filingStatusProcess').text(' ');
	}	
});
}
</script>
</html>