window.addEventListener("DOMContentLoaded", function () {
	// Initialize Firebase
	firebase.initializeApp({
		apiKey: "AIzaSyC-VgGf9pRoOpVcTA3eIrump7tKOJH1_Ao",
		authDomain: "rhythm-tap-ride.firebaseapp.com",
		databaseURL: "https://rhythm-tap-ride.firebaseio.com",
		projectId: "rhythm-tap-ride",
		storageBucket: "rhythm-tap-ride.appspot.com",
		messagingSenderId: "568561761665"
	});
});



const RTR = (function () {
	const template = (function () {
		let body = DOM("Body");
			body.innerHTML = DOM.xhr({ url: "Template.html", doesSync: false }).response;
			
		return body;
	})();

	const audioPlayer = {}; Object.defineProperty(audioPlayer, "ctx", { value: new (AudioContext || webkitAutioContext), enumerable: true }); Object.defineProperties(audioPlayer, {
		bgm: {
			value: new Audio()
		},

		se: {
			value: Object.create(Object.prototype, {
				tap: {
					value: function () {
						let sePlayer = audioPlayer.ctx.createBufferSource();
							sePlayer.connect(audioPlayer.ctx.destination);

						DOM.xhr({
							type: "GET",
							url: "assets/sounds/Tone_Tap.wav",
							doesSync: true,
							resType: "arraybuffer",

							onLoad: function (event) {
								audioPlayer.ctx.decodeAudioData(event.target.response, function (buf) {
									sePlayer.buffer = buf;
								});
							}
						});

						sePlayer.start(0);
					},

					enumerable: true
				},

				miss: {
					value: new Audio(),
					enumerable: true
				}
			}),

			enumerable: true
		}
	});



	const RTR = {}; Object.defineProperties(RTR, {
		Base: {
			value: document.registerElement("RTR-Base", {
				prototype: Object.create(HTMLDivElement.prototype, {
					createdCallback: { value () {} },
					attachedCallback: { value () {} },
					detachedCallback: { value () {} },
					attributeChangedCallback: { value (attr, oldValue, newValue) {} }
				})
			}),

			enumerable: true
		},

		Score: {
			value: (function () {
				const Score = document.registerElement("RTR-Score", {
					prototype: Object.create(HTMLDivElement.prototype, {
						initializeElement: {
							value () {
								for (let i = 0; i < this.attributes.length; i++) {
									this.attributeChangedCallback(this.attributes[i].name, "", this.attributes[i].value);
								}
							}
						},



						createdCallback: {
							value () {
								let base = this.createShadowRoot();
									base.appendChild(document.importNode(template.querySelector("Template#RTR-Score").content, true));

								this.initializeElement();
							}
						},

						attachedCallback: { value () { this.initializeElement() } },
						detachedCallback: { value () { this.initializeElement() } },

						attributeChangedCallback: {
							value (attr, oldValue, newValue) {
								switch (attr.toLowerCase()) {
									case "value":
										this.value = parseInt(newValue);
										break;
								}
							}
						},


						
						__value__: { value: 0, configurable: true, writable: true },

						value: {
							/** @returns {Number} */
							get () { return this.__value__ },

							/** @param {Number} val */
							set (val) {
								this.__value__ = val; this.setAttribute("value", val);
								
								this.textContent = val;
								this.shadowRoot.querySelector("RTR-Score-Scorebar").value = val;
							}
						}
					})
				}); Object.defineProperties(Score, {
					Scorebar: {
						value: document.registerElement("RTR-Score-Scorebar", {
							prototype: Object.create(HTMLDivElement.prototype, {
								initializeElement: {
									value () {
										for (let i = 0; i < this.attributes.length; i++) {
											this.attributeChangedCallback(this.attributes[i].name, "", this.attributes[i].value);
										}
									}
								},



								createdCallback: {
									value () {
										this.initializeElement();
									}
								},

								attachedCallback: { value () { this.initializeElement() } },
								detachedCallback: { value () { this.initializeElement() } },

								attributeChangedCallback: {
									value (attr, oldValue, newValue) {
										switch (attr.toLowerCase()) {
											case "value":
												this.value = parseInt(newValue);
												break;
										}
									}
								},

								__value__: { value: 0, configurable: true, writable: true },

								value: {
									/** @returns {Number} */
									get () { return this.__value__ },

									/** @param {Number} val */
									set (val) {
										this.__value__ = val; this.setAttribute("value", val);

										this.getRootNode().querySelector('Style[UUID="ScorebarStyle"]').textContent = (function () {
											let style = new Style({
												"RTR-Score-Scorebar:Before": {
													"Width": ((val / 1000000) > 1 ? 1 : val / 1000000) * 100 + "%"
												}
											}); style.setAttribute("UUID", "ScorebarStyle");

											return style.textContent.replace(/\r\n/g, "").replace(/\t/g, "");
										})();
									}
								}
							})
						}),

						enumerable: true
					}
				});

				return Score;
			})(),

			enumerable: true
		},

		Playzone: {
			value: document.registerElement("RTR-Playzone", {
				prototype: Object.create(HTMLDivElement.prototype, {
					createdCallback: {
						value () {
							for (let i = 0; i < this.children.length; i++) {
								this.children[i].style.transform = ["Rotate(", 90 - 22.5 * i, "deg)"].join("");
							}
						}
					}
				})
			}),

			enumerable: true
		},

		ToneStream: {
			value: (function () {
				const ToneStream = document.registerElement("RTR-ToneStream", {
					prototype: Object.create(HTMLDivElement.prototype, {
						createdCallback: {
							value () {
								let base = this.createShadowRoot();
									base.appendChild(document.importNode(template.querySelector("Template#RTR-ToneStream").content, true));
							}
						},

						attachedCallback: { value () {} },
						detachedCallback: { value () {} },
						attributeChangedCallback: { value (attr, oldValue, newValue) {} },



						addTone: {
							value (toneObj) {
								this.appendChild(toneObj || new RTR.Tone());
							}
						}
					})
				}); Object.defineProperties(ToneStream, {
					EndPoint: {
						value: document.registerElement("RTR-ToneStream-EndPoint", {
							prototype: Object.create(HTMLDivElement.prototype, {
								createdCallback: {
									value () {
										["touchstart", "mousedown"].forEach((function (elem, index, parent) {
											this.addEventListener(elem, function (event) {
												let closedTone = this.parentNode.querySelector("Content").getDistributedNodes()[0];

												if (closedTone) {
													let selfBoundary = [this.clientTop, this.clientWidth],
														closedToneBoundary = [closedTone.clientTop, closedTone.clientWidth];

													if (selfBoundary) {

													}
												}

												audioPlayer.se.tap();
												document.querySelector("RTR-Score").value += Math.round(Math.random() * 1000 + 1);
											});
										}).bind(this));
									}
								},

								attachedCallback: { value () {} },
								detachedCallback: { value () {} },
								attributeChangedCallback: { value (attr, oldValue, newValue) {} }
							})
						}),

						enumerable: true
					}
				});

				return ToneStream;
			})(),

			enumerable: true
		},

		Tone: {
			value: document.registerElement("RTR-Tone", {
				prototype: Object.create(HTMLImageElement.prototype, {
					initializeElement: {
						value () {
							for (let i = 0; i < this.attributes.length; i++) {
								this.attributeChangedCallback(this.attributes[i].name, "", this.attributes[i].value);
							}
						}
					},



					createdCallback: {
						value () {
							this.initializeElement();

							this.addEventListener("transitionend", function (event) {
								if (event.propertyName == "top") this.streaming = false;
							});
						}
					},

					attachedCallback: {
						value () {
							this.initializeElement();

							setTimeout((function () {
								this.streaming = true;
							}).bind(this));
						}
					},

					detachedCallback: { value () { this.initializeElement() } },

					attributeChangedCallback: {
						value (attr, oldValue, newValue) {
							switch (attr.toLowerCase()) {
								case "src":
									this.src = newValue;
									break;

								case "streaming":
									this.streaming = (newValue === "" ? true : false);
									break;
							}
						}
					},



					__src__: { value: "", configurable: true, writable: true },
					__streaming__: { value: null, configurable: true, writable: true },

					src: {
						/** @returns {String} */
						get () { return this.__src__ },

						/** @param {String} val */
						set (val) {
							this.__src__ = val; this.setAttribute("src", val);

							this.style.backgroundImage = ["URL(", val, ")"].join('"');
						}
					},

					streaming: {
						/** @returns {Boolean} */
						get () { return this.__streaming__ },

						/** @param {Boolean} val */
						set (val) {
							this.__streaming__ = val;
							val ? this.setAttribute("Streaming", "") : this.removeAttribute("Streaming");

							if (!val) this.remove();
						}
					}
				})
			}),

			enumerable: true
		}
	});

	return RTR;
})();