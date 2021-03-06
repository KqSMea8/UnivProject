var bridgeData;
var extraData;
var callback;

function onPay(result, cb) {
    bridgeData = result.data;
    extraData = result.extra;
    callback = cb;
    //TODO test
//        notifyPaySuccess();
    var type = yTerminal.agent();
    switch (type) {
        case _TYPE_WECHAT:
            if (typeof WeixinJSBridge == "undefined") {
                if (document.addEventListener) {
                    document.addEventListener('WeixinJSBridgeReady', onWechatBridgeReady, false);
                } else if (document.attachEvent) {
                    document.attachEvent('WeixinJSBridgeReady', onWechatBridgeReady);
                    document.attachEvent('onWeixinJSBridgeReady', onWechatBridgeReady);
                }
            } else {
                onWechatBridgeReady();
            }
            break;
        case _TYPE_ALIPAY:
            // 如果jsbridge已经注入则直接调用
            if (window.AlipayJSBridge) {
                onAlipayBridgeReady && onAlipayBridgeReady();
            } else {
                // 如果没有注入则监听注入的事件
                document.addEventListener('AlipayJSBridgeReady', onAlipayBridgeReady, false);
            }
            break;
        default:
            alert("客户端不支持付款，请在微信及支付宝客户端进行操作");
            break;
    }
}

function onWechatBridgeReady() {
    WeixinJSBridge.invoke(
        'getBrandWCPayRequest', {
            "appId": bridgeData.jsapiAppid, //公众号名称，由商户传入
            "timeStamp": bridgeData.jsapiTimestamp, //时间戳，自1970 年以来的秒数
            "nonceStr": bridgeData.jsapiNoncestr, //随机串
            "package": bridgeData.jsapiPackage,
            "signType": bridgeData.jsapiSigntype, //微信签名方式：
            "paySign": bridgeData.jsapiPaysign //微信签名
        },
        function (res) {
            if (res.err_msg == "get_brand_wcpay_request:ok") {
                //通知支付成功
                notifyPaySuccess(extraData);
            }
            else {
                notifyPayFail(res.err_msg);
            }
        }
    );
}

function onAlipayBridgeReady() {
    if (null != bridgeData.tradeNo && "SUCCESS" == bridgeData.resultCode) {
        AlipayJSBridge.call("tradePay", {
            tradeNO: bridgeData.tradeNo
        }, function (res) {
            if ("9000" == res.resultCode) {
                //通知支付成功
                notifyPaySuccess(extraData);
            }
            else if ("8000" == res.resultCode) {
                notifyPayError("后台获取支付结果超时，暂时未拿到支付结果");
            }
            else if ("6004" == res.resultCode) {
                notifyPayError("支付过程中网络出错，暂时未拿到支付结果");
            }
            else {
                notifyPayFail();
            }
        });
    }
    else {
        notifyPayError(bridgeData.errCode);
    }
}

function notifyPayFail(msg) {
    //跳转到支付成功页面
    callback && callback.fail(msg);
    // window.location.href = _PAY_FAIL;
}

function notifyPayError(msg) {
    //跳转到支付成功页面
    callback && callback.error(msg);
    // window.location.href = _PAY_ERROR + "?msg=";
}

function notifyPaySuccess(data) {
    //跳转到支付成功页面
    callback && callback.success(data);
    // window.location.href = _PAY_SUCCESS + "?order=" + extraData.id;
}