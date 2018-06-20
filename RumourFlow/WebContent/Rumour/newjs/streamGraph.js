jQuery.noConflict();
var selectedUsers = [];
var keywordsLegend = [];
var linksLegend = [];
var debunkLegend = [];

var sizeScale = d3.scale.linear().domain([ -1, 1 ]).range([ 4, 15 ]);

function showStreamGraph() {
	keywordsLegend = selectedRumours;
	linksLegend = selectedRumourLinks;
	debunkLegend = selectedRumourDebunks;

	jQuery("#main").text("Rumour Stream");
	var VIZ = {};
	var margin = {
		top : 10,
		right : 50,
		bottom : 20,
		left : 50
	}, width = WIDTH - margin.left - margin.right, height = HEIGHT - margin.top
			- margin.bottom;

	var x = d3.scale.linear().range([ 0, width ]);

	var y = d3.scale.linear().rangeRound([ height, 0 ]);

	var xAxis;
	if (selectedRumours.length == 1) {
		xAxis = d3.svg.axis().scale(x).orient("bottom").tickFormat(
				function(d) {
					var format = d3.time.format("%m/%y");
					var date = new Date(
							rumourDataList[0].submissions[d].created * 1000);
					return format(date);
				});
	} else {
		/*
		 * var longestRumour = undefined; xAxis =
		 * d3.svg.axis().scale(x).orient("bottom").tickFormat(function(d) { var
		 * format = d3.time.format("%m/%y");
		 * 
		 * var longestRumourFirstDay = new
		 * Date(rumourDataList[0].submissions[d].created * 1000); if
		 * (longestRumour === undefined) { for (var i = 1; i <
		 * rumourDataList.length - 1; i++) if (new
		 * Date(rumourDataList[i].submissions[0].created * 1000) <
		 * longestRumourFirstDay) { longestRumourFirstDay = new
		 * Date(rumourDataList[i].submissions[0].created * 1000); longestRumour =
		 * i; } }
		 * 
		 * var date = new
		 * Date(rumourDataList[longestRumour].submissions[d].created * 1000);
		 * 
		 * return format(date); });
		 */
		xAxis = d3.svg.axis().scale(x).orient("bottom").ticks(22);
	}

	var yAxis = d3.svg.axis().scale(y).orient("left");

	var color = d3.scale.ordinal().range(
			[ "#001c9c", "#101b4d", "#475003", "#9c8305", "#d3c47c" ]);

	var userColor = d3.scale.ordinal().range([ "#F68387" ]);

	var zoom = d3.behavior.zoom().scaleExtent([ 1, 10 ]).on("zoom", zoomed);

	var drag = d3.behavior.drag().origin(function(d) {
		return d;
	}).on("dragstart", dragstarted).on("drag", dragged)
			.on("dragend", dragended);

	d3.select("#rumourNetwork").html("");

	var svg = d3.select("#rumourNetwork").append("svg").attr("id", "thesvg")
			.call(zoom).attr("viewBox", "0 0 " + WIDTH + " " + HEIGHT).on(
					"contextmenu", function(d, i) {
						d3.select("#rumourNetwork").style("opacity", 0.4);
						// show selectionsSqu
						d3.select("#streamSelection").classed("hidden", false);
						d3.event.preventDefault();
					}).on(
					"click",
					function(d, i) {
						selectedSubmission = undefined;
						currentRumourName = undefined;
						selectRumour = undefined;
						currentRumour = undefined;
						currentIndex = undefined;

						svg.selectAll(".streamPath").transition().duration(250)
								.attr("opacity", function(d, j) {
									return 1;
								}).attr("stroke-with", function(d, j) {
									return 1;
								}).attr("stroke", function(d, j) {
									return 1;
								})

						svg.selectAll(".userStreamPath").transition().duration(
								250).attr("opacity", function(d, j) {
							return 1;
						}).attr("stroke-with", function(d, j) {
							return 1;
						}).attr("stroke", function(d, j) {
							return 1;
						})

						// remove other strokes
						d3.selectAll("rect").attr("stroke", 1);
						showTopicCloud();

						// clear users
						d3.selectAll(".seriesPointsUser").each(
								function() {
									d3.select(this).selectAll("text").each(
											function(d, i) {
												d3.select(this).text(
														function(d1, i1) {
															return "";
														})
											});
								});
						// clear topic

						jQuery('.popover').each(function() {
							jQuery(this).remove();
						});
					}).attr("preserveAspectRatio", "xMinYMin meet").append("g")
			.attr("transform",
					"translate(" + margin.left + "," + margin.top + ")");

	var stack = d3.layout.stack().values(function(d) {
		return d.values;
	}).x(function(d) {
		return d.label;
	}).y(function(d) {
		return d.value;
	});

	var area = d3.svg.area().interpolate("cardinal").x(function(d) {
		return x(d.label);
	}).y0(function(d) {
		return y(d.y0);
	}).y1(function(d) {
		return y(d.y0 + d.y);
	});

	var labelVar = 'quarter';
	var fData, fData1, users;

	VIZ.stackChart = function(data, userData, users, offset) {
		var varNames = d3.keys(data[0]).filter(
				function(key, value) {
					return key !== labelVar && key.indexOf("_rumour_title") < 0
							&& key.indexOf("_topic") < 0
							&& key.indexOf("_index") < 0
							&& key.indexOf("_sentiment") < 0
							&& key.indexOf("_comment_count") < 0
							&& key.indexOf("reddit_id") < 0
							&& key.indexOf("_created") < 0
							&& key.indexOf("_permalink") < 0;
				});

		color.domain(selectedRumours);

		var seriesArr = [], series = {};
		varNames.forEach(function(name) {
			series[name] = {
				name : name,
				values : []
			};
			seriesArr.push(series[name]);
		});

		data.forEach(function(d) {
			varNames.map(function(name) {
				series[name].values.push({
					name : name,
					text : d[name],
					label : d[labelVar],
					index : d[name + "_index"],
					value : +d[name + "_comment_count"],
					topic : d[name + "_topic"],
					sentiment : d[name + "_sentiment"],
					created : d[name + "_created"],
					title : d[name + "_rumour_title"],
					redditID : d[name + "_reddit_id"],
					permalink : d[name + "_permalink"]
				});
			});
		});

		var userArr = [], users = {};
		varNames.forEach(function(name) {
			users[name] = {
				name : name,
				values : []
			};
			userArr.push(users[name]);
		});

		userData.forEach(function(d, i) {
			varNames.map(function(name) {
				var obj = new Object();
				var user = "user_name";
				var value = "user_value";
				var topic = "topic";
				var user_count = "user_count";
				var rumour_text = "rumour_text";
				var rumour_created = "rumour_created";
				obj[user] = d[name + "_user_name"];
				obj[value] = d[name + "_user_value"];
				obj[topic] = d[name + "_topic"];
				obj[rumour_text] = d[name + "_text"];
				obj[rumour_created] = d[name + "_created"];
				obj[user_count] = d[name + "_user_count"];
				obj.label = i;
				users[name].values.push(obj);
			});
		});

		x.domain(d3.extent(data.map(function(d) {
			return d.quarter;
		})));

		stack.offset(offset)
		stack(seriesArr);

		for (var i = 0; i < userArr.length; i++) {
			for (var j = 0; j < userArr[i].values.length; j++) {
				userArr[i].values[j].y0 = seriesArr[i].values[j].y0;
				userArr[i].values[j].y = userArr[i].values[j].user_count;
			}
		}

		y.domain([ 0, d3.max(seriesArr, function(c) {
			return d3.max(c.values, function(d) {
				return d.y0 + d.y;
			});
		}) ]);

		zoom.x(x);
		zoom.y(y);

		var selection = svg.selectAll(".series").data(seriesArr).enter()
				.append("g").attr("class", function(d, i) {
					return "series " + d.name;
				});

		selection.append("path").attr("class", "streamPath").attr("d",
				function(d) {
					return area(d.values);
				}).style("fill", function(d, i) {
			return brewer(d.values[i].index);
		}).attr("stroke", "grey");

		svg
				.selectAll(".streamPath")
				.attr(
						"opacity",
						function(d, i) {
							if (currentRumourName) {
								if (currentRumourName.toLowerCase().indexOf(
										d.values[0].text.toLowerCase()) >= 0) {
									return 1;
								} else {
									return 0.1;
								}
							}
						})
				.on(
						"mouseover",
						function(d, i) {
							if (!currentRumourName) {
								svg.selectAll(".streamPath").transition()
										.duration(250).attr("opacity",
												function(d, j) {
													return j != i ? 0.1 : 1;
												});

								svg.selectAll(".userStreamPath").transition()
										.duration(250).attr("opacity",
												function(d, j) {
													return 0.1;
												});
							}
						})
				.on(
						"click",
						function(d, i) {
							currentRumourName = d.values[0].text;
							currentRumour = "rumour" + i;
							currentIndex = i;
							showTopicCloud();
							showSecondView = true;
							showCommentCloud(currentRumourName);

							// highlight
							svg.selectAll(".streamPath").transition().duration(
									250).attr("opacity", function(d, j) {
								return j != i ? 0.1 : 1;
							}).attr("stroke-with", function(d, j) {
								return j != i ? 1 : 5;
							}).attr("stroke", function(d, j) {
								return j != i ? 1 : 5;
							})

							svg.selectAll(".userStreamPath").transition()
									.duration(250).attr("opacity",
											function(d, j) {
												return j != i ? 0.1 : 1;
											}).attr("stroke-with",
											function(d, j) {
												return j != i ? 1 : 5;
											}).attr("stroke", function(d, j) {
										return j != i ? 1 : 5;
									})

							// remove other strokes
							d3.selectAll("rect").attr("stroke", 1);

							// add stroke
							d3.select(".rect" + i).attr("stroke",
									d3.rgb(d.color).darker(20));

							// show users
							if (selectionStream === 2) {
								// clear users
								d3
										.selectAll(".seriesPointsUser")
										.each(
												function() {
													d3
															.select(this)
															.selectAll("text")
															.each(
																	function(d,
																			i) {
																		d3
																				.select(
																						this)
																				.text(
																						function(
																								d1,
																								i1) {
																							return "";
																						})
																	});
												});

								if (currentRumour) {
									var rumour = d3.selectAll(
											".seriesPointsUser").filter(
											"." + currentRumour);
									var previous_user = "";
									rumour
											.selectAll("text")
											.each(
													function(d, i) {
														d3
																.select(this)
																.text(
																		function(
																				d1,
																				i1) {
																			if (d1.user_name == previous_user) {
																				return "";
																			} else {
																				previous_user = d.user_name;
																				return d1.user_name;
																			}
																		});
													});
								} else {
									d3
											.selectAll(".seriesPointsUser")
											.each(
													function() {
														var previous_user = "";
														d3
																.select(this)
																.selectAll(
																		"text")
																.each(
																		function(
																				d,
																				i) {
																			d3
																					.select(
																							this)
																					.text(
																							function(
																									d1,
																									i1) {
																								if (d1.user_name == previous_user) {
																									return "";
																								} else {
																									previous_user = d.user_name;
																									return d1.user_name;
																								}
																							});
																		});
													});

								}

							}

							// show topics
							if (selectionStream === 3) {
								// clear topics
								d3
										.selectAll(".seriesPointsUser")
										.each(
												function() {
													d3
															.select(this)
															.selectAll("text")
															.each(
																	function(d,
																			i) {
																		d3
																				.select(
																						this)
																				.text(
																						function(
																								d1,
																								i1) {
																							return "";
																						})
																	});
												});

								var rumour = d3.selectAll(".seriesPointsUser")
										.filter("." + currentRumour);
								rumour.selectAll("text").each(function(d, i) {
									d3.select(this).text(function(d1, i1) {
										return d1.topic;
									})
								});
							}
							sankey = true;
							removePopovers();
							var threshold = $(".tthreshold").val();
							getSankeyGraph(currentRumourName, threshold);
							d3.event.stopPropagation();
						}).on(
						"mousemove",
						function(d, i) {
							if (!currentRumour) {
								d3.select(this).classed("hover", true).attr(
										"stroke", brewer(8)).attr(
										"stroke-width", "1px");
							}
						}).on(
						"mouseout",
						function(d, i) {
							if (!currentRumourName) {
								svg.selectAll(".streamPath").transition()
										.duration(250).attr("opacity", "1");

								d3.select(this).classed("hover", false).attr(
										"stroke-width", "1px");

							}
						});

		var selectionUser = svg.selectAll(".seriesUser").data(userArr).enter()
				.append("g").attr("class", function(d, i) {
					return "seriesUser " + d.name;
				});

		selectionUser.append("path").attr("class", "userStreamPath").attr("d",
				function(d) {
					return area(d.values);
				}).attr(
				"opacity",
				function(d) {
					if (currentRumourName != undefined) {
						if (currentRumourName.toLowerCase().indexOf(
								d.name.toLowerCase()) >= 0) {
							return 1;
						} else {
							return 0.1;
						}
					}
				}).style("fill", function(d, i) {
			return brewer(d.name.substring(d.name.length - 1, d.name.length));
		}).attr("stroke", "black").on(
				"mouseover",
				function(d, i) {
					if (!currentRumourName) {
						svg.selectAll(".userStreamPath").transition().duration(
								250).attr("opacity", function(d, j) {
							return j != i ? 0.05 : 1;
						});

						svg.selectAll(".streamPath").transition().duration(250)
								.attr("opacity", function(d, j) {
									return 0.05;
								});
					}
					d3.event.stopPropagation();
				}).on(
				"mouseout",
				function(d, i) {
					if (!currentRumour) {
						svg.selectAll(".streamPath").transition().duration(250)
								.attr("opacity", "1");

						svg.selectAll(".userStreamPath").transition().duration(
								250).attr("opacity", "1");

						d3.select(this).classed("hover", false).attr(
								"stroke-width", "1px");
						d3.event.stopPropagation();
					}
				}).on("click", function(d, i) {
			showRumourUserGraph(d.values[0].rumour_text);
			showSecondView = true;
			showCommentCloud(d.values[0].rumour_text);
			d3.event.stopPropagation();
			removePopovers();
		});

		var points = svg.selectAll(".seriesPoints").data(seriesArr).enter()
				.append("g").attr("class", function(d, i) {
					return "seriesPoints " + d.name;
				});
		points.selectAll(".point").data(function(d) {
			return d.values;
		}).enter().append("circle").attr("class", function(d, i) {
			return "point " + "index" + i;
		}).attr("cx", function(d) {
			return x(d.label);
		}).attr("cy", function(d) {
			return y(d.y0 + d.y);
		}).attr(
				"r",
				function(d, i) {
					var sizeScale = d3.scale.linear().domain([ -1, 1 ]).range(
							[ 4, 15 ]);
					if (typeof d.sentiment != 'undefined' && d.sentiment
							&& d.sentiment !== "NaN") {
						return sizeScale(d.sentiment);
					}
					return "4px";
				}).style("fill", function(d) {
			return brewer(d.index);
		}).on("mouseover", function(d) {
			if (!selectedSubmission) {
				showPopover.call(this, d);
			}
		}).on("click", function(d) {
			removePopovers();
			selectedSubmission = d;
			// show comments
			initSubGraph(d);
			showSecondView = true;
			showCommentCloud(currentRumourName, d.redditID);
			showPopover.call(this, d);
			d3.event.stopPropagation();
		}).on("mouseout", function(d) {
			if (!selectedSubmission) {
				removePopovers();
			}
		})

		// show most active users
		var pointsUser = svg.selectAll(".seriesPointsUser").data(userArr)
				.enter().append("g").attr("class", function(d, i) {
					return "seriesPointsUser " + d.name;
				});

		pointsUser.selectAll(".seriesPointsUser").data(function(d) {
			return d.values;
		}).enter().append("text").attr("class", function(d, i) {
			return "users " + d.user_name;
		}).attr('text-anchor', 'bottom')
		// .attr('dominant-baseline', 'central')
		// .style('font-family', 'FontAwesome')
		.attr("dx", function(d) {
			return x(d.label);
		}).attr("dy", function(d) {
			return y(d.y0 + d.y);
		}).on("mouseover", function(d) {
			// showPopover.call(this, d);
		}).on("mouseout", function(d) {
			// removePopovers();
		}).on("click", function(d) {
			selectedUsers.push(d);
			showUserGraph();
			showSecondView = true;
			showCommentCloud(currentRumourName, d.redditID);
			d3.event.stopPropagation();
		}).text(function(d, i) {
			// if (selectionStream == 1) {
			// for (var k = 0; k < selectedRumours.length; k++) {
			// if (selectedRumours[k] === d.rumour_text) {
			// var rdate = new Date(
			// selectedRumourDebunks[k]);
			// var subDate = new Date(
			// d.rumour_created * 1000);
			// if (rdate >= subDate) {
			// return "\uf007";
			// }
			// }
			// }
			// }
			if (currentRumourName) {
				if (d.rumour_text == currentRumourName) {
					if (selectionStream == 2)
						return d.user_name;
					if (selectionStream == 3)
						return d.topic;
				}
			} else {
				if (selectionStream == 2)
					return d.user_name;
				if (selectionStream == 3)
					return d.topic;
			}

		}).style("fill", function(d) {
			if (selectionStream == 1)
				return "brown";
			if (selectionStream == 2 || selectionStream == 3)
				return "#AF5B5C";
		}).on("mouseover", function(d, i) {
			d3.select(this).style("font-weight", "bold");
		}).on("mouseout", function(d, i) {
			d3.select(this).style("font-weight", "normal");
		}).style("font-size", function(d) {
			return "14px";
		});

		drawAxis();
		drawLegend(selectedRumours);
	}

	function zoomed() {

		// var x = d3.scale.ordinal().rangeRoundBands([ 0, width ], 3);

		// svg.select("g.x.axis").attr("transform", "translate(" +
		// d3.event.translate[0]+","+(height)+")")
		// .call(xAxis.scale(x.range([0, width * d3.event.scale],3 *
		// d3.event.scale)));

		var translate = zoom.translate(), scale = zoom.scale();

		var tx = Math.min(0, Math.max(width * (1 - scale), translate[0]));
		var ty = Math.min(0, Math.max(height * (1 - scale), translate[1]));
		zoom.translate([ tx, ty ]);

		svg.select("g.y.axis").call(yAxis);

		try {
			svg.selectAll("path").attr("d", function(d) {
				return area(d.values);
			});
		} catch (ex) {
			console.log(ex);
		}

		var points = svg.selectAll(".point").attr("cx", function(d) {
			return x(d.label);
		}).attr("cy", function(d) {
			return y(d.y0 + d.y);
		}).attr(
				"r",
				function(d, i) {
					var sizeScale = d3.scale.linear().domain([ -1, 1 ]).range(
							[ 4, 15 ]);
					if (typeof d.sentiment != 'undefined' && d.sentiment
							&& d.sentiment !== "NaN") {
						return sizeScale(d.sentiment);
					}
					return "4px";
				});

		// show most active users
		var pointsUser = svg.selectAll(".users").attr("dx", function(d) {
			return x(d.label);
		}).attr("dy", function(d) {
			return y(d.y0 + d.y);
		});
	}

	function dragstarted(d) {
		d3.event.sourceEvent.stopPropagation();
		d3.select(this).classed("dragging", true);
	}

	function dragged(d) {
		d3.select(this).attr("cx", d.x = d3.event.x).attr("cy",
				d.y = d3.event.y);
	}

	function dragended(d) {
		d3.select(this).classed("dragging", false);
	}

	VIZ.clearAll = function() {
		svg.selectAll("*").remove();
	}

	function drawAxis() {
		svg.append("g").attr("class", "x axis").attr("transform",
				"translate(0," + height + ")").call(xAxis);

		svg.append("g").attr("class", "y axis").call(yAxis).append("text")
				.attr("transform", "rotate(-90)").attr("y", 6).attr("dy",
						".71em").style("text-anchor", "end").text("Count");
	}

	function drawLegend(varNames) {

		var legend = svg.selectAll(".legend").data(varNames).enter()
				.append("g").attr("class", "legend").attr("transform",
						function(d, i) {
							return "translate(55," + (140 - i * 20) + ")";
						});

		legend
				.append("rect")
				.attr("x", width - 30)
				.attr("width", 10)
				.attr("class", function(d, i) {
					return "rect" + i;
				})
				.attr("height", 10)
				.style("fill", function(d, i) {
					return brewer(i);
				})
				.attr("stroke", "grey")
				.on(
						"mouseover",
						function(d, i) {
							if (!currentRumourName) {
								showPopoverLegend.call(this, d, i,
										keywordsLegend, debunkLegend,
										linksLegend);
							}
						})
				.on("mouseout", function(d) {
					if (!currentRumourName) {
						removePopovers();
					}
					;
				})
				.on("contextmenu", function(d, i) {
					d3.select("#rumourNetwork").style("opacity", 0.1);
					d3.select("#colors").classed("hidden", false);
					d3.select("#rumourNetwork").classed("noselect", true);
					d3.event.stopPropagation();
					d3.event.preventDefault();
				})
				.on(
						"click",
						function(d, i) {
							removePopovers();
							currentRumour = "rumour" + i;
							currentIndex = i;
							currentRumourName = d;
							showTopicCloud();
							showCommentCloud(d);

							showPopoverLegend.call(this, d, i, keywordsLegend,
									debunkLegend, linksLegend);

							// highlight
							svg.selectAll(".streamPath").transition().duration(
									250).attr("opacity", function(d, j) {
								return j != i ? 0.1 : 1;
							}).attr("stroke-with", function(d, j) {
								return j != i ? 1 : 5;
							}).attr("stroke", function(d, j) {
								return j != i ? 1 : 5;
							})

							svg.selectAll(".userStreamPath").transition()
									.duration(250).attr("opacity",
											function(d, j) {
												return j != i ? 0.1 : 1;
											}).attr("stroke-with",
											function(d, j) {
												return j != i ? 1 : 5;
											}).attr("stroke", function(d, j) {
										return j != i ? 1 : 5;
									})

							// remove other strokes
							d3.selectAll("rect").attr("stroke", 1);

							// add stroke
							d3.select(this).attr("stroke",
									d3.rgb(d.color).darker(20));

							// show users
							if (selectionStream === 2) {
								// clear users
								d3
										.selectAll(".seriesPointsUser")
										.each(
												function() {
													d3
															.select(this)
															.selectAll("text")
															.each(
																	function(d,
																			i) {
																		d3
																				.select(
																						this)
																				.text(
																						function(
																								d1,
																								i1) {
																							return "";
																						})
																	});
												});

								if (currentRumour) {
									var rumour = d3.selectAll(
											".seriesPointsUser").filter(
											"." + currentRumour);
									var previous_user = "";
									rumour
											.selectAll("text")
											.each(
													function(d, i) {
														d3
																.select(this)
																.text(
																		function(
																				d1,
																				i1) {
																			if (d1.user_name == previous_user) {
																				return "";
																			} else {
																				previous_user = d.user_name;
																				return d1.user_name;
																			}
																		});
													});
								} else {
									d3
											.selectAll(".seriesPointsUser")
											.each(
													function() {
														var previous_user = "";
														d3
																.select(this)
																.selectAll(
																		"text")
																.each(
																		function(
																				d,
																				i) {
																			d3
																					.select(
																							this)
																					.text(
																							function(
																									d1,
																									i1) {
																								if (d1.user_name == previous_user) {
																									return "";
																								} else {
																									previous_user = d.user_name;
																									return d1.user_name;
																								}
																							});
																		});
													});

								}

							}

							// show topics
							if (selectionStream === 3) {
								// clear topics
								d3
										.selectAll(".seriesPointsUser")
										.each(
												function() {
													d3
															.select(this)
															.selectAll("text")
															.each(
																	function(d,
																			i) {
																		d3
																				.select(
																						this)
																				.text(
																						function(
																								d1,
																								i1) {
																							return "";
																						})
																	});
												});

								var rumour = d3.selectAll(".seriesPointsUser")
										.filter("." + currentRumour);
								rumour.selectAll("text").each(function(d, i) {
									d3.select(this).text(function(d1, i1) {
										return d1.topic;
									})
								});
							}
							d3.event.stopPropagation();
						}).attr("stroke", function(d, i) {
					var name = selectedRumours[i];
					if (currentRumourName === name)
						return d3.rgb(d.color).darker(20);

				});

		legend.append("text").attr("x", width - 32).attr("y", 6).attr("dy",
				".35em").style("text-anchor", "end").text(function(d, i) {
			var name = selectedRumours[i];
			return name;
		})
	}

	function showPopoverLegend(d, i, keywordsLegend, debunkLegend, linksLegend) {
		jQuery(this).popover(
				{
					title : keywordsLegend[i],
					placement : 'auto top',
					container : 'body',
					trigger : 'manual',
					html : true,
					content : function() {
						return "Rumour: " + keywordsLegend[i] + "<br/>Debunk: "
								+ debunkLegend[i] + "<br/>"
								+ "Information: <a target=\"_blank\" href=\""
								+ linksLegend[i] + "\">" + "link</a><br>";
					}
				});
		jQuery(this).popover('show')
	}

	function removePopovers() {
		jQuery('.popover').each(function() {
			jQuery(this).remove();
		});
	}

	function showPopover(d) {
		jQuery(this)
				.popover(
						{
							title : d.text,
							placement : 'auto top',
							container : 'body',
							trigger : 'manual',
							html : true,
							content : function() {
								var format = d3.time
										.format("%Y-%m-%d %H:%M:%S");
								var date = new Date(d.created * 1000);
								/*
								 * return "Title: " + d.title + "<br/>Comments: "+
								 * d.value + "<br/>" + "Created: " +
								 * format(date) + "<br/>" + "Twitter URL: <a
								 * target=\"_blank\" href=\"https://www." +
								 * d.permalink + "\">" + "link</a><br>";
								 */
								return "Title: "
										+ d.title
										+ "<br/>Comments: "
										+ d.value
										+ "<br/>"
										+ "Created: "
										+ format(date)
										+ "<br/>"
										+ "Reddit URL: <a target=\"_blank\" href=\"https://www.reddit.com/"
										+ d.redditID.replace("t3_", "") + "/"
										+ "\">" + "link</a><br>";
							}
						});
		jQuery(this).popover('show')
	}

	function showColor(d) {
		jQuery(this).popover(
				{
					title : d.name,
					placement : 'auto top',
					container : 'body',
					trigger : 'manual',
					html : true,
					content : function() {
						return "Title: " + d.title + "<br/>Comments: "
								+ d.value + "<br/>Created: "
								+ new Date(d.created * 1000);
					}
				});
		jQuery(this).popover('show')
	}

	var promises = [];
	// load data
	rumourDataList = [];
	jQuery.each(selectedRumours, function(index, value) {
		var rootURL = encodeURI(server
				+ "/RumourFlow/rest/RedditData/search/title/" + value);
		promises.push(jQuery.ajax({
			type : 'GET',
			url : rootURL,
			dataType : "json",
			success : function(data, err) {
				// sorted data;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert('addWine error: ' + textStatus);
			}
		}));
	});

	var p = jQuery.when(1)
	promises.forEach(function(el, index) {
		p = p.then(function() {
			return el;
		}).then(function(data) {
			data.submissions.sort(function(a, b) {
				var x = a.created;
				var y = b.created;
				return ((x < y) ? -1 : ((x > y) ? 1 : 0));
			});
			rumourDataList.push(data);
		});
	});
	p.then(function() {
		// process rumour data
		fData = [];
		fData1 = [];
		for (var i = 0; i < submission_count; i++) {
			var obj = new Object();
			jQuery.each(rumourDataList, function(index, value) {
				var rumour_name = "rumour" + index;
				obj[rumour_name] = value.keyword;
				var rumour_created = "rumour" + index + "_created";
				if (!value.submissions[i]) {
					var l = 0;
					l++;
				}
				console.log(i);
				console.log(rumour_created);
				console.log(value.submissions);
				obj[rumour_created] = value.submissions[i].created;
				var rumour_comment_count = "rumour" + index + "_comment_count";
				obj[rumour_comment_count] = value.submissions[i].commentCount;
				var rumour_rumour_title = "rumour" + index + "_rumour_title";
				obj[rumour_rumour_title] = value.submissions[i].title;
				var rumour_index = "rumour" + index + "_index";
				obj[rumour_index] = index;
				var topic = value.submissions[i].wikis[0];
				var rumour_topic = "rumour" + index + "_topic";
				obj[rumour_topic] = topic;
				var reddit_id = "rumour" + index + "_reddit_id";
				obj[reddit_id] = value.submissions[i].fullName;
				var rumour_sentiment = "rumour" + index + "_sentiment";
				obj[rumour_sentiment] = value.sentiments[i];
				var permalink = "rumour" + index + "_permalink";
				obj[permalink] = value.submissions[i].permalink;
				obj.quarter = i;
			});
			fData[i] = obj;
		}

		for (var i = 0; i < submission_count; i++) {
			var obj = new Object();
			jQuery.each(rumourDataList, function(index, value) {
				var rumour_title = "rumour" + index + "_title";
				obj[rumour_title] = value.submissions[i].title;
				var rumour_topic = "rumour" + index + "_topic";
				obj[rumour_topic] = value.submissions[i].wikis[0];
				var rumour_text = "rumour" + index + "_text";
				obj[rumour_text] = value.keyword;

				var rumour_user_count = "rumour" + index + "_user_count";
				var uniqueNames = [];
				$.each(value.submissions[i].users, function(i, el) {
					if ($.inArray(el, uniqueNames) === -1)
						uniqueNames.push(el);
				});
				obj[rumour_user_count] = uniqueNames.length;

				var rumour_created = "rumour" + index + "_created";
				obj[rumour_created] = value.submissions[i].created;

				obj.quarter = i;
				fData1[i] = obj;
			});
		}

		// get users
		users = [];
		for (var i = 0; i < submission_count; i++) {
			jQuery.each(rumourDataList, function(index, value) {
				var userMap = new Object();
				var obj = fData1[i];

				for (var j = 0; j < value.submissions[i].users.length; j++) {
					var key = value.submissions[i].users[j];
					if (key === "[deleted]") {
						continue;
					}
					if (key in userMap) {
						userMap[key] = userMap[key] + 1;
					} else {
						userMap[key] = 1;
					}
				}

				// sort users
				userMap = sortMap(userMap);

				var rumour_user_value = "rumour" + index + "_user_value";
				obj[rumour_user_value] = userMap[0].value;
				var rumour_user_name = "rumour" + index + "_user_name";
				obj[rumour_user_name] = userMap[0].name;

				obj.quarter = i;
				users[i] = obj;
				fData1[i] = obj;

			});
		}

		// update rumourList and selected rumours

		VIZ.stackChart(fData, fData1, users, 'wiggle');
		jQuery.mobile.loading("hide");
		jQuery("#dsOption").selectmenu("enable");
	});
}

function showRumourUserGraph(rumour) {
	jQuery("#divUser").hide();
	jQuery("#divThres").hide();
	d3.select("#details").html("");
	jQuery("#second").text("User Flow");
	var VIZ = {};
	var margin = {
		top : 10,
		right : 50,
		bottom : 20,
		left : 50
	}, width = WIDTH - margin.left - margin.right, height = HEIGHT - margin.top
			- margin.bottom;

	var x = d3.scale.linear().range([ 0, width ]);

	var y = d3.scale.linear().rangeRound([ height, 0 ]);

	var currentRumour = undefined;
	for (var i = 0; i < rumourDataList.length; i++) {
		if (rumourDataList[i].keyword === rumour) {
			currentRumour = i;
			break;
		}
	}

	var xAxis = d3.svg
			.axis()
			.scale(x)
			.orient("bottom")
			.tickFormat(
					function(d) {
						if (d % 1 === 0 && isNaN(d % 1)) {
							var format = d3.time.format("%m/%y");
							var date = new Date(
									rumourDataList[currentRumour].submissions[d].created * 1000);
							return format(date);
						}
					});

	var yAxis = d3.svg.axis().scale(y).orient("left");

	var color = d3.scale.ordinal().range(
			[ "#001c9c", "#101b4d", "#475003", "#9c8305", "#d3c47c" ]);

	var zoom = d3.behavior.zoom().scaleExtent([ 1, 10 ]).on("zoom", zoomed);

	var drag = d3.behavior.drag().origin(function(d) {
		return d;
	}).on("dragstart", dragstarted).on("drag", dragged)
			.on("dragend", dragended);

	var svg = d3.select("#details").append("svg").attr("id", "rumourUsers")
			.call(zoom).attr("viewBox", "0 0 " + WIDTH + " " + HEIGHT).on(
					"contextmenu", function(d, i) {
						d3.select("#details").style("opacity", 0.4);
						d3.event.preventDefault();
					}).attr("preserveAspectRatio", "xMinYMin meet").append("g")
			.attr("transform",
					"translate(" + margin.left + "," + margin.top + ")");

	VIZ.stackChart = function(data, offset) {
		var stack = d3.layout.stack().values(function(d) {
			return d.values;
		}).x(function(d, i) {
			return x(i);
		}).y(function(d) {
			return d.value;
		});

		var area = d3.svg.area().interpolate("cardinal").x(function(d, i) {

			return x(i);
		}).y0(function(d) {
			return y(d.y0);
		}).y1(function(d) {
			return y(d.y0 + d.y);
		});

		var labelVar = 'quarter';
		var varNames = d3.keys(data[0]).filter(function(key, value) {
			return key !== labelVar;
		});

		var seriesArr = [], series = {};
		varNames.forEach(function(name) {
			series[name] = {
				name : name,
				values : []
			};
			seriesArr.push(series[name]);
		});

		data.forEach(function(d) {
			varNames.map(function(name) {
				series[name].values.push({
					name : name,
					value : d[name]
				});
			});
		});

		x.domain(d3.extent(data.map(function(d) {
			return d.quarter;
		})));

		stack.offset(offset)
		stack(seriesArr);

		y.domain([ 0, d3.max(seriesArr, function(c) {
			return d3.max(c.values, function(d) {
				return d.y0 + d.y;
			});
		}) ]);

		zoom.y(y);

		var selection = svg.selectAll(".series").data(seriesArr).enter()
				.append("g").attr("class", function(d, i) {
					return "series " + d.name;
				});

		selection.append("path").attr("class", "streamPath").attr("d",
				function(d) {
					return area(d.values);
				}).style("fill", function(d, i) {
			return brewer(i);
		}).style("stroke", "grey");

		drawAxis();
		drawLegend(varNames);
	}

	function zoomed() {
		svg.attr("transform", "translate(" + d3.event.translate + ")scale("
				+ d3.event.scale + ")");
	}

	function dragstarted(d) {
		d3.event.sourceEvent.stopPropagation();
		d3.select(this).classed("dragging", true);
	}

	function dragged(d) {
		d3.select(this).attr("cx", d.x = d3.event.x).attr("cy",
				d.y = d3.event.y);
	}

	function dragended(d) {
		d3.select(this).classed("dragging", false);
	}

	VIZ.clearAll = function() {
		svg.selectAll("*").remove();
	}

	function drawAxis() {
		svg.append("g").attr("class", "x axis").attr("transform",
				"translate(0," + height + ")").call(xAxis);

		svg.append("g").attr("class", "y axis").call(yAxis).append("text")
				.attr("transform", "rotate(-90)").attr("y", 6).attr("dy",
						".71em").style("text-anchor", "end").text("User Count");
	}

	function drawLegend(varNames) {

		var legend = svg.selectAll(".legend").data(varNames).enter()
				.append("g").attr("class", "legend").attr("transform",
						function(d, i) {
							return "translate(55," + (80 - i * 20) + ")";
						});

		legend.append("rect").attr("x", width - 30).attr("width", 10).attr(
				"height", 10).style("fill", function(d, i) {
			return brewer(i);
		}).style("stroke", "grey").on("contextmenu", function(d, i) {
			d3.select("#rumourNetwork").style("opacity", 0.1);
			d3.select("#colors").classed("hidden", false);
			d3.select("#rumourNetwork").classed("noselect", true);
			d3.event.stopPropagation();
			d3.event.preventDefault();
		}).on("click", function(d, i) {
			d3.event.stopPropagation();
		}).style("stroke", function(d, i) {
		});

		legend.append("text").attr("x", width - 32).attr("y", 6).attr("dy",
				".35em").style("text-anchor", "end").text(function(d, i) {
			return varNames[i];
		});
	}

	function removePopovers() {
		jQuery('.popover').each(function() {
			jQuery(this).remove();
		});
	}

	function showPopover(d) {
		jQuery(this).popover(
				{
					title : d.text,
					placement : 'auto top',
					container : 'body',
					trigger : 'manual',
					html : true,
					content : function() {
						return "Title: " + d.title + "<br/>Comments: "
								+ d.value + "<br/>Created: "
								+ new Date(d.created * 1000);
					}
				});
		jQuery(this).popover('show')
	}

	function showColor(d) {
		jQuery(this).popover(
				{
					title : d.name,
					placement : 'auto top',
					container : 'body',
					trigger : 'manual',
					html : true,
					content : function() {
						return "Title: " + d.title + "<br/>Comments: "
								+ d.value + "<br/>Created: "
								+ new Date(d.created * 1000);
					}
				});
		jQuery(this).popover('show')
	}

	var promises = [];
	var rumourUser;
	// load data
	var rootURL = encodeURI(server
			+ "/RumourFlow/rest/RedditData/search/users/" + rumour);
	promises.push(jQuery.ajax({
		type : 'GET',
		url : rootURL,
		dataType : "json",
		success : function(data, err) {
			rumourUser = data;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert('addWine error: ' + textStatus);
		}
	}));

	jQuery.when.apply(jQuery, promises).then(function() {
		// process rumour data
		var fData = [];
		for (var i = 0; i < submission_count; i++) {
			var obj = new Object();
			obj.spreader = rumourUser.spreaders[i].length * 2;
			obj.ignorant = rumourUser.ignorants[i].length;
			obj.stifler = rumourUser.stiflers[i].length * 3;
			obj.quarter = i;
			fData[i] = obj;
		}

		VIZ.stackChart(fData, 'wiggle');
	}, function() {
		// error occurred
	});
}

function showUserGraph() {
	jQuery("#second").text("User Activity");
	d3.select("#details").html("");
	d3.select("#divUserCount").classed("hidden", true);
	d3.select("#divThres").classed("hidden", true);

	$("#btnReset").prop('value', 'Reset')
	var VIZ = {};
	var margin = {
		top : 10,
		right : 50,
		bottom : 20,
		left : 50
	}, width = WIDTH - margin.left - margin.right, height = HEIGHT - margin.top
			- margin.bottom;

	var x = d3.scale.linear().range([ 0, width ]);

	var y = d3.scale.linear().rangeRound([ height, 0 ]);

	var xAxis = d3.svg.axis().scale(x).orient("bottom");

	var yAxis = d3.svg.axis().scale(y).orient("left");

	var color = d3.scale.ordinal().range(
			[ "#001c9c", "#101b4d", "#475003", "#9c8305", "#d3c47c" ]);
	color.domain(selectedUsers);

	var svg = d3.select("#details").append("svg").attr("id", "usersvg").attr(
			"viewBox", "0 0 " + WIDTH + " " + HEIGHT).attr(
			"preserveAspectRatio", "xMinYMin meet").append("g").attr(
			"transform", "translate(" + margin.left + "," + margin.top + ")");

	VIZ.lineChart = function(calData) {
		var line = d3.svg.line().interpolate("cardinal").x(function(d) {
			return x(d.label);
		}).y(function(d) {
			return y(d.value);
		});

		var labelVar = 'quarter';
		var seriesData;
		var data = [];
		selectedUsers.forEach(function(user) {

			seriesData = selectedRumours.map(function(name, index) {
				return {
					name : name,
					values : calData.map(function(d1, i1) {
						var userName;
						if (user.user_name) {
							userName = user.user_name;
						} else {
							userName = user.id;
						}
						return {
							name : name,
							label : d1[labelVar],
							value : +d1["rumour" + index + "_" + userName
									+ "_value"],
							title : d1["rumour" + index + "_" + userName
									+ "_title"],
							user : userName
						};
					})
				};
			});

			data.push(seriesData);
		});
		var max = d3.max(data, function(dat) {
			return d3.max(dat, function(c) {
				return d3.max(c.values, function(d) {
					return d.value;
				});
			})
		})

		var min = d3.min(data, function(dat) {
			return d3.min(dat, function(c) {
				return d3.min(c.values, function(d) {
					return d.value;
				});
			})
		})

		x.domain(d3.extent(calData.map(function(d) {
			return d.quarter;
		})));

		y.domain([ min, max ]);

		svg.append("g").attr("class", "x axis").attr("transform",
				"translate(0," + height + ")").call(xAxis);

		svg.append("g").attr("class", "y axis").call(yAxis).append("text")
				.attr("transform", "rotate(-90)").attr("y", 6).attr("dy",
						".71em").style("text-anchor", "end").text(
						"Comment Count");

		data
				.forEach(function(series, i) {
					var series = svg.selectAll(".series" + i).data(series)
							.enter().append("g").attr("class", "series" + i);

					series.append("path").attr("class", "line").attr("d",
							function(d) {
								return line(d.values);
							}).style("stroke", function(d, i) {
						return brewer(i);
					}).style("stroke-width", "3px").style("fill", "none")

					series
							.selectAll(".linePoint")
							.data(function(d) {
								return d.values;
							})
							.enter()
							.append("circle")
							.attr("class", "linePoint")
							.attr("cx", function(d) {
								return x(d.label);
							})
							.attr("cy", function(d) {
								return y(d.value);
							})
							.attr("r", "5px")
							.style("fill", function(d, i) {
								return color(d.user);
							})
							.attr("stroke", "grey")
							.style("stroke-width", "1px")
							.on("mouseover", function(d) {
								showPopover.call(this, d);
							})
							.on("mouseout", function(d) {
								removePopovers();
							})
							.on(
									"click",
									function(d, i) {
										// test
										var keyword = "";
										if (currentRumourName) {
											keyword = currentRumourName;
										} else {
											selectedRumours.forEach(function(
													value, index) {
												keyword += value + ",";
											});
										}

										var rootURL = encodeURI(server
												+ "/RumourFlow/rest/RedditData/get/users/comments/"
												+ keyword + "/" + d.user + "/"
												+ d.title);
										jQuery.ajax({
											type : 'GET',
											url : rootURL,
											dataType : "json",
											success : function(data, err) {
												// sorted data;
												showComments.call(this, data,
														d.user);
											},
											error : function(jqXHR, textStatus,
													errorThrown) {
												alert('addWine error: '
														+ textStatus);
											}
										});
									})

				});

		drawAxis();
		drawLegend(selectedRumours);
	}

	VIZ.clearAll = function() {
		svg.selectAll("*").remove()
	}

	function drawAxis() {
		svg.append("g").attr("class", "x axis").attr("transform",
				"translate(0," + height + ")").call(xAxis);

		svg.append("g").attr("class", "y axis").call(yAxis).append("text")
				.attr("transform", "rotate(-90)").attr("y", 6).attr("dy",
						".71em").style("text-anchor", "end").text("Count");
	}

	function drawLegend(varNames) {
		var legend = svg.selectAll(".legend").data(varNames).enter()
				.append("g").attr("class", "legend").attr("transform",
						function(d, i) {
							return "translate(55," + (140 - i * 20) + ")";
						});

		legend.append("rect").attr("x", width - 30).attr("width", 10).attr(
				"height", 10).style("fill", function(d, i) {
			return brewer(i);
		}).attr("stroke", "grey");

		legend.append("text").attr("x", width - 32).attr("y", 6).attr("dy",
				".35em").style("text-anchor", "end").text(function(d) {
			return d;
		});
	}

	function removePopovers() {
		jQuery('.popover').each(function() {
			jQuery(this).remove();
		});
	}

	function showPopover(d) {
		jQuery(this).popover(
				{
					title : d.name,
					placement : 'auto top',
					container : 'body',
					trigger : 'manual',
					html : true,
					content : function() {
						return "Title: "
								+ d.title
								+ "<br/>Comments: "
								+ d3.format(",")(
										d.value ? d.value : d.y1 - d.y0)
								+ "<br/>User: " + d.user;
					}
				});
		jQuery(this).popover('show')
	}

	function showComments(d, user) {
		jQuery(this).popover({
			title : user + " - " + "Comments",
			placement : 'auto top',
			container : 'body',
			trigger : 'manual',
			html : true,
			content : function() {
				var str = "";
				for (var i = 0; i < d.length; i++) {
					str += (i + 1) + "." + d[i] + "<br>-----------------<br>";
				}
				return str;
			}
		});
		jQuery(this).popover('show');
	}

	var promises = [];
	// load data
	jQuery.each(selectedRumours, function(index, value) {
		var rootURL = encodeURI(server
				+ "/RumourFlow/rest/RedditData/search/title/" + value);
		promises.push(jQuery.ajax({
			type : 'GET',
			url : rootURL,
			dataType : "json",
			success : function(data, err) {
				// sorted data;
				data.submissions.sort(function(a, b) {
					var x = a.created;
					var y = b.created;
					return ((x < y) ? -1 : ((x > y) ? 1 : 0));
				});
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert('addWine error: ' + textStatus);
			}
		}));
	});

	jQuery.when
			.apply(jQuery, promises)
			.then(
					function() {
						// get users
						var fData = [];

						for (var i = 0; i < submission_count; i++) {
							var obj = new Object();
							selectedUsers
									.forEach(function(user) {
										rumourDataList
												.forEach(function(value, index) {
													// count comments in this
													// submission
													var count = 0;
													var name;
													if (user.user_name) {
														name = "rumour"
																+ index
																+ "_"
																+ user.user_name;
													} else {
														name = "rumour" + index
																+ "_" + user.id;
													}

													for (var k = 0; k < value.submissions[i].users.length; k++) {
														if (value.submissions[i].users[k]
																.toLowerCase() === user.user_name) {
															count = count + 1;
														}
														if (value.submissions[i].users[k]
																.toLowerCase() === user.id) {
															count = count + 1;
														}
													}
													obj[name + "_value"] = count;
													obj[name + "_title"] = value.submissions[i].title;

													obj.quarter = i;
												});
									});
							fData[i] = obj;
						}

						VIZ.lineChart(fData);
					}, function() {
						// error occurred
					});
}

function sortData(data) {
	var sorted = [];
	Object.keys(data).sort(function(a, b) {
		return data[a].name < data[b].name ? -1 : 1
	}).forEach(function(key) {
		sorted.push(data[key]);
	});
	return sorted;
}

function sortMap(data) {
	var sorted = [];
	Object.keys(data).sort(function(a, b) {
		return data[a] > data[b] ? -1 : 1
	}).forEach(function(key) {
		var obj = new Object();
		obj.value = data[key];
		obj.name = key;
		sorted.push(obj);
	});
	return sorted;
}

function getGTMScore(data, node1, node2) {
	for (var i = 0; i < data.links.length; i++) {
		if (data.links[i].source == node1.id
				&& data.links[i].target == node2.id)
			return data.links[i].weight;
	}
	return 0;
}