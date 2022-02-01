<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Filing Status Report</title>
<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>

<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"></script>
<script src="${contextPath}/static/mastergst/js/signups/google-address.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<style>
.dt-buttons {white;left: 58%;float: left;position: relative;color:white}
button.dt-button.buttons-excel.buttons-html5, button.dt-button.buttons-print,
button.dt-button.buttons-pdf.buttons-html5 {background-color: #8ee3fe;	margin-right: 26px;	color: #435a9e !important;font-size: 14px;padding: 5px 10px!important;	border-radius: 4px;	border: 1px;width: 152px;cursor:pointer;}
.dt-buttons :hover{background-color:#364365!important; color:#ffffff!important}
div#msgTable_length {position: relative;}
.dataTables_length select{border:1px solid}
#filingStatusProcess {position:absolute;  z-index: 9; font-size: 23px; left: 50%;  color: #374583; width:30em; height:20px;margin-left: -15em; top:150px}
.dataTables_scrollBody{border-bottom:none!important}
.db-ca-wrap table.dataTable.row-border tbody tr td:first-child{color:#000000!important}
::i-block-chrome, .hdrtitle1{ margin-left:23%!important;}
.db-ca-wrap .dataTables_wrapper .toolbar h4{margin-top:0px!important}
.color-black{color:#000!important}
.dropdown:hover .dropdown-content.reportstatus{display: block;}.arrow-up {width: 0; height: 0; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reportstatus{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 350px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
.drop_btn {position: relative;display: table-cell;}
.drop_content {display: none;position: absolute; min-width: 200px;box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;}
.drop_content a {padding: 12px 16px;text-decoration: none;display: block;}
.drop_btn:hover .drop_content {display: block;left: 80%;background-color: white;top: 9px;padding: 8px;}
.drop_content .arrow-right{ border-right:7px solid white; }
.arrow-right {width: 0; height: 0;border-top: 10px solid transparent;border-bottom: 10px solid transparent;display: inline-flex;position: absolute;left: -7px;top: 5px;}
.dataTables_scrollBody{overflow: unset!important;overflow-x: unset!important;}
.arrow-left {width: 0; height: 0;border-top: 7px solid transparent;border-bottom: 7px solid transparent;border-left: 7px solid white;display: inline-flex;position: absolute;right: -7px;top: 5px;}
</style>
<script type="text/javascript">
var msgTab;
</script>
</head>
<body class="body-cls">
		<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<div class="breadcrumbwrap" >
	<div class="container bread">
	<div class="row">
        <div class="col-md-12 col-sm-12">
		<ol class="breadcrumb">
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
			<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
			<li class="breadcrumb-item active">Filing Status Report</li>
		</ol>
		<div class="retresp"></div>
		</div>
		</div>
		</div>
	</div>
	<div class="db-ca-wrap">
            <div class="container">
			<div class="col-sm-12"><a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right filing_sbtn" role="button" style="padding: 4px 25px;">Back</a></div>
			<br/>
			<div class="helpguide reporthelpguide dropdown helpicon ml-3" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
			<div class="dropdown-content reportstatus"> <span class="arrow-up"></span><span class="pl-2"> All Months GST Filing Status Based on Financial Period</span></div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class="">
				<div class="col-sm-4 pull-right" style=" display: inline;z-index:2">
					<h4 class="hdrtitle1" style="display: inline-block; margin-left:26%; color: #374583;font-size: 20px;">Financial Year : </h4> 
					<select class="pull-right" name="financialYear" id="financialYear">
						<option value="2021">2021-2022</option>
						<option value="2020">2020-2021</option>
						<option value="2019">2019-2020</option>
						<option value="2018">2018-2019</option>
						<option value="2017">2017-2018</option>
					</select>
					</div>	
			</div>
	<div class="bodywrap" style="min-height: 500px;">
		<div class="db-ca-wrap" style="padding-top:0px!important;margin-top:0px!important">
			<div class="container">
				<div class="">
			<div id="filingStatusProcess" class="text-center"></div>
						<div class="customtable db-ca-view tabtable1" style="width:1080px;overflow-y:visible; top:-36px">
							<table id="msgTable"class="display row-border dataTable meterialform"	cellspacing="0" width="100%">
								<thead>
									<tr>
									<th>ReturnType</th><th>April</th><th>May</th><th>June</th><th>July</th><th>August</th><th>September</th><th>October</th><th>November</th><th>December</th><th>January</th><th>February</th><th>March</th>
									</tr>
								</thead>
								<tbody>
									<c:set var="varPending" value="<%=com.mastergst.core.common.MasterGSTConstants.PENDING%>"/>
								<c:forEach items="${lGSTReturnsSummury}" var="GSTReturnsSummury">
									<c:if test='${GSTReturnsSummury.active == "true"}'>
									<c:if test="${GSTReturnsSummury.returntype ne varGSTR2}">
										<tr>
											<td class="text-left">
                                    			<c:choose>
														<c:when test="${GSTReturnsSummury.returntype eq varGSTR1 || GSTReturnsSummury.returntype eq varGSTR3B || GSTReturnsSummury.returntype eq varGSTR4 || GSTReturnsSummury.returntype eq varGSTR6}">
															<a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${GSTReturnsSummury.returntype}"/>/<c:out value="${month}"/>/<c:out value="${year}"/>?type="><span class="color-black">${GSTReturnsSummury.returntype}</span></a>
														</c:when>
														<c:otherwise>
															<span  href="#" data-toggle="tooltip" data-placement="bottom" title="This feature will be made available over coming weeks on schedule with GSTN release">${GSTReturnsSummury.returntype}</span>
														</c:otherwise>
												</c:choose>
											</td>
												<c:forEach var="i" begin="4" end="12">
													<td id="id${GSTReturnsSummury.returntype}${i}" class="drop_btn dropdown"></td>
												</c:forEach>
												<c:forEach var="j" begin="1" end="3">
													<td id="id${GSTReturnsSummury.returntype}${j}" class="drop_btn dropdown"></td>
												</c:forEach>	
										</tr>
										</c:if>
									</c:if>
								</c:forEach>
								</tbody>
							</table><span style="float:right;font-size:13px;position: absolute;right:0px;bottom: 27px;">Note: The above <span class="color-red fa fa-circle" style="font-size:8px;"> </span> Indicates Late Filing</span>
						</div>
				

				</div>
			</div>
		</div>
	</div>
	</div>
	</div>
	<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Help To Read This Report </h3></div>
        <div class=" p-2 steptext-wrap"><span class="pl-2"> All Months GST Filing status Based on Financial Period</span> </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->

</body>
<script type="text/javascript">
$('.drop_btn').hover(function() {
 $(this).find('.drop_content').show();
	}, function() {
		 $(this).find('.drop_content').hide();
	});
$('.tabtable1 tr td:nth-child(13) , .tabtable1 tr td:nth-child(12) , .tabtable1 tr td:nth-child(11)').hover(function() {
	$('.dropdown:hover .dropdown-content').css({'right':'80px','left':'unset'});$(this).find('span:first').removeClass('arrow-right').addClass('arrow-left');
}, function() {$('.dropdown:hover .dropdown-content').css({'right':'unset','left':'80%'});$(this).find('span:first').addClass('arrow-right').removeClass('arrow-left');});
function formatUpdatedDate(dat, type, row){
	var createdDt = new Date(dat.updatedDate) ;
    var month = createdDt.getUTCMonth() + 1; 
	var day = createdDt.getUTCDate();
	var year = createdDt.getUTCFullYear();
	return day+'-'+month+'-'+year;
}

var currDate = new Date();
$(document).ready(function(){
	$( ".helpicon" ).hover(function() {$('.reportstatus').show();
	}, function() {$('.reportstatus').hide();
	});
	$('#msgs_lnk').addClass('active');
	$('.nonAspReports').addClass('active');
	msgTab = $('#msgTable').DataTable({
			dom: '<"toolbar">frtip', 
			"pageLength": 10,
			scrollX:        true,
        scrollCollapse: true,
        bFilter:false,
			
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
					"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
				}
			}
		});
	$('.tabtable1 div.toolbar').html('<h4 class="hdrtitle">GST Filing Status Report</h4><a type="button" class="btn btn-primary" style="color:white; box-shadow:none; font-size:14px;position: absolute;left: 44%;margin-top: -10px;z-index: 2;" onclick="refreshStatusSummary()">Filing Status<i class="fa fa-refresh" id="flngstatusrefreshSummary" style="font-size: 14px; color: #fff; margin-left:5px;"></i></a><a class="btn btn-blue" id="filingStatus_excel" style="position: absolute;left: 57%;margin-top: -10px;z-index: 2;">DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
	var year = currDate.getFullYear();
	var month = currDate.getMonth()+1;
	var initialYear = year;
	msgTab.clear();
	if(month <= 3){
		initialYear--;
	}
	$('#financialYear').val(initialYear);
	updateFilingStatus(initialYear,4);
	});
$('#financialYear').change(function() {
	var finYear = $(this).val();
	updateFilingStatus(finYear,4);
});
function refreshStatusSummary(){
		var financialyear=$('#financialYear').val();
		var pmonth = parseInt('${month}');
		$('#flngstatusrefreshSummary').addClass('fa-spin');
	$.ajax({
		url: "${contextPath}/clntflngstatuss/${client.id}/"+financialyear,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		beforeSend: function () {
			$('#filingStatusProcess').text('Please wait, Loading Data...');
		},
		success : function(summary) {
			var currentDates = new Date();var currentYear = currentDates.getFullYear();
			if(financialyear <= currentYear){
				pmonth = 4;
			}
			updateFilingStatus(financialyear , pmonth);
			window.setTimeout(function(){
				$('#flngstatusrefreshSummary').removeClass("fa-spin");
				successNotification('Client Filing Status Refresh Successfully...');
				$('#filingStatusProcess').text(' ');
				}, 4000);
		},error: function(data) {$('#filingStatusProcess').text(' ');}	
	});
	}
function updateFilingStatus(year,month){
	var years = year;
	$('#filingStatus_excel').attr('href','${contextPath}/dwnldfilingSummary/${id}/${client.id}/'+month+'/'+year);
	var cDate = new Date();
	var cyear = cDate.getFullYear();
	var cmonth = cDate.getMonth()+1;
	if(month <= 3){
		years--;
	}
	var monthstatus = ""+month+year;
	if(month == '9' || month == '8' || month == '7' || month == '6' || month == '5' || month == '4' || month == '3' || month == '2' || month == '1'){
		monthstatus = '0'+monthstatus;
	}
	$.ajax({
		//url: "${contextPath}/clntflngstatus/${client.id}/"+year,
		url: "${contextPath}/clntflngstatuss/${client.id}/"+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		beforeSend: function () {
			$('#filingStatusProcess').text('Please wait, Loading Data...');
		},
		success : function(summary) {
			if(summary) {
				Object.keys(summary).forEach(function(rType) {
					Object.keys(summary[rType]).forEach(function(rPeriod) {
						var content = "";var dDate="";var fDate = "";
								if(summary[rType][rPeriod][0] == 'Filed') {
									var duedate="";	var sm;
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
										if(rType == 'GSTR1'){if(rPeriod.substring(0,2) == '12'){sm=1;duedate = '11-' +sm+ "-" +nextyear;
									       dDate = duedate;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;
									       duedate = '11-' +sm+ "-" +years;dDate=duedate;}
									    }
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR1'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '11-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									/* if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
										if(rType == 'GSTR2'){
											if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
											duedate = '15-' +sm+ "-" +years;
										}
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR2'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '15-' +sm+ "-" +nextyear;}
									} */
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
										if(rType == 'GSTR3B'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
									       duedate = '20-' +sm+ "-" +years;dDate=duedate;
									}
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR3B'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '20-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
										if(rType == 'GSTR8'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
									       duedate = '10-' +sm+ "-" +years;dDate=duedate;
									    }
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR8'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '10-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '03'|| rPeriod.substring(0,2) == '02'|| rPeriod.substring(0,2) == '01'){
										if(rType == 'GSTR9'){var nextyear = (parseInt(years)+1).toString();  duedate = '31-' +12+ "-" +nextyear;dDate=duedate;}
									}
									if(rType == 'GSTR1' || rType == 'GSTR6' || rType == 'GSTR4' || rType == 'GSTR3B' || rType == 'GSTR8'){
										if(filingOption == 'Quarterly' && rType != 'GSTR3B'){
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
												if(summary[rType][rPeriod][2] != null){
													content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';
												}else{
													content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>----</span></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';
												}
											}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){
												if(summary[rType][rPeriod][2] != null){
													content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43x;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';												
												}else{
													content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43x;margin-right: 5px;">:</span><span>----</span></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';
												}
											}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){
												if(summary[rType][rPeriod][2] != null){
													content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';
												}else{
													content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>----</span></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';												
												}
											}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){
												if(summary[rType][rPeriod][2] != null){
													content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';												
												}else{
													content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>----</span></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';												
												}		
											}
										}else{											
											if(summary[rType][rPeriod][2] != null){
												content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';											
											}else{
												content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>----</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';
											}
										}
									}else{
										if(summary[rType][rPeriod][2] != null){
											content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>'+summary[rType][rPeriod][2]+'</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';										
										}else{
											content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:green;">'+summary[rType][rPeriod][0]+'</span><span style="color:red;margin-left: 3px;" class="'+rType+'lateFile_txt'+rPeriod.substring(0,2)+'"></span></strong></div><div>ARN <span class="colon" style="margin-left: 43px;margin-right: 5px;">:</span><span>----</span></div></div><span class="'+rType+'lateFile_class'+rPeriod.substring(0,2)+'"></span><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-green">'+summary[rType][rPeriod][0]+'</span></a>';
										}
									}
								}else if(summary[rType][rPeriod][0] == 'Submitted') {
									var duedate="";	var sm;
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
										if(rType == 'GSTR1'){if(rPeriod.substring(0,2) == '12'){sm=1;var nextyear = (parseInt(years)+1).toString();duedate = '11-' +sm+ "-" +nextyear;
									       dDate = duedate;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;
									       duedate = '11-' +sm+ "-" +years;dDate=duedate;}
									    }
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR1'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '11-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									/* if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
										if(rType == 'GSTR2'){
											if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
											duedate = '15-' +sm+ "-" +years;
										}
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR2'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '15-' +sm+ "-" +nextyear;}
									} */
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
										if(rType == 'GSTR3B'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
									       duedate = '20-' +sm+ "-" +years;dDate=duedate;
									}
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR3B'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '20-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
										if(rType == 'GSTR8'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
									       duedate = '10-' +sm+ "-" +years;dDate=duedate;
									    }
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR8'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '10-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '03'|| rPeriod.substring(0,2) == '02'|| rPeriod.substring(0,2) == '01'){
										if(rType == 'GSTR9'){var nextyear = (parseInt(years)+1).toString();  duedate = '31-' +12+ "-" +nextyear;dDate=duedate;}
									}
									if(rType == 'GSTR1' || rType == 'GSTR6' || rType == 'GSTR4' || rType == 'GSTR3B'  || rType == 'GSTR8'){
										if(filingOption == 'Quarterly' && rType != 'GSTR3B'){
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
												content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-blue">'+summary[rType][rPeriod][0]+'</span></a>';
											}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){
												content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-blue">'+summary[rType][rPeriod][0]+'</span></a>';
											}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){
												content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-blue">'+summary[rType][rPeriod][0]+'</span></a>';
											}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){
												content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-blue">'+summary[rType][rPeriod][0]+'</span></a>';
											}
										}else{											
											content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-blue">'+summary[rType][rPeriod][0]+'</span></a>';
										}
									}else{
										if(rPeriod.substring(0,2) == '03'){
											content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>'+summary[rType][rPeriod][1]+'</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:blue;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-blue">'+summary[rType][rPeriod][0]+'</span></a>';
										}else{
											content = '<h6 style="text-align:center"> - </h6>';
										}
									}
								}  else {
									var duedate="";	var sm;
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
										if(rType == 'GSTR1'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
									       duedate = '10-' +sm+ "-" +years;dDate=duedate;
									    }
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR1'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '10-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									/* if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
										if(rType == 'GSTR2'){
											if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
											duedate = '15-' +sm+ "-" +years;
										}
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR2'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '15-' +sm+ "-" +nextyear;}
									} */
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){
										if(rType == 'GSTR3B'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
									       duedate = '20-' +sm+ "-" +years;dDate=duedate;
									}
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR3B'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '20-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04'){											
										if(rType == 'GSTR8'){if(rPeriod.substring(0,2) == '12'){sm=1;}else{sm = parseInt(rPeriod.substring(0,2)) + 1;}
									       duedate = '10-' +sm+ "-" +years;dDate=duedate;
									    }
									}
									if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
										if(rType == 'GSTR8'){sm = parseInt(rPeriod.substring(0,2)) + 1;var nextyear = (parseInt(years)+1).toString();duedate = '10-' +sm+ "-" +nextyear;dDate=duedate;}
									}
									if(rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06' || rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09' || rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12' || rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '03'|| rPeriod.substring(0,2) == '02'|| rPeriod.substring(0,2) == '01'){
										if(rType == 'GSTR9'){var nextyear = (parseInt(years)+1).toString();  duedate = '31-' +12+ "-" +nextyear;dDate=duedate;}
									}
									if(rType == 'GSTR1' || rType == 'GSTR6' || rType == 'GSTR4' || rType == 'GSTR3B' || rType == 'GSTR8'){
										if(filingOption == 'Quarterly' && rType != 'GSTR3B'){
											if(rPeriod.substring(0,2) == '01' || rPeriod.substring(0,2) == '02' || rPeriod.substring(0,2) == '03'){
												content = '<div class="dropdown-content drop_content"><span class="arrow-right"></span><div>Due date  <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-red">'+summary[rType][rPeriod][0]+'</span></a>';
											}else if(rPeriod.substring(0,2) == '04' || rPeriod.substring(0,2) == '05' || rPeriod.substring(0,2) == '06'){
												content = '<div class="dropdown-content drop_content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-red">'+summary[rType][rPeriod][0]+'</span></a>';
											}else if(rPeriod.substring(0,2) == '07' || rPeriod.substring(0,2) == '08' || rPeriod.substring(0,2) == '09'){
												content = '<div class="dropdown-content drop_content"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-red">'+summary[rType][rPeriod][0]+'</span></a>';
											}else if(rPeriod.substring(0,2) == '10' || rPeriod.substring(0,2) == '11' || rPeriod.substring(0,2) == '12'){
												content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-red">'+summary[rType][rPeriod][0]+'</span></a>';
											}
										}else{											
												content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-red">'+summary[rType][rPeriod][0]+'</span></a>';
										}
									} else {		
										if(rPeriod.substring(0,2) == '03'){
											content = '<div class="dropdown-content drop_content" style="display:none"><span class="arrow-right"></span><div>Due date <span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span>'+duedate+'</span></div><div>Filing Date <span class="colon" style="margin-left: 7px;margin-right: 3px;">:</span><span>----</span></div><div>Filing Satus <span class="colon" style="margin-left: 2px;margin-right: 5px;">:</span><strong><span style="color:red;">'+summary[rType][rPeriod][0]+'</span></strong></div></div><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/'+rType+'/'+rPeriod.substring(0,2)+'/'+rPeriod.substring(2,6)+'?type="><span class="color-red">'+summary[rType][rPeriod][0]+'</span></a>';
										}else{
											content = '<h6 style="text-align:center"> - </h6>';
										}
									}	
								}
							$('#id'+rType+parseInt(rPeriod.substring(0,2))).html(content);
							 fDate = summary[rType][rPeriod][1];
								if(fDate != undefined && fDate != null){
							      var dd = dDate.split("-");
								   var d = new Date(dd[2], dd[1] - 1, dd[0]);
								   var ff = fDate.split("-");
								   var f = new Date(ff[2], ff[1] - 1, ff[0]);
								if(d < f){
									$('.'+rType+'lateFile_class'+rPeriod.substring(0,2)).addClass("fa fa-circle");
									$('.'+rType+'lateFile_class'+rPeriod.substring(0,2)).css({'color':'red','font-size':'8px','display':'inline','vertical-align':'text-top'});
									$('.'+rType+'lateFile_txt'+rPeriod.substring(0,2)).text("(Late Filed)");
								}else{
									$('.'+rType+'lateFile_txt'+rPeriod.substring(0,2)).text("");
								}
							}
								if(rPeriod == monthstatus){
									$('#status'+rType).html(summary[rType][rPeriod]);
									if(summary[rType][rPeriod] == 'Filed') {
										$('#status'+rType).addClass("color-green");
									}else if(summary[rType][rPeriod] == 'Submitted'){
										$('#status'+rType).removeClass('color-green color-red').addClass("color-blue");
									}else{
										$('#status'+rType).removeClass('color-green color-blue').addClass("color-red");
									}
								}
								if(rType != 'GSTR9'){
									if(years == '2017'){
										$('#id'+rType+parseInt('4')).html('<h6 style="text-align:center"> - </h6>');
										$('#id'+rType+parseInt('5')).html('<h6 style="text-align:center"> - </h6>');
										$('#id'+rType+parseInt('6')).html('<h6 style="text-align:center"> - </h6>');
										$('#id'+rType+parseInt('7')).html('<h6 style="text-align:center"> - </h6>');
									}
									if(cyear == year){
										if(cmonth == 1 || cmonth == 2 || cmonth == 3){
											for(var mth=cmonth+1;mth<=3;mth++){
												$('#id'+rType+parseInt(mth)).html('<h6 style="text-align:center"> - </h6>');
											}
										}else{
											for(var mth=cmonth+1;mth<=12;mth++){
												$('#id'+rType+parseInt(mth)).html('<h6 style="text-align:center"> - </h6>');
											}
											for(var mth=1;mth<=3;mth++){
												$('#id'+rType+parseInt(mth)).html('<h6 style="text-align:center"> - </h6>');
											}
										}
									}	
					         }
					});
				});
			}
			$('#filingStatusProcess').text(' ');
		}
	});
	
}

</script>

</html>