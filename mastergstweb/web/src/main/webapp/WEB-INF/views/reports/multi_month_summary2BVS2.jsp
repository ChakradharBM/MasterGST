<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | GSTR2B VS Purchase Register</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script src="${contextPath}/static/mastergst/js/reports/gstr2b_vs_gstr2.js"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<style>.gstr2vs2atable thead td{background-color: #5769bb;color: #fff;}.dataTables_wrapper  .toolbar{float:right;}
.dataTables_scrollBody{border-bottom:none!important}
.shead{background-color: #8064a2!important;}
.phead{background-color:#538dd5!important;}
.ahead{background-color:#e6b8b7!important}
.diffhead{background-color:#c7beb0!important;}
.sdata{color:black!important;background-color:rgba(164, 128, 208, 0.32)!important}
.adata{color:black!important;background-color: rgb(242, 230, 231)!important;}
.pdata{color:black!important;background-color: rgb(217, 229, 245)!important;}
.diffdata{color:black!important;background-color: #f9f6f275!important;}
.text-danger{color:#f50d06!important}.dropdown-content ul li{list-style:none;}
.dropdown:hover .dropdown-content.reportSummaryGSTR2AVS2{display: block;}.arrow-up {width: 0; height: 14px; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reportSummaryGSTR2AVS2{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 540px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
#filing_option1::after{display:none;}
</style>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
  <div class="breadcrumbwrap">
  <div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">GSTR2B - VS - GSTR2 Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>Invoice Wise GSTR2B vs Purchase Register Report</h4>
			<p>
				<span class="reports-monthly">This Report gives you the comparison between GSTR2B-VS-Purchase Register Month wise</span>
				<span class="reports-yearly" style="display: none;">Monthly Sales Report gives you a summary of your monthly sales.</span>
			</p>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
			<div class="dropdown-content reportSummaryGSTR2AVS2"> <span class="arrow-up"></span>
			<ul class="pl-2 mb-0">
                <li> <span class="steptext-desc"><b>1.  Purchase Register </b><span class="colon" style="margin-left: 6px;">:</span><span class="pl-2"> All the Purchase Invoices from your PurchaseRegister/Books</span></span></li>
				<li><span class="steptext-desc"><b>2. GSTR2B </b><span class="colon" style="margin-left: 60px;">:</span><span class="pl-2">Filed Invoices downloaded from GSTIN</span></span></li>
			
		 </ul> 	
		 <b>Comparison</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Differences between PurchaseRegister and GSTR2B (Purchase Data - (minus) GSTR2B Data)</span>
			</div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class=""></div>
			<div class="">
				<div class="dropdown chooseteam mr-0" style="z-index:2;">
					<span class="dropdown-toggle yearly" data-toggle="dropdown" id="fillingoption" style="margin-right: 10px; display: inline-flex;"><label>Report Type:</label>
						<div class="typ-ret" style="z-index: 1;border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
							<span id="filing_option" class="filing_option"	style="vertical-align: top;">Monthly</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 8px;"><i class="fa fa-sort-desc" style="vertical-align: super;"></i> </span>
						</div>
					</span>
					<div class="dropdown-menu ret-type"	style="width: 108px !important; min-width: 36px; left: 19%; top: 26px; border-radius: 2px">
						<a class="dropdown-item" href="#" value="Monthly" onClick="getval('Monthly')">Monthly</a> <a class="dropdown-item"	href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					</div>
					<span class="datetimetxt monthely-sp" style="display: block" id="monthely-sp"> <span><label id="ret-period">Report Period:</label></span>
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
							<div class="typ-ret type_ret_yearly" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
								<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption" class="yearlyoption">2021 - 2022</span>
								<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
									<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
								</span>
							</div>
						</span>
						<div class="dropdown-menu ret-type1" id="financialYear1" style="width: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
						</div>
						<button class="btn btn-greendark pull-right" style="padding: 4px 10px;font-size:14px" onClick="generateData()">Generate</button>
					</span>
					
				</div>
			</div>
			<div class="customtable gstr2vs2atable">
                <table id="comaprison_dataTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%" style="font-size:12px;">
                  <thead>
				  <tr>
				  	  <th rowspan="2" class="text-center shead">Invoice Date</th>
                      <th rowspan="2" class="text-center shead">GSTIN</th>
                      <th rowspan="2" class="text-center shead">Supplier Name</th>
                      <th colspan="6" class="text-center phead">Purchase Register</th>
                      <th colspan="5" class="text-center ahead">GSTR2B</th>
                      <th colspan="6" class="text-center diffhead">Differences (Purchase Register-GSTR2B)</th>
                    </tr>
                    <tr>
                      <th class="text-center phead">Invoice no</th>
                      <th class="text-center phead">Invoice Value</th>
                      <th class="text-center phead">Taxable Value</th>
                      <th class="text-center phead">IGST</th>
                      <th class="text-center phead">CGST</th>
                      <th class="text-center phead">SGST</th>
                      <!-- <th class="text-center">invoice no</th> -->
                      <th class="text-center ahead">Invoice Value</th>
                      <th class="text-center ahead">Taxable Value</th>
                      <th class="text-center ahead">IGST</th>
                      <th class="text-center ahead">CGST</th>
                      <th class="text-center ahead">SGST</th>
                      <!-- <th class="text-center">invoice no</th> -->
                      <th class="text-center diffhead">Invoice Value</th>
                      <th class="text-center diffhead">Taxable Value</th>
                      <th class="text-center diffhead">IGST</th>
                      <th class="text-center diffhead">CGST</th>
                      <th class="text-center diffhead">SGST</th>
                    </tr>
                  </thead>
                  <tbody>
				  </tbody>
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
        <div class=" p-2 steptext-wrap">
		<ul>
        	<li> <span class="steptext-desc"><b>1. Purchase Register </b><span class="colon" style="margin-left: 9px;">:</span><span class="pl-2"> All the Purchase Invoices from your PurchaseRegister/Books</span></span></li>
			<li><span class="steptext-desc"><b>2 . GSTR2B </b><span class="colon" style="margin-left: 64px;">:</span><span class="pl-2">Filed Invoices downloaded from GSTIN</span></span></li>                
		 </ul> 	
 			<b>Comparison</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Differences between PurchaseRegister and GSTR2B (Purchase Data - (minus) GSTR2B Data)</span>
         </div>
      </div>
      <div class="modal-footer"><button type="button" class="btn btn-primary" data-dismiss="modal">Close</button></div>
    </div>
  </div>
</div>
	<!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->

<script type="text/javascript">
	var gstnnumber='<c:out value="${client.gstnnumber}"/>';
	$(document).ready(function() {
		$( ".helpicon" ).hover(function() {$('.reportSummaryGSTR2AVS2 ').show();
		}, function() {$('.reportSummaryGSTR2AVS2 ').hide();
		});
		var date = new Date();
		month = '<c:out value="${month}"/>';
		year = '<c:out value="${year}"/>';
		var year2 = parseInt(year)+1;
		if(month == null || month == '') {
			month = date.getMonth()+1;
			year = date.getFullYear();
		}
		var day = date.getDate();
		var mnt = date.getMonth()+1;
		var yr = date.getFullYear();
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
		$('.dpMonths').datepicker('update', dateValue);
		var currentyear = new Date().getFullYear();
		var currentmonth = new Date().getMonth()+1;
		if(currentmonth < 4){currentyear--;}
		//yearlyAjaxFuntion(currentyear);
		var nxtyear = currentyear+1;
		var yearval = currentyear+"-"+nxtyear;
		document.getElementById('yearlyoption').innerHTML=yearval;
		loadComparisonReportsInvTable('${id}', '${clientid}', '${month}', '${year}', "Monthly");
	
	});
	function generateData() {
		var abc = $('#fillingoption span').html();
		if(abc == 'Monthly'){
			$('.reports-monthly').css("display", "block");$('.reports-yearly').css("display", "none");$('.reports-custom').css("display", "none");
			var fp = $('#monthly').val();
			var fpsplit = fp.split('-');var mn = parseInt(fpsplit[0]);	var yr = parseInt(fpsplit[1]);
			var type='Monthly';
			loadComparisonReportsInvTable('${id}', '${clientid}', mn, yr, "Monthly")
		}else if (abc == 'Yearly') {
			$('#${varRetTypeCode}SummaryTable').css("display", "block");
			$('.reports-monthly').css("display", "none");
			$('.reports-yearly').css("display", "block");
			$('.reports-custom').css("display", "none");
			var year=$('#yearlyoption').html().split("-");
			var type='Yearly';
			loadComparisonReportsInvTable('${id}', '${clientid}', 0, year[0], "Yearly")
		}	
	}
</script>

</body>
</html>