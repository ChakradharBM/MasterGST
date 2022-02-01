<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | GSTR2A vs Purchase Register</title>
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
.dropdown:hover .dropdown-content.reportSummaryGSTR2AVS2{display: block;}.arrow-up {width: 0; height: 14px; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reportSummaryGSTR2AVS2{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 606px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
</style>
<script type="text/javascript">
function updateYearlyOption(value){document.getElementById('yearlyoption1').innerHTML=value;document.getElementById('yearlyoption2').innerHTML=value;}
function getval(sel){document.getElementById('filing_option1').innerHTML=sel;document.getElementById('filing_option2').innerHTML=sel;
	if(sel == 'Custom'){
		$('.monthely-sp').css("display","none");
		$('.yearly-sp').css("display","none");
		$('.custom-sp').css("display","inline-block");
		$('.dropdown-menu.ret-type').css("left","16%");
	}else {
		$('.monthely-sp').css("display","none");
		$('.yearly-sp').css("display","inline-block");
		//$('.ret-type1').css("display","inline-block");
		$('.custom-sp').css("display","none");
		$('.dropdown-menu.ret-type').css("left","19%");
	}
};
	function getdiv() {
		var clientname = '<c:out value="${client.businessname}"/>';	
		var abc = $('#fillingoption span').html();
		if (abc == 'Yearly') {
			$('.yearly1').css("display", "block");
			$('.custom1').css("display", "none");
			$('span#fillingoption').css("vertical-align", "bottom");
			var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');
			var yrs = parseInt(fpsplit[0]);
			var yrs1 = parseInt(fpsplit[0])+1;
			yearlyAjaxFuntion(yrs);
		}else{
		    $('.yearly1').css("display","none");$('.custom1').css("display","block");$('span#fillingoption').css("vertical-align","bottom");
		    var fromtime = $('.fromtime').val();var totime = $('.totime').val();
		    $('.fromtime').val(fromtime);$('.totime').val(totime);
		    customAjaxFuntion(fromtime,totime);
		} 
	}
	
</script>
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
						<li class="breadcrumb-item active">GSTR2A - VS - Purchase Register Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
  <div class="db-ca-wrap yearly1">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>GSTR2A vs Purchase Register Supplier Wise Report</h4><p>This Report gives you the comparision between GSTR2A And Purchase Register Supplier wise</p>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
			<div class="dropdown-content reportSummaryGSTR2AVS2"> <span class="arrow-up"></span>
			<ul class="pl-2 mb-0">
                <li> <span class="steptext-desc"><b>1.  Purchase Register </b><span class="colon" style="margin-left: 6px;">:</span><span class="pl-2"> All the Purchase Invoices from your PurchaseRegister/Books</span></span></li>
				<li><span class="steptext-desc"><b>2. GSTR2A </b><span class="colon" style="margin-left: 60px;">:</span><span class="pl-2">Filed Invoices downloaded from GSTIN</span></span></li>
			
		 </ul> 	
		 <b>Comparision</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Supplier Wise Differences between PurchaseRegister and GSTR2A (Purchase Data - (minus) GSTR2A Data)</span>
			</div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
		<div class="dropdown chooseteam mr-0" style="height: 32px;z-index: 1;display:inline;">
			<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<label>Report Type:</label>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option1" class="filing_option"	style="vertical-align: bottom">Yearly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i> 
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type" style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px"> 
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					<a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div> 
				<span style="display: inline-block;margin-bottom: 4px;" class="yearly-sp">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Return Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption1" class="yearlyoption">2021 - 2022</span><span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc" style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div></Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;z-index:1" onClick="getdiv()">Generate</a>
				</span>
				<span class="datetimetxt custom-sp" style="display: none;z-index: 1" id="custom-sp" >
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;z-index:1" onClick="getdiv()">Generate</a>
					<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="102/2012" data-date-format="mm-yyyy"	data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="02-2012"	readonly="">
							<span class="input-group-addon add-on pull-right">
								<i class="fa fa-sort-desc" id="date-drop"></i> 
							</span>
						</div>					
					</div>
					<div class="datetimetxt datetime-wrap">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="102/2012" data-date-format="mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="02-2012" readonly="">
							<span class="input-group-addon add-on pull-right">
								<i	class="fa fa-sort-desc" id="date-drop"></i>
							</span>
						</div>
					</div> 
				</span>
			</div>
			<div class=" "></div> 
			<div id="yearProcess" class="text-center"></div>
		<div class="customtable gstr2vs2atable">
                <table id="invoicetable" class="display row-border dataTable meterialform" cellspacing="0" width="100%" style="font-size:12px;">
                  <thead>
				  <tr>
				  	  <!-- <th rowspan="2" class="text-center shead">Invoice Date</th> -->
                      <th rowspan="2" class="text-center shead">GSTIN</th>
                      <th rowspan="2" class="text-center shead">Supplier Name</th>
                      <th colspan="5" class="text-center phead">Purchase Register</th>
                      <th colspan="5" class="text-center ahead">GSTR2A</th>
                      <th colspan="5" class="text-center diffhead">Differences (Purchase Register-GSTR2A)</th>
                    </tr>
                    <tr>
                      <!-- <th class="text-center phead">invoice no</th> -->
                      <th class="text-center phead">invoice Value</th>
                      <th class="text-center phead">tax value</th>
                      <th class="text-center phead">Igst</th>
                      <th class="text-center phead">cgst</th>
                      <th class="text-center phead">sgst</th>
                      <!-- <th class="text-center">invoice no</th> -->
                      <th class="text-center ahead">invoice Value</th>
                      <th class="text-center ahead">tax value</th>
                      <th class="text-center ahead">Igst</th>
                      <th class="text-center ahead">cgst</th>
                      <th class="text-center ahead">sgst</th>
                      <!-- <th class="text-center">invoice no</th> -->
                      <th class="text-center diffhead">invoice Value</th>
                      <th class="text-center diffhead">tax value</th>
                      <th class="text-center diffhead">Igst</th>
                      <th class="text-center diffhead">cgst</th>
                      <th class="text-center diffhead">sgst</th>
                    </tr>
                  </thead>
                  <tbody>
				  </tbody>
                  
                </table>
              </div>
		</div>
		</div>
		<div class="db-ca-wrap custom1" style="display: none">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h4>GSTR2A vs Purchase Register Supplier Wise Report</h4><p>This Report gives you the comparision between GSTR2A and Purchase Register Supplier wise</p>
			
			<div class="dropdown chooseteam  mr-0" style="display:inline;z-index: 1;">
				<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<span style="height: 32px">Report Type:</span>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;z-index:1">
						<span id="filing_option2" class="filing_option"	style="vertical-align: top">Custom</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i>
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type"	style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px">
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a> 
					<a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a>
				</div> 
				<span style="display: none; margin-bottom:4px" class="yearly-sp"> 
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px;">Return Period:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; background-color: white; height: 27px; align-items: top; padding-right:14px; min-width: 104px;max-width: 104px;z-index:1">
							<span style="vertical-align: top;" id="yearlyoption2" class="yearlyoption">2021 - 2022</span>
							<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 8px;">
								<i class="fa fa-sort-desc"	style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div>
					</Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')" value="2021">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')" value="2020">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')" value="2019">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')" value="2018">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')" value="2017">2017 - 2018</a>
					</div> 
					<span><a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase;z-index:1"	onClick="getdiv()">Generate</a></span>
				</span> 
				<span class="datetimetxt custom-sp" style="display:block;z-index: 1" id="custom-sp"> 
				<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px; text-transform: uppercase;z-index:1"	onClick="getdiv()">Generate</a>
					<div class="datetimetxt datetime-wrap to-picker">
					<label style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">To:</label>
						<div class="input-group date dpCustom1" id="dpCustom1"	data-date="10-11-2012" data-date-format="dd-mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control totime" value="11-02-2012" readonly=""> 
							<span class="input-group-addon add-on pull-right">
								<i class="fa fa-sort-desc" id="date-drop"></i> 
							</span>
						</div>				
					</div>
					<div class="datetimetxt datetime-wrap">
						<label	style="margin-right: 4px; text-transform: initial; margin-bottom: 0 !important; font-size: 1rem;">From:</label>
						<div class="input-group date dpCustom" id="dpCustom" data-date="10-2-2012" data-date-format="dd-mm-yyyy" data-date-viewmode="years" data-date-minviewmode="months" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 0px; margin-right: 10px; height: 28px;">
							<input type="text" class="form-control fromtime" value="11-02-2014" readonly="">
							<span class="input-group-addon add-on pull-right">
								<i class="fa fa-sort-desc" id="date-drop"></i> 
							</span>
						</div>
					</div> 	
				</span>
			</div>
			<div class="customtable gstr2vs2atable">
                <table id="customInvoicetable" class="display row-border dataTable meterialform" cellspacing="0" width="100%" style="font-size:12px;">
                  <thead>
				  <tr>
				  	  <!-- <th rowspan="2" class="text-center shead">Invoice Date</th> -->
                      <th rowspan="2" class="text-center shead">GSTIN</th>
                      <th rowspan="2" class="text-center shead">Supplier Name</th>
                      <th colspan="5" class="text-center phead">Purchase Register</th>
                      <th colspan="5" class="text-center ahead">GSTR2A</th>
                      <th colspan="5" class="text-center diffhead">Differences (Purchase Register-GSTR2A)</th>
                    </tr>
                    <tr>
                      <!-- <th class="text-center phead">invoice no</th> -->
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
			<div class=" "></div>
			
			<div id="customProcess" class="text-center"></div>
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
				<li><span class="steptext-desc"><b>2 . GSTR2A </b><span class="colon" style="margin-left: 64px;">:</span><span class="pl-2">Filed Invoices downloaded from GSTIN</span></span></li>                
		 </ul> 	
 			<b>Comparision</b><span class="colon" style="margin-left: 3px;">:</span><span class="pl-2" style="display: block;">Supplier Wise Differences between PurchaseRegister and GSTR2A (Purchase Data - (minus) GSTR2A Data)</span>
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
		$(function() {
			$( ".helpicon" ).hover(function() {$('.reportSummaryGSTR2AVS2 ').show();
			}, function() {$('.reportSummaryGSTR2AVS2 ').hide();
			});
			var clientname = '<c:out value="${client.businessname}"/>';
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
			//updateReturnPeriod(ev.date);
			month = ev.date.getMonth()+1;
			year = ev.date.getFullYear();
		});
		$('.dpCustom').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		}).on('changeDate', function(ev) {
			//updateReturnPeriod(ev.date);
			day = ev.date.getDate();
			mnt = ev.date.getMonth()+1;
			yr = ev.date.getFullYear();
			$('.fromtime').val(((''+day).length<2 ? '0' : '')+day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		});
		$('.dpCustom1').datepicker({
			format : "dd-mm-yyyy",
			viewMode : "days",
			minViewMode : "days"
		}).on('changeDate', function(ev) {
			//updateReturnPeriod(ev.date);
			day = ev.date.getDate();
			mnt = ev.date.getMonth()+1;
			yr = ev.date.getFullYear();
			$('.totime').val(day+ '-'+((''+mnt).length<2 ? '0' : '') + mnt + '-' + yr);
		});
		$('.dpMonths').datepicker('update', dateValue);
		$('.dpCustom').datepicker('update', customValue);
		$('.dpCustom1').datepicker('update', customValue);
		var currentyear = new Date().getFullYear();
		var currentmonth = new Date().getMonth()+1;
		if(currentmonth < 4){currentyear--;}
		yearlyAjaxFuntion(currentyear);
		var nxtyear = currentyear+1;
		var yearval = currentyear+"-"+nxtyear;
		document.getElementById('yearlyoption1').innerHTML=yearval;
});
		function formatInvoiceDate(date){
			var invDate = new Date(date);
			var day = invDate.getDate() + "";
			var month = (invDate.getMonth() + 1) + "";
			var year = invDate.getFullYear() + "";
			day = checkZero(day);
			month = checkZero(month);
			year = checkZero(year);
			return day + "/" + month + "/" + year;
		}
		function checkZero(data){
			if(data.length == 1){
				data = "0" + data;
		  	}
			return data;
		}
		function yearlyAjaxFuntion(year){
			var dwnldUrl ="${contextPath}/get2a_vs_2Yearly_dwnldExcel/${id}/${client.id}/"+year;
			$.ajax({
				url: "${contextPath}/get2a_vs_2Yearly/${id}/${client.id}/"+year,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				beforeSend: function () {$('#monthProcess').text('Processing...');},
				success : function(invoice) {
					if ($.fn.DataTable.isDataTable("#invoicetable")) {
						$('#invoicetable').DataTable().clear().destroy();
					}
					if(invoice.length>0){
						var content='';
						for(var i =0;i < invoice.length;i++){
							var gstin='-';
							if(invoice[i].gstin != null){
								gstin=invoice[i].gstin;
							}
							var fullname='-';
							if(invoice[i].fullname != null){
								fullname=invoice[i].fullname;
							}
							content += '<tr>';
							content += '<td style="font-size:12px;" align="center" class="sdata">'+gstin+'</td><td style="font-size:12px;" align="center" class="sdata">'+fullname+'</td><td style="font-size:12px;" align="right" class="pdata">'+formatNumber(invoice[i].gstr2AInvoiceValue.toFixed(2))+'</td><td style="font-size:12px;" align="right" class="pdata">'+formatNumber(invoice[i].gstr2ATaxValue.toFixed(2))+'</td><td align="right" style="font-size:12px;" class="pdata">'+formatNumber(invoice[i].gstr2AIGSTValue.toFixed(2))+'</td><td class="text-right pdata" align="right" style="font-size:12px;">'+formatNumber(invoice[i].gstr2ACGSTValue.toFixed(2))+'</td><td class="text-right pdata" align="right" style="font-size:12px;">'+formatNumber(invoice[i].gstr2ASGSTValue.toFixed(2))+'</td><td style="font-size:12px;" align="right" class="adata"><div align="right">'+formatNumber(invoice[i].gstr2InvoiceValue.toFixed(2))+'</div></td><td class="text-right adata">'+formatNumber(invoice[i].gstr2TaxValue.toFixed(2))+'</td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2IGSTValue.toFixed(2))+'</div></td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2CGSTValue.toFixed(2))+'</div></td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2SGSTValue.toFixed(2))+'</div></td>';
							if(invoice[i].diffInvoiceValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffInvoiceValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffInvoiceValue.toFixed(2))+'</div></td>';
							}
							if(invoice[i].diffTaxValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffTaxValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffTaxValue.toFixed(2))+'</div></td>';
							}
							if(invoice[i].diffIGSTValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffIGSTValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffIGSTValue.toFixed(2))+'</div></td>';
							}
							if(invoice[i].diffCGSTValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffCGSTValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffCGSTValue.toFixed(2))+'</div></td>';
							}
							if(invoice[i].diffSGSTValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffSGSTValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffSGSTValue.toFixed(2))+'</div></td>';
							}
							content += '</tr>';
						}
						$('#invoicetable tbody').append(content);
						$("#invoicetable").DataTable({
							"dom": '<"toolbar"f>lrtip<"clear">', 		
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							"scrollX": true,
						    "scrollCollapse": true,
						    fixedColumns:   {
					            leftColumns: 2
						    },
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
									"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
								}
							}
						});
						$(".customtable div.toolbar").append('<a href="'+dwnldUrl+'" id="yearlydwnldxls" class="btn btn-blue permissionExcel_Download_In_Reports-Supplier_Wise_GSTR2_Vs_GSTR2A">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
					}else{
						$("#invoicetable").DataTable({
							"dom": '<"toolbar"f>lrtip<"clear">', 		
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							"scrollX": true,
						    "scrollCollapse": true,
						    fixedColumns:   {
					            leftColumns: 2
						    },
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
								   	"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
								}
							}
						});
						$(".customtable div.toolbar").append('<a href="'+dwnldUrl+'" id="yearlydwnldxls" class="btn btn-blue permissionExcel_Download_In_Reports-Supplier_Wise_GSTR2_Vs_GSTR2A">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
					}
					rolesPermissions();
				},error: function(err){}
			});
		}
		
		function customAjaxFuntion(fromtime,totime){
			var dwnldUrl="${contextPath}/get2a_vs_2Custom_dwnldExcel/${id}/${client.id}/"+fromtime+"/"+totime;
			$.ajax({
				url: "${contextPath}/get2a_vs_2Custom/${id}/${client.id}/"+fromtime+"/"+totime,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				beforeSend: function () {$('#monthProcess').text('Processing...');},
				success : function(invoice) {
					
					if ($.fn.DataTable.isDataTable("#customInvoicetable")) {
						$('#customInvoicetable').DataTable().clear().destroy();
					}
					if(invoice.length>0){
						var content='';
						for(var i =0;i < invoice.length;i++){
							var gstin='-';
							if(invoice[i].gstin != null){
								gstin=invoice[i].gstin;
							}
							var fullname='-';
							if(invoice[i].fullname != null){
								fullname=invoice[i].fullname;
							}
							content += '<tr>';
							content += '<td style="font-size:12px;" align="center" class="sdata">'+gstin+'</td><td style="font-size:12px;" align="center" class="sdata">'+fullname+'</td><td style="font-size:12px;" align="right" class="pdata">'+formatNumber(invoice[i].gstr2AInvoiceValue.toFixed(2))+'</td><td style="font-size:12px;" align="right" class="pdata">'+formatNumber(invoice[i].gstr2ATaxValue.toFixed(2))+'</td><td align="right" style="font-size:12px;" class="pdata">'+formatNumber(invoice[i].gstr2AIGSTValue.toFixed(2))+'</td><td class="text-right pdata" align="right" style="font-size:12px;">'+formatNumber(invoice[i].gstr2ACGSTValue.toFixed(2))+'</td><td class="text-right pdata" align="right" style="font-size:12px;">'+formatNumber(invoice[i].gstr2ASGSTValue.toFixed(2))+'</td><td style="font-size:12px;" align="right" class="adata"><div align="right">'+formatNumber(invoice[i].gstr2InvoiceValue.toFixed(2))+'</div></td><td class="text-right adata">'+formatNumber(invoice[i].gstr2TaxValue.toFixed(2))+'</td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2IGSTValue.toFixed(2))+'</div></td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2CGSTValue.toFixed(2))+'</div></td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2SGSTValue.toFixed(2))+'</div></td>';
							if(invoice[i].diffInvoiceValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffInvoiceValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffInvoiceValue.toFixed(2))+'</div></td>';
							}
							if(invoice[i].diffTaxValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffTaxValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffTaxValue.toFixed(2))+'</div></td>';
							}
							if(invoice[i].diffIGSTValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffIGSTValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffIGSTValue.toFixed(2))+'</div></td>';
							}
							if(invoice[i].diffCGSTValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffCGSTValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffCGSTValue.toFixed(2))+'</div></td>';
							}
							if(invoice[i].diffSGSTValue !=0){
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffSGSTValue.toFixed(2))+'</div></td>';
							}else{
								content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffSGSTValue.toFixed(2))+'</div></td>';
							}
							content += '</tr>';
						}
						$('#customInvoicetable tbody').append(content);
						$("#customInvoicetable").DataTable({
							"dom": '<"toolbar"f>lrtip<"clear">', 		
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							"scrollX": true,
						    "scrollCollapse": true,
						    fixedColumns:   {
					            leftColumns: 2
						    },
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
									"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
								}
							}
						});
						$(".customtable div.toolbar").append('<a href="'+dwnldUrl+'" id="customdwnldxls" class="btn btn-blue permissionExcel_Download_In_Reports-Supplier_Wise_GSTR2_Vs_GSTR2A">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
					}else{
						$("#customInvoicetable").DataTable({
							"dom": '<"toolbar"f>lrtip<"clear">', 		
							"paging": true,
							"searching": true,
							"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
							"responsive": true,
							"ordering": true,
							"scrollX": true,
						    "scrollCollapse": true,
						    fixedColumns:   {
					            leftColumns: 2
						    },
							"language": {
								"search": "_INPUT_",
								"searchPlaceholder": "Search...",
								"paginate": {
								   	"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
									"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
								}
							}
						});
						$(".customtable div.toolbar").append('<a href="'+dwnldUrl+'" id="customdwnldxls" class="btn btn-blue permissionExcel_Download_In_Reports-Supplier_Wise_GSTR2_Vs_GSTR2A">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
					}
					rolesPermissions();
				},error: function(err){}
			});
		}
</script>

<script type="text/javascript">
$(document).ready(function() {
	$(".nav").css("display",'none');
	var year=new Date().getFullYear();
	var dwnldUrl ="${contextPath}/get2a_vs_2Yearly_dwnldExcel/${id}/${client.id}/"+year;
	$.ajax({
		url: "${contextPath}/get2a_vs_2Yearly/${id}/${client.id}/"+year,
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		beforeSend: function () {$('#monthProcess').text('Processing...');},
		success : function(invoice) {
			$('#invoicetable').DataTable().destroy();
			if(invoice.length>0){
				var content='';
				for(var i =0;i < invoice.length;i++){
					var gstin='-';
					if(invoice[i].gstin != null){
						gstin=invoice[i].gstin;
					}
					var fullname='-';
					if(invoice[i].fullname != null){
						fullname=invoice[i].fullname;
					}
					content += '<tr>';
					content += '<td style="font-size:12px;" align="center" class="sdata">'+gstin+'</td><td style="font-size:12px;" align="center" class="sdata">'+fullname+'</td><td style="font-size:12px;" align="right" class="pdata">'+formatNumber(invoice[i].gstr2AInvoiceValue.toFixed(2))+'</td><td style="font-size:12px;" align="right" class="pdata">'+formatNumber(invoice[i].gstr2ATaxValue.toFixed(2))+'</td><td align="right" style="font-size:12px;" class="pdata">'+formatNumber(invoice[i].gstr2AIGSTValue.toFixed(2))+'</td><td class="text-right pdata" align="right" style="font-size:12px;">'+formatNumber(invoice[i].gstr2ACGSTValue.toFixed(2))+'</td><td class="text-right pdata" align="right" style="font-size:12px;">'+formatNumber(invoice[i].gstr2ASGSTValue.toFixed(2))+'</td><td style="font-size:12px;" align="right" class="adata"><div align="right">'+formatNumber(invoice[i].gstr2InvoiceValue.toFixed(2))+'</div></td><td class="text-right adata">'+formatNumber(invoice[i].gstr2TaxValue.toFixed(2))+'</td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2IGSTValue.toFixed(2))+'</div></td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2CGSTValue.toFixed(2))+'</div></td><td class="fnt adata"><div align="right">'+formatNumber(invoice[i].gstr2SGSTValue.toFixed(2))+'</div></td>';
					if(invoice[i].diffInvoiceValue !=0){
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffInvoiceValue.toFixed(2))+'</div></td>';
					}else{
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffInvoiceValue.toFixed(2))+'</div></td>';
					}
					if(invoice[i].diffTaxValue !=0){
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffTaxValue.toFixed(2))+'</div></td>';
					}else{
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffTaxValue.toFixed(2))+'</div></td>';
					}
					if(invoice[i].diffIGSTValue !=0){
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffIGSTValue.toFixed(2))+'</div></td>';
					}else{
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffIGSTValue.toFixed(2))+'</div></td>';
					}
					if(invoice[i].diffCGSTValue !=0){
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffCGSTValue.toFixed(2))+'</div></td>';
					}else{
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffCGSTValue.toFixed(2))+'</div></td>';
					}
					if(invoice[i].diffSGSTValue !=0){
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="text-danger">'+formatNumber(invoice[i].diffSGSTValue.toFixed(2))+'</div></td>';
					}else{
						content += '<td style="font-size:12px;" align="right" class="diffdata"><div align="right" class="negative">'+formatNumber(invoice[i].diffSGSTValue.toFixed(2))+'</div></td>';
					}
					content += '</tr>';
				}
				$('#invoicetable tbody').append(content);
				$("#invoicetable").DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">', 		
					"paging": true,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
					"responsive": true,
					"ordering": true,
					"scrollX": true,
				    "scrollCollapse": true,
				    fixedColumns:   {
			            leftColumns: 2
				    },
					"language": {
						"search": "_INPUT_",
						"searchPlaceholder": "Search...",
						"paginate": {
							"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
							"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
						}
					}
				});
				$(".customtable div.toolbar").append('<a href="'+dwnldUrl+'" id="monthlydwnldxls" class="btn btn-blue permissionExcel_Download_In_Reports-Supplier_Wise_GSTR2_Vs_GSTR2A">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
			}else{
				$("#invoicetable").DataTable({
					"dom": '<"toolbar"f>lrtip<"clear">', 		
					"paging": true,
					"searching": true,
					"lengthMenu": [ [10, 25, 50, -1], [10, 25, 50, "All"] ],
					"responsive": true,
					"ordering": true,
					"scrollX": true,
				    "scrollCollapse": true,
				    fixedColumns:   {
			            leftColumns: 2
				    },
					"language": {
						"search": "_INPUT_",
						"searchPlaceholder": "Search...",
						"paginate": {
						   	"previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",
							"next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"
						}
					}
				});
			}
			rolesPermissions();
		},error: function(err){}
	});
	$(".customtable div.toolbar").append('<a href="'+dwnldUrl+'" id="monthlydwnldxls" class="btn btn-blue permissionExcel_Download_In_Reports-Supplier_Wise_GSTR2_Vs_GSTR2A">Excel<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></a>');
	rolesPermissions();
});

</script>

</body>
</html>