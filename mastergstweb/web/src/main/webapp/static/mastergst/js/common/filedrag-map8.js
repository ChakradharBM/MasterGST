/*
filedrag.js - HTML5 File Drag & Drop demonstration
Featured on SitePoint.com
Developed by Craig Buckler (@craigbuckler) of OptimalWorks.net
*/
(function() {
	var secondcb = $id("bulkviewfiles");
	// getElementById
	function $id(id) {
		return document.getElementById(id);
	}


	// output information
	function Output1(msg) {
		var m = $id("file_name");
		m.innerHTML = msg;
	}

	// file drag hover
	function FileDragHover1(e) {
		e.stopPropagation();
		e.preventDefault();
		$id("bulkfileselect").className = (e.type == "dragover" ? "hover" : ".xls");
		
	}
	// file selection
	function FileSelectHandler1(e) {
		// cancel event and hover styling
		FileDragHover1(e);
		
		// fetch FileList object
		var files = e.target.files || e.dataTransfer.files;

		// process all File objects
		for (var i = 0, f; f = files[i]; i++) {
			ParseFile1(f);
		}
		var typexls = e.type.match('.xls');
		$('#useremailid').removeClass();
		/*if(!typexls){ 
			alert("Please upload xls file");
		}else{
			alert("You upload xls file");
			}*/
		
	}

 
	// output file information
	function ParseFile1(file) {
		secondcb.style.display = "block";
		var x = document.getElementById('bulkfileselect').value;
		Output1(
			 "<p><span><strong><a href='"+ x +"'> <span class='sm-img'></span>" + file.name +"</span></strong> </a>"+
			//"<span>type: <strong>" + file.type +"</span></strong>"+
			//" <span>size: <strong>" + file.size +" bytes</span></strong>"+
			"</p>"
		);
	}
	// initialize
	function Init() {
		var fileselect = $id("bulkfileselect"),
			filedrag = $id("filedrag"),
			submitbutton = $id("submitbutton1");

		// file select
		fileselect.addEventListener("change", FileSelectHandler1, false);

		// is XHR2 available?
		var xhr = new XMLHttpRequest();
		var parser = new DOMParser();
		//var doc = parser.parseFromString(xhr, "application/xls");
		if (xhr.upload) {
			//filedrag.setRequestHeader("Content-Type", "text/xls");
			// file drop
			filedrag.addEventListener("dragover", FileDragHover1, false);
			filedrag.addEventListener("dragleave", FileDragHover1, false);
			filedrag.addEventListener("drop", FileSelectHandler1, false);
			filedrag.style.display = "block";
			
			// remove submit button
			//submitbutton.style.display = "none";		
		}
	}
	// call initialization file
	if (window.File && window.FileList && window.FileReader) {
		Init();
	}
})();

