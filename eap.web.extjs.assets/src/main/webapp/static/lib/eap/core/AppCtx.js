/*
* Author: chiknin@gmail.com
*/

Ext.ns('eap.core');
AC = eap.core.AppCtx = {
	isStarted: false,
	startTime: null,

	config: null,

	modules: null,
	
	startup: function(cfg, cb) {
		if (arguments.length == 1 && (typeof arguments[0] === 'function')) {
			cb = cfg;
			cfg = {};
		}
		
		var _t = this;
		if (_t.isStarted) {
			return;
		}
		_t.isStarted = false;
		_t.startTime = new Date();

		cfg = _t.config = Ext.applyIf(cfg || {}, {
			version: '1.0',
			serverUrl: cfg.serverUrl || '', // TODO
			serverApiPath: '/api.js', // '/apis',
			enableQuickTips: true,
			// waitMsg: '请求处理中, 请稍候...'
			locale: {
				enabled: true,
				lang: 'zh_CN',
				folder: '/locale/'
			},
			ajax: {
				timeout: 5000,
			},
			loader: {
				enabled: true,
				disableCaching: false,
				disableCachingParam: '_dc',
				paths: {
					'Ext'	: 'static/lib/ext/src',
					'Ext.ux': 'static/lib/ext/src/ux',
					'eap'	: 'static/lib/eap',
					'app'	: 'app'
				},
				preloadRequires: [
					'eap.core.AppUtils'
					,'eap.core.Dic'
					,'eap.widget.Form'
					,'eap.widget.Grid'
					,'eap.CrudUI'
					,'eap.CrudModule'
				],
				preloadRequireModules: []
			},
			direct: {
				enableBuffer: 1
			},
			dic: {
				storeIdPrefix: 'eap.dic.',
				moduleId: '',
				remoteMethod: '',
				defaultComboHeaderOption: {value: '', name: '全部'}
			},
			print: {
				cssPaths: []
			}
		});

		Ext.Loader.setConfig(cfg.loader);
		Ext.require(cfg.loader.preloadRequires);
		Ext.onReady(function() {
			_t._init(_t.config);
			cb && cb(_t);
		});
	},

	shutdown: function(cb) {
		var _t = this;
		if (_t.isStarted !== true) {
			return;
		}

		_t._destroy();

		_t.isStarted = false;
		_t.startTime = null;

		cb && cb(_t);
	},

	/* ------------------------------------- */
	_init: function(cfg) {
		var _t = this;
		_t.modules = {};

		if (Ext.Ajax) {
			Ext.Ajax.timeout = cfg.ajax.timeout;
			Ext.Ajax.defaultHeaders = {
				appVersion: cfg.version
			};
		}

		if (cfg.locale.enabled) {
			Ext.Loader.loadScriptFile('static/lib/eap/locale/ext-lang-' + cfg.locale.lang + '.js', Ext.emptyFn, Ext.emptyFn, this, true);
			_t.updateLocale();
		}
		if (cfg.enableQuickTips) {
			Ext.tip.QuickTipManager.init();
		}
		if (cfg.direct.enableBuffer) {
			Ext.direct.RemotingProvider.enableBuffer = cfg.direct.enableBuffer;
		}

		Ext.each(cfg.loader.preloadRequireModules, function(moduleId) {
			// TODO 加载模块
		});
	},
	_destroy: function() {
		var _t = this;
		
		_t.destroyAllModule();
		_t._t.modules = null;
		
		_t.config = null;
	},

	/* ------------------------------------- */
	getModule: function(id) {
		return this.modules[id];
	},
	registerModule: function(module) {
		var _t = this;
		
		if (!module.id) {
			module.id = module.$className;
		}
		
		_t.modules[module.id] = module;
	},
	loadModule : function(id, config) {
		var module;
		try {
			module = Ext.create(id, config);
		} catch (e) {
			Ext.Error.raise({
				sourceClass: 'eap.core.AppCtx',
				sourceMethod: 'requestModule',
				msg: 'load module["'+id+'"] error: ' + e.message
			});
		}
		this.registerModule(module);
		module.init && module.init();
		return module;
    },
	requestModule: function(id, config, cb, scope) {
		var _t = this,
			module = _t.getModule(id);
		
		if (!module) {
			module = _t.loadModule(id, config);
		}

		cb && cb.call(scope || module, module);
	},
	destroyModule: function(id) {
		var _t = this,
			module = _t.modules[id];
		if (module) {
			module.destroy && module.destroy();
			delete _t.modules[id];
		}
	},
	destroyAllModule: function(filterRreloadRequireModules) {
		var _t = this;
		if (_t.modules) {
			for (var moduleId in _t.modules) {
				if (filterRreloadRequireModules === true && Ext.Array.indexOf(_t.config.loader.preloadRequireModules, moduleId) != -1) {
					continue;
				}
				
				_t.destroyModule(moduleId);
			}
		}
	},

	/* [{m/method, p/params, cb/callback, s/scope}] */
	doRequest: function(id, requests) { // invoke Module method
		var _t = this;
		_t.requestModule(id, null/*模块已加载*/, function(module) {
			module.doRequest(requests);
		}, _t);
	},
	doAction: function(id, request) { // invoke Action method
		this.doRequest(id, {
			m: 'doAction',
			p: request
		});
	},
	makeRequestEvent: function(event, id, request) {
		var _t = this,
			action = {};
		action[event] = {
			fn: function() {
				request.eventArgs = arguments;
				request.eventSource = this;
				
				_t.doRequest(id, request);
			}
		};
		
		return action;
	},

	makeDirectFn: function(id, method) { // directFn // }, async) {
		var _t = this,
			module = _t.getModule(id),
			remoteDirectFn = module.action[method];
		
		var directFn = function() {
			var args = Array.prototype.slice.call(arguments, 0), arg, p = [], cb, s, i;
			for (i = 0; i < args.length; i++) {
				arg = args[i];
				if (Ext.isFunction(arg)) {
					cb = arg;
					if ((i + 1) < args.length) {
						s = args[i + 1];
					};
					break;
				} else {
					p.push(arg);
				}
			}
			
			_t.doAction(id, {
				m: method,
				p: p,
				cb: cb,
				s: s
			});
		};
		directFn.directCfg = remoteDirectFn && remoteDirectFn.directCfg;
		
		return directFn;
	}
}