<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<div class="modal fade" id="deleteModal" role="dialog" aria-labelledby="deleteModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Remove User </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to remove User <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once removed, user will not have access.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnDelete" data-dismiss="modal">Remove User</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Remove</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="deleteAdminModal" role="dialog" aria-labelledby="deleteAdminModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Delete User </h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
          <h6>Are you sure you want to delete User <span id="delPopupDetails"></span> ?</h6>
          <p class="smalltxt text-danger"><strong>Note:</strong> Once deleted, user will not have access.</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" id="btnAdminDelete" data-dismiss="modal">Delete User</button>
        <button type="button" class="btn btn-primary" data-dismiss="modal">Don't Delete</button>
      </div>
    </div>
  </div>
</div>
<script>
function showDeletePopup(userid,email,clientid) {
	$('#deleteModal').modal('show');$('#btnDelete').attr('onclick', "deleteUser('"+userid+"','"+email+"','"+clientid+"')");
}
function deleteUser(userid,email,clientid) {
	$.ajax({
		url: "${contextPath}/delinkUser/"+userid+"/"+email+"/"+clientid,
		success : function(response) {
			table.row($('#row'+userid)).remove().draw();
		}
	});
}

function showAdminUserDeletePopup(userid) {
	$('#deleteAdminModal').modal('show');$('#btnAdminDelete').attr('onclick', "deleteAminUser('"+userid+"')");
}
function deleteAminUser(userid) {
	$.ajax({
		url: "${contextPath}/deleteAdminUser/"+userid,
		success : function(response) {
			table.row($('#row'+userid)).remove().draw();
		}
	});
}
</script>
