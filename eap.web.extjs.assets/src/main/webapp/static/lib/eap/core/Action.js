/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.core.Action', {
	requires: [
		'Ext.direct.Event',
		'Ext.direct.ExceptionEvent',
		'Ext.direct.JsonProvider',
		'Ext.direct.Manager',
		'Ext.direct.PollingProvider',
		'Ext.direct.Provider',
		'Ext.direct.RemotingEvent',
		'Ext.direct.RemotingMethod',
		'Ext.direct.RemotingProvider',
		'Ext.direct.Transaction'
	],

	autoLoadRemotingApi: true,
	remotingApiUrl: null,
	apiParams: null,
	apiNamespace: null,
	actionNamespace: null,
	actionGroup: null,

	remotingApi: null,
	providerManaged: null,

	constructor: function() {
		var _t = this;
		_t.callParent(arguments);
		_t.init();
	},

	init: function() {
		var _t = this;
		if (_t.autoLoadRemotingApi) {
			_t._loadRemotingApi();
		}
	},
	destroy: function() {
		var _t = this;
		if (_t.providerManaged) {
			Ext.direct.Manager.removeProvider(_t._getProvider());
		};
		
		_t.remotingApiUrl = null;
		_t.apiParams = null;
		_t.apiNamespace = null;
		_t.actionNamespace = null;
		_t.actionGroup = null;
		_t.remotingApi = null;
		_t.providerManaged = null;
	},

	_loadRemotingApi: function() {
		var _t = this,
			provider = _t._getProvider();
		
		if (provider) {
			_t._mixinRemotingApi(provider);
		} else {
			Ext.Loader.loadScriptFile(
				_t._getRemotingApiUrl(), // url
				function() {
					provider = _t._getProvider();
					_t._mixinRemotingApi(provider);
				}, // onLoad
				function(msg, synchronous) { // onError
					AU.showErrorDialog({
						title: '系统异常',
						msg: msg
					});
				},
				_t, // scope
				true // synchronous
			);
		}
	},

	_mixinRemotingApi: function(provider) {
		var _t = this;
		_t.providerManaged = Ext.direct.Manager.addProvider(provider);
		
		var api = _t.remotingApi = provider,
			ans = _t.actionNamespace = (api['namespace'] || undefined),
			actions = api['actions'] || {};
		for (var an in actions) { // TODO actions.length == 1
			var action = (ans ? eval(ans)[an] : eval(an)),
				methods = {};
			Ext.each(actions[an] || [], function(m) {
				var mn = m.name;
				methods[mn] = action[mn];
			}, _t);
			
			Ext.apply(_t, methods);
		}
	},
	_getRemotingApiUrl: function() {
		var _t = this;
		if (_t.remotingApiUrl) {
			return _t.remotingApiUrl;
		} else {
			var apiParams = Ext.applyIf({
				apiNs: _t._getApiNamespace(),
				actionNs: _t._getActionNamespace(),
				group: _t._getActionGroup()
			}, _t.apiParams);
			
			return (_t._getBaseRemotingApiUrl() + '?' + Ext.urlEncode(apiParams));
		}
	},
	_getActionNamespace: function() {
		var _t = this;
		return (_t.actionNamespace || 'app.actionNs.' + _t.$className);
	},
	_getActionGroup: function() {
		var _t = this;
		if (_t.actionGroup) {
			return _t.actionGroup;
		}
		
		var className = _t.$className,
			actionGroup = (className.substring(className.lastIndexOf('.') + 1, className.indexOf('Action')) + 'CLR'),
			_actionGroup = actionGroup.charAt(0).toLowerCase() + actionGroup.substring(1);
		
		return _actionGroup;
	},
	_getBaseRemotingApiUrl: function() {
		var c = AC.config;
		return c.serverUrl + c.serverApiPath;
	},
	_getProvider: function() {
		try {
			return eval(this._getProviderName());
		} catch (e) {
			return null;
		}
	},
	_getProviderName: function() {
		return this._getApiNamespace() + '.REMOTING_API';
	},
	_getProviderBaseUrl: function() {
		return eval(this._getApiNamespace() + '.PROVIDER_BASE_URL');
	},
	_getApiNamespace: function() {
		var _t = this;
		return (_t.apiNamespace || 'app.apiNs.' + _t.$className);
	}
});