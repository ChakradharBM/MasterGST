<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Partner</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-partners/dashboard-partners.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script>
var partnerPercentage='<c:out value="${user.partnerPercentage}"/>';
if(partnerPercentage == null || partnerPercentage == ''){
	<c:set var="percent" value="25" />
}else{
	<c:set var="percent" value="${user.partnerPercentage}" />
}
</script>
	<%@include file="/WEB-INF/views/partners/header.jsp" %>
        <div class="db-ca-wrap mt-4">
            <div class="container">
                 
                <!-- Dashboard body start -->
                 <div class="row">
                    <!-- dashboard left block begin -->
                    <div class="col-md-12 col-sm-12">
                      
						 				
										 
  <!-- table start -->
	<div class="customtable db-ca-view tabtable1">
	<table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            <thead>
              <tr>
				<th class="text-center">Customer Id</th>
				<th class="text-center">Customer Name</th>
				<th class="text-center">Customer Email</th>
				<th class="text-center">Subscription Type</th>
				<th class="text-center">Subscription Date</th>
				<th class="text-center">Paid Amount</th>
                <th class="text-center">My Revenue</th>
              </tr>
            </thead>
            <tbody>
			<c:forEach items="${clientsList}" var="client" varStatus="slno">
              <tr data-toggle="modal" data-target="#viewModal${client.id}">
				<td align="center">${slno.count}</td>            
				<td align="center">${client.name}</td>
				<td align="center">${client.email}</td>
				<td align="center">${client.subscriptionType}</td>
				<td align="center"><fmt:formatDate value="${client.updatedDate}" pattern="dd/MM/yyyy" /></td>
				<td align="center"><i class="fa fa-rupee"></i>${client.subscriptionAmount}</td>
				<c:if test="${empty user.partnerPercentage}">
				<td align="center"><i class="fa fa-rupee"></i><fmt:formatNumber type = "number" minFractionDigits="2" maxFractionDigits="2" value = "${client.subscriptionAmount*25/100}" /></td>
				</c:if>
				<c:if test="${not empty user.partnerPercentage}">
				<td align="center"><i class="fa fa-rupee"></i><fmt:formatNumber type = "number" minFractionDigits="2" maxFractionDigits="2" value = "${client.subscriptionAmount*percent/100}" /></td>
				</c:if>
              </tr>
			</c:forEach>
            </tbody>
          </table></div>
	<!-- table end -->
								</div>
                <!-- dashboard left block end -->
                <!-- Dashboard body end -->

            </div>
        </div>
    </div>
		</div>
		
<!-- viewModal Start -->
<c:forEach items="${clientsList}" var="client">
<div class="modal fade modal-right" id="viewModal${client.id}" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-sm" role="document">
    <div class="modal-content">
       
      <div class="modal-body meterialform popupright"><button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/dashboard-ca/closeicon.png" alt="Close" /></span>
        </button>
      <div class="invoice-hdr bluehdr">
                                <h3>View Billing<br /><span class="gstid">${fullname}</span></h3>
                                 
                            </div>
			 <div class="row  p-5">
                        <div class="form-group col-md-12 col-sm-12">
                            <p class="lable-txt astrich">Customer Id</p>
                            <span class="errormsg" id="serialNumber_Msg"></span>
                            <input type="text" id="serialNumber" name="serialNumber" required="required" data-error="Please enter the customer id" placeholder="GSTN1001" value="${client.id}" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>
                         
                        <div class="form-group col-md-12 col-sm-12">
                            <p class="lable-txt astrich">Customer Name</p>
                            <span class="errormsg" id="businessName_Msg"></span>
                            <input type="text" id="businessName" name="businessName" required="required" data-error="Please enter the customer name" placeholder="BVM IT Consultancy Services" value="${client.name}" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>

                        <div class="form-group col-md-12 col-sm-12">
                            <p class="lable-txt astrich">Customer Email </p>
                            <span class="errormsg" id="businessEmail_Msg"></span>
                            <input type="text" id="businessEmail" name="businessEmail" required="required"  data-error="Please enter the valid email id" placeholder="rajesh@gmail.com" value="${client.email}" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-12 col-sm-12">
                            <p class="lable-txt astrich">Subscription Type</p>
                            <span class="errormsg" id="subscriptionType_Msg"></span>
                             <input type="text" id="subscriptionType" name="subscriptionType" required="required" data-error="Please enter the Subscription type chose" placeholder="SubScription Type" value="${client.subscriptionType}" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-12 col-sm-12">
                            <p class="lable-txt astrich">Paid Amount</p>
                            <span class="errormsg" id="subscriptionType_Msg"></span>
                             <input type="text" id="paidAmt" name="amt" required="required" data-error="Please enter the paid amount" placeholder="Paid Amount" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>

						<div class="form-group col-md-12 col-sm-12">
                            <p class="lable-txt astrich">My Revenue</p>
                            <span class="errormsg" id="subscriptionType_Msg"></span>
                             <input type="text" id="revenue" name="revenue" required="required" data-error="Please enter your revenue" placeholder="My Revenue" value="" />
                            <label for="input" class="control-label"></label>
                            <i class="bar"></i> </div>
														
							<div class="bdr-b col-12">&nbsp;</div>
							 <div class="clearfix col-12 mt-4 text-center">
                            <a type="button" class="btn btn-blue-dark" data-toggle="modal" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">Close</span></a></div>
                    </div>
			 										
														
      </div>
       
    </div>
  </div>
</div>
</c:forEach>
<!-- viewModal End -->

    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
		<script type="text/javascript">

   var table = $('table.display').DataTable({
   "dom": '<"toolbar">frtip',
    
     "pageLength": 5,
	"responsive":true,
     "language": {
  			"search": "_INPUT_",
        "searchPlaceholder": "Search...",
        "paginate": {

           "previous": "<img src='${contextPath}/static/mastergst/images/master/td-arw-l.png' />",

           "next": "<img src='${contextPath}/static/mastergst/images/master/td-arw-r.png' />"

        }

     }

   });
 
 
$(".tabtable1  div.toolbar").html('<h4>Billing</h4>');
 
 
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
   $(document).ready(function(){
		$('#nav_billing').addClass('active');
	});
</script>
</body>
</html>