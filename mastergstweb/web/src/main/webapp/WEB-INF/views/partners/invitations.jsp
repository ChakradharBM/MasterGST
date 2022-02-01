<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Partner</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-partners/dashboard-partners.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>

	<%@include file="/WEB-INF/views/partners/header.jsp" %>
        <div class="db-ca-wrap mt-4">
            <div class="container">
                 
                <!-- Dashboard body start -->
                 <div class="row">
                    <!-- dashboard left block begin -->
                    <div class="col-md-12 col-sm-12">
                      
						 				
										 
  <!-- table start -->
	<div class="customtable db-ca-view tabtable1">
	<h4>Invitations</h4>
	<table id="invdbTable" class="display row-border dataTable meterialform" cellspacing="0" width="100%">
            <thead>
              <tr>
				<th class="text-center">User Type</th>
				<th class="text-center">Customer Name</th>
				<th class="text-center">Customer Email</th>
				<th class="text-center">Customer Phone</th>
				<th class="text-center">Created.Date</th>
				<th class="text-center">Join.Date</th>
				<th class="text-center">Estimated Cost</th>
				<th class="text-center">Status</th>
                <th class="text-center">Resend</th>
                <th class="text-center">Comments</th>
              </tr>
            </thead>
            <tbody id="invitationsBody">
            </tbody>
          </table></div>
	<!-- table end -->
								</div>
                <!-- dashboard left block end -->
                <!-- Dashboard body end -->

            </div>
        </div>
    </div>
	<!-- commentsModal Start -->			
	<div class="modal fade" id="leadsCommentsModal" role="dialog" aria-labelledby="leadsCommentsModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document">
			<div class="modal-content" style="height:100vh;">
			<div class="modal-header p-0">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span>
                    </button>
                    <div class="bluehdr" style="width:100%">
                        <h3>Add Comments/Narration</h3>
                    </div>
				</div>
				<div class="modal-body meterialform popupright">
					<div class="row pl-4 pr-2 pt-4">
					<span id="nocomments_leads" class="pl-2"></span>
					<div class="form-group col-md-12">
						<div class="leads_commentsTab" style="max-height:300px;min-height:300px;overflow-y:auto;">
						</div>
					</div>
						<div class="form-group col-md-12 mb-0">
						<label for="Comments">Add Comments/Narration :</label>
						<textarea class="form-control" id="leads_comments" style="width:97%;height:110px;border:1px solid lightgray;"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer" style="display:block;text-align:center;">
				<a type="button" class="btn  btn-blue-dark" id="addComment">Save</a>
				 <button type="button" class="btn  btn-blue-dark" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>	
<!-- commentsModal End -->		
    <!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
		<script type="text/javascript">
$(".indformat").each(function(){
		    $(this).html($(this).html().replace(/,/g , ''));
		});
	OSREC.CurrencyFormatter.formatAll({
		selector: '.indformat'
	});
	$(document).ready(function(){
		$('#nav_invitation').addClass('active');
	});
	function resendInvite(userid,id, name, content, email, mobile) {
		$.ajax({
			url: "${contextPath}/pinvresend",
			async: false,
			cache: false,
			data: {
				'userid': userid,
				'id': id,
				'name': name,
				'content': content,
				'email': email,
				'mobile': mobile
			},
			dataType: "text",
			contentType: 'application/json',
			success : function(data) {
				location.reload(true);
			},
			error : function(data) {
			}
		});
	}
</script>
</body>
</html>