function getSankeyGraph(keyword, threshold) {	
	jQuery("#divUser").hide();
	jQuery("#divThres").show();
	jQuery("#divUserCount").hide();
	jQuery("#second").text("Topic Flow - Semantic");
	$("#btnReset").prop('value', 'User Flow')
	
	if (!keyword){
		keyword = "Obama & Muslim";
	}
	var rootURL = encodeURI(server + "/RumourFlow/rest/RedditData/search/sankey/" + keyword);
	$.ajax({
		type : 'GET',
		url : rootURL,
		dataType : "json",
		success : function(data, err) {
			showSankeyGraph(data, threshold);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('addWine error: ' + textStatus);
		}
	});
}

function getSankeyUserGraph(keyword, threshold) {	
	jQuery("#second").text("Topic Flow - Users");
	jQuery("#divUser").hide();
	jQuery("#divThres").hide();
	jQuery("#divUserCount").show();
	$("#btnReset").prop('value', 'Semantic Flow')

	if (!keyword){
		keyword = "Obama & Muslim";
	}
	var rootURL = encodeURI(server + "/RumourFlow/rest/RedditData/search/sankey/user/" + keyword + "/" + threshold);
	$.ajax({
		type : 'GET',
		url : rootURL,
		dataType : "json",
		success : function(data, err) {
			showSankeyGraph(data, 0.0);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('addWine error: ' + textStatus);
		}
	});
}


function showSankeyGraph(data, threshold){
	var units = "Widgets";
	 
	var margin = {
			top : 10,
			right : 50,
			bottom : 20,
			left : 50
		}, width = WIDTH - margin.left - margin.right, height = HEIGHT - margin.top
				- margin.bottom;
	 
	var formatNumber = d3.format(",.0f"),    // zero decimal places
	    format = function(d) { return formatNumber(d) + " " + units; },
	    color = d3.scale.category20();
	 
	// append the svg canvas to the page
	d3.select("#details").html("");
	var svg = d3.select("#details").append("svg")
	    .attr("viewBox", "0 0 " + WIDTH + " " + HEIGHT)
	    .attr("preserveAspectRatio", "xMinYMin meet")
	  .append("g")
	    .attr("transform", 
	          "translate(" + margin.left + "," + margin.top + ")");
	 
	// Set the sankey diagram properties
	var sankey = d3.sankey()
	    .nodeWidth(36)
	    .nodePadding(10)
	    .size([width, height]);
	 
	var path = sankey.link();
	var graph = data;
	
    var nodeMap = {};
    graph.sankeyNodes.forEach(function(x) 
    { 
    	nodeMap[x.name] = x; 
    });
    graph.nodes = graph.sankeyNodes;
    
    var links = [];
    graph.userLinks.forEach(function(x) {
    	if (x.weight >= threshold)
    		links.push(x);
      });
    
    graph.links = links.map(function(x) {
    	  return {
    	        source: nodeMap[x.source],
    	        target: nodeMap[x.target],
    	        value: x.weight
    	      };
     
    });
	 
  sankey
      .nodes(graph.nodes)
      .links(graph.links)
      .layout(32);
	 
// add in the links
  var link = svg.append("g").selectAll(".link")
      .data(graph.links)
    .enter().append("path")
      .attr("class", "link")
      .attr("d", path)
      .style("stroke-width", function(d) { return Math.max(1, d.dy); })
      .sort(function(a, b) { return b.dy - a.dy; });
 
// add the link titles
  link.append("title")
        .text(function(d) {
      	return d.source.name + " → " + 
                d.target.name + "\n" + format(d.value); });
 
// add in the nodes
  var node = svg.append("g").selectAll(".node")
      .data(graph.nodes)
    .enter().append("g")
      .attr("class", "node")
      .attr("transform", function(d) {
    	  if (!d.y){
    		  d.y = 0;
    	  };
    	  if (!d.x)
    		  d.x = 0;
    	  return "translate(" + d.x + "," + d.y + ")";
      })  
    .call(d3.behavior.drag()
      .origin(function(d) { return d; })
      .on("dragstart", function() { 
		  this.parentNode.appendChild(this); })
      .on("drag", dragmove));
	 
// add the rectangles for the nodes
  node.append("rect")
      .attr("height", function(d) { 
    	  if (d.dy){
    		  return d.dy;
    	  }else{
    		  return 0;
    	  }
    	   
      })
      .attr("width", sankey.nodeWidth())
      .style("fill", function(d) { 
		  return d.color = color(d.name.replace(/ .*/, "")); })
	  .on("mouseover",function(d,i){
	        for (var i = 0; i < selectedRumours.length;i++){
	    	  var rumour = rumourDataList[i];
	    	  if (rumour){
	    		  for (var j = 0;j< rumour.submissions.length;j++){
	  	    		var text = rumour.submissions[j].title.toLowerCase();
	  	    		if (text.indexOf(d.name)>=0){
	  	    			var circle = d3.selectAll(".seriesPoints").filter(".rumour" + i).selectAll(".point").filter(".index" + j);
	  	    			circle.attr("r",10);
	  	    			d3.selectAll(".seriesPoints").filter(".rumour" + i).selectAll(".point").filter(".index" + j).classed("main",true);
	  	    		}
	  	    	  }
	    	  }
	    	  
	        } 
	      })
	      .on("mouseout",function(d,i){
	    	  for (var i = 0; i < selectedRumours.length;i++){
		    	  var rumour = rumourDataList[i];
		    	  if (rumour){
		    		  for (var j = 0;j< rumour.submissions.length;j++){
				    		var text = rumour.submissions[j].title.toLowerCase();
				    		if (text.indexOf(d.name)>=0){
				    			var circle = d3.selectAll(".seriesPoints").filter(".rumour" + i).selectAll(".point").filter(".index" + j);
				    			circle.attr("r",5);
				    			d3.selectAll(".seriesPoints").filter(".rumour" + i).selectAll(".point").filter(".index" + j).classed("main",false);
				    		}
				    	  }
		    	  }		    	  
		        } 
	      })
      .style("stroke", function(d) { 
		  return d3.rgb(d.color).darker(2); })
    .append("title")
      .text(function(d) { 
		  return d.name + "\n" + format(d.value); });
	 
// add in the title for the nodes
  node.append("text")
      .attr("x", -6)
      .attr("y", function(d) {
    	  if (d.dy){
    		  return d.dy / 2;
    	  }else{
    		  return 0;
    	  }
    		   
      })
      .attr("dy", ".35em")
      .attr("text-anchor", "end")
      .attr("transform", null)
      .text(function(d) { return d.name; })
    .filter(function(d) { return d.x < width / 2; })
      .attr("x", 6 + sankey.nodeWidth())
      .attr("text-anchor", "start");
 
// the function for moving the nodes
  function dragmove(d) {
    d3.select(this).attr("transform", 
        "translate(" + (
        	   d.x = Math.max(0, Math.min(width - d.dx, d3.event.x))
        	) + "," + (
                   d.y = Math.max(0, Math.min(height - d.dy, d3.event.y))
            ) + ")");
    sankey.relayout();
    link.attr("d", path);
  }

}