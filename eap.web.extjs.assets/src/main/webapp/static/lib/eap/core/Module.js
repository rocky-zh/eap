/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.core.Module', {
	extend: 'Ext.util.Observable',

	id: null,

	autoLoadLocale: false,
	localeConfig: null,
	locale: null,

	autoLoadAction: true,
	actionConfig: null,
	action: null,
	actionClassSuffix: 'Action',

	autoLoadUI: true,
	uiConfig: null,
	ui: null,
	uiClassSuffix: 'UI',

	/* -------------------------------------*/
	init: function() {
		var _t = this;
		_t.addEvents(
			'beforeHandlerRequest'
		);

		_t.autoLoadLocale && _t._loadLocale(_t.localeConfig);
		_t.autoLoadAction && _t._loadAction(_t.actionConfig);
		_t.autoLoadUI && _t._loadUI(_t.uiConfig);

		_t.config && _t._initConfig(_t.config);
	},
	activate: function(params) {

	},
	destroy: function() {
		var _t = this;
		if (_t.ui) {
			Ext.destroy(_t.ui);
			_t.ui = null;
		}
		if (_t.action) {
			_t.action.destroy();
			_t.action = null;
		}
		if (_t.locale) {
			_t.locale = null;
		}
	},

	_loadLocale: function(config) {
		var _t = this,
			className = _t.$className;
		AU.Ajax({
			url: className.substring(0, className.lastIndexOf('.')).replace(/\./g, '/') + config.localFolder + config.lang + '.json', // TODO BASEPATH
			async: false, // TODO
			success: function(response) {
				var localeText = response.responseText || {};
				_t.locale = Ext.decode(localeText);
			}
		});
	},
	_loadAction: function(config) {
		var _t = this,
			actionClassName = (_t.$className + _t.actionClassSuffix),
			cfg = config;
		try {
			_t.action = Ext.create(actionClassName, cfg);
		} catch (e) {
			Ext.Error.raise({
				sourceClass: _t.$className,
				sourceMethod: '_loadAction',
				msg: 'load action[' + actionClassName + '] error: ' + e.message
			});
		}
	},
	_loadUI: function(config) {
		var _t = this,
			uiClassName = (_t.$className + _t.uiClassSuffix),
			cfg = Ext.apply(_t._getModuleUIConfig(), config); // TODO INJECT MODULE
		try {
			_t.ui = Ext.create(uiClassName, cfg);
		} catch (e) {
			Ext.Error.raise({
				sourceClass: _t.$className,
				sourceMethod: '_loadUI',
				msg: 'load ui[' + uiClassName + '] error: ' + e.message
			});
		}
	},
	_initConfig: function(config) {
		var _t = this;

		if (_t.ui) {
			var itemIdComps = _t.ui.query('[itemId]');
			Ext.each(itemIdComps, function(itemIdComp, index) {
				_t[itemIdComp.itemId] = itemIdComp;
			});
			if (config.ref) {
				for (var r in config.ref) {
					_t[r] = _t.down(config.ref[r]);
				}
			}
		}

		if (config.event) {
			for (var c in config.event) {
				var es = config.event[c];
				if (Ext.isString(es)) {
					es = [es];
				}

				if (Ext.isArray(es)) { // ['beforerender', 'itemclick']
					Ext.each(es, function(e) {
						_t[c].on(e, Ext.bind(_t['_on' + Ext.String.capitalize(c) + Ext.String.capitalize(e)], _t));
					});
				} else {
					for (var e in es) { // {'click': fn, 'keypress': fn}
						var ec = es[e], fn, scope, options;
						if (Ext.isString(ec)) {
							fn = Ext.bind(_t[ec], _t);
						} else if (Ext.isFunction(ec)) {
							fn = ec;
						} else {
							fn = Ext.isString(ec.fn) ? Ext.bind(_t[ec.fn], _t) : ec.fn;
							scope = ec.scope || _t;
							options = ec.options;
						}
						_t[c].on(e, fn, scope, options);
					}
				}
			}
		}
	},

	/*  -------------------------------------*/
	locVal: function(key, params) {
		var _t = this,
			enableLocale = _t.localeConfig.enableLocale,
			msg;
			
		if (enableLocale === true) {
			var keys = key.split('.'),
				msg = _t.locale[keys.shift()];
			Ext.each(keys, function(k) {
				msg = msg[k];
			}, me);
		} else {
			msg = key;
		}
		
		if (params) {
			msg = Ext.String.format(msg, params);
		}
		
		return msg;
	},

	/*  -------------------------------------*/
	doRequest: function(requests) { // AOP logging / auth / exception handle
		var _t = this,
			reqs = (Ext.isArray(requests) ? requests : [requests]),
			req;
		for (var i = 0; i < reqs.length; i++) {
			req = reqs[i];
			if (_t.fireEvent('beforeHandlerRequest', req) === false) {
				break;
			}

			var m = _t[req.m], 
				p = req.p, 
				cb = req.cb || Ext.emptyFn, 
				s = req.s || _t, 
				ret;
			
			ret = m.apply(_t, [p, req]);
			cb.apply(s, [ret, req]);
		}
	},
	doAction: function(actionRequest, moduleRequest) { // caller -> handleRequest()
		var _t = this,
			ar = actionRequest,
			a = _t.action,
			m = a[ar.m],
			p = [].concat(ar.p),//ar.p ? (Ext.isArray(ar.p) ? ar.p : [ar.p]) : [],
			cb = ar.cb;
			sync = ar.sync,
			s = ar.s || _t;

		if (sync === true) {
			var request = {
				url: a.remotingApi.url,
				async: false,
				jsonData: {
					tid: 0, // TODO
					type: 'rpc',
					action: _t.action._getActionGroup(),
					method: ar.m,
					data: ar.p
				}
			};

			var response = Ext.Ajax.request(request);
			if (response.status == 200) {
				cb && cb.apply(s, [Ext.decode(response.responseText)[0], response]);
			} else {
				AU.exception(response.responseText);
			}
		} else {
			if (cb) {
				p = p.concat(function(result, e) {
					var tx = e.getTransaction();
					if (!e.status) {
						AU.showException(e.message);
						return;
					};
					cb.apply(s, [result, e]);
				}).concat(s);
			};

			m.apply(a, p);
		}
	},

	/*  -------------------------------------*/
	renderView: function(viewId, viewConfig, position) {
		var _t = this,
			viewConfig = Ext.apply(_t._getModuleUIConfig(), viewConfig),
			position = position || 'place',
			renderTo = (viewConfig.renderTo || _t.ui.id); // || Ext.getBody()
		delete viewConfig.renderTo;
		
		viewId = _t._getViewId(viewId);
		
		var refCmp = renderTo.isComponent ? renderTo : Ext.getCmp(renderTo);
		if (position == 'place') {
			refCmp.removeAll(true);
		} else {
			Ext.destroy(Ext.getCmp(viewId));
		}
		
		Ext.suspendLayouts();
		var view = Ext.create(viewId, 
			Ext.apply(viewConfig, {
				id: viewId // TODO viewId conflict
			})
		);
		refCmp.add(view);
		Ext.resumeLayouts(true);
		
		return view;
	},
	showWindow: function(viewId, viewConfig, windowConfig) {
		var _t = this,
			viewConfig = Ext.apply(_t._getModuleUIConfig(), viewConfig),
			viewUI = Ext.create(_t._getViewId(viewId), viewConfig),
			win = Ext.create('Ext.window.Window', Ext.apply({
					modal: true,
					resizable: false
				}, 
				windowConfig, 
				{			
					autoScroll: true,
					layout: 'fit',
					// layout: 'anchor',
					// defaults: {
					// 	layout: 'anchor',
					// 	anchor: '100% 100%'
					// },
					items: viewUI
				}
			));
		
		return win.show();
	},

	/*  -------------------------------------*/
	up: function(selector) {
		return this.ui.up(selector);
	},
	down: function(selector) {
		return this.ui.down(selector);
	},

	/*  -------------------------------------*/
	_getModuleUIConfig: function() {
		var _t = this;
		return {
			moduleId: _t.id,
			// renderView: Ext.bind(_t.renderView, _t),
			// showWindow: Ext.bind(_t.showWindow, _t),
			module: _t
		};
	},
	_getViewId: function(viewId) {
		if (viewId.indexOf('.view.') == -1) {
			var className = this.$className;
			viewId = (className.substring(0, className.lastIndexOf('.')) + '.view.' + viewId);
		}
		
		return viewId;
	},

	/*  -------------------------------------*/
	makeDirectFn: function(method) {
		return AC.makeDirectFn(this.id, method);
	}
});