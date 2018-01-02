	var fill = d3.scale.category20();
	var layout;
	
	var layout1;
	var cloud = [];
	
	function showCommentCloud(keyword, redditID){
		if (!keyword){
			keyword = 'all';
		}
		if (showSecondView){
			d3.select("#wordCloud").html("");
			var rootURL = server + "/RumourFlowNew/rest/RedditData/search/cloud/" + keyword + "/" + redditID;
			$.ajax({
				type: 'GET',
				url: rootURL,
				dataType: "json",
				success: initWordCloud,
				error: function(jqXHR, textStatus, errorThrown){
					alert('addWine error: ' + textStatus);
				}
			});
		}		
	}
	
	function showTopicCloud(){
		var keyword = "";
		if (currentRumourName){
			keyword = currentRumourName;			
		}else{
			selectedRumours.forEach(function(value,index){
				keyword += value + ",";
			});
		}
		if (keyword){
			d3.select("#topicCloud").html("");
			var rootURL = server + "/RumourFlowNew/rest/RedditData/search/tcloud/" + keyword;
			$.ajax({
				type: 'GET',
				url: rootURL,
				dataType: "json",
				success: initTopicCloud,
				error: function(jqXHR, textStatus, errorThrown){
					alert('addWine error: ' + textStatus);
				}
			});
		}
		
	}

	function initTopicCloud(data){
		var scale = d3.scale.linear().range([ 1, 20 ]);
		layout = d3.layout.cloud().size([400, HEIGHT])
		.words(data)
		.padding(1)
	    .rotate(function() { return ~~(Math.random() * 2) * 90; })
	    .font("Impact")
	    .fontSize(function(d) { 
	    	return scale(d.size); 
	    })
	    .on("end", drawTopic);
		layout.start();
	}
	
	function drawTopic(words) {
		d3.select("#topicCloud").append("svg")
	      .attr("preserveAspectRatio", "xMinYMin meet")
	      .attr("viewBox", "0 0 " + 400 + " " + HEIGHT)
	    .append("g")
	      .attr("transform", "translate(" + layout.size()[0] / 2 + "," + layout.size()[1] / 2 + ")")
	    .selectAll("text")
	      .data(words)
	    .enter().append("text")
	      .on("mouseover",function(d,i){
	        for (var i = 0; i < selectedRumours.length;i++){
	    	  var rumour = rumourDataList[i];
	    	  if (rumour){
	    		  for (var j = 0;j< rumour.submissions.length;j++){
	  	    		var text = rumour.submissions[j].title.toLowerCase();
	  	    		if (text.indexOf(d.text)>=0){
	  	    			var circle = d3.selectAll(".seriesPoints").filter(".rumour" + i).selectAll(".point").filter(".index" + j);
	  	    			circle.attr("r",15);
	  	    			d3.selectAll(".seriesPoints").filter(".rumour" + i).selectAll(".point").filter(".index" + j).classed("main",true);
	  	    		}
	  	    	  }
	    	  }
	    	  
	        } 
	      })
	      .on("mouseout",function(d,i){
	    		var points = d3.select("#rumourNetwork").selectAll(".point")
	    		.attr("r", function(d,i){
	    			var sizeScale = d3.scale.linear().domain([ -1, 1 ]).range([ 4, 15 ]);
	    			if (typeof d.sentiment != 'undefined' && d.sentiment && d.sentiment !== "NaN"){
	    				return sizeScale(d.sentiment);
	    			}
	    			return "4px";
	    		});
	    		
	    		d3.selectAll(".seriesPoints").selectAll(".point").classed("main",false);
	      })
	      .style("font-size", function(d) { return d.size + "px"; })
	      .style("font-family", "Impact")
	      .style("fill", function(d, i) { return fill(i); })
	      .attr("text-anchor", "middle")
	      .attr("transform", function(d) {
	        return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
	      })
	      .text(function(d) { 
	    	  return d.text; 
	    });
	}
	
	function initWordCloud(data){
		var scale = d3.scale.linear()
	      .domain([1,10])	// we know score is in this domain
	      .range([20,30])
	      .clamp(true);
		cloud = data;
		layout1 = d3.layout.cloud().size([400, HEIGHT])
		.words(data)
		.padding(1)
	    .rotate(function() { return ~~(Math.random() * 2) * 90; })
	    .font("Impact")
	    .fontSize(function(d) { 
	    	return scale(d.size); 
	    })
	    .on("end", drawWordCloud);
		layout1.start();
	}
	
	function drawWordCloud(words) {
		
		 var node_size = d3.scale.linear()
	      .domain([1,10])	// we know score is in this domain
	      .range([1,16])
	      .clamp(true);
		 
		d3.select("#wordCloud").append("svg")
	      .attr("preserveAspectRatio", "xMinYMin meet")
	      .attr("viewBox", "0 0 " + 400 + " " + HEIGHT)
	    .append("g")
	      .attr("transform", "translate(" + layout1.size()[0] / 2 + "," + layout1.size()[1] / 2 + ")")
	    .selectAll("text")
	      .data(cloud)
	    .enter().append("text")
	      .style("font-size", function(d) { return d.size + "px"; })
	      .style("font-family", "Impact")
	      .style("fill", function(d, i) { return fill(i); })
	      .attr("text-anchor", "middle")
	      .attr("transform", function(d) {
	        return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
	      })
	       .on("mouseover",function(d,i){
	    	if (commentData != null){
	  	    	  	var circle = d3.selectAll(".gNodes").selectAll("circle").attr("r", function(d1,i1){
	  	    	  		if (d1.label.indexOf(d.text) >= 0){
	  	    	  			return node_size(d1.score) + 6;
	  	    	  		};
	  	    	  		return node_size(d1.score);
	  	    	  	})
	  	    	  	.style("fill",function(d1,i1){
	  	    	  		if (d1.label.indexOf(d.text) >= 0){
	  	    	  			return node_size(d1.score) + 6;
	  	    	  		};
	  	    	  		return node_size(d1.score);
	  	    	  	});
	    	}	         
	      })
	      .on("mouseout",function(d,i){
	    	  if (commentData != null){
	    			var circle = d3.selectAll(".gNodes").selectAll("circle").attr("r", function(d,i){
	  	    	  		return node_size(d.score);
	  	    	  	});
		    	}
	      })
	      .text(function(d) { 
	    	  return d.text; 
	    });
	}


	
	
	
