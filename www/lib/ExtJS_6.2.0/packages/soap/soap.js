Ext.define("Ext.data.soap.Reader", {
    extend: "Ext.data.reader.Xml", alias: "reader.soap", getData: function (b) {
        var c = b.documentElement, a = c.prefix;
        return Ext.DomQuery.selectNode(a + "|Body", b)
    }
});
Ext.define("Ext.data.soap.Proxy", {
    extend: "Ext.data.proxy.Ajax",
    alias: "proxy.soap",
    requires: ["Ext.data.soap.Reader"],
    config: {
        soapAction: {},
        operationParam: "op",
        reader: "soap",
        url: "",
        envelopeTpl: ['<?xml version="1.0" encoding="utf-8" ?>', '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">', "{[values.bodyTpl.apply(values)]}", "</soap:Envelope>"],
        createBodyTpl: null,
        readBodyTpl: ["<soap:Body>", '<{operation} xmlns="{targetNamespace}">', '<tpl foreach="params">', "<{$}>{.}</{$}>", "</tpl>", "</{operation}>", "</soap:Body>"],
        updateBodyTpl: null,
        destroyBodyTpl: null,
        writeBodyTpl: ["<soap:Body>", '<{operation} xmlns="{targetNamespace}">', '<tpl for="records">', '{% var recordName=values.modelName.split(".").pop(); %}', "<{[recordName]}>", '<tpl for="fields">', "<{name}>{[parent.get(values.name)]}</{name}>", "</tpl>", "</{[recordName]}>", "</tpl>", "</{operation}>", "</soap:Body>"],
        targetNamespace: ""
    },
    applyEnvelopeTpl: function (a) {
        return this.createTpl(a)
    },
    applyCreateBodyTpl: function (a) {
        return this.createTpl(a)
    },
    applyReadBodyTpl: function (a) {
        return this.createTpl(a)
    },
    applyUpdateBodyTpl: function (a) {
        return this.createTpl(a)
    },
    applyDestroyBodyTpl: function (a) {
        return this.createTpl(a)
    },
    applyWriteBodyTpl: function (a) {
        return this.createTpl(a)
    },
    createTpl: function (a) {
        if (a && !a.isTpl) {
            a = new Ext.XTemplate(a)
        }
        return a
    },
    doRequest: function (a) {
        var d = this, e = a.getAction(), g = d.getApi()[e],
            f = Ext.applyIf(a.getParams() || {}, d.getExtraParams() || {}), b = d.getEnvelopeTpl().apply({
                operation: g,
                targetNamespace: d.getTargetNamespace(),
                params: f,
                records: a.getRecords(),
                bodyTpl: d.getBodyTpl(e)
            }), c = new Ext.data.Request({
                url: d.getUrl() + "?" + d.getOperationParam() + "=" + g,
                method: "POST",
                action: e,
                operation: a,
                xmlData: b,
                headers: Ext.apply({SOAPAction: d.getSoapAction()[e]}, d.getHeaders()),
                timeout: d.getTimeout(),
                scope: d,
                disableCaching: false
            });
        c.setCallback(d.createRequestCallback(c, a));
        return d.sendRequest(c)
    },
    getBodyTpl: function (b) {
        b = Ext.String.capitalize(b);
        var a = this["get" + b + "BodyTpl"]();
        return a || this.getWriteBodyTpl()
    }
});