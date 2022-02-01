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
    <title>MasterGST | Bus Detail Report</title>
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
						<li class="breadcrumb-item active">Bus Detail Report</li>
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
				<span class="reports-monthly" style="font-size:20px">Bus Detail Report</span>
			</h2>
			<div class=""></div>
			</br>
			
			<div class="tab-pane" id="gtab1" role="tabpanel">
			    <div class="customtable db-ca-view salestable reports_dataTable">
					<table id='reports_dataTable' class="row-border dataTable meterialform" cellspacing="0" width="100%">
						<thead>
							<tr>
								<th>Bus Number</th><th class="text-center">RTA</th><th class="text-center">Fitness</th><th class="text-center">Insurance</th><th class="text-center">Authorization</th><th class="text-center">State Permit</th><th class="text-center">Pollution</th><th class="text-center">RC EXPIRY</th>
							</tr>
						</thead>
						<tbody id='invBodyVariableExp'>
							<tr>
								<td>AP04Z0171</td>
								<td class="text-center">Chittor</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">2025</td>
							</tr>
							<tr>
								<td>AP04Z0171</td>
								<td class="text-center">Hyderabad</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">2022</td>
							</tr>
							<tr>
								<td>AP04Z0131</td>
								<td class="text-center">Warangle</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">2023</td>
							</tr>
							<tr>
								<td>AP04Z0191</td>
								<td class="text-center">Delhi</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">2022</td>
							</tr>
							<tr>
								<td>AP04Z0111</td>
								<td class="text-center">Chittor</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">2025</td>
							</tr>
							<tr>
								<td>TA04Z0142</td>
								<td class="text-center">Hyderabad</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">2022</td>
							</tr>
							<tr>
								<td>AP04Z0101</td>
								<td class="text-center">Warangle</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">2023</td>
							</tr>
							<tr>
								<td>KA04Z0114</td>
								<td class="text-center">Nellore</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">2021</td>
							</tr>
							<tr>
								<td>AP04Z0179</td>
								<td class="text-center">Chittor</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">2025</td>
							</tr>
							<tr>
								<td>AP04Z0188</td>
								<td class="text-center">Hyderabad</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">2022</td>
							</tr>
							<tr>
								<td>AP04Z0152</td>
								<td class="text-center">Warangle</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">2023</td>
							</tr>
							<tr>
								<td>AP04Z0122</td>
								<td class="text-center">hyderabad</td>
								<td class="text-center">YES</td>
								<td class="text-center">NO</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">YES</td>
								<td class="text-center">2022</td>
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
			invsDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', '${month}', '${year}', '${usertype}', '${fullname}', booksOrReturns,'Monthly');
		});
		function generateData() {
			var abc = $('#fillingoption span').html();
			var booksOrReturns = '';
			var type='';
			clearInvFiltersReportss('GSTR1');
			if(abc == 'Monthly'){
				$('#${varRetTypeCode}SummaryTable').css("display", "none");
				$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
				var fp = $('#monthly').val();var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
				var type='';
				loadReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadReportsUsersInDropDown);
				loadReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}', mn, yr, type, loadReportsCustomersInDropdown);
				loadReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', mn, yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
				initializeRemoveAppliedFiltersReports('${varRetType}','${varRetTypeCode}');
				invsDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', mn, yr, '${usertype}', '${fullname}', booksOrReturns, 'Monthly');
			}else if (abc == 'Yearly') {
				$('#${varRetTypeCode}SummaryTable').css("display", "block");
				$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "block");$('.reports-custom').css("display", "none");
				var year=$('#yearlyoption').html().split("-");
				var type='';
				loadReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadReportsUsersInDropDown);
				loadReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}', 0, year[1], type, loadReportsCustomersInDropdown);
				loadReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, 'Yearly');
				loadReportsSummary('${id}','${varRetTypeCode}','${client.id}', 0, year[1], type);
				initiateCallBacksForMultiSelectReports('${varRetType}','${varRetTypeCode}');
				initializeRemoveAppliedFiltersReports('${varRetType}','${varRetTypeCode}');
				invsDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', 0, year[1], '${usertype}', '${fullname}', booksOrReturns, 'Yearly');
				//$('#dwnldxls').attr('href','${contextPath}/dwnldReportsFinancialSummaryxls/${id}/${client.id}/${varRetTypeCode}/'+year[0].trim());
				//loadSummaryForExcelDwnld('${id}', '${client.id}', '${varRetType}',year[0].trim(),null,null,'Yearly');
				//$('#dwnldxls').attr('href','${contextPath}/dwnldReportsFinancialSummaryxls/${id}/${client.id}/${varRetType}?reporttype=invoice_report&year='+year[0].trim()+'&fromdate=null&todate=null');
			}else{
				$('#${varRetTypeCode}SummaryTable').css("display", "block");
				$('.reports-monthly').css("display", "none");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "block");
				var fromtime = $('.fromtime').val();var totime = $('.totime').val();$('.fromtime').val(fromtime);$('.totime').val(totime);
				var type ='custom';
				loadReportsUsersByClient('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', loadReportsUsersInDropDown);
				loadReportsInvoiceSupport('${client.id}', '${varRetType}', '${varRetTypeCode}', fromtime, totime, type, loadReportsCustomersInDropdown);
				loadReportsInvTable('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, type);
				initiateCallBacksForMultiSelectReports('${varRetType}','${varRetTypeCode}');
				initializeRemoveAppliedFiltersReports('${varRetType}','${varRetTypeCode}');
				loadReportsSummary('${id}','${varRetType}','${client.id}', fromtime, totime,'custom');
				invsDownloads('${id}', '${client.id}', '${varRetType}','${varRetTypeCode}', fromtime, totime, '${usertype}', '${fullname}', booksOrReturns, type);
				//loadSummaryForExcelDwnld('${id}', '${client.id}', '${varRetType}',"0",fromtime,totime,'custom');
				//$('#dwnldxls').attr('href','${contextPath}/dwnldReportsFinancialSummaryxls/${id}/${client.id}/${varRetType}?reporttype=invoice_report&year=0&fromdate='+fromtime+'&todate='+totime);
			}	
		}
	</script>
</body>
</html>