<!-- 
	sales_report page creation on 04/12/2018
	sales_report page creation by mahindra
-->

<%@include file="/WEB-INF/views/includes/taglib.jsp"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no" />
<meta name="description" content="" />
<meta name="author" content="" />
<link rel="icon" href="static/images/master/favicon.ico" />
<title>MasterGST</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp"%>
<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css"
	media="all" />
<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css"
	media="all" />
<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/common/datetimepicker.css"
	media="all" />
<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/reports/reports.css"
	media="all" />
<script
	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js"
	type="text/javascript"></script>
<script
	src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js"
	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js"
	type="text/javascript"></script>
<style>
#ret-period {
	margin-bottom: 0;
	padding: 0px;
	vertical-align: baseline;
	font-size: 1rem;
	text-transform: capitalize;
	margin-right: 10px;
}

span.finance-year {
	display: none
}

.datetimetxt .fa {
	top: 8px !important;
	right: 4px;
}

.datetimetxt {
	margin-top: 0px !important
}

#fillingoption::after, #fillingoption1::after {
	content: none;
	display: none
}

select {
	border: none;
	background-color: unset
}

.datetimetxt.datetime-wrap {
	display: inline-flex;
	float: unset;
}
</style>
<script type="text/javascript">
	/*----- tag input ------*/
	$('input.btaginput').tagsinput({
		tagClass : 'big',
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

	/*--- bootstrap multiselect --*/
	$(function() {
		$('.multiselect-ui').multiselect({
			includeSelectAllOption : true
		});
	});

	function getval(sel) {
		document.getElementById('filing_option').innerHTML = sel;
		document.getElementById('filing_option1').innerHTML = sel;
		document.getElementById('filing_option2').innerHTML = sel;
		if (sel == 'Custom') {
			$('.monthely-sp').css("display", "none");
			$('.yearly-sp').css("display", "none");
			$('.custom-sp').css("display", "inline-block");
			$('.dropdown-menu.ret-type').css("left", "13%");

		} else if (sel == 'Yearly') {
			$('.monthely-sp').css("display", "none");
			$('.yearly-sp').css("display", "inline-block");
			$('.custom-sp').css("display", "none");
			$('.dropdown-menu.ret-type').css("left", "19%");

		} else {
			$('.monthely-sp').css("display", "inline-block");
			$('.yearly-sp').css("display", "none");
			$('.custom-sp').css("display", "none");
			$('.dropdown-menu.ret-type').css("left", "19%");

		}
	};
	function getdiv() {
		var abc = $('#fillingoption span').html();
		if (abc == 'Custom') {
			$('.monthely1').css("display", "none");
			$('.yearly1').css("display", "none");
			$('.custom1').css("display", "block");
			$('span#fillingoption').css("vertical-align", "bottom");
		} else if (abc == 'Yearly') {
			$('.monthely1').css("display", "none");
			$('.yearly1').css("display", "block");
			$('.custom1').css("display", "none");
			$('span#fillingoption').css("vertical-align", "bottom");
		} else {
			$('.monthely1').css("display", "block");
			$('.yearly1').css("display", "none");
			$('.custom1').css("display", "none");
			$('span#fillingoption').css("vertical-align", "middle");
		}
	}
	function downloadPDF() {
		var element = document.getElementById('print-wrap');

		const
		options = {
			margin : [ 0.25, 0.15, 0.25, -0.15 ],
			fontSize : 9,
			image : {
				type : 'jpeg',
				quality : 1
			},
			html2canvas : {
				dpi : 192,
				width : 850,
				letterRendering : true
			},
			jsPDF : {
				unit : 'in',
				format : 'a4',
				orientation : 'portrait',
				autotable : true
			}
		}

		html2pdf(element, options);
	}
</script>
</head>
<body>
	<!-- header page begin -->
	<%@include file="/WEB-INF/views/includes/client_header.jsp"%>
	<!-- header page end -->

	<!--- breadcrumb start -->
	<div class="container bread">
		<ol class="breadcrumb">
			<li class="breadcrumb-item"><a href="dashboard-ca-db.html">Dashboard
			</a></li>
			<li class="breadcrumb-item"><a href="reports.html">Reports</a></li>
			<li class="breadcrumb-item active">Sales Report</li>
		</ol>
	</div>
	<!--- breadcrumb end -->
	<!-- Purchase Report start -->
	<div class="db-ca-wrap monthely1">
		<div class="container">
			<!-- <div class="row"> -->
			<!-- dashboard cp table begin -->
			<!-- <h3>Reports Coming Soon..</h3> -->
			<!-- dashboard cp table end -->
			<!-- </div> -->
			<div class="clearfix"></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right"
				role="button" style="padding: 4px 25px;">Back</a>
			<h2>Monthly Sales Report</h2>
			<p>Sales Report gives you a summary of your monthly, quarterly
				and annual purchases.</p>
			<div class="dropdown chooseteam mr-0">
				<span class="dropdown-toggle yearly" data-toggle="dropdown"
					id="fillingoption"
					style="margin-right: 10px; display: inline-flex;"> <label>Return
						Type:</label>
					<div class="typ-ret"
						style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option" class="filing_option"
							style="vertical-align: top;">Monthly</span><span
							class="input-group-addon add-on pull-right"
							style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i
							class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
					</div></Span>
				<div class="dropdown-menu ret-type"
					style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
					<a class="dropdown-item" href="#" value="Monthly"
						onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"
						href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a> <a
						class="dropdown-item" href="#" value="Custom"
						onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: block"
					id="monthely-sp"> <span><label id="ret-period">Return
							Period:</label></span>
					<div class="datetimetxt datetime-wrap pull-right">

						<div class="input-group date dpMonths" id="dpMonths"
							data-date="102/2012" data-date-format="mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control" value="02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"
							style="padding: 4px 10px;" onClick="getdiv()">Generate</a>
					</div>
				</span> <span style="display: none" class="yearly-sp"> <span
					class="dropdown-toggle yearly" data-toggle="dropdown"
					id="fillingoption1"
					style="margin-right: 10px; display: inline-flex;"><label
						id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Return
							Period:</label>
						<div class="typ-ret"
							style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;">2017
								- 2018</span><span class="input-group-addon add-on pull-right"
								style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc"
								style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div></Span>
					<div class="dropdown-menu ret-type1" id="financialYear1"
						style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="2017">2017 - 2018</a> <a
							class="dropdown-item" href="#" value="2018">2018 - 2019</a>

					</div> <a href="#" class="btn btn-greendark  pull-right" role="button"
					style="padding: 4px 10px; text-transform: uppercase"
					onClick="getdiv()">Generate</a>
				</span> <span class="datetimetxt custom-sp" style="display: none"
					id="custom-sp"> <label
					style="margin-right: 4px; text-transform: initial;; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
					<div class="datetimetxt datetime-wrap">
						<div class="input-group date dpCustom" id="dpCustom"
							data-date="102/2012" data-date-format="mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control" value="02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
					</div> <label
					style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
					<div class="datetimetxt datetime-wrap">
						<div class="input-group date dpCustom1" id="dpCustom1"
							data-date="102/2012" data-date-format="mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 8px; height: 28px; margin-right: 10px;">
							<input type="text" class="form-control" value="02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"
							style="padding: 4px 10px;" onClick="getdiv()">Generate</a>
					</div>

				</span>
			</div>
			<div class="clearfix"></div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
				<!-- Summary info start -->
				<div class="normaltable meterialform">
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Filter</div>
						<div class="noramltable-row-desc">
							<input type="text" value="BTB_INVOICE" data-role="tagsinput"
								placeholder="Add tags" class="btaginput" /> <span
								class="btn-remove-tag">Clear All<span data-role="remove"></span></span>
						</div>
					</div>
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
						<div class="noramltable-row-desc">
							<select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Invoice Type</option>
								<option value="tomatoes">Invoice Type1</option>
								<option value="mozarella">Invoice Type2</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Sub Type</option>
								<option value="tomatoes">Sub Type 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Filing Filter</option>
								<option value="tomatoes">Filing Filter 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">GSTIN</option>
								<option value="tomatoes">GSTIN 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Include Cancelled</option>
								<option value="tomatoes">Include Cancelled 1</option>
							</select>
						</div>
					</div>
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Filter Summery</div>
						<div class="noramltable-row-desc">
							<div class="normaltable-col hdr">
								Total Transactions
								<div class="normaltable-col-txt">24560</div>
							</div>
							<div class="normaltable-col hdr">
								Total IGST
								<div class="normaltable-col-txt">67890</div>
							</div>
							<div class="normaltable-col hdr">
								Total SGST
								<div class="normaltable-col-txt">78903</div>
							</div>
							<div class="normaltable-col hdr">
								Total CGST
								<div class="normaltable-col-txt">50938</div>
							</div>
							<div class="normaltable-col hdr">
								Total CESS
								<div class="normaltable-col-txt">20938</div>
							</div>
							<div class="normaltable-col hdr">
								Total Tax Value
								<div class="normaltable-col-txt">209847</div>
							</div>
						</div>
					</div>
				</div>
				<!-- Summary info end -->
			</div>
			<div class="customtable db-ca-view reportTable reportTable2">
				<!-- <div class="addsales dropdown pull-right mb-2"
					style="z-index: 2; position: relative; top: 38px;">
					<div class="split-button-menu-dropdown importdrop">
						<button
							class="btn btn-blue b-split-right b-r-cta b-m-super-subtle pull-right"
							id="idMenuDropdown" data-toggle="dropdown"
							style="border-left: solid 1px #435a93; border-bottom-left-radius: 0px; color: #435a93; border-top-left-radius: 0px; width: 10px; padding: .4rem 1rem;">
							<span class="showarrow"> <i class="fa fa-caret-down"></i></span>
						</button>
						<button class="btn btn-blue b-split-left b-r-cta b-m-super-subtle"
							id="idMenuDropdown" data-toggle="dropdown" aria-haspopup="true"
							style="width: 131px; box-shadow: none; text-align: left; color: #435a93; border-bottom-right-radius: 0px; border-top-right-radius: 0px; padding: .4rem 1rem;"
							aria-expanded="false" href="#">Download As</button>
						<div class="dropdown-menu dropdown-menu-right importmenu"
							aria-labelledby="idMenuDropdown" style="width: 165px !important;">
							<a class="dropdown-item importdropdown" href="#">Excel </a> <a
								class="dropdown-item importdropdown" href="#">Pdf</a>
						</div>
					</div>
				</div>-->
				<!-- <a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm " style="position: absolute; top: 3%;right: 0px; margin-left: 5px;    z-index: 1;" onclick="downloadPDF()">Download PDF</a> -->
				<!-- <a href="javascript:void()" onClick="jQuery('#print-wrap').print();" id="idPermissionUpload_Invoice" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm" style="position: absolute; top: 3%;right: 11%; margin-left: 5px;    z-index: 1;">Download Excel</a> -->
				<table id="reportTable3 print-wrap"
					class="display row-border dataTable meterialform" cellspacing="0"
					width="100%">
					<thead>
						<tr>
							<th class="text-center">Tax Details</th>

							<th class="text-center">July</th>
							<th class="text-center">August</th>
							<th class="text-center">September</th>
							<th class="text-center">October</th>
							<th class="text-center">November</th>
							<th class="text-center">December</th>
							<th class="text-center">January</th>
							<th class="text-center">February</th>
							<th class="text-center">March</th>
							<th class="text-center">April</th>
							<th class="text-center">May</th>
							<th class="text-center">June</th>
						</tr>
					</thead>
					<tbody>
						<tr data-toggle="modal" data-target="#viewModal">
							<td align="center"><h6>Sales</h6></td>
							<td align="center">100,000</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>

						<!-- <tr>
							<td align="center"><h6>Purchases</h6></td>
							<td align="center">200,000</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr> -->

						<tr>
							<td align="center"><h6>Income Tax</h6></td>
							<td align="center">100,000</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap yearly1" style="display: none">
		<div class="container">
			<!-- <div class="row"> -->
			<!-- dashboard cp table begin -->
			<!-- <h3>Reports Coming Soon..</h3> -->
			<!-- dashboard cp table end -->
			<!-- </div> -->
			<div class="clearfix"></div>
					
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right"
				role="button" style="padding: 4px 25px;">Back</a>
			<h2>Yearly Sales Report</h2>
			<p>Sales Report gives you a summary of your monthly, quarterly
				and annual purchases.</p>
			<div class="dropdown chooseteam mr-0" style="height: 32px">
				<span class="dropdown-toggle yearly" data-toggle="dropdown"
					id="fillingoption"
					style="margin-right: 10px; display: inline-flex;"><span>Return
						Type:</span>
					<div class="typ-ret"
						style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option1" class="filing_option"
							style="vertical-align: bottom">Yearly</span><span
							class="input-group-addon add-on pull-right"
							style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i
							class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
					</div></span>
				<div class="dropdown-menu ret-type"
					style="WIDTH: 108px !important; min-width: 36px; left: 16%; top: 26px">
					<a class="dropdown-item" href="#" value="Monthly"
						onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"
						href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a> <a
						class="dropdown-item" href="#" value="Custom"
						onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: none"
					id="monthely-sp"> <label id="ret-period">Return
						Period:</label>
					<div class="datetimetxt datetime-wrap pull-right">

						<div class="input-group date dpMonths" id="dpMonths"
							data-date="102/2012" data-date-format="mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control" value="02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"
							style="padding: 4px 10px;; text-transform: uppercase;"
							onClick="getdiv()">Generate</a>
					</div>
				</span> <span style="display: inline-block" class="yearly-sp"> <span
					class="dropdown-toggle yearly" data-toggle="dropdown"
					id="fillingoption1"
					style="margin-right: 10px; display: inline-flex;"><label
						id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Return
							Period:</label>
						<div class="typ-ret"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;">2017
								- 2018</span><span class="input-group-addon add-on pull-right"
								style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc"
								style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div></Span>
					<div class="dropdown-menu ret-type1" id="financialYear1"
						style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="2017">2017 - 2018</a> <a
							class="dropdown-item" href="#" value="2018">2018 - 2019</a>

					</div> <a href="#" class="btn btn-greendark  pull-right" role="button"
					style="padding: 4px 10px;; text-transform: uppercase;"
					onClick="getdiv()">Generate</a>

				</span> <span class="datetimetxt custom-sp" style="display: none"
					id="custom-sp"> <label
					style="margin-right: 4px; text-transform: initial;; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
					<div class="datetimetxt datetime-wrap">

						<div class="input-group date dpCustom" id="dpCustom"
							data-date="102/2012" data-date-format="mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control" value="02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
					</div> <label
					style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
					<div class="datetimetxt datetime-wrap">

						<div class="input-group date dpCustom1" id="dpCustom1"
							data-date="102/2012" data-date-format="mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control" value="02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"
							style="padding: 4px 10px;; text-transform: uppercase;"
							onClick="getdiv()">Generate</a>
					</div>

				</span>
			</div>
			<div class="clearfix"></div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
				<!-- Summary info start -->
				<div class="normaltable meterialform">
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Filter</div>
						<div class="noramltable-row-desc">
							<input type="text" value="BTB_INVOICE" data-role="tagsinput"
								placeholder="Add tags" class="btaginput" /> <span
								class="btn-remove-tag">Clear All<span data-role="remove"></span></span>
						</div>
					</div>
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
						<div class="noramltable-row-desc">
							<select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Invoice Type</option>
								<option value="tomatoes">Invoice Type1</option>
								<option value="mozarella">Invoice Type2</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Sub Type</option>
								<option value="tomatoes">Sub Type 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Filing Filter</option>
								<option value="tomatoes">Filing Filter 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">GSTIN</option>
								<option value="tomatoes">GSTIN 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Include Cancelled</option>
								<option value="tomatoes">Include Cancelled 1</option>
							</select>
						</div>
					</div>
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Filter Summery</div>
						<div class="noramltable-row-desc">
							<div class="normaltable-col hdr">
								Total Transactions
								<div class="normaltable-col-txt">24560</div>
							</div>
							<div class="normaltable-col hdr">
								Total IGST
								<div class="normaltable-col-txt">67890</div>
							</div>
							<div class="normaltable-col hdr">
								Total SGST
								<div class="normaltable-col-txt">78903</div>
							</div>
							<div class="normaltable-col hdr">
								Total CGST
								<div class="normaltable-col-txt">50938</div>
							</div>
							<div class="normaltable-col hdr">
								Total CESS
								<div class="normaltable-col-txt">20938</div>
							</div>
							<div class="normaltable-col hdr">
								Total Tax Value
								<div class="normaltable-col-txt">209847</div>
							</div>
						</div>
					</div>
				</div>
				<!-- Summary info end -->
			</div>
			<div class="customtable db-ca-view reportTable reportTable4">
				<!-- <a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm " style="position: absolute; top: 3%;right: 0px; margin-left: 5px;    z-index: 1;">Download PDF</a> -->
				<!-- <a href="#" id="idPermissionUpload_Invoice" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm" style="position: absolute; top: 3%;right: 11%; margin-left: 5px;    z-index: 1;">Download Excel</a> -->
				<!--  
				<div class="addsales dropdown pull-right mb-2"
					style="z-index: 2; position: relative; top: 38px;">
					<div class="split-button-menu-dropdown importdrop">
						<button
							class="btn btn-blue b-split-right b-r-cta b-m-super-subtle pull-right"
							id="idMenuDropdown" data-toggle="dropdown"
							style="border-left: solid 1px #435a93; border-bottom-left-radius: 0px; color: #435a93; border-top-left-radius: 0px; width: 10px; padding: .4rem 1rem;">
							<span class="showarrow"> <i class="fa fa-caret-down"></i></span>
						</button>
						<button class="btn btn-blue b-split-left b-r-cta b-m-super-subtle"
							id="idMenuDropdown" data-toggle="dropdown" aria-haspopup="true"
							style="width: 131px; box-shadow: none; text-align: left; color: #435a93; border-bottom-right-radius: 0px; border-top-right-radius: 0px; padding: .4rem 1rem;"
							aria-expanded="false" href="#">Download As</button>
						<div class="dropdown-menu dropdown-menu-right importmenu"
							aria-labelledby="idMenuDropdown" style="width: 165px !important;">
							<a class="dropdown-item importdropdown" href="#">Excel </a> <a
								class="dropdown-item importdropdown" href="#">Pdf</a>
						</div>
					</div>
				</div>
				-->
				<table id="reportTable4"
					class="display row-border dataTable meterialform" cellspacing="0"
					width="100%">
					<thead>
						<tr>
							<th class="text-center">Tax Details</th>

							<th class="text-center">July</th>
							<th class="text-center">August</th>
							<th class="text-center">September</th>
							<th class="text-center">October</th>
							<th class="text-center">November</th>
							<th class="text-center">December</th>
							<th class="text-center">January</th>
							<th class="text-center">February</th>
							<th class="text-center">March</th>
							<th class="text-center">April</th>
							<th class="text-center">May</th>
							<th class="text-center">June</th>
						</tr>
					</thead>
					<tbody>
						<tr data-toggle="modal" data-target="#viewModal">
							<td align="center"><h6>Sales</h6></td>
							<td align="center">100,000</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>

						<tr>
							<td align="center"><h6>Income Tax</h6></td>
							<td align="center">100,000</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap custom1" style="display: none">
		<div class="container">
			<!-- <div class="row"> -->
			<!-- dashboard cp table begin -->
			<!-- <h3>Reports Coming Soon..</h3> -->
			<!-- dashboard cp table end -->
			<!-- </div> -->
			<div class="clearfix"></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right"
				role="button" style="padding: 4px 25px;">Back</a>
			<h2>Sales Report</h2>
			<p>Sales Report gives you a summary of your monthly, quarterly
				and annual purchases.</p>
			<div class="dropdown chooseteam  mr-0">
				<span class="dropdown-toggle yearly" data-toggle="dropdown"
					id="fillingoption"
					style="margin-right: 10px; display: inline-flex;"><span
					style="height: 32px">Report Type:</span>
					<div class="typ-ret"
						style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 86.42px;">
						<span id="filing_option2" class="filing_option"
							style="vertical-align: top">Custom</span><span
							class="input-group-addon add-on pull-right"
							style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;"><i
							class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
					</div></span>
				<div class="dropdown-menu ret-type"
					style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px">
					<a class="dropdown-item" href="#" value="Monthly"
						onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"
						href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a> <a
						class="dropdown-item" href="#" value="Custom"
						onClick="getval('Custom')">Custom</a>
				</div>
				<span class="datetimetxt monthely-sp" style="display: none"
					id="monthely-sp"> <label id="ret-period">Return
						Period:</label>
					<div class="datetimetxt datetime-wrap pull-right">

						<div class="input-group date dpMonths" id="dpMonths"
							data-date="102/2012" data-date-format="mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px;">
							<input type="text" class="form-control" value="02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"
							style="padding: 4px 10px;; text-transform: uppercase;"
							onClick="getdiv()">Generate</a>
					</div>
				</span> <span style="display: none" class="yearly-sp"> <span
					class="dropdown-toggle yearly" data-toggle="dropdown"
					id="fillingoption1"
					style="margin-right: 10px; display: inline-flex;"><label
						id="ret-period" style="margin-bottom: 3px;">Return Period:</label>
						<div class="typ-ret"
							style="border: 1px solid; border-radius: 2px; background-color: white; height: 27px; align-items: top; min-width: 96.42px;">
							<span style="vertical-align: top;">2017 - 2018</span><span
								class="input-group-addon add-on pull-right"
								style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
								<i class="fa fa-sort-desc"
								style="vertical-align: super; margin-left: 6px;"></i>
							</span>
						</div></Span>
					<div class="dropdown-menu ret-type1" id="financialYear1"
						style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="2017">2017 - 2018</a> <a
							class="dropdown-item" href="#" value="2018">2018 - 2019</a>

					</div> <a href="#" class="btn btn-greendark  pull-right" role="button"
					style="padding: 4px 10px;; text-transform: uppercase;"
					onClick="getdiv()">Generate</a>
				</span> <span class="datetimetxt custom-sp" style="display: block"
					id="custom-sp"> <label
					style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
					<div class="datetimetxt datetime-wrap">

						<div class="input-group date dpCustom" id="dpCustom"
							data-date="10-2-2012" data-date-format="dd-mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control" value="11-02-2014"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
					</div> <label
					style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
					<div class="datetimetxt datetime-wrap">

						<div class="input-group date dpCustom1" id="dpCustom1"
							data-date="10-11-2012" data-date-format="dd-mm-yyyy"
							data-date-viewmode="years" data-date-minviewmode="months"
							style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control" value="11-02-2012"
								readonly=""> <span
								class="input-group-addon add-on pull-right"><i
								class="fa fa-sort-desc"></i> </span>
						</div>
						<a href="#" class="btn btn-greendark  pull-right" role="button"
							style="padding: 4px 10px;; text-transform: uppercase;"
							onClick="getdiv()">Generate</a>
					</div>

				</span>
			</div>
			<div class="clearfix"></div>
			<div class="tab-pane" id="gtab1" role="tabpanel">
				<!-- Summary info start -->
				<div class="normaltable meterialform">
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Filter</div>
						<div class="noramltable-row-desc">
							<input type="text" value="BTB_INVOICE" data-role="tagsinput"
								placeholder="Add tags" class="btaginput" /> <span
								class="btn-remove-tag">Clear All<span data-role="remove"></span></span>
						</div>
					</div>
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
						<div class="noramltable-row-desc">
							<select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Invoice Type</option>
								<option value="tomatoes">Invoice Type1</option>
								<option value="mozarella">Invoice Type2</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Sub Type</option>
								<option value="tomatoes">Sub Type 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Filing Filter</option>
								<option value="tomatoes">Filing Filter 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">GSTIN</option>
								<option value="tomatoes">GSTIN 1</option>
							</select> <select id="dates-field2" class="multiselect-ui form-control"
								multiple="multiple">
								<option value="cheese">Include Cancelled</option>
								<option value="tomatoes">Include Cancelled 1</option>
							</select>
						</div>
					</div>
					<div class="noramltable-row">
						<div class="noramltable-row-hdr">Filter Summery</div>
						<div class="noramltable-row-desc">
							<div class="normaltable-col hdr">
								Total Transactions
								<div class="normaltable-col-txt">24560</div>
							</div>
							<div class="normaltable-col hdr">
								Total IGST
								<div class="normaltable-col-txt">67890</div>
							</div>
							<div class="normaltable-col hdr">
								Total SGST
								<div class="normaltable-col-txt">78903</div>
							</div>
							<div class="normaltable-col hdr">
								Total CGST
								<div class="normaltable-col-txt">50938</div>
							</div>
							<div class="normaltable-col hdr">
								Total CESS
								<div class="normaltable-col-txt">20938</div>
							</div>
							<div class="normaltable-col hdr">
								Total Tax Value
								<div class="normaltable-col-txt">209847</div>
							</div>
						</div>
					</div>
				</div>
				<!-- Summary info end -->
			</div>
			<div class="customtable db-ca-view reportTable reportTable5">
				<!-- <div class="addsales dropdown pull-right mb-2"
					style="z-index: 2; position: relative; top: 38px;">
					<div class="split-button-menu-dropdown importdrop">
						<button
							class="btn btn-blue b-split-right b-r-cta b-m-super-subtle pull-right"
							id="idMenuDropdown" data-toggle="dropdown"
							style="border-left: solid 1px #435a93; border-bottom-left-radius: 0px; color: #435a93; border-top-left-radius: 0px; width: 10px; padding: .4rem 1rem;">
							<span class="showarrow"> <i class="fa fa-caret-down"></i></span>
						</button>
						<button class="btn btn-blue b-split-left b-r-cta b-m-super-subtle"
							id="idMenuDropdown" data-toggle="dropdown" aria-haspopup="true"
							style="width: 131px; box-shadow: none; text-align: left; color: #435a93; border-bottom-right-radius: 0px; border-top-right-radius: 0px; padding: .4rem 1rem;"
							aria-expanded="false" href="#">Download As</button>
						<div class="dropdown-menu dropdown-menu-right importmenu"
							aria-labelledby="idMenuDropdown" style="width: 165px !important;">
							<a class="dropdown-item importdropdown" href="#">Excel </a> <a
								class="dropdown-item importdropdown" href="#">Pdf</a>
						</div>
					</div> 
				</div>
					-->
				<table id="reportTable5"
					class="display row-border dataTable meterialform" cellspacing="0"
					width="100%">
					<thead>
						<tr>
							<th class="text-center">Tax Details</th>

							<th class="text-center">July</th>
							<th class="text-center">August</th>
							<th class="text-center">September</th>
							<th class="text-center">October</th>
							<th class="text-center">November</th>
							<th class="text-center">December</th>
							<th class="text-center">January</th>
							<th class="text-center">February</th>
							<th class="text-center">March</th>
							<th class="text-center">April</th>
							<th class="text-center">May</th>
							<th class="text-center">June</th>
						</tr>
					</thead>
					<tbody>
						<tr data-toggle="modal" data-target="#viewModal">
							<td align="center"><h6>Sales</h6></td>
							<td align="center">100,000</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td align="center"><h6>Income Tax</h6></td>
							<td align="center">100,000</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>



					</tbody>
				</table>
			</div>
		</div>
	</div>
	</div>

	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->
	<!-- Add Modal Start -->
	<div class="modal fade modal-right" id="addModal" tabindex="-1"
		role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-md" role="document">
			<div class="modal-content">
				<div class="modal-body meterialform popupright bs-fancy-checks">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true"> <img
							src="static/images/master/closeicon-blue.png" alt="Close" /></span>
					</button>
					<div class="bluehdr">
						<h3>Add User</h3>
					</div>
					<!-- row begin -->
					<div class="row  p-5">
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">User Name</p>
							<span class="errormsg" id="userName_Msg"></span> <input
								type="text" id="userName" name="userName" required="required"
								placeholder="1001" value="" /> <label for="input"
								class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Role</p>
							<span class="errormsg" id="role_Msg"></span> <input type="text"
								id="role" name="role" required="required" placeholder="190"
								value="" /> <label for="input" class="control-label"></label> <i
								class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Password</p>
							<span class="errormsg" id="password_Msg"></span> <input
								type="text" id="password" name="password" required="required"
								placeholder="190" value="" /> <label for="input"
								class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Confirm Password</p>
							<span class="errormsg" id="confirmPassword_Msg"></span> <input
								type="text" id="confirmPassword" name="confirmPassword"
								required="required" placeholder="190" value="" /> <label
								for="input" class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Company</p>
							<span class="errormsg" id="company_Msg"></span> <input
								type="text" id="company" name="company" required="required"
								placeholder="190" value="" /> <label for="input"
								class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Branch</p>
							<span class="errormsg" id="branch_Msg"></span> <input type="text"
								id="branch" name="branch" required="required" placeholder="190"
								value="" /> <label for="input" class="control-label"></label> <i
								class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Created By</p>
							<span class="errormsg" id="createdBy_Msg"></span> <input
								type="text" id="createdBy" name="createdBy" required="required"
								placeholder="Jhon Smith" value="" /> <label for="input"
								class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="form-group col-md-6 col-sm-12">
							<p class="lable-txt">Date</p>
							<span class="errormsg" id="date_Msg"></span> <input type="text"
								name="date" id="datetimepicker2" required="required"
								placeholder=" " value="" /> <label for="input"
								class="control-label"></label> <i class="bar"></i>
						</div>
						<div class="bdr-b col-12 mb-3">&nbsp;</div>
						<div class="form-group col-md-12 col-sm-12">
							<p class="lable-txt mb-4">Select the Permissions</p>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox" id="view" /> <label
									for="view"><span class="ui"></span> </label> <span
									class="labletxt">View</span>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox" id="admin" /> <label
									for="admin"><span class="ui"></span> </label> <span
									class="labletxt">Admin</span>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox" id="delete" />
								<label for="delete"><span class="ui"></span> </label> <span
									class="labletxt">Delete</span>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox" id="create" />
								<label for="create"><span class="ui"></span> </label> <span
									class="labletxt">Create</span>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="checkbox" id="update" />
								<label for="update"><span class="ui"></span> </label> <span
									class="labletxt">Update</span>
							</div>
						</div>
						<div class="bdr-b col-12">&nbsp;</div>
						<div class="clearfix col-12 mt-4 text-center">
							<a type="button" class="btn btn-blue-dark" data-toggle="modal"
								data-dismiss="modal" aria-label="Close"><span
								aria-hidden="true">ADD</span></a>
						</div>
					</div>
					<!-- row end -->
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	var table = $('table.display').DataTable({
		   "dom": '<"toolbar">frtip',    
		     "pageLength": 5,
			 "responsive":true,
			 "ordering": false,
			 "searching": false,
		     "language": {
		  	    "search": "_INPUT_",
		        "searchPlaceholder": "Search...",
		        "paginate": {
		           "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
					"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
		       }
		     }
		   }); 
		 
		$(".reportTable2  div.toolbar").html('<h4>Monthly Summary of BVM</h4><a href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm" style="position: absolute; top: 3%; z-index: 1;">Download Excel</a>');  
		$(".reportTable4  div.toolbar").html('<h4>Yearly Summary of BVM</h4><a href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm" style="position: absolute; top: 3%; z-index: 1;">Download Excel</a>');
		$(".reportTable5  div.toolbar").html('<h4>Summary of BVM</h4><a href="#" class="btn btn-greendark pull-right btn-all-iview-sales btn-sm" style="position: absolute; top: 3%; z-index: 1;">Download Excel</a>');

		var headertext = [],

		headers = document.querySelectorAll("table.display th"),

		tablerows = document.querySelectorAll("table.display th"),

		tablebody = document.querySelector("table.display tbody");

		for (var i = 0; i < headers.length; i++) {

			var current = headers[i];

			headertext.push(current.textContent.replace(/\r?\n|\r/, ""));

		}

		for (var i = 0, row; row = tablebody.rows[i]; i++) {

			for (var j = 0, col; col = row.cells[j]; j++) {

				col.setAttribute("data-th", headertext[j]);

			}

		}

		/*---*/
		var dateValue = '12-2018';
		var date = $('.dpMonths').datepicker({
			format : "mm-yyyy",
			viewMode : "months",
			minViewMode : "months",

		});

		$('.dpCustom').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		});
		$('.dpCustom1').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		});
	</script>
</body>
</html>
