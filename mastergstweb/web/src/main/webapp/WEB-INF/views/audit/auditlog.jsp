<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
	<head>
		<title>MasterGST | Client Audit Log</title>
		<%@include file="/WEB-INF/views/includes/profile_script.jsp" %>
		<link rel="stylesheet" href="${contextPath}/static/mastergst/css/userprofile_edit/reminder.css" media="all" />
		<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
		<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
 <link rel="stylesheet" href="${contextPath}/static/mastergst/css/audit/audit.css" media="all" />
		<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
		<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
		<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
		<script src="${contextPath}/static/mastergst/js/audit/auditlog.js" type="text/javascript"></script>
		<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
		<style>
.bdrbtm { width:100%; margin:10px auto;border-bottom:1px solid #000; }
.db-ca-wrap table.dataTable.row-border tbody tr td:first-child{color:#354052!important}
</style>
	</head>
	<body class="body-cls">
		<!-- header page begin -->
		<c:choose>
			<c:when test='${not empty client && not empty client.id}'>
				<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
			</c:when>
			<c:otherwise>
				<%@include file="/WEB-INF/views/includes/newclintheader.jsp" %>
			</c:otherwise>
		</c:choose>
		<div class="breadcrumbwrap">
			<div class="container">
				<div class="row">
					<div class="col-md-12 col-sm-12">
						<ol class="breadcrumb">
							<li class="breadcrumb-item">
								<c:choose>
									<c:when test="${usertype eq userCenter}"><a href="#" class="urllink" link="${contextPath}/cp_centers/<c:out value=" ${id} "/>/<c:out value="${fullname} "/>/<c:out value="${usertype} "/>"/>Admin</a>
									</c:when>
									<c:otherwise><a href="#" class="urllink" link="${contextPath}/teamuser/<c:out value=" ${id} "/>/<c:out value="${fullname} "/>/<c:out value="${usertype} "/>"/>Admin</a>
									</c:otherwise>
								</c:choose>
							</li>
							<li class="breadcrumb-item active">Audit Log</li>
						</ol>
						<div class="retresp"></div>
					</div>
				</div>
			</div>
		</div>
			<div class="db-ca-wrap monthely1">
		<div class="container">
			
						<div class="">
						<h2 style="position:absolute;z-index:2;">Audit Log</h2>
				<div class="dropdown chooseteam mr-0" style="z-index:2;">
					
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
						<div class="typ-ret" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="filingoption" class="filingoption" style="vertical-align: top;">Today</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						</div>
					</span>
					<div class="dropdown-menu ret-type" id="dropdownmenu" style="width: 108px !important; min-width: 36px; left: 34%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="Today" onClick="getval('Today')">Today</a><a class="dropdown-item" href="#" value="Yesterday" onClick="getval('Yesterday')">Yesterday</a><a class="dropdown-item" href="#" value="Week" onClick="getval('Last Week')">Last Week</a><a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a><a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
					</div>
					<span class="datetimetxt today-sp" style="display: block" id="today-sp">
						<div class="datetimetxt datetime-wrap pull-right">
							<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
						</div>
					</span>
					<span class="datetimetxt yesterday-sp" id="yesterday-sp" style="display:none">
						<div class="datetimetxt datetime-wrap pull-right">
							<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
						</div>
					</span>
					<span class="datetimetxt weekly-sp" id="weekly-sp" style="display:none">
						<div class="datetimetxt datetime-wrap pull-right">
							<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
						</div>
					</span>
					<span class="datetimetxt monthely-sp" id="monthely-sp" style="display:none"> <span><label id="ret-period">Report Period:</label></span>
						<div class="datetimetxt datetime-wrap pull-right">
							<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
									<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
						</div>
					</span> 
					<span style="display:none" class="yearly-sp"> 
						<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="filingoption1"	style="margin-right: 10px; display: inline-flex;">
							<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
							<div class="typ-ret type_ret_yearly" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
								<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
								<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
									<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
								</span>
							</div>
						</span>
						<div class="dropdown-menu ret-type1" id="financialYear1" style="width: 108px !important; min-width: 36px; left: 63%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
						</div>
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
					</span>
					<span class="datetimetxt custom-sp" style="display:none" id="custom-sp">
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="getdiv()">Generate</button>
						<div class="datetimetxt datetime-wrap to-picker">
						<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
							<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 8px; height: 28px; margin-right: 10px;">
								<input type="text" class="form-control totime" value="02-2012"	readonly="">
								<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>
						</div>
						<div class="datetimetxt datetime-wrap dpfromtime">
							<label	style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
							<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
								<input type="text" class="form-control fromtime" value="02-2012"	readonly="">
								<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>	
						</div>
					</span>
				</div>
			</div>
			<div class=" "></div>
		<div class="tab-pane" id="gtab1" role="tabpanel">
		<div class="normaltable meterialform updateExpensefilter_summary" id="updateExpensefilter_summary">
		<div class="filter efilter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="sfilter">
					<span id="divAuditFilters"></span>
					<span class="btn-remove-tag" onclick="clearAuditFilter()">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Search Filter</div>
					<div class="noramltable-row-desc">
						<select id="multiselectAudit1" class="multiselect-ui form-control multiselectAudit1" multiple="multiple"></select>	
						<select id="multiselectAudit2" class="multiselect-ui form-control multiselectAudit2" multiple="multiple"></select>	
						<select id="multiselectAudit3" class="multiselect-ui form-control multiselectAudit3" multiple="multiple">
							<option value="Save As Draft">Save As Draft</option>
							<option value="Save Invoice">Save Invoice</option>
							<option value="Edit and Save">Edit and Save</option>
							<option value="Generate IRN">Generate IRN</option>
							<option value="All Invoices Generate IRN">All Invoices Generate IRN</option>
							<option value="Sync IRN">Sync IRN</option>
							<option value="Cancel IRN">Cancel IRN</option>
							<option value="Cancelled Selected IRN">Cancelled Selected IRN</option>
							<option value="Delete Invoice">Delete Invoice</option>
							<option value="Delete Selected Invoices">Delete Selected Invoices</option>
							<option value="Delete All Invoices">Delete All Invoices</option>
							<option value="Cancelled">Cancelled</option>
							<option value="Generate EwayBill">Generate EwayBill</option>
							<option value="Cancel EwayBill">Cancel EwayBill</option>
							<option value="Update Vehicle">Update Vehicle</option>
							<option value="Reject EwayBill">Reject EwayBill</option>
							<option value="Extend EwayBill Validity">Extend EwayBill Validity</option>
						</select>		
					</div>
			</div>
	</div>
			<div class="customtable db-ca-view salestable reportsdbTable auditlogtable">
			<a href="#" id="auditlogexcel" class="btn btn-blue excel">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>
				<table id='gloablReports_dataTable' class="row-border dataTable meterialform" cellspacing="0" width="100%">
					<thead>
						<tr>
							<th style="width:200px!important">Client Name</th><th class="text-center" style="width:145px!important">Action</th><th class="text-center">Description</th><th class="text-center">User</th><th class="text-center" style="width:145px!important">Date</th>
						</tr>
					</thead>
					<tbody id='invBodysalestable1'></tbody>
				</table>
			</div>
		</div>
	</div>
</div>
		<%@include file="/WEB-INF/views/includes/footer.jsp" %>
	</body>
	
	<div class="modal fade" id="alldetails" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-right" role="document">
            <div class="modal-content">
				<div class="modal-header p-0">
				 <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Audit Log Details</h3>
                    </div>
				</div>
                <div class="modal-body meterialform popupright bs-fancy-checks">
                     <!-- row begin -->
			<div class="pr-4 pl-4 pt-3" id="srcgsitn">
            <div class="row colrow">
            <div class="colhr col-md-3 col-sm-12"> Business Name<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-9 col-sm-12 clientname aud"></div>
              
              <div class="colhr col-md-3 col-sm-12"> GSTIN<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-9 col-sm-12 gstn aud"></div>
            
              <div class="colhr col-md-3 col-sm-12"> User Name<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-9 col-sm-12 username aud"></div>
           
              <div class="colhr col-md-3 col-sm-12"> User Email<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-9 col-sm-12 useremail aud"></div>
           
              <div class="colhr col-md-3 col-sm-12"> Return Type<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-9 col-sm-12 returntype aud"></div>
            
              <div class="colhr col-md-3 col-sm-12"> Action<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-9 col-sm-12 action aud"></div>
            
              <div class="colhr col-md-3 col-sm-12"> Description<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-9 col-sm-12 description aud"></div>
           
              <div class="colhr col-md-3 col-sm-12"> Date<span class="coln-txt pull-right">:</span> </div>
              <div class="colcon col-md-9 col-sm-12 credate aud"></div>
            </div>
                    <!-- row end -->
                </div>
                		<div class="pr-4 pl-4">
            <div class="row colrow details">
            <div class="bdrbtm"></div>
            <div class="colhr col-md-3 col-sm-12"> <h6>Field Name</h6></div>
            <div class="colhr col-md-4 col-sm-12"> <h6>Previous Values</h6></div>
            <div class="colhr col-md-4 col-sm-12"> <h6>Updated Values</h6></div>
             <div class="bdrbtm"></div>
            	<div class="colhr col-md-3 col-sm-12 OINVOICENUMBER"> Invoice Number</div>
              	<div class="colcon col-md-4 col-sm-12 oinvoiceNumber aud OINVOICENUMBER"></div>
              	<div class="colcon col-md-4 col-sm-12 ninvoiceNumber aud OINVOICENUMBER"></div>
              	 <div class="bdrbtm OINVOICENUMBER"></div>
              
              <div class="colhr col-md-3 col-sm-12 OINVOICEDATE"> Invoice Date</div>
              <div class="colcon col-md-4 col-sm-12 oinvoiceDate aud OINVOICEDATE"></div>
              <div class="colcon col-md-4 col-sm-12 ninvoiceDate aud OINVOICEDATE"></div>
              <div class="bdrbtm OINVOICEDATE"></div>
              
              <div class="colhr col-md-3 col-sm-12 OTOTALTAXABLEAMOUNT"> Taxable Amount</div>
              <div class="colcon col-md-4 col-sm-12 ototaltaxableamount aud OTOTALTAXABLEAMOUNT indformat"></div>
              <div class="colcon col-md-4 col-sm-12 ntotaltaxableamount aud OTOTALTAXABLEAMOUNT indformat"></div>
              <div class="bdrbtm OTOTALTAXABLEAMOUNT"></div>
              
              <div class="colhr col-md-3 col-sm-12 OTOTALAMOUNT"> Total Amount</div>
              <div class="colcon col-md-4 col-sm-12 ototalamount aud OTOTALAMOUNT indformat"></div>
              <div class="colcon col-md-4 col-sm-12 ntotalamount aud OTOTALAMOUNT indformat"></div>
              <div class="bdrbtm OTOTALAMOUNT"></div>
              
              <div class="colhr col-md-3 col-sm-12 OTOTALIGSTAMOUNT"> Total IGST Amount</div>
              <div class="colcon col-md-4 col-sm-12 ototalIGSTAmount aud OTOTALIGSTAMOUNT indformat"></div>
              <div class="colcon col-md-4 col-sm-12 ntotalIGSTAmount aud OTOTALIGSTAMOUNT indformat"></div>
              <div class="bdrbtm OTOTALIGSTAMOUNT"></div>
              
              <div class="colhr col-md-3 col-sm-12 OTOTALCGSTAMOUNT"> Total CGST Amount</div>
              <div class="colcon col-md-4 col-sm-12 ototalCGSTAmount aud OTOTALCGSTAMOUNT indformat"></div>
              <div class="colcon col-md-4 col-sm-12 ntotalCGSTAmount aud OTOTALCGSTAMOUNT indformat"></div>
              <div class="bdrbtm OTOTALCGSTAMOUNT"></div>
              
              <div class="colhr col-md-3 col-sm-12 OTOTALSGSTAMOUNT"> Total SGST Amount</div>
              <div class="colcon col-md-4 col-sm-12 ototalSGSTAmount aud OTOTALSGSTAMOUNT indformat"></div>
              <div class="colcon col-md-4 col-sm-12 ntotalSGSTAmount aud OTOTALSGSTAMOUNT indformat"></div>
              <div class="bdrbtm OTOTALSGSTAMOUNT"></div>
              
              <div class="colhr col-md-3 col-sm-12 OTOTALCESSAMOUNT"> Total CESS Amount</div>
              <div class="colcon col-md-4 col-sm-12 ototalCESSAmount aud OTOTALCESSAMOUNT indformat"></div>
              <div class="colcon col-md-4 col-sm-12 ntotalCESSAmount aud OTOTALCESSAMOUNT indformat"></div>
              <div class="bdrbtm OTOTALCESSAMOUNT"></div>
            </div>
                    <!-- row end -->
                </div>
            </div>
            <div class="modal-footer">
				<a href="#" class="btn btn-blue-dark ml-2" data-dismiss="modal" aria-label="Close">Close</a>
				</div>
        </div>
    </div>
    </div>
	<script>
	var clientidsArray=new Array();
	var clntids = '${lClientids}';
	$(document).ready(function(){
		$('.audithead').addClass('active');
		<c:forEach items="${allClients}" var="clients">
			$(".multiselectAudit2").append($("<option></option>").attr("value","${clients.id}").html("${clients.businessname}- <span> ${clients.gstnnumber}</span>"));
		</c:forEach>
		$(".multiselectAudit1").append($("<option></option>").attr("value","${id}").text(globaluser)); 
		<c:forEach items="${userList}" var="users">
			$(".multiselectAudit1").append($("<option></option>").attr("value","${users.id}").html("${users.fullname}"));
		</c:forEach>
		$('.multiselectAudit2').multiselect({
			nonSelectedText: '- client/business Name -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyAuditFilters();
			},
			onSelectAll: function() {
				applyAuditFilters();
			},
			onDeselectAll: function() {applyAuditFilters();}
		});
		
		$('.multiselectAudit1').multiselect({
			nonSelectedText: '- User -',
			includeSelectAllOption: true,
			onChange: function(element, checked) {
				applyAuditFilters();
			},
			onSelectAll: function() {
				applyAuditFilters();
			},
			onDeselectAll: function() {applyAuditFilters();}
		});
		
		clntids = clntids.replace('[','');
		clntids = clntids.replace(']','');
		clientidsArray = clntids.split(',');
		var todaydate = new Date();
		var tmonth = '' + (todaydate.getMonth() + 1);
		var tday = '' + todaydate.getDate();
		var tyear = todaydate.getFullYear();
		if(tmonth.length < 2) tmonth = '0' + tmonth;
		if(tday.length < 2) tday = '0' + tday;
		var dt = tday+'-'+tmonth+'-'+tyear;
		initiateAuditCallBacksForMultiSelect();
		initializeAuditRemoveAppliedFilters();
		auditlogDownloads('${id}', dt, dt,'Today',clientidsArray);
		loadauditInvTable('${id}', dt, dt,'Today',clientidsArray);
	});
	function getdiv() {
		var abc = $('#filingoption').html();
		clearAuditFilter();
		if(abc == 'Today'){
			$('span#fillingoption').css("vertical-align", "middle");
			var todaydate = new Date();
			var month = '' + (todaydate.getMonth() + 1);
			var day = '' + todaydate.getDate();
			var year = todaydate.getFullYear();
			if(month.length < 2) month = '0' + month;
			if(day.length < 2) day = '0' + day;
			var dt = day+'-'+month+'-'+year;
			auditlogDownloads('${id}', dt, dt,abc,clientidsArray);
			loadauditInvTable('${id}', dt, dt,abc,clientidsArray);
		}else if(abc == 'Yesterday'){
			$('span#fillingoption').css("vertical-align", "middle");
			var todaydate = new Date();
			var ydate = todaydate.setDate(todaydate.getDate() - 1);
			var yesterdaydate = new Date(ydate);
			var month = '' + (yesterdaydate.getMonth() + 1);
			var day = '' + (yesterdaydate.getDate());
			var year = yesterdaydate.getFullYear();
			if(month.length < 2) month = '0' + month;
			if(day.length < 2) day = '0' + day;
			var dt = day+'-'+month+'-'+year;
			auditlogDownloads('${id}', dt, dt,abc,clientidsArray);
			loadauditInvTable('${id}', dt, dt,abc,clientidsArray);
		}else if(abc == 'Last Week'){
			$('span#fillingoption').css("vertical-align", "middle");
			var todaydate = new Date();
			var tmonth = '' + (todaydate.getMonth() + 1);
			var tday = '' + todaydate.getDate();
			var tyear = todaydate.getFullYear();
			if(tmonth.length < 2) tmonth = '0' + tmonth;
			if(tday.length < 2) tday = '0' + tday;
			var tdt = tday+'-'+tmonth+'-'+tyear;
			var ydate = todaydate.setDate(todaydate.getDate() - 7);
			var yesterdaydate = new Date(ydate);
			var month = '' + (yesterdaydate.getMonth() + 1);
			var day = '' + (yesterdaydate.getDate());
			var year = yesterdaydate.getFullYear();
			if(month.length < 2) month = '0' + month;
			if(day.length < 2) day = '0' + day;
			var dt = day+'-'+month+'-'+year;
			auditlogDownloads('${id}', dt, tdt,abc,clientidsArray);
			loadauditInvTable('${id}', dt, tdt,abc,clientidsArray);
		}else if(abc == 'Monthly'){
			$('span#fillingoption').css("vertical-align", "middle");
			var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
			auditlogDownloads('${id}', mn, yr,abc,clientidsArray);
			loadauditInvTable('${id}', mn, yr,abc,clientidsArray);
		}else if (abc == 'Yearly') {
			$('span#filingoption').css("vertical-align", "bottom");		
			var year=$('#yearlyoption').html().split("-");
			auditlogDownloads('${id}', 0, year[1],abc,clientidsArray);
			loadauditInvTable('${id}', 0, year[1],abc,clientidsArray);
		}else{
			$('span#filingoption').css("vertical-align","bottom");$('#GSTR1SummaryTable').css("display", "block");
			var fromtime = $('.fromtime').val();
			var totime = $('.totime').val();
			$('.fromtime').val(fromtime);$('.totime').val(totime);
			auditlogDownloads('${id}', fromtime, totime,abc,clientidsArray);
			loadauditInvTable('${id}', fromtime, totime,abc,clientidsArray);
		}
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
	
	function populateElement(id,modalid) {
		$('#'+modalid).modal('show');
		$('.colhr,.colcon,.bdrbtm').css("display","block");
		$('.aud').html("");
		$.ajax({
			url: '${contextPath}/auditlogDetails/'+id,
			contentType: false,
			cache: false,
			success: function(retResponse) {
				if(retResponse) {
					if(modalid == 'alldetails'){
						Object.keys(retResponse).forEach(function(key) {
							if(key == 'auditingFields'){
								if(retResponse.auditingFields == null){
									$('.details').css("display","none");
								}else{
									$('.details').css("display","inline-flex");
									Object.keys(retResponse.auditingFields).forEach(function(auditingFieldskey) {
										if(retResponse.auditingFields[auditingFieldskey] == null){
											var ids = auditingFieldskey.toUpperCase();
											$('.'+ids).css("display","none");
										}else{
											$('.'+auditingFieldskey).html(retResponse.auditingFields[auditingFieldskey]);
										}
									});		
								}
							}else{
								$('.'+key).html(retResponse[key]);
								if(key == 'createdDate'){
									$('.credate').html(formatDate(retResponse[key]));
									
								}
							}
						});
						$(".indformat").each(function(){
						    $(this).html($(this).html().replace(/,/g , ''));
						});
						OSREC.CurrencyFormatter.formatAll({
							selector: '.indformat'
						});
					}
				}
			},
			error: function(e, status, error) {
				if(e.responseText) {
					$('.validform').text(e.responseText);
				}
			}
		});
	}
	</script>
</html>