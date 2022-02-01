<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<c:set var="varRetType" value="<%=MasterGSTConstants.GSTR1%>"/>
<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <link rel="icon" href="static/images/master/favicon.ico" />
    <title>MasterGST | Average Balance report</title>
    <link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
    <%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
    <%@include file="/WEB-INF/views/includes/reports_script.jsp"%>
    <style>
    .datepicker.datepicker-dropdown.dropdown-menu.datepicker-orient-left.datepicker-orient-bottom{top: 209px!important;}
    </style>
</head>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
    <div class="breadcrumbwrap">
	    <div class="container bread">
	     	<div class="row">
	        	<div class="col-md-12 col-sm-12">
	            	<ol class="breadcrumb">
	                	<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/treports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Travel</a></li>
						<li class="breadcrumb-item active">Average Balance report</li>
	                </ol>
	                <div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/treports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h2>
				<span class="reports-monthly" style="font-size:20px">Average Balance Report</span>
			</h2>
			<br/>
			<div class=""></div>
			
			<div class="">
				<div class="dropdown chooseteam mr-0" style="z-index:2;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
					</span>
							<select class="ret-type" id="selcted-val" onchange="changedval(this)" style="background-color:white;width: 108px !important;border-radius: 2px;border: 1px solid black;margin-right: 6px;">
							    <option value="Daily" onClick="getval('Daily')">Daily</option>
							    <option value="Weekly" onClick="getval('Weekly')">Weekly</option>
							    <option value="Monthly" onClick="getval('Monthly')">Monthly</option>
							    <option value="Yearly" onClick="getval('Yearly')">Yearly</option>
							    <option value="Custom" onClick="getval('Custom')">Custom</option>
							 </select>
					<span class="datetimetxt daily-sp" id="daily-sp"> <span><label id="ret-period">Report Period:</label></span>
						<div class="datetimetxt datetime-wrap pull-right">
							<div class="input-group date dpDaily" id="dpDaily" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" class="form-control daily" id="daily"  value="08-02-2021" readonly=""> 
									<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-daily"></i></span>
							</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						</div>
					</span>
					<span class="datetimetxt weekly-sp" style="display:none"> <span><label id="ret-period">Report Period:</label></span>
						<div class="datetimetxt datetime-wrap pull-right">
							<div class="input-group date dpWeekly" id="dpWeekly" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="width: 200px;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" class="form-control weekly" id="weekly" value="08-02-2021 - 14-02-2021" readonly="" style="opacity:0;z-index:2">
								<span id="week-val" style="z-index: 0;position: absolute;">10-01-2021-To-16-01-2021</span> 
									<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						</div>
					</span> 
					<span class="datetimetxt monthely-sp" style="display:none"> <span><label id="ret-period">Report Period:</label></span>
						<div class="datetimetxt datetime-wrap pull-right">
							<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" class="form-control monthly" id="monthly" value="02-2012" readonly=""> 
									<span class="input-group-addon add-on pull-right"><i class="fa fa-sort-desc" id="date-drop"></i></span>
							</div><button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
						</div>
					</span> 
					<span style="display:none" class="yearly-sp"> 
						<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="filing_option1"	style="margin-right: 10px; display: inline-flex;">
							<label id="ret-period" style="margin-bottom: 3px;">Report Period:</label>
						</span>
						<select class="ret-type1" id="financialYear1" style="width: 108px !important;border-radius: 2px;border: 1px solid black;margin-right: 6px;">
							    <option onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</option>
							    <option onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</option>
							    <option onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</option>
							    <option onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</option>
							    <option onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</option>
						</select>
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
					</span>
					<span class="datetimetxt custom-sp" style="display:none" id="custom-sp">
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
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
			
			<div class="tab-pane" id="gtab1" role="tabpanel">
			    <div class="customtable db-ca-view salestable reports_dataTable">
					<table id='reports_dataTable' class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th class="text-center">Duration</th><th class="text-center">MAB</th>
							</tr>
						</thead>
						<tbody id='invBodyVariableExp'>
							<tr>
								<td class="text-center">22</td>
								<td class="text-center">12</td>
								
							</tr>
							<tr>
								<td class="text-center">23</td>
								<td class="text-center">11</td>
							</tr>
							<tr>
								<td class="text-center">22</td>
								<td class="text-center">12</td>
								
							</tr>
							<tr>
								<td class="text-center">23</td>
								<td class="text-center">11</td>
							</tr>
							<tr>
								<td class="text-center">22</td>
								<td class="text-center">12</td>
								
							</tr>
							<tr>
								<td class="text-center">23</td>
								<td class="text-center">11</td>
							</tr>
							<tr>
								<td class="text-center">22</td>
								<td class="text-center">12</td>
								
							</tr>
							<tr>
								<td class="text-center">23</td>
								<td class="text-center">11</td>
							</tr>
							<tr>
								<td class="text-center">22</td>
								<td class="text-center">12</td>
								
							</tr>
							<tr>
								<td class="text-center">23</td>
								<td class="text-center">11</td>
							</tr>
							<tr>
								<td class="text-center">22</td>
								<td class="text-center">12</td>
								
							</tr>
							<tr>
								<td class="text-center">23</td>
								<td class="text-center">11</td>
							</tr>
							<tr>
								<td class="text-center">22</td>
								<td class="text-center">12</td>
								
							</tr>
							<tr>
								<td class="text-center">23</td>
								<td class="text-center">11</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
    <%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<jsp:include page="/WEB-INF/views/reports/invoicedetails.jsp"/>
	<script src="${contextPath}/static/mastergst/js/reports/variable_expence.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function(){
			var booksOrReturns = '';
			var type='';
			$('#${varRetTypeCode}SummaryTable').css("display", "none");
			loadReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadReportsUsersInDropDown);
			loadReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}', booksOrReturns);
			initiateCallBacksForMultiSelectReports('${varRetType}','${varRetTypeCode}');
			initializeRemoveAppliedFiltersReports('${varRetType}','${varRetTypeCode}');
			
		});
		function generateData() {
			
		}
	</script>
</body>
</html>