/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.core.AppUtils', {
	singleton: true,
	alternateClassName: 'AU',

	// baseCSSPrefix: 'eap-',

	requires: [
		'Ext.window.MessageBox'
	],

	ajax: function(options) {
		return Ext.Ajax.request(Ext.applyIf(options || {}, {
			loadMask: true,
			scripts: true,
			failure: function(response, opts) {
				AU.showException(response.responseText);
			}
		}));
	},

	/* DIALOG ---------------------------- */

	alert: function(title, msg, fn, scope) {
		return Ext.Msg.alert(title, msg, fn, scope);
	},

	showDialog: function(config) {
		return Ext.Msg.show(config);
	},
	showWarningDialog: function(config) {
		return this.showDialog(
			Ext.apply({
				title: '警告',
				buttons: Ext.MessageBox.OK,
				icon: Ext.MessageBox.WARNING
			}, config)
		);
	},
	showInfoDialog: function(config) {
		return this.showDialog(
			Ext.apply({
				title: '信息',
				buttons: Ext.MessageBox.OK,
				icon: Ext.MessageBox.INFO
			}, config)
		);
	},
	showConfirmDialog: function(config) {
		return this.showDialog(
			Ext.apply({
				title: '确认',
				buttons: Ext.MessageBox.YESNO,
				icon: Ext.MessageBox.QUESTION
			}, config)
		);
	},
	showErrorDialog: function(config) {
		return this.showDialog(
			Ext.apply({
				title: '错误',
				buttons: Ext.MessageBox.OK,
				icon: Ext.MessageBox.ERROR
			}, config)
		);
	},
	showSuccessDialog: function(config) {
		return this.showDialog(
			Ext.apply({
				title: '成功',
				buttons: Ext.MessageBox.OK,
				icon: Ext.baseCSSPrefix + 'message-box-success'
			}, config)
		);
	},
	showMsg: function(msg) {
		var _t = this;
		if (!_t.msgCt) {
			_t.msgCt = Ext.DomHelper.insertFirst(document.body, {'class': 'global-message'}, true);
			_t.msgTpl = '<div class="msg">{0}</div>';
		}
		
		var msgHtml = Ext.String.format(_t.msgTpl, msg),
			m = Ext.DomHelper.append(_t.msgCt, msgHtml, true);
		m.hide();
		m.slideIn('t').highlight().ghost("t", { delay: 1000, remove: true});
	},

	showException: function(message) {
		var _t = this;

		if (Ext.isString(message)) {
			var msgJson = (/{(.*)}/.exec(message) || '')[0];
			if (msgJson) {
				message = Ext.decode(msgJson);
			}
		}
		
		if (Ext.isObject(message)) {
			var errorCode = message.errorCode;
			if (errorCode == 'AccessDenied') {
				_t.showMsg('访问受限', message.errorMsg || errorCode);
				Ext.defer(function() {
					//App.requestViewportModule();
					alert('TODO');
				}, 1000);
			} 
			else {
				_t.showErrorDialog({
					title: '系统异常',
					msg: (message.errorMsg || errorCode)
				});
			}
		} else {
			_t.showErrorDialog({
				title: '系统异常',
				msg: message//Ext.String.format('Call to {0}.{1} failed with message:<xmp>{2}</xmp>', tx.action, tx.method, e.message)
			});
		}
	},

	showProgress: function(config) {
		return this.showDialog(Ext.applyIf(config || {}, {
			// title: AC.config.waitMsg,
			progress: true,
			closable: false
		}));
	},
	updateProgress: function(currValue, totalValue) {
		if (arguments.length == 0) {
			Ext.MessageBox.hide();
		} else {
			var i = currValue / totalValue;
			Ext.MessageBox.updateProgress(i, '完成 ' + Math.round(100*i)+'%');
		}
	},

	/* PRINT ---------------------------- */
	PRINT_FRAME_ID: '__print_frame__',
	print: function(content) {
		var _t = this, win, doc;
		if (Ext.isIE) {
			win = window.open('', '__print_window__', 'toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
		} else {
			var printFrameDom = (Ext.fly(_t.PRINT_FRAME_ID) || {}).dom;
			if (!printFrameDom) {
				printFrameDom = Ext.DomHelper.append(Ext.getBody().dom, {
					tag: 'iframe',
					id: _t.PRINT_FRAME_ID,
					frameBorder: 0,
					style: 'width:1px;height:1px;position:absolute;right:0;bottom:0;border:none;overflow:hidden;visibility:hidden'
				});
			}
			win = printFrameDom.contentWindow;
		}
		
		doc = win.document;
		doc.open();
		doc.write(content);
		doc.close();
		win.focus();
		win.print();
		win.close();
	},
	
	_printHtmlTpl: Ext.create('Ext.Template', [
		'<!DOCTYPE html>',
		'<html>',
			'<head><title>{title}</title>{head}</head>',
			'<body class=\"app\">{body}</body>',
		'</html>'
	]),
	printHtml: function(title, content, cssPaths) {
		var importCss = [], 
			cssPaths = [AC.config.print.cssPaths].concat(cssPaths || []);
		Ext.each(cssPaths, function(cssPath) {
			importCss.push('@import url("'+ cssPath +'");');
		});
		
		var _t = this,
			html = _t._printHtmlTpl.apply({
				title: title || '打印',
				head: '<style type="text/css">' + 
						importCss.join('') +
					  '</style>',
				body: content
			});
		
		_t.print(html);
	},
	
	printer: function(pageArgs, pageFn) {
		return {
			PAGE_SEPARATOR: '<div class="page-separator"></div>',
			pages: [],
			
			addPage: function(page) {
				var _t = this;
				_t.pages.push(page);
				return _t;
			},
			
			print: function() {
				var _t = this;
				AU.showProgress();
				Ext.each(pageArgs, function(pageArg, index) {
					Ext.callback(pageFn, _t, [pageArg, Ext.bind(_t.addPage, _t)]);
					AU.updateProgress(index + 1, pageArgs.length);
				}, _t);
				AU.updateProgress();
				
				AU.printHtml(_t.pages.join(_t.PAGE_SEPARATOR));
			}
		};
	}

	/* --------------------------------- */
	,onCascadeTreeCheckchange: function(node, checked, opts) {
		var _t = this;
		if (checked) {
			node.bubble(function(parentNode) { // include curr node
				// if (node.getId() != parentNode.getId()) {
					parentNode.set('checked', true);
					// parentNode.fireEvent('checkchange', parentNode, true);
					// console.log(1)
				// }
			});
			node.cascadeBy(function(childNode) { // include curr node
				// if (node.getId() != parentNode.getId()) {
					childNode.set('checked', true);
					// childNode.fireEvent('checkchange', childNode, true);
					// console.log(2)
				// }
			});
		} else {
			if (_t.checkLeafOnly !== false) {
				node.bubble(function(parentNode) { // include curr node
					if (node.getId() != parentNode.getId()) {
						var checkedChildNodes = Ext.Array.filter(parentNode.childNodes, function(childNode) {
							return childNode.get('checked') == true;
						});
						if (checkedChildNodes.length > 0) {
							return false;
						}

						parentNode.set('checked', false);
						// parentNode.fireEvent('checkchange', parentNode, false);
						// console.log(3)
					}
				});
			}
			node.cascadeBy(function(childNode) {
				childNode.set('checked', false);
				// childNode.fireEvent('checkchange', childNode, false);
				// console.log(4)
			}); 
		}
	}

	/* --------------------------------- */
	,filterTreePanel: function(treePanel) {
		return Ext.apply(treePanel, {
			filterBy: function(text, by) {
				var _t = this,
					view = _t.getView(),
					nodesAndParents = [];

				_t.clearFilter();
				_t.getRootNode().cascadeBy(function(tree, view) {
					var currNode = this;
					if (currNode && currNode.data[by] && currNode.data[by].toString().toLowerCase().indexOf(text.toLowerCase()) > -1) {
						// if (currNode.parentNode && currNode.parentNode.isRoot()) {
						// 	nodesAndParents.push(currNode.id);
						// }
						nodesAndParents.push(currNode.id);
						var parentNode = currNode.parentNode;
						while (parentNode && !parentNode.isRoot()) {
							if (!Ext.Array.contains(nodesAndParents, parentNode.id)) {
								nodesAndParents.push(parentNode.id);
							}
							parentNode = parentNode.parentNode;
						}
					}
				}, null, [_t, view]);
				_t.getRootNode().cascadeBy(function(tree, view) {
					var currNode = this,
						uiNode = view.getNodeByRecord(currNode);
					// if(uiNode && !Ext.Array.contains(nodesAndParents, currNode.id)) {
					// 	if (currNode.parentNode && !Ext.Array.contains(nodesAndParents, currNode.parentNode.id)) {
					// 		Ext.get(uiNode).setDisplayed('none');
					// 	}
					// }
					if(uiNode && !Ext.Array.contains(nodesAndParents, currNode.id)) {
						Ext.get(uiNode).setDisplayed('none');
					}
				}, null, [_t, view]);
			},
			clearFilter: function() {
				var _t = this,
					view = _t.getView();
				_t.getRootNode().cascadeBy(function(tree, view){
					var uiNode = view.getNodeByRecord(this);
					if(uiNode) {
						Ext.get(uiNode).setDisplayed('table-row');
					}
				}, null, [_t, view]);

				if (_t.hideRoot === true && _t.rootVisible !== false) {
					_t.hideRootNode();
				}
			},
			hideRootNode: function() {
				var _t = this;
					rootNode = _t.getView().getNodeByRecord(_t.getRootNode());
				Ext.get(rootNode).setDisplayed('none');
			}
		});
	}

	/* --------------------------------- */
	,getTreeCheckedValues: function(treePanel, field) {
		var rootNode = treePanel.getRootNode(),
			values = [];

		rootNode.cascadeBy(function(tree, view){
			if (this.isRoot() && (treePanel.hideRoot == true || treePanel.rootVisible == false)) {
				return true;
			}

			if (this.data.checked == true) {
				values.push(field ? this.raw[field] : this.raw);
			}
		});

		return values;
	}
});