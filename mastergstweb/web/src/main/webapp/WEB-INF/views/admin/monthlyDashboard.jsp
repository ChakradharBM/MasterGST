<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Users</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/dashboard/dashboards.css"
	media="all" />
	<link rel="stylesheet"
	href="${contextPath}/static/mastergst/css/reports/reports.css"
	media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	$('#reports_lnk').addClass('active');
});

</script>
<style type="text/css"> 
#mydiv {position:absolute; top:65%;  z-index: 9; font-size: 23px; left: 50%; color: red; width:30em; height:30px; margin-left: -15em;}
.bodybreadcrumb{margin-top:-8px!important}
</style>
</head>
<body>
	<%@include file="/WEB-INF/views/includes/admin_header.jsp"%>
	<div class="bodywrap" style="min-height: 500px; padding-top:8px">
		<!--- company info bodybreadcrumb start -->
        <div class="bodybreadcrumb">
            <div class="container">
				<div class="row">
					<div class="col-sm-12">
						<div class="bdcrumb-tabs">
							<ul class="nav nav-tabs" role="tablist">
								<li class="nav-item">Reports</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
        </div>
        <!--- company info bodybreadcrumb end -->
	<div class="container" style="padding-top: 30px;">
	<div class ="row">
		<div class="col-sm-12">&nbsp;</div>
		<div class="col-sm-12">&nbsp;</div>
		<div id="mydiv" class="text-center">
		</div>
		<div class="col-sm-12"><a id="backpage_lnk" href="${contextPath}/userreports?id=<c:out value="${id}"/>&fullname=<c:out value="${fullname}"/>&usertype=<c:out value="${usertype}"/>" class="btn btn-blue-dark report mr-0 pull-right" role="button" style="margin-top: -17px;">BACK</a></div>
		<div class="col-sm-12">&nbsp;</div>
					<div class="col-sm-8">
					<h4 class="hdrtitle" style="display: inline-block;color: #374583;font-size: 20px;">Monthly API Usage</h4> 
					</div>
					<div class="col-sm-4">
					<h4 class="hdrtitle1" style="display: inline-block; margin-left:70%; color: #374583;font-size: 20px;">Year : </h4> 
					<select class="pull-right" name="financialYear" id="financialYear">
						<option value="2022">2022</option>
						<option value="2021">2021</option>
						<option value="2020">2020</option>
						<option value="2019">2019</option>
						<option value="2018">2018</option>
						<option value="2017">2017</option>
					</select>
					</div>
					</div>
		<div class="customtable db-ca-view reportTable reportTable2">
            <table id="reportTable2" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
              <thead>
                <tr>
                  <th class="text-center"></th>
                  <th class="text-center">January</th>
                  <th class="text-center">February</th>
                  <th class="text-center">March</th>
				  <th class="text-center">April</th>
                  <th class="text-center">May</th>
                  <th class="text-center">June</th>
                  <th class="text-center">July</th>
                  <th class="text-center">August</th>
                  <th class="text-center">September</th>
                  <th class="text-center">October</th>
                  <th class="text-center">November</th>
                  <th class="text-center">December</th>
                </tr>
              </thead>
              <tbody>
			  <c:set var="monthArray" value="${['1','2','3','4','5','6','7','8','9','10','11','12']}" />
				<tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>Sandbox</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right sandboxtotal" id="Sandbox${month}">${summaryMap[month]['Sandbox']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>Production</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right productiontotal" id="Production${month}">${summaryMap[month]['Production']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>EwayBill Sandbox</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ewaybillSandboxtotal" id="EwayBillSandBox${month}">${summaryMap[month]['EwayBillSandBox']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>EwayBill Production</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right ewaybillProductiontotal" id="EwayBillProduction${month}">${summaryMap[month]['EwayBillProduction']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>E-Invoice Sandbox</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right einvSandboxtotal" id="EInvoiceSandBox${month}">${summaryMap[month]['EInvoiceSandBox']}</td>
					</c:forEach>
                 </tr>
				 <tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>E-Invoice Production</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right einvProductiontotal" id="EInvoiceProduction${month}">${summaryMap[month]['EInvoiceProduction']}</td>
					</c:forEach>
                 </tr>
				<tr data-toggle="modal" data-target="#viewModal">
					<td align="center"><h6>Total</h6> </td>
					<c:forEach items="${monthArray}" var="month" varStatus="loop">
					<td class="text-right total" id="total${month}">${summaryMap[month]['Total']}</td>
					</c:forEach>
                 </tr>
               </tbody>
            </table>
          </div>
		  </div>
</div>
	<!-- footer begin here -->
	<%@include file="/WEB-INF/views/includes/footer.jsp"%>
	<!-- footer end here -->

</body>
<script type="text/javascript">
function formatUpdatedDate(dat, type, row){
	var createdDt = new Date(dat.updatedDate) ;
    var month = createdDt.getUTCMonth() + 1; 
	var day = createdDt.getUTCDate();
	var year = createdDt.getUTCFullYear();
	return day+'-'+month+'-'+year;
}
$(document).ready(function(){
	$('#monthlyapiusage_lnk').addClass('active');
	var createdDt = new Date() ;
	$('#financialYear').val(createdDt.getUTCFullYear());
	$('.sandboxtotal').html(0);
	$('.productiontotal').html(0);
	$('.ewaybillSandboxtotal').html(0);
	$('.ewaybillProductiontotal').html(0);
	$('.einvSandboxtotal').html(0);
	$('.einvProductiontotal').html(0);
	$('.total').html(0);
	$.ajax({
		url: "${contextPath}/mdfymonthlyapiusage?year="+createdDt.getUTCFullYear(),
		async: true,
		cache: false,
		dataType:"json",
		contentType: 'application/json',
		beforeSend: function () {
	    	$('#mydiv').text('Processing...');
	    },
		success : function(summary) {
			$('#mydiv').text('');
			$.each(summary, function(key, value){
				$('#'+key).html(value);								
			});
			$('#total1').html(parseFloat(document.getElementById("Sandbox1").textContent) + parseFloat(document.getElementById("Production1").textContent)+ parseFloat(document.getElementById("EwayBillSandBox1").textContent)+ parseFloat(document.getElementById("EwayBillProduction1").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox1").textContent)+ parseFloat(document.getElementById("EInvoiceProduction1").textContent));
			$('#total2').html(parseFloat(document.getElementById("Sandbox2").textContent) + parseFloat(document.getElementById("Production2").textContent)+ parseFloat(document.getElementById("EwayBillSandBox2").textContent)+ parseFloat(document.getElementById("EwayBillProduction2").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox2").textContent)+ parseFloat(document.getElementById("EInvoiceProduction2").textContent));
			$('#total3').html(parseFloat(document.getElementById("Sandbox3").textContent) + parseFloat(document.getElementById("Production3").textContent)+ parseFloat(document.getElementById("EwayBillSandBox3").textContent)+ parseFloat(document.getElementById("EwayBillProduction3").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox3").textContent)+ parseFloat(document.getElementById("EInvoiceProduction3").textContent));
			$('#total4').html(parseFloat(document.getElementById("Sandbox4").textContent) + parseFloat(document.getElementById("Production4").textContent)+ parseFloat(document.getElementById("EwayBillSandBox4").textContent)+ parseFloat(document.getElementById("EwayBillProduction4").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox4").textContent)+ parseFloat(document.getElementById("EInvoiceProduction4").textContent));
			$('#total5').html(parseFloat(document.getElementById("Sandbox5").textContent) + parseFloat(document.getElementById("Production5").textContent)+ parseFloat(document.getElementById("EwayBillSandBox5").textContent)+ parseFloat(document.getElementById("EwayBillProduction5").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox5").textContent)+ parseFloat(document.getElementById("EInvoiceProduction5").textContent));
			$('#total6').html(parseFloat(document.getElementById("Sandbox6").textContent) + parseFloat(document.getElementById("Production6").textContent)+ parseFloat(document.getElementById("EwayBillSandBox6").textContent)+ parseFloat(document.getElementById("EwayBillProduction6").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox6").textContent)+ parseFloat(document.getElementById("EInvoiceProduction6").textContent));
			$('#total7').html(parseFloat(document.getElementById("Sandbox7").textContent) + parseFloat(document.getElementById("Production7").textContent)+ parseFloat(document.getElementById("EwayBillSandBox7").textContent)+ parseFloat(document.getElementById("EwayBillProduction7").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox7").textContent)+ parseFloat(document.getElementById("EInvoiceProduction7").textContent));
			$('#total8').html(parseFloat(document.getElementById("Sandbox8").textContent) + parseFloat(document.getElementById("Production8").textContent)+ parseFloat(document.getElementById("EwayBillSandBox8").textContent)+ parseFloat(document.getElementById("EwayBillProduction8").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox8").textContent)+ parseFloat(document.getElementById("EInvoiceProduction8").textContent));
			$('#total9').html(parseFloat(document.getElementById("Sandbox9").textContent) + parseFloat(document.getElementById("Production9").textContent)+ parseFloat(document.getElementById("EwayBillSandBox9").textContent)+ parseFloat(document.getElementById("EwayBillProduction9").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox9").textContent)+ parseFloat(document.getElementById("EInvoiceProduction9").textContent));
			$('#total10').html(parseFloat(document.getElementById("Sandbox10").textContent) + parseFloat(document.getElementById("Production10").textContent)+ parseFloat(document.getElementById("EwayBillSandBox10").textContent)+ parseFloat(document.getElementById("EwayBillProduction10").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox10").textContent)+ parseFloat(document.getElementById("EInvoiceProduction10").textContent));
			$('#total11').html(parseFloat(document.getElementById("Sandbox11").textContent) + parseFloat(document.getElementById("Production11").textContent)+ parseFloat(document.getElementById("EwayBillSandBox11").textContent)+ parseFloat(document.getElementById("EwayBillProduction11").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox11").textContent)+ parseFloat(document.getElementById("EInvoiceProduction11").textContent));
			$('#total12').html(parseFloat(document.getElementById("Sandbox12").textContent) + parseFloat(document.getElementById("Production12").textContent)+ parseFloat(document.getElementById("EwayBillSandBox12").textContent)+ parseFloat(document.getElementById("EwayBillProduction12").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox12").textContent)+ parseFloat(document.getElementById("EInvoiceProduction12").textContent));
		}
	});
});

$('#financialYear').change(function() {
			var finYear = $(this).val();
			$('.sandboxtotal').html(0);
			$('.productiontotal').html(0);
			$('.ewaybillSandboxtotal').html(0);
			$('.ewaybillProductiontotal').html(0);
			$('.einvSandboxtotal').html(0);
			$('.einvProductiontotal').html(0);
			$('.total').html(0);
			$.ajax({
				url: "${contextPath}/mdfymonthlyapiusage?year="+finYear,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				beforeSend: function () {
		            $('#mydiv').text('Processing...');
		        },
				success : function(summary) {
					$('#mydiv').text('');
					$.each(summary, function(key, value){
						$('#'+key).html(value);								
					});
				$('#total1').html(parseFloat(document.getElementById("Sandbox1").textContent) + parseFloat(document.getElementById("Production1").textContent)+ parseFloat(document.getElementById("EwayBillSandBox1").textContent)+ parseFloat(document.getElementById("EwayBillProduction1").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox1").textContent)+ parseFloat(document.getElementById("EInvoiceProduction1").textContent));
			$('#total2').html(parseFloat(document.getElementById("Sandbox2").textContent) + parseFloat(document.getElementById("Production2").textContent)+ parseFloat(document.getElementById("EwayBillSandBox2").textContent)+ parseFloat(document.getElementById("EwayBillProduction2").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox2").textContent)+ parseFloat(document.getElementById("EInvoiceProduction2").textContent));
			$('#total3').html(parseFloat(document.getElementById("Sandbox3").textContent) + parseFloat(document.getElementById("Production3").textContent)+ parseFloat(document.getElementById("EwayBillSandBox3").textContent)+ parseFloat(document.getElementById("EwayBillProduction3").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox3").textContent)+ parseFloat(document.getElementById("EInvoiceProduction3").textContent));
			$('#total4').html(parseFloat(document.getElementById("Sandbox4").textContent) + parseFloat(document.getElementById("Production4").textContent)+ parseFloat(document.getElementById("EwayBillSandBox4").textContent)+ parseFloat(document.getElementById("EwayBillProduction4").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox4").textContent)+ parseFloat(document.getElementById("EInvoiceProduction4").textContent));
			$('#total5').html(parseFloat(document.getElementById("Sandbox5").textContent) + parseFloat(document.getElementById("Production5").textContent)+ parseFloat(document.getElementById("EwayBillSandBox5").textContent)+ parseFloat(document.getElementById("EwayBillProduction5").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox5").textContent)+ parseFloat(document.getElementById("EInvoiceProduction5").textContent));
			$('#total6').html(parseFloat(document.getElementById("Sandbox6").textContent) + parseFloat(document.getElementById("Production6").textContent)+ parseFloat(document.getElementById("EwayBillSandBox6").textContent)+ parseFloat(document.getElementById("EwayBillProduction6").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox6").textContent)+ parseFloat(document.getElementById("EInvoiceProduction6").textContent));
			$('#total7').html(parseFloat(document.getElementById("Sandbox7").textContent) + parseFloat(document.getElementById("Production7").textContent)+ parseFloat(document.getElementById("EwayBillSandBox7").textContent)+ parseFloat(document.getElementById("EwayBillProduction7").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox7").textContent)+ parseFloat(document.getElementById("EInvoiceProduction7").textContent));
			$('#total8').html(parseFloat(document.getElementById("Sandbox8").textContent) + parseFloat(document.getElementById("Production8").textContent)+ parseFloat(document.getElementById("EwayBillSandBox8").textContent)+ parseFloat(document.getElementById("EwayBillProduction8").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox8").textContent)+ parseFloat(document.getElementById("EInvoiceProduction8").textContent));
			$('#total9').html(parseFloat(document.getElementById("Sandbox9").textContent) + parseFloat(document.getElementById("Production9").textContent)+ parseFloat(document.getElementById("EwayBillSandBox9").textContent)+ parseFloat(document.getElementById("EwayBillProduction9").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox9").textContent)+ parseFloat(document.getElementById("EInvoiceProduction9").textContent));
			$('#total10').html(parseFloat(document.getElementById("Sandbox10").textContent) + parseFloat(document.getElementById("Production10").textContent)+ parseFloat(document.getElementById("EwayBillSandBox10").textContent)+ parseFloat(document.getElementById("EwayBillProduction10").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox10").textContent)+ parseFloat(document.getElementById("EInvoiceProduction10").textContent));
			$('#total11').html(parseFloat(document.getElementById("Sandbox11").textContent) + parseFloat(document.getElementById("Production11").textContent)+ parseFloat(document.getElementById("EwayBillSandBox11").textContent)+ parseFloat(document.getElementById("EwayBillProduction11").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox11").textContent)+ parseFloat(document.getElementById("EInvoiceProduction11").textContent));
			$('#total12').html(parseFloat(document.getElementById("Sandbox12").textContent) + parseFloat(document.getElementById("Production12").textContent)+ parseFloat(document.getElementById("EwayBillSandBox12").textContent)+ parseFloat(document.getElementById("EwayBillProduction12").textContent)+ parseFloat(document.getElementById("EInvoiceSandBox12").textContent)+ parseFloat(document.getElementById("EInvoiceProduction12").textContent));
				}
			});
		});

</script>

</html>