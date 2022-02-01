<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">

<head>
<title>MasterGST | Reports Tab</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
</head>
<style>

 div.dataTables_wrapper {width: 100%;margin: 0 auto;}
.DTFC_LeftBodyLiner,.DTFC_RightBodyLiner{overflow: hidden!important;width: auto!important;}
.DTFC_LeftHeadWrapper{overflow: hidden!important;}
.db-ca-wrap table.dataTable.no-footer{min-width:103px!important}
.DTFC_LeftBodyWrapper{min-width: 103px!important;}
.customtable .dataTables_wrapper thead th, .customtable.dataTables_wrapper thead td{min-width: 116px;}
.dataTables_scrollBody{border-bottom:none!important}
.DTFC_LeftBodyWrapper{box-shadow: 6px 0 20px 1px rgba(177, 162, 162, 0.3);}
.DTFC_RightBodyWrapper{box-shadow: -15px 0 20px 1px rgba(177, 162, 162, 0.3);}
.dropdown:hover .dropdown-content.reporttax{display: block;}.arrow-up {width: 0; height: 0; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reporttax{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 400px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
</style>
<body>
<!-- header page begin -->
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
	<!--- breadcrumb start -->
	 
	<div class="breadcrumbwrap">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></li>
					</ol>
                    
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	<div class="db-ca-wrap">
		<div class="container"> 
        	<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a> 
			<br/>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
			<div class="dropdown-content reporttax"> <span class="arrow-up"></span><span class="pl-2"> All Months Total Tax and ITC Summary Based on Financial Period</span></div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			<div class="row" style="display:block;">
            	<!-- dashboard ca table begin -->
	                <div class="col-sm-12" >
						<div class ="row mb-4">
							<div class="col-sm-9">
								<h4 class="hdrtitle col-md-12 mb-0 pl-0" style="display: inline-block;">Monthly Tax and ITC summary Report</h4>
								<span style="font-size: 13px;">All Below values are shown in Sales Register And Purchase Register</span> 
							</div>
							<div class="col-sm-3" id="client-fy">
								<h4 class="hdrtitle1" style="display: inline-block;margin-left:-27px;">Financial Year : </h4> 
								<select class="pull-right" name="financialYear" id="financialYear">
									<option value="2021">2021 - 2022</option>
									<option value="2020">2020 - 2021</option>
									<option value="2019">2019 - 2020</option>
									<option value="2018">2018 - 2019</option>
									<option value="2017">2017 - 2018</option>
								</select>
							</div>
						</div>
				<div class="customtable db-ca-view reportTable reportTable2" style="margin-top: -27px;">
	            <table id="report_Table" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
	              <thead>
	                <tr>
	                  <th class="text-center"></th><th class="text-center">April</th><th class="text-center">May</th>
	                  <th class="text-center">June</th><th class="text-center">July</th><th class="text-center">August</th>
	                  <th class="text-center">September</th><th class="text-center">October</th><th class="text-center">November</th>
	                  <th class="text-center">December</th><th class="text-center">January</th><th class="text-center">February</th><th class="text-center">March</th>
					  <th class="text-center">YTD(Year To Date)</th>                  
	                </tr>
	              </thead>
	              <tbody>
				  <c:set var="monthArray" value="${['4','5','6','7','8','9','10','11','12','1','2','3']}" />
					<tr data-toggle="modal" data-target="#viewModal">
						<td align="center"><h6>Sales</h6> </td>
						<c:forEach items="${monthArray}" var="month" varStatus="loop">
						<td class="text-right" id="sales${month}">
							<a class="permissionInvoices-Sales-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister/<c:out value="${month}"/>/<c:out value="${year}"/>?type="><span class="ind_formatss" style="color:black">0.00</span></a></td>
						</c:forEach>
						<td class="text-right ytd ind_formatss ytdSales" id="ytdSales">0.00</td>
                 	</tr>
				 	<tr>
						<td align="center"><h6>Output Tax</h6> </td>
						<c:forEach items="${monthArray}" var="month" varStatus="loop">
							<td class="text-right ind_formatss" id="salestax${month}" data-toggle="tooltip" title = "">0.00</td>
						</c:forEach>
						<td class="text-right ytd ind_formatss ytdSalestax" id="ytdSalestax">0.00</td>
	                 </tr>
	                  <tr>
						<td align="center"><h6>Purchases</h6> </td>
						<c:forEach items="${monthArray}" var="month" varStatus="loop">
							<td class="text-right" id="purchase${month}"><a href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/<c:out value="${month}"/>/<c:out value="${year}"/>?type="><span class="ind_formatss" style="color:black">0.00</span></a></td>
						</c:forEach>
						<td class="text-right ytd ind_formatss ytdPurchases" id="ytdPurchases">0.00</td>
	                 </tr>
					 <tr>
						<td align="center"><h6>Input Tax</h6> </td>
						<c:forEach items="${monthArray}" var="month" varStatus="loop">
							<td class="text-right ind_formatss" id="purchasetax${month}" data-toggle="tooltip" title = "">0.00</td>
						</c:forEach>
						<td class="text-right ytd ind_formatss ytdPurchasetax" id="ytdPurchasetax">0.00</td>
	                 </tr>
					</tbody>
	            </table>
				<p style="font-size: 13px; text-align: right;">Note: The above currency is in Indian Rupees</p>
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
        <div class=" p-2 steptext-wrap"><span class="pl-2"> All Months Total Tax and ITC Summary Based on Financial Period</span> </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
 <!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
	
<script type="text/javascript">
$(document).ready(function() {
	$( ".helpicon" ).hover(function() {$('.reporttax').show();
	}, function() {$('.reporttax').hide();
	});
    var table = $('#report_Table').DataTable({
		"paging": false,
        scrollX: true,
        scrollCollapse: true,
		ordering: false,
		info:false,
		filter:false,
		lengthChange: false,
		fixedColumns:{
			leftColumns:1,
			rightColumns:1
		}
   });
    var finYear=new Date().getFullYear();
    var currentmonth = new Date().getMonth()+1;
	if(currentmonth < 4){finYear--;}
    summaryReport(finYear);
});
$('#financialYear').change(function() {
	var finYear = $(this).val();
	summaryReport(finYear);
});
function summaryReport(finYear){
	 $.ajax({
			url: "${contextPath}/mdfyclntMonthlyreturns?clientid=${client.id}&year="+finYear,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(summary) {
				$.each(summary, function(key, value){
						if(key == '1' || key == '2' || key =='3'){
							var finYears = parseInt(finYear)+1;
							$('#sales'+key).html('<a class="permissionInvoices-Sales-View-Invoices-Sales" style="display:none;"><span class="ind_formatss" style="color:black">'+value.Sales+'</span></a><a class="permissionInvoices-Sales-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister/'+key+'/'+finYears+'?type="><span class="ind_formatss" style="color:black">'+value.Sales+'</span></a>');
							$('#purchase'+key).html('<a class="permissionInvoices-Purchase-View-Invoices-Purchase" style="display:none;"><span class="ind_formatss" style="color:black">'+value.Purchase+'</span></a><a class="permissionInvoices-Purchase-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/'+key+'/'+finYears+'?type="><span class="ind_formatss" style="color:black">'+value.Purchase+'</span></a>');
						}else{
							$('#sales'+key).html('<a class="permissionInvoices-Sales-View-Invoices-Sales" style="display:none;"><span class="ind_formatss" style="color:black">'+value.Sales+'</span></a><a class="permissionInvoices-Sales-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/SalesRegister/'+key+'/'+finYear+'?type="><span class="ind_formatss" style="color:black">'+value.Sales+'</span></a>');
							$('#purchase'+key).html('<a class="permissionInvoices-Purchase-View-Invoices-Purchase" style="display:none;"><span class="ind_formatss" style="color:black">'+value.Purchase+'</span></a><a class="permissionInvoices-Purchase-View" href="${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/PurchaseRegister/'+key+'/'+finYear+'?type="><span class="ind_formatss" style="color:black">'+value.Purchase+'</span></a>');
						}
						$('#salestax'+key).html(value.SalesTax);
						$('#purchasetax'+key).html(value.PurchaseTax);
						$('.ytdSales').html(parseFloat(summary[key].totSales));
						$('.ytdPurchases').html(parseFloat(summary[key].totpurchase));
						$('.ytdSalestax').html(parseFloat(summary[key].totSalesTax));
						$('.ytdPurchasetax').html(parseFloat(summary[key].totPurchasetax));
					});
					OSREC.CurrencyFormatter.formatAll({
						selector: '.ind_formatss'
					});
					rolesPermissions();
			}
		});
}
</script>
</body>
</html>
