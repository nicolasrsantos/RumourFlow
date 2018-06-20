$( document ).ready(function() {
	var rootURL = server + "/RumourFlow/rest/RedditData/search/snopes";
	  $.ajax({
			type: 'GET',
			url: rootURL,
			dataType: "json",
			success: initRumour,
			error: function(jqXHR, textStatus, errorThrown){
				//alert('addWine error: ' + textStatus);
			}
		});
	function initRumour(data){
		if (data){
			$.each(data, function(i, item) {			 
				 if (i < 10)
					 $("#snopes").append("<a href=" + item.url + ">" + item.text + "</p>");

	         });
		}		 
	}
});

	
	
	