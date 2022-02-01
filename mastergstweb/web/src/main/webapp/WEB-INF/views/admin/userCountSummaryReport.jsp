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
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/sales_reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<style>
#subscriptiontable thead tr th {font-size: 12px;font-weight: 500;}
#subscriptiontable tbody tr td {font-size: 14px;}
#subscriptiontable tfoot tr th {color: black;}
.datepicker-orient-left {top: 156px !important;}
.subfoot {color: black !important;}
#file_option::after {display: none;}
#filling_option1::after {display: none;}
#subscriptsummary_monthly tr td {text-align: center;}
#subscriptiontable tfoot tr th {text-align: center;}
#subscriptiontable1 thead tr th {text-align: center;}
#aspusers {width: 210px;}
</style>
<script type="text/javascript">
	$('input.btaginput').tagsinput({
		tagClass : 'big'
	});
	$(document).on('click', '.btn-remove-tag', function() {
		$('.bootstrap-tagsinput').html('');
	});
	$('.multiselect-container>li>a>label').on(
			"click",
			function(e) {
				e.preventDefault();
				var t = $(this).text();
				$('.bootstrap-tagsinput').append(
						'<span class="tag label label-info">' + t
								+ '<span data-role="remove"></span></span>');
			});
	function getval(sel) {
		document.getElementById('filing_option1').innerHTML = sel;
		if (sel == 'Yearly') {
			$('.monthely-sub').css("display", "none");
			$('.yearly-sub').css("display", "inline-block");
		} else {
			$('.monthely-sub').css("display", "inline-block");
			$('.yearly-sub').css("display", "none");
		}
	}
	function getdiv() {
		var type = $('#filing_option1').text();
		if (type == 'Monthly') {
			var monthdate = $("#monthdate").val();
			ajaxFunction(type, monthdate);
		} else {
			var year = $('#yearly_option1').text();
			ajaxFunction("year", year);
		}
	}
	function updateYearlyOption(value) {
		document.getElementById('yearly_option1').innerHTML = value;
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
							<ul class="nav nav-tabs" role="tablist"
								style="border: 0; margin: 5px 0;">
								<li class="nav-item">Reports</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="db-admin-wrap"
			style="margin-top: 43px; padding-top: 33px;">
			<div class="container">
				<div class=" "></div>
				<a href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>"
					class="btn btn-blue-dark pull-right" role="button"
					style="padding: 4px 25px;">Back</a>
				<h4>Yearly Subscription Report</h4>
				<p></p>
				<div class="dropdown choosedrop mr-0"
					style="float: right; z-index: 1;">
					<span class="dropdown-toggle" data-toggle="dropdown"
						id="fillingoption"
						style="margin-right: 10px; display: inline-flex;"><label>Report
							Type:</label>
						<div class="type_ret"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span class="input-group-addon add-on pull-right"
								style="vertical-align: top; display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i
								class="fa fa-sort-desc" style="vertical-align: super;"></i> </span> <span
								id="filing_option1" class="filing_option1">Yearly</span>
						</div> </Span>
					<div class="dropdown-menu ret_type"
						style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="Monthly"
							onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"
							href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					</div>
					<span class="datetimetxt monthely-sub" style="display: none"
						id="monthely-sub"> <label id="ret-period">Report
							Period:</label>
						<div class="datetimetxt datetime-wrap pull-right">
							<div class="input-group date dpMonths" id="dpMonths"
								data-date="102/2012" data-date-format="mm-yyyy"
								data-date-viewmode="years" data-date-minviewmode="months"
								style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
								<input type="text" id="monthdate" class="form-control monthly"
									value="02-2012" readonly=""> <span
									class="input-group-addon add-on pull-right"><i
									class="fa fa-sort-desc" id="date-drop"></i></span>
							</div>
							<a href="#" class="btn btn-greendark  pull-right" role="button"
								style="padding: 4px 10px;; text-transform: uppercase;"
								onClick="getdiv()">Generate</a>
						</div>
					</span> <span style="display: inline-block" class="yearly-sub"> <span
						class="dropdown-toggle" data-toggle="dropdown"
						id="filling_option1"
						style="margin-right: 10px; display: inline-flex;"> <label
							id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Report
								Period:</label>
							<div class="typ-ret"
								style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
								<span style="vertical-align: top; margin-left: 3px;"
									id="yearly_option1" class="yearlyoption">2021</span><span
									class="input-group-addon add-on pull-right"
									style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -8px; left: 9px;">
									<i class="fa fa-sort-desc" id="date-drop"
									style="vertical-align: super; margin-left: 6px;"></i>
								</span>
							</div></Span>
						<div class="dropdown-menu ret-type1" id="financialYear1"
							style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021')">2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020')">2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019')">2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018')">2018</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017')">2017</a>
						</div> <a href="#" class="btn btn-greendark  pull-right" role="button" style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
					</span>
				</div>
				<div class="customtable db-ca-view salestable"
					style="margin-top: 50px;">
					<table id='subscriptiontable'
						class="row-border dataTable meterialform" cellspacing="0"
						width="100%">
						<thead>
							<tr>
								<th colspan="2" class="text-center">Sales Status</th>
								<th>Call Not Lift</th>
								<th>Duplicate</th>
								<th>Not Required</th>
								<th>Ready to Go</th>
								<th>Ready to pay</th>
								<th>Yet to Take Decision</th>
								<th>Pricing Issue</th>
								<th>Sandbox Testing</th>
								<th>Closed</th>
								<th>Status Empty</th>
								<th>Total</th>
							</tr>
						</thead>
						<tbody id='subscriptsummary_monthly'>
							<tr style="background-color: beige;">
								<td colspan="2" class="text-left">API</td>
								<td class='aspdeveloper_callnotlift data'>0</td>
								<td class='aspdeveloper_duplicates data'>0</td>
								<td class='aspdeveloper_notrequired data'>0</td>
								<td class='aspdeveloper_readytogo data'>0</td>
								<td class='aspdeveloper_readytopay data'>0</td>
								<td class='aspdeveloper_yettotakedecision data'>0</td>
								<td class='aspdeveloper_pricingissue data'>0</td>
								<td class='aspdeveloper_sandboxtesting data'>0</td>
								<td class='aspdeveloper_closed data'>0</td>
								<td class='aspdeveloper_statusempty data'>0</td>
								<td class='aspdeveloper_rowTotal data'>0</td>
							</tr>
							<tr>
								<td></td>
								<td class="text-left">API Users</td>
								<td class='aspdeveloper_callnotlift data'>0</td>
								<td class='aspdeveloper_duplicates data'>0</td>
								<td class='aspdeveloper_notrequired data'>0</td>
								<td class='aspdeveloper_readytogo data'>0</td>
								<td class='aspdeveloper_readytopay data'>0</td>
								<td class='aspdeveloper_yettotakedecision data'>0</td>
								<td class='aspdeveloper_pricingissue data'>0</td>
								<td class='aspdeveloper_sandboxtesting data'>0</td>
								<td class='aspdeveloper_closed data'>0</td>
								<td class='aspdeveloper_statusempty data'>0</td>
								<td class='aspdeveloper_rowTotal data'>0</td>
							</tr>
							<tr style="background-color: #efefaf;">
								<td colspan="2" class="text-left">Web</td>
								<td class='web_callnotlift data'>0</td>
								<td class='web_duplicates data'>0</td>
								<td class='web_notrequired data'>0</td>
								<td class='web_readytogo data'>0</td>
								<td class='web_readytopay data'>0</td>
								<td class='web_yettotakedecision data'>0</td>
								<td class='web_pricingissue data'>0</td>
								<td class='web_sandboxtesting data'>0</td>
								<td class='web_closed data'>0</td>
								<td class='web_statusempty data'>0</td>
								<td class='web_rowTotal data'>0</td>
							</tr>
							<tr>
								<td></td>
								<td class="text-left">CA</td>
								<td class='cacmas_callnotlift data'>0</td>
								<td class='cacmas_duplicates data'>0</td>
								<td class='cacmas_notrequired data'>0</td>
								<td class='cacmas_readytogo data'>0</td>
								<td class='cacmas_readytopay data'>0</td>
								<td class='cacmas_yettotakedecision data'>0</td>
								<td class='cacmas_pricingissue data'>0</td>
								<td class='cacmas_sandboxtesting data'>0</td>
								<td class='cacmas_closed data'>0</td>
								<td class='cacmas_statusempty data'>0</td>
								<td class='cacmas_rowTotal data'>0</td>
							</tr>
							<tr>
								<td></td>
								<td class="text-left">Enterprises</td>
								<td class='enterprise_callnotlift data'>0</td>
								<td class='enterprise_duplicates data'>0</td>
								<td class='enterprise_notrequired data'>0</td>
								<td class='enterprise_readytogo data'>0</td>
								<td class='enterprise_readytopay data'>0</td>
								<td class='enterprise_yettotakedecision data'>0</td>
								<td class='enterprise_pricingissue data'>0</td>
								<td class='enterprise_sandboxtesting data'>0</td>
								<td class='enterprise_closed data'>0</td>
								<td class='enterprise_statusempty data'>0</td>
								<td class='enterprise_rowTotal data'>0</td>
							</tr>
							<tr>
								<td></td>
								<td class="text-left">Small Medium Business</td>
								<td class='business_callnotlift data'>0</td>
								<td class='business_duplicates data'>0</td>
								<td class='business_notrequired data'>0</td>
								<td class='business_readytogo data'>0</td>
								<td class='business_readytopay data'>0</td>
								<td class='business_yettotakedecision data'>0</td>
								<td class='business_pricingissue data'>0</td>
								<td class='business_sandboxtesting data'>0</td>
								<td class='business_closed data'>0</td>
								<td class='business_statusempty data'>0</td>
								<td class='business_rowTotal data'>0</td>
							</tr>
							<tr>
								<td></td>
								<td class="text-left">Suvidha Kendra</td>
								<td class='suvidha_callnotlift data'>0</td>
								<td class='suvidha_duplicates data'>0</td>
								<td class='suvidha_notrequired data'>0</td>
								<td class='suvidha_readytogo data'>0</td>
								<td class='suvidha_readytopay data'>0</td>
								<td class='suvidha_yettotakedecision data'>0</td>
								<td class='suvidha_pricingissue data'>0</td>
								<td class='suvidha_sandboxtesting data'>0</td>
								<td class='suvidha_closed data'>0</td>
								<td class='suvidha_statusempty data'>0</td>
								<td class='suvidha_rowTotal data'>0</td>
							</tr>
						</tbody>
						<tfoot>
							<tr>
								<th colspan="2">Sub Totals</th>
								<th class='subTotal_callnotlift data'>0</th>
								<th class='subTotal_duplicates data'>0</th>
								<th class='subTotal_notrequired data'>0</th>
								<th class='subTotal_readytogo data'>0</th>
								<th class='subTotal_readytopay data'>0</th>
								<th class='subTotal_yettotakedecision data'>0</th>
								<th class='subTotal_pricingissue data'>0</th>
								<th class='subTotal_sandboxtesting data'>0</th>
								<th class='subTotal_closed data'>0</th>
								<th class='subTotal_statusempty data'>0</th>
								<th class='subTotal_rowTotal data'>0</th>
							</tr>
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
			var month = date.getMonth() + 1;
			var year = date.getFullYear();
			var dateValue = (month.length < 2 ? '0' : '') + month + '-' + year;
			var date = $('.dpMonths').datepicker({
				autoclose : true,
				viewMode : 1,
				minViewMode : 1,
				format : 'mm-yyyy'
			}).on('changeDate', function(ev) {
				month = ev.date.getMonth() + 1;
				year = ev.date.getFullYear();
			});
			$('.dpMonths').datepicker('update', dateValue);
		});
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			var currentyear = new Date().getFullYear();
			ajaxFunction("year", currentyear);
			$(".ind_formats").each(function(){
			    $(this).html($(this).html().replace(/,/g , ''));
			});
			OSREC.CurrencyFormatter.formatAll({
				selector : '.ind_formats'
			});
		});
		function ajaxFunction(type, currentyear) {
			var urlform = "${contextPath}/summaryusercountdata/" + type + "/"+ currentyear;
			$('.data').html(0);
			$.ajax({
				url : urlform,
				async : true,
				cache : false,
				success : function(subscriptiondata) {
					var status=['callnotlift', 'duplicates', 'notrequired', 'readytogo', 'readytopay', 'yettotakedecision', 'pricingissue', 'sandboxtesting', 'closed', 'statusempty','rowTotal'];
					var callnotlift = 0, duplicates = 0, notrequired = 0, readytogo = 0, readytopay = 0, yettotakedecision = 0, testaccount = 0, pricingissue = 0, sandboxtesting = 0, closed = 0, statusempty = 0;
					var rowTotal=0;
					$.each(subscriptiondata, function(key, usersLst) {
						if (key == 'aspdeveloper') {
							userstype(key, usersLst);
						} else if (key == 'enterprise') {
							userstype(key, usersLst);
						} else if (key == 'cacmas') {
							userstype(key, usersLst);
						} else if (key == 'suvidha') {
							userstype(key, usersLst);
						} else if (key == 'business') {
							userstype(key, usersLst);
						}						
					});
					for(var i=0;i<status.length;i++){
						var counts=parseInt($('.cacmas_'+status[i]).html())+parseInt($('.enterprise_'+status[i]).html())+parseInt($('.suvidha_'+status[i]).html())+parseInt($('.business_'+status[i]).html());
						$('.web_'+status[i]).html(counts);
					}
					for(var i=0;i<status.length;i++){
						var counts=parseInt($('.web_'+status[i]).html())+parseInt($('.aspdeveloper_'+status[i]).html());
						$('.subTotal_'+status[i]).html(counts);
					}
				},
				error : function(errordata) {
					console.log("errordata ->" + errordata);
				}
			});
		}
		function userstype(type, usersLst) {
			var callnotlift = 0, duplicates = 0, notrequired = 0, readytogo = 0, readytopay = 0, yettotakedecision = 0, testaccount = 0, pricingissue = 0, sandboxtesting = 0, closed = 0, statusempty = 0;
			var rowTotal=0;
			$.each(usersLst, function(usrKey, user) {
				var needfollowup = user.needToFollowUp;

				if (needfollowup == 'Call Not Lift') {
					callnotlift += 1;
					rowTotal+=1;
				} else if (needfollowup == 'Duplicate') {
					duplicates += 1;
					rowTotal+=1;
				} else if (needfollowup == 'Not Required') {
					notrequired += 1;
					rowTotal+=1;
				} else if (needfollowup == 'Ready to Go') {
					readytogo += 1;
					rowTotal+=1;
				} else if (needfollowup == 'Ready to Pay') {
					readytopay += 1;
					rowTotal+=1;
				} else if (needfollowup == 'Yet to Take Decision') {
					yettotakedecision += 1;
					rowTotal+=1;
				} else if (needfollowup == 'Pricing Issue') {
					pricingissue += 1;
					rowTotal+=1;
				} else if (needfollowup == 'Sandbox Testing') {
					sandboxtesting += 1;
					rowTotal+=1;
				} else if (needfollowup == 'Closed') {
					closed += 1;
					rowTotal+=1;
				} else {
					statusempty += 1;
					rowTotal+=1;
				}
			});

			$('.' + type + '_callnotlift').html(callnotlift);
			$('.' + type + '_duplicates').html(duplicates);
			$('.' + type + '_notrequired').html(notrequired);
			$('.' + type + '_readytogo').html(readytogo);
			$('.' + type + '_readytopay').html(readytopay);
			$('.' + type + '_yettotakedecision').html(yettotakedecision);
			$('.' + type + '_pricingissue').html(pricingissue);
			$('.' + type + '_sandboxtesting').html(sandboxtesting);
			$('.' + type + '_closed').html(closed);
			$('.' + type + '_statusempty').html(statusempty);
			$('.'+type + '_rowTotal').html(rowTotal);
		}
	</script>
	<jsp:include page="/WEB-INF/views/reports/invoicedetails.jsp" />
</body>
</html>
