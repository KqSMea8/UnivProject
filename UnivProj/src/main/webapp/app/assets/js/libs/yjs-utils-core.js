/**
 * yjs-utils-core.js
 * 
 * Created by ysj on 2014
 */
var y = {};
y.core = {};
//定义事件
y.core.checkEmpty;
y.core.checkNotEmpty;
y.core.serializeForm;
y.core.readGetRequest;
y.core.hashCode;
y.core.asynBlock;

/**
 * 空
 * @param obj
 * @returns {boolean}
 */
y.core.checkEmpty = function (obj) {
    if (obj == null
        || obj == undefined
        || obj == "") {
        return true;
    }
    return false;
};

/**
 * 非空
 * @param obj
 * @returns {boolean}
 */
y.core.checkNotEmpty = function (obj) {
    if (y.core.checkEmpty(obj)) {
        return false;
    }
    return true;
};

/**
 * 将form中的值转换为键值对
 * @param {Object} frm
 */
y.core.serializeForm = function (frm) {
    var o = {};
    var a = $(frm).serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

/**
 * 获取Request_Get值
 * @param {Object} url
 */
y.core.readGetRequest = function (url) {
    var theRequest = {};
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
};

/**
 * 生成一个随机数
 * @param {Object} str
 */
y.core.hashCode = function (str) {
    var hash = 0;
    if (!str || str.length == 0) return hash.toString();
    for (i = 0; i < str.length; i++) {
        char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32bit integer
    }
    return hash.toString();
};

/**
 * 通过递归实现异步阻塞
 * @param {Object} list
 * @param {Object} callbackExec
 * @param {Object} callbackEnd
 */
y.core.asynBlock = function (list, callbackExec, callbackEnd) {
    var each = function (_list, callback) {
        if (_list.length < 1) {
            return callbackEnd && callbackEnd();
        }
        callback(_list.shift(), function () {
            each(list, callback);
        })
    };
    each(list, callbackExec)
};

/**
 * 获取指定Cookie
 * @param {Object} name 
 */
y.core.getCookie = function (name) {
    var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
};

/**
 * 设置Cookie
 * @param {Object} name
 * @param {Object} value 
 */
y.core.setCookie = function (name, value) {
    var day = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + day * 24 * 60 * 60 * 1000);
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
};

/**
 * 删除Cookie
 * @param {Object} name
 */
y.core.removeCookie = function (name) {
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval = getCookie(name);
    if (cval != null) {
        document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
    }
};

y.core.formatDate = function(data,needHour){
	var matDate = new Date(data);
	var year = matDate.getFullYear();
	var lastMonth = matDate.getMonth() + 1;
	var month = matDate.getMonth().toString().length > 1 ? lastMonth : "0" + lastMonth;
	var day = matDate.getDate().toString().length > 1 ? matDate.getDate() : "0" + matDate.getDate();
	var dateStr = "";
	if(needHour) {
		var hours = matDate.getHours().toString().length > 1 ? matDate.getHours() : "0" + matDate.getHours();
		var minutes = matDate.getMinutes().toString().length > 1 ? matDate.getMinutes() : "0" + matDate.getMinutes();
		var seconds = matDate.getSeconds().toString().length > 1 ? matDate.getSeconds() : "0" + matDate.getSeconds();
		dateStr = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
	} else {
		dateStr = year + "-" + month + "-" + day;
	}
	return dateStr;
};
 y.core.fmoney = function(s, n) {
    n = n > 0 && n <= 20 ? n : 2;
    s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";
    var l = s.split(".")[0].split("").reverse(),
        r = s.split(".")[1];
    t = "";
    for (i = 0; i < l.length; i++) {
        t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");
    }
    return t.split("").reverse().join("") + "." + r;
};