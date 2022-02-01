<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Supplier Compliance Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<link rel="stylesheet" src="${contextPath}/static/mastergst/css/common/meterial-form.css" media="all" />
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<style>
.tooltip.bs-tether-element-attached-left .tooltip-inner, .tooltip.bs-tether-element-attached-right .tooltip-inner {background-color:white;color:black;box-shadow: 1px 5px 7px rgba(0, 0, 0, 0.1);text-align:left;}
.tooltip.bs-tether-element-attached-left .tooltip-inner::before, .tooltip.tooltip-right .tooltip-inner::before, .tooltip.tooltip-left .tooltip-inner::before, .tooltip.bs-tether-element-attached-right .tooltip-inner::before{border-right-color: white!important;border-left-color: white!important;}
.toggle-drop::after {display:none;}
button.multiselect.dropdown-toggle.btn-block.text-left.btn.btn-default::after {display: inline-block; width: 0;height: 0; margin-left: .3em; vertical-align: middle; border-top: .3em solid; border-right: .3em solid transparent; border-left: .3em solid transparent;content: "";}
.multiselect-native-select{width: 180px;}
ul.multiselect-container.dropdown-menu{width:300px}
button.multiselect.dropdown-toggle.btn-block.text-left.btn.btn-default{text-align: center!important;}
.dropdown:hover .dropdown-content.reportsupsummary{display: block;}.arrow-up {width: 0; height: 0; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reportsupsummary{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 450px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
#suppliersDbTableBody  tr th{background-color:#f6f9fb;color: #5769bb;}
</style>
</head>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
		<div class="breadcrumbwrap">
		<div class="container">
			<div class="row">
				<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>" />
						<c:choose>
							<c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when>
							<c:otherwise>Business</c:otherwise>
						</c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose>
							<c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when>
							<c:otherwise>${client.businessname}</c:otherwise>
						</c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">Supplier Compliance Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap yearly1_supplier">
		<div id="processing" style="color: #000;font-size: 23px;position: absolute;top: 45%;left: 40%;z-index: 2;font-weight:500"></div>
			<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Suppliers GST Filing summary Report</h4>
			<div class="row" style="padding-left:15px;">
				<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;height: 19px;"> Help To Read This Report
			<div class="dropdown-content reportsupsummary"> <span class="arrow-up"></span><span class="pl-2">Supplier Wise All Months GST Filing Summary Based on Financial Period</span></div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			</div>
				<div id="group_and_client" class="group_and_client" style="display:inline;margin-top: 3px;">
                <div class="form-group" style="display:inline;">
                <label class="mr-3"> Search Filter :</label>
					<select id="multeselectgroup" class="multeselectgroup multiselect-ui form-control" multiple="multiple"></select>
				</div>
			</div>
			<a href="#" class="btn btn-blue excel" id="supplierStatus_excel" style="padding: 6px 15px 5px;font-weight: bold;color: #435a93;margin-left:10px;margin-top:4px;height:29px;float:right;">Download To Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
			<div class="dropdown chooseteam mr-0" style="height: 32px;">
			 	<span style="display: inline-block" class="yearly-sp">
					<span class="dropdown-toggle toggle-drop" data-toggle="dropdown" id="filling_option" style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;margin-right: 7px;">Financial Year:</label>
						<div class="rettype" style="z-index:2;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="finyearoption" class="finyearoption">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc" id="date-drop" style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div>
					</span>
					<div class="dropdown-menu rettype1" id="financial_Year" style="width: 108px !important; min-width: 36px; left: 34%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')">2021 - 2022</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')">2020 - 2021</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')">2019 - 2020</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')">2018 - 2019</a>
						<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')">2017 - 2018</a> 
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	onclick="getdiv()" style="padding: 4px 10px; text-transform: uppercase;font-size: 14px;">Generate</a>
				</span>
			</div>
            <div id="yearProcess" class="text-center"></div>
            <c:if test="${supplierss eq 'suppliers_notfound'}">
	            <div style="height:300px;background: white;">
					<div class="" style="position: relative;top: 140px;color: maroon;text-align: center;left: 155px;font-size: 23px;">suppliers are not available</div>
				</div>
			</c:if>
				<div class="supliers_tables customtable"  id="supliers_tables">
					<table id="suppliersDbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr><th class="text-center">Name/GSTIN</th><th class="text-center">Filing Information</th></tr>
						</thead>
						<tbody id='suppliersDbTableBody'></tbody>
					</table>
				</div>
			</div>
		</div>		
		<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Help To Read This Report </h3></div>
        <div class=" p-2 steptext-wrap"><span class="pl-2">Supplier Wise All Months GST Filing Summary Based on Financial Period</span> </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
	var suppliernameArr =new Array();
	var supplierTable;
	var supplierTableUrl;
	var cpSupplierResponse;
	var isCpSuppliersResponseLoaded = false;
	$(document).ready(function(){
		$( ".helpicon" ).hover(function() {
			$('.reportsupsummary').show();
		}, function() {
			$('.reportsupsummary').hide();
		});
		var year = new Date().getFullYear();
		var currentmonth = new Date().getMonth()+1;
		if(currentmonth < 4){year--;}
		loadSuppliersByClient('${id}', '${client.id}', loadSuppliersInDropDown);
		ajaxFuntion(year);
	});

<!-- <c:forEach items="${suppliers}" var="supplier">
	suppliernameArr.push('${supplier.id}');
</c:forEach> -->
$('#multeselectgroup').multiselect({
	nonSelectedText: '- Supplier Name/GSTIN -',
	includeSelectAllOption: true,
	onChange: function(element, checked) {applyGroup();},
	onSelectAll: function() {applyGroup();},
	onDeselectAll: function() {applyGroup();}
});

function loadSuppliersByClient(id, clientId, callback){
	var urlStr = '${contextPath}/getAddtionalSuppliersSupport/'+id+'/'+clientId
	if(isCpUsersResponseLoaded){
		callback(cpUsersResponse);
	}else{
		$.ajax({
			url: urlStr,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				cpSupplierResponse = response;
				isCpSuppliersResponseLoaded = true;
				callback(response);
			}
		});
	}
}
function loadSuppliersInDropDown(response){
	var usersMultiSelObj = $('#multeselectgroup')
	usersMultiSelObj.find('option').remove().end();
	if (response.billedTogstns.length > 0) {				
		response.billedTogstns.forEach(function(supplier) {
			if(supplier != ''){
				if(supplier.name != "" && supplier.name != undefined && supplier.name != null && supplier.name != "null"){
					usersMultiSelObj.append($("<option></option>").attr("value", supplier.gstnnumber).text(supplier.name +"-"+supplier.gstnnumber));
				}else{
					usersMultiSelObj.append($("<option></option>").attr("value", supplier.gstnnumber).text(supplier.gstnnumber));
				}
			}
		});
	}
	var supplierType ='- Supplier Name/GSTIN -';
	$('#multeselectgroup').multiselect('rebuild');
}
function applyGroup() {
	var groupArr = new Array(); 
	var groupOptions = $('.multeselectgroup option:selected');
	if(groupOptions.length > 0) {
		for(var i=0; i < groupOptions.length; i++) {
			groupArr.push(groupOptions[i].value);
		}
	}
	reloadInvTable(groupArr);
}
function reloadInvTable( vendorArr){
	var cUrl = supplierTableUrl;
	var appd = '';
	if(vendorArr.length > 0){
		appd += '?vendor='+vendorArr.join(',');
	}
	cUrl += appd;
	supplierTable.ajax.url(cUrl).load();
}
function updateYearlyOption(value){
	$('#finyearoption').text(value);
}
function getdiv(){
	var fp = $('#finyearoption').text();
	var fpsplit = fp.split(' - ');
	var yrs = parseInt(fpsplit[0]);
	var yrs1 = parseInt(fpsplit[0])+1;
	var cYear=new Date().getFullYear();
	var cMonth = new Date().getMonth()+1;
	$('#supplierStatus_excel').attr('href','${contextPath}/dwnldsupplierSummaryn/${id}/${client.id}/${month}/'+yrs+"?cYear="+cYear+"&cMonth="+cMonth);
	ajaxFuntion(yrs);
}
function ajaxFuntion(year){
	
	var pUrl = "${contextPath}/suppliercompliance_report/${id}/${fullname}/${usertype}/${client.id}/${month}/"+year;
	if(supplierTable){
		supplierTable.clear();
		supplierTable.destroy();
	}
	supplierTable = $('#suppliersDbTable').DataTable({
		"dom": 'f<"toolbar">lrtip<"clear">',
		"processing": true,
		"serverSide": true,
		"lengthMenu": [ [10, 25, 50, 100, 500], [10, 25, 50, 100, 500] ],
	    "ajax": {
	    	url: pUrl,
	        type: 'GET',
	        contentType: 'application/json; charset=utf-8',
	        dataType: "json",
	        'dataSrc': function(resp){
	        	resp.recordsTotal = resp.suppliers.totalElements;
	        	resp.recordsFiltered = resp.suppliers.totalElements;
	        	return resp.suppliers.content ;
	         }
	     },
		"paging": true,
		'pageLength':10,
		"responsive": true,
		"orderClasses": false,
		"searching": true,
		"order": [[1,'desc']],
		"columns": getSuppliersColumns(year),
		"columnDefs" : getSuppliersColumnsDefs(year)
	});
	
	supplierTableUrl = pUrl;
	var cYear=new Date().getFullYear();
	var cMonth = new Date().getMonth()+1;
	$('#supplierStatus_excel').attr('href','${contextPath}/dwnldsupplierSummaryn/${id}/${client.id}/${month}/'+year+"?cYear="+cYear+"&cMonth="+cMonth);
}

function getSuppliersColumnsDefs(year){
	return  [
		{
			"targets": 1,
			"orderable": false
		}		
	]
 }

function getSuppliersColumns(year){
	var currentyear=new Date().getFullYear();
	var currentMonth=new Date().getMonth()+1;
	
	var details = {data: function ( data, type, row ) {
		var name = '';
		if(data.name != undefined){
			name = '<div class="text-center">'+ data.name +'</div>';
		}
		if(data.gstnnumber != undefined){
			name += '<div class="text-center">'+ data.gstnnumber +'</div>';
		}
	    return '<span class="text-left invoiceclk ">'+name+'</span>';
	 }};
	
	var filingHistoty = {data: function ( data, type, row ) {
		var content = '<table id="" class="display row-border dataTable meterialform" cellspacing="0" width="100%">';
		content += '<thead class="">';
		content += '<tr><th></th><th class="text-center">April</th><th class="text-center">May</th><th class="text-center">June</th><th class="text-center">July</th><th class="text-center">August</th><th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th><th class="text-center">December</th><th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th></tr>'; 
		content += '</thead><tbody><tr><td style="width:65px!important">GSTR1</td>';
		var g1bContent = '';
		Object.keys(data.filingGstr1history).forEach(function(gstr1) {
			var mn = parseInt(gstr1.substring(5,7));
			if(currentyear == year){
				if(currentMonth >= mn){
					if(mn <= 3){
						g1bContent += '<td class="text-right color-red" id="'+gstr1+'">-</td>';
					}else{
						if(data.filingGstr1history[gstr1] == 'Filed') {
							content += '<td class="text-right color-green" id="'+gstr1+'">Filed</td>';
						}else if(data.filingGstr1history[gstr1] == 'Submitted'){
							content += '<td class="text-right color-blue" id="'+gstr1+'">Submitted</td>';
						}else{
							content += '<td class="text-right color-red" id="'+gstr1+'">Pending</td>';
						}
					}
				}else{
					content += '<td class="text-right color-red" id="'+gstr1+'">-</td>';
				}
			}else{
				if(year == '2017' && (mn >=4 && mn <= 6)){
					content += '<td class="text-right color-red" id="'+gstr1+'">-</td>';
				}else{
					if(mn <= 3){
						if(data.filingGstr1history[gstr1] == 'Filed') {
							g1bContent += '<td class="text-right color-green" id="'+gstr1+'">Filed</td>';
						}else if(data.filingGstr1history[gstr1] == 'Submitted'){
							g1bContent += '<td class="text-right color-blue" id="'+gstr1+'">Submitted</td>';
						}else{
							g1bContent += '<td class="text-right color-red" id="'+gstr1+'">Pending</td>';
						}
					}else{
						if(data.filingGstr1history[gstr1] == 'Filed') {
							content += '<td class="text-right color-green" id="'+gstr1+'">Filed</td>';
						}else if(data.filingGstr1history[gstr1] == 'Submitted'){
							content += '<td class="text-right color-blue" id="'+gstr1+'">Submitted</td>';
						}else{
							content += '<td class="text-right color-red" id="'+gstr1+'">Pending</td>';
						}
					}
				}
			}
		});
		content += g1bContent;
		content += '</tr><tr style="width:65px!important"><td>GSTR3B</td>';
		var g3bContent = '';
		Object.keys(data.filingGstr3bhistory).forEach(function(gstr3b) {
			var mn = parseInt(gstr3b.substring(5,7));
			
			if(currentyear == year){
				if(currentMonth >= mn){
					if(mn <= 3){
						g3bContent += '<td class="text-right color-red" id="'+gstr3b+'">-</td>';
					}else{
						if(data.filingGstr3bhistory[gstr3b] == 'Filed') {
							content += '<td class="text-right color-green" id="'+gstr3b+'">Filed</td>';
						}else if(data.filingGstr3bhistory[gstr3b] == 'Submitted'){
							content += '<td class="text-right color-blue" id="'+gstr3b+'">Submitted</td>';
						}else{
							content += '<td class="text-right color-red" id="'+gstr3b+'">Pending</td>';
						}
					}
				}else{
					content += '<td class="text-right color-red" id="'+gstr3b+'">-</td>';
				}
			}else{
				if(year == '2017' && (mn >=4 && mn <= 6)){
					content += '<td class="text-right color-red" id="'+gstr3b+'">-</td>';
				}else{
					if(mn <= 3){
						if(data.filingGstr3bhistory[gstr3b] == 'Filed') {
							g3bContent += '<td class="text-right color-green" id="'+gstr3b+'">Filed</td>';
						}else if(data.filingGstr3bhistory[gstr3b] == 'Submitted'){
							g3bContent += '<td class="text-right color-blue" id="'+gstr3b+'">Submitted</td>';
						}else{
							g3bContent += '<td class="text-right color-red" id="'+gstr3b+'">Pending</td>';
						}
					}else{
						if(data.filingGstr3bhistory[gstr3b] == 'Filed') {
							content += '<td class="text-right color-green" id="'+gstr3b+'">Filed</td>';
						}else if(data.filingGstr3bhistory[gstr3b] == 'Submitted'){
							content += '<td class="text-right color-blue" id="'+gstr3b+'">Submitted</td>';
						}else{
							content += '<td class="text-right color-red" id="'+gstr3b+'">Pending</td>';
						}
					}
				}
			}
		});
		content += g3bContent;
		content += '</tr></tbody></table>';
		return content;
	 }};
	
	if(year == '2017'){
		$('#GSTR1042017').html('<h6 style="text-align:center"> - </h6>');
		$('#GSTR1052017').html('<h6 style="text-align:center"> - </h6>');
		$('#GSTR1062017').html('<h6 style="text-align:center"> - </h6>');
		$('#GSTR3042017').html('<h6 style="text-align:center"> - </h6>');
		$('#GSTR3052017').html('<h6 style="text-align:center"> - </h6>');
		$('#GSTR3062017').html('<h6 style="text-align:center"> - </h6>');
	}
	return [details, filingHistoty];
}

</script>
</body>
</html>