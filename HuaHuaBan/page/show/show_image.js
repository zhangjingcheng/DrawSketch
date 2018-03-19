const app = getApp();

Page({

  /**
     * 页面名称
     */
  name: "index",
  /**
   * 页面的初始数据
   */
  data:{
    pic_urls:'',
    imageUrl:'',
    un_count:'',
    disabled: true,
    second:10 //下一张按钮在10s后才能生效

  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
   
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
    this.initImages();
    this.countdown(this);
    //等待10s
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
  

  initImages(){
    this.data.pic_urls = wx.getStorageSync('pic_url') 
    this.data.un_count = wx.getStorageSync('un_count')
    if (this.data.un_count == 0) {
      //需要一张已完成的图片
    } else {
      var currentImg = this.data.pic_urls[this.data.un_count - 1];
      this.setData({ imageUrl: currentImg })
      wx.setStorageSync('filename', currentImg.split('/').pop())
    }
    console.log(wx.getStorageSync('drawerid'))
  },

  goBack: function () {
    wx.redirectTo({
      url: '../index/index',
    })
  },

  countdown:function(that)  {
    var second = that.data.second
    if (wx.getStorageSync('method') ==  0 || second == 0) {
      console.log(second);
      that.setData({
        disabled: false,
      });
      return;
    }
    var time = setTimeout(function () {
      that.setData({
        second: second - 1
      });
      that.countdown(that);
    }
      , 1000)
  },

})