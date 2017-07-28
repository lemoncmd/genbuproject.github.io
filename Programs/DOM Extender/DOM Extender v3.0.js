/*/
 *#######################################################################
 *DOM Extender v3.0
 *Copyright (C) 2016-2020 Genbu Project & Genbu Hase All Rights Reversed.
 *#######################################################################
/*/
(function () {
	Object.defineProperties(Object.prototype, {
		getClassName: {
			/**
			 * @returns {String}
			 */
			value () {
				return Object.prototype.toString.call(this).slice(8, -1);
			}
		},

		isStrictObject: {
			/**
			 * @param {object} obj
			 */
			value (obj) {
				if (obj !== undefined) {
					return (obj.getClassName() !== "String" && obj.getClassName() !== "Number" && obj instanceof Object && !Array.isArray(obj));
				} else {
					return (this.getClassName() !== "String" && this.getClassName() !== "Number" && this instanceof Object && !Array.isArray(this));
				}
			}
		},

		isStrictArray: {
			/**
			 * @param {object} obj
			 */
			value (obj) {
				if (obj !== undefined) {
					return (obj.getClassName() !== "String" && obj.getClassName() !== "Number" && obj instanceof Array && Array.isArray(obj));
				} else {
					return (this.getClassName() !== "String" && this.getClassName() !== "Number" && this instanceof Array && Array.isArray(this));
				}
			}
		},

		connect: {
			/**
			 * @param {String} valueSeparator
			 * @param {String} paramSeparator
			 */
			value (valueSeparator, paramSeparator) {
				valueSeparator = valueSeparator || "=";
				paramSeparator = paramSeparator || "&";

				let result = [];

				for (let i = 0; i < Object.entries(this).length; i++) {
					result.push(Object.entries(this)[i].join(valueSeparator));
				}
				
				return result.join(paramSeparator);
			}
		},

		toQueryString: {
			/**
			 * @param {object} obj
			 */
			value (obj) {
				return "?" + Object.prototype.connect.call(obj || this, "=", "&");
			}
		}
	});

	Object.defineProperties(String.prototype, {
		removeOverlay: {
			/**
			 * @returns {String}
			 */
			value () {
				let result = this.split("");
					result = result.filter(function (elem, index, parent) {
						return parent.indexOf(elem) == index;
					});

				return result.join("");
			}
		},

		replaces: {
			/**
			 * @param {Array<String>} replaceStrs
			 */
			value (replaceStrs) {
				let res = this;
				
				for (let i = 0; i < replaceStrs.length; i++) {
					res = res.replace(replaceStrs[i][0], replaceStrs[i][1]);
				}
				
				return res;
			}
		}
	});

	Object.defineProperties(Window.prototype, {
		importScript: {
			/**
			 * @param {String} [url=""]
			 * @param {function (Event)} [onLoad=function (event) {}]
			 */
			value (url, onLoad) {
				url = url || "",
				onLoad = onLoad || function (event) {};

				if (!(function () {
					let scripts = document.getElementsByTagName("script");
					
					for (let i = 0; i < scripts.length; i++) {
						if (scripts[i].src == url) return true;
					}
				})()) {
					let elem = document.createElement("script");
						elem.src = url;

						elem.addEventListener("load", function (event) {
							onLoad(event);
						});

					document.head.appendChild(elem);
				}
			}
		},

		btoaAsUTF8: {
			/**
			 * @param {String} [str=""]
			 */
			value (str) {
				return btoa(unescape(encodeURIComponent(str || "")));
			}
		},

		atobAsUTF8: {
			/**
			 * @param {String} [base64Str=""]
			 */
			value (base64Str) {
				return decodeURIComponent(escape(atob(base64Str || "")));
			}
		},

		urlSafe: {
			/**
			 * @param {String} [url=""]
			 */
			value (url) {
				return (url || "").replace(/\+/g, '-').replace(/\//g, '_');
			}
		},


		Script: {
			value: (function () {
				/**
				 * @param {String} url
				 * @param {object} [option={}]
				 * @returns {HTMLScriptElement}
				 */
				function Script (url, option) {
					option = option || {};

					let elem = document.createElement("script");
						!url || (elem.src = url);
						elem.async = option.async || false;
						elem.defer = option.defer || false;
						
					return elem;
				};

				return Script;
			})()
		},

		Style: {
			value: (function () {
				/**
				 * @param {object} data
				 * @returns {HTMLStyleElement}
				 */
				function Style (data) {
					data = data || {};

					let elem = document.createElement("style");
						elem.textContent = (function () {
							let mem = [];

							(function Looper (currentData, currentLevel) {
								for (let elemName in currentData) {
									if (currentData[elemName].isStrictObject()) {
										mem.push("\t".repeat(currentLevel) + elemName + " {");
										Looper(currentData[elemName], currentLevel + 1);
										mem.push("\t".repeat(currentLevel) + "}");
										mem.push("\t".repeat(currentLevel) + "");
									} else {
										mem.push("\t".repeat(currentLevel) + elemName + ": " + currentData[elemName] + ";");
									}
								}
							})(data, 1);

							return mem.join("\r\n");
						})();

					return elem;
				};

				return Style;
			})()
		},

		InlineStyle: {
			value: (function () {
				/**
				 * @param {object} data
				 */
				function InlineStyle (data) {
					let mem = [];
					
					for (let styleName in data) {
						mem.push(styleName + ": " + data[styleName] + ";");
					}
					
					return mem.join(" ");
				};

				return InlineStyle;
			})()
		},

		Canvas: {
			value: (function () {
				/**
				 * @param {Number} [width=0]
				 * @param {Number} [height=0]
				 */
				function Canvas (width, height) {
					let Elem = document.createElement("canvas");
						Elem.width = width || 0;
						Elem.height = height || 0;

					return Elem;
				};

				return Canvas;
			})()
		},

		Svg: {
			value: (function () {
				/**
				 * @param {Number} [width=0]
				 * @param {Number} [height=0]
				 */
				function Svg (width, height) {
					let elem = document.createElementNS("http://www.w3.org/2000/svg", "svg");
						elem.setAttribute("version", "1.1");
						elem.setAttribute("xmlns", "http://www.w3.org/2000/svg");
						
						elem.setAttribute("width", width || 0);
						elem.setAttribute("height", height || 0);
						elem.setAttribute("viewBox", "0 0 " + (width || 0) + " " + (height || 0));
						
					return elem;
				}; Object.defineProperties(Svg, {
					Rect: {
						value: (function () {
							/**
							 * @param {object} [option={}]
							 * @returns {HTMLElement}
							 */
							function Rect (option) {
								option = option || {};

								let elem = document.createElementNSWithParam("http://www.w3.org/2000/svg", "rect", option.params);
									elem.setAttribute("x", option.x || 0);
									elem.setAttribute("y", option.y || 0);
									
									elem.setAttribute("width", option.width || 0);
									elem.setAttribute("height", option.height || 0);
									elem.setAttribute("fill", option.fill || "#000000");
									
								return elem;
							};

							return Rect;
						})()
					},

					Circle: {
						value: (function () {
							/**
							 * @param {object} [option={}]
							 * @returns {HTMLElement}
							 */
							function Circle (option) {
								option = option || {};

								let elem = document.createElementNSWithParam("http://www.w3.org/2000/svg", "circle", option.params);
									elem.setAttribute("cx", option.x || 0);
									elem.setAttribute("cy", option.y || 0);
									elem.setAttribute("r", option.radius || 0);

									elem.setAttribute("fill", option.fill || "#000000");
									
								return elem;
							};

							return Circle;
						})()
					},

					Text: {
						value: (function () {
							/**
							 * @param {object} [option={}]
							 * @returns {HTMLElement}
							 */
							function Text (option) {
								option = option || {};

								let elem = document.createElementNSWithParam("http://www.w3.org/2000/svg", "text", option.params);
									elem.textContent = option.value || "";
									
									elem.setAttribute("x", option.x || 0);
									elem.setAttribute("y", option.y || 0);
									elem.setAttribute("rotate", option.rotate || 0);
									
								return elem;
							};

							return Text;
						})()
					},

					RGB: {
						/**
						 * @param {Number} r
						 * @param {Number} g
						 * @param {Number} b
						 */
						value (r, g, b) {
							return "RGB(" + (r || 0) + ", " + (g || 0) + ", " + (b || 0) + ")";
						}
					},

					RGBA: {
						/**
						 * @param {Number} r
						 * @param {Number} g
						 * @param {Number} b
						 * @param {Number} a
						 */
						value (r, g, b, a) {
							return "RGBA(" + (r || 0) + ", " + (g || 0) + ", " + (b || 0) + ", " + (a || 0) + ")";
						}
					}
				});

				return Svg;
			})()
		}
	});

	Object.defineProperties(Document.prototype, {
		createElementWithParam: {
			/**
			 * @param {String} tagName
			 * @param {object} [option={}]
			 */
			value (tagName, option) {
				option = option || {};

				let elem = document.createElement(tagName);
					!option.attributes || (function () {
						for (let paramName in option.attributes) {
							elem.setAttribute(paramName, option.attributes[paramName]);
						}
					})();
					
					!option.styles || (function () {
						elem.setAttribute("Style", InlineStyle(option.styles));
					})();
					
					!option.events || (function () {
						for (let eventName in option.events) {
							elem.addEventListener(eventName, option.events[eventName]);
						}
					})();

					!option.children || (function () {
						for (let i = 0; i < option.children.length; i++) {
							elem.appendChild(option.children[i]);
						}
					})();
				
				return elem;
			}
		},

		createElementNSWithParam: {
			/**
			 * @param {String} nameSpace
			 * @param {String} tagName
			 * @param {object} [option={}]
			 */
			value (nameSpace, tagName, option) {
				option = option || {};

				let elem = document.createElementNS(nameSpace, tagName);
					!option.attributes || (function () {
						for (let paramName in option.attributes) {
							elem.setAttribute(paramName, option.attributes[paramName]);
						}
					})();
					
					!option.styles || (function () {
						elem.setAttribute("Style", InlineStyle(option.styles));
					})();
					
					!option.events || (function () {
						for (let eventName in option.events) {
							elem.addEventListener(eventName, option.events[eventName]);
						}
					})();

					!option.children || (function () {
						for (let i = 0; i < option.children.length; i++) {
							elem.appendChild(option.children[i]);
						}
					})();
				
				return elem;
			}
		}
	});

	Object.defineProperties(Node.prototype, {
		appendTo: {
			/**
			 * @param {HTMLElement} [parent=document.body]
			 */
			value (parent) {
				(parent || document.body).appendChild(this);
			}
		},

		dismiss: {
			value () {
				this.parentElement.removeChild(this);
			}
		}
	});

	Object.defineProperties(Image.prototype, {
		getImageData: {
			/**
			 * @returns {ImageData}
			 */
			value () {
				this.crossOrigin = this.crossOrigin || "anonymous";

				let cvs = document.createElement("canvas");
					cvs.width = this.naturalWidth;
					cvs.height = this.naturalHeight;
					
				let ctx = cvs.getContext("2d");
					ctx.drawImage(this, 0, 0);
					
				return ctx.getImageData(0, 0, this.naturalWidth, this.naturalHeight);
			}
		},

		toSvg: {
			/**
			 * @returns {SVGSVGElement}
			 */
			value () {
				this.crossOrigin = this.crossOrigin || "anonymous";
				
				let pixels = this.getImageData(),
					elem = new Svg(pixels.width, pixels.height);
					
				for (let y = 0; y < pixels.height; y++) {
					for (let x = 0; x < pixels.width; x++) {
						elem.appendChild(
							new Svg.Rect({
								width: 1,
								height: 1,
								
								x: x,
								x: y,
								fill: Svg.RGBA(pixels.data[(x + y * pixels.width) * 4], pixels.data[(x + y * pixels.width) * 4 + 1], pixels.data[(x + y * pixels.width) * 4 + 2], pixels.data[(x + y * pixels.width) * 4 + 3])
							})
						);
					}
				}
				
				return elem;
			}
		}
	});

	Object.defineProperties(Location.prototype, {
		querySort: {
			value () {
				let querys = {};
				
				for (var i = 0; i < this.search.substr(1).split("&").length; i++) {
					querys[this.search.substr(1).split("&")[i].split("=")[0].toUpperCase()] = this.search.substr(1).split("&")[i].split("=")[1];
				}
				
				return querys;
			}
		},

		getIPs: {
			/**
			 * @param {function (object)} [onLoad=function (res) {}]
			 */
			value (onLoad) {
				onLoad = onLoad || function (res) {};

				let iframe = document.createElement("iframe");
					iframe.style.display = "None";
					
				document.body.appendChild(iframe);
				
				let ip_dups = {};
				
				let RTCPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
				let useWebKit = !!window.webkitRTCPeerConnection;
				
				if (!RTCPeerConnection) {
					let win = iframe.contentWindow;
					
					RTCPeerConnection = win.RTCPeerConnection || win.mozRTCPeerConnection || win.webkitRTCPeerConnection;
					useWebKit = !!win.webkitRTCPeerConnection;
				}
				
				let pc = new RTCPeerConnection({
					iceServers: [{
						urls: "stun:stun.services.mozilla.com"
					}],
					
					optional: [{
						RtpDataChannels: true
					}]
				});
				
				function handleCandidate(candidate) {
					let ip_regex = /([0-9]{1,3}(\.[0-9]{1,3}){3}|[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7})/
					let ip_addr = ip_regex.exec(candidate)[1];
					
					if (ip_dups[ip_addr] === undefined) {
						onLoad({
							type: ip_addr.match(/^(192\.168\.|169\.254\.|10\.|172\.(1[6-9]|2\d|3[01]))/) ? "v4" : ip_addr.match(/^[a-f0-9]{1,4}(:[a-f0-9]{1,4}){7}$/) ? "v6" : "private",
							value: ip_addr
						});
					}
					
					ip_dups[ip_addr] = true;
				}
				
				pc.onicecandidate = function (ice) {
					if (ice.candidate) handleCandidate(ice.candidate.candidate);
				}
				
				pc.createDataChannel("");
				
				pc.createOffer(function (result) {
					pc.setLocalDescription(result, function () {}, function () {});
				}, function () {
					
				});
				
				setTimeout(function () {
					let lines = pc.localDescription.sdp.split('\n');
						lines.forEach(function (line) {
							if (line.indexOf('a=candidate:') === 0) handleCandidate(line);
						});
						
					iframe.parentElement.removeChild(iframe);
				}, 1000);
			}
		}
	});

	Object.defineProperties(Navigator.prototype, {
		isMobile: {
			value () {
				let checker = new MobileDetect(window.navigator.userAgent);

				return (checker.mobile() || checker.phone() || checker.tablet()) ? true : false;
			}
		}
	});



	Object.defineProperties(Math, {
		radicalRoot: {
			/**
			 * @param {Number} base
			 * @param {Number} exponent
			 */
			value (base, exponent) {
				return Math.pow(base, 1 / exponent);
			}
		}
	});

	Object.defineProperties(Math.random, {
		randomInt: {
			value () {
				let result = 0;

				if (arguments.length >= 2) {
					result = Math.round(Math.random() * (arguments[1] - arguments[0]) + arguments[0]);
				} else {
					result = Math.round(Math.random() * arguments[0]);
				}

				return result;
			}
		}
	});
})();



importScript("https://cdnjs.cloudflare.com/ajax/libs/mobile-detect/1.3.5/mobile-detect.min.js");

const DOM = (function () {
	/**
	 * セレクタ($1)に応じてDOM要素を返す
	 * 
	 * ($1) セレクタ
	 * => {:elemName} … elemName要素を生成
	 * => #{:elemName} … IDがelemNameの要素を返す
	 * => .{:elemName} … elemNameクラスの要素を返す
	 * => *{:elemName} … NameがelemNameの要素を返す
	 * => :{:elemName} … elemName要素を返す
	 * => ${:elemName} … elemNameセレクタの1要素を返す
	 * => @{:elemName} … elemNameセレクタの要素を返す
	 * 
	 * @param {String} selectorStr
	 * @param {object} [option={}]
	 */
	const DOM = function (selectorStr, option) {
		selectorStr = selectorStr || "",
		option = option || {};

		let elem = null;

		switch (selectorStr.substr(0, 1)) {
			default:
				try {
					elem = document.createElementWithParam(selectorStr, option);
				} catch (err) {
					throw new SyntaxError("The selector includes invalid characters.");
				}

				break;

			case "#":
				elem = document.getElementById(selectorStr.slice(1));
				break;

			case ".":
				elem = document.getElementsByClassName(selectorStr.slice(1));
				break;

			case "*":
				elem = document.getElementsByName(selectorStr.slice(1));
				break;

			case ":":
				elem = document.getElementsByTagName(selectorStr.slice(1));
				break;

			case "$":
				elem = document.querySelector(selectorStr.slice(1));
				break;

			case "@":
				elem = document.querySelectorAll(selectorStr.slice(1));
				break;
		}

		return elem;
	}; Object.defineProperties(DOM, {
		xhr: {
			/**
			 * @param {object} [option={}]
			 */
			value (option) {
				option = option || {};

				let connector = new XMLHttpRequest();
					!option.resType || (connector.responseType = option.resType);
					
					connector.open(option.type || "GET", option.url || location.href + (option.params ? "?" + (function () {
						let param = [];

						for (let paramName in option.params) {
							param.push(paramName + "=" + option.params[paramName]);
						}

						return param.join("&");
					})() : ""), option.doesSync != undefined ? option.doesSync : true);

					!option.headers || (function () {
						for (let headerName in option.headers) {
							connector.setRequestHeader(headerName, option.headers[headerName]);
						}
					})();

					connector.onload = option.onLoad || function (event) {};
					connector.send(option.data);

				return connector;
			},

			enumerable: true
		},

		jsonp: {
			/**
			 * @param {object} [option={}]
			 */
			value (option) {
				option = option || {};

				let param = [];

				!option.params || (function () {
					for (let paramName in option.params) {
						param.push(paramName + "=" + option.params[paramName]);
					}
				})();

				let elem = document.createElement("script");
					elem.src = (option.url || location.href) + (option.params ? "?" + param.join("&") : "");
					
					elem.onload = function (event) {
						elem.parentElement.removeChild(elem);
					}
					
				document.head.appendChild(elem);
			},

			enumerable: true
		},
		
		rest: {
			value: (function () {
				/**
				 * @param {object} [option={}]
				 */
				function rest (option) {
					option = option || {};

					return this.xhr({
						type: option.type,
						url: option.url,
						doesSync: option.doesSync,

						headers: option.headers,
						params: option.params,
						data: option.data,

						onLoad (event) {
							!option.onLoad || option.onLoad(eval(event.target.response));
						}
					});
				}; Object.defineProperties(rest, {
					calcResources: {
						/**
						 * @param {ProgressEvent} eventObj
						 */
						value (eventObj) {
							/**
							 * @type {Location}
							 */
							let loc = JSON.parse(JSON.stringify(window.location));
								loc.__proto__ = Location.prototype;
								
								loc.href = eventObj.target.responseURL;
								loc.protocol = loc.href.match(/(https|http|file|ftp):\/\/\/?/g)[0];
								loc.pathname = "/" + loc.href.split(/\//g)[loc.href.split(/\//g).length - 1].split("?")[0];
								loc.search = "?" + loc.href.split(/\//g)[loc.href.split(/\//g).length - 1].split("?")[1];
								
								loc.origin = loc.href.replaces([[loc.pathname, ""], [loc.search, ""]]);
								loc.host = loc.href.replaces([[loc.protocol, ""], [loc.pathname, ""], [loc.search, ""]]);
								loc.port = loc.host.split(":")[1] ? loc.host.split(":")[1] : "";
								loc.hostname = loc.host.replace(":" + loc.port, "");
								
							return loc;
						},

						enumerable: true
					}
				});

				return rest;
			})(),

			enumerable: true
		},

		import: {
			/**
			 * @param {String} url
			 * @param {function} [onLoad=function (event)]
			 */
			value (url, onLoad) {
				onLoad = onLoad || function (event) {};

				this.xhr({
					type: "GET",
					url: url,
					doesSync: true,

					onLoad: function (event) {
						if (event.target.response.match("#{using} DOMExtender")) {
							eval(event.target.response)(apiInfo);
							onLoad(event);
						} else {
							throw new EvalError("Load the API for only DOM Extender")
						}
					}
				});
			},

			enumerable: true
		},


		
		/** @type {Number} */
		width: { value: window.innerWidth, configurable: true, writable: true, enumerable: true },

		/** @type {Number} */
		height: { value: window.innerHeight, configurable: true, writable: true, enumerable: true },

		util: {
			value: (function () {
				const util = Object.create(Object.prototype, {
					degToRad: {
						/**
						 * @param {Number} degree
						 */
						value (degree) {
							return degree * Math.PI / 180;
						},

						enumerable: true
					},

					radToDeg: {
						/**
						 * @param {Number} radian
						 */
						value (radian) {
							return radian * 180 / Math.PI;
						},

						enumerable: true
					},

					paramInit: {
						/**
						 * @param {any} obj
						 * @param {any} initValue
						 */
						value (obj, initValue) {
							return (obj != false && !obj) ? initValue : obj;
						},

						enumerable: true
					},

					getCenteredBoundingClientRect: {
						/**
						 * @param {Number} [width=0]
						 * @param {Number} [height=0]
						 * @param {Number} [basisWidth=window.outerWidth]
						 * @param {Number} [basisHeight=window.outerHeight]
						 */
						value (width, height, basisWidth, basisHeight) {
							width = width || 0,
							height = height || 0;

							return Object.create(ClientRect.prototype, {
								width: { value: width },
								height: { value: height },

								left: { value: (window.outerWidth - width) / 2},
								right: { value: (window.outerWidth + width) / 2},
								top: { value: (window.outerHeight - height) / 2},
								bottom: { value: (window.outerHeight + height) / 2}
							});
						}
					}
				}); util[Symbol.toStringTag] = "DOM Utility";

				return util;
			})(),

			enumerable: true
		},



		APIInfo: {
			value: (function () {
				/**
				 * @param {String} apiName
				 * @param {Number} apiVersion
				 */
				function APIInfo (apiName, apiVersion) {
					this.name = apiName || "Untitled API";
					this.version = apiVersion || 1.0;
				}; APIInfo.prototype = Object.create(null, {
					constructor: { value: APIInfo },

					name: { value: "", configurable: true, writable: true, enumerable: true },
					version: { value: 0.0, configurable: true, writable: true, enumerable: true }
				});

				return APIInfo;
			})(),

			enumerable: true
		},

		Watcher: {
			value: (function () {
				let watchers = [];

				/**
				 * @param {object} [option={}]
				 */
				function Watcher (option) {
					option = option || {};

					this.setTarget(option.target || { value: null });
					this.setWatchTick(option.tick || 1);
					this.onGet = option.onGet || function () {};
					this.onChange = option.onChange || function (watcher) {};
				}; Watcher.prototype = Object.create(null, {
					constructor: { value: Watcher },

					watcherID: { value: [0, 0], configurable: true, writable: true, enumerable: true },
					watchTick: { value: [0, 0], configurable: true, writable: true, enumerable: true },
					target: { value: { value: null }, configurable: true, writable: true, enumerable: true },
					oldValue: { value: 0, configurable: true, writable: true, enumerable: true },
					newValue: { value: 0, configurable: true, writable: true, enumerable: true },
					onGet: { value: function () {}, configurable: true, writable: true, enumerable: true },
					onChange: { value: function (watcher) {}, configurable: true, writable: true, enumerable: true },

					setWatchTick: {
						/**
						 * @param {Number} tick
						 */
						value (tick) { this.watchTick = tick }, enumerable: true
					},

					setTarget: {
						/**
						 * @param {object} target
						 */
						value (target) { this.target = target }, enumerable: true
					}
				}); Object.defineProperties(Watcher, {
					addWatcher: {
						/**
						 * @param {Watcher} watcher
						 */
						value (watcher) {
							watcher.watcherID[0] = setInterval(function () {
								watcher.newValue = watcher.target.value;
								
								if (watcher.oldValue !== watcher.newValue) {
									watcher.onChange(watcher);
									watcher.oldValue = watcher.newValue;
								}
							}, watcher.watchTick);
							
							watcher.oldValue = watcher.target.value,
							watcher.newValue = watcher.target.value;
							
							watchers.push(watcher);
							watchers[watchers.length - 1].watcherID[1] = watchers.length - 1;
							
							watcher.watcherID[810] = setInterval(watcher.onGet || function () {}, watcher.watchTick);
							
							return watcher;
						},

						enumerable: true
					},

					removeWatcher: {
						/**
						 * @param {Watcher} watcher
						 */
						value (watcher) {
							clearInterval(watcher.watcherID[0]);
							clearInterval(watcher.watcherID[810]);
							
							watchers.slice(watcher.watcherID[1], 1);
						},

						enumerable: true
					}
				});

				return Watcher;
			})(),

			enumerable: true
		},

		Randomizer: {
			value: (function () {
				/**
				 * @param {Symbol} [usedType=Symbol]
				 */
				function Randomizer (usedType) {
					this.TYPE = Randomizer.TYPE,
					this.CHARMAP = Randomizer.CHARMAP;
					
					this.setType(usedType || this.TYPE.LEVEL3);
				}; Randomizer.prototype = Object.create(null, {
					constructor: { value: Randomizer },
					
					TYPE: { value: null, configurable: true, writable: true, enumerable: true },
					CHARMAP: { value: null, configurable: true, writable: true, enumerable: true },
					currentType: { value: null, configurable: true, writable: true, enumerable: true },

					setType: {
						/**
						 * @param {Symbol} [usedType=Symbol]
						 */
						value (usedType) {
							!usedType || (this.currentType = this.TYPE[Symbol.keyFor(usedType)]);
						},

						enumerable: true
					},

					addRandomizeType: {
						/**
						 * @param {Randomizer.RandomizeType} randomizeType
						 */
						value (randomizeType) {
							if (randomizeType) {
								this.TYPE[randomizeType.name] = randomizeType.type,
								this.CHARMAP[randomizeType.name] = randomizeType.charMap;

								this.currentType = randomizeType.type;
							}
						},

						enumerable: true
					},

					removeRandomizeType: {
						/**
						 * @param {Randomizer.RandomizeType} randomizeType
						 */
						value (randomizeType) {
							if (randomizeType) {
								this.TYPE[randomizeType.name] = undefined,
								this.CHARMAP[randomizeType.name] = undefined;
								
								this.currentType = null;
							}
						},

						enumerable: true
					},

					resetRandomizeType: {
						value () {
							this.TYPE = DOM.Randomize.TYPE,
							this.CHARMAP = DOM.Randomize.CHARMAP;

							this.currentType = null;
						},

						enumerable: true
					},

					generate: {
						/**
						 * @param {Number} [strLength=8]
						 */
						value (strLength) {
							let result = "";

							strLength = strLength || 8;

							for (let i = 0; i < strLength; i++) {
								try {
									result += this.CHARMAP[Symbol.keyFor(this.currentType)][Math.random.randomInt(this.CHARMAP[Symbol.keyFor(this.currentType)].length - 1)];
								} catch (err) {
									throw new TypeError("Do not {:generate} before using {:setType}");
								}
							}

							return result;
						},

						writable: false,
						configurable: false,
						enumerable: false
					}
				}); Object.defineProperties(Randomizer, {
					TYPE: {
						value: (function () {
							const TYPE = Object.create(Object.prototype, {
								LEVEL1: { value: Symbol.for("LEVEL1"), enumerable: true },	//Only Numbers
								LEVEL2: { value: Symbol.for("LEVEL2"), enumerable: true },	//Only Alphabets
								LEVEL3: { value: Symbol.for("LEVEL3"), enumerable: true },	//Numbers & Alphabets
								LEVEL4: { value: Symbol.for("LEVEL4"), enumerable: true },	//Numbers & Alphabets & Some Symbols

								LEVEL101: { value: Symbol.for("LEVEL101"), enumerable: true },	//ひらがな
								LEVEL102: { value: Symbol.for("LEVEL102"), enumerable: true },	//真夏(まなつ)の夜(よる)の淫夢(いんむ)
								LEVEL103: { value: Symbol.for("LEVEL103"), enumerable: true },	//唐澤弁護士(からさわべんごし) & 尊師(そんし)
								LEVEL104: { value: Symbol.for("LEVEL104"), enumerable: true },	//かすてら。じゅーしー & ちんかすてら & 珠照(すてら) & 未定義(みていぎ)さん
								LEVEL105: { value: Symbol.for("LEVEL105"), enumerable: true },	//イサト & 望月(もちづき) & モッチー & もっちー & もちもちゃん
								LEVEL106: { value: Symbol.for("LEVEL106"), enumerable: true },	//魂魄妖夢(こんぱくようむ) & 魂魄妖夢Channel & ValkyrieChannel & Durandal.Project & VC.Project & DCProject & Object & 黐麟(ちりん) & 氏名(しめい)
								LEVEL107: { value: Symbol.for("LEVEL107"), enumerable: true },	//勿論偽名(もちろんぎめい) & 偽名ちゃん(ぎめいちゃん)
								LEVEL108: { value: Symbol.for("LEVEL108"), enumerable: true },	//てぃお
								LEVEL109: { value: Symbol.for("LEVEL109"), enumerable: true },	//Mr.Taka & Takaチャンネル & タカチャンネル
								LEVEL110: { value: Symbol.for("LEVEL110"), enumerable: true }	//ナイキ & Nike(ないき) & にけ & にけにけ(にけみん)
							}); TYPE[Symbol.toStringTag] = "RandomizeType";

							return TYPE;
						})(),

						enumerable: true
					},

					CHARMAP: {
						value: (function () {
							const CHARMAP = Object.create(Object.prototype, {
								LEVEL1: { value: "1234567890".split(""), enumerable: true },
								LEVEL2: { value: "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""), enumerable: true },
								LEVEL3: { value: [""], enumerable: true },
								LEVEL4: { value: "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".split(""), enumerable: true },

								LEVEL101: { value: "あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん".split(""), enumerable: true },
								LEVEL102: { value: ["真夏の夜の淫夢", "まなつのよるのいんむ"].join("").removeOverlay().split(""), enumerable: true },
								LEVEL103: { value: ["唐澤弁護士", "からさわべんごし", "尊師", "そんし"].join("").removeOverlay().split(""), enumerable: true },
								LEVEL104: { value: ["かすてら。じゅーしー", "ちんかすてら", "珠照", "すてら", "未定義さん", "みていぎさん"].join("").removeOverlay().split(""), enumerable: true },
								LEVEL105: { value: ["イサト", "望月", "もちづき", "モッチー", "もっちー", "もちもちゃん"].join("").removeOverlay().split(""), enumerable: true },
								LEVEL106: { value: ["魂魄妖夢", "こんぱくようむ", "魂魄妖夢Channel", "ValkyrieChannel", "Durandal.Project", "VC.Project", "DCProject", "Object", "黐麟", "ちりん", "氏名", "しめい"].join("").removeOverlay().split(""), enumerable: true },
								LEVEL107: { value: ["勿論偽名", "もちろんぎめい", "偽名ちゃん", "ぎめいちゃん"].join("").removeOverlay().split(""), enumerable: true },
								LEVEL108: { value: ["てぃお"].join("").removeOverlay().split(""), enumerable: true },
								LEVEL109: { value: ["Mr.Taka", "Takaチャンネル", "タカチャンネル"].join("").removeOverlay().split(""), enumerable: true },
								LEVEL110: { value: ["ナイキ", "Nike", "ないき", "にけ", "にけにけ", "にけみん"].join("").removeOverlay().split(""), enumerable: true }
							}); CHARMAP[Symbol.toStringTag] = "RandomizeMap";

							CHARMAP.LEVEL3 = CHARMAP.LEVEL1.concat(CHARMAP.LEVEL2),
							CHARMAP.LEVEL4 = CHARMAP.LEVEL4.concat(CHARMAP.LEVEL1.concat(CHARMAP.LEVEL2));

							return CHARMAP;
						})(),

						enumerable: true
					},



					RamdomizeType: {
						value: (function () {
							/**
							 * @param {String} [name="Untitled Type"]
							 * @param {String} [usedChars=""]
							 */
							function RandomizeType (name, usedChars) {
								!name || (this.name = name);
								!usedChars || (this.charMap = usedChars.removeOverlay().split(""));

								this.type = Symbol.for(this.name);
							}; RandomizeType.prototype = Object.create(null, {
								constructor: { value: RandomizeType },

								name: { value: "Untitled Type", configurable: true, writable: true, enumerable: true },
								type: { value: null, configurable: true, writable: true, enumerable: true },
								charMap: { value: ["0"], configurable: true, writable: true, enumerable: true }
							});

							return RandomizeType;
						})(),

						enumerable: true
					}
				});

				return Randomizer;
			})(),

			enumerable: true
		}
	});



	/*DOM.InvalidSelectorError = (function () {
		function InvalidSelectorError (message) {
			this.name = "InvalidSelector";
			this.message = message || "Syntax of selector is invalid";

			Error.captureStackTrace(this);
		}; InvalidSelectorError.prototype = SyntaxError.prototype;

		return InvalidSelectorError;
	})();*/



	/**
	 * @type {DOM.APIInfo}
	 */
	let apiInfo = new DOM.APIInfo("DOM Extender", 3.0);

	window.addEventListener("resize", function (event) {
		DOM.width = window.innerWidth;
		DOM.height = window.innerHeight;
	});

	return DOM;
})();