<link rel="stylesheet" href="${contextPath}/static/mastergst/css/userprofile_edit/profile_edits.css" media="all" />
<script src="${contextPath}/static/mastergst/js/jquery/validator.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/profileedit/profile_edit.js" type="text/javascript"></script>
<div class="modal fade" id="profileViewModal" role="dialog" aria-labelledby="profileViewModal" aria-hidden="true">
  <div class="modal-dialog col-md-4 col-sm-12 modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body meterialform bs-fancy-checks popupright" style="height:auto">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"><img src="${contextPath}/static/mastergst/images/credentials/closebtn.png" alt="Close" /></span> </button>
        <div class="bluehdr">
          <h3 class="user_field" data="fullname">View Profile</h3>
        </div>
        <!--  form begin -->
		<div class="container">
			<div class="form-group row">
				<label class="col-md-3" for="name"><strong>Name<span class="coln-txt">:</span></strong></label>
					<div class="col-md-4" id="name"><span class="user_field user_details_field" id="FullName" data="fullname">${fullname}</span></div>
			</div>
			<div class="form-group row">
				<label class="col-md-3" for="name"><strong>EmailId<span class="coln-txt">:</span></strong></label>
					<div class="col-md-4" id="emailId"><span class="user_field user_details_field" id="EmailId" data="email"></span></div>
			</div>
			<div class="form-group row">
				<label class="col-md-3" for="name"><strong>Mobile Number<span class="coln-txt">:</span></strong></label>
					<div class="col-md-4" id="mobileNumber"><span class="user_field user_details_field" id="MobileNumber" data="mobilenumber"></span></div>
			</div>
			<div class="form-group row">
				<label class="col-md-3" for="name"><strong>Address<span class="coln-txt">:</span></strong></label>
					<div class="col-md-4" id="address"><span class="user_field user_details_field" id="Address" data="address"></span></div>
			</div>			
		</div>
	  <div>
      <div class="modal-footer footer-fixed-bottom">
	  <button type="button" class="btn btn-blue-dark"  id="profile_edit_btn" onclick="profileEditDetails()">Edit</button>
	  <button type="button" class="btn  btn-blue-dark" id="profile_save_btn" onclick="profileSaveDetails()">Save </button>
        <button type="button" class="btn btn-lg btn-blue"  data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
</div>
</div>
<script>
$(document).ready(function(){
     scriptElement = document.createElement( "script" );scriptElement.src = "https://maps.googleapis.com/maps/api/js?key=AIzaSyAg_Twe-j7K6RXYeUswZv3gu_kwMrjbatM&libraries=places&region=IN"; scriptElement.async= true;scriptElement.defer= true;
});
</script>