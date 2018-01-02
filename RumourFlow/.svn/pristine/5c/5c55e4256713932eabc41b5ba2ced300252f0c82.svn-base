var force = undefined;
var svg = undefined;

function getCentralityGraph(keyword, type, reset) {
	jQuery("#main").text("User Graph");
	if (reset && !ebundle)
		svg = undefined;
	if (!keyword){
		keyword = "Obama & Muslim";
	}
	var rootURL = encodeURI(server
			+ "/RumourFlow/rest/RedditData/search/title/" + keyword);
	$.ajax({
		type : 'GET',
		url : rootURL,
		dataType : "json",
		success : function(data, err) {
			createNetwork(type, data.userLinks);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('addWine error: ' + textStatus);
		}
	});
}

function onlyUnique(value, index, self) {
	return self.indexOf(value) === index;
}

function getCategory(categories, username){
	for (var i = 0; i < categories.length;i++){
		var obj = categories[i];
		if (obj.username == username){
			if (obj.category=="OTHER")
				return "NONE";
			return obj.category;
		}
	}
	return "NONE";
}
function createNetwork(type, edgelist) {
	//get category
	var rootURL = encodeURI(server
			+ "/VisualizationWebService/rest/RedditData/search/category/users");
	var categories;
	$.ajax({
		type : 'GET',
		async:false,
		url : rootURL,
		dataType : "json",
		success : function(data, err) {
			categories = data;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('addWine error: ' + textStatus);
		}
	});
	var nodeHash = {};
	var edgeHash = {};
	var nodes = [];
	var edges = [];

	edgelist.forEach(function(edge) {
		//filter nodes
		if (getCategory(categories, edge.source) != "NONE" || getCategory(categories, edge.target) != "NONE"){
			if (!nodeHash[edge.source]) {
				nodeHash[edge.source] = {
					id : edge.source,
					label : edge.source
				};
				nodes.push(nodeHash[edge.source]);
			}
			if (!nodeHash[edge.target]) {
				nodeHash[edge.target] = {
					id : edge.target,
					label : edge.target
				};
				nodes.push(nodeHash[edge.target]);
			}

			edges.push({
				id : nodeHash[edge.source].id + "-" + nodeHash[edge.target].id,
				source : nodeHash[edge.source],
				target : nodeHash[edge.target],
				weight : 1
			});

		}
	});
	createForceNetwork(type, nodes, edges,categories);
}

function createForceNetwork(type, nodes, edges, categories) {
	if (ebundle){
		var fbundling = d3.ForceEdgeBundling().nodes(force.nodes()).edges(
				force.links());
		var results = fbundling();

		var d3line = d3.svg.line().x(function(d) {
			return d.x;
		}).y(function(d) {
			return d.y;
		}).interpolate("linear");
		//plot the data
		
		svg.selectAll("path").remove();
		for (var i = 0; i < results.length; i++) {
			svg.append("path").attr("d", d3line(results[i])).style(
					"stroke-width", 0.5).attr("stroke", "#ff2222").style(
					"fill", "none").style('stroke-opacity', 0.5);
		}		
		return;
	}
	
	var fisheye = d3.fisheye.circular()
    .radius(120);
	
	// create a network from an edgelist

	var node_data = nodes.map(function(d) {
		return d.id
	});
	var edge_data = edges.map(function(d) {
		return [ d.source.id, d.target.id ];
	});

	var rootURL = encodeURI(server
			+ "/RumourFlow/rest/RedditData/get/users/" + currentRumourName + "/" + type);
	
	// var G = new jsnx.cycleGraph();
	var G;
	var clustering;
	var clExtent;
	var betweenness;
	var bwExtent;
	if (type == 2) {
		G = new jsnx.Graph();
		G.addNodesFrom(node_data);
		G.addEdgesFrom(edge_data);						
		//clustering = jsnx.clustering(G);
		//clExtent = d3.extent(d3.values(clustering));
	} else {
		G = new jsnx.DiGraph();
		G.addNodesFrom(node_data);
		G.addEdgesFrom(edge_data);
	}

	
	// var clExtent = d3.extent(d3.values(clustering._stringValues));

	var sizeScale = d3.scale.linear().domain([ 0, 1 ]).range([ 4, 15 ]);

    if (svg){
    	if (type == 0) {
    		byBW();
    	} else if (type == 1) {    		
    		byCLO(G, nodes, edges);
    	} else if (type == 2) {
    		byClustering();
    	}
    	return;
    }

    
	// check closeness centrality
	function checkCloseness(G, nodes, edges) {
		var centrality = []
		var spl = jsnx.allPairsShortestPathLength(G);
		for (var k = 0; k < nodes.length; k++) {
			var pls = spl.get(nodes[k].id);
			var close = 0;
			for (var l = 0; l < nodes.length; l++) {
				if (pls.get(nodes[l].id))
					close += pls.get(nodes[l].id);
			}
			if (close == 0) {
				centrality[k] = 0;
			} else {
				centrality[k] = 1.0 / close;
			}

		}
		return (centrality);
	}

	function byCLO(nodes, edges) {
		//var cent = checkCloseness(nodes, edges);
		var cent = undefined;
		$.ajax({
			type : 'GET',
			url : rootURL,
			dataType : "json",
			async: false,
			success : function(data, err) {
				cent = data;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert('addWine error: ' + textStatus);
			}
		});
		d3.selectAll("circle").transition().duration(1000).style("fill",
				function(d, index) {
					var tmax = Math.max.apply(null, cent) * 1.0;
					if (tmax == 0) {
						tmax = 1.0;
					}
					return brewer(currentIndex);
				}).attr("r", function(d, index) {
			var tmax = Math.max.apply(null, cent) * 1.0;
			if (tmax == 0) {
				tmax = 1.0;
			}
			try{
				return sizeScale((cent[index] / tmax));
			}catch(ex){
				return sizeScale(4);
			}
		});

		drawCentralityLegend(selectedRumours);
	}

	function byClustering(nodes, edges) {
		var clustering = undefined;
		$.ajax({
			type : 'GET',
			url : rootURL,
			dataType : "json",
			async: false,
			success : function(data, err) {
				clustering = data;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert('addWine error: ' + textStatus);
			}
		});
		
		sizeScale.domain(clExtent);
		d3.selectAll("circle").transition().duration(1000).style("fill",
				function(d) {
					return brewer(currentIndex);
				}).attr("r", function(d) {
			return sizeScale(clustering[d.id])
		});
	}

	function byBW() {
		d3.selectAll("circle").transition().duration(1000).style("fill",
				function(d) {
					var id = "GREEN";
					if (getCategory(categories,d.id) == "JOKE")
						id = "YELLOW";
					if (getCategory(categories,d.id) == "REJECT")
						id = "RED";
					if (getCategory(categories,d.id) == "APPROVE")
						id = "BLUE";
					return id;
				})
		.attr("opacity", function(d) {
			var id = "0.2";
			if (getCategory(categories,d.id) == "JOKE")
				id = "1";
			if (getCategory(categories,d.id) == "REJECT")
				id = "1";
			if (getCategory(categories,d.id) == "APPROVE")
				id = "1";
			return id;
		})
		.attr("r", function(d) {
			return 8;
		});
	}
	
	d3.select("#rumourNetwork").html("");
	var margin = {
			top : 10,
			right : 50,
			bottom : 10,
			left : 50
		}, width = WIDTH - margin.left - margin.right, height = HEIGHT + 80 - margin.top
				- margin.bottom;

	force = d3.layout.force().nodes(nodes).links(edges).size([ WIDTH, 500 ])
			.charge(-300).chargeDistance(100).gravity(0.05).on("tick",
					updateNetwork);
	force.start();
	var zoom = d3.behavior.zoom()
    .scaleExtent([0, 10])
    .on("zoom", zoomed);

	var drag = d3.behavior.drag()
	    .origin(function(d) { return d; })
	    .on("dragstart", dragstarted)
	    .on("drag", dragged)
	    .on("dragend", dragended);
	svg = d3.select("#rumourNetwork").append("svg").attr("id", "thesvg")
	.call(zoom)
	.on("contextmenu", function(d, i) {
				d3.select("#rumourNetwork").style("opacity",0.4);				
				//show selections
				d3.select("#userSelection").classed("hidden", false);				
				d3.event.preventDefault();
			})
	.attr("viewBox", "0 0 " + WIDTH + " " + HEIGHT + 80)
			.attr("preserveAspectRatio", "xMinYMin meet")
			.on("click", function(d, i) {
				d3.selectAll(".clink").style("opacity", function(d,i){
					var id = "0.2";
					if (getCategory(categories,d.id) == "JOKE")
						id = "1";
					if (getCategory(categories,d.id) == "REJECT")
						id = "1";
					if (getCategory(categories,d.id) == "APPROVE")
						id = "1";
					return id;
				});
					
				//hide all other nodes
				d3.selectAll(".nodes").style("opacity", function(d,i){
					var id = "0.2";
					if (getCategory(categories,d.id) == "JOKE")
						id = "1";
					if (getCategory(categories,d.id) == "REJECT")
						id = "1";
					if (getCategory(categories,d.id) == "APPROVE")
						id = "1";
					return id;
				});
				
//				d3.selectAll('.txt')
//		        .text(function(d) {
//	            	return "";
//		        });
				
				//selectedUsers=[];
				//showUserGraph();
			})
			.append("g").attr(
			"transform",
			"translate(" + margin.left + "," + margin.top + ")");
	
	// bind event handlers
	
	function zoomed() {
		  svg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
		}

		function dragstarted(d) {
		  d3.event.sourceEvent.stopPropagation();
		  d3.select(this).classed("dragging", true);
		}

		function dragged(d) {
		  d3.select(this).attr("cx", d.x = d3.event.x).attr("cy", d.y = d3.event.y);
		}

		function dragended(d) {
		  d3.select(this).classed("dragging", false);
		}
		
	svg.append("svg:defs").selectAll("marker").data([ "end" ]) // Different
	// link/path
	// types can be
	// defined here
	.enter().append("svg:marker") // This section adds in the arrows
	.attr("id", String).attr("viewBox", "0 -5 10 10").attr("refX", 15).attr(
			"refY", -1.5).attr("markerWidth", 6).attr("markerHeight", 6).attr(
			"orient", "auto").append("svg:path").attr("d", "M0,-5L10,0L0,5");
	
	// add the links and the arrows
	var path = svg.append("svg:g").selectAll("path").data(force.links())
			.enter().append("svg:path")
			.attr("class", function(d,i){
				return "clink " + "l" + d.source.id + " " + "l" + d.target.id;
			})
			.attr("marker-end", "url(#end)");

	var nodeEnter = svg.selectAll("g.node").data(nodes, function(d) {
		return d.id;
	}).enter().append("g").attr("class", "node").call(drag);

	nodeEnter.append("circle").attr("r", 8).attr("class", function(d,i){
		return "nodes " + "node" + d.id;
	})
	// .style("fill", function (d) {return colors(d.module)})
	.attr("stroke", "black").style("stroke-width", function(d) {
		return d.border ? "3px" : "1px"
	})
	.on("dblclick", function(d) {
		selectedUsers.push(d);
		showUserGraph();
		d3.event.stopPropagation();
	})
	.on("click", function(d) {
		selectedUsers.push(d);
		showUserGraph();
		d3.event.stopPropagation();
	});

	svg.on("mousemove", function() {
		
		fisheye.focus(d3.mouse(this));

		nodeEnter.each(function(d) { d.fisheye = fisheye(d); })
		nodeEnter.attr("transform", function(d) {
		  return "translate(" + d.fisheye.x + "," + d.fisheye.y + ")";
		});
		
		// add the links and the arrows
		svg.selectAll(".clink").attr("d", function(d) {
			var dx = d.target.fisheye.x - d.source.fisheye.x,
            dy = d.target.fisheye.y - d.source.fisheye.y,
            dr = Math.sqrt(dx * dx + dy * dy);
			
			return "M" + 
            d.source.fisheye.x + "," + 
            d.source.fisheye.y + "A" + 
            dr + "," + dr + " 0 0,1 " + 
            d.target.fisheye.x + "," + 
            d.target.fisheye.y;
        });
		d3.event.stopPropagation();
	});
	
//	d3.selectAll('.node').on('mouseover', function(d) {
//	    highlight_nodes(d.id, true);
//	    d3.event.stopPropagation();
//	});
//
//	d3.selectAll('.node').on('mouseout', function(d) {
//		highlight_nodes(d.id, false);
//		d3.event.stopPropagation();
//	});
	
	function highlight_nodes(root, on) {
		//hide all other links		
		d3.selectAll(".clink").style("opacity", function(d,i){
			return on ? "0.1" : "1";
		});
			
		//hide all other nodes
		d3.selectAll(".nodes").style("opacity", function(d,i){
			return on ? "0.1" : "1"
		});	    	    	    
	    
	    //node
	    d3.selectAll(".node" + root)
		.style("opacity", "1");
	    
	    //link
	    d3.selectAll(".clink").filter(".l" + root).style("opacity", "1");
	    
	    //get in degree nodes
	    d3.selectAll(".clink").filter(".l" + root).style("opacity", function(d,i){
	    	d3.selectAll(".node" + d.source.id).style("opacity", "1");
	    	
	    	d3.selectAll(".txt" + d.source.id)
	    	  .style("text-anchor", "middle").attr("y", 3)
	    	  .attr(
	    	  "stroke", "brown").style("font-size", "12px")
	    	  .style("fill","red")
	    	 .text(function(d1) {
	            if (on == true){
	            	return d1.id;
	            }else{
	            	return "";
	            }
	        });
	    	
	    	d3.selectAll(".node" + d.target.id).style("opacity", "1");
	    	
	    	d3.selectAll(".txt" + d.target.id)
	    	  .style("text-anchor", "middle").attr("y", 3)
	    	  .attr(
	    	  "stroke", "blue").style("font-size", "20px")
	    	  .style("fill","green")
	    	 .text(function(d1) {
	            if (on == true){
	            	return d1.id;
	            }else{
	            	return "";
	            }
	        });
	    });
	   
	    //root
	    d3.select('.txt' + root)
        .style("text-anchor", "middle").attr("y", 3)
		.attr(
		"stroke", "brown").style("font-size", "40px")
		.style("fill", "green")
        .text(function(d) {
            if (on == true){
            	return d.id;
            }else{
            	return "";
            }
        });
	    
	    if (selectedUsers.length > 0 && !on){
	    	//hide all other links
			d3.selectAll(".clink").style("opacity", function(d,i){
				return "0.1";
			});
				
			//hide all other nodes
			d3.selectAll(".nodes").style("opacity", function(d,i){
				return "0.1";
			});
			
	    	//selected users
		    selectedUsers.forEach(function(n) {
		    	//text
		        d3.select('.txt' + n.id)
		        .style("text-anchor", "middle").attr("y", 3)
				.attr(
				"stroke", "brown").style("font-size", "12px")
		        .text(function(d) {
	            	return d.id;
		        });
		        //node
			    d3.selectAll(".node" + n.id)
				.style("opacity", "1");    		    
		    });
	    }	    	    	    
	    
	}
	
	nodeEnter.append("text").style("text-anchor", "middle").attr("y", 3).style(
			"stroke-width", "1px").style("stroke-opacity", 0.75).attr(
			"stroke", "white").style("font-size", "8px")
	.attr("class",function(d,i){
		return "txt txt" + d.id;
	})
	.text(function(d) {
		return "";
	}).style("pointer-events", "none")

	nodeEnter.append("text").style("text-anchor", "middle").attr("y", 3).style(
			"font-size", "8px").text(function(d) {
		return "";
	}).style("pointer-events", "none")
	
	
	if (type == 0) {
		byBW();
	} else if (type == 1) {		
		byCLO(G, nodes, edges);
	} else if (type == 2) {
		byClustering();
	}

	
	function drawCentralityLegend(varNames) {
		if (varNames) {
			var legend = svg.selectAll(".legend").data(varNames.slice())
					.enter().append("g").attr("class", "legend").attr(
							"transform", function(d, i) {
								return "translate(55," + (140 - i * 20) + ")";
							});

			legend.append("rect").attr("x", width - 30).attr("width", 10).attr(
					"height", 10).style("fill", function(d, i) {
				return brewer(i);
			})
			.attr("stroke", function(d,i) {
				var name = selectedRumours[i];
				if (currentRumourName === name)
					return d3.rgb(d.color).darker(20);
			})
			.on("click", function(d, i) {
				svg = undefined;
				currentRumourName = d;
				showTopicCloud(currentRumourName);
				currentRumour = "rumour" + i;
				currentIndex = i;				
				//remove other strokes
				d3.selectAll("rect")
	            .attr("stroke", 1);
				
				//add stroke
				d3.select(this)
	             .attr("stroke", d3.rgb(d.color).darker(20));
				getCentralityGraph(currentRumourName, centralityType);
				
				d3.event.stopPropagation();	

			});

			legend.append("text").attr("x", width - 32).attr("y", 6).attr("dy",
					".35em").style("text-anchor", "end").text(function(d) {
				return d;
			});
		}
	}

	function updateNetwork(e) {
		// build the arrow.
		path.attr("d",
				function(d) {
					var dx = d.target.x - d.source.x, dy = d.target.y
							- d.source.y, dr = Math.sqrt(dx * dx + dy * dy);
					return "M" + d.source.x + "," + d.source.y + "A" + dr + ","
							+ dr + " 0 0,1 " + d.target.x + "," + d.target.y;
				});
		if (svg){
			svg.selectAll("g.node").attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")"
			});
		}		
	}
	drawCentralityLegend(selectedRumours);
}
