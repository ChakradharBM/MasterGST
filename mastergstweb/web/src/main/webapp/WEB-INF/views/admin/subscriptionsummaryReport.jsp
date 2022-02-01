<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST | Subscription Summary Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css"	media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<style>
/* #subscriptiontable1_filter input{float:right;} */
.datepicker-orient-left{top:156px!important;}
.subfoot{color:black!important;}
#file_option::after{display:none;}
#filling_option1::after{display:none;}
#subscriptsummary_monthly tr td{text-align:center;}
#subscriptiontable1 tfoot th{text-align:center;} 
#subscriptiontable1 thead tr th{text-align:center;}
#aspusers{width:210px;}
</style>
<script type="text/javascript">
	$('input.btaginput').tagsinput({tagClass : 'big'});
	$(document).on('click', '.btn-remove-tag', function() {$('.bootstrap-tagsinput').html('');});
	$('.multiselect-container>li>a>label').on("click", function(e) {e.preventDefault();	var t = $(this).text();
		$('.bootstrap-tagsinput').append('<span class="tag label label-info">' + t + '<span data-role="remove"></span></span>');
	});
	function getval(sel) {
		document.getElementById('filing_option1').innerHTML = sel;
		if (sel == 'Yearly') {
			$('.monthely-sub').css("display", "none");$('.yearly-sub').css("display", "inline-block");
		} else {
			$('.monthely-sub').css("display", "inline-block");$('.yearly-sub').css("display", "none");
		}
	}
	function getdiv(){
		var type =$('#filing_option1').text();
		if(type == 'Monthly'){
			var monthdate=$("#monthdate").val();
			ajaxFunction(type,monthdate);
		}else{
			var year=$('#yearly_option1').text();
			ajaxFunction("year",year);
		}	
	}
	function updateYearlyOption(value){
		document.getElementById('yearly_option1').innerHTML=value;
	}
</script>
</head>
<body class="body-cls">
	<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
	<div class="bodywrap" style="min-height: 480px; padding-top: 1px">
		<!--- company info bodybreadcrumb start -->
		<div class="bodybreadcrumb">
			<div class="container">
				<div class="row">
					<div class="col-sm-12">
						<div class="bdcrumb-tabs">
							<ul class="nav nav-tabs" role="tablist" style="border:0;margin: 5px 0;">
								<li class="nav-item">Reports</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="db-admin-wrap" style="margin-top:43px;padding-top: 33px;">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Yearly Subscription Report</h4><p></p>
			<div class="dropdown choosedrop mr-0" style="float:right;z-index:1;"><span class="dropdown-toggle" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
					<div class="type_ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span class="input-group-addon add-on pull-right" style="vertical-align: top;display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						<span id="filing_option1" class="filing_option1">Yearly</span>
					</div>
				</Span>
				<div class="dropdown-menu ret_type"	style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
					<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
				</div>
				<span class="datetimetxt monthely-sub" style="display: none"	id="monthely-sub">
				<label id="ret-period">Report Period:</label>
					<div class="datetimetxt datetime-wrap pull-right">
						<div class="input-group date dpMonths" id="dpMonths" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months"	style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" id="monthdate" class="form-control monthly" value="02-2012"	readonly=""> 
							<span class="input-group-addon add-on pull-right"><i	class="fa fa-sort-desc" id="date-drop"></i></span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					</div>
				</span> 
				<span style="display: inline-block" class="yearly-sub">
					<span class="dropdown-toggle" data-toggle="dropdown"	id="filling_option1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Report Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearly_option1" class="yearlyoption">2021</span><span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -8px; left: 9px;">
								<i class="fa fa-sort-desc" id="date-drop" style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div></Span>
						<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021')">2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020')">2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019')">2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018')">2018</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017')">2017</a>
						</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
				</span>
			</div>
			<div class="customtable db-ca-view salestable" style="margin-top: 50px;">
		<table id='subscriptiontable1' class="row-border dataTable meterialform" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th></th><th></th><th>Sign UP</th><th>Not Required</th><th>Demo Completed</th><th>Ready to Go</th><th>Paid</th><th>Revenue</th>
				</tr>
			</thead>
			<tbody id='subscriptsummary_monthly'>
				<tr style="background-color: beige;">
					<td style="text-align:left;">API</td><td></td><td id="aspusers">0</td><td id="asp_notreqiured">0</td><td>0</td><td id="asp_readytogo">0</td><td id="asp_paid">0</td><td class="ind_formats" id="asprevenue">0</td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">GST API Sandbox</td><td id="gstsandboxusers"></td><td id="gstsanbox_notreqiured"></td><td></td><td id="gstsandbox_readytogo"></td><td id="gstsandbox_paid"></td><td id="gstapisandbox_revenue"></td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">GST API Production</td><td id="gstapiusers"></td><td id="gstapi_notreqiured"></td><td></td><td id="gstapi_readytogo"></td><td id="gstapi_paid"></td><td id="gstapi_revenue"></td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">Eway Bill Sandbox</td><td id="ewaybillsandboxusers"></td><td id="ewaybillsandbox_notreqiured"></td><td id=""></td><td id="ewaybillsandbox_readytogo"></td><td id="ewaybillsandbox_paid"></td><td id="ewaybillsandbox_revenue"></td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">Eway Bill Production</td><td id="ewaybillapiusers"></td><td  id="ewaybill_notreqiured"></td><td id=""></td><td id="ewaybillapi_readytogo"></td><td id="ewaybillapi_paid"></td><td id="ewaybillapi_revenue"></td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">Einvoice Sandbox</td><td id="einvoicesandboxusers"></td><td id="einvoicesanbox_notreqiured"></td><td></td><td id="einvoicesandbox_readytogo"></td><td id="einvoicesandbox_paid"></td><td id="einvoicesandbox_revenue"></td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">Einvoice Production</td><td id="einvoiceapiusers"></td><td id="einvoiceapi_notreqiured"></td><td></td><td id="einvoiceapi_readytogo"></td><td id="einvoiceapi_paid"></td><td id="einvoiceapi_revenue"></td>
				</tr>
				<tr style="background-color: #efefaf;">
					<td style="text-align:left;">Web App</td><td></td><td id="webappusers">0</td><td id="webapp_notreqiured">0</td><td>0</td><td id="webapp_readytogo">0</td><td id="webapp_paid">0</td><td class="ind_formats" id="webapprevenu">0</td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">CA</td><td id="causers">0</td><td id="ca_notreqiured">0</td><td>0</td><td id="ca_readytogo">0</td><td id="ca_paid">0</td><td class="ind_formats" id="carevenue">0</td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">Small Medium Business</td><td id="businessusers">0</td><td id="business_notreqiured">0</td><td>0</td><td id="business_readytogo">0</td><td id="business_paid">0</td><td class="ind_formats" id="businessrevenue">0</td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">Enterprises</td><td id="enterpriseusers">0</td><td id="enterprise_notreqiured">0</td><td>0</td><td id="enterprise_readytogo">0</td><td id="enterprise_paid">0</td><td class="ind_formats" id="enterpriserevenue">0</td>
				</tr>
				<tr style="background-color: beige;">
					<td style="text-align:left;">Suvidha Kendra</td><td></td><td id="suvidhakendrausers">0</td><td id="suvidhakendra_notreqiured">0</td><td>0</td><td id="suvidhakendra_readytogo">0</td><td id="suvidhakendra_paid">0</td><td class="ind_formats" id="suvidhakendrarevenue">0</td>
				</tr>
				<tr>
					<td></td><td style="text-align:left;">Suvidha Kendras</td><td id="suvidhacenterusers">0</td><td id="suvidhacenters_notreqiured">0</td><td>0</td><td id="suvidhacenters_readytogo">0</td><td id="suvidhacenters_paid">0</td><td class="ind_formats" id="suvidhacentersrevenue">0</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><th colspan="2" class="subfoot" style="text-align:center">Total Users</th>
				<th class="subfoot" id="signuptotal">0</th><th class="subfoot"  id="notreqiuredtotal">0</th><th class="subfoot" id="">0</th><th class="subfoot" id="readytogototal">0</th><th class="subfoot" id="paidtotal">0</th><th class="subfoot ind_formats" id="totalrevenue">0</th></tr>
			</tfoot>
		</table>
	</div>
		</div>
	</div>

</div>
<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<script type="text/javascript">
	$(function() {
		$('#reports_lnk').addClass('active');
		var clientname = '<c:out value="${client.businessname}"/>';
		var date = new Date();
		var	month = date.getMonth()+1;
		var	year = date.getFullYear();
		var dateValue = (month.length<2 ? '0' : '') + month + '-' + year;
		var date = $('.dpMonths').datepicker({
			autoclose: true,
			viewMode: 1,
			minViewMode: 1,
			format: 'mm-yyyy'
		}).on('changeDate', function(ev) {
			month = ev.date.getMonth()+1;
			year = ev.date.getFullYear();
		});
		$('.dpMonths').datepicker('update', dateValue);	
	});
	var table = $('table#subscriptiontable1,table#subscriptiontable2').DataTable({
     	dom: '<"toolbar"f>lrtip<"clear">', 	
			"pageLength": 25,
			"paging": true,
			"searching": true,
			"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
			"responsive": true,
			"ordering": false,
			"language": {
				"search": "_INPUT_",
				"searchPlaceholder": "Search...",
				"paginate": {
				   "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
			   }
		   }
      });
	$("div.toolbar").append('<a href="#" id="exceldwnldlink" class="btn btn-blue">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
</script>
<script type="text/javascript">
	
	$(document).ready(function(){
		var currentyear=new Date().getFullYear();
		ajaxFunction("year",currentyear);
		$(".ind_formats").each(function(){
		    $(this).html($(this).html().replace(/,/g , ''));
		});
		OSREC.CurrencyFormatter.formatAll({selector: '.ind_formats'});
	});
	
	function ajaxFunction(type,currentyear){
		var urlform= "${contextPath}/summaryreportdata/"+type+"/"+currentyear;
		var urlexcel= "${contextPath}/summaryexcelreport/"+type+"/"+currentyear;
		$('#exceldwnldlink').attr("href",urlexcel);
		$.ajax({
			url: urlform,
			async: true,
			cache: false,
			dataType:"json",
			//contentType: 'application/json',
			//beforeSend: function () {$('#monthProcess').text('Processing...');},
			success : function(subscriptiondata) {
				$('#signuptotal').html(subscriptiondata.signuptotal);
				$('#notreqiuredtotal').html(subscriptiondata.notreqiuredtotal);
				$('#readytogototal').html(subscriptiondata.readytogototal);
				$('#paidtotal').html(subscriptiondata.paidtotal);
				
				$('#aspusers').html(subscriptiondata.aspusers+" (asp users subscriptions "+subscriptiondata.aspsubscriptions+")");
				$('#asp_notreqiured').html(subscriptiondata.asp_notreqiured);
				$('#asp_readytogo').html(subscriptiondata.asp_readytogo);
				$('#asp_paid').html(subscriptiondata.asp_paid);
				
				$('#webappusers').html(subscriptiondata.webappusers);
				$('#webapp_notreqiured').html(subscriptiondata.webapp_notreqiured);
				$('#webapp_readytogo').html(subscriptiondata.webapp_readytogo);
				$('#webapp_paid').html(subscriptiondata.webapp_paid);
				
				$('#causers').html(subscriptiondata.causers);
				$('#ca_notreqiured').html(subscriptiondata.ca_notreqiured);
				$('#ca_readytogo').html(subscriptiondata.ca_readytogo);
				$('#ca_paid').html(subscriptiondata.ca_paid);
				
				$('#enterpriseusers').html(subscriptiondata.enterpriseusers);
				$('#enterprise_notreqiured').html(subscriptiondata.enterprise_notreqiured);
				$('#enterprise_readytogo').html(subscriptiondata.enterprise_readytogo);
				$('#enterprise_paid').html(subscriptiondata.enterprise_paid);
				
				$('#businessusers').html(subscriptiondata.businessusers);
				$('#business_notreqiured').html(subscriptiondata.business_notreqiured);
				$('#business_readytogo').html(subscriptiondata.business_readytogo);
				$('#business_paid').html(subscriptiondata.business_paid);
				
				$('#suvidhakendrausers').html(subscriptiondata.suvidhakendrausers);
				$('#suvidhakendra_notreqiured').html(subscriptiondata.suvidhakendra_notreqiured);
				$('#suvidhakendra_readytogo').html(subscriptiondata.suvidhakendra_readytogo);
				$('#suvidhakendra_paid').html(subscriptiondata.suvidhakendra_paid);
				
				$('#suvidhacenterusers').html(subscriptiondata.suvidhakendrausers);
				$('#suvidhacenters_notreqiured').html(subscriptiondata.suvidhakendra_notreqiured);
				$('#suvidhacenters_readytogo').html(subscriptiondata.suvidhakendra_readytogo);
				$('#suvidhacenters_paid').html(subscriptiondata.suvidhakendra_paid);
				$('#suvidhacenterusers').html(subscriptiondata.suvidhakendrausers);
				$('#suvidhacenters_notreqiured').html(subscriptiondata.suvidhakendra_notreqiured);
				$('#suvidhacenters_readytogo').html(subscriptiondata.suvidhakendra_readytogo);
				$('#suvidhacenters_paid').html(subscriptiondata.suvidhakendra_paid);
				
				$('#gstsandboxusers').html(subscriptiondata.gstsandboxusers);
				$('#gstapiusers').html(subscriptiondata.gstapiusers);
				$('#ewaybillsandboxusers').html(subscriptiondata.ewaybillsandboxusers);
				$('#ewaybillapiusers').html(subscriptiondata.ewaybillapiusers);
				$('#einvoicesandboxusers').html(subscriptiondata.einvoicesandboxusers);
				$('#einvoiceapiusers').html(subscriptiondata.einvoiceapiusers);
				
				$('#gstsandbox_paid').html(subscriptiondata.gstsandbox_paid);
				$('#gstapi_paid').html(subscriptiondata.gstapi_paid);
				$('#ewaybillsandbox_paid').html(subscriptiondata.ewaybillsandbox_paid);
				$('#ewaybillapi_paid').html(subscriptiondata.ewaybillapi_paid);
				$('#einvoicesandbox_paid').html(subscriptiondata.einvoicesandbox_paid);
				$('#einvoiceapi_paid').html(subscriptiondata.einvoiceapi_paid);
				
				$('#totalrevenue').html(formatNumber(subscriptiondata.totalrevenue.toFixed(2)));
				$('#carevenue').html(formatNumber(subscriptiondata.carevenue.toFixed(2)));
				$('#businessrevenue').html(formatNumber(subscriptiondata.businessrevenue.toFixed(2)));
				$('#enterpriserevenue').html(formatNumber(subscriptiondata.enterpriserevenue.toFixed(2)));
				$('#suvidhakendrarevenue').html(formatNumber(subscriptiondata.suvidhakendrarevenue.toFixed(2)));
				$('#suvidhacentersrevenue').html(formatNumber(subscriptiondata.suvidhakendrarevenue.toFixed(2)));
				$('#asprevenue').html(formatNumber(subscriptiondata.asprevenue.toFixed(2)));
				
				$('#gstapisandbox_revenue').html(formatNumber(subscriptiondata.gstapisandbox_revenue.toFixed(2)));
				$('#ewaybillsandbox_revenue').html(formatNumber(subscriptiondata.ewaybillsandbox_revenue.toFixed(2)));
				$('#einvoicesandbox_revenue').html(formatNumber(subscriptiondata.einvoicesandbox_revenue.toFixed(2)));
				
				$('#gstapi_revenue').html(formatNumber(subscriptiondata.gstapi_revenue.toFixed(2)));
				$('#ewaybillapi_revenue').html(formatNumber(subscriptiondata.ewaybillapi_revenue.toFixed(2)));
				$('#einvoiceapi_revenue').html(formatNumber(subscriptiondata.einvoiceapi_revenue.toFixed(2)));
				
				$('#gstsandbox_readytogo').html(subscriptiondata.gstsandbox_readytogo);
				$('#gstapi_readytogo').html(subscriptiondata.gstapi_readytogo);
				$('#ewaybillsandbox_readytogo').html(subscriptiondata.ewaybillsandbox_readytogo);
				$('#ewaybillapi_readytogo').html(subscriptiondata.ewaybillapi_readytogo);

				$('#einvoicesandbox_readytogo').html(subscriptiondata.einvoicesandbox_readytogo);
				$('#einvoiceapi_readytogo').html(subscriptiondata.einvoiceapi_readytogo);
				
				$('#webapprevenu').html(formatNumber(subscriptiondata.webapprevenu.toFixed(2)));
			},error:function(errordata) {
				console.log("errordata ::"+errordata);
			}
			
		});
		$('#exceldwnldlink').attr("href",urlexcel);
	}
</script>
<jsp:include page="/WEB-INF/views/reports/invoicedetails.jsp" />
</body>
</html>
