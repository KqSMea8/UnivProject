/**
 * yjs-utils-css.js
 * 
 * Created by ysj on 2016
 */
y.css = {};
//定义事件
y.css.iconsBuild;

/**
 * 生成CSS(用于分割小图标)
 * 
 * @param {Object} name 主命名
 * @param {Object} path 文件路径(完整的或相对的)
 * @param {Object} width 图标宽
 * @param {Object} height 图标高
 * @param {Object} pading 间隔
 * @param {Object} maxWidth 原图宽
 * @param {Object} maxHeight 原图高
 */
y.css.iconsBuild =function(name,path,width,height,pading,maxWidth,maxHeight){
	var rootClass = "."+name+"-icons {background:url("+path+") no-repeat;width:"+width+"px;height:"+height+"px;display: inline-block;}";
	// Lines
	var lineNum = maxWidth/(width+pading);
	// Column
	var colNum = maxHeight/(height+pading);
	var itemClass = "\n";
	for(var i = 0 ;i < colNum; i++){
		for(var j = 0 ;j < lineNum; j++){
			itemClass += ".icon-"+name+"-"+i+"-"+j+"{";
			itemClass += "background-position:-"+((width+pading)*j)+"px"+" -"+((height+pading)*i)+"px;";
			itemClass += "}\n";
		}
	}
	return rootClass+itemClass;
};