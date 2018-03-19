

// 获取全局应用程序实例对象
const app = getApp();
var ctx = null;
var time_start = 0;
var datax = new Array();//total data
var datay = new Array();
var linex = new Array();//line data
var liney = new Array();
var time_line = new Array();//line time data
var start_end_time = new Array();// the total start and end time for every stroke
// 创建页面实例对象
Page({
  /**
   * 页面名称
   */
  name: "index",
  /**
   * 页面的初始数据
   */

  data: {

    showBgSet: false,
    showPenSet: false,
    defaultBgColor: '#e3e3e3',
    defaultPenColor: '#000000',

    //画布背景颜色数据
    canvasBgData: {
      canvasBgColor: "#e3e3e3",
    },
    //画笔数据

    penData: {
      penSize: 1,
      color: '#000000',
    },
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    this.initCanvas();
    datax = [];
    datay = [];
    start_end_time = [];
  },

  /**
   * 加载画布
   */
  initCanvas() {
    ctx = wx.createCanvasContext('myCanvas')
    ctx.setLineCap('round')
    ctx.setLineJoin('round')
  },

  resetColor() {
    var bg = this.data.canvasBgData;
    bg.canvasBgColor = this.data.defaultBgColor;
    var pen = this.data.penData;
    pen.color = this.data.defaultPenColor;
    pen.penSize = 1;

    this.setData({
      showBgSet: false,
      showPenSet: false,
      canvasBgData: bg,
      penData: pen
    })

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  //画图事件

  pointData: {
    begin_x: 0,
    begin_y: 0,
    end_x: null,
    end_y: null,
  },



  start: function (e) {
    var that = this;
    that.setData({
      showBgSet: false,
      showPenSet: false,
    });
    that.pointData.begin_x = e.touches[0].x;
    that.pointData.begin_y = e.touches[0].y;

    ctx.setStrokeStyle(that.data.penData.color);
    ctx.setLineWidth(that.data.penData.penSize);
    ctx.setLineCap('round'); // 让线条圆润
    ctx.beginPath();

    ctx.moveTo(that.pointData.begin_x, that.pointData.begin_y);
    ctx.lineTo(that.pointData.begin_x, that.pointData.begin_y);
    linex.push(that.pointData.begin_x.toFixed(2));
    liney.push(that.pointData.begin_y.toFixed(2));
    if (time_start == 0) {
      time_line.push(0);
    }
    else {
      time_line.push(Date.now() - time_start);
    }
    time_start = Date.now();
    ctx.stroke();
    ctx.draw(true);
  },

  move: function (e) {
    var that = this;
    ctx.moveTo(that.pointData.begin_x, that.pointData.begin_y);  //把路径移动到画布中的指定点，但不创建线条
    ctx.lineTo(e.touches[0].x, e.touches[0].y);  //添加一个新点，然后在画布中创建从该点到最后指定点的线条
    linex.push(e.touches[0].x.toFixed(2));
    liney.push(e.touches[0].y.toFixed(2));
    time_line.push(Date.now() - time_start);
    time_start = Date.now();
    ctx.stroke();  //对当前路径进行描边 
    ctx.draw(true);
    that.pointData.begin_x = e.touches[0].x;
    that.pointData.begin_y = e.touches[0].y;
  },

  end: function (e) {
    datax.push(linex);
    datay.push(liney);
    start_end_time.push(time_line)
    linex = [];
    liney = [];
    time_line = [];
  },

  eraseLaststroke: function () {
    ctx.draw();
    if (datax.length > 0) {
      for (var j = 0; j < datax.length - 1; j++) {
        var last_line_x = datax[j];
        var last_line_y = datay[j];
        for (var i = 0; i < last_line_x.length - 1; i++) {
          ctx.moveTo(last_line_x[i], last_line_y[i]);
          ctx.lineTo(last_line_x[i + 1], last_line_y[i + 1]);
          ctx.stroke();
        } //对当前路径进行描边    
      }
      
      ctx.draw(true);
      datax.pop();
      datay.pop();
      start_end_time.pop();//delete the start time
    }
  },

  clearAll: function () {
    var that = this;
    wx.showModal({
      title: '提示',
      content: '确认清除画板所有内容',
      success: function (res) {
        if (res.confirm) {
          console.log('用户点击确定');
          ctx.draw();
          that.resetColor();
          datax = [];
          datay = [];
          start_end_time = [];
        }
      }
    })
  },

  checkImage: function () {
    wx.setStorageSync('method', 0)
    wx.redirectTo({
      url: '../show/show_image',
    })
  },
  upload: function () {
    var that = this;
    wx.showModal({
      title: '提示',
      content: '确定已完成绘制并上传？',
      success: function (res) {
        if (res.confirm) {
          that.downLoadImage();
        }
      }
    });
  },
  downLoadImage: function () {
    console.log(datax);
    console.log(datay);
    console.log(start_end_time);
    //上传点坐标信息
    wx.request({
      url: 'https://78413126.draw3dsketch.com/finish',
      method: 'post',
      data: {
        //这里是发送给服务器的参数（参数名：参数值）

        "drawerid": wx.getStorageSync('drawerid'),
        "data_x": datax,
        "data_y": datay,
        "diff_time": start_end_time,
        "filename": wx.getStorageSync('filename'),
        // "resx": wx.getStorageSync('resx'),
        // "resy": wx.getStorageSync('resy'),
        "displayx": wx.getStorageSync('displayx'),
        "displayy": wx.getStorageSync('displayy'),
        "device": wx.getStorageSync('device'),
        "dpr": wx.getStorageSync('pixelRatio'),
      },
      header: {
        'content-type': 'application/json'  //这里注意POST请求content-type是小写，大写会报错  
      },
      success: function (res) {
        console.log(res.data)
        if (res.data.result == 1) {
          var rest_url = wx.getStorageSync('pic_url')
          wx.setStorageSync('pic_url', rest_url)
          wx.setStorageSync('un_count', wx.getStorageSync('un_count') - 1)
          wx.setStorageSync('method', 1)
          //delete datax,datay
          datax = []
          datay = []
          start_end_time = []
          wx.redirectTo({
            url: '../show/show_image',
          })
        }
        if (res.data.result != 1) {

          wx.showModal({
            title: '提示',
            content: '服务器状态错误，请联系管理员, ERROR CODE: ' + res.data.result,
            success: function (res) {
              if (res.confirm) {
                console.log('用户点击确定');
              }
            }
          });

        }
      }
    });
  },


})

