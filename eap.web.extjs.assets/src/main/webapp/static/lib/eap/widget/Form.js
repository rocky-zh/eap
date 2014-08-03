/*
* Author: chiknin@gmail.com
*/
Ext.define('eap.widget.Form', {
	extend: 'Ext.form.Panel',
	alias: 'widget.eapform',

	border: false,
	bodyPadding: 10,
	paramOrder: ['id'],
	fieldDefaults: {
		labelWidth: 60,
		labelAlign: 'right'
	}
});