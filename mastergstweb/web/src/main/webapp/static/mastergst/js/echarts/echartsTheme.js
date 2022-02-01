/**
 * 主题名称
 *
 * @type {string}
 */
var themeName = 'infographic';

/**
 * 主题对象
 *
 * @type {Object}
 */
var theme = null;

function selectChange(value){
    themeName = value;
    showLoading();
    $('[name=theme-select]').val(themeName);
    if (themeName != 'default') {
        window.location.hash = value;
        require(['echarts/theme/' + themeName], function(curTheme){
            theme = curTheme;
            setTimeout(refreshAll, 500);
        })
    }
    else {
        window.location.hash = '';
        setTimeout(refreshAll, 500);
    }
}

function showLoading() {
    myChart.showLoading();
}

function refreshAll() {
    myChart.hideLoading();
    myChart.setTheme(theme);
}

function download() {
    if (themeName) {
        window.open('static/mastergst/js/echarts/theme/' + themeName + '.js');
    }
}

var hash = window.location.hash.replace('#','') || 'infographic';
if ($('[name=theme-select]').val(hash).val() != hash) {
    $('[name=theme-select]').val('infographic');
    hash = 'infographic';
}

var developMode = false;
if (developMode) {
    window.esl = null;
    window.define = null;
    window.require = null;
    (function () {
        var script = document.createElement('script');
        script.async = true;

        var pathname = location.pathname;

        var pathSegs = pathname.slice(pathname.indexOf('doc')).split('/');
        var pathLevelArr = new Array(pathSegs.length - 1);
        script.src = 'static/mastergst/js/echarts/esl/esl.js';
        if (script.readyState) {
            script.onreadystatechange = fireLoad;
        }
        else {
            script.onload = fireLoad;
        }
        (document.getElementsByTagName('head')[0] || document.body).appendChild(script);
        
        function fireLoad() {
            script.onload = script.onreadystatechange = null;
            setTimeout(loadedListener,100);
        }
        function loadedListener() {
            // for develop
            require.config({
                packages: [
                    {
                        name: 'echarts',
                        location: 'static/mastergst/js/echarts',
                        main: 'echarts'
                    },
                    {
                        name: 'zrender',
                        location: 'http://ecomfe.github.io/zrender/src',
                        //location: '../../../zrender/src',
                        main: 'zrender'
                    }
                ]
            });
            launchExample();
        }
    })();
}
else {
    // for echarts online home page
    require.config({
        paths: {
            echarts: '/static/mastergst/js/echarts'
        }
    });
    launchExample();
}

var isExampleLaunched;
function launchExample() {
    if (isExampleLaunched) {
        return;
    }

    // 按需加载
    isExampleLaunched = 1;
    // 按需加载
    require(
        [
            'echarts',
            'echarts/theme/' + hash,
            'echarts/chart/line',
            'echarts/chart/bar',
            'echarts/chart/scatter',
            'echarts/chart/k',
            'echarts/chart/pie',
            'echarts/chart/radar',
            'echarts/chart/force',
            'echarts/chart/chord',
            'echarts/chart/map',
            'echarts/chart/gauge',
            'echarts/chart/funnel'
        ],
        requireChartCallback
    );
}
function requireChartCallback (ec, defaultTheme) {
	echarts = ec;
    myChart = echarts.init(document.getElementById('chartId'), defaultTheme);
    //myChart[i].setOption(option[i]);
}
