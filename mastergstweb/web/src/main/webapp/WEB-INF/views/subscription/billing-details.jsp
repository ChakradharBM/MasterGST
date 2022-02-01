<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Billing</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<style>body{;min-height:90%;padding-bottom:0rem!important;}</style>
</head>
<body>
<c:set var="varASP" value="<%=MasterGSTConstants.ASPDEVELOPER%>"/>
<c:set var="varSuvidha" value="<%=MasterGSTConstants.SUVIDHA_CENTERS%>"/>
<%@include file="/WEB-INF/views/includes/app_header.jsp" %>

<div class="bodywrap">
<!-- Header tag only for asp start -->
			<div class="bodybreadcrumb" id="breadcrumb" style="display: block">
				<div class="container">
					<div class="row">
						<div class="col-sm-12">
							<div class="navbar-left">
								<ul>
									<li>Subscriptions</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- Header tag only for asp end -->
<!--- breadcrumb start -->
<div class="breadcrumbwrap main">
<div class="container">
	<div class="row">
        <div class="col-md-12 col-sm-12">
				<ol class="breadcrumb">
					<li class="breadcrumb-item active">Subscriptions</li>
				</ol>
				<span id="errorMessage" class="ml-4" style="color:red;font-size:12px;font-weight:bold;"></span>
				<span class="datetimetxt"> 
					<!-- <input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i>  -->
				</span>
				<span class="f-14-b pull-right mt-1 font-weight-bold">
					<!-- Return Period: -->
				</span>
				<div class="retresp"></div>
			</div>
		</div>
	</div>
</div>

<!--- breadcrumb end -->
 <div class="bodywrap billing-body" style="margin-top: 43px!important;padding-top: 110px!important;">
  <div class="db-inner">
    <!-- begin content  -->
	<div class="container db-inner-txt">
      <div>
        <div class="credentialwrap">
          <div class="row">
            <div class="col-md-12 col-sm-12">
              <div class="search_details">
                <div class="subscript_details_box">
                  <h3>
	                 <c:choose>
	                 	<c:when test="${subscription.apiType eq 'EWAYAPI'}">
	                 		EWAY BILL API
	                 	</c:when>
	                 	<c:when test="${subscription.apiType eq 'GSTAPI'}">
	                 		GST API
	                 	</c:when>
	                 	<c:when test="${subscription.apiType eq 'EWAYBILLSANDBOXAPI'}">
	                 		EWAY BILL SANDBOX API
	                 	</c:when>
	                 	<c:when test="${subscription.apiType eq 'E-INVOICEAPI'}">
	                 		E-INVOICE API
	                 	</c:when>
	                 	<c:when test="${subscription.apiType eq 'E-INVOICESANDBOXAPI'}">
	                 		E-INVOICE SANDBOX API
	                 	</c:when>
	                 	<c:otherwise>
	                 		GST SANDBOX API
	                 	</c:otherwise>
	                 </c:choose> 
                  
                  Details <!--<c:if test="${usertype ne varASP && usertype ne varSuvidha}"><a href='${contextPath}/sbscrpln/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/${pType}/${month}/${year}' class="btn btn-sm btn-blue pull-right">Upgrade Subscription</a></c:if>-->
                  <span><a href="${contextPath}/dbllng/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<%=MasterGSTConstants.ASPDEVELOPER%>/${month}/${year}" class="btn btn-blue-dark urllink" style="float:right">BACK</a></span></h3>
                  <div class="">&nbsp;</div>
                  <div class="table_custom row">
                    <div class="table_row col-md-6 col-sm-12 nopad_l">
                      <div class="table_fild">My Registration Date : </div>
                      <div class="table_txt"><fmt:formatDate value="${userregisterdate}" pattern="dd/MM/yyyy" /></div>
                    </div>
                    <div class="table_row col-md-6 col-sm-12 nopad_r">
                      <div class="table_fild">Subscription Amount : </div>
                      <div class="table_txt"><i class="fa fa-rupee"></i> ${subscription.paidAmount}</div>
                    </div>
                    <div class="table_row col-md-6 col-sm-12 nopad_l">
                      <div class="table_fild" style="width: 15%;">Product Type :</div>
                      <div class="table_txt">
                      	<c:choose>
		                 	<c:when test="${subscription.apiType eq 'EWAYAPI'}">
		                 		EWAY BILL API
		                 	</c:when>
		                 	<c:when test="${subscription.apiType eq 'GSTAPI'}">
		                 		GST API
		                 	</c:when>
		                 	<c:when test="${subscription.apiType eq 'EWAYBILLSANDBOXAPI'}">
		                 		EWAY BILL SANDBOX API
		                 	</c:when>
		                 	<c:when test="${subscription.apiType eq 'E-INVOICEAPI'}">
		                 		E-INVOICE API
		                 	</c:when>
		                 	<c:when test="${subscription.apiType eq 'E-INVOICESANDBOXAPI'}">
		                 		E-INVOICE SANDBOX API
		                 	</c:when>
		                 	<c:otherwise>
		                 		GST SANDBOX API
		                 	</c:otherwise>
	                 	</c:choose> 
                      </div>
                    </div>
                    <div class="table_row col-md-6 col-sm-12 nopad_l">
                      <div class="table_fild">Subscription Type :</div>
                      <div class="table_txt"> Yearly</div>
                    </div>
                    <div class="table_row col-md-6 col-sm-12 nopad_r">
                      <div class="table_fild">Renewal Type :</div>
                      <div class="table_txt"> Annually</div>
                    </div>
                    <div class="table_row col-md-6 col-sm-12 nopad_l">
                      <div class="table_fild">Subscription Start Date :</div>
                      <div class="table_txt"><fmt:formatDate value="${subscription.registeredDate}" pattern="dd/MM/yyyy" /></div>
                    </div>
                    <div class="table_row col-md-6 col-sm-12 nopad_r">
                      <div class="table_fild">Renewal Date :</div>
                      <div class="table_txt"><fmt:formatDate value="${subscription.expiryDate}" pattern="dd/MM/yyyy" /></div>
                    </div>
                    <div class="table_row col-md-6 col-sm-12 nopad_l">
                      <div class="table_fild">Subscription End Date :</div>
                      <div class="table_txt">
                      
                      <%-- <fmt:formatDate value="${subscription.expiryDate}" pattern="dd/MM/yyyy" /> --%>
                      	<c:choose>
							<c:when test="${subscription.subscriptionType eq 'UNLIMITED'}">
								UNLIMITED
							</c:when>
							<c:otherwise>
			                    <fmt:formatDate value="${subscription.expiryDate}" pattern="dd/MM/yyyy" />
							</c:otherwise>
						</c:choose>
                      </div>
                    </div>
					<div class="table_row col-md-6 col-sm-12 nopad_r">
                      <div class="table_fild">Permitted Invoices :</div>
                      <c:choose>
							<c:when test="${subscription.subscriptionType eq 'UNLIMITED'}">
								 <div class="table_txt">UNLIMITED</div>
							</c:when>
							<c:otherwise>
			                    <div class="table_txt">${subscription.allowedInvoices}</div>
							</c:otherwise>
						</c:choose>
                    </div>
					<div class="table_row col-md-6 col-sm-12 nopad_l">
                      <div class="table_fild">Remaining Invoices :</div>
                     	<c:choose>
							<c:when test="${subscription.subscriptionType eq 'UNLIMITED'}">
								<div class="table_txt">UNLIMITED</div>
							</c:when>
							<c:otherwise>
				            	<div class="table_txt">${subscription.allowedInvoices-subscription.processedInvoices} 
									<jsp:useBean id="today" class="java.util.Date" />
									<c:if test="${usertype ne varASP && usertype ne varSuvidha && today.time lt subscription.expiryDate.time}">
										<c:if test="${(subscription.allowedInvoices-subscription.processedInvoices) < 1000}">
											<a href='${contextPath}/sbscrpln/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/AddOn/${month}/${year}' class="btn btn-sm btn-blue pull-right">Buy More Invoices</a>
										 </c:if>
									</c:if>
								</div>
							</c:otherwise>
						</c:choose>
					
					<%-- <c:if test="${subscription.apiType eq 'EWAYBILLSANDBOXAPI' || subscription.apiType eq 'GSTSANDBOXAPI'}">
					  <div class="table_txt">${subscription.allowedInvoices-subscription.processedSandboxInvoices} 
					  	<jsp:useBean id="dtoday" class="java.util.Date" />
					  	<c:if test="${usertype ne varASP && usertype ne varSuvidha && dtoday.time lt subscription.expiryDate.time}">
						  <c:if test="${(subscription.allowedInvoices-subscription.processedSandboxInvoices) < 1000}">
								<a href='${contextPath}/sbscrpln/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/AddOn/${month}/${year}' class="btn btn-sm btn-blue pull-right">Buy More Invoices</a>
						  </c:if>
					  	</c:if>
					 </div>
					</c:if> --%>
					 					 
                    </div>
                  </div>
                </div>
              </div>
              <!--   profile table begin -->
              <div class="customtable">
                <div class="customtable-hdr">
                  <h6>My Credentials List</h6>
                </div>
                <table id="dbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
                  <thead>
                    <tr>
                      <th>Order ID</th>
                      <th>Payment Date</th>
                      <th>Status</th>
                      <th>Transaction ID</th>
                      <th>Paid Amount ( <i class="fa fa-rupee"></i> )</th>
                    </tr>
                  </thead>
                  <tbody>
					<c:forEach items="${paymentList}" var="payment">
                    <tr>
                    <td>
	                     <c:if test="${payment.orderId eq null}">${payment.id}</c:if>
	                     <c:if test="${payment.orderId ne null}">${payment.orderId}</c:if>
                     </td>
                      <td><fmt:formatDate value="${payment.paymentDate}" pattern="dd/MM/yyyy" /></td>
                      <td>${payment.status}</td>
                      <td>
	                      <c:if test="${payment.trackingId eq null}">${payment.response}</c:if>
		                  <c:if test="${payment.trackingId ne null}">${payment.trackingId}</c:if>
                      </td>
                      <td>${payment.amount}</td>
                    </tr>
					</c:forEach>
                  </tbody>
                </table>
              </div>
              <!--   profile table end -->
            </div>
             
             
          </div>
        </div>
      </div>
    </div>
    <!-- end content  -->
  </div>
  <!-- Button trigger modal -->
</div>
<!-- footer begin here -->
 <%@include file="/WEB-INF/views/includes/footer.jsp" %>
<!-- footer end here -->
<script type="text/javascript">

   var table = $('table.display').DataTable({
            dom: 'Bfrtip',
            "pageLength": 5
        });
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
	$('#nav-billing').addClass('active');
	$('.deactivelink').removeClass('active');
  });
 </script>
</body>
</html>
