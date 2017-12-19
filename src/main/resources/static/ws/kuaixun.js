

function shareKuaixun(newsid, issueMonth) {
    if (!$.isNumeric(newsid) || !$.isNumeric(issueMonth)) {
        return false;
    }
    var imgURL = "/static/home/image/newsflash/" + issueMonth + "/share" + newsid + ".png?v=4";
    $('#qcode_tip img').attr("src", imgURL);
    $('#overlay').click(hideTip);
    showTip();
}

function showTip() {
    $('#qcode_tip').css("display", "block");
    $('#overlay').css("display", "block");
    $('.top_tips').css('display', 'block');
}

function hideTip() {
    $('#overlay').css("display", "none");
    $('#qcode_tip').css("display", "none");
    $('.top_tips').css('display', 'none');
}

var sTop = 0;
var h2Top = 0;
var page = 1;	//当前页数
var maxPage = 1000;	//默认最大加载页数
var curTagid = 0; //默认是全部快讯
var loadNewPage = 1;

$(document).ready(function () {
    $(window).scroll(scrollEvent);
});

function reloadPage() {
    $(window).scrollTop(0);
    window.location.reload();
}

function scrollEvent() {
    sTop = $(this).scrollTop();
    showDateTip();
    var h1 = $(document).scrollTop();
    var h2 = $(document).height() - $(window).height();
    if (h1 >= h2 - 350) {
        //$('#debug').html("滚动条已经到达底部 h1=" + h1 + "  h2=" + h2);
        if (loadNewPage == 1) {	//当loadNewPage为1时，才加载，避免一次发送多条请求
            page = page + 1;
            if (page <= maxPage) {
                getNextPage(curTagid, page);
                loadNewPage = 0;
            }
        }
    }
}

var maxNewsid = 5817;
// maxNewsid = 6;
setInterval(getNewNum, 60000);

function getNewNum() {
    var url = "/api/app/getNewNum/?last_newsid=" + maxNewsid;
    var newNum = 0;
    $.getJSON(url, function (data) {
        if (data) {
            data = $.parseJSON(data);
            newNum = data.newNum;
            if (newNum > 0) {
                if ($('#dateTip').css("display") == "none") {
                    reloadPage();
                } else {
                    $('#dateTip div').html('您有<font color="red">' + newNum + '</font>条新消息！');
                }
            }
        }
    });
}

function showDateTip() {
    $(".dateitem h2").each(function (i) {
        h2Top = $(this).offset().top;
        if (h2Top - sTop < 0) {
            $('#dateTip').css("display", "block");
            $('#dateTip h2').html($(this).html());
        }
        if (sTop < 40) {
            $('#dateTip').css("display", "none");
        }
    });
}

//加载下一页
function getNextPage(tagid, page) {
    var url = "http://m.bishijie.com/home/newsflash/getnextpage";
    $.get(url, {tagid: tagid, page: page}, function (data) {
        if (data) {
            $('#kuaixun_list').append(data);
            var tmpText = '';
            $('#kuaixun_list h2').each(function () {
                // alert("h2 top = " + $(this).offset().top);
                if (tmpText == "") {
                    tmpText = $.trim($(this).text());
                } else {
                    if ($.trim($(this).text()) == tmpText) {
                        $(this).css('display', 'none');
                    } else {
                        tmpText = $.trim($(this).text());
                    }
                }
            });
            dealBtnStatus();
        }
        loadNewPage = 1;
    });
}

var textLineNum = 4;

function dealBtnStatus() {
    $("section p").each(function (i) {
        var reg = /px/g;
        var lineHeight = parseInt($(this).css("line-height").replace(reg, ''));
        var pHeight = parseInt($(this).height());
        // alert(pHeight + " lineHeight=" +lineHeight + " " + $(this).html());
        // alert($(this).html());
        if (pHeight <= textLineNum * lineHeight && $(this).css("-webkit-line-clamp") != textLineNum) {
            // alert(pHeight);
            $(this).parent().children('div').css("display", "none");
            $(this).css("height", "auto");
        } else {
            $(this).css("-webkit-line-clamp", textLineNum.toString());
            $(this).css("height", 1.5 * textLineNum + "em");
            $(this).parent().children('div').css("display", "none");
        }
    });
}

$(document).ready(function () {
    dealBtnStatus();
});

var TextBox = '';
var openBtn = '显示全部';
var closeBtn = '收起全部';
<!--控制字数超出隐藏-->
function showMoreText(wt) {
    var reg = /px/g;
    var lineHeight = parseInt($(wt).children("p").css("line-height").replace(reg, ''));
    // alert($(wt).children("p").height());
    if ($(wt).children("p").height() < textLineNum * lineHeight) {	//如果少于3行的高度，不需要展开和收缩
        return false;
    }
    if ($(wt).children("div").children("span").text() == openBtn) {
        $(wt).children("div").html('<span class="more more2"><em></em>' + closeBtn + '</span>');
        TextBox = $(wt).children("p");
        TextBox.css("overflow", "auto");
        TextBox.css("display", "block");
        TextBox.css("height", "auto");
        TextBox.css("-webkit-box-orient", "inherit");
        TextBox.css("-webkit-line-clamp", "inherit");
    } else if ($(wt).children("div").children("span").text() == closeBtn) {
        $(wt).children("div").html('<span class="more more1"><em></em>' + openBtn + '</span>');
        TextBox = $(wt).children("p");
        TextBox.css("overflow", "hidden");
        TextBox.css("display", "-webkit-box");
        TextBox.css("height", 1.5 * textLineNum + "em");
        TextBox.css("-webkit-box-orient", "vertical");
        TextBox.css("-webkit-line-clamp", textLineNum.toString());
    }
}





<!--
微信公众平台jssdk处理
-->
var share_obj = {
    "appid": "wxba08178837090833",
    "timestamp": 1513324949,
    "noncestr": "测试",
    "sha1": "8d9b772311842eb4b6f728573ced535d20fc9960",
    "title": "币世界快讯：快人一步，尽晓币圈事",
    "url": "http:\/\/m.bishijie.com\/kuaixun",
    "imgUrl": "http:\/\/m.bishijie.com\/static\/home\/image\/logo.png",
    "desc": "【OKEx上线TNB交易】今天OKEx发布公告，将于12月15日16:00上线Time New Bank(TNB)交易。TNB是时间商品价值传递结算代币。目前全球均价0.4108，涨幅40.48%。"
};
